/*
 * 文件名：SettleDataClearService.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年08月10日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.service.basicdata.dataclear.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.dataclear.dao.SettleDataClearMapper;
import com.joyintech.service.basicdata.dataclear.dto.SettleBatchResultDto;
import com.joyintech.tams.common.base.dao.SettleBatchParaMapper;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 王波
 * @date 2020年08月10日
 * @see SettleDataClearService
 * @since 1.0
 */
@Service
public class SettleDataClearService extends BaseService<SettleBatchResultDto> {
    @Autowired
    private SettleDataClearMapper settleDataClearMapper;

    @Autowired
    private SettleBatchParaMapper settleBatchParaMapper;

    public Page<SettleBatchResultDto> findLogPage(int page, int length,
                                                  Map<String, Object> pageParam) {
        String dealDateStr = (String)pageParam.get("dealDate");
        String portfolioId = (String)pageParam.get("portfolioId");
        Date dealDate = DateUtil.parse(dealDateStr);
        pageParam.put("dealDate", dealDate);
        if(StringUtil.isNotEmpty(portfolioId)) {
            pageParam.put("portfolioIdList", StringUtil.splitToList(GlobalConstant.COMMA, portfolioId));
        }
        return super.queryForPage(page, length, ()->settleDataClearMapper.selectBatch(pageParam));
    }

    /**
     * 检查是否有批处理任务正在执行
     * @return 0: 没有 >0 ：有
     */
    @DataSource(DataSourceType.OTHER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int checkIsBatchRunning() {
        return settleDataClearMapper.checkIsBatchRunning();
    }

    /**
     * 插入 数据清算参数表
     * @param settleBatchParas  数据清算参数
     * @return 插入数量
     */
    public Integer insertSettleTransParas(List<SettleBatchPara> settleBatchParas) {
        Integer count = 0;
        if (CollectionUtils.isNotEmpty(settleBatchParas)) {
            for (SettleBatchPara settleBatchPara : settleBatchParas) {
                count += settleBatchParaMapper.insertSelective(settleBatchPara);
            }
        }
        return count;
    }
}
