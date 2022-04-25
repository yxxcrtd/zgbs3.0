alter table fin_trade_product add irs_clean_type varchar2(50);
comment on column FIN_TRADE_PRODUCT.irs_clean_type
  is '利率互换清算方式';

alter table fin_trade_product add is_cal varchar2(50);
comment on column FIN_TRADE_PRODUCT.is_cal
  is '利率互换存续期是否计息';