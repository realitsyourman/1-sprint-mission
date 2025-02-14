package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.*;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.NullMessageTitleException;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.validate.MessageServiceValidator;
import com.sprint.mission.discodeit.service.validate.ServiceValidator;
import com.sprint.mission.discodeit.service.validate.UserServiceValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    private static final EntityFactory entityFactory = BaseEntityFactory.getInstance();
    private static final ServiceValidator<Message> messageValidator = new MessageServiceValidator();
    private static final ServiceValidator<User> userValidator = new UserServiceValidator();

    @PostConstruct
    public void init() {
        log.error("주입된 messageRepository: {}", messageRepository.getClass().getSimpleName());
    }


    /**
     * - [x] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
     * - [x] DTO를 활용해 파라미터를 그룹화합니다.
     */
    @Override
    public MessageCreateResponse createMessage(MessageCreateRequest request, MessageSendFileRequest... fileRequests) {

        if (messageValidator.isNullParam(request.title())) {
            throw new NullMessageTitleException();
        }

        User sender = userValidator.entityValidate(userRepository.findUserByName(request.sender()));
        User receiver = userValidator.entityValidate(userRepository.findUserByName(request.receiver()));

        MessageCreateResponse fileMessage = createMessageWithFile(request, fileRequests, sender, receiver);
        if (fileMessage != null) {
            return fileMessage;
        }


        Message message = entityFactory.createMessage(request.title(), request.content(), sender, receiver);
        messageRepository.saveMessage(message);
        return new MessageCreateResponse(message.getId(), message.getMessageTitle(), message.getMessageContent(), message.getMessageSendUser().getId(), message.getMessageReceiveUser().getId());

    }


    /**
     * 특정 `Channel`의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다.
     * `findAllByChannelId`
     */
    @Override
    public Map<UUID, MessageResponse> findAllByChannelId(UUID channelId) {
        Channel findChannel = channelRepository.findChannelById(channelId);

        if (findChannel == null) {
            throw new ChannelNotFoundException("조회하는 채널이 없습니다.");
        }

        Map<UUID, Message> channelMessages = findChannel.getChannelMessages();
        return convertResponseMap(channelMessages);
    }


    @Override
    public MessageResponse getMessageById(UUID messageId) {
        Map<UUID, Message> messages = messageRepository.findAllMessage();

        if (messages == null) {
            throw new MessageNotFoundException("찾는 메세지가 없습니다.");
        }

        Message findMessage = messageValidator.entityValidate(messages.get(messageId));

        return convertToMessageResponse(findMessage);
    }

    /**
     * DTO를 활용해 파라미터를 그룹화합니다.
     * <p>
     * - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
     */
    @Override
    public MessageResponse updateMessage(String messageId, MessageUpdateRequest request) {
        UUID updateMessageId = convertToUUID(messageId);

        if (messageValidator.isNullParam(request.newTitle(), request.newContent())) {
            throw new MessageNotFoundException();
        }

        Message findMessage = messageRepository.findMessageById(updateMessageId);

        // 찾은 메세지 업데이트 후 저장
        findMessage.updateMessage(request.newTitle(), request.newContent());
        messageRepository.saveMessage(findMessage);

        // 채널 messageList에도 반영 해줘야함
        Channel findChannel = getChannel(request, findMessage);
        channelRepository.saveChannel(findChannel);

        return convertToMessageResponse(findMessage);
    }


    /**
     * 관련된 도메인도 같이 삭제합니다.
     * <p>
     * - 첨부파일(`BinaryContent`)
     */
    @Override
    public UUID deleteMessage(String id) {
        UUID messageId = convertToUUID(id);
        Message findMessage = messageRepository.findMessageById(messageId);

        binaryContentRepository.removeContent(messageId);
        messageRepository.removeMessageById(findMessage.getId());

        removeMessageInChannel(messageId);

        return messageId;
    }

    private Channel getChannel(MessageUpdateRequest request, Message findMessage) {
        Channel findChannel = channelRepository.findChannelById(request.channelId());
        findChannel.getChannelMessages().put(findMessage.getId(), findMessage);
        return findChannel;
    }

    private void removeMessageInChannel(UUID messageId) {
        Map<UUID, Channel> channels = channelRepository.findAllChannel();
        channels.values().stream()
                .filter(channel -> channel.getChannelMessages().containsKey(messageId))
                .peek(channel -> channel.getChannelMessages().remove(messageId))
                .forEach(channelRepository::saveChannel);
    }

    private static MessageResponse convertToMessageResponse(Message findMessage) {
        return new MessageResponse(findMessage.getId(),
                findMessage.getMessageTitle(),
                findMessage.getMessageContent(),
                findMessage.getMessageSendUser().getId(),
                findMessage.getMessageReceiveUser().getId()
        );
    }

    private MessageCreateResponse createMessageWithFile(MessageCreateRequest request, MessageSendFileRequest[] fileRequests, User sender, User receiver) {
        if (fileRequests != null) {
            for (MessageSendFileRequest fileRequest : fileRequests) {

                // 파일들 저장
                binaryContentRepository.save(new BinaryContent(fileRequest.getUserId(),
                        fileRequest.getMessageId(),
                        fileRequest.getFileName(),
                        fileRequest.getFileType())
                );

                Message fileMessage = new Message(fileRequest.getMessageId(), request.title(), request.content(), sender, receiver);
                messageRepository.saveMessage(fileMessage);
                return new MessageCreateResponse(fileMessage.getId(), fileMessage.getMessageTitle(), fileMessage.getMessageContent(), fileMessage.getMessageSendUser().getId(), fileMessage.getMessageReceiveUser().getId());

            }
        }
        return null;
    }

    private Map<UUID, MessageResponse> convertResponseMap(Map<UUID, Message> channelMessages) {
        return channelMessages.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> converToMessageResponse(entry.getValue())
                ));
    }

    private MessageResponse converToMessageResponse(Message message) {
        return new MessageResponse(message.getId(),
                message.getMessageTitle(),
                message.getMessageContent(),
                message.getMessageSendUser().getId(),
                message.getMessageReceiveUser().getId()
        );
    }


    // 123e4567e89b12d3a456426614174000 이런 형식으로 들어오는 메세지 아이디 uuid 변환
    private UUID convertToUUID(String messageId) {
        if (messageId == null || messageId.length() != 32 || !messageId.matches("[0-9a-fA-F]+")) {
            throw new MessageNotFoundException("messageID를 확인해주세요");
        }

        String uuid = new StringBuilder(messageId)
                .insert(8, "-")
                .insert(13, "-")
                .insert(18, "-")
                .insert(23, "-")
                .toString();

        return UUID.fromString(uuid);
    }
}
