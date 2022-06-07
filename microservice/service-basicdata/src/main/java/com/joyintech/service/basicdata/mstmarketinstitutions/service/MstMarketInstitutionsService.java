/*
 * 文件名：MstMarketInstitutionsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年8月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.mstmarketinstitutions.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.common.base.dao.MstBondCreditRatingMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoAddMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoMapper;
import com.joyintech.tams.common.base.entity.InterfCmsInstitution;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.common.base.entity.MstCustomerInfo;
import com.joyintech.tams.common.base.entity.MstCustomerInfoAddWithBLOBs;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.mstmarketinstitutions.dao.MstMarketInstitutionsMapper;
import com.joyintech.service.basicdata.mstmarketinstitutions.dto.MstMarketInstitutionsDto;

/**
 * 
 * 市场机构
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstMarketInstitutionsService
 * @since 1.0
 */
@Service("backMstMarketInstitutionsService")
public class MstMarketInstitutionsService extends BaseService<MstMarketInstitutionsDto> {
    /**
     * 市场机构mapper注入
     */
    @Autowired
    private MstMarketInstitutionsMapper mstMarketInstitutionsMapper;
    /**
     * 客户信息附属表
     */
    @Autowired
    private MstCustomerInfoAddMapper mstCustomerInfoAddMapper;
    /**
     * 客户信息表
     */
    @Autowired
    private MstCustomerInfoMapper mstCustomerInfoMapper;
    /**
     * 债券评级表
     */
    @Autowired
    private MstBondCreditRatingMapper mstBondCreditRatingMapper;
    /**
     * 客户代码联想文本框service层实现
      *
      * @param map 检索参数
      * @return SuggestDto
      */
    public List<SuggestDto> getCustomerId(Map<String, Object> map) {
        return mstMarketInstitutionsMapper.getCustomerId(map);
    }
     
     /**
      * 客户名称联想文本框service层实现
       *
       * @param map Map<String, Object> 检索参数
       * @return SuggestDto
       */
    public List<SuggestDto> getCustomerName(Map<String, Object> map) {
        return mstMarketInstitutionsMapper.getCustomerName(map);
    }
    
    /**
     * 使用给定的检索分页数据。返回类型是Service的泛型。
     *  市场机构代码
     *  市场机构名称
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<MstMarketInstitutionsDto> selectForPage(int page,int length,Map<String, Object> pageParam)
    {
        return super.queryForPage(page,length,() -> mstMarketInstitutionsMapper.select(pageParam));
    }
    /**
     * selectPaRskCustomerPage
     * 根据客户代码查询市场机构详细信息
     * <功能详细描述>
     * @param map map
     * @return MstMarketInstitutionsDto
     * @see
     * @since 1.0
     */
    public MstMarketInstitutionsDto selectDetail(Map<String, Object> map){
        return mstMarketInstitutionsMapper.selectDetail(map);
    }
    
    /**
     * 
     *删除时检查数据是否被使用过
     * <功能详细描述>
     * @param dto MstMarketInstitutionsDto
     * @return int
     * @see
     * @since 1.0
     */
    public int checkCodeIsUse(MstMarketInstitutionsDto dto){
        return mstMarketInstitutionsMapper.checkCodeIsUse(dto);
    }
    
    /**
     * 
     *批量删除时检查数据是否被使用过
     * <功能详细描述>
     * @param item List<MstMarketInstitutionsDto> 
     * @return int
     * @see
     * @since 1.0
     */
    public List<MstMarketInstitutionsDto> checkAllCodeIsUse(List<MstMarketInstitutionsDto> item){
        return mstMarketInstitutionsMapper.checkAllCodeIsUse(item);
    }
    
    /**
     * 
     * 通过客户代码删除客户评级表信息 
     * <功能详细描述>
     * @param customerId String
     * @return int
     * @see
     * @since 1.0
     */
    public int deleteMstBondCreditRatingByPrimaryKey(String customerId){
        return mstMarketInstitutionsMapper.deleteMstBondCreditRatingByPrimaryKey(customerId);
    }
    
