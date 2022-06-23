package com.joyintech.tams.valuation.common.interf.transdata.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CBondAnalysisCNBDMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CBondBenchmarkMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CBondEODPricesMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CMoneyMarketFincomeMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CbondanalysiscsiMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CbondanalysisshcMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CbondpricesMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.CbondpricesnetMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.ChinaMutualFundNAVMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.ChinaclosedfundeodpriceMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.ValPayhsrcsecoptionMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.dao.ShiborPricesMapper;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.CBondAnalysisCNBD;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.CBondBenchmark;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.CBondEODPrices;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.CMoneyMarketFincome;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.Cbondanalysiscsi;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.Cbondanalysisshc;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.Cbondprices;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.Cbondpricesnet;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.ChinaMutualFundNAV;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.Chinaclosedfundeodprice;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.ValPayhsrcsecoption;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.entity.ShiborPrices;
import com.joyintech.tams.valuation.common.interf.transdata.pabhq.utils.PabHqContans;
/**
 * 平安银行-行情数据
 * 
 * @author WUJIE
 * @version 1.0 2018年9月3日
 * @see HqTemplateCommonService
 * @since 1.0
 */
@Service
public class HqTemplateCommonService {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(HqTemplateCommonService.class);
    
	//1. 中国债券交易所债券行情
	@Autowired
	CBondEODPricesMapper cBondEODPricesMapper;
	
//	//2. 货币基金每天净值收益
//	@Autowired
//	CMoneyMarketDailyFIncomeMapper cMoneyMarketDailyFIncomeMapper;
	
	//3. 中国共同基金净值
	@Autowired
	ChinaMutualFundNAVMapper chinaMutualFundNAVMapper;
	
	//4. SHIRBO行情 
	@Autowired
	ShiborPricesMapper shiborPricesMapper;
	
	//5. 法定存贷款利率
	@Autowired
	CBondBenchmarkMapper cBondBenchmarkMapper;
	
	//6. 中债登估值
	@Autowired
	CBondAnalysisCNBDMapper cBondAnalysisCNBDMapper;
	
	//7. 货币基金净值收益
	@Autowired
	CMoneyMarketFincomeMapper cMoneyMarketFincomeMapper;
	
	/** add by dongjun on 20180921 start **/
	//8.中国债券行情-净价
    @Autowired
    CbondpricesnetMapper cbondpricesnetMapper;
    
    //9.中国债券行情-全价
    @Autowired
    CbondpricesMapper cbondpricesMapper;
    
    //10.中证估值
    @Autowired
    CbondanalysiscsiMapper cbondanalysiscsiMapper;
    
    //11.中国债券上海清算所债券估值
    @Autowired
    CbondanalysisshcMapper cbondanalysisshcMapper;
    /** add by dongjun on 20180921 start **/
    
    //12.中国封闭式基金日行情
    @Autowired
    ChinaclosedfundeodpriceMapper chinaclosedfundeodpriceMapper;
	/** add by dongjun on 20180921 end **/
    
    /** add by dongjun on 20180921 title:债券行权计划 start**/
    //13.债券行权计划
    @Autowired
    private ValPayhsrcsecoptionMapper payhsrcsecoptionMapper;
    /** add by dongjun on 20180921 title:债券行权计划 end**/
	
	/**
	 * 删除15天前的数据
	 */
	public int deleteFifthDayData(String tmpEName,Date cdate) {
		int result = 0;
		String cdate_str = new SimpleDateFormat("yyyyMMdd").format(DateUtil.addDay(cdate, -15));
		if(PabHqContans.TMP_E_NAME_中国债券交易所债券行情.equals(tmpEName)){
			result = cBondEODPricesMapper.deleteFifthDay(cdate_str);
		}
//		else if(PabHqContans.TMP_E_NAME_货币基金每天净值收益.equals(tmpEName)){
//			result = cMoneyMarketDailyFIncomeMapper.deleteFifthDay(cdate_str);
//		}
		else if(PabHqContans.TMP_E_NAME_中国共同基金净值.equals(tmpEName)){
			result = chinaMutualFundNAVMapper.deleteFifthDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_SHIRBO行情.equals(tmpEName)){
			result = shiborPricesMapper.deleteFifthDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_法定存贷款利率.equals(tmpEName)){
			result = cBondBenchmarkMapper.deleteFifthDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_中债登估值.equals(tmpEName)){
			result = cBondAnalysisCNBDMapper.deleteFifthDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_货币基金净值收益.equals(tmpEName)){
			result = cMoneyMarketFincomeMapper.deleteFifthDay(cdate_str);
		}
		/** add by dongjun on 20180921 start **/
		else if(PabHqContans.TMP_E_NAME_中国债券行情_净价.equals(tmpEName)){
            result = cbondpricesnetMapper.deleteFifthDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中国债券行情_全价.equals(tmpEName)){
            result = cbondpricesMapper.deleteFifthDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中证估值.equals(tmpEName)){
            result = cbondanalysiscsiMapper.deleteFifthDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中国债券上海清算所债券估值.equals(tmpEName)){
            result = cbondanalysisshcMapper.deleteFifthDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中国封闭式基金日行情.equals(tmpEName)){
            result = chinaclosedfundeodpriceMapper.deleteFifthDay(cdate_str);
        }
		/** add by dongjun on 20180921 end **/
		/** add by dongjun on 20181011 title:债券行权计划 start**/
        else if(PabHqContans.TMP_E_NAME_债券行权计划.equals(tmpEName)){
            /** add by chendijun start**/
            //result = payhsrcsecoptionMapper.deleteFifthDay(cdate_str);
            /**add by chendijun end**/ 
        }
		/** add by dongjun on 20181011 title:债券行权计划 end**/
		else{
//			System.out.println("模板"+tmpEName+"目前功能不支持...");
			logger.info("模板"+tmpEName+"目前功能不支持...");
		}
		return result;
	}
	
