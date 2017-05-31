package com.glodon.gbq.message.service;

import java.util.List;

import com.glodon.gbq.message.po.Message;

public interface IMessageService {

	public void saveMessage(Message message);

	public int viewedMessage(String messageId);

	public Long getNotViewedMessageCount(String account, String clientType);

	public Long getMessageCount(String account, String clientType, String code);

	public List<Message> getMessages(String account, String clientType, String code, Integer start, Integer count);

	public void sendMessage(Message message);

	public void taskSentMessage();

	public void sendMessagesByLogin(String sAccount, int nClientType);
	
	public void testMessageChannel(String sendPath, Object message);

}
