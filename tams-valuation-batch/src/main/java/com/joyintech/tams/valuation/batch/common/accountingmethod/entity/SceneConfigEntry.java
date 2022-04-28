/*
 * 文件名：SceneConfigEntry.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2017年1月3日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.valuation.batch.common.accountingmethod.entity;

/**
 * 会计场景配置信息类
 * 
 * @author qiangchengxi
 * @version 1.0 2017年1月3日
 * @see SceneConfigEntry
 * @since 1.0
 */
public class SceneConfigEntry {

    /**
     * 场景分支节点
     */
    private String sceneNodeId;

    /**
     * 场景分支父节点
     */

    private String fSceneNodeId;

    /**
     * 账套代码
     */

    private String booksetId;

    /**
     * 节点代码
     */

    private String nodeId;

    /**
     * 节点值
     */
    private String nodeValue;

    private String accountSceneCode;

    
    /**
     * 获取 sceneNodeId
     * @return sceneNodeId.
     */
    public String getSceneNodeId() {
        return sceneNodeId;
    }

    
    /**
     * 设置 sceneNodeId
     * @param sceneNodeId sceneNodeId
     */
    public void setSceneNodeId(String sceneNodeId) {
        this.sceneNodeId = sceneNodeId;
    }

    
    /**
     * 获取 fSceneNodeId
     * @return fSceneNodeId.
     */
    public String getfSceneNodeId() {
        return fSceneNodeId;
    }

    
    /**
     * 设置 fSceneNodeId
     * @param fSceneNodeId fSceneNodeId
     */
    public void setfSceneNodeId(String fSceneNodeId) {
        this.fSceneNodeId = fSceneNodeId;
    }

    
    /**
     * 获取 booksetId
     * @return booksetId.
     */
    public String getBooksetId() {
        return booksetId;
    }

    
    /**
     * 设置 booksetId
     * @param booksetId booksetId
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    
    /**
     * 获取 nodeId
     * @return nodeId.
     */
    public String getNodeId() {
        return nodeId;
    }

    
    /**
     * 设置 nodeId
     * @param nodeId nodeId
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    
    /**
     * 获取 nodeValue
     * @return nodeValue.
     */
    public String getNodeValue() {
        return nodeValue;
    }

    
    /**
     * 设置 nodeValue
     * @param nodeValue nodeValue
     */
    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public String getAccountSceneCode() {
        return accountSceneCode;
    }

    public void setAccountSceneCode(String accountSceneCode) {
        this.accountSceneCode = accountSceneCode;
    }
}
