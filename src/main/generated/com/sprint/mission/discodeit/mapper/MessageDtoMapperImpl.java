package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-27T10:14:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class MessageDtoMapperImpl implements MessageDtoMapper {

    @Autowired
    private UserDtoMapper userDtoMapper;
    @Autowired
    private BinaryContentDtoMapper binaryContentDtoMapper;

    @Override
    public MessageDto toDto(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageDto.MessageDtoBuilder messageDto = MessageDto.builder();

        messageDto.channelId( messageChannelId( message ) );
        messageDto.author( userDtoMapper.toDto( message.getAuthor() ) );
        messageDto.attachments( binaryContentListToBinaryContentDtoList( message.getAttachments() ) );
        messageDto.id( message.getId() );
        messageDto.createdAt( message.getCreatedAt() );
        messageDto.updatedAt( message.getUpdatedAt() );
        messageDto.content( message.getContent() );

        return messageDto.build();
    }

    private UUID messageChannelId(Message message) {
        if ( message == null ) {
            return null;
        }
        Channel channel = message.getChannel();
        if ( channel == null ) {
            return null;
        }
        UUID id = channel.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<BinaryContentDto> binaryContentListToBinaryContentDtoList(List<BinaryContent> list) {
        if ( list == null ) {
            return null;
        }

        List<BinaryContentDto> list1 = new ArrayList<BinaryContentDto>( list.size() );
        for ( BinaryContent binaryContent : list ) {
            list1.add( binaryContentDtoMapper.toDto( binaryContent ) );
        }

        return list1;
    }
}
