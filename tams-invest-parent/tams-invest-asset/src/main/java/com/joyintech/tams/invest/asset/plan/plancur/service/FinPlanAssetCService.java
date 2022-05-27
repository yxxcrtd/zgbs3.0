/*
 * 文件名：FinPlanAssetCService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.plan.plancur.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.dto.BussFinIntPayDto;
import com.joyintech.tams.component.business.report.asset.plan.dto.BussRepAssetMngProdInfo;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.asset.interfaceDto.*;
import com.joyintech.tams.invest.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.util.MapParseUtil;
import com.joyintech.tams.invest.interf.trade.fromxp.service.InterfServiceLogService;
import com.joyintech.tams.systemtools.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.system.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.report.asset.plan.BussRepAssetMngProdInfoService;
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.asset.plan.plancur.dto.FinPlanAssetCDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author qiangchengxi
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCService
 * @since 1.0
 */
@Service
public class FinPlanAssetCService extends CommonExportService<FinPlanAssetCDto>
    implements BaseProcessService<FinPlanAssetCDto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(FinPlanAssetCService.class);

    @Autowired(required=false)
    private  FinPlanAssetCService finPlanAssetCService;
    /**
     * 资产管理计划中债信息组件
     */
    @Autowired(required=false)
    private BussRepAssetMngProdInfoService bussRepAssetMngProdInfoService;
    /**
     * 共通--资产
     */
    @Autowired(required=false)
    private AssetCommonService assetCommonService;
    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    @Autowired
    private InterfServiceLogService interfServiceLogService;
    
    @Override
    public void doInsert(FinPlanAssetCDto entity, OperationType type) {
        updateFiles(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(FinPlanAssetCDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        entity.setBranch(Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        FinProduct finProduct = new FinProduct();

        //金融产品代码（估值核算）
        finProduct.setFinprodAbbr(finProduct.getFinprodName());
        finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        entity.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        BeanUtil.copyPropertiesIgnoreNull(finProduct,entity);
        finProduct.setProfitType(DictCodeList.收益类型_净值型.getValue());
//        finProduct.setTermDays((dto.getMdate().getTime()-dto.getVdate().getTime())/1000/60/60/24);
        if (entity.getMdate() != null) {
            finProduct.setTermDays((entity.getMdate().getTime()-entity.getVdate().getTime())/1000/60/60/24);
        }
        finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
        entity.setBussRepAssetMngProdInfoDto(bussRepAssetMngProdInfoService.
            getBussRepAssetMngProdInfoDto(entity.getBussRepAssetMngProdInfo()));
       /* update by zhuyongkang 20191009 start*/
//        entity.setBussParticipantInfoDto(assetCommonService.enBussParticipantInfoDto(finProduct,
//            entity.getBussParticipantInfoDto(),entity.getBranch()));
        /* update by zhuyongkang 20191009 end*/
        // entity.setSystemCode("SYS");
        //add by dongzhiling start at 2019-02-22 :生成委外组合
        PtlPortfolio ptlPortfolio = new PtlPortfolio();
        ptlPortfolio.setPortfolioId(finProduct.getFinprodMarketId());
        ptlPortfolio.setPortfolioType(DictCodeListExt1.组合类型_委外组合.getValue());
        ptlPortfolio.setPortfolioName(finProduct.getFinprodName());
        ptlPortfolio.setVdate(finProduct.getVdate());
        ptlPortfolio.setMdate(finProduct.getMdate());
        ptlPortfolio.setProfitType(DictCodeListExt2.收益类型_货币.getValue());
        entity.setPtlPortfolio(ptlPortfolio);
        //add end
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
        	//新建计划类资产时，自动创建交易对手 add by hxy 20200421 start
    		assetCommonService.autoCreateTradeCounter(entity.getManager());
    		//新建计划类资产时，自动创建交易对手 add by hxy 20200421 end
            action = "insert";
        }
        else {
            action = "update";
        }
        
        // 二级法人权限处理
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
            insertDataAuthTypeInst(entity.getFinprodId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_计划类资产_货币型_信息.getValue());
            //modify by wj 20200624 增加组合权限
            insertDataAuthTypeInst(entity.getFinprodMarketId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_组合管理.getValue());

        }
        
        restTemplate.postForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanAssetC/" + action, entity,
            Integer.class);

        // 推送数据到第三方接口

        //1.计划类要素信息

        Map<String, Object> invMap =  JsonUtils.object2Map(entity);
        invMap.put("pageId","FinPlanTargetAsset002S017");
        investInterfCommService.doInvestInterf(invMap);

        //2.利率信息

        invMap.put("pageId","FinPlanTargetAsset002S018");
        investInterfCommService.doInvestInterf(invMap);

    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<FinPlanAssetCDto> selectForPage(Map<String, Object> map) {
        return restTemplate.postForPage(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanAssetC/findPage",
            map, FinPlanAssetCDto.class);
    }
    
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
        String isShowType = (String) exportParam.getSelectParam().get("isShowType");
		if (StringUtil.equals(isShowType, "01")) {
			exportParam.config("/com/joyintech/tams/invest/asset/plan/plancur/conf/export_conf.xml")
					.dataSource(DataSourceType.MASERT);
		} else {
			exportParam.config("/com/joyintech/tams/invest/asset/plan/plancur/conf/export_conf002.xml")
					.dataSource(DataSourceType.MASERT);
		}
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<FinPlanAssetCDto> selectPageData(Map<String, Object> selectParam) {
        try {
            return selectForPage(selectParam);
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getIssuerId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
            + "/CommonSelect/getIssuer", map,
            SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getOriginalOwner(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
                        + "/CommonSelect/getOriginalOwner", map,
                SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getManagerId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
            + "/CommonSelect/getManager", map,
            SelectOptionDto.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getTrusteeId(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
            + "/CommonSelect/getTruster", map,
            SelectOptionDto.class);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    public String getFinprodId(String finprodMarketId){
      return  restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanAssetC/getFinprodId?finprodMarketId="+finprodMarketId,String.class
        );
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/
    /*增加资产代码重复校验 add by zhuyongkang 20191021 start*/
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
                        + "/FinPlanAssetC/selectFinprodMarketId?finprodMarketId="
                        + finprodMarketId,
                Integer.class);
    }
    /*增加资产代码重复校验 add by zhuyongkang 20191021 end*/
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinPlanAssetCDto getBasicInfoByFinprodId(String finprodId) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("finprodId", finprodId);
        return restTemplate.postForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanAssetC/getBasicInfoByFinprodId", param,
            FinPlanAssetCDto.class);
    }
    private void updateFiles(FinPlanAssetCDto entity) {
        List<SysUploadFileInfo> files = entity.getFileArray();
        if(files.size()>0) {
            List<String> fileNo = new ArrayList<>();
            files.forEach(sysUploadFileInfo -> {
                fileNo.add(sysUploadFileInfo.getSeqNo());
            });
            fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
        }
    }

    @Override
    public void doUpdate(FinPlanAssetCDto entity) {
        updateFiles(entity);
    }

    @Override
    public void doDelete(String workflowId) {
        FinPlanAssetCDto finPlanProfitAssetDto = finPlanAssetCService.getBussinessEntityByWorkflowId(workflowId);
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(finPlanProfitAssetDto.getFjId());
        if(files.size()>0) {
            files.forEach(uploadFile -> {
                fileAttachmentProcess.remove(uploadFile.getFileNo());
            });
        }
    }

    /**
     *资管计划-净值型 接口数据入库
     * @param id
     * @param msg
     */
    public void executeInterf(String id,String msg) {
        System.out.println("##############################资管计划-净值型 接口数据入库start#############################");
        FinPlanAssetCDto entity = new FinPlanAssetCDto();
        try {
            if(StringUtil.isNotEmpty(msg)) {
                JSONObject msgJSON = JSONObject.parseObject(msg);
                entity.setFinprodId(msgJSON.getString("asset_code")); // 资产代码
                entity.setFinprodAbbr(msgJSON.getString("asset_sname")); // 资产代码简称
                entity.setFinprodName(msgJSON.getString("asset_name")); // 资产代码全称
                entity.setpFinprodId(msgJSON.getString("mother_asset_code")); // 母资产代码
                entity.setTrialNo(msgJSON.getString("trial_no")); // 法审编号
                entity.setContractNo(msgJSON.getString("contract_no")); // 合同编号
                entity.setProfitType(msgJSON.getString("profit_type")); // 收益类型
                entity.setIssueAmt(msgJSON.getBigDecimal("issue_amt")); // 发行规模
                entity.setCcy(msgJSON.getString("curr_type")); // 币种
                entity.setVdate(msgJSON.getDate("value_date")); // 起息日
                entity.setMdate(msgJSON.getDate("end_date")); // 到期日
                entity.setIdate(msgJSON.getDate("estab_date")); // 发行日
                entity.setCreditEnhanceMeasure(msgJSON.getString("credit_measures")); // 增信措施
                entity.setIssueType(msgJSON.getString("ipo_way")); // 募集方式
                entity.setIssuer(msgJSON.getString("publisher_code")); // 发行人
                entity.setTrusteeId(msgJSON.getString("trustee_code")); // 托管人
                entity.setManager(msgJSON.getString("manager")); // 管理人
                entity.setFundMangerName(msgJSON.getString("fund_manager")); // 基金经理姓名
                entity.setCalendarId(msgJSON.getString("calendar_id")); // 交易日历
                if(msgJSON.getString("is_guaranteed").equals("1")){ // 是否保本
                    entity.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_保本.getValue());
                }else{
                    entity.setCapiIncomeFeature(DictCodeListExt1.本金收益特征_非保本.getValue());
                }
                entity.setPurSpeed(msgJSON.getString("purchase_period")); // 申购确认速度
                entity.setRedSpeed(msgJSON.getString("red_period")); // 赎回确认速度
                entity.setInvestmentAttributes(msgJSON.getString("investment_attributes")); // 投资性质
                entity.setIsCompoundInt(msgJSON.getString("is_compound_int")); // 是否按日复利

                // 委外投资-协议方式报送信息
                BussRepAssetMngProdInfo bussRepAssetMngProdInfo = new BussRepAssetMngProdInfo();
                bussRepAssetMngProdInfo.setRepAssetType(msgJSON.getString("sub_code")); // 资产/负债类别;
                bussRepAssetMngProdInfo.setRepYieldFlag(msgJSON.getString("is_exits_yield")); // 是否有预期收益率
                bussRepAssetMngProdInfo.setRepMaxRate(msgJSON.getBigDecimal("high_rate")); // 预期最高收益率%
                bussRepAssetMngProdInfo.setRepMinRate(msgJSON.getBigDecimal("low_rate")); // 预期最低收益率%
                entity.setBussRepAssetMngProdInfo(bussRepAssetMngProdInfo);

                // 分红计划组件
                BussFinIntPayDto bussFinIntPayDto = new BussFinIntPayDto();
                bussFinIntPayDto.setPayType(msgJSON.getString("paytype")); // 分红方式
                bussFinIntPayDto.setPayCycle(msgJSON.getString("paycycle")); // 分红频率
                bussFinIntPayDto.setDayValue(msgJSON.getString("dayvalue")); // 分红日期
                bussFinIntPayDto.setEndTdayRule(msgJSON.getString("end_tday_rule")); // 截止日营业日准则
                bussFinIntPayDto.setPayTdayRule(msgJSON.getString("pay_tday_rule")); // 付息营业日准则
                bussFinIntPayDto.setRemark(msgJSON.getString("remark")); // 备注
                entity.setBussFinIntPayDto(bussFinIntPayDto);

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
                interfServiceLogService.updateInterfServiceLog(id, true, "资管计划-净值型接口数据接入成功！");
            }
        }catch(Exception e) {
            //修改接口服务日志表状态--失败
            interfServiceLogService.updateInterfServiceLog(id, false, e.getMessage());
        }
        System.out.println("##############################资管计划-净值型 接口数据入库end#############################");
    }
}
