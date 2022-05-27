package com.joyintech.tams.invest.basicdata.mstmarketmanagement.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.basicdata.mstmarketinstitutions.validator.MstMarketInstitutionsValidator;
import com.joyintech.tams.invest.basicdata.mstmarketmanagement.validator.MstMarketManagementValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
/**
 * 
 * 用于评级查询信息
 * @author wangwei5
 * @version 1.0 2020年3月6日
 * @see MstMarketManagementDto
 * @since 1.0
 */
@PageId("MstMarketManagement002")
@ValidLogic(targetClass = MstMarketManagementValidator.class, groups = {ValidateGroupEdit.class,
        ValidateGroupDelete.class})
public class MstMarketManagementDto  extends BaseEntity implements ProcessEntity{
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 画面打开模式
     */
    private String pageModel;

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;

    /**
     * 市场代码
     */
    private String marketId;
    
    /**
     * 组合名称
     */
    private String groupName;
    
    /**
     * 机构代码
     */
    private String orgId;
    /**
     * 对象代码(充当债券代码)
     */
    private String oCode;
    /**
     * 对象名称
     */
    private String oCodeName;
    
    /**
     * 公告日期 
     */
    private String noticeDate;
    
    /**
     * 评级机构
     */
    private String gradeOrgId;
    
    /**
     * 评级机构名称
     */
    private String gradeOrgName;
    /**
     * 评级日期
     */
    private String gradeDate;
    
    /**
     * 前次评级日期
     */
    private String previousgradeDate;
    
    /**
     * 前次评级结果
     */
    private String previousgradeResultDis;
    
    /**
     * 前次评级机构
     */
    private String previousgradeOrgName;
    
    /**
     * 评级日期
     */
    private String gradeDateStr;

    /**
     * 评级展望
     */
    private String gradeExpectation;
    
    /**
     * 变动方向
     */
    private String directionChange;
    
    /**
     * 未知参数(只有01，02)
     */
    private String innerOuter;
    
    /**
     * 评级类型，主体评级、债项评级
     */
    private String oType;
    /**
     * 长短期
     */
    private String shortLongTerm;

    /**
     * 评级结果
     */
    private String gradeResult;

    /**
     * 录入方式
     */
    private String inputType;
    

    /**
     * 评级发起类型
     */
    private String issuerRateType;
    
    /**
     * 原始评级结果
     */
    private String mstRatingResult01;
    
    /**
     * 标准评级结果
     */
    private String mstRatingResult02;
    
    /**
     * 评级展示类型
     */
    private String mstDisType;
    
    
    /**
     * sql查询标识
     */
    private String sqlSearchString;
    
    /**
     * 评级日期开始
     */
    private String gradeDateStart;
    
    /**
     * 评级日期开始
     */
    private String gradeDateEnd;
    
    /**
     * 基准数据类型
     */
    private String baseDataType;
    
    /**
     * 评级来源类型
     */
    private String gradeSourceType;
    
    /**
     * sql排序标识
     */
    private String sqlSortString;

    /**
     * 评级日期，修改时记录修改前的评级日期
     */
    private String oldGradeDate;

    /**
     * 评级机构，修改时记录修改前的评级机构
     */
    private String oldGradeOrgId;

    private String oldGradeResult;

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

    /**
     * 获取 previousgradeDate
     * @return previousgradeDate.
     */
    public String getPreviousgradeDate() {
        return previousgradeDate;
    }




    
    
    /**
     * 获取 groupName
     * @return groupName.
     */
    public String getGroupName() {
        return groupName;
    }





    
    /**
     * 设置 groupName
     * @param groupName groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }





    /**
     * 设置 previousgradeDate
     * @param previousgradeDate previousgradeDate
     */
    public void setPreviousgradeDate(String previousgradeDate) {
        this.previousgradeDate = previousgradeDate;
    }




    
    /**
     * 获取 previousgradeResultDis
     * @return previousgradeResultDis.
     */
    public String getPreviousgradeResultDis() {
        return previousgradeResultDis;
    }




    
    /**
     * 设置 previousgradeResultDis
     * @param previousgradeResultDis previousgradeResultDis
     */
    public void setPreviousgradeResultDis(String previousgradeResultDis) {
        this.previousgradeResultDis = previousgradeResultDis;
    }




    
    /**
     * 获取 previousgradeOrgName
     * @return previousgradeOrgName.
     */
    public String getPreviousgradeOrgName() {
        return previousgradeOrgName;
    }




    
    /**
     * 设置 previousgradeOrgName
     * @param previousgradeOrgName previousgradeOrgName
     */
    public void setPreviousgradeOrgName(String previousgradeOrgName) {
        this.previousgradeOrgName = previousgradeOrgName;
    }




