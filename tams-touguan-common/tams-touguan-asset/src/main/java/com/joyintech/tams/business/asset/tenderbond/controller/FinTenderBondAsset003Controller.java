package com.joyintech.tams.business.asset.tenderbond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.FinBondAssetSaveRequest;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset002Dto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset003Dto;
import com.joyintech.tams.business.asset.tenderbond.service.FinTenderBondAsset003Service;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoDetailQueryDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service.FinBondMarketInfoQuery001Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@PageId("FinTenderBondAsset003")
@RequestMapping("/asset/tenderBondAsset003")
@Api(tags = "新债标的上市流通")
public class FinTenderBondAsset003Controller extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FinTenderBondAsset003Service finTenderBoundAsset003Service;

    @Autowired
    private FinBondMarketInfoQuery001Service finBondMarketInfoQuery001Service;


    @ResponseBody
    @PostMapping("/initParam")
    @ApiOperation(value = "审批页面初始化", notes = "审批页面初始化")
    public Message<Map<String, Object>> init() {
        Map<String, Object> params = new HashMap<>();
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        FinTenderBondAsset003Dto entity = new FinTenderBondAsset003Dto();
        // 待办中过来
        if (StringUtil.isNotEmpty(workflowId)) {
            entity = finTenderBoundAsset003Service.getBussinessEntityByWorkflowId(workflowId);
            params.put("businessId", entity.getFinprodMarketId());
        }
        //数据字典初始化数据
        params.put("ISSUE_STAGE", this.getSelectDataFromDataDic(DictCode.发行阶段, null));
        params.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        params.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        params.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
        params.put("ISSUE_TYPE", this.getSelectDataFromDataDic(DictCode.新债_发行方式, null));
        params.put("TENDER_TYPE", this.getSelectDataFromDataDic(DictCode.招标方式, null));
        params.put("TENDER_TARGET", this.getSelectDataFromDataDic(DictCode.招标标的, null));
        params.put("ISSUE_STYLE", this.getSelectDataFromDataDic(DictCode.发行类型, null));
        params.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        params.put("INT_TYPE", this.getSelectDataFromDataDic(DictCode.利率类型, null));
        params.put("FREQUENCY_UNIT", this.getSelectDataFromDataDic(BussinessDictCode.频率, null));
        params.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        params.put("ISSUE_STATUS", this.getSelectDataFromDataDic(DictCode.发行状态, null));
        params.put("CUSTOM_ASSET_TYPE_TWO", this.getSelectDataFromDataDic(DictCode.行内资产二级分类, null));
        params.put("CUSTOM_ASSET_TYPE_THREE", this.getSelectDataFromDataDic(DictCode.行内资产三级分类, null));
        params.put("CUSTOM_ASSET_TYPE_FOUR", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        params.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
        params.put("SVR_BASIS_TYPE_ACCURACY", this.getSelectDataFromDataDic(DictCode.基准利率精度, null));
        params.put("AMT_INCOME_TYPE",this.getSelectDataFromDataDic(DictCode.本金收益特征, null));

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
        params.put("todoList", getParam("todoList"));
        return Message.success("校验成功", params);
    }

    @ResponseBody
    @PostMapping("/check")
    @ApiOperation(value = "上市流通/撤销校验", notes = "上市流通/撤销校验")
    public Message check(@RequestBody FinTenderBondAsset003Dto dto) {
        List<ValidMessage> validMessageList = finTenderBoundAsset003Service.valid(dto);
        if (CollectionUtils.isNotEmpty(validMessageList)) {
            return Message.fail(ValidUtils.getValidMessage(validMessageList));
        } else {
            return Message.success("校验成功", null);
        }
    }

    @ResponseBody
    @PostMapping("/checkAddCoupons")
    @ApiOperation(value = "上市流通前自动加券校验是否通过", notes = "上市流通前自动加券校验是否通过")
    public Message checkAddCoupons(@RequestBody FinTenderBondAsset003Dto dto) {
        FinBondMarketInfoDetailQueryDto request = finTenderBoundAsset003Service.validAddCoupons(dto);
        if (Objects.isNull(request)) {
            return Message.success("校验成功", null);
        } else {
            return Message.success("校验成功", request);
        }
    }

    @ResponseBody
    @PostMapping("/addCoupons")
    @ApiOperation(value = "上市流通自动加券", notes = "上市流通自动加券")
    public Message addCoupons(@RequestBody FinTenderBondAsset003Dto dto) {
        AjaxResult ajaxResult = finTenderBoundAsset003Service.addCoupons(dto);
        if (CollectionUtils.isEmpty(ajaxResult.getErrorList())) {
            return Message.success("加券成功", null);
        } else {
            return Message.fail("加券失败", ajaxResult.getErrorList());
        }
    }


    @ResponseBody
    @PostMapping("/save")
    @ApiOperation(value = "上市流通/撤销", notes = "上市流通/撤销")
    public Message<AjaxResult> save(@RequestBody FinTenderBondAsset003Dto dto) {
        ProcessParameter<FinTenderBondAsset003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FinTenderBondAsset003Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(FinTenderBondAsset003Dto::getFinprodMarketId);
        params.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标债券类资产管理.getValue());
        if (StringUtil.equals(dto.getOperateType(), OperationType.NEW.name())) {
            processParameter.setOperationType(OperationType.NEW);
            params.setBusinessType(BussinessDictCodeList.共同业务类型_上市流通.getValue());
        } else if (StringUtil.equals(dto.getOperateType(), OperationType.REVOKE.name())) {
            //processParameter.setOperationType(OperationType.REVOKE);
            params.setBusinessType(BussinessDictCodeList.共同业务类型_上市流通撤销.getValue());
            dto.setRevokeReason(dto.getRemark());
            dto.setRemark(null);
        }
        processParameter.setWorkFlowParameter(params);
        try {
            finTenderBoundAsset003Service.start(processParameter, dto);
        } catch (Exception e) {
            logger.error("新债标的流程发起失败", e);
            return Message.fail("流程发起失败");
        }
        return Message.success("处理成功", null);
    }
}
