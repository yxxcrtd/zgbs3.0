package com.joyintech.tams.invest.riskredis.quota.portfolioindicator.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.dto.RskPortfolioIndicator002Dto;
import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.service.RskSupervisionPortfPoint001Service;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;

import com.joyintech.tams.invest.riskredis.quota.portfolioindicator.service.RskSupervisionPortfPoint002Service;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;



/**
 * 组合关联监控指标
 * @author 裴家俊
 * @version 1.0 2018年10月23日
 * @see RskSupervisionPortfPoint001Controller
 * @since 1.0
 */
@Controller
@PageId("RskSupervisionPortfPoint001")
@RequestMapping("/invest/riskredis/quota/portfolioindicator/RskSupervisionPortfPoint001")
public class RskSupervisionPortfPoint001Controller extends BaseController {
	
	/**
     * RskSupervisionPortfPoint001Service
     */
    @Autowired(required = false)
    private RskSupervisionPortfPoint001Service rskSupervisionPortfPoint001Service;
    
    /**
     * RskSupervisionPortfPoint001Service
     */
    @Autowired(required = false)
    private RskSupervisionPortfPoint002Service rskSupervisionPortfPoint002Service;
    
    /**
     * 初始化的查询条件
     */
   private static final IDictCode[] DICT_CODES = new BussinessDictCodeRds[] {BussinessDictCodeRds.指标类型,
	        BussinessDictCodeRds.监督类型, BussinessDictCodeRds.运算符, BussinessDictCodeRds.连接符,BussinessDictCodeRds.操作符,
	        BussinessDictCodeRds.单位, BussinessDictCodeRds.监督时分, BussinessDictCodeRds.监督频率, BussinessDictCodeRds.组合层次,
	        BussinessDictCodeRds.分类维度,BussinessDictCodeRds.收益类型,BussinessDictCodeRds.开放类型,BussinessDictCodeRds.募集方式,
            BussinessDictCodeRds.投资性质,BussinessDictCodeRds.是否面向单一投资者
	       };

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
        SelectDto selectJsmsDto = this.getSelectDataFromDataDic(BussinessDictCodeRds.组合层次, null);
        List<SelectOptionDto> optionJsms = selectJsmsDto.getOptions();
        Map<String,List<SelectOptionDto>> mapZhcc = optionJsms.stream().collect(JoyinCollectors.groupingBy(x->x.getId()));
        model.addAttribute("mapZhcc",mapZhcc);
        return super.init(model);
    }
  
    /**
     * 画面查询方法
     * @return 所有数据
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("findPage")
    public Page<RskPortfolioIndicator002Dto> selectAll()
         {
        return rskSupervisionPortfPoint001Service.selectForPage(
            RequestUtil.getParameters(getRequest()));
    }
    
    /**
     * 画面查询方法
     * @return 所有数据
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("playPortfolio")
    public AjaxResult playPortfolio()
         {
    	rskSupervisionPortfPoint001Service.playPortfolio(RequestUtil.getPageParam());
        return new AjaxResult(getMessage("BUSS_COM_I_0033",getMessage("RskSupervisionIndicator001.play")));
    }
	/**
     * 保存按钮点击时的check
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskPortfolioIndicator002Dto dto, BindingResult result ) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 删除
     * @param dto
     * @return
     * @throws Exception
     */
    @DTXTransaction
    @ResponseBody
    @RequestMapping(ACTION_DELETE)
    public AjaxResult delete(@RequestBody @Validated({
        ValidateGroupDelete.class}) RskPortfolioIndicator002Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<RskPortfolioIndicator002Dto> processParameter = new ProcessParameter<RskPortfolioIndicator002Dto>();
        WorkFlowParameter<RskPortfolioIndicator002Dto> params = new WorkFlowParameter<RskPortfolioIndicator002Dto>();
        params.setPageId("RskSupervisionPortfPoint002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getPortfolioId());
        params.setBusinessType(BussinessDictCodeListRds.共通业务类型_限额组合关联监督指标管理.getValue());
        // 设置流程业务说明, entity即传入的dto
        if(dto.getTreeFlag().equals("A")){
        	params.setBusinessCode(item -> item.getIndId());
        	params.setComment(entity -> {
                return WorkFlowUtil.concatComment(getMessage("RskSupervisionPortfPoint002.indId"),
                    GlobalConstant.OPEN_PARENTHESIS, entity.getIndId(), GlobalConstant.CLOSE_PARENTHESIS,
                    getMessage("RskSupervisionPortfPoint002.indName"), GlobalConstant.OPEN_PARENTHESIS,
                    entity.getIndName(), GlobalConstant.CLOSE_PARENTHESIS,getMessage("RskSupervisionPortfPoint001.portfolioLevel"), GlobalConstant.OPEN_PARENTHESIS,
                    SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.组合层次, entity.getPortfolioLevel()), GlobalConstant.CLOSE_PARENTHESIS);
            });
        }
        else{
        	params.setBusinessCode(item -> item.getPortfolioId());
	        params.setComment(entity -> {
	            return WorkFlowUtil.concatComment(getMessage("RskSupervisionPortfPoint002.portfolioId"),
	                GlobalConstant.OPEN_PARENTHESIS, entity.getPortfolioId(), GlobalConstant.CLOSE_PARENTHESIS,
	                getMessage("RskSupervisionPortfPoint002.portfolioName"), GlobalConstant.OPEN_PARENTHESIS,
	                entity.getPortfolioName(), GlobalConstant.CLOSE_PARENTHESIS,getMessage("RskSupervisionPortfPoint001.portfolioLevel"), GlobalConstant.OPEN_PARENTHESIS,
	                SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.组合层次, entity.getPortfolioLevel()), GlobalConstant.CLOSE_PARENTHESIS);
	        });
        }
        processParameter.setWorkFlowParameter(params);
        /*String msg = rskSupervisionPortfPoint002Service.start(processParameter,
            dto.getSelectedItem().toArray(
                new RskPortfolioIndicator002Dto[dto.getSelectedItem().size()]));*/
        String msg = rskSupervisionPortfPoint002Service.start(processParameter,
                dto.getSelectedItem().toArray(new RskPortfolioIndicator002Dto[dto.getSelectedItem().size()]));
        return new AjaxResult(msg);
    }
    
    /**
     * 导出信息excel
     * @return excel
     * @throws Exception 异常
     */
    @RequestMapping("export")
    public ResponseEntity<byte[]> export()
             {
        return rskSupervisionPortfPoint001Service.export(this.getPageId(),
                RequestUtil.getParameters(getRequest()));

    }
    
    // add by zhuzhen 20191128 组合关联指标删除时校验联合设置 start
    /**
     * 删除按钮提醒check
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("checkDelate")
    public AjaxResult checkDelate(@RequestBody RskPortfolioIndicator002Dto dto) {
        AjaxResult ajaxResult = new AjaxResult();
        List<RskPortfolioIndicator002Dto> itemList = dto.getSelectedItem();
        if(!StringUtil.isEmptyList(itemList)){
            Map<String, Object> parameters = new HashMap<>();
            for (RskPortfolioIndicator002Dto item : itemList) {
                parameters.put("indId", item.getIndId());
                parameters.put("portfolioId", item.getPortfolioId());
                if(rskSupervisionPortfPoint001Service.selectIndividUnite(parameters)){
                    ajaxResult.setMessage(getMessage("RSKRDS_W_0075"), MessageType.确认消息);
                }
            }
        }
        return ajaxResult;
    }
    // add by zhuzhen 20191128 组合关联指标删除时校验联合设置 end
    
}
