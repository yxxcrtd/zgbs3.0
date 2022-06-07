package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 一级申购新建MAPPER
 *
 * @author jp
 * @date 2022/04/18
 */
@Repository
public interface CfetsTrdBondFirstSub002Mapper {

    /**
     * 根据指令代码查询申购产品列表
     *
     * @param param 入参 指令代码等
     * @return int
     * @author jiangpeng
     * @date 2022/4/20 14:16
     */
    List<CfetsTrdBondFirstSubInfoDto> selectSubProducts(Map<String, Object> param);

    /**
     * 根据指令代码查询申购交易信息
     *
     * @param param 入参 指令代码等
     * @return int
     * @author jiangpeng
     * @date 2022/4/20 14:16
     */
    List<CfetsTrdBondFirstSubInfoDto> viewPage(Map<String, Object> param);

    /**
     * 投标确认表撤销时根据投标编号 更新的原始数据撤销状态及备注
     *
     * @param bidId  投标编号
     * @param remark 备注
     * @return int
     * @author jiangpeng
     * @date 2022/4/26 13:46
     */
    int updateBidOrder(@Param("bidId") String bidId, @Param("remark") String remark);

    /**
     * 投标确认明细表撤销时根据投标编号 更新的原始数据撤销状态及备注
     *
     * @param bidId  投标编号
     * @param remark 备注
     * @return int
     * @author jiangpeng
     * @date 2022/4/26 13:48
     */
    int updateBidOrderBidPrice(@Param("bidId") String bidId);

    /**
     * 资产+证券账户维度下流程中、有效的申购记录认购量之和需
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/4/26 13:48
     */
    BigDecimal selectSumSubAmtBySecAcctIdAndFin(Map<String, Object> param);

    /**
     * 按组合维度查询指令剩余额度,含流程中
     *
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/4/26 14:02
     */
    BigDecimal selectEtrAvaNum(Map<String, Object> param);

    /**
     * 根据前置交易编号删除匹配关系表
     *
     * @param preTradeId 前置交易编号
     * @return int
     * @author jiangpeng
     * @date 2022/4/28 17:26
     */
    int deleteByPreTradeId(@Param("preTradeId") String preTradeId);

    /**
     * 获取发行人名称
     *
     * @param finprodId
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/5/18 19:36
     */
    String getIssuer(@Param("finprodId") String finprodId);
}
