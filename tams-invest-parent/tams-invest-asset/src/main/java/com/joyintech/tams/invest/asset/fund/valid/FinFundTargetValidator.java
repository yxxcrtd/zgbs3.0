/*
 * 文件名：FinFundTargetValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年9月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.fund.valid;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.asset.fund.dto.FinFundTargetDto;
import com.joyintech.tams.invest.asset.fund.service.FinFundTargetService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.service.InvDataCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author wangzehui
 * @version 1.0 2018年9月13日
 * @see FinFundTargetValidator
 * @since 1.0
 */
@Component
public class FinFundTargetValidator implements BeanValidator {
    /**
     * 共通接口
     */
    @Autowired
    private CommonBusinessService commonBusinessService;
    @Autowired
    private FinFundTargetService finFundTargetService;

    @Autowired
    private InvDataCheckService chbInvDataCheckService;

    /**
     * 校验
     * @param value 页面数据
     * @return 校验结果
     */
    public List<ValidMessage> valid(Object value) {
        FinFundTargetDto dto = (FinFundTargetDto)value;
        List<ValidMessage> resultList = new ArrayList<ValidMessage>();

        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            List<FinFundTargetDto> list = dto.getSelectedItem();
            // 判断是否选中数据
            if (list != null && list.size()>0) {
                for (FinFundTargetDto mst : list) {
                    //判断是否存在交易
                    if(null != commonBusinessService){
                        if(commonBusinessService.isTransInAsset(mst.getFinprodId())>0){
                            resultList.add(
                                    new ValidMessage(this.getMessage("BUSS_COM_E_0043")));
                        }
                    }

                }
            }
        }
        else if(StringUtil.equals(dto.getOperateType(), "save")||StringUtil.equals(dto.getOperateType(), "submit")){
            if(StringUtil.equals(dto.getCreditFlag(), DictCodeList.是否_是.getValue())
                    && (dto.getActualCreditProvid() == null || StringUtil.equals(dto.getActualCreditProvid(), ""))){
                resultList.add(
                        new ValidMessage("actualCreditProvid",this.getMessage("FW_E_0006", getMessage("FinPlanAssetC002.actualCreditProvid"))));
            }
            if(!DictCodeList.基金类资产类型_货币基金.getValue().equals(dto.getFundType())){
                /* update  by zhuyongkang 20191009 start*/
//                if(dto.getSecondType() == null || "".equals(dto.getSecondType())){
//                    resultList.add(
//                        new ValidMessage("secondType",this.getMessage("FW_E_0006", getMessage("FinFundTarget001.secondType"))));
//                }
                /* update  by zhuyongkang 20191009 end*/
            }else{
                if (dto != null && dto.getBussFinIntPayDto() != null) {
                    if (StringUtil.equals(dto.getBussFinIntPayDto().getPayCycle(), DictCodeList.收益结转频率_按月结转.getValue())) {
                        if(!StringUtil.isNotEmpty(dto.getIsCompoundInt())){
                            resultList.add(new ValidMessage("isCompoundInt",this.getMessage("FW_E_0006", getMessage("FinFundTarget001.isCompoundInt"))));
                        }
                    }
                }
            }
            if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                /*String finprodMarketId = dto.getFinprodMarketId();
                String reg = "(?i)^(?!([a-z]*|\\d*)$)[a-z\\d]+$";
                //判断资产代码是否只由数字和字母组成
                if(!finprodMarketId.matches(reg)){
                    resultList.add(
                        new ValidMessage("资产代码必须由数字和字母组成"));
                }*/
                //通过资产代码查询数据，验证资产代码的唯一性
                Map<String,Object> map = new HashMap<String,Object>();
                if(dto.getFinprodMarketId() != null && !"".equals(dto.getFinprodMarketId())){
                    map.put("finprodMarketId", dto.getFinprodMarketId());
                    FinFundTargetDto param = finFundTargetService.findEntity(map);
                    if(param != null){
                        resultList.add(
                                new ValidMessage(this.getMessage("BUSS_COM_I_0017", getMessage("FinFundTarget001.finprodMarketId")
                                        +"("+dto.getFinprodMarketId()+")")));
                    }
                }
                if((StringUtil.equals(dto.getType10(), BussinessDictCodeList.行内资产三级分类_混合基金.getValue())
                        ||StringUtil.equals(dto.getType10(),BussinessDictCodeList.行内资产三级分类_基金中基金.getValue()))
                        &&StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_ETF.getValue())){
                    resultList.add(new ValidMessage("trmFundType",
                            getMessage("FinFundTarget002.trmFundType01", getMessage("FinFundTarget002.trmFundType"))));
                }
                // add wsz 20220601 修改前端验证问题 ZGCPCN-1046 公募基金标的新建画面，无法新建场外货币基金 start
                if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        ||StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                    if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(), dto.getFundType())&&
                            !(StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_ETF.getValue())||
                                    StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue()))){
                        resultList.add(new ValidMessage("trmFundType",
                                getMessage("FinFundTarget002.trmFundType02", getMessage("FinFundTarget002.trmFundType"))));
                    }
                }
                // add wsz 20220601 修改前端验证问题 ZGCPCN-1046 公募基金标的新建画面，无法新建场外货币基金 end
                /*if(list.size()>0){
                    resultList.add(
                        new ValidMessage(this.getMessage("BUSS_COM_E_0006", getMessage("MstFeeVarieties002.feeName"))));
                }*/
            }
            else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
                if((StringUtil.equals(dto.getType10(), BussinessDictCodeList.行内资产三级分类_混合基金.getValue())
                        ||StringUtil.equals(dto.getType10(),BussinessDictCodeList.行内资产三级分类_基金中基金.getValue()))
                        &&StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_ETF.getValue())){
                    resultList.add(new ValidMessage("trmFundType",
                            getMessage("FinFundTarget002.trmFundType01", getMessage("FinFundTarget002.trmFundType"))));
                }
                // add wsz 20220601 修改前端验证问题 ZGCPCN-1046 公募基金标的新建画面，无法新建场外货币基金 start
                if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        ||StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                    if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(), dto.getFundType()) &&
                            !(StringUtil.equals(dto.getTrmFundType(), DictCodeListExt2.交易所基金类型_ETF.getValue()) ||
                                    StringUtil.equals(dto.getTrmFundType(), DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue()))) {
                        resultList.add(new ValidMessage("trmFundType",
                                getMessage("FinFundTarget002.trmFundType02", getMessage("FinFundTarget002.trmFundType"))));
                    }
                }
                // add wsz 20220601 修改前端验证问题 ZGCPCN-1046 公募基金标的新建画面，无法新建场外货币基金 end
                // 获取原来的数据。
                String finprodMarketId = dto.getFinprodMarketId();
                String oldFinprodMarketId = dto.getOldFinprodMarketId();

                if(StringUtil.isNotEmpty(dto.getFinprodMarketId())  && !StringUtil.equals(finprodMarketId,oldFinprodMarketId)){
                    //如果不相同  则去校验新基金id 是否有重复。。。
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("finprodMarketId", dto.getFinprodMarketId());
                    FinFundTargetDto param = finFundTargetService.findEntity(map);
                    if(param != null){
                        resultList.add(
                                new ValidMessage(this.getMessage("BUSS_COM_I_0017", getMessage("FinFundTarget001.finprodMarketId")
                                        +"("+dto.getFinprodMarketId()+")")));
                    }
                }
            }
