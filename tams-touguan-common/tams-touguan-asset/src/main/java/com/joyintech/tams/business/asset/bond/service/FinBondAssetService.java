package com.joyintech.tams.business.asset.bond.service;

import com.github.pagehelper.PageInfo;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dao.FinBondAssetMapper;
import com.joyintech.tams.business.asset.bond.dto.*;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.bond.dto.BusiFinExercisePlanDto;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstFileCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.asset.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.systemtools.dao.JoyinFSMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 债券标的信息新建
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAssetService
 * @since 1.0
 */
@Service
public class FinBondAssetService extends BaseService<FinBondAssetSaveRequest> implements BaseProcessService<FinBondAssetSaveRequest>{

    @Autowired
    private CommonAutoCouponService commonAutoService;

    /**
     * 投管接口共通组件
     */
    @Autowired
    private InvestInterfCommService investInterfCommService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 共通--资产
     */
    @Autowired
    private AssetCommonService assetCommonService;

    /**
     * 业务--行权计划
     */
    @Autowired(required = false)
    private BusiFinExercisePlanService busiFinExercisePlanService;

    /**
     * 附件
     */
    @Autowired(required = false)
    private MstFileCommonService fileCommonService;
    /**
     * 产品评级共通
     */
    @Autowired(required = false)
    private BusiProductRatingService busiProductRatingService;

    // 债券新建新增快捷录入功能 add by renping 20191010 start
    /**
     * FileAttachmentProcess
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    // 债券新建新增快捷录入功能 add by renping 20191010 end

    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    @Autowired(required = false)
    private FinProductAddMapper finProductAddMapper;

    @Autowired(required = false)
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired(required = false)
    private FinBondAssetMapper finBondAssetMapper;

    @Autowired(required = false)
    private FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired(required = false)
    private FinCashInfoMapper finCashInfoMapper;

    @Autowired(required = false)
    private FinCashCalcRuleMapper finCashCalcRuleMapper;

    @Autowired(required = false)
    private FinCashPayRuleMapper finCashPayRuleMapper;

    @Autowired(required = false)
    private FinRateInfoMapper finRateInfoMapper;

    @Autowired(required = false)
    private FinCashPlanMapper finCashPlanMapper;

    @Autowired(required = false)
    private FinExercisePlanMapper finExercisePlanMapper;

    /**
     * 基础--中债报送(资产)
     */
    @Autowired(required = false)
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private DateCalService dateCalService;

    @Autowired
    private FinProductIssueMapper finProductIssueMapper;

    @Autowired
    private MstCustomerInfoMapper mstCustomerInfoMapper;

    @Autowired
    private MstCustomerInfoAddMapper mstCustomerInfoAddMapper;

    @Autowired
    private IPlatformDate iplatformDate;

    @Autowired
    private JoyinFSMapper joyinFSMapper;

    public static final String ENTRUST_RECORD_INSERT = "insert";

    public static final String ENTRUST_RECORD_UPDATE = "update";

    public static final String ENTRUST_RECORD_DELETE = "delete";


    /**
     * 工作流（留痕操作结束的时候执行的方法）
     */
    @Override
    public void doComplete(FinBondAssetSaveRequest entity, OperationType type) {
        String action = StringUtil.EMPTY;

        if (OperationType.DELETE.equals(type)) {
            action = ENTRUST_RECORD_DELETE;
            doCompleteDelete(entity);
            //处理履历
            finBondAssetMapper.deleteCfetsOprHis(entity.getFinprodMarketId());
        } else {
            entity = encapBaseFinProductBaseInfoList(entity);
            if (OperationType.NEW.equals(type)) {
                //新建债券类资产时，自动创建交易对手 add by hxy 20200421 start
                assetCommonService.autoCreateTradeCounter(entity.getIssuer());
                //新建债券类资产时，自动创建交易对手 add by hxy 20200421 end
                //从待办中进入的无法判断是否新建还是编辑
                if (selectCountByfinprodId(DictCodeList.平安资产类型_债券.getValue() +
                        "_" + entity.getFinprodMarketId()) > 0) {
                    action = ENTRUST_RECORD_UPDATE;
                    doCompleteUpdate(entity);
                } else {
                    action = ENTRUST_RECORD_INSERT;
                    doCompleteInsert(entity);
                }
            } else {
                action = ENTRUST_RECORD_UPDATE;
                doCompleteUpdate(entity);
            }
            //处理履历
            updateCfetsOprHis(entity.getFinprodMarketId(), action);
        }
        try {
            // 推送数据到第三方接口
            //1.债券要素信息
            Map<String, Object> map =  JsonUtils.object2Map(entity);
            map.put("pageId", "FinBondTargetAsset002S001");
            investInterfCommService.doInvestInterf(map);
            //2.债券利率信息
            map.put("pageId", "FinBondTargetAsset002S002");
            investInterfCommService.doInvestInterf(map);
            //3.债券现金流计划
            map.put("pageId", "FinBondTargetAsset002S003");
            investInterfCommService.doInvestInterf(map);
            //4.债券行权计划
            map.put("pageId", "FinBondTargetAsset002S004");
            investInterfCommService.doInvestInterf(map);
        } catch (Exception e) {
            //功能暂未测试
        }

    }

    private void doCompleteInsert(FinBondAssetSaveRequest entity) {
        for (BaseFinProductBaseInfoDto dto: entity.getBaseFinProductBaseInfoList()){
            //金融产品
            FinProduct finProduct = dto.getFinProduct();
            finProductMapper.insertSelective(finProduct);
            //金融产品附属
            FinProductAdd finProductAdd = dto.getFinProductAdd();
            finProductAddMapper.insertSelective(finProductAdd);
            //金融产品类型
            FinProductType finProductType = dto.getFinProductType();
            finProductTypeMapper.insertSelective(finProductType);
            //现金流信息（FIN_CASH_INFO）
            List<FinCashInfo> finCashInfoList = dto.getFinCashInfoList();
            for(FinCashInfo finCashInfo: finCashInfoList) {
                finCashInfoMapper.insertSelective(finCashInfo);
            }
            //现金流计算规则表（FIN_CASH_CALC_RULE）
            List<FinCashCalcRule> finCashCalcRuleList = dto.getFinCashCalcRuleList();
            for(FinCashCalcRule finCashCalcRule: finCashCalcRuleList) {
                finCashCalcRuleMapper.insertSelective(finCashCalcRule);
            }
            //现金流支付规则表（FIN_CASH_PAY_RULE）
            List<FinCashPayRule> finCashPayRuleList = dto.getFinCashPayRuleList();
            for(FinCashPayRule finCashPayRule: finCashPayRuleList) {
                finCashPayRuleMapper.insertSelective(finCashPayRule);
            }
            //现金流利率信息表（FIN_RATE_INFO）
            List<FinRateInfo> finRateInfoList = dto.getFinRateInfoList();
            for(FinRateInfo finRateInfo: finRateInfoList) {
                finRateInfoMapper.insertSelective(finRateInfo);
            }
            //现金流计划表（FIN_CASH_PLAN）
            List<FinCashPlan> finCashPlanList = dto.getFinCashPlanList();
            for(FinCashPlan finCashPlan: finCashPlanList) {
                finCashPlanMapper.insertSelective(finCashPlan);
            }
            //行权计划表（FIN_EXERCISE_PLAN）
            List<FinExercisePlan> finExercisePlanList = dto.getFinExercisePlanList();
            for(FinExercisePlan finExercisePlan: finExercisePlanList) {
                finExercisePlanMapper.insertSelective(finExercisePlan);
            }

        }
        //转债信息（FIN_PRODUCT_CONVERTIBLE）
        FinProductConvertible FinProductConvertible = entity.getProductConvertible();
        if (StringUtil.isNotEmpty(FinProductConvertible)) {
            finProductConvertibleMapper.insertSelective(FinProductConvertible);
        }
        //附件
        MstFileInputParameter mstFileInputParameter = entity.getMstFileInputParameter();
        if (StringUtil.isNotEmpty(mstFileInputParameter)
                && CollectionUtils.isNotEmpty(mstFileInputParameter.getFileList())){
            for (SysUploadFileInfo fileInfo:mstFileInputParameter.getFileList()){
                fileInfo.setBusinessId(entity.getFinprodId());
                finBondAssetMapper.updateFileByPrimaryKey(fileInfo);
            }
        }
    }

