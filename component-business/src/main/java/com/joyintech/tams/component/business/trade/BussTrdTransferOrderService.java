/*
 * 文件名：BussTrdTransferOrderService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年09月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.trade;

import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;

public interface BussTrdTransferOrderService {
    /**
     * 默认entity名称
     */
    String DEFAULT_ENTITY_NAME = "bussTrdTransferOrder";

    /**
     * 验证逻辑
     * @param dto 序列化结果
     * @return 验证结果
     */
    List<ValidMessage> check(List<BussTrdTransferOrderDto> dto);

    /**
     * 组装结果dto
     * @param dto 序列化结果
     * @return 输出dto
     */
    List<BussTrdTransferOrderDto> save(List<BussTrdTransferOrderDto> dto);

    /**
     * 指令生成
     *
     * @param param 参数
     * @return 指令
     * @see
     * @since 1.0
     */
    BussTrdTransferOrderDto generateOrder(BussSettleOrderParameterDto param);

    List<SelectOptionDto> queryAcctList(Map<String, Object> paramMap);
    
    /**
     * 根据账号查询清算账户表
     * 
     * @param ccy 币种
     * @param busiAcctNo 账号
     * @return 清算账户表
     * @see
     * @since 1.0
     */
    StlCapacctInfo queryStlCapacctInfoByBusiAcctNo(String ccy, String busiAcctNo);

}
