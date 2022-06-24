package com.joyintech.tams.svreport.interf.chb.bottomassetposition.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.joyintech.framework.file.SafelyFile;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.FileImpService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.InterfFile;
import com.joyintech.tams.common.base.entity.ChbBotAstPosition;
import com.joyintech.tams.common.base.entity.ChbInterfConfigDto;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.framework.thread.JoyinThreadPoolExecutor;
import com.joyintech.tams.report.interf.common.constant.InterfConstants;
import com.joyintech.tams.report.interf.common.imp.ImportGenerator;
import com.joyintech.tams.report.interf.common.imp.sqlldr.ImpTable;
import com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper;
import com.joyintech.tams.svreport.interf.chb.bottomassetposition.dao.ChbBotAstPosInterfMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;

@Service
public class SvrBotAstPosFileImpService extends FileImpService{

	/**
	 * CHB_BOT_AST_POS
	 */
	
	 private static final String[] DUPLICATIED_LIST = {"POS_DATE","ASSET_CODE","PLAN_ASSET_ID"};
	
	 /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 上游接口解析读取文件处理最大线程数
     */
    @Value("${application.interfMaxCorePoolSize}")
    private int interfMaxCorePoolSize;

    /**
     * 上游接口解析读取文件处理,单次sqlldr处理最大数据条数
     */
    @Value("${application.reportInterfOneTimeSqlLdrDataCount}")
    private int reportInterfOneTimeSqlLdrDataCount;

    /**
     * 上游接口解析读取文件处理,单次sqlldr处理最大数据条数
     */
    @Value("${application.reportInterfOneTimeSqlLdrCommitCount}")
    private int reportInterfOneTimeSqlLdrCommitCount;
    
    /**
     * sqlldr用数据库连接
     */
    @Value("${spring.datasource.reportSqlldrDatabaseName}")
    private String reportSqlldrDatabaseName;
    
    @Value("${application.reportInsertLongCount}")
    private int reportInsertLongCount;
    
    
	@Autowired
	private SqlSessionTemplate template;
	
	@Autowired
	private ChbBotAstPosInterfMapper chbBotAstPosInterfMapper;
	
