package com.joyintech.tams.valuation.common.interf.base.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.Prdbokstl;
import com.joyintech.tams.common.base.entity.TempCheckInfo;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.dto.TargetFinProduct;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateColMatchDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;


/**
 * @author maodongjin
 *
 */
public class SAXParserHandler extends DefaultHandler {
	
//	Logger logger = Logger.getLogger(DefaultHandler.class);

	private static Logger logger = LoggerFactory.getLogger(SAXParserHandler.class);
	
	private int bookIndex = 0;
    
	private StringBuffer value=new StringBuffer();
    
	private List<Object> resultList;
    
	private List<ValInterflog> logResult;
    
	private Object result;
	
	private ValInterflog checkLog;
    
	private Class<?> T = null;
	
	private Field[] fields;
	
	private String rootElement;
	
	private Map<String,TemplateOrderInfoReaderDTO> check;
	
	private Map<String,String> match;

	private StringBuffer info;
	
	private StringBuffer checkError;
	
	private TemplateOrderInfoReaderResult acctEntity;
	
	/**modify by wujie on 20181008 start**/
	
	/**
	 * 接口模板check字段(产品ID)
	 */
	private TempCheckInfo tmpCheckInfo;
	
	/**
	 * 接口模板是否进行资产check
	 * add by wujie on 20181229
	 */
	private boolean isCheckAsset;
	
	/**
	 * 产品、账套、资金账户 列表
	 */
	private Map<String,Prdbokstl> mapPrdbokstl = new HashMap<String,Prdbokstl>();
	/**modify by wujie on 20181008 end**/
	
	/**
	 * 债券借贷、债券回购、拆借标的
	 * add by wujie on 20181229
	 */
	private Map<String,TargetFinProduct> mapTargetFinProduct = new HashMap<String,TargetFinProduct>();
	
	/**
	 * 数据过滤器
	 */
	private Map<String, List<String>> filter;
	
	/**
	 * 记录被校验元素，用于和校验规则进行对比
	 * 	如果校验规则有N个，被校验元素只有小于N个，则说明模版中缺少字段
	 */
	private List<String> cols = new ArrayList<String>();
	
    /**
     * @param _c
     * 	目标DTO的全路径
     * @param _check
     * @param acctEntity 
     * @throws ClassNotFoundException 
     */
    public boolean init(String _c, List<TemplateOrderInfoReaderDTO> _check, 
    		TemplateOrderInfoReaderResult _acctEntity, String _rootElement, 
    		Map<String, List<String>> _filter, Map<String,TempCheckInfo> prdNameMap) throws ClassNotFoundException{
		if(_check==null||_check.isEmpty()){
			return false;
		}
    	resultList = new ArrayList<Object>();
		info = new StringBuffer();
		checkError = new StringBuffer();
		T = Class.forName(_c);
		fields = T.getDeclaredFields();
		acctEntity = _acctEntity;
		logResult = new ArrayList<ValInterflog>();
		check = buildCheckIndex(_check);
		rootElement = _rootElement;
		filter = _filter;
		match = buildMatch(_acctEntity.getListTemplateColMatchDTO());
		
		tmpCheckInfo = prdNameMap.get(_acctEntity.getDtoName());
		/**
		 * add by wujie on 20181229 start remark:资产check
		 */
		isCheckAsset = false;
		if(tmpCheckInfo != null && tmpCheckInfo.getAssetId()!= null){
			isCheckAsset = true;
			this.setMapTargetFinProduct(acctEntity.getMapTargetFinProduct());
		}
		/**add by wujie on 20181229 end**/
		
		
		return true;
    }
    
	/**
     * 构建校验信息字典
     * @param _check
     * @return
     */
    private Map<String, TemplateOrderInfoReaderDTO> buildCheckIndex(List<TemplateOrderInfoReaderDTO> _check) {
    	Map<String, TemplateOrderInfoReaderDTO> checkIndex = new HashMap<String, TemplateOrderInfoReaderDTO>();
    	if(_check!=null&&!_check.isEmpty()){
    		for(TemplateOrderInfoReaderDTO _c : _check){
    			checkIndex.put(_c.getColEName(), _c);
    		}
    	}
		return checkIndex;
	}
    
