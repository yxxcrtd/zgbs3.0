package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto.EtrExchangeAgreRepTrade001Dto;
import com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.service.EtrExchangeAgreRepTrade001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.controller.EtrExchangeTradeCommonRiskcalController;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeCommonRiskcalResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
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

import java.math.BigDecimal;
import java.util.*;

import static com.joyintech.tams.constant.DictCodeListExt3.交易方向_解除质押;

/**
 * 协议回购指令风险试算
 * @author etliu
 * @version 1.0 2021年3月16日
 * @see EtrExchangeTradeCommonRiskcalController
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeAgreRepTradeRiskCal")
@RequestMapping("/etrexchange/agreementrep/etrExchangeAgreRepTradeRiskCal")
@Api(tags = "协议回购指令风险试算")
public class EtrExchangeAgreRepTradeRiskCalController extends BaseController {

    @Autowired
    private EtrExchangeAgreRepTrade001Service etrExchangeAgreRepTrade001Service;

    @Autowired
    private EtrExchangeTradeCommon001Service etrExchangeTrade001CommonService;

    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

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
    public Message<AjaxResult> doRiskcal(@RequestBody EtrExchangeAgreRepTrade001Dto dto) throws Exception {
        try {
//            //风险试算check
//            String message = etrExchangeTrade001CommonService.doRiskcalCheckMethod(dto);
//            if (StringUtil.isNotEmpty(message)) {
//                return new Message<AjaxResult>().fail().setMessage(message);
//            }
            EtrExchangeTradeCommonRiskcalResponse result = new EtrExchangeTradeCommonRiskcalResponse();
            dto.setExpireSettleAmt(Objects.isNull(dto.getExpireSettleAmt())?BigDecimal.ZERO:dto.getExpireSettleAmt());
            if(StringUtil.isNotEmpty(dto.getPs()) && StringUtil.equals(dto.getPs(), 交易方向_解除质押.getValue())
                    && (StringUtil.equals(dto.getTradeMarket(),"03") || (StringUtil.equals(dto.getTradeMarket(),"04") && dto.getExpireSettleAmt().compareTo(BigDecimal.ZERO) == 0))) {
                AjaxResult m =new AjaxResult();
                result.setRiskMathFlag("02");
                result.setRskStatus("0");
                result.setRiskTrialSpan("试算通过");
                m.setData(result);
                return new Message<AjaxResult>().success().setData(m);
            }
            //风险试算组装dto
            Map<String, Object> dataMap = etrExchangeAgreRepTrade001Service.doRiskcalPackageEquity(dto);
            List<String> resultArrList = new ArrayList<>();
            String tempResult = interfRiskRdsController.doInfData(dataMap,"0", "RSK002","F07");
            if (StringUtil.isNotBlank(tempResult)) {
                resultArrList.add(tempResult);
            }
            result.setRiskMathFlag("02");
            Boolean forbidFlag=false;
            Boolean cluflag=true;
            List<EtrExchangeTradeCommonRiskcalResponse> resultlist=new ArrayList<>();
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
                        map.put("kriStatusCn",etrExchangeTrade001CommonService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
                        resultlist.add(BeanUtil.mapToObject(map, EtrExchangeTradeCommonRiskcalResponse.class));
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
