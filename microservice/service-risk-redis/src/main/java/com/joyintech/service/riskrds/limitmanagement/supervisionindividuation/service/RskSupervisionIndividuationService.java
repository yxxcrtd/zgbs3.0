package com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.service;

import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.risktrial.util.RiskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dao.RskSupervisionIndividuationMapper;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividAssetDelDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividConditionDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividSpecialAssetDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividSpecialStageDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskIndividUniteDto;
import com.joyintech.service.riskrds.limitmanagement.supervisionindividuation.dto.RskSupervisionIndividuationDto;

@Service("backRskSupervisionIndividuationService")
public class RskSupervisionIndividuationService extends BaseService {

    @Autowired
    private RskSupervisionIndividuationMapper rskSupervisionIndividuationMapper;

    @Autowired
    private RskIndividConditionMapper rskIndividConditionMapper;

    @Autowired
    private RskIndividUniteMapper rskIndividUniteMapper;

    @Autowired
    private RskIndividAssetDelMapper rskIndividAssetDelMapper;

    @Autowired
    private RskIndividSpecialStageMapper rskIndividSpecialStageMapper;

    @Autowired
    private RskIndividConditionLineMapper conditionLineMapper;

    public List<RskIndividConditionDto> findPageForCondition(Map<String, Object> param) {
        /*
         * if(StringUtil.equals(param.get("inedx"), "0")){ return
         * rskSupervisionIndividuationMapper.selectIndividuationForPage(param); }else
         * if(StringUtil.equals(param.get("inedx"), "4")){ return
         * rskSupervisionIndividuationMapper.selectUniteForPage(param); }
         * RskSupervisionIndividuationDto dto = new RskSupervisionIndividuationDto();
         * //List<RskSupervisionIndividuationDto> allList = new
         * ArrayList<RskSupervisionIndividuationDto>(); List<RskIndividCondition> listCondition =
         * rskSupervisionIndividuationMapper.selectIndividuationForPage(param);
         * List<RskIndividUnite> listUnite =
         * rskSupervisionIndividuationMapper.selectUniteForPage(param);
         * dto.setConditionList(listCondition); dto.setUniteList(listUnite);
         * //allList.addAll(listCondition); //allList.addAll(listUnite);
         */ return rskSupervisionIndividuationMapper.selectIndividuationForPage(param);
    }

    @Transactional(rollbackFor = Throwable.class)
    public int insert(RskSupervisionIndividuationDto dto) {
        // 将该组合下指标的所有条件都删除
        rskSupervisionIndividuationMapper.deleteAllCondition(dto);
        // 如果被删除的联合编号不为空
        //rskSupervisionIndividuationMapper.deleteAllUnite(dto);
        // 将该组合下指标的联合设置都删除
        rskSupervisionIndividuationMapper.deleteAllUnite(dto);
        // 将该组合下指标的剔除资产都删除
        rskSupervisionIndividuationMapper.deleteAllAssetDel(dto);
        // 将该组合下指标的特殊阶段都删除
        rskSupervisionIndividuationMapper.deleteAllSpecialStage(dto);
        // 将该组合下指标的自定义条件都删除
        rskSupervisionIndividuationMapper.deleteAllConditionLines(dto);
        // 如果条件设置列表不为空
        if (!StringUtil.isEmptyList(dto.getConditionList())) {
            for (RskIndividCondition e : dto.getConditionList()) {
                rskIndividConditionMapper.insert(e);
            }
        }
        // 插入联合指标
        if (!StringUtil.isEmptyList(dto.getUniteList())) {
            for (RskIndividUnite e : dto.getUniteList()) {
                rskIndividUniteMapper.insert(e);
            }
        }
        // 插入剔除资产
        if (!StringUtil.isEmptyList(dto.getAssetDelList())) {
            for (RskIndividAssetDel e : dto.getAssetDelList()) {
            	e.setIndId(dto.getIndId());
            	e.setPrtBagId(dto.getPrtBagId());
                rskIndividAssetDelMapper.insert(e);
            }
        }
        // 插入特殊资产阶段
        if (!StringUtil.isEmptyList(dto.getSpecialAssetList())) {
            for (RskIndividSpecialStage e : dto.getSpecialAssetList()) {
                rskIndividSpecialStageMapper.insert(e);
            }
        }
        // 插入特殊阶段
        if (!StringUtil.isEmptyList(dto.getSpecialStageList())) {
            for (RskIndividSpecialStage e : dto.getSpecialStageList()) {
                rskIndividSpecialStageMapper.insert(e);
            }
        }
        // 插入自定义条件
        if (!StringUtil.isEmptyList(dto.getConditionLines())) {
            for (RskIndividConditionLine e : dto.getConditionLines()) {
                e.setConditionId(IDGenerator.generate());
                e.setIndId(dto.getIndId());
                e.setPrtBagId(dto.getPrtBagId());
                conditionLineMapper.insert(e);
                // 如果是否计算组合包  === 是
//                if (StringUtil.equals(e.getIsBagCalc(),"Y")){
//                    List<String> list = rskSupervisionIndividuationMapper.findPortfolioByPackId(dto.getPrtBagId());
//                    if (list.size() > 0){
//                        for (String str : list){
//                            e.setConditionId(IDGenerator.generate());
//                            e.setPrtBagId(str);
//                            conditionLineMapper.insert(e);
//                        }
//                    }
//                }
            }
        }
        return 0;
    }

