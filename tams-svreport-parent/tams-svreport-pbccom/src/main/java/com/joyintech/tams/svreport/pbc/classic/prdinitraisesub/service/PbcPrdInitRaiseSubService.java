package com.joyintech.tams.svreport.pbc.classic.prdinitraisesub.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.svreport.base.service.ChbTypeMappingService;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.DeadlineUtil;
import com.joyintech.tams.report.interf.common.constant.DateUtil;
import com.joyintech.tams.svreport.pbc.classic.prdinitraisesub.dao.PbcInitProdRaisingSubMapper;
import com.joyintech.tams.svreport.pbc.classic.prdinitraisesub.dto.PbcInitProdRaisingSub;
import com.joyintech.tams.svreport.pbc.common.service.PbcClassicsSubService;
import com.joyintech.tams.svreport.position.dto.ChbPrdPosition;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
* 功能:人行单产品报表-产品起始募集信息
* @since 1.7.0
* @version 1.7.0
* @作者：jiangxiao
* @创建时间：2019年6月5日
* @修改记录：
*/
@Service
@Transactional(rollbackFor = Exception.class)
public class PbcPrdInitRaiseSubService extends PbcClassicsSubService<PbcInitProdRaisingSub>{
	
//	'036->AUD，124->CAD，156->CNY，344->HKD，392->JPY，826->GBP，840->USD，978 ->EUR
	private static Map<String, String> cnyMap = new HashMap<String, String>();
	static {
		cnyMap.put("036", "AUD");
		cnyMap.put("124", "CAD");
		cnyMap.put("156", "CNY");
		cnyMap.put("344", "HKD");
		cnyMap.put("392", "JPY");
		cnyMap.put("826", "GBP");
		cnyMap.put("840", "USD");
		cnyMap.put("978", "EUR");
	}
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PbcInitProdRaisingSubMapper pbcInitProdRaisingSubMapper;
	
