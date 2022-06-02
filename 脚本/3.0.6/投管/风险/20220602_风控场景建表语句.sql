-- auto-generated definition
DROP TABLE RISK_SCENE_CONFIG;
create table RISK_SCENE_CONFIG
(
    SEQ_NO            VARCHAR2(32) not null
        constraint RISK_SCENE_CONFIG_PK
        primary key,
    BUSINESS_TYPE     VARCHAR2(8),
    FINPROD_TYPE2     VARCHAR2(16),
    PS                VARCHAR2(8),
    DECISION_TYPE     VARCHAR2(8),
    WORKFLOW_STATUS   VARCHAR2(8),
    SPECIAL_MATCH     VARCHAR2(100),
    IND_PRE_CODE      VARCHAR2(100),
    INDICATOR_CONTROL VARCHAR2(8),
    INDICATOR_OCCUPY  VARCHAR2(8),
    INDICATOR_APPROVE VARCHAR2(8),
    CREDIT_PRE_CODE   VARCHAR2(100),
    CREDIT_CONTROL    VARCHAR2(8),
    CREDIT_OCCUPY     VARCHAR2(8),
    AUTHORIZE_CONTROL VARCHAR2(8),
    AUTHORIZE_APPROVE VARCHAR2(8),
    NAME_LIST_CONTROL VARCHAR2(2),
    NAME_LIST_TYPE    VARCHAR2(8),
    PAGE_ID           VARCHAR2(8),
    MODULE_ID         VARCHAR2(32)
)
    /

comment on table RISK_SCENE_CONFIG is '风险场景配置表'
/

comment on column RISK_SCENE_CONFIG.SEQ_NO is '主键（流水）'
/

comment on column RISK_SCENE_CONFIG.BUSINESS_TYPE is '业务类型：数据字典.头寸业务类型'
/

comment on column RISK_SCENE_CONFIG.FINPROD_TYPE2 is '资产大类：数据字典.金融产品类型(投管用)'
/

comment on column RISK_SCENE_CONFIG.PS is '交易方向：数据字典.交易方向'
/

comment on column RISK_SCENE_CONFIG.DECISION_TYPE is '决策动作：数据字典.系统动作
新建/修改/撤回/退回/关闭/成交/完成/作废/回滚/撤销'
/

comment on column RISK_SCENE_CONFIG.WORKFLOW_STATUS is '流程状态：数据字典.流程节点
流程提交/审批生效/流程退回'
/

comment on column RISK_SCENE_CONFIG.SPECIAL_MATCH is '特殊匹配字段(业务字段拼接)
'
/

comment on column RISK_SCENE_CONFIG.IND_PRE_CODE is '指标占用前置节点：前置节点字段名
'
/

comment on column RISK_SCENE_CONFIG.INDICATOR_CONTROL is '指标控制
00：不控
01：软控
02：硬控'
/

comment on column RISK_SCENE_CONFIG.INDICATOR_OCCUPY is '指标占用类型
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放'
/

comment on column RISK_SCENE_CONFIG.INDICATOR_APPROVE is '指标审批
00：不触发
01：触发'
/

comment on column RISK_SCENE_CONFIG.CREDIT_PRE_CODE is '授信占用前置节点
前置节点字段名
'
/

comment on column RISK_SCENE_CONFIG.CREDIT_CONTROL is '授信控制
00：不控
01：软控
02：硬控
'
/

comment on column RISK_SCENE_CONFIG.CREDIT_OCCUPY is '授信占用类型：
00：不占
01：预占
02：实占
03：预占释放
04：实占释放
05：预占释放再预占
06：实占释放再实占
07：实占预占释放
'
/

comment on column RISK_SCENE_CONFIG.AUTHORIZE_CONTROL is '授权控制
00：不控
01：软控
02：硬控'
/

comment on column RISK_SCENE_CONFIG.AUTHORIZE_APPROVE is '授权审批
01：触发
02：不触发'
/

comment on column RISK_SCENE_CONFIG.NAME_LIST_CONTROL is '名单
00：不控
01：软控
02：硬控'
/

comment on column RISK_SCENE_CONFIG.NAME_LIST_TYPE is '名单类型
01：资产
02：机构
03：资产和机构
'
/

comment on column RISK_SCENE_CONFIG.PAGE_ID is '画面ID'
/

comment on column RISK_SCENE_CONFIG.MODULE_ID is '模板Id'
/