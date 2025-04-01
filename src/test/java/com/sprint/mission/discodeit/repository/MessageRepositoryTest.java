package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.QuerydslConfig;
import com.sprint.mission.discodeit.entity.channel.Channel;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.logging.LogConfiguration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ActiveProfiles("test")
@Import({LogConfiguration.class, QuerydslConfig.class})
@ExtendWith(SpringExtension.class)
class MessageRepositoryTest {

  @Autowired
  MessageRepository messageRepository;

  @Autowired
  ChannelRepository channelRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TestEntityManager em;


  @Test
  @DisplayName("페이징")
  void paging() throws Exception {
    PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Direction.DESC, "createdAt"));

    User user = userRepository.findById(UUID.fromString("3a2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0"))
        .orElse(null);

    Channel channel = channelRepository.save(new Channel("ch99", "channel 99", ChannelType.PUBLIC));
    messageRepository.save(new Message("hi", channel, user, null));

    em.flush();
    em.clear();

    Slice<Message> messages = messageRepository.cursorBasedPaging(channel.getId(), Instant.now(),
        pageRequest);

    List<Message> content = messages.getContent();

    System.out.println("content = " + content);
  }
}
