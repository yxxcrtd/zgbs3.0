package com.joyintech.tams.cfets.etr.bondfirstbid.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtrIbdoRiskcalRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindEtrInfoRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrIBondFirstBidFindIssueInfoRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.service.CfetsEtrBondFirstBid001Service;
import com.joyintech.tams.common.base.entity.SppiImportData;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一级投标指令
 *
 * @Author: kchen
 * @Date: 2022/4/18 16:38
 */
@Controller
@PageId("CfetsEtrBondFirstBid001")
@RequestMapping("/cfets/etrib/cfetsEtrBondFirstBid001")
@Api(tags = "一级投标指令")
public class CfetsEtrBondFirstBid001Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrBondFirstBid001Controller.class);

    @Autowired
    private CfetsEtrBondFirstBid001Service cfetsEtrBondFirstBid001Service;

    @Value("${cmdsWebSocketUrl}")
    private String cmdsWebSocketUrl;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        //一级投标指令画面待发行信息区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrBondFirstBidInfo_issueTable_refresh_period", CacheUtil.getSysParam("cfetsEtrBondFirstBidInfo_issueTable_refresh_period"));
        //一级投标指令画面指令列表区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrBondFirstBidInfo_etrTable_refresh_period", CacheUtil.getSysParam("cfetsEtrBondFirstBidInfo_etrTable_refresh_period"));

        //CMDS地址
        initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
        initPageParams.put("NEED_WORKFLOW", true);
        initPageParams.put("SHARES_TRADE_MARKET_02", DictCodeListExt1.交易市场_二级市场.getValue());
        initPageParams.put("platDate", DateUtil.platDateFromCache());

        //风险
        initPageParams.put("riskMathSwitch", CacheUtil.getSysParam("risk_math_switch"));
        initPageParams.put("pageRedisFlag", CacheUtil.getSysParam("pageRedisFlag"));
        initPageParams.put("isRiskmathSoftcontrol", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));

        //数据字典初始化数据
        initPageParams.put("ISSUE_CLASS", this.getSelectDataFromDataDic(DictCode.新债_发行方式, null));
        initPageParams.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        initPageParams.put("INT_TYPE", this.getSelectDataFromDataDic(DictCode.利率类型, null));
        initPageParams.put("FIRST_BID_STATUS", this.getSelectDataFromDataDic(DictCode.一级投标中标状态, null));
        initPageParams.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));

        //pageId
        initPageParams.put("pageId", this.getPageId());
        return new Message<Map<String, Object>>(initPageParams).success();
    }

    @ApiOperation(value = "查询指令管理分页数据", notes = "查询指令管理分页数据")
    @PostMapping("/findEtrInfoPage")
    @ResponseBody
    public Message<Page<CfetsEtrIBondFirstBidFindEtrInfoDto>> findEtrInfoPage(@RequestBody CfetsEtrIBondFirstBidFindEtrInfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrIBondFirstBidFindEtrInfoDto> list = cfetsEtrBondFirstBid001Service.findEtrInfoPage(dto);
            return new Message<Page<CfetsEtrIBondFirstBidFindEtrInfoDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询指令管理分页数据异常", e);
            return new Message<Page<CfetsEtrIBondFirstBidFindEtrInfoDto>>().fail().setMessage("查询指令管理分页数据异常");
        }

    }

    @ApiOperation(value = "查询待发行信息分页数据", notes = "查询待发行信息分页数据")
    @PostMapping("/findIssueInfoPage")
    @ResponseBody
    public Message<Page<CfetsEtrIBondFirstBidFindIssueInfoDto>> findIssueInfoPage(@RequestBody CfetsEtrIBondFirstBidFindIssueInfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrIBondFirstBidFindIssueInfoDto> list = cfetsEtrBondFirstBid001Service.findIssueInfoPage(dto);
            return new Message<Page<CfetsEtrIBondFirstBidFindIssueInfoDto>>().success().setData(list);
        } catch (Exception e) {
            logger.error("查询待发行信息分页数据异常", e);
            return new Message<Page<CfetsEtrIBondFirstBidFindIssueInfoDto>>().fail().setMessage("查询待发行信息分页数据异常");
        }

    }

    @PostMapping(value = "/exportEtrInfo")
    @ResponseBody
    @ApiOperation(value = "一级投标指令管理导出", notes = "一级投标指令管理导出")
    public void exportEtrInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "一级投标指令管理", () -> {
            List dataList = cfetsEtrBondFirstBid001Service.findEtrInfoPage(req.getDto(CfetsEtrIBondFirstBidFindEtrInfoRequest.class)).getRoot();
            return dataList;
        });
    }

    @PostMapping(value = "/exportIssueInfo")
    @ResponseBody
    @ApiOperation(value = "一级投标待发行信息导出", notes = "一级投标待发行信息导出")
    public void exportIssueInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "一级投标待发行信息", () -> {
            List dataList = cfetsEtrBondFirstBid001Service.findIssueInfoPage(req.getDto(CfetsEtrIBondFirstBidFindIssueInfoRequest.class)).getRoot();
            return dataList;
        });
    }

    @ApiOperation(value = "查询资产是否进行了SPPI测试", notes = "查询资产是否进行了SPPI测试")
    @PostMapping("/queryAssetIsSppiTest/{finprodId:.+}")
    @ResponseBody
    public Message<SppiImportData> queryAssetIsSppiTest(@PathVariable String finprodId) {
        try {
            SppiImportData data = cfetsEtrBondFirstBid001Service.queryAssetIsSppiTest(finprodId);
            return new Message<SppiImportData>().success().setData(data);
        } catch (Exception e) {
            logger.error("查询该资产是否进行了SPPI测试异常", e);
            return new Message<SppiImportData>().fail().setMessage("查询异常");
        }
    }

    @ApiOperation(value = "一级投标指令查询业务模式", notes = "一级投标指令查询业务模式")
    @PostMapping("/queryInvAim/{portfolioId}/{finprodId:.+}")
    @ResponseBody
    public Message<String> queryInvAim(@PathVariable("portfolioId") String portfolioId, @PathVariable String finprodId) {
        try {
            String restult = cfetsEtrBondFirstBid001Service.queryInvAim(portfolioId, finprodId);
            if (StringUtil.isEmpty(restult)) {
                restult = "";
            }
            return new Message<String>().success().setData(restult);
        } catch (Exception e) {
            logger.error("查询一级投标指令查询业务模式异常", e);
            return new Message<String>().fail().setMessage("查询一级投标指令查询业务模式异常");
        }
    }

    @ApiOperation(value = "一级投标指令查询证券账户", notes = "一级投标指令查询证券账户")
    @PostMapping("/querySecAcctId/{portfolioId}/{finprodId:.+}")
    @ResponseBody
    public Message<String> querySecAcctId(@PathVariable("portfolioId") String portfolioId, @PathVariable String finprodId) {
        try {
            //证券账户
            String secAcctId = cfetsEtrBondFirstBid001Service.queryPortfolioSecAcctId(portfolioId, finprodId);
            if (StringUtil.isEmpty(secAcctId)) {
                secAcctId = "";
            }
            return new Message<String>().success().setData(secAcctId);
        } catch (Exception e) {
            logger.error("查询一级投标指令查询证券账户异常", e);
            return new Message<String>().fail().setMessage("查询一级投标指令查询证券账户异常");
        }
    }

