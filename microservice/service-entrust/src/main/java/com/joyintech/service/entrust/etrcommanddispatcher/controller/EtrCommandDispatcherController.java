/*
 * 文件名：EtrCommandDispatcherController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：tanxuan
 * 修改时间：2020年5月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrcommanddispatcher.controller;

import com.joyintech.service.entrust.etrcommanddispatcher.dto.EtrCommandDispatcherTotalDto;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 
 * 指令分发接口controller入口
 * 〈功能详细描述〉
 * @author tanxuan
 * @version 1.0 2020年5月11日
 * @see EtrCommandDispatcherController
 * @since 1.0
 */
@RestController("EtrCommandDispatcherCtr_dtx")
@RequestMapping("/EtrCommandDispatcher")
public class EtrCommandDispatcherController {
    
    /**
     * 指令信息查询service
     */
    @Autowired
    private EtrCommandDispatcherService etrCommandDispatcherService;
    
    /**
     * 指令分发信息推送service
     */
    @Autowired
    private MessageSenderService messageSenderService;
    
    /**
     * 查询指令接口
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("/fingCommandMsg")
    public void fingCommandMsg(@RequestBody Map<String, Object> param)  {
    	etrCommandDispatcherService.pushCommandMsg(param);
    } 
    
    /**
     * 推送指令接口
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("/pushCommandMsg")
    public String pushCommandMsg(@RequestBody EtrCommandDispatcherTotalDto dto)  {
    	//放弃使用mq推送消息,采用接口调用
    	//messageSenderService.pushCommandMsgToMQ(dto);
        return etrCommandDispatcherService.sendMessageByInf(dto);
    } 
   
}
