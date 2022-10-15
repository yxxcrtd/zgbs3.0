/*
 * 文件名：CfetsBondDealQuotedPrice003Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangzhuangzhuang
 * 修改时间：2020年5月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.bankuplink.bond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealEntrustOrderDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.tams.invest.bankuplink.bond.dto.CounterInfoDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 现券买卖-请求报价
 * 
 * @author wangzhuangzhuang
 * @version 1.0 2020年5月19日
 * @see CfetsBondDealQuotedPriceSingle002Service
 * @since 1.0
 */
@Service
public class CfetsBondDealQuotedPriceSingle002Service extends BaseService<CfetsBondDealRequestQuoteDto> implements BaseProcessService<CfetsBondDealRequestQuoteDto> {
    
    
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
    
    /**
     * {@linkplain}
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    
    /**
     * FinProductMapper
     */
    @Autowired
    private FinProductMapper finProductMapper;
    
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonServie;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 根据委托代码查询委托基本信息
     * 
     * @param entrustId 委托代码
     * @return 委托基本信息
     * @see
     * @since 1.0
     */
    public CfetsBondDealRequestQuoteDto queryEntrustInfoByEntrustId(String entrustId){
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
        Map<String, Object> param = new HashMap<String, Object>();
        // param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("loginUserId", LoginUserUtil.getUserName());
        param.put("entrustId", entrustId);
        List<CfetsBondDealEntrustOrderDto> pageList = restTemplate.postForList(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/queryEntrustOrderList",
            param, CfetsBondDealEntrustOrderDto.class);
        
        if (pageList != null && !StringUtil.isEmptyList(pageList)){
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto, pageList.get(0));
        }
        
