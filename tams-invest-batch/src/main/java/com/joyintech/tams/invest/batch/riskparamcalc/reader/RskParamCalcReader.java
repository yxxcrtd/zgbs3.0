/*
 * 文件名：RskParamReCalcReader.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.riskparamcalc.reader;

import com.joyintech.batch.batchbase.baseclass.BaseReader;
import com.joyintech.framework.util.*;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.ocphistory.service.RskOccupyHistoryService;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskInterfDataDto;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskParamCalcReaderDto;
import com.joyintech.tams.invest.batch.riskparamcalc.dto.RskParamReCalcReaderDto;
import com.joyintech.tams.invest.batch.riskparamcalc.service.RskParamReCalcService;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 批处理-指标参数重新更新-Reader
 * 
 * @author 裴家俊
 * @version 1.0 2020年4月17日
 * @see RskParamCalcReader
 * @since 1.0
 */
@Service
@Scope(value = "prototype")
public class RskParamCalcReader extends BaseReader<RskParamReCalcReaderDto> {

    // 控制台日志
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 批处理-指标重新计算-Service
     */
    @Autowired
    private RskParamReCalcService dataService;

    @Autowired(required = false)
    private RskOccupyHistoryService rskPreOccupyInfoProService;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RskParamReCalcReaderDto> createList() {
        Map<String, Object> params = getParams();
        List<String> prodIdList = (List<String>)params.get(BatchConstant.KEY_PRODID_LIST);
        Date sysDate = (Date) params.get("sysDate");

        // 查询 T日（跑批日） 的所有预占、实占数据
        List<RskInterfDataDto> opcInfoList = dataService.getOcpInfoListToday(prodIdList, sysDate);
        // 若没有数据则处理结束
        if (CollectionUtils.isEmpty(opcInfoList)) {
            logger.info("{}日还未发生指令或交易！！！", DateUtil.format(sysDate));
            return new ArrayList<>();
        }
        // ◆解析生成预占重计算数据
        List<RskInterfDataDto> newList = new ArrayList<>();
        // 按照报文流水号分组
        Map<String, List<RskInterfDataDto>> groupMap = opcInfoList.stream().collect(
            Collectors.groupingBy(RskInterfDataDto::getSeqNo));
        // 批量报文拆分
        groupMap.forEach((key, temp) -> newList.addAll(filter(temp)));

        // 按照预占编号分组
        groupMap = newList.stream().collect(Collectors.groupingBy(
                dto -> isPreOccupy(dto.getInfCode()) ? dto.getEventId() : dto.getPreOccupyCode()));
        // 返回计算对象
        return groupMap.entrySet().stream().map(
                en -> new RskParamReCalcReaderDto(en.getKey(), en.getValue())).collect(
                Collectors.toList());
    }

    /**
     * 判断列表中是否包含目标数据
     * 
     * @param dtoList 列表
     * @param dto 目标数据
     * @return true : 包含
     */
    private boolean contains(List<RskInterfDataDto> dtoList, RskInterfDataDto dto) {
        if (CollectionUtils.isEmpty(dtoList) || dto == null) {
            return false;
        }
        // 遍历预占实占信息
        for (RskInterfDataDto item : dtoList) {
            if (StringUtil.isNotEmpty(item)
                && StringUtil.equals(dto.getOrderNo(), item.getOrderNo())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<RskInterfDataDto> filter(List<RskInterfDataDto> dtoList) {
        List<RskInterfDataDto> resList = new ArrayList<>();
        // 遍历预占实占信息
        for (RskInterfDataDto dto : dtoList) {
            // 去重
            if (contains(resList, dto)) {
                continue;
            }
            // 字符串转换报文类
            ClientData data = parseData(dto.getRequestData());
            // 接口代码
            String infCode = dto.getInfCode();
            RskInterfDataDto newDto = new RskInterfDataDto();
            BeanUtil.copyPropertiesIgnoreNull(newDto, dto);
            // 批量接口
            if (isBatchInf(infCode)) {
                // 接口转换
                infCode = convertInfCode(infCode);
                // 取得批量报文体
                List<Map> list = CommonUtil.getList(data.getBody(), "RSK_LIST");
                // 非空判断
                if (!CollectionUtils.isEmpty(list)) {
                    // 找出对应的报文体
                    Map temp = list.stream().filter(item -> StringUtil.equals(dto.getEventId(),
                        item.get("eventId"))).findFirst().orElse(null);
                    // 非空判断
                    if (!CollectionUtils.isEmpty(temp)) {
                        // 设置报文体
                        newDto.setDataMap(temp);
                        // 设置接口代码
                        newDto.setInfCode(infCode);
                        resList.add(newDto);
                    }
                }
            }
            // 非批量接口
            else {
                // 设置报文体
                newDto.setDataMap(data.getBody());
                resList.add(newDto);
            }
        }
        return resList;
    }

    /**
     * 判断接口是否批量接口
     * 
     * @param infCode 接口代码
     * @return true : 是批量接口
     */
    private boolean isBatchInf(String infCode) {
        return CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占批量接口)
               || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占批量接口)
               || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放批量接口)
               || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放批量接口);
    }

    /**
     * 是否预占接口
     * 
     * @param infCode 接口代码
     * @return true : 是预占接口
     */
    private boolean isPreOccupy(String infCode) {
        return CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占接口)
               || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放接口)
               || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占批量接口)
               || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放批量接口);
    }

    /**
     * 接口代码转换
     * 
     * @param infCode 接口代码
     * @return 转换后接口代码
     */
    private String convertInfCode(String infCode) {
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占批量接口)) {
            return BussinessDictCodeListRds.接口系统ID_预占接口.getValue();
        }
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占批量接口)) {
            return BussinessDictCodeListRds.接口系统ID_实占接口.getValue();
        }
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放批量接口)) {
            return BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue();
        }
        else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放批量接口)) {
            return BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue();
        }
        return infCode;
    }

    /**
     * 报文转换
     * 
     * @param data 报文字符串
     * @return {@link ClientData}
     */
    private ClientData parseData(String data) {
        ClientData client = new ClientData();
        Map<String, Object> map = JsonUtils.stringToMap(data);
        client.convert(map);
        return client;
    }
}
