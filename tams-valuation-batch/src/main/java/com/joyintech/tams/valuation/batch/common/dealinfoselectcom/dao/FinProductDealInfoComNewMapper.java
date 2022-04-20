/*
 * 文件名：BatchChildDispatchMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：maodongjin
 * 修改时间：2018年5月5日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dao;

import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 交易读取共通
 * 〈功能详细描述〉
 * @author wudebao
 * @version 1.0 2018年5月5日
 * @see FinProductDealInfoComNewMapper
 * @since 1.0
 */
@Repository
public interface FinProductDealInfoComNewMapper {
 
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @return
     */
    public List<FinProductDealInfoComDTO> selectDealInfo(Map<String, Object> param);
    
    

}
