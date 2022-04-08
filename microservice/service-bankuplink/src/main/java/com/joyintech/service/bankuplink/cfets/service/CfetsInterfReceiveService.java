/*
 * 文件名：CfetsInterfReceiveService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2020年12月23日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.cfets.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.joyintech.background.component.common.pack.common.util.BeanUtil;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.repodlgservice.RepoDialogueQuoteSubService;
import com.joyintech.background.component.uplink.interbank.business.service.impl.IbuMainServiceImpl;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteBondDetMapper;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteBondMapper;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteCrepMapper;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteMapper;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBond;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteCrep;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.cfets.constant.CfetsConstant;
import com.joyintech.service.bankuplink.cfets.constant.MsgTypeEnum;
import com.joyintech.service.bankuplink.cfets.dao.CfetsInterfMapper;
import com.joyintech.service.bankuplink.cfets.dto.cashtransaction.dialogquotation.NoStipulationDto;
import com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoCrossCustodianDto;
import com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoPartyIDDto;
import com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoPartySubIDDto;
import com.joyintech.service.bankuplink.cfets.dto.collateralisedrepo.dialogquotation.NoUnderlyingDto;
import com.joyintech.service.bankuplink.cfets.util.CfetsDataConvertUtil;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentCommonService;
import com.joyintech.service.bankuplink.repo.dao.CfetsRepoDealQuotedPrice001Mapper;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.common.base.entity.CfetsDatadictConvert;
import com.joyintech.tams.common.base.entity.CfetsStatusDatadictConvert;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.IbuQuoteEntCrepPledgeCommonDto;
import com.joyintech.tams.invest.common.service.DateCalCommonService;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

/**
 * 接收Cfets接口机消息处理Service
 * @author etliu
 * @version 1.0 2020年12月23日
 * @see CfetsInterfReceiveService
 * @since 1.0
 */
@Service
public class CfetsInterfReceiveService {
	
	/**
     * CfetsInterfMapper
     */
    @Autowired
    private CfetsInterfMapper cfetsInterfMapper;
    
    @Autowired
    private IbuQuoteMapper ibuQuoteMapper;
    
    @Autowired
    private IbuQuoteBondMapper ibuQuoteBondMapper;
    
    @Autowired
    private IbuQuoteBondDetMapper ibuQuoteBondDetMapper;
    
    /**
     * IbuQuoteCrepMapper
     */
    @Autowired
    private IbuQuoteCrepMapper ibuQuoteCrepMapper;
    
//    /**
//     * {@linkplain}
//     */
//    @Autowired
//    private CfetsQuotedCommonService cfetsQuotedCommonService;
    
    @Autowired
    private DateCalCommonService dateCalCommonService;


    @Autowired
    private IbuMainServiceImpl ibuMainServiceImpl;

    @Autowired
    private IbuComponentCommonService ibuComponentCommonService;
    
    // 异步方法检查数据库中外部报价编号的最大等待时间（毫秒）
    @Value("${application.quote.resp.maxWaitTime:3000}")
    private int maxWaitTime;

    @Autowired
    private CfetsRepoDealQuotedPrice001Mapper cfetsRepoDealQuotedPrice001Mapper;

    @Autowired
    private CstpCommonService cstpCommonService;

    @Autowired
    private RepoDialogueQuoteSubService repoDialogueQuoteSubService;
	/**
     * Cfets接口数据转IbuQuoteBondDto
     * 现券对话报价主表
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
	public IbuQuoteBondDto getIbuQuoteBondDto(Map<String, Object> param) {
	    this.waitForSomeTime();

		IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
		
		if(null == param.get("msgType")) {
			return null;
		}
		String msgType = (String)param.get("msgType");
		
		if(MsgTypeEnum.j.getValue().equals(msgType) && null!=param.get("clOrdID")) {// 35域-报文类型  // 11域-客户参考编号
			IbuQuoteBondDto ibuQuoteBondDto2 = ibuQuoteMapper.selectQuoteBondByKey((String)param.get("clOrdID"));
	    	BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, ibuQuoteBondDto2);
	    	ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion()+1);
	    	ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());// j报文发送状态设为失败
	    	// 根据原操作类型，获取失败报文对应的操作类型
	    	ibuQuoteBondDto.setExeType(CfetsDataConvertUtil.genExeTypeForFailMsg(ibuQuoteBondDto.getExeType()));
	    	ibuQuoteBondMapper.updateByPrimaryKeySelective(ibuQuoteBondDto);
            //登录错误日志
            this.saveIbuQuoteErrlog(ibuQuoteBondDto2.getQuoteId(), param);
	    	return null;
		}
    	if(null!=param.get("securityID")) {// 48域-债券代码
    		String securityID = (String) param.get("securityID");
    		ibuQuoteBondDto.setFinprodMarketId(securityID+".IB");//市场代码
    	}
    	if(null!=param.get("clOrdID")) {// 11域-客户参考编号
    		ibuQuoteBondDto.setQuoteId((String) param.get("clOrdID"));//报价编号（内部代码）
    	}
        if(null!=param.get("quoteID")) {// 117域-报价编号
            ibuQuoteBondDto.setCfetsQuoteId((String) param.get("quoteID"));//CFETS报价编号
        }
        if(null!=param.get("clOrdID")) {// 11域-客户参考编号
            ibuQuoteBondDto.setpQuoteId((String) param.get("clOrdID"));//外部报价编号
        }
    	
        IbuQuoteBond ibuQuoteBondDto2 = null;
        // 先用CFETS报价编号查询db中已存在报价信息
        if (StringUtil.isNotBlank(ibuQuoteBondDto.getCfetsQuoteId())) {
            ibuQuoteBondDto2 = cfetsInterfMapper.selectIbuQuoteBondDtoDataByCfetsQuoteId(ibuQuoteBondDto.getCfetsQuoteId());
        }
        // 如果查询不到，再用客户参考编号查询db中已存在报价信息 
        if (ibuQuoteBondDto2 == null && StringUtil.isNotBlank(ibuQuoteBondDto.getQuoteId())) {
            ibuQuoteBondDto2 = ibuQuoteBondMapper.selectByPrimaryKey(ibuQuoteBondDto.getQuoteId());
        }

        if (ibuQuoteBondDto2 != null) {
//            ibuQuoteBondDto.setQuoteId(ibuQuoteBondDto2.getQuoteId());
//            ibuQuoteBondDto.setpQuoteId(ibuQuoteBondDto2.getpQuoteId());//外部报价编号
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, ibuQuoteBondDto2);
        }
    	
    	ibuQuoteBondDto.setQuoteType((String) param.get("quoteType"));//报价类型
    	String ibuQuoteBondDtoExeType = this.getIbuQuoteBondDtoExeType(ibuQuoteBondDto, param);
    	if(StringUtil.isNotEmpty(ibuQuoteBondDtoExeType) && ibuQuoteBondDtoExeType.split("_").length==4) {
    		ibuQuoteBondDto.setQuoteRoleType(ibuQuoteBondDtoExeType.split("_")[3]);//本方角色
    		ibuQuoteBondDto.setExeType(ibuQuoteBondDtoExeType.split("_")[0]);//操作类型
    	}

    	ibuQuoteBondDto.setBranch(0l);
    	if(null!=param.get("settlDate")) {// 64域-结算日
    	    ibuQuoteBondDto.setSettleDate(DateUtil.parse((String)param.get("settlDate")));// 交割日期
    	}
    	if(null!=param.get("settlType")) {// 63域-清算速度
    		String settlType = this.convertCfetsCodeToSystemCode("settlType", (String)param.get("settlType"));
    		ibuQuoteBondDto.setSettleSpeed(Long.parseLong(settlType));//清算速度
    	}
    	if(null!=param.get("deliveryType")) {// 919域-结算方式
    		String deliveryType = this.convertCfetsCodeToSystemCode("deliveryType", (String)param.get("deliveryType"));
    		ibuQuoteBondDto.setDeliveryType(deliveryType);// 结算方式
    	}
    	if(null!=param.get("clearingMethod")) {// 11143域-清算类型
    		String clearingMethod = this.convertCfetsCodeToSystemCode("clearingMethod", (String)param.get("clearingMethod"));
    		ibuQuoteBondDto.setSettType(clearingMethod);// 清算类型
    	}
    	if(null!=param.get("settlCurrency")) {// 120域-结算币种
    		ibuQuoteBondDto.setSettCcy((String)param.get("settlCurrency"));// 结算币种
    	}
    	if((ibuQuoteBondDto2 == null || StringUtil.isBlank(ibuQuoteBondDto2.getPs())) && null != param.get("side")) {// 54域-交易方向
    		String side = (String)param.get("side");
    		if("1".equals(side)) {
    			side = BussinessDictCodeList.交易方向_买入.getValue();
    		}else {
    			side = BussinessDictCodeList.交易方向_卖出.getValue();
    		}
    		ibuQuoteBondDto.setPs(CfetsDataConvertUtil.convertBondPs(side));// 交易方向
    	}
    	if(null!=param.get("orderQty")) {// 38域-券面总额
    		ibuQuoteBondDto.setShareAmt(ArithmeticUtil.divide(new BigDecimal((String) param.get("orderQty")), BigDecimal.valueOf(100), 4));// 交易份额，债券数量等
    	}
    	if(null!=param.get("settlCurrAmt")) {// 119域-结算金额
    		ibuQuoteBondDto.setFpriceAmt(new BigDecimal((String) param.get("settlCurrAmt")));// 全价总额
    	}
    	if(StringUtil.isNotEmpty(ibuQuoteBondDtoExeType) && ibuQuoteBondDtoExeType.split("_").length==4) {
    		ibuQuoteBondDto.setTradeStatus(ibuQuoteBondDtoExeType.split("_")[1]);//交易状态
    		ibuQuoteBondDto.setSendStatus(ibuQuoteBondDtoExeType.split("_")[2]);//发送状态
    	}
//    	if(null!=param.get("validUntilTime")) {// 62域-报价有效时间
//    		ibuQuoteBondDto.setEffTime(DateUtil.parse((String)param.get("validUntilTime")));//有效时间
//    	}
    	
        // 60域-业务发生时间
        Date transactTime = null;
        if (null != param.get("transactTime")) {
            try {
                transactTime = DateUtil.parseDate((String)param.get("transactTime"),
                    CfetsConstant.DATETIME_FORMAT);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        // 报价日期
        if (null != transactTime) {
            ibuQuoteBondDto.setQuoteDate(transactTime);// 60域-业务发生时间
            ibuQuoteBondDto.setTransactime((String)param.get("transactTime")); // 业务发生时间
        }
        //所有报文回来 证券账户和创建人保留历史数据
        if(StringUtil.isNotEmpty(ibuQuoteBondDto.getQuoteId())){
            IbuQuoteBondDto ibuQuoteBondDtoCfetByQuoteId = ibuQuoteMapper.selectQuoteBondByKey(ibuQuoteBondDto.getQuoteId());
            if(null!=ibuQuoteBondDtoCfetByQuoteId){
                ibuQuoteBondDto.setSecManageAcctId(ibuQuoteBondDtoCfetByQuoteId.getSecManageAcctId());
                ibuQuoteBondDto.setCreateUser(ibuQuoteBondDtoCfetByQuoteId.getCreateUser());
                ibuQuoteBondDto.setDipType(ibuQuoteBondDtoCfetByQuoteId.getDipType());
            }
        }
//    	IbuQuoteBondDto ibuQuoteBondDto2 = ibuQuoteMapper.selectQuoteBondByKey(ibuQuoteBondDto.getQuoteId());
    	if(ibuQuoteBondDto2!=null&&null!=ibuQuoteBondDto2.getVersion()) {
    		ibuQuoteBondDto.setVersion(ibuQuoteBondDto2.getVersion()+1);
    	}
        else {
            ibuQuoteBondDto.setVersion(-1l);
        }
    	
    	return ibuQuoteBondDto;
    }
    
	/**
     * Cfets接口数据转IbuQuoteBondDetDto
     * 现券对话报价明细
     * 
     * @param param 报文参数
     * @param ibuQuoteBondDto 对话报价主表信息
     * @return
     * @see
     * @since 1.0
     */
	public IbuQuoteBondDetDto getIbuQuoteBondDetDto(Map<String, Object> param, IbuQuoteBondDto ibuQuoteBondDto) {
		IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
		
		if(null!=param.get("msgType") && null!=param.get("clOrdID")) {// 35域-报文类型  // 11域-客户参考编号
    		if(MsgTypeEnum.j.getValue().equals((String)param.get("msgType"))) {
    			IbuQuoteBondDetDto ibuQuoteBondDetDto2 = ibuQuoteMapper.selectByQuoteId((String)param.get("clOrdID"));
    			BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, ibuQuoteBondDetDto2);
    			ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto.getVersion()+1);
    			ibuQuoteBondDetDto.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());// j报文发送状态设为失败
    			ibuQuoteBondDetMapper.updateByPrimaryKeySelective(ibuQuoteBondDetDto);
    	    	return null;
    		}
    	}

    	if(null!=param.get("quoteReqID")) {// 131域-请求报价编号
    		ibuQuoteBondDetDto.setCfetsReqId((String) param.get("quoteReqID"));//CFETS请求编号
    	}
