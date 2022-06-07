/*
 * 文件名：InterfRiskRdsDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年1月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.riskrds.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.redisinit.dto.IRiskRds;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 接口参数-实占实体
 * 
 * @author 裴家俊
 * @version 1.0 2020年1月5日
 * @see InterfRiskRdsDto
 * @since 1.0
 */
public class InterfRiskRdsDto implements IRiskRds {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 6066539768504017283L;

    /**
     * 流水号
     */
    private String uuid;

    /**
     * 接口代码RSK001-RSK099
     */
    private String infCode;
    /**
     * 事件日期
     */
    private String eventDate;

    /**
     * 实占编号（交易代码）
     */
    private String eventId;

    /**
     * 事件类型(1：投资交易、2：交易拆分)
     */
    private String eventType;

    /**
     * 事件状态(1：交易、2：存续期)
     */
    private String eventStatus;

    /**
     * 委托编号
     */
    private String entrustCode;

    /**
     * 预占编号
     */
    private String occupyCode;

    /**
     * 产品代码
     */
    private String portfolioCode;

    /**
     * 资产代码
     */
    private String assetCode;

    /**
     * 系统资产大类
     */
    private String assetType;

    /**
     * 系统资产细类
     */
    private String assetType2;

    /**
     * 金融产品分类_投管
     */
    private String finType;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal amount;

    /**
     * 价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal priceAmt;

    /**
     * 数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeNum;

    /**
     * 净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal netPrice;

    /**
     * 全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal allPrice;

    /**
     * 单位净值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitNet;

    /**
     * 到期收益率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yieldEndRate;

    /**
     * 资产利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal assetRate;

    /**
     * 资产到期日(为非标的所用)
     */
    private String mdate;
 /**
     * 投资目的
     */
    private String invAim;
 /**
     * 期限品种 add by wz 20200509
     */
    private String varietyCode;

    /**
     * 是否忽略试算状态（可转债申购确认用）
     */
    private String ignoreStatus;

    /**
     * 是否无视违规
     */
    private String isIgnoreFoul;

    /**
     * 期限品种 add by wz 20200509
     * @return
     */
    public String getVarietyCode() {
        return varietyCode;
    }

    public void setVarietyCode(String varietyCode) {
        this.varietyCode = varietyCode;
    }

    /**
     * 回购期限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal repoTerm;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 投资经理
     */
    private String manager;

    /**
     * 经办人
     */
    private String createUser;

    /**
     * 复核人
     */
    private String approveUser;

    /**
     * 交割日
     */
    private String deliveryDate;

    /**
     * 委托开始日
     */
    private String startDate;

    /**
     * 委托结束日
     */
    private String endDate;

    /**
     * 状态，预占、释放
     */
    private String ocpStatus;

    /**
     * 当前流程状态，待审批、审批退回、审批通过、直接通过
     */
    private String wflStatus;

    /**
     * 达到预警线是否需要人工确认
     */
    private String isWarnNeedConfirm;

    /**
     * 达到审批线是否需要人工确认
     */
    private String isApprNeedConfirm;

    /**
     * 事件类型为1且有预占编号的必须要传（预占后实占前点击风险试算按钮时）
     */
    private String preOccupyCode;

    /**
     * 有预占编号时必填  已经做过预占的交易要标明预占是哪个系统做的预占
     */
    private String sourceSys;

    /**
     * 交易对手，转仓必填
     */
    private String counterId;

    /**
     * 市场代码
     */
    private String marketCode;

    /**
     * 交易场所
     */
    private String tradePlace;

    /**
     * 交易市场
     */
    private String tradeMarketMode;

    /**
     * 产品总资产(虚拟组合所用)
     */
    private BigDecimal tdyAssetValue;

    /**
     * 产品净资产(虚拟组合所用)
     */
    private BigDecimal tdyAssetNetValue;

    /**
     * 资产名称(虚拟组合所用)
     */
    private String assetName;

    /**
     * 产品到期日(虚拟组合所用)
     */
    private String prdMdate;

    /**
     * 投资性质(虚拟组合所用)
     */
    private String investNature;

    /**
     * 是否非标(虚拟组合所用)
     */
    private String isStandard;

    /**
     * 发行规模(虚拟组合所用)
     */
    private String publicAmount;

    /**
     * 分组代码(多对多指令，为了区分是哪一笔计算的指标，格式[类型_委托/交易_序号])
     */
    private String groupCode;

