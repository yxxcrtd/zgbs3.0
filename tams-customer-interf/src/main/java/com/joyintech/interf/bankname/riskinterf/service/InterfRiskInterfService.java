/*
 * 文件名：InterfRiskRdsService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.interf.bankname.riskinterf.service;

import com.joyintech.background.component.common.pack.common.validation.annotation.Amount;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.dao.MstCounterPartyMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.core.BaseRiskRdsService;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.util.CommonBusinessUtil;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.client.ClientUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.constantrds.RedisKeyEnum;
import com.joyintech.tams.invest.common.redisinit.dto.IRiskRds;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IAssetInfo;
import com.joyintech.tams.invest.common.riskinfrds.info.service.AssetInfoRedisService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口组件Service
 * @author 裴家俊
 * @version 1.0 2020年1月8日
 * @see InterfRiskInterfService
 * @since 1.0
 */
@Service
public class InterfRiskInterfService<V extends IRiskRds> extends BaseRiskRdsService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextpath.single:false}")
    private boolean isSingle;

    /**
     * 实占列表
     */
    public static final String RSK_SZ_LIST = "RSK_LIST";

    private List<String> 申购确认_赎回申请=Arrays.asList(DictCodeList.交易方向_申购确认.getValue(),DictCodeList.交易方向_赎回.getValue(),DictCodeList.交易方向_赎回到账.getValue());

    private List<String> 产品端_认购赎回分红=Arrays.asList(DictCodeList.交易方向_产品认购.getValue(),DictCodeList.交易方向_产品申购.getValue()
            ,DictCodeList.交易方向_产品赎回.getValue(),DictCodeList.交易方向_份额强增.getValue(),DictCodeList.交易方向_份额强减.getValue(),DictCodeList.交易方向_现金分红.getValue()
            ,DictCodeList.交易方向_提前终止.getValue(),DictCodeList.交易方向_提前赎回.getValue(),DictCodeList.交易方向_产品付费.getValue(),DictCodeList.交易方向_税费缴税.getValue()
            ,DictCodeList.交易方向_产品提前赎回.getValue(),DictCodeList.交易方向_产品现金分红.getValue());

    private List<String> 协议回购到期交易方向 = Arrays.asList(DictCodeListExt3.交易方向_提前购回.getValue(),DictCodeListExt3.交易方向_到期购回.getValue()
            ,DictCodeListExt3.交易方向_解除质押.getValue());
    /**
     * 资产基本信息更新Redis服务
     */
    @Autowired
    private AssetInfoRedisService assetInfoSrc;

    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;

    @Autowired
    private InterfRiskInterfService<InterfRiskRdsDto> interfRiskRdsService;
    
    @Autowired
    private DateCalService dateCalService;
    
    /**
     * TrdCommonBaseService
     */
    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;
    /**
     * 交易类金融产品表
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private MstCounterPartyMapper mstCounterPartyMapper;

    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

    public ClientData sendRiskRdsDataCommon(Map<String, Object> dataMap,V v) throws Exception {
        logger.info("==========调用微服务端风险接口 start==========");
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("rskList");
        List<InterfRiskRdsDto> requestList = new ArrayList<InterfRiskRdsDto>();
        for (Map<String, Object> entity : listMap) {
            InterfRiskRdsDto interfRiskRdsDto = this.prepareRiskEntity(entity, v);
            if (!StringUtil.isNotEmpty(interfRiskRdsDto)) {
                continue;
            }
            String assetType = interfRiskRdsDto.getAssetType();
            if(DictCodeList.平安资产类型_基金.getValue().equals(assetType)) {
                assetType = "F06";
            } else if("F11".equals(assetType)) {
                assetType = "F10";
            }
            if("F07".equals(assetType)) {
                interfRiskRdsDto.setAssetCode(null);
            } else {
//                interfRiskRdsDto.setAssetCode(assetType + "_" + interfRiskRdsDto.getAssetCode());
                interfRiskRdsDto.setAssetCode(trdCommonBaseService.queryFinprodIdByFinprodMarketId(interfRiskRdsDto.getAssetCode(), assetType));
            }

            /**TZJY-37 【投管】配债，组合挂风险后到场内交易下单，显示风控拒绝，应该是待审批才对  ps 转成 T01 2020-08-21  */
            if (CommonUtil.isType(interfRiskRdsDto.getPs(), DictCodeListExt3.交易方向_配债)) {
                interfRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            }

            /**TZJY-37 【投管】配债，组合挂风险后到场内交易下单，显示风控拒绝，应该是待审批才对  ps 转成 T01 2020-08-21  */

            interfRiskRdsDto.setInfCode(v.getInfCode());
            interfRiskRdsDto.setCcy(StringUtil.isNotEmpty(entity.get(InterfUtil.CCY)) ? entity.get(InterfUtil.CCY).toString()
                    : BussinessDictCodeListRds.币种_人民币_CNY.getValue());
            interfRiskRdsDto.setPreOccupyCode(interfRiskRdsDto.getEntrustCode());
            if (StringUtil.equals(CommonUtil.findValue(entity,"ps"),DictCodeList.交易方向_到期续做.getValue())){
                InterfRiskRdsDto secdto = new InterfRiskRdsDto();
                BeanUtil.copyPropertiesIgnoreNull(secdto, interfRiskRdsDto);
                interfRiskRdsDto.setPreOccupyCode(interfRiskRdsDto.getPreOccupyCode() + interfRiskRdsDto.getPs());
                interfRiskRdsDto.setEventId(interfRiskRdsDto.getEventId() + interfRiskRdsDto.getPs());
                secdto.setPs(DictCodeList.交易方向_到期.getValue());
                // 原回购到期
                String preTradeId = "";
                if (CommonUtil.findValue(entity,"firstIssueDealNo") != null){
                    preTradeId = String.valueOf(CommonUtil.findValue(entity,"firstIssueDealNo"));
                }
                FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
                finTradeProductKey.setBranch(0L);
                finTradeProductKey.setFinprodId(preTradeId);
                FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
                if (finTradeProduct != null){
                    if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),finTradeProduct.getFinprodType2())){
                        interfRiskRdsDto.setAssetType2(DictCodeList.回购拆借方向_正回购.getValue());
                        secdto.setAssetType2(DictCodeList.回购拆借方向_正回购.getValue());
                    }else {
                        interfRiskRdsDto.setAssetType2(DictCodeList.回购拆借方向_逆回购.getValue());
                        secdto.setAssetType2(DictCodeList.回购拆借方向_逆回购.getValue());
                    }
                    // 交易编号 = 原交易编号 + PS
                    secdto.setEventId(secdto.getEventId() + secdto.getPs());
                    // 预占编号 = 预占编号 + PS
                    secdto.setPreOccupyCode(secdto.getPreOccupyCode() + secdto.getPs());
                    secdto.setAmount(finTradeProduct.getmTradeAmt());
                    secdto.setTradeNum(finTradeProduct.getPrin());
                    secdto.setAssetRate(finTradeProduct.getIntRate());
                    secdto.setOneDay(finTradeProduct.getTerm());
                    secdto.setMdate(finTradeProduct.getMdate());
                    requestList.add(secdto);
                }
            }
            requestList.add(interfRiskRdsDto);
        }


        Map<String, Object> header = createHead(requestList.get(0));
        String requstjson = JsonUtils.object2Json(requestList);
        ClientData data = new ClientData();
        data.setHead(header);
        Map<String, Object> body = new HashMap<String, Object>();
