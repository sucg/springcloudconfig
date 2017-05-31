package com.glodon.gbq.message.websocket;

public interface IGBQMessageProcessor {
	public boolean sendMessage(String sendPath, Object message);
}
