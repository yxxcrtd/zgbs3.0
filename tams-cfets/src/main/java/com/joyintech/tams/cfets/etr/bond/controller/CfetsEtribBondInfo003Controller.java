/*
 * 文件名：EtribBondInfo003Controller.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月19日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrCalcAmountRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrCalcAmountResponse;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtribBondInfo003Service;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.etribbondinfo.service.EtrIbBondinfo002Service;
import com.joyintech.tams.invest.entrust.etribbondinfo.validator.EtribBondInfo003Validator;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Controller
@PageId("CfetsEtrIbBondinfo003")
@RequestMapping("cfets/etrib/CfetsEtrIbBondinfo003")
@Api(tags = "银行间债券多组合指令新建")
public class CfetsEtribBondInfo003Controller extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsEtribBondInfo003Controller.class);

    @Autowired
    private CfetsEtribBondInfo003Service cfetsetribBondInfo003Service;

    @Autowired
    private EtribBondInfo003Validator etribBondInfo003Validator;

    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrIbBondinfo002Service etrIbBondinfo002Service;
    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 多组合指令新建画面
     * @param model
     * @return
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.交易市场);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.投资目的);
        this.setSelectOptionToModel(model, DictCode.交易平台);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.组合系列);
        this.setSelectOptionToModel(model, DictCode.业务模式);
        Date sysDate = DateUtil.platDateFromCache();
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        CfetsEtribBondInfo003Dto entity = null;
        boolean todoList = false;
        // 代办中过来
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            entity = cfetsetribBondInfo003Service.getBussinessEntityByWorkflowId(workflowId);
            if(Objects.isNull(entity.getTradeAmt())) {
                entity.setTradeAmt(BigDecimal.ZERO);
            }
            if(Objects.isNull(entity.getEntrustOwnNum())) {
                entity.setEntrustOwnNum(BigDecimal.ZERO);
            }
            entity.setMatureYield(ArithmeticUtil.multiply(entity.getMatureYield(),new BigDecimal(100)));
            entity.setExerYield(ArithmeticUtil.multiply(entity.getExerYield(),new BigDecimal(100)));
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("calculateField",entity.getCalculateField());
            model.addAttribute("person_entity", JsonUtils.object2Json(entity.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);
            model.addAttribute("entity_BussCombDistribtList",  JsonUtils.object2Json(entity.getBussCombDistribtList()));
            model.addAttribute("entity_RiskTrialRetList",  JsonUtils.object2Json(entity.getRiskTrialRetDto()));

            todoList = true;
            model.addAttribute("businessId", entity.getTotalEtrustId());
            model.addAttribute("totalEtrustId", entity.getTotalEtrustId());
        } else {
            entity = new CfetsEtribBondInfo003Dto();
            entity.setInvManagerId(LoginUserUtil.getUserName());
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setCalendarId(BussinessDictCodeList.wind交易场所_银行间.getValue());
            entity.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            entity.setSecAcctId("999999");
            entity.setTradeMarket(BussinessDictCodeList.交易场所_银行间.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("businessId", IDGenerator.generate());
        }
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
        model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("Y", DictCodeList.是否_是.getValue());
        //model.addAttribute("CCY", DictCodeList.币种_人民币_CNY.getValue());
        //展示投资目的或业务模式参数
        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("TodoList", todoList);
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        //卖空模式
        model.addAttribute("SHORT_SELLING_SUPPORTED", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));

        return super.init(model);
    }

    /**
     * 获取组合分配的相关值
     * @return
     */
    @ResponseBody
    @PostMapping("/getEntrustId")
    public Message<String> getEntrustId() {
        BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
        String code = bussGetBussinessIdService != null? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
        return new Message<String>().success().setData(code);
    }

    /**
     * 获取组合分配的相关值
     * @param param 参数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPortfolioAllocationValues")
    public Map<String, Object> getPortfolioAllocationValues(@RequestBody Map<String, Object> param) {
        return cfetsetribBondInfo003Service.getPortfolioAllocationValues(param);
    }
    /**
     * 净价、行权收益率、到期收益率
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/calData")
    @ApiOperation(value = "新建指令净价全价收益率计算功能", notes = "新建指令净价全价收益率计算功能")
    @ResponseBody
    public Message<AjaxResult> calData(@RequestBody CfetsEtribBondInfo003Dto dto) {
        try {
            AjaxResult result = new AjaxResult();
            cfetsetribBondInfo003Service.calcAmt(dto);
            result.setData(dto);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("计算功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("计算异常");
        }

    }
   /* @PostMapping("/calData")
    @ApiOperation(value = "新建指令净价全价收益率计算功能", notes = "新建指令净价全价收益率计算功能")
    @ResponseBody
    public Message<CfetsEtrCalcReturnDto> calData(@RequestBody CfetsEtrCalcRequestDto requestDto) {
        try {
            CfetsEtrCalcReturnDto dto = cfetsEtrIbBondinfoService.getEtrFinProductInfo(secId);
            return new Message<CfetsEtrCalcReturnDto>().success().setData(dto);
        } catch (Exception e) {
            logger.error("计算功能异常", e);
            return new Message<CfetsEtrCalcReturnDto>().fail().setMessage("计算异常");
        }
    }*/
   @PostMapping("/calAmountData")
   @ApiOperation(value = "新建指令金额计算功能", notes = "新建指令金额计算功能")
   @ResponseBody
   public Message<AjaxResult> calAmountData(@RequestBody CfetsEtrCalcAmountRequest dto) {
       try {
           AjaxResult result = new AjaxResult();
           CfetsEtrCalcAmountResponse res=cfetsetribBondInfo003Service.calAmountData(dto);
           result.setData(res);
           return new Message<AjaxResult>().success().setData(result);
       } catch (Exception e) {
           logger.error("新建指令金额计算功能异常", e);
           return new Message<AjaxResult>().fail().setMessage("新建指令金额计算功能异常");
       }
   }


}
