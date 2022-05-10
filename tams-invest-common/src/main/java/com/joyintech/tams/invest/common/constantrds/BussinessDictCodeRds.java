/*
 * 文件名：BussinessDictCodeRds.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved. 描述：中信银行资管系统
 */
package com.joyintech.tams.invest.common.constantrds;

import com.joyintech.framework.util.StringUtil;

/**
 * 数据字典枚举
 *
 * @author 裴家俊
 * @version 1.0 2019年8月4日
 * @see BussinessDictCodeRds
 * @since 1.0
 */
public enum BussinessDictCodeRds implements com.joyintech.framework.base.BaseDictCode{
	/**
	 * REDIS模块
	 */
    REDIS模块("REDIS_MODEL"),
    /**
	 * 阶段类型
	 */
	阶段类型("STAGE_TYPE"),
	/**
	 * 控制类型
	 */
	控制类型("CONTROL_TYPE"),
	/**
	 * 触警操作
	 */
	触警操作("ALARM_OPER"),

    /**
     * 项目类一级分类
     */
    项目类一级分类("ASSET_TYPE_PROJECT"),
    /**
     * 提醒类型_大分类
     */
    提醒类型_大分类("REMINDER_TYPE_BIG"),
    /**
     * 提醒类型_小分类
     */
    提醒类型_小分类("REMINDER_TYPE_SMALL"),

    /**
    * 金融产品类型
    */
    金融产品类型("FINPROD_TYPE"),

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
     * 指标参数计算方式
     */
    指标参数计算方式("PARAM_MATH_TYPE"),

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
    * 登记备案机构
    */
    登记备案机构("REGIST_ORG_REC"),
    /**
    * 投资阶段
    */
    投资阶段("INVEST_STAGE"),
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
    募集方式("RAISE_TYPE"),
    /**
    * 投资性质
    */
    投资性质("INVEST_TYPE"),
    /**
    * 开放模式
    */
    开放模式("OPENMODE"),


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
     * 日期类型
     */
    日期类型("DATE_RISK_TYPE"),
    /**
     * 现金流单位
     */
    现金流单位("CASH_UNIT_TYPE"),
    /**
     *企业性质
     */
    企业性质("ENTERPRISE_PROPERTY"),
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
     * 导入方式
     */
    导入方式("IMPORT_TYPE"),
    /**
     * 业务类型
     */
    业务类型("BUSI_TYPE"),

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
     * 日志级别
     */
    日志级别("LOG_TYPE"),
    /**
     * 展示层级
     */
    展示层级("SHOW_LEVEL"),
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
     * 估值来源
     */
    估值来源("VALUATION_SOURCE"),
    /**
     * 风险事件运算关系对象
     */
    风险事件运算关系对象(
        "EVENT_REL_ITEM"
    ),
    /**
     * 指标状态
     */
    指标状态(
        "INF_KRI_STATUS"
    ),
    /**
     * 事件类型
     */
    事件类型(
        "INF_EVENT_TYPE"
    ),
    // added by wangzhen 20190822 start
    /**
     * 风险事件管理
  */
    风险事件管理("RSK_EVENT_MANAGEMENT"),
 	/**
 	 *  风险事件特征值类型
 	 */
 	风险事件特征值类型("RISK_EVENT_CHARA_TYPE"),
 	
 	/**
 	 *  特征值偏移单位
 	 */
 	特征值偏移单位("RISK_EVENT_CHARA_OFFSET_UNIT"),

    /**
     *  批机能类型
     */
    批机能类型("BATCH_FUNCTION"),
 	
 	// added by wangzhen 20190822 end





//-------------------------------------DICTCODE---------------------------------------------

    监管_现金及银行存款("CHB_DEPO_ASSET_TYPE"),
    监管_同业存单("CHB_INTET_BANK_DEPO"),
    监管_拆放同业及买入返售("CHB_DEMO_PEER_BUY"),
    监管_债券("CHB_WINDTYPEONE"),
    监管_理财直接融资工具("CHB_INVESTTYPE_DECLARE"),
    监管_新增可投资资产("CHB_ADD_INVEST_ASSET"),
    监管_非标准化债权类资产("CHB_ASSET_TYPE_PROJECT"),
    监管_权益类资产("CHB_EQUIT_ASSET"),
    监管_金融衍生品("CHB_STRUCT_PROD_LINK_SUBJECT"),
    监管_代客境外理财投资QDII("CHB_ASSET_TYPE_QDII"),
    监管_商品类资产("CHB_ASSET_TYPE_COMMOD"),
    监管_另类资产("CHB_ASSET_TYPE_OTHER"),
    监管_公募基金("CHB_FUND_ASSET_SECOND_TYPE"),
    监管_私募基金("CHB_FUND_ASSET_PRIVATE_TYPE"),
    监管_资产管理产品("CHB_PLAN_ASSET_TYPE"),
    监管_委外投资_协议方式("CHB_INVEST_PROTOCOL"),
    监管_同业拆入及卖出回购("CHB_INTER_BANK_REPO"),
    监管_其他_负债("CHB_OTHER_DEBT"),
    监管_其他货币市场工具("CHB_OTHER_MARKET_TOOL"),
    监管_其他产业投资基金("CHB_OTHER_INDUS_INVEST_FUND"),
    /**
     * 监管一级分类
     */
    中债_监管一级分类("SUPERVISE_ASSET_TYPE"),
    /**
     * 资管_存款类资产
     */
    资管_存款类资产("CLASS_MANAGE_DEPO_ASSET"),
    /**
     * 资管_债券类资产
     */
    资管_债券类资产("CLASS_MANAGE_BOND_ASSET"),
    /**
     * 资管_项目类资产
     */
    资管_项目类资产("CLASS_MANAGE_PROJECT_ASSET"),
    /**
     * 资管_权益类资产
     */
    资管_权益类资产("CLASS_MANAGE_EQUIT_ASSET"),
    /**
     * 资管_基金类资产
     */
    资管_基金类资产("CLASS_MANAGE_FUND_ASSET"),
    /**
     * 资管_计划类资产
     */
    资管_计划类资产("CLASS_MANAGE_PLAN_ASSET"),
    /**
     * 资管_衍生品交易
     */
    资管_衍生品交易("CLASS_MANAGE_DERI_ASSET"),
    /**
     * 资管_质押式回购
     */
    资管_质押式回购("CLASS_MANAGE_REPU_ASSET"),
    /**
     * 资管一级分类
     */
    资管一级分类("CLASS_MANAGE_FIRST_TYPE"),
    /**
     * 债券类业务
     */
    授权资产_债券类业务("AUTH_BOND_BUSS"),
    /**
     * 权益类资产(授权)
     */
    授权资产_权益类资产("AUTH_EQUIT_ASSET"),
    /**
     * 基金专户及资产管理计划
     */
    授权资产_基金专户及资产管理计划("AUTH_FUND_MANAGE_PLAN"),
    /**
     * 公募证券投资基金
     */
    授权资产_公募证券投资基金("AUTH_INVEST_FUND"),
    /**
     * 货币市场类业务
     */
    授权资产_货币市场类业务("AUTH_MARKET_BUSS"),
    /**
     * 授权资产一级分类
     */
    授权资产一级分类("AUTH_ASSET_FIRST_TYPE"),
    /**
     * 参数模板
     */
    参数模板("PARA_TEMPLATE"),
    /**
     * 复合模板取值类型
     */
    复合模板取值类型("CPL_MODEL_VAL_TYPE"),
    /**
     * 所属行业分类
     */
    所属行业分类("INDUSTRY_TYPE"),
    /**
     * 日期范围类型
     *
     */
    日期范围类型("DATE_RANGE_TYPE"),
    /**
     * 评级分类
     */
    评级分类("GRADE_TYPE"),
    /**
     * 资产类型分类
     */
    资产类型分类("ASSET_TYPE_SORT"),
    /**
     * 存储识别域
     */
    存储识别域("STORAGE_DISTIN"),
    /**
     * 参数粒度
     */
    参数粒度("PARA_GRAIN"),

