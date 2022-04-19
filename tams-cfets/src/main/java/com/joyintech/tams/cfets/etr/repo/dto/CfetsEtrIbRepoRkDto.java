package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.EtrSecStock;
import com.joyintech.tams.common.base.entity.EtrSecStockConfig;
import com.joyintech.tams.common.base.entity.PtlSecStock;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.entrust.etribrepo.validator.EtrIbRepoRk002Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 */
@PageId("EtrIbRepoRk002")
@ValidLogic(targetClass = EtrIbRepoRk002Validator.class , groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class CfetsEtrIbRepoRkDto extends EtrSecStock implements ProcessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6153733662100542772L;
	
	/**
     * 按钮id
     */
    private String elementId;
    
    /**
     * 画面
     */
    private String pageModel;
    
    /**
     * 操作类型
     */
    private String operateType;
    
    /**
     * 债券自动入库设置
     */
    List<EtrSecStockConfig> etrSecStockConfig;
    
    /**
     * 组合质押库
     */
    List<PtlSecStock> ptlSecStock;

    private List<CfetsEtrSecStockConfig> cfetsEtrSecStockConfig;

    @ApiModelProperty(name="defaultStock",value="默认质押券库" ,example = "999999")
    private String defaultStock;

    public List<EtrSecStockConfig> getEtrSecStockConfig() {
		return etrSecStockConfig;
	}

	public void setEtrSecStockConfig(List<EtrSecStockConfig> etrSecStockConfig) {
		this.etrSecStockConfig = etrSecStockConfig;
	}

	public List<PtlSecStock> getPtlSecStock() {
		return ptlSecStock;
	}

	public void setPtlSecStock(List<PtlSecStock> ptlSecStock) {
		this.ptlSecStock = ptlSecStock;
	}

	/**
     * 设置编号
     */
    private String configId;

    /**
     * 质押库编号
     */
    private String pledgeStockId;

    /**
     * 资产一级分类
     */
    private String windtypeone;

    /**
     * 资产二级分类
     */
    private String windtypetwo;

    /**
     * 主体评级
     */
    private String mGradeResult;

    /**
     * 债项评级
     */
    private String secGradeResult;

    /**
     * 是否次级债
     */
    private String isSecBond;

    /**
     * 是否含权债
     */
    private String isRight;

    /**
     * 是否私募债
     */
    private String isPpb;
    
    /**
     * 组合代码
     */
    private String portfolioId;
    
    /**
     * 不触发预占切面
     */
    private String otherFlg;
    
    public String getOtherFlg() {
        return DictCodeListExt1.是或否_是.getValue();
    }

    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
    }

    /**
     * 取得 组合代码
     * @return 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 组合代码
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 设置编号
     * @return 设置编号
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * 设置 设置编号
     * @param configId 设置编号
     */
    public void setConfigId(String configId) {
        this.configId = configId == null ? null : configId.trim();
    }

    /**
     * 取得 质押库编号
     * @return 质押库编号
     */
    public String getPledgeStockId() {
        return pledgeStockId;
    }

    /**
     * 设置 质押库编号
     * @param pledgeStockId 质押库编号
     */
    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId == null ? null : pledgeStockId.trim();
    }

    /**
     * 取得 资产一级分类
     * @return 资产一级分类
     */
    public String getWindtypeone() {
        return windtypeone;
    }

    /**
     * 设置 资产一级分类
     * @param windtypeone 资产一级分类
     */
    public void setWindtypeone(String windtypeone) {
        this.windtypeone = windtypeone == null ? null : windtypeone.trim();
    }

    /**
     * 取得 资产二级分类
     * @return 资产二级分类
     */
    public String getWindtypetwo() {
        return windtypetwo;
    }

    /**
     * 设置 资产二级分类
     * @param windtypetwo 资产二级分类
     */
    public void setWindtypetwo(String windtypetwo) {
        this.windtypetwo = windtypetwo == null ? null : windtypetwo.trim();
    }

    /**
     * 取得 主体评级
     * @return 主体评级
     */
    public String getmGradeResult() {
        return mGradeResult;
    }

    /**
     * 设置 主体评级
     * @param mGradeResult 主体评级
     */
    public void setmGradeResult(String mGradeResult) {
        this.mGradeResult = mGradeResult == null ? null : mGradeResult.trim();
    }

    /**
     * 取得 债项评级
     * @return 债项评级
     */
    public String getSecGradeResult() {
        return secGradeResult;
    }

    /**
     * 设置 债项评级
     * @param secGradeResult 债项评级
     */
    public void setSecGradeResult(String secGradeResult) {
        this.secGradeResult = secGradeResult == null ? null : secGradeResult.trim();
    }

    /**
     * 取得 是否次级债
     * @return 是否次级债
     */
    public String getIsSecBond() {
        return isSecBond;
    }

    /**
     * 设置 是否次级债
     * @param isSecBond 是否次级债
     */
    public void setIsSecBond(String isSecBond) {
        this.isSecBond = isSecBond == null ? null : isSecBond.trim();
    }

    /**
     * 取得 是否含权债
     * @return 是否含权债
     */
    public String getIsRight() {
        return isRight;
    }

    /**
     * 设置 是否含权债
     * @param isRight 是否含权债
     */
    public void setIsRight(String isRight) {
        this.isRight = isRight == null ? null : isRight.trim();
    }

    /**
     * 取得 是否私募债
     * @return 是否私募债
     */
    public String getIsPpb() {
        return isPpb;
    }

    /**
     * 设置 是否私募债
     * @param isPpb 是否私募债
     */
    public void setIsPpb(String isPpb) {
        this.isPpb = isPpb == null ? null : isPpb.trim();
    }
    
    /**
     * 取得 委托代码
     * @return 委托代码
     */
    @Override
    @PrimaryKey(value = Field.PK_1)
    public String getPledgeStockName() {
        return super.getPledgeStockName();
    }
    
    

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getPageModel() {
		return pageModel;
	}

	public void setPageModel(String pageModel) {
		this.pageModel = pageModel;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

    public List<CfetsEtrSecStockConfig> getCfetsEtrSecStockConfig() {
        return cfetsEtrSecStockConfig;
    }

    public void setCfetsEtrSecStockConfig(List<CfetsEtrSecStockConfig> cfetsEtrSecStockConfig) {
        this.cfetsEtrSecStockConfig = cfetsEtrSecStockConfig;
    }

    public String getDefaultStock() {
        return defaultStock;
    }

    public void setDefaultStock(String defaultStock) {
        this.defaultStock = defaultStock;
    }
}
