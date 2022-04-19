package com.joyintech.tams.invest.period.trdrepoduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.invest.common.autoperiod.repo.AutoPeriodRepoCommonService;
import com.joyintech.tams.invest.common.component.business.basicdata.BaseTradeFeeCommService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.*;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.dto.period.trdrepoduration.RepoInfo;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;
import com.joyintech.tams.invest.period.common.service.PeriodCommonService;
import com.joyintech.tams.invest.period.trdrepoduration.dto.TrdRepoDuration002Dto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: tams-invest-parent
 * @Package com.joyintech.period.trdbondduration.service
 * @Description: 债券存续期管理-查询
 * @date Date : 2018年09月21日 14:32
 */
@Service
public class TrdRepoDuration002Service extends BaseService<TrdRepoDuration002Dto> implements BaseProcessService<TrdRepoDuration002Dto> {

    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;

    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;

    @Autowired
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private InvestInterfCommService investInterfCommService;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;
    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    /**
     * AutoPeriodRepoCommonService
     */
    @Autowired
    private AutoPeriodRepoCommonService autoPeriodRepoCommonService;
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;
    /**
     * BaseGetBusinessId
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBusinessId;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    private BaseTradeFeeCommService baseTradeFeeCommService;
    public TrdRepoDuration002Dto findEffectiveData(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findEffectiveData",
            pageParam, TrdRepoDuration002Dto.class);
    }

    @Override
    public void doComplete(TrdRepoDuration002Dto entity, OperationType operationType) {
        FinProductTradeDto tradeDto = genFinProductTradeDto(entity, operationType);
        if(StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())
                ||StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue())
                ||StringUtil.equals(entity.getPs(), DictCodeList.交易方向_到期续做.getValue())){
            tradeDto.getBaseTradeProductDealDtos().forEach(item->{
                item.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
            });
        }
        tradeDto.setNeedSum(true);
        if (OperationType.REVOKE == operationType) {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            entity.setOperateType(BussinessConstant.REVOKE);
        }
        else {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        }
        //到期续做处理
        if (entity.getPstype() != null && entity.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())) {
            tradeDto.getBaseTradeProductDealDtos().forEach(item -> {
                item.getTrdProductDeal().setIntAmt(ArithmeticUtil.subtract(entity.getFpriceAmt(), entity.getTheoryPrinex()));// 画面的到期结算金额 - 首期交易的本金金额
            });
            TrdProductDeal deal = get首期TrdProductDeal(entity);// 获取界面数据
            entity.setTrdProductDeal(deal);
        }

        //String entrustId = finTradeProductMapper.findEntrustIdByTradeId(entity.getFinprodId());
        tradeDto.getBaseTradeProductDealDtos().forEach(item->{
            /*if(StringUtil.isNotEmpty(entrustId)){
                item.setEntrustId(entrustId);
            }*/
            if (OperationType.REVOKE == operationType) {
                getInterfInputDto(item, DictCodeListExt1.交易流程状态_撤销);
            }
            else {
                getInterfInputDto(item, DictCodeListExt1.交易流程状态_有效);
            }
        });
        if (entity.getPstype() != null && entity.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())) {
            tradeDto.setIeFlag(entity.getIeFlag());
        }
        baseTradeProductDealCommServiceImpl.execute(tradeDto);
        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        entity.setIssuePartyId(CacheUtil.getSysParam("issue_party_id"));

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> {
                        baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId());
                        if (OperationType.REVOKE == operationType) {
                            getInterfInputDto(baseTradeProductDealDto1, DictCodeListExt1.交易流程状态_撤销);
                        }
                        else {
                            getInterfInputDto(baseTradeProductDealDto1, DictCodeListExt1.交易流程状态_有效);
                        }
                    });
            baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
            tradeDto.getBaseTradeProductDealDtos().forEach(
                    baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
        }
        if(StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())
                ||StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue())){
            tradeDto.getBaseTradeProductDealDtos().forEach(item->{
                item.getTrdProductDeal().setPs(entity.getPs());
            });
        }
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/doComplete", entity, String.class);
    }

    private void getInterfInputDto(BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易流程状态) {
        if (baseTradeProductDealDto.getTrdProductDealAdd() == null
                || StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())
                || AllTradeCommonUtil.isNotActPortfolio(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
            return;
        }
        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdRepoDuration002");
        investInterfCommDto.setTradeStatus(交易流程状态.getValue());
        baseTradeProductDealDto.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }

    private FinProductTradeDto genFinProductTradeDto(TrdRepoDuration002Dto trdBondDuration001Dto, OperationType operationType) {
        // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        TrdProductDeal trdProductDeal = getTrdProductDeal(trdBondDuration001Dto);

        if (operationType == OperationType.REVOKE) {
            trdProductDeal.setTradeId(trdProductDeal.getTradeId() + "R");
        }
        List<TrdTradeFeeDetail> trdTradeFeeDetailList = new ArrayList<>();

        //到期续做处理
        if(trdBondDuration001Dto.getPstype()!=null && trdBondDuration001Dto.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())){
            //查询原始的首期的交易

            TrdProductDeal  首期交易deal= get首期TrdProductDeal(trdBondDuration001Dto);//获取界面数据


        }
        //有通道费 入库费用明细表
        if(StringUtil.isNotEmpty(trdBondDuration001Dto.getChannelFee())){
        	TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 交易费用明细表.费用类型 = “通道费”
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            // 交易费用明细表.是否随交易支付  根据收取方式判断
        	if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), trdBondDuration001Dto.getCollectType())){
            	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            }else{
            	trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            }
            // 交易费用明细表.交易金额 = 交易.通道费
            trdTradeFeeDetail.setFeeAmt(trdBondDuration001Dto.getChannelFee());
            trdTradeFeeDetailList.add(trdTradeFeeDetail);
        }
