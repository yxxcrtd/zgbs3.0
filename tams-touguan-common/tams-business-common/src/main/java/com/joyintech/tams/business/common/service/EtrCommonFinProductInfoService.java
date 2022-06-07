package com.joyintech.tams.business.common.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.busicommon.productRating.dto.CommonProductRatingDto;
import com.joyintech.tams.busicommon.productRating.service.CommonProductRatingService;
import com.joyintech.tams.business.common.dao.EtrCommonFinProductInfoMapper;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoRequest;
import com.joyintech.tams.business.common.dto.CfetsEtrFinProductInfoResponse;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt4;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 银行间债券信息查询
 *
 * @author zhuys
 * @@date 2022年04月14日
 * @see EtrCommonFinProductInfoService
 * @since 1.0
 */
@Service("EtrCommonFinProductInfoService")
public class EtrCommonFinProductInfoService extends BaseService {

    private static String SEPARATOR = "/";
    private static String AMPERS_AND = "&";
    private static String BASE_SPCD_CODE = "999999";
    private static String COMM = ",";

    @Autowired
    private EtrCommonFinProductInfoMapper etrCommonFinProductInfoMapper;
    @Autowired
    private CfgDataTableService cfgDataTableService;

    /**
     * 产品评级共通
     */
    @Autowired(required = false)
    private CommonProductRatingService commonProductRatingService;


