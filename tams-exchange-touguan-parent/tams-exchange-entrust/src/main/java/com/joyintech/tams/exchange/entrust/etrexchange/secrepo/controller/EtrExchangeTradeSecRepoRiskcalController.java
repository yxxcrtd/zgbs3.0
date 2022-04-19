/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.component.business.riskrds.controller.RiskRdsTrialController;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeRiskcalSecRepoResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service.EtrExchangeTradeSecRepo001Service;
import com.joyintech.tams.invest.common.client.ClientData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易所指令交易风险试算
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrExchangeTradeSecRepoRiskcalController
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeSecRepoRiskcal")
@RequestMapping("/etrexchange/common/etrExchangeTradeSecRepoRiskcal")
@Api(tags = "交易所指令交易风险试算")
public class EtrExchangeTradeSecRepoRiskcalController extends BaseController{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @Autowired
    private RiskRdsTrialController riskRdsTrialController;

    @Autowired
    private EtrExchangeTradeSecRepo001Service etrExchangeTradeSecRepo001Service;

    @ApiOperation(value = "风险试算初始化参数", notes = "风险试算初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody EtrExchangeTradeSaveSecRepoRequest dto) throws Exception {
        try {
            //风险试算check
            String  message = etrExchangeTradeSecRepo001Service.doRiskcalCheckMethod(dto);
            if (StringUtil.isNotEmpty(message)) {
                return new Message<AjaxResult>().fail().setMessage(message);
            }
            EtrExchangeTradeRiskcalSecRepoResponse result=new EtrExchangeTradeRiskcalSecRepoResponse();
            //风险试算组装dto
            Map<String, Object> dataMap=etrExchangeTradeSecRepo001Service.doRiskcalPackageEquity(dto);
            List<String> resultArrList=new ArrayList<>();
            //债券
            if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {

                  if (StringUtil.equals(dto.getPs(),DictCodeListExt1.交易方向_债转股.getValue())) {
                      resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "0", "RSK011", "F01");
                  }else{
                      resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "0", "RSK002", "F01");
                  }

//                String riskResult = riskRdsTrialController.doInfBatchData(dataMap, "EtrExchangeTradeMulti001", "0", "F01");
//                resultArrList.add(riskResult);
            }
            //质押式回购
            else if (StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                 || StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
                resultArrList=interfRiskRdsController.doInfBatchData(dataMap,"0", "RSK002","F07");
            }
            result.setRiskMathFlag("02");
            Boolean forbidFlag=false;
            Boolean cluflag=true;
            List<EtrExchangeTradeRiskcalSecRepoResponse> resultlist=new ArrayList<>();
            if(!StringUtil.isEmptyList(resultArrList) ){
                for(String row:resultArrList){
                    ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
                    if(StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
                            && StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
                    ){
                        if (StringUtil.equals(resultArr.getHead().get("std400msgt"),"E")) {
                            return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
                        }
                    }
                    if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
                        return new Message<AjaxResult>().success().setMessage("无");
                    }
                    List<Map<String, Object>> riskList = (List<Map<String, Object>>)resultArr.getBody().get("RSK_LIST");
                    for(Map<String, Object> map: riskList){
                        if(StringUtil.equals(map.get("kriStatus"),"0") && !StringUtil.equals(map.get("kriTrend"),"向好")){
                            forbidFlag=true;
                        }
                        map.put("kriStatusCn",etrExchangeTradeSecRepo001Service.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
                        resultlist.add(BeanUtil.mapToObject(map, EtrExchangeTradeRiskcalSecRepoResponse.class));
                    }
                    result.setRiskcalReturnList(resultlist);
                    result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
                    //当置为不通过时就不再进来了
                    if(cluflag) {
                        if(!StringUtil.equals(resultArr.getBody().get("rskStatus"),"3")){
                            if(StringUtil.equals(resultArr.getBody().get("rskStatus"),"4")&&forbidFlag){
                                cluflag=false;
                                resultArr.getBody().put("rskStatus","3");
                                result.setRskStatus("3");
                            }
                        }else{
                            cluflag=false;
                        }
                        if (!cluflag) {
                            result.setRiskTrialSpan("试算不通过");
                        } else {
                            result.setRiskTrialSpan("试算通过");
                        }
                    }

                }

            }
            AjaxResult m =new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("查询异常");
        }
    }
}
