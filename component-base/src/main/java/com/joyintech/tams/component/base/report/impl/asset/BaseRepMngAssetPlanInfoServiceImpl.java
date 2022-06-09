/*
 * 文件名：RepAssetMngProInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.base.report.impl.asset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.tams.common.base.dao.RepAssetMngPlanMapper;
import com.joyintech.tams.common.base.entity.RepAssetMngPlan;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.component.base.report.asset.BaseRepMngAssetPlanInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepMngAssetPlanInfoDto;
import com.joyintech.tams.component.base.report.impl.dao.BaseRepMngAssetPlanInfoMapper;
import com.joyintech.tools.util.ListUtils;

/**
 * 资产管理产品中债报送信息组件
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年8月28日
 * @see RepAssetMngProInfoImpl
 * @since 1.0
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BaseRepMngAssetPlanInfoServiceImpl extends BaseService<RepAssetMngPlan>implements BaseRepMngAssetPlanInfoService{
    
    /**
     * baseRepMngAssetPlanInfoMapper
     */
    @Autowired
    private BaseRepMngAssetPlanInfoMapper baseRepMngAssetPlanInfoMapper;
    /**
     * baseRepMngAssetPlanInfoServiceImpl
     */
    @Autowired
    private BaseRepMngAssetPlanInfoServiceImpl baseRepMngAssetPlanInfoServiceImpl;
    @Autowired
    private RepAssetMngPlanMapper repAssetMngPlanMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(BaseRepMngAssetPlanInfoDto dto) {
        return baseRepMngAssetPlanInfoServiceImpl.delete(dto) && baseRepMngAssetPlanInfoServiceImpl.insert(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insert(BaseRepMngAssetPlanInfoDto dto) {
        List<RepAssetMngPlan> repAssetMngPlanList = dto.getRepAssetMngPlanList();
        Integer i = 0;
        if(repAssetMngPlanList != null && repAssetMngPlanList.size()>0){
            for(RepAssetMngPlan entity:repAssetMngPlanList){
                i+=repAssetMngPlanMapper.insert(entity);
            }
            return i==repAssetMngPlanList.size();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(BaseRepMngAssetPlanInfoDto dto) {
        List<String> repFinprodIdList = dto.getRepFinprodIdList();
        Integer i = 0;
        if(repFinprodIdList != null && repFinprodIdList.size()>0){
            for(String repFinprodId:repFinprodIdList){
                i+=repAssetMngPlanMapper.deleteByPrimaryKey(repFinprodId);
            }
            return i==repFinprodIdList.size();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RepAssetMngPlan> search(BaseRepMngAssetPlanInfoDto dto) {
        List<String> repFinprodIdList = dto.getRepFinprodIdList();
        List<List<String>> list= ListUtils.splitList(repFinprodIdList);
        List<RepAssetMngPlan> repAssetMngPlanList = new ArrayList<RepAssetMngPlan>();
        if(list!=null){
            for(List<String> idList:list){
                repAssetMngPlanList.addAll(baseRepMngAssetPlanInfoMapper.search(idList));
            }
        }
        return repAssetMngPlanList;
    }
    
    
}