	@Autowired
	private ChbAssetInfoInterfMapper chbAssetInfoInterfMapper;

    
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	
	@SuppressWarnings("unchecked")
	private boolean excuteProdOpenSchByStep(List<Map<String, String>> posList, String type, 
			String tmpPath, ImpTable table,String dataSource,String saveFlag) {
		// 定义新增数据list-验证不通过数据
		// modify by convertMapToEntity yangxianjiu 2022-01-19 start
//		Map<String,Object> dataMap = convertMapToEntity(posList,dataSource,saveFlag);
//		List<ChbBotAstPosition> list = (List<ChbBotAstPosition>) dataMap.get("list");
		List<ChbBotAstPosition> list = (List<ChbBotAstPosition>) convertMapToEntity(posList,ChbBotAstPosition.class);
		list.stream().forEach(dto->{
			dto.setPosId(IDGenerator.generate(36));
		});
		// modify by convertMapToEntity yangxianjiu 2022-01-19 end
		if (CollectionUtils.isNotEmpty(list)) {
			// 先删后插,删除当日接入的持仓数据
			if (tmpPath == null) {
				// 插入开放计划
				insertData(list);
			} else {
				
				createDataFile(tmpPath + "/CHB_BOT_AST_POS/CHB_BOT_AST_POS", list, table.getColumns());
			}
		}
		return CollectionUtils.isNotEmpty(list);
	}
	
	
    /**
     * 将map转换为entity格式
     *
     * @param infoList 传入的listMap
     * @param entities 转换后的lis
     * @param listCloumn 表列名
     * @param interType 数据来源
     * @see
     * @since 1.0
     */
    private Map<String,Object> convertMapToEntity(List<Map<String, String>> dataList,String dataSource,String saveFlag) {
        // 获取产品表的列
    	List<ChbBotAstPosition> list = new ArrayList<ChbBotAstPosition>();
//    	Map<String,String> deleteList = new HashMap<String,String>();
        for (Map<String, String> map : dataList) {
//        	if(com.joyintech.tams.report.interf.common.constant.InterfConstants.SAVEFLAG_ALL.equals(saveFlag)){//全量
//        		deleteList.put(map.get("POS_DATE").toString(),null);
//        	}
        	ChbBotAstPosition t = new ChbBotAstPosition();
        	t.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
        	t.setDatasource(dataSource);
            for (String key : map.keySet()) {
            	if(key==null)
            		continue;
            	if(key.equals("ERRMSG")){//待处理的数据
            		t.setStatus(DictCodeListExt1.维护数据状态_待处理.getValue());
            		continue;
            	}
                String value = map.get(key);
                Field field = ReflectionUtils.findField(ChbBotAstPosition.class,
                			subBlank(key.toLowerCase()));
                if(field==null)
                	continue;
                field.setAccessible(true);
                Type type = field.getGenericType();
                if("java.lang.String".equals(type.getTypeName())){
                    try {
                        field.set(t, (value == null || "".equals(value)) ? "" : value);
                    } catch ( IllegalAccessException e) {
                        logger.error(e.getMessage(),e);
                    }
                }else if(value != null && !"".equals(value)){
                    if ("java.math.BigDecimal".equals(type.getTypeName())) {
                        try {
                            field.set(t,new BigDecimal(value));
                        } catch ( IllegalAccessException e) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                    else if ("java.lang.Integer".equals(type.getTypeName())) {
                        try {
                            field.set(t,Integer.valueOf(value));
                        } catch (IllegalAccessException e) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                    else if ("java.lang.Short".equals(type.getTypeName())) {
                        try {
                            field.set(t, Short.valueOf(value));
                        } catch ( IllegalAccessException e) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                    else if ("java.util.Date".equals(type.getTypeName())) {
                        if (value != null&&""!=value) {
                            try {
                                field.set(t, DateUtil.parse(value));
                            } catch ( IllegalAccessException e) {
                                logger.error(e.getMessage(),e);
                            }
                        }
                    }
                }  else{
                	try {
                        field.set(t, value);
                    } catch ( Exception e) {
                    	logger.error(e.getMessage(),e);
                    }
                }
            }
            t.setPosId(IDGenerator.generate(36));
            list.add(t);
        }
        Map<String,Object> rs = new HashMap<String,Object>();
        rs.put("list",list);
//        rs.put("deleteList",deleteList.keySet());
        return rs;
    }

	
	
	/**
	 * 根据产品删除开放计划
	 * @param prodidlist
	 */
	private void deleteData(List<ChbBotAstPosition> listChbBotAstPosition) {
		logger.debug("批量删除底层资产持仓信息----开始-----:" + df.format(new Date()));
		// 批量删除
		int singalCount = Integer.parseInt(null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)
				? "100" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
		int deleteCount = listChbBotAstPosition.size();
		if (deleteCount > 0) {
			if (deleteCount <= singalCount) {
				chbBotAstPosInterfMapper.deleteBatchList(listChbBotAstPosition);
			} else {
				for (int i = 0; i < deleteCount / singalCount; i++) {
					chbBotAstPosInterfMapper.deleteBatchList(listChbBotAstPosition.subList(0, singalCount));
					listChbBotAstPosition.removeAll(listChbBotAstPosition.subList(0, singalCount));
				}
				if (CollectionUtils.isNotEmpty(listChbBotAstPosition)) {
					chbBotAstPosInterfMapper.deleteBatchList(listChbBotAstPosition);
				}
			}
		}
		logger.debug("批量删除底层资产持仓信息----结束-----:" + df.format(new Date()));
	}
	
	/**
	 * 根据产品删除开放计划
	 * @param prodidlist
	 */
	private void deleteByPosDate(Set<String> dateList) {
		logger.debug("按日期删除底层资产持仓信息----开始-----:" + df.format(new Date()));
		for(String date : dateList){
			chbBotAstPosInterfMapper.deleteByPosdate(DateUtil.parse(date));
			logger.debug("成功删除:"+date+"的数据---------");
		}
		
		logger.debug("按日期删除底层资产持仓信息----结束-----:" + df.format(new Date()));
	}
	
	/**
	 * 根据产品删除开放计划
	 * @param prodidlist
	 */
	private void deleteByUpdateTime(Date date) {
		logger.debug("按日期删除底层资产持仓信息----开始-----:" + df.format(date));
			chbBotAstPosInterfMapper.deleteByUpdateTime();
		
		logger.debug("按日期删除底层资产持仓信息----结束-----:" + df.format(new Date()));
	}
	
	
	
	/**
	 * 开放计划入库
	 * @param openSchlist
	 */
	private void insertData(List<ChbBotAstPosition> listChbBotAstPosition) {
		logger.debug("批量插入底层持仓信息----开始-----:" + df.format(new Date()));
    	SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
    	//批量插入mapper
    	ChbBotAstPosInterfMapper batchMapper =  session.getMapper(ChbBotAstPosInterfMapper.class);
	    List<String> assetTypeList = new ArrayList<String>();
	    assetTypeList.add(DictCodeListExt2.资产_负债类别_资产管理产品.getValue());
	    assetTypeList.add(DictCodeListExt2.资产_负债类别_私募基金.getValue());
	    assetTypeList.add(DictCodeListExt2.资产_负债类别_委外投资_协议方式.getValue());
		List<String> assetPlanLList = chbAssetInfoInterfMapper.selectAssetCodeByAssetType(assetTypeList, null);
		try {
			int count = 0;
			for (ChbBotAstPosition position : listChbBotAstPosition) {
				position.setCreateUser("system");
	        	position.setUpdateUser("system");
	        	position.setIschl("02");
	        	position.setStatus("E");
				if(assetPlanLList.contains(position.getAssetCode())){
	        		position.setIsfinaplan("01");
	        	}else{
					position.setIsfinaplan("02");
	        	}
				
				batchMapper.insert(position);
				count++;
				if (count % 1000 == 0) {
					session.commit();
					session.clearCache();
				}
			}
			session.commit();
			session.clearCache();
		} finally {
			session.close();
		}
		logger.debug("批量插入开放计划信息----结束-----:" + df.format(new Date()));
	}
	

    
    
	

	@Override
	public String saveData(List<Map<String, String>> posList, String saveFlag,String dataSource) {

		// 解析成功或失败的标识
		boolean success = true;
		String type = "";
		String tmpPath =  SvrDataUtil.getAppParamCache(GlobalConstant.SYS_PARA_KEY, GlobalConstant.COM_PROP_ACCESS_EXPORTPATH);
		// 数据小于1000直接入库
		logger.info("----底层资产持仓信息入库，开始-----");
		
		if(InterfConstants.SAVEFLAG_ADD.equals(saveFlag)){
			//增量
			//modify by convertMapToEntity yangxianjiu 2022-01-19 start
//			Map<String,Object> dataMap = convertMapToEntity(posList,dataSource,saveFlag);
//			List<ChbBotAstPosition> list = (List<ChbBotAstPosition>) dataMap.get("list");
			List<ChbBotAstPosition> list = (List<ChbBotAstPosition>) convertMapToEntity(posList,ChbBotAstPosition.class);
			list.stream().forEach(dto->{
				dto.setPosId(IDGenerator.generate(36));
			});
			//modify by convertMapToEntity yangxianjiu 2022-01-19 end
			mergeData(list);
		}else if (posList.size() <= reportInterfOneTimeSqlLdrDataCount) {
			deleteByUpdateTime(new Date());
			success = excuteProdOpenSchByStep(posList, type, null, null,dataSource,saveFlag);
		} else {// 大于1000使用sqlloader入库
			deleteByUpdateTime(new Date());
			// 创建表
			ImpTable table = new ImpTable(InterfDefine.资管计划底层资产持仓接口数据.getCode());
			table.setColumns(ImportGenerator.createImpTableColumns(InterfDefine.资管计划底层资产持仓接口数据.getCode()));
			// 创建一个线程池
			if (interfMaxCorePoolSize < 1) {
				interfMaxCorePoolSize = 20;
			}
			ExecutorService service = new JoyinThreadPoolExecutor(interfMaxCorePoolSize);
			List<Callable<Boolean>> tasks = new ArrayList<>();
			if (null != posList) {
				int start = 0;
				int end = 0;
				int size = posList.size();
				int max = (int) Math.ceil((double) size / 100000);
				for (int i = 0; i < max; i++) {
					start = end;
					end = Math.min(size, 1000 * (i + 1));
					List<Map<String, String>> subList = posList.subList(start, end);
					tasks.add(new Callable<Boolean>() {
						@Override
						public Boolean call() {
							return excuteProdOpenSchByStep(subList, type, tmpPath, table,dataSource,saveFlag);
						}
					});
				}
			}
			// 为线程池添加任务
			List<Future<Boolean>> taskResult;
			try {
				taskResult = service.invokeAll(tasks);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
				return success?"success":"fail";
			}
			for (Future<Boolean> res : taskResult) {
				try {
					success = success && res.get();
				} catch (InterruptedException | ExecutionException e) {
					logger.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
					return success?"success":"fail";
				}
			}
			// 任务结束，停止线程池的所有线程
			service.shutdown();
			Collection<File> files = FileUtils.listFiles(new SafelyFile(tmpPath+"/CHB_BOT_AST_POS/"), new String[] { "tmp" }, false);
			List<String> content = new ArrayList<>(posList.size());
			String result = "";
			for (File f : files) {
				if (f.getName().startsWith(InterfDefine.资管计划底层资产持仓接口数据.getCode())) {
					try {
						content.addAll(FileUtils.readLines(f, Charset.forName("UTF-8")));
						logger.debug("查找sqlloder文件" + f);
					} catch (IOException e) {
						logger.debug("查找sqlloder文件异常");
						logger.error(e.getMessage(), e);
						result = "fail";
					} finally {
						f.delete();
					}
				}
			}
			if ("fail".equals(result)) {
				return result;
			}
			// 系统是否使用的db2数据库
			boolean isDB2DataBase = CommonUtil.isDB2DataBase();
			if (isDB2DataBase) {
				// db2数据库处理
				if (CollectionUtils.isNotEmpty(content)) {
					try {
						success = success && ImportGenerator.importToDBByPart(table, tmpPath+"/CHB_BOT_AST_POS/", content,
								reportInterfOneTimeSqlLdrDataCount, reportInterfOneTimeSqlLdrCommitCount, "del",
								reportSqlldrDatabaseName);
					} catch (IOException | InterruptedException e) {
						logger.error(e.getMessage(), e);
						return success?"success":"fail";
					}
				}
			} else {
				// oracle原逻辑
				if (CollectionUtils.isNotEmpty(content)) {
					try {
						success = success && ImportGenerator.importToDBByPart(table, tmpPath+"/CHB_BOT_AST_POS/", content,
								reportInterfOneTimeSqlLdrDataCount, reportInterfOneTimeSqlLdrCommitCount, "txt",
								reportSqlldrDatabaseName);
					} catch (IOException | InterruptedException e) {
						logger.error(e.getMessage(), e);
						return success?"success":"fail";
					}
				}
			}
		}
		logger.info("----底层资产持仓信息入库，结束-----");

		return success?"success":"fail";
	}
	
	private void mergeData(List<ChbBotAstPosition> listNE) {
		 SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
			try {
					 	int count = 0;
			            logger.debug("底层资产持仓接口数据插入开始--------------" + new Date());
			            ChbBotAstPosInterfMapper batchMapper = session.getMapper(ChbBotAstPosInterfMapper.class);
			            int batchCount = reportInsertLongCount==0?50:reportInsertLongCount;//每批commit的个数
			            int batchLastIndex = batchCount;//每批最后一个的下标
			            for(int index = 0; index < listNE.size();){
			                if(batchLastIndex > listNE.size()-1){
			                    batchLastIndex = listNE.size();
			                    batchMapper.mergeData(listNE.subList(index, batchLastIndex));
			                    break;//数据插入完毕,退出循环
			                }else{
			                    batchMapper.mergeData(listNE.subList(index, batchLastIndex));
			                    index = batchLastIndex;//设置下一批下标
			                    batchLastIndex = index + batchCount;                     
			                }
			                count ++;
			                // 每20次提交
			                if (count % 20 == 0) {
			                    session.commit();
			                    // 清理缓存，防止溢出
			                    session.clearCache();
			                }
			            }
			            session.commit();
			            // 清理缓存，防止溢出
			            session.clearCache();
			} finally {
				session.close();
			}
	 }
	

	@SuppressWarnings("unchecked")
	@Override
	public String importDB(List<Map<String, String>> dataList,InterfFile file, String date) {
		String rs = "success";
		// 解析文件
		if(dataList==null){
			try {
				Map<String,Object> parseResult  = parseFile(file) ;
				dataList = (List<Map<String, String>>) parseResult.get("dataList");
				rs = (String) parseResult.get("remoteDir");
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		// 解析成功，对数据list进行处理
		if (!CollectionUtils.isEmpty(dataList)) {
//			List<Map<String, String>>  listA =
			checkData(dataList, file.getFilename(), InterfDefine.资管计划底层资产持仓接口数据.getCode(),
							InterfDefine.资管计划底层资产持仓接口数据.getCode(),null, DUPLICATIED_LIST);
//			dataList.addAll(listA);
			if(CollectionUtils.isEmpty(dataList)){
				return rs;
			}
			//全量
	        ChbInterfConfigDto interfConfig = ChbInterfUtil.queryInterfConfigByInterfid(InterfDefine.资管计划底层资产持仓接口数据.getCode());
	        if (interfConfig == null){
	            logger.info("CHB_INTERF_CONFIG表中无配置项！");
	            return "fail";
	        }
	        String saveFlag = interfConfig.getContentType();
			rs = saveData(dataList,saveFlag,file.getDataSource());
		}
		return rs;

	}


	
	
}
