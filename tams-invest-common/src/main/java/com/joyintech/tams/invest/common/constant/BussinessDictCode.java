/*
 * 文件名：BussinessDictCode.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved. 描述：中信银行资管系统
 * 修改人：丁志鹏 修改时间：2018年8月4日 修改内容：新建 系统名称：平安银行
 */
package com.joyintech.tams.invest.common.constant;

import com.joyintech.framework.util.StringUtil;

/**
 * 数据字典枚举
 *
 * @author 丁志鹏
 * @version 1.0 2018年8月4日
 * @see BussinessDictCode
 * @since 1.0
 */
public enum BussinessDictCode implements com.joyintech.framework.base.BaseDictCode{

    /**
     * 行内资产类别
     */
    行内资产类别("CHB_BANK_ASSET_TYPE"),
    /**
     * 出入库方向
     */
    出入库方向("LIBRARY_DIRECTION"),
    /**
     * 项目类一级分类
     */
    项目类一级分类("ASSET_TYPE_PROJECT"),
    /**
     * 投资目的
     */
    投资目的("INV_AIM"),
    /**
     * 处理状态
     */
    处理状态("DISPOSE_STATUS"),
    /**
     * 委托关闭方式
     */
    委托关闭方式("ENTRUST_CLOSE_TYPE"),
    /**
     * 委托接收状态
     */
    委托接收状态("ENTRUST_RECEIVE_TYPE"),
    /**
     * 委托接收状态
     */
    指令处理状态("COMMAND_DEAL_STATUS"),
    /**
     * 委托操作类型
     */
    委托操作类型("ENTRUST_OPERATE_TYPE"),
    /**
     * 提醒类型_大分类
     */
    提醒类型_大分类("REMINDER_TYPE_BIG"),
    /**
     * 提醒类型_小分类
     */
    提醒类型_小分类("REMINDER_TYPE_SMALL"),
    /**
     * 金融产品类型(投管用)
     */
    金融产品类型_投管用("FINPROD_TYPE_TG"),
    /**
    * 金融产品类型
    */
    金融产品类型("FINPROD_TYPE"),
    /**
     * 分红方案
     */
    分红方案("BONU_SCALC_FLAG"),
    /**
    * 收益类型
    */
    收益类型("PROFIT_TYPE"),
    /**
    * 息票品种
    */
    息票品种("COUPON_SPECIES"),
    /**
    * 币种
    */
    币种("CCY"),
    /**
     * 指标参数实现类型
     */
    指标参数实现类型("PARA_DISPLAY_TYPE"),
    /**
     * 指标参数粒度
     */
    指标参数粒度("PARA_GRANULARITY"),
    /**
     * 本金收益特征
     */
    本金收益特征("AMT_INCOME_TYPE"),
    /**
    * 境内外
    */
    境内外("BLN_AREA"),
    /**
     * 币种对
     */
    币种对("CYY_PAIR"),
    /**
     * 交易市场
     */
    交易市场("SHARES_TRADE_MARKET"),
    /**
     * 资产来源
     */
    资产来源("ASSET_SOURCE"),
    /**
     * 资产来源条线
     */
    资产来源条线("ASSET_SOURCE_LINE"),
    /**
     * 曲线名称
     */
    曲线名称("CURVE_DATA"),
    /**
    * 交易流通场所
    */
    交易流通场所("TRADE_MARKET"),
    /**
     * 交易场所
     */
    交易场所("TRADE_PLACE"),
    /**
    * 发行方式
    */
    发行方式("ISSUE_TYPE"),
    /**
    * 是否
    */
    是否("YES_OR_NO"),
    /**
     * 是否
     */
     是否01_02("YES_NO"),
    /**
    * 分支类型
    */
    分支类型("BRANCH_TYPE"),
    /**
    * 回购类型
    */
    回购类型("TARGET_BACK_TYPE"),
    /**
    * 利率类型
    */
    利率类型("INT_TYPE"),
    /**
    * 计息基础
    */
    计息基础("BASIS"),
    /**
    * WIND一级分类（含债券一级分类等）
    */
    WIND一级分类_含债券一级分类等("WINDTYPEONE"),
    /**
    * WIND二级分类（含债券二级分类等）
    */
    WIND二级分类_含债券二级分类等("WINDTYPETWO"),
    /**
    * 重置类型(基准利率调整规则)
    */
    重置类型_基准利率调整规则("RESET_TYPE"),
    /**
    * 提前观察单位(延后时间单位)
    */
    提前观察单位_延后时间单位("ADVANCE_OBSERV_UNIT"),
    /**
    * 营业日准则
    */
    营业日准则("TDAY_RULE"),
    // 一体化对应etliu start
//    /**
//    * 支付频率(付息频率)
//    */
//    支付频率_付息频率("INTPAY_CYCLE"),
    // 一体化对应etliu end
    /**
    * 行权状态
    */
    行权状态("EXEC_TYPE"),
    /**
    * 行权方式
    */
    行权方式("EXEC_MODE"),
    /**
    * 评级类型
    */
    评级类型("MAIN_GRADE_TYPE"),
    /**
    * 长期短期
    */
    长期短期("LONG_SHORT_TERM"),
    /**
    * 评级结果
    */
    评级结果("MAIN_GRADE_RESULT"),
    /**
     * 评级机构
     */
    评级机构("GRADE_PARTY"),
    /**
    * 资产负债类别
    */
    资产负债类别("ASSETDEBT_TYPE"),
    /**
    * 二级资产类别
    */
    二级资产类别("ASSET_TYPE_SECOND_LEVEL"),
    /**
     * 机构类型
     */
    机构类型("DEPTTYPE"),

