/*
 * 文件名：MstSecuritiesAccountDao.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangjun
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.mstsecuritiesaccount.dto;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.StlSecAcctInfo;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.export.paginator.domain.ITableRow;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.Length;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.invest.basicdata.mstsecuritiesaccount.validator.MstSecuritiesAccountValidator;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.annotation.PrimaryKey.Field;
import com.joyintech.workflow.engine.dto.ProcessEntity;

import java.util.List;


/**
 * 证券账户管理信息
 * 〈功能详细描述〉
 * @author zhangjun
 * @version 1.0 2018年9月11日
 * @see MstSecuritiesAccountDto
 * @since 1.0
 */
@PageId("MstSecuritiesAccount002")
@ValidLogic(targetClass = MstSecuritiesAccountValidator.class, groups = {ValidateGroupEdit.class,
    ValidateGroupDelete.class})
public class MstSecuritiesAccountDto extends StlSecAcctInfo implements ITableRow,ProcessEntity{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 操作类型
     */
    private String operateType;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String elementId;
    
    /**
     * 选中的数据
     */
    private transient List<MstSecuritiesAccountDto> selectedItem;
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private String pageModel;
    
    /**
     * 文件名称
     */
    private String fileName;
    
    /**
     * 文件编号
     */
    private String seqNo;
    /**
     * 业务代码（关联其他表的）
     */
    private String bussinessId;
    /**
     * 文件服务器路径
     */
    private String path;

    /**
     * 文件列表
     */
    private transient List<UploadFile> fileArray;

    /**
     * 托管机构-双录
     */
    private String registOrgContrast;
    /**
     * 回款账户-双录
     */
    private String recAcctIdContrast;
    /**
     * 资金结算账户-双录
     */
    private String trdAcctIdContrast;
    /**
     * 交易场所-双录
     */
    private String tradePlaceContrast;
    /**
     * 是否双录且是第一审批节点
     */
    private String isDoubleInput;


    /**
     * null
     */
    private String tradePlace2;
    
//  /**
//  * 期货公司
//  */
// private String futuresCompany;
//
// public String getFuturesCompany() {
//     return futuresCompany;
// }
//
// public void setFuturesCompany(String futuresCompany) {
//     this.futuresCompany = futuresCompany;
// }
 /**
  * 交易编号
  */
 private String tradeNo;

 public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

    public String getIsDoubleInput() {
        return isDoubleInput;
    }

    public void setIsDoubleInput(String isDoubleInput) {
        this.isDoubleInput = isDoubleInput;
    }

    public String getRegistOrgContrast() {
        return registOrgContrast;
    }

    public void setRegistOrgContrast(String registOrgContrast) {
        this.registOrgContrast = registOrgContrast;
    }

    public String getRecAcctIdContrast() {
        return recAcctIdContrast;
    }

    public void setRecAcctIdContrast(String recAcctIdContrast) {
        this.recAcctIdContrast = recAcctIdContrast;
    }

    public String getTrdAcctIdContrast() {
        return trdAcctIdContrast;
    }

    public void setTrdAcctIdContrast(String trdAcctIdContrast) {
        this.trdAcctIdContrast = trdAcctIdContrast;
    }

    public String getTradePlaceContrast() {
        return tradePlaceContrast;
    }

    public void setTradePlaceContrast(String tradePlaceContrast) {
        this.tradePlaceContrast = tradePlaceContrast;
    }
    
    /**
     * 获取 fileArray
     * @return fileArray.
     */
    public List<UploadFile> getFileArray() {
        return fileArray;
    }

    /**
     * 设置 fileArray
     * @param fileArray fileArray
     */
    public void setFileArray(List<UploadFile> fileArray) {
        this.fileArray = fileArray;
    }
    
    public String getTradePlaceStr(){
        if (super.getTradePlace() == null) {
            return StringUtil.EMPTY;
        }
    	if(super.getTradePlace().contains(",")) {
    		StringBuffer str=new StringBuffer();
    		String[] split = super.getTradePlace().split(",");
    		for (int i = 0; i < split.length; i++) {
    			if(i==split.length-1) {
    				str.append(SysDataUtil.getNameFromDataDict(DictCode.交易场所, split[i]));
    			}else {
    				str.append(SysDataUtil.getNameFromDataDict(DictCode.交易场所, split[i])+",");
    			}
			}
    		return str.toString();
    	}else {
    		   return SysDataUtil.getNameFromDataDict(DictCode.交易场所, super.getTradePlace());
    	}
    }
    
