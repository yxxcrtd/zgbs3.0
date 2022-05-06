/*
 * 文件名：TrdRepoTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdrepotrade.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.cstp.TrdRepoTradeCSTPDto;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.trade.common.controller.TrdCommonController;
import com.joyintech.tams.invest.trade.common.dto.EtrInvestInfoDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade002Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 回购投资交易新建
 * 〈功能详细描述〉
 *
 * @author 裴家俊
 * @version 1.0 2018年12月18日
 * @see TrdRepoTrade002Controller
 * @since 1.0
 */
@PageId("TrdRepoTrade002")
@RequestMapping("/invest/trade/trdrepotrade/TrdRepoTrade002")
@Controller
public class TrdRepoTrade002Controller extends BaseController {
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * trdLendInvestTrade002Service
     */
    @Autowired
    private TrdRepoTrade002Service trdRepoTrade002Service;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    
    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired(required = false)
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;
    
    @Autowired
    private TrdCommonController trdCommonController;
    

    @Override
    public String init(Model model) {
        // 向model添加下拉选项列表，及相关下拉选项常量
        this.setSelectOptions(model);
        String repoamt = this.getParam("repoamt");
        String tradeId = this.getParam("tradeId");
        String workflowStatus = this.getParam("workflowStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String isCstp = this.getParam("isCstp");
        String isCstpSure = this.getParam("isCstpSure");
        String isSubmit = this.getParam("isSubmit");
        String tranRecord = this.getParam("tranRecord");



        // add by etliu 20210501 银行间场内场外拆分 start
        model.addAttribute("parentPageId", this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.回购类型, this.getParam("parentPageId"));
        // add by etliu 20210501 银行间场内场外拆分 end

        TrdRepoTrade002Dto dto = new TrdRepoTrade002Dto();

        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdRepoTrade002Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute("riskTrialRetDto", StringUtil.isEmptyList(dto.getRiskTrialRetDto()) ? null : JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("workflowId", workflowId);
            if (StringUtil.isBlank(dto.getTradeId())) {
                // 根据工作流代码查询交易编号
                dto.setTradeId(trdRepoTrade002Service.selectTradeIdByWorkflowId(workflowId));
            }

            // add by yeming 20210914 银行间场内场外拆分 start
            model.addAttribute("parentPageId", dto.getParentPageId());
            PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, dto.getParentPageId());
            PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.回购类型, dto.getParentPageId());
            // add by yeming 20210914 银行间场内场外拆分 end
        }
        else if(StringUtil.isNotEmpty(tranRecord)){
            model.addAttribute("isTranRecord", DictCodeList.是否_是.getValue());
            dto=JsonUtils.json2Object(tranRecord,TrdRepoTrade002Dto.class);
        }

