package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST)
    public ChannelResponse createChannel(@Validated @RequestBody ChannelCreateRequest request) {
        return channelService.createChannel(request);
    }

    @RequestMapping(value = "/{channelName}", method = RequestMethod.PUT)
    public ChannelUpdateResponse updateChannel(@PathVariable("channelName") String channelName,
                                               @Validated @RequestBody ChannelUpdateRequest request) {

        return channelService.updateChannel(channelName, request);
    }

    @RequestMapping(value = "/{channelName}", method = RequestMethod.DELETE)
    public Result<UUID> deleteChannel(@PathVariable("channelName") String channelName) {
        return new Result<>(channelService.removeChannelByName(channelName));
    }

    // 유저가 가입한 모든 채널 뿌리기

    /**
     * 유저가 가입한 모들 채널 뿌리기
     */
    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    public Map<UUID, ChannelListResponse> getAllChannelOfUser(@PathVariable("userName") String userName) {
        return channelService.getAllChannelsOfUser(userName);
    }

    /**
     * 채널에 유저 초대
     */
    @RequestMapping(value = "/{channelId}/{userName}", method = RequestMethod.POST)
    public Result<ChannelAddUserResponse> addUser(@PathVariable("channelId") UUID channelId, @PathVariable("userName") String userName) {
        ChannelAddUserResponse channelAddUserResponse = channelService.addUserChannel(channelId, userName);

        return new Result<>(channelAddUserResponse);
    }
}
