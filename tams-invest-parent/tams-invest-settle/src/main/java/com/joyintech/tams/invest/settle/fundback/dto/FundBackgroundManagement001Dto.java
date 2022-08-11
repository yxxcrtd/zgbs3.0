package com.joyintech.tams.invest.settle.fundback.dto;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.dto.settle.fundback.FundBackgroundManagementDto;

/**
 * @program: tams-invest-parent
 * @description: 资金后台管理用dto
 * @author: 强成西
 * @create: 2018-11-15 16:51
 **/
public class FundBackgroundManagement001Dto extends FundBackgroundManagementDto {
    /**
	 * 发送状态 20200317 add
	 */
	private String sendStatusStr;

    /**
     * 债券资产交易场所
     */
    private String tradeMarket;

    public String getStatusValidString() {
        return SysDataUtil.getNameFromDataDict(DictCode.画面数据状态, getStatusValid());
    }

    public String getPaBussTypeString() {
        if (DictCodeListExt1.平安业务类型_产品强减.getValue().equals(super.getPaBussType())) {
            return SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs());
        }
        else if (DictCodeListExt2.平安业务类型_股票交易.getValue().equals(super.getPaBussType())) {
            return "股票交易" + "(" + SysDataUtil.getNameFromDataDict(DictCode.交易方向, super.getPs()) + ")";
        }
        else if (DictCodeListExt2.平安业务类型_现金分红.getValue().equals(super.getPaBussType())
        || DictCodeListExt2.平安业务类型_送转.getValue().equals(super.getPaBussType())
        || DictCodeListExt2.平安业务类型_分红送转.getValue().equals(super.getPaBussType())
        ) {
            return "股票交易" + "(" + SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, getPaBussType()) + ")";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.平安业务类型, getPaBussType());
    }

    public String getOperateTypeString() {
        return SysDataUtil.getNameFromDataDict(DictCode.指令状态, getOperateType());
    }

    public String getPayTypeString() {
    	// 一体化对应etliu start
//        return SysDataUtil.getNameFromDataDict(DictCode.划款类型, getPayType());
    	return SysDataUtil.getNameFromDataDict(DictCode.平安划款类型, getPayType());
     // 一体化对应etliu end
    }

    public String getManagerString() {
        return CacheUtil.getAppParamCache(SystemGlobalConstant.SYS_USER_KEY, super.getManager());
    }
    
    public String getCcyString(){
    	return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }
	
	public String getSendStatusStr() {
		return StringUtil.isNotEmpty(super.getSendStatus()) 
				? SysDataUtil.getNameFromDataDict(DictCode.发送状态 , super.getSendStatus()) : "" ;
	}

    @Override
    public String getTradeMarket() {
        return tradeMarket;
    }

    @Override
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
