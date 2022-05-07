/*
 * 文件名：AllTradeCommonUtil.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：etliu 修改时间：2019年1月12日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.trade;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.dto.PtlRecePositionFlowDto;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dao.TrdCommonBaseMapper;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesOutputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.dao.WorkflowMapper;
import com.joyintech.workflow.engine.dto.WflStatusDto;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

//import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
//import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
//import com.joyintech.tams.valuation.api.service.ValInterfCommonServiceApi;
//import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfo;
//import com.joyintech.tams.valuation.common.interf.base.dto.ChannelProdDeal;
//import com.joyintech.tams.valuation.common.interf.howmany.dto.TrdProductDealDto;

/**
 * 所有交易都要调用的共通Util
 *
 * @author etliu
 * @version 1.0 2019年1月12日
 * @see AllTradeCommonUtil
 * @since 1.0
 */
@Service
@Lazy(false)
public class AllTradeCommonUtil {

    private static Logger logger = LoggerFactory.getLogger("接口调用");

    // 自动生成反向交易 chenzhili start
    private static StlPayOrdersMapper stlPayOrdersMapper;

    private static TrdProductDealMapper trdProductDealMapper;

    private static TrdProductDealAddMapper trdProductDealAddMapper;

    private static PaTrdProductDealMapper paTrdProductDealMapper;

    private static PtlPortfolioMapper ptlPortfolioMapper;

    private static FinProductTypeMapper finProductTypeMapper;

    private static JoyinRestTemplate restTemplate;

    private static BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;
    // 自动生成反向交易 chenzhili end

    private static PtlTradeMapper ptlTradeMapper;

    private static EtrCommonCashPositionDealService etrCommonCashPositionDealService;
    
    
    private static TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    private static TrdCommonBaseMapper trdCommonBaseMapper;

    private static BasePtlRecePositionService basePtlRecePositionService;

    // 调用投管接口 add by renping 20190205 start
    /**
     * 投管接口共通组件
     */
    private static InvestInterfCommService investInterfCommService;
    // 调用投管接口 add by renping 20190205 end

    private static WorkflowMapper workflowMapper;

    /**
     * 所有交易生效后调用的共通方法
     *
     * @param inputDto
     * @see
     * @since 1.0
     */
    public static void handleAfterTradeComplete(AllTradeCommonInputDto inputDto) {
        // try {
        // 自动生成反向交易 chenzhili start 下面的入账方法改变了busiType，这里的反向交易生成先调用
        if(!StringUtil.equals(inputDto.getIsNotAutoGenerateReverseDeal(),DictCodeList.是否_是.getValue())){
//            逾期资产转让不需要自动生成
            autoGenerateReverseDeal(inputDto);
        }
        // 自动生成反向交易 chenzhili end
        boolean 子组合调用了接口 = false;
        if (inputDto.getTrdProductDealAdd() != null) {
            String 原组合代码 = inputDto.getTrdProductDealAdd().getPortfolioId();
            if (StringUtil.isNotEmpty(inputDto.getActPortfolioId()) && !inputDto.getActPortfolioId().equals(原组合代码)) {
                inputDto.getTrdProductDealAdd().setPortfolioId(inputDto.getActPortfolioId());
                inputDto.setProdid(inputDto.getActPortfolioId());
                调用估值接口(inputDto); // 子组合调用估值接口
                inputDto.getTrdProductDealAdd().setPortfolioId(原组合代码);
                inputDto.setProdid(原组合代码);
                子组合调用了接口 = true;
            }
        }
        // 子组合交易时，母组合不需要调接口
        if (!子组合调用了接口) {
            调用估值接口(inputDto);
        }
        // }
        // catch (Exception e) {
        // logger.error("接口调用失败", e);
        // }
    }

    private static void 调用估值接口(AllTradeCommonInputDto inputDto) {
         //通道交易 新增 强成西 2019/03/26 start
        if (inputDto.getTrdProductDealAdd() != null) {
            TrdProductDealDto deal = new TrdProductDealDto();
            BeanUtil.copyPropertiesIgnoreNull(deal, inputDto.getTrdProductDeal());
            TrdProductDealAdd dealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(dealAdd, inputDto.getTrdProductDealAdd());
            TrdFinProductBarter barterDeal = null;
            // 金融产品实物交易
            if (inputDto.getTrdFinProductBarter() != null) {
                barterDeal = new TrdFinProductBarter();
                BeanUtil.copyPropertiesIgnoreNull(barterDeal, inputDto.getTrdFinProductBarter());
            }
            /** ADD BY WUJIE ON 20190906 START **/
            boolean isChlIdTZ = false;
            if (DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2())
                || DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(deal.getFinprodType2())) {
                /**
                 * CHL_MODEL_TYPE 01:计划类资产资产 不支持通道
                 */
                String chlModelType = CacheUtil.getSysParam("CHL_MODEL_TYPE");
                if (StringUtil.isNotEmpty(chlModelType) && chlModelType.equals("01")) {
                    inputDto.getTrdProductDeal().setChlId(null);
                    //项目类  计划类净值 通道信息置空  逾期信息置空
                    if (StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
                            ||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
                            ||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
                            ||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
                            ||StringUtil.equals(deal.getBusiType(), DictCodeList.业务类型_逾期交易.getValue())
                    ){
                        inputDto.getTrdProductDeal().setChlId(null);
                    }
                }else {
                    isChlIdTZ = StringUtil.isNotEmpty(inputDto.getTrdProductDeal().getChlId());
                }
            }
            else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())){
            	//IS_CAL_LOADRATE 01 借款利率
            	if(StringUtil.isNotEmpty(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE)) && CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE).equals("01")){
            		isChlIdTZ = StringUtil.isNotEmpty(inputDto.getTrdProductDeal().getChlId());
            	}else{
            		inputDto.getTrdProductDeal().setChlId(null);
            	}
            }
            else {
                isChlIdTZ = StringUtil.isNotEmpty(inputDto.getTrdProductDeal().getChlId());
            }
            /** ADD BY WUJIE ON 20190906 END **/

            // 通道不为空时，调用通道相关接口
            if (isChlIdTZ) {
//                ValInterfCommonServiceApi valInterfCommonServiceApi = SpringContextUtil.getBean(ValInterfCommonServiceApi.class);
//                FinProductAddMapper finProductMapperAdd = SpringContextUtil.getBean(FinProductAddMapper.class);
//                FinProductAdd payType = finProductMapperAdd.selectByPrimaryKey(inputDto.getTrdProductDeal().getChlId());
//
//                // 20200204 魏鹏：增加通道(一次性划款)内部转仓交易时通道划款的处理 设计变更 start
//                // 定义 变量.资金交收指令DTO
//                StlOrders stlOrders = new StlOrders();
//                // IF 输入.接口数据.划款指令 IS NOT NULL
//                if(inputDto.getStlPayOrders() != null){
//                    BeanUtil.copyPropertiesIgnoreNull(stlOrders, inputDto.getStlPayOrders());
//                    // IF 数据字典：划款方式.逐笔划款 eq 变量.通道标的扩展信息Dto.划款方式
//                    if (DictCodeListExt2.划款方式_逐笔划款.getValue().equals(payType.getPayType())){
//                        // 变量.资金交收指令DTO.本方账户  = 变量.资金交收指令DTO.对方账户
//                        stlOrders.setMyAcctNo(stlOrders.getCounterAcctNo());
//                        // 变量.资金交收指令DTO.对方账户 = null
//                        stlOrders.setCounterAcctNo(null);
//                    }
//                    // 变量.资金交收指令DTO.结算金额 = NULL
//                    stlOrders.setSettleAmt(null);
//                }
//                // 20200204 魏鹏：增加通道(一次性划款)内部转仓交易时通道划款的处理 设计变更 end
//
//
//                if (DictCodeList.交易方向_付费.getValue().equals(inputDto.getTrdProductDeal().getPs())) {
//                    List<TrdTradeFeeDetail> trdTradeFeeDetailList = genTradeFeeDetails(inputDto.getTrdProductDeal());
//
//                    valInterfCommonServiceApi.prdDealSep(deal, trdTradeFeeDetailList, dealAdd.getPortfolioId(), deal.getChlId(),
//                        DictCodeListExt1.金融产品类型_投资组合.getValue(), deal.getBranch(),
//                        DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType.getPayType()) ? "01" : "00");
//                }
//                else if (StringUtil.equals(DictCodeList.交易方向_税费缴税.getValue(), inputDto.getTrdProductDeal().getPs())) {
//
//                    valInterfCommonServiceApi.prdDealSep(deal, inputDto.getListTrdTradeFeeDetail(), deal.getFinprodId(),
//                        deal.getChlId(), inputDto.getProdFinprodType(), deal.getBranch(),
//                        DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType.getPayType()) ? "01" : "00");
//                }
//                else {
//                    // 重新获取金融产品类型
//                    ValItemValueGenUtil.genFinprodType(deal);
//                    // 重新获取业务类型
//                    ValItemValueGenUtil.genBusiType(deal);
//                    if (DictCodeListExt2.划款方式_逐笔划款.getValue().equals(payType.getPayType())
//                            && !DictCodeList.交易方向_申购确认.getValue().equals(deal.getPs())
//                            && !DictCodeList.交易方向_赎回确认.getValue().equals(deal.getPs())
//                            && !DictCodeList.交易方向_认购确认.getValue().equals(deal.getPs())) {
//                        // 调用接口【理财产品借通道投资交易】
//                        // 2.4 产品买卖通道交易，调用接口【理财产品借通道投资交易】
//                        ChannelProdDeal channelProdDeal = do生成ChannelProdDeal(deal, inputDto);
//                        channelProdDeal.setIsSwapTransaction(inputDto.getIsSwapTransaction());
//                        /** add by yanya on 20191205 非货币基金交易为除权登记时，交易金额、交易本金、交易份额置为0,防止生成交易凭证 **/
//                        if ((DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue().equals(deal.getBusiType())
//                             || DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue().equals(deal.getBusiType()))
//                            && DictCodeList.交易方向_除权登记.getValue().equals(deal.getPs())) {
//                            channelProdDeal.setTradeAmt(BigDecimal.ZERO);
//                            channelProdDeal.setPrinAmt(BigDecimal.ZERO);
//                            channelProdDeal.setShareAmt(BigDecimal.ZERO);
//                        }
//
////                        // 通道指令信息传参的时候，估值使用传过来的指令信息 modify by wujie 20191117 start
////                        StlOrders stlOrders = null;
////                        if (inputDto.getStlPayOrders() != null) {
////                            stlOrders = new StlOrders();
////                            BeanUtil.copyPropertiesIgnoreNull(stlOrders, inputDto.getStlPayOrders());
////                            stlOrders.setSettleAmt(null);//modify wujie on 20200119 remark:不使用前提资金交收指令的结算金额
////                        }
//                        channelProdDeal.setStlOrders(stlOrders);
//                        channelProdDeal.setIsSwapTransaction(inputDto.getIsSwapTransaction());
//                        // 通道指令信息传参的时候，估值使用传过来的指令信息 modify by wujie 20191117 end
//                        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->理财产品借通道投资交易SHEET
//                        valInterfCommonServiceApi.getChannelProdDeal(channelProdDeal);
//                    }
//                    // 20200204 魏鹏：增加通道(一次性划款)内部转仓交易时通道划款的处理 设计变更 start
//                    // IF 数据字典：是否.是 eq 输入.接口数据.是否转仓交易 AND 数据字典：划款方式.一次性划款 eq 变量.通道标的扩展信息Dto.划款方式
//                    // update by caiqun 20200903 TISHKBS-1038一次性划款的通道，未做通道资金划拨，债券买入交易（通过该通道投资）审批通过后，入账信息表（BOK_DEAL_ACCOUNTING）表无数据
//                 /*   if(DictCodeList.是否_是.getValue().equals(inputDto.getIsSwapTransaction()) &&
//                            DictCodeListExt2.划款方式_一次性划款.getValue().equals(payType.getPayType())){
//                        // 调用接口【理财产品借通道投资交易】
//                        // 2.4 产品买卖通道交易，调用接口【理财产品借通道投资交易】
//                        ChannelProdDeal channelProdDeal = do生成ChannelProdDeal(deal, inputDto);
//                        // 变量.理财产品借通道投资交易DTO.资金交收指令 = 变量.资金交收指令DTO
//                        channelProdDeal.setStlOrders(stlOrders);
//                        // 变量.理财产品借通道投资交易DTO.是否转仓交易 = 输入.接口数据.是否转仓交易
////                        inputDto.setIsSwapTransaction(inputDto.getIsSwapTransaction());
//                        channelProdDeal.setIsSwapTransaction(inputDto.getIsSwapTransaction());
//                        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->理财产品借通道投资交易SHEET
//                        valInterfCommonServiceApi.getChannelProdDeal(channelProdDeal);
//                    }*/
//                    // 20200204 魏鹏：增加通道(一次性划款)内部转仓交易时通道划款的处理 设计变更 end
//
//                    // 调用接口【通道投资资产交易拆分】
//                    // TODO:债转股交易的金融产品实物交易信息也要作为参数调用接口
//                    /**
//                     * MODIFY BY WUJIE ON 20191118 START valInterfCommonServiceApi.chlInvestAssetSep(dealAdd.getPortfolioId(), DictCodeListExt1.金融产品类型_投资组合.getValue(),
//                     * deal, dealAdd, inputDto.getTrdTradeFeeDetail(),null,null);
//                     **/
//                    logger.info("调用组合对应【通道投资资产交易拆分】接口处理开始-----------------------");
//                    logger.info("调用组合对应【通道投资资产交易拆分】接口处理交易编号" + deal.getTradeId() + "-----------------------");
//                    logger.info("调用组合对应【通道投资资产交易拆分】接口处理产品代码" + inputDto.getProdid() + "-----------------------");
//                    logger.info("调用组合对应【通道投资资产交易拆分】接口处理产品类型" + inputDto.getProdFinprodType() + "-----------------------");
//                    ChannelProdDeal channelProdDeal = new ChannelProdDeal();
//                    BeanUtil.copyPropertiesIgnoreNull(channelProdDeal, deal);
//                    channelProdDeal.setTrdProductDealAdd(dealAdd);
//                    // modify by renping 基金赎回到账时账务凭证不正确 20200325 start
//                    // 通道费赎回到账时不能出通道费凭证（因为赎回确认时已经出过了）
//                    boolean channelFeeT24Flag = (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(
//                        deal.getFinprodType2()) || DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(deal.getFinprodType2()))
//                        && StringUtil.equals(deal.getPs(), DictCodeList.交易方向_赎回到账.getValue())
//                        && inputDto.getTrdTradeFeeDetail() != null
//                        && StringUtil.equals(inputDto.getTrdTradeFeeDetail().getIsPayWithTrade(), DictCodeList.是否_是.getValue());
//                    if (!channelFeeT24Flag) {
//                        channelProdDeal.setTrdTradeFeeDetail(inputDto.getTrdTradeFeeDetail());
//                    }
//                    // modify by renping 基金赎回到账时账务凭证不正确 20200325 end
//                    channelProdDeal.setProdId(inputDto.getProdid());
//                    channelProdDeal.setProductType(inputDto.getProdFinprodType());
//                    channelProdDeal.setPtlTradeList(inputDto.getPtlTradeList());
//                    // 非通道费用List
//                    channelProdDeal.setListTrdTradeFeeDetail(inputDto.getListTrdTradeFeeDetail());
//
//                    // add by renping 基金赎回到账时账务凭证不正确 20200325 start
//                    // 通道费直接加到交易金额上
//                    if (channelFeeT24Flag) {
//                        channelProdDeal.setTradeAmt(ArithmeticUtil.doAdd(deal.getTradeAmt(),
//                            inputDto.getTrdTradeFeeDetail().getFeeAmt()));
//                    }
//                    // add by renping 基金赎回到账时账务凭证不正确 20200325 end
//                    // 金融产品实物交易表
//                    channelProdDeal.setTrdFinProductBarter(barterDeal);
//                    // 资金交收指令
//                    // 通道指令信息传参的时候，估值使用传过来的指令信息 modify by renping 20191117 start
////                    StlOrders stlOrders = null;
////                    if (inputDto.getStlPayOrders() != null) {
////                        stlOrders = new StlOrders();
////                        BeanUtil.copyPropertiesIgnoreNull(stlOrders, inputDto.getStlPayOrders());
////
////                        /** ADD BY WUJIE ON 20191205 START REMARK:通道投资 **/
////                        if (DictCodeListExt2.划款方式_逐笔划款.getValue().equals(payType.getPayType())) {
////                            stlOrders.setMyAcctNo(stlOrders.getCounterAcctNo());
////                            stlOrders.setCounterAcctNo(null);
////                        }
////                        /** ADD BY WUJIE ON 20191205 END **/
////                        stlOrders.setSettleAmt(null);//modify wujie on 20200119 remark:不使用前提资金交收指令的结算金额
////
////                    }
//                    // IF 数据字典：是否.是 ==输入.接口数据.是否转仓交易
//                    if(DictCodeList.是否_是.getValue().equals(inputDto.getIsSwapTransaction())){
//                        // 变量.资金交收指令DTO = NULL
//                        stlOrders = null;
//                    }
//                    channelProdDeal.setStlOrders(stlOrders);
//                    channelProdDeal.setIsSwapTransaction(inputDto.getIsSwapTransaction());
//                    // 通道指令信息传参的时候，估值使用传过来的指令信息 modify by renping 20191117 end
//                    valInterfCommonServiceApi.chlInvestAssetSep(channelProdDeal);
//                    logger.info("调用组合对应【通道投资资产交易拆分】接口处理结束-----------------------");
//                    /** MODIFY BY WUJIE ON 20191118 END **/
//                }
                // 和需求确认通道暂时也占授信，后期预留开关 start add by zcw
                CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
                if (!(DictCodeListExt1.交易状态_初始.getValue().equals(inputDto.getTrdProductDeal().getTradeStatus())
                      && (DictCodeList.交易方向_买入.getValue().equals(inputDto.getTrdProductDeal().getPs())
                          || DictCodeListExt2.交易方向_认购.getValue().equals(inputDto.getTrdProductDeal().getPs())
                          || DictCodeList.交易方向_赎回确认.getValue().equals(inputDto.getTrdProductDeal().getPs())
                          || DictCodeList.交易方向_申购.getValue().equals(inputDto.getTrdProductDeal().getPs())
                          || DictCodeListExt2.交易方向_新股中签.getValue().equals(inputDto.getTrdProductDeal().getPs())
                          || DictCodeListExt2.交易方向_配股.getValue().equals(inputDto.getTrdProductDeal().getPs())
                          || DictCodeListExt2.交易方向_增发.getValue().equals(inputDto.getTrdProductDeal().getPs())))) {
                    ceLinesUpdate.isNeedOccupyCre(inputDto, true, true, false);
                }
                // 和需求确认通道暂时也占授信，后期预留开关 end add by zcw
            }
            else {
                do调用接口生成入账信息(inputDto);
            }
        }
        // 通道交易 新增 强成西 2019/03/26 end
        else {
            do调用接口生成入账信息(inputDto);
        }
    }

    /**
     * 调用接口【理财产品借通道投资交易】
     *
     * @param inputDto 参数
     * @see
     * @since 1.0
     */
    public static void getChannelProdDeal(AllTradeCommonInputDto inputDto) {
//        ValInterfCommonServiceApi valInterfCommonServiceApi = SpringContextUtil.getBean(ValInterfCommonServiceApi.class);
//        ChannelProdDeal channelProdDeal = do生成ChannelProdDeal(inputDto.getTrdProductDeal(), inputDto);
//        valInterfCommonServiceApi.getChannelProdDeal(channelProdDeal);
    }

    private static List<TrdTradeFeeDetail> genTradeFeeDetails(TrdProductDeal trdProductDeal) {
        TrdTradeFeeDetail detail = new TrdTradeFeeDetail();
        detail.setTradeId(trdProductDeal.getTradeId());
        detail.setChlFinprodId(trdProductDeal.getChlId());
        detail.setFeeAmt(trdProductDeal.getTradeAmt());
        detail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        detail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        return Arrays.asList(new TrdTradeFeeDetail[] {detail});
    }