    /**
     * 是否虚拟资产(是/否)
     */
    private String isVirtual;

    /**
     * 机构代码
     */
    private String issuerCode;

    /**
     * 是否股东发行
     */
    private String isShareHolder;

    public BigDecimal getTdyAssetValue() {
        return tdyAssetValue;
    }

    public void setTdyAssetValue(BigDecimal tdyAssetValue) {
        this.tdyAssetValue = tdyAssetValue;
    }

    public BigDecimal getTdyAssetNetValue() {
        return tdyAssetNetValue;
    }

    public void setTdyAssetNetValue(BigDecimal tdyAssetNetValue) {
        this.tdyAssetNetValue = tdyAssetNetValue;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getPrdMdate() {
        return prdMdate;
    }

    public void setPrdMdate(String prdMdate) {
        this.prdMdate = prdMdate;
    }

    public String getIsVirtual() {
        return isVirtual;
    }

    public void setIsVirtual(String isVirtual) {
        this.isVirtual = isVirtual;
    }

    /**
     * 金额(无利息)
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal amountEx;

    /**
     * 交易币种
     */
    private String ccy= BussinessDictCodeListRds.币种_人民币_CNY.getValue();

    /**
     * 撤销实占标识
     */
    private String revokeFlg= BussinessDictCodeListRds.是否_否.getValue();

    /**
     * 机构代码
     */
    private String instCode;

    /**
     *
     *
     * 取得 流水号
     * @return 流水号
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置 流水号
     * @param uuid 流水号
     */
    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    /**
     * 取得 事件日期
     * @return 事件日期
     */
    public String getEventDate() {
        return eventDate;
    }

    /**
     * 设置 事件日期
     * @param eventDate 事件日期
     */
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate== null ? null : eventDate.trim();
    }

    /**
     * 取得 实占编号（交易代码）
     * @return 实占编号（交易代码）
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * 设置 实占编号（交易代码）
     * @param eventId 实占编号（交易代码）
     */
    public void setEventId(String eventId) {
        this.eventId = eventId == null ? null : eventId.trim();
    }

    /**
     * 取得 事件类型(1：投资交易、2：交易拆分)
     * @return 事件类型(1：投资交易、2：交易拆分)
     */
    @Override
    public String getEventType() {
        return eventType;
    }

    /**
     * 设置 事件类型(1：投资交易、2：交易拆分)
     * @param eventType 事件类型(1：投资交易、2：交易拆分)
     */
    @Override
    public void setEventType(String eventType) {
        this.eventType = eventType == null ? null : eventType.trim();
    }

    /**
     * 取得 委托编号
     * @return 委托编号
     */
    public String getEntrustCode() {
        return entrustCode;
    }

    /**
     * 设置 委托编号
     * @param entrustCode 委托编号
     */
    public void setEntrustCode(String entrustCode) {
        this.entrustCode = entrustCode == null ? null : entrustCode.trim();
    }

    /**
     * 取得 预占编号
     * @return 预占编号
     */
    public String getOccupyCode() {
        return occupyCode;
    }

    /**
     * 设置 预占编号
     * @param occupyCode 预占编号
     */
    public void setOccupyCode(String occupyCode) {
        this.occupyCode = occupyCode == null ? null : occupyCode.trim();
    }

    public String getPortfolioCode() {
        return portfolioCode;
    }

    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    public String getAssetCode() {
        return assetCode;
    }

