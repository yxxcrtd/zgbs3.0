/*
 * 文件名：EtrIbBondinfo003Controller.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月29日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.service.entrust.etribbondinfo.controller;

/**
 * 多组合
 * @author 王波
 * @date 2021年01月29日
 * @see EtrIbBondinfo003Controller
 * @since 1.0
 */

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etribbondinfo.dto.EtrIbBondinfo002Dto;
import com.joyintech.service.entrust.etribbondinfo.dto.EtribBondInfo003Dto;
import com.joyintech.service.entrust.etribbondinfo.service.EtrIbBondinfo003Service;
import com.joyintech.tams.common.base.dao.WflEtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.WflEtrEntrustMapper;
import com.joyintech.tams.common.base.entity.WflEtrEntrustAddKey;
import com.joyintech.tams.common.base.entity.WflEtrEntrustKey;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 债券投资委托微服务controller
 */
@RestController("etrIbBondinfo003Controllere_dtx")
@RequestMapping("EtrIbBondinfo003")
public class EtrIbBondinfo003Controller {

    @Autowired
    private EtrIbBondinfo003Service etrIbBondinfo003Service;



    /**
     * 工作流完成方法 <功能详细描述>
     *
     * @param entity 实体类
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public void doComplete(@RequestBody EtribBondInfo003Dto entity)
            throws IllegalAccessException, InvocationTargetException {
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos()))  {
            for (EtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", dto.getEntrustId());
                etrIbBondinfo003Service.doDeleteEtrFlow(param);
            }
        }
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
        for (EtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
            etrIbBondinfo003Service.doComplete(dto, entity.getOperateType());
        }
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doInsert")
    public void doInsert(@RequestBody EtribBondInfo003Dto entity){
        if ("submit".equals(entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos())) {
                for (EtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
                    dto.setWorkflowId(entity.getWorkflowId());
                    WFlowCommonService.doInsertFlow(entity.getOperateType(), etrIbBondinfo003Service.initBaseEtrInvestInfoDto(dto));
                }
            }
        }
        
        etrIbBondinfo003Service.doInsert(entity, entity.getOperateType());
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doUpdate")
    public void doUpdate(@RequestBody EtribBondInfo003Dto entity){
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            for (EtribBondInfo003Dto dto : entity.getEtrInvestBondInfoDtos()) {
                WFlowCommonService.doUpdateFlow(entity.getOperateType(), etrIbBondinfo003Service.initBaseEtrInvestInfoDto(dto));
            }
        }
    }
    
    @Transactional
    @DTXTransaction
    @PostMapping("doBack")
    public void doBack(@RequestBody EtribBondInfo003Dto entity){
        if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondInfoDtos()))  {
            for (EtribBondInfo003Dto dto: entity.getEtrInvestBondInfoDtos()) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", dto.getEntrustId());
                etrIbBondinfo003Service.doDeleteEtrFlow(param);
            }
        }
    }
    
    @Transactional
    @DTXTransaction
    @PostMapping("doDeleteEtrFlow")
    public boolean doDeleteEtrFlow(@RequestBody
                                           Map<String,Object> param){
       return etrIbBondinfo003Service.doDeleteEtrFlow(param);
    }
}
