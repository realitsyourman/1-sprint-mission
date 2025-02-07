package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.channel.*;
import com.sprint.mission.discodeit.entity.message.Message;
import com.sprint.mission.discodeit.entity.message.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.message.MessageSendFileRequest;
import com.sprint.mission.discodeit.entity.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.user.*;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.auth.AuthService;
import com.sprint.mission.discodeit.service.status.ReadStatusService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext ac = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = ac.getBean("basicUserService", UserService.class);
		ChannelService channelService = ac.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = ac.getBean("basicMessageService", MessageService.class);
		AuthService userAuthService = ac.getBean("userAuthService", AuthService.class);
		ReadStatusService readStatusService = ac.getBean("readStatusService", ReadStatusService.class);
		UserStateService userStateService = ac.getBean("userStateService", UserStateService.class);
		BinaryContentService binaryContentService = ac.getBean("binaryContentService", BinaryContentService.class);

		log.error(userService.toString());
		log.error(channelService.toString());
		log.error(messageService.toString());
		log.error(userAuthService.toString());
		log.error(readStatusService.toString());
		log.error(userStateService.toString());
		log.error(binaryContentService.toString());

		initializeJsonFiles();

		System.out.println("===");
		System.out.println("[유저 서비스 테스트]");
		UserCommonRequest userCommonRequest = getUserCommonRequest("userA", "userA@gmail.com", "passwordA", UserRole.ROLE_COMMON);
		UserCommonRequest userCommonRequest2 = getUserCommonRequest("userB", "userB@gmail.com", "passwordB", UserRole.ROLE_ADMIN);
		BinaryContent binaryContent = new BinaryContent(UUID.randomUUID(), "profileimg", "png");

		System.out.println("유저 생성");
		User user = userService.createUser(userCommonRequest);
		User userWithProfile = userService.createUserWithProfile(userCommonRequest2, binaryContent);
		System.out.println(user);

		System.out.println("\n유저 조회");
		UserResponse response = userService.find(user.getId());
		System.out.println(response.getUserStatus().getUserId());
		System.out.println(response.getUserName());
		System.out.println(response.getUserEmail());

		System.out.println("\n프사 있는 유저 조회");
		Map<UUID, BinaryContent> byUserId = binaryContentService.findByUserId(userWithProfile.getId());
		byUserId.values().forEach(bc -> System.out.println("fileName = " + bc.getFileName() +
				", fileType = " + bc.getFileType() +
				", filePath = " + bc.getFilePath() ));


		System.out.println("\n유저 모든 조회");
		Map<UUID, UserResponse> findAllUsers = userService.findAll();
		System.out.println(findAllUsers.values());

		System.out.println("\n유저 업데이트");
		UserCommonRequest updateRequest = new UserCommonRequest("updateName", "update@gmail.com", "updatepwd");
		User updatedUser = userService.update(user.getId(), updateRequest);
		System.out.println(updatedUser);

		System.out.println("\n유저 삭제");
		userService.deleteUser(userWithProfile.getId());
		Map<UUID, UserResponse> users = userService.findAll();
		System.out.println(users);


		System.out.println("====================");
		System.out.println("====================");


		System.out.println("\n\n===");
		System.out.println("[채널 서비스 테스트]");
		System.out.println("채널 생성");
		ChannelCommonRequest channelCommonRequest = new ChannelCommonRequest(UUID.randomUUID(), "public_ch.1", user);
		Channel publicChannel = channelService.createPublicChannel(channelCommonRequest, new HashMap<>());
		System.out.println(publicChannel);

		System.out.println("\n채널 조회");
		ChannelFindResponse findChannel = channelService.findChannelById(publicChannel.getId());
		System.out.println(findChannel);

		System.out.println("\n채널 업데이트");
		ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(publicChannel.getId(), "변경된 채널", "PUBLIC", user);
		channelService.updateChannel(channelUpdateRequest);
		ChannelFindResponse updateChannel = channelService.findChannelById(publicChannel.getId());
		System.out.println(updateChannel);

		System.out.println("\n채널 삭제");
		channelService.removeChannelById(publicChannel.getId());
		Map<UUID, ChannelFindResponse> allChannels = channelService.getAllChannels(user.getId());
		System.out.println(allChannels);

		System.out.println("\n====================");
		System.out.println("====================");



		// 메세지 서비스 테스트를 위한 채널 생성
		Channel pubCh = channelService.createPublicChannel(channelCommonRequest, new HashMap<>());

		System.out.println("\n\n===");
		System.out.println("[메세지 서비스 테스트]");
		System.out.println("메세지 생성");
		User user2 = userService.createUser(userCommonRequest2);
		MessageCreateRequest messageCreateRequest = new MessageCreateRequest("안녕하세요", "반갑습니다.", user, user2);
		MessageCreateRequest messageCreateRequest2 = new MessageCreateRequest("답장입니다.", "ㅎㅇ", user2, user);
		Message message = messageService.createMessage(messageCreateRequest);
		Message message2 = messageService.createMessage(messageCreateRequest2);
		System.out.println(message);

		System.out.println("\n메세지 조회");
		Message findMessage = messageService.getMessageById(message.getId());
		System.out.println(findMessage);

		System.out.println("\n메세지에 파일 첨부해서 생성");
		MessageCreateRequest createRequestWithFile = new MessageCreateRequest("파일 첨부", "같이하기.", user, user2);
		MessageSendFileRequest fileRequest = new MessageSendFileRequest(user.getId(), "codeit_sprint", "jar");
		Message fileInMessage = messageService.createMessage(createRequestWithFile, fileRequest);
		System.out.println(fileInMessage);
		Map<UUID, BinaryContent> userSendFile = binaryContentService.findByUserId(user.getId());
		userSendFile.values().forEach(bc -> System.out.println("fileName = " + bc.getFileName() + ", fileType = " + bc.getFileType()));

		System.out.println("\n채널 안 모든 메세지 조회");
		ChannelAddMessageRequest addRequest = new ChannelAddMessageRequest(pubCh.getId(), message);
		ChannelAddMessageRequest addRequest2 = new ChannelAddMessageRequest(pubCh.getId(), message2);
		channelService.sendMessage(addRequest);
		channelService.sendMessage(addRequest2);
		Map<UUID, Message> allMessages = messageService.findAllByChannelId(pubCh.getId());
		System.out.println(allMessages);

		System.out.println("\n메세지 업데이트");
		MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(message.getId(), "새로운 제목", "새로운 내용");
		Message updateMessage = messageService.updateMessage(messageUpdateRequest);
		System.out.println(updateMessage);

		System.out.println("\n메세지 삭제 후 전체 조회");
		messageService.deleteMessage(message.getId());
		System.out.println(messageService.findAllByChannelId(pubCh.getId()));


		System.out.println("\n====================");
		System.out.println("====================");
		System.out.println("\n\n===");
		System.out.println("[유저 Auth 서비스 테스트]");
		System.out.println("로그인 시도");
		UserLoginRequest loginRequest = new UserLoginRequest("updateName", "updatepwd");
		User loginedUser = userAuthService.login(loginRequest);
		System.out.println(loginedUser);

		System.out.println("\n잘못된 로그인 시도");
		UserLoginRequest loginRequest2 = new UserLoginRequest("adssadsad", "1251dcasdwq");
		User loginedUser2 = userAuthService.login(loginRequest2);
		System.out.println(loginedUser2);
	}

	private static UserCommonRequest getUserCommonRequest(String userName, String email, String pwd, UserRole role) {
		User user = new User(userName, email, pwd, role);
		UserCommonRequest userCommonRequest = new UserCommonRequest(user.getUserName(), user.getUserEmail(), user.getUserPassword());
		return userCommonRequest;
	}

	// 테스트 할때마다 json 초기화
	public static void initializeJsonFiles() throws IOException {
		String projectRoot = System.getProperty("user.dir");
		ObjectMapper objectMapper = new ObjectMapper();

		String[] fileNames = {
				"usersStatus.json",
				"users.json",
				"readStatus.json",
				"messages.json",
				"channels.json",
				"binaryContent.json"
		};

		for (String fileName : fileNames) {
			ObjectNode rootNode = objectMapper.createObjectNode();

			Path filePath = Paths.get(projectRoot, fileName);
			objectMapper.writerWithDefaultPrettyPrinter()
					.writeValue(filePath.toFile(), rootNode);
		}
	}
}
