--1.新建clob类型的字段
alter table RSK_ISUP_POINT_VAL add MATH_PROCESS_CLOB_TEMP clob;
--2.把原字段的值插入到新建字段中
update RSK_ISUP_POINT_VAL set MATH_PROCESS_CLOB_TEMP = MATH_PROCESS;
--3.删除原字段
alter table RSK_ISUP_POINT_VAL drop column MATH_PROCESS;
--4.新字段重命名
alter table RSK_ISUP_POINT_VAL rename column MATH_PROCESS_CLOB_TEMP to MATH_PROCESS;