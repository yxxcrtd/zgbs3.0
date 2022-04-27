/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.common.controller;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon002Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon003Service;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.service.EtrExchangeTradeJj001Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.TempConstant;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTrade002Dto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeCommon002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeCommon002")
@RequestMapping("/etrexchange/common/EtrExchangeTradeCommon002")
@Api(tags = "场内交易-持仓指令共通接口")
public class EtrExchangeTradeCommon002Controller extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(EtrExchangeTradeCommon002Controller.class);

    /**
     * 可转债委托处理详细
     */
    @Autowired
    private EtrExchangeTradeCommon002Service etrExchangeTradeCommon002Service;


    @ApiOperation(value="查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String,Object>> init() {
        Map<String, Object> initPageParams = new HashMap<>();
        setTradePlaceSelectOptions(initPageParams);
        setFinprodTypeTgSelectOptions(initPageParams);
        setPsSelectOptions(initPageParams);
        EtrExchangeTrade002Dto exchangeTradeDto002 = new EtrExchangeTrade002Dto();
        initPageParams.put("entity",exchangeTradeDto002);
        initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        initPageParams.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        initPageParams.put("DECLA_TYPE_PRM05", DictCodeListExt3.申报类型_不指定.getValue());
        initPageParams.put("DECLA_TYPE_PRM14", DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
        initPageParams.put("ENTRUST_BUSS_TYPE_15", DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue());
        initPageParams.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        initPageParams.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
        initPageParams.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        initPageParams.put("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
        initPageParams.put("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
        initPageParams.put("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
        // 场内交易指令画面持仓列表自动刷新时间（毫秒）
        long etrExchange_posTable_refresh_period = 20000;
        String tempSysParaVal = CacheUtil.getSysParam(
            BussinessConstant.ETREXCHANGE_POSTABLE_REFRESH_PERIOD);
        if (StringUtil.isNotBlank(tempSysParaVal)
            && !StringUtil.equals(BussinessConstant.ETREXCHANGE_POSTABLE_REFRESH_PERIOD,
                tempSysParaVal)) {
            try {
                etrExchange_posTable_refresh_period = Long.parseLong(tempSysParaVal);
            }
            catch (Exception e) {
            }
        }
        initPageParams.put("etrExchange_posTable_refresh_period", etrExchange_posTable_refresh_period);
        /*add by chenjiaxing 更改交易方向枚举值 start*/
        SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        for(int i = 0 ; i < selectDto1.getOptions().size() ; i++){
            if(StringUtil.equals(selectDto1.getOptions().get(i).getId(),"T03")){
                selectDto1.getOptions().get(i).setText("债回售");
            }
        }
        List<SelectOptionDto> optionsGradeResult = selectDto1.getOptions();
        initPageParams.put("PS_ETR", JsonUtils.objectArray2Json(optionsGradeResult.toArray()));
        initPageParams.put("ETR_CHECK_POS_SWITCH",CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH));
        initPageParams.put("noTradeBusiness", TempConstant.NO_TRADE_BUSINESS_ETR);
        initPageParams.put("SHARES_TRADE_PLATFORM_99", DictCodeList.交易平台_其它.getValue());
        /*add by chenjiaxing 更改交易方向枚举值 end*/
        return new Message<Map<String, Object>>(initPageParams).success();
    }
    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param model Model
     */
    private void setTradePlaceSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易场所, null);
        // 固定收益
        List<SelectOptionDto> tradePlaceList = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(tradePlaceList);
        initPageParams.put("TRADE_PLACE", selectDto);
    }
    /**
     * 由于交易平台不同，设置不同资产类型
     *
     * @param initPageParams initPageParams
     */
    private void setFinprodTypeTgSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto =this.getSelectDataFromDataDic(DictCode.金融产品类型_投管用, null);
        // 固定收益
        List<SelectOptionDto> finprodTypeTgList = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(finprodTypeTgList);
        initPageParams.put("FINPROD_TYPE_TG", selectDto);
    }
    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param model Model
     */
    private void setPsSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        // 竞价平台
        List<SelectOptionDto> ps_jj = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_行权_回售.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt2.交易方向_配股.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_债转股.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券入库.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券出库.getValue())
                     || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_配债.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(ps_jj);
        initPageParams.put("PS_JJ", selectDto);

        // 大宗平台
        List<SelectOptionDto> ps_dz = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
//                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
//                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PS_DZ", JsonUtils.objectArray2Json(ps_dz.toArray()));
        // 固定收益
        List<SelectOptionDto> ps_gd = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
        }).collect(Collectors.toList());
        initPageParams.put("PS_GD", JsonUtils.objectArray2Json(ps_gd.toArray()));
    }
    
    /**
     * 查询持仓
     * @return
     * @see
     * @since 1.0
     */
    @ApiOperation(value="查询持仓分页数据",notes="查询持仓分页数据")
    @PostMapping("findPositionsPage")
    @ResponseBody
    public Message<Page<EtrExchangeTradePositionsCommonResponse>> findPositionsPage(@RequestBody   EtrExchangeTradePositionsCommonRequest request){
        try {
            request.setSecId(null);
            Page<EtrExchangeTradePositionsCommonResponse> page = etrExchangeTradeCommon002Service.findPositionsPage(request);
            return new Message<Page<EtrExchangeTradePositionsCommonResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<EtrExchangeTradePositionsCommonResponse>>().fail().setMessage("查询异常");
        }

    }

    // modify by etliu 债券质押持仓查询只有非交易业务指令使用，代码删除 20220308 start
