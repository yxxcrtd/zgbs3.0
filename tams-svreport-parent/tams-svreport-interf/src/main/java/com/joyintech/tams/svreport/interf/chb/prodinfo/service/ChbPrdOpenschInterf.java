package com.joyintech.tams.svreport.interf.chb.prodinfo.service;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.BaseInterfService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.base.entity.ChbPrdOpensch;
import com.joyintech.tams.common.svreport.chb.constant.ChbInterfConfig;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.svreport.interf.chb.conf.DataSourceType;
import com.joyintech.tams.svreport.interf.chb.prodinfo.dao.ChbPrdOpenschInterfMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 开放计划数据接口处理类
 * @author jiangxiao
 * @version 3.0 2019年2月19日
 * @since 3.0
 */
@SuppressWarnings("rawtypes")
@Service
@Transactional(rollbackFor = Exception.class)
public class ChbPrdOpenschInterf extends BaseInterfService{

	private Logger logger = LoggerFactory.getLogger(getClass());

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private ChbPrdOpenschInterfMapper chbPrdOpenschInterfMapper;
	
	@Autowired
	private SqlSessionTemplate template;
	
	/**
	 * @Description 开放计划表(CHB_PRD_OPENSCH)数据接入处理
	 * @param batchDate
	 * @return void
	 * @version 3.0 2019年2月19日
	 */
	public List<String> generatePrdOpenschData(Date batchDate,List<String> prodIdlist){
		// 查询数据
		List<ChbPrdOpensch> openSchlist = getBusEntity(prodIdlist,null, null, "D");
		// 入库
		if(!StringUtil.isEmptyList(openSchlist)){
			insertData2(openSchlist);
		}
		return new ArrayList<String>();
	}
	@Override
	public List<ChbPrdOpensch> getBusEntity(List<String> prodIdlist, Date date, String institutionCode, String source) {
		//从FIN_OPEN_PLAN获取数据进入CHB_PRD_OPENSCH
		//切换数据源查询信息
		ChbPrdOpenschInterf chbPrdOpenschInterf2 =  SpringContextUtil.getBean(ChbPrdOpenschInterf.class);
		logger.debug("读取上游开放计划信息----开始-----:" +  df.format(new Date()));
		Date lastUpdatetime = ChbInterfUtil.queryUpdateTime(ChbInterfConfig.产品开放计划信息接口.getInterId());
		//查询有开放计划变更的产品集合
		List<String> prodidlist = chbPrdOpenschInterf2.selectOpenSchProdid(lastUpdatetime,prodIdlist);
		List<ChbPrdOpensch> openSchlist = new ArrayList<ChbPrdOpensch>();
		if(CollectionUtils.isNotEmpty(prodidlist)){
			//查询申赎或者赎回的计划
			openSchlist = chbPrdOpenschInterf2.selectOpenSchData(null,prodidlist);
			//查询申赎同频的计划
			List<ChbPrdOpensch> openSchsublist = chbPrdOpenschInterf2.selectOpenSchData("03",prodidlist);
//			String issuePartyId = CommonUtil.selectBankCode();
			String issuePartyId = CacheUtil.getSysParam("SVR_ISSUE_PARTY_ID");
			if(CollectionUtils.isNotEmpty(openSchlist)){
				for(ChbPrdOpensch opensch : openSchlist){
					//主键
					opensch.setSeqno(IDGenerator.generate(36));
					//创建人
					opensch.setCreateUser(SecurityUtil.getLoginUser() == null ?"system" :SecurityUtil.getLoginUser().getUid());
					//更新人
					opensch.setUpdateUser(SecurityUtil.getLoginUser() == null ?"system" :SecurityUtil.getLoginUser().getUid());
					//有效状态
					opensch.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
					//数据来源
					if("D".equals(source)){
						opensch.setDataSource("16");
					}
					//发行机构代码
					opensch.setIssuePartyId(issuePartyId);

					if(opensch.getUpdateTime().compareTo(lastUpdatetime) > 0){
						lastUpdatetime = opensch.getUpdateTime();
					}
				}
			}
			//当开放计划类型为申赎同频，申购和赎回计划各存一条
			if(CollectionUtils.isNotEmpty(openSchsublist)){
				for(ChbPrdOpensch opensch : openSchsublist){
					//主键
					opensch.setSeqno(IDGenerator.generate(36));
					opensch.setCreateUser(SecurityUtil.getLoginUser() == null ?"system" :SecurityUtil.getLoginUser().getUid());
					opensch.setUpdateUser(SecurityUtil.getLoginUser() == null ?"system" :SecurityUtil.getLoginUser().getUid());
					//有效状态
					opensch.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
					//数据来源
					if("D".equals(source)){
						opensch.setDataSource("16");
					}
					//发行机构代码
					opensch.setIssuePartyId(issuePartyId);
					//申购计划
					opensch.setPlanType("01");
					openSchlist.add(opensch);
					ChbPrdOpensch openschsub = new ChbPrdOpensch();
					BeanUtils.copyProperties(opensch,openschsub);
					//赎回计划
					openschsub.setPlanType("02");
					openschsub.setSeqno(IDGenerator.generate(36));
					openSchlist.add(openschsub);

					if(opensch.getUpdateTime().compareTo(lastUpdatetime) > 0){
						lastUpdatetime = opensch.getUpdateTime();
					}
				}
			}
			//先删后插
			if("D".equals(source)){
				deleteData(prodidlist);
			}
//			insertData(prodidlist, openSchlist);
			//新的最后更新时间
			if(lastUpdatetime!=null){
				ChbInterfUtil.updateLastupdatetime(ChbInterfConfig.产品开放计划信息接口.getInterId(), lastUpdatetime);;
			}
		}
		logger.debug("读取上游开放计划信息----结束-----:" +  df.format(new Date()));

		return openSchlist;
	}

