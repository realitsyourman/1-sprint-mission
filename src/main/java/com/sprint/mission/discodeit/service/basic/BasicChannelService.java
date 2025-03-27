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
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCanNotModifyException;
import com.sprint.mission.discodeit.mapper.UserDtoMapper;
import com.sprint.mission.discodeit.mapper.UserDtoMapperImpl;
import com.sprint.mission.discodeit.mapper.entitymapper.ChannelMapper;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
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

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelCanNotModifyException(channelId.toString());
    }

    Channel modifiedChannel = channel.modifying(request.newName(), request.newDescription());

    return mapper.toDto(modifiedChannel);
  }

  /**
   * 유저가 참여 중인 모든 채널 뽑기
   */
  @Override
  @Transactional(readOnly = true)
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

    UserDtoMapper userMapper = new UserDtoMapperImpl();

    return readStatusList.stream()
        .map(read -> userMapper.toDto(read.getUser()))
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

    List<User> users = userRepository.findByIdIn(participantIds);
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
}
