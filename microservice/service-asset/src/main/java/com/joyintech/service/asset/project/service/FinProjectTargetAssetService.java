/*
 * 文件名：FinPorjectTargetAssetService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月24日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.asset.project.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.common.dao.AssetCommonMapper;
import com.joyintech.service.asset.common.dao.BussFinProductCreditMapper;
import com.joyintech.service.asset.common.dao.BussPaFinProductBuyoutRateMapper;
import com.joyintech.service.asset.common.dao.BussReduceShareInfoMapper;
import com.joyintech.service.asset.common.dao.BussSubscriptionInfoMapper;
import com.joyintech.service.asset.common.service.AssetCommonService;
import com.joyintech.service.asset.common.service.BusiFinBondRatingInfoService;
import com.joyintech.service.asset.common.service.FinInterestFactorService;
import com.joyintech.service.asset.project.dao.FinProjectTargetAssetMapper;
import com.joyintech.service.asset.project.dto.FinProjectTargetAssetCmsDto;
import com.joyintech.service.asset.project.dto.FinProjectTargetAssetDto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.report.asset.*;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetAstBillInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetCreditNonstdInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetEquityInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepBondAssetInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepFundAssetInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepMngAssetPlanInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepOtherInvestTargetInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tools.util.ListUtils;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目类资产标的信息管理
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月24日
 * @see
 * @since 1.0
 */
@Service("backFinProjectTargetAssetService")
public class FinProjectTargetAssetService extends BaseService<FinProjectTargetAssetDto>{

    @Autowired
    private FinProjectTargetAssetMapper finProjectTargetAssetMapper;

    @Autowired
    private BaseFinProductBaseInfoService baseFinProductBaseInfoService;

    @Autowired(required = false)
    private FinInterestFactorService finInterestFactorService;

    /**
     * 非标资产(票据或信用)报送信息(基础组件)
     */
    @Autowired(required = false)
    private BaseRepAssetAstBillInfoService baseRepAssetAstBillInfoService;

    /**
     * 信贷及非标资产报送(基础组件)
     */
    @Autowired(required = false)
    private BaseRepAssetCreditNonstdInfoService baseRepAssetCreditNonstdInfoService;

    /**
     * 权益类资产中债报送(基础组件)
     */
    @Autowired(required = false)
    private BaseRepAssetEquityInfoService baseRepAssetEquityInfoService;

    /**
     * 其他资产报送信息
     */
    @Autowired(required = false)
    private BaseRepOtherInvestTargetInfoService baseRepOtherInvestTargetInfoService;

    /**
     * 基金资产报送信息
     */
    @Autowired(required = false)
    private BaseRepFundAssetInfoService baseRepFundAssetInfoService;

    /**
     * 债券资产报送
     */
    @Autowired(required = false)
    private BaseRepBondAssetInfoService baseRepBondAssetInfoService;

    /**
     * 计划类报送
     */
    @Autowired(required = false)
    private BaseRepMngAssetPlanInfoService baseRepMngAssetPlanInfoService;
    /**
     * 资产负债(基础组件)
     */
    @Autowired(required = false)
    private BaseRepAssetDeptInfoService baseRepAssetDeptInfoService;

    /**
     * 金融产品扩展(平安)
     */
    @Autowired
    private PaFinProductMapper  paFinProductMapper;
    /**
     * 金融产品买断利率(平安,生成)
     */
    @Autowired
    private PaFinProductBuyoutRateMapper paFinProductBuyoutRateMapper;

    /**
     * 金融产品买断利率(平安,组件)
     */
    @Autowired
    private BussPaFinProductBuyoutRateMapper bussPaFinProductBuyoutRateMapper;

    /**
     * 投后信息
     */
    @Autowired
    private FinPostInvestInfoMapper finPostInvestInfoMapper;

    /**
     * 减持信息(生成)
     */
    @Autowired
    private FinReduceShareInfoMapper finReduceShareInfoMapper;

    /**
     * 减持信息(组件)
     */
    @Autowired
    private BussReduceShareInfoMapper bussReduceShareInfoMapper;

    /**
     * 认购信息(生成)
     */
    @Autowired
    private FinSubscriptionInfoMapper finSubscriptionInfoMapper;

    /**
     * 认购信息(组件)
     */
    @Autowired
    private BussSubscriptionInfoMapper  bussSubscriptionInfoMapper;