//    	if(null!=param.get("clOrdID")) {// 11域-客户参考编号
//    		ibuQuoteBondDetDto.setQuoteId((String) param.get("clOrdID"));// 报价编号（内部代码）
//    	}
    	ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto.getQuoteId());// 报价编号（内部代码）
    	
        IbuQuoteBondDetDto dBIbuQuoteBondDetDto = null;
        // 先用CFETS报价编号查询db中已存在报价信息
        if (null != param.get("quoteID")) {
            List<IbuQuoteBondDetDto> tempList = cfetsInterfMapper.selectIbuQuoteBondDetDtoDataByCfetsQuoteId(
                (String)param.get("quoteID"));
            if (!StringUtil.isEmptyList(tempList)) {
                dBIbuQuoteBondDetDto = tempList.get(0);
            }
        }
        // 如果查询不到，再用客户参考编号查询db中已存在报价信息
        if (dBIbuQuoteBondDetDto == null) {
            dBIbuQuoteBondDetDto = ibuQuoteMapper.selectByQuoteId(ibuQuoteBondDetDto.getQuoteId());
        }
        if (dBIbuQuoteBondDetDto != null) {
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, dBIbuQuoteBondDetDto);
        }

    	if(null!=param.get("price")) {// 44域-净价
    		ibuQuoteBondDetDto.setUnitCprice(new BigDecimal((String) param.get("price")));// 单位净价
    	}
    	if(null!=param.get("accruedInterestAmt")) {// 159域-应计利息
    		ibuQuoteBondDetDto.setUnitInt(new BigDecimal((String) param.get("accruedInterestAmt")));// 单位利息 
    	}
    	if(null!=param.get("dirtyPrice")) {// 10048域-全价
    		ibuQuoteBondDetDto.setUnitFprice(new BigDecimal((String) param.get("dirtyPrice")));// 单位全价
    	}
    	if(null!=param.get("principal")) {// 10239域-每百元本金额
    		ibuQuoteBondDetDto.setParValue(new BigDecimal((String) param.get("principal")));//单位面值，债券100或者当前面值(还本前)，其他资产1 
    	}
    	if(null!=param.get("orderQty")) {//38域-券面总额
    		ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.divide(new BigDecimal((String) param.get("orderQty")), BigDecimal.valueOf(100), 4));//  交易份额，债券数量等
    	}
    	if(null!=param.get("totalPrincipal")) {// 10310域-总本金或者当前本金总额
    		ibuQuoteBondDetDto.setPrinAmt(new BigDecimal((String) param.get("totalPrincipal")));//  交易本金，交易成本等
    	}
    	if(null!=param.get("orderQty")) {// 38域-券面总额
    		ibuQuoteBondDetDto.setParValueAmt(new BigDecimal((String) param.get("orderQty")));// 面值总额
    	}
    	if(null!=param.get("price") && null!=param.get("orderQty")) {//44域-净价  38域-券面总额
    		ibuQuoteBondDetDto.setCpriceAmt(new BigDecimal((String) param.get("price")).multiply(new BigDecimal((String) param.get("orderQty")).divide(new BigDecimal(100))));// 净价总额
    	}
    	if(null!=param.get("accruedInterestTotalAmt")) {// 10002域-应计利息总额
    		ibuQuoteBondDetDto.setIntAmt(new BigDecimal((String) param.get("accruedInterestTotalAmt")));//利息总额
    	}
    	if(null!=param.get("settlCurrAmt")) {// 119域-结算金额
    		ibuQuoteBondDetDto.setFpriceAmt(new BigDecimal((String) param.get("settlCurrAmt")));//全价总额
    	}
    	if(null!=param.get("yield")) {//236域-到期收益率值
    		ibuQuoteBondDetDto.setYield(ArithmeticUtil.divide(new BigDecimal((String) param.get("yield")), BigDecimal.valueOf(100), 6));// 到期收益率
    	}
        // 234域-行权收益率值
        BigDecimal exerYield = this.genExerYield(param);
        if (exerYield != null) {
            ibuQuoteBondDetDto.setExerYield(exerYield);
        }
    	this.getIbuQuoteBondDetDtoExeType(ibuQuoteBondDetDto, param);
    	ibuQuoteBondDetDto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());//交易状态
    	ibuQuoteBondDetDto.setSendStatus(ibuQuoteBondDto.getSendStatus());//发送状态

    	if(null!=param.get("execID")) {// 成交编号
    		ibuQuoteBondDetDto.setExecid((String) param.get("execID"));// 17域-成交编号
    	}
    	if(null!=param.get("validUntilTime")) {// 62域-报价有效时间
    		try {
				ibuQuoteBondDetDto.setReEffTime(DateUtil.parseDate((String)param.get("validUntilTime"),CfetsConstant.DATETIME_FORMAT));//回复有效时间
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	
        if (null != param.get("transactTime")) {
            ibuQuoteBondDetDto.setTransactime((String)param.get("transactTime")); // 业务发生时间
        }
    	
    	// IBU_QUOTE_BOND_DET表的计算方式（CALCULATE_FIELD）未登录，应该默认登录净价
    	if (StringUtil.isBlank(ibuQuoteBondDetDto.getCalculateField())) {
    	    ibuQuoteBondDetDto.setCalculateField(BussinessDictCodeList.计算选择方式_净价.getValue());
    	}

    	ibuQuoteBondDetDto.setVersion(ibuQuoteBondDto.getVersion());
    	
    	return ibuQuoteBondDetDto;
    }
    
	/**
     * Cfets接口数据转IbuQuoteCrepDto
     * 回购对话报价
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
	public ReceiveRepoDialogueQuoteDto getIbuQuoteCrepDto(Map<String, Object> param) {
	    this.waitForSomeTime();

	    ReceiveRepoDialogueQuoteDto resultDto = new ReceiveRepoDialogueQuoteDto();
	    IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
	    
        if (null == param.get("msgType")) {
            return null;
        }
        String msgType = (String)param.get("msgType");

        if (MsgTypeEnum.j.getValue().equals(msgType) && null != param.get("clOrdID")) {// 35域-报文类型 // 11域-客户参考编号
            RepoDialogueQuoteDto inputBean = repoDialogueQuoteSubService.getRepoDialogueQuote((String) param.get("clOrdID"));
            IbuQuoteCrep ibuQuoteCrep = inputBean.getIbuQuoteCrepDto();
            ibuQuoteCrep.setVersion(ibuQuoteCrepDto.getVersion() + 1);
            ibuQuoteCrep.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());// j报文发送状态设为失败
            // 根据原操作类型，获取失败报文对应的操作类型
            ibuQuoteCrep.setExeType(CfetsDataConvertUtil.genExeTypeForFailMsg(ibuQuoteCrepDto.getExeType()));
            repoDialogueQuoteSubService.deleteRepoDialogueQuoteByKey((String) param.get("clOrdID"), false);
            repoDialogueQuoteSubService.insertRepoDialogueQuote(inputBean, true);
            BeanUtil.copyPropertiesIgnoreNull(resultDto, inputBean);
            //多组合
            //登录错误日志
            this.saveIbuQuoteErrlog(ibuQuoteCrepDto.getQuoteId(), param);
            return resultDto;
        }
        
        //报文类型为8客户编号取报价编号
        if(MsgTypeEnum.MSGTYPE_8.getValue().equals(msgType) && null!=param.get("quoteID")) {// 117 对应报价编号
            param.put("clOrdID", param.get("quoteID"));
        }
	    
        if (null != param.get("clOrdID")) {
            ibuQuoteCrepDto.setQuoteId((String)param.get("clOrdID"));// 11域-客户参考编号
        }
        ibuQuoteCrepDto.setQuoteType((String)param.get("quoteType"));// 报价类型
        if (null != param.get("clOrdID")) {// 11域-客户参考编号
            ibuQuoteCrepDto.setpQuoteId((String)param.get("clOrdID"));// 外部报价编号
        }
        if (null != param.get("quoteID")) {
            ibuQuoteCrepDto.setCfetsQuoteId((String)param.get("quoteID"));// 117域-报价编号
        }
        
        // 版本号
//        IbuQuoteCrep ibuQuoteCrep = ibuQuoteCrepMapper.selectByPrimaryKey(ibuQuoteCrepDto.getQuoteId());
        IbuQuoteCrep ibuQuoteCrep = null;
        // 先用CFETS报价编号查询db中已存在报价信息
        if (StringUtil.isNotBlank(ibuQuoteCrepDto.getCfetsQuoteId())) {
            ibuQuoteCrep = cfetsInterfMapper.selectIbuQuoteCrepDataByCfetsQuoteId(ibuQuoteCrepDto.getCfetsQuoteId());
        }
        // 如果查询不到，再用客户参考编号查询db中已存在报价信息
        if (ibuQuoteCrep == null && StringUtil.isNotBlank(ibuQuoteCrepDto.getQuoteId())) {
            ibuQuoteCrep = ibuQuoteCrepMapper.selectByPrimaryKey(ibuQuoteCrepDto.getQuoteId());
        }
        if (ibuQuoteCrep != null) {
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrepDto, ibuQuoteCrep);
            ibuQuoteCrepDto.setVersion(ibuQuoteCrep.getVersion() + 1);
        }
        else {
            ibuQuoteCrepDto.setVersion(-1l);
        }
        // 操作类型、本方角色、报价状态、发送状态、本方银行间机构、本方银行间交易员、对手银行间机构、对手方银行间交易员字段信息
        this.genIbuQuoteCrepExeTypeInfo(param, ibuQuoteCrepDto);
        
        // 60域-业务发生时间
        Date transactTime = null;
        if (null != param.get("transactTime")) {
            try {
                transactTime = DateUtil.parseDate((String)param.get("transactTime"),
                    CfetsConstant.DATETIME_FORMAT);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        // 报价日期
        if (null != transactTime) {
            ibuQuoteCrepDto.setQuoteDate(transactTime);// 60域-业务发生时间
            ibuQuoteCrepDto.setTransactime((String)param.get("transactTime"));
        }

        if(null!=param.get("settlType")) {
            String settlType = this.convertCfetsCodeToSystemCode("settlType", String.valueOf(param.get("settlType")));// 63域-清算速度
            ibuQuoteCrepDto.setSettleSpeed(Long.parseLong(settlType));
        }
        if(null!=param.get("deliveryType")) {
            String deliveryType = this.convertCfetsCodeToSystemCode("deliveryType", String.valueOf(param.get("deliveryType")));// 919域-首期结算方式
            ibuQuoteCrepDto.setvDeliveryType(deliveryType);
        }
        if(null!=param.get("deliveryType2")) {
            String deliveryType2 = this.convertCfetsCodeToSystemCode("deliveryType", String.valueOf(param.get("deliveryType2")));// 10045域-到期结算方式
            ibuQuoteCrepDto.setmDeliveryType(deliveryType2); // 10045域-到期结算方式
        }
        if(null!=param.get("clearingMethod")) {
            String clearingMethod = this.convertCfetsCodeToSystemCode("clearingMethod", String.valueOf(param.get("clearingMethod")));// 11143域-清算类型
            ibuQuoteCrepDto.setSettType(clearingMethod);
        }
        if(null!=param.get("settlCurrency")) {
            ibuQuoteCrepDto.setSettCcy(String.valueOf(param.get("settlCurrency"))); // 120域-结算币种
        }
        
        // 首期结算日
        if (null != transactTime) {
            String dealCalId = CacheUtil.getSysParam("ibuVar_calendarId");
            if (StringUtil.isBlank(dealCalId) || StringUtil.equals("ibuVar_calendarId", dealCalId)) {
                dealCalId = "IB";
            }
            ibuQuoteCrepDto.setvSettleDate(
                    dateCalCommonService.getPlusDaysWorkDay(transactTime,
                            ibuQuoteCrepDto.getSettleSpeed().intValue(), dealCalId));
        }
        
        if(null!=param.get("tradeLimitDays")) {
            ibuQuoteCrepDto.setTerm(Long.parseLong(String.valueOf(param.get("tradeLimitDays"))));// 10316域-回购期限
        }
        
        // 到期结算日
        if (ibuQuoteCrepDto.getvSettleDate() != null && ibuQuoteCrepDto.getTerm() != null) {
            String dealCalId = CacheUtil.getSysParam("ibuVar_calendarId");
            if (StringUtil.isBlank(dealCalId) || StringUtil.equals("ibuVar_calendarId", dealCalId)) {
                dealCalId = "IB";
            }
            ibuQuoteCrepDto.setmSettleDate(
                dateCalCommonService.getPlusDaysWorkDay(ibuQuoteCrepDto.getvSettleDate(),
                    ibuQuoteCrepDto.getTerm().intValue(), dealCalId));
        }
    	// 债券币种:默认人民币
    	ibuQuoteCrepDto.setCcy(com.joyintech.tams.constant.DictCodeList.币种_人民币_CNY.getValue());
    	
    	// 交易方向
    	// 如果是首次接收到报文，db里还没有报价信息，则转换交易方向，如果db里已经有报价信息，则直接设置交易方向为空，以db里为准
    	if (ibuQuoteCrep == null) {
    	    if (null != param.get("side")) {
                String side = String.valueOf(param.get("side"));// 54域-交易方向
                if ("1".equals(side)) {
                    side = BussinessDictCodeList.回购拆借方向_逆回购.getValue();
                }
                else {
                    side = BussinessDictCodeList.回购拆借方向_正回购.getValue();
                }
                ibuQuoteCrepDto.setRepoPs(side);
            }
            ibuQuoteCrepDto.setRepoPs(CfetsDataConvertUtil.convertRepoPs(ibuQuoteCrepDto.getRepoPs()));
        }
        else {
            ibuQuoteCrepDto.setRepoPs(ibuQuoteCrep.getRepoPs());
        }
        if (null != param.get("price")) {
            ibuQuoteCrepDto.setRate(ArithmeticUtil.divide(new BigDecimal(String.valueOf(param.get("price"))), BigDecimal.valueOf(100), 6));// 44域-回购利率
        }

    	if(null!=param.get("tradeCashAmt")) {
    		ibuQuoteCrepDto.setFpriceAmt(new BigDecimal(String.valueOf(param.get("tradeCashAmt"))));// 10312域-交易金额
    	}
    	
    	if (ibuQuoteCrepDto.getFpriceAmt() != null && ibuQuoteCrepDto.getRate() != null
    	    && ibuQuoteCrepDto.getvSettleDate() != null && ibuQuoteCrepDto.getmSettleDate() != null) {
    	    long 实际占款天数 = DateUtil.getBetweenDayNumber(ibuQuoteCrepDto.getvSettleDate(), ibuQuoteCrepDto.getmSettleDate());
    	    // 到期利息
    	    ibuQuoteCrepDto.setmIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(ibuQuoteCrepDto.getFpriceAmt(), ibuQuoteCrepDto.getRate(), BigDecimal.valueOf(实际占款天数)), BigDecimal.valueOf(365), 2));
    	    // 到期金额
    	    ibuQuoteCrepDto.setmTradeAmt(ArithmeticUtil.doAdd(ibuQuoteCrepDto.getFpriceAmt(), ibuQuoteCrepDto.getmIntAmt()));
    	}
    	// 计息基础:默认 A/365
    	ibuQuoteCrepDto.setBasis(com.joyintech.tams.constant.DictCodeList.计息基础_A_365.getValue());
    	//  期限品种
    	if (ibuQuoteCrepDto.getTerm() != null) {
    	    ibuQuoteCrepDto.setTermType(DateCalCommonService.genTermTypeByTerm(ibuQuoteCrepDto.getTerm()));
    	}

        List<NoCrossCustodianDto> noCrossCustodians = JSONObject.parseObject(
            JSON.toJSONString(param.get("noCrossCustodians")),
            new TypeReference<List<NoCrossCustodianDto>>() {
            });
    	if (!StringUtil.isEmptyList(noCrossCustodians)) {
    	    BigDecimal cbFpriceAmt = new BigDecimal(0);
    	    BigDecimal scFpriceAmt = new BigDecimal(0);
    	    for (NoCrossCustodianDto noCrossCustodianDto : noCrossCustodians) {
    	        // 中债全价总额
                if ("3".equals(noCrossCustodianDto.getCrossCustodianInstitutionName())
                    && null != noCrossCustodianDto.getCrossCustodianTradeAmt()) {
                    cbFpriceAmt = ArithmeticUtil.doAdd(cbFpriceAmt,new BigDecimal(noCrossCustodianDto.getCrossCustodianTradeAmt()));
                    /*ibuQuoteCrepDto.setCbFpriceAmt(
                        new BigDecimal(noCrossCustodianDto.getCrossCustodianTradeAmt()));*/// 12050域-跨托管交易金额
                }
                // 上清全价总额
                if ("4".equals(noCrossCustodianDto.getCrossCustodianInstitutionName())
                    && null != noCrossCustodianDto.getCrossCustodianTradeAmt()) {
                    scFpriceAmt = ArithmeticUtil.doAdd(scFpriceAmt,new BigDecimal(noCrossCustodianDto.getCrossCustodianTradeAmt()));
                    /*ibuQuoteCrepDto.setScFpriceAmt(
                        new BigDecimal(noCrossCustodianDto.getCrossCustodianTradeAmt()));*/// 12050域-跨托管交易金额
                }
    	    }
            ibuQuoteCrepDto.setCbFpriceAmt(cbFpriceAmt);
            ibuQuoteCrepDto.setScFpriceAmt(scFpriceAmt);
    	}

        if (null != param.get("execID")) {
            ibuQuoteCrepDto.setExecid(String.valueOf(param.get("execID")));// 17域-成交编号
        }

    	// 质押品信息
    	List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = new ArrayList<>();
    	
    	BigDecimal shareAmt = null; // 交易份额
    	BigDecimal shareCtAmt = null; // 折算券面金额
    	BigDecimal cbShareCtAmt = null; // 中债折算券面金额
    	BigDecimal scShareCtAmt = null; // 上清折算券面金额
    	
        // 711域-质押券
        List<NoUnderlyingDto> noUnderlyings = JSONObject.parseObject(
            JSON.toJSONString(param.get("noUnderlyings")),
            new TypeReference<List<NoUnderlyingDto>>() {
            });
        if (!StringUtil.isEmptyList(noUnderlyings)) {
            // 查询所有质押券信息的标的信息列表
            List<String> finprodMarketIdList = new ArrayList<>();
            for (NoUnderlyingDto noUnderlyingDto : noUnderlyings) {
                finprodMarketIdList.add(noUnderlyingDto.getUnderlyingSecurityID() + ".IB");
            }
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("finprodMarketIdList", finprodMarketIdList);
            List<FinProduct> finProductList = cfetsInterfMapper.selectFinProductList(paramMap);
            Map<String, FinProduct> finProductMap = new HashMap<>();
            if (!StringUtil.isEmptyList(finProductList)) {
                finProductMap = finProductList.stream().collect(
                    JoyinCollectors.toMap(x -> x.getFinprodMarketId(), x -> x));
            }

            for (NoUnderlyingDto noUnderlyingDto : noUnderlyings) {
                IbuQuoteCrepPledgeDto pledgeDto = new IbuQuoteCrepPledgeDto();
                pledgeDto.setQuoteId(ibuQuoteCrepDto.getQuoteId()); // 报价编号
                pledgeDto.setBranch(0l); // 分支序号

                // 质押金融产品代码
                String finMarketId = noUnderlyingDto.getUnderlyingSecurityID() + ".IB";
                pledgeDto.setFinprodMarketId(finMarketId);
                FinProduct finProduct = finProductMap.get(finMarketId);
                if (finProduct != null) {
                    pledgeDto.setPledgeFinprodId(finProduct.getFinprodId());
                }
                else {
                    pledgeDto.setPledgeFinprodId(finMarketId);
                }
                BigDecimal 质押券券面总额 = null;
                if (StringUtil.isNotEmpty(noUnderlyingDto.getUnderlyingQty())) {
                    质押券券面总额 = new BigDecimal(noUnderlyingDto.getUnderlyingQty());
                }
                // 版本号
                pledgeDto.setVersion(ibuQuoteCrepDto.getVersion());
                // 质押数量
                if (质押券券面总额 != null) {
                    pledgeDto.setPledgeNumber(
                        ArithmeticUtil.divide(质押券券面总额, BigDecimal.valueOf(100), 4));
                }
                // 质押率
                if (!StringUtil.isEmptyList(noUnderlyingDto.getNoUnderlyingStips())) {
                    pledgeDto.setPledgeRatio(ArithmeticUtil.divide(new BigDecimal(
                        noUnderlyingDto.getNoUnderlyingStips().get(0).getUnderlyingStipValue()),
                        BigDecimal.valueOf(100), 4));
                }
                // 质押金额
                if (质押券券面总额 != null) {
                    pledgeDto.setPledgeAmt(质押券券面总额);
                }
                
                shareAmt = ArithmeticUtil.doAdd(shareAmt, pledgeDto.getPledgeNumber());
                BigDecimal 当笔折算券面金额 = ArithmeticUtil.multiply(质押券券面总额, pledgeDto.getPledgeRatio());
                shareCtAmt = ArithmeticUtil.doAdd(shareCtAmt, 当笔折算券面金额);
                
                if (finProduct != null && StringUtil.equals(
                    com.joyintech.tams.constant.DictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue(),
                    finProduct.getRegistOrg())) {
                    cbShareCtAmt = ArithmeticUtil.doAdd(cbShareCtAmt, 当笔折算券面金额); // 中债折算券面金额
                }
                if (finProduct != null && StringUtil.equals(
                    com.joyintech.tams.constant.DictCodeList.登记托管机构_上海清算所.getValue(),
                    finProduct.getRegistOrg())) {
                    scShareCtAmt = ArithmeticUtil.doAdd(scShareCtAmt, 当笔折算券面金额); // 上清折算券面金额
                }
                
                ibuQuoteCrepPledgeDtoList.add(pledgeDto);
            }
        }
        ibuQuoteCrepDto.setShareAmt(shareAmt); // 交易份额
        ibuQuoteCrepDto.setShareCtAmt(shareCtAmt); // 折算券面金额
        String isKtg = "N";
        if (cbShareCtAmt != null && scShareCtAmt != null) {
            isKtg = "Y";
            ibuQuoteCrepDto.setCbShareCtAmt(cbShareCtAmt); // 中债折算券面金额
            ibuQuoteCrepDto.setScShareCtAmt(scShareCtAmt); // 上清折算券面金额
        }
        //多组合拆分
        if(null!=ibuQuoteCrepPledgeDtoList&&ibuQuoteCrepPledgeDtoList.size()>0&&StringUtil.isNotEmpty(ibuQuoteCrepDto.getQuoteId())){
            //查找关系表
            String quoteId = ibuQuoteCrepDto.getQuoteId();
            Map<String,Object> map = new HashMap<>();
            map.put("quoteId",quoteId);
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPrice001Mapper.selectEtrReByQuoteId(map);
            if(StringUtil.isNotEmpty(ptlAllocations)){
                List<IbuQuoteEtrReDto> ibuQuoteEtrRes = new ArrayList<>();
                List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = new ArrayList<>();
                Map<String,Object> mapFpriceAmt= new HashMap<>();
                //报文返回总金额
                mapFpriceAmt.put("isKtg",isKtg);
                mapFpriceAmt.put("ps",ibuQuoteCrepDto.getRepoPs());
                if(StringUtil.equals(isKtg,"Y")){
                    mapFpriceAmt.put("cbfpriceAmt",null!=ibuQuoteCrepDto.getCbFpriceAmt()?ibuQuoteCrepDto.getCbFpriceAmt():BigDecimal.ZERO);
                    mapFpriceAmt.put("scfpriceAmt",null!=ibuQuoteCrepDto.getScFpriceAmt()?ibuQuoteCrepDto.getScFpriceAmt():BigDecimal.ZERO);
                }else{
                    BigDecimal  fpriceAmtTotalBW = null!=ibuQuoteCrepDto.getFpriceAmt()?ibuQuoteCrepDto.getFpriceAmt():BigDecimal.ZERO;
                    BigDecimal  shareCtAmtTotalBW = null!=ibuQuoteCrepDto.getShareCtAmt()?ibuQuoteCrepDto.getShareCtAmt():BigDecimal.ZERO;
                    BigDecimal  shareAmtTotalBW = null!=ibuQuoteCrepDto.getShareAmt()?ibuQuoteCrepDto.getShareAmt():BigDecimal.ZERO;
                    mapFpriceAmt.put("fpriceAmt",fpriceAmtTotalBW);
                    mapFpriceAmt.put("shareCtAmt",shareCtAmtTotalBW);
                    mapFpriceAmt.put("shareAmt",shareAmtTotalBW);
                }
                ptlAllocations = cstpCommonService.组合分配拆分(ptlAllocations,mapFpriceAmt);
                //尾差处理  尾差放在指令的交易金额最大的记录
                for(PtlAllocationDto item : ptlAllocations){
                    IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
                    ibuQuoteEtrReDto.setQuoteId(quoteId);
                    ibuQuoteEtrReDto.setVersion(ibuQuoteCrepDto.getVersion());// 版本号
                    ibuQuoteEtrReDto.setEntrustId(item.getEntrustId());
                    ibuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
                    ibuQuoteEtrReDto.setShareAmt(item.getShareAmt());
                    ibuQuoteEtrReDto.setShareCtAmt(item.getShareCtAmt());
                    ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                    ibuQuoteEtrReDto.setCbFpriceAmt(item.getCbFpriceAmt());
                    ibuQuoteEtrReDto.setScFpriceAmt(item.getScFpriceAmt());
                    ibuQuoteEtrReDto.setCpriceAmt(item.getcPriceAmt());
                    ibuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                    ibuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                    ibuQuoteEtrReDto.setPriceModel(item.getPriceModel());
                    ibuQuoteEtrReDto.setIntAmt(item.getIntAmt());
                    ibuQuoteEtrReDto.setMtradeAmt(item.getMtradeAmt());
                    ibuQuoteEtrRes.add(ibuQuoteEtrReDto);
                }
                CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto = new CfetsRepoDealQuotedPrice001Dto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice001Dto,ibuQuoteCrepDto);
                //报文回来的券 跟 组合关联上
                //查找回购报价指令质押品
                List<CfetsRepoDealQuotedPrice003Dto> cfetsRepoDealQuotedPrice003Dtos = cfetsRepoDealQuotedPrice001Mapper.listIbuQuoteEntCrepPledge(quoteId);
                List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = new ArrayList<>();
                cfetsRepoDealQuotedPrice001Dto.setIsDzh("Y");
                cfetsRepoDealQuotedPrice001Dto.setPledgeBondList(pledgeBondList);
                cfetsRepoDealQuotedPrice001Dto.setPtlAllocationList(ptlAllocations);
                cfetsRepoDealQuotedPrice001Dto.setIsKtg(isKtg);
                cfetsRepoDealQuotedPrice001Dto.setIsMq("Y");
                cfetsRepoDealQuotedPrice001Dto.setVersion(ibuQuoteCrepDto.getVersion());
                if(!StringUtil.isEmptyList(cfetsRepoDealQuotedPrice003Dtos)){
                    pledgeBondList = ibuComponentCommonService.报文返回券关联组合拆分(cfetsRepoDealQuotedPrice003Dtos, ibuQuoteCrepPledgeDtoList);
                    //转换
                    if(!StringUtil.isEmptyList(pledgeBondList)){
                        for (CfetsRepoDealQuotedPrice003Dto item : pledgeBondList) {
                            IbuQuoteEntCrepPledgeDto ibuQuoteEntCrepPledge = new IbuQuoteEntCrepPledgeDto();
                            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteEntCrepPledge,item);
                            ibuQuoteEntCrepPledge.setQuoteId(item.getQuoteId());
                            ibuQuoteEntCrepPledge.setVersion(ibuQuoteCrepDto.getVersion());// 版本号
                            ibuQuoteEntCrepPledge.setEntrustId(item.getEntrustId());
                            ibuQuoteEntCrepPledge.setPortfolioId(item.getPortfolioId());
                            ibuQuoteEntCrepPledge.setPledgeFinprodId(item.getFinprodId());
                            ibuQuoteEntCrepPledge.setBondCode(item.getBondCode());
                            ibuQuoteEntCrepPledge.setBranch(0L);// 分支*/
                            ibuQuoteEntCrepPledge.setPledgeRatio(ArithmeticUtil.divide(item.getPledgeRate(), new BigDecimal(100), 4));// 质押率*/
                            ibuQuoteEntCrepPledge.setUnitCprice(item.getNetPrice());
                            ibuQuoteEntCrepPledges.add(ibuQuoteEntCrepPledge);
                        }
                    }
                }else{
                    for (IbuQuoteCrepPledgeDto item : ibuQuoteCrepPledgeDtoList) {
                        CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto,item);
                        cfetsRepoDealQuotedPrice003Dto.setFinprodId(item.getPledgeFinprodId());
                        cfetsRepoDealQuotedPrice003Dto.setPledgePrin(ArithmeticUtil.multiply(item.getPledgeNumber(),new BigDecimal("100")));
                        cfetsRepoDealQuotedPrice003Dto.setPledgeRate(ArithmeticUtil.multiply(item.getPledgeRatio(), BigDecimal.valueOf(100)));
                        pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                    }
                    ibuQuoteEntCrepPledges= ibuComponentCommonService.质押券拆分(cfetsRepoDealQuotedPrice001Dto);
                }
                resultDto.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
                resultDto.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteEntCrepPledges);
            }
        }
        resultDto.setIbuQuoteCrepDto(ibuQuoteCrepDto);
    	resultDto.setIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
    	return resultDto;
    }
	
    /**
     * Cfets接口数据转IbuQuoteBondDto  现券请求报价
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public IbuQuoteBondDto getIbuQuoteBondDtoForReq(Map<String, Object> param) {
        this.waitForSomeTime();

        String msgType = null;
        if(null!=param.get("msgType") ) {
            msgType = (String)param.get("msgType");
        }
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList = new ArrayList<IbuQuoteBondDetDto>();
        
        if(MsgTypeEnum.j.getValue().equals(msgType) && null!=param.get("clOrdID")) {// 35域-报文类型  // 11域-客户参考编号
            IbuQuoteBondDto ibuQuoteBondDto2 = ibuQuoteMapper.selectQuoteBondByKey((String)param.get("clOrdID"));
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, ibuQuoteBondDto2);
            ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion()+1);
            ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_失败.getValue());// j报文发送状态设为失败
            // 根据原操作类型，获取失败报文对应的操作类型
            ibuQuoteBondDto.setExeType(CfetsDataConvertUtil.genExeTypeForFailMsg(ibuQuoteBondDto.getExeType()));

            // 更新明细表数据
            cfetsInterfMapper.updateBondReqDetDataStatusForJ(ibuQuoteBondDto);

            // 更新主表数据
            ibuQuoteBondMapper.updateByPrimaryKeySelective(ibuQuoteBondDto);
            param.put("detList", null);
            
            //登录错误日志
            this.saveIbuQuoteErrlog(ibuQuoteBondDto.getQuoteId(), param);
            return null;
        }
        if(MsgTypeEnum.R.getValue().equals(msgType)
                ||MsgTypeEnum.U32.getValue().equals(msgType)
                || MsgTypeEnum.U29.getValue().equals(msgType)
                || MsgTypeEnum.AG.getValue().equals(msgType)) {//146域  抽取到param第一层中，方便共用逻辑
            JSONObject parseObject = JSON.parseObject(JSON.toJSONString(param));
            JSONArray array = parseObject.getJSONArray("noRelatedSym");
            @SuppressWarnings("unchecked")
            Map<String,Object> map= JSON.parseObject(array.get(0).toString(), HashMap.class);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!param.containsKey(entry.getKey())) {
                    param.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        // 60域-业务发生时间
        Date transactTime = null;
        if (null != param.get("transactTime")) {
            try {
                transactTime = DateUtil.parseDate((String)param.get("transactTime"),
                    CfetsConstant.DATETIME_FORMAT);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        // 报价日期
        if (null != transactTime) {
            ibuQuoteBondDto.setQuoteDate(transactTime);// 60域-业务发生时间
            ibuQuoteBondDto.setTransactime((String)param.get("transactTime"));
        }
        
        IbuQuoteBondDto ibuQuoteBondDto2 = null;
        // 先用【131域-请求报价编号】查询db中已存在报价信息
        if (param.get("quoteReqID") != null) {
            ibuQuoteBondDto2 = cfetsInterfMapper.selectIbuQuoteBondDtoDataByCfetsQuoteId(
                String.valueOf(param.get("quoteReqID")));
        }
        // 如果查询不到，再用【11域-客户参考编号】查询db中已存在报价信息 
        if (ibuQuoteBondDto2 == null && param.get("clOrdID") != null) {
            IbuQuoteBond ibuQuoteBond = ibuQuoteBondMapper.selectByPrimaryKey(String.valueOf(param.get("clOrdID")));
            if (ibuQuoteBond != null) {
                ibuQuoteBondDto2 = new IbuQuoteBondDto();
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto2, ibuQuoteBond);
            }
        }
        // 如果查询不到，再用【117域-报价编号】查询db中已存在报价明细信息，进而查询报价信息主表信息
        if (ibuQuoteBondDto2 == null && param.get("quoteID") != null) {
            IbuQuoteBondDetDto ibuQuoteBondDetDto = ibuQuoteMapper.selectQuoteDetByReqId(String.valueOf(param.get("quoteID")));
            if (ibuQuoteBondDetDto != null) {
                IbuQuoteBond ibuQuoteBond = ibuQuoteBondMapper.selectByPrimaryKey(ibuQuoteBondDetDto.getQuoteId());
                if (ibuQuoteBond != null) {
                    ibuQuoteBondDto2 = new IbuQuoteBondDto();
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto2, ibuQuoteBond);
                }
            }
        }
        if (ibuQuoteBondDto2 == null) {
            ibuQuoteBondDto2 = new IbuQuoteBondDto();
            ibuQuoteBondDto2.setQuoteId(String.valueOf(param.get("clOrdID")));
        }
        
        // 原主表交易状态
        String oldTradeStatus = ibuQuoteBondDto2.getTradeStatus();
        
        // 如果当前主表状态为成交中，则只处理8报文
        if (StringUtil.equals(DictCodeList.银行间上行报价状态_成交中.getValue(),
            ibuQuoteBondDto2.getTradeStatus())
            && !MsgTypeEnum.MSGTYPE_8.getValue().equals(msgType)) {
            return null;
        }
        // 如果当前主表状态为已成交，则后续接到任何报文直接忽略
        else if (StringUtil.equals(DictCodeList.银行间上行报价状态_已成交.getValue(),
            ibuQuoteBondDto2.getTradeStatus())) {
            return null;
        }
        // 接收请求报价拒绝通知（所有做市方），报文直接忽略
        else if (MsgTypeEnum.U32.getValue().equals(msgType)
            && StringUtil.equals("115", getParamVaule(param.get("quoteStatus")))) {
            return null;
        }
        
        List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList2 = ibuQuoteMapper.selectByQuoteListId(ibuQuoteBondDto2.getQuoteId());
        
        // 覆盖总表值
        this.converIbuQuoteBondDto(ibuQuoteBondDto2, param);
        // 请求报价所有的为非做市方
        String mySide = "03";
        String ibuQuoteBondDtoExeType = this.convertStatusCfetsCodeToSystemCode(
            getParamVaule(param.get("quoteType")), getParamVaule(param.get("msgType")),
            getParamVaule(param.get("quoteTransType")), getParamVaule(param.get("quoteStatus")),
            getParamVaule(param.get("execType")), mySide);// 业务系统操作类型
        ibuQuoteBondDtoExeType = ibuQuoteBondDtoExeType + "_" + mySide;

        // 若当前操作为本方请求报价撤回（交易状态为撤回中），则直接更新主表及明细表的状态为本方请求报价撤回成功
        if (StringUtil.equals(DictCodeList.银行间上行报价状态_撤回中.getValue(), oldTradeStatus)) {
            ibuQuoteBondDto2.setExeType(DictCodeList.银行间上行操作类型_本方请求报价撤回成功.getValue());// 操作类型
            ibuQuoteBondDto2.setTradeStatus(DictCodeList.银行间上行报价状态_已撤回.getValue());// 交易状态
            ibuQuoteBondDto2.setSendStatus(DictCodeList.银行间上行发送状态_成功.getValue());// 发送状态
        }
        else if (StringUtil.isNotEmpty(ibuQuoteBondDtoExeType)
            && ibuQuoteBondDtoExeType.split("_").length == 4) {
            // 接收请求回复过期通知(Z报文，报价状态为21-过期)场景，不更新主表状态，其他场景更新
            ibuQuoteBondDto2.setQuoteRoleType(ibuQuoteBondDtoExeType.split("_")[3]);// 本方角色
            ibuQuoteBondDto2.setExeType(ibuQuoteBondDtoExeType.split("_")[0]);// 操作类型
            if (!(MsgTypeEnum.Z.getValue().equals(msgType)
                && StringUtil.equals("21", getParamVaule(param.get("quoteStatus"))))) {
                ibuQuoteBondDto2.setTradeStatus(ibuQuoteBondDtoExeType.split("_")[1]);// 交易状态
                ibuQuoteBondDto2.setSendStatus(ibuQuoteBondDtoExeType.split("_")[2]);// 发送状态
            }
        }

        for (IbuQuoteBondDetDto ibuQuoteBondDetDto2 : ibuQuoteBondDetDtoList2) {
            // 若当前操作为本方请求报价撤回（交易状态为撤回中），则直接更新主表及明细表的状态为本方请求报价撤回成功
            if (StringUtil.equals(DictCodeList.银行间上行报价状态_撤回中.getValue(), oldTradeStatus)) {
                ibuQuoteBondDetDto2.setTradeStatus(DictCodeList.银行间上行报价状态_已撤回.getValue());// 交易状态
                ibuQuoteBondDetDto2.setSendStatus(DictCodeList.银行间上行发送状态_成功.getValue());// 发送状态

                IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, ibuQuoteBondDetDto2);
                ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto2.getVersion() + 1);
                ibuQuoteBondDetDtoList.add(ibuQuoteBondDetDto);
                
                continue;
            }
            
            if (MsgTypeEnum.U32.getValue().equals(msgType)) {
//                // 接收请求报价拒绝通知（所有做市方）
//                if (StringUtil.equals("115", getParamVaule(param.get("quoteStatus")))) {
//                    IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
//                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, ibuQuoteBondDetDto2);
//                    ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto2.getVersion() + 1);
//                    ibuQuoteBondDetDto.setTradeStatus(DictCodeList.银行间上行报价状态_已拒绝.getValue());
//                    ibuQuoteBondDetDtoList.add(ibuQuoteBondDetDto);
//                    continue;
//                }
                // 接收请求报价过期通知
                if (StringUtil.equals("21", getParamVaule(param.get("quoteStatus")))) {
                    IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, ibuQuoteBondDetDto2);
                    ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto2.getVersion() + 1);
                    ibuQuoteBondDetDto.setTradeStatus(DictCodeList.银行间上行报价状态_已过期.getValue());
                    ibuQuoteBondDetDtoList.add(ibuQuoteBondDetDto);
                    continue;
                }
            }
            
            // 请求报价S报文，单个交易员回复，明细表只更新当前交易员的
            if (MsgTypeEnum.S.getValue().equals(msgType) || MsgTypeEnum.Z.getValue().equals(msgType)
                || MsgTypeEnum.MSGTYPE_8.getValue().equals(msgType)
                || MsgTypeEnum.AG.getValue().equals(msgType)) {
                String cfetsPartyId = this.getCfetsPartyID(param, ibuQuoteBondDto2.getOuPartyId()); // 获取发送方交易账户21位码
                if (!StringUtil.equals(cfetsPartyId, ibuQuoteBondDetDto2.getCpPartyId())) {
//                    IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
//                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, ibuQuoteBondDetDto2);
//                    ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto2.getVersion() + 1);
//                    ibuQuoteBondDetDtoList.add(ibuQuoteBondDetDto);
                    continue;
                }
            }
            // 覆盖明细表值
            converIbuQuoteBondDetDto(ibuQuoteBondDetDto2, ibuQuoteBondDto2, param);
            // 处理交易和发送状态
            if (StringUtil.isNotEmpty(ibuQuoteBondDtoExeType)
                && ibuQuoteBondDtoExeType.split("_").length == 4) {
                ibuQuoteBondDetDto2.setTradeStatus(ibuQuoteBondDtoExeType.split("_")[1]);// 交易状态
                ibuQuoteBondDetDto2.setSendStatus(ibuQuoteBondDtoExeType.split("_")[2]);// 发送状态
            }
            IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, ibuQuoteBondDetDto2);
            ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto2.getVersion() + 1);
            ibuQuoteBondDetDtoList.add(ibuQuoteBondDetDto);
        }

        BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, ibuQuoteBondDto2);
        //所有报文回来 证券账户和创建人保留历史数据
        if(StringUtil.isNotEmpty(ibuQuoteBondDto.getQuoteId())){
            IbuQuoteBondDto ibuQuoteBondDtoCfetByQuoteId = ibuQuoteMapper.selectQuoteBondByKey(ibuQuoteBondDto.getQuoteId());
            if(null!=ibuQuoteBondDtoCfetByQuoteId){
                ibuQuoteBondDto.setSecManageAcctId(ibuQuoteBondDtoCfetByQuoteId.getSecManageAcctId());
                ibuQuoteBondDto.setCreateUser(ibuQuoteBondDtoCfetByQuoteId.getCreateUser());
                ibuQuoteBondDto.setDipType(ibuQuoteBondDtoCfetByQuoteId.getDipType());
            }
        }
        // 根据明细数据的状态，修改主表数据状态
        this.handleBondReqQuoteStatus(ibuQuoteBondDto, ibuQuoteBondDetDtoList2);
        
        param.put("detList", ibuQuoteBondDetDtoList);
        if(ibuQuoteBondDto2!=null&&null!=ibuQuoteBondDto2.getVersion()) {
            ibuQuoteBondDto.setVersion(ibuQuoteBondDto2.getVersion()+1);
        }
        else {
            ibuQuoteBondDto.setVersion(-1l);
        }
        return ibuQuoteBondDto;
    }
	
	/**
	 * 接收到报文后，先等待一段时间，防止此次报文的前置数据库处理尚未提交
	 */
	private void waitForSomeTime() {
	    try {
            Thread.sleep(maxWaitTime);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

    /**
     * 从报文中获取行权收益率
     * 
     * @param param 报文
     * @return 行权收益率
     */
    private BigDecimal genExerYield(Map<String, Object> param) {
        if (param == null) {
            return null;
        }

        List<NoStipulationDto> noStipulations = JSONObject.parseObject(
            JSON.toJSONString(param.get("noStipulations")),
            new TypeReference<List<NoStipulationDto>>() {
            });
        if (!StringUtil.isEmptyList(noStipulations)) {
            for (NoStipulationDto noStipulationDto : noStipulations) {
                if (StringUtil.equals("StrikeYield", noStipulationDto.getStipulationType())
                    && StringUtil.isNotBlank(noStipulationDto.getStipulationValue())) {
                    return ArithmeticUtil.divide(
                        new BigDecimal(noStipulationDto.getStipulationValue()),
                        BigDecimal.valueOf(100), 6);
                }
            }
        }

        return null;
    }

	/**
	 * 获取回购对话报价的
	 * 操作类型、本方角色、报价状态、发送状态、本方银行间机构、本方银行间交易员、对手银行间机构、对手方银行间交易员字段信息
	 * @param param 报文参数
	 * @param ibuQuoteCrepDto 回购对话报价dto
	 */
	private void genIbuQuoteCrepExeTypeInfo(Map<String, Object> param, IbuQuoteCrepDto ibuQuoteCrepDto) {
	    // 获取本方角色、本方银行间机构、本方银行间交易员、对手银行间机构、对手方银行间交易员字段信息(如果db里已经有值，则以db为准)
	    this.setPartyMap(param);
	    if (StringUtil.isBlank(ibuQuoteCrepDto.getQuoteRoleType())) {
	        ibuQuoteCrepDto.setQuoteRoleType(param.get("mySide").toString()); // 本方角色
	    }
	    if (StringUtil.isBlank(ibuQuoteCrepDto.getOuPartyId())) {
	        ibuQuoteCrepDto.setOuPartyId(param.get("ouPartyId").toString()); // 本方银行间机构
        }
        if (StringUtil.isBlank(ibuQuoteCrepDto.getOuTraderId())) {
            ibuQuoteCrepDto.setOuTraderId(param.get("ouTrader").toString()); // 本方银行间交易员
        }
        if (StringUtil.isBlank(ibuQuoteCrepDto.getCpPartyId())) {
            ibuQuoteCrepDto.setCpPartyId(param.get("cpPartyId").toString()); // 对手银行间机构
        }
        if (StringUtil.isBlank(ibuQuoteCrepDto.getCpTraderId())) {
            ibuQuoteCrepDto.setCpTraderId(param.get("cpTrader").toString()); // 对手方银行间交易员
        }
	    
	    // 操作类型、报价状态、发送状态
	    String quoteTransType = getParamVaule(param.get("quoteTransType"));
	    // AJ报文使用quoteRespType
	    if (StringUtil.equals(MsgTypeEnum.AJ.getValue(), getParamVaule(param.get("msgType")))) {
	        quoteTransType = getParamVaule(param.get("quoteRespType"));
	    }
        String ibuQuoteBondDtoExeType = this.convertStatusCfetsCodeToSystemCode(
            getParamVaule(param.get("quoteType")), getParamVaule(param.get("msgType")),
            quoteTransType, getParamVaule(param.get("quoteStatus")),
            getParamVaule(param.get("execType")), ibuQuoteCrepDto.getQuoteRoleType());// 业务系统操作类型
        List<String> tempList = StringUtil.splitToList("_", ibuQuoteBondDtoExeType);
        if (!StringUtil.isEmptyList(tempList) && tempList.size() > 2) {
            //针对交谈修改  操作类型特殊处理
            if (StringUtil.equals(MsgTypeEnum.AI.getValue(), getParamVaule(param.get("msgType")))
            &&StringUtils.equals(ibuQuoteCrepDto.getExeType(),DictCodeList.银行间上行操作类型_本方交谈修改发送.getValue())){
                ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方交谈修改发送成功.getValue());
            }
            else{
                ibuQuoteCrepDto.setExeType(tempList.get(0));
            }
            ibuQuoteCrepDto.setTradeStatus(tempList.get(1));
            ibuQuoteCrepDto.setSendStatus(tempList.get(2));
        }
	}

	/**
     * Cfets数据字典转换 根据Cfets数据字典转换表基础数据，将Cfets数据代码转化为系统数据代码
     * 
     * @param fieldCode 字段代码
     * @param systemItemCode 系统数据代码
     * @return Cfets数据代码
     */
    private String convertCfetsCodeToSystemCode(String fieldCode, String cfetsItemCode) {
//        @SuppressWarnings("unchecked")
//        Map<String, String> dataMap = CacheUtil.getAppCache("CFETS_DATADICT_CONVERT_DATA2",
//            Map.class);

        Map<String, String> dataMap = null;
        if (dataMap == null || dataMap.isEmpty()) {
            List<CfetsDatadictConvert> cfetsDatadictConvertList = cfetsInterfMapper.selectCfetsDataDictConvertData();

            if (!StringUtil.isEmptyList(cfetsDatadictConvertList)) {
                dataMap = (Map<String, String>)cfetsDatadictConvertList.stream().collect(
                    JoyinCollectors.toMap(x -> x.getFieldCode() + "_" + x.getCfetsItemCode(),
                        x -> x.getSystemItemCode()));
                
//                CacheUtil.putAppCache("CFETS_DATADICT_CONVERT_DATA2", dataMap);
            }
        }
        
        String dataKey = fieldCode + "_" + cfetsItemCode;
        if (dataMap != null && dataMap.containsKey(dataKey)) {
            return dataMap.get(dataKey);
        }

        return cfetsItemCode;
    }
    
    /**
     * Cfets数据字典转换 根据Cfets数据字典转换表基础数据，将Cfets数据代码转化为系统数据代码
     * 
     * @param quoteTypeIbu 报价类别
     * @param msgType 报文类型
     * @param quoteTransType 操作类型
     * @param quoteStatus 报价状态
     * @param execType 成交状态
     * @param mySide 业务系统本方角色
     * @return 业务系统操作类型
     */
    private String convertStatusCfetsCodeToSystemCode(String quoteTypeIbu, String msgType,String quoteTransType,String quoteStatus,String execType,String mySide) {
//        @SuppressWarnings("unchecked")
//        Map<String, String> dataMap = CacheUtil.getAppCache("CFETS_STATUS_DATADICT_CONVERT_DATA",
//            Map.class);

        Map<String, String> dataMap = null;
        if (dataMap == null || dataMap.isEmpty()) {
            List<CfetsStatusDatadictConvert> cfetsStatusDatadictConvertList = cfetsInterfMapper.selectCfetsStatusDataDictConvertData();

            if (!StringUtil.isEmptyList(cfetsStatusDatadictConvertList)) {
                dataMap = (Map<String, String>)cfetsStatusDatadictConvertList.stream().collect(
                    JoyinCollectors.toMap(x -> x.getQuoteTypeIbu() + "_" + x.getMsgType()+ "_" + getStatusVaule(x.getQuoteTransType())+ "_" + getStatusVaule(x.getQuoteStatus())+ "_" + getStatusVaule(x.getExecType())+ "_" + getStatusVaule(x.getMySide()),
                        x -> getStatusVaule(x.getExeType()) + "_" + getStatusVaule(x.getTradeStatus()) + "_" + getStatusVaule(x.getSendStatus())));
                
//                CacheUtil.putAppCache("CFETS_STATUS_DATADICT_CONVERT_DATA", dataMap);
            }
        }
        
        String dataKey = quoteTypeIbu + "_" + msgType + "_" + quoteTransType + "_" + quoteStatus + "_" + execType + "_" + mySide;
        if (dataMap != null && dataMap.containsKey(dataKey)) {
            return dataMap.get(dataKey);
        }
        return null;
    }
    
    private String getIbuQuoteBondDtoExeType(IbuQuoteBondDto ibuQuoteBondDto,Map<String, Object> param) {
    	// 获取本方角色、本方银行间机构、本方银行间交易员、对手银行间机构、对手方银行间交易员字段信息
	    this.setPartyMap(param);
	    if (StringUtil.isBlank(ibuQuoteBondDto.getQuoteRoleType())) {
	        ibuQuoteBondDto.setQuoteRoleType(param.get("mySide").toString()); // 本方角色
	    }
	    if (StringUtil.isBlank(ibuQuoteBondDto.getOuPartyId())) {
	        ibuQuoteBondDto.setOuPartyId(param.get("ouPartyId").toString()); // 本方银行间机构
	    }
	    if (StringUtil.isBlank(ibuQuoteBondDto.getOuTraderId())) {
	        ibuQuoteBondDto.setOuTraderId(param.get("ouTrader").toString()); // 本方银行间交易员
	    }
//	    String mySide = param.get("mySide").toString();
    	
    	String exeType = this.convertStatusCfetsCodeToSystemCode(getParamVaule(param.get("quoteType")), getParamVaule(param.get("msgType")), getParamVaule(param.get("quoteTransType")), getParamVaule(param.get("quoteStatus")), getParamVaule(param.get("execType")), ibuQuoteBondDto.getQuoteRoleType());//业务系统操作类型
    	exeType = exeType + "_" + ibuQuoteBondDto.getQuoteRoleType();
    	return exeType;
    }
    
    private String getParamVaule(Object paramObject) {
    	if(paramObject!=null) {
    		String paramString = String.valueOf(paramObject);
    		if(StringUtil.isNotEmpty(paramString)) {
    			return paramString;
    		}
    	}
    	return "";
    }
    
    private String getIbuQuoteBondDetDtoExeType(IbuQuoteBondDetDto ibuQuoteBondDetDto,Map<String, Object> param) {
    	// 获取本方角色、本方银行间机构、本方银行间交易员、对手银行间机构、对手方银行间交易员字段信息
//	    this.setPartyMap(param);
        if (StringUtil.isBlank(ibuQuoteBondDetDto.getCpPartyId())) {
            ibuQuoteBondDetDto.setCpPartyId(param.get("cpPartyId").toString()); // 对手银行间机构
        }
        if (StringUtil.isBlank(ibuQuoteBondDetDto.getCpTrader())) {
            ibuQuoteBondDetDto.setCpTrader(param.get("cpTrader").toString()); // 对手方银行间交易员
        }
	    String mySide = param.get("mySide").toString();
	    
    	String exeType = this.convertStatusCfetsCodeToSystemCode(getParamVaule(param.get("quoteType")), getParamVaule(param.get("msgType")), getParamVaule(param.get("quoteTransType")), getParamVaule(param.get("quoteStatus")), getParamVaule(param.get("execType")), mySide);//业务系统操作类型
    	return exeType;
    }
    
    /*
     * 获取本方角色、本方银行间机构、本方银行间交易员、对手银行间机构、对手方银行间交易员字段信息
     */
    private void setPartyMap(Map<String, Object> param){
    	param.put("mySide", "02");//业务系统本方角色 默认 回复方
    	List<NoPartyIDDto> noPartyIDs = JSONObject.parseObject(JSON.toJSONString(param.get("noPartyIDs")),new TypeReference<List<NoPartyIDDto>>() {});
    	String partyID101 = "";
    	String partyID102 = "";
    	String partySubID101 = "";
    	String partySubID102 = "";
    	if (!StringUtil.isEmptyList(noPartyIDs)) {
    	    for(NoPartyIDDto noPartyIDDto:noPartyIDs) {
    	        if(101 == noPartyIDDto.getPartyRole() || 1 == noPartyIDDto.getPartyRole()) {//发送方
    	            if(null!=noPartyIDDto.getPartyID()) {
    	                partyID101 = noPartyIDDto.getPartyID();// 448域-发起方交易账户 21 位码
    	            }
    	            if (!StringUtil.isEmptyList(noPartyIDDto.getNoPartySubIDs())) {
    	                for(NoPartySubIDDto noPartySubIDDto:noPartyIDDto.getNoPartySubIDs()) {
    	                    if(null!=noPartySubIDDto.getPartySubID()) {
    	                        partySubID101 = noPartySubIDDto.getPartySubID();// 523域:交易员ID
    	                    }
    	                }
    	            }
    	        }else {//接收方
    	            if(null!=noPartyIDDto.getPartyID()) {
    	                partyID102 = noPartyIDDto.getPartyID();// 448域-发起方交易账户 21 位码
    	            }
    	            if (!StringUtil.isEmptyList(noPartyIDDto.getNoPartySubIDs())) {
    	                for(NoPartySubIDDto noPartySubIDDto:noPartyIDDto.getNoPartySubIDs()) {
    	                    if(null!=noPartySubIDDto.getPartySubID()) {
    	                        partySubID102 = noPartySubIDDto.getPartySubID();// 523域:交易员ID
    	                    }
    	                }
    	            }
    	        }
    	    }
    	}
    	if(StringUtils.isNotEmpty(partyID101)) {
    	    int dataCount = cfetsInterfMapper.selectIbuPortfolioDataCount(partyID101);
            if (dataCount > 0) {
                param.put("mySide", "01");// 业务系统本方角色 询价方
                param.put("ouPartyId", partyID101);
                param.put("ouTrader", partySubID101);
                param.put("cpPartyId", partyID102);
                param.put("cpTrader", partySubID102);
            }
            else {
                param.put("mySide", "02");//// 业务系统本方角色 回复方
                param.put("ouPartyId", partyID102);
                param.put("ouTrader", partySubID102);
                param.put("cpPartyId", partyID101);
                param.put("cpTrader", partySubID101);
            }
    	}
    }
    
    private String getStatusVaule(String status) {
    	if(StringUtil.isNotEmpty(status) && status.split("-").length==2) {
    		return status.split("-")[0];
    	}
    	if (StringUtil.isNotBlank(status)) {
    	    return status;
    	}
    	
    	return "";
    }

    /**
     * 现券请求报价
     * 根据明细数据的状态，修改主表数据状态
     * @param ibuQuoteBondDto 主表数据
     * @param ibuQuoteBondDetDtoList 明细数据
     */
    private void handleBondReqQuoteStatus(IbuQuoteBondDto ibuQuoteBondDto, List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList) {
        if (ibuQuoteBondDto == null || StringUtil.isEmptyList(ibuQuoteBondDetDtoList)) {
            return;
        }
        
        Map<String, String> detStatusMap = ibuQuoteBondDetDtoList.stream().collect(
            JoyinCollectors.toMap(x -> StringUtil.isBlank(x.getTradeStatus()) ? "null" : x.getTradeStatus() , x -> "tradeStatus"));
        
        // 如果所有明细的状态都是已拒绝，则主表状态设置为已终止
        if (detStatusMap != null && detStatusMap.size() == 1
            && detStatusMap.containsKey(DictCodeList.银行间上行报价状态_已拒绝.getValue())) {
            ibuQuoteBondDto.setExeType(DictCodeList.银行间上行操作类型_接收全拒绝请求报价.getValue());
            ibuQuoteBondDto.setTradeStatus(DictCodeList.银行间上行报价状态_已终止.getValue());
        }
//        else if (detStatusMap != null && detStatusMap.size() == 1
//            && detStatusMap.containsKey(DictCodeList.银行间上行报价状态_已过期.getValue())) {
//            ibuQuoteBondDto.setExeType(DictCodeList.银行间上行操作类型_接收已过期.getValue());
//            ibuQuoteBondDto.setTradeStatus(DictCodeList.银行间上行报价状态_已过期.getValue());
//        }
    }
    
    private void converIbuQuoteBondDto(IbuQuoteBondDto ibuQuoteBondDto,Map<String, Object> param) {
    	if(null!=param.get("securityID")) {// 48域-债券代码
    		String securityID = (String) param.get("securityID");
    		ibuQuoteBondDto.setFinprodMarketId(securityID+".IB");//市场代码
    	}
    	if(StringUtil.isBlank(ibuQuoteBondDto.getQuoteId()) && null!=param.get("clOrdID")) {// 11域-客户参考编号
    		ibuQuoteBondDto.setQuoteId((String) param.get("clOrdID"));//报价编号（内部代码）
    	}
    	ibuQuoteBondDto.setQuoteType((String) param.get("quoteType"));//报价类型
//    	String ibuQuoteBondDtoExeType = this.getIbuQuoteBondDtoExeType(ibuQuoteBondDto, param);
//    	if(StringUtil.isNotEmpty(ibuQuoteBondDtoExeType) && ibuQuoteBondDtoExeType.split("_").length==4) {
//    		ibuQuoteBondDto.setQuoteRoleType(ibuQuoteBondDtoExeType.split("_")[3]);//本方角色
//    		ibuQuoteBondDto.setExeType(ibuQuoteBondDtoExeType.split("_")[0]);//操作类型
//    	}
    	if(StringUtil.isBlank(ibuQuoteBondDto.getpQuoteId()) && null!=param.get("clOrdID")) {// 11域-客户参考编号
    		ibuQuoteBondDto.setpQuoteId((String) param.get("clOrdID"));//外部报价编号
    	}
    	if(StringUtil.isBlank(ibuQuoteBondDto.getCfetsQuoteId()) && null!=param.get("quoteReqID")) {// 131域-请求报价编号
    		ibuQuoteBondDto.setCfetsQuoteId((String) param.get("quoteReqID"));//CFETS报价编号
    	}
    	ibuQuoteBondDto.setBranch(0l);//分支序号
    	if(null!=param.get("settlDate")) {// 64域-结算日
    	    ibuQuoteBondDto.setSettleDate(DateUtil.parse((String)param.get("settlDate")));// 交割日期
    	}
    	if(null!=param.get("settlType")) {// 63域-清算速度
    		String settlType = this.convertCfetsCodeToSystemCode("settlType", (String)param.get("settlType"));
    		ibuQuoteBondDto.setSettleSpeed(Long.parseLong(settlType));//清算速度
    	}
    	if(null!=param.get("deliveryType")) {// 919域-结算方式
    		String deliveryType = this.convertCfetsCodeToSystemCode("deliveryType", (String)param.get("deliveryType"));
    		ibuQuoteBondDto.setDeliveryType(deliveryType);// 结算方式
    	}
    	if(null!=param.get("clearingMethod")) {// 11143域-清算类型
    		String clearingMethod = this.convertCfetsCodeToSystemCode("clearingMethod", (String)param.get("clearingMethod"));
    		ibuQuoteBondDto.setSettType(clearingMethod);// 清算类型
    	}
    	if(null!=param.get("settlCurrency")) {// 120域-结算币种
    		ibuQuoteBondDto.setSettCcy((String)param.get("settlCurrency"));// 结算币种
    	}
    	if(StringUtil.isBlank(ibuQuoteBondDto.getPs()) && null!=param.get("side")) {// 54域-交易方向
    		String side = (String)param.get("side");
    		if("1".equals(side)) {
    			side = BussinessDictCodeList.交易方向_买入.getValue();
    		}else {
    			side = BussinessDictCodeList.交易方向_卖出.getValue();
    		}
    		ibuQuoteBondDto.setPs(CfetsDataConvertUtil.convertBondPs(side));// 交易方向
    	}
    	// modify by etliu 20210316 银行间上下行多组合 start
    	// 请求报价主表的金额信息，是请求时总的金额信息，以报价发起时为准，后面不更新
//    	if(null!=param.get("orderQty")) {// 38域-券面总额
//    		ibuQuoteBondDto.setShareAmt(ArithmeticUtil.divide(new BigDecimal((String) param.get("orderQty")), BigDecimal.valueOf(100), 4));// 交易份额，债券数量等
//    	}
//    	if(null!=param.get("settlCurrAmt")) {// 119域-结算金额
//    		ibuQuoteBondDto.setFpriceAmt(new BigDecimal((String) param.get("settlCurrAmt")));// 全价总额
//    	}
    	// modify by etliu 20210316 银行间上下行多组合 end
//    	if(StringUtil.isNotEmpty(ibuQuoteBondDtoExeType) && ibuQuoteBondDtoExeType.split("_").length==4) {
//    		ibuQuoteBondDto.setTradeStatus(ibuQuoteBondDtoExeType.split("_")[1]);//交易状态
//    		ibuQuoteBondDto.setSendStatus(ibuQuoteBondDtoExeType.split("_")[2]);//发送状态
//    	}
        if (null != param.get("transactTime")) {// 60域-业务发生时间
            ibuQuoteBondDto.setTransactime((String)param.get("transactTime"));
        }
    }
    
    private void converIbuQuoteBondDetDto(IbuQuoteBondDetDto ibuQuoteBondDetDto, IbuQuoteBondDto ibuQuoteBondDto2, Map<String, Object> param) {
    	if(StringUtil.isBlank(ibuQuoteBondDetDto.getCfetsReqId()) && null!=param.get("quoteID")) {// 117域-报价编号
    		ibuQuoteBondDetDto.setCfetsReqId((String) param.get("quoteID"));//CFETS请求编号
    	}
    	if (ibuQuoteBondDto2 != null && StringUtil.isNotBlank(ibuQuoteBondDto2.getQuoteId())) {
    	    ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto2.getQuoteId());// 报价编号（内部代码）
    	}
    	else if(null!=param.get("clOrdID")) {// 11域-客户参考编号
    		ibuQuoteBondDetDto.setQuoteId((String) param.get("clOrdID"));// 报价编号（内部代码）
    	}
    	if (StringUtil.isBlank(ibuQuoteBondDetDto.getReqId())) { // 117域-报价编号
    	    ibuQuoteBondDetDto.setReqId(ibuQuoteBondDetDto.getCfetsReqId()); // 请求编号(请求报价使用的外部编号)
    	}
    	
    	if(null!=param.get("price")) {// 44域-净价
    		ibuQuoteBondDetDto.setUnitCprice(new BigDecimal((String) param.get("price")));// 单位净价
    	}
    	if(null!=param.get("accruedInterestAmt")) {// 159域-应计利息
    		ibuQuoteBondDetDto.setUnitInt(new BigDecimal((String) param.get("accruedInterestAmt")));// 单位利息 
    	}
    	if(null!=param.get("dirtyPrice")) {// 10048域-全价
    		ibuQuoteBondDetDto.setUnitFprice(new BigDecimal((String) param.get("dirtyPrice")));// 单位全价
    	}
    	if(null!=param.get("principal")) {// 10239域-每百元本金额
    		ibuQuoteBondDetDto.setParValue(new BigDecimal((String) param.get("principal")));//单位面值，债券100或者当前面值(还本前)，其他资产1 
    	}
    	if(null!=param.get("orderQty")) {//38域-券面总额
    		ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.divide(new BigDecimal((String) param.get("orderQty")), BigDecimal.valueOf(100), 4));// 交易份额，债券数量等
    	}
    	if(null!=param.get("totalPrincipal")) {// 10310域-总本金或者当前本金总额
    		ibuQuoteBondDetDto.setPrinAmt(new BigDecimal((String) param.get("totalPrincipal")));//  交易本金，交易成本等
    	}
    	if(null!=param.get("orderQty")) {// 38域-券面总额
    		ibuQuoteBondDetDto.setParValueAmt(new BigDecimal((String) param.get("orderQty")));// 面值总额
    	}
    	if(null!=param.get("price") && null!=param.get("orderQty")) {//44域-净价  38域-券面总额
    		ibuQuoteBondDetDto.setCpriceAmt(new BigDecimal((String) param.get("price")).multiply(new BigDecimal((String) param.get("orderQty")).divide(new BigDecimal(100))));// 净价总额
    	}
    	if(null!=param.get("accruedInterestTotalAmt")) {// 10002域-应计利息总额
    		ibuQuoteBondDetDto.setIntAmt(new BigDecimal((String) param.get("accruedInterestTotalAmt")));//利息总额
    	}
    	if(null!=param.get("settlCurrAmt")) {// 119域-结算金额
    		ibuQuoteBondDetDto.setFpriceAmt(new BigDecimal((String) param.get("settlCurrAmt")));//全价总额
    	}
    	if(null!=param.get("yield")) {//236域-到期收益率值
    		ibuQuoteBondDetDto.setYield(ArithmeticUtil.divide(new BigDecimal((String) param.get("yield")), BigDecimal.valueOf(100), 6));// 到期收益率
    	}
    	// 234域-行权收益率值
    	BigDecimal exerYield = this.genExerYield(param);
    	if (exerYield != null) {
    	    ibuQuoteBondDetDto.setExerYield(exerYield);
    	}
    	
