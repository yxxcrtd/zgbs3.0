package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplCalendar;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.settle.batch.datacreate.constant.StlDataCreateConstant;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealWriteBaseDto;
import com.joyintech.tams.settle.batch.datadeal.function.PersonalityFunction;
import com.joyintech.tams.settle.common.dto.FundInfoDto;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 深中登SJSJG
 * 
 * @author 陈卓
 * @date 2020/7/11
 * @see StlDataDealSzdSjsjgProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdSjsjgProcessService {

    public static final List<String> LIST_ZQLB_SHARES = Arrays.asList(
            StlDataDealProcessConstant.SJSMX_ZQLB_00,
            StlDataDealProcessConstant.SJSMX_ZQLB_03,
            StlDataDealProcessConstant.SJSMX_ZQLB_07,
            StlDataDealProcessConstant.SJSMX_ZQLB_30,
            StlDataDealProcessConstant.SJSMX_ZQLB_37
    );
    private static final List<String> LIST_ZQLB_BONDS = Arrays.asList(
            StlDataDealProcessConstant.SJSMX_ZQLB_10,
            StlDataDealProcessConstant.SJSMX_ZQLB_11,
            StlDataDealProcessConstant.SJSMX_ZQLB_12,
            StlDataDealProcessConstant.SJSMX_ZQLB_1A,
            StlDataDealProcessConstant.SJSMX_ZQLB_1C,
            StlDataDealProcessConstant.SJSMX_ZQLB_1E,
            StlDataDealProcessConstant.SJSMX_ZQLB_1F,
            StlDataDealProcessConstant.SJSMX_ZQLB_1H,
            StlDataDealProcessConstant.SJSMX_ZQLB_1J,
            StlDataDealProcessConstant.SJSMX_ZQLB_1R,
            StlDataDealProcessConstant.SJSMX_ZQLB_1S,
            StlDataDealProcessConstant.SJSMX_ZQLB_1X,
            StlDataDealProcessConstant.SJSMX_ZQLB_1Y,
            StlDataDealProcessConstant.SJSMX_ZQLB_1Z
    );
    private static final List<String> LIST_ZQLB_FUNDS = Arrays.asList(
            StlDataDealProcessConstant.SJSMX_ZQLB_15,
            StlDataDealProcessConstant.SJSMX_ZQLB_16,
            StlDataDealProcessConstant.SJSMX_ZQLB_18,
            StlDataDealProcessConstant.SJSMX_ZQLB_EF,
            StlDataDealProcessConstant.SJSJG_ZQLB_EJ
    );

    @Autowired
    private StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    private SettleCommonService settleCommonService;

    @Autowired(required = false)
    private StlDownDataDealService stlDownDataDealService;
	
	@Autowired
    protected StlDownDataDealMapper stlDownDataDealMapper;

    @Autowired
    protected FinProductMapper finProductMapper;

    @Autowired
    protected TrdCommonBaseService trdCommonBaseService;

  /*  @Autowired
    protected ICalendar iCalendar;*/

    // 生成数据清算批处理step2 processor的结果Dto(深中登SJSJG)
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdSjsjgWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) throws ParseException {
        // 核算信息全量数据
        Map<String, List<StlDownDataDealAccountInfoDto>> map = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(true);
        // 资产类型
        String finprodType = stlDownDataDealOperatorDto.getFinprodType();
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), finprodType)) {
        	// 将SzdSjsjg清算数据List中，证券类别是PGQZ的数据去除，单独放在PGQZ的SJSJG清算数据List里面做后续处理。
    		List<SzdSjsjg> pgqzSzdSjsjgList = new ArrayList<SzdSjsjg>();
    		List<SzdSjsjg> notPgqzSzdSjsjgList = new ArrayList<SzdSjsjg>();
        	if (stlDownDataDealOperatorDto != null && stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto() != null &&
       			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList() != null && 
       			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList().size() > 0) {
        		for (SzdSjsjg szdSjsjg : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList()) {
        			if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.YWLB_PGQZ) ||
                            StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_TGSS)) {
        				pgqzSzdSjsjgList.add(szdSjsjg);
        			}
        			else {
        				notPgqzSzdSjsjgList.add(szdSjsjg);
        			}
        		}
        		stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().setSjsjgList(notPgqzSzdSjsjgList);
        	}
        	stlDownDataDealWriteDto = secTypeIsBondDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
         	stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
            // PGQZ的SJSJG清算数据List数据单独处理
            if (pgqzSzdSjsjgList != null && pgqzSzdSjsjgList.size() > 0) {
            	List<FinProductQuto> finProductQutoList = generatorBondFinProductQutoList(pgqzSzdSjsjgList, map, stlDownDataDealOperatorDto);
            	stlDownDataDealWriteDto.setFinProductQutoList(finProductQutoList);
            }
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), finprodType)) {
        	stlDownDataDealWriteDto = secTypeIsRepoDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
         	stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), finprodType)) {
        	stlDownDataDealWriteDto = secTypeIsFundDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), finprodType)) {
        	// 将SzdSjsjg清算数据List中，证券类别是PGQZ的数据去除，单独放在PGQZ的SJSJG清算数据List里面做后续处理。
    		List<SzdSjsjg> pgqzSzdSjsjgList = new ArrayList<SzdSjsjg>();
    		List<SzdSjsjg> notPgqzSzdSjsjgList = new ArrayList<SzdSjsjg>();
        	if (stlDownDataDealOperatorDto != null && stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto() != null &&
       			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList() != null && 
       			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList().size() > 0) {
        		for (SzdSjsjg szdSjsjg : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList()) {
        			if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.YWLB_PGQZ) || 
                            (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_TGSS) &&
                                StringUtil.equals(szdSjsjg.getJgltlx(), StlDataCreateConstant.JSMX_LTLX))) {
        				pgqzSzdSjsjgList.add(szdSjsjg);
        			}
        			else {
        				notPgqzSzdSjsjgList.add(szdSjsjg);
        			}
        		}
        		stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().setSjsjgList(notPgqzSzdSjsjgList);
        	}
        	stlDownDataDealWriteDto = secTypeIsSharesDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
            // PGQZ的SJSJG清算数据List数据单独处理
            if (pgqzSzdSjsjgList != null && pgqzSzdSjsjgList.size() > 0) {
            	List<FinProductQuto> finProductQutoList = generatorSharesFinProductQutoList(pgqzSzdSjsjgList, map, stlDownDataDealOperatorDto);
            	stlDownDataDealWriteDto.setFinProductQutoList(finProductQutoList);
            }
        }
     	
        return stlDownDataDealWriteDto;
    }

    /**
     * 债券类型SJSMX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 结算数据
     * @throws ParseException
     */
    private StlDownDataDealWriteDto secTypeIsBondDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        
        
        // 债转股的话，输入参数需要单独处理。根据股的数据重新设置清算本金和收付净额
    	List<SzdSjsjg> szdSjsjgList = stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList();
    	if (szdSjsjgList != null && szdSjsjgList.size() > 0 && StringUtil.equals(szdSjsjgList.get(0).getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_ZQZG)) {
    		List<SzdSjsjg> szdSjsjgListNew = new ArrayList<SzdSjsjg>();
    		SzdSjsjg szdSjsjgZq = new SzdSjsjg();
    		SzdSjsjg szdSjsjgGp = new SzdSjsjg();
    		for (SzdSjsjg szdSjsjg : szdSjsjgList) {
    			if (StringUtil.equals(szdSjsjg.getJgzqlb(), StlDataDealProcessConstant.SJSMX_ZQLB_12) || StringUtil.equals(szdSjsjg.getJgzqlb(), StlDataDealProcessConstant.SJSMX_ZQLB_1E)) {
    				szdSjsjgZq = szdSjsjg;
    			}
    			else {
    				szdSjsjgGp = szdSjsjg;
    			}
    		}
    		szdSjsjgZq.setJgqsbj(szdSjsjgGp.getJgqsbj());
    		szdSjsjgZq.setJgsfje(szdSjsjgGp.getJgsfje());
    		szdSjsjgGp.setJgqsbj(new BigDecimal(0L));
    		szdSjsjgGp.setJgsfje(new BigDecimal(0L));
    		szdSjsjgListNew.add(szdSjsjgZq);
    		szdSjsjgListNew.add(szdSjsjgGp);
    		stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().setSjsjgList(szdSjsjgListNew);
    	}
    	
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsjg, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            // 单位净价
            settleTransData.setUnitCprice(szdSjsjg.getJgcjjg());
            // 单位全价
            settleTransData.setUnitFprice(szdSjsjg.getJgqsjg());
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsjg.getJgqssl()));
            // 全价总额
            settleTransData.setFpriceAmt(abs(szdSjsjg.getJgqsbj()));
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
            // 交易方向、业务场景明细分类、交易份额、交易日期等设置
            setBondDeal(szdSjsjg, settleTransData, settleTransDataAdd);

            return true;
        }));
    }

    /**
     * 回购类型SJSMX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 结算数据
     * @throws ParseException
     */
    private StlDownDataDealWriteDto secTypeIsRepoDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsjg, settleTransData, settleTransDataAdd) -> {
            // 交易币种固定为人民币
            settleTransData.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // 利率
            settleTransData.setIntRate(ArithmeticUtil.divide(abs(szdSjsjg.getJgcjjg()), new BigDecimal(100), 8));
            // 计息基础
            settleTransData.setBasis(DictCodeList.计息基础_A_365.getValue());

            settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());

            settleTransDataAdd.setTradeId(settleTransData.getTradeId());
            settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式协议回购.getValue());
            settleTransDataAdd.setSeRepDayType(CacheUtil.getSysParam(StlDataDealProcessConstant.STL_SE_REP_DAY_TYPE));

            // 回购交易
            setRepoDeal(szdSjsjg, settleTransData, settleTransDataAdd);
            return true;
        }));
    }

    /**
     * 基金
     * @param stlDownDataDealOperatorDto
     * @param map
     * @return
     * @throws ParseException
     */
    private StlDownDataDealWriteDto secTypeIsFundDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        // 全量基金信息获取
        Map<String, FundInfoDto> fundInfoDtoMap = settleCommonService.getAllFundInfoDto();
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsjg, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
            FundInfoDto fundInfoDto = fundInfoDtoMap.get(settleTransData.getFinprodId());
            if(null == fundInfoDto) {
                return false;
            }
            // 业务类型
            if (StringUtil.equals(DictCodeList.基金类型_货币基金.getValue(), fundInfoDto.getType1())) {
                settleTransData.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
            } else {
                settleTransData.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
            }
            // 单位净价
            settleTransData.setUnitCprice(szdSjsjg.getJgcjjg());
            // 交易本金
            settleTransData.setPrinAmt(szdSjsjg.getJgqsbj());
            // 利息总额  
            settleTransData.setIntAmt(szdSjsjg.getJgqsbj());
            // 全价总额
            settleTransData.setFpriceAmt(szdSjsjg.getJgqsbj());
            setFundDeal(szdSjsjg,settleTransData,settleTransDataAdd);
            return true;
        }));
    }

    private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {

        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsjg, settleTransData, settleTransDataAdd) -> {
        	// 金融产品代码
			settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());
            // 单位净价
            settleTransDataAdd.setTradeId(settleTransData.getTradeId());
            settleTransData.setUnitCprice(szdSjsjg.getJgcjjg());
            setSharesDeal(szdSjsjg,settleTransData,settleTransDataAdd);
            // 交易日期
            settleTransData.setTradeDate(DateUtil.parse(szdSjsjg.getJgcjrq()));
            // 交割日期
            if(DictCodeListExt2.交易方向_配股.getValue().equals(settleTransData.getPs())){
                settleTransData.setSettleDate(DateUtil.parse(szdSjsjg.getJgfsrq()));
            }else{
                settleTransData.setSettleDate(DateUtil.parse(szdSjsjg.getJgjsrq()));
            }
            return true;
        }));
    }

    private void setSharesDeal(SzdSjsjg szdSjsjg, SettleTransData settleTransData, SettleTransDataAdd settleTransDataAdd) {
        String ywlb = szdSjsjg.getJgywlb();
        // 要约收购
        if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_YYZJ)) {
            // TODO
        }
        // 配售
        else if((StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_PGDZ)
                || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_PG90))
                && StringUtil.equals("01", szdSjsjg.getJgsjlx())) {
            settleTransData.setPs(DictCodeListExt2.交易方向_配股.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_配股认购_配股.getValue());
            if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_PGDZ)) {
                settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            }
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
        }
        //股息派发
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_QPPX)) {
            settleTransData.setFpriceAmt(abs(szdSjsjg.getJgzjje()));
            settleTransData.setPs(DictCodeList.交易方向_现金分红.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_分红_现金分红.getValue());
            //单位净价
            settleTransData.setUnitCprice(BigDecimal.ZERO);
            //单位利息
            settleTransData.setUnitInt(ArithmeticUtil.divide(abs(szdSjsjg.getJgzjje()),szdSjsjg.getJgcjsl(),2));
            //交易份额
            settleTransData.setShareAmt(BigDecimal.ZERO);
        }
        //红股及转增股
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_QPHG)
                && StringUtil.equals(szdSjsjg.getJgltlx(), StlDataCreateConstant.JSMX_LTLX)) {
            //全价总额
            settleTransData.setFpriceAmt(BigDecimal.ZERO);
            settleTransData.setPs(DictCodeList.交易方向_红利再投.getValue());
            //单位净价
            settleTransData.setUnitCprice(BigDecimal.ZERO);
            //单位利息
            settleTransData.setUnitInt(BigDecimal.ZERO);
            //交易份额
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            //业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_分红_红利再投.getValue());
        }
    }

    /**
     * 基金交易时设置交易方向和业务场景明细
     * @param szdSjsjg
     * @param settleTransData
     * @param settleTransDataAdd
     */
    private void setFundDeal(SzdSjsjg szdSjsjg, SettleTransData settleTransData, SettleTransDataAdd settleTransDataAdd) {
        String ywlb = szdSjsjg.getJgywlb();
        String zqlb = szdSjsjg.getJgzqlb();
        // 货币ETF收益
        if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_EFSY)) {
            // 交易方向
            settleTransData.setPs(DictCodeList.交易方向_现金分红.getValue());
            // 交易份额
            settleTransData.setShareAmt(BigDecimal.ZERO);
            // 交易金额
            settleTransData.setTradeAmt(abs(szdSjsjg.getJgsfje()));
            // 业务场景分类明细
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_分红_现金分红.getValue());
        }
        // 基金分红
        else if((StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_QPPX)
                || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_QPZT))
                && LIST_ZQLB_FUNDS.contains(zqlb)){
            // 交易份额
            settleTransData.setShareAmt(szdSjsjg.getJgjssl());
            // 交易金额
            settleTransData.setTradeAmt(abs(szdSjsjg.getJgsfje()));
            if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_QPPX)) {
                // 交易方向
                settleTransData.setPs(DictCodeList.交易方向_现金分红.getValue());
                // 业务场景分类明细
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_15)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_LOF_分红_现金分红.getValue());
                }
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_16)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_开放式基金_分红_现金分红.getValue());
                }
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_18)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_分红_现金分红.getValue());
                }
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_EF)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_分红_现金分红.getValue());
                }
                // GNS-924: 修改深交所现金分红成交明细表赋值 modify by fuyongsheng 20220221 start
                settleTransData.setPrinAmt(szdSjsjg.getJgsfje());
                settleTransData.setFpriceAmt(szdSjsjg.getJgsfje());
                settleTransData.setIntAmt(szdSjsjg.getJgsfje());
                // GNS-924: 修改深交所现金分红成交明细表赋值 modify by fuyongsheng 20220221 end
            }
            else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_QPZT)) {
                // 交易方向
                settleTransData.setPs(DictCodeList.交易方向_红利再投.getValue());
                // 业务场景分类明细
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_15)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_LOF_分红_红利再投.getValue());
                }
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_16)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_开放式基金_分红_红利再投.getValue());
                }
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_18)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_分红_红利再投.getValue());
                }
                if(StringUtil.equals(zqlb, StlDataDealProcessConstant.SJSMX_ZQLB_EF)) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_分红_红利再投.getValue());
                }
                // 交割日期需要设置成清算日期
                // GNS-923:交割日期取清算数据entity.交收日期 modify by fuyongsheng 20220221 start
                // settleTransData.setSettleDate(settleTransData.getTradeDate());
                settleTransData.setSettleDate(DateUtil.parse(szdSjsjg.getJgjsrq()));
                // GNS-923:交割日期取清算数据entity.交收日期 modify by fuyongsheng 20220221 end
                //交易方向 = 红利再投时，零碎份额资金 = ABS(清算数据Entity.资金金额)
                settleTransData.setFragmentaryFunds(abs(szdSjsjg.getJgzjje()));
            }
        }
    }

    /**
     * 债券交易时设置交易方向和业务场景明细
     * @param szdSjsjg
     * @param settleTransData
     */
    private void setBondDeal(SzdSjsjg szdSjsjg, SettleTransData settleTransData, SettleTransDataAdd settleTransDataAdd) throws ParseException {
//         债券赎回  
//    	if (StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQSH, szdSjsjg.getJgywlb())) {
//    		settleTransData.setPs(DictCodeList.交易方向_赎回.getValue());
//    		settleTransData.setIntAmt(abs(szdSjsjg.getJgzjje()));
//    		settleTransData.setPrinAmt(abs(szdSjsjg.getJgzjje()));
//    		settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
//            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_赎回.getValue());
//    	}
    	// 债券回售
    	if ((StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQHS, szdSjsjg.getJgywlb()) || StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQHZ, szdSjsjg.getJgywlb())) &&
    			StringUtil.equals(StlDataDealProcessConstant.SJSMX_GFXZ_00, szdSjsjg.getJggfxz())) {
    		settleTransData.setPs(DictCodeList.交易方向_行权_回售.getValue());
    		settleTransData.setIntAmt(abs(szdSjsjg.getJgzjje()));
    		settleTransData.setPrinAmt(abs(szdSjsjg.getJgzjje()));
    		settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_回售_行权回售.getValue());
            // 针对债券-回售特殊处理为竞价平台
            settleTransData.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
            // 交易日期
            settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgqsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
    	}
    	// GZYHTZJY-1075
    	if (StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQHG, szdSjsjg.getJgywlb())) {
            settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgcjrq(),DateUtil.DEFAULT_OTHER_FORMAT));
            settleTransData.setSettleDate(DateUtil.parseDate(szdSjsjg.getJgcjrq(),DateUtil.DEFAULT_OTHER_FORMAT));
            // 金融产品代码
            String finprodId = DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ";
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
            if(finProduct == null){
                finprodId = DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ";
                finProduct = finProductMapper.selectByPrimaryKey(finprodId);
            }
    	    if(finProduct != null && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finProduct.getFinprodType2())){ // 可交换私募债换股-债券变动
                settleTransData.setPs(DictCodeListExt3.交易方向_债转股.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_转股_债转股.getValue());
                //交易份额
                settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            }else if(finProduct != null && DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finProduct.getFinprodType2())){ // 可交换私募债换股-股票变动
                settleTransData.setPs(DictCodeListExt3.交易方向_债转股.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_转股_债转股.getValue());
                settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
                //交易份额
                settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
                //交易份额
                // settleTransData.setFpriceAmt(abs(szdSjsjg.getJgzjje()));
                List<SzdSjsjg> szdSjsjgs = stlDownDataDealMapper.getSzdSjsjgByApplyTradeId(settleTransData.getApplyTradeId());
                if(CollectionUtils.isNotEmpty(szdSjsjgs)){
                    for(SzdSjsjg data : szdSjsjgs){
                        if(!data.getJgzqdm().equals(szdSjsjg.getJgzqdm())){
                            // (债券Jgcjsl*100-股票Jgzjje)/股票Jgjssl
                            settleTransData.setUnitCprice(ArithmeticUtil.divide(ArithmeticUtil.subtract(
                                    ArithmeticUtil.multiply(data.getJgcjsl(),new BigDecimal(100L)),szdSjsjg.getJgzjje()),
                                    szdSjsjg.getJgjssl(),2));
                            settleTransData.setUnitFprice(settleTransData.getUnitCprice());
                            //交易金额
                            settleTransData.setFpriceAmt(szdSjsjg.getJgzjje());
                        }
                    }
                }
            }
    	}
    	// 债转股业务
    	if (StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQZG, szdSjsjg.getJgywlb())) {
    		settleTransData.setPs(DictCodeListExt1.交易方向_债转股.getValue());
    		settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_转股_债转股.getValue());
    		settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
    		settleTransData.setPrinAmt(abs(szdSjsjg.getJgqsbj()));
            settleTransData.setFpriceAmt(abs(szdSjsjg.getJgqsbj()));
            if(BigDecimal.ZERO.compareTo(abs(szdSjsjg.getJgjssl())) < 0) {// 交收数量大于0
                settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            }
