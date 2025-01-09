package com.sprint.mission.discodeit.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    @DisplayName("유저가 제대로 생성되었는지 확인")
    void createUser() {
        User user = new User("userId", "test@email.com", "testPassword");
        assertEquals("userId", user.getUserName());
        assertEquals("test@email.com", user.getUserEmail());
        assertEquals("testPassword", user.getUserPassword());
    }

    @Test
    @DisplayName("잘못된 유저 생성 검증(일부러 예외 발생)")
    void createUserException() {
        User user = new User("", "testemail.com", "word");
        assertEquals("", user.getUserName());
        assertEquals("testemail.com", user.getUserEmail());
        assertEquals("word", user.getUserPassword());
    }

    @Test
    @DisplayName("이름이 없을 때 검증")
    void createNoneUserName() {
        assertThrows(IllegalArgumentException.class, () -> new User("", "testEmail", "testPassword"));
    }

    @Test
    @DisplayName("이메일이 없거나 잘못된 형식 검증")
    void checkUserEmailNoneOrWrong() {
        assertThrows(IllegalArgumentException.class, () -> new User("testUser", "", "dqwddq))"));
        assertThrows(IllegalArgumentException.class, () -> new User("testUser", "testEmailnavercom", "dqwddq"));
        assertThrows(IllegalArgumentException.class, () -> new User("testUser", "testEmail@navercom", "dqwddq"));
        assertThrows(IllegalArgumentException.class, () -> new User("testUser", "sdasdsgmail.com", "d1234124"));
    }

    @Test
    @DisplayName("비밀번호가 없거나 길이가 6보다 작을 경우 검증")
    void checkUserPasswordNoneOrShort() {
        assertThrows(IllegalArgumentException.class, () -> new User("testUser", "testEmail", ""));
        assertThrows(IllegalArgumentException.class, () -> new User("testUser", "testEmail", "12345"));
    }
}