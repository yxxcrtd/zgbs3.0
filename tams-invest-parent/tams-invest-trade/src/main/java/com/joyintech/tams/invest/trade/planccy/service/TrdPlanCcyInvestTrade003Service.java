/*
 * 文件名：TrdPlanCcyInvestTrade003Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：xiexinting
 * 修改时间：2019年1月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.trade.planccy.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.common.service.BasRiskMathCommService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.settle.BussSettleOrderCommService;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.component.business.trade.dto.TrdInvestPersonInfo002Dto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussTrdTransferOrderBase;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.common.trade.service.MultiApproveTradeCheckService;
import com.joyintech.tams.invest.trade.bond.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.trade.fund.dto.TrdFundInvestTradeDto;
import com.joyintech.tams.invest.trade.fund.dto.WorkFlowDto;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeConfirmDto;
import com.joyintech.tams.invest.trade.planccy.dto.TrdPlanCcyInvestTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author xiexinting
 * @version 1.0 2019年1月7日
 * @see TrdPlanCcyInvestTrade003Service
 * @since 1.0
 */
@Service
public class TrdPlanCcyInvestTrade003Service extends BaseService<TrdPlanCcyInvestTradeConfirmDto> implements BaseProcessService<TrdPlanCcyInvestTradeConfirmDto>{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdPlanCcyInvestTrade003Service.class);

    /**
     * 附件信息共通Service
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 业务编号组件
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;

    /**
     * 清算指令共通Service
     */
    @Autowired(required = false)
    private BussSettleOrderCommService bussSettleOrderCommService;

    /**
     * 划款指令业务组件service
     */
    @Autowired(required = false)
    private BussTrdTransferOrderService bussTrdTransferOrderService;

    /**
     * 金融产品交易共通
     */
    @Autowired(required = false)
    private BaseTradeProductDealCommService baseTradeProductDealCommService;
    /**
     * 风险试算组件Service
     */
    @Autowired(required = false)
    private BasRiskMathCommService basRiskMathCommService;
    /**
     * 交易及存续期交易，保存进待办，点批量审批时候检查service
     */
    @Autowired(required = false)
    private MultiApproveTradeCheckService multiApproveTradeCheckService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    /**
     * 交易及存续期交易，保存进待办，点批量审批时候需要报错。
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.MASERT)
    public String checkForSubmit(FlowEntity entity, boolean isMutiApproval) {
        if (isMutiApproval) {
            // 业务编号
            String tradeId = entity.getTradeId();
            if (!multiApproveTradeCheckService.multiApproveTradeCheck(tradeId)) {
                return getMessage("BUSS_COM_E_0100", tradeId);
            }
        }
        return null;
    }

    /**
     * 确认审批结束
     */
    @Override
    public void doComplete(TrdPlanCcyInvestTradeConfirmDto entity, OperationType arg1) {
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            UploadFile file = null;
            List<SysUploadFileInfo> syfiList = entity.getFileArray();
            for(SysUploadFileInfo sufi : syfiList){
                file = fileAttachmentProcess.fetch(sufi.getSeqNo());
                String generateId = IDGenerator.generate();
                file.setFileNo(generateId);
                fileAttachmentProcess.upload(file);
                sufi.setSeqNo(generateId);
            }
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                prepareEntity(entity);
            }
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
            // 费用明细
            List<TrdTradeFeeDetail> ttfdList = new ArrayList<>();
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())) {

                TrdTradeFeeDetail ttfd = new TrdTradeFeeDetail();
                ttfd.setTradeId(entity.getTradeId());
                ttfd.setFeeType(DictCodeListExt1.一次性费用类型_申购费.getValue());
                ttfd.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                ttfd.setFeeAmt(entity.getFeeAmtPay());
                ttfdList.add(ttfd);
            }
            else if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue()) || StringUtil.equals(entity.getPs(),
                    DictCodeList.交易方向_赎回到账.getValue())) {
                TrdTradeFeeDetail ttfd = new TrdTradeFeeDetail();
                ttfd.setTradeId(entity.getTradeId());
                ttfd.setFeeType(DictCodeListExt1.一次性费用类型_赎回费.getValue());
                ttfd.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                ttfd.setFeeAmt(entity.getFeeAmtPay());
                ttfdList.add(ttfd);
            }
            TrdTradeFeeDetail ttfd = null;
            if (StringUtil.isNotEmpty(entity.getChlId())) {
                ttfd = new TrdTradeFeeDetail();
                ttfd.setTradeId(entity.getTradeId());
                ttfd.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
                ttfd.setIsPayWithTrade(DictCodeList.是否_是.getValue());
                ttfd.setFeeAmt(entity.getChannelFee());
                ttfdList.add(ttfd);
            }

            dto.setTrdTradeFeeDetailList(ttfdList);
            dto.setTrdProductDeal(entity);
