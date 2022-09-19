/*
 * 文件名：InterfBaseService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年7月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.riskrdsinf.core.service;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.riskrdsinf.cache.InterfCacheUtil;
import com.joyintech.service.riskrdsinf.core.annotation.INF;
import com.joyintech.service.riskrdsinf.core.client.ClientData;
import com.joyintech.service.riskrdsinf.core.constant.InfColumns;
import com.joyintech.service.riskrdsinf.core.constant.InfDictCodeList;
import com.joyintech.service.riskrdsinf.core.exception.InterfException;
import com.joyintech.service.riskrdsinf.core.util.InfMapValUtil;
import com.joyintech.redis.common.util.ProducerUtils;
import com.joyintech.redis.core.base.PipeWriteService;
import com.joyintech.redis.lock.Locker;
import com.joyintech.redis.msg.entity.MessageBody.Operation;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.common.base.entity.InterfRiskLogWithBLOBs;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.ImplProdAssetAmount;
import com.joyintech.tams.invest.common.riskinfrds.info.entity.IProdAssetAmount;
import com.joyintech.tams.invest.common.riskinfrds.info.service.ExchargeInfoService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.FundValuationService;
import com.joyintech.tams.invest.common.riskinfrds.info.service.ProdAssetAmountService;

/**
 * 风险接口Service
 * 
 * @author zhangrui1
 * @version 1.0 2019年7月26日
 * @see InfBaseService
 * @since 1.0
 */
public abstract class InfBaseService<V> {

    /**
     * 处理结果
     */
    private static final String HANDLEOK = "HANDLEOK";

    /**
     * KEY:不需要校验资产代码的交易
     */
    public static final String INF_ASSET_NONEED_PS = "infAssetNoNeedPs";

    /**
     * 信息码:成功
     */
    protected static final String AAAAAAA = "AAAAAAA";

    /**
     * 风险系统标识
     */
    protected static final String RISK_SYS_CODE = "riskinf_system_code";

    /**
     * 信息码:成功
     */
    protected static final String RISK_CODE = "RISK";

    protected static final String PRE_NOT_EXISTS = "对应的预占信息不存在";

    @Autowired
    private PipeWriteService<String, String, V> redisWriter;

    @Autowired
    private Validator validator;

    /**
     * 接口ID
     */
    private String infId;

    /**
     * 服务线程数据
     */
    private ThreadLocal<Map<String, Object>> serviceInfo = ThreadLocal.withInitial(HashMap::new);

    /**
     * 项目值内容发布者
     */
    private ProducerUtils producer = ProducerUtils.getInstance();

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 汇率行情信息Redis读写Service
     */
    @Autowired
    private ExchargeInfoService exchargeSrc;

    /**
     * 基金估值行情Redis读写Service
     */
    @Autowired
    private FundValuationService fundValSrc;

    // add by zhangrui1 start
    /**
     * 资产实时规模信息
     */
    @Autowired
    protected ProdAssetAmountService prodAssetAmtSrc;
    // add by zhangrui1 end

    /**
     * 取得INF注解
     * 
     * @return INF注解实例
     * @see
     * @since 1.0
     */
    private INF getINF() {
        return getClass().getAnnotation(INF.class);
    }

    @Autowired
    private void init() {
        INF inf = getINF();
        if (inf != null) {
            infId = inf.value();
        }
    }

    /**
     * 取得 服务线程数据
     * 
     * @return 服务线程数据
     * @see
     * @since 1.0
     */
    private Map<String, Object> getServiceInfo() {
        return serviceInfo.get();
    }

    /**
     * 设置 服务线程数据
     * 
     * @param column 接口规范
     * @param info 数据
     * @see
     * @since 1.0
     */
    protected void setServiceInfo(InfColumns column, Object info) {
        setServiceInfo(column.fieldName(), info);
    }

    /**
     * 取得 服务线程数据
     * 
     * @param column 接口规范
     * @return 服务线程数据
     * @see
     * @since 1.0
     */
    protected Object getServiceInfo(InfColumns column) {
        return getServiceInfo(column.fieldName());
    }

    /**
     * 设置 服务线程数据
     * 
     * @param key 主键
     * @param info 数据
     * @see
     * @since 1.0
     */
    public void setServiceInfo(String key, Object info) {
        getServiceInfo().put(key, info);
    }

    /**
     * 取得 服务线程数据
     * 
     * @param key 主键
     * @return 服务线程数据
     * @see
     * @since 1.0
     */
    public Object getServiceInfo(String key) {
        return getServiceInfo().get(key);
    }

    /**
     * 清除线程缓存数据
     */
    public void cleanServiceInfo() {
        serviceInfo.remove();
        serviceInfo.set(new HashMap<>());
    }

