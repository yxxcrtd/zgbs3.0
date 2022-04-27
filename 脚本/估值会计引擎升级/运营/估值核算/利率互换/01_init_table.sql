/**利率互换的估值模型 新增收付轧差，主键约束增加VALMODEL_ID**/
alter table MST_VAL_IRS drop  primary key;
drop index PK_MST_VAL_IRS;
alter table MST_VAL_IRS
  add constraint PK_MST_VAL_IRS primary key (FINPROD_ID2, DATASOURCE_TYPE, VAL_DATE,VALMODEL_ID);

comment on column MST_VAL_IRS.valmodel_id
  is '估值模型：01单曲贴现，02收付轧差';

/**利率互换的估值模型 新增收付轧差模型 并且默认为单曲贴现模型*/
  alter table BOK_VAL_SCHEME_DETAIL add   VALUATION_ID  VARCHAR2(50) ;
  comment on column BOK_VAL_SCHEME_DETAIL.VALUATION_ID
  is '估值模型';