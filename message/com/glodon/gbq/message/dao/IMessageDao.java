package com.glodon.gbq.message.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glodon.gbq.common.dao.ICommonBaseDao;
import com.glodon.gbq.message.po.Message;

public interface IMessageDao extends ICommonBaseDao<Message> {
	public int viewedMessage(String messageId);

	public int sendedMessage(String messageId);

	public Long getNotViewedMessageCount(String account, String clientType);

	public Long getMessageCount(String account, String clientType, String code);

	public List<Message> getMessages(String account, String clientType, String code, Integer start,
			Integer count);

	public List<Map<String, Object>> getHaveNotSendedMessageUserAccounts();

	public List<Message> getNotSendedMessages(HashMap<String, Object> params);

	public int updateNotSendedMessages(HashMap<String, Object> params);

	List<Message> getNotSendedMessages(HashMap<String, Object> oParams, Integer start, Integer count);
}
