package com.glodon.gbq.web.restapi;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.glodon.common.StrUtils;
import com.glodon.gbq.message.po.Message;
import com.glodon.gbq.message.service.IMessageService;
import com.glodon.gbq.message.util.ClientTypeEnum;
import com.glodon.gbq.message.websocket.IGBQMessageProcessor;
import com.glodon.gbq.web.model.ResultModel;

@Controller
@RequestMapping("/api/message")
public class MessageAPI {

	@Autowired
	private IMessageService messageService;
//	@Autowired
//	private IGBQMessageProcessor messageProcessor;
//	
//	@RequestMapping(method = RequestMethod.GET, value = "/test")
//	@ResponseBody
//	public void markMessageRead() {
//		messageProcessor.sendMessage("/queue/pc/sunls", "queue");
//		messageProcessor.sendMessage("/topic/pc/sunls", "topic");
//	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/MarkMessageRead")
	@ResponseBody
	public ResultModel markMessageRead(String messageid) {
		ResultModel oModel = new ResultModel();
		messageService.viewedMessage(messageid);
		oModel.setData(true);
		oModel.setResult(true);
		return oModel;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/GetMessageCount")
	@ResponseBody
	public ResultModel GetMessageCount(String account, String clientType, String messageType) {
		ResultModel oModel = new ResultModel();
		oModel.setData(messageService.getMessageCount(account, clientType, messageType));
		oModel.setResult(true);
		return oModel;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/GetNewMessageCount")
	@ResponseBody
	public ResultModel GetNewMessageCount(String account, String clientType) {
		ResultModel oModel = new ResultModel();
		oModel.setData(messageService.getNotViewedMessageCount(account, clientType));
		oModel.setResult(true);
		return oModel;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/GetMessages")
	@ResponseBody
	public ResultModel getMessages(String account, String clientType, String messageType, Integer index, Integer count) {
		ResultModel oModel = new ResultModel();
		oModel.setData(messageService.getMessages(account, clientType, messageType, index, count));
		oModel.setResult(true);
		return oModel;
	}
	
//	@RequestMapping(method = RequestMethod.POST, value = "/SendMessage")
//	@ResponseBody
//	public ResultModel sendMessage(String account, String message, String clientType, String messageType) {
//		ResultModel oModel = new ResultModel();
//		int nType = 0;
//		if(StrUtils.isNotEmpty(clientType)) {
//			nType = ClientTypeEnum.valueOf(clientType).ordinal();
//		}
//		Message oMessage = new Message(null, account, nType, false, false, message, 0, messageType, new Date());
//		oModel.setResult(messageService.sendMessage(oMessage));
//		return oModel;
//	}	
}
