package com.joyintech.tams.valuation.batch.batchunistart.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.dao.SysBatchTaskMapper;
import com.joyintech.framework.base.entity.SysBatchJob;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.dao.SysParaMapper;
import com.joyintech.system.base.entity.SysPara;
import com.joyintech.system.base.entity.SysParaKey;
import com.joyintech.tams.common.base.dao.SysBatchUnistartGrpconfigMapper;
import com.joyintech.tams.common.base.entity.SysBatchUnistart;
import com.joyintech.tams.common.base.entity.SysBatchUnistartDetail;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.batch.batchunistart.dao.BatchUnistartMapper;
import com.joyintech.tams.valuation.batch.batchunistart.entity.BatchUnistartDto;
import com.joyintech.tams.valuation.batch.batchunistart.entity.BatchUnistartGrpconfig;
import com.joyintech.tams.valuation.batch.batchunistart.service.BatchUnistartCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Scope("prototype")
public class BatchUnistartProcessor extends BaseProcessor<NeedOperateEntity, BatchUnistartDto> {

    @Autowired
    private BatchUnistartMapper batchUnistartMapper;

    @Autowired
    private SysParaMapper sysParaMapper;

    @Autowired
    private SysBatchTaskMapper sysBatchTaskMapper;

    @Autowired
    private SysBatchUnistartGrpconfigMapper sysBatchUnistartGrpconfigMapper;

    @Autowired
    private BatchUnistartCommonService batchUnistartCommonService;

