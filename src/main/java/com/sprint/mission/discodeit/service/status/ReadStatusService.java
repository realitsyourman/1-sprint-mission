package com.sprint.mission.discodeit.service.status;

import com.sprint.mission.discodeit.entity.status.read.*;
import com.sprint.mission.discodeit.entity.user.UserCommonResponse;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.StatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadStatusService implements StatusService<ReadStatus> {
    private final ReadStatusRepository readStatusRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    public ReadStatus create(Object request) {
        if (!(request instanceof ReadStatusCreateRequest createRequest)) {
            throw new IllegalArgumentException("ReadStatusCreateRequest 객체가 아닙니다.");
        }
        return convertToReadStatus(createRequest);
    }

    public ChannelReadStatus createChannelReadStatus(String channelId) {
        UUID uuid = convertToUUID(channelId);
        ReadStatus readStatus = find(uuid);
        if (readStatus != null) {
            return new ChannelReadStatus(readStatus.getChannelId(), readStatus.getLastReadAt());
        }

        return new ChannelReadStatus(uuid, Instant.now());
    }

    /**
     * `id`로 조회합니다.
     */
    @Override
    public ReadStatus find(UUID channelId) {
        return readStatusRepository.findByChannelId(channelId);
    }

    /**
     * userId를 조건으로 전부 조회
     */
    @Override
    public Map<UUID, UserReadStatusResponse> findAllByUserId(String userName) {
        UserCommonResponse findUser = userService.find(userName);
        UUID userId = findUser.id();
        Map<UUID, ReadStatus> allReadStatus = readStatusRepository.findAll();

        if (allReadStatus.isEmpty()) {
            throw new IllegalArgumentException("readStatus를 찾지 못했습니다.");
        }

        return allReadStatus.values().stream()
                .filter(stat -> stat.getUserId().equals(userId))
                .collect(Collectors.toMap(
                        ReadStatus::getChannelId,
                        s -> new UserReadStatusResponse(s.getChannelId(), s.getLastReadAt())
                ));
    }

    /**
     * DTO를 활용해 파라미터를 그룹화합니다.
     * - 수정 대상 객체의 `id` 파라미터, 수정할 값 파라미터
     */
    @Override
    public ReadStatus update(Object request) {
        if (!(request instanceof ReadStatusUpdateRequest)) {
            throw new IllegalArgumentException("ReadStatusUpdateRequest 객체가 아닙니다.");
        }
        ReadStatusUpdateRequest updateRequest = (ReadStatusUpdateRequest) request;
        System.out.println("updateRequest.channelId() = " + updateRequest.channelId());
        ReadStatus findByChannelId = readStatusRepository.findByChannelId(updateRequest.channelId());

        if (findByChannelId == null) {
            throw new IllegalArgumentException("채널에 대한 정보가 없습니다.");
        }

        findByChannelId.updateLastReadAt();
        return findByChannelId;
    }

    public ChannelReadStatus updateChannelReadStatus(String id) {
        UUID channelId = convertToUUID(id);

        ReadStatus update = readStatusRepository.update(channelId);

        return new ChannelReadStatus(update.getChannelId(), update.getLastReadAt());
    }


    /**
     * `id`로 삭제합니다.
     */
    @Override
    public void delete(UUID channelId) {
        ReadStatus findChannelStat = readStatusRepository.findByChannelId(channelId);

        if(findChannelStat == null) {
            throw new IllegalArgumentException("채널에 대한 정보가 없습니다.");
        }

        readStatusRepository.remove(channelId);
    }

    private ReadStatus convertToReadStatus(ReadStatusCreateRequest createRequest) {
        // channel이나 user가 존재하지 않으면 예외
        isNotExistsChannelOrUser(createRequest);

        // 같은 channel과 user와 관련된 객체가 있으면 예외
        isExistsChannelOrUser(createRequest);

        ReadStatus readStatus = ReadStatus.createReadStatus(createRequest.userId(), createRequest.channelId());
        readStatusRepository.save(readStatus);

        return readStatus;
    }

    private void isExistsChannelOrUser(ReadStatusCreateRequest createRequest) {
        ReadStatus byChannelId = readStatusRepository.findByChannelId(createRequest.channelId());
        if (byChannelId != null) {
            throw new IllegalArgumentException("생성할 수 없습니다. 이미 채널이 존재합니다.");
        }
    }

    private void isNotExistsChannelOrUser(ReadStatusCreateRequest createRequest) {
        Optional.ofNullable(channelService.findChannelById(createRequest.channelId()))
                .orElseThrow(() -> new ChannelNotFoundException("채널이 존재하지 않습니다."));
        Optional.ofNullable(userService.find(createRequest.userId()))
                .orElseThrow(() -> new UserNotFoundException("유저가 존재하지 않습니다."));
    }

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
