-- 删除数据
delete from t_terminal_agent_info where agent_type='kcbp';

delete from t_terminal_agent_info where agent_type='ufx';

-- 导入数据
insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (52, 'ufx', 106, 'Bidding', null, null, null, to_date('16-02-2022 09:22:24', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333002', '普通下单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (53, 'ufx', 106, 'ETF_APPLY_BUY', null, null, null, to_date('16-02-2022 10:38:40', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333012', 'ETF申购', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (54, 'ufx', 106, 'ETF_APPLY_SELL', null, null, null, to_date('16-02-2022 10:42:22', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333013', 'ETF赎回', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (55, 'ufx', 108, 'Bulk', null, null, null, to_date('16-02-2022 10:43:44', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332702', '综合下单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (56, 'ufx', 108, 'FixedIncome', null, null, null, to_date('16-02-2022 10:45:11', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332722', '固收委托下单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (57, 'ufx', 108, 'RepurchaseSQSZ', null, null, null, to_date('16-02-2022 10:51:44', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332400', '深交所债券质押协议回购初始交易下单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (58, 'ufx', 108, 'RepurchaseBuybackSZ', null, null, null, to_date('16-02-2022 11:01:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332401', '深交所债券质押协议回购购回交易下单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (59, 'ufx', 108, 'RepurchaseRenewalSZ', null, null, null, to_date('16-02-2022 11:03:00', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332410', '深交所债券质押协议回购到期续做下单（未实现）', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (60, 'ufx', 108, 'RepurchaseSH', null, null, null, to_date('16-02-2022 11:06:10', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332725', '上交所债券质押式协议回购', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (61, 'ufx', 105, 'Bidding', null, null, null, to_date('16-02-2022 11:09:34', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333101', '证券委托查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (62, 'ufx', 105, 'ProtocolBack', null, null, null, to_date('16-02-2022 11:10:55', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332704', '综合业务委托查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (63, 'ufx', 104, 'Bidding', null, null, null, to_date('16-02-2022 11:12:08', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333102', '证券成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (64, 'ufx', 107, 'Bidding', null, null, null, to_date('16-02-2022 11:13:34', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333017', '委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (65, 'ufx', 109, 'Bulk', null, null, null, to_date('16-02-2022 11:14:41', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332703', '综合业务委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (66, 'ufx', 109, 'FixedIncome', null, null, null, to_date('16-02-2022 11:16:12', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332723', '上海固收委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (67, 'ufx', 109, 'RepurchaseSZ', null, null, null, to_date('16-02-2022 11:19:49', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332402', '深交所债券质押协议回购委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (68, 'ufx', 109, 'RepurchaseSH', null, null, null, to_date('16-02-2022 11:23:55', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332726', '上交所债券质押式协议回购委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (69, 'ufx', 111, 'IntegratedQuote', null, null, null, to_date('16-02-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_398', '大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (70, 'ufx', 4, null, null, null, null, to_date('16-02-2022 09:19:12', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_331100', '恒生用户登录', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (71, 'ufx', 111, 'RepurchaseSH', null, null, null, to_date('16-02-2022 11:38:53', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332728', '上海债券质押式协议回购行情信息查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (72, 'ufx', 112, '333001', null, null, null, to_date('16-02-2022 11:40:22', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333001', '大约可买获取', 1);

-- insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
-- values (73, 'ufx', 112, '333103', null, null, null, to_date('16-02-2022 11:41:19', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333103', '证券持仓快速查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (74, 'ufx', 113, null, null, null, null, to_date('16-02-2022 11:43:10', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_339304', '历史证券成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (75, 'ufx', 101, null, null, null, null, to_date('16-02-2022 11:44:22', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332254', '客户资金快速查询', 1);

-- insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
-- values (76, 'ufx', 102, null, null, null, null, to_date('16-02-2022 11:45:21', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333103', '证券持仓快速查询', 1);

-- insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
-- values (77, 'ufx', 115, null, null, null, null, to_date('16-02-2022 11:46:37', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333116', '质押回购可用查询', 1);

-- insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
-- values (78, 'ufx', 117, null, null, null, null, to_date('16-02-2022 11:47:45', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_333116', '标准券查询', 1);

-- insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
-- values (79, 'ufx', 118, null, null, null, null, to_date('16-02-2022 13:34:18', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_398', '大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (80, 'ufx', 119, 'IntegratedAfterQuoteSZ', null, null, null, to_date('16-02-2022 13:35:47', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_394', '深圳盘后大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (81, 'ufx', 119, 'IntegratedAfterQuoteSH', null, null, null, to_date('16-02-2022 13:38:22', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_364', '上海盘后大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (83, 'ufx', 121, 'IntegratedBondQuoteSH', null, null, null, to_date('16-02-2022 13:43:31', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_361', '上海固收平台报价行情', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (84, 'ufx', 104, 'ProtocolBack', null, null, null, to_date('16-02-2022 13:52:09', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332705', '综合业务成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (85, 'ufx', 332645, null, null, null, null, to_date('16-02-2022 14:07:22', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332645', '上海RTGS清算信息查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (86, 'ufx', 332646, null, null, null, null, to_date('16-02-2022 14:09:41', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332646', '深圳RTGS清算信息查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (104, 'ufx', 105, null, null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332403', '债券质押协议回购委托查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (105, 'ufx', 108, 'BondOrderSZ', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332647', '债券现券交易协商委托', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (106, 'ufx', 108, 'BondAgreeSZ', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332648', '债券现券交易协商确认委托', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (107, 'ufx', 130, 'BondConsultQuerySZ', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332653', '债券现券转发申报信息查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (108, 'ufx', 106, 'BondSellBackSH', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332742', '固收债券回售申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (109, 'ufx', 107, 'BondSellBackSH', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332743', '固收债券回售申报撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (110, 'ufx', 121, 'IntegratedBondQuoteSZ', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_386', '债券现券逐笔委托行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (111, 'ufx', 111, 'BondRepurchaseQuerySZ', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332440', '债券质押协议回购转发信息查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (112, 'ufx', 111, null, null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332441', '债券质押协议回购转发扩展信息查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (113, 'ufx', 109, 'BondOrderSZ', null, null, null, to_date('17-05-2022 11:28:20', 'dd-mm-yyyy hh24:mi:ss'), 'GFZQ', 'TG_GFZQ_UFX_332651', '深圳债券现券委托撤单', 1);

-- 恒生柜台 修改名称
update t_terminal_agent_info set gp_code='UFX' where gp_code='GFZQ';
update t_terminal_agent_info set TRIGGER_CODE=CONCAT('TG', substr(TRIGGER_CODE,8)) where agent_type='ufx'; 