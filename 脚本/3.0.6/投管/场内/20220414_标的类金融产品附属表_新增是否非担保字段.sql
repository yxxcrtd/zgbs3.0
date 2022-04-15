alter table FIN_PRODUCT_ADD
    add IS_NO_GUARD VARCHAR2(50);

comment on column FIN_PRODUCT_ADD.IS_NO_GUARD is '是否非担保';