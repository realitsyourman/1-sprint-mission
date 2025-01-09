package com.sprint.mission.discodeit.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    User sender = new User("sender", "sender@gmail.com" ,"123456");
    User receiver = new User("receiver", "sendme@naver.com", "dsadssdasasd");
    @Test
    @DisplayName("메세지 내용이 비었는지 검증")
    void checkMessageContentNone() {
        assertThrows(IllegalArgumentException.class, () -> new Message("hi", "", sender, receiver));
    }

    @Test
    @DisplayName("checkMessageContent() 일부러 예외 발생")
    void checkMessageContentNoneException() {
        assertThrows(IllegalArgumentException.class, () -> new Message("", "안녕하세요", sender, receiver));
    }

    @Test
    @DisplayName("메세지를 보내거나 받는 사람 검증")
    void checkSenderAndReciver() {
        assertThrows(IllegalArgumentException.class, () -> new Message("hi", "안녕하세요?", null, null));
        assertThrows(IllegalArgumentException.class, () -> new Message("hi", "안녕하세요?", null, receiver));
        assertThrows(IllegalArgumentException.class, () -> new Message("hi", "안녕하세요?", sender, null));
        assertThrows(IllegalArgumentException.class, () -> new Message("hi", "안녕하세요?", sender, sender));
    }

}