/*
 * 文件名：IbuComponentReceiveService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.ibu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteBondMapper;
import com.joyintech.background.component.uplink.interbank.database.dao.IbuQuoteCrepMapper;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteBond;
import com.joyintech.background.component.uplink.interbank.database.entity.IbuQuoteCrep;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.cfets.service.CfetsInterfReceiveService;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.service.bankuplink.repo.dao.CfetsRepoDealQuotedPrice001Mapper;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.service.bankuplink.repo.service.CfetsRepoDealQuotedPrice001Service;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.trade.dao.BaseAssetCashPositionCommMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 接收网关返回的数据
 * 
 * @author zhangliang
 * @version 1.0 2020年5月28日
 * @see IbuComponentReceiveService
 * @since 1.0
 */
@Service
public class IbuComponentReceiveService extends BaseService{
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuMainService ibuMainService;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    /**
     * {@linkplain}
     */
    @Autowired
    private BaseGetBussinessId bussinessIdGetter;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuQuoteBondMapper ibuQuoteBondMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private IbuQuoteCrepMapper ibuQuoteCrepMapper;
    
    @Autowired
    private CfetsInterfReceiveService cfetsInterfReceiveService;
    
    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;
    
    @Autowired
    private CfetsRepoDealQuotedPrice001Service cfetsRepoDealQuotedPrice001Service;
    
    /**
     * IbuComponentRiskService
     */
    @Autowired
    private IbuComponentRiskService ibuComponentRiskService;

    @Autowired
    private CfetsRepoDealQuotedPrice001Mapper cfetsRepoDealQuotedPrice001Mapper;


    @Autowired
    BaseAssetCashPositionCommMapper baseAssetCashPositionCommMapper;

    /**
     * 组合可用持仓和实际持仓查询更新
     */
    @Autowired(required = false)
    private BasePtlSecPositionInfoService basePtlSecPositionInfoService;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(IbuComponentReceiveService.class);

