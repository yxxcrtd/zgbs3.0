package com.joyintech.tams.business.asset.protect.service;

import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.protect.dao.CreditProtectionCertificateMapper;
import com.joyintech.tams.business.asset.protect.dao.InterfCreditInfoMapper;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificate002Dto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinProductAddDto;
import com.joyintech.tams.business.asset.tenderbond.service.FinTenderBondAssetService;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.systemtools.dao.JoyinFSMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 信用保护凭证标的
 *
 * @author wsz
 * @version 1.0 2022年5月11日
 * @see FinTenderBondAssetService
 * @since 1.0
 */
@Service
public class CreditProtectionCertificate002Service extends BaseService<CreditProtectionCertificate002Dto> implements BaseProcessService<CreditProtectionCertificate002Dto> {

    @Autowired(required = false)
    private CreditProtectionCertificateMapper creditProtectionCertificateMapper;

    @Autowired(required = false)
    private InterfCreditInfoMapper interfCreditInfoMapper;
    @Resource
    private FinProductMapper finProductMapper;
    @Resource
    private FinProductAddMapper finProductAddMapper;
    @Resource
    private FinProductTypeMapper finProductTypeMapper;
    /**
     * 现金流信息表
     */
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;
    /**
     * 现金流计算规则表
     */
    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;
    /**
     * 现金流计划
     */
    @Autowired
    private  FinCashPlanMapper finCashPlanMapper;
    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private JoyinFSMapper joyinFSMapper;