    /**
     * 
     * 构建匹配数据字典
     * @param _match
     * @return
     */
    private Map<String, String> buildMatch(List<TemplateColMatchDTO> _match) {
        Map<String, String> map = null;
        if (_match != null && !_match.isEmpty()) {
            map = new HashMap<String, String>();
            for (TemplateColMatchDTO _m : _match) {
                map.put(_m.getTmpColEName(), _m.getDtoColEName());
            }
        }
        return map;
    }


	/*
     * 用来遍历xml文件开始的标签
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);
        
        value = new StringBuffer();
        
        if (qName.equals(rootElement)) {
            bookIndex++;
            try {
				result = T.newInstance();
				checkLog = new ValInterflog();
				info = new StringBuffer();
				checkError = new StringBuffer();
				cols = new ArrayList<String>();
			} catch (InstantiationException e) {
				logger.error("",e);
			} catch (IllegalAccessException e) {
				logger.error("",e);
			}
        }else if (!qName.equals(rootElement)) {
//            logger.debug("节点名是 ： " + qName);
        }
    }

    /*
     * 用来遍历xml文件开始的标签
     */
    @Override
    public void endElement(String uri, String localName, String qName) 
            throws SAXException {
        super.endElement(uri, localName, qName);
        
        
        if (qName.equals(rootElement)) {
        	
        	checkLog.setTmpName(acctEntity.getTmpEName());// 模板名称
        	checkLog.setFileName(acctEntity.getTmpCName());// 文件名称
        	checkLog.setExDate(new Date());// 日期
        	checkLog.setParentSeqno(acctEntity.getParentSeqno());// 主副日志表连接键
        	checkLog.setDetail(info.toString());
        	checkLog.setNum((long) bookIndex);
        	
        	if(DictCodeList.是否_否.getValue().equals(checkLog.getIsCheck())){
        		//校验不通过的数据就丢弃掉
        	}else{
        		if(cols.size()<check.size()){
        			//模版中缺少字段，全部数据丢弃
        			StringBuffer lostCol = new StringBuffer();
					for(Entry<String,TemplateOrderInfoReaderDTO> entry : check.entrySet()){
						if(!cols.contains(entry.getValue().getColEName())&&entry.getValue().getIsExist().equals("1")){
							lostCol.append(entry.getValue().getColEName()+";");
						}else{
							continue;
						}
					}
					//增加对IS_EXIST逻辑启用
					if(StringUtils.isNotEmpty(lostCol)){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(acctEntity.getTmpCName() +" 接口文件中缺少字段："+lostCol+"，该文件所有数据将被丢弃，请联系管理员立即处理.");
						logger.error(acctEntity.getTmpCName() +" 接口文件中缺少字段："+lostCol+"，该文件所有数据将被丢弃，请联系管理员立即处理.");
					}else{
						/**add by wujie on 20190102 start**/
						InterfHelper.setValInterflogKey(result, InterfHelper.getInterfLogKey(checkLog));
						/**add by wujie on 20190102 end**/

						resultList.add(result);
						checkLog.setIsCheck(DictCodeList.是否_是.getValue());
					}
        		}else{
        			
        			/**add by wujie on 20190102 start**/
        			InterfHelper.setValInterflogKey(result, InterfHelper.getInterfLogKey(checkLog));
        			/**add by wujie on 20190102 end**/
        			
            		resultList.add(result);
            		checkLog.setIsCheck(DictCodeList.是否_是.getValue());
        		}
        	}
        	checkLog.setRemark(checkError.toString());
        	logResult.add(checkLog);
        }else{
        	for(Field f : fields){
        	    String q_name = qName;
                if (match != null) {
                    q_name = match.get(qName);
                }
        		if(q_name != null && q_name.equals(f.getName())){
        			try {
        				cols.add(qName);
        				info.append(qName+"-"+value.toString()+";");
        				if (filter(f.getName(), value.toString())) {
        				    setValue(f,result,value.toString(),qName);
        				} else {
        				    checkLog.setIsCheck(DictCodeList.是否_否.getValue());
        				    checkError.append(f.getName()+"："+"数据被过滤器过滤;");
        				}
        			} catch (IllegalArgumentException e) {
        				logger.error("",e);
        			} catch (IllegalAccessException e) {
        				logger.error("",e);
        			}
        		}
        	}
        }
    }

