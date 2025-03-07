package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.status.user.UserStatus;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserCreateResponse;
import com.sprint.mission.discodeit.entity.user.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserStatusUpdateResponse;
import com.sprint.mission.discodeit.entity.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserUpdateResponse;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStateService userStateService;
  private final BinaryContentStorage binaryContentStorage;

  /**
   * 유저 생성
   */
  @Override
  public UserCreateResponse join(UserCreateRequest request, MultipartFile file) throws IOException {
    BinaryContent profile = getProfile(file);
    User savedMember = saveUser(request, profile);

    saveUserStatus(savedMember);
    saveProfileImg(file, savedMember);

    return new UserCreateResponse(savedMember.getId(), savedMember.getUsername(),
        savedMember.getEmail(), BinaryContentMapper.toDto(savedMember.getProfile()), true);
  }

  /**
   * userId로 유저 찾기(단건)
   */
  @Transactional(readOnly = true)
  @Override
  public UserCreateResponse findById(UUID userId) {
    User findUser = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    return new UserCreateResponse(findUser.getId(), findUser.getUsername(), findUser.getEmail(),
        BinaryContentMapper.toDto(findUser.getProfile()), true);
  }

  /**
   * 모든 유저 찾기
   */
  @Transactional(readOnly = true)
  @Override
  public List<UserCreateResponse> findAll() {
    List<User> users = userRepository.findUsers();

    return users.stream()
        .map(user -> new UserCreateResponse(user.getId(), user.getUsername(), user.getEmail(),
            BinaryContentMapper.toDto(user.getProfile()), true))
        .toList();
  }

  /**
   * 유저 삭제
   */
  @Override
  public UUID delete(UUID userId) {
    userRepository.removeUserById(userId);

    return userId;
  }

  /**
   * 유저 업데이트
   */
  @Override
  public UserUpdateResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      MultipartFile profile) {

    User findUser = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    findUser.changeUser(userUpdateRequest.newUsername(), userUpdateRequest.newEmail(),
        userUpdateRequest.newPassword());

    return new UserUpdateResponse(userId, findUser.getUsername(), findUser.getEmail(),
        BinaryContentMapper.toDto(findUser.getProfile()), true);
  }

  /**
   * 유저 상태 업데이트
   */
  @Override
  public UserStatusUpdateResponse updateOnlineStatus(UUID userId, UserStatusUpdateRequest request) {
    User findUser = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    findUser.getStatus().setLastActiveAt(request.newLastActiveAt());

    return new UserStatusUpdateResponse(userId, findUser.getId(), request.newLastActiveAt());
  }

  /**
   * 유저 저장 메서드
   */
  private User saveUser(UserCreateRequest request, BinaryContent bin) {
    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(request.getPassword())
        .profile(bin)
        .build();

    return userRepository.save(user);
  }

  /**
   * 프로필 객체 생성
   */
  private static BinaryContent getProfile(MultipartFile file) {
    BinaryContent bin = null;
    if (file != null) {
      bin = BinaryContent.builder()
          .fileName(file.getOriginalFilename())
          .size(file.getSize())
          .contentType(file.getContentType())
          .build();
    }
    return bin;
  }

  /**
   * 실제로 프로필 이미지 저장
   */
  private void saveProfileImg(MultipartFile file, User savedMember) throws IOException {
    if (file != null && savedMember.getProfile() != null) {
      binaryContentStorage.put(savedMember.getProfile().getId(), file.getBytes());
    }
  }

  /**
   * 유저 상태 저장
   */
  private void saveUserStatus(User savedMember) {
    UserStatus userStatus = userStateService.create(new UserStatus(savedMember));
    savedMember.changeUserStatus(userStatus);
  }

}

