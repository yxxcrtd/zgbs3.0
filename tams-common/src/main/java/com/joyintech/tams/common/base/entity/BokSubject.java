package com.joyintech.tams.common.base.entity;

public class BokSubject extends BokSubjectKey {
    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 上级科目号
     */
    private String fsubjectId;

    /**
     * 余额方向
     */
    private String balFlag;

    /**
     * 科目类型
     */
    private String subjectType;

    /**
     * 科目级别
     */
    private Long subjectLevel;

    /**
     * 是否核算数量
     */
    private String accQuaFlag;

    /**
     * 是否计息
     */
    private String accIntFlag;

    /**
     * 是否允许透支
     */
    private String overdrawnFlag;

    /**
     * 是否生成四级科目
     */
    private String genDetsubFlag;

    /**
     * 基础科目性质
     */
    private String baseSubjectNature;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 证券管理户代码
     */
    private String secManageAcctId;

    /**
     * 内部科目编码
     */
    private String subjectInternalCode;
    /**
     * "辅助核算表达式（存储FEL表达式，如交易场所为银行间，发行方式为封闭式则生成表达式TRADE_MARKET='01'&&ISSUE_TYPE='01'
     * 如果是记账科目，必填。"
     */
    private String subjectHandFelCode;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 是否核算项
     */
    private String isAcc;

    public String getIsAcc() {
        return isAcc;
    }

    public void setIsAcc(String isAcc) {
        this.isAcc = isAcc;
    }

    public String getSubjectInternalCode() {
        return subjectInternalCode;
    }

    public void setSubjectInternalCode(String subjectInternalCode) {
        this.subjectInternalCode = subjectInternalCode;
    }

    public String getSubjectHandFelCode() {
        return subjectHandFelCode;
    }

    public void setSubjectHandFelCode(String subjectHandFelCode) {
        this.subjectHandFelCode = subjectHandFelCode;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 取得 科目名称
     * @return 科目名称
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * 设置 科目名称
     * @param subjectName 科目名称
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName == null ? null : subjectName.trim();
    }

    /**
     * 取得 上级科目号
     * @return 上级科目号
     */
    public String getFsubjectId() {
        return fsubjectId;
    }

    /**
     * 设置 上级科目号
     * @param fsubjectId 上级科目号
     */
    public void setFsubjectId(String fsubjectId) {
        this.fsubjectId = fsubjectId == null ? null : fsubjectId.trim();
    }

    /**
     * 取得 余额方向
     * @return 余额方向
     */
    public String getBalFlag() {
        return balFlag;
    }

    /**
     * 设置 余额方向
     * @param balFlag 余额方向
     */
    public void setBalFlag(String balFlag) {
        this.balFlag = balFlag == null ? null : balFlag.trim();
    }

    /**
     * 取得 科目类型
     * @return 科目类型
     */
    public String getSubjectType() {
        return subjectType;
    }

    /**
     * 设置 科目类型
     * @param subjectType 科目类型
     */
    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType == null ? null : subjectType.trim();
    }

    /**
     * 取得 科目级别
     * @return 科目级别
     */
    public Long getSubjectLevel() {
        return subjectLevel;
    }

    /**
     * 设置 科目级别
     * @param subjectLevel 科目级别
     */
    public void setSubjectLevel(Long subjectLevel) {
        this.subjectLevel = subjectLevel;
    }

    /**
     * 取得 是否核算数量
     * @return 是否核算数量
     */
    public String getAccQuaFlag() {
        return accQuaFlag;
    }

    /**
     * 设置 是否核算数量
     * @param accQuaFlag 是否核算数量
     */
    public void setAccQuaFlag(String accQuaFlag) {
        this.accQuaFlag = accQuaFlag == null ? null : accQuaFlag.trim();
    }

    /**
     * 取得 是否计息
     * @return 是否计息
     */
    public String getAccIntFlag() {
        return accIntFlag;
    }

    /**
     * 设置 是否计息
     * @param accIntFlag 是否计息
     */
    public void setAccIntFlag(String accIntFlag) {
        this.accIntFlag = accIntFlag == null ? null : accIntFlag.trim();
    }

    /**
     * 取得 是否允许透支
     * @return 是否允许透支
     */
    public String getOverdrawnFlag() {
        return overdrawnFlag;
    }

    /**
     * 设置 是否允许透支
     * @param overdrawnFlag 是否允许透支
     */
    public void setOverdrawnFlag(String overdrawnFlag) {
        this.overdrawnFlag = overdrawnFlag == null ? null : overdrawnFlag.trim();
    }

    /**
     * 取得 是否生成四级科目
     * @return 是否生成四级科目
     */
    public String getGenDetsubFlag() {
        return genDetsubFlag;
    }

    /**
     * 设置 是否生成四级科目
     * @param genDetsubFlag 是否生成四级科目
     */
    public void setGenDetsubFlag(String genDetsubFlag) {
        this.genDetsubFlag = genDetsubFlag == null ? null : genDetsubFlag.trim();
    }

    /**
     * 取得 基础科目性质
     * @return 基础科目性质
     */
    public String getBaseSubjectNature() {
        return baseSubjectNature;
    }

    /**
     * 设置 基础科目性质
     * @param baseSubjectNature 基础科目性质
     */
    public void setBaseSubjectNature(String baseSubjectNature) {
        this.baseSubjectNature = baseSubjectNature == null ? null : baseSubjectNature.trim();
    }

    /**
     * 取得 投资目的
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim == null ? null : invAim.trim();
    }

    /**
     * 取得 证券管理户代码
     * @return 证券管理户代码
     */
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    /**
     * 设置 证券管理户代码
     * @param secManageAcctId 证券管理户代码
     */
    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId == null ? null : secManageAcctId.trim();
    }
}