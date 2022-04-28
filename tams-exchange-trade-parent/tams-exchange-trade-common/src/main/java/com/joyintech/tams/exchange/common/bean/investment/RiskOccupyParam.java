package com.joyintech.tams.exchange.common.bean.investment;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * create By MAXX on 2020/5/12
 */
public class RiskOccupyParam {
    private List<ReqRiskOccupy> rskList;
    private String assetType;
    private String eventType;

    public List<ReqRiskOccupy> getRskList() {
        return rskList;
    }

    public void setRskList(List<ReqRiskOccupy> rskList) {
        this.rskList = rskList;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("rskList", rskList)
                .add("assetType", assetType)
                .add("eventType", eventType)
                .toString();
    }
}
