/*
 * 文件名：FinPlanNetAssetService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.plan.planNet.service;

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
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepMngAssetPlanInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepTrustInvestModeInfoDto;
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
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.asset.interfaceDto.*;
import com.joyintech.tams.invest.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.util.MapParseUtil;
import com.joyintech.tams.invest.interf.trade.fromxp.service.InterfServiceLogService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 计划类资产（净值型）service
 * 
 * @author liuwei
 * @version 1.0 2018年9月10日
 * @see FinPlanNetAssetService
 * @since 1.0
 */
@Service
public class FinPlanNetAssetService extends CommonExportService<FinPlanNetAssetDto>
    implements BaseProcessService<FinPlanNetAssetDto>{
    /**
     * 共通--资产
     */
    @Autowired
    private AssetCommonService assetCommonService;
    /**
     * 资产管理计划中债信息组件
     */
    @Autowired(required=false)
    private BussRepAssetMngProdInfoService bussRepAssetMngProdInfoService;
    /**
     * 委外投资-协议方式中债信息组件
     */
    @Autowired(required=false)
    private BussRepOutInvestAgrInfoService bussRepOutInvestAgrInfoService;
    /**
     * 计划类资产（净值型）Service
     */
    @Autowired(required=false)
    private FinPlanNetAssetService finPlanNetAssetService;
    /**
     * 各种业务共通组件
     */
    @Autowired(required=false)
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
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    @Autowired
    private InterfServiceLogService interfServiceLogService;

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
            + "/FinPlanNetAsset/getFinProdMarketId?finprodMarketId=" + param,
            SuggestDto.class);
    }

    @Override
    public void doInsert(FinPlanNetAssetDto entity, OperationType type) {
    	entity.setFjId(entity.getWorkflowId());
    	List<FileCommonDto> files = entity.getFileCommonList();
//      if(files.size()>0) {
//          List<String> fileNo = new ArrayList<>();
//          files.forEach(fileCommonDto -> {
//              fileNo.add(fileCommonDto.getFileNo());
//          });
//          fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
//      }
      UploadFile file = null;
//      List<SysUploadFileInfo> syfiList = entity.getFileArray();
      List<String> fileNo = new ArrayList<>();
      if(files!=null) {
          for (FileCommonDto sufi : files) {
              file = fileAttachmentProcess.fetch(sufi.getFileNo());
              if (file != null) {
                  String generateId = IDGenerator.generate();
                  file.setFileNo(generateId);
                  fileAttachmentProcess.upload(file);
                  sufi.setFileNo(generateId);
                  fileNo.add(generateId);
              }
          }
      }
      fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
    }

