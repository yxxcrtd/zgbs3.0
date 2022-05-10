/*
 * 文件名：FinPlanAssetNet001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：liuwei
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.asset.plan.planNet.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.asset.common.dao.AssetCommonMapper;
import com.joyintech.service.asset.common.service.AssetCommonService;
import com.joyintech.service.asset.plan.planNet.dao.FinPlanNetAssetMapper;
import com.joyintech.service.asset.plan.planNet.dto.BussRepAssetMngProdInfo;
import com.joyintech.service.asset.plan.planNet.dto.BussRepOutInvestAgrInfo;
import com.joyintech.service.asset.plan.planNet.dto.FinPlanNetAssetDto;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
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
import com.joyintech.tams.invest.common.CommonUtil;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 计划类资产（净值型）Service
 * 
 * @author liuwei
 * @version 1.0 2018年9月10日
 * @see FinPlanNetAssetService
 * @since 1.0
 */
@Service("backFinPlanNetAssetService")
public class FinPlanNetAssetService extends BaseService<FinPlanNetAssetDto> {
    
    /**
     * 定义计划类资产（净值型）mapper
     */
    @Autowired
    private FinPlanNetAssetMapper finPlanNetAssetMapper;
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
    // 投后信息表mapper by赵东东 2019.9.29 start
    /**
     * 投后信息表mapper
     */
    @Autowired 
    private FinPostInvestInfoMapper fnPostInvestInfoMapper;
     // 投后信息表mapper by赵东东 2019.9.29 end
    @Autowired 
    private RepAssetOtherMapper repAssetOtherMapper;
    /**
     * 查询是否自动创建组合AssetTypeConfigMapper
     */
    @Autowired
    private AssetTypeConfigMapper assetTypeConfigMapper;
  //标的共通service
    @Autowired(required=false)
    private AssetCommonService assetCommonService;
    
    @Autowired
    private AssetCommonMapper assetCommonMapper;
    
    /**
     * 基础 --附件
     *//*
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;*/
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
        map.put("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        map.put("finprodMarketId", finprodMarketId);
        return finPlanNetAssetMapper.getFinProdMarketId(map);
    }
    
