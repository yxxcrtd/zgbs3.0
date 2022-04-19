package com.joyintech.tams.invest.batch.tenderbond.service;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductBarterTotalMapper;
import com.joyintech.tams.common.base.dao.TrdFinProductDealTotalMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.TrdFinProductBarterTotal;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.batch.tenderbond.dao.FinTenderBondAssetMapper;
import com.joyintech.tams.invest.batch.tenderbond.dto.FinTenderBondAssetDto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新债标的自动上市流通
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
    public void listingAndCirculation(Date plateDate) {
        Map<String,Object> param = new HashMap<>();
        param.put("listingDate",plateDate);
        param.put("issueStage", DictCodeListExt4.发行阶段_已发行.getValue());
        param.put("finprodType2", DictCodeListExt2.平安资产类型_一级市场债.getValue());
        param.put("TRADE_PLACE_01", DictCodeListExt1.交易场所_银行间.getValue());
        param.put("TRADE_PLACE_02", DictCodeListExt1.交易场所_商业银行柜台.getValue());
        param.put("ISSUE_STATUS_02",DictCodeListExt1.发行状态_发行失败.getValue());
        List<FinTenderBondAssetDto> list = finTenderBoundAssetMapper.selectListing(param);
        if(CollectionUtils.isEmpty(list))return;
        list.forEach(dto -> {
            /**
             * 标的类金融产品 = 调用【标的类金融产品】查询接口 参数： 金融产品类型（投管分类）=数据字典.金融产品类型（投管）.债券 and 标的类金融产品.FINPROD_MARKET_ID=新债.上市代码
             * IF 标的类金融产品 == NULL
             *     债券市场信息接口信息= 查询接口表INTERF_PAYHSRCSECINFO  参数：接口表.债券ID=新债.上市代码
             * END IF
             * IF 债券市场信息接口信息 !=NULL AND 债券市场信息接口信息.size() > 1
             *  债券市场信息接口信息 = 根据 资产名称、资产简称、交易场所、登记托管机构、起息日、到期日、发行人、息票品种、利率类型、币种 进行去重
             * END
             * IF 债券市场信息接口信息 !=NULL AND 债券市场信息接口信息.size() == 1
             * 调用【债券自动加券】接口执行自动加券//自动加券 CommonAutoCouponService
             * END
             * IF 标的类金融产品 == NULL AND 债券市场信息接口信息 == NULL
             *     债券市场信息接口信息= 查询接口表INTERF_PAYHSRCSECINFO  参数：新债.资产名称、新债.资产简称、新债.交易场所、新债.登记托管机构、新债.起息日、新债.到期日、新债.发行人、新债.息票品种、新债.利率类型、新债.币种
             * END IF
             * IF 债券市场信息接口信息 != NULL AND 债券市场信息接口信息.size() == 1
             *    调用【债券自动加券】接口执行自动加券//自动加券 CommonAutoCouponService
             * END
             */
            //1.上市代码不为空
            if(StringUtil.isNotEmpty(dto.getAppearMarketId())) {

            }else {

            }
        });
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
        product.setCirculationCode(dto.getAppearMarketId());
        product.setCirculationDate(dto.getListingDate());
        finProductMapper.updateForTenderBound(product);
        FinProductAdd productAdd = new FinProductAdd();
        productAdd.setFinprodId(dto.getFinprodId());
        productAdd.setIssueStatus(DictCodeListExt1.发行状态_发行成功.getValue());
        finProductAddMapper.updateForTenderBound(productAdd);

        //TODO 发送头寸
        //记录操作日志
        saveOperate(dto.getFinprodId(),dto.getRemark());
    }

    /**
     * 保存操作类型
     *
     * @param tradeId
     * @param remark
     */
    public void saveOperate(String tradeId,String remark) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(tradeId);
        oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
        oprHisReq.setBusiDes(remark);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

}
