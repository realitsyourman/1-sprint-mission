package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("select m.author from Message m join m.channel where m.channel = :channel")
  List<User> findUserByChannel(@Param("channel") Channel channel);

  Page<Message> findByChannel_Id(UUID channelId, Pageable pageable);

  @Override
  @NonNull
  @EntityGraph(attributePaths = {"channel", "author"})
  Optional<Message> findById(UUID uuid);

  @Query("select m from Message m"
      + " join fetch m.channel c"
      + " join fetch m.author a"
      + " where m.channel.id = :channelId"
      + " and m.createdAt > :cursor order by m.createdAt desc")
  Page<Message> findAllByChannelIdWithCursor(@Param("channelId") UUID ChannelId,
      @Param("cursor") Instant cursor, Pageable pageable);
}
