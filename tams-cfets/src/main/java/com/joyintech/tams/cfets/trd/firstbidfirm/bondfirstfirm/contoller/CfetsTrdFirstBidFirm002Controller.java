package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.contoller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.trd.bond.dto.RiskCalRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInitRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service.CfetsTrdFirstBidFirm002Service;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.contoller.CfetsTrdBondFirstSub002Controller;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeList;
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
 * 一级投标确认新建
 *
 * @Author: kchen
 * @Date: 2022/5/14 11:07
 */
@Controller
@PageId("CfetsTrdFirstBidFirm002")
@RequestMapping("/cfets/finbond/tenderBondSub/CfetsTrdFirstBidFirm002")
@Api(tags = "CFETS一级投标确认-新建/修改")
public class CfetsTrdFirstBidFirm002Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub002Controller.class);

    @Autowired
    CfetsTrdFirstBidFirm002Service cfetsTrdFirstBidFirm002Service;

    @Autowired
    private CfetsRepoQuotedPrice004Service cfetsRepoQuotedPrice004Service;

    /**
     * 风险接口组件Controller
     */
//    @Autowired
//    private InterfRiskRdsController interfRiskRdsController;

    @Autowired
    private IPlatformDate iPlatformDate;

    /**
     * 初始化页面
     *
     * @param map 入参 workflowId isFromToDoList
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/4/22 10:56
     */
    @ApiOperation(value = "审批初始化", notes = "审批页面初始化 入参 workflowId isFromToDoList")
    @PostMapping("/initPage")
    @ResponseBody
    public Message<Map<String, Object>> initPage(@RequestBody Map<String, Object> map) {
        Map<String, Object> initPageParams = new HashMap<>();
        // 资金账户类型_托管账户
        initPageParams.put("acctType_A01", BussinessDictCodeList.资金账户类型_托管账户.getValue());
        // 系统日期
        initPageParams.put("sysDate", iPlatformDate.getSysDateStr());
        // 币种_人民币
        initPageParams.put("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());
        // 是否进行指令汇总(Y:是，N:否)_一级投标
        initPageParams.put("IS_ENTRUST_SUM_FLAG_BID", CacheUtil.getSysParam("IS_ENTRUST_SUM_FLAG_BID"));
        // 登记页面id 用于登记撤销
        initPageParams.put("PAGE_ID_BOND_FIRST", "CfetsTrdFirstBidFirm002");
        //刷新时间
        initPageParams.put("ibuQuoteBondFirst_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        initPageParams.put("effDate", CacheUtil.getSysParam("ibuVar_quoteEffTime"));

        //流程id
        String workflowId = "null".equals(map.get(GlobalConstant.WORKFLOW_ID)) ? null : (String) map.get(GlobalConstant.WORKFLOW_ID);
//        String isFromToDoList = getParam("isFromToDoList");
        if (StringUtil.isNotEmpty(workflowId)) {
            CfetsTrdFirstBidFirmInfoDto entity = cfetsTrdFirstBidFirm002Service.getBussinessEntityByWorkflowId(workflowId);
//            test(entity);
            initPageParams.put(GlobalConstant.WORKFLOW_ID, workflowId);
            initPageParams.put(GlobalConstant.ENTITY, entity);
        }
        return new Message<>(initPageParams).success();
    }

//    /**
//     * 风险试算测试
//     * @author jiangpeng
//     * @date 2022/5/9 10:42
//     * @param entity
//     */
//    private void test(CfetsTrdBondFirstSubInfoDto entity){
//        CfetsEtribdoRiskcalResponse riskcalResponse = entity.getRiskcalResponse();
//        if(StringUtil.isNotEmpty(riskcalResponse)){
//            return;
//        }
//        riskcalResponse = new CfetsEtribdoRiskcalResponse();
//        riskcalResponse.setRiskMathFlag("02");
//        riskcalResponse.setRiskTrialSpan("试算不通过");
//        riskcalResponse.setStdrtninfo("处理成功");
//        List<CfetsEtribdoRiskcalResponse> riskcalReturnList = new ArrayList<>();
//        CfetsEtribdoRiskcalResponse riskcal = new CfetsEtribdoRiskcalResponse();
//        riskcal.setKriCode("80434e46d8bd490cb75b2418bfe53b02");
//        riskcal.setPortfolioCode("AAA1");
//        riskcal.setPortfolioName("AAA1");
//        riskcal.setKriTypeCn("流动性管理类");
//        riskcal.setAssetName("A1");
//        riskcal.setWarningLine(">=2700000.0000万元");
//        riskcal.setApproveLine(">=3000000.0000万元");
//        riskcal.setForbiddenLn("<10%");
//        riskcal.setKriValue("<10%");
//        riskcal.setOldValue("<10%");
//        riskcal.setKriTrend("向好");
//        riskcal.setKriStatus("03");
//        riskcal.setTotalConclu("试算不通过");
//        riskcal.setKriStatusCn("3");
//        riskcal.setKriName("3");
//        riskcal.setKriType("3");
//        riskcal.setKriExpr("3");
//        riskcal.setKriOldExpr("3");
//        riskcalReturnList.add(riskcal);
//        riskcal.setKriCode("80434e46d8bd490cb75b2418bfe51b02");
//        riskcalReturnList.add(riskcal);
//        riskcalResponse.setRiskcalReturnList(riskcalReturnList);
//        entity.setRiskcalResponse(riskcalResponse);
//    }


    @ApiOperation(value = "<投标确认>界面初始化检查", notes = "点击【投标确认】按钮、<指令查询明细>页面点击【确认】调用")
    @PostMapping("/dealexecutioncheck")
    @ResponseBody
    public Message<AjaxResult> dealexecutioncheck(@RequestBody CfetsTrdFirstBidFirmInitRequest request) {
        AjaxResult ajaxResult = cfetsTrdFirstBidFirm002Service.dealexecutioncheck(request);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }


    @ApiOperation(value = "<投标确认>界面初始化", notes = "点击【投标确认】按钮、<指令查询明细>页面点击【确认】调用,[<投标确认>界面初始化检查]返回成功结果后调用,")
    @PostMapping("/dealexecutioninit")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> dealexecutioninit(@RequestBody CfetsTrdFirstBidFirmInitRequest request) {
        CfetsTrdFirstBidFirmInfoDto initData = cfetsTrdFirstBidFirm002Service.queryEntrustInfoById(request);
        if (StringUtil.isNotEmpty(initData)) {
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(initData);
        } else {
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("<投标确认>界面初始化异常");
        }
    }


    @ApiOperation(value = "<投标确认>查看界面初始化", notes = "点击<投标确认执行明细>里的数据后,调用该接口 查看<投标确认>数据")
    @PostMapping("/viewPage")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> viewPage(@RequestBody CfetsTrdFirstBidFirmInitRequest request) {
        CfetsTrdFirstBidFirmInfoDto viewInfo = cfetsTrdFirstBidFirm002Service.viewPage(request);
        if (StringUtil.isNotEmpty(viewInfo)) {
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(viewInfo);
        } else {
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("<投标确认>查看界面初始化异常");
        }
    }

    @ApiOperation(value = "<指令明细查询>查询", notes = "<指令明细查询>页面初始化或点击【查询】按钮调用 ")
    @PostMapping("/etrAddSearch")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubEntrustDto>> etrAddSearch(@RequestBody CfetsTrdBondFirstSubEntrustRequest request) {
        try {
            Page<CfetsTrdBondFirstSubEntrustDto> list = /*cfetsTrdFirstBidFirm001Service.etrAddSearch(request)*/null;
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("指令查询明细添加异常", e);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().fail().setMessage("指令查询明细添加异常");
        }
    }


    @ApiOperation(value = "<投标产品列表>新增或删除", notes = "分销买入时,<投标产品列表>新增或删除")
    @PostMapping("/addOrDelList")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> addOrDelList(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        CfetsTrdFirstBidFirmInfoDto dealData = cfetsTrdFirstBidFirm002Service.addOrDelList(request);
        if (StringUtil.isNotEmpty(dealData)) {
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(dealData);
        } else {
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("<投标产品列表>查询异常");
        }
    }

    @ApiOperation(value = "自动分配", notes = "点击【自动分配】按钮调用")
    @PostMapping("/autoAllocation")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> autoAllocation(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto dealData = cfetsTrdFirstBidFirm002Service.autoAllocation(request);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(dealData);
        } catch (Exception e) {
            logger.error("自动分配异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("自动分配异常");
        }
    }

    @ApiOperation(value = "其他金额分配", notes = "分配应缴款金额和结算服务费,编辑 基本信息中应缴款金额、结算服务费或申购列表里的认购量 时调用")
    @PostMapping("/otherAllocation")
    @ResponseBody
    public Message<List<CfetsTrdBondFirstSubProductDto>> otherAllocation(@RequestBody CfetsTrdBondFirstSubInfoDto request) {
        try {
            List<CfetsTrdBondFirstSubProductDto> list = cfetsTrdFirstBidFirm002Service.otherAllocation(request);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("其他金额分配异常", e);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().fail().setMessage("其他金额分配异常");
        }
    }

    @ApiOperation(value = "计算", notes = "联动计算,编辑基本信息中认购量时调用")
    @PostMapping("/calData")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> calData(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto result = cfetsTrdFirstBidFirm002Service.calData(request);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("计算数据异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("计算数据异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算check", notes = "风险试算check")
    @PostMapping("doRiskcalCheck")
    public Message<AjaxResult> doRiskcalCheck(@RequestBody RiskCalRequest riskCalRequest) {
        AjaxResult ajaxResult = null/*cfetsTrdBondFirstSub002Service.riskCheckNotEmpty(riskCalRequest)*/;
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody RiskCalRequest riskCalRequest) {
        CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
        try {
//            AjaxResult ajaxResult = cfetsTrdBondFirstSub002Service.riskCheckNotEmpty(riskCalRequest);
//            if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
//                return new Message<AjaxResult>().success().setData(ajaxResult);
//            }
//            Map<String, Object> dataMap = cfetsTrdBondFirstSub002Service.doRiskcalPackage(riskCalRequest);
//            List<String> resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "0", "RSK002", "F01");
//            result.setRiskMathFlag("02");
//            boolean forbidFlag = false;
//            boolean cluflag = true;
//            //是否全部正常
//            boolean allNormalFlag = true;
//            List<CfetsEtribdoRiskcalResponse> resultlist = new ArrayList<>();
//            if (!StringUtil.isEmptyList(resultArrList)) {
//                for (String row : resultArrList) {
//                    ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
//                    if (StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
//                            && StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
//                    ) {
//                        if (StringUtil.equals(resultArr.getHead().get("std400msgt"), "E")) {
//                            return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
//                        }
//                    }
//                    if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
//                            || !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
//                            || !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
//                        return new Message<AjaxResult>().success().setMessage("无");
//                    }
//                    List<Map<String, Object>> riskList = (List<Map<String, Object>>) resultArr.getBody().get("RSK_LIST");
//                    for (Map<String, Object> map : riskList) {
//                        if (allNormalFlag && !StringUtil.equals(map.get("kriStatus"), "0")) {
//                            allNormalFlag = false;
//                        }
//                        if (StringUtil.equals(map.get("kriStatus"), "0") && !StringUtil.equals(map.get("kriTrend"), "向好")) {
//                            forbidFlag = true;
//                        }
////                        map.put("kriStatusCn", cfetsEtrIbBondinfoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
//                        resultlist.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
//                    }
//                    if (!allNormalFlag) {
//                        result.setRiskcalReturnList(resultlist);
//                    }
//                    result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
//                    //当置为不通过时就不再进来了
//                    if (cluflag) {
//                        if (!StringUtil.equals(resultArr.getBody().get("rskStatus"), "3")) {
//                            if (StringUtil.equals(resultArr.getBody().get("rskStatus"), "4") && forbidFlag) {
//                                cluflag = false;
//                                resultArr.getBody().put("rskStatus", "3");
//                                result.setRskStatus("3");
//                            }
//                        } else {
//                            cluflag = false;
//                        }
//                        if (!cluflag) {
//                            result.setRiskTrialSpan("试算不通过");
//                        } else {
//                            result.setRiskTrialSpan("试算通过");
//                        }
//                    }
//
//                }
//
//            }
            AjaxResult m = new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }
    }

    /**
     * 投标确认提交检查
     *
     * @param dto 页面元素
     * @return com.joyintech.tams.business.common.pojo.Message<com.joyintech.framework.ajax.AjaxResult>
     * @author jiangpeng
     * @date 2022/5/16 16:07
     */
    @ApiOperation(value = "投标确认提交检查", notes = "投标确认点击【提交】调用")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody CfetsTrdFirstBidFirmInfoDto dto) {
        try {
            AjaxResult ajaxResult = cfetsTrdFirstBidFirm002Service.check(dto);
            return new Message<AjaxResult>().success().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("投标确认提交检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("投标确认提交检查异常");
        }
    }

    /**
     * 投标确认提交(保存)
     *
     * @param dto 页面元素
     * @return com.joyintech.tams.business.common.pojo.Message<com.joyintech.framework.ajax.AjaxResult>
     * @author jiangpeng
     * @date 2022/4/22 10:54
     */
    @ApiOperation(value = "投标确认提交", notes = "投标确认点击【提交】【保存】调用")
    @ResponseBody
    @PostMapping("save")
    public Message save(@RequestBody CfetsTrdFirstBidFirmInfoDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }
        dto.setBidId(cfetsTrdFirstBidFirm002Service.getBidId(null));
        ProcessParameter<CfetsTrdFirstBidFirmInfoDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsTrdFirstBidFirmInfoDto> params = new WorkFlowParameter<>();
        // 页面id
        params.setPageId("CfetsTrdFirstBidFirm002");
        //
        params.setElementId(dto.getElementId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_中标结果确认.getValue());
        params.setBusinessCode(dto.getBidId());
        String finalStr = "一级投标确认-交易日期:" + DateUtil.format(iPlatformDate.getSysDate());
        params.setComment(entity -> finalStr);
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        processParameter.setPageId(params.getPageId());
        try {
            String msg = cfetsTrdFirstBidFirm002Service.start(processParameter, dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("保存申购新建失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }


    @ApiOperation(value = "当日申购撤销检查", notes = "当日申购撤销检查")
    @ResponseBody
    @PostMapping("/checkDaySubRevokeCheck")
    public Message<AjaxResult> checkDaySubRevokeCheck(@RequestBody CfetsBondFirstSubCheckRequst requst) {
        return new Message<AjaxResult>().success().setData(cfetsTrdFirstBidFirm002Service.checkDayRegRevokeCheck(requst));
    }

    @ApiOperation(value = "当日申购撤销", notes = "当日申购撤销")
    @ResponseBody
    @PostMapping("subRevoke")
    public Message<AjaxResult> subRevoke(@RequestBody CfetsBondFirstSubCheckRequst requst) throws WorkFlowException {
        //前后台转换
        AjaxResult ajaxResult = cfetsTrdFirstBidFirm002Service.checkDayRegRevokeCheck(requst);
        if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        List<CfetsTrdFirstBidFirmInfoDto> dtos = new ArrayList<>();
        for (CfetsBondFirstSubCheckDetaileRequst cfetsBondFirstSubCheckDetaileRequst : requst.getCfetsBondFirstSubCheckDetaileRequsts()) {
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("CfetsTrdFirstBidFirm002");
            wflValidInputParameter.setBusinessDataPk1(cfetsBondFirstSubCheckDetaileRequst.getBidId());
            ProcessMark processMark = cfetsRepoQuotedPrice004Service.getBusinessInfoByPageIdAndPk(wflValidInputParameter);
            CfetsTrdFirstBidFirmInfoDto dto = cfetsTrdFirstBidFirm002Service.getBussinessEntity(processMark);
            dto.setBidId(cfetsBondFirstSubCheckDetaileRequst.getBidId());
            dto.setRemark(StringUtil.join("申购撤销原因：", requst.getRemark()));
            dto.setOperateType("revoke");
            dtos.add(dto);
        }
        ProcessParameter<CfetsTrdFirstBidFirmInfoDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsTrdFirstBidFirmInfoDto> workFlowParameter = new WorkFlowParameter<>();
        workFlowParameter.setPageId("CfetsTrdFirstBidFirm002");
        workFlowParameter.setElementId("CfetsTrdFirstBidFirm002_btn_revoke");
        workFlowParameter.setBusinessCode(CfetsTrdFirstBidFirmInfoDto::getBidId);
        workFlowParameter.setBusinessType(BussinessDictCodeList.共同业务类型_中标结果确认.getValue());
        workFlowParameter.setComment(entity -> "申购编号 (" + entity.getBidId() + ")");
        processParameter.setPageId(workFlowParameter.getPageId());
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setOperationType(OperationType.REVOKE);
        String msg = cfetsTrdFirstBidFirm002Service.start(processParameter,
                dtos.toArray(new CfetsTrdFirstBidFirmInfoDto[dtos.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }
}
