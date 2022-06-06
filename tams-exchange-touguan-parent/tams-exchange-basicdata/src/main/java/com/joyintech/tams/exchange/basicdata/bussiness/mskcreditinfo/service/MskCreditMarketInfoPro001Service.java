package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dao.MskCreditInfoProMapper;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MskCreditMarketInfoPro001Service extends BaseService<MskCreditInfoProSaveRequest> implements BaseProcessService<MskCreditInfoProSaveRequest>{


    @Autowired(required = false)
    MskCreditInfoProMapper mskCreditInfoProMapper;

    public Page<MskCreditInfoProSaveRequest> selectForPage(MskCreditInfoProRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        pageParam.put("secIdList", StringUtil.splitToList(null, request.getSecid()));
        pageParam.put("valueSourceStr", request.getValueSource());
        pageParam.put("search_isNumberFlag", BaseMap.isNumeric(request.getSearch()));
        if (CollectionUtils.isNotEmpty(request.getDate())) {
            String cdateStart = request.getDate().get(0);
            if (StringUtil.isNotEmpty(cdateStart)){
                pageParam.put("cdateStart",cdateStart);
            }
            String cdateEnd = request.getDate().get(1);
            if (StringUtil.isNotEmpty(cdateEnd)){
                pageParam.put("cdateEnd",cdateEnd);
            }
        }
        Page<MskCreditInfoProSaveRequest> page
                =  super.queryForPage(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),
                () -> mskCreditInfoProMapper.queryForPage(pageParam));
        return page;

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


    @Override
    public void doInsert(MskCreditInfoProSaveRequest entity, OperationType type) {
        if(OperationType.NEW == type){
            this.insert(entity);
        }
    }

    @Override
    public void doUpdate(MskCreditInfoProSaveRequest entity) {

    }

    @Override
    public void doDelete(String workflowId) {

    }

    @Override
    public void doComplete(MskCreditInfoProSaveRequest mskCreditInfoProSaveRequest, OperationType operationType) {
        if (OperationType.DELETE == operationType) {
            this.delete(mskCreditInfoProSaveRequest);
        }else if(OperationType.MODIFY == operationType){
            this.update(mskCreditInfoProSaveRequest);
        }
    }

    private void update(MskCreditInfoProSaveRequest mskCreditInfoProSaveRequest) {
        mskCreditInfoProMapper.updateByPrimaryKeySelective(mskCreditInfoProSaveRequest);
    }

    private void insert(MskCreditInfoProSaveRequest mskCreditInfoProSaveRequest) {
        mskCreditInfoProMapper.insertSelective(mskCreditInfoProSaveRequest);
    }

    private void delete(MskCreditInfoProSaveRequest mskCreditInfoProSaveRequest) {
        mskCreditInfoProMapper.deleteByPrimaryKey(mskCreditInfoProSaveRequest);
    }

    @Override
    public void doBack(MskCreditInfoProSaveRequest entity) {

    }

    public boolean isInWflProcess(WflValidInputParameter parameter) {
        parameter.setSeachMode("01");
        List<String> result = mskCreditInfoProMapper.getBusinessData(parameter);
        if (CollectionUtils.isNotEmpty(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public MskCreditInfoProSaveRequest getBasicInfos(String secid, Object o) {
        MskCreditInfoProSaveRequest dto = new MskCreditInfoProSaveRequest();
        Map<String,Object> map= new HashMap<>();
        map.put("secid",secid);
        return mskCreditInfoProMapper.getBasicInfo(map);
    }

    public Page<SelectOptionDto> getFinprodMarketInfo(MskCreditInfoProSaveRequest request) {
        Map<String, Object> pageParam = BeanUtil.object2Map(request);
        return this.queryForPageForSelectReturn(request.getPageMode().getPageNo(), request.getPageMode().getPageSize(),() -> mskCreditInfoProMapper.getFinprodMarketInfo(pageParam));
    }
}
