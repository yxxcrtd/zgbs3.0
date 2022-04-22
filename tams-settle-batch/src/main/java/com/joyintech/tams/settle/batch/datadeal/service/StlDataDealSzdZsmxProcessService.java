package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.entity.SzdZsmx;
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
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/7/11
 * @see StlDataDealSzdZsmxProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdZsmxProcessService {

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;
	
	@Autowired
    protected SettleCommonService settleCommonService;

    // 生成数据清算批处理step2 processor的结果Dto(深中登ZSMX)
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdZsmxWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) {
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
     * 股票类型ZSMX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) {
        // 处理结果数据定义
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        // 遍历 输入.ZSMX清算数据List 清算数据Entity
        for(SzdZsmx szdZsmx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getZsmxList()) {
            // 组合代码 = 证券账户 + 托管单元
            String ptlId = szdZsmx.getMxgddm() + szdZsmx.getMxxwdh();
            // 组合代码不在输入的组合代码List里面不处理
            List<StlDownDataDealAccountInfoDto> accountInfoDtolist = map.get(ptlId);

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
            // 金融产品代码
            settleTransData.setFinprodId(DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdZsmx.getMxzqdh() + ".SH");
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            // 交易日期
            settleTransData.setTradeDate(szdZsmx.getMxfsrq());
            // 交割日器
            settleTransData.setSettleDate(szdZsmx.getMxfsrq());
            // 交易币种
            settleTransData.setCcy(stlDataDealProcessCommonService.getCcy(szdZsmx.getMxhbdh()));
            // 交易方向
            settleTransData.setPs(StringUtil.equalsIgnoreCase(szdZsmx.getMxywlb(), StlDataDealProcessConstant.ZSMX_YWLB_ZS) ? DictCodeList.交易方向_税费缴税.getValue() : null);
            //交易结果
            settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdZsmx.getJgdm()));
            // 成交编号
            String generate = IDGenerator.generate();
            settleTransData.setTradeId(generate);
            settleTransData.setCjbh(generate);
			// 执行日期
			settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
			// 证券品种
			settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
            // 申请编号
            settleTransData.setApplyTradeId(szdZsmx.getMxjsls());
            // 分支序号
            settleTransData.setBranch(0L);
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());
            // 交易份额
            settleTransData.setShareAmt(new BigDecimal(szdZsmx.getMxjcgs()));
            // 交易金额
            settleTransData.setTradeAmt(szdZsmx.getMxfsje());
            // 交易金额_原始
            settleTransData.setTradeAmtRaw(szdZsmx.getMxfsje());
            // 交易场所
            settleTransData.setTradeMarket(DictCodeList.交易流通场所_深圳证券交易所.getValue());
            // 交易市场
            settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            // 席位号
            settleTransData.setSeatNumber(szdZsmx.getMxxwdh());
            // 组合代码
            settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
            // 证券账户代码
            settleTransData.setSecAcctId(szdZsmx.getMxgddm());
            // 资金账户
            settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
            // 业务场景分类
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_股息红利税_补缴.getValue());
			// 临时表流水号
			settleTransData.setSeqNo(szdZsmx.getSeqno());

            // TODO 佣金，佣金是否随交易支付，佣金_原始

            StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
            stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
//            stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
        }
        return stlDownDataDealWriteDto;
    }
}
