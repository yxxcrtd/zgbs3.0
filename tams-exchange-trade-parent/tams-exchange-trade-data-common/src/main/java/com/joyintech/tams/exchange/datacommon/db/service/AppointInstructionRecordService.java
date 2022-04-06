package com.joyintech.tams.exchange.datacommon.db.service;

import com.joyintech.tams.exchange.common.db.domain.AppointInstructionRecord;

import java.util.List;

/**
 * @author lifangpeng
 * @date 2022/4/2 11:50
 */
public interface AppointInstructionRecordService {

    /**
     * 批量保存指令分发数据
     *
     * @param records 指令分发数据
     * @return 保存结果
     */
    void saveBatch(List<AppointInstructionRecord> records);

    /**
     * 批量修改指令分发数据
     *
     * @param records 指令分发数据
     * @return 保存结果
     */
    void updateBatch(List<AppointInstructionRecord> records);

    /**
     * 保存指令分发数据
     *
     * @param record 指令分发数据
     * @return 保存结果
     */
    int save(AppointInstructionRecord record);

    /**
     * 修改指令分发数据
     *
     * @param record 指令分发数据
     * @return 保存结果
     */
    int update(AppointInstructionRecord record);
}
