package com.joyintech.tams.svreport.chb.prodpersonissue.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.common.svreport.datacheck.beforeinsert.dto.ChbCheckTableInfo;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
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
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService;
import com.joyintech.tams.batch.report.common.db.SvrDBUtil;
import com.joyintech.tams.common.base.entity.ChbTypeMapping;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.report.common.util.ChbDeadlineUtil;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.report.interf.common.constant.InterfMappingConstants;
import com.joyintech.tams.svreport.chb.datacheck.beforeinsert.service.ChbCommonCheckService;
import com.joyintech.tams.svreport.chb.perprodeclarereg.dao.PerProDeclareReg001Mapper;
import com.joyintech.tams.svreport.chb.perprodeclarereg.dto.PerProDeclareRegDto;
import com.joyintech.tams.svreport.chb.prodpersonissue.dao.ProdPersonIssue001Mapper;
import com.joyintech.tams.svreport.chb.prodpersonissue.dto.ProdPersonIssue001Dto;
import com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbTypeMappingMapper;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * 功能:产品发行申报登记批处理
 * @since 1.7.0
 * @version 1.7.0
 * @作者：weiyuqiang
 * @创建时间：2019年5月30日
 * @修改记录：
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProdIssueAndDecalreRegBatchService  extends SvrCBBaseService{
	
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ProdPersonIssue001Mapper prodPersonIssue001Mapper;
    
    @Autowired
    private ChbTypeMappingMapper chbTypeMappingMapper;
    
    @Autowired
    private PerProDeclareReg001Mapper perProDeclareReg001Mapper;
    
    @Autowired
    private SqlSessionTemplate template;
    
    @Autowired
    private SvrDBUtil svrDBUtil;

	/**
     * 入库前校验用Service
     */
    @Autowired
    private ChbCommonCheckService chbCommonCheckService;
    
    public void generateIssueData(Date batchTime,List<String> prodIds,boolean isSynchronizeTask,String InstitutionCode){
		setBatchBeanName(BatchBeanName.产品发行申报登记报送);
		logInfoBatchStart();
  		// 产品超过500全产品跑当天批
        if (prodIds != null && prodIds.size() > 500){
            prodIds = null;
        }
        //申报登记
        createSubAction(batchTime,InstitutionCode, prodIds);
		//生成发行登记
        createIssueAction(batchTime,prodIds);
		logInfoBatchEnd();
	}

    /**
     * @Description 发行登记生成
     * @param batchTime 跑批日
     * @param prodInfos 产品list
     * @throws BatchException
     * @version 1.7 2019年5月22日
     */
    public void createIssueAction(Date batchTime,List<String> prodInfos){
        Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
        
      //add by leipeng 20200628 多法人模式改造
    	String model_type = null;
    	if(CommonUtil.isBAmodel()){
    		model_type = "01";
    	}
        // 产品发行登记查询数据
        List<ProdPersonIssue001Dto> issueSubList =prodPersonIssue001Mapper.queryGenerateList(DateUtil.format(batchTime),prodInfos,model_type);
        Map<String,ProdPersonIssue001Dto> regMap = new HashMap<String,ProdPersonIssue001Dto>();
        if(!CollectionUtils.isEmpty(issueSubList)){
            //查询发行已登记数据
            List<ProdPersonIssue001Dto> reglist = prodPersonIssue001Mapper.queryRegIssueList(issueSubList);
            if(!CollectionUtils.isEmpty(reglist)){
                for(ProdPersonIssue001Dto regdto : reglist){
                    regMap.put(regdto.getProdId(), regdto);
                }
            }
        }
        // 产品发行登记插入列表
        List<ProdPersonIssue001Dto> issueInsertDataList = new ArrayList<ProdPersonIssue001Dto>();
        // 产品发行登记删除列表
        List<ProdPersonIssue001Dto> issueDeleteDataList = new ArrayList<ProdPersonIssue001Dto>();
        // 处理产品发行登记
        for(ProdPersonIssue001Dto sub : issueSubList){
        	// 产品发行申报登记报送 批处理:不再生成的5个字段 zhangyf 20200309 start
        	sub.setGradeType(null);;
        	sub.setExpMinYield(null);
        	sub.setExpMaxYield(null);
        	sub.setExpAvgYield(null);
        	sub.setYieldComment(null);
        	// 产品发行申报登记报送 批处理:不再生成的5个字段 zhangyf 20200309 end
        	
            sub.setRegistSeqno(IDGenerator.generate(36));
            // 报送流水号
            sub.setSumitSeq(CommonUtil.getSequenceIndexData(DataIndexSequenceType.中债登记管理));
            sub.setCreateUser(CommonUtil.getUserName());
            sub.setUpdateUser(CommonUtil.getUserName());
            sub.setCreateDept(CommonUtil.getDeptCode());
            sub.setStatusDate(batchTime);
            sub.setOrgCode(CommonUtil.getOrgCode());
            sub.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
            //update by leipeng 20200623
            //发行机构代码取值
            if(StringUtil.isEmpty(sub.getIssuePartyId())){
            	sub.setIssuePartyId(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
            }
//            if(StringUtil.isEmpty(sub.getIssuePartyId())){
//                String issuePartyId = CommonUtil.selectBankCode();
//                sub.setIssuePartyId(issuePartyId);
//            }
            

            // 已登记
            if (regMap.get(sub.getProdId()) != null) {
                //删除未报送的数据
                issueDeleteDataList.add(sub);
                //根据基础表数据状态判断生成登记数据状态
                if(DictCodeListExt1.维护数据状态_删除.getValue().equals(sub.getStatus())){
                	if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(sub.getProdId()).getStatus())){
                		continue;
                	}
                    sub.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
                }else{
                	if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(sub.getProdId()).getStatus())){
                		sub.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
                	}else if(compareIssueRegData(regMap.get(sub.getProdId()),sub)){// 判断要素表数据有没有修改
                        continue;
                    }else{
                        sub.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
                    }
                }
                // 准备插入处理未报送的数据
                issueInsertDataList.add(sub);
            } else {
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

            // 产品发行登记报送时点:募集起始日期
            if(sub.getRaiseVdate()!=null){
                List<Date> issueDateList = ChbDeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_产品发行登记.getValue(),
                        sub.getStatus(), dateMap,
                        sub.getRaiseVdate());
                if (CollectionUtils.isNotEmpty(issueDateList)) {
                    sub.setChbSubmitDeadline(issueDateList.get(0));
                    sub.setSubmitDeadline(issueDateList.get(1));
                }
            }
        }
        
    	//update by leipeng 插入的数据获取表 内的实际登记编码
        if(CommonUtil.isBAmodel()){
            Map<String,String> data = svrDBUtil.getAllRegCode();
            
            for (ProdPersonIssue001Dto perProDeclareRegDto : issueInsertDataList) {
         	   perProDeclareRegDto.setIssuePartyId(data.get(perProDeclareRegDto.getProdId()));
            }
    	}
        
        

        // 删除、插入发行登记
        // add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 start
        issueInsertDataList = chbCommonCheckService.checkData(issueInsertDataList, ChbCheckTableInfo.个人发行登记表SUB);
        // add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 end
        dealResultIssueCommitSingle(issueDeleteDataList,issueInsertDataList);
    }
    
    /**
     * @Description 申报登记生成
     * @param batchTime 跑批日
     * @param prodInfos 产品list
     * @throws BatchException
     * @return void
     * @version 1.7 2019年5月22日
     */
    public void createSubAction(Date batchTime,String InstitutionCode,List<String> prodInfos){
    	//add by leipeng 20200628 多法人模式改造
    	String model_type = null;
    	if(CommonUtil.isBAmodel()){
    		model_type = "01";
    	}
        //查询申报登记数据
        List<PerProDeclareRegDto> list = perProDeclareReg001Mapper.selectPerProdListEx(DateUtil.format(batchTime),model_type,InstitutionCode,prodInfos);
        Map<String,PerProDeclareRegDto> regMap = new HashMap<String,PerProDeclareRegDto>();
        if(!CollectionUtils.isEmpty(list)){
            //查询申报已登记数据
            List<PerProDeclareRegDto> reglist = perProDeclareReg001Mapper.selectRegPerProdList(list);
            if(!CollectionUtils.isEmpty(reglist)){
                for(PerProDeclareRegDto regdto : reglist){
                    regMap.put(regdto.getProdId(), regdto);
                }
            }
        }
        //报送时点
        List<Date> declare = null;
        Map<String, List<Date>> declaredateMap = new HashMap<String, List<Date>>();
        Map<String, List<Date>> declaredateMapP = new HashMap<String, List<Date>>();
        // 映射源数据和中债所需数据
        List<ChbTypeMapping> haveOrNotMappingList = chbTypeMappingMapper.selectMappingBySrcDataType(InterfMappingConstants.MAAPING_HAVE_OR_NOT);
        Map<String, ChbTypeMapping> haveOrNotMappingMap  = new HashMap<String, ChbTypeMapping>();
        if (!StringUtil.isEmptyList(haveOrNotMappingList)){
            haveOrNotMappingMap = haveOrNotMappingList.stream().collect(Collectors.toMap(ChbTypeMapping::getSrcdatatypeid, chbTypeMapping -> chbTypeMapping));
        }
        // 产品申报登记插入列表
        List<PerProDeclareRegDto> subInsertDataList = new ArrayList<PerProDeclareRegDto>();
        // 产品申报登记删除列表
        List<PerProDeclareRegDto> subDeleteDataList = new ArrayList<PerProDeclareRegDto>();
       for(int i=0;i<list.size();i++ ){
            PerProDeclareRegDto single = list.get(i);
            // 映射源数据和中债所需数据
            if (!StringUtil.isEmptyList(haveOrNotMappingList)){
                //发型机构提前终止权标识
                if (haveOrNotMappingMap.get(single.getTermRightFlag()) != null){
                    single.setTermRightFlag(haveOrNotMappingMap.get(single.getTermRightFlag()).getObjdatatypeid());
                }
                //客户赎回权标识
                if(haveOrNotMappingMap.get(single.getCusRedeemFlag()) != null){
                    single.setCusRedeemFlag(haveOrNotMappingMap.get(single.getCusRedeemFlag()).getObjdatatypeid());
                }
                //产品增信标识
                if(haveOrNotMappingMap.get(single.getCreditFlag()) != null){
                    single.setCreditFlag(haveOrNotMappingMap.get(single.getCreditFlag()).getObjdatatypeid());   
                }
            }
            //已报送(生成修改未报送、删除未报送)
            if (regMap.get(single.getProdInsId()) != null) {
                // 读系统参数
                if("Y".equals(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_SUBMIT_ONCHANGE))){
                	//删除未报送的数据
                    subDeleteDataList.add(single);
                    //根据基础表数据状态判断生成登记数据状态
                    if(DictCodeListExt1.维护数据状态_删除.getValue().equals(single.getStatus())){
                    	if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(single.getProdInsId()).getStatus())){
                    		continue;
                    	}
                        single.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
                    }else{
                    	if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(single.getProdInsId()).getStatus())){
                    		single.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
                    	}else if(compareRegData(regMap.get(single.getProdInsId()),single)){// 判断要素表数据有没有修改
                            continue;
                        }else{
                            single.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
                        }
                    }
                    //未报送的数据
                    subInsertDataList.add(single);
                }
            } else {
                // 基础表数据状态为删除状态，且登记表无已报送数据不生成报送数据
                if (DictCodeListExt1.维护数据状态_删除.getValue().equals(single.getStatus())){
                    subDeleteDataList.add(single);
                    continue;
                } else {
                    subDeleteDataList.add(single);
                    //未报送的数据
                    subInsertDataList.add(single);
                    single.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
                }
            }
            
            //登记序列
            single.setRegistSeqno(IDGenerator.generate(36));
           // 报送流水号
           single.setSumitSeq(CommonUtil.getSequenceIndexData(DataIndexSequenceType.中债登记管理));
            //报送状态
            single.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
            //数据日期
            single.setStatusDate(batchTime);
            //创建人
            single.setCreateUser(CommonUtil.getUserName());
            //更信任
            single.setUpdateUser(CommonUtil.getUserName());
            //创建部门
            single.setCreateDept(CommonUtil.getDeptCode());
            //创建机构
            single.setOrgCode(CommonUtil.getOrgCode());
            //update by leipeng 20200623
            //发行机构代码取值
           if(StringUtil.isEmpty(single.getIssuePartyId())){
            	single.setIssuePartyId(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
            }
//           if(StringUtil.isEmpty(single.getIssuePartyId())){
//               String issuePartyId = CommonUtil.selectBankCode();
//               single.setIssuePartyId(issuePartyId);
//           }
           //募集起始日期（从）
            if(single.getRaisePstrdate()!=null){
                if (DictCodeListExt2.募集方式_中债_公募.getValue().equals(single.getRaiseType())){
                    declare=DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_产品申报登记.getValue(), 
                            DictCodeListExt2.细分类别_公募.getValue(),single.getStatus(),
                            declaredateMap, list.get(i).getRaisePstrdate(), SVReportGlobalConstant.SUBMIT_TIME);
                } else if (DictCodeListExt2.募集方式_中债_私募.getValue().equals(single.getRaiseType())){
                        declare=DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_产品申报登记.getValue(), 
                                DictCodeListExt2.细分类别_私募.getValue(),single.getStatus(),
                                declaredateMapP, list.get(i).getRaisePstrdate(), SVReportGlobalConstant.SUBMIT_TIME);
                } else {
                    logger.error("产品代码："+single.getProdId()+"的募集方式接入错误！");
                }
                if (CollectionUtils.isNotEmpty(declare)) {
                    //中债报送截止日
                    single.setChbSubmitDeadline(declare.get(0));
                    //行内报送截止日
                    single.setSubmitDeadline(declare.get(1));
                }
            }
        }
     //update by leipeng 插入的数据获取表 内的实际登记编码
       if(CommonUtil.isBAmodel()){
           Map<String,String> data = svrDBUtil.getAllRegCode();
           
           for (PerProDeclareRegDto PerProDeclareRegDto : subInsertDataList ) {
               if(StringUtil.isNotEmpty(data.get(PerProDeclareRegDto.getProdId()))){
                   PerProDeclareRegDto.setIssuePartyId(data.get(PerProDeclareRegDto.getProdId()));
               }
           }
   		}
		// add by dubaojun 2020-10-14 入库前需要进行数据检验，检验错误的数据不入库 start
       subInsertDataList = chbCommonCheckService.checkData(subInsertDataList, ChbCheckTableInfo.个人产品申报登记SUB);
       // add by dubaojun 2020-10-14 入库前需要进行数据检验，检验错误的数据不入库 end
       // 删除、插入发行登记
       dealResultSubCommitSingle(subDeleteDataList,subInsertDataList);
    }
    
    
    /**
     * 功能:带提交的方式单条处理数据结果，入库
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月7日
     * @param assetList 
     * void
     */
    private void dealResultIssueCommitSingle(List<ProdPersonIssue001Dto> deleteDataList,List<ProdPersonIssue001Dto> insertDataList){
        if (CollectionUtils.isNotEmpty(deleteDataList)) {
            int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            for(int index = 0; index < deleteDataList.size();){
                if(batchLastIndex > deleteDataList.size()-1){
                    batchLastIndex = deleteDataList.size();
                    prodPersonIssue001Mapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                    prodPersonIssue001Mapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
                    index = batchLastIndex;//设置下一批下标
                    batchLastIndex = index + batchCount;                     
                }
            }
        }
        if (CollectionUtils.isNotEmpty(insertDataList)) {
            int count = 0;
            SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try{
                ProdPersonIssue001Mapper batchMapper =  session.getMapper(ProdPersonIssue001Mapper.class);
                for(ProdPersonIssue001Dto asset : insertDataList){
                    batchMapper.insertSub(asset);
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
     * 功能:带提交的方式单条处理数据结果，入库
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年3月7日
     * @param assetList 
     * void
     */
    private void dealResultSubCommitSingle(List<PerProDeclareRegDto> deleteDataList,List<PerProDeclareRegDto> insertDataList){
        if (CollectionUtils.isNotEmpty(deleteDataList)) {
            int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            for(int index = 0; index < deleteDataList.size();){
                if(batchLastIndex > deleteDataList.size()-1){
                    batchLastIndex = deleteDataList.size();
                    perProDeclareReg001Mapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                    perProDeclareReg001Mapper.deleteByProdid(deleteDataList.subList(index, batchLastIndex));
                    index = batchLastIndex;//设置下一批下标
                    batchLastIndex = index + batchCount;                     
                }
            }
        }
        if (CollectionUtils.isNotEmpty(insertDataList)) {
            int count = 0;
            SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
            try{
                PerProDeclareReg001Mapper batchMapper =  session.getMapper(PerProDeclareReg001Mapper.class);
                for(PerProDeclareRegDto asset : insertDataList){
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
     * @Description 判断要素表中发行登记数据是否有变
     * @param dto
     * @param regdto
     * @return boolean
     * @version 1.7 2019年5月22日
     */
    public boolean compareIssueRegData(ProdPersonIssue001Dto dto,ProdPersonIssue001Dto regdto){
        ProdPersonIssue001Dto compare1 = genIssueDto(dto);
        ProdPersonIssue001Dto compare2 = genIssueDto(regdto);
        if(JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
            return true;
        }else{
            return false;
        }
    }
    
    private ProdPersonIssue001Dto genIssueDto(ProdPersonIssue001Dto dto){
        ProdPersonIssue001Dto gendto = new ProdPersonIssue001Dto();
        //产品登记编码
        gendto.setProdRegistCode(dto.getProdRegistCode());
        //产品代码
        gendto.setProdId(dto.getProdId());
        //募集起始日期
        gendto.setRaiseVdate(dto.getRaiseVdate());
        //募集结束日期
        gendto.setRaiseEnddate(dto.getRaiseEnddate());
        //产品起始日期
        gendto.setPrdVdate(dto.getPrdVdate());
        //产品终止日期
        gendto.setPrdMdate(dto.getPrdMdate());
        //是否为结构化
        gendto.setIsConstracte(dto.getIsConstracte());
        //层级类别
        gendto.setGradeType(dto.getGradeType());
        //预计客户最低年收益率 %
        gendto.setExpMinYield((dto.getExpMinYield()==null?BigDecimal.ZERO:dto.getExpMinYield()).setScale(5, BigDecimal.ROUND_HALF_UP));
        //预计客户最高年收益率 %
        gendto.setExpMaxYield((dto.getExpMaxYield()==null?BigDecimal.ZERO:dto.getExpMaxYield()).setScale(5, BigDecimal.ROUND_HALF_UP));
        //平均预计客户年收益率%
        gendto.setExpAvgYield((dto.getExpAvgYield()==null?BigDecimal.ZERO:dto.getExpAvgYield()).setScale(5, BigDecimal.ROUND_HALF_UP));
        //收益率分档情况说明
        gendto.setYieldComment(dto.getYieldComment());
        //开放模式      01 有规律开放/02 无规律开放
        gendto.setOpenMode(dto.getOpenMode());
        //规律开放周期
        gendto.setOpenCircle(dto.getOpenCircle());
        //其他规律开放周期 (天)
        gendto.setOpenCircleDays((dto.getOpenCircleDays()==null?BigDecimal.ZERO:dto.getOpenCircleDays()).setScale(4, BigDecimal.ROUND_HALF_UP));
        //无规律开放说明
        gendto.setRuleComment(dto.getRuleComment());
        //首次开放周期起始日
        gendto.setFirstOpenDay(dto.getFirstOpenDay());
        //节假日是否开放            01 是/02 否
        gendto.setHolidayOnFlag(dto.getHolidayOnFlag());
        //平均开放次数（年化）
        gendto.setAvgOpenCount((dto.getAvgOpenCount()==null?BigDecimal.ZERO:dto.getAvgOpenCount()).setScale(4, BigDecimal.ROUND_HALF_UP));
        //开放期业务
        gendto.setOpenBuss(dto.getOpenBuss());
        //开放期业务说明
        gendto.setOpenBussRemark(dto.getOpenBussRemark());
        // add by dubaojun 2020-11-05 五月升级字段比较 start
        // 管理方式
        gendto.setManageType(dto.getManageType());
        // 业绩比较基准下限
        gendto.setYieldLow(dto.getYieldLow());
        // 业绩比较基准上限
        gendto.setYieldHigh(dto.getYieldHigh());
        // 业绩比较基准说明
        gendto.setYieldRemark(dto.getYieldRemark());
        // 资金托管账号
        gendto.setBusiAcctNo(dto.getBusiAcctNo());
        // 资金托管账户
        gendto.setAcctName(dto.getAcctName());
        // add by dubaojun 2020-11-05 五月升级字段比较 end
        return gendto;
    }
    
    /**
     * @Description 判断要素表中申报登记数据是否有变
     * @param dto
     * @param regdto
     * @return boolean
     * @version 1.7 2019年5月22日
     */
    public boolean compareRegData(PerProDeclareRegDto dto,PerProDeclareRegDto regdto){
        PerProDeclareRegDto compare1 = genDeclareDto(dto);
        PerProDeclareRegDto compare2 = genDeclareDto(regdto);
        if(JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
            return true;
        }else{
            return false;
        }
    }
    
    private PerProDeclareRegDto genDeclareDto(PerProDeclareRegDto dto){
        PerProDeclareRegDto gendto = new PerProDeclareRegDto();
        //产品代码
        gendto.setProdId(dto.getProdId());
        //产品名称
        gendto.setProdName(dto.getProdName());
        //行内标识码
        gendto.setProdInsId(dto.getProdInsId());
        //产品品牌
        gendto.setProdBrand(dto.getProdBrand());
        //产品期次
        gendto.setProdStage((dto.getProdStage()==null?BigDecimal.ZERO:dto.getProdStage()).setScale(4, BigDecimal.ROUND_HALF_UP));
        //产品审批人身份证号
        gendto.setApproverId(dto.getApproverId());
        //产品审批人姓名
        gendto.setApproverName(dto.getApproverName());
        //产品设计人身份证号
        gendto.setDesignerId(dto.getDesignerId());
        //产品设计人姓名
        gendto.setDesignerName(dto.getDesignerName());
        //投资经理身份证号
        gendto.setManagerId(dto.getManagerId());
        //投资经理姓名
        gendto.setManagerName(dto.getManagerName());
        //业务联络人姓名
        gendto.setLiaisonName(dto.getLiaisonName());
        //业务联络人座机
        gendto.setLiaisonTel(dto.getLiaisonTel());
        //业务联络人手机
        gendto.setLiaisonMobile(dto.getLiaisonMobile());
        //业务联络人邮箱
        gendto.setLiaisonEmail(dto.getLiaisonEmail());
        //产品收益类型
        gendto.setProfitType(dto.getProfitType());
        //产品期限
        gendto.setTermtype(dto.getTermtype());
        //资金投向地区
        gendto.setInvestArae(dto.getInvestArae());
        //产品投资国家或地区
        gendto.setInvestCountry(dto.getInvestCountry());
        //理财业务服务模式
        gendto.setFinanSerModel(dto.getFinanSerModel());
        //产品运作模式
        gendto.setOperateMode(dto.getOperateMode());
        //产品资产配置方式
        gendto.setCmmode(dto.getCmmode());
        //产品管理模式
        gendto.setPmmode(dto.getPmmode());
        //实际管理人员名称
        gendto.setMname(dto.getMname());
        //产品定价方式
        gendto.setPricedMode(dto.getPricedMode());
        //合作模式
        gendto.setContMode(dto.getContMode());
        //合作机构名称
        gendto.setCooperationName(dto.getCooperationName());
        //投资资产种类及比例
        gendto.setInvTypeRat(dto.getInvTypeRat());
        //投资者风险偏好
        gendto.setRiskType(dto.getRiskType());
        //产品销售区域
        gendto.setSaleArea(dto.getSaleArea());
        //募集币种
        gendto.setRaiseCcy(dto.getRaiseCcy());
        //兑付本金币种
        gendto.setCashPrinCcy(dto.getCashPrinCcy());
        //兑付收益币种
        gendto.setCashProfCcy(dto.getCashProfCcy());
        //起点销售金额
        gendto.setSaleAmtStr((dto.getSaleAmtStr()==null?BigDecimal.ZERO:dto.getSaleAmtStr()).setScale(4, BigDecimal.ROUND_HALF_UP));
        //计划募集金额
        gendto.setRaiseAmtPlan((dto.getRaiseAmtPlan()==null?BigDecimal.ZERO:dto.getRaiseAmtPlan()).setScale(4, BigDecimal.ROUND_HALF_UP));
        //募集起始日期（从）
        gendto.setRaisePstrdate(dto.getRaisePstrdate());
        //募集起始日期（到）
        gendto.setRaisePenddate(dto.getRaisePenddate());
        //投资本金到账日
        gendto.setInvestPrinDate(dto.getInvestPrinDate());
        //投资收益到账日
        gendto.setInvestProfDate(dto.getInvestProfDate());
        //销售手续费
        gendto.setSaleFeeRate((dto.getSaleFeeRate()==null?BigDecimal.ZERO:dto.getSaleFeeRate()).setScale(5, BigDecimal.ROUND_HALF_UP));
        //境内托管机构名称
        gendto.setInDepName(dto.getInDepName());
        //境内托管机构代码
        gendto.setInDepCode(dto.getInDepCode());
        //境外托管机构国别
        gendto.setOutDepCtry(dto.getOutDepCtry());
        //境外托管机构名称
        gendto.setOutDepName(dto.getOutDepName());
        //托管费率
        gendto.setTruFeeRate((dto.getTruFeeRate()==null?BigDecimal.ZERO:dto.getTruFeeRate()).setScale(5, BigDecimal.ROUND_HALF_UP));
        //产品风险等级
        gendto.setRiskLevel(dto.getRiskLevel());
        //发型机构提前终止权标识
        gendto.setTermRightFlag(dto.getTermRightFlag());
        //客户赎回权标识
        gendto.setCusRedeemFlag(dto.getCusRedeemFlag());
        //产品增信标识
        gendto.setCreditFlag(dto.getCreditFlag());
        //产品增信机构类型
        gendto.setCreditType(dto.getCreditType());
        //产品增信形式
        gendto.setCreditMode(dto.getCreditMode());
        //备注
        gendto.setRemark(dto.getRemark());
        //募集方式 01公募 02私募
        gendto.setRaiseType(dto.getRaiseType());
        //投资性质 01固定收益类/02权益类/03混合类/04商品及金融衍生品类
        gendto.setIvstorientation(dto.getIvstorientation());
        //是否金融同业专属
        gendto.setExcFinSector(dto.getExcFinSector());
        //是否设置最短持有期限
        gendto.setIsShortTerm(dto.getIsShortTerm());
        //最短持有期限(天)
        gendto.setShortTerm(dto.getShortTerm());
        //最短持有期后是否自由赎回
        gendto.setIsFreeRedem(dto.getIsFreeRedem());
        //是否现金管理类
        gendto.setCashManaCate(dto.getCashManaCate());
        //业绩比较基准%
        gendto.setYield((dto.getYield()==null?BigDecimal.ZERO:dto.getYield()).setScale(5, BigDecimal.ROUND_HALF_UP));
        //投资管理费率%
        gendto.setInvestFeeRate((dto.getInvestFeeRate()==null?BigDecimal.ZERO:dto.getInvestFeeRate()).setScale(5, BigDecimal.ROUND_HALF_UP));
        // 是否合规新产品
        gendto.setIsFhxgcp(dto.getIsFhxgcp());

        return gendto;
    }
	
    /**
     * 功能:数据变更接口统一调度入口
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月9日
     * @param executeDate
     * @param prodList
     * @param assetList
     * @throws BusinessException 
     * @see com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService#execute(java.util.Date, java.util.List, java.util.List)
     */
    @Override
    public void execute(Date executeDate, List<String> prodList, List<String> assetList,Map<String,List<String>> param) throws BusinessException {
        try {
            boolean isSynchronizeTask = false;
            List<String> isSynList = param.get("isSynchronizeTask");
            if (CollectionUtils.isNotEmpty(isSynList)){
                if (DictCodeList.是否_中债_是.getValue().equals(isSynList.get(0))){
                    isSynchronizeTask = true;
                } else {
                    isSynchronizeTask = false;
                }
            }
        	generateIssueData(executeDate,prodList,isSynchronizeTask,null);
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
            throw new BusinessException(e);
        }
    }
}
