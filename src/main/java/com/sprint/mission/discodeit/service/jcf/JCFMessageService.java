package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messagesList;

    public JCFMessageService(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    public JCFMessageService() {
        this.messagesList = new ArrayList<>();
    }

    @Override
    public Message sendMessage(String title, String content, User sender, User receiver) {
        Message message = new Message(title, content, sender, receiver);
        messagesList.add(message);
        return message;
    }

    @Override
    public Message getMessageById(UUID messageId) {
        return messagesList.stream()
                .filter(message -> message.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾는 메세지가 없습니다."));
    }

    @Override
    public List<Message> getAllMessages() {
        return messagesList.stream().toList();
    }

    @Override
    public Message updateMessage(UUID messageId, String newTitle, String newContent) {
        Message message = getMessageById(messageId);
        message.updateMessage(newTitle, newContent);

        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message findMessage = getMessageById(messageId);
        messagesList.remove(findMessage);
    }
}
