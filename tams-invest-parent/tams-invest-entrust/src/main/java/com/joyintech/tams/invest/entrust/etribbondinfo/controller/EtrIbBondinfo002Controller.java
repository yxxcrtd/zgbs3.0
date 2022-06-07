package com.joyintech.tams.invest.entrust.etribbondinfo.controller;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
//import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.tams.invest.entrust.etribbondinfo.service.EtrIbBondinfo002Service;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtrIbBondinfo002Dto;

/**
 * 债券投资委托新建 〈功能详细描述〉
 */
@Controller
@PageId("EtrIbBondinfo002")
@RequestMapping("/invest/entrust/etribbondinfo/EtrIbBondinfo002")
public class EtrIbBondinfo002Controller extends BaseController {

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrIbBondinfo002Service etrIbBondinfo002Service;

    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    
    /**
     * 行权计划共通service
     */
    @Autowired
    private BusiFinExercisePlanService busiFinExercisePlanService;
    
    public static final String ENTRUST_RECORD_NEW = "new";

	public static final String ENTRUST_RECORD_MODIFY = "modify";


    /**
     * 初始化002新建页面
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.投资目的);
        this.setSelectOptionToModel(model, DictCode.业务模式);
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("T39", DictCodeList.交易方向_行权_赎回.getValue());
        model.addAttribute("type01", DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("type03", DictCodeList.收益类型_货币型.getValue());
        model.addAttribute("type02", DictCodeList.收益类型_收益型.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("Y", DictCodeList.是否_是.getValue());
        model.addAttribute("CCY", DictCodeList.币种_人民币_CNY.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE") == null ? "01" : CacheUtil.getSysParam("IS_SHOW_TYPE"));
        
        model.addAttribute("SHARES_TRADE_MARKET_01", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        
        model.addAttribute("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
        model.addAttribute("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);

        boolean todoList = false;
        
        String entrustId = getParam("entrustId");
        String finprodId = getParam("finprodId");
        String portfolioId = getParam("portfolioId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrIbBondinfo002Dto dto = etrIbBondinfo002Service.getBussinessEntityByWorkflowId(
                    workflowId);
//            if (StringUtil.isBlank(dto.getParentPortfolioId())) {
//                dto.setParentPortfolioId(etrCommonService.selectParentPortfolioIdByPortfolioId(
//                        dto.getPortfolioId()
//                ));
//            }
            model.addAttribute(GlobalConstant.ENTITY, dto);
            todoList = true;
            model.addAttribute("workflowId", workflowId);         
            model.addAttribute("businessId", dto.getEntrustId());
            model.addAttribute("calculateField",dto.getCalculateField());
        }
        //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrIbBondinfo002Dto entity = new EtrIbBondinfo002Dto();
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            entity.setInvManagerId(LoginUserUtil.getUserName());
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("businessId", IDGenerator.generate());
        }
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("shortSellingSupported", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        model.addAttribute("TodoList", todoList);
        return super.init(model);
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getfinprodIdForAdd")
    public Page<SelectOptionDto> getfinprodIdForAdd()
            throws Exception {
        Page<SelectOptionDto> temp = etrIbBondinfo002Service.getfinprodIdForAdd(RequestUtil.getPageParam());
        return temp;
    }

//    /**
//     * 得到母组合代码
//     *
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @ResponseBody
//    @RequestMapping("/parentPortfolioId")
//    public String parentPortfolioId(@RequestBody Map<String, Object> param)
//            throws Exception {
//         String temp = etrCommonService.selectParentPortfolioIdByPortfolioId(param.get("portfolioId").toString());
//        return temp;
//    }

    /**
     * 可用面值计算
     * <功能详细描述>
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAvailableNum")
    public BigDecimal getAvailableNum(String finprodId) {
        return etrIbBondinfo002Service.getAvailableNum(finprodId);
    }

    /**
     * 净价、行权收益率、到期收益率
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/calData")
    @ResponseBody
    public AjaxResult calData(@RequestBody EtrIbBondinfo002Dto dto) {
        AjaxResult result = new AjaxResult();
        etrIbBondinfo002Service.calcAmt(dto);
        result.setData(dto);
        return result;
    }

    /**
     * 查询行权价
     *
     * @author chaowei
     */
    @ResponseBody
    @RequestMapping("getExercisePricebyPara")
    public BigDecimal getExercisePricebyPara(@RequestBody Map<String, Object> param) {
        return etrIbBondinfo002Service.getExercisePricebyPara(param);
    }


