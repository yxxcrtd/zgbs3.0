package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListStl;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/7/14
 * @see StlDataDealSzdZqbdProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdZqbdProcessService {

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    protected SettleCommonService settleCommonService;
	
	@Autowired
    protected StlDownDataDealMapper stlDownDataDealMapper;

    // 生成数据清算批处理step2 processor的结果Dto(上中登ZQBD)
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdZqbdWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) throws ParseException {
        // 核算信息全量数据
        Map<String, List<StlDownDataDealAccountInfoDto>> map = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(true);
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        if (StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
        	stlDownDataDealWriteDto = secTypeIsFundDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
        	// 将ZQBD清算数据List中，证券类别是配股权益的数据去除，单独放在配股ZQBD清算数据List里面做后续处理。
    		List<SzdZqbd> pgSzdZqbdList = new ArrayList<SzdZqbd>();
    		//上市流通
    		List<SzdZqbd> gpSsltList = new ArrayList<SzdZqbd>();
    		List<SzdZqbd> notPgSzdZqbdList = new ArrayList<SzdZqbd>();
        	if (stlDownDataDealOperatorDto != null && stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto() != null &&
       			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList() != null && 
       			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList().size() > 0) {
        		for (SzdZqbd szdZqbd : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList()) {
        			if (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PG)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLX_00K)) {
        				pgSzdZqbdList.add(szdZqbd);
        			}
        			else if(
                            (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                                    && StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                                    && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                                    && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_P)
                            )
                            ||
                            (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                                    && !StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                                    && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                                    && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_P)
                            )
                            ||
                            (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                                    && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_S)
                                    && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                            )
                    ){
                        gpSsltList.add(szdZqbd);
                    }
        			else {
        				notPgSzdZqbdList.add(szdZqbd);
        			}
        		}
        		stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().setZqbdList(notPgSzdZqbdList);
        	}
        	stlDownDataDealWriteDto = secTypeIsSharesDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
            // 配股ZQBD清算数据List数据单独处理
            if (pgSzdZqbdList != null && pgSzdZqbdList.size() > 0) {
            	List<FinProductQuto> finProductQutoList = generatorSharesFinProductQutoList(pgSzdZqbdList, map, stlDownDataDealOperatorDto);
            	stlDownDataDealWriteDto.setFinProductQutoList(finProductQutoList);
            }
            //股票上市流通
            stlDownDataDealWriteDto.getFinProductQutoList().addAll(generatorBondCirculateFinProductQutoList(gpSsltList, map, stlDownDataDealOperatorDto));
            //
            List<ShareBonusReg> shareBonusRegList = generatorShareBonusRegList(stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList(), map, stlDownDataDealOperatorDto);
            stlDownDataDealWriteDto.setShareBonusRegList(shareBonusRegList);
        }
        if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
        	if (stlDownDataDealOperatorDto != null && stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto() != null &&
           			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList() != null && 
           			stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList().size() > 0) {
                // GNS-888: modify by fuyongsheng 20220218 start
                // 上市流通list
                List<SzdZqbd> zqSsltList = new ArrayList<>();
                // 配债list
                List<SzdZqbd> zqPzList = new ArrayList<>();
	        	for (SzdZqbd szdZqbd : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList()) {
                    if(
                        (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                            && StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                        )
                        ||
                        (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                            && !StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                            && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_P)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                        )
                    ){
                        zqSsltList.add(szdZqbd);
                    }else{
                        zqPzList.add(szdZqbd);
                    }
                }
                List<FinProductQuto> finProductQutoList = generatorBondFinProductQutoList(zqPzList, map, stlDownDataDealOperatorDto);
                // GNS-888: modify by fuyongsheng 20220218 end
	        	List<FinProductQuto> circulateFinProductQutoList = generatorBondCirculateFinProductQutoList(zqSsltList, map, stlDownDataDealOperatorDto);
                finProductQutoList.addAll(circulateFinProductQutoList);
                stlDownDataDealWriteDto.setFinProductQutoList(finProductQutoList);
        	}
        }
        return stlDownDataDealWriteDto;
    }

    // 基金类证券品种处理
    private StlDownDataDealWriteDto secTypeIsFundDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        // 全量基金信息获取
        Map<String, FundInfoDto> fundInfoDtoMap = settleCommonService.getAllFundInfoDto();

        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, (szdZqbd, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdZqbd.getZqdm() + ".SH");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
            // 基金标的
            FundInfoDto fundInfoDto = fundInfoDtoMap.get(settleTransData.getFinprodId());
            // 基金Map不存在则不处理
            if (null == fundInfoDto) {
                return false;
            }
            // 业务类型
            if (StringUtil.equals(DictCodeList.基金类型_货币基金.getValue(), fundInfoDto.getType1())) {
                settleTransData.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
            }
            else {
                settleTransData.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
            }
            // 到期日
            settleTransData.setMdate(DateUtil.addDay(settleTransData.getTradeDate(), -1));
            // 单位面值
            settleTransData.setParValue(fundInfoDto.getFaceValue());
            // 全价总额
            settleTransData.setFpriceAmt(new BigDecimal(szdZqbd.getBdsl()));
            // 交易金额
            settleTransData.setTradeAmt(BigDecimal.ZERO);
            // 交易原始金额
            settleTransData.setTradeAmtRaw(BigDecimal.ZERO);
            // 交易方向
            settleTransData.setPs(DictCodeList.交易方向_红利再投.getValue());
            // 业务场景分类
            settleTransData.setBusiSceneCalss(getBusiSceneCalss(fundInfoDto));
            return true;
        });
    }

    /**
     * 股票类型ABCSJ数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, (szdZqbd, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdZqbd.getZqdm() + ".SH");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());

            // 送股
            // GNS-932:送股转上市流通了,这里不入了 modify by fuyongsheng 20220224 start
//            if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
//                    && StringUtil.equals(szdZqbd.getQylb(), StlDataDealProcessConstant.QYLB_S)
//                    && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)) {
//                // 交易方向
//                // GNS-883:交易方向应为红利再投 modify by fuyongsheng 20220216 start
//                // settleTransData.setPs(DictCodeListStl.交易方向_送股.getValue());
//                settleTransData.setPs(DictCodeList.交易方向_红利再投.getValue());
//                // GNS-883:交易方向应为红利再投 modify by fuyongsheng 20220216 end
//                // 业务场景明细分类
////                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_送股.getValue());
//                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_分红_红利再投.getValue());
//            }
            // GNS-932:送股转上市流通了,这里不入了 modify by fuyongsheng 20220224 end
            // 配股确认
            if((StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT) || StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_XL))
                    && StringUtil.equals(szdZqbd.getQylb(), StlDataDealProcessConstant.QYLB_P)
                    && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)) {
                // 交易方向
                settleTransData.setPs(DictCodeListExt2.交易方向_配股.getValue());
                // 业务场景明细分类
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_配股认购_配股.getValue());
            }
            // 送股-除权登记
            if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                    && StringUtil.equals(szdZqbd.getQylb(), StlDataDealProcessConstant.QYLB_S)
                    // GNS-864: bdlx应是00J modify by fuyongsheng 20220214 start
                    // && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLX_00K)
                    && (StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLX_00K) ||
                    StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLX_00J))
                // GNS-864: bdlx应是00J modify by fuyongsheng 20220214 end
            ) {
                // 交易方向
                // GNS-932:交易方向登成红利再投 modify by fuyongsheng 20220224 start
                // settleTransData.setPs(DictCodeList.交易方向_除权登记.getValue());
                settleTransData.setPs(DictCodeList.交易方向_红利再投.getValue());
                // 业务场景明细分类
                // settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_分红_除权登记.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_送股.getValue());
                String calendarId = CacheUtil.getSysParam(StlDataDealProcessConstant.STL_DOWNDATA_DEAL_SHJYS_CALID);
                // 交割日(取变量.交割日的下一工作日)
                settleTransData.setSettleDate(stlDataDealProcessCommonService.getNextWorkDay(settleTransData.getSettleDate(), calendarId));
                // GNS-932:交易方向登成红利再投 modify by fuyongsheng 20220224 end
            }
            return true;
        });
    }

    /**
     * ZQBD公共数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeCommonDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map, PersonalityFunction<SzdZqbd, SettleTransData, SettleTransDataAdd> funcion) throws ParseException {
        // 处理结果数据定义
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        // 遍历 输入.ZSMX清算数据List 清算数据Entity
        for(SzdZqbd szdZqbd : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZqbdList()) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdZqbd.getZqzh() + szdZqbd.getXwh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }

            // 定义变量.成交数据明细DTO
            SettleTransData settleTransData = new SettleTransData();
            // 定义变量.成交数据明细附属表DTO
            SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
            // 成交编号
            String generate = IDGenerator.generate();
            settleTransData.setTradeId(generate);
            settleTransData.setCjbh(generate);
			// 执行日期
			settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
			// 证券品种
			settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
            // 申请编号
            settleTransData.setApplyTradeId(szdZqbd.getQsbh());
            // 分支序号
            settleTransData.setBranch(0L);
            // 交易日期
            settleTransData.setTradeDate(DateUtil.parseDate(szdZqbd.getBdrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交割日期
            settleTransData.setSettleDate(DateUtil.parseDate(szdZqbd.getBdrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交易币种
            settleTransData.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // 交易份额
            settleTransData.setShareAmt(new BigDecimal(szdZqbd.getBdsl()).abs());
            // 交易场所
            settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
            // 交易市场
            settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            // 交收结果
            settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdZqbd.getJgdm()));

            // TODO 佣金计算

            // 席位号
            settleTransData.setSeatNumber(szdZqbd.getXwh());
            // 组合代码
            settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
            // 证券账户代码
            settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
            // 资金账户
            settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
			// 临时表流水号
			settleTransData.setSeqNo(szdZqbd.getSeqno());

            // 自定义处理
            if(null != funcion) {
                if(!funcion.apply(szdZqbd, settleTransData, settleTransDataAdd)) {
                    continue;
                }
            }

            StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
            stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
//            stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
            // 成交数据与投管交易关系
            SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
            settleTransTgMapping.setTradeId(settleTransData.getTradeId());
            stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
        }
        return stlDownDataDealWriteDto;
    }

    private String getBusiSceneCalss(FundInfoDto fundInfoDto) {
        if (StringUtil.isNotEmpty(fundInfoDto.getType7())) {
            if (StringUtil.equals(DictCodeListExt2.交易所基金类型_ETF.getValue(), fundInfoDto.getType7())) {
                return DictCodeListStl.业务场景分类明细_基金_ETF_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeListExt2.交易所基金类型_LOF.getValue(), fundInfoDto.getType7())) {
                return DictCodeListStl.业务场景分类明细_基金_LOF_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeListExt2.交易所基金类型_分级基金.getValue(), fundInfoDto.getType7())) {
                return DictCodeListStl.业务场景分类明细_基金_分级LOF_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeListExt2.交易所基金类型_封闭式基金.getValue(), fundInfoDto.getType7())) {
                return DictCodeListStl.业务场景分类明细_基金_封闭式基金_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeListExt2.交易所基金类型_实时申赎货币基金.getValue(), fundInfoDto.getType7())) {
                return DictCodeListStl.业务场景分类明细_基金_实时申赎货币基金_分红_红利再投.getValue();
            }
        }
        else {
            if (StringUtil.equals(DictCodeList.基金类型_货币基金.getValue(), fundInfoDto.getType1())) {
                return DictCodeListStl.业务场景分类明细_基金_货币基金_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeList.基金类型_债券基金.getValue(), fundInfoDto.getType1()) || StringUtil.equals(DictCodeList.基金类型_股票基金.getValue(), fundInfoDto.getType1()) ||
                    StringUtil.equals(DictCodeList.基金类型_混合基金.getValue(), fundInfoDto.getType1()) || StringUtil.equals(DictCodeList.基金类型_私募基金.getValue(), fundInfoDto.getType1()) ||
                    StringUtil.equals(DictCodeList.基金类型_其他公募基金.getValue(), fundInfoDto.getType1())) {
                return DictCodeListStl.业务场景分类明细_基金_非货币基金_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeList.基金类型_ETF.getValue(), fundInfoDto.getType1())) {
                return DictCodeListStl.业务场景分类明细_基金_ETF_分红_红利再投.getValue();
            }
            if (StringUtil.equals(DictCodeList.基金类型_LOF.getValue(), fundInfoDto.getType1())) {
                return DictCodeListStl.业务场景分类明细_基金_LOF_分红_红利再投.getValue();
            }
        }
        return null;
    }
    
    private List<FinProductQuto> generatorSharesFinProductQutoList(List<SzdZqbd> pgSzdZqbdList, 
    		Map<String, List<StlDownDataDealAccountInfoDto>> map,
    		StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
    	List<FinProductQuto> list = new ArrayList<FinProductQuto>();
    	for(SzdZqbd szdZqbd : pgSzdZqbdList) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdZqbd.getZqzh() + szdZqbd.getXwh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }
            String approvalNo = szdZqbd.getZqdm();
            List<String> finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue(), approvalNo);
			if (finprodIdList != null && finprodIdList.size() > 0) {
	            FinProductQuto finProductQuto = new FinProductQuto();
	            finProductQuto.setSeqNo(IDGenerator.generate());
	            finProductQuto.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
	            finProductQuto.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
				finProductQuto.setFinprodId(finprodIdList.get(0));
				finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
				finProductQuto.setCdate(DateUtil.parse(szdZqbd.getBdrq()));
                // GNS-853: 登录股东代码 modify by fuyongsheng 20220216 start
                finProductQuto.setBondId(szdZqbd.getZqzh());
                // GNS-853: 登录股东代码 modify by fuyongsheng 20220216 end
				finProductQuto.setQuto(new BigDecimal(szdZqbd.getBdsl()));
	            list.add(finProductQuto);
			}
    	}
    	return list;
    }

    /**
     * 配债
     * @param pgSzdZqbdList
     * @param map
     * @param stlDownDataDealOperatorDto
     * @return
     */
    private List<FinProductQuto> generatorBondFinProductQutoList(List<SzdZqbd> pgSzdZqbdList, 
    		Map<String, List<StlDownDataDealAccountInfoDto>> map,
    		StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
    	List<FinProductQuto> list = new ArrayList<FinProductQuto>();
    	for(SzdZqbd szdZqbd : pgSzdZqbdList) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdZqbd.getZqzh() + szdZqbd.getXwh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }
            String approvalNo = szdZqbd.getZqdm();
            List<String> finprodIdList = new ArrayList<>();
            List<FinProduct> finProducts = stlDownDataDealMapper.getFinproductByFinProdMarketId(szdZqbd.getZqdm() + ".SH");
            if(CollectionUtils.isNotEmpty(finProducts) && DictCodeListExt1.证券品种_债券.getValue().equals(stlDownDataDealOperatorDto.getFinprodType())
                    && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finProducts.get(0).getFinprodType2())){
                finprodIdList.add(finProducts.get(0).getFinprodId());
            }else{
                finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue(), approvalNo);
            }
			if (finprodIdList != null && finprodIdList.size() > 0) {
	            FinProductQuto finProductQuto = new FinProductQuto();
	            finProductQuto.setSeqNo(IDGenerator.generate());
	            finProductQuto.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
	            finProductQuto.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
				finProductQuto.setFinprodId(finprodIdList.get(0));
				finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
				finProductQuto.setCdate(DateUtil.parse(szdZqbd.getBdrq()));
				finProductQuto.setQuto(new BigDecimal(szdZqbd.getBdsl()));
	            list.add(finProductQuto);
			}
    	}
    	return list;
    }

    private List<ShareBonusReg> generatorShareBonusRegList(List<SzdZqbd> pgSzdZqbdList,
                                                           Map<String, List<StlDownDataDealAccountInfoDto>> map,
                                                           StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
        List<ShareBonusReg> list = new ArrayList<ShareBonusReg>();
        for(SzdZqbd szdZqbd : pgSzdZqbdList) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdZqbd.getZqzh() + szdZqbd.getXwh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }
            // 送股-除权登记
            if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                    && StringUtil.equals(szdZqbd.getQylb(), StlDataDealProcessConstant.QYLB_S)
                    && (StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLX_00K)
                    // GNS-864: 兼容bdlx为00J modify by fuyongsheng 20220215 start
                    || StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLX_00J))) {
                // GNS-864: 兼容bdlx为00J modify by fuyongsheng 20220215 end
                ShareBonusReg shareBonusReg = new ShareBonusReg();
                // GNS-864: 修改sharesId赋值 modify by fuyongsheng 20220216 start
                // shareBonusReg.setSharesId(szdZqbd.getZqzh() + ".SH");
                shareBonusReg.setSharesId(szdZqbd.getZqdm() + ".SH");
                // GNS-864: 修改sharesId赋值 modify by fuyongsheng 20220216 end
                shareBonusReg.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
                try {
                    // GNS-864:日期格式解析错误 modify by fuyongsheng 20220216 start
                    // shareBonusReg.setRegDate(DateUtil.parseDate(szdZqbd.getBdrq(),DateUtil.DEFAULT_DATE_FORMAT_EN));
                    shareBonusReg.setRegDate(DateUtil.parseDate(szdZqbd.getBdrq(),DateUtil.DEFAULT_OTHER_FORMAT));
                    // GNS-864:日期格式解析错误 modify by fuyongsheng 20220216 end
                    shareBonusReg.setDelDate(DateUtil.addDay(shareBonusReg.getRegDate(), 1));
                    shareBonusReg.setRedShareListingDate(DateUtil.addDay(shareBonusReg.getRegDate(), 2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // GNS-864:取绝对值 modify by fuyongsheng 20220216 start
                // shareBonusReg.setPerDevidentAmt(new BigDecimal(szdZqbd.getBdsl()));
                shareBonusReg.setPerDevidentAmt(new BigDecimal(szdZqbd.getBdsl()).abs());
                // GNS-864:取绝对值 modify by fuyongsheng 20220216 end
                list.add(shareBonusReg);
            }
        }
        return list;
    }
    //上市流通
    private List<FinProductQuto> generatorBondCirculateFinProductQutoList(List<SzdZqbd> pgSzdZqbdList,
                                                                 Map<String, List<StlDownDataDealAccountInfoDto>> map,
                                                                 StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
        List<FinProductQuto> list = new ArrayList<FinProductQuto>();
        for(SzdZqbd szdZqbd : pgSzdZqbdList) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdZqbd.getZqzh() + szdZqbd.getXwh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }
            String approvalNo = szdZqbd.getZqdm();
            List<String> finprodIdList = new ArrayList<>();
            List<FinProduct> finProducts = stlDownDataDealMapper.getFinproductByFinProdMarketId(szdZqbd.getZqdm() + ".SH");
            if(CollectionUtils.isNotEmpty(finProducts)){
                if(DictCodeListExt1.证券品种_债券.getValue().equals(stlDownDataDealOperatorDto.getFinprodType())
                    && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finProducts.get(0).getFinprodType2())){
                    finprodIdList.add(finProducts.get(0).getFinprodId());
                }
                if(DictCodeListExt1.证券品种_股票.getValue().equals(stlDownDataDealOperatorDto.getFinprodType())
                        && DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finProducts.get(0).getFinprodType2())){
                    finprodIdList.add(finProducts.get(0).getFinprodId());
                }
            }
            if(CollectionUtils.isEmpty(finProducts)){
                finprodIdList = stlDownDataDealMapper.getFinproductIdByApprovalNo(DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue(), approvalNo);
            }
            if (finprodIdList != null && finprodIdList.size() > 0) {
                FinProductQuto finProductQuto = new FinProductQuto();
                finProductQuto.setSeqNo(IDGenerator.generate());
                finProductQuto.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
                finProductQuto.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
                finProductQuto.setFinprodId(finprodIdList.get(0));
                finProductQuto.setOthType(DictCodeListExt3.金融产品其他代码类型_上市流通代码.getValue());
                finProductQuto.setCdate(DateUtil.parse(szdZqbd.getBdrq()));
                // GNS-880:取绝对值 modify by fuyongsheng 20220216 start
                // finProductQuto.setQuto(new BigDecimal(szdZqbd.getBdsl()));
                finProductQuto.setQuto(new BigDecimal(szdZqbd.getBdsl()).abs());
                // GNS-880:取绝对值 modify by fuyongsheng 20220216 end
                if(DictCodeListExt1.证券品种_股票.getValue().equals(stlDownDataDealOperatorDto.getFinprodType())){
                    if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                            && StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                    ){
                        finProductQuto.setPs(DictCodeList.交易方向_申购确认.getValue());
                    }
                    else if (StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                            && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_S)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                    ){
                        finProductQuto.setPs(DictCodeList.交易方向_红利再投.getValue());
                    }else if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                            && !StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                            && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_P)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                    ){
                        finProductQuto.setPs(DictCodeListExt2.交易方向_配股.getValue());
                    }
                }else{ // 债券
                    if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                        && StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                        && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)
                    ){
                        finProductQuto.setPs(DictCodeList.交易方向_申购.getValue());
                    }else if(StringUtil.equals(szdZqbd.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                            && !StringUtil.equals(szdZqbd.getLtlx(),StlDataDealProcessConstant.LTLX_N)
                            && StringUtil.equals(szdZqbd.getQylb(),StlDataDealProcessConstant.QYLB_P)
                            && StringUtil.equals(szdZqbd.getBdlx(), StlDataDealProcessConstant.BDLB_00G)){
                        finProductQuto.setPs(DictCodeListExt3.交易方向_配债.getValue());
                    }
                }
                list.add(finProductQuto);
            }
        }
        return list;
    }

}