    private void setValue(Field _f, Object _obj, String _v, String _col) throws IllegalArgumentException, IllegalAccessException {
    	_f.setAccessible(true);
		Class<?> t = _f.getType();
		if (t.toString().contains("java.util.Date")) {
			Date _d = checkDateValue(_v,_col);
			_f.set(_obj, _d);
		}else if (t.toString().contains("java.math.BigDecimal")) {
			BigDecimal _b = checkBigValue(_v,_col);
			_f.set(_obj, _b);
		}else if (t.toString().contains("java.lang.Integer")) {
			Integer _i = checkIntValue(_v,_col);
			_f.set(_obj, _i);
		}else if (t.toString().contains("java.lang.Long")) {
			Long _l = checkLonValue(_v,_col);
			_f.set(_obj, _l);
		}else if(t.toString().contains("java.lang.String")){
			_v = checkStrValue(_v,_col);
			_f.set(_obj, _v);
		}else{
			logger.warn("字段类型无法识别，请联系系统管理员.");
		}
	}


	/**
	 * String类型数据校验
	 * @param _v
	 * @param colName
	 * @return
	 */
	private String checkStrValue(String _v, String colName) {
		if(check!=null&&check.containsKey(colName)){
			TemplateOrderInfoReaderDTO _c = check.get(colName);
			if(!_c.getIsExist().equals("1")){
				checkLog.setIsCheck(DictCodeList.是否_是.getValue());
			}
			//非空检查
			if(DictCodeList.是否_是.getValue().equals(_c.getIsNull())){
				if(_v==null||_v.isEmpty()){
					checkLog.setIsCheck(DictCodeList.是否_否.getValue());
					checkError.append(colName+"："+"字段不可为空;");
					return null;
				}
			}
			//长度检查
			if(_v!=null&&!_v.isEmpty()){
				BigDecimal _len = new BigDecimal(_v.length()+1);
				if(_len.compareTo(_c.getMaxLength())>0){
					if(_v==null||_v.isEmpty()){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength());
						return null;
					}
				}
			}
			/**add by wujie on 20180926 start remark:数据字典验证**/
			if(_v!=null && !_v.isEmpty() && _v.trim().length()!=0){
				if(_c.getDictCode()!=null && !_c.getDictCode().isEmpty()){
					int result = InterfHelper.isValiDateDict(_c.getDictCode(),_v);
					if(result == 0){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段传值"+_v+"不在数据字典["+_c.getDictCode()+"]设置范围内.");
						return null;
					} 
					else if(result == -1){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段对应的数据字典["+_c.getDictCode()+"]不存在，需要联系管理员进行配置.");
						return null;
					}
				}
			}
			/**add by wujie on 20180926 end**/		
			/**
			 * 资产check
			 * add by wujie on 20181229 start
			 */
			if(isCheckAsset 
					&& colName.equals(tmpCheckInfo.getAssetId())){
				boolean ischeck = false;
				if(this.getMapTargetFinProduct()!=null){
					String[] finprodTypes = tmpCheckInfo.getFinprodTypes().split(",");
					for(String finprodType :finprodTypes){
						String key = _v+finprodType;
						TargetFinProduct targetFinProduct = this.getMapTargetFinProduct().get(key);
						if(targetFinProduct !=null){
							ischeck = true;
							break;
						}
					}
				}
				if(!ischeck){
					checkLog.setIsCheck(DictCodeList.是否_否.getValue());
                    checkError.append(colName+"：资产["+_v+"]不存在，需要联系管理员进行资产信息接入确认.");
                    return null;
				}
			}
			/**add by wujie on 20181229 end**/
		}else{
			checkLog.setIsCheck(DictCodeList.是否_否.getValue());
			checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			return null;
		}
		return _v;
	}


	/**
	 * Long类型数据校验
	 * @param _v
	 * @param colName
	 * @return
	 */
	private Long checkLonValue(String _v, String colName) {
		Long l = null;
		if(check.containsKey(colName)){
			TemplateOrderInfoReaderDTO _c = check.get(colName);
			if(!_c.getIsExist().equals("1")){
				checkLog.setIsCheck(DictCodeList.是否_是.getValue());
			}
			//非空检查
			if(DictCodeList.是否_是.getValue().equals(_c.getIsNull())){
				if(_v==null||_v.isEmpty()){
					checkLog.setIsCheck(DictCodeList.是否_否.getValue());
					checkError.append(colName+"："+"字段不可为空;");
					logger.warn(colName+"："+"字段不可为空;");
					return null;
				}
			}
			//长度检查
			if(_v!=null&&!_v.isEmpty()){
				BigDecimal _len = new BigDecimal(_v.length()+1);
				if(_len.compareTo(_c.getMaxLength())>0){
					if(_v==null||_v.isEmpty()){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						return null;
					}
				}
			}
			//格式检查
			try{
				if(_v==null||_v.trim().isEmpty()){
					l = null;
				}else{
					l = new Long(_v);
				}
			}catch (Exception e) {
				checkLog.setIsCheck(DictCodeList.是否_否.getValue());
				checkError.append(colName+"：Long数值错误——"+_v+";");
				logger.warn(colName+"：Long数值错误——"+_v+";");
			}
		}else{
			checkLog.setIsCheck(DictCodeList.是否_否.getValue());
			checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			return null;
		}
		return l;
	}


	/**
	 * Integer数据校验
	 * @param _v
	 * @param colName
	 * @return
	 */
	private Integer checkIntValue(String _v, String colName) {
		Integer l = null;
		if(check.containsKey(colName)){
			TemplateOrderInfoReaderDTO _c = check.get(colName);
			if(!_c.getIsExist().equals("1")){
				checkLog.setIsCheck(DictCodeList.是否_是.getValue());
			}
			//非空检查
			if(DictCodeList.是否_是.getValue().equals(_c.getIsNull())){
				if(_v==null||_v.isEmpty()){
					checkLog.setIsCheck(DictCodeList.是否_否.getValue());
					checkError.append(colName+"："+"字段不可为空;");
					logger.warn(colName+"："+"字段不可为空;");
					return null;
				}
			}
			//长度检查
			if(_v!=null&&!_v.isEmpty()){
				BigDecimal _len = new BigDecimal(_v.length()+1);
				if(_len.compareTo(_c.getMaxLength())>0){
					if(_v==null||_v.isEmpty()){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						return null;
					}
				}
			}
			//格式检查
			try{
				if(_v==null||_v.trim().isEmpty()){
					l = null;
				}else{
					l = new Integer(_v);
				}
			}catch (Exception e) {
				checkLog.setIsCheck(DictCodeList.是否_否.getValue());
				checkError.append(colName+"：Integer数值错误——"+_v+";");
				logger.warn(colName+"：Integer数值错误——"+_v+";");
			}
		}else{
			checkLog.setIsCheck(DictCodeList.是否_否.getValue());
			checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			return null;
		}
		return l;
	}


	/**
	 * BigDecimal数据校验
	 * @param _v
	 * @param colName
	 * @return
	 */
	private BigDecimal checkBigValue(String _v, String colName) {
		BigDecimal l = null;
		if(check.containsKey(colName)){
			TemplateOrderInfoReaderDTO _c = check.get(colName);
			if(!_c.getIsExist().equals("1")){
				checkLog.setIsCheck(DictCodeList.是否_是.getValue());
			}
			//非空检查
			if(DictCodeList.是否_是.getValue().equals(_c.getIsNull())){
				if(_v==null||_v.isEmpty()){
					checkLog.setIsCheck(DictCodeList.是否_否.getValue());
					checkError.append(colName+"："+"字段不可为空;");
					logger.warn(colName+"："+"字段不可为空;");
					return null;
				}
			}
			//长度检查
			if(_v!=null&&!_v.isEmpty()){
				BigDecimal _len = new BigDecimal(_v.length()+1);
				if(_len.compareTo(_c.getMaxLength())>0){
					if(_v==null||_v.isEmpty()){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						return null;
					}
				}
			}
			//格式检查
			try{
				if(_v==null||_v.trim().isEmpty()){
					l = null;
				}else{
					l = new BigDecimal(_v);
					l = l.setScale(InterfHelper.getDecimal(_v), BigDecimal.ROUND_HALF_UP);
				}
			}catch (Exception e) {
				checkLog.setIsCheck(DictCodeList.是否_否.getValue());
				checkError.append(colName+"：BigDecimal数值错误——"+_v+";");
				logger.warn(colName+"：BigDecimal数值错误——"+_v+";");
			}
		}else{
			checkLog.setIsCheck(DictCodeList.是否_否.getValue());
			checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			return null;
		}
		return l;
	}


	/**
	 * Date类型数据校验
	 * @param _v
	 * @param colName
	 * @return
	 */
	private Date checkDateValue(String _v, String colName) {
		Date l = null;
		if(check.containsKey(colName)){
			TemplateOrderInfoReaderDTO _c = check.get(colName);
			if(!_c.getIsExist().equals("1")){
				checkLog.setIsCheck(DictCodeList.是否_是.getValue());
			}
			//非空检查
			if(DictCodeList.是否_是.getValue().equals(_c.getIsNull())){
				if(_v==null||_v.isEmpty()){
					checkLog.setIsCheck(DictCodeList.是否_否.getValue());
					checkError.append(colName+"："+"字段不可为空;");
					logger.warn(colName+"："+"字段不可为空;");
					return null;
				}
			}
			//长度检查
			if(_v!=null&&!_v.isEmpty()){
				BigDecimal _len = new BigDecimal(_v.length()+1);
				if(_len.compareTo(_c.getMaxLength())>0){
					if(_v==null||_v.isEmpty()){
						checkLog.setIsCheck(DictCodeList.是否_否.getValue());
						checkError.append(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						logger.warn(colName+"：字段长度为"+_len+"超过指定长度"+_c.getMaxLength()+";");
						return null;
					}
				}
			}
			//格式检查
			try{
				if(_v==null||_v.trim().isEmpty()){
					l = null;
				}else{
					l = DateUtil.parse(_v);
				}
			}catch (Exception e) {
				checkLog.setIsCheck(DictCodeList.是否_否.getValue());
				checkError.append(colName+"：Date数值错误——"+_v+";");
				logger.warn(colName+"：Date数值错误——"+_v+";");
			}
		}else{
			checkLog.setIsCheck(DictCodeList.是否_否.getValue());
			checkError.append(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			logger.warn(colName+"：字段不存在校验规则，需要联系管理员进行配置.");
			return null;
		}
		return l;
	}
	
	/**
	 * 
	 * 过滤器。过滤不在范围的数据
	 * 
	 * @param _col
	 * @param _v
	 */
	private boolean filter(String _col, String _v) {
	    if (filter == null) {
	        return true;
	    }
	    if (!filter.containsKey(_col)) {
	        return true;
	    }
	    if (filter.get(_col).contains(_v)) {
	        return true;
	    }
        return false;
	}


	// 用来标识解析开始
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        logger.debug("SAX解析开始");
    }

    // 用来标识解析结束
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        logger.debug("SAX解析结束,总共解析数据为："+bookIndex);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        value = value.append(new String(ch, start, length));
        if (!value.toString().trim().equals("")) {
//            logger.debug("----节点值为: " + value);
        }
    }


	public List<ValInterflog> getLogResult() {
		return logResult;
	}


	public void setLogResult(List<ValInterflog> logResult) {
		this.logResult = logResult;
	}

  
    public List<Object> getResultList() {
		return resultList;
	}


	public void setResultList(List<Object> resultList) {
		this.resultList = resultList;
	}

	/**
	 * @return the mapPrdbokstl
	 */
	public Map<String, Prdbokstl> getMapPrdbokstl() {
		return mapPrdbokstl;
	}

	/**
	 * @param mapPrdbokstl the mapPrdbokstl to set
	 */
	public void setMapPrdbokstl(Map<String, Prdbokstl> mapPrdbokstl) {
		this.mapPrdbokstl = mapPrdbokstl;
	}

	/**
	 * @return the mapTargetFinProduct
	 */
	public Map<String, TargetFinProduct> getMapTargetFinProduct() {
		return mapTargetFinProduct;
	}

	/**
	 * @param mapTargetFinProduct the mapTargetFinProduct to set
	 */
	public void setMapTargetFinProduct(Map<String, TargetFinProduct> mapTargetFinProduct) {
		this.mapTargetFinProduct = mapTargetFinProduct;
	}
	
}