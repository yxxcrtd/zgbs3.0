/*
 * 文件名：InterfRiskRdsController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.riskrds.controller;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 接口组件Controller
 * 
 * @author 裴家俊
 * @version 1.0 2020年1月5日
 * @see InterfRiskRdsController
 * @since 1.0
 */
@Controller
@RequestMapping("/riskrds/interf")
public class InterfRiskRdsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    // add by zhangrui1 批量试算 Start
    /**
     * 批量风险试算
     * 
     * @param dataList 批量数据
     * @param eventType 事件类型
     * @param assetType 资产类型
     * @return 试算结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("batchRiskTrialRds/{eventType}/{assetType}")
    public String batchRiskTrialRds(@RequestBody List<Map<String, Object>> dataList,
                                    @PathVariable("eventType") String eventType,
                                    @PathVariable("assetType") String assetType)
        throws Exception {
        if (CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"),
            BussinessDictCodeListRds.是否_是)) {
            logger.info("==========批量试算接口<RSK011>事件类型:{} start==========", eventType);
            // 报文体为空\事件类型为空
            if (CollectionUtils.isEmpty(dataList) || StringUtil.isEmpty(eventType)) {
                throw new NullPointerException("请求报文或者请求参数为空");
            }
            InterfRiskRdsDto infRdsDto = new InterfRiskRdsDto();
            infRdsDto.setEventType(eventType);
            infRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue());
            infRdsDto.setAssetType(assetType);
            ClientData rsp = interfRiskRdsService.sendRiskRdsListData(infRdsDto, dataList, null);
            logger.info("==========批量试算接口<RSK011>事件类型:{} end==========", eventType);
            return JsonUtils.object2Json(rsp);
        }
        else {
            logger.info("==========未启动风险试算==========");
            return null;
        }
    }
    // add by zhangrui1 批量试算 End

    /**
     * 风险接口统一调用入口(画面端)
     * @param dataMap
     * @param eventType  事件类型 0精确委托 1交易 2 拆分 3模糊委托
     * @param infCode    接口代码 RSK001预占 RSK002风险试算 RSK003实占
     * @param assetType  资产大类(对应投管为数据字典平安资产类型)
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("doInfData/{eventType}/{infCode}/{assetType}")
    public String doInfData(@RequestBody Map<String, Object> dataMap, @PathVariable("eventType") String eventType, @PathVariable("infCode") String infCode, @PathVariable("assetType") String assetType)
        throws IOException {
        try {
            if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
                logger.info("==========前端交易或委托类转换接口:{} start==========", eventType + infCode);
                if (!StringUtil.isNotEmpty(dataMap) || dataMap.isEmpty()) {
                    throw new IllegalArgumentException("请求报文为空");
                }
                if (StringUtil.isEmpty(eventType) || StringUtil.isEmpty(infCode)/*||StringUtil.isEmpty(assetType)*/) {
                    throw new IllegalArgumentException("请求参数为空");
                }
                InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
                interfRiskRdsDto.setEventType(eventType);
                interfRiskRdsDto.setInfCode(infCode);
                interfRiskRdsDto.setAssetType(assetType);
                //中标结果维护风险试算特殊处理
                String busiType = StringUtil.isNotEmpty(dataMap.get("busiType"))?String.valueOf(dataMap.get("busiType")):"";
                if (CommonUtil.isType(busiType, BussinessDictCodeList.共同业务类型_中标结果确认)
                        || CommonUtil.isType(busiType, BussinessDictCodeList.共同业务类型_中标结果维护)) {
                    interfRiskRdsDto.setEventStatus(busiType);
                }

                logger.info("==========调用共通方法sendRiskRdsDataCommon==========");
                return JsonUtils.object2Json(interfRiskRdsService.sendRiskRdsDataCommon(dataMap, interfRiskRdsDto));
            }else {
                logger.info("==========系统参数中未开启风险试算开关==========");
                return null;
            }
        }
        catch (Exception e) {
            logger.info("==========交易或委托类转换接口 Exception==========\r\n {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 风险接口统一调用入口(批量画面端)
     * @param dataMap
     * @param eventType  事件类型 0精确委托 1交易 2 拆分 3模糊委托
     * @param infCode    接口代码 RSK001预占 RSK002风险试算 RSK003实占
     * @param assetType  资产大类(对应投管为数据字典平安资产类型)
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("doInfBatchData/{eventType}/{infCode}/{assetType}")
    public List<String> doInfBatchData(@RequestBody Map<String, Object> dataMap, @PathVariable("eventType") String eventType, @PathVariable("infCode") String infCode, @PathVariable("assetType") String assetType)
            throws IOException {
        try {
            if(CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
                logger.info("==========前端交易或委托类转换接口:{} start==========", eventType + infCode);
                if (!StringUtil.isNotEmpty(dataMap) || dataMap.isEmpty()) {
                    throw new IllegalArgumentException("请求报文为空");
                }
                if (StringUtil.isEmpty(eventType) || StringUtil.isEmpty(infCode)) {
                    throw new IllegalArgumentException("请求参数为空");
                }
                InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
                interfRiskRdsDto.setEventType(eventType);
                interfRiskRdsDto.setInfCode(infCode);
                interfRiskRdsDto.setAssetType(assetType);
                logger.info("==========调用共通方法sendRiskRdsDataCommon==========");
                List<String> retList=new ArrayList<>();
                List<Map<String, Object>> listMap=interfRiskRdsService.getBatchAllocation(dataMap,interfRiskRdsDto);
                if (StringUtil.isEmptyList(listMap)){
                    logger.info("==========风险试算参数为空==========");
                    return null;
                }
                // 风险批量试算需要调用 RSK011：BussinessDictCodeListRds.接口系统ID_风险试算批量接口，组合包需要累加
                if (listMap.size() > 1){
                    interfRiskRdsDto.setEventType(eventType);
                    interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue());
                    interfRiskRdsDto.setAssetType(assetType);
                    ClientData rsp = interfRiskRdsService.sendRiskRdsListData(interfRiskRdsDto, listMap, null);
                    logger.info("==========批量试算接口<RSK011>事件类型:{} end==========", eventType);
                    retList.add(JsonUtils.object2Json(rsp));
                }else {
                    retList.add(JsonUtils.object2Json(interfRiskRdsService.sendRiskRdsDataCommon(listMap.get(0), interfRiskRdsDto)));
                }
                return retList;
            }else {
                logger.info("==========系统参数中未开启风险试算开关==========");
                return null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("==========交易或委托类转换接口 Exception==========\r\n {}", e.getMessage());
            return null;
        }
    }
}