    /**
     * 设置 执行结果
     * 
     * @param handleOK 执行结果
     * @see
     * @since 1.0
     */
    public void setHandleOK(boolean handleOK) {
        setServiceInfo(HANDLEOK, handleOK);
    }

    /**
     * 取得 执行结果
     * 
     * @return true:执行成功
     * @see
     * @since 1.0
     */
    public boolean isHandleOK() {
        Object handleOK = getServiceInfo(HANDLEOK);
        return handleOK != null && (boolean)handleOK;
    }

    /**
     * 取得 接口ID
     * 
     * @return 接口ID
     * @see
     * @since 1.0
     */
    public String getInfId() {
        return infId;
    }

    /**
     * 取得 报文时间戳
     *  
     * @return 报文时间戳
     */
    protected String getSendTimestamp(){
    	return (String) getServiceInfo(InfColumns.COM_STD400RSTS);
    }
 
    /**
     * 设置 报文时间戳
     * 
     * @param request 报文
     */
    private void setSendTimestamp(ClientData request){
    	String key = InfColumns.COM_STD400RSTS.fieldName();
    	setServiceInfo(key, request.getHeadField(key));
    }
    
    /**
     * 取得 消费系统代码
     * 
     * @return 消费系统代码
     * @see
     * @since 1.0
     */
    protected String getSysCode() {
        return (String)getServiceInfo(InfColumns.COM_SYSCODE);
    }

    /**
     * 取得 操作类型
     * 
     * @param request 请求数据
     * @return 操作类型
     * @see
     * @since 1.0
     */
    protected String getOpType() {
        return (String)getServiceInfo(InfColumns.COM_OPTYPE);
    }

    private void setOpType(ClientData request) {
        InfColumns infCol = InfColumns.COM_OPTYPE;
        setServiceInfo(infCol, request.getBodyField(infCol.fieldName()));
    }

    /**
     * 取得 报文流水号
     * 
     * @return 报文流水号
     */
    protected String getSeqNo() {
        return (String)getServiceInfo(InfColumns.COM_SEQ_NO);
    }

    private void setSeqNo(ClientData request) {
        InfColumns infCol = InfColumns.COM_SEQ_NO;
        setServiceInfo(infCol, request.getHeadField(infCol.fieldName()));
    }

    /**
     * 设置是否不需要资产
     * 
     * @param request 报文
     */
    protected void setAssetNoNeeds(ClientData request) {
        boolean isNoAsset = StringUtil.contains(InterfCacheUtil.getSysParam(INF_ASSET_NONEED_PS), (String)request.getBodyField("ps"));
        setServiceInfo(INF_ASSET_NONEED_PS, isNoAsset);
    }

    /**
     * 是否不需要资产
     * 
     * @return true:不需要
     */
    protected boolean isAssetNoNeeds(){
        return (boolean) getServiceInfo(INF_ASSET_NONEED_PS);
    }

    /**
     * 设置返回码
     * 
     * @param response 应答数据
     * @param type 信息类型
     * @param code 返回码
     * @see
     * @since 1.0
     */
    protected void setRtnCode(ClientData response, String type, int code) {
        String rtnCode = AAAAAAA;
        String msgType = InfDictCodeList.信息类型_正常.getValue();
        if (code != 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(10000 + code);
            // 系统标识
            //String sysFlag = InterfCacheUtil.getSysParam(InfColumns.COM_SYS_FLAG.fieldName());
            String sysFlag = InterfCacheUtil.getSysParam(RISK_SYS_CODE);
            rtnCode = StringUtil.isNotEmpty(sysFlag)?sysFlag:RISK_CODE + type + (code < 0 ? builder.toString() : builder.substring(1));
            msgType = code < 0 ? InfDictCodeList.信息类型_错误.getValue() : msgType;
        }
        // 信息类型
        response.setHeadField(InfColumns.COM_STD400MSGT.fieldName(), msgType);
        // 信息码
        response.setHeadField(InfColumns.COM_RTNCODE.fieldName(), rtnCode);
    }

    /**
     * 设置返回消息
     * 
     * @param response 应答数据
     * @param msg 返回消息
     * @see
     * @since 1.0
     */
    protected void setRtnMsg(ClientData response, String msg) {
        response.setHeadField(InfColumns.COM_RTNMSG.fieldName(), msg);
    }

