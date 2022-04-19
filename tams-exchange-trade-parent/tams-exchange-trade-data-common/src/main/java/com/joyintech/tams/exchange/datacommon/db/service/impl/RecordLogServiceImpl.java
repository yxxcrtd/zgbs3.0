package com.joyintech.tams.exchange.datacommon.db.service.impl;

import com.joyintech.tams.exchange.common.db.domain.RecordLog;
import com.joyintech.tams.exchange.datacommon.db.dao.RecordLogMapper;
import com.joyintech.tams.exchange.datacommon.db.service.RecordLogService;
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
public class RecordLogServiceImpl implements RecordLogService {
    @Autowired
    private RecordLogMapper recordLogMapper;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public void saveBatch(List<RecordLog> records) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        RecordLogMapper recordMapper = sqlSession.getMapper(RecordLogMapper.class);
        AtomicInteger index = new AtomicInteger(0);
        records.forEach(record -> {
            index.getAndIncrement();
            recordMapper.insert(record);
            if (index.get() % 500 == 0) {
                sqlSession.flushStatements();
            }
        });
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public void updateBatch(List<RecordLog> records) {
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        RecordLogMapper recordMapper = sqlSession.getMapper(RecordLogMapper.class);
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
    public int save(RecordLog record) {
        return recordLogMapper.insert(record);
    }

    @Override
    public int update(RecordLog record) {
        return recordLogMapper.updateByPrimaryKeySelective(record);
    }

}
