/*
 * 文件名：FinProductBuild002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：zhangliang 修改时间：2018年10月12日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.product.productelement.netvalueproduct.service;

import com.google.common.collect.Maps;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.PtlValParam;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.portfolio.dto.FundAccountInfoDto;
import com.joyintech.tams.component.business.portfolio.dto.SecuritiesAccountInfoDto;
import com.joyintech.tams.component.business.prod.*;
import com.joyintech.tams.component.business.prod.dto.*;
import com.joyintech.tams.component.business.report.prod.BussRepFinProductInfoService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.prodinfo.BussRuleService;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCond;
import com.joyintech.tams.invest.common.redisinit.service.RiskRedisInitService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.invest.product.productelement.anaplerotic.dto.FinProductAnapleroticInfoDto;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.dto.FinNetValueProductBuild002Dto;
import com.joyintech.tams.invest.product.productelement.netvalueproduct.dto.PtlPortfolioInfoDto;
import com.joyintech.tams.invest.product.productelement.scheduling.dto.PrdIssueScheduleDto;
import com.joyintech.tams.invest.product.productelement.scheduling.service.FinProductScheduling002Service;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.service.WorkFlowService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品新建管理
 * 
 * @author zhangliang
 * @version 1.0 2018年10月12日
 * @see FinNetValueProductBuild002Service
 * @since 1.0
 */