    /**
     * 是否有锁定期
     */
    是否有锁定期("WITH_LOCK_PERIOD"),
    /**
     * 行情类型_利率
     */
    行情类型_利率("RATE_TYPE"),
    /**
     * 重算方式
     */
    重算方式("RECAL_TYPE"),
    /**
     * 分录类型
     */
    分录类型("RECORD_TYPE"),
    /**
     * 赎回确认速度
     */
    赎回确认速度("REDEM_SPEED"),
    /**
     * 正回购核算分类
     */
    正回购核算分类("REPON_PO_ACCOUNTING_CLASSIFICATION"),
    /**
     * 逆回购核算分类
     */
    逆回购核算分类("REPON_RE_ACCOUNTING_CLASSIFICATION"),
    /**
     * 报表维度
     */
    报表维度("REPORT_DIMEN"),
    /**
     * 报表期限类型
     */
    报表期限类型("REPORT_TERM_TYPE"),
    /**
     * 回购费用处理方式
     */
    回购费用处理方式("REP_FEE_EXE_TYPE"),
    /**
     * 估值模板
     */
    估值模板("SCHEME_MODE"),
    /**
     * 配置方案类型
     */
    配置方案类型("SCHEME_TYPE"),
    /**
     * 税务参数类型
     */
    税务参数类型("SCHEME_TYPE2"),
    /**
     * 证券细分类
     */
    证券细分类("SECURITY_DETAIL_TYPE"),
    /**
     * 债券交易费用损益计入方式
     */
    债券交易费用损益计入方式("SEC_DEAL_FEE_SUBJECT"),
    /**
     * 证券品种
     */
    证券品种("SEC_TYPE"),
    /**
     * 结算方式(债券借贷)
     */
    结算方式_债券借贷("SETTLEMENT_WAY_LENDING"),
    /**
     * 交易所回购计提天数方式
     */
    交易所回购计提天数方式("SE_REP_DAY_TYPE"),
    /**
     * 计提区分
     */
    计提区分("ACCURE_TYPE"),
    /**
     * 银行账场景代码
     */
    银行账场景代码("BANK_TABLE_ID"),
    /**
     * 一键做账模块分类
     */
    一键做账模块分类("UNITFYCACCOUNTANT_MODEL_CODE"),
    /**
     * 回补方式
     */
    回补方式("ANAPLEROTIC_TYPE"),
    /**
     * 回补规则
     */
    回补规则("ANAPLEROTIC_RULE"),
    /**
     * 通道类型
     */
    通道类型("CHANNEL_TYPE"),
    /**
     * 池属性
     */
    池属性("ACCPOOL_PROP"),
    /**
     * 池类型
     */
    池类型("ACCPOOL_TYPE"),
    /**
     * 授信模式
     */
    授信模式("CE_MODEL"),
    /**
     * 中债报送数据类型
     */
    中债报送数据类型("CHB_DATA_TYPE"),
    /**
     * 确认类型
     */
    确认类型("CONFIRM_TYPE"),
    /**
     * 净值日规则
     */
    净值日规则("CPRICE_RULE"),
    /**
     * 币种（中债）
     */
    币种_中债("CURRENCY_CHB"),
    /**
     * 披露确认状态
     */
    披露确认状态("DIS_CONFIRM_STATUS"),
    /**
     * 日期类型(信批)
     */
    日期类型_信批("DIS_DATE_TYPE"),
    /**
     * 披露文件类型
     */
    披露文件类型("DIS_FILE_TYPE"),
    /**
     * 权益人类别
     */
    权益人类别("EQUITY_HOLDER_TYPE"),
    /**
     * 执行结果
     */
    执行结果("EXEC_RESULT"),

