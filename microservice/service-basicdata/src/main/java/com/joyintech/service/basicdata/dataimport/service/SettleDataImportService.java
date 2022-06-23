/*
 * 文件名：SettleDataImport001Service.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年08月06日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.service.basicdata.dataimport.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.basicdata.dataimport.dao.SettleDataImportMapper;
import com.joyintech.service.basicdata.dataimport.dto.SettleTransResultDto;
import com.joyintech.tams.common.base.dao.SettleTransParaMapper;
import com.joyintech.tams.common.base.entity.SettleTransPara;

/**
 * @author 王波
 * @date 2020年08月06日
 * @see SettleDataImportService
 * @since 1.0
 */
@Service
public class SettleDataImportService extends BaseService<SettleTransResultDto> {

    @Autowired
    private SettleDataImportMapper settleDataImportMapper;

    @Autowired
    private SettleTransParaMapper settleTransParaMapper;

    public Page<SettleTransResultDto> findLogPage(int page, int length,
                                                  Map<String, Object> pageParam) {
        String importDateStr = (String)pageParam.get("importDate");
        Date importDate = DateUtil.parse(importDateStr);
        pageParam.put("importDate", importDate);
        return super.queryForPage(page, length, ()->settleDataImportMapper.selectBatch(pageParam));
    }

    /**
     * 插入执行的数据
     * @param settleTransParas 成交数据明细导入参数表
     * @return 插入的数量
     */
    public Integer insertSettleTransParas(List<SettleTransPara> settleTransParas) {
        Integer count = 0;
        if (CollectionUtils.isNotEmpty(settleTransParas)) {
            for (SettleTransPara settleTransPara : settleTransParas) {
                count += settleTransParaMapper.insertSelective(settleTransPara);
            }
        }

        return count;
    }

    /**
     * 检查是否有批处理任务正在执行
     * @return 0: 没有 >0 ：有
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int checkIsBatchRunning() {
        return settleDataImportMapper.checkIsBatchRunning();
    }
}
