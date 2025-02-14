package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.entity.status.user.UserStatusRequest;
import com.sprint.mission.discodeit.entity.user.*;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import com.sprint.mission.discodeit.service.validate.UserServiceValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStateService userStateService;
    private final BinaryContentService binaryContentService;


    private static final EntityFactory entityFactory = BaseEntityFactory.getInstance();
    private static final UserServiceValidator validator = new UserServiceValidator();


    @PostConstruct
    public void init() {
        log.error("주입된 userRepository: {}", userRepository.getClass().getSimpleName());
    }

    @Override
    public UserCommonResponse createUser(UserCommonRequest user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        checkDuplicated(user);

        UUID userId = UUID.randomUUID();
        return createBasicUser(userId, user);
    }

    @Override
    public UserCommonResponse createUserWithProfile(UserCommonRequest createDto, BinaryContent binaryContent) {
        // 먼저 사용자를 생성합니다
        UserCommonResponse userResponse = createUser(createDto);

        if (binaryContent == null) {
            throw new IllegalUserException("프로필 이미지 등록 오류: null");
        }

        // 이메일로 사용자를 찾습니다
        User user = userRepository.findUserByEmail(createDto.userEmail());
        if (user == null) {
            throw new UserNotFoundException("방금 생성한 사용자를 찾을 수 없습니다.");
        }

        BinaryContentRequest binaryContentCreateRequest = new BinaryContentRequest(
                user.getId(),  // 찾은 사용자의 ID를 사용
                binaryContent.getMessageId(),
                binaryContent.getFileName(),
                binaryContent.getFileType()
        );

        binaryContentService.create(binaryContentCreateRequest);

        return userResponse;
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new UserNotFoundException();
        }

        UserStatus status = userStateService.find(userId);
        return new UserResponse(user.getUserName(), user.getUserEmail(), status.getState());
    }

    // 유저 이름으로 찾기
    @Override
    public UserCommonResponse find(String userName) {
        User findUser = userRepository.findUserByName(userName);

        if (findUser == null) {
            throw new UserNotFoundException();
        }

        return convertToUserResponse(findUser.getId(), findUser.getUserName(), findUser.getUserEmail());
    }

    /**
     * 사용자의 온라인 상태도 같이 포함
     * 패스워드 정보 X
     */
    @Override
    public Map<UUID, UserResponse> findAll() {
        Map<UUID, User> allUsersMap = Optional.ofNullable(userRepository.findAllUser())
                .orElseThrow(() -> new UserNotFoundException("유저가 아무도 없습니다."));

        return convertToUserResponseMap(allUsersMap);
    }


    /**
     * - [ ] 선택적으로 프로필 이미지를 대체할 수 있습니다.
     * - [ ] DTO를 활용해 파라미터를 그룹화합니다.
     *   - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
     */
    @Override
    public UserCommonResponse update(String userName, UserCommonRequest updateDto) {
        User findUser = userRepository.findUserByName(userName);
        if (findUser == null) {
            throw new UserNotFoundException();
        }

        findUser.updateName(updateDto.userName());
        findUser.updateEmail(updateDto.userEmail());
        findUser.updatePassword(updateDto.userPassword());

        userStateService.updateByUserName(userName);
        userRepository.userSave(findUser);

        return new UserCommonResponse(findUser.getId(), findUser.getUserName(), findUser.getUserEmail());
    }


    /**
     * 프사도 바꿀 수 있음
     */
    @Override
    public UserCommonResponse updateUserWithProfile(UUID userId, BinaryContentRequest binaryContent) {
        if (binaryContent == null) {
            throw new IllegalUserException("not found binary context");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // userStat 업데이트
        updateUserStat(userId);

        // 유저 프사 업데이트
        BinaryContentRequest changeBin = updateUserProfileImage(userId);
        binaryContentService.update(changeBin);

        return convertToUserResponse(user.getId(), user.getUserName(), user.getUserEmail());
    }

    private BinaryContentRequest updateUserProfileImage(UUID userId) {
        BinaryContentResponse updateBin = binaryContentService.findByUserId(userId).values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유저 프사를 찾을 수 없음"));

        BinaryContentRequest changeBin = new BinaryContentRequest(updateBin.userId(), updateBin.messageId(), updateBin.fileName(), updateBin.fileType());
        return changeBin;
    }


    /**
     * 관련된 도메인도 같이 삭제합니다.
     * - `BinaryContent`(프로필), `UserStatus`
     */
    @Override
    public UUID deleteUser(UUID userId) {
        if (userId == null) {
            throw new IllegalUserException("userId를 다시 확인해주세요.");
        }

        userRepository.removeUserById(userId);
        userStateService.delete(userId);
        binaryContentService.delete(userId);

        return userId;
    }

    private void updateUserStat(UUID userId) {
        Map<UUID, UserStatus> findUserStatMap = userStateService.findAllByUserId(userId);
        UserStatus userStatus = findUserStatMap.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾는 userStatus가 없습니다."));
        userStatus.updateUserStatus();
        userStateService.update(new UserStatusRequest(userStatus.getUserId(), userStatus.getUserName(),userStatus.getState()));
    }

    private UserCommonResponse createBasicUser(UUID userId, UserCommonRequest user) {
        if (validator.isNullParam(user.userName(), user.userPassword())) {
            throw new IllegalUserException();
        }

        saveUser(userId, user);
        saveUserStatus(userId, user);

        log.warn("유저 등록: {}", user.userName());

        return new UserCommonResponse(userId, user.userName(), user.userEmail());
    }

    private void saveUserStatus(UUID userId, UserCommonRequest user) {
        UserStatus userStatus = new UserStatus(userId, user.userName());
        userStateService.create(new UserStatusRequest(userStatus.getUserId(), userStatus.getUserName(), userStatus.getState()));
    }

    private void saveUser(UUID userId, UserCommonRequest user) {
        User createUser = new User(userId, user.userName(), user.userEmail(), user.userPassword(), UserRole.ROLE_COMMON);
        userRepository.userSave(createUser);
    }

    private void checkDuplicated(UserCommonRequest user) {
        /**
         * `username`과 `email`은 다른 유저와 같으면 안됩니다.
         */
        Map<UUID, User> allUsers = userRepository.findAllUser();
        if (allUsers.isEmpty()) {
            return;
        }

        boolean isSameUser = allUsers.values().stream().anyMatch(users -> users.getUserName().equals(user.userName()));

        boolean isSameEmail = allUsers.values().stream().anyMatch(users -> users.getUserEmail().equals(user.userEmail()));

        if (isSameUser || isSameEmail) {
            throw new IllegalUserException("username 또는 email이 다른 유저와 같습니다.");
        }
    }

    private Map<UUID, UserResponse> convertToUserResponseMap(Map<UUID, User> allUsersMap) {
        Map<UUID, UserResponse> convertMap = allUsersMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            UserStatus status = userStateService.find(entry.getKey());
                            return new UserResponse(entry.getValue().getUserName(),
                                    entry.getValue().getUserEmail(),
                                    status.getState());
                        }
                ));

        return convertMap;
    }

    private static UserCommonResponse convertToUserResponse(UUID id, String userName, String userEmail) {
        return new UserCommonResponse(id, userName, userEmail);
    }

/*    private static User convertToUser(UserCommonRequest updateDto, UserResponse findUser) {
        User user = new User(findUser.getUserName(), findUser.getUserEmail(), updateDto.userPassword(), );
        return user;
    }

    private UserResponse getUserResponse(UUID userId, UserCommonRequest updateDto) {
        if(userId == null || updateDto == null) {
            throw new IllegalUserException();
        }
        UserResponse findUser = getUserById(userId);
        return findUser;
    }*/
}
