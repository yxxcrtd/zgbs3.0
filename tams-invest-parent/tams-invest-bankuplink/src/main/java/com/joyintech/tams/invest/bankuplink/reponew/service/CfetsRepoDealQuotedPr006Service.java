
package com.joyintech.tams.invest.bankuplink.reponew.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.bankuplink.reponew.dto.CfetsRepoDealQuotedPr006Dto;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.component.base.group.BasePtlSecPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.PtlPositionHoldDto;
import com.joyintech.tams.invest.common.dto.PtlSecPositionFlowDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.etr.EtrPositionHoldDto;
import com.joyintech.tams.invest.common.dto.etr.EtrSecPositionFlowDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.service.impl.etrcash.EtrCommonCashPositionDealService;
import com.joyintech.tools.collectors.JoyinCollectors;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 质押式回购->质押券置换-》service
 *
 * @author nixiukuan
 * @version 1.0 2019年11月9日
 * @since 1.0
 */

@Service
@Transactional(rollbackFor=Exception.class)
public class CfetsRepoDealQuotedPr006Service extends BaseService<CfetsRepoDealQuotedPr006Dto> implements BaseProcessService<CfetsRepoDealQuotedPr006Dto> {


    /**
     * 质押券置换信息表
     */
    @Autowired
    FinPledgeDisplaceMapper finPledgeDisplaceMapper;

    /**
     *质押券置换明细表
     */
    @Autowired
    FinPledgeDisplaceDetailMapper finPledgeDisplaceDetailMapper;

    /**
     *质押券置换结果
     */
    @Autowired
    FinPledgeDisplaceResultMapper finPledgeDisplaceResultMapper;

    /**
     * 交易对手
     */
    @Autowired
    MstCounterPartyMapper mstCounterPartyMapper;

    /**
     * 交易费用明细表
     */
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    /**
     * 金融产品交易表
     */
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    /**
     * 组合持仓流水表
     */
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;

    /**
     * 业务规则生成服务
     */
    @Autowired
    private BaseGetBusinessId baseGetBusinessIdImpl;

    /**
     * 质押品信息
     */
    @Autowired
    private FinDealPledgeMapper finDealPledgeMapper;

    /**
     * 业务编号取得共通
     */
    @Autowired
    private BaseGetBussinessId baseGetBussinessIdimpl;

    /**
     * 组合可用持仓和实际持仓查询更新组件接口
     */
    @Autowired
    private BasePtlSecPositionInfoService basePtlSecPositionInfoServiceImpl;
    /**
     * 附件上传类
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 共通Service
     */
    @Autowired(required = false)
    private EtrCommonCashPositionDealService etrCommonCashPositionDealService;
    /**
     * 工作流中doComplete
     *
     * @param
     */

    @Override
    public void doComplete(CfetsRepoDealQuotedPr006Dto entity, OperationType operationType) {
        repoSecSubstitutionInsert(entity);
    }
    /**
     * 工作流doInsert
     *
     * @param entity        entity
     * @param operationType operationType
     */
    @Override
    public void doInsert(CfetsRepoDealQuotedPr006Dto entity, OperationType operationType) {
        handleBussFlowData(entity);
    }

