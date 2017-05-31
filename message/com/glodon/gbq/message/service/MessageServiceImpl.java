package com.glodon.gbq.message.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.glodon.gbq.message.dao.IMessageDao;
import com.glodon.gbq.message.po.Message;
import com.glodon.gbq.message.util.ClientTypeEnum;
import com.glodon.gbq.message.util.Constants;
import com.glodon.gbq.message.websocket.IGBQMessageProcessor;

@Service
@Transactional("messageTransactionManager")
public class MessageServiceImpl implements IMessageService {

	@Autowired
	private IMessageDao messageDao;

	@Autowired
	private IGBQMessageProcessor messageProcessor;
	
	@Override
	public void saveMessage(Message message) {
		messageDao.save(message);
	}

	@Override
	public int viewedMessage(String messageId) {
		return messageDao.viewedMessage(messageId);
	}

	@Override
	public Long getNotViewedMessageCount(String account, String clientType) {
		return messageDao.getNotViewedMessageCount(account, clientType);
	}

	@Override
	public void sendMessage(Message message) {
		// 获得发送地址
		String sDestPath = generateSendingDestPath(message.getClientType(), message.getAccount());
		List<Message> oMessagesList = new ArrayList<Message>();
		oMessagesList.add(message);
		String s_msg =  JSON.toJSONString(oMessagesList);
		String s_messageId = message.getMessageId();
		if(s_msg.length() > 0){
			if(messageProcessor.sendMessage(sDestPath, s_msg)){
				messageDao.sendedMessage(s_messageId);
			}
		}
	}

	@Override
	public List<Message> getMessages(String account, String clientType, String code, Integer start, Integer count) {
		return messageDao.getMessages(account, clientType, code, start, count);
	}

	@Override
	public void taskSentMessage() {
		List<Map<String, Object>> userlist = messageDao.getHaveNotSendedMessageUserAccounts();
		for (Map<String, Object> userMap : userlist) {
			// 不取全部的，这里取50，如果出现大量数据，这里会内存溢出
			List<Message> messagesList = messageDao.getNotSendedMessages((HashMap<String, Object>) userMap, 0, 50);
			String sAccount = (String) userMap.get("account");
			Integer nIType = (Integer) userMap.get("clientType");
			// 生成发送地址
			String sDestPath = generateSendingDestPath(nIType, sAccount);
			if (null != messagesList && messagesList.isEmpty() == false
					&& messageProcessor.sendMessage(sDestPath, JSON.toJSONString(messagesList))) {
				messageDao.updateNotSendedMessages((HashMap<String, Object>) userMap);
			}
		}
	}

	@Override
	public Long getMessageCount(String account, String clientType, String code) {
		return messageDao.getMessageCount(account, clientType, code);
	}

	@Override
	public void sendMessagesByLogin(String sAccount, int nClientType) {
		HashMap<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("account", sAccount);
		userMap.put("clientType", nClientType);
		// 不取全部的，这里取50，如果出现大量数据，这里会内存溢出
		List<Message> messagesList = messageDao.getNotSendedMessages((HashMap<String, Object>) userMap, 0, 50);
		// 生成发送地址
		String sDestPath = generateSendingDestPath(nClientType, sAccount);
		if (null != messagesList && messagesList.isEmpty() == false){
			HashMap<String, Object> userMap_f = userMap;
			try {
				if(messageProcessor.sendMessage(sDestPath, JSON.toJSONString(messagesList))) {
					messageDao.updateNotSendedMessages((HashMap<String, Object>) userMap_f);
				}
			} catch (Exception e) {
			
			}
		}
	}

	private String generateSendingDestPath(int clientType, String account) {
		// 发送的类型
		String sClientType = Constants.PC;
		if (clientType == ClientTypeEnum.web.ordinal()) {
			sClientType = Constants.WEB;
		}
		if (clientType == ClientTypeEnum.mobile.ordinal()) {
			sClientType = Constants.Mobile;
		}
		// topic或者是queue
		String broker = Constants.TOPIC_MessageBroker;
		if (Constants.pathSessionIDMap.containsKey(Constants.QUEUE_MessageBroker + sClientType + "/" + account)) {
			broker = Constants.QUEUE_MessageBroker;
		}

		return broker + sClientType + "/" + account;
	}

	@Override
	public void testMessageChannel(final String sendPath,final Object message) {
//		threadPool.execute(new Runnable() {
//			@Override
//			public void run() {
//				try {
					messageProcessor.sendMessage(sendPath, message);
//				}catch(Exception e){
//					
//				}
//			}
//		});
	}

}
