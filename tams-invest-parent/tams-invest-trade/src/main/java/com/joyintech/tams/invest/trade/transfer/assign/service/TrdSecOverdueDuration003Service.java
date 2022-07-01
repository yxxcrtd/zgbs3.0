package com.joyintech.tams.invest.trade.transfer.assign.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.dto.PtlRecePositionFlowDto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.autoperiod.ptlreceinfo.BasePtlReceInfoService;
import com.joyintech.tams.invest.common.autoperiod.ptlreceinfo.dto.PtlReceInfo;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.trade.transfer.assign.dto.TrdSecOverdueDuration003Dto;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资产到期结转Service
 * @Description TrdSecOverdueDuration003Service
 * @auther 丁志鹏
 * @create 2019-12-24 15:41
 */
@Service("trdSecOverdueDuration003ServiceEx")
public class TrdSecOverdueDuration003Service extends BaseService<TrdSecOverdueDuration003Dto> implements BaseProcessService<TrdSecOverdueDuration003Dto> {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(TrdSecOverdueDuration003Service.class);
    
    /**
     * 业务编号生成共通
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessIdImpl;
    /**
     * 存续期共通service
     */
    @Autowired(required = false)
    private AutoPeriodCommonService autoPeriodCommonService;
    /**
     * 应收款持仓共通Service
     */
    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;
    /**
     * 应收款持仓共通Service
     */
    @Autowired
    private BasePtlReceInfoService basePtlReceInfoService;
    /**
     * 交易共通
     */
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
//    /**
//     * 附件上传类
//     */
//    @Autowired
//    private PeriodCommonService periodCommonService;
    
    /**
	 * 投管接口共通组件
	 */
	@Autowired
	private InvestInterfCommService investInterfCommService;
    
    /**
     * 获取画面的业务编号
     * @return 生成的业务编号【交易代码，汇总交易编号】
     */
    public String genBusiCode(){
        if(baseGetBusinessIdImpl != null){
            BussinessCodeParameter parameter = new BussinessCodeParameter();
            parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
            return baseGetBusinessIdImpl.buildBussinessCode(parameter);
        }
        return IDGenerator.generate();
    }

    /**
     *  查询需要初始化到画面的数据
     * @param map 查询参数
     * @return 匹配结果
     */
    public TrdSecOverdueDuration003Dto findTFEffData(Map<String, Object> map) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/findTFEffData",
                map,TrdSecOverdueDuration003Dto.class);
    }

    /**
     * 画面组合代码
     * @param param 初始数据
     * @return 查询的结果
     */
    public Page<SelectOptionDto> selectPortfolioId(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/selectPortfolioId",
                param,SelectOptionDto.class);
    }

    /**
     * 获取资产转让信息
     * @param param 查询参数
     * @return 转让结果
     */
    public Map<String,Object> genAssetTfInfo(Map<String, Object> param) {
        param.put("platDate",DateUtil.format(DateUtil.platDateFromCache()));
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/genAssetTfInfo",
                param,Map.class);
    }

    @Override
    public void doInsert(TrdSecOverdueDuration003Dto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTotleTradeId());
        }
        entity.setCreateUser(SecurityUtil.getLoginUser().getUserName());
        entity.setCreateDept(SecurityUtil.getLoginUser().getDeptCode());
        Date date = new Date();
        entity.setCreateTime(date);
        entity.setUpdateTime(date);
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        setWorkflowId(entity.getBaseTotalTradeInfoDto(), entity.getWorkflowId());
        saveDataWhenStartWorkflow(entity, type);
    }
    @Override
    public void doUpdate(TrdSecOverdueDuration003Dto entity) {
        if (!StringUtil.equals("revoke", entity.getOperateType())
                && !StringUtil.equals("delete", entity.getOperateType())) {
            if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                    && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
                // 将附件和当前业务对象绑定（更新附件的业务代码）
                fileAttachmentProcess.updateBussinessNo(
                        entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                                Collectors.toList()),
                        entity.getTotleTradeId());
            }
            entity.setBaseTotalTradeInfoDto(genFinProductTradeDto(entity));
            ProcessMark newEntity;
            try {
                newEntity = WorkFlowUtil.getBusinessObjectFromRequest(TrdSecOverdueDuration003Dto.class, true);
                // 工作流ID
                newEntity.setWorkflowId(entity.getWorkflowId());
                newEntity.setStatus(entity.getStatus());
                newEntity.setBusinessData(JsonUtils.object2Json(entity));
                // 更新业务表数据
                // 操作留痕表
                WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
            saveDataWhenStartWorkflow(entity, OperationType.MODIFY);
        }
    }

    /**
     * 保存数据
     * @param entity 画面数据
     * @param type 操作类型
     */
    public void saveDataWhenStartWorkflow(TrdSecOverdueDuration003Dto entity, OperationType type) {
        if (BussinessConstant.SUBMIT.equals(entity.getOperateType()) && type != OperationType.REVOKE) {
            // 插入Entity到对应的业务流程DB中
            if (entity.getNeedSkipInsertWfl() == null || !entity.getNeedSkipInsertWfl()) {
                restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdPlanInterestDuration/doInsert",
                        entity, String.class);
            }
            if (type != OperationType.REVOKE) {
                execute交易共通(entity, DictCodeListExt1.交易流程状态_流程中);
            }
        }
        else if (type == OperationType.REVOKE) {
            restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/doRevoke", entity,
                    String.class);
        }
    }

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
    }

    @Override
    public void doBack(TrdSecOverdueDuration003Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        doDelete(entity.getWorkflowId());
    }

    /**
     * 持仓流水共通
     * @param entity 总数据
     * @param 交易流程状态 交易流程状态
     */
    private void execute交易共通(TrdSecOverdueDuration003Dto entity, DictCodeListExt1 交易流程状态) {
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        tradeDto.setWorkflowStatus(交易流程状态.getValue());
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        
        if (DictCodeListExt1.交易流程状态_撤销 != 交易流程状态) {
            entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
//                调用 组合持仓，现金流水 ，委托指令持仓，委托指令流水，应收款持仓| 同各资产的买入卖出
                List<BaseTradeInfoDto> 交易list = baseTradeInfoDto.getBaseTradeInfoDtoList();
//                调用 组合持仓，现金流水 ，委托指令持仓，委托指令流水 | 同各资产的买入卖出
                List<BaseTradeInfoDto> 罚息list = baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList();
                交易list.forEach(交易info->{
                    StlPayOrders stlPayOrders = null;
                    if(StringUtil.equals(交易info.getTrdProductDeal().getPs(), DictCodeList.交易方向_应收款项转出.getValue())){
                        stlPayOrders = entity.getBaseTotalTradeInfoDto().getStlPayOrders();
                    }
                    else{
                        stlPayOrders = baseTradeInfoDto.getStlPayOrders();
                    }
                    BaseTradeProductDealDto 交易Deal = tranferTradeInfo(交易info,stlPayOrders,entity.getTotleTradeId(),false);
                    交易Deal.getTrdProductDeal().setShareAmt(BigDecimal.ZERO);
                    交易Deal.getTrdProductDeal().setPrinAmt(BigDecimal.ZERO);
                    baseTradeProductDealDtos.add(交易Deal);
                });
                罚息list.forEach(罚息info->{
                	StlPayOrders stlPayOrders = null;
                    if(StringUtil.equals(罚息info.getTrdProductDeal().getPs(), DictCodeList.交易方向_卖出.getValue())){
                        stlPayOrders = entity.getBaseTotalTradeInfoDto().getStlPayOrders();
                    }
                    else{
                        stlPayOrders = baseTradeInfoDto.getStlPayOrders();
                    }
                    BaseTradeProductDealDto 罚息Deal = tranferTradeInfo(罚息info,stlPayOrders,entity.getTotleTradeId(),false);
                    罚息Deal.getTrdProductDeal().setShareAmt(BigDecimal.ZERO);
                    罚息Deal.getTrdProductDeal().setPrinAmt(BigDecimal.ZERO);
                    baseTradeProductDealDtos.add(罚息Deal);
                });
            });
            tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            // 投管接口新增  wqd 20200227
            for(BaseTradeProductDealDto dealDto : baseTradeProductDealDtos) {
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        		investInterfCommDto.setPageId("TrdSecOverdueDuration001");
        		investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_新建.getValue());
        		investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
        		investInterfCommDto.setTrdProductDeal(dealDto.getTrdProductDeal());
        		List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
        		BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersList,dealDto.getStlPayOrdersList());
        		investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
        		investInterfCommService.doInvestInterf(investInterfCommDto);
            }
        }
        else{
            if(entity.getBaseTotalTradeInfoDto()!= null && CollectionUtils.isNotEmpty(entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList())){
                entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
//                调用 组合持仓，现金流水 ，委托指令持仓，委托指令流水，应收款持仓| 同各资产的买入卖出
                    List<BaseTradeInfoDto> 交易list = baseTradeInfoDto.getBaseTradeInfoDtoList();
//                调用 组合持仓，现金流水 ，委托指令持仓，委托指令流水 | 同各资产的买入卖出
                    List<BaseTradeInfoDto> 罚息list = baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList();
                     交易list.forEach(交易info->{
                        StlPayOrders stlPayOrders = null;
                        if(StringUtil.equals(交易info.getTrdProductDeal().getPs(),DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(交易info.getTrdProductDeal().getPs(), DictCodeList.交易方向_应收款项转出.getValue())){
                            stlPayOrders = entity.getBaseTotalTradeInfoDto().getStlPayOrders();
                        }
                        else{
                            stlPayOrders = baseTradeInfoDto.getStlPayOrders();
                        }
                        baseTradeProductDealDtos.add(tranferTradeInfo(交易info,stlPayOrders,entity.getTotleTradeId(),true));
                    });
                    罚息list.forEach(罚息info->{
                        StlPayOrders stlPayOrders = null;
                        if(StringUtil.equals(罚息info.getTrdProductDeal().getPs(),DictCodeList.交易方向_卖出.getValue())){
                            stlPayOrders = entity.getBaseTotalTradeInfoDto().getStlPayOrders();
                        }
                        else{
                            stlPayOrders = baseTradeInfoDto.getStlPayOrders();
                        }
                        baseTradeProductDealDtos.add(tranferTradeInfo(罚息info,stlPayOrders,entity.getTotleTradeId(),true));
                    });
                });
                tradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
            }
            // 投管接口新增  wqd 20200227
            for(BaseTradeProductDealDto dealDto : baseTradeProductDealDtos) {
                InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        		investInterfCommDto.setPageId("TrdSecOverdueDuration001");
        		investInterfCommDto.setTradeStatus(DictCodeListExt1.画面数据状态_撤销.getValue());
        		investInterfCommDto.setPortfolioId(entity.getActPortfolioId());
        		investInterfCommDto.setTrdProductDeal(dealDto.getTrdProductDeal());
        		List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
        		BeanUtil.copyPropertiesIgnoreNull(stlPayOrdersList,dealDto.getStlPayOrdersList());
        		investInterfCommDto.setStlPayOrdersList(stlPayOrdersList);
        		investInterfCommService.doInvestInterf(investInterfCommDto);
            }
            //            TODO 留痕表 不存在场景 （暂不处理）
        }
        baseTradeProductDealCommServiceImpl.execute(tradeDto);
