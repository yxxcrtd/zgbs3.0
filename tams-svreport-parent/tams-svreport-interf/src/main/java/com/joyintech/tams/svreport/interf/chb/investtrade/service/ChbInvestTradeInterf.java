package com.joyintech.tams.svreport.interf.chb.investtrade.service;

import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.BaseInterfService;
import com.joyintech.tams.common.InterfDefine;
import com.joyintech.tams.common.base.entity.ChbInvestTrade;
import com.joyintech.tams.common.base.entity.ChbOrgRegistBankCode;
import com.joyintech.tams.common.svreport.chb.constant.ChbInterfConfig;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.ChbInterfUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.report.interf.common.constant.InterfConstants;
import com.joyintech.tams.svreport.interf.chb.assetinfo.dao.ChbAssetInfoInterfMapper;
import com.joyintech.tams.svreport.interf.chb.conf.DataSourceType;
import com.joyintech.tams.svreport.interf.chb.investtrade.dao.ChbInvestTradeInterfMapper;
import com.joyintech.tams.svreport.interf.chb.investtrade.dto.MstCounterPartyDto;
import com.joyintech.tams.systemtools.util.CommonUtil;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能:资产要素表接口
 * @since 1.6.0
 * @version 1.6.0
 * @作者：weiyuqiang
 * @创建时间：2019年2月18日
 * @修改记录：
 */
@SuppressWarnings("rawtypes")
@Service
@Transactional(rollbackFor = Exception.class)
public class ChbInvestTradeInterf  extends BaseInterfService {
	
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ChbInvestTradeInterfMapper chbInvestTradeInterfMapper;
	
	@Autowired
	private ChbAssetInfoInterfMapper chbAssetInfoInterfMapper;
	
	@Autowired
	private SqlSessionTemplate template;
	
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	// 股票衍生品投资品种集合
	private String[] derivativeArr= new String[] {"25", "26", "27", "28", "29", "30"};
	

    /**
     * 功能:接入投资交易信息
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月18日
     * @param batchDate 跑批日
     * @param isSynchronizeTask 是否从轮训进入
     * @prodList 产品代码
     * void
     */
    public Map<String,List<String>> generateInvestTradeData(Date batchDate,List<String> prodList,boolean isSynchronizeTask) {
		// 查询数据
		List<ChbInvestTrade> insertDataList = getBusEntity(prodList, batchDate, null, "D");

		if(CollectionUtils.isNotEmpty(insertDataList)){
			logger.debug("上游投资交易信息插入:----开始------" +  df.format(new Date()));
			insertDataMerge(insertDataList,null);
			logger.debug("上游投资交易信息插入:----结束------" +  df.format(new Date()));
		}
		return new HashMap<>();
	}

	@Override
	public List<ChbInvestTrade> getBusEntity(List<String> prodIdlist, Date date, String institutionCode, String source) {
		// 通道交易是否穿透報送 01 是 02 否
		String isChlBtm = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_CHL_BTM);
		// 用于返回的数据
		Map<String,List<String>> mapPara = new HashMap<String,List<String>>();
		List<String> tradenoList = new ArrayList<String>();
		List<String> prodAddList = new ArrayList<String>();
		List<String> assetList = new ArrayList<String>();
		// 切换数据源查询信息
		Date lastUpdatetime = ChbInterfUtil.queryUpdateTime(ChbInterfConfig.中债资产交易信息接口.getInterId());
		//查询信息
		ChbInvestTradeInterf chbInvestTradeInterf =  SpringContextUtil.getBean(ChbInvestTradeInterf.class);
		logger.debug("上游投资交易信息查询:----开始------" +  df.format(new Date()));
		// 查询当日新增/更改交易信息
		List<ChbInvestTrade> listChbInvestTrade = chbInvestTradeInterf.queryInvestTradeByInterfdateBatch(lastUpdatetime,prodIdlist);
		logger.debug("上游投资交易信息查询:----结束------" +  df.format(new Date()));
		// 登记插入列表
		List<ChbInvestTrade> insertDataList = new ArrayList<ChbInvestTrade>();
		// 根据资产代码和资产分类：资管计划去中债要素表中查询
		List<String> assetTypeList = new ArrayList<String>();
		assetTypeList.add(DictCodeListExt2.资产_负债类别_资产管理产品.getValue());
		assetTypeList.add(DictCodeListExt2.资产_负债类别_私募基金.getValue());
		assetTypeList.add(DictCodeListExt2.资产_负债类别_委外投资_协议方式.getValue());
		// 生成日期为空防止要素（维护）变动更新日期
		List<String> assetPlanLList = chbAssetInfoInterfMapper.selectAssetCodeByAssetType(assetTypeList, null);
		// 开关：上游资产交易数据接入是否自动生成产品买通道交易(01：自动生成 02：不自动生成)
		String isAutoGeneChlDeal = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_AUTOGENE_CHLDEAL);
		// 发行机构代码
		String issueId = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID);