    /**
     * 获取 gradeExpectation
     * @return gradeExpectation.
     */
    public String getGradeExpectation() {
        return gradeExpectation;
    }

    /**
	 * 变动方向名称
	 * @return
	 */
	public String getDirectionChangeStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.变动方向, getDirectionChange());
	}

	/**
	 * 评级展望名称
	 * @return
	 */
	public String getGradeExpectationStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.评级展望, getGradeExpectation());
	}
	
	//START modify by wj 20200416 评级机构由字典中取值。 
	/**
	 * 评级展望名称
	 * @return
	 */
	public String getGradeOrgStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.评级机构, getGradeOrgId());
	}
    
    /**
     * 设置 gradeExpectation
     * @param gradeExpectation gradeExpectation
     */
    public void setGradeExpectation(String gradeExpectation) {
        this.gradeExpectation = gradeExpectation;
    }



    
    /**
     * 获取 directionChange
     * @return directionChange.
     */
    public String getDirectionChange() {
        return directionChange;
    }



    
    /**
     * 设置 directionChange
     * @param directionChange directionChange
     */
    public void setDirectionChange(String directionChange) {
        this.directionChange = directionChange;
    }



    
    /**
     * 获取 innerOuter
     * @return innerOuter.
     */
    public String getInnerOuter() {
        return innerOuter;
    }



    
    /**
     * 设置 innerOuter
     * @param innerOuter innerOuter
     */
    public void setInnerOuter(String innerOuter) {
        this.innerOuter = innerOuter;
    }



    /**
     * 获取 noticeDate
     * @return noticeDate.
     */
    public String getNoticeDate() {
        return noticeDate;
    }


    
    /**
     * 设置 noticeDate
     * @param noticeDate noticeDate
     */
    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }


    /**
     * 获取 gradeOrgName
     * @return gradeOrgName.
     */
    public String getGradeOrgName() {
        return gradeOrgName;
    }

    
    /**
     * 设置 gradeOrgName
     * @param gradeOrgName gradeOrgName
     */
    public void setGradeOrgName(String gradeOrgName) {
        this.gradeOrgName = gradeOrgName;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * 取得 债券代码（市场代码）
     * 
     * @return 债券代码（市场代码）
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_2)
    public String getoCode() {
        return oCode;
    }

    /**
     * 设置 债券代码（市场代码）
     * 
     * @param oCode
     *            债券代码（市场代码）
     */
    public void setoCode(String oCode) {
        this.oCode = oCode == null ? null : oCode.trim();
    }
    
    public String getoCodeName() {
        return oCodeName;
    }

    public void setoCodeName(String oCodeName) {
        this.oCodeName = oCodeName;
    }

    /**
     * 取得 评级机构
     * 
     * @return 评级机构
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_3)
    public String getGradeOrgId() {
        return gradeOrgId;
    }

    /**
     * 设置 评级机构
     * 
     * @param gradeOrgId
     *            评级机构
     */
    public void setGradeOrgId(String gradeOrgId) {
        this.gradeOrgId = gradeOrgId == null ? null : gradeOrgId.trim();
    }

    /**
     * 取得 评级日期
     * 
     * @return 评级日期
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_4)
    public String getGradeDate() {
        return gradeDate;
    }

    /**
     * 设置 评级日期
     * 
     * @param gradeDate
     *            评级日期
     */
    public void setGradeDate(String gradeDate) {
        this.gradeDate = gradeDate;
    }

    /**
     * 取得 评级类型，主体评级、债项评级
     * 
     * @return 评级类型，主体评级、债项评级
     */
    @PrimaryKey(value= PrimaryKey.Field.PK_1)
    public String getoType() {
        return oType;
    }

    /**
     * 设置 评级类型，主体评级、债项评级
     * 
     * @param oType
     *            评级类型，主体评级、债项评级
     */
    public void setoType(String oType) {
        this.oType = oType == null ? null : oType.trim();
    }

    /**
     * 取得 长短期
     * 
     * @return 长短期
     */
    public String getShortLongTerm() {
        return shortLongTerm;
    }

    /**
     * 设置 长短期
     * 
     * @param shortLongTerm
     *            长短期
     */
    public void setShortLongTerm(String shortLongTerm) {
        this.shortLongTerm = shortLongTerm == null ? null : shortLongTerm.trim();
    }

    /**
     * 取得 评级结果
     * 
     * @return 评级结果
     */
    public String getGradeResult() {
        return gradeResult;
    }

    /**
     * 设置 评级结果
     * 
     * @param gradeResult
     *            评级结果
     */
    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult == null ? null : gradeResult.trim();
    }

    /**
     * 取得 录入方式
     * 
     * @return 录入方式
     */
    public String getInputType() {
        return inputType;
    }

    /**
     * 设置 录入方式
     * 
     * @param inputType
     *            录入方式
     */
    public void setInputType(String inputType) {
        this.inputType = inputType == null ? null : inputType.trim();
    }

    /**
     * 取得 评级发起类型
     * 
     * @return 评级发起类型
     */
    public String getIssuerRateType() {
        return issuerRateType;
    }

    /**
     * 设置 评级发起类型
     * 
     * @param issuerRateType
     *            评级发起类型
     */
    public void setIssuerRateType(String issuerRateType) {
        this.issuerRateType = issuerRateType == null ? null : issuerRateType.trim();
    }

    public String getMstRatingResult01() {
        return mstRatingResult01;
    }

    public void setMstRatingResult01(String mstRatingResult01) {
        this.mstRatingResult01 = mstRatingResult01;
    }

    public String getMstRatingResult02() {
        return mstRatingResult02;
    }

    public void setMstRatingResult02(String mstRatingResult02) {
        this.mstRatingResult02 = mstRatingResult02;
    }
    
    /**
     * 评级机构名称
     * @return
     */
    public String getGradeOrgIdName() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级机构, getGradeOrgId());
    }

    /**
     * 评级结果名称
     * @return
     */
    public String getPreviousgradeResult() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, getPreviousgradeResultDis());
    }/**
     * 评级结果名称
     * @return
     */
    public String getGradeResultDis() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, getGradeResult());
    }
    
    /**
     * 评级类型名称
     * @return
     */
    public String getoTypeName() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级类型, getoType());
    }
    
    /**
     * 获取评级类型
     * @return String  
     */
    public String getShortLongTermName() {
        return SysDataUtil.getNameFromDataDict(DictCode.长期短期, getShortLongTerm());
    }
    
    /**
     * 获取评级类型
     * @return String  
     */
    public String getShortLongTermStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.长期短期, getShortLongTerm());
    }

    public String getMstDisType() {
        return mstDisType;
    }

    public void setMstDisType(String mstDisType) {
        this.mstDisType = mstDisType;
    }

    public String getSqlSearchString() {
        return sqlSearchString;
    }

    public void setSqlSearchString(String sqlSearchString) {
        this.sqlSearchString = sqlSearchString;
    }

    public String getGradeDateStr() {
        return gradeDateStr;
    }

    public void setGradeDateStr(String gradeDateStr) {
        this.gradeDateStr = gradeDateStr;
    }

    public String getGradeDateStart() {
        return gradeDateStart;
    }

    public void setGradeDateStart(String gradeDateStart) {
        this.gradeDateStart = gradeDateStart;
    }

    public String getGradeDateEnd() {
        return gradeDateEnd;
    }

    public void setGradeDateEnd(String gradeDateEnd) {
        this.gradeDateEnd = gradeDateEnd;
    }

    public String getGradeSourceTypeStr() {
        if (StringUtil.equals(getGradeSourceType(),"02")){
            return "内部评级";
        }else {
            return "外部评级";
        }
    }

    public String getGradeSourceType() {
        if (StringUtil.equals("98",getGradeOrgId()) || StringUtil.equals("99",getGradeOrgId())){
            return "02";
        }else {
            return "01";
        }
    }

    public void setGradeSourceType(String gradeSourceType) {
        this.gradeSourceType = gradeSourceType;
    }

    public String getBaseDataType() {
        return baseDataType;
    }

    public void setBaseDataType(String baseDataType) {
        this.baseDataType = baseDataType;
    }

    public String getSqlSortString() {
        return sqlSortString;
    }

    public void setSqlSortString(String sqlSortString) {
        this.sqlSortString = sqlSortString;
    }

    public String getOldGradeDate() {
        return oldGradeDate;
    }

    public void setOldGradeDate(String oldGradeDate) {
        this.oldGradeDate = oldGradeDate;
    }

    public String getOldGradeOrgId() {
        return oldGradeOrgId;
    }

    public void setOldGradeOrgId(String oldGradeOrgId) {
        this.oldGradeOrgId = oldGradeOrgId;
    }

    public String getOldGradeResult() {
        return oldGradeResult;
    }

    public void setOldGradeResult(String oldGradeResult) {
        this.oldGradeResult = oldGradeResult;
    }
}


