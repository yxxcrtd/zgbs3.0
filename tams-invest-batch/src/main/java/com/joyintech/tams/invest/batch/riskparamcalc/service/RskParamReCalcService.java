/*
 * 文件名：RskParamReCalcService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.riskparamcalc.service;

import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.invest.batch.riskparamcalc.dao.RskParamReCalcMapper;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskInterfDataDto;
import com.joyintech.tams.invest.common.batchList.dao.RskIndicatorParaCalcdetailListMapper;
import com.joyintech.tams.invest.common.batchList.service.BatchListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 批处理-指标重新计算-Service
 * 
 * @author 裴家俊
 * @version 1.0 2020年4月17日
 * @see RskParamReCalcService
 * @since 1.0
 */
@Service
public class RskParamReCalcService {

    @Autowired
    private  BatchListService<RskIndicatorParaCalcdetail> detailsService;
    
    @Autowired
    private RskIndicatorParaCalcdetailListMapper detailsMapper;

    /**
     * 批处理-指标重新计算-Mapper
     */
    @Autowired
    private RskParamReCalcMapper mapper;

    /**
     * 取得 所有预占实占信息
     * 
     * @param infoList 产品代码
     * @param sysDate 平台日期
     * @return 所有预占实占信息
     */
    public List<RskInterfDataDto> getOcpInfoList(List<String> infoList, Date sysDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("sysDate", sysDate);
        param.put("infoList", infoList);
        return mapper.queryOcpInfo(param);
    }

    /**
     * 批量保存参数计算流水
     * 
     * @param details 参数计算流水
     */
    @Transactional(rollbackFor = Throwable.class)
    public void saveDetails(List<RskIndicatorParaCalcdetail> details){
        mapper.deleteRskIndParaDtl(details);
        detailsService.setBatchListMapper(detailsMapper);
        detailsService.insertBatchList(details);
    }

    public List<RskInterfDataDto> getOcpInfoListToday(List<String> prodIdList, Date sysDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("sysDate", sysDate);
        param.put("infoList", prodIdList);
        return mapper.getOcpInfoListToday(param);
    }
}