    private void doCompleteUpdate(FinBondAssetSaveRequest entity) {
        for (BaseFinProductBaseInfoDto dto: entity.getBaseFinProductBaseInfoList()){
            //金融产品
            FinProduct finProduct = dto.getFinProduct();
            finProductMapper.updateByPrimaryKey(finProduct);
            //金融产品附属
            FinProductAdd finProductAdd = dto.getFinProductAdd();
            finProductAddMapper.updateByPrimaryKey(finProductAdd);
            //金融产品类型
            FinProductType finProductType = dto.getFinProductType();
            finProductTypeMapper.updateByPrimaryKey(finProductType);
            //现金流信息（FIN_CASH_INFO）
            finBondAssetMapper.deleteFinCashInfo(entity.getFinprodId());
            List<FinCashInfo> finCashInfoList = dto.getFinCashInfoList();
            for(FinCashInfo finCashInfo: finCashInfoList) {
                finCashInfoMapper.insertSelective(finCashInfo);
            }
            //现金流计算规则表（FIN_CASH_CALC_RULE）
            finBondAssetMapper.deleteFinCashCalcRule(entity.getFinprodId());
            List<FinCashCalcRule> finCashCalcRuleList = dto.getFinCashCalcRuleList();
            for(FinCashCalcRule finCashCalcRule: finCashCalcRuleList) {
                finCashCalcRuleMapper.insertSelective(finCashCalcRule);
            }
            //现金流支付规则表（FIN_CASH_PAY_RULE）
            finBondAssetMapper.deleteFinCashPayRule(entity.getFinprodId());
            List<FinCashPayRule> finCashPayRuleList = dto.getFinCashPayRuleList();
            for(FinCashPayRule finCashPayRule: finCashPayRuleList) {
                finCashPayRuleMapper.insertSelective(finCashPayRule);
            }
            //现金流利率信息表（FIN_RATE_INFO）
            finBondAssetMapper.deleteFinRateInfo(entity.getFinprodId());
            List<FinRateInfo> finRateInfoList = dto.getFinRateInfoList();
            for(FinRateInfo finRateInfo: finRateInfoList) {
                finRateInfoMapper.insertSelective(finRateInfo);
            }
            //现金流计划表（FIN_CASH_PLAN）
            finBondAssetMapper.deleteFinCashPlan(entity.getFinprodId());
            List<FinCashPlan> finCashPlanList = dto.getFinCashPlanList();
            for(FinCashPlan finCashPlan: finCashPlanList) {
                finCashPlanMapper.insertSelective(finCashPlan);
            }
            //行权计划表（FIN_EXERCISE_PLAN）
            finBondAssetMapper.deleteFinExercisePlan(entity.getFinprodId());
            List<FinExercisePlan> finExercisePlanList = dto.getFinExercisePlanList();
            for(FinExercisePlan finExercisePlan: finExercisePlanList) {
                finExercisePlanMapper.insertSelective(finExercisePlan);
            }
        }
        //转债信息（FIN_PRODUCT_CONVERTIBLE）
        FinProductConvertible FinProductConvertible = entity.getProductConvertible();
        if (StringUtil.isNotEmpty(FinProductConvertible)) {
            finProductConvertibleMapper.updateByPrimaryKey(FinProductConvertible);
        }
        //附件
        MstFileInputParameter mstFileInputParameter = entity.getMstFileInputParameter();
        if (StringUtil.isNotEmpty(mstFileInputParameter)
                && CollectionUtils.isNotEmpty(mstFileInputParameter.getFileList())){
            for (SysUploadFileInfo fileInfo:mstFileInputParameter.getFileList()){
                fileInfo.setBusinessId(entity.getFinprodId());
                finBondAssetMapper.updateFileByPrimaryKey(fileInfo);
            }
        }
    }

    private void doCompleteDelete(FinBondAssetSaveRequest entity) {
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        finProductMapper.deleteByPrimaryKey(entity.getFinprodId());
        finProductAddMapper.deleteByPrimaryKey(entity.getFinprodId());
        FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        finProductTypeKey.setBranch(branch);
        finProductTypeKey.setFinprodId(entity.getFinprodId());
        finProductTypeMapper.deleteByPrimaryKey(finProductTypeKey);
        finProductConvertibleMapper.deleteByPrimaryKey(entity.getFinprodId());
        // 现金流信息
        finBondAssetMapper.deleteFinCashInfo(entity.getFinprodId());
        // 现金流计算规则表
        finBondAssetMapper.deleteFinCashCalcRule(entity.getFinprodId());
        // 现金流支付规则表
        finBondAssetMapper.deleteFinCashPayRule(entity.getFinprodId());
        //现金流计划表
        finBondAssetMapper.deleteFinCashPlan(entity.getFinprodId());
        // 现金流利率信息表
        finBondAssetMapper.deleteFinRateInfo(entity.getFinprodId());
        // 行权计划表
        finBondAssetMapper.deleteFinExercisePlan(entity.getFinprodId());
//        finBondAssetMapper.deleteTrdProductDeal()
        // 行权计划表
        finBondAssetMapper.deleteFileByBussinessId(entity.getFinprodId());
    }


