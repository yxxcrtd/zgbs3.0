/*
 * 文件名：InterfRiskRdsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.riskrds.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.entity.SysBatchGrpPrdlink;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.system.base.entity.SysInstitution;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.riskrds.dao.RiskRdsTrialCommonMapper;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.constant.PageFieldToInterfFactory;
import com.joyintech.tams.component.business.riskrds.core.BaseRiskRdsService;
import com.joyintech.tams.component.business.riskrds.dto.EntrustBaseDto;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.dto.TradeBaseDto;
import com.joyintech.tams.component.business.util.CommonBusinessUtil;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.client.ClientUtil;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessConstantRds;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.redisinit.dto.IRiskRds;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 接口组件Service
 *
 * @author 裴家俊
 * @version 1.0 2020年1月8日
 * @see InterfRiskRdsService
 * @since 1.0
 */
@Service
public class InterfRiskRdsService<V extends IRiskRds> extends BaseRiskRdsService<V> {

    // add by zhangrui1 正反同步交易特殊处理 start
    /**
     * 分组ID
     */
    public static final String GROUP_ID = "groupId";

    /**
     * 是否正反同步交易
     */
    public static final String IS_SYNC = "isSync";

    /**
     * 是否转化申请交易
     */
    public static final String IS_CONV = "isConv";

    // add by zhangrui1 正反同步交易特殊处理 end
    // add by zhangrui1 取基金净值 start
    @Autowired
    private FundNetService fundNetService;
    // add by zhangrui1 取基金净值 end

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FinProductConvertibleMapper finProductConvertibleMapper;

    /**
     * 交易类金融产品表
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;

    @Autowired
    private MstCfetsCustomerInfoMapper mstCfetsCustomerInfoMapper;

    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private RiskRdsTrialCommonMapper riskRdsTrialCommonMapper;

    private List<String> 申购确认_赎回申请 = Arrays.asList(DictCodeList.交易方向_赎回.getValue(),
            DictCodeList.交易方向_申购确认.getValue(), DictCodeList.交易方向_赎回到账.getValue());

    private List<String> 场内指令_质押券入库_质押券出库 = Arrays.asList(DictCodeListExt3.交易方向_质押券入库.getValue(),
            DictCodeListExt3.交易方向_质押券出库.getValue());

    private List<String> 产品端_认购赎回分红 = Arrays.asList(DictCodeList.交易方向_产品认购.getValue(), DictCodeList.交易方向_产品申购.getValue()
            , DictCodeList.交易方向_产品赎回.getValue(), DictCodeList.交易方向_份额强增.getValue(), DictCodeList.交易方向_份额强减.getValue(), DictCodeList.交易方向_现金分红.getValue()
            , DictCodeList.交易方向_提前终止.getValue(), DictCodeList.交易方向_提前赎回.getValue(), DictCodeList.交易方向_产品付费.getValue(), DictCodeList.交易方向_税费缴税.getValue()
            , DictCodeList.交易方向_产品提前赎回.getValue(), DictCodeList.交易方向_产品现金分红.getValue(), DictCodeList.交易方向_产品到期.getValue());

    private List<String> 协议回购到期交易方向 = Arrays.asList(DictCodeListExt3.交易方向_提前购回.getValue(), DictCodeListExt3.交易方向_到期购回.getValue()
            , DictCodeListExt3.交易方向_解除质押.getValue());
    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    @Autowired
    private DateCalService dateCalService;

    public ClientData sendRiskRdsDataCommon(Map<String, Object> dataMap, V v) throws IOException, InstantiationException {
        // add by zhangrui1 增加批量接口调用逻辑 start
        String infCode = v.getInfCode();
        boolean isInnerTrd = checkInnerCounter(dataMap);
        //是否是股票的分红送转 add by chendijun 20200523
        boolean isStockFHSZ = (StringUtil.equals((String) dataMap.get(InterfUtil.ps), DictCodeList.股票分红类型_分红送转.getValue())
                && StringUtil.equals((String) dataMap.get("finprodType2"), DictCodeListExt1.金融产品类型_投管用_股票.getValue()));

        //场内交易指令暂不处理的交易方向
        if (StringUtil.equals(dataMap.get("fromEtrExchange"), DictCodeList.是否_是.getValue())
                && StringUtil.isNotEmpty(dataMap.get("ps"))
                && 场内指令_质押券入库_质押券出库.contains(dataMap.get("ps").toString())) {
            return null;
        }

        // 批量接口或者转仓或者是股票分红送转
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占批量接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放批量接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_风险试算批量接口)
                // 预占功能 add by zhangrui1 start
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放批量接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占批量接口)
                // 预占功能 add by zhangrui1 end
                // 交易所协议回购，到期续做 需要拆成两笔 走批量接口 start
                || StringUtil.equals(CommonUtil.findValue(dataMap, "ps"), DictCodeList.交易方向_到期续做.getValue())
                // 交易所协议回购，到期续做 需要拆成两笔 走批量接口 end
                || (isInnerTrd && CommonUtil.isType((String) dataMap.get(InterfUtil.ps), BussinessDictCodeList.交易方向_卖出))
                || isStockFHSZ) {
            // BSTOT-2419 by zhangrui1 Start
            //获取母组合
            exchangePortfolioBySub(dataMap);

            // add by etliu 20210831 start
            if (isStockFHSZ) {
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
                }
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
                }
            }
            // add by etliu 20210831 end

            String counterId = (String) CommonUtil.findValue(dataMap, "counterId");
            if (isInnerTrd || StringUtil.equals(CommonUtil.findValue(dataMap, "ps"), DictCodeList.交易方向_到期续做.getValue())) {
                counterId = AllTradeCommonUtil.exchangePortfolio(counterId);
                dataMap.put(PageFieldToInterfFactory.INF_COUNTERID, counterId);
                // add by zhangrui1 非批量接口时改成批量接口 start
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
                }
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
                }
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_风险试算接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue());
                }
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue());
                }
                if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
                    v.setInfCode(BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
                }
                // add by zhangrui1 非批量接口时改成批量接口 end
            }
            // BSTOT-2419 by zhangrui1 end
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            //判断是否来自场内交易指令
            if (StringUtil.equals(dataMap.get("fromEtrExchange"), DictCodeList.是否_是.getValue())) {
                list = prepareEtrExchangeRiskListEntity(dataMap, v);
            } else {
                list = prepareRiskListEntity(dataMap, v);
            }
            // add by zhangrui1 根据资产类型设置开关 start
            if (isRskOpened(v.getAssetType())) {
                return null;
            }
            // add by zhangrui1 根据资产类型设置开关 end
            logger.info("==========调用共通方法sendListDataCommon==========");
            return this.sendListDataCommon(list, v.getInfCode());
        }
        // add by zhangrui1 债券借贷存续期特别处理 start
        if ((CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                DictCodeListExt1.金融产品类型_投管用_债券借贷融入)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                DictCodeListExt1.金融产品类型_投管用_债券借贷融出))
                // 现金交割
                && CommonUtil.isType((String) dataMap.get("isCashPay"), BussinessDictCodeList.是否_是)) {
            if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
                v.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
            }
            if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
                v.setInfCode(BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
            }
            dataMap.put("ps2", CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                    DictCodeListExt1.金融产品类型_投管用_债券借贷融入) ? BussinessDictCodeList.交易方向_买入.getValue() : BussinessDictCodeList.交易方向_卖出.getValue());
            List<Map<String, Object>> list = periodbondLend(dataMap, v);
            logger.info("==========调用共通方法sendListDataCommon==========");
            return this.sendListDataCommon(list, v.getInfCode());
        }
        // add by zhangrui1 债券借贷存续期特别处理 end
        // add by zhangrui1 增加批量接口调用逻辑 end
        InterfRiskRdsDto interfRiskRdsDto = this.prepareRiskEntity(dataMap, v);
        if (!StringUtil.isNotEmpty(interfRiskRdsDto)) {
            return null;
        }
        interfRiskRdsDto.setInfCode(v.getInfCode());
        interfRiskRdsDto.setEventType(v.getEventType());
        interfRiskRdsDto.setCcy(StringUtil.isNotEmpty(dataMap.get(InterfUtil.CCY)) ? dataMap.get(InterfUtil.CCY).toString() : BussinessDictCodeListRds.币种_人民币_CNY.getValue());
        //暂时没有预占
        // if(CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托)){
        // interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
        // }
        if (!StringUtil.isNotEmpty(interfRiskRdsDto.getAssetType()) && StringUtil.isNotEmpty(v.getAssetType())) {
            interfRiskRdsDto.setAssetType(v.getAssetType());
        }
        // add by zhangrui1 start
        if ((StringUtil.equals(BussinessDictCodeListRds.接口系统ID_预占释放接口.getValue(), v.getInfCode()) && !StringUtil.equals(dataMap.get("manualSettle"), "Y"))
                || StringUtil.equals(BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(), v.getInfCode())) {
            interfRiskRdsDto.setRevokeFlg(DictCodeList.是否_是.getValue());
        }
        // add by zhangrui1 end
        if (isInnerTrd && StringUtil.equals(BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(), v.getInfCode())) {
            interfRiskRdsDto.setRevokeFlg(DictCodeList.是否_是.getValue());
            // add by zhangrui1 处理转仓反向交易的撤销 start
            String tradeId = interfRiskRdsDto.getEventId();
            String rTradeId = AllTradeCommonUtil.queryRTradeId(removeLasStr(tradeId));
            // 拼接转仓时用的实占编号
            if (StringUtil.isNotEmpty(rTradeId)) {
                interfRiskRdsDto.setEventId(removeLasStr(rTradeId) + interfRiskRdsDto.getPs());
            }
            // add by zhangrui1 处理转仓反向交易的撤销 end
            if (StringUtil.isEmpty(interfRiskRdsDto.getAssetCode())) {
                interfRiskRdsDto.setAssetCode(
                        (String) CommonUtil.findValue(dataMap, InterfUtil.finprodId));
            }
        }
        // 可转债申购 申购时 金额根据预估中签比例（系统参数）计算
        doCalcAmtWithB14(dataMap, interfRiskRdsDto);

        //暂不处理的交易方向
        if (StringUtil.isNotEmpty(interfRiskRdsDto.getPs()) && 申购确认_赎回申请.contains(interfRiskRdsDto.getPs())) {
            // 可转债申购确认需要进行实占
            if (doBondToStock(dataMap, interfRiskRdsDto)
                    && !CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托)) {
                return null;
            }
        }

        //场内交易指令暂不处理的交易方向
        if (StringUtil.equals(dataMap.get("fromEtrExchange"), DictCodeList.是否_是.getValue())
                && StringUtil.isNotEmpty(interfRiskRdsDto.getPs())
                && 场内指令_质押券入库_质押券出库.contains(interfRiskRdsDto.getPs())) {
            return null;
        }

        // add by zhangrui1 添加是否内部交易 Start
        interfRiskRdsDto.setIsInnerTrd(isInnerTrd ? BussinessDictCodeListRds.是否_是.getValue()
                : BussinessDictCodeListRds.是否_否.getValue());
        // add by zhangrui1 添加是否内部交易 end
        logger.info("==========调用共通方法sendDataCommon==========");
        //wangyang1 风险二级法人改造 start
        interfRiskRdsDto.setInstCode(MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName()));
        //wangyang1 风险二级法人改造 end
        return this.sendDataCommon(interfRiskRdsDto);
    }

    /**
     * 可转债申购 申购时 金额根据预估中签比例（系统参数）计算
     * 暂时不需要实现具体内容
     *
     * @param dataMap          原始数据
     * @param interfRiskRdsDto 报文数据
     */
    private void doCalcAmtWithB14(Map<String, Object> dataMap, InterfRiskRdsDto interfRiskRdsDto) {
//        if(StringUtil.equals(CommonUtil.findValue(dataMap, "windtypeone"),DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
//                && DictCodeList.交易方向_申购.getValue().equals(interfRiskRdsDto.getPs())){
//            if (StringUtil.equals(DictCodeListExt2.申购类型_普通申购.getValue(),CommonUtil.findValue(dataMap, "purchType"))
//                    && StringUtil.isNotEmpty(CacheUtil.getSysParam(BussinessConstant.PREDICT_WIN_PERCENT))){
//                BigDecimal percent = new BigDecimal(CacheUtil.getSysParam(BussinessConstant.PREDICT_WIN_PERCENT));
//                interfRiskRdsDto.setAmount(percent.multiply(interfRiskRdsDto.getAmount()).divide(new BigDecimal(100),2, RoundingMode.HALF_UP));
//            }
//        }
    }

    /**
     * 可转债申购确认需要进行实占<br>
     * 中签券面总额大于等于申购券面总额则不参与实占
     *
     * @param dataMap          原始数据
     * @param interfRiskRdsDto 报文数据
     * @return true:不需要实占
     */
    private boolean doBondToStock(Map<String, Object> dataMap, InterfRiskRdsDto interfRiskRdsDto) {
        if (StringUtil.equals(CommonUtil.findValue(dataMap, "windtypeone"),
                DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())
                && CommonUtil.isType(interfRiskRdsDto.getPs(), DictCodeList.交易方向_申购确认)) {
            // 中签券面总额
            Object winlotteryAmtObj = CommonUtil.findValue(dataMap, "winlotteryAmt");
            BigDecimal winlotteryAmt = winlotteryAmtObj instanceof BigDecimal ? (BigDecimal) winlotteryAmtObj : new BigDecimal(
                    winlotteryAmtObj.toString());
            // 申购券面总额
            Object tradeAmtObj = CommonUtil.findValue(dataMap, "tradeAmt");
            BigDecimal tradeAmt = tradeAmtObj instanceof BigDecimal ? (BigDecimal) tradeAmtObj : new BigDecimal(
                    tradeAmtObj.toString());
            // 金额 = 中签券面总额 - 申购券面总额
            BigDecimal amount = winlotteryAmt.subtract(tradeAmt);
            // 中签券面总额大于等于申购券面总额则不参与实占
            if (BigDecimal.ZERO.compareTo(amount) <= 0) {
                return true;
            }
            // 对前面申购的部分进行反向实占
            interfRiskRdsDto.setAmount(amount);
            interfRiskRdsDto.setPreOccupyCode("");
            // 可转债申购确认交易，忽略试算结果，直接更新指标参数值
            interfRiskRdsDto.setIgnoreStatus("Y");
            interfRiskRdsDto.setPs(DictCodeList.交易方向_申购.getValue());
            return false;
        }
        return true;
    }

