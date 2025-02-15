package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.status.user.UserStatusReponse;
import com.sprint.mission.discodeit.entity.status.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.UserCommonRequest;
import com.sprint.mission.discodeit.entity.user.UserCommonResponse;
import com.sprint.mission.discodeit.entity.user.UserCreateWithBinaryContentResponse;
import com.sprint.mission.discodeit.entity.user.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStateService userStateService;

    @RequestMapping(method = RequestMethod.POST)
    public UserCommonResponse joinUser(@Validated @RequestBody UserCommonRequest request) {
        return userService.createUser(request);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserCreateWithBinaryContentResponse joinUserWithImg(@Validated @RequestPart("json") String request,
                                                               @RequestPart("file") MultipartFile file) throws IOException {

        ObjectMapper mapper = new ObjectMapper(); // 싱글톤
        UserCommonRequest userRequest = mapper.readValue(request, UserCommonRequest.class);

        BinaryContentRequest fileRequest = new BinaryContentRequest(file.getOriginalFilename(), file, null);

        return userService.createUserWithProfile(userRequest, fileRequest);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map<UUID, UserResponse> findAllUsers() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.PUT)
    public UserCommonResponse updateUser(@PathVariable("userName") String userName,
                                         @Validated @RequestBody UserCommonRequest request) {

        return userService.update(userName, request);
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
    public UUID deleteUser(@PathVariable("userName") String userName) {
        UserCommonResponse findUser = userService.find(userName);

        return userService.deleteUser(findUser.id());
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.PATCH)
    public UserStatusReponse updateUserStatus(@PathVariable("userName") String userName,
                                              @Validated @RequestBody UserStatusUpdateRequest request) {

        return userStateService.updateByUserName(userName, request);
    }
}