    /**
    * 机构类型(按规模划分)
    */
    机构类型_按规模划分("PARTY_TYPE1"),
    /**
    * 机构类型(按经济类型划分)
    */
    机构类型_按经济类型划分("PARTY_TYPE2"),
    /**
    * 机构类型(按技术领域划分)
    */
    机构类型_按技术领域划分("PARTY_TYPE3"),
    /**
    * 存款类资产类型
    */
    存款类资产类型("DEPO_ASSET_TYPE"),
    /**
    * 存款类资产存款类型
    */
    存款类资产存款类型("DEPO_TYPE"),
    /**
    * 结构性产品挂钩标的
    */
    结构性产品挂钩标的("STRUCT_PROD_LINK_SUBJECT"),
    /**
    * 基金类资产类型
    */
    基金类资产类型("FUND_ASSET_TYPE"),
    /**
    * 基金类资产二级类型
    */
    基金类资产二级类型("FUND_ASSET_SECOND_TYPE"),
    /**
    * 分红方式
    */
    分红方式("BONUS_MODE"),
    /**
    * 收益结转频率
    */
    收益结转频率("RETURN_FREQUENCY"),
    /**
    * 清算速度
    */
    清算速度("ACCOUNT_SPEED"),
    /**
    * 机构所属行业一级分类
    */
    机构所属行业一级分类("INSTITUTION_INDUSTRY_FIRST_LEVEL"),
    /**
    * 机构所属行业二级分类
    */
    机构所属行业二级分类("INSTITUTION_INDUSTRY_SECOND_LEVEL"),
    /**
    * 登记托管机构
    */
    登记托管机构("REGIST_ORG_INV"),
    /**
    * 登记备案机构
    */
    登记备案机构("REGIST_ORG_REC"),
    /**
    * 投资阶段
    */
    投资阶段("INVEST_STAGE"),
    /**
    * 估值来源
    */
    估值来源("VALUATION_SOURCE"),
    /**
    * 录入方式
    */
    录入方式("INPUT_TYPE"),
    /**
    * 生成方式
    */
    生成方式("CREATE_TYPE"),
    /**
    * 基准类型
    */
    基准类型("BENCHMARK_TYPE"),
    /**
     * 金额显示精度
     */
    金额显示精度("AMOUNT_DISPLAY_ACCURACY"),
    /**
    * 行情类型
    */
    行情类型("MARKET_TYPE"),
    /**
    * 股票交易状态
    */
    股票交易状态("SHARES_TRADE_STATUS"),
    /**
     * 交易状态
     */
    交易状态("TRADE_STATUS"),
    /**
    * 公司性质
    */
    公司性质("COMPANY_NATURE"),
    /**
    * 客户性质
    */
    客户性质("CUSTOMER_NATURE"),
    /**
    * 客户类型
    */
    客户类型 ("CUSTOMER_TYPE"),
    /**
    * 控股类型
    */
    控股类型("HOLDING_TYPE"),
    /**
    * 客户主体类型
    */
    客户主体类型("CUSTOMER_PRIN_TYPE"),
    /**
    * 授信策略
    */
    授信策略("CREDIT_POLICY"),
    /**
    * 产业结构调整类型
    */
    产业结构调整类型("INDUSTY_STRUCT_ADJ_TYPE"),
    /**
    * 工业转型升级标识
    */
    工业转型升级标识("INDUSTY_TRANSFORM_TYPE"),
    /**
    * 客户评级类型
    */
    客户评级类型("CUSTOMER_RATING_TYPE"),
    /**
    * 客户评级认定结果
    */
    客户评级认定结果("CUSTOMER_RATING_RESULT"),
    /**
    * 客户评级使用模型类别
    */
    客户评级使用模型类别("CUSTOMER_RATING_MODULE_TYPE"),
    /**
    * 性别
    */
    性别("SEX"),
    /**
    * 收益率曲线类型
    */
    收益率曲线类型("CURVE_TYPE"),
    /**
    * 利率行情
    */
    利率行情("SECRATE_DATA"),
    /**
    * 指数行情
    */
    指数行情("INDEX_DATA"),
    /**
    * 收益率曲线
    */
    收益率曲线("CURVE_DATA"),
    /**
    * 交易方向
    */
    交易方向("PS"),
    /**
    * 销售部门
    */
    销售部门("SALE_DEPT"),
    /**
    * 客户等级
    */
    客户等级("CLIENT_RANK"),

