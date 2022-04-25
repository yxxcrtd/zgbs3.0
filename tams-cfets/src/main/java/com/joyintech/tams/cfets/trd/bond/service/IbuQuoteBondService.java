package com.joyintech.tams.cfets.trd.bond.service;

import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealDialogueQuoteDto;
import com.joyintech.service.bankuplink.bond.dto.CfetsBondDealRequestQuoteDto;
import com.joyintech.service.bankuplink.bond.service.CfetsBondDealQuotedPriceService;
import com.joyintech.service.bankuplink.ibu.service.IbuComponentCommonService;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondEtrMapper;
import com.joyintech.tams.cfets.trd.bond.dao.CfetsIbuQuoteBondMapper;
import com.joyintech.tams.cfets.trd.bond.dto.*;
import com.joyintech.tams.cfets.trd.repo.dao.CfetsIbuQuoteRepoMapper;
import com.joyintech.tams.cfets.etrdispatcher.service.CfetsEtrDispatcherService;
import com.joyintech.tams.cfets.etr.bond.dao.CfetsEtrIbBondinfoMapper;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.common.component.base.entrust.impl.BaseEtrInvestInfoServiceImpl;
import com.joyintech.tams.invest.common.component.base.entrust.impl.dao.BaseEtrInvestInfoMapper;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.riskinfrds.util.EntrustCompleteUtil;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.entrust.common.aop.EntrustRdsCommonAop;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zznn
 * @date 2021/11/9
 */
