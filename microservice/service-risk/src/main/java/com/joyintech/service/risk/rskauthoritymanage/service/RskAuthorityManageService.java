/*
 * 文件名：RskAuthorityManageService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月30日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.risk.rskauthoritymanage.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.common.base.dao.RskAuthorizeMngMapper;
import com.joyintech.tams.common.base.entity.RskAuthorizeMng;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.risk.rskauthoritymanage.dao.RskAuthorityManageMapper;
import com.joyintech.service.risk.rskauthoritymanage.dto.RskAuthorityManageDto;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author qiangchengxi
 * @version 1.0 2018年11月30日
 * @see RskAuthorityManageService
 * @since 1.0
 */
@Service("backRskAuthorityManageService")
public class RskAuthorityManageService extends BaseService<RskAuthorityManageDto> {
    @Autowired
    private RskAuthorityManageMapper rskAuthorityManageMapper;
    @Autowired
    private RskAuthorizeMngMapper rskAuthorizeMngMapper;
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getUser(Map<String, Object> param,int length,int page) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        Page<SelectOptionDto> resultList = this.queryForPageForSelectReturn(page, length, () -> rskAuthorityManageMapper.getUser(param));
        DynamicDataSourceHolder.clearDataSource();
        return resultList;
    }
    /**
     * 页面查询
     * <功能详细描述>
     * @param page
     * @param length
     * @param param
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public Page<RskAuthorityManageDto> selectAll(int page, int length, Map<String, Object> param) {
        String userName = (String)param.get("userName");
        if(StringUtil.isNotEmpty(userName)){
            param.put("userName", StringUtil.splitToList(GlobalConstant.COMMA,userName));
        }else{
            param.put("userName",null);
        }
        String tradeMarket = (String)param.get("tradeMarket");
        if(StringUtil.isNotEmpty(tradeMarket)){
            param.put("tradeMarket", StringUtil.splitToList(GlobalConstant.COMMA,tradeMarket));
        }else{
            param.put("tradeMarket",null);
        }
        String tradeDirection = (String)param.get("tradeDirection");
        if(StringUtil.isNotEmpty(tradeDirection)){
            param.put("tradeDirection", StringUtil.splitToList(GlobalConstant.COMMA,tradeDirection));
        }else{
            param.put("tradeDirection",null);
        }
        String assetSecType = (String)param.get("assetSecType");
        if(StringUtil.isNotEmpty(assetSecType)){
            param.put("assetSecType", StringUtil.splitToList(GlobalConstant.COMMA,assetSecType));
        }else{
            param.put("assetSecType",null);
        }
        param.put("AUTHORIZE_TYPE_02", DictCodeList.授权类型_授权.getValue());
        Page<RskAuthorityManageDto> pag = super.queryForPage(page, length, () -> rskAuthorityManageMapper.select(param));
        List<RskAuthorityManageDto> list = pag.getRoot();
        if(list != null && list.size()>0){
            for(RskAuthorityManageDto d:list){
                DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
                String realName = rskAuthorityManageMapper.selectRealNameByUserName(d.getUserName());
                DynamicDataSourceHolder.clearDataSource();
                d.setUserName(realName);
            }
        }
        pag.setRoot(list);
        return pag;
    }
    /**
     * 根据用户名查询真实的名字
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public String selectRealNameByUserName(String userName){
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        String realName = rskAuthorityManageMapper.selectRealNameByUserName(userName);
        DynamicDataSourceHolder.clearDataSource();
        return realName;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int update(RskAuthorityManageDto dto) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("rolePos", dto.getRolePos());
        map.put("userName", dto.getUserName());
        List<RskAuthorityManageDto> list = rskAuthorityManageMapper.getDtoByUserName(map);
        for(RskAuthorityManageDto e:list){
            if(checkDtoIsConflict(e,dto) && !StringUtil.equals(e.getSeqNo(), dto.getSeqNo())){
                rskAuthorizeMngMapper.deleteByPrimaryKey(e.getSeqNo());
            }
        }
        if(StringUtil.equals(dto.getAuthorizeType(), DictCodeList.授权类型_无限授权.getValue())){
            BigDecimal b = new BigDecimal("999999999999999");
            dto.setAuthorizeLimitAmt(b);
        }
        /*if(StringUtil.equals(dto.getAuthorizeType(), BussinessDictCodeList.授权类型_不授权.getValue())){
            BigDecimal b = new BigDecimal("-1");
            dto.setAuthorizeLimitAmt(b);
        }*/
        else{
            dto.setAuthorizeLimitAmt(dto.getAuthorizeLimitAmt().multiply(BigDecimal.valueOf(10000)));
        }
        return rskAuthorizeMngMapper.updateByPrimaryKey(dto);
    }
    /**
     * 
     * check数据知否重复
     * <功能详细描述>
     * @param dto1
     * @param dto2
     * @return
     * @see
     * @since 1.0
     */
    public boolean checkDtoIsConflict(RskAuthorityManageDto dto1,RskAuthorityManageDto dto2){
        //第一级返回false再判断第二级，第二级返回false再判断第三级
        if(isNotSame(dto1.getRolePos(), dto2.getRolePos())||isNotSame(dto1.getUserName(), dto2.getUserName())
            ||isNotSame(dto1.getAssetType(), dto2.getAssetType())||isNotSame(dto1.getAssetSecType(),dto2.getAssetSecType())||isNotSame(dto1.getTradeMarket(),dto2.getTradeMarket())
            ||isNotSame(dto1.getTradeDirection(),dto2.getTradeDirection())){
            return false;
        }
        //返回true证明授权重复
        return true;
    }
    
    public boolean isNotSame(String str1,String str2){
        //如果Str1与str2不相等且str1和str2都不为空则返回true
        return !StringUtil.equals(str1, str2)&&StringUtil.isNotEmpty(str1)&&StringUtil.isNotEmpty(str2);
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int insert(RskAuthorityManageDto dto) {
        for(RskAuthorityManageDto d:dto.getSelectedItem()){
            if(StringUtil.equals(d.getAuthorizeType(), DictCodeList.授权类型_无限授权.getValue())){
                BigDecimal b = new BigDecimal("999999999999999");
                d.setAuthorizeLimitAmt(b);
            }
            else if(StringUtil.equals(d.getAuthorizeType(), DictCodeList.授权类型_不授权.getValue())){
                BigDecimal b = new BigDecimal("-1");
                d.setAuthorizeLimitAmt(b);
            }
            else{
                BigDecimal b = new BigDecimal("10000");
                d.setAuthorizeLimitAmt(d.getAuthorizeLimitAmt().multiply(b));
            }
            rskAuthorizeMngMapper.insert(d);
        }
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
    public int delete(RskAuthorityManageDto dto) {
        return rskAuthorizeMngMapper.deleteByPrimaryKey(dto.getSeqNo());
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
    public RskAuthorityManageDto selectBySecNo(RskAuthorityManageDto dto) throws IllegalAccessException, InvocationTargetException {
        RskAuthorityManageDto entity = new RskAuthorityManageDto();
        RskAuthorizeMng rskAuthorizeMng = rskAuthorizeMngMapper.selectByPrimaryKey(dto.getSeqNo());
        if(null != rskAuthorizeMng){
            if(StringUtil.equals(rskAuthorizeMng.getAuthorizeType(), DictCodeList.授权类型_授权.getValue())){
                BigDecimal b = new BigDecimal("10000");
                rskAuthorizeMng.setAuthorizeLimitAmt(rskAuthorizeMng.getAuthorizeLimitAmt().divide(b));
            }
            else{
                rskAuthorizeMng.setAuthorizeLimitAmt(null);
            }
            BeanUtil.copyProperties(entity,rskAuthorizeMng);
            return entity;
        }
        
        return null;
    }
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     *//*
    public List<RskAuthorityManageDto> selectByDto(RskAuthorityManageDto dto) {
        return rskAuthorityManageMapper.selectByDto(dto);
    }*/
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param userName
     * @return
     * @see
     * @since 1.0
     */
    public List<RskAuthorityManageDto> getDtoByUserName(Map<String,Object> map) {
        return rskAuthorityManageMapper.getDtoByUserName(map);
    }
    /**
     * 查询流程中的数据
     * <功能详细描述>
     * @param parameter 函数输入参数
     * @return 
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public List<RskAuthorityManageDto> getBusinessData(WflValidInputParameter parameter) {
      //查询流程中数据"01"
        parameter.setSeachMode("01");
        List<RskAuthorityManageDto> list = rskAuthorityManageMapper.getBusinessData(parameter);
        return list;
    }

    /**
     * 检查是否有流程中的资产交易数据
     * @param param Map<String, Object>
     * @return Integer
     * @see
     * @since 1.0
     */
    public Integer selectAssetTrade(Map<String, Object> param) {
        return rskAuthorityManageMapper.selectAssetTrade(param)==null?0:rskAuthorityManageMapper.selectAssetTrade(param).size();
    }
    /**
     * 查询已授权用户
     * @param page
     * @param length
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
	public Page<RskAuthorityManageDto> getExistUser(int page, int length, Map<String, Object> param) {
		return this.queryForPageForSelectReturn(page, length, ()->rskAuthorityManageMapper.getExistUser(param));
	}
	public List<RskAuthorityManageDto> selectByUserName(String userName) {
		return rskAuthorityManageMapper.selectByUserName(userName);
	}
}
