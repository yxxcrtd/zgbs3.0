package com.joyintech.tams.exchange.service.trade.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.service.trade.management.service.ReviewManagement;
import com.joyintech.tams.exchange.util.MasterUtil;

/**
 * @ClassName ApprovalController
 * @Description TODO
 * @Author swwan
 * @Date 2020/7/29 17:13
 * @Version 1.0
 **/
@RestController
//@PageId("TrdExchange002")
@RequestMapping("/trade/joyin")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewManagement reviewManagement;

    @RequestMapping(value = "/order/review/examine", method = RequestMethod.POST)
    public ResultBean handleOrderReviewExamine(@RequestParam(value = "eventId") String orderId,
                                               @RequestParam(value = "approveResult") String status,
                                               @RequestParam(value = "approveReason", required = false) String reason) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObjectResultBean(null);
    	}
        return reviewManagement.handleExamine(orderId, status, reason);
    }

    @RequestMapping(value = "/integratedEntrust/review/examine", method = RequestMethod.POST)
    public ResultBean handleIntegratedEntrustReviewExamine(@RequestParam(value = "eventId") String orderId,
                                                           @RequestParam(value = "approveResult") String status,
                                                           @RequestParam(value = "approveReason", required = false) String reason) {
    	if(!MasterUtil.isMaster()) {
    		return MasterUtil.postForObjectResultBean(null);
    	}
        return reviewManagement.handleExamine(orderId, status, reason);
    }


}
