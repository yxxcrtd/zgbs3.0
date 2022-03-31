/*
 * 文件名：MstNetAssetQuotationInfo001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.basicdata.marketdata.mstrateassetquotationinfo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.basicdata.marketdata.mstrateassetquotationinfo.dao.ComRateAssetQuotationInfo001Mapper;
import com.joyintech.service.basicdata.marketdata.mstrateassetquotationinfo.dto.ComRateAssetQuotationInfo001Dto;
import com.joyintech.tams.common.base.dao.FinProductValuationMapper;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 计划类（利率型）资产估值行情信息查询service
 *
 * @author lby
 * @version 1.0 2020年10月20日
 * @see ComRateAssetQuotationInfo001Service
 * @since 1.0
 */

@Service(value="serviceMstRateAssetQuotationInfo001Service")
@Transactional
public class ComRateAssetQuotationInfo001Service extends BaseService<ComRateAssetQuotationInfo001Dto> {

    /**
     * 计划类（利率型）资产估值行情信息mapper
     */
    @Autowired
    private ComRateAssetQuotationInfo001Mapper mstRateAssetQuotationInfo001Mapper;

    /**
     * 场外金融产品估值Mapper
     */
    @Autowired
    private FinProductValuationMapper finProductValuationMapper;


    /**
     * 查询按钮点击事件
     * 通过条件查询数据
     *
     * @param pageParam 页面参数
     * @return page
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<ComRateAssetQuotationInfo001Dto> selectForPage(int page, int length, Map<String, Object> pageParam) throws Exception {
        pageParam.put("PROFITTYPE_VALUE_02", DictCodeListExt2.收益类型_预期收益.getValue());
        // 估值不接入finprod_type2，故用估值的金融产品类型做判断
        pageParam.put("TYPE_GUZHI_F17", DictCodeListExt1.金融产品类型_计划类资产.getValue());
        // pageParam.put("TYPE_TOUGUAN_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        // pageParam.put("TYPE_TOUGUAN_F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        return super.queryForPage(page, length, () -> mstRateAssetQuotationInfo001Mapper.selectForPage(pageParam));
    }


    /**
     * 查询界面资产代码联想文本框
     * 可以通过输入的资产代码模糊查询
     * @param map 查询参数
     * @return  资产名称和资产代码列表
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getAssetCode(Map<String, Object> map) {
        return mstRateAssetQuotationInfo001Mapper.getAssetCode(map);
    }
    /**
     * 资产名称联想文本框
     * 可以通过输入的资产名称模糊查询
     * @param map 查询参数
     * @return  资产名称和资产代码列表
     * @see
     * @since 1.0
     */
    public List<SuggestDto> getAssetName(Map<String, Object> map) {
        return mstRateAssetQuotationInfo001Mapper.getAssetName(map);
    }

    public List<SelectOptionDto> getAssetCodeList(Map<String, Object> map) {
        return mstRateAssetQuotationInfo001Mapper.getAssetCodeList(map);
    }

    public ComRateAssetQuotationInfo001Dto getFinProdNameAndFinprodId(Map<String, Object> map) {
        return mstRateAssetQuotationInfo001Mapper.getFinProdNameAndFinprodId(map);
    }

    public int insert(ComRateAssetQuotationInfo001Dto entity) {
        Date sysDate = DateUtil.getSysDate() != null ? DateUtil.parse(DateUtil.getSysDate()) : DateUtil.now();
//        entity.setPublishDate(entity.getPublishDate() == null ? sysDate : entity.getPublishDate());

        //修改 公布日期默认=估值日期  yangchen 20220331
        entity.setPublishDate(entity.getValDate());
        // 一体化对应etliu start
        // entity.setInputType(BussinessDictCodeList.录入方式_手工维护.getValue());
        entity.setInputType(DictCodeListExt1.录入方式_手工录入.getValue());
        // 一体化对应etliu end
        entity.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        //entity.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_计划类资产.getValue());
        //投管接口调用
//        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
//        investInterfCommDto.setPageId("MstNetAssetQuotationInfo002");
//        investInterfCommDto.setPortfolioId(entity.getFinProdMarketId());
//        investInterfCommDto.setFinProductValuation(entity);
//        investInterfCommService.doInvestInterf(investInterfCommDto);
        return finProductValuationMapper.insertSelective(entity);
    }

    public List<ComRateAssetQuotationInfo001Dto> selectByIdAndDate(Map<String, Object> map) {
        return mstRateAssetQuotationInfo001Mapper.selectByIdAndDate(map);
    }

    public ComRateAssetQuotationInfo001Dto selectMstRateAsset(Map<String, Object> param) {
        return mstRateAssetQuotationInfo001Mapper.selectMstRateAsset(param);
    }

    public int update(ComRateAssetQuotationInfo001Dto entity) {
        entity.setFinprodId(entity.getFinProdMarketId());
        entity.setInputType(DictCodeListExt1.录入方式_手工录入.getValue());
        return mstRateAssetQuotationInfo001Mapper.update(entity);
    }

    public int delete(ComRateAssetQuotationInfo001Dto entity) {
        entity.setFinprodId(entity.getFinProdMarketId());
        return mstRateAssetQuotationInfo001Mapper.delete(entity);
    }

    public List<ComRateAssetQuotationInfo001Dto> selectForExport(Map<String, Object> param) {
        param.put("PROFITTYPE_VALUE_02", DictCodeListExt2.收益类型_预期收益.getValue());
        param.put("TYPE_GUZHI_F17", DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        param.put("TYPE_TOUGUAN_F18", DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        return mstRateAssetQuotationInfo001Mapper.selectForPage(param);
    }
}
