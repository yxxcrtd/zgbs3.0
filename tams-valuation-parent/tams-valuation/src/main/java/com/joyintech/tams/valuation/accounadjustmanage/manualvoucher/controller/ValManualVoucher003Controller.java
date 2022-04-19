/*
 * 文件名：ValManualVoucher001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.entity.BokBooksetDate;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.accounadjustmanage.assetbook.service.ValAssetBook001Service;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.dto.*;
import com.joyintech.tams.valuation.accounadjustmanage.manualvoucher.service.ValManualVoucher003Service;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.service.BaseCommonService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新建-手工凭证
 * 
 * @author weipeng
 * @version 2.0 2018年5月3日
 * @see ValManualVoucher003Controller
 * @since 2.0
 */
@Controller
@PageId("ValManualVoucher003")
@RequestMapping("/valuation/accounadjustmanage/manualvoucher/ValManualVoucher003")
public class ValManualVoucher003Controller extends BaseController {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(ValManualVoucher003Controller.class);

    @Autowired
    private ValManualVoucher003Service valManualVoucher003Service;

    @Autowired
    private BaseCommonService baseCommonService;
    
    @Autowired
    private InterfCommonService interfCommonService;
    @Autowired
    private ValAssetBook001Service valAssetBook001Service;
    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;

