package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.binarycontent.dto.BinaryContentResponse;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

  List<BinaryContentResponse> findAllByIdIn(Collection<UUID> ids);
}
