package com.joyintech.tams.invest.trade.manualsettle.dto;

import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.TrdManualSettleInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.Date;
import java.util.List;

/**
 * @author zhangnian
 * @ClassName ManualSettleDto
 * @description TODO
 * @date 2020/10/21 0021 16:45
 **/
public class ManualSettleDto extends TrdManualSettleInfo implements ITableRow, ProcessEntity {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 操作类型
     */
    private String operateType;

    private String pageModel;
    /**
     * 要素id
     */
    private String elementId;

    /**
     * 市场代码
     */
    private String finprodMarketId;

    /**
     * 金融产品全称
     */
    private String finprodName;

    /**
     * 金融产品类型
     */
    private String finprodType2;

    /**
     * 组合名称
     */
    private String portfolioName;

    private String counterName;

    /**
     * 质押券市场代码（质押式回购）
     */
    private String pledgeIdAbbr;

    /**
     * 原质押券市场代码（质押式回购）
     */
    private String bPledgeIdAbbr;

    private String rownum;

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
    }

    /**
     * 选择数据
     */
    private List<ManualSettleDto> selectedItem;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getPageModel() {
        return pageModel;
    }

    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public List<ManualSettleDto> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(List<ManualSettleDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_1)
    public String getSettleSeqNo(){
        return super.getSettleSeqNo();
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradeMarket());
    }

    public String getTradePlatStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易平台, this.getTradePlat());
    }

    public String getSettleStatusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交收状态, this.getSettleStatus());
    }

    public String getBusiTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交收业务类别, this.getBusiType());
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    /**
     * 交收时间
     */
    private Date settleTime;

    /**
     * 操作类型 数据字典
     */
    private String operType;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 交收流水序号
     */
    private String seqNo;

    public Date getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    public String getSettleTimeStr(){
        if(null!=getSettleTime()){
            return DateUtil.format(getSettleTime(),"HH:mm:ss");
        }
        return StringUtil.EMPTY;
    }

    public String getSettleDateStr(){
        if(null!=getSettleTime()){
            return DateUtil.format(getSettleTime(),"yyyy-MM-dd");
        }
        return StringUtil.EMPTY;
    }

    public String getOperTypeStr() {
        if(StringUtil.isNotEmpty(this.getOperType())){
            return SysDataUtil.getNameFromDataDict(DictCode.交收操作类型, this.getOperType());
        }
        return StringUtil.EMPTY;
    }


    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getPledgeIdAbbr() {
        return pledgeIdAbbr;
    }

    public void setPledgeIdAbbr(String pledgeIdAbbr) {
        this.pledgeIdAbbr = pledgeIdAbbr;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getbPledgeIdAbbr() {
        return bPledgeIdAbbr;
    }

    public void setbPledgeIdAbbr(String bPledgeIdAbbr) {
        this.bPledgeIdAbbr = bPledgeIdAbbr;
    }
}

