/*
 * 文件名：FinBondTargetAssetService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.bond.service;

import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.service.asset.bond.dto.FinCashPlanCheck;
import com.joyintech.service.asset.bond.dto.FinExercisePlanCheck;
import com.joyintech.service.asset.common.service.FinPayInterestStdInfoService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.report.asset.BaseRepAssetEquityInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepOtherInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetEquityInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepOtherDto;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.autoperiod.bond.AutoPeriodBondCommonService;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurDbInertDto;
import com.joyintech.tams.invest.common.dto.period.trdbondduration.TrdBondDurationDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.bond.dao.FinBondTargetAssetMapper;
import com.joyintech.service.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.service.asset.common.service.AssetCommonService;
import com.joyintech.service.asset.common.service.BusiFinBondRatingInfoService;
import com.joyintech.service.asset.common.service.FinInterestFactorService;
import com.joyintech.tams.common.base.entity.CreditLines;
import com.joyintech.tams.common.base.entity.FinPostInvestInfo;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.common.base.entity.PaFinProduct;
import com.joyintech.tams.common.base.entity.RepAssetDept;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepAssetDeptInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepBondAssetInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetDeptInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepBondAssetInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 债券标的信息
 * @author zhangnian
 * @version 1.0 2018年9月20日
 * @see FinBondTargetAssetService
 * @since 1.0
 */
@Service("backFinBondTargetAssetService")
public class FinBondTargetAssetService extends BaseService<FinBondTargetAssetDto>{
    /**
     * 基础--标的类金融产品
     */
    @Autowired(required = false)
    private  BaseFinProductBaseInfoService baseFinProductBaseInfoService;
    
    /**
     * 基础 --附件
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;
    
    @Autowired(required = false)
    private FinInterestFactorService finInterestFactorService;
    
    @Autowired(required = false)
    private BusiFinBondRatingInfoService busiFinBondRatingInfoService;
    
    @Autowired
    private FinBondTargetAssetMapper finBondTargetAssetMapper;
    
    @Autowired
    private MstBondCreditRatingMapper  mstBondCreditRatingMapper;
    
    /**
     * 基础--中债报送(负债)
     */
    @Autowired(required = false)
    private BaseRepAssetDeptInfoService baseRepAssetDeptInfoService;

    /**
     * 权益类资产中债报送(基础组件)
     */
    @Autowired(required = false)
    private BaseRepAssetEquityInfoService baseRepAssetEquityInfoService;

    /**
     * 基础--中债报送(资产)
     */
    @Autowired(required = false)
    private BaseRepBondAssetInfoService baseRepBondAssetInfoService;
  //标的共通service
    @Autowired(required=false)
    private AssetCommonService assetCommonService;
    @Autowired(required=false)
    private PaFinProductMapper paFinProductMapper;

    @Autowired(required = false)
    private FinProductConvertibleMapper finProductConvertibleMapper;
    /* add by zhangpuyu 2019/10/24 start */
    /**
     * 投后信息
     */
    @Autowired
    private FinPostInvestInfoMapper finPostInvestInfoMapper;
    /* add by zhangpuyu 2019/10/24 start */

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired(required = false)
    private BaseRepOtherInfoService baseRepOtherInfoService;

    @Autowired
    private RepAssetBondMapper repAssetBondMapper;
    @Autowired
    private RepAssetDeptMapper repAssetDeptMapper;
    @Autowired
    private RepAssetOtherMapper repAssetOtherMapper;
    @Autowired
    private RepAssetEquityMapper repAssetEquityMapper;

    @Autowired
    private FinPayInterestStdInfoService finPayInterestStdInfoService;

    /**
     * 平台日期
     */
    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * AutoPeriodCommonService
     */
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;

    /**
     * AutoPeriodBondCommonService
     */
    @Autowired
    private AutoPeriodBondCommonService autoPeriodBondCommonService;