    /**
     * 根据业务编号规则流水号生成业务代码
     * 
     * @param idRuleSeqno 业务编号规则流水号
     * @return 业务代码
     * @see
     * @since 1.0
     */
    private String genBusId(String idRuleSeqno) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setSeqNo(idRuleSeqno);
        return bussinessIdGetter.buildBussinessCode(codeParameter);
    }
    
    /**
     * 超时刷新机制
     * 
     * @param quoteId 报价编号
     * @return 返回结果
     * @see
     * @since 1.0
     */
    @Transactional
    public ResultBaseDto timeOutQuoteRefreshBond(String quoteId){
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        
        IbuQuoteBond ibuQuoteBond = ibuQuoteBondMapper.selectByPrimaryKey(quoteId);
        if (ibuQuoteBond != null){
            Map<String, Object> map = new HashMap<String, Object>();
            // 传入pQuoteId
            map.put("pQuoteId", ibuQuoteBond.getpQuoteId());
            map.put("quoteId", ibuQuoteBond.getQuoteId());
            map.put("quoteType", ibuQuoteBond.getQuoteType());
            if (StringUtil.equals(ibuQuoteBond.getQuoteType(), BussinessDictCodeList.报价方式_对话报价.getValue())) {
                Object o = restTemplate.postForObject("http://jetGatewayService/jetGateway/queryQuateStatus", map, Object.class);
                // 无法查询到报价，发送状态则更新为失败，可以回退重操作
                Map<String, Object> param = JsonUtils.object2Map(o);
                if (StringUtil.equals(param.get("errCode"), "D2C_9998")){
                    param.put("quoteId", ibuQuoteBond.getQuoteId());
                    param.put("exeType", DictCodeList.银行间上行操作类型_本方新增发送失败.getValue());
                }
                resultBaseDto = this.updateBondDialogueQuoteInterfResp(param);
            }
            else if (StringUtil.equals(ibuQuoteBond.getQuoteType(), BussinessDictCodeList.报价方式_请求报价.getValue())) {
                Object o = restTemplate.postForObject("http://jetGatewayService/jetGateway/queryQuateStatus", map, Object.class);
                // 无法查询到报价，发送状态则更新为失败，可以回退重操作
                Map<String, Object> param = JsonUtils.object2Map(o);
                if (StringUtil.equals(param.get("errCode"), "D2C_9998")){
                    param.put("quoteId", ibuQuoteBond.getQuoteId());
                    param.put("exeType", DictCodeList.银行间上行操作类型_本方新增发送失败.getValue());
                }
                resultBaseDto = this.updateBondRequestQuoteInterfResp(JsonUtils.object2Map(o));
            }
        }
        
        return resultBaseDto;
    }
    
    /**
     * 超时刷新机制
     * 
     * @param quoteId 报价编号
     * @return 返回结果
     * @see
     * @since 1.0
     */
    @Transactional
    public ResultBaseDto timeOutQuoteRefreshRepo(String quoteId){
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        
        IbuQuoteCrep ibuQuoteCrep = ibuQuoteCrepMapper.selectByPrimaryKey(quoteId);
        if (ibuQuoteCrep != null){
            Map<String, Object> map = new HashMap<String, Object>();
            // 传入pQuoteId
            map.put("pQuoteId", ibuQuoteCrep.getpQuoteId());
            map.put("quoteId", ibuQuoteCrep.getQuoteId());
            map.put("quoteType", DictCodeList.报价系统区分类型_质押式回购对话报价.getValue());
            Object o = restTemplate.postForObject("http://jetGatewayService/jetGateway/queryQuateStatus", map, Object.class);
            // 无法查询到报价，发送状态则更新为失败，可以回退重操作
            Map<String, Object> param = JsonUtils.object2Map(o);
            if (StringUtil.equals(param.get("errCode"), "D2C_9998")){
                
            }
            resultBaseDto = this.updateRepoDialogueQuoteInterfResp(JsonUtils.object2Map(o));
        }
        
        return resultBaseDto;
    }

    /**
     * 更新对话报价的接口反馈信息
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    public ResultBaseDto updateBondDialogueQuoteInterfResp(Map<String, Object> param) {
        logger.info("【CFETS接口接收-现券对话报价】IbuComponentReceiveService.updateBondDialogueQuoteInterfResp异步接受交易中心数据：" + param.toString());
        
        ReceiveBondDialogueQuoteDto inputBean = new ReceiveBondDialogueQuoteDto();
        
//        inputBean.setStatus(MapUtils.getBoolean(param, "status", true));
//        inputBean.setErrCode(MapUtils.getString(param, "errCode"));
//        inputBean.setErrMsg(MapUtils.getString(param, "errMsg"));
        
//        // 银行间报价主信息
//        IbuQuoteBondDto ibuQuoteBondDto = JSON.parseObject(JSON.toJSONString(param), IbuQuoteBondDto.class);
//        // 银行间报价金额基对手信息
//        IbuQuoteBondDetDto ibuQuoteBondDetDto = JSON.parseObject(JSON.toJSONString(param), IbuQuoteBondDetDto.class);
        
        inputBean.setStatus(true);
        param.put("quoteType", DictCodeList.报价系统区分类型_现券对话报价.getValue());//对话报价
        IbuQuoteBondDto ibuQuoteBondDto = cfetsInterfReceiveService.getIbuQuoteBondDto(param);
        IbuQuoteBondDetDto ibuQuoteBondDetDto = cfetsInterfReceiveService.getIbuQuoteBondDetDto(param, ibuQuoteBondDto);
        if(ibuQuoteBondDto==null) {//接口失败反馈
        	return new ResultBaseDto(true);
        }
        // 报价编号（内部代码）Modified by JIAOXUJIN on 2020-06-05
        User userInfo = null;
        if (StringUtil.equals(ibuQuoteBondDto.getExeType(),
            DictCodeList.银行间上行操作类型_对手方报价新增.getValue())) {
            ibuQuoteBondDto.setQuoteId(this.genBusId("IBU_001"));
            userInfo = new User();
            userInfo.setUserName(ibuQuoteBondDto.getCreateUser());
            userInfo.setDeptCode("000000");
        }
        // 根据市场代码查询金融产品代码
        if (StringUtil.isNotEmpty(ibuQuoteBondDto.getFinprodMarketId())) {
            String finprodId = cfetsQuotedCommonService.queryFinprodIdByFinprodMarketId(ibuQuoteBondDto.getFinprodMarketId());
            if (StringUtil.isNotEmpty(finprodId)){
                ibuQuoteBondDto.setFinprodId(finprodId);
            }
            else{
                ibuQuoteBondDto.setFinprodId(ibuQuoteBondDto.getFinprodMarketId());
            }
            // Added by JIAOXUJIN on 2020-06-03
            ibuQuoteBondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_债券.getValue());
            ibuQuoteBondDto.setBranch(0L);
        }
        // add by zhangliang 20200701 返回错误时备注不需要记录jet返回的信息
        ibuQuoteBondDto.setRemark(null);

//        // add by 2020-07-08 接收网关的券面总额需要转成份额
//        ibuQuoteBondDto.setShareAmt(ArithmeticUtil.divide(ibuQuoteBondDto.getShareAmt(), new BigDecimal(100), 4));
        
        // add by 2020-07-13 本方交易员特殊处理
        if (StringUtil.equals(ibuQuoteBondDto.getOuTraderId(), "UNASSIGNED")) {
            ibuQuoteBondDto.setOuTraderId(ibuQuoteBondDto.getCreateUser());
        }
        // add by 2020-07-17 cfetsQuoteId特殊处理
        if (!StringUtil.isNotEmpty(ibuQuoteBondDto.getCfetsQuoteId())) {
        	ibuQuoteBondDto.setCfetsQuoteId(ibuQuoteBondDetDto.getCfetsReqId());
        }
        
        ibuQuoteBondDto.setpQuoteId(ibuQuoteBondDto.getQuoteId());
        
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        
        if (StringUtil.equals(ibuQuoteBondDto.getExeType(),
            DictCodeList.银行间上行操作类型_对手方报价新增.getValue())) {
            ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto.getQuoteId());
        }
        // 收益率需要去百分化处理
        // delete by etliu 20201225 start
//        ibuQuoteBondDetDto.setYield(this.yieldHandle(ibuQuoteBondDetDto.getYield(), true));
//        ibuQuoteBondDetDto.setExerYield(this.yieldHandle(ibuQuoteBondDetDto.getExerYield(), true));
        // delete by etliu 20201225 end
//        // add by 2020-07-08 接收网关的券面总额需要转成份额
//        ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.divide(ibuQuoteBondDetDto.getShareAmt(), new BigDecimal(100), 4));
        inputBean.setIbuQuoteBondDetDto(ibuQuoteBondDetDto);
        if(null!=ibuQuoteBondDto&&StringUtil.isNotEmpty(ibuQuoteBondDto.getQuoteId())){
            //查找关系表
            String quoteId = ibuQuoteBondDto.getQuoteId();
            Map<String,Object> map = new HashMap<>();
            map.put("quoteId",quoteId);
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPrice001Mapper.selectEtrReByQuoteId(map);
            if(!StringUtil.isEmptyList(ptlAllocations)){
                List<IbuQuoteEtrReDto> ibuQuoteEtrRes = new ArrayList<>();
                //报文返回总金额
                BigDecimal  shareAmtTotalBW = null!=ibuQuoteBondDto.getShareAmt()?ibuQuoteBondDto.getShareAmt():BigDecimal.ZERO;
                BigDecimal  fpriceAmtTotalBW = null!=ibuQuoteBondDto.getFpriceAmt()?ibuQuoteBondDto.getFpriceAmt():BigDecimal.ZERO;
                BigDecimal 交易份额尾差= shareAmtTotalBW; BigDecimal 全价总额尾差= fpriceAmtTotalBW;
                //按比例重新计算交易金额  尾差交给最大金额
                //最大交易金额
                PtlAllocationDto ptlAllocationDtoMax = ptlAllocations.stream().max(Comparator.comparing(PtlAllocationDto::getShareAmt)).get();
                //组合分配列表总金额
                BigDecimal shareAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getShareAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal fpriceAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                for(PtlAllocationDto ptlAllocationDto : ptlAllocations){
                    BigDecimal shareAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmtTotalBW,ptlAllocationDto.getShareAmt()),shareAmtTotalFP,2,RoundingMode.HALF_DOWN);
                    BigDecimal fpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(fpriceAmtTotalBW,ptlAllocationDto.getFpriceAmt()),fpriceAmtTotalFP,2,RoundingMode.HALF_DOWN);
                    ptlAllocationDto.setShareAmt(shareAmt);
                    ptlAllocationDto.setFpriceAmt(fpriceAmt);
                    交易份额尾差 = ArithmeticUtil.subtract(交易份额尾差,shareAmt);
                    全价总额尾差 = ArithmeticUtil.subtract(全价总额尾差,fpriceAmt);
                }
                //尾差处理  尾差放在指令的交易金额最大的记录
                for(PtlAllocationDto item : ptlAllocations){
                    IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
                    BigDecimal total = ArithmeticUtil.doAdd(item.getOurShareAmt(),item.getLendinShareAmt());
                    if(交易份额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                        item.setShareAmt(ArithmeticUtil.doAdd(item.getShareAmt(),交易份额尾差));
                    }
                    if(全价总额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                        item.setFpriceAmt(ArithmeticUtil.doAdd(item.getFpriceAmt(),全价总额尾差));
                    }
                    ibuQuoteEtrReDto.setQuoteId(quoteId);
                    ibuQuoteEtrReDto.setVersion(ibuQuoteBondDto.getVersion());// 版本号
                    ibuQuoteEtrReDto.setEntrustId(item.getEntrustId());
                    ibuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
                    ibuQuoteEtrReDto.setShareAmt(item.getShareAmt());
                    ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                    ibuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                    ibuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                    ibuQuoteEtrReDto.setInvAim(item.getInvAim());
                    //按比例处理融入券和本方券
                    if(null!=total&&total.compareTo(BigDecimal.ZERO)!=0){
                        //万元保留一位小数处理
                        item.setOurShareAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getOurShareAmt(),item.getShareAmt()),ArithmeticUtil.multiply(total,new BigDecimal(100)),1,RoundingMode.HALF_DOWN),new BigDecimal(100)));
                        item.setLendinShareAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getLendinShareAmt(),item.getShareAmt()),ArithmeticUtil.multiply(total,new BigDecimal(100)),1,RoundingMode.HALF_DOWN),new BigDecimal(100)));
                        BigDecimal 剩余券面 = ArithmeticUtil.subtract(item.getShareAmt(),ArithmeticUtil.doAdd(item.getOurShareAmt(),item.getLendinShareAmt()));
                        item.setOurShareAmt(ArithmeticUtil.doAdd(item.getOurShareAmt(),剩余券面));
                        ibuQuoteEtrReDto.setOurShareAmt(item.getOurShareAmt());
                        ibuQuoteEtrReDto.setLendinShareAmt(item.getLendinShareAmt());
                    }
                    ibuQuoteEtrRes.add(ibuQuoteEtrReDto);
                }
                inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
            }
        }
        QuoteResultDto quoteResultDto = ibuMainService.updateBondDialogueQuoteInterfResp(inputBean,
            userInfo);
        if (quoteResultDto.isStatus()) {
            // 6、更新委托完成度
            cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDto);
            // 7、MQ发布消息前台订阅通过ws控制表格刷新
            // 对手方对话报价新增(我方作为回复方)，需要制定交易员推送
            cfetsQuotedCommonService.publishMQCommonHandle(quoteResultDto, true);
            //头寸处理
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                quoteResultDto.setIsExistNew(DictCodeList.是否_是.getValue());
                updateBondDialoguePosition(quoteResultDto);
            }
            updateBondDialoguePositionRule(quoteResultDto);
            // 成交后风险处理
            //ibuComponentRiskService.excuteRiskHandle(quoteResultDto);
        }

        return quoteResultDto;
    }
    
    /**
     * 更新请求报价的接口反馈信息
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    public ResultBaseDto updateBondRequestQuoteInterfResp(Map<String, Object> param) {
        ResultBaseDto resultBaseDto = new ResultBaseDto(true);
        
        logger.info("【CFETS接口接收-现券请求报价】IbuComponentReceiveService.updateBondRequestQuoteInterfResp异步接受交易中心数据：" + param.toString());
        ReceiveBondRequestQuoteDto inputBean = new ReceiveBondRequestQuoteDto();

//        inputBean.setStatus(MapUtils.getBoolean(param, "status", true));
//        inputBean.setErrCode(MapUtils.getString(param, "errCode"));
//        inputBean.setErrMsg(MapUtils.getString(param, "errMsg"));
//        // 银行间报价主信息 Modified by JIAOXUJIN on 2020-06-05
//        IbuQuoteBondDto ibuQuoteBondDto = JSON.parseObject(JSON.toJSONString(param),
//            IbuQuoteBondDto.class);
        
        inputBean.setStatus(true);
        param.put("quoteType", DictCodeList.报价系统区分类型_现券请求报价.getValue());//请求报价
        IbuQuoteBondDto ibuQuoteBondDto = cfetsInterfReceiveService.getIbuQuoteBondDtoForReq(param);
        
        if(ibuQuoteBondDto==null) {//接口失败反馈
        	return new ResultBaseDto(true);
        }

        // 报价编号（内部代码）Modified by JIAOXUJIN on 2020-06-05
        if (StringUtil.equals(ibuQuoteBondDto.getExeType(),
            DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue())) {
            ibuQuoteBondDto.setQuoteId(this.genBusId("IBU_002"));
        }

        // 根据市场代码查询金融产品代码
        if (StringUtil.isNotEmpty(ibuQuoteBondDto.getFinprodMarketId())) {
            String finprodId = cfetsQuotedCommonService.queryFinprodIdByFinprodMarketId(
                ibuQuoteBondDto.getFinprodMarketId());
            if (StringUtil.isNotEmpty(finprodId)){
                ibuQuoteBondDto.setFinprodId(finprodId);
            }
            else{
                ibuQuoteBondDto.setFinprodId(ibuQuoteBondDto.getFinprodMarketId());
            }
            // Added by JIAOXUJIN on 2020-06-05
            ibuQuoteBondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_债券.getValue());
            ibuQuoteBondDto.setBranch(0L);
        }
        // add by zhangliang 20200701 返回错误时备注不需要记录jet返回的信息
        ibuQuoteBondDto.setRemark(null);

//        // add by 2020-07-08 接收网关的券面总额需要转成份额
//        ibuQuoteBondDto.setShareAmt(ArithmeticUtil.divide(ibuQuoteBondDto.getShareAmt(), new BigDecimal(100), 4));
        
        // add by 2020-07-13 本方交易员特殊处理
        if (StringUtil.equals(ibuQuoteBondDto.getOuTraderId(), "UNASSIGNED")) {
            ibuQuoteBondDto.setOuTraderId(ibuQuoteBondDto.getCreateUser());
        }
        
        // 银行间报价金额基对手信息 Modified by JIAOXUJIN on 2020-06-05
        Object detListObj = param.get("detList");
        if (detListObj != null) {
            List<IbuQuoteBondDetDto> quoteBondDetList = JSON.parseObject(
                JSON.toJSONString(detListObj), new TypeReference<List<IbuQuoteBondDetDto>>() {
                });

            if (CollectionUtils.isNotEmpty(quoteBondDetList)) {
                for (IbuQuoteBondDetDto ibuQuoteBondDetDto : quoteBondDetList) {
                    if (StringUtil.equals(ibuQuoteBondDto.getExeType(),
                        DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue())) {
                        ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto.getQuoteId());

                        // Added by JIAOXUJIN on 2020-06-05
                        /*ibuQuoteBondDto.setShareAmt(ibuQuoteBondDetDto.getShareAmt());
                        ibuQuoteBondDto.setFpriceAmt(ibuQuoteBondDetDto.getFpriceAmt());*/
                    }
                    // add by 2020-07-08 接收网关的券面总额需要转成份额
                    // 收益率需要去百分化处理
                 // delete by etliu 20201225 start
