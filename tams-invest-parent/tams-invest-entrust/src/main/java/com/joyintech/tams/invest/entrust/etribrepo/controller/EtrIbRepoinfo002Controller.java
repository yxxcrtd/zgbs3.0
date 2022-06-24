package com.joyintech.tams.invest.entrust.etribrepo.controller;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtrIbRepoinfo002Dto;
import com.joyintech.tams.invest.entrust.etribrepo.service.EtrIbRepoinfo002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import com.joyintech.framework.security.LoginUserUtil;

/**
 * 质押式回购投资委托新建 〈功能详细描述〉
 */
@Controller
@PageId("EtrIbRepoinfo002")
@RequestMapping("/invest/entrust/etribrepo/EtrIbRepoinfo002")
public class EtrIbRepoinfo002Controller extends BaseController {

    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrIbRepoinfo002Service etrIbRepoinfo002Service;

    @Autowired
    private EtrCommonService etrCommonService;
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
    	setSelectOptionToModel(model, DictCode.交易场所);
        setSelectOptionToModel(model, DictCode.回购拆借方向);
        setSelectOptionToModel(model, DictCode.币种);
        setSelectOptionToModel(model, DictCode.清算速度);
        setSelectOptionToModel(model, DictCode.回购类型);
        setSelectOptionToModel(model, DictCode.回购期限品种);
        setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.交易平台);
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));
        model.addAttribute("ps_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("ps_04", DictCodeList.回购拆借方向_逆回购.getValue());
        model.addAttribute("tradeP01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("tradeP02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("tradeP03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("repoTypeH01", DictCodeList.回购类型_质押式协议回购.getValue());
        model.addAttribute("repoTypeH02", DictCodeList.回购类型_质押式竞价回购.getValue());
        model.addAttribute("speed0", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("speed1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("GC001", DictCodeList.回购期限品种_GC001.getValue());
        model.addAttribute("GC002", DictCodeList.回购期限品种_GC002.getValue());
        model.addAttribute("GC003", DictCodeList.回购期限品种_GC003.getValue());
        model.addAttribute("GC004", DictCodeList.回购期限品种_GC004.getValue());
        model.addAttribute("GC007", DictCodeList.回购期限品种_GC007.getValue());
        model.addAttribute("GC014", DictCodeList.回购期限品种_GC014.getValue());
        model.addAttribute("GC028", DictCodeList.回购期限品种_GC028.getValue());
        model.addAttribute("GC091", DictCodeList.回购期限品种_GC091.getValue());
        model.addAttribute("GC182", DictCodeList.回购期限品种_GC182.getValue());
        model.addAttribute("R001", DictCodeList.回购期限品种_R001.getValue());
        model.addAttribute("R007", DictCodeList.回购期限品种_R007.getValue());
        model.addAttribute("R014", DictCodeList.回购期限品种_R014.getValue());
        model.addAttribute("R021", DictCodeList.回购期限品种_R021.getValue());
        model.addAttribute("RC001", DictCodeList.回购期限品种_RC001.getValue());
        model.addAttribute("RC003", DictCodeList.回购期限品种_RC003.getValue());
        model.addAttribute("RC007", DictCodeList.回购期限品种_RC007.getValue());
        model.addAttribute("R_001", DictCodeList.回购期限品种_R_001.getValue());
        model.addAttribute("R_002", DictCodeList.回购期限品种_R_002.getValue());
        model.addAttribute("R_003", DictCodeList.回购期限品种_R_003.getValue());
        model.addAttribute("R_004", DictCodeList.回购期限品种_R_004.getValue());
        model.addAttribute("R_007", DictCodeList.回购期限品种_R_007.getValue());
        model.addAttribute("R_014", DictCodeList.回购期限品种_R_014.getValue());
        model.addAttribute("R_028", DictCodeList.回购期限品种_R_028.getValue());
        model.addAttribute("R_091", DictCodeList.回购期限品种_R_091.getValue());
        model.addAttribute("R_182", DictCodeList.回购期限品种_R_182.getValue());
        model.addAttribute("R1M", DictCodeList.回购期限品种_R1M.getValue());
        model.addAttribute("R2M", DictCodeList.回购期限品种_R2M.getValue());
        model.addAttribute("R3M", DictCodeList.回购期限品种_R3M.getValue());
        model.addAttribute("R4M", DictCodeList.回购期限品种_R4M.getValue());
        model.addAttribute("R6M", DictCodeList.回购期限品种_R6M.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE") == null ? "01" : CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("fintype_zhg", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("fintype_nhg", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
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
            EtrIbRepoinfo002Dto dto = etrIbRepoinfo002Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute("entity_bussRepoSecDto", JsonUtils.object2Json(dto.getBussRepoSec()));
//            if (StringUtil.isBlank(dto.getParentPortfolioId())) {
//                dto.setParentPortfolioId(etrCommonService.selectParentPortfolioIdByPortfolioId(
//                        dto.getPortfolioId()
//                ));
//            }
            model.addAttribute(GlobalConstant.ENTITY, dto);
            todoList = true;
            model.addAttribute("workflowId", workflowId);         
            model.addAttribute("businessId", dto.getEntrustId());
        }
        //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrIbRepoinfo002Dto entity = new EtrIbRepoinfo002Dto();
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            entity.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
            entity.setAssetType1(DictCodeList.回购类型_质押式协议回购.getValue());
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("businessId", IDGenerator.generate());
        }    
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("shortSellingSupported", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        model.addAttribute("TodoList", todoList);
        model.addAttribute("repo_pledge_model",CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        return super.init(model);
    }

    /**
          * 获取组合代码联想文本框信息 <功能详细描述>
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getentrustIdAdd")
    public Page<SelectOptionDto> getentrustIdAdd()
            throws Exception {
        Page<SelectOptionDto> temp = etrIbRepoinfo002Service.getentrustIdAdd(RequestUtil.getPageParam());
        return temp;
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
        Page<SelectOptionDto> temp = etrIbRepoinfo002Service.getfinprodIdForAdd(RequestUtil.getPageParam());
        return temp;
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
            ValidateGroupEdit.class}) EtrIbRepoinfo002Dto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        AjaxResult ar = new AjaxResult();
        // 可用现金软校验
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())
            && dto.getProAvailableCash() != null) {
           if (dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
              
               ar.setData(this.getMessage("TRD_C_0002"));
              // return ar;
           }
        }
        
        if(StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())
        		&&StringUtil.isNotEmpty(dto.getChlId()) &&StringUtil.isNotEmpty(dto.getChlIdCash())){
        	if (dto.getEntrustAmt().compareTo(dto.getChlIdCash()) == 1) {
        		if(StringUtil.isNotEmpty(ar.getData())){
        			ar.setData(this.getMessage("通道T日可用现金不足，")+ar.getData());
        		}else{
        			ar.setData(this.getMessage("通道T日可用现金不足，请确认是否继续？"));
        		}
            }
        }
        if(StringUtil.isNotEmpty(ar.getData())){
        	return ar;
        }

        // modify by wanshiwei ZGZGST-2657 7：指令阶段不check 20210917 start
        //光大PB子系统：添加委托金额校验--委托金额不低于10万 add by:mengpeng
//        if (dto.getEntrustAmt().compareTo(BigDecimal.valueOf(100000)) == -1){
//            ar.setData(this.getMessage("委托金额低于10万，是否继续？"));
//            return ar;
//        }
        // modify by wanshiwei ZGZGST-2657 7：指令阶段不check 20210917 end

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
    public AjaxResult save(@RequestBody EtrIbRepoinfo002Dto dto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        dto.setAssetType1(DictCodeList.回购类型_质押式回购.getValue());
        //设置默认的投资经理
        dto.setInvManagerId(LoginUserUtil.getUserName());

        if (StringUtil.equals(dto.getSettleSpreed(),
                DictCodeList.清算速度_T_0.getValue())) {
            dto.setSettleSpeed((long)0);
        }
        if (StringUtil.equals(dto.getSettleSpreed(),
                DictCodeList.清算速度_T_1.getValue())) {
            dto.setSettleSpeed((long) 1);
        }

        //万元转换 add  by yeming 20210817 start
        if(dto.getBussRepoSec() != null){
            if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
            if(CollectionUtils.isNotEmpty(dto.getBussRepoSec().getPledgeBondList())){
                for (PledgeBondInfoDto pledgeBond :  dto.getBussRepoSec().getPledgeBondList()) {
                    if (pledgeBond.getAvaPledgePrin() != null) {
                        pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                    }
                    if (pledgeBond.getPledgePrin() != null) {
                        pledgeBond.setPledgePrin(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                    }
                    if (pledgeBond.getPrin() != null) {
                        pledgeBond.setPrin(ArithmeticUtil.multiply(pledgeBond.getPrin(), new BigDecimal(10000)));
                    }
                }
            }
        }
        //万元转换 add  by yeming 20210817 end

        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_回购投资委托);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
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
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_质押式回购.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------预占功能 end----------
        ProcessParameter<EtrIbRepoinfo002Dto> processParameter = new ProcessParameter<EtrIbRepoinfo002Dto>();
        WorkFlowParameter<EtrIbRepoinfo002Dto> params = new WorkFlowParameter<EtrIbRepoinfo002Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_回购投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> this.getMessage("EtrIbRepoinfo002") + "(" + entity.getPortfolioId() + ")"
                        + "-" + this.getMessage("EtrIbRepoinfo002.ps") + "(" + SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, entity.getPs()) + ")" + "-" + "("
                        + this.getMessage("EtrIbRepoinfo002.vdate") + ":" + DateUtil.format(entity.getVdate())
                        + ")-(" + this.getMessage("EtrIbRepoinfo002.mdate") + ":"
                        + DateUtil.format(entity.getMdate()) + ")-"
                        + this.getMessage("EtrRepo002.entrustAmt") + ":"
                        + entity.getEntrustAmt());
        processParameter.setWorkFlowParameter(params);
        String msg = etrIbRepoinfo002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
    /**
     * 质押库：下拉框
     *
     * @param params 画面参数
     * @return 质押库：下拉框
     * @see
     * @since 1.0
     */
    @RequestMapping("getPledgeStockIdByPortfolioId")
    @ResponseBody
    public Page<SelectOptionDto> getPledgeStockIdByPortfolioId(@RequestParam Map<String, Object> params) {
        return etrIbRepoinfo002Service.getPledgeStockIdByPortfolioId(params);
    }
}
