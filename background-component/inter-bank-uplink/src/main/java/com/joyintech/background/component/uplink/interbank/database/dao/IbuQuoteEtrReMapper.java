package com.joyintech.background.component.uplink.interbank.database.dao;

import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteEtrRe;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteEtrReKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IbuQuoteEtrReMapper {
    int deleteByPrimaryKey(IbuQuoteEtrReKey key);

    int insert(IbuQuoteEtrRe record);

    int insertSelective(IbuQuoteEtrRe record);

    int insertBasicByBasicInfo(IbuQuoteEtrRe record);

    IbuQuoteEtrRe selectByPrimaryKey(IbuQuoteEtrReKey key);

    List<IbuQuoteEtrRe> selectByQuoteId(String quoteId);

    int updateByPrimaryKeySelective(IbuQuoteEtrRe record);

    int updateByPrimaryKey(IbuQuoteEtrRe record);
}