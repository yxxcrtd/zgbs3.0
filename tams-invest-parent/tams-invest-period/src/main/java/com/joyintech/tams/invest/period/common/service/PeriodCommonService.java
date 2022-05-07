package com.joyintech.tams.invest.period.common.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.PaTrdProductDealMapper;
import com.joyintech.tams.common.base.dao.PtlTradeMapper;
import com.joyintech.tams.common.base.dao.TrdProductDealAddMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.settle.dto.BussSettleOrderParameterDto;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.dto.PtlRecePositionFlowDto;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.component.base.trade.BaseTradeProductDealService;
import com.joyintech.tams.invest.common.component.business.common.BaseAssetCashPositionCommService;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioCashPositionsFlowInputDto;
import com.joyintech.tams.invest.common.component.business.common.dto.PortfolioPositionsFlowInputDto;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.risk.service.RiskCreditListCommonValidatorService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dao.CreditLinesDealsMapper;
import com.joyintech.tams.invest.common.trade.dao.PreTrdCommonMapper;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.trdbondduration.dto.TrdBondDuration002ProcessDto;
import com.joyintech.tams.invest.period.trdbondduration.dto.TrdBondDuration003ProcessDto;
import com.joyintech.tams.invest.period.trdbuyoutduration.dto.TrdRepoBuyoutDuration002Dto;
import com.joyintech.tams.invest.period.trddepoduration.dto.TrdDepoDuration001Dto;
import com.joyintech.tams.invest.period.trdirsduration.dto.TrdIRSDuration002ProcessDto;
import com.joyintech.tams.invest.period.trdlendduration.dto.TrdLendDuration001Dto;
import com.joyintech.tams.invest.period.trdoptionduration.dto.OptionDurationDetailDto;
import com.joyintech.tams.invest.period.trdplanintduration.dto.TrdPlanInterestDurationProcessDto;
import com.joyintech.tams.invest.period.trdprojduration.dto.TrdProjectDurationProcessDto;
import com.joyintech.tams.invest.period.trdrepoduration.dto.TrdRepoDuration002Dto;
import com.joyintech.tams.invest.period.trdseclendduration.dto.TrdSecLendDuration002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @program: tams-invest-parent
 * @description: 存续期共通Service
 * @author: 强成西
 * @create: 2018-10-30 15:42
 **/
@Service
public class PeriodCommonService extends BaseService {
	/**
	 *
	 */
	@Autowired(required = false)
	private BussTrdTransferOrderService bussTrdTransferOrderService;
    @Autowired
	private BaseTradeProductDealService baseTradeProductDealService;
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;
    @Autowired
    private PaTrdProductDealMapper paTrdProductDealMapper;

    @Autowired
    private PreTrdCommonMapper preTrdCommonMapper;
    @Autowired
    private CreditLinesDealsMapper creditLinesDealsMapper;
    @Autowired
    private FinProductAddMapper finProductAddMapper;
    @Autowired
    private PtlTradeMapper ptlTradeMapper;
    @Autowired
    private BasePtlRecePositionService basePtlRecePositionService;

    @Autowired
    private RiskCreditListCommonValidatorService riskCreditListCommonValidatorService;

	public List<SelectOptionDto> queryAcctList(Map<String, Object> paramMap) {
		if(bussTrdTransferOrderService != null) {
			return bussTrdTransferOrderService.queryAcctList(paramMap);
		}
		return null;
	}
    public String checkAcctNo(List<BussTrdTransferOrderDto> bussTrdTransferOrder) {
        for (BussTrdTransferOrderDto bussTrdTransferOrderDto : bussTrdTransferOrder) {
            if (DictCodeListExt1.收支方向_收入.getValue().equals(bussTrdTransferOrderDto.getPs()) && StringUtil.isEmpty(bussTrdTransferOrderDto.getRecAcctNo())) {
                return this.getMessage("PERIOD_E_0003");
            } else if (DictCodeListExt1.收支方向_支出.getValue().equals(bussTrdTransferOrderDto.getPs()) && StringUtil.isEmpty(bussTrdTransferOrderDto.getPayAcctNo())){
                return this.getMessage("PERIOD_E_0004");
            }
        }
        return null;
    }