//    /**
//     * 查询债券质押持仓
//     *
//     * @return
//     * @see
//     * @since 1.0
//     */
//    @ApiOperation(value="查询债券质押持仓分页数据",notes="查询债券质押持仓分页数据")
//    @PostMapping("findZqzyPositionsPage")
//    @ResponseBody
//    public Message<Page<EtrExchangeTradePositionsCommonResponse>> findZqzyPositionsPage(@RequestBody   EtrExchangeTradePositionsCommonRequest request){
//        Page<EtrExchangeTradePositionsCommonResponse> page = etrExchangeTradeCommon002Service.findZqzyPositionsPage(request);
//        return new Message<Page<EtrExchangeTradePositionsCommonResponse>>().success().setData(page);
//    }
    // modify by etliu 债券质押持仓查询只有非交易业务指令使用，代码删除 20220308 end

    /**
     * 查询证券代码联想框文本框
     *
     * @param request 画面参数
     * @return 证券代码联想框文本框
     * @see
     * @since 1.0
     */
    @ApiOperation(value = " 证券代码：联想文本框",notes = "证券代码：联想文本框")
    @PostMapping("getSecAcctId")
    public Message<Page<SelectOptionDto>> getSecAcctId(@RequestBody SelectOptionRequestDto request) {
        Page<SelectOptionDto> tempList = etrExchangeTradeCommon002Service.getSecAcctId(request);
        return new Message<Page<SelectOptionDto>>().success().setData(tempList);
    }

    /**
     * 查询上交所和深交所标准券数量
     *
     * @return 上交所和深交所标准券数量
     * @see
     * @since 1.0
     */
    @ApiOperation(value = " 查询上交所和深交所标准券数量",notes = "查询上交所和深交所标准券数量")
    @PostMapping("loadSecNum")
    @ResponseBody
    public Message<Map<String, String>> loadSecNum(@RequestBody SelectOptionRequestDto request) {
        Map<String, String> map = etrExchangeTradeCommon002Service.loadSecNum(request);
        return new Message<Map<String, String>>().success().setData(map);
    }

    /**
     * 指令列表
     * @return
     */
    @ApiOperation(value = " 查询指令列表分页数据",notes = "查询指令列表分页数据")
    @PostMapping("selectEtrExchange")
    @ResponseBody
    public Message<Page<EtrExchangeTradeEtrExchangeCommonResponse>> selectEtrExchange(@RequestBody  EtrExchangeTradeEtrExchangeCommonRequest request){
        try {
            Page<EtrExchangeTradeEtrExchangeCommonResponse> result = etrExchangeTradeCommon002Service.selectEtrExchange(request);
            return new Message<Page<EtrExchangeTradeEtrExchangeCommonResponse>>().success().setData(result);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<EtrExchangeTradeEtrExchangeCommonResponse>>().fail().setMessage("查询异常");
        }
    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("exportPosition")
    @ResponseBody
    @ApiOperation(value = "持仓列表导出", notes = "持仓列表导出")
    public void exportPosition(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
//        Map<String, Object> param = RequestUtil.getPageParam();
//        /*指令执行导出*/
//        String datas = this.getParam("datas");
//        List<EtrExchangeTrade003Dto> param1;
//        if (datas == null) {
//            param1 = etrExchangeTradeCommon001Service.selectEtrExchange2(RequestUtil.getPageParam());
//        } else {
//            param1 = JsonUtils.json2List(datas, EtrExchangeTrade003Dto.class);
//        }
//        File outFile = etrExchangeTradeCommon003Service.makeFile(
//                "/com/joyintech/tams/invest/entrust/etrexchangetrade/conf/export_conf1.xml",
//                param1);
//        return ExportUtil.export(
//                "场内交易指令管理" + SystemGlobalConstant.OPEN_PARENTHESIS
//                        + DateUtil.format(DateUtil.platDateFromCache())
//                        + SystemGlobalConstant.CLOSE_PARENTHESIS
//                        + SystemGlobalConstant.POINT
//                        + FileNameUtil.getFileExtName(outFile),
//                outFile, true);
        // 导出
        ExcelUitl.doExport(requestDto, response, "持仓列表", () -> {
            EtrExchangeTradePositionsCommonRequest dto = requestDto.getDto(EtrExchangeTradePositionsCommonRequest.class);
            List dataList = etrExchangeTradeCommon002Service.findPositionsPage(dto).getRoot();
            return dataList;
        });
    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @PostMapping("exportEtrExchange")
    @ResponseBody
    @ApiOperation(value = "指令列表导出", notes = "指令列表导出")
    public void exportEtrExchange(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "指令列表", () -> {
            EtrExchangeTradeEtrExchangeCommonRequest dto = requestDto.getDto(EtrExchangeTradeEtrExchangeCommonRequest.class);
            List dataList = etrExchangeTradeCommon002Service.selectEtrExchange(dto).getRoot();
            return dataList;
        });
    }
}
