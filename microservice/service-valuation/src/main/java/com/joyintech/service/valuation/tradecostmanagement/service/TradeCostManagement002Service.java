package com.joyintech.service.valuation.tradecostmanagement.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.service.valuation.tradecostmanagement.dao.TradeCostManagement002Mapper;
import com.joyintech.service.valuation.tradecostmanagement.dto.TradeCostFinProductDto;
import com.joyintech.service.valuation.tradecostmanagement.dto.TradeCostManagement002Dto;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 交易费用管理 新建 更新
 *
 * @Author wangXiaoYong
 * @create 2019/11/19
 */
@Service("backTradeCostManagement002Service")
public class TradeCostManagement002Service extends BaseService<TradeCostManagement002Dto> {

    @Autowired
    private TradeCostManagement002Mapper tradeCostManagement002Mapper;

    // 金融产品交易
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    // 金融产品交易附属
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    // 交易费用明细
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;


    // 接口共通
    @Autowired
    private InterfCommonService interfCommonService;

    // 入账信息表
    @Autowired
    private BokDealAccountingMapper bokDealAccountingMapper;

    // 账务明细表
    @Autowired
    private BokDealAccountingDetailMapper bokDealAccountingDetailMapper;

    // 账务明细代码表
    @Autowired
    private BokDetailIdMapper bokDetailIdMapper;

    // 结算指令
    @Autowired
    private StlOrdersMapper stlOrdersMapper;

    // 结算指令扩展
    @Autowired
    private StlOrdersExtMapper stlOrdersExtMapper;

    @Autowired
    private DBUtil dbUtil;


