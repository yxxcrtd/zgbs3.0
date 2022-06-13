package com.joyintech.tams.invest.portfolio.ptlfundallocation.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.dao.StlPayOrdersMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealMapper;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.common.base.entity.StlPayOrders;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.dto.PtlFundAllocationDto;
import com.joyintech.tams.invest.portfolio.ptlfundallocation.service.PtlFundAllocation002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 资金划拨新建
 */
@Controller
@PageId("PtlFundAllocation002")
@RequestMapping("/invest/portfolio/ptlfundallocation/PtlFundAllocation002")
public class PtlFundAllocation002Controller extends BaseController {
    /**
     * 资金划拨Service
     */
    @Autowired
    private PtlFundAllocation002Service ptlFundAllocation002Service;
    /**
     * 平台日期实体类
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     * 业务编号生成类
     */
    @Autowired(required = false)
    public BaseGetBusinessId baseGetBussinessId;

    @Autowired(required = false)
    public BussCommonMapperDelegateService bussCommonMapperDelegateService;

    /**
     * 重写init方法
     *
     * @param model 模型
     * @return 返回pageId
     */
    @Override
    public String init(Model model) {
        super.setSelectOptionToModel(model, DictCode.指令类型, DictCode.币种);
        model.addAttribute("Fin_Type", DictCodeList.共通业务类型_资金划拨管理.getValue());
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String orderId = getParam("orderId");
        String portfolioId = getParam("portfolioId");
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            PtlFundAllocationDto dto = ptlFundAllocation002Service.getBussinessEntityByWorkflowId(workflowId);
            //多组合银证转账特殊处理
            if (StringUtil.equals(dto.getPayType(), DictCodeList.交易方向_银证转账.getValue())) {
                dto.setPayType(DictCodeListExt1.指令类型_银证转账_银转证.getValue());
            } else if(StringUtil.equals(dto.getPayType(), DictCodeList.交易方向_证银转账.getValue())){
                dto.setPayType(DictCodeListExt1.指令类型_银证转账_证转银.getValue());
            }
            if (dto.getBussTrdTransferOrder() != null) {
                model.addAttribute("stlPayOrdersList", JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
            } else {
                model.addAttribute("tradeId", dto.getTradeId());
                dto.setBusiNo(dto.getOrderId());
            }
            model.addAttribute("entity", dto);
        } else if (StringUtil.isNotEmpty(orderId)) {
            PtlFundAllocationDto dto = new PtlFundAllocationDto();
            StlPayOrders stlPayOrders = bussCommonMapperDelegateService.selectBySinglePrimaryKey(StlPayOrdersMapper.class, orderId, StlPayOrders.class);
            BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
            if (stlPayOrders != null) {
                TrdProductDeal trdProductDeal = bussCommonMapperDelegateService.selectBySinglePrimaryKey(TrdProductDealMapper.class, stlPayOrders.getTradeId(), TrdProductDeal.class);
//                  dto.setPayType(trdProductDeal.getPs());
                if (StringUtil.equals(trdProductDeal.getPs(), DictCodeList.交易方向_银证转账.getValue())) {
                    dto.setPayType(DictCodeListExt1.指令类型_银证转账_银转证.getValue());
                } else {
                    dto.setMyAcctNo(stlPayOrders.getCounterAcctNo());
                    dto.setCounterAcctNo(stlPayOrders.getMyAcctNo());
                    dto.setPayType(DictCodeListExt1.指令类型_银证转账_证转银.getValue());
                }

                dto.setRemark(trdProductDeal.getRemark());
            }
            dto.setBusiNo(orderId);
//                dto.setAmount(new BigDecimal("0"));
            dto.setPortfolioId(portfolioId);
            model.addAttribute("tradeId", dto.getTradeId());
            model.addAttribute("entity", dto);
        }
        //add by lizhi 是否限制划款指令开关 2020年2月26日  start
        model.addAttribute("GEN_PAY_ORDERS_AUTO",CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        //add by lizhi 是否限制划款指令开关 2020年2月26日  end
        return super.init(model);
    }

    /**
     * 获取平台日期
     *
     * @return 平台日期字符串
     * @see
     * @since 1.0
     */
    @RequestMapping("/getInitDate")
    @ResponseBody
    public String getInitDate() {
        String dateStr = iPlatformDate.getSysDateStr() != null ? iPlatformDate.getSysDateStr() : DateUtil.format(new Date());
        return dateStr;
    }

    /**
     * 查询组合
     *
     * @return 组合集合
     * @see
     * @since 1.0
     */
    @RequestMapping("/getPortfolioId")
    @ResponseBody
    public List<SelectOptionDto> getPortfolioId() {
        return ptlFundAllocation002Service.getPortfolioId(RequestUtil.getPageParam());
    }

    /**
     * 查询收款账号
     *
     * @return 收款账号集合
     * @see
     * @since 1.0
     */
    @RequestMapping("/getCounterAcctNo")
    @ResponseBody
    public List<SelectOptionDto> getCounterAcctNo() {
        return ptlFundAllocation002Service.getCounterAcctNo(RequestUtil.getPageParam());
    }

    /**
     * 查询付款账号
     *
     * @return 付款账号集合
     * @see
     * @since 1.0
     */
    @RequestMapping("/getMyAcctNo")
    @ResponseBody
    public List<SelectOptionDto> getMyAcctNo() {
        return ptlFundAllocation002Service.getMyAcctNo(RequestUtil.getPageParam());
    }

    /**
     * 获取账户余额
     *
     * @return 账户余额
     * @see
     * @since 1.0
     */
    @RequestMapping("/getAmount")
    @ResponseBody
    public BigDecimal getAmount() {
        return ptlFundAllocation002Service.getAmount(RequestUtil.getPageParam());
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto    实体类
     * @param result
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) PtlFundAllocationDto dto, BindingResult result) {
        String str = "";
        String changeLineMark = "<br/>";
        if (!StringUtil.isNotEmpty(dto.getMyAcctNo())) {
            if (StringUtil.isNotBlank(str)) {
                str += changeLineMark;
            }
            str += getMessage("FW_E_0006", getMessage("PtlFundAllocation001.myAcctNos"));
        }
        if (!StringUtil.isNotEmpty(dto.getCounterAcctNo())) {
            if (StringUtil.isNotBlank(str)) {
                str += changeLineMark;
            }
            str += getMessage("FW_E_0006", getMessage("PtlFundAllocation001.counterAcctNo"));
        }
        if (!StringUtil.isNotEmpty(dto.getSettleAmt())) {
            if (StringUtil.isNotBlank(str)) {
                str += changeLineMark;
            }
            str += getMessage("FW_E_0006", getMessage("PtlFundAllocation001.settleAmt"));
        }
        if (StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
            if (StringUtil.isNotBlank(str)) {
                str += changeLineMark;
            }
            str += getMessage("FW_E_0006", getMessage("BussTrdTransferOrder"));
        }
        
        //划拨金额要大于0
        if (StringUtil.isNotEmpty(dto.getSettleAmt()) && 
                dto.getSettleAmt().compareTo(BigDecimal.ZERO)==0) {
            if (StringUtil.isNotBlank(str)) {
                str += changeLineMark;
            }
            str += getMessage("PTL_FUND_ALLOCATION_0001",getMessage("PtlFundAllocation001.settleAmts"));
        }
        // add by xingxiaokang 2019-03-09 划拨金额不能大于付款账户余额 start  
        if (StringUtil.isNotEmpty(dto.getSettleAmt()) && StringUtil.isNotEmpty(dto.getMyAcctAmount()) && dto.getSettleAmt().compareTo(dto.getMyAcctAmount())==1) {
            if (StringUtil.isNotBlank(str)) {
                str += changeLineMark;
            }
            str += getMessage("BUSS_COM_E_0003", getMessage("PtlFundAllocation001.settleAmts"),
                getMessage("PtlDvpFundAllocation001.myAcctAmount"));
        }
        // add by xingxiaokang 2019-03-09 划拨金额不能大于付款账户余额 end
        if (StringUtil.isNotEmpty(str)) {
            AjaxResult res = new AjaxResult();
            res.setMessage(str, MessageType.错误消息);
            return res;
        }
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     *
     * @param dto dto
     * @param br  br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public Message<AjaxResult> save(@RequestBody @Validated({
            ValidateGroupEdit.class}) PtlFundAllocationDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            AjaxResult message = new AjaxResult(br);
            new Message<AjaxResult>().success().setData(message);
        }
        try {
            if (StringUtil.isEmpty(dto.getBusiNo())) {
                BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
                bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资金划拨管理);
                dto.setTradeId(baseGetBussinessId != null ? baseGetBussinessId.buildBussinessCode(bussinessCodeParameter) : IDGenerator.generate());
                if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().get(0) != null) {
                    dto.setBusiNo(dto.getBussTrdTransferOrder().get(0).getOrderId());
                }
            }
            dto.setOrderId(dto.getBussTrdTransferOrder().get(0).getOrderId());
            if (bussCommonMapperDelegateService != null) {
                PtlPortfolio ptlPortfolio = bussCommonMapperDelegateService.selectBySinglePrimaryKey(PtlPortfolioMapper.class, dto.getPortfolioId(), PtlPortfolio.class);
                if (ptlPortfolio != null) {
                    dto.setPortfolioName(ptlPortfolio.getPortfolioName());
                }
            }
            ProcessParameter<PtlFundAllocationDto> processParameter = new ProcessParameter<PtlFundAllocationDto>();
            WorkFlowParameter<PtlFundAllocationDto> params = new WorkFlowParameter<PtlFundAllocationDto>();
            params.setPageId("PtlFundAllocation002");
            params.setElementId(dto.getElementId());
            params.setBusinessCode(item -> item.getPortfolioId());
            params.setBusinessType(DictCodeList.共通业务类型_资金划拨管理.getValue());
            // 设置流程业务说明, 组合名称 + "(" + 组合名称 + ")" + "-"  + "组合代码" + "("  + "组合代码"+")"
            params.setComment(
                    entity -> this.getMessage("PtlFundAllocation001.portfolioId") + "("
                            + entity.getPortfolioId() + ")" + "-"
                            + this.getMessage("PtlFundAllocation001.portfolioName") + "("
                            + entity.getPortfolioName() + ")"
                            + this.getMessage("PtlFundAllocation001.orderId") + "("
                            + entity.getBussTrdTransferOrder().get(0).getOrderId() + ")");
            processParameter.setWorkFlowParameter(params);
            //add wsz ZGCPCN-921 银证转账，添加捕捉头寸提交报系统异常。 start
            String msg = null;
            try {
                msg = ptlFundAllocation002Service.start(processParameter, dto);
                AjaxResult m = new AjaxResult(msg);
                return new Message<AjaxResult>().success().setData(m);
            } catch (Exception e) {
                return PositionUtil.ajaxErrorReturn(e);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return PositionUtil.ajaxErrorReturn(e);
        }
        //add wsz ZGCPCN-921 银证转账，提交报系统异常。 end
    }

    /**
     * 获取账户实际余额
     *
     * @return 账户余额
     * @see
     * @since 1.0
     */
    @RequestMapping("/getActAmount")
    @ResponseBody
    public BigDecimal getActAmount() {
        return ptlFundAllocation002Service.getActAmount(RequestUtil.getPageParam());
    }
}
