/*
 * 文件名：MstTradeOpponent001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年8月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.basicdata.msttradeopponent.service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.dao.PaFinProductMapper;
import com.joyintech.tams.common.base.dao.RepAssetMngPlanMapper;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.msttradeopponent.dao.BasicMstTradeOpponent001Mapper;
import com.joyintech.service.basicdata.msttradeopponent.dto.MstTradeOpponentDto;

/**
 * 交易对手管理Service
 * @author fanshuanghu
 * @version 1.0 2018年8月20日
 * @see MstTradeOpponent001Service
 * @since 1.0
 */
@Service("backMstTradeOpponent001Service")
public class MstTradeOpponent001Service extends BaseService<MstTradeOpponentDto>{
    /**
     * 注入实体Mapper
     */
    @Autowired
    private BasicMstTradeOpponent001Mapper basicMstTradeOpponent001Mapper;
    
    @Autowired
    private FinProductMapper finProductMapper;
    
    @Autowired
    private FinProductTypeMapper finProductTypeMapper;
    
    @Autowired
    private RepAssetMngPlanMapper repAssetMngPlanMapper;
    
    @Autowired
    private PaFinProductMapper paFinProductMapper;
    
    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;
    /**
     * 获取交易对手类型
     * @param map 查询条件
     * @return 下拉框实体类集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCounterType(Map<String, Object> map){
        return basicMstTradeOpponent001Mapper.queryCounterType(map);
    }
    /**
     * 分页查询
     * @param pageParam 分页查询条件
     * @return page实体类
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<MstTradeOpponentDto> selectForPage(int page,int length,Map<String, Object> pageParam)
    {
        return super.queryForPage(page,length,() -> selectAllForPage(pageParam));
    }
    
    //分页查询 获取交易对手
    private List<MstTradeOpponentDto> selectAllForPage(Map<String, Object> param) {
    	List<MstTradeOpponentDto> list = basicMstTradeOpponent001Mapper.queryForPage(param);
    	if(list!=null && list.size()>0) {
    		for(MstTradeOpponentDto dto : list) {
    			if(dto.getCounterId()!=null && dto.getCounterId().contains("F16_")) {
    				dto.setCounterId(dto.getCounterId().replace("F16_", ""));
    			}
    		}
    	}
    	return list;
    }
    
    /**
     * 获取账号信息
     * @see
     * @since 1.0
     * @return 账号实体类
     */
    public List<SelectOptionDto> getAccount(Map<String,Object> map){
        return basicMstTradeOpponent001Mapper.getAccount(map);
    }
    /**
     * 獲取所屬總行
     * @param param 請求參數
     * @return
     * @see
     * @since 1.0
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public List<SelectOptionDto> getOfficeId(String param){
        return basicMstTradeOpponent001Mapper.getOfficeId(param);
    }
    
    public Page<SelectOptionDto> getMarketId(Map<String, Object> map, int page, int length){
    	return this.queryForPageForSelectReturn(page, length,()->basicMstTradeOpponent001Mapper.getMarketId(map));
    }
    /**
     * 获取费用品种
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getFeeId(Map<String,Object> map){
        return basicMstTradeOpponent001Mapper.getFeeId(map);
    }
    /**
     * 获取账号类型
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getAccountType(){
        return basicMstTradeOpponent001Mapper.getAccountType();
    }
    public List<SelectOptionDto> getAccountTypeInfo(){
        return basicMstTradeOpponent001Mapper.getAccountTypeInfo();
    }
    
    public List<SelectOptionDto> getCustomerIdMarket(String param){
        return basicMstTradeOpponent001Mapper.getCustomerIdMarket(param);
    }
    /**
     * 获取管理人
     * @return 下拉框实体类
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCustomerId(String param){
        return basicMstTradeOpponent001Mapper.getCustomerId(param);
    }
    
    public List<SuggestDto> getCounterName(String param){
        return basicMstTradeOpponent001Mapper.getCounterName(param);
    }
    
    public List<SelectOptionDto> getCounterId(String param){
        return basicMstTradeOpponent001Mapper.getCounterId(param);
    }
    /**
     * 获取托管人
     * @return 下拉框实体类
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCustomerId1(String param){
        return basicMstTradeOpponent001Mapper.getCustomerId1(param);
    }
//    /**
//     * 导出 {@inheritDoc}
//     */
//    @Override
//    protected void buildExportParam(ExportParam exportParam) {
//        Map<String, Object> map = exportParam.getSelectParam();
//        exportParam.config(
//            "/com/joyintech/basicdata/msttradeopponent/conf/export_conf.xml").select(
//                () -> mstTradeOpponent001Mapper.queryForPage(map)).dataSource(
//                    DataSourceType.MASERT);
//    }
    /**
     * 工作流
     * {@inheritDoc}
     */
    public void doComplete(MstTradeOpponentDto dto, String type) {
        if (StringUtil.equals(type, "DELETE")) {
            basicMstTradeOpponent001Mapper.deleteCounterAcct(dto);
            
            
//            mstTradeOpponent001Mapper.deleteMstCounterParty(dto);
            mstCounterPartyMapper.deleteByPrimaryKey(dto.getCounterId());
            if(DictCodeListExt1.交易对手类型_通道计划.getValue().equals(dto.getCounterType())){
                basicMstTradeOpponent001Mapper.deleteFinProductAcct(dto);
                basicMstTradeOpponent001Mapper.deleteFinCashCalcRule(dto);
                basicMstTradeOpponent001Mapper.deleteFinCashInfo(dto);
                basicMstTradeOpponent001Mapper.deleteFinCashPlan(dto);
                
//                mstTradeOpponent001Mapper.deleteFinProduct(dto);
                finProductMapper.deleteByPrimaryKey(dto.getFinprodId());
//                mstTradeOpponent001Mapper.deleteRepAssetMngPlan(dto);
                repAssetMngPlanMapper.deleteByPrimaryKey(dto.getFinprodId());
                FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
                finProductTypeKey.setBranch(dto.getBranch());
                finProductTypeMapper.deleteByPrimaryKey(finProductTypeKey);
//                mstTradeOpponent001Mapper.deleteFinProductType(dto);
                
                if(StringUtil.isNotEmpty(dto.getBankTrustCode())){
//                    mstTradeOpponent001Mapper.deletePaFinProduct(dto);
                    paFinProductMapper.deleteByPrimaryKey(dto.getFinprodId());
                }
            }
            
        }
    }
    /**
     * 统计
     * <功能详细描述>
     * @param counterId id
     * @return 统计结果
     * @see
     * @since 1.0
     */
    public int selectCounter(String counterId) {
        return basicMstTradeOpponent001Mapper.selectCounter(counterId);
    }
    /**
     * 统计金融产品
     * <功能详细描述>
     * @param dto 参数
     * @return 统计结果
     * @see
     * @since 1.0
     */
    public int selectFinProduct(MstTradeOpponentDto dto) {
        return basicMstTradeOpponent001Mapper.selectFinProduct(dto);
    }
    
    public StlCapacctInfo getCctInfo(Map<String,Object> map){
        return basicMstTradeOpponent001Mapper.getCctInfo(map);
        
    }

    /**
     * 统计 当交易对手类别是分行时，是否被债券投后责任机构使用
     * <功能详细描述>
     * @param counterId id
     * @return 统计结果
     * @see
     * @since 1.0
     */
    public int selectFHCounter(String counterId) {
        return basicMstTradeOpponent001Mapper.selectFHCounter(counterId);
    }
    public List<MstTradeOpponentDto> selectCounterIdByCustomer(Map<String,Object> map){
        return basicMstTradeOpponent001Mapper.selectCounterIdByCustomer(map);
    }

    /**
     * 获取银行间机构21位码
     * @param param
     * @return
     */
    public Page<SelectOptionDto> selectPartyId(Map<String, Object> param, int page, int length) {
        return this.queryForPageForSelectReturn(page, length,()->basicMstTradeOpponent001Mapper.selectPartyId(param));
    }
}
