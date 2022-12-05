package com.joyintech.tams.common.basicdata.marketdata.externaloptions.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.MstValOpMapper;
import com.joyintech.tams.common.base.entity.MstValOp;
import com.joyintech.tams.common.base.entity.MstValOpKey;
import com.joyintech.tams.common.basicdata.constant.ServiceApplication;
import com.joyintech.tams.common.basicdata.marketdata.externaloptions.dao.ComExternalOptionsMapper;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.common.basicdata.marketdata.externaloptions.dto.ComExternalOptions002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2019- 12-31 11:35
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ComExternalOptions002Service extends CommonExportService<ComExternalOptions002Dto> implements BaseProcessService<ComExternalOptions002Dto> {

    @Autowired
    ComExternalOptionsMapper comExternalOptionsMapper;

    @Autowired
    MstValOpMapper mstValOpMapper;

    public List<SuggestDto> getFinprodId(Map<String, Object> params) {

        return comExternalOptionsMapper.findFinRateInfoByBenchmarkId(params);
    }


    public ComExternalOptions002Dto findByPrimaryKey(Map<String, Object> map)
    {
        String str = DictCodeListExt3.基准类型_2_合约标的.getValue(); //07
        //System.out.println(str);
        map.put("BENCHMARK_TYPE2",str);
        return comExternalOptionsMapper.selectByPrimaryKey(map);
    }

    @Override
    public void doComplete(ComExternalOptions002Dto entity, OperationType type) {
        if (OperationType.NEW.equals(type)) {
            this.insertSelective(entity);
        } else if (OperationType.MODIFY.equals(type)) {
            this.updateByPrimaryKeySelective(entity);
        } else if (OperationType.DELETE.equals(type)) {
            this.deleteByPrimaryKey(entity);
        }
    }


    public String findByBenchmarkId(String finprodId2)
    {
        String str = new String();
        return comExternalOptionsMapper.findByBenchmarkId(finprodId2);
    }
    
    public ComExternalOptions002Dto dateCheck(Map<String, Object> params){
        return comExternalOptionsMapper.dateCheck(params);
    }

    public ComExternalOptions002Dto getTargetInfo(Map<String, Object> param) {
        List<ComExternalOptions002Dto> externalOptions002DtoList = restTemplate.postForList(ServiceApplication.CommonBasicDataService + "/ComExternalOptions/getTargetInfo",
                param, ComExternalOptions002Dto.class);
        if (!StringUtil.isEmptyList(externalOptions002DtoList)){
            return externalOptions002DtoList.get(0);
        }
        return null;
    }

    /**
     * 新建
     *
     * @param dto
     */
    public void insertSelective(ComExternalOptions002Dto dto) {
        MstValOp mstValOp = new MstValOp();
        BeanUtils.copyProperties(dto, mstValOp);
        mstValOp.setValDate(DateUtil.parse(dto.getValDate()));
        mstValOp.setPublishDate(DateUtil.parse(dto.getPublishDate()));
        mstValOpMapper.insert(mstValOp);
    }

    /**
     * 修改
     *
     * @param dto
     */
    public void updateByPrimaryKeySelective(ComExternalOptions002Dto dto) {
        MstValOp mstValOp = new MstValOp();
        BeanUtils.copyProperties(dto, mstValOp);
        mstValOp.setValDate(DateUtil.parse(dto.getValDate()));
        mstValOp.setPublishDate(DateUtil.parse(dto.getPublishDate()));
        mstValOpMapper.updateByPrimaryKeySelective(mstValOp);
    }

    /**
     * 删除
     *
     * @param dto
     */
    public void deleteByPrimaryKey(ComExternalOptions002Dto dto) {
        MstValOpKey key = new MstValOpKey();
        BeanUtils.copyProperties(dto, key);
        key.setValDate(DateUtil.parse(dto.getValDate()));
        mstValOpMapper.deleteByPrimaryKey(key);
    }
}
