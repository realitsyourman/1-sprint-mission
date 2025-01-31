package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ac = SpringApplication.run(DiscodeitApplication.class, args);

		// EntityService 들 중에 Basic만 Bean으로 등록됨 -> 그래서 UserService.class 같이 인터페이스 이름으로 뽑을 수 있음
		UserService userService = ac.getBean("userService", UserService.class);
		ChannelService channelService = ac.getBean("channelService", ChannelService.class);
		MessageService messageService = ac.getBean("messageService", MessageService.class);

		log.error(userService.toString());
		log.error(channelService.toString());
		log.error(messageService.toString());
	}

}
