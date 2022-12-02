/*
 * 文件名：MstYieldCurveInfoService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.basicdata.dayendaccount.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.tams.common.base.dao.SettleTransDataMapper;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.invest.basicdata.dayendaccount.controller.DayEndAccount001Controller;
import com.joyintech.tams.invest.basicdata.dayendaccount.dto.DayEndAccount002Dto;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日终清算数据核对
 * 〈功能详细描述〉
 * @author chengzhong
 * @version 1.0 2021年4月23日
 * @see DayEndAccount001Controller
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DayEndAccount002Service extends CommonExportService<DayEndAccount002Dto>
    implements BaseProcessService<DayEndAccount002Dto> {

    @Autowired
    private SettleTransDataMapper settleTransDataMapper;

    @Override
    public void doComplete(DayEndAccount002Dto dayEndAccount002Dto, OperationType operationType) {
        restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME
                + "/DayEndAccount/doComplete/" + operationType, dayEndAccount002Dto, Integer.class);
    }

    public SettleTransData findSettleTransDataById(String id) {
        return settleTransDataMapper.selectByPrimaryKey(id);
    }
}
