package com.sprint.mission.discodeit.service.status;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import com.sprint.mission.discodeit.entity.status.read.ReadStatusRequest;
import com.sprint.mission.discodeit.entity.status.read.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  /**
   * 읽음 상태 생성
   */
  public ReadStatusDto create(ReadStatusRequest request) {
    User user = userRepository.getReferenceById(request.userId());
    Channel channel = channelRepository.getReferenceById(request.channelId());

    ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
    readStatusRepository.save(readStatus);

    return ReadStatusMapper.toDto(readStatus);
  }

  /**
   * 읽음 상태 조회
   */
  public List<ReadStatusDto> findByUserId(UUID userId) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUser_Id(userId);

    return readStatuses.stream()
        .map(ReadStatusMapper::toDto)
        .toList();
  }

  /**
   * 읽음 상태 수정
   */
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(ReadStatusNotFoundException::new);

    ReadStatus modifiedStat = readStatus.changeLastReadAt(request.newLastReadAt());

    return ReadStatusMapper.toDto(modifiedStat);
  }
}
