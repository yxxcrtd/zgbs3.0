package com.joyintech.tams.cfets.trd.repo.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelTplUitl;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.trd.bond.service.IbuQuoteBondService;
import com.joyintech.tams.cfets.trd.repo.dto.*;
import com.joyintech.tams.cfets.trd.repo.service.CfetsRepoDealQuotedPrice002Service;
import com.joyintech.tams.cfets.trd.repo.service.IbuQuoteRepoService;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.dao.TamsBaseMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 本币交易-质押式回购交易 初始化页面
 *
 * @author jiangpeng
 * @date 2021/12/4 15:38
 */
@Controller
@PageId("IbuQuoteRepo")
@RequestMapping("/cfets/bankuplink/repo/IbuQuoteRepo")
@Api(tags = "CFETS质押式回购交易-常用查询类接口")
public class CfetsIbuQuoteRepoController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsIbuQuoteRepoController.class);

    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    @Autowired
    private IbuQuoteRepoService ibuQuoteRepoService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;

    @Autowired
    private CfetsRepoDealQuotedPrice002Service cfetsRepoDealQuotedPrice002Service;

    @Autowired
    private TamsBaseMapper tamsBaseMapper;

    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @ApiOperation(value = "初始化参数", notes = "初始化页面参数")
    @PostMapping("/initParams")
    @ResponseBody
    public Message<Map<String, Object>> initParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        // 清算速度 T+0、T+1
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
        // 交易方向 正回购、逆回购
        initPageParams.put("BACK_LEND_DIRECTION", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        // 价格模式 限价指令、指定价格
        initPageParams.put("PRICE_MODEL", this.getSelectDataFromDataDic(DictCode.价格模式, null));
        // 结算方式
        initPageParams.put("SETT_MODE", this.getSelectDataFromDataDic(DictCode.结算方式, null));
        // 回购方式
        initPageParams.put("REPO_METHOD", this.getSelectDataFromDataDic(DictCode.回购方式, null));
        // 有效状态
        initPageParams.put("STATUS_VALID", this.getSelectDataFromDataDic(DictCode.有效状态, null));
        // 发送状态
        initPageParams.put("SEND_STATUS", this.getSelectDataFromDataDic(DictCode.发送状态, null));
        // 报价状态
        initPageParams.put("QUOTE_STUTAS_IBU", this.getSelectDataFromDataDic(BussinessDictCode.银行间上行报价状态, null));
        // 分配方式
        initPageParams.put("DISTRIBUTION", this.getSelectDataFromDataDic(DictCode.分配方式, null));
        // 报价方式_登记
        initPageParams.put("QUOTE_STYLE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式_登记, null));
        // 报价方式
        initPageParams.put("QUOTE_TYPE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式, null));
        // 币种
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        // 清算类型
        initPageParams.put("CLEAR_TYPE", this.getSelectDataFromDataDic(DictCode.清算类型, null));
        // 登记托管机构
        initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        // 债券类型
        initPageParams.put("BOND_TYPE", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        // 回购方式_双边回购
        initPageParams.put("REPO_METHOD_1", DictCodeListExt3.回购方式_双边回购.getValue());
        // 资金账户类型_托管账户
        initPageParams.put("acctType_A01", BussinessDictCodeList.资金账户类型_托管账户.getValue());
        // 资金账户类型_交易所资金账户
        initPageParams.put("acctType_A07", BussinessDictCodeList.资金账户类型_交易所资金账户.getValue());
        // 委托状态_待接收
        initPageParams.put("ENTRUST_STATUS_02", DictCodeList.委托状态_待接收.getValue());
        // 委托状态_已接收
        initPageParams.put("ENTRUST_STATUS_03", DictCodeList.委托状态_已接收.getValue());
        // 委托状态_已完成
        initPageParams.put("ENTRUST_STATUS_04", DictCodeList.委托状态_已完成.getValue());
        // 委托状态_已退回
        initPageParams.put("ENTRUST_STATUS_05", DictCodeList.委托状态_已退回.getValue());
        // 交易日历_银行间
        initPageParams.put("BATCH_QUARTZ_SKIP_HOLIDAY_MARKET_CAL_TYPE_03", "03");
        // 系统日期
        initPageParams.put("sysDate", iPlatformDate.getSysDateStr());
        // 币种_人民币
        initPageParams.put("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());

        //新增报价银行间交易员是否允许修改（Y：可以，N：不可以修改）
        initPageParams.put("ouTraderIdCanEditFlag", CacheUtil.getSysParam("ibuFlag_ouTraderIdCanEdit"));
        //银行间上行没有选择委托指令的场合是否允许发起对话报价（01：不允许，02：允许）
        initPageParams.put("ibuFlag_startDialogueQuoteWithNoEntrust", CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));
        // 待回复报价是否所有交易员可见（Y：所有交易员都可见，N：只有交易员可见）
        initPageParams.put("ibuFlag_allTraderCanShowUnAnswerQuote", CacheUtil.getSysParam("ibuFlag_allTraderCanShowUnAnswerQuote"));
        //刷新时间
        initPageParams.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        //银行间质押式回购质押券模式：01无质押券库，02单个质押库，03多个质押券库
        initPageParams.put("repo_pledge_model", CacheUtil.getSysParam(BussinessConstant.REPO_PLEDGE_MODEL));
        //CMDS地址
        initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
        //本币导出按钮配置
        initPageParams.put("EXPORT_SHOW", CacheUtil.getSysParam("EXPORT_SHOW"));
        // 登记页面id 用于登记撤销
        initPageParams.put("PAGE_ID_REGIT", "IbuQuoteRepo004");
        // 报价页面id 质押式回购-对话报价
        initPageParams.put("PAGE_ID_DIALOG", "CfetsQuoteRepo003");
        // 01: 开启银行间报价 02:关闭银行间报价（事后录入）
        initPageParams.put("ibuFlag_quote_switch", CacheUtil.getSysParam(BussinessConstant.ibuFlag_quote_switch));
        //系统是否支持报价业务 Y:支持,N:不支持
        initPageParams.put("is_sys_support_quote", CacheUtil.getSysParam("is_sys_support_quote"));
        return new Message<>(initPageParams).success();
    }

    @ApiOperation(value = "查询指令列表分页数据", notes = "查询指令列表分页数据")
    @PostMapping("/pageEntrust")
    @ResponseBody
    public Message<Page<RepoEntrustDto>> pageEntrust(@RequestBody RepoEntrustRequest dto) {
        try {
            Page<RepoEntrustDto> list = ibuQuoteRepoService.listEntrust(dto);
            return new Message<Page<RepoEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询指令列表分页数据异常", e);
            return new Message<Page<RepoEntrustDto>>().fail().setMessage("查询指令列表分页数据异常");
        }
    }

    @ApiOperation(value = "指令列表导出", notes = "指令列表导出")
    @ResponseBody
    @RequestMapping("exportEntrust")
    public void export(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "指令列表", () -> {
            RepoEntrustRequest repoEntrustRequest = requestDto.getDto(RepoEntrustRequest.class);
            List dataList = ibuQuoteRepoService.listEntrust(repoEntrustRequest).getRoot();
            return dataList;
        });
    }

    @ApiOperation(value = "查询已质押债券列表信息", notes = "指令详情查询指令已质押债券列表信息")
    @PostMapping("/selectRepoEntrustPledges")
    @ResponseBody
    public Message<List<RepoEntrustPledgeDto>> selectRepoEntrustPledges(@RequestBody CfetsRepoDealQuoteRequest cfetsRepoDealQuoteRequest) {
        List<RepoEntrustPledgeDto> list = ibuQuoteRepoService.selectRepoEntrustPledges(cfetsRepoDealQuoteRequest);
        return new Message<List<RepoEntrustPledgeDto>>().success().setData(list);
    }

    @ApiOperation(value = "交易执行初始化检查", notes = "交易执行初始化检查")
    @PostMapping("/dealexecutioncheck")
    @ResponseBody
    public Message<AjaxResult> dealexecutioncheck(@RequestBody CfetsRepoDealQuoteRequest cfetsRepoDealQuoteRequest) {
        AjaxResult ajaxResult = ibuQuoteRepoService.dealexecutioncheck(cfetsRepoDealQuoteRequest);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ApiOperation(value = "交易执行初始化", notes = "交易执行初始化")
    @PostMapping("/dealexecutioninit")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> dealexecutioninit(@RequestBody CfetsRepoDealQuoteRequest cfetsRepoDealQuoteRequest) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.queryEntrustInfoById(cfetsRepoDealQuoteRequest);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }
    @ApiOperation(value = "查询质押券", notes = "指定质押券(模式2)时,添加指令后带出质押券信息")
    @PostMapping("/ptlAllocationAddAfter")
    @ResponseBody
    public Message<List<RepoEntrustPledgeDto>> ptlAllocationAddAfter(@RequestBody RepoAutoAllocationRequest autoAllocationRequest) {
        try {
            List<RepoEntrustPledgeDto> list = ibuQuoteRepoService.ptlAllocationAddAfter(autoAllocationRequest);
            return new Message<List<RepoEntrustPledgeDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询质押券信息异常", e);
            return new Message<List<RepoEntrustPledgeDto>>().fail().setMessage("查询质押券信息异常");
        }
    }

    @ApiOperation(value = "费用计算", notes = "根据交易金额、交易日期、交易方向计算交易手续费和结算服务费")
    @PostMapping("/calFee")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> calFee(@RequestBody RepoCalRequest request) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.calFee(request);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }

    @ApiOperation(value = "组合分配添加初始化", notes = "组合分配列表,点击添加按钮的初始化")
    @PostMapping("/ptlAllocationAdd")
    @ResponseBody
    public Message<List<RepoEntrustDto>> ptlAllocationAdd(@RequestBody RepoPtlAllocationAddRequest ptlAllocationAddRequest) {
        try {
            List<RepoEntrustDto> list = ibuQuoteRepoService.ptlAllocationAdd(ptlAllocationAddRequest);
            return new Message<List<RepoEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("组合分配添加初始化异常", e);
            return new Message<List<RepoEntrustDto>>().fail().setMessage("组合分配添加初始化异常");
        }
    }

    @ApiOperation(value = "删除组合", notes = "删除组合联动质押券信息")
    @PostMapping("/ptlAllocationDel")
    @ResponseBody
    public Message<List<RepoEntrustPledgeDto>> ptlAllocationDel(@RequestBody RepoAutoAllocationRequest autoAllocationRequest) {
        List<RepoEntrustPledgeDto> list = ibuQuoteRepoService.ptlAllocationDel(autoAllocationRequest);
        return new Message<List<RepoEntrustPledgeDto>>().success().setData(list);
    }

    @ApiOperation(value = "自动分配", notes = "自动分配按钮触发功能,将基本信息中费用,金额按要求分配到组合分配中")
    @PostMapping("/autoAllocation")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> autoAllocation(@RequestBody RepoAutoAllocationRequest autoAllocationRequest) {
        try {
            CfetsRepoDealQuoteCommonDto resultDto = ibuQuoteRepoService.autoAllocation(autoAllocationRequest);
            return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(resultDto);
        } catch (Exception e) {
            logger.error("自动分配异常", e);
            return new Message<CfetsRepoDealQuoteCommonDto>().fail().setMessage("自动分配异常");
        }
    }

    @ApiOperation(value = "金额信息计算", notes = "根据[清算速度][交易金额][回购利率][回购期限] 计算 [首次结算日][到期结算日][实际占款天数][回购利息][到期结算金额]")
    @PostMapping("/calAmtInfo")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> calAmtInfo(@RequestBody RepoCalRequest request) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.calAmtInfo(request);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }

    @ResponseBody
    @ApiOperation(value = "风险试算check", notes = "风险试算check")
    @PostMapping("/doRiskcalCheck")
    public Message<AjaxResult> doRiskcalCheck(@RequestBody RepoRiskCalRequest riskCalRequest) {
        AjaxResult ajaxResult = ibuQuoteRepoService.riskCheckNotEmpty(riskCalRequest);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }


    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("/doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody RepoRiskCalRequest riskCalRequest) {
        CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
        try {
//            AjaxResult ajaxResult = ibuQuoteRepoService.riskCheckNotEmpty(riskCalRequest);
//            if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
//                return new Message<AjaxResult>().success().setData(ajaxResult);
//            }
//            Map<String, Object> dataMap = ibuQuoteRepoService.doRiskcalPackage(riskCalRequest);
//            result.setRiskMathFlag("02");
//            List<String> resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "1", "RSK002", "F07");
//            boolean forbidFlag = false;
//            boolean cluflag = true;
//            //是否全部正常
//            boolean allNormalFlag = true;
//            List<CfetsEtribdoRiskcalResponse> resultList = new ArrayList<>();
//            if (!StringUtil.isEmptyList(resultArrList)) {
//                for (String row : resultArrList) {
//                    ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
//                    if (StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
//                            && StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
//                    ) {
//                        if (StringUtil.equals(resultArr.getHead().get("std400msgt"), "E")) {
//                            return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
//                        }
//                    }
//                    if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
//                            || !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
//                            || !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
//                        return new Message<AjaxResult>().success().setMessage("无");
//                    }
//                    List<Map<String, Object>> riskList = (List<Map<String, Object>>) resultArr.getBody().get("RSK_LIST");
//                    for (Map<String, Object> map : riskList) {
//                        if (allNormalFlag && !StringUtil.equals(map.get("kriStatus"), "0")) {
//                            allNormalFlag = false;
//                        }
//                        if (StringUtil.equals(map.get("kriStatus"), "0") && !StringUtil.equals(map.get("kriTrend"), "向好")) {
//                            forbidFlag = true;
//                        }
//                        map.put("kriStatusCn", ibuQuoteRepoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
//                        resultList.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
//                    }
//                    if (!allNormalFlag) {
//                        result.setRiskcalReturnList(resultList);
//                    }
//                    result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
//                    //当置为不通过时就不再进来了
//                    if (cluflag) {
//                        if (!StringUtil.equals(resultArr.getBody().get("rskStatus"), "3")) {
//                            if (StringUtil.equals(resultArr.getBody().get("rskStatus"), "4") && forbidFlag) {
//                                cluflag = false;
//                                resultArr.getBody().put("rskStatus", "3");
//                                result.setRskStatus("3");
//                            }
//                        } else {
//                            cluflag = false;
//                        }
//                        if (!cluflag) {
//                            result.setRiskTrialSpan("试算不通过");
//                        } else {
//                            result.setRiskTrialSpan("试算通过");
//                        }
//                    }
//
//                }
//
//            }
            AjaxResult m = new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }
    }

    @ApiOperation(value = "查询交易执行明细信息", notes = "查询交易执行明细信息")
    @PostMapping("/listDealTransactionDetails")
    @ResponseBody
    public Message<Page<RepoDealTransactionDetailsDto>> listDealTransactionDetails(@RequestBody RepoEntrustRequest dto) {
        try {
            String entrustId;
            if (StringUtil.isNotEmpty(dto) && CollectionUtils.isNotEmpty(dto.getEntrustIdList())) {
                entrustId = dto.getEntrustIdList().get(0);
            } else {
                return new Message<Page<RepoDealTransactionDetailsDto>>().success().setData(new Page<>());
            }
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> param = new HashMap<>();
            param.put("entrustId", entrustId);
            // 排序处理
            if (StringUtil.isNotEmpty(dto.getPageMode())) {
                if (StringUtil.isNotEmpty(dto.getPageMode().getDirection())) {
                    param.put("direction", dto.getPageMode().getDirection());
                }
                if (StringUtil.isNotEmpty(dto.getPageMode().getSort())) {
                    param.put("sort", dto.getPageMode().getSort());
                }
                if (StringUtil.isNotEmpty(dto.getPageMode().getPageNo())) {
                    param.put("pageNo", dto.getPageMode().getPageNo());
                }
                if (StringUtil.isNotEmpty(dto.getPageMode().getPageSize())) {
                    param.put("pageSize", dto.getPageMode().getPageSize());
                }
            }
            Page<RepoDealTransactionDetailsDto> page = ibuQuoteRepoService.listDealTransactionDetails(param);
            return new Message<Page<RepoDealTransactionDetailsDto>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询交易执行明细信息异常", e);
            return new Message<Page<RepoDealTransactionDetailsDto>>().fail().setMessage("查询交易执行明细信息异常");
        }
    }

    @ApiOperation(value = "查看信息界面", notes = "根据交易编号查询登记查看信息界面")
    @PostMapping("/queryViewPage/{quoteId}")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> queryViewPage(@PathVariable("quoteId") String quoteId) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.queryViewPage(quoteId);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }

    @ApiOperation(value = "当日登记文件下载", notes = "当日登记文件下载")
    @PostMapping("/downDayReg")
    @ResponseBody
    public void downDayReg(@RequestBody Map<String,String> param, HttpServletResponse response){
        ExcelUitl.downFile(response,new File(param.get("filePath")));
    }

    @ApiOperation(value = "查询当日登记列表分页数据", notes = "查询当日登记列表分页数据")
    @PostMapping("/pageDayReg")
    @ResponseBody
    public Message<Page<CfetsRepoDayRegDto>> pageDayReg(@RequestBody RepoEntrustRequest dto) {
        try {
            Page<CfetsRepoDayRegDto> list = ibuQuoteRepoService.pageDayReg(dto);
            return new Message<Page<CfetsRepoDayRegDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询当日登记列表分页数据异常", e);
            return new Message<Page<CfetsRepoDayRegDto>>().fail().setMessage("查询当日登记列表分页数据异常");
        }
    }

    @ApiOperation(value = "当日登记导出", notes = "当日登记导出")
    @ResponseBody
    @RequestMapping("exportDataDayReg")
    public void exportDataDayReg(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "当日登记", () -> {
            RepoEntrustRequest repoEntrustRequest = requestDto.getDto(RepoEntrustRequest.class);
            List dataList = ibuQuoteRepoService.pageDayReg(repoEntrustRequest).getRoot();
            return dataList;
        });
    }

    @ApiOperation(value = "当日登记本币交易导出检查", notes = "当日登记本币交易导出检查")
    @PostMapping("/exportDayRegCheck")
    @ResponseBody
    public Message<Map<String, Object>> exportDayRegCheck(@RequestBody CfetsExportRequestDto requestDto) {
        Map<String, Object> param = new HashMap<>();
        List<String> quoteIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(requestDto.getData())) {
            for (BaseMap item : requestDto.getData()) {
                quoteIds.add((String) item.get("quoteId"));
            }
            param.put("quoteIds", quoteIds);
        }
        //目前只导出对话登记
        //param.put("quoteTypes", Arrays.asList("01"));
        List<CfetsRepoDayRegDto> dataList = ibuQuoteRepoService.listDayReg(param);
        return new Message<Map<String, Object>>().success().setData(ibuQuoteRepoService.exportDayRegCheck(dataList));
    }

    @ApiOperation(value = "当日登记本币交易导出", notes = "当日登记本币交易导出")
    @PostMapping("/exportDayReg")
    @ResponseBody
    public Message<List<Map<String, String>>> exportDayReg(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        List<String> quoteIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(requestDto.getData())) {
            for (BaseMap item : requestDto.getData()) {
                quoteIds.add((String) item.get("quoteId"));
            }
            param.put("quoteIds", quoteIds);
        }
        if (null != requestDto.getParams()) {
            String operationFlag = (String) requestDto.getParams().get("operationFlag");
            param.put("operationFlag", operationFlag);
        }
        //目前只导出对话登记
        param.put("quoteTypes", Arrays.asList("01"));
        List<CfetsRepoDayRegDto> dataList = ibuQuoteRepoService.listDayRegExport(param);
        String ibuExportCountMax = CacheUtil.getSysParam("IBU_EXPORT_COUNT_MAX");
        String issueTypeStr = "质押式回购";
        String prefix = StringUtil.join("本币交易导出_", iPlatformDate.getSysDateStr(), "_");
        requestDto.setData(null);
        //获取机构信息
        Map<String, String> customerMap = ibuQuoteRepoService.selectAllCustomer();

        //导出数据处理
        if (CollectionUtils.isNotEmpty(dataList)) {
            dataList.forEach(item -> {
                //债券代码不需要后缀
                if (StringUtil.isNotEmpty(item.getFinprodMarketId())) {
                    item.setFinprodMarketId(item.getFinprodMarketId().substring(0, item.getFinprodMarketId().indexOf(".")));
                }
                //清算速度不需要显示T，只需要显示+0或+1
                if (StringUtil.isNotEmpty(item.getSettleSpeedStr())) {
                    item.setSettleSpeedStr(item.getSettleSpeedStr().substring(item.getSettleSpeedStr().indexOf("+"), item.getSettleSpeedStr().length()));
                }
                //回购方式
                if (item.getRepoType() != null) {
                    item.setRepoTypeStr(DictCodeListExt3.from(DictCode.回购方式, item.getRepoType()).getCodeName());
                }

                //对手方
                item.setCpPartyName(null);
                if (!customerMap.isEmpty() && StringUtil.isNotEmpty(item.getPartyId())) {
                    item.setCpPartyName(customerMap.get(item.getPartyId()));
                }

            });
        }

        //导出数据必填项校验
        String message = checkExcelNotNull(requestDto, dataList);

        List<File> files = new ArrayList<>();
        if (StringUtil.isNotEmpty(ibuExportCountMax) && CollectionUtils.isNotEmpty(dataList)) {
            int max = Integer.parseInt(ibuExportCountMax);
            int fileCount = (dataList.size() + max - 1) / max;
            List<List<CfetsRepoDayRegDto>> collect = Stream.iterate(0, n -> n + 1).limit(fileCount).map(
                    a -> dataList.stream().skip(a * max).limit(max).collect(Collectors.toList())
            ).collect(Collectors.toList());
            for (int i = 0; i < collect.size(); i++) {
                List<CfetsRepoDayRegDto> sfetsRepoDayRegDtos = collect.get(i);
                //获取递增序号
                Map<String, String> sequenceIndex = new HashMap<>();
                sequenceIndex.put("databaseType", "Oracle");
                sequenceIndex.put("sequenceName", "DAYREGEXPORT_REPO_SEQUENCE");
                Long sequenceIndexData = tamsBaseMapper.getSequenceIndexData(sequenceIndex);
                if (null == sequenceIndexData) {
                    sequenceIndexData = 1L;
                }
                String quoteTypeStr = StringUtil.isNotEmpty(sfetsRepoDayRegDtos.get(0).getQuoteTypeStr()) ? "对话报价" : sfetsRepoDayRegDtos.get(0).getQuoteTypeStr();
                String appendString = collect.size() >= 1 ? String.format("%04d", sequenceIndexData) : StringUtil.EMPTY;
                String excelAndSheetName = StringUtil.join(issueTypeStr, quoteTypeStr, prefix, appendString);
                files.add(ExcelTplUitl.createFileRepo(requestDto, excelAndSheetName, "本币交易", () -> {
                    return sfetsRepoDayRegDtos;
                }));
            }
        } else {
            String quoteTypeStr = "对话报价";
            //获取递增序号
            Map<String, String> sequenceIndex = new HashMap<>();
            sequenceIndex.put("databaseType", "Oracle");
            sequenceIndex.put("sequenceName", "DAYREGEXPORT_REPO_SEQUENCE");
            Long sequenceIndexData = tamsBaseMapper.getSequenceIndexData(sequenceIndex);
            if (null == sequenceIndexData) {
                sequenceIndexData = 1L;
            }
            String excelAndSheetName = StringUtil.join(issueTypeStr, quoteTypeStr, prefix, String.format("%04d", sequenceIndexData));
            files.add(ExcelTplUitl.createFileRepo(requestDto, excelAndSheetName, "本币交易", () -> {
                return dataList;
            }));
        }
        //处理导出次数
        if (CollectionUtils.isNotEmpty(dataList)) {
            ibuQuoteRepoService.handExportTimes(dataList);
        }

        for (File file : files) {
            Map<String, String> map = new HashMap<>();
            map.put("fileName", file.getName());
            map.put("filePath", file.getAbsolutePath());
            result.add(map);
        }
        return new Message<List<Map<String, String>>>().success().setMessage(message).setData(result);

    }

    /**
     * 导出数据必填项校验
     * @param requestDto
     * @param dataList
     * @return
     */
    private String checkExcelNotNull(CfetsExportRequestDto requestDto, List<CfetsRepoDayRegDto> dataList) {
        String message = StringUtil.EMPTY;
        List<String> headList =  requestDto.getHeader().stream().filter(head -> head.getString("colName").contains("*"))
                .map(m -> m.getString("colCode")).collect(Collectors.toList());
        for(CfetsRepoDayRegDto dto:dataList){
            //交易方向为空的数据不校验
            if (StringUtil.isEmpty(dto.getPs())) {
                break;
            }
            BaseMap map  = BaseMap.newMap(BeanUtil.object2Map(dto));
            for (String head:headList) {
                if(map.get(head) == null || StringUtil.isEmpty(map.get(head).toString())){
                    return  "导出数据必填项存在空值，请手动添加";
                }
            }
        }

        return message;
    }


    @ApiOperation(value = "接收指令(批量)", notes = "接收指令(批量,key=entrustIds 传数组)")
    @PostMapping("receiveEntrust")
    @ResponseBody
    public Message<AjaxResult> recallDelegate(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result = new AjaxResult();
            List<ValidMessage> validMessages = ibuQuoteRepoService.receiveEntrust(param);
            result.setErrorList(validMessages);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("接收指令(批量)异常", e);
            return new Message<AjaxResult>().fail().setMessage("接收指令(批量)异常");
        }
    }

    @ApiOperation(value = "放弃委托(批量)", notes = "放弃委托(批量,key=entrustIds 传字符串用逗号隔开)")
    @PostMapping("giveUpEntrust")
    @ResponseBody
    public Message<AjaxResult> entrustGiveUpHandle(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result = new AjaxResult();
            List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
            entrustIds.forEach(item -> {
                String massge = ibuQuoteRepoService.giveUpEntrust(item);
                if (StringUtil.isNotEmpty(massge)) {
                    result.appendMessage(new ValidMessage(massge));
                    return;
                }
            });
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("放弃指令异常", e);
            return new Message<AjaxResult>().fail().setMessage("放弃指令异常");
        }
    }

    @ApiOperation(value = "退回校验", notes = "退回校验(批量,key=entrustIds指令代码)")
    @PostMapping("entrustRefuceCheck")
    @ResponseBody
    public Message<AjaxResult> entrustRefuceCheck(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result = new AjaxResult();
            if (StringUtil.isNotEmpty(param.get("entrustId"))) {
                param.put("entrustIdList", StringUtil.splitToList(",", (String) param.get("entrustId")));
                param.remove("entrustId");
            }
            result = ibuQuoteRepoService.entrustRefuceCheck(param, false);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("指令退回异常", e);
            return new Message<AjaxResult>().fail().setMessage("指令退回异常");
        }
    }

    @ApiOperation(value = "指令退回(批量)", notes = "指令退回(批量,key=entrustIds指令代码, key=remark退回原因)")
    @PostMapping("entrustRefuce")
    @ResponseBody
    public Message<AjaxResult> entrustRefuce(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result = new AjaxResult();
            if (StringUtil.isNotEmpty(param.get("entrustId"))) {
                param.put("entrustIdList", StringUtil.splitToList(",", (String) param.get("entrustId")));
                param.remove("entrustId");
            }
            result = ibuQuoteRepoService.entrustRefuceCheck(param, true);
            if (CollectionUtils.isNotEmpty(result.getErrorList())) {
                return new Message<AjaxResult>().success().setData(result);
            }
            ibuQuoteBondService.entrustRefuce(param);
            return new Message<AjaxResult>().success().setMessage("退回成功");
        } catch (Exception e) {
            logger.error("指令退回异常", e);
            return new Message<AjaxResult>().fail().setMessage("指令退回异常");
        }
    }

    @ApiOperation(value = "强制完成检查", notes = "强制完成检查")
    @ResponseBody
    @RequestMapping("/doForcedFinishCheck")
    public Message<AjaxResult> doForcedFinishCheck(@RequestBody Map<String, Object> param) {
        BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
        AjaxResult ar = null;
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        param.put("entrustIdList", entrustIds);
        param.remove("entrustIds");
        return new Message<AjaxResult>().success().setData(ibuQuoteRepoService.doForcedFinishCheck(param, false));
    }


    @ApiOperation(value = "强制完成", notes = "强制完成")
    @ResponseBody
    @RequestMapping("doForcedFinish")
    public Message<AjaxResult> doForcedFinish(@RequestBody Map<String, Object> param) {
        AjaxResult ar;
        List<String> entrustIds = StringUtil.splitToList(",", (String) param.get("entrustIds"));
        param.put("entrustIdList", entrustIds);
        param.remove("entrustIds");
        ar = ibuQuoteRepoService.doForcedFinishCheck(param, true);
        if (CollectionUtils.isNotEmpty(ar.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ar);
        }
        try {
            ibuQuoteRepoService.doForcedFinish(param);
            ar = new AjaxResult(this.getMessage("强制完成成功"));
        } catch (Exception e) {
            logger.error("强制完成异常", e);
            ar = new AjaxResult(this.getMessage("强制完成异常"));
        }
        return new Message<AjaxResult>().success().setData(ar);
    }

    @ApiOperation(value = "组合分配汇总计算", notes = "组合分配汇总计算交易金额")
    @PostMapping("/ptlAllocationCal")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> ptlAllocationCal(@RequestBody RepoAutoAllocationRequest request) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.ptlAllocationCal(request);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }

    @ApiOperation(value = "交易金额变动联动计算", notes = "交易金额变动联动[根据][交易方向][期限品种][质押券数量][回购利率][清算速度][回购期限][质押券信息]计算[金额信息][费用][组合分配]")
    @PostMapping("/tradeAmtChange")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> tradeAmtChange(@RequestBody RepoAutoAllocationRequest request) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.tradeAmtChange(request);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }

    @ApiOperation(value = "查询可用质押券信息(正回购)", notes = "添加质押券按钮初始化及查询,根据已选质押券、指令代码、查询条件,指定债券或指定债券库(系统配置)查询质押债券信息")
    @PostMapping("/pledgesAdd")
    @ResponseBody
    public Message<Page<RepoEntrustPledgeDto>> pledgesAdd(@RequestBody RepoPledgeRequest repoPledgeRequest) {
        try {
            Page<RepoEntrustPledgeDto> list = ibuQuoteRepoService.pledgesAdd(repoPledgeRequest);
            return new Message<Page<RepoEntrustPledgeDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询可用质押券信息分页数据异常", e);
            return new Message<Page<RepoEntrustPledgeDto>>().fail().setMessage("查询可用质押券信息分页数据异常");
        }
    }

    @ApiOperation(value = "查询可用质押券信息(逆回购)", notes = "逆回购 添加质押券按钮初始化及查询,根据已选质押券、指令代码、查询条件,指定债券或指定债券库(系统配置)查询质押债券信息")
    @PostMapping("/pledgesAdd04")
    @ResponseBody
    public Message<Page<RepoEntrustPledgeDto>> pledgesAdd04(@RequestBody RepoPledgeRequest repoPledgeRequest) {
        try {
            Page<RepoEntrustPledgeDto> list = ibuQuoteRepoService.pledgesAdd04(repoPledgeRequest);
            return new Message<Page<RepoEntrustPledgeDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询可用质押券信息分页数据异常", e);
            return new Message<Page<RepoEntrustPledgeDto>>().fail().setMessage("查询可用质押券信息分页数据异常");
        }
    }

    @ApiOperation(value = "组合分配、质押券联动", notes = "添加、删除 组合分配、质押券信息后联动计算")
    @PostMapping("/ptlAndpledgeEdit")
    @ResponseBody
    public Message<CfetsRepoDealQuoteCommonDto> ptlAndpledgeEdit(@RequestBody RepoAutoAllocationRequest request) {
        CfetsRepoDealQuoteCommonDto cfetsRepoDealQuoteCommonDto = ibuQuoteRepoService.ptlAndPledgeEdit(request);
        return new Message<CfetsRepoDealQuoteCommonDto>().success().setData(cfetsRepoDealQuoteCommonDto);
    }

    @ApiOperation(value = "质押券保存校验", notes = "根据质押券信息,到期结算日,交易方向 校验数据")
    @ResponseBody
    @RequestMapping("/doSavePledgesCheck")
    public Message<AjaxResult> doSavePledgesCheck(@RequestBody RepoAutoAllocationRequest request) {
        AjaxResult ajaxResult = ibuQuoteRepoService.doSavePledgesCheck(request);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ApiOperation(value = "当日报价", notes = "当日报价")
    @PostMapping("/listDayPrice")
    @ResponseBody
    public Message<com.joyintech.background.component.common.pack.common.grid.Page<CfetsRepoDayPriceDto>> pageDayPrice(@RequestBody RepoEntrustRequest dto) {
        try {
            com.joyintech.background.component.common.pack.common.grid.Page<CfetsRepoDayPriceDto> result = cfetsRepoDealQuotedPrice002Service.listDayPricePage(dto);
            return new Message<com.joyintech.background.component.common.pack.common.grid.Page<CfetsRepoDayPriceDto>>().success().setData(result);
        } catch (Exception e) {
            logger.error("查询当日报价数据异常", e);
            return new Message<com.joyintech.background.component.common.pack.common.grid.Page<CfetsRepoDayPriceDto>>().fail().setMessage("查询当日报价数据异常");
        }
    }

    @ApiOperation(value = "当日报价导出", notes = "当日报价导出")
    @ResponseBody
    @RequestMapping("/exportDayPrice")
    public void exportDayPrice(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "当日报价", () -> {
            RepoEntrustRequest dto = requestDto.getDto(RepoEntrustRequest.class);
            List dataList = cfetsRepoDealQuotedPrice002Service.listDayPrice(dto);
            return dataList;
        });
    }

    @ResponseBody
    @PostMapping("/getEntrustDet/{pageCode}/{dtCode}/{spcdCodes}")
    @ApiOperation(value = "只读表单设置及数据查询_指令详情信息", notes = "只读表单设置及数据查询_指令详情信息")
    public Message<Map<String, Object>> getEntrustDet(@PathVariable("pageCode") String pageCode,
                                                      @PathVariable("dtCode") String dtCode,
                                                      @PathVariable("spcdCodes") String spcdCodes,
                                                      @RequestBody CfetsRepoDealQuoteRequest request) {
        try {
            Map<String, Object> resultData = ibuQuoteRepoService.findEntrustDet(pageCode, dtCode, spcdCodes, request);
            return new Message<Map<String, Object>>().success().setData(resultData);
        } catch (Exception e) {
            logger.error("查询指令详情信息明细数据异常", e);
            return new Message<Map<String, Object>>().fail().setMessage("查询指令详情信息明细异常");
        }
    }
}
