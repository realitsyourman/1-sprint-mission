package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.status.read.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  @EntityGraph(attributePaths = {"user", "channel"})
  ReadStatus findFirstByChannelOrderByLastReadAt(Channel channel);

  @EntityGraph(attributePaths = {"user.profile", "user.status", "channel"})
  @Query("select r from ReadStatus r where r.user.id = :userId")
  List<ReadStatus> findAllChannelsInUser(@Param("userId") UUID userId);

  @EntityGraph(attributePaths = {"user.profile", "user.status", "channel"})
  List<ReadStatus> findAllByChannel(Channel channel);


  // test
  List<ReadStatus> findByChannel_Id(UUID channelId);

  List<ReadStatus> findAllByUser_Id(UUID userId);
}
