package com.glodon.gbq.message.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.glodon.gbq.message.util.Constants;

@Component
public class WebSocketMessageProcessor implements IGBQMessageProcessor {

	private final SimpMessagingTemplate operations;

	@Autowired
	public WebSocketMessageProcessor(SimpMessagingTemplate operations) {
		this.operations = operations;
	}

	/**
	 * 发消息
	 * 
	 * @param sendPath
	 * @param message
	 */
	@Override
	public boolean sendMessage(String sendPath, Object message) {
		if (Constants.pathSessionIDMap.containsKey(sendPath)) {
			try {
				operations.convertAndSend(sendPath, message);
				return true;
			} catch (MessagingException e) {
				return false;
			}
		}
		return false;
	}

}
