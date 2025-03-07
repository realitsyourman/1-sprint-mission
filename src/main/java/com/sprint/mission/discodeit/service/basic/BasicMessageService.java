package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.message.MessageContentUpdateRequest;
import com.sprint.mission.discodeit.entity.message.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.message.MessageCreateResponse;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private PageResponseMapper<Message> pageResponseMapper;

  /**
   * 메세지 만들기
   */
  @Override
  public MessageDto create(MessageCreateRequest request,
      List<MultipartFile> attachments) {

    Channel findChannel = getChannel(request);
    User findUser = getUser(request);

    List<BinaryContent> files = convertToBinaryContent(attachments);

    Message message = createMessage(request, findChannel, findUser);
    message.attachFiles(files);
    messageRepository.save(message);

    return MessageMapper.toDto(message);
  }

  /**
   * 페이징해서 모든 메세지 가져오기
   */
  @Override
  public PageResponse<Message> findMessagesWithPaging(UUID channelId, Pageable pageable) {
    PageRequest pageRequest = getPageRequest(pageable);

    Page<Message> messages = messageRepository.findById(channelId, pageRequest);

    return pageResponseMapper.fromPage(messages);
  }

  /**
   * 메세지 삭제
   */
  @Override
  public void remove(UUID messageId) {
    messageRepository.deleteById(messageId);
  }

  /**
   * 메세지 수정
   */
  @Override
  public MessageDto update(UUID messageId, MessageContentUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(MessageNotFoundException::new);

    message.updateContent(request.newContent());

    return MessageMapper.toDto(message);
  }

  /**
   * pageRequest 얻기
   */
  private static PageRequest getPageRequest(Pageable pageable) {
    return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        pageable.getSort());
  }

  /**
   * DTO로 변환
   */
  private static MessageCreateResponse convertMessageCreateResponse(List<BinaryContent> files,
      Message message) {
    List<BinaryContentDto> binaryContentDtos = files.stream()
        .map(BinaryContentMapper::toDto)
        .toList();

    return MessageCreateResponse.builder()
        .id(message.getId())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .content(message.getContent())
        .channelId(message.getChannel().getId())
        .author(UserMapper.toDto(message.getAuthor()))
        .attachments(binaryContentDtos)
        .build();
  }

  /**
   * multifile을 binaryContent로
   */
  private static List<BinaryContent> convertToBinaryContent(List<MultipartFile> attachments) {
    List<BinaryContent> files = null;
    if (attachments.isEmpty()) {
      files = new ArrayList<>();
    } else {
      files = attachments.stream()
          .map(file -> new BinaryContent(file.getOriginalFilename(), file.getSize(),
              file.getContentType()))
          .toList();
    }
    return files;
  }

  private Message createMessage(MessageCreateRequest request, Channel findChannel, User findUser) {
    return Message.builder()
        .content(request.content())
        .channel(findChannel)
        .author(findUser)
        .build();
  }

  private User getUser(MessageCreateRequest request) {
    return userRepository.findById(request.authorId())
        .orElseThrow(UserNotFoundException::new);
  }

  private Channel getChannel(MessageCreateRequest request) {
    return channelRepository.findById(request.channelId())
        .orElseThrow(ChannelNotFoundException::new);
  }

  //  /**
