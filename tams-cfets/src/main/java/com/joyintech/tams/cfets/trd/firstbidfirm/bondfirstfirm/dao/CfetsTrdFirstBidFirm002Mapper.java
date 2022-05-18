package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dao;

import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import org.springframework.stereotype.Repository;

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
     * 查询指令流程中及生效的自主投标和代投代缴的投标确认数据
     *
     * @param param
     * @return int
     * @author jiangpeng
     * @date 2022/5/17 16:27
     */
    int selectBidFirmCountByEntrustId(Map<String, Object> param);
}
