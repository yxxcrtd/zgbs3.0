package com.joyintech.tams.invest.trade.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.risk.dto.RiskCreditListCommonDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.risk.service.RiskNamListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesDealCommon;
import com.joyintech.tams.invest.trade.common.service.RiskAuthorizeListCommonValidatorService;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.controller
 * @Description: 项目类资产 存续期查询
 * @date Date : 2018年09月21日 13:48
 */
@Controller
@RequestMapping("/invest/trade/TrdCommonController")
public class TrdCommonController extends BaseController {

    /**
     * TradeCommonService
     */
    @Autowired
    private TradeCommonService tradeCommonService;

    @Autowired
    private RiskNamListCommonValidatorService riskNamListCommonValidatorService;

    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    
    @Autowired
    private RiskAuthorizeListCommonValidatorService riskAuthorizeListCommonValidatorService;
    
    /**
     * 获取下一个工作日
     *
     * @param params 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    @RequestMapping("getNextWorkDay")
    @ResponseBody
    public String getNextWorkDay(@RequestParam Map<String, Object> params) {
        return tradeCommonService.getNextWorkDay(params);
    }
    

    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@RequestBody TrdProductDeal trdProductDeal) {
        AjaxResult wr = new AjaxResult();
        RiskCreditListCommonDto riskCreditListCommonDto = new RiskCreditListCommonDto();
        //名单管理  isNameSoftControl true:软控  false:硬控
        boolean isNameSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_NAME_SOFTCONTROL")); 
//        Map<String,Object> params = new HashMap<>();
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        String ps = trdProductDeal.getPs();
        String counterId = trdProductDeal.getCounterId();
        if (isNameSoftControl) {
            Map<String, Object> param = new HashMap<>();
            param.put("listDate", trdProductDeal.getTradeDate());
            param.put("connterId",trdProductDeal.getCounterId());
            if (StringUtil.isNotEmpty(trdProductDeal.getFinprodId())) {
                param.put("finprodId", trdProductDeal.getFinprodId());
            }else{
                param.put("inputValue1", trdProductDeal.getInputValue1());
                param.put("inputValue2", trdProductDeal.getInputValue2());
                param.put("inputValue3", trdProductDeal.getInputValue3());
            }
            param = riskNamListCommonValidatorService.名单是否匹配(param);
            if (Boolean.parseBoolean(param.get("isAssetMatch").toString())) {
                if (param.containsKey("listTypeAsset")) {
                    JoyinMessage ms = new JoyinMessage("资产在黑名单内，是否确认提交?", MessageType.确认消息);
                    joyinMessages.add(ms);
                    wr.setJoyinMessages(joyinMessages);
                }
            } else if(!Boolean.parseBoolean(param.get("isAssetMatch").toString()) && Boolean.parseBoolean(param.get("isAssetWhiteList").toString())) { 
                    JoyinMessage ms = new JoyinMessage("该资产不在白名单内，是否确认提交?", MessageType.确认消息);
                    joyinMessages.add(ms);
                    wr.setJoyinMessages(joyinMessages); 
            }
            if (!Boolean.parseBoolean(param.get("isOrgBlackMatch").toString())) {    
                if (param.containsKey("listTypeOrg")) {
                    JoyinMessage ms = new JoyinMessage("资产所属机构（或交易对手）在黑名单内，是否确认提交?", MessageType.确认消息);
                    joyinMessages.add(ms);
                    wr.setJoyinMessages(joyinMessages);
                }
            } else if (!Boolean.parseBoolean(param.get("isOrgWhiteMatch").toString())) {
                JoyinMessage ms = new JoyinMessage("该资产所属机构（或交易对手）不在白名单内，是否确认提交?", MessageType.确认消息);
                joyinMessages.add(ms);
                wr.setJoyinMessages(joyinMessages);
            }
        }
        //授信管理  isCreditSoftControl true:软控  false:硬控 
        boolean isCreditSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_CREDIT_SOFTCONTROL")); 
//        String status = "01"; 
        if (!StringUtil.equals(BussinessDictCodeList.监控授信开关_关.getValue(), CacheUtil.getSysParam(BussinessConstant.ISCREDIT)) && isCreditSoftControl) {
        	CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        	AllTradeCommonInputDto inputDto =new AllTradeCommonInputDto();
        	inputDto.setTrdProductDeal(trdProductDeal);
        	// TODO 回购类型
        	inputDto.setRepoType(trdProductDeal.getRemark());
        	List<CreditLinesDealCommon> list = ceLinesUpdate.isNeedOccupyCre(inputDto, true,false,false);
        	if(list !=null && list.size()>0){
        		if(list.get(0).getErrMessage() !=null && getMessage("BUSS_COM_CRE_001").equals(list.get(0).getErrMessage())){
        			list.get(0).setErrMessage(getMessage("BUSS_COM_CRE_004")); 
        		}
        		JoyinMessage joyinMessage = new JoyinMessage(list.get(0).getErrMessage(), MessageType.确认消息);
        		joyinMessages.add(joyinMessage);
        	}    	           
//        	riskCreditListCommonDto = riskCreditListCommonValidatorService.授信是否匹配(trdProductDeal);
//            if (StringUtil.isNotEmpty(riskCreditListCommonDto) && StringUtil.isNotEmpty(riskCreditListCommonDto.getCreditLinesDealCommonlist())) {
//                for (CreditLinesDealCommon creditCommon : riskCreditListCommonDto.getCreditLinesDealCommonlist()) {
//                    if (StringUtil.isNotEmpty(creditCommon.getErrMessage())) {
//                        JoyinMessage ms = new JoyinMessage(creditCommon.getErrMessage(), MessageType.确认消息);
//                        joyinMessages.add(ms);
//                    }     
//                    if (StringUtil.isNotEmpty(creditCommon.getStatus()) && "02".equals(creditCommon.getStatus())) { 
//                        status = "02"; 
//                    }
//                }  
//            }
//            riskCreditListCommonDto.setStatus(status);
                wr.setJoyinMessages(joyinMessages);
        } 
        //授权管理  isAuthorizationSoftcontrol true:软控  false:硬控
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        if (isAuthorizationSoftcontrol) {
            trdProductDeal.setPs(ps);
            trdProductDeal.setCounterId(counterId);
            boolean isRiskAuthorize = riskAuthorizeListCommonValidatorService.授权是否匹配(trdProductDeal);
            if (StringUtil.isNotEmpty(isRiskAuthorize) && isRiskAuthorize) {
                JoyinMessage ms = new JoyinMessage("交易本金金额超过领导授权额度，是否确认提交!", MessageType.确认消息);
                joyinMessages.add(ms);
                wr.setJoyinMessages(joyinMessages);  
            } 
        }  
        wr.setData(riskCreditListCommonDto);
        return wr;
    }
    
//    /**
//     * <一句话功能简述>
//     * <功能详细描述>
//     * @param params
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @Deprecated
//    @RequestMapping("delLinesDeal")
//    @ResponseBody
//    public List<CreditLinesDealCommon> delLinesDeal(@RequestBody RiskCreditListCommonDto riskCreditListCommonDto,String tradeId) { 
//        if ("02".equals(riskCreditListCommonDto.getStatus())) {
//            return null;
//        }
//        if (!StringUtil.isNotEmpty(riskCreditListCommonDto.getCreLineslist())) {
//            return null;
//        }
//        return riskCreditListCommonValidatorService.delLinesDeal(riskCreditListCommonDto,tradeId);
//    }
@RequestMapping("checkTrd")
@ResponseBody
public AjaxResult checkTrd(@RequestBody TrdProductDeal trdProductDeal) {
    AjaxResult wr = new AjaxResult();


    Map<String, Object> map = new HashMap<String, Object>();
    List<JoyinMessage> joyinMessagesEx = new ArrayList<>();
    map.put("payDate", DateUtil.parse(DateUtil.getSysDate()));
    map.put("finprodId", trdProductDeal.getFinprodId());
		/*List<ValidMessage> joyinMessages = new ArrayList<>();
		int count=tradeCommonService.getPayTrad(map);
		if(count > 0) {
			ValidMessage ms = new ValidMessage("收息,还本,到期日当天禁止转仓!");
		    joyinMessages.add(ms);
		    wr.setErrorList(joyinMessages);
		}*/
    if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(trdProductDeal.getFinprodType2())
            || DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(trdProductDeal.getFinprodType2())
            || DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(trdProductDeal.getFinprodType2())) {
        // 删除当日存在待处理存续期交易时不允许转仓的限制。
        if(!DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(trdProductDeal.getFinprodType2())) {
            int counts = tradeCommonService.selectTrdOnPayDate(map);
            if (counts > 0) {
                wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                wr.setMessage("该资产当天存在存续期待处理或流程中交易，请先处理！", MessageType.错误消息);
                return wr;
            }
        }

        map.put("balanceDate", DateUtil.addDay(DateUtil.parse(DateUtil.getSysDate()), -1));
        map.put("booksetId", trdProductDeal.getPortfolioId());
        map.put("subjectNo", "12219903" + trdProductDeal.getFinprodId());
        Map<String, Object> balanceAmt = tradeCommonService.getYSKLXBalance(map);
        if (StringUtil.isNotEmpty(balanceAmt) && (((BigDecimal) balanceAmt.get("BAMT")).compareTo(BigDecimal.ZERO) > 0)) {
            JoyinMessage ms = new JoyinMessage("其他应收款-利息科目余额大于0，是否确认提交?", MessageType.确认消息);
            joyinMessagesEx.add(ms);
            wr.setJoyinMessages(joyinMessagesEx);
        }
    }
    return wr;
}

// add by etliu 20210129 start
    /**
     * 检查画面选择的成交编号是否存在对应的报价
     * 目前债券二级交易和回购交易画面会用到
     *
     * @return 是否存在对应的报价(Y、N)
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/checkExecIdHasQuote")
    public String checkExecIdHasQuote(@RequestParam Map<String, Object> params) {
        return tradeCommonService.checkExecIdHasQuote((String) params.get("execId"));
    }
    // add by etliu 20210129 end
}
