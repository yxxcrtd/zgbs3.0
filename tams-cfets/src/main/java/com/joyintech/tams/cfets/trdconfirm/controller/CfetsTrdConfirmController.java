/**
 * 银行间成交确认
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsEtrDispatcherController
 * @since 1.0
 */

package com.joyintech.tams.cfets.trdconfirm.controller;


import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.trdconfirm.dto.*;
import com.joyintech.tams.cfets.trdconfirm.service.CfetsTrdConfirmService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@PageId("CfetsTrdConfirm")
@RequestMapping("/cfets/trdconfirm/CfetsTrdConfirm")
@Api(tags = "银行间交易成交确认")
public class CfetsTrdConfirmController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsTrdConfirmController.class);

    @Autowired
    private CfetsTrdConfirmService cfetsTrdConfirmService;

    @Autowired
    private IPlatformDate iPlatformDate;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        //是否支持CSTP下行接口
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_BOND", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BOND"));
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_REPO", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_REPO"));
        initPageParams.put("IS_SUPPORT_CSTP_FLAG_BONDFIRST", CacheUtil.getSysParam("IS_SUPPORT_CSTP_FLAG_BONDFIRST"));
        //银行间债券交易执行是否可手工确认
        initPageParams.put("IS_CFETSSECTRD_MANUALCONFIRM_FLAG", CacheUtil.getSysParam("IS_CFETSSECTRD_MANUALCONFIRM_FLAG"));
        //银行间回购交易执行是否可手工确认
        initPageParams.put("IS_CFETSREPOTRD_MANUALCONFIRM_FLAG", CacheUtil.getSysParam("IS_CFETSREPOTRD_MANUALCONFIRM_FLAG"));
        // 银行间一级申购交易执行是否可手工确认
        initPageParams.put("IS_CFETSFIRSTSECTRD_MANUALCONFIRM_FLAG", CacheUtil.getSysParam("IS_CFETSFIRSTSECTRD_MANUALCONFIRM_FLAG"));
        //交易员是否可查看全部CSTP下行成交数据
        initPageParams.put("IS_QUERYALLCSTP_FLAG", CacheUtil.getSysParam("IS_QUERYALLCSTP_FLAG"));
        //自动刷新时间
        initPageParams.put("cfetsTrdConfirm_regList_refresh_period", CacheUtil.getSysParam("cfetsTrdConfirm_regList_refresh_period"));
        initPageParams.put("cfetsTrdConfirm_cstpList_refresh_period", CacheUtil.getSysParam("cfetsTrdConfirm_cstpList_refresh_period"));
        initPageParams.put("cfetsTrdConfirm_matchList_refresh_period", CacheUtil.getSysParam("cfetsTrdConfirm_matchList_refresh_period"));
        //数据字典初始化数据
        initPageParams.put("REG_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.登记资产类型, null));
        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        initPageParams.put("TRADE_MARKET", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
        initPageParams.put("QUOTE_TYPE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式, null));
        initPageParams.put("TRD_MATCH_TYPE", this.getSelectDataFromDataDic(DictCode.下行交易匹配类型, null));
        initPageParams.put("CSTP_BUSINESS_TYPE", this.getSelectDataFromDataDic(DictCode.CSTP资产类型, null));
        initPageParams.put("QUOTE_STYLE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式_登记, null));
        initPageParams.put("CUSTOM_ASSET_TYPE_TWO", this.getSelectDataFromDataDic(DictCode.行内资产二级分类, null));
        // 系统日期
        initPageParams.put("sysDate", iPlatformDate.getSysDateStr());
        //pageId
        initPageParams.put("pageId", "CfetsTrdConfirm");
        return new Message<Map<String, Object>>(initPageParams).success();
    }
    @ApiOperation(value = "交易执行列表分页查询", notes = "交易执行列表分页查询")
    @PostMapping("/findAllRegInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmRegInfoListResponse>> findAllRegInfoList(@RequestBody CfetsTrdConfirmRegInfoListRequest dto) {
        try {
            Page<CfetsTrdConfirmRegInfoListResponse> page =cfetsTrdConfirmService.findAllRegInfoList(dto);
            return new Message<Page<CfetsTrdConfirmRegInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询交易执行列表分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmRegInfoListResponse>>().fail().setMessage("查询交易执行列表分页数据异常");
        }
    }
    @ResponseBody
    @ApiOperation(value = "交易执行-成交确认", notes = "交易执行-成交确认")
    @PostMapping("trdConfirmSave")
    public Message<AjaxResult> trdConfirmSave(@RequestBody CfetsTrdConfirmRegInfoListResponse dto) {
        //对选择的数据进行check
        AjaxResult ajax= cfetsTrdConfirmService.trdConfirmSaveCheckMethod(dto);
        if(null!=ajax && !StringUtil.isEmptyList(ajax.getErrorList())){
            return new Message<AjaxResult>().success().setData(ajax);
        }
        try {
            AjaxResult ajaxResult = cfetsTrdConfirmService.trdConfirmSaveMethod(dto);
            if (null!=ajaxResult && !StringUtil.isEmptyList(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }else{
                return new Message<AjaxResult>().success().setData(new AjaxResult("成交确认成功"));
            }
        } catch (Exception e) {
            logger.error("交易执行-成交确认数据异常", e);
            return new Message<AjaxResult>().fail().setMessage("成交确认数据异常");
        }
    }
    @ApiOperation(value = "CSTP成交回报分页查询", notes = "CSTP成交回报分页查询")
    @PostMapping("/findAllCstpInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmCstpInfoListResponse>> findAllCstpInfoList(@RequestBody CfetsTrdConfirmCstpInfoListRequest dto) {
        try {
            Page<CfetsTrdConfirmCstpInfoListResponse> page =cfetsTrdConfirmService.findAllCstpInfoList(dto);
            return new Message<Page<CfetsTrdConfirmCstpInfoListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询CSTP成交回报分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmCstpInfoListResponse>>().fail().setMessage("查询CSTP成交回报分页数据异常");
        }
    }
    @ResponseBody
    @ApiOperation(value = "CSTP成交回报-手工匹配校验", notes = "CSTP成交回报-手工匹配校验")
    @PostMapping("checkCstpManualMatch")
    public Message<AjaxResult> checkCstpManualMatch(@RequestBody CfetsTrdConfirmManualMatchInfoRequest manualMatchDto){
        try {
            AjaxResult ajax= cfetsTrdConfirmService.checkCstpManualMatch(manualMatchDto);
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
    @ApiOperation(value = "CSTP成交回报-手工匹配", notes = "CSTP成交回报-手工匹配")
    @PostMapping("cstpManualMatch")
    public Message<AjaxResult> cstpManualMatch(@RequestBody CfetsTrdConfirmManualMatchInfoRequest manualMatchDto) {
        try {
            AjaxResult ajaxResult = cfetsTrdConfirmService.cstpManualMatchMethod(manualMatchDto);
            if (null!=ajaxResult && !StringUtil.isEmptyList(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }else{
                return new Message<AjaxResult>().success().setData(new AjaxResult("手工匹配确定成功"));
            }
        } catch (Exception e) {
            logger.error("手工匹配数据异常", e);
            return new Message<AjaxResult>().fail().setMessage("手工匹配数据异常");
        }
    }
    @ApiOperation(value = "匹配确认列表分页查询", notes = "匹配确认列表分页查询")
    @PostMapping("/findAllTrdCstpInfoList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmTrdCstpMatchResponse>> findAllTrdCstpInfoList(@RequestBody CfetsTrdConfirmTrdCstpMatchRequest dto){
        try {
            Page<CfetsTrdConfirmTrdCstpMatchResponse> page =cfetsTrdConfirmService.findAllTrdCstpInfoList(dto);
            return new Message<Page<CfetsTrdConfirmTrdCstpMatchResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询匹配确认列表分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmTrdCstpMatchResponse>>().fail().setMessage("查询匹配确认列表分页数据异常");
        }
    }
    @ApiOperation(value = "匹配确认列表-交易执行债券数据分页查询", notes = "匹配确认列表-交易执行债券数据分页查询")
    @PostMapping("/findAllTrdBondList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmExecBondInfoResponse>> findAllTrdBondList(@RequestBody CfetsTrdConfirmExecCstpRequest dto) {
        try {
            Page<CfetsTrdConfirmExecBondInfoResponse> page =cfetsTrdConfirmService.findAllTrdBondList(dto);
            return new Message<Page<CfetsTrdConfirmExecBondInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询匹配确认列表分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmExecBondInfoResponse>>().fail().setMessage("查询匹配确认列表分页数据异常");
        }
    }

    @ApiOperation(value = "匹配确认列表-交易执行一级申购数据分页查询", notes = "匹配确认列表-交易执行质押式数据分页查询")
    @PostMapping("/findAllTrdBidList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmExecFirstBondInfoResponse>> findAllTrdBidList(@RequestBody CfetsTrdConfirmExecCstpRequest dto) {
        try {
            Page<CfetsTrdConfirmExecFirstBondInfoResponse> page = cfetsTrdConfirmService.findAllTrdBidList(dto);
            return new Message<Page<CfetsTrdConfirmExecFirstBondInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("交易执行质押式数据分页查询数据异常", e);
            return new Message<Page<CfetsTrdConfirmExecFirstBondInfoResponse>>().fail().setMessage("交易执行质押式数据分页查询数据异常");
        }
    }
    @ApiOperation(value = "匹配确认列表-交易执行质押式数据分页查询", notes = "匹配确认列表-交易执行质押式数据分页查询")
    @PostMapping("/findAllTrdRepoList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmExecRepoInfoResponse>> findAllTrdRepoList(@RequestBody CfetsTrdConfirmExecCstpRequest dto) {
        try {
            Page<CfetsTrdConfirmExecRepoInfoResponse> page =cfetsTrdConfirmService.findAllTrdRepoList(dto);
            return new Message<Page<CfetsTrdConfirmExecRepoInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询匹配确认列表分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmExecRepoInfoResponse>>().fail().setMessage("查询匹配确认列表分页数据异常");
        }
    }
    @ApiOperation(value = "匹配确认列表-cstp成交债券数据分页查询", notes = "匹配确认列表-cstp成交债券数据分页查询")
    @PostMapping("/findAllCstpBondList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmCstpBondInfoResponse>> findAllCstpBondList(@RequestBody CfetsTrdConfirmExecCstpRequest dto) {
        try {
            Page<CfetsTrdConfirmCstpBondInfoResponse> page =cfetsTrdConfirmService.findAllCstpBondList(dto);
            return new Message<Page<CfetsTrdConfirmCstpBondInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询匹配确认列表分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmCstpBondInfoResponse>>().fail().setMessage("查询匹配确认列表分页数据异常");
        }
    }

    @ApiOperation(value = "匹配确认列表-cstp成交一级申购数据分页查询", notes = "匹配确认列表-cstp成交质押式数据分页查询")
    @PostMapping("/findAllCstpBidList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmExecFirstBondInfoResponse>> findAllCstpBidList(@RequestBody CfetsTrdConfirmExecCstpRequest dto) {
        try {
            Page<CfetsTrdConfirmExecFirstBondInfoResponse> page = cfetsTrdConfirmService.findAllCstpBidList(dto);
            return new Message<Page<CfetsTrdConfirmExecFirstBondInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("cstp成交质押式数据分页查询数据异常", e);
            return new Message<Page<CfetsTrdConfirmExecFirstBondInfoResponse>>().fail().setMessage("cstp成交质押式数据分页查询数据异常");
        }
    }
    @ApiOperation(value = "匹配确认列表-cstp成交质押式数据分页查询", notes = "匹配确认列表-cstp成交质押式数据分页查询")
    @PostMapping("/findAllCstpRepoList")
    @ResponseBody
    public Message<Page<CfetsTrdConfirmCstpRepoInfoResponse>> findAllCstpRepoList(@RequestBody CfetsTrdConfirmExecCstpRequest dto) {
        try {
            Page<CfetsTrdConfirmCstpRepoInfoResponse> page =cfetsTrdConfirmService.findAllCstpRepoList(dto);
            return new Message<Page<CfetsTrdConfirmCstpRepoInfoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询匹配确认列表分页数据异常", e);
            return new Message<Page<CfetsTrdConfirmCstpRepoInfoResponse>>().fail().setMessage("查询匹配确认列表分页数据异常");
        }
    }
    @ResponseBody
    @ApiOperation(value = "匹配取消", notes = "匹配取消")
    @PostMapping("cancelTrdMatchInfo")
    public Message<AjaxResult> cancelTrdMatchInfo(@RequestBody CfetsTrdConfirmTrdCstpMatchResponse manualMatchDto) {
        try {
            cfetsTrdConfirmService.cancelTrdMatchInfo(manualMatchDto);
            return new Message<AjaxResult>().success().setData(new AjaxResult("匹配取消成功"));
        } catch (Exception e) {
            logger.error("匹配取消数据异常", e);
            return new Message<AjaxResult>().fail().setMessage("匹配取消数据异常");
        }
    }

    @PostMapping(value="/exportFindAllRegInfo")
    @ResponseBody
    @ApiOperation(value = "交易执行列表导出", notes = "交易执行列表导出")
    public void exportFindAllRegInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "交易执行列表管理", () -> {
            List dataList = cfetsTrdConfirmService.findAllRegInfoList(req.getDto(CfetsTrdConfirmRegInfoListRequest.class)).getRoot();
            return dataList;
        });
    }

    @PostMapping(value="/exportFindAllCstpInfo")
    @ResponseBody
    @ApiOperation(value = "CSTP成交回报列表导出", notes = "CSTP成交回报列表导出")
    public void exportFindAllCstpInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "CSTP成交回报列表管理", () -> {
            List dataList = cfetsTrdConfirmService.findAllCstpInfoList(req.getDto(CfetsTrdConfirmCstpInfoListRequest.class)).getRoot();
            return dataList;
        });
    }

    @PostMapping(value="/exportFindAllTrdCstpInfo")
    @ResponseBody
    @ApiOperation(value = "匹配确认列表导出", notes = "匹配确认列表导出")
    public void exportFindAllTrdCstpInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "匹配确认列表列表管理", () -> {
            List dataList = cfetsTrdConfirmService.findAllTrdCstpInfoList(req.getDto(CfetsTrdConfirmTrdCstpMatchRequest.class)).getRoot();
            return dataList;
        });
    }

    @ResponseBody
    @ApiOperation(value = "交易执行-发行失败", notes = "交易执行-发行失败")
    @PostMapping("issuingFail")
    public Message<AjaxResult> issuingFail(@RequestBody CfetsTrdConfirmRegInfoListResponse dto) {
        //对选择的数据进行check
        AjaxResult ajax = cfetsTrdConfirmService.issuingFailCheckMethod(dto);
        if (null != ajax && !StringUtil.isEmptyList(ajax.getErrorList())) {
            return new Message<AjaxResult>().success().setData(ajax);
        }
        try {
            AjaxResult ajaxResult = cfetsTrdConfirmService.issuingFailMethod(dto);
            if (null != ajaxResult && !StringUtil.isEmptyList(ajaxResult.getErrorList())) {
                return new Message<AjaxResult>().success().setData(ajaxResult);
            } else {
                return new Message<AjaxResult>().success().setData(new AjaxResult("发行失败成功"));
            }
        } catch (Exception e) {
            logger.error("交易执行-发行失败数据异常", e);
            return new Message<AjaxResult>().fail().setMessage("发行失败数据异常");
        }
    }
}