        else if (StringUtil.isNotEmpty(tradeId)) {
            dto = trdRepoTrade002Service.getTrdRepoTradeDto(tradeId);

            if(dto!=null && StringUtil.isNotEmpty(dto.getrTradeId())){
                model.addAttribute("rTradeId",dto.getrTradeId());
            }

        } else {
            String entrustId = this.getParam("entrustId");
            if(StringUtil.isNotBlank(entrustId)){
                model.addAttribute("entrustId",entrustId);
            }
            String portfolioId = this.getParam("portfolioId");
            String preTradeId = this.getParam("preTradeId");//预录入
            if(StringUtil.isNotBlank(preTradeId)){
                model.addAttribute("preTradeId",preTradeId);
            }
            String ps = this.getParam("ps");
            dto.setEntrustId(StringUtil.isNotEmpty(entrustId) ? entrustId : null);
            dto.setPortfolioId(StringUtil.isNotEmpty(portfolioId) ? portfolioId : null);
            dto.setPs(StringUtil.isNotEmpty(ps) ? ps : null);
            if (StringUtil.isNotEmpty(entrustId)) {
	            BaseEtrInvestInfoDto  basedto = trdRepoTrade002Service.getEntrustInfo(entrustId);
	            if (StringUtil.isNotEmpty(basedto.getEtrEntrustAdd())){
	                dto.setSecAcctId(basedto.getEtrEntrustAdd().getSecAcctId());
	                dto.setChlId(basedto.getChlId());
	            }
            }
        }
        if (StringUtil.isNotBlank(tradeId)) {
            model.addAttribute("tradeId", tradeId);
        } else {
//        	model.addAttribute("tradeId", dto.getTradeId()==null?this.getParam("entrustId"):dto.getTradeId());
            model.addAttribute("tradeId", dto.getTradeId());
        }
        if (StringUtil.isNotBlank(workflowId)) {
            model.addAttribute("workflowId", workflowId);
        } else {
            model.addAttribute("workflowId", dto.getWorkflowId());
        }
        model.addAttribute("noBranchNoValue", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        //add by chenjiaxing 银行间下行交易费用匹配展示 start
        if(StringUtils.isNotBlank(this.getParam("procedureAmt")) && StringUtils.isNotBlank(this.getParam("fTradeId"))){
            dto.setProcedureAmt(new BigDecimal(this.getParam("procedureAmt")));
        }
        if(StringUtils.isNotBlank(this.getParam("settleAmt")) && StringUtils.isNotBlank(this.getParam("fTradeId"))){
            dto.setSettleAmt(new BigDecimal(this.getParam("settleAmt")));
        }
        //add by chenjiaxing 银行间下行交易费用匹配展示 end
        model.addAttribute("entity", dto);
        // 人员信息
        model.addAttribute("entity_trdInvestPersonInfo",
                dto.getTrdInvestPersonInfo() == null ? null : JsonUtils.object2Json(
                        dto.getTrdInvestPersonInfo()));
        // 添加质押券信息
        model.addAttribute("entity_bussRepoSecDto",
                dto.getBussRepoSec() == null ? null : JsonUtils.object2Json(
                        dto.getBussRepoSec()));
        // 划款信息
        if (dto != null && !StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            model.addAttribute("entity_bussRepFinProductInfoDto",
                    JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
        }
        model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
        Date sysDate = DateUtil.platDateFromCache();
        String formatDate = DateUtil.format(sysDate);
        model.addAttribute("formatDate",formatDate);
        if(StringUtils.isNotBlank(this.getParam("fTradeId"))){
            model.addAttribute("fTradeId",this.getParam("fTradeId"));
            //add by chenjiaxing 银行间下行交易费用匹配展示 start
            model.addAttribute("procedureAmt",this.getParam("procedureAmt"));
            model.addAttribute("settleAmtSS",this.getParam("settleAmt"));
            //add by chenjiaxing 银行间下行交易费用匹配展示 end
            model.addAttribute("repoamt",repoamt);
        }
        model.addAttribute("isCstp",isCstp);
        model.addAttribute("isCstpSure",isCstpSure);
        model.addAttribute("isSubmit",isSubmit);
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
        model.addAttribute("PINAN_ASSET_TYPE_F07",DictCodeList.平安资产类型_质押式回购.getValue());
        
		model.addAttribute("REPO_TYPE_R001", DictCodeList.回购期限品种_R001.getValue());
		model.addAttribute("REPO_TYPE_R007", DictCodeList.回购期限品种_R007.getValue());
		model.addAttribute("REPO_TYPE_R014", DictCodeList.回购期限品种_R014.getValue());
		model.addAttribute("REPO_TYPE_R021", DictCodeList.回购期限品种_R021.getValue());
		model.addAttribute("REPO_TYPE_R1M", DictCodeList.回购期限品种_R1M.getValue());
		model.addAttribute("REPO_TYPE_R2M", DictCodeList.回购期限品种_R2M.getValue());
		model.addAttribute("REPO_TYPE_R3M", DictCodeList.回购期限品种_R3M.getValue());
		model.addAttribute("REPO_TYPE_R4M", DictCodeList.回购期限品种_R4M.getValue());
		model.addAttribute("REPO_TYPE_R6M", DictCodeList.回购期限品种_R6M.getValue());
		model.addAttribute("REPO_TYPE_R9M", DictCodeList.回购期限品种_R9M.getValue());
		model.addAttribute("REPO_TYPE_R1Y", DictCodeList.回购期限品种_R1Y.getValue());
		PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        model.addAttribute("userId", LoginUserUtil.getUserName());
        return super.init(model);
    }

    /**
     * 向model添加下拉选项列表，及相关下拉选项常量
     *
     * @param model model
     */
    private void setSelectOptions(Model model) {
        this.setSelectOptionToModel(model, DictCode.回购拆借方向, DictCode.回购类型,
                /* 添加五级分类数据字典、zhuyongkang 2019/8/8  start*/
                DictCode.行内资产二级分类,
                DictCode.行内资产三级分类,
                DictCode.行内资产四级分类,
                DictCode.行内资产五级分类,
                /* 、zhuyongkang 2019/8/8  end*/
                DictCode.交易场所, DictCode.清算速度, DictCode.有效状态, DictCode.币种, DictCode.计息基础,
				DictCode.计息基础,DictCode.回购期限品种);

        // 交易场所_银行间
        model.addAttribute("ACCT_TYPE_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        // 交易场所_银行间
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        // 交易场所_上海证券交易所
        model.addAttribute("TRADE_PLACE_02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        // 交易场所_深圳证券交易所
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        // 回购拆借方向_正回购
        model.addAttribute("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
        // 回购拆借方向_逆回购
        model.addAttribute("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
        // 回购拆借方向_逆回购
        model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        // 回购拆借方向_逆回购
        model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        // 监督时分_交易监控
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        // 回购类型_质押式协议回购
        model.addAttribute("TARGET_BACK_TYPE_H01", DictCodeList.回购类型_质押式协议回购.getValue());
        // 回购类型_质押式竞价回购
        model.addAttribute("TARGET_BACK_TYPE_H02", DictCodeList.回购类型_质押式竞价回购.getValue());
        //add by yujiaping 2020-11-25 BS-1144 交易市场选择“银行间”，回购类型为“质押式回购”start
        // 回购类型_质押式回购
        model.addAttribute("TARGET_BACK_TYPE_H04", DictCodeList.回购类型_质押式回购.getValue());
        //add by yujiaping 2020-11-25 BS-1144 交易市场选择“银行间”，回购类型为“质押式回购”end
        // 清算速度_T_0
        model.addAttribute("ACCOUNT_SPEED_01", DictCodeList.清算速度_T_0.getValue());
        // 清算速度_T_1
        model.addAttribute("ACCOUNT_SPEED_02", DictCodeList.清算速度_T_1.getValue());
        // 计息基础_A_360
        model.addAttribute("BASIS_A_360", DictCodeList.计息基础_A_360.getValue());
        // 计息基础_A_365
        model.addAttribute("BASIS_A_365", DictCodeList.计息基础_A_365.getValue());
        // 是否_否
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        // 是否_是
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        // 交易方向_买入
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        // 资产类型_债券质押式逆回购
        model.addAttribute("FINPROD_TYPE_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        // 资产类型_债券质押式正回购
        model.addAttribute("FINPROD_TYPE_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        // 清算速度_T_1
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
        model.addAttribute("R9M", DictCodeList.回购期限品种_R9M.getValue());
        model.addAttribute("R1Y", DictCodeList.回购期限品种_R1Y.getValue());
		model.addAttribute("SETTLESPEED_1", DictCodeList.清算速度_T_1.getValue());
		model.addAttribute("SETTLESPEED_2", DictCodeListExt2.清算速度_T_2.getValue());
		//--------add by 王国显 费用设置 2019/07/19------start--------
        // 佣金
        model.addAttribute("FEE_TYPE_05", BussinessDictCodeList.费用类型_佣金.getValue());
        // 交易手续费
        model.addAttribute("FEE_TYPE_06", BussinessDictCodeList.费用类型_交易手续费.getValue());
        // 结算手续费
        model.addAttribute("FEE_TYPE_08", BussinessDictCodeList.费用类型_结算手续费.getValue());
        //--------add by 王国显 费用设置 2019/07/19------end--------

    }

    /**
     * 查询期限品种
     *
     * @param params 画面参数
     * @return 期限品种：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getTermTypeReback")
    @ResponseBody
    public List<SuggestDto> getTermTypeReback(@RequestParam Map<String, Object> params) {
        List<? extends DataDictItem> lstDict = SysDataUtil.getDataDictItem(DictCode.回购期限品种.getValue());
        List<SuggestDto> resultList = new ArrayList<SuggestDto>();
        if (!StringUtil.isEmptyList(lstDict)) {
            for (DataDictItem item : lstDict) {
                if (StringUtil.isNotEmpty(item.getParentId())) {
                    String[] array = item.getParentId().split("\\.");
                    if (array.length == 2) {
                        if ((!StringUtil.isNotEmpty(params.get("tradeMarket")) || StringUtil.equals(array[1], params.get("tradeMarket")))
                                && (item.getItemCode().indexOf((String) params.get("param")) >= 0
                                || item.getItemValue().indexOf((String) params.get("param")) >= 0)) {
                            SuggestDto option = new SuggestDto();
                            option.setHidCode(item.getItemCode());
                            option.setCode(item.getItemCode());
                            option.setName(item.getItemValue());
                            option.setShortName(item.getItemValue());
                            int days = 0;
                            if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC001.getValue())) {
                                days = 1;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC002.getValue())) {
                                days = 2;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC003.getValue())) {
                                days = 3;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC004.getValue())) {
                                days = 4;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC007.getValue())) {
                                days = 7;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC014.getValue())) {
                                days = 14;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC028.getValue())) {
                                days = 28;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC091.getValue())) {
                                days = 91;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_GC182.getValue())) {
                                days = 182;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_001.getValue())) {
                                days = 1;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_002.getValue())) {
                                days = 2;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_003.getValue())) {
                                days = 3;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_004.getValue())) {
                                days = 4;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_007.getValue())) {
                                days = 7;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_014.getValue())) {
                                days = 14;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_028.getValue())) {
                                days = 28;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_091.getValue())) {
                                days = 91;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R_182.getValue())) {
                                days = 182;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_RC001.getValue())) {
                                days = 1;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_RC003.getValue())) {
                                days = 3;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_RC007.getValue())) {
                                days = 7;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R001.getValue())) {
                                days = 1;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R007.getValue())) {
                                days = 7;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R014.getValue())) {
                                days = 14;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R021.getValue())) {
                                days = 21;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R1M.getValue())) {
                                days = 30;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R2M.getValue())) {
                                days = 60;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R3M.getValue())) {
                                days = 90;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R4M.getValue())) {
                                days = 120;
                            } else if (StringUtil.equals(item.getItemValue(), DictCodeList.回购期限品种_R6M.getValue())) {
                                days = 180;
                            }
                            option.setObject(days);
                            resultList.add(option);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 根据组合代码查询证券托管户：联想框文本框
     *
     * @param params 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecManageAcctId")
    @ResponseBody
    public List<SelectOptionDto> getSecManageAcctId(@RequestParam Map<String, Object> params) {
        return this.trdRepoTrade002Service.getSecManageAcctId(params);
    }
    
    /**
     * 推算到期日、实际占款天数
     * @return 到期日
     */
    @RequestMapping("getMdate")
    @ResponseBody
    public Map<String,Object> getMdate(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdRepoTrade002Service.getMdate(map);
    }
    /**
     * 计算两个日期之间天数
     * @return 日期区间
     */
    @RequestMapping("getBetweenDays")
    @ResponseBody
    public String getBetweenDays(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdRepoTrade002Service.getBetweenDays(map);
    }

    /**
     * 查询委托信息
     *
     * @param entrustId 委托编号
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfo")
    public BaseEtrInvestInfoDto getEntrustInfo(String entrustId) {
    	BigDecimal wflPrinAmt = tradeCommonService.getWflPrinAmt(entrustId, DictCodeList.平安资产类型_质押式回购.getValue(), null);
    	BaseEtrInvestInfoDto baseDto=this.trdRepoTrade002Service.getEntrustInfo(entrustId);
    	baseDto.getEtrEntrust().setEntrustAmt(ArithmeticUtil.subtract(baseDto.getEtrEntrust().getEntrustAmt(), wflPrinAmt));
        return baseDto;
    }

    /**
     * 组合代码、证券托管户、交易日、到期日改变时，查询可使用标准券额度
     *
     * @param params params
     * @return 可使用标准券额度
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAvaStandardStockPosition")
    public BigDecimal getAvaStandardStockPosition(@RequestParam Map<String, Object> params) {
        return this.trdRepoTrade002Service.getAvaStandardStockPosition(params);
    }


    /**
     * 获取下一个工作日
     *
     * @param params 画面参数
     * @return 下一个工作日
     * @see
     * @since 1.0
     */
    @RequestMapping("getNextWorkDay")
    @ResponseBody
    public String getNextWorkDay(@RequestParam Map<String, Object> params) {
        return trdRepoTrade002Service.getNextWorkDay(params);
    }
    
    /**
     * 检验是否节假日
     * @param map
     * @return
     */
    @RequestMapping("isDealCalendarHoliday")
    @ResponseBody
    public boolean isDealCalendarHoliday(@RequestBody Map<String, Object> map) {
        return dateCalService.isDealCalendarHoliday(DateUtil.parse((String)map.get("mpayDate")),(String)map.get("calendarId"));
    }

    /**
     * 保存时校验
     *
     * @param dto    dto
     * @param result result
     * @return
     */
    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@Validated({ValidateGroupEdit.class}) @RequestBody TrdRepoTrade002Dto dto, BindingResult result) {
    	if (result.hasErrors()) {
			return new AjaxResult(result);
		}
    	String confirmMsg ="";
    	String changeLineMark = "<br/>";
    	
    	/*//huwenbin 指令接收交易录单验证数据已存在流程中
        if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
			String str = tradeCommonService.check(dto.getEntrustId(), this.getPageId());
			if(StringUtil.isNotEmpty(str)){
				AjaxResult res= new AjaxResult();
				res.setMessage(str, MessageType.错误消息); 
				return res; 
			}
    	}*/
        // 质押式逆回购添加期限上限 365天	王贵奇 20190905
		if(dto.getTerm()>365) {
			AjaxResult err= new AjaxResult();
            err.setMessage(this.getMessage("TRD_E_0019"), MessageType.错误消息);
            return err;
		}
		// TISHKS-672:CLONE - 【交易管理】【场内交易】【场内质押式回购交易】新建页面，必填字段没有必填校验 start
		if (null == dto.getVpayDate() || null == dto.getMpayDate()) {
            AjaxResult err= new AjaxResult();
            err.setMessage(this.getMessage("TRD_E_0021"), MessageType.错误消息);
            return err;
        }
        // TISHKS-672:CLONE - 【交易管理】【场内交易】【场内质押式回购交易】新建页面，必填字段没有必填校验 end
        // 到期日如果是节假日 要提醒
        confirmMsg += trdRepoTrade002Service.checkConfirm(dto, result);
//        if (result != null && result.hasErrors()) {
//        	return commonCheck(result, dto.getOperateType());
//        }else{
        	if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())){
        		if (dto.getBussTrdAvailableCashQuery() != null && dto.getBussTrdAvailableCashQuery().getAvalibleCash() !=null){
        			if (StringUtil.isEmpty(dto.getChlId()) && dto.getPrinAmt().compareTo(dto.getBussTrdAvailableCashQuery().getAvalibleCash()) > 0){
//        				AjaxResult ar = new AjaxResult();
//        				ar.setData(this.getMessage("TRD_C_0002"));
//        				return ar;
        				if(StringUtil.isNotEmpty(confirmMsg)){
        					confirmMsg += changeLineMark;
        				}
                        if(!StringUtils.contains(confirmMsg,this.getMessage("TRD_C_0002"))){
                            confirmMsg +=this.getMessage("TRD_C_0002");
                        }
        			}
        			else if (StringUtil.isNotEmpty(dto.getChlId())
        					&& DictCodeListExt2.划款方式_一次性划款.getValue().equals(dto.getPayType())
        					&& dto.getTradeAmtAndFee() != null
        					&& dto.getTradeAmtAndFee().compareTo(dto.getBussTrdAvailableCashQuery().getChannelAvailableCash()) > 0) {
//        				AjaxResult ar = new AjaxResult();
//        				ar.setData(this.getMessage("TRD_C_0004",getMessage("invest.channelId")));
//        				return ar;
        				if(StringUtil.isNotEmpty(confirmMsg)){
        					confirmMsg += changeLineMark;
        				}
                        if(!StringUtils.contains(confirmMsg,this.getMessage("TRD_C_0004",getMessage("invest.channelId")))){
                            confirmMsg +=this.getMessage("TRD_C_0004",getMessage("invest.channelId"));
                        }
        			}
        		}
        	}
//        }

        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue()) && dto.getBussRepoSec() != null
            && dto.getBussRepoSec().getPledgeBondList() != null) {
            for (PledgeBondInfoDto pledgeBondInfoDto : dto.getBussRepoSec().getPledgeBondList()) {
                Map<String, Object> params = new HashMap<>();
                params.put("finprodId", pledgeBondInfoDto.getFinprodId());
                params.put("tradeDate", DateUtil.format(dto.getTradeDate()));
                params.put("mdate", DateUtil.format(dto.getMdate()));
                params.put("portfolioId", dto.getActPortfolioId());
                BigDecimal 融入交易融入的券面总额 = trdRepoTrade002Service.get融入交易融入的券面总额(params);
//                	（本次交易日期>=取得的融入交易的交易日期，本次交易的到期日>=取得的融入交易的到期日）的总的标的券券面总额，
//                券面总额>质押后的可用持仓，则提醒“下次融入到期时可用持仓不足”，可以保存

                if (融入交易融入的券面总额 != null
                        && ArithmeticUtil.subtract(pledgeBondInfoDto.getAvaPledgePrin(), pledgeBondInfoDto.getPledgePrin()).compareTo(融入交易融入的券面总额) < 0) {
                    confirmMsg += pledgeBondInfoDto.getBondAbbrName() + "("+pledgeBondInfoDto.getBondCode()+")" + "融入到期时，可用持仓不足<br/>";
                }
            }
        }
        // add by jiangpeng on 20210519 start TISHKBS-807
        if (StringUtil.equals(DictCodeList.回购拆借方向_逆回购.getValue(), dto.getPs())
                && (StringUtil.equals(dto.getTradeMarket(), DictCodeList.交易流通场所_银行间债券市场.getValue())
                    || ((StringUtil.equals(dto.getTradeMarket(), DictCodeList.交易流通场所_上海证券交易所.getValue())
                            ||StringUtil.equals(dto.getTradeMarket(), DictCodeList.交易流通场所_深圳证券交易所.getValue()))
                        && StringUtil.equals(dto.getRepoType(), DictCodeList.回购类型_质押式协议回购.getValue())))) {
            if (dto.getBussRepoSec() != null && dto.getBussRepoSec().getTotalPrinAmt() != null && !StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())) {
                BigDecimal prinAmt = dto.getPrinAmt() == null ? BigDecimal.ZERO : dto.getPrinAmt();//本金金额
                //券面总额以万元为单位
                BigDecimal totalPrinAmt = dto.getBussRepoSec() == null ? BigDecimal.ZERO : ArithmeticUtil.multiply(dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000));//券面总额
                if (totalPrinAmt.compareTo(prinAmt) < 0) {//券面总额小于本金金额
                    confirmMsg += this.getMessage("TRD_E_00124") + changeLineMark;
                }
            }
        }
        // add by jiangpeng on 20210519 end TISHKBS-807
    	if (!StringUtil.equals("null", confirmMsg) && confirmMsg.length() > 0) {
			AjaxResult wr = new AjaxResult();
			wr.setData(confirmMsg);
			return wr;
		} else {
			return commonCheck(result, dto.getOperateType());
		}	
        
    }

