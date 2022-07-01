/*
 * 文件名：FinProductDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.asset.depo.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.validation.annotation.*;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.PaFinProduct;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.PaFinProductBuyoutRate;
import com.joyintech.tams.component.business.asset.dto.BussFinRecommentDto;
import com.joyintech.tams.component.business.asset.dto.FinInterestFactorDto;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.project.dto.BussParticipantInfoDto;
import com.joyintech.tams.component.business.project.dto.BussPostInvestInfoDto;
import com.joyintech.tams.component.business.report.asset.depo.dto.BussRepDepositInfoDto;
import com.joyintech.tams.component.business.report.asset.depo.dto.BussRepDlRepoInfoDto;
import com.joyintech.tams.invest.asset.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.invest.asset.common.dto.BaseRepAssetDeptInfoDto;
import com.joyintech.tams.invest.asset.common.dto.BaseRepDespositAssetInfoDto;
import com.joyintech.tams.invest.asset.common.dto.BaseRepDlRepoInfoDto;
import com.joyintech.tams.invest.asset.depo.validator.FinDepositTargetValidator;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

/**
 * 标的类金融产品DTO
 *
 * @author zhangnian
 * @version 1.0 2018年9月6日
 * @see FinProductDto
 * @since 1.0
 */
@PageId("FinDepositTarget002")
@ValidLogic(targetClass = FinDepositTargetValidator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class FinDepositTargetDto extends FinProduct implements ITableRow, ProcessEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;

    private String elementId;

    private List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList;

    /**
     * 存款类资产报送信息
     */
    private BaseRepDespositAssetInfoDto baseRepDespositAssetInfoDto;

    /**
     * 拆放同业及买入返售报送信息
     */
    private BaseRepDlRepoInfoDto baseRepDlRepoInfoDto;

    /**
     * 负债信息
     */
    private BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto;

    private MstFileInputParameter mstFileInputParameter;

    /**
     * 资产一级分类
     */
    private String type6;

    /**
     * 资产二级分类
     */
    private String type7;
    // 增加type9、type10、type11、type12 赵东东 2109.7.23 start
    // 增加注释 赵东东 2019.7.26 start
    /**
     * 行内资产二级分类
     */
    // 增加注释 赵东东 2019.7.26 end
    private String type9;
    // 增加注释 赵东东 2019.7.26 start
    /**
     * 行内资产三级分类
     */
    // 增加注释 赵东东 2019.7.26 end
    private String type10;
    // 增加注释 赵东东 2019.7.26 start
    /**
     * 行内资产四级分类
     */
    // 增加注释 赵东东 2019.7.26 end
    private String type11;
    // 增加注释 赵东东 2019.7.26 start
    /**
     * 行内资产五级分类
     */
    // 增加注释 赵东东 2019.7.26 end
    private String type12;
    // 增加type9、type10、type11、type12 赵东东 2109.7.23 end

    /**
     * 日历名称
     */
    private String calendarName;

    /**
     * 交易对手类型---分行、通道
     */
    private String counterType;

    /**
     * 是否交易日历
     */
    private String isTradeCalendar;
    /**
     * 资产提供行
     */
    private String assetProvider;

    /**
     * 资产提供行名称
     */
    private String assetProviderName;

    /**
     * 存款机构名
     */
    private String issuerName;

    /**
     * 资产推荐费率
     */
    private BigDecimal referRate;

    /**
     * 利率
     */
    private BigDecimal rate;

    /**
     * 金融产品分类表Entity
     */
    private FinProductType finProductType;

    /**
     * 授信额度代码
     */
    private String seqno;

    /**
     * 授信配置代码
     */
    private String creditMstId;

    /**
     * 授信客户编号（机构授信）/金融产品代码（资产授信）/资产系列代码（资产系列授信）
     */
    private String customerId;

    /**
     * 批复流水号
     */
    private String pflsh;
    /**
     * 已占用额度，初始值为0
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal hapCreditAmt;

    /**
     * 实际授信主体
     */
    private String actualCreditProvid;
    /**
     * 是否占用主体授信
     */
    private String creditFlag;

    /**
     * 参与方信息
     */
    @Valid
    private BussParticipantInfoDto bussParticipantInfoDto;

    // 增加推荐方与推荐费率 add by renping 20191014 start
    /**
     * 推荐方
     */
    private String recommender;

    private String sponsorName;

    /**
     * 资产来源
     */
    private String assetSource;

    /**
     * 买断利率
     */
    private BigDecimal buyOutRate;

    /**
     * 是否可提前支取，风险限额用，不影响存续期
     */
    private String isInAdvance;

    public String getIsInAdvance() {
        return isInAdvance;
    }

    public void setIsInAdvance(String isInAdvance) {
        this.isInAdvance = isInAdvance;
    }

    public String getAssetSource() {
        return assetSource;
    }

    public void setAssetSource(String assetSource) {
        this.assetSource = assetSource;
    }

    /**
     * 取得 推荐方
     *
     * @return 推荐方
     */
    public String getRecommender() {
        return recommender;
    }

    /**
     * 设置 推荐方
     *
     * @param recommender 推荐方
     */
    public void setRecommender(String recommender) {
        this.recommender = recommender == null ? null : recommender.trim();
    }

    /**
     * 资产推荐方名称
     */
    private String recommenderName;

    /**
     * 取得 资产推荐方名称
     *
     * @return 资产推荐方名称
     */
    public String getRecommenderName() {
        return recommenderName;
    }

    /**
     * 设置 资产推荐方名称
     *
     * @param recommender 资产推荐方名称
     */
    public void setRecommenderName(String recommenderName) {
        this.recommenderName = recommenderName == null ? null : recommenderName.trim();
    }

    /**
     * 资产推荐方及费率信息
     */
    @Valid
    private BussFinRecommentDto bussFinRecommentDto;

    /**
     * 获取 bussFinRecommentDto
     *
     * @return bussFinRecommentDto.
     */
    public BussFinRecommentDto getBussFinRecommentDto() {
        return bussFinRecommentDto;
    }

    /**
     * 设置 bussFinRecommentDto
     *
     * @param bussFinRecommentDto bussFinRecommentDto
     */
    public void setBussFinRecommentDto(BussFinRecommentDto bussFinRecommentDto) {
        this.bussFinRecommentDto = bussFinRecommentDto;
    }
    // 增加推荐方与推荐费率 add by renping 20191014 end
    /**
     * 法审编号
     */
    private String trialNo;

    /**
     * 获取 creditFlag
     *
     * @return creditFlag.
     */
    public String getCreditFlag() {
        return creditFlag;
    }


    /**
     * 设置 creditFlag
     *
     * @param creditFlag creditFlag
     */
    public void setCreditFlag(String creditFlag) {
        this.creditFlag = creditFlag;
    }

    /**
     * 计息要素DTO
     */
    @Valid
    private FinInterestFactorDto finInterestFactorDto;

    /**
     * 附件信息DTO
     */
    private List<UploadFile> fileArray;

    /**
     * 付息信息DTO
     */
    @Valid
    private FinPayInterestStdInfoDto finPayInterestStdInfoDto;

    /**
     * 中债报送DTO
     */
    @Valid
    private BussRepDepositInfoDto bussRepDepositInfoDto;

    /**
     * 拆放同业及买入返售报送信息Dto
     */
    @Valid
    private BussRepDlRepoInfoDto bussRepDlRepoInfoDto;

    /**
     * 金融产品扩展entity
     */
    private PaFinProduct paFinProduct;
    /**
     * 选中的记录
     */
    private transient List<FinDepositTargetDto> selectedItem;

    /**
     * 投后管理机构
     */
    private String postOrg;

    /**
     * 监管评级
     */
    private String regulatoryRating;

    /**
     * 投后信息
     */
    private BussPostInvestInfoDto bussPostInvestInfoDto;


    /**
     * 金融产品买断利率(平安个性)
     */
    private PaFinProductBuyoutRate paFinProductBuyoutRate;

    /**
     * 是否有存单
     */
    private String isReceipt;

    /**
     * 源起息日
     */
    private Date oldVdate;

    /**
     * 计息利率方式
     * add by jiangPeng on 20200721
     */
    private String calRateType;


    /**
     * 获取 paFinProduct
     *
     * @return paFinProduct.
     */
    public PaFinProduct getPaFinProduct() {
        return paFinProduct;
    }

    /**
     * 设置 paFinProduct
     *
     * @param paFinProduct paFinProduct
     */
    public void setPaFinProduct(PaFinProduct paFinProduct) {
        this.paFinProduct = paFinProduct;
    }

    /**
     * 获取 type6
     *
     * @return type6.
     */
    public String getType6() {
        return type6;
    }

    /**
     * 设置 type6
     *
     * @param type6 type6
     */
    public void setType6(String type6) {
        this.type6 = type6;
    }

    /**
     * 获取 type7
     *
     * @return type7.
     */
    /*
     * @NotEmpty(fieldName = "FinDepositTarget002.type7" ,groups =
     * {ValidateGroupEdit.class})
     */
    public String getType7() {
        return type7;
    }

    /**
     * 设置 type7
     *
     * @param type7 type7
     */
    public void setType7(String type7) {
        this.type7 = type7;
    }

    /**
     * 获取 operateType
     *
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }
    // 增加获取、设置type9，获取、设置type10，获取、设置type11，获取、设置type12 赵东东 2109.7.23 start
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 获取 type9
     *
     * @return type9.
     */
    // 增加注释 赵东东 2019.7.26 end
    @NotEmpty(fieldName = "FinDepositTarget002.type9", groups = {ValidateGroupEdit.class})
    public String getType9() {
        return type9;
    }
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 设置 type9
     *
     * @param type9 type9
     */
    // 增加注释 赵东东 2019.7.26 end
    public void setType9(String type9) {
        this.type9 = type9;
    }
    // // 增加注释 赵东东 2019.7.26 start

    /**
     * 获取 type10
     *
     * @return type10.
     */
    // 增加注释 赵东东 2019.7.26 end
    @NotEmpty(fieldName = "FinDepositTarget002.type10", groups = {ValidateGroupEdit.class})
    public String getType10() {
        return type10;
    }
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 设置 type10
     *
     * @param type10 type10
     */
    // 增加注释 赵东东 2019.7.26 end
    public void setType10(String type10) {
        this.type10 = type10;
    }
    // // 增加注释 赵东东 2019.7.26 start

    /**
     * 获取 type11
     *
     * @return type11.
     */
    // 增加注释 赵东东 2019.7.26 end
//	@NotEmpty(fieldName = "FinDepositTarget002.type11", groups = { ValidateGroupEdit.class })
    public String getType11() {
        return type11;
    }
    // // 增加注释 赵东东 2019.7.26 start

    /**
     * 设置 type11
     *
     * @param type11 type11
     */
    // 增加注释 赵东东 2019.7.26 end
    public void setType11(String type11) {
        this.type11 = type11;
    }
    // // 增加注释 赵东东 2019.7.26 start

    /**
     * 获取 type12
     *
     * @return type12.
     */
    // 增加注释 赵东东 2019.7.26 end
//	@NotEmpty(fieldName = "FinDepositTarget002.type12", groups = { ValidateGroupEdit.class })
    public String getType12() {
        return type12;
    }
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 设置 type12
     *
     * @param type12 type12
     */
    // 增加注释 赵东东 2019.7.26 end
    public void setType12(String type12) {
        this.type12 = type12;
    }
    // 增加获取、设置type9，获取、设置type10，获取、设置type11，获取、设置type12 赵东东 2109.7.23 end

    /**
     * 设置 operateType
     *
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 获取 pageModel
     *
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }

    /**
     * 设置 pageModel
     *
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    /**
     * 获取 elementId
     *
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * 设置 elementId
     *
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * 金融产品代码
     */
    @Override
    @PrimaryKey(Field.PK_1)
//    @Code(fieldName = "FinProjectTargetAsset002.finprodId", length = 32, groups = {ValidateGroupEdit.class})
//    @NotEmpty(fieldName = "FinProjectTargetAsset002.finprodId",groups={ValidateGroupEdit.class})//非空校验
    public String getFinprodMarketId() {
        return super.getFinprodMarketId();
    }
    /**
     * 市场代码 {@inheritDoc}
     *//*
     * @Override
     *
     * @NotEmpty(fieldName="FinDepositTarget002.finprodId",groups =
     * {ValidateGroupEdit.class}) public String getFinprodMarketId(){ return
     * super.getFinprodMarketId(); }
     */

    /**
     * 资产名称 {@inheritDoc}
     */
    @Override
    @Length(fieldName = "FinDepositTarget002.finprodName", max = 100, groups = {ValidateGroupEdit.class})
    @NotEmpty(fieldName = "FinDepositTarget002.finprodName", groups = {ValidateGroupEdit.class})
    public String getFinprodName() {
        return super.getFinprodName();
    }

    @Length(fieldName = "FinDepositTarget002.remark", max = 300, groups = {ValidateGroupEdit.class})
    public String getRemark() {
        return super.getRemark();
    }

    /**
     * 存款机构 {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinDepositTarget002.issuer", groups = {ValidateGroupEdit.class})
    public String getIssuer() {
        return super.getIssuer();
    }

    /**
     * 存款本金 {@inheritDoc}
     */
    @Override
    @Amount(fieldName = "FinDepositTarget002.issueAmt", maxIntBit = 30, groups = {ValidateGroupEdit.class})
    public BigDecimal getIssueAmt() {
        return super.getIssueAmt();
    }

    /**
     * 起息日 {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinDepositTarget002.vdate", groups = {ValidateGroupEdit.class})
    public Date getVdate() {
        return super.getVdate();
    }

    /**
     * 到期日 {@inheritDoc}
     */
    @Override
    public Date getMdate() {
        return super.getMdate();
    }

    /**
     * 是否境内外 {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "FinDepositTarget002.blnArea", groups = {ValidateGroupEdit.class})
    public String getBlnArea() {
        return super.getBlnArea();
    }

    /**
     * 币种
     */
    @Override
    @NotEmpty(fieldName = "FinDepositTarget002.ccy", groups = {ValidateGroupEdit.class})
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * 交易日历
     */
    @Override
    @NotEmpty(fieldName = "FinDepositTarget002.calendarId", groups = {ValidateGroupEdit.class})
    public String getCalendarId() {
        return super.getCalendarId();
    }

    /**
     * 获取 finInterestFactorDto
     *
     * @return finInterestFactorDto.
     */
    public FinInterestFactorDto getFinInterestFactorDto() {
        return finInterestFactorDto;
    }

    /**
     * 设置 finInterestFactorDto
     *
     * @param finInterestFactorDto finInterestFactorDto
     */
    public void setFinInterestFactorDto(FinInterestFactorDto finInterestFactorDto) {
        this.finInterestFactorDto = finInterestFactorDto;
    }

    /**
     * 获取 baseFinProductBaseInfoList
     *
     * @return baseFinProductBaseInfoList.
     */
    public List<BaseFinProductBaseInfoDto> getBaseFinProductBaseInfoList() {
        return baseFinProductBaseInfoList;
    }

    /**
     * 设置 baseFinProductBaseInfoList
     *
     * @param baseFinProductBaseInfoList baseFinProductBaseInfoList
     */
    public void setBaseFinProductBaseInfoList(List<BaseFinProductBaseInfoDto> baseFinProductBaseInfoList) {
        this.baseFinProductBaseInfoList = baseFinProductBaseInfoList;
    }

    /**
     * 获取币种name
     *
     * @return
     * @see
     * @since 1.0
     */
    public String getCcyName() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    /**
     * 获取资产类型name
     *
     * @return
     * @see
     * @since 1.0
     */
    public String getType6Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.存款类资产类型, this.getType6());
    }
    //增加getType9Name、getType10Name、getType11Name、getType12Name 赵东东 2019.7.23 start
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 行内资产二级分类name
     *
     * @return
     * @see
     * @since 1.0
     */
    // 增加注释 赵东东 2019.7.26 end
    public String getType9Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, this.getType9());
    }
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 行内资产三级分类name
     *
     * @return
     * @see
     * @since 1.0
     */
    // 增加注释 赵东东 2019.7.26 end
    public String getType10Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, this.getType10());
    }
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 行内资产四级分类name
     *
     * @return
     * @see
     * @since 1.0
     */
    // 增加注释 赵东东 2019.7.26 end
    public String getType11Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.getType11());
    }
    // 增加注释 赵东东 2019.7.26 start

    /**
     * 行内资产五级分类name
     *
     * @return
     * @see
     * @since 1.0
     */
    // 增加注释 赵东东 2019.7.26 end
    public String getType12Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产五级分类, this.getType12());
    }
    //增加getType9Name、getType10Name、getType11Name、getType12Name 赵东东 2019.7.23 end

    /**
     * 获取 selectedItem
     *
     * @return selectedItem.
     */
    public List<FinDepositTargetDto> getSelectedItem() {
        return selectedItem;
    }

    /**
     * 设置 selectedItem
     *
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<FinDepositTargetDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * 获取 fileArray
     *
     * @return fileArray.
     */
    public List<UploadFile> getFileArray() {
        return fileArray;
    }

    /**
     * 设置 fileArray
     *
     * @param fileArray fileArray
     */
    public void setFileArray(List<UploadFile> fileArray) {
        this.fileArray = fileArray;
    }


    public FinPayInterestStdInfoDto getFinPayInterestStdInfoDto() {
        return finPayInterestStdInfoDto;
    }

    public void setFinPayInterestStdInfoDto(FinPayInterestStdInfoDto finPayInterestStdInfoDto) {
        this.finPayInterestStdInfoDto = finPayInterestStdInfoDto;
    }

    /**
     * 获取 calendarName
     *
     * @return calendarName.
     */
    public String getCalendarName() {
        return calendarName;
    }

    /**
     * 设置 calendarName
     *
     * @param calendarName calendarName
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    /**
     * 获取 assetProvider
     *
     * @return assetProvider.
     */
