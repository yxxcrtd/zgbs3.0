/*
 * 文件名：CfetsBondDealQuotedPrice002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.bond.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealDbDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.RegFinProductDealAddMapper;
import com.joyintech.tams.common.base.dao.RegFinProductDealMapper;
import com.joyintech.tams.common.base.dao.RegFinProductDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.RegFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.RegTradeFeeMapper;
import com.joyintech.tams.common.base.dao.RegTradeFeeTotalMapper;
import com.joyintech.tams.common.base.dao.WflRegFinProDealTotalAddMapper;
import com.joyintech.tams.common.base.dao.WflRegFinProductDealAddMapper;
import com.joyintech.tams.common.base.dao.WflRegFinProductDealMapper;
import com.joyintech.tams.common.base.dao.WflRegFinProductDealTotalMapper;
import com.joyintech.tams.common.base.dao.WflRegTradeFeeMapper;
import com.joyintech.tams.common.base.dao.WflRegTradeFeeTotalMapper;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.RegFinProductDeal;
import com.joyintech.tams.common.base.entity.RegFinProductDealAdd;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotal;
import com.joyintech.tams.common.base.entity.RegFinProductDealTotalAdd;
import com.joyintech.tams.common.base.entity.RegTradeFee;
import com.joyintech.tams.common.base.entity.RegTradeFeeTotal;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdProductDealAddPre;
import com.joyintech.tams.common.base.entity.TrdProductDealPre;
import com.joyintech.tams.common.base.entity.WflRegFinProDealTotalAdd;
import com.joyintech.tams.common.base.entity.WflRegFinProductDeal;
import com.joyintech.tams.common.base.entity.WflRegFinProductDealAdd;
import com.joyintech.tams.common.base.entity.WflRegFinProductDealTotal;
import com.joyintech.tams.common.base.entity.WflRegTradeFee;
import com.joyintech.tams.common.base.entity.WflRegTradeFeeTotal;
import com.joyintech.tams.component.base.core.impl.dao.MstWorkflowCommonServiceMapperImpl;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.PreTrdCommonBaseService;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.trade.pretrdbondtrade.dto.PreTrdBondInvestTrade002Dto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.constant.ProcessState;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service.getBussinessCodeParameter;

/**
 * 现券报价(登记)
 */
@Service("vueCfetsBondDealQuotedPrice004Service")
public class CfetsBondDealQuotedPrice004Service extends BaseService<CfetsBondDealRegisterQuoteDto> implements BaseProcessService<CfetsBondDealRegisterQuoteDto> {

    private static final Logger logger = LoggerFactory.getLogger(CfetsBondDealQuotedPrice004Service.class);

    @Autowired
    private IPlatformDate iPlatformDate;
    @Autowired
    private FinProductMapper finProductMapper;
    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

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
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private BaseEtrInvestInfoService baseEtrInvestInfoServiceImpl;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService cfetsQuotedCommonServicevue;

    @Autowired
    private MstWorkflowCommonServiceMapperImpl mstWorkflowCommonServiceMapperImpl;

