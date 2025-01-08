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
        List<Channel> channelList = new ArrayList<>();
        User user1 = new User("사용자1", "o134maka@gmail.com", "1234");
        User user2 = new User("사용자2", "dsadsd@naver.com", "thisispassword");
        Channel codeit = new Channel("코드잇", user1, Arrays.asList(user1, user2));
        Channel game = new Channel("게임방", user2, Arrays.asList(user2));

        ChannelService channelService = new JCFChannelService(channelList);
        channelService.createChannel(codeit);
        channelService.readChannelInfo("코드잇");

        System.out.println("\n모든 채널 출력");
        channelService.createChannel(game);
        channelService.readAllChannels();

        System.out.println("\n특정 채널 수정");
        Channel updateGame = new Channel("게임 디코방", user1, Arrays.asList(user1)); // 업데이트할 채널
        channelService.updateChannel(game ,updateGame); // 업데이트
        channelService.readAllChannels();

        System.out.println("\n특정 채널 삭제");
        channelService.removeChannel(updateGame.getChannelName());
        channelService.readAllChannels();
        System.out.println();
    }
}
