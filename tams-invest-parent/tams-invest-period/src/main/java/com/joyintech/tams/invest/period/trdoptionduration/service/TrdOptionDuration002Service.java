package com.joyintech.tams.invest.period.trdoptionduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.dto.FileCommonDto;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.component.business.trade.BaseTradeProductDealCommService;
import com.joyintech.tams.component.business.trade.BussTrdTransferOrderService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.common.AutoPeriodCommonService;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseTradeProductDealDto;
import com.joyintech.tams.invest.common.dto.MstFileInputParameter;
import com.joyintech.tams.invest.common.dto.StlPayOrdersDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.common.InvestInterfCommDto;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;
import com.joyintech.tams.invest.common.service.InvestInterfCommService;
import com.joyintech.tams.invest.period.trdoptionduration.dto.OptionDurationDetailDto;

import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.FinProductTradeDto;

import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrdOptionDuration002Service extends BaseService<OptionDurationDetailDto>
        implements BaseProcessService<OptionDurationDetailDto> {

    /**
     * 列表的name属性
     */
    public static final String TABLE_NAME = "TrdOptionDuration002";

    @Autowired
    private BussTrdTransferOrderService bussTrdTransferOrderImplService;
    @Autowired
    private BaseTradeProductDealCommService baseTradeProductDealCommServiceImpl;
    @Autowired
    private AutoPeriodCommonService autoPeriodCommonService;
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private WflStlPayOrdersMapper wflStlPayOrdersMapper;
    @Autowired
    private InvestInterfCommService investInterfCommService;

    @Autowired
    private WflTrdFinProductDealTotalMapper wflTrdFinProductDealTotalMapper;

    @Autowired
    private WflTrdProductDealMapper wflTrdProductDealMapper;

    @Autowired
    private WflTrdProductDealAddMapper wflTrdProductDealAddMapper;

    @Autowired
    private WflTrdDealTotalAddMapper wflTrdDealTotalAddMapper;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 编号生成
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    /**
     * 金融产品交易汇总扩展业务流程表
     */
    @Autowired(required = false)
    private WflPaTrdFinDealTotalMapper wflPaTrdFinDealTotalMapper;

    @Override
    public void doInsert(OptionDurationDetailDto entity, OperationType type) {
        saveDataWhenStartWorkflow(entity, type);
    }

    @Override
    public void doUpdate(OptionDurationDetailDto entity) {
        saveDataWhenStartWorkflow(entity, OperationType.NEW);
    }

    /**
     * 为调用共通组件组装的entity
     *
     * @param entity
     * @return
     */
    public static TrdProductDeal getTrdProductDeal(OptionDurationDetailDto entity) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        trdProductDeal.setPsPayment(entity.getSffx());
        trdProductDeal.setTradeId(entity.getTotleTradeId());
        trdProductDeal.setFinprodId(entity.getFinprodId());
        trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_期权.getValue()); // TODO 估值核算 金融产品类型
        trdProductDeal.setFinprodType2(entity.getFinprodType2());
        trdProductDeal.setCcy(entity.getCcy());
        if(StringUtil.equals(entity.getXqType(),"DQXQ")){
            trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期到期交易.getValue());
            trdProductDeal.setPs(DictCodeListExt1.交易方向_到期行权.getValue());
            trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_期权到期行权.getValue());
        }else if(StringUtil.equals(entity.getXqType(),"TQXQ")){
            trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期提前行权交易.getValue());
            trdProductDeal.setPs(DictCodeListExt1.交易方向_提前行权.getValue());
            trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_期权提前行权.getValue());
        }else{
            trdProductDeal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期放弃行权交易.getValue());
            trdProductDeal.setPs(DictCodeListExt1.交易方向_放弃行权.getValue());
            trdProductDeal.setTgBusiType(DictCodeListExt3.平安业务类型_期权放弃行权.getValue());
        }
        if("FQXQ".equals(entity.getXqType())){
            trdProductDeal.setTradeDate(entity.getFqxqDate());
            trdProductDeal.setSettleDate(entity.getMdate());
        }else{
            trdProductDeal.setTradeDate(entity.getXqDate());
            trdProductDeal.setSettleDate(entity.getZfDate()==null?entity.getXqDate():entity.getZfDate());////update zhangweiqi 2020-02-12 修改
        }
        trdProductDeal.setVdate(entity.getVdate());
        trdProductDeal.setCounterId(entity.getCounterId());
        trdProductDeal.setMdate(entity.getMdate());
        trdProductDeal.setShareAmt(entity.getAmount());
        trdProductDeal.setPrinAmt(entity.getPrinAmt());
        trdProductDeal.setTradeAmt(entity.getTradeAmt());
        trdProductDeal.setCounterId(entity.getCounterId());
        trdProductDeal.setTrader(entity.getTrader());
        trdProductDeal.setPayDate(entity.getZfDate()==null?entity.getXqDate():entity.getZfDate());//update zhangweiqi 2020-02-12 修改
        trdProductDeal.setTotleTradeId(entity.getBusiCode());
        trdProductDeal.setFpriceAmt(trdProductDeal.getTradeAmt());
        trdProductDeal.setDqje(entity.getDqprinAmt());
        return trdProductDeal;
    }

    @Override
    public void doComplete(OptionDurationDetailDto entity, OperationType operationType) {
        entity.setActPortfolioId(entity.getPortfolioId());
        entity.setPortfolioId(AllTradeCommonUtil.exchangePortfolio(entity.getPortfolioId()));
        FinProductTradeDto tradeDto = genFinProductTradeDto(entity,operationType);
        if (OperationType.REVOKE == operationType) {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
            entity.setOperateType(BussinessConstant.REVOKE);
        } else {
            tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
        }

        tradeDto.getBaseTradeProductDealDtos().forEach(item->{
            if (OperationType.REVOKE == operationType) {
                getInterfInputDto(item, DictCodeListExt1.交易流程状态_撤销);
            }
            else {
                getInterfInputDto(item, DictCodeListExt1.交易流程状态_有效);
            }
        });

        baseTradeProductDealCommServiceImpl.execute(tradeDto);

        if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
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

        // 新建交易撤销数据 或 插入Entity到对应的业务DB中, 并删除流程表中的数据
        restTemplate.postForObject(ServiceApplication.PERIOD_SERVICE_NAME + "/TrdOptionDuration/doComplete", entity, String.class);
    }

    private void getInterfInputDto(BaseTradeProductDealDto baseTradeProductDealDto,
                                   DictCodeListExt1 交易流程状态) {
        if (baseTradeProductDealDto.getTrdProductDealAdd() == null
                || StringUtil.isEmpty(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())
                || AllTradeCommonUtil.isNotActPortfolio(baseTradeProductDealDto.getTrdProductDealAdd().getPortfolioId())) {
            return;
        }

        InvestInterfCommDto investInterfCommDto = new InvestInterfCommDto();
        investInterfCommDto.setPageId("TrdOptionDuration002");
        investInterfCommDto.setTradeStatus(交易流程状态.getValue());
        investInterfCommDto.setTrdProductDeal(baseTradeProductDealDto.getTrdProductDeal());
        investInterfCommDto.setTrdProductDealAdd(baseTradeProductDealDto.getTrdProductDealAdd());
        if (baseTradeProductDealDto.getStlPayOrdersList() != null
                && !baseTradeProductDealDto.getStlPayOrdersList().isEmpty()) {
            investInterfCommDto.setStlPayOrders(baseTradeProductDealDto.getStlPayOrdersList().get(0));
        }
        investInterfCommService.doInvestInterf(investInterfCommDto);
    }

    public FinProductTradeDto genFinProductTradeDto(OptionDurationDetailDto entity,OperationType operationType) {
        // 5.构造调用组件的输入参数DTO
        FinProductTradeDto finProductTradeDto = new FinProductTradeDto();
        finProductTradeDto.setNeedSum(true);
        List<BaseTradeProductDealDto> baseTradeProductDealDtos = new ArrayList<>();
        BaseTradeProductDealDto baseTradeProductDealDto = new BaseTradeProductDealDto();
        baseTradeProductDealDtos.add(baseTradeProductDealDto);
        TrdProductDeal trdProductDeal = getTrdProductDeal(entity);

        // 划款指令表Entity
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(
                entity.getBussTrdTransferOrder());
        List<StlPayOrdersDto> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                StlPayOrdersDto dto = new StlPayOrdersDto();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                dto.setTradeId(entity.getBusiCode());
                stlPayOrdersDtos.add(dto);
            });
        }
        TrdProductDealAdd add = new TrdProductDealAdd();
        add.setPortfolioId(entity.getPortfolioId());

        baseTradeProductDealDto.setTrdProductDealAdd(add);
        baseTradeProductDealDto.setTrdProductDeal(trdProductDeal);
        baseTradeProductDealDto.setStlPayOrdersList(stlPayOrdersDtos);
        finProductTradeDto.setBaseTradeProductDealDtos(baseTradeProductDealDtos);
        return finProductTradeDto;
    }

    public static WflTrdFinProductDealTotal gen汇总流程表(OptionDurationDetailDto entity) {
        WflTrdFinProductDealTotal trdFinProductDealTotal = new WflTrdFinProductDealTotal();
        trdFinProductDealTotal.setTotleTradeId(entity.getBusiCode());

        trdFinProductDealTotal.setFinprodId(entity.getFinprodId());
        trdFinProductDealTotal.setFinprodType2(entity.getFinprodType2());
        trdFinProductDealTotal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_期权.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setCcy(entity.getCcy());
        trdFinProductDealTotal.setBranch(0L);
        trdFinProductDealTotal.setSettleDate(entity.getZfDate());
        trdFinProductDealTotal.setTradeDate(entity.getXqDate());
        if(StringUtil.equals(entity.getXqType(),"DQXQ")){
            trdFinProductDealTotal.setPs(DictCodeListExt1.交易方向_到期行权.getValue());
            trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期到期交易.getValue());
            trdFinProductDealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_期权到期行权.getValue());
        }else if(StringUtil.equals(entity.getXqType(),"TQXQ")){
            trdFinProductDealTotal.setPs(DictCodeListExt1.交易方向_提前行权.getValue());
            trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期提前行权交易.getValue());
            trdFinProductDealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_期权提前行权.getValue());
        }else{
            trdFinProductDealTotal.setPs(DictCodeListExt1.交易方向_放弃行权.getValue());
            trdFinProductDealTotal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期放弃行权交易.getValue());
            trdFinProductDealTotal.setTgBusiType(DictCodeListExt3.平安业务类型_期权放弃行权.getValue());
            trdFinProductDealTotal.setTradeDate(entity.getFqxqDate());
        }
        trdFinProductDealTotal.setSecManageAcctId(entity.getSecManageAcctId());
        trdFinProductDealTotal.setVdate(entity.getVdate());
        trdFinProductDealTotal.setMdate(entity.getMdate());
        trdFinProductDealTotal.setShareAmt(entity.getAmount());
        trdFinProductDealTotal.setTradeAmt(entity.getTradeAmt());//暂时用借贷费用替换实际结算金额
        trdFinProductDealTotal.setTradeMarket(entity.getTradeMarket());
        trdFinProductDealTotal.setfTradeId(entity.getfTradeId());//银行间成交编号
        trdFinProductDealTotal.setCounterId(entity.getCounterId());
        trdFinProductDealTotal.setTrader(entity.getTrader());
        trdFinProductDealTotal.setCounterTrader(entity.getCounterTrader());
        trdFinProductDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
        trdFinProductDealTotal.setWorkflowId(entity.getWorkflowId());
        trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        trdFinProductDealTotal.setStatus(DictCodeList.数据状态_新建.getValue());
        trdFinProductDealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        trdFinProductDealTotal.setPortfolioId(entity.getPortfolioId());
        trdFinProductDealTotal.setRemark(entity.getRemark());
        return trdFinProductDealTotal;
    }

    /**
     *
     * @param entity
     * @param type
     * @return void
     */
    private void saveDataWhenStartWorkflow(OptionDurationDetailDto entity, OperationType type) {
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTotleTradeId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getTotleTradeId());
        }

        if (type == OperationType.REVOKE || "submit".equals(entity.getOperateType())) {
            // 金融产品交易信息DTO.金融产品交易汇总Entity -> 金融产品交易汇总业务流程表
            WflTrdFinProductDealTotal trdFinProductDealTotal = gen汇总流程表(entity);
            // 金融产品交易信息DTO.划款指令表Entity -> 划款指令业务流程表
            List<WflStlPayOrders> stlPayOrdersDtos = gen划款指令流程(entity, type);
            // 金融产品交易Entity -> 金融产品交易业务流程表
            List<WflTrdProductDeal> wflTrdProductDeals = gen交易业务流程(entity);

            if (type == OperationType.REVOKE) {
                trdFinProductDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId() + 'R');
                trdFinProductDealTotal.setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                wflTrdProductDeals.forEach(wflTrdProductDeal -> {
                    wflTrdProductDeal.setTradeStatus(DictCodeListExt1.交易流程状态_撤销.getValue());
                    wflTrdProductDeal.setTradeId(wflTrdProductDeal.getTradeId() + 'R');
                });
            }
            if(StringUtil.isNotEmpty(wflTrdProductDeals)){
                for (WflTrdProductDeal wflTrdProductDeal: wflTrdProductDeals) {
                    wflTrdProductDealMapper.insertSelective(wflTrdProductDeal);
                    WflTrdProductDealAdd wflTrdProductDealAdd = new WflTrdProductDealAdd();
                    BeanUtil.copyPropertiesIgnoreNull(wflTrdProductDealAdd, wflTrdProductDeal);
                    wflTrdProductDealAddMapper.insertSelective(wflTrdProductDealAdd);
                }
            }
            WflTrdDealTotalAdd wflTrdDealTotalAdd = new WflTrdDealTotalAdd();
            BeanUtil.copyPropertiesIgnoreNull(wflTrdDealTotalAdd, trdFinProductDealTotal);
            wflTrdDealTotalAdd.setPsPayment(entity.getSffx());//收付方向
            wflTrdDealTotalAddMapper.insertSelective(wflTrdDealTotalAdd);
            wflTrdFinProductDealTotalMapper.insertSelective(trdFinProductDealTotal);
            stlPayOrdersDtos.forEach(wflStlPayOrders -> wflStlPayOrdersMapper.insertSelective(wflStlPayOrders));

            //金融产品交易汇总扩展业务流程表
            WflPaTrdFinDealTotal wflPaTrdFinDealTotal = new WflPaTrdFinDealTotal();
            wflPaTrdFinDealTotal.setTotleTradeId(trdFinProductDealTotal.getTotleTradeId()); // 汇总交易编号
            wflPaTrdFinDealTotal.setPaBussType(trdFinProductDealTotal.getTgBusiType());
            wflPaTrdFinDealTotal.setWorkflowId(trdFinProductDealTotal.getWorkflowId()); // 工作流代码
            wflPaTrdFinDealTotal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
            wflPaTrdFinDealTotalMapper.insertSelective(wflPaTrdFinDealTotal);

            if (type != OperationType.REVOKE) {
                // 调用业务组件.【金融产品交易共通.交易产品接口】
                FinProductTradeDto tradeDto = genFinProductTradeDto(entity, OperationType.NEW);
                tradeDto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                baseTradeProductDealCommServiceImpl.execute(tradeDto);
                if (StringUtil.isNotEmpty(entity.getActPortfolioId()) && !StringUtil.equals(entity.getPortfolioId(), entity.getActPortfolioId())) {
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getActPortfolioId()));
                    baseTradeProductDealCommServiceImpl.insertPosition(tradeDto);
                    tradeDto.getBaseTradeProductDealDtos().forEach(
                            baseTradeProductDealDto1 -> baseTradeProductDealDto1.getTrdProductDealAdd().setPortfolioId(entity.getPortfolioId()));
                }
            }
        }

    }

    private List<WflStlPayOrders> gen划款指令流程(OptionDurationDetailDto entity, OperationType type) {
        List<BussTrdTransferOrderDto> stlPayOrdersList = bussTrdTransferOrderImplService.save(entity.getBussTrdTransferOrder());
        List<WflStlPayOrders> stlPayOrdersDtos = new ArrayList<>();
        if (stlPayOrdersList != null) {
            stlPayOrdersList.forEach(stlPayOrders -> {
                WflStlPayOrders dto = new WflStlPayOrders();
                BeanUtil.copyPropertiesIgnoreNull(dto, stlPayOrders);
                dto.setTradeId(entity.getBusiCode());
                dto.setPayType(DictCodeListExt1.平安划款类型_托管划款.getValue());
                dto.setSettleDate(entity.getZfDate());
                dto.setPs(entity.getSffx());//收付方向
                dto.setWorkflowId(entity.getWorkflowId());
                dto.setStatus(DictCodeList.数据状态_新建中.getValue());
                dto.setCreateDept(entity.getCreateDept());
                dto.setUpdateTime(entity.getUpdateTime());
                dto.setCreateUser(entity.getCreateUser());
                dto.setCreateTime(entity.getCreateTime());
                dto.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                if (type == OperationType.REVOKE) {
                    dto.setOrderStatus(DictCodeListExt1.指令状态_撤销.getValue());
                    dto.setTradeId(dto.getTradeId() + "R");
                } else {
                    dto.setOrderStatus(DictCodeListExt1.指令状态_新建.getValue());
                }
                stlPayOrdersDtos.add(dto);
            });
        }
        return stlPayOrdersDtos;
    }

    public List<WflTrdProductDeal> gen交易业务流程(OptionDurationDetailDto entity) {
        List<FinCashPlan> 现金流 = restTemplate.postForList(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdSecLendDuration/findFinCashInfo", entity.getFinprodId(),
                FinCashPlan.class);
        List<WflTrdProductDeal> deals = new ArrayList<>();
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        现金流.forEach(finCashPlan -> {
            if("03".equals(finCashPlan.getCashType())){
                WflTrdProductDeal deal = new WflTrdProductDeal();
                deal.setTradeId(entity.getBusiCode());
                deal.setTradeDate(entity.getXqDate());
                deal.setSettleDate(entity.getZfDate());
                deal.setPayDate(entity.getZfDate());
                if(StringUtil.equals(entity.getXqType(),"DQXQ")){
                    deal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期到期交易.getValue());
                    deal.setPs(DictCodeListExt1.交易方向_到期行权.getValue());
                    deal.setTgBusiType(DictCodeListExt3.平安业务类型_期权到期行权.getValue());
                }else if(StringUtil.equals(entity.getXqType(),"TQXQ")){
                    deal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期提前行权交易.getValue());
                    deal.setPs(DictCodeListExt1.交易方向_提前行权.getValue());
                    deal.setTgBusiType(DictCodeListExt3.平安业务类型_期权提前行权.getValue());
                }else{
                    deal.setBusiType(DictCodeListExt2.共通业务类型_期权存续期放弃行权交易.getValue());
                    deal.setPs(DictCodeListExt1.交易方向_放弃行权.getValue());
                    deal.setTgBusiType(DictCodeListExt3.平安业务类型_期权放弃行权.getValue());
                    deal.setTradeDate(entity.getFqxqDate());
                }
                deal.setFinprodId(entity.getFinprodId());
                deal.setFinprodType(DictCodeListExt1.金融产品类型_投管用_期权.getValue());
                deal.setFinprodType2(entity.getFinprodType2());
                deal.setShareAmt(entity.getAmount());
                deal.setSecManageAcctId(entity.getSecManageAcctId());
                deal.setCcy(entity.getCcy());
                deal.setVdate(entity.getVdate());
                deal.setMdate(entity.getMdate());

                deal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
                deal.setIsCancel(DictCodeList.是否_否.getValue());
                deal.setTradeMarket(entity.getTradeMarket());
                deal.setWorkflowStatus(DictCodeListExt1.交易流程状态_流程中.getValue());
                deal.setWorkflowId(entity.getWorkflowId());
                deal.setTotleTradeId(entity.getBusiCode());
                deal.setCashId(finCashPlan.getCashId());
                // 名义本金
                deal.setPrinAmt(entity.getShareAmt());
                deal.setStatus(DictCodeList.交易方向_到期.getValue());
                deal.setBranch(0L);
                deal.setTradeAmt(entity.getTradeAmt());
                deal.setRemark(entity.getRemark());
                deals.add(deal);
            }
        });

        return deals;
    }


    public Page<SelectOptionDto> getoption(Map<String,Object> map){
        return restTemplate.postForPage(
            ServiceApplication.PERIOD_SERVICE_NAME + "/TrdOptionDuration/getoption", map,
            SelectOptionDto.class);
    }

    /**
     * 根据画面参数获取entity
     * <功能详细描述>
     * @param map 参数
     * @return 返回结果值
     * @see
     * @since 1.0
     */
    public OptionDurationDetailDto getMyEntity(Map<String, Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.PERIOD_SERVICE_NAME + "/TrdOptionDuration/getEntityByMap",
                map, OptionDurationDetailDto.class);
    }

    /**
     * 封装附件信息
     * <一句话功能简述>
     * <功能详细描述>
     * @param busiId
     * @param list
     * @return
     * @see
     * @since 1.0
     */
    public FileCommonDto encapFileCommon(String busiId, List<FileCommonDto> list) {
        FileCommonDto fileCommonDto = new FileCommonDto();
        //文件上传输入参数
        MstFileInputParameter mstFileInputParameter = new MstFileInputParameter();
        List<SysUploadFileInfo> fileList  = new ArrayList<>();
        for(FileCommonDto filecCommonDto:list){
            //根据文件路径转换成对应文件
            SysUploadFileInfo file = new SysUploadFileInfo();
            file.setSeqNo(filecCommonDto.getFileNo());
            fileList.add(file);
        }
        mstFileInputParameter.setFileList(fileList);
        mstFileInputParameter.setBussinessId(busiId);
        fileCommonDto.setMstFileInputParameter(mstFileInputParameter);
        return fileCommonDto;
    }
    @Override
    public void doDelete(String workflowId) {
        baseTradeProductDealCommServiceImpl.delete(workflowId);
        wflTrdFinProductDealTotalMapper.deleteByWorkflowId(workflowId);
        wflTrdDealTotalAddMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealMapper.deleteByWorkflowId(workflowId);
        wflTrdProductDealAddMapper.deleteByWorkflowId(workflowId);
        wflPaTrdFinDealTotalMapper.deleteByWorkflowId(workflowId);
        wflStlPayOrdersMapper.deleteByWorkflowId(workflowId);
    }

    @Override
    public void doBack(OptionDurationDetailDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
        doDelete(entity.getWorkflowId());
    }

    @Override
    public void doRevoke(OptionDurationDetailDto entity) {
        AllTradeCommonUtil.deletePosition(entity.getRevokeReason(), entity.getTotleTradeId());
    }

    /**
     * 获取交易编号
     * @param xqType
     * @return
     */
    public String getTradeId(String xqType){
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        if(StringUtil.equals(xqType,"DQXQ")){
            parameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_期权存续期到期交易));
        }else if(StringUtil.equals(xqType,"TQXQ")){
            parameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_期权存续期提前行权交易));
        }else{
            parameter.setCommonBusinessExtType(DictCodeListExt2.fromDictCodeList(DictCodeListExt2.共通业务类型_期权存续期放弃行权交易));
        }
       return baseGetBusinessIdImpl.buildBussinessCode(parameter);
    }
}