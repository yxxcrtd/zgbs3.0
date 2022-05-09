-------------瞿志  20220225 组合关联指标改造 start----------------------------
ALTER TABLE RSK_INDIVID_CONDITION ADD DATE_STR VARCHAR2(50);
COMMENT ON COLUMN RSK_INDIVID_CONDITION.DATE_STR IS '日期选项(起)';
ALTER TABLE RSK_INDIVID_CONDITION ADD DATE_END VARCHAR2(50);
COMMENT ON COLUMN RSK_INDIVID_CONDITION.DATE_END IS '日期选项(末)';

ALTER TABLE RSK_INDIVID_CONDITION ADD DATE_UNIT_STR VARCHAR2(50);
COMMENT ON COLUMN RSK_INDIVID_CONDITION.DATE_UNIT_STR IS '日期单位(起)';
ALTER TABLE RSK_INDIVID_CONDITION ADD DATE_UNIT_END VARCHAR2(50);
COMMENT ON COLUMN RSK_INDIVID_CONDITION.DATE_UNIT_END IS '日期单位(末)';
-------------瞿志  20220225 组合关联指标改造 end----------------------------

-------------瞿志  20220301 特定资产类型设置 start----------------------------
ALTER TABLE RSK_SPECIFIC_ASSET_TYPE ADD IS_LIQUIDITY1 VARCHAR2(32);
COMMENT ON COLUMN RSK_SPECIFIC_ASSET_TYPE.IS_LIQUIDITY1 IS '是否无活跃交易市场且需要采用估值技术确定公允价值资产';
ALTER TABLE RSK_SPECIFIC_ASSET_TYPE ADD IS_LIQUIDITY2 VARCHAR2(32);
COMMENT ON COLUMN RSK_SPECIFIC_ASSET_TYPE.IS_LIQUIDITY2 IS '是否不具备活跃交易市场或者在活跃市场中无报价，且不能采用估值技术可靠计量公允价值的';
ALTER TABLE RSK_SPECIFIC_ASSET_TYPE ADD IS_LIQUIDITY3 VARCHAR2(32);
COMMENT ON COLUMN RSK_SPECIFIC_ASSET_TYPE.IS_LIQUIDITY3 IS '是否7个工作日可变现资产';
-------------瞿志  20220301 特定资产类型设置 end----------------------------


/** 流动性风险产品端新增字段 add by kongzhifang on 2022-03-03 start **/
alter table fin_product_add add direct_disclosure_risk_flag varchar2(50);
comment on column fin_product_add.direct_disclosure_risk_flag is '说明书是否披露风险标志';

alter table prd_sale_param add singleInvestor_holding_Max NUMBER(30,14);
comment on column prd_sale_param.singleInvestor_holding_Max is '单一投资者持有上限';

alter table prd_sale_param add is_Face_SingleInvestor varchar2(50);
comment on column prd_sale_param.is_Face_SingleInvestor is '是否面向单一投资者';

alter table fin_product_add add IS_OPEN_EVERYDAY varchar2(50);
comment on column fin_product_add.IS_OPEN_EVERYDAY is '是否每日开放申赎';

alter table fin_product_add add IS_SEVEN_DAY_CASHABLE varchar2(50);
comment on column fin_product_add.IS_SEVEN_DAY_CASHABLE is '是否7日可变现资产';

alter table fin_product_add add OPEN_TYPE varchar2(50);
comment on column fin_product_add.OPEN_TYPE is '开放类型';

/** 流动性风险产品端新增字段 add by kongzhifang on 2022-03-03 end **/

-------------裴家俊  20220304 组合自动关联指标之个性化参数设置 start----------------------------
--条件设置-视图
DROP VIEW RSK_INDIVID_CONDITION_VIEW;
CREATE VIEW
    RSK_INDIVID_CONDITION_VIEW
    (
        CONDITION_ID ,
        PRT_BAG_ID,
        IND_ID ,
        CONDITION_ITEM ,
        CONTROL_TYPE ,
        VALUE_STR ,
        VALUE_END ,
        DATE_STR,
		DATE_END,
		DATE_UNIT_STR,
		DATE_UNIT_END,
        UPDATE_TIME,
        CREATE_TIME,
        CREATE_DEPT,
        CREATE_USER,
        UPDATE_USER,
		PROCESS_MODEL		
) AS 
SELECT  CONDITION_ID ,
         PORTFOLIO_ID ,
         IND_ID ,
         CONDITION_ITEM ,
         CONTROL_TYPE ,
         VALUE_STR ,
         VALUE_END ,
         DATE_STR,
		 DATE_END,
		 DATE_UNIT_STR,
		 DATE_UNIT_END,
         UPDATE_TIME,
         CREATE_TIME,
         CREATE_DEPT,
         CREATE_USER,
         UPDATE_USER,
		 PROCESS_MODEL
