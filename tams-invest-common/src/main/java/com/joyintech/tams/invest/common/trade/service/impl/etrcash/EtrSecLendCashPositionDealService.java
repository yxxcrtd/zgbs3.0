package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.common.base.entity.EtrPledgeKey;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dao.EtrCommonCashPositionMapper;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 债券借贷交易
 */
@Service
@Lazy(false)
public class EtrSecLendCashPositionDealService  {

    @Autowired
    private static BaseEtrInvestInfoMapper baseEtrInvestInfoMapper;

    @Autowired
    private static EtrCommonCashPositionMapper etrCommonCashPositionMapper;

    @Autowired
    public void setBaseEtrInvestInfoMapper(BaseEtrInvestInfoMapper baseEtrInvestInfoMapper) {
        EtrSecLendCashPositionDealService.baseEtrInvestInfoMapper = baseEtrInvestInfoMapper;
    }
    @Autowired
    public void setEtrCommonCashPositionMapper(EtrCommonCashPositionMapper etrCommonCashPositionMapper) {
        EtrSecLendCashPositionDealService.etrCommonCashPositionMapper = etrCommonCashPositionMapper;
    }


    public static void deal债券借贷CashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                             EtrPositionHoldDto etrPositionHoldDto) {
        EtrSecPositionDto etrSecPositionDto = new EtrSecPositionDto();
        TrdProductDeal deal = finProductTradeDto.getTrdProductDeal();
        etrSecPositionDto.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
        String secAcctId = finProductTradeDto.getTrdProductDeal().getSecManageAcctId();
        if (StringUtil.isEmpty(secAcctId) && StringUtil.isEmpty(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = "999999";
        } else if (StringUtil.isNotEmpty(finProductTradeDto.getTrdProductDeal().getChlId())) {
            secAcctId = finProductTradeDto.getTrdProductDeal().getChlId();
        }

        etrSecPositionDto.setSecAcctId(secAcctId);
        etrSecPositionDto.setFinprodId(finProductTradeDto.getTrdProductDeal().getFinprodId());
        etrSecPositionDto.setBranch(0l);
        etrSecPositionDto.setCdate(finProductTradeDto.getTrdProductDeal().getTradeDate());
        etrSecPositionDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        // 交易方向
        String ps = deal.getPs();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
                finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
                finProductTradeDto);
        // 交易生成的现金流水
        if (DictCodeList.交易方向_买入.getValue().equals(deal.getPs())
        //modify by jiangpeng on 20210430 ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现
                && !StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {// 首期
            if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())) {
                if (DictCodeList.持仓类型_实际.equals(持仓类型)){
                    EtrSecPositionFlowDto etrSecPositionFlowDto1 = new EtrSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto1, etrSecPositionFlowDto);
                    etrSecPositionFlowDto1.setAmount(deal.getPrinAmt());
                    etrSecPositionFlowDto1.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto1);
                }
                if (DictCodeList.持仓类型_实际.equals(持仓类型) && StringUtil.isNotEmpty(finProductTradeDto.getSecLendFinprodId())) {
                    etrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId());
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(deal.getPrinAmt(), 100, 2));
                    etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
                if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 无委托提交 每只券减少委托质押券面总额/100
                    if (StringUtils.isBlank(entrustId)) {
                        BussRepoSecDto bussRepoSecDto = finProductTradeDto.getBussRepoSec();//质押劵信息
//                        修改质押券为空的情况
                        if (bussRepoSecDto!=null && bussRepoSecDto.getPledgeBondList()!= null && bussRepoSecDto.getPledgeBondList().size()>0 ) {
                            List<PledgeBondInfoDto> pledgeBondListDetailed = bussRepoSecDto.getPledgeBondList();
                            for (PledgeBondInfoDto pledgeBondInfoDto : pledgeBondListDetailed) {
                                // 减少委托本金金额
                                EtrSecPositionFlowDto etrSecPositionFlowDto1 = new EtrSecPositionFlowDto();
                                BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto1, etrSecPositionFlowDto);
                                etrSecPositionFlowDto1.setAmount(ArithmeticUtil.divide(pledgeBondInfoDto.getPledgePrin().negate(), 100, 2));
                                etrSecPositionFlowDto1.setFinprodId(pledgeBondInfoDto.getFinprodId());
                                etrSecPositionFlowDto1.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto1);
                            }
                        }
                    }else{
                        BigDecimal shengYuEntrustAmt = etrCommonCashPositionMapper.selectShengYuEntrustAmt(entrustId);
                        //判断这笔指令剩余待完成额度是否等于此次交易额度，如果等于就冲销指令中质押券头寸，然后按照交易中质押券券面占用头寸
                        if(finProductTradeDto.getTrdProductDeal().getPrinAmt().compareTo(shengYuEntrustAmt)==0){
                            BussRepoSecDto bussRepoSecDto = finProductTradeDto.getBussRepoSec();
                            if (bussRepoSecDto!=null && bussRepoSecDto.getPledgeBondList()!= null && bussRepoSecDto.getPledgeBondList().size()>0 ) {
                                List<PledgeBondInfoDto> pledgeBondListDetailed = bussRepoSecDto.getPledgeBondList();
                                for (PledgeBondInfoDto pledgeBondInfoDto : pledgeBondListDetailed) {
                                    EtrSecPositionFlowDto etrSecPositionFlowDto1 = new EtrSecPositionFlowDto();
                                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto1, etrSecPositionFlowDto);
                                    etrSecPositionFlowDto1.setAmount(etrCommonCashPositionMapper.selectZhiLingChiCang(entrustId,pledgeBondInfoDto.getFinprodId()).negate());
                                    etrSecPositionFlowDto1.setFinprodId(pledgeBondInfoDto.getFinprodId());
                                    etrSecPositionFlowDto1.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto1);

                                    EtrSecPositionFlowDto etrSecPositionFlowDto2 = new EtrSecPositionFlowDto();
                                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto2, etrSecPositionFlowDto);
                                    etrSecPositionFlowDto2.setAmount(etrCommonCashPositionMapper.selectJiaoYiChiCang(entrustId, pledgeBondInfoDto.getFinprodId()).add(
                                            ArithmeticUtil.divide(pledgeBondInfoDto.getPledgePrin(), 100, 2)).negate());
                                    etrSecPositionFlowDto2.setFinprodId(pledgeBondInfoDto.getFinprodId());
                                    etrSecPositionFlowDto2.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto2);
                                }
                            }
                        }
                    }
                }
            } else {
                if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                    EtrSecPositionFlowDto etrSecPositionFlowDto1 = new EtrSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto1, etrSecPositionFlowDto);
                    etrSecPositionFlowDto1.setAmount(deal.getPrinAmt());
                    etrSecPositionFlowDto1.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto1);

                    if (StringUtil.isEmpty(entrustId)) { // 无委托
                        etrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId());
                        etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(deal.getPrinAmt(), 100, 2).negate());
                        etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                        etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    }
                }
            }
        }
        else {// 到期
            // 融入到期，只在提交时，体现持仓和现金.只需处理债券交割，现金交割（暂不实现）
            if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())) {
                if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                    etrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId());
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(deal.getPrinAmt(), 100, 2).negate());
                    etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
                if (DictCodeList.持仓类型_实际.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) { // 无委托提交 每只券减少委托质押券面总额/100
                    if (StringUtils.isBlank(entrustId)) {
                        List<RepoInfo> bussRepoSecDto = EtrCommonCashPositionDealService.findRepoInfo(deal.getFinprodId());//质押劵信息
                        if (CollectionUtils.isNotEmpty(bussRepoSecDto)) {
                            for (RepoInfo pledgeBondInfoDto : bussRepoSecDto) {
                                // 减少委托本金金额
                                EtrSecPositionFlowDto etrSecPositionFlowDto1 = new EtrSecPositionFlowDto();
                                BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto1, etrSecPositionFlowDto);
                                //modify by jiangpeng on 20210416 start
                                // ZGBSS-1149 【债券借贷存续期】：融入到期交易和撤销融入到期交易产生的错误
                                // 债券借贷融入到期 释放质押持仓
                                etrSecPositionFlowDto1.setAmount(pledgeBondInfoDto.getPledgeNumber());
                                //流程审批结束且不为撤销时，指令可用持仓增加
//                                if (DictCodeList.持仓类型_实际.equals(持仓类型) && !etrPositionHoldDto.isNeedNegative()){
//                                    etrSecPositionFlowDto1.setAmount(etrSecPositionFlowDto1.getAmount().negate());
//                                }
                                //add by jiangpeng on 20210416 end
                                etrSecPositionFlowDto1.setFinprodId(pledgeBondInfoDto.getPledgeFinprodId());
                                etrSecPositionFlowDto1.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto1);
                            }
                        }
                    }
                }

                // modify by jiangpeng on 20210416 start
                // ZGBSS-1149 【债券借贷存续期】：融入到期交易和撤销融入到期交易产生的错误
                //撤销时不改变预增金额
                List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        etrCashPositionFlowDto.setOrderId(stlPayOrdersDto.getOrderId());
                        // 可用现金 减少借贷费用的可用现金
                        etrCashPositionFlowDto.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                        etrCashPositionFlowDto.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto.setCdate(stlPayOrdersDto.getSettleDate());
                        etrCashPositionFlowDto.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                        if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }
                        // modify by jiangpeng on 20210429 start
                        // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
                        // 融入没有预增现金
                        /*if ((DictCodeList.持仓类型_实际.equals(持仓类型) || DictCodeList.持仓类型_可用.equals(持仓类型))
                                && !etrPositionHoldDto.isNeedNegative()) {
                            // 处理预增现金
                            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                            //流程审批结束且不为撤销时，预增现金增加提交时减少的值
                            if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                                etrCashPositionFlowDto1.setAmount(etrCashPositionFlowDto1.getAmount().negate());
                            }
                            etrCashPositionFlowDto1.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                            etrCashPositionFlowDto1.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                        }*/
                        // modify by jiangpeng on 20210429 end
                    });
                }
                // modify by jiangpeng on 20210416 end
            }
         // 融出到期，只在生效时，体现持仓和现金.只需处理债券交割，现金交割（暂不实现）
            else if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())){
                if (DictCodeList.持仓类型_实际.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                    etrSecPositionFlowDto.setFinprodId(finProductTradeDto.getSecLendFinprodId());
                    etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(deal.getPrinAmt(), 100, 2));
                    // add by jiangpeng on 20210421 start
                    // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
                    if (StringUtil.equals(DictCodeList.是否_是.getValue(), finProductTradeDto.getIsCashPay())
                            && StringUtil.equals(finProductTradeDto.getSecLendFinprodId(), deal.getFinprodId())) {
                        etrSecPositionFlowDto.setAmount(etrSecPositionFlowDto.getAmount().negate());
                    }
                    // add by jiangpeng on 20210421 end
                    etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
                List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
                if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                    stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
                        etrCashPositionFlowDto.setOrderId(stlPayOrdersDto.getOrderId());
                        // 可用现金 资金到账日增加到账金额
                        etrCashPositionFlowDto.setAmount(stlPayOrdersDto.getSettleAmt());
                        etrCashPositionFlowDto.setAcctId(stlPayOrdersDto.getMyAcctNo());
                        etrCashPositionFlowDto.setCdate(stlPayOrdersDto.getSettleDate());
                        if (DictCodeList.持仓类型_实际.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()){
                            etrCashPositionFlowDto.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto); 
                        }
                        
                        // 处理预增现金，撤销时不处理预增现金
                        // modify by jiangpeng on 20210419
                        //ZGBSS-1150 【债券借贷存续期】：融出到期交易和撤销融入到期交易产生的错误
                        if ((DictCodeList.持仓类型_实际.equals(持仓类型) || DictCodeList.持仓类型_可用.equals(持仓类型)) && !etrPositionHoldDto.isNeedNegative()) {
                            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto () ;
                            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
//                          etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
                            etrCashPositionFlowDto1.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                            etrCashPositionFlowDto1.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                            //流程结束时去除流程提交时增加的预增金额
                            if (DictCodeList.持仓类型_实际.equals(持仓类型)){
                                etrCashPositionFlowDto1.setAmount(etrCashPositionFlowDto1.getAmount().negate());    
                            }
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);  
                        }
                    });
                }
            }
            // add by jiangpeng on 20210421 start
            // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
            else if (StringUtil.equals(deal.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    && StringUtil.equals(DictCodeList.是否_是.getValue(), finProductTradeDto.getIsCashPay())) {
                if (DictCodeList.持仓类型_实际.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                    etrSecPositionFlowDto.setAmount(deal.getShareAmt());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
            }
            // add by jiangpeng on 20210421 end
        }
    }


    public static void deal债券借贷CEntrustStart(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            BussRepoSecDto bussRepoSecDto = entrustCommonDto.getBussRepoSecDto();//质押劵信息
            // 委托生成的持仓流水
            EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                    entrustCommonDto);
            String ps = etrEntrust.getPs();
            if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())) {
                List<PledgeBondInfoDto> pledgeBondListDetailed = bussRepoSecDto.getPledgeBondList();
                if(CollectionUtils.isNotEmpty(pledgeBondListDetailed)){
                    for (PledgeBondInfoDto pledgeBondInfoDto:pledgeBondListDetailed) {
                        EtrSecPositionFlowDto etrSecPositionFlowDto1 = new EtrSecPositionFlowDto();
                        BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto1, etrSecPositionFlowDto);
                        // 减少委托本金金额
                        etrSecPositionFlowDto1.setAmount(ArithmeticUtil.divide(pledgeBondInfoDto.getPledgePrin().negate(),100,2));
                        etrSecPositionFlowDto1.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue()+"_"+pledgeBondInfoDto.getBondCode());
                        etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto1);
                    }
                }
            }
            else {
                // 减少委托本金金额
                etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(etrEntrust.getEntrustAmt().negate(),100,2));
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
    }

    public static void deal债券借贷CEntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        EtrPledgeKey etrPledgeKey = new EtrPledgeKey();
        EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
        etrPledgeKey.setEntrustId(etrEntrust.getEntrustId());
        List<EtrPledge> etrPledges = baseEtrInvestInfoMapper.selectEtrPledgeByPrimaryKey(etrPledgeKey);
        if(etrPledges==null || etrPledges.size()==0){
            etrPledges = baseEtrInvestInfoMapper.selectWflEtrPledgeByPrimaryKey(etrPledgeKey);
        }
        // 交易方向
        String ps = etrEntrust.getPs();
        // 委托生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(entrustCommonDto);
        if (StringUtil.equals(etrEntrust.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())) {
            if(CollectionUtils.isNotEmpty(etrPledges)){
                for (EtrPledge pledgeBondInfoDto:etrPledges) {
                    etrSecPositionFlowDto.setFinprodId(pledgeBondInfoDto.getFinprodId());
                    //获取质押流水
                    BigDecimal amount = etrCommonCashPositionMapper.selectCommandPositionFlow(etrSecPositionFlowDto);
                    if (StringUtil.isNotEmpty(amount)) {
                        etrSecPositionFlowDto.setAmount(amount.negate());
                    }
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);

                    //上面是释放指令全部占用的头寸，下面是加上 占用交易部分的头寸
                    EtrSecPositionFlowDto etrSecPositionFlowDto2 = new EtrSecPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto2, etrSecPositionFlowDto);
                    etrSecPositionFlowDto2.setAmount(etrCommonCashPositionMapper.selectJiaoYiChiCang(etrEntrust.getEntrustId(), pledgeBondInfoDto.getFinprodId()).negate());
                    etrSecPositionFlowDto2.setFinprodId(pledgeBondInfoDto.getFinprodId());
                    etrSecPositionFlowDto2.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto2);
                }
            }
        }
        else {
            BigDecimal etrFinish = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());
            // 关闭日每只券增加未完成券面总额/100
            etrSecPositionFlowDto.setAmount(ArithmeticUtil.divide(ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), etrFinish),100,2));
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
        }
    }
}
