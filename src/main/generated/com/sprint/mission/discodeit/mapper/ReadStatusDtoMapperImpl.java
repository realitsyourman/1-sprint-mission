package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import com.sprint.mission.discodeit.entity.user.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-27T10:14:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class ReadStatusDtoMapperImpl implements ReadStatusDtoMapper {

    @Override
    public ReadStatusDto toDto(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }

        UUID userId = null;
        UUID channelId = null;
        UUID id = null;
        Instant lastReadAt = null;

        userId = readStatusUserId( readStatus );
        channelId = readStatusChannelId( readStatus );
        id = readStatus.getId();
        lastReadAt = readStatus.getLastReadAt();

        ReadStatusDto readStatusDto = new ReadStatusDto( id, userId, channelId, lastReadAt );

        return readStatusDto;
    }

    private UUID readStatusUserId(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }
        User user = readStatus.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID readStatusChannelId(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }
        Channel channel = readStatus.getChannel();
        if ( channel == null ) {
            return null;
        }
        UUID id = channel.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
