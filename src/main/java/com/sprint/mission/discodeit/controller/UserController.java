package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.status.user.UserStatusReponse;
import com.sprint.mission.discodeit.entity.status.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.UserCommonRequest;
import com.sprint.mission.discodeit.entity.user.UserCommonResponse;
import com.sprint.mission.discodeit.entity.user.UserCreateWithBinaryContentResponse;
import com.sprint.mission.discodeit.entity.user.UserResponse;
import com.sprint.mission.discodeit.factory.UserRequestMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.status.UserStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStateService userStateService;
    private final UserRequestMapper userRequestMapper;

    /**
     * 프사 없는 유저 등록
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public UserCommonResponse joinUser(@Validated @RequestBody UserCommonRequest request) {
        return userService.createUser(request);
    }

    /**
     * 프로필 사진과 함께 유저 등록
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/profile", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserCreateWithBinaryContentResponse joinUserWithImg(@Validated @RequestPart("json") String request,
                                                               @RequestPart("file") MultipartFile file) throws IOException {

        // string으로 넘어온 json 형식 바디를 request dto로 변환
        UserCommonRequest userRequest = userRequestMapper.stringToJson(request, UserCommonRequest.class);

        BinaryContentRequest fileRequest = new BinaryContentRequest(file.getOriginalFilename(), file, null);

        return userService.createUserWithProfile(userRequest, fileRequest);
    }

    /**
     * 모든 유저 뽑기
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result<List<UserResponse>> findAllUsers() {
        return new Result<>(userService.findAll());
    }


    /**
     * 유저 업데이트
     */
    @RequestMapping(value = "/{userName}", method = RequestMethod.PUT)
    public UserCommonResponse updateUser(@PathVariable("userName") String userName,
                                         @Validated @RequestBody UserCommonRequest request) {

        return userService.update(userName, request);
    }

    /**
     * 유저 삭제
     */
    @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
    public Result<UUID> deleteUser(@PathVariable("userName") String userName) {
        UserCommonResponse findUser = userService.find(userName);

        return new Result<>(userService.deleteUser(findUser.id()));
    }

    /**
     * 유저 상태 업데이트
     */
    @RequestMapping(value = "/{userName}", method = RequestMethod.PATCH)
    public UserStatusReponse updateUserStatus(@PathVariable("userName") String userName,
                                              @Validated @RequestBody UserStatusUpdateRequest request) {

        return userStateService.updateByUserName(userName, request);
    }
}
