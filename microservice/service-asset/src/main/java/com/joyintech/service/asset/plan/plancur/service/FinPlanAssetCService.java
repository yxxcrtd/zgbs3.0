/*
 * 文件名：FinPlanAssetCService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.plancur.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.common.dao.AssetCommonMapper;
import com.joyintech.service.asset.common.service.AssetCommonService;
import com.joyintech.service.asset.plan.plancur.dao.FinPlanAssetCMapper;
import com.joyintech.service.asset.plan.plancur.dto.BussParticipantInfoDto;
import com.joyintech.service.asset.plan.plancur.dto.BussRepAssetMngProdInfoDto;
import com.joyintech.service.asset.plan.plancur.dto.FinPlanAssetCDto;
import com.joyintech.tams.common.base.dao.AssetTypeConfigMapper;
import com.joyintech.tams.common.base.dao.PaFinProductMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.RepAssetOtherMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.report.asset.BaseRepAssetDeptInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepMngAssetPlanInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetDeptInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepMngAssetPlanInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
import org.apache.commons.codec.binary.StringUtils;
import java.util.stream.Collectors;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author qiangchengxi
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCService
 * @since 1.0
 */
@Service("backFinPlanAssetCService")
public class FinPlanAssetCService extends BaseService<FinPlanAssetCDto> {
    @Autowired
    private FinPlanAssetCMapper finPlanAssetCMapper;
  //标的类金融产品基本信息组件
    @Autowired(required=false)
    private BaseFinProductBaseInfoService baseFinProductBaseInfoService;
    //资产管理产品中债报送信息组件
    @Autowired(required=false)
    private BaseRepMngAssetPlanInfoService baseRepMngAssetPlanInfoService;
    //资产负债信息报送组件
    @Autowired(required=false)
    private BaseRepAssetDeptInfoService baseRepAssetDeptInfoService;
    /**
     * 组合表mapper
     */
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    
    /**
     * 查询是否自动创建组合AssetTypeConfigMapper
     */
    @Autowired
    private AssetTypeConfigMapper assetTypeConfigMapper;
    /**
     * 附件信息共通Service
     */
    @Autowired(required=false)
    private MstFileCommonService mstFileCommonService;
    @Autowired(required=false)
    private PaFinProductMapper paFinProductMapper;
    //标的共通service
    @Autowired(required=false)
    private AssetCommonService assetCommonService;
    //    中债资产其他要素 Mapper
    @Autowired
    private RepAssetOtherMapper repAssetOtherMapper;
   
    @Autowired
    private AssetCommonMapper assetCommonMapper;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param page
     * @param length
     * @param param
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public Page<FinPlanAssetCDto> selectForPage(int page, int length,
                                                  Map<String, Object> pageParam) {
        pageParam.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        pageParam.put("PROFIT_TYPE_03", DictCodeList.收益类型_货币型.getValue());
        /*lishuping 2019/7/19 start*/
      if(StringUtil.isNotEmpty(pageParam.get("category"))){
            pageParam.put("categoryList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("category")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type"))){
            pageParam.put("typeList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type9"))){
            pageParam.put("type9List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type9")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type10"))){
            pageParam.put("type10List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type10")));
        }
