package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID channelId;
    private Long createdAt;
    private Long updatedAt;
    private String channelName;
    private User channelOwnerUser;
    private List<User> channelUsers;

    public Channel(String channelName, User channelOwnerUser, List<User> channelUsers) {
        this.channelId = UUID.randomUUID();
        this.channelName = channelName;
        this.channelOwnerUser = channelOwnerUser;
        this.channelUsers = channelUsers;
        this.createdAt = System.currentTimeMillis();
    }

    public String updateChannelName(String updateChannelName) {
        this.channelName = updateChannelName;
        this.updatedAt = System.currentTimeMillis();
        return this.channelName;
    }

    public User updateOwnerUser(User updateOwnerUser) {
        this.channelOwnerUser = updateOwnerUser;
        this.updatedAt = System.currentTimeMillis();
        return this.channelOwnerUser;
    }

    public User addChannelUser(User addChannelUser) {
        this.channelUsers.add(addChannelUser);
        this.updatedAt = System.currentTimeMillis();
        return addChannelUser;
    }

    public List<User> updateChannelUsers(List<User> updateChannelUsers) {
        this.channelUsers = updateChannelUsers;
        this.updatedAt = System.currentTimeMillis();
        return this.channelUsers;
    }


    public UUID getChannelId() {
        return channelId;
    }

    public java.lang.String getChannelName() {
        return channelName;
    }

    public User getChannelOwnerUser() {
        return channelOwnerUser;
    }

    public List<User> getChannelUsers() {
        return channelUsers;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
