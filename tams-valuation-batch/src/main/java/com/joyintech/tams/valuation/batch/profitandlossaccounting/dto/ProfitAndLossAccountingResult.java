/*
 * 文件名：ProfitAndLossAccountingResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.profitandlossaccounting.dto;

import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;

import java.io.Serializable;
import java.util.List;

public class ProfitAndLossAccountingResult extends BookGenerateResult implements Serializable{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 620810151647580998L;
    public ProfitAndLossAccountingResult(){

    }
    public ProfitAndLossAccountingResult(List<ValBokBookdetailBatch> list){
        super.setValBokBookdetailBatch(list);
    }
}
