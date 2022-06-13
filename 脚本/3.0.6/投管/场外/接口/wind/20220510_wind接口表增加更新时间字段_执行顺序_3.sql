alter table INTERF_WIND_RELEASE_INFO add update_time TIMESTAMP;
COMMENT on column INTERF_WIND_RELEASE_INFO.update_time is '更新时间';
alter table INTERF_WIND_UNRELEASE_SEC_INFO ADD update_time TIMESTAMP;
COMMENT on COLUMN INTERF_WIND_UNRELEASE_SEC_INFO.update_time is '更新时间';
