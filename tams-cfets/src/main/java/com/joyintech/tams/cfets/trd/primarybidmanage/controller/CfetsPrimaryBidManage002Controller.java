package com.joyintech.tams.cfets.trd.primarybidmanage.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.*;
import com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManage001Service;
import com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManageService;
import com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManageValidatorService;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 债券类标的信息新建
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see CfetsPrimaryBidManage002Controller
 * @since 1.0
 */
@Controller
@PageId("CfetsPrimaryBidManage002")
@RequestMapping("/cfets/trd/primarybidmanage/CfetsPrimaryBidManage002")
@Api(tags = "中标结果维护新建")
public class CfetsPrimaryBidManage002Controller extends BaseController{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CfetsPrimaryBidManageService cfetsPrimaryBidManageService;


    @Autowired
    private CfetsPrimaryBidManage001Service cfetsPrimaryBidManage001Service;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private CfetsPrimaryBidManageValidatorService CfetsPrimaryBidManageValidatorService;

    @Autowired
    private FinProductMapper finProductMapper;

    @ApiOperation(value="新建页面初始化参数", notes = "新建页面初始化参数")
    @PostMapping("/getInitPageParams")
    @ResponseBody
    public Message<Map<String,Object>> getInitPageParams(){
        //初始化基本信息画面数据字典
       Map<String,Object> initPageParams = new HashMap<>();
       initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
       initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
       initPageParams.put("BID_TYPE", this.getSelectDataFromDataDic(DictCode.中标方式, null));
       initPageParams.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));
       initPageParams.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
       initPageParams.put("pageModel",this.getPageModel());
       initPageParams.put("BID_TYPE_01", DictCodeListExt4.中标方式_单标位中标.getValue());
       String isModifiedIntamt = CacheUtil.getSysParam("IS_MODIFIED_INTAMT");
       initPageParams.put("IS_MODIFIED_INTAMT",isModifiedIntamt);
       initPageParams.put("iPlatformDate",iPlatformDate.getSysDateStr());
       initPageParams.put("COUPON_SPECIES_01",DictCodeList.息票品种_附息.getValue());
       initPageParams.put("COUPON_SPECIES_04",DictCodeList.息票品种_利随本清.getValue());
       //取系统参数
       initPageParams.put("BID_MIN_UNIT", CacheUtil.getSysParam("BID_MIN_UNIT"));
       return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     * 本方交易员联想文本框初始化。
     *
     * @return 联想文本框
     */
    @ApiOperation(value = "查询本方交易员", notes = "查询本方交易员")
    @ResponseBody
    @PostMapping("selectTrader")
    public Message<Page<SelectOptionDto>> selectTrader(@RequestBody SelectOptionCommonRequest request){
        try {
            Page<SelectOptionDto> page = cfetsPrimaryBidManage001Service.selectTrader(request);
            return new Message<Page<SelectOptionDto>>().success().setData(page);
        }catch (Exception e) {
            logger.error("查询本方交易员数据异常",e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询本方交易员下拉框异常");
        }
    }


    /**
     * 查询投标确认信息
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询投标确认信息",notes = "查询投标确认信息")
    @ResponseBody
    @PostMapping("getBidManageCfmInsert")
    public Message<CfetsPrimaryBidManageSaveRequest> getBidManageCfmInsert(@RequestBody CfetsPrimaryBidManageCfmRequest request) {
        try {
            CfetsPrimaryBidManageSaveRequest response = cfetsPrimaryBidManageService.getBidManageCfmInsert(request);
            return new Message<CfetsPrimaryBidManageSaveRequest>().success().setData(response);
        }catch (Exception e) {
            logger.error("查询投标确认信息数据异常",e);
            return new Message<CfetsPrimaryBidManageSaveRequest>().fail().setMessage("查询投标确认信息异常");
        }
    }

    /**
     * 查询中标结果维护信息
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询中标结果维护信息(含代办)",notes = "查询中标结果维护信息(含代办)")
    @ResponseBody
    @PostMapping("selectBasicInfo")
    public Message<CfetsPrimaryBidManageSaveRequest> selectBasicInfo(@RequestBody CfetsPrimaryBidManageSaveRequest request) {
        try {
            String workflowId = request.getWorkflowId();
            CfetsPrimaryBidManageSaveRequest response = new CfetsPrimaryBidManageSaveRequest();
            if (StringUtil.isNotEmpty(workflowId)) {
                response = cfetsPrimaryBidManageService.getBussinessEntityByWorkflowId(workflowId);
            }else if (StringUtil.isNotEmpty(request.getTotalWinId())){
                response = cfetsPrimaryBidManageService.selectBasicInfo(request);
            }
            return new Message<CfetsPrimaryBidManageSaveRequest>().success().setData(response);
        }catch (Exception e) {
            logger.error("查询投标确认信息数据异常",e);
            return new Message<CfetsPrimaryBidManageSaveRequest>().fail().setMessage("查询投标确认信息异常");
        }
    }

    /**
     * 查询投标确认信息
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value="分配明细联动处理",notes = "分配明细联动处理")
    @ResponseBody
    @PostMapping("autoAllocation")
    public Message<CfetsPrimaryBidManageSaveRequest> autoAllocation(@RequestBody CfetsPrimaryBidManageSaveRequest request) {
        try {
            CfetsPrimaryBidManageSaveRequest response = cfetsPrimaryBidManageService.autoAllocation(request);
            return new Message<CfetsPrimaryBidManageSaveRequest>().success().setData(response);
        }catch (Exception e) {
            logger.error("查询投标确认信息数据异常",e);
            return new Message<CfetsPrimaryBidManageSaveRequest>().fail().setMessage("查询投标确认信息异常");
        }
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
    public Message<AjaxResult> check(@RequestBody CfetsPrimaryBidManageSaveRequest dto, BindingResult result) {
        AjaxResult ajax = new AjaxResult();
        List<ValidMessage> validList = new ArrayList<>();
        ValidUtils.valid(dto,validList);
        ValidUtils.valid(dto.getAllocationDetails(),validList);
        List<ValidMessage> valids = CfetsPrimaryBidManageValidatorService.valid(dto);
        validList.addAll(valids);
        if (CollectionUtils.isNotEmpty(validList)){
            ajax.setErrorList(validList);
            return new Message<AjaxResult>().success().setData(ajax);
        }
        return new Message<AjaxResult>().success().setData(commonCheck(result, dto.getOperateType()));
    }
    
    /**
     * 
     * 保存提交
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
    public Message<AjaxResult> save(@RequestBody CfetsPrimaryBidManageSaveRequest dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
        }
        //生成中标结果编号
        if (StringUtil.equals(dto.getOperateStatus(), OperationType.NEW.name())) {
            dto.setTotalWinId(IDGenerator.generate());
        }
        dto.setIsWin(DictCodeList.是否_是.getValue());
        WorkFlowParameter<CfetsPrimaryBidManageSaveRequest> params = new WorkFlowParameter<CfetsPrimaryBidManageSaveRequest>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(CfetsPrimaryBidManageSaveRequest::getBidId);
        if (StringUtil.equals(dto.getBidModle(),DictCodeListExt4.投标方式_自主投标.getValue())) {
            params.setBusinessType(DictCodeListExt4.共通业务类型_中标结果维护自主投标.getValue());
        }else{
            params.setBusinessType(DictCodeListExt4.共通业务类型_中标结果维护代投代缴.getValue());
        }
        // 设置流程业务说明
        params.setComment(entity -> {
            return WorkFlowUtil.concatComment("中标日期"+
                    GlobalConstant.OPEN_PARENTHESIS+ iPlatformDate.getSysDateStr() +GlobalConstant.CLOSE_PARENTHESIS+
                    "资产简称"+GlobalConstant.OPEN_PARENTHESIS+entity.getFinprodAbbr()+GlobalConstant.CLOSE_PARENTHESIS
                    +"中标方式"+GlobalConstant.OPEN_PARENTHESIS+dto.getAllocationDetails().get(0).getBidTypeStr()+GlobalConstant.CLOSE_PARENTHESIS
                    +"中标标位"+GlobalConstant.OPEN_PARENTHESIS+ArithmeticUtil.multiply(dto.getAllocationDetails().get(0).getBidPrice(),BigDecimal.valueOf(100))+"%"+GlobalConstant.CLOSE_PARENTHESIS
                    +"中标券面总额"+GlobalConstant.OPEN_PARENTHESIS+ ArithmeticUtil.divide(dto.getWinPrinamt(),BigDecimal.valueOf(10000),2)+"万元"+GlobalConstant.CLOSE_PARENTHESIS);
        });
        ProcessParameter<CfetsPrimaryBidManageSaveRequest> parameter = new ProcessParameter<CfetsPrimaryBidManageSaveRequest>();
        parameter.setOperationType(OperationType.NEW);
        parameter.setWorkFlowParameter(params);
        String msg = cfetsPrimaryBidManageService.start(parameter, dto);
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }

    /**
     * 净价、行权收益率、到期收益率
     * <功能详细描述>
     *
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/calData")
    @ApiOperation(value = "新建指令净价全价收益率计算功能", notes = "新建指令净价全价收益率计算功能")
    @ResponseBody
    public Message<AjaxResult> calData(@RequestBody CfetsPrimaryBidManageSaveRequest dto) {
        try {
            AjaxResult result = new AjaxResult();
            cfetsPrimaryBidManageService.calcAmt(dto);
            result.setData(dto);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("计算功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("计算异常");
        }

    }

    @PostMapping("/calAmountData")
    @ApiOperation(value = "新建指令金额计算功能", notes = "新建指令金额计算功能")
    @ResponseBody
    public Message<AjaxResult> calAmountData(@RequestBody CfetsBidManageCalcAmountRequest dto) {
        try {
            AjaxResult result = new AjaxResult();
            CfetsBidManageCalcAmountResponse res=cfetsPrimaryBidManageService.calAmountData(dto);
            result.setData(res);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("新建指令金额计算功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("新建指令金额计算功能异常");
        }
    }

    /**
     * 根据画面的信息计算价格和到期收益率。<br/>
     * 如果以净价为基准，则计算全价和到期收益率<br/>
     * 如果以全价为基准，则计算净价和到期收益率<br/>
     * 如果以到期收益率为基准，则计算净价和全价
     *
     * @param dto
     * @return 计算结果
     * @author dubaojun
     */
    @ApiOperation(value = "到期收益率为基准计算净价", notes = "到期收益率为基准计算净价")
    @ResponseBody
    @PostMapping("/calcPriceAndYield")
    public Message<CalcPriceAndYieldResponse> calcPriceAndYield(@RequestBody CalcPriceAndYieldRequest dto ){
        try{
            dto.setPs(DictCodeList.交易方向_买入.getValue());
            dto.setCalculateField("03");
            if (StringUtil.isNotEmpty(dto.getFinprodId())){
                FinProduct finProduct = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
                if (!StringUtil.isNotEmpty(finProduct.getCirculationCode())){
                     return new Message<CalcPriceAndYieldResponse>().success().setData(null);
                }
               if (StringUtil.equals( finProduct.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())){
                  dto.setFinprodId("F01_"+finProduct.getCirculationCode());
               }
            }
            Map<String, Object> param = BeanUtil.object2Map(dto);
            Map<String, BigDecimal> returnMap=cfetsPrimaryBidManageService.calcPriceAndYield(param);
            Map<String, Object> returnObjMap=new HashMap<>();
            returnMap.forEach((key, value) -> {
                returnObjMap.put(key,value);
            });
            CalcPriceAndYieldResponse response=BeanUtil.mapToObject(returnObjMap,CalcPriceAndYieldResponse.class);
            return new Message<CalcPriceAndYieldResponse>().success().setData(response);
        }catch (Exception e){
            e.printStackTrace();
            return new Message<CalcPriceAndYieldResponse>().fail().setData(null);
        }
    }

    @ApiOperation(value = "获取到期收益率", notes = "获取到期收益率")
    @ResponseBody
    @PostMapping("/getYield")
    public Message<BigDecimal> getYield(@RequestBody CfetsYieldRequest dto ){
        try{
            BigDecimal response=cfetsPrimaryBidManageService.getYield(dto);
            return new Message<BigDecimal>().success().setData(response);
        }catch (Exception e){
            e.printStackTrace();
            return new Message<BigDecimal>().fail().setData(null);
        }
    }
}
