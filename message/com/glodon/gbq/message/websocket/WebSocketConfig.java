package com.glodon.gbq.message.websocket;

import java.util.List;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.glodon.gbq.message.util.Constants;

/**
 * WebSocket 配置文件
 * 
 * @author sunls
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	public WebSocketConfig() {
	}
	@Bean
	public CustomHandshakeHandler customHandshakeHandler() {
		return new CustomHandshakeHandler();
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(Constants.Endpoint).setHandshakeHandler(customHandshakeHandler());
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
	}

	@Bean
	public MyChannelInterceptors myChannelInterceptors() {
		return new MyChannelInterceptors();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(myChannelInterceptors());
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		// 这个地方需要进行压力测试，逐步调整，目前不改变
		registration.taskExecutor().corePoolSize(4).maxPoolSize(10);
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		return true;
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
//		registry.enableStompBrokerRelay(Constants.QUEUE_MessageBroker,Constants.TOPIC_MessageBroker).setAutoStartup(true)
//		.setRelayHost(RelayHost).setRelayPort(RelayPort).setClientLogin("guest").setClientPasscode("guest");
		// quene是1对1的情况，topic是1对多的情况，这个在于客户端订阅时候，订阅目的地的第一个选择的是哪个，服务端这里都支持
		registry.enableSimpleBroker(Constants.QUEUE_MessageBroker, Constants.TOPIC_MessageBroker);
	}
}