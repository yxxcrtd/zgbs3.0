package com.joyintech.tams.invest.trade.transfer.fund.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.dto.BussTrdAvailableCashQuery;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.transfer.aop.RdsTransferAop;
import com.joyintech.tams.invest.trade.transfer.common.constant.CheckStatus;
import com.joyintech.tams.invest.trade.transfer.common.constant.ValuationStatus;
import com.joyintech.tams.invest.trade.transfer.common.dto.DeviationDegreeDto;
import com.joyintech.tams.invest.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.tams.invest.trade.transfer.common.service.TransferCommon001Service;
import com.joyintech.tams.invest.trade.transfer.fund.dto.FundAssetTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.fund.service.FundAssetTransfer001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

import io.swagger.annotations.ApiOperation;

/**
 * 基金资产转仓
 *
 * @author liupengfei
 */
@Controller
@PageId("FundAssetTransfer001")
@RequestMapping("/invest/trade/transfer/fund/FundAssetTransfer001")
public class FundAssetTransfer001Controller extends BaseController {
    /**
     * fundAssetTransfer001Service
     */
    @Autowired
    private FundAssetTransfer001Service fundAssetTransfer001Service;

    /**
     * 主键生成类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    @Autowired
    private TransferCommon001Service transferCommon001Service;

    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;

    /**
     * 重写init方法，加载页面
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        //下拉框
        this.setSelectOptionToModel(model, DictCode.币种, DictCode.交易方向);
        Map<String, Object> paraMap = RequestUtil.getPageParam();
        String tradeId = (String) paraMap.get("busiId");
        String pageModel = (String) paraMap.get("pageModel");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String portfolioId = getParam("portfolioId");
        FundAssetTransfer001Dto entity = null;
        Map<String, Object> param = RequestUtil.getPageParam();

        //来自代办
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            entity = fundAssetTransfer001Service.getBussinessEntityByWorkflowId(workflowId);

            portfolioId = entity.getInvTransPortf();
            //组合估值状态
            PortfolioValuationDto valuationDto = transferCommon001Service.getPtlPortfolioValuation(portfolioId);
            entity.setCheckStatus(CheckStatus.getCheckStatusName(valuationDto.getCheckStatus()));
            entity.setValueStatus(ValuationStatus.getValuationStatusName(valuationDto.getValuationTableStatus()));
            // 设置到model中
            model.addAttribute(GlobalConstant.WORKFLOW_ID, workflowId);
            model.addAttribute("entityData", JsonUtils.object2Json(entity.getFunAssetTransferDtoList()));
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("bussTrdTransferOrder", JsonUtils.object2Json(entity.getBussTrdTransferOrder()));
            model.addAttribute("formTodoList", true);
        }
        //初始化界面
        else if (StringUtil.equals(pageModel, GlobalConstant.DETAIL_PAGE_MODEL_NEW) && !StringUtil.isNotEmpty(workflowId)) {
            entity = fundAssetTransfer001Service.setData(param);
            //组合估值状态
            PortfolioValuationDto valuationDto = transferCommon001Service.getPtlPortfolioValuation(portfolioId);
            entity.setCheckStatus(CheckStatus.getCheckStatusName(valuationDto.getCheckStatus()));
            entity.setValueStatus(ValuationStatus.getValuationStatusName(valuationDto.getValuationTableStatus()));
            //非货基查询初始单位净值
            if (StringUtil.equals(entity.getFundType(), DictCodeList.基金类资产类型_非货币基金.getValue())) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("tradeDate", DateUtil.format(DateUtil.addDay(entity.getTradeDate(), -1)));
                paramMap.put("finprodId", entity.getFinprodIdOrg());
                paramMap.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
                paramMap.put("portfolioId", portfolioId);
                BigDecimal initCarryOverQty = fundAssetTransfer001Service.findInitCarryOverQty(paramMap);
                entity.setUnitCprice(Objects.nonNull(initCarryOverQty) ? initCarryOverQty.setScale(4, RoundingMode.HALF_UP) : new BigDecimal("0.00"));
            } else {
                entity.setUnitCprice(BigDecimal.ONE);
            }
            entity.setSecManageAcctName(MapUtils.getString(paraMap, "secAcctName"));
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("PS_T04", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("PS_T19", DictCodeList.交易方向_赎回确认.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("ps", entity.getPs());
        model.addAttribute("sysDate", DateUtil.getSysDate());
        model.addAttribute("FUND_ASSET_TYPE_F01", DictCodeList.基金类资产类型_货币基金.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
//        model.addAttribute("FINPROD_TYPE2_F02", DictCodeList.平安资产类型_基金.getValue());
        model.addAttribute("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());
        model.addAttribute("FUND_ASSET_TYPE_F02", DictCodeList.基金类资产类型_非货币基金.getValue());
        model.addAttribute("risk_math_switch", CacheUtil.getSysParam("risk_math_switch"));
        model.addAttribute(GlobalConstant.DETAIL_PAGE_MODEL_KEY, pageModel);

        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);
        return super.init(model);
    }

    @ResponseBody
    @PostMapping("/calculateDeviationDegree")
    @ApiOperation(value = "计算偏离度", notes = "计算偏离度")
    public Message<DeviationDegreeDto> calculateDeviationDegree(@RequestBody DeviationDegreeDto dto) {
        try {
            DeviationDegreeDto productDto = fundAssetTransfer001Service.calculateDeviationDegree(dto);
            return new Message<DeviationDegreeDto>().success().setData(productDto);
        } catch (Exception e) {
            return new Message<DeviationDegreeDto>().fail().setMessage("计算偏离度异常");
        }
    }

    /**
     * 划款指令生成实体类
     */
    @ResponseBody
    @RequestMapping("getEntity")
    public List<TrdProductDeal> getEntity(HttpServletRequest request) {
        Map<String, Object> map = RequestUtil.getParameters(getRequest());
        FundAssetTransfer001Dto dto = BeanUtil.mapToObject(map, FundAssetTransfer001Dto.class);
        List<FundAssetTransfer001Dto> dtoList = JSONArray.parseArray(String.valueOf(map.get("dataList")), FundAssetTransfer001Dto.class);
        List<TrdProductDeal> trdProductDealList = fundAssetTransfer001Service.generateOrders(dto, dtoList);
        return trdProductDealList;
    }