    public List<SelectOptionDto> queryUniteIndIdList(Map<String, Object> param) {
        if(StringUtil.isNotEmpty(param.get("initValue"))){
            param.put("initValue",RiskUtil.convertPortfCode(param.get("initValue").toString().replaceAll(" ","")));
        }
        return rskSupervisionIndividuationMapper.queryUniteIndIdList(param);
    }

    public List<SelectOptionDto> queryUniteIndIdList_RULE(Map<String, Object> param) {
        if(StringUtil.isNotEmpty(param.get("initValue"))){
            param.put("initValue",RiskUtil.convertPortfCode(param.get("initValue").toString().replaceAll(" ","")));
        }
        return rskSupervisionIndividuationMapper.queryUniteIndIdList_RULE(param);
    }

    public List<RskIndividUniteDto> findPageForUnite(Map<String, Object> param) {
    	List<RskIndividUniteDto> list = rskSupervisionIndividuationMapper.selectUniteForPage(param);
    	/*if(!StringUtil.isEmptyList(list)){
    		for(RskIndividUniteDto e:list){
        		e.setUniteIndId(StringUtil.splitToList(",", e.getIndId()));
        	}
    	}*/
        return list;
    }

    public Page<SelectOptionDto> queryFinprodIdList(int page,int length,Map<String, Object> param) {
        return super.queryForPage(page, length,
                () -> rskSupervisionIndividuationMapper.queryFinprodIdList(param));
    }

    public List<RskIndividAssetDelDto> findPageForAssetDel(Map<String, Object> param) {
        return rskSupervisionIndividuationMapper.findPageForAssetDel(param);
    }

    public List<RskIndividSpecialStageDto> findPageForSpecial(Map<String, Object> param) {
        return rskSupervisionIndividuationMapper.findPageForSpecial(param);
    }

    public List<RskIndividSpecialAssetDto> findPageForSpecialAsset(Map<String, Object> param) {
        return rskSupervisionIndividuationMapper.findPageForSpecialAsset(param);
    }

    public String getIndType(String indId) {
        return rskSupervisionIndividuationMapper.getIndType(indId);
    }

    public Page<SelectOptionDto> getFinprodId(int page, int length, Map<String, Object> param) {
        return this.queryForPageForSelectReturn(page, length,
            () -> rskSupervisionIndividuationMapper.getFinprodId(param));
    }

    public RskIndividSpecialStageDto queryForSpecial(String uniteId) {
        return rskSupervisionIndividuationMapper.queryForSpecial(uniteId);
    }

    public RskIndividSpecialAssetDto queryForAsset(String uniteId) {
        return rskSupervisionIndividuationMapper.queryForAsset(uniteId);
    }

	public String getVdate(String portfolioId) {
		return rskSupervisionIndividuationMapper.getVdate(portfolioId);
	}

    public List<RskIndividConditionCustom> findPageForCustomized(Map<String, Object> param) {
        return  rskSupervisionIndividuationMapper.findAll(param);
    }

    public int checkExistsUsing(Map<String, Object> param) {
        return  rskSupervisionIndividuationMapper.checkExistsUsing(param);
    }

    public Page<SelectOptionDto> queryConditionList(int page, int length, Map<String, Object> param) {
        return super.queryForPage(page, length,
                () -> rskSupervisionIndividuationMapper.queryConditionList(param));
    }

    public List<RskIndividConditionLine> findPageForCustomizedItem(Map<String, Object> param) {
        return  rskSupervisionIndividuationMapper.findAllCustomizedItem(param);
    }
}
