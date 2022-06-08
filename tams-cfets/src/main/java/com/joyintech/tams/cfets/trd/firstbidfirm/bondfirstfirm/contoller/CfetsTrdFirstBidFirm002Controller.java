package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.contoller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.risk.aop.CfetsEntrustHanderAop;
import com.joyintech.tams.cfets.trd.bond.dto.RiskCalRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.*;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service.CfetsTrdFirstBidFirm001Service;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service.CfetsTrdFirstBidFirm002Service;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.contoller.CfetsTrdBondFirstSub002Controller;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoQuotedPrice004Service;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    CfetsTrdFirstBidFirm001Service cfetsTrdFirstBidFirm001Service;

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
        CfetsTrdFirstBidFirmInfoDto initData = cfetsTrdFirstBidFirm002Service.dealexecutioninit(request);
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
    public Message<Page<CfetsTrdFirstBidFirmEntrustDto>> etrAddSearch(@RequestBody CfetsTrdBondFirstBidFirmEntrustRequest request) {
        try {
            Page<CfetsTrdFirstBidFirmEntrustDto> list = cfetsTrdFirstBidFirm001Service.pageEntrust(request);
            return new Message<Page<CfetsTrdFirstBidFirmEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("指令查询明细添加异常", e);
            return new Message<Page<CfetsTrdFirstBidFirmEntrustDto>>().fail().setMessage("指令查询明细添加异常");
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
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("<投标产品列表>新增或删除查询异常");
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

    @ApiOperation(value = "中标标位联动", notes = "分销买入 修改【中标标位】时调用")
    @PostMapping("/winPriceLink")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> winPriceLink(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto result = cfetsTrdFirstBidFirm002Service.winPriceLink(request);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("中标标位联动异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("中标标位联动异常");
        }
    }

    @ApiOperation(value = "券面总额 应计利息 联动", notes = "分销买入 修改【券面总额】或【应计利息】时 调用 联动金额信息，投标产品列表的金额信息。券面总额需点分配计算")
    @PostMapping("/bidPriceTotalLink")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> bidPriceTotalLink(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto result = cfetsTrdFirstBidFirm002Service.calAmtAndAutoAllocation(request, request.getPrice());
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("券面总额联动异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("券面总额联动异常");
        }
    }

    @ApiOperation(value = "净价联动", notes = "分销买入 修改【净价】时调用")
    @PostMapping("/priceLink")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> priceLink(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto result = cfetsTrdFirstBidFirm002Service.calAmtAndAutoAllocation(request, request.getPrice());
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("券面总额联动异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("券面总额联动异常");
        }
    }

    @ApiOperation(value = "结算服务费联动", notes = "分销买入 修改【结算服务费】或修改<投标产品列表>的【券面总额】时调用 重新分配<投标产品列表>的交易金额/应计利息总额/缴款金额和结算服务费")
    @PostMapping("/settFeeAmtLink")
    @ResponseBody
    public Message<List<CfetsTrdFirstBidFirmProductDto>> settFeeAmtLink(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            List<CfetsTrdFirstBidFirmProductDto> result = cfetsTrdFirstBidFirm002Service.otherDistribution(request);
            return new Message<List<CfetsTrdFirstBidFirmProductDto>>().success().setData(result);
        } catch (Exception e) {
            logger.error("结算服务费联动异常", e);
            return new Message<List<CfetsTrdFirstBidFirmProductDto>>().fail().setMessage("结算服务费联动异常");
        }
    }

    @ApiOperation(value = "标的校验", notes = "自主投标 修改 基本信息中【汇总投标券面总额】和<投标产品列表>中信息 调用")
    @PostMapping("/checkFinInfo")
    @ResponseBody
    public Message<AjaxResult> checkFinInfo(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            AjaxResult result = cfetsTrdFirstBidFirm002Service.checkFinInfo(request);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("标的校验异常", e);
            return new Message<AjaxResult>().fail().setMessage("标的校验异常");
        }
    }

    @ApiOperation(value = "投标方式联动计算", notes = "修改 基本信息中【投标方式】 调用")
    @PostMapping("/biddingMethodLink")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> biddingMethodLink(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto result = cfetsTrdFirstBidFirm002Service.biddingMethodLink(request);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("投标方式联动计算联动异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("投标方式联动计算联动异常");
        }
    }

    @ApiOperation(value = "汇总投标列表联动", notes = "自主投标 修改 投标产品列表的【汇总投标券面总额】和汇总投标列表中【投标标位】 调用")
    @PostMapping("/productListLink")
    @ResponseBody
    public Message<CfetsTrdFirstBidFirmInfoDto> productListLink(@RequestBody CfetsTrdFirstBidFirmInfoDto request) {
        try {
            CfetsTrdFirstBidFirmInfoDto result = cfetsTrdFirstBidFirm002Service.productListLink(request);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("汇总投标列表联动异常", e);
            return new Message<CfetsTrdFirstBidFirmInfoDto>().fail().setMessage("汇总投标列表联动异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算check", notes = "风险试算check")
    @PostMapping("doRiskcalCheck")
    public Message<AjaxResult> doRiskcalCheck(@RequestBody RiskCalRequest riskCalRequest) {
        AjaxResult ajaxResult = null/*cfetsTrdBondFirstSub002Service.riskCheckNotEmpty(riskCalRequest)*/;
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

//    @ResponseBody
//    @ApiOperation(value = "风险试算", notes = "风险试算")
//    @PostMapping("doRiskcal")
//    public Message<AjaxResult> doRiskcal(@RequestBody RiskCalRequest riskCalRequest) {
//        CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
//        try {
//            AjaxResult m = new AjaxResult();
//            m.setData(result);
//            return new Message<AjaxResult>().success().setData(m);
//        } catch (Exception e) {
//            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
//        }
//    }

    /**
     * 投标确认提交检查
     *
     * @param dto
     * @return com.joyintech.tams.business.common.pojo.Message<com.joyintech.framework.ajax.AjaxResult>
     * @author jiangpeng
     * @date 2022/5/21 11:16
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
        ProcessParameter<CfetsTrdFirstBidFirmInfoDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsTrdFirstBidFirmInfoDto> params = new WorkFlowParameter<>();
        cfetsTrdFirstBidFirm002Service.selectCash(dto);
        // 页面id
        params.setPageId(getPageId());
        // 代办事项中显示名称拼接
        String str = "投标日期:" + DateUtil.format(iPlatformDate.getSysDate());
        String finprodAbbr = null;
        String counterName = null;
        String tradeAmt = null;
        if (StringUtil.isNotEmpty(dto.getFinprodAbbr())) {
            finprodAbbr = "资产简称:" + dto.getFinprodAbbr();
        }
        if (StringUtil.isNotEmpty(dto.getCounterName())) {
            counterName = "对手方:" + dto.getCounterName();
        }
        if (StringUtil.isNotEmpty(dto.getTotalBiddingNum())) {
            tradeAmt = "汇总投标券面总额:" + ArithmeticUtil.divide(dto.getTotalBiddingNum(), new BigDecimal(10000), 2, RoundingMode.HALF_UP) + "万元";
        }
        params.setElementId(dto.getElementId());
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), dto.getBiddingMethod())) {
            //  自主投标
            params.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标.getValue());
            dto.setBidId(cfetsTrdFirstBidFirm002Service.getBidId(BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标));
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), dto.getBiddingMethod())) {
            //代投代缴
            params.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴.getValue());
            dto.setBidId(cfetsTrdFirstBidFirm002Service.getBidId(BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴));
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), dto.getBiddingMethod())) {
            // 分销买入
            params.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
            dto.setBidId(cfetsTrdFirstBidFirm002Service.getBidId(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入));
            str = "交易日期:" + DateUtil.format(iPlatformDate.getSysDate());
            tradeAmt = "分销买入券面总额:" +
                    ArithmeticUtil.divide(dto.getBidPriceTotal(), new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString() + "万元";
        }
        str += StringUtil.isNotEmpty(finprodAbbr) ? "-" + finprodAbbr : "";
        str += StringUtil.isNotEmpty(counterName) ? "-" + counterName : "";
        str += StringUtil.isNotEmpty(tradeAmt) ? "-" + tradeAmt : "";
        params.setBusinessCode(dto.getBidId());
        String finalStr = str;
        params.setComment(entity -> finalStr);
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        processParameter.setPageId(params.getPageId());
        try {
            String msg = cfetsTrdFirstBidFirm002Service.start(processParameter, dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("保存一级投标确认新建失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }


    @ApiOperation(value = "撤销检查", notes = "<投标><分销买入>点击【撤销】时调用")
    @ResponseBody
    @PostMapping("/checkRevoke")
    public Message<AjaxResult> checkRevoke(@RequestBody CfetsTrdFirstBidFirmRevokeCheckRequst requst) {
        return new Message<AjaxResult>().success().setData(cfetsTrdFirstBidFirm002Service.checkRevoke(requst));
    }

    @ApiOperation(value = "撤销", notes = "[撤销检查]通过后调用")
    @ResponseBody
    @PostMapping("/revoke")
    public Message<AjaxResult> revoke(@RequestBody CfetsTrdFirstBidFirmRevokeCheckRequst requst) throws WorkFlowException {
        //前后台转换
        AjaxResult ajaxResult = cfetsTrdFirstBidFirm002Service.checkRevoke(requst);
        if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        List<CfetsTrdFirstBidFirmInfoDto> dtos = new ArrayList<>();
        String biddingMethod = null;
        for (CfetsTrdFirstBidFirmCheckDetaileRequst detail : requst.getDetails()) {
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId(getPageId());
            wflValidInputParameter.setBusinessDataPk1(detail.getBidId());
            ProcessMark processMark = cfetsRepoQuotedPrice004Service.getBusinessInfoByPageIdAndPk(wflValidInputParameter);
            CfetsTrdFirstBidFirmInfoDto dto = cfetsTrdFirstBidFirm002Service.getBussinessEntity(processMark);
            dto.setBidId(detail.getBidId());
            dto.setRemark(StringUtil.join("投标确认撤销原因：", requst.getRemark()));
            dto.setOperateType("revoke");
            biddingMethod = dto.getBiddingMethod();
            dtos.add(dto);
        }
        ProcessParameter<CfetsTrdFirstBidFirmInfoDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CfetsTrdFirstBidFirmInfoDto> workFlowParameter = new WorkFlowParameter<>();
        workFlowParameter.setPageId(getPageId());
        workFlowParameter.setElementId(getPageId() + "_btn_revoke");
        workFlowParameter.setBusinessCode(CfetsTrdFirstBidFirmInfoDto::getBidId);
        if (StringUtil.equals(DictCodeListExt4.投标方式_自主投标.getValue(), biddingMethod)) {
            //  自主投标
            workFlowParameter.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标.getValue());
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_代投代缴.getValue(), biddingMethod)) {
            //代投代缴
            workFlowParameter.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴.getValue());
        } else if (StringUtil.equals(DictCodeListExt4.投标方式_分销买入.getValue(), biddingMethod)) {
            // 分销买入
            workFlowParameter.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入.getValue());
        }
        workFlowParameter.setComment(entity -> "投标编号 (" + entity.getBidId() + ")");
        processParameter.setPageId(workFlowParameter.getPageId());
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setOperationType(OperationType.REVOKE);
        String msg = cfetsTrdFirstBidFirm002Service.start(processParameter,
                dtos.toArray(new CfetsTrdFirstBidFirmInfoDto[dtos.size()]));
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody CfetsTrdFirstBidFirmInfoDto dto){
        try {
            String message = cfetsTrdFirstBidFirm002Service.doRiskcalCheckMethod(dto);
            if (com.joyintech.framework.util.StringUtil.isNotEmpty(message)) {
                AjaxResult ar = new AjaxResult();
                List<ValidMessage> warnList = new ArrayList<>();
                warnList.add(new ValidMessage(message));
                ar.setErrorList(warnList);
                ar.setMessageType(MessageType.错误消息);
                return new Message<AjaxResult>().setData(ar);
            }
            AjaxResult ajaxResult = CfetsEntrustHanderAop.doRiskcal(dto,
                    BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue(), DictCodeList.平安资产类型_债券.getValue());
            return new Message<AjaxResult>().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("风险试算", e);
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }
    }
}
