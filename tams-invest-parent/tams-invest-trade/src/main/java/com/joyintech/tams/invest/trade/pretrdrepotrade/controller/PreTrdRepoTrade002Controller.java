

package com.joyintech.tams.invest.trade.pretrdrepotrade.controller;

import com.joyintech.framework.ajax.AjaxResult;
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
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BussTrdAvailableCashQueryService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.tams.invest.trade.pretrdrepotrade.dto.PreTrdRepoTrade002Dto;
import com.joyintech.tams.invest.trade.pretrdrepotrade.dto.TrdInvestPersonInfoDto;
import com.joyintech.tams.invest.trade.pretrdrepotrade.service.PreTrdRepoTrade002Service;
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

import java.math.BigDecimal;
import java.util.*;

/**
 * 回购投资交易新建
 * 〈功能详细描述〉
 * @see PreTrdRepoTrade002Controller
 * @since 1.0
 */
@PageId("PreTrdRepoTrade002")
@RequestMapping("/invest/trade/pretrdrepotrade/PreTrdRepoTrade002")
@Controller
public class PreTrdRepoTrade002Controller extends BaseController {
	@Autowired
	private TradeCommonService tradeCommonService;
    /**
     * trdLendInvestTrade002Service
     */
    @Autowired
    private TrdRepoTrade003Service trdRepoTrade003Service;
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private TrdStandardBond001Service trdStandardBond001Service;

    @Autowired
    private PreTrdRepoTrade002Service preTrdRepoTrade002Service;
    /**
     * DateCalService
     */
    @Autowired
    private DateCalService dateCalService;
    @Autowired(required = false)
    private BussTrdAvailableCashQueryService bussTrdAvailableCashQueryService;

    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Override
    public String init(Model model) {

        // 向model添加下拉选项列表，及相关下拉选项常量
        this.setSelectOptions(model);


        String tradeId = this.getParam("tradeId");
        String workflowStatus = this.getParam("workflowStatus");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String entrustId = this.getParam("entrustId");
        PreTrdRepoTrade002Dto dto = new PreTrdRepoTrade002Dto();
        TrdInvestPersonInfoDto trdInvestPersonInfoDto = null;
        if (StringUtil.equals(workflowStatus, DictCodeListExt1.交易流程状态_流程中.getValue())
                || (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotBlank(workflowId))) {
            dto = preTrdRepoTrade002Service.getBussinessEntityByWorkflowId(
                    workflowId);
            //人员信息
            trdInvestPersonInfoDto = new TrdInvestPersonInfoDto();
            trdInvestPersonInfoDto.setCounterTrader(dto.getCounterTrader());
            trdInvestPersonInfoDto.setCounterId(dto.getCounterId());
            trdInvestPersonInfoDto.setTrader(dto.getTrader());
            trdInvestPersonInfoDto.setSecAcctId(dto.getSecAcctId());
            trdInvestPersonInfoDto.setRegistOrg(dto.getRegistOrg());
            model.addAttribute("validPortfolioId",DictCodeListExt1.是否_否.getValue());
            if (!StringUtil.isEmptyList(dto.getPledgeBondList()) && StringUtil.isNotEmpty(dto.getPledgeBondList().get(0).getEntrustId())){
                model.addAttribute("isEntrust",DictCodeListExt1.是否_是.getValue());
            }
        }else if (StringUtil.isNotEmpty(tradeId)) {
            dto = preTrdRepoTrade002Service.getPreTrdRepoTradeDto(tradeId);
            //人员信息
            trdInvestPersonInfoDto = new TrdInvestPersonInfoDto();
            trdInvestPersonInfoDto.setCounterTrader(dto.getCounterTrader());
            trdInvestPersonInfoDto.setCounterId(dto.getCounterId());
            trdInvestPersonInfoDto.setTrader(dto.getTrader());
            trdInvestPersonInfoDto.setRegistOrg(dto.getRegistOrg());
            trdInvestPersonInfoDto.setSecAcctId(dto.getSecManageAcctId());
            model.addAttribute("validPortfolioId",DictCodeListExt1.是否_否.getValue());
        }else if (StringUtil.isNotEmpty(entrustId)){
            dto = preTrdRepoTrade002Service.getPreTrdRepoTradeDtoFromEtr(entrustId);
            //人员信息
            trdInvestPersonInfoDto = new TrdInvestPersonInfoDto();
            trdInvestPersonInfoDto.setCounterTrader(dto.getCounterTrader());
            trdInvestPersonInfoDto.setCounterId(dto.getCounterId());
            trdInvestPersonInfoDto.setTrader(dto.getTrader());
            trdInvestPersonInfoDto.setRegistOrg(dto.getRegistOrg());
            trdInvestPersonInfoDto.setSecAcctId(dto.getSecManageAcctId());

            model.addAttribute("validPortfolioId",DictCodeListExt1.是否_否.getValue());
            //add by chenjiaxing 界面放开添加质押券按钮 start
            if(dto.getBussRepoSecPtl() != null
                    && dto.getBussRepoSecPtl().getPledgeBondList() != null
                    && dto.getBussRepoSecPtl().getPledgeBondListDetailed() != null
                    && dto.getBussRepoSecPtl().getPledgeBondListDetailed().size() > 0){
                model.addAttribute("isEntrust",DictCodeListExt1.是否_是.getValue());
            }
            //add by chenjiaxing 界面放开添加质押券按钮 end
        }

        model.addAttribute("noBranchNoValue", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        model.addAttribute("entity",  dto);
        // 人员信息
        model.addAttribute("entity_trdInvestPersonInfo",
            trdInvestPersonInfoDto == null ? null : JsonUtils.object2Json(trdInvestPersonInfoDto));
        model.addAttribute("entity_bussRepoSecDto",
                dto.getBussRepoSecPtl() == null ? null : JsonUtils.object2Json(dto.getBussRepoSecPtl()));
        model.addAttribute("formTodoList", !WorkFlowUtil.isNotFromToDoList());
        Date sysDate = DateUtil.platDateFromCache();
        String formatDate = DateUtil.format(sysDate);
        model.addAttribute("formatDate",formatDate);
        model.addAttribute("entrustId",entrustId);
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);

    }