//            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
//                dto.getTrdProductDeal().setTradeAmt(null);
//            }
            dto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            // 划款指令
            List<StlPayOrdersDto> spodList = new ArrayList<>();
            for (BussTrdTransferOrderDto bttod : entity.getBussTrdTransferOrder()) {
                spodList.add(bttod);
                dto.setStlPayOrdersList(spodList);
            }
            baseTradeProductDealDtos.add(dto);
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            //basRiskMathCommService.insert(entity.getRiskTrialRetDto(), entity.getTradeId());
            //        委托代码不为空处理委托代码
            if(StringUtil.isNotEmpty(entity.getEntrustId())){
                dto.setEntrustId(entity.getEntrustId());
            }

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
            entity.setTrdTradeFeeDetail(ttfd);
            restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/doComplete", entity,
                Integer.class);
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                    && !"N".equals(CacheUtil.getSysParam("is_auto_redemption"))) {
                doCompleteRedemption(entity);
            }
        } else if (StringUtil.equals(entity.getOperateType(), "REVOKE")) {
            Map<String, Object> param = new HashMap<>();
            param.put("state", DictCodeList.流程状态_新建.getValue());
            param.put("tradeId", entity.getTradeId());
            List<WorkFlowDto> workflowIds = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/TrdFundInvestTrade/selectTradeAfter",
            		param, WorkFlowDto.class);
            if (workflowIds != null && workflowIds.size() > 0) {
           	 for (WorkFlowDto flow : workflowIds) {
           		 try {
					WorkFlowUtil.deleteProcess(flow.getSeqNo(), flow.getWorkflowId());
				} catch (WorkFlowException e) {
					// TODO Auto-generated catch block
                     logger.error(e.getMessage(),e);
				}
           	 }
            }
        }
//        调用接口  add by zhuyongkang 20191127 start
        List<String> tradeIds = new ArrayList<>();
        tradeIds.add(entity.getTradeId());
        try {
            restTemplate.postForObject(
                    ServiceApplication.CUSTOMER_INTERF_NAME + "/interf/SettleTransAccountInterf/TransAccountInfo", tradeIds,
                    Map.class);
        }catch (Exception e){

        }