    /**
     * 接口执行方法
     * 
     * @param request 请求数据
     * @return 处理结果（应答数据）
     * @see
     * @since 1.0
     */
    public Map<String, Object> execute(ClientData request) {
        String resStr = JsonUtils.object2Json(request);
        // 2.2.1 初始化数据
        ClientData response = new ClientData();
        // 将接口输入输出数据存入接口日志表
        writeLog(request, null, resStr);
        try {
            beforeHandle(request, response);
            // 校验数据内容
            if (!checkRequestErr(request, response)) {
                // 汇率换算
                exchangeCcy(request);
                // 2.2.2 调用数据处理方法（各接口实现）
                Map<String, Map<String, V>> result = handle(request, response);
                // 2.2.3 刷新Redis
                freshRedis(request, result);
                // 2.2.4 事件处理结束
                afterHandle(request, response);
            }
        }
        catch (Exception e) {
            // 2.2.5 异常处理
            doException(response, "处理异常", e);
        }
        // 将接口输入输出数据存入接口日志表
        writeLog(request, response, resStr);
        String rspStr = JsonUtils.object2Json(response);
    	getLogger().debug("应答报文:{}", rspStr);
        // 清除线程缓存
        cleanServiceInfo();
        return response.convertToMap();
    }

    /**
     * 是否需要根据净值计算金额/数量
     * 
     * @return true:是
     */
    protected boolean isCalcAmount(){
    	return true;
    }

    // add by zhangrui1 start
    protected static final String TRDNUMKEY = "tradeNum";
    protected static final String AMOUNTKEY = "amount";
    protected static final String MIXAMOUNTKEY = "mixAmount";
    protected static final String INF_BUY_PS_AMT = "infBuyPsAmt";
    protected static final String INF_SAL_PS_AMT = "infSalPsAmt";
    protected static final String INF_BUY_PS_NUM = "infBuyPsNum";
    protected static final String INF_SAL_PS_NUM = "infSalPsNum";
    protected static final String INF_EPS_PS_AMT = "infEpsPsAmt";
    protected static final String INF_EPS_PS_NUM = "infEpsPsNum";
    protected static final String ASSET_AMT_VAL = "assetAmtVal";
    protected static final String ASSET_AMT_INFO = "assetAmtInfo";

    /**
     * 更新实时资产规模
     * 
     * @param preCode 预占代码
     * @param num 本次实占数量/金额
     * @param consumer 处理函数
     * @param ocpList 历史实占列表
     * @return 实占超过预占的部分
     */
    protected BigDecimal ocpSurplusNum(String preCode, BigDecimal num,
                                       Function<String, BigDecimal> consumer,
                                       Set<String> ocpList) {
        // 无预占的场合
        if (consumer == null || StringUtil.isEmpty(preCode)) {
            return num;
        }
        // 预占的数量/金额
        BigDecimal allPreNum = consumer.apply(preCode);
        // 实占的数量/金额
        BigDecimal allOcpNum = num == null ? BigDecimal.ZERO : num;
        // 实时资产规模存在且实占的预占信息存在
        if (!CollectionUtils.isEmpty(ocpList)) {
            for (String ocp : ocpList) {
                allOcpNum = ArithmeticUtil.doAdd(allOcpNum,
                    consumer.apply(StringUtil.remove(ocp, GlobalConstant.COLON + "2")));
            }
        }
        // 实占超过预占的部分
        return allOcpNum.subtract(allPreNum);
    }

    /**
     * 计算占比金额
     * 
     * @param info 实时资产规模
     * @param num 资产数量
     * @param preOcpCode 预占编号
     */
    protected void calcMixAmount(ImplProdAssetAmount info, BigDecimal num, String preOcpCode) {
/**        if (StringUtil.isEmpty(preOcpCode)) { */
            // 占比金额
            setServiceInfo(ASSET_AMT_VAL, calcMixAmount(info.getAmount(), info.getNum(), num));
/**        }*/
/**        else {
            // 占比金额
            setServiceInfo(ASSET_AMT_VAL,
                calcMixAmount(info.ocpAmount(preOcpCode), info.ocpNum(preOcpCode), num));
        }*/
    }

    /**
     * 计算占比金额【目标数量 * 原金额 / 原数量】
     * 
     * @param oldAmt 原金额
     * @param oldNum 原数量
     * @param newNum 目标数量
     * @return 目标金额
     */
    protected BigDecimal calcMixAmount(BigDecimal oldAmt, BigDecimal oldNum, BigDecimal newNum) {
        BigDecimal newAmt = ArithmeticUtil.multiply(2, RoundingMode.HALF_UP, newNum, oldAmt);
        return CommonUtil.math(newAmt, oldNum, BussinessDictCodeListRds.操作符_除.getValue());
    }

    /**
     * 计算占比金额
     * 
     * @param request 报文
     * @param isRevoke 是否撤销
     * @param isPreOcp 是否预占释放
     * @param ocpList 预占下的实占列表
     */
    protected void calcMixAmount(Map<String, Object> request, boolean isRevoke, boolean isPreOcp,
                                 Set<String> ocpList) {
        calcMixAmount(request, isRevoke, isPreOcp, ocpList, false);
    }

