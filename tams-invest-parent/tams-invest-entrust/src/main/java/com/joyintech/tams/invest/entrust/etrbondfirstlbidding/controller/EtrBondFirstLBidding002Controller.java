package com.joyintech.tams.invest.entrust.etrbondfirstlbidding.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.invest.common.pagemenusplit.PageMenuSplitUtil;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.dto.EtrBondFirstLBiddingDto;
import com.joyintech.tams.invest.entrust.etrbondfirstlbidding.service.EtrBondFirstBiddingService;
import com.joyintech.tams.systemtools.dto.PageElementConfigDto;
import com.joyintech.tams.systemtools.util.PageElementUtil;
import com.joyintech.workflow.engine.constant.OperationType;
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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@PageId("EtrBondFirstLBidding002")
@RequestMapping("/invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding002")
public class EtrBondFirstLBidding002Controller extends BaseController {
    
    /**
     * 业务编号组件
     */
    @Autowired(required=false)
    private BaseGetBusinessId bussGetBussinessIdService;
    /**
     * 债券一级委托service
     */
    @Autowired
    private EtrBondFirstBiddingService firstService;
    
    
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.WIND一级分类_含债券一级分类等);
        //新增资产二、三、四级分类 王贵奇 2019/7/27 开始
        this.setSelectOptionToModel(model, DictCode.行内资产二级分类 );
        this.setSelectOptionToModel(model, DictCode.行内资产三级分类 );
        this.setSelectOptionToModel(model, DictCode.行内资产四级分类 );
        //新增资产二、三、四级分类 王贵奇 2019/7/27 结束
        this.setSelectOptionToModel(model, BussinessDictCode.缴款方式);
        this.setSelectOptionToModel(model, BussinessDictCode.币种);
        this.setSelectOptionToModel(model, BussinessDictCode.交易场所);
        this.setSelectOptionToModel(model, BussinessDictCode.交易市场);
        this.setSelectOptionToModel(model, BussinessDictCode.交易方向);
        this.setSelectOptionToModel(model, BussinessDictCode.清算速度);
        this.setSelectOptionToModel(model, BussinessDictCode.投资目的);
        this.setSelectOptionToModel(model, BussinessDictCode.交易平台);
        this.setSelectOptionToModel(model, DictCode.业务模式);

        // add by etliu 20210501 银行间场内场外拆分 start
        model.addAttribute("parentPageId", this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, this.getParam("parentPageId"));
        PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易平台, this.getParam("parentPageId"));
        // add by etliu 20210501 银行间场内场外拆分 end
        Date sysDate = DateUtil.platDateFromCache();
        model.addAttribute("sysDate", DateUtil.format(sysDate));
        String entrustId = getParam("entrustId");
        String finprodId = getParam("finprodId");
        String portfolioId = getParam("portfolioId");
        // by zhaodongdong 20191116 start 
        String mId = getParam("mId");
        String tradeMarket = getParam("tradeMarket");
        // by zhaodongdong 20191116 end
        //配置单位 add by yeming 20210721 start
        String entrustAmtUnit = CacheUtil.getSysParam("entrustAmtUnit");
        //配置单位 add by yeming 20210721 end

        //展示投资目的或业务模式参数
