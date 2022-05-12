package com.joyintech.tams.invest.batch.tenderbond.service;

import com.google.common.collect.Lists;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductBarterTotalMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.batch.tenderbond.dao.FinTenderBondAssetBatchMapper;
import com.joyintech.tams.invest.batch.tenderbond.dto.FinTenderBondAssetBatchDto;
import com.joyintech.tams.invest.batch.valbatchimport.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.invest.batch.valbatchimport.common.service.CommonAutoCouponService;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.valuationusemerge.WflValidInputParameter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 新债标的自动上市流通
 *
 * @author gwl
 * @version 1.0 2022年04月18日
 * @see FinTenderBondAssetBatchService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FinTenderBondAssetBatchService {

    @Resource
    private FinTenderBondAssetBatchMapper finTenderBoundAssetMapper;

    @Resource
    private AutoPeriodCommonService autoPeriodCommonService;

    @Resource
    private CommonAutoCouponService commonAutoCouponService;

    @Resource
    private TrdFinProductDealTotalMapper trdFinProductDealTotalMapper;

    @Resource
    private TrdFinProductBarterTotalMapper trdFinProductBarterTotalMapper;

    @Resource
    private FinProductMapper finProductMapper;

    @Resource
    private FinProductAddMapper finProductAddMapper;

    @Resource
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 上市流通
     *
     * @param plateDate
     */
    public void execute(Date plateDate) {
        List<Long> seqNoList = new ArrayList<>();
        try {
            //处理已经手动上市流通但未到上市日的情况
            this.dealCompleteListing(plateDate, seqNoList);
            //正常的已发行的待上市流通的记录
            Map<String, Object> param = new HashMap<>();
            param.put("listingDate", plateDate);
            param.put("issueStage", DictCodeListExt4.发行阶段_已发行.getValue());
            param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
            param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
            param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
            param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
            param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
            param.put("ISSUE_STATUS_02", DictCodeListExt1.发行状态_发行失败.getValue());
            List<FinTenderBondAssetBatchDto> list = finTenderBoundAssetMapper.selectListing(param);
            if (CollectionUtils.isEmpty(list)) return;
            for (FinTenderBondAssetBatchDto dto : list) {
                if (isInProcess(dto.getFinprodMarketId(), "FinTenderBondAsset002", "FinTenderBondAsset003")) {
                    continue;
                }
                if (StringUtil.isNotEmpty(dto.getAppearMarketId())) {//上市代码不为空
                    param.put("finprodType2", DictCodeList.平安资产类型_债券.getValue());
                    param.put("finprodMarketId", dto.getAppearMarketId());
                    FinProduct product = finTenderBoundAssetMapper.selectProduct(param);
                    if (Objects.nonNull(product)) {
                        dto.setCirculationCode(product.getFinprodMarketId());
                        dto.setCirculationDate(plateDate);
                        save(dto, plateDate, seqNoList);
                        continue;
                    }
                    //根据 资产名称、资产简称、交易场所、登记托管机构、起息日、到期日、发行人、息票品种、利率类型、币种 进行去重
                    List<InterfPayhsrcsecinfo> interList = finTenderBoundAssetMapper.getBySecId(dto.getAppearMarketId());
                    if (CollectionUtils.isNotEmpty(interList) && interList.size() == 1) {
                        if (this.autoBondCoupon(interList.get(0))) {
                            dto.setCirculationCode(interList.get(0).getSecid());
                            dto.setCirculationDate(plateDate);
                            save(dto, plateDate, seqNoList);
                        }
                        continue;
                    }
                    if (CollectionUtils.isNotEmpty(interList) && interList.size() > 1) {
                        continue;
                    }
                    interList = finTenderBoundAssetMapper.getByParam(dto);
                    if (CollectionUtils.isNotEmpty(interList) && interList.size() == 1) {
                        if (this.autoBondCoupon(interList.get(0))) {
                            dto.setCirculationCode(interList.get(0).getSecid());
                            dto.setCirculationDate(plateDate);
                            save(dto, plateDate, seqNoList);
                        }
                    }
                } else {
                    List<InterfPayhsrcsecinfo> interList = finTenderBoundAssetMapper.getByParam(dto);
                    if (CollectionUtils.isNotEmpty(interList) && interList.size() == 1) {
                        if (this.autoBondCoupon(interList.get(0))) {
                            dto.setCirculationCode(interList.get(0).getSecid());
                            dto.setCirculationDate(plateDate);
                            save(dto, plateDate, seqNoList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (CollectionUtils.isNotEmpty(seqNoList)) {
                String errorMsg = new String();
                seqNoList.forEach(i -> {
                    PositionHttpRequest.sendRevoke(i + "", DictCodeListExt1.子系统代码_交易.getValue(), errorMsg);
                });
                if (StringUtil.isNotEmpty(errorMsg)) {
                    throw new RuntimeException("上市流通批处理失败,头寸冲正失败");
                }
            }
            throw new RuntimeException("上市流通批处理失败");
        }
    }

    /**
     * 处理已经完成上市流通，但未发头寸的记录
     */
    private void dealCompleteListing(Date plateDate, List<Long> seqList) {
        Map<String, Object> param = new HashMap<>();
        param.put("listingDate", plateDate);
        param.put("finprodType2", DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("ISSUE_STATUS_01", DictCodeListExt1.发行状态_发行成功.getValue());
        List<FinTenderBondAssetBatchDto> list = finTenderBoundAssetMapper.selectCompleteList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> this.sendPosition(e, plateDate, seqList));
        }
    }

    /**
     * 判断是否在流程中
     *
     * @param pk1
     * @return
     */
    public Boolean isInProcess(String pk1, String... pageId) {
        if (com.joyintech.framework.util.StringUtil.isEmpty(pk1)) return Boolean.FALSE;
        WflValidInputParameter wflValidInputParameter = null;
        for (String str : pageId) {
            wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId(str);
            wflValidInputParameter.setBusinessDataPk1(pk1);
            wflValidInputParameter.setSeachMode("01");
            List<String> result = finTenderBoundAssetMapper.getBusinessData(wflValidInputParameter);
            if (CollectionUtils.isNotEmpty(result)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 数据入库
     *
     * @param dto
     * @param plateDate
     */
    private void save(FinTenderBondAssetBatchDto dto, Date plateDate, List<Long> seqList) {
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
        trdFinProductDealTotal.setTradeDate(plateDate);
        trdFinProductDealTotal.setCcy(DictCodeList.币种_人民币_CNY.getValue());
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
        product.setCirculationCode(dto.getCirculationCode());
        product.setCirculationDate(dto.getCirculationDate());
        finProductMapper.updateForTenderBound(product);
        FinProductAdd productAdd = new FinProductAdd();
        productAdd.setFinprodId(dto.getFinprodId());
        productAdd.setIssueStatus(DictCodeListExt1.发行状态_发行成功.getValue());
        finProductAddMapper.updateForTenderBound(productAdd);
        //记录操作日志
        saveOperate(dto.getFinprodMarketId(), dto.getRemark());
        //发送头寸
        dto.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
        this.sendPosition(dto, plateDate, seqList);
    }


    private void sendPosition(FinTenderBondAssetBatchDto dto, Date plateDate, List<Long> seqList) {
        long seqNo = (long) Arrays.deepHashCode(new Object[]{dto.getTotleTradeId(), DictCodeListExt1.子系统代码_交易.getValue(), new Date().getTime(), Thread.currentThread().getId()});
        //调用头寸
        Map<String, String> params = new HashMap();
        params.put("finprodType2", DictCodeList.平安资产类型_债券.getValue());
        params.put("finprodMarketId", dto.getAppearMarketId());
        String finproductId = finTenderBoundAssetMapper.selectFinprodId(params);
        BaseMap param = BaseMap.newMap().put("userId", "SYSTEM")
                .put("totalTradeId", dto.getTotleTradeId())
                .put("afterListingFinprodId", finproductId)
                .put("beforeListingFinprodId", dto.getFinprodId())
                .put("modelType", DictCodeListExt1.子系统代码_交易.getValue())
                .put("busiType", DictCodeListExt3.头寸业务类型_上市流通.getValue())
                .put("busiAct", DictCodeListExt3.业务动作_上市.getValue())
                .put("ps", DictCodeList.交易方向_上市流通.getValue())
                .put("systemAct", DictCodeListExt3.系统动作_新建.getValue())
                .put("cdate", plateDate)
                .put("ccy", dto.getCcy())
                .put("balanceCheck", DictCodeList.是否_否.getValue())
                .put("interfaceType", "Bond001")
                .put("tradePlace", dto.getTradeMarket())
                .put("decisionAct", DictCodeListExt3.系统动作_新建.getValue())
                .put("PositionSeqno", seqNo)
                .put("isListingGenerate", DictCodeList.是否_是.getValue());
        if (dto.getTradeMarket().equals(DictCodeListExt1.交易场所_银行间.getValue()) || dto.getTradeMarket().equals(DictCodeListExt1.交易场所_商业银行柜台.getValue())) {
            param.put("interfaceType", "Bond001");
        } else {
            param.put("interfaceType", "Bond002");
        }
        seqList.add(seqNo);
        PositionHttpRequest.send(param);
    }


    /**
     * 自动加券成功返回true,失败返回false
     *
     * @param payhsrcsecinfo
     */
    private boolean autoBondCoupon(InterfPayhsrcsecinfo payhsrcsecinfo) {
        InterfPayhsrcsecinfoDto dto = new InterfPayhsrcsecinfoDto();
        dto.setSecidList(Lists.newArrayList(payhsrcsecinfo.getSecid()));
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = commonAutoCouponService.autoBondCoupon(dto);
        if (Objects.nonNull(interfPayhsrcsecinfoDto) && CollectionUtils.isNotEmpty(interfPayhsrcsecinfoDto.getErrorBondList())) {
            return false;
        }
        return true;
    }

    /**
     * 保存操作类型
     *
     * @param tradeId
     * @param remark
     */
    public void saveOperate(String tradeId, String remark) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(tradeId);
        oprHisReq.setOperType(DictCodeListExt3.操作类型_上市流通.getValue());
        oprHisReq.setBusiDes(remark);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

}