//        有交易结算费入费用明细表
        if(StringUtil.isNotEmpty(trdBondDuration001Dto.getActTradeFee())){
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(trdProductDeal.getTradeId());
            // 交易费用明细表.费用类型 = “交易手续费”
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_交易手续费.getValue());
            // 交易费用明细表.是否随交易支付  根据收取方式判断
            if(StringUtil.equals(DictCodeListExt2.收取方式_回款前扣除.getValue(), trdBondDuration001Dto.getCollectType())){
                trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            }else{
                trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            }
            // 交易费用明细表.交易金额 = 交易.通道费
            trdTradeFeeDetail.setFeeAmt(trdBondDuration001Dto.getActTradeFee());
            trdTradeFeeDetailList.add(trdTradeFeeDetail);
        }

//        设置交易费用
        if(CollectionUtils.isNotEmpty(trdTradeFeeDetailList)){
            baseTradeProductDealDto.setTrdTradeFeeDetailList(trdTradeFeeDetailList);
        }
        // 划款指令表Entity
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(
            trdBondDuration001Dto.getBussTrdTransferOrder());
        List<StlPayOrdersDto> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                StlPayOrdersDto dto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                if (operationType == OperationType.REVOKE) {
                    dto.setTradeId(dto.getTradeId() + "R");
                }
                stlPayOrdersDtos.add(dto);
            });
        }
        TrdProductDealAdd add = new TrdProductDealAdd();
        add.setPortfolioId(trdBondDuration001Dto.getPortfolioId());
//        提前终止利率
        add.setTerminateRate(trdBondDuration001Dto.getTerminateRate());
