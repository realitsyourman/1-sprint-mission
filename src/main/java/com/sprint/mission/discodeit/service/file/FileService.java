package com.sprint.mission.discodeit.service.file;

import java.util.Map;
import java.util.UUID;

public interface FileService<T> {

    void save();

    Map<UUID, T> load();
}
