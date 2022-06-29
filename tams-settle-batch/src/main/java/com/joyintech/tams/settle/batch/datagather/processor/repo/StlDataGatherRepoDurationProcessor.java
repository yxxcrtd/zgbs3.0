package com.joyintech.tams.settle.batch.datagather.processor.repo;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;
import com.joyintech.tams.settle.batch.datagather.dto.*;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 回购存续期处理
 * @author zznn
 * @date 2021/11/1
 */
@Service
@Lazy
public class StlDataGatherRepoDurationProcessor extends BaseProcessor<NeedOperateEntity, InvestTradeWriteDto> {

    @Autowired
    private SettleCommonService settleCommonService;

    @Override
    public InvestTradeWriteDto process(NeedOperateEntity operateEntity) throws Exception {
        InvestTradeWriteDto investTradeWriteDto = new InvestTradeWriteDto();
        InvestTradeReaderDto item = (InvestTradeReaderDto)operateEntity;
        // 根据InvestTradeReaderDto中的成交数据明细数据列表，构建一个InvestTradeWriteDto
        List<SettleTransDataDto> settleTransDataDtos = item.getSettleTransDataList();
        if (CollectionUtils.isNotEmpty(settleTransDataDtos)) {
            //汇总交易
            List<TotalTrdProductDealDto> finProductDealTotalList = new ArrayList<>();
            //组合交易
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            //投管交易关系
            List<SettleTransTgMapping>  settleTransTgMappingList = new ArrayList<>();
            //划款指令
            List<StlPayOrders> stlPayOrdersList = new ArrayList<>();
            //金融产品交易明细
            List<TrdProductDealDto> productDealList = new ArrayList<>();
            //金融产品汇总附属交易
            List<TrdProductDealAdd> trdProductDealAddList = new ArrayList<>();
            handTrdProductDeal(settleTransDataDtos);
            
            //add by wujie on 20220301 start remark:调用估值交易入账参数对象
            List<AllTradeCommonInputDto> listInputDto = new ArrayList<>();
            //add by wujie on 20220301 end remark:调用估值交易入账参数对象
            
            for (SettleTransDataDto settleTransData : settleTransDataDtos) {
                //汇总交(资产维度)
                TotalTrdProductDealDto totalTrdProductDealDto = new TotalTrdProductDealDto();
                // GNS-925:竞价回购到期生成存续期交易有误 modify by fuyongsheng 20220220 start
                // totalTrdProductDealDto.setShareAmt(settleTransData.getShareAmt());
                totalTrdProductDealDto.setShareAmt(settleTransData.getShareNum());
                // GNS-925:竞价回购到期生成存续期交易有误 modify by fuyongsheng 20220220 end
                totalTrdProductDealDto.setTradeAmt(settleTransData.getTradeAmt());
                // GNS-925:计算利息总额 modify by fuyongsheng 20220228 start
                // totalTrdProductDealDto.setIntAmt(settleTransData.getIntAmt());
                totalTrdProductDealDto.setIntAmt(settleTransData.getTrdProductDealList().stream().map(TrdProductDealDto::getIntAmt).reduce(BigDecimal.ZERO,BigDecimal::add));
                // GNS-925:计算利息总额 modify by fuyongsheng 20220228 end
                totalTrdProductDealDto.setPrinAmt(settleTransData.getShareNum());
                // ZGZGST-4220:解决页面字段为空的问题 fuyongsheng 20211216 modify start
                // totalTrdProductDealDto.setFpriceAmt(settleTransData.getFpriceAmt());
                // 全价总额 = 本金 + 金额 modify by fuyongsheng 20211218 start
                // totalTrdProductDealDto.setFpriceAmt(settleTransData.getmTradeAmt());
                // GNS-889:使用工具类操作,防止null指针 modify by fuyongsheng 20220220 start
                // totalTrdProductDealDto.setFpriceAmt(totalTrdProductDealDto.getIntAmt().add(totalTrdProductDealDto.getPrinAmt()));
                totalTrdProductDealDto.setFpriceAmt(ArithmeticUtil.doAdd(totalTrdProductDealDto.getIntAmt(),totalTrdProductDealDto.getPrinAmt()));
                // GNS-889:使用工具类操作,防止null指针 modify by fuyongsheng 20220220 end
                // 全价总额 = 本金 + 金额 modify by fuyongsheng 20211218 end

                // GNS-925:生成回购到期数据有误 modify by fuyongsheng 20220220 start
                // totalTrdProductDealDto.setTheoryInt(settleTransData.getmIntAmt());
                // totalTrdProductDealDto.setTheoryprinAmt(settleTransData.getmPrinAmt());
                totalTrdProductDealDto.setTheoryInt(totalTrdProductDealDto.getIntAmt());
                totalTrdProductDealDto.setTheoryprinAmt(totalTrdProductDealDto.getPrinAmt());
                // GNS-925:生成回购到期数据有误 modify by fuyongsheng 20220220 end

                // ZGZGST-4220:解决页面字段为空的问题 fuyongsheng 20211216 modify end
                totalTrdProductDealDto.setTradeAmt(totalTrdProductDealDto.getFpriceAmt());
                createTotalDeal(totalTrdProductDealDto,settleTransData);
                //wangyang1 20220114 start
                String totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_逆回购录入);
                totalTrdProductDealDto.setTotleTradeId(totalTradeId);
                totalTrdProductDealDto.setFeeAmt(BigDecimal.ZERO);
                totalTrdProductDealDto.setFeeAmtUnpay(BigDecimal.ZERO);
                totalTrdProductDealDto.setRemark("数据清算自动处理");
                totalTrdProductDealDto.setDqprinAmt(settleTransData.getmPrinAmt());
                totalTrdProductDealDto.setDeliveryType("03");
                totalTrdProductDealDto.setmDeliveryType("03");
                //wangyang1 20220114 end
                finProductDealTotalList.add(totalTrdProductDealDto);

                //投管交易关系
                List<String> tradeIdList = settleTransData.getTradeIdList();
                SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
                settleTransTgMapping.setTradeId(settleTransData.getTradeId());
                settleTransTgMapping.setTradeIdTg(totalTrdProductDealDto.getTotleTradeId());
                settleTransTgMapping.setTgDataStatus("02");
                // add by fuyongsheng 对成交与投管关系执行时间赋值 fuyongsheng 20211217 start
                settleTransTgMapping.setDealDate(getBatchExecuteInfo().getExecuteDate());
                // add by fuyongsheng 对成交与投管关系执行时间赋值 fuyongsheng 20211217 end
                settleTransTgMappingList.add(settleTransTgMapping);
                //组合交易
                //划款指令
                StlPayOrders stlPayOrders = createStlPayOrders(totalTrdProductDealDto,settleTransData.getAcctNo());
                stlPayOrdersList.add(stlPayOrders);
                   
                //add by wujie on 20220301 start remark:调用估值交易入账参数对象
                List<TrdProductDeal> rstlist = new ArrayList<>();
                //add by wujie on 20220301 end remark:调用估值交易入账参数对象
                
                for (TrdProductDealDto  trdProductDealDto: settleTransData.getTrdProductDealList()) {
                    TrdProductDealDto trdProductDeal = new TrdProductDealDto();
                    trdProductDeal.setPs(trdProductDealDto.getPs());
                    //交易份额
                    trdProductDeal.setShareAmt(trdProductDealDto.getShareAmt());
                    //交易本金
                    trdProductDeal.setPrinAmt(trdProductDealDto.getPrinAmt());
                    //利息总额
                    trdProductDeal.setIntAmt(trdProductDealDto.getIntAmt());
                    //全价总额
                    trdProductDeal.setFpriceAmt(trdProductDealDto.getFpriceAmt());
                    //交易金额
                    trdProductDeal.setTradeAmt(trdProductDealDto.getTradeAmt());
                    trdProductDeal.setTotleTradeId(totalTrdProductDealDto.getTotleTradeId());
                    createDeal(trdProductDeal,settleTransData);
                    //wangyang1 20220114 start
                    trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_逆回购录入));
                    trdProductDeal.setPayDate(trdProductDealDto.getSettleDate());
                    //wangyang1 20220114 end
                    trdProductDeal.setPortfolioId(settleTransData.getPortfolioId());
                    TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
                    trdProductDealAdd.setTradeId(trdProductDeal.getTradeId());
                    trdProductDealAdd.setPortfolioId(settleTransData.getPortfolioId());
                    PtlTrade ptlTrade = new PtlTrade();
                    ptlTrade.setPortfolioId(settleTransData.getPortfolioId());
                    ptlTradeList.add(createPtlTrade(ptlTrade,trdProductDeal));
                    productDealList.add(trdProductDeal);
                    trdProductDealAddList.add(trdProductDealAdd);
                    
                    //add by wujie on 20220301 start remark:调用估值交易入账参数对象
                    TrdProductDeal dto = new TrdProductDeal();
                    BeanUtil.copyProperties(dto, trdProductDeal);
                    rstlist.add(dto);
                    //add by wujie on 20220301 end remark:调用估值交易入账参数对象
                }
                //add by wujie on 20220301 start remark:调用估值交易入账参数对象
            	AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            	inputDto.setProdid(totalTrdProductDealDto.getPortfolioId());
                inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
                inputDto.setTrdProductDealList(rstlist);
                inputDto.setStlPayOrders(stlPayOrders);
                listInputDto.add(inputDto);
            	//add by wujie on 20220301 end remark:调用估值交易入账参数对象
            }
            //add by wujie on 20220301 start remark:调用估值交易入账参数对象
            investTradeWriteDto.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
            investTradeWriteDto.setListInputDto(listInputDto);
            //add by wujie on 20220301 end remark:调用估值交易入账参数对象
            
            investTradeWriteDto.setSettleTransTgMappingList(settleTransTgMappingList);
            investTradeWriteDto.setFinProductDealTotalList(finProductDealTotalList);
            investTradeWriteDto.setPtlTradeList(ptlTradeList);
            investTradeWriteDto.setStlPayOrdersList(stlPayOrdersList);
            investTradeWriteDto.setProductDealList(productDealList);
            investTradeWriteDto.setTrdProductDealAddList(trdProductDealAddList);
            investTradeWriteDto.setSecType(DictCodeListExt1.证券品种_回购.getValue());
        }
        return investTradeWriteDto;
    }

    /**
     * 构建汇总交易
     * @param dealTotal
     * @param settleTransData
     */
    private void createTotalDeal(TotalTrdProductDealDto dealTotal,SettleTransDataDto settleTransData){
        // ZGZGST-4220:存续期交易编号根据正、逆回购不同进行区分 fuyongsheng 20211216 modify start
        // String totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易);
        String totalTradeId;
        if (StringUtil.equals(settleTransData.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())) {
            totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_正回购到期);
        } else {
            totalTradeId = settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_逆回购到期);
        }
        // ZGZGST-4220:存续期交易编号根据正、逆回购不同进行区分 fuyongsheng 20211216 modify end
        dealTotal.setTotleTradeId(totalTradeId);
        // 业务类型
        dealTotal.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
        // 金融产品代码
        dealTotal.setFinprodId(settleTransData.getFinprodId());
        // 金融产品类型（投管分类）
        dealTotal.setFinprodType2(settleTransData.getFinprodType2());
        // 分支序号
        dealTotal.setBranch(0L);
        // 交易日
        // dealTotal.setTradeDate(settleTransData.getTradeDate());
        dealTotal.setTradeDate(settleTransData.getMdate());
        // 起息日
        dealTotal.setVdate(settleTransData.getVdate());
        // 到期日
        // ZGZGST-4220:到期日赋值Mdate fuyongsheng modify start
        // dealTotal.setMdate(settleTransData.getMdate2());
        dealTotal.setMdate(settleTransData.getMdate());
        // ZGZGST-4220:到期日赋值Mdate fuyongsheng modify end
        // 交割日
        dealTotal.setSettleDate(settleTransData.getSettleDate());
        // 交易币种
        dealTotal.setCcy(settleTransData.getCcy());
        // 单位净价
        dealTotal.setUnitCprice(settleTransData.getUnitCprice());
        // 单位利息
        dealTotal.setUnitInt(settleTransData.getUnitInt());
        // 单位全价
        dealTotal.setUnitFprice(settleTransData.getUnitFprice());
        // 单位面值
        dealTotal.setParValue(settleTransData.getParValue());
        // 净价总额
        dealTotal.setCpriceAmt(settleTransData.getCpriceAmt());
        // 交易份额
        //dealTotal.setShareAmt(settleTransData.getShareAmt());
        // 交易本金
        //dealTotal.setTradeAmt(settleTransData.getTradeAmt());
        // 利息总额
        //dealTotal.setIntAmt(settleTransData.getIntAmt());
        // 全价总额
        //dealTotal.setFpriceAmt(settleTransData.getFpriceAmt());
        // 交易金额
        //dealTotal.setTradeAmt(settleTransData.getTradeAmt());
        // 交收方式
        dealTotal.setSettlemenType(settleTransData.getSettleMode());
        // 清算速度
        dealTotal.setSettleSpeed(settleTransData.getSettleSpeed());
        // 交易方向
        dealTotal.setPs(settleTransData.getPs());
        // 投资目的
        dealTotal.setInvAim(settleTransData.getInvAim());
        // 交易状态
        dealTotal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        dealTotal.setIsCancel(DictCodeList.是否_否.getValue());
        //是否结清
        dealTotal.setIsClean(DictCodeList.是否_是.getValue());
        // 证券管理户代码
        dealTotal.setSecManageAcctId(settleTransData.getSecAcctId());
        //是否外部交易
        dealTotal.setIsOutTrade(DictCodeList.是否_是.getValue());
        //是否逾期
        dealTotal.setIsOverdueDeal(DictCodeList.是否_否.getValue());
        // 交易场所
        dealTotal.setTradeMarket(settleTransData.getTradeMarket());
        // 交易平台
        dealTotal.setTradePlat(settleTransData.getTradePlat());
        // 交易市场
        dealTotal.setTradeMarketMode(settleTransData.getTradeMarketMode());
        // 投资组合代码
        dealTotal.setPortfolioId(settleTransData.getPortfolioId());
        // 投管业务类型
        dealTotal.setTgBusiType(settleTransData.getBusiType());
        // 席位号
        dealTotal.setSeatId(settleTransData.getSeatNumber());
        // 利率
        dealTotal.setRate(null);
        // 回购期限(天)
        dealTotal.setRepoTerm(null);
        // 期限品种
        dealTotal.setTermType(null);
        // 委托代码
        dealTotal.setEntrustId(settleTransData.getEntrustId());
    }

    private PtlTrade createPtlTrade(PtlTrade ptlTrade,TrdProductDealDto trdProductDealDto) {
        // 组合代码
        //ptlTrade.setPortfolioId(trdProductDealDto.getPortfolioId());
        // 业务编号
        ptlTrade.setBusiNo(trdProductDealDto.getTradeId());
        // 业务表
        ptlTrade.setBusiTable(DictCodeListStl.业务表_金融产品交易表.getValue());
        // 业务明细代码
        ptlTrade.setBusiId(trdProductDealDto.getFinprodId());
        // 投资目的
        ptlTrade.setInvAim(trdProductDealDto.getInvAim());
        // 证券托管户代码
        ptlTrade.setSecManageAcctId(trdProductDealDto.getSecManageAcctId());
        // 实际发生日
        ptlTrade.setSettleDate(trdProductDealDto.getSettleDate());
        // 业务类型
        ptlTrade.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
        return ptlTrade;
    }

    /**
     * 构建交易明细
     * @param trdProductDeal
     * @param settleTransData
     */
    private void createDeal(TrdProductDealDto trdProductDeal, SettleTransDataDto settleTransData){
        trdProductDeal.setTradeId(settleCommonService.buildBussinessCodeExt(DictCodeList.共通业务类型_资产存续期交易));
        // 业务类型
        trdProductDeal.setBusiType(DictCodeList.共通业务类型_质押式回购存续期.getValue());
        // 金融产品代码
        trdProductDeal.setFinprodId(settleTransData.getFinprodId());
        // 金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(settleTransData.getFinprodType2());
        // 金融产品类型
        trdProductDeal.setFinprodType(settleTransData.getFinprodType());
        // 分支序号
        trdProductDeal.setBranch(0L);
        // 交易日
        // ZGZGST-4220:回购存续期交易日是明细到期日 fuyongsheng modify start
        // trdProductDeal.setTradeDate(settleTransData.getTradeDate());
        trdProductDeal.setTradeDate(settleTransData.getMdate());
        // ZGZGST-4220:回购存续期交易日是明细到期日 fuyongsheng modify end
        // 起息日
        // ZGZGST-4220:金融产品交易表起息日和到期日赋值 fuyongsheng modify start
        // trdProductDeal.setVdate(null);
        trdProductDeal.setVdate(settleTransData.getVdate());
        // 到期日
        // trdProductDeal.setMdate(null);
        trdProductDeal.setMdate(settleTransData.getMdate());
        // ZGZGST-4220:金融产品交易表起息日和到期日赋值 fuyongsheng modify end
        // 交付日
        trdProductDeal.setPayDate(settleTransData.getVpayDate());
        // 交割日
        trdProductDeal.setSettleDate(settleTransData.getSettleDate());
        // 交易币种
        trdProductDeal.setCcy(settleTransData.getCcy());
        // 净值日期
        trdProductDeal.setCpriceDate(null);
        // 投资目的
        trdProductDeal.setInvAim(settleTransData.getInvAim());
        // 交易状态
        trdProductDeal.setTradeStatus(DictCodeListExt1.交易状态_初始.getValue());
        // 是否已撤销
        trdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
        // 原交易编号
        trdProductDeal.setpTradeId(null);
        // 是否结清
        trdProductDeal.setIsClean(null);
        // 汇总交易编号（父类中设置）
        // 反向交易编号
        trdProductDeal.setrTradeId(null);
        // 通道代码
        trdProductDeal.setChlId(null);
        // 交易对手
        trdProductDeal.setCounterId(null);
        // 是否外部交易
        trdProductDeal.setIsOutTrade(DictCodeList.是否_是.getValue());
        // 交易场所
        trdProductDeal.setTradeMarket(settleTransData.getTradeMarket());
        // 交易平台
        trdProductDeal.setTradePlat(settleTransData.getTradePlat());
        // 交易市场
        trdProductDeal.setTradeMarketMode(settleTransData.getTradeMarketMode());
        // 本方交易员
        trdProductDeal.setTrader(null);
        // 对手方交易员
        trdProductDeal.setCounterTrader(null);
        // 对手方联系方式
        trdProductDeal.setCounterContact(null);
        // 证券管理户代码
        trdProductDeal.setSecManageAcctId(settleTransData.getSecAcctId());
        // 关联系统交易编号
        trdProductDeal.setfTradeId(null);
        // 现金流代码
        trdProductDeal.setCashId(null);
        // 登记托管机构
        trdProductDeal.setRegistOrg(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue());
        // 备注
        // trdProductDeal.setRemark(null);
        trdProductDeal.setRemark("数据清算自动处理");
        // 撤销说明
        trdProductDeal.setCancelRemark(null);
        // 销售代码
        trdProductDeal.setSaleCode(null);
        // 计提份额
        trdProductDeal.setAccrNum(null);
        // 交收方式
        trdProductDeal.setSettlemenType(settleTransData.getSettleMode());
        // 席位号
        trdProductDeal.setSeatId(settleTransData.getSeatNumber());
        // 投资组合代码
        trdProductDeal.setPortfolioId(settleTransData.getPortfolioId());
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
        // 成交编号（金额计算时已设置）
        // 权益登记日
        trdProductDeal.setRegDate(null);
        // 权益登记日份额
        trdProductDeal.setRegDateAmt(null);
        // 是否优先配售
        trdProductDeal.setIsPurchTypeYx(settleTransData.getIsPurchTypeYx());
        // 委托代码
        trdProductDeal.setEntrustId(settleTransData.getOrderId());
        // 资金账户
        trdProductDeal.setAcctNo(settleTransData.getAcctNo());
        trdProductDeal.setSettleSpeed(settleTransData.getSettleSpeed());
       /* //交易本金
        trdProductDeal.setPrinAmt(settleTransData.getPrinAmt());
        //利息总额
        trdProductDeal.setIntAmt(settleTransData.getIntAmt());
        //全价总额
        trdProductDeal.setFpriceAmt(settleTransData.getFpriceAmt());
        //交易金额
        trdProductDeal.setTradeAmt(settleTransData.getTradeAmt());*/
        // ZGZGST-4220: 净价总额应为空 fuyongsheng 20211216 modify start
        //净价总额
        // trdProductDeal.setCpriceAmt(com.joyintech.framework.util.ArithmeticUtil.subtract(settleTransData.getFpriceAmt(),settleTransData.getIntAmt()));
        // ZGZGST-4220: 净价总额应为空 fuyongsheng 20211216 modify end
        // 单位净价
        trdProductDeal.setUnitCprice(settleTransData.getUnitCprice());
        // 单位利息
        trdProductDeal.setUnitInt(settleTransData.getUnitInt());
        // 单位全价
        trdProductDeal.setUnitFprice(settleTransData.getUnitFprice());
        // ZGZGST-4220:交易方向已在外层处理 fuyongsheng 20211216 modify start
        // 交易方向
        // trdProductDeal.setPs(settleTransData.getPs());
        // ZGZGST-4220:交易方向已在外层处理 fuyongsheng 20211216 modify end
        // 结算方式 券款兑付
        trdProductDeal.setSettMode("03");
        if(StringUtil.equals(settleTransData.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
            trdProductDeal.setTgBusiType( DictCodeListExt1.平安业务类型_正回购到期.getValue());
        }else{
            trdProductDeal.setTgBusiType( DictCodeListExt1.平安业务类型_逆回购到期.getValue());
        }
    }


    /**
     * 划款指令
     * @param total
     * @param acctNo
     * @return
     */
    private  StlPayOrders createStlPayOrders(TrdFinProductDealTotal total,String acctNo) {
        // 指令
        StlPayOrders stlPayOrders = new StlPayOrders();
        // 指令编号
        stlPayOrders.setOrderId(settleCommonService.buildBussinessIdBySeqno("9998"));
        // 指令类型
        stlPayOrders.setOrderType(null);
        // 结算日期
        stlPayOrders.setSettleDate(total.getSettleDate());
        // 本方资金账户
        stlPayOrders.setMyAcctNo(acctNo);
        // 结算金额
        stlPayOrders.setSettleAmt(total.getTradeAmt());
        // 币种
        stlPayOrders.setCcy(total.getCcy());
        // 是否需要发送
        stlPayOrders.setIsNeedSend(DictCodeList.是否_否.getValue());
        // 是否汇总交易
        stlPayOrders.setIsTotalTrade(DictCodeList.是否_是.getValue());
        // 交易编号
        stlPayOrders.setTradeId(total.getTotleTradeId());
        // 指令状态
        stlPayOrders.setOrderType(DictCodeListExt1.交易状态_初始.getValue());
        // 投资组合代码
        stlPayOrders.setPortfolioId(total.getPortfolioId());
        // 备注
        stlPayOrders.setRemark("数据清算自动处理");
        // 划款类型
        stlPayOrders.setPayType(DictCodeListExt1.平安划款类型_清算划款.getValue());
        // GNS-925:收付方向未登录 modify by fuyongsheng 20220220 start
        // 收付方向
        if(StringUtil.equals(total.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
            stlPayOrders.setPs(DictCodeListExt1.收支方向_支出.getValue());
        }else{
            stlPayOrders.setPs(DictCodeListExt1.收支方向_收入.getValue());
        }
        // GNS-925:收付方向未登录 modify by fuyongsheng 20220220 end
        return stlPayOrders;
    }


    /**
     * 处理明细
     * @param settleTransDataDtos
     */
    private void handTrdProductDeal(List<SettleTransDataDto> settleTransDataDtos){
        //分组(成交编号、席位号、交易日期)
        Map<String, List<SettleTransDataDto>> map = settleTransDataDtos.stream().collect(Collectors.groupingBy(item -> fetchGroupKey(item)));
        //交易金额汇总 利息总额汇总
        Map<String,BigDecimal> tradeAmtMap = new HashMap<>();Map<String,BigDecimal> intAmtMap = new HashMap<>();
        //最大交易金额  最大利息总额
        Map<String,String> maxTradeAmtMap = new HashMap<>();Map<String,String> maxIntAmtMap = new HashMap<>();
        for (Map.Entry<String, List<SettleTransDataDto>> key : map.entrySet()) {
            String keyitem = key.getKey();
            List<SettleTransDataDto> list = key.getValue();
            if(CollectionUtils.isNotEmpty(list)){
                // tradeAmtMap.put(keyitem,list.stream().map(item -> item.getTradeAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                tradeAmtMap.put(keyitem,list.stream().map(SettleTransData::getTradeAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                // intAmtMap.put(keyitem,list.stream().map(item -> item.getIntAmt()).reduce(BigDecimal.ZERO, BigDecimal::add));
                intAmtMap.put(keyitem,list.stream().map(SettleTransData::getIntAmt).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
                // maxTradeAmtMap.put(keyitem,list.stream().max((p1, p2) -> p1.getTradeAmt().compareTo(p2.getTradeAmt())).get().getTradeId2());
                maxTradeAmtMap.put(keyitem,list.stream().filter(item-> null != item.getTradeAmt()).max((p1, p2) -> p1.getTradeAmt().compareTo(p2.getTradeAmt())).orElse(new SettleTransDataDto()).getTradeId2());
                // maxTradeAmtMap.put(keyitem,list.stream().max((p1, p2) -> p1.getIntAmt().compareTo(p2.getIntAmt())).get().getTradeId2());
                maxIntAmtMap.put(keyitem,list.stream().filter(item-> null != item.getIntAmt()).max((p1, p2) -> p1.getIntAmt().compareTo(p2.getIntAmt())).orElse(new SettleTransDataDto()).getTradeId2());
            }
        }
        //组装
        for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
            String key = fetchGroupKey(settleTransDataDto);
            List<TrdProductDealDto> trdProductDeals = new ArrayList<>();
            if(StringUtil.equals(settleTransDataDto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())){
                settleTransDataDto.setBusiType("52"); //质押式正回购到期
            }else{
                settleTransDataDto.setBusiType("51");//质押式逆回购到期
            }
            //settleTransDataDto.setVdate(settleTransDataDto.getTradeDate());
            //settleTransDataDto.setMdate(settleTransDataDto.getMpayDate());
            settleTransDataDto.setPs(DictCodeList.交易方向_到期.getValue());
            settleTransDataDto.setTradeAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getTradeAmt(),settleTransDataDto.getShareNum()),settleTransDataDto.getPrinAmt(),2));
            if(tradeAmtMap.containsKey(key)){
                BigDecimal bigDecimal = tradeAmtMap.get(key);
                tradeAmtMap.put(key, ArithmeticUtil.subtract(bigDecimal,settleTransDataDto.getTradeAmt()));
            }
            if(null!=settleTransDataDto.getIntAmt()&&settleTransDataDto.getIntAmt().compareTo(BigDecimal.ZERO)!=0){
                TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
                trdProductDealDto.setPs(DictCodeList.交易方向_收息到账.getValue());
//                trdProductDealDto.setShareAmt(settleTransDataDto.getShareNum());
                //还本收息.交易份额
                trdProductDealDto.setShareAmt(BigDecimal.ZERO);
                // ZGZGST-4220:存续期交易回购利息赋值有误 fuyongsheng modify 20211218 start
                // settleTransDataDto.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getIntAmt(),settleTransDataDto.getShareNum()),settleTransDataDto.getShareAmt(),2));
                trdProductDealDto.setIntAmt(ArithmeticUtil.divide(ArithmeticUtil.multiply(settleTransDataDto.getIntAmt(),settleTransDataDto.getShareNum()),settleTransDataDto.getShareAmt(),2));
                // ZGZGST-4220:存续期交易回购利息赋值有误 fuyongsheng modify 20211218 end
                trdProductDealDto.setTradeAmt(trdProductDealDto.getIntAmt());
                trdProductDealDto.setFpriceAmt(trdProductDealDto.getIntAmt());
                if(intAmtMap.containsKey(key)){
                    BigDecimal bigDecimal = intAmtMap.get(key);
                    intAmtMap.put(key, ArithmeticUtil.subtract(bigDecimal,settleTransDataDto.getIntAmt()));
                }
                trdProductDealDto.setPrinAmt(BigDecimal.ZERO);
                trdProductDeals.add(trdProductDealDto);
            }
            if(null!=settleTransDataDto.getShareAmt()&&settleTransDataDto.getShareAmt().compareTo(BigDecimal.ZERO)!=0){
                TrdProductDealDto trdProductDealDto = new TrdProductDealDto();
                BigDecimal shareNum = settleTransDataDto.getShareNum();
                trdProductDealDto.setPs(DictCodeList.交易方向_还本到账.getValue());
                trdProductDealDto.setShareAmt(shareNum);
                trdProductDealDto.setPrinAmt(shareNum);
                trdProductDealDto.setIntAmt(BigDecimal.ZERO);
                trdProductDealDto.setFpriceAmt(shareNum);
                trdProductDealDto.setTradeAmt(shareNum);
                trdProductDeals.add(trdProductDealDto);
            }
            settleTransDataDto.setTrdProductDealList(trdProductDeals);
        }

        //差额分配
        for (SettleTransDataDto settleTransDataDto : settleTransDataDtos) {
            String key = fetchGroupKey(settleTransDataDto);
            if(null!=tradeAmtMap.get(key)&&tradeAmtMap.get(key).compareTo(BigDecimal.ZERO)>0){
                if(maxTradeAmtMap.containsKey(key)&&StringUtil.equals(maxTradeAmtMap.get(key),settleTransDataDto.getTradeId2())){
                    settleTransDataDto.setTradeAmt(ArithmeticUtil.doAdd(settleTransDataDto.getTradeAmt(),tradeAmtMap.get(key)));
                }
            }
            if(null!=intAmtMap.get(key)&&intAmtMap.get(key).compareTo(BigDecimal.ZERO)>0){
                settleTransDataDto.getTrdProductDealList().forEach(item ->{
                    if(StringUtil.equals(item.getPs(),DictCodeList.交易方向_收息到账.getValue())){
                        if(maxIntAmtMap.containsKey(key)&&StringUtil.equals(maxIntAmtMap.get(key),settleTransDataDto.getTradeId2())){
                            item.setIntAmt(ArithmeticUtil.doAdd(item.getIntAmt(),intAmtMap.get(key)));
                            item.setFpriceAmt(item.getIntAmt());
                            item.setTradeAmt(item.getIntAmt());
                        }
                    }
                });
            }
        }
    }

    private static String fetchGroupKey(SettleTransDataDto item){
        return StringUtil.joinString(Arrays.asList(item.getCjbh(),item.getSeatNumber(), DateUtil.format(item.getTradeDate())),"_");
    }
}

