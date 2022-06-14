package com.joyintech.tams.disclosure.busicommon.jixu.framework.report.mvc;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.druid.pool.DruidDataSource;
import com.joyintech.tams.disclosure.busicommon.jixu.framework.report.ReportManager;
import com.joyintech.tams.disclosure.busicommon.jixu.framework.report.pojo.ReportInfo;
import com.joyintech.tams.disclosure.busicommon.jixu.framework.report.repository.ReportRepository;

@Controller
public class UploadControl {
	
	@Autowired
	private ReportRepository reportRepository;
	
	@Autowired
	private ReportManager reportManager;
	
	@RequestMapping(value="/reportCreate.do", method = RequestMethod.GET)
	public String reportUpload() {
		
		ReportInfo r = new ReportInfo();
		r.setReportID("2");
		r.setUri("CLGG");
		r.setDesc("Sample Report");
		r.setFolder("/ZX");
		r.setRepository("C://Reports");
		r.setParams("{'portnum':'String', 'rep_id':'String'}");
		r.setUser("jasperadmin");
		r.setPassword("jasperadmin");
		r.setDatasource("PROJECT");
		r.setGenerator("JasperSource");
		
		reportRepository.createReport(r);
		return "upload";
	}
	
	@RequestMapping(value="/reportExport.do", method = RequestMethod.GET)
	public String reportExport() {
		//ReportManager rm = new ReportManager(null);
		ReportInfo r1 = reportManager.getReportByURI("CLGG");
		
//		BasicDataSource dataSource = new BasicDataSource();
		DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        dataSource.setUrl("jdbc:db2://192.168.70.62:50000/tamsdb:currentSchema=REVEAL_MAST_TEST;");
        dataSource.setUsername("tamsinst");
        dataSource.setPassword("tamsinst");
		reportManager.setDataSource(dataSource);
		
		HashMap<String, Object> parameters1 = new HashMap<String, Object>();
		parameters1.put("portnum", "C184R0197");
		parameters1.put("rep_id", "1");
		
		reportManager.exportReport(r1, parameters1, "pdf", "C:\\Reports\\CLGG.pdf");
		
		return "upload";
	}
	
	@RequestMapping(value="/reportGen.do", method = RequestMethod.GET)
	public void reportGen(HttpServletRequest request, HttpServletResponse response) {
		//ReportManager rm = new ReportManager(null);
		ReportInfo r1 = reportManager.getReportByURI("CLGG");
		
		DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        dataSource.setUrl("jdbc:db2://192.168.70.62:50000/tamsdb:currentSchema=REVEAL_MAST_TEST;");
        dataSource.setUsername("tamsinst");
        dataSource.setPassword("tamsinst");
		reportManager.setDataSource(dataSource);
		
		HashMap<String, Object> parameters1 = new HashMap<String, Object>();
		parameters1.put("portnum", "C184R0197");
		parameters1.put("rep_id", "1");
		
		//reportManager.exportReport(r1, parameters1, "pdf", "C:\\Reports\\CLGG.pdf");
		reportManager.genReport(r1, parameters1, request, response);
	}
}

