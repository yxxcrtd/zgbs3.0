/*
 * 文件名：FinExFundTarget002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.exfund.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.asset.exfund.dto.FinExFundTargetDto;
import com.joyintech.tams.invest.asset.exfund.service.FinExFundTargetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场内基金标的
 * 〈功能详细描述〉
 * @author meifaxin
 * @version 1.0 2021年7月8日
 * @see FinExFundTarget002Controller
 * @since 1.0
 */
@PageId("FinExFundTarget002")
@Controller
@RequestMapping("/invest/asset/exfund/FinExFundTarget002")
public class FinExFundTarget002Controller extends BaseController{
    @Autowired
    private FinExFundTargetService finExFundTargetService;
    /**
     * 各种共通组件
     */
    @Autowired(required=false)
    private CommonBusinessService commonBusinessService;
    /**
     * 
     * 新建页面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        String finprodId = getParam("finprodId");
      //设置货币基金的参数
        model.addAttribute("F01", DictCodeList.基金类资产类型_货币基金.getValue());
        /* add by zhangpuyu 2019/20/24 start */
        model.addAttribute("counterType",DictCodeListExt1.交易对手类型_分行.getValue());
        /* add by zhangpuyu 2019/20/24 end */
        model.addAttribute("F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        //增加快捷录入 add by zhangnian 2019/10/15 start
        String finprodMarketId=getParam("finprodMarketId");
        boolean isQuickInput = Boolean.parseBoolean(getParam("isQuickInput"));
        if (isQuickInput&&StringUtil.isNotEmpty(finprodMarketId)){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("finprodMarketId",finprodMarketId);
            FinExFundTargetDto dto = finExFundTargetService.findEntity(map);
            if(null!=dto){
                finprodId = dto.getFinprodId();
            }
        }
        //增加快捷录入 add by zhangnian 2019/10/15 end
        //是否存在与工作流中
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            FinExFundTargetDto dto = finExFundTargetService.getBussinessEntityByWorkflowId(
                workflowId);
//            model.addAttribute("pageModel", dto.getPageModel());
            model.addAttribute("businessId", workflowId);
            //commonBusinessService.setTradeMarketId(dto,model);
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(finprodId)) {
            //获得页面展示元素
            FinExFundTargetDto dto = finExFundTargetService.selectByFinprodId(finprodId);
            if(null != commonBusinessService && dto!= null){
                if (!isQuickInput){
                    if(commonBusinessService.isTransInAsset(dto.getFinprodId())>0){
                        dto.setIsTransInAsset(DictCodeList.是否_是.getValue());
                    }
                    else{
                        dto.setIsTransInAsset(DictCodeList.是否_否.getValue());
                    }
                }
            }
            // 快录时，不默认带出附件信息
            if(null != dto && !isQuickInput){
                model.addAttribute("businessId", dto.getFinprodId());
                //commonBusinessService.setTradeMarketId(dto,model);
            }
            
            //commonBusinessService.setTradeMarketId(dto,model);
            model.addAttribute("entity", dto);
        }
        else if(WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(finprodId)){
            FinExFundTargetDto dto = new FinExFundTargetDto();
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            /*dto.setPurSpeed(DictCodeList.清算速度_T_1.getValue());*/
            dto.setTradeMarket(DictCodeListExt1.交易场所_上海证券交易所.getValue());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            /**jira:TWO-61 add by chendijun start**/
            dto.setCapiIncomeFeature(DictCodeList.是否_否.getValue());
            //add by yanya 20190830 增加投资性质默认值
            dto.setInvestNature(DictCodeList.投资性质_固定收益类.getValue());
            /**jira:TWO-61 add by chendijun end**/
            //commonBusinessService.setTradeMarketId(dto,model);
            model.addAttribute("entity", dto);
        }
        this.setSelectOptionToModel(model, DictCode.基金类资产类型,DictCode.币种
            , DictCode.清算速度, DictCode.关联范围,DictCode.交易场所
            ,DictCode.分红方式,DictCode.行内资产三级分类,DictCode.行内资产四级分类,DictCode.行内资产五级分类
            ,DictCode.交易所基金类型,DictCode.分级基金,DictCode.ETF基金类型
            ,DictCode.基金类资产二级类型,DictCode.当日回转交易,DictCode.投资性质);
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("INTI_BONUS_MODE", DictCodeList.分红方式_红利再投); // 默认分红方式-红利再投 王贵奇 2019/7/29
        model.addAttribute("NO", DictCodeList.是否_否.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("TRM_FUND_TYPE_01",DictCodeListExt2.交易所基金类型_ETF.getValue());
        model.addAttribute("TRM_FUND_TYPE_02",DictCodeListExt2.交易所基金类型_LOF.getValue());
        model.addAttribute("TRM_FUND_TYPE_03",DictCodeListExt2.交易所基金类型_分级基金.getValue());
        model.addAttribute("TRM_FUND_TYPE_04",DictCodeListExt2.交易所基金类型_封闭式基金.getValue());
        model.addAttribute("TRM_FUND_TYPE_05",DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue());
        model.addAttribute("GRA_FUND_TYPE_M",DictCodeListExt2.分级基金_母基金.getValue());
        model.addAttribute("GRA_FUND_TYPE_A",DictCodeListExt2.分级基金_子基金A.getValue());
        model.addAttribute("GRA_FUND_TYPE_B",DictCodeListExt2.分级基金_子基金B.getValue());
        model.addAttribute("ETF_TYPE_01",DictCodeListExt2.ETF类型_单市场ETF.getValue());
        model.addAttribute("ETF_TYPE_02",DictCodeListExt2.ETF类型_跨市场ETF.getValue());
        model.addAttribute("ETF_TYPE_03",DictCodeListExt2.ETF类型_跨境ETF.getValue());
        model.addAttribute("ETF_TYPE_04",DictCodeListExt2.ETF类型_债券ETF.getValue());
        model.addAttribute("ETF_TYPE_05",DictCodeListExt2.ETF类型_黄金ETF.getValue());
        model.addAttribute("ETF_TYPE_06",DictCodeListExt2.ETF类型_货币ETF.getValue());
        model.addAttribute("TRADE_PLACE_03",DictCodeListExt1.交易场所_上海证券交易所.getValue());
        model.addAttribute("TRADE_PLACE_04",DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        model.addAttribute("TRANSACTION_CALENDAR_SH",DictCodeList.交易日历_上海交易所日历.getValue());
        model.addAttribute("TRANSACTION_CALENDAR_SZ",DictCodeList.交易日历_深圳交易所日历.getValue());
        if("01".equals(CacheUtil.getSysParam(BussinessConstant.CEMODEL))){
            model.addAttribute("creditFlag","N");
        }else{
            model.addAttribute("creditFlag","Y");
        }
        return super.init(model);
    }
    /**
     * 获取基金管理人下拉列表
     * <一句话功能简述>
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getManager")
    public Page<SelectOptionDto> getManager()
        throws Exception{
        Map<String,Object>map = RequestUtil.getPageParam();
        return finExFundTargetService.getManager(map);
    }
    /**
     * 新建时根据市场代码查询基金信息
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/findEntity")
    public FinExFundTargetDto findEntity(@RequestBody Map<String,Object> map){
        return finExFundTargetService.findEntity(map);
    }
    
    /**
     * 新建时根据市场代码查询wind接口基金信息
     * @param finprodMarketId 市场代码
     * @return
     * @throws ParseException 
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getInterfFundDto")
    public AjaxResult getInterfFundDto(@RequestParam("finprodMarketId") String finprodMarketId,
    		@RequestParam("tradeMarketId") String tradeMarketId) throws ParseException{
        
        AjaxResult ar = new AjaxResult();
        FinExFundTargetDto dto = finExFundTargetService.getInterfFundDto(finprodMarketId + tradeMarketId);
        if (StringUtil.isNotEmpty(dto)) {
        	dto.setFinprodMarketId(finprodMarketId);
            ar.setData(dto);
        }else {
            List<ValidMessage> errList = new ArrayList<ValidMessage>();
            errList.add(new ValidMessage(this.getMessage("COMMON_E_0008",this.getMessage("FinExFundTarget001.finprodMarketId"))));
            ar.setData(errList);
        }
        return ar;
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
        ValidateGroupEdit.class}) FinExFundTargetDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) FinExFundTargetDto dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<FinExFundTargetDto> processParameter = new ProcessParameter<FinExFundTargetDto>();
        WorkFlowParameter<FinExFundTargetDto> params = new WorkFlowParameter<FinExFundTargetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item -> item.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_基金类资产.getValue());
        //修改时为画面所选记录的金融产品代码。新建时调用各种业务共通组件.获取金融产品代码
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            if(commonBusinessService != null){
              //设置金融产品代码
                dto.setFinprodId(commonBusinessService.getFinProdId(dto.getFinprodMarketId(), dto.getFinprodType2(), null));
            }
        }
        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> SpringContextUtil.getTextValue("FinExFundTarget001.fundId") + "(" + entity.getFinprodMarketId() + ")"+ "-"
            + SpringContextUtil.getTextValue("FinExFundTarget001.finprodName") + "("
            + entity.getFinprodName() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = finExFundTargetService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    //modify by mashukai 20201117 BS-2006公募基金草稿箱提交时中债报送信息反显没有值问题
    @Override
    public Object after(String draftobj, Model model) {
        Map<String,Object> map =model.asMap();
        FinExFundTargetDto dto = (FinExFundTargetDto)map.get("entity");
        if(StringUtil.isNotEmpty(getParam(GlobalConstant.WORKFLOW_ID))){
            dto.setWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute("entity",dto);
        }
        return super.after(draftobj, model);
    }

}
