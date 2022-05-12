package com.joyintech.tams.exchange.service.trade.processor.portfolio;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.InstructionDetail;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import com.joyintech.tams.exchange.common.enums.exchange.PortfolioDetailBusiStatusEnum;
import com.joyintech.tams.exchange.common.enums.exchange.PortfolioDetailBusiTypeEnum;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.dao.PortfolioDetailByManualMapper;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionDetailService;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 组合分配
 *
 * @author swwan
 * @date 2022/5/11 - 11:21
 */
@Component
public class PortfolioAssignHelper {

    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(PortfolioAssignHelper.class);

    /**
     * 金额默认精度
     */
    private static final Integer AMT_SCALE = 2;
    /**
     * 除法默认精度
     */
    private static final Integer DIV_SCALE = 8;

    @Autowired
    private PortfolioDetailByManualMapper portfolioDetailByManualMapper;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private InstructionDetailService instructionDetailService;
    @Autowired
    private QuoteService quoteService;

    /**
     * 组合分配计算
     *
     * @param instructionId 指令代码
     * @param busiType      业务类型
     * @param orginVolume   待分配数量
     * @param orginPrice    价格
     * @param orginProp     交易属性
     * @return 分配列表
     */
    public ResultBean queryPortfolioAssign(String instructionId, PortfolioDetailBusiTypeEnum busiType, final BigDecimal orginVolume, final BigDecimal orginPrice, final String orginProp) {
        log.info("queryPortfolioAssign() called with parameters => 【instructionId = {}】, 【busiType = {}】, 【orginVolume = {}】, 【orginPrice = {}】, 【orginProp = {}】", instructionId, busiType, orginVolume, orginPrice, orginProp);

        // 待分配数量/金额错误
        if (TradeUtils.lessThanZero(orginVolume)) {
            log.warn("待分配数量/金额错误 => 【orginVolume = {}】", orginVolume);
            return ResultBean.error(ErrorCode.REQ_PARAMETER_ERROR);
        }

        // 获取指令
        Instruction instruction = instructionService.findById(instructionId);
        if (Objects.isNull(instruction)) {
            log.warn("未找到指令信息: instructionId [{}]", instructionId);
            return ResultBean.error(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }

        // 获取多组合
        List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(instructionId);
        // 排序，以保证尾插分配稳定
        instructionDetails.sort(Comparator.comparing(InstructionDetail::getPortfolioId));

        // 获取委托属性
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, orginProp);
        if (StrUtil.isEmpty(entrustProp)) {
            log.warn("未找到委托属性: instructionId [{}], entrustProp = [{}]", instructionId, orginProp);
            return ResultBean.error(ErrorCode.REQ_PARAMETER_ENTRUST_PROP_ERROR);
        }

        // 最小交易单元：整数倍
        BigDecimal minStep = BigDecimal.valueOf(instruction.queryMinTrdUnitLimit(entrustProp).getMinStep());
        if (ObjectUtil.isNull(minStep) || minStep.intValue() <= 0) {
            log.warn("未找到交易属性: instructionId [{}], entrustProp = [{}]", instructionId, entrustProp);
            return ResultBean.error(ErrorCode.MIN_TRD_UNIT_LIMIT_ERROR);
        }

        // 价格
        BigDecimal price = orginPrice;
        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        //市价委托填充反向涨跌停价
        if (EntrustProp.isMarketPrice(entrustProp)) {
            ResultBean tickBean = quoteService.getTick(instruction.getFinprodMarketId());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
                price = EntrustBs.isSell(entrustBs)
                        ? NumberUtil.round(tick.getLowerLimitPrice(), 4)
                        : NumberUtil.round(tick.getUpperLimitPrice(), 4);
            }
        }
        // 特殊处理 质押式回购交易，将委托价格设置为100
        if (EntrustUtils.isPledgeRepurchase(instruction)) {
            price = BigDecimal.valueOf(100L);
        }

        /*if (CollUtil.isEmpty(instructionDetails)) {
            // 单组合时处理
            InstructionDetail instructionDetail = new InstructionDetail();
            instructionDetail.setEntrustId(instruction.getEntrustId());
            instructionDetail.setPortfolioId(instruction.getPortfolioId());
            instructionDetail.setPortfolioName(instruction.getPortfolioName());
            instructionDetail.setDisRate(BigDecimal.valueOf(1.00));
            instructionDetail.setEntrustAmt(instruction.getEntrustAmt());
            instructionDetail.setEntrustNum(new BigDecimal(instruction.getEntrustNum()));
            instructionDetail.setTargetType(instruction.getEntrustNum() == 0 ? "02" : "01");
            instructionDetails.add(instructionDetail);
        } else if (instructionDetails.size() == 1) {
            InstructionDetail instructionDetail = instructionDetails.get(0);
            if (instructionDetail.getDisRate() == null || instructionDetail.getDisRate().compareTo(new BigDecimal("0")) == 0) {
                instructionDetail.setDisRate(BigDecimal.valueOf(1.00));
            }
        }*/