@Service
public class IbuQuoteBondService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(IbuQuoteBondService.class);
    @Autowired
    private IPlatformDate iPlatformDate;

    @Autowired
    private CfetsIbuQuoteBondMapper cfetsIbuQuoteBondMapper;

    @Autowired(required = false)
    private BaseEtrInvestInfoServiceImpl etrInvesInfo;

    @Autowired
    private EtrDistributeMapper etrDistributeMapper;

    @Autowired
    private IbuQuoteBondCalService ibuQuoteBondCalService;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private EtrEntrustMapper etrEntrustMapper;

    @Autowired
    private BaseEtrInvestInfoMapper etrInvestInfoMapper;

    @Autowired
    private CfetsIbuQuoteBondEtrMapper cfetsIbuQuoteBondEtrMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    @Autowired
    private BaseEtrInvestInfoServiceImpl etrInvestInfoServiceImpl;

    @Autowired
    private CfetsEtrDispatcherService cfetsEtrDispatcherService;

    @Autowired
    private CfetsBondDealQuotedPriceService cfetsBondDealQuotedPriceService;

    @Autowired
    private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;

    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    @Autowired
    private RegFinProductDealTotalMapper regFinProductDealTotalMapper;

    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private BaseEtrInvestInfoMapper baseEtrInvestInfoMapper;

    @Autowired
    private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

    @Autowired
    private IbuComponentCommonService ibuComponentCommonService;

    @Autowired
    private CfetsBondDealQuotedPrice002Service cfetsBondDealQuotedPrice002Service;
    @Autowired
    private CfetsBondDealQuotedPrice003Service cfetsBondDealQuotedPrice003Service;

    @Autowired(required = false)
    private BaseEtrCashPositionCommService baseEtrCashPositionCommService;
    @Autowired
    private CfetsEtrIbBondinfoMapper cfetsetrIbBondinfoMapper;

    @Autowired
    private CfetsIbuQuoteRepoMapper cfetsIbuQuoteRepoMapper;
    /**
     * 风险试算check
     * @param entity
     * @return
     */
    public AjaxResult riskCheckNotEmpty(RiskCalRequest entity) {
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(entity.getPtlAllocations())){
            for (PtlAllocationDto ptlAllocation : entity.getPtlAllocations()) {
                if(null==ptlAllocation.getShareAmt()){
                    errorList.add(new ValidMessage("请输入组合分配券面总额"));
                    break;
                }
            }
            ajaxResult.setErrorList(errorList);
        }
        return ajaxResult;
    }

    /**
     * 指令列表
     * @param dto
     * @return
     */
    public List<EntrustDto> listEntrust(EntrustRequest dto,String loginUserId){
        List<EntrustDto> result = new ArrayList<>();
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        setParam(pageParam);
        if(StringUtil.isNotEmpty(loginUserId)){
            pageParam.put("loginUserId", loginUserId);
        }
        List<CfetsBondDealEntrustOrderDto> cfetsBondDealEntrustOrderDtos = cfetsIbuQuoteBondMapper.listEntrust(pageParam);
        cfetsBondDealEntrustOrderDtos.forEach(item ->{
                EntrustDto entrustDto = JsonUtils.json2Object(JsonUtils.object2Json(item),EntrustDto.class);
                result.add(entrustDto);
        });
        return result;
    }

    /**
     *  初始化指令列表(分页)
     * @param dto
     * @return
     */
    public Page<EntrustDto> pageEntrust(EntrustRequest dto){
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        setParam(pageParam);
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsIbuQuoteBondMapper.listEntrust(pageParam));
    }

    /**
     * 指令详细信息
     * @param entrustId
     * @return
     */
    public EntrustDto gettEntrust(String  entrustId){
        EntrustDto result = new EntrustDto();
        Map<String, Object> param = new HashMap<>();
        param.put("entrustIdList",Arrays.asList(entrustId));
        param.put("loginUserId", LoginUserUtil.getUserName());
        param.put("platDate", iPlatformDate.getSysDateStr());
        setParam(param);
        List<CfetsBondDealEntrustOrderDto> cfetsBondDealEntrustOrderDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
        if(CollectionUtils.isNotEmpty(cfetsBondDealEntrustOrderDtos)){
            CfetsBondDealEntrustOrderDto cfetsBondDealEntrustOrderDto = cfetsBondDealEntrustOrderDtos.get(0);
            BeanUtil.copyPropertiesIgnoreNull(result, cfetsBondDealEntrustOrderDto);
            result.setFpriceAmt(ArithmeticUtil.multiply(result.getEntrustNum(),result.getUnitFprice(),new BigDecimal("100")));
            result.setOurEntNum(ArithmeticUtil.divide(result.getOurEntNum(),new BigDecimal(10000),2));
            result.setLendinEntNum(ArithmeticUtil.divide(result.getLendinEntNum(),new BigDecimal(10000),2));
        }
        return result;
    }


    /**
     * 接收指令
     * @param param
     * @return
     */
    public List<ValidMessage> receiveEntrust(Map<String,Object> param){
        List<ValidMessage> errorList=new ArrayList<ValidMessage>();
        param.put("trader",LoginUserUtil.getUserName());
        List<EtrDistribute> list=getDistributeTrader(param);
        List<String> traderList=new ArrayList<String>();
        for(EtrDistribute etrDis:list){
            errorList.addAll(etrInvesInfo.checkDelegateOperation(etrDis.getEntrustId(), DictCodeListExt1.委托操作类型_接收.getValue(), traderList, DictCodeListExt1.委托关闭方式_人工.getValue()));
            etrDis.setUpdateUser(etrDis.getTrader());
        }
        //无错误时，进行接收操作
        if(errorList.size() == 0){
            etrInvesInfo.receptionDelegate(list);
            List<String> entrustIds = StringUtil.splitToList(",",(String)param.get("entrustIds"));
            for(String entrustId:entrustIds){
                //更新操作记录日志
                CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
                oprHisReq.setEntrustId(entrustId);
                oprHisReq.setOperType(DictCodeListExt3.操作类型_接收.getValue());
                joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
            }
        }
        return errorList;
    }

    public List<EtrDistribute> getDistributeTrader(Map<String,Object> param){
        List<EtrDistribute> list=new ArrayList<EtrDistribute>();
        EtrDistributeKey etrKey=new EtrDistributeKey();
        etrKey.setTrader((String) param.get("trader"));
        List<String> entrustIds = StringUtil.splitToList(",",(String)param.get("entrustIds"));
        for(String entrustId:entrustIds){
            etrKey.setEntrustId(entrustId);
            list.add(etrDistributeMapper.selectByPrimaryKey(etrKey));
        }
        return list;
    }

    /**
     * 初始化报价页面(委托代码)
     * @param cfetsBondDealQuoteRequest
     * @return
     */
    public CfetsBondDealQuoteCommonDto queryEntrustInfoById(CfetsBondDealQuoteRequest cfetsBondDealQuoteRequest) {
        CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto = new CfetsBondDealQuoteCommonDto();
        //模式
        String sellPara = CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED");
        Map<String, Object> param = BeanUtil.object2Map(cfetsBondDealQuoteRequest);
        setParam(param);
        List<CfetsBondDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
        if (!StringUtil.isEmptyList(entrustDtos)) {
            CfetsBondDealEntrustOrderDto cfetsBondDealEntrustOrderDto = entrustDtos.get(0);
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealQuoteCommonDto, cfetsBondDealEntrustOrderDto);
            cfetsBondDealQuoteCommonDto.setRemark(StringUtil.EMPTY);
            //清算速度处理
            if(StringUtil.equals(cfetsBondDealEntrustOrderDto.getSettleSpreed(),"0")){
                cfetsBondDealQuoteCommonDto.setSettleSpreedCode(BussinessDictCodeList.清算速度_T加0.getValue());
            }
            if(StringUtil.equals(cfetsBondDealEntrustOrderDto.getSettleSpreed(),"1")){
                cfetsBondDealQuoteCommonDto.setSettleSpreedCode(BussinessDictCodeList.清算速度_T加1.getValue());
            }
            if(StringUtil.equals(cfetsBondDealEntrustOrderDto.getSettleSpreed(),"2")){
                cfetsBondDealQuoteCommonDto.setSettleSpreedCode(BussinessDictCodeList.清算速度_T加2.getValue());
            }
            cfetsBondDealQuoteCommonDto.setShareAmt(entrustDtos.stream().map(CfetsBondDealEntrustOrderDto::getEntrustAvaNum).reduce(BigDecimal.ZERO, BigDecimal::add));
            cfetsBondDealQuoteCommonDto.setShareAmt(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getShareAmt(),new BigDecimal("10000")));
            if(StringUtil.equals(sellPara,"02")){
                //处理券面总额
                cfetsBondDealQuoteCommonDto.setOurEntNum(cfetsBondDealQuoteCommonDto.getShareAmt());
                if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getPs(), com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue())){
                    cfetsBondDealQuoteCommonDto.setSellAbleNum(BigDecimal.ZERO);
                }
                if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getPs(),com.joyintech.tams.constant.DictCodeList.交易方向_卖出.getValue())){
                    cfetsBondDealQuoteCommonDto.setLendinEntNum(BigDecimal.ZERO);
                }
            }
            //处理结算日期
            cfetsBondDealQuoteCommonDto.setTradeDate(iPlatformDate.getSysDate());
            cfetsBondDealQuoteCommonDto.setSettleDate(ibuQuoteBondCalService.getNextWorkDay(cfetsBondDealQuoteCommonDto.getTradeDate(),null==cfetsBondDealEntrustOrderDto.getSettleSpreed()?0:Integer.parseInt(cfetsBondDealEntrustOrderDto.getSettleSpreed())));
            //处理锚定值
            Map<String,Object> anchoringValueMap = handcalAnchoringValue(entrustDtos,"01");
            Set<String> set = anchoringValueMap.keySet();
            cfetsBondDealQuoteCommonDto.setPrice(null);
            cfetsBondDealQuoteCommonDto.setMatureYield(null);
            cfetsBondDealQuoteCommonDto.setExerYield(null);
            for (String key : set) {
                cfetsBondDealQuoteCommonDto.setCalculateField(key);
                if(StringUtil.equals(key,"01")){
                    cfetsBondDealQuoteCommonDto.setPrice((BigDecimal)anchoringValueMap.get(key));
                    break;
                }
                if(StringUtil.equals(key,"02")){
                    cfetsBondDealQuoteCommonDto.setMatureYield(ArithmeticUtil.divide((BigDecimal)anchoringValueMap.get(key),100,6));
                    break;
                }
                if(StringUtil.equals(key,"03")){
                    cfetsBondDealQuoteCommonDto.setExerYield(ArithmeticUtil.divide((BigDecimal)anchoringValueMap.get(key),100,6));
                    break;
                }
            }
            //信贷资产支持证券、资产支持票据锚定值应默认净价且不可修改
            if(StringUtil.equals(cfetsBondDealEntrustOrderDto.getType11(),DictCodeListExt3.行内资产四级分类_交易商协会ABN.getValue())||StringUtil.equals(cfetsBondDealEntrustOrderDto.getType11(),DictCodeListExt3.行内资产四级分类_银保监会主管ABS.getValue())){
                cfetsBondDealQuoteCommonDto.setCalculateField("01");
            }
            //处理行权日
            Map<String,Object> planParam = new HashMap<>();
            planParam.put("finprodId",cfetsBondDealQuoteCommonDto.getFinprodId());
            planParam.put("platDate", iPlatformDate.getSysDateStr());
            planParam.put("exerciseType", com.joyintech.tams.constant.DictCodeList.行权方式_回售.getValue());
            FinExercisePlan finExercisePlan = cfetsIbuQuoteBondEtrMapper.getFinExercisePlan(planParam);
            if(StringUtil.equals(cfetsBondDealQuoteCommonDto.getPs(),com.joyintech.tams.constant.DictCodeList.交易方向_行权_回售.getValue())){
                if(null!=finExercisePlan){
                    cfetsBondDealQuoteCommonDto.setTheoreticalExerciseDate(finExercisePlan.getExerciseDate());
                    cfetsBondDealQuoteCommonDto.setActSetteDate(finExercisePlan.getActExerciseDate());
                }
                cfetsBondDealQuoteCommonDto.setUnitFprice(cfetsBondDealQuoteCommonDto.getPrice());
                cfetsBondDealQuoteCommonDto.setCpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getPrice(),cfetsBondDealQuoteCommonDto.getShareAmt()),new BigDecimal(100),2));
                cfetsBondDealQuoteCommonDto.setFpriceAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(cfetsBondDealQuoteCommonDto.getUnitFprice(),cfetsBondDealQuoteCommonDto.getShareAmt()),new BigDecimal(100),2));
            }/*else{
                if(null!=finExercisePlan){
                    cfetsBondDealQuoteCommonDto.setExercisePlan(true);
                }else{
                    cfetsBondDealQuoteCommonDto.setExercisePlan(false);
                }
            }*/

            //判断未来是否有行权计划
            Map<String, Object> paramExer =new HashMap<>();
            paramExer.put("platDate",DateUtil.platDateFromCache());
            paramExer.put("finprodId",cfetsBondDealQuoteCommonDto.getFinprodId());
            Boolean flag=cfetsetrIbBondinfoMapper.queryAssetIsRight(paramExer);
            cfetsBondDealQuoteCommonDto.setExercisePlan(flag);

            //登记本方交易员应默认当前登录用户姓名
            cfetsBondDealQuoteCommonDto.setQuoteMySideTrader(cfetsBondDealQuoteCommonDto.getMySideTrader());
            cfetsBondDealQuoteCommonDto.setQuoteOuTraderName(cfetsBondDealEntrustOrderDto.getMySideTraderName());
            if(StringUtil.isEmpty(cfetsBondDealQuoteCommonDto.getOuTraderName())&&null!=SecurityUtil.getLoginUser()){
                cfetsBondDealQuoteCommonDto.setMySideTrader(SecurityUtil.getLoginUser().getUserName());
                cfetsBondDealQuoteCommonDto.setOuTraderName(SecurityUtil.getLoginUser().getRealName());
            }
            //处理组合分配
            List<PtlAllocationDto> ptlAllocatios = new ArrayList<>();
            for (CfetsBondDealEntrustOrderDto entrustDto : entrustDtos) {
                PtlAllocationDto ptlAllocationDto = new PtlAllocationDto();
                BeanUtil.copyPropertiesIgnoreNull(ptlAllocationDto,entrustDto);
                ptlAllocationDto.setAvaNum(ArithmeticUtil.multiply(entrustDto.getEntrustAvaNum(),new BigDecimal("10000")));
                //券面总额应默认等于剩余指令额度
                ptlAllocationDto.setShareAmt(ArithmeticUtil.multiply(entrustDto.getAvaNum(),new BigDecimal("10000")));
                ptlAllocatios.add(ptlAllocationDto);
            }
            cfetsBondDealQuoteCommonDto.setPtlAllocatios(ptlAllocatios);
            //计算费用
            try{
                CalRequest calRequest = new CalRequest();
                calRequest.setFpriceAmt(cfetsBondDealQuoteCommonDto.getFpriceAmt());
                calRequest.setSettleDate(cfetsBondDealQuoteCommonDto.getSettleDate());
                calRequest.setPs(cfetsBondDealQuoteCommonDto.getPs());
                calRequest.setFpriceAmt(cfetsBondDealQuoteCommonDto.getFpriceAmt());
                calRequest.setSecManageAcctId(cfetsBondDealQuoteCommonDto.getSecManageAcctId());
                calRequest.setShareAmt(cfetsBondDealQuoteCommonDto.getShareAmt());
                List<TrdTradeFeeDetail> trdTradeFeeDetails = ibuQuoteBondCalService.calFee(calRequest);
                if(CollectionUtils.isNotEmpty(trdTradeFeeDetails)){
                    trdTradeFeeDetails.forEach(item ->{
                        if(StringUtil.equals(item.getFeeType(),BussinessDictCodeList.费用类型_交易手续费.getValue())){
                            cfetsBondDealQuoteCommonDto.setDealFeeAmt(item.getFeeAmt());
                            return;
                        }
                        if(StringUtil.equals(item.getFeeType(),BussinessDictCodeList.费用类型_结算手续费.getValue())){
                            cfetsBondDealQuoteCommonDto.setSettFeeAmt(item.getFeeAmt());
                            return;
                        }
                    });
                }
            }
            catch (Exception e){
                e.printStackTrace();
                logger.error("计算费用异常:"+e.getMessage());
            }
        }
        return cfetsBondDealQuoteCommonDto;
    }

    /**
     * 组合列表添加
     * @param ptlAllocationAddRequest
     * @return
     */
    public List<EntrustDto> ptlAllocationAdd(PtlAllocationAddRequest ptlAllocationAddRequest){
        List<EntrustDto> result = new ArrayList<>();
        Map<String, Object> param = BeanUtil.object2Map(ptlAllocationAddRequest);
        List<String> entrustIdList = ptlAllocationAddRequest.getEntrustIdList();
        entrustIdList.removeAll(Collections.singleton(null));
        param.put("notentrustIdList",CollectionUtils.isNotEmpty(entrustIdList)?entrustIdList:null);
        param.put("platDate", iPlatformDate.getSysDateStr());
        param.put("entrustStatus",Arrays.asList(com.joyintech.tams.constant.DictCodeList.委托状态_已接收.getValue()));
        param.remove("entrustIdList");
        setParam(param);
        List<CfetsBondDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
        //过滤，查询显示的数据需要是全部符合条件的数据
        if(CollectionUtils.isNotEmpty(entrustDtos)){
            filterptlAllocationAdd(entrustDtos,result);
        }
        return result;
    }


    /**
     * 过滤多余数据
     * @param entrustDtos
     * @param result
     */
    public void filterptlAllocationAdd(List<CfetsBondDealEntrustOrderDto> entrustDtos,List<EntrustDto> result){
        Map<String,Object> anchoringValueMap = handcalAnchoringValue(entrustDtos,"02");
        Set<String> set = anchoringValueMap.keySet();
        String calculateField = StringUtil.EMPTY;BigDecimal comparValue = null;
        for (String key : set) {
            calculateField = key;
            comparValue = (BigDecimal)anchoringValueMap.get(key);
            break;
        }
        for (CfetsBondDealEntrustOrderDto item : entrustDtos) {
            comparValue = ArithmeticUtil.round(comparValue,4, RoundingMode.HALF_UP);
            if((StringUtil.equals(calculateField,"01")&&isqualified(item,item.getPrice(),comparValue,"01"))
            ||(StringUtil.equals(calculateField,"02")&&isqualified(item,item.getPrice(),comparValue,"02"))
            ||(StringUtil.equals(calculateField,"03")&&isqualified(item,item.getPrice(),comparValue,"02"))){
                EntrustDto entrustDto = new EntrustDto();
                BeanUtil.copyPropertiesIgnoreNull(entrustDto,item);
                result.add(entrustDto);
                continue;
            }
        }
    }

    /**
     * 是否合规
     * @param dto
     * @param inputvalue
     * @param comparValue
     * @param direction  比较方向  01正向  02反向
     * @return
     */
    public boolean isqualified(CfetsBondDealEntrustOrderDto dto,BigDecimal inputvalue,BigDecimal comparValue,String direction){
            String ps = dto.getPs();
            inputvalue = ArithmeticUtil.round(inputvalue,4, RoundingMode.HALF_UP);
        if(StringUtil.equals(dto.getPriceModel(),DictCodeList.价格模式_指定价格.getValue())&&inputvalue.compareTo(comparValue)==0){return true;}
        if(StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue())){
            if(StringUtil.equals(dto.getPriceModel(),DictCodeList.价格模式_限价指令.getValue())&&StringUtil.equals(direction,"01")&&inputvalue.compareTo(comparValue)<=0){return true;}
            if(StringUtil.equals(dto.getPriceModel(),DictCodeList.价格模式_限价指令.getValue())&&StringUtil.equals(direction,"02")&&inputvalue.compareTo(comparValue)>=0){return true;}
        }
        if(StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_行权_回售.getValue())){
            if(StringUtil.equals(dto.getPriceModel(),DictCodeList.价格模式_限价指令.getValue())&&StringUtil.equals(direction,"01")&&inputvalue.compareTo(comparValue)<=0){return true;}
            if(StringUtil.equals(dto.getPriceModel(),DictCodeList.价格模式_限价指令.getValue())&&StringUtil.equals(direction,"02")&&inputvalue.compareTo(comparValue)>=0){return true;}
        }
        return false;
    }



    /**
     *  初始化组合分配列表(委托列表触发)
     * @param list
     * @return
     */
    public List<EntrustDto>  initPtlAllocation(List<CfetsBondDealEntrustOrderDto> list){
        List<EntrustDto> result = new ArrayList<>();
        for (CfetsBondDealEntrustOrderDto item : list) {
            EntrustDto entrustDto = new EntrustDto();
            BeanUtil.copyPropertiesIgnoreNull(entrustDto,item);
            result.add(entrustDto);
        }
        return result;
    }

    /**
     * 交易执行点击check
     * @param cfetsBondDealQuoteRequest
     * @return
     */
    public AjaxResult dealexecutioncheck(CfetsBondDealQuoteRequest cfetsBondDealQuoteRequest) {
        AjaxResult ajaxResult = new AjaxResult();
        try{
            String message = StringUtil.EMPTY;String ps = StringUtil.EMPTY;
            Map<String, Object> param = BeanUtil.object2Map(cfetsBondDealQuoteRequest);
            setParam(param);
            String exportShow = CacheUtil.getSysParam("EXPORT_SHOW");
            List<CfetsBondDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
            if (!StringUtil.isEmptyList(entrustDtos)) {
                CfetsBondDealEntrustOrderDto one = entrustDtos.get(0);
                ps = one.getPs();
                boolean psFlag = true;
                boolean settleSpreedFlag = true;
                boolean finprodIdFlag = true;
                boolean secManageAcctIdFlag = true;
                boolean partyIdFlag = true;
                boolean tradeMarketFlag = true;
                for (CfetsBondDealEntrustOrderDto item : entrustDtos) {
                    if(!StringUtil.equals(item.getEntrustStatus(), com.joyintech.tams.constant.DictCodeList.委托状态_已接收.getValue())){
                        message = "指令" + item.getEntrustId() + "委托状态不是已接收,不可交易执行!";
                        ajaxResult.setMessage(message);
                        return ajaxResult;
                    }
                    //提交的时候根据交易类型验证
                    /*if (StringUtil.isEmpty(item.getPartyId())&&StringUtil.equals(exportShow,"01")&&StringUtil.equals(item.getTradeMarket(), BussinessDictCodeList.交易场所_银行间.getValue())) {
                        message = "指令" + item.getEntrustId() + "下组合未关联交易账户，请至[银行间业务参数管理-CFETS关联账户]中维护";
                        ajaxResult.setMessage(message);
                        return ajaxResult;
                    }*/
                    if (psFlag && !StringUtil.equals(one.getPs(), item.getPs())) {
                        psFlag = false;
                        message = message + "交易方向OR";
                    }
                    if (settleSpreedFlag && !StringUtil.equals(one.getSettleSpreed(), item.getSettleSpreed())) {
                        settleSpreedFlag = false;
                        message = message + "清算速度OR";
                    }
                    if (finprodIdFlag && !StringUtil.equals(one.getFinprodId(), item.getFinprodId())) {
                        finprodIdFlag = false;
                        message = message + "债券标的OR";
                    }
                    if (secManageAcctIdFlag && !StringUtil.equals(one.getSecManageAcctId(), item.getSecManageAcctId())) {
                        secManageAcctIdFlag = false;
                        message = message + "证券账户OR";
                    }
                    if (tradeMarketFlag && !StringUtil.equals(one.getTradeMarket(), item.getTradeMarket())) {
                        tradeMarketFlag = false;
                        message = message + "交易场所OR";
                    }
                    //EXPORT_SHOW ==01 本币交易导出按钮配置为显示
                    if (StringUtil.equals(exportShow, "01")) {
                        //交易场所+交易方向+清算速度+标的+证券账户+交易账户必须一致
                        if (partyIdFlag && !StringUtil.equals(one.getPartyId(), item.getPartyId())&&(StringUtil.equals(item.getTradeMarket(), BussinessDictCodeList.交易场所_银行间.getValue()))) {
                            partyIdFlag = false;
                            message = message + "交易账户OR";
                        }
                    }
                }
                //提示
                if (StringUtil.isNotEmpty(message)) {
                    ajaxResult.setMessage(StringUtil.join("所选指令不满足汇总条件：[",message.substring(0, message.length() - 2),"]不同"));
                    return ajaxResult;
                }
                //价格模式判断
                //所有数据以保留4位小数，第5位四舍五入
                entrustDtos.stream().forEach(item -> {
                    if (null != item.getPrice()) {
                        item.setPrice(item.getPrice().setScale(4, BigDecimal.ROUND_HALF_UP));
                    }
                    if (null != item.getMatureYield()) {
                        item.setMatureYield(item.getMatureYield().setScale(4, BigDecimal.ROUND_HALF_UP));

                    }
                    if (null != item.getExerYield()) {
                        item.setExerYield(item.getExerYield().setScale(4, BigDecimal.ROUND_HALF_UP));
                    }
                });
                //按价格模式分组
                Map<String, List<CfetsBondDealEntrustOrderDto>> groupByPriceModel = entrustDtos.stream().collect(Collectors.groupingBy(CfetsBondDealEntrustOrderDto::getPriceModel));
                //按锚定值分组
                Map<String, List<CfetsBondDealEntrustOrderDto>> groupByCalculateField = entrustDtos.stream().collect(Collectors.groupingBy(CfetsBondDealEntrustOrderDto::getCalculateField));
                //锚定值是否一致
                boolean translateFlag = true;
                int i = 0;
                String translateKey = StringUtil.EMPTY;
                for (Map.Entry<String, List<CfetsBondDealEntrustOrderDto>> entry : groupByCalculateField.entrySet()) {
                    i++;
                    translateKey = entry.getKey();
                }
                if (i != 1) {
                    translateFlag = false;
                }
                if (groupByPriceModel.containsKey(DictCodeList.价格模式_指定价格.getValue())) {
                    List<CfetsBondDealEntrustOrderDto> cfetsBondDealEntrustOrderDtos = groupByPriceModel.get(DictCodeList.价格模式_指定价格.getValue());
                    if (groupByPriceModel.containsKey(DictCodeList.价格模式_限价指令.getValue())) {
                        //三维度是否一致标志
                        boolean priceFlag = true;
                        boolean matureYieldFlag = true;
                        boolean exerYieldFlag = true;
                        List<CfetsBondDealEntrustOrderDto> priceMode01lList = groupByPriceModel.get(DictCodeList.价格模式_指定价格.getValue());
                        List<CfetsBondDealEntrustOrderDto> priceMode02lList = groupByPriceModel.get(DictCodeList.价格模式_限价指令.getValue());
                        CfetsBondDealEntrustOrderDto priceModelOne = priceMode01lList.get(0);
                        //有限价指令模式   混合模式
                        //最值(最大值、最小值)
                        BigDecimal maximumPrice = BigDecimal.ZERO;
                        BigDecimal maximumMatureYield = BigDecimal.ZERO;
                        BigDecimal maximumExerYield = BigDecimal.ZERO;
                        if (StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue())) {
                            //最小的价格
                            maximumPrice = priceMode02lList.stream().min((u1, u2) -> u1.getPrice().compareTo(u2.getPrice())).get().getPrice();
                            //最大的收益率
                            maximumMatureYield = priceMode02lList.stream().max((u1, u2) -> u1.getMatureYield().compareTo(u2.getMatureYield())).get().getMatureYield();
                            maximumExerYield = priceMode02lList.stream().max((u1, u2) -> u1.getExerYield().compareTo(u2.getExerYield())).get().getExerYield();
                        }
                        if (StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_卖出.getValue())) {
                            //最大的价格
                            maximumPrice = priceMode02lList.stream().max((u1, u2) -> u1.getPrice().compareTo(u2.getPrice())).get().getPrice();
                            //最小的收益率
                            maximumMatureYield = priceMode02lList.stream().min((u1, u2) -> u1.getMatureYield().compareTo(u2.getMatureYield())).get().getMatureYield();
                            maximumExerYield = priceMode02lList.stream().min((u1, u2) -> u1.getExerYield().compareTo(u2.getExerYield())).get().getExerYield();
                        }
                        if (translateFlag) {
                            for (CfetsBondDealEntrustOrderDto item : priceMode01lList) {
                                if (StringUtil.equals(translateKey, "01") && priceModelOne.getPrice().compareTo(item.getPrice()) != 0) {
                                    priceFlag = false;
                                    message = "存在多条指定价格指令，净价需一致";
                                    break;
                                }
                                if (StringUtil.equals(translateKey, "02") && priceModelOne.getMatureYield().compareTo(item.getMatureYield()) != 0) {
                                    matureYieldFlag = false;
                                    message = "存在多条指定价格指令，到期收益率需一致";
                                    break;
                                }
                                if (StringUtil.equals(translateKey, "03") && priceModelOne.getExerYield().compareTo(item.getExerYield()) != 0) {
                                    exerYieldFlag = false;
                                    message = "存在多条指定价格指令，行权收益率需一致";
                                    break;
                                }
                                if (StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue())) {
                                    if (priceFlag && StringUtil.equals(translateKey, "01") && item.getPrice().compareTo(maximumPrice) > 0) {
                                        message = "同时存在指定价格指令和限价指令，指定价格指令的净价不能大于限价指令中净价的最小值";
                                        break;
                                    }
                                    if (matureYieldFlag && StringUtil.equals(translateKey, "02") && item.getMatureYield().compareTo(maximumMatureYield) < 0) {
                                        message = "同时存在指定价格指令和限价指令，指定价格指令的到期收益率不能小于限价指令中到期收益率的最大值";
                                        break;
                                    }
                                    if (exerYieldFlag && StringUtil.equals(translateKey, "03") && item.getExerYield().compareTo(maximumExerYield) < 0) {
                                        message = "同时存在指定价格指令和限价指令，指定价格指令的行权收益率不能小于限价指令中行权收益率的最大值";
                                        break;
                                    }
                                }
                                if (StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_卖出.getValue())) {
                                    if (priceFlag && StringUtil.equals(translateKey, "01") && item.getPrice().compareTo(maximumPrice) < 0) {
                                        message = "同时存在指定价格指令和限价指令，指定价格指令的净价不能小于限价指令中净价的最大值";
                                        break;
                                    }
                                    if (matureYieldFlag && StringUtil.equals(translateKey, "02") && item.getMatureYield().compareTo(maximumMatureYield) > 0) {
                                        message = "同时存在指定价格指令和限价指令，指定价格指令的到期收益率不能大于限价指令中到期收益率的最小值";
                                        break;
                                    }
                                    if (exerYieldFlag && StringUtil.equals(translateKey, "03") && item.getExerYield().compareTo(maximumExerYield) > 0) {
                                        message = "同时存在指定价格指令和限价指令，指定价格指令的行权收益率不能大于限价指令中行权收益率的最小值";
                                        break;
                                    }
                                }
                            }
                            if (StringUtil.isNotEmpty(message)) {
                                ajaxResult.setMessage(message);
                                return ajaxResult;
                            }
                        } else {
                            //三者其中之一一致
                            boolean threeFlag = true;
                            for (CfetsBondDealEntrustOrderDto item : priceMode01lList) {
                                if (priceModelOne.getPrice().compareTo(item.getPrice()) != 0 && priceModelOne.getMatureYield().compareTo(item.getMatureYield()) != 0 && priceModelOne.getExerYield().compareTo(item.getExerYield()) != 0) {
                                    threeFlag = false;
                                    message = "存在多条指定价格指令，净价/到期收益率/行权收益率三者中任一数据需一致";
                                    break;
                                }
                            }
                            if (StringUtil.isNotEmpty(message)) {
                                ajaxResult.setMessage(message);
                                return ajaxResult;
                            }
                            if (threeFlag) {
                                for (CfetsBondDealEntrustOrderDto item : priceMode01lList) {
                                    if (StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue())) {
                                        if (item.getMatureYield().compareTo(maximumMatureYield) < 0) {
                                            message = "同时存在指定价格指令和限价指令，指定价格指令的到期收益率不能小于限价指令中到期收益率的最大值";
                                            break;
                                        }
                                        if (null!=item.getExerYield()&&item.getExerYield().compareTo(maximumExerYield) < 0) {
                                            message = "同时存在指定价格指令和限价指令，指定价格指令的行权收益率不能小于限价指令中行权收益率的最大值";
                                            break;
                                        }
                                        if (item.getPrice().compareTo(maximumPrice) > 0) {
                                            message = "同时存在指定价格指令和限价指令，指定价格指令的净价不能大于限价指令中净价的最小值";
                                            break;
                                        }
                                    }
                                    if (StringUtil.equals(ps, com.joyintech.tams.constant.DictCodeList.交易方向_卖出.getValue())) {
                                        if (item.getMatureYield().compareTo(maximumMatureYield) > 0) {
                                            message = "同时存在指定价格指令和限价指令，指定价格指令的到期收益率不能大于限价指令中到期收益率的最小值";
                                            break;
                                        }
                                        if (null!=item.getExerYield()&&item.getExerYield().compareTo(maximumExerYield) > 0) {
                                            message = "同时存在指定价格指令和限价指令，指定价格指令的行权收益率不能大于限价指令中行权收益率的最小值";
                                            break;
                                        }
                                        if (item.getPrice().compareTo(maximumPrice) < 0) {
                                            message = "同时存在指定价格指令和限价指令，指定价格指令的净价不能小于限价指令中净价的最大值";
                                            break;
                                        }
                                    }
                                }
                            }
                            if (StringUtil.isNotEmpty(message)) {
                                ajaxResult.setMessage(message);
                                return ajaxResult;
                            }
                        }
                    } else {
                        //无限制指令模式
                        //1.存在多条指定价格指令，净价/到期收益率/行权收益率需一致
                        //2.存在多条指定价格指令，净价/到期收益率/行权收益率三者中任一数据需一致
                        //3.存在多条指定价格指令，净价需一致
                        CfetsBondDealEntrustOrderDto priceModelOne = cfetsBondDealEntrustOrderDtos.get(0);
                        if (StringUtil.isNotEmpty(message)) {
                            ajaxResult.setMessage(message);
                            return ajaxResult;
                        }
                        for (CfetsBondDealEntrustOrderDto item : entrustDtos) {
                            //锚定值一致
                            if (translateFlag) {
                                //01 净价  02 到期收益率 03 行权收益率
                                if (StringUtil.equals(translateKey, "01") && priceModelOne.getPrice().compareTo(item.getPrice()) != 0) {
                                    message = "存在多条指定价格指令，净价需一致";
                                    break;
                                }
                                if (StringUtil.equals(translateKey, "02") && priceModelOne.getMatureYield().compareTo(item.getMatureYield()) != 0) {
                                    message = "存在多条指定价格指令，到期收益率需一致";
                                    break;
                                }
                                if (StringUtil.equals(translateKey, "03") && priceModelOne.getExerYield().compareTo(item.getExerYield()) != 0) {
                                    message = "存在多条指定价格指令，行权收益率需一致";
                                    break;
                                }
                            } else {
                                if (priceModelOne.getPrice().compareTo(item.getPrice()) != 0 && priceModelOne.getMatureYield().compareTo(item.getMatureYield()) != 0 && (null!=priceModelOne.getExerYield()&&priceModelOne.getExerYield().compareTo(item.getExerYield()) != 0)) {
                                    message = "存在多条指定价格指令，净价/到期收益率/行权收益率三者中任一数据需一致";
                                    break;
                                }
                            }
                        }
                        if (StringUtil.isNotEmpty(message)) {
                            ajaxResult.setMessage(message);
                            return ajaxResult;
                        }
                    }
                }
            }
        }catch (Exception e){
            ajaxResult.setMessage("交易执行检查异常!");
            return ajaxResult;
        }
        return ajaxResult;
    }

    /**
     * 构造生成交易编号的参数
     * @param ps
     * @return
     */
    public static BussinessCodeParameter getBussinessCodeParameter(String ps) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue(), ps)) {
            codeParameter.setCommonBusinessType(com.joyintech.tams.constant.DictCodeList.共通业务类型_债券买入);
        }
        else {
            codeParameter.setCommonBusinessType(com.joyintech.tams.constant.DictCodeList.共通业务类型_债券卖出);
        }
        return codeParameter;
    }


    /**
     * 构建汇总费用实体
     * @param bussinessId
     * @param feeType
     * @param amount
     * @return
     */
    public RegTradeFeeTotal prepareRegTradeFeeTotal(String bussinessId,String feeType,BigDecimal amount){
        RegTradeFeeTotal regTradeFeeTotal = new RegTradeFeeTotal();
        regTradeFeeTotal.setTradeId(bussinessId);
        regTradeFeeTotal.setFeeType(feeType);
        regTradeFeeTotal.setIsPayWithTrade(com.joyintech.tams.constant.DictCodeList.是否_否.getValue());
        regTradeFeeTotal.setFeeAmt(amount);
        return regTradeFeeTotal;
    }

    /**
     * 构建明细费用实体
     * @param bussinessId
     * @param feeType
     * @param amount
     * @return
     */
    public RegTradeFee prepareRegTradeFee(String bussinessId, String feeType, BigDecimal amount){
        RegTradeFee regTradeFee = new RegTradeFee();
        regTradeFee.setTradeId(bussinessId);
        regTradeFee.setFeeType(feeType);
        regTradeFee.setIsPayWithTrade(com.joyintech.tams.constant.DictCodeList.是否_否.getValue());
        regTradeFee.setFeeAmt(amount);
        return regTradeFee;
    }

    /**
     * 获取锚定值
     * @param entrustDtos
     * @param getsource  01表示指令列表初始化界面   02表示组合列表添加指令初始化
     * @return
     */
    public Map<String,Object> handcalAnchoringValue(List<CfetsBondDealEntrustOrderDto> entrustDtos,String getsource){
        Map<String,Object> result = new HashMap<>();
        //按价格模式分组
        Map<String, List<CfetsBondDealEntrustOrderDto>> groupByPriceModel = entrustDtos.stream().collect(Collectors.groupingBy(CfetsBondDealEntrustOrderDto::getPriceModel));
        //按锚定值分组
        Map<String, List<CfetsBondDealEntrustOrderDto>> groupByCalculateField = entrustDtos.stream().collect(Collectors.groupingBy(CfetsBondDealEntrustOrderDto::getCalculateField));
        //锚定值是否一致
        boolean translateFlag = true;int i=0;String translateKey = StringUtil.EMPTY;
        //是否有默认值
        boolean initValueFlag = true;
        for (Map.Entry<String, List<CfetsBondDealEntrustOrderDto>> entry : groupByCalculateField.entrySet()) {i++;translateKey = entry.getKey();}
        if(i!=1){ translateFlag = false;}
        if(translateFlag){
            //是否显示初始值
            if(groupByPriceModel.containsKey(DictCodeList.价格模式_限价指令.getValue())){
                initValueFlag = false;
            }
        }else{
            FinProduct finProduct = finProductMapper.selectByPrimaryKey(entrustDtos.get(0).getFinprodId());
            if(StringUtil.equals(finProduct.getIsRight(), com.joyintech.tams.constant.DictCodeList.是否_是.getValue())){
                //锚定值非行权收益率，在交易执行面板，应该默认选择到期收益率
                if(!groupByCalculateField.containsKey("03")){
                    translateKey = "02";
                }else{
                    //行权收益率
                    translateKey = "03";
                }
            }else{
                //到期收益率
                translateKey = "02";
            }
            initValueFlag = false;
        }
        if(StringUtil.equals(getsource,"02")){
            initValueFlag = true;
        }
        CfetsBondDealEntrustOrderDto  resultDto = null;
        if(groupByCalculateField.containsKey(translateKey)){
            resultDto =  groupByCalculateField.get(translateKey).get(0);
        }
        if(StringUtil.equals(translateKey,"01")){
            result.put(translateKey,initValueFlag&&null!=resultDto?resultDto.getPrice():null);
            return result;
        }
        if(StringUtil.equals(translateKey,"02")){
            result.put(translateKey,initValueFlag&&null!=resultDto?resultDto.getMatureYield():null);
            return result;
        }
        if(StringUtil.equals(translateKey,"03")){
            result.put(translateKey,initValueFlag&&null!=resultDto?resultDto.getExerYield():null);
            return result;
        }
        return null;
    }

    /**
     * 指令放弃
     * @param entrustId
     * @return
     */
    public String giveUpEntrust(String entrustId) {
        if (StringUtil.isEmpty(entrustId)) {
            return "1";
        }

        EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(entrustId);
        if (etrEntrust == null) {
            return "1";
        }

        // 已接收且完成度为0的指令可以选择【放弃】接收
        if (!StringUtil.equals(etrEntrust.getEntrustStatus(),
                BussinessDictCodeList.委托状态_已接收.getValue())) {
            return "1";
        }
        /*EtrFinishStatusKey etrFinishStatusKey = new EtrFinishStatusKey();
        etrFinishStatusKey.setEntrustId(entrustId);
        List<EtrFinishStatus> etrFinishStatusList = etrInvestInfoMapper.selectEtrFinishStatusByPrimaryKey(
                etrFinishStatusKey);
        if (!StringUtil.isEmptyList(etrFinishStatusList)) {
            return "1";
        }*/
        Map<String,Object> param= new HashMap<>();
        param.put("entrustId", entrustId);
        /*List<EtrQuoteFinishStatus> etrQuoteFinishStatusList =  cfetsIbuQuoteBondEtrMapper.listEtrQuoteFinishStatus(param);
        if (!StringUtil.isEmptyList(etrQuoteFinishStatusList)) {
            return "1";
        }*/
        EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustId);
        if (etrEntrustAdd == null) {
            return "1";
        }

        //是否存在流程中或者有效且数据状态不为 已撤回 已终止

        int i =cfetsIbuQuoteBondEtrMapper.checkDataStatus(param);
        if(i>0){
            return "2";
        }
        //2.查询指令分发的临时表数据  插入  指令分发表，并将状态改为  待接收。
        List<EtrDistributeInterfTmp> distmpList = cfetsIbuQuoteBondEtrMapper.listEtrDistributeInterfTmp(param);

        if(!StringUtil.isEmptyList(distmpList)) {
            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId,com.joyintech.tams.constant.DictCodeList.委托状态_待接收.getValue());

            List<EtrDistribute> disList = new ArrayList<EtrDistribute>();

            for(EtrDistributeInterfTmp entity : distmpList) {
                EtrDistribute dis = new EtrDistribute();
                dis.setEntrustId(entity.getEntrustId());
                dis.setTrader(entity.getTrader());
                dis.setRecStatus(com.joyintech.tams.constant.DictCodeList.委托状态_待接收.getValue());
                dis.setTradingHeader(entity.getTradingHeader());
                disList.add(dis);
            }

            this.etrInvestInfoServiceImpl.distributeDelegate(disList);
        }else{
            boolean status = etrInvestInfoServiceImpl.updateDelegateStatus(entrustId,com.joyintech.tams.constant.DictCodeListExt3.委托状态_待分发.getValue());
        }
        //更新操作记录日志
        CfetsOprHisInfoRequest oprHisReq=new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(entrustId);
        oprHisReq.setOperType(DictCodeListExt3.操作类型_放弃.getValue());
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
        return "3";
    }


    /**
     * check 退回指令
     * @param param
     * @return
     */
    public AjaxResult entrustRefuceCheck(Map<String,Object> param){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if(!StringUtil.isNotEmpty(param.get("remark"))){
            errorList.add(new ValidMessage("请输入退回原因!"));
        }
        setParam(param);
        List<CfetsBondDealEntrustOrderDto> cfetsBondDealEntrustOrderDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
        if(CollectionUtils.isNotEmpty(cfetsBondDealEntrustOrderDtos)){
            cfetsBondDealEntrustOrderDtos.forEach(item -> {
                if(StringUtil.equals(item.getEntrustStatus(), com.joyintech.tams.constant.DictCodeList.委托状态_待接收)
                        ||(StringUtil.equals(item.getEntrustStatus(), com.joyintech.tams.constant.DictCodeList.委托状态_待接收)&&item.getTransactioncom().compareTo(BigDecimal.ZERO)!=0)){
                    errorList.add(new ValidMessage("只可对待分发或者已接收执行进度为0的指令执行退回操作!"));
                }
                param.put("entrustId",item.getEntrustId());
                if(cfetsIbuQuoteBondEtrMapper.checkDataStatus(param)>0){
                    errorList.add(new ValidMessage("存在流程中的交易数据，不可退回!"));
                }
            });
        }

        /*if(cfetsIbuQuoteBondEtrMapper.countNotDjsEtr(param)>0){
            errorList.add(new ValidMessage("只可对待分发指令执行退回操作!"));
        }*/
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 指令退回
     * @param param
     * @return
     */
    public AjaxResult entrustRefuce(Map<String,Object> param) {
        param.put("rekReason",(String)param.get("remark"));
        return cfetsEtrDispatcherService.refuseEntrust(param);
    }

    public void doForcedFinish(Map<String,Object> param) throws Exception{
        setParam(param);
        List<CfetsBondDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
        if(entrustDtos != null && entrustDtos.size()>0){
            List<CfetsEtribBondInfo003Dto> dtolist = new ArrayList<>();
            for(CfetsBondDealEntrustOrderDto  item : entrustDtos){
                // 委托指令查询
                EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(item.getEntrustId());
                // 更改委托状态
                etrEntrust.setEntrustStatus(com.joyintech.tams.constant.DictCodeList.委托状态_已完成.getValue());
                etrEntrust.setRemark(StringUtil.join(etrEntrust.getRemark(),(String)param.get("remark")));
                // 修改委托指令表中值
                int flag = etrEntrustMapper.updateByPrimaryKeySelective(etrEntrust);
                // 修改委托指令分发表中值 add by yangwenjun 20201209
                baseEtrInvestInfoMapper.updatEetrDistribute(etrEntrust);
                // 委托指令附属表查询
                EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(item.getEntrustId());
                //获取已完成额度
                Map<String, Object> params = new HashMap<>();
                params.put("sumTradeFlag","01");
                params.put("portfolioId",etrEntrust.getPortfolioId());
                params.put("entrustId",etrEntrust.getEntrustId());
                params.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
                params.put("YES_OR_NO_N", com.joyintech.tams.constant.DictCodeList.是否_否.getValue());
                params.put("PS_T01", com.joyintech.tams.constant.DictCodeList.交易方向_买入.getValue());
                BigDecimal finshAmt = cfetsIbuQuoteRepoMapper.selectFinshAmt(params);
                BigDecimal entrustAmt = etrEntrust.getEntrustNum() != null ? etrEntrust.getEntrustNum() : etrEntrust.getEntrustAmt();
                etrEntrustAdd.setForcedFinishAmt(ArithmeticUtil.subtract(entrustAmt, finshAmt));
                etrEntrustAddMapper.updateByPrimaryKeySelective(etrEntrustAdd);
                CfetsEtribBondInfo003Dto etrInvestBondInfoDto = new CfetsEtribBondInfo003Dto();
                BeanUtil.copyPropertiesIgnoreNull(etrInvestBondInfoDto,item);
                dtolist.add(etrInvestBondInfoDto);
            }
            List<String> entrustIdList = dtolist.stream().map(
                    CfetsEtribBondInfo003Dto::getEntrustId).collect(Collectors.toList());
            // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit start
            if(CollectionUtils.isNotEmpty(entrustIdList)){
                Map<String, BigDecimal> etrFinishMap = EntrustCompleteUtil.getEtrFinishAmtList(entrustIdList);
                //委托没有做过交易的，和撤销相同逻辑，删除预占
                List<CfetsEtribBondInfo003Dto> recallEntrustIdList = new ArrayList<>();
                //委托已有完成额度，做过交易，走关闭完成的逻辑，更新预占
                List<String> closeEntrustIdList = new ArrayList<>();
                for(CfetsEtribBondInfo003Dto etrDto : dtolist) {
                    if(com.joyintech.background.component.common.pack.common.util.StringUtil.isNotEmpty(etrFinishMap) && com.joyintech.background.component.common.pack.common.util.StringUtil.isNotEmpty(etrFinishMap.get(etrDto.getEntrustId()))
                            && BigDecimal.ZERO.compareTo(etrFinishMap.get(etrDto.getEntrustId()))<0) {
                        closeEntrustIdList.add(etrDto.getEntrustId());
                    }
                    else if(entrustIdList.contains(etrDto.getEntrustId())){
                        recallEntrustIdList.add(etrDto);
                    }
                }

                //没有做过交易，直接关闭的
                if(!com.joyintech.background.component.common.pack.common.util.StringUtil.isEmptyList(recallEntrustIdList)) {
                    // add by yushilong 批量撤销预占 start
                    EntrustRdsCommonAop.doListPreOccupy(recallEntrustIdList, com.joyintech.tams.constant.DictCodeList.平安资产类型_债券.getValue(),
                            BussinessDictCodeListRds.接口系统ID_预占释放批量接口.getValue(), false, null);
                    // add by yushilong 批量撤销预占 end
                }

                //已经有委托完成度的
                if(!com.joyintech.background.component.common.pack.common.util.StringUtil.isEmptyList(closeEntrustIdList)) {
                    EntrustCompleteUtil.doComplete(closeEntrustIdList);
                }
            }
            // ZGZGST-2330:委托关闭判断是否有完成额度，分情况处理 fuyongsheng 20210919 edit end
            // add by zhangrui1 批量委托关闭 end
            if (StringUtil.equals(com.joyintech.tams.constant.DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                //插入旧头寸
                dtolist.forEach(item ->{
                    baseEtrCashPositionCommService.entrustCloseExecute(item.getEntrustId(), com.joyintech.tams.constant.DictCodeList.是否_是.getValue());
                });
            }

            //发送头寸，批量关闭
            cfetsEtrIbBondinfoService.closeDelegatePositionMethod(dtolist, com.joyintech.tams.constant.DictCodeList.平安资产类型_债券.getValue());
            //更新操作日志
            dtolist.forEach(item ->{
                cfetsEtrIbBondinfoService.forcedFinishOprHisMethod(item);
            });
        }
    }
    /**
     * 强制完成check
     * @param param
     * @return
     */
    public AjaxResult doForcedFinishCheck(Map<String,Object> param,boolean isremark){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        setParam(param);
        if(!StringUtil.isNotEmpty(param.get("remark"))&&isremark){
            errorList.add(new ValidMessage("请输入强制完成原因!"));
        }
        if(cfetsIbuQuoteBondEtrMapper.countWflEtr(param)>0){
            errorList.add(new ValidMessage("存在流程中交易数据，不可进行强制完成!"));
        }
        List<CfetsBondDealEntrustOrderDto> entrustDtos = cfetsIbuQuoteBondMapper.listEntrust(param);
        if(CollectionUtils.isNotEmpty(entrustDtos)){
            entrustDtos.forEach(item ->{
                if(!StringUtil.equals(item.getEntrustStatus(),BussinessDictCodeList.委托状态_已接收.getValue())){
                    errorList.add(new ValidMessage("只可对已接收指令执行退回操作!"));
                    return;
                }
                if(null!=item.getTradeFinishPercent()&&item.getTradeFinishPercent().compareTo(BigDecimal.ZERO)==0){
                    errorList.add(new ValidMessage("只可对成交进度>0的已接收未完成指令进行强制完成操作!"));
                    return;
                }
            });
        }
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }


    /**
     * 风险试算实体组装
     * @param riskCalRequest
     * @return
     */
    public  Map<String, Object> doRiskcalPackage(RiskCalRequest riskCalRequest) {
        Map<String, Object> result = new HashMap<>();
        List<Map> bussTrdTransfenpeiDto=new ArrayList<>();
        for(PtlAllocationDto ptlAllocationDto:riskCalRequest.getPtlAllocations()){
            Map<String, Object> stringObjectMap = BeanUtil.object2Map(ptlAllocationDto);
            stringObjectMap.put("tradeAmount",ptlAllocationDto.getShareAmt());
            stringObjectMap.put("fpriceAmt",ptlAllocationDto.getShareAmt());
            stringObjectMap.put("portfolioCode",ptlAllocationDto.getPortfolioId());
            bussTrdTransfenpeiDto.add(stringObjectMap);
        }
        result.put("bussTrdTransfenpeiDto",bussTrdTransfenpeiDto);
        result.put("finprodId",riskCalRequest.getFinprodId());
        result.put("finprodType2",DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        result.put("ps",riskCalRequest.getPs());
        result.put("ccy","CNY");
        return result;

    }

    /**
     * 查询交易成交信息
     * @param entrustId
     * @return
     */
    public List<DealTransactionDetailsDto> listDealTransactionDetails(String entrustId){
        return cfetsIbuQuoteBondMapper.listDealTransactionDetails(entrustId);
    }

    /**
     * 行权收益率是否显示
     *
     * @param
     * @return 行权收益率是否显示
     * @see
     * @since 1.0
     */
    public String isExerYieldDivShow(Map<String, Object> param) {
        String finprodId = (String) param.get("finprodId");
        if (StringUtil.isBlank(finprodId)) {
            return com.joyintech.tams.constant.DictCodeList.是否_否.getValue();
        }

        // 是否含权债
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct == null) {
            return com.joyintech.tams.constant.DictCodeList.是否_否.getValue();
        }
        else {
            String isRight = finProduct.getIsRight();
            if (StringUtil.contains(isRight, GlobalConstant.COMMA)) {
                isRight = StringUtil.splitToList(GlobalConstant.COMMA, isRight).get(0);
            }
            if (!StringUtil.equals(com.joyintech.tams.constant.DictCodeList.是否_是.getValue(), isRight)) {
                return com.joyintech.tams.constant.DictCodeList.是否_否.getValue();
            }
        }

        // tradeDate
        if (StringUtil.isBlank((String) param.get("tradeDate"))) {
            param.put("sysDate", iPlatformDate.getSysDate());
        }
        else {
            param.put("sysDate", DateUtil.parse((String) param.get("tradeDate")));
        }

        int dataCount = cfetsIbuQuoteBondMapper.selectBondHasEexrPlanCount(param);
        if (dataCount > 0) {
            return com.joyintech.tams.constant.DictCodeList.是否_是.getValue();
        }

        return com.joyintech.tams.constant.DictCodeList.是否_否.getValue();
    }

    /**
     * 撤回
     * @param bondQuoteInfoDto
     * @return
     */
    public boolean doReCall(BondQuoteInfoDto bondQuoteInfoDto) {

        CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
        cfetsOprHisInfoRequest.setEntrustId(bondQuoteInfoDto.getQuoteId());
        cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_撤回.getValue());
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_询价方.getValue())) {
            CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,bondQuoteInfoDto);
            cfetsBondDealQuotedPriceService.sendBondDialogueQuoteRevoke(cfetsBondDealDialogueQuoteDto);
            //处理头寸
            cfetsBondDealQuotedPrice002Service.sendPosition(cfetsBondDealQuotedPrice002Service.handpositionByQuoteId(bondQuoteInfoDto.getQuoteId()),DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),null);
            /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                            + "/CfetsBondDealQuotedPrice/sendBondDialogueQuoteRevoke",
                    bondQuoteInfoDto, boolean.class);*/
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_做市方.getValue())) {
            CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto,bondQuoteInfoDto);
            cfetsBondDealQuotedPriceService.sendBondRequestQuoteRevokeReply(cfetsBondDealRequestQuoteDto);
            //处理头寸
            cfetsBondDealQuotedPrice003Service.sendPosition(cfetsBondDealQuotedPrice003Service.handpositionByQuoteId(bondQuoteInfoDto.getQuoteId()),DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),null);
            /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                            + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteRevokeReply",
                    bondQuoteInfoDto, boolean.class);*/
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteRoleType(),
                BussinessDictCodeList.银行间上行本方角色_非做市方.getValue())) {
            CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto,bondQuoteInfoDto);
            cfetsBondDealQuotedPriceService.sendBondRequestQuoteRevoke(cfetsBondDealRequestQuoteDto);
            cfetsBondDealQuotedPrice003Service.sendPosition(cfetsBondDealQuotedPrice003Service.handpositionByQuoteId(bondQuoteInfoDto.getQuoteId()),DictCodeListExt3.流程节点_审批生效.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),DictCodeListExt3.系统动作_撤回.getValue(),null);
            /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                            + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteRevoke",
                    bondQuoteInfoDto, boolean.class);*/
            //处理头寸
        }
        joyinbusinessCommonService.updateCfetsOprHis(cfetsOprHisInfoRequest);
        return false;
    }

    /**
     * 回退
     * @param bondQuoteInfoDto
     * @return
     */
    public boolean doBack(BondQuoteInfoDto bondQuoteInfoDto) {
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.报价方式_对话报价.getValue())) {
            CfetsBondDealDialogueQuoteDto cfetsBondDealDialogueQuoteDto = new CfetsBondDealDialogueQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealDialogueQuoteDto,bondQuoteInfoDto);
            ibuComponentCommonService.bondDialogueQuoteRestore(cfetsBondDealDialogueQuoteDto);
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                cfetsBondDealDialogueQuoteDto.setOperateType("Restore");
                cfetsBondDealDialogueQuoteDto.setIsExistNew(DictCodeList.是否_是.getValue());
                cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(cfetsBondDealDialogueQuoteDto);//头寸处理
            }
            //处理头寸
            cfetsBondDealQuotedPrice002Service.sendPosition(cfetsBondDealQuotedPrice002Service.handpositionByQuoteId(bondQuoteInfoDto.getQuoteId()),DictCodeListExt3.流程节点_流程提交.getValue(),DictCodeListExt3.系统动作_退回.getValue(),DictCodeListExt3.系统动作_退回.getValue(),null);
            return true;
            //return cfetsBondDealQuotedPriceService.bondDialogueQuoteRestore(cfetsBondDealDialogueQuoteDto);
            /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                            + "/CfetsBondDealQuotedPrice/bondDialogueQuoteRestore",
                    bondQuoteInfoDto, boolean.class);*/
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.报价方式_请求报价.getValue())) {
            CfetsBondDealRequestQuoteDto cfetsBondDealRequestQuoteDto = new CfetsBondDealRequestQuoteDto();
            BeanUtil.copyPropertiesIgnoreNull(cfetsBondDealRequestQuoteDto,bondQuoteInfoDto);
            ibuComponentCommonService.sendBondRequestQuoteRestore(cfetsBondDealRequestQuoteDto);
            if (StringUtil.equals(DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BussinessConstant.IS_HANDLE_OLDPOS_FLAG))){
                cfetsBondDealRequestQuoteDto.setOperateType("Restore");
                cfetsBondDealRequestQuoteDto.setIsExistNew(DictCodeList.是否_是.getValue());
                cfetsBondDealQuotedPriceService.doInsetOrUpdateForPtlPositionHold(cfetsBondDealRequestQuoteDto);//头寸处理
            }
            //处理头寸
            cfetsBondDealQuotedPrice003Service.sendPosition(cfetsBondDealQuotedPrice003Service.handpositionByQuoteId(bondQuoteInfoDto.getQuoteId()),DictCodeListExt3.流程节点_流程提交.getValue(),DictCodeListExt3.系统动作_退回.getValue(),DictCodeListExt3.系统动作_退回.getValue(),null);
            return true;
            //return cfetsBondDealQuotedPriceService.sendBondRequestQuoteRestore(cfetsBondDealRequestQuoteDto);
            /*return restTemplate.postForObject(ServiceApplication.BANKUPLINK_SERVICE_NAME
                            + "/CfetsBondDealQuotedPrice/sendBondRequestQuoteRestore",
                    bondQuoteInfoDto, boolean.class);*/
        }
        return false;
    }

    /**
     * 报价删除
     * @param bondQuoteInfoDto
     * @return
     */
    public boolean doDel(BondQuoteInfoDto bondQuoteInfoDto) {
        if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.报价方式_对话报价.getValue())) {
            cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(bondQuoteInfoDto.getQuoteId());
            return cfetsBondDealQuotedPriceService.deleteBondRequestQuote(bondQuoteInfoDto.getQuoteId());
        }
        else if (StringUtil.equals(bondQuoteInfoDto.getQuoteType(),
                BussinessDictCodeList.报价方式_请求报价.getValue())) {
            cfetsBondDealQuotedPriceService.doDeletePtlPositionHoldByQuoteId(bondQuoteInfoDto.getQuoteId());
            return cfetsBondDealQuotedPriceService.deleteBondRequestQuote(bondQuoteInfoDto.getQuoteId());
        }
        return false;
    }

    /**
     * 当日登记(分页列表)
     * @param dto
     * @return
     */
    public Page<CfetsBondDayRegDto> pageDayReg(DayRegRequest dto){
        Map<String, Object> pageParam = BeanUtil.object2Map(dto);
        pageParam.put("loginUserId", LoginUserUtil.getUserName());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09",DictCodeListExt3.操作类型_导出.getValue());
        setParam(pageParam);
        pageParam.put("CONFIRM_STATUS_0", com.joyintech.tams.constant.DictCodeList.确认状态_未确认.getValue());
        return super.queryForPage(dto.getPageMode().getPageNo(), dto.getPageMode().getPageSize(), () -> cfetsIbuQuoteBondMapper.listDayReg(pageParam));
    }

    /**
     * 当日登记check
     * @param dataList
     * @return
     */
    public Map<String,Object> exportDayRegCheck(List<CfetsBondDayRegDto> dataList){
        Map<String,Object> result = new HashMap<>();
        //是否首次导出
        String message = StringUtil.EMPTY;
        List<String>  quoteIds = new ArrayList<>();
        for (CfetsBondDayRegDto item : dataList) {
            if(StringUtil.equals(item.getPs(),com.joyintech.tams.constant.DictCodeList.交易方向_行权_回售.getValue())){
                message = StringUtil.join("交易方向行权-回售,不可导出!业务编号:"+item.getQuoteId());
                break;
            }
            if(!StringUtil.equals(item.getValidStatus(),BussinessDictCodeList.有效状态_有效.getValue())){
                message = StringUtil.join("非有效状态,不可导出!业务编号:"+item.getQuoteId());
                break;
            }
            if(!StringUtil.equals(item.getTradeMarket(),DictCodeListExt1.交易场所_银行间.getValue())){
                message = StringUtil.join("交易场所非银行间,不可导出!业务编号:"+item.getQuoteId());
                break;
            }
            if(!StringUtil.equals(item.getQuoteType(),"01")){
                message = StringUtil.join("报价方式非对话报价,现阶段不可导出!业务编号:"+item.getQuoteId());
                break;
            }
            if(item.getStatisticalTimes()!=0){
                quoteIds.add(item.getQuoteId());
            }
            if(StringUtil.isEmpty(item.getFinprodMarketId())||StringUtil.isEmpty(item.getFinprodName())||StringUtil.isEmpty(item.getPsStr())||StringUtil.isEmpty(item.getOuPartyName())
            ||StringUtil.isEmpty(item.getCpPartyName())||StringUtil.isEmpty(item.getCpTraderName())||null==item.getShareAmt()||null==item.getPrice()||null==item.getMatureYield()||StringUtil.isEmpty(item.getSettleTypeStr())
            ||StringUtil.isEmpty(item.getSettleMethodStr())||StringUtil.isEmpty(item.getSettleTypeStr())){
                result.put("messagesoft","存在必填项为空值");
                break;
            }
        }
        result.put("message",message);
        if(CollectionUtils.isNotEmpty(quoteIds)){
            result.put("quoteIds",quoteIds);
        }
        return result;

    }
    public List<CfetsBondDayRegDto> listDayReg(Map<String,Object> pageParam){
        pageParam.put("loginUserId", LoginUserUtil.getUserName());
        pageParam.put("platDate", iPlatformDate.getSysDateStr());
        pageParam.put("OPR_TYPE_09",DictCodeListExt3.操作类型_导出.getValue());
        setParam(pageParam);
        pageParam.put("CONFIRM_STATUS_0", com.joyintech.tams.constant.DictCodeList.确认状态_未确认.getValue());
        return  cfetsIbuQuoteBondMapper.listDayReg(pageParam);
    }

    /**
     * 当日登记撤销检查
     * @param cfetsBondRegCheckRequst
     * @return
     */
    public AjaxResult checkDayRegRevokeCheck(CfetsBondRegCheckRequst cfetsBondRegCheckRequst){
        AjaxResult ajaxResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        //是否存在流程中数据
        cfetsBondRegCheckRequst.getCfetsBondRegCheckDetaileRequsts().forEach(item ->{
            if(!StringUtil.equals(item.getValidStatus(),BussinessDictCodeList.有效状态_有效.getValue())){
                errorList.add(new ValidMessage("非有效状态数据不支持撤销!业务编号:"+item.getQuoteId()));
            }
            WflValidInputParameter wflValidInputParameter = new WflValidInputParameter();
            wflValidInputParameter.setPageId("IbuQuoteBond004");
            wflValidInputParameter.setBusinessDataPk1(item.getQuoteId());
            if(null != mstWorkflowCommonService &&
                    mstWorkflowCommonService.isInWflProcess(wflValidInputParameter)){
                errorList.add(
                        new ValidMessage(this.getMessage("COMMON_E_0012",item.getQuoteId())));
            }
            //是否已确认成交
            if(cfetsIbuQuoteBondMapper.countMatchInfo(item.getQuoteId(),com.joyintech.tams.constant.DictCodeList.确认状态_已确认.getValue())>0){
                errorList.add(new ValidMessage("当日登记所选登记数据已成交确认不支持撤销!业务编号:"+item.getQuoteId()));
            }
            //回售登记数据，在理论行权日前一交易日（包含前一交易日）之前均支持回售登记撤销操作，支持隔夜撤销。理论行权日之后（包含理论行权日）回售登记不支持手动撤销
            if(StringUtil.equals(item.getPs(), com.joyintech.tams.constant.DictCodeList.交易方向_行权_回售.getValue())){
                RegFinProductDealTotal regFinProductDealTotal = regFinProductDealTotalMapper.selectByPrimaryKey(item.getQuoteId());
                if(iPlatformDate.getSysDate().compareTo(regFinProductDealTotal.getSettleDate())>=0){
                    errorList.add(new ValidMessage("理论行权日之后(包含理论行权日)回售登记不支持手动撤销!业务编号:"+item.getQuoteId()));
                }
            }
            if(CollectionUtils.isNotEmpty(errorList)){
                return;
            }
        });
        ajaxResult.setErrorList(errorList);
        return ajaxResult;
    }

    /**
     * 本币交易导出履历处理
     * @param dataList
     */
    public  void handExportTimes(List<CfetsBondDayRegDto> dataList){
        List<CfetsOprHisInfoRequest> result = new ArrayList<>();
        dataList.forEach(item ->{
            CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
            cfetsOprHisInfoRequest.setEntrustId(item.getQuoteId());
            cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_导出.getValue());
            cfetsOprHisInfoRequest.setSeqNo(IDGenerator.generate(32));
            cfetsOprHisInfoRequest.setOperDate(DateUtil.platDateFromCache());
            cfetsOprHisInfoRequest.setOperTime(DateUtil.platDateFromCache());
            cfetsOprHisInfoRequest.setUserId(SecurityUtil.getLoginUser().getUserName());
            cfetsOprHisInfoRequest.setCreateUser(SecurityUtil.getLoginUser().getUserName());
            cfetsOprHisInfoRequest.setCreateDept(CommonUtil.getDeptCode());
            cfetsOprHisInfoRequest.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
            joyinbusinessCommonService.updateCfetsOprHis(cfetsOprHisInfoRequest);
            //result.add(cfetsOprHisInfoRequest);
        });
        //joyinbusinessCommonMapper.mergeCfetsOprHis(result);
    }

    private void setParam(Map<String,Object> param){
        param.put("finprodType2", BussinessDictCodeList.金融产品类型_债券.getValue());
        param.put("tradeMarket01", BussinessDictCodeList.交易场所_银行间.getValue());
        param.put("tradeMarket02", BussinessDictCodeList.交易场所_商业银行柜台市场.getValue());
        param.put("tradeMarketMode", BussinessDictCodeList.交易市场_二级市场.getValue());
        param.put("entrustStatusUnAccept", BussinessDictCodeList.委托状态_待接收.getValue());
        param.put("entrustStatusAccept", BussinessDictCodeList.委托状态_已接收.getValue());
        param.put("entrustStatusComple", BussinessDictCodeList.委托状态_已完成.getValue());
        param.put("entrustStatusReturn", BussinessDictCodeList.委托状态_已退回.getValue());
        param.put("secAcctSource", BussinessDictCodeList.账户来源_自营.getValue());
        param.put("psT01", BussinessDictCodeList.交易方向_买入.getValue());
        param.put("psT02", BussinessDictCodeList.交易方向_卖出.getValue());
        param.put("psT03", BussinessDictCodeList.交易方向_行权回售.getValue());
        param.put("year",DateUtil.getYear(iPlatformDate.getSysDateStr()));
        param.put("loginUserId", param.containsKey("loginUserId") ? (String) param.get("loginUserId") : LoginUserUtil.getUserName());
        param.put("tradeStatusList", Arrays.asList(BussinessDictCodeList.银行间上行报价状态_已成交.getValue(),
                BussinessDictCodeList.银行间上行报价状态_已撤回.getValue(),
                BussinessDictCodeList.银行间上行报价状态_已终止.getValue(),
                BussinessDictCodeList.银行间上行报价状态_已过期.getValue()));
    }
}

