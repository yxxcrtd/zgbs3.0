package com.joyintech.tams.business.asset.bond.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.file.SafelyFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.*;
import com.joyintech.tams.business.asset.bond.service.FinBondAsset001Service;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset002Dto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.MstBondCreditRating;
import com.joyintech.tams.common.base.entity.SysUploadFileInfo;
import com.joyintech.tams.component.business.asset.BussFinRepaymentService;
import com.joyintech.tams.component.business.asset.FinPayInterestStdInfoService;
import com.joyintech.tams.component.business.asset.dto.BussFinRepaymentDto;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.bond.BusiFinBondRatingInfoService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.business.asset.bond.service.FinBondAssetService;
import com.joyintech.tams.business.asset.bond.service.FinBondAssetValidatorService;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 债券类标的信息新建
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAsset002Controller
 * @since 1.0
 */
@Controller
@PageId("FinBondAsset002")
@RequestMapping("/business/asset/bond/FinBondAsset002")
@Api(tags = "债券标的信息新建")
public class FinBondAsset002Controller extends BaseController{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FinBondAssetService finBondAssetService;

    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    
    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private FinBondAssetValidatorService finBondAssetValidatorService;

    @Autowired
    private FinPayInterestStdInfoService finPayInterestStdInfoService;

    @Autowired
    private BussFinRepaymentService bussFinRepaymentService;

    @Autowired
    private FinBondAsset001Service finBondAsset001Service;

