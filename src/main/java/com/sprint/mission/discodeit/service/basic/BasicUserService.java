package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.binarycontent.UploadBinaryContent;
import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.entity.status.user.UserStatusRequest;
import com.sprint.mission.discodeit.entity.user.*;
import com.sprint.mission.discodeit.exception.user.IllegalUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import com.sprint.mission.discodeit.service.validate.UserServiceValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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
    public UserCreateWithBinaryContentResponse createUserWithProfile(UserCommonRequest createDto, BinaryContentRequest request) {
        UserCommonResponse userResponse = createUser(createDto);
        request.updateId(userResponse.id()); // channelId는 null인 상태, requestUserId는 있음

        UserCreateWithBinaryContentResponse userWithFileResponse = new UserCreateWithBinaryContentResponse(
                userResponse.id(),
                userResponse.userName(),
                userResponse.userEmail(),
                request.getFileName()
        );

        if (request.getFile() == null && request.getFiles().isEmpty()) {
            log.info("유저 생성 완료: {}", userWithFileResponse.getUserName());
            return userWithFileResponse;
        }

        try {
            User user = userRepository.findUserByEmail(createDto.userEmail());
            if (user == null) {
                throw new UserNotFoundException("방금 생성한 사용자를 찾을 수 없습니다.");
            }

            // 프로필 이미지 업로드 및 저장
            List<UploadBinaryContent> uploadBinaryContents = binaryContentService.create(request);
            log.info("프사 있는 유저 생성: {}", user.getUserName());

            UploadBinaryContent uploadBinaryContent = uploadBinaryContents.get(0);
            userWithFileResponse.setFileName(uploadBinaryContent.getSavedFileName());

            return userWithFileResponse;
        } catch (IOException e) {
            log.error("유저 생성 실패: {}", createDto.userName(), e);
            throw new IllegalUserException("프로필 이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }

        UserStatus status = userStateService.find(userId);
        BinaryContentResponse profileImage = null;
        try {
            profileImage = binaryContentService.find(userId);
        } catch (Exception e) {
            log.debug("No profile image found for user: {}", userId);
        }

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
    public List<UserResponse> findAll() {
        Map<UUID, User> allUsersMap = Optional.ofNullable(userRepository.findAllUser())
                .orElseThrow(() -> new UserNotFoundException("유저가 아무도 없습니다."));

        return allUsersMap.entrySet().stream()
                .map(entry -> new UserResponse(
                        entry.getValue().getUserName(),
                        entry.getValue().getUserEmail(),
                        userStateService.find(entry.getKey()).getState(),
                        binaryContentService.findBinaryContentByUserId(entry.getKey())
                                .map(bin -> bin.getUploadFile().getSavedFileName())
                                .orElse("default_profile.png")
                )).toList();

        //return convertToUserResponseMap(allUsersMap);
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
        if (userId == null) {
            throw new IllegalUserException("User ID cannot be null");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        }

        updateUserStat(userId);

        List<BinaryContentResponse> existingProfiles = binaryContentService.findAllById(userId);

        // 기존 프로필 이미지 삭제
        for (BinaryContentResponse profile : existingProfiles) {
            binaryContentService.delete(profile.fileId());
        }

        // 새 프로필 이미지 업로드
        if (binaryContent != null && (binaryContent.getFile() != null || !binaryContent.getFiles().isEmpty())) {
            try {
                List<UploadBinaryContent> newProfiles = binaryContentService.create(binaryContent);
                log.info("Updated profile images for user: {}, number of new files: {}",
                        user.getUserName(), newProfiles.size());
            } catch (IOException e) {
                log.error("Failed to update profile images for user: {}", userId, e);
                throw new IllegalUserException("프로필 이미지 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            }
        }

        return convertToUserResponse(user.getId(), user.getUserName(), user.getUserEmail());
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

        // 프로필 이미지 삭제
        List<BinaryContentResponse> profiles = binaryContentService.findAllById(userId);
        for (BinaryContentResponse profile : profiles) {
            binaryContentService.delete(profile.fileId());
        }

        userRepository.removeUserById(userId);
        userStateService.delete(userId);

        log.info("User deleted successfully: {}", userId);
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
        UserStatus userStatus = UserStatus.createUserStatus(userId, user.userName());
        userStateService.create(new UserStatusRequest(userStatus.getUserId(), userStatus.getUserName(), userStatus.getState()));
    }

    private void saveUser(UUID userId, UserCommonRequest user) {
        User createUser = User.createUser(userId, user, UserRole.ROLE_COMMON);
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

    private static UserCommonResponse convertToUserResponse(UUID id, String userName, String userEmail) {
        return new UserCommonResponse(id, userName, userEmail);
    }

    private Map<UUID, UserResponse> convertToUserResponseMap(Map<UUID, User> allUsersMap) {
        return allUsersMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            UserStatus status = userStateService.find(entry.getKey());
                            Optional<BinaryContent> binaryContentByUserId = binaryContentService.findBinaryContentByUserId(entry.getValue().getId());
                            return new UserResponse(
                                    entry.getValue().getUserName(),
                                    entry.getValue().getUserEmail(),
                                    status.getState(),
                                    binaryContentByUserId
                                            .map(content -> content.getUploadFile().getSavedFileName())
                                            .orElse(null)
                            );
                        }
                ));
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
