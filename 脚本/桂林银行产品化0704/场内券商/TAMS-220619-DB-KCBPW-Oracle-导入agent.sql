-- 删除数据
delete from t_terminal_agent_info where agent_type='kcbp' or  agent_type='kcbpw';
-- 导入数据

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (1, 'kcbp', 4, null, null, null, null, to_date('13-10-2021 16:27:13', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410301', '登录资金账号', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (2, 'kcbp', 105, null, null, null, null, to_date('13-10-2021 16:29:29', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410510', '当日委托明细查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (3, 'kcbp', 104, 'Bidding', null, null, null, to_date('13-10-2021 16:32:24', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410436', '当日成交明细查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (4, 'kcbp', 104, 'FixedIncome', null, null, null, to_date('13-10-2021 16:35:24', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460003', '固收平台交易成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (5, 'kcbp', 104, 'ProtocolBack_SZ', null, null, null, to_date('13-10-2021 16:38:33', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411003', '深圳债券协议回购成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (6, 'kcbp', 104, 'ProtocolBack_SH', null, null, null, to_date('13-10-2021 16:39:55', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411007', '上海债券协议回购成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (7, 'kcbp', 106, null, null, null, null, to_date('13-10-2021 16:42:31', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_JJ_410411', '委托下单(竞价)', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (8, 'kcbp', 107, null, null, null, null, to_date('13-10-2021 16:45:47', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410413', '委托撤单(竞价)', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (9, 'kcbp', 108, 'Bulk', null, null, null, to_date('13-10-2021 16:47:58', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_DZ_410411', '委托下单(深交所综合协议,上交所大宗)', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (10, 'kcbp', 108, 'FixedIncomeBS', null, null, null, to_date('13-10-2021 16:49:09', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460010', '固收平台委托买卖交易', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (11, 'kcbp', 108, 'FixedIncomeBaoJia', null, null, null, to_date('13-10-2021 16:52:51', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460013', '固收平台确定报价报价申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (12, 'kcbp', 108, 'FixedIncomeChenJiao', null, null, null, to_date('13-10-2021 16:53:54', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460014', '固收平台确定报价成交申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (13, 'kcbp', 108, 'ProtocolBackSHYiXiang', null, null, null, to_date('13-10-2021 16:54:52', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411031', '上海债券协议回购意向申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (14, 'kcbp', 108, 'ProtocolBack_SH_BaoJia', null, null, null, to_date('13-10-2021 16:55:35', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411032', '上海债券协议回购报价申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (15, 'kcbp', 108, 'ProtocolBack_SH_ChenJiao', null, null, null, to_date('13-10-2021 16:56:28', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411024', '上海债券协议回购成交申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (16, 'kcbp', 108, 'ProtocolBack_SZ', null, null, null, to_date('13-10-2021 16:57:06', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411000', '深证债券协议回购委托申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (17, 'kcbp', 109, 'Bulk', null, null, null, to_date('13-10-2021 17:01:24', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410413', '委托撤单(深交所综合协议,上交所大宗)', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (18, 'kcbp', 109, 'ProtocolBack_SZ', null, null, null, to_date('13-10-2021 17:02:46', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411001', '深交所质押式协议回购委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (19, 'kcbp', 109, 'ProtocolBack_SH', null, null, null, to_date('13-10-2021 17:04:06', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411025', '上交所质押式协议回购委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (20, 'kcbp', 109, 'FixedIncome', null, null, null, to_date('13-10-2021 17:04:42', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460011', '固收平台委托撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (21, 'kcbp', 111, null, 1, 'SZ', null, to_date('13-10-2021 17:08:13', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410995', '深圳大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (22, 'kcbp', 111, null, 1, 'SH', null, to_date('13-10-2021 17:11:13', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410996', '上海大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (23, 'kcbp', 111, null, 2, 'SH', null, to_date('13-10-2021 17:27:03', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410556', '上海盘后固定价格交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (24, 'kcbp', 111, null, 3, 'SH', null, to_date('13-10-2021 17:28:35', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411027', '上海债券协议回购非公开报价行情', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (25, 'kcbp', 111, null, 4, 'SH', null, to_date('13-10-2021 17:29:34', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411029', '上海债券协议回购公开报价行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (26, 'kcbp', 111, null, 5, 'SH', null, to_date('13-10-2021 17:31:21', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460018', '上海固收平台确定报价行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (27, 'kcbp', 112, null, null, null, null, to_date('13-10-2021 17:32:23', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410410', '最大交易数量查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (28, 'kcbp', 113, null, null, null, null, to_date('13-10-2021 17:33:11', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411513', '历史成交明细', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (29, 'kcbp', 101, null, null, null, null, to_date('13-10-2021 17:34:01', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410502', '资金查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (30, 'kcbp', 102, null, null, null, null, to_date('13-10-2021 17:36:28', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410504', '持仓查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (31, 'kcbp', 115, null, null, null, null, to_date('13-10-2021 17:42:26', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410203', '查询合约数据', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (32, 'kcbp', 116, null, null, null, null, to_date('13-10-2021 17:44:03', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410575', '质押库明细查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (33, 'kcbp', 117, null, null, null, null, to_date('13-10-2021 17:45:26', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410574', '标准券汇总查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (34, 'kcbp', 118, null, null, 'SZ', null, to_date('14-10-2021 08:46:41', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410995', '深圳大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (35, 'kcbp', 118, null, null, 'SH', null, to_date('14-10-2021 08:47:42', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410996', '上海大宗交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (36, 'kcbp', 119, null, null, 'SH', null, to_date('14-10-2021 08:49:12', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410556', '上海盘后固定价格交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (37, 'kcbp', 119, null, null, 'SZ', null, to_date('14-10-2021 08:51:28', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410559', '深圳盘后固定价格交易行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (38, 'kcbp', 120, null, 1, null, null, to_date('14-10-2021 08:53:47', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411027', '上海债券协议回购非公开报价行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (39, 'kcbp', 120, null, 0, null, null, to_date('14-10-2021 08:55:06', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411029', '上海债券协议回购公开报价行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (40, 'kcbp', 121, null, null, null, null, to_date('14-10-2021 08:58:11', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460018', '上海固收平台确定报价行情查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (41, 'kcbp', 11, 'Bidding', null, null, null, to_date('14-10-2021 09:02:25', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410510', '当日委托明细查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (42, 'kcbp', 11, 'FixedIncome', null, null, null, to_date('14-10-2021 09:06:41', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460001', '固收平台交易委托明细查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (43, 'kcbp', 11, 'ProtocolBack_SH', null, null, null, to_date('14-10-2021 09:08:40', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411006', '上海债券协议回购委托流水查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (44, 'kcbp', 11, 'ProtocolBack_SZ', null, null, null, to_date('14-10-2021 09:09:54', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411002', '深圳债券协议回购委托流水查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (45, 'kcbp', 12, 'Bidding', null, null, null, to_date('14-10-2021 09:18:19', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410436', '当日成交明细查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (46, 'kcbp', 12, 'FixedIncome', null, null, null, to_date('14-10-2021 09:24:15', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_460003', '固收平台交易成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (47, 'kcbp', 12, 'ProtocolBack_SZ', null, null, null, to_date('14-10-2021 09:26:36', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411003', '深圳债券协议回购成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (48, 'kcbp', 12, 'ProtocolBack_SH', null, null, null, to_date('14-10-2021 09:27:37', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411007', '上海债券协议回购成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (49, 'kcbp', 410232, null, null, null, null, to_date('14-10-2021 09:40:04', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410232', 'KCBP系统状态查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (50, 'kcbp', 411552, null, null, null, null, to_date('14-10-2021 09:41:34', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_411552', 'RTGS交收状态查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (51, 'kcbp', 11, null, 2, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410559', '深圳盘后固定价格交易行情查询', 1);


insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (150, 'kcbp', 108, 'BondOrderSZ', null, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410677', '深圳现券协商成交申报', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (151, 'kcbp', 108, 'BondAgreeSZ',null, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410678', '深圳现券协商成交申报回复', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (152, 'kcbp', 109, 'BondOrderSZ', null, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410679', '深圳现券成交申报、现券竞买发起、现券竞买应价撤单', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (153, 'kcbp', 105, 'ProtocolBack', null, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410680', '深圳现券协商成交、竞买发起、竞买应价委托查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (154, 'kcbp', 104, 'ProtocolBack', null, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410681', '深圳现券协商成交、竞买发起、竞买应价成交查询', 1);

insert into t_terminal_agent_info (ID, AGENT_TYPE, FUNCTION_CODE, ENTRUST_PROP, QUOTE_TYPE, MARKET, ENTRUST_BS, CREATE_TIME, GP_CODE, TRIGGER_CODE, REMARK, STATUS)
values (155, 'kcbp', 130, 'BondConsultQuerySZ', null, null, null, to_date('23-03-2022 15:52:32', 'dd-mm-yyyy hh24:mi:ss'), 'GZYH', 'GZ_KCBP_410682', '转发成交申报查询', 1);


-- 金证W柜台 修改名称
update t_terminal_agent_info set gp_code='KCBPW' ,agent_type='kcbpw' where gp_code='GZYH';
update t_terminal_agent_info set TRIGGER_CODE=CONCAT('TG_KCBPW', substr(TRIGGER_CODE,8)) where agent_type='kcbpw';
 