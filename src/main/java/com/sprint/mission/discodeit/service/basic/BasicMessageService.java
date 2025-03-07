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
  private final PageResponseMapper<Message> pageResponseMapper = new PageResponseMapper<>();

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
   * 커서 기반 페이징해서 메세지 가져오기
   */
  @Override
  public PageResponse<Message> findMessagesWithPaging(UUID channelId, Instant cursor,
      Pageable pageable) {

    PageRequest pageRequest = getPageRequest(pageable);

    Page<Message> pagedMessages = messageRepository.findAllByChannelIdWithCursor(channelId,
        cursor, pageRequest);

    return pageResponseMapper.fromPage(pagedMessages);
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
//   * 페이징해서 모든 메세지 가져오기
//   */
//  @Override
//  public PageResponse<Message> findMessagesWithPaging(UUID channelId, Pageable pageable) {
//    PageRequest pageRequest = getPageRequest(pageable);
//
//    Page<Message> messages = messageRepository.findAllByChannelId(channelId, pageRequest);
//
//    return pageResponseMapper.fromPage(messages);
//  }
}
