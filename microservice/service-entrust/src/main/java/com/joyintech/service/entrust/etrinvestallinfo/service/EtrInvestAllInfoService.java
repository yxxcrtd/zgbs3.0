/*
 * 文件名：EtrInvestAllInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrinvestallinfo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.service.entrust.etrinvestallinfo.dto.EtrInvestAllInfoExportPdfDto;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.common.service.EntrustCommonPersonSelectService;
import com.joyintech.service.entrust.etrinvestallinfo.dao.EtrInvestAllInfoMapper;
import com.joyintech.service.entrust.etrinvestallinfo.dto.EtrInvestAllInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 所有投资委托信息微服务service
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2018年9月10日
 * @see EtrInvestAllInfoService
 * @since 1.0
 */
@Service("backEtrInvestAllInfoService")
public class EtrInvestAllInfoService extends BaseService<EtrInvestAllInfoDto> {
    /**
     * 自定义mapper
     */
    @Autowired
    private EtrInvestAllInfoMapper etrInvestAllInfoMapper;
    
    @Autowired
    private EntrustCommonPersonSelectService entrustCommonPersonSelectService;
    /**
     * 
     * 获取组合代码的联想文本框
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getentrustId(String entrustId){
        return etrInvestAllInfoMapper.getentrustId(entrustId);
    }
    
    /**
     * 
     * 获取资产编号的联想文本框
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getfinprodId(String finprodId){
        //wangyang1 2020年7月15日 start
        List<SuggestDto> list=etrInvestAllInfoMapper.getfinprodIdPub(finprodId);
        list.addAll(etrInvestAllInfoMapper.getfinprodId(finprodId));
        //wangyang1 2020年7月15日 end
        return list; 
    }
    
    /**
     * 
     * 所有投资委托信息查询
     * <功能详细描述>
     * @param param
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public Page<EtrInvestAllInfoDto> selectForPage(int page, int length,Map<String, Object> param)
    {
        param.put("ASSET_TYPE_07", DictCodeList.平安资产类型_拆借.getValue());
        param.put("ASSET_TYPE_08", DictCodeList.平安资产类型_质押式回购.getValue());
        param.put("ASSET_TYPE_10", DictCodeList.平安资产类型_买断式回购.getValue());
        param.put("ASSET_TYPE_12", DictCodeListExt2.平安资产类型_债券借贷.getValue());
        param.put("ASSET_TYPE_16", DictCodeListExt2.平安资产类型_利率互换.getValue());
        param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        param.put("assetBond", DictCodeList.平安资产类型_债券.getValue());
        param.put("ENTRUST_CLASS_02", DictCodeListExt3.投资指令类型_多组合单资产指令.getValue());
        String tradePlat =(String)param.get("tradePlat");
        if(StringUtil.isNotEmpty(tradePlat) && StringUtil.equals(tradePlat, DictCodeList.清算速度_T_0.getValue())){
            param.put("tradePlat", 0);
        }else if(StringUtil.isNotEmpty(tradePlat) && StringUtil.equals(tradePlat, DictCodeList.清算速度_T_1.getValue())){
            param.put("tradePlat", 1);
        }
        
        String portfolioId = (String)param.get("portfolioId");
        if(StringUtil.isNotEmpty(portfolioId)){
            param.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA,portfolioId));
        }
         //勾选导出
        if (StringUtil.isNotEmpty(param.get("selectedItem"))){     
            List<String> selectedItem = StringUtil.splitToList(",",(String)param.get("selectedItem"));           
            if(selectedItem.size() > 0){
                param.put("etridList",selectedItem);
            }
        }
        Page<EtrInvestAllInfoDto> pageDto = super.queryForPage(page, length, () -> etrInvestAllInfoMapper.select(param));
        List<EtrInvestAllInfoDto> dtoList = pageDto.getRoot();
        dtoList=entrustCommonPersonSelectService.getRealName2(dtoList);
        pageDto.setRoot(dtoList);
        return pageDto;
    }

    public EtrInvestAllInfoDto getById(Map<String, Object> map){
        EtrInvestAllInfoDto dto = etrInvestAllInfoMapper.getById(map);
        return dto;
    }

    /**
     * 
     * 查询PB导出信息 add by caoxue 2019-12-31
     * <功能详细描述>
     * @param param
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
    public List<EtrInvestAllInfoDto> selectExportPB(Map<String, Object> param)
    {
        param.put("ASSET_TYPE_08", DictCodeList.平安资产类型_质押式回购.getValue());
        param.put("assetBond", DictCodeList.平安资产类型_债券.getValue());
       
        //勾选导出
        if (StringUtil.isNotEmpty(param.get("selectedItem"))){     
            List<String> selectedItem = StringUtil.splitToList(",",(String)param.get("selectedItem"));           
            if(selectedItem.size() > 0){
                param.put("etridList",selectedItem);
            }
        }
        //变更PB是否导出状态
        etrInvestAllInfoMapper.updateExportPB(param);
        return etrInvestAllInfoMapper.selectExportPB(param);
    }

    /**
     * 查询导出pdf数据 add by zengchengfa
     * @param param
     * @return
     * @throws Exception
     */
    public List<EtrInvestAllInfoExportPdfDto> selectExportPdf(Map<String, Object> param) throws Exception{
        if (StringUtil.isNotEmpty(param.get("selectedItem"))){
            List<String> selectedItem = StringUtil.splitToList(",",(String)param.get("selectedItem"));
            if(selectedItem.size() > 0){
                param.put("etrList",selectedItem);
            }
        }
        param.put("assetBond", DictCodeList.平安资产类型_债券.getValue());
        return etrInvestAllInfoMapper.selectExportPdf(param);
    }
}
