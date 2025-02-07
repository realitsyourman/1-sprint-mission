package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.entity.status.user.UserStatusRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserCommonRequest;
import com.sprint.mission.discodeit.entity.user.UserResponse;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import com.sprint.mission.discodeit.service.validate.ServiceValidator;
import com.sprint.mission.discodeit.service.validate.UserServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStateService userStateService;
    private final BinaryContentService binaryContentService;


    private static final EntityFactory entityFactory = BaseEntityFactory.getInstance();
    private static final ServiceValidator<User> validator = new UserServiceValidator();

    @Override
    public User createUser(UserCommonRequest user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        checkDuplicated(user);

        return createBasicUser(user);
    }

    @Override
    public User createUserWithProfile(UserCommonRequest createDto, BinaryContent binaryContent) {
        User basicUser = createUser(createDto);


        if (binaryContent == null) {
            throw new IllegalUserException("프로필 이미지 등록 오류: null");
        }

        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(basicUser.getId(), binaryContent.getMessageId(), binaryContent.getFileName(), binaryContent.getFileType());
        binaryContentService.create(binaryContentCreateRequest);

        return basicUser;
    }

    @Override
    public UserResponse find(UUID userId) {
        User findUser = validator.entityValidate(userRepository.findUserById(userId));

        if (findUser == null) {
            throw new UserNotFoundException();
        }

        UserStatus status = userStateService.find(userId);
        return new UserResponse(findUser.getUserName(), findUser.getUserEmail(), status);
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
    public User update(UUID updateUserId, UserCommonRequest updateDto) {
        User findUser = userRepository.findUserById(updateUserId);
        findUser.updateName(updateDto.userName());
        findUser.updateEmail(updateDto.userEmail());
        findUser.updatePassword(updateDto.userPassword());

//        Map<UUID, UserStatus> allByUserId = userStateService.findAllByUserId(updateUserId);
//        // 스테이터스 업데이트
//        allByUserId.values()
//                        .forEach(UserStatus::updateUserStatus);

        userStateService.updateByUserId(updateUserId);
        return userRepository.userSave(findUser);
    }

    /**
     * 프사도 바꿀 수 있음
     */
    @Override
    public User updateUserWithProfile(UUID userId, BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new IllegalUserException("not found binary context");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // userStat 업데이트
        Map<UUID, UserStatus> findUserStatMap = userStateService.findAllByUserId(userId);
        UserStatus userStatus = findUserStatMap.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾는 userStat가 없습니다."));
        userStatus.updateUserStatus();
        userStateService.update(new UserStatusRequest(userStatus.getUserId(), userStatus.getState()));

        // 유저 프사 업데이트
        BinaryContent updateBin = binaryContentService.findByUserId(userId).values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유저 프사를 찾을 수 없음"));

        BinaryContent changeBin = updateBin.upload(binaryContent.getFileName(), binaryContent.getFileType());
        binaryContentService.update(changeBin);

        return user;
    }

    /**
     * 관련된 도메인도 같이 삭제합니다.
     * - `BinaryContent`(프로필), `UserStatus`
     */
    @Override
    public void deleteUser(UUID userId) {
        if (userId == null) {
            throw new IllegalUserException("userId를 다시 확인해주세요.");
        }

        userRepository.removeUserById(userId);
        userStateService.delete(userId);
        binaryContentService.delete(userId);
    }

    private User createBasicUser(UserCommonRequest user) {
        if (validator.isNullParam(user.userName(), user.userPassword())) {
            throw new IllegalUserException();
        }

        User savedUser = validator.entityValidate(entityFactory.createUser(user.userName(), user.userEmail(), user.userPassword()));

        if (savedUser == null) {
            throw new UserNotFoundException();
        }

        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStateService.create(new UserStatusRequest(userStatus.getUserId(), userStatus.getState()));
        userRepository.userSave(savedUser);

        return savedUser;
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

        if (isSameUser && isSameEmail) {
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
                                    status);
                        }
                ));

        return convertMap;
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