        BigDecimal volume = orginVolume;

        // 历史明细
        List<PortfolioDetail> portfolioDetails = portfolioDetailByManualMapper.selectListByInstructionAndBusiType(instructionId, busiType.getCode());
        log.info("查询的历史组合分配明细：{}", portfolioDetails);
        // 历史数量
        BigDecimal historyVolume = portfolioDetails.stream().map(PortfolioDetail::getTargetNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 订单数量
        BigDecimal totalVolume = NumberUtil.add(volume, historyVolume);

        // 计算结果列表
        List<PortfolioDetail> list = ListUtil.list(false);
        // 计算结果
        PortfolioDetail portfolioDetail = null;
        BigDecimal entrustVolume = BigDecimal.ZERO;
        BigDecimal entrustAmt = BigDecimal.ZERO;

        for (InstructionDetail detail : instructionDetails) {
            portfolioDetail = new PortfolioDetail();

            portfolioDetail.setInstructionId(instructionId);
            portfolioDetail.setPortfolioId(detail.getPortfolioId());
            portfolioDetail.setPortfolioName(detail.getPortfolioName());
            portfolioDetail.setTargetType(detail.getTargetType());
            portfolioDetail.setBusiStatus(PortfolioDetailBusiStatusEnum.CONFIRM.getCode());
            portfolioDetail.setBusiType(busiType.getCode());

            // 根据比例计算数量
            entrustVolume = NumberUtil.div(NumberUtil.mul(detail.getEntrustNum(), totalVolume), instruction.getEntrustNum(), DIV_SCALE);
            // 取最小交易单位整数倍
            entrustVolume = TradeUtils.subtractMod(entrustVolume, minStep);
            // 金额
            entrustAmt = NumberUtil.round(NumberUtil.mul(entrustVolume, price), AMT_SCALE);

            log.debug("根据分配比例计算，组合名称 = [{}]， 组合编号 = [{}]，订单数量 = [{}]， 分配比例 = [{}]， 委托数量 = [{}]，委托金额 = [{}]",
                    portfolioDetail.getPortfolioName(), portfolioDetail.getPortfolioId(), totalVolume,
                    detail.getDisRate(), entrustVolume, entrustAmt);
            portfolioDetail.setTargetNum(entrustVolume);
            portfolioDetail.setTargetAmt(entrustAmt);

            list.add(portfolioDetail);
        }

        // 尾差处理
        PortfolioDetail maxItem = list.stream().max(Comparator.comparing(PortfolioDetail::getTargetNum)).get();
        BigDecimal summaryWithOutMaxVolume = list.stream().filter(response -> !StrUtil.equals(response.getPortfolioId(), maxItem.getPortfolioId()))
                .map(PortfolioDetail::getTargetNum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tailVolume = NumberUtil.sub(totalVolume, summaryWithOutMaxVolume);
        BigDecimal tailAmt = NumberUtil.round(NumberUtil.mul(price, tailVolume), AMT_SCALE);
        log.debug("尾插处理，组合名称 = [{}], 组合编号 = [{}], 原委托数量 = [{}], 现委托数量 = [{}]，现委托金额 = [{}]",
                maxItem.getPortfolioName(), maxItem.getPortfolioId(),
                maxItem.getTargetNum(), tailVolume, tailAmt);
        maxItem.setTargetNum(tailVolume);
        maxItem.setTargetAmt(tailAmt);

        //历史明细
        Map<String, List<PortfolioDetail>> map = portfolioDetails.stream().collect(Collectors.groupingBy(PortfolioDetail::getPortfolioId));
        for (PortfolioDetail detail : list) {
            // 历史数量 根据组合代码求和
            BigDecimal historyNum = map.get(detail.getPortfolioId()).stream().map(PortfolioDetail::getTargetNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            detail.setTargetNum(NumberUtil.sub(detail.getTargetNum(), historyNum));
            detail.setTargetAmt(NumberUtil.round(NumberUtil.mul(detail.getTargetNum(), price), AMT_SCALE));
        }

        log.debug("queryPortfolioAssign() called with result => 【list = {}】", list);
        return ResultBean.success(list);
    }
}
