package com.joyintech.service.riskrds.limitmanagement.rsksupervisionriskanalyze.controller;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.riskrds.limitmanagement.rsksupervisionriskanalyze.dto.RskSupervisionRiskAnalyzeDto;
import com.joyintech.service.riskrds.limitmanagement.rsksupervisionriskanalyze.service.RskSupervisionRiskAnalyzeService;
import com.joyintech.tams.constant.DictCodeListExt2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: tams-invest-parent
 * @description: RskSupervisionRiskAnalyzeController
 * @author: liuwei
 * @create: 2018-11-01 14:37
 **/
@RestController("backRskSupervisionRiskAnalyzeController")
@RequestMapping("/RskSupervisionRiskAnalyze")
public class RskSupervisionRiskAnalyzeController {
    /**
     * 指数参数管理Service
     */
    @Autowired
    private RskSupervisionRiskAnalyzeService rskSupervisionRiskAnalyzeService;

    /**
     * 画面查询方法
     *
     * @param param 画面参数
     * @param page 页码数
     * @param length 页面长度
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<RskSupervisionRiskAnalyzeDto> selectAll(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return rskSupervisionRiskAnalyzeService.selectForPage(page, length, param);
    }
    
    @PostMapping("findPageInit/{page}/{length}")
    public Page<RskSupervisionRiskAnalyzeDto> selectAllInit(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return rskSupervisionRiskAnalyzeService.selectForPageInit(page, length, param);
    }
    
    @PostMapping("findPageMx/{page}/{length}")
    public Page<RskSupervisionRiskAnalyzeDto> selectAllMx(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return rskSupervisionRiskAnalyzeService.selectForPageMx(page, length, param);
    }
    
    @PostMapping("findPageNx/{page}/{length}")
    public Page<RskSupervisionRiskAnalyzeDto> selectAllNx(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return rskSupervisionRiskAnalyzeService.selectForPageNx(page, length, param);
    }
    
    @PostMapping("findPageFu/{page}/{length}")
    public Page<RskSupervisionRiskAnalyzeDto> selectAllFu(@RequestBody Map<String, Object> param,
                                                        @PathVariable("page") int page, @PathVariable("length") int length)
    {
        return rskSupervisionRiskAnalyzeService.selectForPageFu(page, length, param);
    }

    @PostMapping("getUserName/{page}/{length}")
    public Page<SelectOptionDto> getUserName(@RequestBody Map<String, Object> param,
                                             @PathVariable("page") int page,
                                             @PathVariable("length") int length) {
        param.put("roleName", DictCodeListExt2.角色岗位_产品设计岗.getValue());
        return rskSupervisionRiskAnalyzeService.selectUserName(param,page,length);
    }

    @PostMapping("selectIndName/{page}/{length}")
    public Page<SelectOptionDto> selectIndName(@RequestBody Map<String, Object> map,@PathVariable("page") int page, @PathVariable("length") int length){
        return rskSupervisionRiskAnalyzeService.selectIndName(map,page,length);
    }


    @PostMapping("selectGroupName/{page}/{length}")
    public Page<SelectOptionDto> selectGroupName(@RequestBody Map<String, Object> map,@PathVariable("page") int page, @PathVariable("length") int length){
        return rskSupervisionRiskAnalyzeService.selectGroupName(map,page,length);
    }
}
