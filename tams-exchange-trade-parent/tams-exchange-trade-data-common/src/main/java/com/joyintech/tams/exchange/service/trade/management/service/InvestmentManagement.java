package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
import com.joyintech.tams.exchange.common.bean.investment.BusinessDepartmentBean;
import com.joyintech.tams.exchange.common.bean.investment.CapitalAccountBean;
import com.joyintech.tams.exchange.common.bean.investment.EntrustOpeBean;
import com.joyintech.tams.exchange.common.bean.investment.EntrustReBackBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.request.InstructionQueryRequest;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.ListConvertUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.check.AppointInstructionCheck;
import com.joyintech.tams.exchange.service.trade.check.CheckResult;
import com.joyintech.tams.exchange.service.trade.processor.MessageConsumerService;
import com.joyintech.tams.exchange.service.trade.processor.constants.Constants;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3.投管业务类型_质押券出入库;

@Component
public class InvestmentManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentManagement.class);
    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;
    @Autowired
    FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    @Autowired
    private InstructionService instructionService;
    @Autowired
    private InstructionPledgeService instructionPledgeService;
    @Autowired
    private InstructionDetailService instructionDetailService;
    @Autowired
    private MsgReceiverManager msgReceiverManager;
    @Autowired
    private MessageConsumerService messageConsumerService;
    @Autowired
    private CommonManagement commonManagement;
    @Autowired
    private InvestRiskManagement investRiskManagement;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AppointInstructionCheck appointInstructionCheck;
    @Autowired
    private RecordLogService recordLogService;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    /**
     * 指令查询带条件
     *
     * @param instructionId
     * @param portfolioId
     * @param entrustStatus
     * @param traderList
     * @param AMOUNT
     * @param positionStr
     */
    public List<Instruction> queryInstructions(String instructionId, String portfolioId, String entrustStatus, String traderList, Double AMOUNT, String positionStr) {
        String param = null;
        if (Objects.nonNull(instructionId)) param = "?entrustID=" + instructionId;
        if (Objects.nonNull(portfolioId)) if (Objects.nonNull(param)) {
            param += "&portfolioId=" + portfolioId;
        } else {
            param = "?portfolioId=" + portfolioId;
        }
        if (Objects.nonNull(entrustStatus)) if (Objects.nonNull(param)) {
            param += "&entrustStatus=" + entrustStatus;
        } else {
            param = "?entrustStatus=" + entrustStatus;
        }
        if (Objects.nonNull(traderList)) if (Objects.nonNull(param)) {
            param += "&traderList=" + traderList;
        } else {
            param = "?traderList=" + traderList;
        }
        if (Objects.nonNull(AMOUNT)) if (Objects.nonNull(param)) {
            param += "&AMOUNT=" + AMOUNT;
        } else {
            param = "?AMOUNT=" + AMOUNT;
        }
        if (Objects.nonNull(positionStr)) if (Objects.nonNull(param)) {
            param += "&positionStr=" + positionStr;
        } else {
            param = "?positionStr=" + positionStr;
        }
        if (!Strings.isNullOrEmpty(ServiceApplication.INSTRUCTION_SERVICE)) {
            Instruction[] forObject = null;
            if (isSingle) {
                forObject = simpleRestTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions + param, null, Instruction[].class);
            } else {
                forObject = restTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions + param, null, Instruction[].class);
            }
            return Arrays.asList(forObject);
        }
        return null;
    }

    private void updateSysParam(Map<String, Object> pageParam) {
        String fairCode = (String) pageParam.get("fairCode");
        String platDate = (String) pageParam.get("platDate");
        String approveCode = (String) pageParam.get("approveCode");
        String tradeTimeSwitch = (String) pageParam.get("tradeTimeSwitch");
        String fairModeEntrustPriceBestThanInstruction = (String) pageParam.get("fairModeEntrustPriceBestThanInstruction");
        String fairModeWithUnacceptInstruction = (String) pageParam.get("fairModeWithUnacceptInstruction");

        if (StringUtil.isNotEmpty(fairCode)) {
            commonManagement.setFairCode(fairCode);
        }
        if (StringUtil.isNotEmpty(platDate)) {
            commonManagement.setPlatDateStr(platDate);
        }
        if (StringUtil.isNotEmpty(approveCode)) {
            commonManagement.setRiskApprovalCode(approveCode);
        }
        if (StringUtil.isNotEmpty(tradeTimeSwitch)) {
            commonManagement.setTradeTimeSwitch(tradeTimeSwitch);
        }
        // modify by etliu 20211113 start
        // TISHKS-343: 需求文档中描述闭市不可下委托，但系统没有做此校验；
        if (StringUtil.isNotEmpty(fairModeEntrustPriceBestThanInstruction)) {
            commonManagement.setFairModeEntrustPriceBestThanInstruction(fairModeEntrustPriceBestThanInstruction);
        }
        if (StringUtil.isNotEmpty(fairModeWithUnacceptInstruction)) {
            commonManagement.setFairModeWithUnacceptInstruction(fairModeWithUnacceptInstruction);
        }
        // modify by etliu 20211113 end
    }

    /**
     * 指令列表导出内容查询
     *
     * @param request
     * @return
     */
    public List<InstructionExportVo> queryExportPage(InstructionQueryRequest request) {
        Map<String, Object> pageParam = JsonUtils.getObjectFromJson(JsonUtils.getJsonFromObject(request), Map.class);
        InstructionPage<Instruction, InstructionPledge, InstructionDetail> page = queryInstructions(pageParam);
        List<Instruction> instructionList = page.getEtr_entrust_list();
        return convertFieldValue(instructionList);
    }

    /**
     * 对指令状态、交易平台、委托方向、价格模式、资产类型字典值进行转换
     */
    public List<InstructionExportVo> convertFieldValue(List<Instruction> instructionList) {
        if (Objects.isNull(instructionList) || instructionList.isEmpty()) {
            return Collections.emptyList();
        }
        List<InstructionExportVo> returnList = new ArrayList<>(instructionList.size());
        InstructionExportVo newInstruction = null;
        for (Instruction instruction : instructionList) {
            newInstruction = new InstructionExportVo();
            BeanUtil.copyProperties(instruction, newInstruction);
            if (Objects.nonNull(DictCodeList.from(DictCode.委托状态, newInstruction.getEntrustStatus()))) {
                newInstruction.setEntrustStatus(DictCodeList.from(DictCode.委托状态, newInstruction.getEntrustStatus()).getCodeName());
            }
            if (Objects.nonNull(DictCodeList.from(DictCode.交易平台, newInstruction.getTradePlat()))) {
                newInstruction.setTradePlat(DictCodeList.from(DictCode.交易平台, newInstruction.getTradePlat()).getCodeName());
            }
            if (Objects.nonNull(DictCodeListExt2.from(DictCode.交易方向, newInstruction.getPs()))) {
                newInstruction.setPs(DictCodeListExt2.from(DictCode.交易方向, newInstruction.getPs()).getCodeName());
            }
            if (Objects.nonNull(DictCodeListExt2.from(DictCode.价格模式, newInstruction.getPriceMode()))) {
                newInstruction.setPriceMode(DictCodeListExt2.from(DictCode.价格模式, newInstruction.getPriceMode()).getCodeName());
            }
            if (Objects.nonNull(DictCodeListExt1.from(DictCode.金融产品类型_投管用, newInstruction.getFinprodType2()))) {
                newInstruction.setFinprodType2(DictCodeListExt1.from(DictCode.金融产品类型_投管用, newInstruction.getFinprodType2()).getCodeName());
            }
            if (Objects.nonNull(DictCodeListExt3.from(DictCode.投管业务类型, newInstruction.getBusiTypeTg()))) {
                newInstruction.setBusiTypeTg(DictCodeListExt3.from(DictCode.投管业务类型, newInstruction.getBusiTypeTg()).getCodeName());
            }
            newInstruction.setProgress(newInstruction.getProgress());
            newInstruction.setDealProgress(newInstruction.getDealProgress());
            newInstruction.setLeftVolume(newInstruction.getLeftVolume());
            newInstruction.setLeftAmount(newInstruction.getLeftAmount());
            returnList.add(newInstruction);
        }
        return returnList;
    }

    /**
     * 查询指令列表
     * <p/>
     * <ul>
     *  <li>
     *      dataType : 数据类型（为空时从内存查询，否则从投管查询）
     *      traderList : 交易员列表
 *      </li>
     * </ul>
     *
     * @param pageParam 查询参数
     * @return 指令列表
     */
    public InstructionPage<Instruction, InstructionPledge, InstructionDetail> queryInstructions(Map<String, Object> pageParam) {
        // 更新系统参数
        updateSysParam(pageParam);

        InstructionPage<Instruction, InstructionPledge, InstructionDetail> instructionPage;
        String dataType = (String) pageParam.get("dataType");

        if (CharSequenceUtil.isNotEmpty(dataType)) {
            instructionPage = queryInstructionsFromTg(pageParam);
        } else {
            instructionPage = queryInstructionsFromCache(pageParam);
        }

        return instructionPage;

        /*InstructionPage<Instruction, InstructionPledge, InstructionDetail> bean = new InstructionPage<>();
        List<InstructionPledge> pledgeList = new ArrayList<>();
        List<InstructionDetail> detailList = new ArrayList<>();
        List<Instruction> instructionUserList = new ArrayList<>();
        if (Strings.isNullOrEmpty((String) pageParam.get("dataType"))) {
            List<String> traderList = (List<String>) pageParam.get("traderList");
            if (Objects.nonNull(traderList)) {
                traderList.forEach(user -> {
                    List<Instruction> instructionList = new ArrayList<>();
                    List<String> instructionIdList = (List<String>) pageParam.get("entrustId");
                    if (Objects.nonNull(instructionIdList) && instructionIdList.size() > 0) {
                        for (String instructionId : instructionIdList) {
                            Instruction instruction = instructionService.findById(instructionId);
                            if (Objects.nonNull(instruction)) {
                                instructionList.add(instruction);
                            }
                        }
                    } else {
                        instructionList = instructionService.queryInstructionByUserIdAndStockCode(user, null);
                    }
                    List<Instruction> instructionCloneList = new ArrayList<>();
                    if (Objects.nonNull(instructionList)) {
                        instructionList.forEach(instruction -> {
                            MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean;
                            if (InvestmentAssetType.FUTURES.value.equals(instruction.getAssetType())) {
                                minPriceAndMinTrdUnitLimitBean = futuresInstrumentService.queryMinPriceAndMinTrdUnitLimitByFuture(instruction);
                                instruction.setMinPriceAndMinTrdUnitLimitBean(minPriceAndMinTrdUnitLimitBean);
                                Instruction instructionClone = instruction.cloneInstruction();
                                instructionClone.setFinprodMarketId(StockCodeUtils.getStockCode(instruction.getFinprodMarketId()));
                                instructionCloneList.add(instructionClone);
                            } else {
                                minPriceAndMinTrdUnitLimitBean = instrumentService.queryMinPriceAndMinTrdUnitLimit(instruction.getFinprodMarketId(), instruction.getTradePlat(), instruction.getPs());
                                instruction.setMinPriceAndMinTrdUnitLimitBean(minPriceAndMinTrdUnitLimitBean);
                                Instruction instructionClone = instruction.cloneInstruction();
                                if ("EX005".equals(instructionClone.getBusiTypeTg())) {
                                    instructionClone.setEntrustPrice(instructionClone.getEntrustPrice().multiply(BigDecimal.valueOf(100)));
                                }
                                instructionCloneList.add(instructionClone);
                            }
                            List<InstructionPledge> instructionPledges = instructionPledgeService.queryInstructionPledgesByInstructionId(instruction.getEntrustId());
                            if (Objects.nonNull(instructionPledges) && instructionPledges.size() > 0) {
                                pledgeList.addAll(instructionPledges);
                            }
                            List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(instruction.getEntrustId());
                            if (Objects.nonNull(instructionDetails) && instructionDetails.size() > 0) {
                                detailList.addAll(instructionDetails);
                            }
                        });
                    }
                    instructionUserList.addAll(instructionCloneList);
                });
            }
            //指令根据创建时间倒排序
            bean.setEtr_entrust_list(instructionUserList.stream().sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime())).collect(Collectors.toList()));
            bean.setEtr_pledge_list(pledgeList);
            bean.setEtr_entrust_detail_list(detailList);
        } else {
            if (isSingle) {
                bean = simpleRestTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions, pageParam, InstructionPage.class);
            } else {
                bean = restTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions, pageParam, InstructionPage.class);
            }

            List<Instruction> instructions = ListConvertUtils.convertList(bean.getEtr_entrust_list(), Instruction.class);
            //指令根据创建时间倒排序
            bean.setEtr_entrust_list(instructions.stream().sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime())).collect(Collectors.toList()));
            bean.setEtr_pledge_list(ListConvertUtils.convertList(bean.getEtr_pledge_list(), InstructionPledge.class));
            bean.setEtr_entrust_detail_list(ListConvertUtils.convertList(bean.getEtr_entrust_detail_list(), InstructionDetail.class));
        }
        return bean;*/
    }

    /**
     * 调用投管接口查询指令列表
     *
     * @param pageParam 查询参数
     * @return 指令列表
     */
    private InstructionPage<Instruction, InstructionPledge, InstructionDetail> queryInstructionsFromTg(Map<String, Object> pageParam) {

        InstructionPage<Instruction, InstructionPledge, InstructionDetail> bean = null;
        String url = ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions;

        try {
            if (isSingle) {
                bean = simpleRestTemplate.postForObject(url, pageParam, InstructionPage.class);
            } else {
                bean = restTemplate.postForObject(url, pageParam, InstructionPage.class);
            }
        } catch (Exception e) {
            logger.warn("调用投管接口失败: url = [{}], param = [{}], errorMsg = [{}]", url, JSONUtil.toJsonStr(pageParam), ExceptionUtil.getMessage(e));
        }

        if (ObjectUtil.isNull(bean)) {
            return new InstructionPage<>();
        }

        List<Instruction> instructions = ListConvertUtils.convertList(bean.getEtr_entrust_list(), Instruction.class);
        fillMinPriceAndTrdUnitLimit(instructions);
        handlePriceWithZYCRK(instructions);
        bean.setEtr_entrust_list(sortInstructionsByCreateTime(instructions));

        List<InstructionPledge> instructionPledges = ListConvertUtils.convertList(bean.getEtr_pledge_list(), InstructionPledge.class);
        bean.setEtr_pledge_list(instructionPledges);

        List<InstructionDetail> instructionDetails = ListConvertUtils.convertList(bean.getEtr_entrust_detail_list(), InstructionDetail.class);
        bean.setEtr_entrust_detail_list(instructionDetails);

        return bean;
    }

    /**
     * 缓存中查询指令列表
     *
     * @param pageParam 查询参数
     * @return 指令列表
     */
    public InstructionPage<Instruction, InstructionPledge, InstructionDetail> queryInstructionsFromCache(Map<String, Object> pageParam) {

        InstructionPage<Instruction, InstructionPledge, InstructionDetail> bean = new InstructionPage<>();

        List<String> traderList = Convert.toList(String.class, pageParam.get("traderList"));
        if (CollUtil.isEmpty(traderList)) {
            return bean;
        }

        List<Instruction> instructionList = Lists.newArrayList();
        traderList.forEach(trader -> instructionList.addAll(instructionService.queryInstructionByUserIdAndStockCode(trader, null)));
        if (CollUtil.isEmpty(instructionList)) {
            return bean;
        }

        fillMinPriceAndTrdUnitLimit(instructionList);

        // 价格修改仅限于前端展示，故使用拷贝的数据，而不使用内存数据
        List<Instruction> instructionCloneList = cloneInstructions(instructionList);
        handlePriceWithZYCRK(instructionCloneList);
        bean.setEtr_entrust_list(sortInstructionsByCreateTime(instructionCloneList));

        List<String> ids = instructionList.stream().map(Instruction::getEntrustId).collect(Collectors.toList());
        bean.setEtr_pledge_list(getPledgesByIds(ids));
        bean.setEtr_entrust_detail_list(getDetailsByIds(ids));

        return bean;
    }

    private List<Instruction> sortInstructionsByCreateTime(List<Instruction> instructions) {
        return instructions.stream()
                .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                .collect(Collectors.toList());
    }

    private List<Instruction> cloneInstructions(List<Instruction> instructions) {
        return instructions.stream().map(Instruction::cloneInstruction).collect(Collectors.toList());
    }

    private void handlePriceWithZYCRK(List<Instruction> instructions) {
        if (CollUtil.isEmpty(instructions)) {
            return;
        }
        for (Instruction instruction : instructions) {
            if (投管业务类型_质押券出入库.getValue().equals(instruction.getBusiTypeTg())) {
                instruction.setEntrustPrice(NumberUtil.mul(instruction.getEntrustPrice(), 100));
            }
        }
    }

    private void fillMinPriceAndTrdUnitLimit(List<Instruction> instructions) {
        if (CollUtil.isEmpty(instructions)) {
            return;
        }
        MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean = null;
        for (Instruction instruction : instructions) {
            minPriceAndMinTrdUnitLimitBean = instruction.getMinPriceAndMinTrdUnitLimitBean();

            if (ObjectUtil.isNull(minPriceAndMinTrdUnitLimitBean) || ErrorCode.isFail(minPriceAndMinTrdUnitLimitBean.getErrorCode())) {
            /*if (InvestmentAssetType.FUTURES.value.equals(instruction.getAssetType())) {
                minPriceAndMinTrdUnitLimitBean = futuresInstrumentService.queryMinPriceAndMinTrdUnitLimitByFuture(instruction);
                instruction.setMinPriceAndMinTrdUnitLimitBean(minPriceAndMinTrdUnitLimitBean);
                Instruction instructionClone = instruction.cloneInstruction();
                instructionClone.setFinprodMarketId(StockCodeUtils.getStockCode(instruction.getFinprodMarketId()));
                return;
            }*/
                minPriceAndMinTrdUnitLimitBean = instrumentService.queryMinPriceAndMinTrdUnitLimit(instruction.getFinprodMarketId(), instruction.getTradePlat(), instruction.getPs());
                instruction.setMinPriceAndMinTrdUnitLimitBean(minPriceAndMinTrdUnitLimitBean);
            }
        }
    }

    private List<InstructionDetail> getDetailsByIds(List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        List<InstructionDetail> details = Lists.newArrayList();
        for (String id : ids) {
            List<InstructionDetail> instructionDetails = instructionDetailService.queryInstructionDetailsByInstructionId(id);
            if (CollUtil.isNotEmpty(instructionDetails)) {
                details.addAll(instructionDetails);
            }
        }
        return details;
    }

    private List<InstructionPledge> getPledgesByIds(List<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        List<InstructionPledge> pledges = Lists.newArrayList();
        for (String id : ids) {
            List<InstructionPledge> instructionPledges = instructionPledgeService.queryInstructionPledgesByInstructionId(id);
            if (CollUtil.isNotEmpty(instructionPledges)) {
                pledges.addAll(instructionPledges);
            }
        }
        return pledges;
    }


    /**
     * 查询指令列表
     *
     * @return
     */
    public InstructionPage<Instruction, InstructionPledge, InstructionDetail> queryInstructionsAll() {
        if (!Strings.isNullOrEmpty(ServiceApplication.INSTRUCTION_SERVICE)) {
            Map<String, Object> pageParam = new HashMap<>();
            pageParam.put("AMOUNT", 0);
            pageParam.put("positionStr", "");
            InstructionPage<Instruction, InstructionPledge, InstructionDetail> bean = null;
            if (isSingle) {
                bean = simpleRestTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions, pageParam, InstructionPage.class);
            } else {
                bean = restTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.queryInstructions, pageParam, InstructionPage.class);
            }
            bean.setEtr_entrust_list(ListConvertUtils.convertList(bean.getEtr_entrust_list(), Instruction.class));
            bean.setEtr_pledge_list(ListConvertUtils.convertList(bean.getEtr_pledge_list(), InstructionPledge.class));
            bean.setEtr_entrust_detail_list(ListConvertUtils.convertList(bean.getEtr_entrust_detail_list(), InstructionDetail.class));
            return bean;
        }
        return new InstructionPage<>();
    }

    /***
     * @Description: 指令接收|放弃接口
     * @Param: [params]
     * @return: com.joyintech.tams.exchange.common.bean.ResultBean
     * @Author: MAXX
     * @Date: 2020/4/28
     */
    public ResultBean acceptOrRefuse(List<EntrustOpeBean> entrustList) {
        try {
            return doAcceptOrRefuse(entrustList);
        } catch (Exception e) {
            logger.error("acceptOrRefuse error: {}", e);
            return new ResultBean(ErrorCode.INSTRUCTION_ACCEPT_OR_REFUSE_ERROR.value, e.getMessage());
        }
    }

    public ResultBean doAcceptOrRefuse(List<EntrustOpeBean> entrustList) {
        logger.info("acceptOrRefuse params:{}", JSONArray.toJSONString(entrustList));
        Map<String, Object> param = new HashMap<String, Object>();
        // 处理指令数据
        Set<String> noRefuseSet = new HashSet<>();
        getCancelInstructionsMap(entrustList, noRefuseSet);
        Map<String, EntrustOpeBean> acceptOrRefuseMap = entrustList.stream().filter(entrustOpeBean -> !noRefuseSet.contains(entrustOpeBean.getEntrustId())).collect(Collectors.toMap(EntrustOpeBean::getEntrustId, entrustOpeBean -> entrustOpeBean, (oldValue, newValue) -> newValue));
        if (acceptOrRefuseMap.size() > 0) {
            param.put("entrustOpeList", acceptOrRefuseMap.values());
            EntrustReBackBean bean = null;
            if (isSingle) {
                bean = simpleRestTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.acceptOrRefuse, param, EntrustReBackBean.class);
            } else {
                bean = restTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.acceptOrRefuse, param, EntrustReBackBean.class);
            }
            logger.info("acceptOrRefuse bean:{}", JSONObject.toJSONString(bean));
            if (Objects.nonNull(bean)) {
                bean.getEntrustOpeList().forEach(entrustRtnBean -> {
                    EntrustOpeBean entrustOpeBean = acceptOrRefuseMap.get(entrustRtnBean.getEntrustId());
                    Instruction instruction = instructionService.findById(entrustRtnBean.getEntrustId());
                    if (Objects.nonNull(instruction)) {
                        if (InstructionFlag.SUCCESS.value.equals(entrustRtnBean.getStatus())) {
                            if (InstructionFlag.ACCEPT.value.equals(entrustOpeBean.getRecStatus())) {
                                instruction.setEntrustStatus(InstructionStatus.ALREADY_RECEIVE.value);
                                instruction.setTradeId(entrustOpeBean.getTrader());
                                if (isSingle) {
                                    instruction.setTraderName(simpleRestTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getRealNameByUser, entrustOpeBean.getTrader(), String.class));
                                } else {
                                    instruction.setTraderName(restTemplate.postForObject(ServiceApplication.INSTRUCTION_SERVICE + TradeServiceURL.getRealNameByUser, entrustOpeBean.getTrader(), String.class));
                                }
                                // 接收后，删除其他trader下的未接收指令
                                List<String> traderSet = JsonUtils.getObjectListFromJson(instruction.getTradersInfo(), String.class);
                                if (Objects.nonNull(traderSet)) {
                                    logger.info("when this instruction {} -- {} accept deleting other traders -- the traderSet is {}", instruction.getEntrustId(), instruction.getTradeId(), JsonUtils.getJsonFromObject(traderSet));
                                    traderSet.forEach(s -> instructionService.deleteTraderCache(s, instruction.getFinprodMarketId(), instruction.getEntrustId()));
                                    traderSet.clear();
                                    traderSet.add(entrustOpeBean.getTrader());
                                }
                                instructionService.asyncUpdate(instruction);
                            } else {
                                instruction.setEntrustStatus(InstructionStatus.WAIT_RECEIVE.value);
                                List<String> traderSet = JsonUtils.getObjectListFromJson(instruction.getTradersInfo(), String.class);
                                for (String s : traderSet) {
                                    Instruction i = instruction.cloneInstruction();
                                    i.setTradeId(s);
                                    instructionService.asyncUpdate(i);
                                }
                            }
                            entrustOpeBean.setErrorCode(ErrorCode.SUCCESS.value);
                            entrustOpeBean.setErrorMsg(ErrorCode.SUCCESS.errorMsg);
                        } else {//失败
                            entrustOpeBean.setErrorCode(ErrorCode.INSTRUCTION_ACCEPT_OR_REFUSE_ERROR.value);
                            entrustOpeBean.setErrorMsg(ErrorCode.INSTRUCTION_ACCEPT_OR_REFUSE_ERROR.errorMsg);
                        }
                    } else {
                        logger.info("instruction [{}] is null, ignore", entrustRtnBean.getEntrustId());
                    }
                });
            }
        }
        return new ResultBean(ErrorCode.SUCCESS, entrustList);
    }

    private void getCancelInstructionsMap(List<EntrustOpeBean> entrustList, Set<String> instructionIdSet) {
        for (EntrustOpeBean instruction : entrustList) {
            if (InstructionFlag.REFUSE.value.equals(instruction.getRecStatus())) {// 只处理放弃指令
                Instruction instructionBean = instructionService.findById(instruction.getEntrustId());
                // 已放弃指令不能再次放弃
                if (Objects.nonNull(instructionBean) && InstructionStatus.RETURN.value.equals(instructionBean.getEntrustStatus())) {
                    instructionIdSet.add(instruction.getEntrustId());
                    instruction.setErrorCode(ErrorCode.INSTRUCTION_CANCELED_ALREADY.value);
                    instruction.setErrorMsg(ErrorCode.INSTRUCTION_CANCELED_ALREADY.errorMsg);
                } else {
                    List<Entrust> entrusts = entrustService.queryEntrustByInstructionId(instruction.getEntrustId());
                    if (Objects.nonNull(entrusts)) {
                        List<Entrust> entrustListNoCancel = entrusts.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType())).collect(Collectors.toList());
                        entrustListNoCancel.forEach(entrust -> {
                            // 审批中订单不可撤
                            if (EntrustStatus.isPendingStatus(entrust.getEntrustStatus())) {
                                instructionIdSet.add(instruction.getEntrustId());
                                instruction.setErrorCode(ErrorCode.INSTRUCTION_HAVE_PENDING_APPROVAL.value);
                                instruction.setErrorMsg(ErrorCode.INSTRUCTION_HAVE_PENDING_APPROVAL.errorMsg);
                                // 在途的订单不可撤
                            } else if (EntrustStatus.isOnOrder(entrust.getEntrustStatus())) {
                                instructionIdSet.add(instruction.getEntrustId());
                                instruction.setErrorCode(ErrorCode.INSTRUCTION_HAVE_REPORTED_ENTRUST.value);
                                instruction.setErrorMsg(ErrorCode.INSTRUCTION_HAVE_REPORTED_ENTRUST.errorMsg);
                                // 有成交的订单不可撤
                            } else if (entrust.getTradedVolume() > 0) {
                                instructionIdSet.add(instruction.getEntrustId());
                                instruction.setErrorCode(ErrorCode.INSTRUCTION_HAVE_TRADE.value);
                                instruction.setErrorMsg(ErrorCode.INSTRUCTION_HAVE_TRADE.errorMsg);
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 条件查询资金账户
     *
     * @param portfolioId
     * @param acctNo
     * @param busiAcctNo
     * @param acctType
     */
    public List<CapitalAccountBean> queryCapitalAccount(String portfolioId, String acctNo, String busiAcctNo, String acctType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("Content-Type", "application/json");
        paramMap.put("portfolioId", portfolioId);
        paramMap.put("acctNo", acctNo);
        paramMap.put("busiAcctNo", busiAcctNo);
        paramMap.put("acctType", acctType);
        if (!Strings.isNullOrEmpty(ServiceApplication.BASIC_DATA_SERVICE)) {
            CapitalAccountBean[] capitalAccountBeans = null;
            if (isSingle) {
                capitalAccountBeans = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryCapitalAccount, paramMap, CapitalAccountBean[].class);
            } else {
                capitalAccountBeans = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryCapitalAccount, paramMap, CapitalAccountBean[].class);
            }
            return Arrays.asList(capitalAccountBeans);
        }
        return new ArrayList<>();
    }

    /**
     * 查询全部资金账户
     */
    public List<CapitalAccountBean> queryCapitalAccountAll() {
        if (!Strings.isNullOrEmpty(ServiceApplication.BASIC_DATA_SERVICE)) {
            Map<String, String> map = new HashMap<>();
            map.put("Content-Type", "application/json");
            CapitalAccountBean[] capitalAccountBeans = null;
            if (isSingle) {
                capitalAccountBeans = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryCapitalAccount, map, CapitalAccountBean[].class);
            } else {
                capitalAccountBeans = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryCapitalAccount, map, CapitalAccountBean[].class);
            }
            return Arrays.asList(capitalAccountBeans);
        }
        return new ArrayList<>();
    }

    /**
     * 根据账户id 、 账户类型查询
     */
    public List<CapitalAccountBean> queryCapitalAccountByIdOrAgentType(String accountId, String agentType) {
        try {
            if (!Strings.isNullOrEmpty(ServiceApplication.BASIC_DATA_SERVICE)) {
                Map<String, String> map = new HashMap<>();
                if (!Strings.isNullOrEmpty(agentType))
                    map.put("acctType", agentType);
                if (!Strings.isNullOrEmpty(accountId))
                    map.put("acctNo", accountId);
                CapitalAccountBean[] capitalAccountBeans;
                if (isSingle) {
                    capitalAccountBeans = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryCapitalAccount, map, CapitalAccountBean[].class);
                } else {
                    capitalAccountBeans = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryCapitalAccount, map, CapitalAccountBean[].class);
                }
                return Arrays.asList(capitalAccountBeans);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            logger.warn("查询资金账户信息失败: {}", e);
            return null;
        }
    }

    /**
     * 查询营业部
     *
     * @param stockExchangeNo
     */
    public List<BusinessDepartmentBean> queryBusinessDepartment(String stockExchangeNo) {
        if (!Strings.isNullOrEmpty(ServiceApplication.BASIC_DATA_SERVICE)) {
            Map<String, Object> map = new HashMap<>();
            map.put("stockExchangeNo", stockExchangeNo);
            List<BusinessDepartmentBean> businessDepartmentBeanList = new ArrayList<>();
            if (isSingle) {
                businessDepartmentBeanList = simpleRestTemplate.postForList(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryBusinessDepartment, map, BusinessDepartmentBean.class);
            } else {
                businessDepartmentBeanList = restTemplate.postForList(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryBusinessDepartment, map, BusinessDepartmentBean.class);
            }
            return businessDepartmentBeanList;
        }
        return new ArrayList<>();
    }

    /**
     * 查询全部账户风控
     */
    public List<AccountRiskControlBean> queryAccountRiskControlAll() {
        if (!Strings.isNullOrEmpty(ServiceApplication.INVESTMENT_MANAGEMENT_SERVICE)) {
            AccountRiskControlBean[] accountRiskControlBeans = null;
            if (isSingle) {
                accountRiskControlBeans = simpleRestTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryAccountRiskControl, null, AccountRiskControlBean[].class);
            } else {
                accountRiskControlBeans = restTemplate.postForObject(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.queryAccountRiskControl, null, AccountRiskControlBean[].class);
            }
            return Arrays.asList(accountRiskControlBeans);
        }
        return null;
    }

    /**
     * 条件查询账户风控
     *
     * @param brockerId
     * @param busiAcctNo
     */
    public List<AccountRiskControlBean> queryAccountRiskControl(String brockerId, String busiAcctNo) {
        String param = null;
        if (Objects.nonNull(brockerId))
            param = "?brockerId=" + brockerId;
        if (Objects.nonNull(busiAcctNo))
            if (Objects.nonNull(param)) {
                param += "&busiAcctNo=" + busiAcctNo;
            } else {
                param = "?busiAcctNo=" + busiAcctNo;
            }
        if (!Strings.isNullOrEmpty(ServiceApplication.INVESTMENT_MANAGEMENT_SERVICE)) {
            AccountRiskControlBean[] accountRiskControlBeans = null;
            if (isSingle) {
                accountRiskControlBeans = simpleRestTemplate.postForObject(ServiceApplication.INVESTMENT_MANAGEMENT_SERVICE + TradeServiceURL.queryAccountRiskControl + param, null, AccountRiskControlBean[].class);
            } else {
                accountRiskControlBeans = restTemplate.postForObject(ServiceApplication.INVESTMENT_MANAGEMENT_SERVICE + TradeServiceURL.queryAccountRiskControl + param, null, AccountRiskControlBean[].class);
            }
            return Arrays.asList(accountRiskControlBeans);
        }
        return null;
    }

    public ResultBean handleExamine(String eventId, String instructionsStatus, String approveReason) {
        EntrustInterface entrust = null;
        List<Entrust> entrusts = null;
        int status, type = 0;
        if (eventId.startsWith(IDPrefix.NORMAL.value)) {
            entrust = entrustService.findById(eventId);
            type = Constants.PROCESS_TYPE_ENTRUST;
        } else if (eventId.startsWith(IDPrefix.BULK.value)) {
            entrust = integratedEntrustService.findById(eventId);
            type = Constants.PROCESS_TYPE_INTEGRATED;
        } else if (eventId.startsWith(IDPrefix.ORDER.value)) {//处理公平交易
            entrusts = entrustService.queryEntrustByOrderId(eventId);
            type = Constants.PROCESS_TYPE_FIRE_ORDER;
        }
        if (Objects.isNull(entrust)) {
            if (Objects.isNull(entrusts))
                return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST, eventId);
        }
        if (RiskType.EXAMINE_REFUSE.value.equals(instructionsStatus) && Strings.isNullOrEmpty(approveReason)) {
            entrust.setErrorMsg("拒绝原因不可以为空");
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR, entrust);
        }
        if (eventId.startsWith(IDPrefix.ORDER.value)) {
            List<Entrust> approvalList = new ArrayList();
            entrusts.forEach(entrust1 -> {
                int entrustStatus = ((EntrustInterface) entrust1).getEntrustStatus();
                if (entrustStatus == EntrustStatus.CANCELED.value) {
                    entrust1.setErrorMsg("委托已撤，不允许下单");
                    List<OrderInterface> orderInterfaces = new ArrayList<>();
                    orderInterfaces.add(entrust1);
                    investRiskManagement.riskRelease(orderInterfaces, instructionService.findById(entrust1.getInstructionId()));
                } else if (entrustStatus == EntrustStatus.PENDING_APPROVAL.value) {
                    approvalList.add(entrust1);
                } else {
                    entrust1.setErrorMsg("指令状态不是待审批");
                }
            });

            if (!approvalList.isEmpty()) {
                Order order = orderService.findById(eventId);
                TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(order.getInstructionId());
                tradeMsgReceiver.addMsg(MsgType.EXAMINE_ENTRUST, new ExamineOrder(instructionsStatus, approveReason, order, Constants.PROCESS_TYPE_FIRE_ORDER));
            }
            return new ResultBean(ErrorCode.SUCCESS, entrusts);
        } else {
            status = entrust.getEntrustStatus();
            if (status == EntrustStatus.CANCELED.value) {
                if (RiskType.EXAMINE_APPROVAL.value.equals(instructionsStatus)) {
                    entrust.setErrorMsg("委托已撤，不允许下单");
                } else {
                    entrust.setErrorMsg("审批拒绝，说明：" + approveReason);
                }
                List<OrderInterface> orderInterfaces = new ArrayList<>();
                orderInterfaces.add(entrust);
                // 撤单时，意向单，审批拒绝不需要调用风控
                if (!(RiskType.EXAMINE_REFUSE.value.equals(instructionsStatus)
                        || EntrustUtils.isIntentionEntrust(entrust.getEntrustProp()))) {
                    investRiskManagement.riskRelease(orderInterfaces, instructionService.findById(entrust.getInstructionId()));
                }
                return new ResultBean(ErrorCode.SUCCESS, entrust);
            } else if (status == EntrustStatus.PENDING_APPROVAL.value) {
                TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
                tradeMsgReceiver.addMsg(MsgType.EXAMINE_ENTRUST, new ExamineOrder(instructionsStatus, approveReason, entrust, type));
                return new ResultBean(ErrorCode.SUCCESS, entrust);
            } else {
                entrust.setErrorMsg("委托状态不是待审批");
                return new ResultBean(ErrorCode.INSTRUCTIONS_STATUS_ERROR, entrust);
            }
        }
    }

    public ResultBean instructionChange(String instructionId, String entrustStatus) {
        Instruction instruction = instructionService.findById(instructionId);
        if (Objects.nonNull(instruction)) {
            instruction.setEntrustStatus(entrustStatus);
            return new ResultBean(ErrorCode.SUCCESS);
        } else {
            return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
    }

    public ResultBean cancelInstruction(String instructionId, String instructionStatus) {
        // 关闭时，强制关闭
        Instruction instruction = instructionService.findById(instructionId);
        if (Objects.isNull(instruction)) {
            return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
        if (!Strings.isNullOrEmpty(instructionStatus) && InstructionStatus.CLOSE.value.equals(instructionStatus)) {
            instruction.setEntrustStatus(InstructionStatus.CLOSE.value);
        } else if (!Strings.isNullOrEmpty(instructionStatus) && InstructionStatus.RETURN.value.equals(instructionStatus)) {
            if (InstructionStatus.WAIT_RECEIVE.value.equals(instruction.getEntrustStatus())) {
                instruction.setEntrustStatus(InstructionStatus.RETURN.value);
            } else {
                return new ResultBean(ErrorCode.INSTRUCTIONS_RETURN_FORBID);
            }
        } else {
            // 撤回时，如果有在途委托返回失败
            List<Entrust> entrusts = entrustService.queryEntrustByInstructionId(instructionId);
            if (!entrusts.isEmpty()) {
                for (Entrust entrust : entrusts) {
                    if (!EntrustStatus.isFinished(entrust.getEntrustStatus())) {
                        return new ResultBean(ErrorCode.INSTRUCTIONS_CANCEL_FORBID);
                    }
                }
            } else {
                List<IntegratedEntrust> integratedEntrusts = integratedEntrustService.queryIntegratedEntrustByInstructionId(instructionId);
                if (!integratedEntrusts.isEmpty()) {
                    for (IntegratedEntrust entrust : integratedEntrusts) {
                        if (!EntrustStatus.isFinished(entrust.getEntrustStatus())) {
                            return new ResultBean(ErrorCode.INSTRUCTIONS_CANCEL_FORBID);
                        }
                    }
                }
            }
            instruction.setEntrustStatus(InstructionStatus.RECALLED.value);
        }
        List<String> traderSet = JsonUtils.getObjectListFromJson(instruction.getTradersInfo(), String.class);
        if (Objects.nonNull(traderSet)) {
            logger.info("when this instruction {} been cancelled or closed, update all traders entrustStatus -- the traderSet is {}", instruction.getEntrustId(), JsonUtils.getJsonFromObject(traderSet));
            traderSet.forEach(s -> {
                instructionService.deleteTraderCache(s, instruction.getFinprodMarketId(), instruction.getEntrustId());
                instruction.setTradeId(s);
                instructionService.asyncUpdate(instruction);
            });
        } else {
            instructionService.asyncUpdate(instruction);
        }
        return new ResultBean(ErrorCode.SUCCESS);
    }

    public ResultBean flushInstructionFromInvest() {
        try {
            InstructionPage<Instruction, InstructionPledge, InstructionDetail> instructionPages = queryInstructionsAll();
            return appointInstruction(instructionPages);
        } catch (Exception e) {
            return new ResultBean(ErrorCode.FLUSH_INSTRUCTION_FAILED);
        }
    }

    /**
     * 根据全价获取净价,调用投管接口查询
     *
     * @param fullPrice   全价
     * @param volume      数量
     * @param instruction 指令
     * @return entrustPrice
     */
    public BigDecimal queryEntrustPriceByFullPriceFromInvest(BigDecimal fullPrice, long volume, Instruction instruction, int round) {
        Map<String, Object> pageParam = getExpireOrFullPriceOrEntrustPriceParam(volume, instruction);
        pageParam.put("fpriceAmt", TradeUtils.multiply(fullPrice, volume, 2));
        String url = ServiceApplication.TRADE_SERVICE_NAME + TradeServiceURL.fpriceAmt;
        try {
            logger.info("根据全价查询净价：入参 pageParam {} url {}", JsonUtils.getJsonFromObject(pageParam), url);
            ExpireIncomeRateRet result;
            if (isSingle) {
                result = simpleRestTemplate.postForObject(url, pageParam, ExpireIncomeRateRet.class);
            } else {
                result = restTemplate.postForObject(url, pageParam, ExpireIncomeRateRet.class);
            }
            if (ExchangeConstant.HTTP_STATUS_CODE_OK.equals(result.getStatuCode())) {
                return TradeUtils.getBigDecimal(result.getData().getUnitCprice(), round);
            } else {
                logger.info("全价转换净价接口调用失败：errorMsg {}", JsonUtils.getJsonFromObject(result.getErrorList()));
                return fullPrice;
            }
        } catch (Exception e) {
            logger.info("根据全价查询净价时，投管接口调用失败 {}", e);
            return fullPrice;
        }
    }

    /**
     * 根据净价价获取全价,调用投管接口查询
     *
     * @param entrustPrice 净价
     * @param volume       数量
     * @param instruction  指令
     * @return entrustPrice
     */
    public BigDecimal queryFullPriceByEntrustPriceFromInvest(BigDecimal entrustPrice, long volume, Instruction instruction, int round) {
        try {
            Map<String, Object> pageParam = getExpireOrFullPriceOrEntrustPriceParam(volume, instruction);
            pageParam.put("unitCprice", entrustPrice);
            String url = ServiceApplication.TRADE_SERVICE_NAME + TradeServiceURL.unitCprice;
            logger.info("调用微服务根据净价获取全价,到期收益率, url {} pageParam {}", url, JsonUtils.getJsonFromObject(pageParam));
            ExpireIncomeRateRet result = sendReqExpireIncomeRate(pageParam, url);
            if (ExchangeConstant.HTTP_STATUS_CODE_OK.equals(result.getStatuCode())) {
                return TradeUtils.getBigDecimal(result.getData().getUnitFprice(), round);
            } else {
                logger.info("净价转换全价接口调用失败：errorMsg {}", JsonUtils.getJsonFromObject(result.getErrorList()));
                return entrustPrice;
            }
        } catch (Exception e) {
            logger.info("根据全价查询净价时，投管接口调用失败 {}", e);
            return entrustPrice;
        }
    }

    private Map<String, Object> getExpireOrFullPriceOrEntrustPriceParam(long volume, Instruction instruction) {
        Map<String, Object> pageParam = new HashMap<>();
        pageParam.put("finprodId", instruction.getFinprodId());
        pageParam.put("tradeMarket", instruction.getTradeMarket());
        pageParam.put("settleDate", commonManagement.getPlatDateStr());
        String invAim = instruction.getInvAim();
        if (Strings.isNullOrEmpty(invAim)) {
            invAim = "AC";
        }
        pageParam.put("invAim", invAim);
        pageParam.put("tradeAmt", ExchangeConstant.PRICE_OF_STANDARD_BONDS * volume);
        pageParam.put("tradeMarketMode", DictCodeListExt1.交易市场_二级市场.getValue());
        return pageParam;
    }

    /***
     * @Description: 根据净价设置全价, 到期收益率
     * @Param: instruction, entrust
     * @return: void
     * @Author: swwan
     * @Date: 2020/9/24
     */
    public void setYieldAndAllPriceFromNetPrice(Instruction instruction, Entrust entrust, int round) {
        Map<String, Object> pageParam = getExpireOrFullPriceOrEntrustPriceParam(entrust.getEntrustVolume(), instruction);
        pageParam.put("unitCprice", entrust.getEntrustPrice());
        String url = ServiceApplication.TRADE_SERVICE_NAME + TradeServiceURL.unitCprice;
        logger.info("调用微服务根据净价获取全价,到期收益率, url {} pageParam {}", url, JsonUtils.getJsonFromObject(pageParam));
        ExpireIncomeRateRet result = sendReqExpireIncomeRate(pageParam, url);
        if (ExchangeConstant.HTTP_STATUS_CODE_OK.equals(result.getStatuCode())) {
            BigDecimal unitFprice = TradeUtils.getBigDecimal(result.getData().getUnitFprice(), round);
            BigDecimal yeild = TradeUtils.getBigDecimal(result.getData().getYield(), round);
            entrust.setFullPrice(unitFprice);
            entrust.setExpireYearRate(yeild);
        } else {
            logger.info("净价转换全价接口调用失败：errorMsg {}", JsonUtils.getJsonFromObject(result.getErrorList()));
            entrust.setFullPrice(new BigDecimal(0));
            entrust.setExpireYearRate(new BigDecimal(0));
        }
    }

    /***
     * @Description: 根据全价设置净价, 到期收益率
     * @Param: instruction, entrust
     * @return: void
     * @Author: swwan
     * @Date: 2020/9/24
     */
    public void setYeildAndNetPriceFromAllPrice(Instruction instruction, Entrust entrust, int round) {
        Map<String, Object> pageParam = getExpireOrFullPriceOrEntrustPriceParam(entrust.getEntrustVolume(), instruction);
        pageParam.put("fpriceAmt", TradeUtils.multiply(entrust.getEntrustPrice(), entrust.getEntrustVolume(), 4));
        pageParam.put("unitCprice", 0);
        String url = ServiceApplication.TRADE_SERVICE_NAME + TradeServiceURL.fpriceAmt;
        logger.info("调用微服务根据全价获取净价,到期收益率, url {} pageParam {}", url, JsonUtils.getJsonFromObject(pageParam));
        ExpireIncomeRateRet result = sendReqExpireIncomeRate(pageParam, url);
        if (ExchangeConstant.HTTP_STATUS_CODE_OK.equals(result.getStatuCode())) {
            BigDecimal unitCprice = TradeUtils.getBigDecimal(result.getData().getUnitCprice(), round);
            BigDecimal yeild = TradeUtils.getBigDecimal(result.getData().getYield(), round);
            entrust.setFullPrice(entrust.getEntrustPrice());
            entrust.setEntrustPrice(unitCprice);
            entrust.setExpireYearRate(yeild);
        } else {
            logger.info("净价转换全价接口调用失败：errorMsg {}", JsonUtils.getJsonFromObject(result.getErrorList()));
            entrust.setFullPrice(entrust.getEntrustPrice());
            entrust.setEntrustPrice(new BigDecimal(0));
            entrust.setExpireYearRate(new BigDecimal(0));
        }
    }

    private ExpireIncomeRateRet sendReqExpireIncomeRate(Map<String, Object> pageParam, String url) {
        ExpireIncomeRateRet result;
        try {
            if (isSingle) {
                result = simpleRestTemplate.postForObject(url, pageParam, ExpireIncomeRateRet.class);
            } else {
                result = restTemplate.postForObject(url, pageParam, ExpireIncomeRateRet.class);
            }
            logger.info("应答报文[{}]", JSONObject.toJSONString(result));
            return result;
        } catch (Exception e) {
            logger.info("净价转换全价接口调用失败: {}", e.getMessage(), e);
            result = new ExpireIncomeRateRet();
            result.setStatuCode(ExchangeConstant.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR);
            return result;
        }
    }

    public ResultBean appointInstruction(InstructionPage<Instruction, InstructionPledge, InstructionDetail> instructionPage) throws Exception {
        logger.info("接收指令分发数据信息：{}", JsonUtils.getJsonFromObject(instructionPage));

        //指令信息
        List<Instruction> instructionList = instructionPage.getEtr_entrust_list();
        //组合信息
        List<InstructionDetail> detailList = instructionPage.getEtr_entrust_detail_list();
        //质押券信息
        List<InstructionPledge> pledgeList = instructionPage.getEtr_pledge_list();
        List<String> entrustIds = instructionList.stream().map(Instruction::getEntrustId).collect(Collectors.toList());
        try {
            //保存分发指令数据
            List<RecordLog> records = saveAppointInstruction(instructionList, detailList, pledgeList);

            CheckResult checkResult = appointInstructionCheck.checkAppointInstruction(instructionList, detailList, pledgeList);
            int code = checkResult.getCode();
            Map<String, Object> data = checkResult.getData();
            if (code == 1) {
                updateAppointInstruction(records, entrustIds, checkResult.getMsg());
                return new ResultBean(ErrorCode.APPOINT_INSTRUCTION_PARAMETER_ERROR.value(), checkResult.getMsg());
            } else if (code == 2 && MapUtils.isNotEmpty(data)) {
                Set<String> keys = data.keySet();
                instructionList = instructionList.stream().filter(o -> !keys.contains(o.getEntrustId())).collect(Collectors.toList());
                detailList = detailList.stream().filter(o -> !keys.contains(o.getEntrustId())).collect(Collectors.toList());
                pledgeList = pledgeList.stream().filter(o -> !keys.contains(o.getEntrustId())).collect(Collectors.toList());

                updateAppointInstruction(records, entrustIds, Lists.newArrayList(keys.iterator()), data);
            } else {
                updateAppointInstruction(records, entrustIds);
            }

            dealAppointInstruction(instructionList, detailList, pledgeList);

            logger.info("处理指令分发数据信息: {}", JsonUtils.getJsonFromObject(instructionPage));

            return new ResultBean(ErrorCode.SUCCESS);
        } catch (Exception e) {
            logger.warn("处理指令分发数据失败：{}", e.getMessage(), e);
//            throw e;
            return new ResultBean(ErrorCode.APPOINT_INSTRUCTION_ERROR);
        }
    }

    private List<RecordLog> saveAppointInstruction(List<Instruction> instructionList, List<InstructionDetail> detailList, List<InstructionPledge> pledgeList) {
        logger.info("保存分发指令数据开始...");
        if (CollectionUtils.isEmpty(instructionList)) {
            logger.error("分发指令数据为空");
            return Collections.emptyList();
        }
        Map<String, List<InstructionDetail>> detailMap = Maps.newHashMap();
        if (Objects.nonNull(detailList)) {
            detailMap.putAll(detailList.stream().collect(Collectors.groupingBy(InstructionDetail::getEntrustId)));
        }
        Map<String, List<InstructionPledge>> pledgeMap = Maps.newHashMap();
        if (Objects.nonNull(pledgeList)) {
            pledgeMap.putAll(pledgeList.stream().collect(Collectors.groupingBy(InstructionPledge::getEntrustId)));
        }

        List<RecordLog> records = Lists.newArrayList();
        instructionList.forEach(instruction -> {
            String entrustId = instruction.getEntrustId();
            RecordLog record = new RecordLog();
            record.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            record.setBusiCode1(entrustId);
            String entrustInfo = JsonUtils.getJsonFromObject(instruction);
            List<String> remark = Lists.newArrayList();
            if (entrustInfo.length() > 4000) {
                entrustInfo = entrustInfo.substring(0, 4000);
                remark.add("指令信息长度超出4000");
            }
            record.setRecordInfo(entrustInfo);

            String detailInfo = JsonUtils.getJsonFromObject(detailMap.get(entrustId));
            if (detailInfo.length() > 4000) {
                detailInfo = detailInfo.substring(0, 4000);
                remark.add("组合信息长度超出4000");
            }
            record.setDetailInfo(detailInfo);

            List<InstructionPledge> pledges = pledgeMap.get(entrustId);
            if (CollectionUtils.isNotEmpty(pledges)) {
                String pledgeInfo = JsonUtils.getJsonFromObject(pledges);
                if (pledgeInfo.length() > 4000) {
                    pledgeInfo = pledgeInfo.substring(0, 4000);
                    remark.add("质押券信息长度超出4000");
                }
                record.setPledgeInfo(pledgeInfo);
            }
            record.setLogType(0);
            record.setStatus(0);
            record.setCreateUser(instruction.getInvManagerId());
            record.setCreateTime(new Date());
            record.setUpdateUser(instruction.getInvManagerId());
            record.setUpdateTime(record.getCreateTime());
            if (CollectionUtils.isNotEmpty(remark)) {
                record.setRemark(String.join(";", remark));
            }
            records.add(record);
        });
        recordLogService.saveBatch(records);
        logger.info("保存分发指令数据结束...");
        return records;
    }

    private void updateAppointInstruction(List<RecordLog> records, List<String> errorIds, String errorMsg) {
        if (CollectionUtils.isNotEmpty(errorIds)) {
            logger.info("指令分发检查[不通过]：{}", errorIds);
            records.forEach(record -> {
                record.setUpdateTime(new Date());
                if (errorIds.contains(record.getBusiCode1())) {
                    record.setStatus(2);
                    record.setCheckMsg(errorMsg);
                }
            });

            recordLogService.updateBatch(records);
        }
    }

    private void updateAppointInstruction(List<RecordLog> records, List<String> entrustIds, List<String> errorIds,
                                          Map<String, Object> data) {
        logger.info("指令分发检查[不通过]：{}", errorIds);
        logger.info("指令分发检查[通过]：{}", ListUtils.subtract(entrustIds, errorIds));
        records.forEach(record -> {
            record.setUpdateTime(new Date());
            if (errorIds.contains(record.getBusiCode1())) {
                record.setStatus(2);
                record.setCheckMsg(JsonUtils.getJsonFromObject(data.get(record.getBusiCode1())));
            } else {
                record.setCheckMsg("检查通过");
                record.setStatus(1);
            }
        });

        recordLogService.updateBatch(records);
    }

    private void updateAppointInstruction(List<RecordLog> records, List<String> entrustIds) {
        logger.info("指令分发检查[通过]：{}", entrustIds);
        records.forEach(record -> {
            record.setUpdateTime(new Date());
            record.setCheckMsg("检查通过");
            record.setStatus(1);
        });

        recordLogService.updateBatch(records);
    }

    private void dealAppointInstruction(List<Instruction> instructionList, List<InstructionDetail> detailList, List<InstructionPledge> pledgeList) {
        Set<String> instructionPledgeIdSet = new HashSet<>();
        Set<String> instructionDetailIdSet = new HashSet<>();
        if (Objects.nonNull(pledgeList))
            pledgeList.forEach(instructionPledge -> instructionPledgeIdSet.add(instructionPledge.getEntrustId()));
        if (Objects.nonNull(detailList))
            detailList.forEach(instructionDetail -> instructionDetailIdSet.add(instructionDetail.getEntrustId()));
        //对于多组合时，组合代码、组合名称展示多个用逗号隔开
        messageConsumerService.processInstructionsPortfolio(instructionList, detailList);
        messageConsumerService.processInstructions(instructionList, instructionPledgeIdSet, instructionDetailIdSet);
        messageConsumerService.processInstructionPledges(pledgeList, instructionPledgeIdSet);
        messageConsumerService.processInstructionDetail(detailList, instructionDetailIdSet);
    }
}