//        母子组合不同时直接调用
        if(StringUtil.isNotEmpty(entity.getActPortfolioId())&&!StringUtil.equals(entity.getActPortfolioId(),entity.getPortfolioId())){
            baseTradeProductDealDtos.forEach(temp->{
                temp.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
            });
            baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
        }
        //        调用应收款持仓共通
        if(交易流程状态 != DictCodeListExt1.交易流程状态_流程中){
            Map<String,Object> mapReceInfo = new HashMap<>();
            mapReceInfo.put("finprodId",entity.getFinprodId());
            PtlReceInfo 应收款标的dto = null;
            PtlReceInfo 本金应收款标的 = null;
            PtlReceInfo 利息应收款标的 = null;
            if(entity.getBjBranch()!=null){
                mapReceInfo.put("branch",entity.getBjBranch());
                本金应收款标的 = basePtlReceInfoService.genPtlReceInfo(mapReceInfo);
            }
            if(entity.getLiBranch()!=null){
                mapReceInfo.put("branch",entity.getLiBranch());
                利息应收款标的 = basePtlReceInfoService.genPtlReceInfo(mapReceInfo);
            }
            应收款标的dto = 本金应收款标的 != null ? 本金应收款标的:利息应收款标的;
            List<BaseTradeInfoDto> 交易list = entity.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList().get(0).getBaseTradeInfoDtoList();
            List<PtlRecePositionFlowDto>ptlRecePositionFlowList = new ArrayList<>();
            for(BaseTradeInfoDto tradeInfo:交易list){
                if((tradeInfo.getTrdProductDeal().getPrinAmt()==null||tradeInfo.getTrdProductDeal().getPrinAmt().compareTo(BigDecimal.ZERO)==0)
                &&tradeInfo.getTrdProductDeal().getIntAmt()!=null){
                    gen应收款持仓流水(tradeInfo,entity,ptlRecePositionFlowList,利息应收款标的);
                }
                else{
                    gen应收款持仓流水(tradeInfo,entity,ptlRecePositionFlowList,应收款标的dto);
                }
            }
//            撤销反向处理
            if(交易流程状态.equals(DictCodeListExt1.交易流程状态_撤销) &&CollectionUtils.isNotEmpty(ptlRecePositionFlowList)){
                ptlRecePositionFlowList.forEach(item->{
                    item.setTradeId(StringUtil.endsWith(item.getTradeId(),"R")?item.getTradeId():item.getTradeId()+"R");
                    item.setAmount(item.getAmount().negate());
                });
            }
//            入应收款持仓流水
            if(CollectionUtils.isNotEmpty(ptlRecePositionFlowList)){
                ptlRecePositionFlowList.forEach(flowDto->{
                    if(StringUtil.isNotEmpty(flowDto.getPortfolioId())){
                        basePtlRecePositionService.inertRecePositionFlow(flowDto);
                    }
                });
            }
        }
    }

    /**
     * 根据交易信息生成应收款持仓流水
     * @param tradeInfo 交易信息
     * @param entity 总数据
     * @param ptlRecePositionFlowList 生成结果的容器
     * @应收款标的 应收款标的
     */
    private void gen应收款持仓流水(BaseTradeInfoDto tradeInfo, TrdSecOverdueDuration003Dto entity, List<PtlRecePositionFlowDto> ptlRecePositionFlowList,PtlReceInfo 应收款标的) {
//        组合代码								画面.组合代码							注：如果画面.组合代码组合类型为子组合时，对应的母组合代码需要再调用一次
//        证券管理账户/通道代码								画面.证券账户/通道
//        金融产品代码								画面.金融产品代码
//        分支序号								本金应收款标的dto.分支序号
//        投资目的								同金融产品交易设置的投资目的
//        持仓类型								数据字典.持仓类型.实际
//        发生日期								画面.转让日期
//        应收款类型								数据字典.应收款类型.逾期未收本金
//        计划支付日期								本金应收款标的dto.调整后的计划支付日
//        币种								本金应收款标的dto.币种
//        数量								金融产品交易.交易本金*-1
//        母金融产品代码
//        交易编号								金融产品交易.交易编号
        TrdProductDealAdd addDeal = tradeInfo.getTrdProductDealAdd();
        PtlRecePositionFlowDto ptlRecePositionFlow = new PtlRecePositionFlowDto();
        TrdProductDeal deal = tradeInfo.getTrdProductDeal();
        BigDecimal amount = deal.getIntAmt() != null ? deal.getIntAmt():deal.getPrinAmt();
        ptlRecePositionFlow.setSecAcctId(entity.getSecAcctId());
        ptlRecePositionFlow.setFinprodId(entity.getFinprodId());
        ptlRecePositionFlow.setBranch(deal.getBranch());
        ptlRecePositionFlow.setCdate(entity.getTransferDate());
        ptlRecePositionFlow.setTradeId(deal.getTradeId());
        ptlRecePositionFlow.setCcy(deal.getCcy());
        ptlRecePositionFlow.setInputType(DictCodeListExt1.录入方式_手工录入.getValue());
        ptlRecePositionFlow.setPayDate(deal.getMdate());
        ptlRecePositionFlow.setHodingType(DictCodeList.持仓类型_实际.getValue());
//        取出应收款类型
        ptlRecePositionFlow.setRecType(deal.getStatus());
        ptlRecePositionFlow.setInvAim(StringUtil.isNotEmpty(deal.getInvAim())?deal.getInvAim():"999999");
        ptlRecePositionFlowList.add(ptlRecePositionFlow);
        if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(deal.getPs(),DictCodeList.交易方向_应收款项转出.getValue())){
            ptlRecePositionFlow.setPortfolioId(entity.getPortfolioId());
            amount = amount.negate();
            ptlRecePositionFlow.setAmount(amount);
            if(!StringUtil.equals(entity.getPortfolioId(),entity.getActPortfolioId())){
                PtlRecePositionFlowDto subPtlRece = new PtlRecePositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(subPtlRece,ptlRecePositionFlow);
                subPtlRece.setPortfolioId(entity.getActPortfolioId());
                ptlRecePositionFlowList.add(subPtlRece);
            }
        }
        else{
            String 实际交易对手 = entity.getTrdInvestPersonInfo().getCounterId();
            ptlRecePositionFlow.setPortfolioId(实际交易对手);
            ptlRecePositionFlow.setAmount(amount);
            String 母交易对手 = AllTradeCommonUtil.exchangePortfolio(实际交易对手);
            if(!StringUtil.equals(实际交易对手,母交易对手)&&StringUtil.isNotEmpty(母交易对手)){
                PtlRecePositionFlowDto subPtlRece = new PtlRecePositionFlowDto();
                BeanUtil.copyPropertiesIgnoreNull(subPtlRece,ptlRecePositionFlow);
                subPtlRece.setPortfolioId(母交易对手);
                ptlRecePositionFlowList.add(subPtlRece);
            }
        }
    }

    /**
     * 转换存储dto 为共通dto
     * @param 交易info 交易信息
     * @param stl 划款指令
     * @return 组装结果
     */
    private BaseTradeProductDealDto tranferTradeInfo(BaseTradeInfoDto 交易info,StlPayOrders stl,String tradeId,boolean isRevoke) {
        TrdProductDeal dealExe = new TrdProductDeal();
        TrdProductDeal deal = 交易info.getTrdProductDeal();
        BeanUtils.copyProperties(deal, dealExe);
        if(StringUtil.equals(deal.getPs(), DictCodeList.交易方向_应收款项转出.getValue())){
            dealExe.setPs(DictCodeList.交易方向_卖出.getValue());
        }
        else if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_应收款项转入.getValue())){
            dealExe.setPs(DictCodeList.交易方向_买入.getValue());
        }
        TrdProductDealAdd dealAdd = 交易info.getTrdProductDealAdd();
        TrdProductDealAdd dealAddExe = new TrdProductDealAdd();
        BeanUtils.copyProperties(dealAdd, dealAddExe);
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(dealAddExe);
        StlPayOrdersDto stlExe = new StlPayOrdersDto();
        BeanUtil.copyPropertiesIgnoreNull(stlExe,stl);
        stlExe.setSettleAmt(dealExe.getTradeAmt());
        baseTradeProductDealDto.setStlPayOrdersList(Collections.singletonList(stlExe));
        if(isRevoke){
            dealExe.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            String tradeIdR = dealExe.getTradeId();
            tradeIdR+=tradeIdR.endsWith("R")?"":"R";
            dealExe.setTradeId(tradeIdR);
            dealAddExe.setTradeId(tradeIdR);
            stlExe.setTradeId(tradeIdR);
        }
        if(StringUtil.isNotEmpty(tradeId)){
            dealExe.setTradeId(tradeId);
            dealAddExe.setTradeId(tradeId);
            stlExe.setTradeId(tradeId);
        }
        return baseTradeProductDealDto;
    }

    /**
     * 转换所需dto
     * @param dto 待转换数据
     * @return 转换结果
     * 【说明dto转换结果->
     * baseTradeInfoDto：转出交易的汇总信息
     * baseTradeInfoDto.baseTrade ：转入的汇总信息
     * baseTradeInfoDto.baseTradeInfoDtoList ：本金利息相关的信息（本金利息转入转出）
     * baseTradeInfoDto.baseTrade.baseTradeInfoDtoList : 本金利息的罚息相关交易信息（本金罚息利息罚息转入转出）】
     */
    public BaseTotalTradeInfoDto genFinProductTradeDto(TrdSecOverdueDuration003Dto dto) {
        //实际交易组合处理
        dto.setActPortfolioId(dto.getPortfolioId());
        dto.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(dto.getPortfolioId()));
        BaseTotalTradeInfoDto result = new BaseTotalTradeInfoDto();
