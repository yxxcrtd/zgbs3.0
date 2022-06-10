package com.joyintech.tams.exchange.common.bean.investment;

import java.io.Serializable;
import java.math.BigDecimal;

public class PledgeTradeMessage implements Cloneable, Serializable {

    private static final long serialVersionUID = 8859641052093235574L;
    /**
     * 质押品代码
     */
    private String finprodMarketId;

    /**
     * 质押数量
     */
    private BigDecimal pledgeNumber;

    /**
     * 置换方向
     */
    private String displaceDir;

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public String getDisplaceDir() {
        return displaceDir;
    }

    public void setDisplaceDir(String displaceDir) {
        this.displaceDir = displaceDir;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
