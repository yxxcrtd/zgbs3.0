/*
 * 文件名：IbuComponentCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.ibu.service;

import com.joyintech.background.component.common.pack.common.grid.Page;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.uplink.interbank.business.dto.*;
import com.joyintech.background.component.uplink.interbank.business.service.IbuMainService;
import com.joyintech.background.component.uplink.interbank.common.DictCodeList;
import com.joyintech.background.component.uplink.interbank.common.IbuDictCode;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.*;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CounterInfoDto;
import com.joyintech.service.bankuplink.cfets.service.CfetsInterfService;
import com.joyintech.service.bankuplink.common.service.CfetsQuotedCommonService;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice001Dto;
import com.joyintech.service.bankuplink.repo.dto.CfetsRepoDealQuotedPrice003Dto;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.MstCfetsCustomerInfoMapper;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonService;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoDealQuotedPriceDto;
import com.joyintech.tams.invest.common.dto.cstp.CfetsRepoPledgeBondDto;
import com.joyintech.tams.invest.common.dto.cstp.IbuQuoteEntCrepPledgeCommonDto;
import com.joyintech.tams.invest.common.util.QuotaPageModelUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 银行间上行组件调用入口
 * 
 * @author zhangliang
 * @version 1.0 2020年5月14日
 * @see IbuComponentCommonService
 * @since 1.0
 */
@Service
public class IbuComponentCommonService extends BaseService {

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(IbuComponentCommonService.class);

    /**
     * {@linkplain}
     */
    @Autowired
    private IbuMainService ibuMainService;

    /**
     * {@linkplain}
     */
    @Autowired
    private BaseGetBussinessId bussinessIdGetter;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * {@linkplain}
     */
    @Autowired
    private CfetsQuotedCommonService cfetsQuotedCommonService;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductMapper finProductMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private MstCfetsCustomerInfoMapper mstCfetsCustomerInfoMapper;
    
    /**
     * CfetsInterfService
     */
    @Autowired
    private CfetsInterfService cfetsInterfService;
    
    /**
     * IbuComponentRiskService
     */
    @Autowired
    private IbuComponentRiskService ibuComponentRiskService;

    @Autowired
    private CstpCommonService cstpCommonService;

    /**
     * 构造组件需要的用户信息
     * 
     * @param baseEntity 业务类
     * @return 用户信息
     * @see
     * @since 1.0
     */
    private User genUserInfo(BaseEntity baseEntity) {
        User userInfo = new User();
        userInfo.setUserName(baseEntity.getCreateUser());
        userInfo.setDeptCode(baseEntity.getCreateDept());
        return userInfo;
    }

    /**
     * 判断组件返回结果
     * 
     * @param quoteResultDto 组件返回结果
     * @see
     * @since 1.0
     */
    @Transactional
    private void checkHandleResult(QuoteResultDto quoteResultDto) {
        if (!quoteResultDto.isStatus()) {
            logger.info("调用银行间上行组件接口错误码：" + quoteResultDto.getErrCode());
            logger.info("调用银行间上行组件接口错误信息描述：" + quoteResultDto.getErrMsg());
            logger.debug("调用银行间上行组件接口错误码：" + quoteResultDto.getErrCode());
            logger.debug("调用银行间上行组件接口错误信息描述：" + quoteResultDto.getErrMsg());
            throw new BusinessException("MSG:调用银行间上行组件接口异常." + quoteResultDto.getErrMsg());
        }
    }

//    /**
//     * 调用接口发送jet
//     * 
//     * @param quoteResultDto 报价对象
//     * @param quoteId 报价代码
//     * @param user 用户信息
//     * @see
//     * @since 1.0
//     */
//    private GateWayReturnMsgDto sendInterfHandle(QuoteResultDto quoteResultDto) {
//        // 1、根据返回结果转成map
//        Map<String, Object> sendData = new HashMap<>();
//        if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_现券对话报价.getValue())) {
//            BondDialogueQuoteDto bondDialogueQuoteDto = quoteResultDto.getBondDialogueQuote();
//            // 根据金融产品代码查询市场代码
//            if (bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId() != null) {
//                FinProduct finProduct = finProductMapper.selectByPrimaryKey(
//                    bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId());
//                if (finProduct != null) {
//                    bondDialogueQuoteDto.getIbuQuoteBondDto().setFinprodMarketId(
//                        finProduct.getFinprodMarketId());
//                }
//            }
//            sendData = JsonUtils.object2Map(bondDialogueQuoteDto.getIbuQuoteBondDto());
//            sendData.putAll(JsonUtils.object2Map(bondDialogueQuoteDto.getIbuQuoteBondDetDto()));
//        }
//        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_现券请求报价.getValue())) {
//            BondRequestQuoteDto bondRequestQuoteDto = quoteResultDto.getBondRequestQuote();
//            // 根据金融产品代码查询市场代码
//            if (bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId() != null) {
//                FinProduct finProduct = finProductMapper.selectByPrimaryKey(
//                    bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId());
//                if (finProduct != null) {
//                    bondRequestQuoteDto.getIbuQuoteBondDto().setFinprodMarketId(
//                        finProduct.getFinprodMarketId());
//                }
//            }
//            sendData = JsonUtils.object2Map(bondRequestQuoteDto.getIbuQuoteBondDto());
//            sendData.put("detList",
//                JsonUtils.object2Map(bondRequestQuoteDto).get("ibuQuoteBondDetDtoList"));
//        }
//        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_质押式回购对话报价.getValue())) {
//            RepoDialogueQuoteDto repoDialogueQuoteDto = quoteResultDto.getRepoDialogueQuote();
//            // 根据金融产品代码查询市场代码
//            if (!StringUtil.isEmptyList(repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList())) {
//                for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList()) {
//                    if (ibuQuoteCrepPledgeDto.getFinprodId() != null) {
//                        FinProduct finProduct = finProductMapper.selectByPrimaryKey(
//                            ibuQuoteCrepPledgeDto.getFinprodId());
//                        if (finProduct != null) {
//                            ibuQuoteCrepPledgeDto.setFinprodMarketId(
//                                finProduct.getFinprodMarketId());
//                        }
//                    }
//                }
//            }
//            sendData = JsonUtils.object2Map(repoDialogueQuoteDto.getIbuQuoteCrepDto());
//            sendData.put("pledgeList",
//                JsonUtils.object2Map(repoDialogueQuoteDto).get("ibuQuoteCrepPledgeDtoList"));
//        }
//        logger.info("发送网关数据：" + sendData.toString());
//
//        // 3、等待网关返回的结果转成map给微服务
//        GateWayReturnMsgDto gateWayReturnMsgDto = new GateWayReturnMsgDto();
//        gateWayReturnMsgDto.setStatus(true);
//        gateWayReturnMsgDto.setpQuoteId(IDGenerator.generate());
//        // Modified by JIAOXUJIN on 2020-06-03，减去2020-05-01 00:00:00
//        /*
//         * gateWayReturnMsgDto.setVersion( (Calendar.getInstance().getTimeInMillis() -
//         * 1588262400000L) / 1000L);
//         */
//        if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_现券对话报价.getValue())) {
//            gateWayReturnMsgDto.setQuoteId(
//                quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto().getQuoteId());
//            GateWayReqMsgDTO gateWayReqMsgDTO = new GateWayReqMsgDTO();
//            gateWayReqMsgDTO.setDetId(
//                quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDetDto().getDetId());
//            // Commented by JIAOXUJIN on 2020-06-12
//            // gateWayReqMsgDTO.setReqId(IDGenerator.generate());
//            gateWayReturnMsgDto.setReqMsgDto(Arrays.asList(gateWayReqMsgDTO));
//        }
//        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_现券请求报价.getValue())) {
//            gateWayReturnMsgDto.setQuoteId(
//                quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto().getQuoteId());
//            List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList = quoteResultDto.getBondRequestQuote().getIbuQuoteBondDetDtoList();
//            if (!StringUtil.isEmptyList(ibuQuoteBondDetDtoList)) {
//                List<GateWayReqMsgDTO> gateWayReqMsgDTOList = new ArrayList<>();
//                for (IbuQuoteBondDetDto ibuQuoteBondDetDto : ibuQuoteBondDetDtoList) {
//                    GateWayReqMsgDTO gateWayReqMsgDTO = new GateWayReqMsgDTO();
//                    gateWayReqMsgDTO.setDetId(ibuQuoteBondDetDto.getDetId());
//                    // Commented by JIAOXUJIN on 2020-06-12
//                    // gateWayReqMsgDTO.setReqId(IDGenerator.generate());
//                    gateWayReqMsgDTOList.add(gateWayReqMsgDTO);
//                }
//                gateWayReturnMsgDto.setReqMsgDto(gateWayReqMsgDTOList);
//            }
//        }
//        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_质押式回购对话报价.getValue())) {
//            gateWayReturnMsgDto.setQuoteId(
//                quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto().getQuoteId());
//            GateWayReqMsgDTO gateWayReqMsgDTO = new GateWayReqMsgDTO();
//            // Commented by JIAOXUJIN on 2020-06-12
//            // gateWayReqMsgDTO.setReqId(IDGenerator.generate());
//            gateWayReturnMsgDto.setReqMsgDto(Arrays.asList(gateWayReqMsgDTO));
//        }
//
//        return gateWayReturnMsgDto;
//    }
//
//    /**
//     * 发送网关参数特殊处理部分
//     * 
//     * @param sendData 发送网关参数
//     * @return 发送网关参数
//     * @see
//     * @since 1.0
//     */
//    private Map<String, Object> sendDataConvert(Map<String, Object> sendData) {
//        // 现场反馈发送网关信息需要branchId（本方机构对应的六位机构码），此处特殊处理，组件预留字段暂时不动
//        if (StringUtil.isNotEmpty(sendData.get("ouPartyId"))) {
//            MstCfetsCustomerInfo mstCfetsCustomerInfo = mstCfetsCustomerInfoMapper.selectByPrimaryKey(
//                sendData.get("ouPartyId").toString());
//            if (mstCfetsCustomerInfo != null) {
//                sendData.put("branchId", mstCfetsCustomerInfo.getPartyCustomerId());
//            }
//        }
//
//        return sendData;
//    }
//
//    /**
//     * 调用接口发送jet
//     * 
//     * @param quoteResultDto 报价对象
//     * @param quoteId 报价代码
//     * @param user 用户信息
//     * @see
//     * @since 1.0
//     */
//    @Transactional
//    private GateWayReturnMsgDto sendInterfHandlePingan(QuoteResultDto quoteResultDto) {
//        if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_现券对话报价.getValue())) {
//            BondDialogueQuoteDto bondDialogueQuoteDto = quoteResultDto.getBondDialogueQuote();
//            // 根据金融产品代码查询市场代码
//            if (bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId() != null) {
//                FinProduct finProduct = finProductMapper.selectByPrimaryKey(
//                    bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId());
//                if (finProduct != null) {
//                    bondDialogueQuoteDto.getIbuQuoteBondDto().setFinprodMarketId(
//                        finProduct.getFinprodMarketId());
//                }
//            }
//            // 发送给网关的数据本方交易员要转成UM号，目前jet那边只能UM号
//            if (bondDialogueQuoteDto.getIbuQuoteBondDto().getOuTraderId() != null) {
//            	Map<String, Object> param = new HashMap<String, Object>();
//            	param.put("ibuTrader", bondDialogueQuoteDto.getIbuQuoteBondDto().getOuTraderId());
//            	List<SysUser> sysUserList = cfetsQuotedCommonService.queryLoginTraderId(param);
//            	if (!StringUtil.isEmptyList(sysUserList)) {
//            		bondDialogueQuoteDto.getIbuQuoteBondDto().setOuTraderId(sysUserList.get(0).getUserName());
//            	}
//            }
//            
//            // 发送给网关的收益率需要百分化处理
//            if (bondDialogueQuoteDto.getIbuQuoteBondDetDto() != null) {
//                // 现券到期收益率
//                bondDialogueQuoteDto.getIbuQuoteBondDetDto().setYield(this.yieldHandle(
//                    bondDialogueQuoteDto.getIbuQuoteBondDetDto().getYield(), false));
//                // 现券行权收益率
//                bondDialogueQuoteDto.getIbuQuoteBondDetDto().setExerYield(this.yieldHandle(
//                    bondDialogueQuoteDto.getIbuQuoteBondDetDto().getExerYield(), false));
//                // add by 2020-07-08 发送给网关的份额需要转成券面总额
//                bondDialogueQuoteDto.getIbuQuoteBondDetDto().setShareAmt(ArithmeticUtil.multiply(bondDialogueQuoteDto.getIbuQuoteBondDetDto().getShareAmt(), new BigDecimal(100)));
//            }
//            Map<String, Object> sendData = JsonUtils.object2Map(
//                bondDialogueQuoteDto.getIbuQuoteBondDto());
//            sendData.putAll(JsonUtils.object2Map(bondDialogueQuoteDto.getIbuQuoteBondDetDto()));
//            logger.info("发送网关数据：" + this.sendDataConvert(sendData));
//            if (StringUtil.equals(bondDialogueQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
//                DictCodeList.银行间上行本方角色_询价方.getValue())) {
//                return restTemplate.postForObject(
//                    "http://jetGatewayService/jetGateway/sendRftBondOutRequest", sendData,
//                    GateWayReturnMsgDto.class);
//            }
//            else if (StringUtil.equals(
//                bondDialogueQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
//                DictCodeList.银行间上行本方角色_回复方.getValue())) {
//                return restTemplate.postForObject(
//                    "http://jetGatewayService/jetGateway/sendRftBondInRequest", sendData,
//                    GateWayReturnMsgDto.class);
//            }
//        }
//        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_现券请求报价.getValue())) {
//            BondRequestQuoteDto bondRequestQuoteDto = quoteResultDto.getBondRequestQuote();
//            // 根据金融产品代码查询市场代码
//            if (bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId() != null) {
//                FinProduct finProduct = finProductMapper.selectByPrimaryKey(
//                    bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId());
//                if (finProduct != null) {
//                    bondRequestQuoteDto.getIbuQuoteBondDto().setFinprodMarketId(
//                        finProduct.getFinprodMarketId());
//                }
//            }
//            // 发送给网关的数据本方交易员要转成UM号，目前jet那边只能UM号
//            if (bondRequestQuoteDto.getIbuQuoteBondDto().getOuTraderId() != null) {
//            	Map<String, Object> param = new HashMap<String, Object>();
//            	param.put("ibuTrader", bondRequestQuoteDto.getIbuQuoteBondDto().getOuTraderId());
//            	List<SysUser> sysUserList = cfetsQuotedCommonService.queryLoginTraderId(param);
//            	if (!StringUtil.isEmptyList(sysUserList)) {
//            		bondRequestQuoteDto.getIbuQuoteBondDto().setOuTraderId(sysUserList.get(0).getUserName());
//            	}
//            }
//            // add by 2020-07-08 发送给网关的份额需要转成券面总额
//            if (bondRequestQuoteDto.getIbuQuoteBondDto().getShareAmt() != null) {
//                bondRequestQuoteDto.getIbuQuoteBondDto().setShareAmt(ArithmeticUtil.multiply(bondRequestQuoteDto.getIbuQuoteBondDto().getShareAmt(), new BigDecimal(100)));
//            }
//            // 发送给网关的收益率需要百分化处理
//            if (!StringUtil.isEmptyList(bondRequestQuoteDto.getIbuQuoteBondDetDtoList())) {
//                for (IbuQuoteBondDetDto ibuQuoteBondDetDto : bondRequestQuoteDto.getIbuQuoteBondDetDtoList()) {
//                    // 现券到期收益率
//                    ibuQuoteBondDetDto.setYield(
//                        this.yieldHandle(ibuQuoteBondDetDto.getYield(), false));
//                    // 现券行权收益率
//                    ibuQuoteBondDetDto.setExerYield(
//                        this.yieldHandle(ibuQuoteBondDetDto.getExerYield(), false));
//                    // add by 2020-07-15 发送给网关的份额需要转成券面总额
//                    ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.multiply(ibuQuoteBondDetDto.getShareAmt(), new BigDecimal(100)));
//                }
//            }
//            // 有效时间不能大于当前时间+2小时
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            calendar.add(Calendar.HOUR, 2);
//            // 需要判断是否大于系统参数，如果大于系统参数，则置为系统参数
//            Date effTime = DateUtil.parse(DateUtil.format(new Date()) + " " + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00");
//            bondRequestQuoteDto.getIbuQuoteBondDto().setEffTime(calendar.getTime());
//            if (calendar.getTime().compareTo(effTime) > 0) {
//            	bondRequestQuoteDto.getIbuQuoteBondDto().setEffTime(effTime);
//            }
//            
//            Map<String, Object> sendData = JsonUtils.object2Map(
//                bondRequestQuoteDto.getIbuQuoteBondDto());
//            sendData.put("detList",
//                JsonUtils.object2Map(bondRequestQuoteDto).get("ibuQuoteBondDetDtoList"));
//            logger.info("发送网关数据：" + this.sendDataConvert(sendData));
//            if (StringUtil.equals(bondRequestQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
//                DictCodeList.银行间上行本方角色_做市方.getValue())) {
//                return restTemplate.postForObject(
//                    "http://jetGatewayService/jetGateway/sendRfqBondInRequest", sendData,
//                    GateWayReturnMsgDto.class);
//            }
//            else if (StringUtil.equals(bondRequestQuoteDto.getIbuQuoteBondDto().getQuoteRoleType(),
//                DictCodeList.银行间上行本方角色_非做市方.getValue())) {
//                return restTemplate.postForObject(
//                    "http://jetGatewayService/jetGateway/sendRfqBondOutRequest", sendData,
//                    GateWayReturnMsgDto.class);
//            }
//        }
//        else if (StringUtil.equals(quoteResultDto.getQuoteType(),
//            DictCodeList.报价系统区分类型_质押式回购对话报价.getValue())) {
//            RepoDialogueQuoteDto repoDialogueQuoteDto = quoteResultDto.getRepoDialogueQuote();
//            // 根据金融产品代码查询市场代码
//            if (!StringUtil.isEmptyList(repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList())) {
//                for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList()) {
//                    if (ibuQuoteCrepPledgeDto.getPledgeFinprodId() != null) {
//                        FinProduct finProduct = finProductMapper.selectByPrimaryKey(ibuQuoteCrepPledgeDto.getPledgeFinprodId());
//                        if (finProduct != null) {
//                            ibuQuoteCrepPledgeDto.setFinprodMarketId(finProduct.getFinprodMarketId());
//                        }
//                    }
//                    // 质押率
//                    ibuQuoteCrepPledgeDto.setPledgeRatio(this.yieldHandle(ibuQuoteCrepPledgeDto.getPledgeRatio(), false));
//                    // 质押数量
//                    ibuQuoteCrepPledgeDto.setPledgeNumber(ArithmeticUtil.multiply(ibuQuoteCrepPledgeDto.getPledgeNumber(), new BigDecimal(100)));
//                }
//            }
//            // 发送给网关的数据本方交易员要转成UM号，目前jet那边只能UM号
//            if (repoDialogueQuoteDto.getIbuQuoteCrepDto().getOuTraderId() != null) {
//            	Map<String, Object> param = new HashMap<String, Object>();
//            	param.put("ibuTrader", repoDialogueQuoteDto.getIbuQuoteCrepDto().getOuTraderId());
//            	List<SysUser> sysUserList = cfetsQuotedCommonService.queryLoginTraderId(param);
//            	if (!StringUtil.isEmptyList(sysUserList)) {
//            		repoDialogueQuoteDto.getIbuQuoteCrepDto().setOuTraderId(sysUserList.get(0).getUserName());
//            	}
//            }
//            // 发送给网关的收益率需要百分化处理
//            if (repoDialogueQuoteDto.getIbuQuoteCrepDto() != null) {
//                // 回购利率
//                repoDialogueQuoteDto.getIbuQuoteCrepDto().setRate(this.yieldHandle(repoDialogueQuoteDto.getIbuQuoteCrepDto().getRate(), false));
//                // 交易份额，债券数量等
//                repoDialogueQuoteDto.getIbuQuoteCrepDto().setShareAmt(ArithmeticUtil.multiply(repoDialogueQuoteDto.getIbuQuoteCrepDto().getShareAmt(), new BigDecimal(100)));
//            }
//            Map<String, Object> sendData = JsonUtils.object2Map(
//                repoDialogueQuoteDto.getIbuQuoteCrepDto());
//            sendData.put("pledgeList",
//                JsonUtils.object2Map(repoDialogueQuoteDto).get("ibuQuoteCrepPledgeDtoList"));
//            logger.info("发送网关数据：" + this.sendDataConvert(sendData));
//            if (StringUtil.equals(repoDialogueQuoteDto.getIbuQuoteCrepDto().getQuoteRoleType(),
//                DictCodeList.银行间上行本方角色_询价方.getValue())) {
//                return restTemplate.postForObject(
//                    "http://jetGatewayService/jetGateway/sendRftRepoOutRequest", sendData,
//                    GateWayReturnMsgDto.class);
//            }
//            else if (StringUtil.equals(
//                repoDialogueQuoteDto.getIbuQuoteCrepDto().getQuoteRoleType(),
//                DictCodeList.银行间上行本方角色_回复方.getValue())) {
//                return restTemplate.postForObject(
//                    "http://jetGatewayService/jetGateway/sendRftRepoInRequest", sendData,
//                    GateWayReturnMsgDto.class);
//            }
//        }
//
//        return null;
//    }

