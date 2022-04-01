/*
 * 文件名：TaxBalanceQuery001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：leipeng
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dao.TrdOutRightRepoValuationTrade001Mapper;
import com.joyintech.service.valuation.trademanage.trdbuyoutrepovaluation.dto.TrdOutRightRepoValuationTrade003Dto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(value="serviceTrdOutRightRepoValuationTrade002Service")
public class TrdOutRightRepoValuationTrade002Service extends CommonExportService<TrdOutRightRepoValuationTrade003Dto>{

    @Autowired
    private TrdOutRightRepoValuationTrade001Mapper trdOutRightRepoValuationTrade001Mapper;

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
        return this.queryForPageForSelectReturn(page, length, ()->trdOutRightRepoValuationTrade001Mapper.findPortfolioId(params));
    }


    //资产代码下拉框
    public Page<SelectOptionDto> findFinprodId(int page, int length,Map<String, Object> params){
        return this.queryForPageForSelectReturn(page, length, ()->trdOutRightRepoValuationTrade001Mapper.findFinprodId(params));
    }


    /**
     *
     * 工作流完结之后的操作
     * <功能详细描述>
     * @param entity 实体
     * @see
     * @since 1.0
     */
    public void doComplete(TrdOutRightRepoValuationTrade003Dto entity) {

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
            String nextId = this.getNextId("ORTRADEID_SEQUENCE");
            //平台日
            String dateStr= DateUtil.format(iPlatformDate.getSysDate(),DateUtil.DEFAULT_OTHER_FORMAT);//yyyyMMdd
            // 拼接
            tradeId = "JY"+"OREND"+dateStr+ nextId;
            // 查询是否重复
            int num = trdOutRightRepoValuationTrade001Mapper.selectCountByTradeId(tradeId);
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
        return trdOutRightRepoValuationTrade001Mapper.getNextId(sequenceName);
    }


    /**
     * 根据交易编号查询数据
     */
    public TrdOutRightRepoValuationTrade003Dto queryTradeInfoByTradeId01(String tradeId){
        return trdOutRightRepoValuationTrade001Mapper.queryTradeInfoByTradeId01(tradeId);
    }




}
