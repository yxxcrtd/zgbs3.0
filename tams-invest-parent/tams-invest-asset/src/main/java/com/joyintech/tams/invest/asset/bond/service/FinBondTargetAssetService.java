/*
 * 文件名：FinBondTargetAssetService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.bond.service;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.asset.BussFinRepaymentService;
import com.joyintech.tams.component.business.asset.FinInterestFactorService;
import com.joyintech.tams.component.business.asset.FinPayInterestStdInfoService;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.component.business.bond.BusiFinBondRatingInfoService;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.bond.dto.BusiFinBondRatingInfoDto;
import com.joyintech.tams.component.business.bond.dto.BusiFinExercisePlanDto;
import com.joyintech.tams.component.business.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.component.business.common.service.CommonAutoCouponService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.core.service.MstFileCommonService;
import com.joyintech.tams.component.business.report.asset.bond.BussFinBondRepInfoService;
import com.joyintech.tams.component.business.report.asset.project.BussRepEquityInfoService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.tams.invest.asset.common.dto.*;
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 债券标的信息新建
 * @author zhangnian
 * @version 1.0 2018年9月20日
 * @see FinBondTargetAssetService
 * @since 1.0
 */
@Service
public class FinBondTargetAssetService extends CommonExportService<FinBondTargetAssetDto>
implements BaseProcessService<FinBondTargetAssetDto>{
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
     * 业务组件--计息要素
     */
    @Autowired(required = false)
    private FinInterestFactorService finInterestFactorService;

    /**
     * 业务--付息信息
     */
    @Autowired(required = false)
    private FinPayInterestStdInfoService finPayInterestStdInfoService;

    /**
     * 业务--还本信息
     */
    @Autowired(required = false)
    private BussFinRepaymentService bussFinRepaymentService;
    /**
     * 业务--行权计划
     */
    @Autowired(required = false)
    private BusiFinExercisePlanService busiFinExercisePlanService;

    /**
     * 业务--评级信息
     */
    @Autowired(required = false)
    private BusiFinBondRatingInfoService busiFinBondRatingInfoService;
    /**
     * 业务--中债报送
     */
    @Autowired(required = false)
    private BussFinBondRepInfoService bussFinBondRepInfoService;
    /**
     * 权益类--中债报送
     */
    @Autowired(required = false)
    private BussRepEquityInfoService bussRepEquityInfoService;
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
    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam pageParam
     * @return Page类 Page
     * @throws Exception 异常信息
     */
    public Page<FinBondTargetAssetDto> selectForPage(Map<String, Object> pageParam) {
        pageParam.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        pageParam.put("cashType",DictCodeListExt1.现金流二级类型_利息.getValue());
        pageParam.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());//现金流业务类型
        if (StringUtil.isNotEmpty(pageParam.get("type4"))) {
            pageParam.put("type4", StringUtil.splitToList(null, pageParam.get("type4").toString()));
        }else{
            pageParam.remove("type4");
        }
        // type9-11 created by wangguiqi on 2019/7/19 -start
        if (StringUtil.isNotEmpty(pageParam.get("type9"))) {
            pageParam.put("type9", StringUtil.splitToList(null, pageParam.get("type9").toString()));
        }else{
            pageParam.remove("type9");
        }
        if (StringUtil.isNotEmpty(pageParam.get("type10"))) {
            pageParam.put("type10", StringUtil.splitToList(null, pageParam.get("type10").toString()));
        }else{
            pageParam.remove("type10");
        }
        if (StringUtil.isNotEmpty(pageParam.get("type11"))) {
            pageParam.put("type11", StringUtil.splitToList(null, pageParam.get("type11").toString()));
        }else{
            pageParam.remove("type11");
        }
        //添加是否新增债券查询条件 add by liupeilin 2020-01-15 begin
        if(!pageParam.containsKey("isFileter")){
            pageParam.put("isFileter", "");
        }
        pageParam.put("platDate", DateUtil.format(DateUtil.platDateFromCache()));
        //添加是否新增债券查询条件 add by liupeilin 2020-01-15 end
        Page<FinBondTargetAssetDto> page = restTemplate.postForPage(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/findPage",
                pageParam, FinBondTargetAssetDto.class);
        // type9-11 created by wangguiqi on 2019/7/19 -end
        List<FinBondTargetAssetDto> finBondTargetAssetDtoList = page.getRoot();
        List<FinBondTargetAssetDto> finBondTargetAssetDtoListTemp = new ArrayList<>();
        for(FinBondTargetAssetDto dto : finBondTargetAssetDtoList ){
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("oCode", dto.getIssuer());
            param.put("oType", DictCodeList.评级类型_主体评级.getValue());
            //查询评级类型为主体评级的数据
            List<BusiFinProductRatingDto> ztList = busiProductRatingService.getZtBestRatingByOrgId(param);
            if(ztList != null && ztList.size()>0){
                //只取第一条数据
                dto.setZtGradeOrgId(ztList.get(0).getGradeOrgId());
                dto.setZtGradeResult(ztList.get(0).getGradeResult());
            }
            Map<String,Object> zxparam = new HashMap<String,Object>();
            zxparam.put("oCode", dto.getFinprodMarketId());
            zxparam.put("oType", DictCodeList.评级类型_债项评级.getValue());
            //查询评级类型是债项评级的数据
            List<BusiFinProductRatingDto> zxList = busiProductRatingService.getZxBestRatingByMarketId(zxparam);
            if(zxList != null && zxList.size()>0){
                //只取第一条数据
                dto.setZxGradeOrgId(zxList.get(0).getGradeOrgId());
                dto.setZxGradeResult(zxList.get(0).getGradeResult());
            }
            finBondTargetAssetDtoListTemp.add(dto);
        }
        page.setRoot(finBondTargetAssetDtoListTemp);
        return page;
    }

    /**
     * 工作流（留痕操作结束的时候执行的方法）
     */
    @Override
    public void doComplete(FinBondTargetAssetDto entity, OperationType type) {
        String action = StringUtil.EMPTY;
        entity = encapBaseFinProductBaseInfoList(entity);
        String basis = StringUtil.EMPTY;
        String basisStyle = entity.getFinInterestFactorDto().getBasisStyle();
        if(StringUtil.equals(basisStyle, DictCodeListExt1.利息分配方式_平均分配.getValue())){
            basis = DictCodeList.计息基础_A_A_平均分配.getValue();
        }else if(StringUtil.equals(basisStyle, DictCodeListExt1.利息分配方式_按照实际天数分配.getValue())){
            basis = DictCodeList.计息基础_A_A_按实际天数分配.getValue();
        }else{
            basis = entity.getFinInterestFactorDto().getBasis();
        }
        for(BaseFinProductBaseInfoDto baseFinProductBaseInfoEntity:entity.getBaseFinProductBaseInfoList()){
            for(FinRateInfo finRateInfo:baseFinProductBaseInfoEntity.getFinRateInfoList()){
                finRateInfo.setBasis(basis);
            }
            for(FinCashCalcRule finCashCalcRule:baseFinProductBaseInfoEntity.getFinCashCalcRuleList()){
                finCashCalcRule.setBasis(basis);
            }
        }
        // add by lizhi 判断系统参数ASSET_INFO_CHB_HIDE是否显示中债信息 2020-2-28
        if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE), DictCodeListExt1.是或否_否.getValue())){
            // 资产负债报送信息的市场代码和金融产品名称设值
            if (entity.getBaseRepAssetDeptInfoDto()!=null && entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList() != null && entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList().size() > 0) {
                entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList().get(0).setFinprodMarketId(entity.getFinprodMarketId());
                entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList().get(0).setRepFinprodName(entity.getFinprodName());
            }
        }
        if (OperationType.DELETE.equals(type)) {
            action = "delete";
        }
        else if (OperationType.NEW.equals(type)) {
        	//新建债券类资产时，自动创建交易对手 add by hxy 20200421 start
    		assetCommonService.autoCreateTradeCounter(entity.getIssuer());
    		//新建债券类资产时，自动创建交易对手 add by hxy 20200421 end
            action = "insert";
            //从待办中进入的无法判断是否新建还是编辑
            if(selectCountByfinprodId(DictCodeList.平安资产类型_债券.getValue() +
                    "_" + entity.getFinprodMarketId())>0){
                action = "update";
            }
        }
        else {
            action = "update";
        }

        // 一体化对应etliu 现金流支付规则表，支付频率没存值，可能有问题
        if (!StringUtil.isEmptyList(entity.getBaseFinProductBaseInfoList())) {
        	for (BaseFinProductBaseInfoDto productBaseInfoDto : entity.getBaseFinProductBaseInfoList()) {
				if (!StringUtil.isEmptyList(productBaseInfoDto.getFinCashPayRuleList())) {
					for (FinCashPayRule finCashPayRule : productBaseInfoDto.getFinCashPayRuleList()) {
						if (StringUtil.isBlank(finCashPayRule.getPayCycle())) {
                            //modify by jiangpeng on 20210426 TISHKBS-2051 CLONE - 贴现债数据错误
							finCashPayRule.setPayCycle(BussinessDictCodeList.频率_到期一次.getValue());
						}
					}
				}
			}
        }

        //add by mashukai 20201124 JNZGEQUAT-57 wind利率调整数据未接到问题 start
        if(entity.getFinCashCalcRuleList().size() > 0){
            entity.getBaseFinProductBaseInfoList().get(0).setFinCashCalcRuleList(entity.getFinCashCalcRuleList());
        }
        //add by mashukai 20201124 JNZGEQUAT-57 wind利率调整数据未接到问题 end
        restTemplate.postForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/" + action, entity,
            Integer.class);

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
        }
        catch (Exception e)
        {
            //功能暂未测试
        }
    }

    /**
     * 获取基本信息--修改/详细
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinBondTargetAssetDto getBasicInfos(String finprodId, String finprodType){
        if(StringUtils.isNotEmpty(finprodType)){
            return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
                            + "/FinBondTargetAsset/getBasicInfo?finprodId="
                            + finprodId+"&finprodType="+finprodType,
                    FinBondTargetAssetDto.class);
        }
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
                        + "/FinBondTargetAsset/getBasicInfo?finprodId="
                        + finprodId,
                FinBondTargetAssetDto.class);
    }

    /**
     * 获取基本信息--修改/详细
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinBondTargetAssetDto getBasicInfo(String finprodId){
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
            + "/FinBondTargetAsset/getBasicInfo?finprodId="
            + finprodId,
            FinBondTargetAssetDto.class);
    }

    /**
     * 封装基础组件-标的类金融产品需要的DTO
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public FinBondTargetAssetDto encapBaseFinProductBaseInfoList(FinBondTargetAssetDto dto){
        Long branch =StringUtil.isEmpty(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE))?0:Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        //现金流信息
        List<FinCashInfo> finCashInfoList = new ArrayList<FinCashInfo>();
        //现金流支付计划
        List<FinCashPlan> finCashPlanList = new ArrayList<FinCashPlan>();
        //标的类金融产品entity
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct, dto);
        //期限
        finProduct.setTermDays(DateUtil.getBetweenDayNumber(dto.getVdate(),dto.getMdate()));
        finProduct.setIssueAmt(ArithmeticUtil.multiply(dto.getIssueAmt(),new BigDecimal("10000")));
        /*if(null!=bussFinBondRepInfoService){
            if(null!=dto.getBussFinBondRepInfoDto()){
                finProduct.setRegistOrg(dto.getBussFinBondRepInfoDto().getRepRegistOrg());
            }
        }*/
        // 一体化对应etliu start
        // 债券新建画面，入库时，标的类金融产品表的含权债摊销日字段，当含权债时，登数据字典【含权债摊销日.最近行权日】
        if (StringUtil.equals(DictCodeList.是否_是.getValue(), finProduct.getIsRight())) {
            finProduct.setExeDate(CacheUtil.getSysParam("ISSECTOEXEDATE") == null ?
                    DictCodeList.含权债摊销日_最近行权日.getValue() : CacheUtil.getSysParam("ISSECTOEXEDATE"));
        }
        // 实际到期日:先存成到期日
        finProduct.setActmdate(finProduct.getMdate());
        // 一体化对应etliu end

        //金融产品分类表entity
        FinProductType finProductType = new FinProductType();
        BeanUtil.copyPropertiesIgnoreNull(finProductType, dto);finProductType.setBranch(branch);
        finProductType.setType6(dto.getType4());finProductType.setType7(dto.getType5());
        finProductType.setType8(DictCodeList.平安资产类型_债券.getValue());
        //金融产品附属表entity
        FinProductAdd finProductAdd = new FinProductAdd();
        BeanUtil.copyPropertiesIgnoreNull(finProductAdd, dto);
        finProductAdd.setIsPpb(dto.getIsPPB());
        //----add by jjpei 20190727 添加投资性质----start----
        if(StringUtil.isNotBlank(dto.getType4())&&DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue().equals(dto.getType4())){
            finProductAdd.setInvestNature(DictCodeList.投资性质_权益类.getValue());
        }
        else{
            finProductAdd.setInvestNature(DictCodeList.投资性质_固定收益类.getValue());
        }
        //----add by jjpei 20190727 添加投资性质----end----
        //现金流信息Entity-本金
        FinCashInfo finCashInfoPrincipal = encapFinCashInfo(dto,DictCodeListExt1.现金流一级类型_本金.getValue(),DictCodeListExt1.现金流二级类型_本金.getValue(),branch);
        //现金流信息Entity-利息
        FinCashInfo finCashInfoInterest = encapFinCashInfo(dto,DictCodeListExt1.现金流一级类型_利息.getValue(),DictCodeListExt1.现金流二级类型_利息.getValue(),branch);
        finCashInfoList.add(finCashInfoPrincipal);finCashInfoList.add(finCashInfoInterest);
        //计息要素DTO 现金流利率信息  现金流计算规则
        if(null!=finInterestFactorService){
            if(null!=dto.getFinInterestFactorDto()){
                FinInterestFactorDto finInterestFactorDto = finInterestFactorService.getFinInterestFactorDto(dto.getFinInterestFactorDto());
                finInterestFactorDto = assetCommonService.encapFinInterestFactor(finProduct,finInterestFactorDto,finCashInfoInterest,null, branch);
                //息票品种
                finProduct.setCouponSpecies(finInterestFactorDto.getCouponSpecies());
                List<FinRateInfo> finRateInfoList = new ArrayList<FinRateInfo>();
                finRateInfoList.addAll(finInterestFactorDto.getRateList());
                List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<FinCashCalcRule>();
                finCashCalcRuleList.addAll(finInterestFactorDto.getCalcRuleList());
                baseFinProductBaseInfoDto.setFinRateInfoList(finRateInfoList);
                baseFinProductBaseInfoDto.setFinCashCalcRuleList(finCashCalcRuleList);
            }
        }

        //付息信息DTO 现金流支付规则 现金流支付支付计划
        if(null!=finPayInterestStdInfoService){
            FinPayInterestStdInfoDto finPayInterestStdInfoDto = finPayInterestStdInfoService.getFinPayInterestStdInfoDto(dto.getFinPayInterestStdInfoDto());
            finPayInterestStdInfoDto = assetCommonService.encapFinPayInterestStdInfo(finProduct,finPayInterestStdInfoDto,finCashInfoPrincipal, finCashInfoInterest, branch);
            baseFinProductBaseInfoDto.setFinCashPayRuleList(finPayInterestStdInfoDto.getFinCashPayRuleList());
            finCashPlanList.addAll(finPayInterestStdInfoDto.getFinCashPlan2List());
        }
        //还本信息DTO 现金流支付支付计划
        if(null!=bussFinRepaymentService){
            BussFinRepaymentDto bussFinRepaymentDto = assetCommonService.encapBussFinRepayment(finProduct, dto.getBussFinRepaymentDto(), finCashInfoPrincipal, branch);
            finCashPlanList.addAll(bussFinRepaymentDto.getFinCashPlan2List());
        }
        //行权计划DTO 行权计划
        if(null!=busiFinExercisePlanService){
            if(null!=dto.getBusiFinExercisePlanDto()){
                BusiFinExercisePlanDto busiFinExercisePlanDto= assetCommonService.encapBusiFinExercisePlan(finProduct, dto.getBusiFinExercisePlanDto(), finCashInfoPrincipal, branch);
                baseFinProductBaseInfoDto.setFinExercisePlanList(busiFinExercisePlanDto.getFinExercisePlanList());
            }
        }
        //评级信息
        //主体评级
        String gradeResult = "";
        if(null!=busiFinBondRatingInfoService){
            if(null!=dto.getBusiFinBondRatingInfoDto()){
                BusiFinBondRatingInfoDto busiFinBondRatingInfoDto= assetCommonService.encapBusiFinBondRatingInfo(finProduct, dto.getBusiFinBondRatingInfoDto());
                dto.setBusiFinBondRatingInfoDto(busiFinBondRatingInfoDto);
                //主体评级
                if (StringUtil.isNotEmpty(busiFinBondRatingInfoDto)){
                    for (MstBondCreditRating m:busiFinBondRatingInfoDto.getMstBondCreditRatingList()) {
                        if (StringUtil.equals(m.getoType(), DictCodeList.评级类型_主体评级.getValue())){
                            gradeResult = m.getGradeResult();
                            break;
                        }
                    }
                }
                
                
            }
        }
        // 权益类资产中债报送
        if (null != bussRepEquityInfoService) {
            if (StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
            ||StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue())) {
                if (null != dto.getBussRepEquityInfoDto()) {
                    BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto = assetCommonService.encapBaseRepAssetEquityInfoDto(
                            finProduct, dto.getBussRepEquityInfoDto());
                    RepAssetOther repAssetOther = new RepAssetOther();
                    BeanUtil.copyPropertiesIgnoreNull(repAssetOther, dto.getBussRepEquityInfoDto());
                    BaseRepOtherDto baseRepOtherDto = assetCommonService.encapBaseRepOtherDto(finProduct, repAssetOther);
                    dto.setBaseRepOtherDto(baseRepOtherDto);
                    // 负债
                    BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = assetCommonService.encapbaseRepAssetDeptInfoDto(finProduct,
                            dto.getBussRepEquityInfoDto().getRepAssetType(), dto.getBussRepEquityInfoDto().getRepTradeMarket());
                    dto.setBaseRepAssetEquityInfoDto(baseRepAssetEquityInfoDto);
                    dto.setBaseRepAssetDeptInfoDto(baseRepAssetDeptInfoDto);
                }
            }
            else {
                dto.setBaseRepAssetEquityInfoDto(null);
            }
        }
        //中债报送
        if(null!=bussFinBondRepInfoService){
            if (!StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
            &&!StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue())) {
                if(null!=dto.getBussFinBondRepInfoDto()){
                    //债券资产报送
                    BaseRepBondAssetInfoDto baseRepBondAssetInfoDto = assetCommonService.encapBaseRepBondAssetInfoDto(finProduct, dto.getBussFinBondRepInfoDto());
                    //负债
                    BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = assetCommonService.encapbaseRepAssetDeptInfoDto(finProduct,dto.getBussFinBondRepInfoDto().getRepAssetType(),dto.getBussFinBondRepInfoDto().getRepTradeMarket());
//                    if(StringUtil.equals(dto.getBussFinBondRepInfoDto().getRepPartyType1(), DictCodeListExt3.发行机构类型_按规模划分_中小微企业.getValue())){
                        RepAssetOther repAssetOther = new RepAssetOther();
                        BeanUtil.copyPropertiesIgnoreNull(repAssetOther,dto.getBussFinBondRepInfoDto());
                        BaseRepOtherDto baseRepOtherDto = assetCommonService.encapBaseRepOtherDto(finProduct, repAssetOther);
                        dto.setBaseRepOtherDto(baseRepOtherDto);
//                    }
                    //主体评级
                    if (StringUtil.isNotEmpty(baseRepBondAssetInfoDto.getRepAssetBondList())){
                        for (RepAssetBond r : baseRepBondAssetInfoDto.getRepAssetBondList()){
                            r.setRepMainGrade(gradeResult);
                        }
                    }
                    dto.setBaseRepAssetDeptInfoDto(baseRepAssetDeptInfoDto);
                    dto.setBaseRepBondAssetInfoDto(baseRepBondAssetInfoDto);
                }
            }
        }
        //附件信息
        if(null!=fileCommonService){
            if(null!=dto.getFileCommonList()){
                FileCommonDto fileCommonDto = assetCommonService.encapFileCommon(finProduct,dto.getFileCommonList());
                dto.setMstFileInputParameter(fileCommonDto.getMstFileInputParameter());
            }
        }
        //转债信息
        if(StringUtil.equals(dto.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
                ||StringUtil.equals(dto.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue())){
            dto.getFinProductConvertible().setFinprodId(dto.getFinprodId());
        }
        baseFinProductBaseInfoDto.setFinProduct(finProduct);
        baseFinProductBaseInfoDto.setFinProductType(finProductType);
        baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
        baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfoList);
        baseFinProductBaseInfoDto.setFinCashPlanList(finCashPlanList);
        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList = new ArrayList<BaseFinProductBaseInfoDto>();
        baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
        dto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return dto;
    }

    /**
     * 组装现金流信息
     * @see
     * @since 1.0
     */
    public FinCashInfo encapFinCashInfo(FinBondTargetAssetDto dto,String cashType1,String cashType2,Long branch){
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
    public FinBondTargetAssetDto getFinprodId(FinBondTargetAssetDto dto){
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
        return restTemplate.getForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/selectCountByfinprodMarketId?finprodMarketId="+finprodMarketId,
            Integer.class);
    }

	@Override
	protected void buildExportParam(ExportParam exportParam) {
		exportParam.getSelectParam().put("finprodType2", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
		String isShowType = (String) exportParam.getSelectParam().get("isShowType");
		if (StringUtil.equals(isShowType, "01")) {
			exportParam.config("/com/joyintech/tams/invest/asset/bond/conf/export_conf.xml")
					.dataSource(DataSourceType.MASERT);
		} else {
			exportParam.config("/com/joyintech/tams/invest/asset/bond/conf/export_conf002.xml")
					.dataSource(DataSourceType.MASERT);
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<FinBondTargetAssetDto> selectPageData(Map<String, Object> selectParam) {
        return selectForPage(selectParam);
    }

    /**
     * 查询接口表资产信息
     * <功能详细描述>
     * @param finprodMarketId
     * @return
     * @see
     * @since 1.0
     */
    public FinBondTargetAssetDto getInterfBondDto(String finprodMarketId) {
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
        FinBondTargetAssetDto bondTargetDto = new FinBondTargetAssetDto();
        //金融产品基本信息表
        FinProduct finProduct =  baseInfoDto.getFinProduct();
        if (StringUtil.isNotEmpty(finProduct)) {
            BeanUtil.copyPropertiesIgnoreNull(bondTargetDto,finProduct);
        }
        bondTargetDto.setRepAssetType(baseInfoDto.getRepAssetType());
        //类型
        if (StringUtil.isNotEmpty(baseInfoDto.getFinProductType())) {
            bondTargetDto.setType4(baseInfoDto.getFinProductType().getType4());
            bondTargetDto.setType5(baseInfoDto.getFinProductType().getType5());
			bondTargetDto.setType9(baseInfoDto.getFinProductType().getType9());
			bondTargetDto.setType10(baseInfoDto.getFinProductType().getType10());
			bondTargetDto.setType11(baseInfoDto.getFinProductType().getType11());
			bondTargetDto.setType12(baseInfoDto.getFinProductType().getType12());	 
			
		}
        //附属表
        if (StringUtil.isNotEmpty(baseInfoDto.getFinProductAdd())) {
            bondTargetDto.setIsAutoUpdate(baseInfoDto.getFinProductAdd().getIsAutoUpdate());
            // 20200819 债券快录添加是否城投债以及城投债级别 wangwei start
            bondTargetDto.setCityBondLev(baseInfoDto.getFinProductAdd().getCityBondLev());
            bondTargetDto.setIsCityBond(baseInfoDto.getFinProductAdd().getIsCityBond());
            // 20200819 债券快录添加是否城投债以及城投债级别 wangwei end

            bondTargetDto.setIsNetprice(baseInfoDto.getFinProductAdd().getIsNetprice());
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
            FinInterestFactorDto finInterestFactorDto = new FinInterestFactorDto();
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
            FinPayInterestStdInfoDto finPayInterestStdInfoDto = new FinPayInterestStdInfoDto();
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
                List<FinCashPlanDto> payInvestDtos = new ArrayList<>();
                for (FinCashPlan finCashPlan : finCashPlanList) {
                    if (BussinessDictCodeList.现金流类型_中间还本.getValue().equals(finCashPlan.getCashType())
                    || BussinessDictCodeList.现金流类型_到期还本.getValue().equals(finCashPlan.getCashType())){
                        FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                        BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto,finCashPlan);
                        repPayDtos.add(finCashPlanDto);
                        
                    }else{
                    	if (!DictCodeList.息票品种_贴现.getValue().equals(bondTargetDto.getCouponSpecies())
                    			&& !DictCodeList.息票品种_零息.getValue().equals(bondTargetDto.getCouponSpecies())) {
                            FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                            BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto,finCashPlan);
                            finCashPlanDto.setRangeYield(ArithmeticUtil.multiply(finCashPlanDto.getRangeYield(), new BigDecimal(100)));
                            payInvestDtos.add(finCashPlanDto);
                    	}
                    }
                }
                bussFinRepaymentDto.setFinCashPlanList(repPayDtos);
                finPayInterestStdInfoDto.setFinCashPlanList(payInvestDtos);
            }

            bondTargetDto.setFinPayInterestStdInfoDto(finPayInterestStdInfoDto);
            bondTargetDto.setBussFinRepaymentDto(bussFinRepaymentDto);
        }

        // 行权计划
        BusiFinExercisePlanDto busiFinExercisePlanDto = new BusiFinExercisePlanDto();
        busiFinExercisePlanDto.setFinExercisePlanList(baseInfoDto2.getFinExercisePlanList());
        bondTargetDto.setBusiFinExercisePlanDto(busiFinExercisePlanDto);
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
    public FinBondTargetAssetDto getBondDto(String finprodMarketId) {
        //债券基本信息
        FinBondTargetAssetDto bondTargetDto = getBasicInfo(finprodMarketId);
        if (bondTargetDto == null) {
            return bondTargetDto;
        }
        bondTargetDto = restTemplate.postForObject(
            ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/searchData",
            bondTargetDto, FinBondTargetAssetDto.class);

        if (null != fileAttachmentProcess && StringUtil.isNotEmpty(bondTargetDto.getFinprodId())) {
            // 快录时，不默认带出附件信息
//            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(
//                bondTargetDto.getFinprodId());
//            bondTargetDto.setFileArray(files);
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
        // 中债报送信息
        if (bondTargetDto.getBaseRepBondAssetInfoDto() != null && !StringUtil.isEmptyList(
            bondTargetDto.getBaseRepBondAssetInfoDto().getRepAssetBondList())) {
            bondTargetDto.setRepAssetBond(
                bondTargetDto.getBaseRepBondAssetInfoDto().getRepAssetBondList().get(0));
        }
        //权益类报送信息
        if (bondTargetDto.getBaseRepAssetEquityInfoDto() != null && !StringUtil.isEmptyList(
                bondTargetDto.getBaseRepAssetEquityInfoDto().getRepAssetEquityList())) {
            bondTargetDto.setRepAssetEquity(
                    bondTargetDto.getBaseRepAssetEquityInfoDto().getRepAssetEquityList().get(0));
        }
        //其他报送信息
        if (bondTargetDto.getBaseRepOtherDto() != null && !StringUtil.isEmptyList(
                bondTargetDto.getBaseRepOtherDto().getRepAssetOtherList())) {
            bondTargetDto.setRepAssetOther(
                    bondTargetDto.getBaseRepOtherDto().getRepAssetOtherList().get(0));
        }
        if (bondTargetDto.getBaseRepAssetDeptInfoDto() != null && !StringUtil.isEmptyList(
            bondTargetDto.getBaseRepAssetDeptInfoDto().getRepAssetDeptList())) {
            bondTargetDto.setRepAssetDept(
                bondTargetDto.getBaseRepAssetDeptInfoDto().getRepAssetDeptList().get(0));
        }
        // 现金流计算规则
        // 根据主键获取业务实体--付息信息
        FinPayInterestStdInfoDto finPayInterestStdInfoDto = finPayInterestStdInfoService.getFinPayInterestStdInfoByProductId(
            bondTargetDto.getFinprodId(), bondTargetDto.getvdateStr(),
            DictCodeListExt1.现金流二级类型_利息.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue());
        bondTargetDto.setFinPayInterestStdInfoDto(finPayInterestStdInfoDto);
        // 现金流支付规则
        BussFinRepaymentDto bussFinRepaymentDto = new BussFinRepaymentDto();
        finPayInterestStdInfoDto = finPayInterestStdInfoService.getFinPayInterestStdInfoByProductId(
            bondTargetDto.getFinprodId(), bondTargetDto.getvdateStr(),
            DictCodeListExt1.现金流二级类型_本金.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue());
        if (null != finPayInterestStdInfoDto) {
            bussFinRepaymentDto.setPayCycle(finPayInterestStdInfoDto.getPayCycle());
            bussFinRepaymentDto.setRemark(finPayInterestStdInfoDto.getRemark());
            bondTargetDto.setBussFinRepaymentDto(bussFinRepaymentDto);
        }
        // 行权计划
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", bondTargetDto.getFinprodId());
        param.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
        param.put("execType01", DictCodeList.行权状态_未行权.getValue());
        param.put("execType02", DictCodeList.行权状态_已行权.getValue());
        List<FinExercisePlan> finExercisePlanList = busiFinExercisePlanService.getfinExercisePlanByProductId(
            param);
        BusiFinExercisePlanDto busiFinExercisePlanDto = new BusiFinExercisePlanDto();
        busiFinExercisePlanDto.setFinExercisePlanList(finExercisePlanList);
        bondTargetDto.setBusiFinExercisePlanDto(busiFinExercisePlanDto);
        if (bondTargetDto != null && bondTargetDto.getFinInterestFactorDto() != null) {
            FinInterestFactorDto finInterestFactorDto = bondTargetDto.getFinInterestFactorDto();
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

    /**
     * 获取新建界面的以及市场投标债券代码联想下拉框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.ASSET_SERVICE_NAME
                                       + "/FinBondTargetAsset/getfinprodIdForAdd"
                                       , param,
            SelectOptionDto.class);

	}

	public int isHqTrade(String finprodId) {
		return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/isHqTrade?finprodId=" + finprodId,
	            Integer.class);
	}
    /**
     * 校验金融产品代码不重复
     * @param finprodId
     * @return
     */
    public int selectCountByfinprodId(String finprodId) {
        return restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/selectCountByfinprodId?finprodId="+finprodId,
                Integer.class);
    }

    /**
     * 校验 一级市场投标债券 不重复
     * @param oFinprodId
     * @return
     */
    public int selectCountByOFinprodId(String oFinprodId,String finprodId) {
        return restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/selectCountByOFinprodId?oFinprodId="+oFinprodId+"&finprodId="+finprodId,
                Integer.class);
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
        return restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/selectIsIssueAsset?finprodId="+finprodId,
                Integer.class);
    }


    /**
     * 查询业务表资产信息
     *
     * @param finprodId 资产代码
     * @return 资产付息等现金流信息
     * @see
     * @since 1.0
     */
    public FinBondTargetAssetDto getFinPlanList(String finprodId) {
        return restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/getFinPlanList?finprodId="+finprodId,
                FinBondTargetAssetDto.class);
    }



    /**
     * 根据产品代码得到起息日和到期日
     * @param oFinprodId 产品代码
     * @return
     */
    public FinBondTargetAssetDto getMdateAndVdate(String oFinprodId){
        return restTemplate.getForObject(
                ServiceApplication.ASSET_SERVICE_NAME + "/FinBondTargetAsset/getMdateAndVdate?oFinprodId="+oFinprodId,
                FinBondTargetAssetDto.class);
    }

}
