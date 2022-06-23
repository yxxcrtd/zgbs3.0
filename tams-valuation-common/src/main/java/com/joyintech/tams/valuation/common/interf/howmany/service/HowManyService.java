package com.joyintech.tams.valuation.common.interf.howmany.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.BokDetailIdMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.common.ResultCode;
import com.joyintech.tams.valuation.common.interf.common.ResultObject;
import com.joyintech.tams.valuation.common.interf.howmany.common.HowManyCommonService;
import com.joyintech.tams.valuation.common.interf.howmany.dao.HowManyCommonMapper;
import com.joyintech.tams.valuation.common.interf.howmany.dto.BokBookdetailCurrentDto;
import com.joyintech.tams.valuation.common.interf.howmany.dto.TrdProductDealDto;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.common.service.DataTransplantCommonService;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口共通
 * 
 * @author wujie
 * @version 1.0 2019年5月15日
 * @see HowManyService
 * @since 1.0
 */
@Service
public class HowManyService {
	
	@Autowired
	private HowManyCommonService howManyCommonService;
	
	@Autowired
	private DataTransplantCommonService dataTransplantCommonService;
	
	@Autowired
	private BookAccountCommonService bookAccountCommonService;
	
	@Autowired
	private HowManyCommonMapper howManyCommonMapper;
	
	@Autowired
	private BokDetailIdMapper bokDetailIdMapper;
	
	@Autowired
	private DBUtil dBUtil;
	/**
	 * 几许交易入账_初始
	 * @param acctSubjectId 核算主体代码
	 * @param trdProductDealDto 金融产品交易
	 * @return ResultObject 处理结果
	 */
	public ResultObject dealAccount(String acctSubjectId, TrdProductDealDto trdProductDealDto){
		ResultObject resultObject = new ResultObject();
		
		//1.定义变量
		//变量.入账账套List
		List<BokBookset>  listBokBookset = new ArrayList<BokBookset>();
		//变量.账务明细代码
		String bokDetailIdStr = null;
		//变量.入库账务明细代码List
		List<BokDetailId> listBokDetailId = new ArrayList<BokDetailId>();
		//变量.入库入账信息List
		List<BokDealAccounting> listBokDealAccounting = new ArrayList<BokDealAccounting>();
		//变量.入库入账明细信息List
		List<BokDealAccountingDetail>  listBokDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
		
		
		//2.详情处理
		//2.1 入参检查
		resultObject = howManyCommonService.paramCheck(acctSubjectId, trdProductDealDto);
		if(!ResultCode.SUCCESS.getCode().equals(resultObject.getResultCode())){
			return resultObject;
		}
		//2.2 交易入账账套信息查询
		listBokBookset = howManyCommonMapper.queryBokBooksetByBusiId(acctSubjectId); 
		if (!CollectionUtils.isNotEmpty(listBokBookset)) {
			resultObject.setResultCode(ResultCode.FAILURE.getCode());
			resultObject.setResultMsg("参数.核算主体【"+acctSubjectId+"】无对应核算账套");
			return resultObject;
		}
		//2.3 交易入账
		bokDetailIdStr = dataTransplantCommonService.getBookDetailId(trdProductDealDto.getFinprodType(), 
				trdProductDealDto.getFinprodId(), null, null,trdProductDealDto.getCcy());
		for(BokBookset bok :listBokBookset){
			//2.3.1 账务明细代码表(BOK_DETAIL_ID)
			BokDetailId bokDetailId = new BokDetailId();
			bokDetailId.setBooksetId(bok.getBooksetId());
			bokDetailId.setBokDetailId(bokDetailIdStr);
			bokDetailId.setBusiId(trdProductDealDto.getFinprodId());
			bokDetailId.setBusiDetailType(trdProductDealDto.getFinprodType());
			bokDetailId.setTradeId(trdProductDealDto.getTradeId());
			bokDetailId.setDetailSubjectNoTail(trdProductDealDto.getDetailSubjectNoTail());
			bokDetailId.setDetailSubnameTail(trdProductDealDto.getDetailSubnameTail());
			bokDetailId.setBranch(InterfCommonConstant.分支序号_默认);
			listBokDetailId.add(bokDetailId);
			
			//2.3.2 入账信息表(BOK_DEAL_ACCOUNTING)
			BokDealAccounting bokDealAccounting = new BokDealAccounting();
			bokDealAccounting.setBooksetId(bok.getBooksetId());
			bokDealAccounting.setBusiNo(trdProductDealDto.getTradeId());
			bokDealAccounting.setBusiTable(trdProductDealDto.getBusiTable());
			bokDealAccounting.setBooksetDate(trdProductDealDto.getTradeDate());
			bokDealAccounting.setBookDate(trdProductDealDto.getTradeDate());
			bokDealAccounting.setHappenDate(trdProductDealDto.getTradeDate());
			bokDealAccounting.setIsAccounted(DictCodeList.是否_否.getValue());
			listBokDealAccounting.add(bokDealAccounting);
			
			//2.3.3 入账账务明细表(BOK_DEAL_ACCOUNTING_DETAIL)
			BokDealAccountingDetail bokDealAccountingDetail = new BokDealAccountingDetail();
			bokDealAccountingDetail.setBooksetId(bok.getBooksetId());
			bokDealAccountingDetail.setBusiNo(trdProductDealDto.getTradeId());
			bokDealAccountingDetail.setBusiTable(trdProductDealDto.getBusiTable());
			bokDealAccountingDetail.setBusiType(trdProductDealDto.getBusiType());
			bokDealAccountingDetail.setBokDetailId(bokDetailIdStr);
			listBokDealAccountingDetail.add(bokDealAccountingDetail);
		}
		//2.4 保存交易入账信息
		resultObject = howManyCommonService.insertDealAccount(listBokDealAccounting, 
				listBokDealAccountingDetail, listBokDetailId);
		
		//3.返回处理结果
		return resultObject;
	}
	
	
	/**
	 * 几许交易入账(撤销) 
	 * @param tradeId 交易编号
	 * @return
	 */
	public ResultObject revokeDealAccount(String tradeId){
		ResultObject resultObject = new ResultObject();
		resultObject.setResultCode(ResultCode.SUCCESS.getCode());
		resultObject.setResultMsg("执行成功");
		
		//删除入账信息表
		howManyCommonMapper.delBokDealAccountingByTradeId(tradeId);
		
		//删除入账明细信息表
		howManyCommonMapper.delBokDealAccountingDetailByTradeId(tradeId);
		
		return resultObject;
	}
	
