package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.Date;


@ApiModel(value="信用保护凭证行情信息",description="信用保护凭证行情信息")
public class MskCreditInfoDto extends BaseEntity implements ProcessEntity {

    private String secid;//凭证代码
    private String cdate;//估值日
    private BigDecimal mdprice;//估值全价
    private BigDecimal mcprice;//估值净价
    private BigDecimal mint;//应计利息
    private BigDecimal myield;//估价收益率(%)
    private BigDecimal mMduration;//估价修正久期
    private BigDecimal mCnvxty;//市场凸性
    private BigDecimal issueMarketNum;//发行市场数
    private String creditType;//信用类型
    private String valueSource;//估值来源
    private String createUser;//创建人
    private String createDept;//创建部门
    private Date createTime;//创建时间
    private String updateUser;//更新人
    private Date updateTime;//更新时间

    public String getSecid() {
        return secid;
    }

    public void setSecid(String secid) {
        this.secid = secid;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public BigDecimal getMdprice() {
        return mdprice;
    }

    public void setMdprice(BigDecimal mdprice) {
        this.mdprice = mdprice;
    }

    public BigDecimal getMcprice() {
        return mcprice;
    }

    public void setMcprice(BigDecimal mcprice) {
        this.mcprice = mcprice;
    }

    public BigDecimal getMint() {
        return mint;
    }

    public void setMint(BigDecimal mint) {
        this.mint = mint;
    }

    public BigDecimal getMyield() {
        return myield;
    }

    public void setMyield(BigDecimal myield) {
        this.myield = myield;
    }

    public BigDecimal getmMduration() {
        return mMduration;
    }

    public void setmMduration(BigDecimal mMduration) {
        this.mMduration = mMduration;
    }

    public BigDecimal getmCnvxty() {
        return mCnvxty;
    }

    public void setmCnvxty(BigDecimal mCnvxty) {
        this.mCnvxty = mCnvxty;
    }

    public BigDecimal getIssueMarketNum() {
        return issueMarketNum;
    }

    public void setIssueMarketNum(BigDecimal issueMarketNum) {
        this.issueMarketNum = issueMarketNum;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    @Override
    public String getCreateUser() {
        return createUser;
    }

    @Override
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Override
    public String getCreateDept() {
        return createDept;
    }

    @Override
    public void setCreateDept(String createDept) {
        this.createDept = createDept;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getUpdateUser() {
        return updateUser;
    }

    @Override
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
