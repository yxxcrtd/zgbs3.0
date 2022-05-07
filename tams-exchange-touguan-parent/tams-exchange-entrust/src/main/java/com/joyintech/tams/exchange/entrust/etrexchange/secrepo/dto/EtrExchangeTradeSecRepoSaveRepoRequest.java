/*
 * 文件名：EtrExchangeTradeDto001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.validator.EtrExchangeTradeSecRepo001Validator;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *  交易信息 （左边下）
 * @author meifaxin
 * @version 1.0 2020年7月2日
 * @see EtrExchangeTradeSecRepoSaveRepoRequest
 * @since 1.0
 */
@PageId("etrExchangeTradeRepo")
@ValidLogic(targetClass = EtrExchangeTradeSecRepo001Validator.class , groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
@ApiModel(value="指令下单表单",description="指令下单表单")
public class EtrExchangeTradeSecRepoSaveRepoRequest extends EtrExchangeTradeSaveSecRepoRequest{
}
