package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.util.BigDecimalUtil;
import com.joyintech.tams.cfets.trd.bond.dto.CalRequest;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.bond.dto.RiskCalRequest;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondCalService;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub001Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao.CfetsTrdBondFirstSub002Mapper;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInitRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.RegTradeFee;
import com.joyintech.tams.common.base.entity.RegTradeFeeTotal;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.WflEtrEntrustKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 一级申购 新建 SERVICE
 *
 * @author jp
 * @date 2022/04/18
 */
@Service
public class CfetsTrdBondFirstSub002Service extends BaseService<CfetsTrdBondFirstSubInfoDto> implements BaseProcessService<CfetsTrdBondFirstSubInfoDto> {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub002Service.class);

    @Autowired
    private CfetsTrdBondFirstSub001Mapper cfetsTrdBondFirstSub001Mapper;

    @Autowired
    private CfetsTrdBondFirstSub002Mapper cfetsTrdBondFirstSub002Mapper;

    /**
     * 指令信息(流程中)
     */
    @Autowired
    private WflEtrEntrustMapper wflEtrEntrustMapper;

    /**
     * 平台日期组件接口，提供平台日期的获取和更新
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private IbuQuoteBondCalService ibuQuoteBondCalService;

    @Autowired
    private CfetsTrdBondFirstSub001Service cfetsTrdBondFirstSub001Service;

    /**
     * 认购量最小变动单位
     */
    private static BigDecimal subMinChangeUnit = new BigDecimal(1000);

    /**
     * 应缴款金额最小变动单位
     */
    private static BigDecimal payMinChangeUnit = new BigDecimal(0.01);

    /**
     * 结算服务费最小变动单位
     */
    private static BigDecimal feeMinChangeUnit = new BigDecimal(0.01);

    /**
     * 申购点击check
     *
     * @param request 入参,指令信息
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/18 15:16
     */
    public AjaxResult dealexecutioncheck(CfetsTrdBondFirstSubInitRequest request) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        Date systemDate = iPlatformDate.getSysDate();
        logger.debug("自动分配,入参:" + JsonUtils.object2Json(request));
        if (StringUtil.isNotEmpty(request) && StringUtil.isNotEmpty(request.getEntrustDtos())) {
            String finprodMarketId = null;
            String secManageAcctId = null;
            for (CfetsTrdBondFirstSubEntrustDto entrustDto : request.getEntrustDtos()) {
                ValidMessage error = new ValidMessage();
                // 资产代码+证券账户必须一致提示信息
                String msg = null;
                // 1.指令状态为已接收且该指令不存在指令流程中数据，不满足弹出提示
                if (!StringUtil.equals(entrustDto.getEntrustStatus(), DictCodeList.委托状态_已接收.getValue())) {
                    errorList.add(new ValidMessage("只可对已接收的指令执行申购操作!"));
                }
                WflEtrEntrustKey wflEtrEntrust = wflEtrEntrustMapper.selectByEntrustId(entrustDto.getEntrustId());
                if (StringUtil.isNotEmpty(wflEtrEntrust)) {
                    errorList.add(new ValidMessage("指令存在指令流程中数据不可执行申购操作!"));
                }
                // 2.平台日必须等于发行日，否则弹出提示“平台日为发行日才可进行申购”
                if (DateUtil.compareTo(systemDate, entrustDto.getIssueDate()) != 0) {
                    errorList.add(new ValidMessage("平台日为发行日才可进行申购!"));
                }
                // 3.选择多条指令时，资产代码+证券账户必须一致，如不满足则提示“所选指令不满足汇总条件：【债券标的OR证券账户】不同”，多个条件不满足则同时显示多个条件。
                if (StringUtil.isEmpty(finprodMarketId)) {
                    finprodMarketId = entrustDto.getFinprodMarketId();
                }
                if (!StringUtil.equals(finprodMarketId, entrustDto.getFinprodMarketId())) {
                    msg = "债券标的";
                }
                if (StringUtil.isEmpty(secManageAcctId)) {
                    secManageAcctId = entrustDto.getSecManageAcctId();
                }
                if (!StringUtil.equals(secManageAcctId, entrustDto.getSecManageAcctId())) {
                    if (StringUtil.isNotEmpty(msg)) {
                        msg += "OR";
                    }
                    msg += "证券账户";
                }
                if (StringUtil.isNotEmpty(msg)) {
                    msg = "【" + msg + "】不同";
                    errorList.add(new ValidMessage(msg));
                }
            }
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 初始化 申购页面
     *
     * @param request 指令信息
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/4/19 19:32
     */
    public CfetsTrdBondFirstSubInfoDto queryEntrustInfoById(CfetsTrdBondFirstSubInitRequest request) {
        logger.debug("初始化申购页面,入参:" + JsonUtils.object2Json(request));
        CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto = new CfetsTrdBondFirstSubInfoDto();
        Map<String, Object> param = new HashMap<>();
        //1.查询申购产品列表(获取资产信息,及剩余指令额度)
        List<CfetsTrdBondFirstSubInfoDto> listTmp = cfetsTrdBondFirstSub002Mapper.selectSubProducts(param);
        // 数据为空或查询结果大于1 返回空值
        if (CollectionUtils.isEmpty(listTmp)
                || CollectionUtils.isEmpty(cfetsTrdBondFirstSubInfoDto.getProductDtos())
                || (CollectionUtils.isNotEmpty(listTmp) && listTmp.size() > 1)) {
            logger.debug("初始化申购页面,查询结果数:" + listTmp.size());
            return null;
        }
        cfetsTrdBondFirstSubInfoDto = listTmp.get(0);
        //2.汇总认购量 由各组合指令自动带入，默认为各组合指令的剩余指令额度，可修改，修改后不得高于各组合指令的剩余指令额度
        cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(BigDecimal.ZERO);
        for (CfetsTrdBondFirstSubProductDto product : cfetsTrdBondFirstSubInfoDto.getProductDtos()) {
            cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(ArithmeticUtil.doAdd(cfetsTrdBondFirstSubInfoDto.getSubscriptionAmount(), product.getSubscriptionAmount()));
        }
        // 3.计算 【应缴款金额】=发行价格*认购量*10000/100 【结算服务费】，根据银行间债券交易费用规则计算
        this.calAmtAndFee(cfetsTrdBondFirstSubInfoDto);
        logger.debug("初始化申购页面,结果:" + JsonUtils.object2Json(cfetsTrdBondFirstSubInfoDto));
        return cfetsTrdBondFirstSubInfoDto;
    }

    /**
     * TODO 汇总认购量 废弃?
     *
     * @param cfetsTrdBondFirstSubInfoDto 申购信息
     * @author jiangpeng
     * @date 2022/4/24 11:26
     */
    private void sumSubscriptionAmount(CfetsTrdBondFirstSubInfoDto cfetsTrdBondFirstSubInfoDto) {
        cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(BigDecimal.ZERO);
        for (CfetsTrdBondFirstSubProductDto product : cfetsTrdBondFirstSubInfoDto.getProductDtos()) {
            // 认购量不得大于各组合指令的剩余指令额度
            // 考虑到可能有的指令不是初次申购或者不是初始化调用的该方法,此时认购量的值可能大于剩余指令额度,所以先做值比较和赋值,再汇总
            if (BigDecimalUtil.ACompareToB(product.getSubscriptionAmount(), product.getAvaNum(), 6) > 0) {
                product.setSubscriptionAmount(product.getAvaNum());
            }
            cfetsTrdBondFirstSubInfoDto.setSubscriptionAmount(ArithmeticUtil.doAdd(cfetsTrdBondFirstSubInfoDto.getSubscriptionAmount(), product.getSubscriptionAmount()));
        }
    }

    /**
     * 计算 应缴款金额 结算服务费
     *
     * @param dto 入参
     * @author jiangpeng
     * @date 2022/4/22 16:25
     */
    public CfetsTrdBondFirstSubInfoDto calAmtAndFee(CfetsTrdBondFirstSubInfoDto dto) {
        // 认购量为空不处理
        if (!StringUtil.isNotEmpty(dto.getSubscriptionAmount())) {
            return dto;
        }
        // 【应缴款金额】=发行价格*认购量*10000/100
        dto.setPayableAmount(ArithmeticUtil.divide(ArithmeticUtil.multiply(dto.getSubscriptionAmount(), dto.getIssuePrice()), 100, 6));
        // 【结算服务费】，根据银行间债券交易费用规则计算
        CalRequest calRequest = new CalRequest();
        calRequest.setFpriceAmt(dto.getPayableAmount());
        calRequest.setSettleDate(dto.getTradeDate());
        calRequest.setPs(DictCodeList.交易方向_买入.getValue());
        calRequest.setSecManageAcctId(dto.getProductDtos().get(0).getSecManageAcctId());
        calRequest.setShareAmt(dto.getSubscriptionAmount());
        List<TrdTradeFeeDetail> trdTradeFeeDetails = ibuQuoteBondCalService.calFee(calRequest);
        if (CollectionUtils.isNotEmpty(trdTradeFeeDetails)) {
            trdTradeFeeDetails.forEach(item -> {
                if (StringUtil.equals(item.getFeeType(), BussinessDictCodeList.费用类型_结算手续费.getValue())) {
                    dto.setSettFeeAmt(item.getFeeAmt());
                    return;
                }
            });
        }
        return dto;
    }

    /**
     * 自动分配
     *
     * @param autoRequest 画面元素
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    public List<CfetsTrdBondFirstSubProductDto> autoAllocation(CfetsTrdBondFirstSubInfoDto autoRequest) {
        logger.debug("自动分配,入参:" + JsonUtils.object2Json(autoRequest));
        BigDecimal subscriptionAmount = autoRequest.getSubscriptionAmount();
        // 剩余指令额度不为0的数据
        List<CfetsTrdBondFirstSubProductDto> list = new ArrayList<>();
        // 剩余指令额度为0的数据
        List<CfetsTrdBondFirstSubProductDto> listAumIsZero = new ArrayList<>();
        //当存在剩余指令额度为0的数据时，认购量为空，且整条数据置灰不可修改并放置列表最底部，此种数据不参与如认购量分配、应缴款金额分配、结算服务费分配等此类计算
        for (CfetsTrdBondFirstSubProductDto dto : autoRequest.getProductDtos()) {
            if (BigDecimalUtil.isZero(dto.getAvaNum()) || dto.getAvaNum() == null) {
                listAumIsZero.add(dto);
            } else {
                list.add(dto);
            }
        }
        // 降序
        list.sort(Comparator.comparing(CfetsTrdBondFirstSubProductDto::getAvaNum));
        // 1.按比例分配
        //按比例分配
        if (StringUtil.equals(autoRequest.getDipType(), "140")) {
            list = this.propDistribution(subscriptionAmount, list);
        }
        // 2.平均分配
        // 平均分配
        if (StringUtil.equals(autoRequest.getDipType(), "139")) {
            list = this.aveDistribution(subscriptionAmount, list);
        }
        // 3.分配应缴款金额和结算服务费
        this.otherDistribution(autoRequest.getSubscriptionAmount(), autoRequest.getPayableAmount(), autoRequest.getSettFeeAmt(), list);
        list.addAll(listAumIsZero);
        logger.debug("自动分配,结果:" + JsonUtils.object2Json(list));
        return list;
    }

    /**
     * 自动分配-按比例分配
     *
     * @param subAmt      认购量
     * @param productDtos 申购产品列表
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    private List<CfetsTrdBondFirstSubProductDto> propDistribution(BigDecimal subAmt, List<CfetsTrdBondFirstSubProductDto> productDtos) {
        // 自动计算的计算结果需要为最小变动单位1000的整数倍，且每条数据的认购量须小于等于剩余指令额度
        BigDecimal minChangeUnit = subMinChangeUnit;
        //指令剩余额度汇总
        BigDecimal avaNumTotal = productDtos.stream().map(CfetsTrdBondFirstSubProductDto::getAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 剩余尾差
        BigDecimal tailDifference = subAmt;
        int index = 0;
        for (CfetsTrdBondFirstSubProductDto item : productDtos) {
            index++;
            // 占比值 根据每条数据的“剩余指令额度/指令剩余额度汇总*基本信息中认购量”计算
            BigDecimal proportion = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getAvaNum(), subAmt), avaNumTotal, 4);
            // 记录剩余指令额度
            item.setAvaNumTemp(item.getAvaNum());
            // 最后一位时
            if (index == productDtos.size()) {
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) >= 0) {
                    // 剩余指令额度大于尾差且尾差小于占比值时 用倒减法求值
                    item.setSubscriptionAmount(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) >= 0
                        && BigDecimalUtil.ACompareToB(proportion, tailDifference, 6) < 0) {
                    // 剩余指令额度大于占比值且尾差大于占比值时
                    item.setSubscriptionAmount(proportion);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) < 0) {
                    // 剩余指令额度小于占比值且剩余指令额度小于尾差时
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            } else {// 每条数据的认购量须小于等于剩余指令额度
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), proportion, 6) >= 0) {
                    item.setSubscriptionAmount(proportion);
                } else {
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            }
            item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), item.getSubscriptionAmount()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getSubscriptionAmount());
        }
        // 处理尾差,最小变动单位序列分配算法
        this.handleTailDifferenceSubscriptionAmount(productDtos, tailDifference, minChangeUnit);
        return productDtos;
    }

    /**
     * 自动分配-平均分配
     *
     * @param subAmt      认购量
     * @param productDtos 申购产品列表
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/21 13:52
     */
    private List<CfetsTrdBondFirstSubProductDto> aveDistribution(BigDecimal subAmt, List<CfetsTrdBondFirstSubProductDto> productDtos) {
        // 自动计算的计算结果需要为最小变动单位1000的整数倍，且每条数据的认购量须小于等于剩余指令额度
        BigDecimal minChangeUnit = subMinChangeUnit;
        // 平均值计算
        BigDecimal avg = ArithmeticUtil.divide(subAmt, productDtos.size(), 4);
        // 剩余尾差
        BigDecimal tailDifference = subAmt;
        int index = 0;
        for (CfetsTrdBondFirstSubProductDto item : productDtos) {
            index++;
            // 记录剩余指令额度
            item.setAvaNumTemp(item.getAvaNum());
            // 最后一位时
            if (index == productDtos.size()) {
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) >= 0
                        && BigDecimalUtil.ACompareToB(avg, tailDifference, 6) >= 0) {
                    // 剩余指令额度大于尾差且尾差小于平均值时 用倒减法求值
                    item.setSubscriptionAmount(tailDifference);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) >= 0
                        && BigDecimalUtil.ACompareToB(avg, tailDifference, 6) < 0) {
                    // 剩余指令额度大于平均值且尾差大于平均值时
                    item.setSubscriptionAmount(avg);
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 6) < 0) {
                    // 剩余指令额度小于平均值且剩余指令额度小于尾差时
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            } else {// 每条数据的认购量须小于等于剩余指令额度
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), avg, 6) >= 0) {
                    item.setSubscriptionAmount(avg);
                } else {
                    item.setSubscriptionAmount(item.getAvaNumTemp());
                }
            }
            item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), item.getSubscriptionAmount()));
            tailDifference = ArithmeticUtil.subtract(tailDifference, item.getSubscriptionAmount());
        }
        // 处理尾插,最小变动单位序列分配算法
        this.handleTailDifferenceSubscriptionAmount(productDtos, tailDifference, minChangeUnit);

        return productDtos;
    }

    /**
     * 分配应缴款金额和结算服务费
     *
     * @param subAmt
     * @param payableAmount
     * @param settFeeAmt
     * @param productDtos
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/24 14:06
     */
    private List<CfetsTrdBondFirstSubProductDto> otherDistribution(BigDecimal subAmt, BigDecimal payableAmount, BigDecimal settFeeAmt,
                                                                   List<CfetsTrdBondFirstSubProductDto> productDtos) {
        if (subAmt == null) {
            subAmt = BigDecimal.ZERO;
        }
        if (payableAmount == null) {
            payableAmount = BigDecimal.ZERO;
        }
        if (settFeeAmt == null) {
            settFeeAmt = BigDecimal.ZERO;
        }
        // 平均值计算
        BigDecimal avg = ArithmeticUtil.divide(subAmt, new BigDecimal(productDtos.size()), 2);
        // 应缴款金额剩余尾差
        BigDecimal payTailDifference = payableAmount;
        // 费用剩余尾差
        BigDecimal feeTailDifference = settFeeAmt;
        int index = 0;
        for (CfetsTrdBondFirstSubProductDto item : productDtos) {
            BigDecimal payableAmountDetail = ArithmeticUtil.multiply(ArithmeticUtil.divide(item.getSubscriptionAmount(), subAmt, 2, RoundingMode.DOWN), payableAmount);
            BigDecimal feeAmountDetail = avg;
            // 最后一笔且尾差小于计算出的分配值时,用倒减值
            if (index == 0) {
                if (BigDecimalUtil.ACompareToB(payableAmountDetail, payTailDifference, 3) >= 0) {
                    payableAmountDetail = payTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(avg, feeTailDifference, 3) >= 0) {
                    feeAmountDetail = feeTailDifference;
                }
            }
            if (BigDecimalUtil.isZero(item.getSubscriptionAmount()) || item.getSubscriptionAmount() == null) {
                // 认购量为0时,不分配应缴款金额和结算服务费
                payableAmountDetail = BigDecimal.ZERO;
                feeAmountDetail = BigDecimal.ZERO;
            }
            // 应缴款金额
            // 根据申购产品列表中 认购量/基本信息中总的认购量*基本信息中应缴款金额，计算认购量占比时，根据最小变动单位位数，舍位计算；
            // 尾差适用最小变动单位序列分配算法继续分配；
            item.setPayableAmount(payableAmountDetail);
            // 结算服务费
            // 根据申购产品列表中数据条数平均分配结算服务费，尾差适用最小变动单位序列分配算法继续分配
            item.setSettFeeAmt(feeAmountDetail);
            payTailDifference = ArithmeticUtil.subtract(payTailDifference, item.getPayableAmount());
            feeTailDifference = ArithmeticUtil.subtract(feeTailDifference, item.getSettFeeAmt());
        }
        this.handleTailDifferenceOhter(productDtos, payTailDifference, feeTailDifference);
        return productDtos;
    }

    /**
     * 其他金额分配
     *
     * @param autoRequest 页面元素
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubProductDto>
     * @author jiangpeng
     * @date 2022/4/24 16:34
     */
    public List<CfetsTrdBondFirstSubProductDto> otherAllocation(CfetsTrdBondFirstSubInfoDto autoRequest) {

        logger.debug("自动分配,入参:" + JsonUtils.object2Json(autoRequest));
        BigDecimal subscriptionAmount = autoRequest.getSubscriptionAmount();
        // 剩余指令额度不为0的数据
        List<CfetsTrdBondFirstSubProductDto> list = new ArrayList<>();
        // 剩余指令额度为0的数据
        List<CfetsTrdBondFirstSubProductDto> listAumIsZero = new ArrayList<>();
        //当存在剩余指令额度为0的数据时，认购量为空，且整条数据置灰不可修改并放置列表最底部，此种数据不参与如认购量分配、应缴款金额分配、结算服务费分配等此类计算
        for (CfetsTrdBondFirstSubProductDto dto : autoRequest.getProductDtos()) {
            if (BigDecimalUtil.isZero(dto.getAvaNum()) || dto.getAvaNum() == null) {
                listAumIsZero.add(dto);
            } else {
                list.add(dto);
            }
        }
        // 降序
        list.sort(Comparator.comparing(CfetsTrdBondFirstSubProductDto::getAvaNum));
        // 分配应缴款金额和结算服务费
        this.otherDistribution(autoRequest.getSubscriptionAmount(), autoRequest.getPayableAmount(), autoRequest.getSettFeeAmt(), list);
        list.addAll(listAumIsZero);
        logger.debug("自动分配,结果:" + JsonUtils.object2Json(list));
        return list;
    }

    /**
     * 重新计算 应缴款金额 结算服务费 并 重新分配产品列表中 认购量 应缴款金额 结算服务费
     *
     * @param autoRequest 页面元素
     * @return com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto
     * @author jiangpeng
     * @date 2022/4/24 17:16
     */
    public CfetsTrdBondFirstSubInfoDto calData(CfetsTrdBondFirstSubInfoDto autoRequest) {
        // 重新计算 应缴款金额 结算服务费
        CfetsTrdBondFirstSubInfoDto result = this.calAmtAndFee(autoRequest);
        // 重新分配 认购量 应缴款金额 结算服务费
        result.setProductDtos(this.autoAllocation(autoRequest));
        return result;
    }

    /**
     * 风险试算check
     *
     * @param entity 入参 画面元素
     * @return com.joyintech.framework.ajax.AjaxResult
     * @author jiangpeng
     * @date 2022/4/18 15:00
     */
    public AjaxResult riskCheckNotEmpty(RiskCalRequest entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entity.getPtlAllocations())) {
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 构造生成交易编号的参数
     *
     * @param ps 交易方向
     * @return com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter
     * @author jiangpeng
     * @date 2022/4/21 14:29
     */
    public static BussinessCodeParameter getBussinessCodeParameter(String ps) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
//        if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue(), ps)) {
//            codeParameter.setCommonBusinessType(com.joyintech.tams.constant.DictCodeList.共通业务类型_债券买入);
//        } else {
//            codeParameter.setCommonBusinessType(com.joyintech.tams.constant.DictCodeList.共通业务类型_债券卖出);
//        }
        //TODO 处理逻辑
        return codeParameter;
    }

    /**
     * 构建汇总费用实体
     *
     * @param bussinessId
     * @param feeType
     * @param amount
     * @return com.joyintech.tams.common.base.entity.RegTradeFeeTotal
     * @author jiangpeng
     * @date 2022/4/18 15:18
     */
    public RegTradeFeeTotal prepareRegTradeFeeTotal(String bussinessId, String feeType, BigDecimal amount) {
        RegTradeFeeTotal regTradeFeeTotal = new RegTradeFeeTotal();
        regTradeFeeTotal.setTradeId(bussinessId);
        regTradeFeeTotal.setFeeType(feeType);
        regTradeFeeTotal.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        regTradeFeeTotal.setFeeAmt(amount);
        return regTradeFeeTotal;
    }

    /**
     * 构建明细费用实体
     *
     * @param bussinessId
     * @param feeType
     * @param amount
     * @return com.joyintech.tams.common.base.entity.RegTradeFee
     * @author jiangpeng
     * @date 2022/4/18 15:18
     */
    public RegTradeFee prepareRegTradeFee(String bussinessId, String feeType, BigDecimal amount) {
        RegTradeFee regTradeFee = new RegTradeFee();
        regTradeFee.setTradeId(bussinessId);
        regTradeFee.setFeeType(feeType);
        regTradeFee.setIsPayWithTrade(DictCodeList.是否_否.getValue());
        regTradeFee.setFeeAmt(amount);
        return regTradeFee;
    }

    /**
     * 风险试算实体组装
     *
     * @param riskCalRequest
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/4/21 14:28
     */
    public Map<String, Object> doRiskcalPackage(RiskCalRequest riskCalRequest) {
        Map<String, Object> result = new HashMap<>();
        List<Map> bussTrdTransfenpeiDto = new ArrayList<>();
        for (PtlAllocationDto ptlAllocationDto : riskCalRequest.getPtlAllocations()) {
            Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
            stringObjectMap.put("tradeAmount", ptlAllocationDto.getShareAmt());
            stringObjectMap.put("fpriceAmt", ptlAllocationDto.getShareAmt());
            stringObjectMap.put("portfolioCode", ptlAllocationDto.getPortfolioId());
            bussTrdTransfenpeiDto.add(stringObjectMap);
        }
        result.put("bussTrdTransfenpeiDto", bussTrdTransfenpeiDto);
        result.put("finprodId", riskCalRequest.getFinprodId());
        result.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        result.put("ps", riskCalRequest.getPs());
        result.put("ccy", "CNY");
        return result;
    }

    /**
     * 处理生效逻辑
     *
     * @param dto
     * @param operationType
     * @author jiangpeng
     * @date 2022/4/24 11:42
     */
    @Override
    public void doComplete(CfetsTrdBondFirstSubInfoDto dto, OperationType operationType) {

    }

    /**
     * 组装输入参数
     *
     * @param param 参数map集
     * @author jiangpeng
     * @date 2022/4/19 14:21
     */
    private void setParam(Map<String, Object> param) {
        param.put("ISSUE_CLASS_01", DictCodeListExt4.新债_发行方式_报价发行.getValue());
        param.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
        param.put("YN_N", DictCodeListExt1.是否_否.getValue());
    }

    /**
     * 分页设置默认值
     *
     * @param pageMode 页面信息
     * @author jiangpeng
     * @date 2022/4/19 14:22
     */
    private PageMode setPageMode(PageMode pageMode) {
        if (pageMode == null) {
            pageMode = new PageMode();
        }
        int pageNo = StringUtil.isNotEmpty(pageMode.getPageNo()) ? pageMode.getPageNo() : 1;//默认第一页
        int pageSize = StringUtil.isNotEmpty(pageMode.getPageSize()) ? pageMode.getPageSize() : 10;//默认每页10条
        pageMode.setPageNo(pageNo);
        pageMode.setPageSize(pageSize);
        return pageMode;
    }

    /**
     * 认购量尾差处理,最小变动单位序列分配算法
     * 求取平均数时是取余,平均分配的尾差必定不小于0
     *
     * @param productDtos    产品列表
     * @param tailDifference 尾差
     * @param minChangeUnit  最小变动单位
     * @author jiangpeng
     * @date 2022/4/24 9:49
     */
    private void handleTailDifferenceSubscriptionAmount(List<CfetsTrdBondFirstSubProductDto> productDtos, BigDecimal tailDifference, BigDecimal minChangeUnit) {
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0) {
            for (CfetsTrdBondFirstSubProductDto item : productDtos) {
                // 分配值
                BigDecimal assign = BigDecimal.ZERO;
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(tailDifference)) {
                    break;
                }
                //指令剩余额度为0时,处理下一条数据
                if (BigDecimalUtil.isZero(item.getAvaNumTemp())) {
                    continue;
                }
                // 处理尾差值的不同
                if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) >= 0) {
                    // 剩余指令额度大于等于最小分配单位 尾差大于等于最小变动单位 时,分配最小变动单位
                    assign = minChangeUnit;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度大于最小分配单位 尾差小于最小变动单位 时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) >= 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位但不小于尾差时,直接分配尾差的值
                    assign = tailDifference;
                } else if (BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), minChangeUnit, 4) < 0
                        && BigDecimalUtil.ACompareToB(item.getAvaNumTemp(), tailDifference, 4) < 0
                        && BigDecimalUtil.ACompareToB(tailDifference, minChangeUnit, 4) < 0) {
                    // 剩余指令额度小于最小分配单位且小于尾差时,直接分配剩余指令额度
                    assign = item.getAvaNumTemp();
                }
                // 认购量加上分配值
                item.setSubscriptionAmount(ArithmeticUtil.doAdd(item.getSubscriptionAmount(), assign));
                // 尾差减去分配值
                tailDifference = ArithmeticUtil.subtract(tailDifference, assign);
                // 剩余指令额度减去分配值
                item.setAvaNumTemp(ArithmeticUtil.subtract(item.getAvaNumTemp(), assign));
            }
        }
    }

    /**
     * 应缴款金额和结算服务费尾差处理,最小变动单位序列分配算法
     *
     * @param productDtos       产品列表
     * @param payTailDifference 应缴款金额尾差
     * @param feeTailDifference 结算服务费尾差
     * @author jiangpeng
     * @date 2022/4/24 9:49
     */
    private void handleTailDifferenceOhter(List<CfetsTrdBondFirstSubProductDto> productDtos, BigDecimal payTailDifference, BigDecimal feeTailDifference) {
        while (payTailDifference.compareTo(BigDecimal.ZERO) > 0 || feeTailDifference.compareTo(BigDecimal.ZERO) > 0) {
            for (CfetsTrdBondFirstSubProductDto item : productDtos) {
                // 尾差等于0,循环结束
                if (BigDecimalUtil.isZero(payTailDifference) && BigDecimalUtil.isZero(feeTailDifference)) {
                    break;
                }
                // 认购量为0时,不分配应缴款金额和结算服务费
                if (BigDecimalUtil.isZero(item.getSubscriptionAmount()) || item.getSubscriptionAmount() == null) {
                    continue;
                }
                // 应缴款金额分配值
                BigDecimal payAssign = BigDecimal.ZERO;
                // 费用分配值
                BigDecimal feeAssign = BigDecimal.ZERO;
                if (BigDecimalUtil.ACompareToB(payTailDifference, payMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    payAssign = payMinChangeUnit;
                } else {
                    payAssign = payTailDifference;
                }
                if (BigDecimalUtil.ACompareToB(feeTailDifference, feeMinChangeUnit, 4) >= 0) {
                    // 尾差值大于等于最小分配单位
                    feeAssign = payMinChangeUnit;
                } else {
                    feeAssign = feeTailDifference;
                }
                // 认购量加上分配值
                item.setPayableAmount(ArithmeticUtil.doAdd(item.getPayableAmount(), payAssign));
                // 认购量加上分配值
                item.setSettFeeAmt(ArithmeticUtil.doAdd(item.getSettFeeAmt(), feeAssign));
                // 尾差减去分配值
                payTailDifference = ArithmeticUtil.subtract(payTailDifference, payAssign);
                // 尾差减去分配值
                feeTailDifference = ArithmeticUtil.subtract(feeTailDifference, feeAssign);
            }
        }
    }
}

