package com.joyintech.tams.svreport.chb.proddurationreg.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.dto.ChbCheckTableInfo;
import com.joyintech.tams.svreport.chb.common.SvrDataStatusUtil;
import com.joyintech.tams.svreport.chb.datacheck.beforeinsert.service.ChbCommonCheckService;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.batch.report.common.db.SvrDBUtil;
import com.joyintech.tams.common.base.entity.ChbPrdBonusDeal;
import com.joyintech.tams.common.base.entity.ChbPrdDeal;
import com.joyintech.tams.common.base.entity.ChbPrdDealSub;
import com.joyintech.tams.common.base.entity.ChbPrdFeeDeal;
import com.joyintech.tams.common.base.entity.ChbPrdPeriodYield;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.ChbDeadlineUtil;
import com.joyintech.tams.svreport.chb.proddurationreg.dao.ChbPrdDurationRegMapper;
import com.joyintech.tams.svreport.chb.proddurationreg.dao.ProdDurationReg001Mapper;
import com.joyintech.tams.svreport.chb.proddurationreg.dto.ChbPrdDurationDeal;
import com.joyintech.tams.svreport.chb.proddurationreg.dto.ProdDurationReg001Dto;
import com.joyintech.tams.svreport.position.dto.ChbPrdPosition;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * 产品存续期登记前期批处理操作(基础表到目标表的数据整合)
 * @author Administrator
 *
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProdDurationReg002Service extends SvrDataStatusUtil {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
    private ChbPrdDurationRegMapper chbPrdDurationRegMapper;
	@Autowired
    private ProdDurationReg001Mapper prodDurationReg001Mapper;
	@Autowired
	private SqlSessionTemplate template;
	/**
     * 入库前校验用Service
     */
    @Autowired
    private ChbCommonCheckService chbCommonCheckService;

	@Autowired
    private SvrDBUtil svrDBUtil;


//    @Value("${application.reportSelectOrDeleteCount}")
//    public  int reportSelectOrDeleteCount;

    public void generateDurationRegData(Date batchDate,List<String> prodIdList){

    	logger.debug("获取当日要报备的交易信息:----开始------" + df.format(new Date()));
    	//1、根据跑批日获取当日要报备的交易信息
    	List<ChbPrdDurationDeal> resultList = getDealListByDate(batchDate,prodIdList);
    	logger.debug("获取当日要报备的交易信息:----结束------" + df.format(new Date()));

    	logger.debug("处理当日要报备的交易信息:----开始------" + df.format(new Date()));
    	//2、对交易数据进行详细处理，比如汇率换算，金额计算等逻辑操作
        HashMap<String,List<ChbPrdDealSub>> map = calDealData(batchDate,resultList);
        logger.debug("处理当日要报备的交易信息:----结束------" + df.format(new Date()));

    	//3、持久化到存续登记表
        List<ChbPrdDealSub> dataList = map.get("INSERT");
        List<ChbPrdDealSub> delList = map.get("DELETE");
        if(!StringUtil.isEmptyList(delList)){
        	deleteBatch(delList);
        }
    	if(!StringUtil.isEmptyList(dataList)){


    		//update by leipeng 插入的数据获取表 内的实际登记编码
	       if(CommonUtil.isBAmodel()){
	           Map<String,String> data = svrDBUtil.getAllRegCode();

	           for (ChbPrdDealSub dto : dataList) {
	        	   dto.setIssuePartyId(data.get(dto.getProdId()));
	           }
	   		}
			// add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 start
			dataList = chbCommonCheckService.checkData(dataList, ChbCheckTableInfo.产品存续登记表SUB);
            // add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 end

			insertBatch(dataList);
//			if(totalCount>0){
//				if(totalCount<=singalCount){
//					chbPrdDurationRegMapper.deleteBatch(dataList);
//		    		chbPrdDurationRegMapper.insertBatch(dataList);
//				}
//				else{
//					for(int i = 0; i < totalCount/singalCount ; i++){
//						chbPrdDurationRegMapper.deleteBatch(dataList.subList(0, singalCount));
//			    		chbPrdDurationRegMapper.insertBatch(dataList.subList(0, singalCount));
//						dataList.removeAll(dataList.subList(0, singalCount));
//					}
//	                if(CollectionUtils.isNotEmpty(dataList)){
//	                	chbPrdDurationRegMapper.deleteBatch(dataList);
//			    		chbPrdDurationRegMapper.insertBatch(dataList);
//	                }
//				}
//			}
    	}

//    	if(!StringUtil.isEmptyList(map.get("UPDATE"))){
//    		chbPrdDurationRegMapper.updateBatch(map.get("UPDATE"));
//    	}
    }

	private void deleteBatch(List<ChbPrdDealSub> dataList) {
		logger.debug("未报送登记信息批量删除:----开始------" + df.format(new Date()));
		int singalCount = Integer.parseInt(null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?
                							"100" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
		List<List<ChbPrdDealSub>> bigList = createPrdDealSubList(dataList,singalCount);
		for(int i=0;i < bigList.size();i++){
			chbPrdDurationRegMapper.deleteBatch(bigList.get(i));
		}
		logger.debug("未报送登记信息批量删除:----结束------" + df.format(new Date()));
	}

    private void insertBatch(List<ChbPrdDealSub> dataList) {
    	logger.debug("登记信息入库:----开始------" + df.format(new Date()));
		SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
    	//批量插入mapper
		ChbPrdDurationRegMapper batchMapper =  session.getMapper(ChbPrdDurationRegMapper.class);
		try{
			int count = 0;
			for(ChbPrdDealSub dl : dataList){
				batchMapper.insertPrdDealSub(dl);
        		count++;
        		if(count % 1000==0){
            		session.commit();
            		session.clearCache();
        		}
        	}
			session.commit();
    		session.clearCache();
		}
//		catch (Exception e) {
//            logger.debug("批量登记信息出错:" + e.getMessage());
//			session.rollback();
//        }
		finally {
    		session.close();
    	}
		logger.debug("登记信息入库:----结束------" + df.format(new Date()));
	}

	/**
	 * 根据跑批日获取需要登记的交易信息
	 * @param batchDate
	 * @return
	 */
	private List<ChbPrdDurationDeal> getDealListByDate(Date batchDate,List<String> prodIdList) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String openProdMdateConf = "";
		openProdMdateConf = SvrDataUtil.getSysParam(SVReportGlobalConstant.OPEN_PROD_MDATE_CONF);
		// 开放式产品的申购赎回交易，业务结束日取值配置（01 确认日、02 开放结束日），默认为01
		if (StringUtil.isEmpty(openProdMdateConf)) {
			openProdMdateConf = "01";
		}
		Map<String, Object> pageParam =new HashMap<>();
		pageParam.put("batchDate",sdf.format(batchDate));
		pageParam.put("prodIdList",prodIdList);
		pageParam.put("openProdMdateConf",openProdMdateConf);
		//季末最后一个自然日期
		pageParam.put("thisSeasonEnd",thisSeasonEnd(batchDate));
		//变动类型-变动类型_净值变动
		pageParam.put("changeType01",DictCodeListExt2.变动类型_净值变动.getValue());
		//变动类型-变动类型_收益率变动
		pageParam.put("changeType02",DictCodeListExt2.变动类型_收益率变动.getValue());
		//产品运作模式=产品运作模式_开放式净值型
		pageParam.put("opratemode03",DictCodeListExt2.产品运作模式_开放式净值型.getValue());
		//产品类型
		pageParam.put("prodtypen",DictCodeListExt2.产品类型_中债_净值型产品.getValue());
		//开放式产品
        List<ChbPrdDurationDeal> openList = chbPrdDurationRegMapper.getOpenProdIdsByDealSub(pageParam);

		Map<String, Object> pageParamCol =new HashMap<>();
		pageParamCol.put("batchDate",sdf.format(batchDate));
		pageParamCol.put("prodIdList",prodIdList);
		//变动类型-变动类型_净值变动
		pageParamCol.put("changeType01",DictCodeListExt2.变动类型_净值变动.getValue());
		//变动类型-变动类型_收益率变动
		pageParamCol.put("changeType02",DictCodeListExt2.变动类型_收益率变动.getValue());
		//产品运作模式=产品运作模式_封闭式净值型
		pageParamCol.put("opratemode01",DictCodeListExt2.产品运作模式_封闭式净值型.getValue());
		//产品类型
		pageParamCol.put("prodtypen",DictCodeListExt2.产品类型_中债_净值型产品.getValue());
		//封闭式产品
        List<ChbPrdDurationDeal> clolist = chbPrdDurationRegMapper.getProdIdsByDealSub(pageParamCol);
        //产品Id集合
        List<ChbPrdDurationDeal> list = new ArrayList<ChbPrdDurationDeal>();
        // 将查询出的开放式与封闭式产品整合
        if (!StringUtil.isEmptyList(openList)) {
            list.addAll(openList);
        }
        if (!StringUtil.isEmptyList(clolist)) {
            list.addAll(clolist);
        }
        // add by dubaojun 2020-05-27 青岛银行信息披露与监管报送 start
		// 同一产品代码+业务结束日取业务开始日最小的记录，将集合按照业务开始日从小到大排序
		list.sort((dto1, dto2) -> {
			int result = dto1.getProdId().compareTo(dto2.getProdId());
			if(result == 0) {
				result = DateUtil.compareDate(dto1.getOpenEnd(), dto2.getOpenEnd());
			}
			if(result == 0) {
				result = DateUtil.compareDate(dto1.getOpenStart(), dto2.getOpenStart());
			}
			return result;
		});
		// add by dubaojun 2020-05-27 青岛银行信息披露与监管报送 end
        //基础信息变更会有数据状态不同的数据
        Map<String,ChbPrdDurationDeal> realListMap=new HashMap<String,ChbPrdDurationDeal>();
        for(ChbPrdDurationDeal single:list){
        	// modify by dubaojun 2020-05-26 青岛银行信息披露与监管报送 start
        	String key= single.getProdId()/*+"-"+single.getOpenStart()*/+"-"+single.getOpenEnd();
			// modify by dubaojun 2020-05-26 青岛银行信息披露与监管报送 end
        	if(realListMap.containsKey(key)){
        		ChbPrdDurationDeal valueDeal=realListMap.get(key);
        		if(single.getStatus().equals("M")){
        			valueDeal.setStatus("M");
        		}else if(single.getStatus().equals("D")){
        			if(valueDeal.getStatus().equals("D")){
        				valueDeal.setStatus("D");
        			}else{
        				valueDeal.setStatus("M");
        			}
        		}else{
        			valueDeal.setStatus("M");
        		}
        	}else{
        		realListMap.put(key, single);
        	}
        }
        List<ChbPrdDurationDeal> reallist = new ArrayList<ChbPrdDurationDeal>();
        for(String key:realListMap.keySet()){
        	ChbPrdDurationDeal single=realListMap.get(key);
        	//撤销 产品交易 、产品报价、产品费用中的某一类全部交易时 撤销日和交易日不在同一天只能查出这种全部撤销交易 此时不应该生成删除登记
        	if("D".equals(single.getStatus())){
        		single.getOpenStart();
        		List<String> prdoList=new ArrayList<String>();
        		prdoList.add(single.getProdId());
				pageParam.put("batchDate",sdf.format(single.getOpenStart()));
				pageParam.put("prodIdList",prdoList);
        		List<ChbPrdDurationDeal> singleopenList = chbPrdDurationRegMapper.getOpenProdIdsByDealSub(pageParam);
		        //封闭式产品
				pageParamCol.put("batchDate",sdf.format(single.getOpenStart()));
				pageParamCol.put("prodIdList",prdoList);
		        List<ChbPrdDurationDeal> singleclolist = chbPrdDurationRegMapper.getProdIdsByDealSub(pageParamCol);
		      if(!StringUtil.isEmptyList(singleopenList)){
		    	  if(singleopenList.size()>1){
		    		  single.setStatus("M");
		    	  }
		      }else{
		    	  if(singleclolist.size()>1){
		    		  single.setStatus("M");
		    	  }
		      }
        	}
        	reallist.add(single);
        }
        if (!StringUtil.isEmptyList(reallist)) {
            // 查询相关基础信息
            List<ChbPrdDurationDeal> listBase = chbPrdDurationRegMapper.getProdDealSubList(reallist);
            // 查询交易信息
            List<ChbPrdDeal> dealList = chbPrdDurationRegMapper.getAllPrdDeal(reallist, openProdMdateConf);
            // 投资者累计兑付金额
            List<ChbPrdDeal> dealListTemp = null;
            // 查询费用信息
            List<ChbPrdFeeDeal> feeList = chbPrdDurationRegMapper.getAllPrdFeeList(reallist);
            List<ChbPrdFeeDeal> feeListTemp = null;
            for (ChbPrdDurationDeal chbPrdDurationDeal : reallist) {
                dealListTemp = new ArrayList<ChbPrdDeal>();
                feeListTemp = new ArrayList<ChbPrdFeeDeal>();
                chbPrdDurationDeal.setBatchDate(batchDate);
                // 基本信息处理
                for (ChbPrdDurationDeal base : listBase) {
                	// add by zhangliang 后面处理逻辑未报送的需要删除重新生成，所有RegSeq和RegSeq1都要判断 20201115 start
                	//已报送
                    if (chbPrdDurationDeal.getProdId().equals(base.getProdId())
                        /*&& ChbPrdDurationDeal.getOrgCode().equals(base.getOrgCode())*/
                        && chbPrdDurationDeal.getOpenEnd().equals(base.getOpenEnd())) {
                        chbPrdDurationDeal.setRegSeq(base.getRegSeq());
                    }
                    //未报送
//                    else else
                    if(chbPrdDurationDeal.getProdId().equals(base.getProdId())
                       /* && ChbPrdDurationDeal.getOrgCode().equals(base.getOrgCode())*/
                        && chbPrdDurationDeal.getOpenEnd().equals(base.getOpenEnd1())){
                        chbPrdDurationDeal.setRegSeq1(base.getRegSeq1());
                    }
					// add by zhangliang 后面处理逻辑未报送的需要删除重新生成，所有RegSeq和RegSeq1都要判断 20201115 end
                }
                // 交易信息处理
                for (ChbPrdDeal chbPrdDeal : dealList) {
                    if (chbPrdDurationDeal.getProdId().equals(chbPrdDeal.getProdId())
                		&& DateUtil.compareDate(chbPrdDurationDeal.getOpenEnd(),
                				chbPrdDeal.getSettdateDate()) == 0
                        /*&& ChbPrdDurationDeal.getOrgCode().equals(chbPrdDeal.getOrgCode())*/) {
                        dealListTemp.add(chbPrdDeal);
                    }
                }
                // 费用信息处理
                for (ChbPrdFeeDeal chbPrdFeeDeal : feeList) {
                    if (chbPrdDurationDeal.getProdId().equals(chbPrdFeeDeal.getProdId())
                    	&& DateUtil.compareDate(chbPrdDurationDeal.getOpenEnd(),
                    	        chbPrdFeeDeal.getPayDate()) == 0
                       /* && ChbPrdDurationDeal.getOrgCode().equals(ChbPrdFeeDeal.getOrgCode())*/) {
                        feeListTemp.add(chbPrdFeeDeal);
                    }
                }

                chbPrdDurationDeal.setDealList(dealListTemp);
                chbPrdDurationDeal.setFeeList(feeListTemp);
            }
        }
        return reallist;
	}

	/**
     * 处理登记信息，金额计算
     * @param resultList
     */
	@SuppressWarnings("deprecation")
	private HashMap<String,List<ChbPrdDealSub>> calDealData(Date batchDate,List<ChbPrdDurationDeal> resultList) {
		List<ChbPrdDealSub> insertList = new ArrayList<ChbPrdDealSub>();
		List<ChbPrdDealSub> delList = new ArrayList<ChbPrdDealSub>();
		List<ChbPrdDealSub> updateList = new ArrayList<ChbPrdDealSub>();
        HashMap<String,List<ChbPrdDealSub>> map = new HashMap<String,List<ChbPrdDealSub>>();
        String amtSumType = SvrDataUtil.getSysParam(SVReportGlobalConstant.DEALSUB_BALANCEAMT_SUM_TYPE);
		ChbPrdDealSub result = null;
		for(ChbPrdDurationDeal model : resultList){
			result = new ChbPrdDealSub();
			initChbPrdDealSub(result);

//			boolean  isdel=false;
			 // 通用字段操作
//	        if (StringUtil.isNotEmpty(model.getRegSeq())) {
//	        	if (DictCodeListExt1.维护数据状态_删除.getValue().equals(model.getStatus())){
//	        		ChbPrdDealSub ybs= prodDurationReg001Mapper.selectByPrimaryKey(model.getRegSeq());
//	        		result=ybs;
//	        		result.setStatus(DictCodeListExt2.报表数据状态_删除.getValue());
//	        		result.setSubmitFeedbackStatus(null);
//	        		isdel=true;
//	        	} else if (DictCodeListExt1.维护数据状态_修改.getValue().equals(model.getStatus())){
//	        		result.setStatus(DictCodeListExt2.报表数据状态_修改.getValue());
//	        	} else if (DictCodeListExt1.维护数据状态_有效.getValue().equals(model.getStatus())){
//	        		continue;
//	        	} else {
//	        		logger.info("数据维护状态有误，应为：D|A|M!");
//	        		break;
//	        	}
//	        	insertList.add(result);
//	        	delList.add(result);
//	        }
//	        else {
//	        	if (DictCodeListExt1.维护数据状态_删除.getValue().equals(model.getStatus())){
//	        		delList.add(result);
//	        	}else{
//	        		result.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
//	        		insertList.add(result);
//	        		delList.add(result);
//	        	}
//	        }

			result.setProdId(model.getProdId());
//	        result.setOrgCode(model.getOrgCode());
			result.setOrgCode(CommonUtil.getOrgCode());
			result.setDeptCode(CommonUtil.getDeptCode());
			result.setUpdateUser(CommonUtil.getUserName());
			result.setCreateUser(CommonUtil.getUserName());
//	        result.setIssuePartyId(model.getRegistBankCode());
	        result.setIssuePartyId(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
//			if(StringUtil.isEmpty(result.getIssuePartyId())){
//				String issuePartyId = CommonUtil.selectBankCode();
//				result.setIssuePartyId(issuePartyId);
//			}
	        result.setProdRegistCode(model.getRegCode());
	        result.setVdate(DateUtil.format(model.getOpenStart(),"yyyy-MM-dd"));
			/**
			 * 此时数据日期为交易日期-为不影响原代码逻辑，在后续会重新塞值
			 */
	        result.setMdate(DateUtil.format(model.getOpenEnd(),"yyyy-MM-dd"));//数据日期
			result.setStatusDate(batchDate);

	        // 细分类
	        String typeDetail = "";

	        // 产品存续期登记中是否有未报送数据
	        if (StringUtil.isNotEmpty(model.getRegSeq1())) {
	        	result.setRegistSeqno(model.getRegSeq1());
//	            updateList.add(result);
	        }
	        else {
	            result.setRegistSeqno(IDGenerator.generate(36));
	        }
			// 报送流水号
			result.setSumitSeq(CommonUtil.getSequenceIndexData(DataIndexSequenceType.中债登记管理));
	        result.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
	        //生成删除未报送直接用 之前的已报送的详细数据
//	        if(isdel){
//	        	continue;
//	        }
	        // 报价日
	        Date quotationDate = model.getOpenEnd();
	        // 存汇率map
	        Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
	        // 交易信息
	        List<ChbPrdDeal> dealList = model.getDealList();
	        // 处理产品费用信息
	        List<ChbPrdFeeDeal> feeList = model.getFeeList();
	        /**
	         * 币种及申购兑付金额
	         * 格式： CNY,1000000.00,500000.00,50000.00
	         *      USD,2000000.00,1000000.00,100000.00
	         *      ....
	         *      ....
	         * key:币种   value:申购金额,兑付金额,兑付收益金额
	         */
	        HashMap<String,String> ccyMap = new HashMap<String,String>();
	        //根据type取值最新收益率以及实现收益率
            String type = "01";
	        // 非净值型产品
	        if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {
	            if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())) {
	                typeDetail = DictCodeListExt2.细分类别_开放式非净值型.getValue();
	            }else{
	            	typeDetail = DictCodeListExt2.细分类别_封闭式非净值型.getValue();
	            }
	            // 处理交易
                if (!StringUtil.isEmptyList(dealList)) {
                    for (ChbPrdDeal chbPrdDeal : dealList) {
                    	this.getDealCcy(ccyMap,chbPrdDeal,model,null);
                        if (DictCodeListExt2.产品存续期登记交易类型_存续期申购.getValue().equals(chbPrdDeal.getTradeType())
                            || DictCodeListExt2.产品存续期登记交易类型_份额增强.getValue().equals(
                                chbPrdDeal.getTradeType())) {
                        	//累计申购份额
                            result.setPurchaseQty((null == result.getPurchaseQty()?BigDecimal.ZERO:result.getPurchaseQty()).add(chbPrdDeal.getTradeQty()));
                        }
                        else if (DictCodeListExt2.产品存续期登记交易类型_存续期赎回.getValue().equals(chbPrdDeal.getTradeType())
                                || DictCodeListExt2.产品存续期登记交易类型_提前赎回.getValue().equals(
                                        chbPrdDeal.getTradeType())
                        		|| DictCodeListExt2.产品存续期登记交易类型_份额减弱.getValue().equals(
                                     chbPrdDeal.getTradeType())
                                || DictCodeListExt2.产品存续期登记交易类型_到期兑付.getValue().equals(
                                     chbPrdDeal.getTradeType())) {
                            //累计赎回份额
                            result.setRedeemQty((null == result.getRedeemQty()?BigDecimal.ZERO:result.getRedeemQty()).add(chbPrdDeal.getTradeQty()));
                            //到期时，产品的实现收益率以及最新收益率取值相同
                            if(DictCodeListExt2.产品存续期登记交易类型_到期兑付.getValue().equals(
                                    chbPrdDeal.getTradeType())){
                            	type = "02";
                            }
                        }
                    }
                }

                // 实现收益率
                //如果是分层产品，则算术平均:子产品1认购金额*reaYield + 子产品2认购金额*reaYield / 所有子产品认购总额
                BigDecimal reaYield = BigDecimal.ZERO;
                int cnt = chbPrdDurationRegMapper.isLevProd(model.getProdId());
                if(cnt > 0){
                	reaYield = calAvgYield(model,quotationDate,type);
                }else{
                	ChbPrdPeriodYield yieldModel = chbPrdDurationRegMapper.getReaYield(model.getProdId(),
                			quotationDate, type);
                	if(yieldModel!=null&&yieldModel.getProdValue()!=null)
                	reaYield = yieldModel.getProdValue();
                }
	            result.setReaYield(ArithmeticUtil.multiply(reaYield, new BigDecimal(100)).setScale(5, RoundingMode.HALF_UP));

	            // 最新收益率
	            BigDecimal newExpYield = BigDecimal.ZERO;
	            if(cnt > 0){
	            	newExpYield = calAvgYield(model,quotationDate,"02");
	            }else{
	            	ChbPrdPeriodYield yieldModel = chbPrdDurationRegMapper.getReaYield(model.getProdId(), quotationDate, "02");
                	if(yieldModel!=null&&yieldModel.getProdValue()!=null)
                	  newExpYield = yieldModel.getProdValue();
	            }
	            result.setNewExpYield(ArithmeticUtil.multiply(newExpYield, new BigDecimal(100)).setScale(5, RoundingMode.HALF_UP));
	        }
	        // 净值型产品
	        else {
	        	if (DictCodeListExt2.产品运作模式_开放式净值型.getValue().equals(model.getOperateMode())) {
	                typeDetail = DictCodeListExt2.细分类别_开放式净值型.getValue();
	            }else{
	            	typeDetail = DictCodeListExt2.细分类别_封闭式净值型.getValue();
	            }
	            // 初始净值
	            result.setOriginalValue(new BigDecimal(1));
	            // 产品净值
	            ChbPrdPeriodYield newExpYield = chbPrdDurationRegMapper.getReaYield(model.getProdId(), quotationDate, "02");
	            if(newExpYield!=null&&newExpYield.getProdValue()!=null){
	            	result.setPrdValue(newExpYield.getProdValue().setScale(5, RoundingMode.HALF_UP));
					//净值日期
					result.setYieldDate(newExpYield.getChangeDate());
	            }else{
	            	result.setPrdValue(BigDecimal.ONE);
	            }
                // 处理交易
                if (!StringUtil.isEmptyList(dealList)) {
                	//同一天同一个产品同一个币种移动平均收益只计算一次
                	List<String> calAvgIntamtList = new ArrayList<String>();
                    for (ChbPrdDeal chbPrdDeal : dealList) {
                    	this.getDealCcy(ccyMap,chbPrdDeal,model,calAvgIntamtList);
                        if (DictCodeListExt2.产品存续期登记交易类型_存续期申购.getValue().equals(chbPrdDeal.getTradeType())
                            || DictCodeListExt2.产品存续期登记交易类型_份额增强.getValue().equals(
                                chbPrdDeal.getTradeType())
                            || DictCodeListExt2.产品存续期登记交易类型_收益再投资.getValue().equals(
                                chbPrdDeal.getTradeType())) {
                            // 累计申购份额
                            result.setPurchaseQty((null == result.getPurchaseQty()?BigDecimal.ZERO:result.getPurchaseQty()).add(chbPrdDeal.getTradeQty()));
                        }
                        else if (DictCodeListExt2.产品存续期登记交易类型_存续期赎回.getValue().equals(
                            chbPrdDeal.getTradeType())
                                || DictCodeListExt2.产品存续期登记交易类型_提前赎回.getValue().equals(
                                        chbPrdDeal.getTradeType())
                                 || DictCodeListExt2.产品存续期登记交易类型_份额减弱.getValue().equals(
                                     chbPrdDeal.getTradeType())
                                 || DictCodeListExt2.产品存续期登记交易类型_到期兑付.getValue().equals(
                                     chbPrdDeal.getTradeType())) {
                            //累计赎回份额
                            result.setRedeemQty((null == result.getRedeemQty()?BigDecimal.ZERO:result.getRedeemQty()).add(chbPrdDeal.getTradeQty()));
                        }
                    }
                }

                // 默认为人民币
	            if(StringUtil.isEmpty(result.getValueCcy())){
	            	  if(newExpYield!=null&&newExpYield.getCcy()!=null){
	            		  result.setValueCcy(newExpYield.getCcy());
	            	  }else{
	            		  result.setValueCcy(DictCodeListExt1.币种_中债_人民币.getValue());
	            	  }
	            }
                // 累计单位分红
				BigDecimal totalUnitBonusAmount = chbPrdDurationRegMapper.getTotalUnitBonusAmount(model.getProdId(),quotationDate)==null?BigDecimal.ZERO:chbPrdDurationRegMapper.getTotalUnitBonusAmount(model.getProdId(),quotationDate);
                result.setTotalValue(totalUnitBonusAmount);

	            //折算人民币初始净值
	            result.setCnyOrlValue(ChbCcyConvertUtil.changeCnyMoney(result.getValueCcy(), result.getOriginalValue(),
		                model.getOpenEnd(), rateMap).setScale(5, RoundingMode.HALF_UP));
				//折算人民币净值
				result.setCnyPrdValue(ChbCcyConvertUtil.changeCnyMoney(result.getValueCcy(), result.getPrdValue(),
						model.getOpenEnd(), rateMap).setScale(5, RoundingMode.HALF_UP));
				// 产品累计净值
				result.setTotalValue((null == result.getTotalValue()?BigDecimal.ONE:result.getTotalValue()).setScale(5, RoundingMode.HALF_UP));
				// 累计人民币净值
				result.setCnyTalValue(ChbCcyConvertUtil.changeCnyMoney(result.getValueCcy(), result.getTotalValue(),
						model.getOpenEnd(), rateMap).setScale(5, RoundingMode.HALF_UP));

	        }

	        // 产品余额份额获取
            List<ChbPrdPosition> list = chbPrdDurationRegMapper.getBalance(model.getProdId(),quotationDate);
        	//产品余额
        	BigDecimal balance = BigDecimal.ZERO;
            //产品份额
        	BigDecimal balanceQty = BigDecimal.ZERO;
            if(!StringUtil.isEmptyList(list)){
            	for(ChbPrdPosition pos : list){
            		balance = balance.add(ChbCcyConvertUtil.changeCnyMoney(pos.getCcy(),
    	                    pos.getBalanceAmt(), model.getOpenEnd(), rateMap));
            		balanceQty = balanceQty.add(pos.getBalanceQty());
            	}
            }

            /**  add 产品余额以持有者信息进行汇总的分支处理追加  by  wudebao  20210308  start  */
            if("01".equals(amtSumType)) {
            	//当汇总方式（DEALSUB_BALANCEAMT_SUM_TYPE）  ==    ‘01：投资端持有汇总  的情况下进行以下处理
                //取得本产品的持有明细汇总
        		BigDecimal balanceAmt = chbPrdDurationRegMapper.getBalanceAmt(model.getProdId(),quotationDate);
        		result.setBalanceAmt(balanceAmt);

            }else {
            	if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
         	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {
            		result.setBalanceAmt(balance);
            	} else {
            		// 净值型产品产品余额=产品份额*折算人民币净值
            		result.setBalanceAmt(balanceQty.multiply(result.getCnyPrdValue()).setScale(2, RoundingMode.HALF_UP));
            	}
            }

        	/**  add 产品余额以持有者信息进行汇总的分支处理追加  by  wudebao  20210308  start  */

            result.setPrdQty(balanceQty);

	        // 银行端收益
            if(feeList!=null&&!feeList.isEmpty()){
            	for (ChbPrdFeeDeal chbPrdFeeInfo : feeList) {
            		BigDecimal bankImpInt = ChbCcyConvertUtil.changeCnyMoney(chbPrdFeeInfo.getCcy(),
            				chbPrdFeeInfo.getAmt(), model.getOpenEnd(), rateMap);
            		result.setBankImpInt(((null == result.getBankImpInt())?BigDecimal.ZERO:result.getBankImpInt()).add(bankImpInt));
            	}
            }

	        //币种及申购兑付金额
	        StringBuffer sb = new StringBuffer();
	        for(Map.Entry<String, String> entry: ccyMap.entrySet()){
	        	sb.append(entry.getKey()).append(",").append(entry.getValue()).append(";");
	        }
	        if(sb.length() > 1){
	        	result.setCcyPurchase(sb.deleteCharAt(sb.length() - 1).toString());
	        }else{
//	        	result.setCcyPurchase("CNY,0.00,0.00,0.00");//delete by lingxiaoqing on 2021-06-03 for 按照交易币种来报送
				String ccy = (null == result.getValueCcy() ? "CNY" : result.getValueCcy());
				result.setCcyPurchase(ccy + ",0.00,0.00,0.00");//add by lingxiaoqing on 2021-06-03 for 按照交易币种来报送
			}

	        // 报送截止日生成
	        Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
	        HashMap<String,List<Date>> submitDateMap = new HashMap<String,List<Date>>();
	        String key = DictCodeListExt2.报表名称_产品存续期登记.getValue()
	        		     + typeDetail;
	        List<Date> dateList = new ArrayList<Date>();
	        if(submitDateMap.containsKey(key)){
	        	dateList = submitDateMap.get(key);
	        }else{
				String subStaus=model.getStatus();
				if(StringUtil.equals(model.getStatus(),DictCodeListExt2.数据状态_有效.getValue())){
					subStaus=DictCodeListExt2.报送数据状态_新建.getValue();
				}else if(StringUtil.equals(model.getStatus(),DictCodeListExt2.数据状态_修改.getValue())){
					subStaus=DictCodeListExt2.报送数据状态_修改.getValue();
				}else if( model.getStatus()==null||StringUtil.equals(model.getStatus(),DictCodeListExt2.数据状态_删除.getValue())){
					subStaus=DictCodeListExt2.报送数据状态_删除.getValue();
				}
	        	dateList = ChbDeadlineUtil.getChbEndDate(
	    	            DictCodeListExt2.报表名称_产品存续期登记.getValue(), typeDetail,
						subStaus,dateMap, model.getOpenEnd() == null ? batchDate : model.getOpenEnd());
	        	submitDateMap.put(key, dateList);
	        }

	        if (StringUtil.isNotEmpty(dateList)) {
	            result.setChbSubmitDeadline(dateList.get(0));
	            result.setSubmitDeadline(dateList.get(1));
	        }
	        /**  add 新增每万份份额分红和每万份现金分红  by  dongxiangyang  20200325  start  */
	        ChbPrdBonusDeal  deal=new  ChbPrdBonusDeal();
	        deal.setProdId(result.getProdId());
	        deal.setPayDate(DateUtil.parse(result.getMdate()));
	        //查询每万份份额分红和每万份现金分红
	        List<ProdDurationReg001Dto> lsitsub=chbPrdDurationRegMapper.selectWanAmt(deal);
	        if(lsitsub!=null&&lsitsub.size()>0){
	        	for(ProdDurationReg001Dto  sub:lsitsub){
	        	    // modify by dubaojun 2020-10-26 每万份份额分红和每万份现金分红保留5位小数 start
	        		//04 现金分红
					if(sub.getTradeType().equals("04") && sub.getCashAmtWan() != null){
//						result.setCashAmtWan(sub.getCashAmtWan().setScale(5, RoundingMode.HALF_UP));//delete by lingxq on 2021-06-03 for 折算人民币
						//----add by lingxq on 2021-06-03 for 折算人民币 ---->>>>>
						BigDecimal cashAmt = sub.getCashAmtWan();
						if (!StringUtil.equals(sub.getValueCcy(), DictCodeListExt1.币种_中债_人民币.getValue())){
							cashAmt = ChbCcyConvertUtil.changeCnyMoney(sub.getValueCcy(), sub.getCashAmtWan(),
									DateUtil.parse(result.getMdate()), rateMap);
						}
						result.setCashAmtWan(cashAmt.setScale(5, RoundingMode.HALF_UP));
						//----add by lingxq on 2021-06-03 for 折算人民币 ----<<<<<
					}
	        		//05 红利再投
	        		if(sub.getTradeType().equals("05") && sub.getShareAmtWan() != null){
	        			result.setShareAmtWan(sub.getShareAmtWan().setScale(5, RoundingMode.HALF_UP));
	        		}
                    // modify by dubaojun 2020-10-26 每万份份额分红和每万份现金分红保留5位小数 end
	        	}
	        }
	        /** add 新增每万份份额分红和每万份现金分红  by  dongxiangyang  20200325  end  */
			// add by zhangliang 20201109 数据流向处理 ===================================start
			result.setStatus(model.getStatus());
			ChbPrdDealSub chbPrdDealSub = null;
			if (StringUtil.isNotEmpty(model.getRegSeq())) {
				chbPrdDealSub = prodDurationReg001Mapper.selectByPrimaryKey(model.getRegSeq());

			}
			Map<String, ChbPrdDealSub> handleDataMap = handleDataStatus(result, chbPrdDealSub);
			if (handleDataMap.containsKey(SvrDataStatusUtil.INSERT_DATA)){
				insertList.add(handleDataMap.get(SvrDataStatusUtil.INSERT_DATA));
			}
			if (handleDataMap.containsKey(SvrDataStatusUtil.DELETE_DATA)){
				delList.add(handleDataMap.get(SvrDataStatusUtil.DELETE_DATA));
			}
			// add by zhangliang 20201109 数据流向处理 ===================================end
	    }
		if(!StringUtil.isEmptyList(updateList)){
			map.put("UPDATE", updateList);
		}
		if(!StringUtil.isEmptyList(insertList)){
			map.put("INSERT", insertList);
		}
		if(!StringUtil.isEmptyList(delList)){
			map.put("DELETE", delList);
		}
		return map;
	}
	/**
	 * 汇总逻辑处理
     * 处理登记信息，金额计算
     * @param resultList
     */
	@SuppressWarnings("deprecation")
	private HashMap<String,List<ChbPrdDealSub>> calDealDataTotal(Date batchDate,List<ChbPrdDurationDeal> resultList) {
		List<ChbPrdDealSub> insertList = new ArrayList<ChbPrdDealSub>();
		List<ChbPrdDealSub> delList = new ArrayList<ChbPrdDealSub>();
		List<ChbPrdDealSub> updateList = new ArrayList<ChbPrdDealSub>();
        HashMap<String,List<ChbPrdDealSub>> map = new HashMap<String,List<ChbPrdDealSub>>();
		ChbPrdDealSub result = null;
		for(ChbPrdDurationDeal model : resultList){
			result = new ChbPrdDealSub();
			initChbPrdDealSub(result);
//			boolean  isdel=false;
//			 // 通用字段操作
//	        if (StringUtil.isNotEmpty(model.getRegSeq())) {
//	        	if (DictCodeListExt1.维护数据状态_删除.getValue().equals(model.getStatus())){
//	        		ChbPrdDealSub ybs= prodDurationReg001Mapper.selectByPrimaryKey(model.getRegSeq());
//	        		result=ybs;
//	        		result.setStatus(DictCodeListExt2.报表数据状态_删除.getValue());
//	        		result.setSubmitFeedbackStatus(null);
//	        		isdel=true;
//	        	} else if (DictCodeListExt1.维护数据状态_修改.getValue().equals(model.getStatus())){
//	        		result.setStatus(DictCodeListExt2.报表数据状态_修改.getValue());
//	        	} else if (DictCodeListExt1.维护数据状态_有效.getValue().equals(model.getStatus())){
//	        		continue;
//	        	} else {
//	        		logger.info("数据维护状态有误，应为：D|A|M!");
//	        		break;
//	        	}
//	        	insertList.add(result);
//	        	delList.add(result);
//	        }
//	        else {
//	        	if (DictCodeListExt1.维护数据状态_删除.getValue().equals(model.getStatus())){
//	        		delList.add(result);
//	        	}else{
//	        		result.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
//	        		insertList.add(result);
//	        		delList.add(result);
//	        	}
//	        }
			result.setProdId(model.getProdId());
//	        result.setOrgCode(model.getOrgCode());
			result.setOrgCode(CommonUtil.getOrgCode());
			result.setDeptCode(CommonUtil.getDeptCode());
			result.setUpdateUser(CommonUtil.getUserName());
			result.setCreateUser(CommonUtil.getUserName());
//	        result.setIssuePartyId(model.getRegistBankCode());
	        result.setIssuePartyId(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
//			if(StringUtil.isEmpty(result.getIssuePartyId())){
//				String issuePartyId = CommonUtil.selectBankCode();
//				result.setIssuePartyId(issuePartyId);
//			}
	        result.setProdRegistCode(model.getRegCode());
	        result.setVdate(DateUtil.format(model.getOpenStart()));
	        result.setMdate(DateUtil.format(model.getOpenEnd()));//数据日期
			result.setStatusDate(batchDate);

			/**
			 * update by xr  对与开放式产品，开放周期日为取周期结束日，取得确认日处理，
			 * 修改BUG时，为减少对原逻辑影响，取了原model中得openend1字段，为周期结束日在，交易查询结束后
			 * 为周期结束日重新赋值
			 */
	        //update by leipeng 上面逻辑导致阜新接口接入数据无法正常生成存续期交易.
			if(null!=model.getOpenEnd1()&&!SvrDataUtil.getSysParam("CHB_PRD_DEAL_FILE_TYPE").equals(SVReportGlobalConstant.CHB_PRD_DEAL_FILE_TYPE_02)){
				result.setMdate(DateUtil.format(model.getOpenEnd1(),"yyyy-MM-dd"));//数据日期
			}

	    }
		if(!StringUtil.isEmptyList(updateList)){
			map.put("UPDATE", updateList);
		}
		if(!StringUtil.isEmptyList(insertList)){
			map.put("INSERT", insertList);
		}
		if(!StringUtil.isEmptyList(delList)){
			map.put("DELETE", delList);
		}
		return map;
	}

	/**
	 * 拼接申购赎回信息
	 * @param ccyMap
	 * @param chbPrdDeal
	 * @param model
	 * @param calAvgIntamtList
	 */
	@SuppressWarnings("deprecation")
	private void getDealCcy(HashMap<String,String> ccyMap,
			               ChbPrdDeal chbPrdDeal,
			               ChbPrdDurationDeal model,
			               List<String> calAvgIntamtList) {
		Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
		if (StringUtil.isNotEmpty(chbPrdDeal.getCcy())) {
			//申购金额
        	BigDecimal purAmt = BigDecimal.ZERO;
        	//赎回金额
        	BigDecimal redAmt = BigDecimal.ZERO;
        	//兑付收益
        	BigDecimal intAmt = BigDecimal.ZERO;
            if(ccyMap.containsKey(chbPrdDeal.getCcy())){
            	String value = ccyMap.get(chbPrdDeal.getCcy());
            	purAmt = new BigDecimal(value.split(",")[0]);
            	redAmt = new BigDecimal(value.split(",")[1]);
            	intAmt = new BigDecimal(value.split(",")[2]);
				if (DictCodeListExt2.产品存续期登记交易类型_存续期申购.getValue().equals(chbPrdDeal.getTradeType())
						||DictCodeListExt2.产品存续期登记交易类型_份额增强.getValue().equals(chbPrdDeal.getTradeType())) {

					purAmt = purAmt.add(ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
							chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap));
				}
				else if (DictCodeListExt2.产品存续期登记交易类型_存续期赎回.getValue().equals(chbPrdDeal.getTradeType())
						|| DictCodeListExt2.产品存续期登记交易类型_提前赎回.getValue().equals(chbPrdDeal.getTradeType())
						|| DictCodeListExt2.产品存续期登记交易类型_到期兑付.getValue().equals(chbPrdDeal.getTradeType())
						||DictCodeListExt2.产品存续期登记交易类型_份额减弱.getValue().equals(chbPrdDeal.getTradeType())) {
            	    if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
            	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {

            	    	redAmt = redAmt.add(ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
                				            chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap)
//		            	    		   .add(null == chbPrdDeal.getIntAmt() ? BigDecimal.ZERO :
//		            	    				ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
//		            				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap))
		            	    		   );

            	    	intAmt = intAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
				            	    		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
				            				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
            	    }else{
            	    	//同一天既有赎回(到期)又有现金分红,收益只取一次
            	    	String key = model.getProdId() + "-" + chbPrdDeal.getPayDate() + "-" + chbPrdDeal.getCcy();
            	    	if(!calAvgIntamtList.contains(key)){
	            	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getCcy(),chbPrdDeal.getPayDate());
	            	    	intAmt = intAmt.add(avgIntamt);
	            	    	calAvgIntamtList.add(key);
            	    	}
	       	    		redAmt = redAmt.add(ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
	       	    							chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap));
            	    }
                }
                else if (DictCodeListExt2.产品存续期登记交易类型_收益再投资.getValue().equals(
                        chbPrdDeal.getTradeType())) {
                	redAmt = redAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
                		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
    				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
                	if (DictCodeListExt2.产品运作模式_开放式净值型.getValue().equals(model.getOperateMode())
               	            || DictCodeListExt2.产品运作模式_封闭式净值型.getValue().equals(model.getOperateMode())) {
	                	//同一天既有红利再投又有现金分红,收益只取一次
	                	String key = model.getProdId() + "-" + chbPrdDeal.getPayDate() + "-" + chbPrdDeal.getCcy();
	        	    	if(!calAvgIntamtList.contains(key)){
	            	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getCcy(),chbPrdDeal.getPayDate());
	            	    	intAmt = intAmt.add(avgIntamt);
	            	    	calAvgIntamtList.add(key);
	        	    	}
	        	    	//add by 20200608 净值型产品的认购金额加上红利再投
	        	     	purAmt = purAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
	                		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
	    				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
                	}

                }
                else if (DictCodeListExt2.产品存续期登记交易类型_收益分配.getValue().equals(
                    chbPrdDeal.getTradeType())) {

                	redAmt = redAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
				                		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
				    				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));

                	if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
            	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {
                    	//兑付金额
                		intAmt = intAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
				            	    		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
				            				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
            	    }else{
            	    	//同一天既有赎回(到期)又有现金分红,收益只取一次
            	    	String key = model.getProdId() + "-" + chbPrdDeal.getPayDate() + "-" + chbPrdDeal.getCcy();
            	    	if(!calAvgIntamtList.contains(key)){
	            	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getCcy(),chbPrdDeal.getPayDate());
	            	    	intAmt = intAmt.add(avgIntamt);
	            	    	calAvgIntamtList.add(key);
            	    	}
            	    }
                }

            	value = joinAmt(purAmt, redAmt, intAmt);
            	ccyMap.put(chbPrdDeal.getCcy(), value);

            }else{
            	String value = "";
				if (DictCodeListExt2.产品存续期登记交易类型_存续期申购.getValue().equals(chbPrdDeal.getTradeType())
						||DictCodeListExt2.产品存续期登记交易类型_份额增强.getValue().equals(chbPrdDeal.getTradeType())) {

					purAmt = ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
							chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap);
				}
				else if (DictCodeListExt2.产品存续期登记交易类型_存续期赎回.getValue().equals(chbPrdDeal.getTradeType())
						|| DictCodeListExt2.产品存续期登记交易类型_提前赎回.getValue().equals(chbPrdDeal.getTradeType())
						|| DictCodeListExt2.产品存续期登记交易类型_到期兑付.getValue().equals(chbPrdDeal.getTradeType())
						||DictCodeListExt2.产品存续期登记交易类型_份额减弱.getValue().equals(chbPrdDeal.getTradeType())) {
            	   if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
           	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {

            		   redAmt = redAmt.add(ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
       	    								chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap)
//            				          .add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
//            				        	    ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
//             	    								chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap))
            				          );

            		   intAmt = intAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
				            			   ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
												chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));

	           	   }else{
		           		//同一天既有赎回(到期)又有现金分红,收益只取一次
		           		String key = model.getProdId() + "-" + chbPrdDeal.getPayDate() + "-" + chbPrdDeal.getCcy();
	        	    	if(!calAvgIntamtList.contains(key)){
	            	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getCcy(),chbPrdDeal.getPayDate());
	            	    	intAmt = intAmt.add(avgIntamt);
	            	    	calAvgIntamtList.add(key);
	        	    	}

	           	    	redAmt = redAmt.add(ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
	       	    							chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap));
	           	   }

                }
                else if (DictCodeListExt2.产品存续期登记交易类型_收益再投资.getValue().equals(
                       chbPrdDeal.getTradeType())) {
                	redAmt = redAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
                		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
    				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
                	if (DictCodeListExt2.产品运作模式_开放式净值型.getValue().equals(model.getOperateMode())
               	            || DictCodeListExt2.产品运作模式_封闭式净值型.getValue().equals(model.getOperateMode())) {
	                	//同一天既有红利再投又有现金分红,收益只取一次
	                	String key = model.getProdId() + "-" + chbPrdDeal.getPayDate() + "-" + chbPrdDeal.getCcy();
	        	    	if(!calAvgIntamtList.contains(key)){
	            	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getCcy(),chbPrdDeal.getPayDate());
	            	    	intAmt = intAmt.add(avgIntamt);
	            	    	calAvgIntamtList.add(key);
	        	    	}
	        	    	//add by 20200608 净值型产品的认购金额加上红利再投
	        	    	purAmt = purAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
	                		ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
	    				        chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
                	}

                }
                else if (DictCodeListExt2.产品存续期登记交易类型_收益分配.getValue().equals(
                    chbPrdDeal.getTradeType())) {

                	redAmt = redAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
			                		    ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
										chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));

                	if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
               	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {

                		intAmt = intAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
				                			ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
					    								chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
	           	   }else{
	        	    	//同一天既有赎回(到期)又有现金分红,收益只取一次
	           		String key = model.getProdId() + "-" + chbPrdDeal.getPayDate() + "-" + chbPrdDeal.getCcy();
        	    	if(!calAvgIntamtList.contains(key)){
            	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getCcy(),chbPrdDeal.getPayDate());
            	    	intAmt = intAmt.add(avgIntamt);
            	    	calAvgIntamtList.add(key);
        	    	}
	           	   }
                }
            	value = joinAmt(purAmt, redAmt, intAmt);
            	ccyMap.put(chbPrdDeal.getCcy(), value);
            }
        }
		if (StringUtil.isNotEmpty(chbPrdDeal.getInterestCcy())) {
				//申购金额
	        	BigDecimal purAmt = BigDecimal.ZERO;
	        	//赎回金额
	        	BigDecimal redAmt = BigDecimal.ZERO;
	        	//兑付收益
	        	BigDecimal intAmt = BigDecimal.ZERO;
	            if(ccyMap.containsKey(chbPrdDeal.getInterestCcy())){
	            	String value = ccyMap.get(chbPrdDeal.getInterestCcy());
	            	purAmt = new BigDecimal(value.split(",")[0]);
	            	redAmt = new BigDecimal(value.split(",")[1]);
					if (DictCodeListExt2.产品存续期登记交易类型_存续期赎回.getValue().equals(chbPrdDeal.getTradeType())
							|| DictCodeListExt2.产品存续期登记交易类型_提前赎回.getValue().equals(chbPrdDeal.getTradeType())
							|| DictCodeListExt2.产品存续期登记交易类型_到期兑付.getValue().equals(chbPrdDeal.getTradeType())
							||DictCodeListExt2.产品存续期登记交易类型_份额减弱.getValue().equals(chbPrdDeal.getTradeType())){
	            		if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
		           	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {
		            		redAmt = redAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
						            			ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
				 	    								chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
		           	    }else{
            	    		//兑付金额  = 赎回金额 + 现金分红
    	       	    		redAmt = redAmt.add(ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
    	       	    							chbPrdDeal.getTradePrince(), model.getOpenEnd(), rateMap));
		           	    }
	            	}
	            	intAmt = new BigDecimal(value.split(",")[2]);
	            	if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
	           	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {
	            		intAmt = intAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
					            			ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
					    								chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
	           	    }else{
	           	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getInterestCcy(),chbPrdDeal.getPayDate());
            	    	intAmt = intAmt.add(avgIntamt);
	           	    }

	            	value = joinAmt(purAmt, redAmt, intAmt);
	            	ccyMap.put(chbPrdDeal.getInterestCcy(), value);

	            }else{
	            	String value = "";
	            	if (DictCodeListExt2.产品运作模式_开放式非净值型.getValue().equals(model.getOperateMode())
	           	            || DictCodeListExt2.产品运作模式_封闭式非净值型.getValue().equals(model.getOperateMode())) {
	            		intAmt = intAmt.add(null == chbPrdDeal.getIntAmt()?BigDecimal.ZERO:
					            			ChbCcyConvertUtil.changeCnyMoney(chbPrdDeal.getCcy(),
					    								chbPrdDeal.getIntAmt(), model.getOpenEnd(), rateMap));
	           	    }else{
	           	    	BigDecimal avgIntamt = calAvgIntamt(model,chbPrdDeal.getInterestCcy(),chbPrdDeal.getPayDate());
            	    	intAmt = intAmt.add(avgIntamt);
	           	    }
	            	value = joinAmt(purAmt, redAmt, intAmt);
	            	ccyMap.put(chbPrdDeal.getInterestCcy(), value);
	            }
	    }
	}


	/**
	 *
	 * @param purAmt
	 * @param redAmt
	 * @param intAmt
	 * @return
	 */
	private String joinAmt(BigDecimal purAmt, BigDecimal redAmt, BigDecimal intAmt) {
		String value;
		value = (BigDecimal.ZERO.compareTo(purAmt)==0? "0.00" : purAmt.setScale(2, RoundingMode.HALF_UP).toString()) + ","
				+ (BigDecimal.ZERO.compareTo(redAmt)==0? "0.00" :redAmt.setScale(2, RoundingMode.HALF_UP).toString()) + ","
				+ (BigDecimal.ZERO.compareTo(intAmt)==0? "0.00" :intAmt.setScale(2, RoundingMode.HALF_UP).toString());
		return value;
	}

	/**
	 * 初始值
	 * @param result
	 */
	private void initChbPrdDealSub(ChbPrdDealSub result) {
		//申购份额
    	result.setPurchaseQty(BigDecimal.ZERO);
    	//赎回份额
    	result.setRedeemQty(BigDecimal.ZERO);
    	//银行收益
    	result.setBankImpInt(BigDecimal.ZERO);
	}

	/**
	 * 子产品算术平均计算收益率
	 * @param model
	 * @param quotationDate
	 * @param type
	 * @return
	 */
	private BigDecimal calAvgYield(ChbPrdDurationDeal model,Date quotationDate,String type) {
		BigDecimal avgYield = BigDecimal.ZERO;
		//所有子产品认购交易
		 List<ChbPrdDeal> subList = chbPrdDurationRegMapper.getSubPrdDeal(model.getProdId());
		 if(!StringUtil.isEmptyList(subList)){
			 BigDecimal intamt = BigDecimal.ZERO;
			 BigDecimal totalAmt = BigDecimal.ZERO;
			 BigDecimal yield = BigDecimal.ZERO;
			 for(ChbPrdDeal dl : subList){
				 ChbPrdPeriodYield yieldModel = chbPrdDurationRegMapper.getReaYield(dl.getProdId(),quotationDate, type);
             	if(yieldModel!=null)
             		yield = yieldModel.getProdValue();
				 if(null == yield){
					 yieldModel = chbPrdDurationRegMapper.getReaYield(model.getProdId(),quotationDate, type);
					 //母产品的报价
					 yield =  yieldModel.getProdValue();
				 }
				 intamt = intamt.add(dl.getTradePrince().multiply(null == yield?BigDecimal.ZERO:yield));
				 totalAmt = totalAmt.add(dl.getTradePrince());
			 }
			 avgYield = intamt.divide(totalAmt,7,RoundingMode.HALF_UP);
		 }
		return avgYield;
	}

	/**
	 * 净值类产品按照头寸移动平均计算后的利息处理
	 * @param model
	 * @param ccy
	 * @param quotationDate
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private BigDecimal calAvgIntamt(ChbPrdDurationDeal model,String ccy, Date quotationDate) {
		BigDecimal intamt = BigDecimal.ZERO;
		// 存汇率map
        Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
		List<ChbPrdPosition> intamtList = chbPrdDurationRegMapper.getIntAmt(model.getProdId(),
				                                                            ccy,
																			quotationDate);
		for(ChbPrdPosition pos : intamtList){
			intamt = intamt.add(ChbCcyConvertUtil.changeCnyMoney(pos.getCcy(),
	                pos.getTdaddIntamt(), model.getOpenEnd(), rateMap));
		}

		return intamt;
	}

	/**
	 * 将集合均分成几个大小一致的子集合
	 * @return
	 */
	public static List<List<ChbPrdDealSub>>  createPrdDealSubList(List<ChbPrdDealSub> targe,int size) {
		List<List<ChbPrdDealSub>> listArr = new ArrayList<List<ChbPrdDealSub>>();
		//获取被拆分的数组个数
		int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;
		for(int i=0;i<arrSize;i++) {
			List<ChbPrdDealSub> sub = new ArrayList<ChbPrdDealSub>();
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
	 109      * 功能：得到当前季度季末 格式为：xxxx-yy-zz (eg: 2007-12-31)<br>
	 110      * @return String
	 111      * @author pure
	 112      */
	public static String thisSeasonEnd(Date  date) {
		String dateString = "";
		Calendar localTime=Calendar.getInstance();
		localTime.setTime(date);
		int   x = localTime.get(Calendar.YEAR);
		int  y = localTime.get(Calendar.MONTH) + 1;
		if (y >= 1 && y <= 3) {
			dateString = x + "-" + "03" + "-" + "31";
		}
		if (y >= 4 && y <= 6) {
			dateString = x + "-" + "06" + "-" + "30";
		}
		if (y >= 7 && y <= 9) {
			dateString = x + "-" + "09" + "-" + "30";
		}
		if (y >= 10 && y <= 12) {
			dateString = x + "-" + "12" + "-" + "31";
		}
		return dateString;
	}

	@Override
	public void execute(Date executeDate, List<String> prodList, List<String> assetList, Map<String, List<String>> param) throws BusinessException {

	}

	@Override
	public <T> boolean compareData(T info, T sub) {
		String compare1 = JSON.toJSONString(genDto((ChbPrdDealSub)info));
		String compare2 = JSON.toJSONString(genDto((ChbPrdDealSub)sub));
		if(!StringUtil.equals(compare1, compare2)){
			logger.info("业务表对应数据为：{}", compare1);
			logger.info("登记表对应数据为：{}", compare2);
			return true;
		}else{
			return false;
		}
	}

	private ChbPrdDealSub genDto(ChbPrdDealSub dto){
		ChbPrdDealSub genDto = new ChbPrdDealSub();
		genDto.setProdId(dto.getProdId());
		if(!MultilevelDataAuthorityUtil.isEnabled()){
			genDto.setIssuePartyId(dto.getIssuePartyId());
		}
		genDto.setProdRegistCode(dto.getProdRegistCode());
		genDto.setVdate(dto.getVdate());
		genDto.setMdate(dto.getMdate());
		genDto.setBankImpInt(dto.getBankImpInt() != null ? dto.getBankImpInt().setScale(2) : null);
		genDto.setBalanceAmt(dto.getBalanceAmt() != null ? dto.getBalanceAmt().setScale(2) : null);
		genDto.setNewExpYield(dto.getNewExpYield() != null ? dto.getNewExpYield().setScale(5) : null);
		genDto.setPurchaseQty(dto.getPurchaseQty() != null ? dto.getPurchaseQty().setScale(5) : null);
		genDto.setRedeemQty(dto.getRedeemQty() != null ? dto.getRedeemQty().setScale(5) : null);
		genDto.setPrdQty(dto.getPrdQty() != null ? dto.getPrdQty().setScale(5) : null);
		genDto.setCcyPurchase(dto.getCcyPurchase());
		genDto.setOriginalValue(dto.getOriginalValue() != null ? dto.getOriginalValue().setScale(5) : null);
		genDto.setPrdValue(dto.getPrdValue() != null ? dto.getPrdValue().setScale(5) : null);
		genDto.setTotalValue(dto.getTotalValue() != null ? dto.getTotalValue().setScale(5) : null);
		genDto.setValueCcy(dto.getValueCcy());
		genDto.setCnyOrlValue(dto.getCnyOrlValue() != null ? dto.getCnyOrlValue().setScale(5) : null);
		genDto.setCnyPrdValue(dto.getCnyPrdValue() != null ? dto.getCnyPrdValue().setScale(5) : null);
		genDto.setCnyTalValue(dto.getCnyTalValue() != null ? dto.getCnyTalValue().setScale(5) : null);
		genDto.setReaYield(dto.getReaYield() != null ? dto.getReaYield().setScale(5) : null);
		genDto.setShareAmtWan(dto.getShareAmtWan() != null ? dto.getShareAmtWan().setScale(5) : null);
		genDto.setCashAmtWan(dto.getCashAmtWan() != null ? dto.getCashAmtWan().setScale(5) : null);
		genDto.setOrgCode(dto.getOrgCode());
		genDto.setRemark(dto.getRemark());
		genDto.setCreateTime(null);
		genDto.setUpdateTime(null);

		return genDto;
	}
}
