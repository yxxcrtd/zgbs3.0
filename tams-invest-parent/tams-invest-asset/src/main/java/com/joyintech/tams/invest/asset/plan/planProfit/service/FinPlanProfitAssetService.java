/*
 * 文件名：FinPlanProfitAsset001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.plan.planProfit.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.BussFinRepaymentService;
import com.joyintech.tams.component.business.asset.FinInterestFactorService;
import com.joyintech.tams.component.business.asset.FinPayInterestStdInfoService;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.project.BussParticipantInfoService;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.component.business.report.asset.plan.BussRepAssetMngProdInfoService;
import com.joyintech.tams.component.business.report.asset.plan.dto.BussRepAssetMngProdInfoDto;
import com.joyintech.tams.component.business.report.asset.trust.BussRepOutInvestAgrInfoService;
import com.joyintech.tams.component.business.report.asset.trust.dto.BussRepOutInvestAgrInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.asset.plan.planProfit.dto.FinPlanProfitAssetDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.interf.trade.fromxp.service.InterfServiceLogService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 计划类资产（利率型）service
 * 
 * @author liuwei
 * @version 1.0 2018年9月14日
 * @see FinPlanProfitAssetService
 * @since 1.0
 */
@Service
public class FinPlanProfitAssetService extends CommonExportService<FinPlanProfitAssetDto> implements BaseProcessService<FinPlanProfitAssetDto> {

    /**
     * 共通--资产
     */
    @Autowired
    private AssetCommonService assetCommonService;

    /**
     * 资产管理计划中债信息组件
     */
    @Autowired(required = false)
    private BussRepAssetMngProdInfoService bussRepAssetMngProdInfoService;

    /**
     * 委外投资-协议方式中债信息组件
     */
    @Autowired(required = false)
    private BussRepOutInvestAgrInfoService bussRepOutInvestAgrInfoService;

    /**
     * 计息要素组件
     */
    @Autowired(required = false)
    private FinInterestFactorService finInterestFactorService;

    /**
     * 付息信息组件
     */
    @Autowired(required = false)
    private FinPayInterestStdInfoService finPayInterestStdInfoService;

    /**
     * 计划类资产（利率型）
     */
    @Autowired(required = false)
    private FinPlanProfitAssetService finPlanProfitAssetService;

    /**
     * 各种业务共通组件
     */
    @Autowired(required = false)
    private CommonBusinessService commonBusinessService;

    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 参与方信息
     */
    @Autowired(required = false)
    private BussParticipantInfoService bussParticipantInfoService;