    /**
     * 使用给定的sqlID检索分页数据。返回类型是Service的泛型。
     * @param pageParam pageParam
     * @param page 页码
     * @param length 页面长度
     * @return Page类  Page
     * @throws Exception 异常信息
     */
    public Page<FinPlanNetAssetDto> selectForPage(int page, int length, Map<String, Object> pageParam)
    {
        pageParam.put("TYPE_TOUGUAN_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        pageParam.put("PROFIT_TYPE_01", DictCodeList.收益类型_净值型.getValue());
        if(StringUtil.isNotEmpty(pageParam.get("category"))){
            pageParam.put("categoryList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("category")));
        }
        if(StringUtil.isNotEmpty(pageParam.get("type"))){
            pageParam.put("typeList",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type")));
        }
       /* 新增行内五级分类 zhangpuyu 2019-7-25start */
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
//      if(StringUtil.isNotEmpty(pageParam.get("type12"))){
//          pageParam.put("type12List",StringUtil.splitToList(GlobalConstant.COMMA,(String)pageParam.get("type12")));
//      }
        /* 新增行内五级分类 zhangpuyu 2019-7-25 end */
        return super.queryForPage(page, length, () -> finPlanNetAssetMapper.select(pageParam));
    }

    /**
     * 出账编号的联想下拉框事件
     * @param map 输入管理人参数
     * @return 查询的管理人代码和管理人名称列表
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCmsId(Map<String,Object> map) {
        return finPlanNetAssetMapper.getCmsId(map);
    }

    /**
     * 出账编号的联动事件
     *
     * @param cmsId 输入管理人参数
     * @return 查询的管理人代码和管理人名称列表
     * @see
     * @since 1.0
     */
    public FinPlanNetAssetDto getChange(String cmsId) {
        return finPlanNetAssetMapper.getChange(cmsId);
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
        return finPlanNetAssetMapper.getIssuer(map);
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
        return finPlanNetAssetMapper.getTrusteeId(map);
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
        return finPlanNetAssetMapper.getCalendarId(map);
    }
    /**
     * 删除操作
     * 
     * @param dto 选中参数
     * @see
     * @since 1.0
     */
    public void delete(FinPlanNetAssetDto dto) {
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
        // 删除投后信息 by 赵东东2019.10.21 start 
        fnPostInvestInfoMapper.deleteByPrimaryKey(dto.getFinprodId());
        // 删除投后信息 by 赵东东2019.10.21 end 
      //删除授信关联
        CreditLines creditLines = new CreditLines();
        creditLines.setCustomerId(dto.getFinprodId());
        assetCommonService.deleteCredit(creditLines);
        repAssetOtherMapper.deleteByPrimaryKey(dto.getFinprodId());
      //删除委外组合 add wangpeng 2019.07.23
        if(StringUtil.isNotEmpty(dto.getFinprodMarketId())){
        	 ptlPortfolioMapper.deleteByPrimaryKey(dto.getFinprodMarketId());
        }
    }

    /**
     * 插入操作
     *
     * @param dto 选中参数
     * @see
     * @since 1.0
     */
    public void insert(FinPlanNetAssetDto dto) throws Exception{
        //构建标的类金融产品dto
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        baseFinProductBaseInfoDto.setFinProduct(dto.getFinProduct());
        baseFinProductBaseInfoDto.setFinProductAdd(dto.getFinProductAdd());
        baseFinProductBaseInfoDto.setFinProductType(dto.getFinProductType());
        baseFinProductBaseInfoDto.setFinCashInfoList(dto.getFinCashInfoList());
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(dto.getFinCashCalcRuleList());
        baseFinProductBaseInfoDto.setFinRateInfoList(dto.getFinRateInfoList());
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

        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(dto, baseFinProductBaseInfoDto);

        List<BaseFinProductBaseInfoDto> list = new ArrayList<>();
        list.add(baseFinProductBaseInfoDto);
        //调用标的类金融产品基础组件进行插入
        baseFinProductBaseInfoService.insert(list);
        //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        paFinProduct.setFinprodId(dto.getFinProduct().getFinprodId());
        //paFinProduct.setCashOutNo(dto.getCmsId());
        //将数据插入到标的类金融产品扩展表
        paFinProductMapper.insertSelective(paFinProduct);
        //调用资产管理计划中债报送信息基础组件插入数据
        //投后信息插入数据by赵东东 2019.9.29 start
        FinPostInvestInfo finPostInvestInfo = new FinPostInvestInfo();
        BeanUtil.copyPropertiesIgnoreNull(finPostInvestInfo,dto);
        fnPostInvestInfoMapper.insert(finPostInvestInfo);
        //投后信息插入数据by赵东东 2019.9.29 end
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
            //插入中债资产其它要素by赵东东 2019.10.10 start
            repAssetOtherMapper.insert(dto.getRepAssetOther());
            //插入中债资产其它要素by赵东东 2019.10.10 end
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
            //插入中债资产其它要素by赵东东 2019.10.10 start
            repAssetOtherMapper.insert(dto.getRepAssetOther());
            //插入中债资产其它要素by赵东东 2019.10.10 end
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
        if (Objects.nonNull(dto.getRepAssetDept())){
            repAssetDeptList.add(dto.getRepAssetDept());
            for (RepAssetDept r :repAssetDeptList){
                r.setRepCcy(dto.getCcy());
            }
        }
        baseRepAssetDeptInfoService.insert(repAssetDeptList);
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
    public void update(FinPlanNetAssetDto dto) throws Exception{
        //构建标的类金融产品dto
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        baseFinProductBaseInfoDto.setFinProduct(dto.getFinProduct());
        baseFinProductBaseInfoDto.setFinProductAdd(dto.getFinProductAdd());
        baseFinProductBaseInfoDto.setFinProductType(dto.getFinProductType());
        baseFinProductBaseInfoDto.setFinCashInfoList(dto.getFinCashInfoList());
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(dto.getFinCashCalcRuleList());
        baseFinProductBaseInfoDto.setFinRateInfoList(dto.getFinRateInfoList());
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
        List<BaseFinProductBaseInfoDto> list = new ArrayList<>();

        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(dto, baseFinProductBaseInfoDto);
        list.add(baseFinProductBaseInfoDto);
        //调用标的类金融产品基础组件进行修改
        baseFinProductBaseInfoService.update(finprodIdList,list);
        //构建标的类金融产品扩展dto
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyPropertiesIgnoreNull(paFinProduct,dto);
        paFinProduct.setFinprodId(dto.getFinProduct().getFinprodId());
        //paFinProduct.setCashOutNo(dto.getCmsId());
        //对标的类金融产品扩展表的数据进行修改
        paFinProductMapper.updateByPrimaryKeySelective(paFinProduct);
        // 投后信息表修改 by 赵东东 2019.10.17 start 
        FinPostInvestInfo finPostInvestInfo = new FinPostInvestInfo();
        BeanUtil.copyPropertiesIgnoreNull(finPostInvestInfo,dto);
        fnPostInvestInfoMapper.updateByPrimaryKey(finPostInvestInfo);
         // 投后信息表修改 by 赵东东 2019.10.17 end 
        //调用资产管理计划中债报送信息基础组件修改数据
        if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_信托产品.getValue())
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
          //修改中债资产其它要素by赵东东 2019.10.10 start
           repAssetOtherMapper.updateByPrimaryKeySelective(dto.getRepAssetOther());
            //修改中债资产其它要素by赵东东 2019.10.10 end
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
          //修改中债资产其它要素by赵东东 2019.10.10 start
            repAssetOtherMapper.updateByPrimaryKeySelective(dto.getRepAssetOther());
          //修改中债资产其它要素by赵东东 2019.10.10 end
        }
        //调用资产负债报送信息组件修改数据
        BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto = new BaseRepAssetDeptInfoDto();
        List<RepAssetDept> repAssetDeptList = new ArrayList<>();
        // 20210720 增加判空校验 by caozhenming start
        if (Objects.nonNull(dto.getRepAssetDept())){
            repAssetDeptList.add(dto.getRepAssetDept());
            for (RepAssetDept r :repAssetDeptList){
                r.setRepCcy(dto.getCcy());
            }
        }
        // 20210720 增加判空校验 by caozhenming end
        baseRepAssetDeptInfoDto.setRepAssetDeptList(repAssetDeptList);
        List<String> finProdIdList = new ArrayList<>();
        finProdIdList.add(dto.getFinProduct().getFinprodId());
        baseRepAssetDeptInfoDto.setFinprodIdList(finProdIdList);
        baseRepAssetDeptInfoService.updateBaseRepAssetDeptInfoDto(baseRepAssetDeptInfoDto);
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
    }

    /**
     * 修改或双击查看界面显示
     *
     * @param finprodId 金融产品代码
     * @return 基本信息页面值
     * @see
     * @since 1.0
     */
    public FinPlanNetAssetDto getBasicInfoByFinprodId(String finprodId){
        Map<String,Object> map = new HashMap<>();
        map.put("finprodId",finprodId);
        map.put("FIN_CASH_BUSINESS_TYPE_01",DictCodeListExt1.现金流业务类型_金融产品.getValue());
        map.put("CASH_TYPE_1_02",DictCodeListExt1.现金流一级类型_利息.getValue());
        return finPlanNetAssetMapper.getBasicInfoByFinprodId(map);
    }
    /**
     * 资产管理计划业务组件
     *
     * @param finprodId 金融产品代码
     * @return 资产管理计划业务组件页面值
     * @see
     * @since 1.0
     */
    public BussRepAssetMngProdInfo getRepAssetMngProdInfoByFinprodId(String finprodId){
        return finPlanNetAssetMapper.getRepAssetMngProdInfoByFinprodId(finprodId);
    }
    /**
     * 委外投资-协议方式业务组件
     *
     * @param finprodId 金融产品代码
     * @return 委外投资-协议方式业务组件页面值
     * @see
     * @since 1.0
     */
    public BussRepOutInvestAgrInfo getRepOutInvestAgrInfoByFinprodId(String finprodId){
        return finPlanNetAssetMapper.getRepOutInvestAgrInfoByFinprodId(finprodId);
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
    	Map<String,Object> paraMap = new HashMap<String,Object>();
    	paraMap.put("finprodMarketId",finprodMarketId);
        return finPlanNetAssetMapper.selectFinprodMarketId(paraMap);
    }

    /**
     * 查询客户名称
     *
     * @param custormId 客户代码
     * @return 客户名称
     * @see
     * @since 1.0
     */
    public String getName(String custormId){
        return finPlanNetAssetMapper.getName(custormId);
    }
    // 增加查询资产代码 by 赵东东 2019.10.12  start
    /**
     * 查询资产代码
     *
     * @param finprodMarketId 资产代码
     * @return 查询资产代码
     * @see
     * @since 1.0
     */
    public String getFinprodId(String finprodMarketId){
        return finPlanNetAssetMapper.getFinprodId(finprodMarketId);
    }
     // 增加查询资产代码 by 赵东东 2019.10.12  end

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
    private void procFinProductOpenPlan(FinPlanNetAssetDto dto,
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
