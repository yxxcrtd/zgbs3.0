/*
 * 文件名：CfetsInterfService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2020年12月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.cfets.service;

import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.cfets.constant.CfetsConstant;
import com.joyintech.service.bankuplink.cfets.dao.CfetsInterfMapper;
import com.joyintech.service.bankuplink.cfets.dto.CfetsInputSuppleDto;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.dialogquotation.NoQuoteEntrieDto;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.dialogquotation.QuoteBody;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.dialogquotation.QuoteCancelBody;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.dialogquotation.QuoteResponseBody;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.dialogquotation.*;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.NoRelatedSymReqCxlDto;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.QuoteRequestBody;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.QuoteRequestCancelBody;
import com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.*;
import com.joyintech.service.bankuplink.cfets.util.CfetsDataConvertUtil;
import com.joyintech.service.bankuplink.cfets.util.CfetsInterUtil;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.FinProductSpecialDto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.CfetsDatadictConvert;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Cfets接口机连接Service
 * 
 * @author etliu
 * @version 1.0 2020年12月19日
 * @see CfetsInterfService
 * @since 1.0
 */
@Service
public class CfetsInterfService {

//    /**
//     * 日志
//     */
//    private static Logger logger = LoggerFactory.getLogger(CfetsInterfService.class);

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductMapper finProductMapper;
    
    /**
     * FinProductTypeMapper
     */
    @Autowired FinProductTypeMapper finProductTypeMapper;
    
    /**
     * CfetsInterfMapper
     */
    @Autowired
    private CfetsInterfMapper cfetsInterfMapper;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

    /**
     * 调用接口发送Cfets接口机
     * 
     * @param quoteResultDto 报价对象
     * @return 接口处理结果
     */
    public GateWayReturnMsgDto sendInterfHandle(QuoteResultDto quoteResultDto) {
        CfetsInputSuppleDto cfetsInputSuppleDto = new CfetsInputSuppleDto();

        if (StringUtil.equals(quoteResultDto.getQuoteType(),
            DictCodeList.报价系统区分类型_现券对话报价.getValue())) {
            BondDialogueQuoteDto bondDialogueQuoteDto = quoteResultDto.getBondDialogueQuote();
            // 根据金融产品代码查询市场代码
            if (bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId() != null) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId());
                if (finProduct != null) {
                    cfetsInputSuppleDto.setFinProduct(finProduct);
                    
                    FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
                    finProductTypeKey.setFinprodId(finProduct.getFinprodId());
                    finProductTypeKey.setBranch(0L);
                    FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
                    cfetsInputSuppleDto.setFinProductType(finProductType);
                    
                    bondDialogueQuoteDto.getIbuQuoteBondDto().setFinprodMarketId(
                        finProduct.getFinprodMarketId());

                    if (StringUtil.isNotBlank(finProduct.getFinprodMarketId())) {
                        cfetsInputSuppleDto.setSecurityID(
                            StringUtil.splitToList(".", finProduct.getFinprodMarketId()).get(0));
                        cfetsInputSuppleDto.setSymbol(finProduct.getFinprodAbbr());
                        if (StringUtil.isBlank(cfetsInputSuppleDto.getSymbol())) {
                            cfetsInputSuppleDto.setSymbol(finProduct.getFinprodName());
                        }
                    }
                    // 到期违约债只能传净价 不传应计利息
                    List<FinProductSpecialDto> finProductSpecialDtos =
                            joyinbusinessCommonMapper.queryFinProductSpecialByFinprodIds(Collections.singletonList(finProduct.getFinprodId()));
                    if (CollectionUtils.isNotEmpty(finProductSpecialDtos)
                            && StringUtil.equals(finProductSpecialDtos.get(0).getIsDefaultBondIndicator(), DictCodeList.是否_是.getValue())) {
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().setUnitInt(null);
                    }
                }
            }

