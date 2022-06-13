package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dao;


import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.common.base.entity.MstBondValuationInfoKey;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoDto;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MskCreditInfoProMapper {

    int deleteByPrimaryKey(MskCreditInfoProSaveRequest key);

    int insert(MskCreditInfoProSaveRequest record);

    int insertSelective(MskCreditInfoProSaveRequest record);

    int insertBasicByBasicInfo(MskCreditInfoProSaveRequest record);

    MskCreditInfoProSaveRequest selectByPrimaryKey(MskCreditInfoProSaveRequest key);

    int updateByPrimaryKeySelective(MskCreditInfoProSaveRequest record);

    int updateByPrimaryKey(MskCreditInfoProSaveRequest record);

    List<MskCreditInfoDto> queryForPage(Map<String, Object> pageParam);

    List<MskCreditInfoProSaveRequest> select(Map<String, Object> params);

    List<MskCreditInfoProSaveRequest> selectByKey(Map<String, Object> map);

    List<String> getBusinessData(WflValidInputParameter parameter);

    MskCreditInfoProSaveRequest getBasicInfo(Map<String, Object> map);

    List<SelectOptionDto> getFinprodMarketInfo(Map<String, Object> pageParam);
}