//        罚息金额
        add.setPenaltyIntamt(trdBondDuration001Dto.getPenaltyIntamt());
        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersDtos);
        baseTradeProductDealDto.setAssetType2(trdBondDuration001Dto.getType6());
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        finProductTradeDto.setAssetType2(trdBondDuration001Dto.getType6());
//        finProductTradeDto.setBussRepoSec(trdBondDuration001Dto.get);
        Map<String,Object> map = new HashMap<>();
        map.put("finprodId",trdBondDuration001Dto.getFinprodId());
        List<RepoInfo> repoInfoList = restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findRepoInfo",
                map, RepoInfo.class);
        if(repoInfoList!=null && repoInfoList.size()>0){
//        质押券信息
            BussRepoSecDto bussRepoSecDto = new BussRepoSecDto();
            finProductTradeDto.setBussRepoSec(bussRepoSecDto);
            List<PledgeBondInfoDto> pledgeBondList = new ArrayList<>();
            repoInfoList.forEach(item->{
                PledgeBondInfoDto pledgeBondInfoDto = new PledgeBondInfoDto();
                pledgeBondInfoDto.setBondCode(item.getFinprodMarketId());
                pledgeBondInfoDto.setPledgePrin(item.getQty());
                pledgeBondInfoDto.setFinprodId(item.getPledgeFinprodId());
                pledgeBondList.add(pledgeBondInfoDto);
            });
            bussRepoSecDto.setPledgeBondList(pledgeBondList);
        }
        return finProductTradeDto;
    }

    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(TrdRepoDuration002Dto entity) {
        return AutoPeriodRepoCommonService.getTrdProductDeal(entity);
    }

    @Override
    public void doInsert(TrdRepoDuration002Dto entity, OperationType type) {
        saveDataWhenStartWorkflow(entity, type);
    }

    private void saveDataWhenStartWorkflow(TrdRepoDuration002Dto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getBusiCode())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getBusiCode());
        }
        if (type == OperationType.REVOKE || "submit".equals(entity.getOperateType())) {
            // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
            WflTrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表(entity);
            trdFinProductDealTotal.setPortfolioId(entity.getActPortfolioId());
            if(entity.getPstype()!=null && entity.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())){//到期续做的时候
                trdFinProductDealTotal.setPs(DictCodeList.交易方向_到期续做.getValue());//汇总表只有一条交易方向为到期续做的数据
                trdFinProductDealTotal.setBranch(
                        AutoPeriodCommonService.changePsToBranch(entity.getmDate(), DictCodeList.交易方向_到期续做.getValue()));

                if(type != OperationType.REVOKE) {
                    FinTradeProduct finTradeProduct = new FinTradeProduct();
                    finTradeProduct = gen到期续做交易标的业务表(entity);
                    entity.setFinTradeProduct(finTradeProduct);//金融产品交易标的
                    //金融产品交易汇总附属业务流程--begin
                    WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
                    wflTrdDealTotalAdd.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
                    wflTrdDealTotalAdd.setMpFinprodId(finTradeProduct.getFinprodId());
                    wflTrdDealTotalAdd.setMpBranch(finTradeProduct.getBranch());
                    wflTrdDealTotalAdd.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
                    wflTrdDealTotalAdd.setWorkflowId(entity.getWorkflowId());
                    wflTrdDealTotalAdd.setStatus(DictCodeList.数据状态_新建.getValue());
                    wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);//
                }//新建流程才需要建交易标的表
            }
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<WflStlPayOrders> stlPayOrdersDtos = gen划款指令流程(entity, type);

            // 金融产品交易汇总扩展Entity -> 金融产品交易汇总扩展业务流程表
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = gen汇总扩展业务流程(entity, trdFinProductDealTotal);

            // 金融产品交易Entity -> 金融产品交易业务流程表
            List<WflTrdProductDeal> wflTrdProductDeals = gen交易业务流程(entity);


            if (type == OperationType.REVOKE) {
                trdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId() + 'R');
                wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId());
                trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                    wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + 'R');
                });
            }
            wflTrdProductDeals.forEach(wflTrdProductDeal -> wflTrdProductDealMapper.insertSelective(wflTrdProductDeal));
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);
            wflTrdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            stlPayOrdersDtos.forEach(wflStlPayOrders -> wflStlPayOrdersMapper.insertSelective(wflStlPayOrders));

            if (type != OperationType.REVOKE) {
                // 调用业务组件.【金融产品交易共通.交易产品接口】
                String ps = entity.getPs();
//                调用共通转换交易方向
                FinProductTradeDto tradeDto = genFinProductTradeDto(entity, OperationType.NEW);
                if(StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_投资端_提前终止.getValue())
                        ||StringUtil.equals(entity.getPs(), DictCodeListExt3.交易方向_投资端_逾期返售.getValue())){
                    tradeDto.getBaseTradeProductDealDtos().forEach(item->{
                        item.getTrdProductDeal().setPs(DictCodeList.交易方向_到期.getValue());
                    });
                }
                tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                if(entity.getPstype()!=null && entity.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())) {//到期续做的时候
                    tradeDto.setIeFlag(entity.getIeFlag());//正逆回购支出方向
                }
                baseTradeProductDealCommServiceImpl.execute(tradeDto);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !entity.getActPortfolioId().equals(entity.getPortfolioId())) {
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
            }
            if(type != OperationType.REVOKE && entity.getPstype()!=null && entity.getPstype().equals(DictCodeList.交易方向_到期续做.getValue())){
                //到期续做新建交易标的信息
                restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/doInsert", entity,
                        String.class);
            }
        }
    }

    @Override
    public void doUpdate(TrdRepoDuration002Dto entity) {
        baseTradeProductDealCommServiceImpl.delete(entity.getWorkflowId());
        saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }

    private List<WflTrdProductDeal> gen交易业务流程(TrdRepoDuration002Dto entity) {
        return autoPeriodRepoCommonService.gen交易业务流程(entity);
    }

    private WflPaTrdFinDealTotal gen汇总扩展业务流程(TrdRepoDuration002Dto entity, WflTrdFinProductDealTotal trdFinProductDealTotal) {
        return AutoPeriodRepoCommonService.gen汇总扩展业务流程(entity, trdFinProductDealTotal);
    }

    private List<WflStlPayOrders> gen划款指令流程(TrdRepoDuration002Dto entity, OperationType type) {
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(entity.getBussTrdTransferOrder());
        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                dto.setTradeId(entity.getBusiCode());
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setStatus(DictCodeList.数据状态_新建中.getValue());
                dto.setCreateDept(entity.getCreateDept());
                dto.setUpdateTime(entity.getUpdateTime());
                dto.setCreateUser(entity.getCreateUser());
                dto.setCreateTime(entity.getCreateTime());
                dto.setWorkflowStatus(DictCodeList.数据状态_新建.getValue());
                if (type == OperationType.REVOKE) {
                    dto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                    dto.setTradeId(dto.getTradeId() + "R");
                }
                else {
                    dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                }
                stlPayOrdersDtos.add(dto);
            });
        }
        return stlPayOrdersDtos;
    }

    private static WflTrdFinProductDealTotal gen汇总流程表(TrdRepoDuration002Dto entity) {
        return AutoPeriodRepoCommonService.gen汇总流程表(entity, DictCodeList.共通业务类型_质押式回购存续期.getValue());
    }

    @Autowired
    private PeriodCommonService periodCommonService;

    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
        TrdRepoDuration002Dto entity = getBussinessEntityByWorkflowId(workflowId);
        TrdCommonDeleteDto trdCommonDeleteDto = new TrdCommonDeleteDto();
        trdCommonDeleteDto.setWorkflowId(workflowId);
        if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
            && DictCodeList.回购类型_质押式竞价回购.getValue().equals(entity.getType6())) {
            trdCommonDeleteDto.setWflTrdFinProductDealTotal(gen汇总流程表(entity));
        }

        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/doDelete", trdCommonDeleteDto,
            String.class);
    }

    public Page<RepoInfo> findRepoInfo() {
        return new Page<>(restTemplate.postForList(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findRepoInfo",
            RequestUtil.getPageParam(), RepoInfo.class));
    }

    @Override
    public void doBack(TrdRepoDuration002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
    }

    @Override
    public void doRevoke(TrdRepoDuration002Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getBusiCode());
    }

    /**
     * 计算两个日期之间天数
     * @return 日期区间
     */
    public String getBetweenDays(Map<String,Object> map){
        String vdateString = (String)map.get("vdate");
        String mdateString = (String)map.get("mdate");
        Long cashholdingdays = null;
        if(StringUtil.isNotEmpty(vdateString) && StringUtil.isNotEmpty(mdateString)){
            cashholdingdays = DateUtil.getBetweenDate(vdateString, mdateString, DateUtil.DurationType.天);
        }
        return cashholdingdays != null ? cashholdingdays.toString() : null;
    }

    private FinTradeProduct gen到期续做交易标的业务表(TrdRepoDuration002Dto entity) {
        FinTradeProductKey key = new FinTradeProductKey();
        key.setFinprodId(entity.getFinprodId());
        key.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        FinTradeProduct orgfinTradeProduct = finTradeProductMapper.selectByPrimaryKey(key);//先查询原始交易的标的
        // 金融产品代码 = 金融产品交易Entity.交易编号（自动生成）
        if (baseGetBusinessId != null) {
        BussinessCodeParameter codeParameter = new BussinessCodeParameter();
        if (StringUtil.equals(entity.getPs(),
                DictCodeList.回购拆借方向_正回购.getValue())) {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_正回购录入);
        } else {
            codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_逆回购录入);
        }
            orgfinTradeProduct.setFinprodId(baseGetBusinessId.buildBussinessCode(codeParameter));
        }else {
            orgfinTradeProduct.setFinprodId(IDGenerator.generate());
        }
        // 分支序号 = 系统参数.分支序号固定值（和估值核算保持一致，0）
        orgfinTradeProduct.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
            // 名义本金 = 画面.续做本金
        orgfinTradeProduct.setPrin(entity.getTheoryPrinex());
            // 起息日 = 画面.起息日-原始交易的到期日
        orgfinTradeProduct.setVdate(DateUtil.parse(entity.getmDate()));
            // 到期日 = 画面.续做到期日
        orgfinTradeProduct.setMdate(DateUtil.parse(entity.getmDatex()));
            //交易日
        orgfinTradeProduct.setVpayDate(orgfinTradeProduct.getVdate());
            //到期日
        orgfinTradeProduct.setMpayDate(orgfinTradeProduct.getMdate());
        orgfinTradeProduct.setActCapDays(DateUtil.getBetweenDayNumber(orgfinTradeProduct.getVdate(),orgfinTradeProduct.getMdate()));
            //finTradeProduct.setActCapDays(input.getActCapDays());
            // 利率类型 = 数据字典缓存.利率类型.固定利率
        orgfinTradeProduct.setIntType(DictCodeList.利率类型_固定利率.getValue());
            // 固定利率 = 画面.回购利率/100
        orgfinTradeProduct.setIntRate(
                    ArithmeticUtil.divide(entity.getIntRatex(), BigDecimal.valueOf(100), 8));
            // 到期本金 = 画面.本续做本金
        orgfinTradeProduct.setmPrinAmt(entity.getTheoryPrinex());
            // 到期利息 = 画面.续做利息
        orgfinTradeProduct.setmIntAmt(entity.getTheoryIntex());
            // 到期金额 = 画面.续做到期结算金额
        orgfinTradeProduct.setmTradeAmt(entity.getTradeAmtex());
            // 交易对手 = 画面.交易对手
        orgfinTradeProduct.setCounterId(entity.getCounterIdex());
        orgfinTradeProduct.setTerm(Long.parseLong(entity.getTermex()));
        orgfinTradeProduct.setTermType(entity.getTermTypeReback());//期限品种
            // 工作流代码
        orgfinTradeProduct.setWorkflowId(entity.getWorkflowId());
        orgfinTradeProduct.setCreateTime(new Date());
        orgfinTradeProduct.setUpdateTime(new Date());

        return orgfinTradeProduct;
    }

    /**
     * 为调用共通组件组装的entity
     *组装首期的回购交易
     * @param entity
     * @return
     */
    public TrdProductDeal get首期TrdProductDeal(TrdRepoDuration002Dto entity) {
        return AutoPeriodRepoCommonService.get首期TrdProductDeal(entity);
    }

    public TrdRepoDuration002Dto findTrdMarketByFinprodId(Map<String, Object> pageParam) {
        pageParam.put("userName", LoginUserUtil.getUserName());
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findTrdMarketByFinprodId",
                pageParam, TrdRepoDuration002Dto.class);
    }

    /**
     * 划款信息
     *
     * @param bizCode 业务编号
     * @return
     */
    public TrdRepoDuration002Dto findTrdDealInfoByBusiCode(String bizCode) {
        return restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdRepoDuration/findTrdDealInfoByBusiCode",
                bizCode, TrdRepoDuration002Dto.class);
    }


    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(TrdRepoDuration002Dto input) {
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", input.getPs());//买入
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
       // param.put("secAcctId", input.getSecAcctId());//证券户信息
        param.put("chlId", input.getChlId());
        return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
                param, PledgeBondInfoDto.class);
    }

}
