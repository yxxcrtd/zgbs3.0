/**
 * 银行间指令查询
 * @author sjj
 * @version 1.0 2021年11月25日
 * @see CfetsEtrInfoQueryController
 * @since 1.0
 */

package com.joyintech.tams.cfets.etrinfoquery.controller;


import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.cfets.etrinfoquery.dto.CfetsEtrInfoQueryListRequest;
import com.joyintech.tams.cfets.etrinfoquery.dto.CfetsEtrInfoQueryListResponse;
import com.joyintech.tams.cfets.etrinfoquery.service.CfetsEtrInfoQueryService;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.joyintech.tams.business.common.pojo.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@PageId("CfetsEtrInfoQuery")
@RequestMapping("/cfets/cfetsetrinfoquery/CfetsEtrInfoQuery")
@Api(tags = "银行间指令查询")
public class CfetsEtrInfoQueryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CfetsEtrInfoQueryController.class);

    @Autowired
    private CfetsEtrInfoQueryService cfetsEtrInfoQueryService;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        //数据字典初始化数据
        initPageParams.put("PINAN_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.平安资产类型, null));
        initPageParams.put("BUSI_TYPE_POSITION", this.getSelectDataFromDataDic(DictCode.头寸业务类型, null));
        initPageParams.put("TRADE_MARKET", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
//        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        List<SelectOptionDto> psList = makeAllFloatRate();
        initPageParams.put("PS", JsonUtils.objectArray2Json(psList.toArray()));
        initPageParams.put("SHORTSELLINGSUPPORTED01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        //自动刷新时间(ms)
        initPageParams.put("cfetsEtrInfoQuery_trade_refresh_period", CacheUtil.getSysParam("cfetsEtrInfoQuery_trade_refresh_period"));

        return new Message<Map<String, Object>>(initPageParams).success();
    }

    /**
     *
     * 组交易方向下拉框值
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    private List<SelectOptionDto> makeAllFloatRate() {
        List<SelectOptionDto> listFloatRate = new ArrayList<SelectOptionDto>();

        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        for (SelectOptionDto dto : selectDto1.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto2 = this.getSelectDataFromDataDic(DictCode.回购拆借方向, null);
        for (SelectOptionDto dto : selectDto2.getOptions()) {
            listFloatRate.add(dto);
        }
        SelectDto selectDto3 = this.getSelectDataFromDataDic(DictCode.互换方式, null);
        for (SelectOptionDto dto : selectDto3.getOptions()) {
            listFloatRate.add(dto);
        }
        return listFloatRate;
    }

    /**
     * 指令列表分页查询
     *
     * @param dto
     * @return Page<CfetsEtrInfoQueryListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "银行间指令查询列表分页查询", notes = "银行间指令查询列表分页查询")
    @PostMapping("/findAllData")
    @ResponseBody
    public Message<Page<CfetsEtrInfoQueryListResponse>> findAllData(@RequestBody CfetsEtrInfoQueryListRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrInfoQueryListResponse> page = cfetsEtrInfoQueryService.findAllData(dto);
            return new Message<Page<CfetsEtrInfoQueryListResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询银行间指令列表分页数据异常", e);
            return new Message<Page<CfetsEtrInfoQueryListResponse>>().fail().setMessage("查询银行间指令列表分页数据异常");
        }
    }

    @PostMapping(value = "/exportEtrInfoQueryList")
    @ResponseBody
    @ApiOperation(value = "银行间指令列表导出", notes = "银行间指令列表导出")
    public void exportEtrInfoQueryList(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "银行间指令查询", () -> {
            List dataList = cfetsEtrInfoQueryService.findAllData(req.getDto(CfetsEtrInfoQueryListRequest.class)).getRoot();
            return dataList;
        });
    }
}
