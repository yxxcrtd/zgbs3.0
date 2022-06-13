package com.joyintech.tams.cfets.etr.bond.service;

import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dao.WebSocketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("WebSocketService")
@Transactional
public class PositionSocketService {

    @Autowired
    private WebSocketMapper webSocketMapper;

    /**
     * 头寸余额变动同步数据
     * @param dataMap
     */
    public void insertOrUpdate(BaseMap dataMap) {
        int count = webSocketMapper.select(dataMap);
        if (count == 0){
            webSocketMapper.insert(dataMap);
        }else{
            webSocketMapper.update(dataMap);
        }
    }

    public void insertOrUpdateForFlows(List list, String tableName) {
        if (list != null && !list.isEmpty()){
            Map param = (Map) list.get(0);
            param.put("tableName",tableName);
            webSocketMapper.deleteFlows(param);
        }
        for (Object obj : list){
            BaseMap each = BaseMap.newMap((Map) obj);
            if (each != null) {
                if (each.get("WORK_FLOW_ID") == null){ each.put("WORK_FLOW_ID", ""); }
                if (each.get("TRADE_ID") == null){ each.put("TRADE_ID", ""); }
                if (each.get("ORD_ID") == null){ each.put("ORD_ID", ""); }
                if (each.get("TOTAL_TRADE_ID") == null){ each.put("TOTAL_TRADE_ID", ""); }
                if (each.get("UPBLSTM") == null){ each.put("UPBLSTM", "0"); }
                if (each.get("BUSI_ACT") == null){ each.put("BUSI_ACT", ""); }
                if (each.get("BUSI_TYPE") == null){ each.put("BUSI_TYPE", ""); }
                if (each.get("PS") == null){ each.put("PS", ""); }
                if (each.get("SYSTEM_ACT") == null){ each.put("SYSTEM_ACT", ""); }
                if (each.get("OPERATIONTYPE") == null){ each.put("OPERATIONTYPE", ""); }
                if (each.get("ATAMOUNT") == null){ each.put("ATAMOUNT", "0"); }
                if (each.get("CREATE_USER") == null){ each.put("CREATE_USER", "system"); }
                if (each.get("ENTRUST_ID") == null){ each.put("ENTRUST_ID", ""); }
                
                each.put("CDATE", each.getDate("CDATE"));
                each.put("CREATE_TIME", each.getDate("CREATE_TIME"));
                each.put("tableName", tableName);
                webSocketMapper.insertFlows(each);
            }
            
        }
    }
}
