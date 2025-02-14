package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.status.user.UserStatusReponse;
import com.sprint.mission.discodeit.entity.status.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.UserCommonRequest;
import com.sprint.mission.discodeit.entity.user.UserCommonResponse;
import com.sprint.mission.discodeit.entity.user.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public UserCommonResponse joinUser(@Validated @RequestBody UserCommonRequest request, BindingResult bindingResult) {
        return userService.createUser(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Map<UUID, UserResponse> findAllUsers() {
        return userService.findAll();
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.PUT)
    public UserCommonResponse updateUser(@PathVariable("userName") String userName, @Validated @RequestBody UserCommonRequest request) {
        return userService.update(userName, request);
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
    public UUID deleteUser(@PathVariable("userName") String userName) {
        UserCommonResponse findUser = userService.find(userName);

        return userService.deleteUser(findUser.id());
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.PATCH)
    public UserStatusReponse updateUserStatus(@PathVariable("userName") String userName, @Validated @RequestBody UserStatusUpdateRequest request) {
        return userStateService.updateByUserName(userName, request);
    }
}
