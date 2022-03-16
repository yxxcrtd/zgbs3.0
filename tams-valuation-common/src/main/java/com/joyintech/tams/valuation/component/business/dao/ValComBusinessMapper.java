/*
 * 文件名：DateCalcMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.component.business.dao;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 组件共通服务
 * 
 * @author yanya
 * @version 1.0 2020-03-04
 * @see ValComBusinessMapper
 * @since 1.0
 */
@Repository
public interface ValComBusinessMapper {

    Date getMaxTradeDate(String finprodId);


    /**
     * 资产是否存在交易
     *
     * @param finprodId 金融产品代码
     * @return 交易笔数
     * @see
     * @since 1.0
     */
    int isTransInAsset(String finprodId);

    /**
     * 资产代码
     * @param pageParam
     * @return
     */
    List<SelectOptionDto> getAssetId(Map<String, Object> pageParam);

    /**
     * 产品代码
     * @param pageParam
     * @return
     */
    List<SelectOptionDto> getProdName(Map<String, Object> pageParam);

    /**
     * 资金账户信息
     * @param param 参数
     * @return 债券交易列表
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getTrdAcctId(Map<String, Object> param);

    String getTrdAcctId1(Map<String, Object> param);

    /**
     * 内部科目配置
     * @param param 参数
     * @return 内部科目配置
     * @see
     * @since 1.0
     */
    List<SelectOptionDto> getSubjectInteral(Map<String, Object> param);

    /**
     * @author  殷亚
     * @description 内部科目模糊搜索组件
     * @date 2022-1-19 14:13
     * @param param:
     * @return java.util.List<com.joyintech.framework.select.SuggestDto>
     */
    List<SuggestDto> findSubjectInteral(Map<String, Object> param);

}
