package com.joyintech.tams.cfets.trd.repo.dao;

import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteCrepDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteCrepPledgeDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteEntCrepPledgeDto;
import com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteEtrReDto;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.cfets.trd.repo.dto.CfetsRepoDealEntrustOrderDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 本币交易-质押式回购交易 Mapper
 *
 * @author jiangpeng
 * @date 2021/12/4 15:39
 */
@Repository
public interface CfetsIbuQuoteRepo003Mapper {

    /**
     * 根据报价编号查询查询质押式回购对话报价信息
     *
     * @param quoteId String
     * @return IbuQuoteCrepDto
     */
    IbuQuoteCrepDto selectWflQuoteCrepByKey(String quoteId);

    /**
     * 根据报价编号查询查询质押式回购报价质押品信息
     *
     * @param quoteId String
     * @return IbuQuoteCrepPledgeDto
     */
    List<IbuQuoteCrepPledgeDto> selectWflQuoteCrepPledgeByKey(String quoteId);

    /**
     * 根据报价编号，查询银行间报价指令关系业务表数据
     *
     * @param quoteId 报价编号
     * @return 银行间报价指令关系业务表数据
     */
    List<IbuQuoteEtrReDto> selectWflQuoteEtrReDataByQuoteId(@Param("quoteId") String quoteId);

    /**
     * @param quoteId 报价编号
     * @return java.util.List<com.joyintech.background.component.uplink.interbank.business.dto.IbuQuoteEntCrepPledgeDto>
     * @author jiangpeng
     * @date 2022/1/4 14:51
     */
    List<IbuQuoteEntCrepPledgeDto> selectWflQuoteEntCrepPledgeDataByQuoteId(@Param("quoteId") String quoteId);

    List<CfetsRepoDealQuotedPrice003Dto>  selectQuoteCrepPledgeForDzh(Map<String,Object> map);

    List<CfetsRepoDealQuotedPrice003Dto>  selectWflQuoteCrepPledgeForDzh(Map<String,Object> map);

    /**
     * 委托列表初始化
     *
     * @param param 条件
     * @return 指令列表
     */
    List<CfetsRepoDealEntrustOrderDto> listEntrust(Map<String, Object> param);
}
