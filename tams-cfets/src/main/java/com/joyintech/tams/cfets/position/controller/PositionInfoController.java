package com.joyintech.tams.cfets.position.controller;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.position.dto.PositionCashRequestDto;
import com.joyintech.tams.cfets.position.dto.PositionInfoRequestDto;
import com.joyintech.tams.cfets.position.service.PositionInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cfets/positionInfo")
@Api(tags = "头寸相关查询")
public class PositionInfoController {

    @Autowired
    private PositionInfoService positionInfoService;
    
    @ApiOperation(value = "交易头寸信息查询", notes = "交易头寸信息查询")
    @PostMapping("/queryTrdPositionHead")
    @ResponseBody
    public Message<List<BaseMap>> queryTrdPositionHead(@RequestBody PositionInfoRequestDto requestDto){
        List<BaseMap> list = new ArrayList<>();
        try{
            list = positionInfoService.queryTrdPositionHead(requestDto);
        }catch (Exception e){
            return new Message<List<BaseMap>>().fail().setMessage(e.getMessage());
        }
        return new Message<List<BaseMap>>().success().setData(list);
    }

    @ApiOperation(value = "指令现金查询", notes = "指令现金查询")
    @PostMapping("/queryEtrAvailableCash")
    @ResponseBody
    public Message<List<BaseMap>> queryEtrAvailableCash(@RequestBody PositionCashRequestDto requestDto) {
        //组合去重
        List<String> portfolioIdList =  requestDto.getPortfolioIdList().stream().distinct().collect(Collectors.toList());
        BaseMap cashMap = positionInfoService.queryEtrAvailableCash(
                portfolioIdList,
                requestDto.getCdate(),
                requestDto.getAcctId(),
                requestDto.getCcy(),
                requestDto.getCalId(),
                requestDto.getFinprodId()
        );
        // 封装现金结果集
        List<BaseMap> data = positionInfoService.assembleCash(cashMap);
        return new Message<List<BaseMap>>().success().setData(data);
    }

    @ApiOperation(value = "指令持仓查询", notes = "指令持仓查询")
    @PostMapping("/queryEtrPosition")
    @ResponseBody
    public Message<List<BaseMap>> queryEtrPosition(@RequestBody PositionInfoRequestDto requestDto) {
        BaseMap secMap = positionInfoService.queryEtrPosition(
                requestDto.getPortfolioIdList(),
                requestDto.getCdate(),
                requestDto.getFinprodId()
        );
        String sellFlag = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        List<BaseMap> data = positionInfoService.assembleSec(sellFlag, secMap);
        return new Message<List<BaseMap>>().success().setData(data);
    }

    @ApiOperation(value = "交易现金查询", notes = "交易现金查询")
    @PostMapping("/queryTrdAvailableCash")
    @ResponseBody
    public Message<List<BaseMap>> queryTrdAvailableCash(@RequestBody PositionCashRequestDto requestDto) {
        BaseMap cashMap = positionInfoService.queryTrdAvailableCash(
                requestDto.getPortfolioIdList(),
                requestDto.getCdate(),
                requestDto.getAcctId(),
                requestDto.getCcy(),
                requestDto.getCalId(),
                requestDto.getAcctTypeList(),
                requestDto.getAcctTypes(),
                requestDto.getFinprodId()
        );
        // 封装现金结果集
        List<BaseMap> data = positionInfoService.assembleCash(cashMap);
        return new Message<List<BaseMap>>().success().setData(data);
    }
    
    @ApiOperation(value = "交易持仓查询", notes = "交易持仓查询")
    @PostMapping("/queryTrdPosition")
    @ResponseBody
    public Message<BaseMap> queryTrdPosition(@RequestBody PositionInfoRequestDto requestDto) {
        return new Message<BaseMap>().success().setData(
                positionInfoService.queryTrdPosition(
                        requestDto.getPortfolioIdList(),
                        requestDto.getCdate(),
                        requestDto.getFinprodId()
                )
        );
    }
    
}
