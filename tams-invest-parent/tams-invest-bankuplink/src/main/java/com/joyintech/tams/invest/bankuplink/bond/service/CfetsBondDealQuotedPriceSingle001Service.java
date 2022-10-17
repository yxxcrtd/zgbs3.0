/*
 * 文件名：CfetsBondDealQuotedPrice002Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.bankuplink.bond.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.SecInfoModelDto;
import com.joyintech.tams.invest.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 现券买卖-对话报价
 * 
 * @author zhangliang
 * @version 1.0 2020年5月13日
 * @see CfetsBondDealQuotedPriceSingle001Service
 * @since 1.0
 */
@Service
public class CfetsBondDealQuotedPriceSingle001Service extends BaseService<CfetsBondDealDialogueQuoteDto> implements BaseProcessService<CfetsBondDealDialogueQuoteDto> {

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
   @Autowired
   private BusiFinExercisePlanService busiFinExercisePlanService;
    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonServie;
    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;
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
            List<CfetsBondDealEntrustOrderDto> list = restTemplate.postForList(
                ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/queryEntrustOrderList",
                param, CfetsBondDealEntrustOrderDto.class);

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
        //券面按万元处理 故需要乘以10000
        BigDecimal avaNum = cfetsBondDealDialogueQuoteDto.getAvaNum();
        cfetsBondDealDialogueQuoteDto.setAvaNum(ArithmeticUtil.multiply(avaNum,new BigDecimal("10000")));
        AjaxResult ar = this.calcAmt(cfetsBondDealDialogueQuoteDto);
        cfetsBondDealDialogueQuoteDto.setAvaNum(avaNum);
        if (ar != null && ar.getData() != null) {
            CfetsBondDealDialogueQuoteDto calcDto = (CfetsBondDealDialogueQuoteDto)ar.getData();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto, calcDto);
            cfetsBondDealDialogueQuoteDto.setAvaNum(avaNum);
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
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(),
            "marketResponse")) {
            return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                              + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteNewReply",
                cfetsBondDealDialogueQuoteDto, boolean.class);
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(),
            "dialogueResponse")) {
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "done")) {
                return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteDeal",
                    cfetsBondDealDialogueQuoteDto, boolean.class);
            }
            else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(),
                "refuse")) {
                return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteRefuse",
                    cfetsBondDealDialogueQuoteDto, boolean.class);
            }
            return false;
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getQuotaPageModel(), "new")) {
            if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(), "save")) {
                return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/insertBondDialogueQuote",
                    cfetsBondDealDialogueQuoteDto, boolean.class);
            }
            else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getOperatorType(),
                "submit")) {
                return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                                  + "/CfetsBondDealQuotedPrice/sendNewBondDialogueQuote",
                    cfetsBondDealDialogueQuoteDto, boolean.class);
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
        return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/getBondDialogueQuote",
            map, CfetsBondDealDialogueQuoteDto.class);
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

        /*if(StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getPrice())) {
            cfetsBondDealDialogueQuoteDto = restTemplate.postForObject(
                    ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/calcAmt",
                    cfetsBondDealDialogueQuoteDto, CfetsBondDealDialogueQuoteDto.class);
        }
        ajaxResult.setData(cfetsBondDealDialogueQuoteDto);*/
        cfetsBondDealDialogueQuoteDto = cfetsBondDealQuotedPrice002Service.calAmt2(cfetsBondDealDialogueQuoteDto);
        cfetsBondDealDialogueQuoteDto.setMatureYield(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getMatureYield(),new BigDecimal(100)));
        cfetsBondDealDialogueQuoteDto.setExerYield(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getExerYield(),new BigDecimal(100)));
        ajaxResult.setData(cfetsBondDealDialogueQuoteDto);
        return ajaxResult;
    }

    /**
     * 1.7计算按钮点击事件 检查输入参数是否合法
     *
     * @param dto 画面输入参数
     * @return 检查结果
     * @see
     * @since 1.0
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

            // 债券到期日<=交割日时
            if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getSettleDate())
                && StringUtil.isNotEmpty(secInfoModelDto.getEndDate())) {
                if (DateUtil.compareDate(secInfoModelDto.getEndDate(),
                    cfetsBondDealDialogueQuoteDto.getSettleDate()) <= 0) {
                    validMessageList.add(new ValidMessage("settleDate",
                        this.getMessage("BUSS_COM_E_0035",
                            this.getMessage("CfetsBondDealQuotedPrice002.settleDate"),
                            this.getMessage("CfetsBondDealQuotedPrice002.assetMdate"))));
                }
            }
            // 画面.交割日<债券起息日
            if (StringUtil.isNotEmpty(secInfoModelDto.getStartDate())
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
        return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/queryEntrustDealedAmt",
            map, BigDecimal.class);
    }

    /**
     * 查询债券最新估值
     * 
     * @param map 入参
     * @return 最新估值
     * @see
     * @since 1.0
     */
    public MstBondValuationInfo queryLastBondValuation(String finprodMarketId) {
        Map<String, Object> map = new HashMap<>();
        map.put("finprodMarketId", finprodMarketId);
        map.put("platDate", iPlatformDate.getSysDateStr());
        return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/queryLastBondValuation",
            map, MstBondValuationInfo.class);
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
        	String finprodId="";String ps ="";String settleSpreed ="";String myside="";String portfolioSeries="";
        	boolean finprodIdFlag=true; boolean psFlag =true; boolean  settleSpreedFlag =true; boolean mysideFlag=true; boolean portfolioSeriesFlag=true;
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
                        if(!StringUtil.isNotEmpty(portfolioSeries)){
                            portfolioSeries = dto.getPortfolioSeries();
                        }
                        if(!StringUtil.equals(portfolioSeries,dto.getPortfolioSeries())&&portfolioSeriesFlag){
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_R_036")));
                            portfolioSeriesFlag=false;
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
        // add by etliu 20210309 银行间上下行多组合 start
        this.calcListFpriceAmt(entity);
        // add by etliu 20210309 银行间上下行多组合 end
        
		// 历史报价撤销
		if (StringUtil.equals(entity.getOptType(), "revoke")) {
			entity.setQuoteType(BussinessDictCodeList.交易类型_对话报价.getValue());
			restTemplate.postForObject(
					ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doHisRevoke", entity,
					Boolean.class);
		} else {//做市方回应
			if (StringUtil.equals(entity.getQuotaPageModel(), "marketResponse")) {
				restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
						+ "/CfetsBondDealQuotedPrice/sendBondRequestQuoteNewReply", entity, boolean.class);
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "dialogueResponse")) {
				// 对话回应
				if (StringUtil.equals(entity.getOperatorType(), "done")) {
					// 成交
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteDeal", entity, boolean.class);
				} else if (StringUtil.equals(entity.getOperatorType(), "refuse")) {
					// 拒绝
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteRefuse", entity, boolean.class);
				}
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "new")) {
				// 新增
				if (StringUtil.equals(entity.getOperatorType(), "save")) {
					// 保存
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/insertBondDialogueQuote", entity, boolean.class);
				} else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
					// 提交
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendNewBondDialogueQuote", entity, boolean.class);
				}
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "edit")) {
				// 修改
				if (StringUtil.equals(entity.getOperatorType(), "save")) {
					// 保存
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/insertBondDialogueQuote", entity, boolean.class);
				} else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
					// 提交
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendUpdateBondDialogueQuote", entity, boolean.class);
				} else {
					entity.setQuoteType(BussinessDictCodeList.交易类型_对话报价.getValue());
					restTemplate.postForObject(
							ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doHisRevoke",
							entity, Boolean.class);
				}
			}
		}
	}
    
    
    @Override
	public void doBack(CfetsBondDealDialogueQuoteDto entity){
		//回退释放头寸
    	doDeletePtlPositionHold(entity.getWorkflowId());
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
        maxDataDto.setFpriceAmt(ArithmeticUtil.doAdd(maxDataDto.getFpriceAmt(), dValue));
    }
    // add by etliu 20210309 银行间上下行多组合 end
    
    
    /**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsBondDealDialogueQuoteDto entity,OperationType type){
        Map<String,Object> map = new HashMap<>();

        // 上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(entity.getBusinessId());
        mstFileInputParameter.setFileList(entity.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
        if ("save".equals(entity.getOperatorType())) {
            return;
        }
        entity.setWorkflowId(entity.getWorkflowId());
        
     // add by etliu 20210309 银行间上下行多组合 start
        this.calcListFpriceAmt(entity);
     // add by etliu 20210309 银行间上下行多组合 end
        
        insertWflBondDialogueQuote(entity);
        doInsetOrUpdateForPtlPositionHold(entity);
    }
    
	@Override
    public void doUpdate(CfetsBondDealDialogueQuoteDto entity){
        Map<String,Object> map = new HashMap<>();
        // 上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(entity.getBusinessId());
        mstFileInputParameter.setFileList(entity.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }

        if (!StringUtil.equals("revoke", entity.getOperatorType()) && !StringUtil.equals("delete", entity.getOperatorType())) {
            // add by etliu 20210309 银行间上下行多组合 start
            this.calcListFpriceAmt(entity);
            // add by etliu 20210309 银行间上下行多组合 end
            
            // 重新insert
        	insertWflBondDialogueQuote(entity);
            doInsetOrUpdateForPtlPositionHold(entity);
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
        restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doDeleteWflDataForBond", param,
            Boolean.class);
    }
    
    
    /**
     * 删除持仓占用
     * @param workflowId
     */
    private void doDeletePtlPositionHold(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        param.put("pageWflType","BOND");
        restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doDeletePtlPositionHold", param,
            Boolean.class);
    }
    
    
    
    /**
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by amyli 
	 * 20201230
	 * @param dto
	 */
	public void doInsetOrUpdateForPtlPositionHold(CfetsBondDealDialogueQuoteDto dto) {
		restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doInsetOrUpdateDialogueForPtlPositionHold", dto,
				Integer.class);
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
                new BigDecimal(100))).compareTo(entrustAmt.subtract(usedShareAmt)) > 0) {
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
     * 
     * @param validList 验证消息
     * @param cfetsBondDealDialogueQuoteDto 对话报价信息
     * @param etrEntrust 委托基础信息
     * @param etrEntrustAdd 委托附属信息
     * @see
     * @since 1.0
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
		BigDecimal usedShareAmt = restTemplate.postForObject(
				ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsQuotedCommon/queryUsedShareAmt",
				param, BigDecimal.class);
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
	 * 
	 * @param map
	 *            入参
	 * @return
	 * @see
	 * @since 1.0
	 */
	public Boolean insertWflBondDialogueQuote(CfetsBondDealDialogueQuoteDto entity) {
		// 做市方回应
		if (StringUtil.equals(entity.getQuotaPageModel(), "marketResponse")) {

		} else if (StringUtil.equals(entity.getQuotaPageModel(), "dialogueResponse")) {
			// 对话回应
			if (StringUtil.equals(entity.getOperatorType(), "done")) {
				entity.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
			} else if (StringUtil.equals(entity.getOperatorType(), "refuse")) {
				entity.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_终止中.getValue());
			}
		} else if (StringUtil.equals(entity.getQuotaPageModel(), "new")) {
			// 新增
			if (StringUtil.equals(entity.getOperatorType(), "save")) {
				// 保存
			} else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
				// 提交
			}
		} else if (StringUtil.equals(entity.getQuotaPageModel(), "edit")) {
			// 修改
			if (StringUtil.equals(entity.getOperatorType(), "save")) {
			} else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
				entity.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_修改中.getValue());
			}
		}
		return restTemplate.postForObject(
				ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/insertWflBondDialogueQuote",
				entity, Boolean.class);
	}

	public List<CfetsBondDealDialogueQuoteDto> getCfetsBondDealDialogueQuoteDtoList(String quoteId) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("quoteId", quoteId);
        map.put("loginUserId", LoginUserUtil.getUserName());
        return restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                + "/CfetsBondDealQuotedPrice/getCfetsBondDealDialogueQuoteDtoList",
                map, CfetsBondDealDialogueQuoteDto.class);
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

    public String isExerYieldDivShow(Map<String, Object> paramMap) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/isExerYieldDivShow",
                paramMap, String.class);
    }

    public CfetsBondDealDialogueQuoteDto queryDialogueQuoteByEntrustIdList(String entrustIdListStr) {
        List <String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, entrustIdListStr);
        if(entrustIdList.size()<1){
            return new CfetsBondDealDialogueQuoteDto();
        }
        List<CfetsBondDealDialogueQuoteDto> cfetsBondDealDialogueQuoteDtoList = new ArrayList<>();
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = this.queryEntrustInfoByEntrustId(entrustIdList.get(0));
        BigDecimal avaNum = BigDecimal.ZERO;
        BigDecimal price = cfetsBondDealDialogueQuoteDto.getPrice();
        for(String entrustId:entrustIdList){
            CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuote = this.queryEntrustInfoByEntrustId(entrustId);
//            	cfetsBondDealDialogueQuote.
            avaNum = com.joyintech.calculation.util.ArithmeticUtil.doAdd(avaNum,cfetsBondDealDialogueQuote.getAvaNum());
            cfetsBondDealDialogueQuote.setEntrustAvaNum(cfetsBondDealDialogueQuote.getAvaNum());
            cfetsBondDealDialogueQuote.setAvaNum(null);
            if(StringUtil.equals(cfetsBondDealDialogueQuoteDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())){
                if(StringUtil.isNotEmpty(price)&&price.compareTo(cfetsBondDealDialogueQuote.getPrice())>0){
                    price=cfetsBondDealDialogueQuote.getPrice();
                }
            }else{
                if(StringUtil.isNotEmpty(price)&&price.compareTo(cfetsBondDealDialogueQuote.getPrice())<0){
                    price=cfetsBondDealDialogueQuote.getPrice();
                }
            }
            cfetsBondDealDialogueQuoteDtoList.add(cfetsBondDealDialogueQuote);
        }
        cfetsBondDealDialogueQuoteDto.setCfetsBondDealDialogueQuoteDtoList(cfetsBondDealDialogueQuoteDtoList);
        cfetsBondDealDialogueQuoteDto.setAvaNum(avaNum);
        cfetsBondDealDialogueQuoteDto.setPrice(price);
        return cfetsBondDealDialogueQuoteDto;
    }


    /**
     *
     * @param paramMap
     * @return
     */

    public Map<String,Object> getSppiInfoByY1(Map<String, Object> paramMap){
        Map<String,Object> result = new HashMap<>();
        String finprodId = paramMap.get("finprodId").toString();
        if(!StringUtil.isNotEmpty(paramMap.get("price"))){
            result.put("result","0");
            return result;
        }
        BigDecimal price = new BigDecimal(paramMap.get("price").toString());
        Date platDate = DateUtil.platDateFromCache();

        Boolean sinal = true;
        //1 查询资产是否有行权计划
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("finprodId", finprodId);
        param.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        param.put("execType01", DictCodeList.行权状态_未行权.getValue());
        param.put("execType02", DictCodeList.行权状态_已行权.getValue());
        List<FinExercisePlan> finExercisePlanList = busiFinExercisePlanService.getfinExercisePlanByProductId(param);
        if(finExercisePlanList==null||finExercisePlanList.size()==0){
            result.put("result","0");
            return result;
        }else if((StringUtil.equals(finExercisePlanList.get(0).getExerciseType(),"01")||StringUtil.equals(finExercisePlanList.get(0).getExerciseType(),"02"))&&DateUtil.compareDate(platDate,finExercisePlanList.get(0).getActExerciseDate())<=0){

        }else{
            result.put("result","0");
            return result;
        }

        //2 查询sppi中债中的使权不重大的购入价条件（小于第二个数字）
        String bidPriceCon = restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME + "/SppiTest001/getSppiInfoByY1?finprodId="
                + finprodId, String.class);
        if(StringUtil.isNotEmpty(bidPriceCon)) {
            try{
                bidPriceCon = bidPriceCon.split(",")[1];
            }catch (Exception e){
                result.put("result","1");
                return result;
            }
            BigDecimal bidPriceCon2 = new BigDecimal(bidPriceCon);
            if (price.compareTo(bidPriceCon2) < 0) {
                result.put("result","2");
                result.put("bidPriceCon",bidPriceCon2);
                return result;
            }
        }
        result.put("result","0");
        return result;
    }
}