    /**
     * 保存处理
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @RequestMapping("save")
    @ResponseBody
    AjaxResult save(@RequestBody TrdRepoTrade002Dto dto) throws Exception {
        //万元转换 add  by yeming 20210817 start
        /*if(dto.getBussRepoSec() != null){
            if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
            }
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
        }*/
        //万元转换 add  by yeming 20210817 end
        if (StringUtil.isBlank(dto.getTradeId())) {
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(dto.getPs(),
                        DictCodeList.回购拆借方向_正回购.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                } else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                }
                dto.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                dto.setTradeId(IDGenerator.generate());
            }
        }
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        }
        ProcessParameter<TrdRepoTrade002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdRepoTrade002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        } else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        //----------add by jjpei 20200114 核风险版本 start----------
        if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
            // 选择的成交编号存在对应的报价，则不影响风险
            if (!(StringUtil.isNotBlank(dto.getfTradeId())
                    && StringUtil.equals(DictCodeList.是否_是.getValue(),
                    tradeCommonService.checkExecIdHasQuote(dto.getfTradeId())))) {
                AjaxResult ar=interfRiskRdsService.checkRiskRds(new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                        BussinessDictCodeListRds.接口系统ID_实占接口.getValue(),DictCodeList.平安资产类型_质押式回购.getValue(),
                        (en)->interfRiskRdsService.sendRiskRdsData(dto,en));
                if (StringUtil.isNotEmpty(ar)) {
                    return ar;
                }
                // add by zhangrui1 start
                // 将实占信息设置到线程缓存出错时撤销用
                TradeRdsCommonAop.setLocalCache(dto);
                // add by zhangrui1 end
            }
        }
        //----------add by jjpei 20200114 核风险版本 end----------
        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_质押式回购交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("TrdRepoTrade002.tradeId") + GlobalConstant.OPEN_PARENTHESIS + entity.getTradeId() + GlobalConstant.CLOSE_PARENTHESIS,
                DictCodeListExt1.交易场所_银行间.getValue().equals(dto.getTradeMarket())?"":getMessage("TrdRepoTrade002.repoType") + GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(DictCode.回购类型, entity.getRepoType()) + GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("TrdRepoTrade002.tradeDate") + GlobalConstant.OPEN_PARENTHESIS + DateUtil.format(entity.getTradeDate()) + GlobalConstant.CLOSE_PARENTHESIS));
        String msg = trdRepoTrade002Service.start(processParameter, dto);
        
        //授信占用额度
