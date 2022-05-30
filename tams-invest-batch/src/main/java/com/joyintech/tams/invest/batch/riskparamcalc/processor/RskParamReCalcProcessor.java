/*
 * 文件名：RskParamReCalcProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.riskparamcalc.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskIndicatorParaCalcdetail;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskInterfDataDto;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskParamReCalcReaderDto;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskParamReCalcWriterDto;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.riskinfrds.base.service.CalcOccupyInfoService;
import com.joyintech.tams.invest.common.riskinfrds.occupy.entity.OccupyRelParam;


/**
 * 批处理-指标参数重新更新-Processor
 * 
 * @author 裴家俊
 * @version 1.0 2020年4月17日
 * @see RskParamReCalcProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RskParamReCalcProcessor extends BaseProcessor<NeedOperateEntity, RskParamReCalcWriterDto> {

    /**
     * 预占信息计算Service
     */
    @Autowired
    private CalcOccupyInfoService calcService;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public RskParamReCalcWriterDto process(NeedOperateEntity arg0)
        throws Exception {
        RskParamReCalcReaderDto dto = (RskParamReCalcReaderDto)arg0;
        // 取得实占信息数据
        List<RskInterfDataDto> dataList = dto.getDataList();
        // 初始化结果
        RskParamReCalcWriterDto resDto = new RskParamReCalcWriterDto();
        // 空值时处理结束并返回空的结果
        if (StringUtil.isEmptyList(dataList)) {
            return resDto;
        }
        // 初始化计算明细列表
        List<RskIndicatorParaCalcdetail> details = new ArrayList<>();
        // 从参数取平台日期
        String sysDate = DateUtil.format((Date)getParams().get("sysDate"),
            DateUtil.DEFAULT_OTHER_FORMAT);
        // 初始化预占实占信息列表
        Map<String, OccupyRelParam> ocpMap = new HashMap<>();
        // 初始化预占实占索引
        Set<String> ocpSet = new HashSet<>();
        // 初始化预占报文数据
        Map<String, Object> mapData = new HashMap<>();
        calcService.startTransaction();
        // 遍历执行
        dataList.forEach(data -> {
            // 接口代码
            String infCode = data.getInfCode();
            // 报文
            Map<String, Object> temp = data.getDataMap();
            // 设置平台日期
            temp.put("sysDate", sysDate);
            // 设置UUID
            temp.put("uuid", data.getOrderNo());
            // 事件编号
            String eventId = data.getEventId();
            // 处理预占实占：预占占用多少，实占最终占用多少（交易生效的占用-交易撤销的释放）
            if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
                // 执行生成预占实占信息（生效交易）
                ocpMap.put(eventId, calcService.doOccupy(eventId, temp));
                // 记录实占索引
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
                    ocpSet.add(eventId);
                }
                // 记录预占报文
                else {
                    mapData.putAll(temp);
                }
            }
            // 处理释放：有撤销交易的记录，处理上方【执行生成预占实占信息】，做扣减
            else {
                calcService.releaseOccupy(ocpMap.get(eventId), temp);
            }
        });
        // 预占实占信息存在
        if (!CollectionUtils.isEmpty(ocpMap)) {
            // 预占信息
            OccupyRelParam preOcpInfo = ocpMap.get(dto.getPreOccupyCode());
            // 有实占的场合
            if (!CollectionUtils.isEmpty(ocpSet)) {
                // 执行预占 - 实占
                ocpSet.forEach(ocp -> {
                    OccupyRelParam ocpInfo = ocpMap.get(ocp);
                    calcService.subtractOccupy(preOcpInfo, ocpInfo);
                });
            }
            // 风险计算并返回结果
            Map<String, Object> response = calcService.calcAndSave(preOcpInfo, mapData);
            // 调用计算逻辑
            Object dtlObj = response.get("PARA_VAL_DETAILS");
            if (dtlObj != null) {
                details.addAll((List<RskIndicatorParaCalcdetail>)dtlObj);
            }
            // 参数值计算流水信息
            resDto.setParaCalList(details);
        }
        calcService.commit();
        return resDto;
    }

}
