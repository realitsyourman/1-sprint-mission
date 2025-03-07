package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserCreateResponse;
import com.sprint.mission.discodeit.entity.user.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserStatusUpdateResponse;
import com.sprint.mission.discodeit.entity.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserUpdateResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserCreateResponse join(UserCreateRequest request, MultipartFile file) throws IOException;

  UserCreateResponse findById(UUID userId);

  List<UserCreateResponse> findAll();

  UUID delete(UUID userId);

  UserUpdateResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      MultipartFile profile);

  UserStatusUpdateResponse updateOnlineStatus(UUID userId, UserStatusUpdateRequest request);

//  UserCommonResponse createUser(
//      UserCommonRequest user); // `username`과 `email`은 다른 유저와 같으면 안됩니다, `UserStatus`를 같이 생성합니다.
//
//  default UserCreateWithBinaryContentResponse createUserWithProfile(UserCommonRequest createDto,
//      BinaryContentRequest request) throws IOException {
//    return null;
//  } // 선택적으로 프로필 이미지를 같이 등록할 수 있습니다.
//
//  UserCreateResponse createUserWithProfile(UserCreateRequest request,
//      MultipartFile file) throws IOException;
//
//  /**
//   * - [x] 사용자의 온라인 상태 정보를 같이 포함하세요. - [x] 패스워드 정보는 제외하세요.
//   */
//  UserResponse find(UUID userId); // 유저 찾기
//
//  UserCommonResponse find(String userName);
//
//  List<UserResponse> findAll(); // 유저 전부 찾기
//
//  UserCommonResponse update(String userName, UserCommonRequest updateDto); // 유저 정보 업데이트
//
//  default UserCommonResponse updateUserWithProfile(UUID userId,
//      BinaryContentRequest binaryContent) {
//    return null;
//  } // 선택적으로 프로필 이미지를 대체할 수 있습니다.
//
//  UserUpdateResponse updateUser(UUID userId, UserUpdateRequest request, MultipartFile file)
//      throws IOException;
//
//  /**
//   * 관련된 도메인도 같이 삭제합니다. - `BinaryContent`(프로필), `UserStatus`
//   */
//  UUID deleteUser(UUID userId);
//
//  List<UserFindResponse> findAllUsers();
}
