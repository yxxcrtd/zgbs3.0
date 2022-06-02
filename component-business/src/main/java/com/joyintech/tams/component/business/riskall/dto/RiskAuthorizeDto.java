package com.joyintech.tams.component.business.riskall.dto;

import com.joyintech.tams.common.base.entity.RskAuthorizeMng;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;

public class RiskAuthorizeDto extends RskAuthorizeMng {

    /**
     * 是否超授权
     */
    private boolean authorizeLimit;

    public String getRskStatus(){
        return authorizeLimit ? "超授权" : "通过";
    }

    public String getTradeMarketStr(){
        return SysDatadictUtil.getNameFromDataDict(DictCode.交易场所,super.getTradeMarket());
    }

    public String getRolePosStr(){
        return SysDatadictUtil.getNameFromDataDict(DictCode.角色岗位,super.getRolePos());
    }

    public String getAssetTypeStr(){
        return SysDatadictUtil.getNameFromDataDict(DictCode.授权类型一级,super.getAssetType());
    }

    public String getAssetSecTypeStr(){
        return SysDatadictUtil.getNameFromDataDict(DictCode.授权类型二级,super.getAssetSecType());
    }

    public boolean isAuthorizeLimit() {
        return authorizeLimit;
    }

    public void setAuthorizeLimit(boolean authorizeLimit) {
        this.authorizeLimit = authorizeLimit;
    }
}
