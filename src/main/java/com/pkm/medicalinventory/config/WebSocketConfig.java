package com.pkm.medicalinventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
	static ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	static void stopThreadPoolTaskScheduler () {
		if (null != threadPoolTaskScheduler) {
			try {
				threadPoolTaskScheduler.destroy();
				log.info("threadPoolTaskScheduler stopped");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public WebSocketConfig() {
		log.info("====================Web Socket Config=====================");
	}
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    	log.info("configureMessageBroker");
    	long heartbeatServer = 10000; // 10 seconds
        long heartbeatClient = 10000; // 10 seconds

        threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        threadPoolTaskScheduler.initialize();
//        ts.initialize();
      
        config.enableSimpleBroker("/wsResp")
        .setHeartbeatValue(new long[]{heartbeatServer, heartbeatClient})
        .setTaskScheduler(threadPoolTaskScheduler)
        ;
        config.setApplicationDestinationPrefixes("/app");
    }
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	log.info(". . . . . . . . . register Stomp Endpoints . . . . . . . . . . ");
        registry.addEndpoint("/realtime-app").setAllowedOrigins("*").withSockJS();
    }
}