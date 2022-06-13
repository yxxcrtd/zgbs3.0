package com.joyintech.tams.invest.trade.manualsettle.dto;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.TrdManualSettleFlow;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author zhangnian
 * @ClassName ManualSettleFlowDto
 * @description TODO
 * @date 2020/10/22 0022 14:05
 **/
public class ManualSettleFlowDto extends TrdManualSettleFlow {
    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 金融产品全称
     */
    private String finprodName;

    /**
     * 金融产品类型
     */
    private String finprodType2;

    private String operator;

    /**
     * 质押品ID
     */
    private String pledgeId;

    /**
     * 质押品ID abbr
     */
    private String pledgeIdAbbr;

    /**
     * 原质押品ID
     */
    private String bPledgeId;

    /**
     * 原质押品ID简称
     */
    private String bPledgeIdAbbr;

    /**
     * 质押品名称 abbr
     */
    private String pledgeName;

    /**
     *回购金额（质押式回购）
     */
    private BigDecimal prin;

    /**
     *到期结算金额（质押式回购）
     */
    private BigDecimal mTradeAmt;

    /**
     * 质押数量（质押式回购）
     */
    private BigDecimal pledgeNumber;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    // ZGCPCN-998: 债券的数量单位改为万元,数量*100/10000,其余不变 modify by fuyongsheng 20220601 start
    public String getPledgeNumberStr(){
        if(this.getPledgeNumber() == null){
            return StringUtil.EMPTY;
        }
        if(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(this.getFinprodType2())){
            return String.valueOf(ArithmeticUtil.divide(this.getPledgeNumber(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }else{
            return String.valueOf(this.getPledgeNumber());
        }
    }

    public String getSettleNumStr() {
        if(this.getSettleNum() == null){
            return StringUtil.EMPTY;
        }
        if(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(this.getFinprodType2())){
            return String.valueOf(ArithmeticUtil.divide(this.getSettleNum(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }else{
            return String.valueOf(this.getSettleNum());
        }
    }
    // ZGCPCN-998: 债券的数量单位改为万元,数量*100/10000,其余不变 modify by fuyongsheng 20220601 end


    // ZGCPCN-998: 回购金额单位改为万元,金额*100/10000 modify by fuyongsheng 20220601 start
    public String getPrinStr() {
        if(null == this.getPrin()){
            return StringUtil.EMPTY;
        }
        return String.valueOf(ArithmeticUtil.divide(this.getPrin(), new BigDecimal(100), 2, RoundingMode.HALF_UP));
    }
    // ZGCPCN-998: 回购金额单位改为万元,数量*100/10000 modify by fuyongsheng 20220601 end

    public String getOperTypeStr() {
        if(StringUtil.isNotEmpty(this.getOperType())){
            return SysDataUtil.getNameFromDataDict(DictCode.交收操作类型, this.getOperType());
        }
        return StringUtil.EMPTY;
    }

    public String getSettleTimeStr(){
        if(null!=super.getSettleTime()){
            return DateUtil.format(super.getSettleTime(),"HH:mm:ss");
        }
        return StringUtil.EMPTY;
    }

    public String getSettleDateStr(){
        if(null!=super.getSettleTime()){
            return DateUtil.format(super.getSettleTime(),"yyyy-MM-dd");
        }
        return StringUtil.EMPTY;
    }

    public String getPledgeId() {
        return pledgeId;
    }

    public void setPledgeId(String pledgeId) {
        this.pledgeId = pledgeId;
    }

    public String getPledgeIdAbbr() {
        return pledgeIdAbbr;
    }

    public void setPledgeIdAbbr(String pledgeIdAbbr) {
        this.pledgeIdAbbr = pledgeIdAbbr;
    }

    public String getPledgeName() {
        return pledgeName;
    }

    public void setPledgeName(String pledgeName) {
        this.pledgeName = pledgeName;
    }

    public BigDecimal getPrin() {
        return prin;
    }

    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public String getbPledgeId() {
        return bPledgeId;
    }

    public void setbPledgeId(String bPledgeId) {
        this.bPledgeId = bPledgeId;
    }

    public String getbPledgeIdAbbr() {
        return bPledgeIdAbbr;
    }

    public void setbPledgeIdAbbr(String bPledgeIdAbbr) {
        this.bPledgeIdAbbr = bPledgeIdAbbr;
    }
}

