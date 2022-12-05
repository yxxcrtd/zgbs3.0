package com.joyintech.tams.common.basicdata.marketdata.externaloptions.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.common.basicdata.marketdata.externaloptions.dto.ComExternalOptions002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2019- 12-31 11:35
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ComExternalOptions002Service extends CommonExportService<ComExternalOptions002Dto> implements BaseProcessService<ComExternalOptions002Dto> {

    public List<SuggestDto> getFinprodId(Map<String, Object> params) {

        return restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComExternalOptions/findFinRateInfoByBenchmarkId",
                params, SuggestDto.class);
    }


    public ComExternalOptions002Dto findByPrimaryKey(Map<String, Object> map)
    {
        String str = DictCodeListExt3.基准类型_2_合约标的.getValue(); //07
        //System.out.println(str);
        map.put("BENCHMARK_TYPE2",str);
        return restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                        + "/ComExternalOptions/findByPrimaryKey",
                map, ComExternalOptions002Dto.class);
    }

    @Override
    public void doComplete(ComExternalOptions002Dto entity, OperationType type) {
        restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                        + "/ComExternalOptions/doComplete/" + type.name(),
                entity, AjaxResult.class);
    }


    public String findByBenchmarkId(String finprodId2)
    {
        String str = new String();
        return restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                        + "/ComExternalOptions/findByBenchmarkId?finprodId2="+finprodId2,str,
                String.class);
    }

    public ComExternalOptions002Dto dateCheck(Map<String, Object> params){
        return restTemplate.postForObject(ServiceApplication.CommonBasicDataService
                + "/ComExternalOptions/dateCheck", params, ComExternalOptions002Dto.class);
    }

    public ComExternalOptions002Dto getTargetInfo(Map<String, Object> param) {
        List<ComExternalOptions002Dto> externalOptions002DtoList = restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComExternalOptions/getTargetInfo",
                param, ComExternalOptions002Dto.class);
        if (!StringUtil.isEmptyList(externalOptions002DtoList)){
            return externalOptions002DtoList.get(0);
        }
        return null;
    }
}
