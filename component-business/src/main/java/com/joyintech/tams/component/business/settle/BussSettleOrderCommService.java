/*
 * 文件名：BussSettleOrderCommService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：杨智
 * 修改时间：2018年09月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.component.business.settle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import org.springframework.web.bind.annotation.RequestBody;

import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderResultDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;

/**
 * 清算指令共通接口
 */
public interface BussSettleOrderCommService {
    /**
     * @param deal 金融产品交易Entity
     * @param isTotalTrade 是否汇总交易
     * @param bankLendingType 拆借类型
     * @param portfolioId 组合代码
     * @return 清算指令结果
     */
    BussSettleOrderResultDto generateSettleOrder(TrdProductDeal deal, String isTotalTrade, String bankLendingType, String portfolioId
    		,String dealMode,String ieFlag);

    /**
     * @param dto 入参
     * @return 清算指令结果
     */
    BussSettleOrderResultDto generateSettleOrder(BussSettleOrderParameterDto dto);

    static void genOrderSelect(BussSettleOrderResultDto dto, BussTrdTransferOrderDto ret) {
        List<SelectOptionDto> myCapacctList = dto.getMyCapacctInfoList().stream().map(item -> {
            SelectOptionDto optionDto = new SelectOptionDto();
            optionDto.setId(item.getAcctNo());
            optionDto.setText(item.getAcctName());
            return optionDto;
        }).collect(Collectors.toList());
        List<SelectOptionDto> counterCapacctList = dto.getCounterCapacctInfoList().stream().map(item -> {
            SelectOptionDto optionDto = new SelectOptionDto();
            optionDto.setId(item.getAcctNo());
            optionDto.setText(item.getAcctName());
            return optionDto;
        }).collect(Collectors.toList());

        // 画面划款信息，账号信息无默认值，需要下拉勾选（业务共通主件）
        if (!StringUtil.isEmptyList(myCapacctList)) {
            if("null".equals(myCapacctList.get(0).getId())){
                ret.setPayAcctNo(null);
                ret.setPayAcctName(null);
            }else{
                ret.setPayAcctNo(myCapacctList.get(0).getId());
                ret.setPayAcctName(myCapacctList.get(0).getText());
            }  
        }
        if (!StringUtil.isEmptyList(counterCapacctList)) {
            // null转成空字符串 add by renping 20191030 start
            if("null".equals(counterCapacctList.get(0).getId())){
                ret.setRecAcctNo(null);
                ret.setRecAcctName(null);
            }else{
            // null转成空字符串 add by renping 20191030 end
                ret.setRecAcctNo(counterCapacctList.get(0).getId());
                ret.setRecAcctName(counterCapacctList.get(0).getText());
            // null转成空字符串 add by renping 20191030 start
            }  
            // null转成空字符串 add by renping 20191030 end
        }
    }

    List<BussTrdTransferOrderBase> selectOrdersByBizCode(String bizCode);

    List<SelectOptionDto> queryAcctList(@RequestBody Map<String, Object> paramMap);
    
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
    
    List<StlCapacctInfo> selectProdCapacctInfoList(Map<String, Object> paramMap);

    /**
     * 根据证券托管户和通道查询账号
     * @param paramMap 参数
     * @return 查询的账号结果
     */
    List<StlCapacctInfo> getAcctNoByInfo(Map<String, Object> paramMap);

    List<StlCapacctInfo> selectCounterCapacctInfoList(Map<String, Object> paramMap);

    //撤销基金除权登记时，根据前置交易编号查询不到划款指令特殊处理
    List<BussTrdTransferOrderBase> selectOrdersByBizCodeByBTradeId(String bizCode);
}
