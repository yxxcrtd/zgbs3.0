/*
 * 文件名：TrdCommandDispatcher001Mapper.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：wangkang
 * 修改时间：2018年9月12日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.trade.investtrade.investdealquery.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.service.trade.investtrade.investdealquery.dto.TrdInvestDealQueryPdfDto;
import com.joyintech.tams.constant.DictCodeListExt3;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.trade.investtrade.investdealquery.dao.TrdInvestDealQuery001Mapper;
import com.joyintech.service.trade.investtrade.investdealquery.dto.AssetTransferExport001Dto;
import com.joyintech.service.trade.investtrade.investdealquery.dto.TrdInvestDealQuery001Dto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;

@Service("backTrdInvestDealQuery001Service")
@Transactional(rollbackFor=Exception.class)
public class TrdInvestDealQuery001Service extends BaseService<TrdInvestDealQuery001Dto>{
	/**
	 * 手动书写mapper
	 */
	@Autowired
	private TrdInvestDealQuery001Mapper trdInvestDealQuery001Mapper;
	/**
	 * 标的类金融产品mapper
	 */
	@Autowired
	private FinProductMapper finProductMapper;
	/**
	 * 流程中的查询
	 * @param page
	 * @param length
	 * @param pageParam
	 * @return
	 * @throws Exception
	 */
	public Page<TrdInvestDealQuery001Dto> selectForPage(int page, int length,Map<String, Object> pageParam)
	{
//		setParams(pageParam);
		Page<TrdInvestDealQuery001Dto>  pageLimit = super.queryForPage(page, length, () -> trdInvestDealQuery001Mapper.selectForPage(pageParam));
		finprodType2PinAn(pageLimit.getRoot());
		return pageLimit;
		
	}
	/**
	 * 有效和撤销时的搜索
	 * @param page
	 * @param length
	 * @param pageParam
	 * @return
	 * @throws Exception
	 */
	public Page<TrdInvestDealQuery001Dto> validSelectForPage(int page, int length,Map<String, Object> pageParam)
	{
//		setParams(pageParam);
	    Page<TrdInvestDealQuery001Dto>  pageLimit = super.queryForPage(page, length, () -> trdInvestDealQuery001Mapper.validSelectForPage(pageParam));
	    finprodType2PinAn(pageLimit.getRoot());
        return pageLimit;
	}
	/**
	 * 撤销时的搜索
	 * @param page
	 * @param length
	 * @param pageParam
	 * @return
	 * @throws Exception
	 */
	public Page<TrdInvestDealQuery001Dto> cancelSelectForPage(int page, int length,Map<String, Object> pageParam)
	{
//		setParams(pageParam);
	    Page<TrdInvestDealQuery001Dto>  pageLimit = super.queryForPage(page, length, () -> trdInvestDealQuery001Mapper.cancelSelectForPage(pageParam));
	    finprodType2PinAn(pageLimit.getRoot());
        return pageLimit;
	}
	/**
	 * 状态为空时的查询
	 * @param page
	 * @param length
	 * @param pageParam
	 * @return
	 * @throws Exception
	 */
	public Page<TrdInvestDealQuery001Dto> alllSelectForPage(int page, int length,Map<String, Object> pageParam)
	{
//		setParams(pageParam);
	    Page<TrdInvestDealQuery001Dto>  pageLimit = super.queryForPage(page, length, () -> trdInvestDealQuery001Mapper.allSelectForPage(pageParam));
	    finprodType2PinAn(pageLimit.getRoot());
        return pageLimit;
	}
	
	/*private void setParams(Map<String, Object> pageParam) {
        pageParam.put("LEND_01", DictCodeList.回购拆借方向_拆入.getValue());
        pageParam.put("LEND_02", DictCodeList.回购拆借方向_拆出.getValue());
        pageParam.put("LEND_03", DictCodeList.回购拆借方向_正回购.getValue());
        pageParam.put("LEND_04", DictCodeList.回购拆借方向_逆回购.getValue());
        pageParam.put("LEND_05", DictCodeList.回购拆借方向_融入.getValue());
        pageParam.put("LEND_06", DictCodeList.回购拆借方向_融出.getValue());
        pageParam.put("F08", DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
        pageParam.put("F09", DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
        pageParam.put("F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        pageParam.put("F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        pageParam.put("F04", DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
        pageParam.put("F05", DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
        pageParam.put("F29", DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
        pageParam.put("F30", DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
    }*/
	/**
	 * 委托代码联想框
	 * @param entrustId
	 * @return
	 */
	public List<SuggestDto> getEntrustId(String entrustId){
		return trdInvestDealQuery001Mapper.getEntrustId(entrustId);
	}
	/**
	 * 组合代码联想框
	 * @param portfolioId
	 * @return
	 */
	public List<SuggestDto> getPortfolioIds(String portfolioId){
		Map<String,Object> map = new HashMap<>();
		map.put("portfolioId",portfolioId);
		map.put("portfolioType",DictCodeListExt1.组合类型_委外组合.getValue());
		return trdInvestDealQuery001Mapper.getPortfolioIds(map);
	}
	/**
	 * 资产代码联想框
	 * @param portfolidId
	 * @return
	 */
	public List<SuggestDto> getFinprodMarketIds(String portfolidId){
		return trdInvestDealQuery001Mapper.getFinprodMarketIds(portfolidId);
	}
	/**
	 * 将金融产品类型转为平安数据字典
	 * <功能详细描述>
	 * @param list
	 * @return
	 * @see
	 * @since 1.0
	 */
	public List<TrdInvestDealQuery001Dto> finprodType2PinAn(List<TrdInvestDealQuery001Dto> list){
	    if(!list.isEmpty()){
            for(TrdInvestDealQuery001Dto dto:list){
                String finType2=dto.getFinprodType2();
				dto.setFinprodType2(transferFinprodType2(finType2, dto.getFinprodId()));
            }    
        }
	    return list;
	}

	/**
	 * 将金融产品类型转换为平安资产类型
	 * @param finType2
	 * @param finprodId
	 * @return
	 */
	private String transferFinprodType2(String finType2, String finprodId){
		if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
			return DictCodeList.平安资产类型_债券.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_基金.getValue())){
			return DictCodeList.平安资产类型_基金.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
			return DictCodeList.平安资产类型_存款.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_拆入.getValue())
				|| StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_拆出.getValue())){
			return DictCodeList.平安资产类型_拆借.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
				|| StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())){
			return DictCodeList.平安资产类型_质押式回购.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue())
				|| StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())){
			return DictCodeList.平安资产类型_买断式回购.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
			return DictCodeList.平安资产类型_项目类资产.getValue();
		}
		else if(StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
			FinProduct finProduct=finProductMapper.selectByPrimaryKey(finprodId);
			if(StringUtil.equals(finProduct.getProfitType(), DictCodeList.收益类型_净值型.getValue())){
				return DictCodeList.平安资产类型_计划类资产_净值型.getValue();
			}
			else if(StringUtil.equals(finProduct.getProfitType(), DictCodeList.收益类型_收益型.getValue())){
				return DictCodeList.平安资产类型_计划类资产_利率型.getValue();
			}
			else if(StringUtil.equals(finProduct.getProfitType(), DictCodeList.收益类型_货币型.getValue())){
				return DictCodeList.平安资产类型_计划类资产_货币型.getValue();
			}
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_即期.getValue())) {
			return DictCodeListExt2.平安资产类型_外汇即期.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_远期.getValue())) {
			return DictCodeListExt2.平安资产类型_外汇远期.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_掉期.getValue())) {
			return DictCodeListExt2.平安资产类型_外汇掉期.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue())) {
			return DictCodeListExt2.平安资产类型_债券借贷.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())) {
			return DictCodeListExt2.平安资产类型_债券借贷.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_利率互换.getValue())) {
			return DictCodeListExt2.平安资产类型_利率互换.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
			return DictCodeListExt2.平安资产类型_股票.getValue();
		}
		else if (StringUtil.equals(finType2, DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue())) {
			return DictCodeListExt2.平安资产类型_一级市场债.getValue();
		}
		// add by etliu 20210421 权益类资产相关代码合并 start
		else if (StringUtil.equals(finType2, DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue())){
			return DictCodeListExt3.平安资产类型_权益类资产.getValue();
		}
		// add by etliu 20210421 权益类资产相关代码合并 end
		return StringUtil.EMPTY;
	}

	/**
     * check是否为转仓交易
     * 
     * @param teadIds
     * @return
     * @see
     * @since 1.0
     */
    public int selectCheck(List<String> teadIds) {
        return trdInvestDealQuery001Mapper.selectCheck(teadIds);
    }
    
    /**
     * 债券类交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByBond(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByBond(teadIds);
    }
     
    /**
     * 存款类交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByDeposit(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByDeposit(teadIds);
    }
    
    /**
     * 计划类利率型交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByPlanLl(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByPlanLl(teadIds);
    }
     
    /**
     * 计划类净值型交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByPlanJz(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByPlanJz(teadIds);
    }

    /**
     * 计划类货币型交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByPlanHb(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByPlanHb(teadIds);
    }

    /**
     * 基金类交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByFund(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByFund(teadIds);
    }
    
    /**
     * 项目类交易
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public List<AssetTransferExport001Dto> selectDataByProject(List<String> teadIds){
        return trdInvestDealQuery001Mapper.selectDataByProject(teadIds);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectInvestRateByPlan(AssetTransferExport001Dto dto) {
        return trdInvestDealQuery001Mapper.selectInvestRateByPlan(dto);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal selectInvestRateByProject(AssetTransferExport001Dto dto) {
        return trdInvestDealQuery001Mapper.selectInvestRateByProject(dto);
    }

	/**
	 * 查询导出pdf数据 add by zengchengfa
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<TrdInvestDealQueryPdfDto> selectExportPdfList(Map<String, Object> param) throws Exception{
		if (StringUtil.isNotEmpty(param.get("selectedItem"))){
			List<String> selectedItem = StringUtil.splitToList(",",(String)param.get("selectedItem"));
			if(selectedItem.size() > 0){
				param.put("tradeIdList",selectedItem);
			}
		}
		List<TrdInvestDealQueryPdfDto> pdfDtos = trdInvestDealQuery001Mapper.selectExportPdfList(param);
		// ZGZGST-3548:将金融产品类型转换为平安资产类型 fuyongsheng 20210927 edit start
		if(CollectionUtils.isNotEmpty(pdfDtos)){
			pdfDtos.forEach(pdfDto-> pdfDto.setFinprodType2(transferFinprodType2(pdfDto.getFinprodType2(),
					pdfDto.getFinprodId())));
		}
		// ZGZGST-3548:将金融产品类型转换为平安资产类型 fuyongsheng 20210927 edit end
		return pdfDtos;
	}
}
