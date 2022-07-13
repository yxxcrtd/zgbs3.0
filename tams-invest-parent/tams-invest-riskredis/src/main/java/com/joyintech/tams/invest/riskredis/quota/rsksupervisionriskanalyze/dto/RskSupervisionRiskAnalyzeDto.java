package com.joyintech.tams.invest.riskredis.quota.rsksupervisionriskanalyze.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.RskIsupPointVal;

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeRds;
import com.joyintech.tams.invest.common.risktrial.dto.ExprTreeMath;

/**
 * @program: tams-invest-parent
 * @description: RskSupervisionRiskAnalyzeDto
 * @author: 裴家俊
 * @create: 2019-10-01
 **/
public class RskSupervisionRiskAnalyzeDto extends RskIsupPointVal {
    /**
	 * <意义，目的和功能，以及可能被用到的地方>
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 组合/组合包名称
     */
    private String groupName;
    /**
     * 监督指标名称
     */
    private String indName;

    /**
     * 监督类型
     */
    private String supType;

    /**
     * 备注
     */
    private String memo;
    
    /**
     * 指标类型
     */
    private String indType;
    
    private String assetCode;
    
    private String assetName;
    
    /**
     * 组合名称
     */
    private String portfolioName;
    
    /**
     * 持仓规模
     */
    private String shareAmt;
    
    /**
     * 投资经理
     */
    private String invManagerId;
    /**
     * 突破次数
     */
    private int frequency;

    /**
     * 明细条数
     */
    private int mxCount;
    
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	public String getShareAmt() {
		return shareAmt;
	}

	public void setShareAmt(String shareAmt) {
		this.shareAmt = shareAmt;
	}

	

	public String getInvManagerId() {
		return invManagerId;
	}

	public void setInvManagerId(String invManagerId) {
		this.invManagerId = invManagerId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 获取 groupName
     *
     * @return groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置 groupName
     *
     * @param groupName groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 获取 indName
     *
     * @return indName
     */
    public String getIndName() {
        return indName;
    }

    /**
     * 设置 indName
     *
     * @param indName indName
     */
    public void setIndName(String indName) {
        this.indName = indName;
    }

    /**
     * 获取 supType
     *
     * @return supType
     */
    public String getSupType() {
        return supType;
    }

    /**
     * 设置 supType
     *
     * @param supType supType
     */
    public void setSupType(String supType) {
        this.supType = supType;
    }

    /**
     * 获取 memo
     *
     * @return memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置 memo
     *
     * @param memo memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public String getIndType() {
		return indType;
	}

	public void setIndType(String indType) {
		this.indType = indType;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	/**
     * 将监督类型对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getSupTypeAbbr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.监督类型, this.getSupType());
    }
    /**
     * 将监督状态对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getSupStatAbbr(){
        return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.监督状态, this.getSupStat());
    }
    /**
     * 将联合监控状态对应的数据字典代码转换为数据字典值
     * @return 数据字典值
     */
    public String getSupUniteStatAbbr(){
        if(StringUtil.isNotEmpty(this.getSupUniteStat())) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCodeRds.监督状态, this.getSupUniteStat());
        }
        return null;
    }
    /**
     * 监督日期
     */
    private Date supDate;
    /**
     * 获取 supDate
     *
     * @return supDate
     */
    public Date getSupDate() {
        return supDate;
    }

    /**
     * 设置 supDate
     *
     * @param supDate supDate
     */
    public void setSupDate(Date supDate) {
        this.supDate = supDate;
    }

    @Override
    public String getSupVal() {
       if(StringUtil.isNotEmpty(super.getSupVal())&&(super.getSupVal().indexOf("E")>-1||super.getSupVal().indexOf("e")>-1)){
           // modify by etliu 20220713 start
           // 监督值不是数值的情况下，直接返回字符串
           try {
               BigDecimal bd=new BigDecimal(super.getSupVal());
               return  bd.toPlainString();
           } catch (NumberFormatException e) {
               return super.getSupVal();
           }
           // modify by etliu 20220713 end

       }else {
           return super.getSupVal();
       }
    }

    public int getMxCount() {
        return mxCount;
    }

    public void setMxCount(int mxCount) {
        this.mxCount = mxCount;
    }

    private String isShow;

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