/// **
// * 스프린트 미션 5 요구사항 user 등록
// */
//@PostConstruct
//public void init() {
//  log.error("주입된 userRepository: {}", userRepository.getClass().getSimpleName());
//}
//@Override
//public UserCreateResponse createUserWithProfile(UserCreateRequest request,
//    MultipartFile file) throws IOException {
//
//  // 중복 체크
//  checkDuplicated(request.username(), request.email());
//
//  if (file == null) {
//    User user = buildUser(request, null);
//    saveUserStatus(user.getId(), user.getUsername());
//
//    userRepository.save(user);
//    return getUserCreateResponse(user);
//  }
//
//  /**
//   * TODO: 지금 BinaryContent가 필요한 상황임. 근데 String만 반환중임 -> 이거 고치기
//   */
//  String savedFileName = getSavedFile(file);
//
//  User user = buildUser(request, savedFileName);
//  saveUserStatus(user.getId(), user.getUsername());
//  userRepository.save(user);
//
//  return getUserCreateResponse(user);
//}
//
/// **
// * 스프린트 미션 5 요구사항, user 목록 조회
// */
//@Override
//public List<UserFindResponse> findAllUsers() {
//  Map<UUID, User> allUser = userRepository.findAllUser();
//
//  return allUser.values().stream()
//      .map(user -> new UserFindResponse(
//          user.getId(),
//          user.getCreatedAt(),
//          user.getUpdatedAt(),
//          user.getUserName(),
//          user.getUserEmail(),
//          user.getProfileId(),
//          userStateService.find(user.getId()).getState().equals("online")
//      ))
//      .toList();
//}
//
/// **
// * 스프린트 미션 5, 유저 업데이트, 프사도 같이 가능
// */
//@Override
//public UserUpdateResponse updateUser(UUID userId, UserUpdateRequest request, MultipartFile file)
//    throws IOException {
//
//  checkNull(userId);
//  checkDuplicated(request.newUsername(), request.newEmail());
//
//  if (file == null) {
//    return savedNoneProfileImgUser(userId, request);
//  }
//
//  String savedFileName = getSavedFile(file);
//
//  User updateUser = updateUserInfo(userId, request, savedFileName);
//
//  return getUserUpdateResponse(updateUser);
//}
//
//@Override
//public UserCommonResponse createUser(UserCommonRequest user) {
//  if (user == null) {
//    throw new UserNotFoundException("null user");
//  }
//  checkDuplicated(user.userName(), user.userEmail());
//
//  UUID userId = UUID.randomUUID();
//  return createBasicUser(userId, user);
//}
//
//@Override
//public UserCreateWithBinaryContentResponse createUserWithProfile(UserCommonRequest createDto,
//    BinaryContentRequest request) {
//  UserCommonResponse userResponse = createUser(createDto);
//  request.updateId(userResponse.id()); // channelId는 null인 상태, requestUserId는 있음
//
//  UserCreateWithBinaryContentResponse userWithFileResponse = new UserCreateWithBinaryContentResponse(
//      userResponse.id(),
//      userResponse.userName(),
//      userResponse.userEmail(),
//      request.getFileName()
//  );
//
//  if (request.getFile() == null && request.getFiles().isEmpty()) {
//    log.info("유저 생성 완료: {}", userWithFileResponse.getUserName());
//    return userWithFileResponse;
//  }
//
//  try {
//    User user = userRepository.findUserByEmail(createDto.userEmail());
//    if (user == null) {
//      throw new UserNotFoundException("null user");
//    }
//
//    // 프로필 이미지 업로드 및 저장
//    List<UploadBinaryContent> uploadBinaryContents = binaryContentService.create(request);
//    log.info("프사 있는 유저 생성: {}", user.getUserName());
//
//    UploadBinaryContent uploadBinaryContent = uploadBinaryContents.get(0);
//    userWithFileResponse.setFileName(uploadBinaryContent.getSavedFileName());
//
//    return userWithFileResponse;
//  } catch (IOException e) {
//    log.error("유저 생성 실패: {}", createDto.userName(), e);
//    throw new IllegalUserException("프로필 이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
//  }
//}
//
//@Override
//public UserResponse find(UUID userId) {
//  User user = userRepository.findUserById(userId);
//  if (user == null) {
//    throw new UserNotFoundException("null user");
//  }
//
//  UserStatus status = userStateService.find(userId);
//  BinaryContentResponse profileImage = null;
//  try {
//    profileImage = binaryContentService.find(userId.toString());
//  } catch (Exception e) {
//    log.debug("No profile image found for user: {}", userId);
//  }
//
//  return new UserResponse(user.getUserName(), user.getUserEmail(), status.getState());
//}
//
/// / 유저 이름으로 찾기
//
//@Override
//public UserCommonResponse find(String userName) {
//  User findUser = userRepository.findUserByName(userName);
//
//  if (findUser == null) {
//    throw new UserNotFoundException(userName);
//  }
//
//  return convertToUserResponse(findUser.getId(), findUser.getUserName(), findUser.getUserEmail());
//}
//
/// **
// * 사용자의 온라인 상태도 같이 포함 패스워드 정보 X
// */
//@Override
//public List<UserResponse> findAll() {
//  Map<UUID, User> allUsersMap = Optional.ofNullable(userRepository.findAllUser())
//      .orElseThrow(() -> new UserNotFoundException("null"));
//
//  return allUsersMap.entrySet().stream()
//      .map(entry -> new UserResponse(
//          entry.getValue().getUserName(),
//          entry.getValue().getUserEmail(),
//          userStateService.find(entry.getKey()).getState(),
//          binaryContentService.findBinaryContentByUserId(entry.getKey())
//              .map(bin -> bin.getUploadFile().getSavedFileName())
//              .orElse("default_profile.png")
//      )).toList();
//
//  //return convertToUserResponseMap(allUsersMap);
//}
//
/// **
// * - [ ] 선택적으로 프로필 이미지를 대체할 수 있습니다. - [ ] DTO를 활용해 파라미터를 그룹화합니다. - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
// */
//@Override
//public UserCommonResponse update(String userName, UserCommonRequest updateDto) {
//  User findUser = userRepository.findUserByName(userName);
//  if (findUser == null) {
//    throw new UserNotFoundException();
//  }
//
//  findUser.updateName(updateDto.userName());
//  findUser.updateEmail(updateDto.userEmail());
//  findUser.updatePassword(updateDto.userPassword());
//
//  userStateService.updateByUserName(userName, updateDto);
//  userRepository.userSave(findUser);
//
//  return new UserCommonResponse(findUser.getId(), findUser.getUserName(),
//      findUser.getUserEmail());
//}
//
//
/// **
// * 프사도 바꿀 수 있음
// */
//@Override
//public UserCommonResponse updateUserWithProfile(UUID userId, BinaryContentRequest binaryContent) {
//  if (userId == null) {
//    throw new IllegalUserException("User ID cannot be null");
//  }
//
//  User user = userRepository.findUserById(userId);
//  if (user == null) {
//    throw new UserNotFoundException();
//  }
//
//  updateUserStat(userId);
//
//  List<BinaryContentResponse> existingProfiles = binaryContentService.findAllById(userId);
//
//  // 기존 프로필 이미지 삭제
//  for (BinaryContentResponse profile : existingProfiles) {
//    binaryContentService.delete(profile.id());
//  }
//
//  // 새 프로필 이미지 업로드
//  if (binaryContent != null && (binaryContent.getFile() != null || !binaryContent.getFiles()
//      .isEmpty())) {
//    try {
//      List<UploadBinaryContent> newProfiles = binaryContentService.create(binaryContent);
//      log.info("Updated profile images for user: {}, number of new files: {}",
//          user.getUserName(), newProfiles.size());
//    } catch (IOException e) {
//      log.error("Failed to update profile images for user: {}", userId, e);
//      throw new IllegalUserException("프로필 이미지 업데이트 중 오류가 발생했습니다: " + e.getMessage());
//    }
//  }
//
//  return convertToUserResponse(user.getId(), user.getUserName(), user.getUserEmail());
//}
//
//
/// **
// * 관련된 도메인도 같이 삭제합니다. - `BinaryContent`(프로필), `UserStatus`
// */
//@Override
//public UUID deleteUser(UUID userId) {
//  if (userId == null) {
//    throw new IllegalUserException("userId를 다시 확인해주세요.");
//  }
//
//  // 프로필 이미지 삭제
//  List<BinaryContentResponse> profiles = binaryContentService.findAllById(userId);
//  for (BinaryContentResponse profile : profiles) {
//    binaryContentService.delete(profile.id());
//  }
//
//  userRepository.removeUserById(userId);
//  userStateService.delete(userId);
//
//  log.info("User deleted successfully: {}", userId);
//  return userId;
//}
//
//private void updateUserStat(UUID userId) {
//  Map<UUID, UserStatus> findUserStatMap = userStateService.findAllByUserId(userId);
//  UserStatus userStatus = findUserStatMap.values().stream()
//      .findFirst()
//      .orElseThrow(() -> new IllegalArgumentException("찾는 userStatus가 없습니다."));
//  userStatus.updateUserStatus();
//  userStateService.update(new UserStatusRequest(userStatus.getUserId(), userStatus.getUserName(),
//      userStatus.getState()));
//}
//
//private UserCommonResponse createBasicUser(UUID userId, UserCommonRequest user) {
//  if (validator.isNullParam(user.userName(), user.userPassword())) {
//    throw new IllegalUserException();
//  }
//
//  saveUser(userId, user);
//  saveUserStatus(userId, user.userName());
//
//  log.warn("유저 등록: {}", user.userName());
//
//  return new UserCommonResponse(userId, user.userName(), user.userEmail());
//}
//
//private UserUpdateResponse savedNoneProfileImgUser(UUID userId, UserUpdateRequest request) {
//  User user = userRepository.findUserById(userId);
//
//  checkNullAndUpdateUser(request, user);
//
//  userRepository.userSave(user);
//  return getUserUpdateResponse(user);
//}
//
//private void saveUserStatus(UUID userId, String username) {
//  UserStatus userStatus = UserStatus.createUserStatus(userId, username);
//  userStateService.create(new UserStatusRequest(userStatus.getUserId(), userStatus.getUserName(),
//      userStatus.getState()));
//}
//
//private static UserUpdateResponse getUserUpdateResponse(User updateUser) {
//  return new UserUpdateResponse(updateUser.getId(), updateUser.getCreatedAt(), Instant.now(),
//      updateUser.getUserName(), updateUser.getUserEmail(), updateUser.getUserPassword(),
//      updateUser.getProfileId());
//}
//
//private void saveUser(UUID userId, UserCommonRequest user) {
//  User createUser = User.createUser(userId, user, UserRole.ROLE_COMMON);
//  userRepository.userSave(createUser);
//}
//
//private String getSavedFile(MultipartFile file) throws IOException {
//  BinaryContentRequest binaryContent = BinaryContentRequest.builder()
//      .fileName(file.getOriginalFilename())
//      .file(file)
//      .build();
//
//  List<UploadBinaryContent> uploadBinaryContents = binaryContentService.create(binaryContent);
//  return Objects.requireNonNull(uploadBinaryContents.stream()
//          .findFirst()
//          .orElse(null))
//      .getSavedFileName();
//}
//
//private void checkDuplicated(String name, String email) {
//  /**
//   * `username`과 `email`은 다른 유저와 같으면 안됩니다.
//   */
//
//  List<User> allUsers = userRepository.findAll();
//  if (allUsers.isEmpty()) {
//    return;
//  }
//
//  boolean isSameUser = allUsers.stream()
//      .anyMatch(users -> users.getUsername().equals(name));
//
//  boolean isSameEmail = allUsers.stream()
//      .anyMatch(users -> users.getUsername().equals(email));
//
//  if (isSameEmail) {
//    throw new UserExistsException(email);
//  } else if (isSameUser) {
//    throw new UserExistsException(name);
//  }
//}
//
//private static UserCommonResponse convertToUserResponse(UUID id, String userName,
//    String userEmail) {
//  return new UserCommonResponse(id, userName, userEmail);
//}
//
//private Map<UUID, UserResponse> convertToUserResponseMap(Map<UUID, User> allUsersMap) {
//  return allUsersMap.entrySet().stream()
//      .collect(Collectors.toMap(
//          Map.Entry::getKey,
//          entry -> {
//            UserStatus status = userStateService.find(entry.getKey());
//            Optional<BinaryContent> binaryContentByUserId = binaryContentService.findBinaryContentByUserId(
//                entry.getValue().getId());
//            return new UserResponse(
//                entry.getValue().getUserName(),
//                entry.getValue().getUserEmail(),
//                status.getState(),
//                binaryContentByUserId
//                    .map(content -> content.getUploadFile().getSavedFileName())
//                    .orElse(null)
//            );
//          }
//      ));
//}
//
//private static UserCreateResponse getUserCreateResponse(User user) {
//  return new UserCreateResponse(user.getId(), Instant.now(), Instant.now(), user.getUsername(),
//      user.getEmail(), user.getPassword(),
//      user.getProfile().getFileName());
//}
//
//private static User buildUser(UserCreateRequest request, String savedFileName) {
//  return User.builder()
//      .username(request.username())
//      .email(request.email())
//      .password(request.password())
//      .profile(savedFileName)
//      .build();
//}
//
//private User updateUserInfo(UUID userId, UserUpdateRequest request, String savedFile) {
//  User findUser = userRepository.findUserById(userId);
//
//  checkNullAndUpdateUser(request, findUser);
//  if (savedFile != null) {
//    findUser.setProfileId(savedFile);
//  }
//
//  return userRepository.userSave(findUser);
//}
//
//private void checkNull(UUID userId) {
//  User user = userRepository.findUserById(userId);
//  if (user == null) {
//    throw new UserNotFoundException(userId.toString());
//  }
//}
//
//private static void checkNullAndUpdateUser(UserUpdateRequest request, User findUser) {
//  if (request.newUsername() != null && !request.newUsername().isEmpty()) {
//    findUser.setUserName(request.newUsername());
//  }
//
//  if (request.newEmail() != null && !request.newEmail().isEmpty()) {
//    findUser.setUserEmail(request.newEmail());
//  }
//
//  if (request.newPassword() != null && !request.newPassword().isEmpty()) {
//    findUser.setUserPassword(request.newPassword());
//  }
//}