//    		if (!StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_12, szdSjsjg.getJgzqlb()) && !StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_1E, szdSjsjg.getJgzqlb())) {
//            settleTransDataAdd表暂时不登  因为后续没有使用到
//            settleTransDataAdd.setTradeId(szdSjsjg.getJgywlsh());
//            settleTransDataAdd.setConPrice(szdSjsjg.getJgqsjg());
//            settleTransDataAdd.setConAmt(szdSjsjg.getJgjssl());
//            settleTransDataAdd.setConPrinAmt(abs(szdSjsjg.getJgqsbj()));
//            settleTransDataAdd.setrFinprodId(szdSjsjg.getJgzqdm());
//    		}
    	}
        // 配股配债认购
//        if (StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_PGRG, szdSjsjg.getJgywlb()) && LIST_ZQLB_BONDS.contains(szdSjsjg.getJgzqlb())) {
//            settleTransData.setPs(DictCodeListExt3.交易方向_配债.getValue());
//            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_优先配售_申购.getValue());
//            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm2() + ".SZ");
//        }
        // 配股配债到账
        if (StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_PGDZ, szdSjsjg.getJgywlb()) && LIST_ZQLB_BONDS.contains(szdSjsjg.getJgzqlb())) {
            settleTransData.setPs(DictCodeListExt3.交易方向_配债.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_优先配售_买入.getValue());
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            // GNS-868:配债赋值tradeDate和settleDate modify by fuyongsheng 20220217 start
            settleTransData.setTradeDate(DateUtil.parse(szdSjsjg.getJgcjrq()));
            settleTransData.setSettleDate(DateUtil.parse(szdSjsjg.getJgfsrq()));
            settleTransData.setUnitFprice(stlDownDataDealMapper.getPriceByFinproductId(
                    DictCodeListExt3.金融产品其他代码类型_配债代码.getValue(), settleTransData.getFinprodId()));
            // GNS-868:配债赋值tradeDate和settleDate modify by fuyongsheng 20220217 end
            settleTransData.setFpriceAmt(ArithmeticUtil.multiply(settleTransData.getShareAmt(), settleTransData.getUnitFprice()));
            settleTransData.setTradeAmt(settleTransData.getFpriceAmt());
        }
    }

    /**
     * 回购交易设置
     * @param szdSjsjg
     * @param settleTransData
     */
    private void setRepoDeal(SzdSjsjg szdSjsjg, SettleTransData settleTransData,SettleTransDataAdd settleTransDataAdd) throws ParseException {
//        // 清算本金
        BigDecimal qsbj = szdSjsjg.getJgqsbj();
        // 业务类别
        String ywlb = szdSjsjg.getJgywlb();
        // 质押式回购协议：初始交易、购回交易、提前购回、--协议回购合约
        if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYCS)
                || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYDQ)
                || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYTG)) {
            // 金融产品代码 修改为 数据清算Entity.证券代码
//            settleTransData.setFinprodId(szdSjsjg.getJgfjsm());
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + "_" + szdSjsjg.getJgzqdm() + ".SZ");
            
            if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYCS)) {
                // 交易方向
                settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                // 交易份额 修改为取 清算本金
//                settleTransData.setShareAmt(abs(szdSjsjg.getJgcjsl()));
                settleTransData.setShareAmt(abs(qsbj));
                // 业务场景分类
                if(BigDecimal.ZERO.compareTo(szdSjsjg.getJgqsbj()) < 0){
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_首期_买入.getValue());
                }else if(BigDecimal.ZERO.compareTo(szdSjsjg.getJgqsbj()) > 0){
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue());    
                }
                // 交易本金
                settleTransData.setPrinAmt(abs(qsbj));
                // 起息日
                settleTransData.setVdate(DateUtil.parseDate(szdSjsjg.getJgcjrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期日
                settleTransData.setMdate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 交易日
                settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期本金
                settleTransData.setmPrinAmt(settleTransData.getPrinAmt());
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parseDate(szdSjsjg.getJgjsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID);
                // 到期交收日（其它日期判断是否工作日，非工作日顺延至下一工作日）
                String isHoliday = stlDataDealProcessCommonService.getIsHoliday(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT), calendarId);
                if("0".equals(isHoliday)){
                    settleTransData.setMpayDate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                }else{
                    settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
                }
                // settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
                // 回购利息
//                BigDecimal repayInt = ArithmeticUtil.multiply(
//                        ArithmeticUtil.divide(ArithmeticUtil.multiply(abs(qsbj), abs(szdSjsjg.getJgcjjg())), 100, 2),
//                        ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getMpayDate(), settleTransData.getVpayDate())), 365,2));
//                //新增 到期利息  add by msk
                settleTransData.setmIntAmt(BigDecimal.ZERO);
                // 到期金额     去除到期金额  modify by msk
                settleTransData.setmTradeAmt(BigDecimal.ZERO);
                // 单位净价  add by msk
                settleTransData.setUnitCprice(ArithmeticUtil.divide(abs(szdSjsjg.getJgcjjg()),100,2));
                // 全价总额  add by msk
                settleTransData.setFpriceAmt(abs(szdSjsjg.getJgqsbj()));
                // 期限品种
                settleTransData.setTermType(getTermType(settleTransData.getVdate(),settleTransData.getMdate()));
            } else if (StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYDQ)
                    || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYTG)) {
                // 交易方向
                settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
                if (StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYTG)) {
                    settleTransData.setPs(DictCodeList.交易方向_提前终止.getValue());
                }
                // 交易份额
                // settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
                // 业务场景分类
                if(BigDecimal.ZERO.compareTo(szdSjsjg.getJgqsbj()) < 0){
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期.getValue());
                    if (StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYTG)) {
                        settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_提取终止.getValue());
                    }
                }else if(BigDecimal.ZERO.compareTo(szdSjsjg.getJgqsbj()) > 0){
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_到期.getValue());
                    if (StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYTG)) {
                        settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_提取终止.getValue());
                    }
                }
                // 交易本金
                settleTransData.setPrinAmt(abs(qsbj));
                // 到期日
                settleTransData.setMdate(DateUtil.parseDate(szdSjsjg.getJgqsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 交易日
                settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期交收日
                settleTransData.setMpayDate(DateUtil.parseDate(szdSjsjg.getJgjsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期金额 = abs(abs(清算数据.收付净额) - abs(清算数据.经手费) - abs(清算数据.结算费))
                // settleTransData.setmTradeAmt(abs(qsbj));
                settleTransData.setmTradeAmt(abs(ArithmeticUtil.subtract(abs(szdSjsjg.getJgsfje()),
                        ArithmeticUtil.doAdd(szdSjsjg.getJgjyjsf(),szdSjsjg.getJgjsf()))));
                // 单位净价  add by msk
                settleTransData.setUnitCprice(ArithmeticUtil.divide(abs(szdSjsjg.getJgcjjg()),100,2));
                // 到期本金 = abs(清算本金) modify by fuyongsheng 20220105 start
                settleTransData.setmPrinAmt(abs(qsbj));
                // settleTransData.setmPrinAmt(settleTransData.getmTradeAmt().subtract(settleTransData.getmIntAmt()));
                // 到期利息=到期金额-到期本金
                settleTransData.setmIntAmt(settleTransData.getmTradeAmt().subtract(settleTransData.getmPrinAmt()));
                // 利息总额
                settleTransData.setIntAmt(settleTransData.getmIntAmt());
                // 交易份额
                settleTransData.setShareAmt(settleTransData.getmPrinAmt());
                // 到期本金 = 到期金额-回购利息 modify by fuyongsheng 20220105 end
                // 首期成交编号
                settleTransData.setfCjbh(szdSjsjg.getJgfjsm());
                // 起息日
                settleTransData.setVdate(DateUtil.parse(szdSjsjg.getJgqtrq()));
                // 查询首期
                List<SettleTransData> sqSettleTransDatas = trdCommonBaseService.selectSqSettleTransDatas(
                        settleTransData.getfCjbh(), settleTransData.getSeatNumber(), settleTransData.getSecAcctId(),
                        settleTransData.getVdate());
                if(CollectionUtils.isNotEmpty(sqSettleTransDatas)){
                    // settleTransData.setVdate(sqSettleTransDatas.get(0).getVdate());
                    // 首期交收日 = 起息日+1个工作日
                    settleTransData.setVpayDate(nextWorkDay(settleTransData.getVdate()));
                    /*if(sqSettleTransDatas.get(0).getMpayDate().compareTo(settleTransData.getSettleDate())>0){
                        settleTransData.setPs(DictCodeListExt3.交易方向_投资端_提前终止.getValue());
                    }*/
                }
                //全价总额(到期金额)
                // settleTransData.setFpriceAmt(settleTransData.getmTradeAmt().add(settleTransData.getmIntAmt()));
                settleTransData.setFpriceAmt(settleTransData.getmTradeAmt());
            }
            //去除 协议回购合约的数据处理
//            else {
//                // 交易方向
//                settleTransData.setPs(DictCodeListStl.交易方向_质押券入库.getValue());
//                // 交易份额
//                settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
//                // 交易本金
//                settleTransData.setPrinAmt(abs(qsbj));
//                // 起息日
//                settleTransData.setVdate(DateUtil.parseDate(szdSjsjg.getJgcjrq(), DateUtil.DEFAULT_OTHER_FORMAT));
//                // 到期日
//                settleTransData.setMdate(DateUtil.parseDate(szdSjsjg.getJgcjrq(), DateUtil.DEFAULT_OTHER_FORMAT));
//                // 交易日
//                settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
//                // 首期交收日
//                settleTransData.setVpayDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
//                // 到期交收日
//                settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
//            }
            // 根据清算本金设置金融产品类型
            setFinprodType(ywlb, qsbj,settleTransData);
        }
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYXZ)
                || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYXB)) {
            // 根据清算本金设置金融产品类型
            setFinprodType(ywlb,qsbj,settleTransData);
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + "_" + szdSjsjg.getJgzqdm() + ".SZ");
//            // 业务场景分类
//            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue());
            // 设置业务场景分类 modify by msk  
            setBusiSceneCalssXY(qsbj,settleTransData);
            // 交易日
            settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交易方向
            settleTransData.setPs(DictCodeList.交易方向_到期续做.getValue());
            // 起息日
            settleTransData.setVdate(DateUtil.parseDate(szdSjsjg.getJgcjrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 到期日
            settleTransData.setMdate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 首期交收日  修改为取值交收日期 modify by msk
//            settleTransData.setVpayDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            settleTransData.setVpayDate(DateUtil.parseDate(szdSjsjg.getJgjsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID);
            // 到期交收日（其它日期判断是否工作日，非工作日顺延至下一工作日）
            String isHoliday = stlDataDealProcessCommonService.getIsHoliday(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT), calendarId);
            if("0".equals(isHoliday)){
                settleTransData.setMpayDate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            }else{
                settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
            }
            // 到期交收日
            // settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
            // 交易本金 ABS(清算数据Entity.清算本金)
            settleTransData.setPrinAmt(abs(qsbj));
            // settleTransData.setPrinAmt(abs(szdSjsjg.getJgqssl()));
            // 单位净价
            settleTransData.setUnitCprice(ArithmeticUtil.divide(abs(szdSjsjg.getJgcjjg()),100,2));
            // 首期成交编号
            settleTransData.setfCjbh(szdSjsjg.getJgfjsm());
            // 到期金额(ABS(资金金额))
            // settleTransData.setmTradeAmt(settleTransData.getPrinAmt());
            settleTransData.setmTradeAmt(abs(szdSjsjg.getJgzjje()));
            // 查询首期清算数据
            List<SettleTransData> sqSettleTransDatas = trdCommonBaseService.selectSqSettleTransDatas(
                    settleTransData.getfCjbh(), settleTransData.getSeatNumber(), settleTransData.getSecAcctId(),
                    settleTransData.getVdate());
            if(CollectionUtils.isNotEmpty(sqSettleTransDatas)){
                // 到期本金
                settleTransData.setmPrinAmt(sqSettleTransDatas.get(0).getPrinAmt());
            }
//            settleTransData.setmPrinAmt(ArithmeticUtil.subtract(settleTransData.getmTradeAmt(), settleTransData.getmIntAmt()));
            //到期利息(到期金额-到期本金)
//            settleTransData.setmIntAmt(abs(ArithmeticUtil.subtract(abs(szdSjsjg.getJgsfje()),
//                    ArithmeticUtil.doAdd(settleTransData.getPrinAmt(),szdSjsjg.getJgjyjsf(),szdSjsjg.getJgjsf()))));
            settleTransData.setmIntAmt(ArithmeticUtil.subtract(settleTransData.getmTradeAmt(),settleTransData.getmPrinAmt()));
            //全价总额
            // settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_到期续作.getValue());
            if(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue().equals(settleTransData.getBusiSceneCalss())){
                settleTransData.setFpriceAmt(abs(ArithmeticUtil.subtract(settleTransData.getPrinAmt(), settleTransData.getmTradeAmt())));
            }else{
                settleTransData.setFpriceAmt(ArithmeticUtil.subtract(settleTransData.getPrinAmt(), settleTransData.getmTradeAmt()));
            }
            // GZYHTZJY-1039 交易份额 = abs(清算本金) fuyongsheng modify 20220106 start
            // 交易份额 = abs(清算本金)
            settleTransData.setShareAmt(abs(szdSjsjg.getJgqsbj()));
            // GZYHTZJY-1039 交易份额 = abs(清算本金) fuyongsheng modify 20220106 end
            // 期限品种
            settleTransData.setTermType(getTermType(settleTransData.getVdate(),settleTransData.getMdate()));
        }
        /**
         * XYBG 变更质押券   XYZJ 解除质押
         */
        else if (StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYBG)
                    || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYZJ)) {
            // 首期成交编号
            settleTransData.setfCjbh(szdSjsjg.getJgfjsm());
            // 查询首期
            SettleTransData sqSettleTransData = trdCommonBaseService.selectSqSettleTransDatas(settleTransData.getfCjbh(),
                    settleTransData.getSeatNumber(), settleTransData.getSecAcctId(), settleTransData.getVdate()).get(0);

            // 金融产品代码
            settleTransData.setFinprodId(sqSettleTransData.getFinprodType2());
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            // 期限品种
            settleTransData.setTermType(sqSettleTransData.getTermType());
            // 起息日
            settleTransData.setVdate(sqSettleTransData.getVdate());
            // 交易方向
            settleTransData.setPs(DictCodeListExt3.交易方向_中途换券.getValue());
            setDifferentConf(settleTransData, szdSjsjg, sqSettleTransData);
        }
        // 报价回购
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_BJCS) || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_BJDQ)) {
            // 根据清算本金设置金融产品类型
            setFinprodType(ywlb,qsbj,settleTransData);
            if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_BJCS)) {
                // 交易方向
                settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                // 起息日
                settleTransData.setVdate(DateUtil.parseDate(szdSjsjg.getJgcjrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期日
                settleTransData.setMdate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parseDate(szdSjsjg.getJgjsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期交收日
                settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
                if(BigDecimal.ZERO.compareTo(qsbj) < 0) {// 清算本金大于0
                    // 业务场景分类
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价正回购_首期_买入.getValue());
                }
                if(BigDecimal.ZERO.compareTo(qsbj) > 0) {// 清算本金小于0
                    // 业务场景分类
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价逆回购_首期_买入.getValue());
                }
                // 本金金额
                settleTransData.setPrinAmt(abs(qsbj));
                // 回购利息
                BigDecimal repayInt = ArithmeticUtil.multiply(
                        ArithmeticUtil.divide(ArithmeticUtil.multiply(abs(qsbj), abs(szdSjsjg.getJgcjjg())), 100, 2),
                        ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getMpayDate(), settleTransData.getVpayDate())), 365,2));
                // 到期本金
                settleTransData.setmPrinAmt(settleTransData.getPrinAmt());
                // 到期金额
                settleTransData.setmTradeAmt(ArithmeticUtil.doAdd(settleTransData.getmPrinAmt(), repayInt));
            }
            else {
                // 交易方向
                settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
                // 起息日
                settleTransData.setVdate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期日
                settleTransData.setMdate(DateUtil.parseDate(szdSjsjg.getJgcjrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parseDate(szdSjsjg.getJgqtrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                // 到期交收日
                settleTransData.setMpayDate(nextWorkDay(DateUtil.parseDate(szdSjsjg.getJgjsrq(), DateUtil.DEFAULT_OTHER_FORMAT)));
                if(BigDecimal.ZERO.compareTo(qsbj) < 0) {// 清算本金大于0
                    // 业务场景分类
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价正回购_到期.getValue());
                }
                if(BigDecimal.ZERO.compareTo(qsbj) > 0) {// 清算本金小于0
                    // 业务场景分类
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_报价逆回购_到期.getValue());
                }
                // 到期金额
                settleTransData.setmTradeAmt(abs(qsbj));
            }
        }
        // 报价回购质押券入库
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_BJRK)) {
            // 成交编号
            // settleTransData.setTradeId(szdSjsjg.getJgddbh());
            settleTransData.setApplyTradeId(szdSjsjg.getJgddbh());
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            // 数据明细质押品信息DTO 在 secTypeCommonDeal 方法中设置
        }
        // 报价回购质押券出库
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_BJCK)) {
            // 成交编号
            // settleTransData.setTradeId(szdSjsjg.getJgddbh());
            settleTransData.setApplyTradeId(szdSjsjg.getJgddbh());
            if(BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) == 0) { //清算数据Entity.交收数量 = 0
                // 交易方向： 数据字典：交易方向.派息派本
                settleTransData.setPs(DictCodeListStl.交易方向_派本派息.getValue());
                // 金融产品代码
                settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm2() + ".SZ");
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
                // 利息总额
                settleTransData.setIntAmt(abs(szdSjsjg.getJgzjje()));
                // 交易本金
                settleTransData.setPrinAmt(abs(qsbj));
                // 业务类型
                settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
            }
            else {
                // 交易份额
                settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
                // 数据明细质押品信息DTO 在 secTypeCommonDeal 方法中设置
            }
        }
        // 质押式回购质押券入库  add by msk
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_ZYRK) ||
                StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_ZY01)) {
            //证券品种
            settleTransData.setSecType(DictCodeListExt1.证券品种_债券.getValue());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
            // 成交编号
            // settleTransData.setTradeId(szdSjsjg.getJgddbh());
            settleTransData.setApplyTradeId(szdSjsjg.getJgddbh());
            //交易方向
            settleTransData.setPs(DictCodeListExt3.交易方向_质押券入库.getValue());
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            //交割日期
            settleTransData.setSettleDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            //业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_标准券入库.getValue());
            // 回购类型
            settleTransDataAdd.setRepoType(null);
        }
        // 质押式回购质押券出库 add by msk
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_ZYCK)) {
            //证券品种
            settleTransData.setSecType(DictCodeListExt1.证券品种_债券.getValue());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
            // 成交编号
            // settleTransData.setTradeId(szdSjsjg.getJgddbh());
            settleTransData.setApplyTradeId(szdSjsjg.getJgddbh());
            //交易方向
            settleTransData.setPs(DictCodeListExt3.交易方向_质押券出库.getValue());
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsjg.getJgjssl()));
            //交割日期
            settleTransData.setSettleDate(DateUtil.parseDate(szdSjsjg.getJgfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            //业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_标准券出库.getValue());
            // 回购类型
            settleTransDataAdd.setRepoType(null);
        }
    }

    private void setDifferentConf(SettleTransData settleTransData, SzdSjsjg szdSjsjg, SettleTransData sqSettleTransData) {
        if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_XYBG)) {
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(sqSettleTransData.getFinprodType2())) {
                if (BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) > 0) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换入.getValue());
                    settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换入.getValue());
                }
                if (BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) < 0) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换出.getValue());
                    settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                }
                if (BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) == 0) {
                    settleTransData.setBusiSceneCalss("数据字典：业务场景分类明细.债券协议正回购-换券-换钱");
                    settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                }
            }
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(sqSettleTransData.getFinprodType2())) {
                if (BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) > 0) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_换券_换出.getValue());
                    settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                }
                if (BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) < 0) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_换券_换入.getValue());
                    settleTransData.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());
                }
            }
        }
        if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_XYZJ)) {
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(sqSettleTransData.getFinprodType2())) {
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_逾期返售解除质押.getValue());
                settleTransData.setTradeType("数据字典：置换方向.换出");
            }
            if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(sqSettleTransData.getFinprodType2())) {
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_逾期返售解除质押.getValue());
                settleTransData.setTradeType("数据字典：置换方向.换出");
            }
        }
    }

    private String setDeliveryResult(SzdSjsjg szdSjsjg) {
        String deliveryResult = DictCodeListExt4.交收结果_交收失败.getValue();
        if ("N".equals(szdSjsjg.getJgjsbz())) {
            return deliveryResult;
        }
        if ("Y".equals(szdSjsjg.getJgjsbz())) {
            deliveryResult = DictCodeListExt4.交收结果_正常交收.getValue();
            if (0 != szdSjsjg.getJgjssl().compareTo(szdSjsjg.getJgcjsl())) {
                deliveryResult = DictCodeListExt4.交收结果_部分交收.getValue();
            }
        }
        return deliveryResult;
    }

    private BigDecimal abs(BigDecimal value) {
        return null != value ? value.abs() : null;
    }

    private void setFinprodType(String ywlb, BigDecimal qsbj, SettleTransData settleTransData) {
        if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYDQ)
                || StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSJG_YWLB_XYTG)){
            if(BigDecimal.ZERO.compareTo(qsbj) < 0) {// 清算本金大于0
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
            }
            if(BigDecimal.ZERO.compareTo(qsbj) > 0) {// 清算本金小于0
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
            }
        } else {
            if(BigDecimal.ZERO.compareTo(qsbj) < 0) {// 清算本金大于0
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
            }
            if(BigDecimal.ZERO.compareTo(qsbj) > 0) {// 清算本金小于0
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
            }
        }
    }

    /**
     * SJSMX公共数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeCommonDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto,
                                                      Map<String, List<StlDownDataDealAccountInfoDto>> map,
                                                      PersonalityFunction<SzdSjsjg, SettleTransData, SettleTransDataAdd> funcion) throws ParseException {
        // 资产标的信息
//        List<String> assetList = new ArrayList<>();
//        if(StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), stlDownDataDealOperatorDto.getFinprodType())){
//            assetList = settleCommonService.getAllAssetInfo(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
//        }
//        if(StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), stlDownDataDealOperatorDto.getFinprodType())){
//            assetList = settleCommonService.getAllAssetInfo(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
//        }
        // 资产标的信息
        List<String> bondAssetList = settleCommonService.getAllAssetInfo(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        List<String> sharesAssetList = settleCommonService.getAllAssetInfo(DictCodeListExt1.金融产品类型_投管用_股票.getValue());

        // 处理结果数据定义
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        // 遍历 输入.SJSJG清算数据List 清算数据Entity
        for(SzdSjsjg szdSjsjg : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsjgList()) {
            // KEY = 证券账户号码 + 交易单元
            // GNS-865,GNS-866:交易单元为空时使用托管单元 modify by fuyongsheng 20220214 start
            String jgjydy = StringUtil.isNotEmpty(szdSjsjg.getJgjydy()) ? szdSjsjg.getJgjydy():szdSjsjg.getJgtgdy();
            String key = szdSjsjg.getJgzqzh() + jgjydy;
            // GNS-865,GNS-866:交易单元为空时使用托管单元 modify by fuyongsheng 20220214 end
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            //去除组合代码的判断  modify by msk
//            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
//                continue;
//            }

            if(StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), stlDownDataDealOperatorDto.getFinprodType()) &&
                    StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_TGSS) ){
                String finprodId = DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ";
                if(!sharesAssetList.contains(finprodId)) {
                    continue;
                }
            }

            if(StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), stlDownDataDealOperatorDto.getFinprodType())){
                String finprodId = DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ";
                String shareFinprodId = DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ";
                // GZYHTZJY-1074 : 债转股业务,债券类型中包含股票数据 modify by fuyongsheng 20220108 start
                if(StringUtil.equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQHG, szdSjsjg.getJgywlb())){
                    if(!bondAssetList.contains(finprodId) && !sharesAssetList.contains(shareFinprodId)) {
                        continue;
                    }
                }else{
                    if(!bondAssetList.contains(finprodId)) {
                        continue;
                    }
                }
                // GZYHTZJY-1074 : 债转股业务,债券类型中包含股票数据 modify by fuyongsheng 20220108 end
            }

            StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
            // 债券 QPPX、QPDF、ZQSH场景单独处理
            if(StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), stlDownDataDealOperatorDto.getFinprodType()) &&
                (szdSjsjg.getJgywlb().equals(StlDataDealProcessConstant.SJSJG_YWLB_QPPX)
                            || szdSjsjg.getJgywlb().equals(StlDataDealProcessConstant.SJSJG_YWLB_QPDF)
                            || szdSjsjg.getJgywlb().equals(StlDataDealProcessConstant.SJSJG_YWLB_ZQSH)) &&
                StringUtil.equals(DictCodeListStl.清算数据源表_深中登SJSJG.getValue(), stlDownDataDealOperatorDto.getDataFile()) &&
                StringUtil.isEmpty(szdSjsjg.getJgzqlb())){
                // QPPX、QPDF、ZQSH场景
                stlDownDataDealWriteBaseDto = dealBondSpecialType(szdSjsjg, stlDownDataDealOperatorDto,  accountInfoDtolist);
            }else if((StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_ZYRK) ||
                    StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_ZY01) ||
                    StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_ZYCK))
                    && BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) == 0) {
//                if (BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) == 0){
                    continue;
//                }
            }else{
                // 定义变量.成交数据明细DTO
                SettleTransData settleTransData = new SettleTransData();
                // 定义变量.成交数据明细附属表DTO
                SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();

                // 交易平台
                settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdSjsjg.getJgjyfs()));
                // 交收方式
                settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdSjsjg.getJgjsfs()));
                // 交收结果
                settleTransData.setDeliveryResult(setDeliveryResult(szdSjsjg));
                // 交易币种
                settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdSjsjg.getJghbdh()));

                String generate = IDGenerator.generate();
                settleTransData.setTradeId(generate);
                // 成交编号 修改为 执行编号  modify by msk
//                settleTransData.setCjbh(szdSjsjg.getJgywlsh());
                settleTransData.setCjbh(szdSjsjg.getJgzxbh());
                // 执行日期
                settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
                // 证券品种
                settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
                // 申请编号
                settleTransData.setApplyTradeId(szdSjsjg.getJgddbh());
                // 分支序号
                settleTransData.setBranch(0L);
                // 交易日期
                if(StringUtil.isNotEmpty(szdSjsjg.getJgqsrq())){
                    settleTransData.setTradeDate(DateUtil.parseDate(szdSjsjg.getJgqsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                }
                // 交割日期
                if(StringUtil.isNotEmpty(szdSjsjg.getJgjsrq())){
                    settleTransData.setSettleDate(DateUtil.parseDate(szdSjsjg.getJgjsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
                }
                // 清算速度
                settleTransData.setSettleSpeed(0L);
                if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
                    settleTransData.setSettleSpeed(1L);
                }
                // 印花税
                settleTransData.setStampFee(abs(szdSjsjg.getJgyhs()));
                // 经手费
                settleTransData.setJsfAmt(abs(szdSjsjg.getJgjyjsf()));
                // 过户费
                settleTransData.setGhfAmt(abs(szdSjsjg.getJgghf()));
                // 证管费
                settleTransData.setZgfAmt(abs(szdSjsjg.getJgjggf()));
                // 手续费
                settleTransData.setSxfAmt(abs(szdSjsjg.getJgsxf()));
                // 结算费
                settleTransData.setSettleFee(abs(szdSjsjg.getJgjsf()));
                // 交易金额
                settleTransData.setTradeAmt(abs(szdSjsjg.getJgsfje()));
                // 交易金额_原始
                settleTransData.setTradeAmtRaw(abs(szdSjsjg.getJgsfje()));
                // 印花税_原始
                settleTransData.setStampFeeRaw(abs(szdSjsjg.getJgyhs()));
                // 经手费_原始
                settleTransData.setJsfAmtRaw(abs(szdSjsjg.getJgjsf()));
                // 过户费_原始
                settleTransData.setGhfAmtRaw(abs(szdSjsjg.getJgghf()));
                // 证管费_原始
                settleTransData.setZgfAmtRaw(abs(szdSjsjg.getJgjggf()));
                // 手续费_原始
                settleTransData.setSxfAmtRaw(abs(szdSjsjg.getJgsxf()));
                // 结算费_原始
                settleTransData.setSettleFeeRaw(abs(szdSjsjg.getJgjsf()));
                // 交易场所
                settleTransData.setTradeMarket(DictCodeList.交易流通场所_深圳证券交易所.getValue());
                // 交易市场
                settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
                // 席位号
                // settleTransData.setSeatNumber(szdSjsjg.getJgjydy());
                settleTransData.setSeatNumber(StringUtil.isNotEmpty(szdSjsjg.getJgjydy()) ? szdSjsjg.getJgjydy():szdSjsjg.getJgtgdy());
                // 组合代码
                settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
                // 证券账户代码
                settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
                // 资金账户
                settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
                // 临时表流水号
                settleTransData.setSeqNo(szdSjsjg.getSeqno());

                if(null != funcion) {
                    if(!funcion.apply(szdSjsjg, settleTransData, settleTransDataAdd)) {
                        continue;
                    }
                }
                // 日终过来的文件结算价格就是全价的，需要反算净价总额、应计利息总额
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), settleTransData.getFinprodType2())) {
                    stlDownDataDealService.calc(settleTransData);
                }
                stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
                if (settleTransDataAdd != null && StringUtil.isNotEmpty(settleTransData.getTradeId())) {
                    settleTransDataAdd.setTradeId(settleTransData.getTradeId());
                    stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
                }
                // 成交数据与投管交易关系
                SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
                settleTransTgMapping.setTradeId(settleTransData.getTradeId());// 取成交编号
                stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);

                // 成交数据明细质押品信息
                if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_BJRK) // 回购-质押券入库
                        || (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_BJCK) && BigDecimal.ZERO.compareTo(szdSjsjg.getJgjssl()) != 0) // 回购-质押券出库
                ) {
                    // 数据明细质押品信息DTO
                    SettleTransDataPledge settleTransDataPledge = new SettleTransDataPledge();
                    settleTransDataPledge.setTradeId(settleTransData.getTradeId());
                    settleTransDataPledge.setBranch(0L);
                    settleTransDataPledge.setPledgeFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + szdSjsjg.getJgzqdm2() + ".SZ");
                    settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(abs(szdSjsjg.getJgqsbj()), 100, 2));
                    stlDownDataDealWriteBaseDto.getSettleTransDataPledgeList().add(settleTransDataPledge);
                }else if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_XYCS)){
                    //业务类别 = 初始交易 时新增数据明细质押品信息DTO  add by msk
                    // 数据明细质押品信息DTO
                    SettleTransDataPledge settleTransDataPledge = new SettleTransDataPledge();
                    settleTransDataPledge.setTradeId(settleTransData.getTradeId());
                    settleTransDataPledge.setBranch(0L);
                    settleTransDataPledge.setPledgeFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + "_" + szdSjsjg.getJgzqdm() + ".SZ");
                    if(DictCodeListStl.业务场景分类明细_债券协议逆回购_首期_买入.getValue().equals(settleTransData.getBusiSceneCalss())){
                        settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(abs(szdSjsjg.getJgcjsl()), 100, 2));
                        settleTransDataPledge.setPledgeRatio(ArithmeticUtil.divide(settleTransData.getPrinAmt(), abs(szdSjsjg.getJgcjsl()), 2));
                        settleTransDataPledge.setPledgeAmt(abs(szdSjsjg.getJgcjsl()));
                    }else{
                        settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(abs(szdSjsjg.getJgjssl()), 100, 2));
                        settleTransDataPledge.setPledgeRatio(ArithmeticUtil.divide(settleTransData.getPrinAmt(), abs(szdSjsjg.getJgjssl()), 2));
                        settleTransDataPledge.setPledgeAmt(abs(szdSjsjg.getJgjssl()));
                    }
                    stlDownDataDealWriteBaseDto.getSettleTransDataPledgeList().add(settleTransDataPledge);
                }else if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_XYXZ)){
                    //业务类别 = 到期续做 时新增数据明细质押品信息DTO  add by msk
                    // 数据明细质押品信息DTO
                    SettleTransDataPledge settleTransDataPledge = new SettleTransDataPledge();
                    settleTransDataPledge.setTradeId(settleTransData.getTradeId());
                    settleTransDataPledge.setBranch(0L);
                    settleTransDataPledge.setPledgeFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue()+ "_" + szdSjsjg.getJgzqdm() + ".SZ");
                    settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(settleTransData.getPrinAmt(), 100, 2));
                    settleTransDataPledge.setPledgeRatio(BigDecimal.ONE);
                    settleTransDataPledge.setPledgeAmt(settleTransData.getPrinAmt());
                    stlDownDataDealWriteBaseDto.getSettleTransDataPledgeList().add(settleTransDataPledge);
                } else if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_XYBG)
                        || StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_XYZJ)) {
                    if(!DictCodeListStl.业务场景分类明细_债券协议正回购_换券_换钱.getValue().equals(settleTransData.getBusiSceneCalss())){
                        // 数据明细质押品信息DTO
                        SettleTransDataPledge settleTransDataPledge = new SettleTransDataPledge();
                        // 流水号
                        settleTransDataPledge.setTradeId(settleTransData.getTradeId());
                        // 分支序号
                        settleTransDataPledge.setBranch(0L);
                        // 质押金融产品代码  金融产品类型(投管).债券 + 清算数据Entity.证券代码 + ".SZ"
                        settleTransDataPledge.setPledgeFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
                        // 质押数量
                        settleTransDataPledge.setPledgeNumber(ArithmeticUtil.divide(abs(szdSjsjg.getJgqsbj()), 100, 2));
                        // 质押率
                        settleTransDataPledge.setPledgeRatio(null);
                        // 置换方向
                        settleTransDataPledge.setDisplaceDirection(settleTransData.getPs());
                        stlDownDataDealWriteBaseDto.getSettleTransDataPledgeList().add(settleTransDataPledge);
                    }
                }
            }
            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
        }
        return stlDownDataDealWriteDto;
    }

    private void setBusiSceneCalssXY(BigDecimal qsbj, SettleTransData settleTransData) {
        if(BigDecimal.ZERO.compareTo(qsbj) < 0) {// 清算本金大于0
            // 业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议正回购_到期续作.getValue());
        }
        if(BigDecimal.ZERO.compareTo(qsbj) > 0) {// 清算本金小于0
            // 业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券协议逆回购_到期续作.getValue());
        }
    }

    /**
     * 取下一个工作日
     * @param parseDate
     * @return
     */
    private Date nextWorkDay(Date parseDate) {
        String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SZJYS_CALID);
        return stlDataDealProcessCommonService.getNextWorkDay(parseDate, calendarId);
    }
    
    private List<FinProductQuto> generatorBondFinProductQutoList(List<SzdSjsjg> pgqzSzdSjsjgList,
                                                                 Map<String, List<StlDownDataDealAccountInfoDto>> map,
                                                                 StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
    	List<FinProductQuto> list = new ArrayList<FinProductQuto>();
    	for(SzdSjsjg szdSjsjg : pgqzSzdSjsjgList) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdSjsjg.getJgzqzh() + szdSjsjg.getJgtgdy();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }
            String approvalNo = szdSjsjg.getJgzqdm();
            List<String> finprodIdList = new ArrayList<>();
            List<FinProduct> finProducts = stlDownDataDealMapper.getFinproductByFinProdMarketId(approvalNo + ".SZ");
            if(CollectionUtils.isNotEmpty(finProducts)){
                if( DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finProducts.get(0).getFinprodType2())){
                    finprodIdList.add(finProducts.get(0).getFinprodId());
                }
            }
            if(CollectionUtils.isEmpty(finprodIdList)){
                if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.YWLB_PGQZ)){
                    finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue(), approvalNo);
                }else if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_TGSS)){
                    finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue(), approvalNo);
                }
            }
			if (finprodIdList != null && finprodIdList.size() > 0) {
	            FinProductQuto finProductQuto = new FinProductQuto();
	            finProductQuto.setSeqNo(IDGenerator.generate());
	            finProductQuto.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
				finProductQuto.setFinprodId(finprodIdList.get(0));
	            finProductQuto.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
                if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.YWLB_PGQZ)){
                    finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());    
                }else if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_TGSS)){
                    finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue());
                }
				finProductQuto.setCdate(DateUtil.parse(szdSjsjg.getJgfsrq()));
				finProductQuto.setQuto(abs(szdSjsjg.getJgjssl()));
	            list.add(finProductQuto);
			}
    	}
    	return list;
    }
    
    private List<FinProductQuto> generatorSharesFinProductQutoList(List<SzdSjsjg> pgqzSzdSjsjgList,
                                                                   Map<String, List<StlDownDataDealAccountInfoDto>> map,
                                                                   StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
    	List<FinProductQuto> list = new ArrayList<FinProductQuto>();
    	for(SzdSjsjg szdSjsjg : pgqzSzdSjsjgList) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdSjsjg.getJgzqzh() + szdSjsjg.getJgtgdy();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
//            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
//                continue;
//            }
            String approvalNo = szdSjsjg.getJgzqdm();
            List<String> finprodIdList = new ArrayList<>();
            // GZYHTZJY-1084:优先查标的表 modify fuyongsheng 20220112 start
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
            if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.YWLB_PGQZ)){
                // GNS-855:深交所配股FIN_PRODUCT_QUTO金融产品代码应该是股票代码 modify fuyongsheng 20220214 start
//                if(finProduct != null){
//                    finprodIdList.add(finProduct.getFinprodId());
//                }else{
                finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue(), approvalNo);
