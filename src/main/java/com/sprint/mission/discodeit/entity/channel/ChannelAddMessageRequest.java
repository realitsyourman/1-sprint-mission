package com.sprint.mission.discodeit.entity.channel;

import com.sprint.mission.discodeit.entity.message.Message;

import java.util.UUID;

public record ChannelAddMessageRequest (
        UUID channelId,
        Message message
){
}