    /**
     * 工作流doUpdate
     *
     * @param entity  entity
     */
    @Override
    public void doUpdate(CfetsRepoDealQuotedPr006Dto entity) {
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
        handleBussFlowData(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doBack(CfetsRepoDealQuotedPr006Dto entity){
        AllTradeCommonUtil.deletePosition(entity.getCancelRemark(), entity.getTradeId());
    }
    
    /**
     * 根据交易对手id获取交易对手名称
     *
     * @param counterId 画面参数dto
     * @see
     * @since 1.0
     */
    public String selectCounterName(String counterId) {

      return   mstCounterPartyMapper.selectByPrimaryKey(counterId).getCounterName();
    }

    /**
     * 提交时登录业务流程表
     *
     * @param entity 画面参数dto
     * @see
     * @since 1.0
     */
    private void handleBussFlowData(CfetsRepoDealQuotedPr006Dto entity) {

        //附件上传
        if (CollectionUtils.isNotEmpty(entity.getUploadFileList())
                && StringUtil.isNotEmpty(entity.getTradeId())) {
            fileAttachmentProcess.updateBussinessNo(
                    entity.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    entity.getBussinessCode());
        }
        //质押券置换交易流程中时占用换入债券的可用持仓；
        // 1.选入债券列表
        if (entity.getPs().equals(DictCodeList.回购拆借方向_融入.getValue()) || entity.getPs().equals(DictCodeList.回购拆借方向_正回购.getValue())) {
            //换入质押券 以万元为单位
            List<PledgeBondInfoDto> pledgeBondList = entity.getBussRepoSec().getPledgeBondList();
            if(!StringUtil.isEmptyList(pledgeBondList)){
                pledgeBondList.forEach(item ->{item.setPledgePrin(ArithmeticUtil.multiply(item.getPledgePrin(),new BigDecimal(10000)));});
            }
            ptlSecPositionFlowLogic(entity,false, pledgeBondList);

        }
    }
    /**
     * 根据分支号和交易编号查询交易信息明细
     * @param tradeId 交易编号
     * @return 交易信息
     * @see
     * @since 1.0
     */
    public CfetsRepoDealQuotedPr006Dto getTrdRepoTradeDto(String tradeId) {
        Map<String, Object> param = new HashMap<>();
        param.put("tradeId", tradeId);
        param.put("BRANCH_0", CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE));
        return   restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getTrdRepoTradeDto",
                param, CfetsRepoDealQuotedPr006Dto.class);
    }

    /**
     * 写入质押品的组合持仓流水\质押券置换信息及交易费用表等
     *
     * @param entity
     */

