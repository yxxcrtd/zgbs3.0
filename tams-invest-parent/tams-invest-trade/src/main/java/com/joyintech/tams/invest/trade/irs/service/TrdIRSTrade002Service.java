/*
 * 文件名：TrdIRSTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：admin
 * 修改时间：2019年7月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.irs.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.invest.common.constant.BussinessJixuConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.dto.FinCashResetRuleDto;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.AssetInterestCalForIRS;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseFinProductCashInputDto;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.common.TrdCommDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.common.util.ValItemValueGenUtil;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.trade.irs.dto.IRSFeeInfoDto;
import com.joyintech.tams.invest.trade.irs.dto.TrdIRSTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;

@Service
@Transactional(rollbackFor=Exception.class)
public class TrdIRSTrade002Service extends BaseService<TrdIRSTradeDto> implements BaseProcessService<TrdIRSTradeDto>  {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdIRSTrade002Service.class);

	/**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
	
	/**
     * MultiApproveTradeCheckService
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;
    
    /**
     * BaseTradeProductDealCommService
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    /**
     * 共通Mapper的通用代理方案
     */
    @Autowired
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    /**
     * 
     */
    @Autowired
    private CommonBusinessService commonBusinessService;
	
    /**
     * 日历
     */
    @Autowired
    private DateCalService dateCalService;

    /**
     * 估值接口
     */
    @Autowired
    private ValuationInterfService valuationInterfService;

    @Autowired
    FinTradeProductMapper finTradeProductMapper;

    @Autowired
    PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    FinProductTypeMapper finProductTypeMapper;

    @Autowired
    PtlTradeMapper ptlTradeMapper;

    @Autowired
    TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    StlFinProductAcctMapper stlFinProductAcctMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflPaTrdProductDealMapper wflPaTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealOthMapper wflTrdProductDealOthMapper;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    // 调用投管接口 add by wm 20190211 start
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;
    
    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;
    
    // 调用投管接口 add by wm 20190211 end
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }
    
    
    /**
     * 新建
     * {@inheritDoc}
     */
	 @Override
    public void doInsert(TrdIRSTradeDto entity, OperationType operationType) {
        // 新建交易
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            this.handleBussFlowData(entity);
        }
        // 撤销交易
        else {
            this.doRevokeInsert(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdate(TrdIRSTradeDto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
            && !StringUtil.equals("delete", entity.getOperateType())) {
            // 先删除流程中数据
            this.doDelete(entity.getWorkflowId());

            // 重新insert
            this.handleBussFlowData(entity);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        
//        //根据工作流代码查询交易相关信息
//        WflTrdProductDealAdd wflTrdProductDealAdd = restTemplate.getForObject(
//            ServiceApplication.TRADE_SERVICE_NAME
//                    + "/common/trdcommonbase/selectWflTrdProductDealAdd/" + workflowId, WflTrdProductDealAdd.class);
//        //删除授信占用
//        if (null != wflTrdProductDealAdd && null != wflTrdProductDealAdd.getTradeId()){
//            riskCreditListCommonValidatorService.deleteCredit(wflTrdProductDealAdd.getTradeId());
//        }
    }

    /**
     * 
     * {@inheritDoc}
     */
	@Override
	public void doComplete(TrdIRSTradeDto entity, OperationType operationType) {
		// TODO Auto-generated method stub
	    //设置母组合代码和子组合代码--范双虎
	    entity.setActPortfolioId(entity.getPortfolioId());
	    entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
	    
		this.prepareEntity(entity);

        List<FinCashInfo> finCashInfoList =new ArrayList<FinCashInfo>();

		// 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易表
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, entity.getTrdProductDealAdd());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展表
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(paTrdProductDeal, entity.getPaTrdProductDeal());
        BaseFinProductCashInputDto dto = null;
        List<PtlTrade> ptlTradeList = new ArrayList<>();
        if (!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)) {
            // 交易类金融产品
        	FinTradeProduct finTradeProduct = entity.getFinTradeProduct();
        	// 金融产品分类
        	FinProductType finProductType = entity.getFinProductType();
            // 组合交易信息表
            PtlTrade ptlTrade = entity.getPtlTrade();
            // 金融产品账户信息 本方账户
            StlFinProductAcct stlFinProductAcct= entity.getStlFinProductAcct();
            // 金融产品账户信息 对手方账户
            StlFinProductAcct stlFinProductAcctCor= entity.getStlFinProductAcctCor();
            // 费用
            List<TrdTradeFeeDetail> trdTradeFeeDetailList = entity.getTrdTradeFeeDetailList();
            
            // add by wangpeng 获取估值分类123 start
            ValItemValueGenUtil.genTypeForFinProductType(finTradeProduct, finProductType);
            // add by wangpeng end

            finTradeProductMapper.insertSelective(finTradeProduct);
            paTrdProductDealMapper.insertSelective(paTrdProductDeal);
            finProductTypeMapper.insertSelective(finProductType);
            ptlTradeMapper.insertSelective(ptlTrade);
            trdTradeFeeDetailList.forEach(trdTradeFeeDetail -> trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail));
            ptlTradeList.add(ptlTrade);
            if(StringUtil.isNotEmpty(entity.getPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
                // 组合交易信息表
                PtlTrade actPtlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(actPtlTrade, ptlTrade);
                //母组合代码
                actPtlTrade.setPortfolioId(entity.getPortfolioId());
                ptlTradeMapper.insertSelective(actPtlTrade);
                ptlTradeList.add(actPtlTrade);
            }

            List<StlFinProductAcct>accList = new ArrayList<>();
            if(stlFinProductAcct !=null) {
                stlFinProductAcctMapper.insertSelective(stlFinProductAcct);
            }
            if(stlFinProductAcctCor!=null) {
                stlFinProductAcctMapper.insertSelective(stlFinProductAcctCor);
            }

            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
            baseTradeProductDealDto.setTrdProductDeal(trdProductDeal); // 金融产品交易
            baseTradeProductDealDto.setTrdProductDealAdd(trdProductDealAdd); // 金融产品附属
            finProductTradeDto.setBaseTradeProductDealDtos(Arrays.asList(baseTradeProductDealDto));

            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入交易基本信息与[实际]的组合持仓流水及现金流水
            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);
            
            //子组合插入流水
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(), entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }

            // 7.2删除原业务流程表中的交易记录
            String workflowId = entity.getWorkflowId();
            if (StringUtil.isBlank(workflowId)) {
                WflTrdProductDealKey wflTrdProductDealKey = new WflTrdProductDealKey();
                wflTrdProductDealKey.setTradeId(entity.getTradeId());
                wflTrdProductDealKey.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                WflTrdProductDeal wflTrdProductDeal = bussCommonMapperDelegateService.selectByMultiPrimaryKey(
                    WflTrdProductDealMapper.class, wflTrdProductDealKey, WflTrdProductDeal.class);

                if (wflTrdProductDeal != null) {
                    workflowId = wflTrdProductDeal.getWorkflowId();
                }
            }
            this.deleteBussFlowData(workflowId);

            // jixu接口 start
            dto = this.cashFlowLogic(entity);
            // jixu接口 end
            
        	// 所有交易生效后调用的共通方法
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(entity.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
            inputDto.setTrdProductDeal(trdProductDeal);
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                inputDto.setActPortfolioId(entity.getActPortfolioId());
            }
            inputDto.setPtlTradeList(ptlTradeList);
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
//
//            //插入风险试算结果信息
//            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
        }
        // 撤销
        else {
            this.doCompleteRevoke(entity, ptlTradeList);
            //根据业务代码查询现金流信息
            List<FinCashInfo> list = this.getFinCashInfoList(entity.getTradeId());
            //封装Dto
            if(!list.isEmpty()){
                for(FinCashInfo finCashInfo : list){
                    // 现金流id
                    finCashInfo.setCashId(finCashInfo.getCashId());
                    // 现金流一级类型 = 数据字典缓存.现金流一级类型.利息
                    finCashInfo.setCashType1(finCashInfo.getCashType1());
                    // 币种 = 画面.币种
                    finCashInfo.setCcy(finCashInfo.getCcy());
                    // 业务代码 = 金融产品交易Entity.交易编号
                    finCashInfo.setFinprodId(finCashInfo.getFinprodId());
                    // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
                    finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
                    // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
                    finCashInfo.setBranch(finCashInfo.getBranch());
                    // 工作流代码
                    finCashInfo.setWorkflowId(finCashInfo.getWorkflowId());
                    // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
                    finCashInfo.setCashType2(finCashInfo.getCashType2());
                    finCashInfoList.add(finCashInfo);
                }
               // dto.setFinCashInfoList(finCashInfoList);
            }
        }
        // 调用投管接口 add by wm 20190211 start
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdIRSTrade002");
        if(!operationType.equals(OperationType.REVOKE) && !operationType.equals(OperationType.DELETE)){
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
        }
        else{
            investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
        }
        investInterfCommDto.setPortfolioId(entity.getPortfolioId());
        investInterfCommDto.setTrdProductDeal(trdProductDeal);
        investInterfCommDto.setTrdProductDealAdd(trdProductDealAdd);
        investInterfCommDto.setTradeFeeDetaillist(entity.getTrdTradeFeeDetailList());
        List<StlFinProductAcct> stlFinProductAcctList = new ArrayList<>();
        if(entity.getStlFinProductAcct() !=null) {
        	stlFinProductAcctList.add(entity.getStlFinProductAcct());
        }
        if(entity.getStlFinProductAcctCor()!=null) {
        	stlFinProductAcctList.add(entity.getStlFinProductAcctCor());
        }
        investInterfCommDto.setStlFinProductAcctList(stlFinProductAcctList);
        if(dto!=null){
        	investInterfCommDto.setFinCashPayRuleList(dto.getFinCashPayRuleList());
            investInterfCommDto.setFinRateInfoList(dto.getFinRateInfoList());
        }
        investInterfCommDto.setOtherTypeForConvert(entity.getIrsType());
        investInterfCommService.doInvestInterf(investInterfCommDto);
        // 调用投管接口 add by wm 20190211 end
        