 // 一体化对应etliu start
//    /**
//    * 还款周期
//    */
//    还款周期("REPAYMENT_PERIOD"),
 // 一体化对应etliu end
    /**
    * 具体类别
    */
    具体类别("CHB_SPECIFIC_SORT"),
    /**
    * 委托状态
    */
    委托状态("ENTRUST_STATUS"),
    /**
    * 货币对
    */
    货币对("CYY_PAIR"),
    /**
    * OPICS参数类型
    */
    OPICS参数类型("OPICS_PARA_YPE"),
    /**
    * 关联范围
    */
    关联范围("CORRELATION_RANGE"),
    /**
    * 费用类型
    */
    费用类型("FEE_TYPE"),
    /**
    * 指令类型
    */
    指令类型("ORDER_TYPE"),
    /**
    * 机构费用类型
    */
    机构费用类型("INST_FEE_TYPE"),
    /**
    * 机构费用二级类型
    */
    机构费用二级类型("INST_FEE_SEC_TYPE"),
    /**
    * 收费类型
    */
    收费类型("CHARGE_TYPE"),
    /**
     * 基数类型
     */
    基数类型("BASE_TYPE"),
    /**
    * 计费基数(费用品种)
    */
    计费基数_费用品种("BILL_BASE_FEE_KIND"),
    /**
    * 业务编号类型
    */
    业务编号类型("BUSINESS_ID_TYPE"),
    /**
    * 日期规则
    */
    日期规则("DATE_RULE_TYPE"),
    /**
     * 周期单位
     */
    周期单位("PERIOD_UNIT"),
    /**
    * 业务编号一级分类
    */
    业务编号一级分类("BUSINESS_ID_TYPE_1"),
    /**
    * 政府投资基金投向
    */
    中债_政府投资基金投向("CHB_INVEST_FUND_TURN"),
    /**
    * 基金类二级资产类别
    */
    中债_基金类二级资产类别("CHB_FUND_ASSET_SECOND_TYPE"),
    /**
     * 存款类二级资产类别
     */
    中债_存款类二级资产类别("CHB_DEPO_ASSET_TYPE"),
    /**
     * 债券类二级资产类别
     */
    中债_债券类二级资产类别("CHB_WINDTYPEONE"),
    /**
     * 项目类二级资产类别
     */
    中债_项目类二级资产类别("CHB_ASSET_TYPE_PROJECT"),
    /**
     * 存款类二级资产类别
     */

    /**
    * 结构性存款挂钩标的类别
    */
    中债_结构性存款挂钩标的类别("CHB_STRUCT_PROD_LINK_SUBJECT"),
    /**
    * 是否
    */
    中债_是否("CHB_YES_OR_NO"),
    /**
    * 计息基础
    */
    中债_计息基础("CHB_BASIS"),
    /**
    * 发行方式
    */
    中债_发行方式("CHB_ISSUE_TYPE"),
    /**
    * 主体评级
    */
    中债_主体评级("CHB_BODY_GRADE"),
    /**
    * 登记托管机构
    */
    中债_登记托管机构("CHB_REGIST_ORG_INV"),
    /**
    * 结算状态
    */
    结算状态("SETTLEMENT_STATUS"),
    /**
    * 拆借类型
    */
    拆借类型("BANK_LENDING_TYPE"),
    /**
    * 文件类型
    */
    文件类型("FILE_TYPE_FG"),
    /**
    * 附件类型
    */
    附件类型("ATTACHMENT_TYPE"),
    /**
    * 模板类型
    */
    模板类型("TEMPLATE_TYPE"),
    /**
    * 资金流动类型
    */
    资金流动类型("FUND_FLOW_TYPE"),
    /**
    * 处理模式
    */
    处理模式("DISPOSE_PATTERN"),
    /**
    * 开放类型
    */
    开放类型("ISSUE_MODE"),
    /**
    * 产品收益标识
    */
    产品收益标识("PRODPROFITFLAG"),
    /**
    * 分层类型
    */
    分层类型("CLASSIFICATION_TYPE"),
    /**
    * 募集方式
    */
    募集方式("RAISETYPE"),
    /**
    * 投资性质
    */
    投资性质("INVEST_TYPE"),
    /**
    * 开放模式
    */
    开放模式("OPENMODE"),
    /**
    * 推算方法
    */
    推算方法("CALC_METHOD"),