@Service
public class FinNetValueProductBuild002Service extends BaseService<FinNetValueProductBuild002Dto> implements BaseProcessService<FinNetValueProductBuild002Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FinNetValueProductBuild002Service.class);

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductScheduling002Service finProductScheduling002Service;

    /**
     * {@linkplain}
     */
    @Autowired
    private BussRepFinProductInfoService bussRepFinProductInfoService;

    /**
     * {@linkplain}
     */
    @Autowired
    private BussFinProductFeeInfoService bussFinProductFeeInfoService;

    // 丁俊峰添加浮动管理费
    @Autowired
    private BussFinProductFloatFeeService bussFinProductFloatFeeService;

    /**
     * 信息披露业务组件Service
     */
    @Autowired(required = false)
    private BussFinProductRevalDocumentService bussFinProductRevalDocumentService;

    /**
     * 开放计划组件
     */
    @Autowired
    private BussFinProductOpenPlanService bussFinProductOpenPlanService;

    /**
     * 付息计划组件
     */
    @Autowired
    private BussFinProductPayPlanService bussFinProductPayPlanService;

    /**
     * {@linkplain}
     */
    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired(required = false)
    private FinNetValueProductBuild002Service finNetValueProductBuild002Service;

    /**
     * 业务流程表service
     */
    @Autowired(required = false)
    private WorkFlowService workflowService;

    @Autowired
    private BussRuleService bussRuleService;

    /**
     * redis数据初始化
     */
    @Autowired
    private RiskRedisInitService<ImplCond> redisRriter;

    /**
     * 线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 处理其他信息（待处理的组合信息、费用信息）
     * 
     * @param entity 产品信息
     * @see
     * @since 1.0
     */
    private void insertOtherData(FinNetValueProductBuild002Dto entity) {
        // 未选组合时生成一条待处理的组合信息 (组合代码 = 市场代码 组合名称 = 金融产品简称)
        WorkFlowParameter<PtlPortfolioInfoDto> params = new WorkFlowParameter<PtlPortfolioInfoDto>();
        params.setPageId("PtlManagementInfo002");
        params.setElementId("PtlManagementInfo002_btn_save");
        params.setBusinessCode(entity.getFinprodMarketId());
        params.setServiceName("PtlManagementInfo001Service");
        params.setBusinessType(DictCodeList.共通业务类型_组合管理.getValue());
        // 设置流程业务说明, 组合名称 + "(" + 组合名称 + ")" + "-" + "组合代码" + "(" + "组合代码"+")"
        params.setComment(
            item -> this.getMessage("PtlManagementInfo001.portfolioName") + "(" + item.getPortfolioName() + ")" + "-"
                    + this.getMessage("PtlManagementInfo001.portfolioId") + "(" + item.getPortfolioId() + ")");

        try {
            PtlPortfolioInfoDto ptlPortfolioInfoDto = new PtlPortfolioInfoDto();
            BeanUtil.copyProperties(ptlPortfolioInfoDto, entity);
            ptlPortfolioInfoDto.setPortfolioId(entity.getFinprodMarketId());
            ptlPortfolioInfoDto.setPortfolioName(entity.getFinprodAbbr());
            ptlPortfolioInfoDto.setPortfolioType(DictCodeListExt1.组合类型_投资类组合.getValue());
            ptlPortfolioInfoDto.setProfitType(entity.getProfitType());
            ptlPortfolioInfoDto.setOpenType(entity.getOperationType());
            ptlPortfolioInfoDto.setRaiseType(entity.getIssueType());
            ptlPortfolioInfoDto.setInvestNature(entity.getInvestNature());
            ptlPortfolioInfoDto.setVdate(entity.getVdate());
            ptlPortfolioInfoDto.setMdate(entity.getMdate());
            ptlPortfolioInfoDto.setIsGuarantee("N");
            PtlValParam ptlValParam = new PtlValParam();
            BeanUtil.copyProperties(ptlValParam, entity.getBussFinProductValuationDto());
            if(StringUtil.isNotBlank(ptlPortfolioInfoDto.getProfitType()) && StringUtil.equals(ptlPortfolioInfoDto.getProfitType(),DictCodeList.收益类型_货币型.getValue())){
                ptlValParam.setMilionProfitDec(ptlValParam.getUnitvalueDec());
                ptlValParam.setMilionProfitDecMode(ptlValParam.getUnitvalueMode());
            }
            ptlPortfolioInfoDto.setPtlValParam(ptlValParam);

            // 二级法人改造 modify by wj  20200619 五个特殊参数 修改 为特殊取值方法
            String userName = "";
            if(MultilevelDataAuthorityUtil.isEnabled()){
                userName = CacheUtil.getSpecialSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID, MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
            }else{
                userName = CacheUtil.getSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID);
            }
            List<String> userIdList = new ArrayList<String>();
            //userIdList.add(userName);
            /*add by liupengfei for 多级法人改造 默认选中 上级管理员用户和本级管理员 start*/
//            if(MultilevelDataAuthorityUtil.isEnabled()) {
//                Map<String, Object> param = new HashMap<>();
//                String createInst = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
//                List<String> resourcesTreeInstCodeList = CommonUtil.getParentInstitution(createInst);
//                // 修复npe bug modify by wj ----- 20200715
//                if (resourcesTreeInstCodeList == null) {
//                    resourcesTreeInstCodeList = new ArrayList<>();
//                }
//
//                //BS-1942 modify by wj 20200828 会将成员行机构的is_admin用户也加入权限。
//                //【组合审批】联社模式，默认分配的用户不对
//                //resourcesTreeInstCodeList.add(createInst);
//                param.put("resourcesTreeInstCodeList", resourcesTreeInstCodeList);
//                List<String> userNameList = restTemplate.postForObject(
//                        ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getAdminUserList",
//                        param, List.class);
//                if (!StringUtil.isEmptyList(userNameList)) {
//                    userIdList.addAll(userNameList);
//                }
//                userIdList.add(entity.getCreateUser());
//            }
            /*add by liupengfei for 多级法人改造 默认选中 上级管理员用户和本级管理员 end*/     
            //设置默认用户  TISHKBS-1941
            //联社模式下-本机构以及上级机构所有用户
            //1. 获取所有机构
            Map<String, Object> param = new HashMap<>();
            List<String> resourcesTreeInstCodeList = null;
            if(MultilevelDataAuthorityUtil.isEnabled()) {
                String createInst = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
                resourcesTreeInstCodeList = CommonUtil.getParentInstitution(createInst);
                // 修复npe bug modify by wj ----- 20200715
                if (resourcesTreeInstCodeList == null) {
                    resourcesTreeInstCodeList = new ArrayList<>();
                }
                resourcesTreeInstCodeList.add(createInst);
            }
            //银行模式下本机构所有用户
            else {
            	resourcesTreeInstCodeList = new ArrayList<>();
            	String institutionCode = restTemplate.postForObject(
            			ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getUserInstitution"
            			,LoginUserUtil.getUserName(), String.class);
            	if(StringUtil.isNotEmpty(institutionCode)) {
            		resourcesTreeInstCodeList.add(institutionCode);
            	}
            }
            param.put("resourcesTreeInstCodeList", resourcesTreeInstCodeList);
            //2. 获取机构下所有用户
            List<String> userNameList = restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/getUserList",
                    param, List.class);
            if (!StringUtil.isEmptyList(userNameList)) {
                userIdList.addAll(userNameList);
            }
            //3. 设置组合用户
            ptlPortfolioInfoDto.setUserId(userIdList);
            // 组装资金账户信息
            List<FundAccountInfoDto> fundAccountInfoList = new ArrayList<FundAccountInfoDto>();
            List<BussFinProductFundAccountInfoDto> prod_accountinfo = entity.getBussFinProductFundAccountInfoDto();
            if (prod_accountinfo != null && prod_accountinfo.size() > 0) {
                for (BussFinProductFundAccountInfoDto temp_account : prod_accountinfo) {
                    FundAccountInfoDto fundAccountInfo = new FundAccountInfoDto();
                    fundAccountInfo.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
                    fundAccountInfo.setAcctType(temp_account.getAcctType());
                    //fundAccountInfo.setAcctNo(temp_account.getAcctNo().concat("_" + temp_account.getCcy()));
                    fundAccountInfo.setAcctNo(temp_account.getAcctNo());
                    fundAccountInfo.setAcctName(temp_account.getAcctName());
                    fundAccountInfo.setBankDep(temp_account.getBankDep());
                    fundAccountInfo.setLargePayno(temp_account.getLargePayno());
                    fundAccountInfo.setCcy(temp_account.getCcy());
                    fundAccountInfo.setCreateDept(temp_account.getCreateDept());
                    fundAccountInfo.setCreateTime(temp_account.getCreateTime());
                    fundAccountInfo.setCreateUser(temp_account.getCreateUser());
                    fundAccountInfo.setUpdateTime(temp_account.getUpdateTime());
                    fundAccountInfo.setUpdateUser(temp_account.getUpdateUser());
                    fundAccountInfoList.add(fundAccountInfo);
                }
            }
            ptlPortfolioInfoDto.setFundAccountInfoDto(fundAccountInfoList);
            // 证券账户信息
            List<SecuritiesAccountInfoDto> securitiesAccountInfoList = new ArrayList<SecuritiesAccountInfoDto>();
            ptlPortfolioInfoDto.setSecuritiesAccountInfoDto(securitiesAccountInfoList);

            WorkFlowUtil.startProcess(params, null, null, userName, OperationType.NEW, false, ptlPortfolioInfoDto);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinNetValueProductBuild002Dto entity, OperationType type) {
        if (type == OperationType.DELETE) {
            if (StringUtil.isNotEmpty(entity.getFinprodMarketId())) {
                restTemplate.getForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/deleteBokBookSet?finprodId=" + "F16_"
                                          + entity.getFinprodMarketId(),
                    Integer.class);
            }
            // add by niuqianyu 2019-07-18 start 删除附件
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(entity.getFinprodId());
            if (files.size() > 0) {
                files.forEach(uploadFile -> {
                    fileAttachmentProcess.remove(uploadFile.getFileNo());
                });

            }
            // add by niuqianyu 2019-07-18 end

            // update zhangweiqi 解决不能删除的问题
            if (!StringUtils.isNotEmpty(entity.getFinprodId()))
                entity.setFinprodId(commonBusinessService != null ? commonBusinessService.getFinProdId(
                    entity.getFinprodMarketId(), DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), null) : IDGenerator.generate());
            restTemplate.getForObject(ServiceApplication.PRODUCT_SERVICE_NAME
                                      + "/FinProductCommon/deleteFinProductCommon?finprodId=" + entity.getFinprodId(),
                Integer.class);

            if (entity.getBussRepFinProductInfoDto() != null) {
                bussRepFinProductInfoService.delete(entity.getFinprodId());
            }
        }
        else {
        	if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.PRODUCTT_IS_MERGER), DictCodeList.是否_否.getValue())) {
        		//当分开时，金融产品代码为F16+行内标识码
            	if(type == OperationType.NEW){
            		// 生成金融产品代码
                    entity.setFinprodId(commonBusinessService != null ? commonBusinessService.getFinProdId(entity.getFinprodMarketId(),
                        DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), null) : IDGenerator.generate());
                    entity.getBussRepFinProductInfoDto().setIsMerger(DictCodeList.是否_否.getValue());
            	}
            	else{
                	// 生成金融产品代码
            		if(StringUtil.isEmpty(entity.getFinprodId())){
                        entity.setFinprodId(commonBusinessService != null ? commonBusinessService.getFinProdId(entity.getFinprodMarketId(),
                                DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), null) : IDGenerator.generate());
            		}
                    entity.getBussRepFinProductInfoDto().setIsMerger(DictCodeList.是否_否.getValue());
            	}
        	}
        	else{
            	// 生成金融产品代码
                entity.setFinprodId(commonBusinessService != null ? commonBusinessService.getFinProdId(entity.getFinprodMarketId(),
                    DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), null) : IDGenerator.generate());

                if(StringUtil.isEmpty(entity.getRepFinprodInsId())) {
                    entity.setRepFinprodInsId(entity.getFinprodMarketId());
                }
        	}
            // 分支序号
            entity.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 画面Id
            entity.setPageId("FinNetValueProductBuild002");
            // 组合代码
            boolean ptlFlag = false;
            if (!StringUtil.isNotEmpty(entity.getPtlId())) {
                ptlFlag = true;
                entity.setPtlId(entity.getFinprodMarketId());
            }
            // 循环生成付费计划信息
            if (entity.getBussFinProductFeeInfoDto() != null) {
                if (!StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())) {
                    // 一体化对应etliu start
                    // List<? extends DataDictItem> feeTypeItemList = SysDataUtil.getDataDictItem(
                    // DictCode.费用类型.getValue());
                    List<? extends DataDictItem> feeTypeItemList = SysDataUtil.getDataDictItem(DictCode.费用品种类型.getValue());
                    // 一体化对应etliu end
                    List<? extends DataDictItem> basicItemList = SysDataUtil.getDataDictItem(DictCode.计费基数_费用品种.getValue());
                    List<? extends DataDictItem> basisItemList = SysDataUtil.getDataDictItem(DictCode.计息基础.getValue());
                    for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList()) {
                        // 处理费用类型、计费基础、计息基础
                        feeTypeItemList.forEach(item -> {
                            if (StringUtil.equals(item.getItemValue(), bussFinProductFeeInfoDto.getFeeType())) {
                                bussFinProductFeeInfoDto.setFeeType(item.getItemCode());
                            }
                        });
                        basicItemList.forEach(item -> {
                            if (StringUtil.equals(item.getItemValue(), bussFinProductFeeInfoDto.getBasicBill())) {
                                bussFinProductFeeInfoDto.setBasicBill(item.getItemCode());
                            }
                        });
                        basisItemList.forEach(item -> {
                            if (StringUtil.equals(item.getItemValue(), bussFinProductFeeInfoDto.getBasis())) {
                                bussFinProductFeeInfoDto.setBasis(item.getItemCode());
                            }
                        });

                        // 不规则费用不生成付费计划
                        // 一体化对应etliu start
                        // if (!StringUtil.equals(bussFinProductFeeInfoDto.getPayFeeRule(),
                        // DictCodeList.付费规则_不规则.getValue())) {
                        if (!StringUtil.equals(DictCodeList.收费类型_不规则费用.getValue(), bussFinProductFeeInfoDto.getChargeType())
                            && !StringUtil.equals(bussFinProductFeeInfoDto.getPayFeeRule(), DictCodeList.频率_不规则.getValue())) {
                            // 一体化对应etliu end
                            bussFinProductFeeInfoDto.setDealCalId(entity.getCalendarId());
                            bussFinProductFeeInfoDto.setVdate(entity.getVdate());
                            bussFinProductFeeInfoDto.setMdate(entity.getMdate());
                            bussFinProductFeeInfoDto.setProfitType(entity.getProfitType());
                            bussFinProductFeeInfoDto.setFinCashPlanList(
                                bussFinProductFeeInfoService.generatePayFeePlan(bussFinProductFeeInfoDto));
                        }
                        //ZGBSS-1320 费用品种新建 二级法人改造
                        if(MultilevelDataAuthorityUtil.isEnabled()) {
                            //创建机构代码
                            String createInst =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(entity.getCreateUser());
                            entity.setCreateInst(createInst);

                            // 有查询权限的机构代码
                            List<String> searchOrgCodeList = null;
                            // 有编辑权限的机构代码
                            List<String> editOrgCodeList = null;
                            // 新建或修改时重新设置二级法人权限，删除时则清空
                            if (!OperationType.DELETE.equals(type)) {
                                //获取所有父级机构。  拥有查看权限
                                searchOrgCodeList = CommonUtil.getParentInstitution(createInst);
                                // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                                editOrgCodeList = Arrays.asList(entity.getCreateInst());
                            }
                            // 记录二级法人权限
                            insertDataAuthTypeInst(bussFinProductFeeInfoDto.getFeeId(), searchOrgCodeList, editOrgCodeList, BussinessDictCodeList.共同业务类型_费用品种管理.getValue());
                        }
                    }
                }
            }

            // 生成开放计划
