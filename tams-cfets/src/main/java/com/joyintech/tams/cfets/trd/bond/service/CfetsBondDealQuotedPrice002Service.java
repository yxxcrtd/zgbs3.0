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

import com.joyintech.background.component.uplink.interbank.business.dto.BondDialogueQuoteDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteBondDto;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.ibu.dto.BondQuoteInfoDto;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.SecInfoModelDto;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.constant.ProcessState;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 现券买卖-对话报价
 */
@Service("vueCfetsBondDealQuotedPrice002Service")
public class CfetsBondDealQuotedPrice002Service extends BaseService<CfetsBondDealDialogueQuoteDto> implements BaseProcessService<CfetsBondDealDialogueQuoteDto> {

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Autowired
    private com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService cfetsQuotedCommonServiceMicro;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonServie;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    //bankuplink微服务
    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;

    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired
    private IbuMainService ibuMainService;

    @Autowired
    private com.joyintech.tams.cfets.trd.bond.service.CfetsQuotedCommonService cfetsQuotedCommonServicevue;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;
    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;

    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;
    /**
     * 设置流程业务说明
     */
    @Override
    public String generateTitle(CfetsBondDealDialogueQuoteDto entity, OperationType type) {
        DecimalFormat df = new DecimalFormat("###,###.00");
        String avaNumStr="";
        if(StringUtil.isNotEmpty(entity.getAvaNum())){
            avaNumStr=df.format(entity.getAvaNum());
        }
        return  "报价日期(" + DateUtil.format(entity.getSettleDate())+")-"+
                "资产简称(" + (entity.getFinprodAbbr()==null?"":entity.getFinprodAbbr()) + ")-"+
                "对手方("+(entity.getCounterName()==null?"":entity.getCounterName())+")-"+
                "报价操作("+  entity.getQuoteOperatorType() +")-" +
                "券面总额("+ avaNumStr +"万元)";
    }

