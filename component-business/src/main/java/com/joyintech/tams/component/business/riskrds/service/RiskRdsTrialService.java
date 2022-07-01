/*
 * 文件名：RiskRdsTrialService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年9月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.riskrds.service;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.tams.invest.common.client.ClientData;

/**
 * 风险试算Service
 * 
 * @author zhangrui1
 * @version 1.0 2020年9月19日
 * @see RiskRdsTrialService
 * @since 1.0
 */
@Service
public class RiskRdsTrialService {

    @Autowired
    private RiskRouterService routerService;

    public <T> ClientData sendList(String pageId, String infCode, List<T> dtoList,
                                   Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return null;
        }
        if (null != consumer) {
            dtoList.forEach(t -> {
                if (null != t) {
                    consumer.accept(t);
                }
            });
        }
        return routerService.executeClient(infCode, pageId, dtoList, null);
    }
}