//            if (entity.getBussFinProductOpenPlanDto() != null) {
//                BussFinProductOpenPlanDto bussFinProductOpenPlanDto = entity.getBussFinProductOpenPlanDto();
//                bussFinProductOpenPlanDto.setVdate(entity.getVdate());
//                bussFinProductOpenPlanDto.setMdate(entity.getMdate());
//                bussFinProductOpenPlanDto.setDealMode(entity.getDealMode());
//                bussFinProductOpenPlanDto.setTradCalender(entity.getCalendarId());
//                bussFinProductOpenPlanDto.setRaiseStrDate(entity.getFirstSaleVdate());
//                bussFinProductOpenPlanDto.setRaiseEndDate(entity.getFirstSaleMdate());
//                bussFinProductOpenPlanDto.setPurConfDays(entity.getPurSpeed());
//                bussFinProductOpenPlanDto.setIsCycle(entity.getIsCycle());
//                bussFinProductOpenPlanDto.setCloseTerm(entity.getCloseDays());
//                entity.getBussFinProductOpenPlanDto().setFinOpenPlanList(
//                    bussFinProductOpenPlanService.genOpenPlanList(bussFinProductOpenPlanDto));
//            }

            // 付息计划
            if (entity.getBussFinProductPayPlanDto() != null) {
                BussFinProductPayPlanDto bussFinProductPayPlanDto = entity.getBussFinProductPayPlanDto();
                bussFinProductPayPlanDto.setVdate(entity.getVdate());
                bussFinProductPayPlanDto.setMdate(entity.getMdate());
                bussFinProductPayPlanDto.setCalendarId(entity.getCalendarId());
                entity.getBussFinProductPayPlanDto().setCashPlanList(
                    bussFinProductPayPlanService.getProductInterestPlanData(bussFinProductPayPlanDto));
            }

            //
            /**
             * 丁俊峰 生成浮动管理付费计划
             */
            if (StringUtil.equals(DictCodeList.处理模式_分档周期型.getValue(), entity.getDealMode())
                || StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode())) {
                // 浮动管理费分层信息 不为空
                if (entity.getBussFinProductFloatFeeDto() != null
                    && !StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())) {
                    entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList().forEach(
                        bussFinProductFloatFeeDto -> {
                            if (StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())) {
                                bussFinProductFloatFeeDto.setVdate(entity.getVdate());
                                bussFinProductFloatFeeDto.setMdate(entity.getMdate());
                                bussFinProductFloatFeeDto.setDealCalId(entity.getCalendarId());
                                bussFinProductFloatFeeDto.setFinCashPlanList(
                                    bussFinProductFloatFeeService.generateFloatFeePlan(bussFinProductFloatFeeDto));
                            }
                        });
                }
            }
            else {
                // 浮动管理费分层信息 不为
                if (entity.getBussFinProductFloatFeeDto() != null
                    && StringUtil.isNotEmpty(entity.getBussFinProductFloatFeeDto().getFeeId())) {
                    BussFinProductFloatFeeDto bussFinProductFloatFeeDto = entity.getBussFinProductFloatFeeDto();
                    bussFinProductFloatFeeDto.setVdate(entity.getVdate());
                    bussFinProductFloatFeeDto.setMdate(entity.getMdate());
                    bussFinProductFloatFeeDto.setDealCalId(entity.getCalendarId());
                    bussFinProductFloatFeeDto.setFinCashPlanList(
                        bussFinProductFloatFeeService.generateFloatFeePlan(bussFinProductFloatFeeDto));
                }
            }

            if (entity.getBussFinProductBonusPlanDto() != null
                && StringUtil.equals(DictCodeList.分红规则_申购同频.getValue(), entity.getBussFinProductBonusPlanDto().getPayCycle())) {
                // 开放计划不为空
                if (entity.getBussFinProductOpenPlanDto() != null
                    && !StringUtil.isEmptyList(entity.getBussFinProductOpenPlanDto().getFinOpenPlanList())
                	//add by cxq 20200108 解决OPEX-745
                    && !StringUtil.equals(entity.getBussFinProductOpenPlanDto().getOpenMode(),DictCodeList.开放模式_高频开放.getValue())) {
                	List<FinCashPlan> cashPlanList = new ArrayList<>();

                	// modify by lizhi 20200712 BS-1108货币型产品创建，申购同频时生成的分红计划不对 start
//                    for (FinOpenPlan opdto : entity.getBussFinProductOpenPlanDto().getFinOpenPlanList()) {
//                        FinCashPlan finCashPlan = new FinCashPlan();
//                        finCashPlan.setVdate(opdto.getOpenDateStr());
//                        finCashPlan.setMdate(opdto.getOpenDateEnd());
//                        finCashPlan.setPayDate(opdto.getOpenVdate());
//                        finCashPlan.setMdateUnadjust(opdto.getOpenDateEnd());
//                        cashPlanList.add(finCashPlan);
//                    }
                    
                    List<FinOpenPlan> finOpenPlanList = entity.getBussFinProductOpenPlanDto().getFinOpenPlanList();
                    for(int i = 0; i < finOpenPlanList.size();i++){
                    	FinCashPlan finCashPlan = new FinCashPlan();
                    	FinOpenPlan finOpenPlan = finOpenPlanList.get(i);
                    	if(0 == i){
                            finCashPlan.setVdate(entity.getVdate());
                            finCashPlan.setMdate(finOpenPlan.getOpenDateEnd());
                            finCashPlan.setPayDate(finOpenPlan.getOpenVdate());
                            finCashPlan.setMdateUnadjust(finOpenPlan.getOpenDateEnd());
                    	}else{
                            finCashPlan.setVdate(finOpenPlanList.get(i - 1).getOpenDateEnd());
                            finCashPlan.setMdate(finOpenPlan.getOpenDateEnd());
                            finCashPlan.setPayDate(finOpenPlan.getOpenVdate());
                            finCashPlan.setMdateUnadjust(finOpenPlanList.get(i - 1).getOpenDateEnd());
                    	}
                        cashPlanList.add(finCashPlan);
                    }
                	// modify by lizhi 20200712 BS-1108货币型产品创建，申购同频时生成的分红计划不对 end
                    
                    entity.getBussFinProductBonusPlanDto().setCashPlanList(cashPlanList);
                }
            }
            // 处理信息披露 前台信息披露查看或修改模式带出的是文件名称，此处转成对应Id
            if (entity.getBussFinProductRevalDocumentDto() != null) {
                List<BussFinProductRevalDocumentDto> finProductRevalDocumentDtoList = entity.getBussFinProductRevalDocumentDto().getFinProductRevalDocumentDtoList();
                if (!StringUtil.isEmptyList(finProductRevalDocumentDtoList)) {
                    for (BussFinProductRevalDocumentDto bussFinProductRevalDocumentDto : finProductRevalDocumentDtoList) {
                        List<SelectOptionDto> fileNameList = bussFinProductRevalDocumentService.selectByFileType(
                            bussFinProductRevalDocumentDto.getFileType(), null);
                        if (!StringUtil.isEmptyList(fileNameList)) {
                            fileNameList.forEach(item -> {
                                if (StringUtil.equals(item.getText(), bussFinProductRevalDocumentDto.getFileName())) {
                                    bussFinProductRevalDocumentDto.setFileName(item.getId());
                                }
                            });
                        }

                    }
                }
            }
            if (type == OperationType.NEW) {
                // add by niuqianyu 2019-07-18 satrt 更新附件信息
                List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(entity.getFjId());
                if (files.size() > 0) {
                    List<String> fileNo = new ArrayList<>();
                    files.forEach(uploadFile -> {
                        fileNo.add(uploadFile.getFileNo());
                    });
                    fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFinprodId());
                }
                // add by niuqianyu 2019-07-18 end
                
                entity.setIsFhxgcp(entity.getBussRepFinProductInfoDto().getIsFhxgcp());
                entity.setIsYllccp(entity.getBussRepFinProductInfoDto().getIsYllccp());
                restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/insertFinProductCommon",
                    entity, Integer.class);

                // 中债报送信息登表
                if (entity.getBussRepFinProductInfoDto() != null) {
					 //update by xr for TISHKBS-662 START
                    // 报送信息存值 募集起始日期 yjm start 20190801
                   //entity.getBussRepFinProductInfoDto().setRepRaisePstrdate(entity.getFirstSaleVdate());
                   
                   // entity.getBussRepFinProductInfoDto().setRepRaisePenddate(entity.getFirstSaleMdate());
                    //update by xr for TISHKBS-662 END
                    // 发行机构代码
                    // update by shengqiqiang 产品新建时登记银行代码从 机构登记银行代码管理 功能接入  2020.12.8 start

                        entity.getBussRepFinProductInfoDto().setRepIssuePartyId(CommonUtil.selectBankCode());

                    //entity.getBussRepFinProductInfoDto().setRepIssuePartyId(CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID"));
                    // update by shengqiqiang 产品新建时登记银行代码从 机构登记银行代码管理 功能接入  2020.12.8 end

                    // 报送信息存值 募集起始日期 yjm end 20190801
                    entity.getBussRepFinProductInfoDto().setRepFinprodId(entity.getFinprodId());
                    entity.getBussRepFinProductInfoDto().setRepFinprodName(entity.getFinprodName());
                    if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.PRODUCTT_IS_MERGER), DictCodeList.是否_否.getValue())){
                        entity.getBussRepFinProductInfoDto().setRepFinprodInsId(entity.getRepFinprodInsId());
                    }
                    else{
                        entity.getBussRepFinProductInfoDto().setRepFinprodInsId(entity.getFinprodMarketId());
                    }
                    entity.getBussRepFinProductInfoDto().setRepRaiseVdate(entity.getFirstSaleVdate());
                    entity.getBussRepFinProductInfoDto().setRepRaiseEnddate(entity.getFirstSaleMdate());
                    entity.getBussRepFinProductInfoDto().setRepPrdVdate(entity.getVdate());
                    entity.getBussRepFinProductInfoDto().setRepPrdMdate(entity.getMdate());
                    //modify by wm 申报登记表此字段存值错误 2019-12-05 start