//                }
                // GNS-855:深交所配股FIN_PRODUCT_QUTO金融产品代码应该是股票代码 modify fuyongsheng 20220214 end
            }else if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_TGSS) &&
                    StringUtil.equals(szdSjsjg.getJgltlx(), StlDataCreateConstant.JSMX_LTLX)){
                if(finProduct != null){
                    finprodIdList.add(finProduct.getFinprodId());
                }else{
                    finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue(), approvalNo);
                }
            }
            // GZYHTZJY-1084:优先查标的表 modify fuyongsheng 20220112 end
			if (finprodIdList != null && finprodIdList.size() > 0) {
	            FinProductQuto finProductQuto = new FinProductQuto();
	            finProductQuto.setSeqNo(IDGenerator.generate());
	            finProductQuto.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
	            finProductQuto.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
				finProductQuto.setFinprodId(finprodIdList.get(0));
                // GNS-853: 登录股东代码 modify by fuyongsheng 20220216 start
                finProductQuto.setBondId(szdSjsjg.getJgzqzh());
                // GNS-853: 登录股东代码 modify by fuyongsheng 20220216 end
                if (StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.YWLB_PGQZ)){
                    finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());    
                }else {
                    finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue());
                }
				//取值从成交日期修改为发送日期
				finProductQuto.setCdate(DateUtil.parse(szdSjsjg.getJgfsrq()));
				finProductQuto.setQuto(abs(szdSjsjg.getJgjssl()));
	            list.add(finProductQuto);
			}
    	}
    	return list;
    }

    /**
     * 处理QPPX、QPDF、ZQSH场景类型交收数据
     * @param szdSjsjg
     * @param stlDownDataDealOperatorDto
     * @param accountInfoDtolist
     * @return
     */
    private StlDownDataDealWriteBaseDto dealBondSpecialType(SzdSjsjg szdSjsjg,
                                                            StlDownDataDealOperatorDto stlDownDataDealOperatorDto, List<StlDownDataDealAccountInfoDto> accountInfoDtolist){
        StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
        // 定义变量.成交数据明细DTO
        SettleTransData settleTransData = new SettleTransData();
        // 定义变量.成交数据明细附属表DTO
        SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
        String generate = IDGenerator.generate();
        // 流水号
        settleTransData.setTradeId(generate);
        // 成交编号   修改为取执行编号
        settleTransData.setCjbh(szdSjsjg.getJgzxbh());
        // 申请编号   
        settleTransData.setApplyTradeId(szdSjsjg.getJgddbh());
        // 金融产品代码
        // ZGZGST-4212:修改资产代码取值字段 fuyongsheng 20211214 modify start
        // settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm2() + ".SZ");
        settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsjg.getJgzqdm() + ".SZ");
        // ZGZGST-4212:修改资产代码取值字段 fuyongsheng 20211214 modify end
        // 金融产品类型(投管分类)
        settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        // 金融产品类型(估值核算)
        settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
        // 分支序号
        settleTransData.setBranch(0L);
        // 业务类型
        settleTransData.setBusiType(DictCodeListExt3.业务类型_债券交易.getValue());
        // 交易日期
        // modify by fuyongsheng 交易日无需+1自然日 start
        // ImplCalendar implCalendar = new ImplCalendar();
        // settleTransData.setTradeDate(DateUtil.parse(implCalendar.calcDate(szdSjsjg.getJgqsrq(), 1,
        //        BussinessDictCodeListRds.操作符_加.getValue(), BussinessDictCodeListRds.特征值偏移单位_自然日.getValue())));
        settleTransData.setTradeDate(DateUtil.parse(szdSjsjg.getJgqsrq()));
        // modify by fuyongsheng 交易日无需+1自然日 end
        // 交割日期
        settleTransData.setSettleDate(DateUtil.parse(szdSjsjg.getJgjsrq()));
        // 交易币种
        settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdSjsjg.getJghbdh()));
        // 交易平台
        settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdSjsjg.getJgjyfs()));
        // 交收方式
        settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdSjsjg.getJgjsfs()));
        // 交收结果
        settleTransData.setDeliveryResult(setDeliveryResult(szdSjsjg));
        // 执行日期
        settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
        // 证券品种
        settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
        // 清算速度
        settleTransData.setSettleSpeed(0L);
        if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
            settleTransData.setSettleSpeed(1L);
        }
        // 交易场所
        settleTransData.setTradeMarket(DictCodeList.交易流通场所_深圳证券交易所.getValue());
        // 交易市场
        settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        // 席位号(托管单元)
        settleTransData.setSeatNumber(szdSjsjg.getJgtgdy());
        // 组合代码
        settleTransData.setPortfolioId(null);
        // 证券账户代码
        settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
        // 资金账户
        settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
        // 临时表流水号
        settleTransData.setSeqNo(szdSjsjg.getSeqno());
        // 交易份额  清算数据Entity.成交数量/100
        // ZGZGST-4207:通过工具类处理，防止字段为空 fuyongsheng 20211214 modify start
        // settleTransData.setShareAmt(new BigDecimal(Math.abs(szdSjsjg.getJgcjsl().divide(new BigDecimal(100)).doubleValue())));
        settleTransData.setShareAmt(new BigDecimal(Math.abs(ArithmeticUtil.divide(szdSjsjg.getJgcjsl(),new BigDecimal(100),2).doubleValue())));
        // ZGZGST-4207:通过工具类处理，防止字段为空 fuyongsheng 20211214 modify end
        // 单位面值 调用【当前面值】
        settleTransData.setParValue(stlDataDealProcessCommonService.getUnitPrice(settleTransData.getFinprodId(),
                settleTransData.getTradeDate()));
        if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_QPDF)){ // QPDF
            // 交易方向
            settleTransData.setPs(DictCodeList.交易方向_还本.getValue());
            // 业务场景细类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_还本收息_还本.getValue());
            // 单位净价
            settleTransData.setUnitCprice(new BigDecimal(0));
            // 单位全价
            settleTransData.setUnitFprice(new BigDecimal(0));
            // 单位利息
            settleTransData.setUnitInt(new BigDecimal(0));
            // 交易本金(单位面值*交易份额)
            // modify by etliu 20220227 start
            // 广农商二阶段 GNS-901：【广农商场内清算】场内债券存续期-涉及到还本的交易，“利息明细信息”中未登录还本金额
//            settleTransData.setPrinAmt(ArithmeticUtil.multiply(settleTransData.getParValue(), settleTransData.getShareAmt()));
            settleTransData.setPrinAmt(szdSjsjg.getJgsfje());
            // modify by etliu 20220227 end
            // 净价总额
            settleTransData.setCpriceAmt(new BigDecimal(0));
            // 利息总额
            settleTransData.setIntAmt(new BigDecimal(0));
            // 全价总额 abs(清算数据Entity.收付净额)
            settleTransData.setFpriceAmt(szdSjsjg.getJgsfje().abs());
        }else if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_QPPX)){ // QPPX
            settleTransData.setPs( DictCodeList.交易方向_收息.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_收息.getValue());
            // 单位净价
            settleTransData.setUnitCprice(new BigDecimal(0));
            // 单位全价
            settleTransData.setUnitFprice(new BigDecimal(0));
            // 单位利息
            settleTransData.setUnitInt(new BigDecimal(0));
            // 交易本金
            settleTransData.setPrinAmt(new BigDecimal(0));
            // 净价总额
            settleTransData.setCpriceAmt(new BigDecimal(0));
            // 利息总额 abs(清算数据Entity.收付净额)
            settleTransData.setIntAmt(szdSjsjg.getJgsfje().abs());
            // 全价总额 abs(清算数据Entity.收付净额)
            settleTransData.setFpriceAmt(szdSjsjg.getJgsfje().abs());
        }else if(StringUtil.equals(szdSjsjg.getJgywlb(), StlDataDealProcessConstant.SJSJG_YWLB_ZQSH)){ // ZQSH
            settleTransData.setPs( DictCodeList.交易方向_行权_赎回.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_赎回.getValue());
            // 单位全价
            settleTransData.setUnitFprice(ArithmeticUtil.divide(szdSjsjg.getJgsfje(), szdSjsjg.getJgqsbj(), 2));
            // 单位利息(此公共方法计算多种参数,仅使用单位利息)
            stlDownDataDealService.calc(settleTransData);
            // 单位净价 变量.单位全价 - 变量.单位利息
            settleTransData.setUnitCprice(ArithmeticUtil.subtract(settleTransData.getUnitFprice(), settleTransData.getUnitInt()));
            // 交易本金 变量.单位面值 * 变量.交易份额
            settleTransData.setPrinAmt(ArithmeticUtil.multiply(settleTransData.getParValue(), settleTransData.getShareAmt()));
            // 净价总额 变量.单位净价* 变量.交易份额
            settleTransData.setCpriceAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()));
            // 利息总额 abs(清算数据Entity.收付净额)-净价总额
            settleTransData.setIntAmt(ArithmeticUtil.subtract(szdSjsjg.getJgsfje().abs(), settleTransData.getCpriceAmt()));
            // 全价总额 abs(清算数据Entity.收付净额)
            settleTransData.setFpriceAmt(szdSjsjg.getJgsfje().abs());
        }
        // 印花税
        settleTransData.setStampFee(BigDecimal.ZERO);
        // 经手费
        settleTransData.setJsfAmt(BigDecimal.ZERO);
        // 过户费
        settleTransData.setGhfAmt(BigDecimal.ZERO);
        // 证管费
        settleTransData.setZgfAmt(BigDecimal.ZERO);
        // 手续费
        settleTransData.setSxfAmt(BigDecimal.ZERO);
        // 结算费
        settleTransData.setSettleFee(BigDecimal.ZERO);
        // 交易金额
        settleTransData.setTradeAmt(abs(szdSjsjg.getJgsfje()));
        // 佣金
        settleTransData.setCommissionAmt(BigDecimal.ZERO);
        // 佣金_原始
        settleTransData.setCommissionAmtRaw(BigDecimal.ZERO);
        // 交易金额_原始
        settleTransData.setTradeAmtRaw(abs(szdSjsjg.getJgsfje()));
        // 印花税_原始
        settleTransData.setStampFeeRaw(abs(szdSjsjg.getJgyhs()));
        // 经手费_原始
        settleTransData.setJsfAmtRaw(abs(szdSjsjg.getJgjyjsf()));
        // 过户费_原始
        settleTransData.setGhfAmtRaw(abs(szdSjsjg.getJgghf()));
        // 证管费_原始
        settleTransData.setZgfAmtRaw(abs(szdSjsjg.getJgjggf()));
        // 手续费_原始
        settleTransData.setSxfAmtRaw(abs(szdSjsjg.getJgsxf()));
        // 结算费_原始
