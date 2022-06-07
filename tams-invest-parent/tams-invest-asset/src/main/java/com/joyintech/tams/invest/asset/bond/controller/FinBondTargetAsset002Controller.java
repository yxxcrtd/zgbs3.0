/*
 * 文件名：FinBondTargetAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.bond.controller;

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
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.component.business.bond.BusiFinBondRatingInfoService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.tams.invest.asset.bond.service.FinBondTargetAssetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 债券类标的信息新建
 * @author zhangnian
 * @version 1.0 2018年9月20日
 * @see FinBondTargetAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinBondTargetAsset002")
@RequestMapping("/invest/asset/bond/FinBondTargetAsset002")
public class FinBondTargetAsset002Controller extends BaseController{

    @Autowired
    private FinBondTargetAssetService finBondTargetAssetService;
    
    @Autowired
    private IPlatformDate iPlatformDate;
    
    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private BusiFinBondRatingInfoService busiFinBondRatingInfoService;

    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.交易场所,DictCode.WIND一级分类_含债券一级分类等,DictCode.境内外,
		// 新增邮储五级分类 -wangguiqi on 2019/7/19 -start
		DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类, DictCode.行内资产五级分类,
		// 新增邮储五级分类 -wangguiqi on 2019/7/19 -end
		DictCode.WIND二级分类_含债券二级分类等,DictCode.本金收益特征,DictCode.发行方式,DictCode.是否,DictCode.币种,DictCode.登记托管机构_中债,
        //新增城投债级别 add by gejiaao 2019/8/22
        DictCode.城投债级别,DictCode.交易平台
    };
    @Override
    public String init(Model model){
        //初始化基本信息画面数据字典
        setSelectOptionToModel(model, DICT_CODES);
        String finprodId = this.getParam("finprodId");
        String finprodType = this.getParam("finprodType");
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
            FinBondTargetAssetDto finBondTargetAssetDto = finBondTargetAssetService.getBasicInfos(
                finprodId, finprodType);
            //commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            //点击修改保存提交会校验实际授信主体必填问题
            String a = CacheUtil.getSysParam(BussinessConstant.CEMODEL);
            if(StringUtil.equals("01", a)){
            	finBondTargetAssetDto.setCreditFlag("");
            }
            model.addAttribute("entity", finBondTargetAssetDto);
        }//来自于代办
        else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            FinBondTargetAssetDto finBondTargetAssetDto = finBondTargetAssetService.getBussinessEntityByWorkflowId(workflowId);
            //commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
            //设置到model中
            model.addAttribute("entity", finBondTargetAssetDto);
        }else{
            FinBondTargetAssetDto finBondTargetAssetDto = new FinBondTargetAssetDto();
            finBondTargetAssetDto.setFinprodId(IDGenerator.generate());
            finBondTargetAssetDto.setIsAutoUpdate(DictCodeList.是否_是.getValue());
            finBondTargetAssetDto.setBlnArea(DictCodeList.境内外_境内.getValue());
            //commonBusinessService.setTradeMarketId(finBondTargetAssetDto,model);
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
        //add by lizhi 是否显示中债信息开关 2020年2月28日  start
        model.addAttribute("ASSET_INFO_CHB_HIDE", 
                CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE)==null? 
                        DictCodeListExt1.是或否_是.getValue(): CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE));
        //add by lizhi 是否显示中债信息开关 2020年2月28日  end
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
        ValidateGroupEdit.class}) FinBondTargetAssetDto dto, BindingResult result) {
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
        ValidateGroupEdit.class}) FinBondTargetAssetDto dto, BindingResult br) throws WorkFlowException {
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
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        WorkFlowParameter<FinBondTargetAssetDto> params = new WorkFlowParameter<FinBondTargetAssetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产信息.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment(getMessage("FinBondTargetAsset002.finprodId")+
                GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId(), GlobalConstant.CLOSE_PARENTHESIS+
                getMessage("FinBondTargetAsset002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+
                entity.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinBondTargetAssetDto> parameter = new ProcessParameter<FinBondTargetAssetDto>();
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
        throws ParseException {
        AjaxResult ar = new AjaxResult();
        // 债券新建新增快捷录入功能 add by renping 20191010 start
        FinBondTargetAssetDto dto = finBondTargetAssetService.getBondDto(
                finprodMarketId + tradeMarketId);
        if (StringUtil.isNotEmpty(dto)) {
            dto.setFinprodMarketId(finprodMarketId);
            //快录时不带出上市代码
            dto.setAppearMarketId(null);
            //债券快录时不考虑  授信
            dto.setCreditFlag("");
            ar.setData(dto);
        }else {
            dto = finBondTargetAssetService.getInterfBondDto(
                    finprodMarketId + tradeMarketId);

            if (StringUtil.isNotEmpty(dto)) {
                //债券快录时不考虑  授信
                dto.setCreditFlag("");
                dto.setFinprodMarketId(finprodMarketId);
                ar.setData(dto);
            } else {
                // 债券新建新增快捷录入功能 add by renping 20191010 end
                List<ValidMessage> errList = new ArrayList<>();
                errList.add(new ValidMessage(this.getMessage("COMMON_E_0008",
                        this.getMessage("FinBondTargetAsset002.finprodId"))));
                ar.setData(errList);
            }
        }
        return ar;
    }

    @ResponseBody
    @RequestMapping("/getBasicInfo")
    public FinBondTargetAssetDto getBasicInfo(@RequestBody FinBondTargetAssetDto finBondTargetAssetDto){
        FinBondTargetAssetDto resultDto = finBondTargetAssetService.getBasicInfo(
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
    public Page<SelectOptionDto> getfinprodIdForAdd() {
        return finBondTargetAssetService.getfinprodIdForAdd(RequestUtil.getPageParam());
    }

    @ResponseBody
    @RequestMapping("/getIsNetSett")
    private AjaxResult getIsNetSett(@RequestParam("finprodMarketId") String finprodMarketId,
                                             @RequestParam("tradeMarket") String tradeMarket,
                                             @RequestParam("type9") String type9,
                                             @RequestParam("isPpb") String isPpb) {
        Map<String,String> map=new HashMap<String,String> ();
        AjaxResult ar = new AjaxResult();
        String isNetSett=StringUtil.EMPTY;
        //String tradePlat=StringUtil.EMPTY;
        if(StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), tradeMarket)){
            // 全额结算 标识
            boolean settleFlag = false;
            // 判断债券代码 是否在 139000-139999代码段债券、123000-123499代码段债券 范围内
            String secid = finprodMarketId;
            if(finprodMarketId.contains (".")){
                secid=finprodMarketId.substring(0,finprodMarketId.indexOf("."));
            }
            Pattern pattern=Pattern.compile("^-?\\d+(\\.\\d+)?$");
            //是否属于139000-139999代码段债券
            if(pattern.matcher(secid).matches()) {
                Long l = Long.parseLong(secid);
                if ((l > 139000 && l < 139999) || (l > 123000 && l < 123499)) {
                    settleFlag = true;
                }
            }
            //全额结算 : 私募债 || 139000-139999代码段债券、123000-123499代码段债券 || 资产支持证券
            if(StringUtil.equals(isPpb, "Y") || settleFlag || StringUtil.equals(BussinessDictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue(), type9)){
                isNetSett="N";
                //tradePlat=DictCodeList.交易平台_固定收益平台.getValue();
            }else{
                //以上情况都不符合则为纳入净额结算标准范围的债券
                isNetSett="Y";
                //tradePlat=DictCodeList.交易平台_固定收益平台.getValue()+","+DictCodeList.交易平台_竞价撮合平台.getValue()+","+DictCodeList.交易平台_大宗交易平台.getValue();
            }
        }else if(StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), tradeMarket)){
            //全额结算: 私募债 || 资产支持证券
            if(StringUtil.equals(isPpb, "Y") || StringUtil.equals(BussinessDictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue(), type9)){
                isNetSett="N";
                //tradePlat=DictCodeList.交易平台_综合协议交易平台.getValue();
            }else{
                isNetSett="Y";
                //tradePlat=DictCodeList.交易平台_竞价撮合平台.getValue()+","+DictCodeList.交易平台_综合协议交易平台.getValue();
            }
        }
        map.put("isNetSett", isNetSett);
        //map.put("tradePlat", tradePlat);

        ar.setData (map);
        return ar;
    }

    /**
     * add by xxg 20210721
     * 根据产品代码得到起息日和到期日
     */
    @ResponseBody
    @RequestMapping("/isSameMdateAndVdate")
    public Boolean isSameMdateAndVdate(@RequestBody FinProduct finProduct){
        //债券标的   债券修改或新建，关联一级市场债券时，若关联的一级债券起息日、到期日与资产起息日、到期日不一致，需要进行提醒
        FinBondTargetAssetDto dto = finBondTargetAssetService.getMdateAndVdate(finProduct.getoFinprodId());
        if (null != dto.getVdate() && null != dto.getMdate()) {
            if (DateUtil.compareDate(dto.getMdate(), finProduct.getMdate()) != 0
                    || DateUtil.compareDate(dto.getVdate(), finProduct.getVdate()) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化评级信息
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @RequestMapping("/getFinBondRatingDto")
    @ResponseBody
    public List<MstBondCreditRating> getFinBondRatingDto(@RequestParam("issuer") String issuer) {
        List<MstBondCreditRating> mstBondCreditRatingList = new ArrayList<MstBondCreditRating>();
        Map<String,Object> ztparam = new HashMap<String,Object>();
        //主体为发行人（债券类资产）
        if(StringUtil.isNotEmpty(issuer)) {
            ztparam.put("oCode", issuer);
            mstBondCreditRatingList.addAll(busiFinBondRatingInfoService.getmstBondCreditRatingByOcode(ztparam));
        }
        return mstBondCreditRatingList;
    }

}
