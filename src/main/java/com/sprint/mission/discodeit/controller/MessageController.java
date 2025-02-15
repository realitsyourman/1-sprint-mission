package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.message.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    @RequestMapping(method = RequestMethod.POST)
    public MessageCreateResponse sendMessage(@RequestBody MessageCreateRequest request) throws IOException {
        return messageService.createMessage(request, null);
    }

    @RequestMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageCreateResponse sendMessageWithFile(
            @RequestPart(value = "request") String requestStr,
            @RequestPart(value = "file") List<MultipartFile> files) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        MessageCreateRequest request = mapper.readValue(requestStr, MessageCreateRequest.class);

        MessageSendFileRequest fileRequest = new MessageSendFileRequest(files.get(0).getOriginalFilename(), null, files);

        return messageService.createMessage(request, fileRequest);
    }

    @PutMapping("/{messageId}")
    public MessageResponse updateMessage(
            @PathVariable("messageId") String messageId,
            @Validated @RequestBody MessageUpdateRequest request) {
        return messageService.updateMessage(messageId, request);
    }

    @DeleteMapping("/{messageId}")
    public UUID deleteMessage(@PathVariable("messageId") String messageId) {
        return messageService.deleteMessage(messageId);
    }

    @GetMapping("/ch/{channelId}")
    public Map<UUID, MessageResponse> getAllMessages(@PathVariable("channelId") String channelId) {
        return messageService.findAllMessageByChannelId(channelId);
    }

    @GetMapping("/{messageId}")
    public MessageResponse getMessage(@PathVariable("messageId") String messageId) {
        return messageService.getMessageById(UUID.fromString(messageId));
    }
}