    /**
     * 销售类型
     */
    销售类型("SALETYPE"),
    /**
     * 执行状态
     */
    执行状态("EXEC_STATUS"),
    /**
     * 投资者类别
     */
    投资者类别("HOLDER_TYPE"),
    /**
     * 持有信息数据来源
     */
    持有信息数据来源("HOLD_INFO_SOURCE"),
    /**
     * 核算分类
     */
    核算分类("HS_TYPE"),
    /**
     * 出入项类型
     */
    出入项类型("INOUT_ITEM"),
    /**
     * 池操作类型
     */
    池操作类型("INOUT_TYPE"),
    /**
     * 机构证件类别
     */
    机构证件类别("INSTITUTION_IDENTIFY_TYPE"),
    /**
     * 维护数据状态
     */
    维护数据状态("MAINTAIN_STATUS"),
    /**
     * 基准区分
     */
    基准区分("MAKE_TIME_TYPE"),
    /**
     * 月报投资者类别
     */
    月报投资者类别("MONTH_HOLDER_TYPE"),
    /**
     * 指令业务类型
     */
    指令业务类型("ORDER_BUSINESS_TYPE"),
    /**
     * 产品品种
     */
    产品品种("PBC_PROD_VARIETY"),
    /**
     * 人行报表名称
     */
    人行报表名称("PBC_REPORT_TYPE"),
    /**
     * 个人证件类别
     */
    个人证件类别("PERSONAL_IDENTIFY_TYPE"),
    /**
     * 产品风险等级(中债)
     */
    产品风险等级_中债("PRODUCT_RISK_GRADE"),
    /**
     * 复核状态
     */
    复核状态("RECHECK_STATUS"),
    /**
     * 报表名称(CODE)
     */
    报表名称("SVR_REPORT_CODE"),
    /**
     * 报告数据类型
     */
    报告数据类型("REPORT_DATA_TYPE"),
    /**
     * 报表数据状态
     */
    报表数据状态("REPORT_STATUS"),
    /**
     * 报表类别
     */
    报表类别("REPORT_STYLE"),
    /**
     * 报表启用状态
     */
    报表启用状态("REPORT_TYPE"),
    /**
     * 披露渠道
     */
    披露渠道("REVEAL_CHANNEL"),
    /**
     * 披露频率
     */
    披露频率("REVEAL_FREQUENCY"),
    /**
     * 报送数据状态
     */
    报送数据状态("SUBMITTED_STATUS"),
    /**
     * 报送反馈状态
     */
    报送反馈状态("SUBMIT_FEEDBACK_STATUS"),
    /**
     * 推送方式
     */
    推送方式("SUBMIT_REQUIRE2"),
    /**
     * 报送时点
     */
    报送时点("SUBMIT_REQUIRE3"),
    /**
     * 报送结果
     */
    报送结果("SUBMIT_RESULT"),
    /**
     * 报送状态
     */
    报送状态("SUBMIT_STATUS"),
    /**
     * 报送时点类型
     */
    报送时点类型("SUBMIT_TIMETYPE"),
    /**
     * 投资本金_收益_到账日
     */
    投资本金_收益_到账日_中债("SVR_AMTDATETYPE"),
    /**
     * 资产/负债类别
     */
    资产_负债类别("SVR_ASSETDEBT_TYPE"),
    /**
     * 票据类信用证类资产类型
     */
    票据类信用证类资产类型("SVR_ASSET_TYPE_BILL_LETTERCREDIT"),
    /**
     * 资产/负债二级类别
     */
    资产_负债二级类别("SVR_ASSET_TYPE_SECOND_LEVEL"),
    /**
     * 行内资产类别
     */
    行内资产类别("SVR_BANK_IN_ASSET_TYPE"),
    /**
     * 计息基础(中债)
     */
    计息基础_中债("SVR_BASIS"),
    /**
     * 基准利率
     */
    基准利率("SVR_BASIS_TYPE"),
    /**
     * 会计核算方式(中债)
     */
    会计核算方式_中债("SVR_BOOKMODEL_DECLARE"),
    /**
     * 业务种类
     */
    业务种类("SVR_BUSINESS_TYPE"),
    /**
     * 购买结构
     */
    购买结构("SVR_BUY_STRUCT"),
    /**
     * 变动类型
     */
    变动类型("SVR_CHANG_TYPE"),
    /**
     * 检查结果
     */
    检查结果("SVR_CHECK_RESULT"),
    /**
     * 产品资产配置方式(中债)
     */
    产品资产配置方式_中债("SVR_CMMODE"),
    /**
     * 合作模式
     */
    合作模式("SVR_CONTMODE"),
    /**
     * 所属国家或地区
     */
    所属国家或地区("SVR_COUNTRY_AREA"),
    /**
     * 产品增信形式
     */
    产品增信形式("SVR_CREDIT_MODE"),
    /**
     * 产品增信机构类型
     */
    产品增信机构类型("SVR_CREDIT_TYPE"),
    /**
     * 产品存续期登记交易类型
     */
    产品存续期登记交易类型("SVR_DEAL_TYPE"),
    /**
     * 存续状态
     */
    存续状态("SVR_DURATIONFLAG"),
    /**
     * 费用二级类型
     */
    费用二级类型("SVR_FEE_SEC_TYPE"),
    /**
     * 费用类型
     */
    费用类型("SVR_FEE_TYPE"),

