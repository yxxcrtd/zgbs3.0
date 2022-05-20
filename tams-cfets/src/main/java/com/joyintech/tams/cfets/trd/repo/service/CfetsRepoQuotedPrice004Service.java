/*
 * 文件名：CfetsBondDealQuotedPrice002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.repo.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.trade.buyoutrepo.service.TrdOutRightRepoTradeService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondRegCheckRequst;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondService;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepo004Mapper;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepoMapper;
import com.joyintech.tams.cfets.trd.repo.dto.*;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPosPortfolioCashRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPosPortfolioSecRequest;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoPositionRequest;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.*;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.BussRepoSecService;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.trade.lend.dto.FinTradeProductDto;
import com.joyintech.tams.invest.trade.pretrdrepotrade.dto.PreTrdRepoTrade002Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 质押回购报价(登记)
 */
@Service("vueCfetsRepoQuotedPrice004Service")
public class CfetsRepoQuotedPrice004Service extends BaseService<CfetsRepoDealRegisterQuoteDto> implements BaseProcessService<CfetsRepoDealRegisterQuoteDto> {

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired
    private CfetsIbuQuoteRepoMapper cfetsIbuQuoteRepoMapper;

    @Autowired
    private CfetsIbuQuoteRepo004Mapper cfetsIbuQuoteRepo004Mapper;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;

    @Autowired
    private RegFinProductDealTotalMapper regFinProductDealTotalMapper;

    @Autowired
    private RegFinProductDealTotalAddMapper regFinProductDealTotalAddMapper;

    @Autowired
    private RegTradeFeeTotalMapper regTradeFeeTotalMapper;

    @Autowired
    private RegFinProductDealMapper regFinProductDealMapper;

    @Autowired
    private RegFinProductDealAddMapper regFinProductDealAddMapper;

    @Autowired
    private RegTradeFeeMapper regTradeFeeMapper;

    @Autowired
    private WflRegFinProductDealTotalMapper wflRegFinProductDealTotalMapper;

    @Autowired
    private WflRegFinProDealTotalAddMapper wflRegFinProDealTotalAddMapper;

    @Autowired
    private WflRegTradeFeeTotalMapper wflRegTradeFeeTotalMapper;

    @Autowired
    private WflRegFinProductDealMapper wflRegFinProductDealMapper;

    @Autowired
    private WflRegFinProductDealAddMapper wflRegFinProductDealAddMapper;

    @Autowired
    private WflRegTradeFeeMapper wflRegTradeFeeMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private WflRegFinPledgeMapper wflRegFinPledgeMapper;

    @Autowired
    private WflRegFinTotalPledgeMapper wflRegFinTotalPledgeMapper;

    @Autowired
    private RegFinPledgeMapper regFinPledgeMapper;

    @Autowired
    private RegFinTotalPledgeMapper regFinTotalPledgeMapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    @Autowired
    private PtlCashPositionFlowMapper ptlCashPositionFlowMapper;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoServiceImpl;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    @Autowired
    private PreTrdCommonBaseService preTrdCommonBaseService;

    @Autowired
    private TrdOutRightRepoTradeService trdOutRightRepoTradeService;

    @Autowired
    private BussRepoSecService bussRepoSecService;

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;

    /**
     * 组合可用现金和实际现金查询更新组件
     */
    @Autowired
    private BasePtlCashPositionInfoService basePtlCashPositionInfoServiceImpl;
    /**
     * 利率保留位数
     */
    private static final int yieldScale = 4;
    /**
     * 质押券递增维度
     */
    private final static String pledgeDimension = "0.1";