    /**
     * 根据工作流代码获取交易信息 <功能详细描述>
     *
     * @param workflowId
     * @return
     * @see
     * @since 1.0
     */
    public List<WflTrdProductDeal> findDealByWorkflowId(String workflowId) {
        return restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdLendDuration/findDealByWorkflowId",
            workflowId, WflTrdProductDeal.class);
    }

    public int selectMatchedData(String bizCode) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/selectMatchedData", bizCode,
            Integer.class);
    }

    /**
     * 划款信息
     *
     * @param bizCode 业务编号
     * @return
     */
    public List<BussTrdTransferOrderDto> selectPayOrders(String bizCode) {
        return restTemplate.postForList(ServiceApplication.SETTLE_SERVICE_NAME + "/BussSettleOrderComm/selectOrdersByBizCode",
            bizCode, BussTrdTransferOrderDto.class);
    }

    public static String change现金流类型To交易方向(String cashType) {
        return AutoPeriodCommonService.change现金流类型To交易方向(cashType);
    }

    public static Long changePsToBranch(String mDate, String ps) {
        return AutoPeriodCommonService.changePsToBranch(mDate, ps);
    }

    /**
     * 业务共通组件.资产头寸现金持仓共通组件
     */
    @Autowired
    private BaseAssetCashPositionCommService assetPositionsCashCommServiceImpl;

    public void gen可用持仓(String portfolioId, List<StlPayOrdersDto> stlPayOrdersList, List<PtlSecPositionFlowDto> ptlSecPositions,
                        List<PtlCashPositionFlowDto> ptlCashPositions, TrdProductDeal trdProductDeal) {
        PortfolioPositionsFlowInputDto inputDto = new PortfolioPositionsFlowInputDto();
        inputDto.setPositionType(DictCodeList.持仓类型_可用.getValue());
        inputDto.setTrdProductDeal(trdProductDeal);
        inputDto.setPortfolioId(portfolioId);

        PortfolioCashPositionsFlowInputDto cashInputDto = new PortfolioCashPositionsFlowInputDto();
        cashInputDto.setPositionType(DictCodeList.持仓类型_可用.getValue());
        cashInputDto.setTrdProductDeal(trdProductDeal);
        cashInputDto.setPortfolioId(portfolioId);
        cashInputDto.setStlPayOrders(stlPayOrdersList);
        List<PtlSecPositionFlowDto> ptlSecPositionsGet = assetPositionsCashCommServiceImpl.positionDeal(inputDto);
        List<PtlCashPositionFlowDto> ptlCashPositionsGet = assetPositionsCashCommServiceImpl.cashDeal(cashInputDto);
        if (ptlSecPositionsGet != null) {
            ptlSecPositions.addAll(ptlSecPositionsGet);
        }
        if (ptlCashPositionsGet != null) {
            ptlCashPositions.addAll(ptlCashPositionsGet);
        }
    }

    public void periodCommonRevokeCheck(String statusValid, String bizCode, List<ValidMessage> errorList) {
        if (!DictCodeListExt1.画面数据状态_有效.getValue().equals(statusValid)) {
            errorList.add(new ValidMessage("statusValid", this.getMessage("FIN_ASSET_E_0006")));
        }
        else {
            int count = selectMatchedData(bizCode);
            if (count > 0) {
                errorList.add(new ValidMessage("", this.getMessage("FIN_ASSET_E_0005")));
            }
            return;
        }
    }

    public static void setEntityBussOrderToModel(Model model, List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos) {
        if (bussTrdTransferOrderDtos != null && !bussTrdTransferOrderDtos.isEmpty()) {
            bussTrdTransferOrderDtos.forEach(bussTrdTransferOrderDto -> {
                SelectOptionDto optionDto = new SelectOptionDto();
                if (StringUtil.isNotEmpty(bussTrdTransferOrderDto.getPayAcctNo())) {
                    optionDto.setId(bussTrdTransferOrderDto.getPayAcctNo());
                    optionDto.setText(bussTrdTransferOrderDto.getPayAcctName());
                    bussTrdTransferOrderDto.setPayAcctNoList(Arrays.asList(optionDto));
                }
                if (StringUtil.isNotEmpty(bussTrdTransferOrderDto.getRecAcctNo())) {
                    optionDto = new SelectOptionDto();
                    optionDto.setId(bussTrdTransferOrderDto.getRecAcctNo());
                    optionDto.setText(bussTrdTransferOrderDto.getRecAcctName());
                    bussTrdTransferOrderDto.setRecAcctNoList(Arrays.asList(optionDto));
                }
            });
            model.addAttribute("entity_bussTrdTransferOrderDto", bussTrdTransferOrderDtos);
        }
    }

    public boolean checkIfMaxDate(Map<String, Object> params) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/checkIfMaxDate", params,
                Boolean.class);
    }

    public List<BaseTradeProductDealDto> findEffectiveDeal(String busiCode,DictCodeListExt1 交易流程状态) {
        return findEffectiveDeal(busiCode,交易流程状态,false);
    }
