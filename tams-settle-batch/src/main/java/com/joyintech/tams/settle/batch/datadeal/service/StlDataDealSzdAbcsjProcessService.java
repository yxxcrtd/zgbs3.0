package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.entity.SzdAbcsj;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealWriteBaseDto;
import com.joyintech.tams.settle.common.dto.StlDownDataDealAccountInfoDto;
import com.joyintech.tams.settle.common.service.SettleCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/7/14
 * @see StlDataDealSzdAbcsjProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdAbcsjProcessService {

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    protected SettleCommonService settleCommonService;

    // 生成数据清算批处理step2 processor的结果Dto(上中登ABCSJ)
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdAbcsjWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) throws ParseException {
        // 核算信息全量数据
        Map<String, List<StlDownDataDealAccountInfoDto>> map = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(true);
        
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), stlDownDataDealOperatorDto.getFinprodType())) {
            stlDownDataDealWriteDto = secTypeIsSharesDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        return stlDownDataDealWriteDto;
    }

    /**
     * 股票类型ABCSJ数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        // 处理结果数据定义
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        // 遍历  输入.ABCSJ清算数据List 清算数据Entity
        for(SzdAbcsj szdAbcsj : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getAbcsjList()) {
            // key = 证券账号
            String key = szdAbcsj.getZqzh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(key);

            if (CollectionUtils.isEmpty(accountInfoDtolist)) {
                continue;
            }
            // 组合代码不在输入的组合代码List里面不处理
            else if (stlDownDataDealOperatorDto.getPortfolioIdList().size() > 0 && !stlDownDataDealOperatorDto.getPortfolioIdList().contains(accountInfoDtolist.get(0).getPortfolioId())) {
                continue;
            }

            // 定义变量.成交数据明细DTO
            SettleTransData settleTransData = new SettleTransData();
            // 定义变量.成交数据明细附属表DTO
//            SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
            String generate = IDGenerator.generate();
            settleTransData.setTradeId(generate);
            settleTransData.setCjbh(generate);
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdAbcsj.getZqdm() + ".SH");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            // 交易日期
            settleTransData.setTradeDate(DateUtil.parseDate(szdAbcsj.getTzrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交割日器
            settleTransData.setSettleDate(DateUtil.parseDate(szdAbcsj.getTzrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交易币种
            settleTransData.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // 交易方向
            settleTransData.setPs(getPs(szdAbcsj));
            // 成交编号
            //settleTransData.setTradeId(generate);
			// 执行日期
			settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
			// 证券品种
			settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
            // 申请编号
            settleTransData.setApplyTradeId(szdAbcsj.getXh());
            // 分支序号
            settleTransData.setBranch(0L);
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());
            // 交易份额
            settleTransData.setShareAmt(new BigDecimal(szdAbcsj.getSl1()));
            // 交易金额
            settleTransData.setTradeAmt(new BigDecimal(szdAbcsj.getJe1()));
            // 交易金额_原始
            settleTransData.setTradeAmtRaw(new BigDecimal(szdAbcsj.getJe1()));
            // 单位净价
            settleTransData.setUnitCprice(new BigDecimal(szdAbcsj.getJg1()));
            // 交易场所
            settleTransData.setTradeMarket(DictCodeList.交易流通场所_上海证券交易所.getValue());
            // 交易市场
            settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            // 组合代码
            settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
            // 证券账户代码
            settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
            // 资金账户
            settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
            // 利率
            settleTransData.setIntRate(new BigDecimal(szdAbcsj.getBl1()));
            // 业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_股息红利税_补缴.getValue());
            // 交收结果
            settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdAbcsj.getJgdm()));
            // TODO 佣金，佣金是否随交易支付，佣金_原始

            StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
            stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
//            stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
        }
		
		return stlDownDataDealWriteDto;
    }

    private String getPs(SzdAbcsj abcsj) {
        if(StringUtil.equals(abcsj.getZqlb(), StlDataDealProcessConstant.ZQLB_PT)
                && StringUtil.equals(abcsj.getTzlb(), StlDataDealProcessConstant.TZLB_HLS)
                && StringUtil.equals(abcsj.getScdm(), StlDataDealProcessConstant.SCDM_01)) {
            return DictCodeList.交易方向_税费缴税.getValue();
        }
        return null;
    }
}
