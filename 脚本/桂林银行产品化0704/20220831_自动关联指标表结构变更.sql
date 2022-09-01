--表结构变更
alter table PA_PTL_PORTFOLIO add SELF_MANAGEMENT varchar2(50);
comment on column PA_PTL_PORTFOLIO.SELF_MANAGEMENT is '自主管理计划';

alter table PRD_SALE_PARAM add AGENCY_ORIENTED varchar2(50);
comment on column PRD_SALE_PARAM.AGENCY_ORIENTED is '是否面向非机构投资者';