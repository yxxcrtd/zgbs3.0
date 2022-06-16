package com.joyintech.tams.cfets.etr.bondfirstbid.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.service.CfetsEtrBondFirstBid002Service;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级投标指令新增/修改
 *
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

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    /**
     * 业务编号组件
     */
    @Autowired
    private BaseGetBusinessId bussGetBussinessIdService;

    /**
     * 头寸信息
     */
    @Autowired
    private PositionInfoService positionInfoService;


    @ResponseBody
    @PostMapping("/initParam")
    @ApiOperation(value = "新建/修改/查看页面初始化", notes = "新建/修改/查看页面初始化")
    public Message<Map<String, Object>> init(@RequestBody Map<String, Object> map) {
        Map<String, Object> params = new HashMap<>();
        String workflowId = "null".equals(map.get(GlobalConstant.WORKFLOW_ID)) ? null : (String) map.get(GlobalConstant.WORKFLOW_ID);
        boolean todoList = false;
        CfetsEtrBondFirstBid002Dto entity = new CfetsEtrBondFirstBid002Dto();
        // 待办中过来
        if (StringUtil.isNotEmpty(workflowId)) {
            todoList = true;
            entity = cfetsEtrBondFirstBid002Service.getBussinessEntityByWorkflowId(workflowId);

        }
        //数据字典初始化数据
        params.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));
        params.put("TENDER_TARGET", this.getSelectDataFromDataDic(DictCode.招标标的, null));
        // 加券按钮
        params.put("IS_ADDBOND_SHOW", CacheUtil.getSysParam("IS_ADDBOND_SHOW"));


        params.put(GlobalConstant.ENTITY, entity);
        //pageId
        params.put("pageId", this.getPageId());
        //当前平台日
        params.put("platDate", DateUtil.platDateFromCache());
        //当前用户
        params.put("userId", SecurityUtil.getLoginUser().getUserName());
        //流程id
        params.put("workflowId", workflowId);
        //是否流程中打开
        params.put("todoList", todoList);
        return new Message<Map<String, Object>>(params).success();
    }


    @ResponseBody
    @PostMapping("/checkSave")
    @ApiOperation(value = "新建/修改提交时校验", notes = "新建/修改提交时校验")
    public Message<AjaxResult> checkSave(@RequestBody CfetsEtrBondFirstBidSaveRequest cfetsDto) {
        try {
            AjaxResult ajax= cfetsEtrBondFirstBid002Service.checkSave(cfetsDto);
            //AjaxResult ajax= new AjaxResult();
            if(null!=ajax){
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                return new Message<AjaxResult>().success();
            }
        }catch (Exception e) {
            logger.error("多组合指令新建保存/提交校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }

    @ResponseBody
    @ApiOperation(value = "多组合指令新建保存/提交", notes = "多组合指令新建保存/提交")
    @PostMapping("save")
    public Message<AjaxResult> save(@RequestBody CfetsEtrBondFirstBidSaveRequest cfetsDto) {

        if (StringUtil.isNotEmpty(cfetsDto.getEntrustId())
                && (StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_招标发行.getValue())
                || StringUtil.equals(cfetsDto.getIssueClass(), DictCodeListExt4.新债_发行方式_簿记建档.getValue()))) {
            //根据指令代码获取指令信息
            EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(cfetsDto.getEntrustId());
            cfetsDto.setEntrustStatus(etrEntrust.getEntrustStatus());
            if (etrEntrust != null && !(StringUtil.equals(DictCodeList.委托状态_已退回.getValue(), etrEntrust.getEntrustStatus())
                    || StringUtil.equals(DictCodeList.委托状态_已撤回.getValue(), etrEntrust.getEntrustStatus()))) {
                //投标量没有改高，不走流程直接更新原指令信息
                if (cfetsDto.getTotalBiddingNum().compareTo(ArithmeticUtil.multiply(etrEntrust.getEntrustNum(), BigDecimal.valueOf(100))) < 1) {
                    CfetsEtrBondFirstBid002Dto allDto = cfetsEtrBondFirstBid002Service.packageEquity(cfetsDto);
                    cfetsEtrBondFirstBid002Service.saveModifyPassInfo(allDto);
                    AjaxResult m = new AjaxResult("一级指令修改直接通过成功！");
                    return new Message<AjaxResult>().success().setData(m);
                }
            }

        }

        CfetsEtrBondFirstBid002Dto allDto = cfetsEtrBondFirstBid002Service.packageEquity(cfetsDto);

        List<CfetsEtrBondFirstBid002Dto> dataList = new ArrayList<>();

        for (CfetsEtrBondFirstBid002Dto dto : allDto.getEtrInvestBondFirstBidDtos()) {
            if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeListExt3.共通业务类型_一级投标指令);
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
                        dataList, DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue(),
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
        if(StringUtil.isNotEmpty(cfetsDto.getEntrustId())){
            allDto.setTotalEtrustId(cfetsDto.getEntrustId());
        }else{
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeListExt3.共通业务类型_一级投标指令);
            String generatedId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
            allDto.setTotalEtrustId(generatedId);
        }

        ProcessParameter<CfetsEtrBondFirstBid002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsEtrBondFirstBid002Dto> params = new WorkFlowParameter<>();

        params.setPageId(this.getPageId());
        params.setElementId(cfetsDto.getElementId());
        params.setBusinessCode(CfetsEtrBondFirstBid002Dto::getTotalEtrustId);
        params.setBusinessType(DictCodeListExt3.共通业务类型_一级投标指令.getValue());
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
