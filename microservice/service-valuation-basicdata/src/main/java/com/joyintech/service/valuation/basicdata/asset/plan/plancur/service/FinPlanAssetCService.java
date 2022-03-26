/*
 * 文件名：FinPlanAssetCService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.basicdata.asset.plan.plancur.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.basicdata.asset.plan.plancur.dao.FinPlanAssetCMapper;
import com.joyintech.service.valuation.basicdata.asset.plan.plancur.dto.BussParticipantInfoDto;
import com.joyintech.service.valuation.basicdata.asset.plan.plancur.dto.BussRepAssetMngProdInfoDto;
import com.joyintech.service.valuation.basicdata.asset.plan.plancur.dto.FinPlanAssetCDto;
import com.joyintech.service.valuation.basicdata.base.dto.BaseFinProductBaseInfoDto;
import com.joyintech.service.valuation.basicdata.base.service.BaseFinProductBaseInfoService;
import com.joyintech.tams.common.base.dao.AssetTypeConfigMapper;
import com.joyintech.tams.common.base.dao.PaFinProductMapper;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.RepAssetOtherMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author qiangchengxi
 * @version 1.0 2019年1月7日
 * @see FinPlanAssetCService
 * @since 1.0
 */
@Service(value="serviceFinPlanAssetCService")
public class FinPlanAssetCService extends BaseService<FinPlanAssetCDto> {
    @Autowired
    private FinPlanAssetCMapper finPlanAssetCMapper;
  //标的类金融产品基本信息组件
    @Autowired(required=false)
    private BaseFinProductBaseInfoService baseFinProductBaseInfoService;
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
    @Autowired(required=false)
    private PaFinProductMapper paFinProductMapper;
    @Autowired
    private RepAssetOtherMapper repAssetOtherMapper;