    // 一体化对应etliu start
//    /**
//    * 推算规则
//    */
//    推算规则("CALC_RULE"),
    /**
    * 推算方式
    */
    推算方式("CALC_RULE"),
    // 一体化对应etliu end

    /**
    * 付息类型
    */
    付息类型("INTEREST_TYPE"),
    /**
    * 尾数处理方式
    */
    尾数处理方式("MANTISSA_PROCESS_MODE"),
  // 一体化对应etliu start
//    /**
//    * 资产净值日类型
//    */
//    资产净值日类型("AST_VLU_DATE_TYPE"),
//    /**
//    * 付费规则
//    */
//    付费规则("PAY_RULE"),
// 一体化对应etliu end

    /**
    * 成功失败标志
    */
    成功失败标志("IS_SUCCESS"),
    /**
     * 目标客户类型
    */
    目标客户类型("SALETARGET_DECLARE"),
    /**
    * 计划类资产一级分类
    */
    计划类资产一级分类("PLAN_ASSET_TYPE"),
    /**
    * 计划类资产二级分类
    */
    计划类资产二级分类("PLAN_ASSET_SEC_TYPE"),
    /**
    * 现金流类型
    */
    现金流类型("CASH_CLASS_TYPE"),
    /**
    * 申赎开放类型
    */
    申赎开放类型("RANSOM_ISSUE_MODE"),
    /**
    * 期限品种(产品端)
    */
    期限品种_产品端("TERM_TYPE_PRODUCT"),
    /**
     * 期限品种
     */
    期限品种("TERM_TYPE_REBACK"),
    /**
    * 规律开放周期
    */
    规律开放周期("REGULAR_OPEN_CYCLE"),
    /**
    * 开放期业务
    */
    开放期业务("OPEN_BUSS"),
    /**
    * 排期发行方式
    */
    排期发行方式("SCHEDUAL_ISSUE"),
    /**
    * 委外协议
    */
    委外协议("CHB_OUTSOURCE_AGREEMENT"),
    /**
    * 资金实际投向
    */
    资金实际投向("CHB_INVESTTYPE_DECLARE"),
    /**
    * 资管计划属性
    */
    资管计划属性("CHB_PRO_PROPERTY"),
    /**
    * 委外投资属性
    */
    委外投资属性("CHB_OUTSOURCE_INVEST_PROPERTY"),
    /**
    * 购买结构
    */
    购买结构("CHB_BUY_STRUCT"),
    /**
    * 管理方式
    */
    管理方式("CHB_MANATYPE_PLAN_ASSET"),
    /**
    * 重置规则
    */
    重置规则("EFFECTIVE_RULE"),
 // 一体化对应etliu start
//    /**
//    * 业绩基准规则
//    */
//    业绩基准规则("ACHIEVEMENT_BENCHMARK_RULE"),
	/**
	 * 业绩报酬计算规则
	 */
    业绩报酬计算规则("FLOAT_CALC_RULE"),
 // 一体化对应etliu end
    /**
    * 有效状态
    */
    有效状态("STATUS_VALID"),
	/**
     * 共同业务类型
     */
	共同业务类型("COMMON_BUSINESS_TYPE"),
	/**
	 * 交易对手类型
	 */
	交易对手类型 ("COUNTER_TYPE"),
	/**
	 * 资金账户类型
	 */
	资金账户类型("ACCT_TYPE_FG"),
	/**
	 * 计划一级分类
	 */
	计划一级分类("PLAN_ASSET_NET_TYPE"),
	/**
	 * 计划二级分类
	 */
	计划二级分类("PLAN_ASSET_NET_SEC_TYPE"),
	/**
	 * 现金流一级类型
	 */
	现金流一级类型("CASH_TYPE_1"),
	/**
	 * 现金流二级类型
	 */
	现金流二级类型("CASH_TYPE_2"),
	/**
	 * 现金流业务类型
	 */
	现金流业务类型("FIN_CASH_BUSINESS_TYPE"),
	/**
	 * 指令状态
	 */
	指令状态("COMMAND_STATUS"),
	/**
	 * 使用状态
	 */
	使用状态("USE_STATUS"),
	/**
	 *
	 */
	管理对象类型("MANAGE_OBJECT_TYPE"),
	/**
	 *
	 */
	金融产品交易业务类型("FINANCE_PRODUCT_BUSINESS_TYPE"),
	/**
     * 组合类型
     */
    组合类型("PORTFOLIO_TYPE"),
    /**
     * 发行状态
     */
    发行状态("ISSUE_STATUS"),
    /**
     * 平安业务类型
     */
    平安业务类型("PINAN_BUSINESS_TYPE"),
    /**
     * 付息周期
     */
    付息周期("INTPAY_PERIOD"),
    /**
     * 二级资产类别（中债）
     */
    计划类资产一级分类_中债("CHB_PLAN_ASSET_TYPE"),
    /**
     * 清算操作类型
     */
    清算操作类型("ACCOUNT_ACTION_TYPE"),
    /**
     * 画面数据状态
     */
    画面数据状态("PAGE_DATA_STATUS"),
    /**
     * 画面数据状态
     */
    交易流程状态("TRADE_FLOW_STATUS"),
    /**
     * 星期号
     */
    星期号("WEEK"),
    /**
     * 日期号
     */
    日期号("DAY_OF_MONTH"),
    /**
     * 工作日号
     */
    工作日号("WORKDAY_OF_MONTH"),
    /**
     * 交易平台
     */
    交易平台("SHARES_TRADE_PLATFORM"),
    /**
     * 收支方向
     */
    收支方向("IE_FLAG"),
    /**
     * 基数日期类型
     */
    基数日期类型("BASE_DATE_TYPE"),
 // 一体化对应etliu start
//    /**
//     * 划款类型
//     */
//    划款类型("PAY_TYPE"),
    /**
     * 平安划款类型
     */
    平安划款类型("PA_PAY_TYPE"),
 // 一体化对应etliu end
    /**
     * 利息分配方式
     */
    利息分配方式("INTPAY_RULE"),
    /**
     * 持仓类型
     */
    持仓类型("POSITION_TYPE"),
    /**
     * 资金投向地区
     */
    资金投向地区("SALEAREA_DECLARE"),
    /**
     * 产品管理模式
     */
    产品管理模式("PROD_MANAGE"),
    /**
     * 有无
     */
    有无("HAS_OR_NONE"),
    /**
     * 产品投资国家或地区
     */
    产品投资国家或地区("COUNTRY_AREA"),
    /**
     * 理财业务服务模式
     */
    理财业务服务模式("SERVICEMODE"),
    /**
     * 会计核算方式
     */
    会计核算方式("BOOKMODEL_DECLARE"),
    /**
     * 产品资产配置方式
     */
    产品资产配置方式("CMMODE"),
    /**
     * 产品定价方式
     */
    产品定价方式("PRODVALUETYPE"),
    /**
     * 投资资产类型
     */
    投资资产类型("INVESTTYPE_DECLARE"),
    /**
     * 行政区划_省
     */
    行政区划_省("ADMINISTRATIVE_TYPE1"),
    /**
     * 行政区划_市
     */
    行政区划_市("ADMINISTRATIVE_TYPE2"),
    /**
     * 投资风险偏好
     */
    投资风险偏好("RISKTYPE"),
    /**
     * 产品风险等级
     */
    产品风险等级("RISKLEVEL"),
    /**
     * 合作模式
     */
    合作模式("CONTMODE"),
    /**
     * 投资本金_收益到账日
     */
    投资本金_收益到账日("AMTDATETYPE"),
    /**
     * 产品增信机构类型
     */
    产品增信机构类型("CREDIT_TYPE"),
    /**
     * 产品增信形式
     */
    产品增信形式("CREDIT_MODE"),
    /**
     * 产品运作模式
     */
    产品运作模式("OPERATEMODE_DECLARE"),
    /**
     * 日期类型
     */
    日期类型("DATE_TYPE"),
    /**
     * 现金流单位
     */
    现金流单位("CASH_UNIT_TYPE"),
    /**
     *企业性质
     */
    企业性质("ENTERPRISE_PROPERTY"),
    /**
     *分红规则
     */
    分红规则("BONUS_RULE"),
    /**
     * 回购拆借方向
     */
    回购拆借方向("BACK_LEND_DIRECTION"),
    /**
     * 销售模式
     */
    销售模式("SALE_PATTERN"),
    /**
     * 角色岗位
     */
    角色岗位("ROLE_POST"),
    /**
     * 销售渠道
     */
    销售渠道("SALECHANNELCHB"),
    /**
     * 币种钞汇标志
     */
    币种钞汇标志("NOTE_EXCHANGE_FLAG"),
    /**
     * 开放状态
     */
    开放状态("OPEN_STATUS"),
    /**
     * 监督类型
     */
    监督类型("SUP_TYPE"),
    /**
     * 参数实现类型
     */
    参数实现类型("PARA_DISPLAY_TYPE"),
	 /**
     * 监督时分
     */
    监督时分("SUP_TIME_TYPE"),
	 /**
     * 指标类型
     */
	指标类型("IND_TYPE"),
	 /**
     * 监督频率
     */
	监督频率("SUP_FREQUENCY"),
	 /**
     * 运算符
     */
	运算符("SUP_OPERATOR"),
	 /**
     * 操作符
     */
	操作符("SUP_CONNECTOR"),
	 /**
     * 连接符
     */
	连接符("SUP_LINK_TYPE"),
	 /**
     * 单位
     */
	单位("SUP_UNIT"),
	 /**
     * 监督状态
     */
	监督状态("SUP_STAT"),
	 /**
     * 组合层次
     */
	组合层次("PORTFOLIO_LEVEL"),
	 /**
     * 分类维度
     */
	分类维度("CLASSIFY_DIMENSION"),
	/**
     * 证监会行业分类1
     */
	证监会行业分类1("CSRC_INDUSTRY_SORT_ONE"),
	/**
     * 证监会行业分类2
     */
    证监会行业分类2("CSRC_INDUSTRY_SORT_TWO"),
    /**
     * 自定义现金流类型
     */
    自定义现金流类型("CUSTOM_CASHFLOW_TYPE"),
    /**
     * 我行对应层级
     */
    我行对应层级("PARALLELISM_TIER"),
    /**
     * 受让资产性质
     */
    受让资产性质("TRANSFER_ASSET_NATUER"),
    /**
     * 业务品种
     */
    业务品种("BUSINESS_VARIETY"),
    /**
     * 批复方
     */
    批复方("REPLY_PARTY"),
    /**
     * 信托
     */
    计划二级分类_信托("TRUST_ASSET_SEC_TYPE"),
    /**
     * 股票类型
     */
    股票类型("SHARES_TYPE"),
    /**
     * 平安资产类型
     */
    平安资产类型("PINAN_ASSET_TYPE"),
    /**
     * 一次性费用类型
     */
    一次性费用类型("SINGLE_FEE_TYPE"),
    /**
     * 收/受益权类型
     */
    收受益权类型("CHB_PROFIT_TYPE"),
    /**
     *  重点监控行业和领域类别
     */
    重点监控行业和领域类别("CHB_MOINT_TYPE"),
    /**
     * 担保方式
     */
    担保方式("CHB_GUA_TYPE"),
    /**
     * 申万行业一级类型
     */
    申万行业一级类型("SW_INDUSTRY_ONE_TYPE"),
    /**
     * 业务表
     */
    业务表("BUSINESS_TABLE"),
    /**
     * 费用品种类型
     */
    费用品种类型("FEE_SPECIES_TYPE"),
    /**
     * 授权类型
     */
    授权类型("AUTHORIZE_TYPE"),
    /**
     * 托管流水借贷方向
     */
    托管流水借贷方向("CD_FLOW_FLAG"),
    /**
     * 匹配状态
     */
    匹配状态("MATCH_STATUS"),
    /**
     * 交易类型_CSTP
     */
    交易类型_CSTP("TRADE_TYPE_CSTP"),
    /**
     * 估值字段类型
     */
    估值字段类型("VAL_INDEX_TYPE"),
    /**
     * 估值字段
     */
    估值字段("VAL_INDEX"),
    /**
     * 资产附件类型
     */
    资产附件类型("ASSET_ATTACH_TYPE"),
    /**
     * 汇总科目
     */
    汇总科目("COLLECT_SUBJECT"),
    /**
     * 股票质押
     */
    股票质押("SHARES_PLEDGE"),
    /**
     * 回购期限品种
     */
    回购期限品种("TERM_TYPE_REBACK"),
    /**
     * 科目代码级别类型
     */
    科目代码级别类型("SUBJECT_RANK_TYPE"),
    /**
     * 账户来源
     */
    账户来源("ACCOUNT_SOURCE"),
    /**
     * 映射状态
     */
    映射状态("MAPPER_TYPE"),
    /**
     * 估值资产来源
     */
    估值资产来源("VAL_ASSET_SOURCE"),
    /**
     * 资产科目类别
     */
    资产科目类别("ASSET_SUBJECT_SORT"),
 // 一体化对应etliu start
	/**
     * 含权债摊销日
     */
    含权债摊销日("EXEC_DATE"),
	/**
     * 税类型
     */
    税类型("TAX_TYPE"),
    /**
     * 动作标志
     */
    动作标志("ACTION_STATUS"),
    /**
     * 频率
     */
    频率("FREQUENCY_UNIT"),
    /**
     * 估值舍入规则
     */
    估值舍入规则("VALUATION_ROUND_RULE"),
    /**
     * 计提类型
     */
    计提类型("DRAW_TYPE"),
    /**
     * 导入方式
     */
    导入方式("IMPORT_TYPE"),
    /**
     * 回购标的类型_中债
     */
    回购标的类型_中债("CHB_TARGET_BACK_LINK_SUBJECT"),
    /**
     * 业务类型
     */
    业务类型("BUSI_TYPE"),
    /**
     * 估值资金账户类型
     */
    估值资金账户类型("ACCT_TYPE"),
 // 一体化对应etliu end

