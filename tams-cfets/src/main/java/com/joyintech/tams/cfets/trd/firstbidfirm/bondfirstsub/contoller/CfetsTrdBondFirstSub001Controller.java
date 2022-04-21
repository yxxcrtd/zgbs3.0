package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.contoller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.trd.bond.dto.RiskCalRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub001Service;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.client.ClientData;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级申购 初始化页面
 * @author jp
 * @date 2022/04/18
 */
@Controller
@PageId("TenderBondSub")
@RequestMapping("/cfets/finbond/tenderBondSub/TenderBondSub")
@Api(tags = "CFETS一级申购-常用查询类接口")
public class CfetsTrdBondFirstSub001Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub001Controller.class);

    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    @Autowired
    private CfetsTrdBondFirstSub001Service cfetsTrdBondFirstSub001Service;

    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @Autowired
    private IPlatformDate iPlatformDate;

    @ApiOperation(value = "初始化参数", notes = "初始化参数")
    @PostMapping("/initParams")
    @ResponseBody
    public Message<Map<String, Object>> initParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        //CMDS地址
        initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
        //刷新时间
        initPageParams.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        initPageParams.put("effDate", CacheUtil.getSysParam("ibuVar_quoteEffTime"));
        return new Message<>(initPageParams).success();
    }

    @ApiOperation(value = "查询指令列表分页数据", notes = "查询指令列表分页数据")
    @PostMapping("/pageEntrust")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubEntrustDto>> pageEntrust(@RequestBody CfetsTrdBondFirstSubEntrustRequest dto) {
        try {
            Page<CfetsTrdBondFirstSubEntrustDto> list = cfetsTrdBondFirstSub001Service.pageEntrust(dto);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询指令列表分页数据异常", e);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().fail().setMessage("查询指令列表分页数据异常");
        }
    }

    @ApiOperation(value = "接收指令(批量)", notes = "接收指令(批量,key=entrustIds 传字符串用逗号隔开)")
    @PostMapping("receiveEntrust")
    @ResponseBody
    public Message<AjaxResult> recallDelegate(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result = new AjaxResult();
            List<ValidMessage> validMessages = cfetsTrdBondFirstSub001Service.receiveEntrust(param);
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
                String massge = cfetsTrdBondFirstSub001Service.giveUpEntrust(item);
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

    @ApiOperation(value = "指令退回(批量)", notes = "指令退回(批量,key=entrustIds指令代码 传字符串用逗号隔开, key=remark 退回原因)")
    @PostMapping("entrustRefuce")
    @ResponseBody
    public Message<AjaxResult> entrustRefuce(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result;
            if (StringUtil.isNotEmpty(param.get("entrustId"))) {
                param.put("entrustIdList", StringUtil.splitToList(",", (String) param.get("entrustIds")));
            }
            result = cfetsTrdBondFirstSub001Service.entrustRefuceCheck(param);
            if (CollectionUtils.isNotEmpty(result.getErrorList())) {
                return new Message<AjaxResult>().success().setData(result);
            }
            cfetsTrdBondFirstSub001Service.entrustRefuce(param);
            return new Message<AjaxResult>().success().setMessage("退回成功");
        } catch (Exception e) {
            logger.error("指令退回异常", e);
            return new Message<AjaxResult>().fail().setMessage("指令退回异常");
        }
    }

    @ApiOperation(value = "查询指令详细信息", notes = "查询指令详细信息")
    @PostMapping("/getEntrust/{entrustId}")
    @ResponseBody
    public Message<Map<String, Object>> getEntrust(@PathVariable("entrustId") String entrustId) {
        try {
            Map<String, Object> result = cfetsTrdBondFirstSub001Service.getEntrustInfo(entrustId);
            return new Message<Map<String, Object>>().success().setData(result);
        } catch (Exception e) {
            logger.error("查询指令详细信息异常", e);
            return new Message<Map<String, Object>>().fail().setMessage("查询指令详细信息异常");
        }
    }

    @ApiOperation(value = "申购初始化检查", notes = "申购初始化检查")
    @PostMapping("/dealexecutioncheck")
    @ResponseBody
    public Message<AjaxResult> dealexecutioncheck(@RequestBody CfetsTrdBondFirstSubInitRequest request) {
        AjaxResult ajaxResult = cfetsTrdBondFirstSub001Service.dealexecutioncheck(request);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }


    @ApiOperation(value = "申购交易执行初始化", notes = "申购交易执行初始化")
    @PostMapping("/dealexecutioninit")
    @ResponseBody
    public Message<CfetsTrdBondFirstSubInfoDto> dealexecutioninit(@RequestBody CfetsTrdBondFirstSubInitRequest request) {
        CfetsTrdBondFirstSubInfoDto cfetsBondDealQuoteCommonDto = cfetsTrdBondFirstSub001Service.queryEntrustInfoById(request);
        return new Message<CfetsTrdBondFirstSubInfoDto>().success().setData(cfetsBondDealQuoteCommonDto);
    }

    @ApiOperation(value = "指令查询明细添加", notes = "指令查询明细添加")
    @PostMapping("/etrSearch")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubEntrustDto>> etrSearch(@RequestBody CfetsTrdBondFirstSubEntrustRequest dto) {
        try {
            Page<CfetsTrdBondFirstSubEntrustDto> list = cfetsTrdBondFirstSub001Service.pageEntrust(dto);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("指令查询明细添加异常", e);
            return new Message<Page<CfetsTrdBondFirstSubEntrustDto>>().fail().setMessage("指令查询明细添加异常");
        }
    }


    @ApiOperation(value = "自动分配", notes = "自动分配")
    @PostMapping("/autoAllocation")
    @ResponseBody
    public Message<List<CfetsTrdBondFirstSubProductDto>> autoAllocation(@RequestBody CfetsTrdBondFirstSubInfoDto autoRequest) {
        try {
            List<CfetsTrdBondFirstSubProductDto> list = cfetsTrdBondFirstSub001Service.autoAllocation(autoRequest);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("自动分配异常", e);
            return new Message<List<CfetsTrdBondFirstSubProductDto>>().fail().setMessage("自动分配异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算check", notes = "风险试算check")
    @PostMapping("doRiskcalCheck")
    public Message<AjaxResult> doRiskcalCheck(@RequestBody RiskCalRequest riskCalRequest) {
        AjaxResult ajaxResult = cfetsTrdBondFirstSub001Service.riskCheckNotEmpty(riskCalRequest);
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody RiskCalRequest riskCalRequest) {
        CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
        try {
            AjaxResult ajaxResult = cfetsTrdBondFirstSub001Service.riskCheckNotEmpty(riskCalRequest);
            if (CollectionUtils.isNotEmpty(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }
            Map<String, Object> dataMap = cfetsTrdBondFirstSub001Service.doRiskcalPackage(riskCalRequest);
            List<String> resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "0", "RSK002", "F01");
            result.setRiskMathFlag("02");
            Boolean forbidFlag = false;
            Boolean cluflag = true;
            //是否全部正常
            Boolean allNormalFlag = true;
            List<CfetsEtribdoRiskcalResponse> resultlist = new ArrayList<>();
            if (!StringUtil.isEmptyList(resultArrList)) {
                for (String row : resultArrList) {
                    ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
                    if (StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
                            && StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
                    ) {
                        if (StringUtil.equals(resultArr.getHead().get("std400msgt"), "E")) {
                            return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
                        }
                    }
                    if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
                            || !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
                        return new Message<AjaxResult>().success().setMessage("无");
                    }
                    List<Map<String, Object>> riskList = (List<Map<String, Object>>) resultArr.getBody().get("RSK_LIST");
                    for (Map<String, Object> map : riskList) {
                        if (allNormalFlag && !StringUtil.equals(map.get("kriStatus"), "0")) {
                            allNormalFlag = false;
                        }
                        if (StringUtil.equals(map.get("kriStatus"), "0") && !StringUtil.equals(map.get("kriTrend"), "向好")) {
                            forbidFlag = true;
                        }
//                        map.put("kriStatusCn", cfetsEtrIbBondinfoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
                        resultlist.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
                    }
                    if (!allNormalFlag) {
                        result.setRiskcalReturnList(resultlist);
                    }
                    result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
                    //当置为不通过时就不再进来了
                    if (cluflag) {
                        if (!StringUtil.equals(resultArr.getBody().get("rskStatus"), "3")) {
                            if (StringUtil.equals(resultArr.getBody().get("rskStatus"), "4") && forbidFlag) {
                                cluflag = false;
                                resultArr.getBody().put("rskStatus", "3");
                                result.setRskStatus("3");
                            }
                        } else {
                            cluflag = false;
                        }
                        if (!cluflag) {
                            result.setRiskTrialSpan("试算不通过");
                        } else {
                            result.setRiskTrialSpan("试算通过");
                        }
                    }

                }

            }
            AjaxResult m = new AjaxResult();
            m.setData(result);
            return new Message<AjaxResult>().success().setData(m);
        } catch (Exception e) {
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }
    }

    @ApiOperation(value = "当日申购", notes = "查询当日申购列表分页数据,按证券账户+资产代码维度")
    @PostMapping("/pageDaySub")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstDaySubDto>> pageDaySub(@RequestBody CfetsTrdBondFirstDaySubRequest dto) {
        try {
            Page<CfetsTrdBondFirstDaySubDto> list = cfetsTrdBondFirstSub001Service.pageDaySub(dto);
            return new Message<Page<CfetsTrdBondFirstDaySubDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询当日申购列表分页数据异常", e);
            return new Message<Page<CfetsTrdBondFirstDaySubDto>>().fail().setMessage("查询当日申购列表分页数据异常");
        }
    }


    @ApiOperation(value = "当日申购导出", notes = "当日申购导出")
    @ResponseBody
    @RequestMapping("exportDaySub")
    public void exportDaySub(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "当日登记", () -> {
            Map<String, Object> param = requestDto.getDto(Map.class);
            List dataList = cfetsTrdBondFirstSub001Service.listDaySub(param);
            return dataList;
        });
    }

    @ApiOperation(value = "申购明细", notes = "查询申购明细列表分页数据,按证券账户+资产代码维度查询")
    @PostMapping("/pageDaySubDetail")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubInfoDto>> pageDaySubDetail(@RequestBody CfetsTrdBondFirstDaySubRequest dto) {
        try {
            Page<CfetsTrdBondFirstSubInfoDto> list = new Page<>();
            return new Message<Page<CfetsTrdBondFirstSubInfoDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询申购明细列表分页数据异常", e);
            return new Message<Page<CfetsTrdBondFirstSubInfoDto>>().fail().setMessage("查询申购明细列表分页数据异常");
        }
    }

    @ApiOperation(value = "申购执行明细", notes = "查询申购执行明细分页数据,按指令代码查询")
    @PostMapping("/pageDaySubDealDetail")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubDealDetailDto>> pageDaySubDealDetail(@RequestBody CfetsTrdBondFirstDaySubRequest dto) {
        try {
            Page<CfetsTrdBondFirstSubDealDetailDto> list = new Page<>();
            return new Message<Page<CfetsTrdBondFirstSubDealDetailDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询申购执行明细分页数据异常", e);
            return new Message<Page<CfetsTrdBondFirstSubDealDetailDto>>().fail().setMessage("查询申购执行明细分页数据异常");
        }
    }

    @ApiOperation(value = "计算", notes = "编辑联动计算")
    @PostMapping("/calData")
    @ResponseBody
    public Message<CfetsTrdBondFirstSubEntrustDto> calData(@RequestBody CfetsTrdBondFirstSubEntrustDto dto) {
        try {
            CfetsTrdBondFirstSubEntrustDto result = new CfetsTrdBondFirstSubEntrustDto();
            return new Message<CfetsTrdBondFirstSubEntrustDto>().success().setData(result);
        } catch (Exception e) {
            logger.error("计算数据异常", e);
            return new Message<CfetsTrdBondFirstSubEntrustDto>().fail().setMessage("计算数据异常");
        }
    }

}