//                    ibuQuoteBondDetDto.setYield(this.yieldHandle(ibuQuoteBondDetDto.getYield(), true));
//                    ibuQuoteBondDetDto.setExerYield(this.yieldHandle(ibuQuoteBondDetDto.getExerYield(), true));
                 // delete by etliu 20201225 end
//                    ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.divide(ibuQuoteBondDetDto.getShareAmt(), new BigDecimal(100), 4));
                }
            }

            // Modified by JIAOXUJIN on 2020-06-05
            inputBean.setIbuQuoteBondDetDtoList(quoteBondDetList);
        }
        else{
            if (StringUtil.equals(ibuQuoteBondDto.getExeType(), DictCodeList.银行间上行操作类型_对手方新增请求报价.getValue())) {
                resultBaseDto.setStatus(false);
                resultBaseDto.setErrMsg("报文格式不对，缺少detList，请检查！");
                return resultBaseDto;
            }
        }
        
        ibuQuoteBondDto.setpQuoteId(ibuQuoteBondDto.getQuoteId());

        // Modified by JIAOXUJIN on 2020-06-05
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        
        // add by etliu 20210316 银行间上下行多组合 start
        if(null!=ibuQuoteBondDto){
            //查找关系表
            String quoteId = ibuQuoteBondDto.getQuoteId();
            Map<String,Object> map = new HashMap<>();
            map.put("quoteId",quoteId);
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPrice001Mapper.selectEtrReByQuoteId(map);
            if(!StringUtil.isEmptyList(ptlAllocations)){
                List<IbuQuoteEtrReDto> ibuQuoteEtrRes = new ArrayList<>();
                //报文返回总金额
                BigDecimal  shareAmtTotalBW = null!=ibuQuoteBondDto.getShareAmt()?ibuQuoteBondDto.getShareAmt():BigDecimal.ZERO;
                BigDecimal  fpriceAmtTotalBW = null!=ibuQuoteBondDto.getFpriceAmt()?ibuQuoteBondDto.getFpriceAmt():BigDecimal.ZERO;
                BigDecimal 交易份额尾差= shareAmtTotalBW; BigDecimal 全价总额尾差= fpriceAmtTotalBW;
                //按比例重新计算交易金额  尾差交给最大金额
                //最大交易金额
                PtlAllocationDto ptlAllocationDtoMax = ptlAllocations.stream().max(Comparator.comparing(PtlAllocationDto::getShareAmt)).get();
                //组合分配列表总金额
                BigDecimal shareAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getShareAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
//                BigDecimal fpriceAmtTotalFP = ptlAllocations.stream().map(PtlAllocationDto::getFpriceAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                //获取单位全价
                BigDecimal fprice=null;
                if (StringUtil.equals(ibuQuoteBondDto.getExeType(), DictCodeList.银行间上行操作类型_本方请求回复成交成功.getValue())) {
                    List<IbuQuoteBondDetDto> quoteBondDetList = inputBean.getIbuQuoteBondDetDtoList();
                    IbuQuoteBondDto quoteBond = inputBean.getIbuQuoteBondDto();
                    String cfetsPartyId = cfetsInterfReceiveService.getCfetsPartyID(param, quoteBond.getOuPartyId());
                    for(IbuQuoteBondDetDto item:quoteBondDetList){
                        if(null!=quoteBond && StringUtil.equals(quoteBond.getVersion(),item.getVersion())
                                && StringUtil.equals(cfetsPartyId,item.getCpPartyId())){
                                fprice=item.getUnitFprice();
                        }
                    }
                }
                for(PtlAllocationDto ptlAllocationDto : ptlAllocations){
                    BigDecimal shareAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(shareAmtTotalBW,ptlAllocationDto.getShareAmt()),shareAmtTotalFP,2,RoundingMode.HALF_DOWN);
//                    BigDecimal fpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(fpriceAmtTotalBW,ptlAllocationDto.getFpriceAmt()),fpriceAmtTotalFP,2,RoundingMode.HALF_DOWN);
                    ptlAllocationDto.setShareAmt(shareAmt);
                    ptlAllocationDto.setFpriceAmt(shareAmt.multiply(fprice==null?new BigDecimal(100):fprice));
                    交易份额尾差 = ArithmeticUtil.subtract(交易份额尾差,shareAmt);
                    全价总额尾差 = ArithmeticUtil.subtract(全价总额尾差,shareAmt.multiply(fprice==null?new BigDecimal(100):fprice));
                }
                //尾差处理  尾差放在指令的交易金额最大的记录
                for(PtlAllocationDto item : ptlAllocations){
                    IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
                    BigDecimal total = ArithmeticUtil.doAdd(item.getOurShareAmt(),item.getLendinShareAmt());
                    if(交易份额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                        item.setShareAmt(ArithmeticUtil.doAdd(item.getShareAmt(),交易份额尾差));
                    }
                    if(全价总额尾差.compareTo(BigDecimal.ZERO)>0&&StringUtil.equals(item.getEntrustId(),ptlAllocationDtoMax.getEntrustId())){
                        item.setFpriceAmt(ArithmeticUtil.doAdd(item.getShareAmt().divide(fprice==null?new BigDecimal(100):fprice),全价总额尾差));
                    }
                    ibuQuoteEtrReDto.setQuoteId(quoteId);
                    ibuQuoteEtrReDto.setVersion(ibuQuoteBondDto.getVersion());// 版本号
                    ibuQuoteEtrReDto.setEntrustId(item.getEntrustId());
                    ibuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
                    ibuQuoteEtrReDto.setShareAmt(item.getShareAmt());
                    ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                    ibuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                    ibuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                    ibuQuoteEtrReDto.setInvAim(item.getInvAim());
                    //按比例处理融入券和本方券
                    if(null!=total&&total.compareTo(BigDecimal.ZERO)!=0){
                        //万元保留一位小数处理
                        item.setOurShareAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getOurShareAmt(),item.getShareAmt()),ArithmeticUtil.multiply(total,new BigDecimal(100)),1,RoundingMode.HALF_DOWN),new BigDecimal(100)));
                        item.setLendinShareAmt(ArithmeticUtil.multiply(ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getLendinShareAmt(),item.getShareAmt()),ArithmeticUtil.multiply(total,new BigDecimal(100)),1,RoundingMode.HALF_DOWN),new BigDecimal(100)));
                        BigDecimal 剩余券面 = ArithmeticUtil.subtract(item.getShareAmt(),ArithmeticUtil.doAdd(item.getOurShareAmt(),item.getLendinShareAmt()));
                        item.setOurShareAmt(ArithmeticUtil.doAdd(item.getOurShareAmt(),剩余券面));
                        ibuQuoteEtrReDto.setOurShareAmt(item.getOurShareAmt());
                        ibuQuoteEtrReDto.setLendinShareAmt(item.getLendinShareAmt());
                    }
                    ibuQuoteEtrRes.add(ibuQuoteEtrReDto);
                }
                inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
            }
        }
        // add by etliu 20210316 银行间上下行多组合 end

        QuoteResultDto quoteResultDto = ibuMainService.updateBondRequestQuoteInterfResp(inputBean,
            null);
        if (quoteResultDto.isStatus()) {
            // 6、更新委托完成度
            cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDto);
            // 7、MQ发布消息前台订阅通过ws控制表格刷新
            cfetsQuotedCommonService.publishMQCommonHandle(quoteResultDto, true);
            // 8、头寸处理
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                quoteResultDto.setIsExistNew(DictCodeList.是否_是.getValue());
                updateBondRequestPosition(quoteResultDto);
            }
            updateBondRequestPositionRule(quoteResultDto);
            // 成交后风险处理
            //ibuComponentRiskService.excuteRiskHandle(quoteResultDto);
        }

        return quoteResultDto;
    }
    
    /**
     * 更新质押式回购报价的接口反馈信息
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = { Exception.class })
    @DTXTransaction
    public ResultBaseDto updateRepoDialogueQuoteInterfResp(Map<String, Object> param) {
        logger.info("【CFETS接口接收-回购对话报价】IbuComponentReceiveService.updateRepoDialogueQuoteInterfResp异步接受交易中心数据：" + param.toString());
        
        ReceiveRepoDialogueQuoteDto inputBean = new ReceiveRepoDialogueQuoteDto();
        
//        inputBean.setStatus(MapUtils.getBoolean(param, "status", true));
//        inputBean.setErrCode(MapUtils.getString(param, "errCode"));
//        inputBean.setErrMsg(MapUtils.getString(param, "errMsg"));
        
        // 回购报价主信息
//        IbuQuoteCrepDto ibuQuoteCrepDto = JSON.parseObject(JSON.toJSONString(param),
//            IbuQuoteCrepDto.class);
        
        inputBean.setStatus(true);
        param.put("quoteType", DictCodeList.报价系统区分类型_质押式回购对话报价.getValue()); //质押式回购对话报价
        ReceiveRepoDialogueQuoteDto tempResult = cfetsInterfReceiveService.getIbuQuoteCrepDto(param);
        if(StringUtils.equals(tempResult.getIbuQuoteCrepDto().getSendStatus(), DictCodeList.银行间上行发送状态_失败.getValue())) {
            // 接收撤回新增报价失败通知 // 接收撤回交谈报价失败通知 // 接收报价拒绝失败反馈 // 接收报价撤回失败反馈 // 接收拒绝成交报价失败通知
            // 场景不处理头寸
            if (StringUtils.equals(tempResult.getIbuQuoteCrepDto().getTradeStatus(), DictCodeList.银行间上行报价状态_撤回中.getValue())
                    && StringUtils.equals(tempResult.getIbuQuoteCrepDto().getTradeStatus(), DictCodeList.银行间上行报价状态_交谈中.getValue())
                    && StringUtils.equals(tempResult.getIbuQuoteCrepDto().getTradeStatus(), DictCodeList.银行间上行报价状态_成交中.getValue())) {
                //接口失败反馈
                return new ResultBaseDto(true);
            }
            //接口失败反馈
            return new ResultBaseDto(true);
        }
        IbuQuoteCrepDto ibuQuoteCrepDto = tempResult.getIbuQuoteCrepDto();
        ibuQuoteCrepDto.setQuoteType(BussinessDictCodeList.报价方式_对话报价.getValue());
        
//        // 质押品信息
//        if (StringUtil.isNotEmpty(param.get("pledgeList"))) {
//            List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = JSONObject.parseObject(
//                JSON.toJSONString(param.get("pledgeList")),
//                new TypeReference<List<IbuQuoteCrepPledgeDto>>() {
//                });
//            if (!StringUtil.isEmptyList(ibuQuoteCrepPledgeDtoList)) {
//                ibuQuoteCrepPledgeDtoList.stream().forEach(x -> {
//                    if (StringUtil.equals(ibuQuoteCrepDto.getExeType(),
//                        DictCodeList.银行间上行操作类型_对手方报价新增.getValue())) {
//                        // 报价代码
//                        x.setQuoteId(ibuQuoteCrepDto.getQuoteId());
//                    }
//                    // 根据市场代码查询金融产品代码 Modified by JIAOXUJIN on 2020-06-02
//                    if (StringUtil.isNotEmpty(x.getFinprodMarketId())) {
//                        String finprodId = cfetsQuotedCommonService.queryFinprodIdByFinprodMarketId(
//                            x.getFinprodMarketId());
//                        if (StringUtil.isNotEmpty(finprodId)){
//                            x.setPledgeFinprodId(finprodId);
//                        }
//                        else{
//                            x.setPledgeFinprodId(x.getFinprodMarketId());
//                        }
//                        // Added by JIAOXUJIN on 2020-06-05
//                        x.setBranch(0L);
//                    }
////                    x.setPledgeRatio(this.yieldHandle(x.getPledgeRatio(), true));
////                    // add by 2020-07-08 接收网关的券面总额需要转成份额
////                    x.setPledgeNumber(ArithmeticUtil.divide(x.getPledgeNumber(), new BigDecimal(100), 2));
//                });
//            }
//            inputBean.setIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
//        }
        
        List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = tempResult.getIbuQuoteCrepPledgeDtoList();
        List<IbuQuoteEtrReDto> ibuQuoteEtrRes = tempResult.getIbuQuoteEtrReDtoList();
        List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = tempResult.getIbuQuoteEntCrepPledgeDtoList();

        // 报价编号（内部代码）Modified by JIAOXUJIN on 2020-06-05
        User userInfo = null;
        if (StringUtil.equals(ibuQuoteCrepDto.getExeType(),
            DictCodeList.银行间上行操作类型_对手方报价新增.getValue())) {
            ibuQuoteCrepDto.setQuoteId(this.genBusId("IBU_003"));
            userInfo = new User();
            userInfo.setUserName(ibuQuoteCrepDto.getCreateUser());
            userInfo.setDeptCode("000000");
            
            if (!StringUtil.isEmptyList(ibuQuoteCrepPledgeDtoList)) {
                for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList) {
                    // 报价代码
                    ibuQuoteCrepPledgeDto.setQuoteId(ibuQuoteCrepDto.getQuoteId());
                }
            }
            if (!StringUtil.isEmptyList(ibuQuoteEtrRes)) {
                inputBean.setIsDzh("Y");
                ibuQuoteEtrRes.stream().forEach(item -> item.setQuoteId(ibuQuoteCrepDto.getQuoteId()));
            }
            if (!StringUtil.isEmptyList(ibuQuoteEntCrepPledges)) {
                ibuQuoteEntCrepPledges.stream().forEach(item -> item.setQuoteId(ibuQuoteCrepDto.getQuoteId()));
            }
        }
        
        // delete by etliu 20210104 start
//        // 回购利率
//        ibuQuoteCrepDto.setRate(this.yieldHandle(ibuQuoteCrepDto.getRate(), true));
//        // 质押券的汇总 add by zhangliang 2020-07-20 由于网关推过来的数据质押券汇总是乘过折算率的，所以此处特殊处理根据质押明细汇总
//        if (!StringUtil.isEmptyList(inputBean.getIbuQuoteCrepPledgeDtoList())) {
//        	BigDecimal totleNumber = BigDecimal.ZERO;
//        	for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : inputBean.getIbuQuoteCrepPledgeDtoList()) {
//        		totleNumber = ArithmeticUtil.doAdd(totleNumber, ibuQuoteCrepPledgeDto.getPledgeNumber());
//        	}
//        	ibuQuoteCrepDto.setShareAmt(totleNumber);
//        }
//        else {
//        	// 交易份额，债券数量等
//        	ibuQuoteCrepDto.setShareAmt(ArithmeticUtil.divide(ibuQuoteCrepDto.getShareAmt(), new BigDecimal(100), 2));
//        }
        
//        // add by 2020-07-13 本方交易员特殊处理
//        if (StringUtil.equals(ibuQuoteCrepDto.getOuTraderId(), "UNASSIGNED")) {
//            ibuQuoteCrepDto.setOuTraderId(ibuQuoteCrepDto.getCreateUser());
//        }
        // delete by etliu 20210104 end
        
        ibuQuoteCrepDto.setpQuoteId(ibuQuoteCrepDto.getQuoteId());
        
        // add by zhangliang 20200701 返回错误时备注不需要记录jet返回的信息
        ibuQuoteCrepDto.setRemark(null);
        inputBean.setIbuQuoteCrepDto(ibuQuoteCrepDto);
        inputBean.setIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
        //多组合
        inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
        inputBean.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteEntCrepPledges);
        QuoteResultDto quoteResultDto = ibuMainService.updateRepoDialogueQuoteInterfResp(inputBean,
            userInfo);
        if (quoteResultDto.isStatus()) {
            // 6、更新委托完成度
            cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDto);
            // 7、MQ发布消息前台订阅通过ws控制表格刷新
            // 对手方对话报价新增(我方作为回复方)，需要制定交易员推送
            cfetsQuotedCommonService.publishMQCommonHandle(quoteResultDto, true);
            this.updateRepoRequestPositionRule(quoteResultDto);
            // 8、旧头寸处理
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                quoteResultDto.setIsExistNew(DictCodeList.是否_是.getValue());
                quoteResultDto.getRepoDialogueQuote().setIsDzh(DictCodeList.是否_是.getValue());
                this.updateRepoDialoguePosition(quoteResultDto);
            }
            // 成交后风险处理
           // ibuComponentRiskService.excuteRiskHandle(quoteResultDto);
        }
        
        return quoteResultDto;
    }
    
    /**
     * 收益率db登录转换
     * 
     * @param yield 收益率
     * @return 收益率
     * @see
     * @since 1.0
     */
    private BigDecimal yieldHandle(BigDecimal yield, boolean flag) {
        if (yield != null) {
            if (flag) {
                yield = ArithmeticUtil.divide(yield, new BigDecimal(100), 6);
            }
            else {
                yield = ArithmeticUtil.multiply(yield, new BigDecimal(100));
            }
        }
        return yield;
    }

	private boolean updateBondDialoguePosition(QuoteResultDto quoteResultDto) {
		if (null != quoteResultDto && null != quoteResultDto.getBondDialogueQuote()
				&& null != quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto()) {
		    IbuQuoteBondDto ibuQuoteBondDto = quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto();
		    //多组合的ibuQuoteBondDto.getPortfolioId()为空 但是要处理相关头寸  所以这个判断需要优化
		    if (StringUtil.isBlank(ibuQuoteBondDto.getPortfolioId())&&StringUtil.isEmptyList(quoteResultDto.getBondDialogueQuote().getIbuQuoteEtrReDtoList())) {
		        return true;
		    }
		    
			if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
				CfetsBondDealDialogueQuoteDto dto = new CfetsBondDealDialogueQuoteDto();
				BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
				dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
				dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
				dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
				dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
				dto.setFpriceAmt(ibuQuoteBondDto.getFpriceAmt());
				//券面总额【单位：万】
				dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)).divide(new BigDecimal(10000)));
				dto.setPs(ibuQuoteBondDto.getPs());
                if(StringUtil.isNotEmpty(quoteResultDto.getIsExistNew())){
                    dto.setIsExistNew(quoteResultDto.getIsExistNew());
                }
				cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(dto);
				return true;
			} else if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
					BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
					|| StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
							BussinessDictCodeList.银行间上行报价状态_已过期.getValue())) {
				cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(ibuQuoteBondDto.getQuoteId());
				return true;
			}
		}
		return true;
	}

    /**
     * 头寸引擎处理头寸
     * @param quoteResultDto
     * @return
     */
	public boolean  updateBondDialoguePositionRule(QuoteResultDto quoteResultDto){
        if (null != quoteResultDto && null != quoteResultDto.getBondDialogueQuote()
                && null != quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto()) {
            IbuQuoteBondDto ibuQuoteBondDto = quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto();
            if (StringUtil.isEmptyList(quoteResultDto.getBondDialogueQuote().getIbuQuoteEtrReDtoList())) {
                return true;
            }
            CfetsBondDealDialogueQuoteDto dto = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
            dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
            dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
            dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
            dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
            dto.setFpriceAmt(ibuQuoteBondDto.getFpriceAmt());
            //券面总额【单位：万】
            dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)).divide(new BigDecimal(10000)));
            dto.setPs(ibuQuoteBondDto.getPs());
            List<CfetsBondDealDialogueQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<>();
            quoteResultDto.getBondDialogueQuote().getIbuQuoteEtrReDtoList().forEach(item ->{
                CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,item);
                cfetsBondDealDialogueQuoteDto.setAvaNum(ArithmeticUtil.divide(cfetsBondDealDialogueQuoteDto.getAvaNum(),new BigDecimal(100),2));
                cfetsBondDealRequestQuoteDtoList.add(cfetsBondDealDialogueQuoteDto);
            });
            dto.setCfetsBondDealDialogueQuoteDtoList(cfetsBondDealRequestQuoteDtoList);
            if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
                PositionHttpRequest.send(cfetsQuotedCommonService.prepareDialoguePosition(dto, DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),null));
                return true;
            } else if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
                    || StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已过期.getValue())) {
                PositionHttpRequest.send(cfetsQuotedCommonService.prepareDialoguePosition(dto, DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_关闭.getValue(),DictCodeListExt3.系统动作_关闭.getValue(),null));
                return true;
            }
        }
        return true;
    }

	private boolean updateBondRequestPosition(QuoteResultDto quoteResultDto) {
		if (null != quoteResultDto && null != quoteResultDto.getBondRequestQuote()
				&& null != quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto()) {
			IbuQuoteBondDto ibuQuoteBondDto = quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto();
            if (StringUtil.isBlank(ibuQuoteBondDto.getPortfolioId())&&StringUtil.isEmptyList(quoteResultDto.getBondRequestQuote().getIbuQuoteEtrReDtoList())) {
                return true;
            }
			if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
//				CfetsBondDealDialogueQuoteDto dto = new CfetsBondDealDialogueQuoteDto();
//				BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
//				dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
//				dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
//				dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
//				dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
//                //券面总额【单位：万】
//                dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)).divide(new BigDecimal(10000)));
//				dto.setPs(ibuQuoteBondDto.getPs());
//				if(StringUtil.isNotEmpty(quoteResultDto.getIsExistNew())){
//                    dto.setIsExistNew(quoteResultDto.getIsExistNew());
//                }

                CfetsBondDealRequestQuoteDto dto = new CfetsBondDealRequestQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
                dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
                dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
                dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
                dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
                //券面总额【单位：万】
                dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)).divide(new BigDecimal(10000)));
                dto.setPs(ibuQuoteBondDto.getPs());
                if(StringUtil.isNotEmpty(quoteResultDto.getIsExistNew())){
                    dto.setIsExistNew(quoteResultDto.getIsExistNew());
                }

				cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(dto);
				return true;
			} else if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
					BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
					|| StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
							BussinessDictCodeList.银行间上行报价状态_已过期.getValue())) {
				cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(ibuQuoteBondDto.getQuoteId());
				return true;
			}
		}
		return true;
	}

    /**
     * 头寸引擎处理头寸
     * @param quoteResultDto
     * @return
     */
    private boolean updateBondRequestPositionRule(QuoteResultDto quoteResultDto) {
        if (null != quoteResultDto && null != quoteResultDto.getBondRequestQuote()
                && null != quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto()) {
            IbuQuoteBondDto ibuQuoteBondDto = quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto();
            if (StringUtil.isEmptyList(quoteResultDto.getBondRequestQuote().getIbuQuoteEtrReDtoList())) {
                return true;
            }
            CfetsBondDealRequestQuoteDto dto = new CfetsBondDealRequestQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
            dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
            dto.setPortfolioId(ibuQuoteBondDto.getPortfolioId());
            dto.setFinprodId(ibuQuoteBondDto.getFinprodId());
            dto.setTradeStatus(ibuQuoteBondDto.getTradeStatus());
            dto.setFpriceAmt(ibuQuoteBondDto.getFpriceAmt());
            //券面总额【单位：万】
            dto.setAvaNum(ibuQuoteBondDto.getShareAmt().multiply(new BigDecimal(100)).divide(new BigDecimal(10000)));
            dto.setPs(ibuQuoteBondDto.getPs());
            List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<>();
            quoteResultDto.getBondRequestQuote().getIbuQuoteEtrReDtoList().forEach(item ->{
                CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
                BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto,item);
                cfetsBondDealRequestQuoteDto.setAvaNum(ArithmeticUtil.divide(cfetsBondDealRequestQuoteDto.getAvaNum(),new BigDecimal(100),2));
                cfetsBondDealRequestQuoteDtoList.add(cfetsBondDealRequestQuoteDto);
            });
            dto.setCfetsBondDealRequestQuoteDtoList(cfetsBondDealRequestQuoteDtoList);
            if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
                PositionHttpRequest.send(cfetsQuotedCommonService.prepareRequestPosition(dto, DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),null));
                return true;
            } else if (StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
                    || StringUtil.equals(ibuQuoteBondDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已过期.getValue())) {
                PositionHttpRequest.send(cfetsQuotedCommonService.prepareRequestPosition(dto, DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),null));
                return true;
            }
        }
        return true;
    }

    /**
     * 头寸引擎处理头寸
     * @param quoteResultDto
     * @return
     */
    private boolean updateRepoRequestPositionRule(QuoteResultDto quoteResultDto) {
        if (null != quoteResultDto && null != quoteResultDto.getRepoDialogueQuote()) {
            List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = null;
            if (null != quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepPledgeDtoList()) {
                ibuQuoteCrepPledgeDtoList = quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepPledgeDtoList();
            }
            IbuQuoteCrepDto ibuQuoteCrepDto = quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto();
            CfetsRepoDealQuotedPrice001Dto dto = new CfetsRepoDealQuotedPrice001Dto();
            BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteCrepDto);
            dto.setQuoteId(ibuQuoteCrepDto.getQuoteId());
            dto.setPortfolioId(ibuQuoteCrepDto.getTradeStatus());
            dto.setTradeStatus(dto.getTradeStatus());
            dto.setPs(ibuQuoteCrepDto.getRepoPs());
            dto.setTradeDate(ibuQuoteCrepDto.getQuoteDate());
            dto.setIsMq("Y"); //mq推送标识
            dto.setIsDzh(quoteResultDto.getRepoDialogueQuote().getIsDzh());
            if (null != ibuQuoteCrepPledgeDtoList && ibuQuoteCrepPledgeDtoList.size() > 0) {
                List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
                if (StringUtil.equals(quoteResultDto.getRepoDialogueQuote().getIsDzh(), "Y")) {
                    //多组合 处理持仓 传入拆分后质押券
                    List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = quoteResultDto.getRepoDialogueQuote().getIbuQuoteEntCrepPledgeDtoList();
                    if (null != ibuQuoteEntCrepPledges && ibuQuoteEntCrepPledges.size() > 0) {
                        ibuQuoteEntCrepPledges.stream().forEach(item -> {
                            CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                            BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, item);
                            cfetsRepoDealQuotedPrice003Dto.setBondCode(item.getBondCode());
                            cfetsRepoDealQuotedPrice003Dto.setPledgeNumber(ArithmeticUtil.multiply(item.getPledgeNumber(), new BigDecimal(100)));
                            cfetsRepoDealQuotedPrice003Dto.setPledgePrin(ArithmeticUtil.multiply(item.getPledgeNumber(), new BigDecimal(100)));
                            cfetsRepoDealQuotedPrice003Dto.setFinprodId(item.getPledgeFinprodId());
                            pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                        });
                    }
                } else {
                    for (IbuQuoteCrepPledgeDto IbuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList) {
                        CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, IbuQuoteCrepPledgeDto);
                        cfetsRepoDealQuotedPrice003Dto.setBondCode(IbuQuoteCrepPledgeDto.getFinprodMarketId());
                        cfetsRepoDealQuotedPrice003Dto.setPledgePrin(IbuQuoteCrepPledgeDto.getPledgeAmt());
                        pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                    }
                }
                dto.setPledgeBondList(pledgeBondList);
            }
            List<IbuQuoteEtrReDto> ibuQuoteEtrRes = quoteResultDto.getRepoDialogueQuote().getIbuQuoteEtrReDtoList();
            List<PtlAllocationDto> ptlAllocationList = new ArrayList<>();
            if (null != ibuQuoteEtrRes && ibuQuoteEtrRes.size() > 0) {
                ibuQuoteEtrRes.stream().forEach(item -> {
                    PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                    BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto, item);
                    ptlAllocationList.add(ptlAllocationDto);
                });
            }
            dto.setPtlAllocationList(ptlAllocationList);
            if (StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
                cfetsQuotedCommonService.sendPositionMethod(dto, DictCodeListExt3.流程节点_审批生效.getValue(), DictCodeListExt3.系统动作_撤回.getValue(), DictCodeListExt3.系统动作_撤回.getValue(), null);
                return true;
            } else if (StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
                    || StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(),
                    BussinessDictCodeList.银行间上行报价状态_已过期.getValue())) {
                cfetsQuotedCommonService.sendPositionMethod(dto, DictCodeListExt3.流程节点_审批生效.getValue(), DictCodeListExt3.系统动作_撤回.getValue(), DictCodeListExt3.系统动作_撤回.getValue(), null);
                return true;
            }
        }
        return true;
    }

	private boolean updateRepoDialoguePosition(QuoteResultDto quoteResultDto) {
		if (null != quoteResultDto && null != quoteResultDto.getRepoDialogueQuote()) {
			List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = null;
			if (null != quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepPledgeDtoList()) {
				ibuQuoteCrepPledgeDtoList = quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepPledgeDtoList();
			}
			if (null != quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto()) {
				IbuQuoteCrepDto ibuQuoteCrepDto = quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto();
				if (StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(),
						BussinessDictCodeList.银行间上行报价状态_已撤回.getValue())) {
					CfetsRepoDealQuotedPrice001Dto dto = new CfetsRepoDealQuotedPrice001Dto();
					BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteCrepDto);
					dto.setQuoteId(ibuQuoteCrepDto.getQuoteId());
					dto.setPortfolioId(ibuQuoteCrepDto.getTradeStatus());
					dto.setTradeStatus(dto.getTradeStatus());
					dto.setPs(dto.getPs());
                    dto.setIsMq("Y"); //mq推送标识
                    dto.setIsDzh(quoteResultDto.getRepoDialogueQuote().getIsDzh());
                    dto.setIsExistNew(quoteResultDto.getIsExistNew());
					if (null != ibuQuoteCrepPledgeDtoList && ibuQuoteCrepPledgeDtoList.size() > 0) {
                        List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
					    if(StringUtil.equals(quoteResultDto.getRepoDialogueQuote().getIsDzh(),"Y")){
					        //多组合 处理持仓 传入拆分后质押券
                            List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = quoteResultDto.getRepoDialogueQuote().getIbuQuoteEntCrepPledgeDtoList();
                            if(null != ibuQuoteEntCrepPledges && ibuQuoteEntCrepPledges.size()>0){
                                ibuQuoteEntCrepPledges.stream().forEach(item -> {
                                    CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                                    BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, item);
                                    cfetsRepoDealQuotedPrice003Dto.setBondCode(item.getBondCode());
                                    cfetsRepoDealQuotedPrice003Dto.setPledgeNumber(ArithmeticUtil.multiply(item.getPledgeNumber(),new BigDecimal(100)));
                                    cfetsRepoDealQuotedPrice003Dto.setPledgePrin(ArithmeticUtil.multiply(item.getPledgeNumber(),new BigDecimal(100)));
                                    cfetsRepoDealQuotedPrice003Dto.setFinprodId(item.getPledgeFinprodId());
                                    pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                                });
                            }
                            List<IbuQuoteEtrReDto> ibuQuoteEtrRes = quoteResultDto.getRepoDialogueQuote().getIbuQuoteEtrReDtoList();
                            List<PtlAllocationDto> ptlAllocationList = new ArrayList<>();
                            if(null != ibuQuoteEtrRes && ibuQuoteEtrRes.size()>0){
                                ibuQuoteEtrRes.stream().forEach(item -> {
                                    PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                                    BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto,item);
                                    ptlAllocationList.add(ptlAllocationDto);
                                });
                            }
                            dto.setPtlAllocationList(ptlAllocationList);
                        }else{
                            for (IbuQuoteCrepPledgeDto IbuQuoteCrepPledgeDto : ibuQuoteCrepPledgeDtoList) {
                                CfetsRepoDealQuotedPrice003Dto cfetsRepoDealQuotedPrice003Dto = new CfetsRepoDealQuotedPrice003Dto();
                                BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPrice003Dto, IbuQuoteCrepPledgeDto);
                                cfetsRepoDealQuotedPrice003Dto.setBondCode(IbuQuoteCrepPledgeDto.getFinprodMarketId());
                                cfetsRepoDealQuotedPrice003Dto.setPledgePrin(IbuQuoteCrepPledgeDto.getPledgeAmt());
                                pledgeBondList.add(cfetsRepoDealQuotedPrice003Dto);
                            }
                        }
						dto.setPledgeBondList(pledgeBondList);
					}
					//update by zhangnian  20210122  start
                    //逆回购存在质押券为空的情况  故撤回时，需要处理头寸
                    cfetsRepoDealQuotedPrice001Service.doInsetOrUpdateForPtlPositionHold(dto);
                    //update by zhangnian  20210122  end
					return true;
				} else if (StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(),
						BussinessDictCodeList.银行间上行报价状态_已终止.getValue())
						|| StringUtil.equals(ibuQuoteCrepDto.getTradeStatus(),
								BussinessDictCodeList.银行间上行报价状态_已过期.getValue())) {
                    List<PtlSecPositionFlowDto> ptlSecPositionFlows = new ArrayList<>();
                    List<PtlSecPositionFlow> positionFlows = baseAssetCashPositionCommMapper.selectAllPositionByOffsetFlag(ibuQuoteCrepDto.getQuoteId(),
                            com.joyintech.tams.constant.DictCodeList.冲正标识_正常交易.getValue());
                    for (PtlSecPositionFlow ptlSecPositionFlow : positionFlows) {
                        if (StringUtil.equals(ptlSecPositionFlow.getHodingType(), com.joyintech.tams.constant.DictCodeList.持仓类型_质押.getValue())) {
                            PtlSecPositionFlowDto flowDto = new PtlSecPositionFlowDto();
                            BeanUtil.copyPropertiesIgnoreNull(flowDto, ptlSecPositionFlow);
                            flowDto.setAmount(flowDto.getAmount().negate());
                            flowDto.setOffsetFlag(com.joyintech.tams.constant.DictCodeList.冲正标识_冲销交易.getValue());
                            flowDto.setOffsetId(ptlSecPositionFlow.getTradeId());
                            ptlSecPositionFlows.add(flowDto);
                        }
                    }
                    cfetsRepoDealQuotedPrice001Service.doDeletePtlPositionHoldByQuoteId(ibuQuoteCrepDto.getQuoteId());
                    for (PtlSecPositionFlowDto ptlSecPositionFlow : ptlSecPositionFlows) {
                        basePtlSecPositionInfoService.inertSecPositionFlow(ptlSecPositionFlow);
                    }
                    return true;
                }
			}
		}
		return true;
	}

}
