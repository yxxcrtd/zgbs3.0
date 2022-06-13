package com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.workflow.engine.dto.ProcessEntity;

public class TrdIrsValuationDuration003Dto extends BaseEntity implements ProcessEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 产品名称
     */
    private String finprodName;

    /**
     * 收付息类型
     */
    private String irsPsIntType;

    /**
     * 交易对手
     */
    private String counterId;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getIrsPsIntType() {
        return irsPsIntType;
    }

    public void setIrsPsIntType(String irsPsIntType) {
        this.irsPsIntType = irsPsIntType;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }
}
