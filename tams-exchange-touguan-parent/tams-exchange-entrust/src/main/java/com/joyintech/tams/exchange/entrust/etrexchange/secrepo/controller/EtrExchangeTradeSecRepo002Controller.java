/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.controller;

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
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SelectOptionRequestDto;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.*;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service.EtrExchangeTradeSecRepo002Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.TempConstant;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTrade002Dto;
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
import java.util.stream.Collectors;

/**
 * 场内债券指令（现券+通用质押式回购） 左侧共通界面
 * @author qmh
 * @version 1.0 2022年4月7日
 * @see EtrExchangeTradeSecRepo002Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeSecRepo002")
@RequestMapping("/etrexchange/secrepo/EtrExchangeTradeSecRepo002")
@Api(tags = "场内交易-场内债券指令左侧共通接口")
public class EtrExchangeTradeSecRepo002Controller extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(EtrExchangeTradeSecRepo002Controller.class);

    /**
     * 可转债委托处理详细
     */
    @Autowired
    private EtrExchangeTradeSecRepo002Service etrExchangeTradeSecRepo002Service;


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
        initPageParams.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
        initPageParams.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
        initPageParams.put("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
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
        /*add by chenjiaxing 更改交易方向枚举值 end*/
        return new Message<Map<String, Object>>(initPageParams).success();
    }
    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param initPageParams
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
        List<SelectOptionDto> finprodTypeTgList = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(finprodTypeTgList);
        initPageParams.put("FINPROD_TYPE_TG", selectDto);
    }
    /**
     * 由于交易平台不同，设置不同交易平台对应的委托方向
     *
     * @param initPageParams
     */
    private void setPsSelectOptions(Map<String, Object> initPageParams) {
        SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
        List<SelectOptionDto> ps = selectDto.getOptions().stream().filter(option -> {
            return StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(ps);
        initPageParams.put("PS", selectDto);
        List<SelectOptionDto> ps_sec = selectDto.getOptions().stream().filter(option -> {
            return  StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
                    || StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(ps_sec);
        initPageParams.put("PS_SEC", selectDto);
        List<SelectOptionDto> ps_repo = selectDto.getOptions().stream().filter(option -> {
            return  StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
                    || StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
        }).collect(Collectors.toList());
        selectDto.setOptions(ps_repo);
        initPageParams.put("PS_REPO", selectDto);
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
    public Message<Page<EtrExchangeTradePositionsSecRepoResponse>> findPositionsPage(@RequestBody EtrExchangeTradePositionsSecRepoRequest request){
        try {
            request.setSecId(null);
            Page<EtrExchangeTradePositionsSecRepoResponse> page = etrExchangeTradeSecRepo002Service.findPositionsPage(request);
            return new Message<Page<EtrExchangeTradePositionsSecRepoResponse>>().success().setData(page);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<EtrExchangeTradePositionsSecRepoResponse>>().fail().setMessage("查询异常");
        }

    }

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
        Page<SelectOptionDto> tempList = etrExchangeTradeSecRepo002Service.getSecAcctId(request);
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
        Map<String, String> map = etrExchangeTradeSecRepo002Service.loadSecNum(request);
        return new Message<Map<String, String>>().success().setData(map);
    }

    /**
     * 指令列表
     * @return
     */
    @ApiOperation(value = " 查询指令列表分页数据",notes = "查询指令列表分页数据")
    @PostMapping("selectEtrExchange")
    @ResponseBody
    public Message<Page<EtrExchangeTradeEtrExchangeSecRepoResponse>> selectEtrExchange(@RequestBody  EtrExchangeTradeEtrExchangeSecRepoRequest request){
        try {
            Page<EtrExchangeTradeEtrExchangeSecRepoResponse> result = etrExchangeTradeSecRepo002Service.selectEtrExchange(request);
            return new Message<Page<EtrExchangeTradeEtrExchangeSecRepoResponse>>().success().setData(result);
        } catch (Exception e) {
            logger.error("查询功能异常", e);
            return new Message<Page<EtrExchangeTradeEtrExchangeSecRepoResponse>>().fail().setMessage("查询异常");
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
    public void exportPosition(@RequestBody EtrExchangeTradeExportSecRepoRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "持仓列表", () -> {
            EtrExchangeTradePositionsSecRepoRequest dto = requestDto.getDto(EtrExchangeTradePositionsSecRepoRequest.class);
            List dataList = etrExchangeTradeSecRepo002Service.findPositionsPage(dto).getRoot();
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
    public void exportEtrExchange(@RequestBody EtrExchangeTradeExportSecRepoRequest requestDto, HttpServletResponse response) {
        // 导出
        ExcelUitl.doExport(requestDto, response, "指令列表", () -> {
            EtrExchangeTradeEtrExchangeSecRepoRequest dto = requestDto.getDto(EtrExchangeTradeEtrExchangeSecRepoRequest.class);
            List dataList = etrExchangeTradeSecRepo002Service.selectEtrExchange(dto).getRoot();
            return dataList;
        });
    }
}
