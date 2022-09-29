package com.joyintech.tams.invest.trade.transfer.currencytransfer.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.transfer.aop.RdsTransferAop;
import com.joyintech.tams.invest.trade.transfer.common.constant.CheckStatus;
import com.joyintech.tams.invest.trade.transfer.common.constant.ValuationStatus;
import com.joyintech.tams.invest.trade.transfer.common.dto.DeviationDegreeDto;
import com.joyintech.tams.invest.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.tams.invest.trade.transfer.common.service.TransferCommon001Service;
import com.joyintech.tams.invest.trade.transfer.fund.service.FundAssetTransfer001Service;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer002Dto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.service.NetPlanTransfer001Service;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.service.SecTransfer001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 货币型计划资产转仓
 *
 * @author liuwei
 * @version 1.0 2019年12月23日
 * @see CurrencyTransfer001Controller
 * @since 1.0
 */
@Controller
@PageId("CurrencyTransfer001")
@RequestMapping("/invest/trade/transfer/currencytransfer/CurrencyTransfer001")
public class CurrencyTransfer001Controller extends BaseController {
    /**
     * NetPlanTransfer001Service
     */
    @Autowired
    private NetPlanTransfer001Service netPlanTransfer001Service;
    /**
     * 注入业务主键生产类
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private TransferCommon001Service transferCommon001Service;
    @Autowired
    private SecTransfer001Service secTransfer001Service;
    @Autowired
    private FundAssetTransfer001Service fundAssetTransfer001Service;

    /**
     * 重写init方法，加载页面
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.币种);
        NetPlanTransfer001Dto entity = new NetPlanTransfer001Dto();
        Map<String, Object> paraMap = RequestUtil.getPageParam();
        String workflowId = getParam(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID);
        String portfolioId = (String) paraMap.get("portfolioId");

        //来自代办
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            entity = netPlanTransfer001Service.getBussinessEntityByWorkflowId(workflowId);
            // 设置到model中
            for (NetPlanTransfer002Dto info : entity.getNetPlanTransfer002DtoList()) {
                for (BussTrdTransferOrderDto bussDto : entity.getBussTrdTransferOrder()) {
                    info.setStlPayOrdersList(Arrays.asList(bussDto));
                }
            }
            model.addAttribute(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID, workflowId);
            model.addAttribute("entityData", JsonUtils.object2Json(entity.getNetPlanTransfer002DtoList()));
            model.addAttribute("bussTrdTransferOrder", JsonUtils.object2Json(entity.getBussTrdTransferOrder()));
            model.addAttribute("formTodoList", true);
        }
        //初始化界面
        else {
            entity.setTradeDate(DateUtil.platDateFromCache());
            entity.setFinprodMarketId(paraMap.get("finprodMarketId") == null ? null : (String) paraMap.get("finprodMarketId"));
            entity.setFinprodAbbr(paraMap.get("finprodAbbr") == null ? null : (String) paraMap.get("finprodAbbr"));
            entity.setCcy(paraMap.get("ccy") == null ? null : (String) paraMap.get("ccy"));
            entity.setChlName(paraMap.get("chlName") == null ? null : (String) paraMap.get("chlName"));
            entity.setChlId(paraMap.get("chlId") == null ? null : (String) paraMap.get("chlId"));
            entity.setPosAmt(paraMap.get("posAmt") == null ? null : new BigDecimal((String) paraMap.get("posAmt")));
//            //剩余可转仓数量为可用持仓，若存在流程中数据应扣减当前流程中转仓数据start
//            paraMap.put("searchDate", DateUtil.getSysDate());
//            BigDecimal wflAmt = netPlanTransfer001Service.selectWflAmtNet(paraMap);
//            if(wflAmt != null) {
//            	BigDecimal undistributedAmount = paraMap.get("undistributedAmount")==null?null : new BigDecimal((String) paraMap.get("undistributedAmount"));
//            	BigDecimal newUndistributedAmount = ArithmeticUtil.doAdd(undistributedAmount,wflAmt);
//            	entity.setUndistributedAmount(newUndistributedAmount);
//            }else {            	
//            	entity.setUndistributedAmount(paraMap.get("undistributedAmount")==null?null : new BigDecimal((String) paraMap.get("undistributedAmount")));
//            }
//            //剩余可转仓数量为可用持仓，若存在流程中数据应扣减当前流程中转仓数据end
            //add by mashukai 查询资产剩余可用持仓 参考计划类利率型交易 20210324 start 
            PtlSecPositionFlowDto dto = new PtlSecPositionFlowDto();
            dto.setHodingType(DictCodeList.持仓类型_可用.getValue());
            dto.setPortfolioId((String) paraMap.get("portfolioId"));
            dto.setFinprodId((String) paraMap.get("finprodId"));
            dto.setCdate(DateUtil.platDateFromCache());
            BigDecimal undistributedAmount = netPlanTransfer001Service.getPtlSecPosition(dto);
            entity.setUndistributedAmount(undistributedAmount);
            //add by mashukai 查询资产剩余可用持仓 参考计划类利率型交易 20210324 end
            paraMap.put("tradeDate", DateUtil.format(DateUtil.platDateFromCache()));
//            entity.setUnitValue(netPlanTransfer001Service.getUnitValue(paraMap));
            entity.setPortfolioIdOut(paraMap.get("portfolioId") == null ? null : (String) paraMap.get("portfolioId"));
            entity.setSecAcctId(paraMap.get("secAcctId") == null ? null : (String) paraMap.get("secAcctId"));
            entity.setCalendarId(paraMap.get("calendarId") == null ? null : (String) paraMap.get("calendarId"));
            entity.setFinprodId(paraMap.get("finprodId") == null ? null : (String) paraMap.get("finprodId"));
            entity.setType7(netPlanTransfer001Service.selectType7(entity.getFinprodId()));

            if (entity.getUnitValue() == null) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("tradeDate", DateUtil.format(DateUtil.addDay(DateUtil.platDateFromCache(), -1)));
                map.put("portfolioId", paraMap.get("portfolioId").toString());
                map.put("finprodId", paraMap.get("finprodId").toString());
                map.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
                map.put("invAim", "999999");
                BigDecimal unitCprice = secTransfer001Service.findUnitCprice(map);
                //单位净值默认为1
                entity.setUnitValue(new BigDecimal("1"));
                //entity.setUnitValue(Objects.nonNull(unitCprice) ? unitCprice.setScale(4, RoundingMode.HALF_UP) : new BigDecimal("0.00"));
            }
        }

        //组合估值状态
        if (StringUtil.isEmpty(portfolioId)) {
            portfolioId = entity.getPortfolioId();
        }
        PortfolioValuationDto valuationDto = transferCommon001Service.getPtlPortfolioValuation(portfolioId);
        entity.setCheckStatus(CheckStatus.getCheckStatusName(valuationDto.getCheckStatus()));
        entity.setValueStatus(ValuationStatus.getValuationStatusName(valuationDto.getValuationTableStatus()));

        model.addAttribute("entity", entity);

        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
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
     * 获取转仓信息—组合代码
     *
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId() {
        Map<String, Object> map = RequestUtil.getPageParam();
        map.put("userId", LoginUserUtil.getUserName());
        return netPlanTransfer001Service.getPortfolioId(map);
    }

    /**
     * 添加按钮按下前check
     *
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("addCheck")
    public AjaxResult addCheck(@RequestBody NetPlanTransfer001Dto dto, BindingResult br) {
        Map<String, Object> map = RequestUtil.getPageParam();
        AjaxResult ar = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        boolean flag = false;
        String message = "";
        if (StringUtil.isNotEmpty(dto)) {
            if (!StringUtil.isNotEmpty(dto.getPortfolioId())) {
                errorList.add(new ValidMessage("portfolioId", "请输入组合代码！"));
            }
            //产品组合在列表信息已存在
            else if (!StringUtil.isEmptyList(dto.getNetPlanTransfer002DtoList())) {
                for (NetPlanTransfer002Dto entity : dto.getNetPlanTransfer002DtoList()) {
                    if (StringUtil.equals(entity.getPortfolioId(), dto.getPortfolioId())) {
                        errorList.add(new ValidMessage("产品组合:" + dto.getPortfolioId() + "在转仓列表已存在！"));
                        break;
                    }
                }
            }
            //交易份额验证
            if (!StringUtil.isNotEmpty(dto.getTradeAmt())) {
                errorList.add(new ValidMessage("tradeAmt", "请输入交易份额！"));
            } else if (dto.getTradeAmt().compareTo(new BigDecimal(0)) <= 0) {
                errorList.add(new ValidMessage("tradeAmt", "交易份额必须大于0！"));
            }
            //单位净值验证
            if (!StringUtil.isNotEmpty(dto.getUnitValue())) {
                errorList.add(new ValidMessage("unitValue", "请输入单位净值！"));
            } else if (dto.getUnitValue().compareTo(new BigDecimal(0)) <= 0) {
                errorList.add(new ValidMessage("unitValue", "单位净值必须大于0！"));
            }
            //输入的交易份额 > 剩余待转仓份额
            if (StringUtil.isNotEmpty(dto.getTradeAmt()) && StringUtil.isNotEmpty(dto.getUndistributedAmount()) &&
                    dto.getTradeAmt().compareTo(dto.getUndistributedAmount()) > 0) {
                errorList.add(new ValidMessage("tradeAmt", "输入的交易份额不能大于" + dto.getUndistributedAmount().toString() + "!"));
            }
            if (!StringUtil.isNotEmpty(dto.getChlId()) && dto.getAvalibleCash() != null && StringUtil.isNotEmpty(dto.getTradeMoney()) &&
                    dto.getTradeMoney().compareTo(dto.getAvalibleCash()) > 0) {
                flag = true;
                message = getMessage("组合剩余现金不足，请确认是否继续添加？");
//                errorList.add(new ValidMessage("tradeMoney","组合剩余现金不足！"));
            }
        }
        if (StringUtil.isEmptyList(errorList)) {
            if (flag) {
                return new AjaxResult(message, MessageType.确认消息);
            } else {
                ar.setStatuCode(HttpStatus.OK.value());
            }
        } else {
            ar.setErrorList(errorList);
            ar.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return ar;
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto    dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody NetPlanTransfer001Dto dto, BindingResult result) {
        AjaxResult ar = new AjaxResult();
        String btnName = "";
        if (StringUtil.equalsIgnoreCase(OPERATE_TYPE_SAVE, dto.getOperateType())) {
            btnName = "SysCommon.save";
        } else if (StringUtil.equalsIgnoreCase(OPERATE_TYPE_SUBMIT, dto.getOperateType())) {
            btnName = "SysCommon.submit";
        }
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<>();
            for (NetPlanTransfer002Dto ptl : dto.getNetPlanTransfer002DtoList()) {
                sMap.put("chlId", dto.getChlId());
                sMap.put("counterId", ptl.getPortfolioId());
                count = trdBondInvestTrade002Service.selectchlTrade(sMap);
                if (count <= 0) {
                    if (!StringUtil.equals(str, "")) {
                        str += "</br>";
                    }
                    str += "交易对手(" + ptl.getPortfolioId() + ")通过一次性通道投资请先进行划款！";
                }
            }
            if (StringUtil.isNotEmpty(str)) {
                ar.setMessage(str, MessageType.错误消息);
                return ar;
            }
        }
        if (!result.hasErrors()) {
            if (StringUtil.isEmptyList(dto.getNetPlanTransfer002DtoList())) {
                return new AjaxResult("转仓列表不能为空！", MessageType.错误消息);
            } else {
                if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                    ar.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    ar.setMessage("指令列表信息不能空", MessageType.错误消息);
                    return ar;
                }
                if (dto.getNetPlanTransfer002DtoList().size() != dto.getBussTrdTransferOrder().size()) {
                    ar.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    ar.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
                    return ar;
                }
                Map<String, BussTrdTransferOrderDto> bussMap = dto.getBussTrdTransferOrder().stream().collect(Collectors.toMap(BussTrdTransferOrderDto::getInPortfolioId, v1->v1));
                for (NetPlanTransfer002Dto info : dto.getNetPlanTransfer002DtoList()) {
                    if (null == bussMap.get(info.getPortfolioId()) || bussMap.get(info.getPortfolioId()).getSettleAmt().compareTo(info.getTradeMoney()) != 0) {
                        ar.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        ar.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
                        return ar;
                    }
                }
                Map<String, BigDecimal> map = new HashMap<>();
                List<String> list = new ArrayList<>();
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("finprodId", dto.getFinprodId());
                paramMap.put("portfolioIdOut", dto.getPortfolioIdOut());
                paramMap.put("tradeDate", DateUtil.format(dto.getTradeDate()));
                Boolean flag = false;
                for (NetPlanTransfer002Dto entity : dto.getNetPlanTransfer002DtoList()) {
                    if (!StringUtil.isNotEmpty(map.get("unitValue"))) {
                        map.put("unitValue", entity.getUnitValue());
                    } else if (map.get("unitValue").compareTo(entity.getUnitValue()) != 0) {
                        flag = true;
                        break;
                    }
                    if (StringUtil.isNotEmpty(map.get("unitValue"))) {
                        paramMap.put("portfolioId", entity.getPortfolioId());
                        List<TrdProductDeal> unitValueList = netPlanTransfer001Service.getUnitValueList(paramMap);
                        for (TrdProductDeal trdProductDeal : unitValueList) {
                            /*if(unitValueList.size() > 0 && !unitValueList.contains(entity.getUnitValue())){*/
                            if (StringUtil.isNotEmpty(trdProductDeal.getUnitCprice())) {
                                NumberFormat nf = NumberFormat.getNumberInstance();
                                nf.setMinimumFractionDigits(4);
                                String unitCprice = nf.format(trdProductDeal.getUnitCprice());
                                BigDecimal bd = new BigDecimal(unitCprice);
                                if (!bd.equals(entity.getUnitValue())) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                ar.setData(this.getMessage("COMMON_C_0001", this.getMessage(btnName)));
                // 项目类转仓，单位净值默认为1，不需要该校验
                /*if (flag) {
                    ar.setData(this.getMessage("多笔转仓交易中单位净值不一致，是否确认提交？"));
                } else {
                    ar.setData(this.getMessage("COMMON_C_0001", this.getMessage(btnName)));
                }*/
            }
        }
        return ar;
    }

    /**
     * 保存操作
     *
     * @param dto dto
     * @param br  BindingResult
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody NetPlanTransfer001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<NetPlanTransfer001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<NetPlanTransfer001Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        //转仓列表
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType()) &&
                !StringUtil.isEmptyList(dto.getNetPlanTransfer002DtoList())) {
            dto.getNetPlanTransfer002DtoList().forEach(netPlanTransfer002Dto -> {
                netPlanTransfer001Service.genFinProductDto(netPlanTransfer002Dto, dto);
            });
        }
        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            dto.setZcId(IDGenerator.generate());
        }
        params.setBusinessCode(dto.getZcId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> "交易编号：" + dto.getZcId() + ";资产名称：" +
                dto.getFinprodAbbr() + ";交易日：" + DateUtil.format(dto.getTradeDate()));
        processParameter.setWorkFlowParameter(params);

        //实占
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = RdsTransferAop.doListOccupy(dto);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        String msg = netPlanTransfer001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_净值型_交易);
        String index = baseGetBusinessId.buildBussinessCode(codeParameter);
        return index;
    }

    @ResponseBody
    @RequestMapping("/getUnitValue")
    public AjaxResult getUnitValue() {
        AjaxResult result = new AjaxResult();
        Map<String, Object> paraMap = RequestUtil.getPageParam();
        BigDecimal unitValue = netPlanTransfer001Service.getUnitValue(paraMap);
        if (StringUtil.isNotEmpty(unitValue)) {
            result.setData(unitValue);
        }
        return result;
    }

    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    /**
     * 划款指令生成实体类
     */
    @ResponseBody
    @RequestMapping("getEntity")
    public List<TrdProductDeal> getEntity() {
        Map<String, Object> map = RequestUtil.getParameters(getRequest());
        SecTransferDto transferOutDto = BeanUtil.mapToObject(map, SecTransferDto.class);
        List<SecTransferDto> transferInDtoList = JSONArray.parseArray(String.valueOf(map.get("dataList")), SecTransferDto.class);
        return netPlanTransfer001Service.generateOrdersNQs(transferOutDto, transferInDtoList);
    }
}
