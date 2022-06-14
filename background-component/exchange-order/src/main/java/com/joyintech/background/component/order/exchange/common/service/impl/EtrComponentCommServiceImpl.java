/*
 * 文件名：EtrComponentCommServiceImpl.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.order.exchange.common.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.common.pack.common.security.UserUtil;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.DateUtil;
import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.constant.LocalConstant;
import com.joyintech.background.component.order.exchange.common.constant.ProcessUpdateType;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrDealDto;
import com.joyintech.background.component.order.exchange.common.exception.EtrComponentException;
import com.joyintech.background.component.order.exchange.common.service.EtrComponentCommService;
import com.joyintech.background.component.order.exchange.common.service.EtrSubComponentService;
import com.joyintech.background.component.order.exchange.common.util.EtrComponentUtil;
import com.joyintech.background.component.order.exchange.database.dao.CmpComponentCommMapper;
import com.joyintech.background.component.order.exchange.database.entity.FinishStatusComponent;

/**
 * 投资指令组件实现类
 * 
 * @author zhangrui1
 * @version 1.0 2020年7月8日
 * @see EtrComponentCommServiceImpl
 * @since 1.0
 */
class EtrComponentCommServiceImpl<V extends EtrBaseDto> implements EtrComponentCommService<V> {

    /**
     * 错误信息-参数配置项为空或者用户信息为空
     */
    public static final String ERR_MSG_STTEINGS_NULL = "参数配置项为空或者用户信息为空";

    private EtrSubComponentService<V> subComponentService;

    private CmpComponentCommMapper commonMapper;

    private Validator validator;

    /**
     * 参数配置项
     */
    private ThreadLocal<EtrParameterDto> parameter = new ThreadLocal<>();