	@Autowired
    private ChbTypeMappingService chbTypeMappingService;
	
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
		//查询需要生成登记的产品列表
		List<PbcInitProdRaisingSub> raisingSubList = new ArrayList<PbcInitProdRaisingSub>();
		String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
		if(!CollectionUtils.isEmpty(prodIdList)){
			int end = 0;
			for(int i=0;i<prodIdList.size();){
				end = i+100;
				if(end>prodIdList.size()){
					end = prodIdList.size();
				}
				raisingSubList.addAll(pbcInitProdRaisingSubMapper.selectPrdRegistInfo(DateUtil.format(executeDate),prodIdList.subList(i, end)));
				i = end;
			}
		}else{
			raisingSubList = pbcInitProdRaisingSubMapper.selectPrdRegistInfo(DateUtil.format(executeDate),prodIdList);
		}
		if(CollectionUtils.isEmpty(raisingSubList)){
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<PbcInitProdRaisingSub>  listsub=raisingSubList.stream()
         .collect(
                 Collectors.collectingAndThen(
                         Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(pbcInitProdRaisingSub -> pbcInitProdRaisingSub.getProdId()+ pbcInitProdRaisingSub.getVdate()))),
                         ArrayList::new
                 )
         );
		//查询产品头寸
		List<ChbPrdPosition> positionList = new ArrayList<ChbPrdPosition>();
		int end = 0;
		for(int i=0;i<listsub.size();){
			end = i+100;
			if(end>listsub.size()){
				end = listsub.size();
			}
            if(StringUtil.equals(bankCode, "001")){
            	positionList.addAll(pbcInitProdRaisingSubMapper.selectPsbcPrdposition(listsub.subList(i, end)));
            }else{
            	positionList.addAll(pbcInitProdRaisingSubMapper.selectPrdposition(listsub.subList(i, end)));
            }
			i = end;
		}
		//判断产品认购交易数据状态(处理留痕)
		Map<String,PbcInitProdRaisingSub> prodmap = new HashMap<String,PbcInitProdRaisingSub>();
		for (PbcInitProdRaisingSub dto : raisingSubList) {
			if (!DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())) {
				prodmap.put(dto.getPbcProdId(), dto);
			} else if (prodmap.get(dto.getPbcProdId()) == null
					&& DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus())) {
				prodmap.put(dto.getPbcProdId(), dto);
			}
		}
		//处理登记数据
		//新增列表
		List<PbcInitProdRaisingSub> insertlist = new ArrayList<PbcInitProdRaisingSub>();
		//删除列表
		List<PbcInitProdRaisingSub> deletelist = new ArrayList<PbcInitProdRaisingSub>();
		Iterator<Map.Entry<String,PbcInitProdRaisingSub>> iterator = prodmap.entrySet().iterator();
		logger.debug("人行产品起始募集登记数据处理开始");
		while(iterator.hasNext()){
			Map.Entry<String,PbcInitProdRaisingSub> entry = iterator.next();
			PbcInitProdRaisingSub subDto = entry.getValue();
			//临时头寸list
			List<ChbPrdPosition> poslist = new ArrayList<ChbPrdPosition>();
			for(ChbPrdPosition pos : positionList){
				if(StringUtil.equals(bankCode, "001")){
					if(cnyMap.get(pos.getCcy())!=null){
						pos.setCcy(cnyMap.get(pos.getCcy()));
					}
				}
				if(pos.getProdId().equals(subDto.getProdId())){
					poslist.add(pos);
				}
			}
			subDto.setPrdPositionList(poslist);
			// 按产品维度生成起始募集信息Map(key:产品id+销售区域+客户类型+币种)
			// 没有头寸数据返回空
			Map<String,PbcInitProdRaisingSub> result = raisingdataProcess(subDto,executeDate);
			// 按产品维度查询已登记的起始募集信息
			List<PbcInitProdRaisingSub> raisingRegList = pbcInitProdRaisingSubMapper.selectRegList(subDto);
			// 已登记的Map(key:产品id+销售区域+客户类型+币种)
			Map<String,PbcInitProdRaisingSub> regMap = new HashMap<String,PbcInitProdRaisingSub>();
			// 已登记的数据状态
			String status = "";
			if(CollectionUtils.isNotEmpty(raisingRegList)){
				//按照产品、销售区域、客户类型、币种维度生成map
				for(PbcInitProdRaisingSub reg : raisingRegList){
					status = reg.getStatus();
					String key = reg.getProdId()+"|"+reg.getBuyPlace()+"|"+reg.getCustomerType1()+"|"+reg.getCcy()+"|";
					regMap.put(key, reg);
				}
			}
			// 没有已登记的数据且没有头寸，删除未报送数据
			if(MapUtils.isEmpty(regMap)&&MapUtils.isEmpty(result)){
				deletelist.add(subDto);
			}
			// 如果有新建或者修改已登记数据，且生成的数据为空，则生成删除未报送的数据
			if(MapUtils.isNotEmpty(regMap)&&MapUtils.isEmpty(result)){
				for(String key : regMap.keySet()){
					if(!DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(key).getStatus())){
						regMap.get(key).setRegistSeqno(IDGenerator.generate(36));
						regMap.get(key).setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
						regMap.get(key).setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
						regMap.get(key).setSubmitFeedbackStatus(null);
						deletelist.add(regMap.get(key));
						insertlist.add(regMap.get(key));
					}
				}
			}
			// 比较数据
			boolean isupdate = compareMap(regMap,result);
			// 循环生成的数据
			Iterator<Map.Entry<String,PbcInitProdRaisingSub>> iter = result.entrySet().iterator();
			while (iter.hasNext()) {
				PbcInitProdRaisingSub sub = iter.next().getValue();
//				String key = sub.getProdId() + "|" + sub.getBuyPlace() + "|" + sub.getCustomerType1() + "|"
//						+ sub.getCcy() + "|";
				if (CollectionUtils.isEmpty(regMap.keySet())) {// 没有已报送的
					// 若数据状态为删除不生成登记数据
					if (DictCodeListExt2.数据状态_删除.getValue().equals(subDto.getStatus())) {
						// 删除数据(未报送的删除)
						deletelist.add(sub);
						continue;
					} else {
						// 设置更新状态
						sub.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
						sub.setCreateUser(CommonUtil.getUserName());
						sub.setUpdateUser(CommonUtil.getUserName());
						insertlist.add(sub);
						// 删除数据(未报送的删除)
						deletelist.add(sub);
					}
				} else {// 已有已报送的
					// 删除数据(未报送的删除)
					deletelist.add(sub);
					// 业务数据状态为D时，若报送数据状态为删除则不生成登记，否则生成数据未报送
					if (DictCodeListExt2.数据状态_删除.getValue().equals(subDto.getStatus())) {
						if (DictCodeListExt2.报送数据状态_删除.getValue().equals(status)) {
							continue;
						}
						sub.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
					} else {
						// 业务数据状态为新建或者修改时，若报送数据状态为删除则生成新建未报送
						if (DictCodeListExt2.报送数据状态_删除.getValue().equals(status)) {
							sub.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
						} else if (isupdate) {// 若报送数据状态不为删除，判断要素表数据有没有修改，若修改生成修改未报送
							continue;
						} else {
							sub.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
						}
						sub.setCreateUser(CommonUtil.getUserName());
						sub.setUpdateUser(CommonUtil.getUserName());
					}
					insertlist.add(sub);
				}
			}
		}
		logger.debug("人行产品起始募集登记数据处理结束");
		//入库
		intoDatabase(insertlist,deletelist);
	}
	
	private Map<String,PbcInitProdRaisingSub> raisingdataProcess(PbcInitProdRaisingSub dto,Date executeDate){
		Map<String,PbcInitProdRaisingSub> resultMap = new HashMap<String,PbcInitProdRaisingSub>();
		String exchangeSource = CacheUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE_PBC);
		Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
		if(!CollectionUtils.isEmpty(dto.getPrdPositionList())){
			//报送截止日生成
			//当前批次报送时点集合
	        Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
			List<Date> dateList = DeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_产品起始募集信息.getValue(), 
                    DictCodeListExt2.报送数据状态_新建.getValue(), dateMap,
                    dto.getVdate(),SVReportGlobalConstant.SVR_SUBMIT_TIME_PBC);
			String bankCode=SvrDataUtil.getSysParam(SVReportGlobalConstant.CUSTOMER_BANK_CODE);
			for(ChbPrdPosition pos : dto.getPrdPositionList()){
				//根据客户二级类型映射成客户一级类型
				String customerType1 = chbTypeMappingService.getMappingValue("PBC_RAISING_INVESTOR_TYPE", pos.getCustomerType2());
				String buyPlace = pos.getBuyplace();
                // 通过销售区域获取上级省
				if(!StringUtil.equals(bankCode, "001")){
					if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_县, buyPlace) != null){
						buyPlace = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_县, buyPlace);
					} 
					if (SvrDataUtil.getNameFromDataDict(DictCode.行政区划_市, buyPlace) != null){
						buyPlace = SvrDataUtil.getParentCodeFromDataDict(DictCode.行政区划_市, buyPlace);
					}
				}
				//按照产品、销售区域、客户类型、币种维度生成登记数据
				String key = pos.getProdId()+"|"+buyPlace+"|"+customerType1+"|"+pos.getCcy()+"|";
				if(resultMap.get(key)==null){
					PbcInitProdRaisingSub sub = new PbcInitProdRaisingSub();
					//产品代码
					sub.setPbcProdId(dto.getPbcProdId());
					//产品内部代码
					sub.setProdId(dto.getProdId());
					//地区代码
					sub.setBuyPlace(buyPlace);
					//客户类型
					sub.setCustomerType1(customerType1);
					//币种代码
					sub.setCcy(pos.getCcy());
					//起始募集金额
					BigDecimal amtsum = (sub.getInitRaisingAmtsum()==null?BigDecimal.ZERO:sub.getInitRaisingAmtsum()).add(pos.getBalanceAmt());
					sub.setInitRaisingAmtsum(amtsum);
					//起始募集金额折人民币
					if(!DictCodeListExt1.币种_中债_人民币.getValue().equals(pos.getCcy())){
						BigDecimal amtsumcny = ChbCcyConvertUtil.exchangeMoney(pos.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), pos.getCdate(), amtsum, rateMap, exchangeSource);
						sub.setInitRaisingAmtsumcny(amtsumcny);
					}
					//起始募集份额
					BigDecimal initRaisingQty = (sub.getInitRaisingQty()==null?BigDecimal.ZERO:sub.getInitRaisingQty()).add(pos.getBalanceQty());
					sub.setInitRaisingQty(initRaisingQty);
					sub.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
					sub.setRegistSeqno(IDGenerator.generate(36));
			        //数据日期
					sub.setStatusDate(executeDate);
					sub.setCreateUser(CommonUtil.getUserName());
					sub.setUpdateUser(CommonUtil.getUserName());
					sub.setCreateDept(CommonUtil.getDeptCode());
					sub.setOrgCode(CommonUtil.getOrgCode());
					if (!StringUtil.isEmptyList(dateList)) {
						sub.setPbcSubmitDeadline(dateList.get(0));
						sub.setSubmitDeadline(dateList.get(1));
					}
					resultMap.put(key, sub);
				} else {
					PbcInitProdRaisingSub sub = resultMap.get(key);
 					//起始募集金额
					BigDecimal amtsum = (sub.getInitRaisingAmtsum()==null?BigDecimal.ZERO:sub.getInitRaisingAmtsum()).add(pos.getBalanceAmt());
					sub.setInitRaisingAmtsum(amtsum);
					//起始募集金额折人民币
					if(!DictCodeListExt1.币种_中债_人民币.getValue().equals(pos.getCcy())){
						BigDecimal amtsumcny = ChbCcyConvertUtil.exchangeMoney(pos.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), pos.getCdate(), amtsum, rateMap, exchangeSource);
						sub.setInitRaisingAmtsumcny(amtsumcny);
					}
					//起始募集份额
					BigDecimal initRaisingQty = (sub.getInitRaisingQty()==null?BigDecimal.ZERO:sub.getInitRaisingQty()).add(pos.getBalanceQty());
					sub.setInitRaisingQty(initRaisingQty);
					
				}
			}
		}
		return resultMap;
	}
	
	private void intoDatabase(List<PbcInitProdRaisingSub> insertlist, List<PbcInitProdRaisingSub> deletelist) {
		if (CollectionUtils.isNotEmpty(deletelist)) {
		    // 插入前先删除未报送的登记数据
		    logger.debug("人行产品起始募集信息删除未报送数据：删除开始----------");
		    // 数据批量一次删除条数,如果参数设置过大则强制定位50
            int batchDeleteCount = reportSelectOrDeleteCount;
            // 每批最后一个的下标
            int batchDeleteLastIndex = batchDeleteCount;            
            for(int index = 0; index < deletelist.size();){
                if(batchDeleteLastIndex > deletelist.size()-1){
                    batchDeleteLastIndex = deletelist.size();
                    pbcInitProdRaisingSubMapper.deleteBatch(deletelist.subList(index, batchDeleteLastIndex));
                    break;//数据插入完毕,退出循环
                }else{
                    pbcInitProdRaisingSubMapper.deleteBatch(deletelist.subList(index, batchDeleteLastIndex));
                    index = batchDeleteLastIndex;//设置下一批下标
                    batchDeleteLastIndex = index + batchDeleteCount;                     
                }
            }
            logger.debug("人行产品起始募集信息删除未报送数据：删除结束----------");
            logger.debug("人行产品起始募集信息插入数据：插入开始----------");
			int count = 0;
			SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
	    	try{
	    		PbcInitProdRaisingSubMapper batchMapper =  session.getMapper(PbcInitProdRaisingSubMapper.class);
	    		for(PbcInitProdRaisingSub dto : insertlist){
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
	    		logger.error("人行产品起始募集信息入库报错:"+e.getMessage(),e);
	    	} finally {
	    		session.close();
	    	}
            logger.debug("人行产品起始募集信息插入数据：插入结束----------");
		}
	}
	
	private boolean compareMap(Map<String, PbcInitProdRaisingSub> regMap, Map<String, PbcInitProdRaisingSub> result) {
		if(MapUtils.isEmpty(regMap)||MapUtils.isEmpty(result)){
			return false;
		}
		if(regMap.size()!=result.size()){
			return false;
		}
		Iterator<Map.Entry<String, PbcInitProdRaisingSub>> iterator = regMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, PbcInitProdRaisingSub> entry = iterator.next();
			PbcInitProdRaisingSub reg = entry.getValue();
			String key = entry.getKey();
			if(result.get(key)==null||!compareData(reg,result.get(key))){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @Description 比较数据，数据相同返回true
	 * @param dto 
	 * @param regdto
	 * @return boolean
	 * @version 1.7
	 */
	private boolean compareData(PbcInitProdRaisingSub dto,PbcInitProdRaisingSub regdto){
		PbcInitProdRaisingSub compare1 = genDto(dto);
		PbcInitProdRaisingSub compare2 = genDto(regdto);
        if(JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
            return true;
        }else{
            return false;
        }
    }

	private PbcInitProdRaisingSub genDto(PbcInitProdRaisingSub regdto) {
		PbcInitProdRaisingSub sub = new PbcInitProdRaisingSub();
		sub.setPbcProdId(regdto.getPbcProdId());
		sub.setBuyPlace(regdto.getBuyPlace());
		sub.setCustomerType1(regdto.getCustomerType1());
		sub.setCcy(regdto.getCcy());
		sub.setInitRaisingAmtsum((regdto.getInitRaisingAmtsum()==null?BigDecimal.ZERO:regdto.getInitRaisingAmtsum()).setScale(2, BigDecimal.ROUND_HALF_UP));
		sub.setInitRaisingAmtsumcny((regdto.getInitRaisingAmtsumcny()==null?BigDecimal.ZERO:regdto.getInitRaisingAmtsumcny()).setScale(2, BigDecimal.ROUND_HALF_UP));
		sub.setInitRaisingQty((regdto.getInitRaisingQty()==null?BigDecimal.ZERO:regdto.getInitRaisingQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
		return sub;
	}
}