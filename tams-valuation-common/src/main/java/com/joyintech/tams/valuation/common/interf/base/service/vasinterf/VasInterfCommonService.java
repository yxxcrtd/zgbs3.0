package com.joyintech.tams.valuation.common.interf.base.service.vasinterf;


import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Element;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.common.interf.base.constant.DictCodeInterfList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.send.dto.ValRecvServerInfoEntity;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.dao.ValServerDetailMapper;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.model.Body;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.model.Field;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.model.Header;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.model.Info;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.model.Transaction;
import com.joyintech.tams.valuation.common.interf.base.service.vasinterf.model.ValInertfBuildXml001Dto;
import com.joyintech.tams.valuation.common.interf.base.util.DbUtil;
import com.joyintech.tams.valuation.common.interf.transdata.service.HqTemplateCommonService;

import oracle.jdbc.OracleTypes;

/**
 * VAS批量生成文件
 * 
 * @author ex_kjkfb_cuiwx
 * @date 2018/07/30
 *
 * @emp:name
 * @emp:states 0=正常;-1=异常;
 * @emp:document
 */
@Service
@Scope("prototype")
public class VasInterfCommonService{
	
	private static Logger log = LoggerFactory.getLogger(VasInterfCommonService.class);

	private static String TEMPLATE_PATH = "com/joyintech/interf/common/base/service/vasinterf/template/";
	private static String PROCEDURE_PREFIX = "PKG_TCMS_VAS.";
	private String filePath ;

	
	private static Map<String, Info> infoTemplateCache = new HashMap<String, Info>();
	
	
	//返回信息
	//private static Map<String, String> resultMap;
	//StringBuffer strBuffer=new StringBuffer();
	private static List<String> strList;
	//返回信息
	private static List<ValInertfBuildXml001Dto> ValInertfBuildXml001DtoList;	
	
	@Autowired
    private ValServerDetailMapper mapper;

	@ResponseBody
    @RequestMapping(InterfCommonConstant.组装XMl文件)
	public  List<ValInertfBuildXml001Dto> execute(String startDa,String endDa) throws SQLException, ClassNotFoundException {
		Map<String, Object> params = new HashMap<>();
		params.put(InterfCommonConstant.接口_对接系统ID, DictCodeInterfList.接口对接系统ID_资管.getValue());
		List<com.joyintech.tams.valuation.common.interf.base.service.send.dto.ValRecvServerInfoEntity> recvServerList = mapper.getValServerInfo(params);
		
		if(recvServerList.size()==1){
			strList=new ArrayList<String>();
			ValInertfBuildXml001DtoList= new ArrayList<ValInertfBuildXml001Dto>();
			ValRecvServerInfoEntity recv = recvServerList.get(0);
			filePath = recv.getRemotePath();
			DbUtil db = new DbUtil(recv.getServerIp(), recv.getServerPort(), recv.getServerName(), recv.getUserName(), recv.getPassword());
	        db.init();
			Connection conn = null;
			try {
				conn = db.getDbConnection();
				if(null==conn){
					strList.add("组装xml文件时数据库连接失败 请联系管理员配置相关数据库配置!");
					log.error("组装xml文件时数据库连接失败：" + startDa + "至" +endDa + "文件生成失败！");
				}
				if(null!=conn){
					// 获取时间参数，默认为平台日期
				/*	Date startDate = new Date(DateUtil.parse(startDa).getTime());
					if (startDate == null)
						startDate = new Date(DateUtil.now().getTime());
					Date endDate = new Date(DateUtil.parse(endDa).getTime());
					if (endDate == null)
						endDate = startDate;*/
					Date startDate;
					Date endDate;
					if(DateUtil.parse(startDa)==null){
						startDate = new Date(DateUtil.now().getTime());
					}else{
						startDate = new Date(DateUtil.parse(startDa).getTime());
					}
					if(DateUtil.parse(endDa)==null){
						endDate = startDate;
					}else{
						endDate = new Date(DateUtil.parse(endDa).getTime());
					}
					generate(conn, startDate, endDate);
					ValInertfBuildXml001Dto dto = new ValInertfBuildXml001Dto();
		    		dto.setErrorMessage("组装xml文件时数据库连接成功，但是未查询到相关数据！");
		    		ValInertfBuildXml001DtoList.add(dto);
				}
			} catch (Exception e) {
				strList.add("Error:-------------生成文件异常!"+e.getMessage());
				log.error("生成文件异常", e);
			} finally {
				if (db != null){
					db.release();	
				}
				if (conn != null){
					conn.close();
				}
			}
		}else{
			strList.add( "Error:-------------获取ValRecvServerInfo数据源异常  请联系管理员！"+"共获取到"+recvServerList.size()+"条!"+"\n");
			log.error("获取ValRecvServerInfo数据源异常,"+"共获取到"+recvServerList.size()+"条!");
		}					
		return ValInertfBuildXml001DtoList;
	}