//        // jixu接口 start
//        TrdCommDto trdCommDto = new TrdCommDto();
//        if (OperationType.DELETE == operationType) {
//            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_DELETE);;
//        }
//        else if (OperationType.NEW == operationType) {
//            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_ADD);
//        }
//        else {
//            trdCommDto.setOperateType(BussinessConstant.OPERATE_TYPE_UPDATE);
//        }
//        trdCommDto.setHeader(BussinessJixuConstant.消息头_利率互换开仓交易);
//        trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//        trdCommDto.setTrdProductDeal(entity.getTrdProductDeal());
//        trdCommDto.setPtlTradeList(ptlTradeList);
//        trdCommDto.setFinTradeProduct(entity.getFinTradeProduct());
//        if(StringUtil.isNotEmpty(entity.getTrdProductDealAdd())) {
//            trdCommDto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
//        }
//        trdCommDto.setTradeFeeDetaillist(entity.getTrdTradeFeeDetailList());
//        if(operationType.equals(OperationType.REVOKE) || operationType.equals(OperationType.DELETE)){
//            trdCommDto.setFinCashInfoList(finCashInfoList);
//        }
//        if (dto != null) {
//            // List<现金流信息Entity>
//            trdCommDto.setFinCashInfoList(dto.getFinCashInfoList());
//            // List<现金流支付规则表Entity>
//            trdCommDto.setFinCashPayRuleList(dto.getFinCashPayRuleList());
//            // List<现金流计算规则表Entity> 只存固定端
//            trdCommDto.setFinCashCalcRuleList(dto.getFinCashCalcRuleList());
//            // List<现金流利率信息表Entity>
//            trdCommDto.setFinRateInfoList(dto.getFinRateInfoList());
//            // List<现金流计划表Entity>
//            trdCommDto.setFinCashPlanList(dto.getFinCashPlanList());
//            // List<利率重置计划表Entity>
//            trdCommDto.setFinCashResetPlanList(dto.getFinCashResetPlanList());
//        }
//        valuationInterfService.runValInvest2Jixu(trdCommDto);
//        if (dto != null) {
//            TrdCommDto trdCommDto1 = new TrdCommDto();
//            BeanUtil.copyPropertiesIgnoreNull(trdCommDto1, trdCommDto);
//            trdCommDto1.setPtlTradeList(null);
//            trdCommDto1.setHeader(BussinessJixuConstant.消息头_利率互换现金流);
//            // 固定端
//            String cashIdFix = dto.getFinCashInfoList().get(0).getCashId();
//            setTrdCommDto(dto, trdCommDto1, cashIdFix, true);
//            valuationInterfService.runValInvest2Jixu(trdCommDto1);
//            // 浮动端
//            TrdCommDto trdCommDto2 = new TrdCommDto();
//            BeanUtil.copyPropertiesIgnoreNull(trdCommDto2, trdCommDto1);
//            trdCommDto2.setPtlTradeList(null);
//            setTrdCommDto(dto, trdCommDto2, cashIdFix, false);
//            valuationInterfService.runValInvest2Jixu(trdCommDto2);
//        }
//        // jixu接口 end
        //        调用接口  add by zhangpuyu 2019/12/6 start
        try {
            List<String> tradeIds = new ArrayList<>();
            tradeIds.add(entity.getTradeId());
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
        //        调用接口  add by zhangpuyu 2019/12/6 end
	}

    private List<FinCashInfo> getFinCashInfoList(String tradeId) {
        return restTemplate.getForList(ServiceApplication.TRADE_SERVICE_NAME
                        + "/TrdIRSTrade/getFinCashInfoList?tradeId="
                        + tradeId, FinCashInfo.class);
    }

    /**
     * 区分固固定端和浮动端
     *
     * @param paraDto 参数
     * @param trdCommDto 返回结果
     * @param cashIdFix 固定端cashId
     * @param fixFlag 固定端flag
     * @see
     * @since 1.0
     */
    private void setTrdCommDto(BaseFinProductCashInputDto paraDto, TrdCommDto trdCommDto,
                               String cashIdFix, boolean fixFlag) {

        // List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        paraDto.getFinCashInfoList().forEach(dto -> {
            if (StringUtil.equals(dto.getCashId(), cashIdFix) == fixFlag) {
                finCashInfoList.add(dto);
            }
        });
        trdCommDto.setFinCashInfoList(finCashInfoList);

        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();
        paraDto.getFinCashPayRuleList().forEach(dto -> {
            if (StringUtil.equals(dto.getCashId(), cashIdFix) == fixFlag) {
                finCashPayRuleList.add(dto);
            }
        });
        trdCommDto.setFinCashPayRuleList(finCashPayRuleList);

        // List<现金流计算规则表Entity> 只存固定端
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        paraDto.getFinCashCalcRuleList().forEach(dto -> {
            if (StringUtil.equals(dto.getCashId(), cashIdFix) == fixFlag) {
                finCashCalcRuleList.add(dto);
            }
        });
        trdCommDto.setFinCashCalcRuleList(finCashCalcRuleList);

        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();
        paraDto.getFinRateInfoList().forEach(dto -> {
            if (StringUtil.equals(dto.getCashId(), cashIdFix) == fixFlag) {
                finRateInfoList.add(dto);
            }
        });
        trdCommDto.setFinRateInfoList(finRateInfoList);

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();
        paraDto.getFinCashPlanList().forEach(dto -> {
            if (StringUtil.equals(dto.getCashId(), cashIdFix) == fixFlag) {
                finCashPlanList.add(dto);
            }
        });
        trdCommDto.setFinCashPlanList(finCashPlanList);

        // List<利率重置计划表Entity>
        List<FinCashResetPlan> finCashResetPlanList = new ArrayList<>();
        paraDto.getFinCashResetPlanList().forEach(dto -> {
            if (StringUtil.equals(dto.getCashId(), cashIdFix) == fixFlag) {
                finCashResetPlanList.add(dto);
            }
        });
        trdCommDto.setFinCashResetPlanList(finCashResetPlanList);
    }


    /**
     * 撤销doComplete
     *
     * @param entity 参数
     * @see
     * @since 1.0
     */
    private void doCompleteRevoke(TrdIRSTradeDto entity, List<PtlTrade> ptlTradeList) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId=" + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                baseDto.getTrdProductDeal().setWorkflowId(entity.getWorkflowId());
                baseDto.getTrdProductDeal().setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                baseDto.getTrdProductDeal().setCancelRemark(entity.getRevokeReason());
                if (StringUtil.isEmptyList(baseDto.getPtlTrade())) {
                    ptlTradeList.addAll(baseDto.getPtlTrade());
                }
                if (bussCommonMapperDelegateService != null) {
                    PaTrdProductDeal paTrdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(
                        PaTrdProductDealMapper.class, baseDto.getTrdProductDeal().getTradeId(), PaTrdProductDeal.class);
                    paTrdProductDeal.setTradeId(
                        paTrdProductDeal.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                    paTrdProductDeal.setCreateUser(LoginUserUtil.getUserName());
                    paTrdProductDeal.setCreateTime(new Date());
                    paTrdProductDeal.setUpdateUser(paTrdProductDeal.getCreateUser());
                    paTrdProductDeal.setUpdateTime(paTrdProductDeal.getCreateTime());
                    paTrdProductDealMapper.insertSelective(paTrdProductDeal);
                }

                //删除授信占用
                if (null != baseDto.getTrdProductDeal().getTradeId() && !"".equals(baseDto.getTrdProductDeal().getTradeId())){
                    riskCreditListCommonValidatorService.deleteCredit(baseDto.getTrdProductDeal().getTradeId());
                }
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtoList);

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            // 调用业务组件.【金融产品交易共通.交易产品接口】, insert
            // 调用业务组件.【金融产品交易共通.交易产品接口】,写入[实际]的组合持仓流水及现金流水
            baseTradeProductDealCommService.execute(finProductTradeDto);
            //子组合插入流水
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getActPortfolioId(),
                    entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }

        // 插入风险试算结果
        //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
        // 删除原业务流程记录
        this.deleteBussFlowData(entity.getWorkflowId());

        // 调用基础组件.【交易报送组件】删除交易报送信息
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/deleteRepTrade",
            Arrays.asList(entity.getTradeId()), Boolean.class);

    }

    /**
     * 保存时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(TrdIRSTradeDto entity) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
            && StringUtil.isNotEmpty(entity.getTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                entity.getTradeId());
        }

        if (!StringUtil.equals("submit", entity.getOperateType())) {
            return;
        }

        this.prepareEntity(entity);

        // 调用授信共同 sjj start
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(trdProductDeal, entity.getTrdProductDeal());
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
        trdProductDeal.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
        trdProductDeal.setTgBusiType(DictCodeListExt2.平安资产类型_利率互换.getValue());
        inputDto.setTrdProductDeal(trdProductDeal);
        AllTradeCommonUtil.handleBeforeTradeComplete(inputDto,false);
        // 调用授信共同 sjj end

        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
        WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, entity.getTrdProductDeal());
        wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
        WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
        BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, entity.getTrdProductDealAdd());
        wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易Entity -> 金融产品交易其它业务流程表
        WflTrdProductDealOth wflPaTrdProductDealOth = new WflTrdProductDealOth();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDealOth, entity.getWflTrdProductDealOth());
        wflPaTrdProductDealOth.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());

        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
        WflPaTrdProductDeal wflPaTrdProductDeal = new WflPaTrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(wflPaTrdProductDeal, entity.getPaTrdProductDeal());
        wflPaTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
		//add by wangpeng 20190919 流程中 交易方向存入回购交易方向字段
        wflPaTrdProductDeal.setBackLendDirection(entity.getIrsType());
        //add end

        wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
        wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
        wflPaTrdProductDealMapper.insertSelective(wflPaTrdProductDeal);
        wflTrdProductDealOthMapper.insertSelective(wflPaTrdProductDealOth);

    }
    
    
    /**
     * 由画面输入的参数dto，构筑整个需要反映到数据库的实体
     *
     * @param dto 画面输入的参数dto
     * @see
     * @since 1.0
     */
    public void prepareEntity(TrdIRSTradeDto dto) {
        //流程中不会塞子组合代码，这里直接塞成组合代码--范双虎
        if(!StringUtil.isNotEmpty(dto.getActPortfolioId())) {
            dto.setActPortfolioId(dto.getPortfolioId());
        }

    	dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
    	dto.setFinprodType(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
    	
		// 金融产品交易Entity 构造开始----------------------------------------------------------
		TrdProductDeal entry = new TrdProductDeal();
		// 交易编号，交易币种，交易日，起息日，到期日
		BeanUtil.copyPropertiesIgnoreNull(entry, dto);
		// 业务类型
		entry.setBusiType(DictCodeListExt2.共通业务类型_利率互换交易.getValue());
		// 金融产品代码
		entry.setFinprodId(entry.getTradeId());// 是交易编号，不是标的代码
		// 金融产品类型（投管分类）
		entry.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
		// 金融产品类型（估值核算）
		entry.setFinprodType(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
		// 分支序号
		entry.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
		// 交易方向
		entry.setPs(DictCodeList.交易方向_买入.getValue());
		// 交割日
		entry.setSettleDate(dto.getTradeDate());
		// 交易份额
		entry.setShareAmt(dto.getPrin() == null ? BigDecimal.ZERO : dto.getPrin().multiply(new BigDecimal(10000)));
		// 交易本金
		entry.setPrinAmt(entry.getShareAmt());
		// 未随交易支付费用=画面.交易手续费 + 画面.结算费
		entry.setFeeAmtUnpay(dto.getFeeB06() == null ? BigDecimal.ZERO
				: dto.getFeeB07() == null ? BigDecimal.ZERO : dto.getFeeB06().add(dto.getFeeB07()));
		// 总交易费用=画面.交易手续费 + 画面.结算费
		entry.setFeeAmt(entry.getFeeAmtUnpay());

		// yield
		if(dto.getYield()!=null) {
			entry.setYield(dto.getYield().divide(new BigDecimal(100), RoundingMode.HALF_UP));
		}
		// 人员信息
		TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
		if (personInfo != null) {
			// 本方交易员
			entry.setTrader(personInfo.getTrader());
			// 交易对手
			entry.setCounterId(personInfo.getCounterId());
			// 是否外部交易
			entry.setIsOutTrade(personInfo.getIsOutTrade());
			// 对手方交易员
			entry.setCounterTrader(personInfo.getCounterTrader());
			// 联系方式
			entry.setCounterContact(personInfo.getContactInfo());
		}
		dto.setTrdProductDeal(entry);
		// 金融产品交易Entity 构造结束----------------------------------------------------------

		// 金融产品交易附属表Entity
		// 构造开始----------------------------------------------------------
		TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
		// 交易编号，组合代码，委托代码，
		BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd, dto);
		// 生成方式
		trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
		dto.setTrdProductDealAdd(trdProductDealAdd);
		// 金融产品交易附属表Entity
		// 构造结束----------------------------------------------------------

		// 金融产品交易扩展Entity 构造开始----------------------------------------------------------
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        // 交易编号
        paTrdProductDeal.setTradeId(dto.getTradeId());
//        // 平安业务类型(数据字典缓存.平安业务类型)
//        paTrdProductDeal.setPaBussType(DictCodeListExt2.平安资产类型_利率互换.getValue());
        // 交易方向
        paTrdProductDeal.setBackLendDirection(dto.getPs());
        // 交易所属部门
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
        // 工作流代码
        paTrdProductDeal.setWorkflowId(dto.getWorkflowId());
        dto.setPaTrdProductDeal(paTrdProductDeal);
        // 金融产品交易扩展Entity 构造结束----------------------------------------------------------
        
        
		// 组合交易表Entity 构造开始----------------------------------------------------------
		PtlTrade ptlTrade = new PtlTrade();
		// 组合代码
		ptlTrade.setPortfolioId(dto.getPortfolioId());
		// 业务编号
		ptlTrade.setBusiNo(dto.getTradeId());
		// 业务表
		ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
		// 业务明细代码
		ptlTrade.setBusiId(dto.getTradeId());
		// 业务类型
		ptlTrade.setBusiType(DictCodeListExt2.共通业务类型_利率互换交易.getValue());
		// 实际发生日
		ptlTrade.setSettleDate(dto.getTradeDate());
		//真实组合代码（子组合代码）--范双虎
		ptlTrade.setPortfolioId(dto.getActPortfolioId());
		dto.setPtlTrade(ptlTrade);
		// 组合交易表Entity 构造结束----------------------------------------------------------

		// 金融产品账户信息Entity构造开始--------------------------------------------------------
		if (StringUtil.isNotEmpty(dto.getDealAcct())) {
			StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
			// 金融产品代码
			stlFinProductAcct.setFinprodId(dto.getTradeId());
			// 金融产品分类(投管)
			stlFinProductAcct.setFinprodType2(dto.getFinprodType2());
			// 产品账户
			stlFinProductAcct.setAcctNo(dto.getDealAcct());
			// 币种
			stlFinProductAcct.setCcy(dto.getCcy());
			//
			stlFinProductAcct.setAcctType(DictCodeListExt2.标的账户类型_本方账户.getValue());
			dto.setStlFinProductAcct(stlFinProductAcct);
		}
		if (StringUtil.isNotEmpty(dto.getCorAcct())) {
			StlFinProductAcct stlFinProductAcctCor = new StlFinProductAcct();
			// 金融产品代码
			stlFinProductAcctCor.setFinprodId(dto.getTradeId());
			// 金融产品分类(投管)
			stlFinProductAcctCor.setFinprodType2(dto.getFinprodType2());
			// 产品账户
			stlFinProductAcctCor.setAcctNo(dto.getCorAcct());
			// 币种
			stlFinProductAcctCor.setCcy(dto.getCcy());
			//
			stlFinProductAcctCor.setAcctType(DictCodeListExt2.标的账户类型_对手方账户.getValue());
			dto.setStlFinProductAcctCor(stlFinProductAcctCor);
		}
		// 金融产品账户信息Entity构造结束---------------------------

		// 交易费用明细表Entity 构造开始----------------------------------------------------------
		List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
		TrdTradeFeeDetail trdTradeFeeDetailB06 = new TrdTradeFeeDetail();
		// 交易编号
		trdTradeFeeDetailB06.setTradeId(dto.getTradeId());
		// 费用类型 交易手续费
		trdTradeFeeDetailB06.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
		// 是否随交易支付
		trdTradeFeeDetailB06.setIsPayWithTrade(DictCodeList.是否_否.getValue());
		// 画面.交易货币
		trdTradeFeeDetailB06.setCcy(dto.getCcy());
		// 费用金额
		trdTradeFeeDetailB06.setFeeAmt(dto.getFeeB06());
		trdTradeFeeDetailList.add(trdTradeFeeDetailB06);

		TrdTradeFeeDetail trdTradeFeeDetailB07 = new TrdTradeFeeDetail();
		// 交易编号
		trdTradeFeeDetailB07.setTradeId(dto.getTradeId());
		// 费用类型 交易手续费
		trdTradeFeeDetailB07.setFeeType(DictCodeListExt1.一次性费用类型_结算费.getValue());
		// 是否随交易支付
		trdTradeFeeDetailB07.setIsPayWithTrade(DictCodeList.是否_否.getValue());
		// 画面.交易货币
		trdTradeFeeDetailB07.setCcy(dto.getCcy());
		// 费用金额
		trdTradeFeeDetailB07.setFeeAmt(dto.getFeeB07());
		trdTradeFeeDetailList.add(trdTradeFeeDetailB07);
		
		dto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);
		// 交易费用明细表Entity 构造结束----------------------------------------------------------

		// 交易类金融产品Entity 构造开始----------------------------------------------------
		FinTradeProduct finTradeProduct = new FinTradeProduct();
		// 金融产品代码 = 金融产品交易Entity.交易编号
		finTradeProduct.setFinprodId(dto.getTradeId());
		// 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
		finTradeProduct.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
		// 金融产品类型 = 数据字典缓存.金融产品类型（投管用）数据字典.外汇远期投资交易
		finTradeProduct.setFinprodType2(entry.getFinprodType2());
		// 金融产品类型（估值核算）
		finTradeProduct.setFinprodType(entry.getFinprodType());
		// 名义本金 = 交易币种金额
		finTradeProduct.setPrin(entry.getPrinAmt());
		// 名义币种 = 画面.币种
		finTradeProduct.setCcy(dto.getCcy());
		// 起息日 = 画面.起息日
		finTradeProduct.setVdate(dto.getVdate());
		// 到期日 = 画面.到期日
		finTradeProduct.setMdate(dto.getMdate());
		// 期限(天) = 画面.交割日-画面.交易日
		finTradeProduct.setTerm(dto.getTerm());
		// 计息基础
		finTradeProduct.setBasis(dto.getFixBasis());
		// 本金收益特征
		finTradeProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_非保本.getValue());
		// 交易日历 = 画面.交易日历
		finTradeProduct.setCalendarId(dto.getCalendarId());
		// 期限品种 = 画面.期限品种
		finTradeProduct.setTermType(dto.getTermType());
		// 交易对手 = 画面.交易对手
		finTradeProduct.setCounterId(entry.getCounterId());
		// 互换方式
		finTradeProduct.setIrsType(dto.getIrsType());
		// 持有目的 中债新增 20200409
		finTradeProduct.setHoldPurpose(dto.getHoldPurpose());
		dto.setFinTradeProduct(finTradeProduct);
		// 交易类金融产品Entity 构造结束----------------------------------------------------------

		// 交易产品分类表Entity 构造开始----------------------------------------------------------
		FinProductType finProductType = new FinProductType();
		// 金融产品代码 交易编号、
		finProductType.setFinprodId(dto.getTradeId());
		// 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
		finProductType.setBranch(Long.parseLong((String) CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
		// 分类1
		// 分类2
		// 分类3
		
		// 分类8
		finProductType.setType8(DictCodeListExt2.平安资产类型_利率互换.getValue());
		//新增资产五级分类	王贵奇	2019/8/12	开始
		FinProductTypeConfig config = new FinProductTypeConfig();
		config.setConfigType(DictCodeListExt2.配置类型_无标的投管资产类型映射行内资产分类.getValue());
		config.setInputValue1(DictCodeListExt2.平安资产类型_利率互换.getValue());
		config = restTemplate.postForObject(ServiceApplication.COMMON_SERVICE_NAME + "/core/getAssetFiveType",
				config, FinProductTypeConfig.class);
		// 分类9
		finProductType.setType9(config.getOutputValue2());
		// 分类10
		finProductType.setType10(config.getOutputValue3());
		// 分类11
		finProductType.setType11(config.getOutputValue4());
		// 分类12
		finProductType.setType12(config.getOutputValue5());
		//新增资产五级分类	王贵奇	2019/8/12	结束
		dto.setFinProductType(finProductType);
		// 交易产品分类表Entity 构造结束----------------------------------------------------------

		// 金融产品交易业务流程其他信息Entity
		// 构造开始----------------------------------------------------------
		WflTrdProductDealOth wflTrdProductDealOth = new WflTrdProductDealOth();
		// 交易编号 = 金融产品交易Entity.交易编号
		wflTrdProductDealOth.setTradeId(dto.getTradeId());
		// 计息基础
		wflTrdProductDealOth.setBasis(dto.getFixBasis());
		// 利率 固定利率
		wflTrdProductDealOth.setRate(dto.getYield());
		// 期限(天)
		wflTrdProductDealOth.setTerm(dto.getTerm());
		// 互换方式
		wflTrdProductDealOth.setIrsType(dto.getIrsType());
		// 数据状态 = 数据字典缓存.数据状态.新建
		wflTrdProductDealOth.setStatus(DictCodeList.数据状态_新建.getValue());
		// 固定利率付息频率=画面.固定利率.付息频率
		wflTrdProductDealOth.setPayCycleR(dto.getFixPayCycle());
		// 固定利率首期起息日=画面.固定利率.首期起息日
		wflTrdProductDealOth.setFirstPayDateR(dto.getFirstPayDateR());
		// 行情代码=画面.浮动利率.挂钩利率
		wflTrdProductDealOth.setIntRateId(dto.getIntType());
		// 利差=画面.浮动利率.利差
		wflTrdProductDealOth.setSpreadRateF(dto.getSpreadRate());
		// 浮动利率付息频率=画面.浮动利率.付息频率
		wflTrdProductDealOth.setPayCycleF(dto.getFloatPayCycle());
		// 浮动利率首期起息日=画面.浮动利率.首期起息日
		wflTrdProductDealOth.setFirstPayDateF(dto.getFirstPayDateF());
		// 工作流代码=系统后台工作流框架采番
		wflTrdProductDealOth.setWorkflowId(dto.getWorkflowId());
		// 标的金融产品代码
		wflTrdProductDealOth.setPledgeFinprodId(dto.getTradeId());
		// 标的金融产品类型
		wflTrdProductDealOth.setPledgeFinprodType(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
		// 工作流代码
		dto.setWflTrdProductDealOth(wflTrdProductDealOth);
		// 金融产品交易业务流程其他信息Entity
		// 构造结束----------------------------------------------------------

    }
    
    
    private BaseFinProductCashInputDto cashFlowLogic(TrdIRSTradeDto input) {
        // 分支序号
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        Long branchLong = Long.parseLong(branch);

        BaseFinProductCashInputDto inputDto = new BaseFinProductCashInputDto();
        // 互换方式
        String  irsType = input.getIrsType();
        // 是否收取固定 ： TRUE 收取固定  FALSE 收取浮动
        boolean irsTypeIsR = DictCodeListExt2.互换方式_收取固定.getValue().equals(irsType)?true:false;
       
        // 固定端现金流id
        String cashIdR = commonBusinessService.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                irsTypeIsR?DictCodeListExt2.现金流二级类型_收取.getValue():DictCodeListExt2.现金流二级类型_支付.getValue());
        // 浮动端现金流id
        String cashIdF = commonBusinessService.generateCashFlowCode(input.getTradeId(),
                DictCodeListExt1.现金流业务类型_金融产品.getValue(), branchLong,
                irsTypeIsR?DictCodeListExt2.现金流二级类型_支付.getValue():DictCodeListExt2.现金流二级类型_收取.getValue());
        
        // 现金流id list
        List<String> cashIdList = new ArrayList<String>();
        
        cashIdList.add(cashIdR);
        cashIdList.add(cashIdF);
    	// List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        
        // List<现金流支付规则表Entity>
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();
        
        // List<现金流计算规则表Entity>  只存固定端
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        
        // List<现金流利率信息表Entity>
        List<FinRateInfo> finRateInfoList = new ArrayList<>();

        // List<现金流计划表Entity>
        List<FinCashPlan> finCashPlanList = new ArrayList<>();
        
        // List<利率重置计划表Entity>
        List<FinCashResetPlan> finCashResetPlanList = new ArrayList<>();
        /**
         * 遍历现金流id
         */
        for(String cashId:cashIdList) {
        	// 是否是 固定端
        	boolean isR = StringUtil.equals(cashId,cashIdR);
        	
        	// 现金流信息Entity
        	FinCashInfo finCashInfo = new FinCashInfo();
        	// 现金流id
        	finCashInfo.setCashId(cashId);
            // 现金流一级类型 = 数据字典缓存.现金流一级类型.利息
            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            // 币种 = 画面.币种
            finCashInfo.setCcy(input.getCcy());
            // 业务代码 = 金融产品交易Entity.交易编号
            finCashInfo.setFinprodId(input.getTradeId());
            // 业务类型 = 数据字典缓存.现金流业务类型.金融产品
            finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finCashInfo.setBranch(branchLong);
            // 工作流代码
            finCashInfo.setWorkflowId(input.getWorkflowId());
            // 现金流二级类型 = 数据字典缓存.现金流二级类型.利息
            finCashInfo.setCashType2(irsTypeIsR?
            		(isR?DictCodeListExt2.现金流二级类型_收取.getValue():DictCodeListExt2.现金流二级类型_支付.getValue()):
            			isR?DictCodeListExt2.现金流二级类型_支付.getValue():DictCodeListExt2.现金流二级类型_收取.getValue());
            finCashInfoList.add(finCashInfo);
            
            // 现金流支付规则表Entity 
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            // 现金流代码 = 现金流信息Entity-利息.现金流代码
            finCashPayRule.setCashId(cashId);
            // 生效日期 = 画面.起息日
            finCashPayRule.setEffDate(input.getVdate());
            // 支付频率 = 画面.付息频率
            finCashPayRule.setPayCycle(isR?input.getFixPayCycle():input.getFloatPayCycle());
            // 支付频率值 1
            finCashPayRule.setPayCycleVal(1L);
            // 截止日营业日准则 = 画面.计息营业日准则
            finCashPayRule.setEndTdayRule(input.getEndTdayRule());
            // 支付日营业日准则 = 画面.付息营业日准则
            finCashPayRule.setPayTdayRule(input.getPayTdayRule());
            // 推算方式=if 画面.首期起息日 = 画面.起息日 then 数据字典缓存.推算方法.起息日向后 else 数据字典缓存.推算方法.首次支付日
            if(isR&&DateUtil.compareTo(input.getVdate(), input.getFirstPayDateR())==0) {
                finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
            }else if(!isR&&DateUtil.compareTo(input.getVdate(), input.getFirstPayDateF())==0){
                finCashPayRule.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
            }else {
            	finCashPayRule.setCalcRule(DictCodeList.推算方式_首次支付日.getValue());
            }
            // 首次支付日=画面.首期起息日
            finCashPayRule.setFirstPayDate(isR?input.getFirstPayDateR():input.getFirstPayDateF());
            //现金流单位=数据字典缓存.现金流单位.总额
            finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
            //金融产品代码=金融产品交易Entity.交易编号
            finCashPayRule.setFinprodId(input.getTradeId());
            //金融产品类型（估值核算）=调用各种业务共通组件.数据字典转换（目标数据代码）
            finCashPayRule.setFinprodType(input.getFinprodType());
            //金融产品类型（投管分类）=数据字典缓存.金融产品类型（投管用）数据字典.利率互换
            finCashPayRule.setFinprodType2(input.getFinprodType2());
            //分支序号=系统参数.分支序号固定值（和估值核算保持一致，0）
            finCashPayRule.setBranch(branchLong);
            // 工作流代码
            finCashPayRule.setWorkflowId(input.getWorkflowId());
            finCashPayRuleList.add(finCashPayRule);
            
            if(isR) {
            FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
            // 现金流代码 = 现金流信息Entity-利息.现金流代码
            finCashCalcRule.setCashId(cashId);
            // 生效日期 = 画面.起息日
            finCashCalcRule.setEffDate(input.getVdate());
            // 计算类型 = 数据字典缓存.利率类型.固定利率
            finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
            // 基数类型 = 数据字典缓存.基数类型.本金
            finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
            // 基数日期类型 = 数据字典缓存.基数日期类型.当日
            finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
            // 计算基础 = 画面.计息基础
            finCashCalcRule.setBasis(input.getFixBasis());
            // 生效利率 = 画面.固定利率/100
            finCashCalcRule.setYield(ArithmeticUtil.divide(input.getYield(), new BigDecimal(100), 6, RoundingMode.HALF_UP));
            // 是否初始利率 = 数据字典缓存.是否.是
            finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finCashCalcRule.setFinprodId(input.getTradeId());
            // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
            finCashCalcRule.setFinprodType(input.getFinprodType());
            // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
            finCashCalcRule.setFinprodType2(input.getFinprodType2());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finCashCalcRule.setBranch(branchLong);
            // 工作流代码
            finCashCalcRule.setWorkflowId(input.getWorkflowId());
            finCashCalcRuleList.add(finCashCalcRule);
            }
            
            // 现金流利率信息表Entity-利息
            FinRateInfo finRateInfo = new FinRateInfo();
            // 现金流代码 = 现金流信息Entity-利息.现金流代码
            finRateInfo.setCashId(cashId);
            // 生效日 = 画面.起息日
            finRateInfo.setEffDate(input.getVdate());
            // 利率类型 = 数据字典缓存.利率类型.固定利率
            // 一体化对应etliu
            finRateInfo.setIntType(isR?DictCodeList.利率类型_固定利率.getValue():DictCodeList.利率类型_浮动利率.getValue());
            // 计算基础 = 画面.计息基础
            finRateInfo.setBasis(isR?input.getFixBasis():input.getFloatBasis());
            // 系数1
            finRateInfo.setCoefficient(BigDecimal.ONE);
            if(isR) {
                // 初始利率 = 画面.回购利率/100
                finRateInfo.setRate(ArithmeticUtil.divide(input.getYield(), new BigDecimal(100), 6, RoundingMode.HALF_UP));
            }else {
            	// 重置类型=固定端时为空，浮动端时为数据字典缓存.重置类型.随计划重置	
            	finRateInfo.setResetType(DictCodeListExt2.重置类型_随计划重置.getValue());
            	// 重置频率=固定端时为空，浮动端时为画面.利率重置频率
            	finRateInfo.setResetFreq(input.getResetFreq());
            	// 提前观察数量=固定端时为空，浮动端时按照画面.交易日历，画面.起息日 - 画面.首期利率确定日的工作日
            	finRateInfo.setObserveBefDay(new BigDecimal(
            			DateUtil.getBetweenDayNumber(input.getVdate(), input.getFirstResetDate())));
            	// 提前观察单位=固定端时为空，浮动端时数据字典缓存.提前观察单位.工作日
            	finRateInfo.setObserveBefUnit(DictCodeList.提前观察单位_延后时间单位_工作日.getValue());
            	// 利差=固定端时为空，浮动端时为画面.利差
            	finRateInfo.setSpreadRate(input.getSpreadRate());
            	// 基准编号=固定端时为空，浮动端时为挂钩利率
            	finRateInfo.setBenchmarkId(input.getIntType());
            	// 基准类型=固定端时为空，浮动端时为数据字典缓存.基准类型.利率
            	finRateInfo.setBenchmarkType(DictCodeListExt1.基准类型_利率.getValue());
            	// 首期利率确定日=固定端时为空，浮动端时画面.首期利率确定日
            	finRateInfo.setFirstConfirmDate(input.getFirstResetDate());
            }
            // 金融产品代码 = 金融产品交易Entity.交易编号
            finRateInfo.setFinprodId(input.getTradeId());
            // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
            finRateInfo.setBranch(branchLong);
            // 金融产品类型（估值核算） = 调用各种业务共通组件.数据字典转换（目标数据代码）
            finRateInfo.setFinprodType(input.getFinprodType());
            // 金融产品类型（投管分类） = 数据字典缓存.金融产品类型（投管用）数据字典.债券质押式正回购/逆回购
            finRateInfo.setFinprodType2(input.getFinprodType2());
            // 工作流代码
            finRateInfo.setWorkflowId(input.getWorkflowId());
            finRateInfoList.add(finRateInfo);

            
            // 现金流计划表Entity 
            AssetCommonInfoDto scai = new AssetCommonInfoDto();
            // 构建AssetCommonInfoDto
            scai.setFinprodId(input.getFinprodId());
            scai.setVdate(input.getVdate());
            scai.setMdate(input.getMdate());
            // 生成计划用的支付规则对象
            FinCashPayRule finCashPayRuleForPlan = new FinCashPayRule();
    		BeanUtil.copyPropertiesIgnoreNull(finCashPayRuleForPlan, finCashPayRule);
    		// 支付频率=双周时，支付频率值传入2
    		// 支付频率=双周时，支付频率传入周
    		if(StringUtil.equals(finCashPayRuleForPlan.getPayCycle(),DictCodeListExt2.频率_双周.getValue())) {
    			finCashPayRuleForPlan.setPayCycle(DictCodeListExt2.频率_周.getValue());
    			finCashPayRuleForPlan.setPayCycleVal(2L);
    		}
            //获取节假日
            List<String> holidayList = dateCalService.getDealCalendarHoliday(input.getVdate(),input.getMdate(),input.getCalendarId());
            List<FinCashPlanDto> scheList = AssetInterestCalForIRS.cal(finCashPayRuleForPlan, scai, holidayList, false ,FinCashPlanDto.class);
            long index = 1L;
            for(FinCashPlanDto finCashPlanDto:scheList) {
            	// 现金流id
            	finCashPlanDto.setCashId(cashId);
            	// 序号
            	finCashPlanDto.setCashNum(index);
            	// 现金流类型
            	finCashPlanDto.setCashType(DictCodeList.现金流类型_中间收息.getValue());
                // 是否支付利息 数据字典缓存.是否.是
            	finCashPlanDto.setIsPayInt(DictCodeList.是否_是.getValue());
                // 现金流单位 = 数据字典缓存.现金流单位.总额
            	finCashPlanDto.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                // 金融产品代码 = 金融产品交易Entity.交易编号
            	finCashPlanDto.setFinprodId(input.getTradeId());
                // 金融产品类型（估值核算） 
            	finCashPlanDto.setFinprodType(input.getFinprodType());
                // 金融产品类型（投管分类） 
            	finCashPlanDto.setFinprodType2(input.getFinprodType2());
                // 分支序号 = 固定值，系统参数
            	finCashPlanDto.setBranch(branchLong);
            	
            	// 未调整的计算开始日，未调整的计划支付日  赋值为调整后的值
            	finCashPlanDto.setVdateUnadjust(finCashPlanDto.getVdate());
            	finCashPlanDto.setPayDateUnadjust(finCashPlanDto.getPayDate());
            	
            	// 计算周期天数
            	finCashPlanDto.setTermdays(DateUtil.getBetweenDayNumber(finCashPlanDto.getVdate(), finCashPlanDto.getPayDate()));
            	index++;
            }
            finCashPlanList.addAll(scheList);
            if(!isR) {
            	FinCashResetRuleDto finCashResetRuleDto = new FinCashResetRuleDto();
        		BeanUtil.copyPropertiesIgnoreNull(finCashResetRuleDto, finRateInfo);
        		try {
					finCashResetPlanList = commonBusinessService.calFinCashResetPlanList(finCashResetRuleDto, scheList, holidayList);
					for(FinCashResetPlan finCashResetPlan:finCashResetPlanList) {
		            	// 现金流id
						finCashResetPlan.setCashId(cashId);
		                // 金融产品代码 = 金融产品交易Entity.交易编号
		            	finCashResetPlan.setFinprodId(input.getTradeId());
		                // 金融产品类型（估值核算） 
		            	finCashResetPlan.setFinprodType(input.getFinprodType());
		                // 金融产品类型（投管分类） 
		            	finCashResetPlan.setFinprodType2(input.getFinprodType2());
		                // 分支序号 = 固定值，系统参数
		            	finCashResetPlan.setBranch(branchLong);
		            }
				} catch (Exception e) {
                    logger.error(e.getMessage(),e);
				}
            }
        }
        
        inputDto.setFinCashInfoList(finCashInfoList);
        inputDto.setFinCashPayRuleList(finCashPayRuleList);
        inputDto.setFinCashCalcRuleList(finCashCalcRuleList);
        inputDto.setFinRateInfoList(finRateInfoList);
        inputDto.setFinCashPlanList(finCashPlanList);
        inputDto.setFinCashResetPlanList(finCashResetPlanList);

        // 调用共通登录现金流相关数据
        restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME
                + "/common/trdcommonbase/insertFinProductCashData",
            inputDto, Boolean.class);
        return inputDto;
    }
    
    
    /**
     * 撤销交易doInsert
     *
     * @param entity 参数entity
     * @see
     * @since 1.0
     */
    private void doRevokeInsert(TrdIRSTradeDto entity) {
        // 调用基础组合.【金融产品交易组件.查询金融产品交易信息】获取原交易信息
        List<BaseTradeProductDealDto> baseTradeProductDealDtoList = restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/common/trdcommonbase/selectTradeInfo?tradeId="
                + entity.getTradeId(),
            BaseTradeProductDealDto.class);

        if (!StringUtil.isEmptyList(baseTradeProductDealDtoList)) {
            for (BaseTradeProductDealDto baseDto : baseTradeProductDealDtoList) {
                // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
                WflTrdProductDeal wflTrdProductDeal = new WflTrdProductDeal();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDeal, baseDto.getTrdProductDeal());
                // 交易状态=“撤销”
                wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                // 原交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号
                wflTrdProductDeal.setpTradeId(entity.getTradeId());
                // 撤销说明=画面.撤销说明
                wflTrdProductDeal.setCancelRemark(entity.getRevokeReason());
                // 交易编号=金融产品交易信息DTO.金融产品交易Entity.交易编号+“R”
                wflTrdProductDeal.setTradeId(entity.getTradeId() + BussinessConstant.TRADE_REVOKE_TRADE_ID_SPLIT_STR);
                wflTrdProductDeal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDeal.setWorkflowId(entity.getWorkflowId());

                // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
                WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, baseDto.getTrdProductDealAdd());
                wflTrdProductDealAdd.setTradeId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setProdId(wflTrdProductDeal.getTradeId());
                wflTrdProductDealAdd.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                wflTrdProductDealAdd.setWorkflowId(entity.getWorkflowId());
                if(StringUtil.isNotEmpty(baseDto.getActPortfolioId())) {
                    wflTrdProductDealAdd.setPortfolioId(baseDto.getActPortfolioId());
                }
                wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
            }
        }
    }
    
    /**
     * 删除业务流程数据
     *
     * @param workflowId 工作流代码
     * @see
     * @since 1.0
     */
    private void deleteBussFlowData(String workflowId) {
//        // 金融产品交易信息DTO.金融产品交易Entity -> 金融产品交易业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL", "WORKFLOW_ID",
//            workflowId);
//
//        // 金融产品交易信息DTO.金融产品交易附属表Entity -> 金融产品交易附属业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_ADD",
//            "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易扩展表Entity -> 金融产品交易扩展业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_PA_TRD_PRODUCT_DEAL",
//            "WORKFLOW_ID", workflowId);
//
//        // 金融产品交易其它信息Entity -> 金融产品交易其它信息业务流程表
//        bussCommonMapperDelegateService.deleteBySpecificKey("WFL_TRD_PRODUCT_DEAL_OTH", "WORKFLOW_ID",
//            workflowId);
        // 费用被删掉了修改 modify by renping 20191031 start
        baseTradeProductDealCommService.deleteWflData(workflowId);
        // 费用被删掉了修改 modify by renping 20191031 end
    }
    
    
    
	/**
     * 委托代码联动带出画面必要信息
     * 
     * @param entrustId 资产代码
     * @return 利率互换实体类
     * @see
     * @since 1.0
     */
    public TrdIRSTradeDto getDealInfoByEntrustId(String entrustId) {
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                                         + "/TrdIRSTrade/getDealInfoByEntrustId?entrustId="
                                         + entrustId,
                                         TrdIRSTradeDto.class);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param tradeId
     * @return
     * @see
     * @since 1.0
     */
	public TrdIRSTradeDto getIRSTradeDto(String tradeId) {
		// TODO Auto-generated method stub
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdIRSTrade/getTrdIRSTradeDto/"
                                          + tradeId+"/"+CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE),
            null, TrdIRSTradeDto.class);
	}
    
	
	/**
     * 计算结算费用
     * 
     * @param dto 参数
     * @return 费用金额
     * @see
     * @since 1.0
     */
    public BigDecimal calFeeAmtB07(TrdIRSTradeDto dto) {
    	// 基本费用
        BigDecimal feeAmt = new BigDecimal(10);
        // 起息日
        Date vdate = dto.getVdate();
        // 到期日
        Date mdate = dto.getMdate();
        // 名义本金(万元)
        BigDecimal prin = dto.getPrin();
        
        if(vdate!=null&&mdate!=null&&prin!=null) {
        	// 费率信息列表
            IRSFeeInfoDto[] arr_IRSFeeInfo = {new IRSFeeInfoDto(6,DictCodeList.频率_月.getValue(),5)
            		,new IRSFeeInfoDto(1,DictCodeList.频率_年.getValue(),6)
            		,new IRSFeeInfoDto(2,DictCodeList.频率_年.getValue(),7)
            		,new IRSFeeInfoDto(3,DictCodeList.频率_年.getValue(),8)
            		,new IRSFeeInfoDto(4,DictCodeList.频率_年.getValue(),9)
            		,new IRSFeeInfoDto(5,DictCodeList.频率_年.getValue(),10)
            		,new IRSFeeInfoDto(6,DictCodeList.频率_年.getValue(),11)
            		,new IRSFeeInfoDto(7,DictCodeList.频率_年.getValue(),12)
            		,new IRSFeeInfoDto(8,DictCodeList.频率_年.getValue(),13)
            		,new IRSFeeInfoDto(9,DictCodeList.频率_年.getValue(),14)
            		,new IRSFeeInfoDto(10,DictCodeList.频率_年.getValue(),15)};
            
            // 计算后日期
            Date calDate = new Date();
            for(int index=0;index<arr_IRSFeeInfo.length;index++) {
            	IRSFeeInfoDto iRSFeeInfoDto = arr_IRSFeeInfo[index];
            	// 计算得到计算后的日期
            	if(StringUtil.equals(DictCodeList.频率_月.getValue(), iRSFeeInfoDto.getCycleUnit())) {
            		calDate = DateUtil.addDate(vdate, 0, iRSFeeInfoDto.getCycle(), 0);
            	}
            	if(StringUtil.equals(DictCodeList.频率_年.getValue(), iRSFeeInfoDto.getCycleUnit())) {
            		calDate = DateUtil.addDate(vdate, iRSFeeInfoDto.getCycle(), 0 , 0);
            	}
            	// 费率
        		BigDecimal rate = new BigDecimal(iRSFeeInfoDto.getRate());
            	// 计算得到的日期 大于等于到期日
            	if(DateUtil.compareDate(calDate, mdate)>=0) {
            		// 出参.费用 = 入参.名义本金 * 变量.费率 + 变量.基本费用	保留2位小数		
            		feeAmt = feeAmt.add(rate.multiply(prin).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
            		break;
            	}
            	// 15年以上
            	if(index==arr_IRSFeeInfo.length-1) {
            		feeAmt = feeAmt.add(rate.multiply(prin).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
            	}
            }
            
        }
        
        return feeAmt;
    }

    /** 获取业务参数
     * @param entity 业务实体
     * @param taskId 任务代码
     * @return 参数
     * @throws WorkFlowException 工作流异常
     */
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        TrdIRSTradeDto dto = this.getBussinessEntity(entity);
        if (taskId != null) {
            try {
//              return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getPrin().multiply(new BigDecimal(10000)), LoginUserUtil.getUserName(),
//                      DictCodeListExt2.平安资产类型_利率互换.getValue(), dto.getFinprodType(), dto.getTradeMarket(), 
//                      // 资产类型改为授权类型 add by renping 2019-08-06  start
//                      DictCodeList.交易方向_买入.getValue()
//                      , null
//                      // 资产类型改为授权类型 add by renping 2019-08-06  end
//                      );
              Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getPrin().multiply(new BigDecimal(10000)),LoginUserUtil.getUserName(),
                  DictCodeListExt2.平安资产类型_利率互换.getValue(), dto.getFinprodType(), dto.getTradeMarket(), dto.getIrsType()
                  , null ,entity.getWorkflowId());
              params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
              return commonBusinessService.是否需要上级审批(params);
          }
            catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
        }
        else {
            return null;
        }
    }

    @Override
    public void doBack(TrdIRSTradeDto entity) {
        //切面处理风险接口退回
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
    
    @Override
    public void doRevoke(TrdIRSTradeDto entity) {
        //删除授信占用
        if (null != entity.getTradeId() && !"".equals(entity.getTradeId()) && !StringUtil.isNotEmpty(entity.getRevokeReason())){
            riskCreditListCommonValidatorService.deleteCredit(entity.getTradeId());
        }
    }
}
