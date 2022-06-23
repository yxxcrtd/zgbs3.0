package com.joyintech.tams.valuation.model.common.rateinfo.service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ModAssetGradeConfig;
import com.joyintech.tams.common.base.entity.ModAssetGradeSpecialConfig;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.dto.modelDto.ModAssetInfoDto;
import com.joyintech.tams.valuation.model.common.rateinfo.dao.RateInfoCommonMapper;
import com.joyintech.tams.valuation.model.common.rateinfo.dto.ViewRatingInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 模型用的评级结果查询共通
 */
@Service
public class RateInfoCommonService {

    @Autowired
    RateInfoCommonMapper rateInfoCommonMapper;

    @Autowired
    ImpairmentCalculationCommonService impairmentCalculationCommonService;

    Logger logger = LoggerFactory.getLogger(RateInfoCommonService.class);

    /**
     *
     * @param modAssetInfoDto
     * @param modAssetGradeConfig
     * @return
     */
    public ViewRatingInfoDto getRatingInfoByTypeConfig(ModAssetInfoDto modAssetInfoDto, ModAssetGradeConfig modAssetGradeConfig, Date queryDate){
        ViewRatingInfoDto viewRatingInfoDto = new ViewRatingInfoDto();
        //评级类型:主体、债项、最高、最低、发行人、融资人、担保人、存借款机构、交易对手
        String oType = modAssetGradeConfig.getoType();
        //评级机构类型
        String gradeOrgType = modAssetGradeConfig.getGradeOrgType();
        //评级机构
        String gradeOrgId = modAssetGradeConfig.getGradeOrgId();

        viewRatingInfoDto = getRateInfo(modAssetInfoDto,oType,gradeOrgType,gradeOrgId,queryDate);

        return viewRatingInfoDto;
    }

    /**
     *
     * @param modAssetInfoDto
     * @param modAssetGradeSpecialConfig
     * @param queryDate
     * @return
     */
    public ViewRatingInfoDto getRatingInfoBySpecialConfig(ModAssetInfoDto modAssetInfoDto, ModAssetGradeSpecialConfig modAssetGradeSpecialConfig, Date queryDate){
        ViewRatingInfoDto viewRatingInfoDto = new ViewRatingInfoDto();
        //评级类型:主体、债项、最高、最低、发行人、融资人、担保人、存借款机构、交易对手
        String oType = modAssetGradeSpecialConfig.getoType();
        //评级机构类型
        String gradeOrgType = modAssetGradeSpecialConfig.getGradeOrgType();
        //评级机构
        String gradeOrgId = modAssetGradeSpecialConfig.getGradeOrgId();

        viewRatingInfoDto = getRateInfo(modAssetInfoDto,oType,gradeOrgType,gradeOrgId,queryDate);

        return viewRatingInfoDto;
    }



