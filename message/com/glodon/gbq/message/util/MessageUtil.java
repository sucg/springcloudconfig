package com.glodon.gbq.message.util;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.glodon.gbq.message.po.Message;
import com.glodon.gbq.message.service.IMessageService;

@Component
public class MessageUtil {
	@Autowired
	private IMessageService messageService;
	private static int poolSize = 64;
	static{
		ResourceBundle m_bundle = ResourceBundle.getBundle("RabbitMq");
		String sendPoolSize = m_bundle.getString("sendPoolSize");
		try{
			poolSize = Integer.parseInt(sendPoolSize);
		}catch(Exception e){
			
		}
	}
	ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

	public synchronized void sendMessages(String sClientPath) {
		try {
			this.wait(10000);
			String sAccount = sClientPath.split("/pc/")[1].trim();
			messageService.sendMessagesByLogin(sAccount, ClientTypeEnum.pc.ordinal());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void saveAndSend(Message message){
		if (null == message.getAccount() || message.getAccount().length() < 0) {
			return;
		}
		messageService.saveMessage(message);
		final Message oMessage = message;
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try{
					messageService.sendMessage(oMessage);
				}catch(Exception e){
					
				}
			}
		});
	}
	
}
