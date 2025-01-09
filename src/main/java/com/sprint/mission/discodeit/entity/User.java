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
        checkUserName(userName);
        checkUserEmail(userEmail);
        checkUserPassword(userPassword);
        this.userId = UUID.randomUUID();
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean checkUserName(String userName) {
        if(userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("이름을 작성해주세요.");
        }
        return true;
    }

    public boolean checkUserEmail(String userEmail) {
        final String EMAIL_REGEX = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$";

        if(userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("이메일을 적어주세요.");
        }
        else if(!userEmail.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("이메일 형식이 잘못되었습니다.");
        }

        return true;
    }

    public boolean checkUserPassword(String userPassword) {
        if(userPassword == null || userPassword.length() < 6) {
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        }
        return true;
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

    public String updatePassword(String updatePassword) {
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
