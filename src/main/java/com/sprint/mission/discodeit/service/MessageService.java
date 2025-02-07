package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.message.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.message.MessageSendFileRequest;
import com.sprint.mission.discodeit.entity.message.MessageUpdateRequest;

import java.util.Map;
import java.util.UUID;

public interface MessageService {
    Message createMessage(MessageCreateRequest createRequest, MessageSendFileRequest... fileRequests);

    Message getMessageById(UUID messageId);

    Map<UUID, Message> findAllByChannelId(UUID channelId);

    Message updateMessage(MessageUpdateRequest updateRequest);

    void deleteMessage(UUID messageId);
}