    /**
     * 计算占比金额
     * 
     * @param request 报文
     * @param isRevoke 是否撤销
     * @param isPreOcp 是否预占释放
     * @param ocpList 预占下的实占列表
     * @param isRelease 是否释放
     */
    protected void calcMixAmount(Map<String, Object> request, boolean isRevoke, boolean isPreOcp,
                                 Set<String> ocpList, boolean isRelease) {
        if (isPreOcp) {
            return;
        }
        Object amountObj = request.get(AMOUNTKEY);
        Object trdNumObj = request.get(TRDNUMKEY);
        String buyPsAmt = InterfCacheUtil.getSysParam(INF_BUY_PS_AMT);
        String salPsAmt = InterfCacheUtil.getSysParam(INF_SAL_PS_AMT);
        String buyPsNum = InterfCacheUtil.getSysParam(INF_BUY_PS_NUM);
        String salPsNum = InterfCacheUtil.getSysParam(INF_SAL_PS_NUM);
        String epsPsAmt = InterfCacheUtil.getSysParam(INF_EPS_PS_AMT);
        String epsPsNum = InterfCacheUtil.getSysParam(INF_EPS_PS_NUM);
        String portfolioCode = (String) request.get("portfolioCode");
        String assetCode = (String) request.get("assetCode");
        if (StringUtil.isEmpty(assetCode)) {
            return;
        }
        // 取出资产实时规模
        ImplProdAssetAmount info = getProdAssetAmount(portfolioCode, assetCode);
        String ocpCode = (String) request.get("eventId");
        // 预占编号
        String preCode = (String)request.get("preOccupyCode");
        if (info == null) {
            info = new ImplProdAssetAmount();
            info.setPortfolioCode(portfolioCode);
            info.setAssetCode(assetCode);
            info.setNum(BigDecimal.ZERO);
            info.setAmount(BigDecimal.ZERO);
        }
        ocpList = new HashSet<>();
        if (isRelease && isRevoke) {
            // 预占不需要计算实时规模
            ocpList.add(ocpCode);
            releaseMixAmount(info, isPreOcp, ocpCode, isPreOcp ? ocpCode : preCode, ocpList);
            return;
        }
        String ps = (String)request.get("ps");
        String assetType = request.get("assetType") + GlobalConstant.COMMA + ps;
        BigDecimal amount = amountObj == null ? BigDecimal.ZERO : new BigDecimal(
            amountObj.toString());
        BigDecimal num = trdNumObj == null ? BigDecimal.ZERO : new BigDecimal(
            trdNumObj.toString());
        // 当数量存在时，计算占比金额
        if (trdNumObj != null && (isNumChanged(ps, buyPsNum, epsPsNum, assetType) 
                || isNumChanged(ps, salPsNum, epsPsNum, assetType))) {
            // 计算占比金额
            calcMixAmount(info, num, preCode);
            request.put(MIXAMOUNTKEY, getServiceInfo(ASSET_AMT_VAL));
        }
        else {
            setServiceInfo(ASSET_AMT_VAL, null);
        }
        BigDecimal ocpAmt = null;
        BigDecimal ocpNum = null;
        final ImplProdAssetAmount infoBk = info;
        // 买入方向影响金额则加
        if (StringUtil.contains(buyPsAmt, ps) && !StringUtil.contains(epsPsAmt, assetType)) {
            if (isRelease) {
                info.setAmount(CommonUtil.math(info.getAmount(), amount.negate(),
                    BussinessDictCodeListRds.操作符_加.getValue()));
            }
            else {
                BigDecimal amt = ocpSurplusNum(preCode, amount, code -> infoBk.ocpAmount(code), ocpList);
                info.setAmount(CommonUtil.math(info.getAmount(),amt,BussinessDictCodeListRds.操作符_加.getValue()));
            }
            ocpAmt = amount;
        }
        // 卖出方向影响金额则减
        if (StringUtil.contains(salPsAmt, ps) && !StringUtil.contains(epsPsAmt, assetType)) {
            BigDecimal mixAmt = getMixAmount();
            mixAmt = StringUtil.isNotEmpty(mixAmt) ? mixAmt : amount;
            if (isRelease) {
                info.setAmount(CommonUtil.math(info.getAmount(), mixAmt.negate(),
                    BussinessDictCodeListRds.操作符_减.getValue()));
                ocpAmt = mixAmt.negate();
            }
            else {
            info.setAmount(CommonUtil.math(info.getAmount(),
                BigDecimal.ZERO.max(
                    ocpSurplusNum(preCode, mixAmt, code -> infoBk.ocpAmount(code), ocpList)),
                BussinessDictCodeListRds.操作符_减.getValue()));
                ocpAmt = mixAmt.negate();
            }
        }
        // 买入方向影响数量则减
        if (StringUtil.contains(buyPsNum, ps) && !StringUtil.contains(epsPsNum, assetType)) {
            if (isRelease) {
                info.setNum(CommonUtil.math(info.getNum(), num.negate(),
                    BussinessDictCodeListRds.操作符_加.getValue()));
            }
            else {
                BigDecimal changeNum = ocpSurplusNum(preCode, num, code -> infoBk.ocpAmount(code), ocpList);
                info.setNum(CommonUtil.math(info.getNum(),changeNum,BussinessDictCodeListRds.操作符_加.getValue()));
            }
            ocpNum = num;
        }
        // 卖出方向影响数量则减
        if (StringUtil.contains(salPsNum, ps) && !StringUtil.contains(epsPsNum, assetType)) {
            if (isRelease) {
                info.setNum(CommonUtil.math(info.getNum(), num.negate(),
                    BussinessDictCodeListRds.操作符_减.getValue()));
            }
            else {
            info.setNum(CommonUtil.math(info.getNum(),
                BigDecimal.ZERO.max(
                    ocpSurplusNum(preCode, num, code -> infoBk.ocpAmount(code), ocpList)),
                BussinessDictCodeListRds.操作符_减.getValue()));
            }
            ocpNum = num.negate();
        }
        if (isRelease && !isRevoke) {
            info.subOcp(ocpCode, ocpAmt, ocpNum);
        }
        else {
            info.addOcp(ocpCode, ocpAmt, ocpNum);
        }
        // 将资产实时规模保存在本地缓存中
        setServiceInfo(ASSET_AMT_INFO, info);
    }

