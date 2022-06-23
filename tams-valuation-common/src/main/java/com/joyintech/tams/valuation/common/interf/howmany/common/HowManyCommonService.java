package com.joyintech.tams.valuation.common.interf.howmany.common;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.common.ResultCode;
import com.joyintech.tams.valuation.common.interf.common.ResultObject;
import com.joyintech.tams.valuation.common.interf.howmany.dto.TrdProductDealDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 接口共通
 * 
 * @author wujie
 * @version 1.0 2019年5月15日
 * @see HowManyCommonService
 * @since 1.0
 */
@Service
public class HowManyCommonService {
	
	@Autowired
	private BokDealAccountingMapper bokDealAccountingMapper;
	 
	@Autowired
	private BokDealAccountingDetailMapper bokDealAccountingDetailMapper;
	
	@Autowired
	private BokDetailIdMapper bokDetailIdMapper;
	
	@Autowired
	private BokBookdetailCurrentMapper bokBookdetailCurrentMapper;
	
	@Autowired
	private BokBussDetsubMapper bokBussDetsubMapper;

	/**
	 * 几许交易入账(初始)_入参检查
	 * @param acctSubjectId 核算主体代码
	 * @param trdProductDealDto 金融产品交易
	 * @return
	 */
	public ResultObject paramCheck(String acctSubjectId, TrdProductDealDto trdProductDealDto){
		ResultObject resultObject = new ResultObject();
		resultObject.setResultCode(ResultCode.SUCCESS.getCode());
		resultObject.setResultMsg("执行成功");
		
		StringBuffer resultMsg = new StringBuffer();
		//核算主体代码
		if(StringUtil.isEmpty(acctSubjectId)){
			resultMsg.append("参数.核算主体代码：字段值为空;");
		}
		//金融产品交易
		if(trdProductDealDto==null){
			resultMsg.append("参数.金融产品交易：对象为空;");
			resultObject.setResultCode(ResultCode.FAILURE.getCode());
			resultObject.setResultMsg(resultMsg.toString());
			return resultObject;
		}
		//参数.金融产品交易.交易编号
		if(StringUtil.isEmpty(trdProductDealDto.getTradeId())){
			resultMsg.append("参数.金融产品交易.交易编号：字段值为空;");
		}
		//参数.金融产品交易.金融产品代码
		if(StringUtil.isEmpty(trdProductDealDto.getFinprodId())){
			resultMsg.append("参数.金融产品交易.金融产品代码：字段值为空;");
		}
		//参数.金融产品交易.金融产品类型
		if(StringUtil.isEmpty(trdProductDealDto.getFinprodType())){
			resultMsg.append("参数.金融产品交易.金融产品类型：字段值为空;");
		}
		else{
			if(DictCodeListExt1.from(DictCode.金融产品类型, trdProductDealDto.getFinprodType()) == null) {
				resultMsg.append("参数.金融产品交易.金融产品类型：字段值不在有效范围内;");
			}
		}
		//参数.金融产品交易.交易日
		if(trdProductDealDto.getTradeDate()==null){
			resultMsg.append("参数.金融产品交易.交易日：字段值为空;");
		}
		//参数.金融产品交易.业务类型
		if(StringUtil.isEmpty(trdProductDealDto.getBusiType())){
			resultMsg.append("参数.金融产品交易.业务类型：字段值为空;");
		}
		else{
			if(DictCodeList.from(DictCode.业务类型, trdProductDealDto.getBusiType()) == null) {
				resultMsg.append("参数.金融产品交易.业务类型：字段值不在有效范围内;");
			}
		}
		//参数.金融产品交易.业务表
		if(StringUtil.isEmpty(trdProductDealDto.getBusiTable())){
			resultMsg.append("参数.金融产品交易.业务表：字段值为空;");
		}else{
			if(DictCodeList.from(DictCode.业务表, trdProductDealDto.getBusiTable()) == null) {
				resultMsg.append("参数.金融产品交易.业务表：字段值不在有效范围内;");
			}
		}
		
		if(!StringUtil.isEmpty(resultMsg.toString())){
			resultObject.setResultCode(ResultCode.FAILURE.getCode());
			resultObject.setResultMsg(resultMsg.toString());
		}
		return resultObject;
	}
	
	
	/**
	 * 几许交易入账(初始)_入账信息入库
	 * @param listBokDealAccounting 入账信息集合
	 * @param listBokDealAccountingDetail 入账明细信息集合
	 * @param listBokDetailId 账务明细代码集合
	 * @return
	 */
	public ResultObject insertDealAccount(List<BokDealAccounting> listBokDealAccounting,
			List<BokDealAccountingDetail> listBokDealAccountingDetail,List<BokDetailId> listBokDetailId){
		
		ResultObject resultObject = new ResultObject();
		//入账信息入库
		if (CollectionUtils.isNotEmpty(listBokDealAccounting)) {
			for(BokDealAccounting record :listBokDealAccounting) {
				bokDealAccountingMapper.insert(record);
			}
		}
		//入账明细信息入库
		if (CollectionUtils.isNotEmpty(listBokDealAccountingDetail)) {
			for(BokDealAccountingDetail record :listBokDealAccountingDetail){
				bokDealAccountingDetailMapper.insert(record);
			}
		}
		//账务明细代码信息入库
		if (CollectionUtils.isNotEmpty(listBokDetailId)) {
			for(BokDetailId record :listBokDetailId){
				bokDetailIdMapper.deleteByPrimaryKey(record);
				bokDetailIdMapper.insert(record);
			}
		}
		resultObject.setResultCode(ResultCode.SUCCESS.getCode());
		resultObject.setResultMsg("执行成功");
		
		return resultObject;
	}
	
