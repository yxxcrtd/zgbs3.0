package com.joyintech.service.basicdata.marketdata.externaloptions.controller;

import java.util.List;
import java.util.Map;

import com.joyintech.service.basicdata.marketdata.externaloptions.service.ComExternalOptionsService001;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions001Dto;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions002Dto;
import com.joyintech.service.basicdata.marketdata.externaloptions.service.ComExternalOptionsService;
import com.joyintech.workflow.engine.constant.OperationType;

/**
 * @author 姚轶文
 * @create 2019- 12-31 10:42
 */
@RestController("serviceComExternalOptionsController")
@RequestMapping("/ComExternalOptions")
public class ComExternalOptionsController extends BaseController {

    @Autowired
    ComExternalOptionsService comExternalOptionsService;
    @Autowired
    ComExternalOptionsService001 comExternalOptionsService001;

    @PostMapping("/findFinRateInfoByBenchmarkId")
    public List<SuggestDto> findFinRateInfoByBenchmarkId(@RequestBody Map<String, Object> params)
    {
        return comExternalOptionsService.findFinRateInfoByBenchmarkId(params);
    }

    @PostMapping("/findByBenchmarkId")
    public String findByBenchmarkId(String finprodId2)
    {
        return comExternalOptionsService.findByBenchmarkId(finprodId2);
    }

    @PostMapping("/findByPrimaryKey")
    public ComExternalOptions002Dto findByPrimaryKey(@RequestBody Map<String, Object> map) {
        return comExternalOptionsService.findByPrimaryKey(map);
    }

    @PostMapping(value = "doComplete/{action}")
    public void doComplete(@RequestBody ComExternalOptions002Dto dto, @PathVariable("action") String action) {
        comExternalOptionsService.doComplete(dto, OperationType.valueOf(action));
    }


    @PostMapping("/selectList")
    public List<ComExternalOptions001Dto> selectList(@RequestBody Map<String, Object> params)
    {
        return comExternalOptionsService.selectList(params);
    }

    /**
     * 列表查询
     * @return 列表信息
     * @throws Exception 异常
     */
    @PostMapping("findPage/{page}/{length}")
    public Page<ComExternalOptions001Dto> queryPageList(@RequestBody Map<String, Object> params, @PathVariable("page") int page, @PathVariable("length") int length){
        return comExternalOptionsService001.queryPageList(page,length,params);
    }



    @PostMapping("/findTargetId")
    public List<SuggestDto> findTargetId(@RequestBody Map<String, Object> params)
    {
        return comExternalOptionsService.findTargetId(params);
    }

    @PostMapping("/findFinprodId")
    public List<SuggestDto> findFinprodId(@RequestBody Map<String, Object> params)
    {
        return comExternalOptionsService.findFinprodId(params);
    }
    /**
     *
     * 资产信息检索查询
     * <功能详细描述>
     * @param
     * @return
     * @see
     * @since 1.0
     */
    @PostMapping("/getTargetInfo")
    public List<ComExternalOptions002Dto>  getTargetInfo(@RequestBody Map<String, Object> param) {
        return comExternalOptionsService.getTargetInfo(param);
    }

    /**
     * 获取增值税则表 - 不分页
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping("queryAllList")
    public List<ComExternalOptions001Dto> queryAllList(@RequestBody Map<String, Object> param){
        return comExternalOptionsService.queryAllList(param);
    }
    
    /**
     * 估值日期验证
     * @param params
     * @return
     */
    @PostMapping("/dateCheck")
    public ComExternalOptions002Dto dateCheck(@RequestBody Map<String, Object> params) {
        return comExternalOptionsService.dateCheck(params);
    }

}