    protected void releaseMixAmount(ImplProdAssetAmount info, boolean isPreOcp, String ocpCode,
                                    String preCode,
                                    Set<String> ocpList) {
        if (info == null || CollectionUtils.isEmpty(info.getOcpList())) {
            return;
        }
        // 实占预占金额的差额
        BigDecimal temp = StringUtil.isEmpty(preCode) ? info.ocpAmount(ocpCode) : ocpSurplusNum(
            preCode, BigDecimal.ZERO, code -> info.ocpAmount(code), ocpList);
        // 预占时，|实占-预占|.取反 作为撤销的实时规模，实占时，(实占-预占).取反 作为撤销的实时规模（实占超过预占的部分）
        BigDecimal amount = temp.negate();
        // 实占预占数量的差额
        temp = StringUtil.isEmpty(preCode) ? info.ocpNum(ocpCode) : ocpSurplusNum(preCode,
            BigDecimal.ZERO, code -> info.ocpNum(code), ocpList);
        BigDecimal num = temp.negate();
        info.setAmount(info.getAmount().add(amount));
        info.setNum(info.getNum().add(num));
        info.removeOcp(ocpCode);
        // 将资产实时规模保存在本地缓存中
        setServiceInfo(ASSET_AMT_INFO, info);
    }

    /**
     * 取得实时资产规模信息
     * 
     * @param portfolioCode 组合代码
     * @param assetCode 资产代码
     * @return 实时资产规模信息
     */
    protected ImplProdAssetAmount getProdAssetAmount(String portfolioCode, String assetCode) {
        return (ImplProdAssetAmount) prodAssetAmtSrc.getProdAssetAmount(portfolioCode, assetCode);
    }

    protected boolean isNumChanged(String ps, String psNum, String eps, String assetType) {
        return StringUtil.contains(psNum, ps) && !StringUtil.contains(eps, assetType);
    }

    protected BigDecimal getMixAmount() {
        return (BigDecimal) getServiceInfo(ASSET_AMT_VAL);
    }
    /**
     * 更新资产实时规模
     * 
     * @param request 报文
     */
    protected void refreshAssetAmout() {
        IProdAssetAmount info = (IProdAssetAmount) getServiceInfo(ASSET_AMT_INFO);
        if (info != null) prodAssetAmtSrc.saveToRedis(info);
    }

