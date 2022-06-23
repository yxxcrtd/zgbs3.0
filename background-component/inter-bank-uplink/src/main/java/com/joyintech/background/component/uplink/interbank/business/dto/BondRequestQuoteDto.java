/*
 * 文件名：BondDialogueQuetoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：caoguo
 * 修改时间：2020年5月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.uplink.interbank.business.dto;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBondDetHis;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBondHis;

/**
 * 现券请求报价DTO 〈功能详细描述〉
 * 
 * @author caoguo
 * @version 1.0 2020年5月7日
 * @see BondRequestQuoteDto
 * @since 1.0
 */
public class BondRequestQuoteDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1054467650511807010L;

    /**
     * 银行间报价主信息
     */
    private IbuQuoteBondDto ibuQuoteBondDto;

    /**
     * 银行间报价金额基对手信息列表
     */
    private List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList;
    
    /**
     * 银行间报价主流程信息
     */
    private WflIbuQuoteBond2Dto wflIbuQuoteBond2Dto;
    
    /**
     * 银行间报价金额基对手信息列表
     */
    private List<WflIbuQuoteBondDetDto> wflIbuQuoteBondDetDtoList;
  
    /**
     * 银行间上行报价属性配置
     */
    private Map<String, String> attrMap;
    
    /**
     * 银行间报价指令关系业务表
     */
    private List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList;


    /**
     * 银行间报价指令关系流程表
     */
    private List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrReDtoList;


    /**
     * 是否多组合
     */
    private String isDzh;
    
    public BondRequestQuoteDto(IbuQuoteBondDto ibuQuoteBondDto, List<IbuQuoteBondDetDto> IbuQuoteBondDetDtoList) {
        this.ibuQuoteBondDto = ibuQuoteBondDto;
        this.ibuQuoteBondDetDtoList = IbuQuoteBondDetDtoList;
    }
    
    public BondRequestQuoteDto(WflIbuQuoteBond2Dto wflIbuQuoteBond2Dto, List<WflIbuQuoteBondDetDto> wflIbuQuoteBondDetDtoList) {
        this.setWflIbuQuoteBond2Dto(wflIbuQuoteBond2Dto);
        this.setWflIbuQuoteBondDetDtoList(wflIbuQuoteBondDetDtoList);
    }
    
    public BondRequestQuoteDto(IbuQuoteBondDto ibuQuoteBondDto) {
        this.ibuQuoteBondDto = ibuQuoteBondDto;
    }
    
    public BondRequestQuoteDto(){
        
    }

    /**
     * 获取 ibuQuoteBondDto
     * @return ibuQuoteBondDto.
     */
    public IbuQuoteBondDto getIbuQuoteBondDto() {
        return ibuQuoteBondDto;
    }

    /**
     * 设置 ibuQuoteBondDto
     * @param ibuQuoteBondDto ibuQuoteBondDto
     */
    public void setIbuQuoteBondDto(IbuQuoteBondDto ibuQuoteBondDto) {
        this.ibuQuoteBondDto = ibuQuoteBondDto;
    }

    public List<IbuQuoteBondDetDto> getIbuQuoteBondDetDtoList() {
        return ibuQuoteBondDetDtoList;
    }

    public void setIbuQuoteBondDetDtoList(List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList) {
        this.ibuQuoteBondDetDtoList = ibuQuoteBondDetDtoList;
    }

    /**
     * 获取 exeTypeIBU
     * @return exeTypeIBU.
     */
    public String getExeTypeIBU() {
        if (this.ibuQuoteBondDto != null) {
            return this.ibuQuoteBondDto.getExeType();
        }
        return null;
    }

    /**
     * 设置 exeTypeIBU
     * @param exeTypeIBU exeTypeIBU
     */
    public void setExeTypeIBU(String exeTypeIBU) {
        if (this.ibuQuoteBondDto != null) {
            this.ibuQuoteBondDto.setExeType(exeTypeIBU);
        }
    }
    
    /**
     * 设置报价信息及报价明细信息的版本号
     * @param version Long 当前版本号
     * @param bVersion Long 前置版本号
     */
    public void setVersion(Long version, Long bVersion) {
        if (this.ibuQuoteBondDto != null) {
            this.ibuQuoteBondDto.setVersion(version);
            this.ibuQuoteBondDto.setbVersion(bVersion);
        }
        if (this.ibuQuoteBondDetDtoList != null) {
            for(IbuQuoteBondDetDto ibuQuoteBondDetDto : this.ibuQuoteBondDetDtoList){
                ibuQuoteBondDetDto.setVersion(version);
            }
        }
    }
    
    public void setIbuQuoteBondDtoByHis(IbuQuoteBondHis quoteBondHis) {
        IbuQuoteBondDto quoteBond = new IbuQuoteBondDto();
        try {
            BeanUtils.copyProperties(quoteBond, quoteBondHis);
            this.ibuQuoteBondDto = quoteBond;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public IbuQuoteBondHis getIbuQuoteBondDtoByHis(){
    	return null;
    }
    
    public void setIbuQuoteBondDetDtoByHis(List<IbuQuoteBondDetHis> quoteBondDetHis) {
        try {
            if (this.ibuQuoteBondDetDtoList == null) {
                this.ibuQuoteBondDetDtoList = new ArrayList<>();
            }

            if (CollectionUtils.isNotEmpty(quoteBondDetHis)) {
                this.ibuQuoteBondDetDtoList = quoteBondDetHis.stream().map(p -> {
                    Optional<IbuQuoteBondDetDto> oldDetOpt = this.ibuQuoteBondDetDtoList.stream().filter(
                        q -> StringUtils.equals(q.getDetId(), p.getDetId())).findFirst();

                    if (oldDetOpt.isPresent()) {
                        BeanUtil.copyPropertiesIgnoreNull(oldDetOpt.get(), p);
                        return oldDetOpt.get();
                    }
                    else {
                        IbuQuoteBondDetDto detDto = new IbuQuoteBondDetDto();
                        BeanUtil.copyPropertiesIgnoreNull(detDto, p);
                        return detDto;
                    }
                }).collect(Collectors.toList());
            }
        }
        catch (BeansException e) {
            e.printStackTrace();
        }
    }

    public List<IbuQuoteBondDetHis> getIbuQuoteBondDetDtoByHis(){
    	return null;
    }

    /**
     * 获取 attrMap
     * @return attrMap.
     */
    public Map<String, String> getAttrMap() {
        return attrMap;
    }

    /**
     * 设置 attrMap
     * @param attrMap attrMap
     */
    public void setAttrMap(Map<String, String> attrMap) {
        this.attrMap = attrMap;
    }

    public boolean isEmpty() {
        return this.ibuQuoteBondDto == null && CollectionUtils.isEmpty(this.ibuQuoteBondDetDtoList);
    }

	public WflIbuQuoteBond2Dto getWflIbuQuoteBond2Dto() {
		return wflIbuQuoteBond2Dto;
	}

	public void setWflIbuQuoteBond2Dto(WflIbuQuoteBond2Dto wflIbuQuoteBond2Dto) {
		this.wflIbuQuoteBond2Dto = wflIbuQuoteBond2Dto;
	}

	public List<WflIbuQuoteBondDetDto> getWflIbuQuoteBondDetDtoList() {
		return wflIbuQuoteBondDetDtoList;
	}

	public void setWflIbuQuoteBondDetDtoList(List<WflIbuQuoteBondDetDto> wflIbuQuoteBondDetDtoList) {
		this.wflIbuQuoteBondDetDtoList = wflIbuQuoteBondDetDtoList;
	}

	public List<IbuQuoteEtrReDto> getIbuQuoteEtrReDtoList() {
		return ibuQuoteEtrReDtoList;
	}

	public void setIbuQuoteEtrReDtoList(List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList) {
		this.ibuQuoteEtrReDtoList = ibuQuoteEtrReDtoList;
	}

	public List<WflIbuQuoteEtrReDto> getWflIbuQuoteEtrReDtoList() {
		return wflIbuQuoteEtrReDtoList;
	}

	public void setWflIbuQuoteEtrReDtoList(List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrReDtoList) {
		this.wflIbuQuoteEtrReDtoList = wflIbuQuoteEtrReDtoList;
	}

	public String getIsDzh() {
		return isDzh;
	}

	public void setIsDzh(String isDzh) {
		this.isDzh = isDzh;
	}
}
