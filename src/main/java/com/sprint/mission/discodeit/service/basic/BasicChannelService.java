package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.entity.channel.create.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.channel.create.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.channel.update.ChannelModifyRequest;
import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelMapper mapper;

  /**
   * 공개채널 생성
   */
  @Override
  public ChannelDto createPublic(PublicChannelCreateRequest request) {
    Channel channel = createChannel(request);
    channelRepository.save(channel);

    List<UUID> participants = userRepository.findUsers().stream()
        .map(BaseEntity::getId)
        .toList();

    List<ReadStatus> readStatuses = createParticipantsReadStatus(participants, channel);
    List<UserDto> userDtoList = getParticipants(readStatuses);

    return convertToChannelDto(channel, userDtoList);
  }

  /**
   * 비공개 채널 생성
   */
  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = Channel.builder()
        .type(ChannelType.PRIVATE)
        .build();
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = createParticipantsReadStatus(request.participantIds(), channel);
    List<UserDto> participants = getParticipants(readStatuses);

    return convertToChannelDto(channel, participants);
  }

  /**
   * 채널 삭제
   * <p>
   * 관련된 정보 다 삭제(메세지, 파일)
   */
  @Override
  public void remove(UUID channelId) {
    channelRepository.deleteById(channelId);
  }

  /**
   * 채널 정보 수정
   */
  @Override
  public ChannelDto update(UUID channelId, ChannelModifyRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(ChannelNotFoundException::new);

    Channel modifiedChannel = channel.modifying(request.newName(), request.newDescription());

    return mapper.toDto(modifiedChannel);
  }

  /**
   * 유저가 참여 중인 모든 채널 뽑기
   */
  @Override
  public List<ChannelDto> findAllChannelsByUserId(UUID userId) {
    List<ReadStatus> readStatusList = readStatusRepository.findAllChannelsInUser(userId);

    return readStatusList.stream()
        .map(readStatus -> mapper.toDto(readStatus.getChannel()))
        .toList();
  }


  /**
   * user -> userDto
   */
  private List<UserDto> getUsers(List<ReadStatus> readStatusList) {
    return readStatusList.stream()
        .map(read -> UserMapper.toDto(read.getUser()))
        .toList();
  }

  private ChannelDto convertToChannelDto(Channel channel, List<UserDto> participants) {
    return ChannelDto.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .participants(participants)
        .lastMessageAt(Instant.now())
        .build();
  }

  // userDTO로 변환 후 저장
  private List<UserDto> getParticipants(List<ReadStatus> readStatuses) {
    List<ReadStatus> participantsReadStatus = readStatusRepository.saveAll(readStatuses);
    return getUsers(participantsReadStatus);
  }

  // 유저를 찾고, 유저의 readStatus 생성
  private List<ReadStatus> createParticipantsReadStatus(List<UUID> participantIds,
      Channel channel) {

    List<User> users = userRepository.findAllById(participantIds);
    return getReadStatuses(users, channel);
  }

  // 유저 정보로 readStatus 생성
  private List<ReadStatus> getReadStatuses(List<User> users, Channel channel) {
    return users.stream()
        .map(user -> ReadStatus.create(user, channel))
        .toList();
  }

  // 채널 객체 생성
  private static Channel createChannel(PublicChannelCreateRequest request) {
    return Channel.builder()
        .name(request.name())
        .description(request.description())
        .type(ChannelType.PUBLIC)
        .build();
  }