/*
查询应收款交易数据
* */
    public List<BaseTradeProductDealDto> findReceEffectiveDeal(String busiCode,DictCodeListExt1 交易流程状态) {
        return findReceEffectiveDeal(busiCode,交易流程状态,false);
    }
    public List<BaseTradeProductDealDto> findEffectiveDealForFund(String busiCode,DictCodeListExt1 交易流程状态) {
        Map<String,Object>map = new HashMap<>();
        map.put("statusValid", 交易流程状态.getValue());
        map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("bizCode", busiCode);
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        List<BaseTradeProductDealDto> result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
        return result;
    }
    public List<BaseTradeProductDealDto> findEffectiveDeal(String busiCode,DictCodeListExt1 交易流程状态,boolean isTqzq) {
        Map<String,Object>map = new HashMap<>();
        map.put("statusValid", 交易流程状态.getValue());
        map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("bizCode", busiCode);
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        if(isTqzq){
            map.put("isTqzq", BussinessDictCodeList.是否_是.getValue());
        }
        else{
            map.put("isTqzq", "");
        }
        List<BaseTradeProductDealDto> result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration/findEffectiveDeal",
                map, BaseTradeProductDealDto.class);
        if(CollectionUtils.isEmpty(result)) {
            result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
        }
        return result;
    }

    public List<BaseTradeProductDealDto> findReceEffectiveDeal(String busiCode,DictCodeListExt1 交易流程状态,boolean isTqzq) {
        Map<String,Object>map = new HashMap<>();
        map.put("statusValid", 交易流程状态.getValue());
        map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("bizCode", busiCode);
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        if(isTqzq){
            map.put("isTqzq", BussinessDictCodeList.是否_是.getValue());
        }
        else{
            map.put("isTqzq", "");
        }
        List<BaseTradeProductDealDto> result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration/findEffectiveDeal",
                map, BaseTradeProductDealDto.class);
        if(CollectionUtils.isEmpty(result)) {
            result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
        }
        List<TrdProductDeal>  trdProductDealLists=new ArrayList<>();//交易编号
        List<BaseTradeProductDealDto> list=new ArrayList<>();
        if(!CollectionUtils.isEmpty(result)) {
            for (BaseTradeProductDealDto baseDto : result) {
                List<TrdProductDeal>  subtrdProductDealLists=new ArrayList<>();//交易编号
                String totletradeid = baseDto.getTrdProductDeal().getTradeId();//每个子组合的汇总交易编号
                subtrdProductDealLists =
                        baseTradeProductDealService.selectTrdProductDealsByTotalTradeId(totletradeid);//汇总交易编号
                trdProductDealLists.addAll(subtrdProductDealLists);
                }
            }
                //查询应收款流水交易信息
                if(trdProductDealLists!=null && trdProductDealLists.size()>0){
                    for(TrdProductDeal deals:trdProductDealLists) {
                        BaseTradeProductDealDto reVoke=new BaseTradeProductDealDto();
                        reVoke.setRecType(DictCodeListExt1.应收款类型_还本未收利息.getValue());
                        deals.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                        TrdProductDealAdd add = trdProductDealAddMapper.selectByPrimaryKey(deals.getTradeId());
                        if(add!=null){
                            reVoke.setTrdProductDealAdd(add);
                        }
                        reVoke.setTrdProductDeal(deals);
                        PaTrdProductDeal pTPDeal=paTrdProductDealMapper.selectByPrimaryKey(deals.getTradeId());
                        if(pTPDeal!=null){
                            pTPDeal.setTradeId(deals.getTradeId()+"R");
                            reVoke.setPaTrdProductDeal(pTPDeal);
                        }
                        PtlTradeKey key = new PtlTradeKey();
                        key.setBusiNo(deals==null?null:deals.getTradeId());
                        key.setPortfolioId(add==null?null:add.getPortfolioId());
                        PtlTrade ptlTrade=ptlTradeMapper.selectByPrimaryKey(key);
                        List<PtlTrade> ptlTradeList =new ArrayList<>();
                        if(ptlTrade!=null){
                            ptlTrade.setBusiNo(deals.getTradeId()+"R");
                            ptlTradeMapper.insertSelective(ptlTrade);
                        }
                        reVoke.setPayDate(getPayDate(deals.getFinprodId(),deals.getBranch()));
                        list.add(reVoke);
                    }
                }

        return list;
    }

    /**
     *
     * 获取最新的净值
     * <功能详细描述>
     * @param finprodId 金融产品代码
     * @param valDate 估值日期
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getLatestValuation(String finprodId,String valDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("finprodId", finprodId);
        params.put("valDate", valDate);
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/getLatestValuation", params,
            BigDecimal.class);
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
	/**
	 * 根据交易编号删除流程表数据
	 * <功能详细描述>
	 * @param tradeId 交易编号
	 * @see
	 * @since 1.0
	 */
	public void deleteWflByTradeId(String tradeId) {
		restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/deleteWflByTradeId", tradeId,
	            Integer.class);
	}
    /**
     * 在自动生成的红利再投数据中没有份额（防止在代办中直接批量审批不进入画面处理 丢失份额字段）
     * @param infoList 待处理list
     * @param sumRegistShare 总数量
     * @param qty 总份额
     */
    public void autoDealQtyForSubmit(List<FinAssetIntInfo> infoList, BigDecimal sumRegistShare, BigDecimal qty){
//        剩余份额
        BigDecimal surplus =  ArithmeticUtil.round(new BigDecimal(qty.toPlainString()),2,RoundingMode.HALF_UP);
//        计算份额
        for(int i = 0; i<infoList.size();i++){
            if(i == (infoList.size()-1)){
                infoList.get(i).setQty(surplus);
            }
            else{
                BigDecimal prop = ArithmeticUtil.divide(infoList.get(i).getRegistShare(),sumRegistShare,12);
//            计算当前需要的份额
                BigDecimal thisQty = ArithmeticUtil.round(ArithmeticUtil.multiply(prop,qty),2,RoundingMode.HALF_UP);
                infoList.get(i).setQty(thisQty);
                surplus = ArithmeticUtil.subtract(surplus,thisQty);
            }
        };
    }

    /**
     *  统计是否存在流程中的同类交易
     *   资产代码|finprodId，计息截止日|mdate
     * @param map 待查询参数
     * @return 统计结果
     */
    public List<String> getFlowDeal(Map<String,Object> map){
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdBondDuration/getFlowDeal", map,
                ArrayList.class);
    }

    /**
     * 子组合信息处理划款指令（撤销用）
     * @param map 参数交易编号
     * @return 组装结果
     */
    public  List<BaseTradeProductDealDto> getChiledTradeProductDealInfo(Map<String, Object> map) {
//        是否为汇总交易 交易编号为汇总交易的汇总交易的汇总交易代码
        boolean isTotal = true;
        //        处理提前支取没有flag
        if(!map.containsKey("isTqzq")){
            map.put("isTqzq","");
        }
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        List<BaseTradeProductDealDto> tol = null;
        if (StringUtil.isEmpty((String) map.get("bTrade"))) {// 基金时，除权交易
            tol = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
        }
        else {// 基金时，除权交易撤销时，查询分红交易
            String bizCode = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findBTradeId",
                    map, String.class);
            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.FUND_DEAL_TYPE),"02")){
                bizCode = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findBTradeId",
                        map, String.class);
            }
            map.put("bizCode", bizCode);
        }
        if(CollectionUtils.isEmpty(tol)) {
            isTotal = false;
            tol = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
//            if(StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.FUND_DEAL_TYPE),"02")){
//                tol = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDurationNew/findEffectiveDeal",
//                        map, BaseTradeProductDealDto.class);
//            }
        }
        List<BaseTradeProductDealDto> result = new ArrayList<>();
        if (tol != null){
        for(BaseTradeProductDealDto baseTradeProductDealDto:tol){
//            更新汇总交易代码
            if(isTotal){
                map.put("bizCode",baseTradeProductDealDto.getTrdProductDeal().getTradeId());
            }
            map.put("portfolioId",baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
            List<BaseTradeProductDealDto> temp = null;
            if (map.get("isFund") == null || !(Boolean) map.get("isFund")) {
                temp = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration/findChildTradeInfo", map,
                        BaseTradeProductDealDto.class);
//                防止查询完毕的交易信息在下面重新的再次查询
                if(baseTradeProductDealDto != null &&baseTradeProductDealDto.getStlPayOrdersList() != null
                        && baseTradeProductDealDto.getStlPayOrdersList().size() >0 &&
                        CollectionUtils.isNotEmpty(temp)&&
                        (temp.get(0).getStlPayOrdersList() == null || temp.get(0).getStlPayOrdersList().isEmpty())){
                    gen子组合划款指令_撤销用(temp,baseTradeProductDealDto);
                }
            }
            if(CollectionUtils.isEmpty(temp) || temp.get(0).getStlPayOrdersList() == null || temp.get(0).getStlPayOrdersList().isEmpty()) {
                temp = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findEffectiveDeal",map, BaseTradeProductDealDto.class);
                if(baseTradeProductDealDto.getStlPayOrdersList() != null && baseTradeProductDealDto.getStlPayOrdersList().size()>0){
                    gen子组合划款指令_撤销用(temp,baseTradeProductDealDto);
                }
                else{
                    gen划款指令_BY_交易(baseTradeProductDealDto,"REVOKE");
                    if(baseTradeProductDealDto.getStlPayOrdersList()!= null && baseTradeProductDealDto.getStlPayOrdersList().size()>0 ){
                        gen子组合划款指令_撤销用(temp,baseTradeProductDealDto);
                    }
                }
            }
            else{
                gen子组合划款指令_撤销用(temp,baseTradeProductDealDto);
            }
            result.addAll(temp);
            }
        }
        return result;
    }

    public void gen划款指令_BY_交易(BaseTradeProductDealDto baseTradeProductDealDto,String mode) {
        BussSettleOrderParameterDto param = new BussSettleOrderParameterDto();
        param.setIsTotalTrade(DictCodeList.是否_否.getValue());
        param.setPortfolioId(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId());
        param.setDeal(baseTradeProductDealDto.getTrdProductDeal());
        BussTrdTransferOrderDto ret = bussTrdTransferOrderService.generateOrder(param);
        List<BussTrdTransferOrderDto> bussTrdTransferOrderDtos = new ArrayList<>();
        if (ret != null) {
            bussTrdTransferOrderDtos.add(ret);
            if(StringUtil.equals(OperationType.REVOKE.toString(), mode)) {
                ret.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
            }
            if (StringUtil.equals(ret.getPs(), DictCodeListExt1.收支方向_支出.getValue())) {
                ret.setMyAcctNo(ret.getPayAcctNo());
                ret.setCounterAcctNo(ret.getRecAcctNo());
            }
            else {
                ret.setMyAcctNo(ret.getRecAcctNo());
                ret.setCounterAcctNo(ret.getPayAcctNo());
            }
            StlPayOrdersDto stl = gen划款指令流程(bussTrdTransferOrderDtos, baseTradeProductDealDto.getTrdProductDealAdd(), null);
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[]{stl}));
        }
    }

    public void gen子组合划款指令_撤销用(List<BaseTradeProductDealDto> dealList,BaseTradeProductDealDto dealDto){
        StlPayOrdersDto stlPayOrdersDto = dealDto.getStlPayOrdersList().get(0);
        TrdProductDeal dealTotle = dealDto.getTrdProductDeal();
        FinProductTradeDto tradeDto = new FinProductTradeDto();
        tradeDto.setNeedSum(true);
        BigDecimal sum = BigDecimal.ZERO;
        for(BaseTradeProductDealDto deal : dealList){
            sum = ArithmeticUtil.doAdd(deal.getTrdProductDeal().getTradeAmt(),sum);
        }
        BigDecimal now = BigDecimal.ZERO;
        for(BaseTradeProductDealDto deal : dealList){
            StlPayOrdersDto temp = new StlPayOrdersDto();
            BeanUtil.copyPropertiesIgnoreNull(temp,stlPayOrdersDto);
            temp.setPortfolioId(deal.getTrdProductDealAdd().getPortfolioId());
            temp.setOrderId(temp.getOrderId() + "R");
            if(dealList.indexOf(deal)==dealList.size()){
                temp.setSettleAmt(ArithmeticUtil.subtract(stlPayOrdersDto.getSettleAmt(),now));
            }
            else{
//                计算占比
                BigDecimal prop  = ArithmeticUtil.divide(deal.getTrdProductDeal().getTradeAmt(),stlPayOrdersDto.getSettleAmt(),13, RoundingMode.HALF_UP);
//                    根据占比处理数值
                BigDecimal thisNum = ArithmeticUtil.multiply(stlPayOrdersDto.getSettleAmt(),prop).setScale(12, RoundingMode.HALF_UP);
//        			    处理已分配的钱
                now = ArithmeticUtil.doAdd(now,thisNum);
//                   把数据存入dto
                temp.setSettleAmt(thisNum);
            }
            List<StlPayOrdersDto> stlList = new ArrayList<>();
            stlList.add(temp);
            deal.setStlPayOrdersList(stlList);
            deal.getTrdProductDeal().setTradeId(deal.getTrdProductDeal().getTradeId() + 'R');
            if(deal.getTrdProductDeal().getPrinAmt() != null && deal.getTrdProductDeal().getShareAmt() == null){
                deal.getTrdProductDeal().setShareAmt(deal.getTrdProductDeal().getPrinAmt());
            }
            deal.getTrdProductDeal().setCreateUser(tradeDto.getCreateUser());
            deal.getTrdProductDeal().setCreateTime(tradeDto.getCreateTime());
            deal.getTrdProductDeal().setCreateDept(tradeDto.getCreateDept());
            deal.getTrdProductDeal().setUpdateTime(tradeDto.getUpdateTime());
            deal.getTrdProductDeal().setUpdateUser(tradeDto.getUpdateUser());
            if (DictCodeList.交易方向_还本.getValue().equals(deal.getTrdProductDeal().getPs()) && DictCodeList.交易方向_到期.getValue().equals(dealTotle.getPs())) {
                deal.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
            }
        }
    }
    /**
     * 生成划款指令（根据划款指令列表分配划款指令）
     * <功能详细描述>
     * @param stlPayOrdersList 划款指令list
     * @param dealAdd 交易附属
     * @param workflowId 工作流代码
     * @return 生成结果
     * @see
     * @since 1.0
     */
    public StlPayOrdersDto gen划款指令流程(List<BussTrdTransferOrderDto> stlPayOrdersList, TrdProductDealAdd dealAdd,
                                    String workflowId) {
        StlPayOrdersDto stlPayOrders = null;
        for (BussTrdTransferOrderDto dto : stlPayOrdersList) {
            if (dto.getPortfolioId().equals(dealAdd.getPortfolioId())) {
                stlPayOrders = dto;
                stlPayOrders.setOrderStatus(DictCodeList.清算操作类型_新建.getValue());
                stlPayOrders.setTradeId(dealAdd.getTradeId());
                stlPayOrders.setWorkflowId(workflowId);
            }
        }
        return stlPayOrders;
    }

    /**
     * 转换dto
     * @param orgList 原划款指令entity
     * @return 业务组件dto
     */
    public List<BussTrdTransferOrderDto> transformOrderDto(List<com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto>orgList){
        List<BussTrdTransferOrderDto> aimList = new ArrayList<>();
        if(orgList!=null&&orgList.size()>0){
            //            转换dto
            orgList.forEach(item->{
                BussTrdTransferOrderDto temp = new BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(temp,item);
                aimList.add(temp);
            });
        }
        return aimList;
    }
    /**
     * 转换dto
     * @param orgList 原划款指令entity
     * @return 业务组件dto
     */
    public List<com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto> transformOrderDtoBack(List<BussTrdTransferOrderDto>orgList){
        List<com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto> aimList = new ArrayList<>();
        if(orgList!=null&&orgList.size()>0){
            //            转换dto
            orgList.forEach(item->{
                com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto temp = new com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto();
                BeanUtil.copyPropertiesIgnoreNull(temp,item);
                aimList.add(temp);
            });
        }
        return aimList;
    }
    private void setDeal(List<BaseTradeProductDealDto> baseTradeProductDealDtos, BaseTradeInfoDto baseTradeInfoDto,
                         TrdProductDealAdd add, TrdProductDeal dealExe) {
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(dealExe.getFinprodType2())) {
            dealExe.setPs(dealExe.getStatus());
        }
        baseTradeProductDealDto.setTrdProductDeal(dealExe);
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        StlPayOrdersDto orders = new StlPayOrdersDto();
        if (baseTradeInfoDto.getStlPayOrders() != null) {
            BeanUtils.copyProperties(baseTradeInfoDto.getStlPayOrders(), orders);
            baseTradeProductDealDto.setStlPayOrdersList(Arrays.asList(new StlPayOrdersDto[] {orders}));
        }
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
    }
    /**
     * 根据交易dto组装共同dto
     * @param baseTradeInfoDto 交易dto
     * @param baseTradeProductDealDtos 共同dto list
     */
    public List<TrdProductDeal> getCommonExcuteDtoByTrade(BaseTradeInfoDto baseTradeInfoDto , List<BaseTradeProductDealDto> baseTradeProductDealDtos) {
        List<TrdProductDeal> result = new ArrayList<>();
        TrdProductDeal deal = baseTradeInfoDto.getTrdProductDeal();
        if (deal == null) {
            deal = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
            TrdProductDeal dealExe = new TrdProductDeal();
            BeanUtils.copyProperties(deal, dealExe);
            if (DictCodeList.交易方向_还本到账.getValue().equals(dealExe.getPs())) {
                dealExe.setPs(DictCodeList.交易方向_还本.getValue());
            }
            if (DictCodeList.交易方向_收息到账.getValue().equals(dealExe.getPs())) {
                dealExe.setPs(DictCodeList.交易方向_收息.getValue());
            }
            setDeal(baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe);
            if(StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_计划类资产.getValue())||
                    StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_项目类资产.getValue())){
                dealExe.setShareAmt(dealExe.getPrinAmt());
            }
            if(StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_债券.getValue())){
                dealExe.setPs(dealExe.getStatus());
            }
            result.add(dealExe);
        }
        else {
            TrdProductDeal dealExe = new TrdProductDeal();
            BeanUtils.copyProperties(deal, dealExe);
            if (DictCodeList.交易方向_还本到账.getValue().equals(dealExe.getPs())) {
                dealExe.setPs(DictCodeList.交易方向_还本.getValue());
            }
            if (DictCodeList.交易方向_收息到账.getValue().equals(dealExe.getPs())) {
                dealExe.setPs(DictCodeList.交易方向_收息.getValue());
            }
            setDeal(baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getTrdProductDealAdd(), dealExe);
            if(StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_计划类资产.getValue())||
                    StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_项目类资产.getValue())){
                dealExe.setShareAmt(dealExe.getPrinAmt());
            }
            if(StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_债券.getValue())){
                dealExe.setPs(dealExe.getStatus());
            }
            result.add(dealExe);
            if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
                TrdProductDeal deal1 = baseTradeInfoDto.getBaseTrade().getTrdProductDeal();
                TrdProductDeal dealExe1 = new TrdProductDeal();
                BeanUtils.copyProperties(deal1, dealExe1);
                if (DictCodeList.交易方向_还本到账.getValue().equals(dealExe1.getPs())) {
                    dealExe1.setPs(DictCodeList.交易方向_还本.getValue());
                }
                if (DictCodeList.交易方向_收息到账.getValue().equals(dealExe1.getPs())) {
                    dealExe1.setPs(DictCodeList.交易方向_收息.getValue());
                }
                if(StringUtil.equals(dealExe1.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_计划类资产.getValue())||
                        StringUtil.equals(dealExe1.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_项目类资产.getValue())){
                    dealExe1.setShareAmt(dealExe1.getPrinAmt());
                }
                setDeal(baseTradeProductDealDtos, baseTradeInfoDto,baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd(), dealExe1);
                result.add(dealExe1);
                if(StringUtil.equals(dealExe.getFinprodType2(),BussinessDictCodeList.金融产品类型_投管用_债券.getValue())){
                    dealExe1.setPs(dealExe1.getStatus());
                }
            }
        }
        return result;
    }

    public List<BaseTradeProductDealDto> findBTradeEffectiveDeal(String tradeId) {
        Map<String, Object> map = new HashMap<>();
        map.put("statusValid", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("statusValid03", DictCodeListExt1.交易流程状态_撤销.getValue());
        map.put("YON_Y", BussinessDictCodeList.是否_是.getValue());
        map.put("isTqzq", "");
        map.put("bTrade", tradeId);
        String bizCode = restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration/findBTradeId",
                map, String.class);
        if (StringUtil.isNotEmpty(bizCode)) {
            map.put("bizCode", bizCode);
            List<BaseTradeProductDealDto> result = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdFundDuration" +
                            "/findEffectiveDeal",
                    map, BaseTradeProductDealDto.class);
            return result;
        }
        return null;
    }

    public Date getPayDate(String findprodId,Long branch){
        //根据资产代码和branch查询支付日
        PtlRecePositionFlowDto flowDto =new PtlRecePositionFlowDto();
        flowDto.setFinprodId(findprodId);
        flowDto.setBranch(branch);
        return basePtlRecePositionService.getPayDate(flowDto);//该资产还本计划的支付日
    }

    /**
     * 根据交易编号查询该交易是否已被撤销
     *
     * @param totleTradeId 交易编号
     * @return 该交易是否已被撤销
     * @see
     * @since 1.0
     */
    public Boolean isAlreadyRevoked(String totleTradeId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("totleTradeId", totleTradeId);
        paramMap.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        paramMap.put("TRADE_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        return preTrdCommonMapper.selectRevokedDataCount(paramMap) > 0;

    }

    // add by etliu 20210426 权益类资产相关代码合并 start
    public List<StlPayOrdersDto> getGroupPayByList(Map<String,Object> param) {
        return restTemplate.postForList(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdProjectDuration" + "/getGroupPayByList",
                param, StlPayOrdersDto.class);
    }
    // add by etliu 20210426 权益类资产相关代码合并 end

    public AjaxResult executeOccupyCreditLines(Object object, String status, String method){
        AjaxResult ar = new AjaxResult();
        if (StringUtil.equals(BussinessDictCodeList.监控授信开关_关.getValue(), CacheUtil.getSysParam(BussinessConstant.ISCREDIT))){
            return ar;
        }
        if (object != null){
            CreditLinesInputDto creditLinesInputDto = null;
            List<CreditLinesInputDto> inputDtoList = new ArrayList<>();
            // 债券存续期
            if (object instanceof TrdBondDuration002ProcessDto){
                TrdBondDuration002ProcessDto inputDto = (TrdBondDuration002ProcessDto)object;
                String ps = AutoPeriodCommonService.change现金流类型To交易方向(inputDto.getCashType());
                //授信释放
                if (ps != null && (ps.equals(DictCodeList.交易方向_还本.getValue()) || ps.equals(DictCodeList.交易方向_到期.getValue()))){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getTradeDate()));
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setPs(ps);
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setTradeStatus(status);
                    // 券面总额
                    creditLinesInputDto.setTradeAmt(inputDto.getPrinAmtTheory());
                    creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                }
            }
            // 债券存续期-交易方向_行权_赎回
            if (object instanceof TrdBondDuration003ProcessDto){
                TrdBondDuration003ProcessDto inputDto = (TrdBondDuration003ProcessDto)object;
                //授信释放
                if (StringUtil.equals(inputDto.getPs(), DictCodeList.交易方向_行权_赎回.getValue())){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setTradeDate(inputDto.getSettleDate());
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    creditLinesInputDto.setTradeId(inputDto.getBizCode());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setShareAmt(inputDto.getTrdFinProductDealTotal().getShareAmt());
                    creditLinesInputDto.setFinprodType2(inputDto.getTrdFinProductDealTotal().getFinprodType2());
                    // 券面总额
                    creditLinesInputDto.setTradeAmt(inputDto.getQty());
                    creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                }
            }
            // 质押式回购
            if (object instanceof TrdRepoDuration002Dto){
                TrdRepoDuration002Dto inputDto = (TrdRepoDuration002Dto)object;
                if (DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(inputDto.getFinprodType2())){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setFinprodId(null);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setCounterId(inputDto.getCounterId());
                    creditLinesInputDto.setTradeAmt(inputDto.getTheoryPrin());
                    creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getSettleDate()));
                    creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_质押式回购.getValue());
                    creditLinesInputDto.setPsPayment(inputDto.getType6());
                    creditLinesInputDto.setSaleCode(DictCodeList.回购拆借方向_逆回购.getValue());
                    creditLinesInputDto.setTerm(Long.parseLong(inputDto.getTerm()));
                    //查询标的券券面总额及最新面值
                    Map<String,Object> param = new HashMap<>();
                    param.put("finprodId",inputDto.getFinprodId());
                    List<PledgeBondInfoDto> list = creditLinesDealsMapper.findPledgeBondInfo(param);
                    if (!StringUtil.isEmptyList(list)){
                        //List<String> assetTypeList = Arrays.asList(CacheUtil.getSysParam(BussinessConstant.CREDIT_ASSET_TYPE_F03).split(","));
                        // 质押券面总额
                        BigDecimal totalAmt = BigDecimal.ZERO;
                        for (PledgeBondInfoDto item : list) {
                            totalAmt = ArithmeticUtil.doAdd(totalAmt,ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getPledgePrin(), item.getPledgeRatio()), new BigDecimal(100), 2));
                        }
                        // 需要扣减额度的券面总额
                        BigDecimal supAmt = list.stream().filter(item ->{
                            // 查询资产类型
                           /* FinProductTypeKey key = new FinProductTypeKey();
                            key.setFinprodId(item.getFinprodId());
                            key.setBranch(0L);
                            FinProductType finProductType = creditLinesDealsMapper.selectByPrimaryKey(key);
                            FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(item.getFinprodId());
                            String astType = finProductType.getType10();
                            if (StringUtil.equals(finProductAdd.getIsSecBond(),DictCodeList.是否_是.getValue())){
                                astType = astType.concat("_") + DictCodeList.是否_是.getValue();
                            }
                            // 资产类型在【不扣减授信参数】列表
                            if (assetTypeList.contains(astType)){
                                return false;
                            }
                            return true;*/
                            if(riskCreditListCommonValidatorService.checkCallCredit(item.getFinprodId())){
                                BigDecimal divide = ArithmeticUtil.divide(ArithmeticUtil.multiply(item.getPledgePrin(), item.getPledgeRatio()), new BigDecimal(100), 2);
                                item.setPledgePrin(divide);
                                return true;
                            }
                            return false;
                        }).map(PledgeBondInfoDto::getPledgePrin).reduce(BigDecimal.ZERO,BigDecimal::add);

                        BigDecimal tradeAmt = supAmt.divide(totalAmt, RoundingMode.HALF_UP).multiply(inputDto.getTheoryPrin()).setScale(2,RoundingMode.HALF_UP);
                        creditLinesInputDto.setTradeAmt(tradeAmt);

                    }
                }
            }
            // 存款类存续期
            if (object instanceof TrdDepoDuration001Dto){
                TrdDepoDuration001Dto inputDto = (TrdDepoDuration001Dto)object;
                if (StringUtil.equals(inputDto.getPingAnCodeSin(), DictCodeListExt1.平安业务类型_存款提前支取.getValue()) ||
                        StringUtil.equals(inputDto.getPingAnCodeSin(), DictCodeListExt1.平安业务类型_存款到期.getValue())){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeId(inputDto.getTotleTradeId());
                    if (StringUtil.isNotEmpty(inputDto.getEarlyWithdrawalAmt())){
                        creditLinesInputDto.setTradeAmt(inputDto.getEarlyWithdrawalAmt());
                        creditLinesInputDto.setPs(DictCodeList.交易方向_还本.getValue());
                    }else {
                        creditLinesInputDto.setTradeAmt(inputDto.getRemainPrinAmt());
                        creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    }
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setCounterId(inputDto.getCounterId());
                }
            }
            // 计划类利率存续期
            if (object instanceof TrdPlanInterestDurationProcessDto){
                TrdPlanInterestDurationProcessDto inputDto = (TrdPlanInterestDurationProcessDto)object;
                String ps = AutoPeriodCommonService.change现金流类型To交易方向(inputDto.getCashType());
                if (DictCodeList.交易方向_到期.getValue().equals(ps) || DictCodeList.交易方向_还本.getValue().equals(ps)){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setPs(ps);
                    creditLinesInputDto.setTradeAmt(inputDto.getReturnAmount());
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getArrivalDate()));
                    // TISHKS-947:子期次占用母计划额度 modify by fuyongsheng 20220506 start
                    creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
                    // TISHKS-947:子期次占用母计划额度 modify by fuyongsheng 20220506 end
                }
            }
            // 计划类利率存续期-红利再投