    /**
     * 提交保存check 必填项
     *
     * @param entity 提交数据
     * @return 校验返回信息
     */
    public AjaxResult checkSave(CfetsRepoDealQuoteCommonDto entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //模式
        String ps = entity.getPs();
        this.checkEntityEmpty(errorList, entity);
        // 记录指令代码
        List<String> entrustIdList = new ArrayList<>();
        entity.getPtlAllocations().forEach(item ->
                // 记录指令代码
                entrustIdList.add(item.getEntrustId()));
        String partyId = null;
        /*************************************************************组合分配********************************************/
        String repo_pledge_model = CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL);
        if (CollectionUtils.isEmpty(entity.getPtlAllocations())) {
            errorList.add(new ValidMessage("组合分配不可为空"));
        } else {
            // 交易金额汇总
            BigDecimal tradeAmtTotal = BigDecimal.ZERO;
            // 到期结算金额汇总
            BigDecimal mtradeAmtTotal = BigDecimal.ZERO;
            // 回购利息汇总
            BigDecimal mIntAmtTotal = BigDecimal.ZERO;
            // 交易手续费汇总
            BigDecimal dealFeeAmtTotal = BigDecimal.ZERO;
            // 结算手续费汇总
            BigDecimal settFeeamtTotal = BigDecimal.ZERO;
            // 所有限价指令的回购利率中的最小值或最大值
            BigDecimal minOrMaxYield = null;
            // 指定价格的回购利率
            BigDecimal yieldPRM01 = null;
            // 回购利率是否相等
            boolean yieldFlag = true;
            for (RepoPtlAllocationDto ptlAllocatio : entity.getPtlAllocations()) {
                List<ValidMessage> validMessageList = this.checkPtlEmpty(ptlAllocatio, ps);
                if (validMessageList != null && !validMessageList.isEmpty()) {
                    errorList.addAll(validMessageList);
//                    break;
                }
                if (this.ACompareToB(ptlAllocatio.getTradeAmt(), ptlAllocatio.getConvertAmt(), 6) > 0
                        && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
                    errorList.add(new ValidMessage("组合分配中交易金额要小于等于质押券折算总额"));
                }
                // 所有限价指令的回购利率中的最小值 初始化
                if (minOrMaxYield == null && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                    minOrMaxYield = ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                // 交易方式为正回购时,查找限价指令的回购利率中最小值
                if (minOrMaxYield != null && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())
                        && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                    minOrMaxYield = minOrMaxYield.compareTo(ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP)) <= 0
                            ? minOrMaxYield : ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                // 易方式为逆回购时,查找限价指令的回购利率中最大值
                else if (minOrMaxYield != null && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())
                        && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue())) {
                    minOrMaxYield = minOrMaxYield.compareTo(ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP)) >= 0
                            ? minOrMaxYield : ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                // 指定价格回购利率 初始化
                if (yieldPRM01 == null && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())) {
                    yieldPRM01 = ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP);
                }
                if (yieldPRM01 != null && StringUtil.equals(ptlAllocatio.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())
                        && yieldPRM01.compareTo(ArithmeticUtil.round(ptlAllocatio.getPrice(), yieldScale, RoundingMode.HALF_UP)) != 0) {
                    yieldFlag = false;
                }

                tradeAmtTotal = ArithmeticUtil.doAdd(tradeAmtTotal, ptlAllocatio.getTradeAmt());
                mtradeAmtTotal = ArithmeticUtil.doAdd(mtradeAmtTotal, ptlAllocatio.getMtradeAmt());
                mIntAmtTotal = ArithmeticUtil.doAdd(mIntAmtTotal, ptlAllocatio.getmIntAmt());
                dealFeeAmtTotal = ArithmeticUtil.doAdd(dealFeeAmtTotal, ptlAllocatio.getDealFeeAmt());
                settFeeamtTotal = ArithmeticUtil.doAdd(settFeeamtTotal, ptlAllocatio.getSettFeeAmt());
                //组合分配中交易金额要小于等于（剩余指令额度-已成功发出的未收到外汇交易中心成功反馈报价和流程中的报价-流程中交易登记额度）
                // 已实现: 剩余指令额度 = 指令额度-生效的交易登记额度-流程中交易登记额度-已成功发出的未收到外汇交易中心成功反馈报价和流程中的报价
                CfetsRepoDealEntrustOrderDto dto = cfetsIbuQuoteRepoMapper.findEtrLastNumByEtrId(ptlAllocatio.getEntrustId(), entity.getTotleTradeId());
                if (ACompareToB(ptlAllocatio.getTradeAmt(), ptlAllocatio.getAvaNum(), 12) > 0) {
                    errorList.add(new ValidMessage("组合分配中交易金额要小于等于[" + ptlAllocatio.getEntrustId() + "]剩余的指令额度"));
                    break;
                }
                if (ACompareToB(ptlAllocatio.getTradeAmt(), dto.getAvaNum(), 12) > 0) {
                    errorList.add(new ValidMessage("组合分配中交易金额要小于等于[" + ptlAllocatio.getEntrustId() + "]剩余的指令额度(存在流程中数据)"));
                    break;
                }
                if (StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "02")) {
                    continue;
                }
                if (StringUtil.isEmpty(partyId)) {
                    partyId = dto.getPartyId();
                }
                if (StringUtil.isNotEmpty(partyId) && !StringUtil.equals(partyId, dto.getPartyId())) {
                    errorList.add(new ValidMessage("所选指令交易账户不同"));
                    break;
                }
            }

            // 交易金额   到期结算金额  回购利息 交易手续费 结算手续费 总和需要相等
            if (StringUtil.isNotEmpty(entity.getTradeAmt())
                    && ACompareToB(entity.getTradeAmt(), tradeAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配交易金额总和需要等于交易金额"));
            }
            if (StringUtil.isNotEmpty(entity.getMtradeAmt())
                    && ACompareToB(entity.getMtradeAmt(), mtradeAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配到期结算金额总和需要等于到期结算金额"));
            }
            if (StringUtil.isNotEmpty(entity.getmIntAmt())
                    && ACompareToB(entity.getmIntAmt(), mIntAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配回购利息总和需要等于回购利息"));
            }
            if (StringUtil.isNotEmpty(entity.getDealFeeAmt())
                    && ACompareToB(entity.getDealFeeAmt(), dealFeeAmtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配交易手续费总和需要等于交易手续费"));
            }
            if (StringUtil.isNotEmpty(entity.getSettFeeAmt())
                    && ACompareToB(entity.getSettFeeAmt(), settFeeamtTotal, 12) != 0) {
                errorList.add(new ValidMessage("组合分配结算服务费总和需要等于结算服务费"));
            }
            //勾选指令的价格模式只存在指定价格时，回购利率默认等于指定价格指令中的回购利率，可修改，修
            // 改后与默认值不一致时，提交时，弹出提示“ 价格模式为指定价格，交易需要按照指令指定的回购利率进行交易！”
            if (StringUtil.isNotEmpty(yieldPRM01) && !StringUtil.isNotEmpty(minOrMaxYield)
                    && ACompareToB(yieldPRM01, entity.getPrice(), yieldScale) != 0) {
                errorList.add(new ValidMessage("价格模式为指定价格，交易需要按照指令指定的回购利率进行交易！"));
            }
            //交易方向为正回购
            //勾选指令的价格模式只存在限价指令时，回购利率默认为空，
            // 填入值后需不大于限价指令中回购利率的最小值，不满足时提交弹出提示“价格模式为限价指令，交易回购利率不能大于指令指定的回购利率进行交易”
            if (!StringUtil.isNotEmpty(yieldPRM01) && StringUtil.isNotEmpty(minOrMaxYield)
                    && ACompareToB(minOrMaxYield, entity.getPrice(), yieldScale) < 0 && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
                errorList.add(new ValidMessage("价格模式为限价指令，交易回购利率不能大于指令指定的回购利率进行交易！"));
            }
            //交易方向为逆回购
            //勾选指令的价格模式只存在限价指令时，回购利率默认为空，填入值后需不小于限价指令中回购利率的最大值，
            // 不满足时提交弹出提示“价格模式为限价指令，交易回购利率不能小于指令指定的回购利率进行交易”
            if (!StringUtil.isNotEmpty(yieldPRM01) && StringUtil.isNotEmpty(minOrMaxYield)
                    && ACompareToB(minOrMaxYield, entity.getPrice(), yieldScale) > 0 && StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())) {
                errorList.add(new ValidMessage("价格模式为限价指令，交易回购利率不能小于指令指定的回购利率进行交易！"));
            }
            //勾选指令的价格模式同时存在指定价格和限价指令时，回购利率默认为空，填入值后需判断回购利率是否等于指定价格中的回购利率，
            // 不满足提交时，弹出提示“价格模式存在指定价格，交易需要按照指令指定的回购利率进行交易！”
            if (StringUtil.isNotEmpty(yieldPRM01) && StringUtil.isNotEmpty(minOrMaxYield)
                    && ACompareToB(yieldPRM01, entity.getPrice(), yieldScale) != 0) {
                errorList.add(new ValidMessage("价格模式存在指定价格，交易需要按照指令指定的回购利率进行交易！"));
            }
            //检查指令是否是已接收状态
            Map<String, Object> paramFail = new HashMap<>();
            paramFail.put("entrustIdList", entrustIdList);
            paramFail.put("entrustStatus", BussinessDictCodeList.委托状态_已接收.getValue());
            if (cfetsIbuQuoteBondEtrMapper.countFailEtr(paramFail) > 0) {
                errorList.add(new ValidMessage("指令非已接收状态，不可提交!"));
            }
        }
        /**************************************************************组合分配********************************************/
        /*************************************************************质押券信息*******************************************/
        if (CollectionUtils.isEmpty(entity.getRepoEntrustPledgeDtos())) {
            errorList.add(new ValidMessage("质押券不可为空"));
        }
        if (!CollectionUtils.isEmpty(entity.getRepoEntrustPledgeDtos())) {
            // 同个质押券的汇总质押券面总额必须为十万的整数倍，且不超过100亿
            Map<String, BigDecimal> secPledgePrinMap = new HashMap<>();
            for (RepoEntrustPledgeDto repoEntrustPledgeDto : entity.getRepoEntrustPledgeDtos()) {
                List<ValidMessage> validMessageList = checkPledgeEmpty(repoEntrustPledgeDto, ps);
                if (validMessageList != null && !validMessageList.isEmpty()) {
                    errorList.addAll(validMessageList);
                    break;
                }
                // 按质押券维度 汇总质押券面总额
                if (StringUtil.isNotEmpty(secPledgePrinMap.get(repoEntrustPledgeDto.getBondCode()))) {
                    secPledgePrinMap.put(repoEntrustPledgeDto.getBondCode(),
                            ArithmeticUtil.doAdd(secPledgePrinMap.get(repoEntrustPledgeDto.getBondCode()), repoEntrustPledgeDto.getPledgePrin()));
                } else {
                    secPledgePrinMap.put(repoEntrustPledgeDto.getBondCode(), repoEntrustPledgeDto.getPledgePrin());
                }
            }
            // 校验质押券总额(该笔交易,流程中及已生效交易)不能超过指令的质押券总额
            if (StringUtil.equals(repo_pledge_model, "02") && StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
                Map<String, Object> map = new HashMap<>();
                map.put("entrustIdList", entrustIdList);
                map.put("workflowId", entity.getWorkflowId());
                // 将质押券拆分到指令维度
                List<RegFinPledge> list = this.dealWithPledgeList(entity.getRepoEntrustPledgeDtos(), entrustIdList, null, null, null);
                List<RepoEntrustPledgeDto> pledgeDtos = cfetsIbuQuoteRepoMapper.findEtrPrinByEtrIds(map);
                // 校验质押券总额(该笔交易,流程中及已生效交易)不能超过指令的质押券总额
                for (RegFinPledge pledge : list) {
                    for (RepoEntrustPledgeDto pledgeDto : pledgeDtos) {
                        if (StringUtil.equals(pledge.getEntrustId(), pledgeDto.getEntrustId())
                                && StringUtil.equals(pledge.getPledgeFinprodId(), pledgeDto.getFinprodId())
                                && ACompareToB(pledge.getPledgeNumber(), pledgeDto.getAvaPledgePrin(), 12) > 0) {
                            errorList.add(new ValidMessage("质押券总额不能超过指令【" + pledge.getEntrustId() + "】指定的质押券总额"));
                        }
                    }
                }
            }
            // 同个质押券的汇总质押券面总额必须为十万的整数倍，且不超过100亿
            checkSecPledgePrin(secPledgePrinMap, errorList);
        }
        /*************************************************************质押券信息*******************************************/
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * entity 空值校验
     *
     * @param errorList
     * @author jiangpeng
     * @date 2021/12/17 10:47
     */
    public void checkEntityEmpty(List<ValidMessage> errorList, CfetsRepoDealQuoteCommonDto entity) {
        if (!StringUtil.isNotEmpty(entity.getQuoteStyle())) {
            errorList.add(new ValidMessage("请选择报价方式"));
        }
        /**************************************************************基本信息********************************************/
        if (!StringUtil.isNotEmpty(entity.getPs())) {
            errorList.add(new ValidMessage("请输入交易方向"));
        }
        if (!StringUtil.isNotEmpty(entity.getMySideTrader())) {
            errorList.add(new ValidMessage("请输入本方交易员"));
        }
        if (!StringUtil.isNotEmpty(entity.getCounterId())) {
            errorList.add(new ValidMessage("请输入对手方"));
        }
        // 交易对手未绑定21位码,手方交易员非必填
        CfetsSearchInfoRequest cfetsSearchInfoRequest = new CfetsSearchInfoRequest();
        cfetsSearchInfoRequest.setDealType("01");
        cfetsSearchInfoRequest.setInitValue(entity.getCounterId());
        List<SelectOptionDto> list = joyinbusinessCommonService.queryMySideOrCount(cfetsSearchInfoRequest);
        if (StringUtil.isNotEmpty(list.get(0).getObject())
                && StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "01")
                && !StringUtil.isNotEmpty(entity.getCounterTrader())
                && StringUtil.equals(entity.getQuoteStyle(), BussinessDictCodeList.报价方式_对话报价.getValue())) {
            errorList.add(new ValidMessage("请输入对手方交易员"));
        }
        if (!StringUtil.isNotEmpty(entity.getRepoMethod())) {
            errorList.add(new ValidMessage("请输入回购方式"));
        }
        if (!StringUtil.isNotEmpty(entity.getSettleSpreedCode())) {
            errorList.add(new ValidMessage("请输入清算速度"));
        }
        if (!StringUtil.isNotEmpty(entity.getCcy())) {
            errorList.add(new ValidMessage("请输入债券币种"));
        }
        if (!StringUtil.isNotEmpty(entity.getSettCcy())) {
            errorList.add(new ValidMessage("请输入结算币种"));
        }
        if (!StringUtil.isNotEmpty(entity.getRepoTerm())) {
            errorList.add(new ValidMessage("请输入回购期限"));
        }
        if (!StringUtil.isNotEmpty(entity.getPrice())
                || BigDecimal.ZERO.compareTo(entity.getPrice()) >= 0) {
            errorList.add(new ValidMessage("请输入回购利率"));
        }
        if (!StringUtil.isNotEmpty(entity.getTradeAmt())
                || BigDecimal.ZERO.compareTo(entity.getTradeAmt()) >= 0) {
            errorList.add(new ValidMessage("请输入交易金额"));
        }
        if (!StringUtil.isNotEmpty(entity.getShareAmt())
                || BigDecimal.ZERO.compareTo(entity.getShareAmt()) >= 0) {
            errorList.add(new ValidMessage("请输入券面总额合计"));
        }
        if (!StringUtil.isNotEmpty(entity.getConvertAmt())
                || BigDecimal.ZERO.compareTo(entity.getConvertAmt()) >= 0) {
            errorList.add(new ValidMessage("请输入质押券折算总额"));
        }
        if (StringUtil.isNotEmpty(entity.getTradeAmt()) && StringUtil.isNotEmpty(entity.getConvertAmt())
                && this.ACompareToB(entity.getTradeAmt(), entity.getConvertAmt(), 6) > 0) {
            errorList.add(new ValidMessage("交易金额需小于等于质押券折算总额"));
        }
        if (!StringUtil.isNotEmpty(entity.getDeliverytype())) {
            errorList.add(new ValidMessage("请输入首次结算方式"));
        }
        if (!StringUtil.isNotEmpty(entity.getmDeliveryType())) {
            errorList.add(new ValidMessage("请输入到期结算方式"));
        }
        if (!StringUtil.isNotEmpty(entity.getSettleType())) {
            errorList.add(new ValidMessage("请输入清算类型"));
        }
//        if (!StringUtil.isNotEmpty(entity.getDealFeeAmt())) {
//            errorList.add(new ValidMessage("请输入交易手续费"));
//        }
//        if (!StringUtil.isNotEmpty(entity.getSettFeeAmt())) {
//            errorList.add(new ValidMessage("请输入结算服务费"));
//        }
        /**************************************************************基本信息********************************************/
//        if (StringUtil.isNotEmpty(entity.getCounterId())
//                && !StringUtil.equals(entity.getQuoteStyle(), "05")) {//X-REPO 交易对手是CCP
//            String trader = this.findTraderByCounterId(entity.getCounterId());
//            if (!StringUtil.isNotEmpty(trader)) {
//                errorList.add(new ValidMessage("选择的对手方未与银行间机构关联，请先去关联"));
//            }
//        }
    }

    /**
     * 组合分配 空值校验
     *
     * @param ptlAllocatio
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2021/12/17 11:07
     */
    public List<ValidMessage> checkPtlEmpty(RepoPtlAllocationDto ptlAllocatio, String ps) {
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isNotEmpty(ptlAllocatio.getEntrustId())) {
            errorList.add(new ValidMessage("请输入组合分配指令代码"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getPortfolioName())) {
            errorList.add(new ValidMessage("请输入组合分配组合名称"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getAvaNum())) {
            errorList.add(new ValidMessage("请输入组合分配剩余指令额度"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getTradeAmt())) {
            errorList.add(new ValidMessage("请输入组合分配交易金额"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getConvertAmt())
                && StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购)) {
            errorList.add(new ValidMessage("请输入组合分配质押券折算总额"));
        }
//        if (!StringUtil.isNotEmpty(ptlAllocatio.getDealFeeAmt())) {
//            errorList.add(new ValidMessage("请输入组合分配交易手续费"));
//        }
//        if (!StringUtil.isNotEmpty(ptlAllocatio.getSettFeeAmt())) {
//            errorList.add(new ValidMessage("请输入组合分配结算服务费"));
//        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getmIntAmt())) {
            errorList.add(new ValidMessage("请输入组合分配回购利息"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getMtradeAmt())) {
            errorList.add(new ValidMessage("请输入组合分配到期结算金额"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getPriceModel())) {
            errorList.add(new ValidMessage("请输入组合分配价格模式"));
        }
        if (!StringUtil.isNotEmpty(ptlAllocatio.getPrice())) {
            errorList.add(new ValidMessage("请输入组合分配指令回购利率"));
        }
        return errorList;
    }

    /**
     * 质押券信息 空值校验
     *
     * @param repoEntrustPledgeDto
     * @param ps
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     * @author jiangpeng
     * @date 2021/12/17 11:48
     */
    public List<ValidMessage> checkPledgeEmpty(RepoEntrustPledgeDto repoEntrustPledgeDto, String ps) {
        List<ValidMessage> errorList = new ArrayList<>();
        if (StringUtil.isNotEmpty(repoEntrustPledgeDto.getPledgePrinStr())) {
            repoEntrustPledgeDto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(repoEntrustPledgeDto.getPledgePrinStr()), new BigDecimal(10000)));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getBondCode())) {
            errorList.add(new ValidMessage("请输入质押券信息债券代码"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getBondAbbrName())) {
            errorList.add(new ValidMessage("请输入质押券信息债券简称"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPledgePrin())) {
            errorList.add(new ValidMessage("请输入质押券信息质押券面总额"));
        }
        if (BigDecimal.ZERO.compareTo(repoEntrustPledgeDto.getPledgePrin()) >= 0) {
            errorList.add(new ValidMessage("质押券信息质押券面总额需大于零"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPledgeRatio())) {
            errorList.add(new ValidMessage("请输入质押券信息折算比例"));
        }
        if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getRepRegistOrg())) {
            errorList.add(new ValidMessage("请输入质押券信息登记托管机构"));
        }
        if (StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
            if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPortfolioId())) {
                errorList.add(new ValidMessage("请输入质押券信息组合代码"));
            }
            if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getPortfolioName())) {
                errorList.add(new ValidMessage("请输入质押券信息组合名称"));
            }
            if (!StringUtil.isNotEmpty(repoEntrustPledgeDto.getAvaPledgePrin())) {
                errorList.add(new ValidMessage("请输入质押券信息可质押券面总额"));
            }
        }
        return errorList;
    }

    /**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsRepoDealRegisterQuoteDto entity, OperationType type) {
        //履历表
        this.updateCfetsOprHis(entity.getTotleTradeId(), type);
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            entity.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
            CfetsRepoDealDbDto cfetsRepoDealDbDto = prepareEntity(entity, type);
            //登录业务流程
            insertWfl(cfetsRepoDealDbDto);
            //头寸
            sendPositionMethod(entity, cfetsRepoDealDbDto, true, RequestUtil.getParam("confirmOk"));
            this.oldPositionDeal(cfetsRepoDealDbDto, false);
        }
//        if (StringUtil.isNotEmpty(type)) {
//            throw new RuntimeException("cs by jp");
//        }
    }

    @Override
    public void doUpdate(CfetsRepoDealRegisterQuoteDto entity) {
        Map<String, Object> map = new HashMap<>();
        if (!StringUtil.equals("revoke", entity.getOperatorType()) && !StringUtil.equals("delete", entity.getOperatorType())) {
            // 先删除流程中数据
            this.deleteBussFlowData(entity.getWorkflowId());
            CfetsRepoDealDbDto cfetsRepoDealDbDto = this.prepareEntity(entity, StringUtil.equals(entity.getOperatorType(), "revoke") ? OperationType.REVOKE : OperationType.NEW);
            //登录业务流程
            insertWfl(cfetsRepoDealDbDto);
            String confirmOk = RequestUtil.getParam("confirmOk");
            ProcessMark newEntity;
            // 发头寸
            try {
                if (StringUtil.isNotEmpty(entity.getWorkflowId())) {
                    boolean isSubmit = true;
                    SysWflBusinessList sysWflBusinessList = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                    if (StringUtil.equals(sysWflBusinessList.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                        isSubmit = false;
                        entity.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
                        sendPositionMethod(entity, cfetsRepoDealDbDto, isSubmit, confirmOk);
                    } else {
                        entity.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
                        sendPositionMethod(entity, cfetsRepoDealDbDto, isSubmit, confirmOk);
                    }
                }
            } catch (PositionException e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                String code = (String) bm.get("code");
                String messageStr = JsonUtils.json2Object((String) bm.get("message").toString(), String.class);
                if (StringUtil.isEmpty(confirmOk)
                        && StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr, MessageType.确认消息);
                }
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr, MessageType.错误消息);
                }
                throw new PositionException(messageStr);
            }
            this.oldPositionDeal(cfetsRepoDealDbDto, false);
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsBondDealRegisterQuoteDto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(CfetsRepoDealRegisterQuoteDto entity, OperationType operationType) {
        CfetsRepoDealDbDto cfetsRepoDealDbDto = new CfetsRepoDealDbDto();
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            cfetsRepoDealDbDto = this.getCfetsRepoDealDbDto(entity);
        }
        if (cfetsRepoDealDbDto.getRegFinProductDealTotal() == null) {
            cfetsRepoDealDbDto = this.prepareEntity(entity, operationType);
        }
        //删除业务流程数据
        this.deleteBussFlowData(entity.getWorkflowId());
        //登录db
        this.insert(cfetsRepoDealDbDto, operationType);
        //头寸
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            PositionHttpRequest.dorevoke(entity.getTotleTradeId(), DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), entity.getPs());
            this.oldPositionDeal(cfetsRepoDealDbDto, true);
        }
        //处理指令执行进度
        this.insertEtrFinishStatus(cfetsRepoDealDbDto, operationType);
    }

    private CfetsRepoDealDbDto getCfetsRepoDealDbDto(CfetsRepoDealRegisterQuoteDto entity) {
        CfetsRepoDealDbDto result = new CfetsRepoDealDbDto();
        BeanUtil.copyPropertiesIgnoreNull(result, entity);
        result.setOperationType(entity.getOperatorType());
        WflRegFinProductDealTotal wfldealTotal = wflRegFinProductDealTotalMapper.selectByPrimaryKey(entity.getTotleTradeId());
        List<WflRegTradeFeeTotal> wflfeeTotals = cfetsIbuQuoteBondMapper.listWflRegTradeFeeTotal(entity.getTotleTradeId());
        List<WflRegFinProductDeal> wfldeals = cfetsIbuQuoteBondMapper.listWflRegFinProductDeal(entity.getTotleTradeId());
        List<WflRegFinProductDealAdd> wfldealAdds = cfetsIbuQuoteBondMapper.listWflRegFinProductDealAdd(entity.getTotleTradeId());
        List<WflRegTradeFee> wflfees = cfetsIbuQuoteBondMapper.listWflRegTradeFee(entity.getTotleTradeId());
        List<WflRegFinPledge> wflpledges = cfetsIbuQuoteBondMapper.listWflRegFinPledge(entity.getTotleTradeId());

        RegFinProductDealTotal dealTotal = new RegFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(dealTotal, wfldealTotal);
        List<RegTradeFeeTotal> feeTotals = new ArrayList<>();
        wflfeeTotals.forEach(wflfeeTotal -> {
            RegTradeFeeTotal feeTotal = new RegTradeFeeTotal();
            BeanUtil.copyPropertiesIgnoreNull(feeTotal, wflfeeTotal);
            feeTotals.add(feeTotal);
        });
        List<RegFinProductDeal> deals = new ArrayList<>();
        wfldeals.forEach(wfldeal -> {
            RegFinProductDeal deal = new RegFinProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, wfldeal);
            deals.add(deal);
        });
        List<RegFinProductDealAdd> dealAdds = new ArrayList<>();
        wfldealAdds.forEach(wfldealAdd -> {
            RegFinProductDealAdd dealAdd = new RegFinProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(dealAdd, wfldealAdd);
            dealAdds.add(dealAdd);
        });
        List<RegTradeFee> fees = new ArrayList<>();
        wflfees.forEach(wflfee -> {
            RegTradeFee fee = new RegTradeFee();
            BeanUtil.copyPropertiesIgnoreNull(fee, wflfee);
            fees.add(fee);
        });
        List<RegFinPledge> pledges = new ArrayList<>();
        wflpledges.forEach(wflpledge -> {
            RegFinPledge pledge = new RegFinPledge();
            BeanUtil.copyPropertiesIgnoreNull(pledge, wflpledge);
            pledges.add(pledge);
        });

        result.setRegFinProductDealTotal(dealTotal);
        result.setRegTradeFeeTotals(feeTotals);
        result.setRegFinProductDeals(deals);
        result.setRegFinProductDealAdds(dealAdds);
        result.setRegTradeFees(fees);
        result.setRegFinPledges(pledges);

        return result;
    }


    @Override
    public void doDelete(String workflowId) {
        ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
        CfetsRepoDealRegisterQuoteDto entity = getBussinessEntityByWorkflowId(workflowId);
        this.deleteBussFlowData(workflowId);
        if (StringUtil.isNotEmpty(processMark)
                && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
            PositionHttpRequest.doDeleteByWorkFlowId(entity.getWorkflowId(), DictCodeListExt1.子系统代码_交易.getValue(),
                    DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), entity.getPs());
        }
//        if(StringUtil.isNotEmpty(workflowId)){
//            throw new RuntimeException("jp");
//        }
    }

    @Override
    public void doBack(CfetsRepoDealRegisterQuoteDto entity) {
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            //插入旧头寸
            List<WflRegFinProductDeal> wfldeals = cfetsIbuQuoteBondMapper.listWflRegFinProductDeal(entity.getTotleTradeId());
            for (WflRegFinProductDeal deal : wfldeals) {
                AllTradeCommonUtil.deletePosition(null, deal.getTradeId());
            }
        }
    }

    /**
     * 处理指令完成度
     *
     * @param cfetsRepoDealDbDto 入库dto
     * @param operationType      操作类型
     */
    public void insertEtrFinishStatus(CfetsRepoDealDbDto cfetsRepoDealDbDto, OperationType operationType) {
        RegFinProductDealTotal regFinProductDealTotal = cfetsRepoDealDbDto.getRegFinProductDealTotal();
        List<RegFinProductDeal> regFinProductDeals = cfetsRepoDealDbDto.getRegFinProductDeals();
        if (CollectionUtils.isNotEmpty(regFinProductDeals)) {
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            for (RegFinProductDeal regFinProductDeal : regFinProductDeals) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                status.setEntrustId(regFinProductDeal.getEntrustId());
                status.setTradeId(regFinProductDealTotal.getTotleTradeId());
                status.setCstpTradeId(regFinProductDealTotal.getTotleTradeId());
                status.setFinishDate(regFinProductDealTotal.getTradeDate());
                status.setEntFinishAmt(regFinProductDeal.getTradeAmt());
                status.setTrader(regFinProductDealTotal.getOuTraderId());
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    baseEtrInvestInfoServiceImpl.cancelCompleteDelegate(status.getEntrustId(), regFinProductDealTotal.getpTradeId());
                }
                etrFinishStatusDtos.add(status);
            }
            if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
                baseEtrInvestInfoServiceImpl.completeDelegate(etrFinishStatusDtos);
            }
        }
    }

    /**
     * 组装实体
     *
     * @param entity        dto
     * @param operationType 操作类型
     * @return com.joyintech.tams.cfets.bankuplink.repo.dto.CfetsRepoDealDbDto
     * @author jiangpeng
     * @date 2021/12/29 11:06
     */
    public CfetsRepoDealDbDto prepareEntity(CfetsRepoDealRegisterQuoteDto entity, OperationType operationType) {
        CfetsRepoDealDbDto result = new CfetsRepoDealDbDto();
        result.setWorkflowId(entity.getWorkflowId());
        result.setOperationType(entity.getOperatorType());
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            result.setRegFinProductDealTotal(regFinProductDealTotalMapper.selectByPrimaryKey(entity.getTotleTradeId()));
            result.setRegFinProductDealTotalAdd(regFinProductDealTotalAddMapper.selectByPrimaryKey(entity.getTotleTradeId()));
            result.setRegTradeFeeTotals(cfetsIbuQuoteBondMapper.listRegTradeFeeTotal(entity.getTotleTradeId()));
            result.setRegFinProductDeals(cfetsIbuQuoteBondMapper.listRegFinProductDeal(entity.getTotleTradeId()));
            result.setRegFinProductDealAdds(cfetsIbuQuoteBondMapper.listRegFinProductDealAdd(entity.getTotleTradeId()));
            result.setRegTradeFees(cfetsIbuQuoteBondMapper.listRegTradeFee(entity.getTotleTradeId()));
            result.setRegFinPledges(cfetsIbuQuoteRepoMapper.listRegFinPledge(entity.getTotleTradeId()));

            //备注信息
            if (StringUtil.isNotEmpty(entity.getRemark())) {
                if (result.getRegFinProductDealTotal() != null) {
                    result.getRegFinProductDealTotal().setRemark(entity.getRemark());
                }
            }
        } else {
            entity.setOuPartyId(entity.getMySide());
            entity.setOuTraderId(entity.getMySideTrader());
            entity.setClearType(entity.getSettleType());
            entity.setRate(ArithmeticUtil.divide(entity.getPrice(), new BigDecimal(100), 6, RoundingMode.HALF_UP));
            // 证券账户
            String secAcctId = StringUtil.EMPTY;
            if (CollectionUtils.isNotEmpty(entity.getRepoEntrustPledgeDtos()) && CollectionUtils.isNotEmpty(entity.getPtlAllocations())) {
                Map<String, Object> map = new HashMap<>();
                map.put("portfolioId", entity.getPtlAllocations().get(0).getPortfolioId());
                map.put("finprodId", entity.getRepoEntrustPledgeDtos().get(0).getFinprodId());
                secAcctId = this.qryPortfolioSecAcctId(map);
            }
            entity.setSecManageAcctId(secAcctId);
            Long settleSpreed = 0L;
            if (StringUtil.equals(entity.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加0.getValue())) {
                settleSpreed = 0L;
            } else if (StringUtil.equals(entity.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加1.getValue())) {
                settleSpreed = 1L;
            } else if (StringUtil.equals(entity.getSettleSpreedCode(), BussinessDictCodeList.清算速度_T加2.getValue())) {
                settleSpreed = 2L;
            }
            //交易方向
            String ps = entity.getPs();
            //投管业务类型
            String tgBusiType = StringUtil.EMPTY;
            //投管业务类型
            String finprodType2 = StringUtil.EMPTY;
            if (StringUtil.equals(ps, DictCodeListExt3.交易方向_逆回购.getValue())) {
                tgBusiType = BussinessDictCodeList.平安业务类型_逆回购.getValue();
                finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
            }
            if (StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
                tgBusiType = BussinessDictCodeList.平安业务类型_正回购.getValue();
                finprodType2 = DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
            }
            // 金融产品交易汇总登记
            RegFinProductDealTotal regFinProductDealTotal = this.prepareDealTotalEntity(entity, settleSpreed, finprodType2, tgBusiType);
            result.setRegFinProductDealTotal(regFinProductDealTotal);
            // 汇总登记费用明细表
            List<RegTradeFeeTotal> regTradeFeeTotals = new ArrayList<>();
            if (null != entity.getDealFeeAmt()) {
                regTradeFeeTotals.add(ibuQuoteBondService.prepareRegTradeFeeTotal(entity.getTotleTradeId(), BussinessDictCodeList.费用类型_交易手续费.getValue(), entity.getDealFeeAmt()));
            }
            if (null != entity.getSettFeeAmt()) {
                //交易手续费
                regTradeFeeTotals.add(ibuQuoteBondService.prepareRegTradeFeeTotal(entity.getTotleTradeId(), BussinessDictCodeList.费用类型_结算手续费.getValue(), entity.getSettFeeAmt()));
            }
            result.setRegTradeFeeTotals(regTradeFeeTotals);
            // 交易登记明细
            List<RegFinProductDeal> regFinProductDeals = new ArrayList<>();
            List<RegTradeFee> regTradeFees = new ArrayList<>();
            List<String> entrustIdList = new ArrayList<>();
            List<FinTradeProductDto> finTradeProductDtos = new ArrayList<>();
            List<FinTradeProductAdd> finTradeProductAdds = new ArrayList<>();
            List<FinProductType> finProductTypes = new ArrayList<>();
            Map<String, String> entrustIdTradeIdMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(entity.getPtlAllocations())) {
                for (RepoPtlAllocationDto ptlAllocatio : entity.getPtlAllocations()) {
                    RegFinProductDeal regFinProductDeal = new RegFinProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(regFinProductDeal, regFinProductDealTotal);
                    BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                    if (baseGetBusinessId != null) {
                        if (StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_正回购.getValue())) {
                            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                        } else {
                            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                        }
                    }
                    // 金融产品交易登记
                    regFinProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));// 交易编号
                    regFinProductDeal.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());// 业务类型
                    regFinProductDeal.setEntrustId(ptlAllocatio.getEntrustId());// 指令代码
                    regFinProductDeal.setPortfolioId(ptlAllocatio.getPortfolioId());// 组合代码
                    regFinProductDeal.setFinprodType(finprodType2);// 投管类型
                    regFinProductDeal.setFinprodType2(finprodType2);// 投管类型
                    regFinProductDeal.setTradeAmt(ptlAllocatio.getTradeAmt());// 交易金额
                    regFinProductDeal.setmIntAmt(ptlAllocatio.getmIntAmt());// 回购利息
                    regFinProductDeal.setmTradeAmt(ptlAllocatio.getMtradeAmt());// 到期结算金额
                    regFinProductDeal.setDisFaceValue(ptlAllocatio.getConvertAmt());// 折算券面总额
                    regFinProductDeal.setTgBusiType(tgBusiType);// 业务类型
                    regFinProductDeal.setSettleSpeed(settleSpreed);// 清算速度
                    regFinProductDeal.setSettleDate(entity.getvSettleDate());//结算日
                    regFinProductDeal.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());//交易场所
                    regFinProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());//交易市场
                    regFinProductDeal.setSecManageAcctId(entity.getSecManageAcctId());//证券账户
                    regFinProductDeal.setFinprodId(regFinProductDeal.getTradeId());
                    regFinProductDeal.setShareAmt(ArithmeticUtil.divide(ptlAllocatio.getShareAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
                    regFinProductDeal.setFaceValue(ptlAllocatio.getShareAmt());
                    if (null != ptlAllocatio.getDealFeeAmt()) {//交易手续费
                        regTradeFees.add(ibuQuoteBondService.prepareRegTradeFee(
                                regFinProductDeal.getTradeId(), BussinessDictCodeList.费用类型_交易手续费.getValue(), ptlAllocatio.getDealFeeAmt()));
                    }
                    if (null != ptlAllocatio.getSettFeeAmt()) {//结算手续费
                        regTradeFees.add(ibuQuoteBondService.prepareRegTradeFee(
                                regFinProductDeal.getTradeId(), BussinessDictCodeList.费用类型_结算手续费.getValue(), ptlAllocatio.getSettFeeAmt()));
                    }
                    regFinProductDeals.add(regFinProductDeal);
                    // 记录指令代码
                    entrustIdList.add(ptlAllocatio.getEntrustId());
                    // 记录交易编号
                    entrustIdTradeIdMap.put(ptlAllocatio.getEntrustId(), regFinProductDeal.getTradeId());
//                    // 交易类金融产品
//                    FinTradeProductDto finTradeProduct = new FinTradeProductDto();
//                    // 交易类金融产品附属表
//                    FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
//                    this.prepareFinTradeProductAndAdd(entity, repRegistOrg, finTradeProduct, finTradeProductAdd, regFinProductDeal);
//                    finTradeProductDtos.add(finTradeProduct);
//                    finTradeProductAdds.add(finTradeProductAdd);
//                    // 金融产品分类信息
//                    FinProductType finProductType = this.prepareFinProductTypeEntity(regFinProductDeal);
//                    finProductTypes.add(finProductType);
                }
            }