    @ApiOperation(value="新建页面初始化参数", notes = "新建页面初始化参数")
    @PostMapping("/getInitPageParams")
    @ResponseBody
    public Message<Map<String,Object>> getInitPageParams(){
        //初始化基本信息画面数据字典
       Map<String,Object> initPageParams = new HashMap<>();
       initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
       initPageParams.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
       initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种 , null));
       initPageParams.put("TYPE_9", this.getSelectDataFromDataDic(DictCode.行内资产二级分类, null));
       initPageParams.put("TYPE_10", this.getSelectDataFromDataDic(DictCode.行内资产三级分类, null));
       initPageParams.put("TYPE_11", this.getSelectDataFromDataDic(DictCode.行内资产四级分类 , null));
       initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
       initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
       initPageParams.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
       initPageParams.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
       initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
       initPageParams.put("AMT_INCOME_TYPE", this.getSelectDataFromDataDic(DictCode.本金收益特征, null));
       initPageParams.put("CONCEPT_TYPE", this.getSelectDataFromDataDic(DictCode.概念标识, null));
       initPageParams.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
       initPageParams.put("INT_TYPE", this.getSelectDataFromDataDic(DictCode.利率类型, null));
       initPageParams.put("BASE_RATE_ACCURACY", this.getSelectDataFromDataDic(DictCode.基准利率精度, null));
       initPageParams.put("RESET_TYPE", this.getSelectDataFromDataDic(DictCode.重置类型_基准利率调整规则, null));
       initPageParams.put("ADVANCE_OBSERV_UNIT", this.getSelectDataFromDataDic(DictCode.提前观察单位_延后时间单位, null));
       initPageParams.put("INTPAY_RULE", this.getSelectDataFromDataDic(DictCode.利息分配方式, null));
       initPageParams.put("TDAY_RULE", this.getSelectDataFromDataDic(DictCode.营业日准则, null));
       initPageParams.put("FREQUENCY_UNIT", this.getSelectDataFromDataDic(DictCode.频率, null));
       initPageParams.put("CALC_RULE", this.getSelectDataFromDataDic(DictCode.推算方式, null));
       initPageParams.put("BASIS", this.getSelectDataFromDataDic(DictCode.计息基础, null));
       initPageParams.put("EXEC_TYPE", this.getSelectDataFromDataDic(DictCode.行权状态, null));
       initPageParams.put("EXEC_MODE", this.getSelectDataFromDataDic(DictCode.行权方式, null));
       initPageParams.put("ISSUE_CLASS", this.getSelectDataFromDataDic(DictCode.新债_发行方式, null));
       initPageParams.put("TENDER_TYPE", this.getSelectDataFromDataDic(DictCode.招标方式, null));
       initPageParams.put("ISSUE_STYLE", this.getSelectDataFromDataDic(DictCode.发行类型, null));
       initPageParams.put("SECRATE_DATA", this.getSelectDataFromDataDic(DictCode.利率行情, null));
       initPageParams.put("TENDER_TARGET", this.getSelectDataFromDataDic(DictCode.招标标的, null));
       initPageParams.put("GUARANTEE_MODE",this.getSelectDataFromDataDic(DictCode.新债_担保方式, null));

        String finprodId = getParam("finprodId");
       String finprodType = getParam("finprodType");
       String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
       initPageParams.put("YES",DictCodeList.是否_是.getValue());
       initPageParams.put("isTradeCalendar",DictCodeList.是否_是.getValue());
        /* add by zhangpuyu 2019/20/24 start */
       initPageParams.put("counterType",DictCodeListExt1.交易对手类型_分行.getValue());
        /* add by zhangpuyu 2019/20/24 end */
       initPageParams.put("FINPROD_TYPE_TG_F01",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
       initPageParams.put("FINPROD_TYPE_TG_F07", DictCodeListExt1.金融产品类型_投管用_存款.getValue());
       initPageParams.put("FINPROD_TYPE_TG_F17",DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
       initPageParams.put("FINPROD_TYPE_TG_F18",DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
       initPageParams.put("FINPROD_TYPE_TG_F10",DictCodeListExt1.金融产品类型_投管用_股票.getValue());
       initPageParams.put("COUPON_SPECIES_02", DictCodeList.息票品种_贴现.getValue());
       initPageParams.put("COUPON_SPECIES_03", DictCodeList.息票品种_零息.getValue());
       initPageParams.put("CCY_INIT", DictCodeList.币种_人民币_CNY.getValue());
       initPageParams.put("CUSTOM_ASSET_TYPE_FIVE_009", DictCodeListExt2.行内资产五级分类_金融机构次级债.getValue());//wangguiqi	2019/8/16
       initPageParams.put("CUSTOM_ASSET_TYPE_FIVE_158", DictCodeListExt2.行内资产五级分类_政策性金融次级债.getValue());//wangguiqi	2019/8/16
        //平台日
       initPageParams.put("iPlatformDate", DateUtil.parse(DateUtil.getSysDate()));
       initPageParams.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
       initPageParams.put("isShowType", CacheUtil.getSysParam("IS_SHOW_TYPE")==null?"01":CacheUtil.getSysParam("IS_SHOW_TYPE"));
        //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(finprodId)) {
           initPageParams.put("isTransInAsset", commonBusinessService.isTransInAsset(finprodId)!=0?true:false);
           initPageParams.put("isHqTrade", finBondAssetService.isHqTrade(finprodId)>0?true:false);
          //根据主键获取业务实体--标的金融产品基本信息
            FinBondAssetSaveRequest finBondAssetDto = finBondAssetService.getBasicInfos(
                finprodId, finprodType);
            //commonBusinessService.setTradeMarketId(finBondAssetDto,model);
            //点击修改保存提交会校验实际授信主体必填问题
            String a = CacheUtil.getSysParam(BussinessConstant.CEMODEL);
            if(StringUtil.equals("01", a)){
                finBondAssetDto.setCreditFlag("");
            }
           initPageParams.put("entity", finBondAssetDto);
        }//来自于代办
        else if( StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            FinBondAssetSaveRequest finBondAssetDto = finBondAssetService.getBussinessEntityByWorkflowId(workflowId);
            //commonBusinessService.setTradeMarketId(finBondAssetDto,model);
            //设置到model中
           initPageParams.put("entity", finBondAssetDto);
        }else{
            FinBondAssetSaveRequest finBondAssetDto = new FinBondAssetSaveRequest();
            finBondAssetDto.setFinprodId(IDGenerator.generate());
            finBondAssetDto.setIsAutoUpdate(DictCodeList.是否_是.getValue());
//            finBondAssetDto.setBlnArea(DictCodeList.境内外_境内.getValue());
            //commonBusinessService.setTradeMarketId(finBondAssetDto,model);
           initPageParams.put("entity", finBondAssetDto);
        }
        //资产类型
       initPageParams.put("INT_ASSET_TYPE", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
       initPageParams.put("workflowId",workflowId);
       initPageParams.put("pageModel",this.getPageModel());
      //取系统参数
       initPageParams.put("CE_MODEL", CacheUtil.getSysParam(BussinessConstant.CEMODEL));
       initPageParams.put("WINDTYPEONE_B14",DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
       initPageParams.put("WINDTYPEONE_B15",DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());
       initPageParams.put("SVR_SHARES_TYPE_02",DictCodeListExt2.股票类型_优先股.getValue());
       initPageParams.put("SVR_ASSET_TYPE_SECOND_LEVEL", createChbAssetType());
        //add by lizhi 是否显示中债信息开关 2020年2月28日  start
       initPageParams.put("ASSET_INFO_CHB_HIDE", 
                CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE)==null? 
                        DictCodeListExt1.是或否_是.getValue(): CacheUtil.getSysParam(BussinessConstant.ASSET_INFO_CHB_HIDE));
        //add by lizhi 是否显示中债信息开关 2020年2月28日  end
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     *  获取流程中信息
     * @param request
     * @return
     */
    @ApiOperation(value = "获取流程中信息", notes = "获取流程中信息")
    @ResponseBody
    @PostMapping("/getApproveEntity")
    public Message<FinBondAssetSaveRequest> getApproveEntity(@RequestBody FinBondAssetInsertRequest request)
    {
        try{
            String workflowId = request.getWorkflowId();
            FinBondAssetSaveRequest entity = new FinBondAssetSaveRequest();
            if (StringUtil.isNotEmpty(workflowId)) {
                //流程代办checkImportData
                entity = finBondAssetService.getBussinessEntityByWorkflowId(workflowId);
                if (StringUtil.isNotEmpty(entity)){
                    entity.setType9(entity.getTypeTwo());
                    entity.setType10(entity.getTypeThree());
                    entity.setType11(entity.getTypeFour());
                }
            }
            return new Message<FinBondAssetSaveRequest>().success().setData(entity);
        } catch(Exception e){
            e.printStackTrace();
            return new Message<FinBondAssetSaveRequest>().fail().setMessage("查询异常");
        }
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
    @ApiOperation(value = "提交保存check", notes = "提交保存check")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody FinBondAssetSaveRequest dto, BindingResult result) {
        AjaxResult ajax = new AjaxResult();
        List<ValidMessage> validList = new ArrayList<>();
        ValidUtils.valid(dto,validList);
        ValidUtils.valid(dto.getFinProductConvertible(),validList);
        ValidUtils.valid(dto.getFinInterestFactorDto(),validList);
        List<ValidMessage> valids = finBondAssetValidatorService.valid(dto);
        validList.addAll(valids);
        if (!StringUtil.equals(dto.getOperateStatus(), "MODIFY")) {
            String finProdId = commonBusinessService.getFinProdId(dto.getFinprodMarketId() + dto.getTradeMarketId(), DictCodeListExt1.金融产品类型_投管用_债券.getValue(), null);
            if (finBondAssetService.selectCountByfinprodId(finProdId) > 0) {
                validList.add(new ValidMessage("",
                        getMessage("资产代码不能重复!")));
                ajax.setErrorList(validList);
                ajax.setMessageType(MessageType.错误消息);
                return new Message<AjaxResult>().success().setData(ajax);
            }
        }
        if (CollectionUtils.isNotEmpty(validList)){
            ajax.setErrorList(validList);
            return new Message<AjaxResult>().success().setData(ajax);
        }
        return new Message<AjaxResult>().success().setData(commonCheck(result, dto.getOperateType()));
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
    @ApiOperation(value = "保存提交", notes = "保存提交")
    @ResponseBody
    @PostMapping(PAGE_SAVE)
    public Message<AjaxResult> save(@RequestBody FinBondAssetSaveRequest dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
        }
        //生成金融产品代码
        dto.setFinprodMarketId(dto.getFinprodMarketId()+dto.getTradeMarketId());
        if (StringUtil.equals(dto.getOperateStatus(), OperationType.NEW.name())) {
            dto = finBondAssetService.getFinprodId(dto);
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
        WorkFlowParameter<FinBondAssetSaveRequest> params = new WorkFlowParameter<FinBondAssetSaveRequest>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(FinBondAssetSaveRequest::getFinprodMarketId);
        params.setBusinessType(DictCodeList.共通业务类型_债券类资产信息.getValue());
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment("债券代码"+
                GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodMarketId(), GlobalConstant.CLOSE_PARENTHESIS+
                "债券名称"+GlobalConstant.OPEN_PARENTHESIS+
                entity.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS);
        });
        // 给参数变成ProcessParameter而非WorkFlowParameter
        ProcessParameter<FinBondAssetSaveRequest> parameter = new ProcessParameter<FinBondAssetSaveRequest>();
        if (StringUtil.equals(dto.getOperateStatus(), OperationType.NEW.name())) {
            parameter.setOperationType(OperationType.NEW);
        } else if (StringUtil.equals(dto.getOperateStatus(), OperationType.MODIFY.name())) {
            parameter.setOperationType(OperationType.MODIFY);
        }else{
            parameter.setOperationType(OperationType.DELETE);
        }
        parameter.setWorkFlowParameter(params);
        // 系统页面表IS_NEED_WORKFLOW 是否走流程，01-是，02-否
        //封装标的类金融产品基础组件需要的DTO
        String msg = finBondAssetService.start(parameter, dto);
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }


    /**
     * 新建时根据市场代码查询wind接口债券信息
     *
     * @param request 市场代码
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "获取债券基本信息", notes = "获取债券基本信息")
    @ResponseBody
    @PostMapping("/getBasicInfo")
    public Message<FinBondAssetSaveRequest> getBasicInfo(@RequestBody FinBondAssetInsertRequest request){
        String finprodId = null;
        if (StringUtil.isNotEmpty(request.getFinprodId())){
            finprodId = request.getFinprodId();
        }else{
            finprodId = request.getFinprodMarketId();
        }
        FinBondAssetSaveRequest resultDto = finBondAssetService.getBasicInfos(
                finprodId,null);
        return new Message<FinBondAssetSaveRequest>().success().setData(resultDto);
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
    @ApiOperation(value = "获取市场投标债券代码", notes = "获取市场投标债券代码")
    @ResponseBody
    @PostMapping("/getfinprodIdForAdd")
    public Message<Page<SelectOptionDto>> getfinprodIdForAdd(@RequestBody SelectOptionCommonRequest request) {
        Page<SelectOptionDto> page = finBondAssetService.getfinprodIdForAdd(request);
        return new Message<Page<SelectOptionDto>>().success().setData(page);
    }


    /**
     * 是否纳入净额结算
     */
    @ApiOperation(value = "获取是否纳入净额结算", notes = "获取是否纳入净额结算")
    @ResponseBody
    @PostMapping("/getIsNetSett")
    private Message<AjaxResult> getIsNetSett(@RequestBody FinBondAssetInsertRequest request) {
        Map<String,String> map=new HashMap<String,String> ();
        AjaxResult ar = new AjaxResult();
        String finprodMarketId = request.getFinprodMarketId();
        String tradeMarket = request.getTradeMarket();
        String type9 = request.getType10();
        String isPpb = request.getIsPpb();
        String isNetSett=StringUtil.EMPTY;
        String tradePlat=StringUtil.EMPTY;
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
                if ((l >= 139000 && l <= 139999) || (l >= 123000 && l <= 123499)) {
                    settleFlag = true;
                }
            }
            //全额结算 : 私募债 || 139000-139999代码段债券、123000-123499代码段债券 || 资产支持证券
            if(StringUtil.equals(isPpb, "Y") || settleFlag || StringUtil.equals(DictCodeListExt3.行内资产三级分类_资产支持证券.getValue(), type9)){
                isNetSett="Y";
//                tradePlat=DictCodeList.交易平台_固定收益平台.getValue();
            }else{
                //以上情况都不符合则为纳入净额结算标准范围的债券
                isNetSett="N";
//                tradePlat=DictCodeList.交易平台_固定收益平台.getValue()+","+DictCodeList.交易平台_竞价撮合平台.getValue()+","+DictCodeList.交易平台_大宗交易平台.getValue();
            }
        }else if(StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), tradeMarket)){
            //全额结算: 私募债 || 资产支持证券
            if(StringUtil.equals(isPpb, "Y") || StringUtil.equals(DictCodeListExt3.行内资产三级分类_资产支持证券.getValue(), type9)){
                isNetSett="Y";
//                tradePlat=DictCodeList.交易平台_综合协议交易平台.getValue();
            }else{
                isNetSett="N";
//                tradePlat=DictCodeList.交易平台_竞价撮合平台.getValue()+","+DictCodeList.交易平台_综合协议交易平台.getValue();
            }
        }
        map.put("isNetSett", isNetSett);
        ar.setData (map);
        return new Message<AjaxResult>().success().setData(ar);
    }

    /**
     * add by xxg 20210721
     * 根据产品代码得到起息日和到期日
     */
    @ApiOperation(value = "根据产品代码得到起息日和到期日", notes = "根据产品代码得到起息日和到期日")
    @ResponseBody
    @PostMapping("/isSameMdateAndVdate")
    public Message<Boolean> isSameMdateAndVdate(@RequestBody FinBondAssetInsertRequest request){
        //债券标的   债券修改或新建，关联一级市场债券时，若关联的一级债券起息日、到期日与资产起息日、到期日不一致，需要进行提醒
        FinBondAssetSaveRequest dto = finBondAssetService.getMdateAndVdate(request.getoFinprodId());
        if (null != dto.getVdate() && null != dto.getMdate()) {
            if (DateUtil.compareDate(dto.getMdate(), request.getMdate()) != 0
                    || DateUtil.compareDate(dto.getVdate(), request.getVdate()) != 0) {
                return new Message<Boolean>().success().setData(true);
            }
        }
        return new Message<Boolean>().success().setData(false);
    }

    /**
     *
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "计算初始利率和", notes = "计算初始利率")
    @ResponseBody
    @PostMapping("/getyieldByPriceCalculator")
    public Message<AssetCommonInfoRequest> getyieldByPriceCalculator(@RequestBody AssetCommonInfoRequest request) {
        AssetCommonInfoRequest response = new AssetCommonInfoRequest();
        request.setCalendarId(finBondAssetService.getCalendarId(request.getTradeMarket()));
        if (StringUtil.isNotEmpty(request.getPriceFlag()) && StringUtil.equals(request.getPriceFlag(),DictCodeList.是否_是.getValue())) {
            BigDecimal rate = finBondAssetService.getyieldByPriceCalculator(request);
            response.setRate(rate);
        }else if (StringUtil.isNotEmpty(request.getRateFlag()) && StringUtil.equals(request.getRateFlag(),DictCodeList.是否_是.getValue())) {
            BigDecimal issuePrice = finBondAssetService.getPriceByYieldCalculator(request);
            response.setIssuePrice(issuePrice);
        }
        return new Message<AssetCommonInfoRequest>().success().setData(response);
    }

    /**
     *
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "发行人新建按钮check(参数只传发行人代码)", notes = "发行人新建按钮check(参数只传发行人代码)")
    @ResponseBody
    @PostMapping("/issuecheck")
    public Message<AjaxResult> issuecheck(@RequestBody FinBondAssetRequest request) {
        String issuer = request.getIssuer();
        AjaxResult ajax = new AjaxResult();
        if (!StringUtil.isNotEmpty(issuer)) {
            ajax = finBondAssetService.issuecheck(issuer);
        }
        return new Message<AjaxResult>().success().setData(ajax);
    }

    /**
     * 生成付息计划check <功能详细描述>
     *
     * @param previewParam
     * @param result
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "生成付息计划check", notes = "生成付息计划check")
    @ResponseBody
    @PostMapping("/checkGenerationPlan")
    public Message<AjaxResult> checkGenerationPlan(@RequestBody BondFinPayInterestStdInfoRequest previewParam,
                            BindingResult result) {
        if (result != null && result.hasErrors()) {
            return Message.success("生成付息计划check",new AjaxResult());
        }
        AjaxResult resultList=new AjaxResult(result);
        List<ValidMessage> validMessages = finBondAssetService.checkGenerationPlan(previewParam);
        validMessages.addAll(finBondAssetService.valid(previewParam));
        resultList.setErrorList(validMessages);
        return Message.success("生成付息计划check",resultList);
    }

    /**
     * 生成付息计划 <功能详细描述> 说明:计划列表有票面利率 所以返回FinCashPlanDto,不用FinCashPlan
     *
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "生成付息计划", notes = "生成付息计划")
    @ResponseBody
    @PostMapping(value = "/generationPlan")
    public Message<BondFinPayInterestStdInfoResponse> generationPlan(@RequestBody BondFinPayInterestStdInfoRequest dto){
        try {
            // 修改查看初始化
            String finprodId = dto.getFinprodId();
            if(!StringUtil.isNotEmpty(finprodId) && StringUtil.isNotEmpty(dto.getFinprodMarketId())) {
                finprodId = commonBusinessService.getFinProdId(dto.getFinprodMarketId(),DictCodeListExt1.金融产品类型_投管用_债券.getValue(),null);
            }
            if (StringUtil.isNotEmpty(dto.getFinInterestFactorDto()) &&
                    StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getYield()) ){
                dto.getFinInterestFactorDto().setYield(ArithmeticUtil.multiply(dto.getFinInterestFactorDto().getYield(),BigDecimal.valueOf(100)));
            }
            // 付息计划
            List<BondFinCashPlanDto> invList = new ArrayList<>();
            Date maxTradeDate = null;
            if(StringUtil.isNotEmpty(finprodId)) {
                maxTradeDate = finBondAssetService.getMaxTradeDate(finprodId);
                // 生付息成计划
                dto.setMaxTradeDate(maxTradeDate);
            }
            dto.setCalendarId(finBondAssetService.getCalendarId(dto.getTradeMarket()));
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            if(StringUtil.equals(dto.getAdjPlanFlag(), DictCodeList.共通业务类型_付息计划调整.getValue())){
                dto.setFinprodType2(dto.getAdjPlanFlag());
            }
            FinPayInterestStdInfoDto finPayInterestStdInfoDto= new FinPayInterestStdInfoDto();
            BeanUtil.copyPropertiesIgnoreNull(finPayInterestStdInfoDto,dto);
            BondFinInterestFactorDto bondFinInterestFactorDto = dto.getFinInterestFactorDto();
            FinInterestFactorDto finInterestFactorDto = new FinInterestFactorDto();
            finInterestFactorDto.setRate(bondFinInterestFactorDto.getYield());
            BeanUtil.copyPropertiesIgnoreNull(finInterestFactorDto,bondFinInterestFactorDto);
            finPayInterestStdInfoDto.setFinInterestFactorDto(finInterestFactorDto);
            if (CollectionUtils.isNotEmpty(dto.getInvScheduleList())) {
                List<FinCashPlanDto> invScheduleList = new ArrayList<>();
                dto.getInvScheduleList().forEach(bondInvSchedule -> {
                    FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                    BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto, bondInvSchedule);
                    invScheduleList.add(finCashPlanDto);
                });
                finPayInterestStdInfoDto.setInvScheduleList(invScheduleList);
            }
            if (CollectionUtils.isNotEmpty(dto.getInvRepayList())) {
                List<FinCashPlanDto> invRepayList = new ArrayList<>();
                dto.getInvRepayList().forEach(item -> {
                    FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                    BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto, item);
                    invRepayList.add(finCashPlanDto);
                });
                finPayInterestStdInfoDto.setInvRepayList(invRepayList);
            }
            List<FinCashPlanDto> finCashPlanList = finPayInterestStdInfoService.createAssetPayInteresPlan(finPayInterestStdInfoDto);
            for (FinCashPlanDto finCashPlan:finCashPlanList){
                BondFinCashPlanDto bondFinCashPlanDto = new BondFinCashPlanDto();
                BeanUtil.copyPropertiesIgnoreNull(bondFinCashPlanDto,finCashPlan);
                bondFinCashPlanDto.setRangeYield(ArithmeticUtil.divide(bondFinCashPlanDto.getRangeYield(),BigDecimal.valueOf(100),4));
                bondFinCashPlanDto.setLirs(null);
                invList.add(bondFinCashPlanDto);
            }
            //还本计划
            BussFinRepaymentDto bussFinRepaymentDto = new BussFinRepaymentDto();
            BeanUtil.copyPropertiesIgnoreNull(bussFinRepaymentDto, finPayInterestStdInfoDto);
            finPayInterestStdInfoDto.setEndTdayRule(DictCodeList.营业日准则_不调整.getValue());
            bussFinRepaymentDto.setFinPayInterestStdInfoDto(finPayInterestStdInfoDto);
            List<FinCashPlanDto> finCashPlans = bussFinRepaymentService.createAssetRePaymentPlan(bussFinRepaymentDto);
            List<BondFinCashPlanDto> lirs = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(finCashPlans)) {
                for (FinCashPlanDto lirsEntity : finCashPlans) {
                    BondFinCashPlanDto lirsDto = new BondFinCashPlanDto();
                    BeanUtil.copyPropertiesIgnoreNull(lirsDto, lirsEntity);
                    lirsDto.setCashAmt(lirsEntity.getCashAmt());
                    lirsDto.setLirs(null);
                    lirs.add(lirsDto);
                }
            }
            //排序
            Collections.sort(invList, new Comparator<BondFinCashPlanDto>() {
                @Override
                public int compare(BondFinCashPlanDto o1, BondFinCashPlanDto o2) {
                    return o1.getVdate().compareTo(o2.getVdate());
                }
            });
            Collections.sort(lirs, new Comparator<BondFinCashPlanDto>() {
                @Override
                public int compare(BondFinCashPlanDto o1, BondFinCashPlanDto o2) {
                    return o1.getVdate().compareTo(o2.getVdate());
                }
            });
            BondFinPayInterestStdInfoResponse  response = new BondFinPayInterestStdInfoResponse();
            response.setInvScheduleList(invList);
            response.setInvRepayList(lirs);
            response.setMaxTradeDate(maxTradeDate);
            return new Message<BondFinPayInterestStdInfoResponse>().success().setData(response);
        }catch (Exception e){
            e.printStackTrace();
            return new Message<BondFinPayInterestStdInfoResponse>().fail().setMessage("付息计划生成异常");
        }
    }

    /**
     * 附件上传
     * @param file  入参
     * @return 处理结果
     * @throws IOException 异常信息
     */
    @ApiOperation(value = "附件上传", notes = "附件上传")
    @ResponseBody
    @PostMapping( "/uploadFile")
    public Message<SysUploadFileInfo> uploadFile(MultipartFile file) {
        try {
            logger.info("文件上传开始");
            if (StringUtil.isNotEmpty(file)) {
                SysUploadFileInfo fileInfo = finBondAssetService.getUploadFileInfo(file);
                return new Message<SysUploadFileInfo>().success().setData(fileInfo);
            }
            logger.info("文件上传结束");
        }catch (Exception e){
            logger.error("文件上传异常", e);
            return new Message<SysUploadFileInfo>().fail().setMessage("文件上传异常");
        }
        return null;
    }

    /**
     * 附件删除
     * @param file  入参
     * @return 处理结果
     * @throws IOException 异常信息
     */
    @ApiOperation(value = "附件删除", notes = "附件删除")
    @ResponseBody
    @PostMapping( "/deleteFile")
    public Message<AjaxResult> UploadFile(SysUploadFileInfo file) {
        try {
            if (StringUtil.isNotEmpty(file)) {
                AjaxResult ajaxResult = finBondAssetService.delFileInfo(file);
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Message<AjaxResult>().fail().setMessage("文件删除异常");
        }
        return null;
    }


    @ApiOperation(value="正股代码的联想下拉框",notes = "正股代码的联想下拉框")
    @ResponseBody
    @PostMapping("getFinprodMarketIdList")
    public Message<Page<SelectOptionDto>> getFinprodMarketIdList(@RequestBody SelectOptionCommonRequest request){
        try {
            Page<SelectOptionDto> page = finBondAsset001Service.getFinprodMarketIdList(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            return new Message<Page<SelectOptionDto>>().fail().setMessage("正股代码的联想下拉框异常");
        }
    }

    @ApiOperation(value="节假日自动向后推算日期(参数：)",notes = "节假日自动向后推算日期")
    @ResponseBody
    @PostMapping("getDealCalendarHoliday")
    public Message<Date> getDealCalendarHoliday(@RequestBody SelectOptionCommonRequest request){
        try {
            Date date = finBondAssetService.getDealCalendarHoliday(request);
            return new Message<Date>().success().setData(date);
        }catch (Exception e) {
            return new Message<Date>().fail().setMessage("正股代码的联想下拉框异常");
        }
    }

    /**
     * 查看区间付息计划
     * <功能详细描述>
     * @param dto
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查看区间付息计划",notes = "查看区间付息计划")
    @ResponseBody
    @PostMapping("generationSubPlan")
    public Message<List<BondFinCashPlanDto>> generationSubPlan(@RequestBody BondFinPayInterestStdInfoRequest dto) {
        try {
            String finprodId = dto.getFinprodId();
            if(!StringUtil.isNotEmpty(finprodId) && StringUtil.isNotEmpty(dto.getFinprodMarketId())) {
                finprodId = commonBusinessService.getFinProdId(dto.getFinprodMarketId(),DictCodeListExt1.金融产品类型_投管用_债券.getValue(),null);
            }
            if (StringUtil.isNotEmpty(dto.getFinInterestFactorDto()) &&
                    StringUtil.isNotEmpty(dto.getFinInterestFactorDto().getYield()) ){
                dto.getFinInterestFactorDto().setYield(ArithmeticUtil.multiply(dto.getFinInterestFactorDto().getYield(),BigDecimal.valueOf(100)));
            }
            // 付息计划
            List<BondFinCashPlanDto> invList = new ArrayList<>();
            Date maxTradeDate = null;
            if(StringUtil.isNotEmpty(finprodId)) {
                maxTradeDate = finBondAssetService.getMaxTradeDate(finprodId);
                // 生付息成计划
                dto.setMaxTradeDate(maxTradeDate);
            }
            dto.setCalendarId(finBondAssetService.getCalendarId(dto.getTradeMarket()));
            dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            if(StringUtil.equals(dto.getAdjPlanFlag(), DictCodeList.共通业务类型_付息计划调整.getValue())){
                dto.setFinprodType2(dto.getAdjPlanFlag());
            }
            FinPayInterestStdInfoDto finPayInterestStdInfoDto= new FinPayInterestStdInfoDto();
            BeanUtil.copyPropertiesIgnoreNull(finPayInterestStdInfoDto,dto);
            BondFinInterestFactorDto bondFinInterestFactorDto = dto.getFinInterestFactorDto();
            FinInterestFactorDto finInterestFactorDto = new FinInterestFactorDto();
            finInterestFactorDto.setRate(bondFinInterestFactorDto.getYield());
            BeanUtil.copyPropertiesIgnoreNull(finInterestFactorDto,bondFinInterestFactorDto);
            finPayInterestStdInfoDto.setFinInterestFactorDto(finInterestFactorDto);
            if (CollectionUtils.isNotEmpty(dto.getInvScheduleList())) {
                List<FinCashPlanDto> invScheduleList = new ArrayList<>();
                dto.getInvScheduleList().forEach(bondInvSchedule -> {
                    FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                    BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto, bondInvSchedule);
                    invScheduleList.add(finCashPlanDto);
                });
                finPayInterestStdInfoDto.setInvScheduleList(invScheduleList);
            }
            if (CollectionUtils.isNotEmpty(dto.getInvRepayList())) {
                List<FinCashPlanDto> invRepayList = new ArrayList<>();
                dto.getInvRepayList().forEach(item -> {
                    FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                    BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto, item);
                    invRepayList.add(finCashPlanDto);
                });
                finPayInterestStdInfoDto.setInvRepayList(invRepayList);
            }
            List<FinCashPlanDto> finCashPlanList = finPayInterestStdInfoService.createAssetPayInteresPlan(finPayInterestStdInfoDto);
            for (FinCashPlanDto finCashPlan:finCashPlanList){
                BondFinCashPlanDto bondFinCashPlanDto = new BondFinCashPlanDto();
                BeanUtil.copyPropertiesIgnoreNull(bondFinCashPlanDto,finCashPlan);
                bondFinCashPlanDto.setRangeYield(ArithmeticUtil.divide(bondFinCashPlanDto.getRangeYield(),BigDecimal.valueOf(100),4));
                bondFinCashPlanDto.setLirs(null);
                invList.add(bondFinCashPlanDto);
            }
            return new Message<List<BondFinCashPlanDto>>().success().setData(invList);
        }catch (Exception e) {
            return new Message<List<BondFinCashPlanDto>>().fail().setMessage("查看区间付息计划异常");
        }

    }
}
