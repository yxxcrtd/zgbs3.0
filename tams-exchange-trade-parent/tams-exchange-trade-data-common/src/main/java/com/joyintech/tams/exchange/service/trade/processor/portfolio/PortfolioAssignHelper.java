package com.joyintech.tams.exchange.service.trade.processor.portfolio;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
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
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.*;

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
        log.info("【queryPortfolioAssign()】 called with parameters => 【instructionId = {}】, 【busiType = {}】, 【orginVolume = {}】, 【orginPrice = {}】, 【orginProp = {}】", instructionId, busiType, orginVolume, orginPrice, orginProp);

        // 待分配数量
        if (TradeUtils.lessThanZero(orginVolume)) {
            log.warn("待分配数量 => 【orginVolume = {}】", orginVolume);
            ResultBean error = ResultBean.error(ErrorCode.REQ_PARAMETER_ERROR);
            error.setErrorMsg("请求参数错误：待分配数量为0");
            return error;
        }

        // 获取指令
        Instruction instruction = instructionService.findById(instructionId);
        if (Objects.isNull(instruction)) {
            log.warn("未找到指令信息: instructionId [{}]", instructionId);
            return ResultBean.error(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }

        // 获取多组合
        List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(instructionId);
        if (CollUtil.isEmpty(instructionDetails)) {
            log.warn("未找到指令组合明细: instructionId [{}]", instructionId);
            return ResultBean.error(ErrorCode.INSTRUCTION_DETAILS_NOT_EXIST);
        }
        // 排序，先按组合的委托数量排序，再按组合代码排序
        instructionDetails.sort(Comparator.comparing(InstructionDetail::getEntrustNum).thenComparing(InstructionDetail::getPortfolioId));

        // 获取委托属性
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, orginProp);
        //新债交易委托属性
        if (StringUtil.equals(投管业务类型_现券交易.getValue(), instruction.getBusiTypeTg())) {
            if (StrUtil.isEmpty(entrustProp)) {
                log.warn("未找到委托属性: instructionId [{}], entrustProp = [{}]", instructionId, orginProp);
                return ResultBean.error(ErrorCode.REQ_PARAMETER_ENTRUST_PROP_ERROR);
            }
            if (EntrustProp.isConsultingTrade(entrustProp)) {
                entrustProp = EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value;
            }
//            entrustProp = EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value;
        } else if (StringUtil.equals(投管业务类型_通用式质押回购.getValue(), instruction.getBusiTypeTg())) {
            entrustProp = EntrustProp.NORMAL.value;
        }

        MinTrdUnitLimit minTrdUnitLimit = instruction.queryMinTrdUnitLimit(entrustProp);
        if (Objects.isNull(minTrdUnitLimit)) {
            log.warn("未找到最小交易单元: instructionId [{}], entrustProp = [{}]", instructionId, entrustProp);
            return ResultBean.error(ErrorCode.MIN_TRD_UNIT_LIMIT_ERROR);
        }

        // 最小交易单元：整数倍
        BigDecimal minStep = BigDecimal.valueOf(minTrdUnitLimit.getMinStep());
        if (ObjectUtil.isNull(minStep) || minStep.intValue() <= 0) {
            log.warn("未找到最小交易单元: instructionId [{}], entrustProp = [{}]", instructionId, entrustProp);
            return ResultBean.error(ErrorCode.MIN_TRD_UNIT_LIMIT_ERROR);
        }

        // 最小交易数量：优先分配一次
        BigDecimal minNum = BigDecimal.valueOf(minTrdUnitLimit.getMinNum());
        if (ObjectUtil.isNull(minNum) || minNum.intValue() <= 0) {
            log.warn("未找到最小交易数量: instructionId [{}], entrustProp = [{}]", instructionId, entrustProp);
            return ResultBean.error(ErrorCode.MIN_TRD_NUM_LIMIT_ERROR);
        }

        //质押式回购、信用保护凭证的交易量是对金额控制的，因此要转换成数量
        if (StringUtil.equals(投管业务类型_通用式质押回购.getValue(), instruction.getBusiTypeTg())
                || StringUtil.equals(投管业务类型_信用保护凭证.getValue(), instruction.getBusiTypeTg())) {
            minNum = minNum.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
        }

        // 可分配到最小交易数量的组合数量
        int minNumAssignableCount = Math.min(NumberUtil.div(orginVolume, minNum, 2).intValue(), instructionDetails.size());

        // 剩余待分配数量（去掉最小交易数据量）
        BigDecimal orginVolumeWithOutMinNum = NumberUtil.sub(orginVolume, NumberUtil.mul(minNumAssignableCount, minNum));

        //质押式回购、信用保护凭证的步长是对金额控制的，因此要转换成数量
        if (StringUtil.equals(投管业务类型_通用式质押回购.getValue(), instruction.getBusiTypeTg())
                || StringUtil.equals(投管业务类型_信用保护凭证.getValue(), instruction.getBusiTypeTg())) {
            minStep = minStep.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
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

        // 历史明细
        List<PortfolioDetail> portfolioDetails = portfolioDetailByManualMapper.selectListByInstructionAndBusiType(instructionId, busiType.getCode());
        log.info("查询的历史组合分配明细：{}", portfolioDetails);
        // 历史数量
        BigDecimal historyVolume = portfolioDetails.stream().map(PortfolioDetail::getTargetNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 订单数量
//        BigDecimal totalVolume = NumberUtil.add(orginVolume, historyVolume);
        //考虑最小交易数量修改
        BigDecimal totalVolume = NumberUtil.add(orginVolumeWithOutMinNum, historyVolume);
        // 指令委托数量
        BigDecimal instructionVolume = instructionDetails.stream().map(InstructionDetail::getEntrustNum).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算结果列表
        List<PortfolioDetail> list = ListUtil.list(false);
        PortfolioDetail portfolioDetail;

        // 组合明细中的委托数量
        BigDecimal currVolume;
        // 分配给组合的委托数量
        BigDecimal assignVolume;

        for (int i = 0, len = instructionDetails.size(); i < len; i++) {

            InstructionDetail detail = instructionDetails.get(i);
            portfolioDetail = new PortfolioDetail();

            portfolioDetail.setInstructionId(instructionId);
            portfolioDetail.setPortfolioId(detail.getPortfolioId());
            portfolioDetail.setPortfolioName(detail.getPortfolioName());
            portfolioDetail.setTargetType(detail.getTargetType());
            portfolioDetail.setBusiStatus(PortfolioDetailBusiStatusEnum.CONFIRM.getCode());
            portfolioDetail.setBusiType(busiType.getCode());

            portfolioDetail.setEtrSecAmount(detail.getEntrustAmt());
            list.add(portfolioDetail);
            if (i == len - 1) {
                portfolioDetail.setTargetNum(totalVolume);
                break;
            }
            currVolume = detail.getEntrustNum();
            assignVolume = NumberUtil.div(NumberUtil.mul(currVolume, totalVolume), instructionVolume, 0);
            assignVolume = TradeUtils.subtractMod(assignVolume, minStep);

            if (i + minNum.intValue() < instructionDetails.size()) {//没有分到最小交易数量的不分
                assignVolume = BigDecimal.ZERO;
            }

            portfolioDetail.setTargetNum(assignVolume);
            // 更新指令委托总数
            instructionVolume = instructionVolume.subtract(currVolume);
            // 更新待分配的总数
            totalVolume = totalVolume.subtract(assignVolume);
        }


        // 计算结果
        /*PortfolioDetail portfolioDetail = null;
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
        maxItem.setTargetAmt(tailAmt);*/

        //历史明细
        Map<String, List<PortfolioDetail>> map = portfolioDetails.stream().collect(Collectors.groupingBy(PortfolioDetail::getPortfolioId));
        for (PortfolioDetail detail : list) {
            // 历史数量 根据组合代码求和
            List<PortfolioDetail> detailList = map.get(detail.getPortfolioId());
            BigDecimal historyNum = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(detailList)) {
                historyNum = historyNum.add(detailList.stream().map(PortfolioDetail::getTargetNum).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            detail.setTargetNum(NumberUtil.sub(detail.getTargetNum(), historyNum));
            detail.setTargetAmt(NumberUtil.round(NumberUtil.mul(detail.getTargetNum(), price), AMT_SCALE));
        }

        //----------最小交易数量修改------------
        for (int i = 0; i < list.size(); i++) {
            if (i + minNumAssignableCount >= list.size()) {
                PortfolioDetail detail = list.get(i);
                detail.setTargetNum(NumberUtil.add(detail.getTargetNum(), minNum));
                detail.setTargetAmt(NumberUtil.round(NumberUtil.mul(detail.getTargetNum(), price), AMT_SCALE));
            }
        }

        Map<String, PortfolioDetail> portfolioDetailMap = list.stream().collect(Collectors.toMap(PortfolioDetail::getPortfolioId, item -> item, (o1, o2) -> o1));
        log.debug("【queryPortfolioAssign()】 called with result => 【portfolioDetailMap = {}】", portfolioDetailMap);
        return ResultBean.success(portfolioDetailMap);
    }
}
