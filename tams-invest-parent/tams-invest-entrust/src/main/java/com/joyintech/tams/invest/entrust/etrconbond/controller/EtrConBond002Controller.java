package com.joyintech.tams.invest.entrust.etrconbond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrconbond.dto.EtrConBondDto;
import com.joyintech.tams.invest.entrust.etrconbond.service.EtrConBondService;
import com.joyintech.tams.invest.entrust.etrconbond.validator.EtrConBondDtoValidator;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Controller
@PageId("EtrConBond002")
@RequestMapping("/invest/entrust/etrconbond/EtrConBond002")
public class EtrConBond002Controller extends BaseController {
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 可转债委托service
     */
    @Autowired
    private EtrConBondService etrConBondService;

    @Autowired
    private EtrConBondDtoValidator etrConBondDtoValidator;

    @Autowired
    private FinProductMapper finProductMapper;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.WIND一级分类_含债券一级分类等, DictCode.币种,
                DictCode.交易场所, DictCode.交易市场, DictCode.交易方向, DictCode.投资目的,
                DictCode.申购方式, DictCode.申购类型);
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));
        String entrustId = getParam("entrustId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String entrustAmtUnit = CacheUtil.getSysParam("entrustAmtUnit");
        //流程代办
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {

            EtrConBondDto dto = etrConBondService.getBussinessEntityByWorkflowId(
                workflowId);
            //配置单位 edit by yeming 20210721 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getPurchBondTotal() != null) {
                    dto.setPurchBondTotal(dto.getPurchBondTotal().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 edit by yeming 20210721 end
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);
        
        }else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            //编辑页面
            EtrConBondDto dto = etrConBondService.selectByKey(entrustId);
            //配置单位 edit by yeming 20210721 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getPurchBondTotal() != null) {
                    dto.setPurchBondTotal(dto.getPurchBondTotal().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 edit by yeming 20210721 end
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",entrustId);
        }else{
            //新建页面初始化
            EtrConBondDto entity = new EtrConBondDto();
            //配置单位 edit by yeming 20210721 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(entity.getPurchBondTotal() != null) {
                    entity.setPurchBondTotal(entity.getPurchBondTotal().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 edit by yeming 20210721 end
            entity.setEntrustDate(sysDate);
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("etrPs", DictCodeList.交易方向_申购.getValue());
        model.addAttribute("INV_AIM_default", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        //配置单位 add by yeming 20210721 start
        model.addAttribute("entrustAmtUnit", CacheUtil.getSysParam("entrustAmtUnit"));
        model.addAttribute("PURCH_MODLE_default", DictCodeListExt2.申购方式_网下申购.getValue());
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
     * 获取新建界面的资产代码联想下拉框框
     * 
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodIdForAdd")
    public Page<SelectOptionDto> getFinprodIdForAdd(){
        return etrConBondService.getFinprodIdForAdd(RequestUtil.getPageParam());
    }
    /**
     * 
     * 组合可用现金查询
     * <功能详细描述>
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param){
        return etrConBondService.getProAvailableCash(param);
    }
    /**
     * 获取通道可用现金
     * @return
     */
    @ResponseBody
    @RequestMapping("/getChannelAvailableCash")
    public BigDecimal getChannelAvailableCash(@RequestBody Map<String, Object> param){
        BigDecimal bigDecimal = etrConBondService.getChannelAvailableCash(param);
        if(bigDecimal != null){
            return bigDecimal;
        }
        return BigDecimal.ZERO;
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
            ValidateGroupEdit.class}) EtrConBondDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        //可转债网下申购 组合下没有托管户资金账户异常提醒 ZGTFCP-2299
        if(StringUtil.equals(dto.getOperateType(),"save") || StringUtil.equals(dto.getOperateType(),"submit")){
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
            if(null != finProduct && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finProduct.getFinprodType2()) && DictCodeList.交易方向_申购.getValue().equals(dto.getPs())
             && DictCodeListExt2.申购方式_线下申购.getValue().equals(dto.getPurchModle())){
                String accNo = EtrCommonCashPositionDealService.select组合托管户(dto.getPortfolioId(), dto.getCcy(), DictCodeListExt2.资金账户类型_托管户.getValue());
                if(null == accNo){
                    return new AjaxResult("用户组合下没有绑定托管户资金账户", MessageType.错误消息);
                }
            }
        }
        if ((StringUtil.equals(dto.getOperateType(),"save") || StringUtil.equals(dto.getOperateType(),"submit"))
                && (result==null?null:result.getErrorCount()) == 0) {
            AjaxResult ajaxResult = etrConBondDtoValidator.checkConfirmInfo(dto);
            if (null != ajaxResult){
                return ajaxResult;
            }
        }
        return commonCheck(result, dto.getOperateType());
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody EtrConBondDto dto) {
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
    public AjaxResult save(@RequestBody EtrConBondDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_可转债申购委托));
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
        }
        // ----------add by yushilong 20200721 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_债券.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by yushilong 20200721 预占功能 end----------
        ProcessParameter<EtrConBondDto> processParameter = new ProcessParameter<EtrConBondDto>();
        WorkFlowParameter<EtrConBondDto> params = new WorkFlowParameter<EtrConBondDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_可转债申购委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrConBond001") + "(" + entity.getPortfolioId() + ")"
                      + ")-(" +  this.getMessage("EtrConBond001.purchDate") + ":" + DateUtil.format(entity.getEntrustDate())
                      + this.getMessage("EtrBondFirstLBidding002.finprodId") + ":"
                      + entity.getFinprodMarketId()
                      );
        processParameter.setWorkFlowParameter(params);
        String msg = etrConBondService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
