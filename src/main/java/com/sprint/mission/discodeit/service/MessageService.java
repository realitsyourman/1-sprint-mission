package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
    void sendMessage(Message sendMessage);

    void readMessage(Message readMessage);

    void readAllMessages();

    Message updateMessage(Message exMessage ,Message updateMessage);

    void removeMessage(Message garbageMessage);
}
