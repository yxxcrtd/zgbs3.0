package com.joyintech.tams.invest.common.trade.service.impl.etrcash;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService.getSeqNo;

@Service
public class EtrSharesCashPositionDealService extends EtrCashPositionBaseService {

    /**
     * 股票（持仓以数量记）
     * 
     * @param finProductTradeDto
     * @param 持仓类型
     * @param etrPositionHoldDto
     */
    public static void deal股票CashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型,
                                          EtrPositionHoldDto etrPositionHoldDto) {
        TrdProductDeal deal = finProductTradeDto.getTrdProductDeal();
        List<StlPayOrdersDto> stlPayOrdersDtos = finProductTradeDto.getStlPayOrdersList();
        // 交易方向
        String ps = deal.getPs();
        // 委托代码
        String entrustId = finProductTradeDto.getEntrustId();

        // 每股送转
        BigDecimal 每股送转 = deal.getUnitCprice();
        // 每股派息
        BigDecimal 每股派息 = deal.getUnitInt();
        // 持仓数量
        BigDecimal 交易数量 = deal.getShareAmt();
        // 交易金额
        BigDecimal 交易金额 = deal.getTradeAmt();
        // 交易生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDto(
            finProductTradeDto);
        // 交易生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDto(
            finProductTradeDto);
        String acctId = EtrCommonCashPositionDealService.select资金账户(finProductTradeDto.getTrdProductDeal().getSecManageAcctId(),
            finProductTradeDto.getTrdProductDeal().getCcy());
        etrCashPositionFlowDto.setOrderId(deal.getTradeId());

        EtrSecPositionDto etrSecPositionDto = new EtrSecPositionDto();
        etrSecPositionDto.setPortfolioId(finProductTradeDto.getTrdProductDealAdd().getPortfolioId());
        etrSecPositionDto.setSecAcctId(finProductTradeDto.getTrdProductDeal().getSecManageAcctId());
        etrSecPositionDto.setFinprodId(deal.getFinprodId());
        etrSecPositionDto.setBranch(0l);
        etrSecPositionDto.setCdate(deal.getTradeDate());
        etrSecPositionDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        // 查询可用持仓
        BigDecimal 持仓数量 = EtrCommonCashPositionDealService.selectAvailablePositionAmount(etrSecPositionDto);

        // 买入/新股中签/增发（配售）/配股(金额委托、数量委托)
        if (DictCodeList.交易方向_买入.getValue().equals(ps)||DictCodeListExt2.交易方向_新股中签.getValue().equals(ps)||DictCodeListExt2.交易方向_增发.getValue().equals(ps)||DictCodeListExt2.交易方向_配股.getValue().equals(ps)) {
            // 交易生效
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                if (StringUtil.isEmpty(entrustId)) { // 无委托
                    // 交割日期增加交易数量
                    etrSecPositionFlowDto.setAmount(交易数量);
                    // 添加持仓流水到目标dto
                    // GNS-862:配股不增加指令持仓 modify by fuyongsheng 20220218 start
                    // etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    if(!DictCodeListExt2.交易方向_配股.getValue().equals(ps)){
                        etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    }
                    // GNS-862:配股不增加指令持仓 modify by fuyongsheng 20220218 end
                }
                else {// 有委托
                      // 交割日期增加交易数量
                    etrSecPositionFlowDto.setAmount(交易数量);
                    // GNS-862:配股不增加指令持仓 modify by fuyongsheng 20220218 start
                    // etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    if(!DictCodeListExt2.交易方向_配股.getValue().equals(ps)){
                        etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                    }
                    // GNS-862:配股不增加指令持仓 modify by fuyongsheng 20220218 end
                    // 委托dto
                    EntrustCommonDto entrustCommonDto = EtrCommonCashPositionDealService.findEntrust(entrustId);
                    EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
                    // 价格模式
                    String 价格模式 = etrEntrust.getPriceModel();
                    // 委托数量
                    BigDecimal 委托数量 = etrEntrust.getEntrustNum();
                    // 委托价格
                    BigDecimal 委托价格 = etrEntrust.getPrice();
                    // 数量委托
                    if (委托数量 != null && 委托数量.compareTo(BigDecimal.ZERO) > 0) {
                        // 交割日减少（交易金额-交易数量*委托价格,若为市价委托，委托价格取P
                        if (DictCodeListExt2.价格模式_市价指令.getValue().equals(价格模式)) {// 市价委托
                            // 最新收盘价
                            String 股票代码 = finProductTradeDto.getTrdProductDeal().getFinprodId();
                            BigDecimal 最新收盘价 = EtrCommonCashPositionDealService.getClosePrice(股票代码,
                                    etrEntrust.getEntrustDate());
                            // 最新收盘价*（1+10%）记为P0
                            BigDecimal P0 = 最新收盘价 == null ? 委托价格 : 最新收盘价;

                            // 冲销流水
                            etrCashPositionFlowDto.setAmount(交易数量.multiply(P0));
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrCashPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                            // 正常流水
                            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                            etrCashPositionFlowDto1.setSeqno(getSeqNo());
                            etrCashPositionFlowDto1.setAmount(交易金额.negate());
                            etrCashPositionFlowDto1.setAcctId(acctId);
                            etrCashPositionFlowDto1.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);

                        }
                        else {// 非市价委托
                            // 冲销流水
                            etrCashPositionFlowDto.setAmount(交易数量.multiply(委托价格));
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrCashPositionFlowDto.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                            // 正常流水
                            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                            etrCashPositionFlowDto1.setSeqno(getSeqNo());
                            etrCashPositionFlowDto1.setAmount(交易金额.negate());
                            etrCashPositionFlowDto1.setAcctId(acctId);
                            etrCashPositionFlowDto1.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                        }
                    }
                }
            }// 交易提交
            if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                if (StringUtil.isEmpty(entrustId)) { // 无委托
                    // 现金流水dto处理
                    // 交割日减少交易金额
                    EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                    etrCashPositionFlowDto1.setSeqno(getSeqNo());
                    etrCashPositionFlowDto1.setAmount(交易金额.negate());
                    etrCashPositionFlowDto1.setAcctId(acctId);
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);
                }
            }
        }
        // 卖出
        else if (StringUtil.equals(ps, BussinessDictCodeList.交易方向_卖出.getValue())) {
            // 交易生效
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                // 卖出交割日增加交易金额
                etrCashPositionFlowDto.setAmount(交易金额);
                etrCashPositionFlowDto.setAcctId(acctId);
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
            // 交易提交
            if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                if (StringUtil.isEmpty(entrustId)) { // 无委托
                    // 交易日减少交易数量
                    etrSecPositionFlowDto.setAmount(交易数量.negate());
                    // 添加持仓流水到目标dto
                    etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                }
            }
        }
        // 现金分红
        else if (DictCodeList.交易方向_现金分红.getValue().equals(ps)) {
            if (stlPayOrdersDtos != null && !stlPayOrdersDtos.isEmpty()) {
                boolean 处理预增 =  true;
                if (finProductTradeDto.isNotWorkflow()){
                    处理预增 = false;
                }
                // 交易生效
                if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                    // etrCashPositionFlowDto.setAmount(每股派息.multiply(交易数量));
                    deal(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto, acctId, finProductTradeDto.isNotWorkflow());
                }
                // 交易提交
                if (DictCodeList.持仓类型_可用.equals(持仓类型) || etrPositionHoldDto.isNeedNegative()) {
                    EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
                    BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
                    etrCashPositionFlowDto1.setSeqno(getSeqNo());
                    // etrCashPositionFlowDto1.setAmount(每股派息.multiply(交易数量));
                    预增现金处理(etrPositionHoldDto, stlPayOrdersDtos, etrCashPositionFlowDto1, acctId, 处理预增);
                }
            }
        }
        // 送转
        else if (DictCodeList.交易方向_红利再投.getValue().equals(ps)) {
            // GNS-932:上交所红利再投 指令持仓不变 modify by fuyongsheng 20220225 start
            if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(deal.getTradeMarket())){
                return;
            }
            // GNS-932:上交所红利再投 指令持仓不变 modify by fuyongsheng 20220225 end
            // 交易生效
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                // 派息日增加每股送转*持仓数量
                etrSecPositionFlowDto.setAmount(交易数量);
                // 添加持仓流水到目标dto
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
            }
        }
        // 分红送转
        /**else if (DictCodeList.交易方向_除权登记.getValue().equals(ps)) {
            // 交易生效
            if (DictCodeList.持仓类型_实际.equals(持仓类型)) {
                // 派息日增加每股送转*持仓数量
                etrSecPositionFlowDto.setAmount(每股送转.multiply(持仓数量));
                // 添加持仓流水到目标dto
                etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                // 派息日增加每股派息*持仓数量
                etrCashPositionFlowDto.setAmount(每股派息.multiply(持仓数量));
                etrCashPositionFlowDto.setAcctId(acctId);
                etrCashPositionFlowDto.setCdate(deal.getTradeDate());
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
        }**/
    }
    /**
     * gen冲销现金流水生成
     * @param etrCashPositionFlowDto 正常流水
     * @param entrustId 委托代码
     * @param 交易数量
     * @return 冲销流水
     */
    public static EtrCashPositionFlowDto gen冲销现金流水生成(EtrCashPositionFlowDto etrCashPositionFlowDto, String entrustId, BigDecimal 交易数量) {
        EtrCashPositionFlowDto ret = new EtrCashPositionFlowDto();
        BeanUtil.copyPropertiesIgnoreNull(ret,etrCashPositionFlowDto);
        ret.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
        ret.setSeqno(EtrCommonCashPositionDealService.getSeqNo());
        ret.setOffsetId(entrustId);
        EntrustCommonDto entrustCommonDto = EtrCommonCashPositionDealService.findEntrust(entrustId);
        if(entrustCommonDto!=null){
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            // 价格模式
            String 价格模式 = etrEntrust.getPriceModel();
            // 委托价格
            BigDecimal 委托价格 = etrEntrust.getPrice();
            String acctId = EtrCommonCashPositionDealService.select资金账户(entrustCommonDto.getEtrEntrustAdd().getSecAcctId(),
                    etrEntrust.getCcy());
            // 开始日减少委托金额(委托金额=委托数量*委托价格,若为市价委托，则取最新收盘价*（1+10%），记为P0)
            if (价格模式.equals(DictCodeListExt2.价格模式_市价指令.getValue())) {// 市价委托
                // 最新收盘价
                String 股票代码 = entrustCommonDto.getEtrEntrust().getFinprodId();
                BigDecimal 最新收盘价 = EtrCommonCashPositionDealService.getClosePrice(股票代码,
                        entrustCommonDto.getEtrEntrust().getEntrustDate());
                // 最新收盘价*（1+10%）记为P0
                BigDecimal P0 = 最新收盘价 == null ? 委托价格 : 最新收盘价;
                ret.setAmount(交易数量.multiply(P0).negate());
            }
            else {// 非市价委托
                ret.setAmount(交易数量.multiply(委托价格).negate());
            }
        }
        return ret;
    }
    private static void deal(EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos,
                             EtrCashPositionFlowDto etrCashPositionFlowDto, String acctId, boolean isNotWorkflow) {
        stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
            // 可用现金 资金到账日增加到账金额
            EtrCashPositionFlowDto etrCashPositionFlowDto1 = new EtrCashPositionFlowDto();
            BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto1, etrCashPositionFlowDto);
            etrCashPositionFlowDto1.setAmount(stlPayOrdersDto.getSettleAmt());
            etrCashPositionFlowDto1.setAcctId(stlPayOrdersDto.getMyAcctNo());
            etrCashPositionFlowDto1.setCdate(stlPayOrdersDto.getSettleDate());
            etrCashPositionFlowDto1.setSeqno(getSeqNo());
            etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto1);

            if (!isNotWorkflow){
            	// 预增现金 资金到账日减少到账金额
                EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto1);
                etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt().negate());
                etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
                etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                etrCashPositionFlowDto2.setSeqno(getSeqNo());
                etrCashPositionFlowDto1.setOrderId(stlPayOrdersDto.getOrderId());
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
            }
        });
    }

    private static void 预增现金处理(EtrPositionHoldDto etrPositionHoldDto, List<StlPayOrdersDto> stlPayOrdersDtos,
                               EtrCashPositionFlowDto etrCashPositionFlowDto, String acctId, boolean 处理预增) {
        stlPayOrdersDtos.forEach(stlPayOrdersDto -> {
            if(处理预增){
                // 预增现金 资金到账日增加到账金额
                EtrCashPositionFlowDto etrCashPositionFlowDto2 = new EtrCashPositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(etrCashPositionFlowDto2, etrCashPositionFlowDto);
                etrCashPositionFlowDto2.setAmount(stlPayOrdersDto.getSettleAmt());
                etrCashPositionFlowDto2.setAcctId(stlPayOrdersDto.getMyAcctNo());
                etrCashPositionFlowDto2.setCdate(stlPayOrdersDto.getSettleDate());
                etrCashPositionFlowDto2.setHodingType(DictCodeListExt1.持仓类型_委托指令预增.getValue());
                etrCashPositionFlowDto2.setSeqno(getSeqNo());
                etrCashPositionFlowDto2.setOrderId(stlPayOrdersDto.getOrderId());
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto2);
            }
        });
    }

    public static void deal股票EntrustStart(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        if (entrustCommonDto != null) {
            EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
            // 交易方向
            String ps = etrEntrust.getPs();
            // 价格模式
            String 价格模式 = etrEntrust.getPriceModel();
            // 委托数量
            BigDecimal 委托数量 = etrEntrust.getEntrustNum();
            // 委托金额
            BigDecimal 委托金额 = etrEntrust.getEntrustAmt();
            // 委托价格
            BigDecimal 委托价格 = etrEntrust.getPrice();

            // 委托生成的持仓流水
            EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
                entrustCommonDto);
            // 委托生成的现金流水
            EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
                entrustCommonDto);
            String acctId = EtrCommonCashPositionDealService.select资金账户(entrustCommonDto.getEtrEntrustAdd().getSecAcctId(),
                etrEntrust.getCcy());
