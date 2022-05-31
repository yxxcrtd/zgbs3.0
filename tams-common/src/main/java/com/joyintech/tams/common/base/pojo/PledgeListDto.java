package com.joyintech.tams.common.base.pojo;

import java.math.BigDecimal;

/**
 * 〈功能详细描述〉
 * 手工交收委托成交消息主推接口 质押品列表
 * @author yangxunpeng
 * @version 1.0 2022年05月30日
 * @since 1.0
 */
public class PledgeListDto {
    /**
     * 置换方向
     */
    private String displaceDir;

    /**
     *  质押数量
     */
    private BigDecimal pledgeNumber;

    /**
     * 质押品代码
     */
    private String finprodMarketId;

    public String getDisplaceDir() {
        return displaceDir;
    }

    public void setDisplaceDir(String displaceDir) {
        this.displaceDir = displaceDir;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}