    /**
     * 商业汇票类型_中债
     */
    商业汇票类型_中债("CHB_REP_AST_BILL_TYPE"),
    /**
     * 指标类型映射编码
     */
    指标类型映射编码("IND_TYPE_LINK_ENCODE"),
    /**
     * 指标状态
     */
    指标状态("INF_KRI_STATUS"),
    /**
     * wind交易场所
     */
    wind交易场所("WIND_MARKET"),
    /**
     * wind息票品种
     */
    wind息票品种("WIND_COUPON_SPECIES"),
    /**
     * wind利率类型
     */
    wind利率类型("WIND_INT_TYPE"),
    /**
     * wind计息基础
     */
    wind计息基础("WIND_BASIS"),
    /**
     * wind营业日准则
     */
    wind营业日准则("WIND_TDAY_RULE"),
    /**
     * wind推算方式
     */
    wind推算方式("WIND_CALC_METHOD"),
    /**
     * wind行权方式
     */
    wind行权方式("WIND_EXEC_MODE"),
    /**
     * 资产投资性质
     */
    资产投资性质("ASSET_INVEST_PROPORTY"),
	/**
     * 授信配置类型
     */
    授信配置类型("CREDIT_INFO_TYPE"),
	/**
     * 授信占用类型
     */
    授信占用类型("CREDIT_OCCURE_TYPE"),
	/**
     * 到期结算方式
     */
    到期结算方式("MA_SETTLE_METHOD"),
    /**
     * 是否占用授信
     */
    是否占用授信("ISCREDIT"),
    /**
     * 日志级别
     */
    日志级别("LOG_TYPE"),
   //sjj add 授信参数传值方式修改  start
    /**
     * 授信配置代码
     */
    授信配置代码("CREDIT_MST_ID"),
   //sjj add 授信参数传值方式修改  end
    /**
     * 划款方式
     */
    划款方式("PAY_MODE"),
    /**
     * 计费类型
     */
    计费类型("CHARGE_KIND"),
    /**
     * 计费基数
     */
    计费基数("CHARGE_BASIS"),
    /**
     * 池属性
     */
    池属性("ACCPOOL_PROP"),
    /**
     * 池类型
     */
    池类型("ACCPOOL_TYPE"),
    /**
     * 出入项类型
     */
    出入项类型("INOUT_ITEM"),
    /**
     * 出入类型
     */
    出入类型("INOUT_TYPE"),
    /**
     * 收取方式
     */
    收取方式("COLLECT_TYPE"),
    /**
     * 展示层级
     */
    展示层级("SHOW_LEVEL"),
    /**
     *
     */
    商业汇票类型("CHB_REP_AST_BILL_TYPE"),