    /**
     *
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     */
    public Page<FinPlanAssetCDto> selectForPage(int page, int length,
                                                  Map<String, Object> pageParam) throws Exception {
        pageParam.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_计划类资产.getValue());
        pageParam.put("PROFIT_TYPE_03", DictCodeList.收益类型_货币型.getValue());
        pageParam.put("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        pageParam.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
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
        if(StringUtil.isNotEmpty(pageParam.get("type11"))){
            pageParam.put("type11List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type11")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type12"))){
            pageParam.put("type12List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type12")));
        }

        /*lishuping 2019/7/19 end*/
        return super.queryForPage(page, length, () -> finPlanAssetCMapper.selectForPage(pageParam));
    }


    public List<FinPlanAssetCDto> selectForList(Map<String, Object> pageParam) throws Exception {
        pageParam.put("FINPROD_TYPE_TG_F17", DictCodeListExt1.金融产品类型_计划类资产.getValue());
        pageParam.put("PROFIT_TYPE_03", DictCodeList.收益类型_货币型.getValue());
        pageParam.put("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        /*lishuping 2019/7/19 end*/
        return finPlanAssetCMapper.selectForPage(pageParam);
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
        //删除委外组合 add wangpeng 2019.07.23
        if(StringUtil.isNotEmpty(dto.getFinprodMarketId())){
        	 ptlPortfolioMapper.deleteByPrimaryKey(dto.getFinprodMarketId());
        }
        if(baseFinProductBaseInfoService.delete(finprodIdList)){
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
    public int insert(FinPlanAssetCDto dto) throws IllegalAccessException, InvocationTargetException {
        Map<String,Object> param = getDto(dto);
      //定义标的类金融产品dto
        BaseFinProductBaseInfoDto entity = new BaseFinProductBaseInfoDto();

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
        //新建 标的类金融产品基本信息dto list
        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(entity);
        baseFinProductBaseInfoService.insert(list);
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
    public int update(FinPlanAssetCDto dto) throws IllegalAccessException, InvocationTargetException {
        Map<String,Object> param = getDto(dto);
        List<String> finprodIdList = (List<String>)param.get("finprodIdList");
      //定义标的类金融产品dto
        BaseFinProductBaseInfoDto entity = new BaseFinProductBaseInfoDto();

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
        //新建 标的类金融产品基本信息dto list
        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(entity);
        baseFinProductBaseInfoService.update(finprodIdList,list);
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
        if(!StringUtil.isNotEmpty(dto.getFinprodType())){
            dto.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        }
        BeanUtil.copyPropertiesIgnoreNull(finProduct,dto);
        finProduct.setFinprodId(dto.getFinprodId());
        if (dto.getMdate() != null) {
            finProduct.setTermDays((dto.getMdate().getTime()-dto.getVdate().getTime())/1000/60/60/24);
        }
        finProduct.setBlnArea(dto.getBlnArea());
        finProduct.setProfitType(dto.getProfitType());
        //标的类金融产品附属表dto
        FinProductAdd finProductAdd = new FinProductAdd();
        finProductAdd.setFinprodId(finProduct.getFinprodId());
        finProductAdd.setInvestNature(dto.getInvestNature());
        finProductAdd.setDataSource(DictCodeListExt3.估值数据来源_手工.getValue());
        /*  update by zhuyongkang 2019/09/29 start*/
        if (StringUtil.isNotEmpty(dto.getPurSpeed())) {
            finProductAdd.setPurSpeed(Long.parseLong(dto.getPurSpeed()));
        }
        if (StringUtil.isNotEmpty(dto.getRedSpeed())) {
            finProductAdd.setRedSpeed(Long.parseLong(dto.getRedSpeed()));
        }
        /*  update by zhuyongkang 2019/09/29 END*/
        finProductAdd.setIsCompoundInt(dto.getIsCompoundInt());
        if (dto.getBussFinIntPayDto() != null){
            finProductAdd.setIsCompoundInt(dto.getBussFinIntPayDto().getIsCompoundInt());
        }
        //金融产品分类表dto
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(finProduct.getFinprodId());
        finProductType.setType1(dto.getAssetType());
        finProductType.setType2(dto.getAccountintType());
        finProductType.setType3(dto.getTaxType());
        finProductType.setBranch(dto.getBranch());
        finProductType.setType6(dto.getCategory());
        finProductType.setType7(dto.getType());
        /*添加五级分类 lishuping 2019/7/23 start*/
        finProductType.setType9(dto.getType9());
        finProductType.setType10(dto.getType10());
        finProductType.setType11(dto.getType11());
        finProductType.setType12(dto.getType12());
        /*添加五级分类 lishuping 2019/7/23 end*/

        //modify by yanya on 20210902 增加证券品种type18 start
        if(StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), dto.getProfitType())) {
            finProductType.setType18(DictCodeListExt1.证券品种_计划类资产_货币型.getValue());
            finProductType.setType8(DictCodeList.平安资产类型_计划类资产_货币型.getValue());
        }
        else {
            finProductType.setType18(DictCodeListExt1.证券品种_计划类资产_净值型.getValue());
            finProductType.setType8(DictCodeList.平安资产类型_计划类资产_净值型.getValue());
        }
        //modify by yanya on 20210902 增加证券品种type18 end

        //现金流信息List
        List<FinCashInfo> finCashInfoList = new ArrayList<>();
        //现金流计算规则dtolist
        List<FinCashCalcRule> finCashCalcRuleList = new ArrayList<>();
        //现金流信息-本金
        FinCashInfo finCashInfo1 = new FinCashInfo();
        finCashInfo1.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfo1.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfo1.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
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
        param.put("finprodIdList", finprodIdList);
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

    public Page<SelectOptionDto> getCustomer(int page, int length, Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length,
                () -> finPlanAssetCMapper.getCustomer(param));
    }

    /**
     * 资产模糊下拉框
     * @param param
     * @return
     */
    public List<SuggestDto> getAssetId(Map<String, Object> param){
        param.put("FINPROD_TYPE_TG_ZQ", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        //增加母资管计划筛选条件
        if (StringUtil.isNotEmpty(param.get("finprodType2"))) {
            if(StringUtil.equals(param.get("finprodType2"), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
                param.put("finprodType2", StringUtil.splitToList(null, param.get("finprodType2").toString()+","+DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue()));
            }else{
                param.put("finprodType2", StringUtil.splitToList(null, param.get("finprodType2").toString()));
            }
        }
        param.put("PROFIT_TYPE_03", DictCodeList.收益类型_货币型.getValue());
        param.put("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        param.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        return finPlanAssetCMapper.getAssetId(param);
    }

    public Integer isDealCalendarHoliday(Map<String, Object> param) {
        String dateStr = (String) param.get("dateStr");
        String calId = (String) param.get("calId");

        return finPlanAssetCMapper.selectDealCalendarHolidayCount(dateStr,calId);
    }
}
