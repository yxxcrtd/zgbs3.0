package com.joyintech.tams.valuation.common.interf.base.util;

import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.TempCheckInfo;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.utils.ValStringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class InterfHelper {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(InterfHelper.class);

    public static String getMd5CashId_(String ... cashId){
    	String md5CashId = "";
    	if(cashId !=null && cashId.length>0){
    		for(int i=0;i<cashId.length;i++){
    			md5CashId += FinalString.SPLIT+cashId[i];
    		}
    	}
        md5CashId = MD5Util.string2MD5(md5CashId);
    	return md5CashId;
    }

    public static void main(String args[]){
        //dto.getFinprodId(), DictCodeListExt1.金融产品类型_债券.getValue(), branch, cashType2
        System.out.println(        getMd5CashId_("F01_010011.IB",DictCodeListExt1.金融产品类型_债券.getValue(),"C02"));
    }

    @SuppressWarnings("unchecked")
	public static String getDictColdeList_ItemName(String code,String itemcode){
    	String itemName = null;
    	Map<String, DataDict> dictMap = CacheUtil.getAllDictCode();
    	if(dictMap ==null){
    		return null;
    	}
    	DataDict dictCode = dictMap.get(code);
    	if(dictCode==null){
    		return null;
    	}
    	List<DataDictItem> items = (List<DataDictItem>)dictCode.getItems();
    	for(DataDictItem obj :items){
    		if(obj.getItemCode().equals(itemcode)){
    			itemName = obj.getItemValue();
    			break;
    		}
    	}
    	return itemName;
    }
    
    /**
     * 指定数据字典CODE，判断itemcode是否对应存在
     * add by wujie on 20180926
     */
    @SuppressWarnings("unchecked")
	public static int isValiDateDict(String code,String itemcode){
    	int result = 0;
    	Map<String, DataDict> dictMap = CacheUtil.getAllDictCode();
        if (dictMap == null) {
        	return -1;
        }
        DataDict dictCode = dictMap.get(code);
    	if(dictCode==null){
    		return -1;
    	}
    	List<DataDictItem> items = (List<DataDictItem>)dictCode.getItems();
    	for(DataDictItem obj :items){
    		if(obj.getItemCode().equals(itemcode)){
    			result = 1;
    			break;
    		}
    	}
    	return result;
    }
    
    /**
     * 获取字符串数值的有效小数位数
     */
    public static int getDecimal(String _v){
    	int decimal = 0;
    	int beginIndex = _v.indexOf(".");
    	int endIndex = _v.length();
    	if(beginIndex >= 0 && beginIndex<endIndex){
    		String _v_decimal = _v.substring(beginIndex, endIndex);
    		decimal = _v_decimal.length()-1;
    	}
    	return decimal;
    }
    
    /**
     * 生成UUID
     * 
     * @param
     */
    public static String getUUid() {
        UUID uuid = UUID.randomUUID();
        String id = (uuid.toString()).replace("-", "").substring(0, 32);
        return id;
    }
    
    
    /**
     * 设置Object的Key值
     */
    public static void setValInterflogKey(Object result,String key){
    	Class<?> clazz= result.getClass().getSuperclass();
    	Field personNameField = null;
		try {
            personNameField = clazz.getDeclaredField("workflowId");
            personNameField.setAccessible(true);
            personNameField.set(result, key);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
    
    public static String getValInterflogKey(Object object){  
    	String keylog = null;
        Field field = null ;  
        Class<?> clazz = object.getClass().getSuperclass();  
        try {  
            field = clazz.getDeclaredField("workflowId") ;
            field.setAccessible(true);
            keylog = (String)field.get(object) ;  
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }   
        return keylog;  
    }  
    
    public static String getObjectFieldValue(Object object,String colname){ 
    	String colvalue = null;
    	Field field = null;
    	try {
    		field = object.getClass().getDeclaredField(colname);
    		if(field != null){
    			field.setAccessible(true);
    			Class<?> t = field.getType();
    			if (t.toString().contains("java.util.Date")) {
    				colvalue = DateUtil.format((Date)field.get(object),"yyyyMMdd");
    			}
    			else if(t.toString().contains("java.lang.String")){
    				colvalue = (String)field.get(object);
    			}
    		}
    	}catch (Exception e) {
            logger.error(e.getMessage(),e);
        }  
		return colvalue;
    }
    
    /**
     * 获取key值
     * @param valInterflog
     * @return
     */
    public static String getInterfLogKey(ValInterflog checkLog){
    	String interfLogKey = null;
    	//表key:TMP_NAME, FILE_NAME, EX_DATE, NUM
    	interfLogKey = ValStringUtil.getNotNullStr(checkLog.getTmpName())
    			+ValStringUtil.getNotNullStr(checkLog.getFileName())
    			+ValStringUtil.getNotNullStr(DateUtil.format(checkLog.getExDate()))
    			+checkLog.getNum();
    	return interfLogKey;
    }
    
    /**
     * 日志信息 List 转 Map
     * valInterflog 
     */
    public static Map<String,ValInterflog> getMapValInterflog(List<ValInterflog> list){
    	Map<String,ValInterflog> map = new HashMap<String,ValInterflog>();
    	for(ValInterflog log :list){
    		map.put(getInterfLogKey(log), log);
    	}
    	return map;
    }
    
    /**
     * 日志信息Map 转 List
     */
    public static List<ValInterflog> getListValInterflog(Map<String,ValInterflog> map){
    	List<ValInterflog> list = new ArrayList<ValInterflog>();
    	for(String key :map.keySet()){
    		list.add(map.get(key));
    	}
    	return list;
    }
    
    
    public static Map<String,TempCheckInfo> checkFinprodDealMap(){
    	Map<String,TempCheckInfo> prdNameMap = new HashMap<String,TempCheckInfo>();
    	
    	
    	TempCheckInfo temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.净值型产品存续期交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
		prdNameMap.put(TempConstant.净值型产品存续期交易表, temp);
		
		temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.净值型产品分红除权表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.净值型产品分红除权表, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.净值型产品分红交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.净值型产品分红交易表, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.净值型产品费用交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.净值型产品费用交易表, temp);
        
        
    	
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.货币型产品存续期交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.货币型产品存续期交易表, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.货币型产品分红交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.货币型产品分红交易表, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.货币型产品费用交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.货币型产品费用交易表,temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.预期型产品存续期交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.预期型产品存续期交易表, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.预期型产品费用交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("PRD");
        temp.setColCcyName("ccy");
        temp.setColprdTypeName("finprodType");
        temp.setColprdVdateName("prodVdate");
        prdNameMap.put(TempConstant.预期型产品费用交易表, temp);
    	
        String finprodTypes = DictCodeListExt1.金融产品类型_拆入.getValue()
        		+","+DictCodeListExt1.金融产品类型_拆出.getValue()
        		+","+DictCodeListExt1.金融产品类型_同业借款.getValue();
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.拆借交易);
        temp.setColprdName("prodId");
        temp.setColCcyName("ccy");
        temp.setInvestType("INVEST");
        temp.setAssetId("pTradeId");
        temp.setFinprodTypes(finprodTypes);
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        
        prdNameMap.put(TempConstant.拆借交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.拆借收息);
        temp.setColprdName("prodId");
        temp.setColCcyName("ccy");
        temp.setInvestType("INVEST");
        temp.setAssetId("dlId");
        temp.setFinprodTypes(finprodTypes);
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        
        prdNameMap.put(TempConstant.拆借收息,temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.拆借应收款项转让交易);
        temp.setColprdName("prodId");
        temp.setColCcyName("ccy");
        temp.setInvestType("INVEST");
        temp.setAssetId("dlId");
        temp.setFinprodTypes(finprodTypes);
        temp.setColChlId("channelId");
        
        prdNameMap.put(TempConstant.拆借应收款项转让交易,temp);
    	
        
        finprodTypes = DictCodeListExt1.金融产品类型_债券质押式正回购.getValue()
        		+","+DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue()
        		+","+DictCodeListExt1.金融产品类型_债券买断式正回购.getValue()
        		+","+DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue();
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.回购交易);
        temp.setColprdName("prodId");
        temp.setColCcyName("ccy");
        temp.setInvestType("INVEST");
        temp.setAssetId("pTradeId");
        temp.setFinprodTypes(finprodTypes);
        temp.setColChlId("chlId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.回购交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.买断式回购派息交易);
        temp.setColprdName("prodId");
        temp.setColCcyName("ccy");
        temp.setInvestType("INVEST");
        temp.setAssetId("assetId");
        temp.setFinprodTypes(finprodTypes);
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.买断式回购派息交易, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.债券买卖交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("chlId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.债券买卖交易, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.债券存续期交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.债券存续期交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.债券应收款项转让交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.债券应收款项转让交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.逾期交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.逾期交易, temp);
    	
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.存款类资产存款交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.存款类资产存款交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.存款类资产存款存续期表);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.存款类资产存款存续期表, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.存款类资产应收款项转让交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.存款类资产应收款项转让交易, temp);
    	
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.申购申请交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.申购申请交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.申购确认交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.申购确认交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.货币基金赎回确认交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.货币基金赎回确认交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.净值型基金赎回确认交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.净值型基金赎回确认交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.赎回到账交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.赎回到账交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.现金分红交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.现金分红交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.红利再投交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.红利再投交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.基金转换交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.基金转换交易, temp);
    	
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产利率型买卖交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("chlId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.计划类资产利率型买卖交易表, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产利率型存续期交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.计划类资产利率型存续期交易表, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产利率型应收款项转让交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.计划类资产利率型应收款项转让交易表, temp);
    	
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.增值税交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.增值税交易, temp);
    	
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.活期存款收息交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.活期存款收息交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.活期存款转仓交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.活期存款转仓交易, temp);
        
        
        
        finprodTypes = DictCodeListExt1.金融产品类型_债券借贷融入.getValue()
        		+","+DictCodeListExt1.金融产品类型_债券借贷融出.getValue();
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.债券借贷交易);
        temp.setColprdName("prodId");
        temp.setColCcyName("ccy");
        temp.setInvestType("INVEST");
        temp.setAssetId("finprodId");
        temp.setFinprodTypes(finprodTypes);
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.债券借贷交易, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.债券借贷标的券存续期交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.债券借贷标的券存续期交易, temp);
        
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.债券借贷标的券卖出与购回);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("chlId");//modify by dongjun on 20190313 remark:通道相关
        prdNameMap.put(TempConstant.债券借贷标的券卖出与购回, temp);
        
        /** modify by dongjun on 20190313 remark:通道相关 start **/
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.通道基础信息);
        temp.setInvestType("BASICDATA");
        /** modify by huangjian on 20190316 remark:通道相关 start **/
        temp.setColChlId("channelId");
        temp.setScheId("isCompoundInt");
        /** modify by huangjian on 20190320 remark:通道相关 start **/
        temp.setColCcyName("ccy");
        temp.setColprdName("channelId");
        
        
        prdNameMap.put(TempConstant.通道基础信息, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.理财产品借通道投资交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST_TD");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.理财产品借通道投资交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.通道产品费用交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST_TD");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.通道产品费用交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.费用交易表);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.费用交易表, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.结算指令);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        temp.setAcctNo("myAcctNo");
        prdNameMap.put(TempConstant.结算指令, temp);
        /** modify by dongjun on 20190313 remark:通道相关 end **/
        /**add by wujie on 20190311 start remark:增加计划类货币型资产交易的业务check**/
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产货币型申购申请交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.计划类资产货币型申购申请交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产货币型申购确认交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.计划类资产货币型申购确认交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产货币型赎回确认交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.计划类资产货币型赎回确认交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产货币型赎回到账交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.计划类资产货币型赎回到账交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产货币型存续期交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.计划类资产货币型存续期交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.计划类资产货币型买入卖出交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.计划类资产货币型买入卖出交易, temp);
        
        /**add by wujie on 20190311 end remark:增加计划类货币型资产交易的业务check**/
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.产品回补交易);
        temp.setColprdName("prodId");
        temp.setInvestType("QT");
        temp.setColCcyName("ccy");
        prdNameMap.put(TempConstant.产品回补交易, temp);
        
        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.场外期权交易);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.场外期权交易, temp);

        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.利率互换标的信息);
        prdNameMap.put(TempConstant.利率互换标的信息, temp);

        temp = new TempCheckInfo();
        temp.setDtoEname(TempConstant.利率互换交易信息);
        temp.setColprdName("prodId");
        temp.setInvestType("INVEST");
        temp.setColCcyName("ccy");
        temp.setColChlId("channelId");
        prdNameMap.put(TempConstant.利率互换交易信息, temp);
             
        return prdNameMap;
    }
}
