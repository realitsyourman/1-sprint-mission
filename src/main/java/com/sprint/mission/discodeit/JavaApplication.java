package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaApplication {
    public static void main(java.lang.String[] args) {
        /**
         * ChannelService 인터페이스를 구현한 JCFChannelService 클래스를 사용
         */
        List<Channel> channelList = new ArrayList<>();
        User user1 = new User("사용자1", "o134maka@gmail.com", "12346666");
        User user2 = new User("사용자2", "dsadsd@naver.com", "thisispassword");
        Channel codeit = new Channel("코드잇", user1, Arrays.asList(user1, user2));
        Channel game = new Channel("게임방", user2, Arrays.asList(user2));

        ChannelService channelService = new JCFChannelService(channelList);

        System.out.println("채널 생성");
        channelService.createChannel(codeit);
        channelService.readChannelInfo("코드잇");

        System.out.println("\n모든 채널 출력");
        channelService.createChannel(game);
        channelService.readAllChannels();

        System.out.println("\n특정 채널 수정");
        Channel updateGame = new Channel("게임 디코방", user1, Arrays.asList(user1)); // 업데이트할 채널
        channelService.updateChannel(game ,updateGame); // 업데이트
        channelService.readAllChannels();

        System.out.println("\n특정 채널 삭제");
        channelService.removeChannel(updateGame.getChannelName());
        channelService.readAllChannels();
        System.out.println("----");


        /**
         * MessageService 인터페이스를 구현한 JCFMessageService 클래스를 사용
         */
        List<Message> messageList = new ArrayList<>();
        JCFMessageService messageService = new JCFMessageService(messageList);

        Message message1 = new Message("헤이", "안녕하세요?", user1, user2);
        Message message2 = new Message("그래", "반갑다.", user2, user1);

        System.out.println("\n[메세지 전송 및 읽기]");
        messageService.sendMessage(message1);
        messageService.readMessage(message1);

        System.out.println();

        System.out.println("[모든 메세지 읽기]");
        messageService.sendMessage(message2);
        messageService.readAllMessages();

        System.out.println("\n[메세지 변경]");
        Message newMessage = new Message("메세지 바꿀건데", "바뀜?", user2, user1);
        Message changeMessage = messageService.updateMessage(message1, newMessage);
        messageService.readMessage(changeMessage);

        System.out.println("\n[메세지 삭제 전]");
        Message newAddMessage = new Message("새로운 메세지", "추가합니다", user2, user1);
        messageService.sendMessage(newAddMessage);
        messageService.readAllMessages();

        System.out.println("\n[메세지 삭제 후]");
        messageService.removeMessage(newAddMessage);
        messageService.removeMessage(message1);
        messageService.readAllMessages();
        System.out.println("----");

        /**
         * UserService 인터페이스를 구현한 JCFUserService 클래스를 사용
         */
        System.out.println("\n[유저 생성 및 단일 유저 정보 확인]");
        List<User> userList = new ArrayList<>();
        UserService userService = new JCFUserService(userList);
        userService.createUser(user1);
        userService.readUserInfo(user1);

        System.out.println("\n[모든 유저 정보 확인]");
        userService.createUser(user2);
        userService.readAllUsers();

        System.out.println("\n[유저 정보 변경]");
        User newbieUser = new User("뉴비", "newbie@mail.com", "!@newbie12345");
        userService.createUser(newbieUser);
        userService.updateUser(user1, newbieUser);
        userService.readAllUsers();

        System.out.println("\n[유저 삭제]");
        userService.deleteUser(user2);
        userService.readAllUsers();
    }
}