    /**
     * 还本信息
     */
    @Autowired(required = false)
    private BussFinRepaymentService bussFinRepaymentService;
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    /**
     * 资产代码的联想文本框事件
     * 
     * @param param 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinProdMarketId(String param) {
        return restTemplate.getForList(ServiceApplication.ASSET_SERVICE_NAME
                                       + "/FinPlanProfitAsset/getFinProdMarketId?finprodMarketId="
                                       + param,
            SuggestDto.class);
    }

    /**
     * 查询界面询事件
     * 
     * @param map 页面参数
     * @return 调用微服务
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<FinPlanProfitAssetDto> selectForPage(Map<String, Object> map) {
        Page<FinPlanProfitAssetDto> page = restTemplate.postForPage(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/findPage", map,
            FinPlanProfitAssetDto.class);
        return page;
    }

    @Override
    public void doInsert(FinPlanProfitAssetDto entity, OperationType type) {
        updateFiles(entity);
    }

    private void updateFiles(FinPlanProfitAssetDto entity) {
        List<FileCommonDto> files = entity.getFileCommonList();
        if(files.size()>0) {
            List<String> fileNo = new ArrayList<>();
            files.forEach(fileCommonDto -> {
                fileNo.add(fileCommonDto.getFileNo());
            });
            fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
        }
    }

    @Override
    public void doUpdate(FinPlanProfitAssetDto entity) {
        updateFiles(entity);
    }

    @Override
    public void doDelete(String workflowId) {
        FinPlanProfitAssetDto finPlanProfitAssetDto = finPlanProfitAssetService.getBussinessEntityByWorkflowId(workflowId);
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(finPlanProfitAssetDto.getFjId());
        if(files.size()>0) {
            files.forEach(uploadFile -> {
                fileAttachmentProcess.remove(uploadFile.getFileNo());
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinPlanProfitAssetDto dto, OperationType type) {
        String action;
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getFinprodId());
            if(files.size()>0) {
                files.forEach(uploadFile -> {
                    fileAttachmentProcess.remove(uploadFile.getFileNo());
                });
            }
            FinPlanProfitAssetDto entity = getDto(dto);
            restTemplate.postForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/" + action, entity,
                Integer.class);
        }
        else if (OperationType.NEW.equals(type)) {
        	//新建计划类资产时，自动创建交易对手 add by hxy 20200421 start
    		assetCommonService.autoCreateTradeCounter(dto.getManager());
    		//新建计划类资产时，自动创建交易对手 add by hxy 20200421 end
            action = "insert";
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getFjId());
            if(files.size()>0) {
                List<String> fileNo = new ArrayList<>();
                files.forEach(uploadFile -> {
                    fileNo.add(uploadFile.getFileNo());
                });
                fileAttachmentProcess.updateBussinessNo(fileNo, dto.getFinprodId());
            }
            FinPlanProfitAssetDto entity = getDto(dto);
            restTemplate.postForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/" + action, entity,
                Integer.class);
        }
        else {
            action = "update";
            FinPlanProfitAssetDto entity = getDto(dto);
            restTemplate.postForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/" + action, entity,
                Integer.class);
        }

     // 二级法人权限处理
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //创建机构代码
            String createInst =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(dto.getCreateUser());
            dto.setCreateInst(createInst);
            // 有查询权限的机构代码
            List<String> searchOrgCodeList = null;
            // 有编辑权限的机构代码
            List<String> editOrgCodeList = null;
            // 新建或修改时重新设置二级法人权限，删除时则清空
            if (!OperationType.DELETE.equals(type)) {
                //获取所有父级机构。  拥有查看权限
                searchOrgCodeList = CommonUtil.getParentInstitution(createInst);
                // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                editOrgCodeList = Arrays.asList(dto.getCreateInst());
            }
            // 记录二级法人权限
            insertDataAuthTypeInst(dto.getFinprodId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_计划类资产_利率型_信息.getValue());

            //modify by wj 20200624 增加组合权限
            insertDataAuthTypeInst(dto.getFinprodMarketId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_组合管理.getValue());

        }
        
        // 推送数据到第三方接口
        //1.计划类要素信息
        Map<String, Object> invMap =  JsonUtils.object2Map(dto);
        invMap.put("pageId","FinPlanTargetAsset002S017");
        investInterfCommService.doInvestInterf(invMap);

        //2.利率信息
        invMap.put("pageId","FinPlanTargetAsset002S018");
        investInterfCommService.doInvestInterf(invMap);
        //3.现金流计划

        invMap.put("pageId","FinPlanTargetAsset002S019");
        investInterfCommService.doInvestInterf(invMap);

}

    /**
     * 组建dto
     *
     * @param dto 画面参数
     * @return FinPlanNetAssetDto
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public FinPlanProfitAssetDto getDto(FinPlanProfitAssetDto dto) {
        Long branch = Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        // 标的类金融产品entity
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        // 金融产品代码（估值核算）
        finProduct.setFinprodAbbr(finProduct.getFinprodName());
        finProduct.setFinprodType2(dto.getFinprodType2());
        finProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        finProduct.setTermDays((dto.getMdate().getTime()-dto.getVdate().getTime())/1000/60/60/24);
        finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
        dto.setFinProduct(finProduct);
        // 标的类金融产品附属表
        FinProductAdd finProductAdd = new FinProductAdd();
        finProductAdd.setFinprodId(finProduct.getFinprodId());
        finProductAdd.setPurSpeed((long)(StringUtil.equals(dto.getTransMode(), "01") ? 0 : 1));
        finProductAdd.setInvestNature(dto.getInvestNature());
        finProductAdd.setIsOpenEveryDay(dto.getIsOpenEveryDay());
        finProductAdd.setCalRateType(dto.getCalRateType());
        finProductAdd.setCreditEnhanceMeasure(dto.getCreditEnhanceMeasure());
        finProductAdd.setFundMangerName(dto.getFundMangerName());
        dto.setFinProductAdd(finProductAdd);
        // 金融产品分类表
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(finProduct.getFinprodId());
        finProductType.setBranch(branch);
        finProductType.setType6(dto.getCategory());
        /* 四级分类为“自主管理”和“委托投资”都可以自动生成委外组合 yujiaping 2020-11-12 start*/
        if(dto.getType().equals("04")) {       	
        	finProductType.setType7("04");
        }else{
        	finProductType.setType7("02");
        }       
        /*  四级分类为“自主管理”和“委托投资”都可以自动生成委外组合 yujiaping 2020-11-12 end*/
		 /*   start、zhuyongkang、2019.7.1、9原因   */
        finProductType.setType9(dto.getType9());
        finProductType.setType10(dto.getType10());
        finProductType.setType11(dto.getType11());
        finProductType.setType12(dto.getType12());
        /* end、zhuyongkang、2019.7.19 */ 
        finProductType.setType8(DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        dto.setFinProductType(finProductType);
        // 金融产品扩展entity
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct, dto);
        dto.setPaFinProduct(paFinProduct);
        // 现金流信息List
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        // 现金流信息-本金
        FinCashInfo finCashInfo1 = new FinCashInfo();
        if(null!=commonBusinessService) {
            finCashInfo1.setCashId(commonBusinessService.generateCashFlowCode(
                    finProduct.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                    Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)),
                    DictCodeListExt1.现金流二级类型_本金.getValue()));
            finCashInfo1.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            finCashInfo1.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
            finCashInfo1.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo1.setBranch(branch);
            finCashInfo1.setFinprodId(finProduct.getFinprodId());
            finCashInfo1.setCcy(finProduct.getCcy());
        }
        // 现金流信息-利息
        FinCashInfo finCashInfo2 = new FinCashInfo();
        if(null!=commonBusinessService) {
            finCashInfo2.setCashId(commonBusinessService.generateCashFlowCode(
                    finProduct.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                    Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)),
                    DictCodeListExt1.现金流二级类型_利息.getValue()));
            finCashInfo2.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            finCashInfo2.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            finCashInfo2.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo2.setFinprodId(finProduct.getFinprodId());
            finCashInfo2.setCcy(finProduct.getCcy());
            finCashInfo2.setBranch(branch);
        }
        // 将数据插入到list中
        finCashInfoList.add(finCashInfo1);
        finCashInfoList.add(finCashInfo2);
        // 现金流计算规则dtolist
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        // 现金流计算规则表-利息
        FinCashCalcRule finCashCalcRule1 = new FinCashCalcRule();
        if(null!=finInterestFactorService) {
            if (null != dto.getFinInterestFactorDto()) {
                FinInterestFactorDto finInterestFactorDto = finInterestFactorService.getFinInterestFactorDto(
                        dto.getFinInterestFactorDto());
                // 现金流代码
                finCashCalcRule1.setCashId(finCashInfo2.getCashId());
                finCashCalcRule1.setEffDate(dto.getVdate());
                // 一体化对应etliu
//                finCashCalcRule1.setRelaeseDate(dto.getVdate());
                BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule1,
                        finInterestFactorDto.getFinCashCalcRule());
                finCashCalcRule1.setBaseType(DictCodeList.基数类型_实收资本.getValue());
                finCashCalcRule1.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
                finCashCalcRule1.setIsInitial(DictCodeList.是否_是.getValue());
                BigDecimal  rate = BigDecimal.ZERO;
              //非母资管计划
                if(!StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue())){
                    if(StringUtil.equals(finInterestFactorDto.getCalRateType(), DictCodeListExt3.计息利率方式_买断利率计提.getValue())){
                        rate = ArithmeticUtil.divide(dto.getBuyOutRate(), new BigDecimal(100), 14);

                    }else{
                        rate = ArithmeticUtil.divide(finInterestFactorDto.getInvestRate(), new BigDecimal(100), 14);
                    }
                }
                finCashCalcRule1.setYield(rate);
                BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule1, finProduct);
                finCashCalcRule1.setBranch(branch);
                finCashCalcRuleList.add(finCashCalcRule1);
                //现金流信息-参与方信息（多条数据）以及 现金流计算规则dtolist
                if (null != bussParticipantInfoService) {
                    if (null != dto.getBussParticipantInfoDto()) {
                        BussParticipantInfoDto bussParticipantInfoDto = assetCommonService.enBussParticipantInfoDto(finProduct, dto.getBussParticipantInfoDto(), branch);
                        for (FinCashCalcRule finCashCalcRule : bussParticipantInfoDto.getFinCashCalcRuleList()) {
                            finCashCalcRule.setBasis(finInterestFactorDto.getFinCashCalcRule().getBasis());
                            finCashCalcRuleList.add(finCashCalcRule);
                        }
                        finCashInfoList.addAll(bussParticipantInfoDto.getFinCashInfoList());
                    }
                }
                dto.setFinCashInfoList(finCashInfoList);
                dto.setFinCashCalcRuleList(finCashCalcRuleList);
                // 现金流利率信息表dtolist
                List<FinRateInfo> finRateInfoList = new ArrayList<>();
                // 现金流利率信息-利息
                FinRateInfo finRateInfo = new FinRateInfo();
                finRateInfo.setCashId(finCashInfo2.getCashId());
                finRateInfo.setEffDate(dto.getVdate());
                BeanUtil.copyPropertiesIgnoreNull(finRateInfo, finInterestFactorDto.getFinRateInfo());
                finRateInfo.setFinprodId(finProduct.getFinprodId());
                finRateInfo.setBranch(branch);
                finRateInfo.setFinprodType(finProduct.getFinprodType());
                finRateInfo.setFinprodType2(finProduct.getFinprodType2());
                finRateInfo.setRate(rate);
                finRateInfoList.add(finRateInfo);
                // 现金流利率信息-参与方信息
                if (null != bussParticipantInfoService) {
                    if (null != dto.getBussParticipantInfoDto()) {
                        BussParticipantInfoDto bussParticipantInfoDto = assetCommonService.enBussParticipantInfoDto(finProduct, dto.getBussParticipantInfoDto(), branch);
                        for (FinCashInfo entity : bussParticipantInfoDto.getFinCashInfoList()) {
                            if (StringUtil.equals(entity.getCashType2(), DictCodeListExt1.现金流二级类型_通道托管费.getValue())) {
                                FinRateInfo finRateInfo1 = new FinRateInfo();
                                BeanUtil.copyPropertiesIgnoreNull(finRateInfo1, finProduct);
                                finRateInfo1.setCashId(entity.getCashId());
                                finRateInfo1.setEffDate(dto.getVdate());
                                finRateInfo1.setBranch(branch);
                                finRateInfoList.add(finRateInfo1);
                            }
                            if (StringUtil.equals(entity.getCashType2(), DictCodeListExt1.现金流二级类型_通道管理费.getValue())) {
                                FinRateInfo finRateInfo1 = new FinRateInfo();
                                BeanUtil.copyPropertiesIgnoreNull(finRateInfo1, finProduct);
                                finRateInfo1.setCashId(entity.getCashId());
                                finRateInfo1.setEffDate(dto.getVdate());
                                finRateInfo1.setBranch(branch);
                                finRateInfoList.add(finRateInfo1);
                            }
                            if (StringUtil.equals(entity.getCashType2(), DictCodeListExt1.现金流二级类型_通道其他费.getValue())) {
                                FinRateInfo finRateInfo1 = new FinRateInfo();
                                BeanUtil.copyPropertiesIgnoreNull(finRateInfo1, finProduct);
                                finRateInfo1.setCashId(entity.getCashId());
                                finRateInfo1.setEffDate(dto.getVdate());
                                finRateInfo1.setBranch(branch);
                                finRateInfoList.add(finRateInfo1);
                            }
                        }
                    }
                }
                dto.setFinRateInfoList(finRateInfoList);
                //非母资管计划
                if(!StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue())){
                    /**
                     * 现金流支付规则
                     */
                    // 获取付息信息dto
                    if(null!=finPayInterestStdInfoService) {
                        if (null != dto.getFinPayInterestStdInfoDto()) {
                            FinPayInterestStdInfoDto finPayInterestStdInfoDto = finPayInterestStdInfoService.getFinPayInterestStdInfoDto(
                                    dto.getFinPayInterestStdInfoDto());
                            List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();
                            // 现金流支付规则-利息
                            FinCashPayRule finCashPayRule = new FinCashPayRule();
                            BeanUtil.copyPropertiesIgnoreNull(finCashPayRule,
                                    finPayInterestStdInfoDto.getFinCashPayRule());
                            finCashPayRule.setCashId(finCashInfo2.getCashId());
                            finCashPayRule.setEffDate(dto.getVdate());
                            finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                            finCashPayRule.setFinprodId(finProduct.getFinprodId());
                            finCashPayRule.setFinprodType(finProduct.getFinprodType());
                            finCashPayRule.setFinprodType2(finProduct.getFinprodType2());
                            finCashPayRule.setBranch(branch);
                            finCashPayRule.setPayType(DictCodeList.分红方式_现金分红.getValue());
                            
                            // 一体化对应etliu 现金流支付规则表，支付频率没存值，可能有问题
                            if (StringUtil.isBlank(finCashPayRule.getPayCycle())) {
                                finCashPayRule.setPayCycle(BussinessConstant.DEFAULT_PAY_CYCLE);
                            }
                            
                            finCashPayRuleList.add(finCashPayRule);
                            /**
                             * 现金流计划
                             */
                            List<FinCashPlan> finCashPlanList = new ArrayList<>();
                            List<FinCashPlan> finCashPlanLisList = new ArrayList<>();
                            // 现金流计划-利息(多条数据)
                            for (FinCashPlan entity : finPayInterestStdInfoDto.getFinCashPlanList()) {
                                FinCashPlan finCashPlan = new FinCashPlan();
                                BeanUtil.copyPropertiesIgnoreNull(finCashPlan, entity);
                                BigDecimal rangeYield = ArithmeticUtil.divide(entity.getRangeYield(), new BigDecimal(100), 14);
                                finCashPlan.setRangeYield(rangeYield);
                                finCashPlan.setCashId(finCashInfo2.getCashId());
                            finCashPlan.setCashAmt(entity.getIntAmt());
                                if(DateUtil.compareDate(dto.getMdate(), entity.getMdate())==0){
                                    finCashPlan.setCashType(DictCodeList.现金流类型_到期收息.getValue());
                                }else{
                                finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
                                }
                                finCashPlan.setCashBaseamt(dto.getIssueAmt());
                                finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                                finCashPlan.setFinprodId(finProduct.getFinprodId());
                                finCashPlan.setFinprodType(finProduct.getFinprodType());
                                finCashPlan.setFinprodType2(finProduct.getFinprodType2());
                                finCashPlan.setBranch(branch);
                                finCashPlan.setPayType(DictCodeList.分红方式_现金分红.getValue());
                                finCashPlanLisList.add(finCashPlan);
                            }
                            //add by zhangnian 20191120 start
                            // 还本信息DTO 现金流支付支付计划
                            BussFinRepaymentDto bussFinRepaymentDto = new BussFinRepaymentDto();
                            if (null != bussFinRepaymentService) {
                                bussFinRepaymentDto = assetCommonService.encapBussFinRepayment(finProduct, dto.getBussFinRepaymentDto(),
                                        finCashInfo1, branch);
                                finCashPayRuleList.add(bussFinRepaymentDto.getFinCashPayRule());
                            }
                            // 重新计算现金流 add by 2019/02/27 by张年
                            if (null != finPayInterestStdInfoService && null != bussFinRepaymentService) {
                                finCashPlanList.addAll(assetCommonService.calCashBaseamt(finProduct, finCashPlanLisList,
                                        bussFinRepaymentDto.getFinCashPlan2List()));
                            }
                            //add by zhangnian 20191120 end
                            // 现金流计划-本金
                            /*int i = finCashPlanList.size();
                            FinCashPlan finCashPlan1 = new FinCashPlan();
                            BeanUtil.copyPropertiesIgnoreNull(finCashPlan1, finCashPlanList.get(i - 1));
                            finCashPlan1.setCashId(finCashInfo1.getCashId());
                            finCashPlan1.setCashType(DictCodeList.现金流类型_到期还本.getValue());
                            finCashPlan1.setVdateUnadjust(finCashPlanList.get(i - 1).getMdateUnadjust());
                            finCashPlan1.setVdate(finCashPlanList.get(i - 1).getMdate());
                            finCashPlan1.setPrinAmt(dto.getIssueAmt());
                            finCashPlan1.setIntPrinAmt(dto.getIssueAmt());
                            finCashPlan1.setIntAmt(BigDecimal.ZERO);
                            finCashPlan1.setIsPayInt(DictCodeList.是否_是.getValue());
                            finCashPlan1.setCashAmt(dto.getIssueAmt());
                            finCashPlan1.setCashBaseamt(dto.getIssueAmt());
                            finCashPlan1.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
                            finCashPlan1.setFinprodId(finProduct.getFinprodId());
                            finCashPlan1.setFinprodType(finProduct.getFinprodType());
                            finCashPlan1.setFinprodType2(finProduct.getFinprodType2());
                            finCashPlan1.setBranch(branch);
                            finCashPlan1.setPayType(DictCodeList.分红方式_现金分红.getValue());
                            finCashPlanList.add(finCashPlan1);*/
                            dto.setFinCashPlanList(finCashPlanList);
                            dto.setFinCashPayRuleList(finCashPayRuleList);
                        }
                    }
                    /**
                     * 现金流计算规则扩展
                     */
                    PaFinCashCalcRule paFinCashCalcRule = new PaFinCashCalcRule();
                    paFinCashCalcRule.setCashId(finCashInfo2.getCashId());
                    paFinCashCalcRule.setEffDate(dto.getVdate());
                    paFinCashCalcRule.setLoadRate(dto.getFinInterestFactorDto().getLoadRate().divide(new BigDecimal(100)));
                    paFinCashCalcRule.setInvestRate(dto.getFinInterestFactorDto().getInvestRate().divide(new BigDecimal(100)));
                    dto.setPaFinCashCalcRule(paFinCashCalcRule);
                }
            }
        }
        /**
         * 中债报送组件
         */
        if (StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托产品.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_券商资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货资产管理计划.getValue())) {
            // 资产管理计划报送信息组件
            BussRepAssetMngProdInfoDto bussRepAssetMngProdInfoDto = bussRepAssetMngProdInfoService.getBussRepAssetMngProdInfoDto(
                dto.getBussRepAssetMngProdInfo());
            RepAssetDept repAssetDept = new RepAssetDept();
            BeanUtil.copyPropertiesIgnoreNull(repAssetDept,
                bussRepAssetMngProdInfoDto.getRepAssetDept());
            repAssetDept.setRepFinprodId(finProduct.getFinprodId());
            repAssetDept.setFinprodMarketId(finProduct.getFinprodMarketId());
            /* FXZG-6 【阜新银行】REP_ASSET_DEPT部分字段未存值 start */
            repAssetDept.setRepCcy(finProduct.getCcy());
            /* FXZG-6 【阜新银行】REP_ASSET_DEPT部分字段未存值 end */
            repAssetDept.setRepFinprodName(dto.getFinprodName());
            dto.setRepAssetDept(repAssetDept);
            RepAssetMngPlan repAssetMngPlan = new RepAssetMngPlan();
            repAssetMngPlan.setRepFinprodId(finProduct.getFinprodId());
            BeanUtil.copyPropertiesIgnoreNull(repAssetMngPlan,
                bussRepAssetMngProdInfoDto.getRepAssetMngPlan());
			repAssetMngPlan.setRepManagerId(dto.getManager());
			repAssetMngPlan.setRepTrusteeId(dto.getTrusteeId());
            repAssetMngPlan.setRepSetDate(dto.getVdate());
            repAssetMngPlan.setRepEndDate(dto.getMdate());
            dto.setRepAssetMngPlan(repAssetMngPlan);
          /* 中债其他的要素存值 zyk 20191015*/
            RepAssetOther repAssetOther =new RepAssetOther();
            repAssetOther.setRepFinprodId(finProduct.getFinprodId());
            // ZGZGST-1278 增加非null判断 fuyongsheng 20210819 add start
            if(StringUtil.isNotEmpty(dto.getBussRepAssetMngProdInfo())){
                repAssetOther.setRepPrivateprdFlag(dto.getBussRepAssetMngProdInfo().getRepPrivateprdFlag());
            }
            // ZGZGST-1278 增加非null判断 fuyongsheng 20210819 add end
            dto.setRepAssetOther(repAssetOther);
            /* 中债其他的要素存值 zyk 20191015*/
        }
        else if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_银行.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_证券公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他.getValue())){
            // 委外投资-协议方式信息组件
            BussRepOutInvestAgrInfoDto bussRepOutInvestAgrInfoDto = bussRepOutInvestAgrInfoService.getBussRepOutInvestAgrInfoDto(
                dto.getBussRepOutInvestAgrInfo());
            RepAssetDept repAssetDept = new RepAssetDept();
            BeanUtil.copyPropertiesIgnoreNull(repAssetDept,
                bussRepOutInvestAgrInfoDto.getRepAssetDept());
            repAssetDept.setRepFinprodId(finProduct.getFinprodId());
            repAssetDept.setFinprodMarketId(finProduct.getFinprodMarketId());
            repAssetDept.setRepFinprodName(dto.getFinprodName());
            // 币种
            repAssetDept.setRepCcy(finProduct.getCcy());
            dto.setRepAssetDept(repAssetDept);
            RepTrustInvestMode repTrustInvestMode = new RepTrustInvestMode();
            repTrustInvestMode.setRepFinprodId(finProduct.getFinprodId());
            /* 资产要素登记-辽阳   bug 号：2.0.0.1TWO-747  委外投资-协议方式报送信息表取管理人 20191029  start */
            repTrustInvestMode.setRepActualManager(
                finPlanProfitAssetService.getName(dto.getManager()));
             /* 资产要素登记-辽阳   bug 号：2.0.0.1TWO-747  委外投资-协议方式报送信息表取管理人 20191029  end */
            repTrustInvestMode.setRepCustodian(
                finPlanProfitAssetService.getName(dto.getTrusteeId()));
            BeanUtil.copyPropertiesIgnoreNull(repTrustInvestMode,
                bussRepOutInvestAgrInfoDto.getRepTrustInvestMode());
            repTrustInvestMode.setRepInvestStartdate(dto.getVdate());
            repTrustInvestMode.setRepInvestEnddate(dto.getMdate());
            dto.setRepTrustInvestMode(repTrustInvestMode);
            /* 中债其他的要素存值 zyk 20191015*/
            RepAssetOther repAssetOther =new RepAssetOther();
            repAssetOther.setRepFinprodId(finProduct.getFinprodId());
            repAssetOther.setRepPrivateprdFlag(dto.getBussRepOutInvestAgrInfo().getRepPrivateprdFlag());
            dto.setRepAssetOther(repAssetOther);
            /* 中债其他的要素存值 zyk 20191015*/
        }
        
      //add by wangpeng start at 2019-07-15 :生成委外组合
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
//        ptlPortfolio.setPortfolioId(finProduct.getFinprodId());
        //修改为市场代码
        ptlPortfolio.setPortfolioId(finProduct.getFinprodMarketId());
        ptlPortfolio.setPortfolioType(DictCodeListExt1.组合类型_委外组合.getValue());
        ptlPortfolio.setPortfolioName(finProduct.getFinprodName());
        ptlPortfolio.setVdate(finProduct.getVdate());
        ptlPortfolio.setMdate(finProduct.getMdate());
        ptlPortfolio.setProfitType(finProduct.getProfitType());
        dto.setPtlPortfolio(ptlPortfolio);
        //add end
        /**
         * 构建标的类金融产品买断利率entity
         */
        PaFinProductBuyoutRate paFinProductBuyoutRate = new PaFinProductBuyoutRate();
        if(!(StringUtil.equals(DictCodeList.是否_是.getValue(), dto.getParentPlan())&&
        		dto.getBuyOutRate() == null)){
        	paFinProductBuyoutRate.setFinprodId(finProduct.getFinprodId());
        	paFinProductBuyoutRate.setEffDate(dto.getVdate());
        	paFinProductBuyoutRate.setBuyoutRate(dto.getBuyOutRate().divide(new BigDecimal(100)));
        	dto.setPaFinProductBuyoutRate(paFinProductBuyoutRate);
        }
        return dto;
    }

    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
		String isShowType = (String) exportParam.getSelectParam().get("isShowType");
		if (StringUtil.equals(isShowType, "01")) {
			exportParam.config("/com/joyintech/tams/invest/asset/plan/planProfit/conf/export_conf.xml")
					.dataSource(DataSourceType.MASERT);
		} else {
			exportParam.config("/com/joyintech/tams/invest/asset/plan/planProfit/conf/export_conf002.xml")
					.dataSource(DataSourceType.MASERT);
		}
    }

    /**
     * 设置日期格式 {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if (("vdate".equals(propName)) && value != null) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
        if (("mdate".equals(propName)) && value != null) {
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<FinPlanProfitAssetDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 出账编号的联想下拉框事件
     *
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCmsId(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getCmsId", map,
            SelectOptionDto.class);
    }

    /**
     * 出账编号的联动事件
     *
     * @param cmsId 出账编号
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public FinPlanProfitAssetDto getChange(String cmsId) {
        return restTemplate.getForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getChange?cmsId=" + cmsId,
            FinPlanProfitAssetDto.class);
    }

    /**
     * 管理人的联想下拉框事件
     * 
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getManager(Map<String, Object> map) {
        return restTemplate.postForPage(
            ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/getManager",map,
                SelectOptionDto.class);
    }

    /**
     * 托管人的联想下拉框事件
     * 
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getTrusteeId(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getTrusteeId", map,
            SelectOptionDto.class);
    }

    /**
     * 交易日历的联想下拉框事件
     * 
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCalendarId(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getCalendarId", map,
            SelectOptionDto.class);
    }

    /**
     * 放款分行的联想下拉框事件
     * 
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getBank(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getBank", map,
            SelectOptionDto.class);
    }

    /**
     * 资产提供行的多选下拉框事件
     * 
     * @param map 查询参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getBankList(Map<String, Object> map) {
        return restTemplate.postForList(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getBankList", map,
            SelectOptionDto.class);
    }

    /**
     * 修改和双击查看界面查询
     *
     * @param finprodId 金融产品代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public FinPlanProfitAssetDto getBasicInfoByFinprodId(String finprodId) {
        return restTemplate.postForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getBasicInfoByFinprodId",
            finprodId, FinPlanProfitAssetDto.class);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    public String getFinprodId(String finprodMarketId){
        return   restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getFinprodId?finprodMarketId="+finprodMarketId,String.class
        );
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/
    /**
     * 查询市场代码
     *
     * @param finprodMarketId 市场代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Integer selectFinprodMarketId(String finprodMarketId) {
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
                                         + "/FinPlanProfitAsset/selectFinprodMarketId?finprodMarketId="
                                         + finprodMarketId,
            Integer.class);
    }

    /**
     * 查询客户名称
     *
     * @param custormerId 客户代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public String getName(String custormerId) {
        return restTemplate.getForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanProfitAsset/getName?custormerId="
                                         + custormerId,
            String.class);
    }

    @Autowired
    private InterfServiceLogService interfServiceLogService;

    /**
     *计划类利率型标的 接口数据入库
     * @param id
     * @param msgbody
     */
    public void executeInterf(String id,String msgbody) {
        System.out.println("##############################计划类利率型标的 接口数据入库start#############################");
        try {
              FinPlanProfitAssetDto entity = new FinPlanProfitAssetDto();
            if(StringUtil.isNotEmpty(msgbody)) {
                JSONObject msgJSON = JSONObject.parseObject(msgbody);
                entity.setOperateType(msgJSON.getString("OPERATE_TYPE"));
                entity.setType9(msgJSON.getString("TYPE_10"));
                entity.setTrialNo(msgJSON.getString("TRIAL_NO"));
                entity.setTransMode(msgJSON.getString("TRANS_MODE"));
                entity.setBuyOutRate(msgJSON.getBigDecimal("BUYOUT_RATE"));
                entity.setParentPlan(msgJSON.getString("PARENT_PLAN"));
                entity.setCreditEnhanceMeasure(msgJSON.getString("CREDIT_MEASURES"));
                entity.setFundMangerName(msgJSON.getString("FUND_MANAGER"));
                entity.setSettleDays(msgJSON.getString("SETTLE_DAYS"));
                entity.setInvestRate(msgJSON.getBigDecimal("INVEST_RATE"));
                entity.setCalRateType(msgJSON.getString("CAL_RATE_TYPE"));
                entity.setRepAssetType(msgJSON.getString("SUB_CODE")); // 资产/负债类别

                FinProduct finProduct = new FinProduct();
                finProduct.setFinprodId(msgJSON.getString("ASSET_CODE")); // 资产代码
                finProduct.setFinprodAbbr(msgJSON.getString("ASSET_SNAME")); // 资产代码简称
                finProduct.setFinprodName(msgJSON.getString("ASSET_NAME")); // 资产代码全称
                finProduct.setpFinprodId(msgJSON.getString("MOTHER_ASSET_CODE")); // 母资产代码
                finProduct.setIdate(msgJSON.getDate("ESTAB_DATE")); // 发行日
                finProduct.setProfitType(msgJSON.getString("PROFIT_TYPE"));
                finProduct.setCcy(msgJSON.getString("CURR_TYPE"));
                finProduct.setVdate(msgJSON.getDate("VALUE_DATE"));
                finProduct.setMdate(msgJSON.getDate("END_DATE"));
                finProduct.setTrusteeId(msgJSON.getString("TRUSTEE_CODE"));
                finProduct.setIssuer(msgJSON.getString("PUBLISHER_CODE"));
                finProduct.setIssueAmt(msgJSON.getBigDecimal("ISSUE_AMT"));
                finProduct.setTermDays(msgJSON.getLong("TERM_DAYS"));
                finProduct.setManager(msgJSON.getString("ACTUAL_MANAGER"));
                finProduct.setContractNo(msgJSON.getString("CONTRACT_NO"));
                finProduct.setInvestAdviser(msgJSON.getString("INVEST_ADVISER"));
                finProduct.setCalendarId(msgJSON.getString("CALENDAR_ID"));
                finProduct.setRemark(msgJSON.getString("REMAKE"));
                if(msgJSON.getString("IS_GUARANTEED").equals("1")){ // 是否保本
                    finProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
                }else{
                    finProduct.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_非保本.getValue());
                }
                entity.setFinProduct(finProduct);

                // 标的类金融产品附属表
                FinProductAdd finProductAdd = new FinProductAdd();


                finProductAdd.setInvestNature(msgJSON.getString("INVESTMENT_ATTRIBUTES")); // 投资性质
                finProductAdd.setFinprodId(msgJSON.getString("ASSET_CODE")); // 资产代码
                finProductAdd.setPurSpeed(msgJSON.getLong("PURCHASE_PERIOD")); // 申购确认速度
                finProductAdd.setRedSpeed(msgJSON.getLong("RED_PERIOD")); // 赎回确认速度
                finProductAdd.setFundMangerName(msgJSON.getString("FUND_MANGER")); // 基金经理姓名
                entity.setFinProductAdd(finProductAdd);




                String operateTye = msgJSON.getString("OPERATE_TYPE");
                OperationType type;
                if(operateTye.equals("1")){
                    type = OperationType.NEW;
                }else if(operateTye.equals("2")){
                    type = OperationType.MODIFY;
                }else{
                    type = OperationType.DELETE;
                }
                this.doComplete(entity, type);
                //修改接口服务日志表状态--成功
                interfServiceLogService.updateInterfServiceLog(id, true, "计划类利率型标的数据接入成功！");
            }
        }catch(Exception e) {
            //修改接口服务日志表状态--失败
            interfServiceLogService.updateInterfServiceLog(id, false, e.getMessage());
        }
        System.out.println("##############################计划类利率型标的 接口数据入库end#############################");
    }
}
