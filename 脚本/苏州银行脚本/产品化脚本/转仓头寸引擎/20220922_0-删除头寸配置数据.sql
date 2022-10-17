delete  from   PZX_CFG_FLOW         WHERE FLOWID  IN ( SELECT ndexpr FROM PZX_CFG_DECISION_TREE pcdt WHERE rtcode='Oth001' AND ndtype='position' )           ;
delete  from   PZX_CFG_DECISION_TREE    WHERE RTCODE ='Oth001';
delete  from   PZX_CFG_FLOW_LIB_APP     WHERE FLIBCODE IN (select FLIBCODE from   PZX_CFG_FLOW_LIB        WHERE MSGCODE  ='Oth001') ;  
delete  from   PZX_CFG_FLOW_LIB_CODE    WHERE FLIBCODE IN (select FLIBCODE from   PZX_CFG_FLOW_LIB        WHERE MSGCODE  ='Oth001') ;  
delete  from   PZX_CFG_FLOW_LIB_CUSTOM  WHERE FLIBCODE IN (select FLIBCODE from   PZX_CFG_FLOW_LIB        WHERE MSGCODE  ='Oth001') ;  
delete  from   PZX_CFG_FLOW_LIB         WHERE MSGCODE  ='Oth001';
delete  from   PZX_CFG_IN_PARAM         WHERE MSGCODE ='Oth001'     ;
DELETE  FROM PZX_CFG_INTERFACE WHERE MSGCODE ='Oth001';
