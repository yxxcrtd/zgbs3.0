package com.joyintech.tams.exchange.service.trade.server;

import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
//import com.joyintech.tams.exchange.service.trade.util.RelayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
public class TcpServer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

//    @Autowired
//    private ServerBootstrap serverBootstrap;
//
//    @Autowired
//    private InetSocketAddress tcpPort;
//
//    @Autowired
//    @Qualifier("bossGroup")
//    private NioEventLoopGroup bossGroup;
//
//    @Autowired
//    @Qualifier("workerGroup")
//    private NioEventLoopGroup workerGroup;
//
//    private ChannelFuture serverChannelFuture;

    @Resource
    private ApplicationContext ctx;

    @Autowired
    private MsgReceiverManager msgReceiverManager;

//    @Autowired
//    private RelayUtils relayUtils;

    private volatile boolean startFlag = true;


    @PostConstruct
    public void start() {
        SpringContextUtils.setApplicationContext(ctx);
    }

//    @PreDestroy
//    public void stop() throws Exception {
//        serverChannelFuture.channel().closeFuture().sync();
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (startFlag) {
                startFlag = false;
                msgReceiverManager.init();
//                relayUtils.fillPlatDate();
//                serverChannelFuture = serverBootstrap.group(bossGroup, workerGroup).bind(tcpPort).sync();
            }
        } catch (Exception e) {
            logger.error("建立TCP连接失败： {}", e);
        }
    }
}