//        金融产品交易汇总表及相关_转入Entity
//        金融产品交易汇总表及相关_转出Entity
//        金融产品交易及相关-本金转入Entity
//        金融产品交易及相关-利息转入Entity
//        金融产品交易及相关-本金罚息_转入Entity
//        金融产品交易及相关-利息罚息_转入Entity
//        金融产品交易及相关-本金转出Entity
//        金融产品交易及相关-利息转出Entity
//        金融产品交易及相关-本金罚息_转出Entity
//        金融产品交易及相关-利息罚息_转出Entity
//        TrdFinProductDealTotal total = gen汇总交易信息(dto);
//        if (传入.本金分支序号 不为空时)
//        本金应收款标的dto = 调用共通【应收款标的共通】.标的查询(传入.金融产品代码，传入.本金分支序号)
//        if (传入.利息分支序号 不为空时)
//        利息应收款标的dto = 调用共通【应收款标的共通】.标的查询(传入.金融产品代码，传入.利息分支序号)
//        end
//        if（本金应收款标的dto != null）
//        应收款标的dto = 本金应收款标的dto
//        else
//        应收款标的dto = 利息应收款标的dto
//        end
        Map<String,Object> mapReceInfo = new HashMap<>();
        mapReceInfo.put("finprodId",dto.getFinprodId());
        PtlReceInfo 应收款标的dto = null;
        PtlReceInfo 本金应收款标的 = null;
        PtlReceInfo 利息应收款标的 = null;
        if(dto.getBjBranch()!=null){
            mapReceInfo.put("branch",dto.getBjBranch());
            本金应收款标的 = basePtlReceInfoService.genPtlReceInfo(mapReceInfo);
        }
        if(dto.getLiBranch()!=null){
            mapReceInfo.put("branch",dto.getLiBranch());
            利息应收款标的 = basePtlReceInfoService.genPtlReceInfo(mapReceInfo);
        }
        应收款标的dto = 本金应收款标的 != null ? 本金应收款标的:利息应收款标的;
        dto.setMdate(应收款标的dto.getMdate());
        List<BaseTradeInfoDto> baseTotalTradeInfoDtoList = new ArrayList<>();
        result.setBaseTradeInfoDtoList(baseTotalTradeInfoDtoList);
//        转出汇总
//        BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
        TrdFinProductDealTotal total转出 = gen汇总交易信息(dto,应收款标的dto);
        result.setTrdFinProductDealTotal(total转出);
//        baseTradeInfoDto.setTrdFinProductDealTotal(total转出);
//        转出汇总扩展
        PaTrdFinProductDealTotal paTotal转出 = gen汇总扩展交易(dto);
        result.setPaTrdFinProductDealTotal(paTotal转出);
//        baseTradeInfoDto.setPaTrdFinProductDealTotal(paTotal转出);
//        转出汇总附属
        TrdDealTotalAdd addTotal转出 = gen汇总扩展附属(dto);
        result.setTrdDealTotalAdd(addTotal转出);
//        baseTradeInfoDto.setTrdDealTotalAdd(addTotal转出);
//        转入
        BaseTradeInfoDto baseTrade = new BaseTradeInfoDto();
        baseTotalTradeInfoDtoList.add(baseTrade);