        // 清算速度
        if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(), "0")){
            cfetsBondDealRequestQuoteDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加0.getValue());
        }
        else if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(), "1")){
            cfetsBondDealRequestQuoteDto.setSettleSpreed(BussinessDictCodeList.清算速度_T加1.getValue());
        }
        // 交割日期
        Date platDate = iPlatformDate.getSysDate();
        if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(), BussinessDictCodeList.清算速度_T加1.getValue())){
            platDate = cfetsQuotedCommonService.getNextWorkDay(platDate);
        }
        cfetsBondDealRequestQuoteDto.setSettleDate(platDate);
        // 结算方式
        cfetsBondDealRequestQuoteDto.setSettleMethod(BussinessDictCodeList.结算方式_券款对付.getValue());
        // 清算类型
        cfetsBondDealRequestQuoteDto.setSettleType(BussinessDictCodeList.清算类型_全额清算.getValue());
        // 生效日期
        cfetsBondDealRequestQuoteDto.setEffDate(CacheUtil.getSysParam("ibuVar_quoteEffTime"));
        
        return cfetsBondDealRequestQuoteDto;
    }
    
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param CfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return boolean 是否成功
     */
    public boolean save(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return restTemplate.postForObject(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/insertBondRequestQuote",
            cfetsBondDealRequestQuoteDto, boolean.class);
    }
    
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param CfetsBondDealRequestQuoteDto 现券请求报价信息
     * @return boolean 是否成功
     */
    public boolean submit(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return restTemplate.postForObject(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/sendBondRequestQuote",
            cfetsBondDealRequestQuoteDto, boolean.class);
    }
    /**
     * 现券请求报价回复成交<br />
     * 请求报价信息入库
     * 
     * @param CfetsBondDealRequestQuoteDto 现券请求报价信息回复成交
     * @return boolean 是否成功
     */
    public boolean clinchDeal(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto){
        return restTemplate.postForObject(
            ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteDeal",
            cfetsBondDealRequestQuoteDto, boolean.class);
    }
    
    /**
     * 现券请求报价查询<br />
     * 根据主键查询请求报价信息
     * 
     * @param quoteId String 报价编号
     * @return BondDialogueQuoteDto
     */
	public CfetsBondDealRequestQuoteDto getBondRequestQuote(String quoteId, String statusValid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("quoteId", quoteId);
		map.put("statusValid", statusValid);
		map.put("loginUserId", LoginUserUtil.getUserName());
		return restTemplate.postForObject(
				ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/getBondRequestQuote", map,
				CfetsBondDealRequestQuoteDto.class);
	}
    
    /**
     * 根据委托代码查询余额
     * 
     * @param Map param 委托代码
     * @return BigDecimal
     */
    public BigDecimal queryEntrustDealedAmt(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                                          + "/CfetsBondDealQuotedPrice/queryEntrustDealedAmt",
            map, BigDecimal.class);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doComplete(CfetsBondDealRequestQuoteDto entity, OperationType type) {
		// 历史报价撤销
		if (StringUtil.equals(entity.getOptType(), "revoke")) {
			entity.setQuoteType(BussinessDictCodeList.交易类型_请求报价.getValue());
			restTemplate.postForObject(
					ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doHisRevoke", entity,
					Boolean.class);
		} else {
			// 成交
			if (StringUtil.equals(entity.getQuotaPageModel(), "response")) {
				if (StringUtil.equals(entity.getOperatorType(), "done")) {
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendBondRequestQuoteDeal", entity, boolean.class);
				}
			} else if (StringUtil.equals(entity.getQuotaPageModel(), "new")) {
				// 新增
				if (StringUtil.equals(entity.getOperatorType(), "save")) {
					// 保存
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/insertBondRequestQuote", entity, boolean.class);
				} else if (StringUtil.equals(entity.getOperatorType(), "submit")) {
					// 提交
					restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
							+ "/CfetsBondDealQuotedPrice/sendBondRequestQuote", entity, boolean.class);
				}
			} else {// 撤回
				entity.setQuoteType(BussinessDictCodeList.交易类型_请求报价.getValue());
				restTemplate.postForObject(
						ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doHisRevoke", entity,
						Boolean.class);
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
    public void checkCalcFieldEntrust(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto,
                                      List<ValidMessage> validList, StringBuffer sb,
                                      boolean sysParam) {
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(cfetsBondDealRequestQuoteDto.getEntrustId());
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(cfetsBondDealRequestQuoteDto.getEntrustId());
        // 勾选机构的券面总额应小于等于剩余委托券面总额
        List<CounterInfoDto> list = cfetsBondDealRequestQuoteDto.getCounterInfoList();
        if (!StringUtil.isEmptyList(list)) {
            CounterInfoDto counterInfoDto = list.get(0);
            
            // 净价、到期收益率 、行权收益率 小数处理
            if (etrEntrust != null) {
                etrEntrust.setPrice(ArithmeticUtil.round(etrEntrust.getPrice(), 4, RoundingMode.HALF_UP));
            }
            if (etrEntrustAdd != null) {
                etrEntrustAdd.setMatureYield(ArithmeticUtil.round(etrEntrustAdd.getMatureYield(), 4, RoundingMode.HALF_UP));
                etrEntrustAdd.setExerYield(ArithmeticUtil.round(etrEntrustAdd.getExerYield(), 4, RoundingMode.HALF_UP));
            }

            if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getPs(),
                BussinessDictCodeList.交易方向_买入.getValue())) {
                if (etrEntrustAdd != null && StringUtil.equals(etrEntrustAdd.getCalMode(),
                    BussinessDictCodeList.计算选择方式_净价.getValue())) {
                    if (counterInfoDto.getUnitCprice() != null
                        && etrEntrust.getPrice() != null
                        && counterInfoDto.getUnitCprice().compareTo(
                            etrEntrust.getPrice()) > 0) {
                        if (sysParam) {
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_014")));
                        }
                        else {
                            sb.append(this.getMessage("CFETS_COMMON_E_014"));
                            sb.append(GlobalConstant.HTML_NEWLINE);
                        }
                    }
                }
                else if (etrEntrustAdd != null && StringUtil.equals(etrEntrustAdd.getCalMode(),
                    BussinessDictCodeList.计算选择方式_到期收益率.getValue())) {
                    if (counterInfoDto.getYield() != null
                        && etrEntrustAdd.getMatureYield() != null
                        && counterInfoDto.getYield().compareTo(
                        	ArithmeticUtil.multiply(etrEntrustAdd.getMatureYield(), BigDecimal.valueOf(100d))) < 0) {
                        if (sysParam) {
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_015")));
                        }
                        else {
                            sb.append(this.getMessage("CFETS_COMMON_E_015"));
                            sb.append(GlobalConstant.HTML_NEWLINE);
                        }
                    }
                }
                else if (etrEntrustAdd != null && StringUtil.equals(etrEntrustAdd.getCalMode(),
                    BussinessDictCodeList.计算选择方式_行权收益率.getValue())) {
                    if (counterInfoDto.getExerYield() != null
                        && etrEntrustAdd.getExerYield() != null
                        && counterInfoDto.getExerYield().compareTo(
                        	ArithmeticUtil.multiply(etrEntrustAdd.getExerYield(), BigDecimal.valueOf(100d))) < 0) {
                        if (sysParam) {
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_016")));
                        }
                        else {
                            sb.append(this.getMessage("CFETS_COMMON_E_016"));
                            sb.append(GlobalConstant.HTML_NEWLINE);
                        }
                    }
                }
            }
            else if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getPs(),
                BussinessDictCodeList.交易方向_卖出.getValue())) {
                if (etrEntrustAdd != null && StringUtil.equals(etrEntrustAdd.getCalMode(),
                    BussinessDictCodeList.计算选择方式_净价.getValue())) {
                    if (counterInfoDto.getUnitCprice() != null
                        && etrEntrust.getPrice() != null
                        && counterInfoDto.getUnitCprice().compareTo(
                            etrEntrust.getPrice()) < 0) {
                        if (sysParam) {
                            validList.add(new ValidMessage(this.getMessage("CFETS_COMMON_E_017")));
                        }
                        else {
                            sb.append(this.getMessage("CFETS_COMMON_E_017"));
                            sb.append(GlobalConstant.HTML_NEWLINE);
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
	public BigDecimal checkUsedShareAmt(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entrustId", cfetsBondDealRequestQuoteDto.getEntrustId());
		if(!StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getQuoteId())){
			param.put("quoteId", "");
		}else{
			param.put("quoteId", cfetsBondDealRequestQuoteDto.getQuoteId());
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
	public WorkFlowParameter<CfetsBondDealRequestQuoteDto> getWorkFlowParameter(CfetsBondDealRequestQuoteDto dto) {
		WorkFlowParameter<CfetsBondDealRequestQuoteDto> parameter = new WorkFlowParameter<CfetsBondDealRequestQuoteDto>();
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
	public Boolean insertWflBondRequestQuote(CfetsBondDealRequestQuoteDto dto) {
		return restTemplate.postForObject(
				ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/insertWflBondRequestQuote",
				dto, Boolean.class);
	}
	
    // add by etliu 20210309 银行间上下行多组合 start
    /**
     * 计算多组合组合分配列表中的全价总额
     * @param entity
     */
    private void calcListFpriceAmt(CfetsBondDealRequestQuoteDto entity) {
        if (StringUtil.isEmptyList(entity.getCfetsBondDealRequestQuoteDtoList())) {
            return;
        }
        
        for (CfetsBondDealRequestQuoteDto tempDataDto : entity.getCfetsBondDealRequestQuoteDtoList()) {
        }
    }
    // add by etliu 20210309 银行间上下行多组合 end
	
	/**
     * 插入操作
     */
    @Override
    public void doInsert(CfetsBondDealRequestQuoteDto entity,OperationType type){
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
        insertWflBondRequestQuote(entity);
        doInsetOrUpdateForPtlPositionHold(entity);
    }
    
	@Override
    public void doUpdate(CfetsBondDealRequestQuoteDto entity){
        Map<String,Object> map = new HashMap<>();
        // 上传文件修改更新
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setBussinessId(entity.getBusinessId());
        mstFileInputParameter.setFileList(entity.getFileArray());
        if (mstFileCommonService != null) {
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
        if (!StringUtil.equals("revoke", entity.getOperatorType()) && !StringUtil.equals("delete", entity.getOperatorType())) {
            // 重新insert
        	insertWflBondRequestQuote(entity);
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
     * 退回
     * @param entity
     */
    @Override
	public void doBack(CfetsBondDealRequestQuoteDto entity){
		//回退释放头寸
    	doDeletePtlPositionHold(entity.getWorkflowId());
    }
    
    /**
     * 删除操作
     */
    @Override
    public void doDelete(String workflowId) {
    	doDeletePtlPositionHold(workflowId);
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
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by amyli 
	 * 20201230
	 * @param dto
	 */
	public void doInsetOrUpdateForPtlPositionHold(CfetsBondDealRequestQuoteDto dto) {
		restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsBondDealQuotedPrice/doInsetOrUpdateRequestForPtlPositionHold", dto,
				Integer.class);
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
     * 查询债券是否含权债
     * @param finprodId 金融产品代码
     * @return 是否含权债
     */
    public String selectAssetIsRight(String finprodId) {
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct != null) {
            return finProduct.getIsRight();
        }
        
        return DictCodeList.是否_否.getValue();
    }
    
    public List<CfetsBondDealRequestQuoteDto> getCfetsBondDealRequestQuoteDtoList(String quoteId) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("quoteId", quoteId);
        map.put("loginUserId", LoginUserUtil.getUserName());
        return restTemplate.postForList(ServiceApplication.BANKUPLINK_SERVICE_NAME
                + "/CfetsBondDealQuotedPrice/getCfetsBondDealRequestQuoteDtoList",
                map, CfetsBondDealRequestQuoteDto.class);
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
        wflValidInputParameter.setPageId(StringUtil.isNotEmpty(pageId)?pageId:"CfetsBondDealQuotedPrice003");
        //设置报价编号
        wflValidInputParameter.setBusinessDataPk1(quoteId);
        wflValidInputParameter.setWorkflowId(workflowId);
        if(StringUtil.isNotEmpty(mstWorkflowCommonServie)&&mstWorkflowCommonServie.isInWflProcess(wflValidInputParameter)){
            validList.add(new ValidMessage(getMessage("BUSS_COM_E_0023")));
        }
        return validList;
    }


    public CfetsBondDealRequestQuoteDto queryRequestQuoteByEntrustIdList(String entrustIdListStr) {
        List <String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, entrustIdListStr);
        if(entrustIdList.size()<1){
            return new CfetsBondDealRequestQuoteDto();
        }
        List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<>();
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = this.queryEntrustInfoByEntrustId(entrustIdList.get(0));
        BigDecimal avaNum = BigDecimal.ZERO;
        BigDecimal price = cfetsBondDealRequestQuoteDto.getPrice();
        for(String entrustId:entrustIdList){
            CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuote = this.queryEntrustInfoByEntrustId(entrustId);
//            	cfetsBondDealRequestQuote.
            avaNum = com.joyintech.calculation.util.ArithmeticUtil.doAdd(avaNum,cfetsBondDealRequestQuote.getAvaNum());
            cfetsBondDealRequestQuote.setEntrustAvaNum(cfetsBondDealRequestQuote.getAvaNum());
            cfetsBondDealRequestQuote.setAvaNum(null);
            if(StringUtil.equals(cfetsBondDealRequestQuoteDto.getPs(), BussinessDictCodeList.交易方向_买入.getValue())){
                if(StringUtil.isNotEmpty(price)&&price.compareTo(cfetsBondDealRequestQuote.getPrice())>0){
                    price=cfetsBondDealRequestQuote.getPrice();
                }
            }else{
                if(StringUtil.isNotEmpty(price)&&price.compareTo(cfetsBondDealRequestQuote.getPrice())<0){
                    price=cfetsBondDealRequestQuote.getPrice();
                }
            }
            cfetsBondDealRequestQuoteDtoList.add(cfetsBondDealRequestQuote);
        }
        cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(cfetsBondDealRequestQuoteDtoList);
        cfetsBondDealRequestQuoteDto.setAvaNum(avaNum);
        cfetsBondDealRequestQuoteDto.setPrice(price);
        return cfetsBondDealRequestQuoteDto;
    }
}
