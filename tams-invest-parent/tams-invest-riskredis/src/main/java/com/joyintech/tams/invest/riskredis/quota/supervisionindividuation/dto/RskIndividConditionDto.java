package com.joyintech.tams.invest.riskredis.quota.supervisionindividuation.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.RskIndividCondition;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;

public class RskIndividConditionDto extends RskIndividCondition{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getConditionItemStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.条件项, super.getConditionItem());
	}
	public String getControlTypeStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.控制类型, super.getControlType());
	}
	public String getDateStrStr(){
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.日期选项, super.getDateStr());
	}
	public String getDateEndStr(){
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.日期选项, super.getDateEnd());
	}
	public String getDateUnitStrStr(){
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.日期类型_信批, super.getDateUnitStr());
	}
	public String getDateUnitEndStr(){
		return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.日期类型_信批, super.getDateUnitEnd());
	}
}
