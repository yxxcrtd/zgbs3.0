/*
 * 文件名：FinFundTargetService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.fund.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.EncryptUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.common.dao.AssetCommonMapper;
import com.joyintech.service.asset.common.service.AssetCommonService;
import com.joyintech.service.asset.fund.dao.FinFundTargetMapper;
import com.joyintech.service.asset.fund.dto.BussFinIntPayDto;
import com.joyintech.service.asset.fund.dto.BussRepFundInfoDto;
import com.joyintech.service.asset.fund.dto.FinFundTargetDto;
import com.joyintech.tams.common.base.dao.*;


import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.base.report.asset.BaseRepAssetDeptInfoService;
import com.joyintech.tams.component.base.report.asset.BaseRepFundAssetInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepAssetDeptInfoDto;
import com.joyintech.tams.component.base.report.dto.BaseRepFundAssetInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基金类标的信息的微服务service
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年9月10日
 * @see FinFundTargetService
 * @since 1.0
 */
@Service("backFinFundTargetService")
public class FinFundTargetService extends BaseService<FinFundTargetDto> {
    @Autowired
    private FinFundTargetMapper finFundTargetMapper;
    //标的类金融产品基本信息组件
    @Autowired(required=false)
    private BaseFinProductBaseInfoService baseFinProductBaseInfoService;
    //基金资产信息报送
    @Autowired(required=false)
    private BaseRepFundAssetInfoService baseRepFundAssetInfoService;
    //资产负债信息报送组件
    @Autowired(required=false)
    private BaseRepAssetDeptInfoService baseRepAssetDeptInfoService;
    /**
     * 附件信息共通Service
     */
    @Autowired(required=false)
    private MstFileCommonService mstFileCommonService;
  //标的共通service
    @Autowired(required=false)
    private AssetCommonService assetCommonService;
    @Autowired(required=false)
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

    @Autowired
    private RepAssetOtherMapper repAssetOtherMapper;
    
    /* add by zhangpuyu 2019/10/24 start */
    /**
     * 投后信息
     */
    @Autowired
    private FinPostInvestInfoMapper finPostInvestInfoMapper;
    /* add by zhangpuyu 2019/10/24 start */
    
