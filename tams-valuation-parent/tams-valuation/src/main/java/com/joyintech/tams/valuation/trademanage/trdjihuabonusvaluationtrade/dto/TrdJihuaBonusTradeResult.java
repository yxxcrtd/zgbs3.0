/*
 * 文件名：FundDealInfoRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.trademanage.trdjihuabonusvaluationtrade.dto.TrdJihuaBonusValuationTradeDto;

/**
 * 资产类交易接口基金类资产
 *
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see TrdJihuaBonusTradeResult
 * @since 1.0
 */
public class TrdJihuaBonusTradeResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private TrdJihuaBonusValuationTradeDto trdJihuaBonusValuationTradeDto;

    /**
     * @param result
     */
    public TrdJihuaBonusTradeResult(FundDealInfoForInterfRecCommonResult result) {
        this.listValDealAccounting = result.getListValDealAccounting();
        this.listValDealAccountingDetail = result.getListValDealAccountingDetail();
        this.listValFinProductDeal = result.getListValFinProductDeal();
        this.listValFinProductDealExt = result.getListValFinProductDealExt();
        this.listValTradeFeeDetail = result.getListValTradeFeeDetail();
        this.listValInterflog = result.getListValInterflog();
        this.listInterfProductDealBookCommonDto = result.getListInterfProductDealBookCommonDto();
        this.listValBokDetailId = result.getListValBokDetailId();
        this.setListValOrders(result.getListValOrders());
        this.setListValOrdersExt(result.getListValOrdersExt());
        this.setDataNum(result.getDataNum());
        this.paramMap = result.getParamMap();
    }

    /**
     *
     */
    public TrdJihuaBonusTradeResult() {
        super();
    }
    /**
     * 金融产品交易表
     */
    private List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();;

    /**
     * 估值核算交易扩展
     */
    private List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();

    /**
     * 账务明细代码表
     */
    private List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();

    /**
     * 入账信息表
     */
    private List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();

    /**
     * 账务明细表
     */
    private List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();

    /**
     * 交易费用明细表
     */
    private List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();

    /**
     * 日志
     */
    private List<ValInterflog> listValInterflog = new ArrayList<ValInterflog>();

    /**
     * 结算指令
     */
    private List<StlOrders> listValOrders = new ArrayList<StlOrders>();
    private List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();

    /**
     * 交易入账
     */
    private List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto = new ArrayList<InterfProductDealBookCommonDto>();


    /**
     * 读取数据数量
     */
    private int dataNum;

    /**
     * 获取 listValFinProductDeal
     *
     * @return listValFinProductDeal.
     */
    public List<TrdProductDeal> getListValFinProductDeal() {
        return listValFinProductDeal;
    }

    /**
     * 设置 listValFinProductDeal
     *
     * @param listValFinProductDeal listValFinProductDeal
     */
    public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        this.listValFinProductDeal = listValFinProductDeal;
    }

    /**
     * 获取 listValFinProductDealExt
     *
     * @return listValFinProductDealExt.
     */
    public List<TrdProductDealAdd> getListValFinProductDealExt() {
        return listValFinProductDealExt;
    }

    /**
     * 设置 listValFinProductDealExt
     *
     * @param listValFinProductDealExt listValFinProductDealExt
     */
    public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        this.listValFinProductDealExt = listValFinProductDealExt;
    }

    /**
     * 获取 listValDealAccounting
     *
     * @return listValDealAccounting.
     */
    public List<BokDealAccounting> getListValDealAccounting() {
        return listValDealAccounting;
    }

    /**
     * 设置 listValDealAccounting
     *
     * @param listValDealAccounting listValDealAccounting
     */
    public void setListValDealAccounting(List<BokDealAccounting> listValDealAccounting) {
        this.listValDealAccounting = listValDealAccounting;
    }

    /**
     * 获取 listValDealAccountingDetail
     *
     * @return listValDealAccountingDetail.
     */
    public List<BokDealAccountingDetail> getListValDealAccountingDetail() {
        return listValDealAccountingDetail;
    }

    /**
     * 设置 listValDealAccountingDetail
     *
     * @param listValDealAccountingDetail listValDealAccountingDetail
     */
    public void setListValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        this.listValDealAccountingDetail = listValDealAccountingDetail;
    }

    /**
     * 获取 listValTradeFeeDetail
     *
     * @return listValTradeFeeDetail.
     */
    public List<TrdTradeFeeDetail> getListValTradeFeeDetail() {
        return listValTradeFeeDetail;
    }

    /**
     * 设置 listValTradeFeeDetail
     *
     * @param listValTradeFeeDetail listValTradeFeeDetail
     */
    public void setListValTradeFeeDetail(List<TrdTradeFeeDetail> listValTradeFeeDetail) {
        this.listValTradeFeeDetail = listValTradeFeeDetail;
    }

    /**
     * 获取 listValInterflog
     *
     * @return listValInterflog.
     */
    public List<ValInterflog> getListValInterflog() {
        return listValInterflog;
    }

    /**
     * 设置 listValInterflog
     *
     * @param listValInterflog listValInterflog
     */
    public void setListValInterflog(List<ValInterflog> listValInterflog) {
        this.listValInterflog = listValInterflog;
    }


    /**
     * 获取 listInterfProductDealBookCommonDto
     * @return listInterfProductDealBookCommonDto.
     */
    public List<InterfProductDealBookCommonDto> getListInterfProductDealBookCommonDto() {
        return listInterfProductDealBookCommonDto;
    }


    /**
     * 设置 listInterfProductDealBookCommonDto
     * @param listInterfProductDealBookCommonDto listInterfProductDealBookCommonDto
     */
    public void setListInterfProductDealBookCommonDto(List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto) {
        this.listInterfProductDealBookCommonDto = listInterfProductDealBookCommonDto;
    }

    public List<BokDetailId> getListValBokDetailId() {
        return listValBokDetailId;
    }

    public void setListValBokDetailId(List<BokDetailId> listValBokDetailId) {
        this.listValBokDetailId = listValBokDetailId;
    }

    public List<StlOrders> getListValOrders() {
        return listValOrders;
    }

    public void setListValOrders(List<StlOrders> listValOrders) {
        this.listValOrders = listValOrders;
    }

    public List<StlOrdersExt> getListValOrdersExt() {
        return listValOrdersExt;
    }

    public void setListValOrdersExt(List<StlOrdersExt> listValOrdersExt) {
        this.listValOrdersExt = listValOrdersExt;
    }


    /**
     * 用于存放去重数据筛选条件
     */
    private Map<String,Object> paramMap;

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 获取 dataNum
     * @return dataNum.
     */
    public int getDataNum() {
        return dataNum;
    }

    /**
     * 设置 dataNum
     * @param dataNum dataNum
     */
    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

    public TrdJihuaBonusValuationTradeDto getTrdJihuaBonusValuationTradeDto() {
        return trdJihuaBonusValuationTradeDto;
    }

    public void setTrdJihuaBonusValuationTradeDto(TrdJihuaBonusValuationTradeDto trdJihuaBonusValuationTradeDto) {
        this.trdJihuaBonusValuationTradeDto = trdJihuaBonusValuationTradeDto;
    }
}
