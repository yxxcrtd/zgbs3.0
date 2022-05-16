package com.joyintech.service.trade.entrustmsgbymq.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.entrustmsgbymq.dao.EntrustMsgbyMQMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustDetailMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TradePreCommonService {

    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EntrustMsgbyMQMapper entrustMsgbyMQMapper;

    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    /**
     * 处理头寸
     * @param finProductTradeDto 交易信息
     * @param acctNo 资金账号
     */
    public void executePosition(FinProductTradeDto finProductTradeDto, String acctNo){
        // 生成的临时交易表数据已经是组合维度的数据,无需拆分 modify by fuyongsheng 20220511 start
        if(!CollectionUtils.isEmpty(finProductTradeDto.getBaseTradeProductDealDtos())&& StringUtil.isNotEmpty(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getEntrustId())){
//            EtrEntrust etrEntrust=etrEntrustMapper.selectByPrimaryKey(finProductTradeDto.getBaseTradeProductDealDtos().get(0).getEntrustId());
//            //系列户,循环掉之前的方法
//            if(StringUtil.equals(DictCodeListExt3.投资指令类型_多组合单资产指令.getValue(),etrEntrust.getEntrustClass())) {
//                //系列户按照组合维度构建多个finProductTradeDto,处理头寸开始
//                List<FinProductTradeDto> finProductTradeDtoList=new ArrayList<>();
//                List<BaseTradeProductDealDto> baseTradeProductDealDtoList=finProductTradeDto.getBaseTradeProductDealDtos();
//                baseTradeProductDealDtoList.forEach(item -> {
//                    String entrustId = item.getEntrustId();
//                    if(StringUtil.isNotEmpty(entrustId)){
//                        List<EtrEntrustDetail> etrEntrustDetailList=etrEntrustDetailMapper.selectList(entrustId);
//                        BigDecimal totalEntrustAmt = etrEntrustDetailList.stream().map(EtrEntrustDetail::getEntrustAmt).reduce(BigDecimal::add).get();
//                        BigDecimal tradeAmtSingleSum=BigDecimal.ZERO;
//                        BigDecimal shareAmtSingleSum=BigDecimal.ZERO;
//                        BigDecimal settleAmtSingleSum=BigDecimal.ZERO;
//
//                        List<BaseTradeProductDealDto> baseTradeProductDealDtoListMuilt=new ArrayList<>();
//
//                        for(int i=0;i<etrEntrustDetailList.size();i++){
//                            EtrEntrustDetail etrEntrustDetail=etrEntrustDetailList.get(i);
//                            TrdProductDeal trdProductDealMuilt= item.getTrdProductDeal();
//                            TrdProductDealAdd trdProductDealAddMuilt=item.getTrdProductDealAdd();
//                            BigDecimal rate=etrEntrustDetail.getEntrustAmt().divide(totalEntrustAmt,4,BigDecimal.ROUND_HALF_UP);
//                            BigDecimal tradeAmtSingle=BigDecimal.ZERO; //金额
//                            BigDecimal  shareAmtSingle=BigDecimal.ZERO; //份额
//                            //最后一个组合分配的金额=总金额-前几个组合分配的金额之后
//                            if(i==etrEntrustDetailList.size()-1){
//                                tradeAmtSingle= ArithmeticUtil.subtract(trdProductDealMuilt.getTradeAmt(),tradeAmtSingleSum);
//                                shareAmtSingle=ArithmeticUtil.subtract(trdProductDealMuilt.getShareAmt(),shareAmtSingleSum);
//                            }else{
//                                //交易金额
//                                tradeAmtSingle=ArithmeticUtil.round(ArithmeticUtil.multiply(trdProductDealMuilt.getTradeAmt(),rate),2, RoundingMode.HALF_UP);
//                                tradeAmtSingleSum=tradeAmtSingleSum.add(tradeAmtSingleSum);
//                                //交易份额
//                                shareAmtSingle=ArithmeticUtil.round(ArithmeticUtil.multiply(trdProductDealMuilt.getShareAmt(),rate),2, RoundingMode.HALF_UP);
//                                shareAmtSingleSum=shareAmtSingleSum.add(tradeAmtSingleSum);
//
//                            }
//                            trdProductDealMuilt.setTradeAmt(tradeAmtSingle);
//                            trdProductDealMuilt.setShareAmt(shareAmtSingle);
//                            // ZGTFCP-1499:对系列户交易赋值对应的组合 add by fuyongsheng 20220322 start
//                            trdProductDealMuilt.setPortfolioId(etrEntrustDetail.getPortfolioId());
//                            // ZGTFCP-1499:对系列户交易赋值对应的组合 add by fuyongsheng 20220322 end
//                            // ZGCPCN-198:对系列户交易赋值对应的证券管理账户 modify by fuyongsheng 20220425 start
//                            trdProductDealMuilt.setSecManageAcctId(this.getTradePreSecAcctId(acctNo,
//                                    trdProductDealMuilt.getPortfolioId(), trdProductDealMuilt.getTradeMarket()));
//                            // ZGCPCN-198:对系列户交易赋值对应的证券管理账户 modify by fuyongsheng 20220425 end
//                            trdProductDealAddMuilt.setPortfolioId(etrEntrustDetail.getPortfolioId());
//                            // List<BaseTradeProductDealDto> baseTradeProductDealDtoListMuilt=new ArrayList<>();
//                            BaseTradeProductDealDto baseTradeProductDealDtoMuilt=new BaseTradeProductDealDto();
//                            BeanUtil.copyPropertiesIgnoreNull(baseTradeProductDealDtoMuilt,item);
//                            baseTradeProductDealDtoMuilt.setTrdProductDeal(trdProductDealMuilt);
//                            baseTradeProductDealDtoMuilt.setTrdProductDealAdd(trdProductDealAddMuilt);
//                            //划款指令按比例分
//                            if (!StringUtil.isEmptyList(item.getStlPayOrdersList())) {
//                                StlPayOrdersDto stlPayOrdersDtoMuilt = new StlPayOrdersDto();
//                                BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersDtoMuilt, item.getStlPayOrdersList().get(0));
//                                //最后一个组合分配的金额=总金额-前几个组合分配的金额之后
//                                BigDecimal settleAmtSingle=BigDecimal.ZERO;
//                                if(i==etrEntrustDetailList.size()-1){
//                                    settleAmtSingle=ArithmeticUtil.subtract(stlPayOrdersDtoMuilt.getSettleAmt(),settleAmtSingleSum);
//                                }else{
//                                    //结算金额
//                                    settleAmtSingle=ArithmeticUtil.round(ArithmeticUtil.multiply(trdProductDealMuilt.getTradeAmt(),rate),2, RoundingMode.HALF_UP);
//                                    settleAmtSingleSum=tradeAmtSingleSum.add(tradeAmtSingleSum);
//                                }
//                                stlPayOrdersDtoMuilt.setSettleAmt(settleAmtSingle);
//                                stlPayOrdersDtoMuilt.setPortfolioId(etrEntrustDetail.getPortfolioId());
//                                baseTradeProductDealDtoMuilt.setStlPayOrdersList(Arrays.asList(stlPayOrdersDtoMuilt));
//                            }
//                            baseTradeProductDealDtoListMuilt.add(baseTradeProductDealDtoMuilt);
//                            // ZGCPCN-209:修改系列户多组合指令拆分交易对象组装 modify by fuyongsheng 20220429 start
////                            FinProductTradeDto finProductTradeDtoMuilt=new FinProductTradeDto();
////                            BeanUtil.copyPropertiesIgnoreNull(finProductTradeDtoMuilt,finProductTradeDto);
////                            finProductTradeDtoMuilt.setBaseTradeProductDealDtos(baseTradeProductDealDtoListMuilt);
////                            finProductTradeDtoList.add(finProductTradeDtoMuilt);
//                        }
//                        FinProductTradeDto finProductTradeDtoMuilt=new FinProductTradeDto();
//                        BeanUtil.copyPropertiesIgnoreNull(finProductTradeDtoMuilt,finProductTradeDto);
//                        finProductTradeDtoMuilt.setBaseTradeProductDealDtos(baseTradeProductDealDtoListMuilt);
//                        finProductTradeDtoList.add(finProductTradeDtoMuilt);
//                        // ZGCPCN-209:修改系列户多组合指令拆分交易对象组装 modify by fuyongsheng 20220429 end
//                    }
//                });
//                for(int i=0;i<finProductTradeDtoList.size();i++){
//                    baseTradeProductDealCommService.execute(finProductTradeDtoList.get(i));
//                }
//            }else{
                baseTradeProductDealCommService.execute(finProductTradeDto);
//            }
            // 生成的临时交易表数据已经是组合维度的数据,无需拆分 modify by fuyongsheng 20220511 end
        }else{
            baseTradeProductDealCommService.execute(finProductTradeDto);
        }
    }

    /**
     * 查找证券管理户代码
     * @param acctNo
     * @param portfolioId
     * @param tradeMarket
     * @return
     */
    public String getTradePreSecAcctId(String acctNo, String portfolioId, String tradeMarket){
        Map<String,Object> secMap = new HashMap<>();
        secMap.put("acctNo", acctNo);
        secMap.put("portfolioId", portfolioId);
        secMap.put("tradeMarket", tradeMarket);
        return entrustMsgbyMQMapper.queryByAcctNo(secMap);
    }

    /**
     * 是否是多组合系列户指令
     * @param entrustId 指令编号
     * @return
     */
    public boolean isMultiPortFolioEntrust(String entrustId){
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        return etrEntrust != null && DictCodeListExt3.投资指令类型_多组合单资产指令.getValue().equals(etrEntrust.getEntrustClass());
    }
}
