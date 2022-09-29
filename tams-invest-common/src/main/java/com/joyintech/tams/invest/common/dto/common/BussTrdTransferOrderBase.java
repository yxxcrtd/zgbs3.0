/*
 * 文件名：BussTrdTransferOrderDto.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年09月27日 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.common.dto.common;

import java.util.List;

import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;

public class BussTrdTransferOrderBase extends StlPayOrdersDto {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -4871847768906897953L;

    /**
     * 付款账户
     */
    private String payAcctNo;

    /**
     * 付款账户名称
     */
    private String payAcctName;

    /**
     * 收款账户
     */
    private String recAcctNo;

    /**
     * 收款账户名称
     */
    private String recAcctName;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 证券托管户或通道代码
     */
    private String secAcctId;

    /**
     * 付款账户值域
     */
    private List<SelectOptionDto> payAcctNoList;

    /**
     * 收款账户值域
     */
    private List<SelectOptionDto> recAcctNoList;

    /**
     * 业务类型
     */
    private String finprodType2;

    /**
     * 划款类型显示值
     */
    private String payTypeText;

    /**
     * 结算日期(String)
     */
    private String settleDateText;

    public String getCcyText() {
        DictCodeList dictCodeList = DictCodeList.from(DictCode.币种, getCcy());
        if (dictCodeList != null) {
            return dictCodeList.getCodeName();
        }
        return getCcy();
    }

    /**
     * 获取 finprodType2
     *
     * @return finprodType2
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 finprodType2
     *
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取 settleDateText
     * 
     * @return settleDateText.
     */
    public String getSettleDateText() {
        settleDateText = DateUtil.format(getSettleDate());
        return settleDateText;
    }

    /**
     * 设置 settleDateText
     * 
     * @param settleDateText settleDateText
     */
    public void setSettleDateText(String settleDateText) {
        this.settleDateText = settleDateText;
    }

    /**
     * 获取 payTypeText
     *
     * @return payTypeText.
     */
    public String getPayTypeText() {
        DictCodeListExt1 dictCodeList = DictCodeListExt1.from(DictCode.平安划款类型, getPayType());
        // 一体化对应etliu end
        if (dictCodeList != null) {
            return dictCodeList.getCodeName();
        }
        return payTypeText;
    }

    /**
     * 设置 payTypeText
     *
     * @param payTypeText payTypeText
     */
    public void setPayTypeText(String payTypeText) {
        this.payTypeText = payTypeText;
    }

    /**
     * 获取 payAcctNoList
     *
     * @return payAcctNoList
     */
    public List<SelectOptionDto> getPayAcctNoList() {
        return payAcctNoList;
    }

    /**
     * 设置 payAcctNoList
     *
     * @param payAcctNoList payAcctNoList
     */
    public void setPayAcctNoList(List<SelectOptionDto> payAcctNoList) {
        this.payAcctNoList = payAcctNoList;
    }

    /**
     * 获取 recAcctNoList
     *
     * @return recAcctNoList
     */
    public List<SelectOptionDto> getRecAcctNoList() {
        return recAcctNoList;
    }

    /**
     * 设置 recAcctNoList
     *
     * @param recAcctNoList recAcctNoList
     */
    public void setRecAcctNoList(List<SelectOptionDto> recAcctNoList) {
        this.recAcctNoList = recAcctNoList;
    }

    /**
     * 获取 payAcctNo
     *
     * @return payAcctNo
     */
    public String getPayAcctNo() {
        return payAcctNo;
    }

    /**
     * 设置 payAcctNo
     *
     * @param payAcctNo payAcctNo
     */
    public void setPayAcctNo(String payAcctNo) {
        this.payAcctNo = payAcctNo;
    }

    /**
     * 获取 payAcctName
     *
     * @return payAcctName
     */
    public String getPayAcctName() {
        if (StringUtil.isBlank(payAcctName)) {
            payAcctName = getPayAcctNo();
        }
        return payAcctName;
    }

    /**
     * 设置 payAcctName
     *
     * @param payAcctName payAcctName
     */
    public void setPayAcctName(String payAcctName) {
        this.payAcctName = payAcctName;
    }

    /**
     * 获取 recAcctNo
     *
     * @return recAcctNo
     */
    public String getRecAcctNo() {
        return recAcctNo;
    }

    /**
     * 设置 recAcctNo
     *
     * @param recAcctNo recAcctNo
     */
    public void setRecAcctNo(String recAcctNo) {
        this.recAcctNo = recAcctNo;
    }

    /**
     * 获取 recAcctName
     *
     * @return recAcctName
     */
    public String getRecAcctName() {
        if (StringUtil.isBlank(recAcctName)) {
            recAcctName = getRecAcctNo();
        }
        return recAcctName;
    }

    /**
     * 设置 recAcctName
     *
     * @param recAcctName recAcctName
     */
    public void setRecAcctName(String recAcctName) {
        this.recAcctName = recAcctName;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    @Override
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
    @Override
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     *
     * @return portfolioName
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     *
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    
    /**
     * 行内行外
     */
    private String inOutBank;
    
    /**
     * 网点
     */
    private String recAcctPoint;
    
    /**
     * 获取 inOutBank
     * @return inOutBank.
     */
    public String getInOutBank() {
        return inOutBank;
    }

    /**
     * 设置 inOutBank
     * @param inOutBank inOutBank
     */
    public void setInOutBank(String inOutBank) {
        this.inOutBank = inOutBank;
    }
    
    /**
     * 获取 recAcctPoint
     * @return recAcctPoint.
     */
    public String getRecAcctPoint() {
        return recAcctPoint;
    }
    
    /**
     * 设置 recAcctPoint
     * @param recAcctPoint recAcctPoint
     */
    public void setRecAcctPoint(String recAcctPoint) {
        this.recAcctPoint = recAcctPoint;
    }

    /**
     * 获取 invAim
     *
     * @return invAim
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 invAim
     *
     * @param invAim invAim
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 获取 secAcctId
     *
     * @return secAcctId
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * 设置 secAcctId
     *
     * @param secAcctId secAcctId
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 画面账户联动的垃圾代码
     */
    private String acctNoGdhFlag;

    private String reAcctNoGdhFlag;

    public String getAcctNoGdhFlag() {
        return acctNoGdhFlag;
    }

    public void setAcctNoGdhFlag(String acctNoGdhFlag) {
        this.acctNoGdhFlag = acctNoGdhFlag;
    }

    public String getReAcctNoGdhFlag() {
        return reAcctNoGdhFlag;
    }

    public void setReAcctNoGdhFlag(String reAcctNoGdhFlag) {
        this.reAcctNoGdhFlag = reAcctNoGdhFlag;
    }

    private String stlPayOrderShow;

    public String getStlPayOrderShow() {
        return stlPayOrderShow;
    }

    public void setStlPayOrderShow(String stlPayOrderShow) {
        this.stlPayOrderShow = stlPayOrderShow;
    }

    /**
     * 转仓转入组合代码
     */
    private String inPortfolioId;

    public String getInPortfolioId() {
        return inPortfolioId;
    }

    public void setInPortfolioId(String inPortfolioId) {
        this.inPortfolioId = inPortfolioId;
    }
}
