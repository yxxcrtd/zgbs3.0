package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class RiskSceneConfig extends BaseEntity {
    /**
     * 主键（流水）
     */
    private String seqNo;

    /**
     * 业务类型：数据字典.头寸业务类型
     */
    private String businessType;

    /**
     * 资产大类：数据字典.金融产品类型(投管用)
     */
    private String finprodType2;

    /**
     * 交易方向：数据字典.交易方向
     */
    private String ps;

    /**
     * 决策动作：数据字典.系统动作
新建/修改/撤回/退回/关闭/成交/完成/作废/回滚/撤销
     */
    private String decisionType;

    /**
     * 流程状态：数据字典.流程节点
流程提交/审批生效/流程退回
     */
    private String workflowStatus;

    /**
     * 特殊匹配字段(业务字段拼接)

     */
    private String specialMatch;

    /**
     * 指标占用前置节点：前置节点字段名

     */
    private String indPreCode;

    /**
     * 指标控制
00：不控
01：软控
02：硬控
     */
    private String indicatorControl;

    /**
     * 指标占用类型
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放
     */
    private String indicatorOccupy;

    /**
     * 指标审批
00：不触发
01：触发
     */
    private String indicatorApprove;

    /**
     * 授信占用前置节点
前置节点字段名

     */
    private String creditPreCode;

    /**
     * 授信控制
00：不控
01：软控
02：硬控

     */
    private String creditControl;

    /**
     * 授信占用类型：
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放

     */
    private String creditOccupy;

    /**
     * 授权控制
00：不控
01：软控
02：硬控
     */
    private String authorizeControl;

    /**
     * 授权审批
01：触发
02：不触发
     */
    private String authorizeApprove;

    /**
     * 名单
00：不控
01：软控
02：硬控
     */
    private String nameListControl;

    /**
     * 名单类型
01：资产
02：机构
03：资产和机构

     */
    private String nameListType;

    /**
     * 画面ID
     */
    private String pageId;

    /**
     * 模板Id
     */
    private String moduleId;

    /**
     * 取得 主键（流水）
     * @return 主键（流水）
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 主键（流水）
     * @param seqNo 主键（流水）
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo == null ? null : seqNo.trim();
    }

    /**
     * 取得 业务类型：数据字典.头寸业务类型
     * @return 业务类型：数据字典.头寸业务类型
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * 设置 业务类型：数据字典.头寸业务类型
     * @param businessType 业务类型：数据字典.头寸业务类型
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType == null ? null : businessType.trim();
    }

    /**
     * 取得 资产大类：数据字典.金融产品类型(投管用)
     * @return 资产大类：数据字典.金融产品类型(投管用)
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 资产大类：数据字典.金融产品类型(投管用)
     * @param finprodType2 资产大类：数据字典.金融产品类型(投管用)
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 交易方向：数据字典.交易方向
     * @return 交易方向：数据字典.交易方向
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向：数据字典.交易方向
     * @param ps 交易方向：数据字典.交易方向
     */
    public void setPs(String ps) {
        this.ps = ps == null ? null : ps.trim();
    }

    /**
     * 取得 决策动作：数据字典.系统动作
新建/修改/撤回/退回/关闭/成交/完成/作废/回滚/撤销
     * @return 决策动作：数据字典.系统动作
新建/修改/撤回/退回/关闭/成交/完成/作废/回滚/撤销
     */
    public String getDecisionType() {
        return decisionType;
    }

    /**
     * 设置 决策动作：数据字典.系统动作
新建/修改/撤回/退回/关闭/成交/完成/作废/回滚/撤销
     * @param decisionType 决策动作：数据字典.系统动作
新建/修改/撤回/退回/关闭/成交/完成/作废/回滚/撤销
     */
    public void setDecisionType(String decisionType) {
        this.decisionType = decisionType == null ? null : decisionType.trim();
    }

    /**
     * 取得 流程状态：数据字典.流程节点
流程提交/审批生效/流程退回
     * @return 流程状态：数据字典.流程节点
流程提交/审批生效/流程退回
     */
    public String getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * 设置 流程状态：数据字典.流程节点
流程提交/审批生效/流程退回
     * @param workflowStatus 流程状态：数据字典.流程节点
流程提交/审批生效/流程退回
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus == null ? null : workflowStatus.trim();
    }

    /**
     * 取得 特殊匹配字段(业务字段拼接)

     * @return 特殊匹配字段(业务字段拼接)

     */
    public String getSpecialMatch() {
        return specialMatch;
    }

    /**
     * 设置 特殊匹配字段(业务字段拼接)

     * @param specialMatch 特殊匹配字段(业务字段拼接)

     */
    public void setSpecialMatch(String specialMatch) {
        this.specialMatch = specialMatch == null ? null : specialMatch.trim();
    }

    /**
     * 取得 指标占用前置节点：前置节点字段名

     * @return 指标占用前置节点：前置节点字段名

     */
    public String getIndPreCode() {
        return indPreCode;
    }

    /**
     * 设置 指标占用前置节点：前置节点字段名

     * @param indPreCode 指标占用前置节点：前置节点字段名

     */
    public void setIndPreCode(String indPreCode) {
        this.indPreCode = indPreCode == null ? null : indPreCode.trim();
    }

    /**
     * 取得 指标控制
00：不控
01：软控
02：硬控
     * @return 指标控制
00：不控
01：软控
02：硬控
     */
    public String getIndicatorControl() {
        return indicatorControl;
    }

    /**
     * 设置 指标控制
00：不控
01：软控
02：硬控
     * @param indicatorControl 指标控制
00：不控
01：软控
02：硬控
     */
    public void setIndicatorControl(String indicatorControl) {
        this.indicatorControl = indicatorControl == null ? null : indicatorControl.trim();
    }

    /**
     * 取得 指标占用类型
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放
     * @return 指标占用类型
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放
     */
    public String getIndicatorOccupy() {
        return indicatorOccupy;
    }

    /**
     * 设置 指标占用类型
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放
     * @param indicatorOccupy 指标占用类型
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放
     */
    public void setIndicatorOccupy(String indicatorOccupy) {
        this.indicatorOccupy = indicatorOccupy == null ? null : indicatorOccupy.trim();
    }

    /**
     * 取得 指标审批
00：不触发
01：触发
     * @return 指标审批
00：不触发
01：触发
     */
    public String getIndicatorApprove() {
        return indicatorApprove;
    }

    /**
     * 设置 指标审批
00：不触发
01：触发
     * @param indicatorApprove 指标审批
00：不触发
01：触发
     */
    public void setIndicatorApprove(String indicatorApprove) {
        this.indicatorApprove = indicatorApprove == null ? null : indicatorApprove.trim();
    }

    /**
     * 取得 授信占用前置节点
前置节点字段名

     * @return 授信占用前置节点
前置节点字段名

     */
    public String getCreditPreCode() {
        return creditPreCode;
    }

    /**
     * 设置 授信占用前置节点
前置节点字段名

     * @param creditPreCode 授信占用前置节点
前置节点字段名

     */
    public void setCreditPreCode(String creditPreCode) {
        this.creditPreCode = creditPreCode == null ? null : creditPreCode.trim();
    }

    /**
     * 取得 授信控制
00：不控
01：软控
02：硬控

     * @return 授信控制
00：不控
01：软控
02：硬控

     */
    public String getCreditControl() {
        return creditControl;
    }

    /**
     * 设置 授信控制
00：不控
01：软控
02：硬控

     * @param creditControl 授信控制
00：不控
01：软控
02：硬控

     */
    public void setCreditControl(String creditControl) {
        this.creditControl = creditControl == null ? null : creditControl.trim();
    }

    /**
     * 取得 授信占用类型：
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放

     * @return 授信占用类型：
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放

     */
    public String getCreditOccupy() {
        return creditOccupy;
    }

    /**
     * 设置 授信占用类型：
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放

     * @param creditOccupy 授信占用类型：
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放

     */
    public void setCreditOccupy(String creditOccupy) {
        this.creditOccupy = creditOccupy == null ? null : creditOccupy.trim();
    }

    /**
     * 取得 授权控制
00：不控
01：软控
02：硬控
     * @return 授权控制
00：不控
01：软控
02：硬控
     */
    public String getAuthorizeControl() {
        return authorizeControl;
    }

    /**
     * 设置 授权控制
00：不控
01：软控
02：硬控
     * @param authorizeControl 授权控制
00：不控
01：软控
02：硬控
     */
    public void setAuthorizeControl(String authorizeControl) {
        this.authorizeControl = authorizeControl == null ? null : authorizeControl.trim();
    }

    /**
     * 取得 授权审批
01：触发
02：不触发
     * @return 授权审批
01：触发
02：不触发
     */
    public String getAuthorizeApprove() {
        return authorizeApprove;
    }

    /**
     * 设置 授权审批
01：触发
02：不触发
     * @param authorizeApprove 授权审批
01：触发
02：不触发
     */
    public void setAuthorizeApprove(String authorizeApprove) {
        this.authorizeApprove = authorizeApprove == null ? null : authorizeApprove.trim();
    }

    /**
     * 取得 名单
00：不控
01：软控
02：硬控
     * @return 名单
00：不控
01：软控
02：硬控
     */
    public String getNameListControl() {
        return nameListControl;
    }

    /**
     * 设置 名单
00：不控
01：软控
02：硬控
     * @param nameListControl 名单
00：不控
01：软控
02：硬控
     */
    public void setNameListControl(String nameListControl) {
        this.nameListControl = nameListControl == null ? null : nameListControl.trim();
    }

    /**
     * 取得 名单类型
01：资产
02：机构
03：资产和机构

     * @return 名单类型
01：资产
02：机构
03：资产和机构

     */
    public String getNameListType() {
        return nameListType;
    }

    /**
     * 设置 名单类型
01：资产
02：机构
03：资产和机构

     * @param nameListType 名单类型
01：资产
02：机构
03：资产和机构

     */
    public void setNameListType(String nameListType) {
        this.nameListType = nameListType == null ? null : nameListType.trim();
    }

    /**
     * 取得 画面ID
     * @return 画面ID
     */
    public String getPageId() {
        return pageId;
    }

    /**
     * 设置 画面ID
     * @param pageId 画面ID
     */
    public void setPageId(String pageId) {
        this.pageId = pageId == null ? null : pageId.trim();
    }

    /**
     * 取得 模板Id
     * @return 模板Id
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * 设置 模板Id
     * @param moduleId 模板Id
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId == null ? null : moduleId.trim();
    }
}