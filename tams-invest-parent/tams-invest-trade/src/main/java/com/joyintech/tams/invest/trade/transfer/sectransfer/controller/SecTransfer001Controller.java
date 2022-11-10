package com.joyintech.tams.invest.trade.transfer.sectransfer.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.trade.transfer.aop.RdsTransferAop;
import com.joyintech.tams.invest.trade.transfer.common.constant.CheckStatus;
import com.joyintech.tams.invest.trade.transfer.common.constant.ValuationStatus;
import com.joyintech.tams.invest.trade.transfer.common.dto.DeviationDegreeDto;
import com.joyintech.tams.invest.trade.transfer.common.dto.PortfolioValuationDto;
import com.joyintech.tams.invest.trade.transfer.common.service.TransferCommon001Service;
import com.joyintech.tams.invest.trade.transfer.fund.dto.FundAssetTransfer001Dto;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.MstFileOutputParameter;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.tams.invest.trade.transfer.assign.dto.TrdAssetAssignDto;
import com.joyintech.tams.invest.trade.transfer.assign.service.TrdAssetAssignService;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.PtlFundAllocationDto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecResolute001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecResoluteDto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.service.SecTransfer001Service;
import com.joyintech.tams.invest.trade.transfer.sectransfer.service.TrdBondDuration001Service;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.service.TrdAssetsProject001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券交易转仓
 *
 * @author
 * @version 1.0
 * @see SecTransfer001Controller
 * @since 1.0
 */
@Controller
@PageId("SecTransfer001")
@RequestMapping("/invest/trade/transfer/sectransfer/SecTransfer001")
public class SecTransfer001Controller extends BaseController {

    /**
     * SecResolute001Service
     */
    @Autowired
    private SecTransfer001Service secTransfer001Service;

    @Autowired
    TrdAssetsProject001Service trdAssetsProject001Service;

    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * TrdBondDuration001Service 债券存续期service
     */
    @Resource(name = "trdBondDuration001ServiceEx")
    private TrdBondDuration001Service trdBondDuration001Service;

    /**
     * 业务编号生成类
     */
    @Autowired(required = false)
    public BaseGetBusinessId baseGetBussinessId;

    @Autowired
    private TrdAssetAssignService trdAssetAssignService;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;

    @Autowired
    private TransferCommon001Service transferCommon001Service;