    /**
     * 政府投资基金投向
     */
    政府投资基金投向("SVR_GOV_INVEST_DIRECT"),
    /**
     * 结算方式
     */
    结算方式("SETT_MODE"),
    /**
     * 科目级别
     */
    科目级别("SUBJECT_LEVEL"),
    /**
     * 科目名称
     */
    科目名称("SUBJECT_NAME"),
    /**
     * 科目类型
     */
    科目类型("SUBJECT_TYPE"),
    /**
     * 汇总维度
     */
    汇总维度("SUM_DIMEN"),
    /**
     * 会计场景代码
     */
    会计场景代码("TABLE_ID"),
    /**
     * 跑批规则
     */
    跑批规则("TASK_EXE_RULE"),
    /**
     * 计税方式
     */
    计税方式("TAXABLE_SIGN"),
    /**
     * 增值税资产分类
     */
    增值税资产分类("TAX_ASSET_TYPE"),
    /**
     * 增值税_占比比例
     */
    增值税_占比比例("TAX_CALC_PROPORTION"),
    /**
     * 增值税_计算方式
     */
    增值税_计算方式("TAX_CALC_TYPE"),
    /**
     * 免税收入类型
     */
    免税收入类型("TAX_FREE_TYPE"),
    /**
     * 增项_减项
     */
    增项_减项("TAX_IN_OUT"),
    /**
     * 增值税_确认类型
     */
    增值税_确认类型("TAX_P_TYPE"),
    /**
     * 税类型
     */
    税类型("TAX_TYPE"),
    /**
     * 期限品种(回购)
     */
    期限品种_回购("TERM_TYPE_COLLATERALISED_REPO"),
    /**
     * 期限品种(内部拆借)
     */
    期限品种_内部拆借("TERM_TYPE_RESOLUTE"),
    /**
     * 交易场所类别
     */
    交易场所类别("TRADE_PLACE_TYPE"),
    /**
     * 交易日历
     */
    交易日历("TRANSACTION_CALENDAR"),
    /**
     * 数据移植转换模板类型
     */
    数据移植转换模板类型("TRANSPLANT_FORMATION_MODEL_TYPE"),
    /**
     * 估值日期
     */
    估值日期("VALUATION_DATE"),
    /**
     * 估值投资目的
     */
    估值投资目的("VALUATION_INVESTMENT_PURPOSE"),
    /**
     * 估值方法
     */
    估值方法("VALUATION_METHOD"),
    /**
     * 估值市价
     */
    估值市价("VALUATION_PRICE"),
    /**
     * 估值舍入规则
     */
    估值舍入规则("VALUATION_ROUND_RULE"),
    /**
     * 估值表模板
     */
    估值表模板("VALUATION_TEMPLATE"),
    /**
     * 估值交易市场
     */
    估值交易市场("VALUATION_TRADE_MARKET"),
    /**
     * 估值核算业务类型
     */
    估值核算业务类型("VAL_BUSINESS_TYPE"),
    /**
     * 估值表类型
     */
    估值表类型("VAL_TABLE_TYPE"),
    /**
     * 增值税类型2
     */
    增值税类型2("VAT_TYPE2"),
    /**
     * 结清状态
     */
    结清状态("CLEAN_STATUS"),
    /**
     * 计费类型
     */
    计费类型("CHARGE_KIND"),
    /**
     * 计费基数
     */
    计费基数("CHARGE_BASIS"),
    /**
     * 净值数据延迟天数
     */
    净值数据延迟天数("INT_ACCRUAL_MODE"),
    /**
     * 应收款项类别
     */
    应收款项类别("RECEIVABLES_TYPE"),
    /**
     * 委托关闭方式
     */
    委托关闭方式("ENTRUST_CLOSE_TYPE"),
    /**
     * 委托操作类型
     */
    委托操作类型("ENTRUST_OPERATE_TYPE"),
    /**
     * 提前观察单位(延后时间单位)
     */
    提前观察单位_延后时间单位("ADVANCE_OBSERV_UNIT"),
    /**
     * 附加税类型
     */
    附加税类型("ALT_TYPE"),
    /**
     * 基数类型
     */
    基数类型("BASE_TYPE"),
    /**
     * 基准类型_2
     */
    基准类型_2("BENCHMARK_TYPE"),
    /**
     * 计费基数(费用品种)
     */
    计费基数_费用品种("BILL_BASE_FEE_KIND"),
    /**
     * 分红规则
     */
    分红规则("BONUS_RULE"),
    /**
     * 分红方案
     */
    分红方案("BONU_SCALC_FLAG"),
    /**
     * 推算方法
     */
    推算方法("CALC_METHOD"),
    /**
     * 推算方式
     */
    推算方式("CALC_RULE"),
    /**
     * 上市板
     */
    上市板("BOARD"),
    /**
     * 收费类型
     */
    收费类型("CHARGE_TYPE"),
    /**
     * 客户等级
     */
    客户等级("CLIENT_RANK"),
    /**
     * 指令处理状态
     */
    指令处理状态("COMMAND_DEAL_STATUS"),
    /**
     * 关联范围
     */
    关联范围("CORRELATION_RANGE"),
    /**
     * 授信策略
     */
    授信策略("CREDIT_POLICY"),
    /**
     * 货币对
     */
    货币对("CYY_PAIR"),
    /**
     * 处理状态
     */
    处理状态("DO_STATUS"),