	/**
	 * 生成文件
	 * 
	 * @param conn
	 * @param startDate
	 * @param endDate
	 */
	private void generate(Connection conn, Date startDate, Date endDate) {
		File destDir = new File(filePath, new SimpleDateFormat("yyyyMMdd").format(endDate));
		if (!destDir.exists())
			destDir.mkdirs();
		else // 全量文件需要删除，增量不用
			for (File file : destDir.listFiles())
				file.delete();

		log.info(new StringBuilder("开始处理：").append("startDate=").append(startDate).append(", endDate=")
				.append(endDate).toString());

		// 基础信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_ACCOUNTINFO", "ValAccountInfo","账户信息 "); // 账户信息 1
		generate(conn, startDate, endDate, destDir, "P_QUERY_BDACCOUNTINFO", "ValSubjectInfo","标的账户信息 "); // 标的账户信息 2
		// generate(conn, startDate, endDate, destDir, "P_QUERY_ORGINFO", "ValAgencyInfo","机构信息"); // 机构信息 3
		// generate(conn, startDate, endDate, todayDir, "P_QUERY_HOLIDAYINFO", "ValHolidayInfo","节假日信息"); // 节假日信息
		// 净值型产品
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_INFO", "ValPrdInfoN","净值型产品信息 "); // 净值型产品信息 4
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_FEEINFO", "ValPrdFeeInfoN","净值型产品费用基础信息"); // 净值型产品费用基础信息 5
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_FLOATFEE", "ValPrdFloatFeeN","净值型产品浮动管理费计提规则"); // 净值型产品浮动管理费计提规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_FLOATFEEDETAIL", "ValPrdFloatFeeDetailN","净值型产品浮动管理费计提明细规则"); // 净值型产品浮动管理费计提明细规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_FEERATEADJUST", "ValPrdRateAdjustN","净值型产品费率调整信息 "); // 净值型产品费率调整信息 6
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_DURATION", "ValPrdPeriodDealN","净值型产品交易存续期交易"); // 净值型产品交易存续期交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_BONUSDJ", "ValPrdDelDealN","净值型产品分红方案登记");// 净值型产品分红方案登记
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_BONUSDEAL", "ValPrdBonusDealN","净值型产品分红交易");// 净值型产品分红交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_NPROD_DEAL_FEEDEAL", "ValPrdFeeDealN","净值型产品交易费用交易"); // 净值型产品交易费用交易 7
		// 货币型产品
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_INFO", "ValPrdInfoC","货币型产品信息 "); // 货币型产品信息 8
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_FEEINFO", "ValPrdFeeInfoC","货币型产品费用基础信息"); // 货币型产品费用基础信息 9
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_FLOATFEE", "ValPrdFloatFeeC","货币型产品浮动管理费计提规则"); // 货币型产品浮动管理费计提规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_FLOATFEEDETAIL", "ValPrdFloatFeeDetailC","货币型产品浮动管理费计提明细规则"); // 货币型产品浮动管理费计提明细规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_FEERATEADJUST", "ValPrdRateAdjustC","货币型产品费率调整信息"); // 货币型产品费率调整信息 10
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_DURATION", "ValPrdPeriodDealC","货币型产品交易存续期交易"); // 货币型产品交易存续期交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_BONUSDEAL", "ValPrdBonusDealC","货币型产品分红交易");// 货币型产品分红交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_CPROD_DEAL_FEEDEAL", "ValPrdFeeDealC","货币型产品交易费用交易"); // 货币型产品交易费用交易 11
		// ValPrdOpenPlanC货币型产品开放计划
		// ValPrdPayPlanC货币型产品付费计划
		// ValPrdFeeCalPlanC货币型产品费用计提计划
		// 债券
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECINFO", "ValSecBaseInfo","债券基础信息"); // 债券基础信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECPAYRULE", "ValSecPayRateRule","债券还本付息规则"); // 债券还本付息规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECPAYPLAN", "ValSecPayRatePlan","债券还本付息计划"); // 债券还本付息计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECDEAL", "ValSecVestAssetDeal","债券买卖交易"); // 债券买卖交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECVESPERIOD", "ValSecVestPeriod","债券存续期交易"); // 债券存续期交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECRECTRADE", "ValSecReceicableTrade","债券应收款项转让交易"); // 债券应收款项转让交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_SEC_FEERATEADJUST", "ValSecRateAdjust","债券利率调整信息"); // 债券利率调整信息
		
		// 逾期交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FXDEAL", "ValFxDeal","逾期交易"); // 逾期交易
		
		
		// 回购
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECREPOINFO", "ValRepDealSecSubject","债券回购标的"); // 债券回购标的
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECREPODEAL", "ValRepDealSec","债券回购交易"); // 债券回购交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_SECREPOASST", "ValRepDealAsset","债券回购质押品信息"); // 债券回购质押品信息
		// 拆借、同业借款、特殊目的载体
		generate(conn, startDate, endDate, destDir, "P_QUERY_DLMINFO", "ValDlTarget","拆借标的信息"); // 拆借标的信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_DLMDEAL", "ValDlDeal","拆借交易"); // 拆借交易
		// 同业借款、特殊目的载体
		generate(conn, startDate, endDate, destDir, "P_QUERY_DLRATEDEAL", "ValDlRateDeal","拆借收息"); // 拆借收息
		generate(conn, startDate, endDate, destDir, "P_QUERY_DLRATERULE", "ValDlTargetRateRule","拆借付息规则"); // 拆借付息规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_DLRATEPLAN", "ValDlTargetRatePlan","拆借付息计划"); // 拆借付息计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_DLRECTRADE", "ValDlReceicableTrade","拆借应收款项转让交易"); // 拆借应收款项转让交易
		// 存放同业
		generate(conn, startDate, endDate, destDir, "P_QUERY_BOTASTINFO", "ValBotAstInfo","存款类资产基础信息"); // 存款类资产基础信息
	    generate(conn, startDate, endDate, destDir, "P_QUERY_BOTASRATEADJUST", "ValBotAstRateAdjust","存款类资产利率调整"); // 存款类资产利率调整
	    generate(conn, startDate, endDate, destDir, "P_QUERY_BOTASREPAYCAPINT", "ValBotAstRepayCapInt","存款类资产还本付息规则"); // 存款类资产还本付息规则
	    generate(conn, startDate, endDate, destDir, "P_QUERY_BOTASCAPPLAN", "ValBotAstCapPlan","存款类资产还本计划"); // 存款类资产还本计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_BOTASTRATEPLAN", "ValBotAstRatePlan","存款类资产付息计划"); // 存款类资产付息计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_BOTASTTRADE", "ValBotAstTrade","存款交易"); // 存款交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_BOTPERIODFUND", "ValBotPeriodFund","存款类资产存续期交易"); // 存款类资产存续期交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_BOTRECTRADE", "ValBotReceicableTrade","存款类资产应收款项转让交易"); // 存款类资产应收款项转让交易
		// 货币基金
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDINFO", "ValFundMnfBaseInfo","货币基金基础信息"); // 货币基金基础信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDPAYRULE", "ValFundMnfPayRule","货币基金分红规则"); // 货币基金分红规则
		// generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDPAYPLAN", "ValFundMnfPayPlan","货币基金分红计划"); // 货币基金分红计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDTRADEB", "ValFundMnfRedAffirm","货币基金赎回确认交易"); // 货币基金赎回确认交易
		// 净值型资产
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDINFON", "ValFundNBaseInfoN","净值型基金基础信息"); // 净值型基金基础信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDTRADENB", "ValFundNRedAffirmN","净值型基金赎回确认交易"); // 净值型基金赎回确认交易
		// 货币基金、净值型资产
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDTRADEA", "ValApplyPurTade","基金申购申请交易"); // 基金申购申请交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDTRADES", "ValAffirmPurTrade","基金申购确认交易"); // 基金申购确认交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDRED", "ValRedAccoutTrade","基金赎回到账交易"); // 基金赎回到账交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDTRADER", "ValCashBonusTrade","基金现金分红交易"); // 基金现金分红交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDTRADET", "ValBonusResTrade","基金红利再投交易"); // 基金红利再投交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDCHANGE", "ValFundChangeDeal","基金转换交易"); // 基金转换交易

		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDINFOX", "ValMngAssetInfoN","计划类资产（净值型）基础信息"); // 计划类资产（净值型）基础信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_FUNDRATE", "ValNfundRate","委外(非公募基金）行情"); // 委外(非公募基金）行情
		
		// 其它交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_CURRENTDEPOSITRATE", "ValCurrentDepositRate","活期存款利率"); // 活期存款利率
		generate(conn, startDate, endDate, destDir, "P_QUERY_CURRENTDEPOSITDEALINT", "ValCurrentDepositDealInt","活期存款收息交易"); // 活期存款收息交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_TAXDEAL", "ValAddValueTaxDeal","增值税交易"); // 增值税交易
		
		// 费用交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_FEE_FEEDEAL", "ValFeeDeal","费用交易"); // 费用交易
	
		//预期收益型产品
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_INFO", "ValPrdBaseInfo","预期收益型产品基础信息"); 
		//generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_OPENPLAN", "ValPrdOpenPlan","预期收益型产品开放计划"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_SCHEDULE_GZ", "ValPrdPayRule","预期收益型产品付息规则"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_SCHEDULE_PLAN", "ValPrdAccrualPlan","预期收益型产品付息计划"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_FEEINFO", "ValPrdFeeBaseinfo","预期收益型产品费用基础信息"); 
		//generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_FLOATFEE", "ValPrdFloatFee","预期收益型产品浮动管理费计提规则"); 
		//generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_FLOATFEEDETAIL", "ValPrdFloatFeeDetail","预期收益型产品浮动管理费计算明细规则"); 
		//generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_FLOATFEEPLAN", "ValPrdFeeCalPlan","预期收益型产品费用计提计划"); 
		//generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_PAYFEE_PLAN", "ValPrdPayPlan","预期收益型产品付费计划"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_FEERATEADJUST", "ValPrdRateAdjustInfo","预期收益型产品费率调整信息"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_PRICEADJUST", "ValPrdPriceAdjustInfo","预期收益型产品产品报价调整信息"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_DEAL", "ValPrdPeriodDeal","预期收益型产品交易存续期交易"); 
		generate(conn, startDate, endDate, destDir, "P_QUERY_RPROD_FEEDEAL", "ValPrdFeeDeal","预期收益型产品交易费用交易"); 
		
		//自定义、非标
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_INFO", "ValMngAssetInfo","计划类资产基础信息"); //计划类资产基础信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_RATEADJUST", "ValMngRateAdjInfo","计划类资产利率调整信息"); //计划类资产利率调整信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_REPAYRULES", "ValMngRepayCapInt","计划类资产还本付息规则"); // 计划类资产还本付息规则
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_PAYPLAN", "ValMngIntPlan","计划类资产付息计划"); // 计划类资产付息计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_REPAYPLAN", "ValMngCapPlan","计划类资产还本计划"); // 计划类资产还本计划
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_PSDEAL", "ValMngBuyTrade","计划类资产买卖交易"); //  计划类资产买卖交易
		generate(conn, startDate, endDate, destDir, "P_QUERY_PLANASSET_DURATIONDEAL", "ValMngPeriodTrade","计划类资产存续期交易"); // 计划类资产存续期交易
					
		
		//结算指令
		generate(conn, startDate, endDate, destDir, "P_QUERY_CLEANORDER", "ValCleanOrder","结算指令"); // 结算指令
		
		// 场外期权  add by tanxuan on 20200109
		generate(conn, startDate, endDate, destDir, "P_QUERY_OPTTRADESUBJECT", "ValOptTradeSubject","场外期权标的信息"); // 场外期权标的信息
		generate(conn, startDate, endDate, destDir, "P_QUERY_OTCOPTTRADE", "ValOtcOptTrade","场外期权交易"); // 场外期权交易
	}

	/**
	 * 生成文件处理
	 * 
	 * @param conn
	 * @param startDate
	 * @param endDate
	 * @param destDir
	 * @param procedureName
	 * @param templateId
	 * @return
	 */
	private boolean generate(Connection conn, Date startDate, Date endDate, File destDir, String procedureName,
			String templateId,String templateName) {
		String procedure = PROCEDURE_PREFIX + procedureName;
		log.info(new StringBuilder("处理：").append("procedure=").append(procedure).append(",templateId=")
				.append(templateId).toString());
		try {
			List<Map<String, Object>> list = queryData(conn, startDate, endDate, procedure);
			if (StringUtil.isEmptyList(list)){
				log.info("没有查到相关数据");	
				//generateFile(destDir, getTransaction(templateId, list,templateName),templateName,false);
			}
			else{
				
				generateFile(destDir, getTransaction(templateId, list,templateName),templateName);
			}
			return true;
		} catch (Exception e) {
			strList.add( "Error:-------------"+templateName+"：组装失败 请联系管理员！"+e.getMessage());
			log.error("处理失败", e);
		}
		return false;
	}

	/**
	 * 查询数据
	 * 
	 * @param conn
	 * @param startDate
	 * @param endDate
	 * @param procedureName
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, Object>> queryData(Connection conn, Date startDate, Date endDate, String procedureName) throws SQLException{
		
		CallableStatement caller=null;
		try{
			caller = conn.prepareCall(getCallName(procedureName, 5));
			caller.setDate(1, startDate);
			caller.setDate(2, endDate);
			caller.registerOutParameter(3, Types.INTEGER);
			caller.registerOutParameter(4, Types.VARCHAR);
			caller.registerOutParameter(5, OracleTypes.CURSOR);
			caller.execute();

			int result = caller.getInt(3);
			if (result == 1) {
				log.error("查询失败：" + caller.getString(4));
				return null;
			}
			return toList((ResultSet) caller.getObject(5));	
		}finally{
			if(caller!=null)
			{
				caller.close();
			}

		}
	}

	/**
	 * 获取交易数据
	 * 
	 * @param templateId
	 * @param list
	 * @return
	 */
	private Transaction getTransaction(String templateId, List<Map<String, Object>> list,String templateName) {
		Transaction transaction = getTransactionTemplate(templateId, list.size());
		List<Info> infoList = transaction.getBody().getInfo();
		List<Object> infoFieldList = getInfoTemplate(templateId,templateName).getAny();
		for (Map<String, Object> data : list)
			infoList.add(mapToInfo(infoFieldList, data,templateId,templateName));
		return transaction;
	}

	/**
	 * 获取模板
	 * 
	 * @param templateId
	 * @param count
	 * @return
	 */
	private Transaction getTransactionTemplate(String templateId, int count) {
		Transaction transaction = new Transaction();
		Header head = new Header();
		head.setDate(DateUtil.getSysDate()); // 数据日期
		head.setModel(templateId); // 接口代码
		head.setNumber(count); // 记录数
		head.setVer("1.0"); // 版本号
		transaction.setHeader(head);
		transaction.setBody(new Body());
		return transaction;
	}

	/**
	 * 获取模板
	 * 
	 * @param templateId
	 * @return
	 */
	private Info getInfoTemplate(String templateId,String templateName) {
		Info info = infoTemplateCache.get(templateId);
		if (info == null) {
			URL templateXmlURL = getClass().getClassLoader().getResource(TEMPLATE_PATH + templateId + ".xml");
			if (templateXmlURL == null){
				strList.add("Error:-------------模板缺失: " + templateName+"_"+templateId + ".xml!");
				//log.info("Error:-------------模板缺失: " + templateName+"_"+templateId + ".xml!");	
				ValInertfBuildXml001Dto dto = new ValInertfBuildXml001Dto();
				dto.setType(templateName+"_"+templateId + ".xml");
				dto.setConsole("02");
				dto.setErrorMessage("模板缺失: " + templateName+"_"+templateId + ".xml");
				ValInertfBuildXml001DtoList.add(dto);
				throw new BusinessException("模板缺失: " + templateName+"_"+templateId + ".xml!");
			}
			
			info = JAXB.unmarshal(templateXmlURL, Info.class);
			infoElementListToFieldList(info);
			infoTemplateCache.put(templateId, info);
			
		}
		return info;
	}

	/**
	 * Info ElementList to FieldList
	 * 
	 * @param info
	 */
	private void infoElementListToFieldList(Info info) {
		List<Object> fieldList = info.getAny();
		for (int i = 0; i < fieldList.size(); i++)
			fieldList.set(i, elementToField((Element) fieldList.get(i)));
	}

	/**
	 * Element to field
	 * 
	 * @param e
	 * @return
	 */
	private Field elementToField(Element e) {
		Field field = new Field();
		field.setType(e.getNodeName());
		field.setName(e.getAttribute("name"));
		field.setValue(e.getAttribute("value"));
		field.setLength(StringUtil.toInt(e.getAttribute("length")));
		field.setScale(StringUtil.toInt(e.getAttribute("scale")));
		field.setRequired(e.getAttribute("required").equals("true")?true:false);
		field.setDescription(e.getAttribute("description"));
		return field;
	}

	/**
	 * Map数据转换成info
	 * 
	 * @param fieldList
	 * @param map
	 */
	private Info mapToInfo(List<Object> fieldList, Map<String, Object> map,String templateId,String templateName) {
		Info info = new Info();
		List<Object> infoFieldList = info.getAny();
		List<String> emptyList = new ArrayList<String>();
		for (Object field : fieldList) {
			Field e = (Field) field;
			Object value = map.get(e.getValue());
			if (e.isRequired() && (value == null || value.toString().isEmpty()))
				emptyList.add(e.getName() + "(" + e.getDescription() + ")");
			else
				infoFieldList.add(newElement(e.getName(), value));
		}
		if (!emptyList.isEmpty()){
			strList.add("Error:-------------"+templateName+"_"+templateId+":"+StringUtils.join(emptyList, ", ") + " 这些值为必输项但sql查询结果值为空！");
			//log.info("Error:-------------"+templateName+"_"+templateId+":"+StringUtils.join(emptyList, ", ") + " 这些值为必输项但sql查询结果值为空！");
			ValInertfBuildXml001Dto dto = new ValInertfBuildXml001Dto();
			dto.setType(templateName+"_"+templateId+"xml");
			dto.setConsole("02");
			dto.setErrorMessage(templateName+"_"+templateId+":"+StringUtils.join(emptyList, ", ") + " 这些值为必输项但sql查询结果值为空！");
			ValInertfBuildXml001DtoList.add(dto);
			throw new BusinessException(templateName+"_"+templateId+":"+StringUtils.join(emptyList, ", ") + " 这些值为必输项但sql查询结果值为空！");
		}
		return info;
	}

	/**
	 * 新建Element
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	private Object newElement(String name, Object value) {
		String val = value == null ? ""
				: value instanceof java.util.Date ? new SimpleDateFormat("yyyy-MM-dd").format(value) : value.toString();
		return new JAXBElement<String>(new QName(name), String.class, val);
	}

	/**
	 * 生成文件
	 * 
	 * @param destDir
	 * @param transaction
	 */
	private void generateFile(File destDir, Transaction transaction,String templateName) {
		// 找出今天生成的同一模板的所有文件
		final String prefix = new StringBuilder(transaction.getHeader().getModel()).append('_')
				.append(destDir.getName()).append('_').toString();
		File[] existFiles = destDir.listFiles(new FilenameFilter() {
			public boolean accept(File file, String name) {
				return name.startsWith(prefix);
			}
		});

	/*	strList.add(templateName+prefix + num +".xml");
		strListStatus.add("01");*/	
		ValInertfBuildXml001Dto dto =new ValInertfBuildXml001Dto();
	//	if(flag==true){
			// 生成最大的文件后缀
			String num;
			if (existFiles.length == 0) {
				num = "01";
			} else {
				String snum = existFiles[existFiles.length - 1].getName().substring(prefix.length(), prefix.length() + 2);
				num = prefixZero(Integer.parseInt(snum) + 1);
			}
			File newFile = new File(destDir, prefix + num + ".xml");
			JAXB.marshal(transaction, newFile);
			dto.setType(templateName+prefix + num +".xml");
			dto.setConsole("01");
			ValInertfBuildXml001DtoList.add(dto);
			log.info("文件生成成功：" + newFile.getAbsolutePath());	
	/*	}else if(flag==false){
			// 生成最大的文件后缀
			String num;
			if (existFiles.length == 0) {
				num = "01";
			} else {
				String snum = existFiles[existFiles.length - 1].getName().substring(prefix.length(), prefix.length() + 2);
				num = prefixZero(Integer.parseInt(snum) + 1);
			}
			dto.setType(templateName+prefix + num +".xml");
			dto.setConsole("03");
			dto.setErrorMessage("没有查到相关数据");
			ValInertfBuildXml001DtoList.add(dto);
			log.info("没有查到相关数据");
		}*/
		
		
		/*	dto.setType(templateName+prefix + num +".xml");
			dto.setConsole("02");
			dto.setErrorMessage("没有查到相关数据");
			ValInertfBuildXml001DtoList.add(dto);
			log.info("没有查到相关数据");*/

		
	}

	/**
	 * 填0
	 * 
	 * @param n
	 * @return
	 */
	private String prefixZero(int n) {
		return n < 10 ? "0" + n : Integer.toString(n);
	}
	
	/**
	 * 获取存过名称
	 * 
	 * @param procedureName
	 * @param parametersCount
	 * @return
	 */
	public static String getCallName(String procedureName, int parametersCount) {
		return "{call " + procedureName + "(" + getPSParameters(parametersCount) + ")}";
	}
	
	/**
	 * 参数问号对应
	 * 
	 * @param count
	 * @return
	 */
	public static String getPSParameters(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append('?');
		}
		return sb.toString();
	}
	
	/**
	 * ResultSet转List
	 * 
	 * @param rs
	 * @return
	 */
	public List<Map<String, Object>> toList(ResultSet rs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			String[] columnNames = getColumnNames(rs);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (String columnName : columnNames)
					map.put(columnName, rs.getObject(columnName));
				list.add(map);
			}
		} catch (SQLException e) {
			strList.add( "Error:-------------数据库连接异常,请联系管理员！");
			log.error("数据库异常", e);
		} finally {
			close(rs);
		}
		return list;
	}
	
	/**
	 * 关闭数据库连接，参数有顺序：ResultSet > Statement > Connection
	 * 
	 * @param closeables
	 */
	public void close(Object... closeables) {
		for (Object closeable : closeables) {
			if (closeable != null) {
				try {
					if (closeable instanceof ResultSet)
						((ResultSet) closeable).close();
					else if (closeable instanceof Statement)
						((Statement) closeable).close();
					else if (closeable instanceof Connection)
						((Connection) closeable).close();
				} catch (Exception e) {
					strList.add( "Error:-------------关闭数据库资源异常请联系管理员！");
					log.error("关闭数据库资源异常", e);
				}
			}
		}
	}
	
	
	/**
	 * 获取结果集名称
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static String[] getColumnNames(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		String[] columnNames = new String[metaData.getColumnCount()];
		for (int i = 0; i < columnNames.length; i++)
			columnNames[i] = metaData.getColumnLabel(i + 1);
		return columnNames;
	}
}