//        调用接口  add by zhuyongkang 20191127 end
    }

    /**
     * 提交流程启动时
     */
    @Override
    public void doInsert(TrdPlanCcyInvestTradeConfirmDto entity, OperationType type) {
        UploadFile file = null;
        List<SysUploadFileInfo> syfiList = entity.getFileArray();
        for(SysUploadFileInfo sufi : syfiList){
            file = fileAttachmentProcess.fetch(sufi.getSeqNo());
            String generateId = IDGenerator.generate();
            file.setFileNo(generateId);
            fileAttachmentProcess.upload(file);
            sufi.setSeqNo(generateId);
        }
        
        restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/doInsert", entity,
            Integer.class);
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                    &&"N".equals(CacheUtil.getSysParam("is_auto_redemption"))) {
                doInsertRedemption(entity);
            }
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
            dto.setTrdProductDeal(entity);
            dto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            List<StlPayOrdersDto> spodList = new ArrayList<>();

            for (BussTrdTransferOrderDto bttod : entity.getBussTrdTransferOrder()) {
                spodList.add(bttod);
                dto.setStlPayOrdersList(spodList);
                baseTradeProductDealDtos.add(dto);
            }
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            //        委托代码不为空处理委托代码
            if(StringUtil.isNotEmpty(entity.getEntrustId())){
                dto.setEntrustId(entity.getEntrustId());
            }

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);
            entity.setActPortfolioId(entity.getPortfolioId());
            entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
    }

    /**
     * 代办中提交流程启动时
     */
    @Override
    public void doUpdate(TrdPlanCcyInvestTradeConfirmDto entity) {
        if (StringUtil.equals(entity.getOperateType(), "save")) {
            restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/doUpdate", entity,
                Integer.class);
        }
        if (StringUtil.equals(entity.getOperateType(), "submit")) {
            doDelete(entity.getWorkflowId());
            prepareEntity(entity);
            if(StringUtil.isNotEmpty(entity.getActPortfolioId()) && StringUtil.isNotEmpty(entity.getTrdProductDealAdd())) {
                entity.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
            }
            restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/doUpdate", entity,
                Integer.class);
            if (StringUtil.equals(entity.getPs(), DictCodeList.交易方向_赎回确认.getValue())
                    && "N".equals(CacheUtil.getSysParam("is_auto_redemption"))) {
                doInsertRedemption(entity);
            }
            FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
            finProductTradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
            BaseTradeProductDealDto dto = new BaseTradeProductDealDto();
            dto.setTrdProductDeal(entity);
            dto.setTrdProductDealAdd(entity.getTrdProductDealAdd());
            // 划款指令
            List<StlPayOrdersDto> spodList = new ArrayList<>();
            for (BussTrdTransferOrderDto bttod : entity.getBussTrdTransferOrder()) {
                spodList.add(bttod);
                dto.setStlPayOrdersList(spodList);
                baseTradeProductDealDtos.add(dto);
            }
            finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);

            // 撤销时，子组合需要调估值共通
            if (entity.getActPortfolioId() != null && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(finProductTrade -> {
                    finProductTrade.setActPortfolioId(entity.getActPortfolioId());
                });
            }
            baseTradeProductDealCommService.execute(finProductTradeDto);
            if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(StringUtil.equals(baseTradeProductDealDto1.getTrdProductDealAdd().getPortfolioId(), entity.getPortfolioId()) ? entity.getActPortfolioId() : entity.getPortfolioId()));
                baseTradeProductDealCommService.insertPosition(finProductTradeDto);
                finProductTradeDto.getBaseTradeProductDealDtos().forEach(
                        baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
            }
        }
    }

    /**
     * 流程作废时删除业务流程表 <功能详细描述>
     * 
     * @param workflowId
     * @see
     * @since 1.0
     */
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommService.delete(workflowId);
        restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                                  + "/TrdPlanCcyInvestTrade/doDelete?workflowId=" + workflowId,Integer.class);
    }

    /**
     * 查询反显数据 <功能详细描述>
     * 
     * @param Tid
     * @return
     * @see
     * @since 1.0
     */
    public TrdPlanCcyInvestTradeConfirmDto getTrdFundDto(String Tid,String feeType) {
        //YUCHUBS-647 交易查询时费用为空 guyunfei 20190910 start
        return restTemplate.getForObject(ServiceApplication.TRADE_SERVICE_NAME
                                         + "/TrdPlanCcyInvestTrade/getTrdFundDto?tradId=" + Tid + "&feeType=" + feeType,
            TrdPlanCcyInvestTradeConfirmDto.class);
        //YUCHUBS-647 交易查询时费用为空 guyunfei 20190910 end
    }

    /**
     * 查询单位净值 <功能详细描述>
     * 
     * @param map
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPrice(Map<String, Object> map) {
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/getPrice", map,
            BigDecimal.class);
    }

    /**
     * 获取资产代码的交易方式 <功能详细描述>
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public Map<String, Object> getPurSpeed(Map<String, Object> param) {
        return restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/getPurSpeed", param,
            Map.class);
    }

    /**
     * 插入交易到账信息 <功能详细描述>
     * 
     * @param entity
     * @see
     * @since 1.0
     */
    public void doInsertRedemption(TrdPlanCcyInvestTradeConfirmDto entity) {
        UploadFile file = null;
        List<SysUploadFileInfo> syfiList = entity.getFileArray();
        for (SysUploadFileInfo sufi : syfiList) {
            file = fileAttachmentProcess.fetch(sufi.getSeqNo());
            String generateId = IDGenerator.generate();
            file.setFileNo(generateId);
            fileAttachmentProcess.upload(file);
            sufi.setSeqNo(generateId);
        }
        TrdPlanCcyInvestTradeConfirmDto redEntity = new TrdPlanCcyInvestTradeConfirmDto(); 
        BeanUtil.copyPropertiesIgnoreNull(redEntity, entity);
        redEntity.setbTradeId(entity.getTradeId());
        if (baseGetBusinessId != null) {
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易);
            if(StringUtil.equals(redEntity.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())){
                codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
            }else if(StringUtil.equals(redEntity.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())){
                codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
            }else if(StringUtil.equals(redEntity.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())){
                codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
            }
            redEntity.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }
        else {
            redEntity.setTradeId(IDGenerator.generate());
        }
        redEntity.setPs(DictCodeList.交易方向_赎回到账.getValue());
        String actportfolioId = entity.getActPortfolioId();
        prepareEntity(redEntity);
        if(StringUtil.isNotEmpty(actportfolioId)) {
            redEntity.setActPortfolioId(actportfolioId);
        }
        
        restTemplate.postForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/doInsertRedemption",
            redEntity, void.class);
    }
    /**
     * 
     * 查询业务流程表交易到账信息
     * <功能详细描述>
     * @param tradeId
     * @return
     * @see
     * @since 1.0
     */
    public TrdPlanCcyInvestTradeConfirmDto getTrdFundDtoForAccount(String tradeId){
       return restTemplate.getForObject(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/getTrdFundDtoForAccount?tradeId="+tradeId, TrdPlanCcyInvestTradeConfirmDto.class);
    }
    /**
     * 
     * 查询业务流程表中的划款指令
     * <功能详细描述>
     * @param bizCode
     * @return
     * @see
     * @since 1.0
     */
    public List<BussTrdTransferOrderBase> selectOrdersByBizCode(String bizCode) {
        return restTemplate.getForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/selectOrdersByBizCode?bizCode="+bizCode, BussTrdTransferOrderBase.class);
    }
    /**
     * 处理基金投资交易自定义dto <功能详细描述>
     * 
     * @param dto
     * @see
     * @since 1.0
     */
    private void prepareEntity(TrdPlanCcyInvestTradeConfirmDto dto) {
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
//        dto.setTradeAmt(dto.getPrinAmt());
        dto.setYield(ArithmeticUtil.divide(dto.getYield(), new BigDecimal(100)));
        dto.setExerYield(ArithmeticUtil.divide(dto.getExerYield(), new BigDecimal(100)));
        dto.setBranch(Long.valueOf(CacheUtil.getSysParam("noBranchNoValue")));
        dto.setIsCancel(DictCodeList.是否_否.getValue());
        dto.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        dto.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        dto.setIsClean(DictCodeList.是否_是.getValue());
        dto.setFeeAmt(ArithmeticUtil.doAdd(dto.getFeeAmtPay(), dto.getChannelFee()));
        if(StringUtil.isNotEmpty(dto.getRedCfmDate()) &&
                StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), dto.getPs())){
            dto.setSettleDate(dto.getRedCfmDate());
        }else {
            dto.setSettleDate(dto.getTradeDate());
        }
        dto.setIssuePartyId(CacheUtil.getSysParam(BussinessConstant.ISSUE_PARTY_ID));
        TrdInvestPersonInfo002Dto personInfo = dto.getTrdInvestPersonInfo();
        if (personInfo != null) {
            dto.setTrader(personInfo.getTrader());
            dto.setCounterId(personInfo.getCounterId());
            dto.setIsOutTrade(personInfo.getIsOutTrade());
            dto.setCounterTrader(personInfo.getCounterTrader());
            dto.setCounterContact(personInfo.getContactInfo());
        }
        // 金融产品交易附属表
        TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
        trdProductDealAdd.setRedCfmDate(dto.getRedCfmDate());
        trdProductDealAdd.setPurCfmDate(dto.getTradeDate());
        trdProductDealAdd.setTradeId(dto.getTradeId());
        trdProductDealAdd.setPortfolioId(dto.getPortfolioId());
        trdProductDealAdd.setEntrustId(dto.getEntrustId());
