package com.joyintech.tams.common.base.entity;

import java.util.Date;

/**
 * 描述:RSK_NODE_CONFIG_DTL表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2022-05-31
 */
public class RiskNodeConfigDtl {

    /**
     * 配置节点id
     */
    private String nodeId;

    /**
     * 指标前置节点
     */
    private String indicatorPreNode;

    /**
     * 指标控制：00：不控 01：软控 02：硬控
     */
    private String indicatorControl;

    /**
     * 指标触发审批：00：不触发 01：触发
     */
    private String indicatorApprove;

    /**
     * 指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     */
    private String indicatorOccupy;

    /**
     * null
     */
    private String creditPreNode;

    /**
     * 授信控制：00：不控 01：软控 02：硬控
     */
    private String creditControl;

    /**
     * 授信占用：指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     */
    private String creditOccupy;

    /**
     * 授权控制：00：不控 01：软控 02：硬控
     */
    private String authorizeControl;

    /**
     * 授权触发审批：00：不触发 01：触发
     */
    private String authorizeApprove;

    /**
     * 名单控制：00：不控 01：软控 02：硬控
     */
    private String nameListControl;

    /**
     * 控制名单类型：01：资产 02：机构 03：资产和机构
     */
    private String nameListType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 配置节点id
     * @return NODE_ID 配置节点id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * 配置节点id
     * @param nodeId 配置节点id
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    /**
     * 指标前置节点
     * @return INDICATOR_PRE_NODE 指标前置节点
     */
    public String getIndicatorPreNode() {
        return indicatorPreNode;
    }

    /**
     * 指标前置节点
     * @param indicatorPreNode 指标前置节点
     */
    public void setIndicatorPreNode(String indicatorPreNode) {
        this.indicatorPreNode = indicatorPreNode == null ? null : indicatorPreNode.trim();
    }

    /**
     * 指标控制：00：不控 01：软控 02：硬控
     * @return INDICATOR_CONTROL 指标控制：00：不控 01：软控 02：硬控
     */
    public String getIndicatorControl() {
        return indicatorControl;
    }

    /**
     * 指标控制：00：不控 01：软控 02：硬控
     * @param indicatorControl 指标控制：00：不控 01：软控 02：硬控
     */
    public void setIndicatorControl(String indicatorControl) {
        this.indicatorControl = indicatorControl == null ? null : indicatorControl.trim();
    }

    /**
     * 指标触发审批：00：不触发 01：触发
     * @return INDICATOR_APPROVE 指标触发审批：00：不触发 01：触发
     */
    public String getIndicatorApprove() {
        return indicatorApprove;
    }

    /**
     * 指标触发审批：00：不触发 01：触发
     * @param indicatorApprove 指标触发审批：00：不触发 01：触发
     */
    public void setIndicatorApprove(String indicatorApprove) {
        this.indicatorApprove = indicatorApprove == null ? null : indicatorApprove.trim();
    }

    /**
     * 指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     * @return INDICATOR_OCCUPY 指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     */
    public String getIndicatorOccupy() {
        return indicatorOccupy;
    }

    /**
     * 指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     * @param indicatorOccupy 指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     */
    public void setIndicatorOccupy(String indicatorOccupy) {
        this.indicatorOccupy = indicatorOccupy == null ? null : indicatorOccupy.trim();
    }

    /**
     * null
     * @return CREDIT_PRE_NODE null
     */
    public String getCreditPreNode() {
        return creditPreNode;
    }

    /**
     * null
     * @param creditPreNode null
     */
    public void setCreditPreNode(String creditPreNode) {
        this.creditPreNode = creditPreNode == null ? null : creditPreNode.trim();
    }

    /**
     * 授信控制：00：不控 01：软控 02：硬控
     * @return CREDIT_CONTROL 授信控制：00：不控 01：软控 02：硬控
     */
    public String getCreditControl() {
        return creditControl;
    }

    /**
     * 授信控制：00：不控 01：软控 02：硬控
     * @param creditControl 授信控制：00：不控 01：软控 02：硬控
     */
    public void setCreditControl(String creditControl) {
        this.creditControl = creditControl == null ? null : creditControl.trim();
    }

    /**
     * 授信占用：指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     * @return CREDIT_OCCUPY 授信占用：指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     */
    public String getCreditOccupy() {
        return creditOccupy;
    }

    /**
     * 授信占用：指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     * @param creditOccupy 授信占用：指标占用类型：00：不占,01：预占,02：实占,03：预占释放,04：实占释放,05：预占释放再预占,06：实占释放再实占,07：实占预占释放
     */
    public void setCreditOccupy(String creditOccupy) {
        this.creditOccupy = creditOccupy == null ? null : creditOccupy.trim();
    }

    /**
     * 授权控制：00：不控 01：软控 02：硬控
     * @return AUTHORIZE_CONTROL 授权控制：00：不控 01：软控 02：硬控
     */
    public String getAuthorizeControl() {
        return authorizeControl;
    }

    /**
     * 授权控制：00：不控 01：软控 02：硬控
     * @param authorizeControl 授权控制：00：不控 01：软控 02：硬控
     */
    public void setAuthorizeControl(String authorizeControl) {
        this.authorizeControl = authorizeControl == null ? null : authorizeControl.trim();
    }

    /**
     * 授权触发审批：00：不触发 01：触发
     * @return AUTHORIZE_APPROVE 授权触发审批：00：不触发 01：触发
     */
    public String getAuthorizeApprove() {
        return authorizeApprove;
    }

    /**
     * 授权触发审批：00：不触发 01：触发
     * @param authorizeApprove 授权触发审批：00：不触发 01：触发
     */
    public void setAuthorizeApprove(String authorizeApprove) {
        this.authorizeApprove = authorizeApprove == null ? null : authorizeApprove.trim();
    }

    /**
     * 名单控制：00：不控 01：软控 02：硬控
     * @return NAME_LIST_CONTROL 名单控制：00：不控 01：软控 02：硬控
     */
    public String getNameListControl() {
        return nameListControl;
    }

    /**
     * 名单控制：00：不控 01：软控 02：硬控
     * @param nameListControl 名单控制：00：不控 01：软控 02：硬控
     */
    public void setNameListControl(String nameListControl) {
        this.nameListControl = nameListControl == null ? null : nameListControl.trim();
    }

    /**
     * 控制名单类型：01：资产 02：机构 03：资产和机构
     * @return NAME_LIST_TYPE 控制名单类型：01：资产 02：机构 03：资产和机构
     */
    public String getNameListType() {
        return nameListType;
    }

    /**
     * 控制名单类型：01：资产 02：机构 03：资产和机构
     * @param nameListType 控制名单类型：01：资产 02：机构 03：资产和机构
     */
    public void setNameListType(String nameListType) {
        this.nameListType = nameListType == null ? null : nameListType.trim();
    }

    /**
     * 备注
     * @return REMARK 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 创建时间
     * @return CREATE_TIME 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }
}