delete from SYS_PARA where SYSTEM_CODE='TRD' and PARA_CODE='trd_new_bond_quotation_type_control';

INSERT INTO "SYS_PARA" ("SYSTEM_CODE", "PARA_CODE", "PARAM_VALUE", "DATA_TYPE", "DATA_SCOPE", "REMARK", "CREATE_USER", "CREATE_DEPT", "CREATE_TIME", "UPDATE_USER", "UPDATE_TIME", "INSTITUTION_CODE", "UPDATE_FLG")
VALUES ('TRD', 'trd_new_bond_quotation_type_control', '02', NULL, NULL, '交易端新债默认行情类型控制：01-匹配成交，02-协商成交，03-点击成交，04-询价成交，05-竞买成交', 'system', NULL, TO_TIMESTAMP('2022-05-10 09:16:43.000000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), 'system', TO_TIMESTAMP('2022-05-10 09:16:43.913000', 'SYYYY-MM-DD HH24:MI:SS:FF6'), NULL, '01');