    /**
     * 新建
     *
     * @param dto 页面实体
     */
    public Integer save(TradeCostManagement002Dto dto) {
        // 定义标的类金融产品变量
        TradeCostFinProductDto tradeCostFinProductDto
                = tradeCostManagement002Mapper.getTradeCostFinProductDto(dto.getBookSetId());

        //保存到交易表并入库
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());//採番名採番
        trdProductDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());//业务类型
        trdProductDeal.setFinprodId(tradeCostFinProductDto.getFinProdId());//金融产品代码
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_交易费用.getValue());
        trdProductDeal.setTradeDate(DateTime.parse(dto.getPayDate()).toDate());//交易日
        trdProductDeal.setMdate(DateTime.parse(dto.getmDate()).toDate());//到期日
        trdProductDeal.setSettleDate(DateTime.parse(dto.getPayDate()).toDate());//交割日
        trdProductDeal.setCcy(dto.getCcy());//币种
        trdProductDeal.setTradeAmt(dto.getFeeAmt());//交易金额
        trdProductDeal.setPs(DictCodeList.交易方向_付费.getValue());//交易方向
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态
        trdProductDeal.setIsClean(DictCodeList.是否_是.getValue());//是否结清
        trdProductDeal.setRegistOrg(dto.getRegistOrg());//登记托管机构
        trdProductDeal.setTradeMarket(DictCodeListExt1.估值交易市场_银行间.getValue());//交易场所 = 银行间

        trdProductDealMapper.insertSelective(trdProductDeal);

        //保存到金融产品交易附属表并入库
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
        trdProductDealAdd.setPortfolioId(tradeCostFinProductDto.getProtfolioId());// 投资组合代码
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());// 生成方式

        trdProductDealAddMapper.insertSelective(trdProductDealAdd);

        //保存到交易费用明细表并入库
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
        trdTradeFeeDetail.setFeeType(dto.getFeeType());// 一次性费用类型
        trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());// 是否随交易支付
        trdTradeFeeDetail.setFeeAmt(dto.getFeeAmt());// 费用金额
        trdTradeFeeDetail.setFeeId(dto.getFeeType());// 费用类型
        trdTradeFeeDetail.setIsPrepaid(dto.getIsPrepaid());// 是否待摊

        trdTradeFeeDetailMapper.insertSelective(trdTradeFeeDetail);

        //保存到入账信息表、账务明细表、账务明细代码表 并入库
        InterfProductDealBookCommonDto dealAccounting
                = interfCommonService.dealAccounting(trdProductDeal, tradeCostFinProductDto.getFinProdId());
        BokDealAccounting bokDealAccounting = dealAccounting.getBokDealAccounting();// 入账信息
        bokDealAccountingMapper.insertSelective(bokDealAccounting);
        BokDealAccountingDetail bokDealAccountingDetail = dealAccounting.getBokDealAccountingDetail();// 财务明细
        bokDealAccountingDetailMapper.insert(bokDealAccountingDetail);
        BokDetailId bokDetailId = dealAccounting.getBokDetailId();// 财务明细代码
        BokDetailIdKey bokDetailIdKey = new BokDetailIdKey();
        bokDetailIdKey.setBokDetailId(bokDetailId.getBokDetailId());
        bokDetailIdKey.setBooksetId(bokDetailId.getBooksetId());
        bokDetailIdMapper.deleteByPrimaryKey(bokDetailIdKey);//先删除 后添加
        if (bokDetailId.getBranch()==null){
            bokDetailId.setBranch(0L);
        }
        bokDetailIdMapper.insertSelective(bokDetailId);


        //保存到结算指令表、结算指令扩展表并入库
        Map<String, Object> map = interfCommonService.instructionGenerate(trdProductDeal,
                tradeCostFinProductDto.getFinProdId(), tradeCostFinProductDto.getFinProdType(), null);

        if (map.get("ORDERS") != null) {
            List<StlOrders> orders = (List<StlOrders>) map.get("ORDERS");// 结算指令
            for (StlOrders order : orders) {
                stlOrdersMapper.insert(order);
            }
        }
        if (map.get("ORDERS_EXT") != null) {
            List<StlOrdersExt> ordersExts = (List<StlOrdersExt>) map.get("ORDERS_EXT");// 结算指令扩展
            for (StlOrdersExt ordersExt : ordersExts) {
                stlOrdersExtMapper.insert(ordersExt);
            }
        }
        return 1;

    }


    /**
     * 修改
     *
     * @param dto 页面实体
     */
    public Integer update(TradeCostManagement002Dto dto) {

        // 金融产品交易
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setTradeId(dto.getTradeId());// 交易编号
        trdProductDeal.setTradeDate(DateTime.parse(dto.getPayDate()).toDate());//交易日
        trdProductDeal.setMdate(DateTime.parse(dto.getmDate()).toDate());//到期日
        trdProductDeal.setSettleDate(DateTime.parse(dto.getPayDate()).toDate());//交割日
        trdProductDeal.setCcy(dto.getCcy());//币种
        trdProductDeal.setTradeAmt(dto.getFeeAmt());//交易金额
        trdProductDeal.setRegistOrg(dto.getRegistOrg());//登记托管机构
        trdProductDealMapper.updateByPrimaryKeySelective(trdProductDeal);

        // 费用交易明细
        TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
        trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());// 交易编号 不给修改
        trdTradeFeeDetail.setFeeType(dto.getFeeType());
        trdTradeFeeDetail.setFeeId(dto.getFeeType());// 费用类型 不给修改
        trdTradeFeeDetail.setFeeAmt(dto.getFeeAmt());// 费用金额
        trdTradeFeeDetail.setIsPrepaid(dto.getIsPrepaid());// 是否待摊
        trdTradeFeeDetailMapper.updateByPrimaryKeySelective(trdTradeFeeDetail);

        // 更新入账信息表
        BokDealAccounting bokDealAccounting = new BokDealAccounting();
        bokDealAccounting.setBooksetId(dto.getBookSetId());
        bokDealAccounting.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        bokDealAccounting.setBusiNo(dto.getTradeId());
        bokDealAccounting.setBookDate(DateTime.parse(dto.getPayDate()).toDate());// 入账日
        bokDealAccounting.setBooksetDate(DateTime.parse(dto.getPayDate()).toDate());// 录入日
        bokDealAccounting.setHappenDate(DateTime.parse(dto.getPayDate()).toDate());// 实际发生日
        bokDealAccountingMapper.updateByPrimaryKeySelective(bokDealAccounting);

        // 更新指令表
        StlOrders stlOrders = new StlOrders();
        stlOrders.setSettleAmt(dto.getFeeAmt());
        stlOrders.setSettleDate(DateUtil.parse(dto.getPayDate()));
        stlOrders.setCcy(dto.getCcy());
        stlOrders.setTradeId(dto.getTradeId());
        tradeCostManagement002Mapper.updateStlOrdersByTradeId(stlOrders);

        return 1;
    }


}
