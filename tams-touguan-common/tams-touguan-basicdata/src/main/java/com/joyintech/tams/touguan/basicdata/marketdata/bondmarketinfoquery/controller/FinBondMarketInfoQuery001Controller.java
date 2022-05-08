package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoDetailQueryDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketInfoQuery001Dto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req.FinBondMarketInfoQueryRequest;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service.FinBondMarketInfoQuery001Service;
import com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.dto.FinListedBondInfoQuery001Dto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@PageId("FinBondMarketInfoQuery001")
@RequestMapping("/asset/finBondMarketInfoQuery001")
@Api(tags = "债券市场信息查询")
public class FinBondMarketInfoQuery001Controller extends BaseController {

    @Autowired
    private FinBondMarketInfoQuery001Service finBondMarketInfoQuery001Service;


    @ApiOperation(value = "初始化参数", notes = "初始化参数")
    @PostMapping("/initParams")
    @ResponseBody
    public Message<Map<String, Object>> initParams() {
        Map<String, Object> initPageParams = new HashMap<>(16);
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        initPageParams.put("WINDTYPEONE", this.getSelectDataFromDataDic(DictCode.WIND一级分类_含债券一级分类等, null));
        initPageParams.put("WINDTYPETWO", this.getSelectDataFromDataDic(DictCode.WIND二级分类_含债券二级分类等, null));
        initPageParams.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        initPageParams.put("INT_TYPE", this.getSelectDataFromDataDic(DictCode.利率类型, null));
        initPageParams.put("FREQUENCY_UNIT", this.getSelectDataFromDataDic(DictCode.频率, null));
        initPageParams.put("BASIS", this.getSelectDataFromDataDic(DictCode.计息基础, null));
        initPageParams.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
        initPageParams.put("EXEC_MODE", this.getSelectDataFromDataDic(DictCode.行权方式, null));
        initPageParams.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
        initPageParams.put("BLN_AREA", this.getSelectDataFromDataDic(DictCode.境内外, null));
        initPageParams.put("CYY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("MAIN_GRADE_RESULT", this.getSelectDataFromDataDic(DictCode.评级结果, null));
        initPageParams.put("BASE_RATE_ACCURACY", this.getSelectDataFromDataDic(DictCode.基准利率精度, null));
        initPageParams.put("RESET_TYPE", this.getSelectDataFromDataDic(DictCode.重置类型, null));
        initPageParams.put("ADVANCE_OBSERV_UNIT", this.getSelectDataFromDataDic(DictCode.提前观察单位_延后时间单位, null));
        initPageParams.put("INTPAY_RULE", this.getSelectDataFromDataDic(DictCode.利息分配方式, null));
        initPageParams.put("TDAY_RULE", this.getSelectDataFromDataDic(DictCode.营业日准则, null));
        initPageParams.put("CALC_RULE", this.getSelectDataFromDataDic(DictCode.推算方式, null));
        initPageParams.put("CASH_CLASS_TYPE", this.getSelectDataFromDataDic(DictCode.现金流类型, null));
        //导出
        initPageParams.put("EXPORT_SHOW", CacheUtil.getSysParam("EXPORT_SHOW"));
        //刷新时间
        initPageParams.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
        initPageParams.put("effDate", CacheUtil.getSysParam("ibuVar_quoteEffTime"));
        return new Message<>(initPageParams).success();
    }

    @ResponseBody
    @PostMapping("/findPage")
    @ApiOperation(value = "债券市场信息分页查询", notes = "债券市场信息分页查询")
    public Message<Page<FinBondMarketInfoQuery001Dto>> findPage(@RequestBody FinBondMarketInfoQueryRequest request) {
        return Message.success("success", finBondMarketInfoQuery001Service.queryForPage(request));
    }


    @ResponseBody
    @PostMapping(value = "/export")
    @ApiOperation(value = "债券市场信息列表导出", notes = "债券市场信息列表导出")
    public void export(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        ExcelUitl.doExport(req, response, "债券市场信息", () -> finBondMarketInfoQuery001Service.queryForPage(req.getDto(FinBondMarketInfoQueryRequest.class)).getRoot());
    }


    @ResponseBody
    @PostMapping("/detailInfo")
    @ApiOperation(value = "债券市场信息查看", notes = "债券市场信息查看")
    public Message<FinListedBondInfoQuery001Dto> detailInfo(@RequestParam("secId") String secId) {
        FinListedBondInfoQuery001Dto dto = finBondMarketInfoQuery001Service.detailInfo(secId);
        if (StringUtil.isNotEmpty(dto)) {
            return Message.success("success", dto);
        } else {
            return Message.fail("资产代码不存在.");
        }
    }

    @ResponseBody
    @PostMapping("/bondMarketInfo")
    @ApiOperation(value = "债券市场信息查看(接口数据)", notes = "债券市场信息查看(接口数据)")
    public Message<FinBondMarketInfoQuery001Dto> queryFinBondMarketForCoupons(@RequestParam("secId") String secId) {
        FinBondMarketInfoDetailQueryDto dto = finBondMarketInfoQuery001Service.queryFinBondMarketForCoupons(secId);
        if (StringUtil.isNotEmpty(dto)) {
            return Message.success("success", dto);
        } else {
            return Message.fail("资产代码不存在.");
        }
    }

}
