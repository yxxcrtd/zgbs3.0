package com.joyintech.tams.invest.batch.prebatchcheck.tenderbond.service;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.batch.prebatchcheck.common.dto.SysPrebatchResultDto;
import com.joyintech.tams.invest.batch.prebatchcheck.common.service.PrebatchCheckCommonService;
import com.joyintech.tams.invest.batch.prebatchcheck.tenderbond.dao.FinTenderBondPreMapper;
import com.joyintech.tams.invest.common.prebatch.dto.SysPrebatchResultDetail;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动上市流通批前检查
 *
 * @author gwl
 * @date 2022-04-19
 */
@Service
public class FinTenderBondPreService {
    @Resource
    private FinTenderBondPreMapper finTenderBoundPreMapper;
    @Resource
    private PrebatchCheckCommonService prebatchCheckCommonService;

    /**
     * 卡批
     *
     * @param batchExecuteInfo
     * @param dto
     * @param instCode
     */
    public void execute1(SysBatchExecInfo batchExecuteInfo, SysPrebatchResultDto dto, String instCode) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtil.isNotEmpty(instCode)) {
            param.put("PRE_INST_CODE", instCode);
        }
        param.put("checkDate", dto.getCheckDate());
        List<SysPrebatchResultDetail> details = new ArrayList<>();
        param.put("listingDate", dto.getCheckDate());
        param.put("issueStage", DictCodeListExt4.发行阶段_已发行.getValue());
        param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("ISSUE_STATUS_02", DictCodeListExt1.发行状态_发行失败.getValue());
        List<SysPrebatchResultDetail> details1 = finTenderBoundPreMapper.selectPreList(param);
        if (!StringUtil.isEmptyList(details1)) {
            details.addAll(details1);
        }
        if (!StringUtil.isEmptyList(details)) {
            details.forEach(item -> {
                item.setResultSeqNo(dto.getSeqNo());
                item.setBatchSeqNo(batchExecuteInfo.getBatchExedetailSeqNo());
                item.setTaskId(batchExecuteInfo.getTaskId());
                item.setSeqNo(IDGenerator.generate());
                item.setCreateUser(dto.getCreateUser());
                item.setUpdateUser(dto.getUpdateUser());
            });
        }
        prebatchCheckCommonService.insertBatchResultDetail(dto, details);
    }

    /**
     * 不卡批
     *
     * @param batchExecuteInfo
     * @param dto
     * @param instCode
     */
    public void execute2(SysBatchExecInfo batchExecuteInfo, SysPrebatchResultDto dto, String instCode) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtil.isNotEmpty(instCode)) {
            param.put("PRE_INST_CODE", instCode);
        }
        param.put("checkDate", dto.getCheckDate());
        List<SysPrebatchResultDetail> details = new ArrayList<>();
        param.put("listingDate", dto.getCheckDate());
        param.put("issueStage", DictCodeListExt4.发行阶段_已发行.getValue());
        param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("ISSUE_STATUS_02", DictCodeListExt1.发行状态_发行失败.getValue());
        List<SysPrebatchResultDetail> details1 = finTenderBoundPreMapper.selectPreList(param);
        if (!StringUtil.isEmptyList(details1)) {
            details.addAll(details1);
        }
        if (!StringUtil.isEmptyList(details)) {
            details.forEach(item -> {
                item.setResultSeqNo(dto.getSeqNo());
                item.setBatchSeqNo(batchExecuteInfo.getBatchExedetailSeqNo());
                item.setTaskId(batchExecuteInfo.getTaskId());
                item.setSeqNo(IDGenerator.generate());
                item.setCreateUser(dto.getCreateUser());
                item.setUpdateUser(dto.getUpdateUser());
            });
        }
        prebatchCheckCommonService.insertBatchResultDetail(dto, details);
    }


}
