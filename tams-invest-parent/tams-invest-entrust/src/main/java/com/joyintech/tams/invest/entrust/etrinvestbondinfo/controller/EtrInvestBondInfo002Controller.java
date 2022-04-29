/*
 * 文件名：EtrInvestBondInfo002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2018年9月10日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrinvestbondinfo.controller;

import java.math.BigDecimal;
import java.util.*;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.controller.EtrCommonController;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.dto.EtrInvestBondInfoDto;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.service.EtrInvestBondInfoService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 债券投资委托新建 〈功能详细描述〉
 * 
 * @author xiexinting
 * @version 1.0 2018年9月10日
 * @see EtrInvestBondInfo002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrInvestBondInfo002")
@RequestMapping("/invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002")
public class EtrInvestBondInfo002Controller extends BaseController {
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrInvestBondInfoService etrInvestBondInfoService;
    /**
     * 初始化002新建页面
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.WIND一级分类_含债券一级分类等);
        //新增资产二、三、四级分类 王贵奇 2019/7/27 开始
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产三级分类);
        this.setSelectOptionToModel(model, DictCode.行内资产四级分类);
        //新增资产二、三、四级分类 王贵奇 2019/7/27 结束
        this.setSelectOptionToModel(model, DictCode.币种);
        this.setSelectOptionToModel(model, DictCode.交易场所);
        this.setSelectOptionToModel(model, DictCode.交易市场);
        this.setSelectOptionToModel(model, DictCode.交易方向);
        this.setSelectOptionToModel(model, DictCode.清算速度);
        this.setSelectOptionToModel(model, DictCode.投资目的);
        this.setSelectOptionToModel(model, DictCode.交易平台);
        this.setSelectOptionToModel(model, DictCode.价格模式);
        this.setSelectOptionToModel(model, DictCode.业务模式);
        model.addAttribute("PRICEMODEL_03", DictCodeListExt2.价格模式_市价指令.getValue());
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate",DateUtil.format(sysDate));
        model.addAttribute("bankRoom", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("bankPlat", DictCodeList.交易平台_外汇交易中心.getValue());
        model.addAttribute("shse",DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("szse", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("oneMarket", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("marketLv2", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        model.addAttribute("TRADE_PLACE_02", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_01", DictCodeList.交易平台_外汇交易中心.getValue());
        model.addAttribute("SHARES_TRADE_PLATFORM_99", DictCodeList.交易平台_其它.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_01", DictCodeListExt1.交易市场_一级市场.getValue());
        model.addAttribute("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        model.addAttribute("T01", DictCodeList.交易方向_买入.getValue());
        model.addAttribute("T02", DictCodeList.交易方向_卖出.getValue());
        // 一体化对应etliu start
//        model.addAttribute("T03",DictCodeList.交易方向_行权.getValue());
        model.addAttribute("T03",DictCodeList.交易方向_行权_回售.getValue());
        model.addAttribute("T39",DictCodeList.交易方向_行权_赎回.getValue());
        // 一体化对应etliu end
        model.addAttribute("type01", DictCodeList.收益类型_净值型.getValue());
        model.addAttribute("type03", DictCodeList.收益类型_货币型.getValue());
        model.addAttribute("type02", DictCodeList.收益类型_收益型.getValue());
        model.addAttribute("aim01", DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue());
        model.addAttribute("aim02", DictCodeListExt1.投资目的_摊余成本类.getValue());
        model.addAttribute("N", DictCodeList.是否_否.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));        String entrustId = getParam("entrustId");
        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数

        // add by etliu 20210501 银行间场内场外拆分 start
        model.addAttribute("parentPageId", this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易平台, this.getParam("parentPageId"));
        // add by etliu 20210501 银行间场内场外拆分 end

        String finprodId = getParam("finprodId");
        String portfolioId = getParam("portfolioId");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        //配置单位 add by yeming 20210721 start
        String entrustAmtUnit = CacheUtil.getSysParam("entrustAmtUnit");
        //配置单位 add by yeming 20210721 end
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            EtrInvestBondInfoDto dto = etrInvestBondInfoService.getBussinessEntityByWorkflowId(
                workflowId); 
            dto.setRiskTrialRetDto(null);
            //单位以万元展示
            //配置单位 edit by yeming 20210721 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getEntrustNum() != null) {
                    dto.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(10000)));
                }
                if(dto.getMinAmt() != null) {
                    dto.setMinAmt(dto.getMinAmt().divide(new BigDecimal(10000)));
                }
                if(dto.getEntrustOwnNum() != null) {
                    dto.setEntrustOwnNum(dto.getEntrustOwnNum().divide(new BigDecimal(10000)));
                }
                if(dto.getEntrustIntegrateNum() != null) {
                    dto.setEntrustIntegrateNum(dto.getEntrustIntegrateNum().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 edit by yeming 20210721 end
            // add by yeming 20210913 银行间场内场外拆分 start
            model.addAttribute("parentPageId", dto.getParentPageId());
            PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, dto.getParentPageId());
            PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易平台, dto.getParentPageId());
            // add by yeming 20210913 银行间场内场外拆分 end

            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            EtrInvestBondInfoDto dto = etrInvestBondInfoService.selectByKey(entrustId,finprodId,portfolioId);
            /*dto.setCalflag("price");
            etrInvestBondInfoService.calcAmt(dto);*/
            //单位以万元展示
            //配置单位 edit by yeming 20210721 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getEntrustNum() != null) {
                    dto.setEntrustNum(dto.getEntrustNum().divide(new BigDecimal(10000)));
                }
                if(dto.getMinAmt() != null) {
                    dto.setMinAmt(dto.getMinAmt().divide(new BigDecimal(10000)));
                }
                if(dto.getEntrustOwnNum() != null) {
                    dto.setEntrustOwnNum(dto.getEntrustOwnNum().divide(new BigDecimal(10000)));
                }
                if(dto.getEntrustIntegrateNum() != null) {
                    dto.setEntrustIntegrateNum(dto.getEntrustIntegrateNum().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 edit by yeming 20210721 end
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",entrustId);
        }
        //新建初始化
        else if (WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(entrustId)) {
            EtrInvestBondInfoDto entity = new EtrInvestBondInfoDto();
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            entity.setEntrustStatus(DictCodeList.委托状态_新建.getValue());
            //配置单位 edit by yeming 20210721 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(entity.getEntrustNum() != null) {
                    entity.setEntrustNum(entity.getEntrustNum().divide(new BigDecimal(10000)));
                }
                if(entity.getMinAmt() != null) {
                    entity.setMinAmt(entity.getMinAmt().divide(new BigDecimal(10000)));
                }
                if(entity.getEntrustOwnNum() != null) {
                    entity.setEntrustOwnNum(entity.getEntrustOwnNum().divide(new BigDecimal(10000)));
                }
                if(entity.getEntrustIntegrateNum() != null) {
                    entity.setEntrustIntegrateNum(entity.getEntrustIntegrateNum().divide(new BigDecimal(10000)));
                }
            }
            //配置单位 edit by yeming 20210721 end
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_交易监控.getValue());
        model.addAttribute("fintype", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("shortSellingSupported", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        //配置单位 add by yeming 20210721 start
        model.addAttribute("entrustAmtUnit", entrustAmtUnit);
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
     * 获取组合代码联想文本框信息 <功能详细描述>
     * 
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getentrustIdAdd")
    public Page<SelectOptionDto> getentrustIdAdd(){
        Page<SelectOptionDto> temp = etrInvestBondInfoService.getentrustIdAdd(RequestUtil.getPageParam());
        return temp;
    }

    /**
     * 获取新建界面的资产代码联想下拉框框
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getfinprodIdForAdd")
    public Page<SelectOptionDto> getfinprodIdForAdd() {
        Page<SelectOptionDto> temp = etrInvestBondInfoService.getfinprodIdForAdd(RequestUtil.getPageParam());
        return temp;
    }
    /**
     * 
     * 可用面值计算
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAvailableNum")
    public BigDecimal getAvailableNum(String finprodId){
        return etrInvestBondInfoService.getAvailableNum(finprodId);
    }
    /**
     * 
     * 组合可用现金查询
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getProAvailableCash")
    public Map<String, Object> getProAvailableCash(@RequestBody Map<String, Object> param){
        return etrInvestBondInfoService.getProAvailableCash(param);
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
        return etrInvestBondInfoService.getAssetAvailablePosition(param);
    }
    /**
     * 
     * 净价、行权收益率、到期收益率
     * <功能详细描述>
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/calData")
    @ResponseBody
    public AjaxResult calData(@RequestBody EtrInvestBondInfoDto dto){
        AjaxResult result = new AjaxResult();
        etrInvestBondInfoService.calcAmt(dto);
        result.setData(dto);
        return result;
    }

    /**
     * 编辑前，check是否可编辑
     * @param dto
     * @return
     */
    @RequestMapping("/checkEdit")
    @ResponseBody
    public AjaxResult checkEdit(@RequestBody EtrInvestBondInfoDto dto){
        AjaxResult result = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        dto.setOperateType(DictCodeListExt1.委托操作类型_修改.getValue());
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                && !StringUtil.isNotEmpty(dto.getPortfolioId())) {
            errorList.add(new ValidMessage("finprodId", this.getMessage("ETR_BOND_001")));
        }
        errorList.addAll(etrInvestBondInfoService.check(dto));
        if (!StringUtil.isEmptyList(errorList)){
            result.setErrorList(errorList);
        }
        return result;
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
        ValidateGroupEdit.class}) EtrInvestBondInfoDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        if(StringUtil.equals(dto.getOperateType(), "save") || StringUtil.equals(dto.getOperateType(), "submit")){
            List<ValidMessage> errorList = new ArrayList<>();
            if(StringUtil.equals("01",CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"))){
            if (dto.getEntrustIntegrateNum() == null ){
                errorList.add(new ValidMessage("融入券/卖空购回券面总额不能为空"));
            }
            if (dto.getEntrustOwnNum() == null){
                errorList.add(new ValidMessage("本方券券面总额不能为空"));
            }
            }
            if (errorList.size() > 0){
                AjaxResult ajaxResult = new AjaxResult();
                ajaxResult.setErrorList(errorList);
                return ajaxResult;
            }
        }
        // 行权方式check add by renping 20191020 start
        String confirmMsg = "";
        String changeLineMark = "<br/>";
        // 可用现金软校验
        if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
            DictCodeListExt1.是或否_是.getValue())
            && StringUtil.equals(dto.getPs(), DictCodeList.交易方向_买入.getValue())
            && dto.getProAvailableCash() != null) {
            if (dto.getEntrustNum().compareTo(dto.getProAvailableCash()) == 1) {
                confirmMsg = this.getMessage("TRD_C_0002");
            }
        }
        // 可用持仓软校验
        else if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH),
            DictCodeListExt1.是或否_是.getValue())
                 && (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
                     || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())
                     || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue()))
                 && dto.getAssetAvailablePosition() != null) {
            if (dto.getEntrustNum().compareTo(dto.getAssetAvailablePosition()) > 0) {
                confirmMsg = this.getMessage("ETR_BOND_002");
            }
        }
        if (StringUtil.isNotBlank(confirmMsg)) {
            AjaxResult ajaxResult = new AjaxResult();
            ajaxResult.setData(confirmMsg);
            return ajaxResult;
        }
        // 行权方式check add by renping 20191020 end
        
        //授权管理  isAuthorizationSoftcontrol true:软控  false:硬控
