alter table ETR_ENTRUST drop column UNIT_FPRICE_AMT;
alter table WFL_ETR_ENTRUST drop column UNIT_FPRICE_AMT;
alter table ETR_ENTRUST add (UNIT_FPRICE_AMT NUMBER(30,2));
alter table WFL_ETR_ENTRUST add (UNIT_FPRICE_AMT NUMBER(30,2));