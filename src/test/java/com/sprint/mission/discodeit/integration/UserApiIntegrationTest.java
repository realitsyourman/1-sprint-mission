package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.mission.discodeit.entity.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserCreateResponse;
import com.sprint.mission.discodeit.entity.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.user.dto.UserUpdateResponse;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserApiIntegrationTest {

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("유저 생성")
  void createUser() throws Exception {
    UserCreateRequest request = new UserCreateRequest("userA", "userA@mail.com", "password");

    UserCreateResponse createUser = userService.join(request, null);

    assertNotNull(createUser);
    assertEquals("userA", createUser.username());
    assertEquals("userA@mail.com", createUser.email());
  }

  @Test
  @DisplayName("유저 생성 - 프로필 사진과 함께")
  void createUserWithProfileImg() throws Exception {
    UserCreateRequest request = new UserCreateRequest("userA", "userA@mail.com", "password");

    Path path = Paths.get(".discodeit/storage/f8f4f14b-9d06-495e-9a6b-574cef3119f1");
    String fileName = "f8f4f14b-9d06-495e-9a6b-574cef3119f1";
    String originalName = "test_img.jpg";
    String contentType = "image/jpeg";
    byte[] content = Files.readAllBytes(path);

    MockMultipartFile file = new MockMultipartFile(fileName, originalName, contentType,
        content);

    UserCreateResponse createUser = userService.join(request, file);

    assertNotNull(createUser);
    assertEquals("userA", createUser.username());
    assertEquals("userA@mail.com", createUser.email());
    assertEquals(originalName, createUser.profile().fileName());
  }

  @Test
  @DisplayName("유저 수정")
  void modifyUser() throws Exception {
    UUID user1Id = UUID.fromString("3a2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0");
    UserUpdateRequest request = new UserUpdateRequest("newUser", "new@mail.com", "newPassword");

    UserUpdateResponse updatedUser = userService.update(user1Id, request, null);

    assertNotNull(updatedUser);
    assertEquals("newUser", updatedUser.username());
    assertEquals("new@mail.com", updatedUser.email());
  }

  @Test
  @DisplayName("유저 수정 - 프로필 이미지 업데이트")
  void modifyUserWithProfileImg() throws Exception {
    UUID user1Id = UUID.fromString("3a2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0");
    UserUpdateRequest request = new UserUpdateRequest("newUser", "new@mail.com", "newPassword");

    Path path = Paths.get(".discodeit/storage/f8f4f14b-9d06-495e-9a6b-574cef3119f1");
    String fileName = "f8f4f14b-9d06-495e-9a6b-574cef3119f1";
    String originalName = "test_img.jpg";
    String contentType = "image/jpeg";
    byte[] content = Files.readAllBytes(path);

    MockMultipartFile file = new MockMultipartFile(fileName, originalName, contentType,
        content);

    UserUpdateResponse updatedUser = userService.update(user1Id, request, file);

    assertNotNull(updatedUser);
    assertEquals("newUser", updatedUser.username());
    assertEquals("new@mail.com", updatedUser.email());
    assertEquals("test_img.jpg", updatedUser.profile().fileName());
  }

  @Test
  @DisplayName("유저 삭제")
  void deleteUser() throws Exception {
    UUID user1Id = UUID.fromString("3a2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0");

    UUID deletedId = userService.delete(user1Id);

    assertThrows(UserNotFoundException.class, () -> userService.findById(user1Id));
    assertEquals(user1Id, deletedId);
  }

  @Test
  @DisplayName("유저 삭제 - 없는 유저 삭제")
  void notFoundUserDelete() throws Exception {
    UUID noneUser = UUID.fromString("9f2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0");

    assertThrows(UserNotFoundException.class, () -> userService.delete(noneUser));
  }
}
