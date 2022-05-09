/**风险指标:单只定期开放式私募理财产品直接投资于流动性受限资产的市值在开放日不得超过该产品资产净值的20%**/
 DELETE FROM RSK_ISUP_INDICATOR WHERE IND_ID='3f219bc189f941cb9ba8e81a25a60e36';
 INSERT INTO RSK_ISUP_INDICATOR VALUES('3f219bc189f941cb9ba8e81a25a60e36','单只定期开放式私募理财产品直接投资于流动性受限资产的市值在开放日不得超过该产品资产净值的20%','LIMIT_CONCENT','${LDXSXZCCCSZFCT}/${CPJZCJZ}','SUPERVISION','01,02,03','DAYTIME,DAYEND','','{"threshold_1":{"op":">=","value":"18","unit":"PERCENT","is_model":"02"}}','{"threshold_1":{"op":">=","unit":"PERCENT","is_model":"02"}}','{"threshold_1":{"op":">=","value":"20","unit":"PERCENT","is_model":"02"}}','N','','01','','system',null,sysdate,'system',sysdate,'02','','01','Y');



