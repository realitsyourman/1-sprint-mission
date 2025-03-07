package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  ReadStatus findFirstByChannelOrderByLastReadAt(Channel channel);

  @Query("select r from ReadStatus r join fetch r.channel c where r.user.id = :userId")
  List<ReadStatus> findAllChannelsInUser(@Param("userId") UUID userId);

  List<ReadStatus> findAllByChannel(Channel channel);

  List<ReadStatus> findByChannel_Id(UUID channelId);

  List<ReadStatus> findAllByUser_Id(UUID userId);
}
