/*
 * 文件名：RepFundAssetInfoImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.base.report.impl.asset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.dao.RepFundMapper;
import com.joyintech.tams.common.base.entity.RepFund;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.component.base.report.asset.BaseRepFundAssetInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepFundAssetInfoDto;
import com.joyintech.tams.component.base.report.impl.dao.BaseRepFundAssetInfoMapper;
import com.joyintech.tools.util.ListUtils;

/**
 * 基金资产信息报送实现类
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年8月29日
 * @see RepFundAssetInfoImpl
 * @since 1.0
 */
@Service
public class BaseRepFundAssetInfoServiceImpl extends BaseService<RepFund> implements BaseRepFundAssetInfoService{
    
    /**
     * baseRepFundAssetInfoMapper
     */
    @Autowired
    private BaseRepFundAssetInfoMapper baseRepFundAssetInfoMapper;
    /**
     * baseRepFundAssetInfoServiceImpl
     */
    @Autowired
    private BaseRepFundAssetInfoServiceImpl baseRepFundAssetInfoServiceImpl;
    /**
     * RepFundMapper
     */
    @Autowired
    private RepFundMapper repFundMapper;
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(BaseRepFundAssetInfoDto dto) {
        return baseRepFundAssetInfoServiceImpl.delete(dto) & baseRepFundAssetInfoServiceImpl.insert(dto) ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insert(BaseRepFundAssetInfoDto dto) {
        List<RepFund> entityList = dto.getRepFundList();
        int i = 0;
        if(entityList!=null && entityList.size()>0){
            for(RepFund repFund:entityList){
                i+=repFundMapper.insert(repFund);
            }
            return i==entityList.size();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(BaseRepFundAssetInfoDto dto) {
        List<String> repFinprodIdList = dto.getRepFinprodIdList();
        int i = 0;
        for(String repFinprodId:repFinprodIdList){
            i+=repFundMapper.deleteByPrimaryKey(repFinprodId);
        }
        return i== repFinprodIdList.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RepFund> search(BaseRepFundAssetInfoDto dto) {
        List<String> repFinprodIdList = dto.getRepFinprodIdList();
        List<List<String>> list= ListUtils.splitList(repFinprodIdList);
        List<RepFund> repFundList = new ArrayList<RepFund>();
        if(list!=null){
            for(List<String> idList:list){
                dto.setRepFinprodIdList(idList);
                repFundList.addAll(baseRepFundAssetInfoMapper.search(dto));
            }
        }
        
        return repFundList;
    }
    

}
