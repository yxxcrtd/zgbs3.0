package com.joyintech.tams.svreport.pbc.classic.prdbaseinfosubnew.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.ChbTypeMappingExMapper;
import com.joyintech.tams.common.base.entity.ChbTypeMappingEx;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.svreport.pbc.assetposition.constants.PbcAssetPositionConstants;
import com.joyintech.tams.svreport.pbc.classic.data.service.PbcClassicsUtil;
import com.joyintech.tams.svreport.pbc.classic.prdacccountsub.dto.PbcPrdAccountSub;
import com.joyintech.tams.svreport.pbc.classic.prdbaseinfosub.dao.PbcPrdBaseinfoSubMapper;
import com.joyintech.tams.svreport.pbc.classic.prdbaseinfosub.dto.PbcPrdBaseinfoSub;
import com.joyintech.tams.svreport.pbc.classic.prdbaseinfosubnew.dao.PbcPrdBaseinfoSubNewMapper;
import com.joyintech.tams.svreport.pbc.classic.prdbaseinfosubnew.dto.PbcPrdBaseinfoSubNew;
import com.joyintech.tams.svreport.pbc.common.service.PbcClassicsSubService;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 人行产品基本信息登记批处理service
 * @author yuluan
 * @version 1.7 2019年06月04日
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PbcPrdBaseinfoSubNewService extends PbcClassicsSubService<PbcPrdBaseinfoSubNew>{

	@Autowired
	private PbcPrdBaseinfoSubNewMapper pbcPrdBaseinfoSubNewMapper;
	@Autowired
	private SqlSessionTemplate template;
	/**
	 * 映射配置mapper
	 */
	@Autowired
	private ChbTypeMappingExMapper chbTypeMappingExMapper;
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	public final static String PBC_PROD_BASEINFO_SEQCODE="PBCPRDBASEINFO";
	@Override
	public void genReport(Date executeDate, List<String> prodIdList) {
		generateRegistData(executeDate,prodIdList);

	}

	public void generateRegistData(Date executeDate, List<String> prodIdList){
		Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
		//查询需要生成登记的数据
		PbcPrdBaseinfoSubNew qmodle=new PbcPrdBaseinfoSubNew();
		qmodle.setStatusDate(executeDate);
		qmodle.setProdList(prodIdList);
		List<PbcPrdBaseinfoSubNew> needRegistSubList=pbcPrdBaseinfoSubNewMapper.selectNeedRegisteData(qmodle);
		if(CollectionUtils.isEmpty(needRegistSubList)){
			return;
		}
		/***新人行获取ex映射时候取getSrcfield2 字段**/
		List<ChbTypeMappingEx>  subjectNoMappingList = chbTypeMappingExMapper
				.selectByMappingcode(PbcAssetPositionConstants.MAPPINGCODE_DEPT_CODE_CHBTOPBC);
		Map<String,ChbTypeMappingEx> inDepCodeMap = subjectNoMappingList.stream().collect(Collectors.toMap(ChbTypeMappingEx::getSrcfield1, a -> a,(k1,k2)->k1));
		Map<String,PbcPrdBaseinfoSubNew> regMap = new HashMap<String,PbcPrdBaseinfoSubNew>();
		Map<String,String > pbcProdIdMap = new HashMap<String,String>();
		//查询已生成人行产品代码的数据
		List<PbcPrdBaseinfoSubNew> allPbcProdIdlist= new ArrayList<PbcPrdBaseinfoSubNew>();
		int batchCount = Integer.parseInt( null == CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
		int batchLastIndex = batchCount;//每批最后一个的下标
		for(int index = 0; index < needRegistSubList.size();){
			if(batchLastIndex > needRegistSubList.size()-1){
				batchLastIndex = needRegistSubList.size();
				allPbcProdIdlist.addAll(pbcPrdBaseinfoSubNewMapper.selectAllRegisteData(needRegistSubList.subList(index, batchLastIndex)));
				break;//数据插入完毕,退出循环
			}else{
				allPbcProdIdlist.addAll(pbcPrdBaseinfoSubNewMapper.selectAllRegisteData(needRegistSubList.subList(index, batchLastIndex)));
				index = batchLastIndex;//设置下一批下标
				batchLastIndex = index + batchCount;
			}
		}
		if(!CollectionUtils.isEmpty(allPbcProdIdlist)){
			for(PbcPrdBaseinfoSubNew regdto : allPbcProdIdlist){
				pbcProdIdMap.put(regdto.getProdId(), regdto.getPbcProdId());
			}
		}
		//查询已生成人行产品代码的数据
		Map<String,String > pbcAccountIdMap = new HashMap<String,String>();

		//新人行未做相关表数据增补,且与老人行分开进行人行代码生成.此部分代码全部无用
//		List<PbcPrdAccountSub> allPbcAccountIdlist= new ArrayList<PbcPrdAccountSub>();
//		int batchLastIndex1 = batchCount;//每批最后一个的下标
//        for(int index = 0; index < needRegistSubList.size();){
//            if(batchLastIndex1 > needRegistSubList.size()-1){
//            	batchLastIndex1 = needRegistSubList.size();
//            	allPbcAccountIdlist.addAll(pbcPrdBaseinfoSubNewMapper.selectAllRegisteAccountData(needRegistSubList.subList(index, batchLastIndex1)));
//                break;//数据插入完毕,退出循环
//            }else{
//            	allPbcAccountIdlist.addAll(pbcPrdBaseinfoSubNewMapper.selectAllRegisteAccountData(needRegistSubList.subList(index, batchLastIndex1)));
//                index = batchLastIndex1;//设置下一批下标
//                batchLastIndex1 = index + batchCount;
//            }
//        }
////				pbcPrdBaseinfoSubNewMapper.selectAllRegisteAccountData(needRegistSubList);
//		 if(!CollectionUtils.isEmpty(allPbcAccountIdlist)){
//			 for(PbcPrdAccountSub regdto : allPbcAccountIdlist){
//				 pbcAccountIdMap.put(regdto.getAccountId(), regdto.getPbcAccountId());
//			 }
//		 }
		//查询已报送的数据
		List<PbcPrdBaseinfoSubNew> reglist= new ArrayList<PbcPrdBaseinfoSubNew>();
		int batchLastIndex2 = batchCount;//每批最后一个的下标
		for(int index = 0; index < needRegistSubList.size();){
			if(batchLastIndex2 > needRegistSubList.size()-1){
				batchLastIndex2 = needRegistSubList.size();
				reglist.addAll(pbcPrdBaseinfoSubNewMapper.selectAlReadyRegisteData(needRegistSubList.subList(index, batchLastIndex2)));
				break;//数据插入完毕,退出循环
			}else{
				reglist.addAll(pbcPrdBaseinfoSubNewMapper.selectAlReadyRegisteData(needRegistSubList.subList(index, batchLastIndex2)));
				index = batchLastIndex2;//设置下一批下标
				batchLastIndex2 = index + batchCount;
			}
		}
//				pbcPrdBaseinfoSubNewMapper.selectAlReadyRegisteData(needRegistSubList);
		if(!CollectionUtils.isEmpty(reglist)){
			for(PbcPrdBaseinfoSubNew regdto : reglist){
				regMap.put(regdto.getProdId(), regdto);
			}
		}
		// 产品发行登记插入列表
		List<PbcPrdBaseinfoSubNew> issueInsertDataList = new ArrayList<PbcPrdBaseinfoSubNew>();
		// 产品发行登记删除列表
		List<PbcPrdBaseinfoSubNew> issueDeleteDataList = new ArrayList<PbcPrdBaseinfoSubNew>();
		// 处理产品发行登记
		for(PbcPrdBaseinfoSubNew sub : needRegistSubList){
			if(sub.getInDepName() != null && inDepCodeMap.containsKey(sub.getInDepName())){
				sub.setInDepCode(inDepCodeMap.get(sub.getInDepName()).getObjfield2());
			}
			String pbcProdId;
			if(pbcProdIdMap.containsKey(sub.getProdId())){
				pbcProdId=pbcProdIdMap.get(sub.getProdId());
			}else{
//	        		pbcProdId=PbcClassicsUtil.getPbcProdId(sub.getIssuePartyId(), sub.getProfitType(), PBC_PROD_BASEINFO_SEQCODE,sub.getRaiseVdate());
				/*** 20210727 leipeng 新人行改造
				 * 2.2、 调用新的人行内部采番共通											※参考【附件】sheet
				 * 		getPbcProdIdIns
				 * 			参数与老逻辑一致的基础上，加一个参数：
				 * 			采番数量			=	1
				 * */
				pbcProdId=PbcClassicsUtil.getPbcProdIdIns(sub.getIssuePartyId(), sub.getProfitType(), PBC_PROD_BASEINFO_SEQCODE,sub.getRaiseVdate(),1);
			}
			sub.setPbcProdId(pbcProdId);
			if(pbcAccountIdMap.containsKey(sub.getProdId())){
				sub.setAccountId(pbcAccountIdMap.get(sub.getProdId()));
			}
			sub.setRegistSeqno(IDGenerator.generate(36));
			/**根据中债码获取人行机构编码**/
			sub.setIssuePartyId(this.getPbcBankCode(sub.getIssuePartyId()));
			sub.setCreateUser(CommonUtil.getUserName());
			sub.setUpdateUser(CommonUtil.getUserName());
			sub.setCreateDept(CommonUtil.getDeptCode());
//	            sub.setStatusDate(batchTime);
			if(StringUtil.isEmpty(sub.getOrgCode())){
				sub.setOrgCode(CommonUtil.getOrgCode());
			}
			sub.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
			if(sub.getHighYieldIssue()!=null){
				sub.setHighYieldIssue(sub.getHighYieldIssue().setScale(5,BigDecimal.ROUND_HALF_UP));
			}
			if(sub.getLowYieldIssue()!=null){
				sub.setLowYieldIssue(sub.getLowYieldIssue().setScale(5,BigDecimal.ROUND_HALF_UP));
			}
			/**add  by dongxiangyang  江苏银行个性化,添加新字段 存智逻辑 20200117 start*/
			//关于净值型产品带ZH结尾的去除将产品代码存入这个字段
			String  isSubPord=SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_JSBK_IS_SUBPROD);
			if(StringUtil.isNotEmpty(isSubPord)&&isSubPord.equals("01")){
				String  prodId=sub.getProdId();
				if(prodId.endsWith("ZH")){
					sub.setSubProdId(StringUtil.substring(prodId, 0, -2));
				}else{
					sub.setSubProdId(prodId);
				}
			}
			/**add  by dongxiangyang  江苏银行个性化,添加新字段 存智逻辑 20200117 end*/

			// 产品发行登记报送时点:募集起始日期
			if(sub.getVdate()!=null){
				List<Date> issueDateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_新人行产品基本信息.getValue(),
						DictCodeListExt2.报送数据状态_新建.getValue(), dateMap,
						sub.getVdate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
				if (CollectionUtils.isNotEmpty(issueDateList)) {
					sub.setPbcSubmitDeadline(issueDateList.get(0));
					sub.setSubmitDeadline(issueDateList.get(1));
				}
			}
			if (regMap.get(sub.getProdId()) != null) {
				issueDeleteDataList.add(sub);
				//根据基础表数据状态判断生成登记数据状态
				if(DictCodeListExt1.维护数据状态_删除.getValue().equals(sub.getStatus())){
					sub.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
				}else{
					// 判断要素表数据有没有修改
					if(compareIssueRegData(regMap.get(sub.getProdId()),sub)){
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

		//入库
	}

	/**
	 * 功能:带提交的方式单条处理数据结果，入库
	 * @since 1.6.0
	 * @version 1.6.0
	 * @author：weiyuqiang 2019年3月7日
	 * @param assetList
	 * void
	 */
	private void dealResultIssueCommitSingle(List<PbcPrdBaseinfoSubNew> deleteDataList,List<PbcPrdBaseinfoSubNew> insertDataList){
		if (CollectionUtils.isNotEmpty(deleteDataList)) {
			int batchCount = Integer.parseInt( null == CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : CacheUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
			int batchLastIndex = batchCount;//每批最后一个的下标
			for(int index = 0; index < deleteDataList.size();){
				if(batchLastIndex > deleteDataList.size()-1){
					batchLastIndex = deleteDataList.size();
					pbcPrdBaseinfoSubNewMapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
					break;//数据插入完毕,退出循环
				}else{
					pbcPrdBaseinfoSubNewMapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
					index = batchLastIndex;//设置下一批下标
					batchLastIndex = index + batchCount;
				}
			}
		}
		if (CollectionUtils.isNotEmpty(insertDataList)) {
			int count = 0;
			SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
			try{
				PbcPrdBaseinfoSubNewMapper batchMapper =  session.getMapper(PbcPrdBaseinfoSubNewMapper.class);
				for(PbcPrdBaseinfoSubNew asset : insertDataList){
					batchMapper.insert(asset);
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
	public boolean compareIssueRegData(PbcPrdBaseinfoSubNew dto,PbcPrdBaseinfoSubNew regdto){
		PbcPrdBaseinfoSubNew compare1 = genIssueDto(dto);
		PbcPrdBaseinfoSubNew compare2 = genIssueDto(regdto);
		if(JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 根据中债编码获取人行机构编码
	 * @return
	 */
	public String getPbcBankCode(String IssuePartyId){
		String code = "";
		if(StringUtil.isEmpty(IssuePartyId)){
			IssuePartyId=CommonUtil.selectBankCode();
		}
		code = pbcPrdBaseinfoSubNewMapper.selectPbcBankCodeName(IssuePartyId);

		return null!=code?code:"";
	}

	private PbcPrdBaseinfoSubNew genIssueDto(PbcPrdBaseinfoSubNew dto){
		PbcPrdBaseinfoSubNew gendto = new PbcPrdBaseinfoSubNew();
		gendto.setIssuePartyId(dto.getIssuePartyId());
		gendto.setPbcProdType(dto.getPbcProdType());
		gendto.setProdName(dto.getProdName());
		gendto.setProdBrand(dto.getProdBrand());
		gendto.setProdStage(dto.getProdStage());
		gendto.setProdId(dto.getProdId());
		gendto.setRaiseCcy(dto.getRaiseCcy());
		gendto.setCashPrinCcy(dto.getCashPrinCcy());
		gendto.setCashProfCcy(dto.getCashProfCcy());
		gendto.setCustomerType(dto.getCustomerType());
		gendto.setRaiseType(dto.getRaiseType());
		gendto.setManageType(dto.getManageType());
		gendto.setFiduciaryDuty(dto.getFiduciaryDuty());
		gendto.setContMode(dto.getContMode());
		gendto.setOperateMode(dto.getOperateMode());
		gendto.setIvstorientation(dto.getIvstorientation());
		gendto.setBusiModel(dto.getBusiModel());
		gendto.setIncomeGuaranIdent(dto.getIncomeGuaranIdent());
		gendto.setPrinGuaranIdent(dto.getPrinGuaranIdent());
		//gendto.setHighYieldIssue(dto.getHighYieldIssue());
		//gendto.setLowYieldIssue(dto.getLowYieldIssue());
		if(dto.getHighYieldIssue()!=null && StringUtil.isNotEmpty(dto.getHighYieldIssue())){
			gendto.setHighYieldIssue(dto.getHighYieldIssue().setScale(5,BigDecimal.ROUND_HALF_UP));
		}
		if(dto.getLowYieldIssue()!=null && StringUtil.isNotEmpty(dto.getLowYieldIssue())){
			gendto.setLowYieldIssue(dto.getLowYieldIssue().setScale(5,BigDecimal.ROUND_HALF_UP));
		}
		gendto.setRaiseVdate(dto.getRaiseVdate());
		gendto.setRaiseEnddate(dto.getRaiseEnddate());
		gendto.setTermRightFlag(dto.getTermRightFlag());
		gendto.setCusRedeemFlag(dto.getCusRedeemFlag());
		gendto.setCreditFlag(dto.getCreditFlag());
		gendto.setCreditType(dto.getCreditType());
		gendto.setCreditMode(dto.getCreditMode());
		gendto.setInDepName(dto.getInDepName());
		gendto.setInDepCode(dto.getInDepCode());
		gendto.setOutDepCtry(dto.getOutDepCtry());
		gendto.setOutDepName(dto.getOutDepName());
		gendto.setVdate(dto.getVdate());
		gendto.setMdate(dto.getMdate());
		gendto.setIsConstracte(dto.getIsConstracte());
		gendto.setTransferType(dto.getTransferType());
		gendto.setIdentiFlag(dto.getIdentiFlag());
		gendto.setCrossBorderFlag(dto.getCrossBorderFlag());
		gendto.setZjxtFlag(dto.getZjxtFlag());
		return gendto;
	}




	public Map<Object, Object> createData(Map<String, Object> param) {
		Map<Object, Object> data = new HashMap<Object, Object>();
		if (param.get("registSeqno") == null) {
			return data;
		}
		String registSeqno = param.get("registSeqno").toString();
       /* PbcPrdBaseinfoSub pbcPrdBaseinfoSub = pbcMonthReportMapper.selectByPrimaryKey(reportuuid);
        // 如果报表为空则返回data
        if (pbcPrdBaseinfoSub == null) {
            return data;
        }
        //pbcPrdBaseinfoSub.setProdVariety(pbcPrdBaseinfoSub.getProdVariety() + "-" + SysDataUtil.getNameFromDataDict(DictCode.产品品种, pbcPrdBaseinfoSub.getProdVariety()));
        data.put("report", pbcPrdBaseinfoSub);*/
		// 导出excel或预览时，值为0的单元格显示成0还是显示成空，读取系统参数
		String isShowZero = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SHOW_ZERO);
		List<PbcPrdBaseinfoSubNew> listDetail = pbcPrdBaseinfoSubNewMapper.searchPage(param);
		if (!CollectionUtils.isEmpty(listDetail)) {
			for (PbcPrdBaseinfoSubNew detail : listDetail) {
				// 处理数据
                /*dealAssetDebtDateListForExport(detail);
                dealProdData(detail, isShowZero);*/
				data.put("data", detail);
			}
		}
		return data;
	}






}