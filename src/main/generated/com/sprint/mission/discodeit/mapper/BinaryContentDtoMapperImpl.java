package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-27T10:14:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class BinaryContentDtoMapperImpl implements BinaryContentDtoMapper {

    @Override
    public BinaryContentDto toDto(BinaryContent binaryContent) {
        if ( binaryContent == null ) {
            return null;
        }

        BinaryContentDto.BinaryContentDtoBuilder binaryContentDto = BinaryContentDto.builder();

        binaryContentDto.id( binaryContent.getId() );
        binaryContentDto.fileName( binaryContent.getFileName() );
        binaryContentDto.size( binaryContent.getSize() );
        binaryContentDto.contentType( binaryContent.getContentType() );

        return binaryContentDto.build();
    }
}
