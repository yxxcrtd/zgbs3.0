package com.joyintech.tams.invest.common.risktrialrds.dto;

import java.util.LinkedList;
import java.util.List;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.tams.invest.common.risktrialrds.dict.SupervisionStat;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;

/**
 * 禁止项内容解析
 * @author 裴家俊
 * @version 1.0 2018年11月30日
 * @see ForbidPointThreshold
 * @since 1.0
 */
public class ForbidPointThreshold {

	private String value;
	private List<String> forbidList;
	private String fbModel;
	private static final String sep = ",";
	
	public ForbidPointThreshold(String value,String fbModel) {
		this.value = value;
		this.fbModel=fbModel;
		String[] items = this.value.split(sep);
		forbidList = new LinkedList<String>();
		for(String item : items) {
			item = item.trim();
			if(item.length() > 0) {
				forbidList.add(item);
			}
		}
	}

	public String getValue() {
		return value;
	}

	public String getFbModel() {
		return fbModel;
	}

	@Override
	public String toString() {
		return new StringBuffer().append(forbidList)
				.toString().replace(GlobalConstant.OPEN_MIDDLE, "").replace(GlobalConstant.CLOSE_MIDDLE, "");
	}
	
	public SupervisionStat check(String valueStr) {
		String[] valueAry=null;
		valueStr = valueStr.equals("是")?"Y":valueStr.equals("否")?"N":valueStr;
		valueAry=valueStr.trim().split(sep);
		if("Y".equals(fbModel)) {
			for (String value : valueAry) {
				for (String forbidItem : forbidList) {
					if (forbidItem.trim().equals(Const.BrankLowName)) {
						for (String lowRank : Const.BrankLowValue) {
							if (value.equals(lowRank)) {
								return SupervisionStat.VIOLATION;
							}
						}
					} else if (forbidItem.trim().equals(Const.NoLowName)) {
						if (value == null || value.equals("")) {
							return SupervisionStat.VIOLATION;
						}
					} else {
						//if(value.indexOf(forbidItem) >= 0) {
						if (value.equals(forbidItem)) {
							return SupervisionStat.VIOLATION;
						}
					}
				}
			}
			return SupervisionStat.NORMAL;
		}else{
			for (String value : valueAry) {
				boolean fbFlag=false;
				for (String forbidItem : forbidList) {
					if (forbidItem.trim().equals(Const.BrankLowName)) {
						for (String lowRank : Const.BrankLowValue) {
							if (value.equals(lowRank)) {
								fbFlag=true;
								break;
							}
						}
					} else if (forbidItem.trim().equals(Const.NoLowName)) {
						if (value == null || value.equals("")) {
							fbFlag=true;
							break;
						}
					} else {
						if (value.equals(forbidItem)) {
							fbFlag=true;
							break;
						}
					}
				}
				if(!fbFlag){
					return SupervisionStat.VIOLATION;
				}
			}
			return SupervisionStat.NORMAL;
		}
	}
}
