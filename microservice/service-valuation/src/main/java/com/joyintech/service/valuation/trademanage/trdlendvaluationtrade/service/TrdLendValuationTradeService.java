/*
 * 文件名：TaxBalanceQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.dao.TrdLendValuationTradeMapper;
import com.joyintech.service.valuation.trademanage.trdlendvaluationtrade.dto.TrdLendValuationTradeDto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service(value="serviceTrdLendValuationTradeService")
public class TrdLendValuationTradeService extends CommonExportService<TrdLendValuationTradeDto>{

    @Autowired
    private TrdLendValuationTradeMapper trdLendValuationTradeMapper;

    /**
     * 平台日获取接口
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;

    @Autowired
    private TrdBondTradeMapper trdBondTradeMapper;

    /**
     * 产品代码下拉框
     */
    public Page<SelectOptionDto> findPortfolioId(int page, int length,Map<String, Object> params) {
        return this.queryForPageForSelectReturn(page, length, ()->trdLendValuationTradeMapper.findPortfolioId(params));
    }



    /**
     *
     * 工作流完结之后的操作
     * <功能详细描述>
     * @param entity 实体
     * @see
     * @since 1.0
     */
    public void doComplete(TrdLendValuationTradeDto entity) {

    }

    /**
     *
     * 获取分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     * @param pageParam Map<String, Object>
     * @return Page<MstSharePriceDto>
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    public Page<TrdLendValuationTradeDto> selectForPage(int page, int length, Map<String, Object> pageParam) throws Exception {
        //产品代码
        String portfolioId = (String)pageParam.get("portfolioId");
        String[] portfolioIdArr = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            portfolioIdArr = portfolioId.split(",");
        }
        pageParam.put("portfolioIdArr", portfolioIdArr);

        // 获取交易类型
        String ps = (String)pageParam.get("ps");
        // 交易类型多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
//        pageParam.put("psArr", psArr);

        List<String> finprodTypeList  = new ArrayList();
        List<String> psArr01List  = new ArrayList();
        //判断传入的交易类型是否为空
        if(psArr!=null){
            for (String psStr : psArr) {
                if(StringUtil.equals(psStr,"01")){
                    finprodTypeList.add("F01");
                    psArr01List.add("T01");
                }else if(StringUtil.equals(psStr,"02")){
                    finprodTypeList.add("F01");
                    psArr01List.add("T02");
                }else if(StringUtil.equals(psStr,"03")){
                    finprodTypeList.add("F08");
                    psArr01List.add("T07");
                }else{
                    finprodTypeList.add("F09");
                    psArr01List.add("T07");
                }
            }
        }
        String[] finprodTypeArr=finprodTypeList.toArray(new String[finprodTypeList.size()]);
        String[] psArr01=psArr01List.toArray(new String[psArr01List.size()]);
        pageParam.put("finprodTypeArr", finprodTypeArr);
        pageParam.put("psArr01", psArr01);
        return this.queryForPage(page, length,
                () -> trdLendValuationTradeMapper.selectDeal(pageParam));
    }


    /**
     * 策略代码生成
     *
     * @return
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        String tradeId = null;
        while (true) {
            // 获取自增数
            String nextId = this.getNextId("PLANTRADEID_SEQUENCE");
            //平台日
            String dateStr= DateUtil.format(iPlatformDate.getSysDate(),DateUtil.DEFAULT_OTHER_FORMAT);//yyyyMMdd
            // 拼接
            tradeId = "JY"+"PLAN"+dateStr+ nextId;
            // 查询是否重复
            int num = trdLendValuationTradeMapper.selectCountByTradeId(tradeId);
            // 如果自增的策略代码不存在，则跳出循环
            if (num==0) {
                break;
            }
        }
        return tradeId;
    }

    /**
     * 获取自增序列值
     *
     * @param sequenceName 序列名
     * @return
     */
    public String getNextId(String sequenceName) {
        return trdLendValuationTradeMapper.getNextId(sequenceName);
    }


    //根据交易编号查询
    public TrdLendValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return trdLendValuationTradeMapper.queryTradeInfoByTradeId(tradeId);
    }



    //删除
    public int delete(TrdLendValuationTradeDto dto) {
        int count=0;
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId",dto.getPortfolioId());   //产品代码
        params.put("tradeId",dto.getTradeId());

        List<TrdProductDeal> listDeal = new ArrayList<>();
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getTradeId());
        listDeal.add(entity);
        // 1.估值核算交易扩展表 TRD_PRODUCT_DEAL_ADD
        count += valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
        // 2.交易费用明细表 TRD_TRADE_FEE_DETAIL
        count += valCommonBatchMapper.delTradeFeeDetailByTradeId(listDeal);
        // 3.入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        count += valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
        // 4.入账信息表 BOK_DEAL_ACCOUNTING
        count += valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
        // 5.结算指令扩展表 STL_ORDERS_EXT
        count += valCommonBatchMapper.delOrdersExtByTradeId(listDeal);
        // 6.结算指令表 STL_ORDERS
        count += valCommonBatchMapper.delOrdersByTradeId(listDeal);
        // 7.金融产品交易 TRD_PRODUCT_DEAL
        count += valCommonBatchMapper.delProductDealByTradeId(listDeal);
        // 8.金融产品实物交易 TRD_FIN_PRODUCT_BARTER
        count += valCommonBatchMapper.delTrdFinProductBarterByTradeId(listDeal);
        // 9.账务明细代码表 BOK_DETAIL_ID
        count += trdBondTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }


    /**
     *
     * 获取不分页查询结果（sharesId  dealDateString）
     * <功能详细描述>
     */
    public List<TrdLendValuationTradeDto> queryPageList(Map<String, Object> params)  {

        //产品代码
        String portfolioId = (String)params.get("portfolioId");
        String[] portfolioIdArr = null;
        if(StringUtil.isNotEmpty(portfolioId)){
            portfolioIdArr = portfolioId.split(",");
        }
        params.put("portfolioIdArr", portfolioIdArr);

        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);

        return trdLendValuationTradeMapper.selectDeal(params);
    }
}
