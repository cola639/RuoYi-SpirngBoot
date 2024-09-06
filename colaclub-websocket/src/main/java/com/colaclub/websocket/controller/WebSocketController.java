package com.colaclub.websocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/topic")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 接收客户端发送的消息
     *
     * @param message
     */
    @MessageMapping("/topic/test")
    public void handleMessage(byte[] message) {
        String receivedMessage = new String(message);
        log.info("Received message: {}", receivedMessage);
        messagingTemplate.convertAndSend("/topic/test", receivedMessage);
    }

    /**
     * 发送消息
     *
     * @param destination
     * @param request
     */
    @PostMapping
    public void send(@RequestParam("destination") String destination, @RequestBody Object request) {
        log.info("request: '{}'", request);
        messagingTemplate.convertAndSend(destination, request);
    }

}