    // etliu add 上农商版本添加 start
    /**
     * 目标客户一级分类
     */
    目标客户一级分类("TARGET_CUSTOMER_1"),
    /**
     * 目标客户二级分类
     */
    目标客户二级分类("TARGET_CUSTOMER_2"),
    /**
     * 销售类型
     */
    销售类型("SALETYPE"),
    /**
     * 产品系列
     */
    产品系列("PROD_SERIES"),
    /**
     * 产品特点
     */
    产品特点("PROD_FEATURE"),
    /**
     * 调整类型
     */
    调整类型("MDATEADJUSTTYPE"),
    /**
     * 计划类型
     */
    计划类型("SCHEDULE_TYPE"),
    /**
     * 计提方式
     */
    计提方式("ACCR_METHOD"),
    /**
     * 当期亏损处理  add by zhangweiqi 2019-01-10
     */
    当期亏损处理("DEAL_LOSS_MODE"),
    /**
     * 维护数据状态
     */
    维护数据状态("MAINTAIN_STATUS"),
    指令业务类型("ORDER_BUSINESS_TYPE"),
    // etliu add 上农商版本添加 end,
    净值日规则("CPRICE_RULE"),
    管理方式中债("CHB_MANATYPE_PLAN_ASSET"),
    开放模式中债("REPOPEN_MODE"),
    //-----add by 裴家俊 20190514------start----
    /**
     * 估值过滤器
     */
    估值过滤器("VALUATION_FILTER"),
    //-----add by 裴家俊 20190514------start----

