package com.joyintech.tams.valuation.common.interf.base.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateColMatchDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderDTO;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.transdata.service.HqTemplateCommonService;
/**
 * 解析db数据库临时表数据
 * @author WUJIE
 * @version 1.0 2018年9月5日
 * @see DbReadUtils
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DbReadUtils {

	@Autowired
	HqTemplateCommonService hqTemplateCommonService;
	
	/**
	 * 日志
	 */
//	private Logger logger = Logger.getLogger(DbReadUtils.class);
	private static Logger logger = LoggerFactory.getLogger(DbReadUtils.class);
	
	/**
     * db数据库读取数据解析
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> TemplateCheckReturnResult readDb(TemplateOrderInfoReaderResult acctEntity) {
        TemplateCheckReturnResult result = new TemplateCheckReturnResult();
        List<T> listAll = new ArrayList();
        // 日志记录list
        List<ValInterflog> valInterflogList = new ArrayList<ValInterflog>();
        List list = new ArrayList();
        try {
            // 初始化接收数据DTO
            Class<?> clazz = Class.forName(TempConstant.ENTITY_PATH + acctEntity.getDtoName());
            list = readDb(clazz, acctEntity, valInterflogList);
        }
        catch (Exception e) {
        	logger.error("",e);
        }
        if(list!=null && list.size()>0){
        	listAll.addAll(list);
        }
        result.setT(listAll);
        result.setValInterflog(valInterflogList);
        return result;
    }
    
    
    
	@SuppressWarnings("rawtypes")
	public <T> List<T> readDb(Class<T> clazz, TemplateOrderInfoReaderResult acctEntity,
            List<ValInterflog> valList) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
    	// 目标DTO字段
		Field[] fields = clazz.getDeclaredFields();
		Map<String,Field> mapfields = new HashMap<String,Field>();
		for(Field o :fields){
			mapfields.put(o.getName(), o);
		}
		// 将匹配关系表封装成Map
		Map<String, TemplateColMatchDTO> matchs = createMatchMap(acctEntity.getListTemplateColMatchDTO());
		// 构建checkMap
		Map<String, TemplateOrderInfoReaderDTO> checks = createCheckMap(acctEntity.getListTemplateOrderInfoDTO());
    	
		List<T> records = new ArrayList<T>();
    	String cdatestr = new SimpleDateFormat("yyyyMMdd").format(acctEntity.getResovleDate());
    	List list = hqTemplateCommonService.queryData(acctEntity.getTmpEName(),cdatestr);
    	if(list==null || list.size()==0){
//    		System.out.println(acctEntity.getTmpEName()+":今日临时表中未有最新数据...");
    		logger.info(acctEntity.getTmpEName()+":今日临时表中未有最新数据...");
			return null;
		}
    	Class<?> obj_clazz = hqTemplateCommonService.getDBEntityClass(acctEntity.getTmpEName());
		if(obj_clazz==null){
//			System.out.println("实体类"+acctEntity.getTmpEName()+"不存在或路径映射错误...");
			logger.info("实体类"+acctEntity.getTmpEName()+"不存在或路径映射错误...");
			return null;
		}
//    	System.out.println(acctEntity.getTmpEName()+"-本次同步数据："+list.size());
		logger.info(acctEntity.getTmpEName()+"-本次同步数据："+list.size());
    	for(int i = 0;i< list.size();i++){
    		Object obj = list.get(i);
    		
    		/**模板TmpEName对象的字段对象集**/
    		Field[] fields_obj = obj_clazz.getDeclaredFields();
    		
			/**校验日志**/
			ValInterflog checkLog = new ValInterflog();
			StringBuffer checkError = new StringBuffer();
			StringBuffer info = new StringBuffer();
			
			T bean = clazz.newInstance();//实例化模板对应的DTO
			
			/**循环模板TmpEName对象的字段对象，并将字段的值赋值给对应的DTO对象**/
			for (int j = 0; j < fields_obj.length; j++) {
				
				/**add by wujie on 20181107 start**/
				fields_obj[j].setAccessible(true);
				/**add by wujie on 20181107 end**/
				
				String key = fields_obj[j].getName();
				
				/**判断TmpEName对象的字段有没有匹配DTO对象字段**/
				if(!matchs.containsKey(key)){
					continue;
				}
				
				/**判断TmpEName对象的配置的字段匹配字段是不是DTO对象的字段**/
				if(!mapfields.containsKey(matchs.get(key).getDtoColEName())){
					continue;
				}
				fields_obj[j].setAccessible(true);
				Object value = fields_obj[j].get(obj);//获取TmpEName对象的字段的Value
				/**对象转换为字符串**/
				String qValue_str = null;
				Class<?> t = fields_obj[j].getType();
				if(value!=null){
					if (t.toString().contains("java.math.BigDecimal")) {
						qValue_str = ((BigDecimal)value).toPlainString();
					}else{
						qValue_str = String.valueOf(value);
					}
				}
				/**获取DTO的字段对象字段Field**/
				Field field = clazz.getDeclaredField(matchs.get(key).getDtoColEName());
				SetPropAndCheckUtil.setValue(field, bean,qValue_str, checks.get(key), checkLog, checkError,key);
			}
			if(checkLog.getIsCheck()== null || DictCodeList.是否_是.getValue().equals(checkLog.getIsCheck())){
				records.add(bean);
				checkLog.setIsCheck(DictCodeList.是否_是.getValue());
			}
			checkLog.setTmpName(acctEntity.getTmpEName());// 模板名称
			checkLog.setFileName(acctEntity.getTmpCName());// 文件名称
			checkLog.setExDate(new Date());// 日期
			checkLog.setParentSeqno(acctEntity.getParentSeqno());// 主副日志表连接键
			checkLog.setDetail(info.toString());
			checkLog.setNum((long) (i+1));
			checkLog.setRemark(checkError.toString());
			valList.add(checkLog);
    	}
		return records;
	}
    
    
    private Map<String, TemplateColMatchDTO> createMatchMap(List<TemplateColMatchDTO> matchList) {
        Map<String, TemplateColMatchDTO> map = new HashMap<String, TemplateColMatchDTO>();
        if (CollectionUtils.isEmpty(matchList)) {
            return map;
        }
        for (TemplateColMatchDTO dto : matchList) {
            map.put(dto.getTmpColEName(), dto);
        }
        return map;
    }
    
    /**
     * 构建校验信息字典
     * @param checkList
     * @return
     */
    private Map<String, TemplateOrderInfoReaderDTO> createCheckMap(List<TemplateOrderInfoReaderDTO> checkList) {
        Map<String, TemplateOrderInfoReaderDTO> checkIndex = new HashMap<String, TemplateOrderInfoReaderDTO>();
        if(checkList!=null&&!checkList.isEmpty()){
            for(TemplateOrderInfoReaderDTO check : checkList){
                checkIndex.put(check.getColEName(), check);
            }
        }
        return checkIndex;
    }


    
}
