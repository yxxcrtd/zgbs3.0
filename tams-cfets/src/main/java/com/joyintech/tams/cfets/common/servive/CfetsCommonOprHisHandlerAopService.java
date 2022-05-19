package com.joyintech.tams.cfets.common.servive;

import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public List<CfetsOprHisInfoRequest> getCfetsOprHisInfo(Object object, OperationType operationType) {
        Map<String, Object> resultMap = new HashMap<>();
        List<CfetsOprHisInfoRequest> cfetsOprHisInfoRequests = new ArrayList<>();
        String trdId = null;
        String busiDes = null;
        String type = null;
        String workflowId = null;
        if (object != null) {
            // 一级申购
            if (object instanceof CfetsTrdBondFirstSubInfoDto) {
                CfetsTrdBondFirstSubInfoDto entity = (CfetsTrdBondFirstSubInfoDto) object;
                trdId = entity.getBidId();
                busiDes = entity.getRemark();
                workflowId = entity.getWorkflowId();
                if (operationType.equals(OperationType.NEW)) {
                    type = DictCodeListExt3.操作类型_新建.getValue();
                } else if (operationType.equals(OperationType.REVOKE)) {
                    type = DictCodeListExt3.操作类型_撤回.getValue();
                } else if (operationType.equals(OperationType.MODIFY)) {
                    type = DictCodeListExt3.操作类型_修改.getValue();
                }
                CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(trdId);
                oprHisReq.setBusiDes(busiDes);
                oprHisReq.setOperType(type);
                oprHisReq.setWorkflowId(workflowId);
                cfetsOprHisInfoRequests.add(oprHisReq);
            }
            // 一级投标指令
            else if(object instanceof CfetsEtrBondFirstBid002Dto){
                CfetsEtrBondFirstBid002Dto entity = (CfetsEtrBondFirstBid002Dto) object;
                if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondFirstBidDtos())) {
                    for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                        trdId = dto.getEntrustId();
                        busiDes = entity.getRemark();
                        workflowId = entity.getWorkflowId();
                        if (operationType.equals(OperationType.NEW)) {
                            type = DictCodeListExt3.操作类型_新建.getValue();
                        } else if (operationType.equals(OperationType.REVOKE)) {
                            type = DictCodeListExt3.操作类型_撤回.getValue();
                        } else if (operationType.equals(OperationType.MODIFY)) {
                            type = DictCodeListExt3.操作类型_修改.getValue();
                        }
                        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                        oprHisReq.setEntrustId(trdId);
                        oprHisReq.setBusiDes(busiDes);
                        oprHisReq.setOperType(type);
                        oprHisReq.setWorkflowId(workflowId);
                        cfetsOprHisInfoRequests.add(oprHisReq);
                    }
                }

            }
        }
        return cfetsOprHisInfoRequests;
    }
}