    /**
     * 保存或提交调用的实占预占逻辑(非画面端)
     *
     * @param t
     * @param v
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> ClientData sendRiskRdsData(T t, V v) throws IOException, InstantiationException {
        logger.info("==========后端交易或委托类转换接口:{} start==========", v.getEventType() + v.getInfCode());
        Map<String, Object> dataMap = JsonUtils.object2Map(t);
        ClientData returnCli = this.sendRiskRdsDataCommon(dataMap, v);
        logger.info("==========后端交易或委托类转换接口:{} end==========", v.getEventType() + v.getInfCode());
        return returnCli;
    }

    // 多组合、多对多指令/交易处理共通 by zhangrui1 start
    public void doListCommon(V v, Map<String, Object> dataMap,
                             Map<String, String> portfolioIds, List<String> tradeIds,
                             List<Map<String, Object>> targetList)
            throws IOException, InstantiationException {
        String counterId = (String) CommonUtil.findValue(dataMap, "counterId");
        String ps = (String) CommonUtil.findValue(dataMap, "ps");
        boolean isInnerTrd = portfolioIds.containsKey(counterId);
        String isInnerTrdStr = isInnerTrd ? BussinessDictCodeListRds.是否_是.getValue() : BussinessDictCodeListRds.是否_否.getValue();
        dataMap.put(PageFieldToInterfFactory.INF_ISINNERTRD, isInnerTrdStr);
        // 债转股、基金转换、转仓等特殊处理
        if (CommonUtil.isType(ps, DictCodeListExt1.交易方向_债转股)
                || CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)
                || CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换赎回申请)
                || (isInnerTrd && CommonUtil.isType(ps, BussinessDictCodeList.交易方向_卖出))) {
            // 设置内部交易对手
            if (isInnerTrd) {
                dataMap.put(PageFieldToInterfFactory.INF_COUNTERID, StringUtil.isEmpty(
                        portfolioIds.get(counterId)) ? counterId : portfolioIds.get(counterId));
            }

            List<Map<String, Object>> singleRiskList = new ArrayList<Map<String, Object>>();
            //判断是否来自场内交易指令
            if (StringUtil.equals(dataMap.get("fromEtrExchange"), DictCodeList.是否_是.getValue())) {
                singleRiskList = prepareEtrExchangeRiskListEntity(dataMap, v);
            } else {
                singleRiskList = prepareRiskListEntity(dataMap, v);
            }
//            List<Map<String, Object>> singleRiskList=prepareRiskListEntity(dataMap, v);
            if (!StringUtil.isEmptyList(singleRiskList)) {
                targetList.addAll(singleRiskList);
            }
            return;
        }
        // 一般处理
        InterfRiskRdsDto interfRiskRdsDto = this.prepareRiskEntity(dataMap, v);
        if (!StringUtil.isNotEmpty(interfRiskRdsDto)) {
            return;
        }
        // 可转债申购 申购时 金额根据预估中签比例（系统参数）计算
        doCalcAmtWithB14(dataMap, interfRiskRdsDto);
        // 暂不处理的交易方向
        if (申购确认_赎回申请.contains(interfRiskRdsDto.getPs())
                // 可转债申购确认需要进行实占
                && (doBondToStock(dataMap, interfRiskRdsDto)
                && !CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托))) {
            return;
        }
        // 设置事件类型
        interfRiskRdsDto.setEventType(v.getEventType());
        // 设置币种
        Object ccy = dataMap.get(InterfUtil.CCY);
        interfRiskRdsDto.setCcy(StringUtil.isNotEmpty(
                ccy) ? ccy.toString() : BussinessDictCodeListRds.币种_人民币_CNY.getValue());
        // 设置金融产品类型
        if (!StringUtil.isNotEmpty(interfRiskRdsDto.getAssetType())) {
            interfRiskRdsDto.setAssetType(v.getAssetType());
        }
        // 设置撤销标志
        if (CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_预占释放批量接口)) {
            interfRiskRdsDto.setRevokeFlg(DictCodeList.是否_是.getValue());
        }
        if (CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_实占释放批量接口)) {
            interfRiskRdsDto.setRevokeFlg(DictCodeList.是否_是.getValue());
            // 内部交易对手时取交易编号作为条件查询反向交易编号
            if (isInnerTrd) {
                String tradeId = interfRiskRdsDto.getEventId();
                tradeIds.add(removeLasStr(tradeId));
            }
        }
        // 设置是否内部交易
        interfRiskRdsDto.setIsInnerTrd(
                isInnerTrd ? BussinessDictCodeListRds.是否_是.getValue() : BussinessDictCodeListRds.是否_否.getValue());

        targetList.add(BeanUtil.object2Map(interfRiskRdsDto));
    }

    public void changeEventId(Map<String, Object> map) {
        // SNSTZJY-1013 add by zhangrui1 start
        boolean isInnerTrd = StringUtil.equals(map.get("isInnerTrd"),
                BussinessDictCodeListRds.是否_是.getValue());
        String tail = isInnerTrd ? (String) map.get(
                PageFieldToInterfFactory.INF_PS) : StringUtil.EMPTY;
        if (isNeedTail((String) map.get(PageFieldToInterfFactory.INF_PREOCCUPYCODE), tail)) {
            map.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE,
                    map.get(PageFieldToInterfFactory.INF_PREOCCUPYCODE) + tail);
        }
        // SNSTZJY-1013 add by zhangrui1 end
        String eventId = (String) map.get(PageFieldToInterfFactory.INF_EVENTID);
        if (isNeedTail(eventId, tail)) {
            map.put(PageFieldToInterfFactory.INF_EVENTID, eventId + tail);
        }
    }

    protected boolean isNeedTail(String str, String tail) {
        return StringUtil.isNotEmpty(str) && StringUtil.isNotEmpty(tail)
                && !StringUtil.endsWith(str, tail);
    }

    protected void doPreOccupy(Map<String, Object> map, String infCode) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        String ps = (String) map.get(PageFieldToInterfFactory.INF_PS);
        String eventId = (String) map.get(PageFieldToInterfFactory.INF_EVENTID);
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占批量接口)) {
            map.put(PageFieldToInterfFactory.INF_OCCUPYDATE,
//                map.get(PageFieldToInterfFactory.INF_EVENTDATE));
                    StringUtil.isNotEmpty(map.get(PageFieldToInterfFactory.INF_MDATE)) ? map.get(PageFieldToInterfFactory.INF_MDATE) : map.get(PageFieldToInterfFactory.INF_EVENTDATE));//update zhangweiqi 2021-1-6 支持跨日指令
        } else if ((CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占批量接口))
                && StringUtil.isNotEmpty(map.get(PageFieldToInterfFactory.INF_PREOCCUPYCODE))) {
            String tail = StringUtil.endsWith(eventId, ps) ? ps : StringUtil.EMPTY;
            if (!StringUtil.endsWith((String) map.get(PageFieldToInterfFactory.INF_PREOCCUPYCODE),
                    ps)) {
                map.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE,
                        map.get(PageFieldToInterfFactory.INF_PREOCCUPYCODE) + tail);
            }
        }
    }

    /**
     * 批量处理
     *
     * @param v        调用接口属性
     * @param tList    内容
     * @param consumer 自定义处理
     * @return 风控计算结果
     * @throws Exception 异常
     */
    public <T> ClientData sendRiskRdsListData(V v, Collection<T> tList,
                                              Consumer<Map<String, Object>> consumer)
            throws IOException, InstantiationException {
        logger.info("==========后端批量交易或批量委托类转换接口:{} {} start==========", v.getInfCode(),
                v.getEventType());
        if (CollectionUtils.isEmpty(tList)) {
            logger.info("==========空的报文体==========");
            return null;
        }
        if (consumer == null) {
            consumer = map -> {
                // nothing to do
            };
        }
        Set<String> counterIdSet = new HashSet<>();
        List<Map<String, Object>> sourcetList = new ArrayList<>();
        // add by zhangrui1 基金转换申请取最新净值 start
        List<String> assetList = new ArrayList<>();
        // add by zhangrui1 基金转换申请取最新净值 end
        // 取出所有的交易对手判断是否内部交易对手
        for (T t : tList) {
            Map<String, Object> dataMap = JsonUtils.object2Map(t);
            // 个别处理
            consumer.accept(dataMap);
            // 交易对手
            String counterId = (String) CommonUtil.findValue(dataMap,
                    PageFieldToInterfFactory.INF_COUNTERID);
            if (!StringUtil.isEmpty(counterId)) {
                counterIdSet.add(counterId);
            }
            sourcetList.add(dataMap);
            // add by zhangrui1 基金转换申请取最新净值 start
            String ps = (String) CommonUtil.findValue(dataMap, "ps");
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)) {
                assetList.add((String) dataMap.get("finprodId"));
            }
            // add by zhangrui1 基金转换申请取最新净值 end
        }

        // 取出内部交易对手
        Map<String, String> portfolioIds = selectInnerCounter(counterIdSet);
        List<Map<String, Object>> targetList = new ArrayList<>();
        List<String> tradeIds = new ArrayList<>();
        // add by zhangrui1 基金转换申请取最新净值 start
        Map<String, BigDecimal> valMap = fundNetService.getValuationMap(assetList);
        // add by zhangrui1 基金转换申请取最新净值 end
        for (Map<String, Object> dataMap : sourcetList) {
            // add by zhangrui1 基金转换申请取最新净值 start
            String ps = (String) CommonUtil.findValue(dataMap, "ps");
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)) {
                dataMap.put("netPrice", valMap.get((String) dataMap.get("finprodId")));
            }
            // add by zhangrui1 基金转换申请取最新净值 end
            // 报文结构处理
            doListCommon(v, dataMap, portfolioIds, tradeIds, targetList);
        }
        // 取得交易的反向交易编号
        Map<String, String> rTradeIdMap = selectRTradeIds(tradeIds);
        // 替换反向交易编号
        if (!CollectionUtils.isEmpty(targetList) && StringUtil.equals(
                BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue(), v.getInfCode())) {
            targetList.forEach(map -> {
                String eventId = (String) map.get(PageFieldToInterfFactory.INF_EVENTID);
                if (rTradeIdMap.containsKey(eventId)) {
                    map.put(PageFieldToInterfFactory.INF_EVENTID, rTradeIdMap.get(eventId));
                }
            });
        }
        // add by zhangrui1 根据资产类型设置开关 start
        if (isRskOpened(v.getAssetType())) {
            return null;
        }
        // add by zhangrui1 根据资产类型设置开关 end
        // 调用风险服务执行计算
        ClientData res = sendListDataCommon(targetList, v.getInfCode());
        logger.info("==========后端批量交易或批量委托类转换接口:{} {} end==========", v.getInfCode(),
                v.getEventType());
        return res;
    }

    /**
     * 批量处理
     *
     * @param v        调用接口属性
     * @param tList    内容
     * @param consumer 自定义处理
     * @return 风控计算结果
     * @throws Exception 异常
     */
    public <T> List<Map<String, Object>> getRiskRdsListData(V v, Collection tList, Consumer<Map<String, Object>> consumer) {
        logger.info("==========后端批量交易或批量委托类转换接口:{} {} start==========", v.getInfCode(),
                v.getEventType());
        if (CollectionUtils.isEmpty(tList)) {
            logger.info("==========空的报文体==========");
            return null;
        }
        if (consumer == null) {
            consumer = map -> {
                // nothing to do
            };
        }
        Set<String> counterIdSet = new HashSet<>();
        List<Map<String, Object>> sourcetList = new ArrayList<>();
        // add by zhangrui1 基金转换申请取最新净值 start
        List<String> assetList = new ArrayList<>();
        // add by zhangrui1 基金转换申请取最新净值 end
        // 取出所有的交易对手判断是否内部交易对手
        for (Object t : tList) {
            Map<String, Object> dataMap = JsonUtils.object2Map(t);
            // 个别处理
            consumer.accept(dataMap);
            // 交易对手
            String counterId = (String) CommonUtil.findValue(dataMap,
                    PageFieldToInterfFactory.INF_COUNTERID);
            if (!StringUtil.isEmpty(counterId)) {
                counterIdSet.add(counterId);
            }
            sourcetList.add(dataMap);
            // add by zhangrui1 基金转换申请取最新净值 start
            String ps = (String) CommonUtil.findValue(dataMap, "ps");
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)) {
                assetList.add((String) dataMap.get("finprodId"));
            }
            // add by zhangrui1 基金转换申请取最新净值 end
        }

        // 取出内部交易对手
        Map<String, String> portfolioIds = selectInnerCounter(counterIdSet);
        List<Map<String, Object>> targetList = new ArrayList<>();
        List<String> tradeIds = new ArrayList<>();
        // add by zhangrui1 基金转换申请取最新净值 start
        Map<String, BigDecimal> valMap = fundNetService.getValuationMap(assetList);
        // add by zhangrui1 基金转换申请取最新净值 end
        for (Map<String, Object> dataMap : sourcetList) {
            // add by zhangrui1 基金转换申请取最新净值 start
            String ps = (String) CommonUtil.findValue(dataMap, "ps");
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)) {
                dataMap.put("netPrice", valMap.get((String) dataMap.get("finprodId")));
            }
            // add by zhangrui1 基金转换申请取最新净值 end
            // 报文结构处理

            try {
                doListCommon(v, dataMap, portfolioIds, tradeIds, targetList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 取得交易的反向交易编号
        Map<String, String> rTradeIdMap = selectRTradeIds(tradeIds);
        // 替换反向交易编号
        if (!CollectionUtils.isEmpty(targetList) && StringUtil.equals(
                BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue(), v.getInfCode())) {
            targetList.forEach(map -> {
                String eventId = (String) map.get(PageFieldToInterfFactory.INF_EVENTID);
                if (rTradeIdMap.containsKey(eventId)) {
                    map.put(PageFieldToInterfFactory.INF_EVENTID, rTradeIdMap.get(eventId));
                }
            });
        }
        // add by zhangrui1 根据资产类型设置开关 start
        if (isRskOpened(v.getAssetType())) {
            return null;
        }
        // add by zhangrui1 根据资产类型设置开关 end
        // 调用风险服务执行计算
        //ClientData res = sendListDataCommon(targetList, v.getInfCode());
        logger.info("==========后端批量交易或批量委托类转换接口:{} {} end==========", v.getInfCode(), v.getEventType());
        return targetList;
    }
    // 多组合、多对多指令/交易处理共通 by zhangrui1 end

    /**
     * 某些场景不走AOP单调实占或实占释放，不计算指标
     *
     * @param v
     * @param eventType 事件类型
     * @param infCode   事件代码
     * @param assetType 系统资产大类
     * @param iInfRds   执行的方法
     * @return
     * @throws Exception
     */
    public void checkRiskRdsWithoutCalc(V v, String eventType, String infCode, String assetType, IInfRds<V> iInfRds) throws BusinessException {
        try {
            ClientData returnCli = execRiskRds(v, eventType, infCode, assetType, iInfRds);
            if (StringUtil.isNotEmpty(returnCli) && StringUtil.isNotEmpty(returnCli.getHead()) && StringUtil.isNotEmpty(returnCli.getHead().get(BussinessConstantRds.std400msgt))) {
                if (StringUtil.equals(returnCli.getHead().get(BussinessConstantRds.std400msgt).toString(), BussinessConstantRds.E) && StringUtil.isNotEmpty(returnCli.getHead().get(BussinessConstantRds.stdrtninfo))) {
                    throw new BusinessException(BussinessConstantRds.MSG + returnCli.getHead().get(BussinessConstantRds.stdrtninfo).toString());
                }
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 检查风险是否超限(投资交易端点提交调用)
     *
     * @param v
     * @param eventType 事件类型
     * @param infCode   事件代码
     * @param assetType 系统资产大类
     * @param iInfRds   执行的方法
     * @return
     * @throws Exception
     */
    public AjaxResult checkRiskRds(V v, String eventType, String infCode, String assetType, IInfRds<V> iInfRds) throws Exception {
        ClientData returnCli = execRiskRds(v, eventType, infCode, assetType, iInfRds);
        if (StringUtil.isNotEmpty(returnCli)) {
            if (StringUtil.isNotEmpty(returnCli.getHead())) {
                if (StringUtil.equals(returnCli.getHead().get(InterfUtil.std400msgt), InterfUtil.E)) {
                    return getAjaxResult(returnCli, returnCli.getHead().get(InterfUtil.stdrtninfo).toString(), MessageType.错误消息);
                } else {
                    String statusRisk = StringUtil.isNotEmpty(returnCli.getBody()) ? StringUtil.isNotEmpty(returnCli.getBody().get("rskStatus")) ? returnCli.getBody().get("rskStatus").toString() : null : null;

                    //增加软控硬控开关 add by jqq 20201216 start
                    String institutionCode = "";
                    if (MultilevelDataAuthorityUtil.isEnabled()) {
                        institutionCode = MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
                    }
                    String isSoft = CacheUtil.getSpecialSysParam("IS_RISKMATH_SOFTCONTROL", institutionCode);
                    if (StringUtil.equals(statusRisk, BussinessDictCodeListRds.指标状态_禁止.getValue())) {
                        if ("02".equals(isSoft)) {//硬控
                            return getAjaxResult(returnCli, InterfUtil.FORBID_MES, MessageType.错误消息);
                        }
                        return null;
                    }
                    //增加软控硬控开关 add by jqq 20201216 end
//                    if(StringUtil.equals(statusRisk, BussinessDictCodeListRds.指标状态_禁止.getValue())){
//                        return getAjaxResult(returnCli,InterfUtil.FORBID_MES,MessageType.错误消息);
//                    }
                }
            }
        }
        return null;
    }

    /**
     * 产品端交易，风险占用参数构建(接口)
     *
     * @param v
     * @param eventType
     * @param finprodId
     * @param infCode
     * @param iInfRds
     * @throws Exception
     */
    public void occupyProductTrade(V v, String eventType, String finprodId, String infCode, IInfRds<V> iInfRds) throws Exception {
        //在redis里查询资产类型
        String assetType = StringUtil.EMPTY;
        if (StringUtil.isNotEmpty(finprodId)) {
            Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Collections.singletonList(finprodId));
            if (StringUtil.isNotEmpty(mapAsset) && StringUtil.isNotEmpty(mapAsset.values())) {
                for (IAssetInfo iAst : mapAsset.values()) {
                    assetType = StringUtil.isNotEmpty(iAst) ? iAst.getAssetType() : assetType;
                }
            }
        }
        iInfRds.receive(execBase(v, eventType, infCode, assetType));
    }

    /**
     * 检查风险是否超限(多组合)
     *
     * @param t
     * @param v
     * @param eventType 事件类型
     * @param infCode   事件代码
     * @param assetType 系统资产大类
     * @param iInfRds   执行的方法
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Map<String, Object> baseBatchRiskRds(T t, V v, String eventType, String infCode, String assetType, IInfBatchRds<T, V> iInfRds) throws IOException, InstantiationException {
        ClientData returnCli = execBatchRiskRds(t, v, eventType, infCode, assetType, iInfRds);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put(InterfUtil.BACK_PAGE_FLAG, DictCodeList.是否_否.getValue());
        retMap.put(InterfUtil.INF_REMAIN_FLAG, DictCodeList.是否_否.getValue());
        if (StringUtil.isNotEmpty(returnCli)) {
            retMap.put(InterfUtil.RISK_RDS_LIST, returnCli);
            if (StringUtil.isNotEmpty(returnCli.getHead())) {
                //针对异常或超违规线情况进行分别判断
                if (StringUtil.equals(returnCli.getHead().get(InterfUtil.std400msgt), InterfUtil.E)) {
                    //针对异常情况
                    retMap.put(InterfUtil.BACK_PAGE_FLAG, DictCodeList.是否_是.getValue());
                    retMap.put(InterfUtil.INF_REMAIN_FLAG, DictCodeList.是否_是.getValue());
                    retMap.put(InterfUtil.RISK_BACK_MES, returnCli.getHead().get(InterfUtil.stdrtninfo).toString());
                } else {
                    String statusRisk = StringUtil.isNotEmpty(returnCli.getBody()) ? StringUtil.isNotEmpty(returnCli.getBody().get("rskStatus")) ? returnCli.getBody().get("rskStatus").toString() : null : null;
                    if (StringUtil.equals(statusRisk, BussinessDictCodeListRds.指标状态_禁止.getValue())) {
                        //针对超违规线情况
                        retMap.put(InterfUtil.BACK_PAGE_FLAG, DictCodeList.是否_是.getValue());
                        //增加软控硬控开关 add by jqq 20201216 start
                        String institutionCode = "";
                        if (MultilevelDataAuthorityUtil.isEnabled()) {
                            institutionCode = MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName());
                        }
                        String isSoft = CacheUtil.getSpecialSysParam("IS_RISKMATH_SOFTCONTROL", institutionCode);
                        if (BaseDictCodeList.是否_否.getValue().equals(isSoft)) {//硬控
                            retMap.put(InterfUtil.RISK_BACK_MES, InterfUtil.FORBID_MES);
                        }
                    }
                }
            }
        }
        return retMap;
    }

    /**
     * 检查风险是否超限(多组合)
     *
     * @param list
     * @param v
     * @param eventType  事件类型
     * @param infCode    事件代码
     * @param infRevCode 释放事件代码
     * @param assetType  系统资产大类
     * @param iInfRds    执行的方法
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> AjaxResult checkBatchRiskRds(List<T> list, V v, String eventType, String infCode, String infRevCode, String assetType, IInfBatchRds<T, V> iInfRds) throws IOException, InstantiationException {
        List<T> revokeList = new ArrayList<>();
        AjaxResult ar = new AjaxResult();
        boolean needBackFlag = false;
        List<Object> listRiskResult = new ArrayList<>();
        Map<String, Object> arAllMap = new HashMap<>();
        for (T t : list) {
            //调用风险实占或预占接口
            Map<String, Object> arMap = baseBatchRiskRds(t, v, eventType, infCode, assetType, iInfRds);
            if (StringUtil.isNotEmpty(arMap)) {
                //有出现不通过或预占实占时的场景，则设置返回画面标识
                if (StringUtil.equals(arMap.get(InterfUtil.BACK_PAGE_FLAG).toString(), DictCodeList.是否_是.getValue())) {
                    needBackFlag = true;
                    arAllMap.put(InterfUtil.BACK_PAGE_FLAG, DictCodeList.是否_是.getValue());
                    arAllMap.put(InterfUtil.INF_REMAIN_FLAG, DictCodeList.是否_否.getValue());
                    arAllMap.put(InterfUtil.RISK_BACK_MES, arMap.get(InterfUtil.RISK_BACK_MES));
                    //有出现不通过的场景，则设置返回画面标识
                    if (StringUtil.equals(arMap.get(InterfUtil.INF_REMAIN_FLAG).toString(), DictCodeList.是否_是.getValue())) {
                        arAllMap.put(InterfUtil.INF_REMAIN_FLAG, DictCodeList.是否_是.getValue());
                        break;
                    }
                }
                listRiskResult.add(arMap.get(InterfUtil.RISK_RDS_LIST));
            }
            revokeList.add(t);
        }
        //若出现超违规线或异常的情况所有已调用实占或预占的组合再调用释放接口
        if (needBackFlag) {
            for (T t : revokeList) {
                execBatchRiskRds(t, v, eventType, infRevCode, assetType, iInfRds);
            }
            arAllMap.put(InterfUtil.RISK_RDS_LIST, listRiskResult);
            return getAjaxResult(arAllMap, StringUtil.isNotEmpty(arAllMap.get(InterfUtil.RISK_BACK_MES)) ? arAllMap.get(InterfUtil.RISK_BACK_MES).toString() : null, MessageType.错误消息);
        }
        return null;
    }

    /**
     * 发送微服务接口
     *
     * @param bodyT
     * @return
     */
    public <T extends IRiskRds> ClientData sendDataCommon(T bodyT) {
        // add by zhangrui1 根据资产类型设置开关 start
        if (isRskOpened(bodyT.getAssetType())) {
            return null;
        }
        // add by zhangrui1 根据资产类型设置开关 end
        Map<String, Object> header = createHead(bodyT);
        Map<String, Object> body = JsonUtils.object2Map(bodyT);
        // 预占功能 add by zhangrui1 start
        doPreOccupy(body, bodyT.getInfCode());
        // 预占功能 add by zhangrui1 end
        ClientData data = new ClientData();
        data.setHead(header);
        data.setBody(body);
        logger.info("==========调用微服务端风险接口 start==========");
        ClientData result = ClientUtil.sendRequestInf(ServiceApplication.RISK_INF_SERVICE_NAME + InterfUtil.RISK_INF_URL, data.convertToMap(), restTemplate);
        logger.info("==========调用微服务端风险接口 end==========");
        return result;
    }


    // add by zhangrui1 调用批量接口逻辑 start
    private ClientData sendRequest(ClientData data) {
        return ClientUtil.sendRequestInf(ServiceApplication.RISK_INF_SERVICE_NAME + InterfUtil.RISK_INF_URL,
                data.convertToMap(), restTemplate);
    }

    @SuppressWarnings("unchecked")
    public ClientData sendListDataCommon(List<Map<String, Object>> list) {
        if (StringUtil.isEmptyList(list)) {
            logger.info("==========空的报文体==========");
            return null;
        }
        // 修改eventID，避免重复  交易编号+交易方向
        list.forEach(map -> {
            String eventId = (String) map.get(PageFieldToInterfFactory.INF_EVENTID);
            if (StringUtil.isEmpty(eventId)) return;
            String ps = (String) map.get(PageFieldToInterfFactory.INF_PS);
            map.put(PageFieldToInterfFactory.INF_EVENTID, eventId + ps);
        });
        //wangyang1 风险二级法人改造 start
        for (Map<String, Object> map2 : list) {
            map2.put("instCode", MultilevelDataAuthorityUtil.getOrganizationCode(LoginUserUtil.getUserName()));
        }
        //wangyang1 风险二级法人改造 end
        Map<String, Object> header = createHead(BeanUtil.mapToObject(list.get(0), InterfRiskRdsDto.class));
        Map<String, Object> body = new HashMap<>();
        if (CommonUtil.isType((String) header.get(InterfUtil.stdprocode), BussinessDictCodeListRds.接口系统ID_风险试算批量接口)) {
            body.put(PageFieldToInterfFactory.INF_RSK_CALC_LIST, list);
        } else {
            body.put(PageFieldToInterfFactory.INF_RSK_LIST, list);
        }
        ClientData data = new ClientData();
        data.setHead(header);
        data.setBody(body);
        logger.info("==========调用微服务端风险批量接口 start==========");
        ClientData result = sendRequest(data);
        logger.info("==========调用微服务端风险批量接口 end==========");
        return result;
    }

    @SuppressWarnings("unchecked")
    public ClientData sendListDataCommon(List<Map<String, Object>> list, String infCode) {
        if (StringUtil.isEmptyList(list)) {
            logger.info("==========空的报文体==========");
            return null;
        }
        // 预占功能 add by zhangrui1 start
        list.forEach(map -> doPreOccupy(map, infCode));
        // 预占功能 add by zhangrui1 end
        InterfRiskRdsDto riskRdsDto = new InterfRiskRdsDto();
        riskRdsDto.setInfCode(infCode);
        // 构建报文头
        Map<String, Object> header = createHead(riskRdsDto);
        Map<String, Object> body = new HashMap<>();
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_风险试算批量接口)) {
            body.put(PageFieldToInterfFactory.INF_RSK_CALC_LIST, list);
        } else {
            body.put(PageFieldToInterfFactory.INF_RSK_LIST, list);
        }
        // add by zhangrui1 正反同步委托/交易特殊处理 start
        for (Map<String, Object> map : list) {
            Object isSync = map.get(IS_SYNC);
            if (StringUtil.isNotEmpty(isSync)) {
                body.put(IS_SYNC, isSync);
                break;
            }
        }
        for (Map<String, Object> map : list) {
            Object isConv = map.get(IS_CONV);
            if (StringUtil.isNotEmpty(isConv)) {
                body.put(IS_CONV, isConv);
                break;
            }
        }
        // add by zhangrui1 正反同步委托/交易特殊处理 end
        ClientData data = new ClientData();
        data.setHead(header);
        data.setBody(body);
        logger.info("==========调用微服务端风险批量接口 start==========");
        ClientData result = sendRequest(data);
        logger.info("==========调用微服务端风险批量接口 end==========");
        return result;
    }

    /**
     * 场内交易指令批量接口的实体转换
     *
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    public List<Map<String, Object>> prepareEtrExchangeRiskListEntity(Map<String, Object> dataMap, V v) {
        List<Map<String, Object>> result = new ArrayList<>();
        String ps = (String) dataMap.get(InterfUtil.ps);
        String isMulti = (String) dataMap.get("isMulti");
        if (StringUtil.equals(isMulti, DictCodeList.是否_是.getValue())) {
            List<Map<String, Object>> multiPtlList = (List) dataMap.get("tradeMultiPtl001Dtos");
            if (!StringUtil.isEmptyList(multiPtlList)) {
                Map<String, Object> itemDataMap = new HashMap<>();
                itemDataMap.putAll(dataMap);
                for (Map map : multiPtlList) {
                    itemDataMap.putAll(map);
                    InterfRiskRdsDto interfRiskRdsDto = entrustExchangeEntityDev(itemDataMap, v);
                    interfRiskRdsDto.setEventType(v.getEventType());
                    Map<String, Object> infMap = JsonUtils.object2Map(interfRiskRdsDto);
                    result.add(infMap);
                }

            }
        } else {
            if (CommonUtil.isType(ps, DictCodeListExt1.交易方向_债转股)) {
                Map<String, Object> infMap = new HashMap<>();
                dataMap.put("tradeDate", DateUtil.format(DateUtil.platDateFromCache()));
                dataMap.put("conNum", dataMap.get("entrustNum"));
                dataMap.put("shareAmt", dataMap.get("entrustAmt"));
                dataMap.put("busiCode", dataMap.get("entrustId"));
//            dataMap.put("finprodMarketId", dataMap.get("secId"));
                //债券
                dataMap.put(InterfUtil.finprodType2, BussinessDictCodeList.平安资产类型_债券.getValue());
                PageFieldToInterfFactory.mapping(dataMap, infMap);
                infMap.put(InterfUtil.ps, BussinessDictCodeList.交易方向_卖出.getValue());
                doCommon(infMap, v);

                // add by zhangrui1 债转股拼接事件编号 start
                if (isNeedTail((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID),
                        (String) infMap.get(PageFieldToInterfFactory.INF_PS))) {
                    infMap.put(PageFieldToInterfFactory.INF_EVENTID,
                            (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                                    PageFieldToInterfFactory.INF_PS));
                }
                // add by zhangrui1 债转股拼接事件编号 end
                // add by zhangrui1 正反同步交易特殊处理 start
                String groupId = String.valueOf(System.nanoTime());
                infMap.put(GROUP_ID, groupId);
                infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
                // add by zhangrui1 正反同步交易特殊处理 end
                if (StringUtil.isNotEmpty(infMap.get(PageFieldToInterfFactory.INF_AMOUNT))) {
                    String amountStr = null;
                    if (infMap.get(PageFieldToInterfFactory.INF_AMOUNT) instanceof BigDecimal) {
                        amountStr = infMap.get(PageFieldToInterfFactory.INF_AMOUNT).toString();
                    } else {
                        amountStr = (String) infMap.get(PageFieldToInterfFactory.INF_AMOUNT);
                    }
                    infMap.put(PageFieldToInterfFactory.INF_TRADENUM, ArithmeticUtil.divide(new BigDecimal(amountStr), new BigDecimal(100), 2));
                }
                result.add(infMap);
                //股票
                infMap = new HashMap<>();
                FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey((String) dataMap.get("finprodId"));
                if (StringUtil.isNotEmpty(finProductConvertible)) {
                    dataMap.put("shareFinprodId", finProductConvertible.getConFinprodId());
                }

                dataMap.put(InterfUtil.finprodType2, BussinessDictCodeList.平安资产类型_股票.getValue());
                PageFieldToInterfFactory.mapping(dataMap, infMap);
                infMap.put(InterfUtil.ps, BussinessDictCodeList.交易方向_买入.getValue());
                doCommon(infMap, v);
                // 金额=转股数*转股价格
                BigDecimal num = (BigDecimal) convert(dataMap.get("entrustNum"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
                BigDecimal pri = (BigDecimal) convert(dataMap.get("entrustPrice"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
                if (num != null && pri != null) {
                    infMap.put(PageFieldToInterfFactory.INF_AMOUNT, num.multiply(pri));
                }
                // add by zhangrui1 正反同步交易特殊处理 start
                infMap.put(GROUP_ID, groupId);
                infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
                // add by zhangrui1 正反同步交易特殊处理 end
                if (isNeedTail((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID),
                        (String) infMap.get(PageFieldToInterfFactory.INF_PS))) {
                    infMap.put(PageFieldToInterfFactory.INF_EVENTID,
                            (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                                    PageFieldToInterfFactory.INF_PS));
                }
                result.add(infMap);
            }
            //质押式回购
            if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), v.getAssetType())) {
                String assetType2 = String.valueOf(CommonUtil.findValue(dataMap, "assetType2"));
                if (StringUtil.equals(assetType2, DictCodeList.回购类型_质押式协议回购.getValue())
                        && StringUtil.equals(CommonUtil.findValue(dataMap, "ps"), DictCodeList.交易方向_到期续做.getValue())) {
                    InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
                    infRiskRdsDto.setInfCode(v.getInfCode());
                    infRiskRdsDto.setTradePlace(String.valueOf(CommonUtil.findValue(dataMap, "tradeMarket")));
                    infRiskRdsDto.setEventId(CommonUtil.findValue(dataMap, "entrustId") + DictCodeList.交易方向_到期.getValue());
                    infRiskRdsDto.setEventDate(DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
                    //设置母组合代码和子组合代码
                    String portfolioId = String.valueOf(CommonUtil.findValue(dataMap, "portfolioId"));
                    infRiskRdsDto.setPortfolioCode(AllTradeCommonUtil.exchangePortfolio(portfolioId));
                    // 原回购到期
                    String preTradeId = "";
                    if (CommonUtil.findValue(dataMap, "tradeId") != null) {
                        preTradeId = String.valueOf(CommonUtil.findValue(dataMap, "tradeId"));
                    } else {
                        preTradeId = String.valueOf(CommonUtil.findValue(dataMap, "intiTradeId"));
                    }
                    FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
                    finTradeProductKey.setBranch(0L);
                    finTradeProductKey.setFinprodId(preTradeId);
                    FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
                    // add by etliu 20220111 start
                    // GZYHTZJY-1081:【场内对账清结算1231】深交所协议回购，到期续作指令 提交后，缺少T07到期的那笔预占【解决日期：2022-01-11】
                    if (finTradeProduct == null) {
                        if (CommonUtil.findValue(dataMap, "intiTradeId") != null) {
                            finTradeProductKey.setFinprodId(String.valueOf(CommonUtil.findValue(dataMap, "intiTradeId")));
                            finTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
                        }
                    }
                    // add by etliu 20220111 end

                    // add by zhangrui1 正反同步交易特殊处理 start
                    String groupId = String.valueOf(System.nanoTime());
                    // add by zhangrui1 正反同步交易特殊处理 end
                    if (finTradeProduct != null) {
                        infRiskRdsDto.setPs(DictCodeList.交易方向_到期.getValue());
                        infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
                        infRiskRdsDto.setAssetType2(String.valueOf(CommonUtil.findValue(dataMap, "busiPs")));
                        infRiskRdsDto.setAmount(finTradeProduct.getmTradeAmt());
                        infRiskRdsDto.setTradeNum(finTradeProduct.getPrin());
                        infRiskRdsDto.setAssetRate(finTradeProduct.getIntRate());
                        infRiskRdsDto.setAssetCode(null);
                        infRiskRdsDto.setMarketCode(null);
                        if (StringUtil.equals(infRiskRdsDto.getTradePlace(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                            infRiskRdsDto.setCalendarCode("04");
                        } else {
                            infRiskRdsDto.setCalendarCode("05");
                        }
                        infRiskRdsDto.setOneDay(finTradeProduct.getTerm());
                        infRiskRdsDto.setMdate(finTradeProduct.getMdate());
                        Map<String, Object> infMap = JsonUtils.object2Map(infRiskRdsDto);
                        doCommon(infMap, v);
                        infMap.put(GROUP_ID, groupId);
                        infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
                        if (StringUtil.equals(infRiskRdsDto.getTradePlace(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                            infMap.put(IS_CONV, BaseDictCodeList.是否_是.getValue());
                        }
                        result.add(infMap);
                    }
                    // 新首期
                    infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                    infRiskRdsDto.setEventId(CommonUtil.findValue(dataMap, "entrustId") + DictCodeList.交易方向_买入.getValue());
                    infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
                    infRiskRdsDto.setAssetType2(String.valueOf(CommonUtil.findValue(dataMap, "busiPs")));
                    infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "tradeAmt", BigDecimal.ZERO));
                    infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "tradeAmt", BigDecimal.ZERO));
                    infRiskRdsDto.setAssetRate(ArithmeticUtil.divide(CommonUtil.getBigDecimal(dataMap, "yield", BigDecimal.ZERO), new BigDecimal(100), 4));
                    infRiskRdsDto.setAssetCode(null);
                    infRiskRdsDto.setMarketCode(null);
                    if (StringUtil.equals(infRiskRdsDto.getTradePlace(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                        infRiskRdsDto.setCalendarCode("04");
                    } else {
                        infRiskRdsDto.setCalendarCode("05");
                    }
                    infRiskRdsDto.setOneDay(CommonUtil.getBigDecimal(dataMap, "term", BigDecimal.ZERO).longValue());
                    if (CommonUtil.findValue(dataMap, "mdate") instanceof String) {
                        try {
                            infRiskRdsDto.setMdate(DateUtil.addDay(DateUtil.parseDate(String.valueOf(CommonUtil.findValue(dataMap, "mdate")), DateUtil.DEFAULT_DATE_FORMAT_EN), CommonUtil.getBigDecimal(dataMap, "term", BigDecimal.ZERO).intValue()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (CommonUtil.findValue(dataMap, "mdate") instanceof Long) {
                        infRiskRdsDto.setMdate(new Date((Long) CommonUtil.findValue(dataMap, "mdate")));
                    }

                    Map<String, Object> infMap = JsonUtils.object2Map(infRiskRdsDto);
                    doCommon(infMap, v);
                    infMap.put(GROUP_ID, groupId);
                    infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
                    if (StringUtil.equals(infRiskRdsDto.getTradePlace(), DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
                        infMap.put(IS_CONV, BaseDictCodeList.是否_是.getValue());
                    }

                    result.add(infMap);
                }
            }
        }

        return result;
    }

    public List<Map<String, Object>> prepareRiskListEntity(Map<String, Object> dataMap, V v) throws IOException, InstantiationException {
        List<Map<String, Object>> result = new ArrayList<>();
        String ps = (String) dataMap.get(InterfUtil.ps);
        // ZGZGST-2517:质押式回购指令组合系列每个组合需要拆分为独立指令 fuyongsheng 20210917 add start
        if (StringUtil.equals(dataMap.get("assetType1"), DictCodeList.回购类型_质押式回购.getValue())) {
            //正回购和逆回购存在多组合，每个组合需要拆分为独立指令
            if (StringUtil.isNotEmpty(dataMap.get("etrInvestRepoInfoDtos"))) {
                List<Map> etrInvestRepoInfoDtos = (List<Map>) dataMap.get("etrInvestRepoInfoDtos");
                for (Map<String, Object> map : etrInvestRepoInfoDtos) {
                    String eventDate = null;
                    if (StringUtil.isNotEmpty(dataMap.get("entrustDate"))) {
                        Object entrustDate = dataMap.get("entrustDate");
                        String type = entrustDate.getClass().toString();
                        if ("Long".equals(type.substring(type.lastIndexOf('.') + 1, type.length()))) {
                            eventDate = DateUtil.format(new Date((long) entrustDate));
                        } else {
                            eventDate = DateUtil.format(DateUtil.parse(entrustDate.toString()));
                        }
                    }
                    //获取接口类型
                    String infCode = v.getInfCode();
                    InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
                    infRiskRdsDto.setEventType(v.getEventType());
                    infRiskRdsDto.setCcy((String) map.get("ccy"));
                    infRiskRdsDto.setInfCode(infCode);
                    infRiskRdsDto.setEventId((String) map.get("entrustId"));
                    infRiskRdsDto.setPreOccupyCode((String) map.get("entrustId"));
                    infRiskRdsDto.setEventDate(eventDate);
                    infRiskRdsDto.setPortfolioCode((String) map.get("portfolioId"));
                    infRiskRdsDto.setAssetCode((String) map.get("finprodId"));
                    infRiskRdsDto.setAmount(BigDecimal.valueOf(Double.parseDouble((String) map.get("entrustAmt"))));
                    infRiskRdsDto.setDeliveryDate(DateUtil.parse(eventDate));
                    infRiskRdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    infRiskRdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    infRiskRdsDto.setTradePlace((String) dataMap.get("tradeMarket"));
                    infRiskRdsDto.setTradeMarketMode((String) dataMap.get("tradeMarketMode"));
                    infRiskRdsDto.setCalendarCode((String) dataMap.get("calendarId"));
                    infRiskRdsDto.setMdate(new Date((Long) CommonUtil.findValue(dataMap, "mdate")));
                    long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                            DateUtil.parse(infRiskRdsDto.getMdate()));
                    infRiskRdsDto.setOneDay(oneDay);
                    infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                    infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
                    infRiskRdsDto.setAssetType2(ps);
                    infRiskRdsDto.setAssetRate(StringUtil.isNotEmpty(dataMap.get("price")) ? dataMap.get("price") instanceof BigDecimal ? ArithmeticUtil.divide((BigDecimal) dataMap.get("price"), new BigDecimal(100), 4) : ArithmeticUtil.divide(new BigDecimal(dataMap.get("price").toString()), new BigDecimal(100), 4) : null);
                    infRiskRdsDto.setCalendarCode((String) dataMap.get("calendarId"));
                    infRiskRdsDto.setDeadline(StringUtil.isNotEmpty(dataMap.get("repoTerm")) ? new BigDecimal(dataMap.get("repoTerm").toString()) : null);
                    if (CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_预占释放批量接口)) {
                        infRiskRdsDto.setRevokeFlg(BussinessDictCodeList.是否_是.getValue());
                    }
                    result.add(BeanUtil.object2Map(infRiskRdsDto));
                }
            }
        }
        // ZGZGST-2517:质押式回购指令组合系列每个组合需要拆分为独立指令 fuyongsheng 20210917 add end
        else if (CommonUtil.isType(ps, DictCodeListExt1.交易方向_债转股)) {
            Map<String, Object> infMap = new HashMap<>();
            //委托字段转化
            if (StringUtil.equals(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托.getValue())) {
                dataMap.put("tradeDate", DateUtil.format(DateUtil.platDateFromCache()));
                dataMap.put("conNum", dataMap.get("expConNum"));
                dataMap.put("shareAmt", dataMap.get("stocketrPriceTotal"));
                dataMap.put("busiCode", dataMap.get("entrustId"));
                dataMap.put("finprodMarketId", dataMap.get("shareId"));
            }
            // 预占功能 add by zhangrui1 start
            else {
                String entrustId = (String) CommonUtil.findValue(dataMap, "entrustId");
                if (StringUtil.isNotEmpty(entrustId)) {
                    infMap.put(PageFieldToInterfFactory.INF_ENTRUSTCODE, entrustId);
                    infMap.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE,
                            entrustId + BussinessDictCodeList.交易方向_卖出.getValue());
                }
                if (dataMap.get("tradeDate") instanceof Long) {
                    dataMap.put("tradeDate", DateUtil.format(new Date((long) dataMap.get("tradeDate"))));
                }

                if (dataMap.get("settleDate") instanceof Long) {
                    dataMap.put("settleDate", DateUtil.format(new Date((long) dataMap.get("settleDate"))));
                }
            }
            // 预占功能 add by zhangrui1 end
            // 债券
            dataMap.put(InterfUtil.finprodType2, BussinessDictCodeList.平安资产类型_债券.getValue());
            PageFieldToInterfFactory.mapping(dataMap, infMap);
            infMap.put(InterfUtil.ps, BussinessDictCodeList.交易方向_卖出.getValue());
            doCommon(infMap, v);
            // add by zhangrui1 债转股拼接事件编号 start
            if (isNeedTail((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID),
                    (String) infMap.get(PageFieldToInterfFactory.INF_PS))) {
                infMap.put(PageFieldToInterfFactory.INF_EVENTID,
                        (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                                PageFieldToInterfFactory.INF_PS));
            }
            // add by zhangrui1 债转股拼接事件编号 end
            // add by zhangrui1 正反同步交易特殊处理 start
            String groupId = String.valueOf(System.nanoTime());
            infMap.put(GROUP_ID, groupId);
            infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
            BigDecimal num = (BigDecimal) convert(dataMap.get("conNum"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
            BigDecimal pri = (BigDecimal) convert(dataMap.get("conPrice"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
            if (num != null && pri != null) {
                infMap.put(PageFieldToInterfFactory.INF_AMOUNT, num.multiply(pri));
            }
            // add by zhangrui1 正反同步交易特殊处理 end
            if (StringUtil.isNotEmpty(infMap.get(PageFieldToInterfFactory.INF_AMOUNT))) {
                infMap.put(PageFieldToInterfFactory.INF_TRADENUM, ArithmeticUtil.divide(new BigDecimal(infMap.get(PageFieldToInterfFactory.INF_AMOUNT).toString()), new BigDecimal(100), 2));
            }
            result.add(infMap);
            // 股票
            infMap = new HashMap<>();
            FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey((String) dataMap.get("finprodId"));
            if (StringUtil.isNotEmpty(finProductConvertible)) {
                dataMap.put("shareFinprodId", finProductConvertible.getConFinprodId());
            }
            dataMap.put(InterfUtil.finprodType2, BussinessDictCodeList.平安资产类型_股票.getValue());
            PageFieldToInterfFactory.mapping(dataMap, infMap);
            infMap.put(InterfUtil.ps, BussinessDictCodeList.交易方向_买入.getValue());
            doCommon(infMap, v);
            // add by zhangrui1 债转股拼接事件编号 start
            if (isNeedTail((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID),
                    (String) infMap.get(PageFieldToInterfFactory.INF_PS))) {
                infMap.put(PageFieldToInterfFactory.INF_EVENTID,
                        (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                                PageFieldToInterfFactory.INF_PS));
            }
            // add by zhangrui1 债转股拼接事件编号 end
            // 金额=转股数*转股价格
            if (num != null && pri != null) {
                infMap.put(PageFieldToInterfFactory.INF_AMOUNT, num.multiply(pri));
            }
            // add by zhangrui1 正反同步交易特殊处理 start
            infMap.put(GROUP_ID, groupId);
            infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
            // add by zhangrui1 正反同步交易特殊处理 end
            String entrustId = (String) CommonUtil.findValue(dataMap, "entrustId");
            if (!StringUtil.isEmpty(entrustId)
                    && !CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托)) {
                infMap.put(PageFieldToInterfFactory.INF_ENTRUSTCODE, entrustId);
                infMap.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE,
                        entrustId + BussinessDictCodeList.交易方向_买入.getValue());
            }
            result.add(infMap);
        } else if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请) || CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换赎回申请)) {
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换赎回申请)) {
                dataMap.put(InterfUtil.ps, DictCodeListExt2.交易方向_转换申请.getValue());
                if (StringUtil.isNotEmpty(dataMap.get("totleTradeId"))) {
                    dataMap.put("tradeId", dataMap.get("totleTradeId"));
                }
            }
            // 基金转入
            Map<String, Object> infMap = new HashMap<>();
            dataMap.put(InterfUtil.finprodType2, BussinessDictCodeList.平安资产类型_基金.getValue());
            dataMap.put(InterfUtil.ps + 2, BussinessDictCodeList.交易方向_卖出.getValue());
            dataMap.put("busiCode", dataMap.get("tradeId"));
            //委托字段转化
            if (StringUtil.equals(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托.getValue())) {
                dataMap.put("tradeDate", DateUtil.format(DateUtil.platDateFromCache()));
                dataMap.put("changeToFinprodName", dataMap.get("changeToFinprodMarketId"));

                dataMap.put("tradeAmt", dataMap.get("entrustAmt"));
                dataMap.put("shareAmt", dataMap.get("entrustNum"));
                dataMap.put("busiCode", dataMap.get("entrustId"));
            }
            // 预占功能 add by zhangrui1 start
            else {
                String entrustId = (String) CommonUtil.findValue(dataMap, "entrustId");
                if (StringUtil.isNotEmpty(entrustId)) {
                    infMap.put(PageFieldToInterfFactory.INF_ENTRUSTCODE, entrustId);
                    infMap.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE,
                            entrustId + BussinessDictCodeList.交易方向_卖出.getValue());
                }
            }
            // 预占功能 add by zhangrui1 end

            // 市场代码转化成资产代码
            // 市场代码取值变更 modify by yufei 20200826 start
            String changeToFinprodName = (String) CommonUtil.findValue(dataMap, "changeToFinprodMarketId");
            // 市场代码取值变更 modify by yufei 20200826 end
            String preFix = BussinessDictCodeList.金融产品类型_投管用_基金.getValue();
            changeToFinprodName = StringUtil.startsWith(changeToFinprodName, preFix)
                    ? changeToFinprodName : (preFix + GlobalConstant.UNDER_LINE + changeToFinprodName);
            dataMap.put("changeToFinprodName", changeToFinprodName);
            PageFieldToInterfFactory.mapping(dataMap, infMap);
            // add by zhangrui1 基金转换申请使用最新净值 start
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)
                    || CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换赎回申请)) {
                BigDecimal netPrice = CommonUtil.getBigDecimal(dataMap, "netPrice", null);
                if (null == netPrice) {
                    netPrice = fundNetService.getValuation(
                            (String) infMap.get(PageFieldToInterfFactory.INF_ASSETCODE));
                }
                netPrice = null == netPrice ? BigDecimal.ONE : netPrice;
                BigDecimal amount = ArithmeticUtil.multiply(
                        CommonUtil.getBigDecimal(dataMap, "shareAmt", BigDecimal.ZERO), netPrice);
                infMap.put(PageFieldToInterfFactory.INF_TRADENUM, dataMap.get("shareAmt"));
                infMap.put(PageFieldToInterfFactory.INF_AMOUNT, amount);
            }
            // add by zhangrui1 基金转换申请使用最新净值 end
            doCommon(infMap, v);
            if (isNeedTail((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID),
                    (String) infMap.get(PageFieldToInterfFactory.INF_PS))) {
                infMap.put(PageFieldToInterfFactory.INF_EVENTID,
                        (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                                PageFieldToInterfFactory.INF_PS));
            }
            // add by zhangrui1 正反同步交易特殊处理 start
            String groupId = String.valueOf(System.nanoTime());
            infMap.put(GROUP_ID, groupId);
            infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
            infMap.put(IS_CONV, BaseDictCodeList.是否_是.getValue());
            infMap.put("fundConvertFlg", DictCodeList.是否_是.getValue());
            // add by zhangrui1 正反同步交易特殊处理 end
            String eventDate = null;
            if (StringUtil.isNotEmpty(dataMap.get("tradeDate"))) {
                Object tradeDate = dataMap.get("tradeDate");
                String type = tradeDate.getClass().toString();
                if ("Long".equals(type.substring(type.lastIndexOf('.') + 1, type.length()))) {
                    eventDate = DateUtil.format(new Date((long) tradeDate));
                } else {
                    eventDate = DateUtil.format(DateUtil.parse(tradeDate.toString()));
                }
            }
            infMap.put("eventDate", eventDate);
            result.add(infMap);
            // 基金转出
            Object amount = infMap.get(PageFieldToInterfFactory.INF_AMOUNT);
            infMap = new HashMap<>();
            dataMap.put(InterfUtil.ps + 2, BussinessDictCodeList.交易方向_买入.getValue());
            PageFieldToInterfFactory.mapping(dataMap, infMap);
            // add by zhangrui1 基金转换申请使用最新净值 start
            if (CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换申请)
                    || CommonUtil.isType(ps, DictCodeListExt2.交易方向_转换赎回申请)) {
                infMap.put(PageFieldToInterfFactory.INF_AMOUNT, amount);
                infMap.put(PageFieldToInterfFactory.INF_TRADENUM, null);
                Object rFinprodId = CommonUtil.findValue(dataMap, "rFinprodId");
                if (!StringUtil.isNotEmpty(infMap.get(PageFieldToInterfFactory.INF_ASSETCODE))
                        && StringUtil.isNotEmpty(rFinprodId)) {
                    infMap.put(PageFieldToInterfFactory.INF_ASSETCODE, rFinprodId);
                }
            }
            // add by zhangrui1 基金转换申请使用最新净值 end
            doCommon(infMap, v);
            if (isNeedTail((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID),
                    (String) infMap.get(PageFieldToInterfFactory.INF_PS))) {
                infMap.put(PageFieldToInterfFactory.INF_EVENTID,
                        (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                                PageFieldToInterfFactory.INF_PS));
            }
            // add by zhangrui1 正反同步交易特殊处理 start
            infMap.put(GROUP_ID, groupId);
            infMap.put(IS_SYNC, BussinessDictCodeList.是否_是.getValue());
            infMap.put("fundConvertFlg", DictCodeList.是否_是.getValue());
            // add by zhangrui1 正反同步交易特殊处理 end
            String entrustId = (String) CommonUtil.findValue(dataMap, "entrustId");
            if (!StringUtil.isEmpty(entrustId)
                    && !CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托)) {
                infMap.put(PageFieldToInterfFactory.INF_ENTRUSTCODE, entrustId);
                infMap.put(PageFieldToInterfFactory.INF_PREOCCUPYCODE,
                        entrustId + BussinessDictCodeList.交易方向_买入.getValue());
            }
            if (StringUtil.isNotEmpty(dataMap.get("tradeDate"))) {
                Object tradeDate = dataMap.get("tradeDate");
                String type = tradeDate.getClass().toString();
                if ("Long".equals(type.substring(type.lastIndexOf('.') + 1, type.length()))) {
                    eventDate = DateUtil.format(new Date((long) tradeDate));
                } else {
                    eventDate = DateUtil.format(DateUtil.parse(tradeDate.toString()));
                }
            }
            infMap.put("eventDate", eventDate);
            result.add(infMap);
        } else if (CommonUtil.isType(ps, BussinessDictCodeList.交易方向_卖出)) {
            logger.info("============转仓交易============");
            // 一笔卖出交易
            InterfRiskRdsDto dto = prepareRiskEntity(dataMap, v);
            dto.setCounterId((String) CommonUtil.findValue(dataMap, "counterId"));
            // 设置批量接口ID
            dto.setInfCode(changeInCode(v.getInfCode()));
            // 释放的场合设置撤销FLG为Y
            if (CommonUtil.isType(dto.getInfCode(), BussinessDictCodeListRds.接口系统ID_实占释放批量接口)
                    || CommonUtil.isType(dto.getInfCode(), BussinessDictCodeListRds.接口系统ID_预占释放批量接口)) {
                dto.setRevokeFlg(BussinessDictCodeList.是否_是.getValue());
            }
            dto.setIsInnerTrd((String) dataMap.get(PageFieldToInterfFactory.INF_ISINNERTRD));
            dto.setEventType(v.getEventType());
            dto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
            dto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
            Object ccy = dto.getCcy();
            if (!StringUtil.isNotEmpty(ccy)) {
                dto.setCcy(BussinessDictCodeList.币种_人民币.getValue());
            }
            if (StringUtil.equals(dto.getPortfolioCode(), dto.getCounterId())) {
                logger.info("同一母组合下面的两个子组合交易不进行风控");
                return null;
            }
            Map<String, Object> map = BeanUtil.object2Map(dto);
            changeEventId(map);
            result.add(map);
            String isAuto = CacheUtil.getSysParam("is_auto_generate_negate_deal");
            if (CommonUtil.isType(isAuto, DictCodeList.是否_否)
                    || (CommonUtil.isType(dto.getInfCode(), BussinessDictCodeListRds.接口系统ID_实占释放批量接口)
                    && StringUtil.equals(CommonUtil.findValue(dataMap, "operateType"),
                    "revoke"))) {
                if(!StringUtil.equals(map.get("isInnerTrd"),BussinessDictCodeListRds.是否_是.getValue())){
                    return result;
                }
            }
            // 一笔买入交易
            InterfRiskRdsDto dtoBy = new InterfRiskRdsDto();
            BeanUtil.copyPropertiesIgnoreNull(dtoBy, dto);
            dtoBy.setPortfolioCode(dto.getCounterId());
            dtoBy.setCounterId(dto.getPortfolioCode());
            dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
            map = BeanUtil.object2Map(dtoBy);
            changeEventId(map);
            result.add(map);
        }
        //分红送转拆分成一笔现金分红一笔送转
        else if (StringUtil.equals(DictCodeList.股票分红类型_分红送转.getValue(), ps)) {
            Map<String, Object> dataMapTmp = new HashMap<String, Object>();
            dataMapTmp.putAll(dataMap);
            //现金分红交易
            dataMapTmp.put(InterfUtil.ps, DictCodeList.股票分红类型_现金分红.getValue());
            InterfRiskRdsDto fhDto = periodEntityDev(dataMapTmp, v);
            fhDto.setEventType(v.getEventType());
            fhDto.setInfCode(changeInCode(v.getInfCode()));
            // 释放的场合设置撤销FLG为Y
            if (CommonUtil.isType(fhDto.getInfCode(), BussinessDictCodeListRds.接口系统ID_实占释放批量接口)) {
                fhDto.setRevokeFlg(BussinessDictCodeList.是否_是.getValue());
            }
            result.add(BeanUtil.object2Map(fhDto));
            //送转交易
            dataMapTmp.put(InterfUtil.ps, DictCodeList.股票分红类型_送转.getValue());
            InterfRiskRdsDto szDto = periodEntityDev(dataMapTmp, v);
            szDto.setEventType(v.getEventType());
            szDto.setInfCode(changeInCode(v.getInfCode()));
            // 释放的场合设置撤销FLG为Y
            if (CommonUtil.isType(szDto.getInfCode(), BussinessDictCodeListRds.接口系统ID_实占释放批量接口)) {
                szDto.setRevokeFlg(BussinessDictCodeList.是否_是.getValue());
            }
            result.add(BeanUtil.object2Map(szDto));
            // add by zhangrui1 事件编号+交易方向 start
            result.forEach(map -> changeEventId(map));
            // add by zhangrui1 事件编号+交易方向 end
        } else if (StringUtil.equals(DictCodeList.交易方向_上市流通.getValue(), ps)) {
            //上市流通可能存在多个组合交易，每个组合需要拆分为原资产卖出和上市资产买入两笔交易
            if (StringUtil.isNotEmpty(dataMap.get("baseTradeInfoDtos"))) {
                List<Map> baseTradeInfoDtos = (List<Map>) dataMap.get("baseTradeInfoDtos");
                String regionFinprodId = (String) dataMap.get("finprodId");
                String newFinprodId = "F01_" + (String) dataMap.get("appearMarketId");
                String eventDate = null;
                if (StringUtil.isNotEmpty(dataMap.get("tradeDate"))) {
                    Object tradeDate = dataMap.get("tradeDate");
                    String type = tradeDate.getClass().toString();
                    if ("Long".equals(type.substring(type.lastIndexOf('.') + 1, type.length()))) {
                        eventDate = DateUtil.format(new Date((long) tradeDate));
                    } else {
                        eventDate = DateUtil.format(DateUtil.parse(tradeDate.toString()));
                    }
                }
                for (Map<String, Object> map : baseTradeInfoDtos) {
                    //获取接口类型
                    String infCode = v.getInfCode();
                    if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占批量接口)) {
                        infCode = BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue();
                    } else {
                        infCode = BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue();
                    }
                    //获取 组合代码
                    String portfolioId = null, invAim = null, tradeId = null;
                    BigDecimal tradeAmt = BigDecimal.ZERO, shareAmt = BigDecimal.ZERO;
                    if (StringUtil.isNotEmpty(map.get("trdProductDeal"))) {
                        Map<String, Object> trdProductDeal = (Map<String, Object>) map.get("trdProductDeal");
                        //交易金额：市价
                        if (StringUtil.isNotEmpty(trdProductDeal.get("tradeAmt"))) {
                            tradeAmt = new BigDecimal(trdProductDeal.get("tradeAmt").toString());
                        }
                        //交易数量：券面总额
                        if (StringUtil.isNotEmpty(trdProductDeal.get("shareAmt"))) {
                            shareAmt = new BigDecimal(trdProductDeal.get("shareAmt").toString());
                        }
                        invAim = (String) trdProductDeal.get("invAim");
                        tradeId = (String) trdProductDeal.get("tradeId");
                    }
                    if (StringUtil.isNotEmpty(map.get("trdProductDealAdd"))) {
                        Map<String, Object> trdProductDealAdd = (Map<String, Object>) map.get("trdProductDealAdd");
                        portfolioId = (String) trdProductDealAdd.get("portfolioId");
                    }
                    if (StringUtil.isNotEmpty(tradeId) && StringUtil.isNotEmpty(eventDate)) {
                        //买入
                        InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
                        infRiskRdsDto.setEventType(v.getEventType());
                        infRiskRdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                        infRiskRdsDto.setInfCode(infCode);
                        infRiskRdsDto.setEventId(tradeId);
                        infRiskRdsDto.setEventDate(eventDate);
                        infRiskRdsDto.setPortfolioCode(portfolioId);
                        infRiskRdsDto.setAssetCode(newFinprodId);
                        infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                        infRiskRdsDto.setAmount(tradeAmt);
                        infRiskRdsDto.setTradeNum(shareAmt);
                        infRiskRdsDto.setDeliveryDate(DateUtil.parse(eventDate));
                        infRiskRdsDto.setMarketCode(newFinprodId);
                        infRiskRdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                        infRiskRdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                        infRiskRdsDto.setTradeAmtReal(tradeAmt);
                        infRiskRdsDto.setInvAim(invAim);
                        infRiskRdsDto.setTradePlace((String) dataMap.get("tradeMarket"));
                        result.add(BeanUtil.object2Map(infRiskRdsDto));
                        //卖出
                        infRiskRdsDto.setAssetCode(regionFinprodId);
                        infRiskRdsDto.setMarketCode(regionFinprodId);
                        infRiskRdsDto.setPs(DictCodeList.交易方向_卖出.getValue());
                        result.add(BeanUtil.object2Map(infRiskRdsDto));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 找到对应的批量接口
     *
     * @param infCode 接口ID
     * @return 对应的批量接口ID
     */
    private String changeInCode(String infCode) {
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占接口)) {
            return BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue();
        } else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_风险试算接口)) {
            return BussinessDictCodeListRds.接口系统ID_风险试算批量接口.getValue();
        } else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放接口)) {
            return BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue();
        } else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占接口)) {
            return BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
        } else if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放接口)) {
            return BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue();
        }
        return infCode;
    }

    private void doCommon(Map<String, Object> infMap, V v) {
        String infCode = v.getInfCode();
        infMap.put(PageFieldToInterfFactory.INF_CODE, infCode);
        if (CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_实占释放批量接口)
                || CommonUtil.isType(infCode, BussinessDictCodeListRds.接口系统ID_预占释放批量接口)) {
            infMap.put(PageFieldToInterfFactory.INF_REVOKEFLG, BussinessDictCodeList.是否_是.getValue());
        }
        infMap.put(PageFieldToInterfFactory.INF_EVENTTYPE, v.getEventType());
        infMap.put("isWarnNeedConfirm", DictCodeList.是否_否.getValue());
        infMap.put("isApprNeedConfirm", DictCodeList.是否_否.getValue());
        Object ccy = infMap.get(PageFieldToInterfFactory.INF_CCY);
        if (!StringUtil.isNotEmpty(ccy)) {
            infMap.put(PageFieldToInterfFactory.INF_CCY, BussinessDictCodeList.币种_人民币.getValue());
        }
        infMap.put(PageFieldToInterfFactory.INF_EVENTID, removeLasStr((String) infMap.get(PageFieldToInterfFactory.INF_EVENTID)));
        // add by zhangrui1 事件编号+交易方向 start
        changeEventId(infMap);
        // add by zhangrui1 事件编号+交易方向 end
    }
    // add by zhangrui1 调用批量接口逻辑 end

    /**
     * 构造接口实体
     *
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    public InterfRiskRdsDto prepareRiskEntity(Map<String, Object> dataMap, V v) throws IOException, InstantiationException {
        if (StringUtil.isNotEmpty(dataMap.get(InterfUtil.finprodId)) && !StringUtil.isNotEmpty(v.getAssetType())) {
            Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Arrays.asList(dataMap.get(InterfUtil.finprodId).toString()));
            for (IAssetInfo iAst : mapAsset.values()) {
                v.setAssetType(StringUtil.isNotEmpty(iAst) ? iAst.getAssetType() : v.getAssetType());
            }
        }
        //定义返回实体
        if (StringUtil.equals(v.getEventType(), BussinessDictCodeListRds.事件类型_投资交易.getValue())) {
            if (StringUtil.equals(v.getEventStatus(), BussinessDictCodeListRds.事件类型_投资交易_存续期.getValue())) {
                return periodEntityDev(dataMap, v);
            }
            return tradeEntityDev(dataMap, v);
        } else {
            //判断是否来自场内交易指令
            if (StringUtil.equals(dataMap.get("fromEtrExchange"), DictCodeList.是否_是.getValue())) {
                return entrustExchangeEntityDev(dataMap, v);
            } else {
                return entrustEntityDev(dataMap, v);
            }
        }

    }

    /**
     * 委托实体封装
     *
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */

    private InterfRiskRdsDto entrustEntityDev(Map<String, Object> dataMap, V v) {
        //定义返回实体
        InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
        EntrustBaseDto finDto = null;
        try {
            finDto = (EntrustBaseDto) JsonUtils.map2entity(dataMap, EntrustBaseDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("数据转换异常");
        }
        infRiskRdsDto.setEventId(finDto.getEntrustId());
        infRiskRdsDto.setEventDate(DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
        //设置母组合代码和子组合代码
        infRiskRdsDto.setPortfolioCode(AllTradeCommonUtil.exchangePortfolio(StringUtil.isNotEmpty(finDto.getPortfolioId()) ? finDto.getPortfolioId() : finDto.getActPortfolioId()));
        infRiskRdsDto.setAssetCode(finDto.getFinprodId());
        infRiskRdsDto.setPs(finDto.getPs());
        infRiskRdsDto.setAmount(finDto.getEntrustAmt());//项目类,基金，计划类申购，存款,拆借
        infRiskRdsDto.setTradeNum(finDto.getEntrustNum());
        infRiskRdsDto.setDeliveryDate(finDto.getMdate());
        infRiskRdsDto.setMarketCode(finDto.getFinprodId());
        infRiskRdsDto.setTradePlace(finDto.getTradeMarket());
        infRiskRdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setMdate(finDto.getMdate());
        infRiskRdsDto.setInvAim(finDto.getInvAim());
        // add by zhangrui1 拆借回购等没有资产的需要传日历代码 start
        if (!StringUtil.isEmpty(finDto.getCalendarId())) {
            infRiskRdsDto.setCalendarCode(finDto.getCalendarId());
        }
        // add by zhangrui1 拆借回购等没有资产的需要传日历代码 end
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType())) {
            // modify by zhangrui1 20200803 start
            // 债券金额更改为券面总额除以100乘以可用面值
            //infRiskRdsDto.setAmount(finDto.getEntrustNum());//债券为全价总额存款、计划类利率型、项目类资产：本金金额（元）基金（申购）、净值型计划类资产：申购金额（元）货币型计划类资产（申购）：申购金额（元）回购、拆借：本金金额（元）
            //infRiskRdsDto.setTradeNum(finDto.getEntrustNum());//债券：券面总额（张）基金（赎回）：赎回份额（份）货币型计划类资产（赎回）：赎回份额（份）正逆回购:首期结算金额
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(finDto.getAvailableNum()) ? ArithmeticUtil.multiply(ArithmeticUtil.divide(finDto.getEntrustNum(), new BigDecimal(100), 4), finDto.getAvailableNum()) : finDto.getEntrustNum());
            infRiskRdsDto.setTradeNum(ArithmeticUtil.divide(finDto.getEntrustNum(), new BigDecimal(100), 4));
            // modify by zhangrui1 20200803 end
            infRiskRdsDto.setNetPrice(finDto.getPrice());//债券单位净价,其它不填
            infRiskRdsDto.setTradeMarketMode(finDto.getTradeMarketMode());
            //将到期收益率除以100 update by zhangrui1 start
            Object obj = CommonUtil.findValue(dataMap, "matureYield");
            if (obj != null && !StringUtil.isEmpty(String.valueOf(obj))) {
                infRiskRdsDto.setYieldEndRate(ArithmeticUtil.divide(new BigDecimal(obj.toString()), new BigDecimal(100), 4));//到期收益率
            }
            //将到期收益率除以100 update by zhangrui1 end
            //可转债
            if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("purchBondTotal")) ? dataMap.get("purchBondTotal") instanceof BigDecimal ? (BigDecimal) dataMap.get("purchBondTotal") : new BigDecimal(dataMap.get("purchBondTotal").toString()) : null);//可转债为申购券面总额
                infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty(dataMap.get("purchBondTotal")) ? dataMap.get("purchBondTotal") instanceof BigDecimal ? (BigDecimal) dataMap.get("purchBondTotal") : new BigDecimal(dataMap.get("purchBondTotal").toString()) : null);//可转债为申购券面总额
            }
            //update by  zhangnian  20200326  start
            //债转股指令委托
            else if (StringUtil.equals(finDto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
                infRiskRdsDto.setAmount(finDto.getEntrustAmt());
                infRiskRdsDto.setTradeNum(finDto.getEntrustNum());
            }
            //update by  zhangnian  20200326  end
            // add by zhangrui1 增加券面总额（元） start
            infRiskRdsDto.setTotalFaceAmt(infRiskRdsDto.getAmount());
            // add by zhangrui1 增加券面总额（元） end

            //设置是否是股东发行
            infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
        }
        //基金，计划类
        if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_赎回.getValue()) && !StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), v.getAssetType())) {
            /* del by zhangrui1 赎回指令净值不是1 start
            infRiskRdsDto.setAmount(finDto.getEntrustNum());
               del by zhangrui1 赎回指令净值不是1 end */
            infRiskRdsDto.setAmount(null);
        }
        if (StringUtil.equals(finDto.getPs(), DictCodeList.回购拆借方向_拆入.getValue()) ||
                StringUtil.equals(finDto.getPs(), DictCodeList.回购拆借方向_拆出.getValue())) {
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_拆借.getValue());
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            if (StringUtil.isNotEmpty(dataMap.get("mdateAdd"))) {
                Date mdate;
                if (dataMap.get("mdateAdd") instanceof Long) {
                    mdate = new Date(Long.parseLong(dataMap.get("mdateAdd").toString()));
                } else {
                    mdate = DateUtil.parse(dataMap.get("mdateAdd").toString());
                }
                long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(), mdate);
                infRiskRdsDto.setBetweenDate(oneDay);
            }
            infRiskRdsDto.setCalendarCode(finDto.getCalendarId());
            infRiskRdsDto.setMdate(StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "mdateAdd")) ? DateUtil.parse(CommonUtil.findValue(dataMap, "mdateAdd").toString()) : null);
        }
        if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), v.getAssetType())) {
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setAssetRate(StringUtil.isNotEmpty(dataMap.get("price")) ? dataMap.get("price") instanceof BigDecimal ? ArithmeticUtil.divide((BigDecimal) dataMap.get("price"), new BigDecimal(100), 4) : ArithmeticUtil.divide(new BigDecimal(dataMap.get("price").toString()), new BigDecimal(100), 4) : null);
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            infRiskRdsDto.setCalendarCode((String) dataMap.get("calendarId"));
            infRiskRdsDto.setDeadline(StringUtil.isNotEmpty(dataMap.get("repoTerm")) ? new BigDecimal(dataMap.get("repoTerm").toString()) : null);
            //----add by 裴家俊 组合资产剩余期限 20201125 start-----
            Object repoTerm = CommonUtil.findValue(dataMap, "repoTerm");
            if (StringUtil.isNotEmpty(repoTerm)) {
                infRiskRdsDto.setRepoTerm(new BigDecimal(repoTerm.toString()));
                Date vdate = null;
                if (Objects.nonNull(CommonUtil.findValue(dataMap, "vdate"))) {
                    if (dataMap.get("vdate") instanceof Long) {
                        vdate = new Date(Long.parseLong(dataMap.get("vdate").toString()));
                    } else {
                        vdate = DateUtil.parse(dataMap.get("vdate").toString());
                    }
                }
                infRiskRdsDto.setMdate(DateUtil.parse(DateUtil.addDay(Objects.nonNull(vdate) ? DateUtil.format(vdate) : infRiskRdsDto.getEventDate(), Integer.valueOf(repoTerm.toString()))));
            }
            //----add by 裴家俊 组合资产剩余期限 20201125 end-----

            //添加抵押品
            if (finDto.getBussRepoSec() != null && finDto.getBussRepoSec().getPledgeBondList() != null) {
                infRiskRdsDto.setPledgedCode(finDto.getBussRepoSec().getPledgeBondList()
                        .stream()
                        .map(PledgeBondInfoDto::getFinprodId).distinct()
                        .collect(Collectors.joining(RiskUtil.UP_LINE)));
            }
        }
        if (StringUtil.equals(finDto.getBusiPs(), DictCodeList.回购拆借方向_融入.getValue()) ||
                StringUtil.equals(finDto.getBusiPs(), DictCodeList.回购拆借方向_融出.getValue())) {
            BigDecimal totalAmt = ArithmeticUtil.multiply(StringUtil.isNotEmpty(dataMap.get("totalAmt")) ? dataMap.get("totalAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("totalAmt") : new BigDecimal(dataMap.get("totalAmt").toString()) : BigDecimal.ZERO, new BigDecimal(10000));
            infRiskRdsDto.setAmount(ArithmeticUtil.multiply(totalAmt, StringUtil.isNotEmpty(dataMap.get("busiRate")) ? dataMap.get("busiRate") instanceof BigDecimal ? ArithmeticUtil.divide((BigDecimal) dataMap.get("busiRate"), new BigDecimal(100), 4) : ArithmeticUtil.divide(new BigDecimal(dataMap.get("busiRate").toString()), new BigDecimal(100), 4) : BigDecimal.ZERO));
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
            infRiskRdsDto.setAssetType2(finDto.getBusiPs());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            infRiskRdsDto.setCalendarCode((String) dataMap.get("calendarId"));
            infRiskRdsDto.setDeadline(StringUtil.isNotEmpty(dataMap.get("termDays")) ? new BigDecimal(dataMap.get("termDays").toString()) : null);
            if (StringUtil.isNotEmpty(dataMap.get("mdate"))) {
                Date mdate;
                if (dataMap.get("mdate") instanceof Long) {
                    mdate = new Date(Long.parseLong(dataMap.get("mdate").toString()));
                } else {
                    mdate = DateUtil.parse(dataMap.get("mdate").toString());
                }
                long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(), mdate);
                infRiskRdsDto.setBetweenDate(oneDay);
            }
        }
        if (StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetRate(StringUtil.isNotEmpty(dataMap.get("price")) ? dataMap.get("price") instanceof BigDecimal ? ArithmeticUtil.divide((BigDecimal) dataMap.get("price"), new BigDecimal(100), 4) : ArithmeticUtil.divide(new BigDecimal(dataMap.get("price").toString()), new BigDecimal(100), 4) : null);
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            infRiskRdsDto.setCalendarCode((String) dataMap.get("calendarId"));
            infRiskRdsDto.setDeadline(StringUtil.isNotEmpty(dataMap.get("repoTerm2")) ? new BigDecimal(dataMap.get("repoTerm2").toString()) : null);
            //----add by 裴家俊 组合资产剩余期限 20201125 start-----
            Object repoTerm = StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "repoTerm2")) ?
                    CommonUtil.findValue(dataMap, "repoTerm2") : CommonUtil.findValue(dataMap, "repoTerm");//从严控制
            if (StringUtil.isNotEmpty(repoTerm)) {
                infRiskRdsDto.setRepoTerm(new BigDecimal(repoTerm.toString()));
                Date vdate = null;
                if (Objects.nonNull(CommonUtil.findValue(dataMap, "vdate"))) {
                    if (dataMap.get("vdate") instanceof Long) {
                        vdate = new Date(Long.parseLong(dataMap.get("vdate").toString()));
                    } else {
                        vdate = DateUtil.parse(dataMap.get("vdate").toString());
                    }
                }
                infRiskRdsDto.setMdate(DateUtil.parse(DateUtil.addDay(Objects.nonNull(vdate) ? DateUtil.format(vdate) : infRiskRdsDto.getEventDate(), Integer.valueOf(repoTerm.toString()))));
            }
            //----add by 裴家俊 组合资产剩余期限 20201125 end-----

            //添加抵押品
            infRiskRdsDto.setPledgedCode(finDto.getFinprodId());
        }
        if (StringUtil.equals(DictCodeListExt2.平安资产类型_一级市场债.getValue(), v.getAssetType())) {
            // add by zhangrui1 start
            // 中标结果确认及中标结果维护
            if (CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_中标结果确认)
                    || CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_中标结果维护)) {
                infRiskRdsDto.setDiffOccupyFlg((String) dataMap.get("bidId") + v.getEventStatus());
                String amountKey = CommonUtil.isType(v.getEventStatus(),
                        BussinessDictCodeList.共同业务类型_中标结果确认) ? "adjustBallotnum" : "winPrinAmt";
                Object amountObj = dataMap.get(amountKey);
                infRiskRdsDto.setAmount(
                        amountObj != null ? new BigDecimal(amountObj.toString()) : BigDecimal.ZERO);
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());

                // 一级市场债两个service用的DTO里的中标日字段不一样，大小写不一致。。
                if (StringUtil.isNotEmpty(dataMap.get("biddate"))) {
                    if (dataMap.get("biddate") instanceof Long) {
                        infRiskRdsDto.setEventDate(DateUtil.format(
                                new Date(Long.parseLong(dataMap.get("biddate").toString())),
                                DateUtil.DEFAULT_DATE_FORMAT_EN));
                    } else {
                        infRiskRdsDto.setEventDate((String) dataMap.get("biddate"));
                    }
                } else if (StringUtil.isNotEmpty(dataMap.get("bidDate"))) {
                    if (dataMap.get("bidDate") instanceof Long) {
                        infRiskRdsDto.setEventDate(DateUtil.format(
                                new Date(Long.parseLong(dataMap.get("bidDate").toString())),
                                DateUtil.DEFAULT_DATE_FORMAT_EN));
                    } else {
                        infRiskRdsDto.setEventDate((String) dataMap.get("bidDate"));
                    }
                } else if (StringUtil.isNotEmpty(dataMap.get("payDate"))) {
                    if (dataMap.get("payDate") instanceof Long) {
                        infRiskRdsDto.setEventDate(DateUtil.format(
                                new Date(Long.parseLong(dataMap.get("payDate").toString())),
                                DateUtil.DEFAULT_DATE_FORMAT_EN));
                    } else {
                        infRiskRdsDto.setEventDate((String) dataMap.get("payDate"));
                    }
                }
                if (CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_中标结果维护)
                        && StringUtil.isEmpty(infRiskRdsDto.getPortfolioCode())) {
                    List list = (List) dataMap.get("bidOrderBidPriceList");
                    if (!StringUtil.isEmptyList(list)) {
                        Object obj = list.get(0);
                        Map<String, Object> temp = CommonUtil.toMap(obj);
                        if (!CollectionUtils.isEmpty(temp)) {
                            infRiskRdsDto.setPortfolioCode((String) temp.get("portfolioId"));
                        }
                        BigDecimal winPrimAmt = BigDecimal.ZERO;
                        for (Object item : list) {
                            Map<String, Object> tMap = CommonUtil.toMap(item);
                            winPrimAmt = ArithmeticUtil.doAdd(winPrimAmt,
                                    CommonUtil.getBigDecimal(tMap, "winPrinAmt", BigDecimal.ZERO));
                        }
                        infRiskRdsDto.setAmount(winPrimAmt);
                    }
                }
                infRiskRdsDto.setTotalFaceAmt(infRiskRdsDto.getAmount());
                infRiskRdsDto.setTradeNum(ArithmeticUtil.divide(infRiskRdsDto.getTotalFaceAmt(),
                        new BigDecimal(100), 4));
            } else {
                if (CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_申购_登记)) {
                    infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "subscriptionAmount", BigDecimal.ZERO));//债券为全价总额存款、
                    infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "subscriptionAmount", BigDecimal.ZERO));
                    infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
                    infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                    infRiskRdsDto.setTotalFaceAmt(infRiskRdsDto.getAmount());
                    infRiskRdsDto.setPortfolioCode(finDto.getPortfolioId());
                    Object isDiffOccupy = CommonUtil.findValue(dataMap, "isDiffOccupy");
                    if (StringUtil.isNotEmpty(isDiffOccupy)) {
                        infRiskRdsDto.setIsDiffOccupy((String) isDiffOccupy);
                    }
                    Object diffOccupyFlg = CommonUtil.findValue(dataMap, "diffOccupyFlg");
                    if (StringUtil.isNotEmpty(diffOccupyFlg)) {
                        infRiskRdsDto.setDiffOccupyFlg((String) diffOccupyFlg);
                    }
                    Object isReverseOccupy = CommonUtil.findValue(dataMap, "isReverseOccupy");
                    if (StringUtil.isNotEmpty(diffOccupyFlg)) {
                        infRiskRdsDto.setIsReverseOccupy((String) isReverseOccupy);
                    }
                } else if (CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入) ||
                        CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴) ||
                        CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标)) {
                        if (CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_分销买入)) {
                            infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "bidPriceTotal", BigDecimal.ZERO));//券面总额
                            infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "bidPriceTotal", BigDecimal.ZERO));
                        }
                        if (CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_代投代缴)
                          || CommonUtil.isType(v.getEventStatus(), BussinessDictCodeList.共同业务类型_一级投标确认_投标_自主投标)) {
                            infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "totalBiddingNum", BigDecimal.ZERO));//汇总券面总额
                            infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "totalBiddingNum", BigDecimal.ZERO));
                        }
                        infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
                        infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                        infRiskRdsDto.setTotalFaceAmt(infRiskRdsDto.getAmount());
                        infRiskRdsDto.setPortfolioCode(finDto.getPortfolioId());
                        Object isDiffOccupy = CommonUtil.findValue(dataMap, "isDiffOccupy");
                        if (StringUtil.isNotEmpty(isDiffOccupy)) {
                            infRiskRdsDto.setIsDiffOccupy((String) isDiffOccupy);
                        }
                        Object diffOccupyFlg = CommonUtil.findValue(dataMap, "diffOccupyFlg");
                        if (StringUtil.isNotEmpty(diffOccupyFlg)) {
                            infRiskRdsDto.setDiffOccupyFlg((String) diffOccupyFlg);
                        }
                        Object isReverseOccupy = CommonUtil.findValue(dataMap, "isReverseOccupy");
                        if (StringUtil.isNotEmpty(diffOccupyFlg)) {
                            infRiskRdsDto.setIsReverseOccupy((String) isReverseOccupy);
                        }
                }else {
                    // add by zhangrui1 end
                    infRiskRdsDto.setAmount(finDto.getEntrustNum());//债券为全价总额存款、
                    infRiskRdsDto.setTradeNum(
                            ArithmeticUtil.divide(finDto.getEntrustNum(), new BigDecimal(100), 4));
                    infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
                    infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                    // add by zhangrui1 start
                    infRiskRdsDto.setTotalFaceAmt(infRiskRdsDto.getAmount());
                    Object isDiffOccupy = CommonUtil.findValue(dataMap, "isDiffOccupy");
                    if (StringUtil.isNotEmpty(isDiffOccupy)) {
                        infRiskRdsDto.setIsDiffOccupy((String) isDiffOccupy);
                    }
                    Object diffOccupyFlg = CommonUtil.findValue(dataMap, "diffOccupyFlg");
                    if (StringUtil.isNotEmpty(diffOccupyFlg)) {
                        infRiskRdsDto.setDiffOccupyFlg((String) diffOccupyFlg);
                    }
                    Object isReverseOccupy = CommonUtil.findValue(dataMap, "isReverseOccupy");
                    if (StringUtil.isNotEmpty(diffOccupyFlg)) {
                        infRiskRdsDto.setIsReverseOccupy((String) isReverseOccupy);
                    }
                }
            }
            // add by zhangrui1 end

            //设置是否是股东发行
            infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
        }
        if (StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAmount(finDto.getEntrustAmt() == null ? BigDecimal.ZERO : finDto.getEntrustAmt().multiply(new BigDecimal(10000)));
            infRiskRdsDto.setTradeNum(finDto.getEntrustAmt() == null ? BigDecimal.ZERO : finDto.getEntrustAmt().multiply(new BigDecimal(10000)));
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_利率互换.getValue());
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
        }
        if (StringUtil.equals(DictCodeListExt3.平安资产类型_期权.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAmount(finDto.getEntrustNum());
            infRiskRdsDto.setTradeNum(finDto.getEntrustNum());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            if (StringUtil.isNotEmpty(dataMap.get("termDays"))) {
                infRiskRdsDto.setBetweenDate(Long.parseLong(dataMap.get("termDays").toString()));
            }
        }
        if (StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), v.getAssetType())) {
            infRiskRdsDto.setPs(StringUtil.isNotEmpty(dataMap.get("tradePs")) ? dataMap.get("tradePs").toString() : infRiskRdsDto.getPs());
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("tradeAmt")) ? dataMap.get("tradeAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("tradeAmt") : new BigDecimal(dataMap.get("tradeAmt").toString()) : null);
        }
        if (StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), v.getAssetType())) {
            if (!StringUtil.isNotEmpty(finDto.getEntrustNum()) && StringUtil.isNotEmpty(dataMap.get("price"))) {
                infRiskRdsDto.setTradeNum(ArithmeticUtil.divide(finDto.getEntrustAmt(), new BigDecimal((String) dataMap.get("price")), 2));
            }
        }
        // add by zhangrui1 画面到期日是mdateAdd start
        Object mdateAdd = dataMap.get("mdateAdd");
        if ((CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_拆借)
                || CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_拆借)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_拆出)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_拆入)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_特拆拆入))
                && null != mdateAdd) {
            if (mdateAdd instanceof Long) {
                infRiskRdsDto.setMdate(new Date((long) mdateAdd));
            } else if (mdateAdd instanceof Date) {
                infRiskRdsDto.setMdate((Date) mdateAdd);
            } else {
                infRiskRdsDto.setMdate(DateUtil.parse(mdateAdd.toString()));
            }
        }
        // 回购指令画面没有到期日的
        else if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_质押式回购)
                || CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_质押式回购)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_债券买断式正回购)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_债券买断式逆回购)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_债券质押式正回购)
                || CommonUtil.isType((String) dataMap.get(InterfUtil.finprodType2),
                BussinessDictCodeList.金融产品类型_投管用_债券质押式逆回购)) {
            Object vdate = dataMap.get("vdate");
            String repoTerm = dataMap.get("repoTerm") == null ? "0" : dataMap.get("repoTerm").toString();
            if (vdate instanceof Long) {
                infRiskRdsDto.setMdate(DateUtil.addDay(new Date((long) vdate), Integer.valueOf(repoTerm)));
            } else if (mdateAdd instanceof Date) {
                infRiskRdsDto.setMdate(DateUtil.addDay((Date) vdate, Integer.valueOf(repoTerm)));
            } else if (null != vdate) {
                infRiskRdsDto.setMdate(DateUtil.addDay(DateUtil.parse(vdate.toString()), Integer.valueOf(repoTerm)));
            }
        }
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_质押式回购)) {
            if (!StringUtil.isNotEmpty(infRiskRdsDto.getMdate())) {
                infRiskRdsDto.setMdate(DateUtil.parse(infRiskRdsDto.getEventDate()));
            }
            long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                    DateUtil.parse(infRiskRdsDto.getMdate()));
            infRiskRdsDto.setOneDay(oneDay);
            //wangyang1 add by 增加期限金额 2021年9月10日 start
            if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(oneDay)) {
                infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(oneDay)));
            }
            if (StringUtil.isNotEmpty(finDto.getCounterId())) {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
            } else {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
            }
            //wangyang1 add by 增加期限金额 2021年9月10日 end
            //获取交易对手的市场机构代码
            if (StringUtil.isNotEmpty(finDto.getCounterId())) {
                MstCfetsCustomerInfo mstCfetsCustomerInfo = mstCfetsCustomerInfoMapper.selectByPrimaryKey(finDto.getCounterId());
                if (StringUtil.isNotEmpty(mstCfetsCustomerInfo)) {
                    List<MstCounterParty> mstCounterPartys = mstCounterPartyMapper.getCounterByName(mstCfetsCustomerInfo.getPartyName());
                    if (!CollectionUtils.isEmpty(mstCounterPartys)) {
                        MstCounterParty mstCounterParty = mstCounterPartys.get(0);
                        if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                            infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                        } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                            infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                        }
                    }
                }
            }
        }
        // add by zhangrui1 画面到期日是mdateAdd end
        // add by zhangrui1 基金申购使用净值1 start
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_基金)
                && CommonUtil.isType(finDto.getPs(), DictCodeList.交易方向_申购) && CommonUtil.isType(
                (String) dataMap.get("assetType1"), BussinessDictCodeList.基金类资产类型_货币基金)) {
            infRiskRdsDto.setTradeNum(infRiskRdsDto.getAmount());
        }
        // add by zhangrui1 基金申购使用净值1 end
        // add by zhangrui1 计划类货币型卖出指令金额数量字段名不同（entrustAmt2,entrustNum2） start
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_计划类资产_货币型)
                && CommonUtil.isType(infRiskRdsDto.getPs(), BussinessDictCodeList.交易方向_卖出)) {
            infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "entrustAmt2", infRiskRdsDto.getAmount()));
            infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "entrustNum2", infRiskRdsDto.getTradeNum()));
        }
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_计划类资产_货币型)) {
            if (null != infRiskRdsDto.getAmount() || null == infRiskRdsDto.getTradeNum()) {
                infRiskRdsDto.setTradeNum(infRiskRdsDto.getAmount());
            } else if (null == infRiskRdsDto.getAmount() || null != infRiskRdsDto.getTradeNum()) {
                infRiskRdsDto.setAmount(infRiskRdsDto.getTradeNum());
            }
        }

        if (StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), v.getAssetType())) {
            //设置是否是股东发行
            infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
        }
        // add by zhangrui1 计划类货币型卖出指令金额数量字段名不同（entrustAmt2,entrustNum2） end
        return infRiskRdsDto;
    }

    /**
     * 场内交易指令
     *
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    private InterfRiskRdsDto entrustExchangeEntityDev(Map<String, Object> dataMap, V v) {
        //定义返回实体
        InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
        EntrustBaseDto finDto = null;
        try {
            finDto = (EntrustBaseDto) JsonUtils.map2entity(dataMap, EntrustBaseDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("数据转换异常");
        }
        infRiskRdsDto.setEventId(finDto.getEntrustId());
        infRiskRdsDto.setEventDate(DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
        //设置母组合代码和子组合代码
        infRiskRdsDto.setPortfolioCode(AllTradeCommonUtil.exchangePortfolio(StringUtil.isNotEmpty(finDto.getPortfolioId()) ? finDto.getPortfolioId() : finDto.getActPortfolioId()));
        infRiskRdsDto.setAssetCode(finDto.getFinprodId());
        infRiskRdsDto.setPs(finDto.getPs());
        infRiskRdsDto.setAmount(finDto.getEntrustAmt());
        infRiskRdsDto.setTradeNum(finDto.getEntrustNum());
        infRiskRdsDto.setDeliveryDate(finDto.getMdate());
        Object secIdObj = CommonUtil.findValue(dataMap, "secId");
        if (StringUtil.isNotEmpty(secIdObj)) {
            infRiskRdsDto.setMarketCode(secIdObj.toString());
        }
        infRiskRdsDto.setTradePlace(finDto.getTradeMarket());
        infRiskRdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setMdate(finDto.getMdate());
        infRiskRdsDto.setInvAim(finDto.getInvAim());

        //债券
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType())) {
            if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                Object obj = CommonUtil.findValue(dataMap, "entrustPrice");
                if (StringUtil.isNotEmpty(obj)) {
                    infRiskRdsDto.setNetPrice(new BigDecimal(obj.toString()));//指令价格
                    infRiskRdsDto.setAmount(ArithmeticUtil.multiply(new BigDecimal(obj.toString()), infRiskRdsDto.getTradeNum()));
                }
            } else {
                //预占金额：全价*数量
                Object fprice = CommonUtil.findValue(dataMap, "fprice");
                if (StringUtil.isNotEmpty(fprice)) {
                    infRiskRdsDto.setAmount(ArithmeticUtil.multiply(new BigDecimal(fprice.toString()), infRiskRdsDto.getTradeNum()));
                }
                infRiskRdsDto.setNetPrice(finDto.getPrice());//净价
            }


            if (StringUtil.equals(finDto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            }


            infRiskRdsDto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            //将到期收益率除以100 
            Object obj = CommonUtil.findValue(dataMap, "matureYield");
            if (obj != null && StringUtil.isNotEmpty(obj)) {
                infRiskRdsDto.setYieldEndRate(ArithmeticUtil.divide(new BigDecimal(obj.toString()), new BigDecimal(100), 4));//到期收益率
            }

            //增加券面总额（元）
            infRiskRdsDto.setTotalFaceAmt(infRiskRdsDto.getAmount());

            //设置是否是股东发行
            infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
        }

        //质押式回购
        if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), v.getAssetType())) {
            String assetType2 = String.valueOf(CommonUtil.findValue(dataMap, "assetType2"));
            if (StringUtil.equals(assetType2, DictCodeList.回购类型_质押式协议回购.getValue())) {
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
                infRiskRdsDto.setAssetType2(finDto.getBusiPs());

                //新页面传的字段不一样，判断下当assetType2为空的时取repPs
                if (!StringUtil.isNotEmpty(infRiskRdsDto.getAssetType2())) {
                    infRiskRdsDto.setAssetType2(String.valueOf(CommonUtil.findValue(dataMap, "repPs")));
                }
                infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "tradeAmt", BigDecimal.ZERO));
                infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "tradeAmt", BigDecimal.ZERO));

                //新页面传的字段不一样，判断下当tradeAmt为空的时取repoAmt
                if (BigDecimal.ZERO.compareTo(CommonUtil.getBigDecimal(dataMap, "tradeAmt", BigDecimal.ZERO)) == 0) {
                    infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "repoAmt", BigDecimal.ZERO));
                    infRiskRdsDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "repoAmt", BigDecimal.ZERO));
                }
                infRiskRdsDto.setAssetRate(ArithmeticUtil.divide(CommonUtil.getBigDecimal(dataMap, "yield", BigDecimal.ZERO), new BigDecimal(100), 4));
                infRiskRdsDto.setAssetCode(null);
                infRiskRdsDto.setMarketCode(null);
                if (StringUtil.equals(infRiskRdsDto.getTradePlace(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                    infRiskRdsDto.setCalendarCode("04");
                } else {
                    infRiskRdsDto.setCalendarCode("05");
                }
                infRiskRdsDto.setMdate(DateUtil.addDay(finDto.getMdate(), CommonUtil.getBigDecimal(dataMap, "term", BigDecimal.ZERO).intValue()));
                infRiskRdsDto.setOneDay(CommonUtil.getBigDecimal(dataMap, "term", BigDecimal.ZERO).longValue());
                infRiskRdsDto.setCounterId(StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "counterId")) ? CommonUtil.findValue(dataMap, "counterId").toString() : null);
                //获取交易对手的市场机构代码
                if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                    MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(infRiskRdsDto.getCounterId());
                    if (StringUtil.isNotEmpty(mstCounterParty)) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                    }
                }
                //wangyang1 add by 增加期限金额 2021年9月10日 start
                if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(infRiskRdsDto.getOneDay())) {
                    infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(infRiskRdsDto.getOneDay())));
                }
                if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                    infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
                } else {
                    infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
                }
                //wangyang1 add by 增加期限金额 2021年9月10日 end
                return infRiskRdsDto;
            }
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setAssetRate(StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "entrustPrice")) ?
                    ArithmeticUtil.divide(new BigDecimal(CommonUtil.findValue(dataMap, "entrustPrice").toString()), new BigDecimal(100), 4) : null);
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);

            if (StringUtil.equals(infRiskRdsDto.getTradePlace(), DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                infRiskRdsDto.setCalendarCode("04");
            } else {
                infRiskRdsDto.setCalendarCode("05");
            }

            //竞价回购需要 根据期限品种计算MDate,上农商暂时没有协议回购。暂不做处理
            long term = CommonBusinessUtil.TERM_MAP.get(finDto.getFinprodId());
            Object vdateObject = dataMap.get("vdate");
            if (StringUtil.isNotEmpty(term) && StringUtil.isNotEmpty(vdateObject)) {
                infRiskRdsDto.setOneDay(term);

                Date vdate = new Date();
                //试算和提交时Vdate格式不一样
                try {
                    if (vdateObject instanceof Date) {
                        String vdateStr = DateUtil.format((Date) vdateObject, DateUtil.DEFAULT_DATE_FORMAT_EN);
                        vdate = DateUtil.parse(vdateStr);
                    } else {
                        if (DateUtil.isDateStr((String) vdateObject)) {
                            vdate = DateUtil.parse((String) vdateObject);
                        }
                    }
                } catch (Exception e) {
                    vdate = new Date((long) vdateObject);
                }
                Date mDate = DateUtil.addDays(vdate, (int) term);
                List<String> holiday = dateCalService.getDealCalendarHoliday(vdate,
                        mDate, infRiskRdsDto.getCalendarCode());
                Date tradingDay = dateCalService.calculationTradingDay(mDate, 1, holiday, "1");

                infRiskRdsDto.setMdate(tradingDay);
            }


        }
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_质押式回购)) {
            long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                    DateUtil.parse(infRiskRdsDto.getMdate()));
            infRiskRdsDto.setOneDay(oneDay);
            //wangyang1 add by 增加期限金额 2021年9月10日 start
            if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(oneDay)) {
                infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(oneDay)));
            }
            if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
            } else {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
            }
            //wangyang1 add by 增加期限金额 2021年9月10日 end
        }
        //股票
        if (StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), v.getAssetType())) {
            if (StringUtil.equals(finDto.getPs(), DictCodeListExt2.交易方向_配股.getValue())) {
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            }
        }
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_基金)) {
            if (StringUtil.isNotEmpty(finDto.getMdate())) {
                infRiskRdsDto.setMdate(null);
            }
        }
        return infRiskRdsDto;
    }

    /**
     * 交易实体封装
     *
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */

    private InterfRiskRdsDto tradeEntityDev(Map<String, Object> dataMap, V v) {
        //定义返回实体
        InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
        TradeBaseDto finDto = null;
        try {
            finDto = (TradeBaseDto) JsonUtils.map2entity(dataMap, TradeBaseDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("数据转换异常");
        }
        finDto.setTotleTradeId(removeLasStr(finDto.getTotleTradeId()));
        finDto.setTradeId(removeLasStr(finDto.getTradeId()));
        infRiskRdsDto.setEventId(StringUtil.isNotEmpty(finDto.getTotleTradeId()) ? finDto.getTotleTradeId() : StringUtil.isNotEmpty(finDto.getTradeId()) ? finDto.getTradeId() : finDto.getBusiCode());
        infRiskRdsDto.setEventDate(DateUtil.format(finDto.getTradeDate(), DateUtil.DEFAULT_DATE_FORMAT_EN));
        //设置母组合代码
        infRiskRdsDto.setPortfolioCode(AllTradeCommonUtil.exchangePortfolio(StringUtil.isNotEmpty(finDto.getPortfolioId()) ? finDto.getPortfolioId() : finDto.getActPortfolioId()));
        infRiskRdsDto.setAssetCode(StringUtil.equals(finDto.getFinprodId(), infRiskRdsDto.getEventId()) ? null : finDto.getFinprodId());//排除不是标的资产，比如回购，拆借，外汇等
        infRiskRdsDto.setPs(finDto.getPs());
        infRiskRdsDto.setAmount(finDto.getTradeAmt());//项目类,基金，计划类申购,股票
        infRiskRdsDto.setTradeNum(finDto.getShareAmt());
        infRiskRdsDto.setDeliveryDate(finDto.getSettleDate());
        infRiskRdsDto.setMarketCode(StringUtil.equals(finDto.getFinprodId(), infRiskRdsDto.getEventId()) ? null : finDto.getFinprodId());
        infRiskRdsDto.setTradePlace(finDto.getTradeMarket());
        infRiskRdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setMdate(finDto.getMdate());
        infRiskRdsDto.setInvAim(finDto.getInvAim());
        // add by zhangrui1 拆借回购等没有资产的需要传日历代码 start
        if (!StringUtil.isEmpty(finDto.getCalendarId())) {
            infRiskRdsDto.setCalendarCode(finDto.getCalendarId());
        }
        // add by zhangrui1 拆借回购等没有资产的需要传日历代码 end
        infRiskRdsDto.setWithLockPeriod(finDto.getWithLockPeriod());
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType())) {
            //infRiskRdsDto.setAmount(finDto.getFpriceAmt());//债券为全价总额存款、计划类利率型、项目类资产：实付金额（元）基金（申购）、净值型计划类资产：申购金额（元）货币型计划类资产（申购）：申购金额（元）回购、拆借：本金金额（元）
            Object tradeAmtAndFee = CommonUtil.findValue(dataMap, "tradeAmtAndFee");//交易金额
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(tradeAmtAndFee) ? new BigDecimal(tradeAmtAndFee.toString()) : finDto.getFpriceAmt());//若无交易金额用全价总额
            infRiskRdsDto.setAmountEx(finDto.getCpriceAmt());//净价总额(成本)

            // modify by zhangrui1 20200803 start
            //infRiskRdsDto.setTradeNum(finDto.getTradeAmt());//债券：券面总额（张）基金（赎回）：赎回份额（份）货币型计划类资产（赎回）：赎回份额（份）正逆回购:首期结算金额
            infRiskRdsDto.setTradeNum(
                    ArithmeticUtil.divide(finDto.getCpriceAmt(), finDto.getUnitCprice(), 4));
            // modify by zhangrui1 20200803 start
            infRiskRdsDto.setNetPrice(finDto.getUnitCprice());//债券单位净价,其它不填
            infRiskRdsDto.setAllPrice(finDto.getUnitFprice());//债券单位全价,其它不填
            //将到期收益率除以100 update by wz 20200430
            infRiskRdsDto.setYieldEndRate(ArithmeticUtil.divide(finDto.getYield(), new BigDecimal(100), 4));//到期收益率
            infRiskRdsDto.setDeliveryDate(finDto.getSettleDate());
            infRiskRdsDto.setTradeMarketMode(finDto.getTradeMarketMode());
            //可转债
            if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_申购.getValue())) {
                infRiskRdsDto.setAmount(finDto.getTradeAmt());//可转债为申购券面总额
                infRiskRdsDto.setTradeNum(finDto.getTradeAmt());//可转债为申购券面总额
            } else if (StringUtil.equals(finDto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())) {
                infRiskRdsDto.setAmount(finDto.getShareAmt());//可转债为申购券面总额
            }
            // add by zhangrui1 增加券面总额（元） start
            // 债券可用面值 * 交易数量
            infRiskRdsDto.setTotalFaceAmt(
                    ArithmeticUtil.multiply(null == finDto.getParValue() ? new BigDecimal(100) : finDto.getParValue(), infRiskRdsDto.getTradeNum()));
            // add by zhangrui1 增加券面总额（元） end

            //设置是否是股东发行
            infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
        }
        //存款类
        if (StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("tradeAmtAndFee")) ? dataMap.get("tradeAmtAndFee") instanceof BigDecimal ? (BigDecimal) dataMap.get("tradeAmtAndFee") : new BigDecimal(dataMap.get("tradeAmtAndFee").toString()) : null);
        }
        //固收类(非标)
        if (StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), v.getAssetType())) {
            infRiskRdsDto.setEventId(StringUtil.isNotEmpty(finDto.getTotleTradeId()) ? finDto.getTotleTradeId() : StringUtil.isNotEmpty(dataMap.get("bizCode")) ? dataMap.get("bizCode").toString() : finDto.getTradeId());
            infRiskRdsDto.setTradeNum(finDto.getPrinAmt());

            //设置是否是股东发行
            infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
        }
        //基金，计划类
        if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            infRiskRdsDto.setAmount(finDto.getShareAmt());
        }
        if (StringUtil.equals(finDto.getBusiPs(), DictCodeList.回购拆借方向_拆入.getValue()) ||
                StringUtil.equals(finDto.getBusiPs(), DictCodeList.回购拆借方向_拆出.getValue())) {
            Map priObj = StringUtil.isNotEmpty(dataMap.get("finTradeProduct")) ? ((Map) dataMap.get("finTradeProduct")) : null;
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(priObj) ? priObj.get("prin") instanceof BigDecimal ? (BigDecimal) priObj.get("prin") : new BigDecimal(priObj.get("prin").toString()) : null);//净值型计划类资产：申购金额（元）货币型计划类资产（申购）：申购金额（元）回购、拆借：本金金额（元）
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_拆借.getValue());
            infRiskRdsDto.setAssetType2(finDto.getBusiPs());
            if (StringUtil.isNotEmpty(priObj.get("mdate"))) {
                Date mdate;
                if (priObj.get("mdate") instanceof Long) {
                    mdate = new Date(Long.parseLong(priObj.get("mdate").toString()));
                } else {
                    mdate = DateUtil.parse(priObj.get("mdate").toString());
                }
                long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(), mdate);
                infRiskRdsDto.setBetweenDate(oneDay);
            }
            infRiskRdsDto.setCalendarCode((String) CommonUtil.findValue(dataMap, "calendarId"));
            infRiskRdsDto.setMdate(StringUtil.isNotEmpty(priObj) ? RiskUtil.isInvalidDate(priObj.get("mdate")) ? DateUtil.parse(priObj.get("mdate").toString()) : new Date(Long.parseLong(priObj.get("mdate").toString())) : null);
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAmount(finDto.getPrinAmt());//回购、拆借：本金金额（元）
            //回购利率
            infRiskRdsDto.setAssetRate(StringUtil.isNotEmpty(dataMap.get("rebackPrice")) ? dataMap.get("rebackPrice") instanceof BigDecimal ? ArithmeticUtil.divide((BigDecimal) dataMap.get("rebackPrice"), new BigDecimal(100), 4) : ArithmeticUtil.divide(new BigDecimal(dataMap.get("rebackPrice").toString()), new BigDecimal(100), 4) : null);
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setCounterId(StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "counterId")) ? CommonUtil.findValue(dataMap, "counterId").toString() : null);
            //添加期限品种 add by wz 20200509
            //infRiskRdsDto.setVarietyCode(StringUtil.equals(finDto.getTradeMarket(),DictCodeListExt1.交易场所_银行间.getValue())?"D"+(String) dataMap.get("termTypeReback"):(String) dataMap.get("termTypeReback"));
            infRiskRdsDto.setVarietyCode((String) dataMap.get("termTypeReback"));
            if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(infRiskRdsDto.getCounterId());
                if (StringUtil.isNotEmpty(mstCounterParty)) {
                    if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                    } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                    }
                } else {
                    MstCfetsCustomerInfo mstCfetsCustomerInfo = mstCfetsCustomerInfoMapper.selectByPrimaryKey(infRiskRdsDto.getCounterId());
                    if (StringUtil.isNotEmpty(mstCfetsCustomerInfo)) {
                        List<MstCounterParty> mstCounterPartys = mstCounterPartyMapper.getCounterByName(mstCfetsCustomerInfo.getPartyName());
                        if (!CollectionUtils.isEmpty(mstCounterPartys)) {
                            mstCounterParty = mstCounterPartys.get(0);
                            if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                                infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                            } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                                infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                            }
                        }
                    }
                }
            }
            if (StringUtil.isNotEmpty(infRiskRdsDto.getMdate())) {
                long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                        DateUtil.parse(infRiskRdsDto.getMdate()));
                infRiskRdsDto.setOneDay(oneDay);
                if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(oneDay)) {
                    infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(oneDay)));
                }
            }
            if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
            } else {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
            }

            infRiskRdsDto.setCalendarCode((String) CommonUtil.findValue(dataMap, "calendarId"));
            if (StringUtil.isNotEmpty(finDto.getMdate())) {
                infRiskRdsDto.setRepoTerm(BigDecimal.valueOf(DateUtil.getBetweenDayNumber(finDto.getTradeDate(), finDto.getMdate())));
            }
            //添加抵押品
            if (finDto.getBussRepoSec() != null && finDto.getBussRepoSec().getPledgeBondList() != null) {
                infRiskRdsDto.setPledgedCode(finDto.getBussRepoSec().getPledgeBondList()
                        .stream()
                        .map(PledgeBondInfoDto::getFinprodId).distinct()
                        .collect(Collectors.joining(RiskUtil.UP_LINE)));
            }
        }
        if (StringUtil.equals(finDto.getPs(), DictCodeList.回购拆借方向_融入.getValue()) ||
                StringUtil.equals(finDto.getPs(), DictCodeList.回购拆借方向_融出.getValue())) {
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("mTradeAmt")) ? dataMap.get("mTradeAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("mTradeAmt") : new BigDecimal(dataMap.get("mTradeAmt").toString()) : null);
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            infRiskRdsDto.setCalendarCode((String) dataMap.get("calendarId"));
            infRiskRdsDto.setDeadline(StringUtil.isNotEmpty(dataMap.get("term")) ? new BigDecimal(dataMap.get("term").toString()) : null);
            if (StringUtil.isNotEmpty(dataMap.get("mdate"))) {
                Date mdate;
                if (dataMap.get("mdate") instanceof Long) {
                    mdate = new Date(Long.parseLong(dataMap.get("mdate").toString()));
                } else {
                    mdate = DateUtil.parse(dataMap.get("mdate").toString());
                }
                long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(), mdate);
                infRiskRdsDto.setBetweenDate(oneDay);
            }
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_买断式回购.getValue());
            infRiskRdsDto.setAssetType2(finDto.getPs());
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            infRiskRdsDto.setAmount(finDto.getfSettleAmt());//债券为全价总额存款、计划类利率型、项目类资产：实付金额（元）基金（申购）、净值型计划类资产：申购金额（元）货币型计划类资产（申购）：申购金额（元）回购、拆借：本金金额（元）
            infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty(dataMap.get("secTotalPrin")) ? dataMap.get("secTotalPrin") instanceof BigDecimal ? (BigDecimal) dataMap.get("secTotalPrin") : new BigDecimal(dataMap.get("secTotalPrin").toString()) : null);
            infRiskRdsDto.setDeliveryDate(finDto.getTradeDate());
            //回购利率
            infRiskRdsDto.setAssetRate(StringUtil.isNotEmpty(dataMap.get("repoRate")) ? dataMap.get("repoRate") instanceof BigDecimal ? ArithmeticUtil.divide((BigDecimal) dataMap.get("repoRate"), new BigDecimal(100), 4) : ArithmeticUtil.divide(new BigDecimal(dataMap.get("repoRate").toString()), new BigDecimal(100), 4) : null);
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            //添加期限品种 add by wz 20200509
            //infRiskRdsDto.setVarietyCode(StringUtil.equals(finDto.getTradeMarket(),DictCodeListExt1.交易场所_银行间.getValue())?"D"+(String) dataMap.get("termTypeReback"):(String) dataMap.get("termTypeReback"));
            infRiskRdsDto.setVarietyCode((String) dataMap.get("termTypeReback"));

            infRiskRdsDto.setCalendarCode((String) CommonUtil.findValue(dataMap, "dealCalId"));
            if (StringUtil.isNotEmpty(finDto.getMdate())) {
                infRiskRdsDto.setRepoTerm(BigDecimal.valueOf(DateUtil.getBetweenDayNumber(finDto.getTradeDate(), finDto.getMdate())));
            }
            //添加抵押品
            infRiskRdsDto.setPledgedCode(finDto.getFinprodId());
        }
        if (StringUtil.equals(DictCodeListExt2.平安资产类型_一级市场债.getValue(), v.getAssetType())) {
            if (CommonUtil.isType(v.getEventStatus(), DictCodeListExt4.共通业务类型_中标结果维护自主投标) ||
                    CommonUtil.isType(v.getEventStatus(), DictCodeListExt4.共通业务类型_中标结果维护代投代缴)) {
                infRiskRdsDto.setAmount(CommonUtil.getBigDecimal(dataMap, "totalBiddingNum", BigDecimal.ZERO));//汇总券面总额
                infRiskRdsDto.setTradeNum(
                        ArithmeticUtil.divide(infRiskRdsDto.getAmount(), new BigDecimal(100), 4));
                // add by zhangrui1 汇总确认 end
                infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                //一级市场债两个service用的DTO里的中标日字段不一样，大小写不一致。。
                if (StringUtil.isNotEmpty(dataMap.get("bidDate"))) {
                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("bidDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                } else if (StringUtil.isNotEmpty(dataMap.get("payDate"))) {
                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("payDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                }
                infRiskRdsDto.setEventId(StringUtil.isNotEmpty(dataMap.get("bidId")) ? dataMap.get("bidId").toString() : StringUtil.EMPTY);
                //设置是否是股东发行
                infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
            }else if (CommonUtil.isType(v.getEventStatus(), DictCodeListExt4.共同业务类型_一级投标确认_申购_登记)) {
                infRiskRdsDto.setAmount(finDto.getFpriceAmt());
                infRiskRdsDto.setTradeNum(finDto.getShareAmt());
                infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
//                if (StringUtil.isNotEmpty(dataMap.get("tradeDate"))) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("tradeDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
//                } else if (StringUtil.isNotEmpty(dataMap.get("settleDate"))) {
//                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("settleDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
//                }
                infRiskRdsDto.setEventId(StringUtil.isNotEmpty(dataMap.get("tradeId")) ? dataMap.get("tradeId").toString() : StringUtil.EMPTY);
                //设置是否是股东发行
                infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
            }else{
                infRiskRdsDto.setAmount(finDto.getFpriceAmt());//债券为全价总额存款
                // add by zhangrui1 汇总确认 start
                String winPrinAmtKey = "bidPrinAmt";
                Object winPrinAmt = dataMap.get(winPrinAmtKey);
                if (infRiskRdsDto.getAmount() == null && winPrinAmt != null) {
                    infRiskRdsDto.setAmount(winPrinAmt instanceof BigDecimal ? (BigDecimal) winPrinAmt : new BigDecimal(
                            winPrinAmt.toString()));
                }
                infRiskRdsDto.setTradeNum(
                        ArithmeticUtil.divide(infRiskRdsDto.getAmount(), new BigDecimal(100), 4));
                // add by zhangrui1 汇总确认 end
                infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_一级市场债.getValue());
                infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
                //一级市场债两个service用的DTO里的中标日字段不一样，大小写不一致。。
                if (StringUtil.isNotEmpty(dataMap.get("biddate"))) {
                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("biddate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                } else if (StringUtil.isNotEmpty(dataMap.get("bidDate"))) {
                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("bidDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                } else if (StringUtil.isNotEmpty(dataMap.get("payDate"))) {
                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("payDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                }
                infRiskRdsDto.setEventId(StringUtil.isNotEmpty(dataMap.get("bidId")) ? dataMap.get("bidId").toString() : StringUtil.EMPTY);
                //设置是否是股东发行
                infRiskRdsDto.setIsShareHolder(riskRdsTrialCommonMapper.getIsShareholderIssue(dataMap));
            }
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAmount(finDto.getPrin() == null ? BigDecimal.ZERO : finDto.getPrin().multiply(new BigDecimal(10000)));//债券为全价总额存款、
            infRiskRdsDto.setTradeNum(finDto.getPrin() == null ? BigDecimal.ZERO : finDto.getPrin().multiply(new BigDecimal(10000)));
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_利率互换.getValue());
            infRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_期权.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt3.平安资产类型_期权.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAssetType(DictCodeListExt3.平安资产类型_期权.getValue());
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("optFee")) ? dataMap.get("optFee") instanceof BigDecimal ? (BigDecimal) dataMap.get("optFee") : new BigDecimal(dataMap.get("optFee").toString()) : null);
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_掉期.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_外汇掉期.getValue());
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("nDealCurAmt")) ? dataMap.get("nDealCurAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("nDealCurAmt") : new BigDecimal(dataMap.get("nDealCurAmt").toString()) : null);
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_远期.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt2.平安资产类型_外汇远期.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_外汇远期.getValue());
            infRiskRdsDto.setAmount(finDto.getPrinAmt());
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_即期.getValue(), v.getFinType()) ||
                StringUtil.equals(DictCodeListExt2.平安资产类型_外汇即期.getValue(), v.getAssetType())) {
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_外汇即期.getValue());
            infRiskRdsDto.setAmount(finDto.getPrinAmt());
        }

        // 2020-06-17 add by zhangrui1 start
        // 卖出将本金金额设置为数量进行风险计算
        if ((CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_计划类资产_利率型) ||
                CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_存款) ||
                CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_项目类资产)) &&
                (CommonUtil.isType(finDto.getPs(), DictCodeList.交易方向_卖出)
                        || CommonUtil.isType(finDto.getPs(), DictCodeList.交易方向_买入))) {
            String returnAmtKey = "prinAmt";
            Object obj = CommonUtil.findValue(dataMap, returnAmtKey);
            if (StringUtil.isNotEmpty(obj)) infRiskRdsDto.setTradeNum(new BigDecimal(obj.toString()));
        }
        // 2020-06-17 add by zhangrui1 end

        // 预占功能 add by zhangrui1 start
        // 设置委托编号
        infRiskRdsDto.setEntrustCode(finDto.getEntrustId());
        infRiskRdsDto.setPreOccupyCode(finDto.getEntrustId());
        // 预占功能 add by zhangrui1 end
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_质押式回购)) {
            long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                    DateUtil.parse(infRiskRdsDto.getMdate()));
            infRiskRdsDto.setOneDay(oneDay);
            //wangyang1 add by 增加期限金额 2021年9月10日 start
            if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(oneDay)) {
                infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(oneDay)));
            }
            if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
            } else {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
            }
            //wangyang1 add by 增加期限金额 2021年9月10日 end
            if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(infRiskRdsDto.getCounterId());
                if (StringUtil.isNotEmpty(mstCounterParty)) {
                    if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                    } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                    }
                } else {
                    MstCfetsCustomerInfo mstCfetsCustomerInfo = mstCfetsCustomerInfoMapper.selectByPrimaryKey(infRiskRdsDto.getCounterId());
                    if (StringUtil.isNotEmpty(mstCfetsCustomerInfo)) {
                        List<MstCounterParty> mstCounterPartys = mstCounterPartyMapper.getCounterByName(mstCfetsCustomerInfo.getPartyName());
                        if (!CollectionUtils.isEmpty(mstCounterPartys)) {
                            mstCounterParty = mstCounterPartys.get(0);
                            if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                                infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                            } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                                infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                            }
                        }
                    }
                }
            }
        }
        if (StringUtil.isEmpty(infRiskRdsDto.getEventDate())) {
            infRiskRdsDto.setEventDate(
                    DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
        }
        // add by zhangrui1 一级分销缴款 start
        if (StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "bondFirstSign"))) {
            if (!CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_风险试算接口)
                    && !CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_风险试算批量接口)) {
                infRiskRdsDto.setPreOccupyCode(null);
            }
