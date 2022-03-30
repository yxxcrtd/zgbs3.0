package com.joyintech.tams.valuation.batch.common.log.dto;
/*
 * @ClassName DetailBatchLogExpand
 * @Description TODO
 * @Date 2022/3/30 11:34
 * @Author 殷亚
 * @Version 1.0
 **/

public  class DetailBatchLogExpand {
    /*
     *业务代码
     **/
    @DetailLogConfig(description="业务代码")
    private String busiId;
    /*
     *业务类型
     **/
    @DetailLogConfig(description="业务类型")
    private String busiType;
    /*
     *业务明细代码
     **/
    @DetailLogConfig(description="业务明细代码")
    private String bokDetailId;
    /*
     *交易代码
     **/
    @DetailLogConfig(description="交易代码")
    private String tradeId;
    /*
     *账务场景代码
     **/
    @DetailLogConfig(description="账务场景代码")
    private String sceneId;
    /*
     *执行结果
     **/
    @DetailLogConfig(description="执行结果",show = false)
    private String result;

    public String getBusiId() {
        return busiId;
    }

    public void setBusiId(String busiId) {
        this.busiId = busiId;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getBokDetailId() {
        return bokDetailId;
    }

    public void setBokDetailId(String bokDetailId) {
        this.bokDetailId = bokDetailId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