//        baseTradeInfoDto.setBaseTrade(baseTrade);
//        金融产品交易汇总Entity_转入
//		交易方向									买入
//		交易对手									画面.组合代码
//		其他同转出
//金融产品交易汇总附属表Entity_转出
//		同转出
//金融产品交易汇总扩展Entity_转出
//		同转出
        TrdFinProductDealTotal total转入 = new TrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(total转入,total转出);
        baseTrade.setTrdFinProductDealTotal(total转入);
        String 转入汇总交易编号 = genBusiCode();
        total转入.setTotleTradeId(转入汇总交易编号);
        total转入.setpTotleTradeId(total转出.getTotleTradeId());
        total转入.setPs(DictCodeList.交易方向_买入.getValue());
        total转入.setCounterId(dto.getActPortfolioId());
        total转入.setPortfolioId(total转出.getCounterId());
//         转入汇总扩展
        PaTrdFinProductDealTotal paTotal转入 = new PaTrdFinProductDealTotal();
        BeanUtil.copyPropertiesIgnoreNull(paTotal转入,paTotal转出);
        paTotal转入.setTotleTradeId(转入汇总交易编号);
        baseTrade.setPaTrdFinProductDealTotal(paTotal转入);
//        转入汇总附属
        TrdDealTotalAdd addTotal转入 = new TrdDealTotalAdd();
        BeanUtil.copyPropertiesIgnoreNull(addTotal转入,addTotal转出);
        addTotal转入.setTotleTradeId(转入汇总交易编号);
        baseTrade.setTrdDealTotalAdd(addTotal转入);
        //       划款指令
        List<BussTrdTransferOrderDto> stlPayOrdersList = autoPeriodCommonService.save(dto.getBussTrdTransferOrder());
        StlPayOrders 转出stl = autoPeriodCommonService.gen划款指令流程(stlPayOrdersList, total转出, dto.getWorkflowId());
        StlPayOrders 转入stl = gen转入划款指令(转出stl,total转出.getCounterId(),total转入.getTotleTradeId());
        result.setStlPayOrders(转出stl);
        baseTrade.setStlPayOrders(转入stl);

        mapReceInfo.put("portfolioId",dto.getActPortfolioId());
        mapReceInfo.put("secAcctId",dto.getSecAcctId());
//      本金相关交易
        List<BaseTradeInfoDto> 本金转出BaseTradeInfo = new ArrayList<>();
        List<BaseTradeInfoDto> 利息转出BaseTradeInfo = new ArrayList<>();
//        本金转出entity
        if(dto.getPrinAmt()!=null&&BigDecimal.ZERO.compareTo(dto.getPrinAmt()) != 0){
            //
            mapReceInfo.put("branch",本金应收款标的.getBranch());
            List<PtlRecePosition> 应收款持仓信息 = basePtlRecePositionService.genPtlPeceAvailableIntPosition(mapReceInfo);
            if(StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(),dto.getType8())){
                BigDecimal 累计扣减本金 = BigDecimal.ZERO;
                for(PtlRecePosition temp : 应收款持仓信息){
                    BigDecimal 本金 = temp.getAmount();
                    if(本金.compareTo(ArithmeticUtil.subtract(dto.getPrinAmt(),累计扣减本金))>0){
                        本金 = ArithmeticUtil.subtract(dto.getPrinAmt(),累计扣减本金);
                        if(本金.compareTo(BigDecimal.ZERO)==0){
                            break;
                        }
                    }
                    累计扣减本金 = ArithmeticUtil.doAdd(本金,累计扣减本金);
                    BaseTradeInfoDto 交易个体本金  = 生成交易个体本金(dto,total转出,本金,本金应收款标的.getCcy(),temp.getInvAim(),total转出.getSecManageAcctId());
                    交易个体本金.getTrdProductDeal().setBranch(本金应收款标的.getBranch());
                    交易个体本金.getTrdProductDeal().setStatus(DictCodeListExt3.应收款类型_逾期未收本金.getValue());
                    本金转出BaseTradeInfo.add(交易个体本金);
                }

            }
            else{
                BaseTradeInfoDto 交易个体本金  =生成交易个体本金(dto,total转出,dto.getPrinAmt(),本金应收款标的.getCcy(),"999999",total转出.getSecManageAcctId());
                交易个体本金.getTrdProductDeal().setBranch(本金应收款标的.getBranch());
                交易个体本金.getTrdProductDeal().setStatus(DictCodeListExt3.应收款类型_逾期未收本金.getValue());
                本金转出BaseTradeInfo.add(交易个体本金);
            }
        }
//        利息转出entity
        if(dto.getIntAmt()!=null&&BigDecimal.ZERO.compareTo(dto.getIntAmt()) != 0){
            mapReceInfo.put("branch",dto.getLiBranch());
            List<PtlRecePosition> 利息应收款信息 = basePtlRecePositionService.genPtlPeceAvailableIntPosition(mapReceInfo);
            BigDecimal 累计扣减金额 = BigDecimal.ZERO;
            for(PtlRecePosition positionDto: 利息应收款信息){
                BigDecimal 利息总额 = BigDecimal.ZERO;
//                if ( 画面.转让未支付利息 - 变量.累计扣减金额 >= 变量.利息应收款信息.数量)
//	                变量.利息总额 = 画面.转让未支付利息 - 累计扣减金额
//                else
//	                变量.利息总额 = 变量.利息应收款信息.数量
//                  end
//                  累计扣减金额 += 变量.利息总额
                if(ArithmeticUtil.subtract(dto.getIntAmt(),累计扣减金额).compareTo(positionDto.getAmount())>=0){
                    利息总额 = positionDto.getAmount();
                }
                else{
                    利息总额 = ArithmeticUtil.subtract(dto.getIntAmt(),累计扣减金额);
                }
                累计扣减金额 = ArithmeticUtil.doAdd(累计扣减金额,利息总额);
                String 投资目的 = StringUtil.equals(positionDto.getInvAim(),"99999")?null:positionDto.getInvAim();
                BaseTradeInfoDto 交易个体利息  = 生成交易个体利息(dto,total转出,利息总额,positionDto.getCcy(),投资目的,total转出.getSecManageAcctId());
                交易个体利息.getTrdProductDeal().setBranch(positionDto.getBranch());
                交易个体利息.getTrdProductDeal().setStatus(positionDto.getRecType());
                利息转出BaseTradeInfo.add(交易个体利息);
                if(累计扣减金额.compareTo(dto.getIntAmt())>=0){
                    break;
                }
            }
        }
        BigDecimal 已转让罚息 = BigDecimal.ZERO;
        List<BaseTradeInfoDto>本金罚息转出BaseTradeInfo = new ArrayList<>();
