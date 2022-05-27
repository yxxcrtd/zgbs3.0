/*
 * 文件名：FinFundTarget002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.fund.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
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
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.invest.asset.fund.dto.FinFundTargetDto;
import com.joyintech.tams.invest.asset.fund.service.FinFundTargetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 平安基金类标的信息新建
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年9月11日
 * @see FinFundTarget002Controller
 * @since 1.0
 */
@PageId("FinFundTarget002")
@Controller
@RequestMapping("/invest/asset/fund/FinFundTarget002")
public class FinFundTarget002Controller extends BaseController{
    @Autowired
    private FinFundTargetService finFundTargetService;
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
            FinFundTargetDto dto = finFundTargetService.findEntity(map);
            if(null!=dto){
                finprodId = dto.getFinprodId();
            }
        }
        //增加快捷录入 add by zhangnian 2019/10/15 end
        //是否存在与工作流中
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            FinFundTargetDto dto = finFundTargetService.getBussinessEntityByWorkflowId(
                workflowId);
//            model.addAttribute("pageModel", dto.getPageModel());
            model.addAttribute("businessId", workflowId);
            //commonBusinessService.setTradeMarketId(dto,model);
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(finprodId)) {
            //获得页面展示元素
            FinFundTargetDto dto = finFundTargetService.selectByFinprodId(finprodId);
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
            FinFundTargetDto dto = new FinFundTargetDto();
            dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            /*dto.setPurSpeed(DictCodeList.清算速度_T_1.getValue());*/
            dto.setTradeMarket(DictCodeListExt1.交易场所_OTC场外.getValue());
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            /**jira:TWO-61 add by chendijun start**/
            dto.setCapiIncomeFeature(DictCodeList.是否_否.getValue());
            //add by yanya 20190830 增加投资性质默认值
//            dto.setInvestNature(DictCodeList.投资性质_固定收益类.getValue()); //modify by jiangpeng on 20210522 TISHKBS-1373
            /**jira:TWO-61 add by chendijun end**/
            //commonBusinessService.setTradeMarketId(dto,model);
            model.addAttribute("entity", dto);
        }
        this.setSelectOptionToModel(model, DictCode.基金类资产类型,DictCode.币种
            , DictCode.清算速度, DictCode.关联范围,DictCode.交易场所
            // 新增资产五级分类 wangguiqi 2019-07-23 start
            ,DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,DictCode.行内资产五级分类
            // 新增资产五级分类 wangguiqi 2019-07-23 end
            ,DictCode.交易所基金类型,DictCode.分级基金,DictCode.ETF类型
            ,DictCode.基金类资产二级类型,DictCode.是否, DictCode.投资性质);
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
        model.addAttribute("FUND_ASSET_SECOND_TYPE_01",DictCodeList.基金类资产二级类型_债券基金.getValue());
        model.addAttribute("FUND_ASSET_SECOND_TYPE_02",DictCodeList.基金类资产二级类型_货币市场基金.getValue());
        model.addAttribute("FUND_ASSET_SECOND_TYPE_03",DictCodeList.基金类资产二级类型_股票基金.getValue());
        model.addAttribute("FUND_ASSET_SECOND_TYPE_06",DictCodeList.基金类资产二级类型_其他公募基金.getValue());
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
    public Page<SelectOptionDto> getManager(){
        Map<String,Object>map = RequestUtil.getPageParam();
        return finFundTargetService.getManager(map);
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
    public FinFundTargetDto findEntity(@RequestBody Map<String,Object> map){
        return finFundTargetService.findEntity(map);
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
        FinFundTargetDto dto = finFundTargetService.getInterfFundDto(finprodMarketId + tradeMarketId);
        if (StringUtil.isNotEmpty(dto)) {
        	dto.setFinprodMarketId(finprodMarketId);
            ar.setData(dto);
        }else {
            List<ValidMessage> errList = new ArrayList<ValidMessage>();
            errList.add(new ValidMessage(this.getMessage("COMMON_E_0008",this.getMessage("FinFundTarget001.finprodMarketId"))));
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
        ValidateGroupEdit.class}) FinFundTargetDto dto, BindingResult result) {
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
        ValidateGroupEdit.class}) FinFundTargetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<FinFundTargetDto> processParameter = new ProcessParameter<FinFundTargetDto>();
        WorkFlowParameter<FinFundTargetDto> params = new WorkFlowParameter<FinFundTargetDto>();
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
        params.setComment(entity -> SpringContextUtil.getTextValue("FinFundTarget001.fundId") + "(" + entity.getFinprodMarketId() + ")"+ "-"
            + SpringContextUtil.getTextValue("FinFundTarget001.finprodName") + "("
            + entity.getFinprodName() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = finFundTargetService.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    //modify by mashukai 20201117 BS-2006公募基金草稿箱提交时中债报送信息反显没有值问题
    @Override
    public Object after(String draftobj, Model model) {
        Map<String,Object> map =model.asMap();
        FinFundTargetDto dto = (FinFundTargetDto)map.get("entity");
        if(StringUtil.isNotEmpty(getParam(GlobalConstant.WORKFLOW_ID))){
            dto.setWorkflowId(getParam(GlobalConstant.WORKFLOW_ID));
            model.addAttribute("entity",dto);
        }
        return super.after(draftobj, model);
    }
    //modify by mashukai 20201117 BS-2006公募基金草稿箱提交时中债报送信息反显没有值问题
}
