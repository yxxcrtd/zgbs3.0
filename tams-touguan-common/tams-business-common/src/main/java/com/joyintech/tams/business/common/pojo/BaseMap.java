package com.joyintech.tams.business.common.pojo;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BaseMap extends HashMap<String, Object> {

	private static final long serialVersionUID = -3444961562283617782L;

	public BaseMap() {
		super();
	}

	public static BaseMap newMap(Map map){
		if (map == null) {
			return new BaseMap();
		}
		BaseMap bm = new BaseMap();
		bm.putAll(map);
		return bm;
	}

	public static BaseMap newMap(){
		return new BaseMap();
	}

	@Override
	public BaseMap put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public BaseMap(int initialCapacity) {
		super(initialCapacity);
	}

	public String getString(String key) {
		return getString(this, key);
	}
	
	public Long getLong(String key) {
		return getLong(this, key);
	}
	
	public Integer getInteger(String key) {
		return getInteger(this, key);
	}
	
	public BigDecimal getBigDecimal(String key) {
		return getBigDecimal(this, key);
	}

	public String getBigDecimalStr(String key, String pattern) {
		DecimalFormat df1 = new DecimalFormat(pattern);
		BigDecimal temp = getBigDecimal(this, key);
		if (temp == null){
			return null;
		}
		return df1.format(temp);
	}
	public String getBigDecimalStr(Map<String, Object> param, String key, String pattern) {
		DecimalFormat df1 = new DecimalFormat(pattern);
		return df1.format(getBigDecimal(param, key));
	}
	public Date getDate(String key) {
		return getDate(this, key);
	}

	public Date getTimestamp(String key) {
		return new Timestamp(getDate(this, key).getTime());
	}
	
	public void convertDate(String key) {
		Date date = getDate(this, key);
		super.put(key, date);
	}
	
	public Long getLong(Map<String, Object> param,String key) {
		Object value = param.get(key);
		if (null==value || (value instanceof Long)) {
			return (Long) value;
		}
		return (Long)ConvertUtils.convert(value, Long.class);
	}
	
	public Integer getInteger(Map<String, Object> param,String key) {
		Object value = param.get(key);
		if (null==value || (value instanceof Integer)) {
			return (Integer) value;
		}
		return (Integer)ConvertUtils.convert(value, Integer.class);
	}
	
	public String getString(Map<String, Object> param,String key) {
		Object value = param.get(key);
		if (null==value || (value instanceof String)) {
			return (String) value;
		}
		return (String)ConvertUtils.convert(value, String.class);
	}
	
	public BigDecimal getBigDecimal(Map<String, Object> param,String key) {
		Object value = param.get(key);
		if (value == null){
			return null;
		}
		return new BigDecimal(value.toString());
	}
	
	public Date getDate(Map<String, Object> param,String key) {
		Object value = param.get(key);
		if (null==value || (value instanceof Date)) {
			return (Date) value;
		}else if (value instanceof String) {
			try {
				return DateUtils.parseDate((String)value,new String[]{"yyyy-MM-dd","yyyMMdd","yyyy-MM-dd HH:mm:ss"});
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return new Date(getLong(key));
	}

	public String getDateStr(Map<String, Object> param,String key) {
		return DateUtil.format(getDate(param, key));
	}
	
	public String getDateStr(String key) {
		return DateUtil.format(getDate(key));
	}

	/**
	 * 判断是否金额数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		if (StringUtil.isEmpty(str)){
			return false;
		}
		/*if (StringUtil.startsWith(str, "0")){
			return false;
		}*/
		if(StringUtil.contains(str, "-") && !StringUtil.startsWith(str, "-")){
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9.\\-]*");
		return pattern.matcher(str).matches();
	}
}
