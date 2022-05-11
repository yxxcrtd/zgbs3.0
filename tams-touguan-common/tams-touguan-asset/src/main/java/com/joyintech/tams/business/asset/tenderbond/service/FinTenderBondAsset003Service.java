package com.joyintech.tams.business.asset.tenderbond.service;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.asset.bond.dto.FinBondAssetSaveRequest;
import com.joyintech.tams.business.asset.bond.service.FinBondAssetService;
import com.joyintech.tams.business.asset.bond.service.FinBondAssetValidatorService;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductBarterTotal;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.business.asset.tenderbond.dao.FinTenderBondAssetMapper;
import com.joyintech.tams.business.asset.tenderbond.dto.FinTenderBondAsset003Dto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.service.FinBondMarketInfoQuery001Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 新债标的服务类
 *
 * @author gwl
 * @version 1.0 2022年4月11日
 * @see FinTenderBondAsset003Service
 * @since 1.0
 */
@Service
public class FinTenderBondAsset003Service extends BaseService<FinTenderBondAsset003Dto> implements BaseProcessService<FinTenderBondAsset003Dto> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private FinTenderBondAssetMapper finTenderBoundAssetMapper;

    @Resource
    private FinProductMapper finProductMapper;

    @Resource
    private FinProductAddMapper finProductAddMapper;

    @Resource
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Resource
    private TrdFinProductBarterTotalMapper trdFinProductBarterTotalMapper;

    @Resource
    private FinTenderBondAssetService finTenderBoundAssetService;

    @Resource
    private AutoPeriodCommonService autoPeriodCommonService;

    @Autowired
    private FinBondAssetService finBondAssetService;

    @Autowired
    private FinBondAssetValidatorService finBondAssetValidatorService;

    @Autowired
    private FinBondMarketInfoQuery001Service finBondMarketInfoQuery001Service;

    @Resource
    private IPlatformDate iPlatformDate;

    @Override
    public String generateTitle(FinTenderBondAsset003Dto entity, OperationType operationType) {
        return String.format("资产代码(%s) 资产简称(%s) 上市流通", StringUtil.isEmpty(entity.getFinprodMarketId()) ? "" : entity.getFinprodMarketId(),
                StringUtil.isEmpty(entity.getFinprodAbbr()) ? "" : entity.getFinprodAbbr());
    }

    @Override
    public void doInsert(FinTenderBondAsset003Dto entity, OperationType type) {
        if(type == OperationType.REVOKE) return;
        Map<String,String> param = new HashMap();
        param.put("finprodType2",DictCodeList.平安资产类型_债券.getValue());
        param.put("finprodMarketId",entity.getAppearMarketId());
        String finproductId = finTenderBoundAssetMapper.selectFinprodId(param);
        if(StringUtil.isEmpty(finproductId)) {//执行加券
            finBondMarketInfoQuery001Service.addCoupons(entity.getAppearMarketId());
        }
    }

    @Override
    public void doComplete(FinTenderBondAsset003Dto dto, OperationType type) {
        if (OperationType.REVOKE == type) {
            String totleTradeId = finTenderBoundAssetMapper.selectTotleTradeId(dto.getFinprodId());
            if (StringUtil.isNotEmpty(totleTradeId)) {
                TrdFinProductDealTotal total = new TrdFinProductDealTotal();
                total.setTotleTradeId(totleTradeId);
                total.setIsCancel(DictCodeList.是否_是.getValue());
                trdFinProductDealTotalMapper.updateByPrimaryKeySelective(total);
                //金融产品实物汇总交易
                TrdFinProductBarterTotal trdFinProductBarterTotal = new TrdFinProductBarterTotal();
                trdFinProductBarterTotal.setTradeId(String.format("%sR", totleTradeId));
                trdFinProductBarterTotal.setFinprodType(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
                trdFinProductBarterTotal.setFinprodId(dto.getFinprodId());
                trdFinProductBarterTotal.setBranch(0L);
                trdFinProductBarterTotalMapper.insert(trdFinProductBarterTotal);
                //更新标的表
                FinProduct product = new FinProduct();
                product.setFinprodId(dto.getFinprodId());
                product.setCirculationCode(null);
                product.setCirculationDate(null);
                finProductMapper.updateForTenderBound(product);
                FinProductAdd productAdd = new FinProductAdd();
                productAdd.setFinprodId(dto.getFinprodId());
                productAdd.setIssueStatus(null);
                finProductAddMapper.updateForTenderBound(productAdd);
                if(dto.getListingDate().compareTo(iPlatformDate.getSysDate()) == 0) {//只有当上市日期和平台日相等的时候才会发撤销头寸
                    try {
                        PositionHttpRequest.dorevoke(totleTradeId, DictCodeListExt1.子系统代码_交易.getValue(), DictCodeListExt3.头寸业务类型_上市流通.getValue(), DictCodeList.交易方向_上市流通.getValue());
                    } catch (PositionException e) {
                        logger.error("交收失败-头寸处理异常", e);
                        WorkFlowUtil.showMessage("头寸处理异常,请检查头寸日期", MessageType.错误消息);
                        throw new PositionException(e);
                    }
                }
                finTenderBoundAssetService.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_上市流通撤销);
            }
        } else {
            FinProduct product1 = finProductMapper.selectByPrimaryKey(dto.getFinprodId());
            //1、记录交易信息
            //金融产品交易汇总表
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            trdFinProductDealTotal.setTotleTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            trdFinProductDealTotal.setFinprodType2(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            trdFinProductDealTotal.setFinprodType(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            trdFinProductDealTotal.setBusiType(DictCodeListExt1.平安业务类型_上市流通.getValue());
            trdFinProductDealTotal.setFinprodId(dto.getFinprodId());
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_上市流通.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setTradeDate(iPlatformDate.getSysDate());
            trdFinProductDealTotal.setCcy(product1.getCcy());
            trdFinProductDealTotal.setBranch(0L);
            trdFinProductDealTotalMapper.insert(trdFinProductDealTotal);
            //金融产品实物汇总交易
            TrdFinProductBarterTotal trdFinProductBarterTotal = new TrdFinProductBarterTotal();
            trdFinProductBarterTotal.setTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdFinProductBarterTotal.setFinprodType(DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            trdFinProductBarterTotal.setFinprodId(dto.getFinprodId());
            trdFinProductBarterTotal.setBranch(0L);
            trdFinProductBarterTotalMapper.insert(trdFinProductBarterTotal);
            //2、更新标的表
            FinProduct product = new FinProduct();
            product.setFinprodId(dto.getFinprodId());
            product.setCirculationCode(dto.getAppearMarketId());
            product.setCirculationDate(dto.getListingDate());
            finProductMapper.updateForTenderBound(product);
            FinProductAdd productAdd = new FinProductAdd();
            productAdd.setFinprodId(dto.getFinprodId());
            productAdd.setIssueStatus(DictCodeListExt1.发行状态_发行成功.getValue());
            finProductAddMapper.updateForTenderBound(productAdd);
            finTenderBoundAssetService.saveOperate(dto.getFinprodMarketId(), dto.getRemark(), DictCodeListExt3.操作类型_上市流通);
            if(dto.getListingDate().compareTo(iPlatformDate.getSysDate()) == 0) {//只有当上市日和平台日相等时才发头寸
                Map<String,String> params = new HashMap();
                params.put("finprodType2",DictCodeList.平安资产类型_债券.getValue());
                params.put("finprodMarketId",dto.getAppearMarketId());
                String finproductId = finTenderBoundAssetMapper.selectFinprodId(params);
                //调用头寸
                BaseMap param = BaseMap.newMap().put("userId", StringUtil.getOrElse(SecurityUtil.getLoginUser().getUserName(),"SYSTEM"))
                        .put("totalTradeId", trdFinProductDealTotal.getTotleTradeId())
                        .put("marketFinprodId", finproductId)
                        .put("listingFinprodId", product.getFinprodId())
                        .put("modelType", DictCodeListExt1.子系统代码_交易.getValue())
                        .put("busiType", DictCodeListExt3.头寸业务类型_上市流通.getValue())
                        .put("busiAct", DictCodeListExt3.业务动作_上市.getValue())
                        .put("ps", DictCodeList.交易方向_上市流通.getValue())
                        .put("systemAct", DictCodeListExt3.系统动作_新建.getValue())
                        .put("cdate", iPlatformDate.getSysDate())
                        .put("ccy", product1.getCcy())
                        .put("balanceCheck", DictCodeList.是否_否.getValue())
                        .put("interfaceType", "Bond001")
                        .put("tradePlace",product1.getTradeMarket())
                        .put("decisionAct",DictCodeListExt3.系统动作_新建.getValue());
                try {
                    PositionHttpRequest.send(param);
                } catch (PositionException e) {
                    logger.error("处理失败-头寸处理异常", e);
                    WorkFlowUtil.showMessage("头寸处理异常,请检查头寸日期", MessageType.错误消息);
                    throw new PositionException(e);
                }
            }
        }
    }

    /**
     * 根据id返回上市流通信息
     *
     * @param finprodId
     * @return
     */
    public FinTenderBondAsset003Dto getById(String finprodId) {
        return finTenderBoundAssetMapper.getByFinProductId(finprodId);
    }



    /**
     * 上市流通时校验是否可以自动加券
     *
     * @param dto
     * @return
     */
    public FinBondAssetSaveRequest validAddCoupons(FinTenderBondAsset003Dto dto) {
        List<ValidMessage> validMessages = new ArrayList<>();
        Map<String,String> param = new HashMap();
        param.put("finprodType2",DictCodeList.平安资产类型_债券.getValue());
        param.put("finprodMarketId",dto.getAppearMarketId());
        String finproductId = finTenderBoundAssetMapper.selectFinprodId(param);
        if(StringUtil.isEmpty(finproductId)) {
            FinBondAssetSaveRequest request = finBondAssetService.getInterfBondDto(dto.getAppearMarketId());
            if (StringUtil.isNotEmpty(dto)) {
                request.setCreditFlag("");
                request.setFinprodMarketId(dto.getAppearMarketId());
                ValidUtils.valid(request,validMessages);
                ValidUtils.valid(request.getFinProductConvertible(),validMessages);
                ValidUtils.valid(request.getFinInterestFactorDto(),validMessages);
                validMessages.addAll(finBondAssetValidatorService.valid(request));
                if(CollectionUtils.isNotEmpty(validMessages)) {
                    return request;
                }else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 上市流通/撤销
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(FinTenderBondAsset003Dto dto) {
        List<ValidMessage> validMessages = new ArrayList<>();
        if (StringUtil.isEmpty(dto.getWorkflowId()) && finTenderBoundAssetService.isInProcess(dto.getFinprodMarketId(), "FinTenderBondAsset003","FinTenderBondAsset002")) {
            validMessages.add(new ValidMessage("存在流程中的数据"));
        }
        if(StringUtil.isEmpty(dto.getOperateType())) {
            validMessages.add(new ValidMessage("操作类型不能为空"));
        }
        if(StringUtil.isEmpty(dto.getIssueStage())) {
            validMessages.add(new ValidMessage("发行阶段不能为空"));
        }
        if (StringUtil.equals(dto.getOperateType(), OperationType.REVOKE.name())) {
            if (StringUtil.isNotEmpty(dto.getListingDate()) && dto.getListingDate().compareTo(iPlatformDate.getSysDate()) < 0) {
                validMessages.add(new ValidMessage("上市日小于平台日不允许撤销"));
            }
            if (Objects.nonNull(dto.getIssueStage()) && !StringUtil.equals(dto.getIssueStage(), DictCodeListExt4.发行阶段_已上市.getValue())) {
                validMessages.add(new ValidMessage("发行阶段不为已上市的不能进行撤销"));
            }
            Integer count = finTenderBoundAssetMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue(),DictCodeList.是否_否.getValue());
            if (Objects.nonNull(count) && count > 0) {
                validMessages.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        } else {
            if(Objects.isNull(dto.getListingDate())) {
                validMessages.add(new ValidMessage("上市日不能为空"));
            }
            if (Objects.nonNull(dto.getListingDate()) && dto.getListingDate().compareTo(iPlatformDate.getSysDate()) < 0) {
                validMessages.add(new ValidMessage("上市日需大于等于平台日"));
            }
            if (Objects.nonNull(dto.getIssueStatus()) && StringUtil.equals(dto.getIssueStatus(), DictCodeListExt1.发行状态_发行失败.getValue())) {
                validMessages.add(new ValidMessage("发行失败的不能进行上市流通"));
            }
            if (Objects.nonNull(dto.getIssueStage()) && !StringUtil.equals(dto.getIssueStage(), DictCodeListExt4.发行阶段_已发行.getValue())) {
                validMessages.add(new ValidMessage("发行阶段不为已发行的不能进行上市流通"));
            }
        }
        return validMessages;
    }

}
