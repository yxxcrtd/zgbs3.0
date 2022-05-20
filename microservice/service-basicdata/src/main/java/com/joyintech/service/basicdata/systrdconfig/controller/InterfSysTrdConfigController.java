package com.joyintech.service.basicdata.systrdconfig.controller;

import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.service.basicdata.systrdconfig.dto.*;
import com.joyintech.service.basicdata.systrdconfig.service.InterfSysTrdConfigService;
import com.joyintech.service.basicdata.systrdconfig.service.StockInfoService;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecRequest;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecSingleResp;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeOtherCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.joyintech.framework.util.StringUtil;

/**
 * @Classname InterfSysTrdConfigController
 * @Description TODO
 * @author zhuhanlu 
 * @date 2020/10/13 11:29 
 * @version 1.0 
 */
@RestController("InterfSysTrdConfigCtr_dtx")
@RequestMapping("/InterfSysTrdConfig")
public class InterfSysTrdConfigController{

    @Autowired
    private InterfSysTrdConfigService interfSysTrdConfigService;

    @Autowired
    private StockInfoService stockInfoService;

    @Autowired
    private EtrTradeCommonervice etrTradeCommonervice;


    @Autowired
    private EtrTradeOtherCommonService etrTradeOtherCommonService;


    /**
     *
     * @Description //查询最小价差
     * @Param [map]
     * @return com.joyintech.service.basicdata.systrdconfig.dto.InterfSysTrdConfigDto
     * @version 1.0
     * @see
    **/
    @PostMapping("getMinPrice")
    public Map<String,Object> getMinPrice(@RequestBody Map<String,Object> map){
        return interfSysTrdConfigService.getMinPrice(map);
    }

    /**
     *
     * @Description //查询最小单位，交易上下限数据
     * @Param [map]
     * @return com.joyintech.service.basicdata.systrdconfig.dto.InterfSysTrdConfigDto
     * @version 1.0
     * @see
    **/

    @PostMapping("getMinTrdUnitLimit")
    public Map<String,Object> getMinTrdUnitLimit(@RequestBody Map<String,Object> map){
        return interfSysTrdConfigService.getMinTrdUnitLimit(map);
    }

    /**
     *
     * @Description //查询交易时间段
     * @Param []
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
    **/
    @PostMapping("getTrdTimeConfig")
    public Map<String,Object> getTrdTimeConfig(@RequestBody Map<String,Object> map){
        return interfSysTrdConfigService.getTrdTimeConfig(map);
    }

    /**
     *
     * @Description //查询申报信息
     * @Param [map]
     * @return java.util.List<com.joyintech.service.basicdata.systrdconfig.dto.TrdOthCodeInfoDto>
     * @version 1.0
     * @see
    **/
    @PostMapping("getTrdOthCodeInfo")
    public Map<String,Object> getTrdOthCodeInfo(@RequestBody Map<String,Object> map){
        return interfSysTrdConfigService.getTrdOthCodeInfoDto(map);
    }


    /**
     * 查询交易对手信息
     * @param params
     * @return
     */
    @PostMapping("selectMstCounterParty")
    public List<Map<String, Object>> selectMstCounterParty(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.selectMstCounterParty(params);
    }

    /**
     * 查询交易对手席位信息
     * @param params
     * @return
     */
    @PostMapping("selectCounterSeatId")
    public List<Map<String, Object>> selectCounterSeatId(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.selectCounterSeatId(params);
    }

    /**
     * 查询对手方交易员信息
     * @param params
     * @return
     */
    @PostMapping("selectCounterTraderId")
    public List<Map<String, Object>> selectCounterTraderId(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.selectCounterTraderId(params);
    }

    /**
     * 新增交易对手席位
     * @param params
     * @return
     */
    @PostMapping("insertCounterSeatId")
    public Map<String, Object> insertCounterSeatId(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.insertCounterSeatId(params);
    }

    /**
     * 新增对手方交易员
     * @param params
     * @return
     */
    @PostMapping("insertCounterTraderId")
    public Map<String, Object> insertCounterTraderId(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.insertCounterTraderId(params);
    }

    @PostMapping("selectStockInfo")
    public Map<String,Object> selectStockInfo(@RequestBody Map<String,Object> params){
        return stockInfoService.selectStockInfo(params);
    }


    /**
     *
     * @Description //折算率
     * @Param [map]
     * @version 1.0
     * @see
     **/
    @PostMapping("getMoreConvrate")
    public Map<String,Object> getMoreConvrate(@RequestBody Map<String,Object> map){
        Map<String,Object> temp =  interfSysTrdConfigService.getMoreConvrate(map);

            List<StandardCouponReConversionRateDto> lst = (List<StandardCouponReConversionRateDto>)temp.get ("data");
            if(lst.size () == 0) {
                if (StringUtil.isNotEmpty (map.get ("acctNo"))) {
                    return interfSysTrdConfigService.getAllConvrate (map);
                }
            }
        return temp;
    }


    /**
     *
     * @Description // 新股申购、配股配债额度查询
     * @Param [params]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
    **/
    @PostMapping("getPurchQuotaInfo")
    public Map<String,Object> getPurchQuotaInfo(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.getPurchQuotaInfo(params);
    }
    /**
     *
     * @Description // 头寸信息查询汇总
     * @Param [params]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
     **/
    @PostMapping("/queryPositionInfo")
    @ResponseBody
    public List<BaseMap> queryPositionInfo(@RequestBody PositionCashSecRequest requestDto) {
        List<BaseMap> m = etrTradeCommonervice.queryPositionInfo(requestDto);
        return m;
    }
    /**
     *
     * @Description // 组合头寸信息分开
     * @Param [params]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @version 1.0
     * @see
     **/
    @PostMapping("/queryPositionInfoSingle")
    @ResponseBody
    public List<PositionCashSecSingleResp>  queryPositionInfoSingle(@RequestBody PositionCashSecRequest requestDto) {
        List<PositionCashSecSingleResp>  m = etrTradeCommonervice.queryAllPositionInfoSingle(requestDto);
        return m;
    }

    /**
     *
     * @Description // 债券面值接口
     * @Param [params]
     * @version 1.0
     * @see
     **/
    @PostMapping("/selectFaceValue")
    @ResponseBody
    public BigDecimal selectFaceValue(@RequestBody Map<String, Object> params) {
        BigDecimal  m = etrTradeOtherCommonService.selectFaceValue(params);
        return m;
    }

    /**
     * 交易商交易员信息查询接口
     */
    @ResponseBody
    @PostMapping("/getDealerTraderInfo")
    public List<TradeInfoProResponse> selectTraderInfo(@RequestBody TradeInfoProRequest dto ){
        return  interfSysTrdConfigService.getDealerTraderInfo(dto);
    }


    /**
     * 债券现券买入指令提交校验时对于使权不重大的购入价条件判断
     */
    @PostMapping("getBidPriceConInfo")
    public Map<String,Object> getBidPriceConInfo(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.getBidPriceConInfo(params);
    }


    /**
     * SPi测试
     * @param params
     * @return
     */
    @PostMapping("getSppiTestInfo")
    public Map<String,Object> getSppiTestInfo(@RequestBody Map<String,Object> params){
        return interfSysTrdConfigService.getSppiTestInfo(params);
    }
}
