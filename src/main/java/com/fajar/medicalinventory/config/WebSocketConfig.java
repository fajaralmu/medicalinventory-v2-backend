package com.fajar.medicalinventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
	Logger log = LoggerFactory.getLogger(WebSocketConfig.class);
	public WebSocketConfig() {
		log.info("====================Web Socket Config=====================");
	}
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	log.info("configureMessageBroker");
    	long heartbeatServer = 10000; // 10 seconds
        long heartbeatClient = 10000; // 10 seconds

        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(2);
        ts.setThreadNamePrefix("wss-heartbeat-thread-");
        ts.initialize();
      //  config.enableSimpleBroker("/topic");
        config.enableSimpleBroker("/wsResp").setHeartbeatValue(new long[]{heartbeatServer, heartbeatClient})
        .setTaskScheduler(ts);
        config.setApplicationDestinationPrefixes("/app");
    }
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	log.info(". . . . . . . . . register Stomp Endpoints . . . . . . . . . . ");
      //   registry.addEndpoint("/chat");
//         registry.addEndpoint("/random").setAllowedOrigins("*").withSockJS();
//         registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
         registry.addEndpoint("/realtime-app").setAllowedOrigins("*").withSockJS();
    }
}