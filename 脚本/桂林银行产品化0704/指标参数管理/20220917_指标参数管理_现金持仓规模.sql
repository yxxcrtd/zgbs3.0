/**风险指标参数:XJGM 现金持仓规模**/

DECLARE
----实际值
V_CONTENT_01 CLOB := 'SELECT SUM(RVALUE)RVALUE,RKEY FROM (
select m.PORTFOLIO_ID RKEY,nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) as  RVALUE  from PTL_PORTFOLIO m
inner join ptl_capacct_info pci on m.PORTFOLIO_ID = pci.PORTFOLIO_ID
LEFT JOIN PTL_SEC_VALUTION pspv ON m.PORTFOLIO_ID = pspv.PORTFOLIO_ID and pci.ACCT_NO = pspv.FINPROD_ID AND pspv.CDATE = date''${valDate}'' -1
where  m.PORTFOLIO_TYPE IN (''01'',''02'') AND m.MDATE>date''${valDate}''  group by m.PORTFOLIO_ID
UNION ALL
---产品应收申购款(+)产品应付赎回款(-)
select ptl.PORTFOLIO_ID RKEY ,SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) RVALUE from TRD_FIN_PRODUCT_DEAL_TOTAL deal
inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE >  date''${valDate}'' and deal.IS_CANCEL =''N''
GROUP BY ptl.PORTFOLIO_ID
UNION ALL
--可转债网下申购未确认（-）
 SELECT T1.PORTFOLIO_ID RKEY, SUM(nvl(-1*T.TRADE_AMT,0)) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE 
    T.PS  IN ( ''T50'')
   AND FPT.TYPE_8 IN (''F01'')
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
GROUP BY T1.PORTFOLIO_ID
UNION ALL
--场外货币基金、货币资管计划申购申请未确认的交易金额(-)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（+）
 SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
GROUP BY T1.PORTFOLIO_ID
UNION ALL
--场外非货币基金、净值资管计划申购申请未确认的交易金额(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（+）
 SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT,0)
  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
GROUP BY T1.PORTFOLIO_ID
)TEMP
GROUP BY TEMP.RKEY
union all
select sum(nvl(TMP.TRADE_AMT,0)) RVALUE, t.PTL_BAG_ID as RKEY 
from RSK_PORTFOLIO_PACKAGE t
inner join RSK_PORTFOLIO_PACKAGE_DTL t1
  on  t1.PTL_BAG_ID=t.PTL_BAG_ID
inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}''
LEFT join
 (  select pspv.PORTFOLIO_ID,nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) TRADE_AMT 
       from PTL_SEC_VALUTION pspv
        inner join ptl_capacct_info pci on pspv.PORTFOLIO_ID = pci.PORTFOLIO_ID and pspv.FINPROD_ID = pci.ACCT_NO
        where  pspv.CDATE =date''${valDate}'' -1 
        group by pspv.PORTFOLIO_ID
        UNION ALL
        ---产品应收申购款(+)产品应付赎回款(-)
        select ptl.PORTFOLIO_ID,SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) TRADE_AMT from TRD_FIN_PRODUCT_DEAL_TOTAL deal
        inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
        and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE >= date''${valDate}'' and deal.IS_CANCEL =''N'' 
        group by ptl.PORTFOLIO_ID
          UNION ALL
        --可转债网下申购未确认（-）
         SELECT  T1.PORTFOLIO_ID,SUM(nvl(-1*T.TRADE_AMT,0)) TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE 
            T.PS  IN ( ''T50'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        group by T1.PORTFOLIO_ID
        UNION ALL
          --场外货币基金、货币资管计划申购申请未确认的交易金额(-)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
        UNION ALL
        --场外非货币基金、净值资管计划申购申请未确认的交易金额(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
     )TMP
ON  t1.PORTFOLIO_ID = TMP.PORTFOLIO_ID
group by t.PTL_BAG_ID';
----买入预占
V_CONTENT_02 CLOB := 'SELECT SUM(RVALUE)RVALUE,RKEY FROM (
select m.PORTFOLIO_ID RKEY,nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) as  RVALUE  from PTL_PORTFOLIO m
inner join ptl_capacct_info pci on m.PORTFOLIO_ID = pci.PORTFOLIO_ID
LEFT JOIN PTL_SEC_VALUTION pspv ON m.PORTFOLIO_ID = pspv.PORTFOLIO_ID and pci.ACCT_NO = pspv.FINPROD_ID AND pspv.CDATE = date''${valDate}'' -1
where  m.PORTFOLIO_TYPE IN (''01'',''02'') AND m.MDATE>date''${valDate}''  group by m.PORTFOLIO_ID
UNION ALL
---产品应收申购款(+)产品应付赎回款(-)
select ptl.PORTFOLIO_ID RKEY ,SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) RVALUE from TRD_FIN_PRODUCT_DEAL_TOTAL deal
inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE >  date''${valDate}'' and deal.IS_CANCEL =''N''
GROUP BY ptl.PORTFOLIO_ID
UNION ALL
--可转债网下申购未确认（-）
 SELECT T1.PORTFOLIO_ID RKEY, SUM(nvl(-1*T.TRADE_AMT,0)) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE 
    T.PS  IN ( ''T50'')
   AND FPT.TYPE_8 IN (''F01'')
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
GROUP BY T1.PORTFOLIO_ID
UNION ALL
--场外货币基金、货币资管计划申购申请未确认的交易金额(-)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（+）
 SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