//        boolean isAuthorizationSoftcontrol = Boolean.parseBoolean(CacheUtil.getSysParam("IS_AUTHORIZATION_SOFTCONTROL"));
//        if (isAuthorizationSoftcontrol){
//            EtrCommonController etrCommonController = SpringContextUtil.getBean(
//                EtrCommonController.class);
//            return etrCommonController.授权是否匹配(dto);
//        }
        
        return commonCheck(result, dto.getOperateType());
    }
    
    @RequestMapping("checkAuthorization")
    @ResponseBody
    public AjaxResult checkAuthorization(@RequestBody EtrInvestBondInfoDto dto) {
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
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody EtrInvestBondInfoDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
        }
        // ----------add by zhangrui1 20200703 预占功能 start----------
        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
                    DictCodeList.平安资产类型_债券.getValue());
            if (StringUtil.isNotEmpty(ar)) {
                return ar;
            }
        }
        // ----------add by zhangrui1 20200703 预占功能 end----------
        ProcessParameter<EtrInvestBondInfoDto> processParameter = new ProcessParameter<EtrInvestBondInfoDto>();
        WorkFlowParameter<EtrInvestBondInfoDto> params = new WorkFlowParameter<EtrInvestBondInfoDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(DictCodeList.共通业务类型_债券投资委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrInvestBondInfo001") + "(" + entity.getPortfolioId() + ")"
                      + "-" + this.getMessage("EtrInvestBondInfo001.ps")+"("+SysDataUtil.getNameFromDataDict(DictCode.交易方向, entity.getPs())+")" + "-" + "("
                      + this.getMessage("EtrInvestBondInfo001.vdate") + ":" + DateUtil.format(entity.getVdate())
                      + ")-(" + this.getMessage("EtrInvestBondInfo001.mdate") + ":"
                      + DateUtil.format(entity.getMdate())+ ")-"
                      + this.getMessage("EtrInvestBondInfo001.entrustAmt") + ":"
                      + entity.getEntrustNum());
        processParameter.setWorkFlowParameter(params);
        String msg = etrInvestBondInfoService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
    /**
     * 查询估值净价
     * @author wangguiqi
     */
    @ResponseBody
    @RequestMapping("getClosePriceByPara")
    public BigDecimal getClosePriceByPara(@RequestBody Map<String, Object> param){
        return etrInvestBondInfoService.getClosePriceByPara(param);
    }

    /**
     * 查询上一日估值净价
     *
     * @author jiangpeng
     */
    @ResponseBody
    @RequestMapping("getLastPriceByPara")
    public AjaxResult getLastPriceByPara(@RequestBody Map<String, Object> param){
        AjaxResult result = new AjaxResult();
        result.setData(etrInvestBondInfoService.getLastPriceByPara(param));
        return result;
    }
}
