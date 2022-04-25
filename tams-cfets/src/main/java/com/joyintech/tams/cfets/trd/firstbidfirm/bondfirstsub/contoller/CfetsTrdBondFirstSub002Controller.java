package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.contoller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.trd.bond.dto.RiskCalRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub001Service;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub002Service;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级申购新建
 *
 * @author jp
 * @date 2022/04/22
 */
@Controller
@PageId("TenderBondSub002")
@RequestMapping("/cfets/finbond/tenderBondSub/TenderBondSub002")
@Api(tags = "CFETS一级申购-常用查询类接口")
public class CfetsTrdBondFirstSub002Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub002Controller.class);

    @Autowired
    CfetsTrdBondFirstSub001Service cfetsTrdBondFirstSub001Service;

    @Autowired
    CfetsTrdBondFirstSub002Service cfetsTrdBondFirstSub002Service;

    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private IPlatformDate iPlatformDate;

    @ApiOperation(value = "初始化参数", notes = "初始化参数")
    @PostMapping("/initParams")
    @ResponseBody
    public Message<Map<String, Object>> initParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        //刷新时间
        initPageParams.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        initPageParams.put("effDate", CacheUtil.getSysParam("ibuVar_quoteEffTime"));
        return new Message<>(initPageParams).success();
    }

    /**
     * 初始化页面
     *
     * @param model
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/4/22 10:56
     */
    @Override
    @ApiOperation(value = "初始化页面", notes = "初始化页面")
    @PostMapping("/init")
    @ResponseBody
    public String init(Model model) {
        return super.init(model);
    }


    @ApiOperation(value = "申购初始化检查", notes = "点击申购按钮、指令查询明细页面点击确认调用")
    @PostMapping("/dealexecutioncheck")
    @ResponseBody
    public Message<AjaxResult> dealexecutioncheck(@RequestBody CfetsTrdBondFirstSubInitRequest request) {
        AjaxResult ajaxResult = cfetsTrdBondFirstSub002Service.dealexecutioncheck(request);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }


    @ApiOperation(value = "申购交易执行初始化", notes = "点击申购按钮、指令查询明细页面点击确认,申购初始化检查返回成功结果后调用,")
    @PostMapping("/dealexecutioninit")
    @ResponseBody
    public Message<CfetsTrdBondFirstSubInfoDto> dealexecutioninit(@RequestBody CfetsTrdBondFirstSubInitRequest request) {
        CfetsTrdBondFirstSubInfoDto cfetsBondDealQuoteCommonDto = cfetsTrdBondFirstSub002Service.queryEntrustInfoById(request);
        if (StringUtil.isNotEmpty(cfetsBondDealQuoteCommonDto)) {
            return new Message<CfetsTrdBondFirstSubInfoDto>().success().setData(cfetsBondDealQuoteCommonDto);
        } else {
            return new Message<CfetsTrdBondFirstSubInfoDto>().fail().setMessage("申购交易执行初始化异常");
        }
    }

    @ApiOperation(value = "指令查询明细", notes = "添加-指令查询明细 入参 市场代码和证券账户必传,点击添加按钮、指令查询明细页面点击查询按钮时调用")
    @PostMapping("/etrAddSearch")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubEntrustDto>> etrAddSearch(@RequestBody CfetsTrdBondFirstSubEntrustRequest request) {
        try {
            Page<CfetsTrdBondFirstSubEntrustDto> list = cfetsTrdBondFirstSub001Service.etrAddSearch(request);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("指令查询明细添加异常", e);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().fail().setMessage("指令查询明细添加异常");
        }
    }

    @ApiOperation(value = "自动分配", notes = "点击自动分配按钮调用")
    @PostMapping("/autoAllocation")
    @ResponseBody
    public Message<List<CfetsTrdBondFirstSubProductDto>> autoAllocation(@RequestBody CfetsTrdBondFirstSubInfoDto request) {
        try {
            List<CfetsTrdBondFirstSubProductDto> list = cfetsTrdBondFirstSub002Service.autoAllocation(request);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("自动分配异常", e);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().fail().setMessage("自动分配异常");
        }
    }

    @ApiOperation(value = "其他金额分配", notes = "分配应缴款金额和结算服务费,编辑 基本信息中应缴款金额、结算服务费或申购列表里的认购量 时调用")
    @PostMapping("/otherAllocation")
    @ResponseBody
    public Message<List<CfetsTrdBondFirstSubProductDto>> otherAllocation(@RequestBody CfetsTrdBondFirstSubInfoDto request) {
        try {
            List<CfetsTrdBondFirstSubProductDto> list = cfetsTrdBondFirstSub002Service.otherAllocation(request);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("其他金额分配异常", e);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().fail().setMessage("其他金额分配异常");
        }
    }

    @ApiOperation(value = "计算", notes = "联动计算,编辑基本信息中认购量时调用")
    @PostMapping("/calData")
    @ResponseBody
    public Message<CfetsTrdBondFirstSubInfoDto> calData(@RequestBody CfetsTrdBondFirstSubInfoDto request) {
        try {
            CfetsTrdBondFirstSubInfoDto result = cfetsTrdBondFirstSub002Service.calData(request);
            return new Message<CfetsTrdBondFirstSubInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("计算数据异常", e);
            return new Message<CfetsTrdBondFirstSubInfoDto>().fail().setMessage("计算数据异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算check", notes = "风险试算check")
    @PostMapping("doRiskcalCheck")
    public Message<AjaxResult> doRiskcalCheck(@RequestBody RiskCalRequest riskCalRequest) {
        AjaxResult ajaxResult = cfetsTrdBondFirstSub002Service.riskCheckNotEmpty(riskCalRequest);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody RiskCalRequest riskCalRequest) {
        CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
        try {
            AjaxResult ajaxResult = cfetsTrdBondFirstSub002Service.riskCheckNotEmpty(riskCalRequest);
            if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }
            Map<String, Object> dataMap = cfetsTrdBondFirstSub002Service.doRiskcalPackage(riskCalRequest);
            List<String> resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "0", "RSK002", "F01");
            result.setRiskMathFlag("02");
            Boolean forbidFlag = false;
            Boolean cluflag = true;
            //是否全部正常
            Boolean allNormalFlag = true;
            List<CfetsEtribdoRiskcalResponse> resultlist = new ArrayList<>();
            if (!StringUtil.isEmptyList(resultArrList)) {
                for (String row : resultArrList) {
                    ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
                    if (StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
                            && StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
                    ) {
                        if (StringUtil.equals(resultArr.getHead().get("std400msgt"), "E")) {
                            return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
                        }
                    }
                    if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
                        return new Message<AjaxResult>().success().setMessage("无");
                    }
                    List<Map<String, Object>> riskList = (List<Map<String, Object>>) resultArr.getBody().get("RSK_LIST");
                    for (Map<String, Object> map : riskList) {
                        if (allNormalFlag && !StringUtil.equals(map.get("kriStatus"), "0")) {
                            allNormalFlag = false;
                        }
                        if (StringUtil.equals(map.get("kriStatus"), "0") && !StringUtil.equals(map.get("kriTrend"), "向好")) {
                            forbidFlag = true;
                        }
//                        map.put("kriStatusCn", cfetsEtrIbBondinfoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
                        resultlist.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
                    }
                    if (!allNormalFlag) {
                        result.setRiskcalReturnList(resultlist);
                    }
                    result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
                    //当置为不通过时就不再进来了
                    if (cluflag) {
                        if (!StringUtil.equals(resultArr.getBody().get("rskStatus"), "3")) {
                            if (StringUtil.equals(resultArr.getBody().get("rskStatus"), "4") && forbidFlag) {
                                cluflag = false;
                                resultArr.getBody().put("rskStatus", "3");
                                result.setRskStatus("3");
                            }
                        } else {
                            cluflag = false;
                        }
                        if (!cluflag) {
                            result.setRiskTrialSpan("试算不通过");
                        } else {
                            result.setRiskTrialSpan("试算通过");
                        }
                    }

                }

            }
            AjaxResult m = new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }
    }

    /**
     * 申购提交检查
     *
     * @param dto 页面元素
     * @return com.joyintech.tams.business.common.pojo.Message<com.joyintech.framework.ajax.AjaxResult>
     * @author jiangpeng
     * @date 2022/4/22 10:53
     */
    @ApiOperation(value = "申购提交检查", notes = "申购提交检查")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody CfetsTrdBondFirstSubInfoDto dto) {
        try {
            AjaxResult ajaxResult = cfetsTrdBondFirstSub002Service.check(dto);
            return new Message<AjaxResult>().success().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("申购提交检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("申购提交检查异常");
        }
    }

    /**
     * 申购提交(保存)
     *
     * @param dto 页面元素
     * @return com.joyintech.tams.business.common.pojo.Message<com.joyintech.framework.ajax.AjaxResult>
     * @author jiangpeng
     * @date 2022/4/22 10:54
     */
    @ApiOperation(value = "申购提交(保存)", notes = "申购提交(保存)")
    @ResponseBody
    @RequestMapping("save")
    public Message<AjaxResult> save(@RequestBody CfetsTrdBondFirstSubInfoDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }
        dto.setBidId(cfetsTrdBondFirstSub002Service.getBidId(null));
        ProcessParameter<CfetsTrdBondFirstSubInfoDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsTrdBondFirstSubInfoDto> params = new WorkFlowParameter<>();
        params.setPageId("TenderBondSub002");
        params.setElementId(dto.getElementId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_中标结果确认.getValue());
        params.setBusinessCode(dto.getBidId());
        String str = "交易日期:" + DateUtil.format(iPlatformDate.getSysDate());
        String finalStr = str;
        params.setComment(entity -> finalStr);
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        try {
            String msg = cfetsTrdBondFirstSub002Service.start(processParameter, dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("保存申购新建失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

}
