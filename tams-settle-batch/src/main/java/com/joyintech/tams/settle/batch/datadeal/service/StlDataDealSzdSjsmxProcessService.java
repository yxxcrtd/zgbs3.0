package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.entity.SettleTransDataAdd;
import com.joyintech.tams.common.base.entity.SettleTransTgMapping;
import com.joyintech.tams.common.base.entity.SzdJsmx;
import com.joyintech.tams.common.base.entity.SzdSjsmx;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
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
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/7/11
 * @see StlDataDealSzdSjsmxProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdSjsmxProcessService {

    private static final List<String> LIST_GFXZ_SHARES = Arrays.asList(
            StlDataDealProcessConstant.SJSMX_GFXZ_00,
            StlDataDealProcessConstant.SJSMX_GFXZ_01,
            StlDataDealProcessConstant.SJSMX_GFXZ_02,
            StlDataDealProcessConstant.SJSMX_GFXZ_05,
            StlDataDealProcessConstant.SJSMX_GFXZ_10,
            StlDataDealProcessConstant.SJSMX_GFXZ_11,
            StlDataDealProcessConstant.SJSMX_GFXZ_12,
            StlDataDealProcessConstant.SJSMX_GFXZ_13,
            StlDataDealProcessConstant.SJSMX_GFXZ_14,
            StlDataDealProcessConstant.SJSMX_GFXZ_22,
            StlDataDealProcessConstant.SJSMX_GFXZ_24
    );

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
            StlDataDealProcessConstant.SJSMX_ZQLB_EF
    );

    @Autowired
    private StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    private SettleCommonService settleCommonService;

    @Autowired(required = false)
    private StlDownDataDealService stlDownDataDealService;

    @Autowired
    private StlDownDataDealMapper stlDownDataDealMapper;

    // 生成数据清算批处理step2 processor的结果Dto(深中登ZSMX)
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdSjsmxWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) throws ParseException {
        // 核算信息全量数据
        Map<String, List<StlDownDataDealAccountInfoDto>> map = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(true);
        // 资产类型
        String finprodType = stlDownDataDealOperatorDto.getFinprodType();
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), finprodType)) {
            stlDownDataDealWriteDto = secTypeIsBondDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), finprodType)) {
            stlDownDataDealWriteDto = secTypeIsRepoDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
            // SETTLE_TRANS_DATA表数据的TRADE_AMT和TRADE_AMT_RAW数据重新设值
            if (stlDownDataDealWriteDto != null && stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList() != null && stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().size() > 0) {
                for (int index = 0; index < stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().size(); index++) {
                    String tradeId = stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().get(index).getSettleTransData().getTradeId();
                    for (SzdSjsmx szdSjsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsmxList()) {
                        if (StringUtil.equals(szdSjsmx.getMxywlsh(), tradeId)) {
                            // modify by etliu 20211225 start
                            // 光大反馈问题修复：产品的这种写法统一改下吧，精度缺失，会导致我们对不上资金余额；
//            				stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().get(index).getSettleTransData().setTradeAmtRaw(new BigDecimal(Math.abs(szdSjsmx.getMxsfje().doubleValue())));
                            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().get(index).getSettleTransData().setTradeAmtRaw(szdSjsmx.getMxsfje().abs());
                            // modify by etliu 20211225 end
                        }
                    }
                }
            }
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), finprodType)) {
            stlDownDataDealWriteDto = secTypeIsFundDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), finprodType)) {
            stlDownDataDealWriteDto = secTypeIsSharesDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
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
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsmx, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsmx.getMxzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            // 单位净价
            settleTransData.setUnitCprice(szdSjsmx.getMxcjjg());
            // 单位全价
            settleTransData.setUnitFprice(szdSjsmx.getMxqsjg());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
            // 交易币种
            settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdSjsmx.getMxhbdh()));
            // 交易方向、业务场景明细分类
            setBondDeal(szdSjsmx, settleTransData);
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsmx.getMxqssl()));
            // 全价总额
            settleTransData.setFpriceAmt(abs(szdSjsmx.getMxqsbj()));

            // 日终过来的文件结算价格就是全价的，需要反算净价总额、应计利息总额
            stlDownDataDealService.calc(settleTransData);

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
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsmx, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(szdSjsmx.getMxzqdm());
            // 单位全价
            settleTransData.setUnitFprice(szdSjsmx.getMxqsjg());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_债券质押式回购交易.getValue());
            // 交易币种固定为人民币
            settleTransData.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // 利率
            settleTransData.setIntRate(ArithmeticUtil.divide(abs(szdSjsmx.getMxcjjg()), new BigDecimal(100), 8));
            // 计息基础
            settleTransData.setBasis(DictCodeList.计息基础_A_365.getValue());
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsmx.getMxqsbj()));
            // 回购交易
            setRepoDeal(szdSjsmx, settleTransData);
            // 期限品种
            setTermType(szdSjsmx, settleTransData);
            // 目前回购交易平台只有竞价
            settleTransData.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
            settleTransDataAdd.setTradeId(settleTransData.getTradeId());
            // GNS-847: 修改回购类型赋值 modify by fuyongsheng 20220216 start
            // settleTransDataAdd.setRepoType(settleTransData.getBusiType());
            settleTransDataAdd.setRepoType(DictCodeList.回购类型_质押式竞价回购.getValue());
            // GNS-847: 修改回购类型赋值 modify by fuyongsheng 20220216 end
            settleTransDataAdd.setSeRepDayType(CacheUtil.getSysParam(StlDataDealProcessConstant.STL_SE_REP_DAY_TYPE));
            return true;
        }));
    }

    /**
     * 基金类型SJSMX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 结算数据
     * @throws ParseException
     */
    private StlDownDataDealWriteDto secTypeIsFundDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        // 全量基金信息获取
        Map<String, FundInfoDto> fundInfoDtoMap = settleCommonService.getAllFundInfoDto();
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsmx, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdSjsmx.getMxzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_基金.getValue());
            // 交易币种
            settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdSjsmx.getMxhbdh()));
            // 单位净价
            settleTransData.setUnitCprice(szdSjsmx.getMxcjjg());
            // 业务类型、交易方向、业务场景明细分类等
            setFundDeal(szdSjsmx, settleTransData, fundInfoDtoMap, stlDownDataDealOperatorDto);
            return true;
        }));
    }

    /**
     * 股票类型SJSMX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsmx, settleTransData, settleTransDataAdd) -> {
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsmx.getMxzqdm() + ".SZ");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());
            // 交易币种
            settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdSjsmx.getMxhbdh()));
            // 单位净价
            settleTransData.setUnitCprice(szdSjsmx.getMxcjjg());
            // 交易方向、业务场景明细分类
            setSharesDeal(szdSjsmx, settleTransData);
            return true;
        }));
    }

    /**
     * SJSMX公共数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeCommonDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map, PersonalityFunction<SzdSjsmx, SettleTransData, SettleTransDataAdd> funcion) throws ParseException {
        // 处理结果数据定义
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        // 遍历 输入.ZSMX清算数据List 清算数据Entity
        for(SzdSjsmx szdSjsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsmxList()) {
            // KEY = 证券账户号码 + 交易单元
            String key = szdSjsmx.getMxzqzh() + szdSjsmx.getMxjydy();
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

            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsmx.getMxcjsl()));
            // 交易平台
            settleTransData.setTradePlat(stlDataDealProcessCommonService.getTradePlat(szdSjsmx.getMxjyfs()));
            // 交收方式
            settleTransData.setSettleMode(stlDataDealProcessCommonService.getSettleMode(szdSjsmx.getMxjsfs()));
            // 成交编号
            String generate = IDGenerator.generate();
            settleTransData.setTradeId(generate);
            settleTransData.setCjbh(szdSjsmx.getMxzxbh());// modify 成交编号变更 20210825
            // 执行日期
            settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
            // 证券品种
            settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
            // 申请编号
            settleTransData.setApplyTradeId(szdSjsmx.getMxddbh());
            // 分支序号
            settleTransData.setBranch(0L);
            // 交易日期
            settleTransData.setTradeDate(DateUtil.parseDate(szdSjsmx.getMxqsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交割日期
            settleTransData.setSettleDate(DateUtil.parseDate(szdSjsmx.getMxqsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            if (StringUtil.equals(settleTransData.getSettleMode(), DictCodeListStl.交收方式_非担保.getValue())){
                settleTransData.setSettleDate(DateUtil.parseDate(szdSjsmx.getMxjsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            }
            // 清算速度
            settleTransData.setSettleSpeed(0L);
            if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
                settleTransData.setSettleSpeed(1L);
            }
            // 全价总额
            settleTransData.setFpriceAmt(abs(szdSjsmx.getMxqsbj()));
            // 印花税
            settleTransData.setStampFee(abs(szdSjsmx.getMxyhs()));
            // 经手费
            settleTransData.setJsfAmt(abs(szdSjsmx.getMxjyjsf()));
            // 过户费
            settleTransData.setGhfAmt(abs(szdSjsmx.getMxghf()));
            // 证管费
            settleTransData.setZgfAmt(abs(szdSjsmx.getMxjggf()));
            // 手续费
            settleTransData.setSxfAmt(abs(szdSjsmx.getMxsxf()));
            // 结算费
            settleTransData.setSettleFee(abs(szdSjsmx.getMxjsf()));
            // 交易金额
            settleTransData.setTradeAmt(abs(szdSjsmx.getMxqsbj()));
            // 交易金额_原始
            settleTransData.setTradeAmtRaw(abs(szdSjsmx.getMxsfje()));
            // 印花税_原始
            settleTransData.setStampFeeRaw(abs(szdSjsmx.getMxyhs()));
            // 经手费_原始
            settleTransData.setJsfAmtRaw(abs(szdSjsmx.getMxjsf()));
            // 过户费_原始
            settleTransData.setGhfAmtRaw(abs(szdSjsmx.getMxghf()));
            // 证管费_原始
            settleTransData.setZgfAmtRaw(abs(szdSjsmx.getMxjggf()));
            // 手续费_原始
            settleTransData.setSxfAmtRaw(abs(szdSjsmx.getMxsxf()));
            // 结算费_原始
            settleTransData.setSettleFeeRaw(abs(szdSjsmx.getMxjsf()));
            // 交易场所
            settleTransData.setTradeMarket(DictCodeList.交易流通场所_深圳证券交易所.getValue());
            // 交易市场
            settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            // 席位号
            settleTransData.setSeatNumber(szdSjsmx.getMxjydy());
            // 组合代码
            settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
            // 证券账户代码
            settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
            // 资金账户
            settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
            // 临时表流水号
            settleTransData.setSeqNo(szdSjsmx.getSeqno());

            if(null != funcion) {
                if(!funcion.apply(szdSjsmx, settleTransData, settleTransDataAdd)) {
                    continue;
                }
            }

            StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
            stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
            if (settleTransDataAdd != null && StringUtil.isNotEmpty(settleTransData.getTradeId())) {
                settleTransDataAdd.setTradeId(settleTransData.getTradeId());
                stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
            }
            // 成交数据与投管交易关系
            SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
            settleTransTgMapping.setTradeId(settleTransData.getTradeId());//  业务流水号
            stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
        }
        return stlDownDataDealWriteDto;
    }

    private BigDecimal abs(BigDecimal value) {
        return null != value ? value.abs() : null;
    }

    /**
     * 设置期限品种
     * @param szdSjsmx
     * @param settleTransData
     */
    private void setTermType(SzdSjsmx szdSjsmx, SettleTransData settleTransData) {
        String zqdm = szdSjsmx.getMxzqdm();
        switch (zqdm) {
            case StlDataDealProcessConstant.SJSMX_ZQDM_131810:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_001.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131811:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_002.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131800:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_003.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131809:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_004.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131801:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_007.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131802:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_014.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131803:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_028.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131805:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_091.getValue());
                break;
            case StlDataDealProcessConstant.SJSMX_ZQDM_131808:
                settleTransData.setTermType(DictCodeList.回购期限品种_R_182.getValue());
                break;
            default:

        }
    }

    /**
     * 回购交易设置
     * @param szdSjsmx
     * @param settleTransData
     */
    private void setRepoDeal(SzdSjsmx szdSjsmx, SettleTransData settleTransData) throws ParseException {
        // 清算本金
        BigDecimal qsbj = szdSjsmx.getMxqsbj();
        // 业务类别
        String ywlb = szdSjsmx.getMxywlb();
        if(null != qsbj && qsbj.compareTo(BigDecimal.ZERO) > 0) {
//            // 金融产品类型(投管分类)
//            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
//            // 金融产品类型(估值核算)
//            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
            // GNS-893:使用公司工具类解析日期 modify by fuyongsheng 20220216 start
            if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSMX_YWLB_HGCS)) {
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
                // 起息日
                settleTransData.setVdate(DateUtil.parse(szdSjsmx.getMxcjrq()));
                // 到期日
                settleTransData.setMdate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parse(szdSjsmx.getMxjsrq()));
                // 到期交收日
                // GNS-933:首期的到期交收日取其他日期的下一个工作日 modify by fuyongsheng 20220222 start
                // settleTransData.setMpayDate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                settleTransData.setMpayDate(this.nextWorkDay(DateUtil.parse(szdSjsmx.getMxqtrq())));
                // GNS-933:首期的到期交收日取其他日期的下一个工作日 modify by fuyongsheng 20220222 end
                // 业务场景分类
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_首期_买入.getValue());
            }
            else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSMX_YWLB_HGDQ)) {
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
                // 起息日
                settleTransData.setVdate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                // 到期日
                settleTransData.setMdate(DateUtil.parse(szdSjsmx.getMxcjrq()));
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                // 到期交收日
                settleTransData.setMpayDate(DateUtil.parse(szdSjsmx.getMxjsrq()));
                // 业务场景分类
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价逆回购_到期.getValue());
                // 竞价回购到期不登录applyTradeId
                settleTransData.setApplyTradeId(null);
            }
        }
        else {
            //            // 金融产品类型(投管分类)
//            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
//            // 金融产品类型(估值核算)
//            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());

            if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSMX_YWLB_HGCS)) {
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue());
                // 起息日
                settleTransData.setVdate(DateUtil.parse(szdSjsmx.getMxcjrq()));
                // 到期日
                settleTransData.setMdate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parse(szdSjsmx.getMxjsrq()));
                // 到期交收日
                // GNS-933:首期的到期交收日取其他日期的下一个工作日 modify by fuyongsheng 20220222 start
                // settleTransData.setMpayDate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                settleTransData.setMpayDate(this.nextWorkDay(DateUtil.parse(szdSjsmx.getMxqtrq())));
                // GNS-933:首期的到期交收日取其他日期的下一个工作日 modify by fuyongsheng 20220222 end
                // 业务场景分类
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价逆回购_首期_买入.getValue());
            }
            else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSMX_YWLB_HGDQ)) {
                // 金融产品类型(投管分类)
                settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
                // 金融产品类型(估值核算)
                settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue());
                // 起息日
                settleTransData.setVdate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                // 到期日
                settleTransData.setMdate(DateUtil.parse(szdSjsmx.getMxcjrq()));
                // 首期交收日
                settleTransData.setVpayDate(DateUtil.parse(szdSjsmx.getMxqtrq()));
                // 到期交收日
                settleTransData.setMpayDate(DateUtil.parse(szdSjsmx.getMxjsrq()));
                // GNS-893:使用公司工具类解析日期 modify by fuyongsheng 20220216 end
                // 业务场景分类
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券质押式竞价正回购_到期.getValue());
                // 竞价回购到期不登录applyTradeId
                settleTransData.setApplyTradeId(null);
            }
        }
        if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSMX_YWLB_HGCS)) {
            // 交易方向
            settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
            // 本金金额
            settleTransData.setPrinAmt(abs(qsbj));
            // 到期利息 => 变量.本金金额*变量.回购利率*（变量.到期交收日-变量.首期交收日)/365
            BigDecimal hglx = ArithmeticUtil.multiply(ArithmeticUtil.multiply(settleTransData.getPrinAmt(), settleTransData.getIntRate()),
                    ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getVpayDate(), settleTransData.getMpayDate())), 365, 14));
            settleTransData.setmIntAmt(ArithmeticUtil.round(hglx, 2, RoundingMode.HALF_UP));
            // 到期本金
            settleTransData.setmPrinAmt(settleTransData.getPrinAmt());
            // 到期金额
            settleTransData.setmTradeAmt(ArithmeticUtil.doAdd(settleTransData.getPrinAmt(), settleTransData.getmIntAmt()));
            // GNS-933:单位竞价赋值,首期的fintradeproduct的回购利率取得该值 modify by fuyongsheng 20220222 start
            // 单位净价
            settleTransData.setUnitCprice(settleTransData.getIntRate());
            // GNS-933:单位竞价赋值,首期的fintradeproduct的回购利率取得该值 modify by fuyongsheng 20220222 end
        }
        else if(StringUtil.equals(ywlb, StlDataDealProcessConstant.SJSMX_YWLB_HGDQ)) {
            // 交易方向
            settleTransData.setPs(DictCodeList.交易方向_到期.getValue());
            // GNS-925:修改到期赋值 modify by fuyongsheng 20220228 start
            // 本金金额
            settleTransData.setPrinAmt(abs(qsbj));
            // 单位净价
            settleTransData.setUnitCprice(settleTransData.getIntRate());
            // 单位全价
            settleTransData.setUnitFprice(null);
            // 到期利息 => 变量.本金金额*变量.回购利率*（变量.到期交收日-变量.首期交收日)/365
            BigDecimal hglx = ArithmeticUtil.multiply(ArithmeticUtil.multiply(settleTransData.getPrinAmt(), settleTransData.getIntRate()),
                    ArithmeticUtil.divide(new BigDecimal(DateUtil.getBetweenDayNumber(settleTransData.getVpayDate(), settleTransData.getMpayDate())), 365, 14));
            settleTransData.setIntAmt(ArithmeticUtil.round(hglx, 2, RoundingMode.HALF_UP));
            // GNS-925:修改到期赋值 modify by fuyongsheng 20220228 end
        }
    }

    /**
     * 债券交易时设置交易方向和业务场景明细
     * @param szdSjsmx
     * @param settleTransData
     */
    private void setBondDeal(SzdSjsmx szdSjsmx, SettleTransData settleTransData) {
        if(isBondDeal(szdSjsmx)) {
            if(szdSjsmx.getMxqsbj() != null && szdSjsmx.getMxqsbj().compareTo(BigDecimal.ZERO) > 0) {
                settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_债券卖出.getValue());
            }
            else {
                settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_债券买入.getValue());
            }
        }
    }

    /**
     * 股票交易时设置交易方向和业务场景明细
     * @param szdSjsmx
     * @param settleTransData
     */
    private void setSharesDeal(SzdSjsmx szdSjsmx, SettleTransData settleTransData) {
        if(isSharesDeal(szdSjsmx)) {
            if(szdSjsmx.getMxqsbj() != null && szdSjsmx.getMxqsbj().compareTo(BigDecimal.ZERO) > 0) {
                settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_卖出.getValue());
            }
            else {
                settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_买入.getValue());
            }
        }
    }

    /**
     * 基金交易时设置业务类型、交易方向和业务场景明细等
     * @param szdSjsmx
     * @param settleTransData
     * @param fundInfoDtoMap
     * @param stlDownDataDealOperatorDto
     */
    private void setFundDeal(SzdSjsmx szdSjsmx, SettleTransData settleTransData, Map<String, FundInfoDto> fundInfoDtoMap, StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
        if(isFundDeal(szdSjsmx) && StringUtil.lowerCase(stlDownDataDealOperatorDto.getDataFile()).startsWith(StlDataDealProcessConstant.SJSMX_1)) {
            if(szdSjsmx.getMxqsbj() != null && szdSjsmx.getMxqsbj().compareTo(BigDecimal.ZERO) > 0) {
                settleTransData.setPs(DictCodeList.交易方向_卖出.getValue());
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_15, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_LOF_卖出.getValue());
                }
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_16, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_开放式基金_卖出.getValue());
                }
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_18, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_卖出.getValue());
                }
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_EF, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_卖出.getValue());
                }
            }
            else {
                settleTransData.setPs(DictCodeList.交易方向_买入.getValue());
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_15, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_LOF_买入.getValue());
                }
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_16, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_开放式基金_买入.getValue());
                }
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_18, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_封闭式基金_买入.getValue());
                }
                if (StringUtil.equals(StlDataDealProcessConstant.SJSMX_ZQLB_EF, szdSjsmx.getMxzqlb())) {
                    settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_基金_ETF_买入.getValue());
                }
            }
        }
        FundInfoDto fundInfoDto = fundInfoDtoMap.get(DictCodeListExt1.金融产品类型_投管用_基金.getValue() + GlobalConstant.UNDER_LINE + szdSjsmx.getMxzqdm() + ".SZ");
        if (fundInfoDto != null) {
            // 业务类型
            if (StringUtil.equals(DictCodeList.基金类型_货币基金.getValue(), fundInfoDto.getType1())) {
                settleTransData.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
            }
            else {
                settleTransData.setBusiType(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue());
            }
        }

    }

    /**
     * 判断是否为债券交易
     * @param szdSjsmx
     * @return
     */
    private boolean isBondDeal(SzdSjsmx szdSjsmx) {
        /*
         * 业务类别
         *   JY00-集中竞价平台交易，综合金融服务平台交易（担保交收模式）
         *   JY01-综合金融服务平台交易（非担保交收模式）
         * 证券类别
         *   10-国债现货
         *   11-公司/企业债
         *   12-可转换债券
         *   1A-政府支持债券
         *   1C-公司债
         *   1E-可交换公司债
         *   1F-国债及政策性金融债分销
         *   1H-公司债或企业债分销
         *   1J-证券公司次级债
         *   1R-政策性金融债
         *   1S-私募债
         *   1X-资管产品
         *   1Y-优先股
         *   1Z-资产证券化
         * 交易方式
         *   01-普通交易(经深交所)
         *   02-综合金融服务平台交易(经深交所)
         *   03-经深交所申报的非交易业务
         */
        String jyfs = szdSjsmx.getMxjyfs();
        String zqlb = szdSjsmx.getMxzqlb();
        String ywlb = szdSjsmx.getMxywlb();
        return LIST_ZQLB_BONDS.contains(zqlb)
                && (StringUtil.equals(StlDataDealProcessConstant.SJSMX_YWLB_JY00, ywlb) || StringUtil.equals(StlDataDealProcessConstant.SJSMX_YWLB_JY01, ywlb))
                && (StringUtil.equals(StlDataDealProcessConstant.SJSMX_JYFS_01, jyfs) || StringUtil.equals(StlDataDealProcessConstant.SJSMX_JYFS_02, jyfs) || StringUtil.equals(StlDataDealProcessConstant.SJSMX_JYFS_03, jyfs));
    }

    /**
     * 判断是否为股票买卖
     * @param szdSjsmx
     * @return
     */
    private boolean isSharesDeal(SzdSjsmx szdSjsmx) {
        /*
         * 股份性质
         *   00-无限售流通股
         *   01-IPO后限售股
         *   02-股权激励限售股
         *   05-IPO前限售股
         *   10-发起人国家股
         *   11-发起人国有法人股
         *   12-发起人境内法人股
         *   13-发起人外资法人股
         *   14-发起人自然人股
         *   22-定向法人境内法人股
         *   24-定向法人自然人股
         * 交易方式
         *   01-普通交易(经深交所)
         *   02-综合金融服务平台交易(经深交所)
         * 业务类别
         *   JY00-集中竞价平台交易，综合金融服务平台交易（担保交收模式）
         * 证券类别
         *   00-A 股
         *   03-权证
         *   07-A 股增发
         *   30-创业板证券
         *   37-创业板增发
         */
        String gfxz = szdSjsmx.getMxgfxz();
        String jyfs = szdSjsmx.getMxjyfs();
        String zqlb = szdSjsmx.getMxzqlb();
        String ywlb = szdSjsmx.getMxywlb();
        return LIST_GFXZ_SHARES.contains(gfxz)
                && (StringUtil.equals(StlDataDealProcessConstant.SJSMX_JYFS_01, jyfs) || StringUtil.equals(StlDataDealProcessConstant.SJSMX_JYFS_02, jyfs))
                && LIST_ZQLB_SHARES.contains(zqlb)
                && StringUtil.equals(StlDataDealProcessConstant.SJSMX_YWLB_JY00, ywlb);
    }

    /**
     * 判断是否为基金买卖
     * @param szdSjsmx
     * @return
     */
    private boolean isFundDeal(SzdSjsmx szdSjsmx) {
        /*
         * 股份性质
         *   00-无限售流通股
         * 业务类别
         *   JY00-集中竞价平台交易，综合金融服务平台交易（担保交收模式）
         *   JY01-综合金融服务平台交易（非担保交收模式）
         * 证券类别
         *   15-LOF
         *   1603-开放式基金
         *   18-封闭式基金
         *   EF-ETF
         */
        // modify by etliu 20220126 start
//        String gfxz = szdSjsmx.getMxgfxz();
        String zqlb = szdSjsmx.getMxzqlb();
        String ywlb = szdSjsmx.getMxywlb();
        // GZYHTZJY-609: 【场内对账清结算】：基金深交所竞价交易，在系统中配置了佣金的参数，但是在场内清算数据中没有带出来
        return /*StringUtil.equals(StlDataDealProcessConstant.SJSMX_GFXZ_00, gfxz)
                && */(StringUtil.equals(StlDataDealProcessConstant.SJSMX_YWLB_JY00, ywlb) || StringUtil.equals(StlDataDealProcessConstant.SJSMX_YWLB_JY01, ywlb))
                && LIST_ZQLB_FUNDS.contains(zqlb);
        // modify by etliu 20220126 end
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
}
