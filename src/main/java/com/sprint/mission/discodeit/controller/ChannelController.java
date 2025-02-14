package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ch")
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
    public UUID deleteChannel(@PathVariable("channelName") String channelName) {
        return channelService.removeChannelByName(channelName);
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.GET)
    public Map<UUID, ChannelListResponse> getAllChannelOfUser(@PathVariable("userName") String userName) {
        return channelService.getAllChannelsOfUser(userName);
    }
}
