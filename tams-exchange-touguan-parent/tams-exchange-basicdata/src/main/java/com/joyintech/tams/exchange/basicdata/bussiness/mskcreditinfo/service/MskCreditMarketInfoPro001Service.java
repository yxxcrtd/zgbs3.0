package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.service;

import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dao.MskCreditInfoProMapper;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MskCreditMarketInfoPro001Service extends CommonExportService<MskCreditInfoProSaveRequest> implements BaseProcessService<MskCreditInfoProSaveRequest>{


    @Autowired(required = false)
    MskCreditInfoProMapper mskCreditInfoProMapper;

    public Page<MskCreditInfoProSaveRequest> selectForPage(MskCreditInfoProRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        pageParam.put("secIdList", StringUtil.splitToList(null, request.getSecid()));
        pageParam.put("valueSouceStr", request.getValueSource());
        String cdateStart = request.getCdateStart();
        if (StringUtil.isNotEmpty(cdateStart)){
            pageParam.put("cdateStart",cdateStart);
        }
        String cdateEnd = request.getCdateEnd();
        if (StringUtil.isNotEmpty(cdateEnd)){
            pageParam.put("cdateEnd",cdateEnd);
        }
        Page<MskCreditInfoProSaveRequest> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> mskCreditInfoProMapper.queryForPage(pageParam));
        return page;

    }

    @Override
    public void doComplete(MskCreditInfoProSaveRequest mskCreditInfoProSaveRequest, OperationType operationType) {

    }

    @Override
    public String start(ProcessParameter<MskCreditInfoProSaveRequest> params, MskCreditInfoProSaveRequest... entities) throws WorkFlowException {
        return null;
    }

    @Override
    public MskCreditInfoProSaveRequest getBussinessEntity(ProcessMark processMark) {
        return null;
    }

    @Override
    public MskCreditInfoProSaveRequest getBussinessEntityByWorkflowId(String workflowId) {
        return null;
    }

    @Override
    public String start(WorkFlowParameter<MskCreditInfoProSaveRequest> params, MskCreditInfoProSaveRequest... entities) throws WorkFlowException {
        return null;
    }

    @Override
    public void doInsert(MskCreditInfoProSaveRequest entity, OperationType type) {
        if(StringUtil.isNotEmpty(entity.getMyield())){
            entity.setMyield(entity.getMyield().divide(new BigDecimal(100)));
        }
        mskCreditInfoProMapper.insert(entity);
    }

    @Override
    public void doUpdate(MskCreditInfoProSaveRequest entity) {
        mskCreditInfoProMapper.updateByPrimaryKey(entity);
    }

    @Override
    public void doDelete(String workflowId) {

    }

    @Override
    public void doBack(MskCreditInfoProSaveRequest entity) {

    }

    @Override
    public ProcessMark getBusinessInfo(String workflowId) {
        return null;
    }

    @Override
    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
        return null;
    }

    @Override
    public BaseProcessService<MskCreditInfoProSaveRequest> getMyBusinessService() {
        return null;
    }

    @Override
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        return null;
    }

    @Override
    public String checkForSubmitMulti(List<FlowEntity> flowEntityList, boolean isMutiApproval) {
        return null;
    }

    @Override
    public String checkForSubmitMessage(FlowEntity entity, boolean isMutiApproval) {
        return null;
    }

    @Override
    public String generateTitle(MskCreditInfoProSaveRequest entity, OperationType type) {
        return null;
    }

    @Override
    public boolean checkBusinessDataInFlow(MskCreditInfoProSaveRequest dto) {
        return false;
    }

    @Override
    public MskCreditInfoProSaveRequest getBussinessEntityByPk(ProcessMark param) {
        return null;
    }

    @Override
    public void doRevoke(MskCreditInfoProSaveRequest entity) {

    }

    public List<MskCreditInfoProSaveRequest> selectAllList(Map<String, Object> params) {
        return mskCreditInfoProMapper.select(params);
    }


    public MskCreditInfoProSaveRequest selectByKey(String secId, String valueDate, String valueSource) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("secId", secId);
        map.put("valueDate", valueDate);
        map.put("valueSource", valueSource);
        return mskCreditInfoProMapper.selectByKey(map);
    }

}