GROUP BY T1.PORTFOLIO_ID
UNION ALL
--场外非货币基金、净值资管计划申购申请未确认的交易金额(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（+）
 SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT,0)
  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
GROUP BY T1.PORTFOLIO_ID
)TEMP
GROUP BY TEMP.RKEY
union all
select sum(nvl(TMP.TRADE_AMT,0)) RVALUE, t.PTL_BAG_ID as RKEY 
from RSK_PORTFOLIO_PACKAGE t
inner join RSK_PORTFOLIO_PACKAGE_DTL t1
  on  t1.PTL_BAG_ID=t.PTL_BAG_ID
inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}''
LEFT join
 (  select pspv.PORTFOLIO_ID,nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) TRADE_AMT 
       from PTL_SEC_VALUTION pspv
        inner join ptl_capacct_info pci on pspv.PORTFOLIO_ID = pci.PORTFOLIO_ID and pspv.FINPROD_ID = pci.ACCT_NO
        where  pspv.CDATE =date''${valDate}'' -1 
        group by pspv.PORTFOLIO_ID
        UNION ALL
        ---产品应收申购款(+)产品应付赎回款(-)
        select ptl.PORTFOLIO_ID,SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) TRADE_AMT from TRD_FIN_PRODUCT_DEAL_TOTAL deal
        inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
        and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE >= date''${valDate}'' and deal.IS_CANCEL =''N'' 
        group by ptl.PORTFOLIO_ID
          UNION ALL
        --可转债网下申购未确认（-）
         SELECT  T1.PORTFOLIO_ID,SUM(nvl(-1*T.TRADE_AMT,0)) TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE 
            T.PS  IN ( ''T50'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        group by T1.PORTFOLIO_ID
        UNION ALL
          --场外货币基金、货币资管计划申购申请未确认的交易金额(-)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
        UNION ALL
        --场外非货币基金、净值资管计划申购申请未确认的交易金额(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
     )TMP
ON  t1.PORTFOLIO_ID = TMP.PORTFOLIO_ID
group by t.PTL_BAG_ID';
----卖出预占
V_CONTENT_03 CLOB := 'SELECT SUM(RVALUE)RVALUE,RKEY FROM (
select m.PORTFOLIO_ID RKEY,nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) as  RVALUE  from PTL_PORTFOLIO m
inner join ptl_capacct_info pci on m.PORTFOLIO_ID = pci.PORTFOLIO_ID
LEFT JOIN PTL_SEC_VALUTION pspv ON m.PORTFOLIO_ID = pspv.PORTFOLIO_ID and pci.ACCT_NO = pspv.FINPROD_ID AND pspv.CDATE = date''${valDate}'' -1
where  m.PORTFOLIO_TYPE IN (''01'',''02'') AND m.MDATE>date''${valDate}''  group by m.PORTFOLIO_ID
UNION ALL
---产品应收申购款(+)产品应付赎回款(-)
select ptl.PORTFOLIO_ID RKEY ,SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) RVALUE from TRD_FIN_PRODUCT_DEAL_TOTAL deal
inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE >  date''${valDate}'' and deal.IS_CANCEL =''N''
GROUP BY ptl.PORTFOLIO_ID
UNION ALL
--可转债网下申购未确认（-）
 SELECT T1.PORTFOLIO_ID RKEY, SUM(nvl(-1*T.TRADE_AMT,0)) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE 
    T.PS  IN ( ''T50'')
   AND FPT.TYPE_8 IN (''F01'')
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)
GROUP BY T1.PORTFOLIO_ID
UNION ALL
--场外货币基金、货币资管计划申购申请未确认的交易金额(-)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（+）
 SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
