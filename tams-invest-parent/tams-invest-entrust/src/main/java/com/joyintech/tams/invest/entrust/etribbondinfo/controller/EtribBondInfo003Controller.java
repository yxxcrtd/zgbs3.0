/*
 * 文件名：EtribBondInfo003Controller.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月19日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.invest.entrust.etribbondinfo.controller;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
//import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.BussCombDistribtDto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtrIbBondinfo002Dto;
import com.joyintech.tams.invest.entrust.etribbondinfo.dto.EtribBondInfo003Dto;
import com.joyintech.tams.invest.entrust.etribbondinfo.service.EtrIbBondinfo002Service;
import com.joyintech.tams.invest.entrust.etribbondinfo.service.EtribBondInfo003Service;
import com.joyintech.tams.invest.entrust.etribbondinfo.validator.EtribBondInfo003Validator;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 银行间债券指令- 多组合指令新建
 * @author 王波
 * @date 2021年01月19日
 * @see EtribBondInfo003Controller
 * @since 1.0
 */
@Controller
@PageId("EtribBondInfo003")
@RequestMapping("/invest/entrust/etribbondinfo/EtribBondInfo003")
public class EtribBondInfo003Controller extends BaseController {

    @Autowired
    private EtribBondInfo003Service etribBondInfo003Service;

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
        EtribBondInfo003Dto entity = null;
        boolean todoList = false;
        // 代办中过来
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            entity = etribBondInfo003Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("calculateField",entity.getCalculateField());
            model.addAttribute("person_entity", JsonUtils.object2Json(entity.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);
            model.addAttribute("entity_BussCombDistribtList",  JsonUtils.object2Json(entity.getBussCombDistribtList()));
            todoList = true;
            model.addAttribute("businessId", entity.getTotalEtrustId());
        } else {
            entity = new EtribBondInfo003Dto();
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
        model.addAttribute("CCY", DictCodeList.币种_人民币_CNY.getValue());
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("TodoList", todoList);
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        return super.init(model);
    }

