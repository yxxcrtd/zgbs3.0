/*
 * 文件名：FinProjectTargetAsset002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.asset.project.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.asset.depo.dto.FinDepositTargetDto;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections.CollectionUtils;
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
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.asset.bond.dto.FinBondTargetAssetDto;
import com.joyintech.tams.invest.asset.project.dto.FinProjectTargetAssetDto;
import com.joyintech.tams.invest.asset.project.service.FinProjectTargetAssetService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 项目类资产标的信息
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月25日
 * @see FinProjectTargetAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinProjectTargetAsset002")
@RequestMapping("/invest/asset/project/FinProjectTargetAsset002")
public class FinProjectTargetAsset002Controller extends BaseController{

    @Autowired
    private FinProjectTargetAssetService finProjectTargetAssetService;
    
    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Autowired
    private BaseGetBusinessId baseGetBusinessId;
    
    /**
     * 初始化数据字典下拉框
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.资产_负债二级类别,
            DictCode.项目类资产二级类型,DictCode.项目类一级分类, DictCode.资产来源, DictCode.投资性质,
        DictCode.资产来源条线, DictCode.币种, DictCode.本金收益特征, DictCode.证监会行业分类1, DictCode.证监会行业分类2, DictCode.机构所属行业一级分类,
        DictCode.机构所属行业二级分类, DictCode.收受益权类型, DictCode.重点监控行业和领域类别, DictCode.担保方式
        // 项目类资产五级分类改造 add by renping 2019-07-25 start
        ,DictCode.行内资产二级分类, DictCode.行内资产三级分类, DictCode.行内资产四级分类, DictCode.行内资产五级分类};
        // 项目类资产五级分类改造 add by renping 2019-07-25 end
    
    @Override
    public String init(Model model){
        //初始化基本信息画面数据字典
        setSelectOptionToModel(model, DICT_CODES);
        String finprodId = this.getParam("finprodId");
        //String status = getParam("status");
        String workflowId = getParam("workflowId");
        model.addAttribute("YES",DictCodeList.是否_是.getValue());
        model.addAttribute("isTradeCalendar",DictCodeList.是否_是.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F01",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F07",DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F18",DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        //主画面联动数据字典参数
        //频率 add by lwj 20200409 start
        model.addAttribute("FREQUENCY_UNIT_07", DictCodeList.频率_到期一次.getValue());
        model.addAttribute("FREQUENCY_UNIT_16", DictCodeList.频率_按明确时间的还款计划还本.getValue());
        //频率 add by lwj 20200409 end
        //计息类型 add by lwj 20200409 start
        model.addAttribute("SVR_COUPON_SPECIES_02", DictCodeListExt1.计息类型_利随本清固定利率.getValue());
        model.addAttribute("SVR_COUPON_SPECIES_03", DictCodeListExt1.计息类型_利随本清浮动利率.getValue());
        model.addAttribute("SVR_COUPON_SPECIES_04", DictCodeListExt1.计息类型_附息式固定利率.getValue());
        model.addAttribute("SVR_COUPON_SPECIES_05", DictCodeListExt1.计息类型_附息式浮动利率.getValue());
        //计息类型 add by lwj 20200409 end
        //利息分布方式 add by lwj 20200409 start
        model.addAttribute("SVR_INTPAY_RULE_01", DictCodeListExt1.利息分布方式_平均分配.getValue());
        model.addAttribute("SVR_INTPAY_RULE_02", DictCodeListExt1.利息分布方式_按实际天数分配.getValue());
        //利息分布方式 add by lwj 20200409 end
        //还本方式 add by lwj 20200409 start
        model.addAttribute("SVR_REPAYMENT_TYPE_01", DictCodeListExt1.还本方式_过手摊还.getValue());
        model.addAttribute("SVR_REPAYMENT_TYPE_02", DictCodeListExt1.还本方式_固定摊还.getValue());
        //还本方式 add by lwj 20200409 end
        //分期还本条款标识 add by lwj 20200409 start
        model.addAttribute("SVR_PERIOD_REPAYMENT_FLAG_01", DictCodeListExt1.分期还本条款标识_分期还本.getValue());
        model.addAttribute("SVR_PERIOD_REPAYMENT_FLAG_02", DictCodeListExt1.分期还本条款标识_到期一次还本.getValue());
        model.addAttribute("SVR_PERIOD_REPAYMENT_FLAG_99", DictCodeListExt1.分期还本条款标识_其他.getValue());
        //分期还本条款标识 add by lwj 20200409 end
        model.addAttribute("ASSET_TYPE_PROJECT_01", DictCodeList.项目类一级分类_非标债权.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_02", DictCodeList.项目类一级分类_结构化证券.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_03", DictCodeList.项目类一级分类_其他权益类投资.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_04", DictCodeList.项目类一级分类_其他产业投资基金.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_05", DictCodeList.项目类一级分类_股票定向增发投资.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_06", DictCodeList.项目类一级分类_基础设施基金.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_07", DictCodeList.项目类一级分类_券商债权收益权投资.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_08", DictCodeList.项目类一级分类_信贷资产流转项目.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_09", DictCodeList.项目类一级分类_券商收益凭证.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_10", DictCodeList.项目类一级分类_股票质押式回购.getValue());
        model.addAttribute("ASSET_TYPE_PROJECT_11", DictCodeList.项目类一级分类_理财直接融资工具.getValue());
        model.addAttribute("ASSET_TYPE_SECOND_LEVEL_1201", DictCodeListExt2.资产_负债二级类别_票据类.getValue());
        model.addAttribute("ASSET_TYPE_SECOND_LEVEL_1210", DictCodeListExt2.资产_负债二级类别_信用证.getValue());
        model.addAttribute("ASSET_TYPE_SECOND_LEVEL_1299", DictCodeListExt2.资产_负债二级类别_其他非标准化债权类投资.getValue());
        model.addAttribute("ASSET_SOURCE_01", DictCodeListExt1.资产来源_行内.getValue());
        model.addAttribute("ASSET_SOURCE_02", DictCodeListExt1.资产来源_行外.getValue());
        model.addAttribute("INVEST_01", DictCodeList.投资性质_固定收益类.getValue());
        model.addAttribute("INVEST_02", DictCodeList.投资性质_权益类.getValue());
        // 项目类资产五级分类改造 add by renping 2019-07-25 start
        model.addAttribute("PINAN_ASSET_TYPE_F06", DictCodeList.平安资产类型_项目类资产.getValue());
        //增加快捷录入 add by zhangnian 2019/10/15 start
        String finprodMarketId=getParam("finprodMarketId");
        boolean isQuickInput = Boolean.parseBoolean(getParam("isQuickInput"));
        if (isQuickInput&&StringUtil.isNotEmpty(finprodMarketId)){
            Map<String,Object> quickInputMap = new HashMap<String,Object>();
            quickInputMap.put("finprodMarketId",finprodMarketId);
            FinProjectTargetAssetDto dto = finProjectTargetAssetService.getBasicInfo(quickInputMap);
            if(null!=dto){
                finprodId = dto.getFinprodId();
            }
        }
        // 项目类资产五级分类改造 add by renping 2019-07-25 end
        //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(finprodId)) {
            if(!isQuickInput){
                model.addAttribute("isTransInAsset", commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
            }
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("finprodId", finprodId);
            //根据主键获取业务实体--标的金融产品基本信息
            FinProjectTargetAssetDto finProjectTargetAssetDto = finProjectTargetAssetService.getBasicInfo(
                paramMap);
            /*//CMS附件信息 
            List<PaFinProductAssetAttach>  list = finProjectTargetAssetService.getBusinessIdByFinprodId(finprodId);
            if(null!=list&&!list.isEmpty()){
                for(PaFinProductAssetAttach entity:list){
                    if(StringUtil.equals(entity.getAssetAttachType(), DictCodeList.资产附件类型_批复文件.getValue())){
                        finProjectTargetAssetDto.setAssetAttachNo1(entity.getAssetAttachNo());
                    }
                    if(StringUtil.equals(entity.getAssetAttachType(), DictCodeList.资产附件类型_CMS批复文件.getValue())){
                        finProjectTargetAssetDto.setAssetAttachNo2(entity.getAssetAttachNo());
                    }
                }
            }*/
            model.addAttribute("entity", finProjectTargetAssetDto);
        }//来自于代办
        else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            FinProjectTargetAssetDto finProjectTargetAssetDto = finProjectTargetAssetService.getBussinessEntityByWorkflowId(workflowId);
            //设置到model中
            model.addAttribute("entity", finProjectTargetAssetDto);
        }else{
            FinBondTargetAssetDto finBondTargetAssetDto = new FinBondTargetAssetDto();
            finBondTargetAssetDto.setFinprodId(IDGenerator.generate());
        }
        //资产类型
        model.addAttribute("INT_ASSET_TYPE", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        model.addAttribute("workflowId",workflowId);
        model.addAttribute("pageModel",this.getPageModel());
        model.addAttribute("pageId",this.getPageId());
      //取系统参数
        model.addAttribute("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
        model.addAttribute("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        model.addAttribute("pflshType", DictCodeList.是否_是.getValue());
        //add by zhangnian  20191008 start
        //作用于五级分类联动中债
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_039",DictCodeListExt2.行内资产五级分类_信贷资产流转项目.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_040",DictCodeListExt2.行内资产五级分类_信贷资产流转结构化产品.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_041",DictCodeListExt2.行内资产五级分类_其他信贷资产流转.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_042",DictCodeListExt2.行内资产五级分类_其他新增可投资资产.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_043",DictCodeListExt2.行内资产五级分类_商业承兑汇票.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_044",DictCodeListExt2.行内资产五级分类_银行承兑汇票.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_045",DictCodeListExt2.行内资产五级分类_混合类票据.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_046",DictCodeListExt2.行内资产五级分类_其他票据.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_047",DictCodeListExt2.行内资产五级分类_信用证.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_048",DictCodeListExt2.行内资产五级分类_政府购买服务.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_049",DictCodeListExt2.行内资产五级分类_PPP.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_050",DictCodeListExt2.行内资产五级分类_公司客户融资.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_051",DictCodeListExt2.行内资产五级分类_项目融资.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_052",DictCodeListExt2.行内资产五级分类_融贷通.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_053",DictCodeListExt2.行内资产五级分类_存单质押.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_054",DictCodeListExt2.行内资产五级分类_其他信托贷款.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_055",DictCodeListExt2.行内资产五级分类_其他委托贷款.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_056",DictCodeListExt2.行内资产五级分类_信贷资产转让.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_057",DictCodeListExt2.行内资产五级分类_融资融券收益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_058",DictCodeListExt2.行内资产五级分类_股票质押收益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_059",DictCodeListExt2.行内资产五级分类_证券公司收益凭证收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_060",DictCodeListExt2.行内资产五级分类_信贷资产转让收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_061",DictCodeListExt2.行内资产五级分类_信托贷款收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_062",DictCodeListExt2.行内资产五级分类_委托贷款收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_063",DictCodeListExt2.行内资产五级分类_委托债权收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_064",DictCodeListExt2.行内资产五级分类_私募债权收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_065",DictCodeListExt2.行内资产五级分类_应收账款收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_066",DictCodeListExt2.行内资产五级分类_带回购条款的股权性融资收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_067",DictCodeListExt2.行内资产五级分类_票据收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_068",DictCodeListExt2.行内资产五级分类_其他收受益权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_069",DictCodeListExt2.行内资产五级分类_委托债权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_070",DictCodeListExt2.行内资产五级分类_应收账款.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_071",DictCodeListExt2.行内资产五级分类_其他非产业基金.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_072",DictCodeListExt2.行内资产五级分类_其他产业基金.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_073",DictCodeListExt2.行内资产五级分类_私募债权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_074",DictCodeListExt2.行内资产五级分类_保理.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_075",DictCodeListExt2.行内资产五级分类_股票质押式回购场内.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_076",DictCodeListExt2.行内资产五级分类_股票质押式回购场外.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_077",DictCodeListExt2.行内资产五级分类_融资租赁.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_078",DictCodeListExt2.行内资产五级分类_收益凭证.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_079",DictCodeListExt2.行内资产五级分类_收益凭证OTC.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_080",DictCodeListExt2.行内资产五级分类_银登类标准ABS.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_081",DictCodeListExt2.行内资产五级分类_银登非标准ABS.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_082",DictCodeListExt2.行内资产五级分类_其他私募ABS.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_083",DictCodeListExt2.行内资产五级分类_其他产业基金股东借款.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_084",DictCodeListExt2.行内资产五级分类_其他非产业基金股东借款.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_085",DictCodeListExt2.行内资产五级分类_其他非标准划债券类投资.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_086",DictCodeListExt2.行内资产五级分类_股权.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_087",DictCodeListExt2.行内资产五级分类_股票定向增发.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_088",DictCodeListExt2.行内资产五级分类_夹层.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_089",DictCodeListExt2.行内资产五级分类_私募股权基金.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_090",DictCodeListExt2.行内资产五级分类_风险投资.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_091",DictCodeListExt2.行内资产五级分类_阳光私募.getValue());
        model.addAttribute("CUSTOM_ASSET_TYPE_FIVE_092",DictCodeListExt2.行内资产五级分类_其他权益类投资.getValue());
        model.addAttribute("isQuickInput",isQuickInput);
        //add by zhangnian  20191008 end
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
        ValidateGroupEdit.class}) FinProjectTargetAssetDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 融资人、担保人、投资顾问（下拉框）
     * @return
     */
    @ResponseBody
    @RequestMapping("/getFinancier")
    public Page<SelectOptionDto> getFinancier(){
        Map<String,Object>map = RequestUtil.getPageParam();
        Page<SelectOptionDto> temp = finProjectTargetAssetService.getFinancier(map);
        return temp;
    }

    @ResponseBody
    @RequestMapping("/getGuarantee")
    public Page<SelectOptionDto> getGuarantee(){
        Map<String,Object>map = RequestUtil.getPageParam();
        return finProjectTargetAssetService.getGuarantee(map);
    }

    @ResponseBody
    @RequestMapping("/getGuarantee2")
    public Message<Page<SelectOptionDto>> getGuarantee2(){
        Map<String,Object>map = RequestUtil.getPageParam();
        return Message.success("查询成功",finProjectTargetAssetService.getGuarantee(map));
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
        ValidateGroupEdit.class}) FinProjectTargetAssetDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
      //生成金融产品代码
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
            dto = finProjectTargetAssetService.getFinprodId(dto);
        }
        /*if(StringUtil.isEmpty(dto.getAssetAttachNo1())){
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setSeqNo("9996");
            dto.setAssetAttachNo1(baseGetBusinessId.buildBussinessCode(parameter));
        }
        if(StringUtil.isEmpty(dto.getAssetAttachNo2())){
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setSeqNo("9996");
            dto.setAssetAttachNo2(baseGetBusinessId.buildBussinessCode(parameter));
        }*/
        //文件信息
        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getFinprodId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getFinprodId());
        }
        /*//批复文件
        if (CollectionUtils.isNotEmpty(dto.getFileArrayCms1())
            &&StringUtil.isNotEmpty(dto.getAssetAttachNo1())){
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArrayCms1().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getAssetAttachNo1());
        }
        //Cms批复文件
        if (CollectionUtils.isNotEmpty(dto.getFileArrayCms2())
            &&StringUtil.isNotEmpty(dto.getAssetAttachNo2())){
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArrayCms2().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getAssetAttachNo2());
        }*/
        dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        WorkFlowParameter<FinProjectTargetAssetDto> params = new WorkFlowParameter<FinProjectTargetAssetDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getFinprodMarketId());
        params.setBusinessType(DictCodeList.共通业务类型_项目类资产信息.getValue());
        // 设置流程业务说明
        params.setComment(entity ->
        WorkFlowUtil.concatComment(this.getMessage("FinProjectTargetAsset002.finprodId")+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId()+GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage("FinProjectTargetAsset002.finprodName")+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodName()+GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage("FinProjectTargetAsset002.vdate")+GlobalConstant.OPEN_PARENTHESIS+DateUtil.format(entity.getVdate())+GlobalConstant.CLOSE_PARENTHESIS,
            this.getMessage("FinProjectTargetAsset002.mdate")+GlobalConstant.OPEN_PARENTHESIS+DateUtil.format(entity.getMdate())+GlobalConstant.CLOSE_PARENTHESIS));
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinProjectTargetAssetDto> parameter = new ProcessParameter<FinProjectTargetAssetDto>();
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        String msg = finProjectTargetAssetService.start(parameter, dto);
        return new AjaxResult(msg);
    }
    
}
