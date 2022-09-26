package com.joyintech.tams.invest.trade.transfer.aop;

import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.trade.transfer.deposittransfere.dto.DepositTransfere001Dto;
import com.joyintech.tams.invest.trade.transfer.deposittransfere.dto.DepositTransfere002Dto;
import com.joyintech.tams.invest.trade.transfer.fund.dto.FundAssetTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.dto.LendTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.lendtransfer.dto.LendTransfer002Dto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.netplantransfer.dto.NetPlanTransfer002Dto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDto;
import com.joyintech.tams.invest.trade.transfer.planratetransfer.dto.PlanTransferDtos;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransfer001Dto;
import com.joyintech.tams.invest.trade.transfer.sectransfer.dto.SecTransferDto;
import com.joyintech.tams.invest.trade.transfer.trdassetsProject.dto.TrdAssetsProject001Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 工具类 用来组装转仓风险报文
 *
 * @author gwl
 * @version 1.0 2022年8月16日
 * @see RskInputParam
 */
public class RskInputParam {

    private final static Logger logger = LoggerFactory.getLogger(RskInputParam.class);

    /**
     * 组装风险占用报文
     *
     * @param list
     * @param busiType
     * @return
     */
    public static List<Map<String, Object>> buildOccupyParam(Object obj, String infCode) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            logger.info("转仓报文：{}", JsonUtils.object2Json(obj));
            if (Objects.isNull(obj)) return result;
            if (obj instanceof SecTransfer001Dto) {//债券
                SecTransfer001Dto sec001Dto = (SecTransfer001Dto) obj;
                List<SecTransferDto> dtoList = sec001Dto.getSecResoluteDtoList();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (SecTransferDto dto : dtoList) {
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getCounterId());
                    rdsDto.setEventDate(dto.getTradeDateText());
                    rdsDto.setPortfolioCode(dto.getPortfolioId());
                    rdsDto.setAssetCode(dto.getFinprodId());
                    rdsDto.setAmount(dto.getFpriceAmt());
                    rdsDto.setTradeNum(dto.getShareAmt());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setMarketCode(dto.getFinprodMarketId());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setTradeAmtReal(dto.getTradeAmtAndFee());
                    rdsDto.setTradePlace(dto.getTradeMarket());
                    rdsDto.setMdate(dto.getMdate());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setInvAim(dto.getInvAim());
                    rdsDto.setAllPrice(dto.getUnitFprice());
                    rdsDto.setEventId(String.format("%s%s", dto.getTradeId(), rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(dto.getCounterId());
                    dtoBy.setCounterId(dto.getPortfolioId());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", dto.getTradeId(), dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }
            if (obj instanceof TrdAssetsProject001Dto) {//项目类
                TrdAssetsProject001Dto sec001Dto = (TrdAssetsProject001Dto) obj;
                List<TrdAssetsProject001Dto> dtoList = sec001Dto.getProjectListInfoList();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (TrdAssetsProject001Dto dto : dtoList) {
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getPortfolioId());
                    rdsDto.setEventDate(DateUtil.format(dto.getTradeDate(), "yyyy-MM-dd"));
                    rdsDto.setPortfolioCode(StringUtil.isEmpty(sec001Dto.getPortfolioId()) ? sec001Dto.getInvTransPortf() : sec001Dto.getPortfolioId());
                    rdsDto.setAssetCode(String.format("%s_%s","F18",dto.getFinprodMarketId()));
                    rdsDto.setAmount(dto.getSplitTradeAmt());
                    rdsDto.setTradeNum(dto.getPrinAmt());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setMarketCode(dto.getFinprodMarketId());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setTradePlace(dto.getTradeMarket());
                    rdsDto.setMdate(dto.getMdate());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setInvAim(dto.getInvAim());
                    rdsDto.setAllPrice(dto.getUnitFprice());
                    rdsDto.setEventId(String.format("%s%s", dto.getTradeId(), rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(rdsDto.getCounterId());
                    dtoBy.setCounterId(rdsDto.getPortfolioCode());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", dto.getTradeId(), dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }
            if (obj instanceof PlanTransferDto) {//计划利率型
                PlanTransferDto planTransferDto = (PlanTransferDto) obj;
                List<PlanTransferDtos> dtoList = planTransferDto.getPlanTransferDtos();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (PlanTransferDtos dto : dtoList) {
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getPortfolioId());
                    rdsDto.setEventDate(DateUtil.format(dto.getTradeDate(), "yyyy-MM-dd"));
                    rdsDto.setPortfolioCode(planTransferDto.getPortfolioId());
                    rdsDto.setAssetCode(dto.getFinprodId());
                    rdsDto.setAmount(dto.getTransferTradeAmt());
                    rdsDto.setTradeNum(dto.getPrinAmt());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setMarketCode(planTransferDto.getFinprodMarketId());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setEventId(String.format("%s%s", dto.getTradeId(), rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(dto.getPortfolioId());
                    dtoBy.setCounterId(planTransferDto.getPortfolioId());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", dto.getTradeId(), dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }

            if (obj instanceof NetPlanTransfer001Dto) {//计划净值型
                NetPlanTransfer001Dto planTransfer001Dto = (NetPlanTransfer001Dto) obj;
                List<NetPlanTransfer002Dto> dtoList = planTransfer001Dto.getNetPlanTransfer002DtoList();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (NetPlanTransfer002Dto dto : dtoList) {
                    String tradeId = dto.getFinProductTradeDto().getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getTradeId();
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getPortfolioId());
                    rdsDto.setEventDate(DateUtil.format(dto.getTradeDate(), "yyyy-MM-dd"));
                    rdsDto.setPortfolioCode(planTransfer001Dto.getPortfolioId());
                    rdsDto.setAssetCode(planTransfer001Dto.getFinprodId());
                    rdsDto.setAmount(dto.getTradeMoney());
                    rdsDto.setTradeNum(dto.getTradeAmt());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setMarketCode(planTransfer001Dto.getFinprodMarketId());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setEventId(String.format("%s%s", tradeId, rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(dto.getPortfolioId());
                    dtoBy.setCounterId(planTransfer001Dto.getPortfolioId());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", tradeId, dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }
            if (obj instanceof LendTransfer001Dto) {//拆借
                LendTransfer001Dto lendTransfer001Dto = (LendTransfer001Dto) obj;
                List<LendTransfer002Dto> dtoList = lendTransfer001Dto.getLendTransfer002DtoList();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (LendTransfer002Dto dto : dtoList) {
                    String tradeId = dto.getFinProductTradeDto().getBaseTradeProductDealDtos().get(0).getTrdProductDeal().getTradeId();
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getPortfolioId());
                    rdsDto.setEventDate(DateUtil.format(dto.getTradeDate(), "yyyy-MM-dd"));
                    rdsDto.setPortfolioCode(lendTransfer001Dto.getPortfolioId());
                    rdsDto.setAssetCode(lendTransfer001Dto.getFinprodId());
                    rdsDto.setAmount(dto.getTradeAmt());
                    rdsDto.setTradeNum(dto.getPrinAmt());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setEventId(String.format("%s%s", tradeId, rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(dto.getPortfolioId());
                    dtoBy.setCounterId(lendTransfer001Dto.getPortfolioId());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", tradeId, dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }
            if (obj instanceof FundAssetTransfer001Dto) {//基金
                FundAssetTransfer001Dto transfer001Dto = (FundAssetTransfer001Dto) obj;
                List<FundAssetTransfer001Dto> dtoList = transfer001Dto.getFunAssetTransferDtoList();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (FundAssetTransfer001Dto dto : dtoList) {
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getPortfolioId());
                    rdsDto.setEventDate(DateUtil.format(dto.getTradeDate(), "yyyy-MM-dd"));
                    rdsDto.setPortfolioCode(StringUtil.isEmpty(transfer001Dto.getPortfolioId())?transfer001Dto.getInvTransPortf():transfer001Dto.getPortfolioId());
                    rdsDto.setAssetCode(transfer001Dto.getFinprodIdOrg());
                    rdsDto.setAmount(dto.getTradeAmt());
                    rdsDto.setTradeNum(dto.getTradePrin());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setMarketCode(transfer001Dto.getFinprodMarketId());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setTradePlace(transfer001Dto.getTradeMarket());
                    rdsDto.setEventId(String.format("%s%s", dto.getTradeId(), rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(dto.getPortfolioId());
                    dtoBy.setCounterId(rdsDto.getPortfolioCode());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", dto.getTradeId(), dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }
            if (obj instanceof DepositTransfere001Dto) {//存款
                DepositTransfere001Dto transfer001Dto = (DepositTransfere001Dto) obj;
                List<DepositTransfere002Dto> dtoList = transfer001Dto.getDepositTransfere002Dto();
                InterfRiskRdsDto rdsDto = null, dtoBy = null;
                for (DepositTransfere002Dto dto : dtoList) {
                    rdsDto = new InterfRiskRdsDto();
                    rdsDto.setPs(BussinessDictCodeList.交易方向_卖出.getValue());
                    rdsDto.setInfCode(infCode);
                    rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
                    rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
                    rdsDto.setCounterId(dto.getPortfolioId());
                    rdsDto.setEventDate(DateUtil.format(dto.getTradeDate(), "yyyy-MM-dd"));
                    rdsDto.setPortfolioCode(transfer001Dto.getPortfolioId());
                    rdsDto.setAssetCode(transfer001Dto.getFinprodId());
                    rdsDto.setAmount(dto.getTransferTradeAmt());
                    rdsDto.setTradeNum(dto.getPrinAmt());
                    rdsDto.setDeliveryDate(dto.getTradeDate());
                    rdsDto.setMarketCode(transfer001Dto.getFinprodMarketId());
                    rdsDto.setIsWarnNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsApprNeedConfirm(DictCodeList.是否_否.getValue());
                    rdsDto.setIsInnerTrd(DictCodeList.是否_是.getValue());
                    rdsDto.setEventId(String.format("%s%s", dto.getTradeId(), rdsDto.getPs()));
                    result.add(BeanUtil.object2Map(rdsDto));
                    dtoBy = new InterfRiskRdsDto();
                    BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
                    dtoBy.setPortfolioCode(dto.getPortfolioId());
                    dtoBy.setCounterId(transfer001Dto.getPortfolioId());
                    dtoBy.setPs(BussinessDictCodeList.交易方向_买入.getValue());
                    dtoBy.setEventId(String.format("%s%s", dto.getTradeId(), dtoBy.getPs()));
                    result.add(BeanUtil.object2Map(dtoBy));
                }
            }
            logger.info("转仓风险报文：{}", JsonUtils.object2Json(result));
        }catch (Exception e) {
            return null;
        }
        return result;
    }

    /**
     * 组装风险释放报文
     *
     * @param list
     * @param busiType
     * @return
     */
    public static List<Map<String, Object>> buildReleaseParam(String tradeId,String eventDate, String infCode) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            if (Objects.isNull(tradeId)) return result;
            InterfRiskRdsDto rdsDto = new InterfRiskRdsDto();
            rdsDto.setInfCode(infCode);
            rdsDto.setRevokeFlg(DictCodeList.是否_是.getValue());
            rdsDto.setEventType(BussinessDictCodeListRds.事件类型_投资交易.getValue());
            rdsDto.setCcy(BussinessDictCodeListRds.币种_人民币_CNY.getValue());
            rdsDto.setEventDate(eventDate);
            rdsDto.setEventId(String.format("%s%s", tradeId, BussinessDictCodeList.交易方向_卖出.getValue()));
            result.add(BeanUtil.object2Map(rdsDto));
            InterfRiskRdsDto dtoBy = new InterfRiskRdsDto();
            BeanUtil.copyPropertiesIgnoreNull(dtoBy, rdsDto);
            dtoBy.setEventId(String.format("%s%s", tradeId, BussinessDictCodeList.交易方向_买入.getValue()));
            result.add(BeanUtil.object2Map(dtoBy));
            logger.info("转仓撤销风险报文：{}", JsonUtils.object2Json(result));
        }catch (Exception e) {
            return null;
        }
        return result;
    }
}