//    	String ibuQuoteBondDetDtoExeType = this.getIbuQuoteBondDetDtoExeType(ibuQuoteBondDetDto, param);
//    	if(StringUtil.isNotEmpty(ibuQuoteBondDetDtoExeType) && ibuQuoteBondDetDtoExeType.split("_").length==3) {
//    		ibuQuoteBondDetDto.setTradeStatus(ibuQuoteBondDetDtoExeType.split("_")[1]);//交易状态
//    		ibuQuoteBondDetDto.setSendStatus(ibuQuoteBondDetDtoExeType.split("_")[2]);//发送状态
//    	}
    	if(null!=param.get("execID")) {// 成交编号
    		ibuQuoteBondDetDto.setExecid((String) param.get("execID"));// 17域-成交编号
    	}
    	if(null!=param.get("validUntilTime")) {// 62域-报价有效时间
    		try {
				ibuQuoteBondDetDto.setReEffTime(DateUtil.parseDate((String)param.get("validUntilTime"),CfetsConstant.DATETIME_FORMAT));//回复有效时间
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	// 对手方交易员
        if (StringUtil.isBlank(ibuQuoteBondDetDto.getCpTrader())) {
            String cpTrader = this.getCfetsPartySubID(param, ibuQuoteBondDto2.getOuTraderId());
            if (!StringUtil.equals(ibuQuoteBondDto2.getOuTraderId(), cpTrader)) {
                ibuQuoteBondDetDto.setCpTrader(cpTrader);
            }
        }

        if (null != param.get("transactTime")) {// 60域-业务发生时间
            ibuQuoteBondDetDto.setTransactime((String)param.get("transactTime"));
        }
    }
    
    /**
     * 获取对手方交易员
     * <一句话功能简述>
     * <功能详细描述>
     * @param param
     * @param 本方交易员
     * @return
     */
    private String getCfetsPartySubID(Map<String, Object> param, String 本方交易员) {
    	List<NoPartyIDDto> noPartyIDs = JSONObject.parseObject(JSON.toJSONString(param.get("noPartyIDs")),new TypeReference<List<NoPartyIDDto>>() {});
    	if (!StringUtil.isEmptyList(noPartyIDs)) {
    	    for(NoPartyIDDto noPartyIDDto:noPartyIDs) {
//    	        if(101==noPartyIDDto.getPartyRole()) {//发送方
    	        if (!StringUtil.isEmptyList(noPartyIDDto.getNoPartySubIDs())) {
    	            for (NoPartySubIDDto noPartySubIDDto : noPartyIDDto.getNoPartySubIDs()) {
    	                if (null != noPartySubIDDto.getPartySubID()
    	                    && !StringUtil.equals(本方交易员, noPartySubIDDto.getPartySubID())) {
    	                    return noPartySubIDDto.getPartySubID();// 523域:交易员ID
    	                }
    	            }
    	        }
//    	        }
    	    }
    	}
    	return "";
    }
    
    /**
     * 获取发送方交易账户21位码
     * @param param 参数
     * @return 发送方交易账户21位码
     */
    public String getCfetsPartyID(Map<String, Object> param, String 本方机构) {
        List<NoPartyIDDto> noPartyIDs = JSONObject.parseObject(JSON.toJSONString(param.get("noPartyIDs")),new TypeReference<List<NoPartyIDDto>>() {});
        if (!StringUtil.isEmptyList(noPartyIDs)) {
            for(NoPartyIDDto noPartyIDDto:noPartyIDs) {
//                if(101==noPartyIDDto.getPartyRole()) {//发送方
                if (!StringUtil.equals(本方机构, noPartyIDDto.getPartyID())) {
                    return noPartyIDDto.getPartyID(); // 448域:交易账户21位码
                }
//                }
            }
        }
        return "";
    }
    
    /**
     * 保存错误信息
     * @param quoteId 报价编号
     * @param errCode 错误码
     * @param errMsg 错误信息
     */
    private void saveIbuQuoteErrlog(String quoteId, Map<String, Object> paramMap) {
        // 登录错误日志
        String errCode = StringUtil.isNotEmpty(paramMap.get("applErrorCode")) ? paramMap.get(
            "applErrorCode").toString() : StringUtil.EMPTY;
        String errMsg = StringUtil.isNotEmpty(paramMap.get("applErrorDesc")) ? paramMap.get(
            "applErrorDesc").toString() : StringUtil.EMPTY;
        ibuMainServiceImpl.saveIbuQuoteErrlog(quoteId, errCode, errMsg);
    }
}