GROUP BY T1.PORTFOLIO_ID
UNION ALL
--场外非货币基金、净值资管计划申购申请未确认的交易金额(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（+）
 SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT,0)
  when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
 inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
 where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE < DATE''${valDate}''
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
GROUP BY T1.PORTFOLIO_ID
)TEMP
GROUP BY TEMP.RKEY
union all
select sum(nvl(TMP.TRADE_AMT,0)) RVALUE, t.PTL_BAG_ID as RKEY 
from RSK_PORTFOLIO_PACKAGE t
inner join RSK_PORTFOLIO_PACKAGE_DTL t1
  on  t1.PTL_BAG_ID=t.PTL_BAG_ID
inner join PTL_PORTFOLIO t2 on t1.PORTFOLIO_ID=t2.PORTFOLIO_ID and t2.MDATE>date''${valDate}''
LEFT join
 (  select pspv.PORTFOLIO_ID,nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) TRADE_AMT 
       from PTL_SEC_VALUTION pspv
        inner join ptl_capacct_info pci on pspv.PORTFOLIO_ID = pci.PORTFOLIO_ID and pspv.FINPROD_ID = pci.ACCT_NO
        where  pspv.CDATE =date''${valDate}'' -1 
        group by pspv.PORTFOLIO_ID
        UNION ALL
        ---产品应收申购款(+)产品应付赎回款(-)
        select ptl.PORTFOLIO_ID,SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) TRADE_AMT from TRD_FIN_PRODUCT_DEAL_TOTAL deal
        inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
        and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE >= date''${valDate}'' and deal.IS_CANCEL =''N'' 
        group by ptl.PORTFOLIO_ID
          UNION ALL
        --可转债网下申购未确认（-）
         SELECT  T1.PORTFOLIO_ID,SUM(nvl(-1*T.TRADE_AMT,0)) TRADE_AMT 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
         WHERE 
            T.PS  IN ( ''T50'')
           AND FPT.TYPE_8 IN (''F01'')
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
        )
        group by T1.PORTFOLIO_ID
        UNION ALL
          --场外货币基金、货币资管计划申购申请未确认的交易金额(-)、（场外货币基金、货币资管计划）赎回申请未确认的交易金额（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0)  else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F01''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
        UNION ALL
        --场外非货币基金、净值资管计划申购申请未确认的交易金额(-)、（场外非货币基金、净值资管计划赎回申请未确认的交易金额（+）
         SELECT T1.PORTFOLIO_ID RKEY,SUM(case when ps IN (''T06'') and T.TRADE_AMT !=0  then nvl(T.TRADE_AMT,0)
          when ps IN (''T06'') and (T.TRADE_AMT =0 or T.TRADE_AMT is null)  then nvl(T.SHARE_AMT,0)*(select NVL(fvpp.VALUATION,1) from  FIN_PRODUCT_VALUATION fvpp 
         inner JOIN (select fvp.FINPROD_ID,MAX(fvp.VAL_DATE) VAL_DATE from FIN_PRODUCT_VALUATION fvp group by fvp.FINPROD_ID) hq on fvpp.FINPROD_ID = hq.FINPROD_ID and fvpp.VAL_DATE = hq.VAL_DATE 
         where fvpp.FINPROD_ID = T2.FINPROD_MARKET_ID)
          else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
          FROM TRD_PRODUCT_DEAL T
         INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
         INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
         INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
         WHERE 
            T.PS  IN (''T04'',''T06'')
           AND (FPT.TYPE_8 IN (''F04'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09'' and FPT.TYPE_6=''F02''))
           AND T.TRADE_STATUS = ''01''
           AND T.IS_CANCEL = ''N''
           AND T.TRADE_DATE < DATE''${valDate}''
           AND NOT EXISTS (
              -- 没有后续申购确认交易
              SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
        )
        GROUP BY T1.PORTFOLIO_ID
     )TMP
ON  t1.PORTFOLIO_ID = TMP.PORTFOLIO_ID
group by t.PTL_BAG_ID';
----事后(日间)
V_CONTENT_04 CLOB := 'SELECT sum(RVALUE) FROM (
           select nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) as  RVALUE  from PTL_PORTFOLIO m
                inner join ptl_capacct_info pci on m.PORTFOLIO_ID = pci.PORTFOLIO_ID
                LEFT JOIN PTL_SEC_VALUTION pspv ON m.PORTFOLIO_ID = pspv.PORTFOLIO_ID and pci.ACCT_NO = pspv.FINPROD_ID AND pspv.CDATE = date''${valDate}'' -1
                where  m.PORTFOLIO_TYPE IN (''01'',''02'') AND m.MDATE>date''${valDate}'' and  pspv.PORTFOLIO_ID in ${portfolioCode}  
          UNION ALL
            SELECT nvl(sum(pcpf.AMOUNT),0) as RVALUE from PTL_CASH_POSITION n
            LEFT JOIN PTL_CASH_POSITION_FLOW pcpf ON n.PORTFOLIO_ID = pcpf.PORTFOLIO_ID AND n.ACCT_ID = pcpf.ACCT_ID AND n.CDATE =pcpf.CDATE and pcpf.hoding_type = n.hoding_type
            WHERE n.PORTFOLIO_ID in ${portfolioCode} and n.HODING_TYPE=''03'' and n.CDATE =date''${valDate}''
          UNION ALL
          select nvl(sum(AMOUNT),0) as  RVALUE from PTL_CASH_POSITION_FLOW
            where  PORTFOLIO_ID in ${portfolioCode} and HODING_TYPE=''01'' and CDATE >=date''${valDate}'' 
            UNION ALL
            select nvl(sum(pcpf.AMOUNT),0) as  RVALUE from PTL_CASH_POSITION_FLOW pcpf
            INNER JOIN STL_PAY_ORDERS spn ON spn.ORDER_ID = pcpf.ORDER_ID
            where  pcpf.PORTFOLIO_ID in ${portfolioCode} and pcpf.HODING_TYPE=''03'' and pcpf.CDATE >date''${valDate}'' 
            AND EXISTS (select * from TRD_PRODUCT_DEAL tpd where tpd.TOTLE_TRADE_ID = spn.TRADE_ID AND tpd.TRADE_DATE =date''${valDate}'')
            UNION ALL
          select nvl(sum(pcpf.AMOUNT),0) as  RVALUE from PTL_CASH_POSITION_FLOW pcpf
          INNER JOIN STL_PAY_TEMP_ORDERS spn ON spn.ORDER_ID = pcpf.ORDER_ID
            where  pcpf.PORTFOLIO_ID in ${portfolioCode} and pcpf.HODING_TYPE=''03'' and pcpf.CDATE >date''${valDate}'' AND
            AND EXISTS (select * from TRD_PRODUCT_TEMP_DEAL tpd where tpd.TOTLE_TRADE_ID = spn.TRADE_ID AND tpd.TRADE_DATE =date''${valDate}'')
  )';
