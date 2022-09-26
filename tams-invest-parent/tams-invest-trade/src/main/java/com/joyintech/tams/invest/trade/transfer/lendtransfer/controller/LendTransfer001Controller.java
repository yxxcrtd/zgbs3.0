package com.joyintech.tams.invest.trade.transfer.lendtransfer.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.transfer.aop.RdsTransferAop;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.dto.LendTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.dto.LendTransfer002Dto;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.service.LendTransfer001Service;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拆借资产转仓
 *
 * @author liuwei
 * @version 1.0 2020年02月18日
 * @see LendTransfer001Controller
 * @since 1.0
 */
@Controller
@PageId("LendTransfer001")
@RequestMapping("/invest/trade/transfer/lendtransfer/LendTransfer001")
public class LendTransfer001Controller extends BaseController {

    /**
     * LendTransfer001Service
     */
    @Autowired
    private LendTransfer001Service lendTransfer001Service;

    /**
     * 注入业务主键生产类
     */
    @Autowired(required=false)
    private BaseGetBusinessId baseGetBusinessId;

    @Autowired
    private TrdBondInvestTrade002Service trdBondInvestTrade002Service;
    /**
     * 重写init方法，加载页面
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        //下拉框
        this.setSelectOptionToModel(model, DictCode.币种, DictCode.回购拆借方向,DictCode.计息基础,DictCode.拆借类型);
        Map<String,Object> paraMap = RequestUtil.getPageParam();
        String workflowId = getParam(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID);
        LendTransfer001Dto entity = new LendTransfer001Dto();
        //来自代办
        if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            entity = lendTransfer001Service.getBussinessEntityByWorkflowId(workflowId);
            // 设置到model中
            model.addAttribute(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID,workflowId);
            model.addAttribute("entityData", JsonUtils.object2Json(entity.getLendTransfer002DtoList()));
            model.addAttribute("entity", entity);
            model.addAttribute("formTodoList", true);
        }
        //初始化界面
        else {
            if(StringUtil.isNotEmpty(paraMap.get("tradeId"))){
                String tradeId = (String)paraMap.get("tradeId");
                entity = lendTransfer001Service.getTradeInfo(tradeId);
                entity.setTradeDate(DateUtil.platDateFromCache());
                //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据start
                paraMap.put("portfolioId",entity.getPortfolioIdOut());
                if(StringUtil.isNotEmpty(entity.getChlId())){
                	paraMap.put("secAcctId",null);
                }
                else {
                	paraMap.put("secAcctId",entity.getSecAcctId());
                }
                paraMap.put("finprodId",entity.getTradeId());
                paraMap.put("invAim",entity.getInvAim());
                paraMap.put("ccy",entity.getCcy());
                paraMap.put("chlId",entity.getChlId());
                paraMap.put("searchDate", DateUtil.getSysDate());
                BigDecimal wflAmt = lendTransfer001Service.selectWflAmtLend(paraMap);
                if(StringUtil.isNotEmpty(paraMap.get("printAmt"))) {
                	if(wflAmt != null) {
                		BigDecimal newUndistributedAmount = ArithmeticUtil.doAdd(new BigDecimal((String) paraMap.get("printAmt")),wflAmt);
                		entity.setUndistributedAmount(newUndistributedAmount);
                	}else {               		
                		entity.setUndistributedAmount(new BigDecimal((String) paraMap.get("printAmt")));
                	}
                }
                //剩余可转仓本金金额，为可用持仓，若存在流程中数据应扣减当前流程中转仓数据end
            }
            model.addAttribute("entity", entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("A_360", DictCodeList.计息基础_A_360.getValue());
        model.addAttribute("A_365", DictCodeList.计息基础_A_365.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_01", DictCodeList.回购拆借方向_拆入.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_02", DictCodeList.回购拆借方向_拆出.getValue());
        model.addAttribute("FINPROD_TYPE_F08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        model.addAttribute("FINPROD_TYPE_F09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        model.addAttribute("BANK_LENDING_TYPE_02", DictCodeList.拆借类型_特殊目的载体.getValue());
        
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
     * 获取转仓信息—组合代码
     * @return 联想下拉框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        map.put("userId", LoginUserUtil.getUserName());
        return lendTransfer001Service.getPortfolioId(map);
    }

    /**
     * 添加按钮按下前check
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("addCheck")
    public AjaxResult addCheck(@RequestBody LendTransfer001Dto dto, BindingResult br) {
        //Map<String, Object> map = RequestUtil.getPageParam();
        AjaxResult ar = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        boolean flag = false;
        String message = "";
        if (StringUtil.isNotEmpty(dto)) {
            if (!StringUtil.isNotEmpty(dto.getPortfolioId())) {
                errorList.add(new ValidMessage("portfolioId", "请输入组合代码！"));
            }
            //产品组合在列表信息已存在
            else if(!StringUtil.isEmptyList(dto.getLendTransfer002DtoList())){
                for(LendTransfer002Dto entity : dto.getLendTransfer002DtoList()){
                    if(StringUtil.equals(entity.getPortfolioId(),dto.getPortfolioId())){
                        errorList.add(new ValidMessage("产品组合:" + dto.getPortfolioId()+"在转仓列表已存在！"));
                        break;
                    }
                }
            }
            //本金金额验证
            if(!StringUtil.isNotEmpty(dto.getPrinAmt())){
                errorList.add(new ValidMessage("prinAmt","请输入本金金额！"));
            }
            else if(dto.getPrinAmt().compareTo(new BigDecimal(0)) <= 0){
                errorList.add(new ValidMessage("prinAmt","本金金额必须大于0！"));
            }
            //交易金额验证
            if(!StringUtil.isNotEmpty(dto.getTradeAmt())){
                errorList.add(new ValidMessage("tradeAmt","请输入交易金额！"));
            }
            else if(dto.getTradeAmt().compareTo(new BigDecimal(0)) <= 0){
                errorList.add(new ValidMessage("unitValue","交易金额必须大于0！"));
            }
            //应计利息验证
            if(!StringUtil.isNotEmpty(dto.getIntAmt())){
                errorList.add(new ValidMessage("intAmt","请输入应计利息！"));
            }
            //输入的本金金额 > 剩余待转仓份额
            if(StringUtil.isNotEmpty(dto.getPrinAmt()) && StringUtil.isNotEmpty(dto.getUndistributedAmount()) &&
                    dto.getPrinAmt().compareTo(dto.getUndistributedAmount()) > 0){
                errorList.add(new ValidMessage("tradeAmt","输入的本金金额不能大于" + dto.getUndistributedAmount().toString()+"!"));
            }
            if(dto.getAvalibleCash() != null && StringUtil.isNotEmpty(dto.getTradeAmt()) &&
                    dto.getTradeAmt().compareTo(dto.getAvalibleCash()) > 0){
                flag = true;
                message = getMessage("组合剩余现金不足，请确认是否继续添加？");
            }
        }
        if (StringUtil.isEmptyList(errorList)) {
            if(flag) {
                return new AjaxResult(message, MessageType.确认消息);
            }
            else {
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
     * @param dto dto
     * @return check结果
     * @see
     * @since 1.0
     */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody LendTransfer001Dto dto) {
		AjaxResult wr = new AjaxResult();
		if (StringUtil.isEmptyList(dto.getLendTransfer002DtoList())) {
			wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			wr.setMessage("转仓列表信息不能空", MessageType.错误消息);
			return wr;
		}
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<>();
            for (LendTransfer002Dto ptl : dto.getLendTransfer002DtoList()) {
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
            if(StringUtil.isNotEmpty(str)){
                wr.setMessage(str, MessageType.错误消息);
                return wr;
            }
        }
		StringBuilder tips = new StringBuilder();
		for (LendTransfer002Dto item : dto.getLendTransfer002DtoList()) {
			BigDecimal tradeAmt = item.getTradeAmt() == null ? BigDecimal.ZERO : item.getTradeAmt();
			BigDecimal prinAmt = item.getPrinAmt() == null ? BigDecimal.ZERO : item.getPrinAmt();
			BigDecimal intAmt = item.getIntAmt() == null ? BigDecimal.ZERO : item.getIntAmt();
			if (tradeAmt.compareTo(prinAmt.add(intAmt)) != 0) {
				tips.append("【" + item.getPortfolioId() + "】交易金额不等于本息和！</br>");
			}
		}
		if (StringUtil.isNotEmpty(tips.toString())) {
			wr.setData("请确认转仓列表, 是否继续提交？</br>" + tips.toString());
		} else {
			String btnName = "操作";
			if (StringUtil.equalsIgnoreCase("save", dto.getOperateType())) {
				btnName = "SysCommon.save";
			} else if (StringUtil.equalsIgnoreCase("submit", dto.getOperateType())) {
				btnName = "SysCommon.submit";
			}
			wr.setData(SpringContextUtil.getTextValue("COMMON_C_0001",
					new String[] { SpringContextUtil.getTextValue(btnName) }));
		}
		return wr;
	}

    /**
     * 保存操作
     *
     * @param dto dto
     * @param br BindingResult
     * @return 结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody LendTransfer001Dto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<LendTransfer001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<LendTransfer001Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessType(DictCodeList.共通业务类型_拆借投资交易.getValue());

        // 转仓编号
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_资产转仓);
            dto.setZcId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            dto.setZcId(IDGenerator.generate());
        }
        params.setBusinessCode(dto.getZcId());

        // 设置流程业务说明, entity即传入的dto
        params.setComment((entity) -> WorkFlowUtil.concatComment("交易编号" + GlobalConstant.OPEN_PARENTHESIS + dto.getZcId() + GlobalConstant.CLOSE_PARENTHESIS,
                "拆借类型" + GlobalConstant.OPEN_PARENTHESIS + SysDataUtil.getNameFromDataDict(DictCode.拆借类型, dto.getPaBussType()) + GlobalConstant.CLOSE_PARENTHESIS,
                "交易日期"+ GlobalConstant.OPEN_PARENTHESIS + DateUtil.format(dto.getTradeDate()) + GlobalConstant.CLOSE_PARENTHESIS));
        processParameter.setWorkFlowParameter(params);
        //转仓列表
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType()) &&
                !StringUtil.isEmptyList(dto.getLendTransfer002DtoList())) {
            dto.getLendTransfer002DtoList().forEach(lendTransfer002Dto -> {
                lendTransfer001Service.genFinProductDto(lendTransfer002Dto,dto);
            });
        }
        //实占
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = RdsTransferAop.doListOccupy(dto);
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        String msg = lendTransfer001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_拆借投资交易);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return index;
    }
}
