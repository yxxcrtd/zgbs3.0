package com.joyintech.tams.invest.entrust.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.risk.service.RiskNamListCommonValidatorService;
import com.joyintech.tams.invest.entrust.common.dto.EtrFinProductCommonDto;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrbondtostock.dto.EtrBondToStockDto;
import com.joyintech.tams.invest.entrust.etrbondtostock.service.EtrBondToStockService;
import com.joyintech.tams.invest.entrust.etrboutrepo.dto.EtrBoutRepoDto;
import com.joyintech.tams.invest.entrust.etrbrowdepoinfo.dto.EtrBrowDepoInfoDto;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrconbond.service.EtrConBondService;
import com.joyintech.tams.invest.entrust.etrforwex.dto.EtrForwexDto;
import com.joyintech.tams.invest.entrust.etrfund.dto.EtrFundDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrirs.dto.EtrIRSDto;
import com.joyintech.tams.invest.entrust.etrlendspecinfo.dto.EtrLendSpecInfoDto;
import com.joyintech.tams.invest.entrust.etrplanassetc.dto.EtrPlanAssetCDto;
import com.joyintech.tams.invest.entrust.etrplanassetn.dto.EtrPlanAssetNDto;
import com.joyintech.tams.invest.entrust.etrplanassetr.dto.EtrPlanAssetRDto;
import com.joyintech.tams.invest.entrust.etrproject.dto.EtrProjectDto;
import com.joyintech.tams.invest.entrust.etrrepo.dto.EtrRepoDto;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.invest.entrust.etrstock.dto.EtrStockDto;
import com.joyintech.tams.invest.entrust.etrwtfxswap.dto.EtrWtFxswapDto;
import com.joyintech.tams.invest.entrust.etrwtspotex.dto.EtrWtSpotexDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author yjm
 * @date 2019/11/14 0014 15:44
 * @Description 委托指令可用现金查询
 */
@Controller
@RequestMapping("/invest/entrust/EtrCommonController")
public class EtrCommonController extends BaseController {

    /**
     * 日志
     */
    public static Logger logger = LoggerFactory.getLogger(EtrCommonController.class);

