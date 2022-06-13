package com.joyintech.tams.svreport.chb.prodraisetotalregist.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.dto.ChbCheckTableInfo;
import com.joyintech.tams.svreport.chb.common.SvrDataStatusUtil;
import com.joyintech.tams.svreport.chb.datacheck.beforeinsert.service.ChbCommonCheckService;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.batch.report.common.db.SvrDBUtil;
import com.joyintech.tams.common.base.entity.ChbPrdDeal;
import com.joyintech.tams.common.base.entity.ChbPrdDealSub;
import com.joyintech.tams.common.svreport.base.service.ChbTypeMappingService;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.util.ChbDeadlineUtil;
import com.joyintech.tams.svreport.chb.prodraisetotalregist.dao.RaiseTotalRegist001Mapper;
import com.joyintech.tams.svreport.chb.prodraisetotalregist.dto.RaiseTotalRegist001Dto;
import com.joyintech.tams.svreport.position.dto.ChbPrdPosition;
import com.joyintech.tams.systemtools.util.CommonUtil;

/**
 * @Description 募集总量登记批处理service
 * @author jiangxiao
 * @version 1.7 2019年5月24日
 * @since 1.7
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProdRaiseTotalCBService extends SvrDataStatusUtil {
	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RaiseTotalRegist001Mapper raiseTotalRegist001Mapper;
	@Value("${application.reportInsertCount}")
	public  int reportInsertCount;

	@Autowired
	private SvrDBUtil svrDBUtil;
	/**
	 * 映射service
	 */
	@Autowired
	private ChbTypeMappingService chbTypeMappingService;
	//个人
	private static final String SINGLE_PERSON = "01";
	//法人
	private static final String LEGAL_PERSON = "02";
	//非法人
	private static final String UNLEGAL_PERSON = "03";

	private  String[] special_buyplace = new String[]{"210200","330200","350200","370200","440300"};

	/**
	 * 入库前校验用Service
	 */
	@Autowired
	private ChbCommonCheckService chbCommonCheckService;

	/**
	 * @Description 募集总量登记生成
	 * @param executeDate 跑批日
	 * @param prodIdList 生成的产品id
	 * @return void
	 * @version 1.7 2019年5月24日
	 */
	public void generateRegistData(Date executeDate,List<String> prodIdList){
		//获取系统参数是否邮储个性化 001 邮储个性化 
		String isPsbc=CacheUtil.getSysParam("CUSTOMER_BANK_CODE");
		//查询所有要生成募集总量登记的产品信息
		List<RaiseTotalRegist001Dto> prodlistsum = raiseTotalRegist001Mapper.selectRaiseTotallist(DateUtil.format(executeDate),prodIdList);
		if(CollectionUtils.isEmpty(prodlistsum)){
			return;
		}
		//查询已登记数据
		Map<String,RaiseTotalRegist001Dto> regMap = new HashMap<String,RaiseTotalRegist001Dto>();
		List<RaiseTotalRegist001Dto> reglist = raiseTotalRegist001Mapper.selectRegPerProdList(prodlistsum);
		if(!CollectionUtils.isEmpty(reglist)){
			for(RaiseTotalRegist001Dto regdto : reglist){
				regMap.put(regdto.getProdId(), regdto);
			}
		}
		List<ChbPrdPosition> positionlist = new ArrayList<ChbPrdPosition>();
		List<ChbPrdDeal> deallist = new ArrayList<ChbPrdDeal>();
		logger.debug("查询头寸信息开始---------------"+new Date());

//        positionlist=raiseTotalRegist001Mapper.selectPositionData(prodlistsum);
		deallist=raiseTotalRegist001Mapper.selectDealData(prodlistsum);
		logger.debug("查询头寸信息结束---------------"+new Date());
		List<RaiseTotalRegist001Dto> prodtotallist = new ArrayList<RaiseTotalRegist001Dto>();
		Map<String,RaiseTotalRegist001Dto> prodmap = new HashMap<String,RaiseTotalRegist001Dto>();
		//判断认购交易是否全为D
		for (RaiseTotalRegist001Dto dto : prodlistsum) {
			if(DictCodeListExt2.数据状态_有效.getValue().equals(dto.getStatus())||DictCodeListExt2.数据状态_修改.getValue().equals(dto.getStatus())){
				prodmap.put(dto.getProdId(), dto);
			}else if(prodmap.get(dto.getProdId())==null&&(dto.getStatus()==null||DictCodeListExt2.数据状态_删除.getValue().equals(dto.getStatus()))){
				prodmap.put(dto.getProdId(), dto);
			}
		}
		List<RaiseTotalRegist001Dto> insertdtolist = new ArrayList<RaiseTotalRegist001Dto>();
		List<String> deletedtolist = new ArrayList<String>();
		Iterator<Map.Entry<String, RaiseTotalRegist001Dto>> entries = prodmap.entrySet().iterator();
		logger.debug("募集总量登记数据处理开始---------------");
		while(entries.hasNext()){
			Map.Entry<String, RaiseTotalRegist001Dto> dtomap = entries.next();
			RaiseTotalRegist001Dto prodid = dtomap.getValue();
			RaiseTotalRegist001Dto total = new RaiseTotalRegist001Dto();
			List<ChbPrdPosition> posList = new ArrayList<ChbPrdPosition>();
			List<ChbPrdDeal> deList = new ArrayList<ChbPrdDeal>();
			total.setProdId(prodid.getProdId());
			total.setProdRegCode(prodid.getProdRegCode());
			total.setProdRegistCode(prodid.getProdRegistCode());
			total.setStatus(prodid.getStatus());
			total.setBusiAcctNo(prodid.getBusiAcctNo());
			total.setAcctName(prodid.getAcctName());
			//募集结束日
			if(prodid.getRaiseEnddate()==null){
				continue;
			}
			total.setRaiseEnddate(prodid.getRaiseEnddate());
			total.setRegseq(prodid.getRegseq());
			for(ChbPrdPosition pos:positionlist){
				if(prodid.getProdId().equals(pos.getProdId())){
					posList.add(pos);
				}
			}
			for(ChbPrdDeal deal:deallist){
				if(prodid.getProdId().equals(deal.getProdId())){
					deList.add(deal);
				}
			}

			total.setPoslist(posList);
			total.setDeallist(deList);
			prodtotallist.add(total);
			dataProcess(total, executeDate);

			// add by zhangliang 20201109 数据流向处理 ===================================start
			Map<String, RaiseTotalRegist001Dto> handleDataMap = handleDataStatus(total, regMap.get(total.getProdId()));
			if (handleDataMap.containsKey(SvrDataStatusUtil.INSERT_DATA)){
				// 生成删除未报送特殊处理
				if(StringUtil.equals(handleDataMap.get(SvrDataStatusUtil.INSERT_DATA).getStatus(), DictCodeListExt2.报送数据状态_删除.getValue())){
					RaiseTotalRegist001Dto	dto = handleDataMap.get(SvrDataStatusUtil.INSERT_DATA);
					dto.setSubscribeCcyAmt(regMap.get(total.getProdId()).getSubscribeCcyAmt());
					dto.setSaleAreaAmt(regMap.get(total.getProdId()).getSaleAreaAmt());
					dto.setTotalAmt(regMap.get(total.getProdId()).getTotalAmt());
					dto.setIsConsign(regMap.get(total.getProdId()).getIsConsign());
					dto.setConsignAmt(regMap.get(total.getProdId()).getConsignAmt());
					dto.setRaiseQty(regMap.get(total.getProdId()).getRaiseQty());
					insertdtolist.add(dto);
				}
				else{
					insertdtolist.add(handleDataMap.get(SvrDataStatusUtil.INSERT_DATA));
				}
			}
			if (handleDataMap.containsKey(SvrDataStatusUtil.DELETE_DATA)){
				deletedtolist.add(handleDataMap.get(SvrDataStatusUtil.DELETE_DATA).getProdId());
			}

//			if (total.getRegseq() == null) {
//				//没有已报送的
//				if(DictCodeListExt1.发行状态_发行失败.getValue().equals(prodid.getIssuingStatus())
//						||prodid.getStatus()==null||DictCodeListExt2.数据状态_删除.getValue().equals(prodid.getStatus())){
//                    //删除数据(未报送的删除)
//                    deletedtolist.add(total.getProdId());
//					continue;
//				}else{
//					//设置更新状态
//					total.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
//					total.setCreateUser(CommonUtil.getUserName());
//                    total.setUpdateUser(CommonUtil.getUserName());
//					insertdtolist.add(total);
//		        	//删除数据(未报送的删除)
//		        	deletedtolist.add(total.getProdId());
//				}
//			}else {
//				//已有已报送的
//				//设置更新状态
//				//删除数据(未报送的删除)
//	        	deletedtolist.add(total.getProdId());
//				if(DictCodeListExt1.发行状态_发行失败.getValue().equals(prodid.getIssuingStatus())){
//					if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(total.getProdId()).getStatus())){
//                		continue;
//                	}
//					total.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
//				}else{
//					if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(total.getProdId()).getStatus())){
//						total.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
//                	}else if(compareData(regMap.get(total.getProdId()),total)){// 判断要素表数据有没有修改
//                        continue;
//                    }else{
//                    	total.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
//                    }
//				}
//                total.setCreateUser(CommonUtil.getUserName());
//                total.setUpdateUser(CommonUtil.getUserName());
//				insertdtolist.add(total);
//			}
			// add by zhangliang 20201109 数据流向处理 ===================================end

		}
		// add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 start
		insertdtolist = chbCommonCheckService.checkData(insertdtolist, ChbCheckTableInfo.募集总量登记SUB);
		// add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 end
		if("001".equals(isPsbc)){
			/**邮储个性化修改 开始**/
			doUpdateData(insertdtolist);
			/**邮储个性化修改 结束**/
		}


		//update by leipeng 插入的数据获取表 内的实际登记编码
		if(CommonUtil.isBAmodel()){
			Map<String,String> data = svrDBUtil.getAllRegCode();

			for (RaiseTotalRegist001Dto dto : insertdtolist) {
				dto.setIssuePartyId(data.get(dto.getProdId()));
			}
		}

		logger.debug("募集总量登记数据处理结束---------------");
		int singalCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"100" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
		int deleteCount = deletedtolist.size();
		int insertCount = insertdtolist.size();
		logger.debug("募集总量登记数据入库开始---------------");
		if(deleteCount>0){
			if(deleteCount<=singalCount){
				raiseTotalRegist001Mapper.deleteBatch(deletedtolist);
			}
			else{
				for(int i = 0; i < deleteCount/singalCount ; i++){
					raiseTotalRegist001Mapper.deleteBatch(deletedtolist.subList(0, singalCount));
					deletedtolist.removeAll(deletedtolist.subList(0, singalCount));
				}
				if(!CollectionUtils.isEmpty(deletedtolist)){
					raiseTotalRegist001Mapper.deleteBatch(deletedtolist);
				}
			}
		}
		if(insertCount>0){
			if(insertCount<=reportInsertCount){
				raiseTotalRegist001Mapper.insertBatch(insertdtolist);
			}
			else{
				for(int i = 0; i < insertCount/reportInsertCount ; i++){
					raiseTotalRegist001Mapper.insertBatch(insertdtolist.subList(0, reportInsertCount));
					insertdtolist.removeAll(insertdtolist.subList(0, reportInsertCount));
				}
				if(!CollectionUtils.isEmpty(insertdtolist)){
					raiseTotalRegist001Mapper.insertBatch(insertdtolist);
				}
			}
		}
		logger.debug("募集总量登记数据入库结束---------------");
	}
	void doUpdateData(List<RaiseTotalRegist001Dto> insertdtolist){
		//查询所有产品理财产品投资者募集接口数据 放入Map
		List<RaiseTotalRegist001Dto> allList=raiseTotalRegist001Mapper.getAllInterfRaiseSum();
		Map<String,RaiseTotalRegist001Dto> updateMap=new HashMap<String,RaiseTotalRegist001Dto>();
		for(RaiseTotalRegist001Dto dto:allList){
			updateMap.put(dto.getProdId(), dto);
		}
		for(RaiseTotalRegist001Dto single:insertdtolist){
			if(updateMap.containsKey(single.getProdId())){
				RaiseTotalRegist001Dto updateDto=updateMap.get(single.getProdId());
				single.setSingleTotal(updateDto.getSingleTotal());
				single.setLegalTotal(updateDto.getLegalTotal());
				single.setUnlegalTotal(updateDto.getUnlegalTotal());
				single.setSaleAreaAmt(updateDto.getSaleAreaAmt());
				single.setIsConsign(updateDto.getIsConsign());
				single.setConsignAmt(updateDto.getConsignAmt());
				if(updateDto.getConsignAmt()==null&&updateDto.getSaleAreaAmt()!=null) {
					String [] xsqys=updateDto.getSaleAreaAmt().split(";");
					BigDecimal consignAmt =BigDecimal.ZERO;
					for(String  xsqy:xsqys) {
						String [] amts= xsqy.split(",");
						if(amts.length>=3) {
							String amtstr=amts[2];
							BigDecimal amt=new BigDecimal(amtstr);
							consignAmt= consignAmt.add(amt);
						}
					}
					single.setConsignAmt(consignAmt);
				}
			}
		}
	}
	/**
	 * @Description 募集总量数据详细处理
	 * @param RaiseTotalRegist001Dto
	 * @param executeDate 跑批日
	 * @param buyPlaceMappingMap
	 * @return RaiseTotalRegist001Dto
	 */
	public RaiseTotalRegist001Dto dataProcess(RaiseTotalRegist001Dto dto,Date executeDate){
		//交易信息
		List<ChbPrdPosition> poslist = dto.getPoslist();
		List<ChbPrdDeal> deallist = dto.getDeallist();
		BigDecimal totAmt = BigDecimal.ZERO;
		BigDecimal totQty = BigDecimal.ZERO;
		Boolean isagency = false;
		BigDecimal isagencyAmt = BigDecimal.ZERO;
		Map<String,BigDecimal> placeMap = new HashMap<String,BigDecimal>();
		Map<String,String> ccyMap = new HashMap<String,String>();
		int singleTotal = 0;
		int legalTotal = 0;
		int unlegalTotal = 0;
		String exchangeSource = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE);
		Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
		if(!CollectionUtils.isEmpty(deallist)){
			for(ChbPrdDeal deal : deallist){
				BigDecimal chaAmt = ChbCcyConvertUtil.exchangeMoney(deal.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), dto.getRaiseEnddate(), deal.getTradePrince(), rateMap, exchangeSource);
				if(!Arrays.asList(special_buyplace).contains(deal.getBuyPlace())){
					//实际募集金额
					totAmt = totAmt.add(chaAmt);
					//募集总份额
					totQty = totQty.add(deal.getTradeQty());
					//认购币种，金额，折算人民币金额(保留两位小数)
					if(ccyMap.get(deal.getCcy())==null){
						ccyMap.put(deal.getCcy(), deal.getTradePrince().setScale(2,BigDecimal.ROUND_HALF_UP).toString()+GlobalConstant.COMMA+chaAmt.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					}else{
						List<String> amtlist = StringUtil.splitToList(GlobalConstant.COMMA,ccyMap.get(deal.getCcy()));
						//原币种金额
						BigDecimal amount = new BigDecimal(amtlist.get(0));
						amount = amount.add(deal.getTradePrince());
						//折算人民币金额
						BigDecimal cnyAmount = new BigDecimal(amtlist.get(1));
						cnyAmount = cnyAmount.add(chaAmt);
						ccyMap.put(deal.getCcy(),amount.setScale(2,BigDecimal.ROUND_HALF_UP).toString()+GlobalConstant.COMMA+cnyAmount.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
					}
				}
				//产品销售区域和募集金额（保留两位小数）
				String buyPlace = deal.getBuyPlace();
				// 映射源数据和中债所需数据
				buyPlace = chbTypeMappingService.getMappingValue("PRD_RAISETOT_BUY_PLACE", deal.getBuyPlace());
				if(buyPlace!=null){
					if(placeMap.get(buyPlace)==null){
						placeMap.put(buyPlace, chaAmt.setScale(2,BigDecimal.ROUND_HALF_UP));
					}else{
						BigDecimal amt = placeMap.get(buyPlace).add(chaAmt);
						placeMap.put(buyPlace, amt.setScale(2,BigDecimal.ROUND_HALF_UP));
					}
					// modify by wm start  针对身份去除特殊五个市处理 20200812 合过来
					if(StringUtil.equals(DictCodeListExt2.销售区域_中债_青岛市.getValue(), buyPlace)){
						BigDecimal provinceAmt = placeMap.get(DictCodeListExt2.销售区域_中债_山东省.getValue())==null?BigDecimal.ZERO:placeMap.get(DictCodeListExt2.销售区域_中债_山东省.getValue());
						placeMap.put(DictCodeListExt2.销售区域_中债_山东省.getValue(), provinceAmt.subtract(chaAmt).setScale(2,BigDecimal.ROUND_HALF_UP));
					}
					else if(StringUtil.equals(DictCodeListExt2.销售区域_中债_大连市.getValue(), buyPlace)){
						BigDecimal provinceAmt = placeMap.get(DictCodeListExt2.销售区域_中债_辽宁省.getValue())==null?BigDecimal.ZERO:placeMap.get(DictCodeListExt2.销售区域_中债_辽宁省.getValue());
						placeMap.put(DictCodeListExt2.销售区域_中债_辽宁省.getValue(), provinceAmt.subtract(chaAmt).setScale(2,BigDecimal.ROUND_HALF_UP));
					}
					else if(StringUtil.equals(DictCodeListExt2.销售区域_中债_宁波市.getValue(), buyPlace)){
						BigDecimal provinceAmt = placeMap.get(DictCodeListExt2.销售区域_中债_浙江省.getValue())==null?BigDecimal.ZERO:placeMap.get(DictCodeListExt2.销售区域_中债_浙江省.getValue());
						placeMap.put(DictCodeListExt2.销售区域_中债_浙江省.getValue(), provinceAmt.subtract(chaAmt).setScale(2,BigDecimal.ROUND_HALF_UP));
					}
					else if(StringUtil.equals(DictCodeListExt2.销售区域_中债_厦门市.getValue(), buyPlace)){
						BigDecimal provinceAmt = placeMap.get(DictCodeListExt2.销售区域_中债_福建省.getValue())==null?BigDecimal.ZERO:placeMap.get(DictCodeListExt2.销售区域_中债_福建省.getValue());
						placeMap.put(DictCodeListExt2.销售区域_中债_福建省.getValue(), provinceAmt.subtract(chaAmt).setScale(2,BigDecimal.ROUND_HALF_UP));
					}
					else if(StringUtil.equals(DictCodeListExt2.销售区域_中债_深圳市.getValue(), buyPlace)){
						BigDecimal provinceAmt = placeMap.get(DictCodeListExt2.销售区域_中债_广东省.getValue())==null?BigDecimal.ZERO:placeMap.get(DictCodeListExt2.销售区域_中债_广东省.getValue());
						placeMap.put(DictCodeListExt2.销售区域_中债_广东省.getValue(), provinceAmt.subtract(chaAmt).setScale(2,BigDecimal.ROUND_HALF_UP));
					}
					// modify by wm  end 针对身份去除特殊五个市处理 20200812 合过来
				/**是否代销和代销金额字段取值改从交易里面取值  start  add by  dongxiangyang 20201224 **/
				if("01".equals(deal.getIsagency())&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_青岛市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_大连市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_宁波市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_厦门市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_深圳市.getValue(), buyPlace)
				){
					isagency=true;
					BigDecimal amt = ChbCcyConvertUtil.exchangeMoney(deal.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), dto.getRaiseEnddate(), deal.getTradePrince(), rateMap, exchangeSource);
					isagencyAmt = isagencyAmt.add(amt);
				}
				}
				/**是否代销和代销金额字段取值改从交易里面取值  start  add by  dongxiangyang 20201224 **/


				/****当系统参数SVR_PRD_DEAL_DIMENSION=02时，三个客户数字段改成从Chb_Prd_Deal表取认购交易的保有客户数字段 leipeng 20210224***/
				String customerType = chbTypeMappingService.getMappingValue("PRD_RAISETOT_INVESTOR_TYPE", deal.getCustomerType2());

				//数据判断不能为特殊地级市才进行统计
				if(!StringUtil.equals(DictCodeListExt2.销售区域_中债_青岛市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_大连市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_宁波市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_厦门市.getValue(), buyPlace)
						&&!StringUtil.equals(DictCodeListExt2.销售区域_中债_深圳市.getValue(), buyPlace)) {
					if (SINGLE_PERSON.equals(customerType)) {
						//个人
						singleTotal += deal.getCustCounts().intValue();
					} else if (LEGAL_PERSON.equals(customerType)) {
						//法人
						legalTotal += deal.getCustCounts().intValue();
					} else if (UNLEGAL_PERSON.equals(customerType)) {
						//非法人
						unlegalTotal += deal.getCustCounts().intValue();
					}
				}
				/****当系统参数SVR_PRD_DEAL_DIMENSION=02时，三个客户数字段改成从Chb_Prd_Deal表取认购交易的保有客户数字段 leipeng 20210224***/

//				singleTotal=singleTotal+(posmodel.getSingletotal()==null?BigDecimal.ZERO:posmodel.getSingletotal()).intValue();
//				legalTotal=legalTotal+(posmodel.getLegaltotal()==null?BigDecimal.ZERO:posmodel.getLegaltotal()).intValue();
//				unlegalTotal=unlegalTotal+(posmodel.getUnlegaltotal()==null?BigDecimal.ZERO:posmodel.getUnlegaltotal()).intValue();


			}
		}
		/**青岛银行关于客户数的调整   add by  dongxiangyang  20201112 start*/
		//客户数是否从投资者明细取值,01 是  02 否
		String IsChbEqudetCount = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_IS_EQUDET_CONUT);
		if(StringUtil.equals(IsChbEqudetCount, DictCodeList.是否_中债_是.getValue())){
			List<RaiseTotalRegist001Dto> countList=raiseTotalRegist001Mapper.getEquCount(dto.getProdId());
			if(!CollectionUtils.isEmpty(countList)){
				for(RaiseTotalRegist001Dto  countDto:countList){
					//个人
					if(StringUtil.equals("01", countDto.getEquType())){
						singleTotal=countDto.getEquCount();
					}
					//法人
					else if(StringUtil.equals("02", countDto.getEquType())){
						legalTotal=countDto.getEquCount();
					}
					//非法人
					else if(StringUtil.equals("03", countDto.getEquType())){
						unlegalTotal=countDto.getEquCount();
					}
				}
			}
		}else {
			if(!CollectionUtils.isEmpty(poslist)) {
				for (ChbPrdPosition posmodel : poslist) {
//				BigDecimal chaAmt = ChbCcyConvertUtil.exchangeMoney(posmodel.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), dto.getRaiseEnddate(), posmodel.getBalanceAmt(), rateMap, exchangeSource);
//				//实际募集金额
//				totAmt = totAmt.add(chaAmt);
//				//募集总份额
//				totQty = totQty.add(posmodel.getBalanceQty());
					//是否有其他机构代销,代销总金额
				/*if(posmodel.getConsignamt()!=null&&posmodel.getConsignamt().compareTo(BigDecimal.ZERO)>0){
					isagency=true;
					BigDecimal amt = ChbCcyConvertUtil.exchangeMoney(posmodel.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), dto.getRaiseEnddate(), posmodel.getConsignamt(), rateMap, exchangeSource);
					isagencyAmt = isagencyAmt.add(amt);
				}
				*/
					String customerType = chbTypeMappingService.getMappingValue("PRD_RAISETOT_INVESTOR_TYPE", posmodel.getCustomerType2());
					if (SINGLE_PERSON.equals(customerType)) {
						//个人
						singleTotal += posmodel.getCustCounts().intValue();
					} else if (LEGAL_PERSON.equals(customerType)) {
						//法人
						legalTotal += posmodel.getCustCounts().intValue();
					} else if (UNLEGAL_PERSON.equals(customerType)) {
						//非法人
						unlegalTotal += posmodel.getCustCounts().intValue();
					}
					singleTotal = singleTotal + (posmodel.getSingletotal() == null ? BigDecimal.ZERO : posmodel.getSingletotal()).intValue();
					legalTotal = legalTotal + (posmodel.getLegaltotal() == null ? BigDecimal.ZERO : posmodel.getLegaltotal()).intValue();
					unlegalTotal = unlegalTotal + (posmodel.getUnlegaltotal() == null ? BigDecimal.ZERO : posmodel.getUnlegaltotal()).intValue();
				}
			}
		}
		dto.setSingleTotal(singleTotal==0?0:singleTotal);//个人
		dto.setLegalTotal(legalTotal==0?0:legalTotal);//法人
		dto.setUnlegalTotal(unlegalTotal==0?0:unlegalTotal);//非法人
		dto.setTotalAmt(totAmt.setScale(2,BigDecimal.ROUND_HALF_UP));//实际募集金额
		dto.setRaiseQty(totQty);//募集总额
		StringBuffer place = new StringBuffer();
		Iterator<Map.Entry<String, BigDecimal>> entries = placeMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, BigDecimal> entry = entries.next();
			//格式化处理：110000,北京市,10000000.00;440300,深圳市,4000000.00
			//金额小于0不统计进去 modify by wm  针对身份去除特殊五个市处理 20200812
			if(entry.getValue().compareTo(BigDecimal.ZERO)>0){
				place.append(entry.getKey()+" ").append(SysDataUtil.getNameFromDataDict(DictCode.销售区域_中债,entry.getKey())+",").append(entry.getValue().toString()).append(";");
			}
			//金额小于0不统计进去 modify by wm  end
		}
		//是否有其他机构代销,代销总金额
		if(isagency){
			dto.setIsConsign(DictCodeList.是否_中债_是.getValue());
			dto.setConsignAmt(isagencyAmt);
		}else{
			dto.setIsConsign(DictCodeList.是否_中债_否.getValue());
		}
		
		//认购金额为0的情况下，也要生成募集总量登记对应的改造  20210910   start
		dto.setSaleAreaAmt("0");//销售区域及金额
		//认购金额为0的情况下，也要生成募集总量登记对应的改造  20210910   end
		
		if(place.length()>0){
			dto.setSaleAreaAmt(place.toString().substring(0, place.length() - 1));//销售区域及金额
		}
		StringBuffer ccyamt = new StringBuffer();
		Iterator<Map.Entry<String, String>> iterator = ccyMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			//格式化处理：CNY,1000.00,10000.00;USD,10000.00,10000.00
			ccyamt.append(entry.getKey()+",").append(entry.getValue().toString()).append(";");
		}
		if(ccyamt.length()>0){
			dto.setSubscribeCcyAmt(ccyamt.toString().substring(0, ccyamt.length() - 1));//认购币种及金额
		}
		dto.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
		dto.setRegistSeqno(IDGenerator.generate(36));
		// 报送流水号
		dto.setSumitSeq(CommonUtil.getSequenceIndexData(DataIndexSequenceType.中债登记管理));
		dto.setStatusDate(executeDate);

		//发行机构代码取值
		dto.setIssuePartyId(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
//		if(StringUtil.isEmpty(dto.getIssuePartyId())){
//			String issuePartyId = CommonUtil.selectBankCode();
//			dto.setIssuePartyId(issuePartyId);
//		}
//		 报送截止日生成
//		当前批次报送时点集合
		String subStaus=dto.getStatus();
		if(StringUtil.equals(dto.getStatus(),DictCodeListExt2.数据状态_有效.getValue())){
			subStaus=DictCodeListExt2.报送数据状态_新建.getValue();
		}else if(StringUtil.equals(dto.getStatus(),DictCodeListExt2.数据状态_修改.getValue())){
			subStaus=DictCodeListExt2.报送数据状态_修改.getValue();
		}else if( dto.getStatus()==null||StringUtil.equals(dto.getStatus(),DictCodeListExt2.数据状态_删除.getValue())){
			subStaus=DictCodeListExt2.报送数据状态_删除.getValue();
		}
		Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
		List<Date> dateList = ChbDeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_募集总量登记.getValue(),
				subStaus,dateMap, dto.getRaiseEnddate());
		if (!StringUtil.isEmptyList(dateList)) {
			dto.setChbSubmitDeadline(dateList.get(0));
			dto.setSubmitDeadline(dateList.get(1));
		}
		return dto;
	}

	@Override
	public void execute(Date executeDate, List<String> prodList, List<String> assetList, Map<String, List<String>> param) throws BusinessException {

	}

	@Override
	public <T> boolean compareData(T info, T sub) {
		return this.compareData((RaiseTotalRegist001Dto) info, (RaiseTotalRegist001Dto) sub);
	}

	public boolean compareData(RaiseTotalRegist001Dto dto,RaiseTotalRegist001Dto regdto){
		RaiseTotalRegist001Dto compare1 = genDto(dto);
		RaiseTotalRegist001Dto compare2 = genDto(regdto);
		if(!JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
			logger.info("info:" + JSON.toJSONString(compare1));
			logger.info("sub:" + JSON.toJSONString(compare2));
			return true;
		}else{
			return false;
		}
	}

	private RaiseTotalRegist001Dto genDto(RaiseTotalRegist001Dto dto) {
		RaiseTotalRegist001Dto gendto = new RaiseTotalRegist001Dto();
		gendto.setSingleTotal(dto.getSingleTotal());
		gendto.setLegalTotal(dto.getLegalTotal());
		gendto.setUnlegalTotal(dto.getUnlegalTotal());
		gendto.setSubscribeCcyAmt(dto.getSubscribeCcyAmt());
		gendto.setSaleAreaAmt(dto.getSaleAreaAmt());
		gendto.setTotalAmt((dto.getTotalAmt()==null?BigDecimal.ZERO:dto.getTotalAmt()).setScale(2, BigDecimal.ROUND_HALF_UP));
		gendto.setRaiseQty((dto.getRaiseQty()==null?BigDecimal.ZERO:dto.getRaiseQty()).setScale(2, BigDecimal.ROUND_HALF_UP));
		gendto.setIsConsign(dto.getIsConsign());
		gendto.setConsignAmt((dto.getConsignAmt()==null?BigDecimal.ZERO:dto.getConsignAmt()).setScale(2, BigDecimal.ROUND_HALF_UP));
		return gendto;
	}


}
