/*
 * 文件名：ValBondTargetAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.assets.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.assets.bond.dto.ValBondTargetAssetDto;
import com.joyintech.tams.valuation.assets.bond.service.ValBondTargetAssetService;
import com.joyintech.tams.valuation.component.common.constant.BussinessConstant;
//import com.joyintech.tams.valuation.component.common.core.IPlatformDate;
import com.joyintech.tams.valuation.component.common.service.ValCommonBusinessService;
import com.joyintech.tams.valuation.component.common.service.ValDateCalService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 债券类标的信息新建
 * @author zhangnian
 * @version 1.0 2018年9月20日
 * @see ValBondTargetAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("ValBondTargetAsset002")
@RequestMapping("/valuation/assets/bond/ValBondTargetAsset002")
public class ValBondTargetAsset002Controller extends BaseController{

    @Autowired
    private ValBondTargetAssetService finBondTargetAssetService;
    
    @Autowired
    private ValDateCalService dateCalService;
    
//    @Autowired
//    private IPlatformDate iPlatformDate;
    
    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private ValCommonBusinessService commonBusinessService;
    
    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易场所,DictCode.WIND一级分类_含债券一级分类等,DictCode.境内外,
		// 新增邮储五级分类 -wangguiqi on 2019/7/19 -start
		DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类, DictCode.行内资产五级分类,
		// 新增邮储五级分类 -wangguiqi on 2019/7/19 -end
		DictCode.WIND二级分类_含债券二级分类等,DictCode.本金收益特征,DictCode.发行方式,DictCode.是否,DictCode.币种,DictCode.登记托管机构_中债,
        //新增城投债级别 add by gejiaao 2019/8/22
        DictCode.城投债级别
    };
    @Override
    public String init(Model model){
        //初始化基本信息画面数据字典
        setSelectOptionToModel(model, DICT_CODES);
        String finprodId = this.getParam("finprodId");
        //String status = getParam("status");
        String workflowId = getParam("workflowId");
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("isTradeCalendar",DictCodeList.是否_是.getValue());
        /* add by zhangpuyu 2019/20/24 start */
        model.addAttribute("counterType",DictCodeListExt1.交易对手类型_分行.getValue());
        /* add by zhangpuyu 2019/20/24 end */
        model.addAttribute("FINPROD_TYPE_TG_F01",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F18",DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F10",DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        model.addAttribute("COUPON_SPECIES_02", DictCodeList.息票品种_贴现.getValue());
        model.addAttribute("COUPON_SPECIES_03", DictCodeList.息票品种_零息.getValue());
        model.addAttribute("CCY_INIT", DictCodeList.币种_人民币_CNY.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_009", DictCodeListExt2.行内资产五级分类_金融机构次级债.getValue());//wangguiqi	2019/8/16
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_158", DictCodeListExt2.行内资产五级分类_政策性金融次级债.getValue());//wangguiqi	2019/8/16
        //平台日
        model.addAttribute("iPlatformDate", DateUtil.parse(DateUtil.getSysDate()));
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(finprodId)) {
            model.addAttribute("isTransInAsset", commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
            model.addAttribute("isHqTrade", finBondTargetAssetService.isHqTrade(finprodId)>0?true:false);
          //根据主键获取业务实体--标的金融产品基本信息
            ValBondTargetAssetDto finBondTargetAssetDto = finBondTargetAssetService.getBasicInfo(
                finprodId);
            commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            //点击修改保存提交会校验实际授信主体必填问题
            String a = CacheUtil.getSysParam(BussinessConstant.CEMODEL);
            if(StringUtil.equals("01", a)){
            	finBondTargetAssetDto.setCreditFlag("");
            }
            model.addAttribute("entity", finBondTargetAssetDto);
        }//来自于代办
        else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            ValBondTargetAssetDto finBondTargetAssetDto = finBondTargetAssetService.getBussinessEntityByWorkflowId(workflowId);
            commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            //设置到model中
            model.addAttribute("entity", finBondTargetAssetDto);
        }else{
            ValBondTargetAssetDto finBondTargetAssetDto = new ValBondTargetAssetDto();
            finBondTargetAssetDto.setFinprodId(IDGenerator.generate());
            finBondTargetAssetDto.setIsAutoUpdate(DictCodeList.是否_是.getValue());
            finBondTargetAssetDto.setBlnArea(DictCodeList.境内外_境内.getValue());
            commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            model.addAttribute("entity", finBondTargetAssetDto);
        }
        //资产类型
        model.addAttribute("INT_ASSET_TYPE", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("workflowId",workflowId);
        model.addAttribute("pageModel",this.getPageModel());
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("WINDTYPEONE_B14",DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        model.addAttribute("WINDTYPEONE_B15",DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());
        model.addAttribute("SVR_SHARES_TYPE_02",DictCodeListExt2.股票类型_优先股.getValue());
        model.addAttribute("SVR_ASSET_TYPE_SECOND_LEVEL", createChbAssetType());
        return super.init(model);
    }

    /**
     * 债券报送二级分类所有的资产负债类别不应该出现 1001:现金及活期存款，配置参数太多，通过代码控制
     * @return
     */
    private String createChbAssetType() {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.资产_负债二级类别, null);
        if (selectDto != null && !StringUtil.isEmptyList(selectDto.getOptions())) {
            List<SelectOptionDto> options = selectDto.getOptions();
            for (int index = options.size() - 1; index >= 0; index--) {
                SelectOptionDto option = options.get(index);
                if (StringUtil.equals(option.getId(), DictCodeListExt2.资产_负债二级类别_现金及活期存款.getValue())) {
                    options.remove(option);
                }
            }
            return JsonUtils.objectArray2Json(options.toArray());
        }
        return null;
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
        ValidateGroupEdit.class}) ValBondTargetAssetDto dto, BindingResult result) {
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
        ValidateGroupEdit.class}) ValBondTargetAssetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        //生成金融产品代码
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            dto = finBondTargetAssetService.getFinprodId(dto);
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
        dto.setFinprodType(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        WorkFlowParameter<ValBondTargetAssetDto> params = new WorkFlowParameter<ValBondTargetAssetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产信息.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("ValBondTargetAsset002.finprodId")+
                GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId(), GlobalConstant.CLOSE_PARENTHESIS+
                getMessage("ValBondTargetAsset002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+
                entity.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<ValBondTargetAssetDto> parameter = new ProcessParameter<ValBondTargetAssetDto>();
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        //封装标的类金融产品基础组件需要的DTO
        String msg = finBondTargetAssetService.start(parameter, dto);
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
            throws ParseException, IllegalAccessException, InstantiationException {
        AjaxResult ar = new AjaxResult();
        ValBondTargetAssetDto dto = finBondTargetAssetService.getInterfBondDto(
        		finprodMarketId + tradeMarketId);
        if (StringUtil.isNotEmpty(dto)) {
        	dto.setFinprodMarketId(finprodMarketId);
            ar.setData(dto);
        }
        else {
            // 债券新建新增快捷录入功能 add by renping 20191010 start
            dto = finBondTargetAssetService.getBondDto(
                finprodMarketId + tradeMarketId);
            if (StringUtil.isNotEmpty(dto)) {
                dto.setFinprodMarketId(finprodMarketId);
                ar.setData(dto);
            }
            else {
            // 债券新建新增快捷录入功能 add by renping 20191010 end
                List<ValidMessage> errList = new ArrayList<>();
                errList.add(new ValidMessage(this.getMessage("COMMON_E_0008",
                    this.getMessage("ValBondTargetAsset002.finprodId"))));
                ar.setData(errList);
            }
        }

        return ar;
    }

    @ResponseBody
    @RequestMapping("/getBasicInfo")
    public ValBondTargetAssetDto getBasicInfo(@RequestBody ValBondTargetAssetDto finBondTargetAssetDto)
             {
        ValBondTargetAssetDto resultDto = finBondTargetAssetService.getBasicInfo(
                finBondTargetAssetDto.getFinprodId());
        return resultDto;
    }

    /**
     * 获取新建界面的以及市场投标债券代码联想下拉框
     * 
     * @author wangguiqi
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getfinprodIdForAdd")
    public Page<SelectOptionDto> getfinprodIdForAdd() 
         {
        return finBondTargetAssetService.getfinprodIdForAdd(RequestUtil.getPageParam());
    }
    
    /**
     * 发行人的联想下拉框事件
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getIssuerId")
    public Page<SelectOptionDto> getIssuerId(){
        Map<String,Object> map = RequestUtil.getPageParam();
        //sql中参数不是search，页面控件传回是search，做转换 yjm 20190731
        map.put("param", map.get("search"));
        Page<SelectOptionDto> temp = finBondTargetAssetService.getIssuerId(map);
        return temp;
    }
    /**
     * 交易日历下拉框查询
     * 
     * @return 交易日历
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("queryCalendarInfoList")
    public Page<SelectOptionDto> queryCalendarInfoList(){
        return finBondTargetAssetService.queryCalendarInfoList(RequestUtil.getPageParam());
    }
    /**
     * 标的到期日是否为节假日
     * @param finProduct
     * @return
     */
    @ResponseBody
    @RequestMapping("isHoliday")
    public Boolean isHoliday(@RequestBody FinProduct finProduct){
        if(null==finProduct.getMdate()||StringUtil.isEmpty(finProduct.getCalendarId())){
            return false;
        }
        return dateCalService.isDealCalendarHoliday(finProduct.getMdate(), finProduct.getCalendarId());
    }
}