	/**
	 * 四级科目号生成共通
	 * @param bokSubject
	 * @param bokDetailId
	 * @return
	 */
	public BokBussDetsub getFourLevelSubject(BokSubject bokSubject,BokDetailId bokDetailId){
		BokBussDetsub bokBussDetsub = null;
		if(DictCodeList.是否_是.getValue().equals(bokSubject.getGenDetsubFlag())){
			bokBussDetsub = new BokBussDetsub();
			//变量.业务明细科目.四级科目号 =	输入.账套科目.科目号 + 	输入.账务明细代码表.四级科目尾号
			bokBussDetsub.setDetailSubjectNo(bokSubject.getSubjectNo()+(bokDetailId.getDetailSubjectNoTail()==null?"":bokDetailId.getDetailSubjectNoTail()));
			/** modify by yanya on 20210108 四级科目号修正(科目名称 + 四级科目尾号名称) start**/
			//变量.业务明细科目.四级科目名称 = 输入.账套科目.科目名称 + 输入.账务明细代码表.四级科目尾号名称
			//bokBussDetsub.setDetailSubjectName(bokSubject.getSubjectName());
			bokBussDetsub.setDetailSubjectName(bokSubject.getSubjectName() +
					(StringUtil.isNotEmpty(bokDetailId.getDetailSubnameTail()) ? bokDetailId.getDetailSubnameTail() : ""));
			/** modify by yanya on 20210108 四级科目号修正(科目名称 + 四级科目尾号名称) end**/
			//变量.业务明细科目.四级科目尾号 =	输入.账务明细代码表.四级科目尾号	
			bokBussDetsub.setDetailSubjectNoTail(bokDetailId.getDetailSubjectNoTail());
			//变量.业务明细科目.四级科目尾号名称 =	输入.账务明细代码表.四级科目尾号名称
			bokBussDetsub.setDetailSubnameTail(bokDetailId.getDetailSubnameTail());
		}
		return bokBussDetsub;
	}
	
	/**
	 * 插入账务明细当前表、业务明细科目表
	 * @param bokBookdetailCurrent 账务明细当前表
	 * @param bokBussDetsub 业务明细科目表
	 * @return
	 */
	public ResultObject insertKeepAccount(BokBookdetailCurrent bokBookdetailCurrent,BokBussDetsub bokBussDetsub){
		ResultObject resultObject = new ResultObject();
		
		bokBookdetailCurrentMapper.insert(bokBookdetailCurrent);
		if(bokBussDetsub != null){
			BokBussDetsub rdto = bokBussDetsubMapper.selectByPrimaryKey(bokBussDetsub);
			if(rdto == null){
				bokBussDetsubMapper.insert(bokBussDetsub);
			}
		}
		
		resultObject.setResultCode(ResultCode.SUCCESS.getCode());
		resultObject.setResultMsg("生成账务明细当前表成功");
		return resultObject;
	}
}
