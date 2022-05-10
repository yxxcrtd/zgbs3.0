create table INTERF_RDI_BOND_RELEASE_INFO
(
    SECURITY_ID        VARCHAR2(20),
    REISSUE_INDICATOR  CHAR,
    REISSUE_DATE       CHAR(8),
    REISSUE_EVENT_DATE CHAR(8),
    REISSUE_SIZE       NUMBER(10, 2)
)
/

comment on table INTERF_RDI_BOND_RELEASE_INFO is '银行间债券再发行信息接口表'
/

comment on column INTERF_RDI_BOND_RELEASE_INFO.SECURITY_ID is '债券代码'
/

comment on column INTERF_RDI_BOND_RELEASE_INFO.REISSUE_INDICATOR is '增发标识 枚举值： Y-是 N-否'
/

comment on column INTERF_RDI_BOND_RELEASE_INFO.REISSUE_DATE is '增发发行日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_RELEASE_INFO.REISSUE_EVENT_DATE is '增发上市日 格式：YYYYMMDD'
/

comment on column INTERF_RDI_BOND_RELEASE_INFO.REISSUE_SIZE is '增发量（亿）'
/


