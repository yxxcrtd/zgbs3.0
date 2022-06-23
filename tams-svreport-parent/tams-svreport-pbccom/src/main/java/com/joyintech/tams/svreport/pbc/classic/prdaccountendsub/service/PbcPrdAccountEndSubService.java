package com.joyintech.tams.svreport.pbc.classic.prdaccountendsub.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.svreport.interf.chb.conf.DataSourceType;
import com.joyintech.tams.svreport.pbc.classic.prdaccountendsub.dao.PbcPrdAccountEndSubMapper;
import com.joyintech.tams.svreport.pbc.classic.prdaccountendsub.dto.PbcPrdAccountEndSub;
import com.joyintech.tams.svreport.pbc.common.service.PbcClassicsSubService;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * 功能:人行单产品报表-资产池终止信息
 * @since 1.7.0
 * @version 1.7.0
 * @作者：jiangxiao
 * @创建时间：2019年6月11日
 * @修改记录：
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PbcPrdAccountEndSubService extends PbcClassicsSubService<PbcPrdAccountEndSub>{
private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PbcPrdAccountEndSubMapper pbcPrdAccountEndSubMapper;
	
	@Autowired
	private SqlSessionTemplate template;
	
	/**
     * 数据批量一次性insert条数(单条数据字段较多)
     */
    @Value("${application.reportInsertLongCount}")
    private int reportInsertLongCount;

    /**
     * 数据批量一次查询或者删除条数
     */
    @Value("${application.reportSelectOrDeleteCount}")
    private int reportSelectOrDeleteCount;
    
	@Override
	public void genReport(Date executeDate, List<String> prodIdList) {
		generateRegistData(executeDate, prodIdList);
	}
	
	public void generateRegistData(Date executeDate, List<String> prodIdList){
		//查询资产池终止信息
		List<PbcPrdAccountEndSub> sublist = new ArrayList<PbcPrdAccountEndSub>();
		logger.debug("资产池终止信息数据：查询开始----------");
		if(!CollectionUtils.isEmpty(prodIdList)){
			int end = 0;
			for(int i=0;i<prodIdList.size();){
				end = i+100;
				if(end>prodIdList.size()){
					end = prodIdList.size();
				}
				sublist.addAll(pbcPrdAccountEndSubMapper.selectAccountEndSub(DateUtil.format(executeDate), prodIdList.subList(i, end)));
				i = end;
			}
		}else{
			sublist = pbcPrdAccountEndSubMapper.selectAccountEndSub(DateUtil.format(executeDate), prodIdList);
		}
		if(CollectionUtils.isEmpty(sublist)){
			return;
		}
		//查询产品头寸
		List<PbcPrdAccountEndSub> reglist = new ArrayList<PbcPrdAccountEndSub>();
		int end = 0;
		for(int i=0;i<sublist.size();){
			end = i+100;
			if(end>sublist.size()){
				end = sublist.size();
			}
			reglist.addAll(pbcPrdAccountEndSubMapper.selectRegList(sublist.subList(i, end)));
			i = end;
		}
		logger.debug("资产池终止信息数据：查询结束----------");
		Map<String,PbcPrdAccountEndSub> prodmap = new HashMap<String,PbcPrdAccountEndSub>();
		Map<String,PbcPrdAccountEndSub> regmap = new HashMap<String,PbcPrdAccountEndSub>();
		for (PbcPrdAccountEndSub dto : sublist) {
			if (!DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())) {
				prodmap.put(dto.getPbcAccountId(), dto);
			} else if (prodmap.get(dto.getPbcAccountId()) == null
					&& DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())) {
				prodmap.put(dto.getPbcAccountId(), dto);
			}
		}
		if(CollectionUtils.isNotEmpty(reglist)){
			for(PbcPrdAccountEndSub reg : reglist){
				regmap.put(reg.getPbcAccountId(), reg);
			}
		}
		if (!CollectionUtils.isEmpty(sublist)) {
			// 新增列表
			List<PbcPrdAccountEndSub> insertlist = new ArrayList<PbcPrdAccountEndSub>();
			// 删除列表
			List<PbcPrdAccountEndSub> deletelist = new ArrayList<PbcPrdAccountEndSub>();
			Iterator<Map.Entry<String, PbcPrdAccountEndSub>> iter = prodmap.entrySet().iterator();
			logger.debug("资产池终止信息数据：处理开始----------");
			while (iter.hasNext()) {
				PbcPrdAccountEndSub dto = iter.next().getValue();
				if(!DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())&&pbcPrdAccountEndSubMapper.selectIsPosition(dto)<=0){
					continue;
				}
				if (StringUtil.isNotEmpty(dto.getPbcAccountId())) {
					// 主键
					dto.setRegistSeqno(IDGenerator.generate(36));
					dto.setCreateUser(CommonUtil.getUserName());
					dto.setUpdateUser(CommonUtil.getUserName());
					dto.setCreateDept(CommonUtil.getDeptCode());
					dto.setOrgCode(CommonUtil.getOrgCode());
					dto.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
					// 数据日期
					dto.setStatusDate(executeDate);
					// 报送截止日生成
					Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
					List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_资产池终止信息.getValue(), 
			                DictCodeListExt2.报送数据状态_新建.getValue(), dateMap, dto.getMdate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
					if (!StringUtil.isEmptyList(dateList)) {
						dto.setPbcSubmitDeadline(dateList.get(0));
						dto.setSubmitDeadline(dateList.get(1));
					}
					boolean iszero = false;
                	if(!CollectionUtils.isEmpty(sublist)){
                		iszero = isZeroBalance(dto.getMdate(), dto.getProdId());
                	}
					if (dto.getRegseq() == null) {// 没有已报送的
						// 若数据状态为删除，不生成登记数据
						if (DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())) {
							// 删除数据(未报送的删除)
							deletelist.add(dto);
							continue;
						} else {
							if(iszero){
								// 设置更新状态
								dto.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
								insertlist.add(dto);
								// 删除数据(未报送的删除)
								deletelist.add(dto);
							}
						}
					} else {// 已有已报送的
						// 删除数据(未报送的删除)
						deletelist.add(dto);
						if(iszero){
							// 设置更新状态
							// 业务数据状态为删除时，若报送数据状态为删除不生成登记数据，否则生成删除未报送
							if (DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())) {
								if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regmap.get(dto.getPbcAccountId()).getStatus())){
									continue;
								}
								dto.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
							} else {// 业务数据状态为有效或者修改
								// 若报送数据为删除，则生成新建未报送
								if (DictCodeListExt2.报送数据状态_删除.getValue().equals(regmap.get(dto.getPbcAccountId()).getStatus())) {
									dto.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
								} else if(compareData(regmap.get(dto.getPbcAccountId()),dto)){// 若报送数据状态为新建或者修改，判断要素表数据有没有修改，若修改生成修改未报送
									continue;
								}else{
									dto.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
								}
							}
							insertlist.add(dto);
						}
					}
				}
			}
			logger.debug("资产池终止信息数据：处理结束----------");
			// 入库
			intoDatabase(insertlist, deletelist);
		}
	}
	private boolean compareData(PbcPrdAccountEndSub pbcPrdAccountEndSub, PbcPrdAccountEndSub dto) {
		if(pbcPrdAccountEndSub.getMdate().compareTo(dto.getMdate())==0){
			return true;
		}
		return false;
	}

	private void intoDatabase(List<PbcPrdAccountEndSub> insertlist, List<PbcPrdAccountEndSub> deletelist) {
		if (CollectionUtils.isNotEmpty(deletelist)) {
		    // 插入前先删除未报送的登记数据
		    logger.debug("资产池终止信息删除未报送数据：删除开始----------");
		    // 数据批量一次删除条数,如果参数设置过大则强制定位50
            int batchDeleteCount = reportSelectOrDeleteCount;
            // 每批最后一个的下标
            int batchDeleteLastIndex = batchDeleteCount;            
            for(int index = 0; index < deletelist.size();){
                if(batchDeleteLastIndex > deletelist.size()-1){
                    batchDeleteLastIndex = deletelist.size();
                    pbcPrdAccountEndSubMapper.deleteBatch(deletelist.subList(index, batchDeleteLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                    pbcPrdAccountEndSubMapper.deleteBatch(deletelist.subList(index, batchDeleteLastIndex));
                    index = batchDeleteLastIndex;//设置下一批下标
                    batchDeleteLastIndex = index + batchDeleteCount;                     
                }
            }
            logger.debug("资产池终止信息删除未报送数据：删除结束----------");
            logger.debug("资产池终止信息插入数据：插入开始----------");
			int count = 0;
			SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
	    	try{
	    		PbcPrdAccountEndSubMapper batchMapper =  session.getMapper(PbcPrdAccountEndSubMapper.class);
	    		for(PbcPrdAccountEndSub dto : insertlist){
	    			batchMapper.insert(dto);
	    			count++;
	    			if(count%1000==0){
	    				session.commit();
	    	            // 清理缓存，防止溢出
	    	            session.clearCache();
	    			}
	    		}
	    		session.commit();
	            // 清理缓存，防止溢出
	            session.clearCache();
	    	} catch(Exception e) {
	    		session.rollback();
	    		logger.error("资产池终止信息入库报错:"+e.getMessage(),e);
	    	} finally {
	    		session.close();
	    	}
            logger.debug("资产池终止信息插入数据：插入结束----------");
		}
	}
	/**
     * @Description 判断产品持仓是否为零
     * @param mdate
     * @param prodid
     * @return boolean
     * @version 1.7 2019年7月2日
     */
    private boolean isZeroBalance(Date cdate,String prodid){
    	BigDecimal pos = BigDecimal.ZERO;
//    	pos = prodTerminationRegist001Mapper.isAssetPosZero(mdate,prodid);
    	   // 通过系统参数判断是查询一体化还是中债子系统
        String isSubSystem = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SUB_SYSTEM);
        PbcPrdAccountEndSubService pbcPrdInfoEndSubService =  SpringContextUtil.getBean(PbcPrdAccountEndSubService.class);
        if (DictCodeList.是否_中债_是.getValue().equals(isSubSystem)){
        	pos=pbcPrdInfoEndSubService.queryBookBalanceLevel1SubSystem(cdate,prodid);
        }else if (DictCodeList.是否_中债_否.getValue().equals(isSubSystem)){
        	pos=pbcPrdInfoEndSubService.queryBookBalanceLevel1(cdate,prodid);
        }else{
        	 logger.error("系统参数中：SVR_IS_SUB_SYSTEM配置错误！");
             return false;
        }
    	if(pos!=null&&pos.compareTo(BigDecimal.ZERO)==0){
    		return true;
    	}else{
    		return false;
    	}
    }
	/**
     * 功能:查询接入日当天的资产头寸-科目余额部分
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月11日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public BigDecimal queryBookBalanceLevel1(Date cdate,String prodid){
    	return pbcPrdAccountEndSubMapper.queryBookBalanceLevel1(cdate,prodid);
    }

    /**
     * 功能:查询接入日当天的资产头寸-科目余额部分-中债子系统
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月11日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public BigDecimal queryBookBalanceLevel1SubSystem(Date cdate,String prodid){
    	return pbcPrdAccountEndSubMapper.queryBookBalanceLevel1SubSystem(cdate,prodid);
    }
}