//
//  /**
//   * 스프린트 미션 5 심화 요구사항을 위한 PUBLIC 채널 생성 로직
//   */
//  public PublicChannelCreateResponse createPublicChannel(PublicChannelCreateRequest request) {
//    List<UUID> participantIds = findAllUsers();
//
//    Channel channel = Channel.builder()
//        .channelName(request.name())
//        .channelType(CHANNEL_TYPE_PUB)
//        .description(request.description())
//        .participantIds(participantIds)
//        .build();
//
//    channelRepository.saveChannel(channel);
//
//    return getPublicChannelCreateResponse(channel);
//  }
//
//  private List<UUID> findAllUsers() {
//    Map<UUID, User> users = userRepository.findAllUser();
//    return users.values().stream()
//        .map(user -> user.getId())
//        .toList();
//  }
//
//
//  /**
//   * 스프린트 미션 5 심화 요구사항을 위한 **PRIVATE** 채널 생성 로직
//   */
//  @Override
//  public PrivateChannelCreateResponse createPrivateChannel(PrivateChannelCreateRequest request) {
//    Channel channel = Channel.builder()
//        .channelType(CHANNEL_TYPE_PRI)
//        .build();
//    channel.getParticipantIds().addAll(request.participantIds());
//
//    request.participantIds().forEach(uuid -> {
//      readStatusRepository.save(new ReadStatus(uuid, channel.getId()));
//    });
//
//    channelRepository.saveChannel(channel);
//
//    return new PrivateChannelCreateResponse(channel.getId(), channel.getCreatedAt(),
//        channel.getUpdatedAt(), channel.getChannelType(), channel.getChannelName(),
//        channel.getDescription());
//  }
//
//  /**
//   * 스프린트 미션 5, 유저가 참여중인 channel 목록 조회
//   */
//  @Override
//  public List<ChannelFindOfUserResponse> findAllChannelsFindByUserId(UUID userId) {
//    List<ChannelFindOfUserResponse> responses = new ArrayList<>();
//    Map<UUID, Channel> channel = channelRepository.findAllChannel();
//
//    setChannelList(userId, channel, responses);
//
//    return responses;
//  }
//
//
//  /**
//   * 스프린트 미션 5, 채널 수정
//   */
//  @Override
//  public ChannelModifyResponse modifyChannel(UUID channelId, ChannelModifyRequest request) {
//    Channel findChannel = channelRepository.findChannelById(channelId);
//    if (findChannel == null) {
//      throw new ChannelNotFoundException(channelId);
//    }
//
//    if (findChannel.getChannelType().equals("PRIVATE")) {
//      throw new PrivateChannelCanNotModifyException();
//    }
//
//    findChannel.modifyChannel(request.newName(), request.newDescription());
//    channelRepository.saveChannel(findChannel);
//
//    return new ChannelModifyResponse(findChannel.getId(), findChannel.getCreatedAt(),
//        findChannel.getUpdatedAt(), findChannel.getChannelType(), findChannel.getChannelName(),
//        findChannel.getDescription());
//  }
//
//  @Override
//  public ChannelResponse createChannel(ChannelCreateRequest request) {
//    User owner = userRepository.findUserByName(request.ownerName());
//
//    if (owner == null) {
//      throw new UserNotFoundException(request.ownerName());
//    }
//
//    ChannelResponse channelCreateRequest = new ChannelResponse(
//        UUID.randomUUID(),
//        request.channelName(),
//        request.type(),
//        new UserChannelOwnerResponse(owner.getId(), owner.getUserName(), owner.getUserRole()));
//
//    return getUserChannelResponse(channelCreateRequest);
//  }
//
//
//  /**
//   * 일반 public 채널 생성
//   */
//  @Override
//  public ChannelResponse createPublicChannel(ChannelCreateRequest request,
//      Map<UUID, User> userList) {
//    if (request.channelName() == null || request.ownerName() == null) {
//      throw new IllegalChannelException();
//    }
//
//    // 채널 이름 중복 체크 추가
//    validateDuplicateChannelName(request);
//
//    // 채널 자체의 시간 정보
//    createChannelReadStatus(request);
//
//    Channel channel = setPublicChannel(request, userList);
//    channelRepository.saveChannel(channel);
//
//    return convertToChannelResponse(channel);
//  }
//
//  /**
//   * private 채널 생성
//   */
//  @Override
//  public ChannelResponse createPrivateChannel(ChannelCreateRequest privateRequest,
//      Map<UUID, User> userList) {
//    if (privateRequest.ownerName() == null || privateRequest.type().equals(CHANNEL_TYPE_PUB)) {
//      throw new IllegalChannelException();
//    }
//
//    // 채널 자체의 시간 정보
//    ReadStatus channelReadState = new ReadStatus(privateRequest.channelId(),
//        privateRequest.channelId());
//    readStatusRepository.save(channelReadState);
//
//    // 각각 유저에 대한 시간 정보
//    userList.values()
//        .forEach(entry -> {
//          ReadStatus readStatus = new ReadStatus(entry.getId(), privateRequest.channelId());
//          readStatusRepository.save(readStatus);
//        });
//
//    Channel channel = setPrivateChannel(privateRequest, userList);
//    channelRepository.saveChannel(channel);
//
//    UserChannelOwnerResponse userChannelOwnerResponse = getUserChannelOwnerResponse(privateRequest);
//
//    return new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getChannelType(),
//        userChannelOwnerResponse);
//  }
//
//  /**
//   * 채널에 유저 추가
//   */
//  @Override
//  public ChannelAddUserResponse addUserChannel(UUID channelUUID, String username) {
//    Channel findChannel = channelRepository.findChannelById(channelUUID);
//    User findUser = userRepository.findUserByName(username);
//
//    addUserInChannel(findChannel, findUser);
//
//    Channel savedChannel = channelRepository.saveChannel(findChannel);
//
//    return new ChannelAddUserResponse(savedChannel.getId(), savedChannel.getChannelName(),
//        findUser.getUserName());
//  }
//
//  /**
//   * 채널 ID로 찾기
//   */
//  @Override
//  public ChannelFindResponse findChannelById(UUID channelId) {
//    if (channelId == null) {
//      throw new IllegalChannelException("채널 아이디가 올바르지 않습니다.");
//    }
//
//    Channel findChannel = channelRepository.findChannelById(channelId);
//    if (findChannel == null) {
//      return null;
//    }
//
//    // 채널에서 가장 최근의 메세지 시간 정보 뽑기
//    Map<UUID, ReadStatus> allReadStatus = readStatusRepository.findAll();
//    ReadStatus status = findRecentMessageReadStatus(allReadStatus, findChannel);
//
//    if (findChannel.getChannelType().equals(CHANNEL_TYPE_PRI)) {
//      return toChannelPrivateResponse(findChannel, status);
//    } else {
//      return toFindResponse(findChannel, status);
//    }
//
//  }
//
//  /**
//   * @return Map<UUID, Channel>
//   * @Description: channelRepository에서 모든 채널을 가져오기
//   */
//  @Override
//  public Map<UUID, ChannelFindResponse> getAllChannels(UUID userId) {
//    // 채널을 findChannelById를 이용해 ChannelFindResponse DTO로 변경
//    Map<UUID, ChannelFindResponse> channels = convertToChannelFindResponseMap();
//
//    // DTO로 바꾼 새로운 맵을 전부 출력하는데
//    // 채널이 public 타입이면 바로 반환
//    // private 채널이면 조회한 유저가 참여한 채널만 조회
//    return getFindChannels(userId, channels);
//  }
//
//  @Override
//  public Map<UUID, ChannelListResponse> getAllChannelsOfUser(String userName) {
//    User findUser = userRepository.findUserByName(userName);
//    Map<UUID, ChannelFindResponse> allChannels = getAllChannels(findUser.getId());
//
//    // ChannelFindResponse를 ChannelListResponse로 바뀐 뒤 리턴
//    return allChannels.entrySet().stream()
//        .collect(Collectors.toMap(
//            Map.Entry::getKey,
//            entry -> new ChannelListResponse(
//                entry.getValue().getChannelId(),
//                entry.getValue().getChannelName(),
//                entry.getValue().getChannelType()
//            )
//        ));
//  }
//
//
//  private void setChannelList(UUID userId, Map<UUID, Channel> channel,
//      List<ChannelFindOfUserResponse> responses) {
//
//    responses.addAll(
//        channel.values().stream()
//            .filter(chan -> chan.getParticipantIds().contains(userId))
//            .map(ch -> new ChannelFindOfUserResponse(
//                ch.getId(),
//                ch.getChannelType(),
//                ch.getChannelName(),
//                ch.getDescription(),
//                ch.getParticipantIds()
//            ))
//            .toList()
//    );
//
//  }
//
//  /**
//   * - [ ] DTO를 활용해 파라미터를 그룹화합니다. - 수정 대상 객체의 id 파라미터, 수정할 값 파라미터 - [ ] PRIVATE 채널은 수정할 수 없습니다.
//   */
//  @Override
//  public ChannelUpdateResponse updateChannel(String channelName, ChannelUpdateRequest request) {
//    Channel channel = channelRepository.findChannelByName(channelName);
//    User owner = userRepository.findUserByName(request.ownerName());
//
//    channel.updateChannelName(request.channelName());
//    channel.setChannelType(request.channelType());
//    channel.updateOwnerUser(owner);
//
//    if (channel.getChannelType().equals(CHANNEL_TYPE_PRI)) {
//      throw new IllegalChannelException("PRIVATE 채널은 수정할 수 없습니다.");
//    }
//
//    channelRepository.saveChannel(channel);
//
//    return new ChannelUpdateResponse(channel.getId(), channel.getChannelName(),
//        channel.getChannelOwnerUser().getId(), channel.getChannelType());
//  }
//
//
//  /**
//   * 채널에 메세지 추가
//   */
//  @Override
//  public void sendMessage(ChannelAddMessageRequest request) {
//    Channel findChannel = channelRepository.findChannelById(request.channelId());
//    MessageResponse message = messageService.getMessageById(request.messageId());
//
//    if (findChannel == null || message == null) {
//      throw new ChannelNotFoundException(request.channelId());
//    }
//
//    User sender = userRepository.findUserById(message.senderId());
//    User receiver = userRepository.findUserById(message.receiverId());
//
//    Message createdMessage = new Message(message.messageId(), message.messageTitle(),
//        message.messageContent(), sender, receiver);
//    findChannel.addMessageInChannel(createdMessage);
//    channelRepository.saveChannel(findChannel);
//
//  }
//
//  /**
//   * 관련된 도메인도 같이 삭제합니다. - `Message`, `ReadStatus`
//   */
//  @Override
//  public UUID removeChannelById(UUID channelUUID) {
//    Channel findChannel = channelRepository.findChannelById(channelUUID); // 여기서 채널을 못찾음
//
//    Map<UUID, Message> messageMap = Optional.ofNullable(findChannel.getChannelMessages())
//        .orElseThrow(MessageNotFoundException::new);
//
//    // 채널 내 메시지 삭제 (MessageService 사용)
//    if (!messageMap.isEmpty()) {
//      List<UUID> messageIds = new ArrayList<>(messageMap.keySet());
//      for (UUID messageId : messageIds) {
//        messageService.deleteMessage(String.valueOf(messageId));
//      }
//    }
//
//    // 4. ReadStatus 삭제 - 새로운 리스트에 삭제할 ID들을 모은 후 한꺼번에 처리
//    Map<UUID, ReadStatus> allReadStatuses = readStatusRepository.findAll();
//
//    if (!allReadStatuses.isEmpty()) {
//      List<UUID> readStatusesToRemove = new ArrayList<>();
//
//      for (ReadStatus readStatus : allReadStatuses.values()) {
//        if (readStatus.getChannelId().equals(channelUUID)) {
//          readStatusesToRemove.add(readStatus.getChannelId());
//        }
//      }
//
//      for (UUID statusId : readStatusesToRemove) {
//        readStatusRepository.remove(statusId);
//      }
//
//    }
//
//    // 5. 채널 삭제
//    channelRepository.removeChannelById(findChannel.getId());
//
//    return channelUUID;
//  }
//
//  @Override
//  public UUID removeChannelByName(String channelName) {
//    Channel findChannel = channelRepository.findChannelByName(channelName);
//
//    removeChannelById(findChannel.getId());
//
//    return findChannel.getId();
//  }
//
//  private Channel setPublicChannel(ChannelCreateRequest request, Map<UUID, User> userList) {
//    return buildChannel(request, "PUBLIC", userList);
//  }
//
//  private Map<UUID, ChannelFindResponse> getFindChannels(UUID userId,
//      Map<UUID, ChannelFindResponse> channels) {
//    return channels.entrySet().stream()
//        .filter(entry -> {
//          ChannelFindResponse response = entry.getValue();
//
//          return checkPublicOrPrivateChannel(userId, entry, response);
//        })
//        .collect(Collectors.toMap(
//            Map.Entry::getKey,
//            Map.Entry::getValue
//        ));
//  }
//
//  /**
//   * 특정 `User`가 볼 수 있는 Channel 목록을 조회하도록 조회 조건
//   */
//  private Map<UUID, ChannelResponse> findAllByUserId(UUID userId) {
//    Map<UUID, Channel> channels = channelRepository.findAllChannel();
//
//    return channels.values().stream()
//        .filter(ch -> ch.getChannelUsers().values().stream()
//            .anyMatch(user -> user.getId().equals(userId)))
//        .collect(Collectors.toMap(
//            Channel::getId,
//            channel -> new ChannelResponse(channel.getId(), channel.getChannelName(),
//                channel.getChannelType(),
//                new UserChannelOwnerResponse(channel.getChannelOwnerUser().getId(),
//                    channel.getChannelOwnerUser().getUserName(),
//                    channel.getChannelOwnerUser().getUserRole()))
//        ));
//  }
//
//  private boolean checkPublicOrPrivateChannel(UUID userId,
//      Map.Entry<UUID, ChannelFindResponse> entry, ChannelFindResponse response) {
//    if (response.getChannelType().equals(CHANNEL_TYPE_PUB)) { // 퍼블릭 채널이면 그냥 반환
//      return true;
//    } else {
//      Channel channel = channelRepository.findChannelById(entry.getKey());
//
//      return userId.equals(channel.getChannelOwnerUser().getId()) ||
//          channel.getChannelUsers().values().stream()
//              .anyMatch(user -> user.getId().equals(userId));
//    }
//  }
//
//  private Channel setPrivateChannel(ChannelCreateRequest privateRequest, Map<UUID, User> userList) {
//    return buildChannel(privateRequest, "PRIVATE", userList);
//  }
//
//  private Map<UUID, ChannelFindResponse> convertToChannelFindResponseMap() {
//    return channelRepository.findAllChannel().values().stream()
//        .collect(Collectors.toMap(
//            entry -> entry.getId(),
//            entry -> findChannelById(entry.getId())
//        ));
//  }
//
//  private UserChannelOwnerResponse getUserChannelOwnerResponse(
//      ChannelCreateRequest privateRequest) {
//    User owner = userRepository.findUserByName(privateRequest.ownerName());
//    return new UserChannelOwnerResponse(owner.getId(), owner.getUserName(), owner.getUserRole());
//  }
//
//  private Channel buildChannel(ChannelCreateRequest privateRequest, String channelType,
//      Map<UUID, User> userList) {
//    Channel channel = new Channel(privateRequest.channelId());
//    channel.updateChannelName(privateRequest.channelName());
//    channel.updateOwnerUser(userRepository.findUserByName(privateRequest.ownerName()));
//    channel.setChannelType(channelType);
//    channel.updateChannelUsers(userList);
//    channel.getChannelOwnerUser().updateUserRole(UserRole.ROLE_ADMIN);
//    channel.setChannelMessages(new HashMap<>());
//    return channel;
//  }
//
//
//  private static ChannelFindPrivateResponse toChannelPrivateResponse(Channel findChannel,
//      ReadStatus status) {
//    List<UUID> userIdList = findChannel.getChannelUsers().values().stream()
//        .map(entry -> entry.getId())
//        .toList();
//
//    return new ChannelFindPrivateResponse(findChannel.getId(),
//        findChannel.getChannelName(),
//        findChannel.getChannelOwnerUser(),
//        findChannel.getChannelType(),
//        status,
//        userIdList);
//  }
//
//  private static ChannelResponse convertToChannelResponse(Channel channel) {
//    return new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getChannelType(),
//        new UserChannelOwnerResponse(channel.getChannelOwnerUser().getId(),
//            channel.getChannelOwnerUser().getUserName(),
//            channel.getChannelOwnerUser().getUserRole()));
//  }
//
//  private static ChannelFindResponse toFindResponse(Channel findChannel, ReadStatus status) {
//    ChannelFindResponse response = new ChannelFindResponse(findChannel.getId(),
//        findChannel.getChannelName(),
//        findChannel.getChannelOwnerUser(),
//        findChannel.getChannelType(), status);
//
//    return response;
//  }
//
//  private void createChannelReadStatus(ChannelCreateRequest request) {
//    User findUser = userRepository.findUserByName(request.ownerName());
//    ReadStatus channelReadState = ReadStatus.createReadStatus(findUser.getId(),
//        request.channelId());
//    readStatusRepository.save(channelReadState);
//  }
//
//  private void validateDuplicateChannelName(ChannelCreateRequest request) {
//    boolean isDuplicate = channelRepository.findAllChannel().values().stream()
//        .anyMatch(ch -> ch.getChannelName().equals(request.channelName()));
//
//    if (isDuplicate) {
//      throw new IllegalChannelException("중복된 채널 이름입니다.");
//    }
//  }
//
//  private static void addUserInChannel(Channel findChannel, User findUser) {
//    if (!findChannel.getChannelOwnerUser().equals(findUser)) {
//      findUser.updateUserRole(UserRole.ROLE_COMMON);
//    }
//
//    findChannel.addUser(findUser);
//  }
//
//  private static PublicChannelCreateResponse getPublicChannelCreateResponse(Channel channel) {
//    return new PublicChannelCreateResponse(channel.getId(), channel.getCreatedAt(),
//        channel.getUpdatedAt(), channel.getChannelType(), channel.getChannelName(),
//        channel.getDescription());
//  }
//
//  private ChannelResponse getUserChannelResponse(ChannelResponse request) {
//    ChannelResponse response;
//    ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(request.channelId(),
//        request.channelName(), request.owner().userName(), request.channelType());
//
//    if (request.channelType().equals("PUBLIC")) {
//      response = createPublicChannel(channelCreateRequest, new HashMap<>());
//    } else if (request.channelType().equals("PRIVATE")) {
//      response = createPrivateChannel(channelCreateRequest, new HashMap<>());
//    } else {
//      throw new IllegalChannelException();
//    }
//
//    log.warn("채널 생성 [{}][{}]", response.channelType(), response.channelName());
//
//    return response;
//  }
//
//  private static ReadStatus findRecentMessageReadStatus(Map<UUID, ReadStatus> allReadStatus,
//      Channel findChannel) {
//
//    return allReadStatus.values().stream()
//        .filter(entry -> entry.getChannelId().equals(findChannel.getId()))
//        .max(Comparator.comparing(ReadStatus::getLastReadAt))
//        .orElse(null);
//  }
}
