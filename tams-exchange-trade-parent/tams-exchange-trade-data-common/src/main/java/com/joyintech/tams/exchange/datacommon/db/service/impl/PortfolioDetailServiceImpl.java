package com.joyintech.tams.exchange.datacommon.db.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetail;
import com.joyintech.tams.exchange.common.db.domain.PortfolioDetailExample;
import com.joyintech.tams.exchange.datacommon.cache.impl.AsyncServiceImpl;
import com.joyintech.tams.exchange.datacommon.db.dao.IMybatisMapper;
import com.joyintech.tams.exchange.datacommon.db.dao.PortfolioDetailMapper;
import com.joyintech.tams.exchange.datacommon.db.service.PortfolioDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author swwan
 * @date 2022/5/10 - 22:29
 */
@Service
public class PortfolioDetailServiceImpl extends AsyncServiceImpl<String, PortfolioDetail> implements PortfolioDetailService {

    @Resource
    private PortfolioDetailMapper portfolioDetailMapper;

    @Override
    public long countByExample(PortfolioDetailExample example) {
        return portfolioDetailMapper.countByExample(example);
    }

    @Override
    public int deleteByExample(PortfolioDetailExample example) {
        return portfolioDetailMapper.deleteByExample(example);
    }

    @Override
    public int deleteByPrimaryKey(String id) {
        return portfolioDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(PortfolioDetail record) {
        return portfolioDetailMapper.insert(record);
    }

    @Override
    public int insertSelective(PortfolioDetail record) {
        return portfolioDetailMapper.insertSelective(record);
    }

    @Override
    public List<PortfolioDetail> selectByExample(PortfolioDetailExample example) {
        return portfolioDetailMapper.selectByExample(example);
    }

    @Override
    public PortfolioDetail selectByPrimaryKey(String id) {
        return portfolioDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByExampleSelective(PortfolioDetail record, PortfolioDetailExample example) {
        return portfolioDetailMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(PortfolioDetail record, PortfolioDetailExample example) {
        return portfolioDetailMapper.updateByExample(record, example);
    }

    @Override
    public int updateByPrimaryKeySelective(PortfolioDetail record) {
        return portfolioDetailMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(PortfolioDetail record) {
        return portfolioDetailMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateBatch(List<PortfolioDetail> list) {
        return portfolioDetailMapper.updateBatch(list);
    }

    @Override
    public void clearCache() {
        memory_cache.clear();
    }

    @Override
    protected void update(PortfolioDetail portfolioDetail) {
        saveMemoryCache(portfolioDetail);
    }

    // TODO 完善内存缓存
    private void saveMemoryCache(PortfolioDetail portfolioDetail) {

    }

    @Override
    protected void save(PortfolioDetail portfolioDetail) {
        update(portfolioDetail);
    }

    @Override
    protected void delete(String PK) {
        PortfolioDetail portfolioDetail = memory_cache.get(PK);
        if (ObjectUtil.isNotNull(portfolioDetail)) {
            deleteMemoryCache(portfolioDetail);
        }
    }

    // TODO 完善内存缓存
    private void deleteMemoryCache(PortfolioDetail portfolioDetail) {

    }

    @Override
    public IMybatisMapper<PortfolioDetail, String, PortfolioDetailExample> getMapper() {
        return portfolioDetailMapper;
    }
}