    /**
     * 保存按钮点击时的check
     *
     * @param dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) EtrIbBondinfo002Dto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        if (StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")) {
            List<ValidMessage> errorList = new ArrayList<>();
            if (errorList.size() > 0) {
                AjaxResult ajaxResult = new AjaxResult();
                ajaxResult.setErrorList(errorList);
                return ajaxResult;
            }
        }
        
        // 行权方式check
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        // 可用现金软校验
        // 前台单位万元
        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
                DictCodeListExt1.是或否_是.getValue())
                && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                && dto.getProAvailableCash() != null) {
            if (dto.getEntrustNum().multiply(new BigDecimal(10000)).compareTo(dto.getProAvailableCash()) == 1) {
                confirmMsg = this.getMessage("TRD_C_0002");
            }
        }
        // 可用持仓软校验
        else if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                DictCodeListExt1.是或否_是.getValue())
                && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
                && dto.getAssetAvailablePosition() != null) {
            if (dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0) {
                confirmMsg = this.getMessage("ETR_BOND_002");
            }
        }
        if (StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
                || StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs())) {
            Map<String, Object> p = new HashMap<>();
            p.put("finprodId", dto.getFinprodId());
            p.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
            p.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
            p.put("execType01", DictCodeList.行权状态_未行权.getValue());
            p.put("execType02", DictCodeList.行权状态_已行权.getValue());
            List<FinExercisePlan> planList = busiFinExercisePlanService.getfinExercisePlanByProductId(
                    p);
            boolean checkFlag = true;
            Date vdate = dto.getVdate();
            Date mdate = dto.getMdate();
            if (!StringUtil.isEmptyList(planList)) {
                for (FinExercisePlan planDto : planList) {
                    if (planDto.getExerciseDate().compareTo(vdate) >= 0 && planDto.getExerciseDate().compareTo(mdate) <= 0) {
                        if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), dto.getPs())
                                && StringUtil.equals(DictCodeList.行权方式_回售.getValue(),
                                planDto.getExerciseType()))
                                || (StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), dto.getPs())
                                && StringUtil.equals(DictCodeList.行权方式_赎回.getValue(),
                                planDto.getExerciseType()))) {
                            checkFlag = false;
                        }
                    }
                }
            }
            if (checkFlag) {
                if (StringUtil.isNotBlank(confirmMsg)) {
                    confirmMsg += changeLineMark;
                }
                confirmMsg += this.getMessage("债券行权计划中没有满足所选交易方向和日期的行权数据，请确认是否继续？");
            }
        }
        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult ajaxResult = new AjaxResult();
            ajaxResult.setData(confirmMsg);
            return ajaxResult;
        }
        // 行权方式check

        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 保存/提交
     *
     * @param dto dto
     * @param br  br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrIbBondinfo002Dto dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        if (StringUtil.equals(dto.getSettleSpreed(),
                DictCodeList.清算速度_T_0.getValue())) {
            dto.setSettleSpeed((long)0);
        }
        if (StringUtil.equals(dto.getSettleSpreed(),
                DictCodeList.清算速度_T_1.getValue())) {
            dto.setSettleSpeed((long) 1);
        }
        //默认证券账户
        if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("portfolioId", dto.getPortfolioId());
            map.put("finprodId", dto.getFinprodId());
            String secAcctId = etrIbBondinfo002Service.qryPortfolioSecAcctId(map);
            if (StringUtil.isNotEmpty(secAcctId)) {
                dto.setSecAcctId(secAcctId); // 证券账户
            }
        }

        if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
            String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
            dto.setEntrustId(code);
        }

        List<String> fileNos = new ArrayList<>();
        if(StringUtil.isNotEmpty(dto.getBusinessId())){
            List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(dto.getBusinessId());
            for(UploadFile file : files){
                fileNos.add(file.getFileNo());
            }
            // 文件与指令ID绑定
            fileAttachmentProcess.updateBussinessNo(fileNos,dto.getEntrustId());
        }

        // ----------预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            EtrIbBondinfo002Dto dtoOne=new EtrIbBondinfo002Dto();
            BeanUtil.copyPropertiesIgnoreNull(dtoOne, dto);
            dtoOne.setEntrustNum(ArithmeticUtil.multiply(dtoOne.getEntrustNum(),BigDecimal.valueOf(10000)));
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dtoOne,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_债券.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------预占功能 end----------
        ProcessParameter<EtrIbBondinfo002Dto> processParameter = new ProcessParameter<EtrIbBondinfo002Dto>();
        WorkFlowParameter<EtrIbBondinfo002Dto> params = new WorkFlowParameter<EtrIbBondinfo002Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_债券投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> this.getMessage("EtrIbBondinfo002") + "(" + entity.getPortfolioId() + ")"
                        + "-" + this.getMessage("EtrIbBondinfo002.ps") + "(" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs()) + ")" + "-" + "("
                        + this.getMessage("EtrIbBondinfo002.vdate") + ":" + DateUtil.format(entity.getVdate())
                        + ")-(" + this.getMessage("EtrIbBondinfo002.mdate") + ":"
                        + DateUtil.format(entity.getMdate()) + ")-"
                        + this.getMessage("EtribBondInfo003.entrustAmt") + ":"  // 此处使用多组合的卷面总额（万元）描述
                        + entity.getEntrustNum());
        processParameter.setWorkFlowParameter(params);
        String msg = etrIbBondinfo002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }


    /**
     * 计算净价偏离度
     * @param paramMap
     * @return
     */
    @RequestMapping("/calDeviationDegree")
    @ResponseBody
    public BigDecimal calDeviationDegree(@RequestBody Map<String, Object> paramMap) {
        BigDecimal result =
        etrIbBondinfo002Service.calDeviationDegree(paramMap);
        return result;
    }

}