//	@NotEmpty(fieldName = "FinDepositTarget002.assetProvider", groups = { ValidateGroupEdit.class })
    public String getAssetProvider() {
        return assetProvider;
    }

    /**
     * 设置 assetProvider
     *
     * @param assetProvider assetProvider
     */
    public void setAssetProvider(String assetProvider) {
        this.assetProvider = assetProvider;
    }

    /**
     * 获取 assetProviderName
     *
     * @return assetProviderName.
     */
    public String getAssetProviderName() {
        return assetProviderName;
    }

    /**
     * 设置 assetProviderName
     *
     * @param assetProviderName assetProviderName
     */
    public void setAssetProviderName(String assetProviderName) {
        this.assetProviderName = assetProviderName;
    }

    /**
     * 获取 issuerName
     *
     * @return issuerName.
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * 设置 issuerName
     *
     * @param issuerName issuerName
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * 获取 counterType
     *
     * @return counterType.
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 counterType
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    /**
     * 获取 isTradeCalendar
     *
     * @return isTradeCalendar.
     */
    public String getIsTradeCalendar() {
        return isTradeCalendar;
    }

    /**
     * 设置 isTradeCalendar
     *
     * @param isTradeCalendar isTradeCalendar
     */
    public void setIsTradeCalendar(String isTradeCalendar) {
        this.isTradeCalendar = isTradeCalendar;
    }

    /**
     * 获取 finProductType
     *
     * @return finProductType.
     */
    public FinProductType getFinProductType() {
        return finProductType;
    }

    /**
     * 设置 finProductType
     *
     * @param finProductType finProductType
     */
    public void setFinProductType(FinProductType finProductType) {
        this.finProductType = finProductType;
    }

    /**
     * 获取 mstFileInputParameter
     *
     * @return mstFileInputParameter.
     */
    public MstFileInputParameter getMstFileInputParameter() {
        return mstFileInputParameter;
    }

    /**
     * 设置 mstFileInputParameter
     *
     * @param mstFileInputParameter mstFileInputParameter
     */
    public void setMstFileInputParameter(MstFileInputParameter mstFileInputParameter) {
        this.mstFileInputParameter = mstFileInputParameter;
    }

    /**
     * 获取 baseRepDespositAssetInfoDto
     *
     * @return baseRepDespositAssetInfoDto.
     */
    public BaseRepDespositAssetInfoDto getBaseRepDespositAssetInfoDto() {
        return baseRepDespositAssetInfoDto;
    }

    /**
     * 设置 baseRepDespositAssetInfoDto
     *
     * @param baseRepDespositAssetInfoDto baseRepDespositAssetInfoDto
     */
    public void setBaseRepDespositAssetInfoDto(BaseRepDespositAssetInfoDto baseRepDespositAssetInfoDto) {
        this.baseRepDespositAssetInfoDto = baseRepDespositAssetInfoDto;
    }

    /**
     * 获取 bussRepDepositInfoDto
     *
     * @return bussRepDepositInfoDto.
     */
    public BussRepDepositInfoDto getBussRepDepositInfoDto() {
        return bussRepDepositInfoDto;
    }

    /**
     * 设置 bussRepDepositInfoDto
     *
     * @param bussRepDepositInfoDto bussRepDepositInfoDto
     */
    public void setBussRepDepositInfoDto(BussRepDepositInfoDto bussRepDepositInfoDto) {
        this.bussRepDepositInfoDto = bussRepDepositInfoDto;
    }

    /**
     * 获取 baseRepAssetDeptInfoDto
     *
     * @return baseRepAssetDeptInfoDto.
     */
    public BaseRepAssetDeptInfoDto getBaseRepAssetDeptInfoDto() {
        return baseRepAssetDeptInfoDto;
    }

    /**
     * 设置 baseRepAssetDeptInfoDto
     *
     * @param baseRepAssetDeptInfoDto baseRepAssetDeptInfoDto
     */
    public void setBaseRepAssetDeptInfoDto(BaseRepAssetDeptInfoDto baseRepAssetDeptInfoDto) {
        this.baseRepAssetDeptInfoDto = baseRepAssetDeptInfoDto;
    }

    /**
     * 获取 referRate
     *
     * @return referRate.
     */
    //资产推荐费字段保留，设置为非必填 lishuping 20190918 start
    /*@NotEmpty(fieldName = "FinDepositTarget002.referRate", groups = { ValidateGroupEdit.class })*/
    //资产推荐费字段保留，设置为非必填 lishuping 20190918 end
    public BigDecimal getReferRate() {
        return referRate;
    }

    /**
     * 设置 referRate
     *
     * @param referRate referRate
     */
    public void setReferRate(BigDecimal referRate) {
        this.referRate = referRate;
    }

    /**
     * 获取 baseRepDlRepoInfoDto
     *
     * @return baseRepDlRepoInfoDto.
     */
    public BaseRepDlRepoInfoDto getBaseRepDlRepoInfoDto() {
        return baseRepDlRepoInfoDto;
    }

    /**
     * 设置 baseRepDlRepoInfoDto
     *
     * @param baseRepDlRepoInfoDto baseRepDlRepoInfoDto
     */
    public void setBaseRepDlRepoInfoDto(BaseRepDlRepoInfoDto baseRepDlRepoInfoDto) {
        this.baseRepDlRepoInfoDto = baseRepDlRepoInfoDto;
    }

    /**
     * 获取 bussRepDlRepoInfoDto
     *
     * @return bussRepDlRepoInfoDto.
     */
    public BussRepDlRepoInfoDto getBussRepDlRepoInfoDto() {
        return bussRepDlRepoInfoDto;
    }

    /**
     * 设置 bussRepDlRepoInfoDto
     *
     * @param bussRepDlRepoInfoDto bussRepDlRepoInfoDto
     */
    public void setBussRepDlRepoInfoDto(BussRepDlRepoInfoDto bussRepDlRepoInfoDto) {
        this.bussRepDlRepoInfoDto = bussRepDlRepoInfoDto;
    }

    /**
     * 获取 rate
     *
     * @return rate.
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 设置 rate
     *
     * @param rate rate
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getVdateStr() {
        return DateUtil.format(this.getVdate());
    }

    public String getMdateStr() {
        return DateUtil.format(this.getMdate());
    }

    /**
     * 获取 seqno
     *
     * @return seqno.
     */
    public String getSeqno() {
        return seqno;
    }


    /**
     * 设置 seqno
     *
     * @param seqno seqno
     */
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }


    /**
     * 获取 creditMstId
     *
     * @return creditMstId.
     */
    public String getCreditMstId() {
        return creditMstId;
    }


    /**
     * 设置 creditMstId
     *
     * @param creditMstId creditMstId
     */
    public void setCreditMstId(String creditMstId) {
        this.creditMstId = creditMstId;
    }


    /**
     * 获取 customerId
     *
     * @return customerId.
     */
    public String getCustomerId() {
        return customerId;
    }


    /**
     * 设置 customerId
     *
     * @param customerId customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    /**
     * 获取 pflsh
     *
     * @return pflsh.
     */
    public String getPflsh() {
        return pflsh;
    }


    /**
     * 设置 pflsh
     *
     * @param pflsh pflsh
     */
    public void setPflsh(String pflsh) {
        this.pflsh = pflsh;
    }


    /**
     * 获取 hapCreditAmt
     *
     * @return hapCreditAmt.
     */
    public BigDecimal getHapCreditAmt() {
        return hapCreditAmt;
    }


    /**
     * 设置 hapCreditAmt
     *
     * @param hapCreditAmt hapCreditAmt
     */
    public void setHapCreditAmt(BigDecimal hapCreditAmt) {
        this.hapCreditAmt = hapCreditAmt;
    }


    /**
     * 获取 actualCreditProvid
     *
     * @return actualCreditProvid.
     */
    public String getActualCreditProvid() {
        return actualCreditProvid;
    }


    /**
     * 设置 actualCreditProvid
     *
     * @param actualCreditProvid actualCreditProvid
     */
    public void setActualCreditProvid(String actualCreditProvid) {
        this.actualCreditProvid = actualCreditProvid;
    }

    public BussParticipantInfoDto getBussParticipantInfoDto() {
        return bussParticipantInfoDto;
    }

    public void setBussParticipantInfoDto(BussParticipantInfoDto bussParticipantInfoDto) {
        this.bussParticipantInfoDto = bussParticipantInfoDto;
    }

    @Override
    @Length(fieldName = "FinDepositTarget002.contractNo", max = 400, groups = {ValidateGroupEdit.class})
    //TISHKBS-2052 存款类标的合同编号需求是选填 王亚宁 2021/4/27
    //	@NotEmpty(fieldName = "FinDepositTarget002.contractNo", groups = {ValidateGroupEdit.class})
    public String getContractNo() {
        return super.getContractNo();
    }

    @Length(fieldName = "FinDepositTarget002.trialNo", max = 100, groups = {ValidateGroupEdit.class})
    public String getTrialNo() {
        return trialNo;
    }

    public void setTrialNo(String trialNo) {
        this.trialNo = trialNo;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getPostOrg() {
        return postOrg;
    }

    public void setPostOrg(String postOrg) {
        this.postOrg = postOrg;
    }

    public BussPostInvestInfoDto getBussPostInvestInfoDto() {
        return bussPostInvestInfoDto;
    }

    public void setBussPostInvestInfoDto(BussPostInvestInfoDto bussPostInvestInfoDto) {
        this.bussPostInvestInfoDto = bussPostInvestInfoDto;
    }

    public String getRegulatoryRating() {
        return regulatoryRating;
    }

    public void setRegulatoryRating(String regulatoryRating) {
        this.regulatoryRating = regulatoryRating;
    }

    public BigDecimal getBuyOutRate() {
        return buyOutRate;
    }

    public void setBuyOutRate(BigDecimal buyOutRate) {
        this.buyOutRate = buyOutRate;
    }

    public PaFinProductBuyoutRate getPaFinProductBuyoutRate() {
        return paFinProductBuyoutRate;
    }

    public void setPaFinProductBuyoutRate(PaFinProductBuyoutRate paFinProductBuyoutRate) {
        this.paFinProductBuyoutRate = paFinProductBuyoutRate;
    }

    public Date getOldVdate() {
        return oldVdate;
    }

    public void setOldVdate(Date oldVdate) {
        this.oldVdate = oldVdate;
    }

    public String getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(String isReceipt) {
        this.isReceipt = isReceipt;
    }

    /**
     * 计息利率方式
     * add by jiangPeng on 20200721
     */
    public String getCalRateType() {
        return calRateType;
    }

    /**
     * 计息利率方式
     * add by jiangPeng on 20200721
     */
    public void setCalRateType(String calRateType) {
        this.calRateType = calRateType;
    }
}