//            result.setFinTradeProducts(finTradeProductDtos);
//            result.setFinTradeProductAdds(finTradeProductAdds);
            result.setRegFinProductDeals(regFinProductDeals);
//            result.setFinProductTypes(finProductTypes);
            result.setRegTradeFees(regTradeFees);
            // 质押品明细汇总
            List<RegFinTotalPledge> regFinTotalPledges = new ArrayList<>();
            Map<String, RegFinTotalPledge> regFinPledgeMap = new HashMap<>();
            // 质押品明细
            List<RegFinPledge> regFinPledges = new ArrayList<>();
            if (StringUtil.equals(ps, DictCodeListExt3.交易方向_正回购.getValue())) {
                regFinPledges = this.dealWithPledgeList(entity.getRepoEntrustPledgeDtos(), entrustIdList, entrustIdTradeIdMap, null, regFinPledgeMap);
            } else {
                regFinPledges.addAll(this.dealWithPledgeListH04(entity.getRepoEntrustPledgeDtos(), entity.getTradeAmt(),
                        entity.getPtlAllocations(), entrustIdTradeIdMap, regFinPledgeMap));
            }
            for (RegFinTotalPledge regFinTotalPledge : regFinPledgeMap.values()) {
                regFinTotalPledge.setTotleTradeId(entity.getTotleTradeId());
                regFinTotalPledges.add(regFinTotalPledge);
            }
            result.setRegFinTotalPledges(regFinTotalPledges);
            result.setRegFinPledges(regFinPledges);
        }
        return result;
    }

    /**
     * 组装交易汇总信息
     *
     * @param entity       页面数据
     * @param settleSpreed 清算速度
     * @param finprodType2 投管业务类型
     * @param tgBusiType   投管业务类型
     * @return com.joyintech.tams.common.base.entity.RegFinProductDealTotal
     * @author jiangpeng
     * @date 2021/12/29 10:32
     */
    public RegFinProductDealTotal prepareDealTotalEntity(CfetsRepoDealRegisterQuoteDto entity,
                                                         Long settleSpreed, String finprodType2, String tgBusiType) {
        RegFinProductDealTotal total = new RegFinProductDealTotal();

        BeanUtil.copyPropertiesIgnoreNull(total, entity);
//        total.setFinprodId(total.getTotleTradeId());//资产代码
        total.setSettleSpeed(settleSpreed);//清算速度
        total.setQuoteType(entity.getQuoteStyle());// 报价方式
        total.setDeliveryType(entity.getDeliverytype());// 首次结算方式
        total.setmTradeAmt(entity.getMtradeAmt());// 到期结算金额
        total.setFaceValue(entity.getShareAmt());//质押总券面
        total.setShareAmt(ArithmeticUtil.divide(entity.getShareAmt(), new BigDecimal(100), 2, RoundingMode.HALF_UP));//质押总券面
        total.setDipType(entity.getDistribution());//组合分配方式
        total.setDisFaceValue(entity.getConvertAmt());//质押券折算总额
        total.setRepoType(entity.getRepoMethod());//回购方式
        total.setvSettleDate(entity.getSettlementStartDate());//首期结算日
        total.setmSettleDate(entity.getSettlementEndDate());//到期结算日
        total.setActRepoTerm(entity.getActCapDays());//实际占款天数
        total.setClearType(entity.getSettleType());//清算类型
        total.setBranch(0L);
        total.setFinprodType2(finprodType2);
        total.setTgBusiType(tgBusiType);
        total.setSecManageAcctId(entity.getSecManageAcctId());
        total.setSettleDate(entity.getSettlementStartDate());
        total.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
        total.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        return total;
    }

    /**
     * 组装金融产品分类信息
     *
     * @param entity 页面数据
     * @return com.joyintech.tams.common.base.entity.FinProductType
     * @author jiangpeng
     * @date 2021/12/29 11:04
     */
    public FinProductType prepareFinProductTypeEntity(RegFinProductDeal deal) {


        // 金融产品分类Entity 构造开始----------------------------------------------------------
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, deal);
        finProductType.setFinprodId(deal.getTradeId());// 金融产品代码
        finProductType.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));// 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finProductType.setType6(deal.getRepoType());// 分类6
        finProductType.setType7(deal.getPs());// 分类7
        finProductType.setType8(DictCodeList.平安资产类型_质押式回购.getValue());// 分类8
        /*五级分类映射 */
        FinProductTypeConfig finProductTypeConfig = new FinProductTypeConfig();
        finProductTypeConfig.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
        finProductTypeConfig.setInputValue1(DictCodeList.平安资产类型_质押式回购.getValue());
        finProductTypeConfig.setInputValue2(deal.getRepoType());
        finProductTypeConfig.setInputValue3(deal.getPs());
        finProductTypeConfig = this.getAssetFiveType(finProductTypeConfig);
        finProductType.setType9(finProductTypeConfig.getOutputValue2());
        finProductType.setType10(finProductTypeConfig.getOutputValue3());
        finProductType.setType11(finProductTypeConfig.getOutputValue4());
        finProductType.setType12(finProductTypeConfig.getOutputValue5());
        /*五级分类映射 */
        finProductType.setWorkflowId(deal.getWorkflowId());// 工作流代码
        // 金融产品分类Entity 构造结束----------------------------------------------------------
        return finProductType;
    }

    /**
     * 组装 交易类金融产品信息和交易类金融产品附属表信息
     *
     * @param entity             页面数据
     * @param finprodType2       投管业务类型
     * @param tgBusiType         投管业务类型
     * @param repRegistOrg       托管机构
     * @param finTradeProduct    交易类金融产品
     * @param finTradeProductAdd 交易类金融产品附属表
     * @param dealTotal          汇总交易信息
     * @author jiangpeng
     * @date 2021/12/29 10:41
     */
    public void prepareFinTradeProductAndAdd(CfetsRepoDealRegisterQuoteDto entity, String repRegistOrg,
                                             FinTradeProductDto finTradeProduct, FinTradeProductAdd finTradeProductAdd, RegFinProductDeal deal) {
        BeanUtil.copyPropertiesIgnoreNull(finTradeProduct, deal);
        BeanUtil.copyPropertiesIgnoreNull(finTradeProductAdd, deal);
        // 交易类金融产品Entity 构造开始----------------------------------------------------------
        finTradeProduct.setFinprodId(deal.getTradeId());// 金融产品代码 = 金融产品交易Entity.交易编号
        finTradeProduct.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));// 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finTradeProduct.setPrin(deal.getTradeAmt());// 本金金额
        finTradeProduct.setCcy(deal.getCcy());// 币种
        finTradeProduct.setVdate(deal.getvSettleDate());// 起息日
        finTradeProduct.setMdate(deal.getmSettleDate());// 到期日
        finTradeProduct.setVpayDate(deal.getvSettleDate());//
        finTradeProduct.setMpayDate(deal.getmSettleDate());//
        finTradeProduct.setActCapDays(entity.getActCapDays());//实际占款天数
        finTradeProduct.setTerm(deal.getRepoTerm());// 期限(天)
        finTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());// 利率类型
        finTradeProduct.setIntRate(
                ArithmeticUtil.divide(deal.getRate(), BigDecimal.valueOf(100), 8));// 利率
        finTradeProduct.setBasis(DictCodeList.计息基础_A_365.getValue());// 计息基础
        finTradeProduct.setmPrinAmt(deal.getTradeAmt());// 到期本金
        finTradeProduct.setmIntAmt(deal.getmIntAmt());// 到期利息
        finTradeProduct.setmTradeAmt(deal.getmTradeAmt());// 到期金额
        finTradeProduct.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());// 交易场所
        finTradeProduct.setCalendarId("03");// 交易日历 银行间
        finTradeProduct.setTermType(deal.getTermType());// 期限品种

        // 交易类金融产品附属Entity 构造开始----------------------------------------------------------
        BeanUtil.copyPropertiesIgnoreNull(finTradeProductAdd, finTradeProduct);
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        finTradeProductAdd.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        //  金融产品类型 =  数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
        finTradeProductAdd.setFinprodType2(deal.getFinprodType2());
        // 登记托管机构
        finTradeProductAdd.setRegistOrg(repRegistOrg);
        // 交易类金融产品附属Entity 构造结束----------------------------------------------------------
    }

    /**
     * 资产五级分类联动检索
     *
     * @param param
     * @return com.joyintech.tams.common.base.entity.FinProductTypeConfig
     * @author jiangpeng
     * @date 2021/12/29 10:25
     */
    public FinProductTypeConfig getAssetFiveType(FinProductTypeConfig param) {
        // 配置类型未传参时，默认投管分类
        if (StringUtil.isEmpty(param.getConfigType())) {
            param.setConfigType(DictCodeListExt2.配置类型_投管分类.getValue());
        }
        List<FinProductTypeConfig> list = cfetsIbuQuoteRepoMapper.getAssetFiveType(param);
        if (StringUtil.isEmptyList(list) || list.size() > 1 || list.get(0) == null) {
            return new FinProductTypeConfig();
        }
        return list.get(0);
    }

    /**
     * 正回购 处理质押券信息 将质押券拆分到指令维度
     *
     * @param pledgeDtos          页面质押券信息
     * @param entrustIdList       指令代码列表
     * @param entrustIdTradeIdMap 指令,业务编号 Map 非入库数据可为空
     * @param etrPrin             指令指定质押券的质押总额汇总 可为空
     * @return java.util.List<com.joyintech.tams.common.base.entity.RegFinPledge>
     * @author jiangpeng
     * @date 2021/12/15 17:42
     */
    public List<RegFinPledge> dealWithPledgeList(List<RepoEntrustPledgeDto> pledgeDtos, List<String> entrustIdList,
                                                 Map<String, String> entrustIdTradeIdMap, Map<String, BigDecimal> etrPrin,
                                                 Map<String, RegFinTotalPledge> regFinPledgeMap) {
        if (CollectionUtils.isEmpty(pledgeDtos)
                || CollectionUtils.isEmpty(entrustIdList)) {
            return new ArrayList<>();
        }
        // 返回结果列表
        List<RegFinPledge> regFinPledges = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        setParam(param);
        param.put("entrustIdList", entrustIdList);
        // 指定质押券
        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL), "02")) {
            // 查询 指令指定质押券的质押券额
            List<RepoEntrustPledgeDto> repoEntrustPledgeDtos = cfetsIbuQuoteRepoMapper.selectRepoEntrustPledges(param);
            // 按 组合+债券维度汇总质押券额
            Map<String, BigDecimal> ptlSecPrin = new HashMap<>();
            for (RepoEntrustPledgeDto dto : repoEntrustPledgeDtos) {
                if (StringUtil.isNotEmpty(dto.getPledgePrinStr())) {
                    dto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(dto.getPledgePrinStr()), new BigDecimal(10000)));
                }
                if (ptlSecPrin.get(dto.getPortfolioId() + dto.getFinprodId()) == null) {
                    ptlSecPrin.put(dto.getPortfolioId() + dto.getFinprodId(), dto.getPledgePrin());
                } else {
                    BigDecimal pledgePrin = ptlSecPrin.get(dto.getPortfolioId() + dto.getFinprodId());
                    ptlSecPrin.put(dto.getPortfolioId() + dto.getFinprodId(), ArithmeticUtil.doAdd(pledgePrin, dto.getPledgePrin()));
                }
                if (etrPrin == null) {
                    continue;
                }
                if (etrPrin.get(dto.getEntrustId()) == null) {
                    etrPrin.put(dto.getEntrustId(), dto.getPledgePrin());
                } else {
                    BigDecimal pledgeEtrPrin = etrPrin.get(dto.getEntrustId());
                    etrPrin.put(dto.getEntrustId(), ArithmeticUtil.doAdd(pledgeEtrPrin, dto.getPledgePrin()));
                }
            }
            // 质押信息list转map
            Map<String, BigDecimal> pledgeMap = new HashMap<>();
            pledgeDtos.forEach(dto -> {
                pledgeMap.put(dto.getPortfolioId() + dto.getFinprodId(),
                        ArithmeticUtil.multiply(new BigDecimal(dto.getPledgePrinStr()), new BigDecimal(10000)));
                BigDecimal pledgeNumber = ArithmeticUtil.divide(dto.getPledgePrin(), new BigDecimal(100), 6);//转化为数量
                if (regFinPledgeMap != null) {
                    RegFinTotalPledge finTotalPledge = new RegFinTotalPledge();
                    if (StringUtil.isNotEmpty(regFinPledgeMap.get(dto.getFinprodId()))) {
                        finTotalPledge = regFinPledgeMap.get(dto.getFinprodId());
                    }
                    finTotalPledge.setPledgeFinprodId(dto.getFinprodId());//质押金融产品代码
                    finTotalPledge.setPledgeNumber(ArithmeticUtil.doAdd(pledgeNumber, finTotalPledge.getPledgeNumber()));//质押数量
                    finTotalPledge.setPledgeRatio(dto.getPledgeRatio());//质押率
                    regFinPledgeMap.put(finTotalPledge.getPledgeFinprodId(), finTotalPledge);
                }
            });
            // 按质押券面排序 倒序
            repoEntrustPledgeDtos = repoEntrustPledgeDtos.stream()
                    .sorted(Comparator.comparing(RepoEntrustPledgeDto::getPledgePrin).reversed()).collect(Collectors.toList());
            //递增维度
            BigDecimal addDimension = new BigDecimal(pledgeDimension);
            for (Map.Entry<String, BigDecimal> map : pledgeMap.entrySet()) {
                //尾差
                BigDecimal tailDifference = map.getValue();
                for (RepoEntrustPledgeDto dto : repoEntrustPledgeDtos) {
                    if (!StringUtil.equals(dto.getPortfolioId() + dto.getFinprodId(), map.getKey())) {
                        continue;
                    }
                    if (StringUtil.isNotEmpty(dto.getPledgePrinStr())) {
                        dto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(dto.getPledgePrinStr()), new BigDecimal(10000)));
                    }
                    RegFinPledge regFinPledge = new RegFinPledge();
                    BigDecimal pledgePrin = ptlSecPrin.get(dto.getPortfolioId() + dto.getFinprodId());//指令中质押额
                    //质押面值 = 指令中质押额/所有同组合,同债券指令的质押总额*质押信息中的质押额  按比例分配质押额
                    if (ACompareToB(addDimension, tailDifference, 12) < 0) {
                        pledgePrin = ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(dto.getPledgePrin(), map.getValue()), pledgePrin, 12, RoundingMode.HALF_UP);
                        pledgePrin = ArithmeticUtil.multiply(
                                ArithmeticUtil.divide(pledgePrin,
                                        ArithmeticUtil.multiply(addDimension, new BigDecimal(100)), 2),
                                ArithmeticUtil.multiply(addDimension, new BigDecimal(100)));
                    } else {
                        pledgePrin = tailDifference;
                    }
                    tailDifference = ArithmeticUtil.subtract(tailDifference, pledgePrin);
                    regFinPledge.setEntrustId(dto.getEntrustId());//指令代码
                    if (entrustIdTradeIdMap != null && !entrustIdTradeIdMap.isEmpty()) {
                        regFinPledge.setTradeId(entrustIdTradeIdMap.get(dto.getEntrustId()));//交易编号
                    }
                    regFinPledge.setPledgeFinprodId(dto.getFinprodId());//质押券id
                    regFinPledge.setPortfolioId(dto.getPortfolioId());//组合代码