//            if (object instanceof TrdPlanCurrDuration001Dto){
//                TrdPlanCurrDuration001Dto inputDto = (TrdPlanCurrDuration001Dto)object;
//                if ("01".equals(inputDto.getType())){
//                    creditLinesInputDto = new CreditLinesInputDto();
//                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
//                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
//                    creditLinesInputDto.setTradeStatus(status);
//                    creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
//                    creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
//                    creditLinesInputDto.setShareAmt(inputDto.getQty());
//                    creditLinesInputDto.setTradeId(inputDto.getTotleTradeId());
//                    creditLinesInputDto.setTradeDate(inputDto.getReInvesDate());
//                }
//            }
            // 买断式回购存续期
            if (object instanceof TrdRepoBuyoutDuration002Dto){
                TrdRepoBuyoutDuration002Dto inputDto = (TrdRepoBuyoutDuration002Dto)object;
                if (DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue().equals(inputDto.getFinprodType2())){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setCounterId(inputDto.getCounterId());
                    creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    creditLinesInputDto.setTradeAmt(inputDto.getTheoryPrin());
                    creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getSettleDate()));
                    creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_买断式回购.getValue());
                    creditLinesInputDto.setPsPayment(DictCodeList.回购拆借方向_逆回购.getValue());
                    creditLinesInputDto.setTerm(Long.parseLong(inputDto.getTerm()));
                    creditLinesInputDto.setFinprodId(null);
                    //查询标的券券面总额及最新面值
                    Map<String,Object> param = new HashMap<>();
                    param.put("finprodId",inputDto.getFinprodId());
                    List<PledgeBondInfoDto> list = creditLinesDealsMapper.findPledgeBondInfo(param);

                    if (!StringUtil.isEmptyList(list)){
                        // 先判断标的券类型是否需要扣减授信额度
                        List<String> assetTypeList = Arrays.asList(CacheUtil.getSysParam(BussinessConstant.CREDIT_ASSET_TYPE_F03).split(","));
                        // 查询资产类型
                        FinProductTypeKey key = new FinProductTypeKey();
                        key.setFinprodId(list.get(0).getFinprodId());
                        key.setBranch(0L);
                        FinProductType finProductType = creditLinesDealsMapper.selectByPrimaryKey(key);
                        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(list.get(0).getFinprodId());
                        String astType = finProductType.getType10();
                        if (astType != null && StringUtil.equals(finProductAdd.getIsSecBond(),DictCodeList.是否_是.getValue())){
                            astType = astType.concat("_") + DictCodeList.是否_是.getValue();
                        }
                        // 资产类型在【不扣减授信参数】列表
                        if (assetTypeList.contains(astType)){
                            return ar;
                        }
                        creditLinesInputDto.setTradeAmt(list.get(0).getPledgePrin());
                        //最新面值
                        BigDecimal newFaceValue = creditLinesDealsMapper.getFaceValue(creditLinesInputDto);
                        if (newFaceValue == null){
                            newFaceValue = new BigDecimal(100);
                        }
                        if (creditLinesInputDto.getTradeAmt() != null){
                            creditLinesInputDto.setTradeAmt(newFaceValue.multiply(ArithmeticUtil.divide(creditLinesInputDto.getTradeAmt(),100,2)));
                        }

                        CreditLinesInputDto dto2 = new CreditLinesInputDto();
                        BeanUtil.copyPropertiesIgnoreNull(dto2,creditLinesInputDto);
                        dto2.setZhiYaQuan(list.get(0).getFinprodId());
                        inputDtoList.add(dto2);
                    }
                }

            }
            // 项目类资产存续期
            if (object instanceof TrdProjectDurationProcessDto){
                TrdProjectDurationProcessDto inputDto = (TrdProjectDurationProcessDto)object;
                String ps = AutoPeriodCommonService.change现金流类型To交易方向(inputDto.getCashType());
                if (DictCodeList.交易方向_到期.getValue().equals(ps) || DictCodeList.交易方向_还本.getValue().equals(ps)){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
                    creditLinesInputDto.setPs(ps);
                    creditLinesInputDto.setTradeAmt(inputDto.getReturnAmount());
                    creditLinesInputDto.setTradeDate(DateUtil.parse(inputDto.getArrivalDate()));
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                }
            }
            //债券借贷
            if (object instanceof TrdSecLendDuration002Dto){
                TrdSecLendDuration002Dto inputDto = (TrdSecLendDuration002Dto)object;
                if (StringUtil.equals(inputDto.getPs(), DictCodeList.回购拆借方向_融出.getValue())){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeAmt(inputDto.getShareAmt());
                    creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                    creditLinesInputDto.setTgBusiType(DictCodeListExt2.平安资产类型_债券借贷.getValue());
                    creditLinesInputDto.setPsPayment(inputDto.getPs());
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setTerm(Long.parseLong(inputDto.getTerm()));
                }
            }
            // 拆借
            if (object instanceof TrdLendDuration001Dto){
                TrdLendDuration001Dto inputDto = (TrdLendDuration001Dto)object;
                if (StringUtil.equals(inputDto.getPingAnCodeSin(), DictCodeList.回购拆借方向_拆出.getValue())){
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto,inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeAmt(inputDto.getPrinAmt());
                    creditLinesInputDto.setFinprodId("");
                    creditLinesInputDto.setTgBusiType(DictCodeList.平安资产类型_拆借.getValue());
                    creditLinesInputDto.setPsPayment(inputDto.getPingAnCodeSin());
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                }
            }
            // 利率互换存续期
            if (object instanceof TrdIRSDuration002ProcessDto){
                TrdIRSDuration002ProcessDto inputDto = (TrdIRSDuration002ProcessDto)object;
                if ((inputDto.getFixMdate() != null && inputDto.getMdate().equals(inputDto.getFixMdate()))
                        || (inputDto.getFloatMdate() != null && inputDto.getMdate().equals(inputDto.getFloatMdate()))) {
                    creditLinesInputDto = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                    creditLinesInputDto.setTradeStatus(status);
                    creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                    creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                    creditLinesInputDto.setTradeAmt(inputDto.getPrin());
                    creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                    creditLinesInputDto.setTgBusiType(DictCodeListExt2.平安资产类型_利率互换.getValue());

                }
            }
            if (object instanceof OptionDurationDetailDto){
                OptionDurationDetailDto inputDto = (OptionDurationDetailDto)object;
                if (StringUtil.equals(inputDto.getPs(),DictCodeList.交易方向_卖出.getValue())){
                    return ar;
                }
                creditLinesInputDto = new CreditLinesInputDto();
                BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
                creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
                creditLinesInputDto.setTradeStatus(status);
                creditLinesInputDto.setTradeAmt(inputDto.getAmount());
                creditLinesInputDto.setTgBusiType(DictCodeListExt3.平安资产类型_期权.getValue());
                creditLinesInputDto.setFinprodId(inputDto.getFinprodId());
                creditLinesInputDto.setTradeId(inputDto.getBusiCode());
                long termDays = DateUtil.getBetweenDayNumber(inputDto.getVdate(),inputDto.getMdate());
                creditLinesInputDto.setTerm(termDays);
                creditLinesInputDto.setPs(DictCodeList.交易方向_到期.getValue());
                creditLinesInputDto.setTgBusiType(DictCodeListExt3.平安资产类型_期权.getValue());
            }
            // 基金存续期--红利再投 提交到流程占用，撤销释放