    /**
     * 获取组合分配的相关值
     * @param param 参数
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPortfolioAllocationValues")
    public Map<String, Object> getPortfolioAllocationValues(@RequestBody Map<String, Object> param) {
        return etribBondInfo003Service.getPortfolioAllocationValues(param);
    }

    /**
     *  保存按钮时check
     * @param dtoList
     * @param result
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) List<EtribBondInfo003Dto> dtoList, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        List<ValidMessage> messages = new ArrayList<>();
        boolean chlId = false;
        if(dtoList.size()>0) {
            messages = etribBondInfo003Validator.bussValid(dtoList.get(0));
            EtribBondInfo003Dto dto = dtoList.get(0);

            if(!StringUtil.isNotEmpty(dto.getPortfolioSeries())){
                messages.add(new ValidMessage("portfolioSeries", "请输入组合系列。"));
            }
            if(!StringUtil.isNotEmpty(dto.getFinprodMarketId())){
                messages.add(new ValidMessage("finprodId", "请输入资产简称。"));
            }
            if(!StringUtil.isNotEmpty(dto.getPs())){
                messages.add(new ValidMessage("ps", "请输入交易方向。"));
            }
            if(!StringUtil.isNotEmpty(dto.getVdate()))
                messages.add(new ValidMessage("vdate", "请输入指令开始日。"));
			/*if(!StringUtil.isNotEmpty(dtoList.get(0).getTradePlat()))
				messages.add(new ValidMessage("tradePlat", "请输入交易平台。"));*/
            if(!StringUtil.isNotEmpty(dto.getMdate()))
                messages.add(new ValidMessage("mdate", "请输入指令终止日。"));
            if (!StringUtil.isNotEmpty(dtoList.get(0).getTradeMarketMode()))
                messages.add(new ValidMessage("tradeMarketMode", "请输入交易市场！"));
            if(!StringUtil.isNotEmpty(dtoList.get(0).getSettleSpeed()))
                messages.add(new ValidMessage("settleSpeed", "请输入清算速度！"));
            if (StringUtil.isNotEmpty(dtoList.get(0).getChlId())) {
                chlId = true;
            }
            if(!StringUtil.isNotEmpty(dtoList.get(0).getPriceModel())){
                messages.add(new ValidMessage("priceModel", "请输入价格模式！"));
            }
//            if(!StringUtil.isNotEmpty(dtoList.get(0).getCounterId())){
//                messages.add(new ValidMessage("counterId", "请选择交易对手！"));
//            }
//            if(!StringUtil.isNotEmpty(dtoList.get(0).getCalculateField())){
//                messages.add(new ValidMessage("calculateField", "净价、到期收益率、行权收益率三选一！"));
//            }
            // 价格模式check
            if (StringUtil.isNotEmpty(dto.getPriceModel()) &&
                    (StringUtil.equals(dto.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())
                            || StringUtil.equals(dto.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue()))) {
                // 价格模式选择限价指令，净价必填
               if (dto.getPrice() == null ) {
                   messages.add(new ValidMessage("price", "请输入净价。"));
               }
            }
        }
        for(int i = 0; i< dtoList.size(); i++) {
            List<ValidMessage> messageslist = etribBondInfo003Validator.valid(dtoList.get(i));
            if(!StringUtil.isEmptyList(messageslist)){
                if(!StringUtil.isEmptyList(messages)){
                    for(ValidMessage message: messageslist){
                        boolean flag = true;
                        for(ValidMessage msg :messages){
                            //错误不一致  显示错误
                            if(StringUtil.equals(message.getMessage(), msg.getMessage())){
                                flag = false;;
                            }
                        }
                        if(flag){
                            messages.add(message);
                        }
                    }
                }else{
                    for(ValidMessage message: messageslist){
                        //显示错误
                        messages.add(message);
                    }
                }

            }
            if(!StringUtil.isNotEmpty(dtoList.get(i).getPortfolioId()))
                messages.add(new ValidMessage("请输入组合代码(第"+(i+1)+"行)。"));
            if(!StringUtil.isNotEmpty(dtoList.get(i).getEntrustNum()))
                messages.add(new ValidMessage("请输入券面总额(第"+(i+1)+"行)。"));
//            if(StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"),"02")){
//                if(!StringUtil.isNotEmpty(dtoList.get(i).getBusinessModel()))
//                    messages.add(new ValidMessage("请输入业务模式(第"+(i+1)+"行)。"));
//            }else{
                if(!StringUtil.isNotEmpty(dtoList.get(i).getInvAim()))
                    messages.add(new ValidMessage("请输入投资目的(第"+(i+1)+"行)。"));
//            }
            if(!StringUtil.isNotEmpty(dtoList.get(i).getSecAcctId()))
                messages.add(new ValidMessage("请输入证券账户(第"+(i+1)+"行)。"));
            if(chlId && StringUtil.isNotEmpty(dtoList.get(i).getSecAcctId()))
                messages.add(new ValidMessage("不能同时输入通道代码和证券账户(第"+(i+1)+"行)。"));
        }

        if(messages.size()>0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }


        for(int i = 0; i< dtoList.size(); i++) {
            EtribBondInfo003Dto dto = dtoList.get(0);
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
                    && dto.getProAvailableCash() != null) {
                if (dto.getEntrustNum().compareTo(dto.getProAvailableCash()) == 1) {
                    AjaxResult ar = new AjaxResult();
                    ar.setData(this.getMessage("TRD_C_0002") + "(第"+(i+1)+"行)。");
                    return ar;
                }
            }
            if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
                    DictCodeListExt1.是或否_是.getValue())
                    && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
                    && dto.getAssetAvailablePosition() != null) {
                if (dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0) {
                    AjaxResult ar = new AjaxResult();
                    ar.setData(this.getMessage("ETR_BOND_002")+ "(第"+(i+1)+"行)。");
                    return ar;
                }
            }
        }

        return commonCheck(result, dtoList.get(0).getOperateType());
    }

    /**
     * 批量提交
     *
     * @param bondInfoDto EtrInvestBondInfoDto
     * @param br msg
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtribBondInfo003Dto bondInfoDto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        // ----------add by zhangrui1 20200715 预占功能 start----------
        List<EtribBondInfo003Dto> dataList = new ArrayList<>();
        // ----------add by zhangrui1 20200715 预占功能 end----------
        // 针对每个单组合绑定附件
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(bondInfoDto.getBusinessId());
        for(EtribBondInfo003Dto dto: bondInfoDto.getEtrInvestBondInfoDtos()) {
            List<String> fileNos = new ArrayList<>();
            if(!dto.equals(bondInfoDto.getEtrInvestBondInfoDtos().get(0))) {
                if (CollectionUtils.isNotEmpty(files)) {
                    for (UploadFile uploadFile : files) {
                        String generateId = IDGenerator.generate();
                        uploadFile.setFileNo(generateId);
                        fileAttachmentProcess.upload(uploadFile);
                        fileNos.add(generateId);
                    }
                }

            } else {
                for(UploadFile uploadFile : files) {
                    fileNos.add(uploadFile.getFileNo());
                }
            }

            if(!StringUtil.isNotEmpty(dto.getEntrustId())){
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
                String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
                dto.setEntrustId(code);

            }
            //默认证券账户
            if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("portfolioId", dto.getPortfolioId());
                map.put("finprodId", dto.getFinprodId());
                String secAcctId = etrIbBondinfo002Service.qryPortfolioSecAcctId(map);
                if (StringUtil.isNotEmpty(secAcctId)) {
                    dto.setSecAcctId(secAcctId); // 证券账户
                }
            }
            dto.setEntrustOwnNum(dto.getEntrustNum());
            dto.setEntrustIntegrateNum(BigDecimal.ZERO);
            // 文件与指令ID绑定
            fileAttachmentProcess.updateBussinessNo(fileNos,dto.getEntrustId());


            // ----------add by zhangrui1 20200715 预占功能 start----------
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                dataList.add(dto);
            }
            // ----------add by zhangrui1 20200715 预占功能 end----------
        }
        if (StringUtil.equals(BussinessConstant.SUBMIT, bondInfoDto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(
                    dataList, DictCodeList.平安资产类型_债券.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                    true, bondInfoDto.getEtrInvestPersonInfo());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        EntrustRdsCommonAop.riskListToChildren(bondInfoDto.getRiskTrialRetDto(), bondInfoDto.getEtrInvestBondInfoDtos(),
                (dto, list) -> dto.setRiskTrialRetDto(list));

        // 临时主键
        BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
        String generatedId = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
        bondInfoDto.setTotalEtrustId(generatedId);
        for (int i = 0; i < bondInfoDto.getBussCombDistribtList().size(); i++) {
            bondInfoDto.getBussCombDistribtList().get(i).setEntrustId(bondInfoDto.getEtrInvestBondInfoDtos().get(i).getEntrustId());
        }
        if (CollectionUtils.isNotEmpty(bondInfoDto.getFileArray())) {
            List<UploadFile> uploadFiles = new ArrayList<>();
            for (SysUploadFileInfo file:  bondInfoDto.getFileArray()) {
                uploadFiles.add(fileAttachmentProcess.fetch(file.getSeqNo()));
            }
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    uploadFiles.stream().map(UploadFile::getFileNo).collect(
                           Collectors.toList()),
                    bondInfoDto.getTotalEtrustId());
        }
        ProcessParameter<EtribBondInfo003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<EtribBondInfo003Dto> params = new WorkFlowParameter<>();
        params.setPageId("EtribBondInfo003");
        params.setElementId(bondInfoDto.getEtrInvestBondInfoDtos().get(0).getElementId());
        params.setBusinessCode(EtribBondInfo003Dto::getTotalEtrustId);
        params.setBusinessType(DictCodeList.共通业务类型_债券投资委托.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> this.getMessage("EtribBondInfo003") + "(" + entity.getTotalEtrustId() + ")"
                        + "-" + this.getMessage("EtribBondInfo003.ps")+"("+ SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
                        + this.getMessage("EtribBondInfo003.vdate") + ":" + DateUtil.format(entity.getVdate())
                        + ")-(" + this.getMessage("EtribBondInfo003.mdate") + ":"
                        + DateUtil.format(entity.getMdate())+ ")-"
                        + this.getMessage("EtribBondInfo003.entrustAmt") + ":"
                        + entity.getEntrustNum().divide(new BigDecimal("10000")));
        processParameter.setWorkFlowParameter(params);

        String msg = etribBondInfo003Service.start(processParameter, bondInfoDto);
        return new AjaxResult(msg);
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
    @RequestMapping("/calData")
    @ResponseBody
    public AjaxResult calData(@RequestBody EtribBondInfo003Dto dto) {
        AjaxResult result = new AjaxResult();
        etribBondInfo003Service.calcAmt(dto);
        result.setData(dto);
        return result;
    }

}
