package com.glodon.gbq.message.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;

import com.alibaba.fastjson.JSON;
import com.glodon.common.cache.CacheServiceManager;
import com.glodon.common.cache.ICacheService;
import com.glodon.gbq.message.po.MessageCacheVO;
import com.glodon.gbq.message.util.Constants;
import com.glodon.gbq.message.util.MessageUtil;

public class MyChannelInterceptors implements ChannelInterceptor {
	public static final String  separator = "@@@";
	public static final String  KEY = "IP_";
	public static final String  INNER_IP = "INNER_IP_";
	@Autowired
	private MessageUtil messageUtil;
	ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		Map<String, Object> headersMap = message.getHeaders();
		StompCommand oStompCommand = (StompCommand) headersMap.get("stompCommand");
		String sSessionID = Constants.C_SERVER_TAG + "_" + (String) headersMap.get("simpSessionId");
		Map<String, Object> simpSessionAttributes = (Map<String, Object>)headersMap.get("simpSessionAttributes");
		Map<String, Object> nativeHeadersMap = (Map<String, Object>) headersMap.get("nativeHeaders");
		ICacheService iSessionCacheService = CacheServiceManager.getCacheServiceByName("SessionCache");
		ICacheService iMessageCacheService = CacheServiceManager.getCacheServiceByName("MessageCache");
		// 连接的时候，往缓存里面存入信息
		if (StompCommand.CONNECT.equals(oStompCommand)) {
			String sToken;
			// 连接里面是有Token信息的，这个Token信息就是用户登录时候的Token
			List<String> tokenList = (List<String>) nativeHeadersMap.get("token");
			List<String> sInnerIpaddrs = (List<String>) nativeHeadersMap.get("local-inner-ipaddr");
			if(null != sInnerIpaddrs && sInnerIpaddrs.size() > 0){
				String sIP = (String) simpSessionAttributes.get(CustomHandshakeHandler.SIP);
				String sInnerIP = sInnerIpaddrs.get(0);
				//放入缓存，建立关系
				iMessageCacheService.set(KEY + sSessionID, sIP+separator+sInnerIP, 604800);
				iMessageCacheService.addSet(INNER_IP+sIP, sInnerIP);
			}
			if (tokenList != null && tokenList.size() > 0) {
				sToken = tokenList.get(0);
				String json = JSON.toJSONString(iSessionCacheService.get(sToken));
				Map<String,Object> sessionInfo = (Map<String,Object>)JSON.parse(json);
				// 小缓存写入信息sessionid-MessageCacheVo
				MessageCacheVO cacheVO = new MessageCacheVO();
				cacheVO.setSessionID(sSessionID);
				cacheVO.setAccount((String)sessionInfo.get("account"));
				cacheVO.setToken(sToken);
				Constants.sessionIDMessageCacheVoMap.put(sSessionID, cacheVO);
				// 大缓存里面写入信息account-sessionid, 这个超时时间设置为1周
				iMessageCacheService.set((String)sessionInfo.get("account"), sSessionID, 604800);
			}
		}

		// 订阅信息
		if (StompCommand.SUBSCRIBE.equals(oStompCommand)) {
			final String sClientPath = (String) headersMap.get("simpDestination");
			// 给MessageCacheVo中的clientPath赋值
			MessageCacheVO cacheVO = Constants.sessionIDMessageCacheVoMap.get(sSessionID);
			if (null == cacheVO) {
				return message;
			}
			cacheVO.setClientPath(sClientPath);
			Constants.sessionIDMessageCacheVoMap.put(sSessionID, cacheVO);
			Constants.pathSessionIDMap.put(sClientPath, sSessionID);
			// 开启异步方法，延时10秒
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					messageUtil.sendMessages(sClientPath);
				}
			});
		}

		// 断开连接
		if (StompCommand.DISCONNECT.equals(oStompCommand)) {
			String sIP_innerIP = iMessageCacheService.get(KEY + sSessionID);
			if(null != sIP_innerIP){
				String[] oStrs = sIP_innerIP.split(separator);
				String sIP = oStrs[0];
				String sinnerIP = oStrs[1];
				iMessageCacheService.removeFromSet(sIP, sinnerIP);
				iMessageCacheService.delete(KEY + sSessionID);
			}
			MessageCacheVO cacheVO = Constants.sessionIDMessageCacheVoMap.get(sSessionID);
			if (null != cacheVO) {
				iMessageCacheService.delete(cacheVO.getAccount());
				Constants.pathSessionIDMap.remove(cacheVO.getClientPath());
				Constants.sessionIDMessageCacheVoMap.remove(sSessionID);
			}
		}
		return message;
	}

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
	}

	@Override
	public boolean preReceive(MessageChannel channel) {
		return true;
	}

	@Override
	public Message<?> postReceive(Message<?> message, MessageChannel channel) {
		return message;
	}
}
