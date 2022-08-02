package com.joyintech.tams.invest.common.dto.settle.balance;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.LqdPipelineMatch;

import java.util.Date;

/**
 * @program: tams-invest-parent
 * @description:
 * @author: 强成西
 * @create: 2018-12-03 16:58
 **/
public class BaseCashFlowBalanceManagementDto extends LqdPipelineMatch {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 3860751209252642735L;
    /**
     * 交易方向
     */
    private String ps;

    /**
     * 收益类型
     */
    private String profitType;
    
    /**
     * 产品代码（带F16）
     */
    private String finprodId;

    /**
     * 获取 ps
     *
     * @return ps
     */
    public String getPs() {
        return ps;
    }

    /**
     * 交易到期日
     */
    private Date mdate;

    /**
     * 设置 ps
     *
     * @param ps ps
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getProfitType() {
        return profitType;
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    
    /**
     * 获取 finprodId
     * @return finprodId.
     */
    public String getFinprodId() {
        return finprodId;
    }

    
    /**
     * 设置 finprodId
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getMdateStr() {
        return DateUtil.format(mdate);
    }
}