	/**
	 * 根据发布日期删除当天数据库临时表里面的数据
	 */
	public int deleteThisDayData(String tmpEName,Date cdate) {
		int result = 0;
		String cdate_str = new SimpleDateFormat("yyyyMMdd").format(cdate);
		if(PabHqContans.TMP_E_NAME_中国债券交易所债券行情.equals(tmpEName)){
			result = cBondEODPricesMapper.deleteThisDayDay(cdate_str);
		}
//		else if(PabHqContans.TMP_E_NAME_货币基金每天净值收益.equals(tmpEName)){
//			result = cMoneyMarketDailyFIncomeMapper.deleteThisDayDay(cdate_str);
//		}
		else if(PabHqContans.TMP_E_NAME_中国共同基金净值.equals(tmpEName)){
			result = chinaMutualFundNAVMapper.deleteThisDayDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_SHIRBO行情.equals(tmpEName)){
			result = shiborPricesMapper.deleteThisDayDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_法定存贷款利率.equals(tmpEName)){
			result = cBondBenchmarkMapper.deleteThisDayDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_中债登估值.equals(tmpEName)){
			result = cBondAnalysisCNBDMapper.deleteThisDayDay(cdate_str);
		}
		else if(PabHqContans.TMP_E_NAME_货币基金净值收益.equals(tmpEName)){
			result = cMoneyMarketFincomeMapper.deleteThisDayDay(cdate_str);
		}
		/** add by dongjun on 20180921 start **/
		else if(PabHqContans.TMP_E_NAME_中国债券行情_净价.equals(tmpEName)){
            result = cbondpricesnetMapper.deleteThisDayDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中国债券行情_全价.equals(tmpEName)){
            result = cbondpricesMapper.deleteThisDayDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中证估值.equals(tmpEName)){
            result = cbondanalysiscsiMapper.deleteThisDayDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中国债券上海清算所债券估值.equals(tmpEName)){
            result = cbondanalysisshcMapper.deleteThisDayDay(cdate_str);
        }
        else if(PabHqContans.TMP_E_NAME_中国封闭式基金日行情.equals(tmpEName)){
            result = chinaclosedfundeodpriceMapper.deleteThisDayDay(cdate_str);
        }
		/** add by dongjun on 20180921 end **/
		/** add by dongjun on 20181011 title:债券行权计划 start**/
        else if(PabHqContans.TMP_E_NAME_债券行权计划.equals(tmpEName)){
            result = payhsrcsecoptionMapper.deleteThisDayDay(cdate_str);
        }
		/** add by dongjun on 20181011 title:债券行权计划 end**/
		else{
//			System.out.println("模板"+tmpEName+"目前功能不支持...");
			logger.info("模板"+tmpEName+"目前功能不支持...");
		}
		return result;
	}
	
