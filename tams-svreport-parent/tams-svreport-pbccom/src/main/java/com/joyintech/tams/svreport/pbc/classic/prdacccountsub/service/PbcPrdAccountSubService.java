package com.joyintech.tams.svreport.pbc.classic.prdacccountsub.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.svreport.pbc.classic.data.service.PbcClassicsUtil;
import com.joyintech.tams.svreport.pbc.classic.prdacccountsub.dao.PbcPrdAccountSubMapper;
import com.joyintech.tams.svreport.pbc.classic.prdacccountsub.dto.PbcPrdAccountSub;
import com.joyintech.tams.svreport.pbc.common.service.PbcClassicsSubService;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * @Description 人行资产池基本信息登记批处理service
 * @author yuluan
 * @version 1.7 2019年06月04日
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PbcPrdAccountSubService extends PbcClassicsSubService<PbcPrdAccountSub>{

	@Autowired
	private PbcPrdAccountSubMapper pbcPrdAccountSubMapper;
	@Autowired
	private SqlSessionTemplate template;
	  /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public final static String PBC_PROD_ACCOUNT_SEQCODE="PBCPRDACCOUNT";
	@Override
	public void genReport(Date executeDate, List<String> prodIdList) {
		generateRegistData(executeDate,prodIdList);
		
	}
	
	public void generateRegistData(Date executeDate, List<String> prodIdList){
		 Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
		//查询需要生成登记的数据
		PbcPrdAccountSub qmodle=new PbcPrdAccountSub();
		qmodle.setStatusDate(executeDate);
		qmodle.setProdList(prodIdList);
		List<PbcPrdAccountSub> needRegistSubList=pbcPrdAccountSubMapper.selectNeedRegisteData(qmodle);
		if(CollectionUtils.isEmpty(needRegistSubList)){
			return;
		}
		//查询已生成人行产品代码的数据
		Map<String,String > pbcAccountIdMap = new HashMap<String,String>();
		List<PbcPrdAccountSub> allPbcProdIdlist=new ArrayList<PbcPrdAccountSub>();
		 int batchCount = Integer.parseInt( null == CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
         int batchLastIndex = batchCount;//每批最后一个的下标
         for(int index = 0; index < needRegistSubList.size();){
             if(batchLastIndex > needRegistSubList.size()-1){
                 batchLastIndex = needRegistSubList.size();
                 allPbcProdIdlist.addAll(pbcPrdAccountSubMapper.selectAllRegisteData(needRegistSubList.subList(index, batchLastIndex)));
                 break;//数据插入完毕,退出循环
             }else{
            	 allPbcProdIdlist.addAll(pbcPrdAccountSubMapper.selectAllRegisteData(needRegistSubList.subList(index, batchLastIndex)));
                 index = batchLastIndex;//设置下一批下标
                 batchLastIndex = index + batchCount;                     
             }
         }
//		List<PbcPrdAccountSub> allPbcProdIdlist= pbcPrdAccountSubMapper.selectAllRegisteData(needRegistSubList);
		 if(!CollectionUtils.isEmpty(allPbcProdIdlist)){
			 for(PbcPrdAccountSub regdto : allPbcProdIdlist){
				 pbcAccountIdMap.put(regdto.getAccountId(), regdto.getPbcAccountId());
			 }
		 }
		Map<String,PbcPrdAccountSub> regMap = new HashMap<String,PbcPrdAccountSub>();
		//查询已报送的数据
		List<PbcPrdAccountSub> reglist=new ArrayList<PbcPrdAccountSub>();
         int batchLastIndex2 = batchCount;//每批最后一个的下标
         for(int index = 0; index < needRegistSubList.size();){
             if(batchLastIndex2 > needRegistSubList.size()-1){
            	 batchLastIndex2 = needRegistSubList.size();
            	 reglist.addAll(pbcPrdAccountSubMapper.selectAlReadyRegisteData(needRegistSubList.subList(index, batchLastIndex2)));
                 break;//数据插入完毕,退出循环
             }else{
            	 reglist.addAll(pbcPrdAccountSubMapper.selectAlReadyRegisteData(needRegistSubList.subList(index, batchLastIndex2)));
                 index = batchLastIndex2;//设置下一批下标
                 batchLastIndex2 = index + batchCount;                     
             }
         }
//		List<PbcPrdAccountSub> reglist= pbcPrdAccountSubMapper.selectAlReadyRegisteData(needRegistSubList);
		 if(!CollectionUtils.isEmpty(reglist)){
             for(PbcPrdAccountSub regdto : reglist){
            	 regMap.put(regdto.getAccountId(), regdto);
             }
		 }
		  // 产品发行登记插入列表
	        List<PbcPrdAccountSub> issueInsertDataList = new ArrayList<PbcPrdAccountSub>();
	        // 产品发行登记删除列表
	        List<PbcPrdAccountSub> issueDeleteDataList = new ArrayList<PbcPrdAccountSub>();
	        // 处理产品发行登记
	        for(PbcPrdAccountSub sub : needRegistSubList){
	        	String pbcAccountId;
	        	if(pbcAccountIdMap.containsKey(sub.getAccountId())){
	        		pbcAccountId=pbcAccountIdMap.get(sub.getAccountId());
	        	}else{
	        		pbcAccountId=PbcClassicsUtil.getPbcAccountId(sub.getIssuePartyId(),  PBC_PROD_ACCOUNT_SEQCODE,sub.getVdate());
	        	}
	        	sub.setPbcAccountId(pbcAccountId);
	            sub.setRegistSeqno(IDGenerator.generate(36));
	            sub.setCreateUser(CommonUtil.getUserName());
	            sub.setUpdateUser(CommonUtil.getUserName());
	            sub.setCreateDept(CommonUtil.getDeptCode());
//	            sub.setStatusDate(batchTime);
	            sub.setOrgCode(CommonUtil.getOrgCode());
	            sub.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
	            // 产品发行登记报送时点:募集起始日期
	            if(sub.getVdate()!=null){ 
	                List<Date> issueDateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_资产池基本信息.getValue(), 
	                        DictCodeListExt2.报送数据状态_新建.getValue(), dateMap,
	                        sub.getVdate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
	                if (CollectionUtils.isNotEmpty(issueDateList)) {
	                    sub.setPbcSubmitDeadline(issueDateList.get(0));
	                    sub.setSubmitDeadline(issueDateList.get(1));
	                }
	            }
	            if (regMap.get(sub.getAccountId()) != null) {
	            	 issueDeleteDataList.add(sub);
	                    //根据基础表数据状态判断生成登记数据状态
	                    if(DictCodeListExt1.维护数据状态_删除.getValue().equals(sub.getStatus())){
	                        sub.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
	                    }else{
	                        // 判断要素表数据有没有修改
	                        if(compareIssueRegData(regMap.get(sub.getAccountId()),sub)){
	                            continue;
	                        }else{
	                            sub.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
	                        }
	                    }
	                    // 准备插入处理未报送的数据
	                    issueInsertDataList.add(sub);
	            }else{
	            	 // 基础表数据状态为删除状态，且登记表无对应数据则不登记
	                if (DictCodeListExt1.维护数据状态_删除.getValue().equals(sub.getStatus())){
	                    issueDeleteDataList.add(sub);
	                    continue;
	                } else {
	                    issueDeleteDataList.add(sub);
	                    // 准备插入处理未报送的数据
	                    issueInsertDataList.add(sub);
	                    sub.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
	                }
	            }
	        }
	        // 删除、插入发行登记
	        dealResultIssueCommitSingle(issueDeleteDataList,issueInsertDataList);  
	}
	
	 /**
     * 功能:带提交的方式单条处理数据结果，入库
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月7日
     * @param assetList 
     * void
     */
    private void dealResultIssueCommitSingle(List<PbcPrdAccountSub> deleteDataList,List<PbcPrdAccountSub> insertDataList){
        if (CollectionUtils.isNotEmpty(deleteDataList)) {
            int batchCount = Integer.parseInt( null == CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            for(int index = 0; index < deleteDataList.size();){
                if(batchLastIndex > deleteDataList.size()-1){
                    batchLastIndex = deleteDataList.size();
                    pbcPrdAccountSubMapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                	pbcPrdAccountSubMapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
                    index = batchLastIndex;//设置下一批下标
                    batchLastIndex = index + batchCount;                     
                }
            }
        }
        if (CollectionUtils.isNotEmpty(insertDataList)) {
            int count = 0;
            SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try{
            	PbcPrdAccountSubMapper batchMapper =  session.getMapper(PbcPrdAccountSubMapper.class);
                for(PbcPrdAccountSub asset : insertDataList){
                    batchMapper.insert(asset);
                  //当产品信息检查出错误 修改后重新生成产品要素 而资产池要素不重新生成时产品要素资产池代码为空 修改先生成资产池数据 再生成产品基本信息数据
//                    batchMapper.updateBaseInfoAccountId(asset); 
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
            }
            catch(Exception e){
                session.rollback();
                logger.error(e.getMessage(),e);
            }
            finally {
                session.close();
            }
        }
    }
	  /**
     * @Description 判断要素表中登记数据是否有变
     * @param dto
     * @param regdto
     * @return boolean
     * @version 1.7 2019年5月22日
     */
    public boolean compareIssueRegData(PbcPrdAccountSub dto,PbcPrdAccountSub regdto){
    	PbcPrdAccountSub compare1 = genIssueDto(dto);
    	PbcPrdAccountSub compare2 = genIssueDto(regdto);
        if(JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
            return true;
        }else{
            return false;
        }
    }
    private PbcPrdAccountSub genIssueDto(PbcPrdAccountSub dto){
    	PbcPrdAccountSub gendto = new PbcPrdAccountSub();
    	gendto.setIssuePartyId(dto.getIssuePartyId());
    	gendto.setAccountName(dto.getAccountName());
    	gendto.setVdate(dto.getVdate());
    	gendto.setAccountId(dto.getAccountId());
    	return gendto;
    }
}