    @Autowired
    private PreTrdCommonBaseService preTrdCommonBaseService;

    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;

    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsBondDealRegisterQuoteDto entity, OperationType type) {
        DecimalFormat df = new DecimalFormat("###,###.00");
        return "登记日期(" + DateUtil.format(entity.getTradeDate()) + ")-" +
                "资产简称(" + (entity.getFinprodAbbr() == null ? "" : entity.getFinprodAbbr()) + ")-" +
                "对手方(" + (entity.getCounterName() == null ? "" : entity.getCounterName()) + ")-" +
                "券面总额(" + df.format(ArithmeticUtil.divide(entity.getShareAmt(), new BigDecimal(10000), 2)) + "万元)";
    }

    /**
     * 提交保存check 必填项
     *
     * @param entity
     * @return
     */
    public AjaxResult checkNotEmpty(CfetsBondDealQuoteCommonDto entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //模式
        String exportShow = CacheUtil.getSysParam("EXPORT_SHOW");
        String ps = entity.getPs();
        if (StringUtil.isEmpty(ps)) {
            errorList.add(new ValidMessage("请输入交易方向"));
        }
        if (StringUtil.isEmpty(entity.getFinprodId())) {
            errorList.add(new ValidMessage("请输入资产代码"));
        }
        if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
            if (StringUtil.equals(exportShow, "01") && StringUtil.isEmpty(entity.getMySide())) {
                errorList.add(new ValidMessage("请输入本方"));
            }
            if (StringUtil.isEmpty(entity.getMySideTrader())) {
                errorList.add(new ValidMessage("请输入本方交易员"));
            }
            if (StringUtil.isEmpty(entity.getCounterId())) {
                errorList.add(new ValidMessage("请输入对手方"));
            }
            if (StringUtil.equals(CacheUtil.getSysParam("EXPORT_SHOW"), "01") && StringUtil.isEmpty(entity.getCounterTrader()) && (StringUtil.equals(entity.getQuoteStyle(), "01") || StringUtil.equals(entity.getQuoteStyle(), "02"))) {
                errorList.add(new ValidMessage("请输入对手方交易员"));
            }
            if (StringUtil.isEmpty(entity.getCcy())) {
                errorList.add(new ValidMessage("请输入币种"));
            }
            if (StringUtil.isEmpty(entity.getSettleSpreedCode())) {
                errorList.add(new ValidMessage("请输入清算速度"));
            }
            if (null == entity.getSettleDate()) {
                errorList.add(new ValidMessage("请输入结算日期"));
            }
            if (null != entity.getPrice() && entity.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                errorList.add(new ValidMessage("净价必须大于0"));
            }
            if (null == entity.getShareAmt()) {
                errorList.add(new ValidMessage("请输入券面总额"));
            } else {
                if (entity.getShareAmt().compareTo(BigDecimal.ZERO) <= 0) {
                    errorList.add(new ValidMessage("券面总额必须大于0"));
                }
            }
            if (StringUtil.isEmpty(entity.getSettleMethod())) {
                errorList.add(new ValidMessage("请输入结算方式"));
            }
            if (null == entity.getSettleType()) {
                errorList.add(new ValidMessage("请输入清算类型"));
            }
          /*  if(null==entity.getDealFeeAmt()){
                errorList.add(new ValidMessage("请输入交易手续费"));
            }*/
        }
        /*if(StringUtil.equals(sellPara,"02")){
            if(null==entity.getOurEntNum()){
                errorList.add(new ValidMessage("请输入本方券券面总额"));
            }
            if(null==entity.getLendinEntNum()&&StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())){
                errorList.add(new ValidMessage("请输入融入券券面总额"));
            }
            if(null==entity.getSellAbleNum()&&StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())){
                errorList.add(new ValidMessage("请输入卖空购回券面总额"));
            }
        }*/
       /* if(null==entity.getSettFeeAmt()){
            errorList.add(new ValidMessage("请输入结算服务费"));
        }*/
        if (CollectionUtils.isEmpty(entity.getPtlAllocatios())) {
            errorList.add(new ValidMessage("组合分配不可为空"));
        } else {
            List<String> entrustIdList = new ArrayList<>();
            BigDecimal shareAmtTotal = BigDecimal.ZERO;
            BigDecimal dealFeeAmtTotal = BigDecimal.ZERO;
            BigDecimal settFeeamtTotal = BigDecimal.ZERO;
            for (PtlAllocationDto ptlAllocatio : entity.getPtlAllocatios()) {
                entrustIdList.add(ptlAllocatio.getEntrustId());
                if (null == ptlAllocatio.getShareAmt()) {
                    errorList.add(new ValidMessage("请输入组合分配券面总额"));
                    break;
                } else {
                    if (ptlAllocatio.getShareAmt().compareTo(BigDecimal.ZERO) <= 0) {
                        errorList.add(new ValidMessage("组合分配券面总额必须大于0"));
                    }
                }
                if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                    if (null == ptlAllocatio.getDealFeeAmt()) {
                        errorList.add(new ValidMessage("请输入组合分配交易手续费"));
                        break;
                    }
                }
                if (null == ptlAllocatio.getSettFeeAmt()) {
                    errorList.add(new ValidMessage("请输入组合分配结算服务费"));
                    break;
                }
                /*if(StringUtil.equals(sellPara,"02")){
                    if(null==ptlAllocatio.getOurEntNum()){
                        errorList.add(new ValidMessage("请输入组合分配本方券券面总额"));
                        break;
                    }
                    if(null==ptlAllocatio.getLendinEntNum()&&StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())){
                        errorList.add(new ValidMessage("请输入组合分配融入券券面总额"));
                        break;
                    }
                    if(null==ptlAllocatio.getSellAbleNum()&&StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())){
                        errorList.add(new ValidMessage("请输入组合分配卖空购回券面总额"));
                        break;
                    }
                }*/
                if (ptlAllocatio.getAvaNum().compareTo(ptlAllocatio.getShareAmt()) < 0) {
                    errorList.add(new ValidMessage("登记指令剩余额度不能小于券面总额！"));
                }
                if (null != ptlAllocatio.getShareAmt()) {
                    BigDecimal avaNum = cfetsQuotedCommonService.getWflShareAmtByEntrustId(ptlAllocatio.getEntrustId(), entity.getQuoteId());
                    //if (null != wflShareAmt && wflShareAmt.compareTo(BigDecimal.ZERO) > 0 && ArithmeticUtil.doAdd(ptlAllocatio.getShareAmt(), wflShareAmt).compareTo(ptlAllocatio.getAvaNum()) > 0) {
                    //组合分配中交易金额要小于等于（剩余指令额度-已成功发出的未收到外汇交易中心成功反馈报价和流程中的报价-流程中交易登记额度）
                    // 已实现: 剩余指令额度 = 指令额度-生效的交易登记额度-流程中交易登记额度-已成功发出的未收到外汇交易中心成功反馈报价和流程中的报价
                    if (ptlAllocatio.getShareAmt().compareTo(avaNum) > 0) {
                        errorList.add(new ValidMessage(StringUtil.join("组合分配券面总额不能超过指令剩余额度!流程中占用额度:", avaNum.toString(), "元,指令代码:", ptlAllocatio.getEntrustId())));
                    }
                }
                shareAmtTotal = ArithmeticUtil.doAdd(shareAmtTotal, ptlAllocatio.getShareAmt());
                dealFeeAmtTotal = ArithmeticUtil.doAdd(dealFeeAmtTotal, ptlAllocatio.getDealFeeAmt());
                settFeeamtTotal = ArithmeticUtil.doAdd(settFeeamtTotal, ptlAllocatio.getSettFeeAmt());
                if (StringUtil.equals(entity.getTradeMarket(), BussinessDictCodeList.交易场所_银行间.getValue())) {
                    AjaxResult ajaxResultPartyId = cfetsQuotedCommonServicevue.checkpartyId(ptlAllocatio.getEntrustId(), ptlAllocatio.getPortfolioId(), DictCodeListExt3.报价系统区分类型_现券登记报价.getValue());
                    if (StringUtil.isNotEmpty(ajaxResultPartyId.getMessage())) {
                        errorList.add(new ValidMessage(ajaxResultPartyId.getMessage()));
                    }
                }
            }
            //券面总额   交易手续费  结算服务费  总和需要相等
            if (CollectionUtils.isEmpty(errorList)) {
                if (null != entity.getShareAmt() && entity.getShareAmt().compareTo(shareAmtTotal) != 0) {
                    errorList.add(new ValidMessage("组合分配券面总额总和需要等于券面总额"));
                }
                if (null != entity.getDealFeeAmt() && entity.getDealFeeAmt().compareTo(dealFeeAmtTotal) != 0) {
                    errorList.add(new ValidMessage("组合分配交易手续费总和需要等于交易手续费"));
                }
                if (null != entity.getSettFeeAmt() && entity.getSettFeeAmt().compareTo(settFeeamtTotal) != 0) {
                    errorList.add(new ValidMessage("组合分配结算服务费总和需要等于结算服务费"));
                }
            }
            //检查指令是否是已接收状态
            Map<String, Object> paramFail = new HashMap<>();
            paramFail.put("entrustIdList", entrustIdList);
            paramFail.put("entrustStatus", BussinessDictCodeList.委托状态_已接收.getValue());
            if (cfetsIbuQuoteBondEtrMapper.countFailEtr(paramFail) > 0) {
                errorList.add(new ValidMessage("指令非已接收状态，不可提交!"));
            }
        }
        if (StringUtil.isNotEmpty(entity.getFinprodId())) {
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
            if (StringUtil.equals(finProduct.getIsRight(), DictCodeList.是否_是.getValue())) {
                Map<String, Object> param = new HashMap<>();
                if (null == entity.getTradeDate()) {
                    param.put("sysDate", iPlatformDate.getSysDate());
                } else {
                    param.put("sysDate", entity.getTradeDate());
                }
                param.put("finprodId", entity.getFinprodId());
                //未来有行权计划
                if (cfetsIbuQuoteBondMapper.countBondHasEexrPlan(param) > 0) {
                    if (null == entity.getPrice() && null == entity.getMatureYield() && null == entity.getExerYield()) {
                        errorList.add(new ValidMessage("请输入净价或者到期收益率或者行权收益率"));
                    }
                }
            } else {
                if (null == entity.getPrice() && null == entity.getMatureYield()) {
                    errorList.add(new ValidMessage("请输入净价或者到期收益率"));
                }
            }
            //锚定值不可为空
            if (StringUtil.isEmpty(entity.getCalculateField())) {
                errorList.add(new ValidMessage("请输入锚定值(净价或者到期收益率或者行权收益率)"));
            }
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }


    /**
     * 初始化登记
     *
     * @param cfetsBondDealQuoteCommonDto
     * @return
     */
    public CfetsBondDealQuoteCommonDto initReg(CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
        String quoteId = cfetsBondDealQuoteCommonDto.getQuoteId();
        if (StringUtil.equals(cfetsBondDealQuoteCommonDto.getValidStatus(), BussinessDictCodeList.有效状态_流程中.getValue())) {
            cfetsBondDealQuoteCommonDto = cfetsIbuQuoteBondMapper.getWflCfetsBondDealQuoteCommonDto(quoteId);
            if (null != cfetsBondDealQuoteCommonDto) {
                cfetsBondDealQuoteCommonDto.setPtlAllocatios(cfetsIbuQuoteBondMapper.listWflPtlAllocationDto(quoteId));
            }
        } else {
            cfetsBondDealQuoteCommonDto = cfetsIbuQuoteBondMapper.getCfetsBondDealQuoteCommonDto(quoteId);
            if (null != cfetsBondDealQuoteCommonDto) {
                cfetsBondDealQuoteCommonDto.setPtlAllocatios(cfetsIbuQuoteBondMapper.listPtlAllocationDto(quoteId));
            }
        }
        return cfetsBondDealQuoteCommonDto;
    }

    /**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsBondDealRegisterQuoteDto entity,OperationType type){
        //履历表
        updateCfetsOprHis(entity.getTotleTradeId(),type);
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        if (!type.equals(OperationType.REVOKE) && !type.equals(OperationType.DELETE)) {
            CfetsBondDealDbDto cfetsBondDealDbDto = prepareEntity(entity, type);
            //登录业务流程
            insertWfl(cfetsBondDealDbDto);
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
                //处理老头寸
                this.oldPositionDeal(cfetsBondDealDbDto, false);
            }

//            RegFinProductDealTotal regTotal=cfetsBondDealDbDto.getRegFinProductDealTotal();
//            if (null!=regTotal && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))
//                    && (StringUtil.equals(regTotal.getPs(),DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(regTotal.getPs(),DictCodeList.交易方向_卖出.getValue()))
//            ){
//                //插入旧头寸
//                //处理组合及指令头寸
//                PreTrdBondInvestTrade002Dto entityPre=prepareEntityPre(cfetsBondDealDbDto);
//                PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
//                preTrdCommonDto.setTrdProductDealPreList(entityPre.getTrdProductDealPreList());
//                preTrdCommonDto.setTrdProductDealAddPreList(entityPre.getTrdProductDealAddPreList());
//                preTrdCommonDto.setIsExistNew(DictCodeList.是否_是.getValue());
//                preTrdCommonBaseService.excutePosition(preTrdCommonDto,false);
//            }
//            try {
                //头寸
                PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto, DictCodeListExt3.系统动作_新建.getValue(), DictCodeListExt3.系统动作_新建.getValue(), type,null));
//            } catch (PositionException e) {
//                e.printStackTrace();
//                Message message = PositionUtil.ajaxErrorReturn(e);
//                throw new BusinessException("MSG:" + message.getMessage());
//            }
        }

    }

    /**
     * 处理旧头寸
     *
     * @param cfetsBondDealDbDto 入参
     * @param navigate           是否撤销
     */
    private void oldPositionDeal(CfetsBondDealDbDto cfetsBondDealDbDto, boolean navigate) {

        if (navigate) {
            cfetsBondDealDbDto.getRegFinProductDeals().stream().forEach( item ->{
                AllTradeCommonUtil.deleteRegPosition(item.getRemark() == null ? "登记撤销":item.getRemark(), item.getTradeId());
            });

        } else {
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[可用]的组合持仓流水及现金流水
            for (RegFinProductDeal item : cfetsBondDealDbDto.getRegFinProductDeals()) {
                FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
                finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                finProductTradeDto.setIsExistNew(DictCodeList.是否_是.getValue());
                finProductTradeDto.setNeedSum(true);
                finProductTradeDto.setNeedExecuteEtrPosition(true);
                BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, item);
                baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);

                TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                trdProductDealAdd.setPortfolioId(item.getPortfolioId());
                trdProductDealAdd.setTradeId(item.getTradeId());
                trdProductDealAdd.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd);

                //处理指令完成情况
                List<StlPayOrdersDto> stlPayOrders = new ArrayList<>();
                StlPayOrdersDto stlPayOrder = new StlPayOrdersDto();
                if (StringUtil.isEmpty(stlPayOrder.getMyAcctNo())) {
                    String acctId = EtrCommonCashPositionDealService.select组合托管户(item.getPortfolioId(), item.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
                    stlPayOrder.setMyAcctNo(acctId);
                }
                stlPayOrder.setOrderId(item.getTradeId());
                stlPayOrder.setCcy(item.getCcy());
                stlPayOrder.setSettleAmt(item.getTradeAmt());
                stlPayOrder.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                stlPayOrder.setPs(item.getPs());
                stlPayOrders.add(stlPayOrder);
                if (CollectionUtils.isNotEmpty(stlPayOrders)
                        && !DictCodeListExt1.投资目的_卖空.getValue().equals(trdProductDeal.getInvAim())) {
                    baseTradeProductDealDto.setStlPayOrdersList(stlPayOrders);
                }

                // 委托代码不为空设置委托编号
                if (StringUtil.isNotEmpty(item.getEntrustId())) {
                    baseTradeProductDealDto.setEntrustId(item.getEntrustId());
                }

                finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
            }
        }


    }

    @Override
    public void doUpdate(CfetsBondDealRegisterQuoteDto entity) {
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        String confirmOk = RequestUtil.getParam("confirmOk");
        if (!StringUtil.equals("revoke", entity.getOperatorType()) && !StringUtil.equals("delete", entity.getOperatorType())) {
//            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
//                //插入旧头寸
//                //删除组合及指令头寸
//                preTrdCommonBaseService.deletePosition(entity.getWorkflowId());
//            }
            // 先删除流程中数据
            this.deleteBussFlowData(entity.getWorkflowId());
            entity.setSoftSkipMode(DictCodeList.是否_否.getValue());
            CfetsBondDealDbDto cfetsBondDealDbDto = prepareEntity(entity, StringUtil.equals(entity.getOperatorType(), "revoke") ? OperationType.REVOKE : OperationType.NEW);
            //登录业务流程
            insertWfl(cfetsBondDealDbDto);
//            RegFinProductDealTotal regTotal=cfetsBondDealDbDto.getRegFinProductDealTotal();
//            if (null!=regTotal && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))
//                    && (StringUtil.equals(regTotal.getPs(),DictCodeList.交易方向_买入.getValue()) || StringUtil.equals(regTotal.getPs(),DictCodeList.交易方向_卖出.getValue()))
//            ){
//                //插入旧头寸
//                //处理组合及指令头寸
//                PreTrdBondInvestTrade002Dto entityPre=prepareEntityPre(cfetsBondDealDbDto);
//                PreTrdCommonDto preTrdCommonDto = new PreTrdCommonDto();
//                preTrdCommonDto.setTrdProductDealPreList(entityPre.getTrdProductDealPreList());
//                preTrdCommonDto.setTrdProductDealAddPreList(entityPre.getTrdProductDealAddPreList());
//                preTrdCommonDto.setIsExistNew(DictCodeList.是否_是.getValue());
//                preTrdCommonBaseService.excutePosition(preTrdCommonDto,false);
//            }
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
                //处理老头寸
                this.oldPositionDeal(cfetsBondDealDbDto, false);
            }
            ProcessMark newEntity;
            //头寸
            SysWflBusinessList sysWflBusinessList = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
            try {
                if (StringUtil.equals(sysWflBusinessList.getProcessState(), ProcessState.BACK.toString())) {
                    PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto, DictCodeListExt3.系统动作_修改.getValue(), DictCodeListExt3.系统动作_修改.getValue(), OperationType.MODIFY,confirmOk));
                } else if (StringUtil.equals(sysWflBusinessList.getProcessState(), ProcessState.NEW.toString())) {
                    PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto, DictCodeListExt3.系统动作_新建.getValue(), DictCodeListExt3.系统动作_新建.getValue(), OperationType.NEW,confirmOk));
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
    public void doComplete(CfetsBondDealRegisterQuoteDto entity, OperationType operationType) {
        CfetsBondDealDbDto cfetsBondDealDbDto = new CfetsBondDealDbDto();
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            cfetsBondDealDbDto = this.getCfetsDealDbDto(entity);
        }
        if (CollectionUtils.isEmpty(cfetsBondDealDbDto.getRegFinProductDealAdds())) {
            cfetsBondDealDbDto = this.prepareEntity(entity, operationType);
        }
        //删除业务流程数据
        deleteBussFlowData(entity.getWorkflowId());
        //登录db
        insert(cfetsBondDealDbDto, operationType);
        //头寸
        try {
            if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {

                if(StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
                    //处理老头寸
                    this.oldPositionDeal(cfetsBondDealDbDto, true);
                }

                //PositionHttpRequest.sendRevoke(cfetsBondDealDbDto.getRegFinProductDealTotal().getpTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),"Bond001");
                String busiType = StringUtil.EMPTY;
                String ps = cfetsBondDealDbDto.getRegFinProductDealTotal().getPs();
                if (StringUtil.equals(ps, BussinessDictCodeList.交易方向_行权回售.getValue())) {
                    busiType = DictCodeListExt3.头寸业务类型_债券行权交易.getValue();
                } else {
                    busiType = DictCodeListExt3.头寸业务类型_现券交易.getValue();
                }
                PositionHttpRequest.dorevoke(cfetsBondDealDbDto.getRegFinProductDealTotal().getpTradeId(), DictCodeListExt1.子系统代码_交易.getValue(), busiType, ps);
            }/*else{
                PositionHttpRequest.send(preparePosition(cfetsBondDealDbDto,operationType));
            }*/
        } catch (PositionException e) {
            e.printStackTrace();
            Message message = PositionUtil.ajaxErrorReturn(e);
            throw new BusinessException("MSG:" + message.getMessage());
        }
        //处理指令执行进度
        insertEtrFinishStatus(cfetsBondDealDbDto, operationType);
    }

    /**
     * 组装数据
     *
     * @param entity
     * @return
     */
    private CfetsBondDealDbDto getCfetsDealDbDto(CfetsBondDealRegisterQuoteDto entity) {
        CfetsBondDealDbDto result = new CfetsBondDealDbDto();
        BeanUtil.copyPropertiesIgnoreNull(result, entity);
        result.setOperationType(entity.getOperatorType());
        WflRegFinProductDealTotal wfldealTotal = wflRegFinProductDealTotalMapper.selectByPrimaryKey(entity.getTotleTradeId());
        WflRegFinProDealTotalAdd wfldealTotalAdd = wflRegFinProDealTotalAddMapper.selectByPrimaryKey(entity.getTotleTradeId());
        List<WflRegTradeFeeTotal> wflfeeTotals = cfetsIbuQuoteBondMapper.listWflRegTradeFeeTotal(entity.getTotleTradeId());
        List<WflRegFinProductDeal> wfldeals = cfetsIbuQuoteBondMapper.listWflRegFinProductDeal(entity.getTotleTradeId());
        List<WflRegFinProductDealAdd> wfldealAdds = cfetsIbuQuoteBondMapper.listWflRegFinProductDealAdd(entity.getTotleTradeId());
        List<WflRegTradeFee> wflfees = cfetsIbuQuoteBondMapper.listWflRegTradeFee(entity.getTotleTradeId());

        RegFinProductDealTotal dealTotal = new RegFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(dealTotal, wfldealTotal);
        RegFinProductDealTotalAdd dealTotalAdd = new RegFinProductDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(dealTotalAdd, wfldealTotalAdd);
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


        result.setRegFinProductDealTotal(dealTotal);
        result.setRegFinProductDealTotalAdd(dealTotalAdd);
        result.setRegTradeFeeTotals(feeTotals);
        result.setRegFinProductDeals(deals);
        result.setRegFinProductDealAdds(dealAdds);
        result.setRegTradeFees(fees);
        result.setRemark(entity.getRemark());

        return result;
    }


    /**
     * 处理指令完成度
     *
     * @param cfetsBondDealDbDto
     * @param operationType
     */
    public void insertEtrFinishStatus(CfetsBondDealDbDto cfetsBondDealDbDto, OperationType operationType) {
        RegFinProductDealTotal regFinProductDealTotal = cfetsBondDealDbDto.getRegFinProductDealTotal();
        List<RegFinProductDeal> regFinProductDeals = cfetsBondDealDbDto.getRegFinProductDeals();
        if (CollectionUtils.isNotEmpty(regFinProductDeals)) {
            List<EtrFinishStatusDto> etrFinishStatusDtos = new ArrayList<>();
            for (RegFinProductDeal regFinProductDeal : regFinProductDeals) {
                EtrFinishStatusDto status = new EtrFinishStatusDto();
                status.setEntrustId(regFinProductDeal.getEntrustId());
                status.setTradeId(regFinProductDealTotal.getTotleTradeId());
                status.setCstpTradeId(regFinProductDealTotal.getTotleTradeId());
                status.setFinishDate(regFinProductDealTotal.getTradeDate());
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(regFinProductDeal.getEntrustId());
                if (etrEntrustAdd != null && StringUtil.isNotEmpty(etrEntrustAdd.getTargetType())
                        && StringUtil.equals(DictCodeListExt3.目标类型_金额.getValue(), etrEntrustAdd.getTargetType())) {
                    status.setEntFinishAmt(regFinProductDeal.getTradeAmt());
                } else {
                    status.setEntFinishAmt(regFinProductDeal.getShareAmt());
                }
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
     * 退回
     *
     * @param entity
     */
    @Override
    public void doBack(CfetsBondDealRegisterQuoteDto entity) {
        //退回冲销头寸
        //PositionHttpRequest.sendRevoke(entity.getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),"Bond001");
        String ps = entity.getPs();
        if (StringUtil.isNotEmpty(ps) && StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))
                && (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())
                || StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())
                || StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue()))
        ) {
           // List<RegFinProductDeal> regFinProductDeals = cfetsIbuQuoteBondMapper.listRegFinProductDeal(entity.getTotleTradeId());
            List<WflRegFinProductDeal> deals = cfetsIbuQuoteBondMapper.listWflRegFinProductDeal(entity.getTotleTradeId());
            //插入旧头寸
            if (CollectionUtils.isNotEmpty(deals)) {
                deals.stream().forEach(deal -> {
                    AllTradeCommonUtil.deleteRegPosition(null, deal.getTradeId());
                });
            }
            //删除业务流程数据
            this.deleteBussFlowData(entity.getWorkflowId());
//            //插入旧头寸
//            preTrdCommonBaseService.deletePosition(entity.getWorkflowId());
        }
    }

    @Override
    public void doDelete(String workflowId) {
        CfetsBondDealRegisterQuoteDto entity = getBussinessEntityByWorkflowId(workflowId);
        String busiType = StringUtil.EMPTY;
        if (StringUtil.equals(entity.getPs(), BussinessDictCodeList.交易方向_行权回售.getValue())) {
            busiType = DictCodeListExt3.头寸业务类型_债券行权交易.getValue();
        } else {
            busiType = DictCodeListExt3.头寸业务类型_现券交易.getValue();
        }
        try {
            WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
            //发送头寸
            ProcessMark processMark = mstWorkflowCommonServiceMapperImpl.getBusinessInfo(workflowId);
            if (StringUtil.isNotEmpty(processMark)
                    && StringUtil.equals(processMark.getStatus(), DictCodeList.数据状态_修改中.getValue())) {
                PositionHttpRequest.doDeleteByWorkFlowId(workflowId, DictCodeListExt1.子系统代码_交易.getValue(), busiType, entity.getPs());
            }
        } catch (PositionException e) {
            Message message = PositionUtil.ajaxErrorReturn(e);
            logger.error(workflowId + ":" + message.getMessage());
            throw new BusinessException("MSG:头寸处理异常!");
        }
        //PositionHttpRequest.sendRevoke(entity.getTotleTradeId(),DictCodeListExt1.子系统代码_交易.getValue(),"Bond001");
//        if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))) {
//            //插入旧头寸
//            preTrdCommonBaseService.deletePosition(workflowId);
//        }
        deleteBussFlowData(workflowId);
    }

    private void insert(CfetsBondDealDbDto entity, OperationType operationType) {
        RegFinProductDealTotal regFinProductDealTotal = entity.getRegFinProductDealTotal();
        String totleTradeId = regFinProductDealTotal.getTotleTradeId();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            regFinProductDealTotal.setTotleTradeId(StringUtil.join(totleTradeId, "R"));
            regFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            regFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            regFinProductDealTotal.setpTradeId(totleTradeId);
            if (StringUtil.isNotEmpty(entity.getRemark())) {
                regFinProductDealTotal.setRemark(entity.getRemark());
            }
        } else {
            regFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            regFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        }
        regFinProductDealTotalMapper.insert(regFinProductDealTotal);
        if (Objects.nonNull(entity.getRegFinProductDealTotalAdd())) {
            entity.getRegFinProductDealTotalAdd().setTotleTradeId(regFinProductDealTotal.getTotleTradeId());
            regFinProductDealTotalAddMapper.insert(entity.getRegFinProductDealTotalAdd());
        }
        List<RegTradeFeeTotal> regTradeFeeTotals = entity.getRegTradeFeeTotals();
        if (CollectionUtils.isNotEmpty(regTradeFeeTotals)) {
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
        //更新原登记数据状态
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            //汇总
            /*RegFinProductDealTotal regFinProduct = new RegFinProductDealTotal();
            regFinProduct.setTotleTradeId(totleTradeId);
            regFinProduct.setRemark(regFinProductDealTotal.getRemark());
            regFinProduct.setIsCancel("Y");
            regFinProductDealTotalMapper.updateByPrimaryKeySelective(regFinProduct);*/

            //明细
            /*RegFinProductDeal regFinProductDeal = new RegFinProductDeal();
            regFinProductDeal.setTotleTradeId(totleTradeId);
            regFinProductDeal.setRemark(regFinProductDealTotal.getRemark());
            regFinProductDeal.setIsCancel("Y");
            regFinProductDealMapper.updateByPrimaryKeySelective(regFinProductDeal);*/

            //汇总
            cfetsIbuQuoteBondMapper.updateRegFinProductDealTotal(totleTradeId, regFinProductDealTotal.getRemark());
            //明细
            cfetsIbuQuoteBondMapper.updateRegFinProductDeal(totleTradeId, regFinProductDealTotal.getRemark());
        }
    }

    /**
     * 组装实体
     *
     * @param entity
     * @return
     */
    public CfetsBondDealDbDto prepareEntity(CfetsBondDealRegisterQuoteDto entity, OperationType operationType) {
        CfetsBondDealDbDto result = new CfetsBondDealDbDto();
        result.setSoftSkipMode(entity.getSoftSkipMode());
        //模式
        String sellPara = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        //设置一：买入先填充融入券指令额度；卖出先扣除本方券指令额度
        //设置二：按指令上券面总额、本方券券面总额、融入券券面总额的比例计算交易上券面总额、本方券券面总额、融入券券面总额
        String sellParaAddRule = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED_ADDRULE");
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            result.setWorkflowId(entity.getWorkflowId());
            result.setOperationType(entity.getOperatorType());
            RegFinProductDealTotal regFinProductDealTotal = regFinProductDealTotalMapper.selectByPrimaryKey(entity.getTotleTradeId());
            regFinProductDealTotal.setRemark(entity.getRemark());
            result.setRegFinProductDealTotal(regFinProductDealTotal);
            result.setRegFinProductDealTotalAdd(regFinProductDealTotalAddMapper.selectByPrimaryKey(entity.getTotleTradeId()));
            result.setRegTradeFeeTotals(cfetsIbuQuoteBondMapper.listRegTradeFeeTotal(entity.getTotleTradeId()));
            result.setRegFinProductDeals(cfetsIbuQuoteBondMapper.listRegFinProductDeal(entity.getTotleTradeId()));
            result.setRegFinProductDealAdds(cfetsIbuQuoteBondMapper.listRegFinProductDealAdd(entity.getTotleTradeId()));
            result.setRegTradeFees(cfetsIbuQuoteBondMapper.listRegTradeFee(entity.getTotleTradeId()));

            //备注信息
            if (StringUtil.isNotEmpty(entity.getRemark())) {
                if (result.getRegFinProductDealTotal() != null) {
                    result.getRegFinProductDealTotal().setRemark(entity.getRemark());
                }
            }
        } else {
            entity.setOuPartyId(entity.getMySide());
            entity.setOuTraderId(entity.getMySideTrader());
            entity.setDeliveryType(entity.getSettleMethod());
            entity.setClearType(entity.getSettleType());
            entity.setUnitCprice(entity.getPrice());
            entity.setYield(entity.getMatureYield());
            Long settleSpreed = null;
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
            //金融产品交易汇总登记
            RegFinProductDealTotal regFinProductDealTotal = new RegFinProductDealTotal();
            BeanUtil.copyPropertiesIgnoreNull(regFinProductDealTotal, entity);
            regFinProductDealTotal.setTradeAmt(entity.getFpriceAmt());
            regFinProductDealTotal.setSettleSpeed(settleSpreed);
            regFinProductDealTotal.setQuoteType(entity.getQuoteStyle());
            regFinProductDealTotal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            regFinProductDealTotal.setFaceValue(entity.getShareAmt());
            regFinProductDealTotal.setShareAmt(ArithmeticUtil.divide(entity.getShareAmt(), new BigDecimal(100), 2));
            regFinProductDealTotal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            regFinProductDealTotal.setSettCcy(regFinProductDealTotal.getCcy());
            regFinProductDealTotal.setDipType(entity.getDistribution());
            regFinProductDealTotal.setBranch(0L);
            if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                tgBusiType = BussinessDictCodeList.平安业务类型_债券类资产买入.getValue();
            }
            if (StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                tgBusiType = BussinessDictCodeList.平安业务类型_债券类资产卖出.getValue();
            }
            if (StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue())) {
                regFinProductDealTotal.setSettleDate(entity.getTheoreticalExerciseDate());
                tgBusiType = DictCodeListExt1.平安业务类型_债券类资产行权.getValue();
            }
            regFinProductDealTotal.setTgBusiType(tgBusiType);
            //金融产品交易汇总登记附属
            RegFinProductDealTotalAdd regFinProductDealTotalAdd = new RegFinProductDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(regFinProductDealTotalAdd, entity);
            regFinProductDealTotalAdd.setCalMode(entity.getCalculateField());
            if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                regFinProductDealTotalAdd.setLendinFpriceAmt(entity.getSaleinFpriceAmt());
                regFinProductDealTotalAdd.setLendinIntAmt(entity.getSaleinIntAmt());
                regFinProductDealTotalAdd.setLendinShareAmt(ArithmeticUtil.divide(entity.getSellAbleNum(), new BigDecimal(100), 2));
            }
            if (StringUtil.equals(ps, DictCodeList.交易方向_卖出.getValue())) {
                regFinProductDealTotalAdd.setLendinShareAmt(ArithmeticUtil.divide(entity.getLendinShareAmt(), new BigDecimal(100), 2));
            }
            //汇总登记费用明细表
            List<RegTradeFeeTotal> regTradeFeeTotals = new ArrayList<>();
            if (null != entity.getDealFeeAmt()) {
                regTradeFeeTotals.add(ibuQuoteBondService.prepareRegTradeFeeTotal(entity.getTotleTradeId(), BussinessDictCodeList.费用类型_交易手续费.getValue(), entity.getDealFeeAmt()));
            }
            if (null != entity.getSettFeeAmt()) {
                //交易手续费
                regTradeFeeTotals.add(ibuQuoteBondService.prepareRegTradeFeeTotal(entity.getTotleTradeId(), BussinessDictCodeList.费用类型_结算手续费.getValue(), entity.getSettFeeAmt()));
            }
            //交易登记明细
            List<RegFinProductDeal> regFinProductDeals = new ArrayList<>();
            List<RegFinProductDealAdd> regFinProductDealAdds = new ArrayList<>();
            List<RegTradeFee> regTradeFees = new ArrayList<>();
            List<PtlAllocationDto> ptlAllocatios = entity.getPtlAllocatios();
            if (CollectionUtils.isNotEmpty(ptlAllocatios)) {
                Map<String, Object> entrustReclas = new HashMap<>();
                if (StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())) {
                    entrustReclas = cfetsQuotedCommonService.isEntrustReclas(entity.getFinprodId(), entity.getPrice(), entity.getTradeDate());
                }
                //处理本方券、融入券
                Map<String, PtlAllocationDto> ptlAllocatioMap = cfetsQuotedCommonService.handOurAndLendinShareAmt(ptlAllocatios, entity.getWorkflowId(), ps, entity.getTotleTradeId());
                for (PtlAllocationDto ptlAllocatio : ptlAllocatios) {
                    RegFinProductDeal regFinProductDeal = new RegFinProductDeal();
                    BeanUtil.copyPropertiesIgnoreNull(regFinProductDeal, regFinProductDealTotal);
                    BussinessCodeParameter codeParameter = getBussinessCodeParameter(entity.getPs());
                    regFinProductDeal.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
                    regFinProductDeal.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
                    regFinProductDeal.setEntrustId(ptlAllocatio.getEntrustId());
                    regFinProductDeal.setPortfolioId(ptlAllocatio.getPortfolioId());
                    regFinProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    regFinProductDeal.setShareAmt(ArithmeticUtil.divide(ptlAllocatio.getShareAmt(), new BigDecimal(100), 2));
                    regFinProductDeal.setFaceValue(ptlAllocatio.getShareAmt());
                    regFinProductDeal.setTradeAmt(ptlAllocatio.getFpriceAmt());
                    regFinProductDeal.setFpriceAmt(ptlAllocatio.getFpriceAmt());
                    regFinProductDeal.setCpriceAmt(ptlAllocatio.getCpriceAmt());
                    regFinProductDeal.setIntAmt(ptlAllocatio.getIntAmt());
                    regFinProductDeal.setTgBusiType(tgBusiType);
                    regFinProductDeal.setSettleSpeed(settleSpreed);
                    if (StringUtil.equals(ptlAllocatio.getBusinessModel(), DictCodeListExt3.业务模式_收取合同约定的现金流.getValue()) && entrustReclas.containsKey("isEntrustReclas") && (boolean) entrustReclas.get("isEntrustReclas")) {
                        regFinProductDeal.setInvAim(DictCodeListExt3.业务模式_出售.getValue());
                    } else {
                        regFinProductDeal.setInvAim(ptlAllocatio.getBusinessModel());
                    }
                    //附属
                    RegFinProductDealAdd regFinProductDealAdd = new RegFinProductDealAdd();
                    regFinProductDealAdd.setTradeId(regFinProductDeal.getTradeId());
                    if (ptlAllocatioMap.containsKey(ptlAllocatio.getEntrustId())) {
                        regFinProductDealAdd.setOurShareAmt(ArithmeticUtil.divide(ptlAllocatioMap.get(ptlAllocatio.getEntrustId()).getOurShareAmt(), new BigDecimal(100), 2));
                        regFinProductDealAdd.setLendinShareAmt(ArithmeticUtil.divide(ptlAllocatioMap.get(ptlAllocatio.getEntrustId()).getLendinShareAmt(), new BigDecimal(100), 2));
                    }
                    regFinProductDealAdd.setActSetteDate(entity.getActSetteDate());
                    if (null != ptlAllocatio.getDealFeeAmt()) {
                        regTradeFees.add(ibuQuoteBondService.prepareRegTradeFee(regFinProductDeal.getTradeId(), BussinessDictCodeList.费用类型_交易手续费.getValue(), ptlAllocatio.getDealFeeAmt()));
                    }
                    if (null != ptlAllocatio.getSettFeeAmt()) {
                        regTradeFees.add(ibuQuoteBondService.prepareRegTradeFee(regFinProductDeal.getTradeId(), BussinessDictCodeList.费用类型_结算手续费.getValue(), ptlAllocatio.getSettFeeAmt()));
                    }
                    regFinProductDeals.add(regFinProductDeal);
                    regFinProductDealAdds.add(regFinProductDealAdd);
                }
            }
            result.setWorkflowId(entity.getWorkflowId());
            result.setOperationType(entity.getOperatorType());
            result.setRegFinProductDealTotal(regFinProductDealTotal);
            result.setRegFinProductDealTotalAdd(regFinProductDealTotalAdd);
            result.setRegTradeFeeTotals(regTradeFeeTotals);
            result.setRegFinProductDeals(regFinProductDeals);
            result.setRegFinProductDealAdds(regFinProductDealAdds);
            result.setRegTradeFees(regTradeFees);
        }
        return result;
    }

    /**
     * 插入业务流程表
     *
     * @param entity
     */
    private void insertWfl(CfetsBondDealDbDto entity) {
        String workflowId = entity.getWorkflowId();
        WflRegFinProductDealTotal wflRegFinProductDealTotal = new WflRegFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDealTotal, entity.getRegFinProductDealTotal());
        wflRegFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflRegFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        wflRegFinProductDealTotal.setWorkflowId(workflowId);
        wflRegFinProductDealTotalMapper.insert(wflRegFinProductDealTotal);
        WflRegFinProDealTotalAdd wflRegFinProDealTotalAdd = new WflRegFinProDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflRegFinProDealTotalAdd, entity.getRegFinProductDealTotalAdd());
        wflRegFinProDealTotalAdd.setWorkflowId(workflowId);
        wflRegFinProDealTotalAddMapper.insert(wflRegFinProDealTotalAdd);
        List<RegTradeFeeTotal> regTradeFeeTotals = entity.getRegTradeFeeTotals();
        if (CollectionUtils.isNotEmpty(regTradeFeeTotals)) {
            regTradeFeeTotals.forEach(item -> {
                WflRegTradeFeeTotal wflRegTradeFeeTotal = new WflRegTradeFeeTotal();
                BeanUtil.copyPropertiesIgnoreNull(wflRegTradeFeeTotal, item);
                wflRegTradeFeeTotal.setWorkflowId(workflowId);
                wflRegTradeFeeTotalMapper.insert(wflRegTradeFeeTotal);
            });
        }
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
        List<RegFinProductDealAdd> regFinProductDealAdds = entity.getRegFinProductDealAdds();
        if (CollectionUtils.isNotEmpty(regFinProductDealAdds)) {
            regFinProductDealAdds.forEach(item -> {
                WflRegFinProductDealAdd wflRegFinProductDealAdd = new WflRegFinProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflRegFinProductDealAdd, item);
                wflRegFinProductDealAdd.setWorkflowId(workflowId);
                wflRegFinProductDealAddMapper.insert(wflRegFinProductDealAdd);
            });
        }
        List<RegTradeFee> regTradeFees = entity.getRegTradeFees();
        if (CollectionUtils.isNotEmpty(regTradeFees)) {
            regTradeFees.forEach(item -> {
                WflRegTradeFee wflRegTradeFee = new WflRegTradeFee();
                BeanUtil.copyPropertiesIgnoreNull(wflRegTradeFee, item);
                wflRegTradeFee.setWorkflowId(workflowId);
                wflRegTradeFeeMapper.insert(wflRegTradeFee);
            });
        }
    }

    /**
     * 删除业务流程数据
     *
     * @param workflowId
     */
    private void deleteBussFlowData(String workflowId) {
        wflRegFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflRegFinProDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflRegTradeFeeTotalMapper.deleteByWorkflowId(workflowId);
        wflRegFinProductDealMapper.deleteByWorkflowId(workflowId);
        wflRegFinProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflRegTradeFeeMapper.deleteByWorkflowId(workflowId);
    }

    /**
     * 处理头寸
     *
     * @param entity
     * @param decisionAct   角色动作
     * @param systemAct     系统动作
     * @param operationType 框架操作
     * @return
     */
    private Map<String, Object> preparePosition(CfetsBondDealDbDto entity, String decisionAct, String systemAct, OperationType operationType, String confirmOk) {
        Map<String, Object> result = new HashMap<>();
        if (operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)) {
            result.put("interfaceType", "Bond001");
            result.put("totalTradeId", entity.getRegFinProductDealTotal().getTotleTradeId());
            result.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
            result.put("userId", SecurityUtil.getLoginUser().getUserName());
        } else {
            String ps = entity.getRegFinProductDealTotal().getPs();
            String settleSpreed = StringUtil.EMPTY;
            result.put("interfaceType", "Bond001");
            result.put("userId", SecurityUtil.getLoginUser().getUserName());
            result.put("totalTradeId", entity.getRegFinProductDealTotal().getTotleTradeId());
            result.put("flowStatus", DictCodeListExt3.流程节点_流程提交.getValue());
            result.put("modelType", DictCodeListExt1.子系统代码_交易.getValue());
            String balanceCheck = DictCodeList.是否_是.getValue();
            if ((StringUtil.isNotEmpty(entity.getSoftSkipMode())
                    && StringUtil.equals(entity.getSoftSkipMode(), DictCodeList.是否_是.getValue()))
                    || StringUtil.isNotEmpty(confirmOk)) {
                balanceCheck = DictCodeList.是否_否.getValue();
            }
            result.put("balanceCheck", balanceCheck);
            if (StringUtil.equals(ps, BussinessDictCodeList.交易方向_行权回售.getValue())) {
                result.put("busiType", DictCodeListExt3.头寸业务类型_债券行权交易.getValue());
            } else {
                result.put("busiType", DictCodeListExt3.头寸业务类型_现券交易.getValue());
            }
            result.put("busiAct", "02");
            result.put("ps", ps);
            if (null != entity.getRegFinProductDealTotal().getSettleSpeed()) {
                if (entity.getRegFinProductDealTotal().getSettleSpeed() == 0L) {
                    settleSpreed = BussinessDictCodeList.清算速度_T加0.getValue();
                } else if (entity.getRegFinProductDealTotal().getSettleSpeed() == 1L) {
                    settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
                } else if (entity.getRegFinProductDealTotal().getSettleSpeed() == 2L) {
                    settleSpreed = BussinessDictCodeList.清算速度_T加2.getValue();
                }
            }
            result.put("settleSpeed", settleSpreed);
            result.put("tradePlace", BussinessDictCodeList.交易场所_银行间.getValue());
            result.put("systemAct", systemAct);
            result.put("decisionAct", decisionAct);
            result.put("cdate", DateUtil.format(entity.getRegFinProductDealTotal().getTradeDate()));
            result.put("ccy", entity.getRegFinProductDealTotal().getCcy());
            result.put("finprodId", entity.getRegFinProductDealTotal().getFinprodId());
            result.put("workFlowId", entity.getWorkflowId());
            if (StringUtil.equals(decisionAct, DictCodeListExt3.系统动作_修改.getValue())) {
                result.put("bTotalTradeId", entity.getRegFinProductDealTotal().getTotleTradeId());
            }
            if (CollectionUtils.isNotEmpty(entity.getRegFinProductDeals())) {
                List<Map<String, Object>> ptlList = new ArrayList<>();
                Map<String, List<RegFinProductDealAdd>> regFinProductDealAddMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(entity.getRegFinProductDealAdds())) {
                    entity.getRegFinProductDealAdds().stream().collect(Collectors.groupingBy(RegFinProductDealAdd::getTradeId, Collectors.toList()))
                            .forEach(regFinProductDealAddMap::put);
                }
                for (RegFinProductDeal regFinProductDeal : entity.getRegFinProductDeals()) {
                    Map<String, Object> pltMap = new HashMap<>();
                    String portfolioId = regFinProductDeal.getPortfolioId();
                    pltMap.put("portfolioId", portfolioId);
                    pltMap.put("tradeId", regFinProductDeal.getTradeId());
                    pltMap.put("etrId", regFinProductDeal.getEntrustId());
                    pltMap.put("cashAcctId", cfetsIbuQuoteBondMapper.getACCTNO(portfolioId, BussinessDictCodeList.资金账户类型_托管账户.getValue()));
                    pltMap.put("secAcctId", cfetsIbuQuoteBondMapper.getSecAcctId(portfolioId, regFinProductDeal.getFinprodId(), regFinProductDeal.getTradeMarket()));
                    pltMap.put("secAmount", ArithmeticUtil.multiply(regFinProductDeal.getShareAmt(), new BigDecimal(100))); //券面总额
                    pltMap.put("cashAmount", regFinProductDeal.getTradeAmt().setScale(4, RoundingMode.DOWN));
                    RegFinProductDealAdd regFinProductDealAdd = regFinProductDealAddMap.get(regFinProductDeal.getTradeId()).get(0);
                    if (StringUtil.equals(ps, BussinessDictCodeList.交易方向_卖出.getValue()) || StringUtil.equals(ps, BussinessDictCodeList.交易方向_行权回售.getValue())) {
                        pltMap.put("ownSecAmount", ArithmeticUtil.multiply(regFinProductDealAdd.getOurShareAmt(), new BigDecimal(100)));
                        pltMap.put("integrateSecAmount", ArithmeticUtil.multiply(regFinProductDealAdd.getLendinShareAmt(), new BigDecimal(100)));
                    }
                    pltMap.put("etrSecAmount", ArithmeticUtil.multiply(etrEntrustMapper.selectByPrimaryKey(regFinProductDeal.getEntrustId()).getEntrustNum(), new BigDecimal(100)));
                    ptlList.add(pltMap);
                }
                result.put("ptlList", ptlList);
            }
        }
        return result;
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
     * 撤销，共外部调用
     *
     * @param totleTradeId
     */
    public void dorevoke(String totleTradeId, String remark, Boolean isNeedDealPosition, String tgBusiType) {
        CfetsBondDealRegisterQuoteDto entity = new CfetsBondDealRegisterQuoteDto();
        entity.setTotleTradeId(totleTradeId);
        CfetsBondDealDbDto cfetsBondDealDbDto = prepareEntity(entity, OperationType.REVOKE);
        cfetsBondDealDbDto.setRemark(remark);
        insert(cfetsBondDealDbDto,OperationType.REVOKE);
        try{
            String busiType = StringUtil.EMPTY;
            String ps = cfetsBondDealDbDto.getRegFinProductDealTotal().getPs();
            if (StringUtil.equals(ps, BussinessDictCodeList.交易方向_行权回售.getValue())) {
                busiType = DictCodeListExt3.头寸业务类型_债券行权交易.getValue();
            } else if (StringUtil.isNotEmpty(tgBusiType) && (StringUtil.equals(DictCodeListExt3.头寸业务类型_质押式回购首期.getValue(), tgBusiType) || StringUtil.equals(DictCodeListExt3.头寸业务类型_质押式回购到期.getValue(), tgBusiType))) {
                busiType = tgBusiType;
            } else {
                busiType = DictCodeListExt3.头寸业务类型_现券交易.getValue();
            }
            if (isNeedDealPosition) {
                PositionHttpRequest.dorevoke(cfetsBondDealDbDto.getRegFinProductDealTotal().getpTradeId(), DictCodeListExt1.子系统代码_交易.getValue(), busiType, ps);
            }
            //处理指令执行进度
            this.insertEtrFinishStatus(cfetsBondDealDbDto, OperationType.REVOKE);
        } catch (PositionException e) {
            e.printStackTrace();
            Message message = PositionUtil.ajaxErrorReturn(e);
            throw new BusinessException("MSG:" + message.getMessage());
        }
    }

    /**
     * 预录入,插入旧头寸重新组装
     *
     * @param input
     */
    public PreTrdBondInvestTrade002Dto prepareEntityPre(CfetsBondDealDbDto cfetsBondDealDbDto) {
        PreTrdBondInvestTrade002Dto entityPre = new PreTrdBondInvestTrade002Dto();
        //List<金融产品交易（预录入）dto>
        List<TrdProductDealPre> trdProductDealPreList = new ArrayList<>();
        entityPre.setTrdProductDealPreList(trdProductDealPreList);
        //List<金融产品交易（预录入）附属表dto>
        List<TrdProductDealAddPre> trdProductDealAddPreList = new ArrayList<>();
        entityPre.setTrdProductDealAddPreList(trdProductDealAddPreList);
        if (!StringUtil.isEmptyList(cfetsBondDealDbDto.getRegFinProductDeals())) {
            for (RegFinProductDeal item : cfetsBondDealDbDto.getRegFinProductDeals()) {
                TrdProductDealPre trdProductDealPre = new TrdProductDealPre();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealPre, item);
                trdProductDealPre.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
                trdProductDealPre.setTradePlat(DictCodeListExt1.交易场所_银行间.getValue());
                trdProductDealPre.setBusiType(DictCodeListExt1.共通业务类型_债券交易预录入.getValue());
                trdProductDealPre.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                trdProductDealPre.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                trdProductDealPreList.add(trdProductDealPre);
                TrdProductDealAddPre trdProductDealAddPre = new TrdProductDealAddPre();
                BeanUtil.copyPropertiesIgnoreNull(trdProductDealAddPre, item);
                trdProductDealAddPre.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
                trdProductDealAddPreList.add(trdProductDealAddPre);
            }
        }
        return entityPre;
    }
}
