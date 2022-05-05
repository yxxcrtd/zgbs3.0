package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.contoller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.*;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.service.CfetsTrdBondFirstSub001Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级申购 初始化页面
 *
 * @author jp
 * @date 2022/04/18
 */
@Controller
@PageId("TenderBondSub001")
@RequestMapping("/cfets/finbond/tenderBondSub/TenderBondSub001")
@Api(tags = "CFETS一级申购-常用查询类接口")
public class CfetsTrdBondFirstSub001Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdBondFirstSub001Controller.class);

    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    @Autowired
    private CfetsTrdBondFirstSub001Service cfetsTrdBondFirstSub001Service;

    @Autowired
    private IPlatformDate iPlatformDate;

    @ApiOperation(value = "初始化参数", notes = "初始化参数")
    @PostMapping("/initParams")
    @ResponseBody
    public Message<Map<String, Object>> initParams() {
        Map<String, Object> initPageParams = new HashMap<>();
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
        // 系统日期
        initPageParams.put("sysDate", iPlatformDate.getSysDateStr());
        // 币种_人民币
        initPageParams.put("CCY_CNY", DictCodeList.币种_人民币_CNY.getValue());
        //CMDS地址
        initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
        // 系统参数 【是否支持CSTP下行接口_一级申购】
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_BONDFIRST", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST"));
        // 登记页面id 用于登记撤销
        initPageParams.put("PAGE_ID_BOND_FIRST", "TenderBondSub002");
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


    @ApiOperation(value = "指令列表导出", notes = "指令列表导出")
    @ResponseBody
    @PostMapping("exportEntrust")
    public void exportEntrust(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "指令列表", () -> {
            Map<String, Object> param = requestDto.getDto(Map.class);
            List dataList = cfetsTrdBondFirstSub001Service.listEntrust(param);
            return dataList;
        });
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
    @PostMapping("exportDaySub")
    public void exportDaySub(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "当日申购", () -> {
            Map<String, Object> param = requestDto.getDto(Map.class);
            List dataList = cfetsTrdBondFirstSub001Service.listDaySub(param);
            return dataList;
        });
    }

    @ApiOperation(value = "申购明细", notes = "查询申购明细列表分页数据,按证券账户+资产代码维度查询")
    @PostMapping("/pageDaySubDetail")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubDealDetailDto>> pageDaySubDetail(@RequestBody CfetsTrdBondFirstDaySubRequest dto) {
        try {
            Page<CfetsTrdBondFirstSubDealDetailDto> list = cfetsTrdBondFirstSub001Service.pageDaySubDetail(dto);
            return new Message<Page<CfetsTrdBondFirstSubDealDetailDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询申购明细列表分页数据异常", e);
            return new Message<Page<CfetsTrdBondFirstSubDealDetailDto>>().fail().setMessage("查询申购明细列表分页数据异常");
        }
    }

    @ApiOperation(value = "申购执行明细", notes = "查询申购执行明细分页数据,按指令代码查询")
    @PostMapping("/pageDaySubDealDetail")
    @ResponseBody
    public Message<Page<CfetsTrdBondFirstSubDealDetailDto>> pageDaySubDealDetail(@RequestBody CfetsTrdBondFirstDaySubRequest dto) {
        try {
            Page<CfetsTrdBondFirstSubDealDetailDto> list = cfetsTrdBondFirstSub001Service.pageDaySubDealDetail(dto);
            return new Message<Page<CfetsTrdBondFirstSubDealDetailDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询申购执行明细分页数据异常", e);
            return new Message<Page<CfetsTrdBondFirstSubDealDetailDto>>().fail().setMessage("查询申购执行明细分页数据异常");
        }
    }

}
