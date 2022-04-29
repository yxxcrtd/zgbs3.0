package com.joyintech.tams.cfets.common.servive;

import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.workflow.engine.constant.OperationType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangpeng
 * @date 2022年04月26日 18:58
 */
@Service
public class CfetsCommonOprHisHandlerAopService extends BaseService {

    /**
     * 根据输入获取更新履历表要素
     *
     * @param object 输入
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author jiangpeng
     * @date 2022/4/26 19:07
     */
    public Map<String, Object> getCfetsOprHisInfo(Object object, OperationType operationType) {
        Map<String, Object> resultMap = new HashMap<>();
        String trdId = null;
        String busiDes = null;
        String type = null;
        String workflowId = null;
        if (object != null) {
            if (object instanceof CfetsTrdBondFirstSubInfoDto) {
                CfetsTrdBondFirstSubInfoDto dto = (CfetsTrdBondFirstSubInfoDto) object;
                trdId = dto.getBidId();
                busiDes = dto.getRemark();
                workflowId = dto.getWorkflowId();
                if (operationType.equals(OperationType.NEW)) {
                    type = DictCodeListExt3.操作类型_新建.getValue();
                } else if (operationType.equals(OperationType.REVOKE)) {
                    type = DictCodeListExt3.操作类型_撤回.getValue();
                } else if (operationType.equals(OperationType.MODIFY)) {
                    type = DictCodeListExt3.操作类型_修改.getValue();
                }
            }
        }
        resultMap.put("trdId", trdId);
        resultMap.put("busiDes", busiDes);
        resultMap.put("type", type);
        resultMap.put("workflowId", workflowId);
        return resultMap;
    }
}