    protected void saveAssetAmount(IProdAssetAmount... info) {
        if (info != null) prodAssetAmtSrc.saveToRedis(info);
    }
    // add by zhangrui1 end
    protected void calcAmount(Map<String,Object> request, com.joyintech.tams.invest.common.CommonUtil.DoHelper<BigDecimal> consumer){
    	final String old = "Old";
    	Object amountObj = request.get(AMOUNTKEY);
    	Object trdNumObj = request.get(TRDNUMKEY);
    	// 备份数据
    	request.put(AMOUNTKEY + old, amountObj);
    	request.put(TRDNUMKEY + old, trdNumObj);
    	if ((amountObj == null || StringUtil.isEmpty(amountObj.toString()))
    			&& (trdNumObj == null || StringUtil.isEmpty(trdNumObj.toString()))) {
    		return;
    	}
    	if ((amountObj != null && !StringUtil.isEmpty(amountObj.toString()))
    			&& (trdNumObj != null && !StringUtil.isEmpty(trdNumObj.toString()))
    			&& BigDecimal.ZERO.compareTo(new BigDecimal(amountObj.toString())) != 0
    			&& BigDecimal.ZERO.compareTo(new BigDecimal(trdNumObj.toString())) != 0) {
    		return;
    	}
    	//是否需要跳过-对单次计算匹配
    	if(request.get("notCalcAmount")!=null&&request.get("notCalcAmount")instanceof Boolean &&(Boolean)request.get("notCalcAmount")) {
    		return ;
    	}

    	String assetCode = (String) request.get("assetCode");
		BigDecimal unitNet = consumer == null ?BigDecimal.ONE : consumer.handle();
		if (unitNet == null) {
			// 如果基金净值没有则按1计算
			getLogger().debug("没有取到{}的净值，按1计算", assetCode);
			unitNet = BigDecimal.ONE;
		}
    	if (amountObj == null || StringUtil.isEmpty(amountObj.toString()) 
    			|| BigDecimal.ZERO.compareTo(new BigDecimal(amountObj.toString())) == 0) {
    		if(trdNumObj == null) trdNumObj = BigDecimal.ZERO;
    		BigDecimal trdNum = trdNumObj instanceof BigDecimal ? (BigDecimal) trdNumObj : new BigDecimal(trdNumObj.toString());
            BigDecimal amount = trdNum.multiply(unitNet);
            if (unitNet.compareTo(BigDecimal.ONE)==0){
                amount = StringUtil.isNotEmpty(request.get("totalFaceAmt"))?new BigDecimal(request.get("totalFaceAmt").toString()):amount;
            }
    		request.put(AMOUNTKEY, amount);
    	}
    	else if (trdNumObj == null || StringUtil.isEmpty(trdNumObj.toString())
    			|| BigDecimal.ZERO.compareTo(new BigDecimal(trdNumObj.toString())) == 0) {
    		BigDecimal amount = amountObj instanceof BigDecimal ? (BigDecimal) amountObj : new BigDecimal(amountObj.toString());
    		BigDecimal trdNum = CommonUtil.math(amount, unitNet, BussinessDictCodeListRds.操作符_除.getValue());
    		request.put(TRDNUMKEY, trdNum);
    	}
    }

    /**
     * 从redis取得净值
     * 
     * @param assetCode 资产代码
     * @return 净值
     */
    protected BigDecimal getUnitPrice(String assetCode){
        return fundValSrc.getFundValuation(assetCode);
    }

    /**
     * 汇率换算
     * 
     * @param request 请求数据
     */
    protected void exchangeCcy(ClientData request){
        String ccy = (String) request.getBodyField("ccy");
        if (CommonUtil.isType(ccy, BussinessDictCodeListRds.币种_人民币_CNY)) {
            return;
        }
        // 取得 汇率值
        BigDecimal rate = exchargeSrc.getDefaultExchargeVal(ccy);
        // 金额换算
        exchange(request, "amount", rate);
        // 价格换算
        exchange(request, "priceAmt", rate);
        // 净价换算
        exchange(request, "netPrice", rate);
        // 全价换算
        exchange(request, "allPrice", rate);
        // 单位净值换算
        exchange(request, "unitNet", rate);
        // 资产利率
        exchange(request, "assetRate", rate);
    }

    /**
     * 汇率换算
     * 
     * @param request 请求数据
     * @param key 字段名
     * @param exchangeVal 汇率
     */
    protected void exchange(ClientData request, String key, BigDecimal exchangeVal){
        try{
            Object obj = request.getBodyField(key);
            BigDecimal val = null;
            if (!StringUtil.isNotEmpty(obj)) {
//                request.setBodyField(key, BigDecimal.ZERO);
                return;
            }
            if (obj instanceof BigDecimal) {
                val = (BigDecimal) obj;
            }
            else {
                val = new BigDecimal(obj.toString());
            }
            val = CommonUtil.exchangeToCny(val, exchangeVal);
            request.setBodyField(key, val);
        }
        catch(Exception e){
            logger.error("汇率转换出错：", e);
        }
    }