//                  entity.getBussRepFinProductInfoDto().setRepProfitType(entity.getProfitType());
                  entity.getBussRepFinProductInfoDto().setRepProfitType(entity.getProfitFlag());
                  //modify by wm 申报登记表此字段存值错误 2019-12-05 end
                    entity.getBussRepFinProductInfoDto().setRepExpMaxYield(
                        entity.getBussRepFinProductInfoDto().getRepHighYield());
                    entity.getBussRepFinProductInfoDto().setRepExpMinYield(entity.getBussRepFinProductInfoDto().getRepLowYield());
                    // 募集币种
                    entity.getBussRepFinProductInfoDto().setRepRaiseCcy(entity.getCcy());
                    // 起点销售金额
                    entity.getBussRepFinProductInfoDto().setRepSaleAmtStr(entity.getBussFinProductSaleParamDto().getToffStart());
                    //募集金额存元（原万元）yzg 20191114 start 页面改为元 20191126
                    //entity.getBussRepFinProductInfoDto().setRepRaiseAmtPlan(ArithmeticUtil.multiply(entity.getBussFinProductSaleParamDto().getRaiseAmtPlan(), new BigDecimal(10000)));
                    //募集金额存元（原万元）yzg 20191114 end  页面改为元 20191126
                    // 开放计划信息
                    if (null != entity.getBussFinProductOpenPlanDto()) {
                        // 节假日是否开放
                        entity.getBussRepFinProductInfoDto().setRepHolidayOnFlag(
                            entity.getBussFinProductOpenPlanDto().getHdayOpenFlag());
                        // 开放计划
                        if (!StringUtil.isEmptyList(entity.getBussFinProductOpenPlanDto().getFinOpenPlanList())) {
                            // 首次开放周期起始日
                            entity.getBussRepFinProductInfoDto().setRepFirstOpenDay(
                                entity.getBussFinProductOpenPlanDto().getFinOpenPlanList().get(0).getOpenDateStr());
                        }
                        else {
                            // 起息日
                            entity.getBussRepFinProductInfoDto().setRepFirstOpenDay(entity.getVdate());
                        }
                    }
                    /** 20190909 add by chendijun start **/
                    // 产品销售地区
                    entity.getBussRepFinProductInfoDto().setRepSaleArea(entity.getBussFinProductSaleParamDto().getSaleArea());
                    //增加募集方式
                    entity.getBussRepFinProductInfoDto().setRepIssueType(entity.getIssueType());
                    /** 20190909 add by chendijun end **/
                    /**20191203 add by taoran start **/
                    //增加产品投资性质
                    entity.getBussRepFinProductInfoDto().setRepInvestNature(entity.getInvestNature());
                    //增加是否现金管理类
                    entity.getBussRepFinProductInfoDto().setRepIsCashManage(entity.getIsCashManage());
                    /**20191203 add by taoran end **/
                    bussRepFinProductInfoService.insert(entity.getBussRepFinProductInfoDto());

                    // 二级法人权限处理 WY
                    if(MultilevelDataAuthorityUtil.isEnabled() &&   StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.PRODUCTT_IS_MERGER), DictCodeList.是否_是.getValue())  ) {
                        //创建机构代码
                        String createInst =   MultilevelDataAuthorityUtil.getService().queryOrganizationCode(entity.getCreateUser());
                        entity.setCreateInst(createInst);
                        // 有查询权限的机构代码
                        List<String> searchOrgCodeList = null;
                        // 有编辑权限的机构代码
                        List<String> editOrgCodeList = null;
                        // 新建或修改时重新设置二级法人权限，删除时则清空
                        if (!OperationType.DELETE.equals(type)) {
                            //获取所有父级机构。  拥有查看权限
                            searchOrgCodeList = CommonUtil.getParentInstitution(createInst);
                            // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                            editOrgCodeList = Arrays.asList(entity.getCreateInst());
                        }
                        // 记录二级法人权限
                        insertDataAuthTypeInst(entity.getBussRepFinProductInfoDto().getRepFinprodId(), searchOrgCodeList, editOrgCodeList, DictCodeListExt3.共通业务类型_产品申报登记要素管理.getValue());
                    }

                }
                if (ptlFlag) {
                    this.insertOtherData(entity);// edit by dongenkuo 20190312 创建组合放在service层，此处注释掉
                }
            }
            else if (type == OperationType.MODIFY) {
            	entity.setIsFhxgcp(entity.getBussRepFinProductInfoDto().getIsFhxgcp());
                entity.setIsYllccp(entity.getBussRepFinProductInfoDto().getIsYllccp());
                // add by jiangpeng on 20210401 是否是产品编辑 TISHKBS-1952 用于处理产品编辑时覆盖 BOK_BOOKSET_CAPACCT_INFO(账套清算账户表)的问题
                entity.setProductEdit(true);
                restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductCommon/updateFinProductCommon",
                    entity, Integer.class);

                // 中债报送信息登表
                if (entity.getBussRepFinProductInfoDto() != null) {
                    // 报送信息存值 募集起始日期 yjm start 20190801
                    //entity.getBussRepFinProductInfoDto().setRepRaisePstrdate(entity.getFirstSaleVdate());
                    //entity.getBussRepFinProductInfoDto().setRepRaisePenddate(entity.getFirstSaleVdate());
                    // 发行机构代码
                    entity.getBussRepFinProductInfoDto().setRepIssuePartyId(CommonUtil.selectBankCode());
                    // 报送信息存值 募集起始日期 yjm end 20190801
                    entity.getBussRepFinProductInfoDto().setRepFinprodId(entity.getFinprodId());
                    entity.getBussRepFinProductInfoDto().setRepFinprodName(entity.getFinprodName());
                    if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.PRODUCTT_IS_MERGER), DictCodeList.是否_否.getValue())){
                        entity.getBussRepFinProductInfoDto().setRepFinprodInsId(entity.getRepFinprodInsId());
                    }
                    else{
                        entity.getBussRepFinProductInfoDto().setRepFinprodInsId(entity.getFinprodMarketId());
                    }
                    entity.getBussRepFinProductInfoDto().setRepRaiseVdate(entity.getFirstSaleVdate());
                    entity.getBussRepFinProductInfoDto().setRepRaiseEnddate(entity.getFirstSaleMdate());
                    entity.getBussRepFinProductInfoDto().setRepPrdVdate(entity.getVdate());
                    entity.getBussRepFinProductInfoDto().setRepPrdMdate(entity.getMdate());
                    entity.getBussRepFinProductInfoDto().setRepFinprodName(entity.getFinprodName());
                    //modify by wm 申报登记表此字段存值错误 2019-12-05 start
