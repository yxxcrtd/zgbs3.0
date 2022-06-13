package com.joyintech.interf.bankname.cfetsup.secrepoquote.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.interf.bankname.cfetsup.constant.FxDictCodeList;
import com.joyintech.interf.bankname.cfetsup.outbound.CallReturn;
import com.joyintech.interf.bankname.cfetsup.outbound.OutBoundData;
import com.joyintech.interf.bankname.cfetsup.outbound.dto.ICfetsDto;
import com.joyintech.interf.bankname.cfetsup.outbound.service.CfetsRepoClientService;
import com.joyintech.interf.bankname.cfetsup.outbound.service.ClientService;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dao.FxSecrepoQuoteDetailMapper;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dao.FxSecrepoQuoteMapper;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dto.EtrRepoDto;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dto.FxSecrepoQuote002Dto;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dto.FxSecrepoQuote003Dto;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.dto.FxSecrepoQuoteDetail001Dto;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.entity.FxSecrepoQuote;
import com.joyintech.interf.bankname.cfetsup.secrepoquote.entity.FxSecrepoQuoteDetail;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.component.business.common.service.BussCommonMapperDelegateService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.avalon.framework.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 回购对话报价业务层
 *
 * @author zcf
 * @version 1.0
 * @date 2020-01-08
 */
@Service
public class FxSecrepoQuote002Service extends BaseService<FxSecrepoQuote002Dto> implements BaseProcessService<FxSecrepoQuote002Dto>{
    /** 日志 */
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FxSecrepoQuoteMapper fxSecrepoQuoteMapper;
    @Autowired
    private FxSecrepoQuoteDetailMapper fxSecrepoQuoteDetailMapper;
    @Autowired(required = false)
    private BussCommonMapperDelegateService bussCommonMapperDelegateService;
    @Autowired(required = false)
    private DateCalService dateCalService;
    @Autowired
    private CfetsRepoClientService fetsRepoClientService;
    @Autowired
    private ClientService clientService;


    /**
     * 内部报价编号前缀
     */
    private static final String SEC_REPO_QUOTE_ID = "QTSECREPO";

    @Override
    public void doInsert(FxSecrepoQuote002Dto dto, OperationType operationType) {
        if(operationType == OperationType.NEW){
            FxSecrepoQuote entity = packEntity(dto);
            if(dto.getOperateType2().equalsIgnoreCase("0")){
                //新增
               // entity.setSeqno(IDGenerator.generate());
                entity.setBusinessType("add");
                //生成内部报价编号
                String nbQuoteId = genNbQuoteId();
                //内部报价编号
                 entity.setNbquoteid(nbQuoteId);
                 //报价编号
                entity.setQuoteid(nbQuoteId);
                entity.setManual("0");
                entity.setTradestatus("adding");
                entity.setSendstatus(null);
                entity.setRate(entity.getRate().divide(new BigDecimal(100)));
                entity.setHomecstpid("120041344180000105001");
                entity.setCountercstpid("124001151090000104001");
                //entity.setXxbl(entity.getXxbl().divide(new BigDecimal(100)));
                //修改报价,插入修改报价后记录
                fxSecrepoQuoteMapper.insertSelective(entity);
                fxSecrepoQuoteMapper.insertSelectiveHist(entity);
                //添加明细
                insertkSecrepoQuoteDetail(dto,entity.getSeqno());
            } else if(dto.getOperateType2().equalsIgnoreCase("3")){
                // 回复报价
                String replyType = dto.getReplyType();
                if(null != replyType && !"".equals(replyType)){
                    if ("0".equals(replyType)){
                        //成交
                        entity.setBusinessType("confirm");
                        entity.setTradestatus("dealing");
                        entity.setSendstatus(null);
                        entity.setLastmodifierid(LoginUserUtil.getUserName());
                        entity.setLastmodifiedtime(new Date());
                        edit(entity,dto);
                    }else if("1".equals(replyType)){
                        //终止
                        entity.setBusinessType("refuse");
                        entity.setLastmodifierid(LoginUserUtil.getUserName());
                        entity.setLastmodifiedtime(new Date());
                        entity.setTradestatus("terminating");
                        entity.setSendstatus(null);
                        edit(entity,dto);
                    }
                }else{
                    logger.error("回复报价的回复类型为："+replyType);
                }
            }else if(dto.getOperateType2().equalsIgnoreCase("4")){
                //修改报价
                if("chat".equals(entity.getBusinessType())){
                    entity.setBusinessType("chat");
                }else{
                    entity.setBusinessType("update");
                }
                //修改报价时显示状态为修改中
                entity.setTradestatus("updating");
                entity.setSendstatus(null);
                //修改人
                entity.setLastmodifierid(LoginUserUtil.getUserName());
                //修改时间
                entity.setLastmodifiedtime(new Date());
                edit(entity,dto);
            }else if(dto.getOperateType2().equalsIgnoreCase("5")){
                //交谈报价
                entity.setBusinessType("chat");
                entity.setTradestatus("talking");
                entity.setSendstatus(null);
                entity.setLastmodifiedtime(new Date());
                entity.setLastmodifierid(LoginUserUtil.getUserName());
                edit(entity,dto);

            }else if(dto.getOperateType2().equalsIgnoreCase("6")){
                //撤销报价
                if("0".equals(entity.getHomerole())  && "success".equals(entity.getSendstatus())
                        && ("updated".equals(entity.getTradestatus())  || "added".equals(entity.getTradestatus()) || "talked".equals(entity.getTradestatus()))) {
                    entity.setBusinessType("cancel");
                }
                if("1".equals(entity.getHomerole())&& "chat".equals(entity.getBusinessType())
                        && ("talked".equals(entity.getTradestatus()) || "updated".equals(entity.getTradestatus()))){
                    entity.setBusinessType("chat");
                }
                //撤回中
                entity.setTradestatus("revoking");
                entity.setSendstatus(null);
                entity.setLastmodifiedtime(new Date());
                entity.setLastmodifierid(LoginUserUtil.getUserName());
                edit(entity,dto);
            }else if(dto.getOperateType2().equalsIgnoreCase("7")){
                //复制
                entity.setBusinessType("add");
                entity.setTradestatus("adding");
                entity.setSendstatus(null);
                entity.setSeqno(IDGenerator.generate());
                entity.setManual("0");
                entity.setRate(entity.getRate().divide(new BigDecimal(100)));
                entity.setXxbl(entity.getXxbl().divide(new BigDecimal(100)));
                //修改报价,插入修改报价后记录
                fxSecrepoQuoteMapper.insertSelective(entity);
                //"历史表插入修改报价后记录
                fxSecrepoQuoteMapper.insertSelectiveHist(entity);
                //添加明细
                insertkSecrepoQuoteDetail(dto,entity.getSeqno());

            }
        }
    }