//    // 通道交易 新增 强成西 2019/03/26 start
//    private static ChannelProdDeal do生成ChannelProdDeal(TrdProductDeal trdProductDeal, AllTradeCommonInputDto inputDto) {
//        ChannelProdDeal channelProdDeal = new ChannelProdDeal();
//        channelProdDeal.setTradeId(trdProductDeal.getTradeId());
//        channelProdDeal.setProdId(inputDto.getProdid());
//        channelProdDeal.setTradeStatus(trdProductDeal.getTradeStatus());
//        channelProdDeal.setChlId(trdProductDeal.getChlId());
//        // 交易方向= （转仓时（买入、卖出）、申购赎回时（申购、赎回））
//        channelProdDeal.setPs(trdProductDeal.getPs());
//        channelProdDeal.setTradeDate(trdProductDeal.getTradeDate());
//        channelProdDeal.setSettleDate(trdProductDeal.getSettleDate());
//        channelProdDeal.setPrinAmt(trdProductDeal.getPrinAmt() == null ? BigDecimal.ZERO : trdProductDeal.getPrinAmt());
//        channelProdDeal.setTradeAmt(trdProductDeal.getTradeAmt() == null ? BigDecimal.ZERO : trdProductDeal.getTradeAmt());
//        channelProdDeal.setShareAmt(trdProductDeal.getTradeAmt() == null ? BigDecimal.ZERO : trdProductDeal.getTradeAmt());
//        channelProdDeal.setTradeMarket(trdProductDeal.getTradeMarket());
//        channelProdDeal.setCcy(trdProductDeal.getCcy());
//        channelProdDeal.setDealSepFlag("00");
//        channelProdDeal.setIsClean(DictCodeList.是否_是.getValue());
//        channelProdDeal.setfTradeId(trdProductDeal.getTradeId());
//        // 有通道是撤销报错，原交易编号是null了 modify by renping 20191025 start
//        String pTradeId = StringUtil.isNotEmpty(
//            trdProductDeal.getpTradeId()) ? trdProductDeal.getpTradeId() : trdProductDeal.getTradeId();
//        channelProdDeal.setpTradeId(pTradeId);
//        // 有通道是撤销报错，原交易编号是null了 modify by renping 20191025 end
//        /** modify by dongjun on 20190527 质押式正回购交易方向特殊处理 start **/
//        channelProdDeal.setFinprodType(trdProductDeal.getFinprodType());
//        /** modify by dongjun on 20190527 质押式正回购交易方向特殊处理 end **/
//
//        /** add by wujie on 20191118 start **/
//        channelProdDeal.setProductType(inputDto.getProdFinprodType());
//        /** add by wujie on 20191118 end **/
//
//        channelProdDeal.setPtlTradeList(inputDto.getPtlTradeList());
//        //传入通道费信息
//        if (inputDto.getTrdTradeFeeDetail() != null
//                // add by renping 基金赎回到账时账务凭证不正确 20200325 start
//                && !((DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(
//                    trdProductDeal.getFinprodType2())
//                      || DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(
//                          trdProductDeal.getFinprodType2()))
//                     && StringUtil.equals(trdProductDeal.getPs(),
//                         DictCodeList.交易方向_赎回到账.getValue()))) {
//                // add by renping 基金赎回到账时账务凭证不正确 20200325 end
//            	ChannelFeeInfo channelFeeInfo = new ChannelFeeInfo();
//            	channelFeeInfo.setFeeAmt(inputDto.getTrdTradeFeeDetail().getFeeAmt());
//            	channelFeeInfo.setFeeType(inputDto.getTrdTradeFeeDetail().getFeeType());
//            	channelFeeInfo.setIsPayWithTrade(inputDto.getTrdTradeFeeDetail().getIsPayWithTrade());
//            	channelFeeInfo.setFeeId(inputDto.getTrdTradeFeeDetail().getFeeId());
//            	channelProdDeal.setChannelFeeInfo(channelFeeInfo);
//            }
//
//        return channelProdDeal;
//    }
    // 通道交易 新增 强成西 2019/03/26 end

    private static void do调用接口生成入账信息(AllTradeCommonInputDto inputDto) {
//        // 调用估值交易入账接口
//        ValuationInterfService valuationInterfService = SpringContextUtil.getBean(ValuationInterfService.class);
         //调用授信共通
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        if (/*valuationInterfService != null && */ceLinesUpdate != null) {
//            BokTradeBuildInputDto bokTradeBuildInputDto = new BokTradeBuildInputDto();
//            bokTradeBuildInputDto.setProdid(inputDto.getProdid());
//            bokTradeBuildInputDto.setProdFinprodType(inputDto.getProdFinprodType());
//            bokTradeBuildInputDto.setProdVdate(inputDto.getProdVdate());
//            bokTradeBuildInputDto.setTrdProductDeal(inputDto.getTrdProductDeal());
//            /** ADD BY WUJIE ON 20191119 START **/
//            bokTradeBuildInputDto.setPtlTradeList(inputDto.getPtlTradeList());
//            /** ADD BY WUJIE ON 20191119 END **/
//            // add by renping 20191117 start
//            bokTradeBuildInputDto.setStlPayOrders(inputDto.getStlPayOrders());
//            // add by renping 20191117 start
//            // add by jinmaodong 20191214 start
//            bokTradeBuildInputDto.setTrdFinProductBarter(inputDto.getTrdFinProductBarter());
             //add by jinmaodong 20191214 end

            // 外汇，股票，利率互换，债券借贷 不需要走 交易入账 add by qmh 20190812 start
//            if (!(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), inputDto.getTrdProductDeal().getFinprodType())
//                  || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_即期.getValue(), inputDto.getTrdProductDeal().getFinprodType())
//                  || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_远期.getValue(), inputDto.getTrdProductDeal().getFinprodType())
//                  || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_掉期.getValue(), inputDto.getTrdProductDeal().getFinprodType())
//                  || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue(),
//                      inputDto.getTrdProductDeal().getFinprodType()))) {
//                valuationInterfService.bokTradeBuild(bokTradeBuildInputDto);
//            }
            // add by qmh 20190812 end

            if (!(DictCodeListExt1.交易状态_初始.getValue().equals(inputDto.getTrdProductDeal().getTradeStatus())
                  && (DictCodeList.交易方向_买入.getValue().equals(inputDto.getTrdProductDeal().getPs())
                      || DictCodeListExt2.交易方向_认购.getValue().equals(inputDto.getTrdProductDeal().getPs())
                      || DictCodeList.交易方向_赎回确认.getValue().equals(inputDto.getTrdProductDeal().getPs())
                      || DictCodeList.交易方向_申购.getValue().equals(inputDto.getTrdProductDeal().getPs())
                      || DictCodeListExt2.交易方向_新股中签.getValue().equals(inputDto.getTrdProductDeal().getPs())
                      || DictCodeListExt2.交易方向_配股.getValue().equals(inputDto.getTrdProductDeal().getPs())
                      || DictCodeListExt2.交易方向_增发.getValue().equals(inputDto.getTrdProductDeal().getPs())))) {
                ceLinesUpdate.isNeedOccupyCre(inputDto, true, true, false);
            }
        }
    }

    /**
     * 拆入到期_新增
     *
     * @param inputDto 参数
     * @see
     * @since 1.0
     */
    public static void lendMdateAdd(AllTradeCommonInputDto inputDto) {
//        // 调用估值交易入账接口
//        ValuationInterfService valuationInterfService = SpringContextUtil.getBean(ValuationInterfService.class);
        // 调用授信共通
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        if (/*valuationInterfService != null &&*/ ceLinesUpdate != null) {
//            BokTradeBuildInputDto bokTradeBuildInputDto = new BokTradeBuildInputDto();
//            bokTradeBuildInputDto.setProdid(inputDto.getProdid());
//            bokTradeBuildInputDto.setProdFinprodType(inputDto.getProdFinprodType());
//            bokTradeBuildInputDto.setProdVdate(inputDto.getProdVdate());
//            bokTradeBuildInputDto.setTrdProductDealList(inputDto.getTrdProductDealList());
//            bokTradeBuildInputDto.setTrdTradeFeeDetail(inputDto.getTrdTradeFeeDetail());
//            /** ADD BY WUJIE ON 20191119 START **/
//            bokTradeBuildInputDto.setPtlTradeList(inputDto.getPtlTradeList());
//            /** ADD BY WUJIE ON 20191119 END **/
//            bokTradeBuildInputDto.setStlPayOrders(inputDto.getStlPayOrders());
//
//            /**ADD BY WUJIE ON 20210129 START REMARK:回购交易方向：到期续作、逾期贩售**/
//            bokTradeBuildInputDto.setPsType(inputDto.getPsType());
//            /**ADD BY WUJIE ON 20210129 END**/
//
//            valuationInterfService.lendMdateAdd(bokTradeBuildInputDto);
//
//            valuationInterfService.lendMdateAdd(bokTradeBuildInputDto);

            // 调用授信共通
            for (TrdProductDeal trdProductDeal : inputDto.getTrdProductDealList()) {
                AllTradeCommonInputDto input = new AllTradeCommonInputDto();
                input.setTrdProductDeal(trdProductDeal);
                input.setRepoType(inputDto.getRepoType());
                if (!(DictCodeListExt1.交易状态_初始.getValue().equals(trdProductDeal.getTradeStatus())
                      && (DictCodeList.交易方向_买入.getValue().equals(trdProductDeal.getPs())
                          || DictCodeList.交易方向_申购.getValue().equals(trdProductDeal.getPs())
                          || DictCodeListExt2.交易方向_新股中签.getValue().equals(trdProductDeal.getPs())
                          || DictCodeListExt2.交易方向_配股.getValue().equals(trdProductDeal.getPs())
                          || DictCodeListExt2.交易方向_增发.getValue().equals(trdProductDeal.getPs())))) {
                    ceLinesUpdate.isNeedOccupyCre(input, true, true, false);
                }
            }
        }
    }
    
    //买断式回购到期 授信部分修改
    public static void buyRepoMdateAdd(AllTradeCommonInputDto inputDto) {
//        // 调用估值交易入账接口
//        ValuationInterfService valuationInterfService = SpringContextUtil.getBean(ValuationInterfService.class);
//        // 调用授信共通
//        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
//        if (valuationInterfService != null && ceLinesUpdate != null) {
//            BokTradeBuildInputDto bokTradeBuildInputDto = new BokTradeBuildInputDto();
//            bokTradeBuildInputDto.setProdid(inputDto.getProdid());
//            bokTradeBuildInputDto.setProdFinprodType(inputDto.getProdFinprodType());
//            bokTradeBuildInputDto.setProdVdate(inputDto.getProdVdate());
//            bokTradeBuildInputDto.setTrdProductDealList(inputDto.getTrdProductDealList());
//            bokTradeBuildInputDto.setTrdTradeFeeDetail(inputDto.getTrdTradeFeeDetail());
//            /** ADD BY WUJIE ON 20191119 START **/
//            bokTradeBuildInputDto.setPtlTradeList(inputDto.getPtlTradeList());
//            /** ADD BY WUJIE ON 20191119 END **/
//
//            /**ADD BY WUJIE ON 20210129 START REMARK:回购交易方向：到期续作、逾期贩售**/
//            bokTradeBuildInputDto.setPsType(inputDto.getPsType());
//            /**ADD BY WUJIE ON 20210129 END**/
//
//            valuationInterfService.lendMdateAdd(bokTradeBuildInputDto);
//
//            valuationInterfService.lendMdateAdd(bokTradeBuildInputDto);
            // 调用授信共通
//            for (TrdProductDeal trdProductDeal : inputDto.getTrdProductDealList()) {
//                AllTradeCommonInputDto input = new AllTradeCommonInputDto();
//                //使用抵押品信息代替交易编号
//                String finprodid = trdCommonBaseMapper.getFinprodIdByTradeId(trdProductDeal.getFinprodId());
//                trdProductDeal.setFinprodId(finprodid);
//                input.setTrdProductDeal(trdProductDeal);
//                input.setRepoType(inputDto.getRepoType());
//                if (!(DictCodeListExt1.交易状态_初始.getValue().equals(trdProductDeal.getTradeStatus())
//                      && (DictCodeList.交易方向_买入.getValue().equals(trdProductDeal.getPs())
//                          || DictCodeList.交易方向_申购.getValue().equals(trdProductDeal.getPs())
//                          || DictCodeListExt2.交易方向_新股中签.getValue().equals(trdProductDeal.getPs())
//                          || DictCodeListExt2.交易方向_配股.getValue().equals(trdProductDeal.getPs())
//                          || DictCodeListExt2.交易方向_增发.getValue().equals(trdProductDeal.getPs())))) {
//                    ceLinesUpdate.isNeedOccupyCre(input, true, true, false);
//                }
//            }
//        }
    }

    /**
     * 拆入到期_撤销
     *
     * @param inputDto 参数
     * @see
     * @since 1.0
     */
    public static void lendMdatDelete(AllTradeCommonInputDto inputDto) {
        // 调用估值交易入账接口
//        ValuationInterfService valuationInterfService = SpringContextUtil.getBean(ValuationInterfService.class);
//        if (valuationInterfService != null) {
//            BokTradeBuildInputDto bokTradeBuildInputDto = new BokTradeBuildInputDto();
//            bokTradeBuildInputDto.setTotleTradeId(inputDto.getTotleTradeId());
//
//            valuationInterfService.lendMdatDelete(bokTradeBuildInputDto);
//        }
    }

    /**
     * 结算指令交易入账
     *
     * @param inputDto 参数
     * @see
     * @since 1.0
     */
    public static void stlOrdersBokTradeBuild(AllTradeCommonInputDto inputDto) {
        // 调用估值交易入账接口
//        ValuationInterfService valuationInterfService = SpringContextUtil.getBean(ValuationInterfService.class);
//        if (valuationInterfService != null) {
//            BokTradeBuildInputDto bokTradeBuildInputDto = new BokTradeBuildInputDto();
//            bokTradeBuildInputDto.setProdid(inputDto.getProdid());
//            bokTradeBuildInputDto.setProdFinprodType(inputDto.getProdFinprodType());
//            bokTradeBuildInputDto.setProdVdate(inputDto.getProdVdate());
//            bokTradeBuildInputDto.setStlPayOrders(inputDto.getStlPayOrders());
//            bokTradeBuildInputDto.setTrdProductDeal(inputDto.getTrdProductDeal());
//            /** ADD BY WUJIE ON 20191119 START **/
//            bokTradeBuildInputDto.setPtlTradeList(inputDto.getPtlTradeList());
//            /** ADD BY WUJIE ON 20191119 ENE **/
//
//            valuationInterfService.stlOrdersBokTradeBuild(bokTradeBuildInputDto);
//        }
    }

    public static void handleBeforeTradeComplete(AllTradeCommonInputDto inputDto, Boolean isdeleteflag) {
        // 调用授信共通
        CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
        if (ceLinesUpdate != null) {
            if (DictCodeList.交易方向_买入.getValue().equals(inputDto.getTrdProductDeal().getPs())
                || DictCodeList.交易方向_申购.getValue().equals(inputDto.getTrdProductDeal().getPs())
                || DictCodeListExt2.交易方向_认购.getValue().equals(inputDto.getTrdProductDeal().getPs())
                || DictCodeListExt2.交易方向_新股中签.getValue().equals(inputDto.getTrdProductDeal().getPs())
                || DictCodeListExt2.交易方向_配股.getValue().equals(inputDto.getTrdProductDeal().getPs())
                || DictCodeListExt2.交易方向_增发.getValue().equals(inputDto.getTrdProductDeal().getPs())) {
                ceLinesUpdate.isNeedOccupyCre(inputDto, true, true, isdeleteflag);
            }
        }
    }

    // 自动生成反向交易 chenzhili start
    /**
     * 自动生成反向交易
     *
     * @param allTradeCommonInputDto AllTradeCommonInputDto
     */
    private static void autoGenerateReverseDeal(AllTradeCommonInputDto allTradeCommonInputDto) {
        // TrdProductDeal trdProductDeal = allTradeCommonInputDto.getTrdProductDeal();
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, allTradeCommonInputDto.getTrdProductDeal());
        String busiType = trdProductDeal.getBusiType();
        String autoGenerateNegateDealFlag = restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectParam/" + "COM" + "/"
                                                                      + BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL,
            String.class);
        // 场内基金改造 是否内部转仓交易 add by renping 20191203 start
        allTradeCommonInputDto.setIsSwapTransaction(DictCodeList.是否_否.getValue());
        if (StringUtil.isNotEmpty(trdProductDeal.getCounterId())) {
            PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(trdProductDeal.getCounterId());
            if (mcp != null) {
                allTradeCommonInputDto.setIsSwapTransaction(DictCodeList.是否_是.getValue());
            }
        }
        // 场内基金改造 是否内部转仓交易 add by renping 20191203 end
        if (DictCodeList.是否_是.getValue().equals(autoGenerateNegateDealFlag)
            && (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType) || DictCodeList.共通业务类型_存款类资产交易.getValue().equals(busiType)
                || DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)
                || DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)
                || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)
                || DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)
                || DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)
                || DictCodeListExt2.共通业务类型_股票投资交易.getValue().equals(busiType)
                // add by etliu 20210421 权益类资产相关代码合并 start
                || DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType))
                // add by etliu 20210421 权益类资产相关代码合并 start
            && (DictCodeList.交易方向_买入.getValue().equals(trdProductDeal.getPs())
                || DictCodeList.交易方向_卖出.getValue().equals(trdProductDeal.getPs()))) {

            // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
            List<BaseTradeProductDealDto> baseTradeProductDealDtoList = allTradeCommonInputDto.getBaseTradeProductDealDtoList();

            List<BaseTradeProductDealDto> baseReceTradeProductDealDtoList = allTradeCommonInputDto.getBaseReceTradeProductDealDtoList();
            //应收款转出/转入

            if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
                for (BaseTradeProductDealDto btpdd : baseTradeProductDealDtoList) {
                    if (btpdd.getTrdProductDeal() == null) {
                        continue;
                    }

                    PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(btpdd.getTrdProductDeal().getCounterId());
                    if (mcp != null) { // 内部交易对手
                        TrdProductDeal deal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(deal, btpdd.getTrdProductDeal());
                        deal.setrTradeId(btpdd.getTrdProductDeal().getTradeId());
                        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                        if (btpdd.getTrdProductDealAdd() != null) {
                            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, btpdd.getTrdProductDealAdd());
                        }
                        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                        if (btpdd.getPaTrdProductDeal() != null) {
                            BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, btpdd.getPaTrdProductDeal());
                        }

                        String 交易对手_子组合 = deal.getCounterId();// 子组合交易对手
                        String 交易对手的母组合 = AllTradeCommonUtil.exchangePortfolio(deal.getCounterId());// 交易对手的母组合，设置成反向交易的组合代码

                        // 组合代码与交易对手对调
                        // 交易方向为买入；
                        // 其他字段与原始交易相同；
                        // String 原交易母组合代码 = trdProductDealAdd.getPortfolioId();
                        String 原交易子组合代码 = btpdd.getActPortfolioId();
                        deal.setCounterId(原交易子组合代码);
                        trdProductDealAdd.setPortfolioId(交易对手的母组合);
                        BussinessCodeParameter BussinessCodeParameter = getBussinessCodeParameter(busiType, trdProductDeal);
                        BussinessCodeParameter req = new BussinessCodeParameter();
                        req.setMapInfo(BussinessCodeParameter.initMapInfo());
                        deal.setTradeId(restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectRule",
                                req, String.class));
                        trdProductDealAdd.setTradeId(deal.getTradeId());
                        
                      //交易费用不为空 入库交易费用信息
                        if(!StringUtil.isEmptyList(btpdd.getTrdTradeFeeDetailList())){
                        	btpdd.getTrdTradeFeeDetailList().forEach(fee->{
                        		fee.setTradeId(deal.getTradeId());
                        		trdTradeFeeDetailMapper.insertSelective(fee);
                        	});
                        }
                        //交易费用不为空 入库交易费用信息 end
