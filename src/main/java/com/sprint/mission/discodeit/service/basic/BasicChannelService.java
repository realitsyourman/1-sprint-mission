package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicChannelService implements ChannelService {
    private static final EntityFactory entityFactory = BaseEntityFactory.getInstance();
    private final ChannelRepository channelRepository;

    //private final MessageRepository messageRepository = new FileMessageRepository();

    private final MessageService messageService;

    public BasicChannelService(ChannelRepository channelRepository, MessageService messageService) {
        this.channelRepository = channelRepository;
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String channelName, User owner, Map<UUID, User> userList) {
        Channel channel = entityFactory.createChannel(channelName, owner, userList);

        channelRepository.saveChannel(channel);

        return channel;
    }

    @Override
    public Map<UUID, Channel> getChannelByName(String channelName) {
        Map<UUID, Channel> allChannels = getAllChannels();

        return allChannels.entrySet().stream()
                .filter(entry -> entry.getValue().getChannelName().equals(channelName))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return channelRepository.findChannelById(channelId);
    }

    @Override
    public Map<UUID, Channel> getAllChannels() {
        return channelRepository.findAllChannel();
    }

    @Override
    public Channel updateChannel(UUID channelUUID, String channelName, User changeUser) {
        Channel findChannel = channelRepository.findChannelById(channelUUID);

        findChannel.updateChannelName(channelName);
        findChannel.updateOwnerUser(changeUser);

        return channelRepository.saveChannel(findChannel);
    }

    @Override
    public void removeChannelById(UUID channelUUID) {
        channelRepository.removeChannelById(channelUUID);
    }

    @Override
    public void addUserChannel(UUID channelUUID, User addUser) {
        Map<UUID, Channel> allChannel = channelRepository.findAllChannel();

        Channel addUserChannel = allChannel.get(channelUUID);
        addUserChannel.addUser(addUser);

        channelRepository.saveChannel(addUserChannel);
    }

    @Override
    public void kickUserChannel(UUID channelUUID, User kickUser) {
        Channel findChannel = channelRepository.findChannelById(channelUUID);

        findChannel.removeUser(kickUser);

        channelRepository.saveChannel(findChannel);
    }

    // 채널은 메세지를 가지고 있음. 메세지를 추가
    @Override
    public void addMessageInCh(UUID channelId, Message message) {
        Channel findChannel = findChannelById(channelId);
        Message addMessage = findChannel.addMessageInChannel(message);

        // 메세지 저장
        //messageRepository.saveMessage(addMessage);
        messageService.createMessage(message.getMessageTitle(),
                message.getMessageContent(),
                message.getMessageSendUser(),
                message.getMessageReceiveUser());

        // 채널 저장
        channelRepository.saveChannel(findChannel);
    }

    @Override
    public void removeMessageInCh(UUID channelId, Message removeMessage) {
        messageService.deleteMessage(removeMessage.getMessageId());
    }

    @Override
    public Message findChannelMessageById(UUID channelId, UUID messageId) {
        Channel channel = channelRepository.findChannelById(channelId);

        if (channel == null) {
            return null;
        }

        return channel.getChannelMessages().get(messageId);
    }

    @Override
    public Map<UUID, Message> findChannelInMessageAll(UUID channelId) {
        //return messageRepository.findAllMessage();

        return messageService.getAllMessages();
    }
}
