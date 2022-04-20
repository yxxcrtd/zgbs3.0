package com.joyintech.tams.cfets.etr.bondfirstbid.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.service.CfetsEtrBondFirstBid002Service;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 一级投标指令新增/修改
 * @Author: kchen
 * @Date: 2022/4/18 16:38
 */
@Controller
@PageId("CfetsEtrBondFirstBid002")
@RequestMapping("/cfets/etrib/cfetsEtrBondFirstBid002")
@Api(tags = "一级投标指令新增/修改")
public class CfetsEtrBondFirstBid002Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrBondFirstBid002Controller.class);

    @Autowired
    private CfetsEtrBondFirstBid002Service cfetsEtrBondFirstBid002Service;

    /**
     * 业务编号组件
     */
    @Autowired
    private BaseGetBusinessId bussGetBussinessIdService;

    @ResponseBody
    @ApiOperation(value = "多组合指令新建保存/提交", notes = "多组合指令新建保存/提交")
    @PostMapping("save")
    public Message<AjaxResult> save(@RequestBody CfetsEtrBondFirstBidSaveRequest cfetsDto) {

        CfetsEtrBondFirstBid002Dto allDto = cfetsEtrBondFirstBid002Service.packageEquity(cfetsDto);

        List<CfetsEtrBondFirstBid002Dto> dataList = new ArrayList<>();

        for (CfetsEtrBondFirstBid002Dto dto : allDto.getEtrInvestBondFirstBidDtos()) {
            if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共同业务类型_债券一级投标委托);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }

            //预占功能
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                dataList.add(dto);
            }

        }

        try {
            if (StringUtil.equals(BussinessConstant.SUBMIT, allDto.getOperateType())) {
                AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(
                        dataList, DictCodeList.平安资产类型_一级市场债.getValue(),
                        BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                        true, null);
                if (StringUtil.isNotEmpty(ar)) {
                    return new Message<AjaxResult>().success().setData(ar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return PositionUtil.ajaxErrorReturn(e);
        }

        EntrustRdsCommonAop.riskListToChildren(allDto.getRiskTrialRetDto(), allDto.getEtrInvestBondFirstBidDtos(),
                (dto, list) -> dto.setRiskTrialRetDto(list));

        // 临时主键
        if(StringUtil.isEmpty(allDto.getEntrustId())) {
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共同业务类型_债券一级投标委托);
            String generatedId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
            allDto.setTotalEtrustId(generatedId);
        }else{
            allDto.setTotalEtrustId(allDto.getEntrustId());
        }

        ProcessParameter<CfetsEtrBondFirstBid002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsEtrBondFirstBid002Dto> params = new WorkFlowParameter<>();

        params.setPageId(this.getPageId());
        params.setElementId(cfetsDto.getElementId());
        params.setBusinessCode(CfetsEtrBondFirstBid002Dto::getTotalEtrustId);
        params.setBusinessType(DictCodeList.共同业务类型_债券一级投标委托.getValue());
        processParameter.setOperationType(OperationType.NEW);
        processParameter.setWorkFlowParameter(params);

        String msg = null;
        try {
            msg = cfetsEtrBondFirstBid002Service.start(processParameter, allDto);
            AjaxResult m = new AjaxResult(msg);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("保存银行间多组合指令新建失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

}