    /**
     * 查询指定参数条件下符合条件的评级信息
     *  查询[评级日期]小于或者等于[参数.查询日期]，(评级)
     * @param modAssetInfoDto
     *  资产信息
     * @param otype
     *  评级类型
     * @param gradeOrgType
     *  评级机构类型：01最新；02指定
     * @param gradeorgid
     *  评级机构
     * @param queryDate
     *  查询日期
     * @return
     */
    private ViewRatingInfoDto getRateInfo(ModAssetInfoDto modAssetInfoDto, String otype, String gradeOrgType, String gradeorgid, Date queryDate){
        String sectype = modAssetInfoDto.getSecType();
        String ocode = null;
        ViewRatingInfoDto resultDto = null;
        // 定义 变量.主体是否持有金融牌照
        String isFinLicence = null;
        //债券的处理逻辑
        if (DictCodeListExt1.证券品种_债券.getValue().equals(sectype)) {
            //评级主体代码设置为债券市场代码
            ocode=modAssetInfoDto.getFinprodMarketId();
            if (ModelDictCodeList.模型_评级类型_债项评级.getValue().equals(otype)) {
                isFinLicence = DictCodeList.是否_否.getValue();
                resultDto =  getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
            }
            else if (ModelDictCodeList.模型_评级类型_主体评级.getValue().equals(otype)) {
                isFinLicence = modAssetInfoDto.getIssueFinLicence();
                //交易数据.最新评级结果 = 交易数据.主体评级
                resultDto =  getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
            }
            else if (ModelDictCodeList.模型_评级类型_较高值.getValue().equals(otype)) {
                otype = ModelDictCodeList.模型_评级类型_债项评级.getValue();
                ViewRatingInfoDto viewRatingInfoSec = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                // 债项时，是否含有金融牌照 = 否
                if(viewRatingInfoSec != null){
                    viewRatingInfoSec.setIsFinLicence(DictCodeList.是否_否.getValue());
                }
                //交易数据.最新评级结果 = 交易数据.主体评级
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoIssuer = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                if(viewRatingInfoIssuer != null){
                    // 主体取对应发行人是否含有金融牌照
                    viewRatingInfoIssuer.setIsFinLicence(modAssetInfoDto.getIssueFinLicence());
                }
                resultDto =  impairmentCalculationCommonService.maxRating(viewRatingInfoSec,viewRatingInfoIssuer);
                return resultDto;
            }
            else if (ModelDictCodeList.模型_评级类型_较低值.getValue().equals(otype)) {
                otype = ModelDictCodeList.模型_评级类型_债项评级.getValue();
                ViewRatingInfoDto viewRatingInfoSec = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                // 债项时，是否含有金融牌照 = 否
                if(viewRatingInfoSec != null){
                    viewRatingInfoSec.setIsFinLicence(DictCodeList.是否_否.getValue());
                }
                //交易数据.最新评级结果 = 交易数据.主体评级
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoIssuer = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                if(viewRatingInfoIssuer != null){
                    // 主体取对应发行人是否含有金融牌照
                    viewRatingInfoIssuer.setIsFinLicence(modAssetInfoDto.getIssueFinLicence());
                }
                resultDto =  impairmentCalculationCommonService.minRating(viewRatingInfoSec,viewRatingInfoIssuer);
                return resultDto;
            }else{
                logger.warn("评级类型不支持，无法查询评级信息.资产代码为："+modAssetInfoDto.getFinprodId()+"，评级类型为："+otype);
                return null;
            }
        }
        else if (DictCodeListExt1.证券品种_其他债权类.getValue().equals(sectype)) {
            if (ModelDictCodeList.模型_评级类型_发行人.getValue().equals(otype)) {
                // 交易数据.买入时点评级结果 = 发行人外部评级结果
                ocode=modAssetInfoDto.getIssuer();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                resultDto =  getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                isFinLicence = modAssetInfoDto.getIssueFinLicence();
            }
            else if (ModelDictCodeList.模型_评级类型_融资人.getValue().equals(otype)) {
                // 交易数据.买入时点评级结果 = 融资人外部评级结果
                ocode=modAssetInfoDto.getFinancier();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                resultDto =  getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                isFinLicence = modAssetInfoDto.getFinancierFinLicence();
            }
            else if (ModelDictCodeList.模型_评级类型_担保人.getValue().equals(otype)) {
                // 交易数据.买入时点评级结果 = 担保人外部评级结果
                ocode=modAssetInfoDto.getSponsor();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                resultDto =  getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                isFinLicence = modAssetInfoDto.getSponsorFinLicence();
            }
            else if (ModelDictCodeList.模型_评级类型_较高值.getValue().equals(otype)) {
                //交易数据.最新评级结果 = 交易数据.主体评级
                ocode=modAssetInfoDto.getIssuer();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoIssuer = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);

                // 交易数据.买入时点评级结果 = 融资人外部评级结果
                ocode=modAssetInfoDto.getFinancier();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoFinancier = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);

                // 交易数据.买入时点评级结果 = 担保人外部评级结果
                ocode=modAssetInfoDto.getSponsor();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoSponsor = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);

