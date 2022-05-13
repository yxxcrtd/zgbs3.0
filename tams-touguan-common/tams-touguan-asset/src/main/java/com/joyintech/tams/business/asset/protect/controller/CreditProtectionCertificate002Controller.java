package com.joyintech.tams.business.asset.protect.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificate002Dto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificate004Dto;
import com.joyintech.tams.business.asset.protect.dto.CreditProtectionCertificateDto;
import com.joyintech.tams.business.asset.protect.service.CreditProtectionCertificate002Service;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@PageId("CreditProtectionCertificate002")
@RequestMapping("/business/asset/protect/tenderBondAsset002")
@Api(tags = "信用保护凭证标的新增/修改(新)")
public class CreditProtectionCertificate002Controller extends BaseController {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CreditProtectionCertificate002Service creditProtectionCertificate002Service;

    @ResponseBody
    @PostMapping("/initParam")
    @ApiOperation(value = "新建/修改/查看页面初始化", notes = "新建/修改/查看页面初始化")
    public Message<Map<String, Object>> init() {
        Map<String, Object> initPageParams = new HashMap<>();
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        CreditProtectionCertificate002Dto entity = new CreditProtectionCertificate002Dto();
        // 待办中过来
        if (StringUtil.isNotEmpty(workflowId)) {
            entity = creditProtectionCertificate002Service.getBussinessEntityByWorkflowId(workflowId);
            initPageParams.put("businessId", entity.getFinprodMarketId());
        } else {//来自新增或者修改
            String finprodId = getParam("finprodId");
            if (StringUtil.isNotEmpty(finprodId)) {//修改
                entity = creditProtectionCertificate002Service.getById(finprodId);
                initPageParams.put("businessId", entity.getFinprodMarketId());
            }
        }
        //数据字典初始化数据
        initPageParams.put("PROTECT_OBJECT", this.getSelectDataFromDataDic(DictCode.保护对象, null));
        initPageParams.put("SETTLE_METHOD", this.getSelectDataFromDataDic(DictCode.结算方式_标的, null));
        initPageParams.put("FINPROD_TYPE", this.getSelectDataFromDataDic(DictCode.金融产品类型, null));
        initPageParams.put("FINPROD_TYPE_TG", this.getSelectDataFromDataDic(DictCode.金融产品类型_投管用, null));
        initPageParams.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
        initPageParams.put("ISSUE_CLASS", this.getSelectDataFromDataDic(DictCode.新债_发行方式, null));
        initPageParams.put("AMT_INCOME_TYPE", this.getSelectDataFromDataDic(DictCode.本金收益特征, null));
        initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        initPageParams.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
        initPageParams.put("PINAN_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.平安资产类型, null));
        initPageParams.put("CASH_TYPE_1", this.getSelectDataFromDataDic(DictCode.现金流一级类型, null));
        initPageParams.put("CASH_TYPE_2", this.getSelectDataFromDataDic(DictCode.现金流二级类型, null));
        initPageParams.put("FIN_CASH_BUSINESS_TYPE", this.getSelectDataFromDataDic(DictCode.现金流业务类型, null));
        initPageParams.put("BASE_TYPE", this.getSelectDataFromDataDic(DictCode.基数类型, null));
        initPageParams.put("BASE_DATE_TYPE", this.getSelectDataFromDataDic(DictCode.基数日期类型, null));
        initPageParams.put("CASH_CLASS_TYPE", this.getSelectDataFromDataDic(DictCode.现金流类型, null));
        initPageParams.put("CASH_UNIT_TYPE", this.getSelectDataFromDataDic(DictCode.现金流单位, null));
        initPageParams.put("PAY_TYPE", this.getSelectDataFromDataDic(DictCode.支付方式, null));
        initPageParams.put("MARKET_INSTI_TYPE_SCA", this.getSelectDataFromDataDic(DictCode.市场机构机构类型, null));
        initPageParams.put("BASIS", this.getSelectDataFromDataDic(DictCode.计息基础, null));
        //交易日历

        initPageParams.put(GlobalConstant.ENTITY, entity);
        //pageId
        initPageParams.put("pageId", this.getPageId());
        //当前平台日
        initPageParams.put("platDate", DateUtil.platDateFromCache());
        //当前用户
        initPageParams.put("userId", SecurityUtil.getLoginUser().getUserName());
        //流程id
        initPageParams.put("workflowId", workflowId);
        //是否流程中打开
        initPageParams.put("todoList", getParam("todoList"));
        return Message.success("校验成功", initPageParams);
    }


    @ResponseBody
    @PostMapping("/modifyAndDeleteCheck")
    @ApiOperation(value = "列表中点击删除和修改时调用校验", notes = "列表中点击删除和修改时调用校验")
    public Message modifyAndDeleteCheck(@RequestBody CreditProtectionCertificateDto dto) {
        List<ValidMessage> validMessageList = creditProtectionCertificate002Service.modifyAndDeleteCheck(dto);
        if (CollectionUtils.isNotEmpty(validMessageList)) {
            return Message.fail(ValidUtils.getValidMessage(validMessageList));
        } else {
            return Message.success("校验成功", null);
        }
    }


    @ResponseBody
    @PostMapping("/check")
    @ApiOperation(value = "新建/修改提交时校验", notes = "新建/修改提交时校验")
    public Message check(@RequestBody CreditProtectionCertificate002Dto dto) {
        List<ValidMessage> validMessageList = creditProtectionCertificate002Service.valid(dto);
        if (CollectionUtils.isNotEmpty(validMessageList)) {
            return Message.fail(ValidUtils.getValidMessage(validMessageList));
        } else {
            return Message.success("校验成功", null);
        }
    }

    @ResponseBody
    @PostMapping("/addCoupons")
    @ApiOperation(value = "加券按钮", notes = "加券按钮")
    public Message<CreditProtectionCertificate002Dto> addCoupons(@RequestParam("finprodMarketId") String finprodMarketId) {
        return Message.success("success", creditProtectionCertificate002Service.addCoupons(finprodMarketId));
    }

    @ResponseBody
    @PostMapping("/save")
    @ApiOperation(value = "新建/修改", notes = "新建/修改")
    public Message<AjaxResult> save(@RequestBody CreditProtectionCertificate002Dto dto) {
        ProcessParameter<CreditProtectionCertificate002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CreditProtectionCertificate002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(CreditProtectionCertificate002Dto::getFinprodMarketId);
        params.setBusinessType(DictCodeListExt2.平安资产类型_信用保护凭证.getValue());
        if (StringUtil.equals(dto.getOperateType(), OperationType.NEW.name())) {
            processParameter.setOperationType(OperationType.NEW);
        } else if (StringUtil.equals(dto.getOperateType(), OperationType.MODIFY.name())) {
            processParameter.setOperationType(OperationType.MODIFY);
        } else {
            processParameter.setOperationType(OperationType.DELETE);
        }
        try {
            creditProtectionCertificate002Service.start(processParameter, dto);
        } catch (Exception e) {
            logger.error("流程发起失败", e);
            return Message.fail("流程发起失败");
        }
        return Message.success("处理成功", null);
    }

    @ResponseBody
    @PostMapping("/delete")
    @ApiOperation(value = "删除", notes = "删除")
    public Message<AjaxResult> delete(@RequestBody CreditProtectionCertificate004Dto cpc004Dto) {
        ProcessParameter<CreditProtectionCertificate002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CreditProtectionCertificate002Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(cpc004Dto.getElementId());
        params.setBusinessCode(CreditProtectionCertificate002Dto::getFinprodMarketId);
        params.setBusinessType(DictCodeListExt2.平安资产类型_信用保护凭证.getValue());
        CreditProtectionCertificate002Dto dto = creditProtectionCertificate002Service.getById(cpc004Dto.getFinprodId());
        dto.setRemark(cpc004Dto.getRemark());
        dto.setOperateType(OperationType.DELETE.name());
        processParameter.setOperationType(OperationType.DELETE);
        processParameter.setWorkFlowParameter(params);
        try {
            creditProtectionCertificate002Service.start(processParameter, dto);
        } catch (Exception e) {
            logger.error("流程发起失败", e);
            return Message.fail("流程发起失败");
        }
        return Message.success("处理成功", null);
    }

}