    @Override
    public void doComplete(FxSecrepoQuote002Dto dto, OperationType operationType) {
        if(operationType == OperationType.NEW){
            FxSecrepoQuote entity = new FxSecrepoQuote();
            BeanUtil.copyPropertiesIgnoreNull(entity,dto);
            if(dto.getOperateType2().equalsIgnoreCase("0")){
                //新增,修改为有效状态
                updateEffectFlag(entity);
                //流程生效，发送报价
                CallReturn callReturn= clientService.send(entity,"add","add");
                //返回值
                //return resultMessage(callReturn);
            } else if(dto.getOperateType2().equalsIgnoreCase("3")){
                // 回复报价
                String replyType = dto.getReplyType();
                if(null != replyType && !"".equals(replyType)){
                        if ("0".equals(replyType)){
                            //成交
                            updateEffectFlag(entity);
                            //流程生效，发送报价
                            CallReturn callReturn= clientService.send(entity,"confirm","confirm");
                            //返回值
                            //return resultMessage(callReturn);
                        }else if("1".equals(replyType)){
                            //终止
                            updateEffectFlag(entity);
                            //流程生效，发送报价
                            CallReturn callReturn= clientService.send(entity,"refuse","refuse");
                            //返回值
                            //return resultMessage(callReturn);
                        }
                }else{
                    logger.error("回复报价的回复类型为："+replyType);
                }
            }else if(dto.getOperateType2().equalsIgnoreCase("4")){
                //修改报价
                updateEffectFlag(entity);
                //流程生效，发送报价
                CallReturn callReturn=null;
                if("chat".equals(entity.getBusinessType())){
                    callReturn= clientService.send(entity,"chat","chat");
                }else{
                    callReturn= clientService.send(entity,"update","update");
                }
                //返回值
                //return resultMessage(callReturn);
            }else if(dto.getOperateType2().equalsIgnoreCase("5")){
                //交谈报价
                updateEffectFlag(entity);
                //流程生效，发送报价
                CallReturn callReturn= clientService.send(entity,"chat","chat");
                //返回值
                //return resultMessage(callReturn);
            }else if(dto.getOperateType2().equalsIgnoreCase("6")){
                //撤销报价
                updateEffectFlag(entity);
                CallReturn callReturn= clientService.send(entity,"cancel","cancel");
                //返回值
                //return resultMessage(callReturn);
            }
        }else if(operationType == OperationType.MODIFY){
            //编辑
            FxSecrepoQuote entity = packEntity(dto);
            entity.setLastmodifiedtime(new Date());
            entity.setLastmodifierid(LoginUserUtil.getUserName());
            edit(entity,dto);
        }else if(operationType == OperationType.REVOKE || operationType == OperationType.DELETE){
            FxSecrepoQuote entity = packEntity(dto);
            String tradestatus = entity.getTradestatus();
            String homerole = entity.getHomerole();
                if(null != homerole && !"".equals(homerole) && "0".equals(homerole)
                        && null != tradestatus && !"".equals(tradestatus) && "adding".equals(tradestatus) ){
                    //删除该笔报价
                    fxSecrepoQuoteMapper.deleteByPrimaryKey(entity.getSeqno());
                    //删除报价明细
                    fxSecrepoQuoteDetailMapper.deleteByPseqNo(entity.getSeqno());
                }else{
                    List<FxSecrepoQuote> vo =  fxSecrepoQuoteMapper.selectByPrimaryNbquoteid(entity.getNbquoteid());
                    if(null == vo || vo.size()<= 0){
                        logger.error("删除报价时没有查询到历史报价，内部报价编号："+entity.getNbquoteid());
                    }else{
                        //通过历史报价的流水号查询历史明细记录
                        FxSecrepoQuote fxSecrepoQuote = vo.get(0);
                        List<FxSecrepoQuoteDetail> details =  fxSecrepoQuoteDetailMapper.selectHistByPseqNo(fxSecrepoQuote.getSeqno());

                        if(null == details || details.size()<=0 ){
                            logger.error("删除报价时没有查询到历史明细报价，主表报价流水号："+fxSecrepoQuote.getSeqno());
                        }else {
                            //删除该笔报价
                            fxSecrepoQuoteMapper.deleteByPrimaryKey(entity.getSeqno());
                            //插入报价
                            fxSecrepoQuoteMapper.insertSelective(fxSecrepoQuote);
                            //删除报价明细信息
                            fxSecrepoQuoteDetailMapper.deleteByPseqNo(entity.getSeqno());
                            //插入报价明细信息
                            for (FxSecrepoQuoteDetail dvo : details) {
                                fxSecrepoQuoteDetailMapper.insertSelective(dvo);
                            }
                        }
                    }
            }
        }
    }