    /**
     * 重置规则
     */
    重置规则("EFFECTIVE_RULE"),
    /**
     * 行权方式
     */
    行权方式("EXEC_MODE"),
    /**
     * 行权状态
     */
    行权状态("EXEC_TYPE"),
    /**
     * 系统资产大类
     */
    系统资产大类("TYPE_6"),
    /**
     * 有无
     */
    有无("HAS_OR_NONE"),
    /**
     * 付息类型
     */
    付息类型("INTEREST_TYPE"),
    /**
     * 利息分配方式
     */
    利息分配方式("INTPAY_RULE"),
    /**
     * 投资目的
     */
    投资目的("INV_AIM"),
    /**
     * 尾数处理方式
     */
    尾数处理方式("MANTISSA_PROCESS_MODE"),
    /**
     * 结息类型
     */
    结息类型("NODAL_TYPE"),
    /**
     * 指令类型
     */
    指令类型("ORDER_TYPE"),
    /**
     * 参与方费率品种
     */
    参与方费率品种("PARTICIPATE_FEE_TYPE"),
    /**
     * 平安划款类型
     */
    平安划款类型("PA_PAY_TYPE"),
    /**
     * 持仓类型
     */
    持仓类型("POSITION_TYPE"),
    /**
     * 批复方
     */
    批复方("REPLY_PARTY"),
    /**
     * 重置类型(基准利率调整规则)
     */
    重置类型_基准利率调整规则("RESET_TYPE"),
    /**
     * 销售部门
     */
    销售部门("SALE_DEPT"),
    /**
     * 一次性费用类型_投管
     */
    一次性费用类型_投管("SINGLE_FEE_TYPE"),
    /**
     * 回购方向
     */
    回购方向("TARGET_DIRECTION"),
    /**
     * 营业日准则
     */
    营业日准则("TDAY_RULE"),
    /**
     * 计划二级分类(信托)
     */
    计划二级分类_信托("TRUST_ASSET_SEC_TYPE"),
    /**
     * 增值税类型
     */
    增值税类型("VAT_TYPE"),
    /**
     * 计息基础_WIND
     */
    计息基础_WIND("WIND_BASIS"),
    /**
     * 推算方式_WIND
     */
    推算方式_WIND("WIND_CALC_METHOD"),
    /**
     * 息票品种_WIND
     */
    息票品种_WIND("WIND_COUPON_SPECIES"),
    /**
     * 行权方式_WIND
     */
    行权方式_WIND("WIND_EXEC_MODE"),
    /**
     * 利率类型_WIND
     */
    利率类型_WIND("WIND_INT_TYPE"),
    /**
     * 交易场所_WIND
     */
    交易场所_WIND("WIND_MARKET"),
    /**
     * 营业日准则_WIND
     */
    营业日准则_WIND("WIND_TDAY_RULE"),
    /**
     * 委托状态
     */
    委托状态("ENTRUST_STATUS"),
    /**
     * 投资本金(收益)到账日
     */
    投资本金_收益_到账日("AMTDATETYPE"),
    /**
     * 资产状态
     */
    资产状态("ASSET_STATUS"),
    /**
     * 会计核算方式
     */
    会计核算方式("BOOKMODEL_DECLARE"),
    /**
     * 产品资产配置方式
     */
    产品资产配置方式("CMMODE"),
    /**
     * 增信标识
     */
    增信标识("CREDIT_FLAG"),
    /**
     * 文件类型_投管
     */
    文件类型_投管("FILE_TYPE_FG"),
    /**
     * 产品定价方式
     */
    产品定价方式("PRODVALUETYPE"),
    /**
     * 产品管理模式
     */
    产品管理模式("PROD_MANAGE"),
    /**
     * OPICS参数类型
     */
    OPICS参数类型("OPICS_PARA_YPE"),
    /**
     * 项目类一级分类(中债)
     */
    项目类一级分类_中债("CHB_ASSET_TYPE_PROJECT"),
    /**
     * 基准利率种类(中债)
     */
    基准利率种类_中债("CHB_BASE_RATE_SORT"),
    /**
     * 主体评级(中债)
     */
    主体评级_中债("CHB_BODY_GRADE"),
    /**
     * 存款类一级分类(中债)
     */
    存款类一级分类_中债("CHB_DEPO_ASSET_TYPE"),
    /**
     * 基金类二级资产类别(中债)
     */
    基金类二级资产类别_中债("CHB_FUND_ASSET_SECOND_TYPE"),
    /**
     * 担保方式(中债)
     */
    担保方式_中债("CHB_GUA_TYPE"),
    /**
     * 发行方式(中债)
     */
    发行方式_中债("CHB_ISSUE_TYPE"),
    /**
     * 委外协议(中债)
     */
    委外协议_中债("CHB_OUTSOURCE_AGREEMENT"),
    /**
     * 委外投资属性_中债(中债)
     */
    委外投资属性_中债("CHB_OUTSOURCE_INVEST_PROPERTY"),
    /**
     * 结构性存款挂钩标的类别(中债)
     */
    结构性存款挂钩标的类别_中债("CHB_STRUCT_PROD_LINK_SUBJECT"),
    /**
     * WIND一级分类（含债券中债）
     */
    WIND一级分类_含债券中债("CHB_WINDTYPEONE"),
    /**
     * 是否(中债)
     */
    是否_中债("SVR_YES_OR_NO"),
    /**
     * 资产科目类型
     */
    资产科目类型("ASSET_SUBJECT_SORT"),
    /**
     * 汇总科目
     */
    汇总科目("COLLECT_SUBJECT"),
    /**
     * 生效日期类型
     */
    生效日期类型("EFFECTIVE_DATE_TYPE"),
    /**
     * 出入库方向
     */
    出入库方向("LIBRARY_DIRECTION"),
    /**
     * 映射类型
     */
    映射类型("MAPPER_TYPE"),
    /**
     * 计算模式
     */
    计算模式("CALC_MODE"),
    /**
     * 股票质押
     */
    股票质押("SHARES_PLEDGE"),
    /**
     * 条件项
     */
    条件项("CONDITION_ITEM"),

