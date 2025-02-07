package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.message.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.message.MessageSendFileRequest;
import com.sprint.mission.discodeit.entity.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.IllegalChannelException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.NullMessageTitleException;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.validate.MessageServiceValidator;
import com.sprint.mission.discodeit.service.validate.ServiceValidator;
import com.sprint.mission.discodeit.service.validate.UserServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository; // MessageService에서 ChannelRepository를 가져다 써도 되나?

    private static final EntityFactory entityFactory = BaseEntityFactory.getInstance();


    private static final ServiceValidator<Message> messageValidator = new MessageServiceValidator();
    private static final ServiceValidator<User> userValidator = new UserServiceValidator();


    /**
     * - [x] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
     * - [x] DTO를 활용해 파라미터를 그룹화합니다.
     */
    @Override
    public Message createMessage(MessageCreateRequest request, MessageSendFileRequest... fileRequests) {
        if (messageValidator.isNullParam(request.title())) {
            throw new NullMessageTitleException();
        }

        User sendUser = userValidator.entityValidate(request.sender());
        User receiveUser = userValidator.entityValidate(request.receiver());

        if (fileRequests != null) {
            for (MessageSendFileRequest fileRequest : fileRequests) {
                binaryContentRepository.save(new BinaryContent(fileRequest.getUserId(),
                        fileRequest.getMessageId(),
                        fileRequest.getFileName(),
                        fileRequest.getFileType()));

                Message fileMessage = entityFactory.createMessage(fileRequest.getMessageId(), request.title(), request.content(), sendUser, receiveUser);
                return messageRepository.saveMessage(fileMessage);
            }
        }


        Message message = entityFactory.createMessage(request.title(), request.content(), sendUser, receiveUser);
        return messageRepository.saveMessage(message);
    }


    /**
     * 특정 `Channel`의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다.
     * `findAllByChannelId`
     */
    @Override
    public Map<UUID, Message> findAllByChannelId(UUID channelId) {
        Channel findChannel = channelRepository.findChannelById(channelId);

        if (findChannel == null) {
            throw new ChannelNotFoundException("조회하는 채널이 없습니다.");
        }


        return Optional.ofNullable(findChannel.getChannelMessages())
                .orElseThrow(MessageNotFoundException::new);
    }

    @Override
    public Message getMessageById(UUID messageId) {
        Map<UUID, Message> messages = messageRepository.findAllMessage();

        if (messages == null) {
            throw new MessageNotFoundException("찾는 메세지가 없습니다.");
        }

        return messageValidator.entityValidate(messages.get(messageId));
    }

    /**
     *DTO를 활용해 파라미터를 그룹화합니다.
     *
     * - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
     */
    @Override
    public Message updateMessage(MessageUpdateRequest request) {
        if (request.updateMessageId() == null) {
            throw new MessageNotFoundException();
        } else if (messageValidator.isNullParam(request.newTitle(), request.newContent())) {
            throw new MessageNotFoundException();
        }

        Message findMessage = getMessageById(request.updateMessageId());

        findMessage.updateMessage(request.newTitle(), request.newContent());
        messageRepository.saveMessage(findMessage);

        return findMessage;
    }

    /**
     * 관련된 도메인도 같이 삭제합니다.
     *
     * - 첨부파일(`BinaryContent`)
     */
    @Override
    public void deleteMessage(UUID messageId) {
        if (messageId == null) {
            throw new IllegalChannelException("message ID를 확인해주세요.");
        }

        Message findMessage = getMessageById(messageId);

        binaryContentRepository.removeContent(messageId);
        messageRepository.removeMessageById(findMessage.getId());

        removeMessageInChannel(messageId);
    }

    private void removeMessageInChannel(UUID messageId) {
        Map<UUID, Channel> channels = channelRepository.findAllChannel();
        channels.values().stream()
                .filter(channel -> channel.getChannelMessages().containsKey(messageId))
                .peek(channel -> channel.getChannelMessages().remove(messageId))
                .forEach(channelRepository::saveChannel);
    }
}
