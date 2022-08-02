alter table BID_RESULT_TOTAL drop column LISTING_DATE;
alter table BID_RESULT_TOTAL add (LISTING_DATE DATE);
comment on column BID_RESULT_TOTAL.LISTING_DATE is '上市日';

alter table WFL_BID_RESULT_TOTAL drop column LISTING_DATE;
alter table WFL_BID_RESULT_TOTAL add (LISTING_DATE DATE);
comment on column WFL_BID_RESULT_TOTAL.LISTING_DATE is '上市日';