    /**
     * 监控方向
     */
    监控方向("SUP_DIRECTION"),
    /**
     * 评级变动
     */
    评级变动("CHANGE_DIRECTION"),
    /**
     * 评级展望
     */
    评级展望("RATING_OUTLOOK"),
    /**
     * 估值资产来源
     */
    估值资产来源("VAL_ASSET_SOURCE"),
    /**
     * 行政区划_县
     */
    行政区划_县("ADMINISTRATIVE_TYPE3"),
    /**
     * 批处理启动模式
     */
    批处理启动模式("BATCH_START_MODE"),
    /**
     * 批处理执行状态
     */
    批处理执行状态("BATCH_STATUS"),
    /**
     * 共通业务类型
     */
    共通业务类型("COMMON_BUSINESS_TYPE"),
    /**
     * 监管一级分类
     */
    监管一级分类("SUPERVISE_ASSET_TYPE"),
    /**
     * 数据库类型
     */
    数据库类型("DATABASE_TYPE"),
    /**
     * 数据类型
     */
    数据类型("DATA_TYPE"),
    /**
     * 小数显示类型
     */
    小数显示类型("DECIMAL_SHOW_TYPE"),
    /**
     * 机构类型
     */
    机构类型("DEPTTYPE"),
    /**
     * 内部职能
     */
    内部职能("DEPT_FUN"),
    /**
     * 要素类型
     */
    要素类型("ELEMENT_TYPE"),
    /**
     * 层级信息
     */
    层级信息("HIERARCHY_INFO"),
    /**
     * 分享类型（主页配置）
     */
    分享类型_主页配置("HP_SHARE_TYPE"),
    /**
     * 金额显示类型
     */
    金额显示类型("MONEY_SHOW_TYPE"),
    /**
     * 报表面板展示方式
     */
    报表面板展示方式("PANEL_SHOW_TYPE"),
    /**
     * 流程状态
     */
    流程状态("PROCESS_STATE"),
    /**
     * 自定义报表_查询参数类型
     */
    自定义报表_查询参数类型("REPORT_PARA_TYPE"),
    /**
     * 报表展示方式
     */
    报表展示方式("REPORT_SHOW_TYPE"),
    /**
     * 数据权限类型
     */
    数据权限类型("ROLE_DATA_TYPE"),
    /**
     * 数据状态
     */
    数据状态("STATUS"),
    /**
     * 子系统代码
     */
    子系统代码("SUBSYSTEM_CODE"),
    /**
     * 展示样式
     */
    展示样式("USER_STYLE_TYPE"),
    /**
     * 是否
     */
    是或否("YES_NO"),
    /**
     * 动作标志
     */
    动作标志("ACTION_STATUS"),
    /**
     * 生效规则
     */
    生效规则("ACT_RULE"),
    /**
     * 申购确认速度
     */
    申购确认速度("APPLY_SPEED"),
    /**
     * 债券类型
     */
    债券类型("BOND_TYPE"),
    /**
     * 分红频率
     */
    分红频率("BONUS_CYCLE"),
    /**
     * 分红尾差处理方式
     */
    分红尾差处理方式("BONUS_TAIL_MODE"),
    /**
     * 账套类别
     */
    账套类别("BOOKSET_TYPE"),
    /**
     * 凭证类型
     */
    凭证类型("BOOK_TYPE"),
    /**
     * 生成状态
     */
    生成状态("BUILD_STATUS"),
    /**
     * 业务类别
     */
    业务类别("BUSINESS_TYPE"),
    /**
     * 计算类型
     */
    计算类型("CALC_TYPE"),
    /**
     * 损益结转频率
     */
    损益结转频率("CARRYOVER_CYCLE"),
    /**
     * 借贷方向
     */
    借贷方向("CD_FLAG"),
    /**
     * 借贷方向_表外
     */
    借贷方向_表外("CD_FLAG_OFF"),
    /**
     * 对账结果
     */
    对账结果("CHECK_RESULT"),
    /**
     * 对账状态
     */
    对账状态("CHECK_STATUS"),
    /**
     * 对比类型
     */
    对比类型("COMPARE_TYPE"),
    /**
     * 确认状态
     */
    确认状态("CONFIRM_STATUS"),
    /**
     * 复制范围
     */
    复制范围("COPY_RANGE"),
    /**
     * 存款核算分类
     */
    存款核算分类("CURRENT_ACCOUNTING_CLASSIFICATION"),
    /**
     * 数据移植模版类型
     */
    数据移植模版类型("DATA_TRANSPLANT_MODEL_TYPE"),
    /**
     * 日期维度
     */
    日期维度("DATE_DIMENSION"),
    /**
     * 申报日期
     */
    申报日期("DECLAR_DATE"),
    /**
     * 存款类资产类型(GZ)
     */
    存款类资产类型_GZ("DEPOSIT_TYPE"),
    /**
     * 明细区分
     */
    明细区分("DETAIL_DIST"),
    /**
     * 元素数据类型
     */
    元素数据类型("DICT_TYPE"),
    /**
     * 计提频率类型
     */
    计提频率类型("DRAW_CYCLE_TYPE"),
    /**
     * 计提类型
     */
    计提类型("DRAW_TYPE"),
    /**
     * 含权债摊销日
     */
    含权债摊销日("EXEC_DATE"),
    /**
     * 业绩报酬计算规则
     */
    业绩报酬计算规则("FLOAT_CALC_RULE"),
    /**
     * 频率
     */
    频率("FREQUENCY_UNIT"),
    /**
     * 基金核算类型
     */
    基金核算类型("FUND_ACCOUNTING_TYPE"),
    /**
     * 基金分红频率
     */
    基金分红频率("FUND_CARRYOVER_CYCLE"),
    /**
     * 基金类型
     */
    基金类型("FUND_TYPE"),
    /**
     * 公允价值变动计提日类型
     */
    公允价值变动计提日类型("FV_CALCDATE_TYPE"),
    /**
     * 分组代码
     */
    分组代码("GROUP_ID"),
    /**
     * 半年
     */
    半年("HALF_YEAR"),
    /**
     * 节假日_交易市场
     */
    节假日_交易市场("HOLIDAY_TRADE_MARKET"),
    /**
     * 付息分红规则
     */
    付息分红规则("INTEREST_ROLE"),
    /**
     * 重置类型
     */
    重置类型("INTRESET_TYPE"),
    /**
     * 利率结构
     */
    利率结构("INT_RATE_STRUCTURE"),
    /**
     * 投资目的获取方式
     */
    投资目的获取方式("INVESTMENT_PURPOSE_ACCESS"),
    /**
     * 行内行外
     */
    行内行外("IN_OUT_BANK"),
    /**
     * 是否节假日
     */
    是否节假日("IS_HOLIDAY"),
    /**
     * 上期维度
     */
    上期维度("LAST_DIMEN"),
    /**
     * 拆借资产类型
     */
    拆借资产类型("LENDING_TYPE"),
    /**
     * 拆借核算分类
     */
    拆借核算分类("LEND_ACCOUNTING_CLASSIFICATION"),
    /**
     * 亏损处理方式
     */
    亏损处理方式("LOSS_HANDLE_MODE"),
    /**
     * 维护方式
     */
    维护方式("MAINTAIN_MODE"),
    /**
     * 匹配类型
     */
    匹配类型("MATCH_TYPE"),
    /**
     * 本系统估值表列指标
     */
    本系统估值表列指标("MY_GZB_COL_INDEX"),
    /**
     * 本系统余额表列指标
     */
    本系统余额表列指标("MY_YEB_COL_INDEX"),
    /**
     * 下发状态
     */
    下发状态("NOTICE_STATUS"),
    /**
     * 观察频率单位
     */
    观察频率单位("OBS_CYCLE_UNIT"),
    /**
     * 冲正标识
     */
    冲正标识("OFFSET_FLAG"),
    /**
     * 冲减应计利息方式
     */
    冲减应计利息方式("OFFSET_INTAMT_MODE"),
    /**
     * 机构核算分类
     */
    机构核算分类("ORG_VALCLASS"),
    /**
     * 参数值类型
     */
    参数值类型("PARAM_VALUE_TYPE"),
    /**
     * 支付方式
     */
    支付方式("PAY_TYPE"),
    /**
     * 计划类资产类型
     */
    计划类资产类型("PLANNED_TYPE"),
    /**
     * 收益标识
     */
    收益标识("PRD_INCOME_TYPE"),
    /**
     * 产品维度
     */
    产品维度("PRODUCT_DIMENSION"),
    /**
     * 收付方向
     */
    收付方向("PS_PAYMENT"),
    /**
     * 季度
     */
    季度("QUARTER"),
    /**
     * 层级类别
     */
    层级类别("SVR_GRADE_TYPE"),
    /**
     * 有无(中债)
     */
    有无_中债("SVR_HAVE_OR_NOT"),
    /**
     * 投资品种
     */
    投资品种("SVR_INVEST_TRADE_TYPE"),
    /**
     * 投资类型
     */
    投资类型("SVR_INVEST_TRADE_TYPE_S"),
    /**
     * 境内托管机构名称
     */
    境内托管机构名称("SVR_IN_DEP_NAME"),
    /**
     * 债券类资产发行方式
     */
    债券类资产发行方式("SVR_ISSUE_MODE_FUND"),
    /**
     * 资产评级
     */
    资产评级("SVR_MAIN_GRADE"),
    /**
     * 资产评级_短期
     */
    资产评级_短期("SVR_MAIN_GRADE_D"),
    /**
     * 管理方式
     */
    管理方式("SVR_MANAGE_STYLE"),
    /**
     * 开放模式(中债)
     */
    开放模式_中债("SVR_OPEN_MODE"),
    /**
     * 产品运作模式
     */
    产品运作模式("SVR_OPERATEMODE_DECLARE"),
    /**
     * 处理状态(中债)
     */
    处理状态_中债("SVR_OPERATE_STATUS"),
    /**
     * 发行机构类型_按规模划分
     */
    发行机构类型_按规模划分("SVR_PARTY_TYPE1"),
    /**
     * 发行机构类型_按经济类型划分
     */
    发行机构类型_按经济类型划分("SVR_PARTY_TYPE2"),
    /**
     * 发行机构类型_按技术领域划分
     */
    发行机构类型_按技术领域划分("SVR_PARTY_TYPE3"),
    /**
     * 产品期限
     */
    产品期限("SVR_PRODUCT_TERM"),
    /**
     * 定价方式
     */
    定价方式("SVR_PRODVALUETYPE"),
    /**
     * 管理模式
     */
    管理模式("SVR_PROD_MANAGE"),
    /**
     * 产品收益类型
     */
    产品收益类型("SVR_PROD_PROFIT_TYPE"),
    /**
     * 登记托管机构(中债)
     */
    登记托管机构_中债("SVR_REGIST_ORG"),
    /**
     * 投资者风险偏好
     */
    投资者风险偏好("SVR_RISKTYPE"),
    /**
     * 资金投向地区
     */
    资金投向地区("SVR_SALEAREA_DECLARE"),
    /**
     * 具体类别
     */
    具体类别("SVR_SEC_DETAIL_TYPE"),
    /**
     * 理财业务服务模式
     */
    理财业务服务模式("SVR_SERVICEMODE"),
    /**
     * 性别(中债)
     */
    性别_中债("SVR_SEX"),
    /**
     * 标的类别
     */
    标的类别("SVR_TARGET_TYPE"),
    /**
     * 回购标的类别
     */
    回购标的类别("SVR_TARGET_TYPE2"),
    /**
     * 交易流通场所(中债)
     */
    交易流通场所_中债("SVR_TRADE_MARKET"),
    /**
     * 投资端交易类型
     */
    投资端交易类型("SVR_TRADE_TYPE_INVEST"),
    /**
     * 上游接口文件读取处理状态
     */
    上游接口文件读取处理状态("UPINTERF_FILE_READ_STATUS"),
    /**
     * 上游接口文件类型
     */
    上游接口文件类型("UPINTERF_FILE_TYPE"),
    /**
     * 批处理账套类型
     */
    批处理账套类型("BATCH_BOOKSET_TYPE"),
    /**
     * 划款方式
     */
    划款方式("PAY_MODE"),
    /**
     * 产品类型_中债
     */
    产品类型_中债("SVR_PRODTYPE"),
    /**
     * 批处理执行结果
     */
    批处理执行结果("TASK_RESULT"),
    // 中债添加
    /**
     * 销售区域(中债)
     */
    销售区域_中债("SVR_BUY_PLACE"),
    /**
     * 接口类型
     */
    接口类型("SVR_INTERF_TYPE"),
    /**
     * 预警标示
     */
    预警标示("SVR_ISNEEDWARNING"),
    /**
     * 数据来源
     */
    数据来源("SVR_DATA_SOURCE"),
    /**
     * 接口系统ID
     */
    接口系统ID("INF_ID"),
    /**
     * 消费系统
     */
    消费系统("SYS_CODE"),
    /**
     * 币种全称（中债）
     */
    币种全称_中债("SVR_CURRENCY"),
    /**
     * 节假日交易市场(中债)
     */
    节假日_交易市场_中债("SVR_HOLIDAY_TRADE_MARKET"),
    /**
     * 上游接口文件命名规范(中债)
     */
    上游接口文件命名规范_中债("SVR_DATA_FILENAME_STANDARD"),
    /**
     * 客户一级类型(中债)
     */
    客户一级类型("SVR_CUSTOMER_TYPE1"),
    /**
     * 客户二级类型(中债)
     */
    客户二级类型("SVR_CUSTOMER_TYPE2"),
    /**
     * SPV资金托管账户开户行
     */
    SPV资金托管账户开户行("SPV_CAP_ACC_BANK"),
    /**
     * 产品受托职责(中债)
     */
    产品受托职责_中债("SVR_FIDUCIARY_DUTY"),
    /**
     * 产品转让类型(中债)
     */
    产品转让类型_中债("SVR_TRANSFER_TYPE"),
    /**
     * 兑付状态(中债)
     */
    兑付状态_中债("SVR_PAYSTATUS"),
    /**
     * 人行报送反馈状态
     */
    人行报送反馈状态("SVR_PBC_SUBMIT_FEEDBACK_STATUS"),
    /**
     * 批处理任务状态
     */
    批处理任务状态("TASK_STATUS"),
    /**
     * 作业规则1
     */
    作业规则3("RULE_TYPE3"),
    /**
     * 作业规则1
     */
    作业规则1("RULE_TYPE1"),
    /**
     * 披露渠道
     */
    报表数据格式类型("DIS_DATA_TYPE"),
    /**
     * 作业规则1
     */
    作业规则2("RULE_TYPE2"),
    /**
     * 基准日
     */
    基准日("BASIS_DATE_TYPE"),
    /**
     * 规则类型
     */
    规则类型("JOB_RANGE_FLAG"),
    /**
     *
     */
    信息披露状态("REVEAL_STATUS"),
    /**
     * 时点类型
     */
    时点类型("TIME_TYPE"),
    /**
     * 参数类型
     */
    参数类型("PARAM_TYPE"),
//	/**
//	 * 数据来源
//	 */
//	数据来源("DATA_SOURCE"),
    /**
     * 取数范围
     */
    取数范围("DATA_RANGE"),
    /**
     * 披露方式
     */
    披露方式("REVEAL_TYPE"),
    /**
     * 信息披露报表类型
     */
    信息披露报表类型("REP_INFO_TYPE"),
    /**
     * 查询范围
     */
    查询范围("QUERY_SCOPE"),
    /**
     * 查询类型
     */
    查询类型("QUERY_TYPE"),
    /*
     * 校验位置
     */
    校验位置("CHECK_FOR"),
    /**
     * 查询数据类型
     */
    查询数据类型("QUERY_DATA_TYPE"),
    /**
     * 产品信息管理 -> 信息披露 -> 披露状态
     */
    披露状态("DISCLOSURE_STATUS"),
    /**
     * 基础数据类型
     */
    基础数据类型("BASE_DATA_TYPE"),
    /**
     * 执行标志
     */
    执行标志("EXEC_FLAG"),
    /**
     * 争议解决方式
     */
    争议解决方式("CONFLICT_SOLVE_WAY"),
    /**
     * 期限品种_债券借贷
     */
    期限品种_债券借贷("TERM_TYPE_BONDLEND"),
    /**
     * 股票分紅類型
     */
    股票分红类型("STOCK_DEVISION_TYPE"),
    /**
     * 是否沪港通或深港通
     */
    是否沪港通或深港通("SH_OR_ZH"),
    /**
     * 期限品种_拆借
     */
    期限品种_拆借("TERM_TYPE_RESOLUTE"),
    /**
     * 债券借贷期限品种
     */
    债券借贷期限品种("SECLEND_TYPE"),
    /**
     * 账套配置表参数项
     */
    账套配置表参数项("BOK_BOOKSET_EXT"),