    /**
     * 添加校验
     */
    @ResponseBody
    @RequestMapping("addCheck")
    public AjaxResult addCheck(@RequestBody FundAssetTransfer001Dto fundDto) {
        AjaxResult result = new AjaxResult();

        List<ValidMessage> errorList = new ArrayList<>();
        List<FundAssetTransfer001Dto> listInfoDtoList = new ArrayList<>();
        FundAssetTransfer001Dto listInfoDto = new FundAssetTransfer001Dto();
        FundAssetTransfer001Dto dto = new FundAssetTransfer001Dto();

        if (!StringUtil.isNotEmpty(fundDto.getPortfolioId())) {
            errorList.add(new ValidMessage("portfolioId", "请输入组合代码！"));
        }
        if (!StringUtil.isNotEmpty(fundDto.getTradePrin())) {
            errorList.add(new ValidMessage("tradePrin", "请输入交易份额！"));
        }
        if (!StringUtil.isNotEmpty(fundDto.getUnitCprice())) {
            errorList.add(new ValidMessage("unitCprice", "请输入单位净值！"));
        } else {
            if (BigDecimal.ZERO.compareTo(fundDto.getTradePrin()) >= 0) {
                errorList.add(new ValidMessage("tradePrin", "交易份额必须大于0！"));
            }
        }
        BussTrdAvailableCashQuery bussTrdAvailableCashQuery = new BussTrdAvailableCashQuery();
        if (StringUtil.isNotEmpty(fundDto.getBussTrdAvailableCashQuery())) {
            bussTrdAvailableCashQuery = fundDto.getBussTrdAvailableCashQuery();
        }
        if (StringUtil.isNotEmpty(fundDto.getTradePrin()) && StringUtil.isNotEmpty(fundDto.getRollablePrin()) && fundDto.getTradePrin().compareTo(fundDto.getRollablePrin()) > 0) {
            errorList.add(new ValidMessage("tradePrin", "交易份额不能大于剩余可转仓份额！"));
        }
        if (errorList != null && errorList.size() > 0) {
            result.setStatuCode(500);
            result.setErrorList(errorList);
        } else {
            //转仓交易份额不能大于组合剩余本金
            if (StringUtil.isNotEmpty(fundDto.getTradeAmt()) && StringUtil.isNotEmpty(fundDto.getPortoAvailableAmt())) {
            //StringUtil.isNotEmpty(bussTrdAvailableCashQuery) && StringUtil.isNotEmpty(bussTrdAvailableCashQuery.getAvalibleCash())) {
                if (fundDto.getTradeAmt().compareTo(fundDto.getPortoAvailableAmt()) > 0) {
                    result.setMessage("组合剩余现金不足！", MessageType.确认消息);
                }
            }
            result.setStatuCode(200);
            listInfoDto = fundAssetTransfer001Service.setProjectListInfoDto(fundDto);
            listInfoDtoList.add(listInfoDto);
            dto.setFunAssetTransferDtoList(listInfoDtoList);
            result.setData(dto);
        }
        return result;
    }

