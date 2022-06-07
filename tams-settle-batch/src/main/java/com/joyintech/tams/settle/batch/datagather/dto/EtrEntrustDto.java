package com.joyintech.tams.settle.batch.datagather.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.json.CustomDateDeserialize;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCodeListExt3;

import java.util.Arrays;
import java.util.Date;

/**
 * @author zznn
 * @date 2021/10/29
 */
public class EtrEntrustDto extends EtrEntrust {


    /**
     * 证券账户代码
     */
    private String secAcctId;

    /**
     * 交易货币账户
     */
    private String dealCurAcct;

    /**
     * 指令明细表tradeId
     */
    private String tradeId;

    /**
     * 首次交收日
     */
    private Date vpayDate;

    /**
     * 匹配状态
     */
    private String matchClass;

    public String getMatchClass() {
        return matchClass;
    }

    public void setMatchClass(String matchClass) {
        this.matchClass = matchClass;
    }


    /**
     * 取得 首次交收日
     * @return 首次交收日
     */
    public Date getVpayDate() {
        return vpayDate;
    }

    /**
     * 设置 首次交收日
     * @param vpayDate 首次交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVpayDate(Date vpayDate) {
        this.vpayDate = vpayDate;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getDealCurAcct() {
        return dealCurAcct;
    }

    public void setDealCurAcct(String dealCurAcct) {
        this.dealCurAcct = dealCurAcct;
    }

    public String getBusiKey(){
        // modify by fuyongsheng 匹配指令忽略交易平台 start
        // GNS-922:质押券出入库不看交易平台 modify by fuyongsheng 20220219 start
//        if(DictCodeListExt3.交易方向_质押券入库.getValue().equals(getPs()) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(getPs())){
//            return StringUtil.joinString(Arrays.asList(getSecAcctId(),getDealCurAcct(),getFinprodId(), DateUtil.format(getEntrustDate()),getPs()),"_");
//        }
//        else if(DictCodeListExt3.交易方向_债转股.getValue().equals(getPs())){
//            return StringUtil.joinString(Arrays.asList(getSecAcctId(),getDealCurAcct(),getFinprodId(), DateUtil.format(getEntrustDate()),getPs()),"_");
//        }
//        else{
//            return StringUtil.joinString(Arrays.asList(getSecAcctId(),getDealCurAcct(),getFinprodId(), DateUtil.format(getEntrustDate()),getTradePlat(),getPs()),"_");
//        }
        // GNS-922:质押券出入库不看交易平台 modify by fuyongsheng 20220219 end
        return StringUtil.joinString(Arrays.asList(getSecAcctId(),getDealCurAcct(),getFinprodId(), DateUtil.format(getEntrustDate()),getPs()),"_");
        // modify by fuyongsheng 匹配指令忽略交易平台 end
    }

    public String getBusiKeyWithoutDate(){
        // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 start
        // return StringUtil.joinString(Arrays.asList(getSecAcctId(),getDealCurAcct(),getFinprodId(), getTradePlat(),getPs()),"_");
        return StringUtil.joinString(Arrays.asList(getSecAcctId(),getDealCurAcct(),getFinprodId(),getPs()),"_");
        // ZGTFCP-1462:特殊处理场景忽略交易平台 modify by fuyongsheng 20220322 end
    }
}