//                    entity.getBussRepFinProductInfoDto().setRepProfitType(entity.getProfitType());
                    entity.getBussRepFinProductInfoDto().setRepProfitType(entity.getProfitFlag());
                    //modify by wm 申报登记表此字段存值错误 2019-12-05 end
                    entity.getBussRepFinProductInfoDto().setRepExpMaxYield(
                        entity.getBussRepFinProductInfoDto().getRepHighYield());
                    entity.getBussRepFinProductInfoDto().setRepExpMinYield(entity.getBussRepFinProductInfoDto().getRepLowYield());
                    // 募集币种
                    entity.getBussRepFinProductInfoDto().setRepRaiseCcy(entity.getCcy());
                    // 起点销售金额
                    entity.getBussRepFinProductInfoDto().setRepSaleAmtStr(entity.getBussFinProductSaleParamDto().getToffStart());
                    // 销售地区
                    entity.getBussRepFinProductInfoDto().setRepSaleArea(entity.getBussFinProductSaleParamDto().getSaleArea());
                    //增加募集方式
                    entity.getBussRepFinProductInfoDto().setRepIssueType(entity.getIssueType());
                    //募集金额存元（原万元）yzg 20191114 start 页面改为元 20191126
                    //entity.getBussRepFinProductInfoDto().setRepRaiseAmtPlan(ArithmeticUtil.multiply(entity.getBussFinProductSaleParamDto().getRaiseAmtPlan(), new BigDecimal(10000)));
                    //募集金额存元（原万元）yzg 20191114 end  页面改为元 20191126
                    // 开放计划信息
                    if (null != entity.getBussFinProductOpenPlanDto()) {
                        // 节假日是否开放
                        entity.getBussRepFinProductInfoDto().setRepHolidayOnFlag(
                            entity.getBussFinProductOpenPlanDto().getHdayOpenFlag());
                        // 开放计划
                        if (!StringUtil.isEmptyList(entity.getBussFinProductOpenPlanDto().getFinOpenPlanList())) {
                            // 首次开放周期起始日
                            entity.getBussRepFinProductInfoDto().setRepFirstOpenDay(
                                entity.getBussFinProductOpenPlanDto().getFinOpenPlanList().get(0).getOpenDateStr());
                        }
                        else {
                            // 起息日
                            entity.getBussRepFinProductInfoDto().setRepFirstOpenDay(entity.getVdate());
                        }
                    }
					 //增加产品投资性质
                    entity.getBussRepFinProductInfoDto().setRepInvestNature(entity.getInvestNature());
                    bussRepFinProductInfoService.update(entity.getBussRepFinProductInfoDto());
                }

                if (ptlFlag) {
                    this.insertOtherData(entity);// edit by dongenkuo 20190312 创建组合放在service层，此处注释掉
                }
            }
            // 二级法人权限处理
            if(MultilevelDataAuthorityUtil.isEnabled()) {
                //创建机构代码
                String createInst = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
                entity.setCreateInst(createInst); 
                // 有查询权限的机构代码
                List<String> searchOrgCodeList = null;
                // 有编辑权限的机构代码
                List<String> editOrgCodeList = null;
                // 新建或修改时重新设置二级法人权限，删除时则清空
                if (!OperationType.DELETE.equals(type)) {
                    //获取所有父级机构。  拥有查看权限
                    searchOrgCodeList = CommonUtil.getParentInstitution(createInst);
                    // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                    editOrgCodeList = Arrays.asList(entity.getCreateInst());
                }
                // 记录二级法人权限
                insertDataAuthTypeInst(entity.getFinprodId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_产品要素管理.getValue());
                insertDataAuthTypeInst(entity.getFinprodId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_账套管理.getValue());
                insertDataAuthTypeInst(BussinessConstant.BOK_BANK_PREFIX+entity.getFinprodId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_账套管理.getValue());
            } 
            // 清除费用添加时的缓存
            CacheUtil.clearAppCache(entity.getFeeNewAddId());

            //---匹配风险规则包来关联指标且装载到redis 20220517 start---
            if (type == OperationType.MODIFY) {
                assembleRedis(entity);
            }
            //---匹配风险规则包来关联指标且装载到redis 20220517 end---
        }
    }

    /**
     * 装载到redis里
     * @param entity
     */
    private void assembleRedis(FinNetValueProductBuild002Dto entity){
        if (com.joyintech.tams.invest.common.CommonUtil.isType(CacheUtil.getSysParam(RiskUtil.PAGE_REDIS_FLAG), BussinessDictCodeListRds.是否_是)) {
            String portfolioId=StringUtil.replace(entity.getFinprodId(),
                    DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()+ GlobalConstant.UNDER_LINE,StringUtil.EMPTY);
            //修改产品自动匹配规则包
            bussRuleService.matchRulePortByPort(AssetCommonUtil.getProdInfo(portfolioId, obj->obj));
            threadPoolTaskExecutor.execute(()->{
                try {
                    Thread.sleep(RiskUtil.getRefreshTime());
                    ImplCond cond = new ImplCond();
                    cond.setDel(false);
                    cond.setPortfIdArray(portfolioId);
                    logger.info("----------->修改产品基本信息至组合关联指标进入redis开始--------------");
                    redisRriter.dealRiskPtlIndicator(cond);
                    logger.info("----------->修改产品基本信息至组合关联指标进入redis结束--------------");
                } catch (InterruptedException e) {
                    logger.error("===========修改产品基本信息至组合关联指标进入redis异常=============",e);
                }
            });
        }
    }

    /**
     * 根据金融产品代码查询产品排期信息
     * 
     * @param finprodId 金融产品代码
     * @return 产品排期信息
     * @see
     * @since 1.0
     */
    public FinNetValueProductBuild002Dto queryProdScheduleByFinprodId(String finprodId) {
        FinNetValueProductBuild002Dto FinNetValueProductBuild002Dto = new FinNetValueProductBuild002Dto();
        // 根据金融产品代码查询产品排期信息
        PrdIssueScheduleDto prdIssueScheduleDto = finProductScheduling002Service.queryIssueInfoByFinprodId(finprodId);
        if (prdIssueScheduleDto != null) {
            // 设置产品基本信息
            FinNetValueProductBuild002Dto.setFinprodMarketId(prdIssueScheduleDto.getFinprodMarketId());
            FinNetValueProductBuild002Dto.setFinprodName(prdIssueScheduleDto.getFinprodName());
            FinNetValueProductBuild002Dto.setFinprodAbbr(prdIssueScheduleDto.getFinprodAbbr());
            FinNetValueProductBuild002Dto.setEntrustType(prdIssueScheduleDto.getIssueMode());
            FinNetValueProductBuild002Dto.setEntruster(prdIssueScheduleDto.getBranchBank());
            FinNetValueProductBuild002Dto.setFirstSaleVdate(prdIssueScheduleDto.getRaiseStrdate());
            FinNetValueProductBuild002Dto.setFirstSaleMdate(prdIssueScheduleDto.getRaiseEnddate());
            FinNetValueProductBuild002Dto.setVdate(prdIssueScheduleDto.getVdate());
            FinNetValueProductBuild002Dto.setMdate(prdIssueScheduleDto.getMdate());
            FinNetValueProductBuild002Dto.setCcy(prdIssueScheduleDto.getCcy());
        }

        return FinNetValueProductBuild002Dto;
    }

    /**
     * 根据金融产品代码查询产品信息
     * 
     * @param finprodId 金融产品代码
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public FinNetValueProductBuild002Dto queryProdInfoByFinprodId(String finprodId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        map.put("branch", Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        return restTemplate.postForObject(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductBuild/queryProdInfoByFinprodId",
            map, FinNetValueProductBuild002Dto.class);
    }

    /**
     * 查询机构
     *
     * @param map map
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryTrusteeId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductBuild/queryTrusteeId", map,
            SelectOptionDto.class);
    }

    // add by tuqirong 2019-03-26 start 添加附件信息
    private void updateFiles(FinNetValueProductBuild002Dto entity) {
        List<FileCommonDto> files = entity.getFileCommonList();
        if (files.size() > 0) {
            List<String> fileNo = new ArrayList<>();
            files.forEach(fileCommonDto -> {
                fileNo.add(fileCommonDto.getFileNo());
            });
            fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
        }
    }
    
    /**
     * 判断业绩回补的规则类型是否满足产品新建时设置的
     * @param entity 产品基本信息
     * @param finProductAnapleroticInfoDto 业绩回补信息
     * @return true满足，false不满足
     */
    public boolean checkAnapleroticRule(FinNetValueProductBuild002Dto entity,
                                        FinProductAnapleroticInfoDto finProductAnapleroticInfoDto){
        boolean isRight = false;
        //浮动管理费校验
        if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用浮动管理费回补.getValue())){
            List<BussFinProductFloatFeeDto> floatList = entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList();
            if(StringUtil.isEmptyList(floatList)){
                return isRight;
            }
            for (BussFinProductFloatFeeDto bussFinProductFloatFeeDto : floatList) {
                if(StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())&&
                        StringUtil.equals(bussFinProductFloatFeeDto.getProdStyle(), finProductAnapleroticInfoDto.getProdStyle())){
                    isRight = true;
                    break;
                }
            }
        }
        else if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_仅使用固定管理费回补.getValue())){
            List<BussFinProductFeeInfoDto> feeList = entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
            if(StringUtil.isEmptyList(feeList)){
                return isRight;
            }
            for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeList) {
                if((StringUtil.equals(bussFinProductFeeInfoDto.getFeeType(), DictCodeList.费用品种类型_固定管理费.getValue())||
                        StringUtil.equals(bussFinProductFeeInfoDto.getFeeType(), DictCodeList.费用品种类型_固定管理费.getCodeName()))
                        &&(StringUtil.equals(bussFinProductFeeInfoDto.getProdStyle(), finProductAnapleroticInfoDto.getProdStyle())
                        ||StringUtil.isEmpty(bussFinProductFeeInfoDto.getProdStyle()))){
                    isRight = true;
                    break;
                }
            }
        }
        else if(StringUtil.equals(finProductAnapleroticInfoDto.getAnapleroticRule(), DictCodeListExt1.回补规则_优先使用浮动管理费回补.getValue())){
            List<BussFinProductFloatFeeDto> floatList = entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList();
            List<BussFinProductFeeInfoDto> feeList = entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
            if(StringUtil.isEmptyList(floatList)&&StringUtil.isEmptyList(feeList)){
                return isRight;
            }
            for (BussFinProductFloatFeeDto bussFinProductFloatFeeDto : floatList) {
                if(StringUtil.isNotEmpty(bussFinProductFloatFeeDto.getFeeId())&&
                        StringUtil.equals(bussFinProductFloatFeeDto.getProdStyle(), finProductAnapleroticInfoDto.getProdStyle())){
                    isRight = true;
                    break;
                }
            }
            if(!isRight){
                for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeList) {
                    if((StringUtil.equals(bussFinProductFeeInfoDto.getFeeType(), DictCodeList.费用品种类型_固定管理费.getValue())||
                            StringUtil.equals(bussFinProductFeeInfoDto.getFeeType(), DictCodeList.费用品种类型_固定管理费.getCodeName()))
                            &&(StringUtil.equals(bussFinProductFeeInfoDto.getProdStyle(), finProductAnapleroticInfoDto.getProdStyle())
                            ||StringUtil.isEmpty(bussFinProductFeeInfoDto.getProdStyle()))){
                        isRight = true;
                        break;
                    }
                }
            }
        }
        return isRight;
    }
    /**
     * 产品设计系列查询
     * @param param
     * @return
     */
    public List<SelectOptionDto> queryPrdDesign(Map<String, Object> param){
    	return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductBuild/queryPrdDesign", param,
                SelectOptionDto.class);
    }

    @Override
    public void doInsert(FinNetValueProductBuild002Dto entity, OperationType type) {
        updateFiles(entity);
    }

    @Override
    public void doUpdate(FinNetValueProductBuild002Dto entity) {
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("title",this.getMessage("FinProductBuild002.finprodMarketId") + "("
                + entity.getFinprodMarketId() + ")"
                + this.getMessage("FinProductBuild002.finprodAbbr") + "("
                + entity.getFinprodAbbr() + ")" + this.getMessage("FinProductBuild002.vdate")
                + "(" + DateUtil.format(entity.getVdate()) + ")"
                + this.getMessage("FinProductBuild002.mdate") + "("
                + DateUtil.format(entity.getMdate()) + ")");
        map.put("workFlowId",entity.getWorkflowId());
        map.put("updateUser", LoginUserUtil.getUserName());
        map.put("updateTime",DateUtil.now());
        workflowService.updateTitle(map);
        updateFiles(entity);
    }

    @Override
    public void doDelete(String workflowId) {
        FinNetValueProductBuild002Dto finPlanProfitAssetDto = finNetValueProductBuild002Service.getBussinessEntityByWorkflowId(
            workflowId);
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(finPlanProfitAssetDto.getFjId());
        if (files.size() > 0) {
            files.forEach(uploadFile -> {
                fileAttachmentProcess.remove(uploadFile.getFileNo());
            });
        }
    }
    // add by tuqirong 2019-03-26 end
    
}
