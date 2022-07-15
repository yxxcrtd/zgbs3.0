package com.joyintech.tams.invest.common.dto.settle.fundback;

import java.math.BigDecimal;

import com.joyintech.framework.base.BaseEntity;

/**
 * @program: tams-invest-parent
 * @description: 资金后台用dto
 * @author: 强成西 @create018-11-15 16:44
 **/
public class FundBackgroundManagementDto extends BaseEntity {

    /**
     * 工作流实例代码
     */
    private String wflWorkflowId;
    /**
     * 有效状态
     */
    private String statusValid;

    /**
     * 是否是待办事项
     */
    private boolean isTodo;

    /**
     * 业务编号
     */
    private String bizCode;

    /**
     * 业务类型
     */
    private String paBussType;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 划款类型
     */
    private String payType;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 理财产品代码
     */
    private String finprodId;

    /**
     * 产品/资产代码
     */
    private String finProdMarketId;

    /**
     * 产品/资产名称
     */
    private String finProdMarketName;

    /**
     * 现金流日期
     */
    private String cashDate;

    /**
     * 划款金额
     */
    private BigDecimal cashAmt;

    /**
     * 经办人
     */
    private String manager;

    /**
     * 接收时间
     */
    private String receiveDateTime;

    /**
     * 工作流代码
     */
    private String workflowId;

    /**
     * 收益类型，预期收益型/净值型/货币型
     */
    private String profitType;
    
    /**
     * 交易方向
     */
    private String ps;
    
    /**
     * 付款账户
     */
    private String payNo;
    
    /**
     * 付款账户名称
     */
    private String payName;
    
    /**
     * 收款账户
     */
    private String receiveNo;
    
    /**
     * 收款账户名称
     */
    private String receiveName;
    
    /**
     * 划款用途
     */
    private String purPose;
    
    /**
     * 币种
     */
    private String ccy;
    /**
    * 发送状态
    */
   private String sendStatus;
    /**
     * 获取 statusValid
     *
     * @return statusValid
     */
    public String getStatusValid() {
        return statusValid;
    }

    /**
     * 设置 statusValid
     *
     * @param statusValid statusValid
     */
    public void setStatusValid(String statusValid) {
        this.statusValid = statusValid;
    }

    /**
     * 获取 bizCode
     *
     * @return bizCode
     */
    public String getBizCode() {
        return bizCode;
    }

    /**
     * 设置 bizCode
     *
     * @param bizCode bizCode
     */
    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    /**
     * 获取 paBussType
     *
     * @return paBussType
     */
    public String getPaBussType() {
        return paBussType;
    }

    /**
     * 设置 paBussType
     *
     * @param paBussType paBussType
     */
    public void setPaBussType(String paBussType) {
        this.paBussType = paBussType;
    }

    /**
     * 获取 operateType
     *
     * @return operateType
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置 operateType
     *
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 payType
     *
     * @return payType
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 设置 payType
     *
     * @param payType payType
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * 获取 portfolioId
     *
     * @return portfolioId
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     *
     * @param portfolioId portfolioId
     */
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
     * 获取 finProdMarketId
     *
     * @return finProdMarketId
     */
    public String getFinProdMarketId() {
        return finProdMarketId;
    }

    /**
     * 设置 finProdMarketId
     *
     * @param finProdMarketId finProdMarketId
     */
    public void setFinProdMarketId(String finProdMarketId) {
        this.finProdMarketId = finProdMarketId;
    }

    /**
     * 获取 finProdMarketName
     *
     * @return finProdMarketName
     */
    public String getFinProdMarketName() {
        return finProdMarketName;
    }

    /**
     * 设置 finProdMarketName
     *
     * @param finProdMarketName finProdMarketName
     */
    public void setFinProdMarketName(String finProdMarketName) {
        this.finProdMarketName = finProdMarketName;
    }

    /**
     * 获取 cashDate
     *
     * @return cashDate
     */
    public String getCashDate() {
        return cashDate;
    }

    /**
     * 设置 cashDate
     *
     * @param cashDate cashDate
     */
    public void setCashDate(String cashDate) {
        this.cashDate = cashDate;
    }

    /**
     * 获取 cashAmt
     *
     * @return cashAmt
     */
    public BigDecimal getCashAmt() {
        return cashAmt;
    }

    /**
     * 设置 cashAmt
     *
     * @param cashAmt cashAmt
     */
    public void setCashAmt(BigDecimal cashAmt) {
        this.cashAmt = cashAmt;
    }

    /**
     * 获取 manager
     *
     * @return manager
     */
    public String getManager() {
        return manager;
    }

    /**
     * 设置 manager
     *
     * @param manager manager
     */
    public void setManager(String manager) {
        this.manager = manager;
    }

    /**
     * 获取 receiveDateTime
     *
     * @return receiveDateTime
     */
    public String getReceiveDateTime() {
        return receiveDateTime;
    }

    /**
     * 设置 receiveDateTime
     *
     * @param receiveDateTime receiveDateTime
     */
    public void setReceiveDateTime(String receiveDateTime) {
        this.receiveDateTime = receiveDateTime;
    }

    /**
     * 获取 workflowId
     *
     * @return workflowId
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 设置 workflowId
     *
     * @param workflowId workflowId
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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
     * 获取 wflWorkflowId
     *
     * @return wflWorkflowId
     */
    public String getWflWorkflowId() {
        return wflWorkflowId;
    }

    /**
     * 设置 wflWorkflowId
     *
     * @param wflWorkflowId wflWorkflowId
     */
    public void setWflWorkflowId(String wflWorkflowId) {
        this.wflWorkflowId = wflWorkflowId;
    }

    /**
     * 获取 isTodo
     *
     * @return isTodo
     */
    public boolean isTodo() {
        return isTodo;
    }

    /**
     * 设置 isTodo
     *
     * @param isTodo isTodo
     */
    public void setTodo(boolean todo) {
        isTodo = todo;
    }
    
    /**
     * 获取 profitType
     * @return profitType.
     */
    public String getProfitType() {
        return profitType;
    }

    /**
     * 设置 profitType
     * @param profitType profitType
     */
    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }
    
    /**
     * 获取 ps
     * @return ps.
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 ps
     * @param ps ps
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public String getReceiveNo() {
		return receiveNo;
	}

	public void setReceiveNo(String receiveNo) {
		this.receiveNo = receiveNo;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getPurPose() {
		return purPose;
	}

	public void setPurPose(String purPose) {
		this.purPose = purPose;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public String getSendStatus() {
	return sendStatus;
	}
	
	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}
    
}
