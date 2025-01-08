package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID userId;
    private Long createdAt;
    private Long updatedAt;
    private String userName;
    private String userEmail;
    private String userPassword;

    public User(String userName, String userEmail, String userPassword) {
        this.userId = UUID.randomUUID();
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.createdAt = System.currentTimeMillis();
    }

    public String updateName(String updateName) {
        this.userName = updateName;
        this.updatedAt = System.currentTimeMillis();
        return this.userName;
    }

    public String updateEmail(String updateEmail) {
        this.userEmail = updateEmail;
        this.updatedAt = System.currentTimeMillis();
        return this.userEmail;
    }

    public String password(String updatePassword) {
        this.userPassword = updatePassword;
        this.updatedAt = System.currentTimeMillis();
        return this.userPassword;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
