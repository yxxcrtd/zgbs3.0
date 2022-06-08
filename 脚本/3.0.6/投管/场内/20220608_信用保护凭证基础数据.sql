
delete from TRD_MINPRICE_CONFIG where SEQ_NO=29;
delete from TRD_MINPRICE_CONFIG where SEQ_NO=30;
insert into TRD_MINPRICE_CONFIG values ('29','03','99','F37','','','N',to_date('1990-01-01', 'yyyy/mm/dd'), '','','system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));
insert into TRD_MINPRICE_CONFIG values ('30','04','99','F37','','','N',to_date('1990-01-01', 'yyyy/mm/dd'), '','','system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));

delete from TRD_MINPRICE_CONFIG_DETAIL where SEQ_NO=29;
delete from TRD_MINPRICE_CONFIG_DETAIL where SEQ_NO=30;
insert into TRD_MINPRICE_CONFIG_DETAIL values ('29','29','','','0.001','system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));
insert into TRD_MINPRICE_CONFIG_DETAIL values ('30','30','','','0.001','system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));

delete from TRD_ATTRIBUTE_CONFIG where CONFIG_NO=54;
delete from TRD_ATTRIBUTE_CONFIG where CONFIG_NO=55;
delete from TRD_ATTRIBUTE_CONFIG where CONFIG_NO=56;
delete from TRD_ATTRIBUTE_CONFIG where CONFIG_NO=57;

delete from TRD_ATTRIBUTE_CONFIG where CONFIG_NO=62;
delete from TRD_ATTRIBUTE_CONFIG where CONFIG_NO=63;
insert into TRD_ATTRIBUTE_CONFIG values ('54','03','99','F37','','','T01','','500000','02','','','','','1000','02','','500000','02','','',to_date('1990-01-01', 'yyyy/mm/dd'),'system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));
insert into TRD_ATTRIBUTE_CONFIG values ('55','03','99','F37','','','T02','','500000','02','','','','','1000','02','','500000','02','','',to_date('1990-01-01', 'yyyy/mm/dd'),'system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));
insert into TRD_ATTRIBUTE_CONFIG values ('56','04','99','F37','','','T01','','500000','02','500000','','','','500000','02','','','','','',to_date('1990-01-01', 'yyyy/mm/dd'),'system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));
insert into TRD_ATTRIBUTE_CONFIG values ('57','04','99','F37','','','T02','','500000','02','500000','','','','500000','02','','','','','',to_date('1990-01-01', 'yyyy/mm/dd'),'system','',to_date('2021-05-12', 'yyyy/mm/dd'), 'system',to_date('2021-05-12', 'yyyy/mm/dd'));

INSERT INTO TRD_ATTRIBUTE_CONFIG values ('62', '03', '99', 'F37', NULL, NULL, 'T04', NULL, '1', '02', NULL, NULL, NULL, NULL, '1', '02', NULL, NULL, '2', NULL, NULL, TO_DATE('1990-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'), 'system', NULL, TO_TIMESTAMP('2021-05-12 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system', TO_TIMESTAMP('2021-05-12 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'));
INSERT INTO TRD_ATTRIBUTE_CONFIG values ('63', '04', '99', 'F37', NULL, NULL, 'T04', NULL, '1', '02', NULL, NULL, NULL, NULL, '1', '02', NULL, NULL, '2', NULL, NULL, TO_DATE('1990-01-01 00:00:00', 'SYYYY-MM-DD HH24:MI:SS'), 'system', NULL, TO_TIMESTAMP('2021-05-12 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system', TO_TIMESTAMP('2021-05-12 00:00:00.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'));



delete from TRD_TIME_CONFIG where CONFIG_NO=23;
delete from TRD_TIME_CONFIG where CONFIG_NO=24;
insert into TRD_TIME_CONFIG values ('23','03','','F37','','','9:30:00','11:30:00','13:00:00','15:30:00','','','','system','','2021-05-12 14:26:06',to_date('system', 'yyyy/mm/dd'), '2021-09-16 00:00:00',to_date('2021-09-16', 'yyyy/mm/dd'),'','');
insert into TRD_TIME_CONFIG values ('24','04','','F37','','','9:15:00','11:30:00','13:00:00','15:30:00','','','','system','','2021-05-12 14:26:06',to_date('system', 'yyyy/mm/dd'), '2021-09-16 00:00:00',to_date('2021-09-16', 'yyyy/mm/dd'),'','');

