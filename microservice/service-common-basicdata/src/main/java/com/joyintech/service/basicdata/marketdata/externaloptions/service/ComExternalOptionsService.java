package com.joyintech.service.basicdata.marketdata.externaloptions.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.basicdata.marketdata.externaloptions.dao.ComExternalOptionsMapper;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions001Dto;
import com.joyintech.service.basicdata.marketdata.externaloptions.dto.ComExternalOptions002Dto;
import com.joyintech.tams.common.base.dao.MstValOpMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2019- 12-31 10:39
 */
@Service("serviceComExternalOptionsService")
public class ComExternalOptionsService extends CommonExportService<ComExternalOptions002Dto> implements BaseProcessService<ComExternalOptions002Dto> {

    @Autowired
    ComExternalOptionsMapper comExternalOptionsMapper;

    @Autowired
    MstValOpMapper mstValOpMapper;

    public List<SuggestDto> findFinRateInfoByBenchmarkId(Map<String, Object> params) {
        return comExternalOptionsMapper.findFinRateInfoByBenchmarkId(params);
    }

    public List<SuggestDto> findTargetId(Map<String, Object> params) {
        return comExternalOptionsMapper.findTargetId(params);
    }



    public List<SuggestDto> findFinprodId(Map<String, Object> params) {
        return comExternalOptionsMapper.findFinprodId(params);
    }

    public ComExternalOptions002Dto findByPrimaryKey(Map<String, Object> map) {
        return comExternalOptionsMapper.selectByPrimaryKey(map);
    }

    public String findByBenchmarkId(String finprodId2) {
        return comExternalOptionsMapper.findByBenchmarkId(finprodId2);
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

    /**
     * 新建
     *
     * @param dto
     */
    public void insertSelective(ComExternalOptions002Dto dto) {
//        MstValFx mstValIrs = new MstValFx();
//
//        BeanUtils.copyProperties(dto, mstValIrs);
//        mstValIrs.setValDate(DateUtil.parse(dto.getValDate()));
//
//        mstValFxMapper.insertSelective(mstValIrs);
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
//        MstValFx mstValIrs = new MstValFx();
//
//        BeanUtils.copyProperties(dto, mstValIrs);
//        mstValIrs.setValDate(DateUtil.parse(dto.getValDate()));
//
//        mstValFxMapper.updateByPrimaryKeySelective(mstValIrs);
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
//        MstValFxKey key = new MstValFxKey();
//        BeanUtils.copyProperties(dto, key);
//        key.setValDate(DateUtil.parse(dto.getValDate()));
//        mstValFxMapper.deleteByPrimaryKey(key);
        MstValOpKey key = new MstValOpKey();
        BeanUtils.copyProperties(dto, key);
        key.setValDate(DateUtil.parse(dto.getValDate()));
        mstValOpMapper.deleteByPrimaryKey(key);
    }

    public List<ComExternalOptions001Dto> selectList(Map<String, Object> map) {
        return comExternalOptionsMapper.selectList(map);
    }

    /**
     * 列表查询
     * @param params
     * @return
     */
    public List<ComExternalOptions001Dto> queryAllList(Map<String, Object> params) {
        return comExternalOptionsMapper.selectList(params);
    }
    
    /**
     * 估值日期验证
     * @param finprodId2
     * @return
     */
    public ComExternalOptions002Dto dateCheck(Map<String, Object> params) {
        return comExternalOptionsMapper.dateCheck(params);
    }

    public List<ComExternalOptions002Dto> getTargetInfo(Map<String, Object> param) {
        return comExternalOptionsMapper.getTargetInfo(param);
    }
}
