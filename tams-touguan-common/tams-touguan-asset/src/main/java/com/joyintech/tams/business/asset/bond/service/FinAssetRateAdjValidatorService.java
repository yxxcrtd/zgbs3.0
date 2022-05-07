/*
 * 文件名：FinAssetRateAdjDtoValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.business.asset.bond.service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.FinAssetRateAdjResponse;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.asset.common.service.AssetCommonService;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 利率调整业务验证
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2022年4月19日
 * @see FinAssetRateAdjValidatorService
 * @since 1.0
 */
@Component
public class FinAssetRateAdjValidatorService implements BeanValidator{

    @Autowired
    private IPlatformDate iPlatformDate;
    
    @Autowired
    private FinBondAsset002Service finBondAsset002Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidMessage> valid(Object value) {
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();
        FinAssetRateAdjResponse dto = (FinAssetRateAdjResponse)value;
        Set<Date> set = new  HashSet<Date>();Set<String> setForChannel = new  HashSet<String>();
        if(StringUtil.equals(dto.getCouponSpecies(), "01")){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("finprodId", dto.getFinprodId());
            map.put("cashType", DictCodeListExt1.现金流二级类型_利息.getValue());
            AssetCommonInfoDto assetCommonInfoDto  =  finBondAsset002Service.getAssetInfoToRateAdj(map);
            if(null!=assetCommonInfoDto){
                if(StringUtil.equals(assetCommonInfoDto.getIntType(), DictCodeList.利率类型_浮动利率.getValue())){
                    /*resultList.add(new ValidMessage("",
                        getMessage("FIN_ASSET_E_0028")));*/
                }
                if(StringUtil.equals(assetCommonInfoDto.getCouponSpecies(), DictCodeList.息票品种_贴现.getValue())
                    ||StringUtil.equals(assetCommonInfoDto.getCouponSpecies(), DictCodeList.息票品种_零息.getValue())
                || StringUtil.equals(assetCommonInfoDto.getCouponSpecies(), DictCodeList.息票品种_利随本清.getValue())){
                    resultList.add(new ValidMessage("",
                        getMessage("FIN_ASSET_E_0029")));
                }
                return resultList;
            }
        }
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
            for(FinAssetRateAdjResponse entity:dto.getSelectedItem()){
                if(null==entity.getEffDate()){
                    resultList.add(new ValidMessage("effDate",
                        getMessage("COMMON_E_0057", getMessage("FinAssetRateAdj001.effDate"))));
                }
                if(null==entity.getYield()){
                    if(StringUtil.equals(entity.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_通道.getValue())){
                        resultList.add(new ValidMessage("yield",
                            getMessage("COMMON_E_0057", getMessage("FinAssetRateAdj001.rate"))));
                    }else{
                        resultList.add(new ValidMessage("yield",
                            getMessage("COMMON_E_0057", getMessage("FinAssetRateAdj001.loadRate"))));
                    }
                }
                if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_通道.getValue())){
                  //生效日不可重复
                    if(!setForChannel.add(entity.getFeeId()+DateUtil.format(entity.getEffDate()))){
                        resultList.add(new ValidMessage("",
                            getMessage("同一个费用生效日不可重复")));
                    }
                    //费用名称不能为空
                    // 修改费用名称不能为空的判断条件 by 赵东东 2019.10.30
                    if(!StringUtil.isNotEmpty(entity.getFeeId())){
                        resultList.add(new ValidMessage("feeId",
                                getMessage("COMMON_E_0057", getMessage("BussFinProductFeeInfo.feeName"))));
                    }
                }else{
                  //生效日不可重复
                    if(!set.add(entity.getEffDate())){
                        resultList.add(new ValidMessage("",
                            getMessage("FIN_ASSET_E_0014")));
                    }
                }
                //针对可编辑data参与判断
                if (!StringUtil.equals(entity.getUpdateFlg(), "02")) {
                    // 新维护的利率调整数据的生效日不能小于平台日期
                    if (null != entity.getEffDate()) {
                        if (DateUtil.compareDate(entity.getEffDate(), iPlatformDate.getSysDate()) == -1) {
                        	//modify by wm 由于FIN_ASSET_E_0015标签在其他jar中已定义，故将FIN_ASSET_E_0015修改为FIN_ASSET_E_0033 start 
//                            resultList.add(new ValidMessage("", getMessage("FIN_ASSET_E_0015")));
                            resultList.add(new ValidMessage("", getMessage("FIN_ASSET_E_0033")));
                            //modify by wm 由于FIN_ASSET_E_0015标签在其他jar中已定义，故将FIN_ASSET_E_0015修改为FIN_ASSET_E_0033 start 
                            return resultList;
                        }
                        // add by leipeng 20190626 【债券类资产】：利率调整生效日大于债券的到期日没有做时间判断
                        //add 活期存款到期日非必填，添加非空判断
//                        AssetCommonInfoDto basedto = assetCommonService.getMdateByFinproId(dto.getFinprodId());
                        AssetCommonInfoDto basedto = finBondAsset002Service.getMdateByFinproId(dto.getFinprodId());
                        if (StringUtil.isNotEmpty(basedto) && DateUtil.compareDate(entity.getEffDate(), basedto.getMdate()) == 1) {
                            resultList.add(new ValidMessage("", getMessage("FIN_ASSET_E_0019")));
                        }
                    }

                }
            }
        }
        return resultList;
    }

}
