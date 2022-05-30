package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 一级投标确认mapper
 *
 * @author jiangpeng
 * @date 2022/5/17 14:33
 */
@Repository
public interface CfetsTrdFirstBidFirm002Mapper {

    /**
     * 查询新建页面初始化数据
     *
     * @param param 入参
     * @return java.util.List<com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmProductDto>
     * @author jiangpeng
     * @date 2022/5/17 15:12
     */
    List<CfetsTrdFirstBidFirmInfoDto> selectProductsInit(Map<String, Object> param);

    /**
     * 查询是否存在分销买入数据
     *
     * @param param
     * @return java.lang.String
     * @author jiangpeng
     * @date 2022/5/25 19:19
     */
    String selectBidModelInData(Map<String, Object> param);

    /**
     * 查询指令流程中及生效的自主投标和代投代缴的投标确认数据
     *
     * @param param
     * @return int
     * @author jiangpeng
     * @date 2022/5/17 16:27
     */
    int selectBidFirmCountByEntrustId(Map<String, Object> param);

    /**
     * 投标确认表撤销时根据投标编号 更新的原始数据撤销状态及备注
     *
     * @param bidId  投标编号
     * @param remark 备注
     * @return int
     * @author jiangpeng
     * @date 2022/5/21 17:12
     */
    int updateBidOrder(@Param("bidId") String bidId, @Param("remark") String remark);

    /**
     * 投标确认明细表撤销时根据投标编号 更新的原始数据撤销状态
     *
     * @param bidId 投标编号
     * @return int
     * @author jiangpeng
     * @date 2022/5/21 17:12
     */
    int updateBidOrderBidPrice(@Param("bidId") String bidId);

    /**
     * 一级投标确认交易查看
     * @param pageParam
     * @return
     */
    List<CfetsTrdFirstBidFirmInfoDto> viewPage(Map<String, Object> pageParam);

    /**
     * 获取利率
     *
     * @param pageParam
     * @return java.math.BigDecimal
     * @author jiangpeng
     * @date 2022/5/25 14:05
     */
    BigDecimal getRate(Map<String, Object> pageParam);
}