//        if(StringUtil.isNotEmpty(pageParam.get("type11"))){
//            pageParam.put("type11List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type11")));
//        }
        String assetType11 =(String)pageParam.get("type11");
        if(StringUtil.isNotEmpty(assetType11)){
            if (assetType11.indexOf("107")!=-1){
                assetType11=assetType11+",125,137,138,139,140";
            }
            List<String> type11List = StringUtil.splitToList(",", assetType11);
            pageParam.put("type11List", type11List);
        }
        if(StringUtil.isNotEmpty(pageParam.get("type12"))){
            pageParam.put("type12List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type12")));
        }

        /*lishuping 2019/7/19 end*/
        return super.queryForPage(page, length, () -> finPlanAssetCMapper.selectForPage(pageParam));
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int delete(FinPlanAssetCDto dto) {
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(dto.getFinprodId());
        //基金资产报送信息dto
        BaseRepMngAssetPlanInfoDto baseRepMngAssetPlanInfoDto = new BaseRepMngAssetPlanInfoDto();
        baseRepMngAssetPlanInfoDto.setRepFinprodIdList(finprodIdList);
         //删除授信关联
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(dto.getFinprodId());
        assetCommonService.deleteCredit(creditLines);
        //资产负债报送信息dto
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = new BaseRepAssetDeptInfoDto();
        baseRepAssetDeptInfoDto.setFinprodIdList(finprodIdList);
      //删除委外组合 add wangpeng 2019.07.23
        if(StringUtil.isNotEmpty(dto.getFinprodMarketId())){
        	 ptlPortfolioMapper.deleteByPrimaryKey(dto.getFinprodMarketId());
        }
        if(baseFinProductBaseInfoService.delete(finprodIdList) 
            && baseRepMngAssetPlanInfoService.delete(baseRepMngAssetPlanInfoDto)
            && baseRepAssetDeptInfoService.deleteByPrimaryKey(baseRepAssetDeptInfoDto)
            && (finPlanAssetCMapper.deleteByFinprodId(dto.getFinprodId())==1)
            ){
            return 1;
        }
        return 0;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public int insert(FinPlanAssetCDto dto) throws IllegalAccessException, InvocationTargetException, Exception {
        Map<String,Object> param = getDto(dto);
      //定义标的类金融产品dto
        BaseFinProductBaseInfoDto entity = new BaseFinProductBaseInfoDto();
        /*
         * param.put("finProduct", finProduct);
        param.put("finProductAdd", finProductAdd);
        param.put("finProductType", finProductType);
        param.put("finCashInfoList", finCashInfoList);
        param.put("finCashPayRuleList", finCashPayRuleList);
        param.put("finCashCalcRuleList", finCashCalcRuleList);
        param.put("repFund", repFund);
        param.put("repAssetDept", repAssetDept);
         */
        entity.setFinProduct((FinProduct)param.get("finProduct"));
        entity.setFinProductAdd((FinProductAdd)param.get("finProductAdd"));
        entity.setFinProductType((FinProductType)param.get("finProductType"));
        entity.setFinCashInfoList((List<FinCashInfo>)param.get("finCashInfoList"));
        if(param.get("finCashPayRuleList") != null){
            entity.setFinCashPayRuleList((List<FinCashPayRule>)param.get("finCashPayRuleList"));
        }
        if(param.get("finCashCalcRuleList") != null){
            entity.setFinCashCalcRuleList((List<FinCashCalcRule>)param.get("finCashCalcRuleList"));
        }
        if(param.get("finRateInfoList") != null){
            entity.setFinRateInfoList((List<FinRateInfo>)param.get("finRateInfoList"));
        }
        // 增加推荐方与推荐费率 add by renping 20191014 start
        if(param.get("finProductRecommentList") != null){
            entity.setFinProductRecommentList((List<FinProductRecomment>)param.get("finProductRecommentList"));
        }
        // 增加推荐方与推荐费率 add by renping 20191014 end

        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(dto, entity);

        //新建 标的类金融产品基本信息dto list
        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(entity);
        baseFinProductBaseInfoService.insert(list);
        //新建资产管理计划报送信息dto
        BaseRepMngAssetPlanInfoDto baseRepMngAssetPlanInfoDto = new BaseRepMngAssetPlanInfoDto();
        List<RepAssetMngPlan> repAssetMngPlanList= new ArrayList<RepAssetMngPlan>();
        repAssetMngPlanList.add((RepAssetMngPlan)param.get("repAssetMngPlan"));
        baseRepMngAssetPlanInfoDto.setRepAssetMngPlanList(repAssetMngPlanList);
        baseRepMngAssetPlanInfoService.insert(baseRepMngAssetPlanInfoDto);
        //新建资产负债报送信息list
        List<RepAssetDept> repAssetDeptList = new ArrayList<RepAssetDept>();
        repAssetDeptList.add((RepAssetDept)param.get("repAssetDept"));
        baseRepAssetDeptInfoService.insert(repAssetDeptList);
        //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(dto.getFileArray());
        mstFileInputParameter.setBussinessId(dto.getFinprodId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
        //add 2019.7.15 wangpeng 根据配置 决定是否入库组合表信息
        boolean isCreatePortfolio = assetCommonService.selectIsCreatePortfolio(entity.getFinProductType());
        if(isCreatePortfolio){
        	 //组合表插入
            ptlPortfolioMapper.deleteByPrimaryKey(dto.getPtlPortfolio().getPortfolioId());
            ptlPortfolioMapper.insert(dto.getPtlPortfolio());
        }
        //end
        //插入标的类金融产品扩展表
        PaFinProduct paFinProduct = (PaFinProduct)param.get("paFinProduct");
        paFinProductMapper.insert(paFinProduct);
        
        //wangyang1 2020年3月31日 资产投资性质字段未在FIN_PRODUCT_ADD里存值 start
        finPlanAssetCMapper.updateInvestNatureByFinprodId(paFinProduct.getFinprodId(), paFinProduct.getInvestmentAttributes());
        //wangyang1 2020年3月31日 end
        
        /**
         * 增加授信关联
         */
        if(dto.getPflsh() != null && !StringUtil.equals(dto.getPflsh(), "")){
            CreditLines creditLines = new CreditLines();
            creditLines.setCustomerId(dto.getFinprodId());
            creditLines.setSeqno(dto.getSeqno());
            creditLines.setCreditMstId(dto.getCreditMstId());
            assetCommonService.insertCredit(creditLines);
        }
        return 1;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public int update(FinPlanAssetCDto dto) throws IllegalAccessException, InvocationTargetException, Exception {
        Map<String,Object> param = getDto(dto);
        List<String> finprodIdList = (List<String>)param.get("finprodIdList");
      //定义标的类金融产品dto
        BaseFinProductBaseInfoDto entity = new BaseFinProductBaseInfoDto();
        /*
         * param.put("finProduct", finProduct);
        param.put("finProductAdd", finProductAdd);
        param.put("finProductType", finProductType);
        param.put("finCashInfoList", finCashInfoList);
        param.put("finCashPayRuleList", finCashPayRuleList);
        param.put("repFund", repFund);
        param.put("repAssetDept", repAssetDept);
         */
        entity.setFinProduct((FinProduct)param.get("finProduct"));
        entity.setFinProductAdd((FinProductAdd)param.get("finProductAdd"));
        entity.setFinProductType((FinProductType)param.get("finProductType"));
        entity.setFinCashInfoList((List<FinCashInfo>)param.get("finCashInfoList"));
        if(param.get("finCashPayRuleList") != null){
            entity.setFinCashPayRuleList((List<FinCashPayRule>)param.get("finCashPayRuleList"));
        }
        if(param.get("finCashCalcRuleList") != null){
            entity.setFinCashCalcRuleList((List<FinCashCalcRule>)param.get("finCashCalcRuleList"));
        }
        if(param.get("finRateInfoList") != null){
            entity.setFinRateInfoList((List<FinRateInfo>)param.get("finRateInfoList"));
        }
        // 增加推荐方与推荐费率 add by renping 20191014 start
        if(param.get("finProductRecommentList") != null){
            entity.setFinProductRecommentList((List<FinProductRecomment>)param.get("finProductRecommentList"));
        }
        // 增加推荐方与推荐费率 add by renping 20191014 end

        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(dto, entity);

        //新建 标的类金融产品基本信息dto list
        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(entity);
        baseFinProductBaseInfoService.update(finprodIdList,list);
      //修改资产管理计划报送信息dto
        BaseRepMngAssetPlanInfoDto baseRepMngAssetPlanInfoDto = new BaseRepMngAssetPlanInfoDto();
        List<RepAssetMngPlan> repAssetMngPlanList= new ArrayList<RepAssetMngPlan>();
        repAssetMngPlanList.add((RepAssetMngPlan)param.get("repAssetMngPlan"));
        baseRepMngAssetPlanInfoDto.setRepFinprodIdList(finprodIdList);
        baseRepMngAssetPlanInfoDto.setRepAssetMngPlanList(repAssetMngPlanList);
        baseRepMngAssetPlanInfoService.update(baseRepMngAssetPlanInfoDto);
      /*  修改中债新加字段编辑后审批报错 add by zhuyongkang 20191012 start*/
        repAssetOtherMapper.updateByPrimaryKeySelective(dto.getRepAssetOther());
        /*  修改中债新加字段编辑后审批报错 add by zhuyongkang 20191012 end*/
        //修改资产负债报送信息list
        List<RepAssetDept> repAssetDeptList = new ArrayList<RepAssetDept>();
        repAssetDeptList.add((RepAssetDept)param.get("repAssetDept"));
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = new BaseRepAssetDeptInfoDto();
        baseRepAssetDeptInfoDto.setFinprodIdList(finprodIdList);
        baseRepAssetDeptInfoDto.setRepAssetDeptList(repAssetDeptList);
        baseRepAssetDeptInfoService.updateBaseRepAssetDeptInfoDto(baseRepAssetDeptInfoDto);
      //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(dto.getFileArray());
        mstFileInputParameter.setBussinessId(dto.getFinprodId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
        //插入标的类金融产品扩展表
        PaFinProduct paFinProduct = (PaFinProduct)param.get("paFinProduct");
        paFinProductMapper.updateByPrimaryKey(paFinProduct);
        
        //wangyang1 2020年3月31日 资产投资性质字段未在FIN_PRODUCT_ADD里存值 start
        finPlanAssetCMapper.updateInvestNatureByFinprodId(paFinProduct.getFinprodId(), paFinProduct.getInvestmentAttributes());
        //wangyang1 2020年3月31日 end
        /**
         * 修改授信关联
         */
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(dto.getFinprodId());
        List<Map> credMsg = assetCommonMapper.getMstIdByFinproId(dto.getFinprodId());
        if(credMsg.size()>0) {
        	Map<String,String> credMsgMap = (Map<String,String>) credMsg.get(0);
        	creditLines.setCreditMstId(credMsgMap.get("CREDIT_MST_ID"));
        	creditLines.setSeqno(credMsgMap.get("SEQNO"));
        }
        assetCommonService.updateCredit(creditLines);
        return 1;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    private Map<String, Object> getDto(FinPlanAssetCDto dto) throws IllegalAccessException, InvocationTargetException {
        Map<String,Object> param = new HashMap<String,Object>();
        //标的类金融产品dto
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyPropertiesIgnoreNull(finProduct,dto);
        finProduct.setFinprodId(dto.getFinprodId());
//        finProduct.setTermDays((entity.getMdate().getTime()-entity.getVdate().getTime())/1000/60/60/24);
        if (dto.getMdate() != null) {
            finProduct.setTermDays((dto.getMdate().getTime()-dto.getVdate().getTime())/1000/60/60/24);
        }
        finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
        finProduct.setProfitType(DictCodeList.收益类型_货币型.getValue());
        BussRepAssetMngProdInfoDto bussRepAssetMngProdInfoDto = dto.getBussRepAssetMngProdInfoDto();
        //资产负债报送信息
        RepAssetDept repAssetDept = new RepAssetDept();
        repAssetDept.setRepFinprodId(finProduct.getFinprodId());
        repAssetDept.setFinprodMarketId(finProduct.getFinprodMarketId());
        /* FXZG-6 【阜新银行】REP_ASSET_DEPT部分字段未存值 start */
        repAssetDept.setRepCcy(dto.getCcy());
        /*FXZG-6 【阜新银行】REP_ASSET_DEPT部分字段未存值 end*/
        repAssetDept.setRepFinprodName(dto.getFinprodName());
        if(bussRepAssetMngProdInfoDto!=null){
            BeanUtil.copyPropertiesIgnoreNull(repAssetDept,
                    bussRepAssetMngProdInfoDto.getRepAssetDept());
        }
        //资产管理计划报送信息
        RepAssetMngPlan repAssetMngPlan = new RepAssetMngPlan();
        repAssetMngPlan.setRepFinprodId(finProduct.getFinprodId());
        if(bussRepAssetMngProdInfoDto!=null){
            BeanUtil.copyPropertiesIgnoreNull(repAssetMngPlan,
                bussRepAssetMngProdInfoDto.getRepAssetMngPlan());
        }
        repAssetMngPlan.setRepManagerId(dto.getManager());
        repAssetMngPlan.setRepTrusteeId(dto.getTrusteeId());
        repAssetMngPlan.setRepSetDate(dto.getVdate());
        repAssetMngPlan.setRepEndDate(dto.getMdate());
        //中债资产其他要素 by zhuyongkang start
        RepAssetOther repAssetOther = new RepAssetOther();
        repAssetOther.setRepFinprodId(finProduct.getFinprodId());
        if(dto.getBussRepAssetMngProdInfo()!=null){
            repAssetOther.setRepPrivateprdFlag(dto.getBussRepAssetMngProdInfo().getRepPrivateprdFlag());
        }
        dto.setRepAssetOther(repAssetOther);
        repAssetOtherMapper.deleteByPrimaryKey(dto.getRepAssetOther().getRepFinprodId());
        repAssetOtherMapper.insert(dto.getRepAssetOther());
        //  by zhuyongkang end
        //标的类金融产品附属表dto
        FinProductAdd finProductAdd = new FinProductAdd();
        finProductAdd.setFinprodId(finProduct.getFinprodId());
        finProductAdd.setInvestNature(dto.getInvestmentAttributes());
        /*if(StringUtil.equals(dto.getTransMode(), DictCodeList.清算速度_T_0.getValue())){
            finProductAdd.setPurSpeed((long)0);
            finProductAdd.setRedSpeed((long)0);
        }
        else{
            finProductAdd.setPurSpeed((long)1);
            finProductAdd.setRedSpeed((long)1);
        }
        */
        /*  update by zhuyongkang 2019/09/29 start*/
        if (StringUtil.isNotEmpty(dto.getPurSpeed())) {
            finProductAdd.setPurSpeed(Long.parseLong(dto.getPurSpeed()));
        }
        if (StringUtil.isNotEmpty(dto.getRedSpeed())) {
            finProductAdd.setRedSpeed(Long.parseLong(dto.getRedSpeed()));
        }
        /*  update by zhuyongkang 2019/09/29 END*/
        finProductAdd.setIsCompoundInt(dto.getIsCompoundInt());
        finProductAdd.setIsOpenEveryDay(dto.getIsOpenEveryDay());
        finProductAdd.setFundMangerName(dto.getFundMangerName());
        finProductAdd.setCreditEnhanceMeasure(dto.getCreditEnhanceMeasure());
        //金融产品分类表dto
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(finProduct.getFinprodId());
        finProductType.setBranch(dto.getBranch());
        finProductType.setType6(dto.getCategory());
        finProductType.setType7(dto.getType());
        /*添加五级分类 lishuping 2019/7/23 start*/
        finProductType.setType9(dto.getType9());
        finProductType.setType10(dto.getType10());
        finProductType.setType11(dto.getType11());
        finProductType.setType12(dto.getType12());
        /*添加五级分类 lishuping 2019/7/23 end*/
        finProductType.setType8(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
      //现金流信息List
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        //现金流计算规则dtolist
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        //现金流信息-本金
        FinCashInfo finCashInfo1 = new FinCashInfo();
        finCashInfo1.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfo1.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfo1.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfo1.setBranch(dto.getBranch());
        finCashInfo1.setFinprodId(finProduct.getFinprodId());
        finCashInfo1.setCcy(finProduct.getCcy());
        finCashInfo1.setCashId(EncryptUtil.getInstance().md5(dto.getFinprodId()+(char)256+finCashInfo1.getFinprodType()
        +(char)256+finCashInfo1.getBranch()+(char)256+finCashInfo1.getCashType2()));
        //现金流信息-利息
        FinCashInfo finCashInfo2 = new FinCashInfo();
        BeanUtil.copyPropertiesIgnoreNull(finCashInfo2,finCashInfo1);
        finCashInfo2.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfo2.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfo1.setCashId(EncryptUtil.getInstance().md5(dto.getFinprodId()+(char)256+finCashInfo2.getFinprodType()
        +(char)256+finCashInfo2.getBranch()+(char)256+finCashInfo2.getCashType2()));
        finCashInfoList.add(finCashInfo1);
        finCashInfoList.add(finCashInfo2);
        if(null!=finCashCalcRuleList&&!finCashCalcRuleList.isEmpty()){
          //现金流利率信息表dtolist
            List<FinRateInfo> finRateInfoList = new ArrayList<>();
            //托管费
            FinRateInfo finRateInfo1 = new FinRateInfo();
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo1,finCashCalcRuleList.get(0));
            BeanUtil.copyPropertiesIgnoreNull(finRateInfo1,finProduct);
            finRateInfo1.setRate(finCashCalcRuleList.get(0).getYield());
            finRateInfo1.setEffDate(dto.getVdate());
//            finRateInfo1.setRateType(DictCodeList.利率类型_固定利率.getValue());
            finRateInfo1.setBranch(dto.getBranch());
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
            finRateInfoList.add(finRateInfo1);
            finRateInfoList.add(finRateInfo2);
            finRateInfoList.add(finRateInfo3);
            param.put("finRateInfoList", finRateInfoList);
        }
      //现金流信息-参与方信息（多条数据）以及 现金流计算规则dtolist
        if(null!=dto.getBussParticipantInfoDto()){
            BussParticipantInfoDto bussParticipantInfoDto = dto.getBussParticipantInfoDto();
            finCashCalcRuleList.addAll(bussParticipantInfoDto.getFinCashCalcRuleList());
            finCashInfoList.addAll(bussParticipantInfoDto.getFinCashInfoList());
        }
        //现金流支付规则表Entity/分红计划组件
        //分红计划组件dto
        if(dto.getBussFinIntPayDto() != null){
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            BeanUtil.copyProperties(finCashPayRule,dto.getBussFinIntPayDto());
            finCashPayRule.setCashId(finCashInfo2.getCashId());
            finCashPayRule.setEffDate(dto.getVdate());
            finCashPayRule.setFinprodId(finProduct.getFinprodId());
            finCashPayRule.setFinprodType(finProduct.getFinprodType());
            finCashPayRule.setFinprodType2(finProduct.getFinprodType2());
            finCashPayRule.setBranch(dto.getBranch());
            if(DictCodeList.收益结转频率_按月结转.getValue().equals(dto.getBussFinIntPayDto().getPayCycle())){
                finCashPayRule.setDayType(DictCodeListExt1.日期类型_几号.getValue());
            }
            //List<现金流支付规则表Entity>/List<分红计划组件Entity>
            List<FinCashPayRule> finCashPayRuleList = new ArrayList<FinCashPayRule>();
            finCashPayRuleList.add(finCashPayRule);
            param.put("finCashPayRuleList", finCashPayRuleList);
        }
        //标的类金融产品扩展表
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        //获取要修改的金融产品代码集合(修改时用)
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(finProduct.getFinprodId());

        // 增加推荐方与推荐费率 add by renping 20191014 start
        if (StringUtil.isNotEmpty(dto.getRecommender())) {
            List<FinProductRecomment> finProductRecommentList = new ArrayList<>();
            FinProductRecomment finProductRecomment = new FinProductRecomment();
            finProductRecomment.setFinprodId(dto.getFinprodId());
            finProductRecomment.setRecommender(dto.getRecommender());
            finProductRecomment.setWorkflowId(dto.getWorkflowId());
            finProductRecommentList.add(finProductRecomment);
            param.put("finProductRecommentList", finProductRecommentList);
        }
        // 增加推荐方与推荐费率 add by renping 20191014 end
        param.put("finProduct", finProduct);
        param.put("finProductAdd", finProductAdd);
        param.put("finProductType", finProductType);
        param.put("finCashInfoList", finCashInfoList);
        param.put("finCashCalcRuleList", finCashCalcRuleList);
        param.put("repAssetMngPlan", repAssetMngPlan);
        param.put("repAssetDept", repAssetDept);
        param.put("finprodIdList", finprodIdList);
        param.put("paFinProduct", paFinProduct);
        return param;
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public FinPlanAssetCDto getBasicInfoByFinprodId(Map<String, Object> param) {
        List<FinPlanAssetCDto> dtoList =finPlanAssetCMapper.getBasicInfoByFinprodId(param);
        if (dtoList.size() > 0){
            return dtoList.get(0);
        }
        return new FinPlanAssetCDto();
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    public String getFinprodId(String finprodMarketId){
        return finPlanAssetCMapper.getFinprodId(finprodMarketId);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/

    /*增加资产代码重复校验 add by zhuyongkang 20191021 start*/
    /**
     * 查询市场代码
     *
     * @param finprodMarketId 市场代码
     * @return 查询到的市场代码
     * @see
     * @since 1.0
     */
    public int selectFinprodMarketId(String finprodMarketId){
        return finPlanAssetCMapper.selectFinprodMarketId(finprodMarketId);
    }
    /*增加资产代码重复校验 add by zhuyongkang 20191021 end*/

    /**
     * 处理开放计划相关数据生成
     * <功能详细描述>
     * @param finProductCommonInfoDto
     * @param baseFinProductBaseInfoDto
     * @param finProductCommonInfoDto
     * @throws Exception
     * @see
     * @since 1.0
     */
    private void procFinProductOpenPlan(FinPlanAssetCDto dto,
                                        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto)
            throws Exception {
        if (dto.getBussFinProductOpenPlanDto() != null) {
            // 现金流信息dtolist-->本金
            String cashId_amt = baseFinProductBaseInfoDto.getFinCashInfoList().stream()
                    .filter(e->DictCodeListExt1.现金流二级类型_本金.getValue().equals(e.getCashType2()))
                    .map(FinCashInfo::getCashId).collect(Collectors.joining());
            // String cashId_amt = CommonUtil.generateCashFlowCode(
            //         baseFinProductBaseInfoDto.getFinProduct().getFinprodId(),
            //         DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
            //         DictCodeListExt1.现金流二级类型_本金.getValue());
            // FinCashInfo finCashInfo_amt = new FinCashInfo();
            // BeanUtil.copyProperties(finCashInfo_amt, dto);
            // finCashInfo_amt.setCashId(cashId_amt);
            // finCashInfo_amt.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            // finCashInfo_amt.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
            // finCashInfo_amt.setCcy(dto.getCcy());
            // finCashInfo_amt.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
            // finCashInfo_amt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            // finCashInfo_amt.setBranch(0L);
            // finCashInfo_amt.setIsCalc(DictCodeList.是否_是.getValue());
            // baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_amt);

            // 开放规则dtolist
            BussFinProductOpenPlanDto bussFinProductOpenPlanDto = dto.getBussFinProductOpenPlanDto();
            FinOpenRule finOpenRule = new FinOpenRule();
            BeanUtil.copyProperties(finOpenRule, dto);
            finOpenRule.setCashId(cashId_amt);
            finOpenRule.setPurSpeed(bussFinProductOpenPlanDto.getPurSpeed());
            // update zhangweiqi 申赎类型从界面取值
            finOpenRule.setOpenType(bussFinProductOpenPlanDto.getOpenType());
            // finOpenRule.setOpenType(DictCodeList.申赎开放类型_申赎开放.getValue());
            finOpenRule.setCpriceRule(bussFinProductOpenPlanDto.getCpriceRule());// add zhangweiqi
            // 新增净值日规则
            finOpenRule.setOpenMode(bussFinProductOpenPlanDto.getOpenMode());
            finOpenRule.setOpenDays(bussFinProductOpenPlanDto.getOpenDays());
            finOpenRule.setOpenCycle(bussFinProductOpenPlanDto.getOpenCycle());
            finOpenRule.setOpenCycleUnit(bussFinProductOpenPlanDto.getOpenCycleUnit());
            if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getDayOfWeek())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_周几.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getDayOfWeek());
            }
            else if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getDayOfMonth())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_第几自然日.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getDayOfMonth());
            }
            else if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getWorkdayOfMonth())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_第几工作日.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getWorkdayOfMonth());
            }
            // 第几周周几
            else if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getWeekOfMonth())
                    && StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getWorkdayOfWeek())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_第几周周几.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getWeekOfMonth() + ";"
                        + bussFinProductOpenPlanDto.getWorkdayOfWeek());
            }
            finOpenRule.setCalcMethod(bussFinProductOpenPlanDto.getCalcMethod());
            finOpenRule.setFirstOpenDate(bussFinProductOpenPlanDto.getFirstOpenDate());
            finOpenRule.setTradedayRule(bussFinProductOpenPlanDto.getTradedayRule());
            finOpenRule.setHdayOpenFlag(bussFinProductOpenPlanDto.getHdayOpenFlag());
            finOpenRule.setCalcRule(bussFinProductOpenPlanDto.getCalcRule());
            finOpenRule.setOpenDaysType(bussFinProductOpenPlanDto.getOpenDaysType());
            baseFinProductBaseInfoDto.setFinOpenRuleList(new ArrayList<>());
            baseFinProductBaseInfoDto.getFinOpenRuleList().add(finOpenRule);

            // 开放计划dtolist
            baseFinProductBaseInfoDto.setFinOpenPlanList(new ArrayList<>());
            if (!StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                for (FinOpenPlan finOpenPlan : bussFinProductOpenPlanDto.getFinOpenPlanList()) {
                    finOpenPlan.setCashId(cashId_amt);
                    // update zhangweiqi 申赎类型从界面取值
                    finOpenPlan.setOpenType(bussFinProductOpenPlanDto.getOpenType());
                    // finOpenPlan.setOpenType(DictCodeList.申赎开放类型_申赎开放.getValue());
                    finOpenPlan.setOpenDateStrUn(finOpenPlan.getOpenDateStr());
                    finOpenPlan.setOpenDateEndUn(finOpenPlan.getOpenDateEnd());
                    finOpenPlan.setOpenVdateUn(finOpenPlan.getOpenVdate());
                    finOpenPlan.setOpenMdateUn(finOpenPlan.getOpenMdate());
                    finOpenPlan.setOpenNdate(finOpenPlan.getOpenNdate());// add zhangweiqi 净值日
                    finOpenPlan.setFinprodId(
                            baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                    finOpenPlan.setBranch(0L);
                    finOpenPlan.setOpenStatus(DictCodeList.开放状态_开放.getValue());
                    baseFinProductBaseInfoDto.getFinOpenPlanList().add(finOpenPlan);
                }
            }
            // add by zhangweiqi 增加赎回的逻辑 begin
            //周期型产品无赎回计划
            if (!StringUtils.equals(bussFinProductOpenPlanDto.getOpenType(),
                    DictCodeList.申赎开放类型_申赎开放.getValue())) {
                // 开放计划
                if (dto.getBussFinProductOpenPlanRedDto() != null) {
                    // 开放规则dtolist
                    BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto = dto.getBussFinProductOpenPlanRedDto();
                    FinOpenRule finOpenRuleRed = new FinOpenRule();
                    BeanUtil.copyProperties(finOpenRuleRed, bussFinProductOpenPlanRedDto);
                    finOpenRuleRed.setCashId(cashId_amt);
                    finOpenRuleRed.setOpenType(DictCodeList.申赎开放类型_赎回开放.getValue());
                    finOpenRuleRed.setCpriceRule(bussFinProductOpenPlanRedDto.getCpriceRule());// add
                    // zhangweiqi
                    // 新增净值日规则
                    finOpenRuleRed.setOpenMode(bussFinProductOpenPlanRedDto.getOpenMode());
                    finOpenRuleRed.setOpenDays(bussFinProductOpenPlanRedDto.getOpenDays());
                    finOpenRuleRed.setOpenCycle(bussFinProductOpenPlanRedDto.getOpenCycle());
                    finOpenRuleRed.setOpenCycleUnit(
                            bussFinProductOpenPlanRedDto.getOpenCycleUnit());
                    if (StringUtil.isNotEmpty(bussFinProductOpenPlanRedDto.getDayOfWeek())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_周几.getValue());
                        finOpenRuleRed.setDayValue(bussFinProductOpenPlanRedDto.getDayOfWeek());
                    }
                    else if (StringUtil.isNotEmpty(bussFinProductOpenPlanRedDto.getDayOfMonth())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_第几自然日.getValue());
                        finOpenRuleRed.setDayValue(bussFinProductOpenPlanRedDto.getDayOfMonth());
                    }
                    else if (StringUtil.isNotEmpty(
                            bussFinProductOpenPlanRedDto.getWorkdayOfMonth())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_第几工作日.getValue());
                        finOpenRuleRed.setDayValue(
                                bussFinProductOpenPlanRedDto.getWorkdayOfMonth());
                    }
                    // 第几周周几
                    else if (StringUtil.isNotEmpty(bussFinProductOpenPlanRedDto.getWeekOfMonth())
                            && StringUtil.isNotEmpty(
                            bussFinProductOpenPlanRedDto.getWorkdayOfWeek())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_第几周周几.getValue());
                        finOpenRuleRed.setDayValue(
                                bussFinProductOpenPlanRedDto.getWeekOfMonth() + ";"
                                        + bussFinProductOpenPlanRedDto.getWorkdayOfWeek());
                    }
                    finOpenRuleRed.setCalcMethod(bussFinProductOpenPlanRedDto.getCalcMethod());
                    finOpenRuleRed.setFirstOpenDate(
                            bussFinProductOpenPlanRedDto.getFirstOpenDate());
                    finOpenRuleRed.setTradedayRule(bussFinProductOpenPlanRedDto.getTradedayRule());
                    finOpenRuleRed.setHdayOpenFlag(bussFinProductOpenPlanRedDto.getHdayOpenFlag());
                    finOpenRuleRed.setCalcRule(bussFinProductOpenPlanRedDto.getCalcRule());
                    finOpenRuleRed.setOpenDaysType(bussFinProductOpenPlanRedDto.getOpenDaysType());
                    baseFinProductBaseInfoDto.getFinOpenRuleList().add(finOpenRuleRed);

                    // 开放计划dtolist
                    if (!StringUtil.isEmptyList(
                            bussFinProductOpenPlanRedDto.getFinOpenPlanList())) {
                        for (FinOpenPlan finOpenPlanRed : bussFinProductOpenPlanRedDto.getFinOpenPlanList()) {
                            finOpenPlanRed.setCashId(cashId_amt);
                            finOpenPlanRed.setOpenType(DictCodeList.申赎开放类型_赎回开放.getValue());
                            finOpenPlanRed.setOpenDateStrUn(finOpenPlanRed.getOpenDateStr());
                            finOpenPlanRed.setOpenDateEndUn(finOpenPlanRed.getOpenDateEnd());
                            finOpenPlanRed.setOpenVdateUn(finOpenPlanRed.getOpenVdate());
                            finOpenPlanRed.setOpenMdateUn(finOpenPlanRed.getOpenMdate());
                            finOpenPlanRed.setOpenNdate(finOpenPlanRed.getOpenNdate());// add
                            // zhangweiqi
                            // 净值日
                            finOpenPlanRed.setFinprodId(
                                    baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                            finOpenPlanRed.setBranch(0L);
                            finOpenPlanRed.setOpenStatus(DictCodeList.开放状态_开放.getValue());
                            baseFinProductBaseInfoDto.getFinOpenPlanList().add(finOpenPlanRed);
                        }
                    }
                }

            }
            // add by zhangweiqi 增加赎回的逻辑 end
        }
    }
}