    private String seq;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.会计场景代码, DictCode.币种, DictCode.冲正标识,
            DictCode.借贷方向, DictCode.投资目的, DictCode.分录类型, DictCode.金融产品类型 ,DictCode.记账顺序);
        // 获取业务类型
        // 获取 金融产品类型 数据字典
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.金融产品类型, null);
        List<SelectOptionDto> options = selectDto.getOptions();
        // 获取 现金流二级类型 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.现金流二级类型, null);
        options.addAll(selectDto.getOptions());
        // 获取 资金账户类型 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.资金账户类型, null);
        options.addAll(selectDto.getOptions());
        // 获取 一次性费用类型 数据字典
        selectDto = this.getSelectDataFromDataDic(DictCode.一次性费用类型, null);
        options.addAll(selectDto.getOptions());
        // 4个数据字典的全部放入一个下拉框中
        model.addAttribute("busiType", JsonUtils.objectArray2Json(options.toArray()));
        model.addAttribute("QTYSK_BJ", DictCodeBaseSubjectNatrue.其他应收款_本金.getValue());
        model.addAttribute("QTYSK_LX", DictCodeBaseSubjectNatrue.其他应收款_利息.getValue());
        model.addAttribute("FINPROD_TYPE_F35", DictCodeListExt3.金融产品类型_期权.getValue());
        
        // 获取 投资目的 数据字典 
        selectDto = this.getSelectDataFromDataDic(DictCode.投资目的, null);
        options = selectDto.getOptions(); 
        List<SelectOptionDto> invOptions = new ArrayList<>();
        // 取债券相关投资目的
        for (SelectOptionDto item : options) {
            if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(item.getId())
                || DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(item.getId())
                || DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(item.getId())) {
                invOptions.add(item);
            }
        }
        model.addAttribute("INV_AIM", JsonUtils.objectArray2Json(invOptions.toArray()));
        
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");
        // 修改
        String seqno = getParam("seqno");
        if (StringUtil.isNotEmpty(seqno) && !"copy".equals(operateType)) {
            ValManualVoucher001Dto entity = valManualVoucher003Service.getBySeqno(seqno);
            setSeq(entity.getSeqno());
            model.addAttribute("entity", entity);
            // 冲销标志默认值
            model.addAttribute("offsetFlag", entity.getOffsetFlag());
            // 借贷方向设一个默认值
            model.addAttribute("cdFlag", entity.getCdFlag());
            // 入账日期默认为系统平台日
            model.addAttribute("sysDate", entity.getBookDate());
            //操作类型标识
            model.addAttribute("optType","modify");
            model.addAttribute("busiName", entity.getBusiName());
            model.addAttribute("busiDetailType", entity.getFinprodType());
        }//流程中
        else if(StringUtil.isNotEmpty(workflowId)){
            ValManualVoucher003Dto entity= valManualVoucher003Service.getBussinessEntityByWorkflowId(workflowId);

            model.addAttribute("entity", entity);
            model.addAttribute("offsetFlag", entity.getOffsetFlag());
            model.addAttribute("workflowId", workflowId);
            
            // 入账日期默认为系统平台日
            model.addAttribute("sysDate",DateUtil.format(entity.getBookDate(), "yyyy-MM-dd"));
            /** add by yanya on 20191030 增加资产代码及业务明细类型**/
            model.addAttribute("busiId", entity.getBusiId());
            model.addAttribute("busiName", entity.getBusiName());
            model.addAttribute("busiDetailType", entity.getBusiDetailType());
        }
        //add by sunshuaishuai 20210225 复制 start
        else if(StringUtil.isNotEmpty(seqno) && operateType.equals("copy")){
            ValManualVoucher001Dto entity = valManualVoucher003Service.getBySeqno(seqno);
            setSeq(entity.getSeqno());
            entity.setSeqno("");
            model.addAttribute("entity", entity);
            // 冲销标志默认值
            model.addAttribute("offsetFlag", entity.getOffsetFlag());
            // 借贷方向设一个默认值
            model.addAttribute("cdFlag", entity.getCdFlag());
            // 入账日期默认为系统平台日
            model.addAttribute("sysDate", entity.getBookDate());
            //操作类型标识
            model.addAttribute("optType","copy");
            model.addAttribute("busiName", entity.getBusiName());
            model.addAttribute("busiDetailType", entity.getFinprodType());
        }
        //add by sunshuaishuai 20210225 end
        else {
            // 冲销标志默认值
            model.addAttribute("offsetFlag", DictCodeList.冲正标识_正常交易.getValue());
            // 借贷方向设一个默认值
            model.addAttribute("cdFlag", DictCodeList.借贷方向_贷.getValue());
            // 入账日期默认为系统平台日
            model.addAttribute("sysDate",
                DateUtil.format(baseCommonService.getSysDate(), "yyyy-MM-dd"));
            //操作类型标识
            model.addAttribute("optType","new");
        }
        
        return super.init(model);
    }

    /**
     * 根据手工凭证编号bookId获取对应相同编号的所有数据
     *
     * @return 记账手工凭证数据
     * @throws Exception 异常
     * @see
     */
    @RequestMapping("searchList")
    @ResponseBody
    public List<ValManualVoucher003AddDto> searchList() {
        // 手工凭证编号
        String bookId = this.getParam("bookId");
        return valManualVoucher003Service.searchList(bookId);
//        if (StringUtil.isNotEmpty(bookId)) {
//            return valManualVoucher003Service.searchList(bookId);
//        }
//        else {
//            return null;
//        }
    }

    /**
     * 修改凭证摘要
     * 
     * @param dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doAdd")
    public AjaxResult doAdd(@Valid @RequestBody ValManualVoucher003AddValidatorDto dto,
                            BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        AjaxResult ajaxResult = new AjaxResult();
//        dto = valManualVoucher003Service.initTableDto(dto);
        ajaxResult.setData(valManualVoucher003Service.initTableDto(dto));
        return ajaxResult;
    }
    
    /**
     * 确认校验
     * 
     * @param dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("doCheck")
    public AjaxResult doCheck(@Valid @RequestBody ValManualVoucher003CheckValidatorDto dto,
                            BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        AjaxResult ajaxResult = new AjaxResult();
        return ajaxResult;
    }

    /**
     * 检索科目代码
     * 
     * @return 账套科目代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getSubjectNo")
    public AjaxResult getSubjectNo(@RequestBody ValManualVoucher003SubjectDto dto)
        throws Exception {
        String booksetId = dto.getBooksetId();
        String subjectNo = dto.getSubjectNo();
//        String busiId = dto.getBusiId();
//        String finprodType = dto.getFinprodType();
//        String ccy = dto.getCcy();//modify by dongjun on 20181211
        if (StringUtil.isNotEmpty(booksetId) && StringUtil.isNotEmpty(subjectNo)) {
            AjaxResult ajaxResult = new AjaxResult();
            ajaxResult.setData(
                valManualVoucher003Service.getSubjectNo(dto));//modify by dongjun on 20181211
            return ajaxResult;
        }
        else {
            return new AjaxResult();
        }
    }
    
    /**
     * 账套变更，获取对应的账套日期
     * 2018年12月17日  魏鹏新增
     * 
     * @return 记账日
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getBookDate")
    public AjaxResult getBookDate(@RequestBody ValManualVoucher003SubjectDto dto)
        throws Exception {
        String booksetId = dto.getBooksetId();
        if (StringUtil.isNotEmpty(booksetId) ) {
            AjaxResult ajaxResult = new AjaxResult();
            BokBooksetDate bokBooksetDate = bokBooksetDateMapper.selectByPrimaryKey(booksetId);
            if(bokBooksetDate != null){
                ajaxResult.setData(DateUtil.format(bokBooksetDate.getBooksetDate()));
            }
            return ajaxResult;
        }
        else {
            return new AjaxResult();
        }
        
    }
    
    /**
     * 检索科目代码
     * 
     * @return 账套科目代码下拉框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getSubjectNoPullDw")
    public List<SelectOptionDto> getSubjectNoPullDw()
        throws Exception {
//        String param = this.getParam("param");
        String booksetId = this.getParam("booksetId");
        if (StringUtil.isNotEmpty(booksetId)) {
            return valManualVoucher003Service.getSubjectNoPullDw(booksetId);
        }
        else {
            return null;
        }
    }
    
    /**
     * 获取资产代码下拉框
     * 
     * @return 资产代码代码下拉框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getAcctId")
    public List<SelectOptionDto> getAcctId()
        throws Exception {
        String param = this.getParam("param");
        String booksetId = this.getParam("booksetId");
        if (StringUtil.isNotEmpty(booksetId)) {
            return valManualVoucher003Service.getAcctId(booksetId, param);
        }
        else {
            return null;
        }
    }
    
    /**
     * 获取分支序号下拉框
     * 
     * @return 分支序号下拉框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getBranch")
    public List<SelectOptionDto> getBranch()
        throws Exception {
        String param = this.getParam("param");
        String booksetId = this.getParam("booksetId");
        String finprodType = this.getParam("finprodType");
        String subjectNo = this.getParam("subjectNo");
        String finprodId = this.getParam("finprodId");
        if (StringUtil.isNotEmpty(booksetId) || StringUtil.isNotEmpty(finprodType)
            || StringUtil.isNotEmpty(subjectNo) || StringUtil.isNotEmpty(finprodId)) {
            return valManualVoucher003Service.getBranch(booksetId,finprodType, subjectNo, finprodId, param);
        }
        else {
            return null;
        }
    }

    /**
     * 检索资产代码
     * 
     * @return 资产代码下拉框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getBusiId")
    public List<SuggestDto> getBusiId() {
        // 业务类型
        String finprodType = this.getParam("finprodType");
        // 帐套代码
        String booksetId = this.getParam("booksetId");
        String param = this.getParam("param");
        return valManualVoucher003Service.getBusiId(finprodType, booksetId, param);
    }

//    /**
//     * 修改凭证摘要
//     * 
//     * @param dto 参数
//     * @param result 校验结果
//     * @return AjaxResult
//     * @see
//     * @since 1.0
//     */
//    @ResponseBody
//    @RequestMapping("doSubmit")
//    public AjaxResult doSubmit(@Valid @RequestBody ValManualVoucher003Dto dto,
//                               BindingResult result) {
//        if (result != null && result.hasErrors()) {
//            return new AjaxResult(result);
//        }
//        AjaxResult ajaxResult = new AjaxResult(getMessage("COMMON_I_0001"));
//        valManualVoucher003Service.doSubmit(dto);
//        return ajaxResult;
//    }

    /**
     * 手工凭证 - 新建 - 走流程
     * 
     * @param dto 参数
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(value = "doSave", method=RequestMethod.POST)
    public AjaxResult doSave(@RequestBody @Valid ValManualVoucher003Dto dto, BindingResult br) {
        
        if(br.hasErrors()){
            return new AjaxResult(br);
        }
        
        // 启动流程
        ProcessParameter<ValManualVoucher003Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<ValManualVoucher003Dto> params = new WorkFlowParameter<>();

        String s="手工凭证修改：";
        //设置主键，即【即TRD_VOUCH_DEAL（手工凭证交易表）中的TRADE_ID（交易编号）】
        if(!StringUtil.isNotEmpty(dto.getSeqno())){
            dto.setSeqno(interfCommonService.getTradeid("T28", dto.getBookDate(), "A"));
            s="手工凭证新建：";
        }
        
        // 业务代码（凭证流水号）
        params.setBusinessCode(dto.getSeqno());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_手工凭证.getValue());
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
        
        //设置流程业务说明, entity即传入的dto
        String finalS = s;
        params.setComment(entity -> WorkFlowUtil.concatComment(finalS,
            entity.getSeqno(),//凭证流水号
            DateUtil.format(entity.getBookDate()),//记账日期
            entity.getBooksetId()//账套代码
            ));
        
        // 塞入流程参数
        proParam.setWorkFlowParameter(params);
        
        String msg = getMessage("COMMON_I_0001");
        try {
            msg = valManualVoucher003Service.start(proParam, dto);
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }
    
    
    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @param operateType 操作类型
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
   public AjaxResult check(@RequestBody @Valid ValManualVoucher003Dto dto, BindingResult result, String operateType) {
        return commonCheck(result, operateType);
    }
    
    /**
     * 根据流程id，获取对应的凭证新建时的分录信息
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/getValManualVoucher003AddDtoList")
    @ResponseBody
    public List<ValManualVoucher003AddDto> getValManualVoucher003AddDtoList() {
        
        List<ValManualVoucher003AddDto> returnList = new ArrayList<>();
        
        Map<String,Object> map=RequestUtil.getPageParam();
        
        String workflowId = (String)map.get("workflowId");

        String tmpCode = String.valueOf(map.get("tmpCode"));

        String seqno=getSeq();
        
        //根据流程id获取对应的凭证新建时的分录信息
        if(workflowId!=null && workflowId.length()>0){
            ValManualVoucher003Dto entity= valManualVoucher003Service.getBussinessEntityByWorkflowId(workflowId);
            returnList = entity.getListAddDto();
        }
        //add by sunshuaishuai 编辑，复制时加载表格 20210226 start
        else if(!StringUtil.isNotEmpty(workflowId) && seqno!=null && seqno.length()>0){
            List<ValManualVoucher003AddDto> entity = valManualVoucher003Service.searchList1(seqno);
            returnList = entity;
            setSeq("");
        }else if(StringUtil.isNotEmpty(tmpCode) &&  tmpCode!=null && !StringUtil.isNotEmpty(workflowId)){
            List<ValManualVoucherTemplete003AddDto> list = valManualVoucher003Service.quickInputList(tmpCode);
            List<ValManualVoucher003AddDto> dtoList=new ArrayList<ValManualVoucher003AddDto>();
            for(ValManualVoucherTemplete003AddDto dto:list){
                ValManualVoucher003AddDto addDto = new ValManualVoucher003AddDto();
                addDto.setSubjectNo(dto.getSubjectNo());
                addDto.setSubjectName(dto.getSubjectName());
                addDto.setcAmt(dto.getcAmt());
                addDto.setdAmt(dto.getdAmt());
                addDto.setCcy(dto.getCcy());
                addDto.setHappenNumber(dto.getHappenNumber());
                addDto.setHappenAmt(dto.getoHappenAmt());
                dtoList.add(addDto);
            }
            returnList = dtoList;
        }
        //add by sunshuaishuai end
        // 因为分录信息中的【分支序号】实体类中设置的类型为【Long】,当不传递值的时候，会默认给一个0。
        // 故判断当前分支序号若为0，显示为空
        if (returnList != null) {
            for(ValManualVoucher003AddDto dto:returnList){
                if(new Long(0).equals(dto.getBranch())){
                    dto.setBranch(null);
                }
            }
        }else{
            // 若为空给予一个空list，不能为null，否则前台报错。
            returnList = new ArrayList<ValManualVoucher003AddDto>();
        }
        return returnList;
    }
    /**
     * 检索账套代码(只取理财账套)
     * 
     * @return 账套代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getBooksetId2")
    public List<SuggestDto> getBooksetId2()
        throws Exception {
//        String param = super.getRequest().getParameter("param");
        return valAssetBook001Service.getBooksetId2(RequestUtil.getPageParam());
    }
    
    /**modify by dongjun on 20181214 start **/
    /**
     * 
     * 手工凭证科目号联想
     * 
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAllSubjectNo")
    public List<SelectOptionDto> getAllSubjectNo(){
        Map<String,Object> para = RequestUtil.getPageParam();
        para.put("level3", DictCodeListExt1.科目级别_3级.getValue());
        List<SelectOptionDto> dtolist = valManualVoucher003Service.querySubctNo(para);
        return dtolist;
    }
    /**modify by dongjun on 20181214 end **/
    
    /**
     * 获取投资目的
     * 
     * @return 投资目的下拉框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getInvAim")
    public AjaxResult getInvAim(@RequestBody ValManualVoucher003Dto dto)
        throws Exception {
        String busiId = dto.getBusiId();
        String finprodType = dto.getFinprodType();//add by wujie on 20201110
        if (StringUtil.isNotEmpty(busiId) && (
        		DictCodeListExt1.金融产品类型_债券.getValue().equals(finprodType)
        		|| DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodType)
        		)) {
            AjaxResult ajaxResult = new AjaxResult();
            Map<String, Object> params = new HashMap<>();
            params.put("busiId", dto.getBusiId());
            params.put("booksetId", dto.getBooksetId());
//            params.put("busiDetailType", DictCodeListExt1.金融产品类型_债券.getValue());
            params.put("busiDetailType", finprodType);
            params.put("invAimSell",  DictCodeListExt1.投资目的_卖空.getValue());
            BokDetailIdDto bokDetailId = valManualVoucher003Service.getInvAim(params);
            if(bokDetailId != null){
                ajaxResult.setData(bokDetailId);
            }
            return ajaxResult;
        }
        else {
            return new AjaxResult();
        }
    }

    //add by sunshuaishuai 20210224 start
    /**
     * 检索模板代码
     *
     * @return 模板代码联想框内容
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTmpCode")
    public List<SuggestDto> getTmpCode() throws Exception {
        return valManualVoucher003Service.getTmpCode(RequestUtil.getPageParam());
    }


    /**
     * 快录
     *
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/quickInput")
    public ValManualVoucherTemplete003Dto quickInput() throws Exception {
        return valManualVoucher003Service.quickInput(RequestUtil.getPageParam());
    }

    /**
     * 快录时，模板代码校验
     *
     * @return AjaxResult
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/checkTmpCode")
    public AjaxResult checkTmpCode() throws Exception {
        AjaxResult ajaxResult = new AjaxResult();
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        String tmpCode = this.getParam("tmpCode");
        String booksetId = this.getParam("booksetId");
        //点击快录，检验账套是否为空
        if(!StringUtil.isNotEmpty(booksetId)){
            errorList.add(new ValidMessage(getMessage("MANUAL_VOUCHER_E_014")));
            ajaxResult.setErrorList(errorList);
            return ajaxResult;
        }
        //校验输入的模板代码是否有对应的模板
        if(StringUtil.isNotEmpty(booksetId) && StringUtil.isNotEmpty(tmpCode)) {
            String tmpName = valManualVoucher003Service.checkTmpCode(tmpCode);
            if (StringUtil.isNotEmpty(tmpName)) {
                ajaxResult.setData(tmpName);
                return ajaxResult;
            } else {
                errorList.add(new ValidMessage(getMessage("MANUAL_VOUCHER_E_013")));
                ajaxResult.setErrorList(errorList);
                return ajaxResult;
            }
        }else{
            errorList.add(new ValidMessage(getMessage("MANUAL_VOUCHER_E_015")));
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 快录时，科目尾号联动
     *
     * @return 科目尾号联动
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getDetailSubjectNo")
    public AjaxResult getDetailSubjectNo() {
        Map<String,Object> map=RequestUtil.getPageParam();
        String booksetId = (String)map.get("booksetId");
        String datas = this.getParam("datas");
        String busiId=(String)map.get("busiId");
        String finprodType=(String)map.get("finprodType") ;
        List<ValManualVoucher003AddDto> listdto = null;
        try {
            listdto = JsonUtils.json2List(datas, ValManualVoucher003AddDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,String> params=new HashMap<>();
        params.put("booksetId",booksetId);
        params.put("busiId",busiId);
        params.put("finprodType",finprodType);
        List<ValManualVoucher003SubjectDto> list = new ArrayList<ValManualVoucher003SubjectDto>();
        for(ValManualVoucher003AddDto dto : listdto){
            ValManualVoucher003SubjectDto subjectDto=new ValManualVoucher003SubjectDto();
            String subjectNo= dto.getSubjectNo();
            String ccy=dto.getCcy();
            params.put("subjectNo",subjectNo);
            params.put("ccy",ccy);
            if(StringUtil.isNotEmpty(subjectNo)){
                subjectDto=valManualVoucher003Service.getDetailSubjectNo(params);
            }
            list.add(subjectDto);
        }

        if (StringUtil.isNotEmpty(booksetId) && StringUtil.isNotEmpty(datas)) {
            AjaxResult ajaxResult = new AjaxResult();
            ajaxResult.setData(list
                    );
            return ajaxResult;
        }
        else {
            return new AjaxResult();
        }
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
    //add by sunshuaishuai end
}
