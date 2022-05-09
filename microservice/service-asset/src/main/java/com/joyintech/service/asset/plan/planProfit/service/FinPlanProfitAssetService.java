/*
 * 文件名：FinPlanAssetRService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.planProfit.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.common.dao.AssetCommonMapper;
import com.joyintech.service.asset.common.service.AssetCommonService;
import com.joyintech.service.asset.plan.planProfit.dao.FinPlanProfitAssetMapper;
import com.joyintech.service.asset.plan.planProfit.dto.FinPlanProfitAssetDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.report.asset.BaseRepAssetDeptInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepMngAssetPlanInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepTrustInvestModeInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetDeptInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepMngAssetPlanInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepTrustInvestModeInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.invest.common.CommonUtil;
import org.apache.commons.codec.binary.StringUtils;

/**
 * 计划类资产（利率型）Service
 * 
 * @author liuwei
 * @version 1.0 2018年9月14日
 * @see FinPlanProfitAssetService
 * @since 1.0
 */
@Service("backFinPlanProfitAssetService")
public class FinPlanProfitAssetService extends BaseService<FinPlanProfitAssetDto>{
    //    中债资产其他要素 Mapper
    @Autowired
    private RepAssetOtherMapper repAssetOtherMapper;

    /**
     * 定义计划类资产（利率型）mapper
     */
    @Autowired
    private FinPlanProfitAssetMapper finPlanProfitAssetMapper;
    /**
     * 标的类金融产品基本信息组件接口
     */
    @Autowired
    private BaseFinProductBaseInfoService baseFinProductBaseInfoService;
    /**
     * 资产管理产品中债报送信息组件
     */
    @Autowired
    private BaseRepMngAssetPlanInfoService baseRepMngAssetPlanInfoService;
    /**
     * 委外投资-协议方式报送信息
     */
    @Autowired
    private BaseRepTrustInvestModeInfoService baseRepTrustInvestModeInfoService;
    /**
     * 资产负债报送信息组件
     */
    @Autowired
    private BaseRepAssetDeptInfoService baseRepAssetDeptInfoService;
    /**
     * 标的类金融产品扩展表mapper
     */
    @Autowired
    private PaFinProductMapper paFinProductMapper;
    
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
     * 现金流计算规则扩展表mapper
     */
    @Autowired
    private PaFinCashCalcRuleMapper paFinCashCalcRuleMapper;
    /**
     * 现金流计算规则扩展表mapper
     */
    @Autowired
    private PaFinProductBuyoutRateMapper paFinProductBuyoutRateMapper;
  //标的共通service
    @Autowired(required=false)
    private AssetCommonService assetCommonService;
    /**
     * 应收款持仓
     */
    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    @Autowired
    private FinProductMapper finProductMapper;
    
    @Autowired
    private AssetCommonMapper assetCommonMapper;
    