    public FxSecrepoQuote packEntity(FxSecrepoQuote002Dto dto){
        FxSecrepoQuote entity = new FxSecrepoQuote();
        if(StringUtil.isNotEmpty(dto)){
            BeanUtil.copyPropertiesIgnoreNull(entity,dto);
            //产品代码
            entity.setAccountid(dto.getPortfolioId());
            //内部报价编号
            // entity.setNbquoteid(genNbQuoteId());
            //报价日期
            entity.setQuotedate(dto.getDealdate());
            entity.setPseqno(dto.getSeqno());
            //业务发生时间
            //entity.setTransactime(new Date());
            //报价有效时间
            //entity.setEffecttime();
            //有效状态
            entity.setEffectflag(FxDictCodeList.有效状态_新建.getItemCode());
            if(null == entity.getBusinessType() || "".equals(entity.getBusinessType())){
                //新建人员
                entity.setCreatorid(LoginUserUtil.getUserName());
                //新建时间
                entity.setCreatedtime(new Date());
            }

        }
        return entity;
    }
    /**
     * 质押式回购报价明细
     * @param dto
     * @return
     */
    public List<FxSecrepoQuoteDetail> insertkSecrepoQuoteDetail(FxSecrepoQuote002Dto dto,String seqno){
        List<FxSecrepoQuoteDetail> detailList = new ArrayList<>();
        List<FxSecrepoQuote003Dto> finprodList = dto.getFinprodList();
        if(!StringUtil.isEmptyList(finprodList)){
            finprodList.forEach(item ->{
                FxSecrepoQuoteDetail detail = new FxSecrepoQuoteDetail();
                detail.setSeqno(IDGenerator.generate());
                //报价流水号
                detail.setPseqno(seqno);
                //报价编号
                detail.setQuoteid(dto.getQuoteid());
                //债券代码
                detail.setSeccode(item.getFinprodId());
                //券面
                detail.setFacetotalamt(item.getDscCostAmt());
                //折算比例
                detail.setConvrate(item.getConvrate());
                //成交金额
                detail.setDealamt(item.getTrustTotalAmt());
                //有效状态
                detail.setEffectflag(FxDictCodeList.有效状态_有效.getItemCode());
                //新建人员
                detail.setCreatorid(LoginUserUtil.getUserName());
                //新建时间
                detail.setCreatedtime(new Date());
                //修改人
                detail.setLastmodifierid(LoginUserUtil.getUserName());
                //修改时间
                detail.setLastmodifiedtime(new Date());
                List<FxSecrepoQuoteDetail001Dto > dtos = fxSecrepoQuoteDetailMapper.selectFinprodInfoByFinprodId(item.getFinprodId(),dto.getDealdate());
                if(!StringUtil.isEmptyList(dtos)){
                    FxSecrepoQuoteDetail001Dto secrepoQuoteDetaildto = dtos.get(0);
                    BeanUtil.copyPropertiesIgnoreNull(detail,secrepoQuoteDetaildto);
                    //计算剩余期限 计算公式：有行权日：未来最近行权日（回售日或赎回日）-交易日	;无行权日：到期日-交易日
                    int syqx = 0;
                    if(secrepoQuoteDetaildto.getExerciseDate() != null){
                        syqx = DateUtil.compareDate(secrepoQuoteDetaildto.getExerciseDate(),dto.getDealdate());
                    }else{
                        syqx = DateUtil.compareDate(secrepoQuoteDetaildto.getMdate(),dto.getDealdate());
                    }
                    detail.setSyqx(new BigDecimal(syqx));
                }
                fxSecrepoQuoteDetailMapper.insertSelective(detail);
                fxSecrepoQuoteDetailMapper.insertSelectiveHist(detail);
                detailList.add(detail);
            });
        }
        return detailList;
    }