//        settleTransData.setSettleFeeRaw(abs(szdSjsjg.getJgjsf()));
        // 是否优先配售
        settleTransData.setIsPurchTypeYx(DictCodeList.是否_否.getValue());
        stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
        stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
        // 成交数据与投管交易关系
        SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
        settleTransTgMapping.setTradeId(settleTransData.getTradeId());
        stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
        return stlDownDataDealWriteBaseDto;
    }


    // 期限品种取得-SJSJG的回购
    private String getTermType(Date vdate, Date Mdate) {
        //起息日-到期日 <= 1
        if (BigDecimal.ONE.compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_001.getValue();
        }
        //1< 起息日-到期日 <=2  
        else if (BigDecimal.ONE.compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(2).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_002.getValue();
        }
        //2< 起息日-到期日 <=3  
        else if (new BigDecimal(2).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(3).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_003.getValue();
        }
        //3< 起息日-到期日 <=4  
        else if (new BigDecimal(3).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(4).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_004.getValue();
        }
        //4< 起息日-到期日 <=7 
        else if (new BigDecimal(4).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(7).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_007.getValue();
        }
        //7< 起息日-到期日 <=14  
        else if (new BigDecimal(7).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(14).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_014.getValue();
        }
        //14< 起息日-到期日 <=28  
        else if (new BigDecimal(14).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(28).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_028.getValue();
        }
        //28< 起息日-到期日 <=91  
        else if (new BigDecimal(28).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(91).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_091.getValue();
        }
        //91< 起息日-到期日 <=182  
        else if (new BigDecimal(91).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) < 0 &&
                new BigDecimal(182).compareTo(new BigDecimal(getBetweenDayNumber(vdate,Mdate))) >= 0) {
            return DictCodeList.回购期限品种_R_182.getValue();
        }
        return null;
    }

    /**
     * 根据两个日期，取得相隔的天数
     *
     * @param d1 开始日期
     * @param d2 结束日期
     * @return 天数
     */
    public static int getBetweenDayNumber(Date d1, Date d2) {
        LocalDate start = new LocalDate(d1);
        LocalDate end = new LocalDate(d2);
        return Days.daysBetween(start, end).getDays();
    }
}