FROM(SELECT CONDITION_ID ,
              PORTFOLIO_ID ,
              IND_ID ,
              CONDITION_ITEM ,
              CONTROL_TYPE ,
              VALUE_STR ,
              VALUE_END ,
              DATE_STR,
			  DATE_END,
			  DATE_UNIT_STR,
			  DATE_UNIT_END,
              UPDATE_TIME,
              CREATE_TIME,
              CREATE_DEPT,
              CREATE_USER,
              UPDATE_USER,
			  PROCESS_MODEL,
              dense_rank()over(partition BY PORTFOLIO_ID,IND_ID ORDER BY TO_CHAR(UPDATE_TIME,
            'yyyy-MM-dd hh24:mi') DESC) rn
        FROM( SELECT A.CONDITION_ID ,
                      D.PORTFOLIO_ID ,
                      A.IND_ID ,
                      A.CONDITION_ITEM ,
                      A.CONTROL_TYPE ,
                      A.VALUE_STR ,
                      A.VALUE_END ,
                      A.DATE_STR,
					  A.DATE_END,
					  A.DATE_UNIT_STR,
					  A.DATE_UNIT_END,
                      A.UPDATE_TIME,
                      A.CREATE_TIME,
                      A.CREATE_DEPT,
                      A.CREATE_USER,
                      A.UPDATE_USER,
					  'RULE' AS PROCESS_MODEL
                FROM RSK_INDIVID_CONDITION A 
                INNER JOIN RSK_RULE_PORTFOLIO D ON D.RULE_ID = A.PRT_BAG_ID
                WHERE D.PORTFOLIO_ID IS NOT NULL
                UNION ALL
                SELECT  A.CONDITION_ID ,
                        A.PRT_BAG_ID PORTFOLIO_ID ,                      
                        A.IND_ID ,
                        A.CONDITION_ITEM ,
                        A.CONTROL_TYPE ,
                        A.VALUE_STR ,
                        A.VALUE_END ,
                        A.DATE_STR,
						A.DATE_END,
						A.DATE_UNIT_STR,
						A.DATE_UNIT_END,
                        A.UPDATE_TIME,
                        A.CREATE_TIME,
                        A.CREATE_DEPT,
                        A.CREATE_USER,
                        A.UPDATE_USER,
						'PRT' AS PROCESS_MODEL
                FROM  RSK_INDIVID_CONDITION A
                WHERE NOT EXISTS (SELECT 1 FROM RSK_RULE_INFO rri WHERE RULE_ID =A.PRT_BAG_ID ) ))
WHERE rn = 1;

--联合设置-视图
DROP VIEW RSK_INDIVID_UNITE_VIEW;
CREATE VIEW
    RSK_INDIVID_UNITE_VIEW
    (
        UNITE_ID ,
        PRT_BAG_ID,
        IND_ID ,
        REMARK,
        UPDATE_TIME,
        CREATE_TIME,
        CREATE_DEPT,
        CREATE_USER,
        UPDATE_USER,
		PROCESS_MODEL		
) AS 
SELECT  UNITE_ID ,
         PORTFOLIO_ID ,
         IND_ID,
         REMARK,
         UPDATE_TIME,
         CREATE_TIME,
         CREATE_DEPT,
         CREATE_USER,
         UPDATE_USER,
		 PROCESS_MODEL