    /**
     * 缴款方式
     */
    缴款方式("ETR_PAY_TYPE"),
    /**
     * 中标方式
     */
    中标方式("WIN_TYPE"),
    //add by 裴家俊 20190509 poc功能合并 end
    /**
     * 中标状态
     */
    中标状态("BID_STATUS"),

    确认状态("TENDER_CONFIRM_STATUS"),

    缴款状态("PAYMENT_STATUS"),
    回购交易类型("PRODTYPE"),
    /**
     * 情景管理 Added by JIAOXUJIN on 2019-08-26
     */
    情景管理("SCENE_MANAGE"),
    情景启用状态("SCENE_EFF_STATUS"),

    /**
     * 流动性资产 Added by wangkang on 2019-08-26
     */
    行内资产二级分类("CUSTOM_ASSET_TYPE_TWO"),
    行内资产三级分类("CUSTOM_ASSET_TYPE_THREE"),
    因子启停("FTR_BUSINESS_TYPE"),
    /**
     * 授信对象 Added by yufei on 2020-06-29
     */
    授信对象("CREDIT_OBJ"),
    结算方式("SETT_MODE"),
    平安资产一级分类("ASSET_REPORT_TYPE_1"),
  //zhangliang hefei 银行间上行 20200512 add start
    /**
     * 报价方式
     */
    报价方式("QUOTE_TYPE"),
    /**
     * 清算类型
     */
    清算类型("CLEAR_TYPE"),
    /**
     * 银行间上行本方角色
     */
    银行间上行本方角色("QUOTE_ROLE_TYPE"),
    /**
     * 银行间上行回复查询方式
     */
    银行间上行回复查询方式("REPLY_QUERY_TYPE_IBU"),
    /**
     * 银行间上行报价状态
     */
    银行间上行报价状态("QUOTE_STUTAS_IBU"),
    /**
     * 银行间上行发送状态
     */
    银行间上行发送状态("SEND_TYPE_IBU"),
    /**
     * 计算选择方式
     */
    计算选择方式("CALC_INTPUT_TYPE"),
    /**
     * 报价系统区分类型
     */
    报价系统区分类型("QUOTE_SYSMARK_IBU"),
    /**
     * 成交状态
     */
    银行间成交状态("DEAL_TYPE_IBU"),
    //zhangliang hefei 银行间上行 20200512 add end
    