    估值表向上汇总方式("ROLL_UP_TYPE"),

    排序类型("SORT_TYPE"),

    /**
     * 期限品种(利率互换)
     */
    期限品种_利率互换("TERM_TYPE_IRS"),

//    /**
//     * 利率互换曲线类型
//     */
//    利率互换曲线("IRS_CURVE_TYPE"),

    /**
     * 利率互换曲线类型
     */
    利率互换曲线类型("CURVE_CODE_IRS"),

    /**
     * 曲线利率类型
     */
    曲线利率类型("CURVE_INT_TYPE"),

    /**
     * 利率互换曲线时刻
     */
    利率互换曲线时刻("IRS_DATA_TIME"),
    /**
     * 互换方式
     */
    互换方式("IRS_TYPE"),
    /**
     * 标的账户类型
     */
    标的账户类型("ASSET_ACCT_TYPE"),

    /**
     * 名单操作方向
     */
    名单操作方向("RSK_OPERATE_PS"),

    /**
     * 名单操作方式
     */
    名单操作方式("RSK_INPUT_TYPE"),

    /**
     * 名单主体类型
     */
    名单主体类型("RSK_SUBJECT_TYPE"),
    // add by 裴家俊 20190506----------
    /**
     * 指标参数分类维度
     */
    指标参数分类维度("PARA_DISPLAY_CLASSIFY"),
    // add by 裴家俊 20190506----------
    名单生效状态("RSK_LIST_STATUS"),
    /**
     * 	行内资产二级分类
     */
    行内资产二级分类("CUSTOM_ASSET_TYPE_TWO"),