//    private void updateFiles(FinPlanNetAssetDto entity) {
//        List<FileCommonDto> files = entity.getFileCommonList();
////        if(files.size()>0) {
////            List<String> fileNo = new ArrayList<>();
////            files.forEach(fileCommonDto -> {
////                fileNo.add(fileCommonDto.getFileNo());
////            });
////            fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
////        }
//        UploadFile file = null;
////        List<SysUploadFileInfo> syfiList = entity.getFileArray();
//        List<String> fileNo = new ArrayList<>();
//        for(FileCommonDto sufi : files){
//            file = fileAttachmentProcess.fetch(sufi.getFileNo());
//            String generateId = IDGenerator.generate();
//            file.setFileNo(generateId);
//            fileAttachmentProcess.upload(file);
//            sufi.setFileNo(generateId);
//            fileNo.add(generateId);
//        }
//        fileAttachmentProcess.updateBussinessNo(fileNo, entity.getFjId());
//    }

    @Override
    public void doUpdate(FinPlanNetAssetDto entity) {
    	entity.setFjId(entity.getWorkflowId());
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
    public void doDelete(String workflowId) {
//        FinPlanNetAssetDto FinPlanNetAssetDto = finPlanNetAssetService.getBussinessEntityByWorkflowId(workflowId);
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(workflowId);
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
    public void doComplete(FinPlanNetAssetDto dto, OperationType type) {
        String action ;
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getFinprodId());
            if(files.size()>0) {
                files.forEach(uploadFile -> {
                    fileAttachmentProcess.remove(uploadFile.getFileNo());
                });
            }
            restTemplate.postForObject(
                    ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanNetAsset/" + action, dto,
                    Integer.class);
        }
        else if(OperationType.NEW.equals(type)){
        	//新建计划类资产时，自动创建交易对手 add by hxy 20200421 start
    		assetCommonService.autoCreateTradeCounter(dto.getManager());
    		//新建计划类资产时，自动创建交易对手 add by hxy 20200421 end
            action = "insert";
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getWorkflowId());
            if(files.size()>0) {
                List<String> fileNo = new ArrayList<>();
                files.forEach(uploadFile -> {
                    fileNo.add(uploadFile.getFileNo());
                });
                fileAttachmentProcess.updateBussinessNo(fileNo, dto.getFinprodId());
            }
            FinPlanNetAssetDto entity = getDto(dto);
            restTemplate.postForObject(
                    ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanNetAsset/" + action, entity,
                    Integer.class);
            BaseMap param = BaseMap.newMap().put("portfolioId",entity.getFinprodMarketId()).put("openType","02") ;
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/autoRelate", param,
                    Void.class);
        }
        else{
            action = "update";
            List<UploadFile> files1 = fileAttachmentProcess.fetchByBusinessCode(dto.getFinprodId());
            if(files1.size()>0) {
                files1.forEach(uploadFile -> {
                    fileAttachmentProcess.remove(uploadFile.getFileNo());
                });
            }
            List<UploadFile> files2 = fileAttachmentProcess.fetchByBusinessCode(dto.getWorkflowId());
            if(files2.size()>0) {
                List<String> fileNo = new ArrayList<>();
                files2.forEach(uploadFile -> {
                    fileNo.add(uploadFile.getFileNo());
                });
                fileAttachmentProcess.updateBussinessNo(fileNo, dto.getFinprodId());
            }
            FinPlanNetAssetDto entity = getDto(dto);
            restTemplate.postForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanNetAsset/" + action, entity,
                Integer.class);
            //处理组合包和指标自动关联
            BaseMap param = BaseMap.newMap().put("portfolioId",entity.getFinprodMarketId()).put("openType","02") ;
            restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/autoRelate", param,
                    Void.class);

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
            insertDataAuthTypeInst(dto.getFinprodId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_计划类资产_净值型_信息.getValue());

            //modify by wj 20200624 增加组合权限
            insertDataAuthTypeInst(dto.getFinprodMarketId(), searchOrgCodeList, editOrgCodeList,DictCodeList.共通业务类型_组合管理.getValue());

        }
        
        // 推送数据到第三方接口

        //1.计划类要素信息

        Map<String, Object> invMap = JsonUtils.object2Map(dto);
        invMap.put("pageId","FinPlanTargetAsset002S017");
        investInterfCommService.doInvestInterf(invMap);
        //2.利率信息
        invMap.put("pageId","FinPlanTargetAsset002S018");
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
    public FinPlanNetAssetDto getDto(FinPlanNetAssetDto dto){
        //固定参数-分支序号
        Long branch = Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        //标的类金融产品entity
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct,dto);
        //金融产品代码（估值核算）
        finProduct.setFinprodAbbr(finProduct.getFinprodName());
        finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        finProduct.setProfitType(DictCodeList.收益类型_净值型.getValue());
