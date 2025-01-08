package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface ChannelService {
    void createChannel(Channel channel);

    void readChannelInfo(String channelName);

    void readAllChannels();

    Channel updateChannel(Channel channel);

    List<Channel> removeChannel(Channel removeChannel);
}