//                        反向交易子组合
                        List<PtlTrade>ptlTradeListTemp = new ArrayList<>();

                        //通道费不为空 入库通道费信息
                        TrdTradeFeeDetail feeDetail = null;
                        if(btpdd.getTrdTradeFeeDetail()!=null){
                        	feeDetail = new TrdTradeFeeDetail();
                        	BeanUtil.copyPropertiesIgnoreNull(feeDetail,btpdd.getTrdTradeFeeDetail());
                            feeDetail.setTradeId(deal.getTradeId());
                            trdTradeFeeDetailMapper.insertSelective(feeDetail);
                        }
                        //通道费不为空 入库通道费信息 end

                        if (btpdd.getPtlTrade() != null) {
                            // 交易组合是非母子结构组合，交易对手为子组合是bug修正 modify by renping 20191031 start
                            PtlTrade ptlTradeTemp = new PtlTrade();
                            List<PtlTrade> ptlList = new ArrayList<>();
                            BeanUtil.copyPropertiesIgnoreNull(ptlTradeTemp, btpdd.getPtlTrade().get(0));
                            ptlTradeTemp.setBusiNo(deal.getTradeId());
                            ptlTradeTemp.setPortfolioId(交易对手的母组合);
                            ptlTradeMapper.insert(ptlTradeTemp);
                            ptlTradeListTemp.add(ptlTradeTemp);
                            ptlList.add(ptlTradeTemp);
                            if (交易对手_子组合 != null && !交易对手_子组合.equals(交易对手的母组合)) {
                                ptlTradeTemp = new PtlTrade();
                                BeanUtil.copyPropertiesIgnoreNull(ptlTradeTemp, btpdd.getPtlTrade().get(0));
                                ptlTradeTemp.setBusiNo(deal.getTradeId());
                                ptlTradeTemp.setPortfolioId(交易对手_子组合);
                                ptlTradeMapper.insert(ptlTradeTemp);
                                ptlTradeListTemp.add(ptlTradeTemp);
                                ptlList.add(ptlTradeTemp);
                            }
                            btpdd.setPtlTrade(ptlList);
                            // btpdd.getPtlTrade().forEach(ptlTrade -> {
                            // ptlTrade.setBusiNo(deal.getTradeId());
                            // if (ptlTrade.getPortfolioId().equals(原交易母组合代码)) {
                            // ptlTrade.setPortfolioId(交易对手的母组合);
                            // }
                            // else if (交易对手_子组合 != null && !交易对手_子组合.equals(交易对手的母组合)) {
                            // ptlTrade.setPortfolioId(交易对手_子组合);
                            // }
                            // ptlTradeMapper.insert(ptlTrade);
                            // });
                            // 交易组合是非母子结构组合，交易对手为子组合是bug修正 modify by renping 20191031 end
                        }

                        if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
                            deal.setPs(DictCodeList.交易方向_卖出.getValue());
                            // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                            // 对应关系现在先确认这债券和存款类交易，待优化补充 yjm start 20190730
                            if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)) {
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产卖出.getValue());
                            }else if(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_赎回.getValue());
                            }else if(DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_项目类资产卖出.getValue());
                            }
                            // add by etliu 20210421 权益类资产相关代码合并 start
                            else if(DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_卖出.getValue());
                            }
                            // add by etliu 20210421 权益类资产相关代码合并 end
                            else {
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产卖出.getValue());
                            }
                            // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                            // 对应关系现在先确认这债券和存款类交易，待优化补充 yjm end 20190730

                        }
                        else if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
                            deal.setPs(DictCodeList.交易方向_买入.getValue());
                            // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                            // 对应关系现在先确认这债券和存款类交易，待优化补充 yjm start 20190730
                            if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)) {
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_债券类资产买入.getValue());
                            }else if(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                            }else if(DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_项目类资产买入.getValue());
                            }
                            // add by etliu 20210421 权益类资产相关代码合并 start
                            else if(DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt3.平安业务类型_权益类资产_买入.getValue());
                            }
                            // add by etliu 20210421 权益类资产相关代码合并 end
                            else {
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_存款类资产买入.getValue());
                            }
                            paTrdProductDeal.setDeptCode(LoginUserUtil.getDeptCode());//内转交易没有存部门信息
                            // 交易类型和业务类型的对应，若是存错，现金流查询页面展示错误，
                            // 对应关系现在先确认这债券和存款类交易，待优化补充 end start 20190730
                            if(allTradeCommonInputDto.getTurnInvAim()!=null){
                                deal.setInvAim(allTradeCommonInputDto.getTurnInvAim());
                            }
                        }
                        // 反向金融产品交易登陆
                        //内转交易过滤通道信息
                        String chlid = deal.getChlId();
                        if(StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
                        		||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
                        		||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
                    			||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
                    			){
                        	deal.setChlId(null);
                        }
                        else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())){
                        	//IS_CAL_LOADRATE 01 借款利率  
                        	if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE),"01")){
                        		deal.setChlId(null);
                        	}
                        }
                        trdProductDealMapper.insert(deal);
                        // add  by  meifaxin 2021-9-26 busiType插入后值被修改了，重新赋值改回来  tart
                        //busiType入库之后被重新赋值，赋值回来
                        deal.setBusiType(busiType);
                        // add  by  meifaxin 2021-9-26 busiType插入后值被修改了，重新赋值改回来  end
                        deal.setChlId(chlid);
                        // 反向金融产品交易附属表登陆
                        trdProductDealAddMapper.insert(trdProductDealAdd);
                        if (paTrdProductDeal != null) {
                            paTrdProductDeal.setTradeId(deal.getTradeId());
                            // 反向金融产品交易扩展登陆
                            paTrdProductDealMapper.insert(paTrdProductDeal);
                        }

                        // 反向交易附件不生成，以下代码废除
                        // MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
                        // List<SysUploadFileInfo> fileList = new ArrayList<>();
                        // for (SysUploadFileInfo sufi : btpdd.getMstFileOutputParameter().getUploadFileList()) {
                        // SysUploadFileInfo file = new SysUploadFileInfo();
                        // BeanUtil.copyPropertiesIgnoreNull(file, sufi);
                        // file.setBusinessId(trdProductDeal.getTradeId());
                        // fileList.add(file);
                        // }
                        // mstFileInputParameter.setFileList(fileList);
                        // mstFileInputParameter.setBussinessId(trdProductDeal.getTradeId());

                        copyFileInfo(btpdd.getTrdProductDeal().getTradeId(), deal.getTradeId());
                        List<PtlCashPositionFlowDto> ptlCashPositionFlows = new ArrayList<>();

                        // 反向划款申请登陆
                        List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                        if (!StringUtil.isEmptyList(btpdd.getStlPayOrdersList())) {
                            for (StlPayOrdersDto stlPayOrdersDto : btpdd.getStlPayOrdersList()) {
                                StlPayOrdersDto stl = new StlPayOrdersDto();
                                BeanUtil.copyPropertiesIgnoreNull(stl, stlPayOrdersDto);
                                BussinessCodeParameter bussinessCodeParameter1 = new BussinessCodeParameter();
                                bussinessCodeParameter1.setSeqNo("9998");
                                // 生成的业务编号
                                String orderId = restTemplate.postForObject(
                                    ServiceApplication.COMMON_SERVICE_NAME + "/core/selectRule", bussinessCodeParameter1,
                                    String.class);
                                stl.setOrderId(StringUtil.isNotEmpty(orderId) ? orderId : IDGenerator.generate());

                                // 划款信息（如有）中的付款方和收款方对调；
                                String myAcctNo = stl.getMyAcctNo();
                                stl.setMyAcctNo(stl.getCounterAcctNo());
                                stl.setCounterAcctNo(myAcctNo);
                                stl.setPs(DictCodeListExt1.收支方向_支出.getValue().equals(
                                    stl.getPs()) ? DictCodeListExt1.收支方向_收入.getValue() : DictCodeListExt1.收支方向_支出.getValue());
                                if(!(DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType) || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType))){
                                stl.setTradeId(deal.getTradeId());} //项目类和计划类利率型交易存的是汇总交易编号； add by zhouxiuping
                                // add by zoujiamin ZGBSSE-256 start
                                if(DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)){
                                    stl.setTradeId(deal.getTotleTradeId());
                                }
                                // add by zoujiamin ZGBSSE-256 end  暂时只针对债券调整 
                                stlPayOrdersMapper.insert(stl);
                                stlPayOrders.add(stl);
                                ptlCashPositionFlows.addAll(
                                    getPtlCashPositionFlowDto(deal, trdProductDealAdd, stl, 交易对手_子组合, 交易对手的母组合,DictCodeList.持仓类型_实际));
                            }
                        }

                        // TODO 写入持仓和现金
                        PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();

                        List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
                        genSecFlowDto(deal, trdProductDealAdd, ptlSecPositionFlows, busiType, 交易对手_子组合, 交易对手的母组合);

                        ptlPositionHoldDto.setPtlSecPositionFlows(ptlSecPositionFlows);
                        ptlPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);

                        if (ptlPositionHoldDto.getPtlSecPositionFlows() != null
                            && !ptlPositionHoldDto.getPtlSecPositionFlows().isEmpty()) {
                            for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlPositionHoldDto.getPtlSecPositionFlows()) {
                                basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlow);
                            }
                        }
                        if (ptlPositionHoldDto.getPtlCashPositionFlows() != null
                            && !ptlPositionHoldDto.getPtlCashPositionFlows().isEmpty()) {
                            for (PtlCashPositionFlowDto ptlCashPositionFlow : ptlPositionHoldDto.getPtlCashPositionFlows()) {
                                basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
                            }
                        }

                        // 增加委托指令流水逻辑
                        if (baseEtrCashPositionCommService != null) {
                            baseEtrCashPositionCommService.portfolioEtrCashPosition(ptlPositionHoldDto, false);
                        }

                        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                        inputDto.setProdVdate(new Date(deal.getSettleDate().getTime()));
                        inputDto.setTrdProductDeal(deal);
                        inputDto.setStlPayOrders(stlPayOrders.get(0));
                        // 场内基金改造 add by renping 20191117 start
                        if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                            && !StringUtil.isEmptyList(btpdd.getStlPayOrdersList())) {
                            inputDto.setStlPayOrders(btpdd.getStlPayOrdersList().get(0));
                        }
                        // 是否内部转仓交易
                        inputDto.setIsSwapTransaction(DictCodeList.是否_是.getValue());
                        // 场内基金改造 add by renping 20191117 end
                        if (!StringUtil.equals(交易对手_子组合, 交易对手的母组合)) {
                            inputDto.setProdid(交易对手_子组合);
                            if (ptlTradeListTemp != null && ptlTradeListTemp.size() > 0) {
                                inputDto.setPtlTradeList(ptlTradeListTemp);
                            }
                        }
                        else {
                            inputDto.setProdid(交易对手的母组合);
                        }
                        
                        inputDto.setTrdProductDealAdd(trdProductDealAdd);
                        //busiType入库之后被重新赋值，赋值回来
                        deal.setBusiType(busiType);
                        //通道费不为空 入库通道费信息
                        if(feeDetail!=null){
                            inputDto.setTrdTradeFeeDetail(feeDetail);
                        }
                        //通道费不为空 入库通道费信息 end
                        
                        // 是否内部转仓交易
                        inputDto.setIsSwapTransaction(DictCodeList.是否_是.getValue());
                        // 场内基金改造 
                        
                        AllTradeCommonUtil.调用估值接口(inputDto);

                        // 调用投管接口 add by renping 20190205 start
                        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
                        investInterfCommDto.setPageId(allTradeCommonInputDto.getPageId());
                        investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
                        investInterfCommDto.setPortfolioId(交易对手_子组合);
                        investInterfCommDto.setTrdProductDeal(trdProductDeal);
                        investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
                        investInterfCommDto.setStlPayOrders(inputDto.getStlPayOrders());
                        investInterfCommDto.setTradeFeeDetaillist(btpdd.getTrdTradeFeeDetailList());
                        // 计划类与基金的交易时接口交易类型转换还需要一个用于转换的类型
                        if (DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)
                            || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)
                            || DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)) {
                            investInterfCommDto.setOtherTypeForConvert(busiType);
                        }
                        else if (StringUtil.equals(allTradeCommonInputDto.getPageId(),
                            "TrdFundInvestTrade002.02")) {
                            investInterfCommDto.setOtherTypeForConvert(
                                DictCodeList.基金类资产类型_货币基金.getValue());
                        }
                        else if (StringUtil.equals(allTradeCommonInputDto.getPageId(),
                            "TrdFundInvestTrade002.01")) {
                            investInterfCommDto.setOtherTypeForConvert(
                                DictCodeList.基金类资产类型_非货币基金.getValue());
                        }
                        investInterfCommService.doInvestInterf(investInterfCommDto);
                        // 调用投管接口 add by renping 20190205 end
                    }
                }
            }

            if (!StringUtil.isEmptyList(baseReceTradeProductDealDtoList)) {
                for (BaseTradeProductDealDto btpdd : baseReceTradeProductDealDtoList) {
                    if (btpdd.getTrdProductDeal() == null) {
                        continue;
                    }

                    PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(btpdd.getTrdProductDeal().getCounterId());
                    if (mcp != null) { // 内部交易对手
                        TrdProductDeal deal = new TrdProductDeal();
                        BeanUtil.copyPropertiesIgnoreNull(deal, btpdd.getTrdProductDeal());
                        deal.setrTradeId(btpdd.getTrdProductDeal().getTradeId());//方向交易编号
                        deal.setTotleTradeId(allTradeCommonInputDto.getTotleTradeId());//反向汇总交易编号

                        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                        if (btpdd.getTrdProductDealAdd() != null) {
                            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, btpdd.getTrdProductDealAdd());
                        }
                        String 交易对手_子组合 = deal.getCounterId();// 子组合交易对手
                        String 交易对手的母组合 = AllTradeCommonUtil.exchangePortfolio(deal.getCounterId());// 交易对手的母组合，设置成反向交易的组合代码

                        // 组合代码与交易对手对调
                        // 交易方向为买入；
                        // 其他字段与原始交易相同；
                        // String 原交易母组合代码 = trdProductDealAdd.getPortfolioId();
                        String 原交易子组合代码 = btpdd.getActPortfolioId();
                        deal.setCounterId(原交易子组合代码);
                        trdProductDealAdd.setPortfolioId(交易对手的母组合);
                        BussinessCodeParameter BussinessCodeParameter = getBussinessCodeParameter(busiType, trdProductDeal);
                        BussinessCodeParameter req = new BussinessCodeParameter();
                        req.setMapInfo(BussinessCodeParameter.initMapInfo());
                        deal.setTradeId(restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/selectRule",
                                req, String.class));
                        trdProductDealAdd.setTradeId(deal.getTradeId());
                        // 反向交易子组合
                        List<PtlTrade> ptlTradeListTemp = new ArrayList<>();
                        if (btpdd.getPtlTrade() != null) {
                            // 交易组合是非母子结构组合，交易对手为子组合是bug修正 modify by renping 20191031 start
                            PtlTrade ptlTradeTemp = new PtlTrade();
                            List<PtlTrade> ptlList = new ArrayList<>();
                            BeanUtil.copyPropertiesIgnoreNull(ptlTradeTemp, btpdd.getPtlTrade().get(0));
                            ptlTradeTemp.setBusiNo(deal.getTradeId());
                            ptlTradeTemp.setPortfolioId(交易对手的母组合);
                            ptlTradeMapper.insert(ptlTradeTemp);
                            ptlTradeListTemp.add(ptlTradeTemp);
                            ptlList.add(ptlTradeTemp);
                            if (交易对手_子组合 != null && !交易对手_子组合.equals(交易对手的母组合)) {
                                ptlTradeTemp = new PtlTrade();
                                BeanUtil.copyPropertiesIgnoreNull(ptlTradeTemp, btpdd.getPtlTrade().get(0));
                                ptlTradeTemp.setBusiNo(deal.getTradeId());
                                ptlTradeTemp.setPortfolioId(交易对手_子组合);
                                ptlTradeMapper.insert(ptlTradeTemp);
                                ptlTradeListTemp.add(ptlTradeTemp);
                                ptlList.add(ptlTradeTemp);
                            }
                            btpdd.setPtlTrade(ptlList);
                        }

                        if (DictCodeList.交易方向_应收款项转出.getValue().equals(deal.getPs())) {
                            deal.setPs(DictCodeList.交易方向_应收款项转入.getValue());
                        }
                        else if (DictCodeList.交易方向_应收款项转入.getValue().equals(deal.getPs())) {
                            deal.setPs(DictCodeList.交易方向_应收款项转出.getValue());
                        }
                        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
                        if (btpdd.getPaTrdProductDeal() != null) {
                            BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, btpdd.getPaTrdProductDeal());
                        }
                        // 反向金融产品交易登陆
                        //内转交易过滤通道信息
                        String chlid = deal.getChlId();
                        if(StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue())
                        		||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产交易.getValue())
                        		||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_项目类资产存续期.getValue())
                    			||StringUtil.equals(deal.getBusiType(), DictCodeList.共通业务类型_计划类资产_利率型_存续期.getValue())
                    			){
                        	deal.setChlId(null);
                        }
                        else if (DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())){
                        	//IS_CAL_LOADRATE 01 借款利率  
                        	if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.IS_CAL_LOADRATE),"01")){
                        		deal.setChlId(null);
                        	}
                        }
                        trdProductDealMapper.insert(deal);
                        deal.setChlId(chlid);
                        // 反向金融产品交易附属表登陆
                        trdProductDealAddMapper.insert(trdProductDealAdd);

                        // 反向交易附件处理
                        copyFileInfo(btpdd.getTrdProductDeal().getTradeId(), deal.getTradeId());

                        if (paTrdProductDeal != null) {
                            paTrdProductDeal.setTradeId(deal.getTradeId());
                            if(DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_计划类资产_利率型_申购.getValue());
                            }else if(DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)){
                                paTrdProductDeal.setPaBussType(DictCodeListExt1.平安业务类型_项目类资产买入.getValue());
                            }
                            // 反向金融产品交易扩展登陆
                            paTrdProductDealMapper.insert(paTrdProductDeal);
                        }
                        //应收款流水-买入增加一笔实际应收款流水
                        List<BaseTradeProductDealDto> list=new ArrayList<>();
                        FinProductTradeDto finDto = new FinProductTradeDto();
                        finDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                        BaseTradeProductDealDto baseDto = new BaseTradeProductDealDto();
                        baseDto.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                        baseDto.setTrdProductDeal(deal);
                        baseDto.setTrdProductDealAdd(trdProductDealAdd);
                        baseDto.setPayDate(btpdd.getPayDate());
                        list.add(baseDto);
                    finDto.setBaseTradeProductDealDtos(list);
                    finDto.setNeedSum(true);
                    basePtlRecePositionService.insertPosition(finDto);
                    if (交易对手_子组合 != null && !交易对手_子组合.equals(交易对手的母组合)) {
                        finDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(交易对手_子组合));
                        basePtlRecePositionService.insertPosition(finDto);
                        finDto.getBaseTradeProductDealDtos().forEach(
                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(交易对手的母组合));
                        finDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                            finProductTrade.setActPortfolioId(交易对手_子组合);
                        });
                    }


                        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                        inputDto.setProdVdate(new Date(deal.getSettleDate().getTime()));
                        inputDto.setTrdProductDeal(deal);
                        // 是否内部转仓交易
                        inputDto.setIsSwapTransaction(DictCodeList.是否_是.getValue());
                        if (!StringUtil.equals(交易对手_子组合, 交易对手的母组合)) {
                            inputDto.setProdid(交易对手_子组合);
                            if (ptlTradeListTemp != null && ptlTradeListTemp.size() > 0) {
                                inputDto.setPtlTradeList(ptlTradeListTemp);
                            }
                        }
                        else {
                            inputDto.setProdid(交易对手的母组合);
                        }
                        //busiType入库之后被重新赋值，赋值回来
                        deal.setBusiType(busiType);
                        AllTradeCommonUtil.调用估值接口(inputDto);
                    }
                }
            }
        }
    }

    public static void copyFileInfo(String 原交易编号, String 新交易编号) {
        Map<String, Object> params = new HashMap<>();
        params.put("nTradeId", 新交易编号);
        params.put("pTradeId", 原交易编号);
        params.put("createUser", SecurityUtil.getLoginUser().getUserName());
        params.put("seqNo", IDGenerator.generate());
        trdCommonBaseMapper.insertSelectFileInfo(params);
    }

    public static void copyFileInfoByFileNo(String fileNo, String 新交易编号) {
        Map<String, Object> params = new HashMap<>();
        params.put("nTradeId", 新交易编号);
        params.put("fileNo", fileNo);
        params.put("createUser", SecurityUtil.getLoginUser().getUserName());
        params.put("seqNo", IDGenerator.generate());
        //外部交易对手买入卖出交易不会生成交易编号，也不用插入附件信息
        if(StringUtil.isNotBlank(新交易编号)){
            trdCommonBaseMapper.insertSelectFileInfoByFileNo(params);
        }
    }

    public static List<PtlCashPositionFlowDto> getPtlCashPositionFlowDto(TrdProductDeal deal,
                                                                          TrdProductDealAdd trdProductDealAdd,
                                                                          StlPayOrdersDto stl,String 交易对手_子组合,
                                                                          String 交易对手的母组合, DictCodeList 持仓类型) {
        String busiType = deal.getBusiType();
        List<PtlCashPositionFlowDto> resultDtos = new ArrayList<>();
        PtlCashPositionFlowDto resultDto = new PtlCashPositionFlowDto();
        // 输入参数.组合代码
        resultDto.setPortfolioId(交易对手的母组合);
        // 划款指令Entity.本方账户
        resultDto.setAcctId(stl.getMyAcctNo());
        // 输入参数.持仓类型
        resultDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        // 划款指令Entity.币种
        resultDto.setCcy(stl.getCcy());
        // 划款指令Entity.指令编号
        resultDto.setOrderId(stl.getOrderId());
        //      是否拆分登录为已拆分
        resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        List<String> accountTypes = baseAssetCashPositionCommMapper.selectAccountType(交易对手的母组合, stl.getMyAcctNo());
        String payType = null;
        if (StringUtil.isNotEmpty(deal.getChlId())) {
            payType = baseAssetCashPositionCommMapper.getPayType(deal.getChlId(), stl.getMyAcctNo());
            if (StringUtil.isNotEmpty(payType)) {
                resultDto.setChlFinprodId(deal.getChlId());
            }
        }
        if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
            && ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                 && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()))
                || (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                    && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                    && !DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat())
                    && accountTypes.contains(DictCodeListExt1.资金账户类型_产品托管户.getValue()))
                || (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                && !DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat())
                && StringUtil.isNotEmpty(payType))
                || (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                    && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                    && DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat()))
                || (DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)
                    || DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)
                    || DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)
                // add by etliu 20210421 权益类资产相关代码合并 start
                    || DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)))) {
                // add by etliu 20210421 权益类资产相关代码合并 end
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(stl.getSettleAmt(), new BigDecimal(-1)));
        }
        else if ((DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                  && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                  && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                  && !DictCodeList.交易平台_外汇交易中心.getValue().equals(deal.getTradePlat())
                  && accountTypes.contains(DictCodeListExt1.资金账户类型_券商资金账户.getValue()))
                 || (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                     && DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2()))
                 || (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                     && DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType))
                 || (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                     && DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType))
                 || (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
                     && DictCodeListExt2.共通业务类型_股票投资交易.getValue().equals(busiType))) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(ArithmeticUtil.multiply(stl.getSettleAmt(), new BigDecimal(-1)));
            resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
        }
        else if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
                 && (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(deal.getFinprodType2())
                     || DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(deal.getFinprodType2())
                     || DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)
                     || DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)
                     || DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)
                     || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)
                     || DictCodeListExt2.共通业务类型_股票投资交易.getValue().equals(busiType))
                // add by etliu 20210421 权益类资产相关代码合并 start
                     || DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)) {
                // add by etliu 20210421 权益类资产相关代码合并 end
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(stl.getSettleAmt());
        } else if (DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())
                && ( DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)
                || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType))) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(stl.getSettleAmt());
            resultDto.setSplitType(DictCodeListExt3.拆分标识_已拆分.getValue());
        }

        if (StringUtil.isNotEmpty(交易对手_子组合) && !交易对手_子组合.equals(交易对手的母组合)) {
            PtlCashPositionFlowDto resultDto1 = new PtlCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(resultDto1, resultDto);
            resultDto1.setPortfolioId(交易对手_子组合);
            resultDtos.add(resultDto1);
        }
        resultDtos.add(resultDto);
        return resultDtos;
    }

    private static void genSecFlowDto(TrdProductDeal deal, TrdProductDealAdd trdProductDealAdd,
                                      List<PtlSecPositionFlowDto> ptlSecPositionFlows, String busiType, String 交易对手_子组合,
                                      String 交易对手的母组合) {
        PtlSecPositionFlowDto resultDto = new PtlSecPositionFlowDto();
        resultDto.setPortfolioId(交易对手的母组合);
        String secAcctId = com.github.pagehelper.util.StringUtil.isEmpty(
            deal.getSecManageAcctId()) ? deal.getChlId() : deal.getSecManageAcctId();
        resultDto.setSecAcctId(com.github.pagehelper.util.StringUtil.isEmpty(secAcctId) ? "999999" : secAcctId);
        resultDto.setFinprodId(deal.getFinprodId());
        resultDto.setBranch(0L);
        resultDto.setInvAim(com.github.pagehelper.util.StringUtil.isEmpty(deal.getInvAim()) ? "999999" : deal.getInvAim());
        resultDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        resultDto.setCcy(deal.getCcy());
        resultDto.setTradeId(deal.getTradeId());
        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)
            && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
            && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
        }
        else if (((DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)
                   && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()))
                  || DictCodeList.共通业务类型_存款类资产交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)
                // add by etliu 20210421 权益类资产相关代码合并 start
                  || DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType))
                // add by etliu 20210421 权益类资产相关代码合并 end
                 && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
        }
        else if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)
                 && DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket())
                 && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(deal.getShareAmt());
        }
        else if (((DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)
                   && !DictCodeListExt1.交易场所_银行间.getValue().equals(deal.getTradeMarket()))
                  || DictCodeList.共通业务类型_存款类资产交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)
                  || DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)
                // add by etliu 20210421 权益类资产相关代码合并 start
                  || DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType))
                // add by etliu 20210421 权益类资产相关代码合并 end
                 && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(deal.getShareAmt());
        }
        else if (DictCodeListExt2.共通业务类型_股票投资交易.getValue().equals(busiType)
                 && DictCodeList.交易方向_卖出.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getShareAmt(), new BigDecimal(-1)));
        }
        else if (DictCodeListExt2.共通业务类型_股票投资交易.getValue().equals(busiType)
                 && DictCodeList.交易方向_买入.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(deal.getShareAmt());
        }

        if (StringUtil.isNotEmpty(交易对手_子组合) && !交易对手_子组合.equals(交易对手的母组合)) {
            PtlSecPositionFlowDto resultDto1 = new PtlSecPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(resultDto1, resultDto);
            resultDto1.setPortfolioId(交易对手_子组合);
            ptlSecPositionFlows.add(resultDto1);
        }

        ptlSecPositionFlows.add(resultDto);
    }

    /**
     * 生成金融产品交易反向交易时，构造生成业务编码用参数
     *
     * @param busiType busiType
     * @param negateDeal negateDeal
     * @return BussinessCodeParameter
     */
    private static BussinessCodeParameter getBussinessCodeParameter(String busiType, TrdProductDeal negateDeal) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (DictCodeList.共通业务类型_债券类资产交易.getValue().equals(busiType)) {
            if (StringUtil.equals(DictCodeList.交易方向_买入.getValue(), negateDeal.getPs())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券卖出);
            }
            else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            }
        }
        else if (DictCodeList.共通业务类型_存款类资产交易.getValue().equals(busiType)) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_存款录入);
            if (DictCodeList.存款类资产类型_同业借款.getValue().equals(negateDeal.getFinprodType())) {
                codeParameter.setType1(DictCodeList.业务编号一级分类_同业借款);
            }
            else {
                codeParameter.setType1(DictCodeList.业务编号一级分类_存放同业);
            }
        }
        else if (DictCodeList.共通业务类型_基金类资产交易.getValue().equals(busiType)) {
            if (StringUtil.equals(negateDeal.getPs(), DictCodeList.交易方向_申购.getValue())
                || StringUtil.equals(negateDeal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金申购);
            }
            else {
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_货币基金赎回);
            }
        }
        else if (DictCodeListExt2.共通业务类型_股票投资交易.getValue().equals(busiType)) {
            codeParameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_股票投资交易));
        }
        else {
            String noBranchNoValue = restTemplate.getForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectParam/" + "MST" + "/"
                                                               + BussinessConstant.NOBRANCHNOVALUE,
                String.class);
            Long branch = Long.valueOf(noBranchNoValue);
            if (DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue().equals(busiType)) {
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(negateDeal.getFinprodId());
                key.setBranch(branch);
                FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易);
                if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_公募计划.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_公募计划);
                }
                else if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
                }
                else if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
                }
                else if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
                }
            }
            else if (DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue().equals(busiType)) {
                if (StringUtil.equals(negateDeal.getPs(), DictCodeList.交易方向_买入.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
                    if (StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, negateDeal.getFinprodType())) {
                        codeParameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                    }
                    else if (StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, negateDeal.getFinprodType())) {
                        codeParameter.setType1(DictCodeList.业务编号一级分类_信托产品);
                    }
                    else {
                        codeParameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                    }
                }
                else if (StringUtil.equals(negateDeal.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_净值型资产申购);
                    if (StringUtil.equals(DictCodeList.计划类资产一级分类_券商资产管理计划, negateDeal.getFinprodType())) {
                        codeParameter.setType1(DictCodeList.业务编号一级分类_券商资产管理计划);
                    }
                    else if (StringUtil.equals(DictCodeList.计划类资产一级分类_信托产品, negateDeal.getFinprodType())) {
                        codeParameter.setType1(DictCodeList.业务编号一级分类_信托产品);
                    }
                    else {
                        codeParameter.setType1(DictCodeList.业务编号一级分类_保险资产管理计划);
                    }
                }
            }
            else if (DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue().equals(busiType)) {
                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(negateDeal.getFinprodId());
                key.setBranch(branch);
                FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易);
                if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
                }
                else if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
                }
                else if (StringUtil.equals(finProductType.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())) {
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
                }
            }
            else if (DictCodeList.共通业务类型_项目类资产交易.getValue().equals(busiType)) {
                DictCodeList item = DictCodeList.共通业务类型_项目类资产买入;
                if (DictCodeList.交易方向_卖出.getValue().equals(negateDeal.getPs())) {
                    item = DictCodeList.共通业务类型_项目类资产卖出;
                }
                codeParameter.setCommonBusinessType(item);

                FinProductTypeKey key = new FinProductTypeKey();
                key.setFinprodId(negateDeal.getFinprodId());
                key.setBranch(branch);
                FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);

                codeParameter.setType1(类型转换(finProductType.getType6()));
            }
            // add by etliu 20210421 权益类资产相关代码合并 start
            else if (DictCodeListExt3.共通业务类型_权益类资产投资交易.getValue().equals(busiType)) {
                codeParameter.setCommonBusinessType(DictCodeListExt3.共通业务类型_权益类资产投资交易);
            }
            // add by etliu 20210421 权益类资产相关代码合并 end
        }
        return codeParameter;
    }

    private static DictCodeList 类型转换(String projectType) {
        DictCodeList result = null;
        if (DictCodeList.项目类一级分类_非标债权.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_非标债权;
        }
        else if (DictCodeList.项目类一级分类_理财直接融资工具.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_理财直融;
        }
        else if (DictCodeList.项目类一级分类_信贷资产流转项目.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_信贷资产流转项目;
        }
        else if (DictCodeList.项目类一级分类_基础设施基金.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_基础设施基金;
        }
        else if (DictCodeList.项目类一级分类_其他产业投资基金.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_其他产业投资基金;
        }
        else if (DictCodeList.项目类一级分类_其他权益类投资.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_信托产品;
        }
        else if (DictCodeList.项目类一级分类_结构化证券.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_结构化证券;
        }
        else if (DictCodeList.项目类一级分类_股票定向增发投资.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_股票定向增发;
        }
        else if (DictCodeList.项目类一级分类_券商收益凭证.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_券商收益凭证;
        }
        else if (DictCodeList.项目类一级分类_券商债权收益权投资.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_券商资产管理计划;
        }
        else if (DictCodeList.项目类一级分类_股票质押式回购.getValue().equals(projectType)) {
            return DictCodeList.业务编号一级分类_股票质押式回购;
        }
        return result;
    }

    public static TrdTradeFeeDetail genChannelTradeFeeDetail(String tradeId, BigDecimal channelAmt) {
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(tradeId);
        // 交易费用明细表.费用类型 = “通道费”
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        // 交易费用明细表.是否随交易支付 = “是”
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        // 交易费用明细表.交易金额 = 交易.通道费
        trdTradeFeeDetail.setFeeAmt(channelAmt);
        return trdTradeFeeDetail;
    }


    /**
     * 根据收取方式判断是否随交易支付
     *
     * @param tradeId
     * @param channelAmt
     * @return
     */
    public static TrdTradeFeeTotal genChannelTradeFeeDetailTotle(String tradeId, BigDecimal channelAmt, String collectType,
                                                            String isOutTrade) {
        TrdTradeFeeTotal trdTradeFeeDetail = new TrdTradeFeeTotal();
        trdTradeFeeDetail.setTotleTradeId(tradeId);
        // 交易费用明细表.费用类型 = “通道费”
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        // 交易费用明细表.是否随交易支付 内部交易对手随交易支付 外部交易对手 根据收取方式判断
        if (StringUtil.equals(isOutTrade, DictCodeList.是否_否.getValue())) {
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        }
        else {
            if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), collectType)) {
                trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            }
            else {
                trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            }
        }
        // 交易费用明细表.交易金额 = 交易.通道费
        trdTradeFeeDetail.setFeeAmt(channelAmt);
        return trdTradeFeeDetail;
    }

    /**
     * 根据收取方式判断是否随交易支付
     *
     * @param tradeId
     * @param channelAmt
     * @return
     */
    public static TrdTradeFeeDetail genChannelTradeFeeDetail(String tradeId, BigDecimal channelAmt, String collectType,String isOutTrade) {
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(tradeId);
        // 交易费用明细表.费用类型 = “通道费”
        trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
        // 交易费用明细表.是否随交易支付 内部交易对手随交易支付 外部交易对手 根据收取方式判断
        if(StringUtil.equals(isOutTrade, DictCodeList.是否_否.getValue())){
        	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        }else{if (StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), collectType)) {
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
        }
        else {
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());}
        }
        // 交易费用明细表.交易金额 = 交易.通道费
        trdTradeFeeDetail.setFeeAmt(channelAmt);
        return trdTradeFeeDetail;
    }

    /**
     * 根据登记编号删除 可用持仓和现金流水
     * @param revokeReason
     * @param busiCode
     */
    public static void deleteRegPosition(String revokeReason, String busiCode) {
        //BSTOT-1822撤销退回 处理撤销交易编号
        if(StringUtil.isNotEmpty(revokeReason)) {
            if (busiCode.substring(busiCode.length() - 1).equals("R")) {
                busiCode = busiCode.substring(0, busiCode.length() - 1);
            }
            AllTradeCommonUtil.deleteRegPositionReal(busiCode);
        }
        //BSTOT-1822撤销退回 处理撤销交易编号
        else {
            AllTradeCommonUtil.deleteRegPositionReal(busiCode);
        }
    }

    public static void deletePosition(String revokeReason, String busiCode) {
        //BSTOT-1822撤销退回 处理撤销交易编号
        if(StringUtil.isNotEmpty(revokeReason)) {
            if (busiCode.lastIndexOf("R") != busiCode.length() - 1) {
                busiCode = busiCode + "R";
            }
            AllTradeCommonUtil.deletePositionReal(busiCode);
        }
        //BSTOT-1822撤销退回 处理撤销交易编号
        else {
            AllTradeCommonUtil.deletePositionReal(busiCode);
        }
    }

    /**
     * 根据登记编号删除 可用持仓和现金流水
     * @param tradeId
     */
    public static void deleteRegPositionReal(String tradeId) {
        //处理可用持仓流水
        List<PtlSecPositionFlow> flows = baseAssetCashPositionCommMapper.selectAllPosition(tradeId).stream()
                .filter( flow -> DictCodeList.持仓类型_可用.getValue().equals(flow.getHodingType())).collect(Collectors.toList());
        if (!flows.isEmpty()) {
            baseAssetCashPositionCommMapper.deletePositionByTradeId(tradeId);
        }

        //处理可用现金流水
        List<String> csahorderIds = new ArrayList<>();
        csahorderIds.add(tradeId);
        List<PtlCashPositionFlow> cashFlows = baseAssetCashPositionCommMapper.selectAllCashPosition(csahorderIds).stream()
                .filter( flow -> DictCodeList.持仓类型_可用.getValue().equals(flow.getHodingType())).collect(Collectors.toList());;
        if (!cashFlows.isEmpty()) {
            baseAssetCashPositionCommMapper.deleteCashPositionByOrderId(tradeId);
        }
    }
    /**
     * 根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓</br>
     * 如果持仓是质押，融入，融出，在途时，更新持仓表
     *
     * @param tradeId
     */
    public static void deletePositionReal(String tradeId) {
        List<PtlSecPositionFlow> flows = baseAssetCashPositionCommMapper.selectAllPosition(tradeId);
        if (!flows.isEmpty()) {
            baseAssetCashPositionCommMapper.deletePositionByTradeId(tradeId);
            flows.forEach(ptlSecPositionFlow -> {
                if (DictCodeList.持仓类型_在途.getValue().equals(ptlSecPositionFlow.getHodingType())
                    || DictCodeList.持仓类型_质押.getValue().equals(ptlSecPositionFlow.getHodingType())
                    || DictCodeList.持仓类型_融入.getValue().equals(ptlSecPositionFlow.getHodingType())
                    || DictCodeList.持仓类型_融出.getValue().equals(ptlSecPositionFlow.getHodingType())) {
                    baseAssetCashPositionCommMapper.updateSecPositionAmt(ptlSecPositionFlow);
                }
            });
        }
        
        List<PtlRecePositionFlow> receFlows = basePtlRecePositionService.selectAllPosition(tradeId);
        if (!receFlows.isEmpty()) {
        	basePtlRecePositionService.deletePositionByTradeId(tradeId);
            receFlows.forEach(ptlSecPositionFlow -> {
                if (DictCodeList.持仓类型_在途.getValue().equals(ptlSecPositionFlow.getHodingType())) {
                	basePtlRecePositionService.updateSecPositionAmt(ptlSecPositionFlow);
                }
            });
        }

        List<PtlRecePositionFlow> recetotalFlows = basePtlRecePositionService.selectAllPositionBytotelTradeId(tradeId);
        if (!recetotalFlows.isEmpty()) {
            basePtlRecePositionService.deletePositionByTotelTradeId(tradeId);
        }

        List<String> orderIds = baseAssetCashPositionCommMapper.selectOrderIdsByTradeId(tradeId);
        orderIds.add(tradeId);
        if (orderIds.isEmpty()) {
            orderIds = baseAssetCashPositionCommMapper.selectOrderIdsByPTotalTradeId(tradeId);
        }

        if (!orderIds.isEmpty()) {
            List<PtlCashPositionFlow> cashFlows = baseAssetCashPositionCommMapper.selectAllCashPosition(orderIds);
            if (!cashFlows.isEmpty()) {
                baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(orderIds);
                cashFlows.forEach(ptlCashPositionFlow -> {
                    if (DictCodeList.持仓类型_在途.getValue().equals(ptlCashPositionFlow.getHodingType())) {
                        baseAssetCashPositionCommMapper.updateCashPositionAmt(ptlCashPositionFlow);
                    }
                });
            }
        }

        WflTrdProductDealAdd dealAdd = baseAssetCashPositionCommMapper.selectWflTradeByTradeId(tradeId);

        if(dealAdd== null){
            List<WflTrdProductDealAdd>dealAddList = baseAssetCashPositionCommMapper.selectWflTradeByTotalTradeId(tradeId);
            if(CollectionUtils.isNotEmpty(dealAddList)){
                dealAdd = dealAddList.get(0);
            }
        }

        // 退回只处理无委托情况
        if (dealAdd == null || StringUtil.isEmpty(dealAdd.getEntrustId())) {
            if((flows==null || flows.isEmpty()) && dealAdd !=null){//组合持仓
                flows = baseAssetCashPositionCommMapper.selectAllPosition(dealAdd.getTradeId());
                if (!flows.isEmpty()) {
                    baseAssetCashPositionCommMapper.deletePositionByTradeId(dealAdd.getTradeId());
                    flows.forEach(ptlSecPositionFlow -> {
                        if (DictCodeList.持仓类型_在途.getValue().equals(ptlSecPositionFlow.getHodingType())
                                || DictCodeList.持仓类型_质押.getValue().equals(ptlSecPositionFlow.getHodingType())
                                || DictCodeList.持仓类型_融入.getValue().equals(ptlSecPositionFlow.getHodingType())
                                || DictCodeList.持仓类型_融出.getValue().equals(ptlSecPositionFlow.getHodingType())) {
                            baseAssetCashPositionCommMapper.updateSecPositionAmt(ptlSecPositionFlow);
                        }
                    });
                }
            }

            // 指令持仓和现金流水处理
            List<EtrSecPositionFlowDto> etrFlows = baseAssetCashPositionCommMapper.selectAllEtrPosition(tradeId);
            if((etrFlows==null || etrFlows.isEmpty()) && dealAdd !=null){
                etrFlows = baseAssetCashPositionCommMapper.selectAllEtrPosition(dealAdd.getTradeId());
            }
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            etrPositionHoldDto.setPtlSecPositionFlows(etrFlows);
            List<EtrCashPositionFlowDto> ptlCashPositionFlows = baseAssetCashPositionCommMapper.selectAllEtrCashPosition(
                Arrays.asList(tradeId));
            if ((ptlCashPositionFlows == null || ptlCashPositionFlows.isEmpty()) && !orderIds.isEmpty()) {
                ptlCashPositionFlows = baseAssetCashPositionCommMapper.selectAllEtrCashPosition(orderIds);
            }
           if((ptlCashPositionFlows == null || ptlCashPositionFlows.isEmpty()) && dealAdd!=null){
                ptlCashPositionFlows = baseAssetCashPositionCommMapper.selectAllEtrCashPosition(
                        Arrays.asList(dealAdd.getTradeId()));//用交易表的交易编号
            }
           etrPositionHoldDto.setPtlCashPositionFlows(ptlCashPositionFlows);
            if (etrPositionHoldDto.getPtlSecPositionFlows() != null) {
                etrPositionHoldDto.getPtlSecPositionFlows().forEach(
                    etrSecPositionFlowDto -> etrSecPositionFlowDto.setSeqno(getSeqNo()));
            }
            if (etrPositionHoldDto.getPtlCashPositionFlows() != null) {
                etrPositionHoldDto.getPtlCashPositionFlows().forEach(
                    etrCashPositionFlowDto -> etrCashPositionFlowDto.setSeqno(getSeqNo()));
            }
            if (etrCommonCashPositionDealService != null) {
                etrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
            }
        }
    }

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    private static BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    private static BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;

    /**
     * 委托共同Service
     */
    private static BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    /**
     * 目前只针对股票存续期，持仓流水的交易编号存汇总，现金流水的orderId为："O" + 汇总交易编号 根据交易编号，冲销可用，质押，融入，融出，在途FLOW持仓</br>
     * 如果持仓是质押，融入，融出，在途时，更新持仓表
     *
     * @param tradeId
     */
    public static void revokePosition(String tradeId) {
        List<PtlSecPositionFlowDto> flows = baseAssetCashPositionCommMapper.selectAllPositionByTotle(tradeId);
        List<PtlCashPositionFlowDto> cashFlows = baseAssetCashPositionCommMapper.selectAllCashPositionByTotle(tradeId);
        if (flows != null && !flows.isEmpty()) {
            for (PtlSecPositionFlowDto ptlSecPositionFlow : flows) {
                ptlSecPositionFlow.setAmount(ptlSecPositionFlow.getAmount().negate());
                basePtlSecPositionInfoServiceImpl.inertSecPositionFlow(ptlSecPositionFlow);
            }
        }
        if (cashFlows != null && !cashFlows.isEmpty()) {
            for (PtlCashPositionFlowDto ptlCashPositionFlow : cashFlows) {
                ptlCashPositionFlow.setAmount(ptlCashPositionFlow.getAmount().negate());
                basePtlCashPositionInfoServiceImpl.insertCashPositionFlow(ptlCashPositionFlow);
            }
        }
    }

    public static void deletePositionByOrderId(String orderId) {
        if (!orderId.isEmpty()) {
            List<PtlCashPositionFlow> cashFlows = baseAssetCashPositionCommMapper.selectAllCashPosition(Arrays.asList(orderId));
            if (!cashFlows.isEmpty()) {
                baseAssetCashPositionCommMapper.deleteCashPositionByOrderIds(Arrays.asList(orderId));
                cashFlows.forEach(ptlCashPositionFlow -> {
                    if (DictCodeList.持仓类型_在途.getValue().equals(ptlCashPositionFlow.getHodingType())) {
                        baseAssetCashPositionCommMapper.updateCashPositionAmt(ptlCashPositionFlow);
                    }
                });
            }
        }
    }

    /**
     * 如果组合存在父组合，改变参数 trdProductDealAdd 附属表的组合代码为父组合代码<br/>
     * 返回一笔新的父组合的组合交易
     *
     * @param trdProductDealAdd 交易附属表
     * @param ptlTrade 组合交易表
     * @return PtlTrade
     */
    public static PtlTrade exchangePortfolio(TrdProductDealAdd trdProductDealAdd, PtlTrade ptlTrade) {
        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(trdProductDealAdd.getPortfolioId());
        if (portfolio != null && StringUtil.isNotEmpty(portfolio.getParentPortfolioId())) {
            trdProductDealAdd.setPortfolioId(portfolio.getParentPortfolioId());
            PtlTrade ptlTrade1 = new PtlTrade();
            BeanUtils.copyProperties(ptlTrade, ptlTrade1);
            ptlTrade1.setPortfolioId(portfolio.getParentPortfolioId());
            return ptlTrade1;
        }
        return null;
    }

    /**
     * 设置交易附属表的组合代码为母组合代码
     *
     * @param trdProductDealAdd 交易附属表
     */
    public static void exchangePortfolio(WflTrdProductDealAdd trdProductDealAdd) {
        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(trdProductDealAdd.getPortfolioId());
        if (portfolio != null && StringUtil.isNotEmpty(portfolio.getParentPortfolioId())) {
            trdProductDealAdd.setPortfolioId(portfolio.getParentPortfolioId());
        }
    }

    /**
     * 查询母组合代码
     *
     * @param portfolioId 组合代码
     * @return PtlTrade
     */
    public static String exchangePortfolio(String portfolioId) {
        PtlPortfolio portfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
        if (portfolio != null && StringUtil.isNotEmpty(portfolio.getParentPortfolioId())) {
            return portfolio.getParentPortfolioId();
        }
        return portfolioId;
    }

    public static List<Map<String, Object>> getSubPortfolioDispercent(String portfolioId) {
        return baseAssetCashPositionCommMapper.getSubPortfolioDispercent(portfolioId);
    }

    public static String selectWorkflowId(String bizCode) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        String workflowId = trdCommonBaseMapper.selectWorkflowId(bizCode);
        DynamicDataSourceHolder.clear();
        return workflowId;
    }

    public static boolean isNotActPortfolio(String portfolioId) {
        return trdCommonBaseMapper.isNotActPortfolio(portfolioId);
    }

    /**
     * 设置 stlPayOrdersMapper
     *
     * @param stlPayOrdersMapper stlPayOrdersMapper
     */
    @Autowired(required = false)
    public void setStlPayOrdersMapper(StlPayOrdersMapper stlPayOrdersMapper) {
        AllTradeCommonUtil.stlPayOrdersMapper = stlPayOrdersMapper;
    }

    /**
     * 设置 trdProductDealMapper
     *
     * @param trdProductDealMapper trdProductDealMapper
     */
    @Autowired(required = false)
    public void setTrdProductDealMapper(TrdProductDealMapper trdProductDealMapper) {
        AllTradeCommonUtil.trdProductDealMapper = trdProductDealMapper;
    }

    /**
     * 设置 trdProductDealAddMapper
     *
     * @param trdProductDealAddMapper trdProductDealAddMapper
     */
    @Autowired(required = false)
    public void setTrdProductDealAddMapper(TrdProductDealAddMapper trdProductDealAddMapper) {
        AllTradeCommonUtil.trdProductDealAddMapper = trdProductDealAddMapper;
    }

    /**
     * 设置 paTrdProductDealMapper
     *
     * @param paTrdProductDealMapper paTrdProductDealMapper
     */
    @Autowired(required = false)
    public void setPaTrdProductDealMapper(PaTrdProductDealMapper paTrdProductDealMapper) {
        AllTradeCommonUtil.paTrdProductDealMapper = paTrdProductDealMapper;
    }

    /**
     * 设置 finProductTypeMapper
     *
     * @param finProductTypeMapper finProductTypeMapper
     */
    @Autowired(required = false)
    public void setFinProductTypeMapper(FinProductTypeMapper finProductTypeMapper) {
        AllTradeCommonUtil.finProductTypeMapper = finProductTypeMapper;
    }

    @Autowired(required = false)
    public void setPtlTradeMapper(PtlTradeMapper ptlTradeMapper) {
        AllTradeCommonUtil.ptlTradeMapper = ptlTradeMapper;
    }

    @Autowired(required = false)
    public void setPtlTradeMapper(EtrCommonCashPositionDealService etrCommonCashPositionDealService) {
        AllTradeCommonUtil.etrCommonCashPositionDealService = etrCommonCashPositionDealService;
    }

    /**
     * 设置 restTemplate
     *
     * @param restTemplate restTemplate
     */
    @Autowired(required = false)
    public void setRestTemplate(JoyinRestTemplate restTemplate) {
        AllTradeCommonUtil.restTemplate = restTemplate;
    }

    /**
     * 设置 restTemplate
     *
     * @param baseAssetCashPositionCommMapper baseAssetCashPositionCommMapper
     */
    @Autowired(required = false)
    public void setBaseAssetCashPositionCommMapper(BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper) {
        AllTradeCommonUtil.baseAssetCashPositionCommMapper = baseAssetCashPositionCommMapper;
    }

    @Autowired(required = false)
    public void setTrdTradeFeeDetailMapper(TrdCommonBaseMapper trdCommonBaseMapper) {
        AllTradeCommonUtil.trdCommonBaseMapper = trdCommonBaseMapper;
    }

    @Autowired(required = false)
    public void setWorkflowMapper(WorkflowMapper workflowMapper) {
        AllTradeCommonUtil.workflowMapper = workflowMapper;
    }

    @Autowired(required = false)
    public void setBasePtlSecPositionInfoService(BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl) {
        AllTradeCommonUtil.basePtlSecPositionInfoServiceImpl = basePtlSecPositionInfoServiceImpl;
    }

    @Autowired(required = false)
    public void setBasePtlCashPositionInfoService(BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl) {
        AllTradeCommonUtil.basePtlCashPositionInfoServiceImpl = basePtlCashPositionInfoServiceImpl;
    }

    @Autowired(required = false)
    public void setBaseEtrCashPositionCommService(BaseEtrCashPositionCommService baseEtrCashPositionCommService) {
        AllTradeCommonUtil.baseEtrCashPositionCommService = baseEtrCashPositionCommService;
    }

    @Autowired(required = false)
    public void setPtlPortfolioMapper(PtlPortfolioMapper ptlPortfolioMapper) {
        AllTradeCommonUtil.ptlPortfolioMapper = ptlPortfolioMapper;
    }

    
    @Autowired(required = false)
    public void setBasePtlRecePositionService(BasePtlRecePositionService basePtlRecePositionService) {
        AllTradeCommonUtil.basePtlRecePositionService = basePtlRecePositionService;
    }

    // 调用投管接口 add by renping 20190205 start
    @Autowired(required = false)
    public void setInvestInterfCommService(InvestInterfCommService investInterfCommService) {
        AllTradeCommonUtil.investInterfCommService = investInterfCommService;
    }
    // 调用投管接口 add by renping 20190205 end


    
    @Autowired(required = false)
    public void setTrdTradeFeeDetailMapper(TrdTradeFeeDetailMapper trdTradeFeeDetailMapper) {
        AllTradeCommonUtil.trdTradeFeeDetailMapper = trdTradeFeeDetailMapper;
    }

    /**
     * 通过交易编号检索这些交易不靠谱，数据可能还没有提交，这边检索不到
     * 
     * @param inputDto
     * @param deal
     * @param dealAdd
     * @param paDeal
     * @param stlPayOrders
     * @param actPortfolioId 子组合
     */
    public static void setDeals(AllTradeCommonInputDto inputDto, TrdProductDeal deal, TrdProductDealAdd dealAdd,
                                PaTrdProductDeal paDeal, List<? extends StlPayOrders> stlPayOrders, List<PtlTrade> ptlTrades,
                                String actPortfolioId) {
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = new ArrayList<>();
        BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
        dto.setTrdProductDeal(deal);
        dto.setTrdProductDealAdd(dealAdd);
        dto.setPaTrdProductDeal(paDeal);
        dto.setPtlTrade(ptlTrades);
        dto.setActPortfolioId(actPortfolioId);
        if (stlPayOrders != null && !stlPayOrders.isEmpty()) {
            List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
            stlPayOrders.forEach(order -> {
                StlPayOrdersDto ordersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(ordersDto, order);
                stlPayOrdersList.add(ordersDto);
            });
            dto.setStlPayOrdersList(stlPayOrdersList);
            inputDto.setStlPayOrders(stlPayOrdersList.get(0));
        }
        //通道费不为空 反向交易存入通道费  反向交易存入费用
        if(StringUtil.isNotEmpty(inputDto.getTrdTradeFeeDetail())){
        	dto.setTrdTradeFeeDetail(inputDto.getTrdTradeFeeDetail());
        }
        //通道费不为空 反向交易存入通道费  反向交易存入费用
        baseTradeProductDealDtoList.add(dto);
        inputDto.setBaseTradeProductDealDtoList(baseTradeProductDealDtoList);
        inputDto.setPtlTradeList(ptlTrades);
    }

    // 自动生成反向交易 chenzhili end
    
    /**
     * 通过交易编号检索这些交易不靠谱，数据可能还没有提交，这边检索不到
     * 
     * @param inputDto
     * @param deal
     * @param dealAdd
     * @param paDeal
     * @param stlPayOrders
     * @param actPortfolioId 子组合
     * @param ttfl 交易费用信息
     */
    public static void setDeals(AllTradeCommonInputDto inputDto, TrdProductDeal deal, TrdProductDealAdd dealAdd,
                                PaTrdProductDeal paDeal, List<? extends StlPayOrders> stlPayOrders, List<PtlTrade> ptlTrades,
                                String actPortfolioId,List<TrdTradeFeeDetail> ttfl) {
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = new ArrayList<>();
        BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
        dto.setTrdProductDeal(deal);
        dto.setTrdProductDealAdd(dealAdd);
        dto.setPaTrdProductDeal(paDeal);
        dto.setPtlTrade(ptlTrades);
        dto.setActPortfolioId(actPortfolioId);
        if (stlPayOrders != null && !stlPayOrders.isEmpty()) {
            List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();
            stlPayOrders.forEach(order -> {
                StlPayOrdersDto ordersDto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(ordersDto, order);
                stlPayOrdersList.add(ordersDto);
            });
            dto.setStlPayOrdersList(stlPayOrdersList);
            inputDto.setStlPayOrders(stlPayOrdersList.get(0));
        }
      //通道费不为空 反向交易存入通道费  反向交易存入费用
        if(StringUtil.isNotEmpty(inputDto.getTrdTradeFeeDetail())){
        	dto.setTrdTradeFeeDetail(inputDto.getTrdTradeFeeDetail());
        }
        //通道费不为空 反向交易存入通道费  反向交易存入费用
        //存入交易费用信息
        dto.setTrdTradeFeeDetailList(ttfl);
        baseTradeProductDealDtoList.add(dto);
        inputDto.setBaseTradeProductDealDtoList(baseTradeProductDealDtoList);
        inputDto.setPtlTradeList(ptlTrades);
    }

    private static List<PtlRecePositionFlowDto> getPtlRecePositionFlowDto(TrdProductDeal deal,
                                                                                          TrdProductDealAdd trdProductDealAdd,
                                                                                           String busiType, String 交易对手_子组合,
                                                                                          String 交易对手的母组合) {

        List<PtlRecePositionFlowDto> resultDtos = new ArrayList<>();
        PtlRecePositionFlowDto resultDto = new PtlRecePositionFlowDto();
        // 输入参数.组合代码
        resultDto.setPortfolioId(交易对手的母组合);

        // 输入参数.持仓类型
        resultDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        // 划款指令Entity.币种
        resultDto.setCcy(deal.getCcy());

        resultDto.setCreateUser(deal.getCreateUser());
        resultDto.setCreateDept(deal.getCreateDept());
        resultDto.setUpdateUser(deal.getCreateUser());
        if (DictCodeList.交易方向_应收款项转出.getValue().equals(deal.getPs())
                ) {
            resultDto.setCdate(deal.getSettleDate());
            resultDto.setAmount(ArithmeticUtil.multiply(deal.getPrinAmt(), new BigDecimal(-1)));
        }
        else if (DictCodeList.交易方向_应收款项转入.getValue().equals(deal.getPs())) {
            resultDto.setCdate(deal.getTradeDate());
            resultDto.setAmount(deal.getPrinAmt());
        }

        if (StringUtil.isNotEmpty(交易对手_子组合) && !交易对手_子组合.equals(交易对手的母组合)) {
            PtlRecePositionFlowDto resultDto1 = new PtlRecePositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(resultDto1, resultDto);
            resultDto1.setPortfolioId(交易对手_子组合);
            resultDtos.add(resultDto1);
        }
        resultDtos.add(resultDto);
        return resultDtos;
    }
    // add by zhangrui1 20200511 判断转仓交易 start
    /**
     * 判断是否组合代码/内部交易对手
     * 
     * @param portfolioId 组合代码/交易对手
     * @return true:是组合代码/内部交易对手
     */
    public static boolean isPortfolioId(String portfolioId) {
        if (StringUtil.isEmpty(portfolioId)) {
            return false;
        }
        return trdCommonBaseMapper.isPortfolioId(portfolioId);
    }
    // add by zhangrui1 20200511 判断转仓交易 end
	// add by lijie2 20200910 投资目的取值逻辑 start
	/**
	 * 投资目的取值逻辑
	 * 
	 * @param portfolioId
	 * @return
	 */
	public static String getInvAim(String portfolioId) {
		if (StringUtils.isNotEmpty(portfolioId)) {
			PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(portfolioId);
			if (null != ptlPortfolio && StringUtils.isNotEmpty(ptlPortfolio.getProfitType())) {
				if (DictCodeList.收益类型_货币型.getValue().equals(ptlPortfolio.getProfitType())) {
					return DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue();
				}
			}
		}
		return DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue();
	}
	// add by lijie2 20200910 投资目的取值逻辑 end

    // add by zahngrui1 20200703 批量抽取反向交易编号内部就交易对手 start
    /**
     * 批量抽取反向交易编号
     *
     * @param tradeIds 交易编号/汇总交易编号
     * @return 反向交易编号
     */
    public static Map<String, TrdProductDeal> queryRTradeIds(List<String> tradeIds) {
        if (!StringUtil.isEmptyList(tradeIds)) {
            Map<String, Object> map = new HashMap<>();
            map.put("list", tradeIds);
            List<TrdProductDeal> resList = trdCommonBaseMapper.queryRTradeIds(map);
            if (!StringUtil.isEmptyList(resList)) {
                return resList.stream().collect(
                        Collectors.toMap(TrdProductDeal::getTradeId, info -> info));
            }
        }
        return new HashMap<>();
    }

    // add by zhangrui1 20200619 取得反向交易编号 start
    public static String queryRTradeId(String tradeId) {
        if (!StringUtil.isEmpty(tradeId)) {
            return trdCommonBaseMapper.queryRTradeId(tradeId);
        }
        return tradeId;
    }

    // add by zhangrui1 20200619 取得反向交易编号 end

    /**
     * 批量抽取内部交易对手
     *
     * @param counterIds 交易对手
     * @return 内部交易对手
     */
    public static Map<String, PtlPortfolio> selectPortfolioIds(List<String> counterIds) {
        //防止counterIds中存在null的对象
        List<String> tmpList = new ArrayList<String>();
        if (!StringUtil.isEmptyList(counterIds)) {
            for(String counterId:counterIds) {
                if(StringUtil.isNotEmpty(counterId)) {
                    tmpList.add(counterId);
                }
            }
        }

        if (StringUtil.isEmptyList(tmpList)) {
            return new HashMap<>();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", tmpList);
        List<PtlPortfolio> list = trdCommonBaseMapper.selectPortfolioIds(map);
        if (!StringUtil.isEmptyList(list)) {
            return list.stream().collect(
                    Collectors.toMap(PtlPortfolio::getPortfolioId, info -> info));
        }
        return new HashMap<>();
    }
    // add by zahngrui1 20200703 批量抽取反向交易编号内部就交易对手 end


    /**
     *
     * @param workflowId
     * @param 持仓类型
     * @return
     */
    public static boolean 是否处理持仓(String workflowId, String userName, DictCodeList 持仓类型) {
        if (StringUtil.isEmpty(workflowId)) {
            return true;
        }
        List<WflStatusDto> list = workflowMapper.listByPrimaryKey(workflowId);
        if (list == null || list.isEmpty()) { // 没有流程实例是还没有发起流程
            return true;
        }
        boolean 当前节点是否经办节点 = true;
        try {
            当前节点是否经办节点 = WorkflowContext.getProcessService().isFirstUserTask(list.get(0).getWorkflowId(), userName);
            boolean 是最后一个节点 = WorkflowContext.getProcessService().isLastUserTask(list.get(0).getWorkflowId());
            当前节点是否经办节点 = 当前节点是否经办节点 || (是最后一个节点 && 持仓类型 != DictCodeList.持仓类型_可用);
        } catch (WorkFlowException e) {
            logger.error("获取当前节点错误", e);
        }
        return 当前节点是否经办节点;
    }

    public static boolean 自动生成反向交易(String finprodType2, String ps, String counterId) {
        if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
                || DictCodeListExt1.金融产品类型_投管用_存款.getValue().equals(finprodType2)
                || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2)
                || DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue().equals(finprodType2)
                || DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue().equals(finprodType2)
                || DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)
                || DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue().equals(finprodType2))
                && (DictCodeList.交易方向_买入.getValue().equals(ps)
                || DictCodeList.交易方向_卖出.getValue().equals(ps))) {
            String autoGenerateNegateDealFlag = CacheUtil.getSysParam(BussinessConstant.IS_AUTO_GENERATE_NEGATE_DEAL);
            if (DictCodeList.是否_是.getValue().equals(autoGenerateNegateDealFlag)) {
                PtlPortfolio mcp = ptlPortfolioMapper.selectByPrimaryKey(counterId);
                return mcp != null;
            }
        }
        return false;
    }

    public static AjaxResult checkBeforeTradeComplete(List<CreditLinesInputDto> inputDtoList){
        CreditLinesUpdate creditLinesUpdate = SpringContextUtil.getBean(CreditLinesUpdateImpl.class);
        if (creditLinesUpdate != null) {
            List<CreditLinesOutputDto> resultList = creditLinesUpdate.occupyCreditLinesCheck(inputDtoList);
            if(resultList !=null && resultList.size()>0){
                List<String> stringList = new ArrayList<>();
                AjaxResult ar = new AjaxResult();
                int index =0;
                for (CreditLinesOutputDto item : resultList){
                    index++;
                    if (StringUtil.isNotEmpty(item.getErrMessage())){
                        ValidMessage validMessage = new ValidMessage(item.getErrMessage());
                        List<ValidMessage> validMessageList = new ArrayList<>();
                        validMessageList.add(validMessage);
                        ar.setErrorList(validMessageList);
                        return ar;
                    }else {
                        if(resultList.size()>1){
                            for(String confirmMessages :item.getConfirmMessages()){
                                if(StringUtil.isNotEmpty(inputDtoList.get(index-1).getRownum())){
                                    confirmMessages ="第"+inputDtoList.get(index-1).getRownum()+"行:"+confirmMessages;
                                }
                                if(stringList.contains(confirmMessages)){
                                    continue;
                                }
                                stringList.add(confirmMessages);
                            }
                        }else{

                            stringList.addAll(item.getConfirmMessages());
                        }

                    }
                }
                //系统软控提示信息
                if (stringList.size() > 0){
                    StringBuffer sb = new StringBuffer("");
                    stringList.forEach(item -> {
                        sb.append(item).append(BussinessConstant.MSG_SPLIT);
                    });
                    ar.setData(sb.toString());
                }
                return ar;
            }
        }
        return null;
    }
    public static void handleBeforeTradeComplete(List<CreditLinesInputDto> inputDtoList,String occupyType){
        CreditLinesUpdate creditLinesUpdate = SpringContextUtil.getBean(CreditLinesUpdateImpl.class);
        if (creditLinesUpdate != null) {
            // TODO 后续改成调用微服务
            if (StringUtil.equals(occupyType, DictCodeListRsk.授信占用类别_实占.getValue())){
                creditLinesUpdate.occupyCreditLinesTrd(inputDtoList,null);
            }else {
                creditLinesUpdate.occupyCreditLinesEtr(inputDtoList,null);
            }

        }
    }

    // add by zhangrui1 根据交易代码查询指令代码 start
    public static EtrEntrust findEntrustInfo(String tradeId) {
        return trdCommonBaseMapper.selectEntrustInfo(tradeId);
    }
    // add by zhangrui1 根据交易代码查询指令代码 end
}