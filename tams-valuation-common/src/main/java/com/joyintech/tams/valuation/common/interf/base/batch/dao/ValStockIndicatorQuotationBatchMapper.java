/*
 * 文件名：ValBokBookdetailCurrentBatchMapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年7月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.common.interf.base.batch.dao;

import com.joyintech.tams.common.base.entity.MstIndicatorQuotation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValStockIndicatorQuotationBatchMapper {

    int insertBatch(List<MstIndicatorQuotation> list);
    int deleteBatch(List<MstIndicatorQuotation> list);

}
