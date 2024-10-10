package com.usth.chat_app_api.config_websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        webSocketHandlerRegistry.addHandler(myHandler(), "/api/v1/chat");

        webSocketHandlerRegistry.addHandler(myHandler(), "/api/v1/chat")
                .setAllowedOrigins("*");

    }
    @Bean(name = "myWebSocketHandler")
    public WebSocketHandler myHandler() {
        return new MyHandler();
    }
}
