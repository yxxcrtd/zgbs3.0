package com.joyintech.tams.invest.trade.exchangetransmatch.common.service.bond;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.common.TrdProductDealDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDetailDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonEntrustDto;
import com.joyintech.tams.invest.trade.exchangetransmatch.common.dto.ExchangeTransMatchBaseTradeDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 债转股
 */
@Service
public class ExchangeTransMatchBond4Service extends ExchangeTransMatchBondCommonService {

    @Autowired
    private FinProductMapper finProductMapper;
    /**
     * 债券交易处理
     * @param
     * @return
     */
    @Override
    protected void setDealTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        // 业务类型
        trdFinProductDealTotal.setBusiType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        trdFinProductDealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_债转股.getValue());
//        // 债转股的零股折算现金赋值 add by fuyongsheng 20220116 start
//        trdFinProductDealTotal.setTradeAmt(dealDto.getFpriceAmt());
//        // 债转股的零股折算现金赋值 add by fuyongsheng 20220116 end
    }

    @Override
    public List<StlPayOrdersDto> generateStlPayOrdersList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<StlPayOrdersDto> stlPayOrdersList = new ArrayList<>();

        StlPayOrdersDto stlPayOrdersDto = createStlPayOrders(dealDto, entrustDto, trdProductDeal);
        // 收付方向
        stlPayOrdersDto.setPs(DictCodeList.收付方向_收入.getValue());;
        stlPayOrdersList.add(stlPayOrdersDto);

        return stlPayOrdersList;
    }

    @Override
    protected TrdProductDeal createDeal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        // 交易字段设置共通
        TrdProductDeal trdProductDeal = createProductDeal(dealDto, entrustDto,trdFinProductDealTotal);
        trdProductDeal.setSettleSpeed(dealDto.getSettleSpeed());
        // 单位净价
        trdProductDeal.setUnitCprice(dealDto.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(dealDto.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(dealDto.getUnitFprice());
        // 单位面值
        trdProductDeal.setParValue(null);
        // 净价总额，交易份额，交易本金，利息总额，全价总额，随交易支付费用，交易金额，未随交易支付费用，总交易费用
        calcFeeAmtPay(dealDto, entrustDto,trdProductDeal);
        // 交易方向
        trdProductDeal.setPs(entrustDto.getPs());
        // 到期收益率和行权收益率
        this.calc(trdProductDeal);

        trdProductDeal.setTgBusiType( DictCodeListExt3.平安业务类型_债转股.getValue());

        // 到期收益率
        trdFinProductDealTotal.setYield(trdProductDeal.getYield());
        // 行权收益率
        trdFinProductDealTotal.setExerYield(trdProductDeal.getExerYield());

        return trdProductDeal;
    }

    @Override
    public TrdDealTotalAdd generateDealTotalAdd(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal) {
        TrdDealTotalAdd trdDealTotalAdd = new TrdDealTotalAdd();
        trdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdDealTotalAdd.setEntrustId(entrustDto.getEntrustId());

        // 零星股数
//        trdDealTotalAdd.setSpoShareNum(x.getSpoShareNum());

        return trdDealTotalAdd;
    }

    @Override
    public List<TrdFinProductBarterTotal> generateDealBarterTotal(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdFinProductDealTotal trdFinProductDealTotal){
        List<TrdFinProductBarterTotal> trdFinProductBarterTotalList = new ArrayList<>();
        TrdFinProductBarterTotal trdFinProductBarterTotal = new TrdFinProductBarterTotal();
        SettleTransDataTotalAdd settleTransDataTotalAdd = dealDto.getSettleTransDataTotalAdd();

        trdFinProductBarterTotal.setTradeId(trdFinProductDealTotal.getTotleTradeId());
        trdFinProductBarterTotal.setInvAim(entrustDto.getInvAim());
        trdFinProductBarterTotal.setBranch(0L);
        // 金融产品代码
        trdFinProductBarterTotal.setFinprodId(settleTransDataTotalAdd.getrFinprodId());
        trdFinProductBarterTotal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        // 转股价格
        trdFinProductBarterTotal.setUnitCprice(settleTransDataTotalAdd.getConPrice());
        // 转股数
        trdFinProductBarterTotal.setShareAmt(settleTransDataTotalAdd.getConAmt());
        trdFinProductBarterTotalList.add(trdFinProductBarterTotal);
        return trdFinProductBarterTotalList;
    }


    @Override
    protected List<TrdFinProductBarter> generateDealBarterList(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto, TrdProductDeal trdProductDeal) {
        List<TrdFinProductBarter> trdFinProductBarterList = new ArrayList<>();

        TrdFinProductBarter trdFinProductBarter = new TrdFinProductBarter();
        SettleTransDataTotalAdd settleTransDataTotalAdd = dealDto.getSettleTransDataTotalAdd();
        trdFinProductBarter.setTradeId(trdProductDeal.getTradeId());
        trdFinProductBarter.setInvAim(entrustDto.getInvAim());
        trdFinProductBarter.setBranch(0L);
        // 金融产品代码
        trdFinProductBarter.setFinprodId(settleTransDataTotalAdd.getrFinprodId());
        // 金融产品类型
        trdFinProductBarter.setFinprodType(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        // 转股价格
        trdFinProductBarter.setUnitCprice(settleTransDataTotalAdd.getConPrice());
        // 转股数
        // ZGCPCN-1730:债转股支持系列户指令分配 add by fuyongsheng 20220627 start
        // trdFinProductBarter.setShareAmt(settleTransDataTotalAdd.getConAmt());
        if(StringUtil.equals(entrustDto.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
            trdFinProductBarter.setShareAmt(entrustDto.getConAmt());
        }else{
            trdFinProductBarter.setShareAmt(settleTransDataTotalAdd.getConAmt());
        }
        // ZGCPCN-1730:债转股支持系列户指令分配 add by fuyongsheng 20220627 end
        trdFinProductBarterList.add(trdFinProductBarter);

        return trdFinProductBarterList;
    }

    // ZGCPCN-1730:债转股支持系列户指令分配 add by fuyongsheng 20220627 start
    /**
     * 交易信息实体
     * @param dealDto
     * @param entrustDto
     * @return
     */
    @Override
    public List<ExchangeTransMatchBaseTradeDto> genFinProductTradeDto(ExchangeTransMatchCommonDto dealDto, ExchangeTransMatchCommonEntrustDto entrustDto){
        List<ExchangeTransMatchBaseTradeDto> baseTradeProductDealDtoList = new ArrayList<>();
        //生成金融产品汇总交易信息
        TrdFinProductDealTotal trdFinProductDealTotal = this.genTrdFinProductDealTotal(dealDto,entrustDto);

        if(StringUtil.equals(entrustDto.getEntrustClass(),DictCodeListExt3.投资指令类型_多组合单资产指令.getValue())) {
            if(CollectionUtils.isEmpty(entrustDto.getEntrustDetailDtoList())){
                return baseTradeProductDealDtoList;
            }
            // 债转股需要按照债分配数量倒序
            List<ExchangeTransMatchCommonEntrustDetailDto> sortedEntrustDetailDtoList = entrustDto.getEntrustDetailDtoList()
                    .stream()
                    .filter(item -> item.getMarchQty() != null && item.getMarchQty().compareTo(BigDecimal.ZERO) > 0)
                    .sorted(Comparator.comparing(ExchangeTransMatchCommonEntrustDetailDto::getMarchQty).reversed())
                    .collect(Collectors.toList());
            // 处理转股数量分配
            this.handleCovertSharesQtyAllocation(sortedEntrustDetailDtoList, dealDto.getSettleTransDataTotalAdd());
            for (int i=0; i<sortedEntrustDetailDtoList.size(); i++) {
                ExchangeTransMatchCommonEntrustDetailDto detailDto = sortedEntrustDetailDtoList.get(0);
                ExchangeTransMatchCommonEntrustDto entrustDtoTmp = new ExchangeTransMatchCommonEntrustDto();
                BeanUtil.copyPropertiesIgnoreNull(entrustDtoTmp,entrustDto);
                entrustDtoTmp.setPortfolioId(detailDto.getPortfolioId());
                entrustDtoTmp.setMarchQty(detailDto.getMarchQty());
                // 零股现金值赋值给分配数量最大的那条数据
                if(i == 0){
                    entrustDtoTmp.setMarchAmt(detailDto.getMarchAmt());
                }else{
                    entrustDtoTmp.setMarchAmt(BigDecimal.ZERO);
                }
                entrustDtoTmp.setConAmt(detailDto.getConAmt());
                entrustDtoTmp.setMarchFeeAmtUnPay(detailDto.getMarchFeeAmtUnPay());
                entrustDtoTmp.setMarchFeeAmtPay(detailDto.getMarchFeeAmtPay());

                ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();
                //生成金融产品汇总交易信息
                baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
                //生成金融产品汇总交易附属信息
                baseTradeProductDealDto.setTrdDealTotalAdd(this.generateDealTotalAdd(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdFinProductDealTotal()));
                //生成金融产品费用汇总表数据
                baseTradeProductDealDto.setTrdTradeFeeTotalList(this.genTrdTradeFeeTotalList(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdFinProductDealTotal()));
                //生成金融产品交易信息
                baseTradeProductDealDto.setTrdProductDeal(this.genTrdProductDeal(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdFinProductDealTotal()));
                //生成金融产品交易附属表信息
                baseTradeProductDealDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成组合交易表数据
                baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成费用信息
                baseTradeProductDealDto.setTrdTradeFeeDetail(this.genTrdTradeFeeDetail(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成费用信息list
                baseTradeProductDealDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //生成划款指令
                baseTradeProductDealDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));
                //金融产品实物交易表
                baseTradeProductDealDto.setTrdFinProductBarterList(this.generateDealBarterList(dealDto,entrustDtoTmp,baseTradeProductDealDto.getTrdProductDeal()));

                baseTradeProductDealDtoList.add(baseTradeProductDealDto);
            }

        }
        else {
            ExchangeTransMatchBaseTradeDto baseTradeProductDealDto = new ExchangeTransMatchBaseTradeDto();
            //生成金融产品汇总交易信息
            baseTradeProductDealDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
            //生成金融产品汇总交易附属信息
            baseTradeProductDealDto.setTrdDealTotalAdd(this.generateDealTotalAdd(dealDto,entrustDto,baseTradeProductDealDto.getTrdFinProductDealTotal()));
            //生成金融产品费用汇总表数据
            baseTradeProductDealDto.setTrdTradeFeeTotalList(this.genTrdTradeFeeTotalList(dealDto,entrustDto,baseTradeProductDealDto.getTrdFinProductDealTotal()));
            //生成金融产品交易信息
            baseTradeProductDealDto.setTrdProductDeal(this.genTrdProductDeal(dealDto,entrustDto,baseTradeProductDealDto.getTrdFinProductDealTotal()));
            //生成金融产品交易附属表信息
            baseTradeProductDealDto.setTrdProductDealAdd(this.genTrdProductDealAdd(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成组合交易表数据
            baseTradeProductDealDto.setPtlTrade(this.generatePtlTrade(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成费用信息
            baseTradeProductDealDto.setTrdTradeFeeDetail(this.genTrdTradeFeeDetail(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成费用信息list
            baseTradeProductDealDto.setTrdTradeFeeDetailList(this.genTrdTradeFeeDetailList(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //生成划款指令
            baseTradeProductDealDto.setStlPayOrdersList(this.generateStlPayOrdersList(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            //金融产品实物交易表
            baseTradeProductDealDto.setTrdFinProductBarterList(this.generateDealBarterList(dealDto,entrustDto,baseTradeProductDealDto.getTrdProductDeal()));
            baseTradeProductDealDtoList.add(baseTradeProductDealDto);
        }


        return baseTradeProductDealDtoList;
    }

    /**
     * 处理转股数量分配(根据债分配数量比例计算转股数量)
     * @param entrustDetailDtoList
     * @param settleTransDataTotalAdd
     */
    private void handleCovertSharesQtyAllocation(List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList,
                                                 SettleTransDataTotalAdd settleTransDataTotalAdd){
        // 总转股数
        BigDecimal totalConAmt = settleTransDataTotalAdd.getConAmt();
        // 剩余分配转股数
        BigDecimal remainConAmt = settleTransDataTotalAdd.getConAmt();
        // 转股最小交易单元
        BigDecimal conMinStep = BigDecimal.ONE;
        // 债券总分配数量
        BigDecimal totalBondMatchQty = entrustDetailDtoList
                .stream()
                .map(ExchangeTransMatchCommonEntrustDetailDto::getMarchQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 第一轮按照债券分配数量比例进行分配,按最小交易单元(1),进行向下取整
        for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList){
            // 临时分配数量
            BigDecimal conAmtTemp = ArithmeticUtil.divide(ArithmeticUtil.multiply(totalConAmt, entrustDetailDto.getMarchQty()),
                    totalBondMatchQty, 4);
            // 最小交易单元整数倍
            BigDecimal stepTimes = ArithmeticUtil.divide(conAmtTemp, conMinStep, 0, RoundingMode.FLOOR);
            // 分配数量
            BigDecimal conAmt = ArithmeticUtil.multiply(conMinStep, stepTimes);
            entrustDetailDto.setConAmt(conAmt);
            remainConAmt = ArithmeticUtil.subtract(totalConAmt, conAmt);
        }
        // 第一轮分配完,如还有剩余数量,则按照最小交易单元一次依次进行分配,直到分配完为止
        if(remainConAmt.compareTo(BigDecimal.ZERO) > 0){
            for(ExchangeTransMatchCommonEntrustDetailDto entrustDetailDto : entrustDetailDtoList){
                entrustDetailDto.setConAmt(ArithmeticUtil.doAdd(entrustDetailDto.getConAmt(), conMinStep));
                remainConAmt = ArithmeticUtil.subtract(remainConAmt, conMinStep);
                if(remainConAmt.compareTo(BigDecimal.ZERO) == 0){
                    break;
                }
            }
        }
    }
    // ZGCPCN-1730:债转股支持系列户指令分配 add by fuyongsheng 20220627 end
}