//   * 스프린트 미션 5, 메세지 생성
//   */
//  @Override
//  public MessageAndFileCreateResponse create(MessageAndFileCreateRequest request,
//      List<MultipartFile> files) {
//
//    List<UUID> filesUUID = new ArrayList<>();
//
//    Channel findChannel = channelRepository.findChannelById(request.channelId());
//    checkChannelAndUser(request, findChannel);
//
//    Message message = Message.createMessage(request.authorId(), request.channelId(),
//        request.content());
//
//    // 파일 처리
//    if (files != null) {
//      filesUUID = uploadFiles(files, message.getId());
//    }
//
//    message.updateAttachmentIds(filesUUID);
//    messageRepository.saveMessage(message);
//
//    return new MessageAndFileCreateResponse(
//        message.getId(),
//        message.getCreatedAt(),
//        message.getUpdatedAt(),
//        message.getMessageContent(),
//        request.channelId(),
//        request.authorId(),
//        filesUUID
//    );
//  }
//
//  /**
//   * 스프린트 미션 5, 채널의 메세지 목록 조회
//   */
//  @Override
//  public List<MessageAndFileCreateResponse> findAllMessage(UUID channelId) {
//    Map<UUID, Message> messages = messageRepository.findAllMessage();
//
//    return messages.values().stream()
//        .filter(message -> message.getChannelId().equals(channelId))
//        .map(message -> new MessageAndFileCreateResponse(message.getId(),
//            message.getCreatedAt(),
//            message.getUpdatedAt(),
//            message.getMessageContent(),
//            message.getChannelId(),
//            message.getAuthorId(),
//            message.getAttachmentIds()
//        ))
//        .toList();
//  }
//
//
//  /**
//   * 스프린트 미션 5, 메세지 업데이트
//   */
//  @Override
//  public MessageUpdateResponse updateMessage(UUID messageId, MessageContentUpdateRequest request) {
//    Message findMessage = messageRepository.findMessageById(messageId);
//    if (findMessage == null) {
//      throw new MessageNotFoundException(messageId.toString());
//    }
//
//    findMessage.updateMessageContent(request.newContent());
//
//    messageRepository.saveMessage(findMessage);
//
//    return new MessageUpdateResponse(
//        findMessage.getId(),
//        findMessage.getCreatedAt(),
//        findMessage.getUpdatedAt(),
//        findMessage.getMessageContent(),
//        findMessage.getChannelId(),
//        findMessage.getAuthorId(),
//        findMessage.getAttachmentIds()
//    );
//  }
//
//  /**
//   * 스프린트 미션 5, 메세지 삭제
//   */
//  @Override
//  public void remove(UUID messageId) {
//    Message findMessage = messageRepository.findMessageById(messageId);
//    if (findMessage == null) {
//      throw new MessageNotFoundException(messageId.toString());
//    }
//
//    List<BinaryContentResponse> files = binaryContentService.findAllById(messageId);
//    for (BinaryContentResponse file : files) {
//      binaryContentService.delete(file.id());
//    }
//
//    messageRepository.removeMessageById(messageId);
//    removeMessageInChannel(messageId);
//  }
//
//
//  /**
//   * - [x] 선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다. - [x] DTO를 활용해 파라미터를 그룹화합니다.
//   */
//  @Override
//  public MessageCreateResponse create(MessageCreateRequest request,
//      MessageSendFileRequest fileRequest) throws IOException {
//    if (messageValidator.isNullParam(request.title())) {
//      throw new NullMessageTitleException();
//    }
//
//    // 사용자 검증
//    User sender = userValidator.entityValidate(userRepository.findUserByName(request.sender()));
//    User receiver = userValidator.entityValidate(userRepository.findUserByName(request.receiver()));
//    checkInChannel(request, sender, receiver);
//
//    // 메시지 생성 및 저장
//    Message message = entityFactory.createMessage(request.title(), request.content(), sender,
//        receiver);
//    messageRepository.saveMessage(message);
//
//    // 채널에 메시지 추가
//    addMessageInChannel(request, message);
//
//    // 메세지에 파일 추가
//    List<BinaryContentResponse> files = createBinaryFile(fileRequest, message);
//
//    return new MessageCreateResponse(
//        message.getId(),
//        message.getMessageTitle(),
//        message.getMessageContent(),
//        message.getMessageSendUser().getId(),
//        message.getMessageReceiveUser().getId(),
//        files
//    );
//  }
//
//
//  /**
//   * 특정 `Channel`의 Message 목록을 조회하도록 조회 조건을 추가하고, 메소드 명을 변경합니다. `findAllByChannelId`
//   */
//  @Override
//  public Map<UUID, MessageResponse> findAllMessageByChannelId(String id) {
//    UUID channelId = convertToUUID(id);
//    Channel findChannel = channelRepository.findChannelById(channelId);
//
//    if (findChannel == null) {
//      throw new ChannelNotFoundException(channelId);
//    }
//
//    Map<UUID, Message> channelMessages = findChannel.getChannelMessages();
//    return channelMessages.entrySet().stream()
//        .collect(Collectors.toMap(
//            Entry::getKey,
//            entry -> {
//              Message message = entry.getValue();
//              List<BinaryContentResponse> attachments =
//                  binaryContentService.findAllById(message.getId());
//              return convertToMessageResponseWithAttachments(message, attachments);
//            }
//        ));
//  }
//
//
//  @Override
//  public MessageResponse getMessageById(UUID messageId) {
//    Message findMessage = messageRepository.findMessageById(messageId);
//    if (findMessage == null) {
//      throw new MessageNotFoundException("메시지를 찾을 수 없습니다.");
//    }
//
//    // 첨부파일 조회
//    List<BinaryContentResponse> attachments = binaryContentService.findAllById(messageId);
//
//    return convertToMessageResponseWithAttachments(findMessage, attachments);
//  }
//
//  /**
//   * DTO를 활용해 파라미터를 그룹화합니다.
//   * <p>
//   * - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
//   */
//  @Override
//  public MessageResponse updateMessage(String messageId, MessageUpdateRequest request) {
//    UUID updateMessageId = convertToUUID(messageId);
//
//    if (messageValidator.isNullParam(request.newTitle(), request.newContent())) {
//      throw new MessageNotFoundException();
//    }
//
//    Message findMessage = messageRepository.findMessageById(updateMessageId);
//    if (findMessage == null) {
//      throw new MessageNotFoundException("메시지를 찾을 수 없습니다.");
//    }
//
//    findMessage.updateMessage(request.newTitle(), request.newContent());
//    messageRepository.saveMessage(findMessage);
//
//    Channel findChannel = getChannel(request, findMessage);
//    channelRepository.saveChannel(findChannel);
//
//    List<BinaryContentResponse> attachments = binaryContentService.findAllById(updateMessageId);
//    return convertToMessageResponseWithAttachments(findMessage, attachments);
//  }
//
//  /**
//   * 관련된 도메인도 같이 삭제합니다.
//   * <p>
//   * - 첨부파일(`BinaryContent`)
//   */
//  @Override
//  public UUID deleteMessage(String id) {
//    UUID messageId = convertToUUID(id);
//    Message findMessage = messageRepository.findMessageById(messageId);
//    if (findMessage == null) {
//      throw new MessageNotFoundException("메시지를 찾을 수 없습니다.");
//    }
//
//    List<BinaryContentResponse> files = binaryContentService.findAllById(messageId);
//    for (BinaryContentResponse file : files) {
//      binaryContentService.delete(file.id());
//    }
//
//    // 메시지 삭제
//    messageRepository.removeMessageById(messageId);
//    removeMessageInChannel(messageId);
//
//    log.info("메세지 삭제 완료: {}", messageId);
//    return messageId;
//  }
//
//  private Channel getChannel(MessageUpdateRequest request, Message findMessage) {
//    Channel findChannel = channelRepository.findChannelById(request.channelId());
//    findChannel.getChannelMessages().put(findMessage.getId(), findMessage);
//    return findChannel;
//  }
//
//
//  private List<UUID> uploadFiles(List<MultipartFile> files, UUID messageId) {
//    List<UploadBinaryContent> uploadFiles = binaryContentService.createFiles(files, messageId);
//    return uploadFiles.stream()
//        .map(f ->
//            UUID.fromString(f.getSavedFileName()
//                .substring(0, f.getSavedFileName().lastIndexOf("."))))
//        .toList();
//  }
//
//  private void checkChannelAndUser(MessageAndFileCreateRequest request, Channel findChannel) {
//    User author = userRepository.findUserById(request.authorId());
//    if (findChannel == null) {
//      throw new ChannelNotFoundException(request.channelId());
//    }
//    if (author == null) {
//      throw new ChannelAuthorNotFoundException(request.authorId().toString());
//    }
//  }
//
//  private void removeMessageInChannel(UUID messageId) {
//    Map<UUID, Channel> channels = channelRepository.findAllChannel();
//    channels.values().stream()
//        .filter(channel -> channel.getChannelMessages().containsKey(messageId))
//        .peek(channel -> channel.getChannelMessages().remove(messageId))
//        .forEach(channelRepository::saveChannel);
//  }
//
//  private void addMessageInChannel(MessageCreateRequest request, Message message) {
//    Channel findChannel = channelRepository.findChannelById(request.channelId());
//    findChannel.addMessageInChannel(message);
//    channelRepository.saveChannel(findChannel);
//  }
//
//
//  // 123e4567e89b12d3a456426614174000 이런 형식으로 들어오는 메세지 아이디 uuid 변환
//  private UUID convertToUUID(String messageId) {
//    if (messageId.length() == 36) {
//      return UUID.fromString(messageId);
//    } else if (messageId.length() != 32 || !messageId.matches(
//        "[0-9a-fA-F]+")) {
//      throw new MessageNotFoundException("messageID를 확인해주세요");
//    }
//
//    String uuid = new StringBuilder(messageId)
//        .insert(8, "-")
//        .insert(13, "-")
//        .insert(18, "-")
//        .insert(23, "-")
//        .toString();
//
//    return UUID.fromString(uuid);
//  }
//
//  private MessageResponse convertToMessageResponseWithAttachments(Message message,
//      List<BinaryContentResponse> attachments) {
//    return new MessageResponse(
//        message.getId(),
//        message.getMessageTitle(),
//        message.getMessageContent(),
//        message.getMessageSendUser().getId(),
//        message.getMessageReceiveUser().getId(),
//        attachments
//    );
//  }
//
//  private List<BinaryContentResponse> createBinaryFile(MessageSendFileRequest fileRequest,
//      Message message) throws IOException {
//    List<BinaryContentResponse> files = new ArrayList<>();
//
//    // 첨부파일 처리
//    if (fileRequest != null) {
//      try {
//        // BinaryContentRequest 생성
//        BinaryContentRequest binaryRequest = new BinaryContentRequest(
//            message.getId(),
//            message.getMessageSendUser().getId(),
//            fileRequest.getFileName(),
//            fileRequest.getFile(),
//            fileRequest.getFiles()
//        );
//
//        // 파일 업로드
//        List<UploadBinaryContent> uploadedFiles = binaryContentService.create(binaryRequest);
//
//        // 업로드된 파일들의 응답 생성
//        for (UploadBinaryContent uploadedFile : uploadedFiles) {
//          files.add(new BinaryContentResponse(
//              message.getId(),  // fileId를 messageId와 연결
//              uploadedFile.getCreatedAt(),
//              uploadedFile.getSavedFileName(),
//              uploadedFile.getSize(),
//              uploadedFile.getContentType(),
//              uploadedFile.getBytes()
//          ));
//        }
//
//      } catch (IOException e) {
//        log.error("메세지 생성 실패: {}", message.getId(), e);
//        throw e;
//      }
//    }
//    return files;
//  }
//
//  private void checkInChannel(MessageCreateRequest request, User sender, User receiver) {
//    Channel findChannel = channelRepository.findChannelById(request.channelId());
//    if (!(findChannel.isThereUserHere(sender) && findChannel.isThereUserHere(receiver))) {
//      throw new IllegalChannelException("채널에 해당 유저가 없습니다.");
//    }
//  }
}