    public String getSecAcctSourceStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.账户来源, super.getSecAcctSource());
    }
    
    /**
     * 托管机构Text值
     * @return String
     */
    public String getRegistOrgStr(){
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构,super.getRegistOrg());
    }

    /**
     * 获取 operateType
     * @return operateType.
     */
    public String getOperateType() {
        return operateType;
    }

    
    /**
     * 设置 operateType
     * @param operateType operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    
    /**
     * 获取 elementId
     * @return elementId.
     */
    public String getElementId() {
        return elementId;
    }

    
    /**
     * 设置 elementId
     * @param elementId elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    
    /**
     * 获取 selectedItem
     * @return selectedItem.
     */
    public List<MstSecuritiesAccountDto> getSelectedItem() {
        return selectedItem;
    }

    
    /**
     * 设置 selectedItem
     * @param selectedItem selectedItem
     */
    public void setSelectedItem(List<MstSecuritiesAccountDto> selectedItem) {
        this.selectedItem = selectedItem;
    }

    
    /**
     * 获取 pageModel
     * @return pageModel.
     */
    public String getPageModel() {
        return pageModel;
    }

    
    /**
     * 设置 pageModel
     * @param pageModel pageModel
     */
    public void setPageModel(String pageModel) {
        this.pageModel = pageModel;
    }

    
    /**
     * 获取 fileName
     * @return fileName.
     */
    public String getFileName() {
        return fileName;
    }

    
    /**
     * 设置 fileName
     * @param fileName fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    

    
    
    /**
     * 获取 seqNo
     * @return seqNo.
     */
    public String getSeqNo() {
        return seqNo;
    }

    
    /**
     * 设置 seqNo
     * @param seqNo seqNo
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    
    /**
     * 获取 bussinessId
     * @return bussinessId.
     */
    public String getBussinessId() {
        return bussinessId;
    }

    
    /**
     * 设置 bussinessId
     * @param bussinessId bussinessId
     */
    public void setBussinessId(String bussinessId) {
        this.bussinessId = bussinessId;
    }

    /**
     * 获取 path
     * @return path.
     */
    public String getPath() {
        return path;
    }

    
    /**
     * 设置 path
     * @param path path
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * 证券账户账号
     */
    @Override
    @NotEmpty(fieldName = "MstSecuritiesAccount001.secAcctId", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "MstSecuritiesAccount001.secAcctId", max = 30, groups = {ValidateGroupEdit.class})
    @PrimaryKey(value=Field.PK_1)
    public String getSecAcctId() {
        return super.getSecAcctId();
    }
    /**
     * 
     * 证券账户名称
     */
    @Override
    @NotEmpty(fieldName = "MstSecuritiesAccount001.secAcctName", groups = {ValidateGroupEdit.class})
    // TISHKS-538:【重庆已安排】CLONE - 【清算交收】【账户管理】【证券账户管理】新建页面，证券账户名称长度不够 modify by huwei 2022/5/7 start
    @Length(fieldName = "MstSecuritiesAccount001.secAcctName", max = 60, groups = {ValidateGroupEdit.class})
    // TISHKS-538:【重庆已安排】CLONE - 【清算交收】【账户管理】【证券账户管理】新建页面，证券账户名称长度不够 modify by huwei 2022/5/7 end
    public String getSecAcctName() {
        return super.getSecAcctName();
    }
    /**
     * 托管机构
     */
    @Override
    @NotEmpty(fieldName = "MstSecuritiesAccount001.registOrg", groups = {ValidateGroupEdit.class})
    public String getRegistOrg() {
        return super.getRegistOrg();
    }
    
    /**
     * 资金结算账户
     */
    @Override
    @NotEmpty(fieldName = "MstSecuritiesAccount002.trdAcctId", groups = {ValidateGroupEdit.class})
    @Length(fieldName = "MstSecuritiesAccount002.trdAcctId", max = 100, groups = {ValidateGroupEdit.class})
    public String getTrdAcctId() {
        return super.getTrdAcctId();
    }

    /**
     * 交易场所
     */
    @Override
    @NotEmpty(fieldName = "MstSecuritiesAccount002.tradePlace", groups = {ValidateGroupEdit.class})
    public String getTradePlace() {
        return super.getTradePlace();
    }
    
    /**
     * 
     * 股东代码
     */
    @Override
    @Length(fieldName = "MstSecuritiesAccount002.bondId", max = 50, groups = {ValidateGroupEdit.class})
    public String getBondId() {
        return super.getBondId();
    }
    /**
     * 资金账户名称
     */
    private String acctName;
    /**
     * 获取资金账户名称
     * @return acctName.
     */
    public String getAcctName() {
        return acctName;
    }

    /**
     * 资金账户账号
     */
    private String busiAcctNo1;
    /**
     * 回款账户账号
     */
    private String busiAcctNo2;
    
    
    /**
     * 获取 busiAcctNo1
     * @return busiAcctNo1.
     */
    public String getBusiAcctNo1() {
        return busiAcctNo1;
    }

    
    /**
     * 设置 busiAcctNo1
     * @param busiAcctNo1 busiAcctNo1
     */
    public void setBusiAcctNo1(String busiAcctNo1) {
        this.busiAcctNo1 = busiAcctNo1;
    }

    
    /**
     * 获取 busiAcctNo2
     * @return busiAcctNo2.
     */
    public String getBusiAcctNo2() {
        return busiAcctNo2;
    }

    
    /**
     * 设置 busiAcctN2
     * @param busiAcctNo2 busiAcctNo2
     */
    public void setBusiAcctNo2(String busiAcctNo2) {
        this.busiAcctNo2 = busiAcctNo2;
    }

    
    /**
     * 设置 资金账户名称
     * @param acctName acctName
     */
    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    @Override
    @NotEmpty(fieldName = "MstSecuritiesAccount001.secAcctSource", groups = {ValidateGroupEdit.class})
    public String getSecAcctSource(){
        return super.getSecAcctSource();
    }

    public String getTradePlace2() {
        return tradePlace2;
    }

    public void setTradePlace2(String tradePlace2) {
        this.tradePlace2 = tradePlace2;
    }
}
