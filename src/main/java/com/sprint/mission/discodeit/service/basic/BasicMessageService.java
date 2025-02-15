package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.binarycontent.UploadBinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.*;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.NullMessageTitleException;
import com.sprint.mission.discodeit.factory.BaseEntityFactory;
import com.sprint.mission.discodeit.factory.EntityFactory;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.validate.MessageServiceValidator;
import com.sprint.mission.discodeit.service.validate.ServiceValidator;
import com.sprint.mission.discodeit.service.validate.UserServiceValidator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private final BinaryContentService binaryContentService;

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
    public MessageCreateResponse createMessage(MessageCreateRequest request, MessageSendFileRequest fileRequest) throws IOException {
        if (messageValidator.isNullParam(request.title())) {
            throw new NullMessageTitleException();
        }

        // 사용자 검증
        User sender = userValidator.entityValidate(userRepository.findUserByName(request.sender()));
        User receiver = userValidator.entityValidate(userRepository.findUserByName(request.receiver()));

        // 메시지 생성 및 저장
        Message message = entityFactory.createMessage(request.title(), request.content(), sender, receiver);
        messageRepository.saveMessage(message);

        // 채널에 메시지 추가
        addMessageInChannel(request, message);

        List<BinaryContentResponse> attachments = new ArrayList<>();

        // 첨부파일 처리
        if (fileRequest != null) {
            try {
                // BinaryContentRequest 생성
                BinaryContentRequest binaryRequest = new BinaryContentRequest(
                        fileRequest.getFileName(),
                        fileRequest.getFile(),
                        fileRequest.getFiles()
                );

                // 파일 업로드
                List<UploadBinaryContent> uploadedFiles = binaryContentService.create(binaryRequest);

                // 업로드된 파일들의 응답 생성
                for (UploadBinaryContent uploadedFile : uploadedFiles) {
                    attachments.add(new BinaryContentResponse(
                            message.getId(),  // fileId를 messageId와 연결
                            uploadedFile.getUploadFileName()
                    ));
                }

                log.info("Uploaded {} files for message: {}", uploadedFiles.size(), message.getId());
            } catch (IOException e) {
                log.error("Failed to upload files for message: {}", message.getId(), e);
                throw e;
            }
        }

        return new MessageCreateResponse(
                message.getId(),
                message.getMessageTitle(),
                message.getMessageContent(),
                message.getMessageSendUser().getId(),
                message.getMessageReceiveUser().getId(),
                attachments
        );
    }


    /**
     * 특정 `Channel`의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다.
     * `findAllByChannelId`
     */
    @Override
    public Map<UUID, MessageResponse> findAllMessageByChannelId(String id) {
        UUID channelId = convertToUUID(id);
        Channel findChannel = channelRepository.findChannelById(channelId);

        if (findChannel == null) {
            throw new ChannelNotFoundException("조회하는 채널이 없습니다.");
        }

        Map<UUID, Message> channelMessages = findChannel.getChannelMessages();
        return channelMessages.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Message message = entry.getValue();
                            List<BinaryContentResponse> attachments =
                                    binaryContentService.findAllById(message.getId());
                            return convertToMessageResponseWithAttachments(message, attachments);
                        }
                ));
    }


    @Override
    public MessageResponse getMessageById(UUID messageId) {
        Message findMessage = messageRepository.findMessageById(messageId);
        if (findMessage == null) {
            throw new MessageNotFoundException("메시지를 찾을 수 없습니다.");
        }

        // 첨부파일 조회
        List<BinaryContentResponse> attachments = binaryContentService.findAllById(messageId);

        return convertToMessageResponseWithAttachments(findMessage, attachments);
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
        if (findMessage == null) {
            throw new MessageNotFoundException("메시지를 찾을 수 없습니다.");
        }

        findMessage.updateMessage(request.newTitle(), request.newContent());
        messageRepository.saveMessage(findMessage);

        Channel findChannel = getChannel(request, findMessage);
        channelRepository.saveChannel(findChannel);

        List<BinaryContentResponse> attachments = binaryContentService.findAllById(updateMessageId);
        return convertToMessageResponseWithAttachments(findMessage, attachments);
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
        if (findMessage == null) {
            throw new MessageNotFoundException("메시지를 찾을 수 없습니다.");
        }

        // 첨부파일 삭제
        List<BinaryContentResponse> attachments = binaryContentService.findAllById(messageId);
        for (BinaryContentResponse attachment : attachments) {
            binaryContentService.delete(attachment.fileId());
        }

        // 메시지 삭제
        messageRepository.removeMessageById(messageId);
        removeMessageInChannel(messageId);

        log.info("Deleted message and {} attachments: {}", attachments.size(), messageId);
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

    private void addMessageInChannel(MessageCreateRequest request, Message message) {
        Channel findChannel = channelRepository.findChannelById(request.channelId());
        findChannel.addMessageInChannel(message);
        channelRepository.saveChannel(findChannel);
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

    private MessageResponse convertToMessageResponseWithAttachments(Message message, List<BinaryContentResponse> attachments) {
        return new MessageResponse(
                message.getId(),
                message.getMessageTitle(),
                message.getMessageContent(),
                message.getMessageSendUser().getId(),
                message.getMessageReceiveUser().getId(),
                attachments
        );
    }
}