//            委托买入/新股中签/增发（配售）/配股
            if (DictCodeList.交易方向_买入.getValue().equals(ps)||DictCodeListExt2.交易方向_新股中签.getValue().equals(ps)||DictCodeListExt2.交易方向_增发.getValue().equals(ps)||DictCodeListExt2.交易方向_配股.getValue().equals(ps)) {
                // 金额委托
                if (委托金额 != null && 委托金额.compareTo(BigDecimal.ZERO) > 0) {
                    // 减少委托金额
                    etrCashPositionFlowDto.setAmount(委托金额.negate());
                    etrCashPositionFlowDto.setAcctId(acctId);
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                }
                // 数量委托
                else if (委托数量 != null && 委托数量.compareTo(BigDecimal.ZERO) > 0) {
                    // 开始日减少委托金额(委托金额=委托数量*委托价格,若为市价委托，则取最新收盘价*（1+10%），记为P0)
                    if (价格模式.equals(DictCodeListExt2.价格模式_市价指令.getValue())) {// 市价委托
                        // 最新收盘价
                        String 股票代码 = entrustCommonDto.getEtrEntrust().getFinprodId();
                        BigDecimal 最新收盘价 = EtrCommonCashPositionDealService.getClosePrice(股票代码,
                            entrustCommonDto.getEtrEntrust().getEntrustDate());
                        // 最新收盘价*（1+10%）记为P0
                        if(最新收盘价!=null){
                            etrCashPositionFlowDto.setAmount(委托数量.multiply(最新收盘价).negate());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }
                        else if(委托价格!=null){
                            etrCashPositionFlowDto.setAmount(委托数量.multiply(委托价格).negate());
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        } else{
                            etrSecPositionFlowDto.setAmount(委托数量);
                            etrSecPositionFlowDto.setSecAcctId(acctId);
                            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                        }
                    }
                    else {// 非市价委托
                        etrCashPositionFlowDto.setAmount(委托数量.multiply(委托价格).negate());
                        etrCashPositionFlowDto.setAcctId(acctId);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    }
                }
            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
                if(委托金额!=null){
                    etrCashPositionFlowDto.setAmount(委托金额.negate());
                    etrCashPositionFlowDto.setAcctId(acctId);
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                }else if (委托数量 != null && 委托数量.compareTo(BigDecimal.ZERO) > 0) {
                    if (价格模式.equals(DictCodeListExt2.价格模式_市价指令.getValue())) {
                        // 最新收盘价
                        String 股票代码 = entrustCommonDto.getEtrEntrust().getFinprodId();
                        BigDecimal 最新收盘价 = EtrCommonCashPositionDealService.getClosePrice(股票代码,
                                entrustCommonDto.getEtrEntrust().getEntrustDate());
                        // 最新收盘价*（1+10%）记为P0
                        if(最新收盘价!=null){
                            etrCashPositionFlowDto.setAmount(委托数量.multiply(最新收盘价));
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }else if(委托价格!=null){
                            etrCashPositionFlowDto.setAmount(委托数量.multiply(委托价格));
                            etrCashPositionFlowDto.setAcctId(acctId);
                            etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                        }else{
                            etrSecPositionFlowDto.setAmount(委托数量.negate());
                            etrSecPositionFlowDto.setSecAcctId(acctId);
                            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
                        }
                    }else{
                        etrCashPositionFlowDto.setAmount(委托数量.multiply(委托价格));
                        etrCashPositionFlowDto.setAcctId(acctId);
                        etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                    }
                }
            }
        }
    }

    public static void deal股票EntrustClose(EntrustCommonDto entrustCommonDto, EtrPositionHoldDto etrPositionHoldDto) {
        EtrEntrust etrEntrust = entrustCommonDto.getEtrEntrust();
        // 交易方向
        String ps = etrEntrust.getPs();
        // 价格模式
        String 价格模式 = etrEntrust.getPriceModel();
        // 委托数量
        BigDecimal 委托数量 = etrEntrust.getEntrustNum();
        // 委托金额
        BigDecimal 委托金额 = etrEntrust.getEntrustAmt();
        // 委托价格
        BigDecimal 委托价格 = etrEntrust.getPrice();
        // 已完成额度
        Map<String,Object> map=new HashMap<>();
        map.put("sumTradeFlag","01");
        map.put("entrustClass", etrEntrust.getEntrustClass());
        map.put("portfolioId",etrEntrust.getPortfolioId());
        map.put("entrustId",etrEntrust.getEntrustId());
        BigDecimal etrFinish = EtrCommonCashPositionDealService.getEtrExchangeFinishAmtMuilt(map);
        //BigDecimal etrFinish = EtrCommonCashPositionDealService.getEtrFinishAmt(etrEntrust.getEntrustId());
        // 委托生成的持仓流水
        EtrSecPositionFlowDto etrSecPositionFlowDto = EtrCommonCashPositionDealService.getEtrSecPositionFlowDtoFromEtr(
            entrustCommonDto);
        // 委托生成的现金流水
        EtrCashPositionFlowDto etrCashPositionFlowDto = EtrCommonCashPositionDealService.getEtrCashPositionFlowDtoFromEtr(
            entrustCommonDto);
        etrCashPositionFlowDto.setOffsetId(etrEntrust.getEntrustId());
        String acctId = EtrCommonCashPositionDealService.select资金账户(entrustCommonDto.getEtrEntrustAdd().getSecAcctId(),
            etrEntrust.getCcy());
        if (StringUtil.isNotEmpty(acctId) && (DictCodeList.交易方向_买入.getValue().equals(ps)||DictCodeListExt2.交易方向_新股中签.getValue().equals(ps) || DictCodeList.交易方向_申购.getValue().equals(ps)
                ||DictCodeListExt2.交易方向_增发.getValue().equals(ps)||DictCodeListExt2.交易方向_配股.getValue().equals(ps))) {
            // 金额委托
            if (委托金额 != null && 委托金额.compareTo(BigDecimal.ZERO) > 0) {
                // 剩余委托金额
                BigDecimal 剩余委托金额 = ArithmeticUtil.subtract(etrEntrust.getEntrustAmt(), etrFinish);
                // 增加剩余委托金额
                etrCashPositionFlowDto.setAmount(剩余委托金额);
                etrCashPositionFlowDto.setAcctId(acctId);
                etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
            }
            // 数量委托
            else if (委托数量 != null && 委托数量.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal 未完成委托数量 = ArithmeticUtil.subtract(委托数量, etrFinish);
                // 关闭日增加剩余委托数量*委托价格,若为市价委托，委托价格取P0
                if (DictCodeListExt2.价格模式_市价指令.getValue().equals(价格模式)) {// 市价委托
                    // 最新收盘价
                    String 股票代码 = entrustCommonDto.getEtrEntrust().getFinprodId();
                    BigDecimal 最新收盘价 = EtrCommonCashPositionDealService.getClosePrice(股票代码,
                        entrustCommonDto.getEtrEntrust().getEntrustDate());
                    // 最新收盘价*（1+10%）记为P0
                    BigDecimal P0 = 最新收盘价 == null ? 委托价格 : 最新收盘价;
                    etrCashPositionFlowDto.setAmount(未完成委托数量.multiply(P0));
                    etrCashPositionFlowDto.setAcctId(acctId);
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                }
                else {// 非市价委托
                    etrCashPositionFlowDto.setAmount(未完成委托数量.multiply(委托价格));
                    etrCashPositionFlowDto.setAcctId(acctId);
                    etrPositionHoldDto.getPtlCashPositionFlows().add(etrCashPositionFlowDto);
                }
            }
        }
        else if (DictCodeList.交易方向_卖出.getValue().equals(ps)) {
            BigDecimal 未完成委托数量 = ArithmeticUtil.subtract(委托数量, etrFinish);
            // 委托生成的持仓流水
            // 关闭日增加剩余委托份额
            etrSecPositionFlowDto.setAmount(未完成委托数量);
            etrPositionHoldDto.getPtlSecPositionFlows().add(etrSecPositionFlowDto);
        }
    }
}