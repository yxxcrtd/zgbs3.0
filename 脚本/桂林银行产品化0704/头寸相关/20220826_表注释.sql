-- Add comments to the table 
comment on table PZX_CFG_DECISION_TREE
  is '决策树配置信息';
-- Add comments to the columns 
comment on column PZX_CFG_DECISION_TREE.NDID
  is '节点ID';
comment on column PZX_CFG_DECISION_TREE.RTCODE
  is '接口ID';
comment on column PZX_CFG_DECISION_TREE.NDNAME
  is '节点名称';
comment on column PZX_CFG_DECISION_TREE.NDTYPE
  is '节点类型：（decision_dict条件节点，position头寸节点）';
comment on column PZX_CFG_DECISION_TREE.FPMCODE
  is '节点挂钩字典CODE';
comment on column PZX_CFG_DECISION_TREE.NDEXPR
  is '节点值CODE（字典值，头寸组件ID）';
comment on column PZX_CFG_DECISION_TREE.NDLEVEL
  is '节点层级';
comment on column PZX_CFG_DECISION_TREE.PNDID
  is '上级节点ID';
comment on column PZX_CFG_DECISION_TREE.CRTTIME
  is '创建时间';
comment on column PZX_CFG_DECISION_TREE.UPDTIME
  is '修改时间';
comment on column PZX_CFG_DECISION_TREE.USERNM
  is '创建用户';
comment on column PZX_CFG_DECISION_TREE.REMARK1
  is '备注1';
comment on column PZX_CFG_DECISION_TREE.REMARK2
  is '备注2';
comment on column PZX_CFG_DECISION_TREE.REMARK3
  is '备注3';
comment on column PZX_CFG_DECISION_TREE.MSGCODE
  is '品种';
comment on column PZX_CFG_DECISION_TREE.NDEXPRDESC
  is '节点值描述';
  
-- Add comments to the table 
comment on table PZX_CFG_FUN
  is '余额计算函数配置';