//            if (CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_风险试算接口)
//                || CommonUtil.isType(v.getInfCode(), BussinessDictCodeListRds.接口系统ID_风险试算批量接口))
//                infRiskRdsDto.setIsDiffOccupy(BussinessDictCodeList.是否_是.getValue());
        }
        // add by zhangrui1 一级分销缴款 end
        // add by zhangrui1 基金申购使用净值1 start
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_基金)
                && CommonUtil.isType(finDto.getPs(), DictCodeList.交易方向_申购) && CommonUtil.isType(
                (String) dataMap.get("assetType1"), BussinessDictCodeList.基金类资产类型_货币基金)) {
            infRiskRdsDto.setTradeNum(infRiskRdsDto.getAmount());
        }
        // add by zhangrui1 基金申购使用净值1 end
        // add by zhangrui1 赎回确认日期不在指令日期时不使用指令编号 start
        if (CommonUtil.isType(finDto.getPs(), DictCodeList.交易方向_赎回确认)) {
            Date entrsutDate = DateUtil.parse(
                    (String) CommonUtil.findValue(dataMap, "entrustDate"));
            Date tradeDate = DateUtil.parse(infRiskRdsDto.getEventDate());
            Date sysDate = DateUtil.platDateFromCache();
            // 指令日期不是当天且指令日期不等于交易日期
            if (null != entrsutDate && DateUtil.compareDate(sysDate, entrsutDate) != 0
                    && DateUtil.compareDate(tradeDate, entrsutDate) != 0) {
                infRiskRdsDto.setPreOccupyCode(null);
            } else if (null != entrsutDate
                    && StringUtil.isEmpty(finDto.getEntrustId())) {
                EtrEntrust entrust = AllTradeCommonUtil.findEntrustInfo(finDto.getbTradeId());
                if (entrust == null || StringUtil.isEmpty(entrust.getEntrustId())) {
                    throw new BusinessException("MSG:指令编号异常！");
                }
                infRiskRdsDto.setEntrustCode(entrust.getEntrustId());
                infRiskRdsDto.setPreOccupyCode(entrust.getEntrustId());
            }
            // TISHKS-1012:entrsutDate为null没有取到对应的委托预占数据 modify by fuyongsheng 20220506 start
            else if (null == entrsutDate) {
                EtrEntrust entrust = AllTradeCommonUtil.findEntrustInfo(finDto.getbTradeId());
                if (entrust != null && StringUtil.isNotEmpty(entrust.getEntrustId())) {
                    infRiskRdsDto.setEntrustCode(entrust.getEntrustId());
                    infRiskRdsDto.setPreOccupyCode(entrust.getEntrustId());
                }
            }
            // TISHKS-1012:entrsutDate为null没有取到对应的委托预占数据 modify by fuyongsheng 20220506 end
        }
        // add by zhangrui1 赎回确认日期不在指令日期时不使用指令编号 end
        // add by zhangrui1 货币型资产净值为1 start
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_计划类资产_货币型)) {
            if (null != infRiskRdsDto.getAmount() || null == infRiskRdsDto.getTradeNum()) {
                infRiskRdsDto.setTradeNum(infRiskRdsDto.getAmount());
            } else if (null == infRiskRdsDto.getAmount() || null != infRiskRdsDto.getTradeNum()) {
                infRiskRdsDto.setAmount(infRiskRdsDto.getTradeNum());
            }
        }
        // add by zhangrui1 货币型资产净值为1 end
        return infRiskRdsDto;
    }

    /**
     * 交易实体封装
     *
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    private InterfRiskRdsDto periodEntityDev(Map<String, Object> dataMap, V v) throws IOException {
        //定义返回实体
        InterfRiskRdsDto infRiskRdsDto = new InterfRiskRdsDto();
        TradeBaseDto finDto = null;
        try {
            finDto = (TradeBaseDto) JsonUtils.map2entity(dataMap, TradeBaseDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("数据转换异常");
        }
        finDto.setBusiCode(removeLasStr(finDto.getBusiCode()));
        finDto.setTotleTradeId(removeLasStr(finDto.getTotleTradeId()));
        infRiskRdsDto.setEventDate(StringUtil.isNotEmpty(finDto.getmDate()) ? finDto.getmDate() : DateUtil.format(finDto.getMdate(), DateUtil.DEFAULT_DATE_FORMAT_EN));
        //设置母组合代码和子组合代码
        infRiskRdsDto.setPortfolioCode(AllTradeCommonUtil.exchangePortfolio(StringUtil.isNotEmpty(finDto.getPortfolioId()) ? finDto.getPortfolioId() : finDto.getActPortfolioId()));
        infRiskRdsDto.setEventId(StringUtil.isNotEmpty(finDto.getBusiCode()) ? finDto.getBusiCode() + trimInfStr(infRiskRdsDto.getPortfolioCode()) : finDto.getTotleTradeId() + trimInfStr(infRiskRdsDto.getPortfolioCode()));
        infRiskRdsDto.setAssetCode(finDto.getFinprodId());
        infRiskRdsDto.setAssetType(v.getAssetType());
        infRiskRdsDto.setPs(finDto.getPs());
        infRiskRdsDto.setAmount(finDto.getTradeAmt());//项目类,基金，计划类申购,股票
        infRiskRdsDto.setDeliveryDate(finDto.getSettleDate());
        infRiskRdsDto.setMarketCode(finDto.getFinprodId());
        infRiskRdsDto.setTradePlace(finDto.getTradeMarket());
        infRiskRdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
        infRiskRdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
        //add by quzhi 存续期交易增加到期日 begin
        infRiskRdsDto.setMdate(StringUtil.isNotEmpty(finDto.getmDate()) ? DateUtil.parse(finDto.getmDate()) : finDto.getMdate());
        //add by quzhi 存续期交易增加到期日 end
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType()) || StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), v.getAssetType())) {
            infRiskRdsDto.setEventId(StringUtil.isNotEmpty(finDto.getBusiCode()) ? finDto.getBusiCode() + trimInfStr(infRiskRdsDto.getPortfolioCode()) + trimInfStr(finDto.getInvAim()) : finDto.getTotleTradeId() + trimInfStr(infRiskRdsDto.getPortfolioCode()) + trimInfStr(finDto.getInvAim()));
            //债券存续期中中间付息，还本，到期使用的是TradeAmt(实收本息总额)，FpriceAmt是理论本息总额
            infRiskRdsDto.setAmountEx(finDto.getPrinAmt());
            infRiskRdsDto.setAmount(finDto.getFpriceAmt());//债券为全价总额存款、计划类利率型、项目类资产：本金金额（元）基金（申购）、
            infRiskRdsDto.setDeliveryDate(finDto.getSettleDate());
            if (finDto.getPs().equals(DictCodeList.交易方向_行权_赎回.getValue())) {
                infRiskRdsDto.setEventId(StringUtil.isNotEmpty(dataMap.get("bizCode")) ? dataMap.get("bizCode").toString() + trimInfStr(infRiskRdsDto.getPortfolioCode()) + trimInfStr(finDto.getInvAim()) : finDto.getTotleTradeId() + trimInfStr(infRiskRdsDto.getPortfolioCode()) + trimInfStr(finDto.getInvAim()));
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("cPriceAmt")) ? dataMap.get("cPriceAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("cPriceAmt") : new BigDecimal(dataMap.get("cPriceAmt").toString()) : null);
                // add by zhangrui1 start
                // 债券行权回售用券面总额作数量
                Object tradeNumObj = dataMap.get("qty");
                if (tradeNumObj != null) {
                    infRiskRdsDto.setTradeNum(tradeNumObj instanceof BigDecimal ? (BigDecimal) tradeNumObj : new BigDecimal(tradeNumObj.toString()));
                    infRiskRdsDto.setTradeNum(ArithmeticUtil.divide(infRiskRdsDto.getTradeNum(), new BigDecimal(100), 4));
                }
                // add by zhangrui1 end
            }
            // 到期也使用全面总额
            else if (CommonUtil.isType(finDto.getPs(), DictCodeList.交易方向_到期)) {
                Object tradeNumObj = dataMap.get("qty");
                if (tradeNumObj != null) {
                    infRiskRdsDto.setTradeNum(tradeNumObj instanceof BigDecimal ? (BigDecimal) tradeNumObj : new BigDecimal(tradeNumObj.toString()));
                    infRiskRdsDto.setTradeNum(ArithmeticUtil.divide(infRiskRdsDto.getTradeNum(), new BigDecimal(100), 4));
                }
            }
            // add by zhangrui1 增加券面总额（元） start
            Object tradeNumObj = dataMap.get("qty");
            if (tradeNumObj != null) {
                infRiskRdsDto.setTotalFaceAmt(tradeNumObj instanceof BigDecimal ? (BigDecimal) tradeNumObj : new BigDecimal(tradeNumObj.toString()));
            }
            // add by zhangrui1 增加券面总额（元） end
        }
        if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finDto.getFinprodType2()) ||
                StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), v.getAssetType()))
                && StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_中间付息.getValue())) {
            infRiskRdsDto.setEventDate(DateUtil.format(finDto.getTradeDate()));
            String intSettType = (String) dataMap.get("intSettType");
            if (StringUtil.isNotEmpty(intSettType)) {
                if (StringUtil.equals(intSettType, DictCodeListExt3.结息方式_结转为本金.getValue())) {
                    infRiskRdsDto.setPs(DictCodeList.交易方向_红利再投.getValue());
                } else if (StringUtil.equals(intSettType, DictCodeListExt3.结息方式_现金结息.getValue())) {
                    infRiskRdsDto.setPs(DictCodeList.交易方向_收息.getValue());
                }
            }
        }
        if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finDto.getFinprodType2()) ||
                StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), v.getAssetType()))
                && StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_提前支取.getValue())) {
            infRiskRdsDto.setEventDate(DateUtil.format(finDto.getTradeDate()));
            // add by zhangrui1 提前支取金额当作数量 start
            Object earlyObject = dataMap.get("earlyWithdrawalAmt");
            infRiskRdsDto.setTradeNum(earlyObject instanceof BigDecimal ? (BigDecimal) earlyObject : new BigDecimal(earlyObject.toString()));
            infRiskRdsDto.setAmount(finDto.getFpriceAmt());
            // add by zhangrui1 提前支取金额当作数量 end
        }
        if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_货币型.getValue(), v.getAssetType())
                || StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), v.getAssetType())
                || StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), v.getAssetType())
                || StringUtil.equals(DictCodeListExt3.平安资产类型_权益类资产.getValue(), v.getAssetType())) {
            if (finDto.getPs().equals(DictCodeListExt2.交易方向_份额折算.getValue())) {
                if (StringUtil.isNotEmpty(dataMap.get("conversionTime"))) {
                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("conversionTime").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                }
            }
            //红利再投，除权登记不影响直接退出(撤销时因为撤销的是除权登记交易，所以需要增加判断)
            if (finDto.getPs().equals(DictCodeList.交易方向_红利再投.getValue())
                    || (finDto.getPs().equals(DictCodeList.交易方向_除权登记.getValue())
                    && !StringUtil.equals(BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(), v.getInfCode()))) {
                return null;
            }
            //因为撤销操作使用的是除权登记的交易编号，正向分红交易时使用前置交易编号作为关联（非货币基金、权益类生成分红与除权，但货币只生成分红）
            if (StringUtil.equals(BussinessDictCodeListRds.接口系统ID_实占释放接口.getValue(), v.getInfCode())) {
                if ((finDto.getPs().equals(DictCodeList.交易方向_现金分红.getValue())
                        || (finDto.getPs().equals(DictCodeList.交易方向_除权登记.getValue())))
                        && StringUtil.isNotEmpty(dataMap.get("arriveDate"))) {
                    if (StringUtils.isNumeric(dataMap.get("arriveDate").toString())) {
                        infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("arriveDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                    } else {
                        infRiskRdsDto.setEventDate(dataMap.get("arriveDate").toString());
                    }

//                    infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("arriveDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                }
            } else {
                if (finDto.getPs().equals(DictCodeList.交易方向_现金分红.getValue())) {
                    if (StringUtil.isNotEmpty(dataMap.get("bTradeId"))) {
                        infRiskRdsDto.setEventId(dataMap.get("bTradeId").toString() + trimInfStr(infRiskRdsDto.getPortfolioCode()));
                    }
                    //add 20220607修改时间格式 wsz ZGCPCN-1157 start
                    if (StringUtil.isNotEmpty(dataMap.get("arriveDate"))) {
                        if (StringUtils.isNumeric(dataMap.get("arriveDate").toString())) {
                            infRiskRdsDto.setEventDate(DateUtil.format(new Date(Long.parseLong(dataMap.get("arriveDate").toString())), DateUtil.DEFAULT_DATE_FORMAT_EN));
                        } else {
                            infRiskRdsDto.setEventDate(dataMap.get("arriveDate").toString());
                        }
                    }
                    //add 20220607修改时间格式 wsz ZGCPCN-1157 end
                }
            }

        }
        if (StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue()) ||
                StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue()) ||
                StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆出.getValue())) {
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_拆借.getValue());
            infRiskRdsDto.setAssetType2(StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_拆入.getValue()) || StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue()) ? DictCodeList.回购拆借方向_拆入.getValue() : DictCodeList.回购拆借方向_拆出.getValue());
            infRiskRdsDto.setPs(DictCodeList.交易方向_到期.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);

            infRiskRdsDto.setCalendarCode(AssetCommonUtil.getAssetInfo(finDto.getFinprodId(), obj -> obj.getCalendarCode()));
        }
        if (StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) ||
                StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
            infRiskRdsDto.setAmount(finDto.getFpriceAmt());//回购、拆借：到期金额（元）
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            infRiskRdsDto.setAssetType2(StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) ? DictCodeList.回购拆借方向_正回购.getValue() : DictCodeList.回购拆借方向_逆回购.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
            if (StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "mDate"))) {
                infRiskRdsDto.setMdate(DateUtil.parse(CommonUtil.findValue(dataMap, "mDate").toString()));
            }
            if (StringUtil.isNotEmpty(CommonUtil.findValue(dataMap, "counterId"))) {
                String counterId = CommonUtil.findValue(dataMap, "counterId").toString();
                MstCounterParty mstCounterParty = mstCounterPartyMapper.selectByPrimaryKey(counterId);
                if (StringUtil.isNotEmpty(mstCounterParty)) {
                    if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                    } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                        infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                    }
                } else {
                    MstCfetsCustomerInfo mstCfetsCustomerInfo = mstCfetsCustomerInfoMapper.selectByPrimaryKey(counterId);
                    if (StringUtil.isNotEmpty(mstCfetsCustomerInfo)) {
                        List<MstCounterParty> mstCounterPartys = mstCounterPartyMapper.getCounterByName(mstCfetsCustomerInfo.getPartyName());
                        if (!CollectionUtils.isEmpty(mstCounterPartys)) {
                            mstCounterParty = mstCounterPartys.get(0);
                            if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_市场机构.getValue())) {
                                infRiskRdsDto.setIssuerCode(mstCounterParty.getLinkId());
                            } else if (StringUtil.equals(mstCounterParty.getCounterType(), DictCodeListExt1.交易对手类型_内部交易对手.getValue())) {
                                infRiskRdsDto.setIssuerCode(mstCounterParty.getManagerId());
                            }
                        }
                    }
                }
                if (StringUtil.isNotEmpty(counterId)) {
                    infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
                } else {
                    infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
                }
            }
            long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                    DateUtil.parse(infRiskRdsDto.getMdate()));
            infRiskRdsDto.setOneDay(oneDay);
            if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(oneDay)) {
                infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(oneDay)));
            }
            if (StringUtil.equals(DictCodeList.交易方向_到期续做.getValue(), finDto.getPs())) {
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("tradeAmtex")) ? dataMap.get("tradeAmtex") instanceof BigDecimal ? (BigDecimal) dataMap.get("tradeAmtex") : new BigDecimal(dataMap.get("tradeAmtex").toString()) : finDto.getFpriceAmt());
            }
            if (StringUtil.equals(finDto.getPs(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(finDto.getSettleDate()));
            }

            infRiskRdsDto.setCalendarCode(AssetCommonUtil.getAssetInfo(finDto.getFinprodId(), obj -> obj.getCalendarCode()));
            if (!StringUtil.isNotEmpty(infRiskRdsDto.getRepoTerm())) {
                infRiskRdsDto.setRepoTerm(CommonUtil.getBigDecimal(dataMap, "term", null));
            }
        }
        if (StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_期权.getValue())) {
            infRiskRdsDto.setAssetType(DictCodeListExt3.平安资产类型_期权.getValue());
            infRiskRdsDto.setAmount(finDto.getFpriceAmt());//回购、拆借：到期金额（元）
            infRiskRdsDto.setEventDate(DateUtil.format(finDto.getTradeDate()));
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
        }
        if (StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue()) ||
                StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue())) {
            infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("mTradeAmt")) ? dataMap.get("mTradeAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("mTradeAmt") : new BigDecimal(dataMap.get("mTradeAmt").toString()) : BigDecimal.ZERO);
            infRiskRdsDto.setTradeNum(infRiskRdsDto.getAmount());
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
            infRiskRdsDto.setAssetType2(StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue()) ?
                    DictCodeList.回购拆借方向_融入.getValue() : DictCodeList.回购拆借方向_融出.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);

            infRiskRdsDto.setCalendarCode((String) CommonUtil.findValue(dataMap, "calendarId"));
        }
        if (StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue()) ||
                StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue())) {
            infRiskRdsDto.setAssetType(DictCodeList.平安资产类型_买断式回购.getValue());
            infRiskRdsDto.setAssetType2(StringUtil.equals(finDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue()) ? DictCodeList.回购拆借方向_正回购.getValue() : DictCodeList.回购拆借方向_逆回购.getValue());
            infRiskRdsDto.setPs(DictCodeList.交易方向_到期.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);

            infRiskRdsDto.setCalendarCode(AssetCommonUtil.getAssetInfo(finDto.getFinprodId(), obj -> obj.getCalendarCode()));
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue(), finDto.getFinprodType2())) {
            if (StringUtil.equals(DictCodeList.交易方向_收取.getValue(), finDto.getPs()) || StringUtil.equals(DictCodeList.交易方向_支付.getValue(), finDto.getPs())) {
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty(dataMap.get("beforeEndAmt")) ? dataMap.get("beforeEndAmt") instanceof BigDecimal ? (BigDecimal) dataMap.get("beforeEndAmt") : new BigDecimal(dataMap.get("beforeEndAmt").toString()) : BigDecimal.ZERO);
            } else {
                infRiskRdsDto.setAmount(ArithmeticUtil.doAdd(StringUtil.isNotEmpty(dataMap.get("fixIntAmt")) ? (BigDecimal) dataMap.get("fixIntAmt") : BigDecimal.ZERO, StringUtil.isNotEmpty(dataMap.get("floatIntAmt")) ? (BigDecimal) dataMap.get("floatIntAmt") : BigDecimal.ZERO));
            }
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_利率互换.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
        }
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), finDto.getFinprodType2())) {
            infRiskRdsDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());
            infRiskRdsDto.setEventDate(DateUtil.format(finDto.getTradeDate()));
            String withLock = AssetCommonUtil.getTradeInfo(null, Arrays.asList(infRiskRdsDto.getAssetCode()), (list) -> {
                List<String> withLockList = list.stream().filter(item -> StringUtil.isNotEmpty(item.getWithLockPeriod())).map(item -> item.getWithLockPeriod()).collect(Collectors.toList());
                return null != withLockList ? withLockList.contains(DictCodeListExt2.是否有锁定期_否.getValue()) ? DictCodeListExt2.是否有锁定期_否.getValue()
                        : DictCodeListExt2.是否有锁定期_是.getValue() : null;
            });
            infRiskRdsDto.setWithLockPeriod(withLock);
            //分红送转相当于一笔现金分红一笔送转(送转不考虑)
            if (StringUtil.equals(DictCodeList.股票分红类型_现金分红.getValue(), finDto.getPs())
//            ||StringUtil.equals(DictCodeList.股票分红类型_分红送转.getValue(),finDto.getPs())
            ) {
                infRiskRdsDto.setAmount(finDto.getFpriceAmt());
//                infRiskRdsDto.setTradeNum(finDto.getFpriceAmt());//现金分红不传份额，不影响股票数量
                infRiskRdsDto.setPs(DictCodeList.交易方向_现金分红.getValue());
            }
            //送转
            if (StringUtil.equals(DictCodeList.股票分红类型_送转.getValue(), finDto.getPs())) {
//                infRiskRdsDto.setAmount(finDto.getShareAmt());
                infRiskRdsDto.setTradeNum(finDto.getShareAmt());//送转时只传份额，不传金额，需要影响股票数量，影响实占
                infRiskRdsDto.setPs(DictCodeList.交易方向_红利再投.getValue());
            }
        }
        //自定义现金流
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue(), finDto.getFinprodType2())) {
            if (StringUtil.isNotEmpty(dataMap.get("busiNo"))) {
                infRiskRdsDto.setEventId(dataMap.get("busiNo").toString() + trimInfStr(infRiskRdsDto.getPortfolioCode()));
            } else {
                return null;
            }
            infRiskRdsDto.setAmount(finDto.getTradeAmt());
            infRiskRdsDto.setEventDate(DateUtil.format(finDto.getSettleDate()));
            //无投管大类暂塞金融产品分类，防止接口必填项check
            infRiskRdsDto.setAssetType(DictCodeListExt1.金融产品类型_投管用_自定义现金流.getValue());
            infRiskRdsDto.setAssetCode(null);
            infRiskRdsDto.setMarketCode(null);
        }
        if (CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(v.getAssetType(), DictCodeList.平安资产类型_质押式回购)) {
            long oneDay = DateUtil.getBetweenDayNumber(DateUtil.platDateFromCache(),
                    DateUtil.parse(finDto.getmDate()));
            infRiskRdsDto.setOneDay(oneDay);
            //wangyang1 add by 增加期限金额 2021年9月10日 start
            if (StringUtil.isNotEmpty(infRiskRdsDto.getAmount()) && StringUtil.isNotEmpty(oneDay)) {
                infRiskRdsDto.setTermAmount(ArithmeticUtil.multiply(infRiskRdsDto.getAmount(), new BigDecimal(oneDay)));
            }
            if (StringUtil.isNotEmpty(infRiskRdsDto.getCounterId())) {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
            } else {
                infRiskRdsDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
            }
            //wangyang1 add by 增加期限金额 2021年9月10日 end
        }
        //产品端除这些交易方向的排除在外不调实占
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), finDto.getFinprodType2())) {
            //产品端使用的是dealType存储交易方向的，这里需要转换一下
            if (!StringUtil.isNotEmpty(infRiskRdsDto.getPortfolioCode()) && StringUtil.isNotEmpty(finDto.getFinprodId())) {
                FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finDto.getFinprodId());
                if (StringUtil.isNotEmpty(finProductAdd)) {
                    infRiskRdsDto.setPortfolioCode(finProductAdd.getPortfolioId());
                }
            }
            if (产品端_认购赎回分红.contains(finDto.getPs())) {
                //处理产品端各种场景内部实体给接口字段赋值
                setProductInfColumn(infRiskRdsDto, finDto, dataMap);
            } else {
                //退出不调风险接口
                return null;
            }

        }
        // BUG BSTOT-2281 by zhangrui1 start
        // 债券中间付息、还本、到期和相应的撤销，应该用实收本息总额
        repairSet(infRiskRdsDto, dataMap);
        // BUG BSTOT-2281 by zhangrui1 end

        infRiskRdsDto.setTradeAmtReal(infRiskRdsDto.getAmount());
        //存款，债券，质押式回购取tradeAmt
        if (CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_债券) ||
                CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_存款) ||
                CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_质押式回购)) {
            infRiskRdsDto.setTradeAmtReal(finDto.getTradeAmt());
        }

        //ZGBSS-451 计划利率资产、项目类资产、买断式回购存续期交易金额取理论值start
        if (CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_买断式回购)
                || CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_计划类资产_利率型)
                || CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_项目类资产)) {
            String key = "theoryAmt";
            if (CommonUtil.isType(infRiskRdsDto.getAssetType(), DictCodeList.平安资产类型_买断式回购)) {
                key = "fpriceAmt";
            }
            Object obj = dataMap.get(key);
            if (StringUtil.isNotEmpty(obj)) {
                infRiskRdsDto.setAmount(new BigDecimal(obj.toString()));
            }
        }
        //ZGBSS-451 计划利率资产、项目类资产、买断式回购存续期交易金额取理论值end
        return infRiskRdsDto;
    }

    // add by zhangrui1 债券借贷存续期特别处理 start
    public List<Map<String, Object>> periodbondLend(Map<String, Object> dataMap, V v) {
        List<Map<String, Object>> result = new ArrayList<>();
        String eventDate = DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN);
        Map<String, Object> infMap = new HashMap<>();
        infMap.put(PageFieldToInterfFactory.INF_ASSETTYPE2, StringUtil.equals(
                dataMap.get(InterfUtil.finprodType2),
                DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue()) ? DictCodeList.回购拆借方向_融入.getValue() : DictCodeList.回购拆借方向_融出.getValue());
        // 债券借贷到期
        dataMap.put(InterfUtil.finprodType2, DictCodeListExt2.平安资产类型_债券借贷.getValue());
        String ps2Key = "ps2";
        Object ps2 = dataMap.get(ps2Key);
        dataMap.remove(ps2Key);
        PageFieldToInterfFactory.mapping(dataMap, infMap);
        doCommon(infMap, v);
        infMap.put(PageFieldToInterfFactory.INF_EVENTDATE, eventDate);
        result.add(infMap);
        infMap = new HashMap<>();
        // 债券
        dataMap.put(ps2Key, ps2);
        PageFieldToInterfFactory.mapping(dataMap, infMap);
        doCommon(infMap, v);
        infMap.put(PageFieldToInterfFactory.INF_EVENTDATE, eventDate);
        infMap.put(PageFieldToInterfFactory.INF_TRADENUM,
                ArithmeticUtil.divide(
                        CommonUtil.getBigDecimal(infMap, PageFieldToInterfFactory.INF_TOTALFACEAMT, null),
                        new BigDecimal(100), 4));
        infMap.put(
                PageFieldToInterfFactory.INF_EVENTID,
                (String) infMap.get(PageFieldToInterfFactory.INF_EVENTID) + (String) infMap.get(
                        PageFieldToInterfFactory.INF_PS));
        infMap.put("isIgnoreFoul", DictCodeListExt2.是否_是.getValue());
        result.add(infMap);
        return result;
    }
    // add by zhangrui1 债券借贷存续期特别处理 end
    // BUG BSTOT-2281 by zhangrui1 start

    /**
     * 针对遗漏的场景进行字段设定
     *
     * @param dto
     * @param dataMap
     * @author zhangrui1
     * @see
     */
    private void repairSet(InterfRiskRdsDto dto, Map<String, Object> dataMap) {
        // 债券中间付息、还本、到期和相应的撤销，应该用实收本息总额
        if ((CommonUtil.isType(dto.getAssetType(), DictCodeList.平安资产类型_债券)
                /*|| CommonUtil.isType(dto.getAssetType(), DictCodeList.平安资产类型_存款)*/
                || CommonUtil.isType(dto.getAssetType(), DictCodeList.平安资产类型_计划类资产_利率型)
                || CommonUtil.isType(dto.getAssetType(), DictCodeList.平安资产类型_项目类资产))
                && (CommonUtil.isType(dto.getPs(), DictCodeList.交易方向_还本)
                || CommonUtil.isType(dto.getPs(), DictCodeList.交易方向_到期)
                || CommonUtil.isType(dto.getPs(), DictCodeList.交易方向_收息))) {
            String key = "fpriceAmt";
            Object obj = dataMap.get(key);
            if (StringUtil.isNotEmpty(obj)) {
                dto.setAmount(new BigDecimal(obj.toString()));
            }
            // 2020-06-17 add by zhangrui1 start
            // 中间付息、还本、到期将还本金额设置为数量进行风险计算
            if (CommonUtil.isType(dto.getAssetType(), DictCodeList.平安资产类型_债券)
                    && CommonUtil.isType(dto.getPs(), DictCodeList.交易方向_到期)) {
                return;
            }
            String returnAmtKey = CommonUtil.isType(dto.getAssetType(), DictCodeList.平安资产类型_债券) ? "prinAmtTheory" : "returnAmount";
            obj = CommonUtil.findValue(dataMap, returnAmtKey);
            if (StringUtil.isNotEmpty(obj)) dto.setTradeNum(new BigDecimal(obj.toString()));
            // 2020-06-17 add by zhangrui1 end
        }
        // add by zhangrui1 eventDate不能为空 start
        if (StringUtil.isEmpty(dto.getEventDate())) {
            dto.setEventDate(DateUtil.format(DateUtil.platDateFromCache(), DateUtil.DEFAULT_DATE_FORMAT_EN));
        }
        // add by zhangrui1 eventDate不能为空 end
    }
    // BUG BSTOT-2281 by zhangrui1 end

    /**
     * 为批量调用接口获取各交易类型的分配列表
     *
     * @param dataMap
     * @param v
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Map<String, Object>> getBatchAllocation(Map<String, Object> dataMap, V v) {
        List<Map<String, Object>> retMap = new ArrayList<Map<String, Object>>();
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), v.getAssetType())
                && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), v.getAssetType())
                && (StringUtil.isNotEmpty(dataMap.get("isEnabled")) && StringUtil.equals(dataMap.get("isEnabled"), "01"))
                && (StringUtil.isNotEmpty(dataMap.get("finprodType2")) && (StringUtil.equals(dataMap.get("finprodType2"), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                || StringUtil.equals(dataMap.get("finprodType2"), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                || StringUtil.equals(dataMap.get("finprodType2"), "ZYSHG")))
                && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_利率型.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_货币型.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), v.getAssetType())
                || StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), v.getAssetType())
                && StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))) {
            ((List) dataMap.get("bussTrdTransfenpeiDto")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        } else if (StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussRepoSecPtl"))) {
            Map bussRepoSecPtlMap = JsonUtils.object2Map(dataMap.get("bussRepoSecPtl"));
            if (StringUtil.isNotEmpty(bussRepoSecPtlMap.get("pledgeBondList"))) {
                ((List) bussRepoSecPtlMap.get("pledgeBondList")).forEach(en -> {
                    Map<String, Object> dataReMap = new HashMap<>();
                    dataReMap.putAll(dataMap);
                    dataReMap.putAll((Map) en);
                    retMap.add(dataReMap);
                });
            }
        } else if (StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("trdOutRightRepoTradePtl"))) {
            ((List) dataMap.get("trdOutRightRepoTradePtl")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                if (StringUtil.isNotEmpty(dataReMap.get("firstSettleAmt"))) {
                    dataReMap.put("fSettleAmt", dataReMap.get("firstSettleAmt"));
                }
                retMap.add(dataReMap);
            });
        }
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("bussCombDistribtList"))) {
            ((List) dataMap.get("bussCombDistribtList")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        }
        // add by etliu 20210830 start
        // 现券多组合报价
        else if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), v.getAssetType()) && StringUtil.isNotEmpty(dataMap.get("cfetsBondDealQuoteDtoList"))) {
            ((List) dataMap.get("cfetsBondDealQuoteDtoList")).forEach(en -> {
                Map<String, Object> dataReMap = new HashMap<>();
                dataReMap.putAll(dataMap);
                dataReMap.putAll((Map) en);
                retMap.add(dataReMap);
            });
        }
        // add by etliu 20210830 end
        return retMap;
    }

    /**
     * 处理产品端的实体构造
     *
     * @param infRiskRdsDto
     * @param finDto
     * @param dataMap
     * @throws Exception
     */
    private void setProductInfColumn(InterfRiskRdsDto infRiskRdsDto, TradeBaseDto finDto, Map<String, Object> dataMap) throws IOException {
        //收益类型
        String profitType = (String) dataMap.get("profitType");
        infRiskRdsDto.setPs(finDto.getPs());
        infRiskRdsDto.setEventId((String) dataMap.get("tradeId") + trimInfStr(finDto.getPortfolioId()));
        infRiskRdsDto.setAssetCode(null);
        infRiskRdsDto.setAssetType(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        infRiskRdsDto.setMarketCode(null);
        //净值型产品
        if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品认购.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("subConfirmDate"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("subDealAmt")));
                infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty((String) dataMap.get("subDealNum")) ? new BigDecimal((String) dataMap.get("subDealNum")) : BigDecimal.ZERO);
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品申购.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("purConfirmDate"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("purDealAmt")));
                infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty((String) dataMap.get("purDealNum")) ? new BigDecimal((String) dataMap.get("purDealNum")) : BigDecimal.ZERO);
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品赎回.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品提前赎回.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("redConfirmDate"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("redDealAmt")));
                infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty((String) dataMap.get("redDealNum")) ? new BigDecimal((String) dataMap.get("redDealNum")) : BigDecimal.ZERO);
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_份额强增.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_份额强减.getValue())) {

                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("addReduceConfirmDate"))));
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty((String) dataMap.get("addReduceDealAmt")) ? new BigDecimal((String) dataMap.get("addReduceDealAmt")) : BigDecimal.ZERO);
                if (StringUtil.isNotEmpty(dataMap.get("addReduceUnitNetValue"))) {
                    BigDecimal addReduceDealNum = ArithmeticUtil.divide(new BigDecimal((String) dataMap.get("addReduceDealAmt")), new BigDecimal((String) dataMap.get("addReduceUnitNetValue")), 2);
                    infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty((String) dataMap.get("addReduceDealNum")) ? new BigDecimal((String) dataMap.get("addReduceDealNum")) : addReduceDealNum);
                } else {
                    infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty((String) dataMap.get("addReduceDealNum")) ? new BigDecimal((String) dataMap.get("addReduceDealNum")) : new BigDecimal((String) dataMap.get("addReduceDealAmt")));
                }

            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_提前终止.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品到期.getValue())) {

                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("expireConfirmDate"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("expireDealAmt")));
                infRiskRdsDto.setTradeNum(new BigDecimal((String) dataMap.get("expireDealNum")));
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品现金分红.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("actBonusDate"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("bonusCashAmt")));
            }
        }
        //货币型产品
        else if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), profitType)) {
            if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品认购.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("subConfirmDate03"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("subDealAmt03")));
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品申购.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("purConfirmDate03"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("purDealAmt03")));
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品赎回.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品提前赎回.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("redConfirmDate03"))));
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty((String) dataMap.get("redDealAmt03")) ? new BigDecimal((String) dataMap.get("redDealAmt03")) : BigDecimal.ZERO);
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_份额强增.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_份额强减.getValue())) {

                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("addReduceConfirmDate03"))));
                infRiskRdsDto.setAmount(StringUtil.isNotEmpty((String) dataMap.get("addReduceDealAmt03")) ? new BigDecimal((String) dataMap.get("addReduceDealAmt03")) : BigDecimal.ZERO);
                infRiskRdsDto.setTradeNum(StringUtil.isNotEmpty((String) dataMap.get("addReduceDealNum03")) ? new BigDecimal((String) dataMap.get("addReduceDealNum03")) : new BigDecimal((String) dataMap.get("addReduceDealAmt03")));

            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_提前终止.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品到期.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("expireConfirmDate03"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("expireDealAmt03")));
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品现金分红.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("bonusCashBonusDate03"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("bonusCashBonusAmt03")));
            }
        }
        //收益型产品
        else if (StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), profitType)) {
            infRiskRdsDto.setEventId((String) dataMap.get("tradeId") + trimInfStr((String) dataMap.get("ptlId")));
            infRiskRdsDto.setPortfolioCode((String) dataMap.get("ptlId"));
            if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品认购.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品申购.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("vdate"))));
                infRiskRdsDto.setAmount(new BigDecimal((String) dataMap.get("currentSub")));
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品提前赎回.getValue())) {
                infRiskRdsDto.setEventId((String) dataMap.get("totleTradeId") + trimInfStr((String) dataMap.get("ptlId")));
                infRiskRdsDto.setEventDate((String) dataMap.get("tradeDate"));
                infRiskRdsDto.setAmount(ArithmeticUtil.doAdd(new BigDecimal((String) dataMap.get("prinAmt")), new BigDecimal((String) dataMap.get("actCusProfit"))));
            } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品赎回.getValue())
                    || StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品到期.getValue())) {
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("payDate"))));
                infRiskRdsDto.setAmount(ArithmeticUtil.doAdd(new BigDecimal((String) dataMap.get("reduceAmt")), new BigDecimal((String) dataMap.get("actCusProfit"))));
            }
        }

        //费用和税收
        if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_产品付费.getValue())) {
            List<BussTrdTransferOrderDto> bussTrdTransferOrder = JsonUtils.json2List(JsonUtils.object2Json(dataMap.get("bussTrdTransferOrder")), BussTrdTransferOrderDto.class);
            if (!StringUtil.isEmptyList(bussTrdTransferOrder) && bussTrdTransferOrder.size() > 0) {
                BussTrdTransferOrderDto order = bussTrdTransferOrder.get(0);
                infRiskRdsDto.setEventId((String) dataMap.get("tradeId") + trimInfStr(order.getPortfolioId()));
                infRiskRdsDto.setEventDate(DateUtil.format(new Date((long) dataMap.get("payDate"))));
                infRiskRdsDto.setAmount(order.getSettleAmt());
                infRiskRdsDto.setPortfolioCode(order.getPortfolioId());
                //计提式确认页面代码用的是finproduId，而不规则费用新建页面是finprodId
                if (!StringUtil.isNotEmpty(infRiskRdsDto.getMarketCode())) {
                    infRiskRdsDto.setMarketCode((String) dataMap.get("finproduId") == null ? (String) dataMap.get("finprodId") : (String) dataMap.get("finproduId"));
                }

//                    if(!StringUtil.isNotEmpty(infRiskRdsDto.getAssetCode())) {
//                        infRiskRdsDto.setAssetCode((String)dataMap.get("finproduId"));
//                    }
            }

        } else if (StringUtil.equals(finDto.getPs(), DictCodeList.交易方向_税费缴税.getValue())) {
            List<BussTrdTransferOrderDto> bussTrdTransferOrder = JsonUtils.json2List(JsonUtils.object2Json(dataMap.get("bussTrdTransferOrder")), BussTrdTransferOrderDto.class);
            if (!StringUtil.isEmptyList(bussTrdTransferOrder) && bussTrdTransferOrder.size() > 0) {
                BussTrdTransferOrderDto order = bussTrdTransferOrder.get(0);
                infRiskRdsDto.setEventId((String) dataMap.get("totleTradeId") + trimInfStr(order.getPortfolioId()));
                infRiskRdsDto.setEventDate(order.getSettleDateText());
                infRiskRdsDto.setAmount(order.getSettleAmt());
            }

        }

    }

    /**
     * 查询组合包明细数据
     *
     * @param bagId
     * @return RskPortfolioPackage003Dto
     * @since 1.0
     */
    public List<String> queryPortfolioPackageDtlByBagId(String bagId) {
        List<String> list = restTemplate.getForObject(ServiceApplication.RISK_RDS_SERVICE_NAME + "/RskSupervisionPortf003Point/queryPortfolioPackageDtlByBagId?bagId=" + bagId
                , List.class);
        return list;
    }

    // add by zhangrui1 根据资产类型设置开关 start
    public boolean isRskOpened(String assetType) {
        String openedAsset = CacheUtil.getSysParam("rsk_opened_asset");
        // 开关存在且资产类型不在开启风控资产的列表中则返回空结果
        return !StringUtil.isEmpty(openedAsset)
                && !StringUtil.contains(openedAsset, assetType);
    }
    // add by zhangrui1 根据资产类型设置开关 end
}
