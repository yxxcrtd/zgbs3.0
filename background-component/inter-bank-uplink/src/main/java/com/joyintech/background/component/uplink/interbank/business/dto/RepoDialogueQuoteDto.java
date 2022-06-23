/*
 * 文件名：RepoDialogueQuetoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Jiaoxujin
 * 修改时间：2020年5月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.background.component.uplink.interbank.business.dto;

import com.joyintech.background.component.common.pack.common.base.BaseEntity;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteCrepHis;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteCrepPledgeHis;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 质押式回购报价DTO
 * 
 * @author Jiaoxujin
 * @version 1.0 2020年5月8日
 * @see RepoDialogueQuoteDto
 * @since 1.0
 */
public class RepoDialogueQuoteDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 6850751996248978441L;

    /**
     * 回购报价主信息
     */
    private IbuQuoteCrepDto ibuQuoteCrepDto;

    /**
     * 质押品信息
     */
    private List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList;
    
    /**
     * 回购报价主流程信息
     */
    private WflIbuQuoteCrepDto wflIbuQuoteCrepDto;
    
    /**
     * 质押品流程信息
     */
    private List<WflIbuQuoteCrepPledgeDto> wflIbuQuoteCrepPledgeDtoList;


    /**
     * 银行间报价指令关系业务表
     */
    private List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList;

    /**
     * 回购报价指令质押品业务表
     */
    private List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledgeDtoList;

    /**
     * 银行间报价指令关系流程表
     */
    private List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrReDtoList;

    /**
     * 回购报价指令质押品业务流程表
     */
    private List<WflIbuQuoteEntCrepPledgeDto> wflIbuQuoteEntCrepPledgeDtoList;

    /**
     * 是否多组合
     */
    private String isDzh;

    /**
     * 银行间上行报价属性配置
     */
    private Map<String, String> attrMap;
    
    /**
     * 获取 ibuQuoteCrepDto
     * @return ibuQuoteCrepDto.
     */
    public IbuQuoteCrepDto getIbuQuoteCrepDto() {
        return ibuQuoteCrepDto;
    }
    
    public RepoDialogueQuoteDto(){
        
    }
    
    
    public Map<String, String> getAttrMap() {
        return attrMap;
    }

    
    public void setAttrMap(Map<String, String> attrMap) {
        this.attrMap = attrMap;
    }

    public RepoDialogueQuoteDto(IbuQuoteCrepDto ibuQuoteCrepDto,List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList){
        this.ibuQuoteCrepDto = ibuQuoteCrepDto;
        this.ibuQuoteCrepPledgeDtoList = ibuQuoteCrepPledgeDtoList;
    }

    public RepoDialogueQuoteDto(IbuQuoteCrepDto ibuQuoteCrepDto,List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList,List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList,List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledgeDtoList){
        this.ibuQuoteCrepDto = ibuQuoteCrepDto;
        this.ibuQuoteCrepPledgeDtoList = ibuQuoteCrepPledgeDtoList;
        this.ibuQuoteEtrReDtoList = ibuQuoteEtrReDtoList;
        this.ibuQuoteEntCrepPledgeDtoList = ibuQuoteEntCrepPledgeDtoList;
    }

    public RepoDialogueQuoteDto(WflIbuQuoteCrepDto wflIbuQuoteCrepDto,List<WflIbuQuoteCrepPledgeDto> wflIbuQuoteCrepPledgeDtoList){
        this.setWflIbuQuoteCrepDto(wflIbuQuoteCrepDto);
        this.setWflIbuQuoteCrepPledgeDtoList(wflIbuQuoteCrepPledgeDtoList);
    }
    
    /**
     * 设置 ibuQuoteCrepDto
     * @param ibuQuoteCrepDto ibuQuoteCrepDto
     */
    public void setIbuQuoteCrepDto(IbuQuoteCrepDto ibuQuoteCrepDto) {
        this.ibuQuoteCrepDto = ibuQuoteCrepDto;
    }
    
    /**
     * 获取 ibuQuoteCrepPledgeDtoList
     * @return ibuQuoteCrepPledgeDtoList.
     */
    public List<IbuQuoteCrepPledgeDto> getIbuQuoteCrepPledgeDtoList() {
        return ibuQuoteCrepPledgeDtoList;
    }
    
    /**
     * 设置 ibuQuoteCrepPledgeDtoList
     * @param ibuQuoteCrepPledgeDtoList ibuQuoteCrepPledgeDtoList
     */
    public void setIbuQuoteCrepPledgeDtoList(List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList) {
        this.ibuQuoteCrepPledgeDtoList = ibuQuoteCrepPledgeDtoList;
    }

    /**
     * 获取 exeTypeIBU
     * 
     * @return exeTypeIBU.
     */
    public String getExeTypeIBU() {
        if (this.ibuQuoteCrepDto != null) {
            return this.ibuQuoteCrepDto.getExeType();
        }
        return null;
    }

    /**
     * 设置 exeTypeIBU
     * 
     * @param exeTypeIBU exeTypeIBU
     */
    public void setExeTypeIBU(String exeTypeIBU) {
        if (this.ibuQuoteCrepDto != null) {
            this.ibuQuoteCrepDto.setExeType(exeTypeIBU);
        }
    }
    
    /**
     * 设置报价信息及报价明细信息的版本号
     * @param version Long 当前版本号
     * @param bVersion Long 前置版本号
     */
    public void setVersion(Long version, Long bVersion) {
        if (this.ibuQuoteCrepDto != null) {
            this.ibuQuoteCrepDto.setVersion(version);
            this.ibuQuoteCrepDto.setbVersion(bVersion);
        }
        if (CollectionUtils.isNotEmpty(this.ibuQuoteCrepPledgeDtoList)) {
            for(IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList){
                ibuQuoteCrepPledgeDto.setVersion(version);
            }
        }
        if (CollectionUtils.isNotEmpty(this.ibuQuoteEtrReDtoList)) {
            for(IbuQuoteEtrReDto ibuQuoteEtrReDto : ibuQuoteEtrReDtoList){
                ibuQuoteEtrReDto.setVersion(version);
            }
        }
        if (CollectionUtils.isNotEmpty(this.ibuQuoteEntCrepPledgeDtoList)) {
            for(IbuQuoteEntCrepPledgeDto ibuQuoteEntCrepPledgeDto : ibuQuoteEntCrepPledgeDtoList){
                ibuQuoteEntCrepPledgeDto.setVersion(version);
            }
        }
    }
    
    public void setIbuQuoteRepoDtoByHis(IbuQuoteCrepHis ibuQuoteCrepHis) {
        IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
        try {
            BeanUtils.copyProperties(ibuQuoteCrepDto, ibuQuoteCrepHis);
            this.ibuQuoteCrepDto = ibuQuoteCrepDto;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public IbuQuoteCrepHis getIbuQuoteRepoDtoByHis(){
    	return null;
    }

    public void setIbuQuoteCrepPledgeDtoByHis(List<IbuQuoteCrepPledgeHis>  ibuQuoteCrepPledgeHis) {
        try {
            if (this.ibuQuoteCrepPledgeDtoList == null) {
                this.ibuQuoteCrepPledgeDtoList = new ArrayList<>();
            }

            if (CollectionUtils.isNotEmpty(ibuQuoteCrepPledgeHis)) {
                this.ibuQuoteCrepPledgeDtoList = ibuQuoteCrepPledgeHis.stream().map(p -> {
                    Optional<IbuQuoteCrepPledgeDto> oldCrepPledgeOpt = this.ibuQuoteCrepPledgeDtoList.stream().filter(
                        q -> StringUtils.equals(q.getQuoteId(), p.getQuoteId())
                             && Long.compare(q.getBranch(), p.getBranch()) == 0
                             && StringUtils.equals(q.getPledgeFinprodId(), p.getPledgeFinprodId())).findFirst();

                    if (oldCrepPledgeOpt.isPresent()) {
                        BeanUtil.copyPropertiesIgnoreNull(oldCrepPledgeOpt.get(), p);
                        return oldCrepPledgeOpt.get();
                    }
                    else {
                        IbuQuoteCrepPledgeDto detDto = new IbuQuoteCrepPledgeDto();
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
    
    public List<IbuQuoteCrepPledgeHis> getIbuQuoteCrepPledgeDtoByHis(){
    	return null;
    }

    public boolean isEmpty() {
        return this.ibuQuoteCrepDto == null && CollectionUtils.isEmpty(this.ibuQuoteCrepPledgeDtoList);
    }

	public WflIbuQuoteCrepDto getWflIbuQuoteCrepDto() {
		return wflIbuQuoteCrepDto;
	}

	public void setWflIbuQuoteCrepDto(WflIbuQuoteCrepDto wflIbuQuoteCrepDto) {
		this.wflIbuQuoteCrepDto = wflIbuQuoteCrepDto;
	}

	public List<WflIbuQuoteCrepPledgeDto> getWflIbuQuoteCrepPledgeDtoList() {
		return wflIbuQuoteCrepPledgeDtoList;
	}

	public void setWflIbuQuoteCrepPledgeDtoList(List<WflIbuQuoteCrepPledgeDto> wflIbuQuoteCrepPledgeDtoList) {
		this.wflIbuQuoteCrepPledgeDtoList = wflIbuQuoteCrepPledgeDtoList;
	}

    public List<WflIbuQuoteEtrReDto> getWflIbuQuoteEtrReDtoList() {
        return wflIbuQuoteEtrReDtoList;
    }

    public void setWflIbuQuoteEtrReDtoList(List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrReDtoList) {
        this.wflIbuQuoteEtrReDtoList = wflIbuQuoteEtrReDtoList;
    }

    public List<WflIbuQuoteEntCrepPledgeDto> getWflIbuQuoteEntCrepPledgeDtoList() {
        return wflIbuQuoteEntCrepPledgeDtoList;
    }

    public void setWflIbuQuoteEntCrepPledgeDtoList(List<WflIbuQuoteEntCrepPledgeDto> wflIbuQuoteEntCrepPledgeDtoList) {
        this.wflIbuQuoteEntCrepPledgeDtoList = wflIbuQuoteEntCrepPledgeDtoList;
    }

    public List<IbuQuoteEtrReDto> getIbuQuoteEtrReDtoList() {
        return ibuQuoteEtrReDtoList;
    }

    public void setIbuQuoteEtrReDtoList(List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList) {
        this.ibuQuoteEtrReDtoList = ibuQuoteEtrReDtoList;
    }

    public List<IbuQuoteEntCrepPledgeDto> getIbuQuoteEntCrepPledgeDtoList() {
        return ibuQuoteEntCrepPledgeDtoList;
    }

    public void setIbuQuoteEntCrepPledgeDtoList(List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledgeDtoList) {
        this.ibuQuoteEntCrepPledgeDtoList = ibuQuoteEntCrepPledgeDtoList;
    }

    public String getIsDzh() {
        return isDzh;
    }

    public void setIsDzh(String isDzh) {
        this.isDzh = isDzh;
    }
}