//    @ApiOperation(value = "一级投标指令组合关联信息", notes = "一级投标指令组合关联信息")
//    @PostMapping("/queryPortfolioRelationInfo/{portfolioId}/{finprodId:.+}")
//    @ResponseBody
//    public Message<CfetsEtrPortfolioInfoDto> queryPortfolioRelationInfo(@PathVariable("portfolioId") String portfolioId, @PathVariable String finprodId) {
//        try {
//            CfetsEtrPortfolioInfoDto cfetsEtrPortfolioInfoDto = new CfetsEtrPortfolioInfoDto();
//            cfetsEtrPortfolioInfoDto.setPortfolioId(portfolioId);
//            cfetsEtrPortfolioInfoDto.setFinprodId(finprodId);
//            //业务模式
//            String businessModel = cfetsEtrBondFirstBid001Service.findInvAim(portfolioId, finprodId);
//            if (StringUtil.isNotEmpty(businessModel)) {
//                cfetsEtrPortfolioInfoDto.setBusinessModel(businessModel);
//            }
//            //证券账户
//            String secAcctId = cfetsEtrBondFirstBid001Service.queryPortfolioSecAcctId(portfolioId, finprodId);
//            if (StringUtil.isNotEmpty(secAcctId)) {
//                cfetsEtrPortfolioInfoDto.setSecAcctId(secAcctId);
//            }
//
//            return new Message<CfetsEtrPortfolioInfoDto>().success().setData(cfetsEtrPortfolioInfoDto);
//        } catch (Exception e) {
//            logger.error("查询一级投标指令组合关联信息", e);
//            return new Message<CfetsEtrPortfolioInfoDto>().fail().setMessage("查询一级投标指令组合关联信息");
//        }
//    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody CfetsEtrIbdoRiskcalRequest dto) throws Exception {
        AjaxResult ajaxResult = new AjaxResult();
        return new Message<AjaxResult>().success().setData(ajaxResult);
    }

    /**
     * 资产检查
     *
     * @param finprodId
     * @return
     */
    @ApiOperation(value = "资产检查", notes = "资产检查")
    @PostMapping("/finprodIdCheck/{finprodId:.+}")
    @ResponseBody
    public Message<AjaxResult> finprodIdCheck(@PathVariable String finprodId) {
        try {
            AjaxResult ajaxResult = cfetsEtrBondFirstBid001Service.finprodIdCheck(finprodId);
            return new Message<AjaxResult>().success().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("资产检查异常", e);
            return new Message<AjaxResult>().fail().setMessage("资产检查异常");
        }
    }

    @PostMapping("closeCheck")
    @ApiOperation(value = "关闭委托check", notes = "关闭委托check")
    @ResponseBody
    public Message<AjaxResult> closeDelegateCheck(@RequestBody CfetsEtrBondFirstBid002Dto dto) throws Exception{
        return cfetsEtrBondFirstBid001Service.closeCheckDelegate(dto);
    }

    @PostMapping("close")
    @ApiOperation(value = "关闭委托", notes = "关闭委托")
    @ResponseBody
    public Message<AjaxResult> closeDelegate(@RequestBody CfetsEtrBondFirstBid002Dto dto) throws Exception {
        return cfetsEtrBondFirstBid001Service.closeDelegate(dto);
    }

}