    /**
     * 后台日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 投资指令组件-指令状态更新
     * 
     * @param dto 指令信息
     * @param status 指令状态
     * @return 更新结果
     */
    protected EtrBaseDto updateEntrustStatus(EtrBaseDto dto, DictCodeList status) {
        // 指令信息不能为NULL
        if (dto == null) {
            logger.error("------空的指令信息------");
            throw new NullPointerException();
        }
        // 校验参数配置项
        if (!checkSettings()) {
            logger.error("------参数配置项缺失，指令代码[{}]------", dto.getEntrustId());
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        EtrComponentUtil.setBaseInfo(dto, getParameter().getUser());
        // 指令状态
        dto.setEntrustStatus(status.getValue());
        // 子组件更新指令状态
        int res = commonMapper.updateEntrustStatus(dto);
        // 更新失败时
        if (res == 0) {
            logger.error("------更新异常，指令代码[{}]、更细时间[{}]------", dto.getEntrustId(),
                dto.getOldUpdateTime());
            throw new EtrComponentException("当前数据已经被其他人处理，请重新查询后处理。");
        }
        // 原更新时间
        dto.setOldUpdateTime(dto.getUpdateTime());
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public V entrustNew(V dto) {
        // 校验是否设置用户信息
        if (!checkSettings()) {
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        // 设置数据操作信息
        EtrComponentUtil.setBaseInfo(dto, getParameter().getUser());
        return subComponentService.entrustNew(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public V entrustEdit(V dto) {
        // 校验是否设置用户信息
        if (!checkSettings()) {
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        // 设置数据操作信息
        EtrComponentUtil.setBaseInfo(dto, getParameter().getUser());
        return subComponentService.entrustEdit(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public V entrustDel(V dto) {
        // 校验是否设置用户信息
        if (!checkSettings()) {
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        // 设置数据操作信息
        EtrComponentUtil.setBaseInfo(dto, getParameter().getUser());
        return subComponentService.entrustDel(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public EtrBaseDto entrustBack(EtrBaseDto dto) {
        // 更新指令状态-已退回
        EtrBaseDto resDto = updateEntrustStatus(dto, DictCodeList.指令状态_已退回);
        // 接收该委托的交易员的接收状态设为已退回
        commonMapper.updateDistribute(dto);
        return resDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public EtrBaseDto entrustClose(EtrBaseDto dto) {
        // 更新指令状态-已关闭
        return updateEntrustStatus(dto, DictCodeList.指令状态_已关闭);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public EtrBaseDto entrustRevoke(EtrBaseDto dto) {
        // 更新指令状态-已撤回
        return updateEntrustStatus(dto, DictCodeList.指令状态_已撤回);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public EtrBaseDto entrustReceive(EtrBaseDto dto) {
        // 更新指令状态-已接收
        EtrBaseDto resDto = updateEntrustStatus(dto, DictCodeList.指令状态_已接收);
        // 将指令接收数据保存到临时表
        String entrustId = resDto.getEntrustId();
        commonMapper.deleteDistributeTemp(entrustId);
        commonMapper.insertDistributeTemp(entrustId);
        // 接收该委托的交易员的接收状态设为已接收
        commonMapper.updateDistribute(resDto);
        // 其他交易员的委托数据删除
        commonMapper.deleteDistribute(resDto);
        return resDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public EtrBaseDto entrustReceiveCancel(EtrBaseDto dto) {
        // 更新指令状态-待接收
        EtrBaseDto resDto = updateEntrustStatus(dto, DictCodeList.指令状态_待接收);
        String entrustId = resDto.getEntrustId();
        // 删除已接收数据
        EtrBaseDto pDto = new EtrBaseDto();
        pDto.setEntrustId(entrustId);
        commonMapper.deleteDistribute(pDto);
        // 恢复分发数据
        commonMapper.insertDistribute(entrustId);
        return resDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public V entrustSelect(String entrustId) {
        if (entrustId == null) {
            return null;
        }
        return subComponentService.entrustSelect(entrustId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BindingResult entrustCheck(V dto, BindingResult result) {
        // 字段check
        validator.validate(dto, result);
        if (result.hasErrors()) {
            return result;
        }
        if (!checkSettings()) {
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        // 指令开始日期<=指令结束日期
        if (DateUtil.compareDate(dto.getVdate(), dto.getMdate()) == 1) {
            String objectName = dto.getClass().getSimpleName();
            result.addError(error(objectName, LocalConstant.VDATE, "ETR_COMPONENT_ERR_001",
                getField(objectName, LocalConstant.VDATE),
                getField(objectName, LocalConstant.MDATE)));
        }
        // 子组件check
        subComponentService.entrustCheck(dto, result);
        return result;
    }

    /**
     * 校验参数配置项及用户信息
     * 
     * @return false:校验失败
     */
    protected boolean checkSettings() {
        EtrParameterDto dto = getParameter();
        // 参数配置项为空、或者用户信息为空
        if (dto == null || dto.getUser() == null) {
            return false;
        }
        User user = dto.getUser();
        // 用户名为空
        return user.getUserName() != null && !user.getUserName().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void entrustProcessUpdate(List<EtrDealDto> dtoList, ProcessUpdateType type) {
        // 校验参数配置项
        if (!checkSettings()) {
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        // 取出用户信息
        User user = getParameter().getUser();
        // 取消的场合
        if (ProcessUpdateType.DELETE == type) {
            for (EtrDealDto dto : dtoList) {
                commonMapper.deleteDeal(dto);
            }
        }
        // 新增的场合
        else {
            for (EtrDealDto dto : dtoList) {
                commonMapper.deleteDeal(dto);
                commonMapper.insertDeal(getFinishStatusDto(dto, user));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<EtrDealDto> entrustDealSelect(String entrustId) {
        return commonMapper.seleteDeal(entrustId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal entrustProcessSelect(String entrustId) {
        if (entrustId == null || entrustId.isEmpty()) {
            return null;
        }
        return commonMapper.selectEtrProcess(entrustId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateSettings(EtrParameterDto dto) {
        parameter.set(dto);
        if (!checkSettings()) {
            throw new EtrComponentException(ERR_MSG_STTEINGS_NULL);
        }
        UserUtil.bindUser(dto.getUser());
        subComponentService.settings(dto);
    }

    /**
     * 取得配置参数
     * 
     * @return 参数配置项
     */
    protected EtrParameterDto getParameter() {
        return parameter.get();
    }

    public void unload() {
        parameter.remove();
    }

    protected FinishStatusComponent getFinishStatusDto(EtrDealDto dto, User user) {
        FinishStatusComponent finishDto = new FinishStatusComponent();
        BeanUtil.copyPropertiesIgnoreNull(finishDto, dto);
        EtrComponentUtil.setBaseInfo(finishDto, user);
        return finishDto;
    }

    /**
     * 设置 subComponentService
     * 
     * @param subComponentService subComponentService
     */
    public void setSubComponentService(EtrSubComponentService<V> subComponentService) {
        this.subComponentService = subComponentService;
    }

    /**
     * 设置 commonMapper
     * 
     * @param commonMapper commonMapper
     */
    public void setCommonMapper(CmpComponentCommMapper commonMapper) {
        this.commonMapper = commonMapper;
    }

    /**
     * 设置 validator
     * 
     * @param validator validator
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}