//        trdProductDealAdd.setProdId(dto.getFinprodId());
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        dto.setTrdProductDealAdd(trdProductDealAdd);
        // 组合交易表
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setBusiNo(dto.getTradeId());
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiType(DictCodeList.共通业务类型_计划类资产_货币型_交易.getValue());
        ptlTrade.setSettleDate(dto.getSettleDate());
        ptlTrades.add(ptlTrade);
        if (!dto.getPortfolioId().equals(dto.getActPortfolioId())) {
            // 组合交易表
            PtlTrade ptlTrade1 = new PtlTrade();
            ptlTrade1.setPortfolioId(dto.getActPortfolioId());
            ptlTrade1.setBusiNo(dto.getTradeId());
            ptlTrade1.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
            ptlTrade1.setBusiId(dto.getFinprodId());
            ptlTrade1.setBusiType(DictCodeList.共通业务类型_计划类资产_净值型_交易.getValue());
            ptlTrade1.setSettleDate(dto.getSettleDate());
            ptlTrades.add(ptlTrade1);
        }
        dto.setPtlTrade(ptlTrades);
        // 划款指令
        if (StringUtil.equals(DictCodeList.交易方向_申购确认.getValue(), dto.getPs())) {
            List<BussTrdTransferOrderBase> bttob = bussSettleOrderCommService.selectOrdersByBizCode(
                dto.getbTradeId());
            if (bttob != null && bttob.size() > 0) {
                List<BussTrdTransferOrderDto> bttodList = new ArrayList<>();
                BussTrdTransferOrderDto bttod = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(bttod, bttob.get(0));
                bttodList.add(bttod);
                dto.setBussTrdTransferOrder(bttodList);
            }
        }
        if (dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().size() > 0) {
            dto.getBussTrdTransferOrder().get(0).setTradeId(dto.getTradeId());
            dto.setBussTrdTransferOrder(
                bussTrdTransferOrderService.save(dto.getBussTrdTransferOrder()));
            dto.getBussTrdTransferOrder().get(0).setIsTotalTrade(DictCodeList.是否_否.getValue());
        }
        // 金融产品交易扩展
        PaTrdProductDeal paTrdProductDeal = new PaTrdProductDeal();
        paTrdProductDeal.setTradeId(dto.getTradeId());
        String paBussType = null;
        if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_申购申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_申购确认.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回申请.getValue();
        }
        else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回确认.getValue();
        }else if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回到账.getValue())){
            paBussType = DictCodeListExt1.平安业务类型_计划类资产_货币型_赎回到账.getValue();
        }
        paTrdProductDeal.setPaBussType(paBussType);
        paTrdProductDeal.setDeptCode(personInfo==null?null:personInfo.getTraderDept());
        dto.setPaTrdProductDeal(paTrdProductDeal);
    }

    @Override
    public void doBack(TrdPlanCcyInvestTradeConfirmDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
    }

    @Override
    public void doRevoke(TrdPlanCcyInvestTradeConfirmDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
    }
    
    public List<BaseEtrInvestInfoDto> getEntrustInfos(String entrustId, String ps) {
        List<BaseEtrInvestInfoDto> list = restTemplate.postForList(
            ServiceApplication.TRADE_SERVICE_NAME + "/TrdBondInvestTrade/getEntrustInfos/" + entrustId, null,
            BaseEtrInvestInfoDto.class);
        if (CollectionUtils.isNotEmpty(list)) {
            for (BaseEtrInvestInfoDto dto : list) {
                // 设置可委托金额
                dto.setDoEntrustMoney(
                    selectDoEntrustMoney(dto.getEtrEntrust().getEntrustId(), ps));
            }
        }
        return list;
    }
    
    /**
     * 查询剩余委托额度
     * @param entrustId
     * @return
     */
    public BigDecimal selectDoEntrustMoney(String entrustId, String ps) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("entrustId", entrustId);
    	param.put("ps", ps);
    	param.put("TRADE_STATUS", DictCodeListExt1.交易状态_初始.getValue());
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdPlanCcyInvestTrade/getDoEntrustMoneyByEntrustId/", param,
                BigDecimal.class);
    }

    // delete by etliu 20210917 start
    // 只需要对申请环节做授权检查，确认以及到账环节不需要授权检查
