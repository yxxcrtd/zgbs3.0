/*
 * 文件名：InterfPayhsrcsecinfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年1月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.common.dto;

import com.joyintech.tams.common.base.entity.FinStdsecConverseRadio;
import com.joyintech.tams.common.base.entity.InterfPayhsrcsecinfo;
import com.joyintech.tams.common.base.entity.MstBondMarketInfo;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.component.business.report.asset.project.dto.BussRepEquityInfoDto;

import javax.validation.Valid;
import java.util.List;

/**
 * 万德债券资产DTO
 * 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年1月3日
 * @see InterfPayhsrcsecinfoDto
 * @since 1.0
 */
public class InterfPayhsrcsecinfoDto extends InterfPayhsrcsecinfo {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 金融产品代码，组装规则：金融产品类型（投管分类） _ 市场代码 _通道代码，如重复，则加4位流水后缀（0001开始）
     */
    private String finprodId;

    /**
     * 金融产品信息List
     */
    private List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList;

    /**
     * 债券估值行情list
     */
    private List<MstBondValuationInfo> mstBondValuationInfoList;

    /**
     * 债券市场行情list
     */
    private List<MstBondMarketInfo> mstBondMarketInfoList;

    /**
     * 回购标准券折算率list
     */
    private List<FinStdsecConverseRadio> finStdsecConverseRadioList;

    private List<String> secidList;

    /**
     * 中债报送DTO
     */
    @Valid
    private BussFinBondRepInfoDto bussFinBondRepInfoDto;

    /**
     * 业务组件--权益类中债报送
     */
    @Valid
    private BussRepEquityInfoDto bussRepEquityInfoDto;

    /**
     * 存放中债校验不通过的债券信息
     */
    private List<String> errorBondList;


    /**
     * 获取 secidList
     *
     * @return secidList.
     */
    public List<String> getSecidList() {
        return secidList;
    }


    /**
     * 设置 secidList
     *
     * @param secidList secidList
     */
    public void setSecidList(List<String> secidList) {
        this.secidList = secidList;
    }


    /**
     * 获取 finprodId
     *
     * @return finprodId.
     */
    public String getFinprodId() {
        return finprodId;
    }


    /**
     * 设置 finprodId
     *
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }


    /**
     * 获取 baseFinProductBaseInfoList
     *
     * @return baseFinProductBaseInfoList.
     */
    public List<BaseFinProductBaseInfoDto> getBaseFinProductBaseInfoList() {
        return baseFinProductBaseInfoList;
    }


    /**
     * 设置 baseFinProductBaseInfoList
     *
     * @param baseFinProductBaseInfoList baseFinProductBaseInfoList
     */
    public void setBaseFinProductBaseInfoList(List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList) {
        this.baseFinProductBaseInfoList = baseFinProductBaseInfoList;
    }


    public List<MstBondValuationInfo> getMstBondValuationInfoList() {
        return mstBondValuationInfoList;
    }


    public void setMstBondValuationInfoList(List<MstBondValuationInfo> mstBondValuationInfoList) {
        this.mstBondValuationInfoList = mstBondValuationInfoList;
    }


    public List<MstBondMarketInfo> getMstBondMarketInfoList() {
        return mstBondMarketInfoList;
    }


    public void setMstBondMarketInfoList(List<MstBondMarketInfo> mstBondMarketInfoList) {
        this.mstBondMarketInfoList = mstBondMarketInfoList;
    }


    public List<FinStdsecConverseRadio> getFinStdsecConverseRadioList() {
        return finStdsecConverseRadioList;
    }


    public void setFinStdsecConverseRadioList(List<FinStdsecConverseRadio> finStdsecConverseRadioList) {
        this.finStdsecConverseRadioList = finStdsecConverseRadioList;
    }

    public List<String> getErrorBondList() {
        return errorBondList;
    }

    public void setErrorBondList(List<String> errorBondList) {
        this.errorBondList = errorBondList;
    }

}