    /**
     * 作废
     * @param workflowId
     */
    @Override
    public void doDelete(String workflowId) {
        FxSecrepoQuote002Dto dto = this.getBussinessEntityByWorkflowId(workflowId);
        if(StringUtil.isNotEmpty(dto)){
            if(dto.getOperateType1().equalsIgnoreCase("0")){
                //新增
                fxSecrepoQuoteMapper.deleteByPrimaryKey(dto.getSeqno());
                fxSecrepoQuoteDetailMapper.deleteByPseqNo(dto.getSeqno());
            }
            // Sonar Fix modify by huwei 220607
            /*else if(dto.getOperateType2().equalsIgnoreCase("3")){
                // 回复报价
            }else if(dto.getOperateType2().equalsIgnoreCase("4")){
                // 修改报价
            }else if(dto.getOperateType2().equalsIgnoreCase("5")){
                // 交谈报价
            }else if(dto.getOperateType2().equalsIgnoreCase("5")){
                // 撤销报价
            }*/

        }
    }

    /**
     * 根据委托代码查询委托信息
     * @param entrustId
     */
    public EtrRepoDto selectByEntrustId(String entrustId){
        return restTemplate.getForObject(ServiceApplication.ENTRUST_SERVICE_NAME
                        + "/EtrRepo/selectByKey?entrustId=" + entrustId,
                EtrRepoDto.class);
    }

