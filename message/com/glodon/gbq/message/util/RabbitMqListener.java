package com.glodon.gbq.message.util;

import java.util.Date;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.glodon.common.StrUtils;
import com.glodon.common.cache.CacheServiceManager;
import com.glodon.common.cache.ICacheService;
import com.glodon.gbq.message.po.Message;
import com.glodon.gbq.message.po.MessageVo;
import com.glodon.gbq.message.service.IMessageService;
import com.rabbitmq.client.Channel;

public class RabbitMqListener implements ChannelAwareMessageListener {

	@Autowired
	private IMessageService messageService;
	
	private ICacheService cacheService = CacheServiceManager.getCacheServiceByName("MessageCache");

	@Override
	public void onMessage(org.springframework.amqp.core.Message paramMessage, Channel paramChannel) throws Exception {
		try {
			String sMessage = new String(paramMessage.getBody(), "UTF-8");
			MessageVo oMessageVo = JSON.parseObject(sMessage, MessageVo.class);
	
			// 消息入库
			int nType = 0;
			String sClientType = oMessageVo.getClientType();
			if (StrUtils.isNotEmpty(sClientType)) {
				nType = ClientTypeEnum.valueOf(sClientType).ordinal();
			}
			Message oMessage = new Message(UUID.randomUUID().toString(), oMessageVo.getAccount(), nType, false, false,
					oMessageVo.getMessage(), 0, oMessageVo.getMessageType(), new Date());							
			messageService.saveMessage(oMessage);
			
			// 如果用户在线，写入缓存
			String sSessionID = cacheService.get(oMessageVo.getAccount());
			if (null != sSessionID) {	
				String sCacheKey = "TASK_CACHE_" + sSessionID.split("_")[0];
				cacheService.rPush(sCacheKey, JSON.toJSONString(oMessage));
			}
			
			// 确认消息
			paramChannel.basicAck(paramMessage.getMessageProperties().getDeliveryTag(), false);			
		} catch (Exception e) {			
			paramChannel.basicNack(paramMessage.getMessageProperties().getDeliveryTag(), false, true);
			e.printStackTrace();
		}

	}

}
