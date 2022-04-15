package com.joyintech.tams.settle.batch.datagather.constant;

/**
 * @author 何阳阳
 * @date 2020/7/2
 * @see StlDataGatherConstant
 * @since 1.0
 */
public interface StlDataGatherConstant {

    /**
     * 上中登结算明细数据
     */
    String STL_DATA_GATHER_REMARK = "数据清算自动处理";

    /**
     * 新头寸时间类型T+0
     */
    Integer DATE_TYPE_T_0 = 0;

    /**
     * 可转债申购确认
     */
    String BOND_PURCHASE_CONFIRM = "BondPurchaseConfirm";

    /**
     * 债券存续期
     */
    String BOND_DURATION = "BondDuration";

    /**
     * 回购存续期
     */
    String REPO_DURATION = "RepoDuration";

    /**
     * 债券股票上市流通
     */
    String BOND_SHARES_MARKET_TRADE = "BondSharesMarketTrade";

    /**
     * 协议回购逆回购到期
     */
    String AGRE_ANTI_REPO_EXPIRED = "AgreAntiRepoExpired";
}