    /**
     * 资产代码的联想文本框事件
     * 
     * @param finprodMarketId 资产代码（市场代码）
     * @return 查询的资产代码和资产名称列表
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinProdMarketId(String finprodMarketId){
        Map<String,Object> map = new HashMap <>();
        map.put("TYPE_TOUGUAN_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        map.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        map.put("finprodMarketId", finprodMarketId);
        return finPlanProfitAssetMapper.getFinProdMarketId(map);
    }
    
    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     * @param pageParam pageParam
     * @param page 页码
     * @param length 页面长度
     * @return Page类  Page
     * @throws Exception 异常信息
     */
    public Page<FinPlanProfitAssetDto> selectForPage(int page, int length, Map<String, Object> pageParam)
    {
        pageParam.put("TYPE_TOUGUAN_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        pageParam.put("TYPE_TOUGUAN_F27", DictCodeListExt1.金融产品类型_投管用_母资管计划.getValue());
        pageParam.put("PROFIT_TYPE_02", DictCodeList.收益类型_收益型.getValue());
        String category = (String)pageParam.get("category");
        String type = (String)pageParam.get("type");
        
   	 /*   start、zhuyongkang、2019.7.19、原因   */
        String type9 = (String)pageParam.get("type9");
        String type10 = (String)pageParam.get("type10");
        String type11 = (String)pageParam.get("type11");
        String type12 = (String)pageParam.get("type12");
        if(StringUtil.isNotEmpty(type9)){
            pageParam.put("type9List",StringUtil.splitToList(GlobalConstant.COMMA,type9));
        }
        if(StringUtil.isNotEmpty(type10)){
            pageParam.put("type10List",StringUtil.splitToList(GlobalConstant.COMMA,type10));
        }
//        if(StringUtil.isNotEmpty(type11)){
//            pageParam.put("type11List",StringUtil.splitToList(GlobalConstant.COMMA,type11));
//        }
        if(StringUtil.isNotEmpty(type11)){
            if (type11.indexOf("107")!=-1){
                type11=type11+",125,137,138,139,140";
            }
            List<String> type11List = StringUtil.splitToList(",", type11);
            pageParam.put("type11List", type11List);
        }
        if(StringUtil.isNotEmpty(type12)){
            pageParam.put("type12List",StringUtil.splitToList(GlobalConstant.COMMA,type12));
        }
        /* end、zhuyongkang、2019.7.18 */ 
		
        if(StringUtil.isNotEmpty(category)){
            pageParam.put("categoryList",StringUtil.splitToList(GlobalConstant.COMMA,category));
        }
        if(StringUtil.isNotEmpty(type)){
            pageParam.put("typeList",StringUtil.splitToList(GlobalConstant.COMMA,type));
        }
        return super.queryForPage(page, length, () -> finPlanProfitAssetMapper.select(pageParam));
    }

    /**
     * 删除操作
     * 
     * @param dto 选中参数
     * @see
     * @since 1.0
     */
    public void delete(FinPlanProfitAssetDto dto) {
        List<String> finprodIdList = new ArrayList<>();
        finprodIdList.add(dto.getFinprodId());
        baseFinProductBaseInfoService.delete(finprodIdList);
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = new BaseRepAssetDeptInfoDto();
        baseRepAssetDeptInfoDto.setFinprodIdList(finprodIdList);
        baseRepAssetDeptInfoService.deleteByPrimaryKey(baseRepAssetDeptInfoDto);
        BaseRepTrustInvestModeInfoDto baseRepTrustInvestModeInfoDto = new BaseRepTrustInvestModeInfoDto();
        baseRepTrustInvestModeInfoDto.setRepFinprodIdList(finprodIdList);
        baseRepTrustInvestModeInfoService.delete(baseRepTrustInvestModeInfoDto);
        BaseRepMngAssetPlanInfoDto baseRepMngAssetPlanInfoDto = new BaseRepMngAssetPlanInfoDto();
        baseRepMngAssetPlanInfoDto.setRepFinprodIdList(finprodIdList);
        baseRepMngAssetPlanInfoService.delete(baseRepMngAssetPlanInfoDto);
        paFinProductMapper.deleteByPrimaryKey(dto.getFinprodId());
        PaFinProductBuyoutRateKey paFinProductBuyoutRateKey = new PaFinProductBuyoutRateKey();
        paFinProductBuyoutRateKey.setFinprodId(dto.getFinprodId());
        paFinProductBuyoutRateKey.setEffDate(dto.getVdate());
        paFinProductBuyoutRateMapper.deleteByPrimaryKey(paFinProductBuyoutRateKey);
      //删除委外组合 add wangpeng 2019.07.23
        if(StringUtil.isNotEmpty(dto.getFinprodMarketId())){
        	 ptlPortfolioMapper.deleteByPrimaryKey(dto.getFinprodMarketId());
        }
      //删除授信关联
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(dto.getFinprodId());
        assetCommonService.deleteCredit(creditLines);
        //2020年5月29日 wangyang1 REP_ASSET_OTHER删除数据 start
        repAssetOtherMapper.deleteByPrimaryKey(dto.getFinprodId());
        PaFinCashCalcRule paFinCashCalcRule = dto.getPaFinCashCalcRule();
        if(paFinCashCalcRule!=null) {
            PaFinCashCalcRuleKey key = new PaFinCashCalcRuleKey();
            key.setCashId(paFinCashCalcRule.getCashId());
            key.setEffDate(paFinCashCalcRule.getEffDate());
            paFinCashCalcRuleMapper.deleteByPrimaryKey(key);
        }
        //2020年5月29日 wangyang1 REP_ASSET_OTHER删除数据 end

        // 增加删除子计划 20200903 wj
        // 查询子计划list
        List<String> zifinprodIdList= finProductMapper.getSubProd(dto.getFinprodId());
        if(!StringUtil.isEmptyList(zifinprodIdList)) {
            baseFinProductBaseInfoService.delete(zifinprodIdList);
        }


    }

    /**
     * 插入操作
     *
     * @param dto 选中参数
     * @see
     * @since 1.0
     */
    public void insert(FinPlanProfitAssetDto dto) throws Exception{
        //构建标的类金融产品dto
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        baseFinProductBaseInfoDto.setFinProduct(dto.getFinProduct());
        baseFinProductBaseInfoDto.setFinProductAdd(dto.getFinProductAdd());
        baseFinProductBaseInfoDto.setFinProductType(dto.getFinProductType());
        baseFinProductBaseInfoDto.setFinCashInfoList(dto.getFinCashInfoList());
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(dto.getFinCashCalcRuleList());
        baseFinProductBaseInfoDto.setFinRateInfoList(dto.getFinRateInfoList());
        baseFinProductBaseInfoDto.setFinCashPayRuleList(dto.getFinCashPayRuleList());
        baseFinProductBaseInfoDto.setFinCashPlanList(dto.getFinCashPlanList());
        baseFinProductBaseInfoDto.setPaFinProduct(dto.getPaFinProduct());
        // 增加推荐方与推荐费率 add by renping 20191014 start
        if (dto.getBussFinRecommentDto() != null && !StringUtil.isEmptyList(
            dto.getBussFinRecommentDto().getFinProductRecommentList())) {
            dto.getBussFinRecommentDto().getFinProductRecommentList().forEach(
                finProductRecomment -> {
                    finProductRecomment.setFinprodId(dto.getFinprodId());
                });
            baseFinProductBaseInfoDto.setFinProductRecommentList(
                dto.getBussFinRecommentDto().getFinProductRecommentList());
        }

        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(dto, baseFinProductBaseInfoDto);

        // 增加推荐方与推荐费率 add by renping 20191014 end
        List<BaseFinProductBaseInfoDto> list = new ArrayList<>();
        list.add(baseFinProductBaseInfoDto);
        //调用标的类金融产品基础组件进行插入
        baseFinProductBaseInfoService.insert(list);
        //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        paFinProduct.setFinprodId(dto.getFinProduct().getFinprodId());
        if(StringUtil.isNotEmpty(dto.getSettleDays())){
            paFinProduct.setSettleDays(Long.parseLong(dto.getSettleDays()));
        }
        paFinProduct.setCashOutNo(dto.getCmsId());
        //将数据插入到标的类金融产品扩展表
        paFinProductMapper.insertSelective(paFinProduct);
        
        //将数据插入到现金流计算规则扩展
        if(null!=dto.getPaFinCashCalcRule()){
            paFinCashCalcRuleMapper.insert(dto.getPaFinCashCalcRule());
        }
        //调用资产管理计划中债报送信息基础组件插入数据
        if(StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_信托产品.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_券商资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货资产管理计划.getValue())){
            BaseRepMngAssetPlanInfoDto baseRepMngAssetPlanInfoDto = new BaseRepMngAssetPlanInfoDto();
            List<RepAssetMngPlan> repAssetMngPlanList = new ArrayList<>();
            repAssetMngPlanList.add(dto.getRepAssetMngPlan());
            baseRepMngAssetPlanInfoDto.setRepAssetMngPlanList(repAssetMngPlanList);
            baseRepMngAssetPlanInfoService.insert(baseRepMngAssetPlanInfoDto);
            repAssetOtherMapper.insert(dto.getRepAssetOther());
        }
        //调用委外投资—协议方式中债报送信息基础组件插入数据
        else if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_银行.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_证券公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他.getValue())){
            BaseRepTrustInvestModeInfoDto baseRepTrustInvestModeInfoDto = new BaseRepTrustInvestModeInfoDto();
            List<RepTrustInvestMode> repTrustInvestModeList = new ArrayList<>();
            repTrustInvestModeList.add(dto.getRepTrustInvestMode());
            baseRepTrustInvestModeInfoDto.setRepTrustInvestModeList(repTrustInvestModeList);
            baseRepTrustInvestModeInfoService.insert(baseRepTrustInvestModeInfoDto);
            repAssetOtherMapper.insert(dto.getRepAssetOther());
        }
      //add 2019.7.15 wangpeng 根据配置 决定是否入库组合表信息
        boolean isCreatePortfolio = assetCommonService.selectIsCreatePortfolio(dto.getFinProductType());
        if(isCreatePortfolio){
        	 //组合表插入
            ptlPortfolioMapper.deleteByPrimaryKey(dto.getPtlPortfolio().getPortfolioId());
            ptlPortfolioMapper.insert(dto.getPtlPortfolio());
        }
        //end
        //调用资产负债报送信息组件插入数据
        List<RepAssetDept> repAssetDeptList = new ArrayList<>();
        // 20210720 增加判空校验 by caozhenming start
        if (Objects.nonNull(dto.getRepAssetDept())){
            repAssetDeptList.add(dto.getRepAssetDept());
            for (RepAssetDept r :repAssetDeptList){
                r.setRepCcy(dto.getCcy());
            }
        }
        // 20210720 增加判空校验 by caozhenming end
        baseRepAssetDeptInfoService.insert(repAssetDeptList);
        //金融产品买断利率表插入数据
        PaFinProductBuyoutRateKey paFinProductBuyoutRateKey = new PaFinProductBuyoutRateKey();
        if(dto.getPaFinProductBuyoutRate() != null){
        	paFinProductBuyoutRateKey.setFinprodId(dto.getPaFinProductBuyoutRate().getFinprodId());
        	paFinProductBuyoutRateKey.setEffDate(dto.getPaFinProductBuyoutRate().getEffDate());
        	paFinProductBuyoutRateMapper.deleteByPrimaryKey(paFinProductBuyoutRateKey);
        	paFinProductBuyoutRateMapper.insert(dto.getPaFinProductBuyoutRate());
        }
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
    }

    /**
     * 修改操作
     *
     * @param dto 选中参数
     * @see
     * @since 1.0
     */
    public void update(FinPlanProfitAssetDto dto) throws Exception {
        //构建标的类金融产品dto
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        baseFinProductBaseInfoDto.setFinProduct(dto.getFinProduct());
        baseFinProductBaseInfoDto.setFinProductAdd(dto.getFinProductAdd());
        baseFinProductBaseInfoDto.setFinProductType(dto.getFinProductType());
        baseFinProductBaseInfoDto.setFinCashInfoList(dto.getFinCashInfoList());
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(dto.getFinCashCalcRuleList());
        baseFinProductBaseInfoDto.setFinRateInfoList(dto.getFinRateInfoList());
        baseFinProductBaseInfoDto.setFinCashPayRuleList(dto.getFinCashPayRuleList());
        baseFinProductBaseInfoDto.setFinCashPlanList(dto.getFinCashPlanList());
        baseFinProductBaseInfoDto.setPaFinProduct(dto.getPaFinProduct());
        // 增加推荐方与推荐费率 add by renping 20191014 start
        if (dto.getBussFinRecommentDto() != null && !StringUtil.isEmptyList(
            dto.getBussFinRecommentDto().getFinProductRecommentList())) {
            dto.getBussFinRecommentDto().getFinProductRecommentList().forEach(
                finProductRecomment -> {
                    finProductRecomment.setFinprodId(dto.getFinprodId());
                });
            baseFinProductBaseInfoDto.setFinProductRecommentList(
                dto.getBussFinRecommentDto().getFinProductRecommentList());
        }
        // 增加推荐方与推荐费率 add by renping 20191014 end
        List<String> finprodIdList = new ArrayList<>();
        finprodIdList.add(dto.getFinProduct().getFinprodId());

        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(dto, baseFinProductBaseInfoDto);
        List<BaseFinProductBaseInfoDto> list = new ArrayList<>();
        list.add(baseFinProductBaseInfoDto);
        //调用标的类金融产品基础组件进行修改
        baseFinProductBaseInfoService.update(finprodIdList,list);
        if(dto.getFinCashPlanList()!=null && dto.getFinCashPlanList().size()>0){
            //修改付息计划，同步更新应收款持仓表计划支付日
            basePtlRecePositionService.updateRecePayDate(dto.getFinCashPlanList());
        }

        // START 母计划保本标识修改  同步更新子计划的保本标识。无需判断 有没有改 直接更新。
        // TISHKBS-1158 对已经做过交易的计划类利率型母资产交易后跑过批后，在计划类利率型母资产标的中修改保本标识，重新跑批后，计税流水并未按修改后的保本标识进行计税
        FinProduct pfinProduct = new FinProduct();
        pfinProduct.setpFinprodId(dto.getFinProduct().getFinprodId());
        pfinProduct.setCapiIncomeFeature(dto.getFinProduct().getCapiIncomeFeature());
        finProductMapper.updateSonProdByPProd(pfinProduct);
        // end

        //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        paFinProduct.setFinprodId(dto.getFinProduct().getFinprodId());
        if(StringUtil.isNotEmpty(dto.getSettleDays())){
            paFinProduct.setSettleDays(Long.parseLong(dto.getSettleDays()));
        }
        paFinProduct.setCashOutNo(dto.getCmsId());
        //对标的类金融产品扩展表的数据进行修改
        paFinProductMapper.updateByPrimaryKeySelective(paFinProduct); 
        //对现金流计算规则扩展表的数据进行修改
        if(dto.getPaFinCashCalcRule() != null){
        	paFinCashCalcRuleMapper.updateByPrimaryKeySelective(dto.getPaFinCashCalcRule());
        }
        //调用资产管理计划中债报送信息基础组件修改数据
        if(StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_信托产品.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_券商资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他资产管理计划.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货资产管理计划.getValue())){
            BaseRepMngAssetPlanInfoDto baseRepMngAssetPlanInfoDto = new BaseRepMngAssetPlanInfoDto();
            List<RepAssetMngPlan> repAssetMngPlanList = new ArrayList<>();
            repAssetMngPlanList.add(dto.getRepAssetMngPlan());
            baseRepMngAssetPlanInfoDto.setRepAssetMngPlanList(repAssetMngPlanList);
            List<String> repFinprodIdList = new ArrayList<>();
            repFinprodIdList.add(dto.getFinProduct().getFinprodId());
            baseRepMngAssetPlanInfoDto.setRepFinprodIdList(repFinprodIdList);
            baseRepMngAssetPlanInfoService.update(baseRepMngAssetPlanInfoDto);
        }
        //调用委外投资—协议方式中债报送信息基础组件修改数据
        else if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_银行.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_证券公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_基金公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_期货公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_保险公司.getValue())
                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_其他.getValue())){
            BaseRepTrustInvestModeInfoDto baseRepTrustInvestModeInfoDto = new BaseRepTrustInvestModeInfoDto();
            List<RepTrustInvestMode> repTrustInvestModeList = new ArrayList<>();
            repTrustInvestModeList.add(dto.getRepTrustInvestMode());
            baseRepTrustInvestModeInfoDto.setRepTrustInvestModeList(repTrustInvestModeList);
            List<String> repFinprodIdList = new ArrayList<>();
            repFinprodIdList.add(dto.getFinProduct().getFinprodId());
            baseRepTrustInvestModeInfoDto.setRepFinprodIdList(repFinprodIdList);
            baseRepTrustInvestModeInfoService.update(baseRepTrustInvestModeInfoDto);
        }
        //调用资产负债报送信息组件修改数据
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = new BaseRepAssetDeptInfoDto();
        List<RepAssetDept> repAssetDeptList = new ArrayList<>();
        // 20210720 增加判空校验 by caozhenming start
        if (Objects.nonNull(dto.getRepAssetDept())) {
            repAssetDeptList.add(dto.getRepAssetDept());
            for (RepAssetDept r : repAssetDeptList) {
                r.setRepCcy(dto.getCcy());
            }
        }
        baseRepAssetDeptInfoDto.setRepAssetDeptList(repAssetDeptList);
        List<String> finProdIdList = new ArrayList<>();
        finProdIdList.add(dto.getFinProduct().getFinprodId());
        baseRepAssetDeptInfoDto.setFinprodIdList(finProdIdList);
        baseRepAssetDeptInfoService.updateBaseRepAssetDeptInfoDto(baseRepAssetDeptInfoDto);
        //修改金融产品买断利率表数据
        if(dto.getPaFinProductBuyoutRate()!=null){
        	paFinProductBuyoutRateMapper.updateByPrimaryKeySelective(dto.getPaFinProductBuyoutRate());
        }
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
        /**
         * 存在子计划，则修改期次管理 add by hxy 2020.03.28 
         */
        updateFinStageManagement(dto);
    }
    /**
     * 出账编号的联想文本框事件
     *
     * @param map 输入管理人参数
     * @return 查询的管理人代码和管理人名称列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCmsId(Map<String,Object> map) {
        return finPlanProfitAssetMapper.getCmsId(map);
    }

    /**
     * 出账编号的联动事件
     *
     * @param cmsId 输入管理人参数
     * @return 查询的管理人代码和管理人名称列表
     * @see
     * @since 1.0
     */
    public FinPlanProfitAssetDto getChange(String cmsId) {
        return finPlanProfitAssetMapper.getChange(cmsId);
    }

    /**
     * 管理人的联想下拉框事件
     * 
     * @param map 输入管理人参数
     * @return 查询的管理人代码和管理人名称列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getIssuer(Map<String,Object> map) {
        map.put("YESORNO_YES", DictCodeList.是否_是.getValue());
        return finPlanProfitAssetMapper.getIssuer(map);
    }

    /**
     * 托管人的联想下拉框事件
     * 
     * @param map 输入托管人参数
     * @return 查询的托管人代码和托管人名称列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getTrusteeId(Map<String,Object> map) {
        map.put("YESORNO_YES", DictCodeList.是否_是.getValue());
        return finPlanProfitAssetMapper.getTrusteeId(map);
    }

    /**
     * 交易日历的联想下拉框事件
     * 
     * @param map 输入交易日历参数
     * @return 查询的交易日历代码和交易日历名称列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCalendarId(Map<String,Object> map) {
        return finPlanProfitAssetMapper.getCalendarId(map);
    }
    
    /**
     * 放款行的联想下拉框事件
     * 
     * @param map 输入放款行参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getBank(Map<String,Object> map) {
        map.put("COUNTER_TYPE_04", DictCodeListExt1.交易对手类型_分行.getValue());
        return finPlanProfitAssetMapper.getBank(map);
    }

    /**
     * 资产提供行的多选下拉框事件
     * 
     * @param map 查询参数
     * @return 查询结果
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getBankList(Map<String,Object> map) {
        map.put("COUNTER_TYPE_04", DictCodeListExt1.交易对手类型_分行.getValue());
        return finPlanProfitAssetMapper.getBankList(map);
    }
    /**
     * 修改或双击查看界面显示
     *
     * @param finprodId 金融产品代码
     * @return 基本信息页面值
     * @see
     * @since 1.0
     */
    public FinPlanProfitAssetDto getBasicInfoByFinprodId(String finprodId){
        Map<String,Object> map = new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("FIN_CASH_BUSINESS_TYPE_01",DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("CASH_TYPE_1_02",DictCodeListExt1.现金流一级类型_利息.getValue());
        map.put("TYPE_TOUGUAN_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        List<FinPlanProfitAssetDto> resultList = finPlanProfitAssetMapper.getBasicInfoByFinprodId(map);
        if(resultList!=null && resultList.size()>0) {
            FinPlanProfitAssetDto finPlanAssetR = resultList.get(0);
            return finPlanAssetR;
        }
        else return null;
    }
    /**
     * 查询市场代码
     *
     * @param finprodMarketId 市场代码
     * @return 查询到的市场代码
     * @see
     * @since 1.0
     */
    public int selectFinprodMarketId(String finprodMarketId){
        return finPlanProfitAssetMapper.selectFinprodMarketId(finprodMarketId);
    }

    /**
     * 查询客户名称
     *
     * @param custormerId 客户代码
     * @return 客户名称
     * @see
     * @since 1.0
     */
    public String getName(String custormerId){
        return finPlanProfitAssetMapper.getName(custormerId);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 start*/
    public String getFinprodId(String finprodMarketId){
        return finPlanProfitAssetMapper.getFinprodId(finprodMarketId);
    }
    /* 增加快捷录入 add by zhuyongkang 2019/10/12 end*/
    /*增加母计划修改联动子计划 add by hxy 2020.03.28 start*/
    public void updateFinStageManagement(FinPlanProfitAssetDto dto){
    	//查询母计划的所有子计划
    	FinProduct prodDto= dto.getFinProduct();
        List<String> subFinProdIds=finPlanProfitAssetMapper.getFinprodIdByPFid(prodDto.getFinprodId());
    	if(null!=subFinProdIds && subFinProdIds.size()>0 ){
    		for(String subId:subFinProdIds){
    			FinProduct item=new FinProduct();
        		item.setFinprodId(subId);
        		item.setManager(prodDto.getManager());
        		finProductMapper.updateByPrimaryKeySelective(item);
    		}
    		
    	}
    }
    /*增加母计划修改联动子计划 add by hxy 2020.03.28  end*/

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
    private void procFinProductOpenPlan(FinPlanProfitAssetDto dto,
                                        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto)
            throws Exception {
        if (dto.getBussFinProductOpenPlanDto() != null) {
            // 现金流信息dtolist-->本金
            String cashId_amt = CommonUtil.generateCashFlowCode(
                    baseFinProductBaseInfoDto.getFinProduct().getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), 0L,
                    DictCodeListExt1.现金流二级类型_本金.getValue());
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
