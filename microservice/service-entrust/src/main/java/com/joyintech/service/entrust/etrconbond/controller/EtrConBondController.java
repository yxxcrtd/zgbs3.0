/*
 * 文件名：EtrBondFirstBiddingController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.entrust.etrconbond.controller;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.service.entrust.etrconbond.service.EtrConBondService;
import com.joyintech.service.entrust.etrconbond.service.PositionRequestEtrConBondService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustBackupMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_修改;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_新建;

/**
 * 可转债委托
 * @author gejiaao
 * @version 1.0 2019年10月24日
 * @see EtrConBondController
 * @since 1.0
 */
@RestController("EtrConBondCtr_dtx")
@RequestMapping("/EtrConBond")
public class EtrConBondController {
    
    @Autowired
    private EtrConBondService service;

    @Autowired
    private PositionRequestEtrConBondService positionRequestService;

    @Autowired
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private EtrEntrustBackupMapper etrEntrustBackupMapper;
    
    /**
     * 
     * 检索分页数据。返回类型是Service的泛型。
     * <功能详细描述>
     * @param pageParam int
     * @param page int
     * @param length int
     * @return Page<EtrFundDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @PostMapping("/findPage/{page}/{length}")
    public Page<EtrConBondDto> selectForPage(@RequestBody Map<String, Object> pageParam, @PathVariable("page") int page,
                                             @PathVariable("length") int length) {
        return service.selectForPage(page,length,pageParam);
    }

    /**
     * 获取资产代码联想文本框信息
     * @return 
     * @throws Exception
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId/{page}/{length}")
    public Page<SelectOptionDto> getFinprodId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return service.getFinprodId(page, length, param);
    }
    /**
     * 获取新建界面的资产代码联想下拉框框
     * @param
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @PostMapping("getFinprodIdForAdd/{page}/{length}")
    public Page<SelectOptionDto> getFinprodIdForAdd(@RequestBody Map<String, Object> param,@PathVariable("page") int page,
                                                    @PathVariable("length") int length)
    {
        return service.getFinprodIdForAdd(page,length,param);
    }
    
    /**
     * 操作前校验
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("check")
    public List<ValidMessage> check(@RequestBody EtrConBondDto dto){
        return service.check(dto);
    }
    /**
     * 撤回委托 
     * <功能详细描述>
     * @param dto 实体类
     * @return boolean
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("recall")
    public boolean recallDelegate(@RequestBody EtrConBondDto dto) throws IllegalAccessException, InvocationTargetException{
        boolean result = service.recallDelegate(dto);
        positionRequestService.sendRevoke(dto);
        return result;
    }
    /**
     * 关闭委托
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("close")
    public boolean closeDelegate(String entrustId,String closeWay) {
        boolean result = service.closeDelegate(entrustId, closeWay);
        positionRequestService.sendClose(entrustId, closeWay);
        return result;
    }
    /**
     * 通过主键查询数据
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    @GetMapping("selectByKey")
    public EtrConBondDto selectByKey(String entrustId) {
        return service.selectByKey(entrustId);
    }
    /**
     * 工作流完成方法
     * <功能详细描述>
     * @param
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @PostMapping("doComplete")
    public void doComplete(@RequestBody EtrConBondDto dto) throws IllegalAccessException, InvocationTargetException{
        WFlowCommonService.doCompleteFlow(dto.getWorkflowId());
        service.doComplete(dto, dto.getOperateType());
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doInsert")
    public void doInsert(@RequestBody EtrConBondDto dto){
        //保存不处理
        if ("save".equals(dto.getOperateType())) {
            return;
        }
        WFlowCommonService.doInsertFlow(dto.getOperateType(), service.initBaseDto(dto));
        service.doInsert(dto, dto.getOperateType());

        String decisionAct = DictCodeListExt3.系统动作_新建.getValue();
        String systemAct = DictCodeListExt3.系统动作_新建.getValue();
        if (StringUtil.equals(dto.getOperateType(), "modify")) {
            systemAct = DictCodeListExt3.系统动作_修改.getValue();
        }
        positionRequestService.sendEtrConBondDto(dto,decisionAct,systemAct);
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doUpdate")
    public void doUpdate(@RequestBody EtrConBondDto dto){
        WFlowCommonService.doUpdateFlow(dto.getOperateType(), service.initBaseDto(dto));
        
        service.doInsert(dto, dto.getOperateType());

//        String decisionAct = DictCodeListExt3.系统动作_新建.getValue();
//        String systemAct = DictCodeListExt3.系统动作_新建.getValue();
//        SysWflBusinessList sysBus=sysWflBusinessListMapper.selectByPrimaryKey(dto.getWorkflowId());
//        if(null!=sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())){
//            decisionAct = DictCodeListExt3.系统动作_修改.getValue();
//        }
//        if (StringUtil.equals(dto.getOperateType(), "modify")) {
//            systemAct = DictCodeListExt3.系统动作_修改.getValue();
//        }
////        dto.setSoftSkipMode("Y");
//        positionRequestService.sendEtrConBondDto(dto,decisionAct,systemAct);

        //保存不需要处理头寸
        if (!StringUtil.equals(dto.getOperateType(), "save")) {
            //增加调用头寸引擎代码start
            String decisionAct = 系统动作_修改.getValue();
            String systemAct = 系统动作_修改.getValue();
            EtrEntrust etrEntrust = etrEntrustBackupMapper.selectByPrimaryKey(dto.getEntrustId());
            SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(dto.getWorkflowId());
            if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                if (etrEntrust == null) {
                    systemAct = 系统动作_新建.getValue();
                    decisionAct = 系统动作_修改.getValue();
                }
            } else {
                if (etrEntrust == null) {
                    systemAct = 系统动作_新建.getValue();
                    decisionAct = 系统动作_新建.getValue();
                }
            }
            if (StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                dto.setSoftSkipMode("Y");
            }
            try {
                dto.setPositionSeqno(null);
                positionRequestService.sendEtrConBondDto(dto,decisionAct,systemAct);
            } catch (PositionException e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                if (StringUtil.isNotBlank(message.getMessage())) {
                    WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
                } else {
                    if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
                        BaseMap bm = (BaseMap) message.getData();
                        StringBuffer messageStr = (StringBuffer) bm.get("message");
                        WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                    }
                }
            }
            //增加调用头寸引擎代码end
        }
    }

    @Transactional
    @DTXTransaction
    @PostMapping("doBack")
    public void doBack(@RequestBody EtrConBondDto dto){
        WFlowCommonService.doBackFlow(dto.getWorkflowId(), dto.getEntrustId());
        positionRequestService.sendDelete(dto);
    }
    /**
     * 
     * 删除投资委托
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    @Transactional
    @DTXTransaction
    @GetMapping("delete")
    public boolean deleteDelegate(String entrustId) {
        return service.deleteDelegate(entrustId);
    }

    /**
     * 
     * 组合可用现金查询
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param) {
        return service.getProAvailableCash(param);
    }

    /**
     *验证子组合是否可以申购
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("checkChildPortfolio")
    public Boolean checkChildPortfolio(@RequestBody EtrConBondDto etrConBondDto) {
        return service.checkChildPortfolio(etrConBondDto);
    }

    /**
     * 获取金融产品信息
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("getCounterIdByFinprodId")
    public String getCounterIdByFinprodId(@RequestBody String finprodId) {
        return service.getCounterIdByFinprodId(finprodId);
    }
}
