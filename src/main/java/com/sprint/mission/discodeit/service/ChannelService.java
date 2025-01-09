package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public interface ChannelService {
    void createChannel(Channel channel);

    void readChannelInfo(String channelName);

    void readAllChannels();

    Channel updateChannel(Channel exChannel, Channel updateChannel);

    void removeChannel(String removeChannelString);

    void kickUser(User kickUser);
}
