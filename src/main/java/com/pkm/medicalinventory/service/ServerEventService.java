package com.pkm.medicalinventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.pkm.medicalinventory.dto.WebResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServerEventService {

	@Autowired
	private SimpMessagingTemplate webSocket;

	public boolean sendUpdateSession(Object payload) {
		webSocket.convertAndSend("/wsResp/sessions", payload);

		return true;
	}

	private int counter = 0;

	public void sendProgress(double progress, String requestId) {
		log.info("Send progress ({}): {} ({})", counter, Math.ceil(progress), requestId);
		WebResponse resp = new WebResponse().withRequestId(requestId).withPercentage(progress);
		sendProgress(resp);
		counter++;
	}

	public void sendProgress(WebResponse WebResponse) {
		webSocket.convertAndSend("/wsResp/progress/" + WebResponse.getRequestId(), WebResponse);
	}

	public void sendMessageChatToClient(WebResponse response, String requestId) {
		webSocket.convertAndSend("/wsResp/messages/" + requestId, response);
	}

	public void sendChatMessageToAdmin(WebResponse response) {
		webSocket.convertAndSend("/wsResp/adminmessages", response);
	}

}
