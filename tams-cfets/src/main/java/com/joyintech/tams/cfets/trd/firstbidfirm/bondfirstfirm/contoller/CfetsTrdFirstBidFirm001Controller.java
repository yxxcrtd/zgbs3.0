package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.contoller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalRequest;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bondfirstbid.controller.CfetsEtrBondFirstBid001Controller;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmEntrustRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmEtrRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdBondFirstBidFirmUndoSumRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmDistributionInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmDistributionInfoRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmEntrustDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmTenderInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmTenderInfoRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.service.CfetsTrdFirstBidFirm001Service;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 一级投标确认 初始化页面
 *
 * @Author: kchen
 * @Date: 2022/5/14 11:07
 */
@Controller
@PageId("CfetsTrdFirstBidFirm001")
@RequestMapping("/cfets/finbond/tenderBondSub/CfetsTrdFirstBidFirm001")
@Api(tags = "CFETS一级投标确认-常用查询类接口")
public class CfetsTrdFirstBidFirm001Controller extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrBondFirstBid001Controller.class);

    @Autowired
    private CfetsTrdFirstBidFirm001Service cfetsTrdFirstBidFirm001Service;
    /**
     * 风险接口组件Controller
     */
    @Autowired
    private InterfRiskRdsController interfRiskRdsController;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        //数据字典初始化数据
        // 有效状态
        initPageParams.put("ISSUE_STATUS", this.getSelectDataFromDataDic(DictCode.发行状态, null));
        // 发行状态
        initPageParams.put("STATUS_VALID", this.getSelectDataFromDataDic(DictCode.有效状态, null));
        // 债券类型
        initPageParams.put("BOND_TYPE", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        // 委托状态_待接收
        initPageParams.put("ENTRUST_STATUS_02", DictCodeList.委托状态_待接收.getValue());
        // 委托状态_已接收
        initPageParams.put("ENTRUST_STATUS_03", DictCodeList.委托状态_已接收.getValue());
        // 委托状态_已完成
        initPageParams.put("ENTRUST_STATUS_04", DictCodeList.委托状态_已完成.getValue());
        // 委托状态_已退回
        initPageParams.put("ENTRUST_STATUS_05", DictCodeList.委托状态_已退回.getValue());
        // 委托状态_已关闭
        initPageParams.put("ENTRUST_STATUS_06", DictCodeList.委托状态_已关闭.getValue());
        initPageParams.put("FIRST_BID_STATUS", this.getSelectDataFromDataDic(DictCode.一级投标中标状态, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("TENDER_TARGET", this.getSelectDataFromDataDic(DictCode.招标标的, null));
        // 招标标的_利率
        initPageParams.put("TENDER_TARGET_01", DictCodeListExt4.招标标的_利率.getValue());
        // 招标标的_利差
        initPageParams.put("TENDER_TARGET_02", DictCodeListExt4.招标标的_利差.getValue());
        // 招标标的_价格
        initPageParams.put("TENDER_TARGET_03", DictCodeListExt4.招标标的_价格.getValue());
        // 缴款方式
        initPageParams.put("PAY_TYPE", this.getSelectDataFromDataDic(BussinessDictCode.缴款方式, null));
        initPageParams.put("PAY_TYPE_01", BussinessDictCodeList.缴款方式_一级分销.getValue());
        initPageParams.put("PAY_TYPE_02", BussinessDictCodeList.缴款方式_一级缴款.getValue());
        initPageParams.put("PAY_TYPE_03", BussinessDictCodeList.缴款方式_二级交易.getValue());
        // 投标方式
        initPageParams.put("biddingMethod", this.getSelectDataFromDataDic(DictCode.投标方式, null));
        initPageParams.put("biddingMethod_01", DictCodeListExt4.投标方式_自主投标.getValue());
        initPageParams.put("biddingMethod_02", DictCodeListExt4.投标方式_代投代缴.getValue());
        initPageParams.put("biddingMethod_03", DictCodeListExt4.投标方式_分销买入.getValue());
        // 调整策略
        initPageParams.put("ADJUST_STRATEGY", this.getSelectDataFromDataDic(DictCode.调整策略, null));

        // 获取平台日
        Date tradeDate = DateUtil.platDateFromCache();
        initPageParams.put("platDate", tradeDate);
        // 币种
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        // 指令列表刷新时间
        initPageParams.put("cfetsTrdFirstBidFirm_etrTable_refresh_period", CacheUtil.getSysParam("cfetsTrdFirstBidFirm_etrTable_refresh_period"));
        // 投标确认列表刷新时间
        initPageParams.put("cfetsTrdFirstBidFirm_tender_refresh_period", CacheUtil.getSysParam("cfetsTrdFirstBidFirm_tender_refresh_period"));
        // 分销买入列表刷新时间
        initPageParams.put("cfetsTrdFirstBidFirm_distribution_refresh_period", CacheUtil.getSysParam("cfetsTrdFirstBidFirm_distribution_refresh_period"));
        // 投标执行明细列表刷新时间
        initPageParams.put("cfetsTrdFirstBidFirm_details_refresh_period", CacheUtil.getSysParam("cfetsTrdFirstBidFirm_details_refresh_period"));

        // 系统参数 【是否进行指令汇总(Y:是，N:否)_一级投标】
        initPageParams.put("IS_ENTRUST_SUM_FLAG_BID", CacheUtil.getSysParam("IS_ENTRUST_SUM_FLAG_BID"));
        //当前用户
        initPageParams.put("userId", SecurityUtil.getLoginUser().getUserName());
        //pageId
        initPageParams.put("pageId", "CfetsTrdFirstBidFirm002");
        return new Message<Map<String, Object>>(initPageParams).success();

    }

    @ApiOperation(value = "查询指令列表分页数据", notes = "查询指令列表分页数据")
    @PostMapping("/pageEntrust")
    @ResponseBody
    public Message<List<CfetsTrdFirstBidFirmEntrustDto>> pageEntrust(@RequestBody CfetsTrdBondFirstBidFirmEntrustRequest dto) {
        try {
            List<CfetsTrdFirstBidFirmEntrustDto> list = cfetsTrdFirstBidFirm001Service.listEntrust(dto);
            return new Message<List<CfetsTrdFirstBidFirmEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询指令列表分页数据异常", e);
            return new Message<List<CfetsTrdFirstBidFirmEntrustDto>>().fail().setMessage("查询指令列表分页数据异常");
        }
    }

    @ApiOperation(value = "查询指令列表汇总数据", notes = "查询指令列表汇总数据")
    @PostMapping("/pageSumEntrust")
    @ResponseBody
    public Message<List<CfetsTrdFirstBidFirmEntrustDto>> pageSumEntrust(@RequestBody CfetsTrdBondFirstBidFirmEntrustRequest dto) {
        try {
            List<CfetsTrdFirstBidFirmEntrustDto> list = cfetsTrdFirstBidFirm001Service.pageSumEntrust(dto);
            return new Message<List<CfetsTrdFirstBidFirmEntrustDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询指令列表汇总数据异常", e);
            return new Message<List<CfetsTrdFirstBidFirmEntrustDto>>().fail().setMessage("查询指令列表汇总数据异常");
        }
    }

    @PostMapping(value = "/exportEtrSumInfo")
    @ResponseBody
    @ApiOperation(value = "一级投标确认指令管理汇总导出", notes = "一级投标确认指令管理汇总导出")
    public void exportEtrSumInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "一级投标确认指令管理汇总导出", () -> {
            List dataList = cfetsTrdFirstBidFirm001Service.pageSumEntrust(req.getDto(CfetsTrdBondFirstBidFirmEntrustRequest.class));
            return dataList;
        });
    }

    @PostMapping(value = "/exportEtrInfo")
    @ResponseBody
    @ApiOperation(value = "一级投标确认指令管理导出", notes = "一级投标确认指令管理导出")
    public void exportEtrInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "一级投标确认指令管理", () -> {
            List dataList = cfetsTrdFirstBidFirm001Service.pageEntrust(req.getDto(CfetsTrdBondFirstBidFirmEntrustRequest.class)).getRoot();
            return dataList;
        });
    }

    @ResponseBody
    @PostMapping("/undoSumSave")
    @ApiOperation(value = "指令取消汇总", notes = "指令取消汇总")
    public Message<AjaxResult> undoSumSave(@RequestBody CfetsTrdBondFirstBidFirmUndoSumRequest cfetsDto) {
        try {
            Integer num = cfetsTrdFirstBidFirm001Service.undoSumSave(cfetsDto);
            if (null != num) {
                return new Message<AjaxResult>().success();
            } else {
                return new Message<AjaxResult>().fail().setMessage("指令取消汇总保存失败");
            }
        } catch (Exception e) {
            logger.error("指令取消汇总保存异常", e);
            return new Message<AjaxResult>().fail().setMessage("指令取消汇总保存失败");
        }
    }

    @ResponseBody
    @PostMapping("/etrSumOperate")
    @ApiOperation(value = "指令汇总操作", notes = "指令汇总操作")
    public Message<AjaxResult> etrSumOperate(@RequestBody List<CfetsTrdFirstBidFirmEntrustDto> entrustDtoList) {
        try {
            AjaxResult result = new AjaxResult();
            List<ValidMessage> validMessages = cfetsTrdFirstBidFirm001Service.etrSumOperate(entrustDtoList);
            result.setErrorList(validMessages);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("指令汇总操作异常", e);
            return new Message<AjaxResult>().fail().setMessage("指令汇总操作失败");
        }
    }

    @ApiOperation(value = "查询指令详细信息", notes = "查询指令详细信息")
    @PostMapping("/getEntrust/{entrustId}")
    @ResponseBody
    public Message<Map<String, Object>> getEntrust(@PathVariable("entrustId") String entrustId) {
        try {
            Map<String, Object> result = cfetsTrdFirstBidFirm001Service.getEntrustInfo(entrustId);
            return new Message<Map<String, Object>>().success().setData(result);
        } catch (Exception e) {
            logger.error("查询指令详细信息异常", e);
            return new Message<Map<String, Object>>().fail().setMessage("查询指令详细信息异常");
        }
    }

    @ApiOperation(value = "查询投标确认执行明细", notes = "查询投标确认执行明细")
    @PostMapping("/getBidOrderTransactionDetails/{entrustId}")
    @ResponseBody
    public Message<Map<String, Object>> getBidOrderTransactionDetails(@PathVariable("entrustId") String entrustId) {
        try {
            Map<String, Object> result = cfetsTrdFirstBidFirm001Service.getBidOrderTransactionDetails(entrustId);
            return new Message<Map<String, Object>>().success().setData(result);
        } catch (Exception e) {
            logger.error("查询投标确认执行明细异常", e);
            return new Message<Map<String, Object>>().fail().setMessage("查询投标确认执行明细异常");
        }
    }

    @ApiOperation(value = "接收指令(批量)检查", notes = "接收指令(批量,key=entrustIds 传字符串用逗号隔开)")
    @PostMapping("receiveEntrustCheck")
    @ResponseBody
    public Message<AjaxResult> receiveEntrustCheck(@RequestBody List<CfetsTrdBondFirstBidFirmEtrRequest> requestList) {
        try {
            AjaxResult result = cfetsTrdFirstBidFirm001Service.receiveEntrustCheck(requestList);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("接收指令(批量)检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("接收指令(批量)检查异常");
        }
    }

    @ApiOperation(value = "接收指令(批量)", notes = "接收指令(批量,key=entrustIds 传字符串用逗号隔开)")
    @PostMapping("receiveEntrust")
    @ResponseBody
    public Message<AjaxResult> recallDelegate(@RequestBody Map<String, Object> param) {
        try {
            AjaxResult result = new AjaxResult();
            List<ValidMessage> validMessages = cfetsTrdFirstBidFirm001Service.receiveEntrust(param);
            result.setErrorList(validMessages);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("接收指令(批量)异常", e);
            return new Message<AjaxResult>().fail().setMessage("接收指令(批量)异常");
        }
    }

    @ApiOperation(value = "放弃委托(批量)检查", notes = "放弃委托(批量,key=entrustIds 传字符串用逗号隔开)")
    @PostMapping("entrustGiveUpHandleCheck")
    @ResponseBody
    public Message<AjaxResult> entrustGiveUpHandleCheck(@RequestBody List<CfetsTrdBondFirstBidFirmEtrRequest> requestList) {
        try {
            AjaxResult result = cfetsTrdFirstBidFirm001Service.entrustGiveUpHandleCheck(requestList);
            return new Message<AjaxResult>().success().setData(result);
        } catch (Exception e) {
            logger.error("放弃委托(批量)检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("放弃委托(批量)检查异常");
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
                String massge = cfetsTrdFirstBidFirm001Service.giveUpEntrust(item);
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
            result = cfetsTrdFirstBidFirm001Service.entrustRefuceCheck(param);
            if (CollectionUtils.isNotEmpty(result.getErrorList())) {
                return new Message<AjaxResult>().success().setData(result);
            }
            cfetsTrdFirstBidFirm001Service.entrustRefuce(param);
            return new Message<AjaxResult>().success().setMessage("退回成功");
        } catch (Exception e) {
            logger.error("指令退回异常", e);
            return new Message<AjaxResult>().fail().setMessage("指令退回异常");
        }
    }

    @ApiOperation(value = "投标", notes = "投标查询")
    @PostMapping("/tenderSearch")
    @ResponseBody
    public Message<Page<CfetsTrdFirstBidFirmTenderInfoDto>> tenderSearch(@RequestBody CfetsTrdFirstBidFirmTenderInfoRequest request) {
        try {
            Page<CfetsTrdFirstBidFirmTenderInfoDto> list = cfetsTrdFirstBidFirm001Service.tenderSearch(request);
            return new Message<Page<CfetsTrdFirstBidFirmTenderInfoDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("投标查询异常", e);
            return new Message<Page<CfetsTrdFirstBidFirmTenderInfoDto>>().fail().setMessage("投标查询异常");
        }
    }

    @ApiOperation(value = "分销买入", notes = "分销买入查询")
    @PostMapping("/distributionSearch")
    @ResponseBody
    public Message<Page<CfetsTrdFirstBidFirmDistributionInfoDto>> distributionSearch(@RequestBody CfetsTrdFirstBidFirmDistributionInfoRequest request) {
        try {
            Page<CfetsTrdFirstBidFirmDistributionInfoDto> list = cfetsTrdFirstBidFirm001Service.distributionSearch(request);
            return new Message<Page<CfetsTrdFirstBidFirmDistributionInfoDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("分销买入查询异常", e);
            return new Message<Page<CfetsTrdFirstBidFirmDistributionInfoDto>>().fail().setMessage("分销买入查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody CfetsEtrIbdoRiskcalRequest dto) throws Exception {
        try {
            //风险试算check
            String message = cfetsTrdFirstBidFirm001Service.doRiskcalCheckMethod(dto);
            if (StringUtil.isNotEmpty(message)) {
                return new Message<AjaxResult>().fail().setMessage(message);
            }
            CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
            //风险试算组装dto
            Map<String, Object> dataMap = cfetsTrdFirstBidFirm001Service.doRiskcalPackageEquity(dto);
            List<String> resultArrList = interfRiskRdsController.doInfBatchData(dataMap, "0", "RSK002", "F01");
            result.setRiskMathFlag("02");
            Boolean forbidFlag = false;
            Boolean cluflag = true;
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
                        if (StringUtil.equals(map.get("kriStatus"), "0") && !StringUtil.equals(map.get("kriTrend"), "向好")) {
                            forbidFlag = true;
                        }
                        map.put("kriStatusCn", cfetsTrdFirstBidFirm001Service.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
                        resultlist.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
                    }
                    result.setRiskcalReturnList(resultlist);
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
            logger.error("风险试算异常", e);
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }
    }

}