    /**
     * 组件调用共通处理
     * 
     * @param quoteResultDto 组件返回对象
     * @param user 用户信息
     * @param isSendGateWay 是否发送网关
     * @see
     * @since 1.0
     */
    @Transactional
    public boolean resCommonHandle(QuoteResultDto quoteResultDto, User user,
                                   boolean isSendGateWay) {
        long startTime = System.currentTimeMillis();    //获取开始时间
        // 是否调用上行接口
        String isUpLinkSend =  CacheUtil.getSysParam("IS_NEED_UPLINK");
        if(StringUtil.equals(DictCodeList.是否_否.getValue(),isUpLinkSend)){
            return false;
        }
        logger.info("调用上行接口:{}", JsonUtils.object2Json(quoteResultDto));

        // 1、判断组件返回结果
        // 需要平安现场判断是否支持微服务MSG：异常抛出，如果不支持此处要改成返回值给前台 判断 TODO
        this.checkHandleResult(quoteResultDto);
        String quoteId = StringUtils.EMPTY;
		if (null != quoteResultDto.getBondDialogueQuote()) {
			quoteId = quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto().getQuoteId();
		} else if (null != quoteResultDto.getBondRequestQuote()) {
			quoteId = quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto().getQuoteId();
		} else if (null != quoteResultDto.getRepoDialogueQuote()) {
			quoteId = quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto().getQuoteId();
		}
        // 判断是否发送网关
        if (isSendGateWay) {//改了呀，调试，正常不要非判断的
            // 2、调用接口发送jet（测试）
            // GateWayReturnMsgDto gateWayReturnMsgDto = this.sendInterfHandle(quoteResultDto);
            
            // modify by etliu 20201223 start
//            // 2、调用接口发送jet（平安）
//            GateWayReturnMsgDto gateWayReturnMsgDto = this.sendInterfHandlePingan(quoteResultDto);
            GateWayReturnMsgDto gateWayReturnMsgDto = cfetsInterfService.sendInterfHandle(quoteResultDto);
            // modify by etliu 20201223 end
            gateWayReturnMsgDto.setQuoteId(quoteId);
            gateWayReturnMsgDto.setpQuoteId(quoteId);
            logger.info("IbuComponentCommonService.resCommonHandle同步接受网关数据："
                        + JsonUtils.object2Json(gateWayReturnMsgDto));
            // 3、接收网关回复现券请求报价信息
            QuoteResultDto quoteResultDtoN = null;
            if (StringUtil.equals(quoteResultDto.getQuoteType(),
                DictCodeList.报价系统区分类型_现券对话报价.getValue())) {
                // 此处特殊处理一下pQuoteId，按照设计者的思想第一次发送网关时必须返回给我们pQuoteId，后面再有交互可以不发
                if (StringUtil.isEmpty(gateWayReturnMsgDto.getpQuoteId())) {
                    gateWayReturnMsgDto.setpQuoteId(
                        quoteResultDto.getBondDialogueQuote().getIbuQuoteBondDto().getpQuoteId());
                }
                quoteResultDtoN = ibuMainService.receBondDlgQuoteGateWayMsg(gateWayReturnMsgDto,
                    user);
            }
            else if (StringUtil.equals(quoteResultDto.getQuoteType(),
                DictCodeList.报价系统区分类型_现券请求报价.getValue())) {
                // 此处特殊处理一下pQuoteId，按照设计者的思想第一次发送网关时必须返回给我们pQuoteId，后面再有交互可以不发
                if (StringUtil.isEmpty(gateWayReturnMsgDto.getpQuoteId())) {
                    gateWayReturnMsgDto.setpQuoteId(
                        quoteResultDto.getBondRequestQuote().getIbuQuoteBondDto().getpQuoteId());
                }
                quoteResultDtoN = ibuMainService.receBondReqQuoteGateWayMsg(gateWayReturnMsgDto,
                    user);
            }
            else if (StringUtil.equals(quoteResultDto.getQuoteType(),
                DictCodeList.报价系统区分类型_质押式回购对话报价.getValue())) {
                if (StringUtil.isEmpty(gateWayReturnMsgDto.getpQuoteId())) {
                    gateWayReturnMsgDto.setpQuoteId(
                        quoteResultDto.getRepoDialogueQuote().getIbuQuoteCrepDto().getpQuoteId());
                }
                quoteResultDtoN = ibuMainService.receRepoDlgQuoteGateWayMsg(gateWayReturnMsgDto,
                    user);
            }

            // 4、判断组件返回结果
            // 需要平安现场判断是否支持微服务MSG：异常抛出，如果不支持此处要改成返回值给前台 判断 TODO
            if (quoteResultDtoN != null) {
                this.checkHandleResult(quoteResultDtoN);

                // 5、更新委托完成度
                cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDtoN);
            }
        }
        return true;
    }

    /**
     * 根据业务编号规则流水号生成业务代码
     * 
     * @param idRuleSeqno 业务编号规则流水号
     * @return 业务代码
     * @see
     * @since 1.0
     */
    public String genBusId(String idRuleSeqno) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        codeParameter.setSeqNo(idRuleSeqno);
        return bussinessIdGetter.buildBussinessCode(codeParameter);
    }
    
    /**
     * 对话报价构造组件调用对象
     * 
     * @param cfetsBondDealDialogueQuoteDto 前台对话报价对象
     * @return 组件调用对象
     * @see
     * @since 1.0
     */
    private BondDialogueQuoteDto genWflBondDialogueQuoteDto(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        BondDialogueQuoteDto inputBean = new BondDialogueQuoteDto();
        // 银行间报价主信息
        WflIbuQuoteBond2Dto ibuQuoteBondDto = new WflIbuQuoteBond2Dto();
        
        BondDialogueQuoteDto dbQuoteDataDto = null;
        ibuQuoteBondDto.setQuoteRoleType(BussinessDictCodeList.银行间上行本方角色_询价方.getValue());
        if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
            ibuQuoteBondDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
            
            // add by etliu 20210114 start
            // 若报价编号存在，代表是修改场景，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
            dbQuoteDataDto = ibuMainService.getBondDialogueQuote(cfetsBondDealDialogueQuoteDto.getQuoteId());
            if (dbQuoteDataDto != null && dbQuoteDataDto.getIbuQuoteBondDto() != null) {
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, dbQuoteDataDto.getIbuQuoteBondDto());
                //ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion() + 1);
                // modify by lijie2 20200119 start
                ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion());
                // modify by lijie2 20200119 end
            }
            else {
                ibuQuoteBondDto.setVersion(-1L);
            }
            // add by etliu 20210114 end
        }
        else {
            ibuQuoteBondDto.setQuoteId(this.genBusId("IBU_001"));
            ibuQuoteBondDto.setVersion(-1L);
        }
        ibuQuoteBondDto.setQuoteType(DictCodeList.报价系统区分类型_现券对话报价.getValue());
        ibuQuoteBondDto.setFinprodId(cfetsBondDealDialogueQuoteDto.getFinprodId());
        ibuQuoteBondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        ibuQuoteBondDto.setBranch(0L);
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealDialogueQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealDialogueQuoteDto.getEntrustId());
        ibuQuoteBondDto.setQuoteDate(iPlatformDate.getSysDate());
        ibuQuoteBondDto.setSettleDate(cfetsBondDealDialogueQuoteDto.getSettleDate());
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(1L);
        }
        ibuQuoteBondDto.setDeliveryType(cfetsBondDealDialogueQuoteDto.getSettleMethod());
        ibuQuoteBondDto.setSettType(cfetsBondDealDialogueQuoteDto.getSettleType());
        ibuQuoteBondDto.setSettCcy(cfetsBondDealDialogueQuoteDto.getSettleCcy());
        ibuQuoteBondDto.setPs(cfetsBondDealDialogueQuoteDto.getPs());
        ibuQuoteBondDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(100)));// 画面是万元单位 字段存份额 -->
                                                                             // *10000/100
        ibuQuoteBondDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        ibuQuoteBondDto.setEffTime(
            DateUtil.parse(iPlatformDate.getSysDateStr() + " "
                           + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00")); // 请求报价从系统参数获取
        ibuQuoteBondDto.setOuPartyId(cfetsBondDealDialogueQuoteDto.getMySide());
        ibuQuoteBondDto.setOuTraderId(cfetsBondDealDialogueQuoteDto.getMySideTrader());
        ibuQuoteBondDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        ibuQuoteBondDto.setWorkflowId(cfetsBondDealDialogueQuoteDto.getWorkflowId());
        ibuQuoteBondDto.setTradeStatus(cfetsBondDealDialogueQuoteDto.getTradeStatus());
        ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
		// 哎，撤销流程中要特殊处理，和交易撤销一样，审批前状态还是有效 审批后才变成撤销
		if (StringUtils.equals(cfetsBondDealDialogueQuoteDto.getOptType(), "revoke")) {
			ibuQuoteBondDto.setWorkflowStatus(DictCodeListExt1.有效状态_有效.getValue());
			ibuQuoteBondDto.setTradeStatus(DictCodeList.银行间上行报价状态_已成交.getValue());
            //已成交/空的记录点击撤销，提交流程后，状态变成已成交/成功，应该还是已成交/空
            ibuQuoteBondDto.setSendStatus(StringUtil.EMPTY);
		}
        ibuQuoteBondDto.setDealFeeAmt(cfetsBondDealDialogueQuoteDto.getDealFeeAmt());
        ibuQuoteBondDto.setSettFeeAmt(cfetsBondDealDialogueQuoteDto.getSettFeeAmt());
        ibuQuoteBondDto.setDipType(cfetsBondDealDialogueQuoteDto.getDistribution());
        ibuQuoteBondDto.setSecManageAcctId(cfetsBondDealDialogueQuoteDto.getSecManageAcctId());
        inputBean.setWflIbuQuoteBond2Dto(ibuQuoteBondDto);

        // 银行间报价金额基对手信息
        WflIbuQuoteBondDetDto ibuQuoteBondDetDto = new WflIbuQuoteBondDetDto();

        // add by etliu 20210114 start
        // 若报价编号存在，代表是修改场景，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
        if (dbQuoteDataDto != null && dbQuoteDataDto.getIbuQuoteBondDetDto() != null) {
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, dbQuoteDataDto.getIbuQuoteBondDetDto());
            //ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto.getVersion() + 1);
            // modify by lijie2 20200119 start
            ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto.getVersion());
            // modify by lijie2 20200119 end
        }
        else {
            ibuQuoteBondDetDto.setVersion(-1L);
        }
        // add by etliu 20210114 end
        
        ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto.getQuoteId());
        ibuQuoteBondDetDto.setCpPartyId(cfetsBondDealDialogueQuoteDto.getCounterId());
        ibuQuoteBondDetDto.setCpTrader(cfetsBondDealDialogueQuoteDto.getCounterTrader());
        ibuQuoteBondDetDto.setUnitCprice(cfetsBondDealDialogueQuoteDto.getPrice());
        ibuQuoteBondDetDto.setUnitInt(cfetsBondDealDialogueQuoteDto.getUnitInt());
        ibuQuoteBondDetDto.setUnitFprice(cfetsBondDealDialogueQuoteDto.getUnitFprice());
        ibuQuoteBondDetDto.setParValue(cfetsBondDealDialogueQuoteDto.getParValue());
        ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(100)));// 画面是万元单位 字段存份额 -->
                                                                             // *10000/100
        ibuQuoteBondDetDto.setPrinAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(10000)));
        ibuQuoteBondDetDto.setParValueAmt(cfetsBondDealDialogueQuoteDto.getParValueAmt());
        ibuQuoteBondDetDto.setCpriceAmt(cfetsBondDealDialogueQuoteDto.getCpriceAmt());
        ibuQuoteBondDetDto.setIntAmt(cfetsBondDealDialogueQuoteDto.getIntAmt());
        ibuQuoteBondDetDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        ibuQuoteBondDetDto.setYield(
            this.yieldHandle(cfetsBondDealDialogueQuoteDto.getMatureYield(), true));
        ibuQuoteBondDetDto.setExerYield(
            this.yieldHandle(cfetsBondDealDialogueQuoteDto.getExerYield(), true));
        ibuQuoteBondDetDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        ibuQuoteBondDetDto.setCalculateField(cfetsBondDealDialogueQuoteDto.getCalculateField());
        ibuQuoteBondDetDto.setWorkflowId(cfetsBondDealDialogueQuoteDto.getWorkflowId());
        ibuQuoteBondDetDto.setTradeStatus(cfetsBondDealDialogueQuoteDto.getTradeStatus());
        ibuQuoteBondDetDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
        if (StringUtils.equals(cfetsBondDealDialogueQuoteDto.getOptType(), "revoke")) {
        	ibuQuoteBondDetDto.setWorkflowStatus(DictCodeListExt1.有效状态_有效.getValue());
        	ibuQuoteBondDetDto.setTradeStatus(DictCodeList.银行间上行报价状态_已成交.getValue());
        	//ibuQuoteBondDetDto.setSendStatus(DictCodeList.银行间上行发送状态_成功.getValue());
            //已成交/空的记录点击撤销，提交流程后，状态变成已成交/成功，应该还是已成交/空
            ibuQuoteBondDto.setSendStatus(StringUtil.EMPTY);
		}
        inputBean.setWflIbuQuoteBondDetDto(ibuQuoteBondDetDto);

        if(!StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())){//多组合列表非空
        	 inputBean.setIsDzh("Y");
             List<CfetsBondDealDialogueQuoteDto> ptlAllocations = cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList();
             //银行间报价指令关系
             List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrReDtoList = new ArrayList<>();
             ptlAllocations.forEach(item -> {
            	 WflIbuQuoteEtrReDto wflIbuQuoteEtrReDto = new WflIbuQuoteEtrReDto();
            	 wflIbuQuoteEtrReDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
            	 wflIbuQuoteEtrReDto.setVersion(-1L);// 版本号
            	 wflIbuQuoteEtrReDto.setEntrustId(item.getEntrustId());
            	 wflIbuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
            	 wflIbuQuoteEtrReDto.setShareAmt(ArithmeticUtil.multiply(
                		 item.getAvaNum(), new BigDecimal(100)));
            	 wflIbuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
            	 wflIbuQuoteEtrReDto.setWorkflowId(cfetsBondDealDialogueQuoteDto.getWorkflowId());
                 wflIbuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                 wflIbuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                 wflIbuQuoteEtrReDto.setCpriceAmt(item.getCpriceAmt());
                 wflIbuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                 wflIbuQuoteEtrReDto.setIntAmt(item.getIntAmt());
                 wflIbuQuoteEtrReDto.setInvAim(item.getInvAim());
                 wflIbuQuoteEtrReDto.setOurShareAmt(ArithmeticUtil.divide(item.getOurShareAmt(),new BigDecimal(100),2));
                 wflIbuQuoteEtrReDto.setLendinShareAmt(ArithmeticUtil.divide(item.getLendinShareAmt(),new BigDecimal(100),2));
                 wflIbuQuoteEtrReDtoList.add(wflIbuQuoteEtrReDto);
             });
             inputBean.setWflIbuQuoteEtrReDtoList(wflIbuQuoteEtrReDtoList);
        }
        return inputBean;
    }

    /**
     * 对话报价构造组件调用对象
     * 
     * @param cfetsBondDealDialogueQuoteDto 前台对话报价对象
     * @return 组件调用对象
     * @see
     * @since 1.0
     */
    private BondDialogueQuoteDto genBondDialogueQuoteDto(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        BondDialogueQuoteDto inputBean = new BondDialogueQuoteDto();
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        
        BondDialogueQuoteDto dbQuoteDataDto = null;
        if (StringUtil.isNotEmpty(cfetsBondDealDialogueQuoteDto.getQuoteId())) {
            ibuQuoteBondDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
            
            // add by etliu 20210114 start
            // 若报价编号存在，代表是修改场景，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
            dbQuoteDataDto = ibuMainService.getBondDialogueQuote(cfetsBondDealDialogueQuoteDto.getQuoteId());
            if (dbQuoteDataDto != null && dbQuoteDataDto.getIbuQuoteBondDto() != null) {
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, dbQuoteDataDto.getIbuQuoteBondDto());
                //ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion() + 1);
                ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion());
            }
            else {
                ibuQuoteBondDto.setVersion(-1L);
            }
            // add by etliu 20210114 end
        }
        else {
            ibuQuoteBondDto.setQuoteId(this.genBusId("IBU_001"));
            ibuQuoteBondDto.setVersion(-1L);
        }
        
        ibuQuoteBondDto.setQuoteType(DictCodeList.报价系统区分类型_现券对话报价.getValue());
        ibuQuoteBondDto.setQuoteRoleType(BussinessDictCodeList.银行间上行本方角色_询价方.getValue());
        ibuQuoteBondDto.setFinprodId(cfetsBondDealDialogueQuoteDto.getFinprodId());
        ibuQuoteBondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        ibuQuoteBondDto.setBranch(0L);
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealDialogueQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealDialogueQuoteDto.getEntrustId());
        ibuQuoteBondDto.setQuoteDate(iPlatformDate.getSysDate());
        ibuQuoteBondDto.setSettleDate(cfetsBondDealDialogueQuoteDto.getSettleDate());
        if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(cfetsBondDealDialogueQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(1L);
        }
        ibuQuoteBondDto.setDeliveryType(cfetsBondDealDialogueQuoteDto.getSettleMethod());
        ibuQuoteBondDto.setSettType(cfetsBondDealDialogueQuoteDto.getSettleType());
        ibuQuoteBondDto.setSettCcy(cfetsBondDealDialogueQuoteDto.getSettleCcy());
        ibuQuoteBondDto.setPs(cfetsBondDealDialogueQuoteDto.getPs());
        ibuQuoteBondDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(100)));// 画面是万元单位 字段存份额 -->
                                                                             // *10000/100
        ibuQuoteBondDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        ibuQuoteBondDto.setEffTime(
            DateUtil.parse(iPlatformDate.getSysDateStr() + " "
                           + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00")); // 请求报价从系统参数获取
        ibuQuoteBondDto.setOuPartyId(cfetsBondDealDialogueQuoteDto.getMySide());
        ibuQuoteBondDto.setOuTraderId(cfetsBondDealDialogueQuoteDto.getMySideTrader());
        ibuQuoteBondDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
        ibuQuoteBondDto.setDipType(cfetsBondDealDialogueQuoteDto.getDistribution());
        ibuQuoteBondDto.setDealFeeAmt(cfetsBondDealDialogueQuoteDto.getDealFeeAmt());
        ibuQuoteBondDto.setSettFeeAmt(cfetsBondDealDialogueQuoteDto.getSettFeeAmt());
        ibuQuoteBondDto.setSecManageAcctId(cfetsBondDealDialogueQuoteDto.getSecManageAcctId());
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);

        // 银行间报价金额基对手信息
        IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
        
        // add by etliu 20210114 start
        // 若报价编号存在，代表是修改场景，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
        if (dbQuoteDataDto != null && dbQuoteDataDto.getIbuQuoteBondDetDto() != null) {
            BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDetDto, dbQuoteDataDto.getIbuQuoteBondDetDto());
            ibuQuoteBondDetDto.setVersion(ibuQuoteBondDetDto.getVersion());
        }
        else {
            ibuQuoteBondDetDto.setVersion(-1L);
        }
        // add by etliu 20210114 end
        
        ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto.getQuoteId());
        ibuQuoteBondDetDto.setCpPartyId(cfetsBondDealDialogueQuoteDto.getCounterId());
        ibuQuoteBondDetDto.setCpTrader(cfetsBondDealDialogueQuoteDto.getCounterTrader());
        ibuQuoteBondDetDto.setUnitCprice(cfetsBondDealDialogueQuoteDto.getPrice());
        ibuQuoteBondDetDto.setUnitInt(cfetsBondDealDialogueQuoteDto.getUnitInt());
        ibuQuoteBondDetDto.setUnitFprice(cfetsBondDealDialogueQuoteDto.getUnitFprice());
        ibuQuoteBondDetDto.setParValue(cfetsBondDealDialogueQuoteDto.getParValue());
        ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(100)));// 画面是万元单位 字段存份额 -->
                                                                             // *10000/100
        ibuQuoteBondDetDto.setPrinAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getAvaNum(), new BigDecimal(10000)));
        ibuQuoteBondDetDto.setParValueAmt(cfetsBondDealDialogueQuoteDto.getParValueAmt());
        ibuQuoteBondDetDto.setCpriceAmt(cfetsBondDealDialogueQuoteDto.getCpriceAmt());
        ibuQuoteBondDetDto.setIntAmt(cfetsBondDealDialogueQuoteDto.getIntAmt());
        ibuQuoteBondDetDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        ibuQuoteBondDetDto.setYield(
            this.yieldHandle(cfetsBondDealDialogueQuoteDto.getMatureYield(), true));
        ibuQuoteBondDetDto.setExerYield(
            this.yieldHandle(cfetsBondDealDialogueQuoteDto.getExerYield(), true));
        ibuQuoteBondDetDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        ibuQuoteBondDetDto.setCalculateField(cfetsBondDealDialogueQuoteDto.getCalculateField());
        ibuQuoteBondDetDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
        inputBean.setIbuQuoteBondDetDto(ibuQuoteBondDetDto);

        if(!StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())){//多组合列表非空
       	 inputBean.setIsDzh("Y");
            List<CfetsBondDealDialogueQuoteDto> ptlAllocations = cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList();
            //银行间报价指令关系
            List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = new ArrayList<>();
            ptlAllocations.forEach(item -> {
           	 
            	IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
           	
            	ibuQuoteEtrReDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
            	ibuQuoteEtrReDto.setVersion(-1L);// 版本号
	           	ibuQuoteEtrReDto.setEntrustId(item.getEntrustId());
	           	ibuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
	           	ibuQuoteEtrReDto.setShareAmt(ArithmeticUtil.multiply(
               		 item.getAvaNum(), new BigDecimal(100)));
	           	ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                ibuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                ibuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                ibuQuoteEtrReDto.setCpriceAmt(item.getCpriceAmt());
                ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                ibuQuoteEtrReDto.setIntAmt(item.getIntAmt());
                ibuQuoteEtrReDto.setInvAim(item.getInvAim());
                ibuQuoteEtrReDto.setOurShareAmt(ArithmeticUtil.divide(item.getOurShareAmt(),new BigDecimal(100),2));
                ibuQuoteEtrReDto.setLendinShareAmt(ArithmeticUtil.divide(item.getLendinShareAmt(),new BigDecimal(100),2));
	           	ibuQuoteEtrReDtoList.add(ibuQuoteEtrReDto);
            });
            inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrReDtoList);
       }
        
        return inputBean;
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
    
    /**
     * 现券对话报价新增<br />
     * 对话报价信息入流程表
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void insertWflBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.insertWflBondDialogueQuote(
            this.genWflBondDialogueQuoteDto(cfetsBondDealDialogueQuoteDto), user);
    }

    /**
     * 现券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsBondDealDialogueQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void insertBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.insertBondDialogueQuote(
            this.genBondDialogueQuoteDto(cfetsBondDealDialogueQuoteDto), user);

        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }

    /**
     * 构造请求报价调组件对象
     * 
     * @param cfetsBondDealRequestQuoteDto 请求报价
     * @return 组件对象
     * @see
     * @since 1.0
     */
    public BondRequestQuoteDto getBondRequestQuoteDto(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        BondRequestQuoteDto inputBean = new BondRequestQuoteDto();
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        
        // db中既存数据
        BondRequestQuoteDto dbQuoteDataDto = null;
        
        if (StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getQuoteId())) {
            ibuQuoteBondDto.setQuoteId(cfetsBondDealRequestQuoteDto.getQuoteId());
            
            // add by etliu 20210116 start
            // 若报价编号存在，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
            dbQuoteDataDto = ibuMainService.getBondRequestQuote(cfetsBondDealRequestQuoteDto.getQuoteId());
            if (dbQuoteDataDto != null && dbQuoteDataDto.getIbuQuoteBondDto() != null) {
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, dbQuoteDataDto.getIbuQuoteBondDto());
                ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion());
            }
            else {
                ibuQuoteBondDto.setVersion(-1L);
            }
            // add by etliu 20210116 end
        }
        else {
            ibuQuoteBondDto.setQuoteId(this.genBusId("IBU_002"));
            ibuQuoteBondDto.setVersion(-1L);
        }
        ibuQuoteBondDto.setQuoteType(DictCodeList.报价系统区分类型_现券请求报价.getValue());
        ibuQuoteBondDto.setQuoteRoleType(BussinessDictCodeList.银行间上行本方角色_非做市方.getValue());
        ibuQuoteBondDto.setFinprodId(cfetsBondDealRequestQuoteDto.getFinprodId());
        ibuQuoteBondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        ibuQuoteBondDto.setBranch(0L);
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealRequestQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealRequestQuoteDto.getEntrustId());
        ibuQuoteBondDto.setQuoteDate(iPlatformDate.getSysDate());
        ibuQuoteBondDto.setSettleDate(cfetsBondDealRequestQuoteDto.getSettleDate());
        if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(1L);
        }
        ibuQuoteBondDto.setDeliveryType(cfetsBondDealRequestQuoteDto.getSettleMethod());
        ibuQuoteBondDto.setSettType(cfetsBondDealRequestQuoteDto.getSettleType());
        ibuQuoteBondDto.setSettCcy(cfetsBondDealRequestQuoteDto.getSettleCcy());
        ibuQuoteBondDto.setPs(cfetsBondDealRequestQuoteDto.getPs());
        ibuQuoteBondDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealRequestQuoteDto.getAvaNum(), new BigDecimal(100)));// 画面是万元单位 字段存份额
                                                                            // *10000/100
        // ibuQuoteBondDto.setFpriceAmt(cfetsBondDealRequestQuoteDto.getFpriceAmt());
        ibuQuoteBondDto.setEffTime(
                DateUtil.parse(iPlatformDate.getSysDateStr() + " " + (cfetsBondDealRequestQuoteDto.getEffDate() == null
                        ? CacheUtil.getSysParam("ibuVar_quoteEffTime") : cfetsBondDealRequestQuoteDto.getEffDate()) + ":00")); // 请求报价从系统参数获取
        ibuQuoteBondDto.setOuPartyId(cfetsBondDealRequestQuoteDto.getMySide());
        ibuQuoteBondDto.setOuTraderId(cfetsBondDealRequestQuoteDto.getMySideTrader());
        ibuQuoteBondDto.setRemark(cfetsBondDealRequestQuoteDto.getRemark());
        if(StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())){
        	ibuQuoteBondDto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
        }
        ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
        ibuQuoteBondDto.setDipType(cfetsBondDealRequestQuoteDto.getDistribution());
        ibuQuoteBondDto.setDealFeeAmt(cfetsBondDealRequestQuoteDto.getDealFeeAmt());
        ibuQuoteBondDto.setSettFeeAmt(cfetsBondDealRequestQuoteDto.getSettFeeAmt());
        ibuQuoteBondDto.setSecManageAcctId(cfetsBondDealRequestQuoteDto.getSecManageAcctId());
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);

        // 银行间报价金额基对手信息列表
        List<IbuQuoteBondDetDto> ibuQuoteBondDetDtoList = new ArrayList<>();
        List<String> counterIdList = cfetsBondDealRequestQuoteDto.getCounterIdList();
        
        // add by etliu 20210116 start
        // db中既存数据
        Map<String, IbuQuoteBondDetDto> detDataMap = new HashMap<String, IbuQuoteBondDetDto>();
        if (dbQuoteDataDto != null
            && !StringUtil.isEmptyList(dbQuoteDataDto.getIbuQuoteBondDetDtoList())) {
            detDataMap = dbQuoteDataDto.getIbuQuoteBondDetDtoList().stream().collect(
                JoyinCollectors.toMap(x -> x.getCpPartyId(), x -> x));
        }
        // add by etliu 20210116 end
        
        for (String counterId : counterIdList) {
            IbuQuoteBondDetDto dto = new IbuQuoteBondDetDto();
            
            // add by etliu 20210116 start
            if (detDataMap.containsKey(counterId)) {
                BeanUtil.copyPropertiesIgnoreNull(dto, detDataMap.get(counterId));
            }
            // add by etliu 20210116 end
            
            dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
            dto.setCpPartyId(counterId);
            /*
             * BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
             * dto.setYield(this.yieldHandle(dto.getYield(), true));
             * dto.setExerYield(this.yieldHandle(dto.getExerYield(), true));
             */
            if(StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())){
            	dto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
            }
            ibuQuoteBondDetDtoList.add(dto);
        }
        inputBean.setIbuQuoteBondDetDtoList(ibuQuoteBondDetDtoList);
        
        if(!StringUtil.isEmptyList(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList())){//多组合列表非空
       	   inputBean.setIsDzh("Y");
            List<CfetsBondDealRequestQuoteDto> ptlAllocations = cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList();
            //银行间报价指令关系
            List<IbuQuoteEtrReDto> ibuQuoteEtrReDtoList = new ArrayList<>();
            ptlAllocations.forEach(item -> {
            	IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
            	ibuQuoteEtrReDto.setQuoteId(cfetsBondDealRequestQuoteDto.getQuoteId());
            	ibuQuoteEtrReDto.setVersion(-1L);// 版本号
	           	ibuQuoteEtrReDto.setEntrustId(item.getEntrustId());
	           	ibuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
	           	ibuQuoteEtrReDto.setShareAmt(ArithmeticUtil.multiply(
               		 item.getAvaNum(), new BigDecimal(100)));
                ibuQuoteEtrReDto.setFpriceAmt(ArithmeticUtil.multiply(
                    ibuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100)));
                if(StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())){
                    ibuQuoteEtrReDto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
                }
                ibuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                ibuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                ibuQuoteEtrReDto.setCpriceAmt(item.getCpriceAmt());
                ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                ibuQuoteEtrReDto.setIntAmt(item.getIntAmt());
                ibuQuoteEtrReDto.setInvAim(item.getInvAim());
                ibuQuoteEtrReDto.setOurShareAmt(ArithmeticUtil.divide(item.getOurShareAmt(),new BigDecimal(100),2));
                ibuQuoteEtrReDto.setLendinShareAmt(ArithmeticUtil.divide(item.getLendinShareAmt(),new BigDecimal(100),2));
	           	ibuQuoteEtrReDtoList.add(ibuQuoteEtrReDto);
            });
            inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrReDtoList);
       }
        return inputBean;
    }
    
    /**
     * 构造请求报价调组件对象
     * 
     * @param cfetsBondDealRequestQuoteDto 请求报价
     * @return 组件对象
     * @see
     * @since 1.0
     */
    public BondRequestQuoteDto getWflBondRequestQuoteDto(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
    	String quoteEffTime = CacheUtil.getSysParam("ibuVar_quoteEffTime");
        BondRequestQuoteDto inputBean = new BondRequestQuoteDto();
        // 银行间报价主信息
        WflIbuQuoteBond2Dto ibuQuoteBondDto = new WflIbuQuoteBond2Dto();
        
        // db中既存数据
        BondRequestQuoteDto dbQuoteDataDto = null;
        
        if (StringUtil.isNotEmpty(cfetsBondDealRequestQuoteDto.getQuoteId())) {
            ibuQuoteBondDto.setQuoteId(cfetsBondDealRequestQuoteDto.getQuoteId());
            
            // add by etliu 20210116 start
            // 若报价编号存在，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
            dbQuoteDataDto = ibuMainService.getBondRequestQuote(cfetsBondDealRequestQuoteDto.getQuoteId());
            if (dbQuoteDataDto != null && dbQuoteDataDto.getIbuQuoteBondDto() != null) {
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteBondDto, dbQuoteDataDto.getIbuQuoteBondDto());
                //ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion() + 1);
                ibuQuoteBondDto.setVersion(ibuQuoteBondDto.getVersion());
                // add by lijie2 20200118 start 单机构点击成交，进入流程中，此时状态应该更新为：成交中
				if (StringUtils.equals(ibuQuoteBondDto.getTradeStatus(), BussinessDictCodeList.银行间上行报价状态_已回复.getValue())) {
					ibuQuoteBondDto.setTradeStatus(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
				}
                // add by lijie2 20200118 end
            }
            else {
                ibuQuoteBondDto.setVersion(-1L);
            }
            // add by etliu 20210116 end
        }
        else {
            ibuQuoteBondDto.setQuoteId(this.genBusId("IBU_002"));
            ibuQuoteBondDto.setVersion(-1L);
        }
        ibuQuoteBondDto.setQuoteType(DictCodeList.报价系统区分类型_现券请求报价.getValue());
        ibuQuoteBondDto.setQuoteRoleType(BussinessDictCodeList.银行间上行本方角色_非做市方.getValue());
        ibuQuoteBondDto.setFinprodId(cfetsBondDealRequestQuoteDto.getFinprodId());
        ibuQuoteBondDto.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_债券.getValue());
        ibuQuoteBondDto.setBranch(0L);
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealRequestQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealRequestQuoteDto.getEntrustId());
        ibuQuoteBondDto.setQuoteDate(iPlatformDate.getSysDate());
        ibuQuoteBondDto.setSettleDate(cfetsBondDealRequestQuoteDto.getSettleDate());
        if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(cfetsBondDealRequestQuoteDto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            ibuQuoteBondDto.setSettleSpeed(1L);
        }
        ibuQuoteBondDto.setDeliveryType(cfetsBondDealRequestQuoteDto.getSettleMethod());
        ibuQuoteBondDto.setSettType(cfetsBondDealRequestQuoteDto.getSettleType());
        ibuQuoteBondDto.setSettCcy(cfetsBondDealRequestQuoteDto.getSettleCcy());
        ibuQuoteBondDto.setPs(cfetsBondDealRequestQuoteDto.getPs());
        ibuQuoteBondDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealRequestQuoteDto.getAvaNum(), new BigDecimal(100)));// 画面是万元单位 字段存份额
                                                                            // *10000/100
        // ibuQuoteBondDto.setFpriceAmt(cfetsBondDealRequestQuoteDto.getFpriceAmt());
		ibuQuoteBondDto.setEffTime(
				DateUtil.parse(iPlatformDate.getSysDateStr() + " " + (cfetsBondDealRequestQuoteDto.getEffDate() == null
						? quoteEffTime : cfetsBondDealRequestQuoteDto.getEffDate()) + ":00")); // 请求报价从系统参数获取
        ibuQuoteBondDto.setOuPartyId(cfetsBondDealRequestQuoteDto.getMySide());
        ibuQuoteBondDto.setOuTraderId(cfetsBondDealRequestQuoteDto.getMySideTrader());
        ibuQuoteBondDto.setRemark(cfetsBondDealRequestQuoteDto.getRemark());
        if(StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())){
        	ibuQuoteBondDto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
        }
        ibuQuoteBondDto.setWorkflowStatus(DictCodeListExt1.有效状态_流程中.getValue());
        ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
        // 哎，撤销流程中要特殊处理，和交易撤销一样，审批前状态还是有效 审批后才变成撤销
		if (StringUtils.equals(cfetsBondDealRequestQuoteDto.getOptType(), "revoke")) {
			  ibuQuoteBondDto.setWorkflowStatus(DictCodeListExt1.有效状态_有效.getValue());
			  ibuQuoteBondDto.setTradeStatus(DictCodeList.银行间上行报价状态_已成交.getValue());
		      ibuQuoteBondDto.setSendStatus(DictCodeList.银行间上行发送状态_成功.getValue());
		}
        ibuQuoteBondDto.setDealFeeAmt(cfetsBondDealRequestQuoteDto.getDealFeeAmt());
        ibuQuoteBondDto.setSettFeeAmt(cfetsBondDealRequestQuoteDto.getSettFeeAmt());
        ibuQuoteBondDto.setDipType(cfetsBondDealRequestQuoteDto.getDistribution());
        ibuQuoteBondDto.setSecManageAcctId(cfetsBondDealRequestQuoteDto.getSecManageAcctId());
        inputBean.setWflIbuQuoteBond2Dto(ibuQuoteBondDto);

        // 银行间报价金额基对手信息列表
        List<WflIbuQuoteBondDetDto> ibuQuoteBondDetDtoList = new ArrayList<>();
        List<String> counterIdList = cfetsBondDealRequestQuoteDto.getCounterIdList();
        
     // add by etliu 20210116 start
        // db中既存数据
        Map<String, IbuQuoteBondDetDto> detDataMap = new HashMap<String, IbuQuoteBondDetDto>();
        if (dbQuoteDataDto != null
            && !StringUtil.isEmptyList(dbQuoteDataDto.getIbuQuoteBondDetDtoList())) {
            detDataMap = dbQuoteDataDto.getIbuQuoteBondDetDtoList().stream().collect(
                JoyinCollectors.toMap(x -> x.getCpPartyId(), x -> x));
        }
     // add by etliu 20210116 end

        if(null != counterIdList && counterIdList.size() > 0){
        	for (String counterId : counterIdList) {
            	WflIbuQuoteBondDetDto dto = new WflIbuQuoteBondDetDto();
            	
            	// add by etliu 20210116 start
            	if (detDataMap.containsKey(counterId)) {
            	    BeanUtil.copyPropertiesIgnoreNull(dto, detDataMap.get(counterId));
            	}
            	// add by etliu 20210116 end
            	
                dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
                dto.setCpPartyId(counterId);
                /*
                 * BeanUtil.copyPropertiesIgnoreNull(dto, ibuQuoteBondDto);
                 * dto.setYield(this.yieldHandle(dto.getYield(), true));
                 * dto.setExerYield(this.yieldHandle(dto.getExerYield(), true));
                 */
                if(StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())){
                	dto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
                }
                dto.setWorkflowStatus(DictCodeListExt1.有效状态_流程中.getValue());
				if (StringUtils.equals(cfetsBondDealRequestQuoteDto.getOptType(), "revoke")) {
					dto.setWorkflowStatus(DictCodeListExt1.有效状态_有效.getValue());
					dto.setTradeStatus(DictCodeList.银行间上行报价状态_已成交.getValue());
					dto.setSendStatus(DictCodeList.银行间上行发送状态_成功.getValue());
				}
                ibuQuoteBondDetDtoList.add(dto);
            }
		} else if (StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getCounterId())) {
			WflIbuQuoteBondDetDto dto = new WflIbuQuoteBondDetDto();
			dto.setQuoteId(ibuQuoteBondDto.getQuoteId());
			dto.setCpPartyId(cfetsBondDealRequestQuoteDto.getCounterId());
			if (StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())) {
				dto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
			}
			dto.setWorkflowStatus(DictCodeListExt1.有效状态_流程中.getValue());
			ibuQuoteBondDetDtoList.add(dto);
		}
        
        inputBean.setWflIbuQuoteBondDetDtoList(ibuQuoteBondDetDtoList);
        
        if(!StringUtil.isEmptyList(cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList())){//多组合列表非空
       	 	inputBean.setIsDzh("Y");
            List<CfetsBondDealRequestQuoteDto> ptlAllocations = cfetsBondDealRequestQuoteDto.getCfetsBondDealRequestQuoteDtoList();
            //银行间报价指令关系
            List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrReDtoList = new ArrayList<>();
            ptlAllocations.forEach(item -> {
                WflIbuQuoteEtrReDto wflIbuQuoteEtrReDto = new WflIbuQuoteEtrReDto();
                wflIbuQuoteEtrReDto.setQuoteId(cfetsBondDealRequestQuoteDto.getQuoteId());
                wflIbuQuoteEtrReDto.setVersion(-1L);// 版本号
                wflIbuQuoteEtrReDto.setEntrustId(item.getEntrustId());
                wflIbuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
                wflIbuQuoteEtrReDto.setShareAmt(
                    ArithmeticUtil.multiply(item.getAvaNum(), new BigDecimal(100)));
                wflIbuQuoteEtrReDto.setFpriceAmt(ArithmeticUtil.multiply(
                    wflIbuQuoteEtrReDto.getShareAmt(), BigDecimal.valueOf(100)));
                if(StringUtils.isNotEmpty(cfetsBondDealRequestQuoteDto.getWorkflowId())){
                    wflIbuQuoteEtrReDto.setWorkflowId(cfetsBondDealRequestQuoteDto.getWorkflowId());
                }
                wflIbuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                wflIbuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                wflIbuQuoteEtrReDto.setCpriceAmt(item.getCpriceAmt());
                wflIbuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                wflIbuQuoteEtrReDto.setIntAmt(item.getIntAmt());
                wflIbuQuoteEtrReDto.setInvAim(item.getInvAim());
                wflIbuQuoteEtrReDto.setOurShareAmt(ArithmeticUtil.divide(item.getOurShareAmt(),new BigDecimal(100),2));
                wflIbuQuoteEtrReDto.setLendinShareAmt(ArithmeticUtil.divide(item.getLendinShareAmt(),new BigDecimal(100),2));
                wflIbuQuoteEtrReDtoList.add(wflIbuQuoteEtrReDto);
            });
            inputBean.setWflIbuQuoteEtrReDtoList(wflIbuQuoteEtrReDtoList);
       }
        
        return inputBean;
    }

    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void insertWflBondRequestQuote(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        BondRequestQuoteDto inputBean = getWflBondRequestQuoteDto(cfetsBondDealRequestQuoteDto);
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.insertWflBondRequestQuote(inputBean, user);
    }
    
    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void insertBondRequestQuote(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        BondRequestQuoteDto inputBean = getBondRequestQuoteDto(cfetsBondDealRequestQuoteDto);
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.insertBondRequestQuote(inputBean, user);

        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }

    /**
     * 现券对话报价新增发送<br />
     * 根据传入的对话报价新增进行发送，并更新全部报价信息
     * 
     * @param inputBean BondDialogueQuoteDto 现券对话报价信息
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendNewBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        BondDialogueQuoteDto bondDialogueQuoteDto = this.genBondDialogueQuoteDto(
            cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendNewBondDialogueQuote(
            bondDialogueQuoteDto, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        return true;
    }
    
    /**
     * 现券对话报价修改发送<br />
     * 根据传入的对话报价新增进行发送，并更新全部报价信息
     * 
     * @param inputBean BondDialogueQuoteDto 现券对话报价信息
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendUpdateBondDialogueQuote(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        BondDialogueQuoteDto bondDialogueQuoteDto = this.genBondDialogueQuoteDto(
            cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendUpdateBondDialogueQuote(
            bondDialogueQuoteDto, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        return true;
    }

    /**
     * 现券请求报价新增<br />
     * 请求报价信息入库
     * 
     * @param cfetsBondDealRequestQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public boolean sendBondRequestQuote(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        BondRequestQuoteDto bondRequestQuoteDto = this.getBondRequestQuoteDto(
            cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendNewBondRequestQuote(bondRequestQuoteDto,
            user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        return true;
    }

    /**
     * 做市方回复非做市方请求报价；<br />
     * 做市方回复非做市方请求报价 操作类型：本方新增请求回复
     * 
     * @param quoteId String 报价编号
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondRequestQuoteNewReply(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        BondRequestQuoteDto inputBean = new BondRequestQuoteDto();
        // 银行间报价主信息
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        ibuQuoteBondDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealDialogueQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealDialogueQuoteDto.getEntrustId());
        ibuQuoteBondDto.setOuTraderId(cfetsBondDealDialogueQuoteDto.getMySideTrader());
        ibuQuoteBondDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        ibuQuoteBondDto.setEffTime(DateUtil.parse(iPlatformDate.getSysDateStr() + " " + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00"));
        // 做市方回复时主表的数据不用更新，需要记录原始请求报价的券面总额
        // ibuQuoteBondDto.setShareAmt(ArithmeticUtil.multiply(cfetsBondDealDialogueQuoteDto.getResponseAmt(),
        // new BigDecimal(100)));// 画面是万元单位 字段存份额// --> *10000/100
        // ibuQuoteBondDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);

        // 银行间报价金额基对手信息
        IbuQuoteBondDetDto ibuQuoteBondDetDto = new IbuQuoteBondDetDto();
        ibuQuoteBondDetDto.setQuoteId(ibuQuoteBondDto.getQuoteId());
        ibuQuoteBondDetDto.setUnitCprice(cfetsBondDealDialogueQuoteDto.getPrice());
        ibuQuoteBondDetDto.setUnitInt(cfetsBondDealDialogueQuoteDto.getUnitInt());
        ibuQuoteBondDetDto.setUnitFprice(cfetsBondDealDialogueQuoteDto.getUnitFprice());
        ibuQuoteBondDetDto.setParValue(cfetsBondDealDialogueQuoteDto.getParValue());
        ibuQuoteBondDetDto.setShareAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getResponseAmt(), new BigDecimal(100)));// 画面是万元单位
                                                                                  // 字段存份额// -->
                                                                                  // *10000/100
        ibuQuoteBondDetDto.setPrinAmt(ArithmeticUtil.multiply(
            cfetsBondDealDialogueQuoteDto.getResponseAmt(), new BigDecimal(10000)));
        ibuQuoteBondDetDto.setParValueAmt(cfetsBondDealDialogueQuoteDto.getParValueAmt());
        ibuQuoteBondDetDto.setCpriceAmt(cfetsBondDealDialogueQuoteDto.getCpriceAmt());
        ibuQuoteBondDetDto.setIntAmt(cfetsBondDealDialogueQuoteDto.getIntAmt());
        ibuQuoteBondDetDto.setFpriceAmt(cfetsBondDealDialogueQuoteDto.getFpriceAmt());
        ibuQuoteBondDetDto.setYield(
            this.yieldHandle(cfetsBondDealDialogueQuoteDto.getMatureYield(), true));
        ibuQuoteBondDetDto.setExerYield(
            this.yieldHandle(cfetsBondDealDialogueQuoteDto.getExerYield(), true));
        ibuQuoteBondDetDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        inputBean.setIbuQuoteBondDetDtoList(Arrays.asList(ibuQuoteBondDetDto));

        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteNewReply(inputBean,
            null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        // 发布MQ
        cfetsQuotedCommonService.publishMQMarketHandle();
    }

    /**
     * 现券对话报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 对话报价对象
     */
    @Transactional
    public boolean sendBondDialogueQuoteRevoke(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondDialogueQuoteRevoke(
            cfetsBondDealDialogueQuoteDto.getQuoteId(), null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        return true;
    }

    /**
     * 现券请求报价撤回；类似“撤回邮件”<br />
     * 报价撤回发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealDialogueQuoteDto 请求报价对象
     */
    @Transactional
    public boolean sendBondRequestQuoteRevoke(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteRevoke(
            cfetsBondDealRequestQuoteDto.getQuoteId(), null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        // 发布MQ刷新做市报价列表
        cfetsQuotedCommonService.publishMQMarketHandle();

        return true;
    }

    /**
     * 本方撤销请求回复；<br />
     * 做市方撤销回复非做市方请求报价 操作类型：本方修改请求回复
     * 
     * @param cfetsBondDealRequestQuoteDto 请求报价对象
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondRequestQuoteRevokeReply(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteRevokeReply(
            cfetsBondDealRequestQuoteDto.getQuoteId(), null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);

        // 发布MQ刷新当日报价列表
        cfetsQuotedCommonService.publishMQDayHandle();
    }

    /**
     * 现券对话报价回退；把失败状态还原成待处理<br />
     * 报价回退发送处理，更新接口返回数据
     * 
     * @param quoteId String 报价编号
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public void bondDialogueQuoteRestore(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.bondDialogueQuoteRestore(
            cfetsBondDealDialogueQuoteDto.getQuoteId(), null, user);

        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, false);
    }

    /**
     * 现券请求报价回退；把失败状态还原成待处理<br />
     * 报价回退发送处理，更新接口返回数据 操作类型：本方报价回退
     * 
     * @param quoteId String 报价编号
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondRequestQuoteRestore(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteRestore(
            cfetsBondDealRequestQuoteDto.getQuoteId(), null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, false);

        // 7、MQ发布消息前台订阅通过ws控制表格刷新
        // 对手方对话报价新增(我方作为回复方)，需要制定交易员推送
        cfetsQuotedCommonService.publishMQCommonHandle(quoteResultDto, true);
    }

    /**
     * 现券对话报价回复成交<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param inputBean BondDialogueQuoteDto
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondDialogueQuoteDeal(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        BondDialogueQuoteDto inputBean = new BondDialogueQuoteDto();
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        ibuQuoteBondDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealDialogueQuoteDto.getEntrustId());
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealDialogueQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setSecManageAcctId(cfetsBondDealDialogueQuoteDto.getSecManageAcctId());
        ibuQuoteBondDto.setDealFeeAmt(cfetsBondDealDialogueQuoteDto.getDealFeeAmt());
        ibuQuoteBondDto.setSettFeeAmt(cfetsBondDealDialogueQuoteDto.getSettFeeAmt());
        ibuQuoteBondDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        ibuQuoteBondDto.setDipType(cfetsBondDealDialogueQuoteDto.getDistribution());
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        //多组合成交关系表  单独处理
        if(!StringUtil.isEmptyList(cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList())){
            List<IbuQuoteEtrReDto>  ibuQuoteEtrReDtoList = new ArrayList<>();
            cfetsBondDealDialogueQuoteDto.getCfetsBondDealDialogueQuoteDtoList().forEach(item ->{
                IbuQuoteEtrReDto ibuQuoteEtrReDto = new IbuQuoteEtrReDto();
                ibuQuoteEtrReDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
                ibuQuoteEtrReDto.setEntrustId(item.getEntrustId());
                ibuQuoteEtrReDto.setPortfolioId(item.getPortfolioId());
                ibuQuoteEtrReDto.setShareAmt(ArithmeticUtil.multiply(
                        item.getAvaNum(), new BigDecimal(100)));
                ibuQuoteEtrReDto.setFpriceAmt(item.getFpriceAmt());
                ibuQuoteEtrReDto.setDealFeeAmt(item.getDealFeeAmt());
                ibuQuoteEtrReDto.setSettFeeAmt(item.getSettFeeAmt());
                ibuQuoteEtrReDto.setInvAim(item.getInvAim());
                ibuQuoteEtrReDto.setOurShareAmt(ArithmeticUtil.divide(item.getOurShareAmt(),new BigDecimal(100),2));
                ibuQuoteEtrReDto.setLendinShareAmt(ArithmeticUtil.divide(item.getLendinShareAmt(),new BigDecimal(100),2));
                ibuQuoteEtrReDtoList.add(ibuQuoteEtrReDto);
            });
            inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrReDtoList);
        }
        QuoteResultDto quoteResultDto = ibuMainService.sendBondDialogueQuoteDeal(inputBean, null,
            user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }

    /**
     * 现券对话报价回复拒绝<br />
     * 报价回复拒绝发送处理，更新接口返回数据
     * 
     * @param inputBean BondDialogueQuoteDto
     * @param attrMap Map<String, String>
     * @param userInfo User 登录用户信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondDialogueQuoteRefuse(CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto) {
        BondDialogueQuoteDto inputBean = new BondDialogueQuoteDto();
        IbuQuoteBondDto ibuQuoteBondDto = new IbuQuoteBondDto();
        ibuQuoteBondDto.setQuoteId(cfetsBondDealDialogueQuoteDto.getQuoteId());
        ibuQuoteBondDto.setEntrustId(cfetsBondDealDialogueQuoteDto.getEntrustId());
        ibuQuoteBondDto.setPortfolioId(cfetsBondDealDialogueQuoteDto.getPortfolioId());
        ibuQuoteBondDto.setRemark(cfetsBondDealDialogueQuoteDto.getRemark());
        inputBean.setIbuQuoteBondDto(ibuQuoteBondDto);
        User user = this.genUserInfo(cfetsBondDealDialogueQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondDialogueQuoteRefuse(inputBean, null,
            user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }

    /**
     * 删除请求报价的主表，主表历史，明细，明细历史数据
     * 
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    public boolean deleteBondRequestQuote(String quoteId) {
        QuoteResultDto quoteResultDto = ibuMainService.deleteBondRequestQuote(quoteId);
        // 判断组件返回结果
        this.checkHandleResult(quoteResultDto);

        return quoteResultDto.isStatus();
    }
    
    /**
     * 删除报价流程中数据
     * 
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
	public boolean deleteWflDataForBond(String workflowId) {
		return ibuMainService.deleteWflDataForBond(workflowId);
	}
	
	
	/**
	 * 查询流程报价编号
	 * @param pageWflType 判断是现券还是质押式回购工作流流程编号
	 * @param workflowId
	 * @return
	 */
	public String selectWflQuoteIdByWorkflowId(String pageWflType,String workflowId) {
		return ibuMainService.selectWflQuoteIdByWorkflowId(pageWflType,workflowId);
	}
    
    /**
     * 删除回购流程中数据
     * 
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
	public boolean deleteWflDataForRepo(String workflowId) {
		return ibuMainService.deleteWflDataForRepo(workflowId);
	}

    /**
     * 删除对话报价的主表，主表历史，明细，明细历史数据
     * 
     * @param quoteId
     * @return QuoteResultDto
     * @see
     * @since 1.0
     */
    public boolean deleteBondDialogueQuote(String quoteId) {
        QuoteResultDto quoteResultDto = ibuMainService.deleteBondDialogueQuote(quoteId);
        // 判断组件返回结果
        this.checkHandleResult(quoteResultDto);

        return quoteResultDto.isStatus();
    }

    /**
     * 现券对话报价查询<br />
     * 根据主键查询对话报价信息
     * 
     * @param quoteId String 报价编号
     * @return BondDialogueQuoteDto
     */
	public CfetsBondDealDialogueQuoteDto getBondDialogueQuote(String quoteId, String... statusValid) {
        CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
        BondDialogueQuoteDto bondDialogueQuoteDto = new BondDialogueQuoteDto();
		if (null != statusValid && statusValid.length > 0 && StringUtils.equals(statusValid[0], DictCodeListExt1.有效状态_流程中.getValue())) {
			bondDialogueQuoteDto = ibuMainService.getWflBondDialogueQuote(quoteId);
		} else {
			bondDialogueQuoteDto = ibuMainService.getBondDialogueQuote(quoteId);
		}
        
        if (bondDialogueQuoteDto != null) {
            if (bondDialogueQuoteDto.getIbuQuoteBondDto() != null) {
                cfetsBondDealDialogueQuoteDto.setQuoteId(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getQuoteId());
                cfetsBondDealDialogueQuoteDto.setFinprodId(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodId());
                cfetsBondDealDialogueQuoteDto.setPortfolioId(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getPortfolioId());
                cfetsBondDealDialogueQuoteDto.setEntrustId(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getEntrustId());
                cfetsBondDealDialogueQuoteDto.setSettleDate(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getSettleDate());
                if (bondDialogueQuoteDto.getIbuQuoteBondDto().getSettleSpeed() == 0L) {
                    cfetsBondDealDialogueQuoteDto.setSettleSpreed(
                        BussinessDictCodeList.清算速度_T加0.getValue());
                }
                else if (bondDialogueQuoteDto.getIbuQuoteBondDto().getSettleSpeed() == 1L) {
                    cfetsBondDealDialogueQuoteDto.setSettleSpreed(
                        BussinessDictCodeList.清算速度_T加1.getValue());
                }
                cfetsBondDealDialogueQuoteDto.setSettleMethod(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getDeliveryType());
                cfetsBondDealDialogueQuoteDto.setSettleType(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getSettType());
                cfetsBondDealDialogueQuoteDto.setSettleCcy(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getSettCcy());
                cfetsBondDealDialogueQuoteDto.setPs(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getPs());
                cfetsBondDealDialogueQuoteDto.setAvaNum(ArithmeticUtil.multiply(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getShareAmt(), new BigDecimal(100)));
                cfetsBondDealDialogueQuoteDto.setFpriceAmt(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getFpriceAmt());
                cfetsBondDealDialogueQuoteDto.setMySide(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getOuPartyId());
                cfetsBondDealDialogueQuoteDto.setMySideTrader(bondDialogueQuoteDto.getIbuQuoteBondDto().getOuTraderId());// 本方银行间交易员
//                if (StringUtil.equals(bondDialogueQuoteDto.getIbuQuoteBondDto().getExeType(), DictCodeList.银行间上行操作类型_对手方报价新增.getValue())) {
//                	cfetsBondDealDialogueQuoteDto.setMySideTrader(bondDialogueQuoteDto.getIbuQuoteBondDto().getCreateUser());// 本方银行间交易员
//                }
                cfetsBondDealDialogueQuoteDto.setRemark(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getRemark());
                if (bondDialogueQuoteDto.getIbuQuoteBondDetDto() != null) {
                    cfetsBondDealDialogueQuoteDto.setCounterId(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getCpPartyId());
                    cfetsBondDealDialogueQuoteDto.setCounterTrader(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getCpTrader());
                    cfetsBondDealDialogueQuoteDto.setPrice(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getUnitCprice());
                    cfetsBondDealDialogueQuoteDto.setUnitInt(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getUnitInt());
                    cfetsBondDealDialogueQuoteDto.setUnitFprice(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getUnitFprice());
                    cfetsBondDealDialogueQuoteDto.setParValue(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getParValue());
                    cfetsBondDealDialogueQuoteDto.setParValueAmt(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getParValueAmt());
                    cfetsBondDealDialogueQuoteDto.setCpriceAmt(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getCpriceAmt());
                    cfetsBondDealDialogueQuoteDto.setIntAmt(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getIntAmt());
                    cfetsBondDealDialogueQuoteDto.setFpriceAmt(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getFpriceAmt());
                    cfetsBondDealDialogueQuoteDto.setMatureYield(this.yieldHandle(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getYield(), false));
                    cfetsBondDealDialogueQuoteDto.setExerYield(this.yieldHandle(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getExerYield(), false));
                    cfetsBondDealDialogueQuoteDto.setCalculateField(
                        bondDialogueQuoteDto.getIbuQuoteBondDetDto().getCalculateField());
                    // add by zhangliang 20200620 做市方报价回复的场合主表取不到SHARE_AMT需要从明细获取
                    if (cfetsBondDealDialogueQuoteDto.getAvaNum() == null) {
                        cfetsBondDealDialogueQuoteDto.setAvaNum(ArithmeticUtil.multiply(
                            bondDialogueQuoteDto.getIbuQuoteBondDetDto().getShareAmt(),
                            new BigDecimal(100)));
                    }
                    // add by zhangliang 2020-07-16 回复量
                    cfetsBondDealDialogueQuoteDto.setResponseAmt(ArithmeticUtil.multiply(
                            bondDialogueQuoteDto.getIbuQuoteBondDetDto().getShareAmt(),
                            new BigDecimal(100)));
                }
                cfetsBondDealDialogueQuoteDto.setQuoteRoleType(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getQuoteRoleType());
                cfetsBondDealDialogueQuoteDto.setSendStatus(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getSendStatus());
                cfetsBondDealDialogueQuoteDto.setTradeStatus(
                    bondDialogueQuoteDto.getIbuQuoteBondDto().getTradeStatus());
                cfetsBondDealDialogueQuoteDto.setRemark(
                        bondDialogueQuoteDto.getIbuQuoteBondDto().getRemark());
                cfetsBondDealDialogueQuoteDto.setDealFeeAmt(bondDialogueQuoteDto.getIbuQuoteBondDto().getDealFeeAmt());
                cfetsBondDealDialogueQuoteDto.setSettFeeAmt(bondDialogueQuoteDto.getIbuQuoteBondDto().getSettFeeAmt());
                cfetsBondDealDialogueQuoteDto.setDistribution(bondDialogueQuoteDto.getIbuQuoteBondDto().getDipType());
                cfetsBondDealDialogueQuoteDto.setSecManageAcctId(bondDialogueQuoteDto.getIbuQuoteBondDto().getSecManageAcctId());
                cfetsBondDealDialogueQuoteDto.setFinprodAbbr(bondDialogueQuoteDto.getIbuQuoteBondDto().getFinprodAbbr());
                cfetsBondDealDialogueQuoteDto.setType11(bondDialogueQuoteDto.getIbuQuoteBondDto().getType11());
            }
        }

        return cfetsBondDealDialogueQuoteDto;
    }

    // add by wangzhuangzhuang 报价双击查看详情 20200521 start
    /**
     * 现券请求报价查询<br />
     * 根据主键查询请求报价信息
     * 
     * @param quoteId String 报价编号
     * @return BondRequestQuoteDto
     */
    public CfetsBondDealRequestQuoteDto getBondRequestQuote(String quoteId,String... statusValid) {
        CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
        BondRequestQuoteDto bondRequestQuoteDto = new BondRequestQuoteDto();
		if (null != statusValid && statusValid.length > 0 && StringUtils.equals(statusValid[0], DictCodeListExt1.有效状态_流程中.getValue())) {
			bondRequestQuoteDto = ibuMainService.getWflBondRequestQuote(quoteId);
		} else {
			bondRequestQuoteDto = ibuMainService.getBondRequestQuote(quoteId);
		}
        if (bondRequestQuoteDto != null) {
            if (bondRequestQuoteDto.getIbuQuoteBondDto() != null) {
                cfetsBondDealRequestQuoteDto.setQuoteId(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getQuoteId());
                cfetsBondDealRequestQuoteDto.setFinprodId(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getFinprodId());
                cfetsBondDealRequestQuoteDto.setPortfolioId(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getPortfolioId());
                cfetsBondDealRequestQuoteDto.setEntrustId(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getEntrustId());
                cfetsBondDealRequestQuoteDto.setSettleDate(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getSettleDate());
                if (bondRequestQuoteDto.getIbuQuoteBondDto().getSettleSpeed() == 0L) {
                    cfetsBondDealRequestQuoteDto.setSettleSpreed(
                        BussinessDictCodeList.清算速度_T加0.getValue());
                }
                else if (bondRequestQuoteDto.getIbuQuoteBondDto().getSettleSpeed() == 1L) {
                    cfetsBondDealRequestQuoteDto.setSettleSpreed(
                        BussinessDictCodeList.清算速度_T加1.getValue());
                }
                cfetsBondDealRequestQuoteDto.setSettleMethod(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getDeliveryType());
                cfetsBondDealRequestQuoteDto.setSettleType(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getSettType());
                cfetsBondDealRequestQuoteDto.setSettleCcy(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getSettCcy());
                cfetsBondDealRequestQuoteDto.setPs(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getPs());
                cfetsBondDealRequestQuoteDto.setAvaNum(ArithmeticUtil.multiply(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getShareAmt(), new BigDecimal(100)));
                cfetsBondDealRequestQuoteDto.setMySide(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getOuPartyId());
                cfetsBondDealRequestQuoteDto.setMySideTrader(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getOuTraderId());
                cfetsBondDealRequestQuoteDto.setRemark(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getRemark());
                // add by yufei 2020-06-01 有效时间返回 .start
                cfetsBondDealRequestQuoteDto.setEffDate(DateUtil.format(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getEffTime(), "HH:mm"));
                // add by yufei 2020-06-01 有效时间返回 .end
                if (!StringUtil.isEmptyList(bondRequestQuoteDto.getIbuQuoteBondDetDtoList())) {
                    List<CounterInfoDto> list = new ArrayList<>();
                    for (IbuQuoteBondDetDto ibuQuoteBondDetDto : bondRequestQuoteDto.getIbuQuoteBondDetDtoList()) {
                        CounterInfoDto counterInfoDto = new CounterInfoDto();
                        BeanUtil.copyPropertiesIgnoreNull(counterInfoDto, ibuQuoteBondDetDto);
                        counterInfoDto.setYield(
                            this.yieldHandle(counterInfoDto.getYield(), false));
                        counterInfoDto.setExerYield(
                            this.yieldHandle(counterInfoDto.getExerYield(), false));
                        list.add(counterInfoDto);
                    }
                    cfetsBondDealRequestQuoteDto.setCounterInfoList(list);
                }
                cfetsBondDealRequestQuoteDto.setQuoteRoleType(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getQuoteRoleType());
                cfetsBondDealRequestQuoteDto.setSendStatus(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getSendStatus());
                cfetsBondDealRequestQuoteDto.setTradeStatus(
                    bondRequestQuoteDto.getIbuQuoteBondDto().getTradeStatus());
                cfetsBondDealRequestQuoteDto.setRemark(
                		bondRequestQuoteDto.getIbuQuoteBondDto().getRemark());
                cfetsBondDealRequestQuoteDto.setDealFeeAmt(bondRequestQuoteDto.getIbuQuoteBondDto().getDealFeeAmt());
                cfetsBondDealRequestQuoteDto.setSettFeeAmt(bondRequestQuoteDto.getIbuQuoteBondDto().getSettFeeAmt());
                cfetsBondDealRequestQuoteDto.setDistribution(bondRequestQuoteDto.getIbuQuoteBondDto().getDipType());
            }
            
         // add by etliu 20210309 银行间上下行多组合 start
            if (!StringUtil.isEmptyList(bondRequestQuoteDto.getIbuQuoteEtrReDtoList())) {
                // 组合分配信息
                List<CfetsBondDealRequestQuoteDto> cfetsBondDealRequestQuoteDtoList = new ArrayList<CfetsBondDealRequestQuoteDto>();
                for (IbuQuoteEtrReDto ibuQuoteEtrReDto : bondRequestQuoteDto.getIbuQuoteEtrReDtoList()) {
                    CfetsBondDealRequestQuoteDto tempDto = new CfetsBondDealRequestQuoteDto();
                    tempDto.setQuoteId(ibuQuoteEtrReDto.getQuoteId());
                    tempDto.setEntrustId(ibuQuoteEtrReDto.getEntrustId());
                    tempDto.setPortfolioId(ibuQuoteEtrReDto.getPortfolioId());
                    tempDto.setAvaNum(ArithmeticUtil.divide(ibuQuoteEtrReDto.getShareAmt(),
                        BigDecimal.valueOf(10000), 2));
                    cfetsBondDealRequestQuoteDtoList.add(tempDto);
                }
                cfetsBondDealRequestQuoteDto.setCfetsBondDealRequestQuoteDtoList(
                    cfetsBondDealRequestQuoteDtoList);
            }
         // add by etliu 20210309 银行间上下行多组合 end
        }

        return cfetsBondDealRequestQuoteDto;
    }
    // add by wangzhuangzhuang 报价双击查看详情 20200521 end

    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteExtDto> getDayOfferTableList(Map<String, Object> param) {
        // 查询条件转换
        BondQuoteSearchDto inputBean = new BondQuoteSearchDto();

        // 直接根据内部报价编号查询不需要加其他查询条件
        if (param.get("quoteId") != null && !"".equals(param.get("quoteId"))) {
            inputBean.setQuoteId(param.get("quoteId").toString());
            // 报价日期
            inputBean.setQuoteDate(iPlatformDate.getSysDate());
        }
        // 根据画面查询条件过滤
        else {
            // 交易方向
            inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
            // 资产代码
            inputBean.setFinprodFuzzy(
                param.get("finprodId") != null ? param.get("finprodId").toString() : null);

            // 报价编号
            if (StringUtil.isNotEmpty(param.get("quoteType"))) {
                inputBean.setQuoteType(param.get("quoteType").toString());
            }

            if (StringUtil.isNotEmpty(param.get("finprodIdList"))) {
            	inputBean.setFinprodIdList(Arrays.asList(param.get("finprodIdList").toString().split(",")));
            	inputBean.setFinprodId(null);
            	inputBean.setFinprodFuzzy(null);
            }
            // 本方交易员
            if (StringUtil.isNotEmpty(param.get("mySideTrader"))) {
                inputBean.setOuTraderIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                    param.get("mySideTrader").toString()));
            }
            // 对手方
            inputBean.setCpPartyId(
                param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
            if (StringUtil.isNotEmpty(param.get("counterId"))) {
            	inputBean.setCpPartyId(param.get("counterId").toString());
            }
            if (StringUtil.isNotEmpty(param.get("counterTrader"))) {
            	inputBean.setCpPartyTrade(param.get("counterTrader").toString());
            }
            // 报价编号
            if (StringUtil.isNotEmpty(param.get("cfetsQuoteId"))) {
                inputBean.setCfetsQuoteIdFuzzy(param.get("cfetsQuoteId").toString());
            }
            // 报价日期
            inputBean.setQuoteDate(iPlatformDate.getSysDate());
            // 是否全数据查询角色(管理员true， 非管理员false)
            if (param.get("isAdmin") != null) {
                if ((boolean)param.get("isAdmin")) {
                    inputBean.setAutoDataRole(true);
                }
                else {
                    inputBean.setAutoDataRole(false);
                }
            }
            // 登录用户ID Modified by JIAOXUJIN on 2020-06-03
            inputBean.setLoginUserId(MapUtils.getString(param, "loginUserId"));
            // 待回复数据查询方式
            if (param.get("unAnswerFlag") != null) {
                inputBean.setReplyQueryTypeIbu(
                    BussinessDictCodeList.银行间上行回复查询方式_全部交易员可见.getValue());
                if (StringUtil.equals(param.get("unAnswerFlag"), "N")) {
                    inputBean.setReplyQueryTypeIbu(
                        BussinessDictCodeList.银行间上行回复查询方式_指定交易员可见.getValue());
                    inputBean.setLoginTraderId(MapUtils.getString(param, "loginTraderId"));
                }
            }
            // 需要过滤待回复的做市报价
            inputBean.setIsZsbjFilter("true");
            // 排序处理
            // inputBean.setOrderField("");
            // inputBean.setOrderDirection("");

        }
        // 报价状态列表
        if (param.get("dayOfferStatus") != null) {
            List<String> tradeStatusList = new ArrayList<>();
            if (StringUtil.equals(param.get("dayOfferStatus"), "undo")) {
                tradeStatusList.add("NA");// 此处特殊处理，组件那边判断取tradeStatus为空的记录
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_新增中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已新增.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_修改中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已修改.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_待回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_终止中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已拒绝.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_拒绝中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_回复中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_交谈中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue());
            }
            else if (StringUtil.equals(param.get("dayOfferStatus"), "done")) {
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已成交.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已终止.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已过期.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
            }

            inputBean.setTradeStatusList(tradeStatusList);
            if(null != param.get("keywords")){
                inputBean.setKeywords(param.get("keywords").toString());
            }

        }
        //针对前后端分离 高级查询
        if(null!=param.get("pss")){
            inputBean.setPss((List<String>)param.get("pss"));
        }
        if(null!=param.get("finprodMarketIds")){
            inputBean.setFinprodMarketIds((List<String>)param.get("finprodMarketIds"));
        }
        if(null!=param.get("counterIds")){
            inputBean.setCounterIds((List<String>)param.get("counterIds"));
        }
        if (param.get("mySideTraders") != null && !"".equals(param.get("mySideTraders"))) {
            inputBean.setMySideTraders((List<String>)param.get("mySideTraders"));
        }
        if (param.get("counterTraders") != null && !"".equals(param.get("counterTraders"))) {
            inputBean.setCounterTraders((List<String>)param.get("counterTraders"));
        }
        return ibuMainService.getBondQuoteList(inputBean, null);
    }

    /**
     * 质押券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<RepoQuoteExtDto> getRepoDayOfferTableList(Map<String, Object> param) {
        // 查询条件转换
        RepoQuoteSearchDto inputBean = new RepoQuoteSearchDto();

        // 交易方向
        inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
        // 交易方向List
        inputBean.setPsList(param.get("psList") != null ? (List)param.get("psList") : null);
        // 资产代码
        inputBean.setFinprodId(
            param.get("finprodId") != null ? param.get("finprodId").toString() : null);
        // 本方交易员
        if (StringUtil.isNotEmpty(param.get("mySideTrader"))) {
            inputBean.setOuTraderIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                param.get("mySideTrader").toString()));
        }
        // 对手方
        inputBean.setCpPartyId(
            param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("quoteId"))) {
            inputBean.setQuoteId(param.get("quoteId").toString());
        }
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("cfetsQuoteId"))) {
            inputBean.setCfetsQuoteIdFuzzy(param.get("cfetsQuoteId").toString());
        }
        // 报价日期
        inputBean.setQuoteDate(iPlatformDate.getSysDate());
        // 是否全数据查询角色(管理员true， 非管理员false)
        if (param.get("isAdmin") != null) {
            if ((boolean)param.get("isAdmin")) {
                inputBean.setAutoDataRole(true);
            }
            else {
                inputBean.setAutoDataRole(false);
            }
        }
        if (StringUtil.isNotEmpty(param.get("counterId"))) {
            inputBean.setCounterIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                param.get("counterId").toString()));
        }
        if (StringUtil.isNotEmpty(param.get("counterTrader"))) {
            inputBean.setCounterTraderList(StringUtil.splitToList(GlobalConstant.COMMA,
                param.get("counterTrader").toString()));
        }
        if (StringUtil.isNotEmpty(param.get("term"))) {
            inputBean.setTerm(param.get("term").toString());
        }
        //add by chenjiaxing 20210816 start
        if (StringUtil.isNotEmpty(param.get("termStart"))) {
            inputBean.setTermStart(param.get("termStart").toString());
        }
        if (StringUtil.isNotEmpty(param.get("termEnd"))) {
            inputBean.setTermEnd(param.get("termEnd").toString());
        }
        if (StringUtil.isNotEmpty(param.get("ouPartyId"))) {
            inputBean.setOuPartyIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                    param.get("ouPartyId").toString()));
        }
        if(StringUtil.isNotEmpty(param.get("benfangSide"))){
            String benfangSide = param.get("benfangSide").toString();
            if(!StringUtil.equals(benfangSide,"whole")){
                inputBean.setPs(benfangSide);
            }
        }
        //add by chenjiaxing 20210816 end
        // 排序
        if (StringUtil.isNotEmpty(param.get("sort"))) {
            inputBean.setSort(param.get("sort").toString());
        }
        if (StringUtil.isNotEmpty(param.get("direction"))) {
            inputBean.setDirection(param.get("direction").toString());
        }

        // 登录用户ID Modified by JIAOXUJIN on 2020-06-03
        inputBean.setLoginUserId(MapUtils.getString(param, "loginUserId"));
        // 待回复数据查询方式
        if (param.get("unAnswerFlag") != null) {
            inputBean.setReplyQueryTypeIbu(BussinessDictCodeList.银行间上行回复查询方式_全部交易员可见.getValue());
            if (StringUtil.equals(param.get("unAnswerFlag"), "N")) {
                inputBean.setReplyQueryTypeIbu(
                    BussinessDictCodeList.银行间上行回复查询方式_指定交易员可见.getValue());
                inputBean.setLoginTraderId(MapUtils.getString(param, "loginTraderId"));
            }
        }
        // 报价状态列表
        if (param.get("dayOfferStatus") != null) {
            List<String> tradeStatusList = new ArrayList<>();
            if (StringUtil.equals(param.get("dayOfferStatus"), "undo")) {
                tradeStatusList.add("NA");// 此处特殊处理，组件那边判断取tradeStatus为空的记录
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_新增中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已新增.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_修改中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已修改.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_待回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_终止中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已拒绝.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_拒绝中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_回复中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_交谈中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue());

            }
            else if (StringUtil.equals(param.get("dayOfferStatus"), "done")) {
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已成交.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已终止.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已过期.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
            }

            inputBean.setTradeStatusList(tradeStatusList);
        }
     // 交易方向
        inputBean.setKeywords(param.get("keywords") != null ? param.get("keywords").toString() : null);

        //add by chenjiaixing 质押式回购下单管理改造
        String isGetBondCode = "";
        if (StringUtil.isNotEmpty(param.get("isGetBondCode"))) {
            isGetBondCode = param.get("isGetBondCode").toString();
        }
        if(StringUtil.equals(isGetBondCode,"Y")){
            return ibuMainService.getRepoDialogueQuoteList2(inputBean, null);
        }
        //add by chenjiaxing 质押式回购下单管理改造
        return ibuMainService.getRepoDialogueQuoteList(inputBean, null);
    }

    /**
     * 质押券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     *
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public com.joyintech.background.component.common.pack.common.grid.Page<RepoQuoteExtDto> getRepoDayOfferTablePage(Map<String, Object> param) {
        // 查询条件转换
        RepoQuoteSearchDto inputBean = new RepoQuoteSearchDto();
        int pageNo = 1;
        int pageSize = 10;
        if (param.get("pageNo") != null) {
            pageNo = (int) param.get("pageNo");
        }
        if (param.get("pageSize") != null) {
            pageSize = (int) param.get("pageSize");
        }
        // 交易方向
        inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
        // 交易方向List
        inputBean.setPsList(param.get("psList") != null ? (List)param.get("psList") : null);
        // 资产代码
        inputBean.setFinprodId(
                param.get("finprodId") != null ? param.get("finprodId").toString() : null);
        // 本方交易员
        if (StringUtil.isNotEmpty(param.get("mySideTrader"))) {
            inputBean.setOuTraderIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                    param.get("mySideTrader").toString()));
        }
        // 对手方
        inputBean.setCpPartyId(
                param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("quoteId"))) {
            inputBean.setQuoteId(param.get("quoteId").toString());
        }
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("cfetsQuoteId"))) {
            inputBean.setCfetsQuoteIdFuzzy(param.get("cfetsQuoteId").toString());
        }
        // 报价日期
        inputBean.setQuoteDate(iPlatformDate.getSysDate());
        // 是否全数据查询角色(管理员true， 非管理员false)
        if (param.get("isAdmin") != null) {
            if ((boolean)param.get("isAdmin")) {
                inputBean.setAutoDataRole(true);
            }
            else {
                inputBean.setAutoDataRole(false);
            }
        }
        if (StringUtil.isNotEmpty(param.get("counterId"))) {
            inputBean.setCounterIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                    param.get("counterId").toString()));
        }
        if (StringUtil.isNotEmpty(param.get("counterTrader"))) {
            inputBean.setCounterTraderList(StringUtil.splitToList(GlobalConstant.COMMA,
                    param.get("counterTrader").toString()));
        }
        if (StringUtil.isNotEmpty(param.get("term"))) {
            inputBean.setTerm(param.get("term").toString());
        }
        //add by chenjiaxing 20210816 start
        if (StringUtil.isNotEmpty(param.get("termStart"))) {
            inputBean.setTermStart(param.get("termStart").toString());
        }
        if (StringUtil.isNotEmpty(param.get("termEnd"))) {
            inputBean.setTermEnd(param.get("termEnd").toString());
        }
        if (StringUtil.isNotEmpty(param.get("ouPartyId"))) {
            inputBean.setOuPartyIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                    param.get("ouPartyId").toString()));
        }
        if(StringUtil.isNotEmpty(param.get("benfangSide"))){
            String benfangSide = param.get("benfangSide").toString();
            if(!StringUtil.equals(benfangSide,"whole")){
                inputBean.setPs(benfangSide);
            }
        }
        //add by chenjiaxing 20210816 end
        // 排序
        if (StringUtil.isNotEmpty(param.get("sort"))) {
            inputBean.setSort(param.get("sort").toString());
        }
        if (StringUtil.isNotEmpty(param.get("direction"))) {
            inputBean.setDirection(param.get("direction").toString());
        }

        // 登录用户ID Modified by JIAOXUJIN on 2020-06-03
        inputBean.setLoginUserId(MapUtils.getString(param, "loginUserId"));
        // 待回复数据查询方式
        if (param.get("unAnswerFlag") != null) {
            inputBean.setReplyQueryTypeIbu(BussinessDictCodeList.银行间上行回复查询方式_全部交易员可见.getValue());
            if (StringUtil.equals(param.get("unAnswerFlag"), "N")) {
                inputBean.setReplyQueryTypeIbu(
                        BussinessDictCodeList.银行间上行回复查询方式_指定交易员可见.getValue());
                inputBean.setLoginTraderId(MapUtils.getString(param, "loginTraderId"));
            }
        }
        // 报价状态列表
        if (param.get("dayOfferStatus") != null) {
            List<String> tradeStatusList = new ArrayList<>();
            if (StringUtil.equals(param.get("dayOfferStatus"), "undo")) {
                tradeStatusList.add("NA");// 此处特殊处理，组件那边判断取tradeStatus为空的记录
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_新增中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已新增.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_修改中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已修改.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_待回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_终止中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已拒绝.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_拒绝中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_回复中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_交谈中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue());

            }
            else if (StringUtil.equals(param.get("dayOfferStatus"), "done")) {
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已成交.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已终止.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已过期.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
            }

            inputBean.setTradeStatusList(tradeStatusList);
        }
        // 交易方向
        inputBean.setKeywords(param.get("keywords") != null ? param.get("keywords").toString() : null);

        //add by chenjiaixing 质押式回购下单管理改造
        String isGetBondCode = "";
        if (StringUtil.isNotEmpty(param.get("isGetBondCode"))) {
            isGetBondCode = param.get("isGetBondCode").toString();
        }
        if (StringUtil.equals(isGetBondCode, "Y")) {
            return ibuMainService.getRepoDialogueQuoteList2(pageNo, pageSize, inputBean, null);
        }
        //add by chenjiaxing 质押式回购下单管理改造
        return ibuMainService.getRepoDialogueQuoteList(pageNo, pageSize, inputBean, null);
    }

    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteExtDto> getOrderOfferTableList(Map<String, Object> param) {
        // 查询条件转换
        BondQuoteSearchDto inputBean = new BondQuoteSearchDto();
        // 委托代码
        inputBean.setEntrustId(
            param.get("entrustId") != null ? param.get("entrustId").toString() : null);

        return ibuMainService.getBondQuoteList(inputBean, null);
    }

    /**
     * 质押券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<RepoQuoteExtDto> getRepoOrderOfferTableList(Map<String, Object> param) {
        // 查询条件转换
        RepoQuoteSearchDto inputBean = new RepoQuoteSearchDto();
        // 委托代码
        inputBean.setEntrustId(
            param.get("entrustId") != null ? param.get("entrustId").toString() : null);

        return ibuMainService.getRepoDialogueQuoteList(inputBean, null);
    }

    /**
     * 现券报价查询-指定条件<br />
     * 根据传入的不为空的属性进行查询，需支持数据权限
     * 
     * @param inputBean BondQuoteSearchDto 现券报价查询条件
     * @param userInfo User 登录用户信息
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteExtDto> getMarketOfferTableList(Map<String, Object> param) {
        // 查询条件转换
        BondQuoteSearchDto inputBean = new BondQuoteSearchDto();
        // 交易方向
        inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
        // 资产代码
        inputBean.setFinprodFuzzy(
            param.get("finprodId") != null ? param.get("finprodId").toString() : null);
        // 对手方
        inputBean.setCpPartyId(
            param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("cfetsQuoteId"))) {
            inputBean.setCfetsQuoteIdFuzzy(param.get("cfetsQuoteId").toString());
        }
        // 内部报价编号
        inputBean.setQuoteId(
            param.get("quoteId") != null ? param.get("quoteId").toString() : null);
        // 本方角色列表
        inputBean.setQuoteRoleTypeList(
            Arrays.asList(BussinessDictCodeList.银行间上行本方角色_做市方.getValue()));
        // 交易状态
        inputBean.setTradeStatusList(
            Arrays.asList(BussinessDictCodeList.银行间上行报价状态_待回复.getValue()));
        // 报价日期
        inputBean.setQuoteDate(iPlatformDate.getSysDate());

        return ibuMainService.getBondQuoteList(inputBean, null);
    }

    /**
     * 质押券券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void insertRepoDealQuote(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        User user = this.genUserInfo(cfetsRepoDealQuotedPrice001Dto);
        RepoDialogueQuoteDto inputBean = this.convertRepoDialogueQuoteDto(
            cfetsRepoDealQuotedPrice001Dto);
        QuoteResultDto quoteResultDto = ibuMainService.insertRepoDialogueQuote(inputBean, user);

        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }
    
    /**
     * 质押券券对话报价新增<br />
     * 对话报价信息入库
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void insertWflRepoDealQuote(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        User user = this.genUserInfo(cfetsRepoDealQuotedPrice001Dto);
        RepoDialogueQuoteDto inputBean = this.convertWflRepoDialogueQuoteDto(cfetsRepoDealQuotedPrice001Dto);
        QuoteResultDto quoteResultDto = ibuMainService.insertWflRepoDialogueQuote(inputBean, user);
    }

    /**
     * 质押券券对话报价新增发送<br />
     * 对话报价信息入库
     * 
     * @param cfetsRepoDealQuotedPrice001Dto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendNewRepoDealQuote(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        User user = this.genUserInfo(cfetsRepoDealQuotedPrice001Dto);
        RepoDialogueQuoteDto inputBean = this.convertRepoDialogueQuoteDto(
            cfetsRepoDealQuotedPrice001Dto);
        QuoteResultDto quoteResultDto = ibuMainService.sendNewRepoDialogueQuote(inputBean, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }

    /**
     * 质押式回购报价dto转换 <功能详细描述>
     * 
     * @param cfetsRepoDealQuotedPrice001Dto
     * @return 质押式回购报价组件输入实体
     * @see
     * @since 1.0
     */
    public RepoDialogueQuoteDto convertRepoDialogueQuoteDto(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        RepoDialogueQuoteDto inputBean = new RepoDialogueQuoteDto();
        // region 银行间报价主信息
        IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
        // 报价编号（内部代码）
        if (StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getQuoteId())) {
            ibuQuoteCrepDto.setQuoteId(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
            
            // add by etliu 20210119 start
            // 若报价编号存在，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
            RepoDialogueQuoteDto dbRepoDialogueQuoteDto = ibuMainService.getRepoDialogueQuote(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
            if (dbRepoDialogueQuoteDto != null && dbRepoDialogueQuoteDto.getIbuQuoteCrepDto() != null) {
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrepDto, dbRepoDialogueQuoteDto.getIbuQuoteCrepDto());
            }
         // add by etliu 20210119 end
        }
        else {
            ibuQuoteCrepDto.setQuoteId(this.genBusId("IBU_003"));
        }
        ibuQuoteCrepDto.setQuoteType(BussinessDictCodeList.交易类型_对话报价.getValue()); // 报价类型，对话报价/请求报价
        ibuQuoteCrepDto.setpQuoteId(cfetsRepoDealQuotedPrice001Dto.getpQuoteId());//外部报价编号
        // ibuQuoteCrepDto.setExeType(exeType);// 操作类型
        ibuQuoteCrepDto.setCfetsQuoteId(cfetsRepoDealQuotedPrice001Dto.getCfetsQuoteId());//CFETS报价编号
        //默认为询价方
        if(StringUtil.isEmpty(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType())){
            ibuQuoteCrepDto.setQuoteRoleType(BussinessDictCodeList.银行间上行本方角色_询价方.getValue());// 本方角色
        }
        else {
            ibuQuoteCrepDto.setQuoteRoleType(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType());// 本方角色
        }
        ibuQuoteCrepDto.setPortfolioId(cfetsRepoDealQuotedPrice001Dto.getPortfolioId());// 组合代码
        ibuQuoteCrepDto.setEntrustId(cfetsRepoDealQuotedPrice001Dto.getEntrustId());// 委托代码
        ibuQuoteCrepDto.setQuoteDate(iPlatformDate.getSysDate());// 报价日期
        ibuQuoteCrepDto.setvSettleDate(cfetsRepoDealQuotedPrice001Dto.getvSettleDate());// 首期结算日
        ibuQuoteCrepDto.setmSettleDate(cfetsRepoDealQuotedPrice001Dto.getmSettleDate());// 到期结算日
        ibuQuoteCrepDto.setTermType(cfetsRepoDealQuotedPrice001Dto.getTermType());// 期限品种
        ibuQuoteCrepDto.setDealFeeAmt(cfetsRepoDealQuotedPrice001Dto.getDealFeeAmt());// 交易手续费
        ibuQuoteCrepDto.setSettFeeAmt(cfetsRepoDealQuotedPrice001Dto.getSettFeeAmt());// 结算手续费
        ibuQuoteCrepDto.setDipType(cfetsRepoDealQuotedPrice001Dto.getDipType());// 分配方式
        ibuQuoteCrepDto.setSecManageAcctId(cfetsRepoDealQuotedPrice001Dto.getSecAcctId());// 证券账户
        ibuQuoteCrepDto.setActDays(cfetsRepoDealQuotedPrice001Dto.getActDays());// 实际占款天数
        // 清算速度
        if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            ibuQuoteCrepDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            ibuQuoteCrepDto.setSettleSpeed(1L);
        }
        ibuQuoteCrepDto.setvDeliveryType(cfetsRepoDealQuotedPrice001Dto.getvDeliveryType());// 首期结算方式，券款对付、见券付款、见款付券
        ibuQuoteCrepDto.setmDeliveryType(cfetsRepoDealQuotedPrice001Dto.getmDeliveryType());// 到期结算方式，券款对付、见券付款、见款付券
        ibuQuoteCrepDto.setSettType(cfetsRepoDealQuotedPrice001Dto.getSettleType());// 清算类型
        ibuQuoteCrepDto.setSettCcy(cfetsRepoDealQuotedPrice001Dto.getSettleCcy());// 结算币种
        ibuQuoteCrepDto.setCcy(cfetsRepoDealQuotedPrice001Dto.getCcy());// 债券币种
        ibuQuoteCrepDto.setRepoPs(cfetsRepoDealQuotedPrice001Dto.getPs());// 回购方向
        ibuQuoteCrepDto.setRate(ArithmeticUtil.divide(cfetsRepoDealQuotedPrice001Dto.getPrice(),
            new BigDecimal(100), 6));// 回购利率 单位：%，精度：4位
        ibuQuoteCrepDto.setTerm(cfetsRepoDealQuotedPrice001Dto.getTerm());// 期限(天)
        // modify by zhangliang 2020-07-18 券面总额存表需要转成份额
