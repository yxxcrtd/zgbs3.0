package com.joyintech.tams.cfets.etr.bond.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface WebSocketMapper {

    int select(Map<String, Object> map);
    
    void insert(Map<String, Object> map);

    void update(Map<String, Object> map);

    void insertFlows(Map<String, Object> map);

    void deleteFlows(Map<String, Object> map);
}