//		String issueId = CommonUtil.selectBankCode();
		Map<String,ChbInvestTrade> spAssetMap=new HashMap<String,ChbInvestTrade>();
		if(DictCodeList.是否_中债_否.getValue().equals(isChlBtm)){
			String isCalLoadRate =  SvrDataUtil.getSysParam("IS_CAL_LOADRATE");
			List<ChbInvestTrade> spAssetList=chbInvestTradeInterf.selectSpAssetChl(isCalLoadRate);
			spAssetMap=spAssetList.stream().collect(
					JoyinCollectors.toMap(
							x -> x.getAssetCode() , x -> x));
		}
		// 汇率信息放至最外层，每次计算折算人民币，先查rateMap
		Map<String, BigDecimal> rateMap = new HashMap<>();

		// 查询交易对手
		List<MstCounterPartyDto> counterList = chbInvestTradeInterfMapper.selectAllTradeCounter();
		// 内部交易对手集合
		Map<String,String> inSidecounterMap = new HashMap<String,String>();
		if (!CollectionUtils.isEmpty(counterList)){
			for(MstCounterPartyDto counter : counterList){
				if("05".equals(counter.getCounterType())){//交易对手类型为05的是内部交易对手
					inSidecounterMap.put(counter.getCounterId(),counter.getCounterName());
				}
			}
		}


		// 计数用于计算折算人民币次数
		int count = 1;
		for(ChbInvestTrade investDeal : listChbInvestTrade){
			logger.debug("上游投资交易数据处理测试:001---" +  df1.format(new Date()));
			// 外汇远期到期收益交易发生金额取值 add by gaojian 2019-07-23 begin
			if (StringUtil.equals("F12", investDeal.getInvestType()) &&
					StringUtil.equals("09", investDeal.getCurAssetType())) {
				// 计算发生金额
				BigDecimal incrredAmt = chbInvestTradeInterf.calcIncurredAmt(investDeal, rateMap);
				// 发生金额小于0，流动资金类型设置为"10"
				if (incrredAmt.compareTo(BigDecimal.ZERO) < 0) {
					investDeal.setCurAssetType("10");
				}
				// 设置发生金额为计算结果绝对值
				investDeal.setAmt(incrredAmt.abs());
			}
			// 投资类型置空, 资金流动类型说明设为 '无'
			if (StringUtil.equals("F12", investDeal.getInvestType())) {
				investDeal.setInvestType(null);
				investDeal.setCurAssetdetType("无");
			}
			// 外汇远期到期收益交易发生金额取值 add by gaojian 2019-07-23 end
			// 外汇掉期收益交易发生金额取值 add by gaojian 2019-07-23 begin
			if (StringUtil.equals("F13", investDeal.getInvestType()) &&
					StringUtil.equals("09", investDeal.getCurAssetType())) {
				// 计算发生金额
				BigDecimal incrredAmt = chbInvestTradeInterf.calcIncurredAmt(investDeal, rateMap);
				// 发生金额小于0，流动资金类型设置为"10"
				if (incrredAmt.compareTo(BigDecimal.ZERO) < 0) {
					investDeal.setCurAssetType("10");
				}
				// 设置发生金额为计算结果绝对值
				investDeal.setAmt(incrredAmt.abs());
			}
			// 投资类型置空, 资金流动类型说明设为 '无'
			if (StringUtil.equals("F13", investDeal.getInvestType())) {
				investDeal.setInvestType(null);
				investDeal.setCurAssetdetType("无");
			}
			// 外汇掉期收益交易发生金额取值 add by gaojian 2019-07-23 end
			//新的最后更新时间
			if (investDeal.getUpdateTime() != null
					&& lastUpdatetime.compareTo(investDeal.getUpdateTime()) < 0){
				lastUpdatetime = investDeal.getUpdateTime();
			}
			// 填充默认值
			investDeal.setTradeSeqno(IDGenerator.generate(36));
//    		investDeal.setStatus(DictCodeListExt1.维护数据状态_有效.getValue());
			//发行机构代码取值
			investDeal.setRegistBankCode(issueId);
			// 数据日期取原始数据日期
//    		investDeal.setStatusDate(batchDate);
			investDeal.setOrgCode(CommonUtil.getOrgCode());
			investDeal.setDeptCode(CommonUtil.getDeptCode());
			investDeal.setStatusDate(date);
			logger.debug("上游投资交易数据处理测试:002---" +  df1.format(new Date()));
			// 单位净价、单位全价保留四位小数
			if (investDeal.getUnitPriceNet() != null){
				investDeal.setUnitPriceNet(investDeal.getUnitPriceNet().setScale(4,BigDecimal.ROUND_HALF_EVEN));
			}
			if (investDeal.getUnitPriceFull() != null){
				investDeal.setUnitPriceFull(investDeal.getUnitPriceFull().setScale(4,BigDecimal.ROUND_HALF_EVEN));
			}
			// 到期收益率保留5位有效数字，对应数据库7位数字
			if (investDeal.getEndRate() != null){
				investDeal.setEndRate(ArithmeticUtil.multiply(investDeal.getEndRate(), BigDecimal.valueOf(100)).setScale(5,BigDecimal.ROUND_HALF_EVEN));
			}
			logger.debug("上游投资交易数据处理测试:003---" +  df1.format(new Date()));
			// 类型说明为“无”
			if (StringUtil.isEmpty(investDeal.getCurAssetdetType())){
				investDeal.setCurAssetdetType("无");
			}
			// 交易对手为“无”
			if (StringUtil.isEmpty(investDeal.getTradeCounter())){
				investDeal.setTradeCounter("无");
			}
			// 每一千次记日志
			if (count %1000 == 0){
				logger.debug("上游投资交易信息折算人民币第"+count+"次:----开始------" +  df.format(new Date()));
			}
			logger.debug("上游投资交易数据处理测试:004---" +  df1.format(new Date()));
			// 股票、衍生品相关折算人民币金额置空 add by gaojian 2019-07-24 begin
			if (Arrays.asList(derivativeArr).contains(investDeal.getInvestVariety())) {
				investDeal.setCnyAmt(null);
			} else
				// 股票、衍生品相关折算人民币金额置空 add by gaojian 2019-07-24 end
				// 非人民币币种折算人民币金額
				if (!StringUtil.equals(investDeal.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue())) {
					investDeal.setCnyAmt(ChbCcyConvertUtil.changeCnyMoney(investDeal.getCcy(), investDeal.getAmt(), investDeal.getDealdate(), rateMap));
				} else {
					investDeal.setCnyAmt(investDeal.getAmt());
				}
			// 面额折算人民币
			if (investDeal.getFacevalue() != null){
				investDeal.setFacevalue(ChbCcyConvertUtil.changeCnyMoney(investDeal.getCcy(), investDeal.getFacevalue(), investDeal.getDealdate(), rateMap));
			}
			// 设置小数位数，金额数量俩位
			if (investDeal.getAmt() != null){
				investDeal.setAmt(investDeal.getAmt().setScale(2,BigDecimal.ROUND_HALF_EVEN));
			}
//    		// 每一千次记日志
//    		if (count %1000 == 0){
//    			logger.debug("上游投资交易信息折算人民币第"+count+"次:----结束------" +  df.format(new Date()));
//    		}
			if (investDeal.getCnyAmt() != null){
				investDeal.setCnyAmt(investDeal.getCnyAmt().setScale(2,BigDecimal.ROUND_HALF_EVEN));
			}
			if (investDeal.getNum() != null){
				investDeal.setNum(investDeal.getNum().setScale(2,BigDecimal.ROUND_HALF_EVEN));
			}
			logger.debug("上游投资交易数据处理测试:005---" +  df1.format(new Date()));

			// 处理交易类型和是否底层交易字段
			// IS_UNDER_TRADE资产交易才为底层（通道投资）,资管计划交易不为底层
			// TRADE_TYPE资产交易默认'01'，判断是否为资管计划交易,即资产一级分类为资管计划
			if (!StringUtil.isEmptyList(assetPlanLList)
					&& DictCodeListExt2.投资端交易类型_资产交易.getValue().equals(investDeal.getTradeType())){
				if (assetPlanLList.contains(investDeal.getAssetCode())){
					investDeal.setTradeType(DictCodeListExt2.投资端交易类型_资管计划交易.getValue());
//						investDeal.setIsUnderTrade(DictCodeList.是否_中债_否.getValue());
				}
			}
			logger.debug("上游投资交易数据处理测试:006---" +  df1.format(new Date()));
			// 穿透报送通道id为空，不穿透报送则存值
			if (DictCodeList.是否_中债_是.getValue().equals(isChlBtm)){
				investDeal.setChlCpctId(null);
			}
			// 通道购买资产交易需读开关：上游资产交易数据接入是否自动生成产品买通道交易(01：自动生成 02：不自动生成)
			// 判断是否自动生成产品买通道交易，bs无产品买通道交易，需自动生成
			// 资产买卖交易才自动生成
			if (DictCodeListExt2.投资端交易类型_资产交易.getValue().equals(investDeal.getTradeType())
					|| DictCodeListExt2.投资端交易类型_资管计划交易.getValue().equals(investDeal.getTradeType())){
				if(spAssetMap.containsKey(investDeal.getAssetCode())){
					investDeal.setChlCpctId(spAssetMap.get(investDeal.getAssetCode()).getChlCpctId());
					investDeal.setPayType(spAssetMap.get(investDeal.getAssetCode()).getPayType());
				}
				// 不穿透报送则生成相应的通道交易
				if (DictCodeList.是否_中债_是.getValue().equals(isAutoGeneChlDeal)
						&& !StringUtil.isEmpty(investDeal.getChlCpctId())
						&& DictCodeList.是否_中债_否.getValue().equals(isChlBtm)
						// 增加过滤条件：fin_product_add.pay_type=01逐笔划款  BSTOZ-930 zhangyf 20191029 start
						&& StringUtil.equals("01", investDeal.getPayType())
					// 增加过滤条件：fin_product_add.pay_type=01逐笔划款  BSTOZ-930 zhangyf 20191029 end
				) {
					try {
						ChbInvestTrade prodChlDeal = getProdChlDeal(investDeal);
						insertDataList.add(prodChlDeal);
						// 插入到返回的数据中
						if(!tradenoList.contains(investDeal.getTradeCode())){
							tradenoList.add(investDeal.getTradeCode());
						}
						if(!prodAddList.contains(investDeal.getProdId())){
							prodAddList.add(investDeal.getProdId());
						}
						if(!assetList.contains(investDeal.getAssetCode())){
							assetList.add(investDeal.getAssetCode());
						}
					} catch (CloneNotSupportedException e) {
						logger.error("自动生成产品买通道交易错误！");
						logger.error(e.getMessage(),e);
					}
					// 不穿透报送过滤底层交易接入
					continue;
				}
				logger.debug("上游投资交易数据处理测试:007---" +  df1.format(new Date()));
				// 红利再投生成投资所得收益
				if (DictCodeListExt2.投资类型_红利再投.getValue().equals(investDeal.getDealtype()) &&
						!Arrays.asList(derivativeArr).contains(investDeal.getInvestVariety())){
					try {
						ChbInvestTrade assetIntDeal = getAssetIntDeal(investDeal);
						insertDataList.add(assetIntDeal);
						// 插入到返回的数据中
						if(!tradenoList.contains(investDeal.getTradeCode())){
							tradenoList.add(investDeal.getTradeCode());
						}
						if(!prodAddList.contains(investDeal.getProdId())){
							prodAddList.add(investDeal.getProdId());
						}
						if(!assetList.contains(investDeal.getAssetCode())){
							assetList.add(investDeal.getAssetCode());
						}
					} catch (CloneNotSupportedException e) {
						logger.error("红利再投生成投资所得收益错误！");
						logger.error(e.getMessage(),e);
					}
				} else if (DictCodeListExt2.投资类型_现金分红.getValue().equals(investDeal.getDealtype())){
					// 数量 = 分红日产品对应持仓份额取昨日头寸资产头寸对应份额
					investDeal.setNum(investDeal.getIntPayAmt());
				}
				logger.debug("上游投资交易数据处理测试:008---" +  df1.format(new Date()));
			}
			// 是否字段
			// 是否底层交易字段
//			investDeal.setIsUnderTrade(SysDataUtil.getNameFromDataDict(DictCode.是否_中债字段映射,investDeal.getIsUnderTrade()));
//    		if (chbInvestTradeInterfMapper.selectIsExistInvestDealByTradeCode(investDeal.getAssetCode()) > 0){
//    			// 创建人默认system
//	    		investDeal.setUpdateUser(SecurityUtil.getLoginUser().getUid());
//	//        	dto.setCreateUser(SecurityUtil.getLoginUser().getUid());
//	    		investDeal.setUpdateTime(DateUtil.now());
//	    		updateDataList.add(investDeal);
//    		} else {
			// 创建人默认system
			investDeal.setCreateUser(CommonUtil.getUserName());
			//        	dto.setCreateUser(SecurityUtil.getLoginUser().getUid());
			investDeal.setCreateTime(DateUtil.now());
			investDeal.setUpdateTime(null);
			investDeal.setUpdateUser(CommonUtil.getUserName());
			// 数据来源
			investDeal.setDataSource("DB");
			insertDataList.add(investDeal);
			// 插入到返回的数据中
//			if (isSynchronizeTask){
//				if(!tradenoList.contains(investDeal.getTradeCode())){
//					tradenoList.add(investDeal.getTradeCode());
//				}
//				if(!prodAddList.contains(investDeal.getProdId())){
//					prodAddList.add(investDeal.getProdId());
//				}
//				if(!assetList.contains(investDeal.getAssetCode())){
//					assetList.add(investDeal.getAssetCode());
//				}
//			}
//			logger.debug("上游投资交易数据处理测试:009---" +  df1.format(new Date()));
//    		}
			// 若交易对手类型为05 内部交易对手，则交易对手方类型值默认为：07或13
			if(inSidecounterMap.containsKey(investDeal.getTradeCounter())){
				ChbOrgRegistBankCode chbOrgRegistBankCode = chbInvestTradeInterfMapper.selectByRegistBankCode(investDeal.getRegistBankCode());
				if(null != chbOrgRegistBankCode ) {
					// 是否理财子公司，1 是=13，2 否=07
					if(StringUtil.equals("1",chbOrgRegistBankCode.getIsFinSubCompany()))
						investDeal.setRivalPartyType("13");
					else
						investDeal.setRivalPartyType("07");
				}else{
					investDeal.setRivalPartyType("07");
				}


			}
			count++;
		}


		// 穿透报送为否，直接取通道划款和通道回款作为交易信息 zhangyf 20191023 start
		if (DictCodeList.是否_中债_否.getValue().equals(isChlBtm)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("interfDate", ChbInterfUtil.queryUpdateTime(ChbInterfConfig.中债资产交易信息接口.getInterId()));
			map.put("prodList", prodIdlist);
			List<ChbInvestTrade> chlTrans4TradeInfoList = chbInvestTradeInterfMapper.selectChlTrans4TradeInfo(map);
			if (CollectionUtils.isNotEmpty(chlTrans4TradeInfoList)) {
				for(ChbInvestTrade chbInvestTrade : chlTrans4TradeInfoList) {
					chbInvestTrade.setTradeSeqno(IDGenerator.generate(36));

					ChbOrgRegistBankCode chbOrgRegistBankCode = chbInvestTradeInterfMapper.selectByRegistBankCode(chbInvestTrade.getRegistBankCode());
					if (null != chbOrgRegistBankCode) {
						// 是否理财子公司，1 是=13，2 否=07
						if (StringUtil.equals("1", chbOrgRegistBankCode.getIsFinSubCompany()))
							chbInvestTrade.setRivalPartyType("13");
						else
							chbInvestTrade.setRivalPartyType("07");
					} else {
						chbInvestTrade.setRivalPartyType("07");
					}
					// 资金流动类型为01~08时，改字段有值，否则该字段为空；通道不穿透报送，自动生成的投资交易，资产计量方式默认为公允价值
					String[] curAssetTypeArr = {"01", "02", "03", "04", "05", "06", "07", "08"};
					if (Arrays.asList(curAssetTypeArr).contains(chbInvestTrade.getCurAssetType())) {
						chbInvestTrade.setAssetMethod(DictCodeListExt3.资产计量方式_公允价值.getValue());
					}

				}
				insertDataList.addAll(chlTrans4TradeInfoList);
			}
		}
		// 穿透报送为否，直接取通道划款和通道回款作为交易信息 zhangyf 20191023 end

