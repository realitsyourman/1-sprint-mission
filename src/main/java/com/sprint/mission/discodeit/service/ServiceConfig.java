package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    @Bean
    UserService userService(UserRepository userRepository) {
        return new BasicUserService(userRepository);
    }

    @Bean
    ChannelService channelService(ChannelRepository channelRepository, MessageService messageService) {
        return new BasicChannelService(channelRepository, messageService);
    }

    @Bean
    MessageService messageService(MessageRepository messageRepository) {
        return new BasicMessageService(messageRepository);
    }
}
