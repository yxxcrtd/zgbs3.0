package com.joyintech.tams.business.asset.tenderbond.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.business.asset.tenderbond.dto.CalcDto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset001Dto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset002Dto;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAssetSearch;
import com.joyintech.tams.business.asset.tenderbond.service.FinTenderBondAssetService;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Controller
@PageId("FinTenderBondAsset001")
@RequestMapping("/asset/tenderBondAsset001")
@Api(tags = "新债标的查询(新)")
public class FinTenderBondAsset001Controller extends BaseController {
    @Autowired
    private FinTenderBondAssetService finTenderBondAssetService;

    @ResponseBody
    @PostMapping("/getPageParams")
    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        //自动刷新时间
        initPageParams.put("finTenderBondAsset001_list_refresh_period", CacheUtil.getSysParam("finTenderBondAsset001_list_refresh_period"));
        //数据字典初始化数据
        initPageParams.put("ISSUE_STAGE", this.getSelectDataFromDataDic(DictCode.发行阶段, null));
        initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("REGIST_ORG", this.getSelectDataFromDataDic(DictCode.登记托管机构, null));
        initPageParams.put("RAISE_TYPE", this.getSelectDataFromDataDic(DictCode.募集方式, null));
        initPageParams.put("ISSUE_CLASS", this.getSelectDataFromDataDic(DictCode.新债_发行方式, null));
        initPageParams.put("TENDER_TYPE", this.getSelectDataFromDataDic(DictCode.招标方式, null));
        initPageParams.put("TENDER_TARGET", this.getSelectDataFromDataDic(DictCode.招标标的, null));
        initPageParams.put("ISSUE_STYLE", this.getSelectDataFromDataDic(DictCode.发行类型, null));
        initPageParams.put("COUPON_SPECIES", this.getSelectDataFromDataDic(DictCode.息票品种, null));
        initPageParams.put("INT_TYPE", this.getSelectDataFromDataDic(DictCode.利率类型, null));
        initPageParams.put("FREQUENCY_UNIT", this.getSelectDataFromDataDic(BussinessDictCode.频率, null));
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("ISSUE_STATUS", this.getSelectDataFromDataDic(DictCode.发行状态, null));
        initPageParams.put("CUSTOM_ASSET_TYPE_TWO", this.getSelectDataFromDataDic(DictCode.行内资产二级分类, null));
        initPageParams.put("CUSTOM_ASSET_TYPE_THREE", this.getSelectDataFromDataDic(DictCode.行内资产三级分类, null));
        initPageParams.put("CUSTOM_ASSET_TYPE_FOUR", this.getSelectDataFromDataDic(DictCode.行内资产四级分类, null));
        //pageId
        initPageParams.put("pageId", this.getPageId());
        //当前平台日
        initPageParams.put("platDate", DateUtil.platDateFromCache());
        //当前用户
        initPageParams.put("userId", SecurityUtil.getLoginUser().getUserName());
        return Message.success("success", initPageParams);
    }

    @ResponseBody
    @PostMapping("/findPage")
    @ApiOperation(value = "新债标的分页查询", notes = "新债标的分页查询")
    public Message<Page<FinTenderBondAsset001Dto>> findPage(@RequestBody FinTenderBondAssetSearch search) {
        return Message.success("success", finTenderBondAssetService.selectForPage(search));
    }

    @ResponseBody
    @PostMapping("/finprodIdForSelect")
    @ApiOperation(value = "资产代码下拉列表", notes = "资产代码列表,参数:{search,initValue,pageNo,pageSize}")
    public Page<SelectOptionDto> finprodIdForSelect(@RequestBody Map<String, Object> search) {
        return finTenderBondAssetService.getFinprodIdForSelect(search);
    }

    @ResponseBody
    @PostMapping("/crossMarketCodeForSelect")
    @ApiOperation(value = "跨市场代码下拉列表", notes = "跨市场代码下拉列表,参数:{search,initValue,pageNo,pageSize,tradeMarket}")
    public Page<SelectOptionDto> crossMarketCodeForSelect(@RequestBody Map<String, Object> search) {
        return finTenderBondAssetService.getCrossMarketCodeForSelect(search);
    }

    @ResponseBody
    @PostMapping("/appearMarketIdForSelect")
    @ApiOperation(value = "上市代码下拉列表", notes = "上市代码下拉列表,参数:{search,initValue,pageNo,pageSize,includeInterface}")
    public Page<SelectOptionDto> appearMarketIdForSelect(@RequestBody Map<String, Object> search) {
        return finTenderBondAssetService.getAppearMarketIdForSelect(search);
    }


    @ResponseBody
    @PostMapping("/appearMarketIdForSelectV2")
    @ApiOperation(value = "上市代码下拉列表V2", notes = "上市代码下拉列表V2,参数:{search,initValue,pageNo,pageSize,includeInterface}")
    public Message<Page<SelectOptionDto>> appearMarketIdForSelectV2(@RequestBody Map<String, Object> search) {
        return Message.success("success", finTenderBondAssetService.getAppearMarketIdForSelect(search));

    }

    @ResponseBody
    @PostMapping("/intRateForSelect")
    @ApiOperation(value = "基准利率下拉列表", notes = "基准利率下拉列表,参数:{search,initValue,pageNo,pageSize}")
    public Page<SelectOptionDto> intRateForSelect(@RequestBody Map<String, Object> search) {
        return finTenderBondAssetService.getIntRateForSelect(search);
    }

    @ResponseBody
    @PostMapping("/calc")
    @ApiOperation(value = "计算发行价格或者参考收益率", notes = "计算发行价格或者参考收益率,参数:{vdate,mdate,intRate,issuePrice}")
    public CalcDto calc(@RequestBody CalcDto calc) {
        return finTenderBondAssetService.calc(calc);
    }

    @ResponseBody
    @GetMapping("/editCheck")
    @ApiOperation(value = "编辑时控制是否可以编辑上市代码和上市日期", notes = "编辑时控制是否可以编辑上市代码和上市日期")
    public Message<Boolean> editCheck(@RequestParam("finprodId") String finprodId) {
        return Message.success("", finTenderBondAssetService.editCheck(finprodId));
    }

    @ResponseBody
    @PostMapping("/addCoupons")
    @ApiOperation(value = "加券按钮", notes = "加券按钮")
    public Message<FinTenderBondAsset002Dto> addCoupons(@RequestParam("finprodMarketId") String finprodMarketId) {
        return Message.success("success", finTenderBondAssetService.addCoupons(finprodMarketId));
    }

    @ResponseBody
    @PostMapping(value = "/export")
    @ApiOperation(value = "新债标的列表导出", notes = "新债标的列表导出")
    public void export(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        ExcelUitl.doExport(req, response, "新债标的", () ->
                finTenderBondAssetService.selectForPage(req.getDto(FinTenderBondAssetSearch.class)).getRoot()
        );
    }


}
