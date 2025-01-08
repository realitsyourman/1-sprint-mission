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
        /**
         * ChannelService 인터페이스를 구현한 JCFChannelService 클래스를 사용
         */
        User user1 = new User("사용자1", "o134maka@gmail.com", "1234");
        User user2 = new User("사용자2", "dsadsd", "thisispassword");
        Channel channel1 = new Channel("채널1", user1, Arrays.asList(user1, user2));
        List<Channel> channelList = new ArrayList<>();

        ChannelService channelService = new JCFChannelService(channelList);
        channelService.createChannel(channel1);
        channelService.readChannelInfo("채널1");
        channelService.readAllChannels();
    }
}
