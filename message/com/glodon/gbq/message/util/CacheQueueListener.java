package com.glodon.gbq.message.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.glodon.common.cache.CacheServiceManager;
import com.glodon.common.cache.ICacheService;
import com.glodon.gbq.message.po.Message;
import com.glodon.gbq.message.service.IMessageService;

@Component
public class CacheQueueListener {
	
	@Autowired
	private IMessageService messageService;
	
	private ICacheService cacheService = CacheServiceManager.getCacheServiceByName("MessageCache");
	
	private static int poolSize = 64;
	static{
		ResourceBundle m_bundle = ResourceBundle.getBundle("RabbitMq");
		String sendPoolSize = m_bundle.getString("sendPoolSize");
		try{
			poolSize = Integer.parseInt(sendPoolSize);
		}catch(Exception e){
			
		}
	}
	private ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);	
	
	@Scheduled(cron = "0/1 * * * * ?")
	public void sendCachedMessages() {
		while (true) {
			String sMessage = cacheService.lPop(Constants.C_TASK_CACHE_KEY);
			if (null != sMessage) {
				sendMessage(sMessage);
			} else {
				break;
			}
		}
	}	
	
	private void sendMessage(String message) {

		Message oMessage = null;
		try {
			oMessage = JSON.parseObject(message, Message.class);
			if (null == oMessage) {
				return;
			}
		} catch (Exception e) {
			return;
		}

		final Message oThreadMsg = oMessage;
		threadPool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					messageService.sendMessage(oThreadMsg);
				} catch (Exception e) {

				}
			}
		});
	}

	
}