    /**
     * 分页
     * @param page
     * @param length
     * @param pageParam
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public Page<FinBondTargetAssetDto> selectForPage(int page, int length, Map<String, Object> pageParam)
        {
        pageParam.put("MAIN_GRADE_TYPE_01", DictCodeList.评级类型_主体评级.getValue());
        pageParam.put("MAIN_GRADE_TYPE_02", DictCodeList.评级类型_债项评级.getValue());
        Page<FinBondTargetAssetDto> pageDto = super.queryForPage(page, length, () -> finBondTargetAssetMapper.queryForPage(pageParam));
        List<FinBondTargetAssetDto> pageList = pageDto.getRoot();
       // List<FinBondTargetAssetDto> finaList = new ArrayList<FinBondTargetAssetDto>();
        /*for(FinBondTargetAssetDto dto : pageList ){
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("secId", dto.getFinprodMarketId());
            param.put("ZTPI", DictCodeList.评级类型_主体评级.getValue());
            param.put("ZXPJ", DictCodeList.评级类型_债项评级.getValue());
            //查询评级类型为主体评级的数据
            List<FinBondTargetAssetDto> ztList = finBondTargetAssetMapper.getZTPJ(param);
            if(ztList != null && ztList.size()>0){
                //只取第一条数据
                dto.setZtGradeOrgId(ztList.get(0).getZtGradeOrgId());
                dto.setZtGradeResult(ztList.get(0).getZtGradeResult());
            }
            //查询评级类型是债项评级的数据
            List<FinBondTargetAssetDto> zxList = finBondTargetAssetMapper.getZXPJ(param);
            if(zxList != null && zxList.size()>0){
                //只取第一条数据
                dto.setZxGradeOrgId(zxList.get(0).getZxGradeOrgId());
                dto.setZxGradeResult(zxList.get(0).getZxGradeResult());
            }
            finaList.add(dto);
        }*/
        pageDto.setRoot(pageList);
        return pageDto;
    }
    
    /**
     * 插入
     * @param entity
     * @return
     * @see
     * @since 1.0
     */
    public void insert(FinBondTargetAssetDto entity) {
        //标的金融产品信息
        baseFinProductBaseInfoService.insert(entity.getBaseFinProductBaseInfoList());
        //计算规则扩展--单独针对平安
        if(null!=finInterestFactorService){
            finInterestFactorService.insertPaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
        }
        //债券评级信息
        if(null!=busiFinBondRatingInfoService){
            if(null!=entity.getBusiFinBondRatingInfoDto()){
                if(null!=entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList()
                    &&!entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList().isEmpty()){
                    List<MstBondCreditRating> mstBondCreditRatingList = entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList();
                    for(MstBondCreditRating mstBondCreditRatingEntity:mstBondCreditRatingList){
                    	//20200327 taoran add   此处只可修改债项评级  不可修改主体评级   否则会导致评级表数据有问题
                    	if(DictCodeList.评级类型_债项评级.getValue().equals(mstBondCreditRatingEntity.getoType())) {
                    		mstBondCreditRatingMapper.insert(mstBondCreditRatingEntity);
                    	}
                    }
                }
            }
        }
        //权益类资产中债报送
        if (StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
                ||StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue())) {
            if(null!=baseRepAssetEquityInfoService){
                if(null!=entity.getBaseRepAssetEquityInfoDto()){
                    List<RepAssetEquity> list = entity.getBaseRepAssetEquityInfoDto().getRepAssetEquityList();
                    baseRepAssetEquityInfoService.insert(list);
                }
            }
            //资产负债
            if(null!=baseRepAssetDeptInfoService){
                if(null!=entity.getBaseRepAssetDeptInfoDto()){
                    baseRepAssetDeptInfoService.insert(entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList());
                }
            }
        }else{
            if(null!=baseRepAssetDeptInfoService){
                // add by lizhi 判断系统参数ASSET_INFO_CHB_HIDE是否显示中债信息 2020-2-28
                if(!StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE), DictCodeListExt1.是或否_否.getValue())){
                    //中债报送
                    if(entity.getBaseRepAssetDeptInfoDto()!=null){
                        baseRepAssetDeptInfoService.insert(entity.getBaseRepAssetDeptInfoDto().getRepAssetDeptList());
                        baseRepBondAssetInfoService.insert(entity.getBaseRepBondAssetInfoDto().getRepAssetBondList());
                    }
                }
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
      //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,entity);
        //将数据插入到标的类金融产品扩展表
        paFinProductMapper.insertSelective(paFinProduct);
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
        //转债信息
        if((StringUtil.equals(entity.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
                ||StringUtil.equals(entity.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue()))&&!StringUtil.isEmpty(entity.getConFinprodMarketId())){
            finProductConvertibleMapper.insert(entity.getFinProductConvertible());
        }
        /* add by zhangpuyu 2019/10/24 start */
        //投后信息
        if(null!=entity.getFinprodId()){
            FinPostInvestInfo finPostInvestInfo=new FinPostInvestInfo();
        	finPostInvestInfo.setFinprodId(entity.getFinprodId());
        	finPostInvestInfo.setPostOrg(entity.getPostOrg());
            finPostInvestInfoMapper.insert(finPostInvestInfo);
        }
        /* add by zhangpuyu 2019/10/24 start */
    }
    
    /**
     * 修改
     * @param entity
     * @see
     * @since 1.0
     */
    public void update(FinBondTargetAssetDto entity){
        //查询现金流计划表
        List<FinCashPlan> finCashPlanOldList = finBondTargetAssetMapper.getFinCashPlanList(entity.getFinprodId());
        //查询行权计划表
        List<FinExercisePlan> finExercisePlanOldList =finBondTargetAssetMapper.selectFinExercisePlanByFinprodId(entity.getFinprodId());

        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(entity.getFinprodId());
        baseFinProductBaseInfoService.update(finprodIdList, entity.getBaseFinProductBaseInfoList());
        //计算规则扩展--单独针对平安
        if(null!=finInterestFactorService){
            finInterestFactorService.deletePaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
            finInterestFactorService.insertPaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule());
        }
      //债券评级信息
        if(null!=busiFinBondRatingInfoService){
            
        	/**
        	busiFinBondRatingInfoService.deleteMstBondCreditRatingByProductId(entity.getFinprodMarketId());
            if(null!=entity.getBusiFinBondRatingInfoDto()){
                if(null!=entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList()
                    &&!entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList().isEmpty()){
                    List<MstBondCreditRating> mstBondCreditRatingList = entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList();
                    for(MstBondCreditRating mstBondCreditRatingEntity:mstBondCreditRatingList){
                    	//20200327 taoran add   此处只可修改债项评级  不可修改主体评级   否则会导致评级表数据有问题
                    	if(DictCodeList.评级类型_债项评级.getValue().equals(mstBondCreditRatingEntity.getoType())) {
                    		mstBondCreditRatingMapper.insert(mstBondCreditRatingEntity);
                    	}
                    }
                }
            }
            */

        	// modify by wj  20200427  修改bug 没修改评级也会进行债券评级变动提醒
        	
            if(null!=entity.getBusiFinBondRatingInfoDto() && null!=entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList()
                    &&!entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList().isEmpty()){
                
                List<MstBondCreditRating> mstBondCreditRatingList = entity.getBusiFinBondRatingInfoDto().getMstBondCreditRatingList();

            	//1. 先删除掉 跟新list 不一样的数据。。 说明有数据被删/修改
            	busiFinBondRatingInfoService.delDifferentDateByUnique(mstBondCreditRatingList,entity.getFinprodMarketId());
            	for(MstBondCreditRating mstBondCreditRatingEntity:mstBondCreditRatingList){
                	
            		int cnt = busiFinBondRatingInfoService.getMstBondCreditRatingByUnique(mstBondCreditRatingEntity);
            		// 如果能查到说明已存在  查不到则新增。
            		if(cnt<=0){
            			if(DictCodeList.评级类型_债项评级.getValue().equals(mstBondCreditRatingEntity.getoType())) {
                    		mstBondCreditRatingMapper.insert(mstBondCreditRatingEntity);
                    	}
                        //主体评级根据债券发行人在市场机构里维护，债券标的评级信息里不维护主体评级  modify bu caojin 20201004
//            			else if(DictCodeList.评级类型_主体评级.getValue().equals(mstBondCreditRatingEntity.getoType())) {
//                    	    mstBondCreditRatingEntity.setoCode(entity.getIssuer());
//                            mstBondCreditRatingMapper.insert(mstBondCreditRatingEntity);
//                    	}
            		}
                	
                }
            	
            }else{
            	//更新的数据中评级信息为空
                busiFinBondRatingInfoService.deleteMstBondCreditRatingByProductId(entity.getFinprodMarketId());

            }
            
        }
        //权益类资产中债报送
        if (StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
                ||StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue())) {
            if(null!=baseRepAssetEquityInfoService){
                if(null!=entity.getBaseRepAssetEquityInfoDto()){
                    List<RepAssetEquity> list = entity.getBaseRepAssetEquityInfoDto().getRepAssetEquityList();
                    entity.getBaseRepAssetEquityInfoDto().setRepAssetEquityList(list);
                    baseRepAssetEquityInfoService.update(entity.getBaseRepAssetEquityInfoDto());
                    //修改债券资产二级分类，比如从债券修改为权益类，REP_ASSET_BOND表数据删除不掉bug修改
                    BaseRepBondAssetInfoDto baseRepBondAssetInfoDto =new BaseRepBondAssetInfoDto();
                    List<String> repFinprodIdList = new ArrayList<String>();
                    repFinprodIdList.add(list.get(0).getRepFinprodId());
                    baseRepBondAssetInfoDto.setRepFinprodIdList(repFinprodIdList);
                    List<RepAssetBond> repAssetBondTemp = baseRepBondAssetInfoService.select(baseRepBondAssetInfoDto);
                    if(!StringUtil.isEmptyList(repAssetBondTemp)){
                        baseRepBondAssetInfoService.delete(baseRepBondAssetInfoDto);
                    }
                }
            }
            //资产负债
            if(null!=baseRepAssetDeptInfoService){
                if(null!=entity.getBaseRepAssetDeptInfoDto()){
                    baseRepAssetDeptInfoService.updateBaseRepAssetDeptInfoDto(entity.getBaseRepAssetDeptInfoDto());
                }
            }
        }else{
            if(null!=baseRepAssetDeptInfoService&&entity.getBaseRepAssetDeptInfoDto()!=null){
                //中债报送
                baseRepAssetDeptInfoService.updateBaseRepAssetDeptInfoDto(entity.getBaseRepAssetDeptInfoDto());
                baseRepBondAssetInfoService.update(entity.getBaseRepBondAssetInfoDto());
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
      //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,entity);
        //修改标的类金融产品扩展表
        paFinProductMapper.updateByPrimaryKey(paFinProduct);
        /**
         * 修改授信关联
         */
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(entity.getFinprodId());
        creditLines.setSeqno(entity.getSeqno());
        creditLines.setCreditMstId(entity.getCreditMstId());

       /*
        *del by xr 和红红确认了，债券标的修改不做授信修改，测试时，请注意测试此部分类容
       *assetCommonService.updateCredit(creditLines);
       */
        //转债信息
        if(StringUtil.equals(entity.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
                ||StringUtil.equals(entity.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue())){
            finProductConvertibleMapper.deleteByPrimaryKey(entity.getFinprodId());
            if(StringUtil.isNotEmpty(entity.getConFinprodMarketId())){
                finProductConvertibleMapper.insert(entity.getFinProductConvertible());
            }
        }
        /* add by zhangpuyu 2019/10/24 start */
        //投后信息
        if(null!=entity.getFinprodId()){
        	finPostInvestInfoMapper.deleteByPrimaryKey(entity.getFinprodId());
        	FinPostInvestInfo finPostInvestInfo=new FinPostInvestInfo();
        	finPostInvestInfo.setFinprodId(entity.getFinprodId());
        	finPostInvestInfo.setPostOrg(entity.getPostOrg());
        	finPostInvestInfoMapper.insert(finPostInvestInfo);
        }
        /* add by zhangpuyu 2019/10/24 start */

        if (DictCodeList.是否_是.getValue().equals(CacheUtil.getSysParam(BussinessConstant.IS_TODO_SETTLE_FLAG))
             && (StringUtil.equals(entity.getTradeMarket(),DictCodeListExt1.交易场所_银行间.getValue())
                    ||StringUtil.equals(entity.getTradeMarket(),DictCodeListExt1.交易场所_商业银行柜台.getValue()))) {
            this.genTodoSettleDataDto(entity,finCashPlanOldList,finExercisePlanOldList);
        }
    }

    /**
     * 生成待交收数据
     */
    @Transactional(rollbackFor=Exception.class, propagation = Propagation.REQUIRES_NEW)
    private void genTodoSettleDataDto(FinBondTargetAssetDto entity,List<FinCashPlan> finCashPlanOldList,
                                      List<FinExercisePlan> finExercisePlanOldList) {
        //旧现金流计划表
        Map<String, List<FinCashPlan>> oldFinCashPlanList = this.dealFinCashPlan(finCashPlanOldList);
        //旧行权计划表
        List<FinExercisePlan> oldfinExercisePlanList = this.dealFinExercisePlan(finExercisePlanOldList);
        //旧付息计划list
        List<FinCashPlan> finCashPlanfxOldList = null;
        //旧还本计划list
        List<FinCashPlan> finCashPlanhbOldList = null;

        for (Map.Entry<String, List<FinCashPlan>> entry : oldFinCashPlanList.entrySet()) {
            if(!StringUtil.isNotEmpty(entry.getValue().get(0).getPrinAmt())){
                finCashPlanfxOldList =entry.getValue();
            }else{
                finCashPlanhbOldList =entry.getValue();
            }
        }

        //返回比较的旧付息list
        List<FinCashPlanCheck> finCashPlanfxCheckOldList = this.dealCheckFinCashPlan(finCashPlanfxOldList);
        //返回比较的旧还本list
        List<FinCashPlanCheck> finCashPlanhbCheckOldList = this.dealCheckFinCashPlan(finCashPlanhbOldList);
        //返回比较的旧行权list
        List<FinExercisePlanCheck> finExercisePlanCheckOldList = this.dealCheckFinExercisePlan(oldfinExercisePlanList);

        //现金流计划表
        Map<String, List<FinCashPlan>> finCashPlanNowList =
                entity.getBaseFinProductBaseInfoList().get(0).getFinCashPlanList().stream().collect(
                        JoyinCollectors.groupingBy(x ->x.getCashId()));
        //行权计划表
        List<FinExercisePlan> finExercisePlanList = this.dealFinExercisePlan(entity.getBaseFinProductBaseInfoList().get(0).getFinExercisePlanList());

        //付息计划list
        List<FinCashPlan> finCashPlanfxList = null;
        //还本计划list
        List<FinCashPlan> finCashPlanhbList = null;

        for (Map.Entry<String, List<FinCashPlan>> entry : finCashPlanNowList.entrySet()) {
            if(!StringUtil.isNotEmpty(entry.getValue().get(0).getPrinAmt())){
                finCashPlanfxList =entry.getValue();
            }else{
                finCashPlanhbList =entry.getValue();
            }
        }
        //返回比较的付息list
        List<FinCashPlanCheck> finCashPlanfxCheckList = this.dealCheckFinCashPlan(finCashPlanfxList);
        //返回比较的还本list
        List<FinCashPlanCheck> finCashPlanhbCheckList = this.dealCheckFinCashPlan(finCashPlanhbList);
        //返回比较的行权list
        List<FinExercisePlanCheck> finExercisePlanCheckList = this.dealCheckFinExercisePlan(finExercisePlanList);

        //若两个list内容完全一致,返回不做后续操作
        if(Arrays.equals(finCashPlanfxCheckList.toArray(),finCashPlanfxCheckOldList.toArray())
                && Arrays.equals(finCashPlanhbCheckList.toArray(),finCashPlanhbCheckOldList.toArray())
                && Arrays.equals(finExercisePlanCheckOldList.toArray(),finExercisePlanCheckList.toArray())){
            return;
        }
        //若两个list有不同的值，则做以下check，确认是否需要删除/生成当日的数据
        Date iPlatformDateSysDate = iPlatformDate.getSysDate();
//        Date uuu = DateUtil.parse("2021-12-16");
        //是否需要重新生成待交收数据
        Boolean isNeedSettleFlag = false;
        //是否需要删除已生成待交收数据
        Boolean isNeedDeleteSettleFlag = false;
        //新计划非空并且新计划中有今日的数据
        //还本付息list
        List<FinCashPlan> finCashPlanListTemp = entity.getBaseFinProductBaseInfoList().get(0).getFinCashPlanList();
        for(FinCashPlan finCashPlan : finCashPlanListTemp){
            if(finCashPlan.getMdate().compareTo(iPlatformDateSysDate)==0){
                isNeedSettleFlag = true;
            }
            break;
        }
        //行权计划list
        for(FinExercisePlan finExercisePlan : finExercisePlanList){
            if (finExercisePlan.getExerciseDate().compareTo(iPlatformDateSysDate)==0){
                isNeedSettleFlag = true;
            }
            break;
        }

        //新计划非空并且新计划中有今日的数据
        //还本付息list
        for(FinCashPlan finCashPlan : finCashPlanfxOldList){
            if(finCashPlan.getMdate().compareTo(iPlatformDateSysDate)==0){
                isNeedDeleteSettleFlag = true;
            }
            break;
        }
        //还本付息list
        for(FinCashPlan finCashPlan : finCashPlanhbOldList){
            if(finCashPlan.getMdate().compareTo(iPlatformDateSysDate)==0){
                isNeedDeleteSettleFlag = true;
            }
            break;
        }
        //行权计划list
        for(FinExercisePlan finExercisePlan : finExercisePlanList){
            if (finExercisePlan.getExerciseDate().compareTo(iPlatformDateSysDate)==0){
                isNeedDeleteSettleFlag = true;
            }
            break;
        }

        //删除今日是否存在未交收数据
        if(isNeedDeleteSettleFlag){
            this.deleteActData(DictCodeList.共通业务类型_债券类资产存续期.getValue(),
                    DictCodeListExt1.金融产品类型_投管用_债券.getValue(),iPlatformDateSysDate,entity.getFinprodId());
        }
        if(isNeedSettleFlag){
            //重新生成待交收数据
            //重新查询单条记录数据
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("finprodId", entity.getFinprodId());
            paramMap.put("finprodMarketId",entity.getFinprodMarketId());
            paramMap.put("FINPROD_TYPE_TGF01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            paramMap.put("CASH_CLASS_TYPE01", DictCodeList.现金流类型_中间收息.getValue());
            paramMap.put("CASH_CLASS_TYPE02", DictCodeList.现金流类型_中间还本.getValue());
            paramMap.put("CASH_CLASS_TYPE03", DictCodeList.现金流类型_到期还本.getValue());
            paramMap.put("CASH_CLASS_TYPE04", DictCodeList.现金流类型_到期收息.getValue());
            // 数据字典缓存.交易状态.初始
            paramMap.put("TRADE_STATUS01", DictCodeListExt1.交易状态_初始.getValue());
            paramMap.put("TRADE_STATUS03", DictCodeListExt1.交易状态_撤销.getValue());
            paramMap.put("YESNO_N", DictCodeList.是否_否.getValue());
            paramMap.put("YESNO_Y", DictCodeList.是否_是.getValue());
            paramMap.put("TRD10", DictCodeList.共通业务类型_债券类资产存续期.getValue());
            paramMap.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
            paramMap.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
            //调整后的计算结束日
            paramMap.put("mDate", DateUtil.format(iPlatformDateSysDate));

            List<TrdBondDurationDto> trdBondDurationDtoInsertList = new ArrayList<TrdBondDurationDto>();

            List<TrdBondDurationDto> pendingTradeDataList = finBondTargetAssetMapper.findEffectiveData(paramMap);

            for(TrdBondDurationDto pendingTradeData : pendingTradeDataList){
                paramMap.put("yield", pendingTradeData.getYield());
                paramMap.put("vDate", DateUtil.parse(pendingTradeData.getVDate()));
                paramMap.put("adjustMDate",DateUtil.parse(pendingTradeData.getMDate()));
                paramMap.put("secAcctId", pendingTradeData.getSecManageAcctId());
                List<FinAssetIntInfo> finAssetIntInfos = finBondTargetAssetMapper.findAssetIntInfo(paramMap);
                // 实收本息总额: 设置为 全价总额 理论本息总额
                pendingTradeData.setTradeAmt(pendingTradeData.getFpriceAmt());
                BussinessCodeParameter parameter = new BussinessCodeParameter();
                parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
                pendingTradeData.setBusiCode(autoPeriodCommonService.buildBussinessCode(parameter));

                pendingTradeData.setFinAssetIntInfoList(finAssetIntInfos);

                // 构造将要登录的业务流程表数据
                genDataForInsertDto(pendingTradeData);

                trdBondDurationDtoInsertList.add(pendingTradeData);
            }
            //插入数据
            for(TrdBondDurationDto trdBondDurationDto: trdBondDurationDtoInsertList){
                autoPeriodCommonService.insertCfetsDealData(trdBondDurationDto.getTrdBondDurDbInertDto());
            }
        }
    }

    /**
     * 返回check所需要的现金流list
     * @param finCashPlanList
     * @return
     */
    private List<FinCashPlanCheck> dealCheckFinCashPlan(List<FinCashPlan> finCashPlanList) {
        List<FinCashPlanCheck> finCashPlanListTemp = new ArrayList<FinCashPlanCheck>();
        for(FinCashPlan finCashPlan : finCashPlanList){
            FinCashPlanCheck finCashPlanTemp = new FinCashPlanCheck();
            finCashPlanTemp.setMdate(finCashPlan.getMdate());
            finCashPlanTemp.setPayDate(finCashPlan.getPayDate());

            finCashPlanListTemp.add(finCashPlanTemp);
        }
        return finCashPlanListTemp;
    }

    /**
     * 返回check所需要的行权list
     * @param finExercisePlanList
     * @return
     */
    private List<FinExercisePlanCheck> dealCheckFinExercisePlan(List<FinExercisePlan> finExercisePlanList) {
        List<FinExercisePlanCheck> finExercisePlanListTemp = new ArrayList<FinExercisePlanCheck>();
        for(FinExercisePlan finExercisePlan : finExercisePlanList){
            FinExercisePlanCheck finExercisePlanTemp = new FinExercisePlanCheck();
            finExercisePlanTemp.setExerciseDate(finExercisePlan.getExerciseDate());
            finExercisePlanTemp.setActExerciseDate(finExercisePlan.getActExerciseDate());

            finExercisePlanListTemp.add(finExercisePlanTemp);
        }
        return finExercisePlanListTemp;
    }

    /**
     * 现金流计划排序
     * @param finCashPlanList
     * @see
     * @since 1.0
     */
    private Map<String, List<FinCashPlan>> dealFinCashPlan(List<FinCashPlan> finCashPlanList) {
        Map<String, List<FinCashPlan>> returnFinCashPlanList =finCashPlanList.stream().collect(
                JoyinCollectors.groupingBy(x ->x.getCashId()));
        for (Map.Entry<String, List<FinCashPlan>> entry : returnFinCashPlanList.entrySet()) {
            Collections.sort(entry.getValue(), new Comparator<FinCashPlan>() {
                @Override
                public int compare(FinCashPlan o1, FinCashPlan o2) {
                    return o1.getMdate().compareTo(o2.getMdate());
                }
            });
        }
        return returnFinCashPlanList;
    }

    /**
     * 行权计划排序
     * @param finExercisePlanOldList
     * @see
     * @since 1.0
     */
    private List<FinExercisePlan> dealFinExercisePlan(List<FinExercisePlan> finExercisePlanOldList) {
        List<FinExercisePlan> returnFinCashPlanList = finExercisePlanOldList.stream().filter(
                x -> StringUtil.equals(DictCodeList.行权方式_赎回.getValue(), x.getExerciseType())).collect(Collectors.toList());

        // 行权计划排序
        Collections.sort(returnFinCashPlanList, new Comparator<FinExercisePlan>() {
            @Override
            public int compare(FinExercisePlan o1, FinExercisePlan o2) {
                return o1.getExerciseDate().compareTo(o2.getExerciseDate());
            }
        });

        return returnFinCashPlanList;
    }

    /**
     * 删除实际表数据
     *
     * @param busiType 业务类型
     * @param finProdType2 金融产品类型（投管）
     * @see
     * @since 1.0
     */
    public void deleteActData(String busiType, String finProdType2,Date iPlatformDate,String finprodId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("busiType", busiType);
        paramMap.put("finProdType2", finProdType2);
        paramMap.put("batchDate", iPlatformDate);
        paramMap.put("finprodId", finprodId);
        paramMap.put("PS_T07", DictCodeList.交易方向_到期.getValue());
        paramMap.put("PS_T08", DictCodeList.交易方向_还本.getValue());
        paramMap.put("PS_T09", DictCodeList.交易方向_收息.getValue());
        paramMap.put("PS_T39", DictCodeList.交易方向_行权_赎回.getValue());

        finBondTargetAssetMapper.deletePaTrdFinDealTotalData(paramMap);
        finBondTargetAssetMapper.deleteStlPayOrdersData(paramMap);
        finBondTargetAssetMapper.deleteTrdProductDeal(paramMap);
        finBondTargetAssetMapper.deleteTrdProductDealAdd(paramMap);
        finBondTargetAssetMapper.deleteTrdFinProductDealTotalData(paramMap);
    }


    /**
     * 构造将要登录的业务流程表数据
     *
     * @param entity 参数dto
     * @see
     * @since 1.0
     */
    private void genDataForInsertDto(TrdBondDurationDto entity) {
        autoPeriodBondCommonService.genBondDataForInsertDtoNew(entity);

        TrdBondDurDbInertDto trdBondDurDbInertDto = entity.getTrdBondDurDbInertDto();

        TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
        PaTrdFinProductDealTotal paTrdFinProductDealTotal =new PaTrdFinProductDealTotal();
        List<TrdProductDeal> trdProductDealList = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> trdProductDealAddList =new ArrayList<TrdProductDealAdd>();
        List<StlPayOrders> stlPayOrdersList =new ArrayList<StlPayOrders>();

        BeanUtil.copyPropertiesIgnoreNull(trdFinProductDealTotal, trdBondDurDbInertDto.getWflTrdFinProductDealTotal());
        BeanUtil.copyPropertiesIgnoreNull(paTrdFinProductDealTotal, trdBondDurDbInertDto.getWflPaTrdFinDealTotal());
        for(WflTrdProductDeal wflTrdProductDeal : trdBondDurDbInertDto.getWflTrdProductDealList()){
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,wflTrdProductDeal);
            trdProductDealList.add(trdProductDeal);
        }
        for(WflTrdProductDealAdd wflTrdProductDealAdd : trdBondDurDbInertDto.getWflTrdProductDealAdds()){
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDealAdd,wflTrdProductDealAdd);
            trdProductDealAddList.add(trdProductDealAdd);
        }
        for(WflStlPayOrders wflStlPayOrders : trdBondDurDbInertDto.getWflStlPayOrdersList()){
            StlPayOrders stlPayOrders = new StlPayOrders();
            BeanUtil.copyPropertiesIgnoreNull(stlPayOrders,wflStlPayOrders);
            stlPayOrdersList.add(stlPayOrders);
        }

        trdBondDurDbInertDto.setTrdFinProductDealTotal(trdFinProductDealTotal);
        trdBondDurDbInertDto.setPaTrdFinProductDealTotal(paTrdFinProductDealTotal);
        trdBondDurDbInertDto.setTrdProductDealList(trdProductDealList);
        trdBondDurDbInertDto.setTrdProductDealAdds(trdProductDealAddList);
        trdBondDurDbInertDto.setStlPayOrdersList(stlPayOrdersList);

        entity.setTrdBondDurDbInertDto(trdBondDurDbInertDto);
    }

    /**
     * 删除
     * @param entity
     * @see
     * @since 1.0
     */
    public void delete(FinBondTargetAssetDto entity) {
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(entity.getFinprodId());
        //标的金融产品信息
        baseFinProductBaseInfoService.delete(finprodIdList);
        // added by wangzhiang BUG修复，债券删除时漏删一张表 2019-08-24 start
        paFinProductMapper.deleteByPrimaryKey(entity.getFinprodId());
        // added by wangzhiang BUG修复，债券删除时漏删一张表 2019-08-24 end
        //计算规则扩展--单独针对平安
        if(null!=finInterestFactorService){
            finInterestFactorService.deletePaFinCashCalcRule(entity.getFinInterestFactorDto().getPaFinCashCalcRule()); 
        }
        //债券评级信息
        if(null!=busiFinBondRatingInfoService){
            //删除的时候评级信息表数据sec_id 是市场代码，不是金融产品代码
            busiFinBondRatingInfoService.deleteMstBondCreditRatingByProductId(entity.getFinprodMarketId());
        }
        //附件信息
        mstFileCommonService.updateBusinessId(entity.getMstFileInputParameter());
        //权益类资产中债报送
        if (StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
                ||StringUtil.equals(entity.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue())) {
            if(null!=baseRepAssetEquityInfoService){
                if(null!=entity.getBaseRepAssetEquityInfoDto()){
                    baseRepAssetEquityInfoService.delete(entity.getBaseRepAssetEquityInfoDto());
                }
            }
            if(null!=baseRepAssetDeptInfoService){
                if(null!=entity.getBaseRepAssetDeptInfoDto()){
                    baseRepAssetDeptInfoService.deleteByPrimaryKey(entity.getBaseRepAssetDeptInfoDto());
                }
            }
        }else{
                //中债报送信息
                baseRepAssetDeptInfoService.deleteByPrimaryKey(entity.getBaseRepAssetDeptInfoDto());
                baseRepBondAssetInfoService.delete(entity.getBaseRepBondAssetInfoDto());
        }
        //add by zhangnian 20191014 start
        //中债新增信息
        if(null!=baseRepOtherInfoService){
            if(null!=entity.getBaseRepOtherDto()){
                baseRepOtherInfoService.delete(entity.getBaseRepOtherDto());
            }
        }
        //add by zhangnian 20191014 end
      //删除授信关联
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(entity.getFinprodId());
        assetCommonService.deleteCredit(creditLines);
        //转债信息
        if(StringUtil.equals(entity.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
                ||StringUtil.equals(entity.getType4(),DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue())){
            finProductConvertibleMapper.deleteByPrimaryKey(entity.getFinprodId());
        }
        /* add by zhangpuyu 2019/10/24 start */
        //投后信息
        if(null!=entity.getFinprodId()){
            FinPostInvestInfo finPostInvestInfo=new FinPostInvestInfo();
            finPostInvestInfoMapper.deleteByPrimaryKey(entity.getFinprodId());
        }
        /* add by zhangpuyu 2019/10/24 start */
    }
    
    /**
     * 获取基本信息--修改/详细
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinBondTargetAssetDto getBasicInfo(String finprodId, String finprodType) {
        FinBondTargetAssetDto finBondTargetAssetDto = new FinBondTargetAssetDto();
        if(Objects.isNull(finprodType)){
            finBondTargetAssetDto = finBondTargetAssetMapper.getBasicInfo(finprodId, DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        } else {
            finBondTargetAssetDto = finBondTargetAssetMapper.getBasicInfo(finprodId, finprodType);
        }
        //转债信息
        if(null!=finBondTargetAssetDto){
            FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(finBondTargetAssetDto.getFinprodId());
            if (null != finProductConvertible) {
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(finProductConvertible.getConFinprodId());
                if(null!=finProduct){
                    finBondTargetAssetDto.setConFinprodMarketId(finProduct.getFinprodMarketId());
                }
                finBondTargetAssetDto.setFinProductConvertible(finProductConvertible);
            }
        } else {
            return null;
        }
        return finBondTargetAssetDto;
    }
        /*return finBondTargetAssetMapper.getBasicInfo(finprodId,
            // 债券新建新增快捷录入功能 add by renping 20191010 start
            DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // 债券新建新增快捷录入功能 add by renping 20191010 end
        }

    // 债券新建新增快捷录入功能 add by renping 20191010 start
    /**
     * 查询各个组件业务表数据
     *
     * @param entity
     * @see
     * @since 1.0
     */
    public void searchData(FinBondTargetAssetDto entity){
        //计算规则扩展--单独针对平安
        if (null != finInterestFactorService) {
            entity.setFinInterestFactorDto(
                finInterestFactorService.getFinInterestFactorByProductId(entity.getFinprodId(),
                    DateUtil.format(entity.getVdate()), DictCodeListExt1.现金流二级类型_利息.getValue()));
        }
        //债券评级信息
        /*if(null!=busiFinBondRatingInfoService){
            entity.setBusiFinBondRatingInfoDto(new BusiFinBondRatingInfoDto());;
            List<MstBondCreditRating> mstBondCreditRatingList = busiFinBondRatingInfoService.getmstBondCreditRatingByProductId(
                entity.getFinprodId());
            entity.getBusiFinBondRatingInfoDto().setMstBondCreditRatingList(
                mstBondCreditRatingList);
        }*/
        //中债报送
        //根据金融产品代码查询资产负债报送数据
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(entity.getFinprodId());
        BaseRepAssetDeptInfoDto dto2 = new BaseRepAssetDeptInfoDto();
        dto2.setFinprodIdList(finprodIdList);
        entity.setBaseRepAssetDeptInfoDto(new BaseRepAssetDeptInfoDto());
        List<RepAssetDept> repAssetDeptList = baseRepAssetDeptInfoService.selectRepAssetDeptInfo(
            dto2);
        entity.getBaseRepAssetDeptInfoDto().setRepAssetDeptList(repAssetDeptList);

        // 债券资产报送信息
        entity.setBaseRepBondAssetInfoDto(new BaseRepBondAssetInfoDto());
        entity.getBaseRepBondAssetInfoDto().setRepFinprodIdList(finprodIdList);
        entity.getBaseRepBondAssetInfoDto().setRepAssetBondList(
            baseRepBondAssetInfoService.select(entity.getBaseRepBondAssetInfoDto()));
        //权益类报送信息
        entity.setBaseRepAssetEquityInfoDto(new BaseRepAssetEquityInfoDto());
        entity.getBaseRepAssetEquityInfoDto().setRepFinprodIdList(finprodIdList);
        entity.getBaseRepAssetEquityInfoDto().setRepAssetEquityList(baseRepAssetEquityInfoService.select(entity.getBaseRepAssetEquityInfoDto()));
        //报送其他信息
        entity.setBaseRepOtherDto(new BaseRepOtherDto());
        entity.getBaseRepOtherDto().setRepFinprodIdList(finprodIdList);
        entity.getBaseRepOtherDto().setRepAssetOtherList(baseRepOtherInfoService.select(entity.getBaseRepOtherDto()));
        //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = paFinProductMapper.selectByPrimaryKey(entity.getFinprodId());
        BaseFinProductBaseInfoDto baseDto = new BaseFinProductBaseInfoDto();
        baseDto.setPaFinProduct(paFinProduct);
        List<BaseFinProductBaseInfoDto> baseList = new ArrayList<>();
        baseList.add(baseDto);
        entity.setBaseFinProductBaseInfoList(baseList);
    }
    // 债券新建新增快捷录入功能 add by renping 20191010 end

    /**
     * 市场代码是否重复
     * <功能详细描述>
     * @param finprodMarketId
     * @return
     * @see
     * @since 1.0
     */
    public int selectCountByfinprodMarketId(String finprodMarketId){
        return finBondTargetAssetMapper.selectCountByfinprodMarketId(finprodMarketId);
    }
    
    /**
     * 获取新建界面的以及市场投标债券代码联想下拉框
     * 
     * @author wangguiqi
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getfinprodIdForAdd(int page, int length,Map<String, Object> param) {
        param.put("assestType", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        return this.queryForPageForSelectReturn(page, length,()->finBondTargetAssetMapper.getfinprodIdForAdd(param));
    }

	public int isHqTrade(String finprodId) {
		return finBondTargetAssetMapper.isHqTrade(finprodId);
	}

    /**
     * 金融产品代码是否重复
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public int selectCountByfinprodId(String finprodId) {
        return finBondTargetAssetMapper.selectCountByfinprodId(finprodId);
    }

    /**
     * 市场投标债券 是否重复
     * <功能详细描述>
     * @param oFinprodId
     * @return
     * @see
     * @since 1.0
     */
    public int selectCountByOFinprodId(String oFinprodId,String finprodId) {
        return finBondTargetAssetMapper.selectCountByOFinprodId(oFinprodId,finprodId);
    }

    /**
     * 根据资产代码查询是否是上市标的
     *
     * @param finprodId
     * @return 是否是上市标的，大于0则为是
     * @see
     * @since 1.0
     */
    public int selectIsIssueAsset(String finprodId) {
        return finBondTargetAssetMapper.selectIsIssueAsset(finprodId);
    }
    /**
     * 查询资产相关计划
     * <功能详细描述>
     * @param finprodId
     * @return 资产相关计划
     * @see
     * @since 1.0
     */
    @GetMapping("getFinPlanList")
    public FinBondTargetAssetDto getFinPlanList(String finprodId) {
        FinBondTargetAssetDto finBondTargetAssetDto =new FinBondTargetAssetDto();

        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto =new BaseFinProductBaseInfoDto();

        List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList =new ArrayList<BaseFinProductBaseInfoDto>();
        baseFinProductBaseInfoList.add(baseFinProductBaseInfoDto);
        // 查询标的现金流信息list
        List<FinCashInfo> finCashInfolist = finBondTargetAssetMapper.selectFinCashInfo(finprodId);
        // 查询标的现金流计划list
        List<FinCashPlan> finCashPlanList = finBondTargetAssetMapper.getFinCashPlanList(finprodId);
        // 查询标的现金流支付规则
        List<FinCashPayRule> finCashPayRuleList = finBondTargetAssetMapper.getFinCashPayRuleList(finprodId);
        //查询现金流计算规则
        List<FinCashCalcRule> finCashCalcRuleList = finBondTargetAssetMapper.getTargetCashCalcRuleList(finprodId);
        //查询利率信息
        List<FinRateInfo> finRateInfoList = finBondTargetAssetMapper.getFinRateInfoList(finprodId);

        //债券资产报送信息
        RepAssetBond repAssetBond = repAssetBondMapper.selectByPrimaryKey(finprodId);
        //资产负债报送信息
        RepAssetDept repAssetDept = repAssetDeptMapper.selectByPrimaryKey(finprodId);
        //中债资产其他要素
        RepAssetOther repAssetOther = repAssetOtherMapper.selectByPrimaryKey(finprodId);
        //权益类资产报送信息
        RepAssetEquity repAssetEquity = repAssetEquityMapper.selectByPrimaryKey(finprodId);

        baseFinProductBaseInfoDto.setFinCashPlanList(finCashPlanList);
        baseFinProductBaseInfoDto.setFinCashPayRuleList(finCashPayRuleList);
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(finCashCalcRuleList);
        baseFinProductBaseInfoDto.setFinCashInfoList(finCashInfolist);
        baseFinProductBaseInfoDto.setFinRateInfoList(finRateInfoList);

        BaseRepBondAssetInfoDto baseRepBondAssetInfoDto =new BaseRepBondAssetInfoDto();
        if(repAssetBond!=null){
            baseRepBondAssetInfoDto.setRepAssetBondList(Arrays.asList(repAssetBond));
        }
        finBondTargetAssetDto.setBaseRepBondAssetInfoDto(baseRepBondAssetInfoDto);
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto =new BaseRepAssetDeptInfoDto();
        if(repAssetDept!=null){
            baseRepAssetDeptInfoDto.setRepAssetDeptList(Arrays.asList(repAssetDept));
        }
        finBondTargetAssetDto.setBaseRepAssetDeptInfoDto(baseRepAssetDeptInfoDto);
        BaseRepAssetEquityInfoDto baseRepAssetEquityInfoDto =new BaseRepAssetEquityInfoDto();
        if(repAssetEquity!=null){
            baseRepAssetEquityInfoDto.setRepAssetEquityList(Arrays.asList(repAssetEquity));
        }
        finBondTargetAssetDto.setBaseRepAssetEquityInfoDto(baseRepAssetEquityInfoDto);
        BaseRepOtherDto baseRepOtherDto =new BaseRepOtherDto();
        if(repAssetEquity!=null){
            baseRepOtherDto.setRepAssetOtherList(Arrays.asList(repAssetOther));
        }
        finBondTargetAssetDto.setBaseRepOtherDto(baseRepOtherDto);

        finBondTargetAssetDto.setBaseFinProductBaseInfoList(baseFinProductBaseInfoList);
        return finBondTargetAssetDto;
    }


    /**
     * add by xxg 20210721
     *根据产品代码得到起息日和到期日
     * @param oFinprodId 产品代码
     * @return
     */
    public FinProduct getMdateAndVdate(String oFinprodId){
        return  finProductMapper.selectByPrimaryKey(oFinprodId);
    }
}