//                regFinPledge.setPledgeFaceValue(pledgePrin);//质押面值
                    regFinPledge.setPledgeNumber(ArithmeticUtil.divide(pledgePrin, new BigDecimal(100), 12));//质押数量
                    regFinPledge.setPledgeRatio(ArithmeticUtil.divide(dto.getPledgeRatio(), new BigDecimal(100), yieldScale));//质押率
                    regFinPledges.add(regFinPledge);
                    if(tailDifference.compareTo(BigDecimal.ZERO) <= 0){
                        break;
                    }
                }
            }
        } else {
            List<CfetsRepoDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteRepoMapper.listEntrustAvaNum(param);
            // 按 组合+债券维度汇总质押券额
            Map<String, BigDecimal> ptlSecPrin = new HashMap<>();
            for (CfetsRepoDealEntrustOrderDto dto : entrustDtos) {
                if (ptlSecPrin.get(dto.getPortfolioId()) == null) {
                    ptlSecPrin.put(dto.getPortfolioId(), dto.getAvaNum());
                } else {
                    BigDecimal avaNum = ptlSecPrin.get(dto.getPortfolioId());
                    ptlSecPrin.put(dto.getPortfolioId(), ArithmeticUtil.doAdd(avaNum, dto.getAvaNum()));
                }
            }
            //递增维度
            BigDecimal addDimension = new BigDecimal(pledgeDimension);;
            for (RepoEntrustPledgeDto pledgeDto : pledgeDtos) {
                //尾差
                BigDecimal tailDifference = pledgeDto.getPledgePrin();
                for (CfetsRepoDealEntrustOrderDto dto : entrustDtos) {
                    RegFinPledge regFinPledge = new RegFinPledge();
                    if (!StringUtil.equals(dto.getPortfolioId(), pledgeDto.getPortfolioId())) {
                        continue;
                    }
                    if (StringUtil.isNotEmpty(pledgeDto.getPledgePrinStr())) {
                        pledgeDto.setPledgePrin(ArithmeticUtil.multiply(new BigDecimal(pledgeDto.getPledgePrinStr()), new BigDecimal(10000)));
                    }
                    BigDecimal pledgePrin = ptlSecPrin.get(pledgeDto.getPortfolioId());//指令中质押额
                    //质押面值 = 指令剩余额度/统一组合的总额度*质押信息中的质押额  按比例分配质押额
                    if (ACompareToB(addDimension, tailDifference, 12) < 0) {
                        pledgePrin = ArithmeticUtil.divide(
                                ArithmeticUtil.multiply(dto.getAvaNum(), pledgeDto.getPledgePrin()), pledgePrin, 12, RoundingMode.HALF_UP);
                        pledgePrin = ArithmeticUtil.multiply(
                                ArithmeticUtil.divide(pledgePrin,
                                        ArithmeticUtil.multiply(addDimension, new BigDecimal(100)), 2),
                                ArithmeticUtil.multiply(addDimension, new BigDecimal(100)));
                    }
                    tailDifference = ArithmeticUtil.subtract(tailDifference, pledgePrin);
                    regFinPledge.setEntrustId(dto.getEntrustId());//指令代码
                    if (entrustIdTradeIdMap != null && !entrustIdTradeIdMap.isEmpty()) {
                        regFinPledge.setTradeId(entrustIdTradeIdMap.get(dto.getEntrustId()));//交易编号
                    }
                    regFinPledge.setPledgeFinprodId(pledgeDto.getFinprodId());//质押券id
                    regFinPledge.setPortfolioId(pledgeDto.getPortfolioId());//组合代码
//                    regFinPledge.setPledgeFaceValue(pledgePrin);//质押面值
                    regFinPledge.setPledgeNumber(ArithmeticUtil.divide(pledgePrin, new BigDecimal(100), 12));//质押数量
                    regFinPledge.setPledgeRatio(ArithmeticUtil.divide(pledgeDto.getPledgeRatio(), new BigDecimal(100), yieldScale));//质押率
                    regFinPledges.add(regFinPledge);
                    if(tailDifference.compareTo(BigDecimal.ZERO) <= 0){
                        break;
                    }
                }
                if (regFinPledgeMap == null) {
                    continue;
                }
                BigDecimal pledgeNumber = ArithmeticUtil.divide(pledgeDto.getPledgePrin(), new BigDecimal(100), 6);//转化为数量
                RegFinTotalPledge finTotalPledge = new RegFinTotalPledge();
                if (StringUtil.isNotEmpty(regFinPledgeMap.get(pledgeDto.getFinprodId()))) {
                    finTotalPledge = regFinPledgeMap.get(pledgeDto.getFinprodId());
                }
                finTotalPledge.setPledgeFinprodId(pledgeDto.getFinprodId());//质押金融产品代码
                finTotalPledge.setPledgeNumber(ArithmeticUtil.doAdd(pledgeNumber, finTotalPledge.getPledgeNumber()));//质押数量
                finTotalPledge.setPledgeRatio(pledgeDto.getPledgeRatio());//质押率
                regFinPledgeMap.put(finTotalPledge.getPledgeFinprodId(), finTotalPledge);
            }

        }
        return regFinPledges;
    }


    /**
     * 逆回购 处理质押券信息 将质押券拆分到指令维度
     *
     * @param repoEntrustPledgeDtos
     * @param ptlAllocationDtos
     * @return java.util.List<com.joyintech.tams.common.base.entity.RegFinPledge>
     * @author jiangpeng
     * @date 2022/1/20 10:49
     */
    public List<RegFinPledge> dealWithPledgeListH04(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos, BigDecimal tradeAmt,
                                                    List<RepoPtlAllocationDto> ptlAllocationDtos, Map<String, String> entrustIdTradeIdMap,
                                                    Map<String, RegFinTotalPledge> regFinPledgeMap) {
        List<RegFinPledge> regFinPledges = new ArrayList<>();
        List<RepoPtlAllocationDto> ptls = ptlAllocationDtos.stream()
                .sorted(Comparator.comparing(RepoPtlAllocationDto::getTradeAmt).reversed()).collect(Collectors.toList());
        for (RepoEntrustPledgeDto repoEntrustPledgeDto : repoEntrustPledgeDtos) {
            List<RegFinPledge> regFinPledgeList = new ArrayList<>();
            BigDecimal pledgePrin = ArithmeticUtil.divide(repoEntrustPledgeDto.getPledgePrin(), new BigDecimal(10000), 12);//转化为万元
            BigDecimal pledgeNumber = ArithmeticUtil.divide(repoEntrustPledgeDto.getPledgePrin(), new BigDecimal(100), 6);//转化为数量
            BigDecimal tailDifference = pledgePrin;
            RegFinTotalPledge finTotalPledge = new RegFinTotalPledge();
            if (StringUtil.isNotEmpty(regFinPledgeMap.get(repoEntrustPledgeDto.getFinprodId()))) {
                finTotalPledge = regFinPledgeMap.get(repoEntrustPledgeDto.getFinprodId());
            }
            finTotalPledge.setPledgeFinprodId(repoEntrustPledgeDto.getFinprodId());//质押金融产品代码
            finTotalPledge.setPledgeNumber(ArithmeticUtil.doAdd(pledgeNumber, finTotalPledge.getPledgeNumber()));//质押数量
            finTotalPledge.setPledgeRatio(repoEntrustPledgeDto.getPledgeRatio());//质押率
            regFinPledgeMap.put(finTotalPledge.getPledgeFinprodId(), finTotalPledge);
            //递增维度
            for (RepoPtlAllocationDto ptlAllocationDto : ptls) {
                RegFinPledge regFinPledge = new RegFinPledge();
                regFinPledge.setPortfolioId(ptlAllocationDto.getPortfolioId());
                regFinPledge.setPledgeFinprodId(repoEntrustPledgeDto.getFinprodId());//质押券id
                regFinPledge.setPledgeRatio(ArithmeticUtil.divide(repoEntrustPledgeDto.getPledgeRatio(), new BigDecimal(100), yieldScale));//质押率
                regFinPledge.setPledgeNumber(ArithmeticUtil.divide(ArithmeticUtil.multiply(pledgePrin, ptlAllocationDto.getTradeAmt()), tradeAmt));
                regFinPledge.setTradeId(entrustIdTradeIdMap.get(ptlAllocationDto.getEntrustId()));//登记编号
                regFinPledge.setEntrustId(ptlAllocationDto.getEntrustId());//指令代码
                regFinPledgeList.add(regFinPledge);
                tailDifference = ArithmeticUtil.subtract(tailDifference, regFinPledge.getPledgeNumber());
            }
            handleTailDifferencePledge(regFinPledgeList, tailDifference);
            regFinPledgeList.forEach(item -> {
                item.setPledgeNumber(ArithmeticUtil.multiply(item.getPledgeNumber(), new BigDecimal(100)));
            });
            regFinPledges.addAll(regFinPledgeList);
        }
        return regFinPledges;
    }

    /**
     * 质押券质押券面 尾差处理
     *
     * @param repoEntrustPledgeDtos 质押券信息
     * @param tailDifference        尾差
     * @author jiangpeng
     * @date 2021/12/20 14:40
     */
    private void handleTailDifferencePledge(List<RegFinPledge> regFinPledges, BigDecimal tailDifference) {
        //递增维度
        BigDecimal addDimension = new BigDecimal(pledgeDimension);;
        int size = regFinPledges.size();
        //委托维度 已经完全分配的集合
        Set<String> set = new HashSet<>();
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && size > 0) {
            if (tailDifference.compareTo(BigDecimal.ONE) >= 0) {
                addDimension = ArithmeticUtil.divide(tailDifference, size, 4);
            }
            for (RegFinPledge item : regFinPledges) {
                if ((tailDifference.compareTo(BigDecimal.ZERO) <= 0)) {
                    if (set.add(item.getEntrustId())) {
                        size--;
                    }
                } else {
                    item.setPledgeNumber(ArithmeticUtil.doAdd(item.getPledgeNumber(), addDimension));
                    tailDifference = ArithmeticUtil.subtract(tailDifference, addDimension);
                }
            }
        }
    }

    /**
     * 尾差处理
     *
     * @param ptlAllocations 组合分配信息
     * @param tailDifference 尾差
     * @author jiangpeng
     * @date 2021/12/20 14:40
     */
    private void handleTailDifference(List<RepoEntrustPledgeDto> repoEntrustPledgeDtos, BigDecimal tailDifference) {
        //递增维度
        BigDecimal addDimension = new BigDecimal(pledgeDimension);;
        int size = repoEntrustPledgeDtos.size();
        //委托维度 已经完全分配的集合
        Set<String> set = new HashSet<>();
        while (tailDifference.compareTo(BigDecimal.ZERO) > 0 && size > 0) {
            if (tailDifference.compareTo(ArithmeticUtil.multiply(new BigDecimal(pledgeDimension), new BigDecimal(10))) >= 0) {
                addDimension = ArithmeticUtil.divide(tailDifference, size, 4);
            }
            for (RepoEntrustPledgeDto item : repoEntrustPledgeDtos) {
                if ((tailDifference.compareTo(BigDecimal.ZERO) <= 0)) {
                    if (set.add(item.getEntrustId())) {
                        size--;
                    }
                } else {
                    item.setPledgePrin(ArithmeticUtil.doAdd(item.getPledgePrin(), addDimension));
                    tailDifference = ArithmeticUtil.subtract(tailDifference, addDimension);
                }
                if(tailDifference.compareTo(BigDecimal.ZERO) <= 0){
                    break;
                }
            }
        }
    }

    private void insert(CfetsRepoDealDbDto entity, OperationType operationType) {
        RegFinProductDealTotal regFinProductDealTotal = entity.getRegFinProductDealTotal();
        String totleTradeId = regFinProductDealTotal.getTotleTradeId();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            regFinProductDealTotal.setTotleTradeId(StringUtil.join(totleTradeId, "R"));
            regFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            regFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            regFinProductDealTotal.setpTradeId(totleTradeId);
        } else {
            regFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            regFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        }
        regFinProductDealTotalMapper.insert(regFinProductDealTotal);
        if (StringUtil.isNotEmpty(entity.getRegFinProductDealTotalAdd())
                && !(operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE))) {
            entity.getRegFinProductDealTotalAdd().setTotleTradeId(regFinProductDealTotal.getTotleTradeId());
            regFinProductDealTotalAddMapper.insert(entity.getRegFinProductDealTotalAdd());
        }
        List<RegTradeFeeTotal> regTradeFeeTotals = entity.getRegTradeFeeTotals();
        if (CollectionUtils.isNotEmpty(regTradeFeeTotals)
                && !(operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE))) {
            regTradeFeeTotals.forEach(item -> {
                item.setTradeId(regFinProductDealTotal.getTotleTradeId());
                regTradeFeeTotalMapper.insert(item);
            });
        }
        List<RegFinProductDeal> regFinProductDeals = entity.getRegFinProductDeals();
        if (CollectionUtils.isNotEmpty(regFinProductDeals)) {
            regFinProductDeals.forEach(item -> {
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    String tradeId = item.getTradeId();
                    item.setTradeId(StringUtil.join(tradeId, "R"));
                    item.setTotleTradeId(StringUtil.join(totleTradeId, "R"));
                    item.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    item.setIsCancel(DictCodeList.是否_否.getValue());
                    item.setpTradeId(tradeId);
                    //明细
                    RegFinProductDeal regFinProductDeal = new RegFinProductDeal();
                    regFinProductDeal.setTradeId(tradeId);
                    regFinProductDeal.setRemark(regFinProductDealTotal.getRemark());
                    regFinProductDeal.setIsCancel("Y");
                    regFinProductDealMapper.updateByPrimaryKeySelective(regFinProductDeal);
                } else {
                    item.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                    item.setIsCancel(DictCodeList.是否_否.getValue());
                }
                regFinProductDealMapper.insert(item);
            });
        }
        List<RegFinProductDealAdd> regFinProductDealAdds = entity.getRegFinProductDealAdds();
        if (CollectionUtils.isNotEmpty(regFinProductDealAdds)) {
            regFinProductDealAdds.forEach(item -> {
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    item.setTradeId(StringUtil.join(item.getTradeId(), "R"));
                }
                regFinProductDealAddMapper.insert(item);
            });
        }
        List<RegTradeFee> regTradeFees = entity.getRegTradeFees();
        if (CollectionUtils.isNotEmpty(regTradeFees)) {
            regTradeFees.forEach(item -> {
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    item.setTradeId(StringUtil.join(item.getTradeId(), "R"));
                }
                regTradeFeeMapper.insert(item);
            });
        }
        List<RegFinTotalPledge> regFinTotalPledges = entity.getRegFinTotalPledges();
        if (CollectionUtils.isNotEmpty(regFinTotalPledges)) {
            regFinTotalPledges.forEach(item -> {
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    item.setTotleTradeId(StringUtil.join(item.getTotleTradeId(), "R"));
                }
                regFinTotalPledgeMapper.insert(item);
            });
        }
        List<RegFinPledge> regFinPledges = entity.getRegFinPledges();
        if (CollectionUtils.isNotEmpty(regFinPledges)) {
            regFinPledges.forEach(item -> {
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    item.setTradeId(StringUtil.join(item.getTradeId(), "R"));
                }
                regFinPledgeMapper.insert(item);
            });
        }
        /*// 交易类金融产品
        if (StringUtil.isNotEmpty(entity.getFinTradeProducts())) {
            // 先删后插
            entity.getFinTradeProducts().forEach(item -> {
                finTradeProductMapper.deleteByPrimaryKey(item);
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    finTradeProductMapper.insertSelective(item);
                }
            });
        }
        // 交易类金融产品附属表
        if (StringUtil.isNotEmpty(entity.getFinTradeProductAdds())) {
            // 先删后插
            entity.getFinTradeProductAdds().forEach(item -> {
                finTradeProductAddMapper.deleteByPrimaryKey(item);
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    finTradeProductAddMapper.insertSelective(item);
                }
            });
        }
        // 业务品种表
        if (StringUtil.isNotEmpty(entity.getFinProductTypes())) {
            // 先删后插
            entity.getFinProductTypes().forEach(item -> {
                finProductTypeMapper.deleteByPrimaryKey(item);
                if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
                    finProductTypeMapper.insertSelective(item);
                }
            });
        }*/
        //更新原登记数据状态
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {

            //汇总
            RegFinProductDealTotal regFinProduct = new RegFinProductDealTotal();
            regFinProduct.setTotleTradeId(totleTradeId);
            regFinProduct.setRemark(regFinProductDealTotal.getRemark());
            regFinProduct.setIsCancel("Y");
            regFinProductDealTotalMapper.updateByPrimaryKeySelective(regFinProduct);
        }
    }

    /**
     * 插入业务流程表
     *
     * @param entity
     */
    private void insertWfl(CfetsRepoDealDbDto entity) {
        String workflowId = entity.getWorkflowId();
        // 交易汇总
        WflRegFinProductDealTotal wflRegFinProductDealTotal = new WflRegFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDealTotal, entity.getRegFinProductDealTotal());
        wflRegFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflRegFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        wflRegFinProductDealTotal.setWorkflowId(workflowId);
        wflRegFinProductDealTotalMapper.insert(wflRegFinProductDealTotal);
        // 交易汇总附属
        WflRegFinProDealTotalAdd wflRegFinProDealTotalAdd = new WflRegFinProDealTotalAdd();
        if (StringUtil.isNotEmpty(entity.getRegFinProductDealTotalAdd())) {
            BeanUtil.copyPropertiesIgnoreNull(wflRegFinProDealTotalAdd, entity.getRegFinProductDealTotalAdd());
            wflRegFinProDealTotalAdd.setWorkflowId(workflowId);
            wflRegFinProDealTotalAddMapper.insert(wflRegFinProDealTotalAdd);
        }
        // 费用汇总
        List<RegTradeFeeTotal> regTradeFeeTotals = entity.getRegTradeFeeTotals();
        if (CollectionUtils.isNotEmpty(regTradeFeeTotals)) {
            regTradeFeeTotals.forEach(item -> {
                WflRegTradeFeeTotal wflRegTradeFeeTotal = new WflRegTradeFeeTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflRegTradeFeeTotal, item);
                wflRegTradeFeeTotal.setWorkflowId(workflowId);
                wflRegTradeFeeTotalMapper.insert(wflRegTradeFeeTotal);
            });
        }
        // 交易
        List<RegFinProductDeal> regFinProductDeals = entity.getRegFinProductDeals();
        if (CollectionUtils.isNotEmpty(regFinProductDeals)) {
            regFinProductDeals.forEach(item -> {
                WflRegFinProductDeal wflRegFinProductDeal = new WflRegFinProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDeal, item);
                wflRegFinProductDeal.setWorkflowId(workflowId);
                wflRegFinProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                wflRegFinProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                wflRegFinProductDealMapper.insert(wflRegFinProductDeal);
            });
        }
        // 交易附属
        List<RegFinProductDealAdd> regFinProductDealAdds = entity.getRegFinProductDealAdds();
        if (CollectionUtils.isNotEmpty(regFinProductDealAdds)) {
            regFinProductDealAdds.forEach(item -> {
                WflRegFinProductDealAdd wflRegFinProductDealAdd = new WflRegFinProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDealAdd, item);
                wflRegFinProductDealAdd.setWorkflowId(workflowId);
                wflRegFinProductDealAddMapper.insert(wflRegFinProductDealAdd);
            });
        }
        // 费用
        List<RegTradeFee> regTradeFees = entity.getRegTradeFees();
        if (CollectionUtils.isNotEmpty(regTradeFees)) {
            regTradeFees.forEach(item -> {
                WflRegTradeFee wflRegTradeFee = new WflRegTradeFee();
                BeanUtil.copyPropertiesIgnoreNull(wflRegTradeFee, item);
                wflRegTradeFee.setWorkflowId(workflowId);
                wflRegTradeFeeMapper.insert(wflRegTradeFee);
            });
        }
        // 质押券汇总表
        List<RegFinTotalPledge> regFinTotalPledges = entity.getRegFinTotalPledges();
        if (CollectionUtils.isNotEmpty(regFinTotalPledges)) {
            regFinTotalPledges.forEach(item -> {
                WflRegFinTotalPledge wflRegFinTotalPledge = new WflRegFinTotalPledge();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinTotalPledge, item);
                wflRegFinTotalPledge.setWorkflowId(workflowId);
                wflRegFinTotalPledgeMapper.insert(wflRegFinTotalPledge);
            });
        }
        // 质押券
        List<RegFinPledge> regFinPledges = entity.getRegFinPledges();
        if (CollectionUtils.isNotEmpty(regFinPledges)) {
            regFinPledges.forEach(item -> {
                WflRegFinPledge wflRegFinPledge = new WflRegFinPledge();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinPledge, item);
                wflRegFinPledge.setWorkflowId(workflowId);
                wflRegFinPledgeMapper.insert(wflRegFinPledge);
            });
        }
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流ID
     */
    private void deleteBussFlowData(String workflowId) {
        wflRegFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflRegFinProDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflRegTradeFeeTotalMapper.deleteByWorkflowId(workflowId);
        wflRegFinProductDealMapper.deleteByWorkflowId(workflowId);
        wflRegFinProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflRegTradeFeeMapper.deleteByWorkflowId(workflowId);
        wflRegFinPledgeMapper.deleteByWorkflowId(workflowId);
        wflRegFinTotalPledgeMapper.deleteByWorkflowId(workflowId);
    }

    /**
     * 处理履历
     */
    private void updateCfetsOprHis(String totleTradeId, OperationType operationType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(totleTradeId);
        if (operationType.equals(OperationType.NEW)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        } else if (operationType.equals(OperationType.REVOKE)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        }
        oprHisReq.setBusiDes("报价登记");
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    /**
     * 同个质押券的汇总质押券面总额必须为十万的整数倍，且不超过100亿
     *
     * @param secPledgePrinMap 质押券汇总质押券面总额
     * @param errorList        错误信息列表
     * @author jiangpeng
     * @date 2021/12/10 11:23
     */
    private void checkSecPledgePrin(Map<String, BigDecimal> secPledgePrinMap, List<ValidMessage> errorList) {
        // 十万
        BigDecimal oneHundredThousand = new BigDecimal(100000);
        for (Map.Entry<String, BigDecimal> map : secPledgePrinMap.entrySet()) {
            String bondCode = map.getKey();
            BigDecimal pledgerPrin = map.getValue();
            pledgerPrin = ArithmeticUtil.divide(pledgerPrin, oneHundredThousand, 2);
            if (new BigDecimal(pledgerPrin.intValue()).compareTo(pledgerPrin) != 0
                    || oneHundredThousand.compareTo(pledgerPrin) < 0) {
                errorList.add(new ValidMessage("债券" + bondCode + "汇总质押券面总额必须为十万的整数倍，且不超过100亿"));
            }
        }
    }

    /**
     * 头寸处理
     *
     * @param cfetsRepoDealRegisterQuoteDto
     * @param cfetsRepoDealDbDto
     * @author jiangpeng
     * @date 2021/12/24 10:12
     */
    private void sendPositionMethod(CfetsRepoDealRegisterQuoteDto entity, CfetsRepoDealDbDto cfetsRepoDealDbDto, boolean isSubmit, String confirmOk) {
        List<CfetsEtrIbRepoPosPortfolioCashRequest> ptlCashList = new ArrayList<>();
        List<CfetsEtrIbRepoPosPortfolioSecRequest> ptlSecList = new ArrayList<>();
        RegFinProductDealTotal dealTotal = cfetsRepoDealDbDto.getRegFinProductDealTotal();

        for (RegFinProductDeal dto : cfetsRepoDealDbDto.getRegFinProductDeals()) {
            //调用头寸
            CfetsEtrIbRepoPosPortfolioCashRequest posport = new CfetsEtrIbRepoPosPortfolioCashRequest();
            posport.setPortfolioId(dto.getPortfolioId());
            posport.setEtrId(dto.getEntrustId());
            posport.setTradeId(dto.getTradeId());
            if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("acctType", BussinessDictCodeList.资金账户类型_托管账户.getValue());
                String cashAcctId = this.qryPortfolioCashAcctId(map);
                if (StringUtil.isNotEmpty(cashAcctId)) {
                    posport.setCashAcctId(cashAcctId); // 托管户
                }
            }
            posport.setCashAmount(dto.getTradeAmt());
            ptlCashList.add(posport);
            if (StringUtil.isNotEmpty(cfetsRepoDealDbDto.getRegFinPledges())) {
                for (RegFinPledge regFinPledge : cfetsRepoDealDbDto.getRegFinPledges()) {
                    if (!StringUtil.equals(dto.getEntrustId(), regFinPledge.getEntrustId())) {
                        continue;
                    }
                    CfetsEtrIbRepoPosPortfolioSecRequest secport = new CfetsEtrIbRepoPosPortfolioSecRequest();
                    secport.setPortfolioId(regFinPledge.getPortfolioId());
                    secport.setEtrId(regFinPledge.getEntrustId());
                    secport.setTradeId(dto.getTradeId());
                    secport.setFinprodId(regFinPledge.getPledgeFinprodId());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", regFinPledge.getPortfolioId());
                    map.put("finprodId", regFinPledge.getPledgeFinprodId());
                    String secAcctId = this.qryPortfolioSecAcctId(map);
                    if (StringUtil.isNotEmpty(secAcctId)) {
                        secport.setSecAcctId(secAcctId); // 证券账户
                    }
                    secport.setSecAmount(ArithmeticUtil.multiply(regFinPledge.getPledgeNumber(), new BigDecimal(100)));
                    secport.setOwnSecAmount(ArithmeticUtil.multiply(regFinPledge.getPledgeNumber(), new BigDecimal(100)));
                    secport.setIntegrateSecAmount(BigDecimal.ZERO);
                    ptlSecList.add(secport);
                }
            }
        }
        //调用头寸
        CfetsEtrIbRepoPositionRequest req = new CfetsEtrIbRepoPositionRequest();
        req.setInterfaceType("REPO001");
        req.setUserId(SecurityUtil.getLoginUser().getUserName());
        req.setTotalTradeId(cfetsRepoDealDbDto.getRegFinProductDealTotal().getTotleTradeId());
        req.setFlowStatus(DictCodeListExt3.流程节点_流程提交.getValue());
        req.setModelType(DictCodeListExt1.子系统代码_交易.getValue());
        req.setWorkFlowId(entity.getWorkflowId());
        String balanceCheck = DictCodeList.是否_是.getValue();
        if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                || StringUtil.isNotEmpty(confirmOk)) {
            balanceCheck = DictCodeList.是否_否.getValue();
        }
        req.setRepoPledgeModel(CacheUtil.getSysParam("repo_pledge_model"));
        req.setBalanceCheck(balanceCheck);
        req.setBusiType(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue());
        req.setTradePlace(BussinessDictCodeList.交易场所_银行间.getValue());
        req.setSettleSpeed(entity.getSettleSpreedCode());
        req.setBusiAct(DictCodeListExt3.业务动作_交易登记.getValue());
        req.setPs(dealTotal.getPs());
        if (isSubmit) {
            req.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            req.setSystemAct(entity.getSystemAct());
        } else {
            req.setDecisionAct(DictCodeListExt3.系统动作_修改.getValue());
            req.setSystemAct(entity.getSystemAct());
            req.setbTotalTradeId(cfetsRepoDealDbDto.getRegFinProductDealTotal().getTotleTradeId());
        }
        req.setCdate(dealTotal.getTradeDate());
        req.setCcy(DictCodeList.币种_人民币_CNY.getValue());
        req.setPtlCashList(ptlCashList);
        req.setPtlSecList(ptlSecList);
        PositionHttpRequest.send(req);
    }

    /**
     * 查询产品质押债券 证券账户
     *
     * @param map
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 16:09
     */
    public String qryPortfolioSecAcctId(Map<String, Object> map) {
        return cfetsIbuQuoteRepoMapper.qryPortfolioSecAcctId(map);
    }

    /**
     * 查询产品资金账户
     *
     * @param map
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/13 16:09
     */
    private String qryPortfolioCashAcctId(Map<String, Object> map) {
        return cfetsIbuQuoteRepoMapper.qryPortfolioCashAcctId(map);
    }

    /**
     * 分页查询 参数赋值
     *
     * @param param 查询条件集合初始化
     * @author jiangpeng
     * @date 2021/12/11 16:17
     */
    private void setParam(Map<String, Object> param) {
        String loginUserId = LoginUserUtil.getUserName();
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
        param.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
        param.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
        param.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
        param.put("TRADEPLACE03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADEPLACE04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        if ("admin".equals(loginUserId)) {
            loginUserId = "jp";//TODO 测试写死登录用户 提交代码时修改
        }
        param.put("loginUserId", loginUserId);
        param.put("A_012", DictCodeListExt3.资产类型_质押式双边回购本方券已质押持仓.getValue());
        param.put("S_002", DictCodeListExt3.资产类型_可质押持仓.getValue());
        //银行间质押式回购质押券模式：01无质押券库，02单个质押库，03多个质押券库
        param.put("repo_pledge_model", CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
    }


    /**
     * 当日登记撤销检查
     *
     * @param cfetsBondRegCheckRequst
     * @return
     */
    public AjaxResult checkDayRegRevokeCheck(CfetsBondRegCheckRequst cfetsBondRegCheckRequst) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //是否存在流程中数据
        cfetsBondRegCheckRequst.getCfetsBondRegCheckDetaileRequsts().forEach(item -> {
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("IbuQuoteRepo004");
            wflValidInputParameter.setBusinessDataPk1(item.getQuoteId());
            if (null != mstWorkflowCommonService &&
                    mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)) {
                errorList.add(
                        new ValidMessage(this.getMessage("COMMON_E_0012", item.getQuoteId())));
            }
            //是否已确认成交
            if (cfetsIbuQuoteBondMapper.countMatchInfo(item.getQuoteId(), DictCodeList.确认状态_已确认.getValue()) > 0) {
                errorList.add(new ValidMessage("当日登记所选登记数据已成交确认不支持撤销!业务编号:" + item.getQuoteId()));
            }
            if (CollectionUtils.isNotEmpty(errorList)) {
                return;
            }
        });
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 数值比较 a<b 返回-1,a=b返回0,a>b返回1
     *
     * @param a     空值默认为0
     * @param b     空值默认为0
     * @param scale 比较精度,默认四舍五入
     * @return int
     * @author jiangpeng
     * @date 2021/12/30 21:10
     */
    public int ACompareToB(BigDecimal a, BigDecimal b, int scale) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        if (scale == 0) {
            scale = yieldScale;
        }
        return ArithmeticUtil.round(a, scale, RoundingMode.HALF_UP).compareTo(ArithmeticUtil.round(b, scale, RoundingMode.HALF_UP));
    }

    /**
     * 通过交易对手编号 查询是否已关联有效的银行间机构21位机构码
     *
     * @param counterId
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/31 10:23
     */
    public String findTraderByCounterId(String counterId) {
        return cfetsIbuQuoteRepo004Mapper.findTraderByCounterId(counterId);
    }

    /**
     * 查询是否已关联有效的银行间机构21位机构码
     *
     * @param partyId 21位机构码
     * @return java.lang.String
     * @author jiangpeng
     * @date 2021/12/31 10:23
     */
    public String findTraderByPartyId(String partyId) {
        return cfetsIbuQuoteRepo004Mapper.findTraderByPartyId(partyId);
    }

    /**
     * 撤销，供外部调用
     *
     * @param totleTradeId
     * @param remark
     * @author jiangpeng
     * @date 2022/1/21 17:05
     */
    public void revokeForOut(String totleTradeId, String remark) {
        CfetsRepoDealDbDto result = new CfetsRepoDealDbDto();
        RegFinProductDealTotal total = regFinProductDealTotalMapper.selectByPrimaryKey(totleTradeId);
        total.setRemark(remark);
        result.setRegFinProductDealTotal(total);
        result.setRegFinProductDealTotalAdd(regFinProductDealTotalAddMapper.selectByPrimaryKey(totleTradeId));
        result.setRegTradeFeeTotals(cfetsIbuQuoteBondMapper.listRegTradeFeeTotal(totleTradeId));
        result.setRegFinProductDeals(cfetsIbuQuoteBondMapper.listRegFinProductDeal(totleTradeId));
        result.setRegFinProductDealAdds(cfetsIbuQuoteBondMapper.listRegFinProductDealAdd(totleTradeId));
        result.setRegTradeFees(cfetsIbuQuoteBondMapper.listRegTradeFee(totleTradeId));
        result.setRegFinPledges(cfetsIbuQuoteRepoMapper.listRegFinPledge(totleTradeId));
        // 登录db
        this.insert(result, OperationType.REVOKE);
        // 履历表
        this.updateCfetsOprHis(totleTradeId, OperationType.REVOKE);
        // 头寸
        PositionHttpRequest.dorevoke(totleTradeId, DictCodeListExt1.子系统代码_交易.getValue(),
                DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), result.getRegFinProductDealTotal().getPs());
        // 处理旧头寸
        this.oldPositionDeal(result, true);
        //处理指令执行进度
        this.insertEtrFinishStatus(result, OperationType.REVOKE);
    }


    /**
     * 根据工作流代码获取entity
     * <功能详细描述>
     *
     * @param wflValidInputParameter
     * @return
     * @see
     * @since 1.0
     */
    public ProcessMark getBusinessInfoByPageIdAndPk(WflValidInputParameter wflValidInputParameter) {
        return cfetsIbuQuoteRepoMapper.getBusinessInfoByPageIdAndPk(wflValidInputParameter);
    }

    /**
     * 处理旧头寸
     *
     * @param cfetsRepoDealDbDto
     * @param navigate           是否撤销
     * @author jiangpeng
     * @date 2022/2/24 11:17
     */
    private void oldPositionDeal(CfetsRepoDealDbDto cfetsRepoDealDbDto, boolean navigate) {
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
            //处理组质押品的组合持仓流水
            this.ptlSecPositionFlowLogic(cfetsRepoDealDbDto, navigate);
            if(!navigate) {
                for (RegFinProductDeal deal : cfetsRepoDealDbDto.getRegFinProductDeals()) {
                    AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
                    FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                    finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    finProductTradeDto.setAssetType2(DictCodeList.回购类型_质押式回购.getValue());
                    finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
                    BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                    baseTradeProductDealDto.setEntrustId(deal.getEntrustId());
                    TrdProductDeal trdProductDeal = new TrdProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, deal);
                    trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
                    trdProductDeal.setSettleDate(deal.getvSettleDate());
                    trdProductDeal.setPrinAmt(deal.getShareAmt());
                    baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
                    TrdProductDealAdd add = new TrdProductDealAdd();
                    add.setPortfolioId(deal.getPortfolioId());
                    add.setTradeId(deal.getTradeId());
                    add.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                    baseTradeProductDealDto.setTrdProductDealAdd(add);
                    //处理指令完成情况
                    List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                    StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                    if (StringUtil.isEmpty(stlPayOrder.getMyAcctNo())) {
                        String acctId = EtrCommonCashPositionDealService.select组合托管户(deal.getPortfolioId(), deal.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                        stlPayOrder.setMyAcctNo(acctId);
                    }
                    stlPayOrder.setOrderId(deal.getTradeId());
                    stlPayOrder.setCcy(deal.getCcy());
                    stlPayOrder.setSettleAmt(deal.getTradeAmt());
                    stlPayOrder.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                    if (StringUtil.equals(DictCodeListExt3.交易方向_正回购.getValue(), deal.getPs())) {
                        stlPayOrder.setPs(DictCodeListExt1.收支方向_收入.getValue());
                    } else {
                        stlPayOrder.setPs(DictCodeListExt1.收支方向_支出.getValue());
                    }
                    stlPayOrders.add(stlPayOrder);
                    baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
                    finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                    baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                }
            }
        }
    }

    /**
     * 写入质押品的组合持仓流水
     *
     * @param input    画面参数Dto
     * @param isNegate 是否冲销
     */
    private boolean ptlSecPositionFlowLogic(CfetsRepoDealDbDto input, boolean isNegate) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();
        // 处理结果
        EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
        // 持仓流水
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();
        // 现金流水
        List<EtrCashPositionFlowDto> etrCashPositionFlows = new ArrayList<>();
        etrPositionHoldDto.setPtlCashPositionFlows(etrCashPositionFlows);
        etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
        etrPositionHoldDto.setDealEtr(true);
        // 冲销时（撤销）
        if (isNegate) {
            // 标记委托编号,避免重复操作
            Map<String,String> entrustIdList = new HashMap<>();
            for (RegFinProductDeal deal : input.getRegFinProductDeals()) {
                trdOutRightRepoTradeService.updatePledgeBondForRevoke(deal.getpTradeId(), DictCodeList.持仓类型_质押.getValue(), true);
                if (StringUtil.isEmpty(entrustIdList.get(deal.getEntrustId()))) {
                    this.revokeEtrSecPosition(deal.getEntrustId(), null, false, null);
                    entrustIdList.put(deal.getEntrustId(), deal.getEntrustId());
                }
            }
        } else {
            // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
            // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
            if (!StringUtil.isEmptyList(input.getRegFinPledges())
                    && DictCodeList.回购拆借方向_正回购.getValue().equals(input.getRegFinProductDealTotal().getPs())) {
                for (RegFinProductDeal deal : input.getRegFinProductDeals()) {
                    // 可质押券信息（含投资目的）
                    Map<String, List<PledgeBondInfoDto>> avaMap = this.selectAvaPledgeWithInvAim(deal)
                            .stream().collect(JoyinCollectors.groupingBy(x -> x.getFinprodId()));
                    avaMap.entrySet().stream().forEach(entry -> {
                        Collections.sort(entry.getValue(), (o1, o2) -> o1.getInvAimSort().compareTo(o2.getInvAimSort()));
                    });
                    PtlSecPositionFlowDto resultDto;
                    for (RegFinPledge pledgeBond : input.getRegFinPledges()) {
                        if (!StringUtil.equals(pledgeBond.getTradeId(), deal.getTradeId())) {
                            continue;
                        }
                        // 待分配投资目的的质押券面总额
                        BigDecimal pledgePrin = pledgeBond.getPledgeNumber();//数量
                        List<PledgeBondInfoDto> pledgeBondInfoDtos = avaMap.get(pledgeBond.getPledgeFinprodId());
                        if (!StringUtil.isEmptyList(pledgeBondInfoDtos)) {
                            for (PledgeBondInfoDto avaBond : pledgeBondInfoDtos) {
                                resultDto = new PtlSecPositionFlowDto();

                                // 当前投资目的可分配券面总额
                                BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                                // 当前投资目的决定分配的券面总额
                                BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                                // 组合代码 = 画面.组合代码
                                resultDto.setPortfolioId(deal.getPortfolioId());
                                // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
                                resultDto.setSecAcctId(StringUtil.isEmpty(deal.getSecManageAcctId()) ? "999999" : deal.getSecManageAcctId());
                                // 金融产品代码 = 输出质押品DTO.金融产品代码
                                resultDto.setFinprodId(pledgeBond.getPledgeFinprodId());
                                // 分支序号 = 系统固定参数，默认为0
                                resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                                // 持仓类型
                                resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                                // 日期 = 金融产品交易Entity.交易日
                                resultDto.setCdate(deal.getTradeDate());
                                // 数量 = 输出质押品DTO.质押数量
                                resultDto.setAmount(prin);
                                // 币种 = 金融产品交易Entity.币种
                                resultDto.setCcy(deal.getCcy());
                                // 交易编号 = 金融产品交易Entity.交易编号
                                resultDto.setTradeId(deal.getTradeId());
                                // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                                resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());
                                // 数据已入新头寸，Y：已入
                                resultDto.setIsExistNew(DictCodeList.是否_是.getValue());
                                if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                                    lstPtlSecPositionFlowDto.add(resultDto);
                                    EtrSecPositionFlowDto etrSecPositionFlowDto = this.etrSecPositionFlowLogic(resultDto, pledgeBond.getEntrustId());
                                    etrSecPositionFlows.add(etrSecPositionFlowDto);
                                }
                                // 分配完毕后,停止可质押券信息（含投资目的）的循环
                                pledgePrin = pledgePrin.subtract(prin);
                                if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        // 指令持仓更新
        EtrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            if (ptlPositionHoldDto != null) {
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
            }
            return true;
        }
    }

    /**
     * 撤销时更新指令持仓流水
     *
     * @param entrustId
     * @author jiangpeng
     * @date 2022/3/11 10:21
     */
    public void revokeEtrSecPosition(String entrustId, Date settleDate, boolean isRevoke, String tradeId) {
        if (StringUtil.isEmpty(tradeId)) {
            tradeId = entrustId;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("bizCode", entrustId);
        param.put("POSITION_TYPE_ROLLIN", DictCodeListExt1.持仓类型_委托指令可用.getValue());
        param.put("POSITION_TYPE_ROLLOUT", DictCodeListExt1.持仓类型_委托指令可用.getValue());
        List<EtrSecPositionFlow> etrSecPositionFlowList = cfetsIbuQuoteRepo004Mapper.selectEtrSecPositionFlow(param);
        for (EtrSecPositionFlow flow : etrSecPositionFlowList) {
            EtrSecPosition position = new EtrSecPosition();
            flow.setAmount(flow.getAmount().negate());
            if (isRevoke) {
                flow.setAmount(flow.getAmount().negate());
            }
            if (StringUtil.isNotEmpty(settleDate)) {
                flow.setCdate(settleDate);
            }
            flow.setOffsetFlag(DictCodeList.冲正标识_冲销交易.getValue());
            flow.setOffsetId(flow.getTradeId());
            flow.setTradeId(tradeId);
            flow.setSeqno(IDGenerator.generate());
            BeanUtil.copyPropertiesIgnoreNull(position, flow);
            etrSecPositionFlowMapper.insert(flow);
            cfetsIbuQuoteRepo004Mapper.updatePosition(position);
        }
    }

    /**
     * 撤销时更新组合持仓流水
     *
     * @param entrustId
     * @author jiangpeng
     * @date 2022/3/11 10:21
     */
    public void revokePtlCashPosition(String orderId, String hodingType, String hodingTypeOld) {
        Map<String, Object> param = new HashMap<>();
        param.put("bizCode", orderId);
        param.put("hodingType", hodingType);
        List<PtlCashPositionFlow> ptlCashPositionFlows = cfetsIbuQuoteRepo004Mapper.selectPtlCashPositionFlow(param);
        for (PtlCashPositionFlow flow : ptlCashPositionFlows) {
            PtlCashPosition position = new PtlCashPosition();
            flow.setAmount(flow.getAmount().negate());
            BeanUtil.copyPropertiesIgnoreNull(position, flow);
            flow.setSeqno(IDGenerator.generate());
            ptlCashPositionFlowMapper.insert(flow);
            if (StringUtil.equals(DictCodeList.持仓类型_实际.getValue(), hodingType)) {
                cfetsIbuQuoteRepo004Mapper.updatePtlCashPosition(position);
            }
            if(StringUtil.isNotEmpty(hodingTypeOld)){
                PtlCashPositionFlow flow1 = new PtlCashPositionFlow();
                BeanUtil.copyPropertiesIgnoreNull(flow1, flow);
                flow1.setHodingType(hodingTypeOld);
                flow1.setAmount(flow1.getAmount().negate());
                flow1.setSeqno(IDGenerator.generate());
                ptlCashPositionFlowMapper.insert(flow1);
            }
        }
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(RegFinProductDeal input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", input.getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        param.put("secAcctId", input.getSecManageAcctId());
        return bussRepoSecService.selectAvaPledgeBondDetailList(param);
    }

    private EtrSecPositionFlowDto etrSecPositionFlowLogic(PtlSecPositionFlowDto ptlSecPositionFlowDto, String entrustId) {
        EtrSecPositionFlowDto etrSecPositionFlowDto = new EtrSecPositionFlowDto();

        BeanUtil.copyPropertiesIgnoreNull(etrSecPositionFlowDto, ptlSecPositionFlowDto);

        etrSecPositionFlowDto.setSeqno(IDGenerator.generate());
        // 金融产品代码 = 输出质押品DTO.金融产品代码
        // 分支序号 = 系统固定参数，默认为0
        etrSecPositionFlowDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        // 持仓类型
        etrSecPositionFlowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        // 数量 = 输出质押品DTO.质押数量
        etrSecPositionFlowDto.setAmount(ptlSecPositionFlowDto.getAmount().negate());
        // 交易编号 = 金融产品交易Entity.交易编号
        etrSecPositionFlowDto.setTradeId(entrustId);
        return etrSecPositionFlowDto;
    }

    /**
     * 预录入,插入旧头寸重新组装
     *
     * @param input
     * @return com.joyintech.tams.invest.trade.pretrdrepotrade.dto.PreTrdRepoTrade002Dto
     * @author jiangpeng
     * @date 2022/2/24 11:25
     */
    public PreTrdRepoTrade002Dto prepareEntityPre(CfetsRepoDealDbDto input) {
        List<TrdProductDealPre> trdProductDealPres = new ArrayList<>();
        List<TrdProductDealAddPre> trdProductDealAddPres = new ArrayList<>();
        PreTrdRepoTrade002Dto resultDto = new PreTrdRepoTrade002Dto();
        for (RegFinProductDeal deal : input.getRegFinProductDeals()) {
            TrdProductDealPre trdProductDealPre = new TrdProductDealPre();
            TrdProductDealAddPre trdProductDealAddPre = new TrdProductDealAddPre();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealPre, deal);
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddPre, deal);
            trdProductDealPre.setSettleDate(deal.getTradeDate());
            trdProductDealPres.add(trdProductDealPre);
            trdProductDealAddPres.add(trdProductDealAddPre);
        }
        resultDto.setTrdProductDealPreList(trdProductDealPres);
        resultDto.setTrdProductDealAddPreList(trdProductDealAddPres);
        return resultDto;
    }


    @Override
    public String generateTitle(CfetsRepoDealRegisterQuoteDto dto, OperationType type) {
        String counterTrader = null;
        if (StringUtil.isNotEmpty(dto.getCounterTrader())
                && StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "01")) {
            CfetsSearchInfoRequest item = new CfetsSearchInfoRequest();
            item.setDealType("02");
            item.setPartyId(dto.getCounterId());
            item.setSearch(dto.getCounterTrader());
            List<SelectOptionDto> list = joyinbusinessCommonService.queryMySideTraderOrCountTrader(item);
            if (CollectionUtils.isNotEmpty(list)) {
                counterTrader = "-对手方交易员:" + list.get(0).getText() + "-";
            }
        }
        if (StringUtil.isNotEmpty(dto.getCounterTrader())
                && StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "02")) {
            counterTrader = "-对手方交易员:" + dto.getCounterTrader() + "-";
        }
        String str = "交易日期:" + DateUtil.format(iPlatformDate.getSysDate());
        if (StringUtil.isNotEmpty(dto.getRepoTerm())) {
            str += "-回购期限:" + dto.getRepoTerm() + "天";
        }
        str += StringUtil.isEmpty(counterTrader) ? "-" : counterTrader;
        if (StringUtil.isNotEmpty(dto.getTradeAmt())) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            str += "交易金额:" + df.format(ArithmeticUtil.round(dto.getTradeAmt(), 2, RoundingMode.HALF_UP)) + "元";
        }
        return str;
    }

    public int getIsExistCounterId(List<String> entrustIdList){
        return cfetsIbuQuoteRepo004Mapper.getIsExistCounterId(entrustIdList);
    }
}