    /**
     * 设置 资产代码
     * @param assetCode 资产代码
     */
    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode == null ? null : assetCode.trim();
    }

    /**
     * 取得 系统资产大类
     * @return 系统资产大类
     */
    @Override
    public String getAssetType() {
        return assetType;
    }

    /**
     * 设置 系统资产大类
     * @param assetType 系统资产大类
     */
    @Override
    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    /**
     * 取得 系统资产细类
     * @return 系统资产细类
     */
    public String getAssetType2() {
        return assetType2;
    }

    /**
     * 设置 系统资产细类
     * @param assetType2 系统资产细类
     */
    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2 == null ? null : assetType2.trim();
    }

    /**
     * 取得 交易方向
     * @return 交易方向
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向
     * @param ps 交易方向
     */
    public void setPs(String ps) {
        this.ps = ps == null ? null : ps.trim();
    }

    /**
     * 取得 金额
     * @return 金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置 金额
     * @param amount 金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 取得 价格
     * @return 价格
     */
    public BigDecimal getPriceAmt() {
        return priceAmt;
    }

    /**
     * 设置 价格
     * @param priceAmt 价格
     */
    public void setPriceAmt(BigDecimal priceAmt) {
        this.priceAmt = priceAmt;
    }

    /**
     * 取得 数量
     * @return 数量
     */
    public BigDecimal getTradeNum() {
        return tradeNum;
    }

    /**
     * 设置 数量
     * @param tradeNum 数量
     */
    public void setTradeNum(BigDecimal tradeNum) {
        this.tradeNum = tradeNum;
    }

    /**
     * 取得 净价
     * @return 净价
     */
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    /**
     * 设置 净价
     * @param netPrice 净价
     */
    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /**
     * 取得 全价
     * @return 全价
     */
    public BigDecimal getAllPrice() {
        return allPrice;
    }

    /**
     * 设置 全价
     * @param allPrice 全价
     */
    public void setAllPrice(BigDecimal allPrice) {
        this.allPrice = allPrice;
    }

    /**
     * 取得 单位净值
     * @return 单位净值
     */
    public BigDecimal getUnitNet() {
        return unitNet;
    }

    /**
     * 设置 单位净值
     * @param unitNet 单位净值
     */
    public void setUnitNet(BigDecimal unitNet) {
        this.unitNet = unitNet;
    }

    /**
     * 取得 资产利率
     * @return 资产利率
     */
    public BigDecimal getAssetRate() {
        return assetRate;
    }

    /**
     * 设置 资产利率
     * @param assetRate 资产利率
     */
    public void setAssetRate(BigDecimal assetRate) {
        this.assetRate = assetRate;
    }

    /**
     * 取得 交易员
     * @return 交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 交易员
     * @param trader 交易员
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
    }

    /**
     * 取得 投资经理
     * @return 投资经理
     */
    public String getManager() {
        return manager;
    }

    /**
     * 设置 投资经理
     * @param manager 投资经理
     */
    public void setManager(String manager) {
        this.manager = manager == null ? null : manager.trim();
    }

    /**
     * 取得 交割日
     * @return 交割日
     */
    public String getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * 设置 交割日
     * @param deliveryDate 交割日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = DateUtil.format(deliveryDate);
    }

    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * 设置 委托开始日
     * @param startDate 委托开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setStartDate(Date startDate) {
        this.startDate = DateUtil.format(startDate);
    }

    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * 设置 委托结束日
     * @param endDate 委托结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setEndDate(Date endDate) {
        this.endDate = DateUtil.format(endDate);
    }

    /**
     * 取得 状态，预占、释放
     * @return 状态，预占、释放
     */
    public String getOcpStatus() {
        return ocpStatus;
    }

    /**
     * 设置 状态，预占、释放
     * @param ocpStatus 状态，预占、释放
     */
    public void setOcpStatus(String ocpStatus) {
        this.ocpStatus = ocpStatus == null ? null : ocpStatus.trim();
    }

    /**
     * 取得 当前流程状态，待审批、审批退回、审批通过、直接通过
     * @return 当前流程状态，待审批、审批退回、审批通过、直接通过
     */
    public String getWflStatus() {
        return wflStatus;
    }

    /**
     * 设置 当前流程状态，待审批、审批退回、审批通过、直接通过
     * @param wflStatus 当前流程状态，待审批、审批退回、审批通过、直接通过
     */
    public void setWflStatus(String wflStatus) {
        this.wflStatus = wflStatus == null ? null : wflStatus.trim();
    }

    public String getIsWarnNeedConfirm() {
        return isWarnNeedConfirm;
    }

    public void setIsWarnNeedConfirm(String isWarnNeedConfirm) {
        this.isWarnNeedConfirm = isWarnNeedConfirm;
    }

    public String getIsApprNeedConfirm() {
        return isApprNeedConfirm;
    }

    public void setIsApprNeedConfirm(String isApprNeedConfirm) {
        this.isApprNeedConfirm = isApprNeedConfirm;
    }

    public String getPreOccupyCode() {
        return preOccupyCode;
    }

    public void setPreOccupyCode(String preOccupyCode) {
        this.preOccupyCode = preOccupyCode;
    }

    public String getSourceSys() {
        return sourceSys;
    }

    public void setSourceSys(String sourceSys) {
        this.sourceSys = sourceSys;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    @Override
    public String getInfCode() {
        return infCode;
    }

    @Override
    public void setInfCode(String infCode) {
        this.infCode = infCode;
    }

    public String getRevokeFlg() {
        return revokeFlg;
    }

    public void setRevokeFlg(String revokeFlg) {
        this.revokeFlg = revokeFlg;
    }

    @Override
    public String getEventStatus() {
        return this.eventStatus;
    }

    @Override
    public void setEventStatus(String eventStatus) {
         this.eventStatus=eventStatus;
    }

    @Override
    public String getFinType() {
        return finType;
    }

    @Override
    public void setFinType(String finType) {
        this.finType = finType;
    }

    public BigDecimal getYieldEndRate() {
        return yieldEndRate;
    }

    public void setYieldEndRate(BigDecimal yieldEndRate) {
        this.yieldEndRate = yieldEndRate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    // add by zhangrui1 添加是否内部交易 Start
    /**
     * 是否内部交易
     */
    private String isInnerTrd;

    public String getIsInnerTrd() {
        return this.isInnerTrd;
    }

    public void setIsInnerTrd(String isInnerTrd) {
        this.isInnerTrd = isInnerTrd;
    }
    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = DateUtil.format(mdate);
    }

    // add by zhangrui1 添加是否内部交易 end
    // add by zhangrui1 添加债券券面总额（元） start
    /**
     * 券面总额（元）
     */
    private BigDecimal totalFaceAmt;

    /**
     * 获取 券面总额（元）
     * 
     * @return 券面总额（元）.
     */
    public BigDecimal getTotalFaceAmt() {
        return totalFaceAmt;
    }

    /**
     * 设置 券面总额（元）
     * 
     * @param totalFaceAmt 券面总额（元）
     */
    public void setTotalFaceAmt(BigDecimal totalFaceAmt) {
        this.totalFaceAmt = totalFaceAmt;
    }
    // add by zhangrui1 添加债券券面总额（元） end
    
    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    // add by zhangrui1 拆借回购等没有资产的需要传日历代码 start
    /**
     * 日历代码
     */
    private String calendarCode;

    /**
     * 获取 日历代码
     * 
     * @return 日历代码.
     */
    public String getCalendarCode() {
        return calendarCode;
    }

    /**
     * 设置 日历代码
     * 
     * @param calendarCode 日历代码
     */
    public void setCalendarCode(String calendarCode) {
        this.calendarCode = calendarCode;
    }
    // add by zhangrui1 拆借回购等没有资产的需要传日历代码 end
    // add by zhangrui1 回购交易计算剩余期限 start
    /**
     * 剩余期限
     */
    private long oneDay;

    /**
     * 获取 剩余期限
     * 
     * @return 剩余期限.
     */
    public long getOneDay() {
        return oneDay;
    }

    /**
     * 设置 剩余期限
     * 
     * @param oneDay 剩余期限
     */
    public void setOneDay(long oneDay) {
        this.oneDay = oneDay;
    }
    // add by zhangrui1 回购交易计算剩余期限 end
    // add by zhangrui1 差分预占 start
    /**
     * 是否差分预占
     */
    private String isDiffOccupy;

    /**
     * 差分标识
     */
    private String diffOccupyFlg;

    /**
     * 是否逆占
     */
    private String isReverseOccupy;

    /**
     * 获取 是否差分预占
     * 
     * @return 是否差分预占.
     */
    public String getIsDiffOccupy() {
        return isDiffOccupy;
    }

    /**
     * 设置 是否差分预占
     * 
     * @param isDiffOccupy 是否差分预占
     */
    public void setIsDiffOccupy(String isDiffOccupy) {
        this.isDiffOccupy = isDiffOccupy;
    }

    /**
     * 获取 差分标识
     * 
     * @return 差分标识.
     */
    public String getDiffOccupyFlg() {
        return diffOccupyFlg;
    }

    /**
     * 设置 差分标识
     * 
     * @param diffOccupyFlg 差分标识
     */
    public void setDiffOccupyFlg(String diffOccupyFlg) {
        this.diffOccupyFlg = diffOccupyFlg;
    }

    /**
     * 获取 是否逆占
     * 
     * @return 是否逆占.
     */
    public String getIsReverseOccupy() {
        return isReverseOccupy;
    }

    /**
     * 设置 是否逆占
     * 
     * @param isReverseOccupy 是否逆占
     */
    public void setIsReverseOccupy(String isReverseOccupy) {
        this.isReverseOccupy = isReverseOccupy;
    }
    // add by zhangrui1 差分预占 end
    // add by zhangrui1 关联方信息 start
    /**
     * 关联方
     */
    private String relatedParty;

    /**
     * 是否关联方
     */
    private String isRelatedParty;

    /**
     * 获取 关联方
     * 
     * @return 关联方.
     */
    public String getRelatedParty() {
        return relatedParty;
    }

    /**
     * 设置 关联方
     * 
     * @param relatedParty 关联方
     */
    public void setRelatedParty(String relatedParty) {
        this.relatedParty = relatedParty;
    }

    /**
     * 获取 是否关联方
     * 
     * @return 是否关联方.
     */
    public String getIsRelatedParty() {
        return isRelatedParty;
    }

    /**
     * 设置 是否关联方
     * 
     * @param isRelatedParty 是否关联方
     */
    public void setIsRelatedParty(String isRelatedParty) {
        this.isRelatedParty = isRelatedParty;
    }

    public String getIgnoreStatus() {
        return ignoreStatus;
    }

    public void setIgnoreStatus(String ignoreStatus) {
        this.ignoreStatus = ignoreStatus;
    }

    public String getIsIgnoreFoul() {
        return isIgnoreFoul;
    }

    public void setIsIgnoreFoul(String isIgnoreFoul) {
        this.isIgnoreFoul = isIgnoreFoul;
    }

    public BigDecimal getAmountEx() {
        return amountEx;
    }

    public void setAmountEx(BigDecimal amountEx) {
        this.amountEx = amountEx;
    }

    public BigDecimal getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(BigDecimal repoTerm) {
        this.repoTerm = repoTerm;
    }

    /**
     * 获取 instCode
     * @return instCode.
     */
    public String getInstCode() {
        return instCode;
    }

    /**
     * 设置 instCode
     * @param instCode instCode
     */
    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }
    /**
     * 设置期限变量，非标用此变量作为资产的存续时长
     */
    private BigDecimal deadline;

    public BigDecimal getDeadline() {
        return deadline;
    }

    public void setDeadline(BigDecimal deadline) {
        this.deadline = deadline;
    }

    public BigDecimal getTradeAmtReal() {
        return tradeAmtReal;
    }
    /**
     * 实付本息，区别于金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmtReal;

    public void setTradeAmtReal(BigDecimal tradeAmtReal) {
        this.tradeAmtReal = tradeAmtReal;
    }
    private Boolean notCalcAmount;

    public Boolean getNotCalcAmount() {
        return notCalcAmount;
    }

    public void setNotCalcAmount(Boolean notCalcAmount) {
        this.notCalcAmount = notCalcAmount;
    }


    public String getIssuerCode() {
        return issuerCode;
    }

    public void setIssuerCode(String issuerCode) {
        this.issuerCode = issuerCode;
    }

    /**
     * 期限金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal termAmount;

    public BigDecimal getTermAmount() {
        return termAmount;
    }

    public void setTermAmount(BigDecimal termAmount) {
        this.termAmount = termAmount;
    }

    /**
     * 交易对手是否为空
     */
    private String isNullCounter;

    public String getIsNullCounter() {
        return isNullCounter;
    }

    public void setIsNullCounter(String isNullCounter) {
        this.isNullCounter = isNullCounter;
    }

    /**
     * 价格模式
     */
    private String priceModel;

    public String getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    public String getIsShareHolder() {
        return isShareHolder;
    }

    public void setIsShareHolder(String isShareHolder) {
        this.isShareHolder = isShareHolder;
    }

    private long betweenDate;

    public long getBetweenDate() {
        return betweenDate;
    }

    public void setBetweenDate(long betweenDate) {
        this.betweenDate = betweenDate;
    }

    /**
     * 抵押品代码
     */
    private String pledgedCode;

    public String getPledgedCode() {
        return pledgedCode;
    }

    public void setPledgedCode(String pledgedCode) {
        this.pledgedCode = pledgedCode;
    }

    /**
     * 是否锁定(股票交易)
     */
    private String withLockPeriod;

    public String getWithLockPeriod() {
        return withLockPeriod;
    }

    public void setWithLockPeriod(String withLockPeriod) {
        this.withLockPeriod = withLockPeriod;
    }
}
