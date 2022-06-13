/*
 * 文件名：EntrustStatusService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.entrust.service;

import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.batch.entrust.dao.EntrustStatusMapper;
import com.joyintech.tams.invest.batch.entrust.dto.EntrustClosePosEtrReqDto;
import com.joyintech.tams.invest.batch.entrust.dto.EntrustClosePositionReqDto;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.trade.CreditLinesUpdate;
import com.joyintech.tams.invest.common.trade.CreditLinesUpdateImpl;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.constant.DictCodeListExt3.流程节点_流程提交;
import static com.joyintech.tams.constant.DictCodeListExt3.系统动作_关闭;

/**
 * 更新关闭委托状态
 * 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年2月15日
 * @see EntrustStatusService
 * @since 1.0
 */
@Service
public class EntrustStatusService {

    private static final Logger logger = LoggerFactory.getLogger(EntrustStatusService.class);

    @Autowired
    private EntrustStatusMapper entrustStatusMapper;

    @Autowired
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    @Autowired(required = false)
    private JoyinRestTemplate restTemplate;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    @Value("${application.micorservice.contextPath.tamsServer:http://tams-server}")
    private String tamsSerever;

    public void run(SysBatchExecInfo batchExecuteInfo) {
        if (null != batchExecuteInfo.getExecuteDate()) {
            Map<String, String> params = new HashMap<>();
            String sysDate = DateUtil.format(batchExecuteInfo.getExecuteDate());
            params.put("sysDate", sysDate);
            params.put("ENTRUST_STATUS_01", DictCodeList.委托状态_新建.getValue());
            params.put("ENTRUST_STATUS_02", DictCodeList.委托状态_待接收.getValue());
            params.put("ENTRUST_STATUS_08", DictCodeListExt3.委托状态_待分发.getValue());
            params.put("ENTRUST_STATUS_03", DictCodeList.委托状态_已接收.getValue());
            params.put("ENTRUST_STATUS_05", DictCodeList.委托状态_已退回.getValue());
            params.put("ENTRUST_STATUS_07", DictCodeList.委托状态_已撤回.getValue());
            params.put("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
            params.put("FINPROD_TYPE2_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());

            // 处理对接了头寸引擎的指令关闭 start
            List<EntrustClosePosEtrReqDto> etrReqDtoList = entrustStatusMapper.selectEntrustList(params);
            // List<String> entrustIds = entrustStatusMapper.selectEntrustIds(params);
            List<String> entrustIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(etrReqDtoList)) {
                entrustIds = etrReqDtoList.stream().map(EntrustClosePosEtrReqDto::getCloseId).collect(Collectors.toList());
            }
            List<EntrustClosePosEtrReqDto> entrustIdList = etrReqDtoList.stream().filter(item ->
                            (StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                                    || StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                                    || (StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                                    || StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                                    || StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
                                    || (StringUtil.equals(item.getBusiTypePosition(), DictCodeListExt3.头寸业务类型_债券一级申购.getValue())))
                    .collect(Collectors.toList());
            //entrustStatusMapper.updateDistributeStatus(params);//add by yangwenjun 20201211 更新委托指令分发表中的接收状态
           // entrustStatusMapper.updateEntrustStatus(params);
            // 日终关闭委托时，同步释放授信预占
            closeDelegateDealCredit(entrustIds);
            //更新指令业务模式(本币交易涉及资产重分类)
            updateBusModel(sysDate);
            //操作记录表
            closeDelegatOprHisMethod(entrustIdList);
            closeDelegatePositionMethod(entrustIdList);
            // 处理对接了头寸引擎的指令关闭 start


            // 处理未对接了头寸引擎的指令关闭 start
            List<String> entrustIdExistNews = entrustIdList.stream().map(item->item.getCloseId()).collect(Collectors.toList());
            entrustIdExistNews.forEach(s -> baseEtrCashPositionCommService.entrustCloseExecute(s,DictCodeList.是否_是.getValue()));
            entrustIds.removeAll(entrustIdExistNews);
            entrustIds.forEach(s -> baseEtrCashPositionCommService.entrustCloseExecute(s));
            entrustStatusMapper.updateDistributeStatus(params);//add by yangwenjun 20201211 更新委托指令分发表中的接收状态
            // 日终关闭委托时，同步释放授信预占
//            closeDelegateDealCredit(cfetsEntrustIds);
            entrustStatusMapper.updateEntrustStatus(params);
            // 处理未对接了头寸引擎的指令关闭 end
            //更新占用
            updateOccupy(etrReqDtoList);
        }
    }

    /**
     * 日终关闭委托时，同步将授信占用释放（将占用记录更新成无效状态）
     *
     * @param entrustIdList
     */
    private void closeDelegateDealCredit(List<String> entrustIdList) {
        CreditLinesUpdate creditLinesUpdate = SpringContextUtil.getBean(CreditLinesUpdateImpl.class);
        if (creditLinesUpdate != null) {
            List<CreditLinesInputDto> inputDtoList = new ArrayList<>();
            entrustIdList.forEach(entrustId -> {
                CreditLinesInputDto inputDto = new CreditLinesInputDto();
                inputDto.setTradeId(entrustId);
                inputDto.setCreditCallType(DictCodeListRsk.授信占用类别_预占.getValue());
                inputDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                inputDtoList.add(inputDto);
            });
            creditLinesUpdate.occupyCreditLinesEtr(inputDtoList, null);
        }
    }

    private void closeDelegatePositionMethod(List<EntrustClosePosEtrReqDto> dtolist) {
        EntrustClosePositionReqDto req = new EntrustClosePositionReqDto();
        List<EntrustClosePosEtrReqDto> etrList = new ArrayList<>();
        for (EntrustClosePosEtrReqDto t : dtolist) {
            EntrustClosePosEtrReqDto closeItem = new EntrustClosePosEtrReqDto();
            String busiTypePosition = DictCodeListExt3.头寸业务类型_现券交易.getValue();
            if (StringUtil.equals(t.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                if (StringUtil.isNotEmpty(t.getPs())
                        && StringUtil.equals(t.getPs(), BussinessDictCodeList.交易方向_行权回售.getValue())) {
                    busiTypePosition = DictCodeListExt3.头寸业务类型_债券行权交易.getValue(); // 业务类型：01债券一级投标，02现券交易，03债券行权交易，04债券存续期交易
                }
            } else {
                busiTypePosition = DictCodeListExt3.头寸业务类型_质押式回购首期.getValue();
            }
            if (StringUtil.isNotEmpty(t.getBusiTypePosition())) {
                closeItem.setBusiType(t.getBusiTypePosition());
            } else {
                closeItem.setBusiType(busiTypePosition);
            }
            if (StringUtil.isNotEmpty(t.getPs())) {
                closeItem.setPs(t.getPs());
            } else {
                closeItem.setPs(DictCodeList.交易方向_买入.getValue());
            }
            closeItem.setCloseId(t.getCloseId());
            closeItem.setSystemAct(DictCodeListExt3.系统动作_关闭.getValue());
            closeItem.setBusiAct(DictCodeListExt3.业务动作_指令日终关闭.getValue());
            etrList.add(closeItem);
        }
        req.setData(etrList);
        req.setUserId("SYSTEM");
        req.setTotalTradeId(IDGenerator.generate());
        req.setModelType(DictCodeListExt1.子系统代码_委托.getValue());
        PositionHttpRequest.batchClose(req);
    }

    private void closeDelegatOprHisMethod(List<EntrustClosePosEtrReqDto> entrustIdList) {
        if (entrustIdList != null && entrustIdList.size() > 0) {
            for (EntrustClosePosEtrReqDto entity : entrustIdList) {
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entity.getCloseId());
                oprHisReq.setOperType(DictCodeListExt3.操作类型_关闭.getValue());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }
    }

    /**
     * 更新业务模式
     *
     * @param sysDate
     */
    public void updateBusModel(String sysDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("sysDate", sysDate);
        List<String> quoteStatusIbu = Arrays.asList(BussinessDictCodeList.银行间上行报价状态_新增中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_已新增.getValue()
                , BussinessDictCodeList.银行间上行报价状态_修改中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_已修改.getValue()
                , BussinessDictCodeList.银行间上行报价状态_撤回中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_已成交.getValue()
                , BussinessDictCodeList.银行间上行报价状态_待回复.getValue()
                , BussinessDictCodeList.银行间上行报价状态_成交中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_终止中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_已回复.getValue()
                , BussinessDictCodeList.银行间上行报价状态_拒绝中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_回复中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_交谈中.getValue()
                , BussinessDictCodeList.银行间上行报价状态_已交谈.getValue()
                , BussinessDictCodeList.银行间上行报价状态_已交谈.getValue());
        param.put("QUOTE_STUTAS_IBUS", quoteStatusIbu);
        entrustStatusMapper.updateBusModel(param);
    }


    /**
     * 更新风险占用
     *
     * @param etrReqDtoList
     */
    private void updateOccupy(List<EntrustClosePosEtrReqDto> etrReqDtoList) {
        List<EntrustClosePosEtrReqDto> entrustIdList = etrReqDtoList.stream().filter(item ->
                (StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_上海证券交易所.getValue())
                        || StringUtil.equals(item.getTradeMarket(), DictCodeListExt1.交易场所_深圳证券交易所.getValue()))
                        || (StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
                        || StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
                        || StringUtil.equals(item.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
                        || StringUtil.equals(item.getFinprodType2(), DictCodeListExt2.金融产品类型_投管用_一级投标债券.getValue()))).collect(Collectors.toList());

        String url;
        Message<AjaxResult> message;
        for (EntrustClosePosEtrReqDto cfetsEntrustClosePosEtrReqDto : entrustIdList) {
            String pageId;
            if (StringUtil.equals(cfetsEntrustClosePosEtrReqDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                pageId = "IbuQuoteBond";
            } else if (StringUtil.isNotBlank(cfetsEntrustClosePosEtrReqDto.getIssueClass())) {
                if (StringUtil.equals(DictCodeListExt4.新债_发行方式_报价发行.getValue(), cfetsEntrustClosePosEtrReqDto.getIssueClass())) {
                    pageId = "TenderBondSub001";
                } else {
                    pageId = "CfetsTrdFirstBidFirm001";
                }
            } else {
                pageId = "CfetsEtrIbBondinfo003";
            }
            url = tamsSerever + String.format("/riskTrial/doOccupy/%s/%s/%s", pageId, 系统动作_关闭.getValue(), 流程节点_流程提交.getValue());
            EtrEntrust etrEntrust = new EtrEntrust();
            etrEntrust.setEntrustId(cfetsEntrustClosePosEtrReqDto.getCloseId());
            if (isSingle) {
                message = simpleRestTemplate.postForObject(url, etrEntrust, Message.class);
            } else {
                message = restTemplate.postForObject(url, etrEntrust, Message.class);
            }
            logger.info("风险占用更新结果：{}", JsonUtils.object2Json(message));
            if (Objects.nonNull(message) && !"000".equals(message.getCode())) {
                logger.error("风险占用更新异常：{}", cfetsEntrustClosePosEtrReqDto.getCloseId());
            }
        }
    }
}
