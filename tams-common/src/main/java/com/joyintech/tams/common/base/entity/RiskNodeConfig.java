package com.joyintech.tams.common.base.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述:RSK_NODE_CONFIG表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2022-05-31
 */
public class RiskNodeConfig {
    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 指标触发审批：00：不触发 01：触发
     */
    private String nodeCode;

    /**
     * 节点匹配值
     */
    private String nodeVal;

    /**
     * 节点匹配值描述
     */
    private String nodeDesc;

    /**
     * 层级
     */
    private BigDecimal nodeLevel;

    /**
     * 父节点
     */
    private String pNodeId;

    /**
     * 是否有效 默认 0 有效,1 无效
     */
    private BigDecimal status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
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
     * 子节点
     */
    private List<RiskNodeConfig> children;

    /**
     * 配置信息明细
     */
    private RiskNodeConfigDtl configDtl;

    /**
     * 节点id
     * @return NODE_ID 节点id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * 节点id
     * @param nodeId 节点id
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId == null ? null : nodeId.trim();
    }

    /**
     * 节点名称
     * @return NODE_NAME 节点名称
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * 节点名称
     * @param nodeName 节点名称
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName == null ? null : nodeName.trim();
    }

    /**
     * 节点类型
     * @return NODE_TYPE 节点类型
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * 节点类型
     * @param nodeType 节点类型
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType == null ? null : nodeType.trim();
    }

    /**
     * 指标触发审批：00：不触发 01：触发
     * @return NODE_CODE 指标触发审批：00：不触发 01：触发
     */
    public String getNodeCode() {
        return nodeCode;
    }

    /**
     * 指标触发审批：00：不触发 01：触发
     * @param nodeCode 指标触发审批：00：不触发 01：触发
     */
    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode == null ? null : nodeCode.trim();
    }

    /**
     * 节点匹配值
     * @return NODE_VAL 节点匹配值
     */
    public String getNodeVal() {
        return nodeVal;
    }

    /**
     * 节点匹配值
     * @param nodeVal 节点匹配值
     */
    public void setNodeVal(String nodeVal) {
        this.nodeVal = nodeVal == null ? null : nodeVal.trim();
    }

    /**
     * 节点匹配值描述
     * @return NODE_DESC 节点匹配值描述
     */
    public String getNodeDesc() {
        return nodeDesc;
    }

    /**
     * 节点匹配值描述
     * @param nodeDesc 节点匹配值描述
     */
    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc == null ? null : nodeDesc.trim();
    }

    /**
     * 层级
     * @return NODE_LEVEL 层级
     */
    public BigDecimal getNodeLevel() {
        return nodeLevel;
    }

    /**
     * 层级
     * @param nodeLevel 层级
     */
    public void setNodeLevel(BigDecimal nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    /**
     * 父节点
     * @return P_NODE_ID 父节点
     */
    public String getpNodeId() {
        return pNodeId;
    }

    /**
     * 父节点
     * @param pNodeId 父节点
     */
    public void setpNodeId(String pNodeId) {
        this.pNodeId = pNodeId == null ? null : pNodeId.trim();
    }

    /**
     * 是否有效 默认 0 有效,1 无效
     * @return STATUS 是否有效 默认 0 有效,1 无效
     */
    public BigDecimal getStatus() {
        return status;
    }

    /**
     * 是否有效 默认 0 有效,1 无效
     * @param status 是否有效 默认 0 有效,1 无效
     */
    public void setStatus(BigDecimal status) {
        this.status = status;
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
     * 创建人
     * @return UPDATE_USER 创建人
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * 创建人
     * @param updateUser 创建人
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
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

    /**
     * 更新时间
     * @return UPDATE_TIME 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<RiskNodeConfig> getChildren() {
        if(children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<RiskNodeConfig> children) {
        this.children = children;
    }

    public RiskNodeConfigDtl getConfigDtl() {
        return configDtl;
    }

    public void setConfigDtl(RiskNodeConfigDtl configDtl) {
        this.configDtl = configDtl;
    }
}