	/**
	 * 当天发布的数据插入到数据库临时表
	 * @param tmpEName
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int loadThisDayData(Map<String,Object> map){
		int result = 0;
		for (String key : map.keySet()) { 
			List list = null;
			List listInsert = null;
			if(PabHqContans.TMP_E_NAME_中国债券交易所债券行情.equals(key)){
				 list = (List<CBondEODPrices>)map.get(key);
				 listInsert = new ArrayList<CBondEODPrices>();
			}
//			else if(PabHqContans.TMP_E_NAME_货币基金每天净值收益.equals(key)){
//				list = (List<CMoneyMarketDailyFIncome>)map.get(key);
//				listInsert = new ArrayList<CMoneyMarketDailyFIncome>();
//			}
			else if(PabHqContans.TMP_E_NAME_中国共同基金净值.equals(key)){
				list = (List<ChinaMutualFundNAV>)map.get(key);
				listInsert = new ArrayList<ChinaMutualFundNAV>();
			}
			else if(PabHqContans.TMP_E_NAME_SHIRBO行情.equals(key)){
				list = (List<ShiborPrices>)map.get(key);
				listInsert = new ArrayList<ShiborPrices>();
			}
			else if(PabHqContans.TMP_E_NAME_法定存贷款利率.equals(key)){
				list = (List<CBondBenchmark>)map.get(key);
				listInsert = new ArrayList<CBondBenchmark>();
			}
			else if(PabHqContans.TMP_E_NAME_中债登估值.equals(key)){
				list = (List<CBondAnalysisCNBD>)map.get(key);
				listInsert = new ArrayList<CBondAnalysisCNBD>();
			}else if(PabHqContans.TMP_E_NAME_货币基金净值收益.equals(key)){
				list = (List<CMoneyMarketFincome>)map.get(key);
				listInsert = new ArrayList<CMoneyMarketFincome>();
			}
			/** add by dongjun on 20180921 start **/
			else if(PabHqContans.TMP_E_NAME_中国债券行情_净价.equals(key)){
                list = (List<Cbondpricesnet>)map.get(key);
                listInsert = new ArrayList<Cbondpricesnet>();
            }
            else if(PabHqContans.TMP_E_NAME_中国债券行情_全价.equals(key)){
                list = (List<Cbondprices>)map.get(key);
                listInsert = new ArrayList<Cbondprices>();
            }
            else if(PabHqContans.TMP_E_NAME_中证估值.equals(key)){
                list = (List<Cbondanalysiscsi>)map.get(key);
                listInsert = new ArrayList<Cbondanalysiscsi>();
            }
            else if(PabHqContans.TMP_E_NAME_中国债券上海清算所债券估值.equals(key)){
                list = (List<Cbondanalysisshc>)map.get(key);
                listInsert = new ArrayList<Cbondanalysisshc>();
            }
            else if(PabHqContans.TMP_E_NAME_中国封闭式基金日行情.equals(key)){
                list = (List<Chinaclosedfundeodprice>)map.get(key);
                listInsert = new ArrayList<Chinaclosedfundeodprice>();
            }
			/** add by dongjun on 20180921 end **/
			/** add by dongjun on 20181011 title:债券行权计划 start**/
            else if(PabHqContans.TMP_E_NAME_债券行权计划.equals(key)){
                list = (List<ValPayhsrcsecoption>)map.get(key);
                listInsert = new ArrayList<ValPayhsrcsecoption>();
            }
			/** add by dongjun on 20181011 title:债券行权计划 end**/
			else{
//				System.out.println("模板"+key+"目前功能不支持...");
				logger.info("模板"+key+"目前功能不支持...");
				continue;
			}
			// 入库
			if (CollectionUtils.isNotEmpty(list)) {
				for(int i =0; i<list.size();i++){
					if (listInsert.size() < 1000) {
						listInsert.add(list.get(i));
					}else if (listInsert.size() >= 1000) {
						insertBatch(listInsert,key);
						listInsert.clear();
                        listInsert.add(list.get(i));
					}
				}
				// 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	insertBatch(listInsert,key);
                }
                result = list.size();
			}
		}
		return result;
	}
	
	/**
	 * 插入数据共通方法
	 * @param listInsert
	 * @param tmpEName
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int insertBatch(List listInsert,String tmpEName){
		int result =0;
		if(PabHqContans.TMP_E_NAME_中国债券交易所债券行情.equals(tmpEName)){
			result = cBondEODPricesMapper.insertBatch(listInsert);
		}
//		else if(PabHqContans.TMP_E_NAME_货币基金每天净值收益.equals(tmpEName)){
//			result = cMoneyMarketDailyFIncomeMapper.insertBatch(listInsert);
//		}
		else if(PabHqContans.TMP_E_NAME_中国共同基金净值.equals(tmpEName)){
			result = chinaMutualFundNAVMapper.insertBatch(listInsert);
		}
		else if(PabHqContans.TMP_E_NAME_SHIRBO行情.equals(tmpEName)){
			result = shiborPricesMapper.insertBatch(listInsert);
		}
		else if(PabHqContans.TMP_E_NAME_法定存贷款利率.equals(tmpEName)){
			result = cBondBenchmarkMapper.insertBatch(listInsert);
		}
		else if(PabHqContans.TMP_E_NAME_中债登估值.equals(tmpEName)){
			result = cBondAnalysisCNBDMapper.insertBatch(listInsert);
		}
		else if(PabHqContans.TMP_E_NAME_货币基金净值收益.equals(tmpEName)){
			result = cMoneyMarketFincomeMapper.insertBatch(listInsert);
		}
		/** add by dongjun on 20180921 start **/
		else if(PabHqContans.TMP_E_NAME_中国债券行情_净价.equals(tmpEName)){
            result = cbondpricesnetMapper.insertBatch(listInsert);
            
        }
        else if(PabHqContans.TMP_E_NAME_中国债券行情_全价.equals(tmpEName)){
            result = cbondpricesMapper.insertBatch(listInsert);
        }
        else if(PabHqContans.TMP_E_NAME_中证估值.equals(tmpEName)){
            result = cbondanalysiscsiMapper.insertBatch(listInsert);
        }
        else if(PabHqContans.TMP_E_NAME_中国债券上海清算所债券估值.equals(tmpEName)){
            result = cbondanalysisshcMapper.insertBatch(listInsert);
        }
        else if(PabHqContans.TMP_E_NAME_中国封闭式基金日行情.equals(tmpEName)){
            result = chinaclosedfundeodpriceMapper.insertBatch(listInsert);
        }
		/** add by dongjun on 20180921 end **/
		/** add by dongjun on 20181011 title:债券行权计划 start**/
        else if(PabHqContans.TMP_E_NAME_债券行权计划.equals(tmpEName)){
            result = payhsrcsecoptionMapper.insertBatch(listInsert);
        }
		/** add by dongjun on 20181011 title:债券行权计划 end**/
		return result;
	}

	/**
     * 查询满足条件的临时表数据-共通
     */
	@SuppressWarnings("rawtypes")
	public List queryData(String tmpEName,String cdatestr){
    	if(PabHqContans.TMP_E_NAME_中国共同基金净值.equals(tmpEName)){
    		return chinaMutualFundNAVMapper.queryData(cdatestr);
    	}
//    	else if(PabHqContans.TMP_E_NAME_货币基金每天净值收益.equals(tmpEName)){
//    		return cMoneyMarketDailyFIncomeMapper.queryData(cdatestr);
//    	}
    	else if(PabHqContans.TMP_E_NAME_SHIRBO行情.equals(tmpEName)){
    		return shiborPricesMapper.queryData(cdatestr);
    	}
    	else if(PabHqContans.TMP_E_NAME_法定存贷款利率.equals(tmpEName)){
    		return cBondBenchmarkMapper.queryData(cdatestr);
    	}
    	else if(PabHqContans.TMP_E_NAME_中债登估值.equals(tmpEName)){
    		return cBondAnalysisCNBDMapper.queryData(cdatestr);
    	}
    	else if(PabHqContans.TMP_E_NAME_货币基金净值收益.equals(tmpEName)){
    		return cMoneyMarketFincomeMapper.queryData(cdatestr);
    	}
    	/** add by dongjun on 20180921 start **/
    	 else if(PabHqContans.TMP_E_NAME_中国债券行情_净价.equals(tmpEName)){
             return cbondpricesnetMapper.queryData(cdatestr);
         }
         else if(PabHqContans.TMP_E_NAME_中国债券行情_全价.equals(tmpEName)){
             return cbondpricesMapper.queryData(cdatestr);
         }
         else if(PabHqContans.TMP_E_NAME_中证估值.equals(tmpEName)){
             return cbondanalysiscsiMapper.queryData(cdatestr);
         }
         else if(PabHqContans.TMP_E_NAME_中国债券上海清算所债券估值.equals(tmpEName)){
             return cbondanalysisshcMapper.queryData(cdatestr);
         }
         else if(PabHqContans.TMP_E_NAME_中国债券交易所债券行情.equals(tmpEName)){
             return cBondEODPricesMapper.queryData(cdatestr);
         }       
         else if(PabHqContans.TMP_E_NAME_中国封闭式基金日行情.equals(tmpEName)){
             return chinaclosedfundeodpriceMapper.queryData(cdatestr);
         }
    	/** add by dongjun on 20180921 end **/
    	/** add by dongjun on 20181011 title:债券行权计划 start**/
         else if(PabHqContans.TMP_E_NAME_债券行权计划.equals(tmpEName)){             
             return payhsrcsecoptionMapper.queryData(cdatestr);
         }
    	/** add by dongjun on 20181011 title:债券行权计划 end**/
    	
    	return null;
    }
	
	public Class<?> getDBEntityClass(String tmpEName){
		try {
			return Class.forName(PabHqContans.ENTITY_PATH + tmpEName);
		} catch (ClassNotFoundException e) {
		    logger.error(e.getMessage(), e);
		}
		return null;
	}
}
