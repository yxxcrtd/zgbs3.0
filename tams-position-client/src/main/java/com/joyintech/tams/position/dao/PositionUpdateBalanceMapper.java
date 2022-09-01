package com.joyintech.tams.position.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PositionUpdateBalanceMapper {

    int select(Map<String, Object> map);
    
    void insert(Map<String, Object> map);

    void update(Map<String, Object> map);

    void insertFlows(Map<String, Object> map);

    void deleteFlows(Map<String, Object> map);

    String selectSysParaByCode(Map<String, Object> map);
}
