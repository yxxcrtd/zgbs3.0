package com.joyintech.tams.cfets.common.controller;

import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.cfets.trdconfirm.service.CfetsTrdConfirm001Service;
import com.joyintech.tams.cfets.trdconfirm.service.CfetsTrdConfirmService;
import com.joyintech.tams.common.base.entity.InterfCstpBjfxjg;
import com.joyintech.tams.common.base.entity.InterfCstpTrade;
import com.joyintech.tams.common.base.entity.InterfOpicsTradRepo;
import com.joyintech.tams.common.base.entity.InterfTradSecdeal;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.constant.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 共通
 * @author zznn
 * @date 2021/11/26
 */
@Controller
@RequestMapping("/cfets/common/CfetsIbuQuoteBatchCommon")
public class CfetsIbuQuoteBatchCommonController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CfetsTrdConfirmService cfetsTrdConfirmService;
    @Autowired
    private CfetsTrdConfirm001Service cfetsTrdConfirm001Service;

    /**
     * 回溯登记成交确认
     * @param parm
     */
    @ResponseBody
    @PostMapping("resaleregautodeal")
    public void resaleregautodeal(@RequestBody Map<String,Object> parm) throws Exception {
        List<String> totleTradeIds = (List<String>) parm.get("totleTradeIds");
        try{
            totleTradeIds.forEach(item ->{
                cfetsTrdConfirm001Service.doCompleteMethod(cfetsTrdConfirmService.prepareEntity(item, DictCodeListExt1.生成方式_批处理生成.getValue()), OperationType.NEW);
            });
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("回售登记数据自动生成成交数据异常:"+e.getMessage());
            throw  new Exception(e);
        }
    }



    @ResponseBody
    @PostMapping("cstpAutoTrade")
    public void cstpAutoTrade(@RequestBody Map<String,Object> parm) throws Exception {
        try{
            logger.info("-----------银行间自动匹配处理业务数据线程开启-------------");
            InterfCstpTrade interfCstpTrade = JsonUtils.json2Object(JsonUtils.object2Json(parm.get("interfCstpTrade")), InterfCstpTrade.class);
            if(parm.containsKey("interfTradSecdeal")){
                InterfTradSecdeal interfTradSecdeal = JsonUtils.json2Object(JsonUtils.object2Json(parm.get("interfTradSecdeal")), InterfTradSecdeal.class);
                cfetsTrdConfirmService.cstpAutoMatchMethod(interfTradSecdeal, null, null, interfCstpTrade);
            }
            if(parm.containsKey("interfOpicsTradRepo")){
                InterfOpicsTradRepo interfOpicsTradRepo = JsonUtils.json2Object(JsonUtils.object2Json(parm.get("interfOpicsTradRepo")), InterfOpicsTradRepo.class);
                cfetsTrdConfirmService.cstpAutoMatchMethod(null, interfOpicsTradRepo, null, interfCstpTrade);
            }
            if(parm.containsKey("interfCstpBjfxjg")){
                InterfCstpBjfxjg interfCstpBjfxjg = JsonUtils.json2Object(JsonUtils.object2Json(parm.get("interfCstpBjfxjg")), InterfCstpBjfxjg.class);
                cfetsTrdConfirmService.cstpAutoMatchMethod(null, null, interfCstpBjfxjg, null);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("cstp成交回报自动匹配异常:"+e.getMessage());
            throw  new Exception(e);
        }
    }
}

