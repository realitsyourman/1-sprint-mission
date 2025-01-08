package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JCFMessageService implements MessageService {
    private final List<Message> messagesList;

    public JCFMessageService(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public void sendMessage(Message sendMessage) {
        messagesList.add(sendMessage);
    }

    @Override
    public void readMessage(Message message) {
        Date data = new Date();
        data.setTime(message.getCreatedAt());
        String createdAt = new SimpleDateFormat("HH:mm:ss").format(data);

        System.out.println(
                "(" + message.getMessageSendUser().getUserName() +") " + message.getMessageTitle() + "\n"
                + message.getMessageContent() + " (" + message.getMessageReceiveUser().getUserName() + ", " + createdAt +")"
        );
    }

    @Override
    public void readAllMessages() {
        messagesList.forEach(this::readMessage);
    }

    @Override
    public Message updateMessage(Message exMessage, Message updateMessage) {
        exMessage.updateMessageTitle(updateMessage.getMessageTitle());
        exMessage.updateMessageContent(updateMessage.getMessageContent());

        sendMessage(exMessage);
        return exMessage;
    }

    @Override
    public void removeMessage(Message garbageMessage) {
        messagesList.remove(garbageMessage);
    }
}
