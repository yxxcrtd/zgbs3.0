/*
 * 文件名：TrdRepoTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张庚林
 * 修改时间：2019年9月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.trade.trdrepotrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.trade.CeLinesUpdate;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesDealCommon;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.trdrepotrade.dto.TrdRepoTrade003Dto;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade002Service;
import com.joyintech.tams.invest.trade.trdrepotrade.service.TrdRepoTrade003Service;
import com.joyintech.tams.invest.trade.trdstandardbond.dto.TrdStandardBond001Dto;
import com.joyintech.tams.invest.trade.trdstandardbond.service.TrdStandardBond001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 回购投资交易新建
 * 〈功能详细描述〉
 *
 * @author 裴家俊
 * @version 1.0 2018年12月18日
 * @see TrdRepoTrade003Controller
 * @since 1.0
 */
@PageId("TrdRepoTrade003")
@RequestMapping("/invest/trade/trdrepotrade/TrdRepoTrade003")
@Controller
public class TrdRepoTrade003Controller extends BaseController {
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * trdLendInvestTrade002Service
     */
    @Autowired
    private TrdRepoTrade003Service trdRepoTrade003Service;

    @Autowired
    private TrdRepoTrade002Service trdRepoTrade002Service;
    
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private TrdStandardBond001Service trdStandardBond001Service;
    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;
    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;

    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

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
        TrdRepoTrade003Dto dto = new TrdRepoTrade003Dto();
        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = trdRepoTrade003Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute("riskTrialRetDto", StringUtil.isEmptyList(dto.getRiskTrialRetDto()) ? null : JsonUtils.object2Json(dto.getRiskTrialRetDto()));
            model.addAttribute("workflowId", workflowId);
            if (StringUtil.isBlank(dto.getTradeId())) {
                // 根据工作流代码查询交易编号
                dto.setTradeId(trdRepoTrade003Service.selectTradeIdByWorkflowId(workflowId));
            }
        } else if (StringUtil.isNotEmpty(tradeId)) {
            dto = trdRepoTrade003Service.getTrdRepoTradeDto(tradeId);
        } else {
            String entrustId = this.getParam("entrustId");
//            String entrustIds = this.getParam("entrustIds");
            String portfolioId = this.getParam("portfolioId");
//            trdRepoTrade003Service.dealEntrustInfo(entrustIds,dto);
            String ps = this.getParam("ps");
            dto.setEntrustId(StringUtil.isNotEmpty(entrustId) ? entrustId : null);
            dto.setPortfolioId(StringUtil.isNotEmpty(portfolioId) ? portfolioId : null);
            dto.setPs(StringUtil.isNotEmpty(ps) ? ps : null);
            BaseEtrInvestInfoDto  basedto = trdRepoTrade003Service.getEntrustInfo(entrustId);
            if (StringUtil.isNotEmpty(basedto.getEtrEntrustAdd())){
                dto.setSecAcctId(basedto.getEtrEntrustAdd().getSecAcctId());
                dto.setChlId(basedto.getChlId());
            }
        }
        if (StringUtil.isNotBlank(tradeId)) {
            model.addAttribute("tradeId", tradeId);
        } else {
        	model.addAttribute("tradeId", dto.getTradeId()==null?"":dto.getTradeId());
        }
        if (StringUtil.isNotBlank(workflowId)) {
            model.addAttribute("workflowId", workflowId);
        } else {
            model.addAttribute("workflowId", dto.getWorkflowId());
        }
        model.addAttribute("noBranchNoValue", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        model.addAttribute("entity", dto);
        // 人员信息
        model.addAttribute("entity_trdInvestPersonInfo",
                dto.getTrdInvestPersonInfo() == null ? null : JsonUtils.object2Json(
                        dto.getTrdInvestPersonInfo()));
        // 添加质押券信息
        model.addAttribute("entity_bussRepoSecDto",
                dto.getBussRepoSecPtl() == null ? null : JsonUtils.object2Json(
                        dto.getBussRepoSecPtl()));
        // 划款信息
        if (dto != null && !StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            model.addAttribute("entity_bussRepFinProductInfoDto",
                    JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
        }
        model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
        if(workflowId!=null){
            return "invest/trade/trdrepotrade/TrdRepoTrade002";
        }
        if(org.apache.commons.lang.StringUtils.isNotBlank(this.getParam("fTradeId"))){
            model.addAttribute("fTradeId",this.getParam("fTradeId"));
            model.addAttribute("repoamt",repoamt);
        }
        model.addAttribute("isCstp",isCstp);
        model.addAttribute("isCstpSure",isCstpSure);
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);

        // add by etliu 20210501 银行间场内场外拆分 start
        model.addAttribute("parentPageId", this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.回购类型, this.getParam("parentPageId"));
        // add by etliu 20210501 银行间场内场外拆分 end

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
        return this.trdRepoTrade003Service.getSecManageAcctId(params);
    }
    
    /**
     * 推算到期日、实际占款天数
     * @return 到期日
     */
    @RequestMapping("getMdate")
    @ResponseBody
    public Map<String,Object> getMdate(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdRepoTrade003Service.getMdate(map);
    }
    /**
     * 计算两个日期之间天数
     * @return 日期区间
     */
    @RequestMapping("getBetweenDays")
    @ResponseBody
    public String getBetweenDays(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return trdRepoTrade003Service.getBetweenDays(map);
    }

    /**
     * 查询委托信息
     *
     * @return 委托信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getEntrustInfo")
    public BaseEtrInvestInfoDto getEntrustInfo() {
        Map<String,Object> pageMap = RequestUtil.getPageParam();
        String entrustId = (String)pageMap.get("entrustId");
        BaseEtrInvestInfoDto ret =  trdRepoTrade003Service.getEntrustInfo(entrustId);
//        需要其他信息
        if(pageMap.containsKey("isNeedOth")&&StringUtil.equals((String)pageMap.get("isNeedOth"),"Y")&&ret.getBaseEtrInvestInfoDtoList()!=null&&
                ret.getBaseEtrInvestInfoDtoList().size()>0&&bussTrdAvailableCashQueryService != null){
                Map<String,Object> param = new HashMap<>();
                Map<String,Object> othInputMap = new HashMap<>();
                othInputMap.put("acctTypeList",DictCodeListExt1.资金账户类型_产品托管户.getValue());
                Date tradeDate = DateUtil.platDateFromCache();
                othInputMap.put("tradeDate",DateUtil.getSysDate());
                othInputMap.put("ccy",ret.getEtrEntrust().getCcy());
                othInputMap.put("ps",ret.getEtrEntrust().getPs());
                othInputMap.put("portfolioId",ret.getEtrEntrust().getPortfolioId());
                othInputMap.put("chlId",ret.getEtrEntrust().getChlId());
                othInputMap.put("secAcctId",ret.getEtrEntrustAdd().getSecAcctId());
                othInputMap.put("tradeMarket",ret.getEtrEntrust().getTradeMarket());
                String dateId = ret.getEtrEntrust().getCalendarId();
                String finprodId = ret.getEtrEntrust().getFinprodId();
                if (StringUtil.isBlank(dateId) && StringUtil.isNotBlank(finprodId)) {
                    dateId = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
                            finprodId, FinProduct.class).getCalendarId();
                }
                List<String> holidayList = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDay(tradeDate, 15), dateId);
                othInputMap.put("tradeDateNext", DateUtil.format(dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1")));
                for(BaseEtrInvestInfoDto temp : ret.getBaseEtrInvestInfoDtoList()){
                    String portfolioId = temp.getEtrEntrust().getPortfolioId();
                    othInputMap.put("portfolioId",portfolioId);
//                    单笔其他信息
                    Map<String, Object> othMap = new HashMap<>();
//                   委托信息
                    Map<String, Object> entrustOthInfo = bussTrdAvailableCashQueryService.getAvailableCash(othInputMap);
//                    组合名称
                    String portfolioName = trdRepoTrade003Service.getPortfolioNameById(othInputMap);
                    entrustOthInfo.put("portfolioName",portfolioName);
                    List<PledgeBondInfoDto> pledgeBondInfoList= new ArrayList<>();
                    //标准劵信息
                    if(StringUtils.equals(ret.getEtrEntrustAdd().getAssetType1(),DictCodeList.回购类型_质押式竞价回购.getValue())){
                        Date sysDate = DateUtil.platDateFromCache();
                        String formatDate = DateUtil.format(sysDate);
                        param.put("searchDate",formatDate);
                        param.put("portfolioId",temp.getEtrEntrust().getPortfolioId());
                        param.put("secAcctId",temp.getEtrEntrustAdd().getSecAcctId());
                        Page<TrdStandardBond001Dto> trdStandardBond001DtoPage = trdStandardBond001Service.selectPageData(param);
                        if(CollectionUtils.isNotEmpty(trdStandardBond001DtoPage.getRoot())){
                            //设置标准劵额度
                            if(trdStandardBond001DtoPage.getRoot().get(0).getAvaStrdPrinAmtSum()!=null){
                                temp.setAvaStandardStockPosition(trdStandardBond001DtoPage.getRoot().get(0).getAvaStrdPrinAmtSum());
                            }
                        }
                    }
                    for(EtrPledge tmpPledge : temp.getEtrPledgeList()){
 //                        质押券信息
                        othInputMap.put("finprodId",tmpPledge.getFinprodId());
                        PledgeBondInfoDto  tPledgeBondInfoDto = trdRepoTrade003Service.getPledgeBondInfoDto(othInputMap);
//                        委托代码
                        tPledgeBondInfoDto.setEntrustId(tmpPledge.getEntrustId());
//                        组合代码
                        tPledgeBondInfoDto.setPortfolioId(portfolioId);
//                        组合名称
                        tPledgeBondInfoDto.setPortfolioName(portfolioName);
//                        总额
                        tPledgeBondInfoDto.setPledgePrin(tmpPledge.getPledgeAmt());
//                        折算比例*100
                        tPledgeBondInfoDto.setPledgeRatio(ArithmeticUtil.multiply(tmpPledge.getPledgeRatio(),new BigDecimal("100")));
                        tPledgeBondInfoDto.setFinprodId(tmpPledge.getFinprodId());
                        pledgeBondInfoList.add(tPledgeBondInfoDto);
                    }
//                   完成情况处理统计剩余额度
                    BigDecimal finishAmt = BigDecimal.ZERO;
                    for(EtrFinishStatus tEtrFinishStatus :temp.getEtrFinishStatusList()){
                        ArithmeticUtil.doAdd(finishAmt,tEtrFinishStatus.getFinishAmt());
                    }
                    BigDecimal bigDecimal = trdBondInvestTrade002Service.selectDoEntrustMoney(temp.getEtrEntrust().getEntrustId(),temp.getEtrEntrust().getPortfolioId());
                    entrustOthInfo.put("entrustSurAmt",bigDecimal);
                    othMap.put("entrustOthInfo",entrustOthInfo);
//                    质押券的信息
                    othMap.put("pledgeBondInfoList",pledgeBondInfoList);
                    temp.setOthInfo(othMap);
                }
        }
        

        List<BaseEtrInvestInfoDto> baseEtrInvestInfoDtoList=ret.getBaseEtrInvestInfoDtoList();
        if(baseEtrInvestInfoDtoList!=null){
            for(int i=0;i<baseEtrInvestInfoDtoList.size();i++){
                if(baseEtrInvestInfoDtoList.get(i).getEtrEntrust()!=null){
                    BigDecimal doEntrustMoney = trdRepoTrade003Service.selectDoEntrustMoney(baseEtrInvestInfoDtoList.get(i).getEtrEntrust().getEntrustId());
                    BigDecimal entrustAmtBefore=ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().getEntrustAmt();
                    ret.getBaseEtrInvestInfoDtoList().get(i).getEtrEntrust().setEntrustAmt(doEntrustMoney);
                    if(ret.getBaseEtrInvestInfoDtoList().get(i).getOthInfo().containsKey("entrustOthInfo")){
                        Map<String, Object> entrustOthInfo = new HashMap<>();
                        entrustOthInfo=(Map<String, Object>)ret.getBaseEtrInvestInfoDtoList().get(i).getOthInfo().get("entrustOthInfo");
                        entrustOthInfo.put("entrustSurAmt",doEntrustMoney);
                        entrustOthInfo.put("entrustAmtBefore",entrustAmtBefore);
                        ret.getBaseEtrInvestInfoDtoList().get(i).getOthInfo().put("entrustOthInfo", entrustOthInfo);
                    }
                }
            }
        }
        
        return ret;
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
        return this.trdRepoTrade003Service.getAvaStandardStockPosition(params);
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
        return trdRepoTrade003Service.getNextWorkDay(params);
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
    public AjaxResult check(@Validated({ValidateGroupEdit.class}) @RequestBody TrdRepoTrade003Dto dto, BindingResult result) {
    	//huwenbin 指令接收交易录单验证数据已存在流程中
        if(StringUtil.isNotEmpty(dto.getEntrustId()) && WorkFlowUtil.isNotFromToDoList()){
    		String str = tradeCommonService.check(dto.getEntrustId(), "TrdRepoTrade002");
    		if(StringUtil.isNotEmpty(str)){
    			AjaxResult res= new AjaxResult();
				res.setMessage(str, MessageType.错误消息);
    			return res;
    		}
    	}
        
        boolean isCreditSoftControl = Boolean.parseBoolean(CacheUtil.getSysParam("IS_CREDIT_SOFTCONTROL")); 
        if (!isCreditSoftControl && dto.getPs().equals(DictCodeList.回购拆借方向_逆回购.getValue())) {
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(trdProductDeal,dto);
            trdProductDeal.setPs(DictCodeList.交易方向_买入.getValue());
            trdProductDeal.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
            CeLinesUpdate ceLinesUpdate = SpringContextUtil.getBean(CeLinesUpdate.class);
            AllTradeCommonInputDto inputDto =new AllTradeCommonInputDto();
            inputDto.setTrdProductDeal(trdProductDeal);
            inputDto.setRepoType(dto.getRepoType());
            List<CreditLinesDealCommon> list = ceLinesUpdate.isNeedOccupyCre(inputDto, true,false,false);
            if(list !=null && list.size()>0){
                AjaxResult ar = new AjaxResult();
                ar.setMessage(list.get(0).getErrMessage(), MessageType.错误消息);
                return ar;
            }
        }
    	
        // 到期日如果是节假日 要提醒
        AjaxResult ajaxResult = trdRepoTrade003Service.checkConfirm(dto, result);
        if (ajaxResult != null) {
            return ajaxResult;
        }
        
        if (result != null && result.hasErrors()) {
        	return commonCheck(result, dto.getOperateType());
        }else{
        	if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())){
        		/*if (dto.getBussTrdAvailableCashQuery() != null && dto.getBussTrdAvailableCashQuery().getAvalibleCash() !=null){
        			if (StringUtil.isEmpty(dto.getChlId()) && dto.getPrinAmt().compareTo(dto.getBussTrdAvailableCashQuery().getAvalibleCash()) > 0){
        				AjaxResult ar = new AjaxResult();
        				ar.setData(this.getMessage("TRD_C_0002"));
        				return ar;
        			}
        			else if (StringUtil.isNotEmpty(dto.getChlId())
        					&& DictCodeListExt2.划款方式_一次性划款.getValue().equals(dto.getPayType())
        					&& dto.getTradeAmtAndFee() != null
        					&& dto.getTradeAmtAndFee().compareTo(dto.getBussTrdAvailableCashQuery().getChannelAvailableCash()) > 0) {
        				AjaxResult ar = new AjaxResult();
        				ar.setData(this.getMessage("TRD_C_0004",getMessage("invest.channelId")));
        				return ar;
        			}
        		}*/
        		//多组合分配
                List<PledgeBondInfoDto> pledgeBondList=dto.getPledgeBondList();
                if(CollectionUtils.isNotEmpty(pledgeBondList)) {
                    for (PledgeBondInfoDto ptlDto : pledgeBondList) {
                        if (StringUtil.isNotEmpty(ptlDto.getAvalibleCash())&&StringUtil.isNotEmpty(ptlDto.getPrinAmt())){
                            if (StringUtil.isEmpty(dto.getChlId()) && ptlDto.getPrinAmt().compareTo(ptlDto.getAvalibleCash()) > 0){
                                AjaxResult ar = new AjaxResult();
                                ar.setData(this.getMessage("TRD_C_0002"));
                                return ar;
                            }
                            else if (StringUtil.isNotEmpty(dto.getChlId())
                                    && DictCodeListExt2.划款方式_一次性划款.getValue().equals(dto.getPayType())
                                    && StringUtil.isNotEmpty(ptlDto.getTradeAmt())&& StringUtil.isNotEmpty(ptlDto.getChannelAvailableCash())
                                    && ptlDto.getTradeAmt().compareTo(ptlDto.getChannelAvailableCash()) > 0) {
                                AjaxResult ar = new AjaxResult();
                                ar.setData(this.getMessage("TRD_C_0004",getMessage("invest.channelId")));
                                return ar;
                            }
                        }
                    }
                }
        	}
        }
        
        
        
        String confirmMsg ="";
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue()) && dto.getBussRepoSecPtl() != null
            && dto.getBussRepoSecPtl().getPledgeBondListDetailed() != null) {
            for (PledgeBondInfoDto pledgeBondInfoDto : dto.getBussRepoSecPtl().getPledgeBondListDetailed()) {
                Map<String, Object> params = new HashMap<>();
                params.put("finprodId", pledgeBondInfoDto.getFinprodId());
                params.put("tradeDate", DateUtil.format(dto.getTradeDate()));
                params.put("mdate", DateUtil.format(dto.getMdate()));
                params.put("portfolioId", pledgeBondInfoDto.getPortfolioId());
                BigDecimal 融入交易融入的券面总额 = trdRepoTrade002Service.get融入交易融入的券面总额(params);
//                  （本次交易日期>=取得的融入交易的交易日期，本次交易的到期日>=取得的融入交易的到期日）的总的标的券券面总额，
//                券面总额>质押后的可用持仓，则提醒“下次融入到期时可用持仓不足”，可以保存

                if (融入交易融入的券面总额 != null
                        && ArithmeticUtil.subtract(pledgeBondInfoDto.getAvaPledgePrin(), pledgeBondInfoDto.getPledgePrin()).compareTo(融入交易融入的券面总额) < 0) {
                    confirmMsg += pledgeBondInfoDto.getBondAbbrName() + "("+pledgeBondInfoDto.getBondCode()+")" + "融入到期时，可用持仓不足<br/>";
                }
            }
        }
        
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
    AjaxResult save(@RequestBody TrdRepoTrade003Dto dto) throws IOException, InstantiationException, WorkFlowException {
        String msg = "";
        dto.setPledgeBondList(dto.getBussRepoSecPtl().getPledgeBondList());
        dto.setPledgeBondListDetailed(dto.getBussRepoSecPtl().getPledgeBondListDetailed());

        List<TrdRepoTrade003Dto> trd003= new ArrayList<TrdRepoTrade003Dto>();
        for(int i=0;i<dto.getPledgeBondList().size();i++){
            String entrustId = "";
            if(StringUtils.isNotBlank(dto.getPledgeBondList().get(i).getEntrustId())){
                 entrustId = dto.getPledgeBondList().get(i).getEntrustId();
            }

            List<PledgeBondInfoDto>  dto1 = new ArrayList<>();
            List<PledgeBondInfoDto>  dto2 = new ArrayList<>();
//            List<BussTrdTransferOrderDto>  dto3 = new ArrayList<>();
            List<RiskTrialRetDto> risk= new ArrayList<>();
            
            dto1.addAll(dto.getPledgeBondList());
            dto2.addAll(dto.getPledgeBondListDetailed());
//            dto3.addAll(dto.getBussTrdTransferOrder());
            risk.addAll(dto.getRiskTrialRetDto());
            
            dto.setPortfolioId(dto.getPledgeBondList().get(i).getPortfolioId());
            dto.setSecAcctId(dto.getPledgeBondList().get(i).getSecAcctId());
            dto.setPrinAmt(dto.getPledgeBondList().get(i).getPrinAmt());
            dto.setRebackPrice(dto.getPledgeBondList().get(i).getRebackPrice());
            dto.setRebackIntAmt(dto.getPledgeBondList().get(i).getRebackIntAmt());
            dto.setMdateSettleAmt(dto.getPledgeBondList().get(i).getMdateSettleAmt());
            dto.setTradeAmt(dto.getPledgeBondList().get(i).getTradeAmt());
            dto.setTradeAmtAndFee(dto.getPledgeBondList().get(i).getTradeAmt());

            dto.setProcedureAmt(dto.getPledgeBondList().get(i).getTranFeeAmt());
            dto.setSettleAmt(dto.getPledgeBondList().get(i).getSettlementFee());
            dto.setCommissionAmt(dto.getPledgeBondList().get(i).getCommission());
            
            for(int j=0;j<risk.size();j++){
                if(dto.getPledgeBondList().get(i).getPortfolioId().equals(risk.get(j).getPortfolioId())){
                    risk.remove(j);
                    j--;
                }
            }
            
           for(int j=0;j<dto.getRiskTrialRetDto().size();j++){
                if(!dto.getPledgeBondList().get(i).getPortfolioId().equals(dto.getRiskTrialRetDto().get(j).getPortfolioCode())){
                    dto.getRiskTrialRetDto().remove(j);
                    j--;
                }
            }
            
            for(int j=0;j<dto2.size();j++){
                if(dto.getPledgeBondList().get(i).getPortfolioId().equals(dto2.get(j).getPortfolioId())){
                    dto2.remove(j);
                    j--;
                }
            }
            
            for(int j=0;j<dto.getPledgeBondListDetailed().size();j++){
                if(!dto.getPledgeBondList().get(i).getPortfolioId().equals(dto.getPledgeBondListDetailed().get(j).getPortfolioId())){
                    dto.getPledgeBondListDetailed().remove(j);
                    j--;
                }
            }
            
//            for(int j=0;j<dto3.size();j++){
//                if(dto.getPledgeBondList().get(i).getPortfolioId().equals(dto3.get(j).getPortfolioId())){
//                    dto3.remove(j);
//                    j--;
//                }
//            }
            
//            for(int j=0;j<dto.getBussTrdTransferOrder().size();j++){
//                if(!dto.getPledgeBondList().get(i).getPortfolioId().equals(dto.getBussTrdTransferOrder().get(j).getPortfolioId())){
//                    dto.getBussTrdTransferOrder().remove(j);
//                    j--;
//                }
//            }
            

//            for(int j=0;j<dto1.size();j++){
//                if(dto.getPledgeBondList().get(i).getPortfolioId().equals(dto1.get(j).getPortfolioId())){
//                    dto1.remove(j);
//                    j--;
//                }
//            }
            
//            for(int j=0;j<dto.getPledgeBondList().size();j++){
//                if(!dto.getPledgeBondList().get(i).getPortfolioId().equals(dto.getPledgeBondList().get(j).getPortfolioId())){
//                    dto.getPledgeBondList().remove(j);
//                    i--;
//                }
//            }
            
            dto.setBussRepoSec(dto.getBussRepoSecPtl());
            TrdRepoTrade003Dto dtonew=new TrdRepoTrade003Dto();
            BeanUtil.copyPropertiesIgnoreNull(dtonew, dto);
            dtonew.setPledgeBondList(dto.getPledgeBondListDetailed());
            dtonew.setEntrustId(entrustId);
            List<BussTrdTransferOrderDto> orderDto = new ArrayList<>();
            orderDto.add(dto.getBussTrdTransferOrder().get(i));
            dtonew.setBussTrdTransferOrder(orderDto);
            trd003.add(dtonew);
            dto.setEntrustId(entrustId);
            dto.setPledgeBondListDetailed(dto2);
//            dto.setBussTrdTransferOrder(dto3);
            dto.setPledgeBondList(dto1);
            dto.setRiskTrialRetDto(risk);
        }


        for(int i=0;i<trd003.size();i++){
                dto=trd003.get(i);
                if(dto.getPrinAmt()!=null&&dto.getPrinAmt().compareTo(new BigDecimal(0))>0){
                    dto.getBussRepoSec().setPledgeBondList(trd003.get(i).getPledgeBondListDetailed());
                    dto.getBussRepoSec().setTotalPrinAmt(new BigDecimal("0"));
                    for(PledgeBondInfoDto trddto : trd003.get(i).getPledgeBondListDetailed()) {
                    	dto.getBussRepoSec().setTotalPrinAmt(dto.getBussRepoSec().getTotalPrinAmt().add(
                    	        trddto.getPledgePrin().multiply(trddto.getPledgeRatio().divide(new BigDecimal(100))
                                )));
                    }
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
                    // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
                    if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
                        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                    } else {
                        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                    } 
                    //----------add by jjpei 20200114 核风险版本 start----------
                    List<TrdRepoTrade003Dto> dtoList = new ArrayList<>();
                    dtoList.add(dto);
                    if(dto.getOperateType().equals(BussinessConstant.SUBMIT)&& CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)){
                        AjaxResult ar =interfRiskRdsService.checkBatchRiskRds(dtoList,new InterfRiskRdsDto(), BussinessDictCodeListRds.事件类型_投资交易.getValue(),
                                BussinessDictCodeListRds.接口系统ID_实占接口.getValue(), BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(),DictCodeList.平安资产类型_质押式回购.getValue(),
                                (sm,en)->interfRiskRdsService.sendRiskRdsData(sm,en));
                        if (StringUtil.isNotEmpty(ar)) {
                            return ar;
                        }
                    }
                    //----------add by jjpei 20200114 核风险版本 end----------   
                    ProcessParameter<TrdRepoTrade003Dto> processParameter = new ProcessParameter<>();
                    WorkFlowParameter<TrdRepoTrade003Dto> workFlowParameter = new WorkFlowParameter<>();
                    processParameter.setWorkFlowParameter(workFlowParameter);
                    processParameter.setPageId("TrdRepoTrade002");
                    if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
                        processParameter.setOperationType(OperationType.NEW);
                    } else {
                        processParameter.setOperationType(OperationType.MODIFY);
                    }
