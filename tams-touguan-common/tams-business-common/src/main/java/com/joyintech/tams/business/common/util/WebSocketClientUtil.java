package com.joyintech.tams.business.common.util;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint
public class WebSocketClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketClientUtil.class);
	
	private Session session;
	private String url;
	private WebSocketListener listener;

	public void start(String url,WebSocketListener listener) throws Exception {
		this.url = url;
		this.listener = listener;
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		container.connectToServer(this, new URI(url));
	}
	
	@OnOpen
	public void open(Session session) {
		logger.info("连接{}地址",url);
		this.session = session;
	}

	@OnMessage
	public void onMessage(String message) {
		listener.onMessage(message);
	}

	@OnClose
	public void onClose() {
		logger.info("关闭{}连接",url);
		listener.onClose();
	}

	/**
	 * 发送客户端消息到服务端
	 * 
	 * @param message 消息内容
	 */
	public void send(String message) {
		this.session.getAsyncRemote().sendText(message);
	}
	
	public void close() throws IOException {
		if (null!=this.session) {
			logger.info("主动关闭{}连接",url);
			this.session.close();
		}
	}
	
	public static interface WebSocketListener{
		
		public void onMessage(String message);
		public void onClose();
	}
	
	public static void main(String[] args) throws Exception {
		WebSocketClientUtil util = new WebSocketClientUtil();
		util.start("ws://127.0.0.1:8888/dsp/cmds",new WebSocketListener() {
			@Override
			public void onMessage(String message) {
				System.out.println("收到消息:"+message);
			}

			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				
			}
		});
		util.send("101027");
		TimeUnit.HOURS.sleep(1);
	}
}
