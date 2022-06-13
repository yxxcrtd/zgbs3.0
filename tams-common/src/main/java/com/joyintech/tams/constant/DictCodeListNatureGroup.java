/**  
* <p>Title: DictCodeListNatureGroup.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2017</p>  
* <p>Company: www.joyintech.com</p>  
* @author jinmaodong  
* @date 2020-10-19  
* @version 1.0  
*/  
package com.joyintech.tams.constant;

import com.joyintech.framework.base.BaseDictCodeList;
import com.joyintech.framework.base.intf.IDictCode;

/**  
* <p>Title: DictCodeListNatureGroup</p>  
* <p>Description: </p>  
* @author JinMaoDong
* @date 2020-10-19  
*/
public enum DictCodeListNatureGroup implements BaseDictCodeList {
	分组代码_非摊余成本对应科目(DictCode.分组代码,"ACOUT_KMCX","非摊余成本对应科目"),
	分组代码_摊余成本对应科目(DictCode.分组代码,"AC_KMCX","摊余成本对应科目"),
	分组代码_成本类对应科目查询(DictCode.分组代码,"CBL_KMCX","成本类对应科目查询"),
	分组代码_非综合收益对应科目(DictCode.分组代码,"FVOCIOUT_KMCX","非综合收益对应科目"),
	分组代码_综合收益对应科目(DictCode.分组代码,"FVOCI_KMCX","综合收益对应科目"),
	分组代码_非公允价值对应科目(DictCode.分组代码,"FVTPLOUT_KMCX","非公允价值对应科目"),
	分组代码_公允价值对应科目(DictCode.分组代码,"FVTPL_KMCX","公允价值对应科目"),
	分组代码_公允价值变动损益对应查询科目(DictCode.分组代码,"GYJZBD_KMCX","公允价值变动损益对应查询科目"),
	分组代码_损益平准交_未实现对应查询科目(DictCode.分组代码,"SYPZJWSX_KMCX","损益平准交_未实现对应查询科目"),
	分组代码_现金类对应科目查询(DictCode.分组代码,"XJL_KMCX","现金类对应科目查询"),
	分组代码_投资端费用科目查询(DictCode.分组代码,"TZDFY_KMCX","投资端费用科目查询"),
	分组代码_公允价值查询对应科目(DictCode.分组代码,"GYJZBDZC_KMCX","公允价值查询对应科目"),
	分组代码_银行账公允价值查询对应科目(DictCode.分组代码, "BANKGYJZBDZC_KMCX", "银行账公允价值查询对应科目"),
	分组代码_估值表_公允价值变动列表(DictCode.分组代码, "GZB_GYJZBD", "估值表_公允价值变动列表"),
	分组代码_估值表_利息调整列表(DictCode.分组代码, "GZB_LXTZ", "估值表_利息调整列表"),
    分组代码_减值计提成本类对应科查询(DictCode.分组代码,"LOSS_CB_KMCX","减值计提成本类对应科目查询"),
    分组代码_减值计提利息类对应科目查询(DictCode.分组代码,"LOSS_LX_KMCX","减值计提利息类对应科目查询"),
    分组代码_减值计提利息调整类对应科目查询(DictCode.分组代码,"LOSS_LXTZ_KMCX","减值计提利息调整类对应科目查询"),
    分组代码_减值计提减值准备类对应科目查询(DictCode.分组代码,"LOSS_JZZB_KMCX","减值计提减值准备类对应科目查询"),
    分组代码_增值税损益科目查询(DictCode.分组代码,"TAX_LXSR_KMCX","增值税损益科目查询"),
	分组代码_其他综合收益对应查询科目(DictCode.分组代码,"QTZHSY_KMCX","其他综合收益对应查询科目"),

	;
	
	/**  
	* <p>Title: </p>  
	* <p>Description: </p>  
	* @param dictCode
	* 	数据字典code
	* @param value
	* 	枚举值code
	* @param codeName
	* 	枚举值名称  
	*/  
	DictCodeListNatureGroup(DictCode dictCode, String value, String codeName) {
        this.dictCode = dictCode;
        this.value = value;
        this.setCodeName(codeName);
    }
	
	/**
	 * 对应于数据字典code
	 */
	private String value;

	/**
	 * 数据字典枚举
	 */
	private DictCode dictCode;

	/**
	 * 明细名称
	 */
	private String codeName;
	/* (non-Javadoc)  
	 * <p>Title: getDictCode</p>  
	 * <p>Description: </p>  
	 * @return  
	 * @see com.joyintech.framework.base.intf.IDictCodeList#getDictCode()  
	 */
	@Override
	public IDictCode getDictCode() {
		// TODO Auto-generated method stub
		return dictCode;
	}

	/* (non-Javadoc)  
	 * <p>Title: getValue</p>  
	 * <p>Description: </p>  
	 * @return  
	 * @see com.joyintech.framework.base.intf.IDictCodeList#getValue()  
	 */
	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return value;
	}

	/* (non-Javadoc)  
	 * <p>Title: setValue</p>  
	 * <p>Description: </p>  
	 * @param value  
	 * @see com.joyintech.framework.base.intf.IDictCodeList#setValue(java.lang.String)  
	 */
	@Override
	public void setValue(String value) {
		// TODO Auto-generated method stub
        this.value = value;
	}

	/* (non-Javadoc)  
	 * <p>Title: getCodeName</p>  
	 * <p>Description: </p>  
	 * @return  
	 * @see com.joyintech.framework.base.intf.IDictCodeList#getCodeName()  
	 */
	@Override
	public String getCodeName() {
		// TODO Auto-generated method stub
        return codeName;
	}

	/* (non-Javadoc)  
	 * <p>Title: setCodeName</p>  
	 * <p>Description: </p>  
	 * @param codeName  
	 * @see com.joyintech.framework.base.intf.IDictCodeList#setCodeName(java.lang.String)  
	 */
	@Override
	public void setCodeName(String codeName) {
		// TODO Auto-generated method stub
        this.codeName = codeName;
	}

}