//        ibuQuoteCrepDto.setShareAmt(ArithmeticUtil.multiply(cfetsRepoDealQuotedPrice001Dto.getPledgePrinTotal(), new BigDecimal(10000)));// 交易份额，债券数量等
        ibuQuoteCrepDto.setShareAmt(ArithmeticUtil.divide(cfetsRepoDealQuotedPrice001Dto.getPledgePrinTotal(), new BigDecimal(100),4));// 交易份额，债券数量等
        ibuQuoteCrepDto.setShareCtAmt(cfetsRepoDealQuotedPrice001Dto.getShareCtAmt());// 折算券面金额
        ibuQuoteCrepDto.setCbShareCtAmt(cfetsRepoDealQuotedPrice001Dto.getCbShareCtAmt());// 中债折算券面金额
        ibuQuoteCrepDto.setScShareCtAmt(cfetsRepoDealQuotedPrice001Dto.getScShareCtAmt());// 上清折算券面金额
        //ADD BY CHENJIAIXNG 质押式回购新加字段入库 20210901 start
        //中债登券面总额合计（万元） zzTotalAmt
        ibuQuoteCrepDto.setZzTotalAmt(cfetsRepoDealQuotedPrice001Dto.getZzTotalAmt());
        //上清所券面总额合计（万元） sqTotalAmt
        ibuQuoteCrepDto.setSqTotalAmt(cfetsRepoDealQuotedPrice001Dto.getSqTotalAmt());
        //ADD BY CHENJIAIXNG 质押式回购新加字段入库 20210901 end
        ibuQuoteCrepDto.setFpriceAmt(cfetsRepoDealQuotedPrice001Dto.getDealAmt());// 全价总额
        ibuQuoteCrepDto.setCbFpriceAmt(cfetsRepoDealQuotedPrice001Dto.getCbDealAmt());// 中债全价总额
        ibuQuoteCrepDto.setScFpriceAmt(cfetsRepoDealQuotedPrice001Dto.getScDealAmt());// 上清全价总额
        ibuQuoteCrepDto.setmIntAmt(cfetsRepoDealQuotedPrice001Dto.getmIntAmt());// 到期利息
        ibuQuoteCrepDto.setmTradeAmt(cfetsRepoDealQuotedPrice001Dto.getmTradeAmt());// 到期金额
        ibuQuoteCrepDto.setBasis(cfetsRepoDealQuotedPrice001Dto.getBasis());// 计息基础
        ibuQuoteCrepDto.setEffTime(
            DateUtil.parse(iPlatformDate.getSysDateStr() + " "
                           + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00"));// 有效时间
        ibuQuoteCrepDto.setOuPartyId(cfetsRepoDealQuotedPrice001Dto.getMySide());// 本方银行间机构，21位编号
        ibuQuoteCrepDto.setOuTraderId(cfetsRepoDealQuotedPrice001Dto.getMySideTrader());// 本方银行间交易员
        ibuQuoteCrepDto.setCpPartyId(cfetsRepoDealQuotedPrice001Dto.getCounterId());// 对手银行间机构，21位编号
        ibuQuoteCrepDto.setCpTraderId(cfetsRepoDealQuotedPrice001Dto.getCounterTrader());// 对手方银行间交易员
        // ibuQuoteCrepDto.setMyAcctNo(myAcctNo);//本方资金账户
        // ibuQuoteCrepDto.setSecManageAcctId(secManageAcctId);//证券管理户代码
        // ibuQuoteCrepDto.setExecid(execid);//成交编号
        //版本号和前置版本号带出   新增置为-1 其余由前台带出
        // 版本号
        ibuQuoteCrepDto.setVersion(null==cfetsRepoDealQuotedPrice001Dto.getVersion()?-1L:cfetsRepoDealQuotedPrice001Dto.getVersion());
        //前置版本号
        ibuQuoteCrepDto.setbVersion(null==cfetsRepoDealQuotedPrice001Dto.getbVersion()?-1L:cfetsRepoDealQuotedPrice001Dto.getbVersion());
        ibuQuoteCrepDto.setRemark(cfetsRepoDealQuotedPrice001Dto.getRemark());// 备注
        inputBean.setIbuQuoteCrepDto(ibuQuoteCrepDto);
        // endregion

        // region 质押品信息
        List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = new ArrayList<IbuQuoteCrepPledgeDto>();
        if (!StringUtil.isEmptyList(cfetsRepoDealQuotedPrice001Dto.getPledgeBondList())) {
            for (CfetsRepoDealQuotedPrice003Dto dto : cfetsRepoDealQuotedPrice001Dto.getPledgeBondList()) {
                IbuQuoteCrepPledgeDto pledgeDto = new IbuQuoteCrepPledgeDto();
                pledgeDto.setQuoteId(dto.getQuoteId());// 报价编号
                pledgeDto.setPledgeFinprodId(dto.getFinprodId());// 金融产品代码
                pledgeDto.setBranch(0L);// 分支
                pledgeDto.setVersion(-1L);// 版本号
                // pledgeDto.setPledgeFaceValue(); // 质押面值
                pledgeDto.setPledgeNumber(ArithmeticUtil.divide(dto.getPledgePrin(), new BigDecimal(100), 4));// 质押数量
                pledgeDto.setPledgeRatio(ArithmeticUtil.divide(dto.getPledgeRate(), new BigDecimal(100), 4));// 质押率
                pledgeDto.setPledgeAmt(dto.getPledgePrin());// 质押金额
                pledgeDto.setUnitCprice(dto.getNetPrice());// 单位净价
                pledgeDto.setCpriceDate(dto.getValueDate());// 净价日期
                pledgeDto.setQuoteId(ibuQuoteCrepDto.getQuoteId());// 净价日期
                ibuQuoteCrepPledgeDtoList.add(pledgeDto);
            }
            inputBean.setIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
        }
        // endregion
        String isDzh = cfetsRepoDealQuotedPrice001Dto.getIsDzh();
        if(StringUtil.equals(isDzh,"Y")) {
            //质押券汇总
            Map<String, List<IbuQuoteCrepPledgeDto>> map = ibuQuoteCrepPledgeDtoList.stream().collect(Collectors.groupingBy(IbuQuoteCrepPledgeDto::getPledgeFinprodId));
            ibuQuoteCrepPledgeDtoList.clear();
            for (Map.Entry<String, List<IbuQuoteCrepPledgeDto>> entry : map.entrySet()) {
                IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto = new IbuQuoteCrepPledgeDto();
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrepPledgeDto,entry.getValue().get(0));
                ibuQuoteCrepPledgeDto.setPledgeNumber(entry.getValue().stream().map(IbuQuoteCrepPledgeDto::getPledgeNumber).reduce(BigDecimal.ZERO, BigDecimal::add));
                ibuQuoteCrepPledgeDto.setPledgeAmt(entry.getValue().stream().map(IbuQuoteCrepPledgeDto::getPledgeAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
                ibuQuoteCrepPledgeDtoList.add(ibuQuoteCrepPledgeDto);
            }
            inputBean.setIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
            inputBean.setIsDzh(isDzh);
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList();
            //银行间报价指令关系
            List<IbuQuoteEtrReDto> ibuQuoteEtrRes = new ArrayList<>();
            ptlAllocations.forEach(item -> {
                IbuQuoteEtrReDto ibuQuoteEtrRe = new IbuQuoteEtrReDto();
                ibuQuoteEtrRe.setQuoteId(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
                ibuQuoteEtrRe.setVersion(-1L);// 版本号
                ibuQuoteEtrRe.setEntrustId(item.getEntrustId());
                ibuQuoteEtrRe.setPortfolioId(item.getPortfolioId());
                ibuQuoteEtrRe.setShareAmt(ArithmeticUtil.divide(item.getShareAmt(),new BigDecimal(100),4));
                ibuQuoteEtrRe.setShareCtAmt(item.getShareCtAmt());
                ibuQuoteEtrRe.setFpriceAmt(item.getFpriceAmt());
                ibuQuoteEtrRe.setCbFpriceAmt(item.getCbFpriceAmt());
                ibuQuoteEtrRe.setScFpriceAmt(item.getScFpriceAmt());
                ibuQuoteEtrRe.setCpriceAmt(item.getcPriceAmt());
                ibuQuoteEtrRe.setIntAmt(item.getIntAmt());
                ibuQuoteEtrRe.setDealFeeAmt(item.getDealFeeAmt());
                ibuQuoteEtrRe.setSettFeeAmt(item.getSettFeeAmt());
                ibuQuoteEtrRe.setPriceModel(item.getPriceModel());
                ibuQuoteEtrRe.setMtradeAmt(item.getMtradeAmt());
                ibuQuoteEtrRes.add(ibuQuoteEtrRe);
            });
            inputBean.setIbuQuoteEtrReDtoList(ibuQuoteEtrRes);
            List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = 质押券拆分(cfetsRepoDealQuotedPrice001Dto);
            inputBean.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteEntCrepPledges);
        }
        return inputBean;
    }
    
    /**
     * 质押式回购报价dto转换 <功能详细描述>
     * 
     * @param cfetsRepoDealQuotedPrice001Dto
     * @return 质押式回购报价组件输入实体
     * @see
     * @since 1.0
     */
    private RepoDialogueQuoteDto convertWflRepoDialogueQuoteDto(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        RepoDialogueQuoteDto inputBean = new RepoDialogueQuoteDto();
        // region 银行间报价主信息
        WflIbuQuoteCrepDto ibuQuoteCrepDto = new WflIbuQuoteCrepDto();
        
        // 报价编号（内部代码）
        if (StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getQuoteId())) {
            ibuQuoteCrepDto.setQuoteId(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
            
         // add by etliu 20210119 start
            // 若报价编号存在，则先查询数据库中数据，以数据库中数据作为基准，将画面内容更新到dto中
            RepoDialogueQuoteDto dbRepoDialogueQuoteDto = ibuMainService.getRepoDialogueQuote(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
            if (dbRepoDialogueQuoteDto != null && dbRepoDialogueQuoteDto.getIbuQuoteCrepDto() != null) {
                BeanUtil.copyPropertiesIgnoreNull(ibuQuoteCrepDto, dbRepoDialogueQuoteDto.getIbuQuoteCrepDto());
            }
         // add by etliu 20210119 end
        }
        else {
            ibuQuoteCrepDto.setQuoteId(this.genBusId("IBU_003"));
        }
        ibuQuoteCrepDto.setQuoteType(BussinessDictCodeList.交易类型_对话报价.getValue()); // 报价类型，对话报价/请求报价
        ibuQuoteCrepDto.setpQuoteId(cfetsRepoDealQuotedPrice001Dto.getQuoteId());//外部报价编号
        // ibuQuoteCrepDto.setExeType(exeType);// 操作类型
        ibuQuoteCrepDto.setCfetsQuoteId(cfetsRepoDealQuotedPrice001Dto.getCfetsQuoteId());//CFETS报价编号
        ibuQuoteCrepDto.setQuoteRoleType(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType());
        //默认为询价方
        if(StringUtil.isEmpty(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType())){
            ibuQuoteCrepDto.setQuoteRoleType(BussinessDictCodeList.银行间上行本方角色_询价方.getValue());// 本方角色
        }else{
            ibuQuoteCrepDto.setQuoteRoleType(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType());
        }
        ibuQuoteCrepDto.setPortfolioId(cfetsRepoDealQuotedPrice001Dto.getPortfolioId());// 组合代码
        ibuQuoteCrepDto.setEntrustId(cfetsRepoDealQuotedPrice001Dto.getEntrustId());// 委托代码
        ibuQuoteCrepDto.setQuoteDate(iPlatformDate.getSysDate());// 报价日期
        ibuQuoteCrepDto.setvSettleDate(cfetsRepoDealQuotedPrice001Dto.getvSettleDate());// 首期结算日
        ibuQuoteCrepDto.setmSettleDate(cfetsRepoDealQuotedPrice001Dto.getmSettleDate());// 到期结算日
        ibuQuoteCrepDto.setTermType(cfetsRepoDealQuotedPrice001Dto.getTermType());// 期限品种
        ibuQuoteCrepDto.setDealFeeAmt(cfetsRepoDealQuotedPrice001Dto.getDealFeeAmt());// 交易手续费
        ibuQuoteCrepDto.setSettFeeAmt(cfetsRepoDealQuotedPrice001Dto.getSettFeeAmt());// 结算手续费
        ibuQuoteCrepDto.setDipType(cfetsRepoDealQuotedPrice001Dto.getDipType());// 分配方式
        ibuQuoteCrepDto.setSecManageAcctId(cfetsRepoDealQuotedPrice001Dto.getSecAcctId());// 证券账户
        ibuQuoteCrepDto.setActDays(cfetsRepoDealQuotedPrice001Dto.getActDays());// 实际占款天数
        // 清算速度
        if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加0.getValue())) {
            ibuQuoteCrepDto.setSettleSpeed(0L);
        }
        else if (StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getSettleSpreed(),
            BussinessDictCodeList.清算速度_T加1.getValue())) {
            ibuQuoteCrepDto.setSettleSpeed(1L);
        }
        ibuQuoteCrepDto.setvDeliveryType(cfetsRepoDealQuotedPrice001Dto.getvDeliveryType());// 首期结算方式，券款对付、见券付款、见款付券
        ibuQuoteCrepDto.setmDeliveryType(cfetsRepoDealQuotedPrice001Dto.getmDeliveryType());// 到期结算方式，券款对付、见券付款、见款付券
        ibuQuoteCrepDto.setSettType(cfetsRepoDealQuotedPrice001Dto.getSettleType());// 清算类型
        ibuQuoteCrepDto.setSettCcy(cfetsRepoDealQuotedPrice001Dto.getSettleCcy());// 结算币种
        ibuQuoteCrepDto.setCcy(cfetsRepoDealQuotedPrice001Dto.getCcy());// 债券币种
        ibuQuoteCrepDto.setRepoPs(cfetsRepoDealQuotedPrice001Dto.getPs());// 回购方向
        ibuQuoteCrepDto.setRate(ArithmeticUtil.divide(cfetsRepoDealQuotedPrice001Dto.getPrice(),
            new BigDecimal(100), 6));// 回购利率 单位：%，精度：4位
        ibuQuoteCrepDto.setTerm(cfetsRepoDealQuotedPrice001Dto.getTerm());// 期限(天)
        // modify by zhangliang 2020-07-18 券面总额存表需要转成份额
//        ibuQuoteCrepDto.setShareAmt(ArithmeticUtil.multiply(cfetsRepoDealQuotedPrice001Dto.getPledgePrinTotal(), new BigDecimal(10000)));// 交易份额，债券数量等
        ibuQuoteCrepDto.setShareAmt(ArithmeticUtil.divide(cfetsRepoDealQuotedPrice001Dto.getPledgePrinTotal(), new BigDecimal(100),4));// 交易份额，债券数量等
        ibuQuoteCrepDto.setShareCtAmt(cfetsRepoDealQuotedPrice001Dto.getShareCtAmt());// 折算券面金额
        ibuQuoteCrepDto.setCbShareCtAmt(cfetsRepoDealQuotedPrice001Dto.getCbShareCtAmt());// 中债折算券面金额
        ibuQuoteCrepDto.setScShareCtAmt(cfetsRepoDealQuotedPrice001Dto.getScShareCtAmt());// 上清折算券面金额
        ibuQuoteCrepDto.setFpriceAmt(cfetsRepoDealQuotedPrice001Dto.getDealAmt());// 全价总额
        ibuQuoteCrepDto.setCbFpriceAmt(cfetsRepoDealQuotedPrice001Dto.getCbDealAmt());// 中债全价总额
        ibuQuoteCrepDto.setScFpriceAmt(cfetsRepoDealQuotedPrice001Dto.getScDealAmt());// 上清全价总额
        ibuQuoteCrepDto.setmIntAmt(cfetsRepoDealQuotedPrice001Dto.getmIntAmt());// 到期利息
        ibuQuoteCrepDto.setmTradeAmt(cfetsRepoDealQuotedPrice001Dto.getmTradeAmt());// 到期金额
        ibuQuoteCrepDto.setBasis(cfetsRepoDealQuotedPrice001Dto.getBasis());// 计息基础
        // ibuQuoteCrepDto.setTradeStatus(tradeStatus);//报价状态，新增中/已新增/修改中/已修改/撤回中/已撤回/成交中/已成交/终止中/已终止/待回复/已撤销/
        // ibuQuoteCrepDto.setSendStatus(sendStatus);//发送状态，待发送/成功/失败/发送中
        //处理交易状态和发送状态
        IbuQuoteCrepDto ibuQuoteCrepDtoExt = handleStatus(cfetsRepoDealQuotedPrice001Dto);
        ibuQuoteCrepDto.setTradeStatus(ibuQuoteCrepDtoExt.getTradeStatus());
        ibuQuoteCrepDto.setSendStatus(ibuQuoteCrepDtoExt.getSendStatus());
        ibuQuoteCrepDto.setEffTime(
            DateUtil.parse(iPlatformDate.getSysDateStr() + " "
                           + CacheUtil.getSysParam("ibuVar_quoteEffTime") + ":00"));// 有效时间
        ibuQuoteCrepDto.setOuPartyId(cfetsRepoDealQuotedPrice001Dto.getMySide());// 本方银行间机构，21位编号
        ibuQuoteCrepDto.setOuTraderId(cfetsRepoDealQuotedPrice001Dto.getMySideTrader());// 本方银行间交易员
        ibuQuoteCrepDto.setCpPartyId(cfetsRepoDealQuotedPrice001Dto.getCounterId());// 对手银行间机构，21位编号
        ibuQuoteCrepDto.setCpTraderId(cfetsRepoDealQuotedPrice001Dto.getCounterTrader());// 对手方银行间交易员
        // ibuQuoteCrepDto.setMyAcctNo(myAcctNo);//本方资金账户
        // ibuQuoteCrepDto.setSecManageAcctId(secManageAcctId);//证券管理户代码
        // ibuQuoteCrepDto.setExecid(execid);//成交编号
        //版本号和前置版本号带出   新增置为-1 其余由前台带出
        // 版本号
        ibuQuoteCrepDto.setVersion(null==cfetsRepoDealQuotedPrice001Dto.getVersion()?-1L:cfetsRepoDealQuotedPrice001Dto.getVersion());
        //前置版本号
        ibuQuoteCrepDto.setbVersion(null==cfetsRepoDealQuotedPrice001Dto.getbVersion()?-1L:cfetsRepoDealQuotedPrice001Dto.getbVersion());
        ibuQuoteCrepDto.setRemark(cfetsRepoDealQuotedPrice001Dto.getRemark());// 备注
        ibuQuoteCrepDto.setWorkflowId(cfetsRepoDealQuotedPrice001Dto.getWorkflowId());
        ibuQuoteCrepDto.setWorkflowStatus(DictCodeListExt1.有效状态_流程中.getValue());
        inputBean.setWflIbuQuoteCrepDto(ibuQuoteCrepDto);
        // endregion

        // region 质押品信息
        List<CfetsRepoDealQuotedPrice003Dto>  pledgeBondLists =   cfetsRepoDealQuotedPrice001Dto.getPledgeBondList();
        List<WflIbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtoList = new ArrayList<WflIbuQuoteCrepPledgeDto>();
        if (!StringUtil.isEmptyList(pledgeBondLists)) {
            for (CfetsRepoDealQuotedPrice003Dto dto : pledgeBondLists) {
                WflIbuQuoteCrepPledgeDto pledgeDto = new WflIbuQuoteCrepPledgeDto();
                pledgeDto.setQuoteId(dto.getQuoteId());// 报价编号
                pledgeDto.setPledgeFinprodId(dto.getFinprodId());// 金融产品代码
                pledgeDto.setBranch(0L);// 分支
                pledgeDto.setVersion(-1L);// 版本号
                // pledgeDto.setPledgeFaceValue(); // 质押面值
                pledgeDto.setPledgeNumber(
                    ArithmeticUtil.divide(dto.getPledgePrin(), new BigDecimal(100),2));// 质押数量
                pledgeDto.setPledgeRatio(
                    ArithmeticUtil.divide(dto.getPledgeRate(), new BigDecimal(100), 4));// 质押率
                pledgeDto.setPledgeAmt(
                    ArithmeticUtil.multiply(dto.getPledgePrin(), new BigDecimal(1)));// 质押金额
                pledgeDto.setUnitCprice(dto.getNetPrice());// 单位净价
                pledgeDto.setCpriceDate(dto.getValueDate());// 净价日期
                pledgeDto.setQuoteId(ibuQuoteCrepDto.getQuoteId());// 净价日期
                pledgeDto.setWorkflowId(cfetsRepoDealQuotedPrice001Dto.getWorkflowId());
                pledgeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                ibuQuoteCrepPledgeDtoList.add(pledgeDto);
            }
            inputBean.setWflIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
        }
        // endregion
        //多组合  拆分
        String isDzh = cfetsRepoDealQuotedPrice001Dto.getIsDzh();
        if(StringUtil.equals(isDzh,"Y")) {
            //质押券汇总
            Map<String, List<WflIbuQuoteCrepPledgeDto>> map = ibuQuoteCrepPledgeDtoList.stream().collect(Collectors.groupingBy(WflIbuQuoteCrepPledgeDto::getPledgeFinprodId));
            ibuQuoteCrepPledgeDtoList.clear();
            for (Map.Entry<String, List<WflIbuQuoteCrepPledgeDto>> entry : map.entrySet()) {
                WflIbuQuoteCrepPledgeDto wflIbuQuoteCrepPledgeDto = new WflIbuQuoteCrepPledgeDto();
                BeanUtil.copyPropertiesIgnoreNull(wflIbuQuoteCrepPledgeDto,entry.getValue().get(0));
                wflIbuQuoteCrepPledgeDto.setPledgeNumber(entry.getValue().stream().map(WflIbuQuoteCrepPledgeDto::getPledgeNumber).reduce(BigDecimal.ZERO, BigDecimal::add));
                wflIbuQuoteCrepPledgeDto.setPledgeAmt(entry.getValue().stream().map(WflIbuQuoteCrepPledgeDto::getPledgeAmt).reduce(BigDecimal.ZERO, BigDecimal::add));
                ibuQuoteCrepPledgeDtoList.add(wflIbuQuoteCrepPledgeDto);
            }
            inputBean.setWflIbuQuoteCrepPledgeDtoList(ibuQuoteCrepPledgeDtoList);
            inputBean.setIsDzh(isDzh);
            List<PtlAllocationDto> ptlAllocations = cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList();
            //银行间报价指令关系
            List<WflIbuQuoteEtrReDto> wflIbuQuoteEtrRes = new ArrayList<>();
            List<WflIbuQuoteEntCrepPledgeDto> wflIbuQuoteEntCrepPledges = new ArrayList<>();
            ptlAllocations.forEach(item -> {
                WflIbuQuoteEtrReDto wflIbuQuoteEtrRe = new WflIbuQuoteEtrReDto();
                wflIbuQuoteEtrRe.setQuoteId(cfetsRepoDealQuotedPrice001Dto.getQuoteId());
                wflIbuQuoteEtrRe.setVersion(-1L);// 版本号
                wflIbuQuoteEtrRe.setEntrustId(item.getEntrustId());
                wflIbuQuoteEtrRe.setPortfolioId(item.getPortfolioId());
                wflIbuQuoteEtrRe.setShareAmt(item.getShareAmt());
                wflIbuQuoteEtrRe.setShareCtAmt(item.getShareCtAmt());
                wflIbuQuoteEtrRe.setFpriceAmt(item.getFpriceAmt());
                wflIbuQuoteEtrRe.setWorkflowId(cfetsRepoDealQuotedPrice001Dto.getWorkflowId());
                wflIbuQuoteEtrRe.setCpriceAmt(item.getcPriceAmt());
                wflIbuQuoteEtrRe.setIntAmt(item.getIntAmt());
                wflIbuQuoteEtrRe.setDealFeeAmt(item.getDealFeeAmt());
                wflIbuQuoteEtrRe.setSettFeeAmt(item.getSettFeeAmt());
                wflIbuQuoteEtrRe.setPriceModel(item.getPriceModel());
                wflIbuQuoteEtrRe.setMtradeAmt(item.getMtradeAmt());
                wflIbuQuoteEtrRes.add(wflIbuQuoteEtrRe);
            });
            inputBean.setWflIbuQuoteEtrReDtoList(wflIbuQuoteEtrRes);
            List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = 质押券拆分(cfetsRepoDealQuotedPrice001Dto);
            inputBean.setIbuQuoteEntCrepPledgeDtoList(ibuQuoteEntCrepPledges);
            if(null!=ibuQuoteEntCrepPledges){
                ibuQuoteEntCrepPledges.stream().forEach(item -> {
                    WflIbuQuoteEntCrepPledgeDto wflIbuQuoteEntCrepPledge = new WflIbuQuoteEntCrepPledgeDto();
                    BeanUtil.copyPropertiesIgnoreNull(wflIbuQuoteEntCrepPledge,item);
                    wflIbuQuoteEntCrepPledge.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                    wflIbuQuoteEntCrepPledge.setWorkflowId(cfetsRepoDealQuotedPrice001Dto.getWorkflowId());
                    wflIbuQuoteEntCrepPledges.add(wflIbuQuoteEntCrepPledge);
                });
            }
            inputBean.setWflIbuQuoteEntCrepPledgeDtoList(wflIbuQuoteEntCrepPledges);
        }
        return inputBean;
    }

    /**
     *功能描述:修改流程中交易状态、发送状态维护
     * @author zhangnian
     * @date 2021/1/6 0006
     * @param  * @param ibuQuoteCrepDto
     * @return void
    */
    private IbuQuoteCrepDto handleStatus(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto){
        IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
        DictCodeList tradeStatusDict = DictCodeList.from(IbuDictCode.银行间上行报价状态, StringUtil.isNotEmpty(cfetsRepoDealQuotedPrice001Dto.getTradeStatus())
                ? cfetsRepoDealQuotedPrice001Dto.getTradeStatus() : DictCodeList.银行间上行报价状态_新增中.getValue());
        //回复方
        if(StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getQuoteRoleType(),BussinessDictCodeList.银行间上行本方角色_回复方.getValue())) {
            if(StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getQuotaPageModel(), QuotaPageModelUtil.MODEL_EDIT)){
                ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_交谈中.getValue());
                switch (tradeStatusDict){
                    case 银行间上行报价状态_待回复:
                        ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方报价交谈.getValue());
                        break;
                    default:
                        ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方交谈修改.getValue());
                        break;
                }
            }
        }
        //询价方
        else{
                if(StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getQuotaPageModel(),QuotaPageModelUtil.MODEL_EDIT)){
                    switch (tradeStatusDict){
                        case 银行间上行报价状态_已新增 :
                            ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_修改中.getValue());
                            ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方报价新增.getValue());
                            break;
                        case 银行间上行报价状态_已修改 :
                            ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_修改中.getValue());
                            ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方报价修改.getValue());
                            break;
                        case 银行间上行报价状态_待回复:
                            ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_交谈中.getValue());
                            ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方报价交谈.getValue());
                            break;
                        case 银行间上行报价状态_已交谈:
                            ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_交谈中.getValue());
                            ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方交谈修改.getValue());
                            break;
                        //特殊处理:双加打开 修改
                        default:
                            ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_新增中.getValue());
                            ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方报价新增.getValue());
                            break;
                    }
                }
        }
        if(StringUtil.equals(cfetsRepoDealQuotedPrice001Dto.getQuotaPageModel(),QuotaPageModelUtil.MODEL_DIALOGUERESPONSE)){
            ibuQuoteCrepDto.setTradeStatus(DictCodeList.银行间上行报价状态_成交中.getValue());
            ibuQuoteCrepDto.setExeType(DictCodeList.银行间上行操作类型_本方回复成交.getValue());
        }
        //流程中、生效数据  发送状态均为待发送
        //发送状态由接口变更
        ibuQuoteCrepDto.setSendStatus(DictCodeList.银行间上行发送状态_待发送.getValue());
        return ibuQuoteCrepDto;
    }
    /**
     * 根据报价编号查询对话报价信息 <功能详细描述>
     * 
     * @param quoteId
     * @return
     * @see
     * @since 1.0
     */
    public CfetsRepoDealQuotedPrice001Dto queryRepoDialogueQuoteByQuoteId(String quoteId) {
        RepoDialogueQuoteDto repoDialogueQuoteDto = ibuMainService.getRepoDialogueQuote(quoteId);
        return repoDialogueQuoteDto == null ? null : this.convertRepoDialogueQuoteDto(
            repoDialogueQuoteDto);
    }

    /**
     * RepoDialogueQuoteDto 转换 CfetsRepoDealQuotedPrice001Dto <功能详细描述>
     * 
     * @param repoDialogueQuoteDto 组件质押券dto
     * @return 质押券对话报价dto
     * @see
     * @since 1.0
     */
    public CfetsRepoDealQuotedPrice001Dto convertRepoDialogueQuoteDto(RepoDialogueQuoteDto repoDialogueQuoteDto) {
        CfetsRepoDealQuotedPrice001Dto dto = new CfetsRepoDealQuotedPrice001Dto();
        // region 银行间报价主信息
        IbuQuoteCrepDto ibuQuoteCrepDto = repoDialogueQuoteDto.getIbuQuoteCrepDto();
        // 报价编号（内部代码）
        if (ibuQuoteCrepDto == null
                || StringUtil.isEmpty(ibuQuoteCrepDto.getQuoteId())) {
            return null;
        }
        dto.setQuoteId(ibuQuoteCrepDto.getQuoteId());
        dto.setPortfolioId(ibuQuoteCrepDto.getPortfolioId());// 组合代码
        dto.setEntrustId(ibuQuoteCrepDto.getEntrustId());// 委托代码
        dto.setvSettleDate(ibuQuoteCrepDto.getvSettleDate());// 首期结算日
        dto.setmSettleDate(ibuQuoteCrepDto.getmSettleDate());// 到期结算日
        // 清算速度
        if (ibuQuoteCrepDto.getSettleSpeed() == 0L) {
            dto.setSettleSpreed(BussinessDictCodeList.清算速度_T加0.getValue());
        }
        else if (ibuQuoteCrepDto.getSettleSpeed() == 1L) {
            dto.setSettleSpreed(BussinessDictCodeList.清算速度_T加1.getValue());
        }
        dto.setvDeliveryType(ibuQuoteCrepDto.getvDeliveryType());// 首期结算方式，券款对付、见券付款、见款付券
        dto.setmDeliveryType(ibuQuoteCrepDto.getmDeliveryType());// 到期结算方式，券款对付、见券付款、见款付券
        dto.setSettleType(ibuQuoteCrepDto.getSettType());// 清算类型
        dto.setSettleCcy(ibuQuoteCrepDto.getSettCcy());// 结算币种
        dto.setCcy(ibuQuoteCrepDto.getCcy());// 债券币种
        dto.setPs(ibuQuoteCrepDto.getRepoPs());// 回购方向
        dto.setPrice(ArithmeticUtil.multiply(ibuQuoteCrepDto.getRate(), new BigDecimal(100)));// 回购利率
                                                                                              // 单位：%，精度：4位
        dto.setTerm(ibuQuoteCrepDto.getTerm());// 期限(天)
        dto.setTermType(ibuQuoteCrepDto.getTermType());// 期限品种
        // modify by zhangliang 2020-07-18 份额需要转成券面
//        dto.setPledgePrinTotal(ibuQuoteCrepDto.getShareAmt());// 交易份额，债券数量等
        dto.setPledgePrinTotal(ArithmeticUtil.multiply(ibuQuoteCrepDto.getShareAmt(), new BigDecimal(100)));// 交易份额，债券数量等 -> 元
        dto.setAvaNum(ArithmeticUtil.divide(ibuQuoteCrepDto.getShareAmt(), new BigDecimal(10000), 2));
        dto.setCbShareCtAmt(ibuQuoteCrepDto.getCbShareCtAmt());// 中债折算券面金额
        dto.setScShareCtAmt(ibuQuoteCrepDto.getScShareCtAmt());// 上清折算券面金额
        //add by chenjiaxing 界面未展示中债上清面额 20210915 start
        if(ibuQuoteCrepDto.getZzTotalAmt() != null){
            dto.setZzTotalAmt(ibuQuoteCrepDto.getZzTotalAmt());
        }
        if(ibuQuoteCrepDto.getSqTotalAmt() != null){
            dto.setSqTotalAmt(ibuQuoteCrepDto.getSqTotalAmt());
        }
        //add by chenjiaxing 界面未展示中债上清面额 20210915 end
        dto.setDealAmt(ibuQuoteCrepDto.getFpriceAmt());// 全价总额
        // dto.setShareCtAmt(ibuQuoteCrepDto.getShareCtAmt());// 折算券面金额
        if (!StringUtil.isNotEmpty(dto.getShareCtAmt())) {
        	//dto.setShareCtAmt(ibuQuoteCrepDto.getFpriceAmt());// 全价总额
            //折算后
            dto.setShareCtAmt(ibuQuoteCrepDto.getShareCtAmt());
        }
        dto.setCbDealAmt(ibuQuoteCrepDto.getCbFpriceAmt());// 中债全价总额
        dto.setScDealAmt(ibuQuoteCrepDto.getScFpriceAmt());// 上清全价总额
        dto.setmIntAmt(ibuQuoteCrepDto.getmIntAmt());// 到期利息
        dto.setmTradeAmt(ibuQuoteCrepDto.getmTradeAmt());// 到期金额
        dto.setBasis(ibuQuoteCrepDto.getBasis());// 计息基础
        dto.setMySide(ibuQuoteCrepDto.getOuPartyId());// 本方银行间机构，21位编号
        dto.setMySideTrader(ibuQuoteCrepDto.getOuTraderId());// 本方银行间交易员
//        if (StringUtil.equals(ibuQuoteCrepDto.getExeType(), DictCodeList.银行间上行操作类型_对手方报价新增.getValue())) {
//        	dto.setMySideTrader(ibuQuoteCrepDto.getCreateUser());// 本方银行间交易员
//        }
        dto.setCounterId(ibuQuoteCrepDto.getCpPartyId());// 对手银行间机构，21位编号
        dto.setCounterTrader(ibuQuoteCrepDto.getCpTraderId());// 对手方银行间交易员
        dto.setTradeStatus(ibuQuoteCrepDto.getTradeStatus());
        dto.setSendStatus(ibuQuoteCrepDto.getSendStatus());
        dto.setQuoteRoleType(ibuQuoteCrepDto.getQuoteRoleType());
        dto.setCfetsQuoteId(ibuQuoteCrepDto.getCfetsQuoteId());
        dto.setVersion(ibuQuoteCrepDto.getVersion());
        dto.setbVersion(ibuQuoteCrepDto.getbVersion());
        dto.setpQuoteId(ibuQuoteCrepDto.getpQuoteId());
        dto.setExeType(ibuQuoteCrepDto.getExeType());
        dto.setDipType(ibuQuoteCrepDto.getDipType());
        dto.setDealFeeAmt(ibuQuoteCrepDto.getDealFeeAmt());
        dto.setSettFeeAmt(ibuQuoteCrepDto.getSettFeeAmt());
        dto.setSecAcctId(ibuQuoteCrepDto.getSecManageAcctId());
        dto.setActDays(ibuQuoteCrepDto.getActDays());
        dto.setRemark(ibuQuoteCrepDto.getRemark());
        // endregion

        // region 质押品信息
        if (!StringUtil.isEmptyList(repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList())) {
            List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList = new ArrayList<CfetsRepoDealQuotedPrice003Dto>();
            for (IbuQuoteCrepPledgeDto ibuQuoteCrepPledgeDto : repoDialogueQuoteDto.getIbuQuoteCrepPledgeDtoList()) {
                CfetsRepoDealQuotedPrice003Dto pledgeDto = new CfetsRepoDealQuotedPrice003Dto();
                BeanUtil.copyPropertiesIgnoreNull(pledgeDto,ibuQuoteCrepPledgeDto);
                pledgeDto.setQuoteId(ibuQuoteCrepPledgeDto.getQuoteId());// 报价编号
                pledgeDto.setBondCode(ibuQuoteCrepPledgeDto.getFinprodMarketId());
                pledgeDto.setBondAbbrName(ibuQuoteCrepPledgeDto.getFinprodAbbr());
                pledgeDto.setRepRegistOrg(ibuQuoteCrepPledgeDto.getRegistOrg());
                pledgeDto.setFinprodId(ibuQuoteCrepPledgeDto.getPledgeFinprodId());// 金融产品代码
                pledgeDto.setPledgeRate(ArithmeticUtil.multiply(
                    ibuQuoteCrepPledgeDto.getPledgeRatio(), new BigDecimal(100)));// 质押率
                //质押券替换后  单位为元
                pledgeDto.setPledgePrin(ArithmeticUtil.multiply(ibuQuoteCrepPledgeDto.getPledgeNumber(),new BigDecimal(100)));
                pledgeDto.setNetPrice(ibuQuoteCrepPledgeDto.getUnitCprice());// 单位净价
                pledgeDto.setValueDate(ibuQuoteCrepPledgeDto.getCpriceDate());// 净价日期
                pledgeDto.setQuoteId(ibuQuoteCrepDto.getQuoteId());// 净价日期
                pledgeBondList.add(pledgeDto);
            }
            dto.setPledgeBondList(pledgeBondList);
        }
        // endregion
        return dto;
    }

    /**
     * 现券买卖历史报价查询
     * 
     * @param param
     * @return List<BondQuoteExtDto>
     */
    public List<BondQuoteExtDto> queryHisDealList(Map<String, Object> param) {
        BondQuoteSearchDto inputBean = new BondQuoteSearchDto();
        // 组合代码
        if (StringUtil.isNotEmpty(param.get("portfolioId").toString())) {
            inputBean.setPortfolioIdList(Arrays.asList(param.get("portfolioId").toString()));
        }
        // 本方方向
        inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
        // 资产代码
        inputBean.setFinprodFuzzy(
            param.get("finprodId") != null ? param.get("finprodId").toString() : null);
        // 对手方
        inputBean.setCpPartyId(
            param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
        // 交易日期
        if (StringUtil.isNotEmpty(param.get("quoteDate"))) {
            inputBean.setQuoteDate((Date)param.get("quoteDate"));
        }
        // 成交状态
        if (StringUtil.isNotEmpty(param.get("tradeStatus").toString())) {
            inputBean.setTradeStatusList(Arrays.asList(param.get("tradeStatus").toString()));
        }
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("quoteId"))) {
            inputBean.setCfetsQuoteIdFuzzy(param.get("cfetsQuoteId").toString());
        }
        // 委托代码
        inputBean.setEntrustId(
            param.get("entrustId") != null ? param.get("entrustId").toString() : null);
        // 是否全数据查询角色(管理员true， 非管理员false)
        if (param.get("isAdmin") != null) {
            if ((boolean)param.get("isAdmin")) {
                inputBean.setAutoDataRole(true);
            }
            else {
                inputBean.setAutoDataRole(false);
            }
        }
        // 登录用户ID
        inputBean.setLoginUserId(
            param.get("loginUserId") != null ? param.get("loginUserId").toString() : null);

        return ibuMainService.getBondQuoteList(inputBean, null);
    }

    /**
     * 质押式回购历史报价查询
     * 
     * @param param
     * @return List<RepoQuoteExtDto>
     */
    public List<RepoQuoteExtDto> queryPledgeRepoList(Map<String, Object> param) {
        RepoQuoteSearchDto inputBean = new RepoQuoteSearchDto();
        // 组合代码
        if (StringUtil.isNotEmpty(param.get("portfolioId").toString())) {
            // RepoQuoteSearchDto缺少组合代码
        }
        // if(StringUtil.isNotEmpty(param.get("portfolioId").toString())){
        // //RepoQuoteSearchDto缺少组合代码
        // }
        // 本方方向
        inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
        // 对手方
        inputBean.setCpPartyId(
            param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
        // 交易日期
        if (StringUtil.isNotEmpty(param.get("quoteDate"))) {
            inputBean.setQuoteDate((Date)param.get("quoteDate"));
        }
        // 成交状态
        if (StringUtil.isNotEmpty(param.get("tradeStatus").toString())) {
            inputBean.setTradeStatusList(Arrays.asList(param.get("tradeStatus").toString()));
        }
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("quoteId"))) {
            inputBean.setCfetsQuoteIdFuzzy(param.get("cfetsQuoteId").toString());
        }
        // 委托代码
        inputBean.setEntrustId(
            param.get("entrustId") != null ? param.get("entrustId").toString() : null);
        // 是否全数据查询角色(管理员true， 非管理员false)
        if (param.get("isAdmin") != null) {
            if ((boolean)param.get("isAdmin")) {
                inputBean.setAutoDataRole(true);
            }
            else {
                inputBean.setAutoDataRole(false);
            }
        }

        return ibuMainService.getRepoDialogueQuoteList(inputBean, null);
    }

    // add by wangzhuangzhuang 新增请求画面 20200520 start

    /**
     * 现券请求报价回复成交<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondRequestQuoteDeal(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        BondRequestQuoteDto inputBean = getBondRequestQuoteDto(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteDeal(inputBean,
            cfetsBondDealRequestQuoteDto.getDetIdList(), null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }
    
    
    /**
     * 现券请求报价修改<br />
     * 报价回复成交发送处理，更新接口返回数据
     * 
     * @param cfetsBondDealRequestQuoteDto 现券对话报价信息
     * @return QuoteResultDto
     */
    @Transactional
    public void sendBondRequestQuoteModify(CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto) {
        User user = this.genUserInfo(cfetsBondDealRequestQuoteDto);
        BondRequestQuoteDto inputBean = getBondRequestQuoteDto(cfetsBondDealRequestQuoteDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteModify(inputBean,
            cfetsBondDealRequestQuoteDto.getDetIdList(), null, user);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, user, true);
    }

    // add by wangzhuangzhuang 新增请求画面 20200520 end

    /**
     * 质押券对话报价撤销<br />
     * 对话报价信息入库
     * 
     * @param param 对话报价信息
     * @return boolean 是否成功
     */
    @Transactional
    public void revert(Map<String, Object> param) {
        String quoteId = (String)param.get("quoteId");
        User userInfo = new User();
        userInfo.setUserName((String)param.get("userName"));
        userInfo.setDeptCode((String)param.get("deptCode"));
        QuoteResultDto quoteResultDto = ibuMainService.sendRepoDialogueQuoteRevoke(quoteId, null,
            userInfo);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, userInfo, true);

        // 发布MQ刷新当日报价列表
        cfetsQuotedCommonService.publishMQDayHandleRepo();
    }

    /**
     * 错误码信息查询 查询
     * 
     * @param param
     * @return
     */
    public List<IbuIntfErrMsgDto> queryForList(Map<String, Object> param) {

        IbuInrfErrMsgSearchDto ibuInrfErrMsgSearchDto = new IbuInrfErrMsgSearchDto();
        ibuInrfErrMsgSearchDto.setErrCode(
            param.get("errCode") != null ? param.get("errCode").toString() : null);
        ibuInrfErrMsgSearchDto.setErrMsg(
            param.get("errMsg") != null ? param.get("errMsg").toString() : null);

        return ibuMainService.selectIbuIntfErrMsgList(ibuInrfErrMsgSearchDto);
    }

    /**
     * 错误码信息查询 双击查看
     * 
     * @param errCode
     * @return
     */
    public IbuIntfErrMsgDto selectByErrCode(String errCode) {

        return ibuMainService.selectByErrCode(errCode);
    }

    /**
     * 现券对话报价批量新增发送<br />
     * 
     * @param inputList List<BondDialogueQuoteDto>
     * @param userInfo User 登录用户信息
     * @param QuoteResultBatDto
     */
    @Transactional
    public QuoteResultBatDto batchNewBondDialogueQuote(List<BondDialogueQuoteDto> inputList,
                                                       User userInfo) {
        return ibuMainService.batchNewBondDialogueQuote(inputList, userInfo);
    }

    /**
     * 现券请求报价批量新增发送<br />
     * 操作类型 ：本方新增请求报价发送
     * 
     * @param inputList List<BondRequestQuoteDto>
     * @param userInfo User 登录用户信息
     * @param QuoteResultBatDto
     */
    @Transactional
    public QuoteResultBatDto batchNewBondRequestQuote(List<BondRequestQuoteDto> inputList,
                                                      User userInfo) {
        return ibuMainService.batchNewBondRequestQuote(inputList, userInfo);
    }

    /**
     * 质押券对话报价成交 <功能详细描述> 查询已经存在quoteId列表
     * 
     * @param quoteIdList List<String>
     * @return List<String>
     */
    public List<String> selectQuoteIdsByIdList(List<String> quoteIdList) {
        return ibuMainService.selectQuoteIdsByIdList(quoteIdList);
    }

    /**
     * 查询已经存在detId列表
     * 
     * @param detIdList List<String>
     * @return List<String>
     */
    public List<String> selectDetIdsByIdList(List<String> detIdList) {
        return ibuMainService.selectDetIdsByIdList(detIdList);
    }

    /**
     * 质押券对话报价成交 <功能详细描述>
     * 
     * @param param
     * @see
     * @since 1.0
     */
    @Transactional
    public void deal(Map<String, Object> param) {
        String quoteId = (String)param.get("quoteId");
        String entrustId = (String)param.get("entrustId");
        String portfolioId = (String)param.get("portfolioId");
        String isDzh = (String) param.get("isDzh");
        User userInfo = new User();
        userInfo.setUserName((String)param.get("userName"));
        userInfo.setDeptCode((String)param.get("deptCode"));
        RepoDialogueQuoteDto inputbean = new RepoDialogueQuoteDto();
        IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
        ibuQuoteCrepDto.setQuoteId(quoteId);
        if(!StringUtil.equals(isDzh,"Y")){
            ibuQuoteCrepDto.setEntrustId(entrustId);
            ibuQuoteCrepDto.setPortfolioId(portfolioId);
        }
        inputbean.setIbuQuoteCrepDto(ibuQuoteCrepDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendRepoDialogueQuoteDeal(inputbean, null,
            userInfo);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, userInfo, true);
    }
    
    /**
     * 质押券对话报价成交 <功能详细描述>
     * 
     * @param param
     * @see
     * @since 1.0
     */
	@Transactional
	public void modify(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
		User userInfo = this.genUserInfo(cfetsRepoDealQuotedPrice001Dto);
		RepoDialogueQuoteDto inputBean = this.convertRepoDialogueQuoteDto(cfetsRepoDealQuotedPrice001Dto);
		QuoteResultDto quoteResultDto = ibuMainService.sendUpdatedRepoDialogueQuote(inputBean, null, userInfo);
		// 组件调用共通处理
		this.resCommonHandle(quoteResultDto, userInfo, true);
	}

    /**
     * 质押券对话报价拒绝
     * 
     * @param param 入参
     * @see
     * @since 1.0
     */
    @Transactional
    public void reject(Map<String, Object> param) {
        long startTime = System.currentTimeMillis();    //获取开始时间
        String quoteId = (String)param.get("quoteId");
        String entrustId = (String)param.get("entrustId");
        String portfolioId = (String)param.get("portfolioId");
        String isDzh = (String) param.get("isDzh");
        User userInfo = new User();
        userInfo.setUserName((String)param.get("userName"));
        userInfo.setDeptCode((String)param.get("deptCode"));
        RepoDialogueQuoteDto inputbean = new RepoDialogueQuoteDto();
        IbuQuoteCrepDto ibuQuoteCrepDto = new IbuQuoteCrepDto();
        ibuQuoteCrepDto.setQuoteId(quoteId);
        if(!StringUtil.equals(isDzh,"Y")){
            ibuQuoteCrepDto.setEntrustId(entrustId);
            ibuQuoteCrepDto.setPortfolioId(portfolioId);
        }
        inputbean.setIbuQuoteCrepDto(ibuQuoteCrepDto);
        QuoteResultDto quoteResultDto = ibuMainService.sendRepoDialogueQuoteRefuse(inputbean, null,
            userInfo);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, userInfo, true);

        long endTime = System.currentTimeMillis();
        Date startDate = new Date();
        startDate.setTime(startTime);
        Date endDate = new Date();
        endDate.setTime(endTime);
        System.out.println("reject程序运行开始时间：" + new SimpleDateFormat().format(startDate)); //程序运行开始时间
        System.out.println("reject程序运行结束时间：" + new SimpleDateFormat().format(endDate)); //程序运行结束时间
        System.out.println("reject程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }

    /**
     * 根据报价编号查询错误信息
     * 
     * @param quoteId String 报价代码
     * @return IbuQuoteErrlogDto 错误信息
     * @see
     * @since 1.0
     */
    public IbuQuoteErrlogDto selectErrlog(String quoteId) {
        return ibuMainService.selectErrlog(quoteId);
    }

    /**
     * 质押券对话报价拒绝
     * 
     * @param param 入参
     * @see
     * @since 1.0
     */
    @Transactional
    public void restoreDayOff(Map<String, Object> param) {
        String quoteId = (String)param.get("quoteId");
        User userInfo = new User();
        userInfo.setUserName((String)param.get("userName"));
        userInfo.setDeptCode((String)param.get("deptCode"));
        QuoteResultDto quoteResultDto = ibuMainService.sendRepoDialogueQuoteRestore(quoteId, null,
            userInfo);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, userInfo, false);
    }

    /**
     * 质押券对话报价删除
     * 
     * @param param 入参
     * @see
     * @since 1.0
     */
    @Transactional
    public void deleteDayOff(Map<String, Object> param) {
        String quoteId = (String)param.get("quoteId");
        User userInfo = new User();
        userInfo.setUserName((String)param.get("userName"));
        userInfo.setDeptCode((String)param.get("deptCode"));
        QuoteResultDto quoteResultDto = ibuMainService.deleteRepoDialogueQuote(quoteId);
        // 组件调用共通处理
        this.resCommonHandle(quoteResultDto, userInfo, false);
    }

    /**
     * 现券买卖历史报价 对话报价 撤销
     * 
     * @param quoteId 报价代码
     * @param baseEntity 用户信息
     * @return
     */
    @Transactional
    public QuoteResultDto sendBondDialogueQuoteCancel(String quoteId, BaseEntity baseEntity) {
        QuoteResultDto quoteResultDto = ibuMainService.sendBondDialogueQuoteCancel(quoteId, null,
            this.genUserInfo(baseEntity));
        // 判断组件返回结果
        this.checkHandleResult(quoteResultDto);
        
        // 5、更新委托完成度
        cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDto);
        
        // 历史报价撤销风险处理
        ibuComponentRiskService.excuteRiskHandleForHisRevoke(quoteResultDto);

        return quoteResultDto;
    }

    /**
     * 现券买卖历史报价 请求报价 撤销
     * 
     * @param quoteId 报价代码
     * @param baseEntity 用户信息
     * @return
     */
    @Transactional
    public QuoteResultDto sendBondRequestQuoteCancel(String quoteId, BaseEntity baseEntity) {
        QuoteResultDto quoteResultDto = ibuMainService.sendBondRequestQuoteCancel(quoteId, null,
            this.genUserInfo(baseEntity));
        this.checkHandleResult(quoteResultDto);
        
        // 5、更新委托完成度
        cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDto);
        
        // 历史报价撤销风险处理
        ibuComponentRiskService.excuteRiskHandleForHisRevoke(quoteResultDto);

        return quoteResultDto;
    }

    /**
     * 质押式回购历史报价 对话报价 撤销
     * 
     * @param quoteId 报价代码
     * @return
     */
    @Transactional
    public QuoteResultDto sendRepoDialogueQuoteCancel(String quoteId, BaseEntity baseEntity) {
        QuoteResultDto quoteResultDto = ibuMainService.sendRepoDialogueQuoteCancel(quoteId, null,
            this.genUserInfo(baseEntity));
        // 判断组件返回结果
        this.checkHandleResult(quoteResultDto);
        
        // 5、更新委托完成度
        cfetsQuotedCommonService.updateQuoteCompletion(quoteResultDto);
        
        // 历史报价撤销风险处理
        ibuComponentRiskService.excuteRiskHandleForHisRevoke(quoteResultDto);

        return quoteResultDto;
    }

    /**
     * 错误码信息查询 分页查询
     * 
     * @param page
     * @param length
     * @param pageParam
     * @return
     */
    public Page<IbuIntfErrMsgDto> selectForPage(int page, int length,
                                                Map<String, Object> pageParam) {
        IbuInrfErrMsgSearchDto ibuInrfErrMsgSearchDto = new IbuInrfErrMsgSearchDto();
        ibuInrfErrMsgSearchDto.setErrCode(pageParam.get("errCode").toString());// 错误码
        ibuInrfErrMsgSearchDto.setErrMsg(pageParam.get("errMsg").toString());// 错误信息
        ibuInrfErrMsgSearchDto.setOrderField(
            pageParam.get("key") == null ? null : pageParam.get("key").toString());
        ibuInrfErrMsgSearchDto.setOrderDirection(
            pageParam.get("order") == null ? null : pageParam.get("order").toString());
        return ibuMainService.selectForPage(page, length, ibuInrfErrMsgSearchDto);
    }

    /**
     * 现券买卖历史报价查询 page
     * 
     * @param page
     * @param length
     * @param param
     * @return
     */
    public Page<BondQuoteExtDto> queryHisDealPage(int page, int length,
                                                  Map<String, Object> param) {
        BondQuoteSearchDto inputBean = generateBondSearchCondition(param);
        inputBean.setAutoDataRole(true);
        Page<BondQuoteExtDto> pageList =  ibuMainService.getBondQuotePageList(page, length, inputBean, null);
        if(!StringUtil.isEmptyList(pageList.getRoot())) {
        	for(BondQuoteExtDto bondQuoteExtDto : pageList.getRoot()) {
        		// 回复量
        		bondQuoteExtDto.setShareAmt(bondQuoteExtDto.getIbuQuoteBondDet() != null ? ArithmeticUtil.multiply(bondQuoteExtDto.getIbuQuoteBondDet().getShareAmt(), new BigDecimal(100)) : null);
        	}
        }
        return pageList;
    }

    /**
     * 质押式回购历史报价查询 page
     * 
     * @param page
     * @param length
     * @param param
     * @return
     */
    public Page<RepoQuoteExtDto> queryPledgeRepoPage(int page, int length,
                                                     Map<String, Object> param) {
        BondQuoteSearchDto quoteSearchDto = generateBondSearchCondition(param);
        RepoQuoteSearchDto inputBean = new RepoQuoteSearchDto();
        try {
            BeanUtils.copyProperties(inputBean, quoteSearchDto);
            inputBean.setAutoDataRole(true);
            Page<RepoQuoteExtDto> result = ibuMainService.getRepoDialogueQuotePageList(page,
                length, inputBean, null);
            if (result != null && !StringUtil.isEmptyList(result.getRoot())) {
                for (RepoQuoteExtDto extDto : result.getRoot()) {
                    extDto.setRate(ArithmeticUtil.multiply(extDto.getRate(), new BigDecimal(100)));
                    extDto.setShareAmt(extDto.getFpriceAmt());// 全价总额
                }
            }
            return result;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 封装历史报价查询的查询条件 by JIAOXUJIN on 2020-06-09
     */
    private BondQuoteSearchDto generateBondSearchCondition(Map<String, Object> param) {
        BondQuoteSearchDto inputBean = new BondQuoteSearchDto();

        // 组合代码
        if (param.get("portfolioId") != null
            && StringUtil.isNotEmpty(param.get("portfolioId").toString())) {
            inputBean.setPortfolioIdList(Arrays.asList(param.get("portfolioId").toString()));
        }
        // 本方方向
        inputBean.setPs(param.get("ps") != null ? param.get("ps").toString() : null);
        // 资产代码
        inputBean.setFinprodFuzzy(
            param.get("finprodId") != null ? param.get("finprodId").toString() : null);
        // 本方交易员
        if (StringUtil.isNotEmpty(param.get("mySideTrader"))) {
            inputBean.setOuTraderIdList(StringUtil.splitToList(GlobalConstant.COMMA,
                param.get("mySideTrader").toString()));
        }
        // 对手方
        inputBean.setCpPartyId(
            param.get("cpPartyId") != null ? param.get("cpPartyId").toString() : null);
        // 报价编号
        if (StringUtil.isNotEmpty(param.get("quoteId"))) {
            inputBean.setQuoteId(param.get("quoteId").toString());
        }
        // 待回复数据查询方式
        if (param.get("unAnswerFlag") != null) {
            inputBean.setReplyQueryTypeIbu(BussinessDictCodeList.银行间上行回复查询方式_全部交易员可见.getValue());
            if (StringUtil.equals(param.get("unAnswerFlag"), "N")) {
                inputBean.setReplyQueryTypeIbu(
                    BussinessDictCodeList.银行间上行回复查询方式_指定交易员可见.getValue());
                inputBean.setLoginTraderId(MapUtils.getString(param, "loginTraderId"));
            }
        }
        // 成交状态
        if (param.get("tradeStatus") != null) {
            List<String> tradeStatusList = new ArrayList<>();
            if (StringUtil.equals(param.get("tradeStatus"), "01")) {
                tradeStatusList.add("NA");// 此处特殊处理，组件那边判断取tradeStatus为空的记录
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_新增中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已新增.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_修改中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已修改.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_撤回中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_待回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_成交中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_终止中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已回复.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已拒绝.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_拒绝中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_回复中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_交谈中.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已交谈.getValue());

                //update by zhangnian
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤回.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已终止.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已过期.getValue());
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已撤销.getValue());
            }
            else if (StringUtil.equals(param.get("tradeStatus"), "02")) {
                tradeStatusList.add(BussinessDictCodeList.银行间上行报价状态_已成交.getValue());
            }
            inputBean.setTradeStatusList(tradeStatusList);
        }
        // 报价编号
        inputBean.setCfetsQuoteIdFuzzy(
            param.get("cfetsQuoteId") != null ? param.get("cfetsQuoteId").toString() : null);
        // 委托代码
        inputBean.setEntrustId(
            param.get("entrustId") != null ? param.get("entrustId").toString() : null);
        // 是否全数据查询角色(管理员true， 非管理员false)
        if (param.get("isAdmin") != null) {
            if ((boolean)param.get("isAdmin")) {
                inputBean.setAutoDataRole(true);
            }
            else {
                inputBean.setAutoDataRole(false);
            }
        }
        // 登录用户ID
        inputBean.setLoginUserId(
            param.get("loginUserId") != null ? param.get("loginUserId").toString() : null);
        // 排序字段
        inputBean.setOrderField(param.get("key") == null ? null : param.get("key").toString());
        // 排序方式（升序、降序）
        inputBean.setOrderDirection(
            param.get("order") == null ? null : param.get("order").toString());
        // // 是否查询历史表
        //// inputBean.setIsSearchHis(true);
        // 开始日
        inputBean.setStartQuoteDate(
            param.get("startQuoteDate") != null ? param.get("startQuoteDate").toString() : null);
        // 结束日
        inputBean.setEndQuoteDate(
            param.get("endQuoteDate") != null ? param.get("endQuoteDate").toString() : null);
        // 报价代码（内部）
        inputBean.setQuoteId(
            param.get("quoteId") != null ? param.get("quoteId").toString() : null);

        return inputBean;
    }

    /**
     *  质押券列表 按委托+组合的交易金额比例拆分
     * */
    public List<IbuQuoteEntCrepPledgeDto> 质押券拆分(CfetsRepoDealQuotedPrice001Dto cfetsRepoDealQuotedPrice001Dto) {
        List<IbuQuoteEntCrepPledgeDto> ibuQuoteEntCrepPledges = new ArrayList<>();
        CfetsRepoDealQuotedPriceDto cfetsRepoDealQuotedPriceDto = new CfetsRepoDealQuotedPriceDto();
        BeanUtil.copyPropertiesIgnoreNull(cfetsRepoDealQuotedPriceDto, cfetsRepoDealQuotedPrice001Dto);
        List<CfetsRepoDealQuotedPrice003Dto> pledgeBondLists = cfetsRepoDealQuotedPrice001Dto.getPledgeBondList();
        String isDzh = cfetsRepoDealQuotedPrice001Dto.getIsDzh();
        if (StringUtil.equals(isDzh, "Y")) {
            cfetsRepoDealQuotedPriceDto.setPtlAllocationList(cfetsRepoDealQuotedPrice001Dto.getPtlAllocationList());
            List<CfetsRepoPledgeBondDto> cfetsRepoPledgeBonds = new ArrayList<>();
            if (!StringUtil.isEmptyList(pledgeBondLists)) {
                pledgeBondLists.forEach(item -> {
                    CfetsRepoPledgeBondDto cfetsRepoPledgeBondDto = new CfetsRepoPledgeBondDto();
                    BeanUtil.copyPropertiesIgnoreNull(cfetsRepoPledgeBondDto, item);
                    cfetsRepoPledgeBonds.add(cfetsRepoPledgeBondDto);
                });
                cfetsRepoDealQuotedPriceDto.setPledgeBondList(cfetsRepoPledgeBonds);
            }
            List<IbuQuoteEntCrepPledgeCommonDto> ibuQuoteEntCrepPledgeCommons= cstpCommonService.质押券拆分(cfetsRepoDealQuotedPriceDto);
            if(!StringUtil.isEmptyList(ibuQuoteEntCrepPledgeCommons)){
                ibuQuoteEntCrepPledgeCommons.forEach(item -> {
                    IbuQuoteEntCrepPledgeDto ibuQuoteEntCrepPledgeDto = new IbuQuoteEntCrepPledgeDto();
                    BeanUtil.copyPropertiesIgnoreNull(ibuQuoteEntCrepPledgeDto,item);
                    ibuQuoteEntCrepPledges.add(ibuQuoteEntCrepPledgeDto);
                });
            }
        }
        return ibuQuoteEntCrepPledges;
    }

    /**
     *  上行返回报文 质押券需要关联上组合信息
     * @param cfetsRepoDealQuotedPrice003Dtos
     * @param ibuQuoteCrepPledgeDtos
     * @return
     */
    public List<CfetsRepoDealQuotedPrice003Dto> 报文返回券关联组合拆分(List<CfetsRepoDealQuotedPrice003Dto> cfetsRepoDealQuotedPrice003Dtos,List<IbuQuoteCrepPledgeDto> ibuQuoteCrepPledgeDtos){
        List<CfetsRepoDealQuotedPrice003Dto>  result =  new ArrayList<>();
        //返回报文  按质押券分组
        //交易金额按组合分组
        Map<String,Object> pledgeNumberTotalGroupByPtlMap = new HashMap<>();
        //报文返回质押券
        Map<String,Object> pledgNumberTotalByPtlMap = new HashMap<>();
        Map<String, List<CfetsRepoDealQuotedPrice003Dto>> map = cfetsRepoDealQuotedPrice003Dtos.stream().collect(Collectors.groupingBy(CfetsRepoDealQuotedPrice003Dto::getFinprodId));
        for (Map.Entry<String, List<CfetsRepoDealQuotedPrice003Dto>> entry : map.entrySet()) {
            pledgeNumberTotalGroupByPtlMap.put(entry.getKey(),entry.getValue().stream().map(CfetsRepoDealQuotedPrice003Dto::getPledgeNumber).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        Map<String, List<IbuQuoteCrepPledgeDto>> map2 = ibuQuoteCrepPledgeDtos.stream().collect(Collectors.groupingBy((a -> a.getPledgeFinprodId())));
        for (Map.Entry<String, List<IbuQuoteCrepPledgeDto>> entry : map2.entrySet()) {
            pledgNumberTotalByPtlMap.put(entry.getKey(),entry.getValue().stream().map(IbuQuoteCrepPledgeDto::getPledgeNumber).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        Map<String, CfetsRepoDealQuotedPrice003Dto> maxpledgeNumberMap = cfetsRepoDealQuotedPrice003Dtos.parallelStream().collect(Collectors.groupingBy(a -> a.getFinprodId()
                ,Collectors.collectingAndThen(Collectors.reducing((c1, c2) -> (c1.getPledgeNumber().compareTo(c2.getPledgeNumber())>=0) ? c1 : c2), Optional::get)));
        for(CfetsRepoDealQuotedPrice003Dto item : cfetsRepoDealQuotedPrice003Dtos){
            BigDecimal pledgeNumberTotal  = (BigDecimal) pledgeNumberTotalGroupByPtlMap.get(item.getFinprodId());
            for(IbuQuoteCrepPledgeDto item2 : ibuQuoteCrepPledgeDtos){
                if(StringUtil.equals(item.getFinprodId(),item2.getPledgeFinprodId())){
                    String key = item.getFinprodId();
                    CfetsRepoDealQuotedPrice003Dto resultDto = new CfetsRepoDealQuotedPrice003Dto();
                    resultDto.setQuoteId(item.getQuoteId());
                    resultDto.setEntrustId(item.getEntrustId());
                    resultDto.setPortfolioId(item.getPortfolioId());
                    resultDto.setFinprodId(item.getFinprodId());
                    BigDecimal pledgeNumber  = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getPledgeNumber(),item2.getPledgeNumber()),pledgeNumberTotal,2, RoundingMode.HALF_DOWN);
                    //尾差处理
                    if(pledgNumberTotalByPtlMap.containsKey(key)){
                        BigDecimal pledgePrinTotal = new BigDecimal(pledgNumberTotalByPtlMap.get(key).toString());
                        if(pledgePrinTotal.compareTo(BigDecimal.ZERO)>=0){
                            pledgNumberTotalByPtlMap.put(key,ArithmeticUtil.subtract(pledgePrinTotal,pledgeNumber));
                        }
                    }
                    resultDto.setPledgeNumber(pledgeNumber);
                    resultDto.setPledgePrin(ArithmeticUtil.multiply(pledgeNumber,new BigDecimal("100")));// 质押数量
                    resultDto.setPledgeRate(ArithmeticUtil.multiply(item2.getPledgeRatio(), BigDecimal.valueOf(100)));// 质押率*/
                    resultDto.setNetPrice(item2.getUnitCprice());
                    resultDto.setBondCode(item2.getFinprodMarketId());
                    result.add(resultDto);
                }
            }
        }
        //尾差处理  尾差放在指令的交易金额最大的记录
        result.stream().forEach(item -> {
            if(maxpledgeNumberMap.containsKey(item.getFinprodId())){
                CfetsRepoDealQuotedPrice003Dto keyValue = maxpledgeNumberMap.get(item.getFinprodId());
                String key = item.getFinprodId();
                if (pledgNumberTotalByPtlMap.containsKey(key)&&StringUtil.equals(item.getEntrustId(),keyValue.getEntrustId())) {
                    item.setPledgeNumber(ArithmeticUtil.doAdd(item.getPledgeNumber(),new BigDecimal(pledgNumberTotalByPtlMap.get(key).toString())));
                    item.setPledgePrin(ArithmeticUtil.multiply(item.getPledgeNumber(),new BigDecimal(100)));
                }
            }
        });
        return result;
    }
}