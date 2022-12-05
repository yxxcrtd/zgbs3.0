package com.joyintech.tams.invest.basicdata.dayendaccount.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.basicdata.dayendaccount.dto.DayEndAccount002Dto;
import com.joyintech.tams.invest.basicdata.dayendaccount.dto.DayEndAccountDto;
import com.joyintech.tams.invest.basicdata.dayendaccount.service.DayEndAccount001Service;
import com.joyintech.tams.invest.basicdata.dayendaccount.service.DayEndAccount002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 日终清算数据核对
 * 〈功能详细描述〉
 * @author chengzhong
 * @version 1.0 2021年4月23日
 * @see DayEndAccount001Controller
 * @since 1.0
 */

@Controller
@PageId("DayEndAccount002")
@RequestMapping("/invest/basicdata/dayendaccount/DayEndAccount002")
public class DayEndAccount002Controller extends BaseController {

    @Autowired
    private DayEndAccount001Service dayEndAccount001Service;

    @Autowired
    private DayEndAccount002Service dayEndAccount002Service;
    /**
     * 初始化新建页面
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.货币对,DictCode.估值来源);
        String seqNo = getParam("seqNo");
        String workflowId = getParam("workflowId");
        // 修改时
        if (StringUtil.isNotEmpty(seqNo)) {
            DayEndAccount002Dto dayEndAccount002Dto = new DayEndAccount002Dto();
            dayEndAccount002Dto.setSeqNo(seqNo);
            model.addAttribute("entity", dayEndAccount002Dto);
        }
        else if (StringUtil.isNotEmpty(workflowId)) {
            DayEndAccount002Dto dayEndAccount002Dto = new DayEndAccount002Dto();
            dayEndAccount002Dto.setWorkflowId(workflowId);
            model.addAttribute("entity", dayEndAccount002Dto);
        }else{
            DayEndAccountDto dayEndAccountDto = new DayEndAccountDto();
            dayEndAccountDto.setEntrustTradeId(getParam("entrustTradeId"));
            dayEndAccountDto.setFinprodId(getParam("finprodId"));
            dayEndAccountDto.setAccount(getParam("busiAcctNo"));
            dayEndAccountDto.setSeatNumber(getParam("seatNumber"));
            dayEndAccountDto.setTradeMarket(getParam("tradeMarket"));
            dayEndAccountDto.setSettleDate(new Date(Long.valueOf(getParam("settleDate"))));
        	model.addAttribute("entity", dayEndAccountDto);
        }
        // 系统参数：系统参数.是否可修改结算文件中的费用数据
        model.addAttribute("stlModifyCommission", CacheUtil.getSysParam("STL_MODIFY_COMMISSION"));
        return super.init(model);
    }

    /**
     *
     * 编辑修改页面表格查询
     * <功能详细描述>
     * @return Page<PaRskCustomerRatingDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findDataPage")
    public Page<DayEndAccountDto> findDataPage()
            throws Exception {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        String workflowId = (String)pageParam.get("workflowId");
        String seqNo = (String)pageParam.get("seqNo");
        Page<DayEndAccountDto> page = new Page<DayEndAccountDto>();
        if (StringUtil.isNotEmpty(workflowId)) {
            DayEndAccount002Dto dayEndAccount002Dto  = dayEndAccount002Service.getBussinessEntityByWorkflowId(workflowId);
            page.setRoot(dayEndAccount002Dto.getReconDealInfoList());
        } else if (StringUtil.isNotEmpty(seqNo)) {
            DayEndAccount002Dto dayEndAccount002Dto = dayEndAccount001Service.getReconDealInfo(pageParam);
            page.setRoot(dayEndAccount002Dto.getReconDealInfoList());
        }
        return page;
    }

    /**
     *
     * 编辑修改页面表格查询
     * <功能详细描述>
     * @return Page<PaRskCustomerRatingDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findDataDetailPage")
    public Page<DayEndAccountDto> findDataDetailPage()
            throws Exception {
        Map<String, Object> pageParam = RequestUtil.getPageParam();
        String workflowId = (String)pageParam.get("workflowId");
        String seqNo = (String)pageParam.get("seqNo");
        Page<DayEndAccountDto> page = new Page<DayEndAccountDto>();
        if (StringUtil.isNotEmpty(workflowId)) {
            DayEndAccount002Dto dayEndAccount002Dto  = dayEndAccount002Service.getBussinessEntityByWorkflowId(workflowId);
            page.setRoot(dayEndAccount002Dto.getDealDetailList());
        }
        else{
            DayEndAccount002Dto dayEndAccount002Dto = dayEndAccount001Service.getReconDealInfo(pageParam);
            page.setRoot(dayEndAccount002Dto.getDealDetailList());
        }
        return page;
    }

    /**
     * 保存时的check <一句话功能简述> <功能详细描述> 修改类型进行检查
     * 
     * @param dto 检验输入参数
     * @param result 错误参数
     * @return 检验结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) DayEndAccount002Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 
     * 保存/修改
     * <功能详细描述>
     * @param dto 传入参数
     * @param br 传入的报错参数
     * @return 返回结果
     * @throws Exception 抛出异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) DayEndAccount002Dto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        WorkFlowParameter<DayEndAccount002Dto> params = new WorkFlowParameter<DayEndAccount002Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getSeqNo());
        params.setBusinessType(DictCodeListExt3.共通业务类型_券商流水核对.getValue());
        // 设置流程业务说明, entity即传入的dto
        DayEndAccountDto dayEndAccountDto = null;
        if(CollectionUtils.isNotEmpty(dto.getReconDealInfoList())){
            dayEndAccountDto =  dto.getReconDealInfoList().get(0);
        }else if(CollectionUtils.isNotEmpty(dto.getDealDetailList())){
            dayEndAccountDto = dto.getDealDetailList().get(0);
        }
        String portfolioId = dayEndAccountDto.getPortfolioId();
        String finprodId = dayEndAccountDto.getFinprodId();
        // ZGZGST-4149:不展示组合代码 fuyongsheng 20211207 edit start
        // params.setComment(entity -> "组合代码" + "(" + portfolioId + ")" + "-" + "资产代码" + "(" + finprodId + ")");
        params.setComment(entity -> "资产代码" + "(" + finprodId + ")");
        // ZGZGST-4149:不展示组合代码 fuyongsheng 20211207 edit end
        ProcessParameter<DayEndAccount002Dto> parameter = new ProcessParameter<DayEndAccount002Dto>();
        parameter.setWorkFlowParameter(params);
        String msg = dayEndAccount002Service.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     *
     * 根据主键查询成交明细
     * <功能详细描述>
     * @return SettleTransData
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findSettleTransDataById")
    public SettleTransData findSettleTransDataById(String tradeId)
            throws Exception {
        return dayEndAccount002Service.findSettleTransDataById(tradeId);
    }

}
