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
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

        //一级投标指令画面待发行信息区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrIbBondinfo_positionTable_refresh_period", CacheUtil.getSysParam("cfetsEtrIbBondinfo_positionTable_refresh_period"));
        //一级投标指令画面指令列表区域自动刷新时间(ms)
        initPageParams.put("cfetsEtrIbBondinfo_etrTable_refresh_period", CacheUtil.getSysParam("cfetsEtrIbBondinfo_etrTable_refresh_period"));

        //CMDS地址
        initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
        initPageParams.put("NEED_WORKFLOW", true);
        initPageParams.put("SHARES_TRADE_MARKET_02",DictCodeListExt1.交易市场_二级市场.getValue());
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
        }catch (Exception e) {
            logger.error("查询指令管理分页数据异常",e);
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
        }catch (Exception e) {
            logger.error("查询待发行信息分页数据异常",e);
            return new Message<Page<CfetsEtrIBondFirstBidFindIssueInfoDto>>().fail().setMessage("查询待发行信息分页数据异常");
        }

    }

    @PostMapping(value="/exportEtrInfo")
    @ResponseBody
    @ApiOperation(value = "一级投标指令管理导出", notes = "一级投标指令管理导出")
    public void exportEtrInfo(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "一级投标指令管理", () -> {
            List dataList = cfetsEtrBondFirstBid001Service.findEtrInfoPage(req.getDto(CfetsEtrIBondFirstBidFindEtrInfoRequest.class)).getRoot();
            return dataList;
        });
    }

    @PostMapping(value="/exportIssueInfo")
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
    @PostMapping("/queryAssetIsSppiTest/{secId:.+}")
    @ResponseBody
    public Message<SppiImportData> queryAssetIsSppiTest(@PathVariable String secId) {
        try {
            SppiImportData data = cfetsEtrBondFirstBid001Service.queryAssetIsSppiTest(secId);
            return new Message<SppiImportData>().success().setData(data);
        } catch (Exception e) {
            logger.error("查询该资产是否进行了SPPI测试异常", e);
            return new Message<SppiImportData>().fail().setMessage("查询异常");
        }
    }

    @ApiOperation(value = "一级投标指令买入时查询业务模式", notes = "一级投标指令买入时查询业务模式")
    @PostMapping("/queryInvaimBySecIdAndPrd/{portfolioId}/{secId:.+}")
    @ResponseBody
    public Message<String> findInvAim(@PathVariable("portfolioId") String portfolioId,@PathVariable String secId) {
        try {
            String restult = cfetsEtrBondFirstBid001Service.findInvAim(portfolioId,secId);
            if(StringUtil.isEmpty(restult)){
                restult="";
            }
            return new Message<String>().success().setData(restult);
        } catch (Exception e) {
            logger.error("查询一级投标指令买入时查询业务模式", e);
            return new Message<String>().fail().setMessage("查询异常");
        }
    }

    @ResponseBody
    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("doRiskcal")
    public Message<AjaxResult> doRiskcal(@RequestBody CfetsEtrIbdoRiskcalRequest dto) throws Exception {
        return null;
    }

}