    /**
     * 	行内资产三级分类
     */
    行内资产三级分类("CUSTOM_ASSET_TYPE_THREE"),

    /**
     * 	行内资产四级分类
     */
    行内资产四级分类("CUSTOM_ASSET_TYPE_FOUR"),

    /**
     * 	行内资产五级分类
     */
    行内资产五级分类("CUSTOM_ASSET_TYPE_FIVE"),

    /**
     * 	是否_通道
     */
    是否_通道("IS_CHL"),

    /**
     * 除0通过条件
     */
    除0通过条件("DIV_ZERO_PASS_COND"),

    /**
     * 	提醒类型
     */
    提醒类型("REMIND_TYPE"),
    /**
     * 资管资产大类
     */
    资管资产大类(
            "INF_ASSET_TYPE"
    ),
    /**
     * 商业银行净资产维护_有效状态 add by libaisong 2020-12-29 start
     */
    商业银行净资产维护_有效状态(
            "BANK_ASSET_STATUS"
    ),
    /**
     * 	开放周期
     */
    开放周期("OPEN_CYCLE"),
    /**
     * 	处理类型
     */
    处理类型("PROCESS_MODEL"),
    /**
     * 	日期选项
     */
    日期选项("DATE_OPTIONS"),
    /**
     * 	是否面向单一投资者
     */
    是否面向单一投资者("IS_SINGLE_INVESTOR"),
    /**
     * 	说明书是否披露风险标志
     */
    说明书是否披露风险标志("IS_RISK_FLAG"),
    ;
    /**
     * 对应于数据字典code
     */
    private String value;

    BussinessDictCodeRds(String value) {
        this.value = value;
    }

    /**
     * @param value 数据字典值
     * @return DictCode
     * @see
     * @since 1.0
     */
    public static BussinessDictCodeRds get(String value) {
    	BussinessDictCodeRds[] values = BussinessDictCodeRds.values();
        for (BussinessDictCodeRds code : values) {
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
    public static BussinessDictCodeRds fromValue(String value) {
        for (BussinessDictCodeRds dictCode : BussinessDictCodeRds.values()) {
            if (dictCode.getValue().equals(value)) {
                return dictCode;
            }
        }
        return null;
    }

}
