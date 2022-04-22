package com.joyintech.tams.invest.batch.tenderbond.service;

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
import com.joyintech.tams.invest.batch.tenderbond.dao.FinTenderBondAssetMapper;
import com.joyintech.tams.invest.batch.tenderbond.dto.FinTenderBondAssetDto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
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
 * @see FinTenderBondAssetService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FinTenderBondAssetService {

    @Resource
    private FinTenderBondAssetMapper finTenderBoundAssetMapper;

    @Resource
    private AutoPeriodCommonService autoPeriodCommonService;

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
        Map<String, Object> param = new HashMap<>();
        param.put("listingDate", plateDate);
        param.put("issueStage", DictCodeListExt4.发行阶段_已发行.getValue());
        param.put("finprodType2", DictCodeListExt2.平安资产类型_一级市场债.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("ISSUE_STATUS_02", DictCodeListExt1.发行状态_发行失败.getValue());
        List<FinTenderBondAssetDto> list = finTenderBoundAssetMapper.selectListing(param);
        if (CollectionUtils.isEmpty(list)) return;
        for (FinTenderBondAssetDto dto : list) {
            if (StringUtil.isNotEmpty(dto.getAppearMarketId())) {//上市代码不为空
                param.put("finprodType2", DictCodeList.平安资产类型_债券.getValue());
                param.put("finprodMarketId", dto.getAppearMarketId());
                FinProduct product = finTenderBoundAssetMapper.selectProduct(param);
                if (Objects.nonNull(product)) {
                    dto.setCirculationCode(product.getFinprodMarketId());
                    dto.setCirculationDate(plateDate);
                    save(dto, plateDate);
                    continue;
                }
                //根据 资产名称、资产简称、交易场所、登记托管机构、起息日、到期日、发行人、息票品种、利率类型、币种 进行去重
                List<InterfPayhsrcsecinfo> interList = finTenderBoundAssetMapper.getBySecId(dto.getAppearMarketId());
                if (CollectionUtils.isNotEmpty(interList) && interList.size() == 1) {
                    this.autoBondCoupon(interList.get(0));
                    dto.setCirculationCode(interList.get(0).getSecid());
                    dto.setCirculationDate(plateDate);
                    save(dto, plateDate);
                    continue;
                }
                if (CollectionUtils.isNotEmpty(interList) && interList.size() > 1) {
                    continue;
                }
                interList = finTenderBoundAssetMapper.getByParam(dto);
                if (CollectionUtils.isNotEmpty(interList) && interList.size() == 1) {
                    this.autoBondCoupon(interList.get(0));
                    dto.setCirculationCode(interList.get(0).getSecid());
                    dto.setCirculationDate(plateDate);
                    save(dto, plateDate);
                }
            } else {
                List<InterfPayhsrcsecinfo> interList = finTenderBoundAssetMapper.getByParam(dto);
                if (CollectionUtils.isNotEmpty(interList) && interList.size() == 1) {
                    this.autoBondCoupon(interList.get(0));
                    dto.setCirculationCode(interList.get(0).getSecid());
                    dto.setCirculationDate(plateDate);
                    save(dto, plateDate);
                }
            }
        }
        ;
    }

    /**
     * 数据入库
     *
     * @param dto
     * @param plateDate
     */
    private void save(FinTenderBondAssetDto dto, Date plateDate) {
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
        trdFinProductDealTotal.setTradeDate(plateDate);
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
        product.setCirculationCode(dto.getCirculationCode());
        product.setCirculationDate(dto.getCirculationDate());
        finProductMapper.updateForTenderBound(product);
        FinProductAdd productAdd = new FinProductAdd();
        productAdd.setFinprodId(dto.getFinprodId());
        productAdd.setIssueStatus(DictCodeListExt1.发行状态_发行成功.getValue());
        finProductAddMapper.updateForTenderBound(productAdd);
        //记录操作日志
        saveOperate(dto.getFinprodId(), dto.getRemark());
        //调用头寸
        BaseMap param = BaseMap.newMap().put("userId", "SYSTEM")
                .put("totalTradeId",trdFinProductDealTotal.getTotleTradeId())
                .put("finprodId",dto.getFinprodId())
                .put("circulationCode",product.getCirculationCode());
        PositionHttpRequest.sendSslt(param);
    }


    /**
     * 自动加券
     * @param payhsrcsecinfo
     */
    private void autoBondCoupon(InterfPayhsrcsecinfo payhsrcsecinfo) {
        //接口表使用source 和secId作为主键来进行加券
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
        oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        oprHisReq.setBusiDes(remark);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

}
