package com.joyintech.tams.invest.risk.quota.rskspecificassettype.validate;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.risk.quota.rskspecificassettype.dto.RskSpecificAssetTypeDto;
import com.joyintech.tams.invest.risk.quota.rskspecificassettype.service.RskSpecificAssetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RskSpecificAssetTypeValidator implements BeanValidator {

	@Autowired
	private RskSpecificAssetTypeService rskSpecificAssetTypeService;
	@Override
	public List<ValidMessage> valid(Object value) {
		List<ValidMessage> resultList = new ArrayList<>();
		RskSpecificAssetTypeDto dto = (RskSpecificAssetTypeDto) value;
		String operateType = dto.getOperateType();
		if((StringUtil.equals(dto.getOperateType(), "save")|| StringUtil.equals(dto.getOperateType(), "submit"))){
			if(StringUtil.isEmpty(dto.getFinprodId())
			&&StringUtil.isEmpty(dto.getType1())){
				resultList.add(new ValidMessage("资产代码或者资产一级类型必须选择其一"));
			}
			//重复性验证
			Map<String,Object> repeatMap = new HashMap<>();
			repeatMap.put("specificId",dto.getSpecificId());
			//往上
			if(StringUtil.isNotEmpty(dto.getFinprodId())){
				repeatMap.put("finprodId",dto.getFinprodId());
				repeatMap.put("repeatCondition","01");
				if(rskSpecificAssetTypeService.getRepeatCount(repeatMap)>0){
					resultList.add(new ValidMessage("资产代码重复，无法提交!"));
					return resultList;
				}
				// 资产代码作为主键
				dto.setSpecificId(dto.getFinprodId());
				if (rskSpecificAssetTypeService.checkBusinessDataInFlow(dto)){
					resultList.add(new ValidMessage("该资产代码存在流程中数据，无法提交!"));
					return resultList;
				}
			}
			else{
				repeatMap.put("repeatCondition","02");
				if(StringUtil.isNotEmpty(dto.getType4())){
					repeatMap.put("type1",dto.getType1());
					repeatMap.put("type2",dto.getType2());
					repeatMap.put("type3",dto.getType3());
				}else if(StringUtil.isNotEmpty(dto.getType3())){
					repeatMap.put("type1",dto.getType1());
					repeatMap.put("type2",dto.getType2());
				}else if(StringUtil.isNotEmpty(dto.getType2())){
					repeatMap.put("type1",dto.getType1());
				}
				if(rskSpecificAssetTypeService.getRepeatCount(repeatMap)>0){
					resultList.add(new ValidMessage("已存在相同级别授权，无法提交!"));
					return resultList;
				}
				//相同
				repeatMap.put("repeatCondition","02");
				repeatMap.put("type1",dto.getType1());
				repeatMap.put("type2",dto.getType2());
				repeatMap.put("type3",dto.getType3());
				repeatMap.put("type4",dto.getType4());
				if(rskSpecificAssetTypeService.getRepeatCount(repeatMap)>0){
					resultList.add(new ValidMessage("已存在相同级别授权，无法提交!"));
					return resultList;
				}
			}
		}
		return resultList;
	}
}