//            if(dto.getBussRepFundInfoDto() != null){
//                if(dto.getBussRepFundInfoDto().isGovInvest()){
//                    if( "".equals(dto.getBussRepFundInfoDto().getRepGovInvestFor()) || null == dto.getBussRepFundInfoDto().getRepGovInvestFor()){
//                        resultList.add(new ValidMessage(
//                            getMessage("COMMON_E_0057", getMessage("BussRepFundInfo.repGovInvestFor"))));
//                    }
//                }
//            }
            // 分红日只能是1-31的整数值
            if(dto.getBussFinIntPayDto() != null){
                if(StringUtil.isNotEmpty(dto.getBussFinIntPayDto().getDayValue())) {
                    if (dto.getBussFinIntPayDto().getDayValue().length() > 2) {
                        resultList.add(new ValidMessage("bussFinIntPayDto.dayValue", getMessage("FIN_ASSET_E_0030")));
                    } else {
                        Pattern pattern = Pattern.compile("[0-9]*");
                        if (!pattern.matcher(dto.getBussFinIntPayDto().getDayValue()).matches()) {
                            resultList.add(new ValidMessage("bussFinIntPayDto.dayValue", getMessage("FIN_ASSET_E_0030")));
                        } else {
                            int i = Integer.valueOf(dto.getBussFinIntPayDto().getDayValue());
                            if(i>31 || i<1){
                                resultList.add(new ValidMessage("bussFinIntPayDto.dayValue", getMessage("FIN_ASSET_E_0030")));
                            }
                        }
                    }
                }
            }
            //判断流程中是否存在相同的数据
            /*WflValidInputParameter param = new WflValidInputParameter();
            param.setBusinessDataPk1(dto.getFinprodMarketId());
            param.setBusinessDataPk2(dto.getFinprodType2Str());
            param.setPageId("FinFundTarget002");
            if(null != mstWorkflowCommonService){
                if(mstWorkflowCommonService.isInWflProcess(param)){
                    resultList.add(
                        new ValidMessage("市场代码："+dto.getFinprodMarketId()+"已存在"));
                }
            }*/
            //场内基金变更
            //add by zhangnian 20191105 start
            if(StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())
                    ||StringUtil.equals(dto.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                if(StringUtil.isEmpty(dto.getTrmFundAbbr())){
                    resultList.add(new ValidMessage("trmFundAbbr",
                            getMessage("COMMON_E_0057", getMessage("FinFundTarget002.trmFundAbbr"))));
                }
                if(StringUtil.isEmpty(dto.getTrmFundType())){
                    resultList.add(new ValidMessage("trmFundType",
                            getMessage("COMMON_E_0057", getMessage("FinFundTarget002.trmFundType"))));
                }
                if((StringUtil.equals(dto.getType10(), BussinessDictCodeList.行内资产三级分类_混合基金.getValue())
                        ||StringUtil.equals(dto.getType10(),BussinessDictCodeList.行内资产三级分类_基金中基金.getValue()))
                        &&StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_ETF.getValue())){
                    resultList.add(new ValidMessage("trmFundAbbr",
                            getMessage("FinFundTarget002.trmFundType01", getMessage("FinFundTarget002.trmFundType"))));
                }
            }
            if(StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_分级基金.getValue())){
                if(StringUtil.isEmpty(dto.getGraFundType())){
                    resultList.add(new ValidMessage("graFundType",
                            getMessage("COMMON_E_0057", getMessage("FinFundTarget002.graFundType"))));
                }
            }
            if(StringUtil.equals(dto.getTrmFundType(),DictCodeListExt2.交易所基金类型_ETF.getValue())){
                if(StringUtil.isEmpty(dto.getPurRedId())){
                    resultList.add(new ValidMessage("purRedId",
                            getMessage("COMMON_E_0057", getMessage("FinFundTarget002.purRedId"))));
                }
                if(StringUtil.isEmpty(dto.getPurRedAbbr())){
                    resultList.add(new ValidMessage("purRedAbbr",
                            getMessage("COMMON_E_0057", getMessage("FinFundTarget002.purRedAbbr"))));
                }
                if(null==dto.getEtfMinPrunit()){
                    resultList.add(new ValidMessage("etfMinPrunit",
                            getMessage("COMMON_E_0057", getMessage("FinFundTarget002.etfMinPrunit"))));
                }
            }
            //add by zhangnian 20191105 end
            if (StringUtil.isNotEmpty(dto.getBussRepFundInfoDto())){
                resultList = chbInvDataCheckService.chbCheckCommon(resultList, value, null, dto.getBussRepFundInfoDto().getRepAssetType());
            }
        }
        // 是否展示资产四五级分类
        if (StringUtil.equals(CacheUtil.getSysParam("IS_SHOW_TYPE"), "01") &&  (StringUtil.equals(dto.getOperateType(), "save") ||  StringUtil.equals(dto.getOperateType(), "submit"))) {
            if (StringUtil.isEmpty(dto.getType11())) {
                resultList.add(new ValidMessage("type11",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.type11"))));
            }
            if (StringUtil.isEmpty(dto.getType12())) {
                resultList.add(new ValidMessage("type12",
                        getMessage("COMMON_E_0057", getMessage("FinBondTargetAsset002.type12"))));
            }
        }
        //add by yjp 20210507 TISHKBS-2101 到期日<发行日/起息日，可正常提交，没有校验 start
        if (dto != null && dto.getVdate() != null && dto.getMdate() != null) {
        	 if (DateUtil.compareDate(dto.getVdate(),
        			 dto.getMdate()) > 0) {
                 ValidMessage validMessage = new ValidMessage("mdate",
                         getMessage("到期日不可小于发行日期/成立日期"));
                 resultList.add(validMessage);
             }
        }
        if (dto != null && dto.getIdate() != null && dto.getMdate() != null) {
       	 if (DateUtil.compareDate(dto.getIdate(),
       			 dto.getMdate()) > 0) {
                ValidMessage validMessage = new ValidMessage("mdate",
                        getMessage("到期日不可小于发行日期/成立日期"));
                resultList.add(validMessage);
            }
       }
        //add by yjp 20210507 TISHKBS-2101 到期日<发行日/起息日，可正常提交，没有校验 end
        if(StringUtil.equals(dto.getOpenType(),DictCodeList.开放类型_封闭式.getValue())){
            if(StringUtil.isEmpty(dto.getIsSevenDayCashable())){
                resultList.add(new ValidMessage("isSevenDayCashable",
                        getMessage("COMMON_E_0057", getMessage("FinFundTarget002.isSevenDayCashable"))));
            }
        }
        return resultList;
    }

}
