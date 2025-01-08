package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String messageContent;
    private User messageSendUser;
    private User messageReceiveUser;

    public Message(String messageContent, User messageSendUser, User messageReceiveUser) {
        this.id = UUID.randomUUID();
        this.messageContent = messageContent;
        this.messageSendUser = messageSendUser;
        this.messageReceiveUser = messageReceiveUser;
        this.createdAt = System.currentTimeMillis();
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