    /**
     * 获取基本信息--修改/详细
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinBondAssetSaveRequest getBasicInfos(String finprodId, String finprodType){
        FinBondAssetSaveRequest dto = new FinBondAssetSaveRequest();
        Map<String,Object> map= new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("FINPROD_TYPE2_F10",DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        map.put("FIN_CASH_BUSINESS_TYPE_01",DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("CASH_TYPE_1_02",DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("CASH_TYPE_1_01",DictCodeListExt1.现金流一级类型_本金.getValue());
        map.put("CASH_TYPE_2_C02",DictCodeListExt1.现金流二级类型_利息.getValue());
        map.put("CASH_TYPE_2_C01",DictCodeListExt1.现金流二级类型_本金.getValue());
        map.put("FINPROD_TYPE_F01",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("TRADE_PLACE_01",DictCodeListExt1.交易场所_银行间.getValue());
        map.put("TRADE_PLACE_02",DictCodeListExt1.交易场所_商业银行柜台.getValue());
        map.put("TRADE_PLACE_03",DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04",DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        map.put("platformDate",iplatformDate.getSysDateStr());
        if(Objects.isNull(finprodType)){
            map.put("finprodType",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            dto = finBondAssetMapper.getBasicInfo(map);
        } else {
            map.put("finprodType",finprodType);
            dto = finBondAssetMapper.getBasicInfo(map);
        }
        if(null!=dto){
            //转债信息
            BondFinProductConvertibleDto finProductConvertible = new BondFinProductConvertibleDto();
            BeanUtil.copyPropertiesIgnoreNull(finProductConvertible,dto);
            dto.setFinProductConvertible(finProductConvertible);
            //计息要素
            BondFinInterestFactorDto finInterestFactorDto = new BondFinInterestFactorDto();
            BeanUtil.copyPropertiesIgnoreNull(finInterestFactorDto,dto);
            finInterestFactorDto.setSpreadRate(dto.getSpreadRate());
            if (StringUtil.equals(finInterestFactorDto.getBasis(), DictCodeList.计息基础_A_A_平均分配.getValue())){
                finInterestFactorDto.setBasis(DictCodeList.计息基础_A_A.getValue());
                finInterestFactorDto.setIntpayRule(DictCodeListExt1.利息分配方式_平均分配.getValue());
            }
            if (StringUtil.equals(finInterestFactorDto.getBasis(), DictCodeList.计息基础_A_A_按实际天数分配.getValue())){
                finInterestFactorDto.setBasis(DictCodeList.计息基础_A_A.getValue());
                finInterestFactorDto.setIntpayRule(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
            }
            if (StringUtil.equals(finInterestFactorDto.getMarketBasis(), DictCodeList.计息基础_A_A_平均分配.getValue())
                    || StringUtil.equals(finInterestFactorDto.getMarketBasis(), DictCodeList.计息基础_A_A_按实际天数分配.getValue())) {
                finInterestFactorDto.setMarketBasis(DictCodeList.计息基础_A_A.getValue());
            }
            dto.setFinInterestFactorDto(finInterestFactorDto);
            //付息信息
            BondFinPayInterestStdInfoDto finPayInterestStdInfoDto = new BondFinPayInterestStdInfoDto();
            BeanUtil.copyPropertiesIgnoreNull(finPayInterestStdInfoDto,dto);
            if(StringUtil.isNotEmpty(finPayInterestStdInfoDto)) {
                finPayInterestStdInfoDto.setMaxTradeDate(this.getMaxTradeDate(dto.getFinprodId()));
            }
            map.put("cashId",dto.getInterestCashId());
            if (StringUtil.isNotEmpty(dto.getInterestCashId())) {
                List<BondFinCashPlanDto> invScheduleList = finBondAssetMapper.selectInvScheduleList(map);
                for (BondFinCashPlanDto invRepay: invScheduleList) {
                    if (StringUtil.equals(invRepay.getBasis(), DictCodeList.计息基础_A_A_平均分配.getValue())
                            || StringUtil.equals(invRepay.getBasis(), DictCodeList.计息基础_A_A_按实际天数分配.getValue())) {
                        invRepay.setBasis(DictCodeList.计息基础_A_A.getValue());
                    }
                }
                finPayInterestStdInfoDto.setInvScheduleList(invScheduleList);
                dto.setFinPayInterestStdInfoDto(finPayInterestStdInfoDto);
            }
            //付息信息
            map.put("cashId",dto.getPrincipalCashId());
            if (StringUtil.isNotEmpty(dto.getPrincipalCashId())) {
                List<BondFinCashPlanDto> invRepayList = finBondAssetMapper.selectInvScheduleList(map);
                dto.setInvRepayList(invRepayList);
            }
            //行权计划
            map.put("cashId",dto.getPrincipalCashId());
            if (StringUtil.isNotEmpty(dto.getPrincipalCashId())) {
                List<BondFinExercisePlanDto> finExercisePlanList = finBondAssetMapper.selectFinExercisePlanList(map);
                dto.setFinExercisePlanList(finExercisePlanList);
            }
            //发行方式
            List<FinProductIssue> finProductIssues = finBondAssetMapper.selectFinProductIssue(dto.getFinprodMarketId());
            List<BondFinProductIssueDto> finProductIssueList = new ArrayList<>();
            BondFinProductIssueDto bondFinProductIssueDto = new BondFinProductIssueDto();
            BeanUtil.copyPropertiesIgnoreNull(bondFinProductIssueDto,dto);
            for (FinProductIssue finProductIssue:finProductIssues){
                BeanUtil.copyPropertiesIgnoreNull(bondFinProductIssueDto, finProductIssue);
                finProductIssueList.add(bondFinProductIssueDto);
            }
            dto.setFinProductIssueList(finProductIssueList);
            List<SysUploadFileInfo> uploadInfo = joyinFSMapper.getUploadInfo(dto.getFinprodId());
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(uploadInfo);
            mstFileInputParameter.setBussinessId(dto.getFinprodId());
            dto.setMstFileInputParameter(mstFileInputParameter);
//            FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(dto.getFinprodId());
//            BondFinProductConvertibleDto finProductConvertibleDto = new BondFinProductConvertibleDto();
//            BeanUtil.copyPropertiesIgnoreNull(finProductConvertibleDto,finProductConvertible);
//            if (null != finProductConvertible) {
//                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finProductConvertible.getConFinprodId());
//                if(null!=finProduct){
//                    dto.setConFinprodMarketId(finProduct.getFinprodMarketId());
//                }
//                dto.setFinProductConvertible(finProductConvertibleDto);
//            }
        } else {
            return new FinBondAssetSaveRequest();
        }
        return dto;
    }

    /**
     * 封装基础组件-标的类金融产品需要的DTO
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public FinBondAssetSaveRequest encapBaseFinProductBaseInfoList(FinBondAssetSaveRequest dto) {
        Long branch = StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)) ? 0 : Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        dto.setBranch(branch);
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        //现金流信息
        List<FinCashInfo> finCashInfoList = new ArrayList<FinCashInfo>();
        //现金流支付计划
        List<FinCashPlan> finCashPlanList = new ArrayList<FinCashPlan>();
        //现金流计算规则
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        //现金流利率信息表
        List<FinRateInfo> finRateInfoList = new ArrayList<>();
        //现金流利率信息表
        List<FinCashPayRule> finCashPayRuleList = new ArrayList<>();

        //标的类金融产品entity
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        finProduct.setIssuePrice(dto.getIssuePrice());
        //期限
        finProduct.setTermDays(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()));
        finProduct.setIssueAmt(dto.getIssueAmt());
        finProduct.setCalendarId(this.getCalendarId(dto.getTradeMarket()));
        // 一体化对应etliu start
        // 债券新建画面，入库时，标的类金融产品表的含权债摊销日字段，当含权债时，登数据字典【含权债摊销日.最近行权日】
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), finProduct.getIsRight())) {
            finProduct.setExeDate(CacheUtil.getSysParam("ISSECTOEXEDATE") == null ?
                    DictCodeList.含权债摊销日_最近行权日.getValue() : CacheUtil.getSysParam("ISSECTOEXEDATE"));
        }
        // 实际到期日:先存成到期日
        finProduct.setActmdate(finProduct.getMdate());
        finProduct.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        // 一体化对应etliu end
        //金融产品分类表entity
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, dto);
        finProductType.setBranch(branch);
        Map<String,Object> paramCinfig = new HashMap<>();
        paramCinfig.put("configType",DictCodeListExt2.配置类型_WIND分类.getValue());
        paramCinfig.put("inputValue2",dto.getType9());
        paramCinfig.put("inputValue3",dto.getType10());
        paramCinfig.put("inputValue4",dto.getType11());
        FinProductTypeConfig resultType = finBondAssetMapper.getWindType(paramCinfig);
        if(Objects.nonNull(resultType)) {
            finProductType.setType4(resultType.getOutputValue1());
            finProductType.setType5(resultType.getOutputValue2());
            finProductType.setType6(resultType.getOutputValue1());
            finProductType.setType7(resultType.getOutputValue2());
        }
        finProductType.setType8(DictCodeList.平安资产类型_债券.getValue());
        finProductType.setType9(dto.getTypeTwo());
        finProductType.setType10(dto.getTypeThree());
        finProductType.setType11(dto.getTypeFour());
        //金融产品附属表entity
        FinProductAdd finProductAdd = new FinProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(finProductAdd, dto);
        if (StringUtil.isNotEmpty(dto.getIssueType()) &&
                StringUtil.equals(dto.getIssueType(), DictCodeListExt1.募集方式_私募.getValue())) {
            finProductAdd.setIsPpb(DictCodeList.是否_是.getValue());
        } else {
            finProductAdd.setIsPpb(DictCodeList.是否_否.getValue());
        }
        finProductAdd.setFaceValue(BigDecimal.valueOf(100));
        //----add by jjpei 20190727 添加投资性质----start----
//        if(StringUtil.isNotBlank(dto.getType4())&&DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue().equals(dto.getType4())){
//            finProductAdd.setInvestNature(DictCodeList.投资性质_权益类.getValue());
//        }
//        else{
//            finProductAdd.setInvestNature(DictCodeList.投资性质_固定收益类.getValue());
//        }
        //----add by jjpei 20190727 添加投资性质----end----
        //转债信息
        if (StringUtil.isNotEmpty(dto.getFinProductConvertible()) &&
                StringUtil.isNotEmpty(dto.getFinProductConvertible().getConPrice())) {
            FinProductConvertible finProductConvertible = new FinProductConvertible();
            finProductConvertible.setFinprodId(dto.getFinprodId());
            BeanUtil.copyPropertiesIgnoreNull(finProductConvertible, dto.getFinProductConvertible());
            dto.setProductConvertible(finProductConvertible);
        }
        //现金流信息Entity-本金
        FinCashInfo finCashInfoPrincipal = encapFinCashInfo(dto, DictCodeListExt1.现金流一级类型_本金.getValue(), DictCodeListExt1.现金流二级类型_本金.getValue(), branch);
        //现金流信息Entity-利息
        FinCashInfo finCashInfoInterest = encapFinCashInfo(dto, DictCodeListExt1.现金流一级类型_利息.getValue(), DictCodeListExt1.现金流二级类型_利息.getValue(), branch);
        finCashInfoList.add(finCashInfoPrincipal);
        finCashInfoList.add(finCashInfoInterest);
        FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
        finCashCalcRule.setCashId(finCashInfoInterest.getCashId());
        finCashCalcRule.setEffDate(dto.getVdate());
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        //画面.发行人计息基础=A/A且画面.利息分配方式=平均分配时，为数据字典.计息基础（BASIS）.A/A_B_COUP
        //画面.发行人计息基础=A/A且画面.利息分配方式=按实际天数分配时，为数据字典.计息基础（BASIS）.A/A_B_DAY
        //画面.发行人计息基础为A/A以外时，为画面.发行人计息基础"
        BondFinInterestFactorDto finInterestFactorDto = dto.getFinInterestFactorDto();
        finCashCalcRule.setCalcType(finInterestFactorDto.getCalcType());
        finProduct.setCouponSpecies(finInterestFactorDto.getCouponSpecies());
        finCashCalcRule.setBasis(getBasis(finInterestFactorDto.getBasis(),finInterestFactorDto.getIntpayRule()));
        finCashCalcRule.setMarketBasis(getBasis(finInterestFactorDto.getMarketBasis(),finInterestFactorDto.getIntpayRule()));
        //零息和贴现时为画面.参考收益率；附息和利随本清，浮动利率且首次利率确定日可以取到基准利率行情时，为区间利率，否则为画面.初始利率
        if (StringUtil.equals(finInterestFactorDto.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())
                || StringUtil.equals(finInterestFactorDto.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue())) {
            finCashCalcRule.setYield(finInterestFactorDto.getYield());
        } else if (StringUtil.equals(finInterestFactorDto.getCouponSpecies(), DictCodeList.息票品种_附息.getValue())
                || StringUtil.equals(finInterestFactorDto.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())) {
            finCashCalcRule.setYield(finInterestFactorDto.getYield());
        } else {
            finCashCalcRule.setYield(finInterestFactorDto.getYield());
        }
        finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
        finCashCalcRule.setFinprodId(dto.getFinprodId());
        finCashCalcRule.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        finCashCalcRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        finCashCalcRule.setBranch(branch);
        finCashCalcRuleList.add(finCashCalcRule);

        BondFinPayInterestStdInfoDto finPayInterestStdInfoDto = dto.getFinPayInterestStdInfoDto();
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        finCashPayRule.setCashId(finCashInfoInterest.getCashId());
        finCashPayRule.setEffDate(dto.getVdate());
        if (StringUtil.isNotEmpty(finPayInterestStdInfoDto.getPayCycle())) {
            finCashPayRule.setPayCycle(finPayInterestStdInfoDto.getPayCycle());
        }else{
            finCashPayRule.setPayCycle(BussinessDictCodeList.频率_到期一次.getValue());
        }
        finCashPayRule.setEndTdayRule(DictCodeList.营业日准则_不调整.getValue());
        if (StringUtil.equals(finInterestFactorDto.getCouponSpecies(),DictCodeList.息票品种_零息.getValue())
                || StringUtil.equals(finInterestFactorDto.getCouponSpecies(),DictCodeList.息票品种_贴现.getValue())) {
            finCashPayRule.setPayTdayRule(DictCodeList.营业日准则_向后调整.getValue());
        }else{
            finCashPayRule.setPayTdayRule(finPayInterestStdInfoDto.getPayTdayRule());
        }
        finCashPayRule.setCalcRule(finPayInterestStdInfoDto.getCalcRule());
        finCashPayRule.setFirstPayDate(finPayInterestStdInfoDto.getFirstPayDate());
        finCashPayRule.setCashUnitType(DictCodeList.现金流单位_百元法.getValue());
        finCashPayRule.setFinprodId(dto.getFinprodId());
        finCashPayRule.setBranch(branch);
        finCashPayRule.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        finCashPayRule.setPayType(DictCodeList.支付方式_现金.getValue());
        finCashPayRuleList.add(finCashPayRule);

        if(StringUtil.equals(finInterestFactorDto.getCalcType(),DictCodeList.利率类型_浮动利率.getValue())){
            FinRateInfo finRateInfo = new FinRateInfo();
            finRateInfo.setCashId(finCashInfoInterest.getCashId());
            finRateInfo.setEffDate(dto.getVdate());
            finRateInfo.setIntType(dto.getCalcType());
            finRateInfo.setBranch(branch);
            finRateInfo.setBasis(getBasis(finInterestFactorDto.getBasis(),finInterestFactorDto.getIntpayRule()));
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo,finInterestFactorDto);
            finRateInfo.setFinprodId(dto.getFinprodId());
            finRateInfo.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            finRateInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            finRateInfo.setBenchmarkId(finInterestFactorDto.getBasicRate());
            finRateInfo.setBenchmarkIdAccuracy(finInterestFactorDto.getBenchmarkIdAccuracy());
            finRateInfoList.add(finRateInfo);
        }
        //付息信息DTO 现金流支付规则 现金流支付支付计划
        List<BondFinCashPlanDto> invSchedules = finPayInterestStdInfoDto.getInvScheduleList();
        if (CollectionUtils.isNotEmpty(invSchedules)){
            for (int i = 0; i < invSchedules.size(); i++) {
                BondFinCashPlanDto bondFinCashPlanDto = invSchedules.get(i);
                FinCashPlan invSchedule = new FinCashPlan();
                BeanUtil.copyPropertiesIgnoreNull(invSchedule,bondFinCashPlanDto);
                invSchedule.setCashId(finCashInfoInterest.getCashId());
                invSchedule.setCashNum((long) (i + 1));
                if(DateUtil.compareDate(bondFinCashPlanDto.getMdate(), dto.getMdate())==0){
                    invSchedule.setCashType(DictCodeList.现金流类型_到期收息.getValue());
                }else{
                    invSchedule.setCashType(DictCodeList.现金流类型_中间收息.getValue());
                }
                Date vdate = bondFinCashPlanDto.getVdate();Date mdate = bondFinCashPlanDto.getMdate();
                long termdays  = DateUtil.getBetweenDayNumber(DateUtil.format(vdate),DateUtil.format(mdate));
                invSchedule.setTermdays(termdays);
                invSchedule.setFrequency((long)invSchedules.size());
                invSchedule.setPayType(DictCodeList.支付方式_现金.getValue());
                invSchedule.setCashUnitType(DictCodeList.现金流单位_百元法.getValue());
                invSchedule.setFinprodId(dto.getFinprodId());
                invSchedule.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                invSchedule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                invSchedule.setBranch(branch);
                invSchedule.setBasis(getBasis(bondFinCashPlanDto.getBasis(),finInterestFactorDto.getIntpayRule()));
                invSchedule.setVdateUnadjust(invSchedule.getVdate());
                if (!StringUtil.isNotEmpty(invSchedule.getMdateUnadjust())){
                    invSchedule.setMdateUnadjust(invSchedule.getMdate());
                }
                finCashPlanList.add(invSchedule);
            }
        }
        //还本信息DTO 现金流支付支付计划
        List<BondFinCashPlanDto> invRepays = dto.getInvRepayList();
        if (CollectionUtils.isNotEmpty(invRepays)){
            for (int i = 0; i < invRepays.size(); i++) {
                BondFinCashPlanDto bondFinCashPlanDto = invRepays.get(i);
                FinCashPlan invRepay = new FinCashPlan();
                BeanUtil.copyPropertiesIgnoreNull(invRepay,bondFinCashPlanDto);
                invRepay.setCashId(finCashInfoPrincipal.getCashId());
                invRepay.setCashNum((long) (i + 1));
                if(DateUtil.compareDate(bondFinCashPlanDto.getMdate(), dto.getMdate())==0){
                    invRepay.setCashType(DictCodeList.现金流类型_到期还本.getValue());
                }else{
                    invRepay.setCashType(DictCodeList.现金流类型_中间还本.getValue());
                }
                Date vdate = bondFinCashPlanDto.getVdate();
                if (StringUtil.equals(dto.getFinInterestFactorDto().getCouponSpecies(),DictCodeList.息票品种_零息.getValue())
                        ||StringUtil.equals(dto.getFinInterestFactorDto().getCouponSpecies(),DictCodeList.息票品种_贴现.getValue())){
                    vdate = dto.getVdate();
                }
                Date mdate = bondFinCashPlanDto.getMdate();
                long termdays  = DateUtil.getBetweenDayNumber(DateUtil.format(vdate),DateUtil.format(mdate));
                invRepay.setTermdays(termdays);
                invRepay.setFrequency((long)invSchedules.size());
                invRepay.setPayType(DictCodeList.支付方式_现金.getValue());
                invRepay.setCashUnitType(DictCodeList.现金流单位_百元法.getValue());
                invRepay.setFinprodId(dto.getFinprodId());
                invRepay.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                invRepay.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                invRepay.setBranch(branch);
                invRepay.setCashBaseamt(BigDecimal.valueOf(100));
                invRepay.setBasis(getBasis(bondFinCashPlanDto.getBasis(),finInterestFactorDto.getIntpayRule()));
                finCashPlanList.add(invRepay);
            }
        }
        //行权计划DTO 行权计划
        if(null!=dto.getFinExercisePlanList()){
            List<BondFinExercisePlanDto> finExercisePlanList = dto.getFinExercisePlanList();
            List<FinExercisePlan> finExercisePlans = new ArrayList<>();
            for (BondFinExercisePlanDto finExercisePlanDto: finExercisePlanList){
                FinExercisePlan finExercisePlan = new FinExercisePlan();
                BeanUtil.copyPropertiesIgnoreNull(finExercisePlan,finExercisePlanDto);
                finExercisePlan.setCashId(finCashInfoPrincipal.getCashId());
                finExercisePlan.setFinprodId(dto.getFinprodId());
                finExercisePlan.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                finExercisePlan.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                finExercisePlan.setBranch(branch);
                finExercisePlans.add(finExercisePlan);
            }
            baseFinProductBaseInfoDto.setFinExercisePlanList(finExercisePlans);
        }
        //附件信息
        if(null!=fileCommonService){
            if(null!=dto.getFileCommonList()){
                FileCommonDto fileCommonDto = assetCommonService.encapFileCommon(finProduct,dto.getFileCommonList());
                dto.setMstFileInputParameter(fileCommonDto.getMstFileInputParameter());
            }
        }
        baseFinProductBaseInfoDto.setFinProduct(finProduct);
        baseFinProductBaseInfoDto.setFinProductType(finProductType);
        baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
        baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfoList);
        baseFinProductBaseInfoDto.setFinCashPlanList(finCashPlanList);
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(finCashCalcRuleList);
        baseFinProductBaseInfoDto.setFinRateInfoList(finRateInfoList);
        baseFinProductBaseInfoDto.setFinCashPayRuleList(finCashPayRuleList);
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
        dto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return dto;
    }

    private String getBasis(String basis,String intpayRule) {
        if (StringUtil.isNotEmpty(basis)) {
            if (StringUtil.equals(basis, DictCodeList.计息基础_A_A.getValue())) {
                if (StringUtil.equals(intpayRule, DictCodeListExt1.利息分配方式_平均分配.getValue())) {
                    return DictCodeList.计息基础_A_A_平均分配.getValue();
                }
                if (StringUtil.equals(intpayRule, DictCodeListExt1.利息分配方式_按照实际天数分配.getValue())) {
                    return DictCodeList.计息基础_A_A_按实际天数分配.getValue();
                }
                return basis;
            } else {
                return basis;
            }
        }
        return null;
    }

    /**
     * 组装现金流信息
     * @see
     * @since 1.0
     */
    public FinCashInfo encapFinCashInfo(FinBondAssetSaveRequest dto,String cashType1,String cashType2,Long branch){
        FinCashInfo finCashInfo = new FinCashInfo();
        BeanUtil.copyPropertiesIgnoreNull(finCashInfo, dto);
        String cashId = commonBusinessService.generateCashFlowCode(dto.getFinprodId(), DictCodeListExt1.现金流业务类型_金融产品.getValue(), branch, cashType2);
        finCashInfo.setCashId(cashId);
        finCashInfo.setCashType1(cashType1);
        finCashInfo.setCashType2(cashType2);
        finCashInfo.setBranch(branch);
        finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        return  finCashInfo;
    }


