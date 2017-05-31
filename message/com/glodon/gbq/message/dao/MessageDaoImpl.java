package com.glodon.gbq.message.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.glodon.gbq.common.dao.CommonBaseDaoImpl;
import com.glodon.gbq.message.po.Message;
import com.glodon.gbq.message.util.ClientTypeEnum;

@Repository
public class MessageDaoImpl extends CommonBaseDaoImpl<Message> implements IMessageDao {
	@Autowired
	public void setSessionFactory(@Qualifier("messageSessionFactory") SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public int viewedMessage(String messageId) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("update Message set viewed = :viewed where messageId=:messageId ");
		HashMap<String, Object> oParams = new HashMap<String, Object>();
		oParams.put("viewed", true);
		oParams.put("messageId", messageId);
		return this.executeUpdate(oHql.toString(), oParams);
	}

	@Override
	public int sendedMessage(String messageId) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("update Message set sended = :sended where messageId=:messageId ");
		HashMap<String, Object> oParams = new HashMap<String, Object>();
		oParams.put("sended", true);
		oParams.put("messageId", messageId);
		return this.executeUpdate(oHql.toString(), oParams);
	}

	@Override
	public Long getNotViewedMessageCount(String account, String clientType) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("select count(messageId) from Message where account =:account and clientType=:clientType and viewed=:viewed ");
		HashMap<String, Object> oParams = new HashMap<String, Object>();
		oParams.put("account", account);
		oParams.put("viewed", false);
		oParams.put("clientType", ClientTypeEnum.valueOf(clientType).ordinal());
		return (Long) this.getSingleResult(oHql.toString(), oParams);
	}

	@Override
	public List<Message> getMessages(String account, String clientType, String code, Integer start,
			Integer count) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("from Message where account =:account and clientType=:clientType and code=:code order by sendTime desc");
		Query oQuery = this.getSession().createQuery(oHql.toString());
		oQuery.setParameter("account", account);
		oQuery.setParameter("code", code);
		oQuery.setParameter("clientType", ClientTypeEnum.valueOf(clientType).ordinal());
		oQuery.setFirstResult(start);
		oQuery.setMaxResults(count);
		return oQuery.list();
	}

	@Override
	public List<Message> getNotSendedMessages(HashMap<String, Object> oParams) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("from Message where account =:account and clientType=:clientType and sended=0 order by sendTime desc");
		return this.querylist(oHql.toString(), oParams);
	}

	@Override
	public List<Map<String, Object>> getHaveNotSendedMessageUserAccounts() {
		StringBuffer oHql = new StringBuffer();
		oHql.append("select new map(account as account,clientType as clientType) from Message where sended =:sended ");
		oHql.append(" group by account,clientType ");
		Query oQuery = this.getSession().createQuery(oHql.toString());
		oQuery.setParameter("sended", false);
		return oQuery.list();
	}

	@Override
	public int updateNotSendedMessages(HashMap<String, Object> oParams) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("update Message set sended = :sended where account =:account and clientType=:clientType ");
		oParams.put("sended", true);
		return this.executeUpdate(oHql.toString(), oParams);
	}

	@Override
	public Long getMessageCount(String account, String clientType, String code) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("select count(messageId) from Message where account =:account and clientType=:clientType and code=:code ");
		HashMap<String, Object> oParams = new HashMap<String, Object>();
		oParams.put("account", account);
		oParams.put("code", code);
		oParams.put("clientType", ClientTypeEnum.valueOf(clientType).ordinal());
		return (Long) this.getSingleResult(oHql.toString(), oParams);
	}

	@Override
	public List<Message> getNotSendedMessages(HashMap<String, Object> oParams, Integer start, Integer count) {
		StringBuffer oHql = new StringBuffer();
		oHql.append("from Message where account =:account and clientType=:clientType and sended=0 order by sendTime desc");
		Query query = getSession().createQuery(oHql.toString());
		// 设置分页内容
		query.setFirstResult(start).setMaxResults(count);
		return this.querylist(oHql.toString(), oParams);
	}
}
