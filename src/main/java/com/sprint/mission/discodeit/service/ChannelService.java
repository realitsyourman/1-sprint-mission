package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);

    void readChannelInfo(String channelName);

    void readAllChannels();

    void updateChannel(Channel exChannel, Channel updateChannel);

    void removeChannel(String removeChannelString);

    void kickUser(User kickUser);
}
