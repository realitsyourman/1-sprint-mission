package com.sprint.mission.discodeit.entity.binarycontent;

import com.sprint.mission.discodeit.entity.ImmutableBaseObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BinaryContent extends ImmutableBaseObject {
    private UUID fileId;
    private String binaryContentName;
    private UploadBinaryContent uploadFile;
    private List<UploadBinaryContent> uploadFiles;
}