FROM(SELECT UNITE_ID ,
              PORTFOLIO_ID ,
              IND_ID,
              REMARK, 
              UPDATE_TIME,
              CREATE_TIME,
              CREATE_DEPT,
              CREATE_USER,
              UPDATE_USER,
			  PROCESS_MODEL,
              dense_rank()over(partition BY PORTFOLIO_ID,IND_ID ORDER BY TO_CHAR(UPDATE_TIME,
            'yyyy-MM-dd hh24:mi') DESC) rn
        FROM( SELECT A.UNITE_ID ,
                      D.PORTFOLIO_ID ,
                      A.IND_ID,
                      A.REMARK, 
                      A.UPDATE_TIME,
                      A.CREATE_TIME,
                      A.CREATE_DEPT,
                      A.CREATE_USER,
                      A.UPDATE_USER,
					  'RULE' AS PROCESS_MODEL
                FROM RSK_INDIVID_UNITE A 
                INNER JOIN RSK_RULE_PORTFOLIO D ON D.RULE_ID = A.PRT_BAG_ID
                WHERE D.PORTFOLIO_ID IS NOT NULL
                UNION ALL
                SELECT  A.UNITE_ID ,
                        A.PRT_BAG_ID PORTFOLIO_ID ,                  
                        A.IND_ID,
                        A.REMARK,
                        A.UPDATE_TIME,
                        A.CREATE_TIME,
                        A.CREATE_DEPT,
                        A.CREATE_USER,
                        A.UPDATE_USER,
						'PRT' AS PROCESS_MODEL
                FROM  RSK_INDIVID_UNITE A
                WHERE NOT EXISTS (SELECT 1 FROM RSK_RULE_INFO rri WHERE RULE_ID =A.PRT_BAG_ID ) ))
WHERE rn = 1;

--个性化特殊阶段-视图
DROP VIEW RSK_INDIVID_SPECIAL_STAGE_VIEW;
CREATE VIEW
    RSK_INDIVID_SPECIAL_STAGE_VIEW
    (
        UNITE_ID,
        PRT_BAG_ID,
        IND_ID ,
        START_DATE,
        END_DATE,
        UNITE_TYPE,
        FINPROD_ID,
        WARN_COND,
        APPROVE_COND,
        FOUL_COND ,
        UPDATE_TIME,
        CREATE_TIME,
        CREATE_DEPT,
        CREATE_USER,
        UPDATE_USER,
		PROCESS_MODEL
) AS 
SELECT  UNITE_ID ,
         PORTFOLIO_ID ,
         IND_ID ,
         START_DATE,
         END_DATE,
         UNITE_TYPE,
         FINPROD_ID,
         WARN_COND,
         APPROVE_COND,
         FOUL_COND ,
         UPDATE_TIME,
         CREATE_TIME,
         CREATE_DEPT,
         CREATE_USER,
         UPDATE_USER,
		 PROCESS_MODEL
FROM(SELECT UNITE_ID ,
              PORTFOLIO_ID ,
              IND_ID ,
              START_DATE,
              END_DATE,
              UNITE_TYPE,
              FINPROD_ID,
              WARN_COND,
              APPROVE_COND,
              FOUL_COND ,
              UPDATE_TIME,
              CREATE_TIME,
              CREATE_DEPT,
              CREATE_USER,
              UPDATE_USER,
			  PROCESS_MODEL,
              dense_rank()over(partition BY PORTFOLIO_ID,IND_ID ORDER BY TO_CHAR(UPDATE_TIME,
            'yyyy-MM-dd hh24:mi') DESC) rn
        FROM( SELECT A.UNITE_ID ,
                      D.PORTFOLIO_ID ,
                      A.IND_ID,
                      A.START_DATE,
                      A.END_DATE,
                      A.UNITE_TYPE,
                      A.FINPROD_ID,
                      A.WARN_COND,
                      A.APPROVE_COND,
                      A.FOUL_COND ,
                      A.UPDATE_TIME,
                      A.CREATE_TIME,
                      A.CREATE_DEPT,
                      A.CREATE_USER,
                      A.UPDATE_USER,
					  'RULE' AS PROCESS_MODEL
                FROM RSK_INDIVID_SPECIAL_STAGE A 
                INNER JOIN RSK_RULE_PORTFOLIO D ON D.RULE_ID = A.PRT_BAG_ID
                WHERE D.PORTFOLIO_ID IS NOT NULL
                UNION ALL
                SELECT  A.UNITE_ID ,
                        A.PRT_BAG_ID PORTFOLIO_ID ,
                        A.IND_ID,
                        A.START_DATE,
                        A.END_DATE,
                        A.UNITE_TYPE,
                        A.FINPROD_ID,
                        A.WARN_COND,
                        A.APPROVE_COND,
                        A.FOUL_COND ,
                        A.UPDATE_TIME,
                        A.CREATE_TIME,
                        A.CREATE_DEPT,
                        A.CREATE_USER,
                        A.UPDATE_USER,
						'PRT' AS PROCESS_MODEL
                FROM  RSK_INDIVID_SPECIAL_STAGE A
                WHERE NOT EXISTS (SELECT 1 FROM RSK_RULE_INFO rri WHERE RULE_ID =A.PRT_BAG_ID ) ))
