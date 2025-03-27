package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.status.user.UserStatus;
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
public class UserStatusDtoMapperImpl implements UserStatusDtoMapper {

    @Override
    public UserStatusDto toDto(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        Instant lastActiveAt = null;

        userId = userStatusUserId( userStatus );
        id = userStatus.getId();
        lastActiveAt = userStatus.getLastActiveAt();

        UserStatusDto userStatusDto = new UserStatusDto( id, userId, lastActiveAt );

        return userStatusDto;
    }

    private UUID userStatusUserId(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }
        User user = userStatus.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
