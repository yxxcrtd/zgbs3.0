package com.joyintech.tams.svreport.chb.monthreport.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.entity.SysInstitution;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.ChbDeadlineUtil;
import com.joyintech.tams.report.system.instregbankcode.dao.SysInstRegBankCode001Mapper;
import com.joyintech.tams.report.system.instregbankcode.dto.SysInstRegBankCode001Dto;
import com.joyintech.tams.svreport.chb.monthreport.dao.ChbMonthReportMapper;
import com.joyintech.tams.svreport.chb.monthreport.dto.ChbMonthReport;
import com.joyintech.tams.svreport.chb.monthreport.dto.ChbMonthReportPrdConstants;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;




@Transactional(rollbackFor = Exception.class)
@Service
public class MonthReportServiceUtil {
	
	
	@Autowired
    private ChbMonthReportMapper chbMonthReportMapper;
	
	@Autowired
    private SysInstRegBankCode001Mapper sysInstRegBankCode001Mapper;
	/**
	 * 根据传入参数实时新建对应表结构 并插入数据主表数据
	 * @param report
	 * @param ifTrigger
	 * @return
	 */
	public ChbMonthReport buildNewReport(ChbMonthReport report,boolean ifTrigger){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        ChbMonthReport chbMonthReport = new ChbMonthReport();
        chbMonthReport.setTermid(sdf.format(report.getStartdate()));
        //报表ID
        chbMonthReport.setReportuuid(UUID.randomUUID().toString());
        
        //报表类型
        chbMonthReport.setReporttype(ChbMonthReportPrdConstants.REPORT_TYPE_MONTH);

        //起止日期
        chbMonthReport.setStartdate(report.getStartdate());
        chbMonthReport.setEnddate(report.getEnddate());
        
        //数据日期：批处理日期
        chbMonthReport.setStatusDate(report.getStatusDate());  
        
        //报表频率：月报
        chbMonthReport.setReportfrequ(ChbMonthReportPrdConstants.REPORT_FREQU_M);
        
        String[] code = getRegistCode(report.getOrgCode());
        
        chbMonthReport.setIssuePartyId(code[0]);
        chbMonthReport.setIssuePartyName(code[1]);

        //用户信息
//        chbMonthReport.setOrgCode(CommonUtil.getOrgCode());
        chbMonthReport.setOrgCode(report.getOrgCode());
        chbMonthReport.setCreateDept(CommonUtil.getDeptCode());
        chbMonthReport.setCreateUser(CommonUtil.getUserName());
        chbMonthReport.setUpdateUser(CommonUtil.getUserName());
        
        //数据状态
        chbMonthReport.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
        chbMonthReport.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
        //报送时点
        //当前批次报送时点集合
        Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
        List<Date> dateList = ChbDeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_银行月度统计表.getValue(),
        		chbMonthReport.getStatus(),dateMap, chbMonthReport.getEnddate());
		if (!StringUtil.isEmptyList(dateList)) {
			chbMonthReport.setChbSubmitDeadline(dateList.get(0));
			chbMonthReport.setSubmitDeadline(dateList.get(1));
		}
		//插入月报主表数据
		List<ChbMonthReport> ybs=chbMonthReportMapper.selectSendDataByTermId(chbMonthReport.getTermid(),chbMonthReport.getOrgCode(),null);
		 calendar.setTime(report.getStartdate());
		  ChbMonthReport chbDelReport = new ChbMonthReport();
		  chbDelReport.setTermid(chbMonthReport.getTermid());
		  chbDelReport.setOrgCode(chbMonthReport.getOrgCode());
		//查询未报送
		List<ChbMonthReport> wbs=chbMonthReportMapper.selectUnSendDataByTermId(chbMonthReport.getTermid());
		// 生成的数据已报送则不重新生成
		if ( calendar.get(Calendar.DATE)==1 &&ybs!=null&&!ybs.isEmpty()&&(wbs==null||wbs.isEmpty())&&!ifTrigger) {
			return null;
		}
		if(wbs!=null&&!wbs.isEmpty()){
			report.setWbsreportuuid(wbs.get(0).getReportuuid());
		}
		if(ybs!=null&&!ybs.isEmpty()){//有已报送数据 不作处理
//			// 如果有已登记的资产要素则抛异常
//			throw new BatchException("存在已报送的期次:"+chbMonthReport.getTermid()+"\n");
			//增加留痕处理  已报送后可生成修改未报送 
			if(wbs!=null&&!wbs.isEmpty()){
				chbDelReport.setReportuuid(wbs.get(0).getReportuuid());
				chbMonthReportMapper.deleteByModel(chbDelReport);
			}
			chbMonthReport.setStatus("02");//修改
//			return ;
//			for(ChbMonthReport single:ybs){
//				ChbMonthReport upd=new ChbMonthReport();
//				upd.setReportuuid(single.getReportuuid());
//				upd.setStatus(DictCodeList.报送数据状态_删除.getValue());
//				chbMonthReportMapper.updateByPrimaryKeySelective(upd);
//			}
		}else{
			chbMonthReportMapper.deleteByModel(chbDelReport);
		}
		chbMonthReportMapper.insert(chbMonthReport);
		return chbMonthReport;
	}
	
	/**
	 * 获取对应机构未发送的月报数据 
	 * @param termid 月度
	 * @param orgCode 机构编号
	 * @return
	 */
	public ChbMonthReport queryMonthReport(String termid,String orgCode,String sendStatus){
		List<ChbMonthReport> ybs=chbMonthReportMapper.selectSendDataByTermId(termid,orgCode,sendStatus);
		ChbMonthReport report = null;
		
		if(null!=ybs&&ybs.size()>0){
			report = ybs.get(0);
		}
		
		return report;

	}
	
	
	
	/**
	 * 获取对应机构未发送的月报数据 
	 * @param termid 月度
	 * @param orgCode 机构编号
	 * @return
	 */
	public ChbMonthReport queryMonthReportForAsset(String termid,String orgCode){
		List<ChbMonthReport> ybs=chbMonthReportMapper.selectDataByTermIdForAsset(termid,orgCode);
		ChbMonthReport report = null;
		
		if(null!=ybs&&ybs.size()>0){
			report = ybs.get(0);
		}
		
		return report;

	}
	

	
	public String[] getRegistCode(String orgCode){
        String issuePartyId = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID);
//		String issuePartyId =CommonUtil.selectBankCode();
        String  issuePartyName = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_NAME);
//		String  issuePartyName=CommonUtil.selectBankCodeName();
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