    private void repoSecSubstitutionInsert(CfetsRepoDealQuotedPr006Dto entity) {

        //前台券面总额万元为单位
        if(null!=entity.getBussRepoSec()&&!StringUtil.isEmptyList(entity.getBussRepoSec().getPledgeBondList())){
            entity.getBussRepoSec().getPledgeBondList().forEach(item ->{
                item.setPledgePrin(ArithmeticUtil.multiply(item.getPledgePrin(),new BigDecimal(10000)));
            });
        }
        if(null!=entity.getBussResultRepoSec()&&!StringUtil.isEmptyList(entity.getBussResultRepoSec())){
            entity.getBussResultRepoSec().forEach(item ->{
                item.setPledgePrin(ArithmeticUtil.multiply(item.getPledgePrin(),new BigDecimal(10000)));
            });
        }
        if(null!=entity.getOutDataTable()&&!StringUtil.isEmptyList(entity.getOutDataTable())){
            entity.getOutDataTable().forEach(item ->{
                item.setPledgePrin(ArithmeticUtil.multiply(item.getPledgePrin(),new BigDecimal(10000)));
            });
        }
        // 完成后释放换出债券的持仓，换入的债券变更为质押状态；
        //质押券置换信息

        String  bussinessCode=entity.getBussinessCode();
        FinPledgeDisplace finPledgeDisplace=new FinPledgeDisplace();
        finPledgeDisplace.setTradeId(bussinessCode);//业务编号
        finPledgeDisplace.setfTradeId(entity.getTradeId());//画面.原始业务编号
        finPledgeDisplace.setTradeDate(entity.getSubstitutionDate());// 置换日期
        finPledgeDisplace.setRemark(entity.getRemark());// 备注
        finPledgeDisplace.setExchangeFee(entity.getSubstitutionAmt());//置换费用
        finPledgeDisplaceMapper.insert(finPledgeDisplace);
        //if 画面.置换费！=null && 画面.置换费！=0时处理金融产品交易和交易费用明细
        //金融产品交易处理
        if(StringUtil.isNotEmpty(entity.getSubstitutionAmt()) &&entity.getSubstitutionAmt()!=new BigDecimal(0)) {
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            if (StringUtils.isNotBlank(entity.getfTradeId())) {
                trdProductDeal.setfTradeId(entity.getfTradeId());
            }
            trdProductDeal.setTradeId(bussinessCode);//交易编号
            trdProductDeal.setfTradeId(entity.getTradeId());//原交易编号
            trdProductDeal.setPs(DictCodeListExt3.交易方向_费用计提.getValue());//交易方向->费用计提
            trdProductDeal.setTradeMarket(entity.getTradeMarket());//交易市场
            trdProductDeal.setTradeDate(entity.getSubstitutionDate());//交易日=画面.置换日
            trdProductDeal.setFpriceAmt(entity.getSubstitutionAmt());//全价总额=画面.置换费用
            trdProductDeal.setFeeAmtUnpay(entity.getSubstitutionAmt());//未随交易支付费用 = 画面.置换费用
            trdProductDeal.setFeeAmt(entity.getSubstitutionAmt());//总交易费用 = 画面.置换费用
            trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态初始
            trdProductDeal.setIsCancel(DictCodeListExt1.是否_否.getValue());//是否撤销否
            trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());//金融产品类型（估值核算） = 数据字典.金融产品类型（估值核算).投资组合
            trdProductDeal.setBusiType(DictCodeListExt3.业务类型_费用计提交易.getValue());//业务类型 = 数据字典.业务类型.费用计提交易
            trdProductDeal.setRegistOrg(entity.getRegistOrg());// 登记托管机构
            trdProductDeal.setFinprodId(entity.getPortfolioId());//金融产品代码 = 画面.组合代码
            trdProductDeal.setPayDate(entity.getPayDate());
            trdProductDeal.setVdate(entity.getVdate());
            trdProductDeal.setMdate(entity.getMdate());

            trdProductDealMapper.insert(trdProductDeal);
            //交易费用明细处理
            TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
            trdTradeFeeDetail.setTradeId(bussinessCode);//交易编号
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_结算手续费.getValue());//费用类型 数据字典.一次性费用类型.结算手续费
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());//是否随交易支付 = 数据字典.是否.否
            trdTradeFeeDetail.setFeeAmt(entity.getSubstitutionAmt());//费用金额 = 画面.置换费
            trdTradeFeeDetailMapper.insert(trdTradeFeeDetail);
        }
        // LOOP  质押券置换明细.换出list
        entity.getOutDataTable().forEach(item->
        {
            //换出前质押结果记录
            FinPledgeDisplaceResult finPledgeDisplaceResult=new FinPledgeDisplaceResult();
            finPledgeDisplaceResult.setTradeId(finPledgeDisplace.getTradeId());//质押券置换信息.业务编号
            finPledgeDisplaceResult.setFinprodId(item.getFinprodId());//金融产品代码
            finPledgeDisplaceResult.setBranch(entity.getBranch()==null?new BigDecimal(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)):new BigDecimal( entity.getBranch()));//分支序号
            finPledgeDisplaceResult.setResultType(DictCodeListExt3.质押券置换_置换结果_置换前.getValue());
            finPledgeDisplaceResult.setPledgeAmt(item.getPledgePrin());//质押券面总额
            finPledgeDisplaceResult.setFirstNetprice(item.getFirstNetPrice());//首次净价
            finPledgeDisplaceResult.setScale(item.getPledgeRatio().divide(new BigDecimal(100)));//折算比例换出list.折算比例/100
            finPledgeDisplaceResultMapper.insert(finPledgeDisplaceResult);

            //删除之前的质押品
            FinDealPledge finDealPledge=new FinDealPledge();
            finDealPledge.setFinprodId(entity.getTradeId());//原业务编号
            finDealPledge.setPledgeFinprodId(item.getFinprodId());//金融产品代码
            finDealPledge.setBranch(entity.getBranch()==null?Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)):entity.getBranch());//分支序号
            finDealPledgeMapper.deleteByPrimaryKey(finDealPledge);
        });
       // List<PledgeBondInfoDto> outNoCheckBussRepoSecList= entity.getBussResultRepoSec().stream().filter(item -> !entity.getBussRepoSec().getPledgeBondList().contains(item.getBondCode())).collect(toList());

        //换出数据
        List<PledgeBondInfoDto>  outBussRepoSecList=entity.getOutDataTable().stream().filter(item -> (item.getPrin()==null? new BigDecimal(0): item.getPrin()).compareTo(new BigDecimal(0))>0 ).collect(toList());
        outBussRepoSecList.forEach(item->
                {
            //1.质押券置换明细处理
            FinPledgeDisplaceDetail finPledgeDisplaceDetail=new FinPledgeDisplaceDetail();
            finPledgeDisplaceDetail.setSeqNo(UUID.randomUUID().toString().replace("-","")); //流水号
            finPledgeDisplaceDetail.setBranch(entity.getBranch()==null?new BigDecimal(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)):new BigDecimal( entity.getBranch()));//分支序号
            finPledgeDisplaceDetail.setFinprodId(item.getFinprodId());//金融产品代码
            finPledgeDisplaceDetail.setTradeId(finPledgeDisplace.getTradeId());//质押券置换信息.业务编号
            finPledgeDisplaceDetail.setTradeType(DictCodeListExt3.质押券置换_置换方向_换出.getValue());//置换方向(01换出;02换入)
            finPledgeDisplaceDetail.setScale(item.getPledgeRatio());//折算比例换出list.折算比例
            finPledgeDisplaceDetail.setPledgeDisplaceAmt(item.getPrin());//换出面额
            finPledgeDisplaceDetail.setStatus(DictCodeListExt1.有效状态_有效.getValue());
            finPledgeDisplaceDetailMapper.insert(finPledgeDisplaceDetail);

            //2.组合持仓流水处理

            if(entity.getPs().equals(DictCodeList.回购拆借方向_融入.getValue())||entity.getPs().equals(DictCodeList.回购拆借方向_正回购.getValue())) {
                //换出时按照FVTPL-》FVOCI-》AC 冲减
                ptlSecPositionFlowLogic(entity,true,outBussRepoSecList );
            }

        });
        //LOOP  质押券置换结果list
        List<PledgeBondInfoDto> resultList = new ArrayList<PledgeBondInfoDto>();
        Map<String,List<PledgeBondInfoDto>> resultMap = entity.getBussResultRepoSec().stream().collect(JoyinCollectors.groupingBy(
                x -> x.getFinprodId()));
        for (String key: resultMap.keySet()){
            if (resultMap.get(key) != null){
                BigDecimal plgNum = BigDecimal.ZERO;
                PledgeBondInfoDto dto = new PledgeBondInfoDto();
                BeanUtils.copyProperties(resultMap.get(key).get(0),dto);
                for(int i = 0;i< resultMap.get(key).size();i++ ){
                    plgNum = plgNum.add(resultMap.get(key).get(i).getPledgePrin());
                }
                dto.setPledgePrin(plgNum);
                resultList.add(dto);
            }
        }
        resultList.forEach(item->
        {
            //1.质押券置换结果处理
            FinPledgeDisplaceResult finPledgeDisplaceResult=new FinPledgeDisplaceResult();
            finPledgeDisplaceResult.setTradeId(finPledgeDisplace.getTradeId());//质押券置换信息.业务编号
            finPledgeDisplaceResult.setFinprodId(item.getFinprodId());//金融产品代码
            finPledgeDisplaceResult.setBranch(entity.getBranch()==null?new BigDecimal(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)):new BigDecimal( entity.getBranch()));//分支序号
            finPledgeDisplaceResult.setResultType(DictCodeListExt3.质押券置换_置换结果_置换后.getValue());
            finPledgeDisplaceResult.setPledgeAmt(item.getPledgePrin());//质押券面总额
            finPledgeDisplaceResult.setFirstNetprice(item.getFirstNetPrice());//首次净价
            finPledgeDisplaceResult.setScale(item.getPledgeRatio());//折算比例换出list.折算比例
            finPledgeDisplaceResultMapper.insert(finPledgeDisplaceResult);
            //2.回购质押品处理
            FinDealPledge finDealPledge=new FinDealPledge();
            finDealPledge.setFinprodId(entity.getTradeId());//原业务编号
            finDealPledge.setPledgeFinprodId(item.getFinprodId());//金融产品代码
            finDealPledge.setBranch(entity.getBranch()==null?Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)):entity.getBranch());//分支序号
            finDealPledge.setPledgeAmt(item.getPledgePrin());//质押面值=质押券面总额
            finDealPledge.setPledgeNumber(item.getPledgePrin().divide(new BigDecimal(100)));//质押数量=list.质押券面总额/100
            finDealPledge.setPledgeRatio(item.getPledgeRatio().divide(new BigDecimal(100)));//质押率= list.折算比例/100
            finDealPledge.setPledgeAmt(finDealPledge.getPledgeAmt().multiply(finDealPledge.getPledgeRatio()));//质押金额= 质押券置换结果.质押面值*质押券置换结果.质押率
            finDealPledge.setPledgeFaceValue(new BigDecimal(100));//面值
            if(StringUtil.isNotEmpty(finDealPledge.getPledgeAmt())&&finDealPledge.getPledgeAmt().compareTo(BigDecimal.ZERO)!=0){
                finDealPledgeMapper.insert(finDealPledge);
            }
        });

    }

    /**
     * 根据参数删除组合持仓流水
     *
     * @param map 参数列表
     * @return Boolean
     * @see
     * @since 1.0
     */
    public Boolean deletePtlSecPositionFlowByParm(Map<String,Object> map) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdOutRightRepoTrade/deletePtlSecPositionFlowByParm",
                map, Boolean.class);
    }



    /**
     * 写入质押品的组合持仓流水
     *
     * @param input    画面参数Dto
     * @param isNegate 是否冲减
     */
    private boolean ptlSecPositionFlowLogic(CfetsRepoDealQuotedPr006Dto input, boolean isNegate,List<PledgeBondInfoDto> list) {
        List<PtlSecPositionFlowDto> lstPtlSecPositionFlowDto = new ArrayList<>();

        // List<组合持仓流水DTO>(若上记的List<回购质押品DTO>为空，则此为空，反之循环构造DTO)
        // 通过上记输出的List<输出的质押品DTO>，循环该List构造组合持仓流水DTO
        if (!StringUtil.isEmptyList(list)
                && (DictCodeList.回购拆借方向_正回购.getValue().equals(input.getPs()) ||DictCodeList.回购拆借方向_融入.getValue().equals(input.getPs()))) {
            // 可质押券信息（含投资目的）
            Map<String, List<PledgeBondInfoDto>> avaMap =new HashMap<>();
            // 冲减
            if (isNegate) {
                avaMap= this.selectAvaPledgeWithInvAim(input,true)
                        .stream().collect(JoyinCollectors.groupingBy(x -> x.getFinprodId()));
                avaMap.entrySet().stream().forEach(entry -> {
                    Collections.sort(entry.getValue(), (o1, o2) -> o2.getInvAimSort().compareTo(o1.getInvAimSort()));
                });
            }
            else
            {
                avaMap= this.selectAvaPledgeWithInvAim(input,false)
                        .stream().collect(JoyinCollectors.groupingBy(x -> x.getBondCode()));
                avaMap.entrySet().stream().forEach(entry -> {
                    Collections.sort(entry.getValue(), (o1, o2) -> o1.getInvAimSort().compareTo(o2.getInvAimSort()));
                });
            }

            PtlSecPositionFlowDto resultDto;
            for (PledgeBondInfoDto pledgeBond : list) {
                // 待分配投资目的的质押券面总额
                BigDecimal pledgePrin = pledgeBond.getPledgePrin();
                if(isNegate)
                {
                    pledgePrin=pledgeBond.getPrin();
                    pledgeBond.setBondCode(pledgeBond.getFinprodId());
                }
                List<PledgeBondInfoDto> pledgeBondInfoDtos = avaMap.get(pledgeBond.getBondCode());
                if(null!=pledgeBondInfoDtos){
                    for (PledgeBondInfoDto avaBond : pledgeBondInfoDtos) {
                        resultDto = new PtlSecPositionFlowDto();

                        // 当前投资目的可分配券面总额
                        BigDecimal avaPrin = avaBond.getAvaPledgePrin();
                        if(avaPrin.compareTo(new BigDecimal(0))<=0)
                        {
                            continue;
                        }

                        // 当前投资目的决定分配的券面总额
                        BigDecimal prin = pledgePrin.compareTo(avaPrin) > 0 ? avaPrin : pledgePrin;

                        // 组合代码 = 画面.组合代码
                        resultDto.setPortfolioId(input.getPortfolioId());
                        // 证券管理账户/通道代码 = nvl（nvl（金融产品交易Entity.证券托管账户，金融产品交易Entity.通道代码），999999）
    //                        resultDto.setSecAcctId(StringUtil.isEmpty(input.getTrdProductDeal().getSecManageAcctId()) ? "999999" : input.getTrdProductDeal().getSecManageAcctId());
                        resultDto.setSecAcctId(StringUtil.isEmpty(input.getSecAcctId()) ?
                                (StringUtil.isEmpty(input.getChlId()) ? "999999": input.getChlId()) :
                                input.getSecAcctId());//通道

                            // 金融产品代码 = 输出质押品DTO.金融产品代码
                            resultDto.setFinprodId(avaBond.getFinprodId());

                        // 分支序号 = 系统固定参数，默认为0
                        resultDto.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                        // 持仓类型
                        resultDto.setHodingType(DictCodeList.持仓类型_质押.getValue());
                        // 日期 = 置换日期
                        resultDto.setCdate(input.getSubstitutionDate());
                        // 数量 = 输出质押品DTO.质押数量
                        if(isNegate) {
                            resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4).negate());
                        }
                        else
                        {
                            resultDto.setAmount(ArithmeticUtil.divide(prin, new BigDecimal("100"), 4));
                        }
                        // 币种 = 金融产品交易Entity.币种
                        resultDto.setCcy(input.getCcy());
                        // 交易编号 = 金融产品交易Entity.交易编号
                        resultDto.setTradeId(input.getTradeId());
                        // 投资目的 = 按“摊余成本类-公允价值变动计入其他综合收益-公允价值变动计入当期损益”的顺序依次冻结，削减对应投资目的债券可用持仓
                        resultDto.setInvAim(StringUtil.isEmpty(avaBond.getInvAim()) ? "999999" : avaBond.getInvAim());

                        if (avaPrin.compareTo(BigDecimal.ZERO) > 0) {
                            if (StringUtil.isNotEmpty(input.getActPortfolioId()) && !StringUtil.equals(input.getActPortfolioId(), input.getPortfolioId())) {
                                PtlSecPositionFlowDto subResultDto = new PtlSecPositionFlowDto();
                                BeanUtil.copyPropertiesIgnoreNull(subResultDto, resultDto);
                                subResultDto.setPortfolioId(input.getActPortfolioId());
                                lstPtlSecPositionFlowDto.add(subResultDto);
                            }
                            lstPtlSecPositionFlowDto.add(resultDto);
                        }
                        // 分配完毕后,停止可质押券信息（含投资目的）的循环
                        pledgePrin = pledgePrin.subtract(prin);
                        if (pledgePrin.compareTo(BigDecimal.ZERO) <= 0) {
                            break;
                        }
                    }
                }
            }

        }
        //质押券置换指令持仓
        List<EtrSecPositionFlowDto> etrSecPositionFlows = new ArrayList<>();
        for(PtlSecPositionFlowDto it:lstPtlSecPositionFlowDto){
//            if(StringUtil.isEmpty(input.getEntrustId())){
//                //该交易无指令，即无指令委托持仓
//                continue;
//            }
            EtrSecPositionFlowDto dto=new EtrSecPositionFlowDto();
            dto.setSeqno(IDGenerator.generate(32));
            dto.setPortfolioId(it.getPortfolioId());
            dto.setSecAcctId(it.getSecAcctId());
            dto.setFinprodId(it.getFinprodId());
            dto.setBranch(it.getBranch());
            dto.setInvAim(it.getInvAim());
            dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
            dto.setCdate(it.getCdate());
            dto.setCcy(it.getCcy());
            dto.setAmount(it.getAmount().negate());
            dto.setpFinprodId(it.getpFinprodId());
            dto.setTradeId(it.getTradeId());
            dto.setCreateUser(it.getCreateUser());
            dto.setCreateDept(it.getCreateDept());
            dto.setCreateTime(it.getCreateTime());
            dto.setUpdateUser(it.getUpdateUser());
            dto.setUpdateTime(it.getUpdateTime());
            etrSecPositionFlows.add(dto);
        }
        if(null!=etrSecPositionFlows && etrSecPositionFlows.size()>0){
            // 处理结果
            EtrPositionHoldDto etrPositionHoldDto = new EtrPositionHoldDto();
            etrPositionHoldDto.setPtlSecPositionFlows(etrSecPositionFlows);
            etrCommonCashPositionDealService.持久化流水Dto(etrPositionHoldDto);

        }
        if (StringUtil.isEmptyList(lstPtlSecPositionFlowDto)) {
            return true;
        } else {
            PtlPositionHoldDto ptlPositionHoldDto = new PtlPositionHoldDto();
            ptlPositionHoldDto.setPtlSecPositionFlows(lstPtlSecPositionFlowDto);
            return restTemplate.postForObject(ServiceApplication.PORTFOLIO_SERVICE_NAME + "/common/ptlposition/insertPosition",
                    ptlPositionHoldDto, Boolean.class);
        }
    }

    /**
     * 根据画面条件检索可质押券信息（含投资目的）
     *
     * @param input 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeWithInvAim(CfetsRepoDealQuotedPr006Dto input, boolean isNegate){
        Map<String, Object> param = new HashMap<>();
        param.put("portfolioId", input.getPortfolioId());
        param.put("ps", input.getPs());
        param.put("isWithInvAim", true);
        param.put("INV_AIM_AC", DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue());
        param.put("INV_AIM_FVOCI", DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue());
        param.put("INV_AIM_FVTPL", DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());
        param.put("secAcctId", input.getSecAcctId());
        param.put("chlId", input.getChlId());
        if(isNegate) {
        //换出逻辑处理
            return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectOutPledgeBondDetailList",
                    param, PledgeBondInfoDto.class);
        }
        else {
            return restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectAvaPledgeBondDetailList",
                    param, PledgeBondInfoDto.class);
        }
    }

    /**
     * 获取换出质押券信息
     * @param parm 参数
     * @return 质押券信息
     */
    public Page<PledgeBondInfoDto> findOutPage(Map<String, Object> parm) {
        return restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getOutPledgeInfo",
                parm, PledgeBondInfoDto.class);
    }

    /**
     * 根据工作流代码查询交易编号
     *
     * @param workflowId 工作流代码
     * @return 交易编号
     * @see
     * @since 1.0
     */
    public String selectTradeIdByWorkflowId(String workflowId) {
        return restTemplate.postForObject(
                ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/selectTradeIdByWorkflowId/"
                        + workflowId,
                null, String.class);
    }

    /**
     * add by yangwenjun 20201123 根据业务编号查询合作资产
     * @param tradeId
     * @return
     */
    public String getChlId(String tradeId) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/TrdRepoTrade/getChlId", tradeId, String.class);
    }

    /**
     * 查询本方交易员列表
     *
     * @param param 入参
     * @return 本方交易员列表
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryMySideTraderPage(Map<String, Object> param) {
        return restTemplate.postForPage(ServiceApplication.BANKUPLINK_SERVICE_NAME + "/CfetsRepoDealQuotedPr001/queryMySideTraderPage",
                param, SelectOptionDto.class);
    }
}