    /**
     * 保存操作类型
     *
     * @param dto
     * @param operType
     */
    public void saveOperate(CreditProtectionCertificate002Dto dto, DictCodeListExt3 operType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        //add wsz 修改 ZGCPCN-1333 start
        oprHisReq.setEntrustId(dto.getFinprodMarketId());
        //add wsz 修改 ZGCPCN-1333 end
        oprHisReq.setOperType(operType.getValue());
        oprHisReq.setBusiDes(dto.getRemark());

        // modify by yangxunpeng ZGCPCN-1352 start
        oprHisReq.setUserId(dto.getUpdateUser());
        oprHisReq.setCreateUser(dto.getCreateUser());
        oprHisReq.setCreateDept(dto.getCreateDept());
        oprHisReq.setUpdateUser(dto.getUpdateUser());
        // modify by yangxunpeng ZGCPCN-1352 end

        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

    @Override
    public void doComplete(CreditProtectionCertificate002Dto dto, OperationType type) {
        if (OperationType.DELETE == type) {
            this.delete(dto);
            this.saveOperate(dto, DictCodeListExt3.操作类型_放弃);
        } else if (OperationType.NEW == type && "auto".equals(dto.getOperateType2())) {
            this.insert(dto);
            this.saveOperate(dto, DictCodeListExt3.操作类型_自动加券);
        } else if (OperationType.NEW == type && StringUtil.isEmpty(dto.getOperateType2())) {
            this.insert(dto);
            this.saveOperate(dto, DictCodeListExt3.操作类型_新建);
        } else if (OperationType.MODIFY == type && "cover".equals(dto.getOperateType2())) {
            this.update(dto);
            this.saveOperate(dto, DictCodeListExt3.操作类型_资讯数据覆盖);
        } else if (OperationType.MODIFY == type && StringUtil.isEmpty(dto.getOperateType2())) {
            this.update(dto);
            this.saveOperate(dto, DictCodeListExt3.操作类型_修改);
        }
    }

    /**
     * 新增
     *
     * @param dto
     */
//    @Transactional
    public void insert(CreditProtectionCertificate002Dto dto) {
        if (StringUtil.isEmpty(dto.getCalendarId())) {//根据交易场所固定交易日历
            if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                dto.setCalendarId("04");
            }else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                dto.setCalendarId("05");
            }
        }
        //1.保存金融产品标的表
        //金融产品类型_资产代码_增发批次_交易场所进行拼接
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        dto.setFinprodType(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        dto.setFinprodId(getFinprodId(dto));
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        finProductMapper.insert(finProduct);
        //2.保存金融产品标的附属表
        FinProductAdd productAdd = new FinProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(productAdd, dto);
        productAdd.setFinprodId(dto.getFinprodId());
        BigDecimal cash = StringUtil.isNotEmpty(dto.getNomCapi())? dto.getNomCapi().multiply(new BigDecimal(10000)):new BigDecimal(0.00);
        productAdd.setNomCapi(cash);
        productAdd.setBasis(dto.getBasis());
        finProductAddMapper.insert(productAdd);
        //3.保存融产品分类表
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, dto);
        finProductType.setBranch(0l);
        finProductType.setType6(DictCodeList.信用保护凭证类型_信用保护凭证.getValue());
        finProductType.setType7(DictCodeList.信用保护凭证类型_信用保护凭证.getValue());
        finProductType.setType8(DictCodeList.平安资产类型_信用保护凭证.getValue());
        finProductTypeMapper.insert(finProductType);
//        //新增现金流信息
//        String cashId = CommonUtil.generateCashFlowCode(dto.getFinprodId(),
//                DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
//                DictCodeListExt1.现金流二级类型_本金.getValue());
//        FinCashInfo finCashInfo = new FinCashInfo();
//        finCashInfo.setCashId(cashId);
//        finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
//        finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
//        finCashInfo.setFinprodId(dto.getFinprodId());
//        finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
//        finCashInfo.setBranch(0l);
//        BeanUtil.copyPropertiesIgnoreNull(finCashInfo, dto);
//        finCashInfoMapper.insert(finCashInfo);
//        //添加现金流计算规则
//        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
//        BeanUtil.copyPropertiesIgnoreNull(finCashCalcRule, dto);
//        finCashCalcRule.setCashId(cashId);
//        finCashCalcRule.setEffDate(dto.getVdate());
//        finCashCalcRule.setEffDate(dto.getVdate());
//        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
//        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
//        finCashCalcRule.setBasis(dto.getBasis());
//        finCashCalcRule.setFinprodId(dto.getFinprodId());
//        finCashCalcRule.setBranch(0l);
//        finCashCalcRuleMapper.insert(finCashCalcRule);
//        //添加资金流计划
//        FinCashPlan finCashPlan = new FinCashPlan();
//        finCashPlan.setCashId(cashId);
//        finCashPlan.setCashNum(0l);
//        finCashPlan.setCashType(DictCodeList.现金流类型_到期还本.getValue());
//        finCashPlan.setVdateUnadjust(dto.getVdate());
//        finCashPlan.setMdateUnadjust(dto.getMdate());
//        finCashPlan.setPayDateUnadjust(dto.getMdate());
//        finCashPlan.setVdate(dto.getVdate());
//        finCashPlan.setMdate(dto.getMdate());
//        finCashPlan.setPayDate(dto.getMdate());
//        Long day = DateUtil.getBetweenDayNumber(DateUtil.format(dto.getVdate()),DateUtil.format(dto.getMdate()));
//        finCashPlan.setTermdays(day);
//        finCashPlan.setVdateTerm(dto.getVdate());
//        finCashPlan.setMdateTerm(dto.getMdate());
//        finCashPlan.setIntPrinAmt(cash);
//        finCashPlan.setCashBaseamt(cash);
//        finCashPlan.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
//        finCashPlan.setFinprodId(dto.getFinprodId());
//        finCashPlan.setFinprodType(dto.getFinprodType());
//        finCashPlan.setFinprodType2(dto.getFinprodType2());
//        finCashPlan.setPayType(DictCodeList.支付方式_现金.getValue());
//        finCashPlan.setBranch(0l);
//        finCashPlan.setBasis(dto.getBasis());
//        finCashPlanMapper.insert(finCashPlan);
        //附件
        MstFileInputParameter mstFileInputParameter = dto.getMstFileInputParameter();
        if (StringUtil.isNotEmpty(mstFileInputParameter)
                && CollectionUtils.isNotEmpty(mstFileInputParameter.getFileList())){
            for (SysUploadFileInfo fileInfo:mstFileInputParameter.getFileList()){
                fileInfo.setBusinessId(dto.getFinprodId());
                creditProtectionCertificateMapper.updateFileByPrimaryKey(fileInfo);
            }
        }
    }

    /**
     * 删除
     *
     * @param dto
     */
    public void delete(CreditProtectionCertificate002Dto dto) {
        //1.删除融产品分类表
        //add wsz 修改 ZGCPCN-1333 start
        String finprodId = this.getFinprodId(dto.getFinprodMarketId());
        FinProductTypeKey typeKey = new FinProductTypeKey();
        typeKey.setBranch(0l);
        typeKey.setFinprodId(finprodId);
        finProductTypeMapper.deleteByPrimaryKey(typeKey);
        //3.删除金融产品标的附属表
        finProductAddMapper.deleteByPrimaryKey(finprodId);
        //4.删除金融产品标的表
        finProductMapper.deleteByPrimaryKey(finprodId);
        //add wsz 修改 ZGCPCN-1333 end
//        //5.删除现金流信息
//        String cashId = CommonUtil.generateCashFlowCode(dto.getFinprodId(),
//                DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
//                DictCodeListExt1.现金流二级类型_本金.getValue());
//        finCashInfoMapper.deleteByPrimaryKey(cashId);
//        //6.删除现金流计算规则
//        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
//        finCashCalcRule.setCashId(cashId);
//        finCashCalcRule.setEffDate(dto.getVdate());
//        finCashCalcRuleMapper.deleteByPrimaryKey(finCashCalcRule);
//        //7.删除资金流计划
//        FinCashPlan finCashPlan = new FinCashPlan();
//        finCashPlan.setCashId(cashId);
//        finCashPlan.setMdate(dto.getMdate());
//        finCashPlanMapper.deleteByPrimaryKey(finCashPlan);
    }

    /**
     * 修改
     *
     * @param dto
     */
    public void update(CreditProtectionCertificate002Dto dto) {
        //采用先删除在插入的方式处理
        this.delete(dto);
        this.insert(dto);
    }

    public SysUploadFileInfo getUploadFileInfo(MultipartFile file) throws IOException {
        if (!StringUtil.isNotEmpty(file.getOriginalFilename())){
            return null;
        }
        List<String> fileNameList = StringUtil.splitToList(".",file.getOriginalFilename());
        String newfileName = fileNameList.get(0)+"_"+ com.joyintech.framework.util.DateUtil.format(com.joyintech.framework.util.DateUtil.now(), com.joyintech.framework.util.DateUtil.DATETIME_FORMAT_WITH_SSS_YEAR1)+"."+fileNameList.get(1);
        // 本地路径
        String localPath = CacheUtil.getSysParam("upload_file_path") + File.separator;
        String nowDateStr = com.joyintech.framework.util.DateUtil.format(com.joyintech.framework.util.DateUtil.now(), com.joyintech.framework.util.DateUtil.DEFAULT_DATE_FORMAT_EN);
        List<String> nowDateList = StringUtil.splitToList("-",nowDateStr);
        String newPath = localPath + nowDateList.get(0) + File.separator
                + nowDateList.get(1)  + File.separator + nowDateList.get(2)+ File.separator;
        File newPathFile = new SafelyFile(newPath);
        //  生成文件夹
        if (!newPathFile.exists()){
            newPathFile.mkdir();
        }
        File folder = new SafelyFile(newPathFile,newfileName);
        //修改上传文件
        if (!folder.getParentFile().exists())
            folder.getParentFile().mkdirs();
        if (!folder.exists())
            folder.createNewFile();
        FileUtils.copyInputStreamToFile(file.getInputStream(),folder);
        SysUploadFileInfo fileInfo = new SysUploadFileInfo();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        fileInfo.setSeqNo(uuid);
        fileInfo.setFileName(file.getOriginalFilename());
        if (isWindows()){
            newPath = newPath.replaceAll("/","\\\\");
        }
        fileInfo.setPath(newPath+newfileName);
        creditProtectionCertificateMapper.insertFileSelective(fileInfo);
        return fileInfo;
    }

    public boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }

    public AjaxResult delFileInfo(SysUploadFileInfo fileInfo) throws IOException {
        AjaxResult ajaxResult = new AjaxResult();
        if (!StringUtil.isNotEmpty(fileInfo.getSeqNo())){
            return null;
        }
        SysUploadFileInfo file = joyinFSMapper.selectByPrimaryKey(fileInfo.getSeqNo());
        File pathFile = new SafelyFile(file.getPath());
        //  生成文件夹
        if (pathFile.exists()){
            pathFile.delete();
        }
        joyinFSMapper.deleteByPrimaryKey(fileInfo.getSeqNo());
        ajaxResult.setMessage("删除成功");
        return ajaxResult;
    }

    /**
     * 加券
     *
     * @param finprodMarketId
     * @return
     */
    public CreditProtectionCertificate002Dto addCoupons(String finprodMarketId) {
//        CreditProtectionCertificate002Dto dto = new CreditProtectionCertificate002Dto();
        Map<String, String> param = new HashMap<>();
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("finprodMarketId", finprodMarketId);
//        String finprodId = creditProtectionCertificateMapper.selectFinprodId(param);
//        if (StringUtil.isNotEmpty(finprodId)) {
//            return getById(finprodId);
//        } else {//从接口中获取
        CreditProtectionCertificate002Dto dto = interfCreditInfoMapper.getFromInterface(param);
        if(Objects.isNull(dto)){
            return new CreditProtectionCertificate002Dto();
        }
            if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                dto.setCalendarId("04");
            }else if(StringUtil.equals(dto.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                dto.setCalendarId("05");
            }
            return dto;
//        }
    }

    /**
     * 通过主键查询列表数据
     *
     * @param finprodId
     * @return
     */
    public CreditProtectionCertificate002Dto getById(String finprodId) {
        //1.查金融产品标的表
        CreditProtectionCertificate002Dto entity = new CreditProtectionCertificate002Dto();
        Map<String,Object> param = new HashMap<>();
        param.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        //add wsz 修改bug ZGCPCN-1211 20220610 【信用保护凭证标的】信用保护凭证指令资产代码输入资产代码，点击查看，无法查看标的详情 start
        if(!Objects.isNull(finprodId) && !finprodId.contains(DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue())){
            param.put("finprodMarketId", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue()+"_"+finprodId);
        } else {
            param.put("finprodMarketId", finprodId);
        }
        //add wsz 修改bug ZGCPCN-1211 20220610 【信用保护凭证标的】信用保护凭证指令资产代码输入资产代码，点击查看，无法查看标的详情 end
        entity = creditProtectionCertificateMapper.getByFinProdId(param);
        if(Objects.isNull(entity)){
            return null;
        }
        BigDecimal cash = StringUtil.isNotEmpty(entity.getNomCapi())? entity.getNomCapi().divide(new BigDecimal(10000)):new BigDecimal(0.00);
        entity.setNomCapi(cash);
        List<SysUploadFileInfo> uploadInfo = joyinFSMapper.getUploadInfo(entity.getFinprodId());
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(uploadInfo);
        mstFileInputParameter.setBussinessId(entity.getFinprodId());
        entity.setMstFileInputParameter(mstFileInputParameter);
        //查询参考债券
        if(StringUtils.isNotEmpty(entity.getRefSec())){
            Map<String,Object> params = new HashMap<>();
            params.put("finprodType2", DictCodeListExt1.金融产品类型_债券.getValue());
            params.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            params.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            params.put("refList", StringUtil.splitToList(null, entity.getRefSec()));
            List<String> refList = creditProtectionCertificateMapper.getRefList(params);
            entity.setRefSecList(refList.stream().collect(Collectors.joining(",")));
        }
        return entity;
    }

    public CreditProtectionCertificate002Dto getProcessEntity(CreditProtectionCertificate002Dto entity){
        //add wsz 修改现实问题 ZGCPCN-1356 start
        CreditProtectionCertificate002Dto entity2 = null;
        if(StringUtil.isNotEmpty(entity.getFinprodId())) {
            entity2 = this.getById(entity.getFinprodId());
        }
        if(!Objects.isNull(entity2)){
            BeanUtil.copyPropertiesIgnoreNull(entity, entity2);
        } else {
            entity.setRefSecList(this.getRefList(entity));
            entity.setRefEntityStr(this.getMstCustomerInfo(entity.getRefEntity()));
            entity.setIssuershortStr(this.getMstCustomerInfo(entity.getIssuershort()));
            List<SysUploadFileInfo> uploadInfo = joyinFSMapper.getUploadInfo(entity.getFinprodId());
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(uploadInfo);
            mstFileInputParameter.setBussinessId(entity.getFinprodId());
            entity.setMstFileInputParameter(mstFileInputParameter);
        }
        //add wsz 修改现实问题 ZGCPCN-1356 end
        return entity;
    }

    public String getRefList(CreditProtectionCertificate002Dto entity){
        if(!StringUtils.isNotEmpty(entity.getRefSec())){
            return null;
        }
        Map<String,Object> params = new HashMap<>();
        params.put("finprodType2", DictCodeListExt1.金融产品类型_债券.getValue());
        params.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        params.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        params.put("refList", StringUtil.splitToList(null, entity.getRefSec()));
        List<String> refList = creditProtectionCertificateMapper.getRefList(params);
        return refList.stream().collect(Collectors.joining(","));
    }

    public String getMstCustomerInfo(String customerId){
        if(!StringUtils.isNotEmpty(customerId)){
            return null;
        }
        Map<String,Object> params = new HashMap<>();
        params.put("customerId", customerId);
        return creditProtectionCertificateMapper.getMstCustomerInfo(params);
    }

    @Override
    public void doInsert(CreditProtectionCertificate002Dto entity, OperationType type) {

    }

    @Override
    public void doUpdate(CreditProtectionCertificate002Dto entity) {

    }

    @Override
    public void doDelete(String workflowId) {

    }

    @Override
    public void doBack(CreditProtectionCertificate002Dto entity) {

    }

    public String getFinprodId(String finprodId){
        if(!Objects.isNull(finprodId)&&!finprodId.contains(DictCodeList.信用保护凭证类型_信用保护凭证.getValue())){
            return DictCodeList.信用保护凭证类型_信用保护凭证.getValue()+"_"+ finprodId;
        }
        return finprodId;
    }


    /**
     * 保存、修改提交校验
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(CreditProtectionCertificate002Dto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (StringUtil.isEmpty(dto.getWorkflowId()) &&  isInProcess(dto.getFinprodMarketId(), "CreditProtectionCertificate002")) {
            validList.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.isEmpty(operateType)) {
            validList.add(new ValidMessage("操作类型不能为空"));
        }
        if (StringUtil.equals(operateType, OperationType.NEW.name())) {
            this.emptyCheck(dto, validList);
            if(StringUtil.isNotEmpty(dto.getFinprodMarketId()) && dto.getFinprodMarketId().length() > 20) {
                validList.add(new ValidMessage("资产代码长度不能超过20"));
            }
            if(StringUtil.isNotEmpty(dto.getFinprodMarketId()) && !dto.getFinprodMarketId().matches("^[A-Za-z0-9]+.[A-Za-z0-9]+$")) {
                validList.add(new ValidMessage("资产代码只能由数字和字母组成"));
            }
            if (StringUtil.isNotEmpty(dto.getFinprodMarketId()) && StringUtil.isNotEmpty(dto.getTradeMarket())) {
                Map<String, String> param = new HashMap<>();
                param.put("finprodMarketId", dto.getFinprodMarketId());
                if(StringUtil.isNotEmpty(creditProtectionCertificateMapper.selectFinprodId(param))) {
                    validList.add(new ValidMessage("资产代码已存在"));
                }
                String finprodId = getFinprodId(dto);
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
                if (Objects.nonNull(finProduct)) { //同一资产代码只能存在一条数据
                    validList.add(new ValidMessage("同一资产代码只能存在一条数据"));
                }
            }
        }
        if (StringUtil.equals(operateType, "submit")) {
            this.emptyCheck(dto, validList);
            if (StringUtil.isNotEmpty(dto.getFinprodMarketId()) && StringUtil.isNotEmpty(dto.getTradeMarket())) {
                Map<String, String> param = new HashMap<>();
                param.put("finprodMarketId", dto.getFinprodMarketId());
                if(StringUtil.isNotEmpty(creditProtectionCertificateMapper.selectFinprodId(param))) {
                    validList.add(new ValidMessage("资产代码已存在"));
                }
                String finprodId = getFinprodId(dto);
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
                if (Objects.nonNull(finProduct)) { //同一资产代码只能存在一条数据
                    validList.add(new ValidMessage("资产代码已存在"));
                }
            }
        }
        if (StringUtil.equals(operateType, OperationType.MODIFY.name())) {
            this.emptyCheck(dto, validList);
            //修改时以及流程中修改数据审批时，需要判断当前资产是否存在有效或流程中的待执行指令及交易数据，如存在，则不允许修改；
            Integer count = creditProtectionCertificateMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
            if (StringUtil.isNotEmpty(dto.getFinprodMarketId()) && StringUtil.isNotEmpty(dto.getTradeMarket())) {
                Map<String, String> param = new HashMap<>();
                param.put("finprodMarketId", dto.getFinprodMarketId());
                if(StringUtil.isNotEmpty(creditProtectionCertificateMapper.selectFinprodId(param))) {
                    validList.add(new ValidMessage("资产代码已存在"));
                }
                String finprodId = getFinprodId(dto);
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
                if (Objects.nonNull(finProduct)) { //同一资产代码只能存在一条数据
                    validList.add(new ValidMessage("同一资产代码只能存在一条数据"));
                }
            }
        }
        return validList;
    }

    /**
     * 修改或者删除校验
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> modifyAndDeleteCheck(CreditProtectionCertificateDto dto) {
        List<ValidMessage> validList = new ArrayList<>();
        String operateType = dto.getOperateType();
        if (isInProcess(dto.getFinprodMarketId(), "CreditProtectionCertificate002")) {
            validList.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.equals(operateType, OperationType.MODIFY.name())) {
            //修改时以及流程中修改数据审批时，需要判断当前资产是否存在有效或流程中的待执行指令及交易数据，如存在，则不允许修改；
            Integer count = creditProtectionCertificateMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        if (StringUtil.equals(operateType, OperationType.DELETE.name())) {
            //删除时，需要判断当前资产是否存在指令及交易数据，如存在，则不允许删除；
            Integer count = creditProtectionCertificateMapper.countFinprodIdUse(dto.getFinprodId(), null,null);
            if (Objects.nonNull(count) && count > 0) {
                validList.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        }
        return validList;
    }

    /**
     * 构造金融标的代码
     *
     * @param dto
     * @return
     */
    public String getFinprodId(CreditProtectionCertificate002Dto dto) {
        if(!dto.getFinprodMarketId().contains(".")){
            return String.format("%s_%s%s", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue(),dto.getFinprodMarketId(),dto.getTradeMarketId());
        }
        return String.format("%s_%s", DictCodeListExt1.金融产品类型_投管用_信用保护凭证.getValue(),dto.getFinprodMarketId());
    }

    /**
     * 判断是否在流程中
     *
     * @param pk1
     * @return
     */
    public Boolean isInProcess(String pk1, String... pageId) {
        if (StringUtil.isEmpty(pk1)) return Boolean.FALSE;
        WflValidInputParameter wflValidInputParameter = null;
        for (String str : pageId) {
            wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId(str);
            wflValidInputParameter.setBusinessDataPk1(pk1);
            wflValidInputParameter.setSeachMode("01");
            List<String> result = creditProtectionCertificateMapper.getBusinessData(wflValidInputParameter);
            if (CollectionUtils.isNotEmpty(result)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }


    /**
     * 空检查
     *
     * @param dto
     * @param validList
     */
    private void emptyCheck(CreditProtectionCertificate002Dto dto, List<ValidMessage> validList) {
        ValidUtils.valid(dto, validList);
    }

}