    /**
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;

    @Autowired
    private RiskNamListCommonValidatorService riskNamListCommonValidatorService;

    @RequestMapping("getProAvailableCash")
    @ResponseBody
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param){
        return etrCommonService.getProAvailableCash(param);
    }


    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@RequestBody TrdProductDealDto trdProductDeal) {
        AjaxResult wr = new AjaxResult();
        //名单管理  isNameSoftControl true:软控  false:硬控
        boolean isNameSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_NAME_SOFTCONTROL"));
        Map<String,Object> params = new HashMap<>();
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        if (isNameSoftControl) {
            //多对多
            if(null!=trdProductDeal.getSelectedItem()&&!trdProductDeal.getSelectedItem().isEmpty()){
                for(TrdProductDealDto item : trdProductDeal.getSelectedItem()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("listDate", null != item.getTradeDate() ? item.getTradeDate() : item.getEntrustDate());
                    param.put("counterId", item.getCounterId());
                    if (StringUtil.isNotEmpty(item.getFinprodId())) {
                        param.put("finprodId", item.getFinprodId());
                    } else {
                        param.put("inputValue1", item.getInputValue1());
                        param.put("inputValue2", item.getInputValue2());
                        param.put("inputValue3", item.getInputValue3());
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
            } else{
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
        }

//        //授权管理  isAuthorizationSoftcontrol true:软控  false:硬控
//        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
//
//        if (isAuthorizationSoftcontrol) {
//            boolean isRiskAuthorize = 授权是否匹配(trdProductDeal);
//            if (StringUtil.isNotEmpty(isRiskAuthorize) && isRiskAuthorize) {
//                JoyinMessage ms = new JoyinMessage("交易本金金额超过领导授权额度，是否确认提交!", MessageType.确认消息);
//                joyinMessages.add(ms);
//                wr.setJoyinMessages(joyinMessages);
//            }
//        }
        return wr;
    }

    /**
     * 验证方法
     *
     * @param value 需要被验证的业务对象
     * @return 验证消息，如果为空则表示验证通过
     * @since 1.0
     */
    public AjaxResult 授权是否匹配(Object value) {
        AjaxResult wr = new AjaxResult();
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
        // 交易(委托)授权硬控
        if (!isAuthorizationSoftcontrol) {
            return wr;
        }
        try {
            TrdProductDeal trdProductDeal = null;
            String repType = null;

            // 副总裁/总裁助理授权额度检查相关处理
            String counterId = null;
            BigDecimal prinAmt = null;
            String 平安资产类型 = null;
            String windtypeone = null;
            String tradeMarket = null;
            String ps = null;
            // 资产类型改为授权类型 add by renping 2019-08-06  start
            String finprodId = null;
            // 资产类型改为授权类型 add by renping 2019-08-06  end
            // 资产类型改为授权类型 add by zhangnian 2020-03-04  start
            String 无标的投管资产类型映射行内资产分类 = null;
            // 资产类型改为授权类型 add by zhangnian 2019-03-04  end
            // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2020-03-04  start
            String inputValue1 = null;String inputValue2 = null;String inputValue3 = null;
            // 无标的投管资产类型映射行内资产分类 inputValue1 inputValue2 inputValue3 add by zhangnian 2019-03-04  end
            // 债券投资交易
            if (value instanceof EtrInvestBondInfoDto) {
                EtrInvestBondInfoDto inputDto = (EtrInvestBondInfoDto)value;
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改债券获取金额by赵东东 2019.9.4 start
                prinAmt = inputDto.getEntrustNum();
                // 修改债券获取金额by赵东东 2019.9.4 end
                平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 拆借投资交易
            else if (value instanceof EtrLendSpecInfoDto) {
                EtrLendSpecInfoDto inputDto = (EtrLendSpecInfoDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_拆借.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeList.平安资产类型_拆借.getValue();
                inputValue2 = ps;
            }
            // 存款类资产投资交易
            else if (value instanceof EtrBrowDepoInfoDto) {
                EtrBrowDepoInfoDto inputDto = (EtrBrowDepoInfoDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_存款.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 基金类投资交易
            else if (value instanceof EtrFundDto) {
                EtrFundDto inputDto = (EtrFundDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 计划类资产（净值型）交易
            else if (value instanceof EtrPlanAssetNDto) {
                // 修改强转类型by赵东东2019.9.4 start
                /* EtrFundDto inputDto = (EtrFundDto)value; */
                EtrPlanAssetNDto inputDto = (EtrPlanAssetNDto)value;
                // 修改强转类型by赵东东2019.9.4 start
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_计划类资产_净值型.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 计划类资产（利率型）交易
            else if (value instanceof EtrPlanAssetRDto) {
                EtrPlanAssetRDto inputDto = (EtrPlanAssetRDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_卖出.getValue())){
                    prinAmt = inputDto.getEntrustNum();
                }
                平安资产类型 = DictCodeList.平安资产类型_计划类资产_利率型.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            // 回购资产交易
            else if (value instanceof EtrRepoDto) {
                EtrRepoDto inputDto = (EtrRepoDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();repType = inputDto.getAssetType1();
                inputValue1 = DictCodeList.平安资产类型_质押式回购.getValue();
                inputValue2 = repType;
                inputValue3 = ps;
            }
            // 项目类资产交易
            else if (value instanceof EtrProjectDto) {
                EtrProjectDto inputDto = (EtrProjectDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_项目类资产.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            //  计划类资产（货币型）交易
            else if (value instanceof EtrPlanAssetCDto) {
                EtrPlanAssetCDto inputDto = (EtrPlanAssetCDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_计划类资产_货币型.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }
            //  买断式回购
            else if (value instanceof EtrBoutRepoDto) {
                EtrBoutRepoDto inputDto = (EtrBoutRepoDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeList.平安资产类型_买断式回购.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeList.平安资产类型_买断式回购.getValue();
                inputValue2 = ps;
            }
            // 债券借贷
            else if (value instanceof EtrSecLendDto) {
                EtrSecLendDto inputDto = (EtrSecLendDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改获取金额by赵东东 2019.8.23 start
                prinAmt = inputDto.getTotalAmt();
                // 修改获取金额by赵东东 2019.8.23 end
                平安资产类型 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                // 修改获取借贷方向by赵东东 2019.8.23 start
                ps = inputDto.getBusiPs();
                // 修改获取借贷方向by赵东东 2019.8.23 end
                finprodId = "";//inputDto.getFinprodId();
                inputValue1 = DictCodeListExt2.平安资产类型_债券借贷.getValue();
            }
            // 股票
            else if (value instanceof EtrStockDto) {
                EtrStockDto inputDto = (EtrStockDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                // 将ps不等于T02的改成T01 赵东东 2019.8.26 start
                if(!StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                    // 将代码中的字符串改掉by赵东东 2019.8.26 start
                    ps= DictCodeList.交易方向_买入.getValue();
                    // 将代码中的字符串改掉by赵东东 2019.8.26 end
                }
                // 将ps不等于T02的改成T01 赵东东 2019.8.26 end
                finprodId = inputDto.getFinprodId();
            }
            // 外汇即期
            else if (value instanceof EtrWtSpotexDto) {
                EtrWtSpotexDto inputDto = (EtrWtSpotexDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeListExt2.平安资产类型_外汇即期.getValue();
            }
            // 外汇远期
            else if (value instanceof EtrForwexDto) {
                EtrForwexDto inputDto = (EtrForwexDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                prinAmt = inputDto.getEntrustAmt();
                平安资产类型 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                ps = inputDto.getPs();
                inputValue1 = DictCodeListExt2.平安资产类型_外汇远期.getValue();
            }
            // 外汇掉期
            else if (value instanceof EtrWtFxswapDto) {
                EtrWtFxswapDto inputDto = (EtrWtFxswapDto) value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改获取交易金额by赵东东 2019.8.23 start
                prinAmt = inputDto.getTradeAmt();
                // 修改获取交易金额by赵东东 2019.8.23 end
                平安资产类型 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                // 修改获取交易方向by赵东东 2019.8.23 start
                ps = inputDto.getTradePs();
                // 修改获取交易方向by赵东东 2019.8.23 start
                inputValue1 = DictCodeListExt2.平安资产类型_外汇掉期.getValue();
            }
            // 利率互换
            else if (value instanceof EtrIRSDto) {
                EtrIRSDto inputDto = (EtrIRSDto)value;

                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getCounterId();
                // 修改金额的大小by赵东东 2019.8.23 start
                prinAmt = inputDto.getEntrustAmt().multiply(new BigDecimal(10000));
                // 修改金额的大小by赵东东 2019.8.23 end
                平安资产类型 = DictCodeListExt2.平安资产类型_利率互换.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, inputDto);
                // 修改交易方向by赵东东 2019.8.23 start
                ps = inputDto.getBusiPs();
                // 修改交易方向by赵东东 2019.8.23 start
                inputValue1 = DictCodeListExt2.平安资产类型_利率互换.getValue();
            }
            // 可转债 add by gejiaao
            else if (value instanceof EtrConBondDto) {
                EtrConBondDto inputDto = (EtrConBondDto)value;
                EtrConBondService etrConBondService = SpringContextUtil.getBean(EtrConBondService.class);
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getEtrInvestPersonInfo().getCounterId();
                prinAmt = inputDto.getPurchBondTotal();
                平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = etrConBondService.getTrdProductDeal(inputDto);
                ps = inputDto.getPs();
                finprodId = inputDto.getFinprodId();
            }

            // 可转债转股 add by gaopeng  20191123
            else if (value instanceof EtrBondToStockDto) {
                EtrBondToStockDto inputDto = (EtrBondToStockDto)value;
                EtrBondToStockService etrBondToStockService = SpringContextUtil.getBean(EtrBondToStockService.class);
                // 副总裁/总裁助理授权额度检查相关处理
                counterId = inputDto.getEtrInvestPersonInfo().getCounterId();
                prinAmt = inputDto.getStocketrPriceTotal();
                平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
                windtypeone = inputDto.getFinprodType2();
                tradeMarket = inputDto.getTradeMarket();
                trdProductDeal = etrBondToStockService.getTrdProductDeal(inputDto);
                ps = inputDto.getPs();
                trdProductDeal.setFinprodId(inputDto.getShareFinprodId());
                finprodId = inputDto.getShareFinprodId();
            }

            // 授权额度检查（当本金金额高于副总裁/总裁助理相应资产的授权额度时，提示消息）
            if (prinAmt != null) {
                CommonBusinessService commonBusinessService = SpringContextUtil.getBean(
                        CommonBusinessService.class);
                Map<String, Object> params = CommonBusinessService.setParams(counterId, prinAmt, LoginUserUtil.getUserName(),
                        平安资产类型, null, tradeMarket, ps,finprodId, null);
                if (commonBusinessService != null && commonBusinessService.是否超过副总额额度_指令用(params)) {
                    JoyinMessage ms = new JoyinMessage("交易本金金额超过领导授权额度，是否确认提交!", MessageType.确认消息);
                    joyinMessages.add(ms);
                    wr.setJoyinMessages(joyinMessages);
                }
            }

        }
        catch (Exception e) {
            logger.info("执行风险相关交易共通check出现异常，异常信息如下：");
            logger.error(e.getMessage(),e);
        }

        return wr;
    }

    /**
     * 银行间指令关闭check
     *
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("entrustCheckCloseList")
    public AjaxResult entrustCheckCloseList(@RequestBody  Map<String,Object> map) {
    	List<Map<String,Object>> selectedItem= (List<Map<String, Object>>) map.get("selectedItem");
    	AjaxResult ajaxResult=new AjaxResult();
    	if(StringUtil.isNotEmpty(selectedItem)){
    		List<String> entrustIds = new ArrayList<>();
    		for(Map<String,Object> Item:selectedItem){
    			entrustIds.add((String)Item.get("entrustId"));
    		}
    		Map<String,Object> param= new HashMap<>();
    		param.put("entrustIds",entrustIds);
            ajaxResult.setErrorList(etrCommonService.entrustCheckCloseList(param));
            return ajaxResult;
    	}
    	ValidMessage  validMessage= new ValidMessage();
    	validMessage.setMessage("请选择列表中数据");
    	ajaxResult.setErrorList(Arrays.asList(validMessage));
    	return ajaxResult;
    }

    /**
     * 根据键值获取动态配置信息 select ... DYN_CONFIG_INFO where DYN_ID...
     * 根据dynId查询对应配置信息 {"dynId":"id"}
     * @return 示例： {"all":"secName,secType,issuer","checked":"secName"}
     */
    @RequestMapping(value="selectByDynId")
    @ResponseBody
    public String selectByDynId(@RequestBody Map<String,Object> map) {
        return etrCommonService.selectByDynId(map);
    }

    /**
     * 修改动态配置信息  {"dynId":dynId, "checked":"a,b,c", "userName":"dd"}
     * @return
     */
    @RequestMapping("updateByParam")
    @ResponseBody
    public Integer updateByParam(@RequestBody Map<String,Object> map){
        map.put("userName",LoginUserUtil.getUserName());
        return etrCommonService.updateByParam(map);
    }

    /***
     * 持仓数量，可买数量，可买数量，成本价
     * @return
     */
    @RequestMapping(value = "/initAmount")
    public String initAmount(Model model){
        return "invest/entrust/common/EtrInitAmount";
    }

    /***
     * 托管户可用现金，证券户可用现金，T+1日可用现金
     * @return
     */
    @RequestMapping(value = "/initCash")
    public String initCash(Model model){
        return "invest/entrust/common/EtrInitCash";
    }

    /**
     * 查询持仓信息
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findSecAmountInfo")
    public EtrFinProductCommonDto findSecAmountInfo(@RequestBody Map<String, Object> param){
        return etrCommonService.findSecAmountInfo(param);
    }


    /**
     * 查询现金数据
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("findCashAmountInfo")
    public EtrFinProductCommonDto findCashAmountInfo(@RequestBody Map<String, Object> param){
        return etrCommonService.findCashAmountInfo(param);
    }

    /**
     * 计算可买数量
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("calCanBuyAmount")
    public EtrFinProductCommonDto calCanBuyAmount(@RequestBody Map<String, Object> param){
        return etrCommonService.calCanBuyAmount(param);
    }

    public BigDecimal getCostPrice(Map<String, Object> param){
        return etrCommonService.getCostPrice(param);
    }


    /**
     * 获取资产SPPI模块是否有SPPI测试结果
     */
    @ResponseBody
    @RequestMapping("getAssetInfoBySPPI")
    public AjaxResult getAssetInfoBySPPI(@RequestBody Map<String, Object> param){
        String assetStatusIsTestBySppi = etrCommonService.assetIsTestBySppi(param);
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        // 状态码 0 未测试 2 测试通过 3 测试不通过
        if((StringUtil.equals(assetStatusIsTestBySppi,"2")||StringUtil.equals("3",assetStatusIsTestBySppi))){
           if(StringUtil.equals(assetStatusIsTestBySppi,"3"))
              //未通过
               ajaxResult.setData("3");
           else
               ajaxResult.setData("2");
        }else {
            //未测试
            ajaxResult.setData("0");
         }
        return ajaxResult;
    }

    /**
     * 选择收取合同约定的现金流时查询sppi中资产情况
     * @param param
     * @return
     */

    @ResponseBody
    @RequestMapping("getSppiInfoByY1")
    public AjaxResult getSppiInfoByY1(@RequestBody Map<String, Object> param){
         Map<String,Object> sinal =  etrCommonService.getSppiInfoByY1(param);
         AjaxResult ajaxResult = new AjaxResult();
         ajaxResult.setData(sinal);
         return ajaxResult;
    }

}