//            if (object instanceof TrdFundDuration001Dto){
//                TrdFundDuration001Dto inputDto = (TrdFundDuration001Dto)object;
//                if (StringUtil.equals(inputDto.getType(),"04")||StringUtil.equals(inputDto.getType(),"01")){
//                    creditLinesInputDto = new CreditLinesInputDto();
//                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto, inputDto);
//                    creditLinesInputDto.setTradeDate(inputDto.getSettleDate());
//                    creditLinesInputDto.setCreditCallType(DictCodeListRsk.授信占用类别_实占.getValue());
//                    creditLinesInputDto.setPs(DictCodeList.交易方向_买入.getValue());
//                    creditLinesInputDto.setTradeId(inputDto.getTotleTradeId());
//                    creditLinesInputDto.setTradeStatus(status);
//                    creditLinesInputDto.setTradeAmt(inputDto.getTradeAmt());
//                    creditLinesInputDto.setShareAmt(inputDto.getQty());
//                    creditLinesInputDto.setTradeDate(inputDto.getReInvesDate());
//                    creditLinesInputDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
//                }
//            }
            boolean isCallCreditInterf = checkCallCreditInterf(creditLinesInputDto,status,method);
            if (isCallCreditInterf){
                inputDtoList.add(creditLinesInputDto);
                if (inputDtoList.size() > 0){
                    if ("check".equals(method)){
                        ar = AllTradeCommonUtil.checkBeforeTradeComplete(inputDtoList);
                    }else {
                        AllTradeCommonUtil.handleBeforeTradeComplete(inputDtoList, DictCodeListRsk.授信占用类别_实占.getValue());
                    }
                }
            }
        }
        return ar;
    }

    private boolean checkCallCreditInterf(CreditLinesInputDto creditLinesInputDto, String status, String method) {
        if (creditLinesInputDto == null){
            return false;
        }
        if ("doComplete".equals(method)){
            if (DictCodeListExt1.交易状态_撤销.getValue().equals(status) && DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
            if (DictCodeList.交易方向_到期.getValue().equals(creditLinesInputDto.getPs())
                    || DictCodeList.交易方向_还本.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }else if("check".equals(method)){
            if(DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }else if("doInsert".equals(method)){
            if(DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }else if("doBack".equals(method)){
            if(DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }else if("doUpdate".equals(method)){
            if(DictCodeList.交易方向_买入.getValue().equals(creditLinesInputDto.getPs())){
                return true;
            }
        }
        return false;
    }
}
