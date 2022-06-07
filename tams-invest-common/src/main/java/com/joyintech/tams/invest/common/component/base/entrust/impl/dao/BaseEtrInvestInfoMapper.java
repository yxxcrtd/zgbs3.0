package com.joyintech.tams.invest.common.component.base.entrust.impl.dao;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrDistributeKey;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrFinishStatus;
import com.joyintech.tams.common.base.entity.EtrFinishStatusKey;
import com.joyintech.tams.common.base.entity.EtrPledge;
import com.joyintech.tams.common.base.entity.EtrPledgeKey;
import com.joyintech.tams.invest.common.component.base.entrust.dto.BaseEtrFinishAmtDto;
import com.joyintech.tams.invest.common.dto.etr.EtrCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author huwei
 * @version 1.0 2018年9月8日
 * @see BaseEtrInvestInfoMapper
 * @since 1.0
 */
@Repository
public interface BaseEtrInvestInfoMapper {

    /**
     * 委托指令分发查询 <一句话功能简述> <功能详细描述>
     * 
     * @param key EtrDistributeKey
     * @return List
     * @see
     * @since 1.0
     */
    List<EtrDistribute> selectEtrDistributeByPrimaryKey(EtrDistributeKey key);

    /**
     * 查询委托指令分发表中全部数据 <一句话功能简述> <功能详细描述>
     *
     * @return List
     * @see
     * @since 1.0
     */
    List<EtrDistribute> selectAllEtrDistribute();

    /**
     * 委托完成状况表查询 <一句话功能简述> <功能详细描述>
     * 
     * @param key EtrFinishStatusKey
     * @return List
     * @see
     * @since 1.0
     */
    List<EtrFinishStatus> selectEtrFinishStatusByPrimaryKey(EtrFinishStatusKey key);

    /**
     * 委托指令回购质押品查询 <一句话功能简述> <功能详细描述>
     * 
     * @param key EtrPledgeKey
     * @return List
     * @see
     * @since 1.0
     */
    List<EtrPledge> selectEtrPledgeByPrimaryKey(EtrPledgeKey key);

    List<EtrPledge> selectWflEtrPledgeByPrimaryKey(EtrPledgeKey key);

    /**
     * 删除委托指令分发 <一句话功能简述> <功能详细描述>
     * @param key EtrDistributeKey
     * @return int
     * @see
     * @since 1.0
     */
    int deleteEtrDistributeByPrimaryKey(EtrDistributeKey key);

    /**
     * 删除委托完成状况 <一句话功能简述> <功能详细描述>
     * @param key EtrFinishStatusKey
     * @return int
     * @see
     * @since 1.0
     */
    int deleteEtrFinishStatusByPrimaryKey(EtrFinishStatusKey key);

    /**
     * 删除委托指令回购质押品 <一句话功能简述> <功能详细描述>
     * @param key EtrPledgeKey
     * @return int
     * @see
     * @since 1.0
     */
    int deleteEtrPledgeByPrimaryKey(EtrPledgeKey key);

    /**
     * 通过交易员修改委托指令分发 <一句话功能简述> <功能详细描述>
     * @param param Map
     * @return int
     * @see
     * @since 1.0
     */
    int updateEtrDistributeByPrimaryKeySelective(Map<String, Object> param);

    /**
     * 查询委托完成状况表 <一句话功能简述> <功能详细描述>
     * 
     * @param entrustId String
     * @return BaseEtrFinishAmtDto
     * @see
     * @since 1.0
     */
    BaseEtrFinishAmtDto selectEtrFinishAmtDtoByPrimaryKey(String entrustId);
    /**
     * 
     * 根据委托指令批量删除委托指令分发表
     * <功能详细描述>
     * @param list
     * @return
     * @see
     * @since 1.0
     */
    int deletByList(@Param("list") List<EtrDistribute> list);

    /**
     * 删除投标标位
     * <功能详细描述>
     * @param entrustId
     * @return
     * @see
     * @since 1.0
     */
    int deleteBidPrice(String entrustId);

    List<EtrEntrust> selectByPrimaryKeyPtl(Map<String, Object> pageParam);

    String findInvAimByPortIdAndFinId(@Param("portfolioId") String portfolioId, @Param("finprodId") String finprodId);

    String findInvAimByPortIdAndFinType(@Param("portfolioId") String portfolioId, @Param("finprodId") String finprodId);
    EtrEntrustAdd selectAddByPrimaryKeyPtl(Map<String, Object> pageParam);

    List<EtrCashPositionFlowDto> selectEtrClosedCashFlow(String entrustId);

    void updateEtrClosedCashFlow(String seqNo);

    void updateEtrClosedSecFlow(String seqno);

    List<EtrSecPositionFlowDto> selectEtrClosedSecFlow(String entrustId);

    void updatEetrDistribute(EtrEntrust etrEntrust);

    void deleteEtrDistributeInterfTmp(@Param("entrustId") String entrustId);

    /**
     * 根据指令代码删除委托指令明细数据
     * @param entrustId
     * @return
     */
    int deleteEntrustDetail(@Param("entrustId")String entrustId);
}