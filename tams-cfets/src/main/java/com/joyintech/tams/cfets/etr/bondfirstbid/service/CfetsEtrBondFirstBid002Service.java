package com.joyintech.tams.cfets.etr.bondfirstbid.service;

import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.entrust.common.service.WFlowCommonService;
import com.joyintech.service.entrust.etrcommanddispatcher.service.EtrCommandDispatcherService;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bondfirstbid.dao.CfetsEtrBondFirstBid002Mapper;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsBiddingProductDto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBid002Dto;
import com.joyintech.tams.cfets.etr.bondfirstbid.dto.CfetsEtrBondFirstBidSaveRequest;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustBidPriceMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustDetailMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrDistribute;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.EtrEntrustBidPrice;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.base.entrust.BusiAutoEtrDispService;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.invest.common.component.base.core.MstFileCommonService;
import com.joyintech.tams.invest.common.component.base.entrust.BaseEtrInvestInfoService;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一级投标指令
 *
 * @Author: kchen
 * @Date: 2022/4/19 14:52
 */
@Service
public class CfetsEtrBondFirstBid002Service extends CommonExportService<CfetsEtrBondFirstBid002Dto> implements BaseProcessService<CfetsEtrBondFirstBid002Dto> {

    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;

    @Autowired
    private CfetsEtrBondFirstBid002Mapper cfetsEtrBondFirstBid002Mapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private EtrEntrustBidPriceMapper etrEntrustBidPriceMapper;

    @Autowired
    private EtrEntrustDetailMapper etrEntrustDetailMapper;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired(required = false)
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;

    @Autowired
    private BaseEtrInvestInfoMapper baseEtrInvestInfoMapper;

    /**
     * 委托自动分发
     */
    @Autowired(required = false)
    private BusiAutoEtrDispService busiAutoEtrDispService;

    /**
     * IPlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    /**
     * 投资委托基础组件
     */
    @Autowired(required = false)
    private BaseEtrInvestInfoService baseEtrInvestInfoService;

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private MstFileCommonService mstFileCommonService;

    @Autowired(required = false)
    private EtrCommandDispatcherService etrCommandDispatcherService;