    @Override
    public BatchUnistartDto process(NeedOperateEntity item) throws UnsupportedEncodingException {
        // 读取需要处理的任务模块
        SysParaKey key = new SysParaKey();
        key.setSystemCode("SYS");
        key.setParaCode("BATCH_UNISTART_CONFIG");
        SysPara sysPara = batchUnistartCommonService.selectSysPara(key);

        // 如果未指定配置，使用默认配置DEFAULT
        String configId = "DEFAULT";
        if (sysPara != null && !StringUtil.isEmpty(sysPara.getParamValue())) {
            configId = sysPara.getParamValue();
        }

        List<BatchUnistartGrpconfig> grpConfigList = batchUnistartMapper.findGrpconfigById(configId);
        List<BatchUnistartGrpconfig> grpConfigs = new ArrayList<>();
        for (BatchUnistartGrpconfig entity : grpConfigList) {
            if (DictCodeListExt3.批处理任务组类型_日切.getValue().equals(entity.getGrpType())
                && DictCodeList.是否_否.getValue().equals(this.getBatchExecuteDetail().getOtherParam1())) {
                continue;
            }
            //add by yjp ZGBSEI-4 批处理统一启动_不日切不跑日切的批 20210729 start
            if (DictCodeListExt3.批处理任务组类型_日切.getValue().equals(entity.getGrpType())
                    && StringUtil.equals(this.getBatchExecuteInfo().getTaskId(),"999998")) {
                continue;
            }
            //add by yjp ZGBSEI-4 批处理统一启动_不日切不跑日切的批 20210729 end
            /* 统一批处理 监控页面 名称显示优化 shengqiqiang start */
/*            List<SysBatchJob> jobList = batchUnistartCommonService.selectBatchJob(entity.getBatchgrpSeqNo());
            if (CollectionUtils.isEmpty(jobList)) {
                entity.setSystemCode("999");
            }
            else {
                String systemCode = batchUnistartCommonService.getBatchTaskList(
                    jobList.get(0).getTaskId()).getSystemCode();
                entity.setSystemCode(systemCode);
            }*/

            if(entity.getBatchgrpSeqNo().equals("inv1")){
                entity.setSystemCode("INV");
            }else if (entity.getBatchgrpSeqNo().equals("inv2")){
                entity.setSystemCode("INV");
            }else if (entity.getBatchgrpSeqNo().equals("inv3")){
                entity.setSystemCode("INV");
            }else if (entity.getBatchgrpSeqNo().equals("val")){
                entity.setSystemCode("VAL");
            }else if (entity.getBatchgrpSeqNo().equals("dateChange")){
                entity.setSystemCode("INV");
            }else if (entity.getBatchgrpSeqNo().equals("rep")){
                entity.setSystemCode("SVR");
            }else if (entity.getBatchgrpSeqNo().equals("dis1")){
                entity.setSystemCode("DIS");
            }else if (entity.getBatchgrpSeqNo().equals("dis2")){
                entity.setSystemCode("DIS");
            }else  {
                entity.setSystemCode("999");
            }

            /* 统一批处理 监控页面 名称显示优化 shengqiqiang end */
            grpConfigs.add(entity);
        }
        BatchUnistartDto dto = new BatchUnistartDto();
        List<SysBatchUnistartDetail> list = new ArrayList<>();
        SysBatchUnistart sysBatchUnistart = new SysBatchUnistart();

        // 产品组ID
        String prdGrpSeqNo = this.getBatchExecuteDetail().getPrdGroupSeqNo();

        Long orderNo = 1l;
        String seqno = IDGenerator.generate();
        sysBatchUnistart.setSeqNo(seqno);
        sysBatchUnistart.setTaskName(this.getBatchExecuteDetail().getDetailName());
        sysBatchUnistart.setPrdGroupSeqNo(prdGrpSeqNo);
        sysBatchUnistart.setStatus(BatchDictCodeList.批处理执行状态_未执行.getValue());
        sysBatchUnistart.setBusiStartDate(this.getBatchExecuteDetail().getStartDate());
        sysBatchUnistart.setBusiEndDate(this.getBatchExecuteDetail().getEndDate());
        sysBatchUnistart.setRemark(this.getBatchExecuteDetail().getDetailName());
        sysBatchUnistart.setCreateUser(this.getBatchExecuteDetail().getCreateUser());
        sysBatchUnistart.setUpdateUser(this.getBatchExecuteDetail().getCreateUser());

        Long maxCount = grpConfigs.size() * (
            DateUtil.getBetweenDayNumber(sysBatchUnistart.getBusiStartDate(), sysBatchUnistart.getBusiEndDate()) + 1l);
        Date date = sysBatchUnistart.getBusiStartDate();
        while (!date.after(sysBatchUnistart.getBusiEndDate())) {
            for (BatchUnistartGrpconfig entity : grpConfigs) {
                // 手动跑批时，不执行日切批
                if (DictCodeListExt3.批处理任务组类型_日切.getValue().equals(entity.getGrpType())
                    && DictCodeList.是否_否.getValue().equals(this.getBatchExecuteDetail().getOtherParam1())) {
                    continue;
                }
                //add by yjp ZGBSEI-4 批处理统一启动_不日切不跑日切的批 20210729 start
                if (DictCodeListExt3.批处理任务组类型_日切.getValue().equals(entity.getGrpType())
                        && StringUtil.equals(this.getBatchExecuteInfo().getTaskId(),"999998")) {
                    continue;
                }
                //add by yjp ZGBSEI-4 批处理统一启动_不日切不跑日切的批 20210729 end
                SysBatchUnistartDetail e = new SysBatchUnistartDetail();
                e.setSeqNo(seqno);
                e.setDetailSeqNo(IDGenerator.generate());
                if (DictCodeListExt3.批处理任务组类型_T1日.getValue().equals(entity.getGrpType())) {
                    // 类型为T1日传参T+1日
                    e.setBusiDate(DateUtil.addDay(date, 1));
                }
                else {
                    // 类型为T日和日切传参T日
                    e.setBusiDate(date);
                }
                e.setSystemCode(entity.getSystemCode());
                e.setBatchgrpSeqNo(entity.getBatchgrpSeqNo());
                e.setPrdGroupSeqNo(prdGrpSeqNo);
                e.setOrderNo(BigDecimal.valueOf(orderNo++));
                String detailName = subStrByBytes(this.getBatchExecuteDetail().getDetailName(), 150);
                e.setRemark(
                    e.getOrderNo() + "/" + maxCount + "-" + DateUtil.format(e.getBusiDate(), "yyyyMMdd") + "-"
                    + getSysName(e.getSystemCode(), entity.getGrpType()) + "-" + detailName);
                e.setStatus(BatchDictCodeList.批处理执行状态_未执行.getValue());
                e.setCreateUser(this.getBatchExecuteDetail().getCreateUser());
                e.setUpdateUser(this.getBatchExecuteDetail().getCreateUser());
                list.add(e);
            }
            date = DateUtil.addDay(date, 1);
        }
        dto.setSysBatchUnistart(sysBatchUnistart);
        dto.setSysBatchUnistartDetailList(list);
        dto.setProdId(sysBatchUnistart.getTaskName());
        return dto;
    }

    private String getSysName(String systemCode, String grpType) {
        if (DictCodeListExt3.批处理任务组类型_日切.getValue().equals(grpType)) {
            return "日切";
        }
        DictCodeListExt3 sysName = DictCodeListExt3.from(DictCode.批处理系统代码, systemCode);
        if (sysName != null) {
            return sysName.getCodeName();
        }
        else {
            return "其他";
        }
    }

    public String subStrByBytes(String s, int num) throws UnsupportedEncodingException {
        if (StringUtil.isEmpty(s)) {
            return "";
        }
        int len = s.getBytes("UTF-8").length;
        if (len > num) {
            s = s.substring(0, s.length() - 1);
            s = subStrByBytes(s, num);
        }
        return s;
    }

}
