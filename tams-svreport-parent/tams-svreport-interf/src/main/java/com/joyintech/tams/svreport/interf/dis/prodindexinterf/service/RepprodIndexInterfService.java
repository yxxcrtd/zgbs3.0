package com.joyintech.tams.svreport.interf.dis.prodindexinterf.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.FileImpService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.InterfFile;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.svreport.interf.dis.common.DisInterfDecimalUtil;
import com.joyintech.tams.svreport.interf.dis.prodindexinterf.dao.DisRepProdIndexMapper;
import com.joyintech.tams.svreport.interf.dis.prodindexinterf.dto.RepProdIndex;

/**
 * 产品指标表接口
 * @author dongxiangyang
 *
 */
@Service
public class RepprodIndexInterfService extends FileImpService{

	@Autowired
	private  DisRepProdIndexMapper  repProdIndexMapper;
	
	  /**
     * 长sql最大一次插入或者更新条数
     */
    @Value("${application.reportInsertLongCount}")
    public int reportInsertLongCount;
	/**
	 * 重写saveData方法
	 * 产品指标信息入库
	 * listData 费用交易list saveFlag 入库标识
	 */
	@Override
	public String saveData(List<Map<String, String>> listData, String saveFlag, String dateSource) {
		 if (StringUtil.isEmptyList(listData)) {
	            logger.info("saveData方法参数listData为空。");
	            return "success";
	        }
		    //全部删除
//			SimpleDateFormat  fo=new SimpleDateFormat("yyyy-MM-dd");
//			repProdIndexMapper.deleteByValueDate(fo.format(getNowSysDate()));
			// modify by convertMapToEntity yangxianjiu 2022-01-19 start
			List<RepProdIndex>  list=new ArrayList<>();
			List<RepProdIndex>  listTemp = new ArrayList<>();
		 	list = (List<RepProdIndex>) convertMapToEntity(listData,RepProdIndex.class);
		 	list.stream().forEach(dto->{
				dto.setInputType(null);
				dto.setInputUser(null);
				dto.setAdapterId("0");
				dto.setCreateDept(null);
				if (listTemp.size() < reportInsertLongCount) {
					listTemp.add(dto);
				} else if (listTemp.size()  >= reportInsertLongCount) {
					repProdIndexMapper.mergeRepProdIndex(listTemp);
					listTemp.clear();
					listTemp.add(dto);
				}
			});
			if (CollectionUtils.isNotEmpty(listTemp)) {
				repProdIndexMapper.mergeRepProdIndex(listTemp);
			}
			//循环参数
//			for(Map<String, String> map:listData){
//				RepProdIndex dto=new RepProdIndex();
//				try{
//					//dto.setSeqNo(map.get("SEQ_NO"));
//					dto.setProdId(map.get("PROD_ID"));
//					dto.setValueDate(DateUtil.parse(map.get("VALUE_DATE")));
//					dto.setCapital(DisInterfDecimalUtil.str2BigDecimal(map.get("CAPITAL")));
//					dto.setAssetValueCost(DisInterfDecimalUtil.str2BigDecimal(map.get("ASSET_VALUE_COST")));
//				    dto.setAssetValueMarket(DisInterfDecimalUtil.str2BigDecimal(map.get("ASSET_VALUE_MARKET")));
//				    dto.setAssetTotalMarket(DisInterfDecimalUtil.str2BigDecimal(map.get("ASSET_TOTAL_MARKET")));
//				    dto.setUnitValue(DisInterfDecimalUtil.str2BigDecimal(map.get("UNIT_VALUE")));
//				    dto.setUnitValueT(DisInterfDecimalUtil.str2BigDecimal(map.get("UNIT_VALUE_T")));
//				    //dto.setDeviationAmt(new BigDecimal(map.get("DEVIATION_AMT")));
//				    //dto.setDeviation(new BigDecimal(map.get("DEVIATION")));
//				    dto.setProfitTdy(DisInterfDecimalUtil.str2BigDecimal(map.get("PROFIT_TDY")));
//				    //dto.setProfitTotal(new BigDecimal(map.get("PROFIT_TOTAL")));
//				    dto.setwYield(DisInterfDecimalUtil.str2BigDecimal(map.get("W_YIELD")));
//				    dto.setYieldTdy(DisInterfDecimalUtil.str2BigDecimal(map.get("YIELD_TDY")));
//				    dto.setYield7(DisInterfDecimalUtil.str2BigDecimal(map.get("YIELD_7")));
//				    dto.setYieldTotal(DisInterfDecimalUtil.str2BigDecimal(map.get("YIELD_TOTAL")));
//				    //dto.setYieldE(new BigDecimal(map.get("YIELD_E")));
//				    //dto.setYieldEMax(new BigDecimal(map.get("YIELD_E_MAX")));
//				    //dto.setSurplusTerm(Integer.getInteger(map.get("SURPLUS_TERM")));
//				    //dto.setAverageMdate(Integer.getInteger(map.get("AVERAGE_MDATE")));
//
//				    //添加偏离度接入
//			        dto.setDeviation(DisInterfDecimalUtil.str2BigDecimal(map.get("DEVIATION")));
//
//				    dto.setInputType(null);
//				    dto.setInputUser(null);
//				    dto.setAdapterId("0");
//				    dto.setStatus("E");
//				    dto.setCreateUser("interf");
//				    dto.setCreateDept(null);
//				    dto.setUpdateUser("interf");
//
//				    dto.setProfitCurrentYes(DisInterfDecimalUtil.str2BigDecimal(map.get("PROFIT_CURRENT_YES")));
//				    dto.setProfitCurrentNo(DisInterfDecimalUtil.str2BigDecimal(map.get("PROFIT_CURRENT_NO")));
//				    //dto.setProfitCurrent(new BigDecimal(map.get("PROFIT_CURRENT")));
//				    dto.setProfitTdyAble(DisInterfDecimalUtil.str2BigDecimal(map.get("PROFIT_TDY_ABLE")));
////				    dto.setProdProfitTdyAble(new BigDecimal(map.get("PROD_PROFIT_TDY_ABLE")));
////				    dto.setNetvalueTdyYield(new BigDecimal(map.get("NETVALUE_TDY_YIELD")));
////				    dto.setNetvalueTotalYield(new BigDecimal(map.get("NETVALUE_TOTAL_YIELD")));
////				    dto.setNetvalueTdyIncre(new BigDecimal(map.get("NETVALUE_TDY_INCRE")));
////				    dto.setNetvalueTotalIncre(new BigDecimal(map.get("NETVALUE_TOTAL_INCRE")));
//				    dto.setFloatBaseRate(DisInterfDecimalUtil.str2BigDecimal(map.get("FLOAT_BASE_RATE")));
////				    dto.setBounusValue(new BigDecimal(map.get("BOUNUS_VALUE")));
//				    dto.setRateBenchmark(DisInterfDecimalUtil.str2BigDecimal(map.get("RATE_BENCHMARK")));
////				    dto.setCashFormGrantTotal(new BigDecimal(map.get("CASH_FORM_GRANT_TOTAL")));
////				    dto.setReinvestFormGrantTotal(new BigDecimal(map.get("REINVEST_FORM_GRANT_TOTAL")));
////				    dto.setRedemptionTurnoutAmt(new BigDecimal(map.get("REDEMPTION_TURNOUT_AMT")));
////				    dto.setProfitThisyearChange(new BigDecimal(map.get("PROFIT_THISYEAR_CHANGE")));
//				    dto.setBenchmarkInfo(map.get("BENCHMARK_INFO"));
//				    dto.setCashFormGrantTdy(DisInterfDecimalUtil.str2BigDecimal(map.get("CASH_FORM_GRANT_TDY")));
//				    dto.setReinvestFormGrantTdy(DisInterfDecimalUtil.str2BigDecimal(map.get("REINVEST_FORM_GRANT_TDY")));
//				    dto.setRedemIntamtTdy(DisInterfDecimalUtil.str2BigDecimal(map.get("REDEM_INTAMT_TDY")));
//				    dto.setProfitChangeTdy(DisInterfDecimalUtil.str2BigDecimal(map.get("PROFIT_CHANGE_TDY")));
//				    //dto.setFloatTotalRate(new BigDecimal(map.get("FLOAT_TOTAL_RATE")));
//				}catch(Exception e){
//					logger.error("解析数据错误：",e.getMessage(),e);
//	           		return  "fail";
//				}
//			    try {
//		            if(StringUtil.isNotEmpty(map.get("CREATE_TIME"))){
//		                dto.setCreateTime(DateUtil.parseDate(map.get("CREATE_TIME"),new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
//		            }else{
//		                dto.setCreateTime(new Date());
//		            }
//		            if(StringUtil.isNotEmpty(map.get("UPDATE_TIME"))){
//		                dto.setUpdateTime(DateUtil.parseDate(map.get("UPDATE_TIME"),new String[] {DateUtil.DEFAULT_DATETIME_FORMAT_ODS}));
//		            }else{
//		                dto.setUpdateTime(new Date());
//		            }
//		        } catch (ParseException e) {
//		            logger.error("transMap2Dto方法转换CreateTime或UpdateTime异常");
//		            logger.error(e.getMessage(),e);
//		            return  "fail";
//		        }
//			    if (list.size() < reportInsertLongCount) {
//					list.add(dto);
//				} else if (list.size()  >= reportInsertLongCount) {
////					repProdIndexMapper.insert(list);
//					repProdIndexMapper.mergeRepProdIndex(list);
//					list.clear();
//					list.add(dto);
//				}
//			}// 如果循环结束，小于最大值
//			if (CollectionUtils.isNotEmpty(list)) {
////				repProdIndexMapper.insert(list);
//				repProdIndexMapper.mergeRepProdIndex(list);
//			}
		// modify by convertMapToEntity yangxianjiu 2022-01-19 end
		return "success";
	}
	@Override
	public String importDB(List<Map<String, String>> listData, InterfFile interfFile, String date) {
		 String rs = "success";
	        // 对数据list进行处理
	        if (!CollectionUtils.isEmpty(listData)) {
	            checkSaveData(listData, interfFile);
	            // 数据入库
	            ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(
	                InterfDefine.产品基础指标数据.getCode());
	            if (interfConfig == null) {
	                logger.info("CHB_INTERF_CONFIG表中无配置项！");
	                return "fail";
	            }
	            String saveFlag = interfConfig.getContentType();
	            rs = saveData(listData, saveFlag, interfFile.getDataSource());
	        }
	        return rs;
	    }
	
	   /**
     * 数据校验
     * 
     * @param dataList 入库的数据
     * @param interfFile 文件名
     * @see
     * @since 1.0
     */
    private void checkSaveData(List<Map<String, String>> dataList, InterfFile interfFile) {
        //接口类型
        String inerfType = InterfDefine.产品基础指标数据.getCode();
        //检查数据类型
        String dataType = InterfDefine.产品基础指标数据.getCode();
        //重复性检查的keys
        String[] checkEuplicatedKeys = {"PROD_ID","VALUE_DATE"};
        String filename = interfFile == null ? null : interfFile.getFilename();
        List<Map<String, String>>  listA = super.checkData(dataList, filename, inerfType, dataType,null, checkEuplicatedKeys);
//        dataList.addAll(listA);// listA为错误数据，在checkData时被remove，需要重新放回dataList
    }
	

}
