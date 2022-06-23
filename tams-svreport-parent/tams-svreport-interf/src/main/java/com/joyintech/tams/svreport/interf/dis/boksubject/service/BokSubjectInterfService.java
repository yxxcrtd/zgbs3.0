package com.joyintech.tams.svreport.interf.dis.boksubject.service;

import java.text.ParseException;
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
import com.joyintech.tams.svreport.interf.dis.boksubject.dao.DisBokSubjectMapper;
import com.joyintech.tams.svreport.interf.dis.boksubject.dto.BokSubject;
import com.joyintech.tams.svreport.interf.dis.common.DisInterfDecimalUtil;

/**
 * 科目表接口
 * 
 * @author dongxiangyang
 *
 */
@Service
public class BokSubjectInterfService extends FileImpService {

	@Autowired
	private DisBokSubjectMapper bokSubjectMapper;
	/**
	 * 长sql最大一次插入或者更新条数
	 */
	@Value("${application.reportInsertLongCount}")
	public int reportInsertLongCount;

	/**
	 * 重写saveData方法 科目表信息入库 listData 费用交易list saveFlag 入库标识
	 */
	@Override
	public String saveData(List<Map<String, String>> listData, String saveFlag, String dateSource) {
		if (StringUtil.isEmptyList(listData)) {
			logger.info("saveData方法参数listData为空。");
			return "success";
		}
		// 全部删除
		bokSubjectMapper.delete();
		List<BokSubject> list = new ArrayList<>();
		// modify by convertMapToEntity yangxianjiu 2022-01-19 start
		List<BokSubject> listTemp = new ArrayList<>();
		list = (List<BokSubject>) convertMapToEntity(listData,BokSubject.class);
		list.stream().forEach(dto->{
			if (listTemp.size() < reportInsertLongCount) {
				listTemp.add(dto);
			} else if (listTemp.size() >= reportInsertLongCount) {
				bokSubjectMapper.insert(listTemp);
				listTemp.clear();
				listTemp.add(dto);
			}
		});
		if (CollectionUtils.isNotEmpty(listTemp)) {
			bokSubjectMapper.insert(listTemp);
		}
		// modify by convertMapToEntity yangxianjiu 2022-01-19 end
		// 循环参数
//		for (Map<String, String> map : listData) {
//			BokSubject dto = new BokSubject();
//			try{
//                // 兼容老接口PROD_ID
//                dto.setBooksetId(map.get("PROD_ID"));
//				if(dto.getBooksetId() == null){
//					dto.setBooksetId(map.get("BOOKSET_ID"));
//				}
//				// 兼容老接口SUBJECT_ID
//				dto.setSubjectNo(map.get("SUBJECT_ID"));
//                if(dto.getSubjectNo() == null){
//                    dto.setSubjectNo(map.get("SUBJECT_NO"));
//                }
//				dto.setSubjectName(map.get("SUBJECT_NAME"));
//				dto.setFsubjectId(map.get("FSUBJECT_ID"));
//				dto.setSubjectType(map.get("SUBJECT_TYPE"));
//				dto.setSubjectLevel(DisInterfDecimalUtil.str2Long(map.get("SUBJECT_LEVEL")));
//				dto.setGenDetsubFlag(map.get("GEN_DETSUB_FLAG"));
//				dto.setStatus(map.get("STATUS"));
//				dto.setCreateUser("interf");
//				dto.setCreateDept(null);
//				dto.setUpdateUser("interf");
//			}catch(Exception e){
//				logger.error("解析数据错误：",e.getMessage(),e);
//           		return  "fail";
//			}
//			try {
//				if (StringUtil.isNotEmpty(map.get("CREATE_TIME"))) {
//					dto.setCreateTime(DateUtil.parseDate(map.get("CREATE_TIME"),
//							new String[] { DateUtil.DEFAULT_DATETIME_FORMAT_ODS }));
//				} else {
//					dto.setCreateTime(new Date());
//				}
//				if (StringUtil.isNotEmpty(map.get("UPDATE_TIME"))) {
//					dto.setUpdateTime(DateUtil.parseDate(map.get("UPDATE_TIME"),
//							new String[] { DateUtil.DEFAULT_DATETIME_FORMAT_ODS }));
//				} else {
//					dto.setUpdateTime(new Date());
//				}
//			} catch (ParseException e) {
//				logger.error("transMap2Dto方法转换CreateTime或UpdateTime异常");
//				logger.error(e.getMessage(),e);
//           		return  "fail";
//			}
//			if (list.size() < reportInsertLongCount) {
//				list.add(dto);
//			} else if (list.size() >= reportInsertLongCount) {
//				bokSubjectMapper.insert(list);
//				list.clear();
//				list.add(dto);
//			}
//		}
//		// 如果循环结束，小于最大值
//		if (CollectionUtils.isNotEmpty(list)) {
//			bokSubjectMapper.insert(list);
//		}

		return "success";
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
        String inerfType = InterfDefine.科目数据.getCode();
        //检查数据类型
        String dataType = InterfDefine.科目数据.getCode();
        //重复性检查的keys
        String[] checkEuplicatedKeys = {"PROD_ID","SUBJECT_NO"};
        String filename = interfFile == null ? null : interfFile.getFilename();
        List<Map<String, String>>  listA = super.checkData(dataList, filename, inerfType, dataType,null, checkEuplicatedKeys);
//        dataList.addAll(listA);// listA为错误数据，在checkData时被remove，需要重新放回dataList
    }
	
	@Override
	public String importDB(List<Map<String, String>> listData, InterfFile interfFile, String date) {
		 String rs = "success";
	        // 对数据list进行处理
	        if (!CollectionUtils.isEmpty(listData)) {
	            checkSaveData(listData, interfFile);
	            // 数据入库
	            rs = saveData(listData, null,null);
	        }
	        return rs;
	}

}
