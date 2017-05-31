package com.glodon.gbq.message.util;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.glodon.gbq.message.po.MessageCacheVO;

public class Constants {
	public static final String QUEUE_MessageBroker = "/queue";
	public static final String TOPIC_MessageBroker = "/topic";
	public static final String Endpoint = "/websocket";
	public static final String PC = "/pc";
	public static final String WEB = "/web";
	public static final String Mobile = "/mobile";

	// 存放path-SessionID的对应关系
	public static ConcurrentMap<String, String> pathSessionIDMap = new ConcurrentHashMap<String, String>();
	public static final ConcurrentMap<String, MessageCacheVO> sessionIDMessageCacheVoMap = new ConcurrentHashMap<String, MessageCacheVO>();

	public static final String C_SERVER_TAG = UUID.randomUUID().toString();
	public static String C_TASK_CACHE_KEY = "TASK_CACHE_" + C_SERVER_TAG;

	public static String c_smtSystem = "000001"; // 系统消息
	public static String c_smtTask = "010101"; // 任务消息
	public static String c_smtReview = "010102"; // 审查消息
	public static String c_smtDatum = "010103"; // 资料消息
	public static String c_smtArchive = "010104"; // 归档详细

}
