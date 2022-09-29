
-- 默认匹配要素配置
delete from BANK_SYS_PARA where PARA_CODE in ('matchFeatures01','matchFeatures02','matchFeatures03');

INSERT INTO BANK_SYS_PARA (PARA_CODE, PARA_NAME, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, IS_HAVE_SUB, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', '一级申购', '01', '01', '01 启用', '匹配要素:一级申购,具体字段见子表', 'Y', 'system', 'system', sysdate, 'system', sysdate);

INSERT INTO BANK_SYS_PARA ( PARA_CODE, PARA_NAME, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, IS_HAVE_SUB, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', '现券买卖', '01', '01', '01 启用', '匹配要素:现券买卖,具体字段见子表', 'Y', 'system', 'system', sysdate, 'system', sysdate);

INSERT INTO BANK_SYS_PARA ( PARA_CODE, PARA_NAME, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, IS_HAVE_SUB, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', '质押式回购首期', '01', '01', '01 启用', '匹配要素:质押式回购首期,具体字段见子表', 'Y', 'system', 'system', sysdate, 'system', sysdate);

delete from BANK_SYS_PARA_SUB where PARA_CODE in ('matchFeatures01','matchFeatures02','matchFeatures03');

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'tradeDate', '成交日期', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'finprodMarketId', '资产代码', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'tradeMarket', '交易场所', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'ouPartyId', '本方', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'subscriptionAmount', '认购量', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'issuePrice', '发行价格', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'ourTrader', '本方交易员', '02', '02', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures01', 'payableAmount', '应缴款金额', '02', '02', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'counterId', '对方手', '02', '02', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'deliveryType', '结算方式', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'faceValue', '券面总额', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'finprodMarketId', '资产代码', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'ps', '交易方向', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'secManageAcctId', '证券账户', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'settleAtm', '结算金额', '02', '01', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'settleDate', '结算日期', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'settleSpeed', '清算速度', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'tradeDate', '成交日期', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'tradeMarket', '交易场所', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures02', 'yield', '成交价', '02', '01', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '净价/到期收益率/行权收益率', 'system', 'system', sysdate, 'jp', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'tradeDate', '成交日期', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'termDays', '回购期限', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'tradeMarket', '交易场所', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'ps', '交易方向', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'settleSpeed', '清算速度', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'secManageAcctId', '证券账户', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'rate', '回购利率', '02', '02', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'vsettleDate', '首次结算日', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'msettleDate', '到期结算日', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'tradeAmt', '交易金额', '01', '01', '01', '01', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'counterId', '对手方', '02', '02', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'mTradeAmt', '到期结算金额', '02', '02', '01', '02', '01', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '01置灰 02 不置灰(参数值3 表示自动匹配要素 参数值4 表示手工匹配要素)', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'pledgeFinprodId', '债券代码', '01', '01', '01', '01', '02', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '质押券信息', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'pledgeRatio', '折算比例', '01', '01', '01', '01', '02', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '质押券信息', 'system', 'system', sysdate, 'admin', sysdate);

INSERT INTO BANK_SYS_PARA_SUB (PARA_CODE, SUB_PARA_CODE, SUB_PARA_NAME, PARAM_VALUE1, PARAM_VALUE2, PARAM_VALUE3, PARAM_VALUE4, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME) 
VALUES ('matchFeatures03', 'pledgeFaceValue', '券面总额', '01', '01', '01', '01', '02', '01 基础要素 02 可选要素(参数值1 表示自动匹配要素 参数值2 表示手工匹配要素)', '质押券信息', 'system', 'system', sysdate, 'admin', sysdate);

delete from SYS_PARA where PARA_CODE = 'CFETS_IS_AUTO_SURE';

INSERT INTO SYS_PARA (SYSTEM_CODE, PARA_CODE, PARAM_VALUE, DATA_TYPE, DATA_SCOPE, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, INSTITUTION_CODE, UPDATE_FLG) 
VALUES ('COM', 'CFETS_IS_AUTO_SURE', 'Y', '02', null, '匹配确认数据是否需要手工确认_银行间成交确认(Y 是,N 否)', 'system', null, sysdate, 'system', sysdate, null, '01');

delete from SYS_PAGE_ELEMENT where ELEMENT_ID = 'CfetsTrdConfirmConfirm_btn_matchFeatures';

INSERT INTO SYS_PAGE_ELEMENT (PAGE_ID, ELEMENT_ID, ELEMENT_TYPE, IS_CONTROL, TRANSFER_PAGE_ID, REMARK, CREATE_USER, CREATE_DEPT, CREATE_TIME, UPDATE_USER, UPDATE_TIME, ADMIN_FLAG, PARENT_ELEMENT_ID) 
VALUES ('CfetsTrdConfirm', 'CfetsTrdConfirmConfirm_btn_matchFeatures', '01', '01', null, '匹配元素', 'system', null, sysdate, 'system', sysdate, 'C', 'CfetsTrdConfirmDealTab');