----事后(日终)
V_CONTENT_05 CLOB := 'SELECT  SUM(TEMP.RVALUE) RVALUE FROM (
select nvl(sum(nvl(pspv.DSC_COST_AMT,0)+nvl(pspv.TDY_INTINCEXP,0)+nvl(pspv.TDY_FLOAT_INGPL,0)+nvl(pspv.DELAY_PAY_AMT,0)+nvl(pspv.TDY_LOSS_AMT,0)),0) as  RVALUE  from PTL_PORTFOLIO m
inner join ptl_capacct_info pci on m.PORTFOLIO_ID = pci.PORTFOLIO_ID
inner JOIN PTL_SEC_VALUTION pspv ON m.PORTFOLIO_ID = pspv.PORTFOLIO_ID and pci.ACCT_NO = pspv.FINPROD_ID AND pspv.CDATE = date''${valDate}''
 where  m.PORTFOLIO_TYPE IN (''01'',''02'') AND m.MDATE>date''${valDate}'' and  pspv.PORTFOLIO_ID in ${portfolioCode}  
UNION ALL
---产品应收申购款(+)产品应付赎回款(-)
select SUM(case when ps IN (''C1'',''C2'') then nvl(deal.TRADE_AMT,0) else nvl(-1*deal.TRADE_AMT,0) end ) RVALUE from TRD_FIN_PRODUCT_DEAL_TOTAL deal
inner join PTL_TRADE ptl on deal.TOTLE_TRADE_ID = ptl.BUSI_NO where deal.FINPROD_TYPE2=''F16'' and deal.ps IN (''C1'',''C2'',''C4'',''C5'',''C17'',''C18'',''C20'')
and deal.TRADE_DATE != deal.SETTLE_DATE and deal.TRADE_STATUS=''01''and deal.SETTLE_DATE > date''${valDate}'' and deal.IS_CANCEL =''N''
and ptl.PORTFOLIO_ID in ${portfolioCode} 
UNION ALL
--可转债网下申购未确认（-）
 SELECT SUM(nvl(-1*T.TRADE_AMT,0)) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 INNER JOIN TRD_FIN_PRODUCT_DEAL_TOTAL T3 on T.TOTLE_TRADE_ID = T3.TOTLE_TRADE_ID
 WHERE 
    T.PS  IN ( ''T50'')
   AND FPT.TYPE_8 IN (''F01'')
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode} 
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T3.TOTLE_TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''
)

