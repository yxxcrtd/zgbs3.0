package com.joyintech.tams.svreport.pbc.prod.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.ChbOrgRegistBankCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.system.base.entity.SysInstitution;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.constant.SvrServiceApplication;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.report.common.util.SysDataUtil;
import com.joyintech.tams.report.system.instregbankcode.dao.SysInstRegBankCode001Mapper;
import com.joyintech.tams.report.system.instregbankcode.dto.SysInstRegBankCode001Dto;
import com.joyintech.tams.svreport.pbc.common.constants.PbcMonthReportConstants;
import com.joyintech.tams.svreport.pbc.common.service.PbcExportService;
import com.joyintech.tams.svreport.pbc.prod.dao.PbcReportProdDetailMapper;
import com.joyintech.tams.svreport.pbc.prod.dto.PbcMonthPrdPostionDto;
import com.joyintech.tams.svreport.pbc.prod.dto.PbcReportProdDetailDto;
import com.joyintech.tams.svreport.pbc.report.dao.PbcMonthReportMapper;
import com.joyintech.tams.svreport.pbc.report.dao.PbcReportTemplateMapper;
import com.joyintech.tams.svreport.pbc.report.dto.PbcMonthReport;
import com.joyintech.tams.svreport.pbc.report.dto.PbcReportTemplateDto;
import com.joyintech.tams.systemtools.util.CommonUtil;


/**
 * 功能:负债端报表总入口(表1.1~表1.6、表3.1~3.2)
 * @since 1.6.0
 * @version 1.6.0
 * @作者：lingxq
 * @创建时间：2019年3月4日
 * @修改记录：
 */
@Service
public abstract class PbcReportProdTotalService extends PbcExportService  {
    
    @Autowired
    private PbcReportProdDetailMapper pbcReportProdDetailMapper;
    @Autowired
    private PbcMonthReportMapper pbcMonthReportMapper;
	@Autowired
	private PbcReportTemplateMapper pbcReportTemplateMapper;
	@Autowired
    private SysInstRegBankCode001Mapper sysInstRegBankCode001Mapper;
    
    public abstract void generateReport(Date generateDate,Date reportStartDate,Date reportEndDate,
            List<PbcMonthPrdPostionDto> prodPositionList,boolean isEdit);
    
