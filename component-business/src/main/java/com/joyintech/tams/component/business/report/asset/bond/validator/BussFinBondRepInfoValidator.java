/*
 * 文件名：BussFinBondRepInfoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年11月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.report.asset.bond.validator;

import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.stereotype.Component;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.report.asset.bond.dto.BussFinBondRepInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 债券类资产中债报送--业务组件
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年11月17日
 * @see BussFinBondRepInfoValidator
 * @since 1.0
 */
@Component
public class BussFinBondRepInfoValidator implements BeanValidator{

    @Override
    public List<ValidMessage> valid(Object value) {
        BussFinBondRepInfoDto dto = (BussFinBondRepInfoDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        String  finprodType2  = dto.getFinprodType2();
        String  type6 = dto.getType6();
        //债券类资产、项目类资产
        if((StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())
        &&!StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
        &&!StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue()))
            ||(StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                &&StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_理财直接融资工具.getValue()))){
			if (StringUtil.isEmpty(dto.getRepAssetType())) {
				resultList.add(new ValidMessage("repAssetType",
						getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repAssetType"))));
			}
			if(StringUtil.isNotEmpty(dto.getRepAssetType())){
				 if(StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_商业性金融债.getValue())
			                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_企业债券.getValue())
			                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_公司债券.getValue())
                            // Sonar Fix modify by huwei 220608
			                //||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_公司债券.getValue())
			                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_企业债务融资工具.getValue())
			                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_资产支持证券.getValue())
			                ||StringUtil.equals(dto.getRepAssetType(),DictCodeListExt2.资产_负债二级类别_外国债券.getValue())){
			                    if(StringUtil.isEmpty(dto.getRepSectype())){
			                        resultList.add(new ValidMessage("repSectype",
			                                getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repSectype"))));
			                    }
			     }
                if(StringUtil.isEmpty(dto.getRepPartyType1())){
                    resultList.add(new ValidMessage("repPartyType1",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repPartyType1"))));
                }
                if(StringUtil.isEmpty(dto.getRepPartyType2())){
                    resultList.add(new ValidMessage("repPartyType2",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repPartyType2"))));
                }
                if(StringUtil.isEmpty(dto.getRepPartyType3())){
                    resultList.add(new ValidMessage("repPartyType3",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repPartyType3"))));
                }
                if(StringUtil.isEmpty(dto.getRepIndustry())){
                    resultList.add(new ValidMessage("repIndustry",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repIndustry"))));
                }
                if(StringUtil.isEmpty(dto.getRepIndustryLevel2())){
                    resultList.add(new ValidMessage("repIndustryLevel2",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repIndustryLevel2"))));
                }
            /*if(StringUtil.isEmpty(dto.getRepRegistOrg())){
                resultList.add(new ValidMessage("repRegistOrg",
                    getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repRegistOrg"))));
            }*/
                if(StringUtil.isEmpty(dto.getRepTradeMarket())){
                    resultList.add(new ValidMessage("repTradeMarket",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repTradeMarket"))));
                }
                if(StringUtil.equals(dto.getRepTradeMarket(), DictCodeList.交易流通场所_其他交易流通场所.getValue())){
                    if(StringUtil.isEmpty(dto.getRepRemark())){
                        resultList.add(new ValidMessage("repRemark",
                                getMessage("当交易流通场所为其他交易流通场所时，备注要素不可为空", getMessage("BussRepAssetCreditNonstdInfo.repRemark"))));
                    }

                }
                if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_机构间私募系统.getValue())){
                    if(StringUtil.isEmpty(dto.getRepRemark())){
                        resultList.add(new ValidMessage("repRemark",
                                getMessage("COMMON_E_0057", getMessage("BussRepAssetCreditNonstdInfo.repRemark"))));
                    }
                }
            }
        }
        if(StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                &&!StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股权.getValue())
                &&!StringUtil.equals(dto.getRepAssetType(), DictCodeListExt2.资产_负债二级类别_股票_一级市场.getValue()))
        {
        	if(null!=dto.getRepBankSecPerpetual() && StringUtil.equals(dto.getRepBankSecPerpetual(), DictCodeList.是否_中债_是.getValue()) &&StringUtil.isEmpty(dto.getRepSecNature())){
        		
                    resultList.add(new ValidMessage("repSecNature",
                            getMessage("COMMON_E_0057", getMessage("BussFinBondRepInfo.repSecNature"))));
                
        	}
        }
        return resultList;
    }
}