                resultDto =  impairmentCalculationCommonService.maxRating(viewRatingInfoIssuer,viewRatingInfoFinancier,viewRatingInfoSponsor);
                if(resultDto == null){
                    return null;
                }
                if(viewRatingInfoIssuer == resultDto){
                    isFinLicence = modAssetInfoDto.getIssueFinLicence();
                }else if (viewRatingInfoFinancier == resultDto){
                    isFinLicence = modAssetInfoDto.getFinancierFinLicence();
                }else if (viewRatingInfoSponsor == resultDto){
                    isFinLicence = modAssetInfoDto.getSponsorFinLicence();
                }
            } else if (ModelDictCodeList.模型_评级类型_较低值.getValue().equals(otype)) {
                //交易数据.最新评级结果 = 交易数据.主体评级
                ocode=modAssetInfoDto.getIssuer();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoIssuer = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);

                // 交易数据.买入时点评级结果 = 融资人外部评级结果
                ocode=modAssetInfoDto.getFinancier();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoFinancier = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);

                // 交易数据.买入时点评级结果 = 担保人外部评级结果
                ocode=modAssetInfoDto.getSponsor();
                otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
                ViewRatingInfoDto viewRatingInfoSponsor = getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
                resultDto =  impairmentCalculationCommonService.minRating(viewRatingInfoIssuer,viewRatingInfoFinancier,viewRatingInfoSponsor);
                if(resultDto == null){
                    return null;
                }
                // 比较对象地址，确认采用哪个主体对应的是否持有金融牌照
                if(resultDto ==viewRatingInfoIssuer){
                    isFinLicence = modAssetInfoDto.getIssueFinLicence();
                }else if (resultDto == viewRatingInfoFinancier){
                    isFinLicence = modAssetInfoDto.getFinancierFinLicence();
                }else if (resultDto == viewRatingInfoSponsor){
                    isFinLicence = modAssetInfoDto.getSponsorFinLicence();
                }
            }else{
                logger.warn("评级类型不支持，无法查询评级信息.资产代码为："+modAssetInfoDto.getFinprodId()+"，评级类型为："+otype);
                return null;
            }
        }
        else if (DictCodeListExt1.证券品种_存款.getValue().equals(sectype)
            || DictCodeListExt1.证券品种_回购.getValue().equals(sectype)){
            // 交易数据.买入时点评级结果 = 发行人评级结果
            // 交易数据.买入时点评级结果 = 担保人外部评级结果
            ocode=modAssetInfoDto.getIssuer();
            otype = ModelDictCodeList.模型_评级类型_主体评级.getValue();
            resultDto =  getRateInfoWithOrgType(ocode,otype,gradeOrgType,gradeorgid,queryDate);
            isFinLicence = modAssetInfoDto.getIssueFinLicence();
        }else{
            logger.warn("证券品种不支持，无法查询评级信息.资产代码为："+modAssetInfoDto.getFinprodId()+"，证券品种为："+modAssetInfoDto.getSecType());
            return null;
        }
        if(resultDto != null){
            resultDto.setIsFinLicence(isFinLicence);
        }
        return resultDto;
    }

    /**
     * 根据不同的评级机构类型走不同逻辑查询评级信息
     * @param ocode
     * @param otype
     * @param gradeOrgType
     * @param gradeorgid
     * @param queryDate
     * @return
     */
    private ViewRatingInfoDto getRateInfoWithOrgType(String ocode, String otype, String gradeOrgType, String gradeorgid, Date queryDate) {
        ViewRatingInfoDto result =null;
        if (gradeOrgType.equals(DictCodeListExt3.评级机构类型_最新.getValue())) {
            gradeorgid = null;//取最新的时候，评级机构是空的，为避免错误配置，手动置空
        }
        List<ViewRatingInfoDto> ratingInfoList = rateInfoCommonMapper.getRateInfo(ocode, otype, gradeorgid, queryDate);
        if (CollectionUtils.isNotEmpty(ratingInfoList)) {
            for (ViewRatingInfoDto item : ratingInfoList) {
                if (result == null) {
                    result = item;
                } else {
                    result = impairmentCalculationCommonService.maxRating(result, item);
                }
            }
        }
        return result;
    }

    /**
     * 根据资产类型+资产细分类获取系统参数兜底评级
     * @param assetType 资产类型
     * @param assetDetailType 资产细分类
     * @return
     */
    public String getBottomRateInfo(String assetType, String assetDetailType){
        String rateInfo = null;
        if(DictCodeListExt1.证券品种_债券.getValue().equals(assetType)){
            if(DictCodeList.债券类型_国债.getValue().equals(assetDetailType)
                    || DictCodeList.债券类型_政策银行债.getValue().equals(assetDetailType)
                    || DictCodeList.债券类型_地方政府债.getValue().equals(assetDetailType)){
                // 系统参数-国债、政策性金融债、地方政府债兜底评级结果
                rateInfo = CacheUtil.getSysParam(GlobalConstant.BOND_BOTTOM_RATE_1);
            }
            else{
                // 系统参数-除国债、政策性金融债、地方政府债外其他债券兜底评级结果
                rateInfo =CacheUtil.getSysParam(GlobalConstant.BOND_BOTTOM_RATE_2);
            }
        }
        else if(DictCodeListExt1.证券品种_其他债权类.getValue().equals(assetType)){
            // 减值方案系统参数 = 系统参数-其他债权资产兜底融资人评级
            rateInfo = CacheUtil.getSysParam(GlobalConstant.DELT_RIGHT_BOTTOM_RATE);
        }
        else if(DictCodeListExt1.证券品种_存款.getValue().equals(assetType)){
            // 减值方案系统参数 = 系统参数-存款资产兜底评级结果
            rateInfo = CacheUtil.getSysParam(GlobalConstant.DEPOSITE_BOTTOM_RATE);
        }
        else if(DictCodeListExt1.证券品种_回购.getValue().equals(assetType)){
            // 减值方案系统参数 = 系统参数-回购资产兜底评级结果
            rateInfo = CacheUtil.getSysParam(GlobalConstant.REPO_FINANCIER_RATE);
        }

        // 兜底评级结果(01:AA-,02:AA,03:AA+,04:AAA)，根据系统参数映射内部评级
        if(DictCodeListExt3.兜底评级_AA_MINUS.getValue().equals(rateInfo)){
            rateInfo = DictCodeListExt3.内部评级_AA_MINUS.getValue();
        }
        else if(DictCodeListExt3.兜底评级_AA.getValue().equals(rateInfo)){
            rateInfo = DictCodeListExt3.内部评级_AA.getValue();
        }
        else if(DictCodeListExt3.兜底评级_AA_PLUS.getValue().equals(rateInfo)){
            rateInfo = DictCodeListExt3.内部评级_AA_PLUS.getValue();
        }
        else if(DictCodeListExt3.兜底评级_AAA.getValue().equals(rateInfo)){
            rateInfo = DictCodeListExt3.内部评级_AAA.getValue();
        }
        return rateInfo;
    }
}
