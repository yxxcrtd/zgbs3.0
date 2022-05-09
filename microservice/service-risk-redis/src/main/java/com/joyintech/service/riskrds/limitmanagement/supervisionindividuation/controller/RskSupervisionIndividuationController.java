package com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.RskIndividConditionCustom;
import com.joyintech.tams.common.base.entity.RskIndividConditionLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividAssetDelDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividConditionDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividSpecialAssetDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividSpecialStageDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividUniteDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskSupervisionIndividuationDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.service.RskSupervisionIndividuationService;

@RestController("backRskSupervisionIndividuationController_dtx")
@RequestMapping("/RskSupervisionIndividuation")
public class RskSupervisionIndividuationController {

    @Autowired
    private RskSupervisionIndividuationService rskSupervisionIndividuationService;

    /**
     * 画面查询方法
     * 
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForCondition")
    public List<RskIndividConditionDto> findPageForCondition(@RequestBody Map<String, Object> param)
    {
        return rskSupervisionIndividuationService.findPageForCondition(param);
    }

    /**
     * 画面查询方法
     * 
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForUnite")
    public List<RskIndividUniteDto> findPageForUnite(@RequestBody Map<String, Object> param)
    {
        return rskSupervisionIndividuationService.findPageForUnite(param);
    }

    /**
     * 画面查询方法
     * 
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForAssetDel")
    public List<RskIndividAssetDelDto> findPageForAssetDel(@RequestBody Map<String, Object> param)
    {
        return rskSupervisionIndividuationService.findPageForAssetDel(param);
    }

    /**
     * 画面查询方法
     * 
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForSpecial")
    public List<RskIndividSpecialStageDto> findPageForSpecial(@RequestBody Map<String, Object> param)
    {
        return rskSupervisionIndividuationService.findPageForSpecial(param);
    }

    /**
     * 画面查询方法
     * 
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForSpecialAsset")
    public List<RskIndividSpecialAssetDto> findPageForSpecialAsset(@RequestBody Map<String, Object> param)
    {
        return rskSupervisionIndividuationService.findPageForSpecialAsset(param);
    }
    /**
     * 画面查询方法
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForCustomized")
    public List<RskIndividConditionCustom> findPageForCustomized(@RequestBody Map<String, Object> param)
            throws Exception {
        return rskSupervisionIndividuationService.findPageForCustomized(param);
    }
    /**
     * 画面查询方法
     *
     * @return 查询结果集
     * @throws Exception 异常
     */
    @PostMapping("/findPageForCustomizedItem")
    public List<RskIndividConditionLine> findPageForCustomizedItem(@RequestBody Map<String, Object> param)
            throws Exception {
        return rskSupervisionIndividuationService.findPageForCustomizedItem(param);
    }
    @PostMapping("/checkExistsUsing")
    public int checkExistsUsing(@RequestBody Map<String, Object> param)
            throws Exception {
        return rskSupervisionIndividuationService.checkExistsUsing(param);
    }
    @Transactional
    @DTXTransaction
    @PostMapping("/insert")
    public int insert(@RequestBody RskSupervisionIndividuationDto dto) {
        return rskSupervisionIndividuationService.insert(dto);
    }

    @PostMapping("/queryUniteIndIdList")
    public List<SelectOptionDto> queryUniteIndIdList(@RequestBody Map<String, Object> param)
    {
        return rskSupervisionIndividuationService.queryUniteIndIdList(param);
    }

    @PostMapping("/queryUniteIndIdList_RULE")
    public List<SelectOptionDto> queryUniteIndIdList_RULE(@RequestBody Map<String, Object> param)
            throws Exception {
        return rskSupervisionIndividuationService.queryUniteIndIdList_RULE(param);
    }

    @PostMapping("/queryFinprodIdList/{page}/{length}")
    public Page<SelectOptionDto> queryFinprodIdList(@RequestBody Map<String, Object> param,@PathVariable("page") int page, @PathVariable("length") int length)
    {
        return rskSupervisionIndividuationService.queryFinprodIdList(page,length,param);
    }
    @PostMapping("/queryConditionList/{page}/{length}")
    public Page<SelectOptionDto> queryConditionList(@RequestBody Map<String, Object> param,@PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return rskSupervisionIndividuationService.queryConditionList(page,length,param);
    }


    @GetMapping("/getIndType")
    public String getIndType(String indId) {
        return rskSupervisionIndividuationService.getIndType(indId);
    }

    @RequestMapping("getFinprodId/{page}/{length}")
    public Page<SelectOptionDto> getFinprodId(@RequestBody Map<String, Object> param,
                                              @PathVariable("page") int page,
                                              @PathVariable("length") int length) {
        return rskSupervisionIndividuationService.getFinprodId(page, length, param);
    }

    @GetMapping("/queryForSpecial")
    public RskIndividSpecialStageDto queryForSpecial(String uniteId) {
        return rskSupervisionIndividuationService.queryForSpecial(uniteId);
    }

    @GetMapping("/queryForAsset")
    public RskIndividSpecialAssetDto queryForAsset(String uniteId) {
        return rskSupervisionIndividuationService.queryForAsset(uniteId);
    }
    
    @GetMapping("/getVdate")
    public String getVdate(String portfolioId){
    	return rskSupervisionIndividuationService.getVdate(portfolioId);
    }
}
