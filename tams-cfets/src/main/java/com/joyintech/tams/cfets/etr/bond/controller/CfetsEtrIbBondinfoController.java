/*
 * 文件名：EtrBondFirstBiddingController.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yangjm
 * 修改时间：2019年5月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.etr.bond.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.service.trade.investtrade.commanddispatcher.service.TrdCommandDispatcher001Service;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsBussCombDistribtBaseDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrFinProductDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondPosiInfoRequestDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondPosiPage001RequestDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondPosinfo001Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbBondinfoDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbfindEtrinfoRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbfindEtrinfoResponse;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrInvestBondInfoReqDto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfo002Service;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtribBondInfo003Service;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrinvestbondinfo.service.EtrInvestBondInfoService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 银行间债券指令
 * 
 * @author caiqun
 * @version 1.0 2021年10月14日
 * @see CfetsEtrIbBondinfoController
 * @since 1.0
 */
@Controller
@PageId("CfetsEtrBondInfo")
@RequestMapping("/cfets/etrib/cfetsEtrIbBondinfo")
@Api(tags = "银行间债券指令")
public class CfetsEtrIbBondinfoController  extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrIbBondinfoController.class);
    /**
     * 业务编号组件
     */
    @Autowired
    private BaseGetBusinessId bussGetBussinessIdService;
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private CfetsEtrIbBondinfo002Service cfetsEtrIbBondinfo002Service;
    @Autowired
    private CfetsEtribBondInfo003Service cfetsEtrIbBondinfo003Service;
    @Autowired
    private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;
    /**
     * 债券投资委托service
     */
    @Autowired
    private EtrInvestBondInfoService etrInvestBondInfoService;
    /**
     * 委托共通
     */
    @Autowired
    private EtrCommonService etrCommonService;
    /**
     * 指令分发service注入
     */
    @Autowired
    private TrdCommandDispatcher001Service trdCommandDispatcher001Service;
    /**
     * 投资委托信息Service
     */
    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;
    /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;
    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 卖空模式
        initPageParams.put("SHORT_SELLING_SUPPORTED", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        //本币业务指令画面头寸信息区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrIbBondinfo_positionTable_refresh_period", CacheUtil.getSysParam("cfetsEtrIbBondinfo_positionTable_refresh_period"));
        //本币业务指令画面指令列表区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrIbBondinfo_etrTable_refresh_period", CacheUtil.getSysParam("cfetsEtrIbBondinfo_etrTable_refresh_period"));

        //CMDS地址
        initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
        initPageParams.put("NEED_WORKFLOW", true);
        initPageParams.put("SHARES_TRADE_MARKET_02",DictCodeListExt1.交易市场_二级市场.getValue());
        initPageParams.put("platDate",DateUtil.platDateFromCache());
        //风险
        initPageParams.put("riskMathSwitch", CacheUtil.getSysParam("risk_math_switch"));
        initPageParams.put("pageRedisFlag", CacheUtil.getSysParam("pageRedisFlag"));
        initPageParams.put("isRiskmathSoftcontrol", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));
        //数据字典初始化数据
        initPageParams.put("FINPROD_TYPE_TG", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        initPageParams.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));
        initPageParams.put("PRICE_MODEL", this.getSelectDataFromDataDic(DictCode.价格模式, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        //pageId
        initPageParams.put("pageId", "CfetsEtrIbBondinfo003");
        return new Message<Map<String, Object>>(initPageParams).success();
    }
    @ApiOperation(value = "查询指令管理分页数据", notes = "查询指令管理分页数据")
    @PostMapping("/findEtrinfoPage")
    @ResponseBody
    public Message<Page<CfetsEtrIbfindEtrinfoResponse>> findEtrinfoPage(@RequestBody CfetsEtrIbfindEtrinfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrIbfindEtrinfoResponse> list = cfetsEtrIbBondinfoService.findEtrinfoPage(dto);
            return new Message<Page<CfetsEtrIbfindEtrinfoResponse>>().success().setData(list);
        }catch (Exception e) {
            logger.error("查询指令管理分页数据异常",e);
            return new Message<Page<CfetsEtrIbfindEtrinfoResponse>>().fail().setMessage("查询指令管理分页数据异常");
        }

    }
    @ResponseBody
    @ApiOperation(value = "多组合指令新建保存/提交校验", notes = "多组合指令新建保存/提交校验")
    @PostMapping("checkSaveError")
    public Message<AjaxResult> checkSaveError(@RequestBody CfetsEtribSaveRequest cfetsDto, BindingResult br)
            throws Exception {
        try {
            AjaxResult ajax= cfetsEtrIbBondinfoService.checkSaveErrorMethod(cfetsDto, br);
            if(null!=ajax){
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                return new Message<AjaxResult>().success();
            }
        }catch (Exception e) {
            logger.error("多组合指令新建保存/提交校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }

    @ResponseBody
    @ApiOperation(value = "多组合指令新建保存/提交", notes = "多组合指令新建保存/提交")
    @PostMapping("save")
    public Message<AjaxResult> save(@RequestBody CfetsEtribSaveRequest cfetsDto, BindingResult br)
            throws Exception {
        try {
            CfetsEtribBondInfo003Dto allDto = cfetsEtrIbBondinfoService.packageEquity(cfetsDto);
            // ----------add by zhangrui1 20200715 预占功能 start----------
            List<CfetsEtribBondInfo003Dto> dataList = new ArrayList<>();
            // ----------add by zhangrui1 20200715 预占功能 end----------
            for (CfetsEtribBondInfo003Dto dto : allDto.getEtrInvestBondInfoDtos()) {
                if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
                    BussinessCodeParameter param = new BussinessCodeParameter();
                    param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
                    String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                    dto.setEntrustId(code);
                }
                //默认证券账户
//                if (StringUtil.isNotEmpty(dto.getPortfolioId())) {
//                    String acctId = EtrCommonCashPositionDealService.select组合托管户(dto.getPortfolioId(), dto.getCcy(), DictCodeListExt1.资金账户类型_产品托管户.getValue());
//                    dto.setSecAcctId(acctId);
//                }
                if (StringUtil.isNotEmpty(dto.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("portfolioId", dto.getPortfolioId());
                    map.put("finprodId", dto.getFinprodId());
                    String secAcctId = cfetsEtrIbBondinfo002Service.qryPortfolioSecAcctId(map);
                    if (com.joyintech.framework.util.StringUtil.isNotEmpty(secAcctId)) {
                        dto.setSecAcctId(secAcctId); // 证券账户
                    }
                }
                // ----------add by zhangrui1 20200715 预占功能 start----------
                if (StringUtil.isNotEmpty(dto.getEntrustId())) {
                    dataList.add(dto);
                }
                // ----------add by zhangrui1 20200715 预占功能 end----------
            }
            if (StringUtil.equals(BussinessConstant.SUBMIT, allDto.getOperateType())) {
                AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(
                        dataList, DictCodeList.平安资产类型_债券.getValue(),
                        BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(),
                        true, null);
                if (StringUtil.isNotEmpty(ar)) {
                    return new Message<AjaxResult>().success().setData(ar);
                }
            }
            EntrustRdsCommonAop.riskListToChildren(allDto.getRiskTrialRetDto(), allDto.getEtrInvestBondInfoDtos(),
                    (dto, list) -> dto.setRiskTrialRetDto(list));

            if(StringUtil.isNotEmpty(allDto.getEntrustId())){
                allDto.setTotalEtrustId(allDto.getEntrustId());
            }else{
                // 临时主键
                BussinessCodeParameter param = new BussinessCodeParameter();
                param.setCommonBusinessType(DictCodeList.共通业务类型_债券投资委托);
                String generatedId = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
                allDto.setTotalEtrustId(generatedId);
            }
            for (int i = 0; i < allDto.getBussCombDistribtList().size(); i++) {
                allDto.getBussCombDistribtList().get(i).setEntrustId(allDto.getEtrInvestBondInfoDtos().get(i).getEntrustId());
            }
            ProcessParameter<CfetsEtribBondInfo003Dto> processParameter = new ProcessParameter<>();
            WorkFlowParameter<CfetsEtribBondInfo003Dto> params = new WorkFlowParameter<>();

            params.setPageId("CfetsEtrIbBondinfo003");
            params.setElementId(cfetsDto.getElementId());
            params.setBusinessCode(CfetsEtribBondInfo003Dto::getTotalEtrustId);
            params.setBusinessType(DictCodeListExt3.共通业务类型_本币业务指令_债券.getValue());
            processParameter.setOperationType(OperationType.NEW);
            processParameter.setWorkFlowParameter(params);

            String msg = null;
            try {
                msg = cfetsEtrIbBondinfo003Service.start(processParameter, allDto);
                AjaxResult m = new AjaxResult(msg);
                return new Message<AjaxResult>().success().setData(m);
            } catch (Exception e) {
                logger.error("保存银行间多组合指令新建失败", e);
                return PositionUtil.ajaxErrorReturn(e);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return PositionUtil.ajaxErrorReturn(e);
        }
    }
    
    @ApiOperation(value = "持仓列表查询", notes = "持仓列表查询")
    @PostMapping("/queryPositionsPage")
    @ResponseBody
    public Message<Page<CfetsEtrIbBondPosinfo001Dto>> queryPositionsPage(@RequestBody CfetsEtrIbBondPosiPage001RequestDto requestDto) {
        Page<CfetsEtrIbBondPosinfo001Dto> list = cfetsEtrIbBondinfoService.queryPositionsPage(requestDto);
        return new Message<Page<CfetsEtrIbBondPosinfo001Dto>>().success().setData(list);
    }

    @ApiOperation(value = "头寸信息查询", notes = "头寸信息查询")
    @PostMapping("/queryPositionInfo")
    @ResponseBody
    public Message<List<BaseMap>> queryPositionInfo(@RequestBody CfetsEtrIbBondPosiInfoRequestDto requestDto) {
        List<BaseMap> m = cfetsEtrIbBondinfoService.queryPositionInfo(requestDto);
        return new Message<List<BaseMap>>().success().setData(m);
    }

    @PostMapping(value="/exportPosition")
    @ResponseBody
    @ApiOperation(value = "持仓列表导出", notes = "持仓列表导出")
    public void exportPosition(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "持仓列表", () -> {
            CfetsEtrIbBondPosiPage001RequestDto dto = requestDto.getDto(CfetsEtrIbBondPosiPage001RequestDto.class);
            List dataList = cfetsEtrIbBondinfoService.queryPositionsPage(dto).getRoot();
            return dataList;
        });
    }

    @PostMapping(value="/exportEtrInfo")
    @ResponseBody
    @ApiOperation(value = "指令管理导出", notes = "指令管理导出")
    public void exportEtrInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "指令管理", () -> {
            List dataList = cfetsEtrIbBondinfoService.findEtrinfoPage(req.getDto(CfetsEtrIbfindEtrinfoRequest.class)).getRoot();
            return dataList;
        });
    }
    

    @PostMapping("/getEtrFinProductInfo/{secId:.+}")
    @ApiOperation(value = "债券信息查询功能", notes = "债券信息查询功能")
    @ResponseBody
    public Message<CfetsEtrFinProductDto> getEtrFinProductInfo(@PathVariable String secId) {
        try {
//            secId = "F01_lvzq001.IB";
            CfetsEtrFinProductDto dto = cfetsEtrIbBondinfoService.getEtrFinProductInfo(secId);
            return new Message<CfetsEtrFinProductDto>(dto).success().setData(dto);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<CfetsEtrFinProductDto>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "撤回委托Check", notes = "撤回委托Check")
    @PostMapping("recallCheck")
    public Message<AjaxResult> recallCheckDelegate(@RequestBody CfetsEtribBondInfo003Dto dto) throws Exception {
        return cfetsEtrIbBondinfoService.recallCheckDelegate(dto);
    }

    @ResponseBody
    @ApiOperation(value = "撤回委托", notes = "撤回委托")
    @PostMapping("recall")
    public Message<AjaxResult> recallDelegate(@RequestBody CfetsEtribBondInfo003Dto dto) throws Exception {
        return cfetsEtrIbBondinfoService.recallDelegate(dto);
    }

    @PostMapping("closeCheck")
    @ApiOperation(value = "关闭委托check", notes = "关闭委托check")
    @ResponseBody
    public Message<AjaxResult> closeDelegateCheck(@RequestBody CfetsEtribBondInfo003Dto dto) throws Exception{
        return cfetsEtrIbBondinfoService.closeCheckDelegate(dto);
    }

    @PostMapping("close")
    @ApiOperation(value = "关闭委托", notes = "关闭委托")
    @ResponseBody
    public Message<AjaxResult> closeDelegate(@RequestBody CfetsEtribBondInfo003Dto dto) throws Exception {
        //质押式回购也是调用这个关闭方法,但前端没有进行校验
        if(StringUtil.equals(dto.getAssetType(),DictCodeList.平安资产类型_质押式回购.getValue())) {
            Message<AjaxResult> message = cfetsEtrIbBondinfoService.closeCheckDelegate(dto);
            if(CollectionUtils.isNotEmpty(message.getData().getErrorList())) {
                return message;
            }
        }
        return cfetsEtrIbBondinfoService.closeDelegate(dto);
    }

    @ResponseBody
    @ApiOperation(value = "修改委托", notes = "修改委托")
    @PostMapping("modifyDelegate")
    public Message<AjaxResult> modifyDelegate(@RequestBody CfetsEtrInvestBondInfoReqDto dto) throws Exception {
        //对选择的数据进行check
        AjaxResult ajaxResult = cfetsEtrIbBondinfoService.modifyDelegateCheckMethod(dto);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().fail().setData(ajaxResult);
        }
        try {
            CfetsEtrInvestBondInfoReqDto item = dto.getSelectedItem().get(0);
            CfetsEtrIbBondinfoDto result = cfetsEtrIbBondinfoService.findEtrinfo(item);
            AjaxResult m =new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody CfetsEtrIbdoRiskcalRequest dto) throws Exception {
        try {
            //风险试算check
            String  message = cfetsEtrIbBondinfoService.doRiskcalCheckMethod(dto);
            if (StringUtil.isNotEmpty(message)) {
                return new Message<AjaxResult>().fail().setMessage(message);
            }
            CfetsEtribdoRiskcalResponse result=new CfetsEtribdoRiskcalResponse();
            //风险试算组装dto
            Map<String, Object> dataMap=cfetsEtrIbBondinfoService.doRiskcalPackageEquity(dto);
            List<String> resultArrList=interfRiskRdsController.doInfBatchData(dataMap,"0", "RSK002","F01");
            result.setRiskMathFlag("02");
            Boolean forbidFlag=false;
            Boolean cluflag=true;
            List<CfetsEtribdoRiskcalResponse> resultlist=new ArrayList<>();
            if(!StringUtil.isEmptyList(resultArrList) ){
                for(String row:resultArrList){
                    ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
                    if(StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
                            && StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
                    ){
                        if (StringUtil.equals(resultArr.getHead().get("std400msgt"),"E")) {
                            return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
                        }
                    }
                    if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
                        return new Message<AjaxResult>().success().setMessage("无");
                    }
                    List<Map<String, Object>> riskList = (List<Map<String, Object>>)resultArr.getBody().get("RSK_LIST");
                    for(Map<String, Object> map: riskList){
                        if(StringUtil.equals(map.get("kriStatus"),"0") && !StringUtil.equals(map.get("kriTrend"),"向好")){
                            forbidFlag=true;
                        }
                        map.put("kriStatusCn",cfetsEtrIbBondinfoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
                        resultlist.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
                    }
                    result.setRiskcalReturnList(resultlist);
                    result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
                    //当置为不通过时就不再进来了
                    if(cluflag) {
                        if(!StringUtil.equals(resultArr.getBody().get("rskStatus"),"3")){
                            if(StringUtil.equals(resultArr.getBody().get("rskStatus"),"4")&&forbidFlag){
                                cluflag=false;
                                resultArr.getBody().put("rskStatus","3");
                                result.setRskStatus("3");
                            }
                        }else{
                            cluflag=false;
                        }
                        if (!cluflag) {
                            result.setRiskTrialSpan("试算不通过");
                        } else {
                            result.setRiskTrialSpan("试算通过");
                        }
                    }

                }

            }
            AjaxResult m =new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("查询异常");
        }
    }
    @PostMapping("/getPortfolioIdLink")
    @ApiOperation(value = "组合代码链接", notes = "组合代码链接")
    @ResponseBody
    public Message<String> getPortfolioIdLink(@RequestParam("search")String search) {
        try {
            String  url ="invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init?dialog=1&pageModel=2&portfolioId="+search;
            return new Message<String>().success().setData(url);
        } catch (Exception e) {
            logger.error("查询组合代码链接异常", e);
            return new Message<String>().fail().setMessage("查询异常");
        }
    }
    @PostMapping("/getFinprodIdLink")
    @ApiOperation(value = "资产代码链接", notes = "资产代码链接")
    @ResponseBody
    public Message<String> getFinprodIdLink(@RequestParam("search")String search) {
        try {
            String  url ="invest/asset/bond/FinBondTargetAsset002/init?dialog=1&pageModel=2&finprodId="+search;
            return new Message<String>().success().setData(url);
        } catch (Exception e) {
            logger.error("查询资产代码链接异常", e);
            return new Message<String>().fail().setMessage("查询异常");
        }
    }

    @ExceptionHandler
    @ResponseBody
    public Message<String> ExceptionHandler(Exception e) {
        return new Message<String>().fail().setMessage("异常：" + e.getMessage());
    }
    @ApiOperation(value = "回售或卖出时组合分配列表查询", notes = "回售或卖出时组合分配列表查询")
    @PostMapping("/queryCfetsBussCombDisList/{secId:.+}")
    @ResponseBody
    public Message<List<CfetsBussCombDistribtBaseDto>> queryCfetsBussCombDisList(@PathVariable String secId) {
        try {
            List<CfetsBussCombDistribtBaseDto> list = cfetsEtrIbBondinfoService.queryCfetsBussCombDisList(secId);
            return new Message<List<CfetsBussCombDistribtBaseDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询该资产对应的组合分配列表异常", e);
            return new Message<List<CfetsBussCombDistribtBaseDto>>().fail().setMessage("查询异常");
        }
     }

    @RequestMapping("/selectPortfolioIdForSelect")
    @ApiOperation(value = "组合列表查询", notes = "组合列表查询")
    @ResponseBody
    public Page<SelectOptionDto> selectPortfolioIdForSelect(CfetsSearchInfoRequest item) {
        try {
            if(StringUtil.isEmpty(item.getPs())) {
                PageMode page = new PageMode();
                page.setPageNo(item.getPage());
                page.setPageSize(item.getLength());
                item.setPageMode(page);
                return joyinbusinessCommonService.selectPortfolioIdList(item);
            }else {
                List<CfetsBussCombDistribtBaseDto> list = cfetsEtrIbBondinfoService.queryCfetsBussCombDisList(item.getFinprodId());
                List<SelectOptionDto> selectList = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(list)) {
                    for(CfetsBussCombDistribtBaseDto e : list) {
                        if(StringUtil.isNotEmpty(item.getInitValue()) && e.getPortfolioId().equals(item.getInitValue())) {
                            SelectOptionDto dto = new SelectOptionDto();
                            dto.setText(e.getPortfolioName());
                            dto.setId(e.getPortfolioId());
                            dto.setObject(e);
                            selectList.add(dto);
                            break;
                        }
                        if(StringUtil.isEmpty(item.getInitValue())) {
                            SelectOptionDto dto = new SelectOptionDto();
                            dto.setText(e.getPortfolioName());
                            dto.setId(e.getPortfolioId());
                            dto.setObject(e);
                            selectList.add(dto);
                        }
                    }
                }
                return new Page(selectList);
            }
        } catch (Exception e) {
            logger.error("查询组合列表异常", e);
            return null;
        }
    }


    @ApiOperation(value = "查询资产是否行权债", notes = "查询资产是否行权债且行权净价不为空")
    @PostMapping("/queryAssetIsRight/{secId:.+}")
    @ResponseBody
    public Message<Boolean> queryAssetIsRight(@PathVariable String secId) {
        try {
            Boolean flag = cfetsEtrIbBondinfoService.queryAssetIsRight(secId);
            return new Message<Boolean>().success().setData(flag);
        } catch (Exception e) {
            logger.error("查询该资产是否行权债异常", e);
            return new Message<Boolean>().fail().setMessage("查询异常");
        }
    }
    @ApiOperation(value = "查询资产是否进行了赎回登记", notes = "查询资产是否进行了赎回登记")
    @PostMapping("/queryAssetIsShReg/{secId:.+}")
    @ResponseBody
    public Message<Boolean> queryAssetIsShReg(@PathVariable String secId) {
        try {
            Boolean flag = cfetsEtrIbBondinfoService.queryAssetIsShReg(secId);
            return new Message<Boolean>().success().setData(flag);
        } catch (Exception e) {
            logger.error("查询该资产是否进行了赎回登记异常", e);
            return new Message<Boolean>().fail().setMessage("查询异常");
        }
    }
    @ApiOperation(value = "指令买入时查询业务模式", notes = "指令买入时查询业务模式")
    @PostMapping("/queryInvaimBySecIdAndPrd/{portfolioId}/{secId:.+}")
    @ResponseBody
    public Message<String> findInvAim(@PathVariable("portfolioId") String portfolioId,@PathVariable String secId) {
        try {
            String restult = cfetsEtrIbBondinfoService.findInvAim(portfolioId,secId);
            if(!StringUtil.isNotEmpty(restult)){
                restult="";
            }
            return new Message<String>().success().setData(restult);
        } catch (Exception e) {
            logger.error("查询指令买入时查询业务模式异常", e);
            return new Message<String>().fail().setMessage("查询异常");
        }
    }
    @ApiOperation(value = "查询资产是否进行了SPPI测试", notes = "查询资产是否进行了SPPI测试")
    @PostMapping("/queryAssetIsSppiTest/{secId:.+}")
    @ResponseBody
    public Message<SppiImportData> queryAssetIsSppiTest(@PathVariable String secId) {
        try {
            SppiImportData data = cfetsEtrIbBondinfoService.queryAssetIsSppiTest(secId);
            return new Message<SppiImportData>().success().setData(data);
        } catch (Exception e) {
            logger.error("查询该资产是否进行了SPPI测试异常", e);
            return new Message<SppiImportData>().fail().setMessage("查询异常");
        }
    }
}