//        finProduct.setTermDays((dto.getMdate().getTime()-dto.getVdate().getTime())/1000/60/60/24);
        if (dto.getMdate() != null) {
        finProduct.setTermDays((dto.getMdate().getTime()-dto.getVdate().getTime())/1000/60/60/24);
        }
        finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
        dto.setFinProduct(finProduct);
        // 投后信息表by赵东东 2019.9.30 start
        FinPostInvestInfo finPostInvestInfo = new FinPostInvestInfo();
        BeanUtil.copyPropertiesIgnoreNull(finPostInvestInfo,dto);
        dto.setFinPostInvestInfo(finPostInvestInfo);
        // 投后信息表by赵东东 2019.9.30 end
		//标的类金融产品附属表
        FinProductAdd finProductAdd = new FinProductAdd();
		BeanUtil.copyPropertiesIgnoreNull(finProductAdd,dto);
		finProductAdd.setFinprodId(finProduct.getFinprodId());
        //清算速度枚举值新加一个T+2 所以原有判断方法错误 yjm start 20190910
        //finProductAdd.setPurSpeed((long)(StringUtil.equals(dto.getTransMode(),"01")?0:1));
        //update by  zhangnian  20190916 start
        /*if (StringUtil.equals(dto.getTransMode(),"01")){
            finProductAdd.setPurSpeed((long)(0));
        }else if (StringUtil.equals(dto.getTransMode(),"02")){
            finProductAdd.setPurSpeed((long)(1));
        }else {
            finProductAdd.setPurSpeed((long)(2));
        }*/
        finProductAdd.setPurSpeed(Long.parseLong(dto.getPurSpeed()));
        finProductAdd.setRedSpeed(Long.parseLong(dto.getRedSpeed()));
        //update by  zhangnian  20190916 end
        //清算速度枚举值新加一个T+2 所以原有判断方法错误 yjm end 20190910
        finProductAdd.setInvestNature(dto.getInvestmentAttributes());
        finProductAdd.setCreditEnhanceMeasure(dto.getCreditEnhanceMeasure());
        finProductAdd.setFundMangerName(dto.getFundMangerName());
        dto.setFinProductAdd(finProductAdd);
        //金融产品分类表
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(finProduct.getFinprodId());
        finProductType.setBranch(branch);
        finProductType.setType6(dto.getCategory());
        finProductType.setType7(dto.getType());
        finProductType.setType8(DictCodeList.平安资产类型_计划类资产_净值型.getValue());
        //行内五级分类 zhangpuyu 2019.7.22 start
        finProductType.setType9(dto.getType9());
        finProductType.setType10(dto.getType10());
        finProductType.setType11(dto.getType11());
        finProductType.setType12(dto.getType12());
        //行内五级分类 zhangpuyu 2019.7.22 end
        dto.setFinProductType(finProductType);
        //现金流信息List
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        //现金流计算规则dtolist
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        //现金流信息-本金
        FinCashInfo finCashInfo1 = new FinCashInfo();
        finCashInfo1.setCashId(commonBusinessService.generateCashFlowCode(finProduct.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)),DictCodeListExt1.现金流二级类型_本金.getValue()));
        finCashInfo1.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfo1.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfo1.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfo1.setBranch(branch);
        finCashInfo1.setFinprodId(finProduct.getFinprodId());
        finCashInfo1.setCcy(finProduct.getCcy());
        //现金流信息-利息
        FinCashInfo finCashInfo2 = new FinCashInfo();
        BeanUtil.copyPropertiesIgnoreNull(finCashInfo2,finCashInfo1);
        finCashInfo2.setCashId(commonBusinessService.generateCashFlowCode(finProduct.getFinprodId(),DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                Long.valueOf(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)),DictCodeListExt1.现金流二级类型_利息.getValue()));
        finCashInfo2.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfo2.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfoList.add(finCashInfo1);
        finCashInfoList.add(finCashInfo2);
        //现金流信息-参与方信息（多条数据）以及 现金流计算规则dtolist
        if(null!=bussParticipantInfoService){
            if(null!=dto.getBussParticipantInfoDto()){
                BussParticipantInfoDto bussParticipantInfoDto = assetCommonService.enBussParticipantInfoDto(finProduct,dto.getBussParticipantInfoDto(),branch);
                finCashCalcRuleList.addAll(bussParticipantInfoDto.getFinCashCalcRuleList());
                finCashInfoList.addAll(bussParticipantInfoDto.getFinCashInfoList());
            }
        }
        dto.setFinCashCalcRuleList(finCashCalcRuleList);
        dto.setFinCashInfoList(finCashInfoList);
        if(null!=finCashCalcRuleList&&!finCashCalcRuleList.isEmpty()){
          //现金流利率信息表dtolist
            List<FinRateInfo> finRateInfoList = new ArrayList<>();
            //托管费
            FinRateInfo finRateInfo1 = new FinRateInfo();
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo1,finCashCalcRuleList.get(0));
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo1,finProduct);
            finRateInfo1.setRate(finCashCalcRuleList.get(0).getYield());
            finRateInfo1.setEffDate(dto.getVdate());
        	// 一体化对应etliu
            // finRateInfo1.setRateType(DictCodeList.利率类型_固定利率.getValue());
			finRateInfo1.setIntType(DictCodeList.利率类型_固定利率.getValue());
            finRateInfo1.setBranch(branch);
            //管理费
            FinRateInfo finRateInfo2 = new FinRateInfo();
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo2,finRateInfo1);
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo2,finCashCalcRuleList.get(1));
            finRateInfo2.setRate(finCashCalcRuleList.get(1).getYield());
            //其它费
            FinRateInfo finRateInfo3 = new FinRateInfo();
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo3,finRateInfo1);
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo3,finCashCalcRuleList.get(2));
            finRateInfo3.setRate(finCashCalcRuleList.get(2).getYield());
            finRateInfoList.add(finRateInfo1);finRateInfoList.add(finRateInfo2);finRateInfoList.add(finRateInfo3);
            dto.setFinRateInfoList(finRateInfoList);
        }
        if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托产品.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_券商资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货资产管理计划.getValue())){
            if(null!=bussRepAssetMngProdInfoService) {
                BussRepAssetMngProdInfoDto bussRepAssetMngProdInfoDto = bussRepAssetMngProdInfoService.
                        getBussRepAssetMngProdInfoDto(dto.getBussRepAssetMngProdInfo());
                RepAssetDept repAssetDept = new RepAssetDept();
                repAssetDept.setRepFinprodId(finProduct.getFinprodId());
                repAssetDept.setFinprodMarketId(finProduct.getFinprodMarketId());
                /* FXZG-6 【阜新银行】REP_ASSET_DEPT部分字段未存值 start */
                repAssetDept.setRepCcy(finProduct.getCcy());
                /* FXZG-6 【阜新银行】REP_ASSET_DEPT部分字段未存值 end */
                repAssetDept.setRepFinprodName(dto.getFinprodName());
                BeanUtil.copyPropertiesIgnoreNull(repAssetDept, bussRepAssetMngProdInfoDto.getRepAssetDept());
                dto.setRepAssetDept(repAssetDept);
                RepAssetMngPlan repAssetMngPlan = new RepAssetMngPlan();
                repAssetMngPlan.setRepFinprodId(finProduct.getFinprodId());
                BeanUtil.copyPropertiesIgnoreNull(repAssetMngPlan, bussRepAssetMngProdInfoDto.getRepAssetMngPlan());
                repAssetMngPlan.setRepManagerId(dto.getManager());
                repAssetMngPlan.setRepTrusteeId(dto.getTrusteeId());
                repAssetMngPlan.setRepSetDate(dto.getVdate());
                repAssetMngPlan.setRepEndDate(dto.getMdate());
                dto.setRepAssetMngPlan(repAssetMngPlan);
                // 中债其它要素存值by赵东东 2019.10.10 start
                RepAssetOther repAssetOther = new RepAssetOther();
                repAssetOther.setRepFinprodId(finProduct.getFinprodId());
                //BeanUtil.copyPropertiesIgnoreNull(repAssetOther, bussRepOutInvestAgrInfoDto.getRepAssetOther());
                // add by zcw TISHKS-3 计划净值型资产新建，审核时报错。 start
                if(dto.getBussRepAssetMngProdInfo() != null){
                    repAssetOther.setRepPrivateprdFlag(dto.getBussRepAssetMngProdInfo().getRepPrivateprdFlag());
                }
                // add by zcw TISHKS-3 计划净值型资产新建，审核时报错。 end
                dto.setRepAssetOther(repAssetOther);
                // 中债其它要素存值by赵东东 2019.10.10 end
            }
        }
        else if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_银行.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_证券公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他.getValue())){
            if(null!=bussRepAssetMngProdInfoService) {
                BussRepOutInvestAgrInfoDto bussRepOutInvestAgrInfoDto = bussRepOutInvestAgrInfoService.
                        getBussRepOutInvestAgrInfoDto(dto.getBussRepOutInvestAgrInfo());
                RepAssetDept repAssetDept = new RepAssetDept();
                repAssetDept.setRepFinprodId(finProduct.getFinprodId());
                repAssetDept.setFinprodMarketId(finProduct.getFinprodMarketId());
                repAssetDept.setRepFinprodName(dto.getFinprodName());
                //币种
                BeanUtil.copyPropertiesIgnoreNull(repAssetDept, bussRepOutInvestAgrInfoDto.getRepAssetDept());
                dto.setRepAssetDept(repAssetDept);
                RepTrustInvestMode repTrustInvestMode = new RepTrustInvestMode();
                repTrustInvestMode.setRepFinprodId(finProduct.getFinprodId());
                /* 资产要素登记-辽阳   bug 号：2.0.0.1TWO-747  委外投资-协议方式报送信息表取管理人 20191029  start */
                repTrustInvestMode.setRepActualManager(finPlanNetAssetService.getName(dto.getManager()));
                /* 资产要素登记-辽阳   bug 号：2.0.0.1TWO-747  委外投资-协议方式报送信息表取管理人 20191029  end */
                repTrustInvestMode.setRepCustodian(finPlanNetAssetService.getName(dto.getTrusteeId()));
                BeanUtil.copyPropertiesIgnoreNull(repTrustInvestMode, bussRepOutInvestAgrInfoDto.getRepTrustInvestMode());
                repTrustInvestMode.setRepInvestStartdate(dto.getVdate());
                repTrustInvestMode.setRepInvestEnddate(dto.getMdate());
                dto.setRepTrustInvestMode(repTrustInvestMode);
                // 中债其它要素存值by赵东东 2019.10.10 start
                RepAssetOther repAssetOther = new RepAssetOther();
                repAssetOther.setRepFinprodId(finProduct.getFinprodId());
                //BeanUtil.copyPropertiesIgnoreNull(repAssetOther, bussRepOutInvestAgrInfoDto.getRepAssetOther());
                repAssetOther.setRepPrivateprdFlag(dto.getBussRepOutInvestAgrInfo().getRepPrivateprdFlag());
                dto.setRepAssetOther(repAssetOther);
                // 中债其它要素存值by赵东东 2019.10.10 end
            }
           
        }
        //组合表     wangpeng 2019.7.16  新建组合，具体是否入库  后台判断
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

        //add by lpf 20220517 start 委外组合塞PA表用于组合关联指标 start
        PaPtlPortfolio paPtlPortfolio = new PaPtlPortfolio();
        paPtlPortfolio.setPortfolioId(ptlPortfolio.getPortfolioId());
        paPtlPortfolio.setInvestNature(finProductAdd.getInvestNature());
        // modify by swwan TISHKS-1483 start
        /*paPtlPortfolio.setOpenType("02");
        paPtlPortfolio.setRaiseType("2");*/
        // modify by swwan TISHKS-1483 end
        dto.setPaPtlPortfolio(paPtlPortfolio);
        //add by lpf 20220517 start 委外组合塞PA表用于组合关联指标 end

        return dto;
    }


    /**
     * 查询画面查询方法
     * 
     * @param map 画面参数
     * @return 调用微服务
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<FinPlanNetAssetDto> selectForPage(Map<String, Object> map){
        Page<FinPlanNetAssetDto> page = restTemplate.postForPage(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinPlanNetAsset/findPage",
            map, FinPlanNetAssetDto.class);
        return page;
    }
    
    /**
     * 导出
     */
    @Override
    protected void buildExportParam(ExportParam exportParam) {
		String isShowType = (String) exportParam.getSelectParam().get("isShowType");
		if (StringUtil.equals(isShowType, "01")) {
			exportParam.config(
	                "/com/joyintech/tams/invest/asset/plan/planNet/conf/export_conf.xml").dataSource(
	                DataSourceType.MASERT);
		}else {
			exportParam.config(
	                "/com/joyintech/tams/invest/asset/plan/planNet/conf/export_conf002.xml").dataSource(
	                DataSourceType.MASERT);
		}
    }
    /**
     * 设置日期格式
     * {@inheritDoc}
     */
    @Override
    public void setCellValue(String beanName, Cell cell, String propName, Object value) {
        if(("vdate".equals(propName)) && value != null){
            cell.setCellValue(DateUtil.format((Date)value));
        }
        if(("mdate".equals(propName)) && value != null){
            cell.setCellValue(DateUtil.format((Date)value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<FinPlanNetAssetDto> selectPageData(Map<String, Object> selectParam) {
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
    public List<SelectOptionDto> getCmsId(Map<String,Object> map) {
        return restTemplate.postForList(ServiceApplication.ASSET_SERVICE_NAME
                        + "/FinPlanNetAsset/getCmsId" , map,
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
    public FinPlanNetAssetDto getChange(String cmsId) {
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
                        + "/FinPlanNetAsset/getChange?cmsId=" + cmsId,
                FinPlanNetAssetDto.class);
    }

    /**
     * 管理人的联想下拉框事件
     * 
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getIssuer(Map<String,Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
            + "/CommonSelect/getIssuer", map,
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
    public List<SelectOptionDto> getTrusteeId(Map<String,Object> map) {
        return restTemplate.postForList(ServiceApplication.ASSET_SERVICE_NAME
            + "/FinPlanNetAsset/getTrusteeId", map,
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
    public List<SelectOptionDto> getCalendarId(Map<String,Object> map) {
        return restTemplate.postForList(ServiceApplication.ASSET_SERVICE_NAME
            + "/FinPlanNetAsset/getCalendarId" , map,
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
    public FinPlanNetAssetDto getBasicInfoByFinprodId(String finprodId){
        return restTemplate.postForObject(ServiceApplication.ASSET_SERVICE_NAME
                        + "/FinPlanNetAsset/getBasicInfoByFinprodId" ,finprodId,
                FinPlanNetAssetDto.class);
    }

    /**
     * 查询市场代码
     *
     * @param finprodMarketId 市场代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Integer selectFinprodMarketId(String finprodMarketId){
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME +
                        "/FinPlanNetAsset/selectFinprodMarketId?finprodMarketId="+ finprodMarketId,Integer.class);
    }

    /**
     * 查询客户名称
     *
     * @param custormId 客户代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public String getName(String custormId){
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME +
                "/FinPlanNetAsset/getName?custormId="+ custormId,String.class);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getManager(Map<String, Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
            + "/CommonSelect/getManager", map,
            SelectOptionDto.class);
    }


    /**
     * 实际管理人的联想下拉框事件
     * @param map 输入的参数
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getActualManager(Map<String,Object> map) {
        return restTemplate.postForPage(ServiceApplication.COMMON_SERVICE_NAME
                        + "/CommonSelect/getActualManager", map,
                SelectOptionDto.class);
    }
    
    /**
     * 查询资产代码
     *
     * @param finprodMarketId 资产代码
     * @return 调用微服务
     * @see
     * @since 1.0
     */
    public String getFinprodId(String finprodMarketId){
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME +
                "/FinPlanNetAsset/getFinprodId?finprodMarketId="+ finprodMarketId,String.class);
    }
    // 增加查询资产代码 by 赵东东 2019.10.12 end

    /**
     *资管计划-净值型 接口数据入库
     * @param id
     * @param msg
     */
    public void executeInterf(String id,String msg) {
        System.out.println("##############################资管计划-净值型 接口数据入库start#############################");
        FinPlanNetAssetDto entity = new FinPlanNetAssetDto();
        try {
            if(StringUtil.isNotEmpty(msg)) {
                JSONObject msgJSON = JSONObject.parseObject(msg);
                entity.setTrialNo(msgJSON.getString("TRIAL_NO")); // 法审编号
                entity.setContractNo(msgJSON.getString("CONTRACT_NO")); // 合同编号
                entity.setVdate(msgJSON.getDate("VALUE_DATE")); // 起息日
                entity.setMdate(msgJSON.getDate("MDATE")); // 到期日
                entity.setIdate(msgJSON.getDate("ESTAB_DATE")); // 发行日
                entity.setpFinprodId(msgJSON.getString("MOTHER_ASSET_CODE")); // 母资产代码
                entity.setWarnLine(msgJSON.getString("WARN_LINE")); // 预警线
                entity.setFlatLine(msgJSON.getString("FLAT_LINE")); // 止损线
                entity.setBaseRule(msgJSON.getString("BASE_RULE")); // 业绩基准
                entity.setOverallocation(msgJSON.getString("OVERALLOCATION")); // 超额分成方式
                entity.setCreditEnhanceMeasure(msgJSON.getString("CREDIT_MEASURES")); // 增信措施
                entity.setManager(msgJSON.getString("MANAGER")); // 管理人
                entity.setActualManager(msgJSON.getString("ACTUAL_MANAGER")); // 实际管理人
                entity.setRepAssetType(msgJSON.getString("SUB_CODE")); // 资产/负债类别

                // 标的类金融产品
                FinProduct finProduct = new FinProduct();
                finProduct.setFinprodId(msgJSON.getString("ASSET_CODE")); // 资产代码
                finProduct.setFinprodAbbr(msgJSON.getString("ASSET_SNAME")); // 资产代码简称
                finProduct.setFinprodName(msgJSON.getString("ASSET_NAME")); // 资产代码全称
                finProduct.setpFinprodId(msgJSON.getString("MOTHER_ASSET_CODE")); // 母资产代码
                finProduct.setProfitType(msgJSON.getString("PROFIT_TYPE")); // 收益类型
                finProduct.setIssueAmt(msgJSON.getBigDecimal("ISSUE_AMT")); // 发行规模
                finProduct.setCcy(msgJSON.getString("CURR_TYPE")); // 币种
                finProduct.setVdate(msgJSON.getDate("VALUE_DATE")); // 起息日
                finProduct.setMdate(msgJSON.getDate("MDATE")); // 到期日
                finProduct.setIdate(msgJSON.getDate("ESTAB_DATE")); // 发行日
                finProduct.setIssuer(msgJSON.getString("PUBLISHER_CODE")); // 发行人
                finProduct.setTrusteeId(msgJSON.getString("TRUSTEE_CODE")); // 托管人
                finProduct.setIssueType(msgJSON.getString("IPO_WAY")); // 募集方式
                finProduct.setManager(msgJSON.getString("MANAGER")); // 管理人
                finProduct.setCalendarId(msgJSON.getString("CALENDAR_ID")); // 交易日历
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

                // 委外投资-协议方式报送信息
                RepTrustInvestMode repTrustInvestMode = new RepTrustInvestMode();
                repTrustInvestMode.setRepFinprodId(msgJSON.getString("ASSET_CODE")); // 资产代码
                repTrustInvestMode.setRepContainExpectedYield(msgJSON.getString("IS_EXITS_YIELD")); // 是否有预期收益率
                repTrustInvestMode.setRepMaxExpectedYield(msgJSON.getBigDecimal("HIGH_RATE")); // 预期最高收益率%
                repTrustInvestMode.setRepMinExpectedYield(msgJSON.getBigDecimal("LOW_RATE")); // 预期最低收益率%
                entity.setRepTrustInvestMode(repTrustInvestMode);

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
