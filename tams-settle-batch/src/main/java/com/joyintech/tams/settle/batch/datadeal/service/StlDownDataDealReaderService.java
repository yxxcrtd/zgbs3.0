package com.joyintech.tams.settle.batch.datadeal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonDto;
import com.joyintech.tams.invest.common.transmatch.dto.ExchangeTransMatchCommonParamDto;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.SettleDataRulePartitionDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealOperatorBaseDto;

/**
 * 数据清算批处理Service
 * 
 * @author 陈卓
 * @date 2020/7/7
 * @see StlDownDataDealReaderService
 * @since 1.0
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StlDownDataDealReaderService {

	/**
	 * 是否单体服务
	 */
	@Value("${application.micorservice.contextPath.single:false}")
	private boolean isSingle;

	@Autowired(required = false)
	private JoyinRestTemplate restTemplate;

	@Autowired(required = false)
	@Qualifier("simpleRestTemplate")
	protected JoyinRestTemplate simpleRestTemplate;

	@Value("${application.micorservice.contextPath.tamsServer:http://tams-server}")
	private String tamsSerever;

    @Autowired
    protected StlDownDataDealMapper stlDownDataDealMapper;
    
    // 查询所有参数
    public List<SettleBatchPara> selectAllParam(Date dealDate) {
        return stlDownDataDealMapper.selectAllParam(dealDate);
    }

	// 查询需要撤销的清算明细数据数据TradeIds
	public List<SettleTransDataTotalMatch> selectNeedRevokeSettleTransDataTotals(Date dealDate, List<String> portfolioIdList, List<String> secTypeList){
		return stlDownDataDealMapper.selectNeedRevokeSettleTransDataTotals(dealDate, portfolioIdList, secTypeList);
	}

	public void transactionMacthRevokeBatch(List<SettleTransDataTotalMatch> dtoList) {
		ExchangeTransMatchCommonParamDto paramDto = new ExchangeTransMatchCommonParamDto();
		paramDto.setRevokeDataList(dtoList);

		// 调用批量撤销逻辑
		if(isSingle) {
			simpleRestTemplate.postForObject(tamsSerever + "/invest/trade/exchangetransmatch/ExchangeTransMatchCommon/transactionRevokeAutoBatch", paramDto, Map.class);
		}
		else {
			restTemplate.postForObject(tamsSerever + "/invest/trade/exchangetransmatch/ExchangeTransMatchCommon/transactionRevokeAutoBatch", paramDto, Map.class);
		}

	}

    // 删除【成交数据与投管交易关系】
    public int deleteSettleTransTgMapping(Date dealDate, List<String> portfolioIdList, List<String> secTypeList) {
    	return stlDownDataDealMapper.deleteSettleTransTgMapping(dealDate, portfolioIdList, secTypeList);
    }
    
    // 删除【成交数据明细质押品信息表】
    public int deleteSettleTransDataPledge(Date dealDate, List<String> portfolioIdList, List<String> secTypeList) {
    	return stlDownDataDealMapper.deleteSettleTransDataPledge(dealDate, portfolioIdList, secTypeList);
    }
    
    // 删除【成交数据明细附属表】
    public int deleteSettleTransDataAdd(Date dealDate, List<String> portfolioIdList, List<String> secTypeList) {
    	return stlDownDataDealMapper.deleteSettleTransDataAdd(dealDate, portfolioIdList, secTypeList);
    }
    
    // 删除【佣金计算过程表】
    public int deleteCommisionCalcProcess(Date dealDate, List<String> portfolioIdList, List<String> secTypeList) {
    	return stlDownDataDealMapper.deleteCommisionCalcProcess(dealDate, portfolioIdList, secTypeList);
    }
    
    // 删除【成交数据明细表】
    public int deleteSettleTransData(Date dealDate, List<String> portfolioIdList, List<String> secTypeList) {
    	return stlDownDataDealMapper.deleteSettleTransData(dealDate, portfolioIdList, secTypeList);
    }
    
    // 查询上中登JSMX
    public List<SzdJsmx> selectSzdJsmx(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdJsmx(dealDate, dataFile, searchMap);
    }
    
    // 查询深中登SJSMX
    public List<SzdSjsmx> selectSzdSjsmx(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdSjsmx(dealDate, dataFile, searchMap);
    }
    
    // 查询深中登SJSJG
    public List<SzdSjsjg> selectSzdSjsjg(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdSjsjg(dealDate, dataFile, searchMap);
    }
    
    // 查询深中登SJSFX
    public List<SzdSjsfx> selectSzdSjsfx(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdSjsfx(dealDate, dataFile, searchMap);
    }
    
    // 查询上中登ZQBD
    public List<SzdZqbd> selectSzdZqbd(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdZqbd(dealDate, dataFile, searchMap);
    }
    
    // 查询上中登ABCSJ
    public List<SzdAbcsj> selectSzdAbcsj(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdAbcsj(dealDate, dataFile, searchMap);
    }
    
    // 查询深中登ZSMX
    public List<SzdZsmx> selectSzdZsmx(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdZsmx(dealDate, dataFile, searchMap);
    }
    
    // 生成数据清算批处理step2 Reader的结果Dto
    public List<StlDownDataDealOperatorDto> generatorStlDownDataDealOperatorDto(Date dealDate, List<String> portfolioIdList, 
    		List<String> secTypeList) {
    	// 查询清算数据规则表
    	List<SettleDataRule> settleDataRuleList = stlDownDataDealMapper.selectSettleDataRuleBySecType(secTypeList);
		// 清算数据查询及分组处理
		Map<String, StlDownDataDealOperatorDto> dealSettleDataMap = new HashMap<String, StlDownDataDealOperatorDto>();
    	if (settleDataRuleList != null && settleDataRuleList.size() > 0) {
    		// 对清算数据规则表查询结果进行分组
    		Map<SettleDataRulePartitionDto, Map<String, List<String>>> map = new HashMap<SettleDataRulePartitionDto, Map<String, List<String>>>();
    		for (SettleDataRule settleDataRule : settleDataRuleList) {
    			SettleDataRulePartitionDto settleDataRulePartitionDto = new SettleDataRulePartitionDto();
    			settleDataRulePartitionDto.setDataFile(settleDataRule.getDataFile());
    			settleDataRulePartitionDto.setFinprodType(settleDataRule.getFinprodType());
    			settleDataRulePartitionDto.setTableName(settleDataRule.getTableName());
    			if (map.get(settleDataRulePartitionDto) != null) {
    				if (map.get(settleDataRulePartitionDto).containsKey(settleDataRule.getTableField())) {
    					map.get(settleDataRulePartitionDto).get(settleDataRule.getTableField()).add(settleDataRule.getFieldCode());
    				}
    				else {
    					List<String> list = new ArrayList<String>();
        				list.add(settleDataRule.getFieldCode());
        				map.get(settleDataRulePartitionDto).put(settleDataRule.getTableField(), list);
    				}
    			}
    			else {
    				Map<String, List<String>> searchMap = new HashMap<String, List<String>>();
    				List<String> list = new ArrayList<String>();
    				list.add(settleDataRule.getFieldCode());
    				searchMap.put(settleDataRule.getTableField(), list);
    				map.put(settleDataRulePartitionDto, searchMap);
    			} 
    		}
    		Iterator<SettleDataRulePartitionDto> iterator = map.keySet().iterator();
    		while (iterator.hasNext()) {
    			SettleDataRulePartitionDto settleDataRulePartitionDto = iterator.next();
    			Map<String, List<String>> searchMap = map.get(settleDataRulePartitionDto);
    			// 【上中登JSMX】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_上中登JSMX.getValue())) {
    				List<SzdJsmx> szdJsmxListTemp = this.selectSzdJsmx(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				// 由于jsmx02的配债和配股的012是重叠的，需要在这里根据secTypeList做处理
    				List<SzdJsmx> szdJsmxList = new ArrayList<SzdJsmx>();
    				for (SzdJsmx szdJsmxTemp : szdJsmxListTemp) {
    					// 业务类型时012、证券类别时GZ，业务品种处理对象不含债券跳过
    					if (StringUtil.equals(szdJsmxTemp.getYwlx(), StlDataDealProcessConstant.YWLX_012) && 
    						StringUtil.equals(szdJsmxTemp.getZqlb(), StlDataDealProcessConstant.ZQLB_GZ) &&
    						!StringUtil.equals(settleDataRulePartitionDto.getFinprodType(), DictCodeListExt1.证券品种_债券.getValue())) {
    						continue;
    					}
    					// 业务类型时012、证券类别时PG，业务品种处理对象不含股票跳过
    					if (StringUtil.equals(szdJsmxTemp.getYwlx(), StlDataDealProcessConstant.YWLX_012) && 
    						StringUtil.equals(szdJsmxTemp.getZqlb(), StlDataDealProcessConstant.ZQLB_PG) &&
    						!StringUtil.equals(settleDataRulePartitionDto.getFinprodType(), DictCodeListExt1.证券品种_股票.getValue())) {
    						continue;
    					}
    					szdJsmxList.add(szdJsmxTemp);
    				}

    				//清算数据List中以<交收编号,成交编号，业务单元1>为key分组
					Map<String,List<SzdJsmx>> szdJsmxGroupMap =  szdJsmxList.stream().collect(Collectors.groupingBy(ele ->
							new StringBuffer(ele.getJsbh()).append("#").append(ele.getCjbh()).append("#").append(ele.getXwh1()).toString()));
    				if(MapUtils.isNotEmpty(szdJsmxGroupMap)){
						szdJsmxList.clear();
    					for(Map.Entry<String,List<SzdJsmx>> entry : szdJsmxGroupMap.entrySet()){
							List<SzdJsmx> groupItemSzdJsmxList = entry.getValue();
							groupItemSzdJsmxList.removeIf(ele ->  StlDataDealProcessConstant.JLLX_002.equals(ele.getJllx()));
							szdJsmxList.addAll(groupItemSzdJsmxList);
						}
					}

    				for (SzdJsmx szdJsmx : szdJsmxList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdJsmx.getZqzh();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdJsmx.getZqzh());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getJsmxList().add(szdJsmx);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getJsmxList().add(szdJsmx);
    					}
    				}
    			}
    			// 【深中登SJSMX】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_深中登SJSMX.getValue())) {
    				List<SzdSjsmx> szdSjsmxList = this.selectSzdSjsmx(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdSjsmx szdSjsmx : szdSjsmxList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdSjsmx.getMxzqzh();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdSjsmx.getMxzqzh());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getSjsmxList().add(szdSjsmx);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSjsmxList().add(szdSjsmx);
    					}
    				}
    			}
    			// 【深中登SJSJG】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_深中登SJSJG.getValue())) {
    				List<SzdSjsjg> szdSjsjgList = this.selectSzdSjsjg(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdSjsjg szdSjsjg : szdSjsjgList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdSjsjg.getJgzqzh();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdSjsjg.getJgzqzh());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getSjsjgList().add(szdSjsjg);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSjsjgList().add(szdSjsjg);
    					}
    				}
    			}
    			// 【深中登SJSFX】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_深中登SJSFX.getValue())) {
    				List<SzdSjsfx> szdSjsfxList = this.selectSzdSjsfx(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdSjsfx szdSjsfx : szdSjsfxList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdSjsfx.getFxzqzh();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdSjsfx.getFxzqzh());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getSjsfxList().add(szdSjsfx);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSjsfxList().add(szdSjsfx);
    					}
    				}
    			}
    			// 【上中登ZQBD】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_上中登ZQBD.getValue())) {
    				List<SzdZqbd> szdZqbdList = this.selectSzdZqbd(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdZqbd szdZqbd : szdZqbdList) {
    					// 流通类型为0的数据才处理。未来了能会修改。
    					if (StringUtil.isNotEmpty(szdZqbd.getLtlx()) &&
								(StringUtil.equals(szdZqbd.getLtlx(), "0")
								|| StringUtil.equals(szdZqbd.getLtlx(), "N")
								//GNS-880:上交所配股上市流通ltlx是1 modify by fuyongsheng 20220216 start
								|| StringUtil.equals(szdZqbd.getLtlx(), "1")
								//GNS-880:上交所配股上市流通ltlx是1 modify by fuyongsheng 20220216 end
								)) {
	    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
	    							settleDataRulePartitionDto.getDataFile() + szdZqbd.getZqzh();
	    					if (dealSettleDataMap.get(key) == null) {
	    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
	    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
	    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
	    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
	    						stlDownDataDealOperatorDto.setSecAcctId(szdZqbd.getZqzh());
	    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
	    						stlDownDataDealOperatorDto.setDealDate(dealDate);
	    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
	    						stlDownDataDealBaseDto.getZqbdList().add(szdZqbd);
	    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
	    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
	    					}
	    					else {
	    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getZqbdList().add(szdZqbd);
	    					}
    					}
    				}
    			}
    			// 【上中登ABCSJ】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_上中登ABCSJ.getValue())) {
    				List<SzdAbcsj> szdAbcsjList = this.selectSzdAbcsj(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdAbcsj szdAbcsj : szdAbcsjList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdAbcsj.getZqzh();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdAbcsj.getZqzh());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getAbcsjList().add(szdAbcsj);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getAbcsjList().add(szdAbcsj);
    					}
    				}
    			}
    			// 【深中登ZSMX】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_深中登ZSMX.getValue())) {
    				List<SzdZsmx> szdZsmxList = this.selectSzdZsmx(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdZsmx szdZsmx : szdZsmxList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdZsmx.getMxzqdh();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdZsmx.getMxzqdh());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getZsmxList().add(szdZsmx);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getZsmxList().add(szdZsmx);
    					}
    				}
    			}
    			// 【上中登ZQY】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_上中登ZQY.getValue())) {
    				List<SzdZqy> szdZqyList = this.selectSzdZqy(dealDate, settleDataRulePartitionDto.getDataFile());
    				for (SzdZqy szdZqy : szdZqyList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdZqy.getGddm();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdZqy.getGddm());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getSzdZqyList().add(szdZqy);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSzdZqyList().add(szdZqy);
    					}
    				}
    			}
    			// 【深中登SJSKS】清算数据查询
    			if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_深中登SJSKS.getValue())) {
    				List<SzdSjsks> szdSjsksList = this.selectSzdSjsks(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
    				for (SzdSjsks szdSjsks : szdSjsksList) {
    					String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() + 
    							settleDataRulePartitionDto.getDataFile() + szdSjsks.getKsgddm();
    					if (dealSettleDataMap.get(key) == null) {
    						StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
    						stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
    						stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
    						stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
    						stlDownDataDealOperatorDto.setSecAcctId(szdSjsks.getKsgddm());
    						stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
    						stlDownDataDealOperatorDto.setDealDate(dealDate);
    						StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
    						stlDownDataDealBaseDto.getSzdSjsksList().add(szdSjsks);
    						stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
    						dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
    					}
    					else {
    						dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSzdSjsksList().add(szdSjsks);
    					}
    				}
    			}
				// 上中登权益登记通知信息TZXX清算数据查询
				if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_上中登TZXX.getValue())) {
					List<SzdTzxx> szdTzxxList = this.selectSzdTzxx(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
					for (SzdTzxx szdTzxx : szdTzxxList) {
						String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() +
								settleDataRulePartitionDto.getDataFile() + szdTzxx.getZqdm();
						if (dealSettleDataMap.get(key) == null) {
							StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
							stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
							stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
							stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
							stlDownDataDealOperatorDto.setSecAcctId(szdTzxx.getZqdm());
							stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
							stlDownDataDealOperatorDto.setDealDate(dealDate);
							StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
							stlDownDataDealBaseDto.getSzdTzxxList().add(szdTzxx);
							stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
							dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
						}
						else {
							dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSzdTzxxList().add(szdTzxx);
						}
					}
				}
				// 【深中登SJSGB】清算数据查询
				if (StringUtil.equals(settleDataRulePartitionDto.getTableName(), DictCodeListStl.清算数据源表_深中登SJSGB.getValue())) {
					List<SzdSjsgb> szdSjsgbList = this.selectSzdSjsgb(dealDate, settleDataRulePartitionDto.getDataFile(), searchMap);
					for (SzdSjsgb szdSjsgb : szdSjsgbList) {
						String key = settleDataRulePartitionDto.getFinprodType() + settleDataRulePartitionDto.getTableName() +
								settleDataRulePartitionDto.getDataFile();
						if (dealSettleDataMap.get(key) == null) {
							StlDownDataDealOperatorDto stlDownDataDealOperatorDto = new StlDownDataDealOperatorDto();
							stlDownDataDealOperatorDto.setFinprodType(settleDataRulePartitionDto.getFinprodType());
							stlDownDataDealOperatorDto.setTableName(settleDataRulePartitionDto.getTableName());
							stlDownDataDealOperatorDto.setDataFile(settleDataRulePartitionDto.getDataFile());
							stlDownDataDealOperatorDto.setPortfolioIdList(portfolioIdList);
							stlDownDataDealOperatorDto.setDealDate(dealDate);
							StlDownDataDealOperatorBaseDto stlDownDataDealBaseDto = new StlDownDataDealOperatorBaseDto();
							stlDownDataDealBaseDto.getSzdSjsgbList().add(szdSjsgb);
							stlDownDataDealOperatorDto.setStlDownDataDealOperatorBaseDto(stlDownDataDealBaseDto);
							dealSettleDataMap.put(key, stlDownDataDealOperatorDto);
						}
						else {
							dealSettleDataMap.get(key).getStlDownDataDealOperatorBaseDto().getSzdSjsgbList().add(szdSjsgb);
						}
					}
				}

    		}
    	}
    	
    	Iterator<String> iterator = dealSettleDataMap.keySet().iterator();
    	List<StlDownDataDealOperatorDto> result = new ArrayList<StlDownDataDealOperatorDto>();
    	while (iterator.hasNext()) {
    		result.add(dealSettleDataMap.get(iterator.next()));
    	}
    	
    	return result;
    }

    // 删除交易所品种额度（其他业务类型List）
    public int deleteFinProdQuotByOthBussType(Date dealDate, List<String> portfolioIdList, List<String> otherBusinessTypeList) {
    	return stlDownDataDealMapper.deleteFinProdQuotByOthBussType(dealDate, portfolioIdList, otherBusinessTypeList);
    }

    // 删除交易所品种额度（新股申购业务类型List）
    public int deleteFinProdQuotByXgsgBussType(Date dealDate, List<String> xgsgBusinessTypeList) {
    	return stlDownDataDealMapper.deleteFinProdQuotByXgsgBussType(dealDate, xgsgBusinessTypeList);
    }
    
    // 查询上中登ZQY
    public List<SzdZqy> selectSzdZqy(Date dealDate, String dataFile) {
    	return stlDownDataDealMapper.selectSzdZqy(dealDate, dataFile);
    }
    
    // 查询深中登SJSKS
    public List<SzdSjsks> selectSzdSjsks(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
    	return stlDownDataDealMapper.selectSzdSjsks(dealDate, dataFile, searchMap);
    }

	// 查询上中登TZXX
	public List<SzdTzxx> selectSzdTzxx(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
		return stlDownDataDealMapper.selectSzdTzxx(dealDate, dataFile, searchMap);
	}

	// 查询深中登SJSGB
	public List<SzdSjsgb> selectSzdSjsgb(Date dealDate, String dataFile, Map<String, List<String>> searchMap) {
		return stlDownDataDealMapper.selectSzdSjsgb(dealDate, dataFile, searchMap);
	}
	
	// 删除【送股权益登记】
	public int deleteShareBonusRegByCdate(Date dealDate){
		return stlDownDataDealMapper.deleteShareBonusRegByCdate(dealDate);
	}

	// 删除【基金分红公告】
	public int deletemstFundBonusNoticeByCdate(Date dealDate){
    	return stlDownDataDealMapper.deletemstFundBonusNoticeByCdate(dealDate);
	}
}