    /**
     * 查询质押券信息
     * @param param
     * @return
     */
    public BussRepoSecDto selectBussRepoSec(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/BussRepoSec/selectBussRepoSec",
                param, BussRepoSecDto.class);
    }
    /**
     * 查询组合T日可以现金
     * @param param
     * @return
     */
    public BigDecimal selectUsableCashByPortfolio(Map param){
        String tradeDateStr = (String)param.get("tradeDate");
        Date tradeDate = DateUtil.parse(tradeDateStr);
        String dateId = (String)param.get("calendarId");
        String finprodId = (String)param.get("finprodId");
        if (StringUtil.isBlank(dateId) && StringUtil.isNotBlank(finprodId)) {
            dateId = bussCommonMapperDelegateService.selectBySinglePrimaryKey(FinProductMapper.class,
                    param.get("finprodId").toString(), FinProduct.class).getCalendarId();
        }
        List<String> holidayList = dateCalService.getDealCalendarHoliday(tradeDate, DateUtil.addDay(tradeDate, 15), dateId);
        param.put("tradeDateNext", DateUtil.format(dateCalService.calculationTradingDay(tradeDate, 1, holidayList, "1")));
        return null;
    }

    /**
     * 查询质押式回购报价信息
     * @param seqno
     * @return
     */
    public FxSecrepoQuote002Dto selectSecRepoQuoteInfo(String seqno){
        FxSecrepoQuote002Dto dto = new FxSecrepoQuote002Dto();
        FxSecrepoQuote entity = fxSecrepoQuoteMapper.selectByPrimaryKey(seqno);
        List<FxSecrepoQuoteDetail> detailEntity = fxSecrepoQuoteDetailMapper.selectByPseqNo(seqno);
        BeanUtil.copyPropertiesIgnoreNull(dto,entity);
        dto.setPortfolioId(entity.getAccountid());
        if(StringUtil.isNotEmpty(detailEntity)){
            List<FxSecrepoQuote003Dto> bondList = new ArrayList<>();
            detailEntity.forEach(item ->{
                FxSecrepoQuote003Dto dto3 = new FxSecrepoQuote003Dto();
                dto3.setFinprodId(item.getSeccode());
                dto3.setConvrate(item.getConvrate());
                dto3.setPortfolioId(entity.getAccountid());
                dto3.setDscCostAmt(item.getFacetotalamt());
                dto3.setTrustTotalAmt(entity.getDealamt());
                List<FxSecrepoQuoteDetail001Dto> detail001Dtos = fxSecrepoQuoteDetailMapper.selectFinprodInfoByFinprodId(item.getSeccode(),entity.getDealdate());
                if(!StringUtil.isEmptyList(detail001Dtos)){
                    dto3.setMdate(detail001Dtos.get(0).getMdate());
                    //dto3.setFinprodAbbr(bondList.get(0).get);
                    //dto3.setRepoAmt();
                }
                //dto3.
                //dto3.setNetprice();
                bondList.add(dto3);
            });
            dto.setFinprodList(bondList);
        }
        return dto;
    }

    /**
     * 根据委托信息回去报价信息
     * @param dto
     * @return
     */
    public FxSecrepoQuote002Dto getRepoQuoteInfo(EtrRepoDto dto){
        FxSecrepoQuote002Dto entity = new FxSecrepoQuote002Dto();
        //组合代码
        entity.setPortfolioId(dto.getPortfolioId());
        entity.setPortfolioName(dto.getPortfolioId());
        //委托代码
        entity.setTrustid(dto.getEntrustId());
        //交易市场
        entity.setJycs(dto.getTradeMarket());
        //结算币种
        entity.setCcy(dto.getCcy());
        //债券币种
        entity.setBondccy(dto.getCcy());
        //交易方向
        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equalsIgnoreCase("04")){
                entity.setPs(FxDictCodeList.交易方向_逆回购.getItemCode());
            }else if(dto.getPs().equalsIgnoreCase("03")){
                entity.setPs(FxDictCodeList.交易方向_正回购.getItemCode());
            }
        }
        //回购利率
        entity.setRate(dto.getPrice());
        //期限品种
       entity.setDealvar(dto.getTermType());
        //回购期限
       entity.setTerm(dto.getRepoTerm());
        //交易日历
       entity.setCalendarId(dto.getCalendarId());
       //清算速度
        if(dto.getSettleSpeed()!=null){
            if(dto.getSettleSpeed()==0){
                entity.setSettlespeed(DictCodeList.清算速度_T_0.getValue());
            }else if(dto.getSettleSpeed()==1){
                entity.setSettlespeed(DictCodeList.清算速度_T_1.getValue());
            }
        }
       //委托金额
       entity.setDealamt(dto.getEntrustAmt());
        //entity.set
        //质押券信息
        Map param = new HashMap();
        param.put("entrustId", dto.getEntrustId());
        param.put("portfolioId", dto.getPortfolioId());
        param.put("ps", dto.getPs());
        param.put("bizCode", "");
        param.put("SEC_ACCT_ID",dto.getSecuritiesCust() != null? dto.getSecuritiesCust(): dto.getChlId());
        BussRepoSecDto bussRepoSec = selectBussRepoSec(param);
        if(StringUtil.isNotEmpty(bussRepoSec)){
            //汇总券面总额
            entity.setPrinamt(bussRepoSec.getTotalPrinAmt());
            List<PledgeBondInfoDto> pledgeBondList = bussRepoSec.getPledgeBondList();
            List<FxSecrepoQuote003Dto> bondList = new ArrayList<>();
            if(!StringUtil.isEmptyList(pledgeBondList)){
                pledgeBondList.forEach(item ->{
                    FxSecrepoQuote003Dto dto1 = new FxSecrepoQuote003Dto();
                    //债券代码
                    dto1.setFinprodId(item.getBondCode());
                    //债券简称
                    dto1.setFinprodAbbr(item.getBondAbbrName());
                    //质押券面总额
                    dto1.setTrustTotalAmt(item.getPledgePrin());
                    //折算比例
                    dto1.setConvrate(item.getPledgeRatio());
                    //本次可质押券面总额
                    Map map = new HashMap();
                    map.put("FINPROD_TYPE", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    map.put("finprodId",item.getBondCode());
                    List<FxSecrepoQuoteDetail001Dto> bondDetail = fxSecrepoQuoteDetailMapper.selectFinprodDetail(map);
                    if(!StringUtil.isEmptyList(bondList)){
                        //最新净值
                        dto1.setNetprice(bondDetail.get(0).getNetprice());
                        //债券到期日
                        dto1.setMdate(bondDetail.get(0).getMdate());
                        //发行机构
                        dto1.setTrustee(bondDetail.get(0).getIssuser());
                        //最新净价日期
                        dto1.setCdate(bondDetail.get(0).getCdate());
                    }
                    List<FxSecrepoQuoteDetail001Dto> amtDetail = fxSecrepoQuoteDetailMapper.countQuoteAmtBySecCode(dto.getEntrustId(),item.getBondCode());
                    BigDecimal totalAmt = new BigDecimal(0);
                    BigDecimal costAmt = (item.getPledgePrin() == null?new BigDecimal(0): item.getPledgePrin());
                    if(!StringUtil.isEmptyList(amtDetail)){
                        if(amtDetail.get(0).getFacetotalamt() != null
                                && amtDetail.get(0).getFacetotalamt().compareTo(totalAmt) >= 0){
                            totalAmt = amtDetail.get(0).getFacetotalamt();
                            costAmt = item.getPledgePrin().subtract(amtDetail.get(0).getFacetotalamt());
                        }
                    }
                    //已报价券面总额
                    dto1.setFaceTotalAmt(totalAmt);
                    if(costAmt.compareTo(new BigDecimal(0)) >= 0){
                        //本次可质押券面总额
                        dto1.setDscCostAmt(costAmt);
                        //本次质押券面总额
                        dto1.setRepoAmt(costAmt);
                    }
                    bondList.add(dto1);
                });
            }
            entity.setFinprodList(bondList);
        }
        return entity;
    }
    /**
     * 生成内部报价编号
     * @return
     */
    public String genNbQuoteId(){
        //quoteSecRepo20200111234 quoteSecRepo+时间+序列 回购对话
        return SEC_REPO_QUOTE_ID + DateUtil.format(new Date(),"yyyyMMdd") + fxSecrepoQuoteMapper.getSequence();
    }

    public void edit(FxSecrepoQuote entity,FxSecrepoQuote002Dto dto) {
        try {
            String pseqno=entity.getPseqno();
            String nid = IDGenerator.generate();
            entity.setSeqno(nid);
            entity.setPseqno(pseqno);
            if(entity.getRate()!=null){
                entity.setRate(entity.getRate().divide(new BigDecimal(100)));
            }
            if(entity.getXxbl()!=null){
                entity.setXxbl(entity.getXxbl().divide(new BigDecimal(100)));
            }
            //修改报价,插入修改报价后记录
            fxSecrepoQuoteMapper.insertSelective(entity);
            //"历史表插入修改报价后记录
            fxSecrepoQuoteMapper.insertSelectiveHist(entity);
            //添加明细和明细历史
            insertkSecrepoQuoteDetail(dto,nid);
           //删除报价前记录
            fxSecrepoQuoteMapper.deleteByPrimaryKey(pseqno);
            //删除报价前明细表记录
            fxSecrepoQuoteDetailMapper.deleteByPseqNo(pseqno);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }
    public void updateEffectFlag(FxSecrepoQuote entity) {
        entity.setEffectflag(FxDictCodeList.有效状态_有效.getItemCode());
        fxSecrepoQuoteMapper.updateByPrimaryKeySelective(entity);
        //"修改历史表记录的生效状态
        fxSecrepoQuoteMapper.updateByPrimaryKeySelectiveHist(entity);
    }
    public String resultMessage(CallReturn callReturn)
            throws ServiceException {
        boolean success = callReturn.isSuccess();
        if (success){
            return "发送成功！";
        }else{
            return callReturn.getMessage();
        }
    }
}