    @Override
    public void doInsert(CfetsEtrBondFirstBid002Dto entity, OperationType operationType) {
        if ("submit".equals(entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getEtrInvestBondFirstBidDtos())) {

                for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                    WFlowCommonService.insertEtrEntrustRe(dto.getEntrustId(), entity.getTotalEtrustId());
                    dto.setWorkflowId(entity.getWorkflowId());
                    BaseEtrInvestInfoDto baseEtrDto = initBaseEtrInfoFlowDto(dto);
                    doInsertFlowMethod(entity.getOperateType(), baseEtrDto);
                }
                //发送头寸 todo
                //sendPositionMethod(entity, StringUtil.equals(entity.getSystemAct(), DictCodeListExt3.系统动作_新建.getValue()),null);

            }
        }

        for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
            //更新操作记录日志
            CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
            oprHisReq.setEntrustId(dto.getEntrustId());
            if (StringUtil.equals(operationType.name(), "NEW")) {
                oprHisReq.setOperType(DictCodeListExt3.操作类型_新建.getValue());
            } else {
                oprHisReq.setOperType(DictCodeListExt3.操作类型_修改.getValue());
            }
            oprHisReq.setBusiDes(entity.getRemark());
            joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        }

    }

    @Override
    public void doUpdate(CfetsEtrBondFirstBid002Dto entity) {
        if ("save".equals(entity.getOperateType())) {
            return;
        }
        String confirmOk = RequestUtil.getParam("confirmOk");
        if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
            boolean isSubmit = true;
            SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey(entity.getWorkflowId());
            if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                isSubmit = false;
            }
            if ("submit".equals(entity.getOperateType()) && isSubmit) {

                List<CfetsEtrBondFirstBid002Dto> dataList = entity.getEtrInvestBondFirstBidDtos().stream()
                        .filter(dto -> StringUtil.isNotEmpty(dto.getEntrustId())).collect(Collectors.toList());
                try {
                    AjaxResult ar = EntrustRdsCommonAop.doListPreOccupy(dataList,  DictCodeList.平安资产类型_一级市场债.getValue(), BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue(), true, null);
                    if (StringUtil.isNotEmpty(ar)) {
                        throw new BusinessException(ar.getMessage());
                    }
                } catch (Exception e) {
                    throw new BusinessException(e.getMessage());
                }
                EntrustRdsCommonAop.riskListToChildren(entity.getRiskTrialRetDto(), entity.getEtrInvestBondFirstBidDtos(), (dto, list) -> dto.setRiskTrialRetDto(list));
            }

            WFlowCommonService.deleteFlow(entity.getWorkflowId());
            for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
                dto.setWorkflowId(entity.getWorkflowId());
                BaseEtrInvestInfoDto base = initBaseEtrInfoFlowDto(dto);
                WFlowCommonService.insertFlow(base);
            }
            //发送头寸
            try {
                //sendPositionMethod(entity, isSubmit,confirmOk);
            } catch (Exception e) {
                Message message = PositionUtil.ajaxErrorReturn(e);
                BaseMap bm = (BaseMap) message.getData();
                String code = (String) bm.get("code");
                StringBuffer messageStr = (StringBuffer) bm.get("message");
                if (StringUtil.isEmpty(confirmOk) && StringUtil.equals(PositionHttpRequest.CODE_BUSI_RK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.确认消息);
                }
                if (StringUtil.equals(PositionHttpRequest.CODE_BUSI_YK, code)) {
                    WorkFlowUtil.showMessage(messageStr.toString(), MessageType.错误消息);
                }
                throw new PositionException(messageStr.toString());
            }
        }
    }

    @Override
    public void doComplete(CfetsEtrBondFirstBid002Dto entity, OperationType operationType) {
        entity.setOperateType(operationType.toString());
        entity.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
            basRiskMathCommService.insert(entity.getRiskTrialRetDto(), dto.getEntrustId());
        }
        //删除流程中数据
        WFlowCommonService.doCompleteFlow(entity.getWorkflowId());
        for (CfetsEtrBondFirstBid002Dto dto : entity.getEtrInvestBondFirstBidDtos()) {
            try {
                doCompleteMethod(dto, entity.getOperateType());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("新建指令失败", e);
            }
        }
    }

    /**
     * 流程结束后操作
     * @param dto
     * @param operateType
     */
    private void doCompleteMethod(CfetsEtrBondFirstBid002Dto dto, String operateType) {
        BaseEtrInvestInfoDto entity = initBaseEtrInfoDto(dto);
        String isPushMsg = DictCodeList.是否_否.getValue();
        if (StringUtil.equals(operateType, "NEW")) {
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(entity.getEtrEntrust())) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(entity.getEtrEntrust(), entity.getEtrEntrustAdd()));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }
            baseEtrInvestInfoService.newDelegate(entity);
        }else {
            List<EtrDistribute> etrDistributeList = new ArrayList<>();
            if (StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo())
                    && StringUtil.isNotEmpty(dto.getEtrInvestPersonInfo().getTrader())) {
                List<String> traderList = StringUtil.splitToList(GlobalConstant.COMMA,
                        dto.getEtrInvestPersonInfo().getTrader());
                for (String trader : traderList) {
                    EtrDistribute etrDistribute = new EtrDistribute();
                    // 设置接收状态
                    etrDistribute.setRecStatus(DictCodeList.委托状态_待接收.getValue());
                    // 设置指定交易员指定交易员 = 指定交易员
                    etrDistribute.setTrader(trader);
                    // 设置委托代码
                    etrDistribute.setEntrustId(dto.getEntrustId());
                    //
                    etrDistributeList.add(etrDistribute);
                }
            }

            // 自动分发
            if (busiAutoEtrDispService != null) {
                // 检测是否设置交易员
                if (!busiAutoEtrDispService.checkHaveTrader(etrDistributeList)) {
                    // 跳出自动分发
                    entity.setEtrDistributeList(etrDistributeList);
                    isPushMsg = DictCodeList.是否_是.getValue();
                } else {
                    // 查询委托指令下达日期，如果在自动分发区间内则改指令需自动分发
                    if (busiAutoEtrDispService.checkEntrustTime(entity.getEtrEntrust())) {
                        // 自动分发
                        entity.setEtrDistributeList(
                                busiAutoEtrDispService.selectTrader(entity.getEtrEntrust(), entity.getEtrEntrustAdd()));
                        isPushMsg = DictCodeList.是否_是.getValue();
                    }
                }
            }

            baseEtrInvestInfoService.updateDelegate(entity);
            // 文件
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());
            if (mstFileCommonService != null) {
                mstFileCommonService.updateBusinessId(mstFileInputParameter);
            }

            if (DictCodeList.是否_是.getValue().equals(isPushMsg)) {
                Map<String, Object> param = new HashMap<>();
                param.put("entrustId", dto.getEntrustId());
                etrCommandDispatcherService.pushCommandMsg(param);
            }
        }
    }

    /**
     * 后台数据转换投资委托信息流程数据
     *
     * @param dto
     * @return
     */
    public BaseEtrInvestInfoDto initBaseEtrInfoFlowDto(CfetsEtrBondFirstBid002Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EtrEntrust etrEntrust = new EtrEntrust();

            dto.setFinprodType2(DictCodeListExt2.平安资产类型_一级市场债.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
            dto.setRecStatus(DictCodeListExt3.委托状态_流程中.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            etrEntrust.setEntrustAmt(null);
            etrEntrust.setEntrustNum(dto.getTotalBiddingNum().divide(new BigDecimal(100)));
            etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());

            if (StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
                etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
                entity.setEtrEntrustAdd(etrEntrustAdd);
            }

            //委托投标标位表
            List<EtrEntrustBidPrice> bidPriceList = new ArrayList<>();
            List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();
            for (CfetsBiddingProductDto productDto : biddingProductList) {
                EtrEntrustBidPrice bidPrice = new EtrEntrustBidPrice();
                bidPrice.setEntrustId(dto.getEntrustId());
                bidPrice.setBidPrice(productDto.getBidPrice());
                bidPrice.setPortfolioId(productDto.getPortfolioId());
                bidPrice.setBidPrinAmt(ArithmeticUtil.divide(productDto.getBidPrinAmt(), 100, 4));
                bidPriceList.add(bidPrice);
            }
            //委托指令明细
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                EtrEntrustDetail entrustDetail = new EtrEntrustDetail();
                entrustDetail.setEntrustId(dto.getEntrustId());
                entrustDetail.setPortfolioId(entry.getKey());
                entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getPortfolioTotalBidPrinAmt(), 100, 4));
                etrEntrustDetailList.add(entrustDetail);
            }
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());

            entity.setEtrEntrust(etrEntrust);
            entity.setBidPriceList(bidPriceList);
            entity.setEtrEntrustDetailList(etrEntrustDetailList);
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            return entity;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 后台数据转换投资委托信息数据
     *
     * @param dto
     * @return
     */
    public BaseEtrInvestInfoDto initBaseEtrInfoDto(CfetsEtrBondFirstBid002Dto dto) {
        try {
            dto.setOperateType(dto.getOperateType());
            BaseEtrInvestInfoDto entity = new BaseEtrInvestInfoDto();
            EtrEntrust etrEntrust = new EtrEntrust();

            dto.setFinprodType2(DictCodeListExt2.平安资产类型_一级市场债.getValue());
            dto.setIsOutTrade(DictCodeList.是否_是.getValue());
            dto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());
            dto.setRecStatus(DictCodeList.委托状态_待接收.getValue());
            BeanUtil.copyProperties(etrEntrust, dto);
            etrEntrust.setEntrustNum(dto.getTotalBiddingNum().divide(new BigDecimal(100)));
            etrEntrust.setPs(DictCodeList.交易方向_买入.getValue());
            etrEntrust.setEntrustDate(iPlatformDate.getSysDate());

            EtrEntrustAdd etrEntrustAdd = new EtrEntrustAdd();
            if (StringUtil.equals(dto.getIssueClass(), DictCodeListExt4.新债_发行方式_报价发行.getValue())) {
                etrEntrustAdd.setSecAcctId(dto.getSecAcctId());
                entity.setEtrEntrustAdd(etrEntrustAdd);
            }
            //指令下达时间
            if(StringUtil.isNotEmpty(dto.getTransmitTime())){
                etrEntrustAdd.setTransmitTime(dto.getTransmitTime());
            }else{
                etrEntrustAdd.setTransmitTime(new Date());
            }

            //委托投标标位表
            List<EtrEntrustBidPrice> bidPriceList = new ArrayList<>();
            List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();
            for (CfetsBiddingProductDto productDto : biddingProductList) {
                EtrEntrustBidPrice bidPrice = new EtrEntrustBidPrice();
                bidPrice.setEntrustId(dto.getEntrustId());
                bidPrice.setBidPrice(productDto.getBidPrice());
                bidPrice.setPortfolioId(productDto.getPortfolioId());
                bidPrice.setBidPrinAmt(ArithmeticUtil.divide(productDto.getBidPrinAmt(), 100, 4));
                bidPriceList.add(bidPrice);
            }
            //委托指令明细
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                EtrEntrustDetail entrustDetail = new EtrEntrustDetail();
                entrustDetail.setEntrustId(dto.getEntrustId());
                entrustDetail.setPortfolioId(entry.getKey());
                entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getPortfolioTotalBidPrinAmt(), 100, 4));
                etrEntrustDetailList.add(entrustDetail);
            }
            MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
            mstFileInputParameter.setFileList(dto.getFileArray());
            mstFileInputParameter.setBussinessId(dto.getEntrustId());

            entity.setEtrEntrust(etrEntrust);
            entity.setBidPriceList(bidPriceList);
            entity.setEtrEntrustDetailList(etrEntrustDetailList);
            entity.setMstFileInputParameter(mstFileInputParameter);
            entity.setWorkFlowId(dto.getWorkflowId());
            return entity;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 操作类型为submit时执行新增流程数据
     *
     * @param operateType 操作类型
     * @param dto
     */
    private void doInsertFlowMethod(String operateType, BaseEtrInvestInfoDto dto) {
        if ("submit".equals(operateType)) {
            if (StringUtil.isEmpty(dto.getWorkFlowId())) {
                throw new RuntimeException("WorkFlowId初始化不能为空!");
            }
            WFlowCommonService.insertFlow(dto);
        }
    }

    /**
     * 保存提交时，后台数据转换
     *
     * @param cfetsDto
     * @return
     */
    public CfetsEtrBondFirstBid002Dto packageEquity(CfetsEtrBondFirstBidSaveRequest cfetsDto) {
        FinProduct fp = null;
        if (StringUtil.isNotEmpty(cfetsDto.getFinprodId())) {
            fp = cfetsetrIbBondinfoMapper.findAssetInfo(cfetsDto.getFinprodId());
        }
        CfetsEtrBondFirstBid002Dto dto = new CfetsEtrBondFirstBid002Dto();
        BeanUtil.copyPropertiesIgnoreNull(dto, cfetsDto);
        if (StringUtil.isNotEmpty(fp)) {
            dto.setTradeMarket(fp.getTradeMarket());
            dto.setCalendarId(fp.getCalendarId());
            dto.setCcy(fp.getCcy());
            dto.setEntrustDate(cfetsDto.getEtrDate());
            dto.setFinprodAbbr(fp.getFinprodAbbr());
        }
        dto.setInvManagerId(SecurityUtil.getLoginUser().getUserName());
        dto.setEntrustNum(cfetsDto.getTotalBiddingNum());
        dto.setTradeMarketMode(DictCodeListExt1.交易市场_一级市场.getValue());
        dto.setElementId(cfetsDto.getElementId());

        List<CfetsBiddingProductDto> biddingProductList = cfetsDto.getBiddingProductList();
        List<CfetsEtrBondFirstBid002Dto> etrInvestBondFirstBidList = new ArrayList<>();
        //报价发行时，按照证券账户维度拆分为多条指令信息
        if (DictCodeListExt4.新债_发行方式_报价发行.getValue().equals(cfetsDto.getIssueClass())) {
            Map<String, List<CfetsBiddingProductDto>> secAcctIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getSecAcctId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : secAcctIdMap.entrySet()) {
                CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
                BeanUtil.copyPropertiesIgnoreNull(dd, dto);
                dd.setBiddingProductList(null);
                dd.setBiddingProductList(entry.getValue());
                dd.setSecAcctId(entry.getKey());
                etrInvestBondFirstBidList.add(dd);
            }

        } else {
            CfetsEtrBondFirstBid002Dto dd = new CfetsEtrBondFirstBid002Dto();
            BeanUtil.copyPropertiesIgnoreNull(dd, dto);
            etrInvestBondFirstBidList.add(dd);
        }

        //风险
        CfetsEtribdoRiskcalResponse riskDto = cfetsDto.getCfetsEtribdoRiskcalReturn();
        List<RiskTrialRetDto> riskTrialRetDto = new ArrayList<>();
        if (StringUtil.isNotEmpty(riskDto) && !StringUtil.isEmptyList(riskDto.getRiskcalReturnList())) {
            for (CfetsEtribdoRiskcalResponse it : riskDto.getRiskcalReturnList()) {
                RiskTrialRetDto t = new RiskTrialRetDto();
                BeanUtil.copyPropertiesIgnoreNull(t, it);
                t.setConclusion(riskDto.getRiskTrialSpan());
                riskTrialRetDto.add(t);
            }
        }
        dto.setRiskTrialRetDto(riskTrialRetDto);
        dto.setEtrInvestBondFirstBidDtos(etrInvestBondFirstBidList);
        return dto;
    }

    /**
     * 保存提交时check检查
     *
     * @param allDto
     * @return
     */
    public AjaxResult check(CfetsEtrBondFirstBidSaveRequest allDto) {

        List<ValidMessage> messages = new ArrayList<>();
        if (!StringUtil.isNotEmpty(allDto.getFinprodId())) {
            messages.add(new ValidMessage("请输入资产代码。"));
        }

        if (messages.size() > 0) {
            AjaxResult errorRs = new AjaxResult();
            errorRs.setErrorList(messages);
            return errorRs;
        }
        return null;
    }

    /**
     * 保存修改后直接通过的指令数据
     *
     * @param allDto
     */
    public void saveModifyPassInfo(CfetsEtrBondFirstBid002Dto allDto) {
        for (CfetsEtrBondFirstBid002Dto dto : allDto.getEtrInvestBondFirstBidDtos()) {
            //全删委托投标标位表数据
            baseEtrInvestInfoMapper.deleteBidPrice(dto.getEntrustId());
            //全删委托指令明细数据
            baseEtrInvestInfoMapper.deleteEntrustDetail(dto.getEntrustId());
            //更新委托表
            EtrEntrust etrEntrust = new EtrEntrust();
            etrEntrust.setEntrustId(dto.getEntrustId());
            etrEntrust.setEntrustNum(ArithmeticUtil.divide(dto.getTotalBiddingNum(), 100, 4));
            etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
            //更新委托附属表
            etrEntrustAddMapper.updateByPrimaryKeySelective(null);

            //保存委托投标标位表
            List<EtrEntrustBidPrice> bidPriceList = new ArrayList<>();
            List<CfetsBiddingProductDto> biddingProductList = dto.getBiddingProductList();
            for (CfetsBiddingProductDto productDto : biddingProductList) {
                EtrEntrustBidPrice bidPrice = new EtrEntrustBidPrice();
                bidPrice.setEntrustId(dto.getEntrustId());
                bidPrice.setBidPrice(productDto.getBidPrice());
                bidPrice.setPortfolioId(productDto.getPortfolioId());
                bidPrice.setBidPrinAmt(ArithmeticUtil.divide(productDto.getBidPrinAmt(), 100, 4));
                bidPriceList.add(bidPrice);
            }
            if (CollectionUtils.isNotEmpty(bidPriceList)) {
                for (EtrEntrustBidPrice etrEntrustBidPrice : bidPriceList) {
                    etrEntrustBidPriceMapper.insertSelective(etrEntrustBidPrice);
                }
            }

            //保存委托指令明细
            List<EtrEntrustDetail> etrEntrustDetailList = new ArrayList<>();
            Map<String, List<CfetsBiddingProductDto>> portfolioIdMap = biddingProductList.stream().collect(Collectors.groupingBy(CfetsBiddingProductDto::getPortfolioId));
            for (Map.Entry<String, List<CfetsBiddingProductDto>> entry : portfolioIdMap.entrySet()) {
                EtrEntrustDetail entrustDetail = new EtrEntrustDetail();
                entrustDetail.setEntrustId(dto.getEntrustId());
                entrustDetail.setPortfolioId(entry.getKey());
                entrustDetail.setSecAcctId(entry.getValue().get(0).getSecAcctId());
                entrustDetail.setEntrustNum(ArithmeticUtil.divide(entry.getValue().get(0).getPortfolioTotalBidPrinAmt(), 100, 4));
                etrEntrustDetailList.add(entrustDetail);
            }
            if (CollectionUtils.isNotEmpty(etrEntrustDetailList)) {
                for (EtrEntrustDetail etrEntrustDetail : etrEntrustDetailList) {
                    etrEntrustDetailMapper.insertSelective(etrEntrustDetail);
                }
            }
        }
    }
}