//        String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
//        model.addAttribute("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);
        //展示投资目的或业务模式参数

        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        model.addAttribute("TodoList", !WorkFlowUtil.isNotFromToDoList());
        //流程代办
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {

            EtrBondFirstLBiddingDto dto = firstService.getBussinessEntityByWorkflowId(
                workflowId);
            //配置单位 edit by yeming 20210730 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getBidPriceList() != null){
                    for(int i = 0 ; i < dto.getBidPriceList().size() ; i++){
                        EtrEntrustBidPrice etrEntrustBidPrice = dto.getBidPriceList().get(i);
                        if(etrEntrustBidPrice.getBidPrinAmt() != null){
                            etrEntrustBidPrice.setBidPrinAmt(etrEntrustBidPrice.getBidPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
                        }
                    }
                }
            }
            //配置单位 edit by yeming 20210730 end
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("bidPriceList", StringUtil.isEmptyList(dto.getBidPriceList()) ? null : JsonUtils.object2Json(dto.getBidPriceList()));
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",workflowId);

            // add by yeming 20210914 银行间场内场外拆分 start
            model.addAttribute("parentPageId", dto.getParentPageId());
            PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易场所, dto.getParentPageId());
            PageMenuSplitUtil.handlePageSelectDictList(model, DictCode.交易平台, dto.getParentPageId());
            // add by yeming 20210914 银行间场内场外拆分 end
        }else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(entrustId)) {
            //编辑页面
            EtrBondFirstLBiddingDto dto = firstService.selectByKey(entrustId,finprodId,portfolioId);
            //配置单位 edit by yeming 20210730 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(dto.getBidPriceList() != null){
                    for(int i = 0 ; i < dto.getBidPriceList().size() ; i++){
                        EtrEntrustBidPrice etrEntrustBidPrice = dto.getBidPriceList().get(i);
                        if(etrEntrustBidPrice.getBidPrinAmt() != null){
                            etrEntrustBidPrice.setBidPrinAmt(etrEntrustBidPrice.getBidPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
                        }
                    }
                }
            }
            //配置单位 edit by yeming 20210730 end
            model.addAttribute(GlobalConstant.ENTITY, dto);
            model.addAttribute("tradeId", dto.getEntrustId());
            model.addAttribute("bidPriceList", StringUtil.isEmptyList(dto.getBidPriceList()) ? null : JsonUtils.object2Json(dto.getBidPriceList()));
            model.addAttribute("person_entity", JsonUtils.object2Json(dto.getEtrInvestPersonInfo()));
            model.addAttribute("businessId",entrustId);
        }else{
            //新建页面初始化
            EtrBondFirstLBiddingDto entity = new EtrBondFirstLBiddingDto();
            //配置单位 edit by yeming 20210730 start
            if(StringUtil.equals(entrustAmtUnit,"02")){
                if(entity.getBidPriceList() != null){
                    for(int i = 0 ; i < entity.getBidPriceList().size() ; i++){
                        EtrEntrustBidPrice etrEntrustBidPrice = entity.getBidPriceList().get(i);
                        if(etrEntrustBidPrice.getBidPrinAmt() != null){
                            etrEntrustBidPrice.setBidPrinAmt(etrEntrustBidPrice.getBidPrinAmt().divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP));//转成万元
                        }
                    }
                }
            }
            //配置单位 edit by yeming 20210730 end
            entity.setVdate(sysDate);
            entity.setMdate(sysDate);
            // 从一级新债日历进入一级债券委托界面带出信息 by 赵东东  20191116 start
            entity.setmId(mId);
            if(StringUtil.isNotEmpty(mId)) {
            entity.setFinprodMarketId(finprodId);
            entity.setTradeMarket(tradeMarket);
            }
            // 从一级新债日历进入一级债券委托界面带出信息 by 赵东东  20191116 end
            entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
            model.addAttribute(GlobalConstant.ENTITY, entity);
        }
        model.addAttribute("MY_SUP_TIME_TYPE_02", BussinessDictCodeList.监督时分_委托监控.getValue());
        model.addAttribute("fintype", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        model.addAttribute("etrPs", BussinessDictCodeList.交易方向_买入.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        PageElementConfigDto pageElementConfigDto = new PageElementConfigDto();
        pageElementConfigDto.setPageId("BaseRiskMathComm");
        Map<String, String> map = PageElementUtil.isPageElementShow(pageElementConfigDto); // 风险试算页面
        pageElementConfigDto.setPageId(this.getPageId());
        Map<String, String> isEleShow = PageElementUtil.isPageElementShow(pageElementConfigDto);
        isEleShow.putAll(map);
        model.addAttribute("isEleShow", isEleShow);

        //配置单位 add by yeming 20210721 start
        model.addAttribute("entrustAmtUnit", entrustAmtUnit);
        //配置单位 add by yeming 20210721 end

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
        return firstService.getentrustIdAdd(RequestUtil.getPageParam());
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
    @RequestMapping("/getfinprodIdForAdd")
    public Page<SelectOptionDto> getfinprodIdForAdd() {
        return firstService.getfinprodIdForAdd(RequestUtil.getPageParam());
    }
    /**
     * 获取新建界面的主承销商下拉框
     *
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getMainConsignee")
    public Page<SelectOptionDto> getMainConsignee(){
        return firstService.getMainConsignee(RequestUtil.getPageParam());
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
        return firstService.getProAvailableCash(param);
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
        ValidateGroupEdit.class}) EtrBondFirstLBiddingDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new AjaxResult(result);
        }
        return commonCheck(result, dto.getOperateType());
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
    public AjaxResult save(@RequestBody EtrBondFirstLBiddingDto dto, BindingResult br) throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(!StringUtil.isNotEmpty(dto.getEntrustId())){
            BussinessCodeParameter param = new BussinessCodeParameter();
            param.setCommonBusinessType(DictCodeList.共同业务类型_债券一级投标委托);
            String code = bussGetBussinessIdService !=null ? bussGetBussinessIdService.buildBussinessCode(param):IDGenerator.generate();
            dto.setEntrustId(code);
        }
//        // ----------add by yushilong 20200721 预占功能 start----------
//        if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
//            AjaxResult ar = EntrustRdsCommonAop.doPreOccupy(dto,
//                    BussinessDictCodeListRds.接口系统ID_预占接口.getValue(),
//                    DictCodeListExt2.平安资产类型_一级市场债.getValue());
//            if (StringUtil.isNotEmpty(ar)) {
//                return ar;
//            }
//        }
//        // ----------add by yushilong 20200721 预占功能 end----------
        ProcessParameter<EtrBondFirstLBiddingDto> processParameter = new ProcessParameter<EtrBondFirstLBiddingDto>();
        WorkFlowParameter<EtrBondFirstLBiddingDto> params = new WorkFlowParameter<EtrBondFirstLBiddingDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getEntrustId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_债券一级投标委托.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> this.getMessage("EtrBondFirstLBidding001") + "(" + entity.getPortfolioId() + ")"
                      + "-" +  this.getMessage("EtrInvestAllInfo001.vdate") + ":" + DateUtil.format(entity.getVdate())
                      + ")-(" +  this.getMessage("EtrInvestAllInfo001.entrustDate") + ":" + DateUtil.format(entity.getEntrustDate())
                      + ")-(" + this.getMessage("EtrInvestAllInfo001.mdate") + ":"
                      + DateUtil.format(entity.getMdate())+ ")-"
                      + this.getMessage("EtrBondFirstLBidding002.finprodId") + ":"
                      + entity.getFinprodMarketId()
                    /*  + ")-(" + this.getMessage("EtrBondFirstLBidding002.bidMarkPosition") + ":"
                      + DateUtil.format(entity.getMdate())+ ")"*/
                      );
        processParameter.setWorkFlowParameter(params);
        String msg = firstService.start(processParameter, dto);
        return new AjaxResult(msg);
//        return new AjaxResult(getMessage("ETR_X_016"));
    }
}
