/*
 * 文件名：InterfRiskRdsController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.interf.bankname.riskinterf.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.interf.bankname.riskinterf.service.InterfRiskInterfService;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;


/**
 * 接口组件Controller
 * 
 * @author 裴家俊
 * @version 1.0 2020年1月5日
 * @see InterfRiskController
 * @since 1.0
 */
@Controller
@RequestMapping("/interf/risk")
public class InterfRiskController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterfRiskInterfService<InterfRiskRdsDto> interfRiskRdsService;

	/**
	 * 实占列表
	 */
	public static final String RSK_SZ_LIST = "RSK_LIST";

    /**
     * 风险批量占有API
     * @param dataMap
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("doOccupyBatchData")
    public Map<String, Object> doOccupyBatchData(@RequestBody Map<String, Object> dataMap)
            throws IOException {
		ClientData szcliendata =new ClientData();
		logger.info("==========风险三合一服务开始==========");
		try {
			if (CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_否)) {
				logger.info("==========系统参数中未开启风险试算开关==========");
				return doSuccessMsg("系统参数中未开启风险试算开关");
			}

			logger.info("==========风险批量占有:{} start==========");
			if (!StringUtil.isNotEmpty(dataMap) || dataMap.isEmpty()) {
				return doExceptionMsg("请求报文为空");
			} else {
				String resStr = JsonUtils.object2Json(dataMap);
				logger.info("请求报文:{}", resStr);
			}

			Map<String, Object> occupyBatchDataMap = interfRiskRdsService.doOccupyBatchData(dataMap, szcliendata);
			logger.info("==========风险三合一服务结束==========");

			String resStr = JsonUtils.object2Json(occupyBatchDataMap);
			logger.info("返回报文:{}", resStr);

			return occupyBatchDataMap;

		}
        catch (Exception e) {
            logger.info("==========风险三合一服务异常结束 Exception==========\r\n {}", e.getMessage());
            if("指标状态_不通过".equals(e.getMessage())) {
				String resStr = JsonUtils.object2Json(szcliendata.getBody());
				logger.info("返回报文:{}", resStr);
				Map<String, Object> retMap = szcliendata.getBody();
				retMap.put("message", e.getMessage());
				return retMap;
			}

			return doExceptionMsg("接口异常");
        }
    }
    
    /**
     * 风险批量释放API
     * @param dataMap
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("doOccupyReleasBatchData")
    public Map<String, Object> doOccupyReleasBatchData(@RequestBody Map<String, Object> dataMap)
            throws IOException {
		logger.info("==========风险批量释放服务开始==========");
		ClientData szcliendata =new ClientData();
        try {
        	
            if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_否)) {
            	 logger.info("==========系统参数中未开启风险试算开关==========");
                 return doSuccessMsg("系统参数中未开启风险试算开关");
            }
			logger.info("==========风险批量释放API:{} start==========");
			if (!StringUtil.isNotEmpty(dataMap) || dataMap.isEmpty()) {
				return doExceptionMsg("请求报文为空");
			}else {
				String resStr = JsonUtils.object2Json(dataMap);
				logger.info("请求报文:{}", resStr);
			}
			String assetType = (String) dataMap.get("assetType");
			String eventType = (String) dataMap.get("eventType");

			if (StringUtil.isEmpty(eventType)) {
				return doExceptionMsg("请求参数eventType为空");
			}

			Map<String, Object> occupyBatchDataMap = interfRiskRdsService.doOccupyReleasBatchData(dataMap, szcliendata);
			logger.info("==========风险批量释放服务结束==========");
			String resStr = JsonUtils.object2Json(occupyBatchDataMap);
			logger.info("返回报文:{}", resStr);

			return occupyBatchDataMap;

        }
        catch (Exception e) {
            logger.info("==========风险批量释放服务异常结束 Exception==========\r\n {}", e.getMessage());
			if("指标状态_不通过".equals(e.getMessage())) {
				return doExceptionMsg((String)szcliendata.getHead().get("stdrtninfo"));
			}
            return doExceptionMsg("接口异常");
        }
    }

    /**
     * 订单触发风控审批
     * @param dataMap
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("doOrderRskApproval")
    public Map<String, Object> doOrderRskApproval(@RequestBody Map<String, Object> dataMap)
            throws IOException {
		logger.info("==========订单触发风控审批服务开始==========");
		Map<String, Object> resultMap = new HashMap<>();
        try {

            if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_否)) {
                logger.info("==========系统参数中未开启风险试算开关==========");
                return doSuccessMsg("系统参数中未开启风险试算开关");
            }
            logger.info("==========订单触发风控审批:{} start==========");
            if (!StringUtil.isNotEmpty(dataMap) || dataMap.isEmpty()) {
				return doExceptionMsg("请求报文为空");
            }else {
				String resStr = JsonUtils.object2Json(dataMap);
				logger.info("请求报文:{}", resStr);
			}
            String orderID = (String) dataMap.get("orderID");

            if (StringUtil.isEmpty(orderID)) {
				return doExceptionMsg("请求参数orderID为空");
            }

            InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();

            //调用订单触发风控审批接口
            interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_批量风险审批接口.getValue());
            ClientData szsfclientData = interfRiskRdsService.sendRiskRdsDataCommon(dataMap, interfRiskRdsDto);

			logger.info("==========订单触发风控审批服务结束==========");
            if(szsfclientData!=null) {
				String statu = (String)szsfclientData.getBody().get("flowStatus");
				//不通过，返回
				if("0".equals(statu)){
					resultMap.put("flowStatus","0");
					return resultMap;
				} else {
					resultMap.put("flowStatus","1");
					return resultMap;
				}
			} else {
				resultMap.put("flowStatus","1");
				return resultMap;
			}
        }
        catch (Exception e) {
            logger.info("==========订单触发风控审批服务异常结束 Exception==========\r\n {}", e.getMessage());
			resultMap.put("flowStatus","1");
			return resultMap;
        }
    }

	/**
	 * 风险批量释放预占API
	 * @param dataMap
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("doPreOccupyReleasBatchData")
	public Map<String, Object> doPreOccupyReleasBatchData(@RequestBody Map<String, Object> dataMap)
			throws IOException {
		logger.info("==========风险批量释放预占服务开始==========");
		ClientData szcliendata =new ClientData();
		try {

			if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_否)) {
				logger.info("==========系统参数中未开启风险试算开关==========");
				return doSuccessMsg("系统参数中未开启风险试算开关");
			}
			logger.info("==========风险批量释放预占API:{} start==========");
			if (!StringUtil.isNotEmpty(dataMap) || dataMap.isEmpty()) {
				return doExceptionMsg("请求报文为空");
			}else {
				String resStr = JsonUtils.object2Json(dataMap);
				logger.info("请求报文:{}", resStr);
			}


			Map<String, Object> occupyBatchDataMap = interfRiskRdsService.doPreOccupyReleasBatchData(dataMap, szcliendata);
			logger.info("==========风险批量释放预占服务结束==========");
			String resStr = JsonUtils.object2Json(occupyBatchDataMap);
			logger.info("返回报文:{}", resStr);

			return occupyBatchDataMap;

		}
		catch (Exception e) {
			logger.info("==========风险批量释放预占服务异常结束 Exception==========\r\n {}", e.getMessage());
			if("指标状态_不通过".equals(e.getMessage())) {
				return doExceptionMsg((String)szcliendata.getHead().get("stdrtninfo"));
			}
			return doExceptionMsg("接口异常");
		}
	}


    private Map<String, Object> doExceptionMsg(String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("rskStatus","9");
		resultMap.put("message",msg);
		logger.info("==========返回报文：rskStatus：" +resultMap.get("rskStatus")+";message: "+resultMap.get("message"));
		return resultMap;
	}

	private Map<String, Object> doSuccessMsg(String msg) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("rskStatus","0");
		resultMap.put("message","成功");
		logger.info("==========返回报文：rskStatus：" +resultMap.get("rskStatus")+";message: "+resultMap.get("message"));
		return resultMap;
	}

}
