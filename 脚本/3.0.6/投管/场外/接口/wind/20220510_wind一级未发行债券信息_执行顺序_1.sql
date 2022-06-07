CREATE TABLE INTERF_WIND_UNRELEASE_SEC_INFO(
SEC_ID                                               VARCHAR2(50),
BOND_NAME                                            VARCHAR2(100),
BOND_ABBREVIATION                                    VARCHAR2(50),
TRADING_PLACES                                       VARCHAR2(20),
REGISTERED_CUSTODIAN	                             VARCHAR2(20),
ISSUER						                         VARCHAR2(50),
RAISE_MODE						                     VARCHAR2(20),
DOMESTIC_AND_FOREIGN						         VARCHAR2(20),
VALUE_DATE						                     DATE,
EXPIRY_DATE						                     DATE,
PRIMARY_CLASSIFICATION						         VARCHAR2(20),
SECONDARY_CLASSIFICATION						     VARCHAR2(20),         
VARIETY_OF_COUPONS						             VARCHAR2(20),
INTEREST_PAYMENT_FREQUENCY						     VARCHAR2(20),      
RATE_TYPE						                     VARCHAR2(20),
CURRENCY						                     VARCHAR2(20),
IS_BONDS_WITH_RIGHTS					             VARCHAR2(20),
CROSS_MARKET_CODE						             VARCHAR2(20),
TRADING_CALENDAR						             VARCHAR2(20),
SPECIAL_TERMS						                 VARCHAR2(200)
);					
comment on table  INTERF_WIND_UNRELEASE_SEC_INFO is 'wind一级未发行债券信息接口表';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.SEC_ID                      is '债券ID';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.BOND_NAME                   is '债券名称';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.BOND_ABBREVIATION           is '债券简称';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.TRADING_PLACES              is '交易场所';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.REGISTERED_CUSTODIAN	    is '登记托管机构';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.ISSUER						is '发行人';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.RAISE_MODE					is '募集方式';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.DOMESTIC_AND_FOREIGN		is '境内外';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.VALUE_DATE					is '起息日';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.EXPIRY_DATE					is '到期日';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.PRIMARY_CLASSIFICATION		is '一级分类';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.SECONDARY_CLASSIFICATION	is '二级分类';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.VARIETY_OF_COUPONS			is '息票品种';						
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.INTEREST_PAYMENT_FREQUENCY	is '付息频率';
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.RATE_TYPE					is '利率类型';
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.CURRENCY					is '币种';
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.IS_BONDS_WITH_RIGHTS		is '是否含权债券';
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.CROSS_MARKET_CODE			is '跨市场代码';
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.TRADING_CALENDAR			is '交易日历';
comment on column INTERF_WIND_UNRELEASE_SEC_INFO.SPECIAL_TERMS				is '特殊条款';

































