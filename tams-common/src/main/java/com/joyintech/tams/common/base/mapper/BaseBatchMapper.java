package com.joyintech.tams.common.base.mapper;

/**
 * @author Hanyu
 */
public interface BaseBatchMapper<T, K> {
    int insert(T entity);

    int insertSelective(T entity);

    int updateByPrimaryKey(T entity);

    int updateByPrimaryKeySelective(T entity);

    int deleteByPrimaryKey(K pk);
}