    /**
     * 向model添加下拉选项列表，及相关下拉选项常量
     *
     * @param model model
     */
    private void setSelectOptions(Model model) {
        this.setSelectOptionToModel(model, DictCode.回购拆借方向, DictCode.回购类型,DictCode.结算方式, DictCode.清算类型,
            DictCode.交易场所, DictCode.清算速度, DictCode.有效状态, DictCode.币种, DictCode.计息基础,
            DictCode.计息基础,DictCode.回购期限品种);
    //页面默认
        model.addAttribute("deliveryType_default",DictCodeListExt1.结算方式_券款对付.getValue());
        model.addAttribute("deliveryType2_default",DictCodeListExt1.结算方式_券款对付.getValue());
        model.addAttribute("settleType_default",DictCodeListExt1.清算类型_全额清算.getValue());
        model.addAttribute("effTime_default","19:30");

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


    /**
     * 保存时校验
     *
     * @param dto    dto
     * @param result result
     * @return
     */
    @RequestMapping("check")
    @ResponseBody
    public AjaxResult check(@Validated({ValidateGroupEdit.class}) @RequestBody PreTrdRepoTrade002Dto dto, BindingResult result) {


        // 到期日如果是节假日 要提醒
        AjaxResult ajaxResult = preTrdRepoTrade002Service.checkConfirm(dto, result);
        if (ajaxResult != null) {
            return ajaxResult;
        }


        return commonCheck(result, dto.getOperateType());
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
    AjaxResult save(@RequestBody PreTrdRepoTrade002Dto dto) throws WorkFlowException {

        String msg = "";
        dto.setPledgeBondList(dto.getBussRepoSecPtl().getPledgeBondList());
        if (StringUtil.isEmpty(dto.getTotleTradeId())) {
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_正回购.getValue())) {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
                } else {
                    codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
                }
                dto.setTotleTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            } else {
                dto.setTotleTradeId(IDGenerator.generate());
            }
        }
        // 金融产品类型（投管分类）= 数据字典缓存.金融产品类型（投管用）数据字典（根据交易方向与回购类型来综合判断）
        if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())) {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        } else {
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        }
        ProcessParameter<PreTrdRepoTrade002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<PreTrdRepoTrade002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId("PreTrdRepoTrade002");
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        } else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId("PreTrdRepoTrade002");
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessType(DictCodeListExt1.共通业务类型_质押式回购交易预录入.getValue());
        workFlowParameter.setBusinessCode((entity) -> dto.getTotleTradeId());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("TrdRepoTrade002.tradeId") + GlobalConstant.OPEN_PARENTHESIS + entity.getTotleTradeId() + GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("TrdRepoTrade002.repoType") + GlobalConstant.OPEN_PARENTHESIS + "质押式回购预录入" + GlobalConstant.CLOSE_PARENTHESIS,
                getMessage("TrdRepoTrade002.tradeDate") + GlobalConstant.OPEN_PARENTHESIS + DateUtil.format(entity.getTradeDate()) + GlobalConstant.CLOSE_PARENTHESIS)
                );
        // 预录入交易信息处理
        preTrdRepoTrade002Service.prepareEntity(dto);
        msg = preTrdRepoTrade002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }




}
