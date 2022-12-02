
/**券商对账单表新增字段**/

alter table STL_SEC_RECON_DEAL add NET_COMMISSION_AMT NUMBER(30, 2);
comment on column STL_SEC_RECON_DEAL.NET_COMMISSION_AMT is '净佣金';


