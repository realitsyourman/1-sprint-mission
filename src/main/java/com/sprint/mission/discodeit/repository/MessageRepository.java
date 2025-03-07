package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("select m.author from Message m join m.channel where m.channel = :channel")
  List<User> findUserByChannel(@Param("channel") Channel channel);

  Page<Message> findById(UUID id, Pageable pageable);

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);
}