	/**
	 * 几许生成账务明细当前表
	 * @param bokBookdetailCurrentDto 账务明细当前表Dto
	 * @return
	 */
	public ResultObject generateBokBookdetailCurrent(BokBookdetailCurrentDto bokBookdetailCurrentDto){
		ResultObject resultObject = new ResultObject();
		resultObject.setResultCode(ResultCode.SUCCESS.getCode());
		resultObject.setResultMsg("执行成功");
		//1.定义变量
		//变量.账务明细代码
//		String bokDetailIdStr = null;
		//变量.账务明细代码表
		BokDetailId bokDetailId = null;
		//变量.账套日期表
		BokBooksetDate bokBooksetDate = null;
		//变量.账套科目
		BokSubject bokSubject = null;
		//变量.批处理用账务明细
		BokBookdetailCurrent bokBookdetailCurrent = new BokBookdetailCurrent();
		//变量.业务明细科目
		BokBussDetsub bokBussDetsub = null;
		//变量.四级科目号
		String detailSubjectNo = null;
		//变量.四级科目名称
		String detailSubjectName = null;
		//变量.四级科目尾号
		String detailSubjectNoTail = null;
		//变量.四级科目尾号名称
		String detailSubnameTail = null;
		//变量.凭证编号
		String bookId = null;
		
		//2.详情处理
//		bokDetailIdStr = dataTransplantCommonService.getBookDetailId(bokBookdetailCurrentDto.getBusiDetailType(), 
//				bokBookdetailCurrentDto.getBusiId(), null, null,bokBookdetailCurrentDto.getoCcy());
		//2.1 查询账务代码表信息
		BokDetailIdKey bokDetailIdKey = new BokDetailIdKey();
		bokDetailIdKey.setBooksetId(bokBookdetailCurrentDto.getBooksetId());
		bokDetailIdKey.setBokDetailId(bokBookdetailCurrentDto.getBokDetailId());
		bokDetailId = bokDetailIdMapper.selectByPrimaryKey(bokDetailIdKey);
		if(bokDetailId==null){
			resultObject.setResultCode(ResultCode.FAILURE.getCode());
			resultObject.setResultMsg("业务明细代码【"+bokBookdetailCurrentDto.getBusiId()+"】、"
					+ "业务明细类型【"+bokBookdetailCurrentDto.getBusiDetailType()+"】不存在账务明细代码信息;");
			return resultObject;
		}
		
		//2.2 查询账套当前日期信息
		bokBooksetDate = howManyCommonMapper.queryBokBooksetDateByBooksetId(bokBookdetailCurrentDto.getBooksetId());
		if(bokBooksetDate==null){
			resultObject.setResultCode(ResultCode.FAILURE.getCode());
			resultObject.setResultMsg("账套【"+bokBookdetailCurrentDto.getBooksetId()+"】信息不存在;");
			return resultObject;
		}
		
		//2.3 查询账套科目表信息
		bokSubject = howManyCommonMapper.querybokSubjectByKey(bokBookdetailCurrentDto.getBooksetId(), 
				bokBookdetailCurrentDto.getSubjectNo());
		
		if(bokSubject==null){
			resultObject.setResultCode(ResultCode.FAILURE.getCode());
			resultObject.setResultMsg("账套【"+bokBookdetailCurrentDto.getBooksetId()+"】不存在科目【"+bokBookdetailCurrentDto.getSubjectNo()+"】;");
			return resultObject;
		}
		
		//2.4 四级科目尾号生成
		BokBussDetsub bokBussDetsubDTO = howManyCommonService.getFourLevelSubject(bokSubject, bokDetailId);
		if(bokBussDetsubDTO != null){
			detailSubjectNo = bokBussDetsubDTO.getDetailSubjectNo();
			detailSubjectName = bokBussDetsubDTO.getDetailSubjectName();
			detailSubjectNoTail = bokBussDetsubDTO.getDetailSubjectNoTail();
		    detailSubnameTail = bokBussDetsubDTO.getDetailSubnameTail();
		}
		
		//2.5 记账凭证编号生成
		BokBookdetailCurrent bookDetail = new BokBookdetailCurrent();
		bookDetail.setHappenDate(bokBookdetailCurrentDto.getHappenDate());
		bookDetail.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
		bookDetail.setTableId(bokBookdetailCurrentDto.getTableId());
		bookDetail.setTradeId(bokBookdetailCurrentDto.getTradeId());
		bookDetail.setBokDetailId(bokBookdetailCurrentDto.getBokDetailId());
		bookDetail.setBooksetId(bokBookdetailCurrentDto.getBooksetId());		
		bookId = bookAccountCommonService.genBookId(bookDetail);
		
		//2.6 批处理用账务明细初始化
		bokBookdetailCurrent.setSeqno(dBUtil.getSequenceIndexData(DataIndexSequenceType.记账类型));
		bokBookdetailCurrent.setBooksetId(bokBookdetailCurrentDto.getBooksetId());
		bokBookdetailCurrent.setTableId(bokBookdetailCurrentDto.getTableId());
		bokBookdetailCurrent.setTradeId(bokBookdetailCurrentDto.getTradeId());
		bokBookdetailCurrent.setBusiId(bokBookdetailCurrentDto.getBusiId());
		bokBookdetailCurrent.setTaskId(bokBookdetailCurrentDto.getTaskId());
		bokBookdetailCurrent.setTaskPri(bokBookdetailCurrentDto.getTaskPri());
		bokBookdetailCurrent.setHappenDate(bokBookdetailCurrentDto.getHappenDate());
		bokBookdetailCurrent.setBookDate(bokBookdetailCurrentDto.getBookDate());
		bokBookdetailCurrent.setBooksetDate(bokBooksetDate.getBooksetDate());
		bokBookdetailCurrent.setBaseSubjectNature(bokSubject.getBaseSubjectNature());
		bokBookdetailCurrent.setBokDetailId(bokBookdetailCurrentDto.getBokDetailId());
		bokBookdetailCurrent.setSubjectNo(bokBookdetailCurrentDto.getSubjectNo());
		bokBookdetailCurrent.setDetailSubjectNo(detailSubjectNo);
		bokBookdetailCurrent.setCdFlag(bokBookdetailCurrentDto.getCdFlag());
		bokBookdetailCurrent.setoCcy(bokBookdetailCurrentDto.getoCcy());
		bokBookdetailCurrent.setoHappenAmt(bokBookdetailCurrentDto.getoHappenAmt());
		bokBookdetailCurrent.setbCcy(bokBookdetailCurrentDto.getbCcy());
		bokBookdetailCurrent.setbHappenAmt(bokBookdetailCurrentDto.getbHappenAmt());
		bokBookdetailCurrent.setBranch(InterfCommonConstant.分支序号_默认);
		
		BigDecimal happenNumber = BigDecimal.ZERO;
		if(DictCodeList.是否_是.getValue().equals(bokSubject.getAccQuaFlag())){
			happenNumber = bokBookdetailCurrentDto.getHappenNumber();
		}
		bokBookdetailCurrent.setHappenNumber(happenNumber);
		
		bokBookdetailCurrent.setBookId(bookId);
		bokBookdetailCurrent.setBookRemark(bokBookdetailCurrentDto.getBookRemark());
		bokBookdetailCurrent.setOffsetFlag(DictCodeList.冲正标识_正常交易.getValue());
		bokBookdetailCurrent.setBookType(DictCodeList.凭证类型_日终.getValue());
		bokBookdetailCurrent.setBookOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.处理序列)));
		bokBookdetailCurrent.setExDate(bokBookdetailCurrentDto.getExDate());
		bokBookdetailCurrent.setExRate(bokBookdetailCurrentDto.getExRate());
		
		//2.7 业务明细科目初始化
		if(!StringUtil.isEmpty(detailSubjectNoTail)){
			bokBussDetsub = new BokBussDetsub();
			bokBussDetsub.setBooksetId(bokBookdetailCurrentDto.getBooksetId());
			bokBussDetsub.setDetailSubjectNo(detailSubjectNo);
			bokBussDetsub.setDetailSubjectName(detailSubjectName);
			bokBussDetsub.setFsubjectId(bokBookdetailCurrentDto.getSubjectNo());
			bokBussDetsub.setDetailSubjectNoTail(detailSubjectNoTail);
			bokBussDetsub.setDetailSubnameTail(detailSubnameTail);
		}
		
		//3.账务明细当前表、业务明细科目表数据入库
		resultObject = howManyCommonService.insertKeepAccount(bokBookdetailCurrent, bokBussDetsub);
		
		//4.返回处理结果
		return resultObject;
	}
}