//        本金罚息转出entity
//        本金理论罚息 !=0 并且 转让罚息 !=0时录入
        if(dto.getTheoryPenaltyIntBj()!=null&&dto.getPenaltyInt()!=null&&BigDecimal.ZERO.compareTo(dto.getTheoryPenaltyIntBj())!=0&&BigDecimal.ZERO.compareTo(dto.getPenaltyInt())!=0){
//            处理罚息金额 设计书理解为多少条本金交易 多少条本金罚息 交易 罚息按照本金所占总金额占比分配罚息金额
            if(CollectionUtils.isNotEmpty(本金转出BaseTradeInfo)){
                for(BaseTradeInfoDto 本金TradeInfo : 本金转出BaseTradeInfo){
                    String 本金罚息交易Id = genBusiCode();
//                    业务类型									罚息交易
//                      分支序号		本金应收款标的dto.分支序号
//                      交易本金
//                      利息总额		画面.转让罚息*(金融产品交易及相关-本金转出对应投资目的.交易本金/画面.转入未支付本金 + 画面.转入未支付利息)
//                      全价总额
//                      交易方向		卖出
//                      随交易支付费用
//                      交易金额		画面.转让罚息*(金融产品交易及相关-本金转出对应投资目的.交易本金/画面.转入未支付本金 + 画面.转入未支付利息)
//                      未随交易支付费用
//                      总交易费用
//                      其他字段同本金转出
                    BaseTradeInfoDto 本金罚息TradeInfo =genDumpBaseTradeInfoDto(本金TradeInfo,本金罚息交易Id,null);
                    本金罚息TradeInfo.getTrdProductDeal().setBusiType(DictCodeList.业务类型_逾期交易.getValue());
                    本金罚息TradeInfo.getTrdProductDeal().setBranch(本金应收款标的.getBranch());
                    BigDecimal 罚息 =ArithmeticUtil.multiply(dto.getPenaltyInt(),ArithmeticUtil.divide(
                    本金罚息TradeInfo.getTrdProductDeal().getPrinAmt(),ArithmeticUtil.doAdd(dto.getPrinAmt(),dto.getIntAmt()),4));
                    if(ArithmeticUtil.subtract(dto.getPenaltyInt(),ArithmeticUtil.doAdd(已转让罚息,罚息)).compareTo(BigDecimal.ZERO)<0){
                        罚息 = ArithmeticUtil.subtract(dto.getPenaltyInt(),已转让罚息);
                    }
                    已转让罚息 = ArithmeticUtil.doAdd(已转让罚息, 罚息);
                    本金罚息TradeInfo.getTrdProductDeal().setPrinAmt(BigDecimal.ZERO);
                    本金罚息TradeInfo.getTrdProductDeal().setIntAmt(罚息);
                    本金罚息TradeInfo.getTrdProductDeal().setTradeAmt(罚息);
                    本金罚息TradeInfo.getTrdProductDeal().setFpriceAmt(BigDecimal.ZERO);
                    本金罚息TradeInfo.getTrdProductDeal().setPs(DictCodeList.交易方向_卖出.getValue());
                    本金罚息转出BaseTradeInfo.add(本金罚息TradeInfo);
                }
            }
        }
        List<BaseTradeInfoDto>利息罚息转出BaseTradeInfo = new ArrayList<>();
        if(dto.getTheoryPenaltyIntLx()!=null&&dto.getPenaltyInt()!=null&&BigDecimal.ZERO.compareTo(dto.getTheoryPenaltyIntLx())!=0&&BigDecimal.ZERO.compareTo(dto.getPenaltyInt())!=0) {
//            处理罚息金额 设计书理解为多少条本金交易 多少条本金罚息 交易 罚息按照本金所占总金额占比分配罚息金额
            if (CollectionUtils.isNotEmpty(利息转出BaseTradeInfo)) {
                for (BaseTradeInfoDto 利息TradeInfo : 利息转出BaseTradeInfo) {
                    String 利息罚息交易Id = genBusiCode();
//                业务类型									罚息交易
//                分支序号									利息应收款标的dto.分支序号
//                交易本金
//                利息总额									画面.转让罚息*(金融产品交易及相关-利息转出对应.交易本金/画面.转入未支付本金 + 画面.转入未支付利息)																														最后一笔倒减
//                全价总额
//                交易方向									卖出
//                随交易支付费用
//                交易金额									画面.转让罚息*(金融产品交易及相关-利息转出对应.交易本金/画面.转入未支付本金 + 画面.转入未支付利息)																														最后一笔倒减
//                未随交易支付费用
//                总交易费用
//                其他字段同本金转出
                    BaseTradeInfoDto 利息罚息TradeInfo = genDumpBaseTradeInfoDto(利息TradeInfo, 利息罚息交易Id, null);
                    利息罚息TradeInfo.getTrdProductDeal().setBusiType(DictCodeList.业务类型_逾期交易.getValue());
//                    利息罚息TradeInfo.getTrdProductDeal().setBranch(利息应收款标的.getBranch());
//
                    BigDecimal 罚息 = ArithmeticUtil.multiply(dto.getPenaltyInt(), ArithmeticUtil.divide(
                            利息罚息TradeInfo.getTrdProductDeal().getIntAmt(), ArithmeticUtil.doAdd(dto.getPrinAmt(), dto.getIntAmt()), 4));
                    if (利息转出BaseTradeInfo.indexOf(利息TradeInfo)==利息转出BaseTradeInfo.size()-1) {
                        罚息 = ArithmeticUtil.subtract(dto.getPenaltyInt(), 已转让罚息);
                    }
                    已转让罚息 = ArithmeticUtil.doAdd(已转让罚息, 罚息);
                    利息罚息TradeInfo.getTrdProductDeal().setPrinAmt(BigDecimal.ZERO);
                    利息罚息TradeInfo.getTrdProductDeal().setIntAmt(罚息);
                    利息罚息TradeInfo.getTrdProductDeal().setTradeAmt(罚息);
                    利息罚息TradeInfo.getTrdProductDeal().setFpriceAmt(BigDecimal.ZERO);
                    利息罚息TradeInfo.getTrdProductDeal().setPs(DictCodeList.交易方向_卖出.getValue());
                    利息罚息转出BaseTradeInfo.add(利息罚息TradeInfo);
                }
            }
        }
