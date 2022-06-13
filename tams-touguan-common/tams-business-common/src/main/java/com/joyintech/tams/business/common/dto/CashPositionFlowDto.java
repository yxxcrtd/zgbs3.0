package com.joyintech.tams.business.common.dto;

import com.joyintech.tams.common.base.entity.PtlCashPositionFlow;

import java.util.List;

/**
 * @program: microservice
 * @description: 组合现金流水
 **/
public class CashPositionFlowDto extends PtlCashPositionFlow {
    /**
     * 通道金融产品代码
     */
    private String finprodId;

    /**
     * 账户类型
     */
    private List<String> acctType;

    /**
     * 是否添加账户类型条件
     */
    private boolean needAcctType;

    private String splitType;

    public String getSplitType() {
		return splitType;
	}

	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}

    /**
     * 获取 finprodId
     *
     * @return finprodId
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
     * 获取 acctType
     *
     * @return acctType
     */
    public List<String> getAcctType() {
        return acctType;
    }

    /**
     * 设置 acctType
     *
     * @param acctType acctType
     */
    public void setAcctType(List<String> acctType) {
        this.acctType = acctType;
    }

    /**
     * 获取 needAcctType
     *
     * @return needAcctType
     */
    public boolean isNeedAcctType() {
        return needAcctType;
    }

    /**
     * 设置 needAcctType
     *
     * @param needAcctType needAcctType
     */
    public void setNeedAcctType(boolean needAcctType) {
        this.needAcctType = needAcctType;
    }

    /**
     * add by sunhui 20210721 重庆银行：中标结果维护占用持仓改造，增加是否查询冻结持仓flag:Y查询
     */
    private String isFrozenPositionFlag;

    public String getIsFrozenPositionFlag() {
        return isFrozenPositionFlag;
    }

    public void setIsFrozenPositionFlag(String isFrozenPositionFlag) {
        this.isFrozenPositionFlag = isFrozenPositionFlag;
    }

}