  //add by xindi 2020.06.11 start
    请求类型("REQTYPE"),
    IN_OR_OUT("IOTYPE"),
    报文状态_RFT_IN("MSGTYPE_RFT_IN"),
    报文状态_RFQ_IN("MSGTYPE_RFQ_IN"),
    报文状态_RFT_OUT("MSGTYPE_RFT_OUT"),
    报文状态_RFQ_OUT("MSGTYPE_RFQ_OUT"),
    类型("INTERFREQTYPE"),
    状态("INTERFREQSTATUS"),
    报价状态("QUOTESTATUS"),
    //add by xindi 2020.06.11 end
  //add by chendong in 2020-07-07 start
    /**
     * 完成状态
     */
    完成状态("FINISH_STATUS"),
    //add by chendong in 2020-07-07 end

    // add by yeyang 20200714 start 托管户活期利率增加分段计息规则
    TD_LAT("TD_LAT"),
    INT_CALC_TYPE("INT_CALC_TYPE"),
    // add by yeyang 20200714 end
    //add by chendong in 2020-07-20 start
    交易日历("TRANSACTION_CALENDAR"),
    //add by chendong in 2020-07-20 end
    /**
     * 是否特殊委托
     */
    是否特殊委托("ISSPECIALENTRUST"),
    /**
     * 期限品种(利率互换)
     */
    期限品种_利率互换("TERM_TYPE_IRS"),
    /**
     * 互换方式
     */
    互换方式("IRS_TYPE"),
    /**
     * 组合估值方法
     */
    组合估值方法("PTL_VALUATION_METHOD"),
    报价方式_登记("QUOTE_STYLE"),
    分配方式("DISTRIBUTION"),

    ;
    /**
     * 对应于数据字典code
     */
    private String value;

    BussinessDictCode(String value) {
        this.value = value;
    }

    /**
     * @param value 数据字典值
     * @return DictCode
     * @see
     * @since 1.0
     */
    public static BussinessDictCode get(String value) {
    	BussinessDictCode[] values = BussinessDictCode.values();
        for (BussinessDictCode code : values) {
            if (StringUtil.equals(value, code.getValue())) {
                return code;
            }
        }
        return null;
    }

    /**
     * 获取 value
     *
     * @return value.
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 设置 value
     *
     * @param value value
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 通过value生成枚举
     *
     * @param value value值
     * @return DictCode
     */
    public static BussinessDictCode fromValue(String value) {
        for (BussinessDictCode dictCode : BussinessDictCode.values()) {
            if (dictCode.getValue().equals(value)) {
                return dictCode;
            }
        }
        return null;
    }

}
