package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channelList;

    public JCFChannelService(List<Channel> channelList) {
        this.channelList = channelList;
    }

    @Override
    public void createChannel(Channel channel) {
        channelList.add(channel);
    }

    @Override
    public void readChannelInfo(String channelName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = new Date();

        channelList.stream()
                .filter(readChannel -> readChannel.getChannelName().equals(channelName))
                .forEach(readChannel -> {
                    data.setTime(readChannel.getCreatedAt());
                    String createdAt = simpleDateFormat.format(data); /* 유닉스 타임스탬프 변경 */

                    System.out.println("채널 이름: " + readChannel.getChannelName());
                    System.out.println("채널 소유자: " + readChannel.getChannelOwnerUser().getUserName());
                    System.out.println("채널 생성시간: " + createdAt);
                });
    }


    @Override
    public void readAllChannels() {
        channelList.forEach(channel -> readChannelInfo(channel.getChannelName()));
    }

    @Override
    public void updateChannel(Channel exChannel ,Channel updateChannel) {
        channelList.stream()
                .filter(exCh -> exCh.getChannelId().equals(exChannel.getChannelId()))
                .forEach(upCh -> {
                    upCh.updateChannelName(updateChannel.getChannelName());
                    upCh.updateOwnerUser(updateChannel.getChannelOwnerUser());
                    upCh.updateChannelUsers(updateChannel.getChannelUsers());
                });
    }

    @Override
    public List<Channel> removeChannel(Channel removeChannel) {
        return channelList.stream()
                .filter(removalChannel -> removalChannel.getChannelId().equals(removeChannel.getChannelId()))
                .toList();
    }
}