    @Override
    public Map<Object, Object> createData(Map<String,Object> param){
    	Map<Object,Object> data  = new HashMap<Object,Object>();
    	if(param.get("reportuuid")==null){
    		return data;
    	}
    	String reportuuid = param.get("reportuuid").toString();
    	PbcMonthReport pbcReport = pbcMonthReportMapper.selectByPrimaryKey(reportuuid);
        // 如果报表为空则返回data
        if (pbcReport == null){
            return data;
        }
    	pbcReport.setProdVariety(pbcReport.getProdVariety() + "-" + SysDataUtil.getNameFromDataDict(DictCode.产品品种, pbcReport.getProdVariety()));
    	data.put("report", pbcReport);
    	// 导出excel或预览时，值为0的单元格显示成0还是显示成空，读取系统参数
    	String isShowZero = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SHOW_ZERO);
    	List<PbcReportProdDetailDto> listDetail = pbcReportProdDetailMapper.selectByReportuuid(reportuuid);
    	if (!CollectionUtils.isEmpty(listDetail)){
			for(PbcReportProdDetailDto detail : listDetail){
				dealProdData(detail,isShowZero);
                // 处理数据
                if (!DictCodeListExt2.人行报表名称_资管产品只数情况统计表.getValue().equals(pbcReport.getReporttype())){
    				// 处理数据
    				dealAreaDateListForExport(detail);
				}
                data.put(detail.getPropertyCode(), detail);
			}
    	}
    	return  data;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public Map<Object, Object> createDataMS(Map<String,Object> param){
		Map<Object, Object> map= restTemplate.postForObject(
        		SvrServiceApplication.PBC_SERVICE_NAME + "/PbcMonthReportProd/createDataProd",param,
        		Map.class);
		return map;
    }

    
	/**
	 * 功能:处理查询出的产品报表明细数据
	 * @since 1.6.0
	 * @version 1.6.0
	 * @param detail
	 * @return 
	 * PbcReportAssetDebtDetailDto
	 */
	public void dealAreaDateListForExport(PbcReportProdDetailDto detail) {
		BigDecimal tenthousand = new BigDecimal(10000);
		// 人民币
		if(detail.getCny() != null){
			detail.setCny(detail.getCny().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
		//外币
//		if(detail.getForeignCcy() != null){
//			detail.setForeignCcy(detail.getForeignCcy().divide(tenthousand));
//		}
		//美元折人民币
		if(detail.getUsdToCny() != null){
			detail.setUsdToCny(detail.getUsdToCny().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
		//欧元折人民币
		if(detail.getEurToCny() != null){
			detail.setEurToCny(detail.getEurToCny().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
		//英镑折人民币
		if(detail.getGbpToCny() != null){
			detail.setGbpToCny(detail.getGbpToCny().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
		//日元折人民币
		if(detail.getJpyToCny() != null){
			detail.setJpyToCny(detail.getJpyToCny().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
		//其他币种(外币)折人民币
		if(detail.getOthToCny() != null){
			detail.setOthToCny(detail.getOthToCny().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
		//合计
		if(detail.getTotal() != null){
			detail.setTotal(detail.getTotal().divide(tenthousand, 6, BigDecimal.ROUND_HALF_UP));
		}
	}
	/**
	 * 
	 * @param generateDate
	 * @param reportStartDate
	 * @param reportEndDate
	 * @param pbcMonthReport
	 * @return
	 */
	public PbcMonthReport getReportModel(Date generateDate, 
										  Date reportStartDate, 
										  Date reportEndDate,
										  String reportType,boolean isEdit) {
		PbcMonthReport pbcMonthReport = new PbcMonthReport();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		// 期数
		pbcMonthReport.setTermid(sdf.format(reportStartDate));
		// 报表ID
        pbcMonthReport.setReportuuid(IDGenerator.generate(36));
        // 报表类型
        pbcMonthReport.setReporttype(reportType);
        // 起止日期
        pbcMonthReport.setStartdate(reportStartDate);
        pbcMonthReport.setEnddate(reportEndDate);
        // 数据日期
        pbcMonthReport.setStatusDate(generateDate);  
        // 报表频率：月报
        pbcMonthReport.setReportfrequ(PbcMonthReportConstants.REPORT_FREQU_M);


		ChbOrgRegistBankCode bankCode = CommonUtil.getUserBank();

		String code = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID);
//		String  code=CommonUtil.selectBankCode();
		String name =SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_NAME);
//		String  name=CommonUtil.selectBankCodeName();
		if(null!=bankCode){
			code = bankCode.getRegistBankCode();
			name = bankCode.getBelongInstitutionName();
		}
				// 发行机构代码取值
        pbcMonthReport.setIssuePartyId(code);
        pbcMonthReport.setIssuePartyName(name);
        pbcMonthReport.setReportOrgName(name);
        // 社会信用代码
        //pbcMonthReport.setSocialCreditCode(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_SOCIAL_CREDIT_CODE));
        // 金融机构编码
        // pbcMonthReport.setFinInstitutionCode(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
       // pbcMonthReport.setFinInstitutionCode(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_FIN_INSTITUTION_CODE));//add by zhangyf 20191024
       
        //多级法人改造start add by hxy 20200619,联社模式，在后续中修改，此处只修改银行模式
        if(!MultilevelDataAuthorityUtil.isEnabled()) {
        	//银行模式
            SysInstRegBankCode001Dto bankDto=sysInstRegBankCode001Mapper.queryForPageWithMode(null);
        	if(null!=bankDto){
            	// 社会信用代码
                pbcMonthReport.setSocialCreditCode(bankDto.getInsitituteCode());
                // 金融机构编码
            	pbcMonthReport.setFinInstitutionCode(bankDto.getRegistBankCode());
           
        	}
        }
        //多级法人改造end add by hxy 20200619
        // 用户信息
        pbcMonthReport.setOrgCode(CommonUtil.getOrgCode());
        pbcMonthReport.setCreateDept(CommonUtil.getDeptCode());
        pbcMonthReport.setCreateUser(SecurityUtil.getLoginUser()!=null?SecurityUtil.getLoginUser().getUid():"system");
        // 数据状态
        // 根据是否编辑处理数据状态
        pbcMonthReport.setGen(true);
//        if (isEdit){
//            pbcMonthReport.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
//        } else {
            // 查询库中报表的数据状态
            List<PbcMonthReport> apbcMonthReport = pbcMonthReportMapper.selectSendDataByDateAndType(DateUtil.format(reportEndDate), reportType);
            if (CollectionUtils.isEmpty(apbcMonthReport)){
                pbcMonthReport.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
            } else {// 用原报表的数据状态
                String status = DictCodeListExt2.报送数据状态_新建.getValue();
                for (PbcMonthReport report: apbcMonthReport){
                    if (DictCodeListExt2.报送状态_未报送.getValue().equals(report.getSendStatus())){
                        status = report.getSendStatus();
                    }
                }
                pbcMonthReport.setStatus(status);
            }
            // 如果只有一条已报送的数据则不生成报表
            if (!isEdit && (apbcMonthReport.size() == 1 && (DictCodeListExt2.报送状态_接口报送.getValue().equals(apbcMonthReport.get(0).getSendStatus())
                    ||DictCodeListExt2.报送状态_手工处理.getValue().equals(apbcMonthReport.get(0).getSendStatus())))){
                pbcMonthReport.setGen(false);
            }
//        }
        pbcMonthReport.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
        
		//报送时点
        //当前批次报送时点集合
		Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
        List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_人行月度统计报表.getValue(),
                											pbcMonthReport.getStatus(),
                											dateMap, 
                											pbcMonthReport.getEnddate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
        if (!StringUtil.isEmptyList(dateList)) {
            pbcMonthReport.setChbSubmitDeadline(dateList.get(0));
            pbcMonthReport.setSubmitDeadline(dateList.get(1));
        }
		pbcMonthReport.setProdVariety("01");
		pbcMonthReport.setCreateUser(CommonUtil.getUserName());
		pbcMonthReport.setUpdateUser(CommonUtil.getUserName());
		return pbcMonthReport;
	}
	
	/**
	 * 获取模板信息
	 * @param reportuuid
	 * @param reportType
	 * @param status
	 * @return
	 */
	public Map<String,PbcReportProdDetailDto> getReportDetailList(String reportuuid,String reportType,String status){
		List<PbcReportTemplateDto> templateList = pbcReportTemplateMapper.selectByReporttype(reportType, status);
		Map<String,PbcReportProdDetailDto> result = new HashMap<String,PbcReportProdDetailDto>();
		for(PbcReportTemplateDto template : templateList){
			 PbcReportProdDetailDto detail = new PbcReportProdDetailDto();
			 detail.setDetailuuid(IDGenerator.generate(36));
			 detail.setReportuuid(reportuuid);
			 detail.setPropertyCode(template.getPropertycode());
			 detail.setGrouptype(template.getGrouptype());
			 detail.setReporttype(template.getReporttype());
			 detail.setCny(BigDecimal.ZERO);
			 detail.setEurToCny(BigDecimal.ZERO);
			 detail.setUsdToCny(BigDecimal.ZERO);
			 detail.setGbpToCny(BigDecimal.ZERO);
			 detail.setJpyToCny(BigDecimal.ZERO);
			 detail.setOthToCny(BigDecimal.ZERO);
			 detail.setTotal(BigDecimal.ZERO);
			 detail.setProdcounts(0l);
		     result.put(template.getPropertycode(),detail);
		}
		return result;
	}
	
	/**
	 * 持久化数据
	 * @param reportStartDate
	 * @param reportEndDate
	 * @param pbcMonthReport
	 * @param result
	 * @param reportType
	 */
	public void insertReportAndDetails(Date reportStartDate,
									   Date reportEndDate,
									   PbcMonthReport pbcMonthReport,
									   Map<String,PbcReportProdDetailDto> result,
									   String reportType,
									   boolean insertflag){

        if (pbcMonthReport.isGen()){
    		 pbcReportProdDetailMapper.deleteByReportDate(DateUtil.format(reportStartDate),DateUtil.format(reportEndDate),reportType);
    		 PbcMonthReport param = new PbcMonthReport();
    		 param.setStartdate(reportStartDate);
    		 param.setEnddate(reportEndDate);
    		 param.setReporttype(reportType);
    		 pbcMonthReportMapper.deleteByModel(param);	 
    		 if(insertflag){
    			 pbcMonthReportMapper.insert(pbcMonthReport);	 
    		     for ( PbcReportProdDetailDto v:result.values() ) {
    		    	 pbcReportProdDetailMapper.insert(v);
    		     } 
    		 }
        }
	}
			
	/**
	 * 持久化数据
	 * @param reportStartDate
	 * @param reportEndDate
	 * @param pbcMonthReport
	 * @param result
	 * @param reportType
	 */
	public void insertReportAndDetails(Date reportStartDate,
									   Date reportEndDate,
									   PbcMonthReport pbcMonthReport,
									   String deptCode,
									   Map<String,PbcReportProdDetailDto> result,
									   String reportType,
									   boolean insertflag){

        if (pbcMonthReport.isGen()){
        	 //删除时添加机构属性,以免误删
    		 pbcReportProdDetailMapper.deleteByReportDateDept(DateUtil.format(reportStartDate),DateUtil.format(reportEndDate),reportType,deptCode);
    		 PbcMonthReport param = new PbcMonthReport();
    		 param.setStartdate(reportStartDate);
    		 param.setEnddate(reportEndDate);
    		 param.setReporttype(reportType);
    		 param.setDeptCode(deptCode);
    		 pbcMonthReportMapper.deleteByModel(param);	 
    		 if(insertflag){
    			 //add by leipeng 20200319  多法人改造 
    			 String uuid = IDGenerator.generate(36);
    			 pbcMonthReport.setCreateDept(deptCode);
    			 //插入时根据表所属机构关系插入机构编码
    			 pbcMonthReport.setDeptCode(deptCode);
    			 
    			 //插入所属机构数据时候查询对应机构的银行编码
		        String[] code = getRegistCode(deptCode);
		        	pbcMonthReport.setIssuePartyId(code[0]);
		        	pbcMonthReport.setIssuePartyName(code[1]);
		        	pbcMonthReport.setReportOrgName(code[1]);
		        	pbcMonthReport.setOrgCode(deptCode);
		        	//多级法人改造，add 20200620 hxy start 当机构不为空时，去查询机构登记银行代码管理中的登记银行代码
		        	if(null!=deptCode && StringUtil.isNotEmpty(deptCode)){
		        		//联社模式
		        		Map<String, Object> parammode=new HashMap<String, Object>();
		        		parammode.put("orgCode", deptCode);
		                    SysInstRegBankCode001Dto bankDto=sysInstRegBankCode001Mapper.queryForPageWithMode(parammode);
		                	if(null!=bankDto){
		                    	// 社会信用代码
		                        pbcMonthReport.setSocialCreditCode(bankDto.getInsitituteCode());
		                        // 金融机构编码
		                    	pbcMonthReport.setFinInstitutionCode(bankDto.getRegistBankCode());
		                   
		                	}
		        	}
		        	//多级法人改造，add 20200620 hxy end 当机构不为空时，去查询机构登记银行代码管理中的登记银行代码

    			 pbcMonthReport.setReportuuid(uuid);
    			 pbcMonthReportMapper.insert(pbcMonthReport);	 
    		     for ( PbcReportProdDetailDto v:result.values() ) {
    		    	 v.setReportuuid(uuid);
    		    	 pbcReportProdDetailMapper.insert(v);
    		     } 
    		 }
        }
	}	
	
	
	
	
	/**
	 * 数据计算
	 * @param dto
	 * @param ccy
	 * @param value
	 * @param rateMap
	 * @param generateDate
	 * @param source
	 */
    public void setDetailValue(PbcReportProdDetailDto dto,String ccy,BigDecimal value,Map<String, BigDecimal> rateMap,Date generateDate,String source){
        BigDecimal cny = ChbCcyConvertUtil.exchangeMoney(
        		ccy,DictCodeListExt1.币种_中债_人民币.getValue(),generateDate,value,rateMap,source);
        if(DictCodeListExt1.币种_中债_人民币.getValue().equals(ccy)){//人民币
            dto.setCny(dto.getCny().add(cny));
        }else if(DictCodeListExt1.币种_中债_美元.getValue().equals(ccy)){//美元
            dto.setUsdToCny((dto.getUsdToCny().add(cny)));
        }else if(DictCodeListExt1.币种_中债_欧元.getValue().equals(ccy)){//欧元
            dto.setEurToCny((dto.getEurToCny().add(cny)));
        }else if(DictCodeListExt1.币种_中债_英镑.getValue().equals(ccy)){//英镑
            dto.setGbpToCny((dto.getGbpToCny().add(cny)));
        }else if(DictCodeListExt1.币种_中债_日元.getValue().equals(ccy)){//日元
            dto.setJpyToCny((dto.getJpyToCny().add(cny)));
        }else{ //其他币种
            dto.setOthToCny((dto.getOthToCny().add(cny)));
        }
        //合计
        dto.setTotal(dto.getTotal().add(cny));
    }
    
    
    /**
   	 * 数据计算 (多法人处理) 同时计算汇总数据以及当前数据所属部门的数据
   	 * @param dto   汇总数据
   	 * @param deptDto  部门数据
   	 * @param ccy
   	 * @param value
   	 * @param rateMap
   	 * @param generateDate
   	 * @param source
   	 */
       public void setDetailValueAndDept(Map<String,PbcReportProdDetailDto> dto,Map<String,PbcReportProdDetailDto> deptDto,String code,
       												String ccy,BigDecimal value,Map<String, BigDecimal> rateMap,Date generateDate,String source){
       	//汇总数据计算
       	setDetailValue(dto.get(code), ccy, value, rateMap, generateDate, source);
       	//部门数据计算
       	if(null!=deptDto&&null!=deptDto.get(code)){
       		
           	setDetailValue(deptDto.get(code), ccy, value, rateMap, generateDate, source);
       	}

       }
    
    
    /**
     * @Description 导出数据处理
     * @param param
     * @param data
     * @return void
     * @version 1.7 2019年5月13日
     */
    public void createDataForExp(Map<String,Object> param,Map<Object, Object> data){
    	if(param.get("reportuuid")==null){
    		return;
    	}
    	String reportuuid = param.get("reportuuid").toString();
    	PbcMonthReport pbcReport = restTemplate.postForObject(
        		SvrServiceApplication.PBC_SERVICE_NAME + "/PbcMonthReportProd/selectPbcMonthReportByPrimaryKey",reportuuid,
        		PbcMonthReport.class);
    	pbcReport.setProdVariety(pbcReport.getProdVariety() + "-" + SysDataUtil.getNameFromDataDict(DictCode.产品品种, pbcReport.getProdVariety()));
    	data.put("report", pbcReport);
    	List<PbcReportProdDetailDto> listDetail = restTemplate.postForList(
        		SvrServiceApplication.PBC_SERVICE_NAME + "/PbcMonthReportProd/selectProdDetailByReportuuid",reportuuid,
        		PbcReportProdDetailDto.class);
    	// 导出excel或预览时，值为0的单元格显示成0还是显示成空，读取系统参数
    	String isShowZero = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_SHOW_ZERO);
    	if (!CollectionUtils.isEmpty(listDetail)){
			for(PbcReportProdDetailDto detail : listDetail){
				dealProdData(detail,isShowZero);
				// 处理数据
			    if (!DictCodeListExt2.人行报表名称_资管产品只数情况统计表.getValue().equals(pbcReport.getReporttype())){
			        dealAreaDateListForExport(detail);
			    }
				data.put(detail.getPropertyCode(), detail);
			}
    	}
    }
    
    /**
     * @Description 导出预览为零的数据显示空还是显示零
     * @param detail
     * @version 1.7 2019年7月4日
     * @param isShowZero 
     */
    private void dealProdData(PbcReportProdDetailDto detail, String isShowZero) {
    	// 显示成零
		if("01".equals(isShowZero)){
			if(detail.getCny()==null){
				detail.setCny(BigDecimal.ZERO);
			}
			if(detail.getUsdToCny()==null){
				detail.setUsdToCny(BigDecimal.ZERO);
			}
			if(detail.getEurToCny()==null){
				detail.setEurToCny(BigDecimal.ZERO);
			}
			if(detail.getGbpToCny()==null){
				detail.setGbpToCny(BigDecimal.ZERO);
			}
			if(detail.getJpyToCny()==null){
				detail.setJpyToCny(BigDecimal.ZERO);
			}
			if(detail.getOthToCny()==null){
				detail.setOthToCny(BigDecimal.ZERO);
			}
			if(detail.getTotal()==null){
				detail.setTotal(BigDecimal.ZERO);
			}
			if(detail.getProdcounts()==null){
				detail.setProdcounts(new Long(0));
			}
		} else if("02".equals(isShowZero)){
			if(detail.getCny()!=null&&(BigDecimal.ZERO).compareTo(detail.getCny())==0){
				detail.setCny(null);
			}
			if(detail.getUsdToCny()!=null&&(BigDecimal.ZERO).compareTo(detail.getUsdToCny())==0){
				detail.setUsdToCny(null);
			}
			if(detail.getEurToCny()!=null&&(BigDecimal.ZERO).compareTo(detail.getEurToCny())==0){
				detail.setEurToCny(null);
			}
			if(detail.getGbpToCny()!=null&&(BigDecimal.ZERO).compareTo(detail.getGbpToCny())==0){
				detail.setGbpToCny(null);
			}
			if(detail.getJpyToCny()!=null&&(BigDecimal.ZERO).compareTo(detail.getJpyToCny())==0){
				detail.setJpyToCny(null);
			}
			if(detail.getOthToCny()!=null&&(BigDecimal.ZERO).compareTo(detail.getOthToCny())==0){
				detail.setOthToCny(null);
			}
			if(detail.getTotal()!=null&&(BigDecimal.ZERO).compareTo(detail.getTotal())==0){
				detail.setTotal(null);
			}
			if(detail.getProdcounts()!=null&&(new Long(0)).compareTo(detail.getProdcounts())==0){
				detail.setProdcounts(null);
			}
		}
	}

	public PbcMonthReport selectPbcMonthReportByPrimaryKey(String reportuuid){
    	return pbcMonthReportMapper.selectByPrimaryKey(reportuuid);
    }
    
    public List<PbcReportProdDetailDto> selectProdDetailByReportuuid(String reportuuid){
    	return pbcReportProdDetailMapper.selectByReportuuid(reportuuid);
    }
    
    
    public String[] getRegistCode(String orgCode){
        String issuePartyId = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID);
//		String issuePartyId =  CommonUtil.selectBankCode();
        String  issuePartyName = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_NAME);
//		String  issuePartyName = CommonUtil.selectBankCodeName();
        // 发行机构代码取值
        if(null!=orgCode){
        	Map<String,Object> param = new HashMap<>();
        	List<String> orgCodeList = new ArrayList<>();
        	orgCodeList.add(orgCode);
        	param.put("orgCodeList", orgCodeList);
        	List<SysInstRegBankCode001Dto> list = sysInstRegBankCode001Mapper.queryForPage(param);
        	
        	if(null!=list&&list.size()>0){
        		issuePartyId = list.get(0).getRegistBankCode();
        		SysInstitution dtp = (SysInstitution) CommonUtil.queryCode(GlobalConstant.SYS_INSTITUTION_KEY,orgCode);

        		issuePartyName =  null!=dtp?dtp.getInstitutionName():issuePartyName;
        	}
        }
		return new String[]{issuePartyId,issuePartyName};
	}
    
}
