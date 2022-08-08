package com.joyintech.tams.cfets.trd.repo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.tams.cfets.trd.repo.dto.RepoEntrustForcedFinishDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 强制完成流程处理
 *
 * @author jiangpeng
 * @date 2022/01/10
 */
@Service
public class RepoEntrustForcedFinishService extends BaseService<RepoEntrustForcedFinishDto> implements BaseProcessService<RepoEntrustForcedFinishDto> {

    @Autowired
    private IbuQuoteRepoService ibuQuoteRepoService;

    @Override
    public void doComplete(RepoEntrustForcedFinishDto entity, OperationType operationType) {
        Map<String, Object> param = new HashMap<>();
        param.put("entrustId", entity.getEntrustId());
        param.put("remark", entity.getRemark());
        param.put("loginUserId", entity.getLoginUserId());
        try {
            ibuQuoteRepoService.doForcedFinish(param);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("needNextCheck", false);
        return variables;
    }
}

