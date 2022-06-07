package com.joyintech.tams.invest.trade.transfer.buyoutrepo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.trade.bond.service.TrdBondInvestTrade002Service;
import com.joyintech.tams.invest.trade.transfer.buyoutrepo.dto.BuyOutRepoTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.buyoutrepo.dto.BuyOutRepoTransfer002Dto;
import com.joyintech.tams.invest.trade.transfer.buyoutrepo.service.BuyOutRepoTransfer001Service;
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
 * 买断式回购转仓
 *
 * @author liuwei
 * @version 1.0 2019年12月31日
 * @see BuyOutRepoTransfer001Controller
 * @since 1.0
 */
@Controller
@PageId("BuyOutRepoTransfer001")
@RequestMapping("/invest/trade/transfer/buyoutrepo/BuyOutRepoTransfer001")
public class BuyOutRepoTransfer001Controller extends BaseController {

    /**
     * BuyOutRepoTransfer001Service
     */
    @Autowired
    private BuyOutRepoTransfer001Service buyOutRepoTransfer001Service;

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
        this.setSelectOptionToModel(model, DictCode.币种, DictCode.回购拆借方向,DictCode.计息基础,DictCode.回购期限品种);
        Map<String,Object> paraMap = RequestUtil.getPageParam();
        String workflowId = getParam(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID);
        BuyOutRepoTransfer001Dto entity = new BuyOutRepoTransfer001Dto();
        //来自代办
        if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            // 根据工作流与留痕处理提供的共通方法获取业务实体
            entity = buyOutRepoTransfer001Service.getBussinessEntityByWorkflowId(workflowId);
            for(BuyOutRepoTransfer002Dto dto:entity.getBuyOutRepoTransfer002DtoList()){
                dto.setTradeAmt(dto.getTradeAmt().divide(new BigDecimal(10000)));
            }
            // 设置到model中
            model.addAttribute(com.joyintech.framework.constant.GlobalConstant.WORKFLOW_ID,workflowId);
            model.addAttribute("entityData", JsonUtils.object2Json(entity.getBuyOutRepoTransfer002DtoList()));
            model.addAttribute("entity", entity);
            model.addAttribute("formTodoList", true);
        }
        //初始化界面
        else {
            if(StringUtil.isNotEmpty(paraMap.get("tradeId"))){
                String tradeId = (String)paraMap.get("tradeId");
                entity = buyOutRepoTransfer001Service.getTradeInfo(tradeId);
                entity.setTradeDate(DateUtil.platDateFromCache());
                entity.setPrinAmt(ArithmeticUtil.divide(entity.getPrinAmt(),new BigDecimal(10000),6));
                if(StringUtil.isNotEmpty(paraMap.get("amount"))) {
                    entity.setUndistributedAmount(ArithmeticUtil.divide(new BigDecimal((String) paraMap.get("amount")),new BigDecimal(10000),6));
                }
            }
            model.addAttribute("entity", entity);
        }
        model.addAttribute("BACK_LEND_DIRECTION_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_04", DictCodeList.回购拆借方向_逆回购.getValue());
        // 交易市场_二级市场
        model.addAttribute("TRADE_PLACE_SEC", DictCodeListExt1.交易市场_二级市场.getValue());
        // 投资目的_摊余成本类
        model.addAttribute("INV_AIM_TC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        // 监督时分_交易监控
        model.addAttribute("MY_SUP_TIME_TYPE_DEAL", DictCodeListExt1.监督时分_交易监控.getValue());
        // 金融产品类型_正回购
        model.addAttribute("FINPROD_TYPE_TG_F04", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        // 金融产品类型_逆回购
        model.addAttribute("FINPROD_TYPE_TG_F05", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        //add by liuwei for huaxing 20200310 start
        model.addAttribute("CALC_MODEL_03", DictCodeListExt3.计算模式_计算到期净价.getValue());
        //add by liuwei for huaxing 20200310 end
        
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
     * 获取拆分信息—组合代码
     * @return 联想文本框实体类集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getPortfolioId")
    public List<SelectOptionDto> getPortfolioId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        map.put("userId", LoginUserUtil.getUserName());
        return buyOutRepoTransfer001Service.getPortfolioId(map);
    }

    /**
     * 添加按钮按下前check
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("addCheck")
    public AjaxResult addCheck(@RequestBody BuyOutRepoTransfer001Dto dto, BindingResult br) {
        AjaxResult ar = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        List<JoyinMessage> joyinMessage = new ArrayList<>();
        boolean flag = false;
        StringBuffer sb = new StringBuffer();
        if (StringUtil.isNotEmpty(dto)){
            if(!StringUtil.isNotEmpty(dto.getPortfolioId())){
                errorList.add(new ValidMessage("portfolioId","请输入组合代码！"));
            }
            //产品组合在列表信息已存在
            else if(!StringUtil.isEmptyList(dto.getBuyOutRepoTransfer002DtoList())){
                for(BuyOutRepoTransfer002Dto entity : dto.getBuyOutRepoTransfer002DtoList()){
                    if(StringUtil.equals(entity.getPortfolioId(),dto.getPortfolioId())){
                        errorList.add(new ValidMessage("产品组合:" + dto.getPortfolioId()+"在转仓列表已存在！"));
                        break;
                    }
                }
            }
            if(!StringUtil.isNotEmpty(dto.getProPrinAmt())){
                errorList.add(new ValidMessage("proPrinAmt","请输入券面总额！"));
            }
            else if(dto.getProPrinAmt().compareTo(new BigDecimal(0)) <= 0){
                errorList.add(new ValidMessage("proPrinAmt","券面总额必须大于0！"));
            }
            else if(StringUtil.equals(dto.getPs(),DictCodeList.回购拆借方向_正回购.getValue())) {
                /*if(dto.getAssetPosition() != null &&
                        ArithmeticUtil.multiply(dto.getProPrinAmt(),new BigDecimal(10000)).compareTo(dto.getAssetPosition()) > 0) {
//                    flag = true;
//                    sb.append(getMessage("资产剩余持仓不足，请确认是否继续添加？") + GlobalConstant.HTML_NEWLINE);
                    errorList.add(new ValidMessage("资产剩余持仓不足！"));
                }*/
                if(dto.getProPrinAmt().compareTo(buyOutRepoTransfer001Service.selectPosition(dto)) > 0){
                    flag = true;
                    joyinMessage.add(new JoyinMessage("债券不足，请确认是否继续添加？",MessageType.确认消息));
                }
            }
            else if(StringUtil.equals(dto.getPs(),DictCodeList.回购拆借方向_逆回购.getValue()) && dto.getAvalibleCash() != null &&
                    dto.getFirSettleAmt().compareTo(dto.getAvalibleCash()) > 0){
                flag = true;
                joyinMessage.add(new JoyinMessage("组合剩余现金不足，请确认是否继续添加？",MessageType.确认消息));
            }
            //输入的本金金额 > 剩余未拆分本金金额
            if(StringUtil.isNotEmpty(dto.getProPrinAmt()) && StringUtil.isNotEmpty(dto.getUndistributedAmount()) &&
                    dto.getProPrinAmt().compareTo(dto.getUndistributedAmount()) > 0){
                errorList.add(new ValidMessage("输入的本金金额不能大于" + dto.getUndistributedAmount().toString()+"万元!"));
            }
        }
        if(StringUtil.isEmptyList(errorList)){
            if(flag) {
                ar.setJoyinMessages(joyinMessage);
                return ar;
            }
            else {
                ar.setStatuCode(HttpStatus.OK.value());
            }
        }
        else {
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
    public AjaxResult check(@RequestBody BuyOutRepoTransfer001Dto dto) {
        AjaxResult wr = new AjaxResult();
        if (StringUtil.isNotEmpty(dto.getChlId())) {
            Integer count;
            String str = "";
            Map<String, Object> sMap = new HashMap<String, Object>();
            for (BuyOutRepoTransfer002Dto ptl : dto.getBuyOutRepoTransfer002DtoList()) {
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
        if (StringUtil.isEmptyList(dto.getBuyOutRepoTransfer002DtoList())) {
            wr.setStatuCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            wr.setMessage("转仓列表信息不能空", MessageType.错误消息);
        } else {
            String btnName = "操作";
            if (StringUtil.equalsIgnoreCase("save", dto.getOperateType())) {
                btnName = "SysCommon.save";
            } else if (StringUtil.equalsIgnoreCase("submit", dto.getOperateType())) {
                btnName = "SysCommon.submit";
            }
            wr.setData(SpringContextUtil.getTextValue("COMMON_C_0001",
                    new String[]{SpringContextUtil.getTextValue(btnName)}));
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
    public AjaxResult save(@RequestBody BuyOutRepoTransfer001Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<BuyOutRepoTransfer001Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<BuyOutRepoTransfer001Dto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_买断式回购交易.getValue());

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


        params.setComment(entity -> "买断式回购转仓-"+"交易编号："+dto.getZcId()+";资产名称："+
                dto.getFinprodAbbr()+";交易日："+ DateUtil.format(dto.getTradeDate()));
        processParameter.setWorkFlowParameter(params);
        //转仓列表
        if (BussinessConstant.SUBMIT.equals(dto.getOperateType()) &&
                !StringUtil.isEmptyList(dto.getBuyOutRepoTransfer002DtoList())) {
            dto.getBuyOutRepoTransfer002DtoList().forEach(buyOutRepoTransfer002Dto -> {
                buyOutRepoTransfer002Dto.setTradeAmt(ArithmeticUtil.multiply(buyOutRepoTransfer002Dto.getTradeAmt(),new BigDecimal(10000)));
                buyOutRepoTransfer001Service.genFinProductDto(buyOutRepoTransfer002Dto,dto);
            });
        }
        String msg = buyOutRepoTransfer001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    @ResponseBody
    @RequestMapping("/selectIndex")
    public String selectIndex() {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_买断式回购交易);
        String index=baseGetBusinessId.buildBussinessCode(codeParameter);
        return index;
    }
}
