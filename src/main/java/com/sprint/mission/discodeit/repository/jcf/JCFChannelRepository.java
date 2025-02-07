package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class JCFChannelRepository implements ChannelRepository{

    Map<UUID, Channel> channelMap = new HashMap<>();

    @Override
    public Channel saveChannel(Channel channel) {
        channelMap.put(channel.getId(), channel);
        return channelMap.get(channel.getId());
    }

    @Override
    public void removeChannelById(UUID channelId) {
        channelMap.remove(channelId);
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return channelMap.get(channelId);
    }

    @Override
    public Map<UUID, Channel> findAllChannel() {
        return channelMap;
    }
}