//                    if(dto.getEntrustId()!=null&&dto.getEntrustId()!=""){
////                        dto.setTradeId(dto.getEntrustId());
////                        dto.setEntrustId(trd003.get(i).getPledgeBondListDetailed().get(0).getEntrustId());
//                    }
                    workFlowParameter.setPageId("TrdRepoTrade002");
                    workFlowParameter.setElementId(this.getParam("elementId"));
                    workFlowParameter.setBusinessCode((entity) -> entity.getTradeId());
                    workFlowParameter.setBusinessType(DictCodeList.共通业务类型_质押式回购交易.getValue());
                    workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                            getMessage("TrdRepoTrade002.tradeId") + GlobalConstant.OPEN_PARENTHESIS + entity.getTradeId() + GlobalConstant.CLOSE_PARENTHESIS,
                            getMessage("TrdRepoTrade002.repoType") + GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(DictCode.回购类型, entity.getRepoType()) + GlobalConstant.CLOSE_PARENTHESIS,
                            getMessage("TrdRepoTrade002.tradeDate") + GlobalConstant.OPEN_PARENTHESIS + DateUtil.format(entity.getTradeDate()) + GlobalConstant.CLOSE_PARENTHESIS));
                    
                    if(dto.getBussRepoSec()!=null&&dto.getBussRepoSec().getPledgeBondList()!=null){
                        for(int c=0;c<dto.getBussRepoSec().getPledgeBondList().size();c++){
                            dto.getBussRepoSec().getPledgeBondList().get(c).setFinprodId("F01_"+dto.getBussRepoSec().getPledgeBondList().get(c).getBondCode());
                        }
                    }
                    
                    msg = trdRepoTrade003Service.start(processParameter, dto);
                    dto.setTradeId(null);
                    dto.setWorkflowId(null);
                }
            }
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
    public TrdRepoTrade003Dto calFee(@RequestBody TrdRepoTrade003Dto dto) {
        TrdRepoTrade003Dto result = trdRepoTrade003Service.calFee(dto);
        return result;
        
    }
    // add by wangguoxian 费用设置 20190720 end
    
    // add by zhanggenglin 质押式回购多组合 20190813 start
    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return StringUtil.trimHtmlTag(index);
    }
    // add by zhanggenglin 质押式回购多组合 20190813 end
    
}
