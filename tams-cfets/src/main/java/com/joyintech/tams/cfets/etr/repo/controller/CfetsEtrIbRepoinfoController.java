/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.etr.repo.controller;

import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.dto.CfetsSearchInfoRequest;
import com.joyintech.tams.business.common.dto.SecManageAcctIdRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.repo.dto.*;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoAutoRKService;
import com.joyintech.tams.cfets.etr.repo.service.CfetsEtrIbRepoinfoService;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.component.business.trade.BussEtrInvestPersonInfoService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.client.ClientData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see CfetsEtrIbRepoinfoController
 * @since 1.0
 */
@RestController
@PageId("CfetsEtrRepoInfo")
@RequestMapping("/cfets/etrib/cfetsEtrIbRepoinfo")
@Api(tags = "银行间回购指令")
public class CfetsEtrIbRepoinfoController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrIbRepoinfoController.class);
    /**
     * 市场行情接口地址
     */
/*    @Value("${MarketInfoInterf.BaseUrl}")
    private String MarketInfoInterf_BaseUrl;*/
    /**
     * 外汇交易中心行情接口地址
     */
    @Value("${MarketInfoInterf.cmdsUrl}")
    private String MarketInfoInterf_cmdsUrl;

    /**
     * 债券持仓查询
     */
    @Autowired
    private CfetsEtrIbRepoinfoService cfetsEtrIbRepoinfoService;
    
    @Autowired(required=false)
    private BussEtrInvestPersonInfoService bussEtrInvestPersonInfoService;

    @Value("${cmdsWebSocketUrl}")
    private String RepoWebSocketUrl;

    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private CfetsEtrIbRepoAutoRKService cfetsEtrIbRepoAutoRKService;




    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        initPageParams.put("PINAN_ASSET_TYPE_F07", DictCodeList.平安资产类型_质押式回购.getValue());
        // 回购模式
        initPageParams.put("repo_pledge_model", CacheUtil.getSysParam("repo_pledge_model"));
        //本币业务指令画面头寸信息区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrIbRepoinfo_positionTable_refresh_period", CacheUtil.getSysParam("cfetsEtrIbRepoinfo_positionTable_refresh_period"));
        //本币业务指令画面指令列表区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrIbRepoinfo_etrTable_refresh_period", CacheUtil.getSysParam("cfetsEtrIbRepoinfo_etrTable_refresh_period"));
        Map<String,Object> map = new HashMap<>();
        map.put("role_pos", DictCodeListExt2.角色岗位_投资经理.getValue());
        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        Long count = cfetsEtrIbRepoinfoService.selectUserRolePosList(map) ;
        //是否投资经理
        if(count > 0){
            initPageParams.put("is_role_pos", "Y");
        }else{
            initPageParams.put("is_role_pos", "N");
        }
        //CMDS地址
        initPageParams.put("RepoWebSocketUrl", RepoWebSocketUrl);
        initPageParams.put("NEED_WORKFLOW", true);
        initPageParams.put("platDate",DateUtil.platDateFromCache());
        //风险
        initPageParams.put("riskMathSwitch", CacheUtil.getSysParam("risk_math_switch"));
        initPageParams.put("pageRedisFlag", CacheUtil.getSysParam("pageRedisFlag"));
        initPageParams.put("isRiskmathSoftcontrol", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));
        //数据字典初始化数据
        initPageParams.put("FINPROD_TYPE", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        initPageParams.put("FINPROD_TYPE_THREE", this.getSelectDataFromDataDic(DictCode.行内资产三级分类, null));
        initPageParams.put("PRICE_MODEL", this.getSelectDataFromDataDic(DictCode.价格模式, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
        initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        initPageParams.put("MAIN_GRADE_RESULT", this.getSelectDataFromDataDic(DictCode.评级结果, null));
        initPageParams.put("TERM_TYPE_REBACK", this.getSelectDataFromDataDic(DictCode.回购期限品种, null));
        initPageParams.put("PROFIT_TYPE",this.getSelectDataFromDataDic(DictCode.收益类型, null));
        initPageParams.put("ISSUE_MODE",this.getSelectDataFromDataDic(DictCode.开放类型, null));
        initPageParams.put("RAISE_TYPE",this.getSelectDataFromDataDic(DictCode.募集方式, null));
        initPageParams.put("INVEST_TYPE",this.getSelectDataFromDataDic(DictCode.投资性质, null));
        //pageId
        initPageParams.put("pageId", "CfetsEtrIbRepoinfo003");
        initPageParams.put("elementId", "CfetsEtrIbRepoinfo003_btn_submit");
        return new Message<Map<String, Object>>(initPageParams).success();
    }
    
    /**
     * 指令管理查询
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("findZlglPage")
    @ApiOperation(value = "指令管理查询", notes = "指令管理查询")
    public Message<Page<CfetsEtrRepoZLCXResponse>> findZlglPage(@RequestBody CfetsEtrRepoZLCXRequest cfetsEtrRepoZLCXRequest){
        try {
            Page<CfetsEtrRepoZLCXResponse> list = cfetsEtrIbRepoinfoService.findZlglPage(cfetsEtrRepoZLCXRequest);
            return new Message<Page<CfetsEtrRepoZLCXResponse>>().success().setData(list);
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("查询指令管理分页数据异常",e);
            return new Message<Page<CfetsEtrRepoZLCXResponse>>().fail().setMessage("查询指令管理分页数据异常");
        }
    }

    /**
     * 查询债券质押持仓
     * 
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("findZlglPositionPage")
    @ApiOperation(value = "质押券管理查询", notes = "质押券管理查询")
    public Message<Page<CfetsEtrRepoZLCXPositionResponse>> findZlglPositionPage(@RequestBody CfetsEtrRepoZLCXPositionRequest cfetsEtrRepoZLCXPositionRequest){
        try {
            Page<CfetsEtrRepoZLCXPositionResponse> list = cfetsEtrIbRepoinfoService.findZlglPositionPage(cfetsEtrRepoZLCXPositionRequest);
            return new Message<Page<CfetsEtrRepoZLCXPositionResponse>>().success().setData(list);
        }catch (Exception e) {
            e.printStackTrace();
            logger.error("查询质押券管理查询分页数据异常",e);
            return new Message<Page<CfetsEtrRepoZLCXPositionResponse>>().fail().setMessage("查询质押券管理查询分页数据异常");
        }
    }

    @PostMapping("/getSecManageAcctId")
    @ApiOperation(value = "证券账户列表查询", notes = "证券账户列表查询")
    public Message<Page<SelectOptionDto>> getSecManageAcctId(@RequestBody SecManageAcctIdRequestDto secManageAcctIdRequestDto) {
        try {
            Page<SelectOptionDto> tempList = cfetsEtrIbRepoinfoService.getSecManageAcctId(secManageAcctIdRequestDto);
            return new Message<Page<SelectOptionDto>>().success().setData(tempList);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
        }
    }

    @RequestMapping("/queryMySideList")
    public Page<SelectOptionDto> queryMySideList(@RequestBody CfetsSearchInfoRequest item){
        Page<SelectOptionDto> list = joyinbusinessCommonService.queryMySideList(item);
        return list;
    }

    @PostMapping(value="/exportPosition")
    @ApiOperation(value = "质押券列表导出", notes = "质押券列表导出")
    public void exportPosition(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "持仓列表", () -> {
            CfetsEtrRepoZLCXPositionRequest dto = requestDto.getDto(CfetsEtrRepoZLCXPositionRequest.class);
            List dataList = cfetsEtrIbRepoinfoService.findZlglPositionPage(dto).getRoot();
            return dataList;
        });
    }

    @PostMapping(value="/exportEtrInfo")
    @ApiOperation(value = "指令管理导出", notes = "指令管理导出")
    public void exportEtrInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "指令管理", () -> {
            List dataList = cfetsEtrIbRepoinfoService.findZlglPage(req.getDto(CfetsEtrRepoZLCXRequest.class)).getRoot();
            return dataList;
        });
    }

    @PostMapping(value="/checkBondInfo")
    @ApiOperation(value = "添加质押券校验", notes = "模式2 添加质押券校验")
    public Message<AjaxResult> checkBondInfo(@RequestBody CfetsEtrRepoBondInfoRequest request) {
        try {
            AjaxResult ajax = cfetsEtrIbRepoinfoService.checkBondInfo(request);
            if (null != ajax) {
                return new Message<AjaxResult>().success().setData(ajax);
            } else {
                return new Message<AjaxResult>().success().setMessage("校验通过");
            }
        } catch (Exception e) {
            logger.error("添加质押券校验异常", e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }

    @PostMapping("/getPledgeable")
    @ApiOperation(value = "获取可质押券面总额", notes = "模式1、3 获取可质押券面总额")
    public Message<BigDecimal> getPledgeable(@RequestBody CfetsEtrRepoPledgeableRequest request) {
        try {
            BigDecimal pledgeable = BigDecimal.ZERO;
            Map<String, Object> pageParam = BeanUtil.object2Map(request);
            pledgeable = cfetsEtrIbRepoinfoService.getPledgeable(pageParam);
            return new Message<BigDecimal>().success().setData(pledgeable);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<BigDecimal>().fail().setMessage("查询异常");
        }
    }

    /**
     * 质押库：下拉框
     *
     * @param params 画面参数
     * @return 质押库：下拉框
     * @see
     * @since 1.0
     */
    @PostMapping("getPledgeStockIdByPortfolioId")
    @ApiOperation(value = "质押库：下拉框", notes = "质押库：下拉框")
    public Message<List<SelectOptionDto>> getPledgeStockIdByPortfolioId(@RequestBody CfetsSearchPledgeStockRequest params) {
    	try {
            cfetsEtrIbRepoAutoRKService.createComRule(SecurityUtil.getLoginUser().getUserName());
            List<SelectOptionDto> page = cfetsEtrIbRepoinfoService.getPledgeStockIdByPortfolioId(params);
			return new Message<List<SelectOptionDto>>().success().setData(page);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
    	
    }
    /**
     * 质押库：下拉框
     *
     * @param param 画面参数
     * @return 质押库：下拉框
     * @see
     * @since 1.0
     */
    @RequestMapping({"getPledgeStockId"})
    @ApiOperation(value = "质押库：下拉框", notes = "质押库：下拉框")
    public List<SelectOptionDto> getPledgeStockId(@RequestParam Map<String, String> param ) {
        try {
            CfetsSearchPledgeStockRequest params = new CfetsSearchPledgeStockRequest();
            params.setPortfolioId(param.get("portfolioId"));
            params.setInitValue(param.get("initValue"));
            params.setSearch(param.get("search"));
            cfetsEtrIbRepoAutoRKService.createComRule(SecurityUtil.getLoginUser().getUserName());
            List<SelectOptionDto> page = cfetsEtrIbRepoinfoService.getPledgeStockIdByPortfolioId(params);
            return page;
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return null;
        }

    }
    
    /**
     * 出库操作
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "出库操作", notes = "出库操作")
    @PostMapping("outSave")
    public Message<AjaxResult> outSave(@RequestBody CfetsEtrRepoSaveRequest dto) {
     // 校验操作的角色是否是投资经理
        Map<String,Object> map = new HashMap<>();
        AjaxResult ajax = new AjaxResult();
        map.put("role_pos", DictCodeListExt2.角色岗位_投资经理.getValue());
        map.put("userId", SecurityUtil.getLoginUser().getUserName());
        Long count = cfetsEtrIbRepoinfoService.selectUserRolePosList(map) ;
        if(count > 0){
            Long outFlat = cfetsEtrIbRepoinfoService.outSave(dto);
            // 标记为大于0，出库成功
            if(outFlat > 0){
                ajax= new AjaxResult(this.getMessage("EtrStandardBond001.outSave_002"));
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                ajax=  new AjaxResult(this.getMessage("EtrStandardBond001.outSave_003"));
                return new Message<AjaxResult>().success().setData(ajax);
            }
        }else{
            List<JoyinMessage> joyinMessages = new ArrayList<>();
            joyinMessages.add(
                    new JoyinMessage("角色岗位为投资经理才能操作", MessageType.警告消息));
            ajax.setJoyinMessages(joyinMessages);
            List<ValidMessage>  errorList = new ArrayList<>();
            errorList.add(new ValidMessage("角色岗位为投资经理才能操作"));
            ajax=  new AjaxResult();
            ajax.setErrorList(errorList);
            return new Message<AjaxResult>().success().setData(ajax);
        }
    }
    
//    @RequestMapping(value="findQuotations")
//    @ResponseBody
//    public Map<String, Object> findQuotations() {
//    	return etrIbRepo001Service.findQuotations(RequestUtil.getPageParam());
//    }

    @ApiOperation(value = "多组合指令新建保存/提交校验", notes = "多组合指令新建保存/提交校验")
    @PostMapping("checkSaveError")
    public Message<AjaxResult> checkSaveError(@RequestBody CfetsEtrRepoSaveRequest cfetsDto, BindingResult br)
            throws Exception {
        try {
            AjaxResult ajax= cfetsEtrIbRepoinfoService.checkSaveErrorMethod(cfetsDto, br);
            if(null!=ajax){
                return new Message<AjaxResult>().success().setData(ajax);
            }else{
                return new Message<AjaxResult>().success().setMessage("校验通过");
            }
        }catch (Exception e) {
            logger.error("多组合指令新建保存/提交校验异常",e);
            return new Message<AjaxResult>().fail().setMessage("校验失败");
        }
    }


    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody CfetsEtribRepoInfo003Dto dto) throws Exception {
        try {
            //风险试算check
            String  message = cfetsEtrIbRepoinfoService.doRiskcalCheckMethod(dto);
            if (StringUtil.isNotEmpty(message)) {
                return new Message<AjaxResult>().fail().setMessage(message);
            }
            CfetsEtribdoRiskcalResponse result=new CfetsEtribdoRiskcalResponse();
            //风险试算组装dto
            List<Map<String, Object>> dataMap= cfetsEtrIbRepoinfoService.doRiskcalPackageEquity(dto);
            String resultArrList=interfRiskRdsController.batchRiskTrialRds(dataMap,"0", "F07");
            result.setRiskMathFlag("02");
            Boolean forbidFlag=false;
            Boolean cluflag=true;
            List<CfetsEtribdoRiskcalResponse> resultlist=new ArrayList<>();
            if(StringUtil.isNotEmpty(resultArrList) ){
//                for(String row:resultArrList){
                    ClientData resultArr = JsonUtils.json2Object(resultArrList, ClientData.class);
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
                        map.put("kriStatusCn",cfetsEtrIbRepoinfoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
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
//                }
            }
            AjaxResult m =new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("查询异常");
        }
    }


    @ResponseBody
    @ApiOperation(value = "修改委托", notes = "修改委托")
    @PostMapping("modifyDelegate")
    public Message<AjaxResult> modifyDelegate(@RequestBody CfetsEtribRepoInfo003Dto dto) throws Exception {
        //对选择的数据进行check
        AjaxResult ajaxResult = cfetsEtrIbRepoinfoService.modifyDelegateCheckMethod(dto);
        if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ajaxResult);
        }
        try {
            CfetsEtribRepoInfo003Dto item = dto.getSelectedItem().get(0);
            CfetsEtribRepoInfo003Dto result = cfetsEtrIbRepoinfoService.findEtrinfo(item);
            AjaxResult m =new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "查看指令", notes = "查看指令")
    @PostMapping("referenceDelegate")
    public Message<AjaxResult> referenceDelegate(@RequestBody CfetsEtribRepoInfo003Dto dto)  {
        //对选择的数据进行check
        try {
            CfetsEtribRepoInfo003Dto item = dto.getSelectedItem().get(0);
            CfetsEtribRepoInfo003Dto result = cfetsEtrIbRepoinfoService.findEtrinfo(item);
            AjaxResult m =new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<AjaxResult>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "计算期限品种", notes = "计算期限品种")
    @PostMapping("getTermType")
    public Message<AjaxResult> getTermType(@RequestBody Map<String,Object> map) throws Exception {
        String msg = null;
        if(StringUtil.isNotEmpty(map.get("repoTerm"))){
            msg = cfetsEtrIbRepoinfoService.getTermType((Integer) map.get("repoTerm"));
        }
        AjaxResult m =new AjaxResult();
        m.setData(msg);
        return new Message<AjaxResult>().success().setData(m);
    }

    @ResponseBody
    @ApiOperation(value = "计算期限品种", notes = "计算期限品种")
    @PostMapping("getTermTypeByRepoTerm")
    public Message<AjaxResult> getTermType(@RequestParam("repoTerm") String repoTerm) throws Exception {
        String msg = null;
        if(StringUtil.isNotEmpty(repoTerm)){
            msg = cfetsEtrIbRepoinfoService.getTermType(Integer.valueOf(repoTerm));
        }
        AjaxResult m =new AjaxResult();
        m.setData(msg);
        return new Message<AjaxResult>().success().setData(m);
    }

    @RequestMapping("/queryMySideListOfPage")
    @ResponseBody
    public List<SelectOptionDto> queryMySideListOfPage( HttpServletRequest request){
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("dealType",request.getParameter("dealType"));
            map.put("initValue",request.getParameter("initValue"));
            List<SelectOptionDto> list = cfetsEtrIbRepoinfoService.queryMySideList(map);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询功能异常", e);
            return null;
        }
    }

}
