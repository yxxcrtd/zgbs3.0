package com.joyintech.tams.common.base.dao;

import com.joyintech.tams.common.base.entity.FinProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FinProductMapper {
    int deleteByPrimaryKey(String finprodId);

    int insert(FinProduct record);

    int insertSelective(FinProduct record);

    int insertBasicByBasicInfo(FinProduct record);

    FinProduct selectByPrimaryKey(String finprodId);

    FinProduct selectByFinprodMarketId(String finprodMarketId);

    FinProduct selectByFinprodMarketIdAndType2(@Param("finprodMarketId") String finprodMarketId,@Param("finprodType2") String finprodType2);

    int updateByPrimaryKeySelective(FinProduct record);

    int updateByPrimaryKey(FinProduct record);

    List<String> getSubProd(String finprodId);

    int updateSonProdByPProd(FinProduct record);

    int updateForTenderBound(FinProduct record);
}