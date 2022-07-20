package com.joyintech.tams.invest.common.trade.service;

import com.joyintech.tams.common.base.entity.BidOrderBidPrice;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.CommonResult;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.entrust.dto.EntrustCommonDto;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提供金融产品交易共通业务逻辑处理
 *
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.component.business.period.service
 * @Description: 持仓现金共通
 * @date Date : 2018年09月27日 15:31
 */
public interface BaseEtrCashPositionCommService {
    /**
     * 执行现金持仓共同处理
     * @param finProductTradeDto 执行参数
     * @param needNegative 是否取反
     * @return 执行结果
     */
    CommonResult executeEtrCashPosition(BaseTradeProductDealDto finProductTradeDto, DictCodeList 持仓类型, boolean needNegative,String ieFlag,String isOverdueDeal);

    /**
     * 委托开始执行共通
     * @param entrustCommonDto 需要参数
     * @return 执行结果
     */
    CommonResult entrustStartExecute(EntrustCommonDto entrustCommonDto);

    /**
     * 委托关闭执行共通
     * @param entrustId 委托编号
     * @return 执行结果
     */
    CommonResult entrustCloseExecute(String entrustId);
    /**
     * 委托关闭执行共通,主要是向旧表插入头寸
     * @param entrustId 委托编号 isExistNew 是否已经向新表插入数据，Y：已入
     * @return 执行结果
     */
    CommonResult entrustCloseExecute(String entrustId, String isExistNew);

    // modify by etliu 20220719 start
    // 3.0.6协议回购+风险升级 XYHGFXSJ-156
    //【实时资金头寸查询-协议回购】上交所逆回购指令退回后，资金流水未回冲
    /**
     * 委托关闭执行共通,主要是向旧表插入头寸(协议回购专用)
     * @param entrustId 委托编号 isExistNew 是否已经向新表插入数据，Y：已入
     * @return 执行结果
     */
    CommonResult entrustCloseExecuteForH01(String entrustId, String isExistNew);
    // modify by etliu 20220719 end

    /**
     * 委托关闭执行共通,主要是向旧表插入头寸
     * @param entrustId 委托编号 isExistNew 是否已经向新表插入数据，Y：已入 forcedFinishAmt 强制完成额度
     * @return 执行结果
     */
    CommonResult entrustCloseExecute(String entrustId, String isExistNew, BigDecimal forcedFinishAmt);
    /**
     * 标准券管理出入库共通
     * @param list 出入库质押券列表
     * @return 执行结果
     */
    CommonResult StandardBondExecute(List<EtrSecPositionFlowDto> list,DictCodeList 出入库方向) ;
    
    /**
     * 中标结果维护
     * @param finProductTradeDto
     * @return
     */
    CommonResult executeBidResultsMaintenanceExecute(BidOrderBidPrice finProductTradeDto);
    
    /**
     * 查询可用持仓
     * @param etrSecPositionDto
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal qryAvailablePosition(EtrSecPositionDto etrSecPositionDto);
    
    /**
     * 查询指令可用现金 + 预增
     * @param ptlCashPositionFlowDto 指令可用现金查询Dto
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal qryCommandAvailableCash(PtlCashPositionFlowDto ptlCashPositionFlowDto);
    
    /**
     * 查询指令预增现金
     * @param ptlCashPositionFlowDto 指令可用现金查询Dto
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal qryCommandAdvanceCash(PtlCashPositionFlowDto ptlCashPositionFlowDto);

    /**
     * 产品端影响实时现金共通方法
     * // modify by jiangpeng on 20210429 start
     * // ZGBSS-1148 【债券借贷存续期】：到期交易中有“是否现金交割”的功能没有按照预期实现。
     * @param cashInputDto
     * @return
     */
    CommonResult productEtrCashPosition(PortfolioCashPositionsFlowInputDto cashInputDto, String positionType);

    /**
     * 组合划款（自定义现金流、资金划拨、通道划款（回款））影响实时现金共通方法
     * @param ptlPositionHoldDto
     * @return
     */
    CommonResult portfolioEtrCashPosition(PtlPositionHoldDto ptlPositionHoldDto, boolean 是否插入母组合持仓);

    /**
     * 股票存续期撤销
     * @param tradeId
     * @return
     */
    CommonResult revokePosition(String tradeId);
    /**
     * 查询指令现金(新表)
     * @param ptlCashPositionFlowDto 指令可用现金查询Dto
     * @return
     * @see
     * @since 1.0
     */
    BigDecimal queryEtrAvailableCashNew(PtlCashPositionFlowDto ptlCashPositionFlowDto);
}