    /**
     * 生成金融产品代码/市场代码
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public FinBondAssetSaveRequest getFinprodId(FinBondAssetSaveRequest dto){
        String finprodId = StringUtil.EMPTY;
        String  finprodMarketId = dto.getFinprodMarketId();
        finprodId = commonBusinessService!=null?commonBusinessService.getFinProdId(finprodMarketId, DictCodeListExt1.金融产品类型_投管用_债券.getValue(), dto.getChlFinprodId()):IDGenerator.generate();
        dto.setFinprodId(finprodId);
        return dto;
    }

    /**
     *  市场代码是否重复
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public int selectCountByfinprodMarketId(String finprodMarketId){
        return finBondAssetMapper.selectCountByfinprodMarketId(finprodMarketId);
    }

    /**
     * 查询接口表资产信息
     * <功能详细描述>
     * @param finprodMarketId
     * @return
     * @see
     * @since 1.0
     */
    public FinBondAssetSaveRequest getInterfBondDto(String finprodMarketId) {
        InterfPayhsrcsecinfoDto dto = new InterfPayhsrcsecinfoDto();
        List<String> list = new ArrayList<>();
        list.add(finprodMarketId);
        dto.setSecidList(list);
        dto = commonAutoService.autoBondCoupon(dto, finprodMarketId);
        com.joyintech.tams.component.business.common.dto.BaseFinProductBaseInfoDto baseInfoDto =
                new com.joyintech.tams.component.business.common.dto.BaseFinProductBaseInfoDto();
        if (StringUtil.isNotEmpty(dto)) {
            baseInfoDto =  dto.getBaseFinProductBaseInfoList().get(0);
        }else {
            return null;
        }
        //债券基本信息
        FinBondAssetSaveRequest bondTargetDto = new FinBondAssetSaveRequest();
        //金融产品基本信息表
        FinProduct finProduct =  baseInfoDto.getFinProduct();
        if (StringUtil.isNotEmpty(finProduct)) {
            BeanUtil.copyPropertiesIgnoreNull(bondTargetDto,finProduct);
        }
        bondTargetDto.setRepAssetType(baseInfoDto.getRepAssetType());
        //类型
        if (StringUtil.isNotEmpty(baseInfoDto.getFinProductType())) {
//            bondTargetDto.setType4(baseInfoDto.getFinProductType().getType4());
//            bondTargetDto.setType5(baseInfoDto.getFinProductType().getType5());
            bondTargetDto.setType9(baseInfoDto.getFinProductType().getType9());
            bondTargetDto.setType10(baseInfoDto.getFinProductType().getType10());
//            bondTargetDto.setType11(baseInfoDto.getFinProductType().getType11());
//            bondTargetDto.setType12(baseInfoDto.getFinProductType().getType12());

        }
        //附属表
        if (StringUtil.isNotEmpty(baseInfoDto.getFinProductAdd())) {
            bondTargetDto.setIsAutoUpdate(baseInfoDto.getFinProductAdd().getIsAutoUpdate());
            // 20200819 债券快录添加是否城投债以及城投债级别 wangwei start
            bondTargetDto.setCityBondLev(baseInfoDto.getFinProductAdd().getCityBondLev());
            bondTargetDto.setIsCityBond(baseInfoDto.getFinProductAdd().getIsCityBond());
            // 20200819 债券快录添加是否城投债以及城投债级别 wangwei end
        }
        //
        BaseFinProductBaseInfoDto baseInfoDto2 = new BaseFinProductBaseInfoDto();
        BeanUtil.copyPropertiesIgnoreNull(baseInfoDto2, baseInfoDto);
        List<BaseFinProductBaseInfoDto> baseList = new ArrayList<>();
        baseList.add(baseInfoDto2);
        bondTargetDto.setBaseFinProductBaseInfoList(baseList);
        //现金流利率信息表
        List<FinRateInfo> finRateInfos = baseInfoDto2.getFinRateInfoList();
        //计息要素业务
        if (StringUtil.isNotEmpty(finRateInfos)) {
            BondFinInterestFactorDto finInterestFactorDto = new BondFinInterestFactorDto();
            finInterestFactorDto.setCouponSpecies(finProduct.getCouponSpecies());
            BeanUtil.copyPropertiesIgnoreNull(finInterestFactorDto, finRateInfos.get(0));
            //计息基础数据转换
            String basis =  finInterestFactorDto.getBasis();
            if (BussinessDictCodeList.计息基础_A_A_B_DAY.getValue().equals(basis)){
                finInterestFactorDto.setBasis(BussinessDictCodeList.计息基础_A_A.getValue());
                finInterestFactorDto.setBasisStyle(BussinessDictCodeList.利息分配方式_按照实际天数分配.getValue());
            }else if (BussinessDictCodeList.计息基础_A_A_B_COUP.getValue().equals(basis)){
                finInterestFactorDto.setBasis(BussinessDictCodeList.计息基础_A_A.getValue());
                finInterestFactorDto.setBasisStyle(BussinessDictCodeList.利息分配方式_平均分配.getValue());
            }else {
                finInterestFactorDto.setBasis(basis);
            }
            //update by leipeng 现状：选择债券代码，点击放大镜进行加券，接口文件中初始利率为0.035，加券时，初始利率为0.035%    期望：加券时，因前台初始利率已经是%号了，再加券时应乘以100
            if(null!=finInterestFactorDto.getRate()){
                finInterestFactorDto.setRate(finInterestFactorDto.getRate().multiply(new BigDecimal("100")));
            }

            bondTargetDto.setFinInterestFactorDto(finInterestFactorDto);
        }
        //现金流计算规则
        List<FinCashCalcRule>  finCashCalcRuleList = baseInfoDto.getFinCashCalcRuleList();

        //现金流支付规则
        List<FinCashPayRule> finCashPayRuleList = baseInfoDto2.getFinCashPayRuleList();

        if (StringUtil.isNotEmpty(finCashCalcRuleList)) {
            //付息信息业务
            BondFinPayInterestStdInfoDto finPayInterestStdInfoDto = new BondFinPayInterestStdInfoDto();
            //还本信息业务
            BussFinRepaymentDto bussFinRepaymentDto = new BussFinRepaymentDto();
            finPayInterestStdInfoDto.setCouponSpecies(finProduct.getCouponSpecies());
            if (StringUtil.isNotEmpty(finCashPayRuleList)){
                finPayInterestStdInfoDto.setPayCycle(finCashPayRuleList.get(0).getPayCycle());
                finPayInterestStdInfoDto.setCalcRule(finCashPayRuleList.get(0).getCalcRule());
                finPayInterestStdInfoDto.setFirstPayDate(finCashPayRuleList.get(0).getFirstPayDate());
            }
            //现金流计划表
            List<FinCashPlan> finCashPlanList = baseInfoDto2.getFinCashPlanList();
            if (StringUtil.isNotEmpty(finCashPlanList)) {
                //还本
                List<FinCashPlanDto> repPayDtos = new ArrayList<>();
                //付息
                List<BondFinCashPlanDto> payInvestDtos = new ArrayList<>();
                for (FinCashPlan finCashPlan : finCashPlanList) {
                    if (BussinessDictCodeList.现金流类型_中间还本.getValue().equals(finCashPlan.getCashType())
                            || BussinessDictCodeList.现金流类型_到期还本.getValue().equals(finCashPlan.getCashType())){
                        FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                        BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto,finCashPlan);
                        repPayDtos.add(finCashPlanDto);

                    }else{
                        if (!DictCodeList.息票品种_贴现.getValue().equals(bondTargetDto.getCouponSpecies())
                                && !DictCodeList.息票品种_零息.getValue().equals(bondTargetDto.getCouponSpecies())) {
                            BondFinCashPlanDto finCashPlanDto = new BondFinCashPlanDto();
                            BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto,finCashPlan);
                            finCashPlanDto.setRangeYield(ArithmeticUtil.multiply(finCashPlanDto.getRangeYield(), new BigDecimal(100)));
                            payInvestDtos.add(finCashPlanDto);
                        }
                    }
                }
                bussFinRepaymentDto.setFinCashPlanList(repPayDtos);
//                finPayInterestStdInfoDto.setFinCashPlanList(payInvestDtos);
                finPayInterestStdInfoDto.setInvScheduleList(payInvestDtos);
            }
            bondTargetDto.setFinPayInterestStdInfoDto(finPayInterestStdInfoDto);
//            bondTargetDto.setBussFinRepaymentDto(bussFinRepaymentDto);
        }
        // 行权计划
