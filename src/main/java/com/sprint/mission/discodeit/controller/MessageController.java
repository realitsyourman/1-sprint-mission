package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.message.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageCreateResponse sendMessage(@Validated @RequestBody MessageCreateRequest request) {
        return messageService.createMessage(request);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    public MessageResponse updateMessage(@PathVariable("messageId") String messageId, @Validated @RequestBody MessageUpdateRequest request) {
        return messageService.updateMessage(messageId, request);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public UUID deleteMessage(@PathVariable("messageId") String messageId) {
        return messageService.deleteMessage(messageId);
    }
}
