package com.joyintech.tams.settle.batch.datadeal.service;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.SettleTransData;
import com.joyintech.tams.common.base.entity.SettleTransDataAdd;
import com.joyintech.tams.common.base.entity.SettleTransTgMapping;
import com.joyintech.tams.common.base.entity.SzdSjsfx;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.constant.StlDataDealProcessConstant;
import com.joyintech.tams.settle.batch.datadeal.dao.StlDownDataDealMapper;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealOperatorDto;
import com.joyintech.tams.settle.batch.datadeal.dto.StlDownDataDealWriteDto;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StlDownDataDealWriteBaseDto;
import com.joyintech.tams.settle.batch.datadeal.function.PersonalityFunction;
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
 * @see StlDataDealSzdSjsfxProcessService
 * @since 1.0
 */
@Service
public class StlDataDealSzdSjsfxProcessService {

    @Autowired
    protected StlDataDealProcessCommonService stlDataDealProcessCommonService;

    @Autowired
    private SettleCommonService settleCommonService;

    @Autowired(required = false)
    private StlDownDataDealService stlDownDataDealService;

    @Autowired
    protected StlDownDataDealMapper stlDownDataDealMapper;


    // 生成数据清算批处理step2 processor的结果Dto(深中登SJSFX)
    public StlDownDataDealWriteDto generatorStlDownDataDealSzdSjsfxWriteDto(StlDownDataDealOperatorDto stlDownDataDealOperatorDto) throws ParseException {
        // 核算信息全量数据
        Map<String, List<StlDownDataDealAccountInfoDto>> map = settleCommonService.getAllStlDownDataDealAccountInfoDtoList(true);
        // 资产类型
        String finprodType = stlDownDataDealOperatorDto.getFinprodType();
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        if (StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), finprodType)) {
        	stlDownDataDealWriteDto = secTypeIsBondDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
         	stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        else if (StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), finprodType)) {
        	stlDownDataDealWriteDto = secTypeIsSharesDeal(stlDownDataDealOperatorDto, map);
            // 佣金计算
            stlDownDataDealWriteDto = stlDataDealProcessCommonService.calcCommsionAmt(stlDownDataDealOperatorDto, stlDownDataDealWriteDto);
        }
        
     	return stlDownDataDealWriteDto;
    }

    /**
     * 债券类型SJSFX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 结算数据
     * @throws ParseException
     */
    private StlDownDataDealWriteDto secTypeIsBondDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        // 资产标的信息
        List<String> assetList = settleCommonService.getAllAssetInfo(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsfx, settleTransData, settleTransDataAdd) -> {
            String finprodId = DictCodeListExt1.金融产品类型_投管用_债券.getValue() + GlobalConstant.UNDER_LINE + szdSjsfx.getFxzqdm() + ".SZ";
            // 金融产品代码
            settleTransData.setFinprodId(finprodId);
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
            // 单位净价
//            settleTransData.setUnitCprice(new BigDecimal(100));
            //单位净价修改为 ABS(资金金额)
            settleTransData.setUnitCprice(abs(szdSjsfx.getFxzjje()));
            // 单位全价
//            settleTransData.setUnitFprice(new BigDecimal(100));
            //单位全价修改为null
            settleTransData.setUnitFprice(null);
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsfx.getFxqrgs()));
            // 全价总额
//            settleTransData.setFpriceAmt(abs(ArithmeticUtil.multiply(szdSjsfx.getFxqrgs(), new BigDecimal(100))));
            //全价总额修改为 单位净价* 交易份额
            settleTransData.setFpriceAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()));
            // 净价总额
//            settleTransData.setCpriceAmt(settleTransData.getFpriceAmt());
            //净价总额修改为 单位净价* 交易份额
            settleTransData.setCpriceAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()));
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_银行间债券交易.getValue());
            // 利息总额
            settleTransData.setIntAmt(BigDecimal.ZERO);
            // 交易本金改为 交易份额 * 债券的最新面值
            BigDecimal value = stlDataDealProcessCommonService.getUnitPrice(settleTransData.getFinprodId(),
                    settleTransData.getTradeDate());
            // 交易本金
            settleTransData.setPrinAmt(ArithmeticUtil.multiply(settleTransData.getShareAmt(), value == null ? BigDecimal.ZERO : value));
            // 交易金额