WHERE rn = 1;

--个性化剔除资产-视图
DROP VIEW RSK_INDIVID_ASSET_DEL_VIEW;
CREATE VIEW
    RSK_INDIVID_ASSET_DEL_VIEW
    (
        DEL_ID,
        PRT_BAG_ID,
        IND_ID,
        START_DATE ,
        END_DATE,
        FINPROD_ID ,
        CREATE_USER ,
        CREATE_DEPT,
        CREATE_TIME,
        UPDATE_USER,
        UPDATE_TIME,
		PROCESS_MODEL
) AS 
SELECT  DEL_ID ,
         PORTFOLIO_ID ,
         IND_ID ,
         START_DATE ,
         END_DATE ,
         FINPROD_ID,
         CREATE_USER ,
         CREATE_DEPT,
         CREATE_TIME,
         UPDATE_USER,
         UPDATE_TIME,
		 PROCESS_MODEL
FROM(SELECT  DEL_ID ,
              PORTFOLIO_ID ,
              IND_ID ,
              START_DATE ,
              END_DATE ,
              FINPROD_ID,
              UPDATE_TIME,
              CREATE_TIME,
              CREATE_DEPT,
              CREATE_USER,
              UPDATE_USER,
			  PROCESS_MODEL,
              dense_rank()over(partition BY PORTFOLIO_ID,IND_ID ORDER BY TO_CHAR(UPDATE_TIME,
            'yyyy-MM-dd hh24:mi') DESC) rn
        FROM( SELECT A.DEL_ID ,
                      D.PORTFOLIO_ID ,
                      A.IND_ID ,
                      A.START_DATE ,
                      A.END_DATE ,
                      A.FINPROD_ID ,
                      A.UPDATE_TIME,
                      A.CREATE_TIME,
                      A.CREATE_DEPT,
                      A.CREATE_USER,
                      A.UPDATE_USER,
					  'RULE' AS PROCESS_MODEL
                FROM RSK_INDIVID_ASSET_DEL A 
                INNER JOIN RSK_RULE_PORTFOLIO D ON D.RULE_ID = A.PRT_BAG_ID
                WHERE D.PORTFOLIO_ID IS NOT NULL
                UNION ALL
                SELECT  A.DEL_ID ,
                        A.PRT_BAG_ID PORTFOLIO_ID ,
                        A.IND_ID ,
                        A.START_DATE ,
                        A.END_DATE ,
                        A.FINPROD_ID ,
                        A.UPDATE_TIME,
                        A.CREATE_TIME,
                        A.CREATE_DEPT,
                        A.CREATE_USER,
                        A.UPDATE_USER,
						'PRT' AS PROCESS_MODEL
                FROM  RSK_INDIVID_ASSET_DEL A
                WHERE NOT EXISTS (SELECT 1 FROM RSK_RULE_INFO rri WHERE RULE_ID =A.PRT_BAG_ID ) ))
WHERE rn = 1;

--个性化自定义阈值设置-视图
DROP VIEW RSK_IND_CONDITION_LINE_VIEW;
CREATE VIEW
    RSK_IND_CONDITION_LINE_VIEW
    (
        CONDITION_ID ,
        PRT_BAG_ID,
        IND_ID ,
        CONDITION_ITEM ,
        CONTROL_TYPE ,
        VALUE_STR ,
        VALUE_END ,
		WARN_COND ,
		APPROVE_COND ,
		FOUL_COND ,
		IS_BAG_CALC ,
        UPDATE_TIME,
        CREATE_TIME,
        CREATE_DEPT,
        CREATE_USER,
        UPDATE_USER,
		PROCESS_MODEL
) AS 
SELECT  CONDITION_ID ,
        PORTFOLIO_ID,
        IND_ID ,
        CONDITION_ITEM ,
        CONTROL_TYPE ,
        VALUE_STR ,
        VALUE_END ,
		WARN_COND ,
		APPROVE_COND ,
		FOUL_COND ,
		IS_BAG_CALC ,
         UPDATE_TIME,
         CREATE_TIME,
         CREATE_DEPT,
         CREATE_USER,
         UPDATE_USER,
		 PROCESS_MODEL
