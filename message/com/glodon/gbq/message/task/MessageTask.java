package com.glodon.gbq.message.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.glodon.common.cache.CacheServiceManager;
import com.glodon.common.cache.ICacheService;
import com.glodon.gbq.message.po.MessageCacheVO;
import com.glodon.gbq.message.service.IMessageService;
import com.glodon.gbq.message.util.Constants;

@Component
public class MessageTask {
	@Autowired
	private IMessageService messageService;
	
	@Scheduled(cron = "0 0/15 * * * ?")
	public void taskSentMessage() {
		messageService.taskSentMessage();
	}
	
}
