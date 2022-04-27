delete from SYS_PARA where SYSTEM_CODE='TRD' and PARA_CODE='trd_instruction_appoint_check_filter';

INSERT INTO "SYS_PARA" ("SYSTEM_CODE", "PARA_CODE", "PARAM_VALUE", "DATA_TYPE", "DATA_SCOPE", "REMARK", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "UPDATE_FLG")
VALUES ('TRD', 'trd_instruction_appoint_check_filter', '02', NULL, NULL, '交易端接收指令分发数据check过滤开关：01-开启，02-关闭', 'system', NULL, TO_TIMESTAMP('2022-04-27 17:16:43.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system', TO_TIMESTAMP('2022-04-27 16:10:02.913000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), NULL, '01');




