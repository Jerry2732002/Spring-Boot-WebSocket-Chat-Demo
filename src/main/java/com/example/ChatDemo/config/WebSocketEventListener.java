package com.example.ChatDemo.config;

import com.example.ChatDemo.chat.ChatMessage;
import com.example.ChatDemo.chat.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @EventListener
    public void handleConnectSession(SessionConnectedEvent event) {
        log.info("Websocket session connected : {}", event.getMessage());
    }

    @EventListener
    public void handleDisconnectSession(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if(username != null) {
            log.info("User {} Left",username);
            ChatMessage message = ChatMessage.builder()
                    .sender(username)
                    .type(ChatType.LEFT)
                    .build();
            simpMessageSendingOperations.convertAndSend("/group/public",message);
        }
    }
}
