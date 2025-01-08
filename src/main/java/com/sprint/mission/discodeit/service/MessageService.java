package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;

public interface MessageService {
    Message createMessage(Message sendMessage);
    Message readMessage(Message readMessage);
    List<Message> readAllMessages();
    Message updateMessage(Message updateMessage);
    void removeMessage(Message garbageMessage);
}