    @Autowired
    private AssetCommonMapper assetCommonMapper;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<FinFundTargetDto> selectForPage(int page, int length, Map<String, Object> param) 
    {
        param.put("CASH_TYPE_2", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("FIN_CASH_BUSINESS_TYPE", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        String startDate = (String)param.get("startDate");
        if("null".equals(startDate) || "".equals(startDate)){
            param.put("sDate", null);
        }else{
            
            param.put("sDate", DateUtil.parse(startDate));
        }
        String endDate = (String)param.get("endDate");
        if("null".equals(endDate) || "".equals(endDate)){
            param.put("eDate", null);
        }else{
            
            param.put("eDate", DateUtil.parse(endDate));
        }
        return super.queryForPage(page, length, () -> finFundTargetMapper.selectForPage(param));
    }

    
    /**
     * 查询基金管理人
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getIssuer() {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        return finFundTargetMapper.getIssuer(param);
    }

    /**
     * 查询资产代码
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getFinprodMarketIdList(String finprodMarketId) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        param.put("finProdMarketId", finprodMarketId);
        return finFundTargetMapper.getFinProdMarketIdList(param);
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public int insert(FinFundTargetDto dto) throws IllegalAccessException, InvocationTargetException {
        Map<String,Object> param = getDto(dto);
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
        //新建 标的类金融产品基本信息dto list
        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(entity);
        baseFinProductBaseInfoService.insert(list);
        //新建基金资产信息报送dto
        BaseRepFundAssetInfoDto repFundAssetInfoDto = new BaseRepFundAssetInfoDto();
        List<RepFund> repFundList= new ArrayList<RepFund>();
        repFundList.add((RepFund)param.get("repFund"));
        repFundAssetInfoDto.setRepFundList(repFundList);;
        baseRepFundAssetInfoService.insert(repFundAssetInfoDto);
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
      //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        //将数据插入到标的类金融产品扩展表
        paFinProductMapper.insertSelective(paFinProduct);
        
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
        /* add by zhangpuyu 2019/10/24 start */
        //投后信息
        if(null!=dto.getFinprodId()){
            FinPostInvestInfo finPostInvestInfo=new FinPostInvestInfo();
        	finPostInvestInfo.setFinprodId(dto.getFinprodId());
        	finPostInvestInfo.setPostOrg(dto.getPostOrg());
            finPostInvestInfoMapper.insert(finPostInvestInfo);
        }
        /* add by zhangpuyu 2019/10/24 start */
        return 1;
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public int update(FinFundTargetDto dto) throws IllegalAccessException, InvocationTargetException {
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
        entity.setFinCashPayRuleList((List<FinCashPayRule>)param.get("finCashPayRuleList"));
      //修改标的类金融产品基本信息dto list
        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(entity);
        baseFinProductBaseInfoService.update(finprodIdList, list);
      //修改基金资产信息报送dto
        BaseRepFundAssetInfoDto repFundAssetInfoDto = new BaseRepFundAssetInfoDto();
        List<RepFund> repFundList= new ArrayList<RepFund>();
        repFundList.add((RepFund)param.get("repFund"));
        repFundAssetInfoDto.setRepFundList(repFundList);
        repFundAssetInfoDto.setRepFinprodIdList(finprodIdList);
        baseRepFundAssetInfoService.update(repFundAssetInfoDto);
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
      //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        //修改标的类金融产品扩展表
        paFinProductMapper.updateByPrimaryKey(paFinProduct);
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
        /* add by zhangpuyu 2019/10/24 start */
        //投后信息
        if(null!=dto.getFinprodId()){
        	finPostInvestInfoMapper.deleteByPrimaryKey(dto.getFinprodId());
            FinPostInvestInfo finPostInvestInfo=new FinPostInvestInfo();
        	finPostInvestInfo.setFinprodId(dto.getFinprodId());
        	finPostInvestInfo.setPostOrg(dto.getPostOrg());
        	finPostInvestInfoMapper.insert(finPostInvestInfo);
        }
        /* add by zhangpuyu 2019/10/24 start */
        return 1;
    }
    /**
     * 流程结束后，构建数据结构
     * 将画面传入的数据分配到各dto中
     * @param dto
     * @see
     * @since 1.0
     */
    private Map<String,Object> getDto(FinFundTargetDto dto) throws IllegalAccessException, InvocationTargetException {
        Map<String,Object> param = new HashMap<String,Object>();
        //标的类金融产品dto
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyProperties(finProduct,dto);
        finProduct.setFinprodId(dto.getFinprodId());
        finProduct.setBlnArea(DictCodeList.境内外_境内.getValue());
        if(StringUtil.equals(dto.getFundType(), DictCodeList.基金类资产类型_货币基金.getValue())){
            finProduct.setProfitType(DictCodeList.收益类型_货币型.getValue());
        }
        else if(StringUtil.equals(dto.getFundType(), DictCodeList.基金类资产类型_非货币基金.getValue())){
            finProduct.setProfitType(DictCodeList.收益类型_净值型.getValue());
            if(StringUtil.equals(dto.getSecondType(), DictCodeList.基金类资产二级类型_其他公募基金.getValue())){
                finProduct.setCapiIncomeFeature(null);
            }
        }
        //基金资产报送信息
        RepFund repFund = new RepFund();
        BeanUtil.copyProperties(repFund,dto.getBussRepFundInfoDto());
        repFund.setRepFinprodId(finProduct.getFinprodId());
        //根据画面.管理人，关联客户信息查询出客户名称
        String customerName = finFundTargetMapper.getCustomerNameByManager(dto.getManager());
        //将客户名称设置到资产报送信息的基金管理机构名称中
        repFund.setRepMngPartyName(customerName);
        //是否属于政府投资基金
        if (StringUtil.equals(repFund.getRepIsGovInvest(), DictCodeList.是否_否.getValue())) {
            repFund.setRepIsGovInvest(DictCodeList.是否_中债_否.getValue());
        }
       /* if (StringUtil.equals(repFund.getRepIsGovInvest(), DictCodeList.是否_是.getValue())) {
        	repFund.setRepIsGovInvest(DictCodeList.是否_中债_是.getValue());
        } else {
        	repFund.setRepIsGovInvest(DictCodeList.是否_中债_否.getValue());
        }*/
        //资产负债报送信息
        RepAssetDept repAssetDept = new RepAssetDept();
        BeanUtil.copyProperties(repAssetDept,dto.getBussRepFundInfoDto());
        if (StringUtil.equals(dto.getFundType(), DictCodeList.基金类资产类型_货币基金.getValue())) {
        	repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_货币市场基金.getValue());
        } else {
        	if (StringUtil.equals(dto.getSecondType(), DictCodeList.基金类资产二级类型_债券基金.getValue())) {
        		repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_债券基金.getValue());
        	} else if (StringUtil.equals(dto.getSecondType(), DictCodeList.基金类资产二级类型_股票基金.getValue())) {
        		repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_股票基金.getValue());
        	} else if (StringUtil.equals(dto.getSecondType(), DictCodeList.基金类资产二级类型_混合基金.getValue())) {
        		repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_混合基金.getValue());
        	} else if (StringUtil.equals(dto.getSecondType(), DictCodeList.基金类资产二级类型_其他公募基金.getValue())) {
        		repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_其他公募基金.getValue());
        	} else {
        		repAssetDept.setRepAssetType(DictCodeListExt2.资产_负债二级类别_基金中基金.getValue());
        	}
        }
        repAssetDept.setRepCcy(dto.getCcy());
        repAssetDept.setRepFinprodName(finProduct.getFinprodName());
        repAssetDept.setRepFinprodId(finProduct.getFinprodId());
        repAssetDept.setFinprodMarketId(finProduct.getFinprodMarketId());
        repAssetDept.setRepIssuePartyId(dto.getBussRepFundInfoDto().getRepAssetDept().getRepIssuePartyId());
        //标的类金融产品附属表dto
        FinProductAdd finProductAdd = new FinProductAdd();
        //add by zhangnian  20191106 start
        BeanUtil.copyPropertiesIgnoreNull(finProductAdd, dto);
        //add by zhangnian  20191106 end
        finProductAdd.setFinprodId(finProduct.getFinprodId());
        /*if(StringUtil.equals(dto.getPurSpeed(), DictCodeList.清算速度_T_0.getValue())){
            finProductAdd.setPurSpeed((long)0);
            finProductAdd.setRedSpeed((long)0);
        }
        else if(StringUtil.equals(dto.getPurSpeed(), DictCodeList.清算速度_T_1.getValue())){
            finProductAdd.setPurSpeed((long)1);
            finProductAdd.setRedSpeed((long)1);
        } else {
        	finProductAdd.setPurSpeed((long)2);
            finProductAdd.setRedSpeed((long)2);
        }*/
        //申购确认速速
        finProductAdd.setPurSpeed(Long.parseLong(dto.getPurSpeed()));
        //赎回确认速度
        finProductAdd.setRedSpeed(Long.parseLong(dto.getRedSpeed()));
        finProductAdd.setIsCompoundInt(dto.getIsCompoundInt());
        if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_OTC场外.getValue())){
            finProductAdd.setFaceValue(new BigDecimal(1));
        }
        // 投资性质
        finProductAdd.setInvestNature(dto.getInvestNature());
        //场外基金FIN_PRODUCT_ADD表中FACE_VALUE（面值）应存为1
        //金融产品分类表dto
        FinProductType finProductType = new FinProductType();
        finProductType.setFinprodId(finProduct.getFinprodId());
        long i = 0;
        finProductType.setBranch(i);
        finProductType.setType6(dto.getFundType());
        finProductType.setType7(dto.getSecondType());
        finProductType.setType8(DictCodeList.平安资产类型_基金.getValue());
        // 4 new types added by wangguiqi on 2019/7/22 -start
        finProductType.setType9(dto.getType9());
        finProductType.setType10(dto.getType10());
        finProductType.setType11(dto.getType11());
        finProductType.setType12(dto.getType12());
        // 4 new types added by wangguiqi on 2019/7/22 -start
        //现金流信息Entity-本金
        FinCashInfo finCashInfoCapital = new FinCashInfo();
        finCashInfoCapital.setCashId(EncryptUtil.getInstance().md5(dto.getFinprodId()+(char)256+finCashInfoCapital.getFinprodType()
        +(char)256+finCashInfoCapital.getBranch()+(char)256+finCashInfoCapital.getCashType2()));
        finCashInfoCapital.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        finCashInfoCapital.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
        finCashInfoCapital.setCcy(finProduct.getCcy());
        finCashInfoCapital.setFinprodId(finProduct.getFinprodId());
        finCashInfoCapital.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoCapital.setBranch(i);
       //现金流信息Entity-利息
        FinCashInfo finCashInfoInterest = new FinCashInfo();
        finCashInfoInterest.setCashId(EncryptUtil.getInstance().md5(dto.getFinprodId()+(char)256+finCashInfoCapital.getFinprodType()
        +(char)256+finCashInfoCapital.getBranch()+(char)256+finCashInfoCapital.getCashType2()));
        finCashInfoInterest.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        finCashInfoInterest.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        finCashInfoInterest.setCcy(finProduct.getCcy());
        finCashInfoInterest.setFinprodId(finProduct.getFinprodId());
        finCashInfoInterest.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        finCashInfoInterest.setBranch(i);
        //List<现金流信息Entity>
        List<FinCashInfo> finCashInfoList = new ArrayList<FinCashInfo>();
        finCashInfoList.add(finCashInfoCapital);
        finCashInfoList.add(finCashInfoInterest);
        //现金流支付规则表Entity/分红计划组件
        //分红计划组件dto
        
        if(dto.getBussFinIntPayDto() != null){
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            BeanUtil.copyProperties(finCashPayRule,dto.getBussFinIntPayDto());
            finCashPayRule.setCashId(finCashInfoInterest.getCashId());
            finCashPayRule.setEffDate(dto.getVdate());
            finCashPayRule.setFinprodId(finProduct.getFinprodId());
            finCashPayRule.setFinprodType(finProduct.getFinprodType());
            finCashPayRule.setFinprodType2(finProduct.getFinprodType2());
            finCashPayRule.setBranch(i);
            if(DictCodeList.收益结转频率_按月结转.getValue().equals(dto.getBussFinIntPayDto().getPayCycle())){
                finCashPayRule.setDayType(DictCodeListExt1.日期类型_几号.getValue());
            }
            
            // 一体化对应etliu 现金流支付规则表，支付频率没存值，可能有问题
            if (StringUtil.isBlank(finCashPayRule.getPayCycle())) {
				finCashPayRule.setPayCycle(BussinessConstant.DEFAULT_PAY_CYCLE);
			}
            
            //List<现金流支付规则表Entity>/List<分红计划组件Entity>
            List<FinCashPayRule> finCashPayRuleList = new ArrayList<FinCashPayRule>();
            finCashPayRuleList.add(finCashPayRule);
            param.put("finCashPayRuleList", finCashPayRuleList);
        }
        
        //获取要修改的金融产品代码集合(修改时用)
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(finProduct.getFinprodId());
        param.put("finProduct", finProduct);
        param.put("finProductAdd", finProductAdd);
        param.put("finProductType", finProductType);
        param.put("finCashInfoList", finCashInfoList);
        param.put("repFund", repFund);
        param.put("repAssetDept", repAssetDept);
        param.put("finprodIdList", finprodIdList);
        return param;
    }




    /**
     * 调用组件删除
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int delete(FinFundTargetDto dto) {
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(dto.getFinprodId());
        //基金资产报送信息dto
        BaseRepFundAssetInfoDto baseRepFundAssetInfoDto = new BaseRepFundAssetInfoDto();
        baseRepFundAssetInfoDto.setRepFinprodIdList(finprodIdList);
        //资产负债报送信息dto
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = new BaseRepAssetDeptInfoDto();
        baseRepAssetDeptInfoDto.setFinprodIdList(finprodIdList);
      //删除委外组合 add wangpeng 2019.07.23
        if(StringUtil.isNotEmpty(dto.getFinprodMarketId())){
        	 ptlPortfolioMapper.deleteByPrimaryKey(dto.getFinprodMarketId());
        }
      //删除授信关联
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(dto.getFinprodId());
        assetCommonService.deleteCredit(creditLines);
        /* add by zhangpuyu 2019/10/24 start */   //update by caiqun 20200731 调整位置，否则不被调用
        //投后信息
        if(null!=dto.getFinprodId()){
            finPostInvestInfoMapper.deleteByPrimaryKey(dto.getFinprodId());
        }
        /* add by zhangpuyu 2019/10/24 start */     //update by caiqun 20200731 调整位置，否则不被调用
        if(baseFinProductBaseInfoService.delete(finprodIdList) 
            && baseRepFundAssetInfoService.delete(baseRepFundAssetInfoDto)
            && baseRepAssetDeptInfoService.deleteByPrimaryKey(baseRepAssetDeptInfoDto)
            && (finFundTargetMapper.deleteByFinprodId(dto.getFinprodId())==1)
            ){
            return 1;
        }
        return 0;
    }


    /**
     * 根据金融产品代码查询dto
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public FinFundTargetDto selectByFinprodId(String finprodId) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("finprodId", finprodId);
        param.put("CASH_TYPE_2", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("FIN_CASH_BUSINESS_TYPE", DictCodeListExt1.金融产品类型_理财产品.getValue());
        param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        //平台日
        param.put("platDate", DateUtil.platDateFromCache());
        return finFundTargetMapper.selectByFinprodId(param);
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public FinFundTargetDto findEntity(Map<String, Object> param) {
        param.put("CASH_TYPE_2", DictCodeListExt1.现金流二级类型_利息.getValue());
        param.put("FIN_CASH_BUSINESS_TYPE", DictCodeListExt1.金融产品类型_理财产品.getValue());
        param.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return finFundTargetMapper.findEntity(param);
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public BussFinIntPayDto getIntPayByFinprodId(String finprodId) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("finprodId", finprodId);
        param.put("FIN_CASH_BUSINESS_TYPE_01", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        param.put("CASH_TYPE_1_02", DictCodeListExt1.现金流一级类型_利息.getValue());
        param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
        return finFundTargetMapper.getIntPayByFinprodId(param);
    }


    /**
     * 给基金中债报送页面赋值
     * <功能详细描述>
     * @param finprodId
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    public BussRepFundInfoDto getFundInfoByFinprodId(String finprodId) throws IllegalAccessException, InvocationTargetException {
        BussRepFundInfoDto entity = new BussRepFundInfoDto();
        //根据金融产品代码查询基金资产报送数据
        BaseRepFundAssetInfoDto dto1 = new BaseRepFundAssetInfoDto();
        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(finprodId);
        dto1.setRepFinprodIdList(finprodIdList);
        List<RepFund> repFundList = baseRepFundAssetInfoService.search(dto1);
        //根据金融产品代码查询资产负债报送数据
        BaseRepAssetDeptInfoDto dto2 = new BaseRepAssetDeptInfoDto();
        dto2.setFinprodIdList(finprodIdList);
        List<RepAssetDept> repAssetDeptList = baseRepAssetDeptInfoService.selectRepAssetDeptInfo(dto2);
        if(repFundList != null && repAssetDeptList != null){
            if(repFundList.size()>0){
                BeanUtil.copyProperties(entity,repFundList.get(0));
            }
            if(repAssetDeptList.size()>0){
            	String repAssetType = repAssetDeptList.get(0).getRepAssetType();
            	if (StringUtil.equals(repAssetType, DictCodeListExt2.资产_负债二级类别_货币市场基金.getValue())) {
            		repAssetType = DictCodeListExt2.资产_负债二级类别_货币市场基金.getValue();
                } else {
                	if (StringUtil.equals(repAssetType, DictCodeListExt2.资产_负债二级类别_债券基金.getValue())) {
                		repAssetType = DictCodeListExt2.资产_负债二级类别_债券基金.getValue();
                    }
                	else if (StringUtil.equals(repAssetType, DictCodeListExt2.资产_负债二级类别_股票基金.getValue())) {
                		repAssetType = DictCodeListExt2.资产_负债二级类别_股票基金.getValue();
                    }
                	else if (StringUtil.equals(repAssetType, DictCodeListExt2.资产_负债二级类别_混合基金.getValue())) {
                		repAssetType = DictCodeListExt2.资产_负债二级类别_混合基金.getValue();
                    }
                	else if (StringUtil.equals(repAssetType, DictCodeListExt2.资产_负债二级类别_其他公募基金.getValue())) {
                		repAssetType = DictCodeListExt2.资产_负债二级类别_其他公募基金.getValue();
                    }
                	else {
                		repAssetType = DictCodeListExt2.资产_负债二级类别_基金中基金.getValue();
                    }
                }
            	//add by zhangnian 20191014 start
            	//新增信息
                RepAssetOther repAssetOther = repAssetOtherMapper.selectByPrimaryKey(entity.getRepFinprodId());
                if(null!=repAssetOther){
                    BeanUtil.copyProperties(entity,repAssetOther);
                }
                //add by zhangnian 20191014  end
                entity.setRepAssetType(repAssetType);
                entity.setRepTradeMarket(repAssetDeptList.get(0).getRepTradeMarket());
            }
        }
        return entity;
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param entity FinFundTargetDto
     * @see
     * @since 1.0
     */
    public void doInsert(FinFundTargetDto entity) {
      //文件
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        mstFileInputParameter.setFileList(entity.getFileArray());
        mstFileInputParameter.setBussinessId(entity.getWorkflowId());
        if(mstFileCommonService != null){
            mstFileCommonService.updateBusinessId(mstFileInputParameter);
        }
    }


    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param pageParam
     * @return
     * @see
     * @since 1.0
     */
    public BussRepFundInfoDto getInfoByIssuer(Map<String, Object> pageParam) {
        return finFundTargetMapper.getInfoByIssuer(pageParam);
    }
 }