//		List<BaseEntity> allData = new ArrayList<BaseEntity>();
//		if (CollectionUtils.isNotEmpty(updateDataList)) {
//			allData.addAll(updateDataList);
//			// 批量更新
//			int  singalCount = reportSelectOrDeleteCount;
//			int totalCount = updateDataList.size();
//			if(totalCount>0){
//				if(totalCount<=singalCount){
//					chbInvestTradeInterfMapper.updateBatch(updateDataList);
//				}
//				else{
//					for(int i = 0; i < totalCount/singalCount ; i++){
//						chbInvestTradeInterfMapper.updateBatch(updateDataList.subList(0, singalCount));
//						updateDataList.removeAll(updateDataList.subList(0, singalCount));
//					}
//	                if(CollectionUtils.isNotEmpty(updateDataList)){
//	                	chbInvestTradeInterfMapper.updateBatch(updateDataList);
//	                }
//				}
//			}
//		}
//		if (CollectionUtils.isNotEmpty(insertDataList)) {
////			allData.addAll(insertDataList);
//			// 批量插入
//			int  singalCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"500" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
//			int totalCount = insertDataList.size();
//			if(totalCount>0){
//				if(totalCount<=singalCount){
//			    	// 先删再插
//					chbInvestTradeInterfMapper.deleteByTradeCode(insertDataList);
//					chbInvestTradeInterfMapper.insertBatch(insertDataList);
//				}
//				else{
//					for(int i = 0; i < totalCount/singalCount ; i++){
//				    	// 先删再插
//						chbInvestTradeInterfMapper.deleteByTradeCode(insertDataList.subList(0, singalCount));
//						chbInvestTradeInterfMapper.insertBatch(insertDataList.subList(0, singalCount));
//						insertDataList.removeAll(insertDataList.subList(0, singalCount));
//					}
//	                if(CollectionUtils.isNotEmpty(insertDataList)){
//				    	// 先删再插
//						chbInvestTradeInterfMapper.deleteByTradeCode(insertDataList);
//	                	chbInvestTradeInterfMapper.insertBatch(insertDataList);
//	                }
//				}
//			}
//		}
		if(lastUpdatetime!=null){
			ChbInterfUtil.updateLastupdatetime(ChbInterfConfig.中债资产交易信息接口.getInterId(), lastUpdatetime);;
		}
		mapPara.put(InterfConstants.SVR_TRADENOLIST, tradenoList);
		mapPara.put(InterfConstants.SVR_PRODLIST, prodAddList);
		mapPara.put(InterfConstants.SVR_ASSETLIST, assetList);
		return insertDataList;
	}

	@Override
	public Class getEntityClass() {
		return ChbInvestTrade.class;
	}

	@Override
	public String getFileName() {
		return InterfDefine.投资交易信息接口数据.getCode();
	}


	/**
     * 功能:查询接入日当天的资产要素
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月18日
     * @param interfDate 跑批日
     * @prodList 产品代码
     * @return 
     * List<ChbAssetInfo>
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public List<ChbInvestTrade> queryInvestTradeByInterfdateBatch(Date interfDate,List<String> prodList){
		List<ChbInvestTrade> tradeList = new ArrayList<ChbInvestTrade>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("interfDate", interfDate);
		param.put("prodList", prodList);
		//update by leipeng 20200707 多法人数据修改
		String sql = "com.joyintech.tams.svreport.interf.chb.investtrade.dao.ChbInvestTradeInterfMapper.selectInvestTradeByInterfdateBatch";
		//如果是联社模式则调用单独的接入语句
		if(CommonUtil.isBAmodel()){
			sql = "com.joyintech.tams.svreport.interf.chb.investtrade.dao.ChbInvestTradeInterfMapper.selectInvestTradeByInterfdateBatchEx";
		}
		
        template.select(sql,
        		param,new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
            	tradeList.add((ChbInvestTrade) resultContext.getResultObject());
            }
        });
    	return tradeList;
    }
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    // 切换数据源
    @DataSource(DataSourceType.INTERF)
    public List<ChbInvestTrade> selectSpAssetChl(String isCalLoadRate){
		List<ChbInvestTrade> tradeList = new ArrayList<ChbInvestTrade>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("isCalLoadRate", isCalLoadRate);
        template.select("com.joyintech.tams.svreport.interf.chb.investtrade.dao.ChbInvestTradeInterfMapper.selectSpAssetChl",
        		param,new ResultHandler() {
            @Override
            public void handleResult(ResultContext resultContext) {
            	tradeList.add((ChbInvestTrade) resultContext.getResultObject());
            }
        });
    	return tradeList;
    }
    /**
     * 功能:根据通道买资产交易生成产品买通道交易
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月20日
     * @param assetDeal
     * @return 
     * ChbInvestTrade
     * @throws CloneNotSupportedException 
     */
    private ChbInvestTrade getProdChlDeal(ChbInvestTrade assetDeal) throws CloneNotSupportedException{
    	ChbInvestTrade prodChlDeal = new ChbInvestTrade();
    	BeanUtils.copyProperties(assetDeal,prodChlDeal);
    	prodChlDeal.setTradeSeqno(IDGenerator.generate(36));
    	// 通道代码
    	String chlId = prodChlDeal.getChlCpctId();
    	// 重新设置交易编号
    	String tradeCode = prodChlDeal.getTradeCode();
    	StringBuilder sb = new StringBuilder("CHL");
		sb.append(tradeCode);
    	// 合同编号=交易编号
		prodChlDeal.setTradeCode(sb.toString());
		if (StringUtils.isNotEmpty(prodChlDeal.getfTradeCode())){
	        prodChlDeal.setContractCode(prodChlDeal.getfTradeCode());
		} else {
	        prodChlDeal.setContractCode(prodChlDeal.getTradeCode());
		}
    	prodChlDeal.setAssetCode(chlId);
		//仅仅针对逐笔划款的通道投资债券，数量字段
		if(StringUtil.isNotEmpty(prodChlDeal.getInvestVariety()) && prodChlDeal.getInvestVariety().equals(DictCodeListExt2.投资品种_债券.getValue())
            && StringUtil.isNotEmpty(prodChlDeal.getCurAssetType()) && StringUtil.isNotEmpty(prodChlDeal.getCurAssetdetType())
				&&(("02".equals(prodChlDeal.getCurAssetType()) && "到期".equals(prodChlDeal.getCurAssetdetType()))
		           || ("11".equals(prodChlDeal.getCurAssetType()) && "部分到期".equals(prodChlDeal.getCurAssetdetType())))
		  ){
			if(prodChlDeal.getCnyAmt()!=null && prodChlDeal.getCnyAmt().compareTo(BigDecimal.ZERO)!=0){
				prodChlDeal.setNum(prodChlDeal.getCnyAmt());
			}else if(prodChlDeal.getAmt()!=null && prodChlDeal.getAmt().compareTo(BigDecimal.ZERO)!=0){
				prodChlDeal.setNum(prodChlDeal.getAmt());
			}
		}

    	// 投资品种：通道交易
    	prodChlDeal.setInvestVariety(DictCodeListExt2.投资品种_计划类资产_货币型.getValue());
    	// 投资类型：空
    	prodChlDeal.setInvestType(null);
    	// 根据资产交易资产流动类型区分资产流动类型、和交易类型
    	if (!StringUtil.isEmpty(prodChlDeal.getCurAssetType())){
	    	if (DictCodeListExt2.资金流动类型_资产买入.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_存入银行存款.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_拆放同业和买入返售首期.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_同业拆入和卖出回购首期.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_投资资产支出.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_其他支出.getValue().equals(prodChlDeal.getCurAssetType())){
	        	prodChlDeal.setCurAssetType(DictCodeListExt2.资金流动类型_资产买入.getValue());
	        	// 交易类型
	        	prodChlDeal.setDealtype(DictCodeListExt2.投资类型_通道交易_买入.getValue());
	    	} else if (DictCodeListExt2.资金流动类型_资产卖出或到期.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_银行存款到期.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_同业拆入和卖出回购到期.getValue().equals(prodChlDeal.getCurAssetType())
//	    			|| DictCodeListExt2.资金流动类型_投资资产所得收益.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_其他收入.getValue().equals(prodChlDeal.getCurAssetType())
	    			|| DictCodeListExt2.资金流动类型_拆放同业和买入返售到期.getValue().equals(prodChlDeal.getCurAssetType())){
	        	prodChlDeal.setCurAssetType(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue());
	        	// 交易类型
	        	prodChlDeal.setDealtype(DictCodeListExt2.投资类型_通道交易_卖出.getValue());
	    	}
    	}
    	// 通道代码：空
    	prodChlDeal.setChlCpctId(null);
    	// 通道名称：空
    	prodChlDeal.setChlAgrtName(null);
    	// 交易类型：通道交易
    	prodChlDeal.setTradeType(DictCodeListExt2.投资端交易类型_资管计划交易.getValue());
    	// 是否底层交易流水 02 否
    	prodChlDeal.setIsUnderTrade(DictCodeList.是否_中债_否.getValue());
		// 创建人默认system
    	prodChlDeal.setCreateUser(CommonUtil.getUserName());
    	prodChlDeal.setUpdateTime(null);
    	prodChlDeal.setUpdateUser(CommonUtil.getUserName());
        // 数据来源
    	prodChlDeal.setDataSource("DB");
    	
    	// 通道资产交易接入单位净价，全价，到期收益率 add by zhangyf 20191024 BSTOZ-820 start
    	// 到期收益率:为空
		prodChlDeal.setEndRate(null);
		if (StringUtil.equals(DictCodeListExt2.资金流动类型_资产买入.getValue(), prodChlDeal.getCurAssetType())
				|| StringUtil.equals(DictCodeListExt2.资金流动类型_资产卖出或到期.getValue(), prodChlDeal.getCurAssetType())) {
			//单位成交价格（全价）
			if(prodChlDeal.getNum()!=null&&prodChlDeal.getCnyAmt()!=null){
				if(prodChlDeal.getNum().compareTo(BigDecimal.ZERO)!=0){
					prodChlDeal.setUnitPriceFull(prodChlDeal.getCnyAmt().divide(prodChlDeal.getNum(),4,BigDecimal.ROUND_HALF_UP));
				}
			}
			prodChlDeal.setUnitPriceNet(new BigDecimal(1));
		} else {
			prodChlDeal.setUnitPriceFull(null);
			prodChlDeal.setUnitPriceNet(null);
		}
    	// 通道资产交易接入单位净价，全价，到期收益率 add by zhangyf 20191024 BSTOZ-820 start

		// 资金流动类型为01~08时，改字段有值，否则该字段为空；通道不穿透报送，自动生成的投资交易，资产计量方式默认为公允价值
		String[] curAssetTypeArr = {"01","02","03","04","05","06","07","08"};
		if(Arrays.asList(curAssetTypeArr).contains(prodChlDeal.getCurAssetType())) {
			prodChlDeal.setAssetMethod(DictCodeListExt3.资产计量方式_公允价值.getValue());
		}
		
    	return prodChlDeal;
    }
    
    /**
     * 功能:根据红利再投交易生成投资资产所得收益
     * @since 1.6.0
     * @version 1.6.0
     * @author：weiyuqiang 2019年2月20日
     * @param assetDeal
     * @return 
     * ChbInvestTrade
     * @throws CloneNotSupportedException 
     */
    private ChbInvestTrade getAssetIntDeal(ChbInvestTrade assetDeal) throws CloneNotSupportedException{
    	ChbInvestTrade assetIntDeal = new ChbInvestTrade();
    	BeanUtils.copyProperties(assetDeal,assetIntDeal);
    	assetIntDeal.setTradeSeqno(IDGenerator.generate(36));
    	// 重新设置交易编号
    	String tradeCode = assetIntDeal.getTradeCode();
    	StringBuilder sb = new StringBuilder(tradeCode);
    	// 原交易编号+01分红+02
    	assetDeal.setTradeCode(sb.append("01").toString());
    	// 存款类资产流动类型01更改为03
    	if(DictCodeListExt2.投资品种_存款类资产.getValue().equals(assetDeal.getInvestVariety())){
    		assetDeal.setCurAssetType(DictCodeListExt2.资金流动类型_存入银行存款.getValue());
    	}
    	// 合同编号=交易编号
		// if(!StringUtil.isNotEmpty(assetDeal.getContractCode())){
			assetDeal.setContractCode(assetDeal.getTradeCode());
		// }
    	StringBuilder sbNew = new StringBuilder(tradeCode);
		assetIntDeal.setTradeCode(sbNew.append("02").toString());
		// if(!StringUtil.isNotEmpty(assetDeal.getContractCode())){
			assetIntDeal.setContractCode(assetIntDeal.getTradeCode());
		// }
    	// 根据资产交易资产流动类型区分资产流动类型:投资资产所得收益
    	assetIntDeal.setCurAssetType(DictCodeListExt2.资金流动类型_投资资产所得收益.getValue());
    	// 单位成交价格(净价)
    	assetIntDeal.setUnitPriceNet(null);
    	// 单位成交价格(全价)
    	assetIntDeal.setUnitPriceFull(null);
    	// 到期收益率
    	assetIntDeal.setEndRate(null);
    	// 数量 = 分红日产品对应持仓份额取昨日头寸资产头寸对应份额
    	assetIntDeal.setNum(assetDeal.getIntPayAmt());
		// 创建人默认system
    	assetIntDeal.setCreateUser(CommonUtil.getUserName());
    	assetIntDeal.setUpdateTime(null);
    	assetIntDeal.setUpdateUser(CommonUtil.getUserName());
        // 数据来源
    	assetIntDeal.setDataSource("DB");
    	// 资产计量方式
    	assetIntDeal.setAssetMethod(null);
    	return assetIntDeal;
    }

    /**
     * 功能:数据入库
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月5日 
     * @param investTradeList 投资交易信息
     * @param saveFlag:F－全量，I－增量
     */
    public void insertDataMerge(List<ChbInvestTrade> investTradeList,String saveFlag) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            int count = 0;
            logger.debug("投资交易插入开始--------------" + new Date());
            ChbInvestTradeInterfMapper batchMapper = session.getMapper(ChbInvestTradeInterfMapper.class);
            int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
            int batchLastIndex = batchCount;//每批最后一个的下标
            for(int index = 0; index < investTradeList.size();){
                if(batchLastIndex > investTradeList.size()-1){
                    batchLastIndex = investTradeList.size();
                    batchMapper.mergeInfoListEx(investTradeList.subList(index, batchLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                    batchMapper.mergeInfoListEx(investTradeList.subList(index, batchLastIndex));
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
            logger.debug("投资交易插入结束--------------" + new Date());
            session.commit();
            // 清理缓存，防止溢出
            session.clearCache();
        }
        finally {
            session.close();
        }
        logger.debug("投资交易插入结束--------------" + new Date());
    }
	/**
	 * 功能:带提交的方式单条处理数据结果，入库
	 * @since 1.6.0
	 * @version 1.6.0
	 * @author：weiyuqiang 2019年3月7日
	 * @param assetList 
	 * void
	 */
	@SuppressWarnings("unused")
    private void dealResultCommitSingle(List<ChbInvestTrade> investList){
		if (CollectionUtils.isNotEmpty(investList)) {
			logger.debug("上游资产交易信息先删后插-删除:----开始------" +  df.format(new Date()));
			int batchCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"50" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));//每批commit的个数
	        int batchLastIndex = batchCount;//每批最后一个的下标
	        for(int index = 0; index < investList.size();){
	            if(batchLastIndex > investList.size()-1){
	                batchLastIndex = investList.size();
	                chbInvestTradeInterfMapper.deleteByTradeCode(investList.subList(index, batchLastIndex));
//	                System.out.println("index:"+index+"     batchLastIndex:"+batchLastIndex+"list"+prodinfos.subList(index, batchLastIndex).size());
	                break;//数据插入完毕,退出循环
	            }else{
	            	chbInvestTradeInterfMapper.deleteByTradeCode(investList.subList(index, batchLastIndex));
//	                System.out.println("index:"+index+"     batchLastIndex:"+batchLastIndex +"list"+prodinfos.subList(index, batchLastIndex).size());
	                index = batchLastIndex;//设置下一批下标
	                batchLastIndex = index + batchCount;                     
	            }
	        }
			logger.debug("上游资产交易信息先删后插-删除:----结束------" +  df.format(new Date()));
			logger.debug("上游资产交易信息先删后插-插入:----开始------" +  df.format(new Date()));
			int count = 0;
			SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
	    	try{
	    		ChbInvestTradeInterfMapper batchMapper =  session.getMapper(ChbInvestTradeInterfMapper.class);
	    		for(ChbInvestTrade asset : investList){
	    			batchMapper.insert(asset);
	    			count++;
	    			if(count%1000==0){
	    				session.commit();
	    	            // 清理缓存，防止溢出
	    	            session.clearCache();
	    				logger.debug("上游资产交易信息先删后插-插入1000条第"+ count/1000 +"次:----结束------" +  df.format(new Date()));	
	    			}
	    		}
	    		session.commit();
	            // 清理缓存，防止溢出
	            session.clearCache();
	    	}
//	    	catch(Exception e){
//	    		session.rollback();
//	    		throw e;
//	    	}
	    	finally {
	    		session.close();
	    	}
			logger.debug("上游资产交易信息先删后插-插入:----结束------" +  df.format(new Date()));
		}
	}
	
	
	/**   
	* @Title: calcIncurredAmt  
	* @Description: (计算外汇远期到期收益交易发生金额)   
	* @param: 对应外汇远期到期收益交易   
	* @return: 发生金额值（非绝对值）  
	* @throws   
	*/ 
	private BigDecimal calcIncurredAmt(ChbInvestTrade investDeal, Map<String, BigDecimal> rateMap) {
		BigDecimal incurredAmt = BigDecimal.ZERO;
		String curAssetdetType = investDeal.getCurAssetdetType();
		String[] dataArr = curAssetdetType.split(":");
		// 获取实际交易编号
//		String tradeCode = investDeal.getTradeCode();
//		String tradeId = tradeCode.substring(0, tradeCode.length() - 2);
		
//		Map<String,Object> param = new HashMap<String,Object>();
//		List<ChbInvestTradeEx> tradeExList = new ArrayList<ChbInvestTradeEx>();
//		param.put("tradeId", tradeId);
//        template.select("com.joyintech.tams.svreport.interf.chb.investtrade.dao.ChbInvestTradeInterfMapper.queryChbInvestTradeEx",
//        		param,new ResultHandler() {
//            @Override
//            public void handleResult(ResultContext resultContext) {
//            	tradeExList.add((ChbInvestTradeEx) resultContext.getResultObject());
//            }
//        });
//		
//		ChbInvestTradeEx chbInvestTradeEx = chbInvestTradeInterfMapper.queryChbInvestTradeEx(tradeId);
//		if (chbInvestTradeEx != null) {
//			ChbInvestTradeEx chbInvestTradeEx = tradeExList.get(0);
			// 货币对
			String curPair = dataArr[0];
			// 基准货币值（默认为 CNY）
			String basicCcy = DictCodeListExt1.币种_中债_人民币.getValue();
			if (StringUtil.isNotEmpty(curPair)) {
				basicCcy = curPair.split("/")[0];
			}
			// 交易货币币种
			String tradeCcy = dataArr[1];
			// 对应货币币种
			String ccy = dataArr[2];
			// 外汇交易方向
			String excPs = dataArr[3];
			// 交易方向
			String ps = dataArr[4];
			// 对应货币金额
			BigDecimal fpriceAmt = new BigDecimal(dataArr[5]);
			// 交易货币金额
			BigDecimal prinAmt = new BigDecimal(dataArr[6]);
			// 单位全价
			BigDecimal unitFprice = BigDecimal.ONE;
			// 清算模式
			String deliveryType = "";
			if (dataArr.length == 9) {
				unitFprice = new BigDecimal(dataArr[7]);
				deliveryType = dataArr[8];
			}
			if (StringUtil.equals(ccy, basicCcy)) {
				if (StringUtil.equals("DIF-SETT", deliveryType)) {
					fpriceAmt = fpriceAmt.multiply(unitFprice);
				} else {
					fpriceAmt = ChbCcyConvertUtil.exchangeMoney(ccy,investDeal.getCcy(),investDeal.getDealdate(), fpriceAmt, rateMap,"02");
				}
			}else{
				if (StringUtil.equals("DIF-SETT", deliveryType)) {
					prinAmt = prinAmt.multiply(unitFprice);
				} else {
					prinAmt = ChbCcyConvertUtil.exchangeMoney(tradeCcy,investDeal.getCcy(),investDeal.getDealdate(), prinAmt, rateMap,"02");
				}
			}
			
//			// 对应货币不是CNY，需要按照汇率折算为CNY
//			if (!StringUtil.equals(DictCodeListExt1.币种_中债_人民币.getValue(), ccy)) {
//				
//			}
//			// 交易货币不是CNY, 需要按照汇率折算为CNY
//			if (!StringUtil.equals(DictCodeListExt1.币种_中债_人民币.getValue(), tradeCcy)) {
//				
//				
//			}
			if (StringUtil.equals("F12", investDeal.getInvestType())) {
				// 外汇远期对应货币币种等于基准货币值
				if (StringUtil.equals(ccy, basicCcy)) {
					if (StringUtil.equals("T01", excPs)) {
						incurredAmt = fpriceAmt.subtract(prinAmt);
					} else if (StringUtil.equals("T02", excPs)) {
						incurredAmt = prinAmt.subtract(fpriceAmt);
					}
				// 外汇掉期对应货币币种不等于基准货币值
				} else {
					if (StringUtil.equals("T01", excPs)) {
						incurredAmt = prinAmt.subtract(fpriceAmt);
					} else if (StringUtil.equals("T02", excPs)) {
						incurredAmt = fpriceAmt.subtract(prinAmt);
					}
				}
			}
			// 外汇掉期发生金额处理
			if (StringUtil.equals("F13", investDeal.getInvestType())) {
				if (StringUtil.equals("T01", ps)) {
					if (StringUtil.equals(ccy, basicCcy)) {
						if (StringUtil.equals("T_B/S", excPs)) {
							incurredAmt = fpriceAmt.subtract(prinAmt);
						} else if (StringUtil.equals("T_S/B", excPs)) {
							incurredAmt = prinAmt.subtract(fpriceAmt);
						}
					} else {
						if (StringUtil.equals("T_B/S", excPs)) {
							incurredAmt = prinAmt.subtract(fpriceAmt);
						} else if (StringUtil.equals("T_S/B", excPs)) {
							incurredAmt = fpriceAmt.subtract(prinAmt);
						}
					}
				} else if (StringUtil.equals("T07", ps)) {
					if (StringUtil.equals(ccy, basicCcy)) {
						if (StringUtil.equals("T_S/B", excPs)) {
							incurredAmt = fpriceAmt.subtract(prinAmt);
						} else if (StringUtil.equals("T_B/S", excPs)) {
							incurredAmt = prinAmt.subtract(fpriceAmt);
						}
					} else {
						if (StringUtil.equals("T_S/B", excPs)) {
							incurredAmt = prinAmt.subtract(fpriceAmt);
						} else if (StringUtil.equals("T_B/S", excPs)) {
							incurredAmt = fpriceAmt.subtract(prinAmt);
						}
					}
				}
			}
//		}
		return incurredAmt.setScale(2,BigDecimal.ROUND_HALF_EVEN);
	}
}
