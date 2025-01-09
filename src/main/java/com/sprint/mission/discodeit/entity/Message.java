package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String messageTitle;
    private String messageContent;
    private User messageSendUser;
    private User messageReceiveUser;

    public Message(String title ,String messageContent, User messageSendUser, User messageReceiveUser) {
        checkMessageContent(messageContent);
        checkSenderAndReciver(messageSendUser, messageReceiveUser);
        this.id = UUID.randomUUID();
        this.messageTitle = title;
        this.messageContent = messageContent;
        this.messageSendUser = messageSendUser;
        this.messageReceiveUser = messageReceiveUser;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean checkMessageContent(String messageContent) {
        if(messageContent == null || messageContent.isEmpty()) {
            throw new IllegalArgumentException("메시지 내용을 작성해주세요.");
        }

        return true;
    }

    public boolean checkSenderAndReciver(User messageSendUser, User messageReceiveUser) {
        if(messageSendUser == null || messageReceiveUser == null) {
            throw new IllegalArgumentException("메시지를 보내는 사람과 받는 사람을 작성해주세요.");
        }
        else if(messageSendUser.equals(messageReceiveUser)) {
            throw new IllegalArgumentException("메시지를 보내는 사람과 받는 사람이 같습니다.");
        }

        return true;
    }

    public String updateMessageTitle(String updateMessageTitle) {
        this.messageTitle = updateMessageTitle;
        this.updatedAt = System.currentTimeMillis();
        return this.messageContent;
    }

    public String updateMessageContent(String updateMessageContent) {
        this.messageContent = updateMessageContent;
        this.updatedAt = System.currentTimeMillis();
        return this.messageContent;
    }

    public User updateSendUser(User updateSendUser) {
        this.messageSendUser = updateSendUser;
        this.updatedAt = System.currentTimeMillis();
        return this.messageSendUser;
    }

    public User receiveUser(User updateReceiveUser) {
        this.messageReceiveUser = updateReceiveUser;
        this.updatedAt = System.currentTimeMillis();
        return this.messageReceiveUser;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public UUID getId() {
        return id;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public User getMessageSendUser() {
        return messageSendUser;
    }

    public User getMessageReceiveUser() {
        return messageReceiveUser;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
