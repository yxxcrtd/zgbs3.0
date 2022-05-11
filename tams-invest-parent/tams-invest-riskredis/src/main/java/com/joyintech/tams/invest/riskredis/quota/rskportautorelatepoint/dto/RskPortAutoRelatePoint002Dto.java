package com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.RskRuleInfo;
import com.joyintech.tams.common.base.entity.RskRulePoolConfig;
import com.joyintech.tams.invest.common.prodinfo.dto.IPtlRule;
import com.joyintech.tams.invest.common.prodinfo.dto.IRskRulePoolConfig;
import com.joyintech.tams.invest.riskredis.quota.rskportautorelatepoint.valid.RskPortAutoRelatePoint002Validator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

@PageId("RskPortAutoRelatePoint002")
@ValidLogic(targetClass = RskPortAutoRelatePoint002Validator.class,groups = {
	    ValidateGroupEdit.class,ValidateGroupDelete.class})
public class RskPortAutoRelatePoint002Dto extends RskRuleInfo implements ITableRow,ProcessEntity, IPtlRule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    /**
     * 分类维度
     */
    private String classifyDimension;
    /**
     * 分类维度值,多个用逗号分隔
     */
    private String classifyDimensionVal;

    /**
     * 连接符
     */
    private String linkSign;

    /**
     * 参数序号
     */
    private Long paramNum;
    
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 页面参数
     */
    private String pageModel;
    /**
     * 画面Id - 工作流参数
     */
    private String pageId;
    /**
     * 按钮ID - 工作流参数
     */
    private String elementId;
    /**
     * 选择数据
     */
    private List<RskPortAutoRelatePoint002Dto> selectedItem;

	public String getRuleId() {
		return StringUtil.isNotEmpty(super.getRuleId())
				?super.getRuleId():StringUtil.EMPTY;
	}
    @Override
	@Length(fieldName = "RskPortAutoRelatePoint002.ruleName", max = 60,groups={ValidateGroupEdit.class})
	@PrimaryKey(Field.PK_1)
	public String getRuleName() {
		return super.getRuleName();
	}

	
	public String getClassifyDimension() {
		return classifyDimension;
	}

	public void setClassifyDimension(String classifyDimension) {
		this.classifyDimension = classifyDimension;
	}

	public String getClassifyDimensionVal() {
		return classifyDimensionVal;
	}

	public void setClassifyDimensionVal(String classifyDimensionVal) {
		this.classifyDimensionVal = classifyDimensionVal;
	}

	public String getLinkSign() {
		return linkSign;
	}

	public void setLinkSign(String linkSign) {
		this.linkSign = linkSign;
	}

	public Long getParamNum() {
		return paramNum;
	}

	public void setParamNum(Long paramNum) {
		this.paramNum = paramNum;
	}

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

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public List<RskPortAutoRelatePoint002Dto> getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(List<RskPortAutoRelatePoint002Dto> selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<String> matchPortfolioInfo(Function<List<IRskRulePoolConfig>,List<String>> function){
		// 循环插入规则入池
		List<IRskRulePoolConfig> ruleList=new ArrayList<>();
		for (RskPortAutoRelatePoint002Dto e : getSelectedItem()) {
			IRskRulePoolConfig rskRulePoolConfig = new IRskRulePoolConfig();
			BeanUtil.copyPropertiesIgnoreNull(rskRulePoolConfig, e);
			rskRulePoolConfig.setRuleId(getRuleId());
			ruleList.add(rskRulePoolConfig);
		}
		return function.apply(ruleList);
	}

}