//        BusiFinExercisePlanDto busiFinExercisePlanDto = new BusiFinExercisePlanDto();
//        busiFinExercisePlanDto.setFinExercisePlanList(baseInfoDto2.getFinExercisePlanList());
//        bondTargetDto.setBusiFinExercisePlanDto(busiFinExercisePlanDto);
        List<FinExercisePlan> finExercisePlanList = baseInfoDto2.getFinExercisePlanList();
        List<BondFinExercisePlanDto> finExercisePlans = new ArrayList<>();
        for (FinExercisePlan finExercisePlan: finExercisePlanList){
            BondFinExercisePlanDto finExercisePlanDto = new BondFinExercisePlanDto();
            BeanUtil.copyPropertiesIgnoreNull(finExercisePlanDto,finExercisePlan);
            finExercisePlans.add(finExercisePlanDto);
        }
        bondTargetDto.setFinExercisePlanList(finExercisePlans);
        //查询债项
        Map<String, Object> zxmap = new HashMap<>();
        zxmap.put("oCode", finprodMarketId);
        List<BusiFinProductRatingDto> ratingList = busiProductRatingService.getProductMarketRating(zxmap);
        //查询主体
        Map<String, Object> ztmap = new HashMap<>();
        ztmap.put("oCode", bondTargetDto.getIssuer());
        ratingList.addAll(busiProductRatingService.getProductOrgRating(ztmap));
        // 设置评级信息
        bondTargetDto.setMstBondCreditRatingList(ratingList);
        // 中债报送
        RepAssetBond repAssetBond = baseInfoDto.getRepAssetBond();
        bondTargetDto.setRepAssetBond(baseInfoDto.getRepAssetBond());
        bondTargetDto.setRepAssetDept(baseInfoDto.getRepAssetDept());
        return bondTargetDto;
    }
    // 债券新建新增快捷录入功能 add by renping 20191010 start

    /**
     * 查询业务表资产信息
     *
     * @param finprodMarketId
     * @return 资产信息
     * @see
     * @since 1.0
     */
    public FinBondAssetSaveRequest getBondDto(String finprodMarketId) {
        //债券基本信息
        FinBondAssetSaveRequest bondTargetDto = getBasicInfos(finprodMarketId,null);
        if (bondTargetDto == null) {
            return bondTargetDto;
        }
        if (null != fileAttachmentProcess && StringUtil.isNotEmpty(bondTargetDto.getFinprodId())) {
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(
                    bondTargetDto.getFinprodId());
            bondTargetDto.setFileArray(files);
        }
        //查询债项
        Map<String, Object> zxmap = new HashMap<>();
        zxmap.put("oCode", finprodMarketId);
        List<BusiFinProductRatingDto> ratingList = busiProductRatingService.getProductMarketRating(zxmap);
        //查询主体
        Map<String, Object> ztmap = new HashMap<>();
        ztmap.put("oCode", bondTargetDto.getIssuer());
        ratingList.addAll(busiProductRatingService.getProductOrgRating(ztmap));
        // 设置评级信息
        bondTargetDto.setMstBondCreditRatingList(ratingList);
        // 现金流计算规则
        // 根据主键获取业务实体--付息信息
        FinPayInterestStdInfoDto finPayInterestStdInfoDto = this.getFinPayInterestStdInfoByProductId(
                bondTargetDto.getFinprodId(), bondTargetDto.getvdateStr(),
                DictCodeListExt1.现金流二级类型_利息.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue());
        BondFinPayInterestStdInfoDto bondFinPayInterestStdInfoDto = new BondFinPayInterestStdInfoDto();
        if (StringUtil.isNotEmpty(finPayInterestStdInfoDto)) {
            BeanUtil.copyPropertiesIgnoreNull(bondFinPayInterestStdInfoDto, finPayInterestStdInfoDto);
            bondTargetDto.setFinPayInterestStdInfoDto(bondFinPayInterestStdInfoDto);
        }
        // 现金流支付规则
        BussFinRepaymentDto bussFinRepaymentDto = new BussFinRepaymentDto();
        finPayInterestStdInfoDto = this.getFinPayInterestStdInfoByProductId(
                bondTargetDto.getFinprodId(), bondTargetDto.getvdateStr(),
                DictCodeListExt1.现金流二级类型_本金.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue());
        if (null != finPayInterestStdInfoDto) {
            bussFinRepaymentDto.setPayCycle(finPayInterestStdInfoDto.getPayCycle());
            bussFinRepaymentDto.setRemark(finPayInterestStdInfoDto.getRemark());
        }
        // 行权计划
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", bondTargetDto.getFinprodId());
        param.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        param.put("execType01", DictCodeList.行权状态_未行权.getValue());
        param.put("execType02", DictCodeList.行权状态_已行权.getValue());
        List<FinExercisePlan> finExercisePlanList = this.getfinExercisePlanByProductId(
                param);
        BusiFinExercisePlanDto busiFinExercisePlanDto = new BusiFinExercisePlanDto();
        busiFinExercisePlanDto.setFinExercisePlanList(finExercisePlanList);
        List<BondFinExercisePlanDto> finExercisePlans = new ArrayList<>();
        for (FinExercisePlan finExercisePlan: finExercisePlanList){
            BondFinExercisePlanDto finExercisePlanDto = new BondFinExercisePlanDto();
            BeanUtil.copyPropertiesIgnoreNull(finExercisePlanDto,finExercisePlan);
            finExercisePlans.add(finExercisePlanDto);
        }
        bondTargetDto.setFinExercisePlanList(finExercisePlans);
        if (bondTargetDto != null && bondTargetDto.getFinInterestFactorDto() != null) {
            BondFinInterestFactorDto finInterestFactorDto = bondTargetDto.getFinInterestFactorDto();
            if (StringUtil.equals(finInterestFactorDto.getBasis(),
                    DictCodeList.计息基础_A_A_平均分配.getValue())) {
                finInterestFactorDto.setBasisStyle(DictCodeListExt1.利息分配方式_平均分配.getValue());
                finInterestFactorDto.setBasis(DictCodeList.计息基础_A_A.getValue());
            }
            else if (StringUtil.equals(finInterestFactorDto.getBasis(),
                    DictCodeList.计息基础_A_A_按实际天数分配.getValue())) {
                finInterestFactorDto.setBasisStyle(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
                finInterestFactorDto.setBasis(DictCodeList.计息基础_A_A.getValue());
            }
        }
        return bondTargetDto;
    }
    // 债券新建新增快捷录入功能 add by renping 20191010 end

    public List<FinExercisePlan> getfinExercisePlanByProductId(Map<String,Object> map){
        String psT39 = DictCodeList.交易方向_行权_赎回.getValue();
        return finBondAssetMapper.getfinExercisePlanByProductId(map);
    }

    public FinPayInterestStdInfoDto getFinPayInterestStdInfoByProductId(String finprodId, String vdate, String cashType,
                                                                        String finprodType) {
        FinPayInterestStdInfoDto finPayInterestStdInfoDto= finBondAssetMapper.getFinPayInterestStdInfoByProductId(finprodId, vdate, cashType,
                finprodType);
        if(finPayInterestStdInfoDto!=null && cashType.equals(DictCodeListExt1.现金流二级类型_本金.getValue())){
            String repayWithoutInt =finBondAssetMapper.getRepayWithoutIntByProductId(finprodId,cashType);
            finPayInterestStdInfoDto.setIsPayInt(repayWithoutInt);//界面是否还本不带息
        }
        return finPayInterestStdInfoDto;
    }

    /**
     * 获取新建界面的以及市场投标债券代码联想下拉框
     *
     * @param request
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(SelectOptionCommonRequest request) {
        Map<String, Object> param = BeanUtil.object2Map(request);
        param.put("assestType", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        return this.queryForPageForSelectReturn(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                ()->finBondAssetMapper.getfinprodIdForAdd(param));
    }

    public int isHqTrade(String finprodId) {
        return finBondAssetMapper.isHqTrade(finprodId);
    }

    /**
     * 校验金融产品代码不重复
     * @param finprodId
     * @return
     */
    public int selectCountByfinprodId(String finprodId) {
        return finBondAssetMapper.selectCountByfinprodId(finprodId);
    }

    /**
     * 校验 一级市场投标债券 不重复
     * @param oFinprodId
     * @return
     */
    public int selectCountByOFinprodId(String oFinprodId,String finprodId) {
        return finBondAssetMapper.selectCountByOFinprodId(oFinprodId,finprodId);
    }

    /**
     * 根据资产代码查询是否是上市标的
     *
     * @param finprodId
     * @return 是否是上市标的，大于0则为是
     * @see
     * @since 1.0
     */
    public int selectIsIssueAsset(String finprodId){
        return finBondAssetMapper.selectIsIssueAsset(finprodId);
    }


    /**
     * 查询业务表资产信息
     *
     * @param finprodMarketId 资产代码
     * @return 资产付息等现金流信息
     * @see
     * @since 1.0
     */
    public FinBondAssetSaveRequest getFinPlanList(String finprodMarketId) {
        FinBondAssetSaveRequest dto =new FinBondAssetSaveRequest();
        String finprodId = commonBusinessService.getFinProdId(finprodMarketId,DictCodeListExt1.金融产品类型_投管用_债券.getValue(),null);
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto =new BaseFinProductBaseInfoDto();
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList =new ArrayList<BaseFinProductBaseInfoDto>();
        baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
        // 查询标的现金流信息list
        List<FinCashInfo> finCashInfolist = finBondAssetMapper.selectFinCashInfo(finprodId);
        // 查询标的现金流计划list
        List<FinCashPlan> finCashPlanList = finBondAssetMapper.getFinCashPlanList(finprodId);
        // 查询标的现金流支付规则
        List<FinCashPayRule> finCashPayRuleList = finBondAssetMapper.getFinCashPayRuleList(finprodId);
        //查询现金流计算规则
        List<FinCashCalcRule> finCashCalcRuleList = finBondAssetMapper.getTargetCashCalcRuleList(finprodId);
        //查询利率信息
        List<FinRateInfo> finRateInfoList = finBondAssetMapper.getFinRateInfoList(finprodId);
        baseFinProductBaseInfoDto.setFinCashPlanList(finCashPlanList);
        baseFinProductBaseInfoDto.setFinCashPayRuleList(finCashPayRuleList);
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(finCashCalcRuleList);
        baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfolist);
        baseFinProductBaseInfoDto.setFinRateInfoList(finRateInfoList);
        dto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return dto;
    }

    /**
     * 根据产品代码得到起息日和到期日
     * @param oFinprodId 产品代码
     * @return
     */
    public FinBondAssetSaveRequest getMdateAndVdate(String oFinprodId){
        FinBondAssetSaveRequest dto = new FinBondAssetSaveRequest();
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(oFinprodId);
        if(finProduct != null){
            dto.setMdate(finProduct.getMdate());
            dto.setVdate(finProduct.getVdate());
            dto.setoFinprodId(finProduct.getoFinprodId());
        }
        return dto;
    }

    /**
     * 处理履历
     */
    public void updateCfetsOprHis(String entrustId, String optType) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        oprHisReq.setOperType(DictCodeListExt3.操作类型_利率调整.getValue());
        if (ENTRUST_RECORD_INSERT.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        } else if (ENTRUST_RECORD_UPDATE.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
        } else if (ENTRUST_RECORD_DELETE.equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        } else if ("export".equals(optType)) {
            oprHisReq.setOperType(DictCodeListExt3.操作类型_导出.getValue());
        }
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);

    }



    /**
     * 计算初始利率
     * <功能详细描述>
     * @param scai
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getyieldByPriceCalculator(@RequestBody AssetCommonInfoRequest scai){
        BigDecimal yieldResult = BigDecimal.ZERO;
        YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
        YieldPriceInParameter input = new YieldPriceInParameter();
        input.setFullPrice(scai.getIssuePrice());
        input.setCalDate(DateUtil.dateToLocal(scai.getVdate()));
        //到期按节假日顺延
        String calendarId = StringUtil.isNotEmpty(scai.getCalendarId())?scai.getCalendarId():"01";
        List<String> holidayList = dateCalService.getDealCalendarHoliday(scai.getVdate(),scai.getMdate(),calendarId);
        String mdate =  AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(scai.getMdate()), DictCodeList.营业日准则_向后调整.getValue(), holidayList);
        SecInfoModel secInfoModel = new SecInfoModel();
        secInfoModel.setStartDate(DateUtil.dateToLocal(scai.getVdate()));
        secInfoModel.setEndDate(DateUtil.dateToLocal(DateUtil.parse(mdate)));
        if(StringUtil.equals(scai.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())){
            secInfoModel.setBondsType(Variety.零息);
        }else if(StringUtil.equals(scai.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue())){
            secInfoModel.setBondsType(Variety.贴现);
        }
        input.setSecInfoModel(secInfoModel);
        YieldPriceOutParameter parameter = yieldByPriceCalculator.calculate(input);
        if(null != parameter){
            yieldResult = parameter.getMatYield();
            yieldResult =  yieldResult.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return yieldResult;
    }

    /**
     * 计算初始利率
     * <功能详细描述>
     * @param scai
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPriceByYieldCalculator(@RequestBody AssetCommonInfoRequest scai){
        BigDecimal priceResult = BigDecimal.ZERO;
        PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
        PriceYieldInParameter input = new PriceYieldInParameter();
        input.setMatYield(scai.getRate());
        input.setCalDate(DateUtil.dateToLocal(scai.getVdate()));
        //到期按节假日顺延
        List<String> holidayList = dateCalService.getDealCalendarHoliday(scai.getVdate(),scai.getMdate(),scai.getCalendarId());
        String mdate =  AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(scai.getMdate()), DictCodeList.营业日准则_向后调整.getValue(), holidayList);
        SecInfoModel secInfoModel = new SecInfoModel();
        secInfoModel.setStartDate(DateUtil.dateToLocal(scai.getVdate()));
        secInfoModel.setEndDate(DateUtil.dateToLocal(DateUtil.parse(mdate)));
        if(StringUtil.equals(scai.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())){
            secInfoModel.setBondsType(Variety.零息);
        }else if(StringUtil.equals(scai.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue())){
            secInfoModel.setBondsType(Variety.贴现);
        }
        input.setSecInfoModel(secInfoModel);
        PriceYieldOutParameter parameter = priceByYieldCalculator.calculate(input);
        if(null != parameter){
            priceResult = parameter.getFullPrice();
            priceResult =  priceResult.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return priceResult;
    }


    public List<ValidMessage> checkGenerationPlan(BondFinPayInterestStdInfoRequest dto) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        if((StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                &&StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_附息.getValue()))){
            if(StringUtil.isEmpty(dto.getPayTdayRule())){
                resultList.add(new ValidMessage(
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payTdayRule"))));
            }
            if(StringUtil.isEmpty(dto.getPayCycle())){
                resultList.add(new ValidMessage(
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payCycle"))));
            }
            if(!StringUtil.equals(dto.getPayCycle(), DictCodeList.频率_不规则.getValue())){
                if(StringUtil.isEmpty(dto.getCalcRule())){
                    resultList.add(new ValidMessage(
                            getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.calcRule"))));
                }
            }
            if (StringUtil.isNotEmpty(dto.getCalcRule()) && StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方式_首次支付日.getValue())){
                if(StringUtil.isEmpty(dto.getCalcRule())){
                    resultList.add(new ValidMessage(
                            getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.firstPayDate"))));
                }
            }
        }else if(StringUtil.equals(dto.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())){
            if(StringUtil.isEmpty(dto.getPayTdayRule())){
                resultList.add(new ValidMessage(
                        getMessage("COMMON_E_0057", getMessage("FinPayInterestStdInfo.payTdayRule"))));
            }
        }
        if(StringUtil.equals(dto.getCalcRule(), DictCodeList.推算方式_首次支付日.getValue())){
            if(null == dto.getFirstPayDate()){
                resultList.add(new ValidMessage(
                        getMessage("BUSS_COM_E_0008",getMessage("FinPayInterestStdInfo.calcRule"),DictCodeList.推算方式_首次支付日.getCodeName(), getMessage("FinPayInterestStdInfo.firstPayDate"))));
            }
        }
        if (null!=dto.getFinInterestFactorDto() && StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getCalcType()) &&
           StringUtil.equals(dto.getFinInterestFactorDto().getCalcType(),DictCodeList.利率类型_浮动利率.getValue())){
            // 基准利率精度、基准利率、首次利率确定日
            if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getBenchmarkIdAccuracy())){
                resultList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0070", "基准利率精度")));
            }
            if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getBasicRate())){
                resultList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0070", "基准利率")));
            }
            if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getHighestRate())){
                resultList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0070", "利率上限")));
                return resultList;
            }
            if (!StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getLowestRate())){
                resultList.add(new ValidMessage("",
                        getMessage("BUSS_COM_E_0070", "利率下限")));
                return resultList;
            }
            if (dto.getFinInterestFactorDto().getLowestRate().compareTo(dto.getFinInterestFactorDto().getHighestRate())>0){
                resultList.add(new ValidMessage("利率下限必须小于利率上限"));
                return resultList;
            }
        }
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    public List<ValidMessage> valid(BondFinPayInterestStdInfoRequest dto) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        Set<Date> set = new  HashSet<Date>();
        BigDecimal prinAmtSum = new BigDecimal("0");
        //true  确认(重新计算利息) check
        if(Boolean.parseBoolean(dto.getIsAgainCalInt())){
            if(null!=dto.getInvScheduleList()&&!dto.getInvScheduleList().isEmpty()){
                for(FinCashPlan finCashPlanEntity:dto.getInvScheduleList()){
                    if(null==finCashPlanEntity.getVdate()){
                        resultList.add(new ValidMessage("",
                                getMessage("BUSS_COM_E_0069", getMessage("BussFinRepayment.vdate"))));
                        return resultList;
                    }
                    if(null==finCashPlanEntity.getMdate()){
                        resultList.add(new ValidMessage("",
                                getMessage("BUSS_COM_E_0069", getMessage("BussFinRepayment.mdate"))));
                        return resultList;
                    }else{
                        if(null!=dto.getVdate()){
                            if(DateUtil.compareTo(finCashPlanEntity.getMdate(), dto.getVdate())==-1
                                    ||DateUtil.compareTo(finCashPlanEntity.getMdate(), dto.getMdate())==1){
                                resultList.add(new ValidMessage("",
                                        getMessage("FIN_ASSET_E_0016")));
                            }
                        }
                    }
                    if(null==finCashPlanEntity.getPayDate()){
                        resultList.add(new ValidMessage("",
                                getMessage("BUSS_COM_E_0069", getMessage("BussFinRepayment.payDate"))));
                        return resultList;
                    }
                    if(DateUtil.compareDate(finCashPlanEntity.getVdate(),finCashPlanEntity.getMdate())>=0){
                        resultList.add(new ValidMessage(
                                "计息截止日不能小于等于计息开始日"));
                        return resultList;
                    }
                    if (StringUtil.isNotEmpty(dto.getMaxTradeDate())) {
                        if (DateUtil.compareDate(finCashPlanEntity.getVdate(), dto.getMaxTradeDate()) > 0) {
                            resultList.add(new ValidMessage(
                                    "新增计划计息开始日不可以小于交易日"));
                        }
                        if (DateUtil.compareDate(finCashPlanEntity.getMdate(), dto.getMaxTradeDate()) > 0) {
                            resultList.add(new ValidMessage( "新增计划计息结束日不可以小于交易日"));
                        }
                    }
                }
                Collections.sort(dto.getInvScheduleList(), new Comparator<BondFinCashPlanDto>() {
                    @Override
                    public int compare(BondFinCashPlanDto o1, BondFinCashPlanDto o2) {
                        return o1.getVdate().compareTo(o2.getVdate());
                    }
                });
                for (int i = 0; i < dto.getInvScheduleList().size()-1; i++) {
                    if (DateUtil.compareDate(dto.getInvScheduleList().get(i).getMdate(),dto.getInvScheduleList().get(i+1).getVdate())!=0){
                        resultList.add(new ValidMessage(
                                "付息计划不正确，上一条数据的计息截止日必须等于下一条数据的计息开始日"));
                        return resultList;
                    }
                }
                //第一条计息开始日==起息日
                Date firstVdateUnadjust = dto.getInvScheduleList().get(0).getVdate();
                //最后一条计息截止日==到期日
                Date lastMdateUnadjust = dto.getInvScheduleList().get(dto.getInvScheduleList().size()-1).getMdate();
                if(DateUtil.compareDate(firstVdateUnadjust,dto.getVdate())!=0){
                    resultList.add(new ValidMessage("",getMessage("FIN_ASSET_E_0010", getMessage("FinPayInterestStdInfo.asset.vdate"))));
                }
                if(DateUtil.compareDate(lastMdateUnadjust,dto.getMdate())!=0){
                    resultList.add(new ValidMessage("",getMessage("FIN_ASSET_E_0011", getMessage("FinPayInterestStdInfo.asset.mdate"))));
                }
            }
            if(null!=dto.getInvRepayList()&&!dto.getInvRepayList().isEmpty()){
                for(FinCashPlan finCashPlanEntity:dto.getInvRepayList()){
                    if(null==finCashPlanEntity.getPayDate()){
                        resultList.add(new ValidMessage("还本计划列表中支付日不能为空"));
                        return resultList;
                    }
                    if(null==finCashPlanEntity.getMdate()){
                        resultList.add(new ValidMessage("",
                                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.mdate"))));
                    }else{
                        if(null!=dto.getVdate()){
                            if(DateUtil.compareTo(finCashPlanEntity.getMdate(), dto.getVdate())==-1
                                    ||DateUtil.compareTo(finCashPlanEntity.getMdate(), dto.getMdate())==1){
                                resultList.add(new ValidMessage("",
                                        getMessage("FIN_ASSET_E_0016")));
                            }
                        }
                    }
                    //计息截止日不可重复
                    if(!set.add(finCashPlanEntity.getMdate())){
                        resultList.add(new ValidMessage("",
                                getMessage("FIN_ASSET_E_0009", getMessage("BussFinRepayment.mdate"))));
                    }
                    if(null!=finCashPlanEntity.getPrinAmt()){
                        prinAmtSum = prinAmtSum.add(new BigDecimal(finCashPlanEntity.getPrinAmt().toString()));
                    }else{
                        resultList.add(new ValidMessage("",
                                getMessage("COMMON_E_0057", getMessage("BussFinRepayment.prinAmt"))));
                        return resultList;
                    }
                    if(finCashPlanEntity.getPrinAmt().compareTo(new BigDecimal("0"))<=0 ||
                            finCashPlanEntity.getPrinAmt().compareTo(new BigDecimal("100"))>0){
                        resultList.add(new ValidMessage("还本面值请输入0以上，100以下的数据"));
                    }
                }
                //债券类资产(还本计划列表中的还本面额/百元之和必须==100)
                if(prinAmtSum.compareTo(new BigDecimal("100"))!=0){
                    resultList.add(new ValidMessage("还本计划中还本面值之和必须等于100"));
                }
            }
        }
        return resultList;
    }

    /**
     *
     */
    public AjaxResult issuecheck(String issuer) {
        AjaxResult  ajax = new AjaxResult();
        InterfCustomerInfoDto dto= finBondAssetMapper.selectInterfCustomerInfo(issuer);
        if (StringUtil.isNotEmpty(dto)){
            MstCustomerInfo mstCustomerInfo = mstCustomerInfoMapper.selectByPrimaryKey(dto.getCustomerId());
            if (StringUtil.isNotEmpty(mstCustomerInfo)){
                mstCustomerInfo.setIsIssuer(DictCodeList.是否_是.getValue());
                mstCustomerInfoMapper.updateByPrimaryKey(mstCustomerInfo);
                ajax.setMessage("发行人的客户性质已配置！");
                return ajax;
            }else{
                mstCustomerInfo = new MstCustomerInfo();
                mstCustomerInfo.setCustomerId(dto.getCustomerId());
                mstCustomerInfo.setCustomerAbbr(dto.getCustomerAbbr());
                mstCustomerInfo.setCustomerName(dto.getCustomerName());
                mstCustomerInfo.setCustomerType(DictCodeList.客户类型_法人.getValue());
                mstCustomerInfo.setIsIssuer(DictCodeList.是否_是.getValue());
                mstCustomerInfoMapper.insertSelective(mstCustomerInfo);
                MstCustomerInfoAddWithBLOBs  mstCustomerInfoAdd = new MstCustomerInfoAddWithBLOBs();
                mstCustomerInfoAdd.setCustomerId(dto.getCustomerId());
                BeanUtil.copyPropertiesIgnoreNull(mstCustomerInfoAdd,dto);
                mstCustomerInfoAddMapper.insertSelective(mstCustomerInfoAdd);
                ajax.setMessage("发行人已自动生成！");
                return ajax;
            }
        }
        return ajax;
    }

    public String getCalendarId(String tradeMarket){
        if (StringUtil.isNotEmpty(tradeMarket)){
            if (StringUtil.equals(tradeMarket,DictCodeListExt1.交易场所_银行间.getValue())){
                return "03";
            }else if (StringUtil.equals(tradeMarket,DictCodeListExt1.交易场所_上海证券交易所.getValue())){
                return "04";
            }else if (StringUtil.equals(tradeMarket,DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                return "05";
            }else{
                return "06";
            }
        }
        return null;
    }


    public SysUploadFileInfo getUploadFileInfo(MultipartFile file) throws IOException {
        if (!StringUtil.isNotEmpty(file.getOriginalFilename())){
            return null;
        }
        List<String> fileNameList = StringUtil.splitToList(".",file.getOriginalFilename());
        String newfileName = fileNameList.get(0)+"_"+DateUtil.format(DateUtil.now(),DateUtil.DATETIME_FORMAT_WITH_SSS_YEAR1)+"."+fileNameList.get(1);
        // 本地路径
        String localPath = CacheUtil.getSysParam("upload_file_path") + File.separator;
        String nowDateStr =DateUtil.format(DateUtil.now(),DateUtil.DEFAULT_DATE_FORMAT_EN);
        List<String> nowDateList = StringUtil.splitToList("-",nowDateStr);
        String newPath = localPath + nowDateList.get(0) + File.separator
                + nowDateList.get(1)  + File.separator + nowDateList.get(2)+ File.separator;
        File newPathFile = new SafelyFile(newPath);
        //  生成文件夹
        if (!newPathFile.exists()){
            newPathFile.mkdir();
        }
        File folder = new SafelyFile(newPathFile,newfileName);
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
        finBondAssetMapper.insertFileSelective(fileInfo);
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

    public int isTransInAsset(String finProdId) {
        return finBondAssetMapper.isTransInAsset(finProdId);
    }

    public Date getMaxTradeDate(String finprodId) {
        return finBondAssetMapper.getMaxTradeDate(finprodId);
    }

    public boolean isInWflProcess(WflValidInputParameter parameter) {
        parameter.setSeachMode("01");
        List<String> result = finBondAssetMapper.getBusinessData(parameter);
        if (CollectionUtils.isNotEmpty(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Date getDealCalendarHoliday(SelectOptionCommonRequest request) {
        String calendarId = this.getCalendarId(request.getTradeMarket());
        Date date = null;
        boolean dealCalendarHoliday = dateCalService.isDealCalendarHoliday(request.getSearchDate(), calendarId);
        if (!dealCalendarHoliday){
            date = request.getSearchDate();
        }else{
            while (true){
                date =DateUtil.addDays(request.getSearchDate(),1);
                dealCalendarHoliday = dateCalService.isDealCalendarHoliday(date, calendarId);
                if (!dealCalendarHoliday){
                    break;
                }
            }
        }
        return date;
    }
}