    /**
     * 
     * 通过客户代码查询客户评级表信息 
     * <功能详细描述>
     * @param customerId String
     * @return List<PaRskCustomerRating>
     * @see
     * @since 1.0
     */
    public List<MstBondCreditRating> selectMstBondCreditRatingByPrimaryKey(String customerId){
        return mstMarketInstitutionsMapper.selectMstBondCreditRatingByPrimaryKey(customerId);
        
    }
    /**
     * 
     * 实现docomplement方法
     */
    public void doComplete(MstMarketInstitutionsDto entity) {
        //客户信息
        MstCustomerInfo mstCustomerInfo = new MstCustomerInfo();
        //客户评级
        //PaRskCustomerRating paRskCustomerRating = new PaRskCustomerRating();
        //客户信息附属表
        //MstCustomerInfoAdd mstCustomerInfoAdd = new MstCustomerInfoAdd();
        if(StringUtil.equals(entity.getOperateType(), "DELETE")) {
            //客户信息附属表删除数据
            mstCustomerInfoAddMapper.deleteByPrimaryKey(entity.getCustomerId());
            //客户信息表删除数据
            mstCustomerInfoMapper.deleteByPrimaryKey(entity.getCustomerId());
            //客户评级表删除数据
            mstMarketInstitutionsMapper.deleteMstBondCreditRatingByPrimaryKey(entity.getCustomerId());
            
        }
        else if(StringUtil.equals(entity.getOperateType(), "NEW")){
        	MstCustomerInfoAddWithBLOBs istEntity = new MstCustomerInfoAddWithBLOBs();
            BeanUtil.copyPropertiesIgnoreNull(istEntity, entity);
            //客户信息附属表插入数据
            mstCustomerInfoAddMapper.insert(istEntity);
            //客户信息表插入数据
            mstCustomerInfo.setCustomerId(entity.getCustomerId());//客户代码
            mstCustomerInfo.setCustomerName(entity.getCustomerName());//客户名称
            mstCustomerInfo.setCustomerType(DictCodeList.客户类型_法人.getValue());
            if(StringUtil.isNotEmpty(entity.getIsIssuer())) {//是否发行人
                mstCustomerInfo.setIsIssuer(entity.getIsIssuer());
            }
            mstCustomerInfoMapper.insert(mstCustomerInfo);
            //客户评级表插入数据
            List<MstBondCreditRating> mstBondCreditRatingList = new ArrayList<MstBondCreditRating>();
            mstBondCreditRatingList = entity.getMstBondCreditRating();
            for(MstBondCreditRating mstBondCreditRating : mstBondCreditRatingList){
            	mstBondCreditRating.setoCode(entity.getCustomerId());
            	mstBondCreditRating.setoType(DictCodeList.评级类型_主体评级.getValue());
            	mstBondCreditRating.setInputType(DictCodeListExt1.录入方式_手工录入.getValue());
            	mstBondCreditRatingMapper.insertSelective(mstBondCreditRating);
            } 
        }
        else{
            mstCustomerInfoAddMapper.updateByPrimaryKeySelective(entity);
            //客户信息表更新数据
            mstCustomerInfo =   mstCustomerInfoMapper.selectByPrimaryKey(entity.getCustomerId());      
            mstCustomerInfo.setCustomerId(entity.getCustomerId());//客户代码
            mstCustomerInfo.setCustomerName(entity.getCustomerName());//客户名称
            mstCustomerInfo.setCustomerType(DictCodeList.客户类型_法人.getValue());
            mstCustomerInfoMapper.updateByPrimaryKey(mstCustomerInfo);
            //客户评级表数据更新 先删除然后插入
            //客户评级表删除数据
            mstMarketInstitutionsMapper.deleteMstBondCreditRatingByPrimaryKey(entity.getCustomerId());
            //客户评级表插入数据
            List<MstBondCreditRating> mstBondCreditRatingList = new ArrayList<MstBondCreditRating>();
            mstBondCreditRatingList = entity.getMstBondCreditRating();
            for(MstBondCreditRating mstBondCreditRating : mstBondCreditRatingList){
            	mstBondCreditRating.setoCode(entity.getCustomerId());
            	mstBondCreditRating.setoType(DictCodeList.评级类型_主体评级.getValue());
            	mstBondCreditRating.setInputType(DictCodeListExt1.录入方式_手工录入.getValue());
            	mstBondCreditRatingMapper.insertSelective(mstBondCreditRating);
            } 
        }  
    }
    