//            settleTransData.setTradeAmt(abs(szdSjsfx.getFxzjje()));
            //交易金额修改为 单位净价* 交易份额
            settleTransData.setTradeAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()));
            // 交易金额_原始
            //交易金额_原始 修改为 与交易金额一致
            settleTransData.setTradeAmtRaw(settleTransData.getTradeAmt());
            // 印花税
            settleTransData.setStampFee(BigDecimal.ZERO);
            // 经手费
            settleTransData.setJsfAmt(BigDecimal.ZERO);
            // 过户费
            settleTransData.setGhfAmt(BigDecimal.ZERO);
            // 证管费
            settleTransData.setZgfAmt(BigDecimal.ZERO);
            // 手续费
            settleTransData.setSxfAmt(BigDecimal.ZERO);
            // 结算费
            settleTransData.setSettleFee(BigDecimal.ZERO);
            // 印花税_原始
            settleTransData.setStampFee(BigDecimal.ZERO);
            // 经手费_原始
            settleTransData.setJsfAmt(BigDecimal.ZERO);
            // 过户费_原始
            settleTransData.setGhfAmt(BigDecimal.ZERO);
            // 证管费_原始
            settleTransData.setZgfAmt(BigDecimal.ZERO);
            // 手续费_原始
            settleTransData.setSxfAmt(BigDecimal.ZERO);
            // 结算费_原始
            settleTransData.setSettleFee(BigDecimal.ZERO);
            // 交易方向、业务场景明细分类
            setBondDeal(szdSjsfx, settleTransData);
            // 清算速度:申购是T+2
            if(DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())){
                settleTransData.setSettleSpeed(2L);
            }
            // GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 start
            if(DictCodeList.交易方向_申购确认.getValue().equals(settleTransData.getPs())
                    || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())){
                settleTransData.setFinprodId(stlDownDataDealMapper.getLastFinproductIdByApprovalNo(szdSjsfx.getFxzqdm()));
            }
            if(!assetList.contains(settleTransData.getFinprodId())) {
                // 不是债券类资产时，忽略
                return false;
            }
            // GNS
            // GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 end
            // GZYHTZJY-986:申购和申购确认,赋值竞价平台 fuyongsheng modify 20211230 start
            // 交易平台
            settleTransData.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
            // GZYHTZJY-986:申购和申购确认,赋值竞价平台 fuyongsheng modify 20211230 start
            return true;
        }));
    }

    /**
     * 股票类型SJSFX数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeIsSharesDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map) throws ParseException {
        // 资产标的信息
        List<String> assetList = settleCommonService.getAllAssetInfo(DictCodeListExt1.金融产品类型_投管用_股票.getValue());

        return secTypeCommonDeal(stlDownDataDealOperatorDto, map, ((szdSjsfx, settleTransData, settleTransDataAdd) -> {
            String finprodId = DictCodeListExt1.金融产品类型_投管用_股票.getValue() + GlobalConstant.UNDER_LINE + szdSjsfx.getFxzqdm() + ".SZ";
            // 金融产品代码
            settleTransData.setFinprodId(finprodId);
            // 金融产品类型(投管分类)
            settleTransData.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
            // 金融产品类型(估值核算)
            settleTransData.setFinprodType(DictCodeListExt1.金融产品类型_股票.getValue());
            // 业务类型
            settleTransData.setBusiType(DictCodeList.业务类型_股票交易.getValue());
            // 单位净价
            settleTransData.setUnitCprice(abs(szdSjsfx.getFxzjje()));
            // 交易份额
            settleTransData.setShareAmt(abs(szdSjsfx.getFxqrgs()));
            // GZYHTZJY-1017:净价总额未赋值,导致跑批报错 modify by fuyongsheng 20220105 start
            // 净价总额
            settleTransData.setCpriceAmt(settleTransData.getUnitCprice().multiply(settleTransData.getShareAmt()));
            // GZYHTZJY-1017:净价总额未赋值,导致跑批报错 modify by fuyongsheng 20220105 end
            // 交易金额
//            settleTransData.setTradeAmt(abs(szdSjsfx.getFxzjje()));
            //交易金额改为 单位净价 * 交易份额
            settleTransData.setTradeAmt(ArithmeticUtil.multiply(settleTransData.getUnitCprice(), settleTransData.getShareAmt()));
            // GZYHTZJY-1016:全价总额为空,导致划款指令计算金额为0,导致现金流水表金额为0 modify by fuyongsheng 20220105 start
            // 全价总额
            settleTransData.setFpriceAmt(settleTransData.getTradeAmt());
            // GZYHTZJY-1016:全价总额为空,导致划款指令计算金额为0,导致现金流水表金额为0 modify by fuyongsheng 20220105 end
            // 交易金额_原始
            settleTransData.setTradeAmtRaw(settleTransData.getTradeAmt());
            // 交易方向、业务场景明细分类
            setSharesDeal(szdSjsfx, settleTransData);
            // GZYHTZJY-986:申购和申购确认,赋值竞价平台 fuyongsheng modify 20211230 start
            // 交易平台
            settleTransData.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
            // GZYHTZJY-986:申购和申购确认,赋值竞价平台 fuyongsheng modify 20211230 start
            // add by zhangliang 20200902 汇总批处理算价格会用到全价总额
            if (StringUtil.equals(settleTransData.getPs(), DictCodeListExt2.交易方向_新股中签.getValue())){
            	settleTransData.setFpriceAmt(abs(szdSjsfx.getFxzjje()));
            }
            // GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 start
            if(DictCodeList.交易方向_申购确认.getValue().equals(settleTransData.getPs())
                    || DictCodeList.交易方向_申购.getValue().equals(settleTransData.getPs())){
                settleTransData.setFinprodId(stlDownDataDealMapper.getLastFinproductIdByApprovalNo(szdSjsfx.getFxzqdm()));
            }
            if(!assetList.contains(settleTransData.getFinprodId())) {
                // 不是债券类资产时，忽略
                return false;
            }
            // GNS
            // GNS-908:申购和申购确认需要通过申报代码查询资产代码 modify by fuyongsheng 20220218 end
            return true;
        }));
    }

    /**
     * SJSFX公共数据处理
     * @param stlDownDataDealOperatorDto StlDownDataDealWriteDto
     * @param map 核算信息
     * @return 清算处理结果
     */
    private StlDownDataDealWriteDto secTypeCommonDeal(StlDownDataDealOperatorDto stlDownDataDealOperatorDto, Map<String, List<StlDownDataDealAccountInfoDto>> map, PersonalityFunction<SzdSjsfx, SettleTransData, SettleTransDataAdd> funcion) throws ParseException {
        // 处理结果数据定义
        StlDownDataDealWriteDto stlDownDataDealWriteDto = new StlDownDataDealWriteDto();
        // 遍历 输入.ZSMX清算数据List 清算数据Entity
        for(SzdSjsfx szdSjsfx : stlDownDataDealOperatorDto.getStlDownDataDealOperatorBaseDto().getSjsfxList()) {

            // KEY = 证券账户号码 + 托管单元
            String key = szdSjsfx.getFxzqzh() + szdSjsfx.getFxtgdy();
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
            SettleTransDataAdd settleTransDataAdd = new SettleTransDataAdd();
            String generate = IDGenerator.generate();
            // 成交编号
            //settleTransData.setTradeId(szdSjsfx.getFxddbh());
            settleTransData.setTradeId(generate);
            settleTransData.setCjbh(szdSjsfx.getFxddbh());
			// 执行日期
			settleTransData.setDealDate(stlDownDataDealOperatorDto.getDealDate());
			// 证券品种
			settleTransData.setSecType(stlDownDataDealOperatorDto.getFinprodType());
            // 申请编号
            settleTransData.setApplyTradeId(szdSjsfx.getFxddbh());
            // 分支序号
            settleTransData.setBranch(0L);
            // 交易日期
            settleTransData.setTradeDate(DateUtil.parseDate(szdSjsfx.getFxfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 交割日期
            settleTransData.setSettleDate(DateUtil.parseDate(szdSjsfx.getFxfsrq(), DateUtil.DEFAULT_OTHER_FORMAT));
            // 清算速度
            settleTransData.setSettleSpeed(0L);
            if (DateUtil.compareDate(settleTransData.getTradeDate(), settleTransData.getSettleDate()) != 0){
                settleTransData.setSettleSpeed(1L);
            }
            // 交易币种
            settleTransData.setCcy(DictCodeList.币种_人民币_CNY.getValue());
            // 交易场所
            settleTransData.setTradeMarket(DictCodeList.交易流通场所_深圳证券交易所.getValue());
            // 交易市场
            settleTransData.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
            // 席位号
            settleTransData.setSeatNumber(szdSjsfx.getFxtgdy());
            // 组合代码
            settleTransData.setPortfolioId(accountInfoDtolist.get(0).getPortfolioId());
            // 证券账户代码
            settleTransData.setSecAcctId(accountInfoDtolist.get(0).getSecAcctId());
            // 资金账户
            settleTransData.setAcctNo(accountInfoDtolist.get(0).getAcctNo());
			// 临时表流水号
			settleTransData.setSeqNo(szdSjsfx.getSeqno());
            //交收结果
            settleTransData.setDeliveryResult(stlDataDealProcessCommonService.getDeliveryResult(szdSjsfx.getJgdm()));
            // 日终过来的文件结算价格就是全价的，需要反算净价总额、应计利息总额
            stlDownDataDealService.calc(settleTransData);

            if(null != funcion) {
                if(!funcion.apply(szdSjsfx, settleTransData, settleTransDataAdd)) {
                    continue;
                }
            }

            StlDownDataDealWriteBaseDto stlDownDataDealWriteBaseDto = new StlDownDataDealWriteBaseDto();
            stlDownDataDealWriteBaseDto.setSettleTransData(settleTransData);
            if (settleTransDataAdd != null && StringUtil.isNotEmpty(settleTransData.getTradeId())) {
                settleTransDataAdd.setTradeId(settleTransData.getTradeId());
                stlDownDataDealWriteBaseDto.setSettleTransDataAdd(settleTransDataAdd);
            }
            // 成交数据与投管交易关系
            SettleTransTgMapping settleTransTgMapping = new SettleTransTgMapping();
            settleTransTgMapping.setTradeId(settleTransData.getTradeId());// 取执行编号
            stlDownDataDealWriteBaseDto.setSettleTransTgMapping(settleTransTgMapping);
            stlDownDataDealWriteDto.getStlDownDataDealWriteBaseDtoList().add(stlDownDataDealWriteBaseDto);
        }
        return stlDownDataDealWriteDto;
    }

    private void setBondDeal(SzdSjsfx szdSjsfx, SettleTransData settleTransData) {
//        if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A2)) {
//            settleTransData.setPs(DictCodeList.交易方向_申购.getValue());
//            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_投标_申购.getValue());
//        }
//        else if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A3)) {
//            settleTransData.setPs(DictCodeList.交易方向_申购确认.getValue());
//            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_中签_买入.getValue());
//        }
        //债券申购和债券申购确认的判断条件修改,生成数据的业务场景明细分类修改
        if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A3)) {
            settleTransData.setPs(DictCodeList.交易方向_申购.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_网上申购_申购.getValue());
        }
        else if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A5)) {
            settleTransData.setPs(DictCodeList.交易方向_申购确认.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_债券_网上申购_申购确认.getValue());
        }
    }

    private void setSharesDeal(SzdSjsfx szdSjsfx, SettleTransData settleTransData) {
        if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A5)) {
            settleTransData.setPs(DictCodeList.交易方向_申购确认.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_新股中签_确认.getValue());
        }else if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A3)) {
            settleTransData.setPs(DictCodeList.交易方向_申购.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_新股中签_通知.getValue());
        }
        else if(StringUtil.equals(szdSjsfx.getFxywlb(), StlDataDealProcessConstant.SJSFX_YWLB_A4)) {
            settleTransData.setPs(DictCodeListExt2.交易方向_增发.getValue());
            settleTransData.setBusiSceneCalss(DictCodeListStl.业务场景分类明细_股票_公开增发申购_增发配售.getValue());
        }
    }

    private BigDecimal abs(BigDecimal value) {
        return null != value ? value.abs() : null;
    }
}
