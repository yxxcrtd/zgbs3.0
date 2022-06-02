/**
 * 场内指令查询
 * @author msk
 * @version 1.0 2021年12月22日
 * @see FieldEtrInfoQueryController
 * @since 1.0
 */

package com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.controller;


import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.business.common.dto.CommonSearchInfoRequest;
import com.joyintech.tams.business.common.dto.FieldExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.CfetsEtrBondFirstExeListInfoResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.EntrustQueryBean;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FieldEtrInfoQueryListRequest;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FieldEtrInfoQueryListResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.FinProdResponse;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.dto.TradeQueryBean;
import com.joyintech.tams.exchange.entrust.fieldetrinfoquerymain.fieldetrinfoquery.service.FieldEtrInfoQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
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
@PageId("FieldEtrInfoQuery")  
@RequestMapping("/exchange/entrust/fieldetrinfoquerymain/fieldetrinfoquery")
@Api(tags = "场内指令查询")
public class FieldEtrInfoQueryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(FieldEtrInfoQueryController.class);

    @Autowired
    private FieldEtrInfoQueryService fieldEtrInfoQueryService;

    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams(){
        Map<String, Object> initPageParams = new HashMap<>();
        //数据字典初始化数据
        initPageParams.put("PINAN_ASSET_TYPE", this.getSelectDataFromDataDic(DictCode.平安资产类型, null));
        SelectDto busiTypePosition = this.getSelectDataFromDataDic(DictCode.头寸业务类型, null);
        SelectDto busiTypeTg = this.getSelectDataFromDataDic(DictCode.投管业务类型, null);
        List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
        selectOptionDtoList.addAll(busiTypePosition.getOptions());
        selectOptionDtoList.addAll(busiTypeTg.getOptions());
        busiTypePosition.setOptions(selectOptionDtoList);
        initPageParams.put("BUSI_TYPE_POSITION",busiTypePosition);

        initPageParams.put("TRADE_MARKET", this.getSelectDataFromDataDic(DictCode.交易场所, null));
        initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
        initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
//        initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
        List<SelectOptionDto> psList = makeAllFloatRate();
        initPageParams.put("PS", JsonUtils.objectArray2Json(psList.toArray()));
        initPageParams.put("SHORTSELLINGSUPPORTED01", CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
        //自动刷新时间(ms)
        initPageParams.put("CfetsEtrInfoQuery_trade_refresh_period", CacheUtil.getSysParam("cfetsEtrInfoQuery_trade_refresh_period"));

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
     * @return Page<FieldEtrInfoQueryListResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "场内指令查询列表分页查询", notes = "场内指令查询列表分页查询")
    @PostMapping("/findAllData")
    @ResponseBody
    public Message<Page<FieldEtrInfoQueryListResponse>> findAllData(@RequestBody FieldEtrInfoQueryListRequest dto){
            Page<FieldEtrInfoQueryListResponse> page = fieldEtrInfoQueryService.findAllData(dto);
            return new Message<Page<FieldEtrInfoQueryListResponse>>().success().setData(page);
    }

    @ApiOperation(value = "场内指令查询列表分页查询", notes = "场内指令查询列表分页查询")
    @PostMapping("/findFinProdMarketIdList")
    @ResponseBody
    public Message<Page<FinProdResponse>> findFinProdMarketIdList(@RequestBody FieldEtrInfoQueryListRequest dto){
        Page<FinProdResponse> page = fieldEtrInfoQueryService.findFinProdMarketIdList(dto);
        return new Message<Page<FinProdResponse>>().success().setData(page);
    }

    @PostMapping(value = "/exportEtrInfoQueryList")
    @ResponseBody
    @ApiOperation(value = "场内指令列表导出", notes = "场内指令列表导出")
    public void exportEtrInfoQueryList(@RequestBody FieldExportRequestDto req, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(req, response, "场内指令查询", () -> {
            List dataList = fieldEtrInfoQueryService.findAllData(req.getDto(FieldEtrInfoQueryListRequest.class)).getRoot();
            return dataList;
        });
    }

    /**
     * 指令执行查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherExeListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "指令执行查询", notes = "指令执行查询")
    @PostMapping("/getEtrDispatcherExeListInfo")
    @ResponseBody
    public Message<Page<EntrustQueryBean>> getEtrDispatcherExeListInfo(@RequestBody CommonSearchInfoRequest dto)
            throws Exception {
        try {
            Page<EntrustQueryBean> returnDto = fieldEtrInfoQueryService.getEtrDispatcherExeListInfo(dto);
            if (returnDto.getTotal() == 0 && CollectionUtils.isEmpty(returnDto.getRoot())) {
                returnDto = fieldEtrInfoQueryService.getEtrDispatcherExeListInfoByExe(dto);
            }
            return new Message<Page<EntrustQueryBean>>().success().setData(returnDto);
        } catch (Exception e) {
            logger.error("查询指令管理分页数据异常", e);
            return new Message<Page<EntrustQueryBean>>().fail().setMessage("查询指令成交异常");
        }
    }

    /**
     * 一级债券指令执行查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherExeListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "一级债券指令执行查询", notes = "一级债券指令执行查询")
    @PostMapping("/getEtrBondFirstExeListInfo")
    @ResponseBody
    public Message<Page<CfetsEtrBondFirstExeListInfoResponse>> getEtrBondFirstExeListInfo(@RequestBody CommonSearchInfoRequest dto)
            throws Exception {
        try {
            Page<CfetsEtrBondFirstExeListInfoResponse> returnDto = fieldEtrInfoQueryService.getEtrBondFirstExeListInfo(dto);
            return new Message<Page<CfetsEtrBondFirstExeListInfoResponse>>().success().setData(returnDto);
        } catch (Exception e) {
            logger.error("一级债券指令执行查询异常", e);
            return new Message<Page<CfetsEtrBondFirstExeListInfoResponse>>().fail().setMessage("一级债券指令执行查询查询异常");
        }
    }

    /**
     * 指令成交查询
     *
     * @param dto
     * @return Page<CfetsEtrDispatcherDealListInfoResponse>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ApiOperation(value = "指令成交查询", notes = "指令成交查询")
    @PostMapping("/getEtrDispatcherDealListInfo")
    @ResponseBody
    public Message<Page<TradeQueryBean>> getEtrDispatcherDealListInfo(@RequestBody CommonSearchInfoRequest dto)
            throws Exception {
        try {
            Page<TradeQueryBean> returnDto = fieldEtrInfoQueryService.getEtrDispatcherDealListInfo(dto);
            if (returnDto.getTotal() == 0 && CollectionUtils.isEmpty(returnDto.getRoot())) {
                returnDto = fieldEtrInfoQueryService.getEtrDispatcherDealListInfoByExe(dto);
            }
            return new Message<Page<TradeQueryBean>>().success().setData(returnDto);
        } catch (Exception e) {
            logger.error("查询指令成交异常", e);
            return new Message<Page<TradeQueryBean>>().fail().setMessage("查询指令成交异常");
        }
    }


}
