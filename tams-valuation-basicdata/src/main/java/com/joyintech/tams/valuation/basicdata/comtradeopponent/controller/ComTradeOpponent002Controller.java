/*
 * 文件名：ComTradeOpponent002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：huwenbin
 * 修改时间：2018年7月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.basicdata.comtradeopponent.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.MstCounterAcctKey;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.dto.ComTradeOpponentDto;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.dto.FinCashDto;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.dto.StlCapacctDto;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.service.ComTradeOpponent001Service;
import com.joyintech.tams.valuation.basicdata.comtradeopponent.service.ComTradeOpponent002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易对手管理新建
 * 
 * @author huwenbin
 * @version 1.0 2018年7月26日
 * @see ComTradeOpponent002Controller
 * @since 1.0
 */

@Controller
@PageId("ComTradeOpponent002")
@RequestMapping("/valuation/basicdata/comtradeopponent/ComTradeOpponent002")
public class ComTradeOpponent002Controller extends BaseController {


    /**
     * 交易对手查询service注入
     */
    @Autowired
    private ComTradeOpponent001Service comTradeOpponent001Service;

    /**
     * 交易对手新建service注入
     */
    @Autowired
    private ComTradeOpponent002Service comTradeOpponent002Service;

    /**
     * 附件上传类
     */
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 界面初始化 {@inheritDoc}
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.是否, DictCode.资管计划属性,
            DictCode.资金实际投向, DictCode.购买结构, DictCode.管理方式,
            DictCode.交易对手类型, DictCode.资金账户类型, DictCode.计划类资产一级分类,
            DictCode.计划二级分类_信托, DictCode.使用状态, DictCode.计息基础,
            DictCode.计费基数_费用品种, DictCode.机构所属行业二级分类, DictCode.费用品种类型,
                DictCode.客户一级类型, DictCode.客户二级类型,DictCode.是否_中债);
        String counterId = getParam("counterId");
        String counterType = getParam("counterType");
        // 判断是否存在于留痕表
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            ComTradeOpponentDto dto = comTradeOpponent002Service.getBussinessEntityByWorkflowId(
                workflowId);
            List<FinCashDto> finList = dto.getSelectFinCash();
            List<StlCapacctDto> stlList = dto.getSelectStlCapacct();
            model.addAttribute("stlList", stlList);
            model.addAttribute("finList", finList);
            model.addAttribute("dto", dto);
            // 判断逐渐是否为空
        }
        else if (StringUtil.isNotEmpty(counterId) && StringUtil.isNotEmpty(counterType)) {
            // 将金融代码和固定格式的日期字符串传入参数集合
            Map<String, Object> map = new HashMap<String, Object>();
            if (DictCodeListExt1.交易对手类型_内部交易对手.getValue().equals(counterType)) {
                counterId="F16_"+counterId;
            }
            map.put("counterId", counterId);
            // 根据估值日和金融代码进行查找
            ComTradeOpponentDto dto = comTradeOpponent002Service.selectCounterInfo(map);
            map.put("finprodId", dto.getFinprodId());
            List<StlCapacctDto> stlList = dto.getSelectStlCapacct();
            stlList = comTradeOpponent002Service.selectStlCapacctInfo(map);
            List<FinCashDto> finList = null;
            if (DictCodeListExt1.交易对手类型_通道计划.getValue().equals(counterType)) {
                finList = dto.getSelectFinCash();
                finList = comTradeOpponent002Service.selectFinCashInfo(map);
                model.addAttribute("finList", finList);
            }
            model.addAttribute("stlList", stlList);
            model.addAttribute("dto", dto);
            model.addAttribute("counterId", dto.getCounterId());
        }
        model.addAttribute("COUNTER_TYPE_01", DictCodeListExt1.交易对手类型_市场机构.getValue());
        model.addAttribute("COUNTER_TYPE_02", DictCodeListExt1.交易对手类型_资管产品.getValue());
        model.addAttribute("COUNTER_TYPE_03", DictCodeListExt1.交易对手类型_通道计划.getValue());
        model.addAttribute("COUNTER_TYPE_04", DictCodeListExt1.交易对手类型_分行.getValue());
        model.addAttribute("COUNTER_TYPE_05", DictCodeListExt1.交易对手类型_内部交易对手.getValue());
        model.addAttribute("ACCT_TYPE_FG_A01", DictCodeListExt1.资金账户类型_产品托管户.getValue());
        model.addAttribute("ACCT_TYPE_A05", DictCodeListExt1.资金账户类型_交易对手账户.getValue());
        model.addAttribute("ACCT_TYPE_A06", DictCodeListExt1.资金账户类型_通道账户.getValue());
        model.addAttribute("PLAN_ASSET_TYPE_02", DictCodeList.计划类资产一级分类_信托产品.getValue());
        model.addAttribute("IS_OPEN_01", DictCodeListExt1.使用状态_启用.getValue());
        model.addAttribute("CHB_PRO_PROPERTY_99", DictCodeListExt2.资管计划属性_集合.getValue());
        model.addAttribute("CHB_BUY_STRUCT_01", DictCodeListExt2.购买结构_优先.getValue());
        model.addAttribute("CHB_MANATYPE_PLAN_ASSET_01", DictCodeListExt2.管理方式_主动_自主管理.getValue());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("CHB_PRO_PROPERTY_99",DictCodeListExt2.资管计划属性_集合.getValue());
        model.addAttribute("CHB_BUY_STRUCT_01",DictCodeListExt2.购买结构_优先.getValue());
        model.addAttribute("CHB_MANATYPE_PLAN_ASSET_01",DictCodeListExt2.管理方式_主动_自主管理.getValue());
        model.addAttribute("YES_OR_NO_Y",DictCodeList.是否_是.getValue());
        model.addAttribute("C01", DictCodeListExt2.客户一级类型_住户.getValue());
        model.addAttribute("C0100", DictCodeListExt2.客户二级类型_住户.getValue());
        model.addAttribute("C02", DictCodeListExt2.客户一级类型_广义政府.getValue());
        model.addAttribute("C0200", DictCodeListExt2.客户二级类型_广义政府.getValue());
        model.addAttribute("C03", DictCodeListExt2.客户一级类型_非金融企业.getValue());
        model.addAttribute("C0300", DictCodeListExt2.客户二级类型_非金融企业.getValue());
        model.addAttribute("C06", DictCodeListExt2.客户一级类型_境外.getValue());
        model.addAttribute("C0600", DictCodeListExt2.客户二级类型_境外.getValue());
        
        return super.init(model);
    }

    /**
     * 查询、修改和新建按钮点击时的check
     * @param dto
     * @param result
     *  check结果
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) ComTradeOpponentDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * @param dto 验证的实体类
     * @param result 错误信息
     * @return 错误集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("finCheck")
    public AjaxResult finCheck(@RequestBody @Validated({ValidateGroupEdit.class}) FinCashDto dto,
                               BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        return new AjaxResult();
    }

    /**
     * 获取账号
     * 
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAccount")
    public List<SelectOptionDto> getAccount() {
        return comTradeOpponent001Service.getAccount(RequestUtil.getPageParam());
    }

    /**
     * 获取费用信息
     * 
     * @return 费用信息
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getBasis")
    public Map<String, Object> getBasis() {
        String feeId = getParam("feeId");
        return comTradeOpponent001Service.getBasis(feeId);
    }

    /**
     * 获取费用id
     * 
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFeeId")
    public List<SelectOptionDto> getFeeId() {
    	Map<String, Object> param = RequestUtil.getPageParam();
    	List<String> feetypeList = new ArrayList<String>();
    	feetypeList.add(DictCodeList.费用品种类型_通道托管费.getValue());
    	feetypeList.add(DictCodeList.费用品种类型_通道管理费.getValue());
    	feetypeList.add(DictCodeList.费用品种类型_其他费用.getValue());
    	
    	param.put("feetypeList", feetypeList);
    	
        return comTradeOpponent001Service.getFeeId(param);
    }

    /**
     * 获取账号类型
     * 
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAccountType")
    public List<SelectOptionDto> getAccountType() {
        return comTradeOpponent001Service.getAccountType();
    }

    /**
     * 获取账号实体类
     * 
     * @return 账号实体类
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCctInfo")
    public StlCapacctInfo getCctInfo() {
        Map<String, Object> map = RequestUtil.getParameters(getRequest());
        String[] noAndCcy = map.get("acctNo").toString().split("_");
        map.replace("acctNo", noAndCcy[0]);
        map.put("ccy", noAndCcy[1]);
        StlCapacctInfo dto = comTradeOpponent002Service.getCctInfo(map);
        return dto;
    }

    /**
     * 获取账号类型
     * 
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAccountTypeInfo")
    public List<SelectOptionDto> getAccountTypeInfo() {
        return comTradeOpponent001Service.getAccountTypeInfo();
    }

    /**
     * 获取客户代码(管理人)
     * 
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCustomerId")
    public List<SelectOptionDto> getCustomerId() {
        String param = RequestUtil.getParam("search");
        return comTradeOpponent001Service.getCustomerId(param);
    }

    /**
     * 获取所属总行
     * 
     * @return 所属总行
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getOfficeId")
    public List<SelectOptionDto> getOfficeId() {
        String param = RequestUtil.getParam("search");
        if (param == null) {
            param = "";
        }
        return comTradeOpponent001Service.getOfficeId(param);
    }

    /**
     * 获取市場機構
     * 
     * @return 市場機構
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getMarketId")
    public Page<SelectOptionDto> getMarketId() {
    	Map<String, Object> map = RequestUtil.getPageParam();
        return comTradeOpponent001Service.getMarketId(map);
    }

    /**
     * 获取客户信息
     * 
     * @return 客户信息下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCustomerIdMarket")
    public List<SelectOptionDto> getCustomerIdMarket() {
        String param = RequestUtil.getParam("search");
        return comTradeOpponent001Service.getCustomerIdMarket(param);
    }

    /**
     * 获取交易对手名称
     * 
     * @return 交易对手名称
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCounterName")
    public List<SuggestDto> getCounterName() {
        String param = RequestUtil.getParam("param");
        return comTradeOpponent001Service.getCounterName(param);
    }

    /**
     * 获取交易对手代码
     * 
     * @return 交易对手代码集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCounterId")
    public List<SelectOptionDto> getCounterId() {
        String param = RequestUtil.getParam("search");
        return comTradeOpponent001Service.getCounterId(param);
    }

    /**
     * 获取客户代码(托管人)
     * 
     * @return 下拉框集合
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCustomerId1")
    public List<SelectOptionDto> getCustomerId1() {
        String param = RequestUtil.getParam("search");
        return comTradeOpponent001Service.getCustomerId1(param);
    }
    
    /**
     * 获取内部交易对手信息
     * @return 清算账户表实体类
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getCountInfo")
    public List<StlCapacctInfo> getCountInfo(){
        Map<String,Object> map = RequestUtil.getPageParam();
        return comTradeOpponent001Service.getCountInfo(map);
    }
    
//    /**
//     * 获取是否开启
//     * @param mstCounterAcctKey 交易对手表主键
//     * @return 是否开启
//     * @see
//     * @since 1.0
//     */
//    @ResponseBody
//    @RequestMapping("getIsOpen")
//    public AjaxResult getIsOpen(@RequestBody MstCounterAcctKey mstCounterAcctKey) {
//        String tmp = "N";
//        if(bussCommonMapperDelegateService != null){
//            MstCounterAcct s = bussCommonMapperDelegateService.selectByMultiPrimaryKey(MstCounterAcctMapper.class,
//                mstCounterAcctKey, MstCounterAcct.class);
//            if(s == null){
//                tmp= "N";
//            }
//            else if(StringUtil.equals(s.getIsOpen(), DictCodeListExt1.使用状态_启用.getValue())){
//                tmp= "Y";
//            }
//            else{
//                tmp= "N";
//            }
//        }
//        AjaxResult aj = new AjaxResult();
//        aj.setData(tmp);
//        return aj;
//    }
    /**
     * 删除交易对手下账户信息前的检测
     * @param mstCounterAcctKey
     * @return
     */
    @ResponseBody
    @RequestMapping("deleteAcctNoCheck")
    public AjaxResult deleteAcctNoCheck(@RequestBody MstCounterAcctKey mstCounterAcctKey) {
    	String flag = DictCodeList.是否_否.getValue();
    	if(comTradeOpponent002Service.isExistHistoryDeal(mstCounterAcctKey)>0) {
    		flag = DictCodeList.是否_是.getValue();
    	}
        AjaxResult aj = new AjaxResult();
        aj.setData(flag);
		return aj;
    }

    /**
     * 数据保存操作
     * 
     * @param dto 请求实体类
     * @param br 验证的错误
     * @return 错误集合
     * @throws Exception 错误
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(OPERATE_TYPE_SAVE)
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) ComTradeOpponentDto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 新建留痕表参数和工作流参数
        ProcessParameter<ComTradeOpponentDto> processParameter = new ProcessParameter<ComTradeOpponentDto>();
        WorkFlowParameter<ComTradeOpponentDto> params = new WorkFlowParameter<ComTradeOpponentDto>();
        params.setPageId("ComTradeOpponent002");
        params.setElementId(dto.getElementId());
//        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
//        bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_交易对手管理);
        // dto.setTradeId(baseGetBusinessId != null ?
        // baseGetBusinessId.buildBussinessCode(bussinessCodeParameter):IDGenerator.generate());
        if (StringUtil.equals(GlobalConstant.DETAIL_PAGE_MODEL_NEW, (dto.getPageModel()))) {
            if (dto.getCounterId() == null || dto.getCounterId().isEmpty()) {
                dto.setCounterId(IDGenerator.generate());
            }
            if (dto.getCounterName() == null) {
                dto.setCounterName(dto.getCustomerId());
            }
            dto.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            dto.setFinprodId(dto.getCounterId());
//            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_交易对手管理);
//            dto.setFinprodId(
//                (baseGetBusinessId != null
//                 && baseGetBusinessId.buildBussinessCode(bussinessCodeParameter) != null
//                 && "null".equals(baseGetBusinessId.buildBussinessCode(
//                     bussinessCodeParameter))) ? baseGetBusinessId.buildBussinessCode(
//                         bussinessCodeParameter) : IDGenerator.generate());

        }
        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getCounterId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getCounterId());
        }
        params.setBusinessCode(item -> item.getCounterId());
        params.setBusinessType(DictCodeList.共通业务类型_交易对手管理.getValue());
        // 设置流程业务说明, entity即传入的dto
        // 交易对手名称 +"("+ 交易对手名称 +")"+"-"+"交易对手类型"+"-"+"交易对手类型"+"交易对手代码"+"-"+"交易对手代码"
        params.setComment(entity -> this.getMessage("ComTradeOpponent001.counterName") + "("
                                    + entity.getCounterName() + ")"
                                    + this.getMessage("ComTradeOpponent001.counterType") + "("
                                    + entity.getCounterType() + ")"
                                    + this.getMessage("ComTradeOpponent001.counterId") + "("
                                    + entity.getCounterId() + ")");
        // 塞入工作流参数
        processParameter.setWorkFlowParameter(params);
        // 流程开始
        String msg = comTradeOpponent002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