FROM(SELECT CONDITION_ID ,
			PORTFOLIO_ID,
			IND_ID ,
			CONDITION_ITEM ,
			CONTROL_TYPE ,
			VALUE_STR ,
			VALUE_END ,
			WARN_COND ,
			APPROVE_COND ,
			FOUL_COND ,
			IS_BAG_CALC ,
              UPDATE_TIME,
              CREATE_TIME,
              CREATE_DEPT,
              CREATE_USER,
              UPDATE_USER,
			  PROCESS_MODEL,
              dense_rank()over(partition BY PORTFOLIO_ID,IND_ID ORDER BY TO_CHAR(UPDATE_TIME,
            'yyyy-MM-dd hh24:mi') DESC) rn
        FROM( SELECT A.CONDITION_ID ,
                      D.PORTFOLIO_ID ,
                      A.IND_ID ,
                      A.CONDITION_ITEM ,
                      A.CONTROL_TYPE ,
                      A.VALUE_STR ,
                      A.VALUE_END ,
					  A.WARN_COND ,
					  A.APPROVE_COND ,
					  A.FOUL_COND ,
					  A.IS_BAG_CALC ,
                      A.UPDATE_TIME,
                      A.CREATE_TIME,
                      A.CREATE_DEPT,
                      A.CREATE_USER,
                      A.UPDATE_USER,
					  'RULE' AS PROCESS_MODEL
                FROM RSK_INDIVID_CONDITION_LINE A 
                INNER JOIN RSK_RULE_PORTFOLIO D ON D.RULE_ID = A.PRT_BAG_ID
                WHERE D.PORTFOLIO_ID IS NOT NULL
                UNION ALL
                SELECT  A.CONDITION_ID ,
                         A.PRT_BAG_ID PORTFOLIO_ID ,                      
                         A.IND_ID ,
                         A.CONDITION_ITEM ,
                         A.CONTROL_TYPE ,
                         A.VALUE_STR ,
                         A.VALUE_END ,
						 A.WARN_COND ,
						 A.APPROVE_COND ,
						 A.FOUL_COND ,
						 A.IS_BAG_CALC ,
                        A.UPDATE_TIME,
                        A.CREATE_TIME,
                        A.CREATE_DEPT,
                        A.CREATE_USER,
                        A.UPDATE_USER,
						'PRT' AS PROCESS_MODEL
                FROM  RSK_INDIVID_CONDITION_LINE A
                WHERE NOT EXISTS (SELECT 1 FROM RSK_RULE_INFO rri WHERE RULE_ID =A.PRT_BAG_ID ) ))
WHERE rn = 1;

-------------裴家俊  20220304 组合自动关联指标之个性化参数设置 end----------------------------

-------------裴家俊  20220314 更改实占与实占历史表的代码字段长度 start----------------------------
ALTER TABLE RSK_OCCUPY_INFO modify(EVENT_ID varchar(128));
ALTER TABLE RSK_OCCUPY_INFO_HIS modify(EVENT_ID varchar(128));
-------------裴家俊  20220314 更改实占与实占历史表的代码字段长度 end----------------------------

-------------瞿志  20220315 更改条件设置数值(起)为非必填 start----------------------------
ALTER TABLE RSK_INDIVID_CONDITION MODIFY VALUE_STR NULL;
-------------瞿志  20220315 更改条件设置数值(起)为非必填 end----------------------------

-------------裴家俊  20220316 开放规则表添加申购与赎回确认速度字段(计划类资产画面增加开放赎回计划) start-----------
ALTER TABLE FIN_OPEN_RULE ADD (PUR_SPEED NUMBER(10,0));
COMMENT ON COLUMN FIN_OPEN_RULE.PUR_SPEED IS '申购确认速度';

ALTER TABLE FIN_OPEN_RULE ADD (RED_SPEED NUMBER(10,0));
COMMENT ON COLUMN FIN_OPEN_RULE.RED_SPEED IS '赎回确认速度';
-------------裴家俊  20220316 开放规则表添加申购与赎回确认速度字段(计划类资产画面增加开放赎回计划) end-----------

-------------裴家俊  20220326 修改指标名称字段长度 start-----------
alter table RSK_ISUP_INDICATOR MODIFY(IND_NAME VARCHAR2(512));
-------------裴家俊  20220326 修改指标名称字段长度 end-----------