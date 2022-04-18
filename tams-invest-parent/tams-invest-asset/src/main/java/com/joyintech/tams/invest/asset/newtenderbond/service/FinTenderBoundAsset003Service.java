package com.joyintech.tams.invest.asset.newtenderbond.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductBarterTotal;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.asset.newtenderbond.dao.FinTenderBoundAssetMapper;
import com.joyintech.tams.invest.asset.newtenderbond.dto.FinTenderBoundAsset003Dto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 新债标的服务类
 *
 * @author gwl
 * @version 1.0 2022年4月11日
 * @see FinTenderBoundAsset003Service
 * @since 1.0
 */
@Service
public class FinTenderBoundAsset003Service implements BaseProcessService<FinTenderBoundAsset003Dto> {

    @Resource
    private FinTenderBoundAssetMapper finTenderBoundAssetMapper;

    @Resource
    private FinProductMapper finProductMapper;

    @Resource
    private FinProductAddMapper finProductAddMapper;

    @Resource
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Resource
    private TrdFinProductBarterTotalMapper trdFinProductBarterTotalMapper;

    @Resource
    private FinTenderBoundAssetService finTenderBoundAssetService;

    @Resource
    private AutoPeriodCommonService autoPeriodCommonService;

    @Resource
    private IPlatformDate iPlatformDate;

    @Override
    public String generateTitle(FinTenderBoundAsset003Dto entity, OperationType operationType) {
        return String.format("资产代码(%) 资产简称(%) 上市流通", StringUtil.isEmpty(entity.getFinprodMarketId()) ? "" : entity.getFinprodMarketId(),
                StringUtil.isEmpty(entity.getFinprodAbbr()) ? "" : entity.getFinprodAbbr());
    }

    @Override
    public void doComplete(FinTenderBoundAsset003Dto dto, OperationType type) {
        if (StringUtil.equals(OperationType.REVOKE.name(), type)) {
            List<String> totleTradeIdList = finTenderBoundAssetMapper.selectTotleTradeId(dto.getFinprodId());
            if (CollectionUtils.isNotEmpty(totleTradeIdList)) {
                totleTradeIdList.forEach(e -> {
                    TrdFinProductDealTotal total = new TrdFinProductDealTotal();
                    total.setTotleTradeId(e);
                    total.setIsCancel(DictCodeList.是否_是.getValue());
                    trdFinProductDealTotalMapper.updateByPrimaryKeySelective(total);
                    //金融产品实物汇总交易
                    TrdFinProductBarterTotal trdFinProductBarterTotal = new TrdFinProductBarterTotal();
                    trdFinProductBarterTotal.setTradeId(String.format("%sR", e));
                    trdFinProductBarterTotal.setFinprodType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
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

                    //TODO 调用头寸
                });
            }
        } else {
            //1、记录交易信息
            //金融产品交易汇总表
            TrdFinProductDealTotal trdFinProductDealTotal = new TrdFinProductDealTotal();
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_债券买入);
            trdFinProductDealTotal.setTotleTradeId(autoPeriodCommonService.buildBussinessCode(parameter));
            trdFinProductDealTotal.setFinprodType2(DictCodeListExt2.平安资产类型_一级市场债.getValue());
            trdFinProductDealTotal.setFinprodType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
            trdFinProductDealTotal.setBusiType(DictCodeListExt1.平安业务类型_上市流通.getValue());
            trdFinProductDealTotal.setFinprodId(dto.getFinprodId());
            trdFinProductDealTotal.setPs(DictCodeList.交易方向_上市流通.getValue());
            trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
            trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
            trdFinProductDealTotal.setTradeDate(iPlatformDate.getSysDate());
            trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            trdFinProductDealTotal.setBranch(0L);
            trdFinProductDealTotalMapper.insert(trdFinProductDealTotal);
            //金融产品实物汇总交易
            TrdFinProductBarterTotal trdFinProductBarterTotal = new TrdFinProductBarterTotal();
            trdFinProductBarterTotal.setTradeId(trdFinProductDealTotal.getTotleTradeId());
            trdFinProductBarterTotal.setFinprodType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
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

            //TODO 调用头寸
            //TODO 这里处理加券
        }
        //3、记录操作日志
        finTenderBoundAssetService.saveOperate(dto.getFinprodId(), dto.getRemark(), type);
    }


    /**
     * 上市流通/撤销
     *
     * @param dto
     * @return
     */
    public List<ValidMessage> valid(FinTenderBoundAsset003Dto dto) {
        List<ValidMessage> validMessages = new ArrayList<>();
        if (finTenderBoundAssetService.isInProcess(dto.getFinprodMarketId(), "FinTenderBoundAsset003")) {
            validMessages.add(new ValidMessage("存在流程中的数据"));
        }
        if (StringUtil.equals(dto.getOperateType(), OperationType.REVOKE.name())) {
            if (dto.getListingDate().compareTo(iPlatformDate.getSysDate()) < 0) {
                validMessages.add(new ValidMessage("上市日小于平台日不允许撤销"));
            }
            if (!StringUtil.equals(dto.getIssueStage(), DictCodeListExt4.发行阶段_已上市.getValue())) {
                validMessages.add(new ValidMessage("发行阶段不为已上市的不能进行撤销"));
            }
            Integer count = finTenderBoundAssetMapper.countFinprodIdUse(dto.getFinprodId(), DictCodeList.是否_否.getValue());
            if (Objects.nonNull(count) && count > 0) {
                validMessages.add(new ValidMessage("存在指令或者交易使用了该记录信息"));
            }
        } else {
            if (dto.getListingDate().compareTo(iPlatformDate.getSysDate()) < 0) {
                validMessages.add(new ValidMessage("上市日需大于等于平台日"));
            }
            if (StringUtil.equals(dto.getIssueStatus(), DictCodeListExt1.发行状态_发行失败.getValue())) {
                validMessages.add(new ValidMessage("发行失败的不能进行上市流通"));
            }
            if (!StringUtil.equals(dto.getIssueStage(), DictCodeListExt4.发行阶段_已发行.getValue())) {
                validMessages.add(new ValidMessage("发行阶段不为已发行的不能进行上市流通"));
            }
        }
        return validMessages;
    }

}
