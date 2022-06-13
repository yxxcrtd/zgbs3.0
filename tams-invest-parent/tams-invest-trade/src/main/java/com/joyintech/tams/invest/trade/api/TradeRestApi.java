package com.joyintech.tams.invest.trade.api;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.position.service.CommonPositionEngineService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonRevokeDto;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.exchangetransmatch.position.constant.PositionConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 何阳阳
 * @date 2020/8/5
 * @see TradeRestApi
 * @since 1.0
 */
@RestController
@RequestMapping("/invest/trade/api")
public class TradeRestApi {

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private BaseTradeProductDealService baseTradeProductDealService;
    /**
     *
     */
    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;
    /**
     * jixu接口
     */
    @Autowired
    private ValuationInterfService valuationInterfService;

	@Autowired
    private PositionRequestTradeRestService positionRequestTradeRestService;    

	private static final List<String> 正回购_逆回购 = Arrays.asList(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),
            DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());

    @PostMapping("execute")
    @Transactional
    @DTXTransaction
    public CommonResult execute(@RequestBody PositionUpdateParam param) {
        CommonResult result = check(param);
        if(CommonResult.SUCCED == result.getReturnCode()) {
            List<String> tradeIdList = param.getTradeIdList();
            List<String> totalTradeIdList = param.getTotalTradeIdList();
            String workflowStatus = param.getStatus();
            // String busiType = param.getBusiType();
            String assetType2 = param.getAssetType2();
            String dealOldPositionFlag = param.getDealOldPositionFlag();
            if (StringUtil.isNotEmpty(workflowStatus)
                    && (CollectionUtils.isNotEmpty(tradeIdList) || CollectionUtils.isNotEmpty(totalTradeIdList))) {
                // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(workflowStatus);
                finProductTradeDto.setNeedSum(true);
                // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 start
                finProductTradeDto.setNotWorkflow("true".equals(param.getIsNotWorkflow()));
                // GNS-949:增加是否工作流数据标识 modify by fuyongsheng 20220227 end
                if (StringUtil.isNotEmpty(assetType2)) {
                	finProductTradeDto.setAssetType2(assetType2);
                }
                
                
                List<BaseTradeProductDealDto> baseTradeProductDealDtos = null;
                if (CollectionUtils.isNotEmpty(totalTradeIdList)) {
                    baseTradeProductDealDtos = baseTradeProductDealService.selectTotalDeal(totalTradeIdList);
                }
                if (CollectionUtils.isNotEmpty(tradeIdList) && CollectionUtils.isEmpty(baseTradeProductDealDtos)) {
                    baseTradeProductDealDtos = baseTradeProductDealService.select(tradeIdList);
                    // add by etliu 20220219 start
                    if (!CollectionUtils.isEmpty(baseTradeProductDealDtos)) {
                        for (BaseTradeProductDealDto tempDto : baseTradeProductDealDtos) {
                            if (tempDto.getTrdProductDeal() != null && StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), tempDto.getTrdProductDeal().getPs())) {
                                if(正回购_逆回购.contains(tempDto.getTrdProductDeal().getFinprodType2())){
                                    tempDto.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
                                }else{
                                    tempDto.getTrdProductDeal().setPs(DictCodeList.交易方向_收息.getValue());
                                }
                            }
                            else if (tempDto.getTrdProductDeal() != null && StringUtil.equals(DictCodeList.交易方向_还本到账.getValue(), tempDto.getTrdProductDeal().getPs())) {
                                if(正回购_逆回购.contains(tempDto.getTrdProductDeal().getFinprodType2())){
                                    tempDto.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
                                }else{
                                    tempDto.getTrdProductDeal().setPs(DictCodeList.交易方向_还本.getValue());
                                }
                            }
                            else if (tempDto.getTrdProductDeal() != null && StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), tempDto.getTrdProductDeal().getPs())) {
                                tempDto.getTrdProductDeal().setPs(DictCodeList.交易方向_赎回.getValue());
                            }
                            if (tempDto.getTrdFinProductDealTotal() != null
                                    && StringUtil.equals(DictCodeList.交易方向_到期.getValue(), tempDto.getTrdFinProductDealTotal().getPs())) {
                                tempDto.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
                            }
                        }
                    }
                    // add by etliu 20220219 end
                }
                
                finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
                
                if (finProductTradeDto.getBaseTradeProductDealDtos() != null && finProductTradeDto.getBaseTradeProductDealDtos().size() > 0) {
                	for (int index = 0; index < finProductTradeDto.getBaseTradeProductDealDtos().size(); index++) {
                        TrdProductDeal trdProductDeal = finProductTradeDto.getBaseTradeProductDealDtos().get(index).getTrdProductDeal();
                        if(null == trdProductDeal){
                            continue;
                        }
                        // 可转债申购确认需要设置申购类型为普通申购
                        if (StringUtil.equals(trdProductDeal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                			&& StringUtil.equals(trdProductDeal.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                			finProductTradeDto.getBaseTradeProductDealDtos().get(index).setPurchType(DictCodeListExt2.申购类型_普通申购.getValue());
                			// 根据交易表的b_trade_id检索TRD_PRODUCT_DEAL拿到申购的那条交易
                			List<String> bTradeIdList = new ArrayList<String>();
                			bTradeIdList.add(trdProductDeal.getbTradeId());
                			List<BaseTradeProductDealDto> bTradeIdTradeProductDealDtos = baseTradeProductDealService.select(bTradeIdList);
                			if (bTradeIdTradeProductDealDtos != null && bTradeIdTradeProductDealDtos.size() > 0) {
                				finProductTradeDto.getBaseTradeProductDealDtos().get(index).setSecAmount(ArithmeticUtil.multiply(
                						ArithmeticUtil.doSub(bTradeIdTradeProductDealDtos.get(0).getTrdProductDeal().getShareAmt(), finProductTradeDto.getBaseTradeProductDealDtos().get(index).getTrdProductDeal().getShareAmt()), new BigDecimal(100)));
                			}
                		}
                    }
                }

                // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库的头寸是在撤销批处理任务里处理的,不用走老头存逻辑 modify by fuyongsheng 20220327 start
                if(StringUtil.isEmpty(dealOldPositionFlag) || dealOldPositionFlag.equals(DictCodeList.是否_是.getValue())){
                    result = baseTradeProductDealCommService.execute(finProductTradeDto);
                }
                // ZGTFCP-1955,ZGTFCP-1510:上市流通、质押券出入库的头寸是在撤销批处理任务里处理的,不用走老头存逻辑 modify by fuyongsheng 20220327 end

                // 发送新头寸接口数据
                positionRequestTradeRestService.sendTradeRestPositionRequest(finProductTradeDto);

                // 结算数据下行都是单产品单组合，不会有母组合的，该逻辑去掉                
//                for (BaseTradeProductDealDto baseTradeProductDealDto : baseTradeProductDealDtos) {
//                    String portfolioId = baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId();
//                    String actPortfolioId = AllTradeCommonUtil.exchangePortfolio(portfolioId);
//
//                    if (StringUtil.isNotEmpty(actPortfolioId) && !StringUtil.equals(actPortfolioId, portfolioId)) {
//                        finProductTradeDto.getBaseTradeProductDealDtos().forEach(
//                                baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(
//                                        actPortfolioId));
//                        baseTradeProductDealCommService.insertPosition(finProductTradeDto);
//                    }
                    
                    // 下面的这段逻辑，投管交易汇总不应该调用，注释掉。未来估值交易汇总是要用的。
                    // 股票交易，需要调用几许接口
                    /*if(StringUtil.equals(DictCodeListExt2.证券品种_股票.getValue(), busiType)) {
                        List<StlPayOrdersDto> ordersDtos = baseTradeProductDealDto.getStlPayOrdersList();
                        List<StlPayOrders> stlPayOrdersList = new ArrayList<>(ordersDtos.size());
                        if (CollectionUtils.isNotEmpty(ordersDtos)) {
                            for (StlPayOrdersDto ordersDto : ordersDtos) {
                                StlPayOrdersDto stlPayOrders = new StlPayOrdersDto();
                                BeanUtil.copyPropertiesIgnoreNull(stlPayOrders, ordersDto);
                                stlPayOrdersList.add(stlPayOrders);
                            }
                        }
                        TrdCommDto trdCommDto = new TrdCommDto();
                        trdCommDto.setStlPayOrdersList(stlPayOrdersList);
                        trdCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
                        trdCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
                        trdCommDto.setTradeFeeDetaillist(baseTradeProductDealDto.getTrdTradeFeeDetailList());
                        trdCommDto.setPtlTradeList(baseTradeProductDealDto.getPtlTrade());
                        if(StringUtil.equalsIgnoreCase(workflowStatus, DictCodeListExt1.交易流程状态_有效.getValue())) {
                            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
                        }
                        else if(StringUtil.equalsIgnoreCase(workflowStatus, DictCodeListExt1.交易流程状态_撤销.getValue())) {
                            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);
                        }
                        valuationInterfService.runValInvest2Jixu(trdCommDto);
                    }*/
//                }
            }
        }
        return result;
    }

    /**
     * 根据交易编号，删除可用，质押，融入，融出，在途FLOW持仓
     * @param param 参数
     * @return
     */
    @PostMapping("back")
    @Transactional
    @DTXTransaction
    public CommonResult back(@RequestBody PositionUpdateParam param) {
        List<String> totalTradeIdList = param.getTotalTradeIdList();
        if(CollectionUtils.isNotEmpty(totalTradeIdList)) {
            totalTradeIdList.forEach(totalTradeId -> {
                AllTradeCommonUtil.deletePosition(param.getRevokeReason(), totalTradeId);
            });
        }
        return new CommonResult();
    }

    @PostMapping("revoke")
    @Transactional
    @DTXTransaction
    public CommonResult revoke(@RequestBody PositionUpdateParam param) {
        CommonResult result = new CommonResult(CommonResult.SUCCED, "OK");
        List<String> totalTradeIdList = param.getTotalTradeIdList();
        List<String> tradeIdList = param.getTradeIdList();
        if(CollectionUtils.isNotEmpty(totalTradeIdList)) {
            for (String bizCode : totalTradeIdList) {
                investPeriodCommonService.revoke汇总和交易相关表(bizCode,
                        param.getUpdateUser(), param.getRevokeReason(), null, null);
            }
        }
        else if(CollectionUtils.isNotEmpty(tradeIdList)) {
            for (String bizCode : tradeIdList) {
                investPeriodCommonService.revoke汇总和交易相关表(bizCode, param.getUpdateUser(), false, param.getRevokeReason(), null,
                        null,true,null);
            }
        }
        else {
            result.setReturnCode(CommonResult.FAILD);
            result.setReturnMessage("缺少参数");
        }
        return result;
    }

    private CommonResult check(PositionUpdateParam param) {
        if(StringUtil.isEmpty(param.getBusiType()) || StringUtil.isEmpty(param.getStatus())) {
            return new CommonResult(CommonResult.FAILD, "缺少必须参数");
        }
        return new CommonResult(CommonResult.SUCCED, "OK");
    }
}
