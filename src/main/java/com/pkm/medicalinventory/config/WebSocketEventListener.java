package com.pkm.medicalinventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {
	
	Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		log.info("Received a new web socket connection");
		log.info("message: {}", event.getMessage());
		log.info("source : {}", event.getSource());
	}

}
