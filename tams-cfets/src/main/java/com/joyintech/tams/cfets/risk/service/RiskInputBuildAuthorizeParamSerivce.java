package com.joyintech.tams.cfets.risk.service;

import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto.CfetsTrdFirstBidFirmInfoDto;
import com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto.CfetsTrdBondFirstSubInfoDto;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonInputDto;
import com.joyintech.tams.component.business.riskall.service.RiskCommonService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 构建授权试算参数
 *
 * @author jiangpeng
 * @date 2022/6/10 11:39
 */
@Service
public class RiskInputBuildAuthorizeParamSerivce extends RiskCommonService {
    private static final Logger logger = LoggerFactory.getLogger(RiskInputBuildAuthorizeParamSerivce.class);

    @Autowired
    private FinProductTypeMapper finProductTypeMapper;

    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * 构建一级投标指令授权试算参数
     *
     * @param dto  风险dto
     * @param data 数据
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    void buildEtrBondFirstBidAuthorizeParam(RiskCommonInputDto dto, Object data) {
        logger.debug("构建一级投标指令授权试算参数:" + JsonUtils.object2Json(data));
        CfetsEtrBondFirstBidSaveRequest infoDto = (CfetsEtrBondFirstBidSaveRequest) data;
        dto.setCounterId(infoDto.getCounterId());
        dto.setPrinAmt(infoDto.getTotalBiddingNum());
        dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        dto.setTradeMarket(infoDto.getTradeMarket());
        dto.setPs(DictCodeList.交易方向_买入.getValue());
        FinProductTypeKey key = new FinProductTypeKey();
        key.setFinprodId(infoDto.getFinprodId());
        key.setBranch(0L);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
        dto.setAssetSecType(finProductType.getType9());
        dto.setFinprodId(infoDto.getFinprodId());
    }

    /**
     * 构建一级投标确认授权试算参数
     *
     * @param dto  风险dto
     * @param data 数据
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    void buildCfetsTrdFirstBidFirmInfoParam(RiskCommonInputDto dto, Object data) {
        logger.debug("构建一级投标确认授权试算参数:" + JsonUtils.object2Json(data));
        CfetsTrdFirstBidFirmInfoDto infoDto = (CfetsTrdFirstBidFirmInfoDto) data;
        Map<String, Object> map = BeanUtil.object2Map(data);
        dto.setCounterId(infoDto.getCounterId());
        dto.setPrinAmt(infoDto.getTradeAmt());
        dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        dto.setTradeMarket(infoDto.getTradeMarket());
        dto.setPs("T01");
        FinProductTypeKey key = new FinProductTypeKey();
        key.setFinprodId(infoDto.getFinprodId());
        key.setBranch(0L);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
        dto.setAssetSecType(finProductType.getType9());
        dto.setFinprodId(infoDto.getFinprodId());
    }

    /**
     * 构建一级申购授权试算参数
     *
     * @param dto  风险dto
     * @param data 数据
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    void buildCfetsTrdBondFirstSubInfoParam(RiskCommonInputDto dto, Object data) {
        logger.debug("构建一级申购授权试算参数:" + JsonUtils.object2Json(data));
        CfetsTrdBondFirstSubInfoDto infoDto = (CfetsTrdBondFirstSubInfoDto) data;
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(infoDto.getFinprodId());
        dto.setCounterId(finProduct.getIssuer()); //发行人?
        dto.setPrinAmt(infoDto.getSubscriptionAmount());
        dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        dto.setTradeMarket(DictCodeListExt1.交易场所_银行间.getValue());
        dto.setPs(DictCodeList.交易方向_买入.getValue());
        FinProductTypeKey key = new FinProductTypeKey();
        key.setFinprodId(infoDto.getFinprodId());
        key.setBranch(0L);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
        dto.setAssetSecType(finProductType.getType9());
        dto.setFinprodId(infoDto.getFinprodId());
    }

    /**
     * 构建一级申购授权试算参数
     *
     * @param dto  风险dto
     * @param data 数据
     * @author jiangpeng
     * @date 2022/6/10 11:43
     */
    void buildCfetsPrimaryBidManageSaveRequestParam(RiskCommonInputDto dto, Object data) {
        logger.debug("构建中标结果维护授权试算参数:" + JsonUtils.object2Json(data));
        CfetsPrimaryBidManageSaveRequest infoDto = (CfetsPrimaryBidManageSaveRequest) data;
        dto.setCounterId(infoDto.getCounterId());
        dto.setPrinAmt(infoDto.getTotalBiddingNum());
        dto.setAssetType(DictCodeList.平安资产类型_一级市场债.getValue());
        dto.setTradeMarket(infoDto.getTradeMarket());
        dto.setPs(DictCodeList.交易方向_买入.getValue());
        FinProductTypeKey key = new FinProductTypeKey();
        key.setFinprodId(infoDto.getFinprodId());
        key.setBranch(0L);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
        dto.setAssetSecType(finProductType.getType9());
        dto.setFinprodId(infoDto.getFinprodId());
    }

}