    /**
     * 查询债券信息
     *
     * @param pageCode
     * @param dtCode
     * @param request  业务参数
     * @return
     */
    public CfetsEtrFinProductInfoResponse findFinProductInfo(String pageCode, String dtCode, CfetsEtrFinProductInfoRequest request) {
        CfetsEtrFinProductInfoResponse finProductInfo = etrCommonFinProductInfoMapper.findFinProductInfo(request);
        if (Objects.nonNull(finProductInfo)) {
            //评级信息
            setGrade(finProductInfo);
            if (Objects.nonNull(pageCode) && Objects.nonNull(dtCode)) {
                //查询对于业务场景展示表头
                String spcdCode = getSpcdCodesByFinProduct(finProductInfo);
                List<String> spcdCodeList = new ArrayList<>(Arrays.asList(spcdCode.split(COMM)));
                List<CfgDataTableShow> byPageCodeAndDtCodes = cfgDataTableService.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodeList);
                if (CollectionUtils.isNotEmpty(byPageCodeAndDtCodes)) {
                    //息票品种
                    String couponSpecies = finProductInfo.getCouponSpecies();
                    //利率类型
                    String intType = finProductInfo.getIntType();
                    if (StringUtil.isBlank(couponSpecies) || StringUtil.isBlank(couponSpecies)) {
                        byPageCodeAndDtCodes = byPageCodeAndDtCodes.stream().filter(cfgDataTableShow -> !cfgDataTableShow.getColName().contains("/")).collect(Collectors.toList());
                    } else {
                        if (DictCodeList.息票品种_零息.getValue().equals(couponSpecies) || DictCodeList.息票品种_贴现.getValue().equals(couponSpecies)
                                || (DictCodeList.息票品种_附息.getValue().equals(couponSpecies) && DictCodeList.利率类型_浮动利率.getValue().equals(intType))) {
                            byPageCodeAndDtCodes.stream().forEach(cfgDataTableShow -> {
                                if ("referYield".equals(cfgDataTableShow.getColCode())) {
                                    cfgDataTableShow.setColName("参考收益率");
                                }
                            });
                        } else if (DictCodeList.息票品种_利随本清.getValue().equals(couponSpecies)
                                || (DictCodeList.息票品种_附息.getValue().equals(couponSpecies) && DictCodeList.利率类型_固定利率.getValue().equals(intType))) {
                            byPageCodeAndDtCodes.stream().forEach(cfgDataTableShow -> {
                                if ("referYield".equals(cfgDataTableShow.getColCode())) {
                                    cfgDataTableShow.setColName("票面利率");
                                }
                            });
                        }
                    }
                    finProductInfo.setTableShows(byPageCodeAndDtCodes);
                }
            }
        }
        return finProductInfo;
    }

    /**
     * 拼接对应业务场景spcdCode
     *
     * @param finProductInfo
     * @return
     */
    private String getSpcdCodesByFinProduct(CfetsEtrFinProductInfoResponse finProductInfo) {
        StringBuilder sb = new StringBuilder();
        //发行方式
        String issueClass = finProductInfo.getIssueClass();
        //招标方式
        String tenderType = finProductInfo.getTenderType();
        //发行类型
        String issueStyle = finProductInfo.getIssueStyle();
        if (StringUtil.isBlank(issueClass) && StringUtil.isBlank(tenderType) && StringUtil.isBlank(issueStyle)) {
            return BASE_SPCD_CODE;
        }
        if (DictCodeListExt4.新债_发行方式_报价发行.getValue().equals(issueClass)) {
            return sb.append(BASE_SPCD_CODE).append(COMM).append(DictCodeListExt4.新债_发行方式_报价发行.getValue()).toString();
        }


        if (DictCodeListExt4.招标方式_单一价格招标.getValue().equals(tenderType) || DictCodeListExt4.招标方式_数量招标.getValue().equals(tenderType)) {
            return sb.append(BASE_SPCD_CODE)
                    .append(COMM)
                    .append(DictCodeListExt4.新债_发行方式_招标发行.getValue())
                    .append(SEPARATOR)
                    .append(DictCodeListExt4.新债_发行方式_簿记建档.getValue())
                    .append(AMPERS_AND)
                    .append(tenderType)
                    .toString();
        }

        if (DictCodeListExt4.发行类型_增发.getValue().equals(issueStyle) || DictCodeListExt4.发行类型_首发.getValue().equals(issueStyle)) {
            return sb.append(BASE_SPCD_CODE)
                    .append(COMM)
                    .append(DictCodeListExt4.新债_发行方式_招标发行.getValue())
                    .append(SEPARATOR)
                    .append(DictCodeListExt4.新债_发行方式_簿记建档.getValue())
                    .append(AMPERS_AND)
                    .append(DictCodeListExt4.招标方式_荷兰式招标.getValue())
                    .append(SEPARATOR)
                    .append(DictCodeListExt4.招标方式_混合式招标.getValue())
                    .append(AMPERS_AND)
                    .append(issueStyle)
                    .toString();
        }

        return sb.toString();
    }

    /**
     * 设置评级信息
     *
     * @param finProductInfo
     */
    private void setGrade(CfetsEtrFinProductInfoResponse finProductInfo) {
        Map<String, Object> param = new HashMap<>();
        param.put("oCode", finProductInfo.getIssuer());
        param.put("baseDataType","01");
        //查询评级类型为主体评级的数据
        List<CommonProductRatingDto> ztList = commonProductRatingService.getProductOrgRating(param);
        List<CommonProductRatingDto> ztListNew = ztList.stream().sorted(Comparator.comparing(CommonProductRatingDto::getGradeDate).reversed()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ztListNew)) {
            //只取第一条数据
            finProductInfo.setZtGradeOrgId(ztListNew.get(0).getGradeOrgId());
            finProductInfo.setZtGrade(ztListNew.get(0).getGradeResult());
        }
        Map<String, Object> zxparam = new HashMap<>();
        zxparam.put("oCode", finProductInfo.getFinprodMarketId());
        zxparam.put("baseDataType","01");
        //查询评级类型是债项评级的数据
        List<CommonProductRatingDto> zxList = commonProductRatingService.getProductMarketRating(zxparam);
        List<CommonProductRatingDto> zxListNew = zxList.stream().sorted(Comparator.comparing(CommonProductRatingDto::getGradeDate).reversed()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(zxListNew)) {
            //只取第一条数据
            finProductInfo.setZxGradeOrgId(zxList.get(0).getGradeOrgId());
            finProductInfo.setZxGrade(zxList.get(0).getGradeResult());
        }
    }

    /**
     * 处理列名
     *
     * @param colName
     * @param couponSpeciesFlag
     * @return
     */
    private String getColName(String colName, Boolean couponSpeciesFlag) {
        if (!colName.contains("/")) {
            return colName;
        }
        String[] split = colName.split("/");
        if (couponSpeciesFlag) {
            return split[0];
        } else {
            return split[1];
        }
    }

}