//    @Override
//    public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
//        Map<String, Object> variables = new HashMap<>();
//        TrdPlanCcyInvestTradeConfirmDto dto = this.getBussinessEntity(entity);
//        if (taskId != null) {
//        	 try {
//             	if(StringUtil.equals(dto.getPs(), DictCodeList.交易方向_赎回.getValue())){
////             		return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getShareAmt(), LoginUserUtil.getUserName(),
////             				DictCodeList.平安资产类型_计划类资产_货币型.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
////             				// 资产类型改为授权类型 add by renping 2019-08-06  start
////             				, dto.getFinprodId()
////             				// 资产类型改为授权类型 add by renping 2019-08-06  end
////             				);
//                     Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getShareAmt(),LoginUserUtil.getUserName(),
//                         DictCodeList.平安资产类型_计划类资产_货币型.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
//                         , dto.getFinprodId() ,entity.getWorkflowId());
//
//                     return commonBusinessService.是否需要上级审批(params);
//             	}else{
////             		return commonBusinessService.是否需要上级审批(dto.getCounterId(), dto.getTradeAmt(), LoginUserUtil.getUserName(),
////             				DictCodeList.平安资产类型_计划类资产_货币型.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
////             				// 资产类型改为授权类型 add by renping 2019-08-06  start
////             				, dto.getFinprodId()
////             				// 资产类型改为授权类型 add by renping 2019-08-06  end
////             				);
//                     Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getTradeAmt(),LoginUserUtil.getUserName(),
//                         DictCodeList.平安资产类型_计划类资产_货币型.getValue(), dto.getType6(), dto.getTradeMarket(), dto.getPs()
//                         , dto.getFinprodId() ,entity.getWorkflowId());
//                     params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
//                     return commonBusinessService.是否需要上级审批(params);
//             	}
//             }
//            catch (WorkFlowException e) {
//                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
//                throw new WorkFlowException(e);
//            }
//        }
//        else {
//            return null;
//        }
////        return null;
//    }
    // delete by etliu 20210917 end
    public void doCompleteRedemption(TrdPlanCcyInvestTradeConfirmDto entity) {
        {
            UploadFile file = null;
            List<SysUploadFileInfo> syfiList = entity.getFileArray();
            for (SysUploadFileInfo sufi : syfiList) {
                file = fileAttachmentProcess.fetch(sufi.getSeqNo());
                String generateId = IDGenerator.generate();
                file.setFileNo(generateId);
                fileAttachmentProcess.upload(file);
                sufi.setSeqNo(generateId);
            }
            TrdPlanCcyInvestTradeConfirmDto redEntity = new TrdPlanCcyInvestTradeConfirmDto();
            BeanUtil.copyPropertiesIgnoreNull(redEntity, entity);
            redEntity.setbTradeId(entity.getTradeId());
            if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_计划类资产_货币型_交易);
                if(StringUtil.equals(redEntity.getType7(), DictCodeList.计划类资产二级分类_委托债权.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托债权);
                }else if(StringUtil.equals(redEntity.getType7(), DictCodeList.计划类资产二级分类_委托权益.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_委托权益);
                }else if(StringUtil.equals(redEntity.getType7(), DictCodeList.计划类资产二级分类_自主管理.getValue())){
                    codeParameter.setType1(DictCodeList.计划类资产二级分类_自主管理);
                }
                redEntity.setTradeId(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                redEntity.setTradeId(IDGenerator.generate());
            }
            redEntity.setPs(DictCodeList.交易方向_赎回到账.getValue());
            String actportfolioId = entity.getActPortfolioId();
            prepareEntity(redEntity);
            if(StringUtil.isNotEmpty(actportfolioId)) {
                redEntity.setActPortfolioId(actportfolioId);
            }
            doComplete(redEntity,OperationType.NEW);
        }
    }
}
