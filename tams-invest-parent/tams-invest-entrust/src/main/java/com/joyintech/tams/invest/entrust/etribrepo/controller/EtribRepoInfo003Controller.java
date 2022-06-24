package com.joyintech.tams.invest.entrust.etribrepo.controller;

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
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
//import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etribbondinfo.service.EtribBondInfo003Service;
import com.joyintech.tams.invest.entrust.etribrepo.dto.EtribRepoInfo003Dto;
import com.joyintech.tams.invest.entrust.etribrepo.service.EtribRepoInfo003Service;
import com.joyintech.tams.invest.entrust.etribrepo.validator.EtribRepoInfo003Validator;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 银行间质押式回购指令- 多组合指令新建
 * @author wl
 * @date 2021年02月02日
 * @see EtribRepoInfo003Controller
 * @since 1.0
 */
@Controller
@PageId("EtribRepoInfo003")
@RequestMapping("/invest/entrust/etribrepo/EtribRepoInfo003")
public class EtribRepoInfo003Controller extends BaseController {

    @Autowired
    private EtribRepoInfo003Service etribRepoInfo003Service;

    @Autowired
    private EtribRepoInfo003Validator etribRepoInfo003Validator;

    @Autowired
    private EtribBondInfo003Service etribBondInfo003Service;

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
        this.setSelectOptionToModel(model, DictCode.回购拆借方向);
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.回购类型);
        this.setSelectOptionToModel(model, DictCode.回购期限品种);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.投资目的);
        this.setSelectOptionToModel(model, DictCode.交易平台);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.组合系列);
        Date sysDate = DateUtil.platDateFromCache();
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        EtribRepoInfo003Dto entity = null;
        boolean todoList = false;
        // 代办中过来
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            entity = etribRepoInfo003Service.getBussinessEntityByWorkflowId(
                    workflowId);
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(entity.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);
            model.addAttribute("entity_BussCombDistribtList",  JsonUtils.object2Json(entity.getBussCombDistribtList()));
            model.addAttribute("entity_PledgeBondTotalListDetailed",  JsonUtils.object2Json(entity.getPledgeBondTotalListDetailed()));
            todoList = true;
            model.addAttribute("businessId", entity.getTotalEtrustId());
        } else {
            entity = new EtribRepoInfo003Dto();
            entity.setInvManagerId(LoginUserUtil.getUserName());
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            entity.setCalendarId(BussinessDictCodeList.wind交易场所_银行间.getValue());
            entity.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            entity.setSecAcctId("999999");
            model.addAttribute(GlobalConstant.ENTITY, entity);
            model.addAttribute("businessId", IDGenerator.generate());
        }

        model.addAttribute("ps_03", DictCodeList.回购拆借方向_正回购.getValue());
        model.addAttribute("ps_04", DictCodeList.回购拆借方向_逆回购.getValue());
        model.addAttribute("tradeP01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("tradeP02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("tradeP03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("repoTypeH01", DictCodeList.回购类型_质押式协议回购.getValue());
        model.addAttribute("repoTypeH02", DictCodeList.回购类型_质押式竞价回购.getValue());
        model.addAttribute("speed0", DictCodeList.清算速度_T_0.getValue());
        model.addAttribute("speed1", DictCodeList.清算速度_T_1.getValue());
        model.addAttribute("GC001", DictCodeList.回购期限品种_GC001.getValue());
        model.addAttribute("GC002", DictCodeList.回购期限品种_GC002.getValue());
        model.addAttribute("GC003", DictCodeList.回购期限品种_GC003.getValue());
        model.addAttribute("GC004", DictCodeList.回购期限品种_GC004.getValue());
        model.addAttribute("GC007", DictCodeList.回购期限品种_GC007.getValue());
        model.addAttribute("GC014", DictCodeList.回购期限品种_GC014.getValue());
        model.addAttribute("GC028", DictCodeList.回购期限品种_GC028.getValue());
        model.addAttribute("GC091", DictCodeList.回购期限品种_GC091.getValue());
        model.addAttribute("GC182", DictCodeList.回购期限品种_GC182.getValue());
        model.addAttribute("R001", DictCodeList.回购期限品种_R001.getValue());
        model.addAttribute("R007", DictCodeList.回购期限品种_R007.getValue());
        model.addAttribute("R014", DictCodeList.回购期限品种_R014.getValue());
        model.addAttribute("R021", DictCodeList.回购期限品种_R021.getValue());
        model.addAttribute("RC001", DictCodeList.回购期限品种_RC001.getValue());
        model.addAttribute("RC003", DictCodeList.回购期限品种_RC003.getValue());
        model.addAttribute("RC007", DictCodeList.回购期限品种_RC007.getValue());
        model.addAttribute("R_001", DictCodeList.回购期限品种_R_001.getValue());
        model.addAttribute("R_002", DictCodeList.回购期限品种_R_002.getValue());
        model.addAttribute("R_003", DictCodeList.回购期限品种_R_003.getValue());
        model.addAttribute("R_004", DictCodeList.回购期限品种_R_004.getValue());
        model.addAttribute("R_007", DictCodeList.回购期限品种_R_007.getValue());
        model.addAttribute("R_014", DictCodeList.回购期限品种_R_014.getValue());
        model.addAttribute("R_028", DictCodeList.回购期限品种_R_028.getValue());
        model.addAttribute("R_091", DictCodeList.回购期限品种_R_091.getValue());
        model.addAttribute("R_182", DictCodeList.回购期限品种_R_182.getValue());
        model.addAttribute("R1M", DictCodeList.回购期限品种_R1M.getValue());
        model.addAttribute("R2M", DictCodeList.回购期限品种_R2M.getValue());
        model.addAttribute("R3M", DictCodeList.回购期限品种_R3M.getValue());
        model.addAttribute("R4M", DictCodeList.回购期限品种_R4M.getValue());
        model.addAttribute("R6M", DictCodeList.回购期限品种_R6M.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        model.addAttribute("fintype_zhg", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("fintype_nhg", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("TodoList", todoList);
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("repo_pledge_model",CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
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
            ValidateGroupEdit.class}) List<EtribRepoInfo003Dto> dtoList, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        List<ValidMessage> messages = new ArrayList<>();
        boolean chlId = false;
        if(dtoList.size()>0) {
            messages = etribRepoInfo003Validator.bussValid(dtoList.get(0));
            EtribRepoInfo003Dto dto = dtoList.get(0);
            if(!StringUtil.isNotEmpty(dto.getPortfolioSeries()))
                messages.add(new ValidMessage("portfolioSeries", "请输入组合系列。"));
            if(!StringUtil.isNotEmpty(dto.getPriceModel()))
                messages.add(new ValidMessage("priceModel", "请输入价格模式。"));
            if(!StringUtil.isNotEmpty(dto.getVdate()))
                messages.add(new ValidMessage("vdate", "请输入指令开始日。"));
			/*if(!StringUtil.isNotEmpty(dtoList.get(0).getTradePlat()))
				messages.add(new ValidMessage("tradePlat", "请输入交易平台。"));*/
            if(!StringUtil.isNotEmpty(dto.getMdate()))
                messages.add(new ValidMessage("mdate", "请输入指令终止日。"));
            if(!StringUtil.isNotEmpty(dtoList.get(0).getSettleSpreed()))
                messages.add(new ValidMessage("settleSpreed", "请输入清算速度。"));
            if(!StringUtil.isNotEmpty(dtoList.get(0).getRepoTerm()))
                messages.add(new ValidMessage("repoTerm", "请输入回购期限。"));
            if (StringUtil.isNotEmpty(dtoList.get(0).getChlId())) {
                chlId = true;
            }
            // 价格模式check
            if (StringUtil.isNotEmpty(dto.getPriceModel()) &&
                    (StringUtil.equals(dto.getPriceModel(), DictCodeListExt2.价格模式_指定价格.getValue())
                            || StringUtil.equals(dto.getPriceModel(), DictCodeListExt2.价格模式_限价指令.getValue()))) {
                // 价格模式选择限价指令，净价必填
               if (dto.getPrice() == null ) {
                   messages.add(new ValidMessage("settleSpeed", "请输入利率。"));
               }
            }
        }
        for(int i = 0; i< dtoList.size(); i++) {
            List<ValidMessage> messageslist = etribRepoInfo003Validator.valid(dtoList.get(i));
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
            if(!StringUtil.isNotEmpty(dtoList.get(i).getEntrustAmt()))
                messages.add(new ValidMessage("请输入指令金额(第"+(i+1)+"行)。"));
            
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL),"03")
            		&&StringUtil.isNotEmpty(dtoList.get(0).getPs())
            		&&StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue(), dtoList.get(0).getPs())
            		&&!StringUtil.isNotEmpty(dtoList.get(i).getZyqk())){
                messages.add(new ValidMessage("请输入质押券库(第"+(i+1)+"行)。"));
            }
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL),"02")
            		&&StringUtil.isNotEmpty(dtoList.get(0).getPs())
            		&&StringUtil.equals(DictCodeList.回购拆借方向_正回购.getValue(), dtoList.get(0).getPs())){
            	if(StringUtil.isNotEmpty(dtoList.get(i).getPortfolioId())){
            	
            	}
//                messages.add(new ValidMessage("请输入质押券库(第"+(i+1)+"行)。"));
            }
        }

        if(messages.size()>0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }

        // 可用现金软校验
        for(EtribRepoInfo003Dto dto: dtoList) {
        	// 可用现金软校验
            if (StringUtil.equals(dto.getPs(), DictCodeList.回购拆借方向_逆回购.getValue())
                && dto.getProAvailableCash() != null) {
               if (dto.getEntrustAmt().compareTo(dto.getProAvailableCash()) == 1) {
                   AjaxResult ar = new AjaxResult();
                   ar.setData(this.getMessage("TRD_C_0002"));
                   return ar;
               }
            }
        }

        return commonCheck(result, dtoList.get(0).getOperateType());
    }

    /**
     * 批量提交
     *
     * @param RepoInfoDto EtrInvestRepoInfoDto
     * @param br msg
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtribRepoInfo003Dto RepoInfoDto, BindingResult br)
            throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        RepoInfoDto.setAssetType1(DictCodeList.回购类型_质押式回购.getValue());
        // ----------add by zhangrui1 20200715 预占功能 start----------
        List<EtribRepoInfo003Dto> dataList = new ArrayList<>();
        // ----------add by zhangrui1 20200715 预占功能 end----------
        // 针对每个单组合绑定附件
        List<UploadFile> files = fileAttachmentProcess.fetchByBusinessCode(RepoInfoDto.getBusinessId());
        for(EtribRepoInfo003Dto dto: RepoInfoDto.getEtrInvestRepoInfoDtos()) {
            // TISHKS-270:多组合指令回购类型展示为空 fuyongsheng 20211112 add start
            dto.setAssetType1(DictCodeList.回购类型_质押式回购.getValue());
            // TISHKS-270:多组合指令回购类型展示为空 fuyongsheng 20211112 add end
            List<String> fileNos = new ArrayList<>();
            if(!dto.equals(RepoInfoDto.getEtrInvestRepoInfoDtos().get(0))) {
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

            if (StringUtil.isEmpty(dto.getEntrustId())) {
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_回购投资委托);
                String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(
                    param) : IDGenerator.generate();
                dto.setEntrustId(code);
            }
            // 文件与指令ID绑定
            fileAttachmentProcess.updateBussinessNo(fileNos,dto.getEntrustId());

            // ----------add by zhangrui1 20200715 预占功能 start----------
            if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                dataList.add(dto);
            }
            // ----------add by zhangrui1 20200715 预占功能 end----------
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL), "02")
            		||StringUtil.equals(RepoInfoDto.getPs(), DictCodeListExt1.回购方向_正回购.getValue())){
            	dto.setBussRepoSec(new BussRepoSecDto());
                dto.getBussRepoSec().setPageFrom(this.getPageId());;
                List<PledgeBondInfoDto> pledgeBondList= new ArrayList<>();
                // ZGZGST-2152: 回购方向是逆回购时,质押券信息列表明细是null,增加非空判断 fuyongsheng 20210907 edit start
                if(CollectionUtils.isNotEmpty(RepoInfoDto.getPledgeBondTotalListDetailed())){
                    for(PledgeBondInfoDto pledgeBondInfoDto :RepoInfoDto.getPledgeBondTotalListDetailed()){
                        if(StringUtil.equals(dto.getPortfolioId(), pledgeBondInfoDto.getPortfolioId())){
                            //组合代码相同
                            pledgeBondList.add(pledgeBondInfoDto);
                        }
                    }
                }
                // ZGZGST-2152: 回购方向是逆回购时,质押券信息列表明细是null,增加非空判断 fuyongsheng 20210907 edit end
                dto.getBussRepoSec().setPledgeBondList(pledgeBondList);
            }
        }
        if (StringUtil.equals(BussinessConstant.SUBMIT, RepoInfoDto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(
                    dataList, DictCodeList.平安资产类型_质押式回购.getValue(),
                    BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                    true, RepoInfoDto.getEtrInvestPersonInfo());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        EntrustRdsCommonAop.riskListToChildren(RepoInfoDto.getRiskTrialRetDto(), RepoInfoDto.getEtrInvestRepoInfoDtos(),
                (dto, list) -> dto.setRiskTrialRetDto(list));

        // 临时主键
        BussinessCodeParameter param = new BussinessCodeParameter();
        param.setCommonBusinessType(DictCodeList.共通业务类型_回购投资委托);
        String generatedId = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
        RepoInfoDto.setTotalEtrustId(generatedId);
        for (int i = 0; i < RepoInfoDto.getBussCombDistribtList().size(); i++) {
            RepoInfoDto.getBussCombDistribtList().get(i).setEntrustId(RepoInfoDto.getEtrInvestRepoInfoDtos().get(i).getEntrustId());
        }
        if (CollectionUtils.isNotEmpty(RepoInfoDto.getFileArray())) {
            List<UploadFile> uploadFiles = new ArrayList<>();
            for (SysUploadFileInfo file:  RepoInfoDto.getFileArray()) {
                uploadFiles.add(fileAttachmentProcess.fetch(file.getSeqNo()));
            }
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    uploadFiles.stream().map(UploadFile::getFileNo).collect(
                           Collectors.toList()),
                    RepoInfoDto.getTotalEtrustId());
        }
        ProcessParameter<EtribRepoInfo003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<EtribRepoInfo003Dto> params = new WorkFlowParameter<>();
        params.setPageId("EtribRepoInfo003");
        params.setElementId(RepoInfoDto.getEtrInvestRepoInfoDtos().get(0).getElementId());
        params.setBusinessCode(EtribRepoInfo003Dto::getTotalEtrustId);
        params.setBusinessType(DictCodeList.共通业务类型_回购投资委托.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(
                entity -> this.getMessage("EtribRepoInfo003") + "(" + entity.getTotalEtrustId() + ")"
                        + "-" + this.getMessage("EtribRepoInfo003.ps")+"("+ SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, entity.getPs())+")" + "-" + "("
                        + this.getMessage("EtribRepoInfo003.vdate") + ":" + DateUtil.format(entity.getVdate())
                        + ")-(" + this.getMessage("EtribRepoInfo003.mdate") + ":"
                        + DateUtil.format(entity.getMdate())+ ")-"
                        + this.getMessage("EtrRepo002.entrustAmt") + ":"
                        + entity.getEntrustAmt());
        processParameter.setWorkFlowParameter(params);

        String msg = etribRepoInfo003Service.start(processParameter, RepoInfoDto);
        return new AjaxResult(msg);
    }

}