	@Override
	public Class getEntityClass() {
		return ChbPrdOpensch.class;
	}

	@Override
	public String getFileName() {
		return InterfDefine.产品开放计划接口数据.getCode();
	}
	/**
	 * 根据产品删除开放计划
	 * @param prodidlist
	 */
	private void deleteData(List<String> prodidlist){
		logger.debug("批量删除开放计划信息----开始-----:" +  df.format(new Date()));
		//批量删除
		int singalCount = Integer.parseInt(null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"100" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
		int deleteCount = prodidlist.size();
		if(deleteCount>0){
			if(deleteCount<=singalCount){
				chbPrdOpenschInterfMapper.deletePrdOpensch(prodidlist);
			}
			else{
				for(int i = 0; i < deleteCount/singalCount ; i++){
					chbPrdOpenschInterfMapper.deletePrdOpensch(prodidlist.subList(0, singalCount));
					prodidlist.removeAll(prodidlist.subList(0, singalCount));
				}
                if(CollectionUtils.isNotEmpty(prodidlist)){
                	chbPrdOpenschInterfMapper.deletePrdOpensch(prodidlist);
                }
			}
		}
		logger.debug("批量删除开放计划信息----结束-----:" +  df.format(new Date()));
	}
	
	/**
	 * 开放计划有变动，则全删全插操作
	 * @param openSchlist
	 */
	private void insertData2(List<ChbPrdOpensch> openSchlist) {
		logger.debug("批量插入开放计划信息----开始-----:" +  df.format(new Date()));
		SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
    	//批量插入mapper
		ChbPrdOpenschInterfMapper chbPrdOpenschInterfMapper =  session.getMapper(ChbPrdOpenschInterfMapper.class);
		try{
			int count = 0;
			for(ChbPrdOpensch sch : openSchlist){
				chbPrdOpenschInterfMapper.insert(sch);
        		count++;
        		if(count % 1000 == 0){
            		session.commit();
            		session.clearCache();
        		}
        	}
			session.commit();
    		session.clearCache();	
		}
//		catch (Exception e) {
//            logger.debug("批量插入开放计划出错:" + e.getMessage());
//			session.rollback();
//        } 
		finally {
    		session.close();
    	}
		logger.debug("批量插入开放计划信息----结束-----:" +  df.format(new Date()));
	}
	
	/*private void insertData(List<String> prodidlist, List<ChbPrdOpensch> openSchlist) {
		//批量删除
		int singalCount = Integer.parseInt(null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"500" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
		int totalCount = openSchlist.size();
		int deleteCount = prodidlist.size();
		if(deleteCount>0){
			if(deleteCount<=singalCount){
				chbPrdOpenschInterfMapper.deletePrdOpensch(prodidlist);
			}
			else{
				for(int i = 0; i < deleteCount/singalCount ; i++){
					chbPrdOpenschInterfMapper.deletePrdOpensch(prodidlist.subList(0, singalCount));
					prodidlist.removeAll(prodidlist.subList(0, singalCount));
				}
		        if(CollectionUtils.isNotEmpty(prodidlist)){
		        	chbPrdOpenschInterfMapper.deletePrdOpensch(prodidlist);
		        }
			}
		}
		logger.debug("批量删除开放计划信息----结束-----:" +  df.format(new Date()));
//			allData.addAll(openSchlist);
		//批量插入
		logger.debug("批量插入开放计划信息----开始-----:" +  df.format(new Date()));
		if(totalCount>0){
			if(totalCount<=singalCount){
				chbPrdOpenschInterfMapper.insertBatchList(openSchlist);
			}
			else{
				for(int i = 0; i < totalCount/singalCount ; i++){
					chbPrdOpenschInterfMapper.insertBatchList(openSchlist.subList(0, singalCount));
					openSchlist.removeAll(openSchlist.subList(0, singalCount));
				}
		        if(CollectionUtils.isNotEmpty(openSchlist)){
		        	chbPrdOpenschInterfMapper.insertBatchList(openSchlist);
		        }
			}
		}
	}*/
	
	/**
	 * @Description 切换数据源，查询开放计划
	 * @param batchDate
	 * @param plantype
	 * @return List<ChbPrdOpensch>
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.INTERF)
	public List<ChbPrdOpensch> selectOpenSchData(String plantype,List<String> prodidlist){
		List<ChbPrdOpensch> allList = new ArrayList<ChbPrdOpensch>();
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		List<List<String>> bigList = createList(prodidlist,500);
		paraMap.put("plantype", plantype);
		for(int i=0;i < bigList.size();i++){
			paraMap.put("prodidlist", bigList.get(i));
			template.select("com.joyintech.tams.svreport.interf.chb.prodinfo.dao.ChbPrdOpenschInterfMapper.selectOpenSchData",paraMap,
	                new ResultHandler() {
	            @Override
	            public void handleResult(ResultContext resultContext) {
	            	allList.add((ChbPrdOpensch) resultContext.getResultObject());
	            }
	        });
		}
		return allList;
	}
		
	/**
	 * 将集合均分成几个大小一致的子集合
	 * @param 集合
	 * @param 子集合大小
	 * @return
	 */
	public static List<List<String>>  createList(List<String> targe,int size) {
		List<List<String>> listArr = new ArrayList<List<String>>();
		//获取被拆分的数组个数
		int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;
		for(int i=0;i<arrSize;i++) {
			List<String> sub = new ArrayList<String>();
			//把指定索引数据放入到list中
			for(int j = i*size;j <= size*(i+1)-1;j++) {
				if(j <= targe.size()-1) {
					sub.add(targe.get(j));
				}
			}
			listArr.add(sub);
		}
		return listArr;
	}
	
	/**
	 * @Description 切换数据源，查询开放计划变更的所有产品代码
	 * @param batchDate
	 * @param plantype
	 * @return List<ChbPrdOpensch>
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.INTERF)
	public List<String> selectOpenSchProdid(Date batchDate,List<String> prodIdlist){
		List<String> prodList = new ArrayList<String>();
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		paraMap.put("updatetime", batchDate);
		paraMap.put("prodidList", prodIdlist);
		template.select("com.joyintech.tams.svreport.interf.chb.prodinfo.dao.ChbPrdOpenschInterfMapper.selectOpenSchProdid",paraMap,
                new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
            	prodList.add((String) resultContext.getResultObject());
            }
        });
		return prodList;
	}
}