//        trdCommonController.delLinesDeal(dto.getRiskCreditListCommonDto(),dto.getTradeId());
        return new AjaxResult(msg);
    }
    
    // add by wangguoxian 费用设置 20190720 start
    /**
     * 
     * 计算交易费用，结算费，佣金
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("calFee")
    @ResponseBody
    public TrdRepoTrade002Dto calFee(@RequestBody TrdRepoTrade002Dto dto) {
        TrdRepoTrade002Dto result = trdRepoTrade002Service.calFee(dto);
        return result;
        
    }
    // add by wangguoxian 费用设置 20190720 end

    /**
     * 查询债券cstp交易下拉框
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("queryRepoCstpInfoList")
    public Page<SelectOptionDto> queryRepoCstpInfoList() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("prodType",BussinessDictCodeList.回购交易_质押式.getValue());
        Page<SelectOptionDto> suggestDtos = trdRepoTrade002Service.queryRepoCstpInfoList(param);
        return suggestDtos;
    }

    /**
     * 根据成交编号查询cstp交易信息
     * @param fedealno 成交编号
     * @return cstp交易信息
     */
    @ResponseBody
    @RequestMapping("queryRepoCstpInfoByNo")
    public TrdRepoTradeCSTPDto queryRepoCstpInfoByNo(@RequestBody  Map<String,Object> map){
        String prodType = BussinessDictCodeList.回购交易_质押式.getValue();
        return trdRepoTrade002Service.queryRepoCstpInfoByNo(map);
    }


    /**
     * check接口的劵面总额是否和有效和流程中的总和一致
     * @param params
     * @return
     */
    @RequestMapping("isAllTradeAmtByFTradeId")
    @ResponseBody
    public AjaxResult isAllTradeAmtByFTradeId(@RequestBody Map<String, Object> params){
        AjaxResult ajaxResult = new AjaxResult();
        boolean allTradeAmtByFTradeId = trdRepoTrade002Service.isAllTradeAmtByFTradeId(params);
        ajaxResult.setData(allTradeAmtByFTradeId);
        return ajaxResult;
    }
    /** @MethodName: 预录入判断金额是否和cstp里面的金额是一致的
    * @Description: TODO
    * @Param: [params]
    * @Return: com.joyintech.framework.ajax.AjaxResult
    * @Author: wangxuelei
    * @Date: 2020/2/17 0017
    **/
    @RequestMapping("isAllTradeAmtByPreTradeId")
    @ResponseBody
    public AjaxResult isAllTradeAmtByPreTradeId(@RequestBody Map<String, Object> params){
        AjaxResult ajaxResult = new AjaxResult();
        Boolean allTradeAmtByPreTradeId = trdRepoTrade002Service.isAllTradeAmtByPreTradeId(params);
        ajaxResult.setData(allTradeAmtByPreTradeId);
        return ajaxResult;
    }


    /**
     * 质押式回购银行间交易下行自动录单
     */
    @ResponseBody
    @PostMapping("repoAutoTrade")
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> repoAutoTrade(@RequestBody TrdRepoTrade002Dto repoTradeDto) {
        return trdRepoTrade002Service.repoAutoTrade(repoTradeDto);
    }
}