    public AjaxResult checkNotEmpty(CfetsBondDealQuoteCommonDto entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //模式
        String sellPara = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        String ps = entity.getPs();
        if(StringUtil.isEmpty(ps)){
            errorList.add(new ValidMessage("请输入交易方向"));
        }
        if(StringUtil.isEmpty(entity.getFinprodId())){
            errorList.add(new ValidMessage("请输入资产代码"));
        }
        if(StringUtil.equals(ps, DictCodeList.交易方向_买入.getValue())||StringUtil.equals(ps,DictCodeList.交易方向_卖出.getValue())){
            if(StringUtil.isEmpty(entity.getMySide())){
                errorList.add(new ValidMessage("请输入本方"));
            }
            if(StringUtil.isEmpty(entity.getMySideTrader())){
                errorList.add(new ValidMessage("请输入本方交易员"));
            }
            if (StringUtil.isNotEmpty(entity.getMySide())
                    &&StringUtil.isNotEmpty(entity.getMySideTrader())) {
                String userId = joyinbusinessCommonService.findUserByMySideTrader(entity.getMySideTrader(), entity.getMySide());
                if (!StringUtil.isNotEmpty(userId)) {
                    errorList.add(new ValidMessage("选择的本方交易员未与系统用户关联，请先去【用户管理】关联"));
                }
            }
            if(StringUtil.isEmpty(entity.getCounterId())){
                errorList.add(new ValidMessage("请输入对手方"));
            }
            if(StringUtil.isEmpty(entity.getCounterTrader())&&(StringUtil.equals(entity.getQuoteStyle(),"01")||StringUtil.equals(entity.getQuoteStyle(),"02"))){
                errorList.add(new ValidMessage("请输入对手方交易员"));
            }
            if(StringUtil.isEmpty(entity.getCcy())){
                errorList.add(new ValidMessage("请输入币种"));
            }
            if(StringUtil.isEmpty(entity.getSettleSpreedCode())){
                errorList.add(new ValidMessage("请输入清算速度"));
            }
            if(null==entity.getSettleDate()){
                errorList.add(new ValidMessage("请输入结算日期"));
            }
            if(null!=entity.getPrice()&&entity.getPrice().compareTo(BigDecimal.ZERO)<=0){
                errorList.add(new ValidMessage("净价必须大于0"));
            }
            if(null==entity.getShareAmt()){
                errorList.add(new ValidMessage("请输入券面总额"));
            }else{
                if(entity.getShareAmt().compareTo(BigDecimal.ZERO)<=0){
                    errorList.add(new ValidMessage("券面总额必须大于0"));
                }
            }
            if(StringUtil.isEmpty(entity.getSettleMethod())){
                errorList.add(new ValidMessage("请输入结算方式"));
            }
            if(null==entity.getSettleType()){
                errorList.add(new ValidMessage("请输入清算类型"));
            }
            if (StringUtil.isNotEmpty(entity.getCounterId())) {
                String trader = cfetsRepoQuotedPrice004Service.findTraderByPartyId(entity.getCounterId());
                if (!StringUtil.isNotEmpty(trader)) {
                    errorList.add(new ValidMessage("选择的对手方未与系统中交易对手关联，请先去关联"));
                }
            }
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
       /* if(null==entity.getDealFeeAmt()){
            errorList.add(new ValidMessage("请输入交易手续费"));
        }
        if(null==entity.getSettFeeAmt()){
            errorList.add(new ValidMessage("请输入结算服务费"));
        }*/
        if(CollectionUtils.isEmpty(entity.getPtlAllocatios())){
            errorList.add(new ValidMessage("组合分配不可为空"));
        }else{
            BigDecimal shareAmtTotal = BigDecimal.ZERO;BigDecimal dealFeeAmtTotal = BigDecimal.ZERO;BigDecimal settFeeamtTotal = BigDecimal.ZERO;
            for (PtlAllocationDto ptlAllocatio : entity.getPtlAllocatios()) {
                if(null==ptlAllocatio.getShareAmt()){
                    errorList.add(new ValidMessage("请输入组合分配券面总额"));
                    break;
                }
                if(null==ptlAllocatio.getDealFeeAmt()){
                    errorList.add(new ValidMessage("请输入组合分配交易手续费"));
                    break;
                }
                if(null==ptlAllocatio.getSettFeeAmt()){
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
                if(ptlAllocatio.getAvaNum().compareTo(ptlAllocatio.getShareAmt())<0){
                    errorList.add(new ValidMessage("报价指令剩余额度不能小于券面总额！"));
                }
                if(null!=ptlAllocatio.getShareAmt()){
                    BigDecimal wflShareAmtByEntrustId = cfetsQuotedCommonServicevue.getWflShareAmtByEntrustId(ptlAllocatio.getEntrustId());
                    if(null!=wflShareAmtByEntrustId&&wflShareAmtByEntrustId.compareTo(BigDecimal.ZERO)>0&&ArithmeticUtil.doAdd(ptlAllocatio.getShareAmt(),wflShareAmtByEntrustId).compareTo(ptlAllocatio.getAvaNum())>0){
                        errorList.add(new ValidMessage(StringUtil.join("组合分配券面总额不能超过指令剩余额度!流程中占用额度:",wflShareAmtByEntrustId.toString(),"元,指令代码:",ptlAllocatio.getEntrustId())));
                    }
                }
                shareAmtTotal = ArithmeticUtil.doAdd(shareAmtTotal,ptlAllocatio.getShareAmt());
                dealFeeAmtTotal = ArithmeticUtil.doAdd(dealFeeAmtTotal,ptlAllocatio.getDealFeeAmt());
                settFeeamtTotal = ArithmeticUtil.doAdd(settFeeamtTotal,ptlAllocatio.getSettFeeAmt());
                if(StringUtil.equals(entity.getTradeMarket(),BussinessDictCodeList.交易场所_银行间.getValue())){
                    AjaxResult ajaxResultPartyId = cfetsQuotedCommonServicevue.checkpartyId(ptlAllocatio.getEntrustId(), ptlAllocatio.getPortfolioId(), DictCodeListExt3.报价系统区分类型_现券对话报价.getValue());
                    if(StringUtil.isNotEmpty(ajaxResultPartyId.getMessage())){
                        errorList.add(new ValidMessage(ajaxResultPartyId.getMessage()));
                    }
                }
            }
            //券面总额   交易手续费  结算服务费  总和需要相等
            if(CollectionUtils.isEmpty(errorList)){
                if(null!=entity.getShareAmt()&&entity.getShareAmt().compareTo(shareAmtTotal)!=0){
                    errorList.add(new ValidMessage("组合分配券面总额总和需要等于券面总额"));
                }
                if(null!=entity.getDealFeeAmt()&&entity.getDealFeeAmt().compareTo(dealFeeAmtTotal)!=0){
                    errorList.add(new ValidMessage("组合分配交易手续费总和需要等于交易手续费"));
                }
                if(null!=entity.getSettFeeAmt()&&entity.getSettFeeAmt().compareTo(settFeeamtTotal)!=0){
                    errorList.add(new ValidMessage("组合分配结算服务费总和需要等于结算服务费"));
                }
            }
        }
        if(StringUtil.isNotEmpty(entity.getFinprodId())){
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
            if(StringUtil.equals(finProduct.getIsRight(),DictCodeList.是否_是.getValue())){
                Map<String,Object> param = new HashMap<>();
                if (null==entity.getTradeDate()){
                    param.put("sysDate", iPlatformDate.getSysDate());
                }else {
                    param.put("sysDate", entity.getTradeDate());
                }
                param.put("finprodId",entity.getFinprodId());
                //未来有行权计划
                if(cfetsIbuQuoteBondMapper.countBondHasEexrPlan(param)>0){
                    if(null==entity.getPrice()&&null==entity.getMatureYield()&&null==entity.getExerYield()){
                        errorList.add(new ValidMessage("请输入净价或者到期收益率或者行权收益率"));
                    }
                }
            }else{
                if(null==entity.getPrice()&&null==entity.getMatureYield()){
                    errorList.add(new ValidMessage("请输入净价或者到期收益率"));
                }
            }
            //锚定值不可为空
            if(StringUtil.isEmpty(entity.getCalculateField())){
                errorList.add(new ValidMessage("请输入锚定值(净价或者到期收益率或者行权收益率)"));
            }
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }


    public List<ValidMessage> valid(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto){
        // 验证信息
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        String isDzh = cfetsBondDealDialogueQuoteDto.getIsDzh();
        //该报价是否在流程中
        if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())){
            validList.addAll(isInWflProcess(cfetsBondDealDialogueQuoteDto.getQuoteId(),cfetsBondDealDialogueQuoteDto.getPageId(),cfetsBondDealDialogueQuoteDto.getWorkflowId()));
        }
        if (!StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "refuse")) {
            if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getPortfolioId())&&StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())
                    && !StringUtil.equals(isDzh,"Y")) {
                validList.add(new ValidMessage("portfolioId", this.getMessage("FW_E_0006", this.getMessage("CfetsBondDealQuotedPrice002.portfolioName"))));
            }
        }

        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "save")
                || StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "submit")) {
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getAvaNum())) {
                if (cfetsBondDealDialogueQuoteDto.getAvaNum().compareTo(BigDecimal.ZERO) <= 0) {
                    validList.add(new ValidMessage("avaNum", "券面总额必须大于0！"));
                }
                // 券面需要为1000的整数倍（接口规则要求）
                String numStr = ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(),
                        BigDecimal.valueOf(10)).toPlainString();
                if (Double.parseDouble(numStr) % 1.0d > 0) {
                    validList.add(new ValidMessage("avaNum", this.getMessage("QUOTE_UPLOAD_038_2", cfetsBondDealDialogueQuoteDto.getAvaNum().toPlainString())));
                }
            }
            if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())){
                CfetsBondDealDialogueQuoteDto tradeStatusDto =  getBondDialogueQuote(cfetsBondDealDialogueQuoteDto.getQuoteId(), null);
                if(org.apache.commons.codec.binary.StringUtils.equals(tradeStatusDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已成交.getValue()) ||
                        org.apache.commons.codec.binary.StringUtils.equals(tradeStatusDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已终止.getValue())){
                    validList.add(new ValidMessage("tradeStatus", this.getMessage("CFETS_COMMON_E_040")));
                }
            }
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "new")) {
                //多组合
                if(StringUtil.equals(isDzh,"Y")){
                    for (CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
                        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDoneDto = new CfetsBondDealDialogueQuoteDto();
                        cfetsBondDealDialogueQuoteDoneDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
                        cfetsBondDealDialogueQuoteDoneDto.setEntrustId(cfetsBondDealDialogueQuote.getEntrustId());
                        cfetsBondDealDialogueQuoteDoneDto.setAvaNum(cfetsBondDealDialogueQuote.getAvaNum());
                        cfetsBondDealDialogueQuoteDoneDto.setEntrustAvaNum(cfetsBondDealDialogueQuote.getEntrustNum());
                        cfetsBondDealDialogueQuoteDoneDto.setWorkflowId(cfetsBondDealDialogueQuoteDto.getWorkflowId());
                        指令额度判断(cfetsBondDealDialogueQuoteDoneDto,isDzh,"new",validList);
                        if(!StringUtil.isEmptyList(validList)){
                            return validList;
                        }
                    }
                }
                else{
                    if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())) {
                        指令额度判断(cfetsBondDealDialogueQuoteDto,isDzh,"new",validList);
                    }
                    else {
                        if (!StringUtil.equals(
                                CacheUtil.getSysParam("ibuFlag_answerQuoteWithOutEntrust"), "02")) {
                            validList.add(
                                    new ValidMessage("entrustId", this.getMessage("CFETS_COMMON_E_009")));
                        }
                    }
                }
            }
        }
        //多组合
        if(StringUtil.equals(isDzh,"Y")){
            if (!StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "refuse")) {
                if (StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())) {
                    validList.add(new ValidMessage("",getMessage("组合分配中必须存在至少一条指令")));
                    return validList;
                }else{
                    Map<String,Object> map = new HashMap<>();
                    BigDecimal avaNumTotal = new BigDecimal(0);
                    String entrustIdList = StringUtil.EMPTY;
                    for (CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
                        entrustIdList = entrustIdList+cfetsBondDealDialogueQuote.getEntrustId()+",";
                        if(null==cfetsBondDealDialogueQuote.getAvaNum()){
                            validList.add(new ValidMessage("",getMessage("组合分配中券面总额（万元）不可为空")));
                            return validList;
                        }
                        avaNumTotal = ArithmeticUtil.doAdd(avaNumTotal,cfetsBondDealDialogueQuote.getAvaNum());
                        cfetsBondDealDialogueQuoteDto.setEntrustId(cfetsBondDealDialogueQuote.getEntrustId());
                        验证委托状态(cfetsBondDealDialogueQuoteDto, validList);
                        //指令委托金额带出 下面计算剩余额度 需要
                        cfetsBondDealDialogueQuote.setEntrustAmt(cfetsBondDealDialogueQuoteDto.getEntrustAmt());
                        cfetsBondDealDialogueQuoteDto.setEntrustId(StringUtil.EMPTY);
                        if (null != validList && !StringUtil.isEmptyList(validList)) {
                            return validList;
                        }
                    }
                    if(avaNumTotal.compareTo(cfetsBondDealDialogueQuoteDto.getAvaNum())!=0){
                        validList.add(new ValidMessage("",getMessage("报价券面总额必须和各指令分配后券面总额汇总值相等")));
                    }
                    //汇总提交是否满足
                    map.put("entrustIdList",entrustIdList);
                    AjaxResult ar = checkEntrustList(map);
                    if(!StringUtil.isEmptyList(ar.getErrorList())){
                        return ar.getErrorList();
                    }
                }
            }
        }
        else{
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())) {
                验证委托状态(cfetsBondDealDialogueQuoteDto,validList);
            }
            else{
                // 委托为空的场合只有新建模式且系统参数为软校验
                if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "new")
                        && !StringUtil.equals(CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"), "02")) {
                    validList.add(new ValidMessage("entrustId", this.getMessage("CFETS_COMMON_E_004")));
                }
            }
        }
        // 对话报价-回复
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "dialogueResponse")) {
            // 需要区分成交和拒绝，拒绝只需要判断状态 是否变化
            // 查询报价信息检查状态是否变化
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
                CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDtoNew =  getBondDialogueQuote(cfetsBondDealDialogueQuoteDto.getQuoteId(), null);
                com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto bondQuoteInfoDto = new com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto();
                BeanUtil.copyPropertiesIgnoreNull(bondQuoteInfoDto,
                        cfetsBondDealDialogueQuoteDtoNew);
                if (!StringUtil.equals(bondQuoteInfoDto.getAnswerFlag(), "01")) {
                    validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_008")));
                }
            }
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")) {
                //多组合
                if(StringUtil.equals(isDzh,"Y")){
                    for (CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {
                        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDoneDto = new CfetsBondDealDialogueQuoteDto();
                        cfetsBondDealDialogueQuoteDoneDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
                        cfetsBondDealDialogueQuoteDoneDto.setEntrustId(cfetsBondDealDialogueQuote.getEntrustId());
                        cfetsBondDealDialogueQuoteDoneDto.setAvaNum(cfetsBondDealDialogueQuote.getAvaNum());
                        cfetsBondDealDialogueQuoteDoneDto.setEntrustAmt(cfetsBondDealDialogueQuote.getEntrustAmt());
                        cfetsBondDealDialogueQuoteDoneDto.setWorkflowId(cfetsBondDealDialogueQuoteDto.getWorkflowId());
                        指令额度判断(cfetsBondDealDialogueQuoteDoneDto,isDzh,"done",validList);
                        if(!StringUtil.isEmptyList(validList)){
                            return validList;
                        }
                    }
                }
                else{
                    if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())) {
                        指令额度判断(cfetsBondDealDialogueQuoteDto,isDzh,"done",validList);
                    }
                    else {
                        if (!StringUtil.equals(
                                CacheUtil.getSysParam("ibuFlag_answerQuoteWithOutEntrust"), "02")) {
                            validList.add(
                                    new ValidMessage("entrustId", this.getMessage("CFETS_COMMON_E_009")));
                        }
                    }
                }
            }
        }
        else {
            // 7) 需要验证交易的偏离度是否超参数值。交易偏离度=（交易净价-上一日中债估值净价）/上一日中债估值净价*100%；上一日净价为小于平台日的最新净价
            if (!StringUtil.equals(CacheUtil.getSysParam("ibuFlag_dealDeviation"), "02")
                    && StringUtil.isNotEmpty(CacheUtil.getSysParam("ibuVar_dealDeviationVal"))){
                checkDealDeviation(cfetsBondDealDialogueQuoteDto, validList, null, true);
            }

            if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getCalculateField())) {
                validList.add(
                        new ValidMessage(this.getMessage("CFETS_COMMON_E_011")));
            }

            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getAssetTypeOne())) {
                if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getAssetTypeOne(),
                        BussinessDictCodeList.平安资产一级分类_资产支持证券.getValue())) {
                    if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getParValue())) {
                        validList.add(new ValidMessage("parValue", this.getMessage("FW_E_0006",
                                this.getMessage("CfetsBondDealQuotedPrice002.perHundAmt"))));
                    }
                    if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getParValueAmt())) {
                        validList.add(new ValidMessage("parValueAmt", this.getMessage("FW_E_0006",
                                this.getMessage("CfetsBondDealQuotedPrice002.prinAmt"))));
                    }
                    if (!StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                            BussinessDictCodeList.计算选择方式_净价.getValue())) {
                        validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_036")));
                    }
                }
            }

            /*if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getIsRight())
                && StringUtil.equals(cfetsBondDealDialogueQuoteDto.getIsRight(), "Y")) {
                if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getExerYield())) {
                    validList.add(new ValidMessage("exerYield", this.getMessage("FW_E_0006",
                        this.getMessage("CfetsBondDealQuotedPrice002.exec"))));
                }
            }*/

            /*if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getFinprodId())&&null!=cfetsBondDealDialogueQuoteDto.getSettleDate()){
                Map<String,Object> param = new HashMap<>();
                param.put("finprodId",cfetsBondDealDialogueQuoteDto.getFinprodId());
                param.put("tradeDate", DateUtil.format(cfetsBondDealDialogueQuoteDto.getSettleDate()));
                if(StringUtil.equals(ibuQuoteBondService.isExerYieldDivShow(param),BussinessDictCodeList.是否_是.getValue())
                        &&null==cfetsBondDealDialogueQuoteDto.getExerYield()){
                    validList.add(new ValidMessage("exerYield", this.getMessage("FW_E_0006",
                            this.getMessage("CfetsBondDealQuotedPrice002.exec"))));
                }
            }*/

            if (!StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustAvaAmt"), "02")){
                if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())){
//            		CfetsBondDealDialogueQuoteDto entrustDto = cfetsBondDealQuotedPrice002Service.queryEntrustInfoByEntrustId(cfetsBondDealDialogueQuoteDto.getEntrustId());
//        			BigDecimal entrustAmt = entrustDto.getEntrustNum() == null ? BigDecimal.ZERO : entrustDto.getEntrustNum().divide(new BigDecimal(100));//券面总额/100
//        			BigDecimal usedShareAmt = cfetsBondDealQuotedPrice002Service.checkUsedShareAmt(cfetsBondDealDialogueQuoteDto);//已占用额度
//        			if (usedShareAmt.add(cfetsBondDealDialogueQuoteDto.getAvaNum().multiply(new BigDecimal(10000)).divide(new BigDecimal(100))).compareTo(entrustAmt) > 0) {
//        				validList.add(new ValidMessage("avaNum",this.getMessage("CFETS_COMMON_E_010")));
//        			}
                    checkEntrustAvaNum(cfetsBondDealDialogueQuoteDto, validList, null, true);
                }
            }

            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "marketResponse")) {
                if (StringUtil.isEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())) {
                    if (!StringUtil.equals(CacheUtil.getSysParam("ibuFlag_answerQuoteWithOutEntrust"), "02")) {
                        validList.add(new ValidMessage("entrustId", this.getMessage("CFETS_COMMON_E_009")));
                    }
                }

                // 回复量不能超请求报价的券面总额
                if (cfetsBondDealDialogueQuoteDto.getResponseAmt() != null) {
                    if (cfetsBondDealDialogueQuoteDto.getAvaNum() != null) {
                        if (cfetsBondDealDialogueQuoteDto.getResponseAmt().compareTo(
                                cfetsBondDealDialogueQuoteDto.getAvaNum()) > 0) {
                            validList.add(
                                    new ValidMessage("responseAmt", this.getMessage("CFETS_COMMON_E_013")));
                        }
                    }
                    if (cfetsBondDealDialogueQuoteDto.getResponseAmt().compareTo(BigDecimal.ZERO) <= 0) {
                        validList.add(new ValidMessage("avaNum", "回复量必须大于0！"));
                    }
                    // 券面需要为1000的整数倍（接口规则要求）
                    String numStr = ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getResponseAmt(),
                            BigDecimal.valueOf(10)).toPlainString();
                    if (Double.parseDouble(numStr) % 1.0d > 0) {
                        validList.add(new ValidMessage("responseAmt", "回复量" + cfetsBondDealDialogueQuoteDto.getResponseAmt().toPlainString() + "需要为1000元的整数倍"));
                    }
                }
            }
        }

        return validList;
    }

    private void 验证委托状态(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto,List<ValidMessage> validList){
        String entrustId = cfetsBondDealDialogueQuoteDto.getEntrustId();
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        cfetsBondDealDialogueQuoteDto.setEntrustAmt(etrEntrust.getEntrustAmt() == null ? etrEntrust.getEntrustNum().multiply(new BigDecimal(100)) : etrEntrust.getEntrustAmt());
        if (etrEntrust != null) {
            if (StringUtil.equals(etrEntrust.getEntrustStatus(),
                    BussinessDictCodeList.委托状态_已关闭.getValue())
                    || StringUtil.equals(etrEntrust.getEntrustStatus(),
                    BussinessDictCodeList.委托状态_已撤回.getValue())) {
                validList.add(new ValidMessage( this.getMessage("CFETS_COMMON_E_007")+"委托编号为："+ entrustId));
            }
            // 校验委托完成度
            if (!StringUtil.equals(etrEntrust.getEntrustStatus(),
                    BussinessDictCodeList.委托状态_已接收.getValue())) {
                validList.add(new ValidMessage( this.getMessage("CFETS_COMMON_E_002")+"委托编号为："+ entrustId));
            }
            // 验证委托价格
            if (!StringUtil.equals(CacheUtil.getSysParam("ibuFlag_bondOverEntrustPrice"), "02")) {
                checkCalcFieldEntrust(cfetsBondDealDialogueQuoteDto, validList, null, true);
            }
        }
    }

    private void 指令额度判断(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto,String isDzh,String quotaPageModel,List<ValidMessage> validList){
        if (cfetsBondDealDialogueQuoteDto.getAvaNum() != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("entrustId", cfetsBondDealDialogueQuoteDto.getEntrustId());
            map.put("isDzh",isDzh);
            if (StringUtil.isNotBlank(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
                map.put("quoteId", cfetsBondDealDialogueQuoteDto.getQuoteId());
            }
            //报价本身委托完成度表登录的是券面总额  需要除以100
            BigDecimal dealedAmt = queryEntrustDealedAmt(
                    map);
            //查询一下指令本身的完成度 登录的是百元券 不考慮
            /*BigDecimal dealedAmt2 = cfetsBondDealQuotedPrice002Service.queryEntrustDealedAmt2(
                    map);
            if(null!=dealedAmt2){
                dealedAmt = ArithmeticUtil.doAdd(ArithmeticUtil.divide(dealedAmt,new BigDecimal(100),2),dealedAmt2);
            }*/
            //验证下流程中
            //去掉本身流程中的数据  如退回再提交
            map.put("workflowId",cfetsBondDealDialogueQuoteDto.getWorkflowId());
            BigDecimal wflEntrustDealedAmt = getWflEntrustDealedAmt(map);
            BigDecimal wflAmt = null==wflEntrustDealedAmt?BigDecimal.ZERO:wflEntrustDealedAmt;
            BigDecimal avaAmt = BigDecimal.ZERO;
            if(StringUtil.equals(quotaPageModel,"new")&&StringUtil.equals(isDzh,"Y")){
                avaAmt = ArithmeticUtil.subtract(ArithmeticUtil.subtract(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getEntrustAvaNum(), new BigDecimal(10000)),dealedAmt),wflAmt);
            }else{
                if(null==cfetsBondDealDialogueQuoteDto.getEntrustAmt()&&StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())){
                    BigDecimal entrustNum = getEtrEntrust(cfetsBondDealDialogueQuoteDto.getEntrustId()).getEntrustNum();
                    avaAmt = ArithmeticUtil.subtract(ArithmeticUtil.subtract(
                            ArithmeticUtil.multiply(entrustNum,new BigDecimal(100)), dealedAmt),wflAmt);
                }else{
                    avaAmt = ArithmeticUtil.subtract(ArithmeticUtil.subtract(
                            ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getEntrustAmt(),new BigDecimal(10000)), dealedAmt),wflAmt);
                }
            }
            if (ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(10000)).compareTo(avaAmt) > 0) {
                String wflTip = wflAmt.compareTo(BigDecimal.ZERO)!=0?"流程中占用额度:"+wflAmt:"";
                validList.add(new ValidMessage("responseAmt",
                        this.getMessage("CFETS_COMMON_E_010") + avaAmt.toPlainString()+wflTip));
            }
        }
    }

    /**
     * 根据委托代码查询委托基本信息
     * 
     * @param entrustId 委托代码
     * @return 委托基本信息
     * @see
     * @since 1.0
     */
    public CfetsBondDealDialogueQuoteDto queryEntrustInfoById(String entrustId) {
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        if (StringUtil.isNotEmpty(entrustId)) {
            Map<String, Object> param = new HashMap<String, Object>();
            // param.put("platDate", iPlatformDate.getSysDateStr());
            param.put("loginUserId", LoginUserUtil.getUserName());
            param.put("entrustId", entrustId);
            List<com.joyintech.service.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto> list = cfetsBondDealQuotedPriceService.queryEntrustOrderList(param);
            /*List<CfetsBondDealEntrustOrderDto> list = restTemplate.postForList(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/queryEntrustOrderList",
                param, CfetsBondDealEntrustOrderDto.class);*/
            if (!StringUtil.isEmptyList(list)) {
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, list.get(0));
            }
        }
        return cfetsBondDealDialogueQuoteDto;
    }

    /**
     * 根据委托代码查询委托基本信息
     * 
     * @param entrustId 委托代码
     * @return 委托基本信息
     * @see
     * @since 1.0
     */
    public CfetsBondDealDialogueQuoteDto queryEntrustInfoByEntrustId(String entrustId) {
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = this.queryEntrustInfoById(
            entrustId);

        // 清算速度
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(), "0")) {
            cfetsBondDealDialogueQuoteDto.setSettleSpreed(
                BussinessDictCodeList.清算速度_T加0.getValue());
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(), "1")) {
            cfetsBondDealDialogueQuoteDto.setSettleSpreed(
                BussinessDictCodeList.清算速度_T加1.getValue());
        }
        // 交割日期
        Date platDate = iPlatformDate.getSysDate();
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            platDate = cfetsQuotedCommonService.getNextWorkDay(platDate);
        }
        cfetsBondDealDialogueQuoteDto.setSettleDate(platDate);
        // 结算方式
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleMethod())) {
            cfetsBondDealDialogueQuoteDto.setSettleMethod(
                BussinessDictCodeList.结算方式_券款对付.getValue());
        }
        // 清算类型
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleType())) {
            cfetsBondDealDialogueQuoteDto.setSettleType(
                BussinessDictCodeList.清算类型_全额清算.getValue());
        }
        // 结算币种
        if (!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleCcy())) {
            cfetsBondDealDialogueQuoteDto.setSettleCcy(BussinessDictCodeList.币种_人民币.getValue());
        }

        // 调用算法包计算金额要素(委托二级指令应该是能带出这些字段的)
        AjaxResult ar = this.calcAmt(cfetsBondDealDialogueQuoteDto);
        if (ar != null && ar.getData() != null) {
            CfetsBondDealDialogueQuoteDto calcDto = (CfetsBondDealDialogueQuoteDto)ar.getData();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, calcDto);
        }

        return cfetsBondDealDialogueQuoteDto;
    }

    /**
     * 现券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return boolean 是否成功
     */
    public boolean save(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDtoMicro = JsonUtils.json2Object(JsonUtils.object2Json(cfetsBondDealDialogueQuoteDto),com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto.class);
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(),
            "marketResponse")) {
            return cfetsBondDealQuotedPriceService.sendBondRequestQuoteNewReply(cfetsBondDealDialogueQuoteDtoMicro);
            /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteNewReply",
                cfetsBondDealDialogueQuoteDto, boolean.class);*/
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(),
            "dialogueResponse")) {
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")) {
                return cfetsBondDealQuotedPriceService.sendBondDialogueQuoteDeal(cfetsBondDealDialogueQuoteDtoMicro);
                /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteDeal",
                    cfetsBondDealDialogueQuoteDto, boolean.class);*/
            }
            else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(),
                "refuse")) {
                return cfetsBondDealQuotedPriceService.sendBondDialogueQuoteRefuse(cfetsBondDealDialogueQuoteDtoMicro);
               /* return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteRefuse",
                    cfetsBondDealDialogueQuoteDto, boolean.class);*/
            }
            return false;
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "new")) {
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "save")) {
                return cfetsBondDealQuotedPriceService.insertBondDialogueQuote(cfetsBondDealDialogueQuoteDtoMicro);
                /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/insertBondDialogueQuote",
                    cfetsBondDealDialogueQuoteDto, boolean.class);*/
            }
            else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(),
                "submit")) {
                return cfetsBondDealQuotedPriceService.sendNewBondDialogueQuote(cfetsBondDealDialogueQuoteDtoMicro);
                /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/sendNewBondDialogueQuote",
                    cfetsBondDealDialogueQuoteDto, boolean.class);*/
            }

        }
        return false;
    }

    /**
     * 现券对话报价查询<br />
     * 根据主键查询对话报价信息
     * 
     * @param quoteId String 报价编号
     * @return BondDialogueQuoteDto
     */
    public CfetsBondDealDialogueQuoteDto getBondDialogueQuote(String quoteId,String statusValid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("quoteId", quoteId);
        map.put("statusValid", statusValid);
        map.put("loginUserId", LoginUserUtil.getUserName());
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto bondDialogueQuote = cfetsBondDealQuotedPriceService.getBondDialogueQuote(map);
        BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,bondDialogueQuote);
        return cfetsBondDealDialogueQuoteDto;
        /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/getBondDialogueQuote",
            map, CfetsBondDealDialogueQuoteDto.class);*/
    }

    /**
     * 调用算法包计算金额要素
     * 
     * @param cfetsBondDealDialogueQuoteDto 入参
     * @return CfetsBondDealDialogueQuoteDto
     * @see
     * @since 1.0
     */
    public AjaxResult calcAmt(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        AjaxResult ajaxResult = new AjaxResult();

        // 校验输入参数
        /*List<ValidMessage> errorList = this.validCalcAmt(cfetsBondDealDialogueQuoteDto);
        if (!StringUtil.isEmptyList(errorList)) {
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }*/
        
        // add by etliu 20210108 start
        if (StringUtil.isBlank(cfetsBondDealDialogueQuoteDto.getCalculateField())) {
            cfetsBondDealDialogueQuoteDto.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
        }
        // add by etliu 20210108 end


        if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getPrice())){
            com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto1 = new com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto1,cfetsBondDealDialogueQuoteDto);
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,cfetsBondDealQuotedPriceService.calcAmt(cfetsBondDealDialogueQuoteDto1));
            /*cfetsBondDealDialogueQuoteDto = restTemplate.postForObject(
                    ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/calcAmt",
                    cfetsBondDealDialogueQuoteDto, CfetsBondDealDialogueQuoteDto.class);*/
        }
        ajaxResult.setData(cfetsBondDealDialogueQuoteDto);
        return ajaxResult;
    }

    /**
     * 1.7计算按钮点击事件 检查输入参数是否合法
     * @param cfetsBondDealDialogueQuoteDto
     * @return
     */
    private List<ValidMessage> validCalcAmt(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        List<ValidMessage> validMessageList = new ArrayList<ValidMessage>();

        // 2.资产代码
        if (StringUtil.isBlank(cfetsBondDealDialogueQuoteDto.getFinprodId())) {
            ValidMessage validMessage = new ValidMessage("finprodId",
                getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.finprodId")));
            validMessageList.add(validMessage);
        }
        else {
            // d.获取债券基本资料信息
            SecInfoModelDto secInfoModelDto = restTemplate.getForObject(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/getSecInfoModel?finprodId="
                                                                        + cfetsBondDealDialogueQuoteDto.getFinprodId(),
                SecInfoModelDto.class);
            // 1.交割日
            if (cfetsBondDealDialogueQuoteDto.getSettleDate() == null) {
                ValidMessage validMessage = new ValidMessage("settleDate",
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.settleDate")));
                validMessageList.add(validMessage);
            }
            // 3.劵面总额
            if (cfetsBondDealDialogueQuoteDto.getAvaNum() == null) {
                ValidMessage validMessage = new ValidMessage("tradeAmt",
                    getMessage("BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.tradeAmt")));
                validMessageList.add(validMessage);
            }

            if (StringUtil.isBlank(cfetsBondDealDialogueQuoteDto.getCalculateField())) {
                ValidMessage validMessage = new ValidMessage(getMessage("BUSS_COM_E_0038",
                    getMessage("TrdBondInvestTrade002.calculateField")));
                validMessageList.add(validMessage);
            }
            else {
                switch (cfetsBondDealDialogueQuoteDto.getCalculateField()) {
                    case "01":
                        if (cfetsBondDealDialogueQuoteDto.getPrice() == null) {
                            ValidMessage validMessage = new ValidMessage("unitCprice",
                                getMessage("BUSS_COM_E_0038",
                                    getMessage("TrdBondInvestTrade002.unitCprice")));
                            validMessageList.add(validMessage);
                        }
                        break;
                    case "04":
                        if (cfetsBondDealDialogueQuoteDto.getFpriceAmt() == null) {
                            ValidMessage validMessage = new ValidMessage("fpriceAmt", getMessage(
                                "BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.fpriceAmt")));
                            validMessageList.add(validMessage);
                        }
                        break;
                    case "02":
                        if (cfetsBondDealDialogueQuoteDto.getMatureYield() == null) {
                            ValidMessage validMessage = new ValidMessage("yield", getMessage(
                                "BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.yield")));
                            validMessageList.add(validMessage);
                        }
                        break;
                    case "03":
                        if (cfetsBondDealDialogueQuoteDto.getExerYield() == null) {
                            ValidMessage validMessage = new ValidMessage("exerYield", getMessage(
                                "BUSS_COM_E_0038", getMessage("TrdBondInvestTrade002.exerYield")));
                            validMessageList.add(validMessage);
                        }
                        // 根据行权收益率反算其他值的情况，若下一行权日净价未维护，则提示错误消息
                        else {
                            try {
                                if (secInfoModelDto != null
                                    && secInfoModelDto.getNextExeDate() != null
                                    && secInfoModelDto.getNextExercisePrice() == null) {
                                    ValidMessage validMessage = new ValidMessage("exerYield",
                                        getMessage("TRD_E_0015"));
                                    validMessageList.add(validMessage);
                                }
                            }
                            catch (Exception e) {
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            // 债券到期日<=交割日时 // Sonar Fix modify by huwei 220608
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleDate()) && StringUtil.isNotEmpty(secInfoModelDto)
                && StringUtil.isNotEmpty(secInfoModelDto.getEndDate())) {
                if (DateUtil.compareDate(secInfoModelDto.getEndDate(),
                    cfetsBondDealDialogueQuoteDto.getSettleDate()) <= 0) {
                    validMessageList.add(new ValidMessage("settleDate",
                        this.getMessage("BUSS_COM_E_0035",
                            this.getMessage("CfetsBondDealQuotedPrice002.settleDate"),
                            this.getMessage("CfetsBondDealQuotedPrice002.assetMdate"))));
                }
            }
            // 画面.交割日<债券起息日 // Sonar Fix modify by huwei 220608
            if (StringUtil.isNotEmpty(secInfoModelDto) && StringUtil.isNotEmpty(secInfoModelDto.getStartDate())
                && StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleDate())) {
                if (DateUtil.compareDate(cfetsBondDealDialogueQuoteDto.getSettleDate(),
                    secInfoModelDto.getStartDate()) < 0) {
                    validMessageList.add(new ValidMessage("settleDate",
                        this.getMessage("BUSS_COM_E_0004",
                            this.getMessage("CfetsBondDealQuotedPrice002.settleDate"),
                            this.getMessage("CfetsBondDealQuotedPrice002.assetVdate"))));
                }
            }
        }

        return validMessageList;
    }

    /**
     * 查询委托已完成额度
     * 
     * @param map 入参
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal queryEntrustDealedAmt(Map<String, Object> map) {
        return cfetsBondDealQuotedPriceService.queryEntrustDealedAmt(map);
        /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/queryEntrustDealedAmt",
            map, BigDecimal.class);*/
    }

    /**
     * 查询债券最新估值
     * @param finprodMarketId
     * @return
     */
    public MstBondValuationInfo queryLastBondValuation(String finprodMarketId) {
        Map<String, Object> map = new HashMap<>();
        map.put("finprodMarketId", finprodMarketId);
        map.put("platDate", iPlatformDate.getSysDateStr());
        return cfetsBondDealQuotedPriceService.queryLastBondValuation(map);
        /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/queryLastBondValuation",
            map, MstBondValuationInfo.class);*/
    }

    /**
     * 校验委托是否可以进行报价<br/>
     * <功能详细描述>
     * 
     * @param param 参数
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public AjaxResult checkEntrust(Map<String, Object> param) {
        AjaxResult ar = new AjaxResult();
        // 验证信息
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        String entrustId = (String)param.get("entrustId");
        if (StringUtil.isNotEmpty(entrustId)) {
            CfetsBondDealDialogueQuoteDto dto = this.queryEntrustInfoByEntrustId(entrustId);
            if (dto != null) {
                // 委托已接收状态切完成度小于100%
                if (StringUtil.equals(dto.getEntrustStatus(),
                    BussinessDictCodeList.委托状态_已接收.getValue())
                    && dto.getTransactioncom().compareTo(new BigDecimal(100)) >= 0) {
                    validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_023")));
                }
            }
        }
        ar.setData(this.getMessage("COMMON_C_0001", this.getMessage("SysCommon.save")));
        ar.setErrorList(validList);
        return ar;
    }

    
    /**
     * 校验委托是否可以进行报价<br/>
     * <功能详细描述>
     * 
     * @param param 参数
     * @return 处理结果
     * @see
     * @since 1.0
     */
    public AjaxResult checkEntrustList(Map<String, Object> param) {
        AjaxResult ar = new AjaxResult();
        // 验证信息
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
//        String entrustId = (String)param.get("entrustId");
        List <String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, (String)param.get("entrustIdList"));
        if (!StringUtil.isEmptyList(entrustIdList)) {
        	String finprodId="";String ps ="";String settleSpreed ="";String myside="";
        	boolean finprodIdFlag=true; boolean psFlag =true; boolean  settleSpreedFlag =true; boolean mysideFlag=true;
        	for(String entrustId: entrustIdList){
        	    if(StringUtils.isNotEmpty(entrustId)){
                    CfetsBondDealDialogueQuoteDto dto = this.queryEntrustInfoByEntrustId(entrustId);
                    if (dto != null) {
                        // 委托已接收状态切完成度小于100%
                        if (StringUtil.equals(dto.getEntrustStatus(), BussinessDictCodeList.委托状态_已接收.getValue())
                            && dto.getTransactioncom().compareTo(new BigDecimal(100)) >= 0) {
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_031")+"指令编号:"+dto.getEntrustId()));
                        }
                        if(!StringUtil.isNotEmpty(finprodId)){
                            finprodId = dto.getFinprodId();
                        }
                        if(!StringUtil.equals(finprodId,dto.getFinprodId())&&finprodIdFlag){
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_032")));
                            finprodIdFlag=false;
                        }
                        if(!StringUtil.isNotEmpty(ps)){
                            ps = dto.getPs();
                        }
                        if(!StringUtil.equals(ps,dto.getPs())&&psFlag){
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_033")));
                            psFlag=false;
                        }
                        if(!StringUtil.isNotEmpty(settleSpreed)){
                            settleSpreed = dto.getSettleSpreed();
                        }
                        if(!StringUtil.equals(settleSpreed,dto.getSettleSpreed())&&settleSpreedFlag){
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_034")));
                            settleSpreedFlag=false;
                        }
                        if(!StringUtil.isNotEmpty(myside)){
                            myside = dto.getMySide();
                        }
                        if(!StringUtil.equals(myside,dto.getMySide())&&mysideFlag){
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_035")));
                            mysideFlag=false;
                        }
                    }
                }
        	}
        }
        ar.setData(this.getMessage("COMMON_C_0001", this.getMessage("SysCommon.save")));
        ar.setErrorList(validList);
        return ar;
    }
    /**
     * {@inheritDoc}
     */
    @Override
	public void doComplete(CfetsBondDealDialogueQuoteDto entity, OperationType type) {
        String statusValid = null;
        if(StringUtil.isNotEmpty(isInWflProcess(entity.getQuoteId(),entity.getPageId(),null))){
            statusValid = DictCodeListExt1.有效状态_流程中.getValue();
        }else{
            statusValid=DictCodeListExt1.有效状态_有效.getValue();
        }
        CfetsBondDealDialogueQuoteDto cetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.getBondDialogueQuote(
                entity.getQuoteId(), statusValid);
        if(null!=cetsBondDealDialogueQuoteDto && !StringUtil.isEmpty(entity.getTradeStatus()) && !StringUtil.equals(cetsBondDealDialogueQuoteDto.getTradeStatus(),entity.getTradeStatus())){
            throw new BusinessException("MSG:报价状态已变更,无法审批!");
        }
        //this.calcListFpriceAmt(entity);
        this.handOurAndLendinShareAmt(entity);
        BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
        BeanUtil.copyPropertiesIgnoreNull(bondQuoteInfoDto,entity);
        com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = preparEntity(entity);
        CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
        cfetsOprHisInfoRequest.setEntrustId(cfetsBondDealDialogueQuoteDto.getQuoteId());
        cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        // 历史报价撤销
		if (StringUtil.equals(entity.getOptType(), "revoke")) {
			entity.setQuoteType(BussinessDictCodeList.报价方式_对话报价.getValue());
            cfetsBondDealQuotedPriceService.doHisRevoke(bondQuoteInfoDto);
            cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
			/*restTemplate.postForObject(
					ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doHisRevoke", entity,
					Boolean.class);*/
		} else {//做市方回应
			if (StringUtil.equals(entity.getQuotaPageModel(), "marketResponse")) {
                cfetsBondDealQuotedPriceService.sendBondRequestQuoteNewReply(cfetsBondDealDialogueQuoteDto);
				/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
						+ "/CfetsBondDealQuotedPrice/sendBondRequestQuoteNewReply", entity, boolean.class);*/
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "dialogueResponse")) {
				// 对话回应
				if (StringUtil.equals(entity.getOperatorType(), "done")) {
					// 成交
                    cfetsBondDealQuotedPriceService.sendBondDialogueQuoteDeal(cfetsBondDealDialogueQuoteDto);
                    cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_成交.getValue());
					/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteDeal", entity, boolean.class);*/
				} else if (StringUtil.equals(entity.getOperatorType(), "refuse")) {
					// 拒绝
                    cfetsBondDealQuotedPriceService.sendBondDialogueQuoteRefuse(cfetsBondDealDialogueQuoteDto);
                    cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_拒绝.getValue());
					/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteRefuse", entity, boolean.class);*/
				}
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "new")) {
				// 新增
				if (StringUtil.equals(entity.getOperatorType(), "save")) {
					// 保存
                    cfetsBondDealQuotedPriceService.insertBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
					/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/insertBondDialogueQuote", entity, boolean.class);*/
				} else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
					// 提交
                    cfetsBondDealQuotedPriceService.sendNewBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
					/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendNewBondDialogueQuote", entity, boolean.class);*/
				}
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "edit")) {
				// 修改
				if (StringUtil.equals(entity.getOperatorType(), "save")) {
					// 保存
                    cfetsBondDealQuotedPriceService.insertBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
					/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/insertBondDialogueQuote", entity, boolean.class);*/
                    } else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
					// 提交
                    cfetsBondDealQuotedPriceService.sendUpdateBondDialogueQuote(cfetsBondDealDialogueQuoteDto);
					/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendUpdateBondDialogueQuote", entity, boolean.class);*/
				} else {
					entity.setQuoteType(BussinessDictCodeList.报价方式_对话报价.getValue());
                    bondQuoteInfoDto.setQuoteType(BussinessDictCodeList.报价方式_对话报价.getValue());
                    cfetsBondDealQuotedPriceService.doHisRevoke(bondQuoteInfoDto);
					/*restTemplate.postForObject(
							ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doHisRevoke",
							entity, Boolean.class);*/
				}
                cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_修改.getValue());
            }
        }
        joyinbusinessCommonService.updateCfetsOprHis(cfetsOprHisInfoRequest);
	}
    
    
    @Override
	public void doBack(CfetsBondDealDialogueQuoteDto entity){
        String systemAct= handSystemAct(entity);
        sendPosition(entity,DictCodeListExt3.流程节点_流程退回.getValue(),systemAct,systemAct,null);

    	//doDeletePtlPositionHold(entity.getWorkflowId());
        if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){

            //回退释放头寸
            doDeletePtlPositionHold(entity.getWorkflowId());
            //插入旧头寸
            if (StringUtil.equals(entity.getQuotaPageModel(), "edit")) {
                entity.setIsExistNew(DictCodeList.是否_是.getValue());
                entity.getCfetsBondDealDialogueQuoteDtoList().clear();
                doInsetOrUpdateForPtlPositionHold(entity);
            }

        }

    }
    
    // add by etliu 20210309 银行间上下行多组合 start
    /**
     * 计算多组合组合分配列表中的全价总额
     * 总全价总额按照列表的券面总额拆分，尾差放在最大的券面总额的记录上
     * @param entity
     */
    private void calcListFpriceAmt(CfetsBondDealDialogueQuoteDto entity) {
        if (StringUtil.isEmptyList(entity.getCfetsBondDealDialogueQuoteDtoList())) {
            return;
        }
        
        BigDecimal sumAvaNum = entity.getAvaNum(); // 画面上的券面总额（万元单位）
        BigDecimal sumFpriceAmt = entity.getFpriceAmt(); // 画面上的全价总额（元单位）
        BigDecimal tempSumFpriceAmt = BigDecimal.ZERO;
        CfetsBondDealDialogueQuoteDto maxDataDto = null;
        for (CfetsBondDealDialogueQuoteDto tempDataDto : entity.getCfetsBondDealDialogueQuoteDtoList()) {
            BigDecimal tempFpriceAmt = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(tempDataDto.getAvaNum(), sumFpriceAmt), sumAvaNum, 2);
            tempDataDto.setFpriceAmt(tempFpriceAmt);

            tempSumFpriceAmt = ArithmeticUtil.doAdd(tempSumFpriceAmt, tempFpriceAmt);
            if (maxDataDto == null || tempDataDto.getAvaNum().compareTo(maxDataDto.getAvaNum()) > 0) {
                maxDataDto = tempDataDto;
            }
        }
        
        // 尾差处理
        BigDecimal dValue = ArithmeticUtil.subtract(sumFpriceAmt, tempSumFpriceAmt);
        // Sonar Fix modify by huwei 220608
        maxDataDto.setFpriceAmt(ArithmeticUtil.doAdd(null == maxDataDto ? new BigDecimal(0) : maxDataDto.getFpriceAmt(), dValue));
    }
    // add by etliu 20210309 银行间上下行多组合 end
    
    
    /**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsBondDealDialogueQuoteDto entity,OperationType type){
        Map<String,Object> map = new HashMap<>();
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        entity.setWorkflowId(entity.getWorkflowId());
        
     // add by etliu 20210309 银行间上下行多组合 start
     //   this.calcListFpriceAmt(entity);
     // add by etliu 20210309 银行间上下行多组合 end
        //处理本方券、融入券
        this.handOurAndLendinShareAmt(entity);
        insertWflBondDialogueQuote(entity);
        if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
            //插入旧头寸
            entity.setIsExistNew(DictCodeList.是否_是.getValue());
            doInsetOrUpdateForPtlPositionHold(entity);
        }
        String systemAct = handSystemAct(entity);
        sendPosition(entity, DictCodeListExt3.流程节点_流程提交.getValue(), systemAct, systemAct, null);
    }
    
	@Override
    public void doUpdate(CfetsBondDealDialogueQuoteDto entity){
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        Map<String,Object> map = new HashMap<>();
        if (!StringUtil.equals("revoke", entity.getOperatorType()) && !StringUtil.equals("delete", entity.getOperatorType())) {
            // add by etliu 20210309 银行间上下行多组合 start
            //this.calcListFpriceAmt(entity);
            // add by etliu 20210309 银行间上下行多组合 end
            this.handOurAndLendinShareAmt(entity);
            // 重新insert
        	insertWflBondDialogueQuote(entity);
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                entity.setIsExistNew(DictCodeList.是否_是.getValue());
                doInsetOrUpdateForPtlPositionHold(entity);
            }
            String confirmOk = RequestUtil.getParam("confirmOk");
        	try{
                SysWflBusinessList sysWflBusinessList = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
                String systemAct= handSystemAct(entity);
                if(StringUtil.equals(sysWflBusinessList.getProcessState(), ProcessState.BACK.toString())){
                    sendPosition(entity,DictCodeListExt3.流程节点_流程提交.getValue(),DictCodeListExt3.系统动作_修改.getValue(),systemAct,confirmOk);
                }else{
                    sendPosition(entity,DictCodeListExt3.流程节点_流程提交.getValue(),systemAct,systemAct,confirmOk);
                }
            }catch(PositionException e){
                e.printStackTrace(); Message message = PositionUtil.ajaxErrorReturn(e);
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
            //doInsetOrUpdateForPtlPositionHold(entity);
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(CfetsBondDealDialogueQuoteDto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 删除操作
     */
    @Override
    public void doDelete(String workflowId) {
        doDeleteWflBondDialogueQuote(workflowId);
    }
    
    /**
     * 删除工作流数据
     * @param workflowId
     */
    private void doDeleteWflBondDialogueQuote(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        cfetsBondDealQuotedPriceService.doDeleteWflDataForBond(param);
       /* restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doDeleteWflDataForBond", param,
            Boolean.class);*/
    }
    
    
    /**
     * 删除持仓占用
     * @param workflowId
     */
    private void doDeletePtlPositionHold(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        param.put("pageWflType","BOND");
        cfetsBondDealQuotedPriceService.doDeletePtlPositionHold(param);
        /*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doDeletePtlPositionHold", param,
            Boolean.class);*/
    }
    
    
    
    /**
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by amyli 
	 * 20201230
	 * @param dto
	 */
	public void doInsetOrUpdateForPtlPositionHold(CfetsBondDealDialogueQuoteDto dto) {
        cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(preparEntity(dto));
		/*restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doInsetOrUpdateDialogueForPtlPositionHold", dto,
				Integer.class);*/
	}
	
	


    /**
     * 判断委托剩余额度
     * 
     * @param dto 对话报价对象
     * @param validList 错误验证消息
     * @param sb 提示信息
     * @param sysParam true:硬 false:软
     * @see
     * @since 1.0
     */
    public void checkEntrustAvaNum(CfetsBondDealDialogueQuoteDto dto, List<ValidMessage> validList,
                                   StringBuffer sb, boolean sysParam) {
        // 做市报价-回复
        // 回复量不能超过剩余报价券面总额
        if (StringUtil.equals(dto.getQuotaPageModel(), "marketResponse")) {
         // modify by etliu 20210316 银行间上下行多组合 start
            if (StringUtil.isNotEmpty(dto.getEntrustId()) && dto.getResponseAmt() != null
                && StringUtil.isEmptyList(dto.getCfetsBondDealDialogueQuoteDtoList())) {
         // modify by etliu 20210316 银行间上下行多组合 end
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("entrustId", dto.getEntrustId());
                BigDecimal dealedAmt = this.queryEntrustDealedAmt(map);
                if (ArithmeticUtil.multiply(dto.getResponseAmt(), new BigDecimal(10000)).compareTo(
                    ArithmeticUtil.subtract(dto.getEntrustAmt(), dealedAmt)) > 0) {
                    if (sysParam) {
                        validList.add(new ValidMessage("responseAmt",
                            this.getMessage("CFETS_COMMON_E_012")));
                    }
                    else {
                        sb.append(this.getMessage("CFETS_COMMON_E_012"));
                        sb.append(GlobalConstant.HTML_NEWLINE);
                    }
                }
            }
        }
        // 对话报价发起 券面总额不能超过委托剩余
        // modify by etliu 20210316 银行间上下行多组合 start
        else if (StringUtil.isNotEmpty(dto.getEntrustId()) && dto.getAvaNum() != null
            && StringUtil.isEmptyList(dto.getCfetsBondDealDialogueQuoteDtoList())) {
         // modify by etliu 20210316 银行间上下行多组合 end
            CfetsBondDealDialogueQuoteDto entrustDto = this.queryEntrustInfoByEntrustId(
                dto.getEntrustId());
            //调用共通  已经除以10000，计算百元券面总额，需要乘以100
            BigDecimal entrustAmt = entrustDto.getEntrustNum() == null ? BigDecimal.ZERO : entrustDto.getEntrustNum().multiply(
                new BigDecimal(100));// 券面总额/100
            BigDecimal usedShareAmt = this.checkUsedShareAmt(dto);// 已占用额度
            if (usedShareAmt.add(dto.getAvaNum().multiply(new BigDecimal(10000)).divide(
                new BigDecimal(100))).compareTo(entrustAmt) > 0) {
                if (sysParam) {
                    validList.add(
                        new ValidMessage("avaNum", this.getMessage("CFETS_COMMON_E_010")));
                }
                else {
                    sb.append(this.getMessage("CFETS_COMMON_E_010"));
                    sb.append(GlobalConstant.HTML_NEWLINE);
                }
            }
            
/*            Map<String, Object> map = new HashMap<String, Object>();
            map.put("entrustId", dto.getEntrustId());
            BigDecimal dealedAmt = this.queryEntrustDealedAmt(map);
            if (ArithmeticUtil.multiply(dto.getAvaNum(), new BigDecimal(10000)).compareTo(
                ArithmeticUtil.subtract(dto.getEntrustAmt(), dealedAmt)) > 0) {
                if (sysParam) {
                    validList.add(
                        new ValidMessage("avaNum", this.getMessage("CFETS_COMMON_E_010")));
                }
                else {
                    sb.append(this.getMessage("CFETS_COMMON_E_010"));
                    sb.append(GlobalConstant.HTML_NEWLINE);
                }
            }*/
        }
    }

    /**
     * 验证交易偏离度
     * 
     * @param dto 对话报价对象
     * @param validList 错误验证消息
     * @param sb 提示信息
     * @param sysParam true:硬 false:软
     * @see
     * @since 1.0
     */
    public void checkDealDeviation(CfetsBondDealDialogueQuoteDto dto, List<ValidMessage> validList,
                                   StringBuffer sb, boolean sysParam) {
        if (dto.getPrice() != null && dto.getFinprodMarketId() != null) {
            MstBondValuationInfo mstBondValuationInfo = this.queryLastBondValuation(
                dto.getFinprodMarketId());
            if (mstBondValuationInfo != null && mstBondValuationInfo.getNetprice() != null) {
                BigDecimal dealDeviation = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(ArithmeticUtil.subtract(dto.getPrice(),
                        mstBondValuationInfo.getNetprice()), new BigDecimal(100)),
                    mstBondValuationInfo.getNetprice(), 6);
                BigDecimal sysDealDeviation = null;
                try {
                    sysDealDeviation = new BigDecimal(
                        CacheUtil.getSysParam("ibuVar_dealDeviationVal"));
                }
                catch (Exception e) {
                    throw new BusinessException("MSG:请检查系统参数ibuVar_dealDeviationVal值的正确性！");
                }
                if (sysDealDeviation != null
                    && dealDeviation.abs().compareTo(sysDealDeviation) > 0) {
                    if (sysParam) {
                        validList.add(
                            new ValidMessage("price", this.getMessage("CFETS_COMMON_E_035")));
                    }
                    else {
                        sb.append(this.getMessage("CFETS_COMMON_E_035"));
                        sb.append(GlobalConstant.HTML_NEWLINE);
                    }
                }
            }
        }
    }

    /**
     * 验证委托价格
     * @param cfetsBondDealDialogueQuoteDto
     * @param validList
     * @param sb
     * @param sysParam
     */
    public void checkCalcFieldEntrust(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto,
                                      List<ValidMessage> validList, StringBuffer sb,
                                      boolean sysParam) {

        if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getEntrustId())) {
            CfetsBondDealDialogueQuoteDto entrustInfo = this.queryEntrustInfoByEntrustId(
                cfetsBondDealDialogueQuoteDto.getEntrustId());
            
            // 净价、到期收益率 、行权收益率 小数处理
            entrustInfo.setPrice(ArithmeticUtil.round(entrustInfo.getPrice(), 4, RoundingMode.HALF_UP));
            entrustInfo.setMatureYield(ArithmeticUtil.round(entrustInfo.getMatureYield(), 4, RoundingMode.HALF_UP));
            entrustInfo.setExerYield(ArithmeticUtil.round(entrustInfo.getExerYield(), 4, RoundingMode.HALF_UP));
            
            // 交易方向为买入，价格模式为限价;
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getPs())
                && StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getCalculateField())) {
                if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getPs(),
                    BussinessDictCodeList.交易方向_买入.getValue())) {
                    if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                        BussinessDictCodeList.计算选择方式_净价.getValue())) {
                        if (cfetsBondDealDialogueQuoteDto.getPrice() != null
                            && entrustInfo.getPrice() != null
                            && cfetsBondDealDialogueQuoteDto.getPrice().compareTo(
                                entrustInfo.getPrice()) > 0) {
                            if (sysParam) {
                                validList.add(new ValidMessage("price",
                                    this.getMessage("CFETS_COMMON_E_014")));
                            }
                            else {
                                sb.append(this.getMessage("CFETS_COMMON_E_014"));
                                sb.append(GlobalConstant.HTML_NEWLINE);
                            }
                        }
                    }
                    else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                        BussinessDictCodeList.计算选择方式_到期收益率.getValue())) {
                        if (cfetsBondDealDialogueQuoteDto.getMatureYield() != null
                            && entrustInfo.getMatureYield() != null
                            && cfetsBondDealDialogueQuoteDto.getMatureYield().compareTo(
                                entrustInfo.getMatureYield()) < 0) {
                            if (sysParam) {
                                validList.add(new ValidMessage("matureYield",
                                    this.getMessage("CFETS_COMMON_E_015")));
                            }
                            else {
                                sb.append(this.getMessage("CFETS_COMMON_E_015"));
                                sb.append(GlobalConstant.HTML_NEWLINE);
                            }
                        }
                    }
                    else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                        BussinessDictCodeList.计算选择方式_行权收益率.getValue())) {
                        if (cfetsBondDealDialogueQuoteDto.getExerYield() != null
                            && entrustInfo.getExerYield() != null
                            && cfetsBondDealDialogueQuoteDto.getExerYield().compareTo(
                                entrustInfo.getExerYield()) < 0) {
                            if (sysParam) {
                                validList.add(new ValidMessage("exerYield",
                                    this.getMessage("CFETS_COMMON_E_016")));
                            }
                            else {
                                sb.append(this.getMessage("CFETS_COMMON_E_016"));
                                sb.append(GlobalConstant.HTML_NEWLINE);
                            }
                        }
                    }
                }
                else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getPs(),
                    BussinessDictCodeList.交易方向_卖出.getValue())) {
                    if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                        BussinessDictCodeList.计算选择方式_净价.getValue())) {
                        if (cfetsBondDealDialogueQuoteDto.getPrice() != null
                            && entrustInfo.getPrice() != null
                            && cfetsBondDealDialogueQuoteDto.getPrice().compareTo(
                                entrustInfo.getPrice()) < 0) {
                            if (sysParam) {
                                validList.add(new ValidMessage("price",
                                    this.getMessage("CFETS_COMMON_E_017")));
                            }
                            else {
                                sb.append(this.getMessage("CFETS_COMMON_E_017"));
                                sb.append(GlobalConstant.HTML_NEWLINE);
                            }
                        }
                    }
                    else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                        BussinessDictCodeList.计算选择方式_到期收益率.getValue())) {
                        if (cfetsBondDealDialogueQuoteDto.getMatureYield() != null
                            && entrustInfo.getMatureYield() != null
                            && cfetsBondDealDialogueQuoteDto.getMatureYield().compareTo(
                                entrustInfo.getMatureYield()) > 0) {
                            if (sysParam) {
                                validList.add(new ValidMessage("matureYield",
                                    this.getMessage("CFETS_COMMON_E_018")));
                            }
                            else {
                                sb.append(this.getMessage("CFETS_COMMON_E_018"));
                                sb.append(GlobalConstant.HTML_NEWLINE);
                            }
                        }
                    }
                    else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getCalculateField(),
                        BussinessDictCodeList.计算选择方式_行权收益率.getValue())) {
                        if (cfetsBondDealDialogueQuoteDto.getExerYield() != null
                            && entrustInfo.getExerYield() != null
                            && cfetsBondDealDialogueQuoteDto.getExerYield().compareTo(
                                entrustInfo.getExerYield()) > 0) {
                            if (sysParam) {
                                validList.add(new ValidMessage("exerYield",
                                    this.getMessage("CFETS_COMMON_E_019")));
                            }
                            else {
                                sb.append(this.getMessage("CFETS_COMMON_E_019"));
                                sb.append(GlobalConstant.HTML_NEWLINE);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 验证已占用额度
     * @param cfetsBondDealDialogueQuoteDto
     * @return
     */
	public BigDecimal checkUsedShareAmt(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entrustId", cfetsBondDealDialogueQuoteDto.getEntrustId());
		if(!StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())){
			param.put("quoteId", "");
		}else{
			param.put("quoteId", cfetsBondDealDialogueQuoteDto.getQuoteId());
		}
        BigDecimal usedShareAmt = cfetsQuotedCommonServiceMicro.queryUsedShareAmt(param);
		/*BigDecimal usedShareAmt = restTemplate.postForObject(
				ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryUsedShareAmt",
				param, BigDecimal.class);*/
		return usedShareAmt == null ? BigDecimal.ZERO : usedShareAmt;
	}
	
	/**
	 * 根据实际指令类型来生成流程信息 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @return
	 * @see
	 * @since 1.0
	 */
	public WorkFlowParameter<CfetsBondDealDialogueQuoteDto> getWorkFlowParameter(CfetsBondDealDialogueQuoteDto dto) {
		WorkFlowParameter<CfetsBondDealDialogueQuoteDto> parameter = new WorkFlowParameter<CfetsBondDealDialogueQuoteDto>();
		// TODO 根据实际指令类型来生成流程信息
		return parameter;
	}

    /**
     * 插入流程表数据
     * @param entity
     * @return
     */
	public Boolean insertWflBondDialogueQuote(CfetsBondDealDialogueQuoteDto entity) {
        return cfetsBondDealQuotedPriceService.insertWflBondDialogueQuote(preparEntity(entity));
		/*return restTemplate.postForObject(
				ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/insertWflBondDialogueQuote",
				entity, Boolean.class);*/
	}

	public List<CfetsBondDealDialogueQuoteDto> getCfetsBondDealDialogueQuoteDtoList(String quoteId) {
        List<CfetsBondDealDialogueQuoteDto> result = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("quoteId", quoteId);
        map.put("loginUserId", LoginUserUtil.getUserName());
        List<com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList = cfetsBondDealQuotedPriceService.getCfetsBondDealDialogueQuoteDtoList(map);
        for (com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto : cfetsBondDealDialogueQuoteDtoList) {
            CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto1 = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto1,cfetsBondDealDialogueQuoteDto);
            result.add(cfetsBondDealDialogueQuoteDto1);
        }
        return result;
        /*return restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                + "/CfetsBondDealQuotedPrice/getCfetsBondDealDialogueQuoteDtoList",
                map, CfetsBondDealDialogueQuoteDto.class);*/
	}

    /**
     *功能描述   check是否存在流程中
     * @author zhangnian
     * @date 2021/1/15 0015
     * @param  * @param quoteId
     * @return java.util.List<com.joyintech.framework.validation.ValidMessage>
     */
    public List<ValidMessage>  isInWflProcess(String quoteId,String pageId,String workflowId){
        //check流程中
        List<ValidMessage> validList = new ArrayList<ValidMessage>();
        WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
        wflValidInputParameter.setPageId(StringUtil.isNotEmpty(pageId)?pageId:"CfetsBondDealQuotedPrice002");
        wflValidInputParameter.setWorkflowId(workflowId);
        //设置报价编号
        wflValidInputParameter.setBusinessDataPk1(quoteId);
        if(StringUtil.isNotEmpty(mstWorkflowCommonServie)&&mstWorkflowCommonServie.isInWflProcess(wflValidInputParameter)){
            validList.add(new ValidMessage(getMessage("BUSS_COM_E_0023")));
        }
        return validList;
    }

    /*public String isExerYieldDivShow(Map<String, Object> paramMap) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isExerYieldDivShow",
                paramMap, String.class);
    }*/

    public BigDecimal getWflEntrustDealedAmt(Map<String, Object> map) {
        return cfetsBondDealQuotedPriceService.getWflEntrustDealedAmt(map);
        /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                        + "/CfetsBondDealQuotedPrice/getWflEntrustDealedAmt",
                map, BigDecimal.class);*/
    }

    public EtrEntrust getEtrEntrust(String entrustId){
       return  etrEntrustMapper.selectByPrimaryKey(entrustId);
    }

    public BigDecimal queryEntrustDealedAmt2(Map<String, Object> map) {
        return cfetsBondDealQuotedPriceService.queryEntrustDealedAmt2(map);
        /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                        + "/CfetsBondDealQuotedPrice/queryEntrustDealedAmt2",
                map, BigDecimal.class);*/
    }

    /**
     * 前端实体转微服务端
     * @param entity
     * @return
     */
    public com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto preparEntity(CfetsBondDealDialogueQuoteDto entity){
        com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = JsonUtils.json2Object(JsonUtils.object2Json(entity),com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto.class);
        //处理下业务模式
        if(CollectionUtils.isNotEmpty(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())) {
            Map<String, Object> entrustReclas = new HashMap<>();
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_买入.getValue())) {
                entrustReclas = cfetsQuotedCommonServicevue.isEntrustReclas(entity.getFinprodId(), entity.getPrice(), entity.getSettleDate());
            }
            for (com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto item : cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList()) {

                if (StringUtil.equals(item.getBusinessModel(), DictCodeListExt3.业务模式_收取合同约定的现金流.getValue()) && entrustReclas.containsKey("isEntrustReclas") && (boolean) entrustReclas.get("isEntrustReclas")) {
                    item.setInvAim(DictCodeListExt3.业务模式_出售.getValue());
                }
            }
        }
        return cfetsBondDealDialogueQuoteDto;
    }

    /**
     * 处理本方券 融入券
     * @param entity
     */
    public void  handOurAndLendinShareAmt(CfetsBondDealDialogueQuoteDto entity){
        if(CollectionUtils.isNotEmpty(entity.getCfetsBondDealDialogueQuoteDtoList())){
            List<PtlAllocationDto> ptlAllocations = new ArrayList<>();
            entity.getCfetsBondDealDialogueQuoteDtoList().forEach(item ->{
                PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                ptlAllocationDto.setShareAmt(ArithmeticUtil.multiply(item.getAvaNum(),new BigDecimal(10000)));
                ptlAllocationDto.setEntrustId(item.getEntrustId());
                ptlAllocations.add(ptlAllocationDto);
            });
            Map<String, PtlAllocationDto> stringPtlAllocationDtoMap = cfetsQuotedCommonServicevue.handOurAndLendinShareAmt(ptlAllocations, entity.getWorkflowId(), entity.getPs(),entity.getQuoteId());
            entity.getCfetsBondDealDialogueQuoteDtoList().forEach(item ->{
                if(stringPtlAllocationDtoMap.containsKey(item.getEntrustId())){
                    item.setOurShareAmt(stringPtlAllocationDtoMap.get(item.getEntrustId()).getOurShareAmt());
                    item.setLendinShareAmt(stringPtlAllocationDtoMap.get(item.getEntrustId()).getLendinShareAmt());
                }
            });
        }
    }
    /**
     * 通过报价编号   处理头寸需要的数据
     * @param quoteId
     * @return
     */
    public CfetsBondDealDialogueQuoteDto handpositionByQuoteId(String quoteId){
        com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto positionDto = new com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto();
        BondDialogueQuoteDto bondDialogueQuote = ibuMainService.getBondDialogueQuote(quoteId);
        if(null!=bondDialogueQuote&&null!=bondDialogueQuote.getIbuQuoteBondDto()) {
            IbuQuoteBondDto ibuQuoteBondDto = bondDialogueQuote.getIbuQuoteBondDto();
            positionDto.setQuoteId(ibuQuoteBondDto.getQuoteId());
            positionDto.setFinprodId(ibuQuoteBondDto.getFinprodId());
            positionDto.setPs(ibuQuoteBondDto.getPs());
            positionDto.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            String settleSpreed = StringUtil.EMPTY;
            if (ibuQuoteBondDto.getSettleSpeed() == 0L) {
                settleSpreed = BussinessDictCodeList.清算速度_T加0.getValue();
            } else if (ibuQuoteBondDto.getSettleSpeed() == 1L) {
                settleSpreed = BussinessDictCodeList.清算速度_T加1.getValue();
            } else if (ibuQuoteBondDto.getSettleSpeed() == 2L) {
                settleSpreed = BussinessDictCodeList.清算速度_T加2.getValue();
            }
            positionDto.setSettleSpreed(settleSpreed);
            positionDto.setCcy(ibuQuoteBondDto.getSettCcy());
            if(CollectionUtils.isNotEmpty(bondDialogueQuote.getIbuQuoteEtrReDtoList())){
                List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList = new ArrayList<>();
                bondDialogueQuote.getIbuQuoteEtrReDtoList().forEach(item ->{
                    CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
                    BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,item);
                    cfetsBondDealDialogueQuoteDto.setAvaNum(ArithmeticUtil.divide(item.getShareAmt(),new BigDecimal(100),2));
                    cfetsBondDealDialogueQuoteDto.setOurShareAmt(ArithmeticUtil.multiply(item.getOurShareAmt(),new BigDecimal(100)));
                    cfetsBondDealDialogueQuoteDto.setLendinShareAmt(ArithmeticUtil.multiply(item.getLendinShareAmt(),new BigDecimal(100)));
                    cfetsBondDealDialogueQuoteDtoList.add(cfetsBondDealDialogueQuoteDto);
                });
                positionDto.setCfetsBondDealDialogueQuoteDtoList(cfetsBondDealDialogueQuoteDtoList);
            }
        }
        return positionDto;
    }
    /**
     * 处理系统动作
     * @param entity
     * @return
     */
    public String handSystemAct(CfetsBondDealDialogueQuoteDto entity){
        String systemAct = StringUtil.EMPTY;
        if(StringUtil.equals(entity.getQuotaPageModel(), "new")){
            systemAct = DictCodeListExt3.系统动作_新建.getValue();
        }
        if(StringUtil.equals(entity.getQuotaPageModel(), "edit")){
            systemAct = DictCodeListExt3.系统动作_修改.getValue();
        }
        if(StringUtil.equals(entity.getQuotaPageModel(),"dialogueResponse")&&StringUtil.equals(entity.getOperatorType(), "done")){
            systemAct = DictCodeListExt3.系统动作_成交.getValue();
        }
        return systemAct;
    }

    /**
     *
     * @param entity
     * @param flowStatus
     * @param decisionAct  角色动作
     * @param systemAct    系统动作
     */
    public void sendPosition(CfetsBondDealDialogueQuoteDto entity,String flowStatus,String decisionAct,String systemAct,String confirmOk){
        Map<String, Object> stringObjectMap = preparePosition(entity, flowStatus,decisionAct,systemAct,confirmOk);
        if(StringUtil.equals(flowStatus,DictCodeListExt3.流程节点_流程提交.getValue())||StringUtil.equals(flowStatus,DictCodeListExt3.流程节点_流程退回.getValue())){
            if(StringUtil.equals(systemAct,DictCodeListExt3.系统动作_新建.getValue())||StringUtil.equals(systemAct,DictCodeListExt3.系统动作_修改.getValue())||StringUtil.equals(systemAct,DictCodeListExt3.系统动作_成交.getValue())){
                PositionHttpRequest.send(stringObjectMap);
                return;
            }
        }
        if(StringUtil.equals(flowStatus,DictCodeListExt3.流程节点_审批生效.getValue())&&(StringUtil.equals(systemAct,DictCodeListExt3.系统动作_撤回.getValue())||StringUtil.equals(systemAct,DictCodeListExt3.系统动作_关闭.getValue()))){
            PositionHttpRequest.send(stringObjectMap);
            return;
        }
        if(StringUtil.equals(flowStatus,DictCodeListExt3.流程节点_流程提交.getValue())&&StringUtil.equals(systemAct,DictCodeListExt3.系统动作_退回.getValue())){
            PositionHttpRequest.send(stringObjectMap);
            return;
        }
    }


    /**
     * 处理头寸组装
     * @param dto
     * @param flowStatus
     * @return
     */
    public Map<String,Object> preparePosition(CfetsBondDealDialogueQuoteDto dto,String flowStatus,String decisionAct,String systemAct,String confirmOk) {
        com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto entity = JsonUtils.json2Object(JsonUtils.object2Json(dto), com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto.class);
        return cfetsQuotedCommonServiceMicro.prepareDialoguePosition(entity,flowStatus,decisionAct,systemAct,confirmOk);
    }
}
