/*
 * 文件名：EtrSubComponentService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.order.exchange.common.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.database.dao.CmpComponentCommMapper;
import com.joyintech.background.component.order.exchange.database.dao.CmpComponentMapper;
import com.joyintech.background.component.order.exchange.database.dao.EntrustAddComponentMapper;
import com.joyintech.background.component.order.exchange.database.dao.EntrustComponentMapper;
import com.joyintech.background.component.order.exchange.database.entity.EntrustAddComponent;
import com.joyintech.background.component.order.exchange.database.entity.EntrustComponent;

import java.util.Date;

/**
 * 子组件
 * 
 * @author zhangrui1
 * @version 1.0 2020年7月9日
 * @see EtrSubComponentService
 * @since 1.0
 */
public abstract class EtrSubComponentService<V extends EtrBaseDto> implements EtrComponentCheckService<V>, EtrComponentService<V> {

    /**
     * 指令配置项
     */
    private ThreadLocal<EtrParameterDto> settings = new ThreadLocal<>();

    @Autowired
    private EntrustComponentMapper etrMapper;

    @Autowired
    private EntrustAddComponentMapper etrAddMapper;

    @Autowired
    private CmpComponentMapper<V> selectMapper;

    @Autowired
    private CmpComponentCommMapper commonMapper;

    /**
     * 更新指令配置项
     * 
     * @param dto 指令配置项
     */
    public void settings(EtrParameterDto dto) {
        settings.set(dto);
    }

    /**
     * 取得 指令配置项
     * 
     * @return 指令配置项
     */
    public EtrParameterDto settings() {
        return settings.get();
    }

    public void unload() {
        settings.remove();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public V entrustNew(V dto) {
        EntrustComponent etrEntity = new EntrustComponent();
        BeanUtil.copyPropertiesIgnoreNull(etrEntity, dto);
        // 设置指令状态待接收
        if(!StringUtil.equals(DictCodeList.指令状态_待分发.getValue(), etrEntity.getEntrustStatus())){
        	etrEntity.setEntrustStatus(DictCodeList.指令状态_待接收.getValue());
        }
        // 设置币种
        if (etrEntity.getCcy() == null || etrEntity.getCcy().isEmpty()) {
            etrEntity.setCcy(DictCodeList.币种_人民币.getValue());
        }
        EntrustAddComponent etrAddEntity = new EntrustAddComponent();
        BeanUtil.copyPropertiesIgnoreNull(etrAddEntity, dto);
        if (dto.getAddMdate() != null) {
            etrAddEntity.setMdate(dto.getAddMdate());
        }

        etrAddEntity.setEntrustCreateSource(DictCodeList.指令创建来源_场内实时.getValue());
        if (StringUtil.isEmpty(etrAddEntity.getAssetType1())
            && StringUtil.isNotEmpty(etrEntity.getFinprodId())) {
            EntrustAddComponent info = commonMapper.selectAddProperty(etrEntity.getFinprodId());
            if (info != null) {
                etrAddEntity.setAssetType1(info.getAssetType1());
            }
        }
        // 登录指令表
        etrMapper.insertSelective(etrEntity);
        // 登录指令附属表
        if (etrAddEntity!= null&&etrAddEntity.getTransmitTime()==null) {
            etrAddEntity.setTransmitTime(new Date());
        }
        etrAddMapper.insertSelective(etrAddEntity);
        // 个别登录
        doInsert(dto);
        // 更新时间
        dto.setOldUpdateTime(dto.getUpdateTime());
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V entrustEdit(V dto) {
        EntrustComponent etrEntity = new EntrustComponent();
        BeanUtil.copyPropertiesIgnoreNull(etrEntity, dto);
        EntrustAddComponent etrAddEntity = new EntrustAddComponent();
        BeanUtil.copyPropertiesIgnoreNull(etrAddEntity, dto);
        etrAddEntity.setEntrustCreateSource(DictCodeList.指令创建来源_场内实时.getValue());
        if (StringUtil.isEmpty(etrAddEntity.getAssetType1())
            && StringUtil.isNotEmpty(etrEntity.getFinprodId())) {
            EntrustAddComponent info = commonMapper.selectAddProperty(etrEntity.getFinprodId());
            if (info != null) {
                etrAddEntity.setAssetType1(info.getAssetType1());
            }
        }

        if (dto.getAddMdate() != null) {
            etrAddEntity.setMdate(dto.getAddMdate());
        }

        // 更新指令表
        etrMapper.updateByPrimaryKeySelective(etrEntity);
        // 更新指令附属表
        etrAddMapper.updateByPrimaryKeySelective(etrAddEntity);
        // 个别更新
        doUpdate(dto);
        // 更新时间
        dto.setOldUpdateTime(dto.getUpdateTime());
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V entrustDel(V dto) {
        // 删除指令表
        etrMapper.deleteByPrimaryKey(dto.getEntrustId());
        // 删除指令附属表
        etrAddMapper.deleteByPrimaryKey(dto.getEntrustId());
        // 个别删除
        doDelete(dto);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V entrustSelect(String entrustId) {
        return selectMapper.select(entrustId);
    }

    /**
     * 执行个别登录
     * 
     * @param dto 指令信息
     */
    public void doInsert(V dto) {
    }

    /**
     * 执行个别更新
     * 
     * @param dto 指令信息
     */
    public void doUpdate(V dto) {
    }

    /**
     * 执行个别删除
     * 
     * @param dto 指令信息
     */
    public void doDelete(V dto) {
    }

    /**
     * 获取 commonMapper
     * 
     * @return commonMapper.
     */
    protected CmpComponentCommMapper getCommonMapper() {
        return commonMapper;
    }

    /**
     * 获取 etrMapper
     * 
     * @return etrMapper.
     */
    public EntrustComponentMapper getEtrMapper() {
        return etrMapper;
    }

    /**
     * 获取 etrAddMapper
     * 
     * @return etrAddMapper.
     */
    public EntrustAddComponentMapper getEtrAddMapper() {
        return etrAddMapper;
    }

    /**
     * 获取 selectMapper
     * 
     * @return selectMapper.
     */
    public CmpComponentMapper<V> getSelectMapper() {
        return selectMapper;
    }
}
