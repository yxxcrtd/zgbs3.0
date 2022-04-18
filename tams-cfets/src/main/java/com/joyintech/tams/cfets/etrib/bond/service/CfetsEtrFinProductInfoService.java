package com.joyintech.tams.cfets.etrib.bond.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.business.common.service.CfgDataTableService;
import com.joyintech.tams.cfets.etrib.bond.dao.CfetsEtrFinProductInfoMapper;
import com.joyintech.tams.cfets.etrib.bond.dto.CfetsEtrFinProductInfoResponse;
import com.joyintech.tams.constant.DictCodeListExt4;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 银行间债券信息查询
 *
 * @author zhuys
 * @@date 2022年04月14日
 * @see CfetsEtrFinProductInfoService
 * @since 1.0
 */
@Service("CfetsEtrFinProductInfoService")
public class CfetsEtrFinProductInfoService extends BaseService {

    private static String SEPARATOR = "/";
    private static String AMPERS_AND = "&";
    private static String BASE_SPCD_CODE = "999999";
    private static String COMM = ",";

    @Autowired
    private CfetsEtrFinProductInfoMapper cfetsEtrFinProductInfoMapper;
    @Autowired
    private CfgDataTableService cfgDataTableService;

    /**
     * 查询债券信息
     *
     * @param pageCode
     * @param dtCode
     * @param finprodId 金融产品代码
     * @return
     */
    public CfetsEtrFinProductInfoResponse findFinProductInfo(String pageCode, String dtCode, String finprodId) {
        CfetsEtrFinProductInfoResponse finProductInfo = cfetsEtrFinProductInfoMapper.findFinProductInfo(finprodId);
        if (!Objects.isNull(finProductInfo)) {
            //标题
            finProductInfo.setTitle(finProductInfo.getFinprodId() + finProductInfo.getFinprodAbbr());
            //评级信息

            //查询对于业务场景展示表头
            String spcdCode = getSpcdCodesByFinProduct(finProductInfo);
            List<String> spcdCodeList = new ArrayList<>(Arrays.asList(spcdCode.split(COMM)));
            List<CfgDataTableShow> byPageCodeAndDtCodes = cfgDataTableService.getByPageCodeAndDtCodes(pageCode, dtCode, spcdCodeList);
            if (CollectionUtils.isNotEmpty(byPageCodeAndDtCodes)) {
                finProductInfo.setTableShows(byPageCodeAndDtCodes);
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

        if (DictCodeListExt4.发行类型_首发.getValue().equals(issueStyle) || DictCodeListExt4.发行类型_首发.getValue().equals(issueStyle)) {
            return sb.append(BASE_SPCD_CODE)
                    .append(COMM)
                    .append(DictCodeListExt4.新债_发行方式_招标发行.getValue())
                    .append(SEPARATOR)
                    .append(DictCodeListExt4.新债_发行方式_簿记建档.getValue())
                    .append(AMPERS_AND)
                    .append(DictCodeListExt4.招标方式_单一价格招标.getValue())
                    .append(SEPARATOR)
                    .append(DictCodeListExt4.招标方式_数量招标.getValue())
                    .append(AMPERS_AND)
                    .append(issueStyle)
                    .toString();
        }

        return sb.toString();
    }

}
