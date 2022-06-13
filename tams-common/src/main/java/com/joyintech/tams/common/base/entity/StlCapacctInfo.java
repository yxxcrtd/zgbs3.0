package com.joyintech.tams.common.base.entity;

public class StlCapacctInfo extends StlCapacctInfoKey {
    /**
     * 账号
     */
    private String busiAcctNo;

    /**
     * 户名
     */
    private String acctName;

    /**
     * 开户行
     */
    private String bankDep;

    /**
     * 行内行外
     */
    private String inOutBank;

    /**
     * 联行行号
     */
    private String corrNo;

    /**
     * 大额支付系统号/网点号
     */
    private String largePayno;

    /**
     * 银行国际代码
     */
    private String swiftCode;

    /**
     * 境内境外
     */
    private String blnArea;

    /**
     * 是否允许透支
     */
    private String isOverdraw;

    private String ccy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 流水号
     */
    private String seqNo;

	/**
     * 账户性质
     */
    private String accountNature;

    /**
     * 是否计提活期利息
     */
    private String isCalint;

    /**
     * 所属营业部
     */
    private String ownDept;

    /**
     * 账户密码
     */
    private String accPass;

    /**
     * 交易密码
     */
    private String tradePass;

    private String acctType;

    private String stockExchangeNo;

    private String status;

    private String acctPwd;

    private String tradePwd;

    /**
     * 取得 账号
     * @return 账号
     */
    public String getBusiAcctNo() {
        return busiAcctNo;
    }

    /**
     * 设置 账号
     * @param busiAcctNo 账号
     */
    public void setBusiAcctNo(String busiAcctNo) {
        this.busiAcctNo = busiAcctNo == null ? null : busiAcctNo.trim();
    }

    /**
     * 取得 户名
     * @return 户名
     */
    public String getAcctName() {
        return acctName;
    }

    /**
     * 设置 户名
     * @param acctName 户名
     */
    public void setAcctName(String acctName) {
        this.acctName = acctName == null ? null : acctName.trim();
    }

    /**
     * 取得 开户行
     * @return 开户行
     */
    public String getBankDep() {
        return bankDep;
    }

    /**
     * 设置 开户行
     * @param bankDep 开户行
     */
    public void setBankDep(String bankDep) {
        this.bankDep = bankDep == null ? null : bankDep.trim();
    }

    /**
     * 取得 行内行外
     * @return 行内行外
     */
    public String getInOutBank() {
        return inOutBank;
    }

    /**
     * 设置 行内行外
     * @param inOutBank 行内行外
     */
    public void setInOutBank(String inOutBank) {
        this.inOutBank = inOutBank == null ? null : inOutBank.trim();
    }

    /**
     * 取得 联行行号
     * @return 联行行号
     */
    public String getCorrNo() {
        return corrNo;
    }

    /**
     * 设置 联行行号
     * @param corrNo 联行行号
     */
    public void setCorrNo(String corrNo) {
        this.corrNo = corrNo == null ? null : corrNo.trim();
    }

    /**
     * 取得 大额支付系统号/网点号
     * @return 大额支付系统号/网点号
     */
    public String getLargePayno() {
        return largePayno;
    }

    /**
     * 设置 大额支付系统号/网点号
     * @param largePayno 大额支付系统号/网点号
     */
    public void setLargePayno(String largePayno) {
        this.largePayno = largePayno == null ? null : largePayno.trim();
    }

    /**
     * 取得 银行国际代码
     * @return 银行国际代码
     */
    public String getSwiftCode() {
        return swiftCode;
    }

    /**
     * 设置 银行国际代码
     * @param swiftCode 银行国际代码
     */
    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode == null ? null : swiftCode.trim();
    }

    /**
     * 取得 境内境外
     * @return 境内境外
     */
    public String getBlnArea() {
        return blnArea;
    }

    /**
     * 设置 境内境外
     * @param blnArea 境内境外
     */
    public void setBlnArea(String blnArea) {
        this.blnArea = blnArea == null ? null : blnArea.trim();
    }

    /**
     * 取得 是否允许透支
     * @return 是否允许透支
     */
    public String getIsOverdraw() {
        return isOverdraw;
    }

    /**
     * 设置 是否允许透支
     * @param isOverdraw 是否允许透支
     */
    public void setIsOverdraw(String isOverdraw) {
        this.isOverdraw = isOverdraw == null ? null : isOverdraw.trim();
    }

    @Override
    public String getCcy() {
        return ccy;
    }

    @Override
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 流水号
     * @return 流水号
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 流水号
     * @param seqNo 流水号
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo == null ? null : seqNo.trim();
    }

    /**
     * 取得 是否计提活期利息
     * @return 是否计提活期利息
     */
    public String getIsCalint() {
        return isCalint;
    }

    /**
     * 设置 是否计提活期利息
     * @param isCalint 是否计提活期利息
     */
    public void setIsCalint(String isCalint) {
        this.isCalint = isCalint == null ? null : isCalint.trim();
    }

    /**
     * 取得 所属营业部
     * @return 所属营业部
     */
    public String getOwnDept() {
        return ownDept;
    }

    /**
     * 设置 所属营业部
     * @param ownDept 所属营业部
     */
    public void setOwnDept(String ownDept) {
        this.ownDept = ownDept == null ? null : ownDept.trim();
    }

    /**
     * 取得 账户密码
     * @return 账户密码
     */
    public String getAccPass() {
        return accPass;
    }

    /**
     * 设置 账户密码
     * @param accPass 账户密码
     */
    public void setAccPass(String accPass) {
        this.accPass = accPass == null ? null : accPass.trim();
    }

    /**
     * 取得 交易密码
     * @return 交易密码
     */
    public String getTradePass() {
        return tradePass;
    }

    /**
     * 设置 交易密码
     * @param tradePass 交易密码
     */
    public void setTradePass(String tradePass) {
        this.tradePass = tradePass == null ? null : tradePass.trim();
    }

	/**
     * 取得 账户性质
     * @return 账户性质
     */
    public String getAccountNature() {
        return accountNature;
    }
    /**
     * 设置 账户性质
     * @param accountNature 账户性质
     */
    public void setAccountNature(String accountNature) {
        this.accountNature = accountNature;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public String getStockExchangeNo() {
        return stockExchangeNo;
    }

    public void setStockExchangeNo(String stockExchangeNo) {
        this.stockExchangeNo = stockExchangeNo;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcctPwd() {
        return acctPwd;
    }

    public void setAcctPwd(String acctPwd) {
        this.acctPwd = acctPwd;
    }

    public String getTradePwd() {
        return tradePwd;
    }

    public void setTradePwd(String tradePwd) {
        this.tradePwd = tradePwd;
    }
}