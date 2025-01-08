package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaApplication {
    public static void main(java.lang.String[] args) {
        User user1 = new User("사용자1", "dsadsd", "1234");
        Channel channel1 = new Channel("채널1", user1, Arrays.asList(user1));
        List<Channel> channelList = new ArrayList<>();

        ChannelService channelService = new JCFChannelService(channelList);
        channelService.createChannel(channel1);
        channelService.readChannelInfo("채널1");
    }
}
