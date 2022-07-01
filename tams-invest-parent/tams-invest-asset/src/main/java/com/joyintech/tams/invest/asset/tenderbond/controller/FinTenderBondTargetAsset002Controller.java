/*
 * 文件名：FinTenderBondTargetAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangpeng
 * 修改时间：2019年7月4日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.tenderbond.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
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
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.asset.bond.controller.FinBondTargetAsset002Controller;
import com.joyintech.tams.invest.asset.tenderbond.dto.FinTenderBondTargetAssetDto;
import com.joyintech.tams.invest.asset.tenderbond.service.FinTenderBondTargetAssetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 一级债券类标的信息新建
 * @author wangpeng
 * @version 1.0 2019年7月4日
 * @see FinTenderBondTargetAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinTenderBondTargetAsset002")
@RequestMapping("/invest/asset/tenderbond/FinTenderBondTargetAsset002")
public class FinTenderBondTargetAsset002Controller extends BaseController{

    @Autowired
    private FinTenderBondTargetAssetService finTenderBondTargetAssetService;
    
    @Autowired
    private IPlatformDate iPlatformDate;
    
    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private CommonBusinessService commonBusinessService;
    
    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易场所,DictCode.WIND一级分类_含债券一级分类等,DictCode.境内外,
    	DictCode.行内资产二级分类,DictCode.行内资产三级分类,DictCode.行内资产四级分类,DictCode.行内资产五级分类,//新增资产二、三、四、五级分类 王贵奇 2019/7/31
        DictCode.WIND二级分类_含债券二级分类等,DictCode.本金收益特征,DictCode.发行方式,DictCode.是否,DictCode.币种,DictCode.登记托管机构_中债};
    @Override
    public String init(Model model){
        //初始化基本信息画面数据字典
        setSelectOptionToModel(model, DICT_CODES);
        String finprodId = this.getParam("finprodId");
        //String status = getParam("status");
        String workflowId = getParam("workflowId");
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("isTradeCalendar",DictCodeList.是否_是.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F01",BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F18",DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("COUPON_SPECIES_02", DictCodeList.息票品种_贴现.getValue());
        model.addAttribute("COUPON_SPECIES_03", DictCodeList.息票品种_零息.getValue());
        model.addAttribute("CCY_INIT", DictCodeList.币种_人民币_CNY.getValue());
        //平台日
        model.addAttribute("iPlatformDate", iPlatformDate.getSysDate());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(finprodId)) {
            model.addAttribute("isTransInAsset", commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
          //根据主键获取业务实体--标的金融产品基本信息
            FinTenderBondTargetAssetDto finBondTargetAssetDto = finTenderBondTargetAssetService.getBasicInfo(
                finprodId);
            //commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            model.addAttribute("entity", finBondTargetAssetDto);
            // 判断是否关联二级债券
            if(finTenderBondTargetAssetService.selectCountByOFinprodId(finprodId)>0){
                model.addAttribute("isRelatedSecondBond", BussinessDictCodeList.是否_是.getValue());
            }
        }//来自于代办
        else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
        	FinTenderBondTargetAssetDto finBondTargetAssetDto = finTenderBondTargetAssetService.getBussinessEntityByWorkflowId(workflowId);
            //commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            //设置到model中
            model.addAttribute("entity", finBondTargetAssetDto);
        }else{
        	FinTenderBondTargetAssetDto finBondTargetAssetDto = new FinTenderBondTargetAssetDto();
            finBondTargetAssetDto.setFinprodId(IDGenerator.generate());
            finBondTargetAssetDto.setIsAutoUpdate(DictCodeList.是否_是.getValue());
            finBondTargetAssetDto.setBlnArea(DictCodeList.境内外_境内.getValue());
            //commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            model.addAttribute("entity", finBondTargetAssetDto);
        }
        //资产类型
        model.addAttribute("INT_ASSET_TYPE", BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        model.addAttribute("workflowId",workflowId);
        model.addAttribute("pageModel",this.getPageModel());
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        return super.init(model);
    }
    
    /**
     * check--保存
     * @param dto
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) FinTenderBondTargetAssetDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 
     * 保存
     * @param dto
     * @param br
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) FinTenderBondTargetAssetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        //生成金融产品代码
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            dto = finTenderBondTargetAssetService.getFinprodId(dto);
        }
        //文件信息
        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getFinprodId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getFinprodId());
        }
        dto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
        WorkFlowParameter<FinTenderBondTargetAssetDto> params = new WorkFlowParameter<FinTenderBondTargetAssetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getFinprodId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_一级投标债券类资产管理.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("FinBondTargetAsset002.finprodId")+
                GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId(), GlobalConstant.CLOSE_PARENTHESIS+
                getMessage("FinBondTargetAsset002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+
                entity.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinTenderBondTargetAssetDto> parameter = new ProcessParameter<FinTenderBondTargetAssetDto>();
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        //封装标的类金融产品基础组件需要的DTO
        String msg = finTenderBondTargetAssetService.start(parameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 新建时根据市场代码查询wind接口债券信息
     * 
     * @param finprodMarketId 市场代码
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getInterfBondDto")
    public AjaxResult getInterfBondDto(@RequestParam("finprodMarketId") String finprodMarketId,
    		@RequestParam("tradeMarketId") String tradeMarketId)
        throws ParseException {
        AjaxResult ar = new AjaxResult();
        FinTenderBondTargetAssetDto dto = finTenderBondTargetAssetService.getInterfBondDto(
        		finprodMarketId + tradeMarketId);
        if (StringUtil.isNotEmpty(dto)) {
        	dto.setFinprodMarketId(finprodMarketId);
            ar.setData(dto);
        }
        else {
            List<ValidMessage> errList = new ArrayList<>();
            errList.add(new ValidMessage(this.getMessage("COMMON_E_0008",
                this.getMessage("FinBondTargetAsset002.finprodId"))));
            ar.setData(errList);
        }

        return ar;
    }

    @ResponseBody
    @RequestMapping("/getBasicInfo")
    public FinTenderBondTargetAssetDto getBasicInfo(@RequestBody FinTenderBondTargetAssetDto finBondTargetAssetDto){
    	FinTenderBondTargetAssetDto resultDto = finTenderBondTargetAssetService.getBasicInfo(
                finBondTargetAssetDto.getFinprodId());
        return resultDto;
    }
    
    /**
     * 从一级市场新债日历获取数据创建一级市场债券标的
     */
    @ResponseBody
    @RequestMapping("getInsert")
    public void getInsert() {
    	OperationType type = OperationType.NEW;
    	Map<String, Object> params = RequestUtil.getPageParam();
    	String finprodId = RequestUtil.getParam("secId");
    	String finprodAbbr= RequestUtil.getParam("secAbbr");
    	String underWriterId = RequestUtil.getParam("mainSaler");
    	String ztGradeResult = RequestUtil.getParam("issuerGradeResult");
    	String zxGradeResult = RequestUtil.getParam("secGradeResult");
    	String bidDate = RequestUtil.getParam("bidDateStr");
    	String payDate = RequestUtil.getParam("payDateStr");
    	String onmarketDate = RequestUtil.getParam("onmarketDateStr");
    	FinTenderBondTargetAssetDto dto = new FinTenderBondTargetAssetDto();
    	dto = BeanUtil.mapToObject(params,dto.getClass());
    	dto.setBidDate(DateUtil.parse(bidDate));
    	dto.setFinprodId(finprodId);
    	dto.setFinprodMarketId(finprodId);
    	dto.setFinprodAbbr(finprodAbbr);
    	dto.setFinprodName(finprodAbbr);
    	dto.setUnderWriterId(underWriterId);
    	dto.setZtGradeResult(ztGradeResult);
    	dto.setZxGradeResult(zxGradeResult);
    	dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
    	dto.setId(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
    	dto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
    	finTenderBondTargetAssetService.doComplete(dto, type);
    	
    	
    }
}