//        转入entity相关生成
//        对于设计理解 所有的交易与转出有关，所有的待入库交易考虑根据转出生成
        List<BaseTradeInfoDto>本金转入BaseTradeInfo = new ArrayList<>();
        List<BaseTradeInfoDto>利息转入BaseTradeInfo = new ArrayList<>();
        List<BaseTradeInfoDto>本金罚息转入BaseTradeInfo = new ArrayList<>();
        List<BaseTradeInfoDto>利息罚息转入BaseTradeInfo = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(本金转出BaseTradeInfo)){
            for(BaseTradeInfoDto temp : 本金转出BaseTradeInfo){
                BaseTradeInfoDto 转入Temp = genDumpBaseTradeInfoDto(temp,null,total转出.getCounterId());
                转入Temp.getTrdProductDeal().setPs(DictCodeList.交易方向_应收款项转入.getValue());
                转入Temp.getTrdProductDeal().setCounterId(dto.getActPortfolioId());
                本金转入BaseTradeInfo.add(转入Temp);
            }
        }
        if(CollectionUtils.isNotEmpty(利息转出BaseTradeInfo)){
            for(BaseTradeInfoDto temp : 利息转出BaseTradeInfo){
                BaseTradeInfoDto 转入Temp = genDumpBaseTradeInfoDto(temp,null,total转出.getCounterId());
                转入Temp.getTrdProductDeal().setPs(DictCodeList.交易方向_应收款项转入.getValue());
                转入Temp.getTrdProductDeal().setCounterId(dto.getActPortfolioId());
                利息转入BaseTradeInfo.add(转入Temp);
            }
        }
        if(CollectionUtils.isNotEmpty(本金罚息转出BaseTradeInfo)){
            for(BaseTradeInfoDto temp : 本金罚息转出BaseTradeInfo){
                BaseTradeInfoDto 转入Temp = genDumpBaseTradeInfoDto(temp,null,total转出.getCounterId());
                转入Temp.getTrdProductDeal().setPs(DictCodeList.交易方向_买入.getValue());
                转入Temp.getTrdProductDeal().setCounterId(dto.getActPortfolioId());
                本金罚息转入BaseTradeInfo.add(转入Temp);
            }
        }
        if(CollectionUtils.isNotEmpty(利息罚息转出BaseTradeInfo)){
            for(BaseTradeInfoDto temp : 利息罚息转出BaseTradeInfo){
                BaseTradeInfoDto 转入Temp = genDumpBaseTradeInfoDto(temp,null,total转出.getCounterId());
                转入Temp.getTrdProductDeal().setPs(DictCodeList.交易方向_买入.getValue());
                转入Temp.getTrdProductDeal().setCounterId(dto.getActPortfolioId());
                利息罚息转入BaseTradeInfo.add(转入Temp);
            }
        }
        List<BaseTradeInfoDto>交易list = new ArrayList<>();
        List<BaseTradeInfoDto>罚息list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(本金转出BaseTradeInfo)){
            交易list.addAll(本金转出BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(本金转入BaseTradeInfo)){
            交易list.addAll(本金转入BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(利息转入BaseTradeInfo)){
            交易list.addAll(利息转入BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(利息转出BaseTradeInfo)){
            交易list.addAll(利息转出BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(本金罚息转出BaseTradeInfo)){
            罚息list.addAll(本金罚息转出BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(利息罚息转出BaseTradeInfo)){
            罚息list.addAll(利息罚息转出BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(本金罚息转入BaseTradeInfo)){
            罚息list.addAll(本金罚息转入BaseTradeInfo);
        }
        if(CollectionUtils.isNotEmpty(利息罚息转入BaseTradeInfo)){
            罚息list.addAll(利息罚息转入BaseTradeInfo);
        }
        baseTrade.setBaseTradeInfoDtoList(交易list);
        baseTrade.setPenaltyIntBaseTradeInfoDtoList(罚息list);
        return result;
    }

    public StlPayOrders gen转入划款指令(StlPayOrders 转出stl,String counterId,String traderId) {
        StlPayOrders stl = new StlPayOrders();
        BeanUtil.copyPropertiesIgnoreNull(stl,转出stl);
        BussinessCodeParameter bussinessCodeParameter1 = new BussinessCodeParameter();
        bussinessCodeParameter1.setSeqNo("9998");
        // 生成的业务编号
        String orderId = restTemplate.postForObject(
                ServiceApplication.COMMON_SERVICE_NAME + "/core/selectRule", bussinessCodeParameter1,
                String.class);
        stl.setOrderId(StringUtil.isNotEmpty(orderId) ? orderId : IDGenerator.generate());

        stl.setPs(DictCodeListExt1.收支方向_支出.getValue().equals(
                stl.getPs()) ? DictCodeListExt1.收支方向_收入.getValue() : DictCodeListExt1.收支方向_支出.getValue());
        stl.setPortfolioId(counterId);
        stl.setTradeId(traderId);
        stl.setCounterAcctNo(转出stl.getMyAcctNo());
        stl.setMyAcctNo(转出stl.getCounterAcctNo());
        return  stl;
    }

    /**
     * 根据原BaseTradeInfoDto 生成新的BaseTradeInfoDto
     * @param orgBaseInfo 原信息
     * @param tradeId 新的交易编号
     * @param portfolioId 新的组合代码
     * @return 生成结果
     */
    public BaseTradeInfoDto  genDumpBaseTradeInfoDto(BaseTradeInfoDto orgBaseInfo,String tradeId,String portfolioId){
        BaseTradeInfoDto aimTradeInfo = new BaseTradeInfoDto();
        if(StringUtil.isEmpty(tradeId)){
            tradeId = genBusiCode();
        }
        if(orgBaseInfo.getTrdProductDeal() != null){
            TrdProductDeal aimDeal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(aimDeal,orgBaseInfo.getTrdProductDeal());
            aimTradeInfo.setTrdProductDeal(aimDeal);
            aimDeal.setTradeId(tradeId);
        }
        if(orgBaseInfo.getTrdProductDealAdd()!=null){
            TrdProductDealAdd aimDealAdd = new TrdProductDealAdd();
            BeanUtil.copyPropertiesIgnoreNull(aimDealAdd,orgBaseInfo.getTrdProductDealAdd());
            aimTradeInfo.setTrdProductDealAdd(aimDealAdd);
            aimDealAdd.setTradeId(tradeId);
            if(StringUtil.isNotEmpty(portfolioId)){
                String pPortfolioId = AllTradeCommonUtil.exchangePortfolio(portfolioId);
//                不想同时登入母组合代码
                aimDealAdd.setPortfolioId(StringUtil.equals(portfolioId,pPortfolioId)?portfolioId:pPortfolioId);
            }
        }
        if(orgBaseInfo.getPaTrdProductDeal()!=null){
            PaTrdProductDeal aimPaDeal = new PaTrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(aimPaDeal,orgBaseInfo.getPaTrdProductDeal());
            aimTradeInfo.setPaTrdProductDeal(aimPaDeal);
            aimPaDeal.setTradeId(tradeId);
        }
        if(CollectionUtils.isNotEmpty(orgBaseInfo.getPtlTrade())){
            List<PtlTrade>aimPtlTradeList = new ArrayList<>();
            for(PtlTrade item : orgBaseInfo.getPtlTrade()){
                PtlTrade ptlTrade = new PtlTrade();
                BeanUtil.copyPropertiesIgnoreNull(ptlTrade,item);
                ptlTrade.setBusiNo(tradeId);
                aimPtlTradeList.add(ptlTrade);
            }
            if(StringUtil.isNotEmpty(portfolioId)){
                aimPtlTradeList.get(0).setPortfolioId(portfolioId);
                String pPortfolioId = AllTradeCommonUtil.exchangePortfolio(portfolioId);
                if(!StringUtil.equals(pPortfolioId,portfolioId)){
                    if(aimPtlTradeList.size()>1){
                        aimPtlTradeList.get(1).setPortfolioId(pPortfolioId);
                    }
                    else{
//                        内部交易对手为子组合时 需要自动生成母组合的组合交易
                        PtlTrade ptlTrade = new PtlTrade();
                        BeanUtil.copyPropertiesIgnoreNull(ptlTrade,aimPtlTradeList.get(0));
                        ptlTrade.setPortfolioId(pPortfolioId);
                        aimPtlTradeList.add(ptlTrade);
                    }
                }
            }
            aimTradeInfo.setPtlTrade(aimPtlTradeList);
        }
        return aimTradeInfo;
    }
    public BaseTradeInfoDto 生成交易个体利息(TrdSecOverdueDuration003Dto dto, TrdFinProductDealTotal total转出, BigDecimal 利息总额, String ccy, String 投资目的, String 证券账户) {
        BaseTradeInfoDto ret = 生成交易个体本金(dto,total转出,利息总额,ccy,投资目的,证券账户);
//        交易本金 为空
        ret.getTrdProductDeal().setPrinAmt(BigDecimal.ZERO);
        ret.getTrdProductDeal().setIntAmt(利息总额);
        ret.getTrdProductDeal().setShareAmt(BigDecimal.ZERO);
        return  ret;
    }

    private BaseTradeInfoDto 生成交易个体本金(TrdSecOverdueDuration003Dto dto,TrdFinProductDealTotal total,BigDecimal tradeAmt,String 币种,String 投资目的,String 证券账户){
        BaseTradeInfoDto baseTradeInfoDto = new BaseTradeInfoDto();
        String 本金交易ID = genBusiCode();
        TrdProductDeal deal本金转出 = gen金融产品交易(dto,total,tradeAmt,币种,投资目的);
        deal本金转出.setTradeId(本金交易ID);
        deal本金转出.setIntAmt(null);
        TrdProductDealAdd addDeal本金转出 = gen金融产品交易附属(dto,本金交易ID);
        List<PtlTrade> ptlTrades本金转出 = gen组合交易(dto,本金交易ID,投资目的,证券账户);
        baseTradeInfoDto.setTrdProductDeal(deal本金转出);
        baseTradeInfoDto.setTrdProductDealAdd(addDeal本金转出);
        baseTradeInfoDto.setPtlTrade(ptlTrades本金转出);
        return baseTradeInfoDto;
    }
    private List<PtlTrade> gen组合交易(TrdSecOverdueDuration003Dto dto, String 交易ID,String 投资目的,String 证券账户) {
//组合代码		画面.组合代码
//业务编号		金融产品交易.交易编号
//业务表		金融产品交易
//业务明细代码		金融产品代码
//业务类型		数据字典缓存.共通业务类型.已到期资产转让
//投资目的		同金融产品交易设置的投资目的
//证券托管户代码		画面.证券账户/通道中 存在于证券账户表中时，则登录画面.证券账户/通道，否则空
//实际发生日		画面.交割日
        List<PtlTrade> ptlTrades = new ArrayList<>();
        PtlTrade ptlTrade = new PtlTrade();
        ptlTrade.setBusiId(dto.getFinprodId());
        ptlTrade.setBusiNo(交易ID);
        ptlTrade.setBusiTable(DictCodeList.业务表_金融产品表.getValue());
        ptlTrade.setBusiType(DictCodeListExt3.共通业务类型_已到期资产转让.getValue());
        ptlTrade.setSettleDate(dto.getTransferDate());
        ptlTrade.setPortfolioId(dto.getPortfolioId());
        ptlTrade.setInvAim(投资目的);
        ptlTrade.setWorkflowId(dto.getWorkflowId());
        ptlTrades.add(ptlTrade);
        if(StringUtil.isNotEmpty(dto.getActPortfolioId())&&!StringUtil.equals(dto.getPortfolioId(),dto.getActPortfolioId())){
            PtlTrade subPtlTrade = new PtlTrade();
            BeanUtil.copyPropertiesIgnoreNull(subPtlTrade,ptlTrade);
            subPtlTrade.setPortfolioId(dto.getActPortfolioId());
            ptlTrades.add(subPtlTrade);
        }
        return ptlTrades;
    }

    /**
     * 生成金融产品交易附属
     * @param dto
     * @param tradeId
     * @return
     */
    private TrdProductDealAdd gen金融产品交易附属(TrdSecOverdueDuration003Dto dto, String tradeId) {
        TrdProductDealAdd addDeal = new TrdProductDealAdd();
        addDeal.setTradeId(tradeId);
        addDeal.setPortfolioId(dto.getPortfolioId());
        addDeal.setGenType(DictCodeListExt1.生成方式_画面录入.getValue());
        addDeal.setWorkflowId(dto.getWorkflowId());
        return addDeal;
    }

    /**
     * 生成金融产品交易dto
     * @param dto 画面dto
     * @param total 汇总交易
     * @param 转让未支付本金 转让未支付本金
     * @param 币种 币种
     * @param 投资目的 投资目的
     * @return 生成结果
     */
    private TrdProductDeal gen金融产品交易(TrdSecOverdueDuration003Dto dto,TrdFinProductDealTotal total, BigDecimal 转让未支付本金, String 币种, String 投资目的){
//        交易编号									自动生成
//        业务类型									同各类资产的买卖交易逻辑
//        金融产品代码									金融产品代码
//        金融产品类型（估值核算）									同各类资产的买卖交易逻辑
//        金融产品类型（投管分类）									根据传入字段分类8设置，数据字典.金融产品类型(投管用).债券/存款/计划类/项目类资产
//        分支序号									本金应收款标的dto.分支序号
//        交易日									转让日期
//        交割日									转让日期
//        交易币种									本金应收款标的dto.币种
//        交易份额									分类8=债券时，参考债券投资目的及交易生成说明处设置，其他为画面.转让未支付本金
//        交易本金									分类8=债券时，参考债券投资目的及交易生成说明处设置，其他为画面.转让未支付本金
//        全价总额									分类8=债券时，参考债券投资目的及交易生成说明处设置，其他为画面.转让未支付本金
//        交易金额									分类8=债券时，参考债券投资目的及交易生成说明处设置，其他为画面.转让未支付本金
//        交易方向									数据字典.交易方向.应收款项转出
//        投资目的									分类8=债券时，参考债券投资目的及交易生成说明处设置，其他空
//        交易状态									数据字典缓存.交易状态.初始
//                是否已撤销
//        是否结清									否
//        汇总交易编号									金融产品交易汇总.汇总交易编号
//        通道代码									画面.证券账户/通道中 存在于证券账户表中时，则登录空，否则 登录画面.证券账户/通道
//        交易对手									画面.交易对手
//        是否外部交易									否
//        交易场所									数据字典缓存.交易状态.初始
//        证券管理户代码									画面.证券账户/通道中 存在于证券账户表中时，则登录画面.证券账户/通道，否则空
        TrdProductDeal deal = new TrdProductDeal();
        BeanUtil.copyPropertiesIgnoreNull(deal,total);
        deal.setCcy(币种);
        deal.setMdate(dto.getMdate());
        deal.setShareAmt(转让未支付本金);
        deal.setPrinAmt(转让未支付本金);
        deal.setFpriceAmt(转让未支付本金);
        deal.setTradeAmt(转让未支付本金);
        deal.setPs(DictCodeList.交易方向_应收款项转出.getValue());
        deal.setIsClean(DictCodeList.是否_否.getValue());
        deal.setInvAim(投资目的);
        deal.setBusiType(gen分类(dto.getType8(),DictCode.共通业务类型));
        return deal;
    }
    /**
     * 汇总扩展附属
     * @param dto 业务dto
     * @return 返回值
     */
    public TrdDealTotalAdd gen汇总扩展附属(TrdSecOverdueDuration003Dto dto) {
//        汇总交易编号									金融产品交易汇总.汇总交易编号
//        罚息金额									画面.转让罚息
        TrdDealTotalAdd addTotal = new TrdDealTotalAdd();
        addTotal.setTotleTradeId(dto.getTotleTradeId());
        addTotal.setPenaltyIntamt(dto.getPenaltyInt());
        addTotal.setWorkflowId(dto.getWorkflowId());
        return addTotal;
    }

    /**
     * 组装汇总扩展交易信息
     * @param dto 页面数据
     * @return 组装结果
     */
    public PaTrdFinProductDealTotal gen汇总扩展交易(TrdSecOverdueDuration003Dto dto) {
//        汇总交易编号									金融产品交易汇总.汇总交易编号
//        理论应计利息									画面.未支付利息
//        理论还本金额									画面.未支付本金
//        理论罚息金额									画面.罚息
//        平安业务类型									数据字典.投管业务类型.已到期资产转让
        PaTrdFinProductDealTotal paTotal = new PaTrdFinProductDealTotal();
        paTotal.setTotleTradeId(dto.getTotleTradeId());
        paTotal.setTheoryInt(dto.getTheoryInt());
        paTotal.setTheoryPrin(dto.getTheoryPrin());
        paTotal.setTheoryPenaltyInt(dto.getTheoryPenaltyInt());
        paTotal.setPaBussType(DictCodeListExt3.平安业务类型_已到期资产转让.getValue());
        paTotal.setWorkflowId(dto.getWorkflowId());
        return  paTotal;
    }
    public String gen分类(String type8,DictCode dictCode){
        String ret = null ;
//        根据传入字段分类8设置，数据字典.金融产品类型(投管用).债券/存款/计划类/项目类资产
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(),type8)) {
            if(DictCode.共通业务类型.equals(dictCode)){
                ret = DictCodeList.共通业务类型_债券类资产交易.getValue();
            }
            else if(DictCode.金融产品类型_投管用.equals(dictCode)){
                ret = DictCodeListExt1.金融产品类型_投管用_债券.getValue();
            }
        }
        else if (StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(),type8)) {
            if(DictCode.共通业务类型.equals(dictCode)){
                ret = DictCodeList.共通业务类型_存款类资产交易.getValue();
            }
            else if(DictCode.金融产品类型_投管用.equals(dictCode)){
                ret = DictCodeListExt1.金融产品类型_投管用_存款.getValue();
            }
        }
        else if (StringUtil.equals(DictCodeList.平安资产类型_计划类资产_利率型.getValue(),type8)) {
            if(DictCode.共通业务类型.equals(dictCode)){
                ret = DictCodeList.共通业务类型_计划类资产_利率型_交易.getValue();
            }
            else if(DictCode.金融产品类型_投管用.equals(dictCode)){
                ret = DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue();
            }
        }
        else if (StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(),type8)) {
            if(DictCode.共通业务类型.equals(dictCode)){
                ret = DictCodeList.共通业务类型_项目类资产交易.getValue();
            }
            else if(DictCode.金融产品类型_投管用.equals(dictCode)){
                ret = DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue();
            }
        }
        return ret;
    }

    /**
     * 组装汇总交易信息
     * @param dto 页面数据
     * @param 应收款标的dto
     * @return 组装结果
     */
    public TrdFinProductDealTotal gen汇总交易信息(TrdSecOverdueDuration003Dto dto, PtlReceInfo 应收款标的dto) {
        TrdFinProductDealTotal total = new TrdFinProductDealTotal();
//        汇总交易编号									自动生成
        total.setTotleTradeId(dto.getTotleTradeId());
//        业务类型									同各类资产的买卖交易
        total.setBusiType(gen分类(dto.getType8(),DictCode.共通业务类型));
//        金融产品代码									金融产品代码
        total.setFinprodId(dto.getFinprodId());
//        金融产品类型（投管分类）									根据传入字段分类8设置，数据字典.金融产品类型(投管用).债券/存款/计划类/项目类资产
        total.setFinprodType2(gen分类(dto.getType8(),DictCode.金融产品类型_投管用));
//        分支序号									应收款标的dto.分支序号
        total.setBranch(0L);
//        交易日									画面.转让日期
        total.setTradeDate(dto.getTransferDate());
//        交割日									画面.转让日期
        total.setSettleDate(dto.getTransferDate());
//        交易币种									应收款标的dto.币种
        total.setCcy(应收款标的dto.getCcy());
//        交易本金									画面.转让未支付本金
        total.setPrinAmt(dto.getPrinAmt());
//        利息总额									画面.转让未支付利息
        total.setIntAmt(dto.getIntAmt());
//        全价总额									画面.实收本息总额
        total.setFpriceAmt(dto.getTradeAmt());
//        随交易支付费用									0
        total.setFeeAmtPay(BigDecimal.ZERO);
//        交易金额									画面.实收本息总额
        total.setTradeAmt(dto.getTradeAmt());
//        未随交易支付费用									0
        total.setFeeAmtUnpay(BigDecimal.ZERO);
//        总交易费用
        total.setFeeAmt(BigDecimal.ZERO);
//        交易方向									数据字典.交易方向.卖出
        total.setPs(DictCodeList.交易方向_卖出.getValue());
//        交易状态									数据字典缓存.交易状态.初始
        total.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
//        是否已撤销
        total.setIsCancel(DictCodeList.是否_否.getValue());
//        是否结清									否
        total.setIsClean(DictCodeList.是否_否.getValue());
//        证券管理户代码									画面.证券账户/通道中 存在于证券账户表中时，则登录画面.证券账户/通道，否则空
        if(isSecManage(dto.getSecAcctId())){
            total.setSecManageAcctId(dto.getSecAcctId());
        }
        else if(!StringUtil.equals(dto.getSecAcctId(),"999999")){
//        通道代码									画面.证券账户/通道中 存在于证券账户表中时，则登录空，否则 登录画面.证券账户/通道
            total.setChlId(dto.getSecAcctId());
        }
//                前置交易编号
//        交易对手									画面.交易对手
        total.setCounterId(dto.getTrdInvestPersonInfo().getCounterId());
//        是否外部交易									否
        total.setIsOutTrade(DictCodeList.是否_否.getValue());
//                本方交易员
        total.setTrader(dto.getTrdInvestPersonInfo().getTrader());
//        对手方交易员
        total.setCounterTrader(dto.getTrdInvestPersonInfo().getCounterTrader());
//                对手方联系方式
        total.setCounterContact(dto.getTrdInvestPersonInfo().getContactInfo());
//        投资组合代码									画面.组合代码
        total.setPortfolioId(dto.getActPortfolioId());
//        投管业务类型									数据字典.投管业务类型.已到期资产转让
        total.setTgBusiType(DictCodeListExt3.平安业务类型_已到期资产转让.getValue());
//        是否逾期交易									是
        total.setIsOverdueDeal(DictCodeList.是否_是.getValue());
        total.setWorkflowId(dto.getWorkflowId());
        total.setRemark(dto.getRemark());
        return total;
    }

    /**
     * 是否是证券托管户
     * @param secAcctId 代码
     * @return 匹配结果
     */
    public boolean isSecManage(String secAcctId) {
        Map<String,Object> param = new HashMap<>();
        param.put("secAcctId",secAcctId);
        int count = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/isSecManage",
                param, Integer.class);
        return count>0;
    }

    @Override
    public void doComplete(TrdSecOverdueDuration003Dto entity, OperationType operationType) {
        entity.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
        if (OperationType.REVOKE == operationType) {
            execute交易共通(entity, DictCodeListExt1.交易流程状态_撤销);
            entity.setOperateType(BussinessConstant.REVOKE);
        } else {
            execute交易共通(entity, DictCodeListExt1.交易流程状态_有效);
        }
        entity.setIssuePartyId(CacheUtil.getSysParam("issue_party_id"));
        entity.setCounterId(entity.getTrdInvestPersonInfo().getCounterId());
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/doCompleteZr", entity,
                String.class);
    }

    public boolean checkCounterFlowDeal(Map<String, Object> checkMap) {
        int count = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecOverdueDuration/checkCounterFlowDeal", checkMap,
                        Integer.class);
        return count>0;
    }

    public static void setWorkflowId(BaseTotalTradeInfoDto baseTotalTradeInfoDto, String workflowId) {
        if (baseTotalTradeInfoDto != null
                && StringUtil.isEmpty(baseTotalTradeInfoDto.getTrdFinProductDealTotal().getWorkflowId())) {
            baseTotalTradeInfoDto.getTrdFinProductDealTotal().setWorkflowId(workflowId);
            if(StringUtil.isNotEmpty(baseTotalTradeInfoDto.getTrdDealTotalAdd())){
                baseTotalTradeInfoDto.getTrdDealTotalAdd().setWorkflowId(workflowId);
            }
            if(baseTotalTradeInfoDto.getPaTrdFinProductDealTotal()!=null){
                baseTotalTradeInfoDto.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);
            }
            if(baseTotalTradeInfoDto.getStlPayOrders()!=null){
                baseTotalTradeInfoDto.getStlPayOrders().setWorkflowId(workflowId);
            }
            baseTotalTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto -> {
                setWorkflowIdToBaseTradeInfoDto(baseTradeInfoDto,workflowId);
                if(baseTradeInfoDto.getBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getBaseTradeInfoDtoList().size()>0){
                    baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                        setWorkflowIdToBaseTradeInfoDto(subBaseTradeInfoDto,workflowId);
                    });
                }
                if(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().size()>0){
                    baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                        setWorkflowIdToBaseTradeInfoDto(subBaseTradeInfoDto,workflowId);
                    });
                }
                if(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList()!= null && baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0){
                    baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(subBaseTradeInfoDto->{
                        setWorkflowIdToBaseTradeInfoDto(subBaseTradeInfoDto,workflowId);
                    });
                }
            });
        }
    }

    /**
     *  给dto设置工作流代码
     * @param baseTradeInfoDto dto
     * @param workflowId 工作流代码
     */
    private static void setWorkflowIdToBaseTradeInfoDto(BaseTradeInfoDto baseTradeInfoDto ,String workflowId){
        if(baseTradeInfoDto.getTrdFinProductDealTotal()!=null) {
            baseTradeInfoDto.getTrdFinProductDealTotal().setWorkflowId(workflowId);
        }
        if(baseTradeInfoDto.getPaTrdFinProductDealTotal()!=null) {
            baseTradeInfoDto.getPaTrdFinProductDealTotal().setWorkflowId(workflowId);
        }
        if(baseTradeInfoDto.getStlPayOrders()!=null) {
            baseTradeInfoDto.getStlPayOrders().setWorkflowId(workflowId);
        }

        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            baseTradeInfoDto.getTrdProductDeal().setWorkflowId(workflowId);
        }
        if (baseTradeInfoDto.getTrdProductDealAdd() != null) {
            baseTradeInfoDto.getTrdProductDealAdd().setWorkflowId(workflowId);
        }
        if (baseTradeInfoDto.getPaTrdProductDeal() != null) {
            baseTradeInfoDto.getPaTrdProductDeal().setWorkflowId(workflowId);
        }
        if (baseTradeInfoDto.getBaseTrade() != null) {
            if (baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                baseTradeInfoDto.getBaseTrade().getTrdProductDeal().setWorkflowId(workflowId);
            }
            if (baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd() != null) {
                baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd().setWorkflowId(workflowId);
            }
            if (baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal() != null) {
                baseTradeInfoDto.getBaseTrade().getPaTrdProductDeal().setWorkflowId(workflowId);
            }
        }
    }
}