    /**
     * check接口输入
     * 
     * @param request 请求数据
     * @param response 应答数据
     * @return check结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	protected boolean checkRequestErr(ClientData request, ClientData response) {
    	// 对body内容为list的请求进行循环check 王贵奇 20200512
    	if(!StringUtil.isEmptyList((List<?>) request.getBody().get("rskCalcList"))) {
    		boolean flag;
    		List<Map<String, Object>> list = (List<Map<String, Object>>) request.getBody().get("rskCalcList");
    		for(Map<String, Object> map :list) {
    			V info = InfMapValUtil.mapDataToObject(map, getParamClass());
    			flag = checkErr(info, response);
    			if(flag) return true;
    		}
    		return false;
    	}else {
    		V info = InfMapValUtil.mapDataToObject(request.getBody(), getParamClass());
    		return checkErr(info, response);
        }
    }

    /**
     * check报文体
     * 
     * @param obj 报文体
     * @param response 应答数据
     * @return check结果，true:不通过、false:通过
     * @see
     * @since 1.0
     */
    protected boolean checkErr(V obj, ClientData response) {
        BindingResult br = new BeanPropertyBindingResult(obj, "调用风险报文");
        validator.validate(obj, br);
        // 是否有错误
        boolean res = br.hasErrors();
        if (res) {
            FieldError err = br.getFieldError();
            StringBuilder errMsg = new StringBuilder(err.getObjectName());
            errMsg.append(GlobalConstant.POINT).append(err.getField()).append(
                err.getDefaultMessage());
            // 异常处理
            doException(response, errMsg.toString(), null);
        }
        else {
            res = checkWithCond(obj, response);
        }
        return res;
    }

    /**
     * 条件check
     * 
     * @param obj 接口输入
     * @param response 返回数据
     * @return check结果
     * @see
     * @since 1.0
     */
    protected abstract boolean checkWithCond(V obj, ClientData response);

    /**
     * 初始化应答数据
     * 
     * @param request 请求数据
     * @param response 应答数据
     * @see
     * @since 1.0
     */
    protected void beforeHandle(ClientData request, ClientData response) {
        String sysCode = InfColumns.COM_SYSCODE.fieldName();
        // 设置消费系统代码
        setServiceInfo(sysCode, request.getHeadField(sysCode));
        String seqNo = InfColumns.COM_SEQ_NO.fieldName();
        // 输出.head.报文类型
        String stdmsgtype = InfColumns.COM_STDMSGTYPE.fieldName();
        response.setHeadField(stdmsgtype, request.getHeadField(stdmsgtype));
        // 输出.head.多页查询标志
        String std400pgqf = InfColumns.COM_STD400PGQF.fieldName();
        response.setHeadField(std400pgqf, request.getHeadField(std400pgqf));
        // 输出.head.接口ID
        response.setHeadField(InfColumns.COM_INFID.fieldName(), getInfId());
        // 输出.head.交易流水号
        response.setHeadField(seqNo, request.getHeadField(seqNo));
        // 输出.head.信息类型
        String std400msgt = InfColumns.COM_STD400MSGT.fieldName();
        response.setHeadField(std400msgt, "N");
        // 输出.head.返回码
        response.setHeadField(InfColumns.COM_RTNCODE.fieldName(), AAAAAAA);
        // 输出.head.返回信息
        response.setHeadField(InfColumns.COM_RTNMSG.fieldName(), "处理成功");
        // 设置操作类型
        setOpType(request);
        // 设置报文时间戳
        setSendTimestamp(request);
        // 设置是否不需要资产代码
        setAssetNoNeeds(request);
        // 设置报文流水号
        setSeqNo(request);
    }

    /**
     * 刷新Redis
     * 
     * @param request 请求数据
     * @param rdsHash 刷新Redis内容
     * @see
     * @since 1.0
     */
    protected void freshRedis(ClientData request, Map<String, Map<String, V>> rdsHash) {
        // 当内容为空时，不做处理
        if (rdsHash == null) {
            return;
        }
        // 取得操作类型
        String opType = getOpType();
        // 删除
        if (InfDictCodeList.isType(opType, InfDictCodeList.操作类型_删除)) {
            redisWriter.deleteOne(rdsHash);
        }
        // 更新
        else {
            redisWriter.writeList(rdsHash);
        }
    }

    /**
     * 结束处理
     * 
     * @param request 请求数据
     * @param response 应答数据
     * @see
     * @since 1.0
     */
    protected void afterHandle(ClientData request, ClientData response) {
        String tranDate = DateUtil.getCurrentDate();
        String tranTime = DateUtil.format(DateUtil.now(), DateUtil.DATETIME_FORMAT_WITH_SSS_YEAR2);
        tranTime = StringUtil.substring(tranTime, 4);
        // 输出.header.发送日期
        response.setHeadField(InfColumns.COM_TRANDATE.fieldName(), tranDate);
        // 输出.header.发送时间
        response.setHeadField(InfColumns.COM_TRANTIME.fieldName(), tranTime);
    }