    /**
     * 平安附件信息(生成)
     */
    @Autowired
    private PaFinProductAssetAttachMapper paFinProductAssetAttachMapper;
  //标的共通service
    @Autowired(required=false)
    private AssetCommonService assetCommonService;

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
     * 增信信息
     */
    @Autowired
    private FinProductCreditMapper finProductCreditMapper;

    @Autowired
    private BussFinProductCreditMapper bussFinProductCreditMapper;

    @Autowired(required = false)
    private BaseRepOtherInfoService baseRepOtherInfoService;

    @Autowired
    private MstBondCreditRatingMapper  mstBondCreditRatingMapper;

    @Autowired(required = false)
    private BusiFinBondRatingInfoService busiFinBondRatingInfoService;

    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    @Autowired
    private AssetCommonMapper assetCommonMapper;
    /**
     * 查询
     * <功能详细描述>
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<FinProjectTargetAssetDto> selectForPage(int page, int length, Map<String, Object> pageParam)
    {
        return super.queryForPage(page, length, () -> finProjectTargetAssetMapper.queryForPage(pageParam));
    }

    /**
     * 插入
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public void insert(FinProjectTargetAssetDto entity) {
        //标的金融产品信息
        baseFinProductBaseInfoService.insert(entity.getBaseFinProductBaseInfoList());
        //金融产品扩展(单独针对平安)
        if(null!=entity.getPaFinProduct()){
            paFinProductMapper.insert(entity.getPaFinProduct());
        }
        //金融产品买断利率(单独针对平安)
        if(null!=entity.getPaFinProductBuyoutRate()){
            paFinProductBuyoutRateMapper.insert(entity.getPaFinProductBuyoutRate());
        }
        //投后信息
        if(null!=entity.getBussPostInvestInfoDto()){
            finPostInvestInfoMapper.insert(entity.getBussPostInvestInfoDto());
        }
        //减持信息
        if(null!=entity.getBussReduceShareInfoDto()){
            for(FinReduceShareInfo finReduceShareInfo:entity.getBussReduceShareInfoDto().getFinReduceShareInfoList()){
                finReduceShareInfoMapper.insert(finReduceShareInfo);
            }
        }
        //认购信息
        if(null!=entity.getBussSubscriptionInfoDto()){
            for(FinSubscriptionInfo finSubscriptionInfo:entity.getBussSubscriptionInfoDto().getFinSubscriptionInfoList()){
                finSubscriptionInfoMapper.insert(finSubscriptionInfo);
            }
        }
        //增信信息
        if(null!=entity.getBussFinProductCreditDto()){
            for(FinProductCredit finProductCredit:entity.getBussFinProductCreditDto().getFinProductCreditList()){
                finProductCreditMapper.insert(finProductCredit);
            }
        }
        //中债报送
        //非标资产(票据或信用)报送信息
        if(null!=baseRepAssetAstBillInfoService){
            if(null!=entity.getBaseRepAssetAstBillInfoDto()){
                List<RepAssetAstBill> bills = entity.getBaseRepAssetAstBillInfoDto().getRepAssetAstBillList();
//                for (RepAssetAstBill r: bills) {
//                    r.setRepIndustry(entity.getPaFinProduct().getProjInvestIndustry());
//                    r.setRepIndustryLevel2(entity.getPaFinProduct().getProjInvestIndustry2());
//                }
                baseRepAssetAstBillInfoService.insert(bills);
            }
        }
        //信贷及非标资产报送
        if(null!=baseRepAssetCreditNonstdInfoService){
            if(null!=entity.getBaseRepAssetCreditNonstdInfoDto()){
            	//BS-36 新增可投资资产中，部分赎回标识默认的是否，资产要素登记中该字段也是否，期望应为空 start
            	if(StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_信贷资产流转项目.getValue())
            			|| StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_小微企业信贷资产流转和收益权转让.getValue())){
            		RepAssetCreditNonstd racnDto= entity.getBaseRepAssetCreditNonstdInfoDto().getRepAssetCreditNonstdList().get(0);
            		racnDto.setRepPartRedemFlag(null);
            	}
            	//BS-36 新增可投资资产中，部分赎回标识默认的是否，资产要素登记中该字段也是否，期望应为空 end
            	/*else{
            		RepAssetCreditNonstd racnDto= entity.getBaseRepAssetCreditNonstdInfoDto().getRepAssetCreditNonstdList().get(0);
            		racnDto.setRepIsResale(null);
            	}*/
                baseRepAssetCreditNonstdInfoService.insert(entity.getBaseRepAssetCreditNonstdInfoDto().getRepAssetCreditNonstdList());
            }
        }
        //权益类资产中债报送
        if(null!=baseRepAssetEquityInfoService){
            if(null!=entity.getBaseRepAssetEquityInfoDto()){
                List<RepAssetEquity> list = entity.getBaseRepAssetEquityInfoDto().getRepAssetEquityList();
                for (RepAssetEquity e:list) {
                    //中债需求  当资产类别=P02时，没有股票代码，则REP_SHARES_ID取资产代码
                    if (StringUtil.isEmpty(entity.getBussPostInvestInfoDto().getShareCode())){
                        e.setRepSharesId(entity.getFinprodMarketId());
                    }else{
                        e.setRepSharesId(entity.getBussPostInvestInfoDto().getShareCode());
                    }
                    //中债需求  当资产类别=P02时，没有股票代码，则REP_SHARES_ID取资产代码
                    e.setRepIndustry(entity.getPaFinProduct().getProjInvestIndustry());
                    e.setRepIndustryLevel2(entity.getPaFinProduct().getProjInvestIndustry2());
                }
                baseRepAssetEquityInfoService.insert(list);
            }
        }
        //其他资产报送信息
        if(null!=baseRepOtherInvestTargetInfoService){
            if(null!=entity.getBaseRepOtherInvestTargetInfoDto()){
            	if(StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他非标准化债权类投资.getValue())
            			|| StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他新增可投资资产.getValue())){
            		//RepOtherInvestTarget racnDto= entity.getBaseRepOtherInvestTargetInfoDto().getRepOtherInvestTargetList().get(0);
            		//racnDto.setRepIndustryFlag(null);
            		//racnDto.setRepContainExpectedYield2(null);
            	}
                baseRepOtherInvestTargetInfoService.insert(entity.getBaseRepOtherInvestTargetInfoDto().getRepOtherInvestTargetList());
            }
        }
        //基金资产报送信息
        if(null!=baseRepFundAssetInfoService){
            if(null!=entity.getBaseRepFundAssetInfoDto()){
                baseRepFundAssetInfoService.insert(entity.getBaseRepFundAssetInfoDto());
            }
        }
        //债券资产报送
        if(null!=baseRepBondAssetInfoService){
            if(null!=entity.getBaseRepBondAssetInfoDto()){
                baseRepBondAssetInfoService.insert(entity.getBaseRepBondAssetInfoDto().getRepAssetBondList());
            }
        }
        //计划类报送
        if(null!=baseRepMngAssetPlanInfoService){
            if(null!=entity.getBaseRepMngAssetPlanInfoDto()){
                baseRepMngAssetPlanInfoService.insert(entity.getBaseRepMngAssetPlanInfoDto());
            }
        }
        //add by zhangnian 20191014 start
        //中债新增信息
        if(null!=baseRepOtherInfoService){
            if(null!=entity.getBaseRepOtherDto()){
                baseRepOtherInfoService.insert(entity.getBaseRepOtherDto().getRepAssetOtherList());
            }
        }
        //add by zhangnian 20191014 end
        //资产负债
        if(null!=baseRepAssetDeptInfoService){
            if(null!=entity.getBaseRepAssetDeptInfoDto()){
                baseRepAssetDeptInfoService.insert(entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList());
            }
        }
        //计算规则扩展--单独针对平安
        if(null!=finInterestFactorService){
            finInterestFactorService.insertPaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
        }
        //PA_FIN_PRODUCT_ASSET_ATTACH  cms附件数据
        if(StringUtil.isNotEmpty(entity.getAssetAttachNo1())){
            PaFinProductAssetAttach paFinProductAssetAttach = new PaFinProductAssetAttach();
            paFinProductAssetAttach.setAssetAttachNo(entity.getAssetAttachNo1());
            paFinProductAssetAttach.setFinprodId(entity.getFinprodId());
            paFinProductAssetAttach.setAssetAttachType(DictCodeList.资产附件类型_批复文件.getValue());
            paFinProductAssetAttachMapper.insert(paFinProductAssetAttach);
        }
        if(StringUtil.isNotEmpty(entity.getAssetAttachNo2())){
            PaFinProductAssetAttach paFinProductAssetAttach = new PaFinProductAssetAttach();
            paFinProductAssetAttach.setAssetAttachNo(entity.getAssetAttachNo2());
            paFinProductAssetAttach.setFinprodId(entity.getFinprodId());
            paFinProductAssetAttach.setAssetAttachType(DictCodeList.资产附件类型_CMS批复文件.getValue());
            paFinProductAssetAttachMapper.insert(paFinProductAssetAttach);
        }
        //add 2019.7.15 wangpeng 根据配置 决定是否入库组合表信息
        boolean isCreatePortfolio = false;
        if(entity.getBaseFinProductBaseInfoList()!=null&& !entity.getBaseFinProductBaseInfoList().isEmpty()  ){
        	FinProductType type = entity.getBaseFinProductBaseInfoList().get(0).getFinProductType();
        	isCreatePortfolio = assetCommonService.selectIsCreatePortfolio(type);
        }
        if(isCreatePortfolio){
        	 //组合表插入
            ptlPortfolioMapper.deleteByPrimaryKey(entity.getPtlPortfolio().getPortfolioId());
            ptlPortfolioMapper.insert(entity.getPtlPortfolio());
        }
        //end
        /**
         * 增加授信关联
         */
        if(entity.getPflsh() != null && !StringUtil.equals(entity.getPflsh(), "")){
            CreditLines creditLines = new CreditLines();
            creditLines.setCustomerId(entity.getFinprodId());
            creditLines.setSeqno(entity.getSeqno());
            creditLines.setCreditMstId(entity.getCreditMstId());
            assetCommonService.insertCredit(creditLines);
        }
        /**
         * 20191126	add
         * 评级信息
         */
        if(entity.getBusiFinBondRatingInfoDto() != null
        		&& !entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList().isEmpty()){
        	List<MstBondCreditRating> MstBondCreditRatingList = entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList();
        	for(MstBondCreditRating mstBondCreditRating : MstBondCreditRatingList) {
//        		if("02".equals(mstBondCreditRating.getoType())) {
        			mstBondCreditRating.setoCode(entity.getFinprodMarketId());
        			mstBondCreditRating.setShortLongTerm(DictCodeList.长期短期_长期.getValue());
            		mstBondCreditRatingMapper.insert(mstBondCreditRating);
//            	}
        	}
        }
    }

    /**
     * 修改
     * @param entity
     * @see
     * @since 1.0
     */
    public void update(FinProjectTargetAssetDto entity){
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(entity.getFinprodId());
        baseFinProductBaseInfoService.update(finprodIdList, entity.getBaseFinProductBaseInfoList());
        //修改付息计划，同步更新应收款持仓表计划支付日
        for (BaseFinProductBaseInfoDto dto : entity.getBaseFinProductBaseInfoList()) {
            if (dto.getFinCashPlanList() != null && dto.getFinCashPlanList().size() > 0) {
                basePtlRecePositionService.updateRecePayDate(dto.getFinCashPlanList());
            }
        }
        //金融产品扩展(单独针对平安)
        if(null!=entity.getPaFinProduct()){
            paFinProductMapper.deleteByPrimaryKey(entity.getPaFinProduct().getFinprodId());
            paFinProductMapper.insert(entity.getPaFinProduct());
        }
        //金融产品买断利率(单独针对平安)
        if(null!=entity.getPaFinProductBuyoutRate()){
            PaFinProductBuyoutRateKey key = new PaFinProductBuyoutRateKey();
            key.setFinprodId(entity.getFinprodId());
            key.setEffDate(entity.getOldVdate());
            paFinProductBuyoutRateMapper.deleteByPrimaryKey(key);
            paFinProductBuyoutRateMapper.insert(entity.getPaFinProductBuyoutRate());
        }
        //投后信息
        if(null!=entity.getBussPostInvestInfoDto()){
            finPostInvestInfoMapper.deleteByPrimaryKey(entity.getFinprodId());
            finPostInvestInfoMapper.insert(entity.getBussPostInvestInfoDto());
        }
        //减持信息
        if(null!=entity.getBussReduceShareInfoDto()){
            bussReduceShareInfoMapper.deleteByProductId(entity.getFinprodId());
            for(FinReduceShareInfo finReduceShareInfo:entity.getBussReduceShareInfoDto().getFinReduceShareInfoList()){
                finReduceShareInfoMapper.insert(finReduceShareInfo);
            }
        }
        //认购信息
        if(null!=entity.getBussSubscriptionInfoDto()){
            bussSubscriptionInfoMapper.deleteByProductId(entity.getFinprodId());
            for(FinSubscriptionInfo finSubscriptionInfo:entity.getBussSubscriptionInfoDto().getFinSubscriptionInfoList()){
                finSubscriptionInfoMapper.insert(finSubscriptionInfo);
            }
        }
        //增信信息
        if(null!=entity.getBussFinProductCreditDto()){
            bussFinProductCreditMapper.deleteFinProductCreditByProductId(entity.getFinprodId());
            for(FinProductCredit finProductCredit:entity.getBussFinProductCreditDto().getFinProductCreditList()){
                finProductCreditMapper.insert(finProductCredit);
            }
        }
        //中债报送
        //add by 20200428 TISHKBS-293 删除原交易的中债报送信息
        if(null!=finprodIdList && finprodIdList.size()>0 ){
        	deleteRepInfoById(finprodIdList);
         }
        //非标资产(票据或信用)报送信息
        if(null!=baseRepAssetAstBillInfoService){
        	if(null!=entity.getBaseRepAssetAstBillInfoDto()){
                List<RepAssetAstBill> bills = entity.getBaseRepAssetAstBillInfoDto().getRepAssetAstBillList();
//                for (RepAssetAstBill r : bills) {
//                    r.setRepIndustry(entity.getPaFinProduct().getProjInvestIndustry());
//                    r.setRepIndustryLevel2(entity.getPaFinProduct().getProjInvestIndustry2());
//                }
                entity.getBaseRepAssetAstBillInfoDto().setRepAssetAstBillList(bills);
                baseRepAssetAstBillInfoService.update(entity.getBaseRepAssetAstBillInfoDto());
            }
        }
        //信贷及非标资产报送
        if(null!=baseRepAssetCreditNonstdInfoService){
            if(null!=entity.getBaseRepAssetCreditNonstdInfoDto()){
            	//BS-36 新增可投资资产中，部分赎回标识默认的是否，资产要素登记中该字段也是否，期望应为空 start
            	if(StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_信贷资产流转项目.getValue())
            			|| StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_小微企业信贷资产流转和收益权转让.getValue())){
            		RepAssetCreditNonstd racnDto= entity.getBaseRepAssetCreditNonstdInfoDto().getRepAssetCreditNonstdList().get(0);
            		racnDto.setRepPartRedemFlag(null);
            	}
            	//BS-36 新增可投资资产中，部分赎回标识默认的是否，资产要素登记中该字段也是否，期望应为空 end
            	/*else{
            		RepAssetCreditNonstd racnDto= entity.getBaseRepAssetCreditNonstdInfoDto().getRepAssetCreditNonstdList().get(0);
            		racnDto.setRepIsResale(null);
            	}*/
                baseRepAssetCreditNonstdInfoService.update(entity.getBaseRepAssetCreditNonstdInfoDto());
            }
        }
        //权益类资产中债报送
        if(null!=baseRepAssetEquityInfoService){
            if(null!=entity.getBaseRepAssetEquityInfoDto()){
                //REP_ASSET_EQUITY表，股票代码，行业一级分类，行业二级分类 存值 yjm start 20190802
                List<RepAssetEquity> list = entity.getBaseRepAssetEquityInfoDto().getRepAssetEquityList();
                for (RepAssetEquity e:list) {
                    //中债需求  当资产类别=P02时，没有股票代码，则REP_SHARES_ID取资产代码
                    if (StringUtil.isEmpty(entity.getBussPostInvestInfoDto().getShareCode())){
                        e.setRepSharesId(entity.getFinprodMarketId());
                    }else{
                        e.setRepSharesId(entity.getBussPostInvestInfoDto().getShareCode());
                    }
                    //中债需求  当资产类别=P02时，没有股票代码，则REP_SHARES_ID取资产代码
                    e.setRepIndustry(entity.getPaFinProduct().getProjInvestIndustry());
                    e.setRepIndustryLevel2(entity.getPaFinProduct().getProjInvestIndustry2());
                }
                entity.getBaseRepAssetEquityInfoDto().setRepAssetEquityList(list);
                //REP_ASSET_EQUITY表，股票代码，行业一级分类，行业二级分类 存值 yjm start 20190802
                baseRepAssetEquityInfoService.update(entity.getBaseRepAssetEquityInfoDto());
            }
        }
        //其他资产报送信息
        if(null!=baseRepOtherInvestTargetInfoService){
            if(null!=entity.getBaseRepOtherInvestTargetInfoDto()){
            	if(StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他非标准化债权类投资.getValue())
            			|| StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_其他新增可投资资产.getValue())){
            		//RepOtherInvestTarget racnDto= entity.getBaseRepOtherInvestTargetInfoDto().getRepOtherInvestTargetList().get(0);
            		//racnDto.setRepIndustryFlag(null);
            		//racnDto.setRepContainExpectedYield2(null);
            	}
                baseRepOtherInvestTargetInfoService.update(entity.getBaseRepOtherInvestTargetInfoDto());
            }
        }
        //基金资产报送信息
        if(null!=baseRepFundAssetInfoService){
            if(null!=entity.getBaseRepFundAssetInfoDto()){
                baseRepFundAssetInfoService.update(entity.getBaseRepFundAssetInfoDto());
            }
        }
        //债券资产报送
        if(null!=baseRepBondAssetInfoService){
            if(null!=entity.getBaseRepBondAssetInfoDto()){
                baseRepBondAssetInfoService.update(entity.getBaseRepBondAssetInfoDto());
            }
        }
        //计划类报送
        if(null!=baseRepMngAssetPlanInfoService){
            if(null!=entity.getBaseRepMngAssetPlanInfoDto()){
                baseRepMngAssetPlanInfoService.update(entity.getBaseRepMngAssetPlanInfoDto());
            }
        }
        //add by zhangnian 20191014 start
        //中债新增信息
        if(null!=baseRepOtherInfoService){
            if(null!=entity.getBaseRepOtherDto()){
                baseRepOtherInfoService.update(entity.getBaseRepOtherDto());
            }
        }
        //add by zhangnian 20191014 end


        //资产负债
        if(null!=baseRepAssetDeptInfoService){
            if(null!=entity.getBaseRepAssetDeptInfoDto()){
                baseRepAssetDeptInfoService.updateBaseRepAssetDeptInfoDto(entity.getBaseRepAssetDeptInfoDto());
            }
        }
        //计算规则扩展--单独针对平安
        if(null!=finInterestFactorService){
            finInterestFactorService.deletePaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
            finInterestFactorService.insertPaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
        }
        //PA_FIN_PRODUCT_ASSET_ATTACH  cms附件数据
        finProjectTargetAssetMapper.deleteAssetAttachNoByFinprodId(entity.getFinprodId());
        if(StringUtil.isNotEmpty(entity.getAssetAttachNo1())){
            PaFinProductAssetAttach paFinProductAssetAttach = new PaFinProductAssetAttach();
            paFinProductAssetAttach.setAssetAttachNo(entity.getAssetAttachNo1());
            paFinProductAssetAttach.setFinprodId(entity.getFinprodId());
            paFinProductAssetAttach.setAssetAttachType(DictCodeList.资产附件类型_批复文件.getValue());
            paFinProductAssetAttachMapper.insert(paFinProductAssetAttach);
        }
        if(StringUtil.isNotEmpty(entity.getAssetAttachNo2())){
            PaFinProductAssetAttach paFinProductAssetAttach = new PaFinProductAssetAttach();
            paFinProductAssetAttach.setAssetAttachNo(entity.getAssetAttachNo2());
            paFinProductAssetAttach.setFinprodId(entity.getFinprodId());
            paFinProductAssetAttach.setAssetAttachType(DictCodeList.资产附件类型_CMS批复文件.getValue());
            paFinProductAssetAttachMapper.insert(paFinProductAssetAttach);
        }
        /**
         * 修改授信关联
         */
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(entity.getFinprodId());
        List<Map> credMsg = assetCommonMapper.getMstIdByFinproId(entity.getFinprodId());
        if(credMsg.size()>0) {
        	Map<String,String> credMsgMap = (Map<String,String>)credMsg.get(0);
        	creditLines.setCreditMstId(credMsgMap.get("CREDIT_MST_ID"));
        	creditLines.setSeqno(credMsgMap.get("SEQNO"));
        }
        assetCommonService.updateCredit(creditLines);
        /**
         * 评级数据修改
         * 强删强增	20191126
         */
        busiFinBondRatingInfoService.deleteMstBondCreditRatingByProductId(entity.getFinprodMarketId());
        if(null!=entity.getBusiFinBondRatingInfoDto()){
            if(null!=entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList()
                &&!entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList().isEmpty()){
                List<MstBondCreditRating> mstBondCreditRatingList = entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList();
                for(MstBondCreditRating mstBondCreditRatingEntity:mstBondCreditRatingList){
                	//20191125	add  债券标的评级修改，只处理债项数据。
//                	if("02".equals(mstBondCreditRatingEntity.getoType())) {
                        mstBondCreditRatingEntity.setShortLongTerm(DictCodeList.长期短期_长期.getValue());
                		mstBondCreditRatingEntity.setoCode(entity.getFinprodMarketId());
                		mstBondCreditRatingMapper.insert(mstBondCreditRatingEntity);
//                	}
                }
            }
        }
    }

    /**
     * 删除
     * @param entity
     * @see
     * @since 1.0
     */
    public void delete(FinProjectTargetAssetDto entity) {
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(entity.getFinprodId());
        //标的金融产品信息
        baseFinProductBaseInfoService.delete(finprodIdList);
        //金融产品扩展(单独针对平安)
        if(null!=entity.getPaFinProduct()){
            for(String finprodId:finprodIdList){
                paFinProductMapper.deleteByPrimaryKey(finprodId);
            }
        }
        //金融产品买断利率(单独针对平安)
        if(null!=entity.getPaFinProductBuyoutRate()){
            for(String finprodId:finprodIdList){
                bussPaFinProductBuyoutRateMapper.deleteByfinprodId(finprodId);
            }
        }
        //投后信息
        if(null!=entity.getBussPostInvestInfoDto()){
            for(String finprodId:finprodIdList){
                finPostInvestInfoMapper.deleteByPrimaryKey(finprodId);
            }
        }
        //减持信息
        if(null!=entity.getBussReduceShareInfoDto()){
            for(FinReduceShareInfo finReduceShareInfo:entity.getBussReduceShareInfoDto().getFinReduceShareInfoList()){
                bussReduceShareInfoMapper.deleteByProductId(finReduceShareInfo.getFinprodId());
            }
        }
        //认购信息
        if(null!=entity.getBussSubscriptionInfoDto()){
            for(FinSubscriptionInfo finSubscriptionInfo:entity.getBussSubscriptionInfoDto().getFinSubscriptionInfoList()){
                bussSubscriptionInfoMapper.deleteByProductId(finSubscriptionInfo.getFinprodId());
            }
        }
        //增信信息
        if(null!=entity.getBussFinProductCreditDto()){
            bussFinProductCreditMapper.deleteFinProductCreditByProductId(entity.getFinprodId());
        }
      //中债报送
        //非标资产(票据或信用)报送信息
        if(null!=baseRepAssetAstBillInfoService){
            if(null!=entity.getBaseRepAssetAstBillInfoDto()){
                baseRepAssetAstBillInfoService.delete(entity.getBaseRepAssetAstBillInfoDto());
            }
        }
        //信贷及非标资产报送
        if(null!=baseRepAssetCreditNonstdInfoService){
            if(null!=entity.getBaseRepAssetCreditNonstdInfoDto()){
                baseRepAssetCreditNonstdInfoService.delete(entity.getBaseRepAssetCreditNonstdInfoDto());
            }
        }
        //权益类资产中债报送
        if(null!=baseRepAssetEquityInfoService){
            if(null!=entity.getBaseRepAssetEquityInfoDto()){
                baseRepAssetEquityInfoService.delete(entity.getBaseRepAssetEquityInfoDto());
            }
        }
        //其他资产报送信息
        if(null!=baseRepOtherInvestTargetInfoService){
            if(null!=entity.getBaseRepOtherInvestTargetInfoDto()){
                baseRepOtherInvestTargetInfoService.delete(entity.getBaseRepOtherInvestTargetInfoDto());
            }
        }
        //基金资产报送信息
        if(null!=baseRepFundAssetInfoService){
            if(null!=entity.getBaseRepFundAssetInfoDto()){
                baseRepFundAssetInfoService.delete(entity.getBaseRepFundAssetInfoDto());
            }
        }
        //债券资产报送
        if(null!=baseRepBondAssetInfoService){
            if(null!=entity.getBaseRepBondAssetInfoDto()){
                baseRepBondAssetInfoService.delete(entity.getBaseRepBondAssetInfoDto());
            }
        }
        //计划类报送
        if(null!=baseRepMngAssetPlanInfoService){
            if(null!=entity.getBaseRepMngAssetPlanInfoDto()){
                baseRepMngAssetPlanInfoService.delete(entity.getBaseRepMngAssetPlanInfoDto());
            }
        }
        //add by zhangnian 20191014 start
        //中债新增信息
        if(null!=baseRepOtherInfoService){
            if(null!=entity.getBaseRepOtherDto()){
                baseRepOtherInfoService.delete(entity.getBaseRepOtherDto());
            }
        }
        //add by zhangnian 20191014 end
        //资产负债
        if(null!=baseRepAssetDeptInfoService){
            if(null!=entity.getBaseRepAssetDeptInfoDto()){
                baseRepAssetDeptInfoService.deleteByPrimaryKey(entity.getBaseRepAssetDeptInfoDto());
            }
        }
        //计算规则扩展--单独针对平安
        if(null!=finInterestFactorService){
            finInterestFactorService.deletePaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
        }
        //删除委外组合 add wangpeng 2019.07.23
        if(StringUtil.isNotEmpty(entity.getFinprodMarketId())){
        	 ptlPortfolioMapper.deleteByPrimaryKey(entity.getFinprodMarketId());
        }
        //add end 2019.07.23
      //删除授信关联
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(entity.getFinprodId());
        assetCommonService.deleteCredit(creditLines);
        // 评级删除	20191126
        busiFinBondRatingInfoService.deleteMstBondCreditRatingByProductId(entity.getFinprodMarketId());
    }

    /**
     * 获取基本信息--修改/详细
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public FinProjectTargetAssetDto getBasicInfo(@RequestBody Map<String,Object> param){
        return finProjectTargetAssetMapper.getBasicInfo(param);
    }

    /**
     * 获取CMS信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public FinProjectTargetAssetCmsDto getCmsInfoBycashOutNo(@RequestBody Map<String, Object> param){
        return finProjectTargetAssetMapper.getCmsInfoBycashOutNo(param);
    }

    /**
     *  获取cms附件 businessId
     * <一句话功能简述>
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public List<PaFinProductAssetAttach> getBusinessIdByFinprodId(String finprodId){
        return finProjectTargetAssetMapper.getBusinessIdByFinprodId(finprodId);
    }

    public String checkMarketExists(String finprodMarketId) {
        Map<String, Object> map = new HashMap<>();
        map.put("finprodMarketId", finprodMarketId);
        map.put("finprodType2", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        return finProjectTargetAssetMapper.checkMarketExists(map);
    }
    public void deleteRepInfoById(List<String> finprodIdList){
        //非标资产(票据或信用)报送信息
    	if(null!=baseRepAssetAstBillInfoService){
    		   BaseRepAssetAstBillInfoDto dtoBillInfo=new BaseRepAssetAstBillInfoDto();
    	    	dtoBillInfo.setRepFinprodIdList(finprodIdList);
    	    	baseRepAssetAstBillInfoService.delete(dtoBillInfo);
    	 }
    	 //信贷及非标资产报送
        if(null!=baseRepAssetCreditNonstdInfoService){
        	BaseRepAssetCreditNonstdInfoDto dtoAssetCreditInfo=new BaseRepAssetCreditNonstdInfoDto();
        	dtoAssetCreditInfo.setRepFinprodIdList(finprodIdList);
            baseRepAssetCreditNonstdInfoService.delete(dtoAssetCreditInfo);

        }
        //权益类资产中债报送
        if(null!=baseRepAssetEquityInfoService){
        	BaseRepAssetEquityInfoDto dtoAssetEquityInfo=new BaseRepAssetEquityInfoDto();
        	dtoAssetEquityInfo.setRepFinprodIdList(finprodIdList);
            baseRepAssetEquityInfoService.delete(dtoAssetEquityInfo);

        }
        //其他资产报送信息
        if(null!=baseRepOtherInvestTargetInfoService){
        	BaseRepOtherInvestTargetInfoDto dtoOtherTargetInfo=new BaseRepOtherInvestTargetInfoDto();
        	dtoOtherTargetInfo.setRepFinprodIdList(finprodIdList);
        	baseRepOtherInvestTargetInfoService.delete(dtoOtherTargetInfo);

        }
        //基金资产报送信息
        if(null!=baseRepFundAssetInfoService){
        	BaseRepFundAssetInfoDto dtoFundAsset=new BaseRepFundAssetInfoDto();
        	dtoFundAsset.setRepFinprodIdList(finprodIdList);
            baseRepFundAssetInfoService.delete(dtoFundAsset);

        }
        //债券资产报送
        if(null!=baseRepBondAssetInfoService){
        	BaseRepBondAssetInfoDto dtoBondAsset=new BaseRepBondAssetInfoDto();
        	dtoBondAsset.setRepFinprodIdList(finprodIdList);
            baseRepBondAssetInfoService.delete(dtoBondAsset);

        }
        //计划类报送
        if(null!=baseRepMngAssetPlanInfoService){
        	BaseRepMngAssetPlanInfoDto dtoMngAssetPlanInfo=new BaseRepMngAssetPlanInfoDto();
        	dtoMngAssetPlanInfo.setRepFinprodIdList(finprodIdList);
            baseRepMngAssetPlanInfoService.delete(dtoMngAssetPlanInfo);

        }
    }
}