    /**
     * 查询组合信息
     * * @return
     */
    @ResponseBody
    @RequestMapping("selectPortfolioId")
    public List<SelectOptionDto> selectPortfolioId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("userId", LoginUserUtil.getUserName());
        return fundAssetTransfer001Service.selectPortfolioId(param);
    }

    /**
     * 风险侧算
     *
     * @param model 参数
     * @return
     */
    @RequestMapping("riskMathComm")
    public String riskMathPage(Model model) {
        Map param = RequestUtil.getPageParam();
        model.addAttribute("param", param);
        List<RiskTrialRetDto> riskList = new ArrayList<>();
        if (StringUtil.isNotEmpty(param.get("results")) && !StringUtil.equals((String) param.get("results"), "{}")) {
            riskList = JSONArray.parseArray((String) param.get("results"), RiskTrialRetDto.class);
        }
        model.addAttribute("riskList", riskList);

        setSelectOptionToModel(model, DictCode.指标类型, DictCode.监督状态, DictCode.单位,
                DictCode.监督类型, DictCode.监督频率, DictCode.金融产品类型_投管用, DictCode.WIND一级分类_含债券一级分类等,
                DictCode.基金类资产二级类型, DictCode.资产评级, DictCode.申万行业一级类型, DictCode.指标类型映射编码);
        return "/trade/resolute/common/RiskMathComm";
    }

    /**
     * 保存时校验
     *
     * @param dto dto
     * @param rst result
     * @return
     */
    @RequestMapping("saveCheck")
    @ResponseBody
    public AjaxResult check(@RequestBody FundAssetTransfer001Dto dto, BindingResult rst) {
        AjaxResult result = new AjaxResult();
        String confirmMsg = "";
        List<ValidMessage> errorList = new ArrayList<>();
        if (StringUtil.isEmptyList(dto.getFunAssetTransferDtoList())) {
            errorList.add(new ValidMessage("列表信息不能空！"));
        } else {
            if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                result.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                result.setMessage("指令列表信息不能空", MessageType.错误消息);
                return result;
            }
            if (dto.getFunAssetTransferDtoList().size() != dto.getBussTrdTransferOrder().size()) {
                result.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                result.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
                return result;
            }
            Map<String, BussTrdTransferOrderDto> bussMap = dto.getBussTrdTransferOrder().stream().collect(Collectors.toMap(BussTrdTransferOrderDto::getInPortfolioId, v1->v1));
            for (FundAssetTransfer001Dto info : dto.getFunAssetTransferDtoList()) {
                if (null == bussMap.get(info.getPortfolioId()) || bussMap.get(info.getPortfolioId()).getSettleAmt().compareTo(info.getTradeAmt()) != 0) {
                    result.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    result.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
                    return result;
                }
            }
            //1) 本次提交的转仓交易中是否存在单位净值不一致的转仓交易数据；
            BigDecimal unitCprice = dto.getFunAssetTransferDtoList().get(0).getUnitCprice();
            for (FundAssetTransfer001Dto fundDto : dto.getFunAssetTransferDtoList()) {
                if (unitCprice.compareTo(fundDto.getUnitCprice()) != 0) {
                    confirmMsg = "多笔转仓交易中单位净值不一致，是否确认提交？";
                    break;
                }
            }
            //2) 本次提交的转仓交易和当天同一产品同一只资产转仓给其他产品组合的交易（包括已生效和新建流程中的交易数据），是否存在单位净值不一致的数据
            //若任何一项检查发现有不一致的转仓交易数据，则系统提示
            //查询单位净价
            if (confirmMsg == "") {
                List<String> unitCpriceList = fundAssetTransfer001Service.findUnitCprice(dto);
                for (String unitCpriceObj : unitCpriceList) {
                    BigDecimal Obj = new BigDecimal(unitCpriceObj);
                    if (unitCprice.compareTo(Obj) != 0) {
                        confirmMsg = "多笔转仓交易中单位净值不一致，是否确认提交？";
                        break;
                    }
                }
            }
        }
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<>();
            for (FundAssetTransfer001Dto ptl : dto.getFunAssetTransferDtoList()) {
                sMap.put("chlId", dto.getChlId());
                sMap.put("counterId", ptl.getPortfolioId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if (count <= 0) {
                    errorList.add(new ValidMessage("交易对手(" + ptl.getPortfolioId() + ")通过一次性通道投资请先进行划款！"));
                }
            }
        }
        if (errorList.size() > 0) {
            result.setErrorList(errorList);
            return result;
        }
        if (confirmMsg.length() > 0) {
            result.setData(confirmMsg);
            return result;
        } else {
            return commonCheck(rst, dto.getOperateType());
        }
    }

    /**
     * 新建/修改流程 <功能详细描述>
     *
     * @param dto
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody FundAssetTransfer001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<FundAssetTransfer001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FundAssetTransfer001Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        } else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_基金类资产交易.getValue());
        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            dto.setZcId(IDGenerator.generate());
        }
        workFlowParameter.setBusinessCode(dto.getZcId());

        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment("交易编号",
                entity.getZcId(), getMessage("TrdFundInvestTrade002.finprodName"), GlobalConstant.SPLIT, entity.getFinprodAbbr(),
                getMessage("TrdFundInvestTrade002.tradeDate"), DateUtil.format(entity.getTradeDate())));
        dto.setTrader(LoginUserUtil.getUserName());
        dto.setDeptCode(LoginUserUtil.getDeptCode());
        //实占
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = RdsTransferAop.doListOccupy(dto);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }

        if (StringUtil.equals(dto.getFundType(), DictCodeList.基金类资产类型_非货币基金.getValue())){
            List<FundAssetTransfer001Dto> dtoList = dto.getFunAssetTransferDtoList();
            if (CollectionUtils.isNotEmpty(dtoList)) {
                //计算偏离度
                DeviationDegreeDto degreeDto = new DeviationDegreeDto();
                degreeDto.setFinprodId(dto.getFinprodMarketId());
                BigDecimal valuation = fundAssetTransfer001Service.findValuation(degreeDto);
                if (Objects.nonNull(valuation)) {
                    for (FundAssetTransfer001Dto fundDto : dtoList) {
                        BigDecimal deviationDegree = fundDto.getUnitCprice().subtract(valuation)
                                .multiply(BigDecimal.valueOf(100)).divide(valuation, 2, RoundingMode.HALF_UP);
                        if (deviationDegree.compareTo(BigDecimal.ZERO) != 0) {
                            dto.setRiskFlag(DictCodeListExt1.是否_是.getValue());
                            break;
                        }
                    }
                }
            }
        }

        String msg = fundAssetTransfer001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
//    public Message ajaxErrorReturn(Exception e) {
//        String message = e.getMessage();
//        if (StringUtil.isNotEmpty(message)) {
//            String[] arr = message.split("\\|");
//            if (arr != null && arr.length == 2) {
//                String[] arr1 = arr[0].split("\\:");
//                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, arr1[1].trim()) ||
//                        StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, arr1[1].trim())) {
//                    // 头寸返回对象
//                    String a = arr[1];
//                    String b = a.replaceAll("&quot;", "\"");
//                    BaseMap data = JsonUtils.json2Object(b, BaseMap.class);
//
//                    StringBuffer strb = new StringBuffer("【<br/>");
//                    Map responseMap_data = (Map) data.get("data");
//                    if (responseMap_data != null) {
//                        List checkResults = (List) responseMap_data.get("checkResults");
//                        if (checkResults != null) {
//                            for (int i = 0; i < checkResults.size(); i++) {
//                                BaseMap flow = BaseMap.newMap((Map) checkResults.get(i));
//                                BigDecimal CHECKBALANCE = flow.getBigDecimal("CHECKBALANCE");
//                                String UNIT = flow.getString("UNIT");
//                                String PORTFOLIO_ID = flow.getString("PORTFOLIO_ID");
//                                String HODING_TYPE = flow.getString("HODING_TYPE");
//                                PtlPortfolio obj = ptlPortfolioMapper.selectByPrimaryKey(PORTFOLIO_ID);
//                                String portfolioName = "";
//                                if (obj != null) {
//                                    portfolioName = obj.getPortfolioName();
//                                } else {
//                                    portfolioName = PORTFOLIO_ID;
//                                }
//                                strb.append(PORTFOLIO_ID);
//                                strb.append("(");
//                                strb.append(portfolioName);
//                                strb.append(")");
////                                strb.append(CHECKBALANCE.toPlainString());
//                                strb.append("【");
//                                strb.append(SysDataUtil.getNameFromDataDict(DictCode.头寸资产类型, HODING_TYPE));
//                                strb.append("】余额不足，当前余额为：").append(CHECKBALANCE.toPlainString()).append(UNIT.toString());
//                                if ((i + 1) == checkResults.size()) {
//                                    strb.append("<br/>");
//                                } else {
//                                    strb.append("；<br/>");
//                                }
//                            }
//                        }
//                    }
//                    if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, arr1[1].trim())) {
//                        strb.append("】，请确认是否继续？");
//                        /**
//                         * 余额不足，软控确认
//                         */
//                        BaseMap bm = BaseMap.newMap()
//                                .put("code", arr1[1].trim())
//                                .put("message", strb)
//                                .put("data", data);
//                        return new Message().success().setData(bm);
//                    } else {
//                        strb.append("】");
//                        /**
//                         * 余额不足，硬控提示
//                         */
//                        return new Message().fail().setMessage(strb.toString());
//                    }
//                }
//            } else {
//                arr = message.split("\\：");
//                String b = "";
//                if (arr != null && arr.length == 2) {
//                    String msg1 = arr[1];
//                    if (msg1.startsWith("{") && msg1.endsWith("}")) {
//                        String[] array = arr[1].split(",");
//                        String m = "";
//                        for (String a : array) {
//                            if (a.contains("message")) {
//                                m = a;
//                            }
//                        }
//                        if (m.contains("=")) {
//                            b = m.split("=")[1];
//                        } else {
//                            b = m;
//                        }
//                    }
//                    String msg = arr[0] + "：" + b;
//                    return new Message().fail().setMessage(msg).setData(null);
//                }
//                return new Message().fail().setMessage(message).setData(null);
//            }
//        }
//        return new Message().fail().setMessage("无返回信息，未知异常").setData(null);
//    }
}