    /**
     * 登录日志表
     * 
     * @see
     * @since 1.0
     */
    protected void writeLog(ClientData request, ClientData response, String resStr) {
        InterfRiskLogWithBLOBs info = new InterfRiskLogWithBLOBs();
        String rtnCode = response == null ? AAAAAAA : (String)response.getHeadField(InfColumns.COM_RTNCODE.fieldName());
        // 接口ID
        info.setInfId(getInfId());
        // 返回码
        info.setRtnCode(String.valueOf(rtnCode));
        // 返回消息
        info.setRtnMsg(response == null ? null : (String)response.getHeadField(InfColumns.COM_RTNMSG.fieldName()));
        String sysCode = (String)request.getHeadField(InfColumns.COM_SYSCODE.fieldName());
        // 系统代码
        info.setSysCode(sysCode);
        // 用户IP
        info.setIpAddress((String)request.getBodyField(InfColumns.COM_IP.fieldName()));
        // 用户端口号
        info.setPort((int)request.getBodyField(InfColumns.COM_PORT.fieldName()));
        // 报文流水号
        info.setSeqNo((String)request.getHeadField(InfColumns.COM_SEQ_NO.fieldName()));
        // 发送日期
        info.setTranDate((String)request.getHeadField(InfColumns.COM_TRANDATE.fieldName()));
        // 发送时间
        info.setTranTime((String)request.getHeadField(InfColumns.COM_TRANTIME.fieldName()));
        // 请求数据
        info.setRequestData(resStr);
        // 应答数据
        info.setResponseData(response == null ? null : JsonUtils.object2Json(response));
        // 处理状态
        info.setDoStatus(StringUtil.equals(rtnCode,
            AAAAAAA) ? InfDictCodeList.处理状态_成功.getValue() : InfDictCodeList.处理状态_失败.getValue());
        ProducerUtils.getInstance().produce(Operation.W, info);
    }

    /**
     * 异常处理
     * 
     * @param msg 异常信息
     * @param e 异常 {@link Exception}
     * @see
     * @since 1.0
     */
    protected void doException(ClientData response, String msg, Exception e) {
        String errMsg = msg;
        if (e instanceof InterfException) {
            errMsg = errMsg + "【" + StringUtil.substring(e.getMessage(), 0, 60) + "】";
        }
        getLogger().error(errMsg, e);
        setRtnCode(response, InfDictCodeList.信息码_拒绝.getValue(), -1);
        filterPreExceptiion(response,e);
        setRtnMsg(response, errMsg);
    }

    /**
     * 获取参数Class类
     * 
     * @return 参数Class类
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected Class<V> getParamClass() {
        return (Class<V>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 接口执行方法
     * 
     * @param request 请求数据
     * @param response 应答数据
     * @return 刷新的Redis数据,若不需要刷新Redis则返回null
     * @exception InterfException 接口异常
     * @see
     * @since 1.0
     */
    protected abstract Map<String, Map<String, V>> handle(ClientData request, ClientData response)
        throws InterfException;

    /**
     * 异步保存数据到DB
     * 
     * @param info 数据
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    protected <T> void saveDb(Operation opr, T... info) {
        producer.produce(opr, info);
    }

    /**
     * 业务处理，每5ms轮询取锁，当获取锁时执行，否则等待
     * 
     * @param data 接口数据
     * @param helper 处理逻辑接口
     * @return 处理结果
     * @throws InterfException 异常
     * @see
     * @since 1.0
     */
    protected <T> T handleByLock(DoHelper<T> helper)
        throws InterfException {
        T t = null;
        long start = System.currentTimeMillis();
        while (true) {
            // 如果能获取锁则执行处理，否则阻塞
            if (Locker.lock()) {
                try {
                    t = helper.handle();
                }
                catch (Exception e) {
                    getLogger().error("处理异常", e);
                    throw new InterfException(e.getMessage());
                }
                finally {
                    // 释放锁
                    Locker.unLock();
                }
                break;
            }
            try {
                Thread.sleep(5);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                getLogger().error("处理异常", e);
                throw new InterfException("异常中断");
            }
        }
        getLogger().info("Handle by locker time is {}", (System.currentTimeMillis() - start));
        return t;
    }

    /**
     * 添加对预占信息不存在的过滤
     * @param response
     * @param e
     */
    private void filterPreExceptiion(ClientData response,Exception e){
        if(null!=e && e instanceof InterfException
                &&StringUtil.endsWith(e.getMessage(),PRE_NOT_EXISTS)){
            //信息类型
            response.setHeadField(InfColumns.COM_STD400MSGT.fieldName(),InfDictCodeList.信息类型_正常.getValue());
        }

    }


    protected interface DoHelper<T> {

        T handle()
            throws InterfException;
    }

    protected interface ItConsumer<T, V> {
    	V accept(T t);
    }

    /**
     * 取得日志对象
     * 
     * @return 日志对象
     * @see
     * @since 1.0
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * 是否释放接口
     * 
     * @return true:是
     */
    protected boolean isReleaseOcp(){
        return false;
    }
}
