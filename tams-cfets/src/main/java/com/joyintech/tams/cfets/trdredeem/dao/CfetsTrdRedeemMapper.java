package com.joyintech.tams.cfets.trdredeem.dao;



import com.joyintech.tams.cfets.trd.bond.dto.PtlAllocationDto;
import com.joyintech.tams.cfets.trdredeem.dto.CfetsTrdRedeemRegInfoListResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * 银行间交易成交确认
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdRedeemMapper
 * @since 1.0
 */
@Repository
public interface CfetsTrdRedeemMapper {

    /**
     * 查询所有登记信息
     * @param pageParam
     * @return
     */
    List<CfetsTrdRedeemRegInfoListResponse> queryAllRedeemRegInfoList(Map<String, Object> pageParam);

    /**
     * 查询组合分配
     * @param pageParam
     * @return
     */
    List<PtlAllocationDto> queryCfetsBussCombDisList(Map pageParam);
    /**
     * 判断是否存在登记数据
     * @param pageParam
     * @return
     */
    List<CfetsTrdRedeemRegInfoListResponse> isExistRegInfo(Map<String, Object> pageParam);

}
