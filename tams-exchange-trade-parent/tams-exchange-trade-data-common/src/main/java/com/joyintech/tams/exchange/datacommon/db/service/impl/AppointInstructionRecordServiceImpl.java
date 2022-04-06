package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.joyintech.tams.exchange.common.db.domain.AppointInstructionRecord;
import com.joyintech.tams.exchange.datacommon.db.dao.AppointInstructionRecordMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.OrderHistoryMapper;
import com.joyintech.tams.exchange.datacommon.db.service.AppointInstructionRecordService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lifangpeng
 * @date 2022/4/2 11:50
 */
@Service
public class AppointInstructionRecordServiceImpl implements AppointInstructionRecordService {
    @Autowired
    private AppointInstructionRecordMapper appointInstructionRecordMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public void saveBatch(List<AppointInstructionRecord> records) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        AppointInstructionRecordMapper recordMapper = sqlSession.getMapper(AppointInstructionRecordMapper.class);
        AtomicInteger index = new AtomicInteger(0);
        records.forEach(record -> {
            index.getAndIncrement();
            recordMapper.insertSelective(record);
            if (index.get() % 500 == 0) {
                sqlSession.flushStatements();
            }
        });
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public void updateBatch(List<AppointInstructionRecord> records) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        AppointInstructionRecordMapper recordMapper = sqlSession.getMapper(AppointInstructionRecordMapper.class);
        AtomicInteger index = new AtomicInteger(0);
        records.forEach(record -> {
            index.getAndIncrement();
            recordMapper.updateByPrimaryKeySelective(record);
            if (index.get() % 500 == 0) {
                sqlSession.flushStatements();
            }
        });
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public int save(AppointInstructionRecord record) {
        return appointInstructionRecordMapper.insert(record);
    }

    @Override
    public int update(AppointInstructionRecord record) {
        return appointInstructionRecordMapper.updateByPrimaryKeySelective(record);
    }

}
