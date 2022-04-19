package com.joyintech.tams.cfets.trd.bond.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.tams.cfets.trd.bond.dto.EntrustForcedFinishDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 强制完成流程处理
 * @author zznn
 * @date 2021/12/23
 */
@Service
public class EntrustForcedFinishService extends BaseService<EntrustForcedFinishDto> implements BaseProcessService<EntrustForcedFinishDto> {

    @Autowired
    private IbuQuoteBondService ibuQuoteBondService;

    @Override
    public void doComplete(EntrustForcedFinishDto entity, OperationType operationType){
        Map<String,Object> param = new HashMap<>();
        param.put("entrustId",entity.getEntrustId());
        param.put("remark",entity.getRemark());
        param.put("loginUserId", entity.getLoginUserId());
        try{
            ibuQuoteBondService.doForcedFinish(param);
        }catch (Exception e){
            throw new BusinessException("MSG:指令强制完成审批异常!");
        }
    }
}