UNION ALL
--场外基金、净值资管计划、货币资管计划申购申请未确认的交易金额(-)、（场外基金、净值资管计划、货币资管计划）赎回申请未确认的交易金额（+）
 SELECT SUM(case when ps IN (''T06'') then nvl(T.TRADE_AMT,0) else nvl(-1*T.TRADE_AMT,0) end ) RVALUE 
  FROM TRD_PRODUCT_DEAL T
 INNER JOIN TRD_PRODUCT_DEAL_ADD T1 ON T.trade_id = T1.trade_id
 INNER JOIN FIN_PRODUCT T2 on T.FINPROD_ID = T2.FINPROD_ID
 INNER JOIN FIN_PRODUCT_TYPE FPT on T2.FINPROD_ID = FPT.FINPROD_ID and FPT.BRANCH= 0 
 WHERE 
    T.PS  IN (''T04'',''T06'')
   AND (FPT.TYPE_8 IN (''F04'',''F09'') OR (FPT.TYPE_8 = ''F02'' and T2.TRADE_MARKET = ''09''))
   AND T.TRADE_STATUS = ''01''
   AND T.IS_CANCEL = ''N''
   AND T.TRADE_DATE <= DATE''${valDate}''
   AND T1.PORTFOLIO_ID in ${portfolioCode} 
   AND NOT EXISTS (
      -- 没有后续申购确认交易
      SELECT 1 FROM TRD_PRODUCT_DEAL TRD WHERE T.TRADE_ID = TRD.B_TRADE_ID AND TRD.TRADE_STATUS = ''01'' AND TRD.IS_CANCEL = ''N''   
)
)TEMP';
BEGIN
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_01 WHERE PARA_CODE = 'XJGM' and PARA_GRANULARITY ='01';
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_02 WHERE PARA_CODE = 'XJGM' and PARA_GRANULARITY ='02';
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_03 WHERE PARA_CODE = 'XJGM' and PARA_GRANULARITY ='03';
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_04 WHERE PARA_CODE = 'XJGM' and PARA_GRANULARITY ='04';
    UPDATE RSK_ISUP_INDICATOR_PARA_DTL SET CONTENT = V_CONTENT_05 WHERE PARA_CODE = 'XJGM' and PARA_GRANULARITY ='05';
END;
/