    /**
     * 
     * 省市县联动
     * @param param
     * @return
     */
    @Transactional
    @DataSource(DataSourceType.OTHER)
    public List<SelectOptionDto> getAddressSelectOption(Map<String, Object> param){
        return mstMarketInstitutionsMapper.getAddressSelectOption(param);
    }
    
    /**
     * 
     * 查询所有的市场机构代码、名称
     * <功能详细描述>
     * @return List<MstMarketInstitutionsDto>
     * @see
     * @since 1.0
     */
    public List<MstMarketInstitutionsDto> customerNameCheck(String customerName){
        Map<String, Object> param = new HashMap<>();
        param.put("customerName", customerName);
        param.put("customerType", DictCodeList.客户类型_法人.getValue());
        return  mstMarketInstitutionsMapper.customerNameCheck(param);
    }
    
    /**
     * 
     * 根据becif查询CMS_机构信息
     * <功能详细描述>
     * @param becif String
     * @return InterfCmsInstitution
     * @see
     * @since 1.0
     */
    public InterfCmsInstitution selectInterfCmsInstitution(String becif){
        return mstMarketInstitutionsMapper.selectInterfCmsInstitution(becif);
    }
    
    /**
     * 获取评级机构
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getGradeOrgId(Map<String, Object> param) {
    	return this.queryForSuggest(() -> mstMarketInstitutionsMapper.getGradeOrgId(param), null);
    }

    /**
     * 
     * 批量删除时检查数据是否进行过客户性质匹配
     * <功能详细描述>
     * @param item List<MstMarketInstitutionsDto> 
     * @return int
     * @see
     * @since 1.0
     */
    public List<MstMarketInstitutionsDto> deleteCheck(List<MstMarketInstitutionsDto> item){
        return mstMarketInstitutionsMapper.deleteCheck(item);
    }

    /**
     * 1.判断机构表中是否存在
     * 存在：则设置为发行人
     * 不存在：执行第二段逻辑
     * 2.判断接口表里是否存在,
     * 不存在：返回null
     * 存在：判断必填元素是否齐全，齐全则自动添加并设置为发行人
     * 要素不齐全则返回查询数据回前台
     *
     * @param insitituteCode
     * @return
     */
    public MstMarketInstitutionsDto autoAddInstitutions(String insitituteCode) {
        MstMarketInstitutionsDto mstDto = mstMarketInstitutionsMapper.getByInsitituteCode(insitituteCode);
        if(Objects.nonNull(mstDto)) {
            MstCustomerInfo customerInfo = new MstCustomerInfo();
            customerInfo.setCustomerId(mstDto.getCustomerId());
            customerInfo.setIsIssuer(DictCodeList.是否_是.getValue());
            mstCustomerInfoMapper.updateByPrimaryKeySelective(customerInfo);
            mstDto.setDealFlag(0);//不需要处理
            return mstDto;
        }
        MstMarketInstitutionsDto dto = mstMarketInstitutionsMapper.getInterfCustomerInfo(insitituteCode);
        if(Objects.nonNull(dto)) {
            dto.setDealFlag(1);//接口中的需要进行处理
            dto.setIsIssuer(DictCodeList.是否_是.getValue());
        }
        return dto;
    }
}