    /**
     * 重写init方法，加载页面
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        //下拉框
        this.setSelectOptionToModel(model, DictCode.币种, DictCode.交易方向, DictCode.交易场所, DictCode.交易方向, DictCode.业务模式);
        Map<String, Object> paraMap = RequestUtil.getPageParam();
        String tradeId = (String) paraMap.get("tradeId");
        String portfolioId = (String) paraMap.get("portfolioId");
        String pageModel = (String) paraMap.get("pageModel");
        String workflowId = getParam(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID);
        String finprodMarketId = (String) paraMap.get("finprodMarketId");
        SecTransfer001Dto entity = null;

        //来自代办
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            entity = secTransfer001Service.getBussinessEntityByWorkflowId(workflowId);
            //组合估值状态
            PortfolioValuationDto valuationDto = transferCommon001Service.getPtlPortfolioValuation(entity.getInvestPortfolioId());
            entity.setCheckStatus(CheckStatus.getCheckStatusName(valuationDto.getCheckStatus()));
            entity.setValueStatus(ValuationStatus.getValuationStatusName(valuationDto.getValuationTableStatus()));
            // 设置到model中
            model.addAttribute(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID, workflowId);
            model.addAttribute("entity", JsonUtils.object2Json(entity));
            model.addAttribute("bussTrdTransferOrder", JsonUtils.object2Json(entity.getBussTrdTransferOrder()));
            for (SecTransferDto info : entity.getSecResoluteDtoList()) {
                for (BussTrdTransferOrderDto bussDto : entity.getBussTrdTransferOrder()) {
                    if (bussDto.getTradeId().equals(info.getStlPayId())) {
                        info.setStlPayOrdersList(Arrays.asList(bussDto));
                    }
                }
            }
            if (entity.getSecResoluteDtoList().size() != 0) {
                for (int i = 0; i < entity.getSecResoluteDtoList().size(); i++) {
                    if (StringUtil.equals(entity.getSecResoluteDtoList().get(i).getTradeMarket(), "01")) {
                        entity.getSecResoluteDtoList().get(i).setTradeMarket("银行间");
                    } else if (StringUtil.equals(entity.getSecResoluteDtoList().get(i).getTradeMarket(), "03")) {
                        entity.getSecResoluteDtoList().get(i).setTradeMarket("上海证券交易所");
                    } else if (StringUtil.equals(entity.getSecResoluteDtoList().get(i).getTradeMarket(), "04")) {
                        entity.getSecResoluteDtoList().get(i).setTradeMarket("深圳证券交易所");
                    } else if (StringUtil.equals(entity.getSecResoluteDtoList().get(i).getTradeMarket(), "05")) {
                        entity.getSecResoluteDtoList().get(i).setTradeMarket("机构间私募系统");
                    } else if (StringUtil.equals(entity.getSecResoluteDtoList().get(i).getTradeMarket(), "10")) {
                        entity.getSecResoluteDtoList().get(i).setTradeMarket("境外交易场所");
                    }
                    /*add by jiangpeng on 20210512 ZGBSSE-258*/
                    if (StringUtil.isNotEmpty(entity.getSecResoluteDtoList().get(i).getYield())) {
                        entity.getSecResoluteDtoList().get(i).setYield(ArithmeticUtil.multiply(entity.getSecResoluteDtoList().get(i).getYield(), new BigDecimal(100)));
                    }
                    if (StringUtil.isNotEmpty(entity.getSecResoluteDtoList().get(i).getExerYield())) {
                        entity.getSecResoluteDtoList().get(i).setExerYield(ArithmeticUtil.multiply(entity.getSecResoluteDtoList().get(i).getExerYield(), new BigDecimal(100)));
                    }
                    /*add by jiangpeng on 20210512 ZGBSSE-258*/
                }
            }
            model.addAttribute("entityData", JsonUtils.object2Json(entity.getSecResoluteDtoList()));
            model.addAttribute("formTodoList", true);
        }
        //初始化界面
        else if (!StringUtil.isNotEmpty(workflowId)) {
            Map<String, Object> map = new HashMap<>();
            map.put("finprodMarketId", finprodMarketId);
            map.put("portfolioId", portfolioId);
            map.put("cdate", DateUtil.getSysDate());
            map.put("invAim", MapUtils.getString(paraMap, "invAim"));
            if (StringUtil.isNotEmpty(paraMap.get("chlId"))) {
                map.put("secacctid", paraMap.get("chlId"));
                map.put("sec1acctid", paraMap.get("chlId"));
            } else if (StringUtil.isNotEmpty(paraMap.get("secAcctId"))) {
                map.put("secacctid", paraMap.get("secAcctId"));
                map.put("sec1acctid", "999999");
            } else {
                map.put("secacctid", null);
                map.put("sec1acctid", null);
            }
            entity = secTransfer001Service.selectTradeInfo(map);
            //资产代码
            entity.setFinprodMarketId((String) paraMap.get("finprodMarketId"));
            //资产名称
            entity.setFinprodAbbr((String) paraMap.get("finprodName"));
            // 是否行权
            entity.setIsRight(MapUtils.getString(paraMap, "isRight"));
            //本金金额
            entity.setShareAmt(new BigDecimal((String) paraMap.get("amount")));
            //剩余可转仓本金金额            
            //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据start
            paraMap.put("searchDate", DateUtil.getSysDate());
            BigDecimal wflAmt = trdAssetsProject001Service.selectWflAmtAsset(paraMap);
            BigDecimal wflAmt1 = ArithmeticUtil.multiply(wflAmt, new BigDecimal(100));
            if (wflAmt1 != null) {
                BigDecimal newUndistributedAmount = ArithmeticUtil.doAdd(entity.getShareAmt(), wflAmt1);
                entity.setUndistributedAmount(newUndistributedAmount);
            } else {
                entity.setUndistributedAmount(entity.getShareAmt());
            }
            //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据end

            //交易日期
            entity.setTradeDate(DateUtil.parse((String) paraMap.get("tradeDate")));
            //交割日期
            entity.setSettleDate(DateUtil.parse((String) paraMap.get("tradeDate")));
            //交易方向
            entity.setPs((String) paraMap.get("ps"));
            //币种
            entity.setCcy("CNY");
            //交易编号
            entity.setTradeId(tradeId);
            //待转仓产品组合
            entity.setInvestPortfolioId(portfolioId);
            //投资目的
            entity.setInvAim((String) paraMap.get("invAim"));
            entity.setCalendarId((String) paraMap.get("calendarId"));

            //组合估值状态
            PortfolioValuationDto valuationDto = transferCommon001Service.getPtlPortfolioValuation(portfolioId);
            entity.setCheckStatus(CheckStatus.getCheckStatusName(valuationDto.getCheckStatus()));
            entity.setValueStatus(ValuationStatus.getValuationStatusName(valuationDto.getValuationTableStatus()));

            Date vdate = new Date(MapUtils.getLongValue(paraMap, "vdate"));
            Date mdate = new Date(MapUtils.getLongValue(paraMap, "mdate"));
            entity.setVdate(vdate);
            entity.setMdate(mdate);
            /**
             * add by xr start 
             */
            //证券账户
            entity.setSecManageAcctId((String) paraMap.get("secAcctId"));
            //证券账户名
            entity.setSecManageAcctName((String) paraMap.get("secAcctName"));
            //通道代码
            entity.setChlId((String) paraMap.get("chlId"));
            //通道名称
            entity.setChlName((String) paraMap.get("chlName"));
            //组合代码
//            entity.setPortfolioId(portfolioId);
            /**
             * add by xr END  
             */
            //add by xuzhihao start
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(DateUtil.platDateFromCache());
//            calendar.add(Calendar.DATE, -1);
//            paraMap.put("sysDate", DateUtil.format(calendar.getTime(), "yyyy-MM-dd"));
//            paraMap.put("PROFIT_TYPE_02",DictCodeList.收益类型_收益型.getValue());
//            paraMap.put("FINPROD_TYPE_TG_F16",DictCodeListExt1.金融产品类型_投管用_理财产品.getValue().concat("_"));
            if (entity.getUnitCprice() == null) {
                map.put("tradeDate", DateUtil.format(DateUtil.addDay(DateUtil.platDateFromCache(), -1)));
                map.put("finprodId", paraMap.get("finprodId").toString());
                map.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
                BigDecimal unitCprice = secTransfer001Service.findUnitCprice(map);
                entity.setUnitCprice(Objects.nonNull(unitCprice) ? unitCprice.setScale(4, RoundingMode.HALF_UP) : new BigDecimal("0.00"));
            }
            //add by xuzhihao end
            Map<String, String> params = new HashMap<>();
            params.put("portfolioId", portfolioId);
            params.put("finprodId", paraMap.get("finprodId").toString());
            params.put("payDate", DateUtil.getSysDate());
            params.put("prinamt", entity.getShareAmt().toPlainString());
            TrdAssetAssignDto assignDto = trdAssetAssignService.calcAssignAmt(params);
            if (assignDto != null) {
                //entity.setAssignPrinamt(assignDto.getAssignPrinamt());
                entity.setAssignIntamt(assignDto.getAssignIntamt());
                entity.setLiBranch(assignDto.getLiBranch());
                entity.setIsNeedAssign(assignDto.getIsNeedAssign());
            }
        }
        //add by dengyuzhen 20201013 添加转仓投资目的 start
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.业务模式, null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> options = selectDto.getOptions();
            model.addAttribute("TURN_INV_AIM", JsonUtils.objectArray2Json(options.toArray()));
        }
        //add by dengyuzhen 20201013 添加转仓投资目的 end

        model.addAttribute(GlobalConstant.ENTITY, entity);
        model.addAttribute(GlobalConstant.DETAIL_PAGE_MODEL_KEY, pageModel);
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("FINPROD_TYPE_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

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
            DeviationDegreeDto productDto = secTransfer001Service.calculateDeviationDegree(dto);
            return new Message<DeviationDegreeDto>().success().setData(productDto);
        } catch (Exception e) {
            return new Message<DeviationDegreeDto>().fail().setMessage("计算偏离度异常");
        }
    }

    /**
     * 列表加载
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("querySecResoluteDtoList")
    public List<SecTransferDto> querySecResoluteDtoList() {
        String workflowId = getParam(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID);
        //来自代办
        if (StringUtil.isNotEmpty(workflowId)) {
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            SecTransfer001Dto entity = secTransfer001Service.getBussinessEntityByWorkflowId(workflowId);
            return entity.getSecResoluteDtoList();
        } else {
            return new ArrayList<SecTransferDto>();
        }
    }

    /**
     * 划款指令生成实体类
     */
    @ResponseBody
    @RequestMapping("getEntity")
    public List<TrdProductDeal> getEntity() {
        List<TrdProductDeal> trdProductDealList = new ArrayList<>();
        Map<String, Object> map = RequestUtil.getParameters(getRequest());
        SecTransferDto transferOutDto = BeanUtil.mapToObject(map, SecTransferDto.class);
        transferOutDto.setPortfolioId(MapUtils.getString(map, "investPortfolioId"));
        List<SecTransferDto> transferInDtoList = JSONArray.parseArray(String.valueOf(map.get("dataList")), SecTransferDto.class);
        trdProductDealList = secTransfer001Service.generateOrders(transferOutDto, transferInDtoList);
        return trdProductDealList;
    }


    /**
     * 获取资产代码
     *
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodMarketId")
    public List<SelectOptionDto> getFinprodMarketId() {
        Map<String, Object> map = RequestUtil.getPageParam();
        return secTransfer001Service.getFinprodMarketId(map);
    }

    /**
     * 获取拆分信息—组合代码
     *
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId() {
        Map<String, Object> map = RequestUtil.getPageParam();
        map.put("userId", LoginUserUtil.getUserName());
        return secTransfer001Service.getPortfolioId(map);
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
    public AjaxResult addCheck(@RequestBody SecResolute001Dto dto, BindingResult br) {
        AjaxResult ar = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();

        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
        if (StringUtil.isNotEmpty(dto)) {
            if (!StringUtil.isNotEmpty(dto.getPortfolioId())) {
                errorList.add(new ValidMessage(this.getMessage("portfolioId"), "请输入组合代码！"));
            }
            if (!StringUtil.isNotEmpty(dto.getTradeAmt())) {
                errorList.add(new ValidMessage(this.getMessage("tradeAmt"), "请输入券面总额！"));
            } else if (dto.getTradeAmt().compareTo(new BigDecimal(0)) <= 0) {
                errorList.add(new ValidMessage(this.getMessage("tradeAmt"), "券面总额必须大于0！"));
            }
            if (StringUtil.isNotEmpty(dto.getTradeAmt()) &&
                    dto.getTradeAmt().compareTo(dto.getUndistributedAmount()) > 0) {
                errorList.add(new ValidMessage("劵面总额不能大于剩余可转仓劵面总额！"));
            }
            /**
             * add by xr 全价总额不为0控制
             */
            if (!StringUtil.isNotEmpty(dto.getTradeRepAmtS())) {
                errorList.add(new ValidMessage(this.getMessage("fpriceAmt"), "全价总额不能为空！"));
            }
            if (!StringUtil.isNotEmpty(dto.getUnitCprice())) {
                errorList.add(new ValidMessage(this.getMessage("unitCprice"), "净价不能为空！"));
            } else if (dto.getUnitCprice().compareTo(new BigDecimal(0)) <= 0) {
                errorList.add(new ValidMessage(this.getMessage("unitCprice"), "净价必须大于0！"));
            }
            /**
             * add by xr 全价总额不为0控制
             */
   /*         if(StringUtil.isNotEmpty(dto.getTradeAmt()) && 
            		StringUtil.equals(dto.getPs(),DictCodeList.交易方向_卖出.getValue()) &&
                    ArithmeticUtil.divide(dto.getTradeAmt(),new BigDecimal(100),2).compareTo(dto.getAssetPosition()) > 0){
                errorList.add(new ValidMessage("资产剩余持仓不足！"));
            }*/
            if (dto.getSecResoluteDtoList() != null && dto.getSecResoluteDtoList().size() > 0 && StringUtil.isNotEmpty(dto.getPortfolioId())) {
                for (SecResoluteDto secResoluteDto : dto.getSecResoluteDtoList()) {
                    if (StringUtil.equals(dto.getPortfolioId(), secResoluteDto.getPortfolioId())) {
                        errorList.add(new ValidMessage("产品组合在转仓列表已存在！"));
                    }
                }
            }

            if (!StringUtil.isNotEmpty(dto.getTurnInvAim())) {
                errorList.add(new ValidMessage(this.getMessage("turnInvAim"), "请选择转仓业务模式！"));
            }
        }
        if (StringUtil.isEmptyList(errorList)) {
            if (StringUtil.isNotEmpty(dto.getTradeRepAmt()) &&
                    /*	StringUtil.equals(dto.getPs(),DictCodeList.交易方向_买入.getValue()) && */dto.getAvalibleCash() != null &&
                    dto.getTradeRepAmt().compareTo(dto.getAvalibleCash()) > 0) {
//                errorList.add(new ValidMessage(组合剩余现金不足！));
                ar.setMessage("组合剩余现金不足！", MessageType.确认消息);

            }
            ar.setStatuCode(HttpStatus.OK.value());
        } else {
            ar.setErrorList(errorList);
            ar.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return ar;
    }

    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody SecResolute001Dto dto) {
        AjaxResult wr = new AjaxResult();
        if (StringUtil.isEmptyList(dto.getSecResoluteDtoList())) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("转仓列表信息不能空", MessageType.错误消息);
            return wr;
        }
        if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("指令列表信息不能空", MessageType.错误消息);
            return wr;
        }
        if (dto.getSecResoluteDtoList().size() != dto.getBussTrdTransferOrder().size()) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
            return wr;
        }
        Map<String, BussTrdTransferOrderDto> bussMap = dto.getBussTrdTransferOrder().stream().collect(Collectors.toMap(BussTrdTransferOrderDto::getInPortfolioId, v1->v1));
        for (SecResoluteDto info : dto.getSecResoluteDtoList()) {
            if (null == bussMap.get(info.getPortfolioId()) || bussMap.get(info.getPortfolioId()).getSettleAmt().compareTo(info.getTradeRepAmt()) != 0) {
                wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                wr.setMessage("指令生成要素已变更，请重新生成划款指令", MessageType.错误消息);
                return wr;
            }
        }
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<String, Object>();
            for (PtlFundAllocationDto ptl : dto.getPtlFundAllocationDtos()) {
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
                wr.setMessage(str, MessageType.错误消息);
                return wr;
            }
        }
        //查询债券类资产存续期交易 状态为 01-待处理、03-流程中 的数据  2020-10-22 start
        /*Map<String, Object> params = new HashMap<String,Object>();
        params.put("finprodId", dto.getFinprodId());//金融产品代码
        params.put("finprodMarketId", dto.getFinprodMarketId());//市场代码
        params.put("mdateStart", DateUtil.format(dto.getSettleDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));//计息结束日开始
        params.put("mdateEnd", DateUtil.format(dto.getSettleDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));//计息结束日结束
        params.put("statusValid", "01,03");//数据状态
        
        Page<TrdBondDuration002ProcessDto> pageData = trdBondDuration001Service.findPage(params);
        if(pageData != null && pageData.getRoot() != null && !pageData.getRoot().isEmpty()) {
        	wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("该资产当天存在存续期记录，请先处理！", MessageType.错误消息);
            return wr;
        }*/
        //查询债券类资产存续期交易 状态为 01-待处理、03-流程中  2020-10-22 end
        BigDecimal unitCprice = dto.getSecResoluteDtoList().get(0).getUnitCprice();
        for (SecResoluteDto entity : dto.getSecResoluteDtoList()) {
            //判断页面上，明细里面是否有净价不一致
            if (entity.getUnitCprice().compareTo(unitCprice) != 0) {
                wr.setData("多笔转仓交易中净价不一致，是否确认提交");
                return wr;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("portfolioId", dto.getInvestPortfolioId());
            map.put("finprodId", dto.getFinprodId());
            map.put("invAim", dto.getInvAim());
            map.put("tradeDate", dto.getTradeDate());

            map.put("unitCprice", entity.getUnitCprice());
            map.put("turnInvAim", entity.getTurnInvAim());
            map.put("portfolioIdEn", entity.getPortfolioId());
            if (secTransfer001Service.selectYwmsData(map) > 0) {
                wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                wr.setMessage("同一个组合同一个资产的业务模式只能有一个！", MessageType.错误消息);
                return wr;
            }

            if (secTransfer001Service.selectCpriceData(map) > 0) {
                wr.setData("当天同一产品同一资产转仓给其他产品组合的净价不一致，是否确认提交？");
                return wr;
            }

            //判断流程中、有效数据中是否有同一个组合、债券、投资目的、交易日的转仓交易，净值不一样的情况
            if (secTransfer001Service.selectData(map) > 0) {
                wr.setData("多笔转仓交易中净价不一致，是否确认提交");
                return wr;
            }
        }
        if (HttpStatus.OK.value() == wr.getStatuCode()) {
            String btnName = "操作";
            if (StringUtil.equalsIgnoreCase("save", dto.getOperateType())) {
                btnName = "SysCommon.save";
            } else if (StringUtil.equalsIgnoreCase("submit", dto.getOperateType())) {
                btnName = "SysCommon.submit";
            }
            wr.setData(SpringContextUtil.getTextValue("COMMON_C_0001", new String[]{SpringContextUtil.getTextValue(btnName)}));
        }
        return wr;
    }

    /**
     * 保存提交
     *
     * @param dto
     * @return 画面输入参数
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody SecTransfer001Dto dto) throws Exception {
        ProcessParameter<SecTransfer001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<SecTransfer001Dto> workFlowParameter = new WorkFlowParameter<>();
        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(dto.getElementId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        } else {
            dto.setZcId(IDGenerator.generate());
        }
        workFlowParameter.setBusinessCode(dto.getZcId());

        // 设置流程业务说明, entity即传入的dto
        workFlowParameter.setComment(entity -> "交易编号：" + dto.getZcId() + ";资产名称：" +
                dto.getFinprodAbbr() + ";交易日：" + DateUtil.format(dto.getTradeDate()));
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());
        if (StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)) {
            processParameter.setOperationType(OperationType.NEW);
        } else {
            processParameter.setOperationType(OperationType.MODIFY);
        }
        //转仓列表生成交易编号
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType()) &&
                !StringUtil.isEmptyList(dto.getSecResoluteDtoList())) {
            //add by xuzhihao start
            dto.setTradeId(dto.getFinprodId());
            //add by xuzhihao end
            if (BussinessConstant.SUBMIT.equals(dto.getOperateType()) && !StringUtil.isEmptyList(dto.getSecResoluteDtoList())) {
                dto.getSecResoluteDtoList().forEach(secResoluteDto -> {
                    secTransfer001Service.prepareEntity(secResoluteDto, dto);
                    secResoluteDto.setBaseTradeInfoDtos(secTransfer001Service.genFinProductTradeDto(secResoluteDto, dto));
                });
            }
        }
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = RdsTransferAop.doListOccupy(dto);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }

        List<SecTransferDto> dtoList = dto.getSecResoluteDtoList();
        if (CollectionUtils.isNotEmpty(dtoList)) {
            //计算偏离度
            DeviationDegreeDto degreeDto = new DeviationDegreeDto();
            degreeDto.setFinprodId(dto.getFinprodMarketId());
            degreeDto.setTradePlace(dto.getTradeMarket());
            BigDecimal valuation = secTransfer001Service.findValuation(degreeDto);
            if (Objects.nonNull(valuation)) {
                for (SecTransferDto transferDto : dtoList) {
                    BigDecimal deviationDegree = transferDto.getUnitCprice().subtract(valuation)
                            .multiply(BigDecimal.valueOf(100)).divide(valuation, 2, RoundingMode.HALF_UP);
                    if (deviationDegree.compareTo(BigDecimal.ZERO) != 0) {
                        dto.setRiskFlag(DictCodeListExt1.是否_是.getValue());
                        break;
                    }
                }
            }
        }

        return new AjaxResult(secTransfer001Service.start(processParameter, dto));
    }

    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
        String index = baseGetBusinessId.buildBussinessCode(codeParameter);
        return index;
    }

    /**
     * 1.7计算按钮点击事件
     *
     * @param inputDto 画面参数
     * @return 计算结果
     * @see
     * @since 1.0
     */
    @RequestMapping("calculateData")
    @ResponseBody
    public AjaxResult calculateData(@RequestBody SecTransfer001Dto inputDto) {
        return secTransfer001Service.calculateData(inputDto);
    }

    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

}
