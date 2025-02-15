package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.message.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface MessageService {
    /**
     * 메시지를 생성하고, 선택적으로 첨부파일을 추가합니다.
     */
    MessageCreateResponse createMessage(MessageCreateRequest createRequest, MessageSendFileRequest fileRequest) throws IOException;

    /**
     * ID로 메시지를 조회합니다. 첨부파일 정보도 포함됩니다.
     */
    MessageResponse getMessageById(UUID messageId);

    /**
     * 특정 채널의 모든 메시지를 조회합니다.
     */
    Map<UUID, MessageResponse> findAllMessageByChannelId(String channelId);

    /**
     * 메시지를 업데이트합니다.
     */
    MessageResponse updateMessage(String messageId, MessageUpdateRequest updateRequest);

    /**
     * 메시지와 관련된 첨부파일을 모두 삭제합니다.
     */
    UUID deleteMessage(String messageId);
}
