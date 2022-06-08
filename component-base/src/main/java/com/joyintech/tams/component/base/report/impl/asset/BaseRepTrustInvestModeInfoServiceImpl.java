/*
 * 文件名：RepOutInvestAgrInfoImpl.java
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

import com.joyintech.tams.common.base.dao.RepTrustInvestModeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.RepTrustInvestMode;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.component.base.report.asset.BaseRepTrustInvestModeInfoService;
import com.joyintech.tams.component.base.report.dto.BaseRepTrustInvestModeInfoDto;
import com.joyintech.tams.component.base.report.impl.dao.BaseRepTrustInvestModeInfoMapper;
import com.joyintech.tools.util.ListUtils;

/**
 * 委外投资-协议方式报送信息
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年8月29日
 * @see BaseRepTrustInvestModeInfoServiceImpl
 * @since 1.0
 */
@Service
public class BaseRepTrustInvestModeInfoServiceImpl extends BaseService<RepTrustInvestMode>implements BaseRepTrustInvestModeInfoService{
    /**
     * baseRepTrustInvestModeInfoMapper
     */
    @Autowired
    private BaseRepTrustInvestModeInfoMapper baseRepTrustInvestModeInfoMapper;
    /**
     * baseRepTrustInvestModeInfoServiceImpl
     */
    @Autowired
    private BaseRepTrustInvestModeInfoServiceImpl baseRepTrustInvestModeInfoServiceImpl;
    /**
     * 委外投资—协议方式mapper
     */
    @Autowired
    private RepTrustInvestModeMapper repTrustInvestModeMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(BaseRepTrustInvestModeInfoDto dto) {
        return baseRepTrustInvestModeInfoServiceImpl.delete(dto) && baseRepTrustInvestModeInfoServiceImpl.insert(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insert(BaseRepTrustInvestModeInfoDto dto) {
        List<RepTrustInvestMode> repTrustInvestModeList = dto.getRepTrustInvestModeList();
        Integer i = 0;
        if(repTrustInvestModeList != null && repTrustInvestModeList.size()>0){
            for(RepTrustInvestMode entity:repTrustInvestModeList){
                i+=repTrustInvestModeMapper.insert(entity);
            }
            return i==repTrustInvestModeList.size();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(BaseRepTrustInvestModeInfoDto dto) {
        List<String> repFinprodIdList = dto.getRepFinprodIdList();
        int i = 0;
        if(repFinprodIdList!=null&& repFinprodIdList.size()>0){
            for(String repFinprodId:repFinprodIdList){
                i+=repTrustInvestModeMapper.deleteByPrimaryKey(repFinprodId);
            }
            return i==repFinprodIdList.size();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RepTrustInvestMode> search(BaseRepTrustInvestModeInfoDto dto) {
        List<String> repFinprodIdList = dto.getRepFinprodIdList();
        List<List<String>> list= ListUtils.splitList(repFinprodIdList);
        List<RepTrustInvestMode> repTrustInvestModeList = new ArrayList<RepTrustInvestMode>();
        if(list!=null){
            for(List<String> idList:list){
                repTrustInvestModeList.addAll(baseRepTrustInvestModeInfoMapper.search(idList));
            }
        }
        return repTrustInvestModeList;
    }
    
}