            if (StringUtil.equals(bondDialogueQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
                DictCodeList.银行间上行本方角色_询价方.getValue())) {
                if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                        bondDialogueQuoteDto.getIbuQuoteBondDto().getExeType())) {
                    // 调用【/cashtransaction/dialogQuotation/quote S-用于发送、修改报价、报价接收、报价查询反馈】接口
                    QuoteBody quoteBody = this.convertToQuoteBody(bondDialogueQuoteDto,
                        cfetsInputSuppleDto);
                    return CfetsInterUtil.sendQuote(quoteBody);
                }
                else if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getExeType())) {
                    // 调用【/cashtransaction/dialogQuotation/quoteCancel Z-询价方撤销报价或撤销报价交谈】接口
                    QuoteCancelBody quoteBody = this.convertToQuoteBodyForQuoteCancel(
                        bondDialogueQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendQuoteCancle(quoteBody);
                }
            }
            else if (StringUtil.equals(
                bondDialogueQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
                DictCodeList.银行间上行本方角色_回复方.getValue())) {
                // 调用【/cashtransaction/dialogQuotation/quoteResponse
                // AJ-交易双方确认成交、拒绝成绩;回复方报价交谈、修改报价交谈、撤销报价交谈;询价放接收回复方发送的报价通知】接口
                QuoteResponseBody quoteBody = this.convertToQuoteBodyForQuoteResponse(
                    bondDialogueQuoteDto, cfetsInputSuppleDto);
                return CfetsInterUtil.sendQuoteResponse(quoteBody);
            }
        }
        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
            DictCodeList.报价系统区分类型_现券请求报价.getValue())) {
             BondRequestQuoteDto bondRequestQuoteDto = quoteResultDto.getBondRequestQuote();
            // 根据金融产品代码查询市场代码
            if (bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId() != null) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId());
                if (finProduct != null) {
                    bondRequestQuoteDto.getIbuQuoteBondDto().setFinprodMarketId(
                        finProduct.getFinprodMarketId());
                    
                    if (StringUtil.isNotBlank(finProduct.getFinprodMarketId())) {
                        cfetsInputSuppleDto.setSecurityID(
                            StringUtil.splitToList(".", finProduct.getFinprodMarketId()).get(0));
                        cfetsInputSuppleDto.setSymbol(finProduct.getFinprodAbbr());
                        if (StringUtil.isBlank(cfetsInputSuppleDto.getSymbol())) {
                            cfetsInputSuppleDto.setSymbol(finProduct.getFinprodName());
                        }
                    }
                }
            }

            if (StringUtil.equals(bondRequestQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
                DictCodeList.银行间上行本方角色_非做市方.getValue())) {
                if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方新增请求报价发送.getValue(),
                    bondRequestQuoteDto.getIbuQuoteBondDto().getExeType())) {
                    // 调用【/cashtransactionQDM/requestQuotation/quoteRequest
                    // R-用于非做市方发送请求报价、接收请求查询结果反馈等】接口
                    QuoteRequestBody quoteBody = this.convertToQuoteBodyForQuoteRequest(bondRequestQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendQuoteRequest(quoteBody);
                }
                else if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方请求报价撤回.getValue(),
                    bondRequestQuoteDto.getIbuQuoteBondDto().getExeType())) {
                    // 调用【/cashtransactionQDM/requestQuotation/quoteRequestCancel
                    // U32-用于非做市方撤销请求报价或者接收交易完成通知或请求报价过期或者所有做市方拒绝请求报价或请求报价系统自动撤销通知】接口
                    QuoteRequestCancelBody quoteBody = this.convertToQuoteBodyForQuoteRequestCancel(bondRequestQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendQuoteRequestCancle(quoteBody);
                }
                else if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方请求回复成交.getValue(),
                    bondRequestQuoteDto.getIbuQuoteBondDto().getExeType())) {
                    // 调用【/cashtransactionQDM/requestQuotation/quoteResponse
                    // AJ-用于非做市方确认做市方回复的报价】接口
                    com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.QuoteResponseBody quoteBody = this.convertToQuoteBodyForQuoteRequestResponse(bondRequestQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendQuoteRequestResponse(quoteBody);
                }
            }
        }
        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
            DictCodeList.报价系统区分类型_质押式回购对话报价.getValue())) {
            RepoDialogueQuoteDto repoDialogueQuoteDto = quoteResultDto.getRepoDialogueQuote();
            if (StringUtil.equals(repoDialogueQuoteDto.getIbuQuoteCrepDto().getQuoteRoleType(),
                DictCodeList.银行间上行本方角色_询价方.getValue())) {
                if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价新增.getValue(),
                    repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                        || StringUtil.equals(DictCodeList.银行间上行操作类型_本方新增发送.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                     || StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价修改.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                        || StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价交谈.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈发送.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈修改.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())) {
                    // 调用【/collateralisedRepo/dialogquotation/quote S-用于发送、修改报价、报价接收、报价查询反馈】接口
                    com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteBody quoteBody = this.convertToQuoteBodyForRepo(repoDialogueQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendRepoQuote(quoteBody);
                }
                else if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                    repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())) {
                    // 调用【/collateralisedRepo/dialogquotation/quoteCancel Z-询价方撤销报价或撤销报价交谈】接口
                    com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteCancelBody quoteBody = this.convertToQuoteBodyForRepoQuoteCancel(repoDialogueQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendRepoQuoteCancle(quoteBody);
                }
                else if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方回复成交.getValue(),
                    repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方回复拒绝.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())) {
                    // 调用【/collateralisedRepo/dialogquotation/quoteResponse
                    // AJ-交易双方确认成交、拒绝成绩;回复方报价交谈、修改报价交谈、撤销报价交谈;询价放接收回复方发送的报价通知】接口
                    com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteResponseBody quoteBody = this.convertToQuoteBodyForRepoQuoteResponse(repoDialogueQuoteDto, cfetsInputSuppleDto);
                    return CfetsInterUtil.sendRepoQuoteResponse(quoteBody);
                }
            }
            else if (StringUtil.equals(
                repoDialogueQuoteDto.getIbuQuoteCrepDto().getQuoteRoleType(),
                DictCodeList.银行间上行本方角色_回复方.getValue())) {
                // 调用【/collateralisedRepo/dialogquotation/quoteResponse
                // AJ-交易双方确认成交、拒绝成绩;回复方报价交谈、修改报价交谈、撤销报价交谈;询价放接收回复方发送的报价通知】接口
                com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteResponseBody quoteBody = this.convertToQuoteBodyForRepoQuoteResponse(repoDialogueQuoteDto, cfetsInputSuppleDto);
                return CfetsInterUtil.sendRepoQuoteResponse(quoteBody);
            }
        }

        return null;
    }

    /**
     * 转化现券对话报价业务数据到cfets接口dto（新增修改报价）
     * 
     * @param bondDialogueQuoteDto 现券对话报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private QuoteBody convertToQuoteBody(BondDialogueQuoteDto bondDialogueQuoteDto,
                                         CfetsInputSuppleDto inputSuppleDto) {
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = bondDialogueQuoteDto.getIbuQuoteBondDto();
        // 银行间报价金额基对手信息
        IbuQuoteBondDetDto ibuQuoteBondDetDto = bondDialogueQuoteDto.getIbuQuoteBondDetDto();
        
        boolean isAbs = false;
        if (inputSuppleDto.getFinProductType() != null && StringUtil.equals(
            com.joyintech.tams.constant.DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue(),
            inputSuppleDto.getFinProductType().getType6())) {
            isAbs = true;
        }

        QuoteBody quoteBody = new QuoteBody();

        quoteBody.setMarketIndicator("4"); // 10176域-市场标识(4-现券买卖) 应该接口里赋值
        quoteBody.setDataSourceString(null); // 10687域-报价来源(保留扩展字段)
        quoteBody.setQuoteType(1); // 537域-报价类型(1-对话报价) 应该接口里赋值
        quoteBody.setQuoteTransType(this.convertSystemCodeToCfetsCode("quoteTransType",
            ibuQuoteBondDto.getTradeStatus()).charAt(0)); // 10272域-操作类型(N:新增,R:修改)
        quoteBody.setClOrdID(ibuQuoteBondDto.getQuoteId()); // 11域-客户参考编号
        if(StringUtil.equals(DictCodeList.银行间上行操作类型_本方修改发送,
                bondDialogueQuoteDto.getIbuQuoteBondDto().getExeType())){
            quoteBody.setOrigClOrdID(ibuQuoteBondDto.getQuoteId()); // 41域-原客户参考编号
        }
        if (StringUtil.isNotBlank(ibuQuoteBondDto.getCfetsQuoteId())) {
            quoteBody.setQuoteID(ibuQuoteBondDto.getCfetsQuoteId()); // 117域-报价编号
        }
        quoteBody.setTransactTime(ibuQuoteBondDto.getTransactime()); // 60域-业务发生时间
        quoteBody.setSecurityID(inputSuppleDto.getSecurityID()); // 48域-债券代码
//        quoteBody.setSymbol(inputSuppleDto.getSymbol()); // 55域-债券名称
        quoteBody.setSide(
            this.convertSystemCodeToCfetsCode("side", ibuQuoteBondDto.getPs()).charAt(0)); // 54域-交易方向

        if (ibuQuoteBondDetDto.getUnitCprice() != null) {
            quoteBody.setPrice(ArithmeticUtil.round(ibuQuoteBondDetDto.getUnitCprice(), 4, RoundingMode.HALF_UP).doubleValue()); // 44域-净价
        }
//        if (ibuQuoteBondDetDto.getUnitFprice() != null) {
//            quoteBody.setDirtyPrice(ArithmeticUtil.round(ibuQuoteBondDetDto.getUnitFprice(), 4, RoundingMode.HALF_UP).doubleValue()); // 10048域-全价
//        }
        if (ibuQuoteBondDetDto.getUnitInt() != null) {
            quoteBody.setAccruedInterestAmt(ArithmeticUtil.round(ibuQuoteBondDetDto.getUnitInt(), 5, RoundingMode.HALF_UP).toString()); // 159域-应计利息
        }
        if (ibuQuoteBondDetDto.getParValueAmt() != null) {
            quoteBody.setOrderQty(ibuQuoteBondDetDto.getParValueAmt().toString()); // 38域-券面总额
        }
//        if (ibuQuoteBondDetDto.getFpriceAmt() != null) {
//            quoteBody.setTradeCashAmt(ibuQuoteBondDetDto.getFpriceAmt().toString()); // 10312域-交易金额
//        }
//        if (ibuQuoteBondDetDto.getIntAmt() != null) {
//            quoteBody.setAccruedInterestTotalAmt(ArithmeticUtil.round(ibuQuoteBondDetDto.getIntAmt(),2,RoundingMode.HALF_UP).toString()); // 10002域-应计利息总额
//        }
//        if (ibuQuoteBondDetDto.getFpriceAmt() != null) {
//            quoteBody.setSettlCurrAmt(ArithmeticUtil.round(ibuQuoteBondDetDto.getFpriceAmt(),2,RoundingMode.HALF_UP).toString()); // 119域-结算金额
//        }
//        quoteBody.setSettlCurrency(ibuQuoteBondDto.getSettCcy()); // 120域-结算币种
//        quoteBody.setSettlCurrFxRate(CfetsConstant.DEFALUT_SETTL_CURR_FXRATE); // 155域-汇率

        // if (StringUtil.equals(DictCodeListExt1.结算方式_见券付款.getValue(),
        // ibuQuoteBondDto.getDeliveryType())) {
        // quoteBody.setDeliveryType(4); // 919域-结算方式
        // } else if (StringUtil.equals(DictCodeListExt1.结算方式_见款付券.getValue(),
        // ibuQuoteBondDto.getDeliveryType())) {
        // quoteBody.setDeliveryType(5); // 919域-结算方式
        // } else if (StringUtil.equals(DictCodeListExt1.结算方式_券款对付.getValue(),
        // ibuQuoteBondDto.getDeliveryType())) {
        // quoteBody.setDeliveryType(0); // 919域-结算方式
        // }
        quoteBody.setDeliveryType(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("deliveryType", ibuQuoteBondDto.getDeliveryType()))); // 919域-结算方式

        // if (StringUtil.equals(DictCodeListExt1.清算类型_全额清算.getValue(),
        // ibuQuoteBondDto.getSettType())) {
        // quoteBody.setClearingMethod(13); // 11143域-清算类型
        // } else {
        // quoteBody.setClearingMethod(6); // 11143域-清算类型
        // }
        quoteBody.setClearingMethod(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("clearingMethod", ibuQuoteBondDto.getSettType()))); // 11143域-清算类型

        quoteBody.setSettlType(this.convertSystemCodeToCfetsCode("settlType",
            String.valueOf(ibuQuoteBondDto.getSettleSpeed()))); // 63域-清算速度

//        quoteBody.setSettlDate(
//            DateUtil.format(ibuQuoteBondDto.getSettleDate(), DateUtil.DEFAULT_OTHER_FORMAT)); // 64域-结算日

//        if (ibuQuoteBondDetDto.getParValue() != null) {
//            quoteBody.setPrincipal(ArithmeticUtil.round(ibuQuoteBondDetDto.getParValue(), 2, RoundingMode.HALF_UP).toString()); // 10239域-每百元本金额
//        }
//        if (ibuQuoteBondDetDto.getPrinAmt() != null) {
//            quoteBody.setTotalPrincipal(ArithmeticUtil.round(ibuQuoteBondDetDto.getPrinAmt(), 2, RoundingMode.HALF_UP).toString()); // 10310域-总本金或者当前本金总额
//        }
        quoteBody.setUserReference1(null); // 11405域-用户参考数据1
        quoteBody.setUserReference2(null); // 11406域-用户参考数据2
        quoteBody.setUserReference3(null); // 11407域-用户参考数据3
        quoteBody.setUserReference4(null); // 11408域-用户参考数据4s
        quoteBody.setUserReference5(null); // 11409域-用户参考数据5
        quoteBody.setUserReference6(null); // 11410域-用户参考数据6
        //update by zhangnian  20200123 start
        //债券对话报价修改，发送报文s域不对。以下域不要传。
        if(StringUtil.equals(DictCodeList.银行间上行操作类型_本方修改发送.getValue(),
                bondDialogueQuoteDto.getIbuQuoteBondDto().getExeType())){
            quoteBody.setDirtyPrice(null);
            quoteBody.setSymbol(null);
            quoteBody.setTradeCashAmt(null);
            quoteBody.setAccruedInterestTotalAmt(null);
            quoteBody.setSettlCurrAmt(null);
            quoteBody.setSettlDate(null);
            quoteBody.setPrincipal(null);
            quoteBody.setTotalPrincipal(null);
        }
        //update by zhangnian 20200123  end
        
        if (!isAbs) {
            if (ibuQuoteBondDetDto.getYield() != null) {
                quoteBody.setYieldType("MATURITY"); // 235域-到期收益率
                quoteBody.setYield(ArithmeticUtil.round(
                    ArithmeticUtil.multiply(ibuQuoteBondDetDto.getYield(), BigDecimal.valueOf(100)), 4,
                    RoundingMode.HALF_UP).toString()); // 236域-到期收益率值
            }
            
            if (ibuQuoteBondDetDto.getExerYield() != null) {
                // 232域-重复组个数-该场景取值固定为 1
                List<NoStipulationDto> noStipulations = new ArrayList<>();
                NoStipulationDto noStipulationDto = new NoStipulationDto();
                noStipulationDto.setStipulationType("StrikeYield"); // 233域-行权收益率
                noStipulationDto.setStipulationValue(ArithmeticUtil.round(
                    ArithmeticUtil.multiply(ibuQuoteBondDetDto.getExerYield(), BigDecimal.valueOf(100)), 4,
                    RoundingMode.HALF_UP).toString()); // 234域-行权收益率值
                noStipulations.add(noStipulationDto);
                quoteBody.setNoStipulations(noStipulations);
            }
        }

        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDto.getOuPartyId()); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(101); // 452域-101-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);

        // 接收方信息（接收方为交易对手）
        noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDetDto.getCpPartyId()); // 448域-接收方交易账户 21 位码
        noPartyIDDto.setPartyRole(102); // 452域-102-接收方

        noPartySubIDs = new ArrayList<>();
        noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDetDto.getCpTrader()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);
        quoteBody.setNoPartyIDs(noPartyIDs);

        return quoteBody;
    }

    /**
     * 转化现券对话报价业务数据到cfets接口dto（撤销报价）
     * 
     * @param bondDialogueQuoteDto 现券对话报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private QuoteCancelBody convertToQuoteBodyForQuoteCancel(BondDialogueQuoteDto bondDialogueQuoteDto,
                                                             CfetsInputSuppleDto inputSuppleDto) {
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = bondDialogueQuoteDto.getIbuQuoteBondDto();

        QuoteCancelBody quoteBody = new QuoteCancelBody();

//        quoteBody.setSendingTime(DateUtil.format(DateUtil.now(), CfetsConstant.DATETIME_FORMAT)); // 52域-发送时间
        quoteBody.setQuoteType(1); // 537域-报价类型(1-对话报价) 应该接口里赋值
        quoteBody.setClOrdID(ibuQuoteBondDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setOrigClOrdID(ibuQuoteBondDto.getQuoteId()); // 41域-原客户参考编号
        quoteBody.setQuoteID(ibuQuoteBondDto.getCfetsQuoteId()); // 117域-报价编号
        quoteBody.setTransactTime(ibuQuoteBondDto.getTransactime()); // 60域-业务发生时间

        // 295域
        List<NoQuoteEntrieDto> noQuoteEntries = new ArrayList<>();
        NoQuoteEntrieDto noQuoteEntrieDto = new NoQuoteEntrieDto();
        noQuoteEntrieDto.setSymbol(CfetsConstant.MUST_ITEM_DEFAULT_VALUE); // 55域-债券名称
        noQuoteEntrieDto.setSecurityID(inputSuppleDto.getSecurityID()); // 48域-债券代码
        noQuoteEntrieDto.setMarketIndicator("4"); // 10176域-市场标识: 现券买卖
        noQuoteEntries.add(noQuoteEntrieDto);

        quoteBody.setNoQuoteEntries(noQuoteEntries);

        return quoteBody;
    }

    /**
     * 转化现券对话报价业务数据到cfets接口dto（确认或拒绝成交）
     * 
     * @param bondDialogueQuoteDto 现券对话报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private QuoteResponseBody convertToQuoteBodyForQuoteResponse(BondDialogueQuoteDto bondDialogueQuoteDto,
                                                                 CfetsInputSuppleDto inputSuppleDto) {
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = bondDialogueQuoteDto.getIbuQuoteBondDto();
        // 银行间报价金额基对手信息
        IbuQuoteBondDetDto ibuQuoteBondDetDto = bondDialogueQuoteDto.getIbuQuoteBondDetDto();

        QuoteResponseBody quoteBody = new QuoteResponseBody();

        quoteBody.setMarketIndicator("4"); // 10176域-市场标识(4-现券买卖) 应该接口里赋值
        quoteBody.setQuoteType(1); // 537域-报价类型(1-对话报价) 应该接口里赋值
        quoteBody.setQuoteRespType(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("quoteRespType", ibuQuoteBondDto.getExeType()))); // 694域-操作类型(1:成交确认,7:报价拒绝)
        quoteBody.setClOrdID(ibuQuoteBondDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setQuoteID(ibuQuoteBondDto.getCfetsQuoteId()); // 117域-报价编号
        quoteBody.setTransactTime(ibuQuoteBondDto.getTransactime()); // 60域-业务发生时间
        quoteBody.setSecurityID(inputSuppleDto.getSecurityID()); // 48域-债券代码
        quoteBody.setUserReference1(null); // 11405域-用户参考数据1
        quoteBody.setUserReference2(null); // 11406域-用户参考数据2
        quoteBody.setUserReference3(null); // 11407域-用户参考数据3
        quoteBody.setUserReference4(null); // 11408域-用户参考数据4
        quoteBody.setUserReference5(null); // 11409域-用户参考数据5
        quoteBody.setUserReference6(null); // 11410域-用户参考数据6

        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为交易对手）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDto.getOuPartyId()); // 448域-发送方交易账户 21 位码
        noPartyIDDto.setPartyRole(101); // 452域-101-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);

        // 接收方信息（接收方为本方）
        noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDetDto.getCpPartyId()); // 448域-接收方交易账户 21 位码
        noPartyIDDto.setPartyRole(102); // 452域-102-接收方

        noPartySubIDs = new ArrayList<>();
        noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDetDto.getCpTrader()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);
        quoteBody.setNoPartyIDs(noPartyIDs);

        return quoteBody;
    }

    /**
     * 转化现券请求报价业务数据到cfets接口dto（新增请求报价）
     * 
     * @param bondRequestQuoteDto 现券请求报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private QuoteRequestBody convertToQuoteBodyForQuoteRequest(BondRequestQuoteDto bondRequestQuoteDto,
                                         CfetsInputSuppleDto inputSuppleDto) {
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = bondRequestQuoteDto.getIbuQuoteBondDto();
        // 银行间报价金额基对手信息列表
        List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList = bondRequestQuoteDto.getIbuQuoteBondDetDtoList();

        QuoteRequestBody quoteBody = new QuoteRequestBody();
        
        quoteBody.setClOrdID(ibuQuoteBondDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setQueryRequestID(null); // 10168域-查询请求编号 
        quoteBody.setAllocInd("0"); // 11960域-分账标识：0 普通请求报价 无allocation
        quoteBody.setQuoteReqID(null); // 131域-请求报价编号
        
        quoteBody.setUserReference1(null); // 11405域-用户参考数据1
        quoteBody.setUserReference2(null); // 11406域-用户参考数据2
        quoteBody.setUserReference3(null); // 11407域-用户参考数据3
        quoteBody.setUserReference4(null); // 11408域-用户参考数据4
        quoteBody.setUserReference5(null); // 11409域-用户参考数据5
        quoteBody.setUserReference6(null); // 11410域-用户参考数据6
        
        // 62域-报价有效时间
        Date tempEffTime = null;
            
        // 有效时间不能大于当前时间+2小时
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 2);
        // 需要判断是否大于系统参数，如果大于系统参数，则置为系统参数
        Date effTime = DateUtil.parse(DateUtil.format(new Date()) + " " + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00");
        bondRequestQuoteDto.getIbuQuoteBondDto().setEffTime(calendar.getTime());
        tempEffTime = calendar.getTime();
        if (calendar.getTime().compareTo(effTime) > 0) {
            bondRequestQuoteDto.getIbuQuoteBondDto().setEffTime(effTime);
            tempEffTime = effTime;
        }
        
        NoRelatedSymDto noRelatedSymDto = new NoRelatedSymDto();
        noRelatedSymDto.setSymbol(CfetsConstant.MUST_ITEM_DEFAULT_VALUE); // 55域-债券名称
        noRelatedSymDto.setSecurityID(inputSuppleDto.getSecurityID()); // 48域-债券代码
        noRelatedSymDto.setMarketIndicator("4"); // 10176域-市场标识(4-现券买卖) 应该接口里赋值
        noRelatedSymDto.setQuoteType(112); // 537域-报价类型(112-请求报价) 应该接口里赋值
//        noRelatedSymDto.setQuoteStatus(16); // 297域-报价状态(16:正常,19:撤销,5:拒绝,21:过期,107:全部成交)

        noRelatedSymDto.setValidUntilTime(
            DateUtil.format(tempEffTime, CfetsConstant.DATETIME_FORMAT)); // 62域-报价有效时间
        
//        noRelatedSymDto.setTransactTime(
//            DateUtil.format(ibuQuoteBondDto.getUpdateTime(), CfetsConstant.DATETIME_FORMAT)); // 60域-业务发生时间
        noRelatedSymDto.setSide(
            this.convertSystemCodeToCfetsCode("side", ibuQuoteBondDto.getPs()).charAt(0)); // 54域-交易方向

//        if (ibuQuoteBondDetDto.getUnitInt() != null) {
//            noRelatedSymDto.setAccruedInterestAmt(ibuQuoteBondDetDto.getUnitInt().toString()); // 159域-应计利息
//        }
//        if (ibuQuoteBondDetDto.getIntAmt() != null) {
//            noRelatedSymDto.setAccruedInterestTotalAmt(ibuQuoteBondDetDto.getIntAmt().toString()); // 10002域-应计利息总额
//        }
        if (ibuQuoteBondDto.getShareAmt() != null) {
            noRelatedSymDto.setOrderQty(ArithmeticUtil.multiply(ibuQuoteBondDto.getShareAmt(), BigDecimal.valueOf(100)).toString()); // 38域-券面总额
        }
        noRelatedSymDto.setDeliveryType(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("deliveryType", ibuQuoteBondDto.getDeliveryType()))); // 919域-结算方式
        noRelatedSymDto.setClearingMethod(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("clearingMethod", ibuQuoteBondDto.getSettType()))); // 11143域-清算类型
        noRelatedSymDto.setSettlType(this.convertSystemCodeToCfetsCode("settlType",
            String.valueOf(ibuQuoteBondDto.getSettleSpeed()))); // 63域-清算速度
//        noRelatedSymDto.setSettlDate(
//            DateUtil.format(ibuQuoteBondDto.getSettleDate(), DateUtil.DEFAULT_OTHER_FORMAT)); // 64域-结算日
//        if (ibuQuoteBondDetDto.getParValue().toString() != null) {
//            noRelatedSymDto.setPrincipal(ibuQuoteBondDetDto.getParValue().toString()); // 10239域-每百元本金额
//        }
//        if (ibuQuoteBondDetDto.getPrinAmt().toString() != null) {
//            noRelatedSymDto.setTotalPrincipal(ibuQuoteBondDetDto.getPrinAmt().toString()); // 10310域-总本金或者当前本金总额
//        }
        
        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDto.getOuPartyId()); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(101); // 452域-101-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);
        noPartyIDs.add(noPartyIDDto);
        
        // 146域
        List<NoRelatedSymDto> noRelatedSym = new ArrayList<>();
        
        if (!StringUtil.isEmptyList(ibuQuoteBondDetDtoList)) {
            for (IbuQuoteBondDetDto ibuQuoteBondDetDto : ibuQuoteBondDetDtoList) {
                // 接收方信息（接收方为交易对手）
                noPartyIDDto = new NoPartyIDDto();
                noPartyIDDto.setPartyID(ibuQuoteBondDetDto.getCpPartyId()); // 448域-接收方交易账户 21 位码
                noPartyIDDto.setPartyRole(102); // 452域-102-接收方
                noPartyIDs.add(noPartyIDDto);
            }
        }
        
        noRelatedSymDto.setNoPartyIDs(noPartyIDs);
        
        noRelatedSymDto.setNoAllocDtos(null); // 78域
        
        noRelatedSym.add(noRelatedSymDto);
        
        quoteBody.setNoRelatedSym(noRelatedSym);

        return quoteBody;
    }
    
    /**
     * 转化现券请求报价业务数据到cfets接口dto（本方请求报价撤回）
     * 
     * @param bondRequestQuoteDto 现券请求报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private QuoteRequestCancelBody convertToQuoteBodyForQuoteRequestCancel(BondRequestQuoteDto bondRequestQuoteDto,
                                         CfetsInputSuppleDto inputSuppleDto) {
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = bondRequestQuoteDto.getIbuQuoteBondDto();
//        // 银行间报价金额基对手信息列表
//        List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList = bondRequestQuoteDto.getIbuQuoteBondDetDtoList();

        QuoteRequestCancelBody quoteBody = new QuoteRequestCancelBody();
        
        quoteBody.setClOrdID(ibuQuoteBondDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setOrigClOrdID(ibuQuoteBondDto.getQuoteId()); // 41域-原客户参考编号
        quoteBody.setQuoteReqID(ibuQuoteBondDto.getCfetsQuoteId()); // 131域-请求报价编号
        
        // 146域
        List<NoRelatedSymReqCxlDto> noRelatedSym = new ArrayList<>();
        
        NoRelatedSymReqCxlDto noRelatedSymReqCxlDto = new NoRelatedSymReqCxlDto();
        noRelatedSymReqCxlDto.setSymbol(CfetsConstant.MUST_ITEM_DEFAULT_VALUE); // 55域-债券名称
        noRelatedSymReqCxlDto.setSecurityID(inputSuppleDto.getSecurityID()); // 48域-债券代码
        noRelatedSymReqCxlDto.setMarketIndicator("4"); // 10176域-市场标识(4-现券买卖) 应该接口里赋值
        noRelatedSymReqCxlDto.setQuoteType(112); // 537域-报价类型(112-请求报价) 应该接口里赋值
//        noRelatedSymReqCxlDto.setQuoteStatus(19); // 297域-报价状态(16:正常,19:撤销,5:拒绝,21:过期,107:全部成交)
        noRelatedSymReqCxlDto.setTransactTime(ibuQuoteBondDto.getTransactime()); // 60域-业务发生时间
//        if (ibuQuoteBondDetDto.getUnitCprice() != null) {
//            noRelatedSymReqCxlDto.setPrice(ibuQuoteBondDetDto.getUnitCprice().doubleValue()); // 44域-净价
//        }
        
        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDto.getOuPartyId()); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(101); // 452域-101-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);
        noPartyIDs.add(noPartyIDDto);

        noRelatedSymReqCxlDto.setNoPartyIDs(noPartyIDs);
        
        noRelatedSym.add(noRelatedSymReqCxlDto);
        
        quoteBody.setNoRelatedSym(noRelatedSym);

        return quoteBody;
    }
    
    /**
     * 转化现券请求报价业务数据到cfets接口dto（本方请求回复成交）
     * 
     * @param bondRequestQuoteDto 现券请求报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.QuoteResponseBody convertToQuoteBodyForQuoteRequestResponse(BondRequestQuoteDto bondRequestQuoteDto,
                                         CfetsInputSuppleDto inputSuppleDto) {
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = bondRequestQuoteDto.getIbuQuoteBondDto();
        // 银行间报价金额基对手信息列表
        List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList = bondRequestQuoteDto.getIbuQuoteBondDetDtoList();
        IbuQuoteBondDetDto ibuQuoteBondDetDto = null;
        if (!StringUtil.isEmptyList(ibuQuoteBondDetDtoList)) {
//            ibuQuoteBondDetDto = ibuQuoteBondDetDtoList.get(0);
            ibuQuoteBondDetDto = ibuQuoteBondDetDtoList.stream().filter(x -> StringUtil.equals(
                DictCodeList.银行间上行报价状态_成交中.getValue(), x.getTradeStatus())).findFirst().get();
        }

        com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.QuoteResponseBody quoteBody = new com.joyintech.service.bankuplink.cfets.dto.cashtransaction.requestquotation.QuoteResponseBody();
        
        quoteBody.setMarketIndicator("4"); // 10176域-市场标识(4-现券买卖) 应该接口里赋值
        quoteBody.setQuoteType(113); // 537域-报价类型(113-请求回复) 应该接口里赋值
        quoteBody.setQuoteRespType(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("quoteRespType", ibuQuoteBondDto.getExeType()))); // 694域-操作类型(1:成交确认)
        quoteBody.setClOrdID(ibuQuoteBondDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setAllocInd("0"); // 11960域-分账标识：0 普通请求报价 无allocation
        quoteBody.setQuoteID(ibuQuoteBondDetDto.getCfetsReqId()); // 117域-请求回复编号
        quoteBody.setTransactTime(ibuQuoteBondDetDto.getTransactime()); // 60域-业务发生时间
        quoteBody.setSecurityID(inputSuppleDto.getSecurityID()); // 48域-债券代码
        quoteBody.setSide(
            this.convertSystemCodeToCfetsCode("side", ibuQuoteBondDto.getPs()).charAt(0)); // 54域-交易方向
        
        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteBondDto.getOuPartyId()); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(101); // 452域-101-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteBondDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);
        
        noPartyIDs.add(noPartyIDDto);

        quoteBody.setNoPartyIDs(noPartyIDs);

        return quoteBody;
    }

    /**
     * 转化现券对话报价业务数据到cfets接口dto（新增修改报价、交谈报价）
     * 
     * @param repoDialogueQuoteDto 现券对话报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteBody convertToQuoteBodyForRepo(RepoDialogueQuoteDto repoDialogueQuoteDto,
                                         CfetsInputSuppleDto inputSuppleDto) {
        // 回购报价主信息
        IbuQuoteCrepDto ibuQuoteCrepDto = repoDialogueQuoteDto.getIbuQuoteCrepDto();
        // 质押品信息
        List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList();

        com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteBody quoteBody = new com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteBody();

        quoteBody.setMarketIndicator("9"); // 10176域-市场标识(9-质押式回购)
        quoteBody.setTradeRecordID(null); // 11888域-交易记录编号
        quoteBody.setChannel("CFETS"); // 11911域-渠道(iDeal、CFETS)
        quoteBody.setQuoteType(1); // 537域-报价类型(1-对话报价) 应该接口里赋值
        quoteBody.setQuoteTransType(this.convertSystemCodeToCfetsCode("quoteTransType",
            ibuQuoteCrepDto.getTradeStatus()).charAt(0)); // 10272域-操作类型(N:新增,R:修改,1:交谈)
        // 发送修改报价交谈的S报文的操作类型quoteTransType目前是1（交谈），正常应该是R（修改）
        if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈修改.getValue(),
            repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
            || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue(),
                repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())) {
            quoteBody.setQuoteTransType('R');
        }
        
//        quoteBody.setQuoteStatus(16); // 297域-报价状态(16:正常,19:撤销,5:拒绝,21:过期,107:全部成交)
        quoteBody.setClOrdID(ibuQuoteCrepDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setQueryRequestID(null); // 10168域-查询请求编号
//        quoteBody.setOrigClOrdID(ibuQuoteCrepDto.getQuoteId()); // 41域-原客户参考编号
        if (StringUtil.isNotBlank(ibuQuoteCrepDto.getCfetsQuoteId())) {
            quoteBody.setQuoteID(ibuQuoteCrepDto.getCfetsQuoteId()); // 117域-报价编号
        }
        quoteBody.setTransactTime(ibuQuoteCrepDto.getTransactime()); // 60域-业务发生时间
//        quoteBody.setQuoteTime(DateUtil.format(DateUtil.now(), CfetsConstant.DATETIME_FORMAT)); // 10271域-报价发送时间
//        quoteBody.setNegotiationCount(1); // 10192域-交谈轮次
        quoteBody.setSide(
            this.convertSystemCodeToCfetsCode("side", ibuQuoteCrepDto.getRepoPs()).charAt(0)); // 54域-交易方向
        quoteBody.setRepoMethod('1'); // 11376域-回购方式: 1-双边回购
        quoteBody.setTradeLimitDays(String.valueOf(ibuQuoteCrepDto.getTerm())); // 10316域-回购期限
        if (ibuQuoteCrepDto.getRate() != null) {
            quoteBody.setPrice(ArithmeticUtil.round(ArithmeticUtil.multiply(ibuQuoteCrepDto.getRate(), BigDecimal.valueOf(100)),4,RoundingMode.HALF_UP).doubleValue()); // 44域-回购利率
        }
        if (ibuQuoteCrepDto.getFpriceAmt() != null) {
            quoteBody.setTradeCashAmt(ibuQuoteCrepDto.getFpriceAmt().toString()); // 10312域-交易金额
        }
        quoteBody.setSettlType(this.convertSystemCodeToCfetsCode("settlType",
            String.valueOf(ibuQuoteCrepDto.getSettleSpeed()))); // 63域-清算速度
        quoteBody.setDeliveryType(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("deliveryType", ibuQuoteCrepDto.getvDeliveryType()))); // 919域-首期结算方式
        quoteBody.setDeliveryType2(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("deliveryType", ibuQuoteCrepDto.getmDeliveryType()))); // 10045域-到期结算方式
        quoteBody.setClearingMethod(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("clearingMethod", ibuQuoteCrepDto.getSettType()))); // 11143域-清算类型
        quoteBody.setSettlCurrency(ibuQuoteCrepDto.getSettCcy()); // 120域-结算币种
//        quoteBody.setSettlCurrFxRate(CfetsConstant.DEFALUT_SETTL_CURR_FXRATE); // 155域-汇率
        quoteBody.setUserReference1(null); // 11405域-用户参考数据1
        quoteBody.setUserReference2(null); // 11406域-用户参考数据2
        quoteBody.setUserReference3(null); // 11407域-用户参考数据3
        quoteBody.setUserReference4(null); // 11408域-用户参考数据4
        quoteBody.setUserReference5(null); // 11409域-用户参考数据5
        quoteBody.setUserReference6(null); // 11410域-用户参考数据6
        
        // 711域-质押券
        List<NoUnderlyingDto> noUnderlyings = new ArrayList<>();
        
        if (!StringUtil.isEmptyList(ibuQuoteCrepPledgeDtoList)) {
            for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(ibuQuoteCrepPledgeDto.getPledgeFinprodId());
                
                NoUnderlyingDto noUnderlyingDto = new NoUnderlyingDto();
                if (StringUtil.isNotBlank(finProduct.getFinprodAbbr())) {
                    noUnderlyingDto.setUnderlyingSymbol(finProduct.getFinprodAbbr()); // 311域-质押券名称(组件必传域,无值时传输“-”)
                    noUnderlyingDto.setUnderlyingSecurityID(
                        StringUtil.splitToList(".", finProduct.getFinprodMarketId()).get(0)); // 309域-质押券代码
                }
                else {
                    noUnderlyingDto.setUnderlyingSymbol(CfetsConstant.MUST_ITEM_DEFAULT_VALUE); // 311域-质押券名称(组件必传域,无值时传输“-”)
                }
                
                noUnderlyingDto.setUnderlyingQty(ArithmeticUtil.round(
                    ArithmeticUtil.multiply(ibuQuoteCrepPledgeDto.getPledgeNumber(),
                        new BigDecimal(100)),
                    0, RoundingMode.HALF_UP).toString()); // 879域-质押券券面总额
                
                // 887域
                List<NoUnderlyingStipDto> noUnderlyingStips = new ArrayList<>();
                NoUnderlyingStipDto noUnderlyingStipDto = new NoUnderlyingStipDto();
                noUnderlyingStipDto.setUnderlyingStipType("HAIRCUT"); // 888域-折算比例
                noUnderlyingStipDto.setUnderlyingStipValue(ArithmeticUtil.round(
                    ArithmeticUtil.multiply(ibuQuoteCrepPledgeDto.getPledgeRatio(),
                        new BigDecimal(100)),
                    2, RoundingMode.HALF_UP).toString()); // 889域-折算比例值
                noUnderlyingStips.add(noUnderlyingStipDto);
                noUnderlyingDto.setNoUnderlyingStips(noUnderlyingStips);
                
                noUnderlyings.add(noUnderlyingDto);
            }
            quoteBody.setNoUnderlyings(noUnderlyings);
        }
        
        if (ibuQuoteCrepDto.getCbFpriceAmt() != null && ibuQuoteCrepDto.getScFpriceAmt() != null
                && BigDecimal.ZERO.compareTo(ibuQuoteCrepDto.getCbFpriceAmt()) < 0
                && BigDecimal.ZERO.compareTo(ibuQuoteCrepDto.getScFpriceAmt()) < 0) {
            // 12048域-跨托管信息
            List<NoCrossCustodianDto> noCrossCustodians = new ArrayList<>();
            NoCrossCustodianDto noCrossCustodianDto = new NoCrossCustodianDto();
            noCrossCustodianDto.setCrossCustodianInstitutionName("3"); // 12049域-跨托管机构名称(3:中债登)
            noCrossCustodianDto.setCrossCustodianTradeAmt(ibuQuoteCrepDto.getCbFpriceAmt().toString()); // 12050域-跨托管交易金额
            noCrossCustodians.add(noCrossCustodianDto);
            
            noCrossCustodianDto = new NoCrossCustodianDto();
            noCrossCustodianDto.setCrossCustodianInstitutionName("4"); // 12049域-跨托管机构名称(4:上清所)
            noCrossCustodianDto.setCrossCustodianTradeAmt(ibuQuoteCrepDto.getScFpriceAmt().toString()); // 12050域-跨托管交易金额
            noCrossCustodians.add(noCrossCustodianDto);
            quoteBody.setNoCrossCustodians(noCrossCustodians);
        }
        
        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteCrepDto.getOuPartyId()); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(1); // 452域-1-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteCrepDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);

        // 接收方信息（接收方为交易对手）
        noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteCrepDto.getCpPartyId()); // 448域-接收方交易账户 21 位码
        noPartyIDDto.setPartyRole(17); // 452域-17-对手方

        noPartySubIDs = new ArrayList<>();
        noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteCrepDto.getCpTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);
        quoteBody.setNoPartyIDs(noPartyIDs);

        return quoteBody;
    }

    /**
     * 转化质押式回购对话报价业务数据到cfets接口dto（询价方撤销报价或撤销报价交谈）
     * 
     * @param repoDialogueQuoteDto 质押式回购对话报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteCancelBody convertToQuoteBodyForRepoQuoteCancel(RepoDialogueQuoteDto repoDialogueQuoteDto,
                                                             CfetsInputSuppleDto inputSuppleDto) {
        // 回购报价主信息
        IbuQuoteCrepDto ibuQuoteCrepDto = repoDialogueQuoteDto.getIbuQuoteCrepDto();

        com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteCancelBody quoteBody = new com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteCancelBody();

        quoteBody.setTradeRecordID(null); // 11888域-交易记录编号
        quoteBody.setQuoteType(1); // 537域-报价类型(1-对话报价) 应该接口里赋值
        quoteBody.setClOrdID(ibuQuoteCrepDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setQuoteID(ibuQuoteCrepDto.getCfetsQuoteId()); // 117域-报价编号
        quoteBody.setTransactTime(ibuQuoteCrepDto.getTransactime()); // 60域-业务发生时间
        
        // 295域
        List<com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoQuoteEntrieDto> noQuoteEntries = new ArrayList<>();
        com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoQuoteEntrieDto noQuoteEntrieDto = new com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoQuoteEntrieDto();
        noQuoteEntrieDto.setSymbol(CfetsConstant.MUST_ITEM_DEFAULT_VALUE); // 55域-债券名称
        noQuoteEntrieDto.setMarketIndicator("9"); // 10176域-市场标识: 9-质押式回购
        noQuoteEntries.add(noQuoteEntrieDto);
        quoteBody.setNoQuoteEntries(noQuoteEntries);
        
        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteCrepDto.getOuPartyId()); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(1); // 452域-1-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteCrepDto.getOuTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);

        // 接收方信息（接收方为交易对手）
        noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(ibuQuoteCrepDto.getCpPartyId()); // 448域-接收方交易账户 21 位码
        noPartyIDDto.setPartyRole(17); // 452域-17-对手方

        noPartySubIDs = new ArrayList<>();
        noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(ibuQuoteCrepDto.getCpTraderId()); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);
        quoteBody.setNoPartyIDs(noPartyIDs);

        return quoteBody;
    }
    
    /**
     * 转化回购对话报价业务数据到cfets接口dto（AJ-交易双方确认成交、拒绝成交;回复方报价交谈、修改报价交谈、撤销报价交谈;询价放接收回复方发送的报价通知）
     * 
     * @param repoDialogueQuoteDto 回购对话报价业务数据
     * @param inputSuppleDto 业务数据以外，补充信息dto
     * @return cfets接口dto
     */
    private com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteResponseBody convertToQuoteBodyForRepoQuoteResponse(RepoDialogueQuoteDto repoDialogueQuoteDto,
                                                                 CfetsInputSuppleDto inputSuppleDto) {
        // 回购报价主信息
        IbuQuoteCrepDto ibuQuoteCrepDto = repoDialogueQuoteDto.getIbuQuoteCrepDto();
        // 质押品信息
        List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList();

        com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteResponseBody quoteBody = new com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.QuoteResponseBody();

        quoteBody.setMarketIndicator("9"); // 10176域-市场标识(9-质押式回购)
        quoteBody.setTradeRecordID(null); // 11888域-交易记录编号
        
        if (!(StringUtil.equals(DictCodeList.银行间上行操作类型_本方回复拒绝.getValue(),
            repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
            || StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType()))) {
            quoteBody.setChannel("CFETS"); // 11911域-渠道(iDeal、CFETS)
        }
        
        quoteBody.setQuoteType(1); // 537域-报价类型(1-对话报价)
        quoteBody.setQuoteRespType(Integer.valueOf(
            this.convertSystemCodeToCfetsCode("quoteRespType", ibuQuoteCrepDto.getExeType()))); // 694域-操作类型((1-成交确认,7-报价拒绝,2-交谈,101-修改,102-撤销))
//        quoteBody.setQuoteStatus(16); // 297域-报价状态(16:正常,19:撤销,5:拒绝,21:过期,107:全部成交)
        quoteBody.setClOrdID(ibuQuoteCrepDto.getQuoteId()); // 11域-客户参考编号
        quoteBody.setQuoteID(ibuQuoteCrepDto.getCfetsQuoteId()); // 117域-报价编号
        quoteBody.setTransactTime(ibuQuoteCrepDto.getTransactime()); // 60域-业务发生时间
        
        if (StringUtil.equals(repoDialogueQuoteDto.getIbuQuoteCrepDto().getQuoteRoleType(),
            DictCodeList.银行间上行本方角色_回复方.getValue())) {
            if (StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价交谈.getValue(),
                repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈发送.getValue(),
                    repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈修改.getValue(),
                    repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
                    || StringUtil.equals(DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue(),
                        repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())) {
                quoteBody.setSide(
                    this.convertSystemCodeToCfetsCode("side", CfetsDataConvertUtil.convertRepoPs(ibuQuoteCrepDto.getRepoPs())).charAt(0)); // 54域-交易方向
                quoteBody.setRepoMethod('1'); // 11376域-回购方式: 1-双边回购
                quoteBody.setTradeLimitDays(String.valueOf(ibuQuoteCrepDto.getTerm())); // 10316域-回购期限
                if (ibuQuoteCrepDto.getRate() != null) {
                    quoteBody.setPrice(ArithmeticUtil.round(ArithmeticUtil.multiply(ibuQuoteCrepDto.getRate(), BigDecimal.valueOf(100)),4,RoundingMode.HALF_UP).doubleValue()); // 44域-回购利率
                }
                if (ibuQuoteCrepDto.getFpriceAmt() != null) {
                    quoteBody.setTradeCashAmt(ibuQuoteCrepDto.getFpriceAmt().toString()); // 10312域-交易金额
                }
                quoteBody.setSettlType(this.convertSystemCodeToCfetsCode("settlType",
                    String.valueOf(ibuQuoteCrepDto.getSettleSpeed()))); // 63域-清算速度
                quoteBody.setDeliveryType(Integer.valueOf(
                    this.convertSystemCodeToCfetsCode("deliveryType", ibuQuoteCrepDto.getvDeliveryType()))); // 919域-首期结算方式
                quoteBody.setDeliveryType2(Integer.valueOf(
                    this.convertSystemCodeToCfetsCode("deliveryType", ibuQuoteCrepDto.getmDeliveryType()))); // 10045域-到期结算方式
                quoteBody.setClearingMethod(Integer.valueOf(
                    this.convertSystemCodeToCfetsCode("clearingMethod", ibuQuoteCrepDto.getSettType()))); // 11143域-清算类型
                quoteBody.setSettlCurrency(ibuQuoteCrepDto.getSettCcy()); // 120域-结算币种
            }
        }
//        quoteBody.setSettlCurrFxRate(CfetsConstant.DEFALUT_SETTL_CURR_FXRATE); // 155域-汇率
        quoteBody.setUserReference1(null); // 11405域-用户参考数据1
        quoteBody.setUserReference2(null); // 11406域-用户参考数据2
        quoteBody.setUserReference3(null); // 11407域-用户参考数据3
        quoteBody.setUserReference4(null); // 11408域-用户参考数据4
        quoteBody.setUserReference5(null); // 11409域-用户参考数据5
        quoteBody.setUserReference6(null); // 11410域-用户参考数据6
        
        if (!(StringUtil.equals(DictCodeList.银行间上行操作类型_本方回复拒绝.getValue(),
            repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType())
            || StringUtil.equals(DictCodeList.银行间上行操作类型_本方报价撤回.getValue(),
                repoDialogueQuoteDto.getIbuQuoteCrepDto().getExeType()))) {
            // 711域-质押券
            List<NoUnderlyingDto> noUnderlyings = new ArrayList<>();
            
            if (!StringUtil.isEmptyList(ibuQuoteCrepPledgeDtoList)) {
                for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList) {
                    FinProduct finProduct = finProductMapper.selectByPrimaryKey(ibuQuoteCrepPledgeDto.getPledgeFinprodId());
                    
                    NoUnderlyingDto noUnderlyingDto = new NoUnderlyingDto();
                    if (StringUtil.isNotBlank(finProduct.getFinprodAbbr())) {
                        noUnderlyingDto.setUnderlyingSymbol(finProduct.getFinprodAbbr()); // 311域-质押券名称(组件必传域,无值时传输“-”)
                        noUnderlyingDto.setUnderlyingSecurityID(
                            StringUtil.splitToList(".", finProduct.getFinprodMarketId()).get(0)); // 309域-质押券代码
                    }
                    else {
                        noUnderlyingDto.setUnderlyingSymbol(CfetsConstant.MUST_ITEM_DEFAULT_VALUE); // 311域-质押券名称(组件必传域,无值时传输“-”)
                    }
                    
                    noUnderlyingDto.setUnderlyingQty(ArithmeticUtil.round(
                        ArithmeticUtil.multiply(ibuQuoteCrepPledgeDto.getPledgeNumber(),
                            new BigDecimal(100)),
                        0, RoundingMode.HALF_UP).toString()); // 879域-质押券券面总额
                    
                    // 887域
                    List<NoUnderlyingStipDto> noUnderlyingStips = new ArrayList<>();
                    NoUnderlyingStipDto noUnderlyingStipDto = new NoUnderlyingStipDto();
                    noUnderlyingStipDto.setUnderlyingStipType("HAIRCUT"); // 888域-折算比例
                    noUnderlyingStipDto.setUnderlyingStipValue(ArithmeticUtil.round(
                        ArithmeticUtil.multiply(ibuQuoteCrepPledgeDto.getPledgeRatio(),
                            new BigDecimal(100)),
                        2, RoundingMode.HALF_UP).toString()); // 889域-折算比例值
                    noUnderlyingStips.add(noUnderlyingStipDto);
                    noUnderlyingDto.setNoUnderlyingStips(noUnderlyingStips);
                    
                    noUnderlyings.add(noUnderlyingDto);
                }
                quoteBody.setNoUnderlyings(noUnderlyings);
            }
            
            // 12048域-跨托管信息
            if (ibuQuoteCrepDto.getCbFpriceAmt() != null && ibuQuoteCrepDto.getScFpriceAmt() != null) {
                List<NoCrossCustodianDto> noCrossCustodians = new ArrayList<>();
                NoCrossCustodianDto noCrossCustodianDto = new NoCrossCustodianDto();
                noCrossCustodianDto.setCrossCustodianInstitutionName("3"); // 12049域-跨托管机构名称(3:中债登)
                noCrossCustodianDto.setCrossCustodianTradeAmt(ibuQuoteCrepDto.getCbFpriceAmt().toString()); // 12050域-跨托管交易金额
                noCrossCustodians.add(noCrossCustodianDto);

                noCrossCustodianDto = new NoCrossCustodianDto();
                noCrossCustodianDto.setCrossCustodianInstitutionName("4"); // 12049域-跨托管机构名称(4:上清所)
                noCrossCustodianDto.setCrossCustodianTradeAmt(ibuQuoteCrepDto.getScFpriceAmt().toString()); // 12050域-跨托管交易金额
                noCrossCustodians.add(noCrossCustodianDto);
                quoteBody.setNoCrossCustodians(noCrossCustodians);
            }
        }
        
        // 453域-参与方信息
        List<NoPartyIDDto> noPartyIDs = new ArrayList<>();
        
        String 发起方交易账户 = null;
        String 发起方交易员ID = null;
        String 接收方交易账户 = null;
        String 接收方交易员ID = null;
        if (StringUtil.equals(repoDialogueQuoteDto.getIbuQuoteCrepDto().getQuoteRoleType(),
            DictCodeList.银行间上行本方角色_回复方.getValue())) {
            发起方交易账户 = ibuQuoteCrepDto.getCpPartyId();
            发起方交易员ID = ibuQuoteCrepDto.getCpTraderId();
            接收方交易账户 = ibuQuoteCrepDto.getOuPartyId();
            接收方交易员ID = ibuQuoteCrepDto.getOuTraderId();
        }
        else {
            发起方交易账户 = ibuQuoteCrepDto.getOuPartyId();
            发起方交易员ID = ibuQuoteCrepDto.getOuTraderId();
            接收方交易账户 = ibuQuoteCrepDto.getCpPartyId();
            接收方交易员ID = ibuQuoteCrepDto.getCpTraderId();
        }

        // 发送方信息（发起方为本方）
        NoPartyIDDto noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(发起方交易账户); // 448域-发起方交易账户 21 位码
        noPartyIDDto.setPartyRole(1); // 452域-1-发起方

        List<NoPartySubIDDto> noPartySubIDs = new ArrayList<>();
        NoPartySubIDDto noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(发起方交易员ID); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);

        // 接收方信息（接收方为交易对手）
        noPartyIDDto = new NoPartyIDDto();
        noPartyIDDto.setPartyID(接收方交易账户); // 448域-接收方交易账户 21 位码
        noPartyIDDto.setPartyRole(17); // 452域-102-接收方

        noPartySubIDs = new ArrayList<>();
        noPartySubIDDto = new NoPartySubIDDto();
        noPartySubIDDto.setPartySubIDType(2); // 803域-2-交易员ID
        noPartySubIDDto.setPartySubID(接收方交易员ID); // 523域:交易员ID
        noPartySubIDs.add(noPartySubIDDto);
        noPartyIDDto.setNoPartySubIDs(noPartySubIDs);

        noPartyIDs.add(noPartyIDDto);
        quoteBody.setNoPartyIDs(noPartyIDs);

        return quoteBody;
    }
    
    /**
     * Cfets数据字典转换 根据Cfets数据字典转换表基础数据，将系统数据代码转化为Cfets数据代码
     * 
     * @param fieldCode 字段代码
     * @param systemItemCode 系统数据代码
     * @return Cfets数据代码
     */
    private String convertSystemCodeToCfetsCode(String fieldCode, String systemItemCode) {
//        @SuppressWarnings("unchecked")
//        Map<String, String> dataMap = CacheUtil.getAppCache("CFETS_DATADICT_CONVERT_DATA",
//            Map.class);
        Map<String, String> dataMap = null;

//        if (dataMap == null || dataMap.isEmpty()) {
            List<CfetsDatadictConvert> cfetsDatadictConvertList = cfetsInterfMapper.selectCfetsDataDictConvertData();

            if (!StringUtil.isEmptyList(cfetsDatadictConvertList)) {
                dataMap = (Map<String, String>)cfetsDatadictConvertList.stream().collect(
                    JoyinCollectors.toMap(x -> x.getFieldCode() + "_" + x.getSystemItemCode(),
                        x -> x.getCfetsItemCode()));
                
//                CacheUtil.putAppCache("CFETS_DATADICT_CONVERT_DATA", dataMap);
            }
//        }
        
        String dataKey = fieldCode + "_" + systemItemCode;
        if (dataMap != null && dataMap.containsKey(dataKey)) {
            return dataMap.get(dataKey);
        }

        return systemItemCode;
    }
}
