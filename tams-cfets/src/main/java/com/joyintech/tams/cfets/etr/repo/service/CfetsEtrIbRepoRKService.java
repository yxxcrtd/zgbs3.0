package com.joyintech.tams.cfets.etr.repo.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.cfets.etr.repo.dao.CfetsEtrIbRepoinfoMapper;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrIbRepoRkDto;
import com.joyintech.tams.cfets.etr.repo.dto.CfetsEtrRepoZLCXPositionResponse;
import com.joyintech.tams.common.base.dao.EtrSecStockMapper;
import com.joyintech.tams.common.base.entity.EtrEntrustPledge;
import com.joyintech.tams.common.base.entity.EtrSecStockConfig;
import com.joyintech.tams.common.base.entity.PtlSecStock;
import com.joyintech.tams.component.business.basicdata.dto.BusiFinProductRatingDto;
import com.joyintech.tams.component.business.basicdata.service.BusiProductRatingService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.workflow.engine.constant.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CfetsEtrIbRepoRKService extends BaseService<CfetsEtrIbRepoRkDto> {
    @Autowired
    private CfetsEtrIbRepoinfoMapper cfetsEtrIbRepoinfoMapper;

    @Autowired
    private EtrSecStockMapper etrSecStockMapper;
    
    @Autowired
    private CfetsEtrIbRepoinfoService cfetsEtrIbRepoinfoService;
    
    @Autowired(required = false)
    private BusiProductRatingService busiProductRatingService;
    

    
    
    /**
     * 
     * <一句话功能简述>
     * 插入
     * @param dto
     * @see
     * @since 1.0
     */
    public void insert( CfetsEtrIbRepoRkDto dto) {
		//先删后插
		cfetsEtrIbRepoinfoMapper.deleteByPrimaryKey(dto.getPledgeStockId());
		cfetsEtrIbRepoinfoMapper.delete(dto.getPledgeStockId());
		etrSecStockMapper.deleteByPrimaryKey(dto.getPledgeStockId());
		CfetsEtrIbRepoRkDto record = new CfetsEtrIbRepoRkDto();
		record.setPledgeStockId(dto.getPledgeStockId());
		record.setPledgeStockName(dto.getPledgeStockName());
		record.setIsDefStock(DictCodeList.是否_否.getValue());
		// 保存默认质押券库,更新其他质押券库为非默认
		if (StringUtil.isNotEmpty(dto.getDefaultStock())) {
			CfetsEtrIbRepoRkDto temp1 = new CfetsEtrIbRepoRkDto();
			temp1.setIsDefStock(DictCodeList.是否_否.getValue());
			temp1.setCreateUser(LoginUserUtil.getUserName());
			etrSecStockMapper.updateIsDefStockUser(temp1);
			CfetsEtrIbRepoRkDto temp2 = new CfetsEtrIbRepoRkDto();
			BeanUtil.copyPropertiesIgnoreNull(temp2, temp1);
			temp2.setIsDefStock(DictCodeList.是否_是.getValue());
			temp2.setDefaultStock(dto.getDefaultStock());
			etrSecStockMapper.updateIsDefStockUser(temp2);
		}
		if (StringUtil.equals(dto.getDefaultStock(), dto.getPledgeStockId())) {
			record.setIsDefStock(DictCodeList.是否_是.getValue());
		}
		etrSecStockMapper.insert(record);
	}
    
    /**
     * 
     * <一句话功能简述>
     * 删除
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    public int delete( CfetsEtrIbRepoRkDto dto) {
    	cfetsEtrIbRepoinfoMapper.deleteByPrimaryKey(dto.getPledgeStockId());
    	cfetsEtrIbRepoinfoMapper.delete(dto.getPledgeStockId());
        return etrSecStockMapper.deleteByPrimaryKey(dto.getPledgeStockId());
    }
    
    public int checkIsExistByPledgeStockName( Map<String, Object> map){
        return cfetsEtrIbRepoinfoMapper.checkIsExistByPledgeStockName(map);
    }
    
    public CfetsEtrIbRepoRkDto getBusinessInfoByPledgeStockId( String pledgeStockId) {
        return cfetsEtrIbRepoinfoMapper.getBusinessInfoByPledgeStockId(pledgeStockId);
    }
    
    public int checkIsExistDetail( String pledgeStockId) {
        return cfetsEtrIbRepoinfoMapper.checkIsExistDetail(pledgeStockId);
    }
    
    /**
     * 
     * <一句话功能简述>
     * 插入
     * @param dto
     * @see
     * @since 1.0
     */
    public void inserttotal( CfetsEtrIbRepoRkDto dto) {
    	//先删
    	List<CfetsEtrRepoZLCXPositionResponse> F01List = new ArrayList<CfetsEtrRepoZLCXPositionResponse>();
    	cfetsEtrIbRepoinfoService.inserttotalq(dto.getPledgeStockId());
    	List<String> portfolioId = new ArrayList<String>();
		for (EtrSecStockConfig dd : dto.getEtrSecStockConfig()) {
			cfetsEtrIbRepoinfoService.inserttotal(dd);
			Map<String, Object> map = new HashMap<>();
			portfolioId = dto.getPtlSecStock().stream().map(a -> a.getPortfolioId()).collect(Collectors.toList());
			map.put("portfolioId", portfolioId);
			// map.put("pledgeStockId", dd.getPledgeStockId());
			map.put("windtypeone", dd.getWindtypeone());
			map.put("windtypetwo", dd.getWindtypetwo());
			map.put("mGradeResult", dd.getmGradeResult());
			map.put("secGradeResult", dd.getSecGradeResult());
			map.put("isSecBond", dd.getIsSecBond());
			map.put("isRight", dd.getIsRight());
			map.put("isPpb", dd.getIsPpb());
			List<CfetsEtrRepoZLCXPositionResponse> finProductList = cfetsEtrIbRepoinfoService.selectF01List(map);
			List<CfetsEtrRepoZLCXPositionResponse> tempList = new ArrayList<>();
			if (!StringUtil.isEmptyList(finProductList)) {
				if (StringUtil.isNotEmpty(dd.getmGradeResult())) {
					for (CfetsEtrRepoZLCXPositionResponse dto2 : finProductList) {
						map.put("oCode", dto2.getIssuer());
						map.put("oType", DictCodeList.评级类型_主体评级.getValue());
						// 查询评级类型为主体评级的数据
						List<BusiFinProductRatingDto> ztList = busiProductRatingService.getZtBestRatingByOrgId(map);
						if (!StringUtil.isEmptyList(ztList)) {
							String a = ztList.stream().map(BusiFinProductRatingDto::getGradeResult).min(String::compareTo).get();
							if (dd.getmGradeResult().compareTo(a) >= 0) {//不低于主体评级
								if(StringUtil.isNotEmpty(dd.getSecGradeResult())){
									Map<String, Object> zxparam = new HashMap<String, Object>();
									zxparam.put("oCode", dto2.getFinprodMarketId());
									zxparam.put("oType", DictCodeList.评级类型_债项评级.getValue());
									// 查询评级类型是债项评级的数据
									List<BusiFinProductRatingDto> zxList = busiProductRatingService.getZxBestRatingByMarketId(zxparam);
									if (!StringUtil.isEmptyList(zxList)) {
										String b = zxList.stream().map(BusiFinProductRatingDto::getGradeResult).min(String::compareTo).get();
										if (dd.getSecGradeResult().compareTo(b) >= 0) {//不低于债项评级
											tempList.addAll(finProductList);
										}
									}
								}else{
									tempList.addAll(finProductList);
								}
							}
						}
						
					}
				}else{
					if (StringUtil.isNotEmpty(dd.getSecGradeResult())) {
						for (CfetsEtrRepoZLCXPositionResponse dto2 : finProductList) {
							Map<String, Object> zxparam = new HashMap<String, Object>();
							zxparam.put("oCode", dto2.getFinprodMarketId());
							zxparam.put("oType", DictCodeList.评级类型_债项评级.getValue());
							// 查询评级类型是债项评级的数据
							List<BusiFinProductRatingDto> zxList = busiProductRatingService.getZxBestRatingByMarketId(zxparam);
							if (!StringUtil.isEmptyList(zxList)) {
								String b = zxList.stream().map(BusiFinProductRatingDto::getGradeResult).min(String::compareTo).get();
								if (dd.getSecGradeResult().compareTo(b) >= 0) {//不低于债项评级
									tempList.addAll(finProductList);
								}
							}
						}
					}
				}
				if (!StringUtil.isNotEmpty(dd.getmGradeResult()) && !StringUtil.isNotEmpty(dd.getSecGradeResult())) {
					tempList.addAll(finProductList);
				}
			}
			F01List.addAll(tempList);
		}
    	
    	for (PtlSecStock dd: dto.getPtlSecStock()) {
    		PtlSecStock d = new PtlSecStock();
    		d.setPortfolioId(dd.getPortfolioId());
    		d.setPledgeStockId(dd.getPledgeStockId());
    		d.setPledgeStockName(dd.getPledgeStockName());
    		cfetsEtrIbRepoinfoService.insertptl(d);
        }
    	
    	if(!StringUtil.isEmptyList(F01List)){
    		Map< String, Object> param = new HashMap<>();
    		param.put("portfolioIdList", portfolioId);
    		List<CfetsEtrRepoZLCXPositionResponse> pageList =cfetsEtrIbRepoinfoService.selectZlglPositionPage(param);
                Iterator<CfetsEtrRepoZLCXPositionResponse> iterator = F01List.iterator();
                while(iterator.hasNext()){
                	CfetsEtrRepoZLCXPositionResponse avaDto=  iterator.next();
                    for (CfetsEtrRepoZLCXPositionResponse pledDto : pageList) {
                        if(StringUtil.equals(avaDto.getPortfolioId(), pledDto.getPortfolioId())&&
                            StringUtil.equals(avaDto.getSecAcctId(), pledDto.getSecAcctId())&&
                            StringUtil.equals(avaDto.getFinprodId(), pledDto.getFinprodId())&&
                            StringUtil.equals(dto.getPledgeStockId(), pledDto.getPledgeStockId())){
                            iterator.remove() ;
                        }
                    }
                }
                if(!StringUtil.isEmptyList(F01List)){
                	F01List =F01List.stream().distinct().collect(Collectors.toList());
            		for(CfetsEtrRepoZLCXPositionResponse f01:F01List){
            			EtrEntrustPledge entrustPledge = new EtrEntrustPledge();
            			entrustPledge.setPortfolioId(f01.getPortfolioId());
            			entrustPledge.setFinprodId(f01.getFinprodId());
            			entrustPledge.setBranch(f01.getBranch());
            			entrustPledge.setSecAcctId(f01.getSecAcctId());
            			entrustPledge.setScale(f01.getScale());
            			entrustPledge.setPledgeStockId(dto.getPledgeStockId());
            			cfetsEtrIbRepoinfoService.insertEtrEntrustPledge(entrustPledge);
            		}
                }
    		
    	}
    }
    
    
    public CfetsEtrIbRepoRkDto getName( String pledgeStockId) {
        return cfetsEtrIbRepoinfoMapper.getName(pledgeStockId);
    }

	@Transactional(rollbackFor = Exception.class)
	public void doComplete(CfetsEtrIbRepoRkDto entity, OperationType type) {
		if(OperationType.DELETE.equals(type)) {
			delete(entity);
		}else {
			insert(entity);
			inserttotal(entity);
		}
	}
}