//        body.put(RSK_SZ_LIST,requstjson);
        body.put(RSK_SZ_LIST, requestList);
        body.put("orderID",dataMap.get("orderID"));
        data.setBody(body);

        ClientData result = ClientUtil.sendRequestInf(
                ServiceApplication.RISK_INF_SERVICE_NAME + InterfUtil.RISK_INF_URL, data.convertToMap(), restTemplate);
        logger.info("==========调用微服务端风险接口 end==========");
        return result;
    }
 
    public ClientData sendRiskRdsDataCommon(List<Map<String, Object>> dataMap,V v,String rskType, boolean isNeedRiskCheck) throws Exception {
        logger.info("==========调用微服务端风险接口 start==========");
		List<InterfRiskRdsDto> requestList = new ArrayList<InterfRiskRdsDto>();
		for (Map<String, Object> entity : dataMap) {
			InterfRiskRdsDto interfRiskRdsDto = this.prepareRiskEntity(entity, v);
			if (!StringUtil.isNotEmpty(interfRiskRdsDto)) {
				continue;
			}
            String assetType = interfRiskRdsDto.getAssetType();
			if(DictCodeList.平安资产类型_基金.getValue().equals(assetType)) {
                assetType = "F06";
            } else if("F11".equals(assetType)) {
                assetType = "F10";
            }

            if("F07".equals(assetType)) {
                interfRiskRdsDto.setAssetCode(null);
            } else {
//                interfRiskRdsDto.setAssetCode(assetType + "_" + interfRiskRdsDto.getAssetCode());
                interfRiskRdsDto.setAssetCode(trdCommonBaseService.queryFinprodIdByFinprodMarketId(interfRiskRdsDto.getAssetCode(), assetType));
            }
			interfRiskRdsDto.setInfCode(v.getInfCode());
			interfRiskRdsDto.setEventType(v.getEventType());
			interfRiskRdsDto.setCcy(StringUtil.isNotEmpty(entity.get(InterfUtil.CCY)) ? entity.get(InterfUtil.CCY).toString()
							: BussinessDictCodeListRds.币种_人民币_CNY.getValue());
            interfRiskRdsDto.setPreOccupyCode(interfRiskRdsDto.getEntrustCode());
			// 暂时没有预占
			if (CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托)) {
				interfRiskRdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资委托.getValue());
			}
			if (!StringUtil.isNotEmpty(interfRiskRdsDto.getAssetType()) && StringUtil.isNotEmpty(v.getAssetType())) {
				interfRiskRdsDto.setAssetType(v.getAssetType());
			}
	//		if (StringUtil.equals(BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue(), v.getInfCode())) {
	//			interfRiskRdsDto.setRevokeFlg(DictCodeList.是否_是.getValue());
	//		}
            /**TZJY-37 【投管】配债，组合挂风险后到场内交易下单，显示风控拒绝，应该是待审批才对  ps 转成 T01 2020-08-21  */
            if (CommonUtil.isType(interfRiskRdsDto.getPs(), DictCodeListExt3.交易方向_配债)) {
                interfRiskRdsDto.setPs(DictCodeList.交易方向_买入.getValue());
            }

            /**TZJY-37 【投管】配债，组合挂风险后到场内交易下单，显示风控拒绝，应该是待审批才对  ps 转成 T01 2020-08-21  */
			// 暂不处理的交易方向
			if (StringUtil.isNotEmpty(interfRiskRdsDto.getPs()) && 申购确认_赎回申请.contains(interfRiskRdsDto.getPs())) {
				if (!CommonUtil.isType(v.getEventType(), BussinessDictCodeListRds.事件类型_投资委托)) {
					continue;
				}
			}
			//债转股 ，拆成两个，债券卖出，股票买入
            if (CommonUtil.isType(interfRiskRdsDto.getPs(), DictCodeListExt1.交易方向_债转股)) {
               //债券卖出
                InterfRiskRdsDto secdto = new InterfRiskRdsDto();
                BeanUtil.copyPropertiesIgnoreNull(secdto, interfRiskRdsDto);
                secdto.setAmount(interfRiskRdsDto.getAmount());
                secdto.setPriceAmt(new BigDecimal("100"));
                secdto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                secdto.setEventId(secdto.getEventId()+secdto.getPs());

                // add by etliu 20210907 start
                // 债转股两笔的预占编号是 指令编号 + 交易方向
                secdto.setPreOccupyCode(secdto.getEntrustCode() + BussinessDictCodeList.交易方向_卖出.getValue());
                // add by etliu 20210907 end

                requestList.add(secdto);
               //股票买入
                if(entity.get("conShareId")!=null) {
                    InterfRiskRdsDto stockdto = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(stockdto, interfRiskRdsDto);
                    // 金额=转股数*转股价格
                    BigDecimal num = (BigDecimal) convert(entity.get("conNum"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
                    BigDecimal pri = (BigDecimal) convert(entity.get("conPrice"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
                    if (num != null && pri != null) {
                        stockdto.setAmount(num.multiply(pri));
                    }
//                    stockdto.setAssetCode("F10_" + (String) entity.get("conShareId"));
                    stockdto.setAssetCode(trdCommonBaseService.queryFinprodIdByFinprodMarketId((String) entity.get("conShareId"), "F10"));
                    stockdto.setAssetType(BussinessDictCodeList.平安资产类型_股票.getValue());
                    stockdto.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    stockdto.setEventId(stockdto.getEventId()+stockdto.getPs());

                    // add by etliu 20210907 start
                    // 债转股两笔的预占编号是 指令编号 + 交易方向
                    stockdto.setPreOccupyCode(secdto.getEntrustCode() + BussinessDictCodeList.交易方向_买入.getValue());
                    // add by etliu 20210907 end

                    requestList.add(stockdto);
                }
            }else if (StringUtil.equals(CommonUtil.findValue(entity,"ps"),DictCodeList.交易方向_到期续做.getValue())){
                InterfRiskRdsDto secdto = new InterfRiskRdsDto();
                BeanUtil.copyPropertiesIgnoreNull(secdto, interfRiskRdsDto);
                interfRiskRdsDto.setPreOccupyCode(interfRiskRdsDto.getPreOccupyCode() + interfRiskRdsDto.getPs());
                interfRiskRdsDto.setEventId(interfRiskRdsDto.getEventId() + interfRiskRdsDto.getPs());
                secdto.setPs(DictCodeList.交易方向_到期.getValue());
                // 原回购到期
                String preTradeId = "";
                if (CommonUtil.findValue(entity,"firstIssueDealNo") != null){
                    preTradeId = String.valueOf(CommonUtil.findValue(entity,"firstIssueDealNo"));
                }
                FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
                finTradeProductKey.setBranch(0L);
                finTradeProductKey.setFinprodId(preTradeId);
                FinTradeProduct finTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
                if (finTradeProduct != null){
                    if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),finTradeProduct.getFinprodType2())){
                        interfRiskRdsDto.setAssetType2(DictCodeList.回购拆借方向_正回购.getValue());
                        secdto.setAssetType2(DictCodeList.回购拆借方向_正回购.getValue());
                    }else {
                        interfRiskRdsDto.setAssetType2(DictCodeList.回购拆借方向_逆回购.getValue());
                        secdto.setAssetType2(DictCodeList.回购拆借方向_逆回购.getValue());
                    }
                    // 交易编号 = 原交易编号 + PS
                    secdto.setEventId(secdto.getEventId() + secdto.getPs());
                    // 预占编号 = 预占编号 + PS
                    secdto.setPreOccupyCode(secdto.getPreOccupyCode() + secdto.getPs());
                    secdto.setAmount(finTradeProduct.getmTradeAmt());
                    secdto.setTradeNum(finTradeProduct.getPrin());
                    secdto.setAssetRate(finTradeProduct.getIntRate());
                    secdto.setOneDay(finTradeProduct.getTerm());
                    secdto.setMdate(finTradeProduct.getMdate());
                    requestList.add(secdto);
                }
                requestList.add(interfRiskRdsDto);
            }else {
                requestList.add(interfRiskRdsDto);
            }

		}

		Map<String, Object> header = createHead(requestList.get(0));
		//String requstjson = JsonUtils.object2Json(requestList);
		ClientData data = new ClientData();
		data.setHead(header);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(rskType,requestList);

        // add by etliu 20210123 start
        if (!isNeedRiskCheck) {
            body.put("isIgnoreFoul", DictCodeListExt2.是否_是.getValue());
        }
        // add by etliu 20210123 end
        
		data.setBody(body);

		ClientData result = ClientUtil.sendRequestInf(
				ServiceApplication.RISK_INF_SERVICE_NAME + InterfUtil.RISK_INF_URL, data.convertToMap(), restTemplate);
		logger.info("==========调用微服务端风险接口 end==========");
		return result;

    }

    /**
     * 调用微服务端风险接口 ---预占
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    public ClientData sendPreRiskRdsDataCommon(List<Map<String, Object>> dataMap,V v) throws Exception {
        logger.info("==========调用微服务端风险接口 start==========");
        InterfRiskRdsDto riskRdsDto = new InterfRiskRdsDto();
        riskRdsDto.setInfCode(v.getInfCode());

        // 构建报文头
        Map<String, Object> header = createHead(riskRdsDto);
        Map<String, Object> body = new HashMap<>();
        body.put(RSK_SZ_LIST, dataMap);
        ClientData data = new ClientData();
        data.setHead(header);
        data.setBody(body);
        ClientData result = ClientUtil.sendRequestInf(
                ServiceApplication.RISK_INF_SERVICE_NAME + InterfUtil.RISK_INF_URL, data.convertToMap(), restTemplate);
        logger.info("==========调用微服务端风险接口 end==========");
        return result;

    }

    /**
     * 为批量调用接口获取各交易类型的分配列表
     * @param dataMap
     * @param
     * @return
     */
    public List<Map<String, Object>> getBatchAllocation(Map<String, Object> dataMap){
        List<Map<String, Object>> retMap=new ArrayList<Map<String, Object>>();
        if( StringUtil.isNotEmpty(dataMap.get("bussTrdTransfenpeiDto"))){
            ((List)dataMap.get("bussTrdTransfenpeiDto")).forEach(en->{
                Map<String, Object> dataReMap=new HashMap<>();
     //           dataReMap.putAll(dataMap);
                dataReMap.putAll((Map)en);
                retMap.add(dataReMap);
            });
        }
        return retMap;
    }
 
    
    /**
     * 构造接口实体
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    private InterfRiskRdsDto prepareRiskEntity(Map<String, Object> dataMap,V v) throws Exception{
        if(StringUtil.isNotEmpty(dataMap.get(InterfUtil.finprodId))&&!StringUtil.isNotEmpty(v.getAssetType())){
            Map<String, IAssetInfo> mapAsset = assetInfoSrc.getMapFromRedis(RedisKeyEnum.资产基本信息.key(), Arrays.asList(dataMap.get(InterfUtil.finprodId).toString()));
            for(IAssetInfo iAst:mapAsset.values()){
                v.setAssetType(StringUtil.isNotEmpty(iAst)?iAst.getAssetType():v.getAssetType());
            }
        }
        //定义返回实体
        if(StringUtil.equals(v.getEventType(),BussinessDictCodeListRds.事件类型_投资交易.getValue())){
            if(StringUtil.equals(v.getEventStatus(),BussinessDictCodeListRds.事件类型_投资交易_存续期.getValue())){
                return periodEntityDev(dataMap,v);
            }
            return tradeEntityDev(dataMap,v);
        }else{
            return entrustEntityDev(dataMap,v);
        }

    }
    

    /**
     * 委托实体封装
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    private InterfRiskRdsDto entrustEntityDev(Map<String, Object> dataMap,V v) throws Exception {
        //定义返回实体
        InterfRiskRdsDto finDto=(InterfRiskRdsDto)JsonUtils.map2entity(dataMap,InterfRiskRdsDto.class);

        if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),v.getAssetType())) {
            finDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            finDto.setAssetType2(finDto.getPs());
            finDto.setPs(DictCodeList.交易方向_买入.getValue());
            finDto.setAssetCode(null);
            finDto.setMarketCode(null);

        }

        return finDto;
    }

    /**
     * 交易实体封装
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    private InterfRiskRdsDto tradeEntityDev(Map<String, Object> dataMap,V v) throws Exception {
        //定义返回实体
        InterfRiskRdsDto finDto=(InterfRiskRdsDto)JsonUtils.map2entity(dataMap,InterfRiskRdsDto.class);
        //委托编号
        String entrustCode = finDto.getEntrustCode();
        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustCode);
        finDto.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
        if(StringUtil.isNotEmpty(etrEntrust)) {
            if(StringUtil.equals(etrEntrust.getTradeMarket(),DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
                finDto.setCalendarCode("04");
            }
            else if (StringUtil.equals(etrEntrust.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                finDto.setCalendarCode("05");
            }
        }
        if(StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(),v.getAssetType())) {
            // 100 * 交易数量
            finDto.setTotalFaceAmt(
                    ArithmeticUtil.multiply(new BigDecimal(100), finDto.getTradeNum()));
            //到期收益率
            EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustCode);
            if(null!=etrEntrustAdd){
                finDto.setYieldEndRate(etrEntrustAdd.getMatureYield());
            }
        }

        if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(),v.getFinType())||
                StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(),v.getFinType())||
                StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(),v.getAssetType())) {
            String assetType2 = String.valueOf(CommonUtil.findValue(dataMap,"assetType2"));
            if (StringUtil.equals(assetType2,DictCodeList.回购类型_质押式协议回购.getValue())){
                // 查询指令信息，取得正、逆回购信息

                if (StringUtil.isNotEmpty(etrEntrust) && StringUtil.equals(etrEntrust.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
                    finDto.setAssetType2(DictCodeList.回购拆借方向_正回购.getValue());
                }else {
                    finDto.setAssetType2(DictCodeList.回购拆借方向_逆回购.getValue());
                }
                finDto.setInfCode(v.getInfCode());
                finDto.setEventDate(DateUtil.format(DateUtil.platDateFromCache(),DateUtil.DEFAULT_DATE_FORMAT_EN));
                //查询指令附属表
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustCode);
                if (协议回购到期交易方向.contains(finDto.getPs())){
                    finDto.setPs(DictCodeList.交易方向_到期.getValue());
                    //协议回购到期交易方向 需要使用到期结算金额实占
                    if(StringUtil.isNotEmpty(etrEntrustAdd)) {
                        finDto.setAmount(etrEntrustAdd.getmTradeAmt() == null? BigDecimal.ZERO:etrEntrustAdd.getmTradeAmt());
                    }
                    //协议回购到期交易方向 的指令没有进行预占，不需要处理预占信息，将指令代码置空
                    finDto.setEntrustCode(null);
                }else {
                    finDto.setPs(DictCodeList.交易方向_买入.getValue());
                    finDto.setAmount(CommonUtil.getBigDecimal(dataMap, "amount",BigDecimal.ZERO));
                }
                finDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());

                finDto.setTradeNum(CommonUtil.getBigDecimal(dataMap, "amount",BigDecimal.ZERO));
                finDto.setAssetRate(ArithmeticUtil.divide(CommonUtil.getBigDecimal(dataMap, "priceAmt",BigDecimal.ZERO), new BigDecimal(100),4));
                finDto.setAssetCode(finDto.getEventId());
                finDto.setMarketCode(null);

                //获得指令的期限。mdate-vdate是实际账款天数，不等于期限
                if(StringUtil.isNotEmpty(etrEntrustAdd)) {
                    finDto.setOneDay(etrEntrustAdd.getTermDays());
                    finDto.setVarietyCode(String.valueOf(etrEntrustAdd.getTermDays()));
                }
                else {
                    Object vdateObject = dataMap.get("vdate");
                    Object mdateObject = dataMap.get("mdate");
                    if(StringUtil.isNotEmpty(vdateObject) && StringUtil.isNotEmpty(mdateObject)) {
                        long term = DateUtil.getBetweenDayNumber((String)vdateObject, (String) mdateObject);
                        finDto.setOneDay(term);
                        finDto.setVarietyCode(String.valueOf(term));
                    }
                }

                //如果报文中传了期限品种，已报文为准。如果没有，就以上面的计算逻辑算出的为准
                Object varietyCode = dataMap.get("varietyCode");
                if(StringUtil.isNotEmpty(varietyCode)) {
                    finDto.setVarietyCode(String.valueOf(varietyCode));
                }

                finDto.setMdate(DateUtil.parseDate(String.valueOf(CommonUtil.findValue(dataMap,"mdate")),DateUtil.DEFAULT_DATE_FORMAT_EN));
                finDto.setCounterId(StringUtil.isNotEmpty(CommonUtil.findValue(dataMap,"counterId"))?CommonUtil.findValue(dataMap,"counterId").toString():null);
                //获取交易对手的市场机构代码
                if(StringUtil.isNotEmpty(finDto.getCounterId())) {
                    MstCounterParty mstCounterParty =  mstCounterPartyMapper.selectByPrimaryKey(finDto.getCounterId());
                    if(StringUtil.isNotEmpty(mstCounterParty)) {
                        finDto.setIssuerCode(mstCounterParty.getLinkId());
                    }
                }
                //wangyang1 add by 增加期限金额 2021年9月10日 start
                if (StringUtil.isNotEmpty(finDto.getAmount()) && StringUtil.isNotEmpty(finDto.getOneDay())){
                    finDto.setTermAmount(ArithmeticUtil.multiply(finDto.getAmount(),new BigDecimal(finDto.getOneDay())));
                }
                if (StringUtil.isNotEmpty(finDto.getCounterId())){
                    finDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
                }else{
                    finDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
                }
                //wangyang1 add by 增加期限金额 2021年9月10日 end
                return finDto;
            }

            finDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            finDto.setAssetType2(finDto.getPs());
            finDto.setPs(DictCodeList.交易方向_买入.getValue());
            
            //计算 利率/100
            if(StringUtil.isNotEmpty(finDto.getAssetRate())) {
                finDto.setAssetRate(ArithmeticUtil.divide(finDto.getAssetRate(), new BigDecimal(100), 4));
            }

            if(StringUtil.isNotEmpty(assetType2)) {
                if(StringUtil.equals((String)assetType2, DictCodeList.回购类型_质押式协议回购.getValue())) {
                    Object vdateObject = dataMap.get("vdate");
                    if(StringUtil.isNotEmpty(vdateObject) && StringUtil.isNotEmpty(finDto.getMdate())) {
                        long term = DateUtil.getBetweenDayNumber((String)vdateObject, finDto.getMdate());
                        finDto.setOneDay(term);
                    }
                } 
                else {
                    //竞价回购需要根据期限品种计算MDate 
                    Object varietyCode = dataMap.get("varietyCode");
                    if(StringUtil.isNotEmpty(varietyCode)) {
                        long term = CommonBusinessUtil.TERM_MAP.get((String)varietyCode);
                        //期限
                        finDto.setOneDay(term);
                        Object vdateObject = dataMap.get("vdate");
                        if(StringUtil.isNotEmpty(vdateObject) && StringUtil.isNotEmpty(term)) {
                           
                            Date vdate = new Date();
                            //试算和提交时Vdate格式不一样
                            try{
                              if(DateUtil.isDateStr((String)vdateObject))  {
                                  vdate = DateUtil.parse((String)vdateObject);  
                              }
                             
                            } catch (Exception e) {
                                vdate =  new Date((long)vdateObject); 
                            }
                            Date mDate = DateUtil.addDays(vdate, (int)term);
                            List<String> holiday = dateCalService.getDealCalendarHoliday(vdate,
                                mDate, finDto.getCalendarCode()); 
                            Date tradingDay = dateCalService.calculationTradingDay(mDate, 1, holiday, "1");
                            
                            finDto.setMdate(tradingDay);
                        }  
                        else {
                            if(!StringUtil.isNotEmpty(term)) {
                                logger.info("==========查询期限为空,委托编号：=========="+finDto.getEntrustCode());
                            }
                            
                            if(!StringUtil.isNotEmpty(vdateObject)) {
                                logger.info("==========vdate为空,委托编号：=========="+finDto.getEntrustCode());
                            }
                        }
                     
                    } 
                    else {
                        logger.info("==========期限品种varietyCode为空,委托编号：=========="+finDto.getEntrustCode());
                    }
                }
            }
            //wangyang1 add by 增加期限金额 2021年9月10日 start
            if (StringUtil.isNotEmpty(finDto.getAmount()) && StringUtil.isNotEmpty(finDto.getOneDay())){
                finDto.setTermAmount(ArithmeticUtil.multiply(finDto.getAmount(),new BigDecimal(finDto.getOneDay())));
            }
            if (StringUtil.isNotEmpty(finDto.getCounterId())){
                finDto.setIsNullCounter(BaseDictCodeList.是否_是.getValue());
            }else{
                finDto.setIsNullCounter(BaseDictCodeList.是否_否.getValue());
            }
            //wangyang1 add by 增加期限金额 2021年9月10日 end
        }
        return finDto;
    }
    
    /**
     * 交易实体封装
     * @param dataMap
     * @param v
     * @return
     * @throws Exception
     */
    private InterfRiskRdsDto periodEntityDev(Map<String, Object> dataMap,V v) throws Exception {
        //定义返回实体
        InterfRiskRdsDto finDto=(InterfRiskRdsDto)JsonUtils.map2entity(dataMap,InterfRiskRdsDto.class);

        if(StringUtil.equals(finDto.getAssetType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())||
                StringUtil.equals(finDto.getAssetType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) {
            finDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());
            finDto.setAssetType2(StringUtil.equals(finDto.getAssetType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())?DictCodeList.回购拆借方向_正回购.getValue():DictCodeList.回购拆借方向_逆回购.getValue());
            finDto.setAssetCode(null);
            finDto.setMarketCode(null);
        }

        if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(),finDto.getAssetType2())) {
            finDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());
            if(StringUtil.equals(DictCodeList.股票分红类型_现金分红.getValue(),finDto.getPs())){
                finDto.setPs(DictCodeList.交易方向_现金分红.getValue());
            }
            if(StringUtil.equals(DictCodeList.股票分红类型_送转.getValue(),finDto.getPs())){
                finDto.setPs(DictCodeList.交易方向_红利再投.getValue());
            }
            if(StringUtil.equals(DictCodeList.股票分红类型_分红送转.getValue(),finDto.getPs())){
                return null;
            }
        }
        return finDto;
    }

    /**
     * 风险名单控制批量
     * @param dataMap
     */
    public Map<String, Object>  riskNameBatch(Map<String, Object> dataMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        logger.info("==========调用微服务端风险名单控制批量接口 start==========");
        Map result = null;
        if (isSingle) {
            result = simpleRestTemplate.postForObject(ServiceApplication.RISK_SERVICE_NAME + "/RiskNamBatchValidator/batch"
                    ,dataMap, Map.class);
        } else {
            result = restTemplate.postForObject(ServiceApplication.RISK_SERVICE_NAME + "/RiskNamBatchValidator/batch",
                    dataMap, Map.class);
        }
        List<String> errorList = (List<String>)result.get("errorList");
        if(StringUtil.isEmptyList(errorList)) {
            resultMap.put("rskStatus","0");
            resultMap.put("message","成功");
        } else {
            resultMap.put("rskStatus","9");
            resultMap.put("message",errorList.stream().collect(Collectors.joining(",")));
        }
        logger.info("==========风险名单控制批量结果：rskStatus：" +resultMap.get("rskStatus")+";message: "+resultMap.get("message"));

        logger.info("==========调用微服务端风险名单控制批量接口 end==========");
        return resultMap;
    }

    /**
     * 授信占用批量接口
     * @param list
     */
    public Map<String, Object>  sxTradeCompleteBatch(List<Map<String, Object>> list,String tradeStatu, boolean isNeedRiskCheck) throws Exception{
        logger.info("==========授信占用/释放批量接口 start==========");
        //1、封装授信接口的参数
        List<CreditLinesInputDto> trdProductDeals = packageParams(list,tradeStatu);

        //2、授信接口check
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rskStatus","0");
        resultMap.put("message","成功");
        List<CreditLinesInputDto> trdProductDealsobj = new ArrayList<>();
        for(CreditLinesInputDto dto:trdProductDeals){
            CreditLinesInputDto dto1 = new CreditLinesInputDto();
            BeanUtil.copyPropertiesIgnoreNull(dto1, dto);
            trdProductDealsobj.add(dto1);
        }
        if (DictCodeListExt1.交易状态_初始.getValue().equals(tradeStatu) && isNeedRiskCheck){
            resultMap = checkSxTrade(trdProductDeals);
        }

        if(BussinessDictCodeListRds.指标状态_通过.getValue().equals(resultMap.get("rskStatus"))){
            //3、授信接口占用
            AllTradeCommonUtil.handleBeforeTradeComplete(trdProductDealsobj, DictCodeListRsk.授信占用类别_实占.getValue());
        }
        logger.info("==========授信占用/释放批量结果：rskStatus：" +resultMap.get("rskStatus")+";message: "+resultMap.get("message"));
        logger.info("==========授信占用/释放批量接口 start==========");
        return resultMap;
    }

    /**
     * 授信预占
     * @param list
     */
    public void  sxPreTradeCompleteBatch(List<Map<String, Object>> list,String tradeStatu) throws Exception{
        logger.info("==========授信预占释放批量接口 start==========");
        //1、封装授信接口的参数
        List<CreditLinesInputDto> trdlist = new ArrayList<CreditLinesInputDto>();
        for(Map<String, Object> temp :list ) {
            CreditLinesInputDto creditLinesInputDto =new CreditLinesInputDto();
            creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
            creditLinesInputDto.setTradeId((String)temp.get("eventId"));
            creditLinesInputDto.setTradeStatus(tradeStatu);
            trdlist.add(creditLinesInputDto) ;
        }
        //2、授信接口占用
        AllTradeCommonUtil.handleBeforeTradeComplete(trdlist, DictCodeListRsk.授信占用类别_预占.getValue());
        logger.info("==========授信预占释放批量接口 end==========");
    }


    /**
     * 包装调用授信接口的参数
     * @param list
     */
    private List<CreditLinesInputDto> packageParams(List<Map<String, Object>> list,String tradeStatu) throws Exception{
        List<CreditLinesInputDto> trdlist = new ArrayList<CreditLinesInputDto>();
        for(Map<String, Object> temp :list ) {
            InterfRiskRdsDto finDto =(InterfRiskRdsDto)JsonUtils.map2entity(temp,InterfRiskRdsDto.class);
            if (CommonUtil.isType(finDto.getPs(), DictCodeListExt3.交易方向_配债)
                || CommonUtil.isType(finDto.getPs(), DictCodeListExt2.交易方向_配股) ) {
                finDto.setPs(DictCodeList.交易方向_买入.getValue());
            }
            //债转股 ，拆成两个，债券卖出，股票买入
            if (CommonUtil.isType(finDto.getPs(), DictCodeListExt1.交易方向_债转股)) {
                String assetType = finDto.getAssetType();
                //债券卖出
                CreditLinesInputDto inputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(inputDto, finDto);
                inputDto.setTradeStatus(tradeStatu);
                inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                inputDto.setCreditCheckType(DictCodeListRsk.授信占用来源_接口.getValue());
                inputDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                inputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                inputDto.setTradeDate(DateUtil.parse(finDto.getEventDate()));
                inputDto.setCounterId(finDto.getCounterId());
                inputDto.setTradeId((String)temp.get("tradeId"));
                inputDto.setTradeAmt(finDto.getAmount());
                inputDto.setShareAmt(finDto.getTradeNum());
                inputDto.setFinprodId(assetType + "_" + finDto.getAssetCode());
                //最新面值
                BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(inputDto);
                if (newFaceValue == null){
                    newFaceValue = new BigDecimal(100);
                }
                if (inputDto.getShareAmt() != null) {
                    inputDto.setTradeAmt(newFaceValue.multiply(inputDto.getShareAmt()));
                }
                trdlist.add(inputDto);
                //股票买入
                if(temp.get("conShareId") != null) {
                    CreditLinesInputDto inputDto1 = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(inputDto1, inputDto);
                    // 金额=转股数*转股价格
                    BigDecimal num = (BigDecimal) convert(temp.get("conNum"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
                    BigDecimal pri = (BigDecimal) convert(temp.get("conPrice"), BigDecimal.class, obj -> new BigDecimal(obj.toString()));
                    if (num != null && pri != null) {
                        inputDto1.setShareAmt(num);
                        inputDto1.setTradeAmt(num.multiply(pri));
                    }
                    inputDto1.setFinprodId("F10_" + (String) temp.get("conShareId"));
                    inputDto1.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                    inputDto1.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    inputDto1.setEntrustId((String)temp.get("entrustCode"));
                    trdlist.add(inputDto1);
                }
            } else {
                String assetType = finDto.getAssetType();
                if(DictCodeList.平安资产类型_质押式回购.getValue().equals(assetType)
                    && DictCodeList.回购类型_质押式竞价回购.getValue().equals(finDto.getAssetType2())) {
                    continue;
                }

                if (DictCodeList.回购类型_质押式协议回购.getValue().equals(finDto.getAssetType2())){
                    // 查询首期交易类金融产品数据
                    FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
                    finTradeProductKey.setFinprodId((String)temp.get("firstIssueDealNo"));
                    finTradeProductKey.setBranch(0L);
                    FinTradeProduct firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);

                    // 正回购 不占授信
                    if (StringUtil.equals(temp.get("ps"),DictCodeList.回购拆借方向_正回购.getValue()) || (StringUtil.isNotEmpty(firstFinTradeProduct)
                        && StringUtil.equals(firstFinTradeProduct.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()))){
                        continue;
                    }
                    // 深交所到期续做，不涉及授信占用和释放
                    if (StringUtil.isNotEmpty(firstFinTradeProduct) && StringUtil.equals(finDto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue())
                        && StringUtil.equals(firstFinTradeProduct.getTradeMarket(),DictCodeListExt1.交易场所_深圳证券交易所.getValue())){
                        continue;
                    }
                    //add by zhangnian  平安资产类型_质押式回购  start
                    //质押券为利率债(国债、政策型银行债)不占额度
                    if(DictCodeList.平安资产类型_质押式回购.getValue().equals(assetType)&&temp.containsKey("pledgeAssetCodeList")){
                        if(temp.containsKey("occupySource")&&StringUtil.equals(temp.get("occupySource"),"CFET")){
                            List<Map<String,Object>> pledgeAssetCodeList = (List<Map<String,Object>>) temp.get("pledgeAssetCodeList");
                            if(!StringUtil.isEmptyList(pledgeAssetCodeList)) {
                                BigDecimal amount = BigDecimal.ZERO;
                                for (Map<String, Object> stringObjectMap : pledgeAssetCodeList) {
                                    String assetcode = (String)stringObjectMap.get("finprodMarketId");
                                    if (!(StringUtil.isNotEmpty(assetcode) && !riskCreditListCommonValidatorService.checkCallCredit("F01_" + assetcode))) {
                                        amount = ArithmeticUtil.doAdd(amount,new BigDecimal(stringObjectMap.get("amount").toString()));
                                    }
                                }
                                finDto.setAmount(amount);
                            }
                        }else{
                            List<String> pledgeAssetCodeList = (List<String>) temp.get("pledgeAssetCodeList");
                            if(!StringUtil.isEmptyList(pledgeAssetCodeList)) {
                                String assetcode = pledgeAssetCodeList.get(0);
                                if (StringUtil.isNotEmpty(assetcode) && !riskCreditListCommonValidatorService.checkCallCredit("F01_" + assetcode)) {
                                    continue;
                                }
                            }
                        }
                     }
                    //add by zhangnian  20210823 end
                    CreditLinesInputDto creditLinesInputDto =new CreditLinesInputDto();
                    creditLinesInputDto.setTradeStatus(tradeStatu);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setCreditCheckType(DictCodeListRsk.授信占用来源_接口.getValue());
                    creditLinesInputDto.setEntrustId((String)temp.get("entrustCode"));
                    if (协议回购到期交易方向.contains(finDto.getPs())){
                        creditLinesInputDto.setTradeId((String)temp.get("tradeId"));
                        creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                        // 未找到首期信息
                        if (firstFinTradeProduct == null){
                            logger.info("未找到对应首期信息，成交信息:" + JsonUtils.object2Json(temp));
                            continue;
                        }
                        creditLinesInputDto.setTradeAmt(firstFinTradeProduct.getPrin());
                        creditLinesInputDto.setShareAmt(firstFinTradeProduct.getPrin());
                    }else {
                        creditLinesInputDto.setTradeId((String)temp.get("tradeId"));
                        creditLinesInputDto.setTradeAmt(finDto.getAmount());
                        creditLinesInputDto.setShareAmt(finDto.getTradeNum());
                        creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
                    }
                    creditLinesInputDto.setTradeDate(DateUtil.parse(finDto.getEventDate()));
                    creditLinesInputDto.setCounterId(finDto.getCounterId());
                    creditLinesInputDto.setUpdateTime(new Date());
                    creditLinesInputDto.setUpdateUser(LoginUserUtil.getUserName());
                    creditLinesInputDto.setCreateDept(LoginUserUtil.getDeptCode());
                    creditLinesInputDto.setCreateTime(new Date());
                    creditLinesInputDto.setCreateUser(LoginUserUtil.getUserName());
                    creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                    creditLinesInputDto.setPsPayment(finDto.getAssetType2());
//                    creditLinesInputDto.setSaleCode(finDto.getPs());
                    creditLinesInputDto.setSaleCode(DictCodeList.回购拆借方向_逆回购.getValue());
                    trdlist.add(creditLinesInputDto);
                    // 到期续做的 原首期 到期购回
                    if (StringUtil.equals(finDto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue())){
                        // 未找到首期信息
                        if (firstFinTradeProduct == null){
                            logger.info("未找到对应首期信息，成交信息:" + JsonUtils.object2Json(temp));
                            continue;
                        }
                        CreditLinesInputDto creditLinesInputT56 =new CreditLinesInputDto();
                        creditLinesInputT56.setTradeStatus(tradeStatu);
                        creditLinesInputT56.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                        creditLinesInputT56.setCreditCheckType(DictCodeListRsk.授信占用来源_接口.getValue());
                        creditLinesInputT56.setPs(DictCodeList.交易方向_到期.getValue());
                        creditLinesInputT56.setTradeDate(DateUtil.parse(finDto.getEventDate()));
                        creditLinesInputT56.setCounterId(finDto.getCounterId());
                        // 首期的交易编号
                        creditLinesInputT56.setTradeId((String)temp.get("tradeId") + finDto.getPs());
                        creditLinesInputT56.setTradeAmt(firstFinTradeProduct.getPrin());
                        creditLinesInputT56.setShareAmt(firstFinTradeProduct.getPrin());
                        creditLinesInputT56.setUpdateTime(new Date());
                        creditLinesInputT56.setUpdateUser(LoginUserUtil.getUserName());
                        creditLinesInputT56.setCreateDept(LoginUserUtil.getDeptCode());
                        creditLinesInputT56.setCreateTime(new Date());
                        creditLinesInputT56.setCreateUser(LoginUserUtil.getUserName());
                        creditLinesInputT56.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                        creditLinesInputT56.setPsPayment(finDto.getAssetType2());
                        creditLinesInputT56.setSaleCode(DictCodeList.回购拆借方向_逆回购.getValue());
                        trdlist.add(creditLinesInputT56);
                    }
                    continue;
                }
                CreditLinesInputDto creditLinesInputDto =new CreditLinesInputDto();
                creditLinesInputDto.setTradeStatus(tradeStatu);
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                creditLinesInputDto.setCreditCheckType(DictCodeListRsk.授信占用来源_接口.getValue());
                creditLinesInputDto.setEntrustId((String)temp.get("entrustCode"));
                creditLinesInputDto.setPs(finDto.getPs());
                creditLinesInputDto.setTradeDate(DateUtil.parse(finDto.getEventDate()));
                creditLinesInputDto.setCounterId(finDto.getCounterId());
                creditLinesInputDto.setTradeId((String)temp.get("tradeId"));
                creditLinesInputDto.setTradeAmt(finDto.getAmount());
                creditLinesInputDto.setShareAmt(finDto.getTradeNum());
                if(DictCodeList.平安资产类型_基金.getValue().equals(assetType)) {
                    assetType = "F06";
                } else if(DictCodeListExt2.平安资产类型_股票.getValue().equals(assetType)) {
                    assetType = "F10";
                }
                creditLinesInputDto.setFinprodId(assetType + "_" + finDto.getAssetCode());
                if (DictCodeList.平安资产类型_债券.getValue().equals(assetType)){
                    //最新面值
                    BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
                    if (newFaceValue == null){
                        newFaceValue = new BigDecimal(100);
                    }
                    if (creditLinesInputDto.getShareAmt() != null) {
                        creditLinesInputDto.setTradeAmt(newFaceValue.multiply(creditLinesInputDto.getShareAmt()));
                    }
                }
                creditLinesInputDto.setUpdateTime(new Date());
                creditLinesInputDto.setUpdateUser(LoginUserUtil.getUserName());
                creditLinesInputDto.setCreateDept(LoginUserUtil.getDeptCode());
                creditLinesInputDto.setCreateTime(new Date());
                creditLinesInputDto.setCreateUser(LoginUserUtil.getUserName());
                creditLinesInputDto.setNetPrice(finDto.getPriceAmt());
                trdlist.add(creditLinesInputDto);
                if(DictCodeList.平安资产类型_基金.getValue().equals(finDto.getAssetType())&&"T01".equals(finDto.getPs())) {
                    CreditLinesInputDto dto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(dto, creditLinesInputDto);
                    dto.setEntrustId(dto.getTradeId());
                    dto.setIsjijinmairu("true");
                    dto.setIsSpecial("true");
                    trdlist.add(dto);
                }
            }
        }
        return trdlist;
    }

    /**
     * 授信check
     * @param list
     */
    private Map<String, Object> checkSxTrade(List<CreditLinesInputDto>  list) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("rskStatus","0");
        resultMap.put("message","成功");
        AjaxResult ajaxResult = AllTradeCommonUtil.checkBeforeTradeComplete(list);
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())){
            resultMap.put("rskStatus","9");
            resultMap.put("message",ajaxResult.getErrorList().get(0).getMessage());
        }
        return resultMap;
    }

    /**
     * 三合一接口
     * @param dataMap
     * @param szcliendata
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor=Exception.class)
    public Map<String, Object> doOccupyBatchData( Map<String, Object> dataMap,ClientData szcliendata) throws Exception{
        String assetType = (String) dataMap.get("assetType");
        String assetType2 = null;
        String eventType = (String) dataMap.get("eventType");
        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
        interfRiskRdsDto.setAssetType(assetType);
        interfRiskRdsDto.setEventType(eventType);

        //List<Map<String, Object>> listMap=interfRiskRdsService.getBatchAllocation(dataMap);
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("rskList");

        if("F07".equals(assetType)) {
            for(Map<String, Object> tmp : listMap) {
                tmp.put("inputValue1",tmp.get("assetType"));
                tmp.put("inputValue2",tmp.get("assetType2"));
                if(StringUtil.equals(tmp.get("ps"),DictCodeList.回购拆借方向_正回购.getValue())) {
                    tmp.put("inputValue3",DictCodeListExt3.行内资产三级分类_正回购.getValue());
                }
                else {
                    tmp.put("inputValue3",DictCodeListExt3.行内资产三级分类_逆回购.getValue());
                }

                assetType2 = (String) tmp.get("assetType2");
            }
        }
        
        // modify by etliu 20210122 start
        boolean isNeedRiskCheck = true;
        if (dataMap.get("isNeedRiskCheck") != null && StringUtil.equals(
            DictCodeList.是否_否.getValue(), (String)dataMap.get("isNeedRiskCheck"))) {
            isNeedRiskCheck = false;
        }
        //场内交易的竞价回购不需要进行名单控制
//        if(!StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), assetType)) {
        if((!StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), assetType)
            ||(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), assetType)
                && !StringUtil.equals(DictCodeList.回购类型_质押式竞价回购.getValue(), assetType2)))
            && isNeedRiskCheck) {
        // modify by etliu 20210122 end
            //名单控制
            Map<String, Object> rskName= this.riskNameBatch(dataMap);

            //不通过，返回
            if(!BussinessDictCodeListRds.指标状态_通过.getValue().equals(rskName.get("rskStatus"))){
                return rskName;
            }
        }

        if (!StringUtil.equals(BussinessDictCodeList.监控授信开关_关.getValue(), CacheUtil.getSysParam(BussinessConstant.ISCREDIT))) {
            //调用授信实占-批量
            Map<String, Object> sxTrade = interfRiskRdsService.sxTradeCompleteBatch(listMap, DictCodeListExt1.交易状态_初始.getValue(), isNeedRiskCheck);
            //不通过，返回
            if (!BussinessDictCodeListRds.指标状态_通过.getValue().equals(sxTrade.get("rskStatus")) && isNeedRiskCheck) {
                return sxTrade;
            }
        }
        //调用限额实占-批量
        interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
        ClientData cliendata = interfRiskRdsService.sendRiskRdsDataCommon(listMap, interfRiskRdsDto,RSK_SZ_LIST, isNeedRiskCheck);
        //不通过，返回
        if(!BussinessDictCodeListRds.指标状态_通过.getValue().equals(cliendata.getBody().get("rskStatus")) && isNeedRiskCheck){
            szcliendata.setHead(cliendata.getHead());
            szcliendata.setBody(cliendata.getBody());
            throw new Exception("指标状态_不通过");
        }

        //返回结果集
        return cliendata.getBody();
    }

    @Transactional(rollbackFor=Exception.class)
    public Map<String, Object> doOccupyReleasBatchData( Map<String, Object> dataMap,ClientData szcliendata) throws Exception{

        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();
        String assetType = (String) dataMap.get("assetType");
        String eventType = (String) dataMap.get("eventType");
        interfRiskRdsDto.setAssetType(assetType);
        interfRiskRdsDto.setEventType(eventType);
        List<String> retList = new ArrayList<>();

        //List<Map<String, Object>> listMap=interfRiskRdsService.getBatchAllocation(dataMap);
        List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("rskList");

        if("F07".equals(assetType)) {
            for(Map<String, Object> tmp : listMap) {
                tmp.put("inputValue1",tmp.get("assetType"));
                tmp.put("inputValue2",tmp.get("assetType2"));
                tmp.put("inputValue3",tmp.get("ps"));
            }
        }

        if (!StringUtil.equals(BussinessDictCodeList.监控授信开关_关.getValue(), CacheUtil.getSysParam(BussinessConstant.ISCREDIT))) {
            //调用授信释放-批量
            Map<String, Object> sxTrade = interfRiskRdsService.sxTradeCompleteBatch(listMap, DictCodeListExt1.交易状态_撤销.getValue(), true);
            //不通过，返回
            if (!BussinessDictCodeListRds.指标状态_通过.getValue().equals(sxTrade.get("rskStatus"))) {
                return sxTrade;
            }
        }
        //调用合规实占释放/撤销
        interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
        ClientData clientData = interfRiskRdsService.sendRiskRdsDataCommon(listMap, interfRiskRdsDto,RSK_SZ_LIST, true);
        String statu = (String)clientData.getHead().get("std400msgt");
        //不通过，返回
        Map<String, Object> resultMap = new HashMap<>();
        if(!"N".equals(statu)){
            szcliendata.setHead(clientData.getHead());
            szcliendata.setBody(clientData.getBody());
            throw new Exception("指标状态_不通过");
           // return doExceptionMsg((String)szsfclientData.getHead().get("stdrtninfo"));
        } else {
            resultMap.put("rskStatus","0");
            resultMap.put("message","成功");
        }
        //返回结果集
        return resultMap;

    }

    /**
     * 预占释放
     * @param dataMap
     * @param szcliendata
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor=Exception.class)
    public Map<String, Object> doPreOccupyReleasBatchData( Map<String, Object> dataMap,ClientData szcliendata) throws Exception{

        InterfRiskRdsDto interfRiskRdsDto = new InterfRiskRdsDto();

        List<Map<String, Object>> listMap = (List<Map<String, Object>>) dataMap.get("rskList");

        if (!StringUtil.equals(BussinessDictCodeList.监控授信开关_关.getValue(), CacheUtil.getSysParam(BussinessConstant.ISCREDIT))) {
            //调用授信预占释放-批量
            interfRiskRdsService.sxPreTradeCompleteBatch(listMap, DictCodeListExt1.交易状态_撤销.getValue());
        }
        //调用合规预占释放/撤销
        interfRiskRdsDto.setInfCode(BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue());
        ClientData clientData = interfRiskRdsService.sendPreRiskRdsDataCommon(listMap, interfRiskRdsDto);
        String statu = (String)clientData.getHead().get("std400msgt");
        //不通过，返回
        Map<String, Object> resultMap = new HashMap<>();
        if(!"N".equals(statu)){
            szcliendata.setHead(clientData.getHead());
            szcliendata.setBody(clientData.getBody());
            throw new Exception("指标状态_不通过");
            // return doExceptionMsg((String)szsfclientData.getHead().get("stdrtninfo"));
        } else {
            resultMap.put("rskStatus","0");
            resultMap.put("message","成功");
        }
        //返回结果集
        return resultMap;

    }

}
