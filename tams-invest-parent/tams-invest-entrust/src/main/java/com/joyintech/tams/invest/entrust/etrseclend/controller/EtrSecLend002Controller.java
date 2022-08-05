/*
 * 文件名：EtrSecLend002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrseclend.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.MessageException;
import com.joyintech.framework.exception.RestTemplateException;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrseclend.dto.EtrSecLendDto;
import com.joyintech.tams.invest.entrust.etrseclend.service.EtrSecLendService;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 债券投资委托新建 〈功能详细描述〉
 * 
 * @author wangzhuangzhuang
 * @version 1.0 2019年7月5日
 * @see EtrSecLend002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrSecLend002")
@RequestMapping("/invest/entrust/etrseclend/EtrSecLend002")
public class EtrSecLend002Controller extends BaseController {
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrSecLendService etrSecLendService;
    /**
     * 初始化002新建页面
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.交易市场);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.交易平台);
        this.setSelectOptionToModel(model, DictCode.期限品种_债券借贷);
        this.setSelectOptionToModel(model, DictCode.回购拆借方向);
        
        model.addAttribute("BACK_LEND_DIRECTION_INTO", DictCodeList.回购拆借方向_融入.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_OUT", DictCodeList.回购拆借方向_融出.getValue());

        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        String entrustId = getParam("entrustId");
        String finprodId = getParam("targetFinprodId");
        String portfolioId = getParam("portfolioId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String entrustAmtUnit = CacheUtil.getSysParam("entrustAmtUnit");
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrSecLendDto dto = etrSecLendService.getBussinessEntityByWorkflowId(
                workflowId);
            //配置单位 add by yeming 20210723 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getTotalAmt() != null) {
                    dto.setTotalAmt(dto.getTotalAmt().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 add by yeming 20210723 end
            if(dto.getSettleSpeed()!=null){
                if(dto.getSettleSpeed().equals("0")){
                    dto.setSettleSpeed(DictCodeList.清算速度_T_0.getValue());
                }else if(dto.getSettleSpeed().equals("1")){
                    dto.setSettleSpeed(DictCodeList.清算速度_T_1.getValue());
                }
            }
            if(dto.getBusiRate() != null){
                dto.setBusiRate(dto.getBusiRate().multiply(new BigDecimal(100)));  
            }
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            if(StringUtil.isNotEmpty(dto.getBussRepoSec())&&!StringUtil.isEmptyList(dto.getBussRepoSec().getPledgeBondList())){
                if( dto.getBussRepoSec().getTotalPrinAmt()  != null){
                    dto.getBussRepoSec().setTotalPrinAmt(ArithmeticUtil.multiply( dto.getBussRepoSec().getTotalPrinAmt(),new BigDecimal(10000)));
                }
                for (PledgeBondInfoDto pledgeBond :  dto.getBussRepoSec().getPledgeBondList()) {
                    if (pledgeBond.getAvaPledgePrin() != null) {
                        pledgeBond.setAvaPledgePrin(ArithmeticUtil.multiply(pledgeBond.getAvaPledgePrin(), new BigDecimal(10000)));
                    }
                    if (pledgeBond.getPledgePrin() != null) {
                        pledgeBond.setPledgePrin(ArithmeticUtil.multiply(pledgeBond.getPledgePrin(), new BigDecimal(10000)));
                    }
                }
                model.addAttribute("entity_bussRepoSecDto", JsonUtils.object2Json(dto.getBussRepoSec()));
            }

            model.addAttribute("businessId",workflowId);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            EtrSecLendDto dto = etrSecLendService.selectByKey(entrustId,finprodId,portfolioId);
            //配置单位 add by yeming 20210723 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getTotalAmt() != null) {
                    dto.setTotalAmt(dto.getTotalAmt().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 add by yeming 20210723 end
            if(dto.getSettleSpeed()!=null){
                if(dto.getSettleSpeed().equals("0")){
                    dto.setSettleSpeed(DictCodeList.清算速度_T_0.getValue());
                }else if(dto.getSettleSpeed().equals("1")){
                    dto.setSettleSpeed(DictCodeList.清算速度_T_1.getValue());
                }
            }
            
            if(dto.getBusiRate() != null){
                dto.setBusiRate(dto.getBusiRate().multiply(new BigDecimal(100)));  
            }
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",entrustId);
        }
        //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrSecLendDto entity = new EtrSecLendDto();
            //配置单位 add by yeming 20210723 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(entity.getTotalAmt() != null) {
                    entity.setTotalAmt(entity.getTotalAmt().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 add by yeming 20210723 end
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        //----add by jjpei 20190718--------委托监控风险试算-----------start--------
        model.addAttribute("FINPROD_TYPE_TG_F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
        model.addAttribute("MY_SUP_TIME_TYPE_01", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("USE_STATUS_01", DictCodeListExt1.使用状态_启用.getValue());
        //----add by jjpei 20190718--------委托监控风险试算-----------start--------
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("PINAN_ASSET_TYPE_F12",DictCodeListExt2.平安资产类型_债券借贷.getValue());
        //配置单位 add by yeming 20210721 start
        model.addAttribute("entrustAmtUnit", CacheUtil.getSysParam("entrustAmtUnit"));
        //配置单位 add by yeming 20210721 end
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
     * 查询证券托管户：联想框文本框
     *
     * @param params 画面参数
     * @return 证券托管户：联想框文本框
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecManageAcctId")
    @ResponseBody
    public List<SelectOptionDto> getSecManageAcctId(@RequestParam Map<String, Object> params) {
        return etrSecLendService.getSecManageAcctId(params);
    }
    /**
     * 
     * 组合可用持仓查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetAvailablePosition")
    public Map<String, Object> getAssetAvailablePosition(@RequestBody Map<String, Object> param){
        return etrSecLendService.getAssetAvailablePosition(param);
    }
    /**
     * 资产代码：下拉框
     *
     * @param params 画面参数
     * @return 资产代码：下拉框
     * @see
     * @since 1.0
     */
    @RequestMapping("getFinprodId")
    @ResponseBody
    public Page<SelectOptionDto> getFinprodId(@RequestParam Map<String, Object> params) {
        return etrSecLendService.getFinprodId(params);
    }
    /**
     * 保存按钮点击时的check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) EtrSecLendDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        return commonCheck(result, dto.getOperateType());
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody EtrSecLendDto dto) {
        //授权管理  委托授权额度check
        if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETRLIMIT_SWITCH),"01")){
            EtrCommonController etrCommonController = SpringContextUtil.getBean(
                EtrCommonController.class);
            return etrCommonController.授权是否匹配(dto);
        }
        return new AjaxResult();
    }
    
    /**
     * 保存/提交
     * 
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrSecLendDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
//            return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
            return new AjaxResult(br);
        }
        dto.setDecisionAct(DictCodeListExt3.系统动作_修改.getValue());
        dto.setSystemAct(DictCodeListExt3.系统动作_修改.getValue());
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_债券借贷投资委托));
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
            dto.setDecisionAct(DictCodeListExt3.系统动作_新建.getValue());
            dto.setSystemAct(DictCodeListExt3.系统动作_新建.getValue());
        }
        // GLCP-34:债券借贷指令不需要预占 modify by fuyongsehng 20220707 start
//        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
//            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
//                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
//                    DictCodeListExt2.平安资产类型_债券借贷.getValue());
//            if (StringUtil.isNotEmpty(ar)) {
//                return ar;
//            }
//        }
        // GLCP-34:债券借贷指令不需要预占 modify by fuyongsehng 20220707 end
        ProcessParameter<EtrSecLendDto> processParameter = new ProcessParameter<EtrSecLendDto>();
        WorkFlowParameter<EtrSecLendDto> params = new WorkFlowParameter<EtrSecLendDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_债券借贷投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrSecLend.add") + "(" + entity.getPortfolioId() + ")"
                      + "-" + this.getMessage("EtrSecLend.busiPs")+"("+SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, entity.getBusiPs())+")" + "-" + "("
                      + this.getMessage("EtrSecLend.vdate") + ":" + DateUtil.format(entity.getVdate())
                      + ")-(" + this.getMessage("EtrSecLend.mdate") + ":"
                      + DateUtil.format(entity.getMdate())+ ")-"
                      + this.getMessage("EtrSecLend.totalAmt") + ":"
                      + entity.getTotalAmt());
        processParameter.setWorkFlowParameter(params);
        String msg = etrSecLendService.start(processParameter, dto);
        return new AjaxResult(msg);
//        String msg ="";
//        try {
//            msg = etrSecLendService.start(processParameter, dto);
//            AjaxResult m =new AjaxResult(msg);
//            return new AjaxResult(msg);
//        }catch (MessageException e){
//            AjaxResult m =new AjaxResult(e.getMessage(),e.getMessageType());
//            return m ;
////            return etrSecLendService.ajaxErrorReturn(e);
////            //throw new MessageException(e.getMessage(),MessageType.错误消息);
//        }
    }


}
