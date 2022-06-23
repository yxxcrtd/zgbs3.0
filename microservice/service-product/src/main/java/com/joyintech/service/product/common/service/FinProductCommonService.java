package com.joyintech.service.product.common.service;

import java.lang.reflect.InvocationTargetException;
//import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.component.base.core.impl.dao.BaseGetBussinessIdMapper;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.product.common.dao.BenchMarketInfoMapper;
import com.joyintech.service.product.common.dao.FinProductFloatFeeMapper;
import com.joyintech.service.product.common.dao.FinProductGoalInfoMapper;
import com.joyintech.service.product.common.dao.FinProductRevalDocumentMapper;
import com.joyintech.service.product.common.dao.PrdInvestParamMapper;
import com.joyintech.service.product.common.dao.PrdPtlManagementInfo001Mapper;
import com.joyintech.service.product.common.dao.ProductCommonMapper;
import com.joyintech.service.product.common.dto.BussBenchMarkInfoDto;
import com.joyintech.service.product.common.dto.BussBenchMarkLayeredInfoDto;
import com.joyintech.service.product.common.dto.BussFinProductBonusPlanDto;
import com.joyintech.service.product.common.dto.BussFinProductFeeInfoDto;
import com.joyintech.service.product.common.dto.BussFinProductInvestInfoDto;
import com.joyintech.service.product.common.dto.BussFinProductInvestPeriodDto;
import com.joyintech.service.product.common.dto.BussFinProductOpenPlanDto;
import com.joyintech.service.product.common.dto.BussFinProductPayPlanDto;
import com.joyintech.service.product.common.dto.BussFinProductRevalDocumentDto;
import com.joyintech.service.product.common.dto.FinProductAnapleroticInfoDto;
import com.joyintech.service.product.common.dto.FinProductAnapleroticPlanDto;
import com.joyintech.service.product.common.dto.FinProductCommonInfoDto;
import com.joyintech.service.product.common.dto.FinProductFloatFeeDto;
import com.joyintech.service.product.common.dto.FundAccountInfoDto;
import com.joyintech.service.product.common.dto.MstTradeOpponentDto;
import com.joyintech.service.product.common.dto.PtlManagementInfoDto;
import com.joyintech.service.product.common.dto.SecuritiesAccountInfoDto;
import com.joyintech.service.product.common.dto.StlCapacctDto;
import com.joyintech.tams.component.base.core.BaseFinProductBaseInfoService;
import com.joyintech.tams.component.base.core.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.CommonUtil;
//import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.valuationinterf.ValuationInterfService;
import com.joyintech.tams.invest.common.valuationinterf.dto.BussFinProductFundAccountInfoDto;
import com.joyintech.tams.invest.common.valuationinterf.dto.ProdOtherInfoInitInputDto;

/**
 * @author : 强成西
 * @version V1.0
 * @Project: microservice
 * @Package com.joyintech.service.product.common.service
 * @date Date : 2018年09月19日 13:50
 */
@Service("backFinProductCommonService")
public class FinProductCommonService extends BaseService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    // 一体化对应etliu start
    /**
     * ValuationInterfService
     */
    @Autowired
    private ValuationInterfService valuationInterfService;
    /**
     * 交易对手
     */
//    @Autowired
//    private PrdMstTradeOpponentService mstTradeOpponent002Service;
    /**
     * FinFloatAccrualScheMapper
     */
    @Autowired
    private FinFloatAccrualScheMapper finFloatAccrualScheMapper;
    // 一体化对应etliu end

    /**
     * {@link FinProductMapper}
     */
    @Autowired(required = false)
    private FinProductMapper finProductMapper;

    /**
     * {@link ProductCommonMapper}
     */
    @Autowired(required = false)
    private ProductCommonMapper productCommonMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private PrdSaleParamMapper prdSaleParamMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private PaFinProductMapper paFinProductMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private PrdValParamMapper prdValParamMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private PtlGoalMapper ptlGoalMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private FinBaseRuleInfoMapper finBaseRuleInfoMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private BaseFinProductBaseInfoService baseFinProductBaseInfoService;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private MstIndexInfoMapper mstIndexInfoMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private FinFloatCalcRuleMapper finFloatCalcRuleMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private FinFloatCountingRuleMapper finCashCountingRuleMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinFloatCalcRuleDetailMapper finFloatCalcRuleDetailMapper;

    /**
     * {@linkplain}
     */
    // 一体化对应etliu start
//    @Autowired
//    private FinCashAccrscheMapper finCashAccrscheMapper;
    // 一体化对应etliu end

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductGoalInfoMapper finProductGoalInfoMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private PrdInvestParamMapper prdInvestParamMapper;


    /**
     * {@linkplain}
     */
    @Autowired
    private BenchMarketInfoMapper benchMarketInfoMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductFloatFeeService finProductFloatFeeService;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductFloatFeeMapper finProductFloatFeeMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private PtlManageObjMapper ptlManageObjMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private PrdFileMapper prdFileMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private FinProductAddMapper finProductAddMapper;

    /**
     * {@linkplain}
     */
    @Autowired(required = false)
    private MstFeeinfoMapper mstFeeinfoMapper;


    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductFeeInfoService finProductFeeInfoService;

    /**
     * {@linkplain}
     */
    @Autowired
    private FinProductRevalDocumentMapper finProductRevalDocumentMapper;
    @Autowired(required = false)
    private StlFinProductAcctMapper stlFinProductAcctMapper;
    @Autowired
    private PrdLayerParamMapper prdLayerParamMapper;


//    /**
//     * 注入业务主键生产类
//     */
//    @Autowired(required = false)
//    private BaseGetBussinessId baseGetBussinessId;
    /**
     * 组合扩展表Mapper
     */
    @Autowired
    private PaPtlPortfolioMapper paPtlPortfolioMapper;
    /**
     * 组合Mapper
     */
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    /**
     * 組合管理Mapper
     */
    @Autowired
    private PrdPtlManagementInfo001Mapper prdPtlManagementInfo001Mapper;
    /**
     * 组合包明细Mapper
     */
    @Autowired
    private RskPortfolioPackageDtlMapper rskPortfolioPackageDtlMapper;
    /**
     * 组合证券账户
     */
    @Autowired
    private PtlSecAcctMapper ptlSecAcctMapper;
    /**
     * 组合参数Mapper
     */
    @Autowired
    private PtlValParamMapper ptlValParamMapper;
    /**
     * 组合账户Mapper
     */
    @Autowired
    private PtlCapacctInfoMapper ptlCapacctInfoMapper;

    /**
     * 组合权限
     */
    @Autowired
    private PtlPortfolioAuthorityMapper ptlPortfolioAuthorityMapper;

    /**
     * 现金流信息
     */
    @Autowired
    private FinCashInfoMapper finCashInfoMapper;

    /**
     * 业绩回补
     */
    @Autowired
    private FinProductAnapleroticService finProductAnapleroticService;

    @Autowired
    private FinProductTypeMapper productTypeMapper;

    @Autowired
    private BaseGetBussinessIdMapper baseGetBussinessIdMapper;

    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

    @Transactional(readOnly = true)
    public List<SuggestDto> getProdId(Map<String, Object> param) {
        param.put("ISSUE_STATUS01", DictCodeListExt1.发行状态_发行成功.getValue());
        return productCommonMapper.getProdId(param);
    }

    @Transactional(readOnly = true)
    public List<SuggestDto> getGroupId(Map<String, Object> param) {
        return productCommonMapper.getGroupId(RequestUtil.getPageParam());
    }

    @Transactional(readOnly = true)
    public FinProduct queryFinProductByFinprodId(String finprodId) {
        return finProductMapper.selectByPrimaryKey(finprodId);
    }

    /**
     * 查询查询交易对手下拉框
     *
     * @param param 参数map
     * @return 查询交易对手
     * @see
     * @since 1.0
     */
    @Transactional(readOnly = true)
    public Page<SelectOptionDto> queryCounterpartyInfo(Map<String, Object> param, int page, int length) {
        param.put("counterType", DictCodeListExt1.交易对手类型_分行.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.queryCounterpartyInfo(param));
    }

    /**
     * 交易日历联想下拉框
     *
     * @param param 参数map
     * @return 交易日历
     * @see
     * @since 1.0
     */
    @Transactional(readOnly = true)
    public Page<SelectOptionDto> queryCalendarInfoList(Map<String, Object> param, int page, int length) {
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.queryCalendarInfoList(param));
    }

    /**
     * 产品代码联想下拉框
     *
     * @param param 参数map
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryProdIdInfo(Map<String, Object> param) {
        param.put("finprodTypeProd", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        return productCommonMapper.queryProdIdInfo(param);
    }

    public Page<SelectOptionDto> selectUserName(Map<String, Object> pageParam, int page, int length) {
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.selectByUserName(pageParam));
    }

    public Page<SelectOptionDto> selectUserName1(Map<String, Object> pageParam, int page, int length) {
        com.joyintech.tams.systemtools.util.CommonUtil.setInstitutionMapByType_ms(pageParam);
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.selectByUserName1(pageParam));
    }

    public Page<SelectOptionDto> getProductUserName(Map<String, Object> pageParam, int page, int length) {
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.getProductUserName(pageParam));
    }

    public Page<SelectOptionDto> getCustomerName(Map<String, Object> map, int page, int length) {
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.getCustomerName(map));

    }

    /**
     * 产品代码联想下拉框
     *
     * @param param 参数map
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryProdIdInfoIssueSuc(int page, int length, Map<String, Object> param) {
        param.put("finprodTypeProd", DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.queryProdIdInfoIssueSuc(param));
    }

    /**
     * 费用账户联想下拉框
     *
     * @param param 参数map
     * @return 费用信息
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryFeeAccountList(int page, int length, Map<String, Object> param) {
//    	param.put("acctType1_fix", DictCodeListExt1.资金账户类型_管理费账户.getValue());
//    	param.put("acctType1_trust", DictCodeListExt1.资金账户类型_托管费账户.getValue());
//    	param.put("acctType1_sale", DictCodeListExt1.资金账户类型_销售费账户.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.queryFeeAccountList(param));
    }

    /**
     * 费用代码联想下拉框
     *
     * @param param 参数map
     * @return 费用代码
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> queryFeeInfoList(int page, int length, Map<String, Object> param) {
        param.put("relationProd", DictCodeList.关联范围_产品.getValue());
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.queryFeeInfoList(param));
    }

    /**
     * 托管名称下拉框
     *
     * @param map 参数map
     * @return 托管名称
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> queryTrusterCustomerName(Map<String, Object> map) {
        return productCommonMapper.queryTrusterCustomerName(map);
    }

    /**
     * 修改标的类金融产品基本信息共通
     *
     * @param finProductCommonInfoDto 标的类金融产品基本信息
     * @throws Exception Exception
     */
    @Transactional
    public void updateFinProductCommon(FinProductCommonInfoDto finProductCommonInfoDto) throws InvocationTargetException, IllegalAccessException {
        this.deleteFinProductCommon(finProductCommonInfoDto.getFinprodId());
        if (StringUtil.equals(finProductCommonInfoDto.getBussRepFinProductInfoDto().getIsMerger(), DictCodeList.是否_否.getValue())) {
            finProductCommonInfoDto.setFinprodId(StringUtil.defaultString(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue()).concat(GlobalConstant.UNDER_LINE).concat(
                    StringUtil.defaultString(finProductCommonInfoDto.getRepFinprodInsId())));
        }
        //当更新时，更新组合资金账户信息 add by wm start 2019-10-24
//        this.updatePtlCapacctAcct(finProductCommonInfoDto); //modify by jiangpeng on 20210401 TISHKBS-1952 用于处理产品编辑时覆盖 BOK_BOOKSET_CAPACCT_INFO(账套清算账户表)的问题
        //当更新时，更新组合资金账户信息 add by wm end 2019-10-24
        this.insertFinProductCommon(finProductCommonInfoDto);
        //当更新时，更新组合的募集方式 add by wangwei5 start 2020-03-20
        this.updateRaiseType(finProductCommonInfoDto);
        //当更新时，更新组合的募集方式 add by wangwei5 end 2020-03-20
        //更新组合估值小数位
        if (finProductCommonInfoDto.getBussFinProductValuationDto() != null) {
            PtlValParam ptlValParam = new PtlValParam();
            BeanUtil.copyProperties(ptlValParam, finProductCommonInfoDto.getBussFinProductValuationDto());
            ptlValParam.setFinprodId(finProductCommonInfoDto.getPtlId());
            ptlValParamMapper.updateByPrimaryKeySelective(ptlValParam);
        }
    }

    /**
     * 更新组合的募集方式
     */
    @Transactional
    public void updateRaiseType(FinProductCommonInfoDto finProductCommonInfoDto) {
        //根据金融产品代码获取组合代码
        String raisetype = ptlPortfolioMapper.selectportfolioId(finProductCommonInfoDto.getFinprodId());
        Map<String, Object> param = new HashMap<>();
        param.put("raisetype", raisetype);
        param.put("finprodId", finProductCommonInfoDto.getFinprodId());
        ptlPortfolioMapper.updateRaiseType(param);
    }

    /**
     * 更新时跟新组合的资金账户
     *
     * @param finProductCommonInfoDto
     */
    @Transactional
    public void updatePtlCapacctAcct(FinProductCommonInfoDto finProductCommonInfoDto) {
        //首先删除组合资金账户
        productCommonMapper.deletePtlCapacctInfo(finProductCommonInfoDto.getPtlId());
        List<BussFinProductFundAccountInfoDto> fundList = finProductCommonInfoDto.getBussFinProductFundAccountInfoDto();
        for (BussFinProductFundAccountInfoDto dto : fundList) {

            String[] temp = dto.getAcctNo().split("_");
            dto.setAcctNo(temp[0]);
            if (temp.length > 1) {
                dto.setCcy(temp[1]);
            }
            //根据主键删除资金账户
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("acctNo", dto.getAcctNo());
            map.put("acctType", dto.getAcctType());
            map.put("ccy", dto.getCcy());
            map.put("portfolioId", finProductCommonInfoDto.getPtlId());
            productCommonMapper.deletePtlCapacctInfoByKey(map);

            PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
            BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, dto);
            ptlCapacctInfo.setPortfolioId(finProductCommonInfoDto.getPtlId());
            logger.debug(finProductCommonInfoDto.getPtlId() + "---" + dto.getAcctNo());
            ptlCapacctInfoMapper.insertSelective(ptlCapacctInfo);
        }
    }

    /**
     * 删除标的类金融产品基本信息共通
     *
     * @param finprodId 金融产品代码
     * @throws Exception
     */
    @Transactional
    public void deleteFinProductCommon(String finprodId) {
        // 标的类金融产品扩展表
        paFinProductMapper.deleteByPrimaryKey(finprodId);
        // 业绩基准
        benchMarketInfoMapper.deleteBaseRuleInfoByFinprodId(finprodId);
        // 浮动管理费 (先查出浮动管理费对应的现金流代码)
        String floatCashId = finProductFloatFeeService.queryFloatCashIdByFinprodId(finprodId);
        if (floatCashId != null) {
            finProductFloatFeeMapper.deleteFloatFeeCalcRuleByCashId(floatCashId);
            finProductFloatFeeMapper.deleteFloatFeeCountingRuleByCashId(floatCashId);
            finProductFloatFeeMapper.deleteFloatFeeDetailByCashId(floatCashId);
            // 一体化对应etliu start
//            finProductFloatFeeMapper.deleteFloatFeeAccrScheByCashId(floatCashId);
            // 一体化对应etliu end
            finProductFloatFeeMapper.deleteFloatAccrualScheByCashId(floatCashId);
        }

        //update zhangweiqi 删除产品费用的信息 begin
        List<BussFinProductFeeInfoDto> feeList = finProductFeeInfoService.queryFeeInfoByFinprodId(finprodId, null);
        for (BussFinProductFeeInfoDto fee : feeList) {
            finProductFloatFeeMapper.deleteFloatFeeCalcRuleByCashId(fee.getCashId());
            finProductFloatFeeMapper.deleteFloatFeeCountingRuleByCashId(fee.getCashId());
            finProductFloatFeeMapper.deleteFloatFeeDetailByCashId(fee.getCashId());
            //finProductFloatFeeMapper.deleteFloatFeeAccrScheByCashId(fee.getCashId());
            finProductFloatFeeMapper.deleteFloatAccrualScheByCashId(fee.getCashId());
        }
        //update zhangweiqi 删除产品费用的信息 end
        //交易对手 add by zhangweiqi 2019-3-5 begin
//       FinProduct finProduct= finProductMapper.selectByPrimaryKey(finprodId);
//       if(StringUtil.isNotEmpty(finProduct.getFinprodMarketId())){
//    	   MstTradeOpponentDto counterDto=new MstTradeOpponentDto();
//           counterDto.setCounterId(finProduct.getFinprodMarketId());
//    	   mstTradeOpponent002Service.deleteCounterInfo(counterDto);
//       }
        //交易对手 add by zhangweiqi 2019-3-5 end
        productCommonMapper.deleteProductAcct(finprodId);//update zhangweiqi 删除产品账户

        //产品投向 add by yanya 2019-8-23 end
        prdInvestParamMapper.deleteByPrimaryKey(finprodId);

        List<String> subProdList = productCommonMapper.getSubProd(finprodId);
        if (subProdList != null && subProdList.size() > 0) {
            for (String subId : subProdList) {
                prdLayerParamMapper.deleteByPrimaryKey(subId);//update zhangweiqi 删除产品分层信息
                // 业绩基准
                benchMarketInfoMapper.deleteBaseRuleInfoByFinprodId(subId);
                //删除子产品账户
                productCommonMapper.deleteProductAcct(subId);
                // 浮动管理费 (先查出浮动管理费对应的现金流代码)
                String subIdfloatCashId = finProductFloatFeeService.queryFloatCashIdByFinprodId(subId);
                if (subIdfloatCashId != null) {
                    finProductFloatFeeMapper.deleteFloatFeeCalcRuleByCashId(subIdfloatCashId);
                    finProductFloatFeeMapper.deleteFloatFeeCountingRuleByCashId(subIdfloatCashId);
                    finProductFloatFeeMapper.deleteFloatFeeDetailByCashId(subIdfloatCashId);
                    // 一体化对应etliu start
//              finProductFloatFeeMapper.deleteFloatFeeAccrScheByCashId(subIdfloatCashId);
                    // 一体化对应etliu end
                    finProductFloatFeeMapper.deleteFloatAccrualScheByCashId(subIdfloatCashId);
                }
                //产品代码删除业绩回补信息
                finProductAnapleroticService.deleteAnapleroticInfoByFinprodId(subId);
            }
            baseFinProductBaseInfoService.delete(subProdList);
        } else {
            //产品代码删除业绩回补信息(分层产品业绩回补删除)
            finProductAnapleroticService.deleteAnapleroticInfoByFinprodId(finprodId);
        }
        prdSaleParamMapper.deleteByPrimaryKey(finprodId);
        // 估值信息
        prdValParamMapper.deleteByPrimaryKey(finprodId);
        // 产品投向描述
        finProductGoalInfoMapper.deleteFinProdGoalInfo(finprodId);

        // 组合管理对象表
        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        if (finProductAdd != null) {
            PtlManageObjKey key = new PtlManageObjKey();
            // 一体化对应etliu
//        key.setPortfolioId(finProductAdd.getPtlId());
            key.setPortfolioId(finProductAdd.getPortfolioId());
            key.setManageObjId(finprodId);
            key.setManageObjType(DictCodeListExt1.管理对象类型_金融产品.getValue());
            ptlManageObjMapper.deleteByPrimaryKey(key);
//            //update zhangweiqi SHBS-1457  组合删除 2019-3-28 begin
//            if(StringUtil.isNotEmpty(finProductAdd.getPortfolioId())){
//                ptlValParamMapper.deleteByPrimaryKey(finProductAdd.getPortfolioId());
//            	ptlPortfolioMapper.deleteByPrimaryKey(finProductAdd.getPortfolioId());
//            	paPtlPortfolioMapper.deleteByPrimaryKey(finProductAdd.getPortfolioId());
//            	productCommonMapper.deletePtlSecAcct(finProductAdd.getPortfolioId());
//            	productCommonMapper.deletePtlPortfolioAuthority(finProductAdd.getPortfolioId());
//            	productCommonMapper.deletePtlCapacctInfo(finProductAdd.getPortfolioId());
//            	productCommonMapper.deletePtlCashInfo(finProductAdd.getPortfolioId(),DictCodeListExt1.现金流业务类型_投资组合.getValue());
//            }
//          //update zhangweiqi SHBS-1457  组合删除 2019-3-28 end
        }


        // 信息披露
        finProductRevalDocumentMapper.delete(finprodId);

        List<String> finprodIdList = new ArrayList<String>();
        finprodIdList.add(finprodId);
        baseFinProductBaseInfoService.delete(finprodIdList);
    }

    /**
     * 插入标的类金融产品基本信息共通
     *
     * @param finProductCommonInfoDto 标的类金融产品基本信息共通dto
     * @throws Exception Exception
     */
    @Transactional
    public void insertFinProductCommon(FinProductCommonInfoDto finProductCommonInfoDto) throws InvocationTargetException, IllegalAccessException {
        // 平台日期
        Date platFormDate = iPlatformDate.getSysDate();
        // 标的类金融产品基本信息基础组件
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        //add by wm 由于周期型产品期限品种与库中字段不一定，故赋值start
        if (StringUtil.isNotEmpty(finProductCommonInfoDto.getPeriod())) {
            finProductCommonInfoDto.setTermType(finProductCommonInfoDto.getPeriod());
        }
        //add by wm 由于周期型产品期限品种与库中字段不一定，故赋值end
        // 标的类金融产品entity
        if (!StringUtil.isNotEmpty(finProductCommonInfoDto.getFinprodType2())) {
            finProductCommonInfoDto.setFinprodType2(
                    DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
        }
        finProductCommonInfoDto.setFinprodType("");// TODO
        finProductCommonInfoDto.setActmdate(finProductCommonInfoDto.getMdate());
        baseFinProductBaseInfoDto.setFinProduct(finProductCommonInfoDto);

        // 金融产品附属表entity
        FinProductAdd finProductAdd = new FinProductAdd();
        BeanUtil.copyProperties(finProductAdd, finProductCommonInfoDto);

        // 一体化对应etliu start
        finProductAdd.setPortfolioId(finProductCommonInfoDto.getPtlId());
        // 一体化对应etliu end
        if (StringUtil.isNotEmpty(finProductCommonInfoDto.getRedSpeed())) {
            finProductAdd.setRedSpeed(new Long(finProductCommonInfoDto.getRedSpeed()));
        } else {
            finProductAdd.setRedSpeed(finProductAdd.getPurSpeed());
        }
        //add by zhangweiqi 净值化产品状态修改
        if (StringUtil.equals(finProductCommonInfoDto.getPageId(), "FinProductBuild002") ||
                StringUtil.equals(finProductCommonInfoDto.getPageId(), "FinNetValueProductBuild002")) {
            finProductAdd.setIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
        } else {
            finProductAdd.setTmplId(finProductCommonInfoDto.getFinprodId());
        }
        //add by wm 增加周期型产品处理 2019-09-29 start
        if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())) {
            finProductAdd.setIsCycle(DictCodeList.是否_是.getValue());
            finProductAdd.setIsLay(DictCodeList.是否_是.getValue());
            finProductCommonInfoDto.getBussFinProductOpenPlanDto().setOpenType(DictCodeList.申赎开放类型_申购开放.getValue());
        }
        //add by wm 增加周期型产品处理 2019-09-29 end
        baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);

        // 现金流信息表dtolist
        baseFinProductBaseInfoDto.setFinCashInfoList(new ArrayList<>());
        // 现金流计算规则dtolist
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(new ArrayList<>());
        // 现金流计提规则dtolist
        baseFinProductBaseInfoDto.setFinCashCountingRuleList(new ArrayList<>());
        // 现金流利率信息dtolist
        baseFinProductBaseInfoDto.setFinRateInfoList(new ArrayList<>());
        // 现金流支付规则dtolist
        baseFinProductBaseInfoDto.setFinCashPayRuleList(new ArrayList<>());
        // 现金流计划dtolist
        baseFinProductBaseInfoDto.setFinCashPlanList(new ArrayList<>());
        // 开放规则dtolist
        baseFinProductBaseInfoDto.setFinOpenRuleList(new ArrayList<>());
        // 开放计划dtolist
        baseFinProductBaseInfoDto.setFinOpenPlanList(new ArrayList<>());

        //update zhangweiqi 分层产品begin
        FinCashInfo pCashInfo = null;
        FinCashPayRule pCashPayRule = null;
//        List<FinCashPlan> pCashPlanList = new ArrayList<FinCashPlan>();
        FinRateInfo pRateinfo = null;
        ////update zhangweiqi 分层产品end
        HashMap<String, String> cashMap = new HashMap<String, String>();//费用和现金流的关系  add by zhangweiqi
        if (finProductCommonInfoDto.getBussFinProductBonusPlanDto() != null) {
            // 现金流信息dtolist-->分红
            String cashId_int = CommonUtil.generateCashFlowCode(
                    finProductCommonInfoDto.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_利息.getValue());
            FinCashInfo finCashInfo_int = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo_int, finProductCommonInfoDto);
            finCashInfo_int.setCashId(cashId_int);
            finCashInfo_int.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            finCashInfo_int.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            finCashInfo_int.setCcy(finProductCommonInfoDto.getCcy());
            finCashInfo_int.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finCashInfo_int.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo_int.setBranch(finProductCommonInfoDto.getBranch());
            this.checkDealModeIsCalc(finCashInfo_int, finProductCommonInfoDto.getDealMode());
            baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_int);

            // 现金流支付规则dtolist-->分红
            BussFinProductBonusPlanDto bussFinProductBonusPlanDto = finProductCommonInfoDto.getBussFinProductBonusPlanDto();
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            BeanUtil.copyProperties(finCashPayRule, finProductCommonInfoDto);
            finCashPayRule.setCashId(cashId_int);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashPayRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashPayRule.setEffDate(platFormDate);
            }
            if (StringUtil.isNotEmpty(bussFinProductBonusPlanDto.getPayCycleVal())) {
                finCashPayRule.setPayCycle(bussFinProductBonusPlanDto.getPayCycleUnit());
            } else {
                finCashPayRule.setPayCycle(bussFinProductBonusPlanDto.getPayCycle());
            }
            finCashPayRule.setPayCycleVal(bussFinProductBonusPlanDto.getPayCycleVal());
            finCashPayRule.setEndTdayRule(bussFinProductBonusPlanDto.getEndTdayRule());
            finCashPayRule.setPayTdayRule(bussFinProductBonusPlanDto.getPayTdayRule());
            finCashPayRule.setCalcRule(bussFinProductBonusPlanDto.getCalcRule());
            finCashPayRule.setFirstPayDate(bussFinProductBonusPlanDto.getFirstPayDate());
            finCashPayRule.setPayType(bussFinProductBonusPlanDto.getPayType());
            finCashPayRule.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finCashPayRule.setFinprodType(finProductCommonInfoDto.getFinprodType());
            finCashPayRule.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
            finCashPayRule.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);

            if (StringUtil.equals(finProductCommonInfoDto.getIsLay(),
                    DictCodeList.是否_否.getValue())) {
                // 现金流计算规则dtolist-->分红
                FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
                BeanUtil.copyProperties(finCashCalcRule, finProductCommonInfoDto);
                finCashCalcRule.setCashId(cashId_int);
                if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                    finCashCalcRule.setEffDate(finProductCommonInfoDto.getVdate());
                    // 一体化对应etliu
                    // finCashCalcRule.setRelaeseDate(finProductCommonInfoDto.getVdate());
                } else {
                    finCashCalcRule.setEffDate(platFormDate);
                    // 一体化对应etliu
                    // finCashCalcRule.setRelaeseDate(platFormDate);
                }
                finCashCalcRule.setCalcType(finProductCommonInfoDto.getCalcType());
                finCashCalcRule.setBasis(finProductCommonInfoDto.getBasis());
                finCashCalcRule.setYield(
                        ArithmeticUtil.divide(finProductCommonInfoDto.getYield(), 100, 14));
                finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
                finCashCalcRule.setFinprodId(finProductCommonInfoDto.getFinprodId());
                finCashCalcRule.setFinprodType(finProductCommonInfoDto.getFinprodType());
                finCashCalcRule.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
                finCashCalcRule.setBranch(finProductCommonInfoDto.getBranch());
                baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(finCashCalcRule);
            }
            //update zhangweiqi  新增分红计划信息 2019-02-12 begin
            if (!StringUtil.isEmptyList(bussFinProductBonusPlanDto.getCashPlanList())) {
                for (int i = 0; i < bussFinProductBonusPlanDto.getCashPlanList().size(); i++) {
                    FinCashPlan finCashPlan = bussFinProductBonusPlanDto.getCashPlanList().get(i);
                    finCashPlan.setUpdateUser(finProductCommonInfoDto.getUpdateUser());
                    finCashPlan.setCreateUser(finProductCommonInfoDto.getCreateUser());
                    finCashPlan.setCreateDept(finProductCommonInfoDto.getCreateDept());
                    finCashPlan.setCashId(cashId_int);
                    finCashPlan.setCashNum(new Long(i + 1));
                    finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
                    finCashPlan.setVdateUnadjust(finCashPlan.getVdate());
                    finCashPlan.setMdateUnadjust(finCashPlan.getMdate());
                    finCashPlan.setPayDateUnadjust(finCashPlan.getPayDate());
                    finCashPlan.setFinprodId(finProductCommonInfoDto.getFinprodId());
                    finCashPlan.setFinprodType(finProductCommonInfoDto.getFinprodType());
                    finCashPlan.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
                    finCashPlan.setBranch(finProductCommonInfoDto.getBranch());
                    baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);
                }
            }
            //update zhangweiqi  新增分红计划信息 2019-02-12 end
            // 现金流利率信息
            FinRateInfo finRateInfo = new FinRateInfo();
            BeanUtil.copyProperties(finRateInfo, finProductCommonInfoDto);
            finRateInfo.setCashId(cashId_int);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finRateInfo.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finRateInfo.setEffDate(platFormDate);
            }
            // 一体化对应etliu
//            finRateInfo.setRateType(finProductCommonInfoDto.getCalcType());
            finRateInfo.setIntType(finProductCommonInfoDto.getCalcType());
            finRateInfo.setBasis(finProductCommonInfoDto.getBasis());
            finRateInfo.setRate(
                    ArithmeticUtil.divide(finProductCommonInfoDto.getYield(), 100, 14));
            finRateInfo.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finRateInfo.setFinprodType(finProductCommonInfoDto.getFinprodType());
            finRateInfo.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
            finRateInfo.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinRateInfoList().add(finRateInfo);
            pCashInfo = finCashInfo_int;
            pCashPayRule = finCashPayRule;
            pRateinfo = finRateInfo;
        }
        // 现金流支付规则dtolist-->付息
        if (finProductCommonInfoDto.getBussFinProductPayPlanDto() != null) {
            // 现金流信息dtolist-->利息
            String cashId_int = CommonUtil.generateCashFlowCode(
                    finProductCommonInfoDto.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_利息.getValue());
            FinCashInfo finCashInfo_int = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo_int, finProductCommonInfoDto);
            finCashInfo_int.setCashId(cashId_int);
            finCashInfo_int.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            finCashInfo_int.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            finCashInfo_int.setCcy(finProductCommonInfoDto.getCcy());
            finCashInfo_int.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finCashInfo_int.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo_int.setBranch(finProductCommonInfoDto.getBranch());
            this.checkDealModeIsCalc(finCashInfo_int, finProductCommonInfoDto.getDealMode());
            baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_int);

            // 现金流支付规则dtolist-->利息
            BussFinProductPayPlanDto bussFinProductPayPlanDto = finProductCommonInfoDto.getBussFinProductPayPlanDto();
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            BeanUtil.copyProperties(finCashPayRule, finProductCommonInfoDto);
            finCashPayRule.setCashId(cashId_int);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashPayRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashPayRule.setEffDate(platFormDate);
            }
            if (StringUtil.isNotEmpty(bussFinProductPayPlanDto.getPayCycleVal())) {
                finCashPayRule.setPayCycle(bussFinProductPayPlanDto.getPayCycleUnit());
            } else {
                finCashPayRule.setPayCycle(bussFinProductPayPlanDto.getPayCycle());
            }
            finCashPayRule.setPayCycleVal(bussFinProductPayPlanDto.getPayCycleVal());
            finCashPayRule.setEndTdayRule(bussFinProductPayPlanDto.getEndTdayRule());
            finCashPayRule.setPayTdayRule(bussFinProductPayPlanDto.getPayTdayRule());
            finCashPayRule.setCalcRule(bussFinProductPayPlanDto.getCalcRule());
            finCashPayRule.setFirstPayDate(bussFinProductPayPlanDto.getFirstPayDate());
            finCashPayRule.setPayType(bussFinProductPayPlanDto.getPayType());
            finCashPayRule.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finCashPayRule.setFinprodType(finProductCommonInfoDto.getFinprodType());
            finCashPayRule.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
            finCashPayRule.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);

            if (StringUtil.equals(finProductCommonInfoDto.getIsLay(),
                    DictCodeList.是否_否.getValue())) {
                // 现金流计算规则dtolist-->利息
                FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
                BeanUtil.copyProperties(finCashCalcRule, finProductCommonInfoDto);
                finCashCalcRule.setCashId(cashId_int);
                if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                    finCashCalcRule.setEffDate(finProductCommonInfoDto.getVdate());
                    // 一体化对应etliu
                    // finCashCalcRule.setRelaeseDate(finProductCommonInfoDto.getVdate());
                } else {
                    finCashCalcRule.setEffDate(platFormDate);
                    // 一体化对应etliu
                    // finCashCalcRule.setRelaeseDate(platFormDate);
                }
                finCashCalcRule.setCalcType(finProductCommonInfoDto.getCalcType());
                finCashCalcRule.setBasis(finProductCommonInfoDto.getBasis());
                finCashCalcRule.setYield(
                        ArithmeticUtil.divide(finProductCommonInfoDto.getYield(), 100, 14));
                finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
                finCashCalcRule.setFinprodId(finProductCommonInfoDto.getFinprodId());
                finCashCalcRule.setFinprodType(finProductCommonInfoDto.getFinprodType());
                finCashCalcRule.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
                finCashCalcRule.setBranch(finProductCommonInfoDto.getBranch());
                baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(finCashCalcRule);
            }

            if (!StringUtil.isEmptyList(bussFinProductPayPlanDto.getCashPlanList())) {
                for (int i = 0; i < bussFinProductPayPlanDto.getCashPlanList().size(); i++) {
                    FinCashPlan finCashPlan = bussFinProductPayPlanDto.getCashPlanList().get(i);
                    finCashPlan.setUpdateUser(finProductCommonInfoDto.getUpdateUser());
                    finCashPlan.setCreateUser(finProductCommonInfoDto.getCreateUser());
                    finCashPlan.setCreateDept(finProductCommonInfoDto.getCreateDept());
                    finCashPlan.setCashId(cashId_int);
                    finCashPlan.setCashNum(new Long(i + 1));
                    finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
                    finCashPlan.setVdateUnadjust(finCashPlan.getVdate());
                    finCashPlan.setMdateUnadjust(finCashPlan.getMdate());
                    finCashPlan.setPayDateUnadjust(finCashPlan.getPayDate());
                    finCashPlan.setFinprodId(finProductCommonInfoDto.getFinprodId());
                    finCashPlan.setFinprodType(finProductCommonInfoDto.getFinprodType());
                    finCashPlan.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
                    finCashPlan.setBranch(finProductCommonInfoDto.getBranch());
                    baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);
                }
            }

            // 现金流利率信息
            FinRateInfo finRateInfo = new FinRateInfo();
            BeanUtil.copyProperties(finRateInfo, finProductCommonInfoDto);
            finRateInfo.setCashId(cashId_int);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finRateInfo.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finRateInfo.setEffDate(platFormDate);
            }
            // 一体化对应etliu
//            finRateInfo.setRateType(finProductCommonInfoDto.getCalcType());
            finRateInfo.setIntType(finProductCommonInfoDto.getCalcType());
            finRateInfo.setBasis(finProductCommonInfoDto.getBasis());
            finRateInfo.setRate(
                    ArithmeticUtil.divide(finProductCommonInfoDto.getYield(), 100, 14));
            finRateInfo.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finRateInfo.setFinprodType(finProductCommonInfoDto.getFinprodType());
            finRateInfo.setFinprodType2(finProductCommonInfoDto.getFinprodType2());
            finRateInfo.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinRateInfoList().add(finRateInfo);
            //update zhangweiqi begin
            pCashInfo = finCashInfo_int;
            pCashPayRule = finCashPayRule;
            pRateinfo = finRateInfo;
        }
        // 费用
        if (finProductCommonInfoDto.getBussFinProductFeeInfoDto() != null && !StringUtil.isEmptyList(finProductCommonInfoDto.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())) {
            List<BussFinProductFeeInfoDto> feeInfoList = finProductCommonInfoDto.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
            boolean flag = true;
            //如果净值型产品分档 就不存母产品费用信息，因为净值型产品分档费用对应到分层子产品
            if (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), finProductCommonInfoDto.getDealMode()) &&
                    StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType())) {
                flag = false;
            }
            for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeInfoList) {
                //组装费用相关信息
                this.procFinProductFeeInfoData(finProductCommonInfoDto, baseFinProductBaseInfoDto, bussFinProductFeeInfoDto, cashMap, flag);
            }
        }


        // 开放计划（需要在浮动管理费前生成）
        this.procFinProductOpenPlan(finProductCommonInfoDto, baseFinProductBaseInfoDto);

        //回补信息不为空
        if (!StringUtil.isEmptyList(finProductCommonInfoDto.getFinProductAnapleroticTabInfoDtoList())) {
            for (FinProductAnapleroticInfoDto anapleroticTabDto : finProductCommonInfoDto.getFinProductAnapleroticTabInfoDtoList()) {
                FinProductAnapleroticInfoDto anapleroticDto = anapleroticTabDto.getFinProductAnapleroticInfoDto();
                String anapleroticFinprodId = "";
                if (StringUtil.isNotEmpty(anapleroticDto.getProdStyle())
                        && StringUtil.equals(finProductCommonInfoDto.getDealMode(), DictCodeList.处理模式_分档.getValue())) {
                    anapleroticFinprodId = finProductCommonInfoDto.getFinprodId() + "_" + anapleroticDto.getProdStyle();
                } else {
                    anapleroticFinprodId = finProductCommonInfoDto.getFinprodId();
                }
                // 现金流信息dtolist-->回补
                String anaplerotic_int = CommonUtil.generateCashFlowCode(
                        anapleroticFinprodId,
                        BussinessDictCodeList.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                        DictCodeListExt1.现金流二级类型_回补.getValue());
                FinCashInfo finCashInfo_int = new FinCashInfo();
                finCashInfo_int.setCashId(anaplerotic_int);
                finCashInfo_int.setCashType1(DictCodeListExt1.现金流一级类型_回补.getValue());
                finCashInfo_int.setCashType2(DictCodeListExt1.现金流二级类型_回补.getValue());
                finCashInfo_int.setCcy(finProductCommonInfoDto.getCcy());
                finCashInfo_int.setFinprodId(anapleroticFinprodId);
                finCashInfo_int.setFinprodType(BussinessDictCodeList.现金流业务类型_金融产品.getValue());
                finCashInfo_int.setBranch(finProductCommonInfoDto.getBranch());
                finCashInfo_int.setIsCalc(BussinessDictCodeList.是否_是.getValue());
                baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_int);
                anapleroticDto.setAnapleroticId(anaplerotic_int);
                anapleroticDto.setFinprodId(anapleroticFinprodId);
                if (null == anapleroticDto.getEffDate()) {
                    //修改bug BS2.1.0 BSTOZ-964  EffDate为null导致后续入表报错 add by wangcheng 20191030
                    anapleroticDto.setEffDate(finProductCommonInfoDto.getVdate());
                }
                List<FinProductAnapleroticPlanDto> anapleroticPlanList = new ArrayList<FinProductAnapleroticPlanDto>();
                //是否高频
                boolean isHighOpen = false;
                //回补计提计划生成
                if (StringUtil.equals(DictCodeList.开放类型_开放式.getValue(), finProductCommonInfoDto.getOperationType()) &&
                        !StringUtil.isEmptyList(baseFinProductBaseInfoDto.getFinOpenRuleList())) {
                    FinOpenRule finOpenRuleBak = baseFinProductBaseInfoDto.getFinOpenRuleList().get(0);
                    if (!StringUtil.equals(DictCodeList.开放模式_高频开放.getValue(), finOpenRuleBak.getOpenMode())) {
                        List<Date> busiDate = new ArrayList<Date>();
                        BussFinProductOpenPlanDto bussFinProductOpenPlanDto = finProductCommonInfoDto.getBussFinProductOpenPlanDto();
                        BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto = finProductCommonInfoDto.getBussFinProductOpenPlanRedDto();
                        if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType())) {
                            if (bussFinProductOpenPlanDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanDto.getFinOpenPlanList()) {
                                    addDateToList(busiDate, plan.getOpenNdate());
                                }
                            }
                            if (bussFinProductOpenPlanRedDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanRedDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanRedDto.getFinOpenPlanList()) {
                                    addDateToList(busiDate, plan.getOpenNdate());
                                }
                            }
                        } else if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProductCommonInfoDto.getProfitType())) {
                            if (bussFinProductOpenPlanDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanDto.getFinOpenPlanList()) {

                                    addDateToList(busiDate, plan.getOpenDateEnd());
                                }
                            }
                            if (bussFinProductOpenPlanRedDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanRedDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanRedDto.getFinOpenPlanList()) {
                                    addDateToList(busiDate, plan.getOpenDateEnd());
                                }
                            }
                        }

//                         addDateToList(busiDate,finProductCommonInfoDto.getVdate());
                        addDateToList(busiDate, finProductCommonInfoDto.getMdate());
                        addDateToList(busiDate, anapleroticDto.getFirstAnapleroticDate());
                        addDateToList(busiDate, anapleroticDto.getAnapleroticEndDate());
                        addDateToList(busiDate, anapleroticDto.getFirstAnapleroticCycleBegDate());
                        Date startDate = busiDate.get(0);
                        for (Date endDate : busiDate) {
                            if (startDate.compareTo(endDate) == 0) {
                                startDate = endDate;
                                continue;
                            }
                            if (endDate.compareTo(anapleroticDto.getFirstAnapleroticDate()) < 0) {
                                startDate = endDate;
                                continue;
                            }
                            if (endDate.compareTo(anapleroticDto.getAnapleroticEndDate()) > 0) {
                                startDate = endDate;
                                continue;
                            }
                            FinProductAnapleroticPlanDto finProductAnapleroticPlanDto = new FinProductAnapleroticPlanDto();
//                             finProductAnapleroticPlanDto.setCashId(cashId_floatFee);
                            finProductAnapleroticPlanDto.setAnapleroticDate(endDate);
                            //现金流计提计划.计提开始日 = 画面回补计划列表.回补周期开始日
                            finProductAnapleroticPlanDto.setAnapleroticCycleStartDate(startDate);
                            //现金流计提计划.计提结束日 = 画面回补计划列表.回补周期结束日
                            finProductAnapleroticPlanDto.setAnapleroticCycleEndDate(endDate);
                            //现金流计提计划.调整前结束日 = 画面回补计划列表.回补周期结束日
                            finProductAnapleroticPlanDto.setAnapleroticCycleEndDate(endDate);
                            anapleroticPlanList.add(finProductAnapleroticPlanDto);
                            startDate = endDate;
                        }
                    } else {
                        isHighOpen = true;
                    }
                } else {
                    FinProductAnapleroticPlanDto finProductAnapleroticPlanDto = new FinProductAnapleroticPlanDto();
                    finProductAnapleroticPlanDto.setAnapleroticDate(anapleroticDto.getFirstAnapleroticDate());
                    finProductAnapleroticPlanDto.setAnapleroticCycleStartDate(anapleroticDto.getFirstAnapleroticCycleBegDate());
                    finProductAnapleroticPlanDto.setAnapleroticCycleEndDate(anapleroticDto.getAnapleroticEndDate());
                    finProductAnapleroticPlanDto.setAnapleroticCycleEndDate(anapleroticDto.getAnapleroticEndDate());
                    anapleroticPlanList.add(finProductAnapleroticPlanDto);
                }

                anapleroticDto.setFinProductAnapleroticPlanList(anapleroticPlanList);
                finProductAnapleroticService.insertProductAnapleroticInfo(anapleroticDto, finProductCommonInfoDto, isHighOpen);
            }

        }
        if (StringUtil.equals(finProductCommonInfoDto.getProfitType(),
                DictCodeList.收益类型_收益型.getValue())) {
            FinCashInfo finCashInfo_floatFee = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo_floatFee, finProductCommonInfoDto);
            finCashInfo_floatFee.setCashId(CommonUtil.generateCashFlowCode(
                    finProductCommonInfoDto.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
            finCashInfo_floatFee.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
            finCashInfo_floatFee.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            finCashInfo_floatFee.setCcy(finProductCommonInfoDto.getCcy());
            finCashInfo_floatFee.setFinprodId(finProductCommonInfoDto.getFinprodId());
            finCashInfo_floatFee.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo_floatFee.setBranch(finProductCommonInfoDto.getBranch());
            //获取浮动差额中收feeid
            String feeid = productCommonMapper.getFeeIdByFeeType(DictCodeList.费用品种类型_浮动差额中收.getValue());
            finCashInfo_floatFee.setFeeId(feeid);
            baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_floatFee);
        }
        // 预期型产品特殊处理

        // // 产品新建特殊处理, 生成现金流信息(现金流一级类型:费用, 现金流二级类型:税额)
        // if (StringUtil.equals(finProductCommonInfoDto.getPageId(), "FinProductBuild002")) {
        // FinCashInfo finCashInfo_tax = new FinCashInfo();
        // BeanUtil.copyProperties(finCashInfo_tax, finProductCommonInfoDto);
        // finCashInfo_tax.setCashId(CommonUtil.generateCashFlowCode(
        // finProductCommonInfoDto.getFinprodId(),
        // DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
        // DictCodeListExt1.现金流二级类型_税额.getValue()));
        // finCashInfo_tax.setCashType1(DictCodeListExt1.现金流一级类型_税额.getValue());
        // finCashInfo_tax.setCashType2(DictCodeListExt1.现金流二级类型_税额.getValue());
        // finCashInfo_tax.setCcy(finProductCommonInfoDto.getCcy());
        // finCashInfo_tax.setFinprodId(finProductCommonInfoDto.getFinprodId());
        // finCashInfo_tax.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
        // finCashInfo_tax.setBranch(finProductCommonInfoDto.getBranch());
        // finCashInfoList.add(finCashInfo_tax);
        // }

        // 标的类金融产品扩展表
        PaFinProduct paFinProduct = new PaFinProduct();
        BeanUtil.copyProperties(paFinProduct, finProductCommonInfoDto);
        paFinProductMapper.insert(paFinProduct);
        /*guyunfei 20191111 金融产品扩展表字段迁移 start*/
        FinProductType productType = new FinProductType();
        BeanUtil.copyProperties(productType, paFinProduct);
        productType.setBranch(0L);
        productType.setType6(paFinProduct.getProdSeries());
        productType.setType7(paFinProduct.getProdClass());
        productType.setType8(paFinProduct.getProdFeature());
        productTypeMapper.insertSelective(productType);
        /*guyunfei 20191111 金融产品扩展表字段迁移 end*/

        // 一体化对应etliu start
        String resetType = null; // 业绩基准重置规则
        // 一体化对应etliu end

        // 业绩基准
        if (finProductCommonInfoDto.getBussBenchMarkInfoDto() != null
                //如果净值型产品分档 就不存母产品业绩基准，因为净值型产品分档 业绩基准对应到分层子产品
                //净值型周期型产品的业绩基准对应期限层
                && !(StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType())
                && (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), finProductCommonInfoDto.getDealMode())
                || StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())))) {
            FinBaseRuleInfo finBaseRuleInfo = finProductCommonInfoDto.getBussBenchMarkInfoDto();
            if (DictCodeListExt1.披露类型_区间披露.getValue().equals(finBaseRuleInfo.getPublishType()) || StringUtil.isNotEmpty(finBaseRuleInfo.getRateType())) {
                String baseRuleId = CommonUtil.generateCashFlowCode(finProductCommonInfoDto.getFinprodId(), null, finProductCommonInfoDto.getBranch(), DictCodeListExt1.业绩基准类型_产品披露.getValue());
                finBaseRuleInfo.setBaseRuleId(baseRuleId);
                finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
                finBaseRuleInfo.setRateLower(ArithmeticUtil.divide(finBaseRuleInfo.getRateLower(), 100, 14));
                finBaseRuleInfo.setRateLimit(ArithmeticUtil.divide(finBaseRuleInfo.getRateLimit(), 100, 14));
                finBaseRuleInfo.setBranch(finProductCommonInfoDto.getBranch());
                finBaseRuleInfo.setRate(ArithmeticUtil.divide(finBaseRuleInfo.getRate(), 100, 14));
                if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                    finBaseRuleInfo.setEffDate(finProductCommonInfoDto.getVdate());
                } else {
                    finBaseRuleInfo.setEffDate(platFormDate);
                }
                if (StringUtil.equals(finBaseRuleInfo.getRateType(),
                        DictCodeList.利率类型_浮动利率.getValue())) {
                    MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                            finBaseRuleInfo.getIndexId());
                    if (mstIndexInfo != null) {
                        finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                    } else {
                        finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                    }
                }
                finBaseRuleInfo.setFinprodId(finProductCommonInfoDto.getFinprodId());
                finBaseRuleInfo.setSpreadRate(
                        ArithmeticUtil.divide(finBaseRuleInfo.getSpreadRate(), 100, 14));

                // 一体化对应etliu start
                resetType = finBaseRuleInfo.getResetType(); // 业绩基准重置规则
                // 一体化对应etliu end

                finBaseRuleInfoMapper.insert(finBaseRuleInfo);
            }
        }
        // 浮动管理费
        if (finProductCommonInfoDto.getBussFinProductFloatFeeDto() != null) {
            FinProductFloatFeeDto finProductFloatFeeDto = finProductCommonInfoDto.getBussFinProductFloatFeeDto();
            //如果净值型产品分档 就不存母产品浮动管理费，因为净值型产品分档浮动管理费对应到分层子产品-- 净值型周期型产品浮动管理费对应期限层
            if (!(StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType())
                    && (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), finProductCommonInfoDto.getDealMode())
                    || StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())))) {
                //浮动管理费用处理
                this.procFinProductFloatFeeInfoData(finProductCommonInfoDto, baseFinProductBaseInfoDto, finProductFloatFeeDto, resetType, cashMap);
            }

            if (finProductCommonInfoDto.getBussBenchMarkFloatInfoDto() != null
                    //如果净值型产品分档 就不存母产品业绩基准，因为净值型产品分档 业绩基准对应到分层子产品 -- 净值型周期型产品业绩基准对应期限层
                    && !(StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType())
                    && (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), finProductCommonInfoDto.getDealMode())
                    || StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())))) {
                FinBaseRuleInfo finBaseRuleInfo = finProductCommonInfoDto.getBussBenchMarkFloatInfoDto();

                if (StringUtil.isNotEmpty(finBaseRuleInfo.getRateType())) {
                    String baseRuleId = CommonUtil.generateCashFlowCode(finProductCommonInfoDto.getFinprodId(), null, finProductCommonInfoDto.getBranch(), DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
                    finBaseRuleInfo.setBaseRuleId(baseRuleId);
                    finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.披露类型_单值披露.getValue());
                    finBaseRuleInfo.setBranch(finProductCommonInfoDto.getBranch());
                    finBaseRuleInfo.setRate(ArithmeticUtil.divide(finBaseRuleInfo.getRate(), 100, 14));
                    if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                        finBaseRuleInfo.setEffDate(finProductCommonInfoDto.getVdate());
                    } else {
                        finBaseRuleInfo.setEffDate(platFormDate);
                    }
                    if (StringUtil.equals(finBaseRuleInfo.getRateType(),
                            DictCodeList.利率类型_浮动利率.getValue())) {
                        MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                                finBaseRuleInfo.getIndexId());
                        if (mstIndexInfo != null) {
                            finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                        } else {
                            finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                        }
                    }
                    finBaseRuleInfo.setFinprodId(finProductCommonInfoDto.getFinprodId());
                    finBaseRuleInfo.setSpreadRate(
                            ArithmeticUtil.divide(finBaseRuleInfo.getSpreadRate(), 100, 14));

                    // 一体化对应etliu start
                    resetType = finBaseRuleInfo.getResetType(); // 业绩基准重置规则
                    // 一体化对应etliu end

                    finBaseRuleInfoMapper.insert(finBaseRuleInfo);
                }
            }

        }

        List<BaseFinProductBaseInfoDto> list = new ArrayList<BaseFinProductBaseInfoDto>();
        list.add(baseFinProductBaseInfoDto);
        baseFinProductBaseInfoService.insert(list);
        // 理财产品销售参数
        if (finProductCommonInfoDto.getBussFinProductSaleParamDto() != null) {
            PrdSaleParam prdSaleParam = finProductCommonInfoDto.getBussFinProductSaleParamDto();
            prdSaleParam.setFinprodId(finProductCommonInfoDto.getFinprodId());
            prdSaleParam.setFirstSaleVdate(finProductCommonInfoDto.getFirstSaleVdate());
            prdSaleParam.setFirstSaleMdate(finProductCommonInfoDto.getFirstSaleMdate());
            //募集金额 募集上限 募集下限 页面改为元 yzg 20191126 start
//            prdSaleParam.setSaleMax(
//                ArithmeticUtil.multiply(prdSaleParam.getSaleMax(), new BigDecimal(10000)));
//            prdSaleParam.setSaleMin(
//                ArithmeticUtil.multiply(prdSaleParam.getSaleMin(), new BigDecimal(10000)));
//            prdSaleParam.setRaiseAmtPlan(
//                    ArithmeticUtil.multiply(prdSaleParam.getRaiseAmtPlan(), new BigDecimal(10000)));
            //募集金额 募集上限 募集下限 页面改为元 yzg 20191126 end
            prdSaleParam.setHugeRedRatio(
                    ArithmeticUtil.divide(prdSaleParam.getHugeRedRatio(), 100, 14));
            prdSaleParam.setMaxPlgeRate(
                    ArithmeticUtil.divide(prdSaleParam.getMaxPlgeRate(), 100, 14));
            //update zhangweiqi 新增销售参数  begin
            prdSaleParam.setBeforeendFlag(finProductCommonInfoDto.getBeforeEndFlag());
            prdSaleParam.setBeforeestablishFlag(finProductCommonInfoDto.getBeforeEstablishFlag());
            prdSaleParam.setConsignmentFlag(finProductCommonInfoDto.getConsignmentFlag());
            prdSaleParam.setContinueFlag(finProductCommonInfoDto.getContinueFlag());
            prdSaleParam.setDefaultredFlag(finProductCommonInfoDto.getDefaultRedFlag());
            prdSaleParam.setRedFlag(finProductCommonInfoDto.getRedFlag());
            //update zhangweiqi 新增销售参数  end
            prdSaleParamMapper.insert(prdSaleParam);
        }
        // 估值信息
        if (finProductCommonInfoDto.getBussFinProductValuationDto() != null) {
            PrdValParam PrdValParam = finProductCommonInfoDto.getBussFinProductValuationDto();
            PrdValParam.setFinprodId(finProductCommonInfoDto.getFinprodId());
            prdValParamMapper.insert(PrdValParam);
        }

        //产品投向 add by yanya 20190823
        if (finProductCommonInfoDto.getBussFinProductInvestDto() != null) {
            BussFinProductInvestInfoDto bussFinProductInvestInfoDto = finProductCommonInfoDto.getBussFinProductInvestDto();
            bussFinProductInvestInfoDto.setFinprodId(finProductCommonInfoDto.getFinprodId());
            prdInvestParamMapper.insert(bussFinProductInvestInfoDto);
        }
        // 产品投向描述
        if (finProductCommonInfoDto.getBussFinProductGoalInfoDto() != null) {
            if (!StringUtil.isEmptyList(
                    finProductCommonInfoDto.getBussFinProductGoalInfoDto().getFinProductPtlGoalDtoList())) {
                for (PtlGoal ptlGoal : finProductCommonInfoDto.getBussFinProductGoalInfoDto().getFinProductPtlGoalDtoList()) {
                    BeanUtil.copyProperties(ptlGoal, finProductCommonInfoDto);
                    ptlGoal.setInvestMax(ArithmeticUtil.divide(ptlGoal.getInvestMax(), 100, 14));
                    ptlGoal.setInvestMin(ArithmeticUtil.divide(ptlGoal.getInvestMin(), 100, 14));
                    ptlGoal.setFinprodId(finProductCommonInfoDto.getFinprodId());
                    ptlGoalMapper.insert(ptlGoal);
                }
            }
        }
        // 信息披露
        if (finProductCommonInfoDto.getBussFinProductRevalDocumentDto() != null) {
            if (!StringUtil.isEmptyList(
                    finProductCommonInfoDto.getBussFinProductRevalDocumentDto().getFinProductRevalDocumentDtoList())) {
                for (BussFinProductRevalDocumentDto bussFinProductRevalDocumentDto : finProductCommonInfoDto.getBussFinProductRevalDocumentDto().getFinProductRevalDocumentDtoList()) {
                    PrdFile prdFile = new PrdFile();
                    BeanUtil.copyProperties(prdFile, finProductCommonInfoDto);
                    prdFile.setFinprodId(finProductCommonInfoDto.getFinprodId());
                    prdFile.setFileType(bussFinProductRevalDocumentDto.getFileType());
                    prdFile.setRevealFileId(bussFinProductRevalDocumentDto.getFileName());
                    prdFile.setRemark(bussFinProductRevalDocumentDto.getRemark());
                    prdFileMapper.insert(prdFile);
                }
            }
        }

        // 组合管理对象表
        if (StringUtil.isNotEmpty(finProductCommonInfoDto.getPtlId())) {
            PtlManageObj ptlManageObj = new PtlManageObj();
            BeanUtil.copyProperties(ptlManageObj, finProductCommonInfoDto);
            ptlManageObj.setPortfolioId(finProductCommonInfoDto.getPtlId());
            ptlManageObj.setManageObjId(finProductCommonInfoDto.getFinprodId());
            ptlManageObj.setManageObjType(DictCodeListExt1.管理对象类型_金融产品.getValue());
            ptlManageObjMapper.insert(ptlManageObj);
        }

        // 生成有效的费用信息
        if (finProductCommonInfoDto.getBussFinProductFeeInfoDto() != null) {
            List<BussFinProductFeeInfoDto> feeInfoList = finProductCommonInfoDto.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
            if (!StringUtil.isEmptyList(feeInfoList)) {
                for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeInfoList) {
                    if (bussFinProductFeeInfoDto.getFeeId() != null
                            && mstFeeinfoMapper.selectByPrimaryKey(
                            bussFinProductFeeInfoDto.getFeeId()) == null) {
                        bussFinProductFeeInfoDto.setApplyType(
                                DictCodeList.关联范围_产品.getValue());
                        mstFeeinfoMapper.insert(bussFinProductFeeInfoDto);
                    }
                }
            }
        }

        // 一体化对应etliu start
        // 产品新建，调用估值接口
//        if (StringUtil.equals("FinProductBuild002", finProductCommonInfoDto.getPageId())
//        		||StringUtil.equals("FinNetValueProductBuild002", finProductCommonInfoDto.getPageId())) {//add by zhangweiqi净值产品新建也要掉估值接口
//            ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
//            inputDto.setPageId("FinProductBuild002");
//            inputDto.setFinProduct(finProductCommonInfoDto);
//            inputDto.setFinProductAdd(finProductAdd);
//            inputDto.setPrdValParam(finProductCommonInfoDto.getBussFinProductValuationDto());
//            inputDto.setFinProdAcctDataList(finProductCommonInfoDto.getBussFinProductFundAccountInfoDto());
//            //add by jiangpeng on 20210401 TISHKBS-1952 用于处理产品编辑时覆盖 BOK_BOOKSET_CAPACCT_INFO(账套清算账户表)的问题
//            inputDto.setProductEdit(finProductCommonInfoDto.isProductEdit());
//            valuationInterfService.prodOtherInfoInit(inputDto);
//        }
        // 一体化对应etliu end
        //update add zhangweiqi 增加产品账户信息  begin
        MstTradeOpponentDto counterDto = new MstTradeOpponentDto();//交易对手 add by zhangweiqi 2019-3-5
        List<StlCapacctDto> selectStlCapacct = new ArrayList<StlCapacctDto>();
        List<MstCounterSecurityAcct> selectMstCounterSecurityAcctList = new ArrayList<MstCounterSecurityAcct>();
        if (finProductCommonInfoDto.getBussFinProductFundAccountInfoDto() != null && finProductCommonInfoDto.getBussFinProductFundAccountInfoDto().size() > 0) {
            for (BussFinProductFundAccountInfoDto dto : finProductCommonInfoDto.getBussFinProductFundAccountInfoDto()) {
                StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
                BeanUtil.copyProperties(stlFinProductAcct, finProductCommonInfoDto);
                stlFinProductAcct.setFinprodId(finProductCommonInfoDto.getFinprodId());
                /**金融产品账户表设置币种  add by chendijun 2018/08/15 start**/
                stlFinProductAcct.setCcy(dto.getCcy());
                /**金融产品账户表设置币种  add by chendijun 2018/08/15 end**/
                //stlFinProductAcct.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                stlFinProductAcct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                //插入数据
                this.insertStlFinProductAcct(stlFinProductAcct, dto, cashMap);
                //交易对手 add by zhangweiqi 2019-3-5 begin
                if (StringUtil.equals(dto.getAcctType(), DictCodeListExt1.资金账户类型_产品托管户.getValue())) {
                    StlCapacctDto stlCapacctDto = new StlCapacctDto();
                    BeanUtil.copyProperties(stlCapacctDto, finProductCommonInfoDto);
                    stlCapacctDto.setStatus("N");
                    stlCapacctDto.setAcctNo(dto.getAcctNo() + "_" + dto.getCcy());
                    stlCapacctDto.setAccountName(dto.getAcctName());
                    stlCapacctDto.setAccountType(dto.getAcctType());
                    stlCapacctDto.setCcy(dto.getCcy());
                    stlCapacctDto.setBankDep(dto.getBankDep());
                    stlCapacctDto.setLargePayno(dto.getLargePayno());
                    stlCapacctDto.setIsOpen("01");
                    selectStlCapacct.add(stlCapacctDto);

                }
                //交易对手 add by zhangweiqi 2019-3-5 end
            }

        }
        //update add zhangweiqi 增加产品账户信息  end
        //交易对手 add by zhangweiqi 2019-3-5 begin
//        BeanUtil.copyProperties(counterDto, finProductCommonInfoDto);
//        counterDto.setCounterId(finProductCommonInfoDto.getFinprodMarketId());
//        counterDto.setCounterName(finProductCommonInfoDto.getFinprodName());
//        counterDto.setCounterType(DictCodeListExt1.交易对手类型_内部交易对手.getValue());
//        counterDto.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
//        BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter();
//        bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_产品要素管理);
//        counterDto.setFinprodId(baseGetBussinessId != null
//                && baseGetBussinessId.buildBussinessCode(bussinessCodeParameter) != null
//                && "null".equals(baseGetBussinessId.buildBussinessCode(
//                    bussinessCodeParameter)) ? baseGetBussinessId.buildBussinessCode(
//                        bussinessCodeParameter) : IDGenerator.generate());
//
//        List<StlSecAcctInfo> secAcctList= productCommonMapper.getAllStlSecAcctInfo();
//        for(StlSecAcctInfo secAcct:secAcctList){
//        	MstCounterSecurityAcct securityAcct=new MstCounterSecurityAcct();
//        	 BeanUtil.copyProperties(securityAcct, finProductCommonInfoDto);
//        	 securityAcct.setStatus("N");
//        	 securityAcct.setSecurityAcctNo(secAcct.getSecAcctId());
//        	 securityAcct.setSecurityAcctName(secAcct.getSecAcctName());
//        	 securityAcct.setRegistOrg(secAcct.getRegistOrg());
//        	 securityAcct.setIsOpen("01");
//        	 selectMstCounterSecurityAcctList.add(securityAcct);
//        }
//        counterDto.setSelectStlCapacct(selectStlCapacct);
//        counterDto.setSelectMstCounterSecurityAcct(selectMstCounterSecurityAcctList);
//        mstTradeOpponent002Service.doComplete(counterDto, "NEW");

        //add by dongenkuo 插入组合信息 begin
//        this.insertPtlPortfolioInfo(finProductCommonInfoDto);
        //add by dongenkuo 插入组合信息 end

        //交易对手 add by zhangweiqi 2019-3-5 end
        //update zhangweiqi 增加分层信息 begin
        if (finProductCommonInfoDto.getBussFinProductLayeredInfoDto() != null
                && finProductCommonInfoDto.getBussFinProductLayeredInfoDto().size() > 0
                && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), finProductCommonInfoDto.getDealMode())) {
            layerProdInfo(finProductCommonInfoDto, pCashInfo, pCashPayRule, pRateinfo, cashMap);
        }
        //update zhangweiqi 增加分层信息 end
        //add by wm 2019-09-29 start 周期型产品信息处理
        if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())) {
            cycleProdInfo(finProductCommonInfoDto, pCashInfo, pCashPayRule, pRateinfo, cashMap);
        }
        //add by wm 2019-09-29 end 周期型产品信息处理
    }

    private void insertStlFinProductAcct(StlFinProductAcct stlFinProductAcct, BussFinProductFundAccountInfoDto dto, Map<String, String> cashMap) {
        stlFinProductAcct.setAcctNo(dto.getAcctNo());
        if (StringUtil.isNotEmpty(dto.getFeeType())) {
            String feeType = "";
            String[] feeArr = dto.getFeeType().split(",");
            if (feeArr != null && feeArr.length > 0)
                for (int i = 0; i < feeArr.length; i++) {
                    if (cashMap.containsKey(feeArr[i] + stlFinProductAcct.getFinprodId()))
                        feeType += cashMap.get(feeArr[i] + stlFinProductAcct.getFinprodId());

                    if (cashMap.containsKey(feeArr[i] + stlFinProductAcct.getFinprodId()) && i != feeArr.length - 1)
                        feeType += ",";
                }

            stlFinProductAcct.setCashId(feeType);
        }
        stlFinProductAcct.setAcctType(dto.getAcctType());
        stlFinProductAcct.setRemark(dto.getRemark());
        stlFinProductAcctMapper.insert(stlFinProductAcct);
    }

    /**
     * 根据交易编号查询流程中数据是否存在
     *
     * @param tradeId 交易编号
     * @return 流程中数据是否存在
     * @see
     * @since 1.0
     */
    public Boolean isInFlowDataExisted(String tradeId) {
        return productCommonMapper.selectInFlowDataCount(tradeId) > 0;
    }

    /**
     * 根据交易编号查询该交易是否已被撤销
     *
     * @param tradeId 交易编号
     * @return 该交易是否已被撤销
     * @see
     * @since 1.0
     */
    public Boolean isAlreadyRevoked(String tradeId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tradeId", tradeId);
        paramMap.put("YES_NO_YES", DictCodeList.是否_是.getValue());
        paramMap.put("TRADE_STATUS_03", DictCodeListExt1.交易流程状态_撤销.getValue());
        return productCommonMapper.selectRevokedDataCount(paramMap) > 0;
    }

    /**
     * 根据汇总交易编号划款指令信息
     *
     * @param tradeId 汇总交易编号
     * @return 划款指令信息
     */
    public List<StlPayOrders> queryStlPayOrdersByTradeId(String tradeId) {
        return productCommonMapper.queryStlPayOrdersByTradeId(tradeId);
    }

    /**
     * 根据汇总交易编号查询交易信息
     *
     * @param tradeId 汇总交易编号
     * @return 交易信息
     */
    public List<TrdProductDeal> queryTrdProductDealByTotleTradeId(String tradeId) {
        return productCommonMapper.queryTrdProductDealByTotleTradeId(tradeId);
    }

    /**
     * 根据汇总交易编号查询交易附属信息
     *
     * @param tradeId 汇总交易编号
     * @return 交易附属信息
     */
    public List<TrdProductDealAdd> queryTrdProductDealAddByTotleTradeId(String tradeId) {
        return productCommonMapper.queryTrdProductDealAddByTotleTradeId(tradeId);
    }

    /**
     * 根据汇总交易编号查询金融产品交易扩展
     *
     * @param tradeId 汇总交易编号
     * @return 金融产品交易扩展
     */
    public List<PaTrdProductDeal> queryPaTrdProductDealByTotleTradeId(String tradeId) {
        return productCommonMapper.queryPaTrdProductDealByTotleTradeId(tradeId);
    }

    /**
     * 根据汇总交易编号查询组合交易信息
     *
     * @param tradeId 汇总交易编号
     * @return 组合交易信息
     */
    public List<PtlTrade> queryPtlTradeByTotleTradeId(String tradeId) {
        return productCommonMapper.queryPtlTradeByTotleTradeId(tradeId);
    }

    /**
     * 根据汇总交易编号查询理财分行销售明细
     *
     * @param tradeId 汇总交易编号
     * @return 理财分行销售明细
     */
    public List<PaBranchSaleDetail> queryPaBranchSaleDetailByTotleTradeId(String tradeId) {
        return productCommonMapper.queryPaBranchSaleDetailByTotleTradeId(tradeId);
    }

    /**
     * 根据汇总交易编号修改金融产品交易的撤销状态
     *
     * @param tradeId 汇总交易编号
     * @return DB
     */
    public int updateTrdProductDealByTotleTradeId(String tradeId) {
        return productCommonMapper.updateTrdProductDealByTotleTradeId(DictCodeList.是否_是.getValue(), tradeId);
    }

    /**
     * 根据汇总交易编号查询费用明细列表
     *
     * @param tradeId
     * @return
     */
    public List<TrdTradeFeeDetail> queryTrdTradeFeeDetailByTotleTradeId(String tradeId) {
        return productCommonMapper.queryTrdTradeFeeDetailByTotleTradeId(tradeId);
    }

    public List<TrdFinProductDealTotal> queryTrdFinProductDealTotalByModel(TrdFinProductDealTotal trdFinProductDealTotal) {
        return productCommonMapper.queryTrdFinProductDealTotalByModel(trdFinProductDealTotal);
    }

    /**
     * 分层子产品处理
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void layerProdInfo(FinProductCommonInfoDto entity, FinCashInfo pCahsInfo,
                               FinCashPayRule pCashPayRule, FinRateInfo pRateinfo, HashMap<String, String> cashMap) throws InvocationTargetException, IllegalAccessException {
        FinProduct parentFinProduct = finProductMapper.selectByPrimaryKey(
                entity.getFinprodId());
        // 查询现金流信息
        FinCashInfo parentFinCashInfo = pCahsInfo;
        // 查询现金流支付规则表

        FinCashPayRule parentFinCashPay = pCashPayRule;
        // 现金流利率信息表
        FinRateInfo parentFinRateInfo = pRateinfo;


        for (PrdLayerParam prdLayerParam : entity.getBussFinProductLayeredInfoDto()) {
            // 生成金融产品代码
            prdLayerParam.setFinprodId(entity.getFinprodId() + "_" + prdLayerParam.getProdStyle());
            // 理财产品分层参数表
            prdLayerParam.setpFinprodId(entity.getFinprodId());
            prdLayerParam.setVdate(entity.getVdate());
            prdLayerParam.setRemark(entity.getRemark());
            prdLayerParam.setYield(ArithmeticUtil.divide(prdLayerParam.getYield(), 100, 14));
            prdLayerParamMapper.insert(prdLayerParam);

            List<BaseFinProductBaseInfoDto> baseInfoDtoList = new ArrayList<BaseFinProductBaseInfoDto>();
            // 标的类金融产品基本信息基础组件
            BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
            // 现金流信息dtolist
            baseFinProductBaseInfoDto.setFinCashInfoList(new ArrayList<FinCashInfo>());
            // 现金流计算规则dtolist
            baseFinProductBaseInfoDto.setFinCashCalcRuleList(new ArrayList<FinCashCalcRule>());
            // 现金流支付规则dtolist
            baseFinProductBaseInfoDto.setFinCashPayRuleList(new ArrayList<FinCashPayRule>());
            // 现金流利率信息表dtolist
            baseFinProductBaseInfoDto.setFinRateInfoList(new ArrayList<FinRateInfo>());
            // 现金流计划表dtolist
            baseFinProductBaseInfoDto.setFinCashPlanList(new ArrayList<FinCashPlan>());
            //
            baseFinProductBaseInfoDto.setFinCashCountingRuleList(new ArrayList<FinFloatCountingRule>());
            // 标的类金融产品基本信息
            FinProduct finProduct = new FinProduct();
            BeanUtil.copyProperties(finProduct, parentFinProduct);
            finProduct.setFinprodId(prdLayerParam.getFinprodId());
            finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
            // 画面.产品简称
            finProduct.setFinprodAbbr(parentFinProduct.getFinprodAbbr() + "_" + prdLayerParam.getProdStyle());
            // 画面.产品名称
            finProduct.setFinprodName(parentFinProduct.getFinprodName() + "_" + prdLayerParam.getProdStyle());
            finProduct.setVdate(entity.getVdate());
            finProduct.setTermDays(entity.getTermDays());
            finProduct.setpFinprodId(entity.getFinprodId());
            finProduct.setRemark(entity.getRemark());
            baseFinProductBaseInfoDto.setFinProduct(finProduct);
            // 一体化对应etliu start

            // 一体化对应etliu start
            // 金融产品附属表entity
            FinProductAdd finProductAdd = new FinProductAdd();
            BeanUtil.copyProperties(finProductAdd, entity);
            finProductAdd.setPortfolioId(entity.getPtlId());
            finProductAdd.setFinprodId(finProduct.getFinprodId());
            // 一体化对应etliu end

            finProductAdd.setRedSpeed(finProductAdd.getPurSpeed());
            //add by zhangweiqi 净值化产品状态修改
            if (StringUtil.equals(entity.getPageId(), "FinProductBuild002") ||
                    StringUtil.equals(entity.getPageId(), "FinNetValueProductBuild002")) {
                finProductAdd.setIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
            } else {
                finProductAdd.setTmplId(entity.getFinprodId());
            }
            //add by yzg 20191107 产品系列编号 starat
            finProductAdd.setPrdDesignId(entity.getPrdDesignId());
            //add by yzg 20191107 产品系列编号 end
            baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);

            if (entity.getBussFinProductValuationDto() != null) {
                PrdValParam PrdValParam = entity.getBussFinProductValuationDto();
                PrdValParam.setFinprodId(finProduct.getFinprodId());
                // 估值信息
                prdValParamMapper.deleteByPrimaryKey(finProduct.getFinprodId());
                prdValParamMapper.insert(PrdValParam);
            }
            // 一体化对应etliu end
            // 现金流信息表
            if (parentFinCashInfo != null) {
                FinCashInfo finCashInfo = new FinCashInfo();
                BeanUtil.copyProperties(finCashInfo, parentFinCashInfo);
                // 根据规则生成现金流代码
                String cashId = CommonUtil.generateCashFlowCode(prdLayerParam.getFinprodId(),
                        DictCodeListExt1.现金流业务类型_金融产品.getValue(), parentFinCashInfo.getBranch(),
                        DictCodeListExt1.现金流二级类型_利息.getValue());
                finCashInfo.setCashId(cashId);
                finCashInfo.setFinprodId(prdLayerParam.getFinprodId());
                baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo);

                // 现金流计划规则表
                FinCashCalcRule sonFinCashCalcRule = new FinCashCalcRule();
                sonFinCashCalcRule.setCashId(cashId);
                sonFinCashCalcRule.setEffDate(entity.getVdate());

                sonFinCashCalcRule.setCalcType(parentFinRateInfo.getIntType());
                sonFinCashCalcRule.setBasis(parentFinRateInfo.getBasis());
                sonFinCashCalcRule.setYield(prdLayerParam.getYield());
                sonFinCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
                sonFinCashCalcRule.setFinprodId(prdLayerParam.getFinprodId());
                sonFinCashCalcRule.setBranch(parentFinCashInfo.getBranch());
                sonFinCashCalcRule.setFinprodType2(
                        DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
                sonFinCashCalcRule.setRemark(entity.getRemark());
                baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(sonFinCashCalcRule);

                // 现金流支付规则表
                FinCashPayRule finCashPayRule = new FinCashPayRule();
                BeanUtil.copyProperties(finCashPayRule, parentFinCashPay);
                finCashPayRule.setCashId(cashId);
                finCashPayRule.setEffDate(entity.getVdate());
                finCashPayRule.setFinprodId(prdLayerParam.getFinprodId());
                finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
                finCashPayRule.setRemark(entity.getRemark());
                baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);


                // 现金流利率信息表
                FinRateInfo finRateInfo = new FinRateInfo();
                BeanUtil.copyProperties(finRateInfo, parentFinRateInfo);
                finRateInfo.setCashId(cashId);
                finRateInfo.setEffDate(entity.getVdate());
                finRateInfo.setRate(prdLayerParam.getYield());
                finRateInfo.setFinprodId(prdLayerParam.getFinprodId());
                finRateInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
                finRateInfo.setRemark(entity.getRemark());
                baseFinProductBaseInfoDto.getFinRateInfoList().add(finRateInfo);

                // 现金流计划表
                FinCashPlan finCashPlan = new FinCashPlan();
                finCashPlan.setCashId(cashId);
                finCashPlan.setCashNum(new Long(1));
                finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
                finCashPlan.setVdateUnadjust(entity.getVdate());
                finCashPlan.setVdate(entity.getVdate());
                finCashPlan.setMdateUnadjust(parentFinProduct.getMdate());
                finCashPlan.setMdate(parentFinProduct.getMdate());
                finCashPlan.setPayDate(parentFinProduct.getMdate());
                finCashPlan.setPayDateUnadjust(parentFinProduct.getMdate());
                finCashPlan.setFinprodId(prdLayerParam.getFinprodId());
                finCashPlan.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
                finCashPlan.setBranch(parentFinCashInfo.getBranch());
                finCashPlan.setRemark(entity.getRemark());
                baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);

            }
            if (StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode()) && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
                // 费用
                if (entity.getBussFinProductFeeInfoDto() != null && !StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())) {
                    List<BussFinProductFeeInfoDto> feeInfoList = entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
                    for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeInfoList) {
                        //分层子产品未选择款型时 子产品默认保存
                        if (StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getProdStyle()) && !StringUtil.equals(prdLayerParam.getProdStyle(), bussFinProductFeeInfoDto.getProdStyle())) {
                            continue;
                        }
                        //组装费用相关信息
                        this.procFinProductFeeInfoData(entity, baseFinProductBaseInfoDto, bussFinProductFeeInfoDto, cashMap, true);
                    }
                }
                // 开放规则dtolist
                baseFinProductBaseInfoDto.setFinOpenRuleList(new ArrayList<>());
                // 开放计划dtolist
                baseFinProductBaseInfoDto.setFinOpenPlanList(new ArrayList<>());
                //开放计划生成（需要在浮动管理费前生成）
                this.procFinProductOpenPlan(entity, baseFinProductBaseInfoDto);
            }
            String resetType = null;
            // 业绩基准 本身只有净值型产品界面存在 目前只支持净值型分档产品
            if (entity.getBussBenchMarkLayeredInfoDto() != null && !StringUtil.isEmptyList(entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode())
                    && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {

                List<BussBenchMarkLayeredInfoDto> bussBenchMarkLayeredInfoList = entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList();
                for (BussBenchMarkLayeredInfoDto finBaseRuleInfo : bussBenchMarkLayeredInfoList) {
                    //分层子产品未选择款型时 子产品默认保存
                    if (StringUtil.isNotEmpty(finBaseRuleInfo.getProdStyle()) && !StringUtil.equals(prdLayerParam.getProdStyle(), finBaseRuleInfo.getProdStyle())) {
                        continue;
                    }
                    //当产品披露的业绩基准设置在产品上时，直接用页面上取的数据，会导致业绩基准相关利率被多除了
                    BussBenchMarkLayeredInfoDto bussBenchMarkLayeredInfoDto = new BussBenchMarkLayeredInfoDto();
                    BeanUtil.copyPropertiesIgnoreNull(bussBenchMarkLayeredInfoDto, finBaseRuleInfo);

                    if (DictCodeListExt1.披露类型_区间披露.getValue().equals(finBaseRuleInfo.getPublishType()) || StringUtil.isNotEmpty(finBaseRuleInfo.getRateType())) {
                        String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, entity.getBranch(), DictCodeListExt1.业绩基准类型_产品披露.getValue());
                        bussBenchMarkLayeredInfoDto.setBaseRuleId(baseRuleId);
                        bussBenchMarkLayeredInfoDto.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
                        bussBenchMarkLayeredInfoDto.setBranch(entity.getBranch());
                        bussBenchMarkLayeredInfoDto.setRateLower(ArithmeticUtil.divide(finBaseRuleInfo.getRateLower(), 100, 14));
                        bussBenchMarkLayeredInfoDto.setRateLimit(ArithmeticUtil.divide(finBaseRuleInfo.getRateLimit(), 100, 14));
                        bussBenchMarkLayeredInfoDto.setRate(ArithmeticUtil.divide(finBaseRuleInfo.getRate(), 100, 14));
                        if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                            bussBenchMarkLayeredInfoDto.setEffDate(finProduct.getVdate());
                        } else {
                            bussBenchMarkLayeredInfoDto.setEffDate(DateUtil.platDateFromCache());
                        }
                        if (StringUtil.equals(finBaseRuleInfo.getRateType(),
                                DictCodeList.利率类型_浮动利率.getValue())) {
                            MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                                    finBaseRuleInfo.getIndexId());
                            if (mstIndexInfo != null) {
                                bussBenchMarkLayeredInfoDto.setIndexSource(mstIndexInfo.getIndexType());
                            } else {
                                bussBenchMarkLayeredInfoDto.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                            }
                        }
                        bussBenchMarkLayeredInfoDto.setFinprodId(finProduct.getFinprodId());
                        bussBenchMarkLayeredInfoDto.setSpreadRate(
                                ArithmeticUtil.divide(finBaseRuleInfo.getSpreadRate(), 100, 14));

                        // 一体化对应etliu start
                        resetType = bussBenchMarkLayeredInfoDto.getResetType(); // 业绩基准重置规则
                        // 一体化对应etliu end

                        finBaseRuleInfoMapper.insert(bussBenchMarkLayeredInfoDto);
                    }
                }
            }
            // 浮动管理费
            if (entity.getBussFinProductFloatFeeDto() != null && !StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())) {

                for (FinProductFloatFeeDto finProductFloatFeeDto : entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList()) {
                    //分层子产品未选择款型时 子产品默认保存
                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.getProdStyle()) && !StringUtil.equals(prdLayerParam.getProdStyle(), finProductFloatFeeDto.getProdStyle())) {
                        continue;
                    }
                    //只支持净值型产品分档
                    if ((StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                            && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode()))) {
                        //浮动管理费用处理
                        this.procFinProductFloatFeeInfoData(entity, baseFinProductBaseInfoDto, finProductFloatFeeDto, resetType, cashMap);
                    }
                }

            }
            //分档净值型产品 资金账户对应到分层子产品中
            if (!StringUtil.isEmptyList(entity.getBussFinProductFundAccountInfoDto()) && (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                    && StringUtil.equals(DictCodeList.处理模式_分档.getValue(), entity.getDealMode()))) {

                for (BussFinProductFundAccountInfoDto dto : entity.getBussFinProductFundAccountInfoDto()) {
                    StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
                    BeanUtil.copyProperties(stlFinProductAcct, entity);
                    stlFinProductAcct.setFinprodId(prdLayerParam.getFinprodId());
                    //分档子产品资金账户设置币种 yzg 20191231 start
                    stlFinProductAcct.setCcy(dto.getCcy());
                    //分档子产品资金账户设置币种 yzg 20191231 end
                    //stlFinProductAcct.setFinprodType(DictCodeListExt1.金融产品类型_理财产品.getValue());
                    stlFinProductAcct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue());
                    //插入数据
                    this.insertStlFinProductAcct(stlFinProductAcct, dto, cashMap);
                }
            }
            baseInfoDtoList.add(baseFinProductBaseInfoDto);
            baseFinProductBaseInfoService.insert(baseInfoDtoList);
//		                 // 产品报价，调用估值接口
//                        ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
//                        inputDto.setPageId(entity.getPageId());
//                        inputDto.setFinProduct(finProduct);
//                        inputDto.setFinProductAdd(finProductAdd);
//                        PrdValParam prdValParam=new PrdValParam();
//                        BeanUtil.copyProperties(entity.getBussFinProductValuationDto(), entity);
//                        prdValParam.setFinprodId(finProduct.getFinprodId());
//                        inputDto.setPrdValParam(prdValParam);
//
//                        valuationInterfService.prodOtherInfoInit(inputDto);
        }
    }

    /**
     * 周期型产品处理
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void cycleProdInfo(FinProductCommonInfoDto entity, FinCashInfo pCahsInfo,
                               FinCashPayRule pCashPayRule, FinRateInfo pRateinfo, HashMap<String, String> cashMap) throws InvocationTargetException, IllegalAccessException {
        FinProduct parentFinProduct = finProductMapper.selectByPrimaryKey(entity.getFinprodId());
        // 查询现金流信息
//		        FinCashInfo parentFinCashInfo =pCahsInfo;

        //无固定期限的周期型产品不需要自动生成期次数据 modify by lifangpeng 2022-06-23 start
        if (StringUtil.equals(entity.getTermType(), DictCodeListExt2.期限类型_无固定期限.getValue())) {
            cycleProdInfo(parentFinProduct, entity, cashMap);
            return;
        }
        //无固定期限的周期型产品不需要自动生成期次数据 modify by lifangpeng 2022-06-23 end


        if (!StringUtil.equals(entity.getTermType(), DictCodeListExt2.期限类型_多固定期限.getValue())) {
            PrdLayerParam prdLayerParam = new PrdLayerParam();
            // 生成金融产品代码
            //周期型期限产品后缀
            String endFinprodId = "";
            if (StringUtil.equals(entity.getTermType(), DictCodeListExt2.期限类型_单一固定期限.getValue())) {
                endFinprodId = entity.getTermType() + "_" + entity.getInvestmentCycle();
                prdLayerParam.setTermMax(Long.parseLong(entity.getInvestmentCycle()));
            } else {
                endFinprodId = entity.getTermType();
            }
            prdLayerParam.setFinprodId(entity.getFinprodId() + "_" + endFinprodId);
            // 理财产品分层参数表
            prdLayerParam.setpFinprodId(entity.getFinprodId());
            prdLayerParam.setVdate(entity.getVdate());
            prdLayerParam.setIsSale(entity.getConsignmentFlag());
            prdLayerParam.setRemark(entity.getRemark());
            prdLayerParamMapper.insert(prdLayerParam);

            List<BaseFinProductBaseInfoDto> baseInfoDtoList = new ArrayList<BaseFinProductBaseInfoDto>();
            // 标的类金融产品基本信息基础组件
            BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
            // 现金流信息dtolist
            baseFinProductBaseInfoDto.setFinCashInfoList(new ArrayList<FinCashInfo>());
            // 现金流计算规则dtolist
            baseFinProductBaseInfoDto.setFinCashCalcRuleList(new ArrayList<FinCashCalcRule>());
            // 现金流支付规则dtolist
            baseFinProductBaseInfoDto.setFinCashPayRuleList(new ArrayList<FinCashPayRule>());
            // 现金流利率信息表dtolist
            baseFinProductBaseInfoDto.setFinRateInfoList(new ArrayList<FinRateInfo>());
            // 现金流计划表dtolist
            baseFinProductBaseInfoDto.setFinCashPlanList(new ArrayList<FinCashPlan>());
            //
            baseFinProductBaseInfoDto.setFinCashCountingRuleList(new ArrayList<FinFloatCountingRule>());
            // 标的类金融产品基本信息
            FinProduct finProduct = new FinProduct();
            BeanUtil.copyProperties(finProduct, parentFinProduct);
            finProduct.setFinprodId(prdLayerParam.getFinprodId());
            finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
            // 画面.产品简称
            finProduct.setFinprodAbbr(parentFinProduct.getFinprodAbbr() + "_" + endFinprodId);
            // 画面.产品名称
            finProduct.setFinprodName(parentFinProduct.getFinprodName() + "_" + endFinprodId);
            finProduct.setVdate(entity.getVdate());
            finProduct.setTermDays(entity.getTermDays());
            finProduct.setpFinprodId(entity.getFinprodId());
            finProduct.setRemark(entity.getRemark());
            //增加是否轧差清算 yzg 20191107 start
//			                finProduct.setLiquidationYesno(entity.getLiquidationYesno());
            //增加是否轧差清算 yzg 20191107 end
            baseFinProductBaseInfoDto.setFinProduct(finProduct);
            // 一体化对应etliu start

            // 自动生成第一条 期次   起息日为 产品起息日  到期日 为  单一/多固定: 产品起息日+投资周期   无固定 到期日为空
            // 1.  生成 FinTradeProduct 表记录
            FinTradeProduct finTradeProduct = new FinTradeProduct();
            finTradeProduct.setFinprodId(prdLayerParam.getFinprodId());
            finTradeProduct.setBranch(Long.valueOf(baseGetBussinessIdMapper.getNextValBySequenceName("QCGL_FZXH_SEQUENCE")));

            String sequence_6 = com.joyintech.tams.systemtools.util.CommonUtil.getSequenceIndexData(DataIndexSequenceType.期次管理, "%06d");
            String periodId = DateUtil.format(entity.getVdate(), "yyyyMMdd") + entity.getTermDays() + sequence_6;
            finTradeProduct.setPeriodId(periodId);

            finTradeProduct.setVdate(entity.getVdate());
            if (StringUtil.equals(entity.getTermType(), DictCodeListExt2.期限类型_单一固定期限.getValue())) {
                finTradeProduct.setMdate(DateUtil.addDay(entity.getVdate(), Integer.parseInt(entity.getInvestmentCycle())));
            }
            finTradeProduct.setFinprodType(BussinessDictCodeList.金融产品类型_理财子产品.getValue());
            finTradeProduct.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_理财子产品.getValue());
            finTradeProduct.setProfitType(entity.getProfitType());
            finTradeProduct.setCcy(entity.getCcy());

            if (StringUtil.isEmpty(entity.getInvestmentCycle())) {
                finTradeProduct.setTerm(entity.getTermDays());
            } else {
                finTradeProduct.setTerm(Long.valueOf(entity.getInvestmentCycle()));
            }
            finTradeProductMapper.insertSelective(finTradeProduct);
            //2.  生成 finTradeProductAdd
            FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
            BeanUtil.copyProperties(finTradeProductAdd, finTradeProduct);
            finTradeProductAddMapper.insertSelective(finTradeProductAdd);

            // 二级法人权限处理
            if (MultilevelDataAuthorityUtil.isEnabled()) {
                //创建机构代码
                String createInst = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
                entity.setCreateInst(createInst);
                // 有查询权限的机构代码
                List<String> searchOrgCodeList = null;
                // 有编辑权限的机构代码
                List<String> editOrgCodeList = null;

                //获取所有父级机构。  拥有查看权限
                searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
                // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                editOrgCodeList = Arrays.asList(entity.getCreateInst());

                // 记录二级法人权限
                insertDataAuthTypeInst(finTradeProduct.getFinprodId(), searchOrgCodeList, editOrgCodeList, DictCodeList.共通业务类型_期次管理.getValue());
            }

            //3.FinBaseRuleInfo  通过业绩基准和 浮动管理费 生成的 下面都生成的branch为0 和期次要求的不为0 不符 重新生成

            //fincashinfo fin_cash_pay_rule fin_cash_calc_rule fin_float_calc_rule fin_float_calc_rule_detail
            // 生成 F22对应的 fincashinfo
//                            FinCashInfo finCashInfo = new FinCashInfo();
//                            BeanUtil.copyProperties(finCashInfo,entity);
//                            finCashInfo.setCashId(CommonUtil.generateCashFlowCode(finTradeProduct.getFinprodId(),
//                                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finTradeProduct.getBranch(),
//                                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
//                            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
//                            finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
//                            finCashInfo.setBranch(finTradeProduct.getBranch());
//                            finCashInfo.setFeeId(entity.getBussFinProductFloatFeeDto().getFeeId());
//                            //期次新建的时候类型存理财子产品
//                            finCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());		//业务类型
//                            finCashInfo.setIsCalc(DictCodeList.是否_否.getValue());
//                            finCashInfoMapper.insertSelective(finCashInfo);


            // 一体化对应etliu start
            // 金融产品附属表entity
            FinProductAdd finProductAdd = new FinProductAdd();
            BeanUtil.copyProperties(finProductAdd, entity);
            finProductAdd.setPortfolioId(entity.getPtlId());
            finProductAdd.setFinprodId(finProduct.getFinprodId());
            // 一体化对应etliu end

            finProductAdd.setRedSpeed(finProductAdd.getPurSpeed());
            //add by zhangweiqi 净值化产品状态修改
            if (StringUtil.equals(entity.getPageId(), "FinProductBuild002") ||
                    StringUtil.equals(entity.getPageId(), "FinNetValueProductBuild002")) {
                finProductAdd.setIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
            } else {
                finProductAdd.setTmplId(entity.getFinprodId());
            }
            if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode())) {
                finProductAdd.setIsCycle(DictCodeList.是否_是.getValue());
                finProductAdd.setIsLay(DictCodeList.是否_是.getValue());
            }
            finProductAdd.setTermType(entity.getTermType());
            finProductAdd.setLayType(DictCodeList.分层类型_期限分层.getValue());
            finProductAdd.setInvestmentCycle(entity.getInvestmentCycle());

            baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);

//			                if (entity.getBussFinProductValuationDto() != null) {
//			                    PrdValParam PrdValParam = entity.getBussFinProductValuationDto();
//			                    PrdValParam.setFinprodId(finProduct.getFinprodId());
//			                    // 估值信息
//			                    prdValParamMapper.deleteByPrimaryKey(finProduct.getFinprodId());
//			                    prdValParamMapper.insert(PrdValParam);
//			                }
            // 一体化对应etliu end
            // 现金流信息表
//			                if(parentFinCashInfo != null){
//			                    FinCashInfo finCashInfo = new FinCashInfo();
//			                    BeanUtil.copyProperties(finCashInfo, parentFinCashInfo);
//			                    // 根据规则生成现金流代码
//			                    String cashId = CommonUtil.generateCashFlowCode(prdLayerParam.getFinprodId(),
//			                        DictCodeListExt1.现金流业务类型_金融产品.getValue(), parentFinCashInfo.getBranch(),
//			                        DictCodeListExt1.现金流二级类型_利息.getValue());
//			                    finCashInfo.setCashId(cashId);
//			                    finCashInfo.setFinprodId(prdLayerParam.getFinprodId());
//			                    baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo);
//
//			                    // 现金流计划规则表
//			                    FinCashCalcRule sonFinCashCalcRule = new FinCashCalcRule();
//			                    sonFinCashCalcRule.setCashId(cashId);
//			                    sonFinCashCalcRule.setEffDate(entity.getVdate());
//
//			                    sonFinCashCalcRule.setCalcType(parentFinRateInfo.getIntType());
//			                    sonFinCashCalcRule.setBasis(parentFinRateInfo.getBasis());
//			                    sonFinCashCalcRule.setYield(prdLayerParam.getYield());
//			                    sonFinCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
//			                    sonFinCashCalcRule.setFinprodId(prdLayerParam.getFinprodId());
//			                    sonFinCashCalcRule.setBranch(parentFinCashInfo.getBranch());
//			                    sonFinCashCalcRule.setFinprodType2(
//			                        DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//			                    sonFinCashCalcRule.setRemark(entity.getRemark());
//			                    baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(sonFinCashCalcRule);
//
//			                    // 现金流支付规则表
//			                    FinCashPayRule finCashPayRule = new FinCashPayRule();
//			                    BeanUtil.copyProperties(finCashPayRule, parentFinCashPay);
//			                    finCashPayRule.setCashId(cashId);
//			                    finCashPayRule.setEffDate(entity.getVdate());
//			                    finCashPayRule.setFinprodId(prdLayerParam.getFinprodId());
//			                    finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//			                    finCashPayRule.setRemark(entity.getRemark());
//			                    baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);
//
//
//
//			                    // 现金流利率信息表
//			                    FinRateInfo finRateInfo = new FinRateInfo();
//			                    BeanUtil.copyProperties(finRateInfo, parentFinRateInfo);
//			                    finRateInfo.setCashId(cashId);
//			                    finRateInfo.setEffDate(entity.getVdate());
//			                    finRateInfo.setRate(prdLayerParam.getYield());
//			                    finRateInfo.setFinprodId(prdLayerParam.getFinprodId());
//			                    finRateInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//			                    finRateInfo.setRemark(entity.getRemark());
//			                    baseFinProductBaseInfoDto.getFinRateInfoList().add(finRateInfo);
//
//			                    // 现金流计划表
//			                    FinCashPlan finCashPlan = new FinCashPlan();
//			                    finCashPlan.setCashId(cashId);
//			                    finCashPlan.setCashNum(new Long(1));
//			                    finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
//			                    finCashPlan.setVdateUnadjust(entity.getVdate());
//			                    finCashPlan.setVdate(entity.getVdate());
//			                    finCashPlan.setMdateUnadjust(parentFinProduct.getMdate());
//			                    finCashPlan.setMdate(parentFinProduct.getMdate());
//			                    finCashPlan.setPayDate(parentFinProduct.getMdate());
//			                    finCashPlan.setPayDateUnadjust(parentFinProduct.getMdate());
//			                    finCashPlan.setFinprodId(prdLayerParam.getFinprodId());
//			                    finCashPlan.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//			                    finCashPlan.setBranch(parentFinCashInfo.getBranch());
//			                    finCashPlan.setRemark(entity.getRemark());
//			                    baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);
//
//			                }
            if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
                // 费用
//			                    if(entity.getBussFinProductFeeInfoDto() != null && !StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
//			                        List<BussFinProductFeeInfoDto> feeInfoList = entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
//			                        for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeInfoList) {
//			                            //分层子产品未选择款型时 子产品默认保存
//			                            if(StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getProdStyle()) && !StringUtil.equals(prdLayerParam.getProdStyle(), bussFinProductFeeInfoDto.getProdStyle())){
//			                                continue;
//			                            }
//			                            //组装费用相关信息
//			                           this.procFinProductFeeInfoData(entity, baseFinProductBaseInfoDto, bussFinProductFeeInfoDto, cashMap,true);
//			                        }
//			                    }
                // 开放规则dtolist
                baseFinProductBaseInfoDto.setFinOpenRuleList(new ArrayList<>());
                // 开放计划dtolist
                baseFinProductBaseInfoDto.setFinOpenPlanList(new ArrayList<>());
                //开放计划生成（需要在浮动管理费前生成）
                this.procFinProductOpenPlan(entity, baseFinProductBaseInfoDto);
            }
            String resetType = null;
            // 业绩基准 本身只有净值型产品界面存在 目前只支持净值型分档产品
            if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) &&
                    StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
                BussBenchMarkInfoDto bussBenchMarkInfoDto = entity.getBussBenchMarkInfoDto();
                if (bussBenchMarkInfoDto != null &&
                        (DictCodeListExt1.披露类型_区间披露.getValue().equals(bussBenchMarkInfoDto.getPublishType())
                                || StringUtil.isNotEmpty(bussBenchMarkInfoDto.getRateType()))) {
                    String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, entity.getBranch(), DictCodeListExt1.业绩基准类型_产品披露.getValue());
                    bussBenchMarkInfoDto.setBaseRuleId(baseRuleId);
                    bussBenchMarkInfoDto.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
                    bussBenchMarkInfoDto.setBranch(entity.getBranch());
                    bussBenchMarkInfoDto.setRateLower(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRateLower(), 100, 14));
                    bussBenchMarkInfoDto.setRateLimit(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRateLimit(), 100, 14));
                    bussBenchMarkInfoDto.setRate(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRate(), 100, 14));
                    if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                        bussBenchMarkInfoDto.setEffDate(finProduct.getVdate());
                    } else {
                        bussBenchMarkInfoDto.setEffDate(DateUtil.platDateFromCache());
                    }
                    if (StringUtil.equals(bussBenchMarkInfoDto.getRateType(),
                            DictCodeList.利率类型_浮动利率.getValue())) {
                        MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                                bussBenchMarkInfoDto.getIndexId());
                        if (mstIndexInfo != null) {
                            bussBenchMarkInfoDto.setIndexSource(mstIndexInfo.getIndexType());
                        } else {
                            bussBenchMarkInfoDto.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                        }
                    }
                    bussBenchMarkInfoDto.setFinprodId(finProduct.getFinprodId());
                    bussBenchMarkInfoDto.setSpreadRate(
                            ArithmeticUtil.divide(bussBenchMarkInfoDto.getSpreadRate(), 100, 14));

                    // 一体化对应etliu start
                    resetType = bussBenchMarkInfoDto.getResetType(); // 业绩基准重置规则
                    // 一体化对应etliu end

                    finBaseRuleInfoMapper.insert(bussBenchMarkInfoDto);

                    // 重新生成 第一个期次的数据 branch 为 fintradeProduct的branch
//                                    BussBenchMarkInfoDto bussBenchMarkInfoDto2 = new BussBenchMarkInfoDto();
//                                    BeanUtil.copyProperties(bussBenchMarkInfoDto2,bussBenchMarkInfoDto);
//                                    bussBenchMarkInfoDto2.setBranch(finTradeProduct.getBranch());
//                                    bussBenchMarkInfoDto2.setBaseRuleId(CommonUtil.generateCashFlowCode(finTradeProduct.getPeriodId(), null,
//                                            Long.valueOf(finTradeProduct.getBranch()), DictCodeListExt1.业绩基准类型_产品披露.getValue()));
//                                    finBaseRuleInfoMapper.insert(bussBenchMarkInfoDto2);

                }
            }

            //  重新生成 第一个期次的数据 branch 为 fintradeProduct的branch  上面的代码 如果 非区间披露 并且bussBenchMarkInfoDto.getRateType()为空
            //  则不生成  finBaseRuleInfo 则 第一个有效期次无法展示在期次管理页面
            if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) &&
                    StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType()) && entity.getBussBenchMarkInfoDto() != null) {
                // 重新生成 第一个期次的数据 branch 为 fintradeProduct的branch
                BussBenchMarkInfoDto bussBenchMarkInfoDto = new BussBenchMarkInfoDto();
                bussBenchMarkInfoDto.setBranch(finTradeProduct.getBranch());
                bussBenchMarkInfoDto.setBaseRuleId(CommonUtil.generateCashFlowCode(finTradeProduct.getPeriodId(), null,
                        Long.valueOf(finTradeProduct.getBranch()), DictCodeListExt1.业绩基准类型_产品披露.getValue()));

                bussBenchMarkInfoDto.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
//                                bussBenchMarkInfoDto.setBranch(entity.getBranch());
                bussBenchMarkInfoDto.setRateLower(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRateLower(), 100, 14));
                bussBenchMarkInfoDto.setRateLimit(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRateLimit(), 100, 14));
                bussBenchMarkInfoDto.setRate(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRate(), 100, 14));
                if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                    bussBenchMarkInfoDto.setEffDate(finProduct.getVdate());
                } else {
                    bussBenchMarkInfoDto.setEffDate(DateUtil.platDateFromCache());
                }
                if (StringUtil.equals(bussBenchMarkInfoDto.getRateType(),
                        DictCodeList.利率类型_浮动利率.getValue())) {
                    MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                            bussBenchMarkInfoDto.getIndexId());
                    if (mstIndexInfo != null) {
                        bussBenchMarkInfoDto.setIndexSource(mstIndexInfo.getIndexType());
                    } else {
                        bussBenchMarkInfoDto.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                    }
                }
                bussBenchMarkInfoDto.setFinprodId(finProduct.getFinprodId());
                bussBenchMarkInfoDto.setSpreadRate(
                        ArithmeticUtil.divide(bussBenchMarkInfoDto.getSpreadRate(), 100, 14));

                finBaseRuleInfoMapper.insert(bussBenchMarkInfoDto);

            }
            // 浮动管理费
            FinProductFloatFeeDto finProductFloatFeeDto = entity.getBussFinProductFloatFeeDto();
            if (finProductFloatFeeDto != null && (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                    && StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()))) {
                //浮动管理费用处理
                this.procFinProductFloatFeeInfoData(entity, baseFinProductBaseInfoDto, finProductFloatFeeDto, resetType, cashMap);

                BussBenchMarkInfoDto bussBenchMarkInfoDtoTmp = entity.getBussBenchMarkFloatInfoDto();
                FinBaseRuleInfo finBaseRuleInfo = new FinBaseRuleInfo();
                //当净值行产品，并且浮动管理费的业绩基准不为空，并且浮动管理费finProductFloatFeeDto无业绩基准信息时，执行以下操作
                if (bussBenchMarkInfoDtoTmp != null && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                        && StringUtil.isNotEmpty(bussBenchMarkInfoDtoTmp.getRateType()) && StringUtil.isEmpty(finProductFloatFeeDto.getRateType())) {
                    String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, entity.getBranch(), DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
                    finBaseRuleInfo.setBaseRuleId(baseRuleId);
                    finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.披露类型_单值披露.getValue());
                    finBaseRuleInfo.setBranch(entity.getBranch());
                    finBaseRuleInfo.setRateType(bussBenchMarkInfoDtoTmp.getRateType());
                    finBaseRuleInfo.setRate(ArithmeticUtil.divide(bussBenchMarkInfoDtoTmp.getRate(), 100, 14));
                    finBaseRuleInfo.setCoefficient(bussBenchMarkInfoDtoTmp.getCoefficient());
                    finBaseRuleInfo.setResetType(bussBenchMarkInfoDtoTmp.getResetType());
                    if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                        finBaseRuleInfo.setEffDate(finProduct.getVdate());
                    } else {
                        finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
                    }
                    if (StringUtil.equals(finProductFloatFeeDto.getRateType(),
                            DictCodeList.利率类型_浮动利率.getValue())) {
                        MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                                bussBenchMarkInfoDtoTmp.getIndexId());
                        if (mstIndexInfo != null) {
                            finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                        } else {
                            finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                        }
                    }
                    finBaseRuleInfo.setFinprodId(finProduct.getFinprodId());
                    finBaseRuleInfo.setSpreadRate(
                            ArithmeticUtil.divide(bussBenchMarkInfoDtoTmp.getSpreadRate(), 100, 14));

                    // 一体化对应etliu start
                    resetType = finProductFloatFeeDto.getResetType(); // 业绩基准重置规则
                    // 一体化对应etliu end

                    finBaseRuleInfoMapper.insert(finBaseRuleInfo);

                    // 重新生成 第一个期次的数据 branch 为 fintradeProduct的branch
                    // 生成并直接入表 fincashinfo fin_cash_pay_rule fin_cash_calc_rule fin_float_calc_rule fin_float_calc_rule_detail finBaseRuleInfo
                    execFirstPeriodProductData(entity, finTradeProduct, resetType, entity.getTermType(), finProductFloatFeeDto);
//                                        FinBaseRuleInfo finBaseRuleInfo2 = new FinBaseRuleInfo();
//                                        BeanUtil.copyProperties(finBaseRuleInfo2,finBaseRuleInfo);
//                                        finBaseRuleInfo2.setBranch(finTradeProduct.getBranch());
//                                        finBaseRuleInfo2.setBaseRuleId(CommonUtil.generateCashFlowCode(finTradeProduct.getPeriodId(), null,
//                                                finTradeProduct.getBranch(), DictCodeListExt1.业绩基准类型_浮动管理费.getValue()));
//                                        finBaseRuleInfoMapper.insert(finBaseRuleInfo2);
//


                }
            }


            baseInfoDtoList.add(baseFinProductBaseInfoDto);
            baseFinProductBaseInfoService.insert(baseInfoDtoList);
            // 产品报价，调用估值接口
            //周期型产品期限层不用调用估值接口
//	                        ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
//	                        inputDto.setPageId(entity.getPageId());
//	                        inputDto.setFinProduct(finProduct);
//	                        inputDto.setFinProductAdd(finProductAdd);
//	                        PrdValParam prdValParam=new PrdValParam();
//	                        BeanUtil.copyProperties(entity.getBussFinProductValuationDto(), entity);
//	                        prdValParam.setFinprodId(finProduct.getFinprodId());
//	                        inputDto.setPrdValParam(prdValParam);
//	                        valuationInterfService.prodOtherInfoInit(inputDto);
        } else if (entity.getBussFinProductInvestPeriodDto() != null
                && !StringUtil.isEmptyList(entity.getBussFinProductInvestPeriodDto().getBussFinProductInvestPeriodDtoList())) {
            List<BussFinProductInvestPeriodDto> BussFinProductInvestPeriodList = entity.getBussFinProductInvestPeriodDto().getBussFinProductInvestPeriodDtoList();
            for (BussFinProductInvestPeriodDto bussFinProductInvestPeriodDto : BussFinProductInvestPeriodList) {
                PrdLayerParam prdLayerParam = new PrdLayerParam();
                // 生成金融产品代码
                //周期型期限产品后缀
                String endFinprodId = entity.getTermType() + "_" + bussFinProductInvestPeriodDto.getInvestDays();
                prdLayerParam.setTermMax((long) bussFinProductInvestPeriodDto.getInvestDays());
                prdLayerParam.setFinprodId(entity.getFinprodId() + "_" + endFinprodId);
                // 理财产品分层参数表
                prdLayerParam.setpFinprodId(entity.getFinprodId());
                prdLayerParam.setVdate(entity.getVdate());
                prdLayerParam.setIsSale(entity.getConsignmentFlag());
                prdLayerParam.setRemark(entity.getRemark());
                prdLayerParamMapper.insert(prdLayerParam);

                List<BaseFinProductBaseInfoDto> baseInfoDtoList = new ArrayList<BaseFinProductBaseInfoDto>();
                // 标的类金融产品基本信息基础组件
                BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
                // 现金流信息dtolist
                baseFinProductBaseInfoDto.setFinCashInfoList(new ArrayList<FinCashInfo>());
                // 现金流计算规则dtolist
                baseFinProductBaseInfoDto.setFinCashCalcRuleList(new ArrayList<FinCashCalcRule>());
                // 现金流支付规则dtolist
                baseFinProductBaseInfoDto.setFinCashPayRuleList(new ArrayList<FinCashPayRule>());
                // 现金流利率信息表dtolist
                baseFinProductBaseInfoDto.setFinRateInfoList(new ArrayList<FinRateInfo>());
                // 现金流计划表dtolist
                baseFinProductBaseInfoDto.setFinCashPlanList(new ArrayList<FinCashPlan>());
                //
                baseFinProductBaseInfoDto.setFinCashCountingRuleList(new ArrayList<FinFloatCountingRule>());
                // 标的类金融产品基本信息
                FinProduct finProduct = new FinProduct();
                BeanUtil.copyProperties(finProduct, parentFinProduct);
                finProduct.setFinprodId(prdLayerParam.getFinprodId());
                finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
                // 画面.产品简称
                finProduct.setFinprodAbbr(parentFinProduct.getFinprodAbbr() + "_" + endFinprodId);
                // 画面.产品名称
                finProduct.setFinprodName(parentFinProduct.getFinprodName() + "_" + endFinprodId);
                finProduct.setVdate(entity.getVdate());
                finProduct.setTermDays(entity.getTermDays());
                finProduct.setpFinprodId(entity.getFinprodId());
                finProduct.setRemark(entity.getRemark());
                baseFinProductBaseInfoDto.setFinProduct(finProduct);
                // 一体化对应etliu start

                // 自动生成第一条 期次   起息日为 产品起息日  到期日 为  单一/多固定: 产品起息日+投资周期   无固定 到期日为空
                // 1.  生成 FinTradeProduct 表记录
                FinTradeProduct finTradeProduct = new FinTradeProduct();
                finTradeProduct.setFinprodId(prdLayerParam.getFinprodId());
                finTradeProduct.setBranch(Long.valueOf(baseGetBussinessIdMapper.getNextValBySequenceName("QCGL_FZXH_SEQUENCE")));

                String sequence_6 = com.joyintech.tams.systemtools.util.CommonUtil.getSequenceIndexData(DataIndexSequenceType.期次管理, "%06d");
                String periodId = DateUtil.format(entity.getVdate(), "yyyyMMdd") + bussFinProductInvestPeriodDto.getInvestDays() + sequence_6;
                finTradeProduct.setPeriodId(periodId);

                finTradeProduct.setVdate(entity.getVdate());

                finTradeProduct.setMdate(DateUtil.addDay(entity.getVdate(), bussFinProductInvestPeriodDto.getInvestDays()));

                finTradeProduct.setFinprodType(BussinessDictCodeList.金融产品类型_理财子产品.getValue());
                finTradeProduct.setFinprodType2(BussinessDictCodeList.金融产品类型_投管用_理财子产品.getValue());
                finTradeProduct.setProfitType(entity.getProfitType());
                finTradeProduct.setCcy(entity.getCcy());
                finTradeProduct.setTerm(bussFinProductInvestPeriodDto.getInvestDays().longValue());

                finTradeProductMapper.insertSelective(finTradeProduct);

                // 二级法人权限处理
                if (MultilevelDataAuthorityUtil.isEnabled()) {
                    //创建机构代码
                    String createInst = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
                    entity.setCreateInst(createInst);
                    // 有查询权限的机构代码
                    List<String> searchOrgCodeList = null;
                    // 有编辑权限的机构代码
                    List<String> editOrgCodeList = null;

                    //获取所有父级机构。  拥有查看权限
                    searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
                    // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
                    editOrgCodeList = Arrays.asList(entity.getCreateInst());

                    // 记录二级法人权限
                    insertDataAuthTypeInst(finTradeProduct.getFinprodId(), searchOrgCodeList, editOrgCodeList, DictCodeList.共通业务类型_期次管理.getValue());
                }

                //2.  生成 finTradeProductAdd
                FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
                BeanUtil.copyProperties(finTradeProductAdd, finTradeProduct);
                finTradeProductAddMapper.insertSelective(finTradeProductAdd);

                //3.FinBaseRuleInfo  通过业绩基准和 浮动管理费 生成的 下面都已经生成
                //inBaseRuleInfoMapper.insertSelective(finBaseRuleInfo业绩基准);
                //inBaseRuleInfoMapper.insertSelective(finBaseRuleInfo浮动管理费);

                // 一体化对应etliu start
                // 金融产品附属表entity
                FinProductAdd finProductAdd = new FinProductAdd();
                BeanUtil.copyProperties(finProductAdd, entity);
                finProductAdd.setPortfolioId(entity.getPtlId());
                finProductAdd.setFinprodId(finProduct.getFinprodId());
                // 一体化对应etliu end

                finProductAdd.setRedSpeed(finProductAdd.getPurSpeed());
                //add by zhangweiqi 净值化产品状态修改
                if (StringUtil.equals(entity.getPageId(), "FinProductBuild002") ||
                        StringUtil.equals(entity.getPageId(), "FinNetValueProductBuild002")) {
                    finProductAdd.setIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
                } else {
                    finProductAdd.setTmplId(entity.getFinprodId());
                }
                if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode())) {
                    finProductAdd.setIsCycle(DictCodeList.是否_是.getValue());
                    finProductAdd.setIsLay(DictCodeList.是否_是.getValue());
                }
                finProductAdd.setTermType(entity.getTermType());
                finProductAdd.setLayType(DictCodeList.分层类型_期限分层.getValue());
                finProductAdd.setInvestmentCycle(bussFinProductInvestPeriodDto.getInvestDays().toString());

                baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);

//				                if (entity.getBussFinProductValuationDto() != null) {
//				                    PrdValParam PrdValParam = entity.getBussFinProductValuationDto();
//				                    PrdValParam.setFinprodId(finProduct.getFinprodId());
//				                    // 估值信息
//				                    prdValParamMapper.deleteByPrimaryKey(finProduct.getFinprodId());
//				                    prdValParamMapper.insert(PrdValParam);
//				                }
                // 一体化对应etliu end
                // 现金流信息表
//				                if(parentFinCashInfo != null){
//				                    FinCashInfo finCashInfo = new FinCashInfo();
//				                    BeanUtil.copyProperties(finCashInfo, parentFinCashInfo);
//				                    // 根据规则生成现金流代码
//				                    String cashId = CommonUtil.generateCashFlowCode(prdLayerParam.getFinprodId(),
//				                        DictCodeListExt1.现金流业务类型_金融产品.getValue(), parentFinCashInfo.getBranch(),
//				                        DictCodeListExt1.现金流二级类型_利息.getValue());
//				                    finCashInfo.setCashId(cashId);
//				                    finCashInfo.setFinprodId(prdLayerParam.getFinprodId());
//				                    baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo);
                //
//				                    // 现金流计划规则表
//				                    FinCashCalcRule sonFinCashCalcRule = new FinCashCalcRule();
//				                    sonFinCashCalcRule.setCashId(cashId);
//				                    sonFinCashCalcRule.setEffDate(entity.getVdate());
                //
//				                    sonFinCashCalcRule.setCalcType(parentFinRateInfo.getIntType());
//				                    sonFinCashCalcRule.setBasis(parentFinRateInfo.getBasis());
//				                    sonFinCashCalcRule.setYield(prdLayerParam.getYield());
//				                    sonFinCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
//				                    sonFinCashCalcRule.setFinprodId(prdLayerParam.getFinprodId());
//				                    sonFinCashCalcRule.setBranch(parentFinCashInfo.getBranch());
//				                    sonFinCashCalcRule.setFinprodType2(
//				                        DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//				                    sonFinCashCalcRule.setRemark(entity.getRemark());
//				                    baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(sonFinCashCalcRule);
                //
//				                    // 现金流支付规则表
//				                    FinCashPayRule finCashPayRule = new FinCashPayRule();
//				                    BeanUtil.copyProperties(finCashPayRule, parentFinCashPay);
//				                    finCashPayRule.setCashId(cashId);
//				                    finCashPayRule.setEffDate(entity.getVdate());
//				                    finCashPayRule.setFinprodId(prdLayerParam.getFinprodId());
//				                    finCashPayRule.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//				                    finCashPayRule.setRemark(entity.getRemark());
//				                    baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);
                //
                //
                //
//				                    // 现金流利率信息表
//				                    FinRateInfo finRateInfo = new FinRateInfo();
//				                    BeanUtil.copyProperties(finRateInfo, parentFinRateInfo);
//				                    finRateInfo.setCashId(cashId);
//				                    finRateInfo.setEffDate(entity.getVdate());
//				                    finRateInfo.setRate(prdLayerParam.getYield());
//				                    finRateInfo.setFinprodId(prdLayerParam.getFinprodId());
//				                    finRateInfo.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//				                    finRateInfo.setRemark(entity.getRemark());
//				                    baseFinProductBaseInfoDto.getFinRateInfoList().add(finRateInfo);
                //
//				                    // 现金流计划表
//				                    FinCashPlan finCashPlan = new FinCashPlan();
//				                    finCashPlan.setCashId(cashId);
//				                    finCashPlan.setCashNum(new Long(1));
//				                    finCashPlan.setCashType(DictCodeList.现金流类型_中间收息.getValue());
//				                    finCashPlan.setVdateUnadjust(entity.getVdate());
//				                    finCashPlan.setVdate(entity.getVdate());
//				                    finCashPlan.setMdateUnadjust(parentFinProduct.getMdate());
//				                    finCashPlan.setMdate(parentFinProduct.getMdate());
//				                    finCashPlan.setPayDate(parentFinProduct.getMdate());
//				                    finCashPlan.setPayDateUnadjust(parentFinProduct.getMdate());
//				                    finCashPlan.setFinprodId(prdLayerParam.getFinprodId());
//				                    finCashPlan.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
//				                    finCashPlan.setBranch(parentFinCashInfo.getBranch());
//				                    finCashPlan.setRemark(entity.getRemark());
//				                    baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);
                //
//				                }
                if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
                    // 费用
//				                    if(entity.getBussFinProductFeeInfoDto() != null && !StringUtil.isEmptyList(entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList())){
//				                        List<BussFinProductFeeInfoDto> feeInfoList = entity.getBussFinProductFeeInfoDto().getBussFinProductFeeInfoDtoList();
//				                        for (BussFinProductFeeInfoDto bussFinProductFeeInfoDto : feeInfoList) {
//				                            //分层子产品未选择款型时 子产品默认保存
//				                            if(StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getProdStyle()) && !StringUtil.equals(prdLayerParam.getProdStyle(), bussFinProductFeeInfoDto.getProdStyle())){
//				                                continue;
//				                            }
//				                            //组装费用相关信息
//				                           this.procFinProductFeeInfoData(entity, baseFinProductBaseInfoDto, bussFinProductFeeInfoDto, cashMap,true);
//				                        }
//				                    }
                    // 开放规则dtolist
                    baseFinProductBaseInfoDto.setFinOpenRuleList(new ArrayList<>());
                    // 开放计划dtolist
                    baseFinProductBaseInfoDto.setFinOpenPlanList(new ArrayList<>());
                    //开放计划生成（需要在浮动管理费前生成）
                    this.procFinProductOpenPlan(entity, baseFinProductBaseInfoDto);
                }
                String resetType = null;
                // 业绩基准 本身只有净值型产品界面存在 目前只支持净值型分档产品
                if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) &&
                        StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
                    List<BussBenchMarkLayeredInfoDto> bussBenchMarkLayeredInfoList = entity.getBussBenchMarkLayeredInfoDto().getBussBenchMarkLayeredInfoDtoList();
                    for (BussBenchMarkLayeredInfoDto finBaseRuleInfo : bussBenchMarkLayeredInfoList) {
                        //分层子产品未选择款型时 子产品默认保存
                        if (StringUtil.isNotEmpty(finBaseRuleInfo.getInvestmentCycle()) && !StringUtil.equals(prdLayerParam.getTermMax().toString(), finBaseRuleInfo.getInvestmentCycle())) {
                            continue;
                        }

                        if (DictCodeListExt1.披露类型_区间披露.getValue().equals(finBaseRuleInfo.getPublishType()) || StringUtil.isNotEmpty(finBaseRuleInfo.getRateType())) {
                            String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, entity.getBranch(), DictCodeListExt1.业绩基准类型_产品披露.getValue());
                            finBaseRuleInfo.setBaseRuleId(baseRuleId);
                            finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
                            finBaseRuleInfo.setBranch(entity.getBranch());
                            finBaseRuleInfo.setRateLower(ArithmeticUtil.divide(finBaseRuleInfo.getRateLower(), 100, 14));
                            finBaseRuleInfo.setRateLimit(ArithmeticUtil.divide(finBaseRuleInfo.getRateLimit(), 100, 14));
                            finBaseRuleInfo.setRate(ArithmeticUtil.divide(finBaseRuleInfo.getRate(), 100, 14));
                            if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                                finBaseRuleInfo.setEffDate(finProduct.getVdate());
                            } else {
                                finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
                            }
                            if (StringUtil.equals(finBaseRuleInfo.getRateType(),
                                    DictCodeList.利率类型_浮动利率.getValue())) {
                                MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                                        finBaseRuleInfo.getIndexId());
                                if (mstIndexInfo != null) {
                                    finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                                } else {
                                    finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                                }
                            }
                            finBaseRuleInfo.setFinprodId(finProduct.getFinprodId());
                            finBaseRuleInfo.setSpreadRate(
                                    ArithmeticUtil.divide(finBaseRuleInfo.getSpreadRate(), 100, 14));

                            // 一体化对应etliu start
                            resetType = finBaseRuleInfo.getResetType(); // 业绩基准重置规则
                            // 一体化对应etliu end

                            finBaseRuleInfoMapper.insert(finBaseRuleInfo);

                            // 重新生成 第一个期次的数据 branch 为 fintradeProduct的branch
//                                            BussBenchMarkLayeredInfoDto finBaseRuleInfo2 = new BussBenchMarkLayeredInfoDto();
//                                            BeanUtil.copyProperties(finBaseRuleInfo2,finBaseRuleInfo);
//                                            finBaseRuleInfo2.setBranch(finTradeProduct.getBranch());
//                                            finBaseRuleInfo2.setBaseRuleId(CommonUtil.generateCashFlowCode(finTradeProduct.getPeriodId(), null,
//                                                    Long.valueOf(finTradeProduct.getBranch()), DictCodeListExt1.业绩基准类型_产品披露.getValue()));
//                                            finBaseRuleInfoMapper.insert(finBaseRuleInfo2);

                        }

                        if (finBaseRuleInfo != null) {
                            String baseRuleId = CommonUtil.generateCashFlowCode(finTradeProduct.getPeriodId(), null,
                                    Long.valueOf(finTradeProduct.getBranch()), DictCodeListExt1.业绩基准类型_产品披露.getValue());
                            finBaseRuleInfo.setBaseRuleId(baseRuleId);
                            finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
                            finBaseRuleInfo.setBranch(finTradeProduct.getBranch());
                            finBaseRuleInfo.setRateLower(ArithmeticUtil.divide(finBaseRuleInfo.getRateLower(), 100, 14));
                            finBaseRuleInfo.setRateLimit(ArithmeticUtil.divide(finBaseRuleInfo.getRateLimit(), 100, 14));
                            finBaseRuleInfo.setRate(ArithmeticUtil.divide(finBaseRuleInfo.getRate(), 100, 14));
                            if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                                finBaseRuleInfo.setEffDate(finProduct.getVdate());
                            } else {
                                finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
                            }
                            if (StringUtil.equals(finBaseRuleInfo.getRateType(),
                                    DictCodeList.利率类型_浮动利率.getValue())) {
                                MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                                        finBaseRuleInfo.getIndexId());
                                if (mstIndexInfo != null) {
                                    finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                                } else {
                                    finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                                }
                            }
                            finBaseRuleInfo.setFinprodId(finProduct.getFinprodId());
                            finBaseRuleInfo.setSpreadRate(
                                    ArithmeticUtil.divide(finBaseRuleInfo.getSpreadRate(), 100, 14));

                            finBaseRuleInfoMapper.insert(finBaseRuleInfo);


                        }

                    }

                }
                // 浮动管理费
                if (entity.getBussFinProductFloatFeeDto() != null && !StringUtil.isEmptyList(entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList())) {

                    for (FinProductFloatFeeDto finProductFloatFeeDto : entity.getBussFinProductFloatFeeDto().getBussFinProductFloatFeeInfoDtoList()) {
                        //分层子产品未选择款型时 子产品默认保存
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getInvestmentCycle()) && !StringUtil.equals(prdLayerParam.getTermMax().toString(), finProductFloatFeeDto.getInvestmentCycle())) {
                            continue;
                        }
                        //只支持净值型产品分档
                        if ((StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                                && StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()))) {
                            //浮动管理费用处理
                            this.procFinProductFloatFeeInfoData(entity, baseFinProductBaseInfoDto, finProductFloatFeeDto, resetType, cashMap);

                            // 生成并直接入表 fincashinfo fin_cash_pay_rule fin_cash_calc_rule fin_float_calc_rule fin_float_calc_rule_detail finBaseRuleInfo
                            execFirstPeriodProductData(entity, finTradeProduct, resetType, entity.getTermType(), finProductFloatFeeDto);
                        }
                    }

                }

                baseInfoDtoList.add(baseFinProductBaseInfoDto);
                baseFinProductBaseInfoService.insert(baseInfoDtoList);
                // 产品报价，调用估值接口
                //周期型产品期限层不用调用估值接口
//		                        ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
//		                        inputDto.setPageId(entity.getPageId());
//		                        inputDto.setFinProduct(finProduct);
//		                        inputDto.setFinProductAdd(finProductAdd);
//		                        PrdValParam prdValParam=new PrdValParam();
//		                        BeanUtil.copyProperties(entity.getBussFinProductValuationDto(), entity);
//		                        prdValParam.setFinprodId(finProduct.getFinprodId());
//		                        inputDto.setPrdValParam(prdValParam);
//		                        valuationInterfService.prodOtherInfoInit(inputDto);

            }
        }
    }

    private void cycleProdInfo(FinProduct parentFinProduct, FinProductCommonInfoDto entity, HashMap<String, String> cashMap) throws InvocationTargetException, IllegalAccessException {
        PrdLayerParam prdLayerParam = new PrdLayerParam();
        // 生成金融产品代码
        //周期型期限产品后缀
        String endFinprodId = "";
        if (StringUtil.equals(entity.getTermType(), DictCodeListExt2.期限类型_单一固定期限.getValue())) {
            endFinprodId = entity.getTermType() + "_" + entity.getInvestmentCycle();
            prdLayerParam.setTermMax(Long.parseLong(entity.getInvestmentCycle()));
        } else {
            endFinprodId = entity.getTermType();
        }
        prdLayerParam.setFinprodId(entity.getFinprodId() + "_" + endFinprodId);
        // 理财产品分层参数表
        prdLayerParam.setpFinprodId(entity.getFinprodId());
        prdLayerParam.setVdate(entity.getVdate());
        prdLayerParam.setIsSale(entity.getConsignmentFlag());
        prdLayerParam.setRemark(entity.getRemark());
        prdLayerParamMapper.insert(prdLayerParam);

        List<BaseFinProductBaseInfoDto> baseInfoDtoList = new ArrayList<BaseFinProductBaseInfoDto>();
        // 标的类金融产品基本信息基础组件
        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto = new BaseFinProductBaseInfoDto();
        // 现金流信息dtolist
        baseFinProductBaseInfoDto.setFinCashInfoList(new ArrayList<FinCashInfo>());
        // 现金流计算规则dtolist
        baseFinProductBaseInfoDto.setFinCashCalcRuleList(new ArrayList<FinCashCalcRule>());
        // 现金流支付规则dtolist
        baseFinProductBaseInfoDto.setFinCashPayRuleList(new ArrayList<FinCashPayRule>());
        // 现金流利率信息表dtolist
        baseFinProductBaseInfoDto.setFinRateInfoList(new ArrayList<FinRateInfo>());
        // 现金流计划表dtolist
        baseFinProductBaseInfoDto.setFinCashPlanList(new ArrayList<FinCashPlan>());
        //
        baseFinProductBaseInfoDto.setFinCashCountingRuleList(new ArrayList<FinFloatCountingRule>());
        // 标的类金融产品基本信息
        FinProduct finProduct = new FinProduct();
        BeanUtil.copyProperties(finProduct, parentFinProduct);
        finProduct.setFinprodId(prdLayerParam.getFinprodId());
        finProduct.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue());
        // 画面.产品简称
        finProduct.setFinprodAbbr(parentFinProduct.getFinprodAbbr() + "_" + endFinprodId);
        // 画面.产品名称
        finProduct.setFinprodName(parentFinProduct.getFinprodName() + "_" + endFinprodId);
        finProduct.setVdate(entity.getVdate());
        finProduct.setTermDays(entity.getTermDays());
        finProduct.setpFinprodId(entity.getFinprodId());
        finProduct.setRemark(entity.getRemark());
        //增加是否轧差清算 yzg 20191107 start
//			                finProduct.setLiquidationYesno(entity.getLiquidationYesno());
        //增加是否轧差清算 yzg 20191107 end
        baseFinProductBaseInfoDto.setFinProduct(finProduct);
        // 一体化对应etliu start


        // 二级法人权限处理
//        if (MultilevelDataAuthorityUtil.isEnabled()) {
//            //创建机构代码
//            String createInst = MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
//            entity.setCreateInst(createInst);
//            // 有查询权限的机构代码
//            List<String> searchOrgCodeList = null;
//            // 有编辑权限的机构代码
//            List<String> editOrgCodeList = null;
//
//            //获取所有父级机构。  拥有查看权限
//            searchOrgCodeList = com.joyintech.tams.systemtools.util.CommonUtil.getParentInstitution(createInst);
//            // 仅资产的创建机构（当前登录用户所在机构）有编辑权限
//            editOrgCodeList = Arrays.asList(entity.getCreateInst());
//
//            // 记录二级法人权限
//            insertDataAuthTypeInst(finTradeProduct.getFinprodId(), searchOrgCodeList, editOrgCodeList, DictCodeList.共通业务类型_期次管理.getValue());
//        }

        //3.FinBaseRuleInfo  通过业绩基准和 浮动管理费 生成的 下面都生成的branch为0 和期次要求的不为0 不符 重新生成

        // 一体化对应etliu start
        // 金融产品附属表entity
        FinProductAdd finProductAdd = new FinProductAdd();
        BeanUtil.copyProperties(finProductAdd, entity);
        finProductAdd.setPortfolioId(entity.getPtlId());
        finProductAdd.setFinprodId(finProduct.getFinprodId());
        // 一体化对应etliu end

        finProductAdd.setRedSpeed(finProductAdd.getPurSpeed());
        //add by zhangweiqi 净值化产品状态修改
        if (StringUtil.equals(entity.getPageId(), "FinProductBuild002") ||
                StringUtil.equals(entity.getPageId(), "FinNetValueProductBuild002")) {
            finProductAdd.setIssueStatus(DictCodeListExt1.发行状态_确认发行.getValue());
        } else {
            finProductAdd.setTmplId(entity.getFinprodId());
        }
        if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode())) {
            finProductAdd.setIsCycle(DictCodeList.是否_是.getValue());
            finProductAdd.setIsLay(DictCodeList.是否_是.getValue());
        }
        finProductAdd.setTermType(entity.getTermType());
        finProductAdd.setLayType(DictCodeList.分层类型_期限分层.getValue());
        finProductAdd.setInvestmentCycle(entity.getInvestmentCycle());

        baseFinProductBaseInfoDto.setFinProductAdd(finProductAdd);
        // 一体化对应etliu end

        if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
            // 开放规则dtolist
            baseFinProductBaseInfoDto.setFinOpenRuleList(new ArrayList<>());
            // 开放计划dtolist
            baseFinProductBaseInfoDto.setFinOpenPlanList(new ArrayList<>());
            //开放计划生成（需要在浮动管理费前生成）
            this.procFinProductOpenPlan(entity, baseFinProductBaseInfoDto);
        }
        String resetType = null;
        // 业绩基准 本身只有净值型产品界面存在 目前只支持净值型分档产品
        if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()) &&
                StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())) {
            BussBenchMarkInfoDto bussBenchMarkInfoDto = entity.getBussBenchMarkInfoDto();
            if (bussBenchMarkInfoDto != null &&
                    (DictCodeListExt1.披露类型_区间披露.getValue().equals(bussBenchMarkInfoDto.getPublishType())
                            || StringUtil.isNotEmpty(bussBenchMarkInfoDto.getRateType()))) {
                String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, entity.getBranch(), DictCodeListExt1.业绩基准类型_产品披露.getValue());
                bussBenchMarkInfoDto.setBaseRuleId(baseRuleId);
                bussBenchMarkInfoDto.setBaseRuleType(DictCodeListExt1.业绩基准类型_产品披露.getValue());
                bussBenchMarkInfoDto.setBranch(entity.getBranch());
                bussBenchMarkInfoDto.setRateLower(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRateLower(), 100, 14));
                bussBenchMarkInfoDto.setRateLimit(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRateLimit(), 100, 14));
                bussBenchMarkInfoDto.setRate(ArithmeticUtil.divide(bussBenchMarkInfoDto.getRate(), 100, 14));
                if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                    bussBenchMarkInfoDto.setEffDate(finProduct.getVdate());
                } else {
                    bussBenchMarkInfoDto.setEffDate(DateUtil.platDateFromCache());
                }
                if (StringUtil.equals(bussBenchMarkInfoDto.getRateType(),
                        DictCodeList.利率类型_浮动利率.getValue())) {
                    MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                            bussBenchMarkInfoDto.getIndexId());
                    if (mstIndexInfo != null) {
                        bussBenchMarkInfoDto.setIndexSource(mstIndexInfo.getIndexType());
                    } else {
                        bussBenchMarkInfoDto.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                    }
                }
                bussBenchMarkInfoDto.setFinprodId(finProduct.getFinprodId());
                bussBenchMarkInfoDto.setSpreadRate(
                        ArithmeticUtil.divide(bussBenchMarkInfoDto.getSpreadRate(), 100, 14));

                // 一体化对应etliu start
                resetType = bussBenchMarkInfoDto.getResetType(); // 业绩基准重置规则
                // 一体化对应etliu end

                finBaseRuleInfoMapper.insert(bussBenchMarkInfoDto);
            }
        }

        // 浮动管理费
        FinProductFloatFeeDto finProductFloatFeeDto = entity.getBussFinProductFloatFeeDto();
        if (finProductFloatFeeDto != null && (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                && StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), entity.getDealMode()))) {
            //浮动管理费用处理
            this.procFinProductFloatFeeInfoData(entity, baseFinProductBaseInfoDto, finProductFloatFeeDto, resetType, cashMap);

            BussBenchMarkInfoDto bussBenchMarkInfoDtoTmp = entity.getBussBenchMarkFloatInfoDto();
            FinBaseRuleInfo finBaseRuleInfo = new FinBaseRuleInfo();
            //当净值行产品，并且浮动管理费的业绩基准不为空，并且浮动管理费finProductFloatFeeDto无业绩基准信息时，执行以下操作
            if (bussBenchMarkInfoDtoTmp != null && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), entity.getProfitType())
                    && StringUtil.isNotEmpty(bussBenchMarkInfoDtoTmp.getRateType()) && StringUtil.isEmpty(finProductFloatFeeDto.getRateType())) {
                String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, entity.getBranch(), DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
                finBaseRuleInfo.setBaseRuleId(baseRuleId);
                finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.披露类型_单值披露.getValue());
                finBaseRuleInfo.setBranch(entity.getBranch());
                finBaseRuleInfo.setRateType(bussBenchMarkInfoDtoTmp.getRateType());
                finBaseRuleInfo.setRate(ArithmeticUtil.divide(bussBenchMarkInfoDtoTmp.getRate(), 100, 14));
                finBaseRuleInfo.setCoefficient(bussBenchMarkInfoDtoTmp.getCoefficient());
                finBaseRuleInfo.setResetType(bussBenchMarkInfoDtoTmp.getResetType());
                if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                    finBaseRuleInfo.setEffDate(finProduct.getVdate());
                } else {
                    finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
                }
                if (StringUtil.equals(finProductFloatFeeDto.getRateType(),
                        DictCodeList.利率类型_浮动利率.getValue())) {
                    MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                            bussBenchMarkInfoDtoTmp.getIndexId());
                    if (mstIndexInfo != null) {
                        finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                    } else {
                        finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                    }
                }
                finBaseRuleInfo.setFinprodId(finProduct.getFinprodId());
                finBaseRuleInfo.setSpreadRate(
                        ArithmeticUtil.divide(bussBenchMarkInfoDtoTmp.getSpreadRate(), 100, 14));

                // 一体化对应etliu start
                resetType = finProductFloatFeeDto.getResetType(); // 业绩基准重置规则
                // 一体化对应etliu end

                finBaseRuleInfoMapper.insert(finBaseRuleInfo);

                // 重新生成 第一个期次的数据 branch 为 fintradeProduct的branch
                // 生成并直接入表 fincashinfo fin_cash_pay_rule fin_cash_calc_rule fin_float_calc_rule fin_float_calc_rule_detail finBaseRuleInfo
//                execFirstPeriodProductData(entity, finTradeProduct, resetType, entity.getTermType(), finProductFloatFeeDto);
            }
        }
        baseInfoDtoList.add(baseFinProductBaseInfoDto);
        baseFinProductBaseInfoService.insert(baseInfoDtoList);
    }

    private void execFirstPeriodProductData(FinProductCommonInfoDto finProductCommonInfoDto, FinTradeProduct finTradeProduct, String resetType, String termType, FinProductFloatFeeDto finProductFloatFeeDto) throws InvocationTargetException, IllegalAccessException {
        //

//            if(termType!=)
//            FinProductFloatFeeDto bussFinProductFloatFeeDto = finProductCommonInfoDto.getBussFinProductFloatFeeDto();
        if (finProductFloatFeeDto != null && StringUtil.isNotEmpty(finProductFloatFeeDto.getFeeId())) {

            FinCashInfo finCashInfo = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo, finProductCommonInfoDto);
            finCashInfo.setFinprodId(finTradeProduct.getFinprodId());
            finCashInfo.setCashId(CommonUtil.generateCashFlowCode(finTradeProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finTradeProduct.getBranch(),
                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
            finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
            finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            finCashInfo.setBranch(finTradeProduct.getBranch());
            finCashInfo.setFeeId(finProductFloatFeeDto.getFeeId());
            //期次新建的时候类型存理财子产品
            finCashInfo.setFinprodType(DictCodeListExt1.金融产品类型_理财子产品.getValue());
            // 费用默认参与计算
            if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())) {
                finCashInfo.setIsCalc(DictCodeList.是否_否.getValue());
            } else {
                finCashInfo.setIsCalc(DictCodeList.是否_是.getValue());
            }
            finCashInfoMapper.insertSelective(finCashInfo);


            // FinFloatCalcRule
            // 业绩报酬计算规则
            FinFloatCalcRule finFloatCalcRule = new FinFloatCalcRule();
            BeanUtil.copyProperties(finFloatCalcRule, finProductCommonInfoDto);
            finFloatCalcRule.setCashId(finCashInfo.getCashId());
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finFloatCalcRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finFloatCalcRule.setEffDate(DateUtil.platDateFromCache());
            }
            finFloatCalcRule.setBaseRule(DictCodeListExt1.业绩报酬计算规则_业绩基准.getValue());
            if (StringUtil.isNotEmpty(finProductFloatFeeDto.getFloatBaseRule())) {
                finFloatCalcRule.setBaseRule(finProductFloatFeeDto.getFloatBaseRule());
            }
            finFloatCalcRule.setAstVdateType(finProductFloatFeeDto.getNetAssetValueDay());
            finFloatCalcRule.setBaseDateType(finProductFloatFeeDto.getNetAssetValueDay());

            // 货币型产品 资产净值日 默认存 前一自然日
            if (!StringUtil.isNotEmpty(finProductFloatFeeDto.getNetAssetValueDay()) && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProductCommonInfoDto.getProfitType())) {
                finFloatCalcRule.setAstVdateType(DictCodeList.基数日期类型_前一自然日.getValue());
                finFloatCalcRule.setBaseDateType(DictCodeList.基数日期类型_前一自然日.getValue());
            }
            finFloatCalcRule.setBaseType(finProductFloatFeeDto.getBasicBill());
            finFloatCalcRule.setBasis(finProductFloatFeeDto.getBasis());
            if (StringUtil.isNotBlank(resetType)) {
                finFloatCalcRule.setActRule(resetType);
            }
            finFloatCalcRuleMapper.insert(finFloatCalcRule);

            // FinBaseRuleInfo
            FinBaseRuleInfo finBaseRuleInfo = new FinBaseRuleInfo();
            String baseRuleId = CommonUtil.generateCashFlowCode(finTradeProduct.getFinprodId(), null, finTradeProduct.getBranch(), DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
            if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType()) && StringUtil.isNotEmpty(finProductFloatFeeDto.getRateType())) {
                finBaseRuleInfo.setBaseRuleId(baseRuleId);
                finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.披露类型_单值披露.getValue());
                finBaseRuleInfo.setBranch(finTradeProduct.getBranch());
                finBaseRuleInfo.setRateType(finProductFloatFeeDto.getRateType());
                finBaseRuleInfo.setRate(ArithmeticUtil.divide(finProductFloatFeeDto.getRate(), 100, 14));
                finBaseRuleInfo.setIndexId(finProductFloatFeeDto.getIndexId());
                finBaseRuleInfo.setCoefficient(finProductFloatFeeDto.getCoefficient());
                finBaseRuleInfo.setResetType(finProductFloatFeeDto.getResetType());
                finBaseRuleInfo.setRemark(finProductFloatFeeDto.getRemark());
                if (StringUtil.isNotEmpty(finTradeProduct.getVdate())) {
                    finBaseRuleInfo.setEffDate(finTradeProduct.getVdate());
                } else {
                    finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
                }
                if (StringUtil.equals(finProductFloatFeeDto.getRateType(),
                        DictCodeList.利率类型_浮动利率.getValue())) {
                    MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                            finProductFloatFeeDto.getIndexId());
                    if (mstIndexInfo != null) {
                        finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                    } else {
                        finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                    }
                }
                finBaseRuleInfo.setFinprodId(finTradeProduct.getFinprodId());
                finBaseRuleInfo.setSpreadRate(
                        ArithmeticUtil.divide(finProductFloatFeeDto.getSpreadRate(), 100, 14));

                finBaseRuleInfoMapper.insert(finBaseRuleInfo);
            }

            // FinFloatCalcRuledetail
            List<FinFloatCalcRuleDetail> finFloatCalcRuleDetailList = finProductFloatFeeDto.getFinFloatCalcRuleDetailDtoList();
            Date effDate = StringUtil.isNotEmpty(
                    finProductCommonInfoDto.getVdate()) ? finProductCommonInfoDto.getVdate() : DateUtil.platDateFromCache();
            if (!StringUtil.isEmptyList(finFloatCalcRuleDetailList)) {
                for (FinFloatCalcRuleDetail finFloatCalcRuleDetail : finFloatCalcRuleDetailList) {
                    BeanUtil.copyProperties(finFloatCalcRuleDetail, finProductCommonInfoDto);
                    finFloatCalcRuleDetail.setCashId(finCashInfo.getCashId());
                    finFloatCalcRuleDetail.setEffDate(effDate);
                    finFloatCalcRuleDetail.setLev(finFloatCalcRuleDetail.getLev());
                    finFloatCalcRuleDetail.setBaseRuleId(
                            baseRuleId);// 业绩基准代码
                    finFloatCalcRuleDetail.setSpreadRate(ArithmeticUtil.divide(
                            finFloatCalcRuleDetail.getSpreadRate(), 100, 14));
                    finFloatCalcRuleDetail.setChargeRatio(ArithmeticUtil.divide(
                            finFloatCalcRuleDetail.getChargeRatio(), 100, 14));
                    finFloatCalcRuleDetailMapper.insert(finFloatCalcRuleDetail);
                }
            }

        }
    }

    private void checkDealModeIsCalc(FinCashInfo finCashInfo, String dealMode) {
        //edit by zlp 201903015  添加普通处理模式参与计算
        if (StringUtil.equals(dealMode, DictCodeList.处理模式_普通.getValue())
                || StringUtil.equals(dealMode, DictCodeList.处理模式_分档.getValue())
                || StringUtil.equals(dealMode, BussinessDictCodeList.处理模式_日添利.getValue())
                || StringUtil.equals(dealMode, BussinessDictCodeList.处理模式_智能日添利.getValue())
                || StringUtil.equals(dealMode, BussinessDictCodeList.处理模式_货币型.getValue())
                || StringUtil.equals(dealMode, BussinessDictCodeList.处理模式_净值型.getValue())) {
            finCashInfo.setIsCalc(DictCodeList.是否_是.getValue());
        } else {
            finCashInfo.setIsCalc(DictCodeList.是否_否.getValue());
        }
    }

    public List<PrdMdateAdjust> queryPrdMdateAdjustByModel(PrdMdateAdjust prdMdateAdjust) {
        return productCommonMapper.queryPrdMdateAdjustByModel(prdMdateAdjust);
    }

    /**
     * 创建产品流程生效时自动创建一个有效的组合
     *
     * @param entity 产品信息
     * @author dongenkuo
     * @version 1.0 2019年3月10日
     */
    public void insertPtlPortfolioInfo(FinProductCommonInfoDto entity) {
        // 未选组合时生成一条待处理的组合信息 (组合代码 = 产品代码 组合名称 = 金融产品简称)
        try {
            PtlManagementInfoDto ptlPortfolioInfoDto = new PtlManagementInfoDto();
            BeanUtil.copyProperties(ptlPortfolioInfoDto, entity);
            ptlPortfolioInfoDto.setPortfolioId(entity.getFinprodMarketId());
            ptlPortfolioInfoDto.setPortfolioName(entity.getFinprodAbbr());
            ptlPortfolioInfoDto.setPortfolioType(DictCodeListExt1.组合类型_投资类组合.getValue());
            ptlPortfolioInfoDto.setProfitType(entity.getProfitType());
            ptlPortfolioInfoDto.setOpenType(entity.getOperationType());
            ptlPortfolioInfoDto.setRaiseType(entity.getIssueType());
            ptlPortfolioInfoDto.setInvestNature(entity.getInvestNature());
            ptlPortfolioInfoDto.setVdate(entity.getVdate());
            ptlPortfolioInfoDto.setMdate(entity.getMdate());
            ptlPortfolioInfoDto.setIsGuarantee("N");
            PtlValParam ptlValParam = new PtlValParam();
            BeanUtil.copyProperties(ptlValParam, entity.getBussFinProductValuationDto());
            ptlPortfolioInfoDto.setPtlValParam(ptlValParam);
            // 二级法人改造 modify by wj  20200619 五个特殊参数 修改 为特殊取值方法
            //String userName = CacheUtil.getSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID);
            String userName = "";
            if (MultilevelDataAuthorityUtil.isEnabled()) {
                userName = CacheUtil.getSpecialSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID, MultilevelDataAuthorityUtil.getOrganizationCode(entity.getCreateUser()));
            } else {
                userName = CacheUtil.getSysParam(BussinessConstant.PORTFOLIO_HANDLE_USERID);
            }

            List<String> userIdList = new ArrayList<String>();
            userIdList.add(userName);
            ptlPortfolioInfoDto.setUserId(userIdList);
            //组装资金账户信息
            List<FundAccountInfoDto> fundAccountInfoList = new ArrayList<FundAccountInfoDto>();
            List<BussFinProductFundAccountInfoDto> prod_accountinfo = entity.getBussFinProductFundAccountInfoDto();
            if (prod_accountinfo != null && prod_accountinfo.size() > 0) {
                for (BussFinProductFundAccountInfoDto temp_account : prod_accountinfo) {
                    FundAccountInfoDto fundAccountInfo = new FundAccountInfoDto();
                    fundAccountInfo.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
                    fundAccountInfo.setAcctType(temp_account.getAcctType());
                    fundAccountInfo.setAcctNo(temp_account.getAcctNo());
                    fundAccountInfo.setCcy(temp_account.getCcy());
                    fundAccountInfo.setCreateDept(temp_account.getCreateDept());
                    fundAccountInfo.setCreateTime(temp_account.getCreateTime());
                    fundAccountInfo.setCreateUser(temp_account.getCreateUser());
                    fundAccountInfo.setUpdateTime(temp_account.getUpdateTime());
                    fundAccountInfo.setUpdateUser(temp_account.getUpdateUser());
                    fundAccountInfoList.add(fundAccountInfo);
                }
            }
            ptlPortfolioInfoDto.setFundAccountInfoDto(fundAccountInfoList);
            //组装权限信息 系统中所有有效的用户都增加都组合权限表
            List<String> userId = new ArrayList<String>();
            //查询系统内所有生效的用户信息
            List<User> all_user = productCommonMapper.getAllUser();
            if (all_user != null && all_user.size() > 0) {
                for (User userid : all_user) {
                    userId.add(userid.getUserName());
                }
            }
            ptlPortfolioInfoDto.setUserId(userId);
            //组装证券账户信息 将系统内所有有效的证券账户全部添加到组合的证券账户中
            List<SecuritiesAccountInfoDto> securitiesAccountInfoList = new ArrayList<SecuritiesAccountInfoDto>();
            //查询系统内所有生效的证券账户信息
            List<StlSecAcctInfo> allAccountInfo = productCommonMapper.getAllStlSecAcctInfo();
            if (allAccountInfo != null && allAccountInfo.size() > 0) {
                for (StlSecAcctInfo temp_account : allAccountInfo) {
                    SecuritiesAccountInfoDto securitiesAccountInfo = new SecuritiesAccountInfoDto();
                    securitiesAccountInfo.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
                    securitiesAccountInfo.setSecAcctId(temp_account.getSecAcctId());
                    securitiesAccountInfo.setCreateDept(ptlPortfolioInfoDto.getCreateDept());
                    securitiesAccountInfo.setCreateTime(ptlPortfolioInfoDto.getCreateTime());
                    securitiesAccountInfo.setCreateUser(ptlPortfolioInfoDto.getCreateUser());
                    securitiesAccountInfo.setUpdateTime(ptlPortfolioInfoDto.getUpdateTime());
                    securitiesAccountInfo.setUpdateUser(ptlPortfolioInfoDto.getUpdateUser());
                    securitiesAccountInfoList.add(securitiesAccountInfo);
                }
            }
            ptlPortfolioInfoDto.setSecuritiesAccountInfoDto(securitiesAccountInfoList);
            //保存组合
            String action = StringUtil.EMPTY;
            action = "insert";
            //插入资金账户
            for (FundAccountInfoDto dto : ptlPortfolioInfoDto.getFundAccountInfoDto()) {
                dto.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
//                String[] temp = dto.getAcctNo().split("_");
//                dto.setAcctNo(temp[0]);
//                dto.setCcy(temp[1]);
                PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
                BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, dto);
                ptlCapacctInfoMapper.insertSelective(ptlCapacctInfo);
                //update zhangweiqi SHBS-1457  组合删除 2019-3-28
                /*
                restTemplate.postForObject(
                    ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/funInfoInsert", dto,
                    Integer.class);
            */
            }
            //插入证券账户
            for (SecuritiesAccountInfoDto dto : ptlPortfolioInfoDto.getSecuritiesAccountInfoDto()) {
                dto.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
                PtlSecAcct ptlSecAcct = new PtlSecAcct();
                BeanUtil.copyPropertiesIgnoreNull(ptlSecAcct, dto);
                ptlSecAcct.setAcctNo(dto.getSecAcctId());
                ptlSecAcctMapper.insertSelective(ptlSecAcct);
                //update zhangweiqi SHBS-1457  组合删除 2019-3-28
               /* restTemplate.postForObject(
                        ServiceApplication.PORTFOLIO_SERVICE_NAME + "/ptlmanagementinfo/secInfoInsert", dto,
                        Integer.class);*/
            }
            //插入权限信息
            if (ptlPortfolioInfoDto.getUserId() != null && ptlPortfolioInfoDto.getUserId().size() > 0) {
                for (String userId_temp : ptlPortfolioInfoDto.getUserId()) {
                    PtlPortfolioAuthority ptlPortfolioAuthority = new PtlPortfolioAuthority();
                    ptlPortfolioAuthority.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
                    ptlPortfolioAuthority.setUserId(userId_temp);
                    ptlPortfolioAuthorityMapper.insertSelective(ptlPortfolioAuthority);
                    //update zhangweiqi SHBS-1457  组合删除 2019-3-28
                    /*
                    restTemplate.postForObject(
                            ServiceApplication.COMMON_SERVICE_NAME + "/CommonMapperDelegate/insert/" + PtlPortfolioAuthorityMapper.class.getName()
                                                          + "/insertSelective/",
                                                         ptlPortfolioAuthority, Integer.class);
               */
                }
            }
            //插入组合参数表
            ptlValParam.setFinprodId(ptlPortfolioInfoDto.getPortfolioId());
            //update zhangweiqi SHBS-1457  组合删除 2019-3-28
            /*restTemplate.postForObject(
                    ServiceApplication.COMMON_SERVICE_NAME + "/CommonMapperDelegate/insert/" + PtlValParamMapper.class.getName()
                                                  + "/insertSelective/",
                                                  ptlValParam, Integer.class);
            */
            ptlValParamMapper.insertSelective(ptlValParam);
            // 组合插入现金流信息表
            if (StringUtil.equals(ptlPortfolioInfoDto.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
                FinCashInfo finCashInfo = new FinCashInfo();
                //edit by wm 2019-05-11 start 转换报错修改
//                finCashInfo.setCashId(CommonUtil.generateCashFlowCode(
//                		ptlPortfolioInfoDto.getPortfolioId(),
//                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)),
//                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
                finCashInfo.setCashId(CommonUtil.generateCashFlowCode(
                        ptlPortfolioInfoDto.getPortfolioId(),
                        DictCodeListExt1.现金流业务类型_金融产品.getValue(), entity.getBranch(),
                        DictCodeListExt1.现金流二级类型_浮动管理费.getValue()));
                //edit by wm 2019-05-11 end 转换报错修改
                finCashInfo.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
                finCashInfo.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
                finCashInfo.setCcy(DictCodeList.币种_人民币_CNY.getValue());
                finCashInfo.setFinprodId(ptlPortfolioInfoDto.getPortfolioId());
                finCashInfo.setFinprodType(DictCodeListExt1.现金流业务类型_投资组合.getValue());
                //edit by wm 2019-05-11 start 转换报错修改
                finCashInfo.setBranch(entity.getBranch());
//                finCashInfo.setBranch(Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
                //edit by wm 2019-05-11 end 转换报错修改
                finCashInfo.setIsCalc(DictCodeList.是否_是.getValue());
                //update zhangweiqi SHBS-1457  组合删除 2019-3-28
                /*restTemplate.postForObject(
                        ServiceApplication.COMMON_SERVICE_NAME + "/CommonMapperDelegate/insert/" + FinCashInfoMapper.class.getName()
                                                      + "/insertSelective/",
                                                      finCashInfo, Integer.class);
            */
                finCashInfoMapper.insertSelective(finCashInfo);
            }
            //====================== 20190312新增
            PaPtlPortfolio paPtlPortfolio = new PaPtlPortfolio();
            BeanUtil.copyPropertiesIgnoreNull(paPtlPortfolio, ptlPortfolioInfoDto);
            paPtlPortfolioMapper.insertSelective(paPtlPortfolio);
            PtlPortfolio ptlPortfolio = new PtlPortfolio();
            BeanUtil.copyPropertiesIgnoreNull(ptlPortfolio, ptlPortfolioInfoDto);
            ptlPortfolioMapper.insertSelective(ptlPortfolio);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("classify_dimension_01", DictCodeListExt1.分类维度_收益类型.getValue());
            map.put("classify_dimension_02", DictCodeListExt1.分类维度_开放类型.getValue());
            map.put("classify_dimension_03", DictCodeListExt1.分类维度_募集方式.getValue());
            map.put("classify_dimension_04", DictCodeListExt1.分类维度_投资性质.getValue());

            map.put("dimension_01", ptlPortfolioInfoDto.getProfitType());// 收益类型
            map.put("dimension_02", ptlPortfolioInfoDto.getOpenType());// 开放类型
            map.put("dimension_03", ptlPortfolioInfoDto.getRaiseType());// 募集方式
            map.put("dimension_04", ptlPortfolioInfoDto.getInvestNature());// 投资性质

            List<String> ptlBagIdList = prdPtlManagementInfo001Mapper.selectPtlBagId(map);
            prdPtlManagementInfo001Mapper.deleteRskPortfolioPackageDtlByportId(ptlPortfolioInfoDto.getPortfolioId());
            if (ptlBagIdList != null && ptlBagIdList.size() > 0) {
                for (String ptlBagId : ptlBagIdList) {
                    RskPortfolioPackageDtl rskPortfolioPackageDtl = new RskPortfolioPackageDtl();
                    rskPortfolioPackageDtl.setPtlBagId(ptlBagId);
                    rskPortfolioPackageDtl.setPortfolioId(ptlPortfolioInfoDto.getPortfolioId());
                    rskPortfolioPackageDtlMapper.insertSelective(rskPortfolioPackageDtl);
                }
            }
//           // 一体化对应etliu start
//           // 组合新建，调用估值接口
//           ProdOtherInfoInitInputDto inputDto = new ProdOtherInfoInitInputDto();
//           inputDto.setPageId("PtlManagementInfo002");
//           inputDto.setPtlPortfolio(ptlPortfolio);
//           inputDto.setPtlValParam(ptlPortfolioInfoDto.getPtlValParam());
//           inputDto.setNewData(true);
//           if (!StringUtil.isEmptyList(ptlPortfolioInfoDto.getFundAccountInfoDto())) {
//               List<PtlCapacctInfo> prodAcctDataList = new ArrayList<>();
//               for (FundAccountInfoDto fundAccountInfoDto : ptlPortfolioInfoDto.getFundAccountInfoDto()) {
//                   PtlCapacctInfo ptlCapacctInfo = new PtlCapacctInfo();
//                   BeanUtil.copyPropertiesIgnoreNull(ptlCapacctInfo, fundAccountInfoDto);
//                   prodAcctDataList.add(ptlCapacctInfo);
//               }
//               inputDto.setProdAcctDataList(prodAcctDataList);
//           }
//
//           valuationInterfService.prodOtherInfoInit(inputDto);
//           // 一体化对应etliu end
//
//               ptlManagementInfo001Mapper.insertPaPtlPortfolio(entity);
//               ptlManagementInfo001Mapper.insertPtlPortfolio(entity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public int deleteBokBookSet(String finprodId) {
        return productCommonMapper.deleteBokBookSet(finprodId);
    }

    /**
     * 处理费用信息保存表数据存放集合中
     * <功能详细描述>
     *
     * @param finProductCommonInfoDto
     * @param baseFinProductBaseInfoDto
     * @param bussFinProductFeeInfoDto
     * @param cashMap
     * @param flag
     * @throws Exception
     * @see
     * @since 1.0
     */
    private void procFinProductFeeInfoData(FinProductCommonInfoDto finProductCommonInfoDto,
                                           BaseFinProductBaseInfoDto baseFinProductBaseInfoDto,
                                           BussFinProductFeeInfoDto bussFinProductFeeInfoDto, HashMap<String, String> cashMap, boolean flag) throws InvocationTargetException, IllegalAccessException {
        FinProduct finProduct = baseFinProductBaseInfoDto.getFinProduct();
        String cashId_fee = "";
        String cashType2 = "";
        // 托管费
        if (StringUtil.equals(DictCodeList.费用品种类型_托管费.getCodeName(), bussFinProductFeeInfoDto.getFeeType())
                || StringUtil.equals(DictCodeList.费用品种类型_托管费.getValue(), bussFinProductFeeInfoDto.getFeeType())) {
            cashId_fee = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_托管费.getValue());
            cashType2 = DictCodeListExt1.现金流二级类型_托管费.getValue();
        }
        // 固定管理费
        else if (StringUtil.equals(DictCodeList.费用品种类型_固定管理费.getCodeName(), bussFinProductFeeInfoDto.getFeeType())
                || StringUtil.equals(DictCodeList.费用品种类型_固定管理费.getValue(), bussFinProductFeeInfoDto.getFeeType())) {
            cashId_fee = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_固定管理费.getValue());
            cashType2 = DictCodeListExt1.现金流二级类型_固定管理费.getValue();
        }
        // 销售费
        else if (StringUtil.equals(DictCodeList.费用品种类型_销售手续费.getCodeName(), bussFinProductFeeInfoDto.getFeeType())
                || StringUtil.equals(DictCodeList.费用品种类型_销售手续费.getValue(), bussFinProductFeeInfoDto.getFeeType())) {
            cashId_fee = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_销售费.getValue());
            cashType2 = DictCodeListExt1.现金流二级类型_销售费.getValue();
        }
        // 一体化对应etliu start
        else if (StringUtil.equals(DictCodeList.费用品种类型_外包服务费.getCodeName(), bussFinProductFeeInfoDto.getFeeType())
                || StringUtil.equals(DictCodeList.费用品种类型_外包服务费.getValue(), bussFinProductFeeInfoDto.getFeeType())) {
            cashId_fee = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_外包服务费.getValue());
            cashType2 = DictCodeListExt1.现金流二级类型_外包服务费.getValue();
        }
        // 代销费
        else if (StringUtil.equals(DictCodeList.费用品种类型_代销费.getCodeName(), bussFinProductFeeInfoDto.getFeeType())
                || StringUtil.equals(DictCodeList.费用品种类型_代销费.getValue(), bussFinProductFeeInfoDto.getFeeType())) {
            cashId_fee = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_代销费.getValue());
            cashType2 = DictCodeListExt1.现金流二级类型_代销费.getValue();
        }
        // 投资顾问费
        else if (StringUtil.equals(DictCodeListExt2.费用品种类型_投资顾问费.getCodeName(), bussFinProductFeeInfoDto.getFeeType())
                || StringUtil.equals(DictCodeListExt2.费用品种类型_投资顾问费.getValue(), bussFinProductFeeInfoDto.getFeeType())) {
            cashId_fee = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt2.现金流二级类型_投资顾问费.getValue());
            cashType2 = DictCodeListExt2.现金流二级类型_投资顾问费.getValue();
        }
        // 一体化对应etliu end
        if (StringUtil.isEmpty(cashId_fee)) {
            logger.error("当前费用类型不在有效范围内:托管费、固定管理费、销售手续费、外包服务费、代销费、投资顾问费，当前费用类型是："
                    + bussFinProductFeeInfoDto.getFeeType());
        } else {

            //不需要生成
            if (!flag) {
                return;
            }
            //费用id 对应现金流信息
            cashMap.put(bussFinProductFeeInfoDto.getFeeId() + finProduct.getFinprodId(), cashId_fee);// add by zhangweiqi
            // 现金流信息
            FinCashInfo finCashInfo_int = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo_int, finProductCommonInfoDto);
            finCashInfo_int.setCashId(cashId_fee);
            finCashInfo_int.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
            finCashInfo_int.setCashType2(cashType2);
            finCashInfo_int.setCcy(finProductCommonInfoDto.getCcy());
            finCashInfo_int.setFinprodId(finProduct.getFinprodId());
            finCashInfo_int.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo_int.setBranch(finProductCommonInfoDto.getBranch());
            finCashInfo_int.setFeeId(bussFinProductFeeInfoDto.getFeeId());
            // zhangnn 2019/2/27 start
            // 计提费用 是否参与计算 默认 Y
            if (DictCodeList.收费类型_计提式费用.getValue().equals(
                    bussFinProductFeeInfoDto.getChargeType())) {
                finCashInfo_int.setIsCalc(DictCodeList.是否_是.getValue());
            } else {
                finCashInfo_int.setIsCalc(DictCodeList.是否_否.getValue());
            }
            // zhangnn 2019/2/27 end
            // this.checkDealModeIsCalc(finCashInfo_int, finProductCommonInfoDto.getDealMode());
            baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_int);

            // 现金流支付规则
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            BeanUtil.copyProperties(finCashPayRule, finProductCommonInfoDto);
            finCashPayRule.setCashId(cashId_fee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashPayRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashPayRule.setEffDate(DateUtil.platDateFromCache());
            }
            finCashPayRule.setFirstPayDate(bussFinProductFeeInfoDto.getFirstCalEndDate());// add by
            // zhangweiqi
            // 首次计费结束日
            finCashPayRule.setPayCycle(bussFinProductFeeInfoDto.getPayFeeRule());
            finCashPayRule.setEndTdayRule(bussFinProductFeeInfoDto.getCalcFeeTradeRule());
            finCashPayRule.setPayTdayRule(bussFinProductFeeInfoDto.getPayFeeTradeRule());
            finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
            finCashPayRule.setFinprodId(finProduct.getFinprodId());
            finCashPayRule.setFinprodType(finProduct.getFinprodType());
            finCashPayRule.setFinprodType2(finProduct.getFinprodType2());
            finCashPayRule.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);

            // 现金流计算规则
            FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
            BeanUtil.copyProperties(finCashCalcRule, finProductCommonInfoDto);
            finCashCalcRule.setCashId(cashId_fee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashCalcRule.setEffDate(finProductCommonInfoDto.getVdate());
                // 一体化对应etliu
                // finCashCalcRule.setRelaeseDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashCalcRule.setEffDate(DateUtil.platDateFromCache());
                // 一体化对应etliu
                // finCashCalcRule.setRelaeseDate(platFormDate);
            }
            finCashCalcRule.setCalcType(DictCodeList.利率类型_固定利率.getValue());
            finCashCalcRule.setBaseType(bussFinProductFeeInfoDto.getBasicBill());
            finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
            if (bussFinProductFeeInfoDto.getNetAssetValueDay() != null) {
                finCashCalcRule.setBaseDateType(bussFinProductFeeInfoDto.getNetAssetValueDay());
            }

            // 一体化对应etliu start
            if (StringUtil.isBlank(finCashCalcRule.getBaseDateType())) {
                finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
            }
            // 一体化对应etliu end

            finCashCalcRule.setBasis(bussFinProductFeeInfoDto.getBasis());
            finCashCalcRule.setYield(
                    ArithmeticUtil.divide(bussFinProductFeeInfoDto.getFeeRate(), 100, 14));
            finCashCalcRule.setIsInitial(DictCodeList.是否_是.getValue());
            finCashCalcRule.setFinprodId(finProduct.getFinprodId());
            finCashCalcRule.setFinprodType(finProduct.getFinprodType());
            finCashCalcRule.setFinprodType2(finProduct.getFinprodType2());
            finCashCalcRule.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(finCashCalcRule);

            // 现金流计提规则
            FinFloatCountingRule finCashCountingRule = new FinFloatCountingRule();
            BeanUtil.copyProperties(finCashCountingRule, finProductCommonInfoDto);
            finCashCountingRule.setCashId(cashId_fee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashCountingRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashCountingRule.setEffDate(DateUtil.platDateFromCache());
            }
            finCashCountingRule.setAccrType(DictCodeList.计提类型_高频.getValue());
            finCashCountingRule.setAccrCycle(new Long(1));
            finCashCountingRule.setAccrCycleUnit(DictCodeListExt1.周期单位_日.getValue());
            finCashCountingRule.setfAccrDate(bussFinProductFeeInfoDto.getProvisionFirstDay());
            finCashCountingRule.seteAccrDate(bussFinProductFeeInfoDto.geteAccrDate());// 计提结束日 add
            // by
            //新增首次计费基数 20191018 yzg start
            if (StringUtil.isNotEmpty(bussFinProductFeeInfoDto.getFirstBaseType())) {
                finCashCountingRule.setFirstBaseType(bussFinProductFeeInfoDto.getFirstBaseType());
            }
            //新增首次计费基数 20191018 yzg start
            baseFinProductBaseInfoDto.getFinCashCountingRuleList().add(finCashCountingRule);
            // add by zhangweiqi 费用增加现金流计提计划 begin
            /*
             * // 现金流计提计划 Date startDate = bussFinProductFeeInfoDto.getProvisionFirstDay();
             * if(startDate==null) startDate=finProductCommonInfoDto.getVdate(); Date
             * endDate=bussFinProductFeeInfoDto.geteAccrDate();
             * if(endDate==null)endDate=finProductCommonInfoDto.getMdate(); while
             * (DateUtil.compareDate(startDate,endDate) < 1) { FinFloatAccrualSche
             * finFloatAccrualSche = new FinFloatAccrualSche();
             * BeanUtil.copyProperties(finFloatAccrualSche, finProductCommonInfoDto);
             * finFloatAccrualSche.setCashId(cashId_fee);
             * finFloatAccrualSche.setAccrDate(startDate);
             * finFloatAccrualSche.setAccrStrDate(startDate);
             * finFloatAccrualSche.setAccrEndDate(startDate);
             * finFloatAccrualSche.setValDate(startDate);
             * finFloatAccrualScheMapper.insert(finFloatAccrualSche); startDate =
             * DateUtil.addDay(startDate, 1); } //add by zhangweiqi 费用增加现金流计提计划 end
             */
            // 现金流利率信息
            FinRateInfo finRateInfo = new FinRateInfo();
            BeanUtil.copyProperties(finRateInfo, finProductCommonInfoDto);
            finRateInfo.setCashId(cashId_fee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finRateInfo.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finRateInfo.setEffDate(DateUtil.platDateFromCache());
            }
            // 一体化对应etliu
            // finRateInfo.setRateType(DictCodeList.利率类型_固定利率.getValue());
            finRateInfo.setIntType(DictCodeList.利率类型_固定利率.getValue());
            finRateInfo.setBasis(bussFinProductFeeInfoDto.getBasis());
            finRateInfo.setRate(
                    ArithmeticUtil.divide(bussFinProductFeeInfoDto.getFeeRate(), 100, 14));
            finRateInfo.setFinprodId(finProduct.getFinprodId());
            finRateInfo.setFinprodType(finProduct.getFinprodType());
            finRateInfo.setFinprodType2(finProduct.getFinprodType2());
            finRateInfo.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinRateInfoList().add(finRateInfo);

            if (!StringUtil.isEmptyList(bussFinProductFeeInfoDto.getFinCashPlanList())) {
                for (int i = 1; i <= bussFinProductFeeInfoDto.getFinCashPlanList().size(); i++) {
                    FinCashPlan finCashPlan = bussFinProductFeeInfoDto.getFinCashPlanList().get(
                            i - 1);
                    finCashPlan.setUpdateUser(finProductCommonInfoDto.getUpdateUser());
                    finCashPlan.setCreateUser(finProductCommonInfoDto.getCreateUser());
                    finCashPlan.setCreateDept(finProductCommonInfoDto.getCreateDept());
                    finCashPlan.setCashId(cashId_fee);
                    finCashPlan.setCashNum(new Long(i));
                    finCashPlan.setCashType(DictCodeList.现金流类型_费用支付.getValue());
                    finCashPlan.setVdateUnadjust(finCashPlan.getVdate());
                    finCashPlan.setMdateUnadjust(finCashPlan.getMdate());
                    finCashPlan.setPayDateUnadjust(finCashPlan.getPayDate());
                    finCashPlan.setFinprodId(finProduct.getFinprodId());
                    finCashPlan.setFinprodType(finProduct.getFinprodType());
                    finCashPlan.setFinprodType2(finProduct.getFinprodType2());
                    finCashPlan.setBranch(finProductCommonInfoDto.getBranch());
                    baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);
                }
            }
        }
    }

    /**
     * 处理开放计划相关数据生成
     * <功能详细描述>
     *
     * @param finProductCommonInfoDto
     * @param baseFinProductBaseInfoDto
     * @throws Exception
     * @see
     * @since 1.0
     */
    private void procFinProductOpenPlan(FinProductCommonInfoDto finProductCommonInfoDto,
                                        BaseFinProductBaseInfoDto baseFinProductBaseInfoDto) throws InvocationTargetException, IllegalAccessException {
        if (finProductCommonInfoDto.getBussFinProductOpenPlanDto() != null) {
            // 现金流信息dtolist-->本金
            String cashId_amt = CommonUtil.generateCashFlowCode(
                    baseFinProductBaseInfoDto.getFinProduct().getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(), finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_本金.getValue());
            FinCashInfo finCashInfo_amt = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo_amt, finProductCommonInfoDto);
            finCashInfo_amt.setCashId(cashId_amt);
            finCashInfo_amt.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            finCashInfo_amt.setCashType2(DictCodeListExt1.现金流二级类型_本金.getValue());
            finCashInfo_amt.setCcy(finProductCommonInfoDto.getCcy());
            finCashInfo_amt.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
            finCashInfo_amt.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo_amt.setBranch(finProductCommonInfoDto.getBranch());
            this.checkDealModeIsCalc(finCashInfo_amt, finProductCommonInfoDto.getDealMode());
            baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_amt);

            // 开放规则dtolist
            BussFinProductOpenPlanDto bussFinProductOpenPlanDto = finProductCommonInfoDto.getBussFinProductOpenPlanDto();
            FinOpenRule finOpenRule = new FinOpenRule();
            BeanUtil.copyProperties(finOpenRule, finProductCommonInfoDto);
            finOpenRule.setCashId(cashId_amt);
            // update zhangweiqi 申赎类型从界面取值
            finOpenRule.setOpenType(bussFinProductOpenPlanDto.getOpenType());
            // finOpenRule.setOpenType(DictCodeList.申赎开放类型_申赎开放.getValue());
            finOpenRule.setCpriceRule(bussFinProductOpenPlanDto.getCpriceRule());// add zhangweiqi
            // 新增净值日规则
            finOpenRule.setOpenMode(bussFinProductOpenPlanDto.getOpenMode());
            finOpenRule.setOpenDays(bussFinProductOpenPlanDto.getOpenDays());
            finOpenRule.setOpenCycle(bussFinProductOpenPlanDto.getOpenCycle());
            finOpenRule.setOpenCycleUnit(bussFinProductOpenPlanDto.getOpenCycleUnit());
            if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getDayOfWeek())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_周几.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getDayOfWeek());
            } else if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getDayOfMonth())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_第几自然日.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getDayOfMonth());
            } else if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getWorkdayOfMonth())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_第几工作日.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getWorkdayOfMonth());
            }
            // 第几周周几
            else if (StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getWeekOfMonth())
                    && StringUtil.isNotEmpty(bussFinProductOpenPlanDto.getWorkdayOfWeek())) {
                finOpenRule.setDayType(DictCodeListExt1.日期类型_第几周周几.getValue());
                finOpenRule.setDayValue(bussFinProductOpenPlanDto.getWeekOfMonth() + ";"
                        + bussFinProductOpenPlanDto.getWorkdayOfWeek());
            }
            finOpenRule.setCalcMethod(bussFinProductOpenPlanDto.getCalcMethod());
            finOpenRule.setFirstOpenDate(bussFinProductOpenPlanDto.getFirstOpenDate());
            finOpenRule.setTradedayRule(bussFinProductOpenPlanDto.getTradedayRule());
            finOpenRule.setHdayOpenFlag(bussFinProductOpenPlanDto.getHdayOpenFlag());
            finOpenRule.setCalcRule(bussFinProductOpenPlanDto.getCalcRule());
            finOpenRule.setOpenDaysType(bussFinProductOpenPlanDto.getOpenDaysType());
            baseFinProductBaseInfoDto.getFinOpenRuleList().add(finOpenRule);

            // 开放计划dtolist
            if (!StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                for (FinOpenPlan finOpenPlan : bussFinProductOpenPlanDto.getFinOpenPlanList()) {
                    finOpenPlan.setCashId(cashId_amt);
                    // update zhangweiqi 申赎类型从界面取值
                    finOpenPlan.setOpenType(bussFinProductOpenPlanDto.getOpenType());
                    // finOpenPlan.setOpenType(DictCodeList.申赎开放类型_申赎开放.getValue());
                    finOpenPlan.setOpenDateStrUn(finOpenPlan.getOpenDateStr());
                    finOpenPlan.setOpenDateEndUn(finOpenPlan.getOpenDateEnd());
                    finOpenPlan.setOpenVdateUn(finOpenPlan.getOpenVdate());
                    finOpenPlan.setOpenMdateUn(finOpenPlan.getOpenMdate());
                    finOpenPlan.setOpenNdate(finOpenPlan.getOpenNdate());// add zhangweiqi 净值日
                    finOpenPlan.setFinprodId(
                            baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                    finOpenPlan.setBranch(finProductCommonInfoDto.getBranch());
                    finOpenPlan.setOpenStatus(DictCodeList.开放状态_开放.getValue());
                    baseFinProductBaseInfoDto.getFinOpenPlanList().add(finOpenPlan);
                }
            }
            // add by zhangweiqi 增加赎回的逻辑 begin 
            //周期型产品无赎回计划
            if (!StringUtils.equals(bussFinProductOpenPlanDto.getOpenType(),
                    DictCodeList.申赎开放类型_申赎开放.getValue())
                    && !StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())) {
                // 开放计划
                if (finProductCommonInfoDto.getBussFinProductOpenPlanRedDto() != null) {
                    // 开放规则dtolist
                    BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto = finProductCommonInfoDto.getBussFinProductOpenPlanRedDto();
                    FinOpenRule finOpenRuleRed = new FinOpenRule();
                    BeanUtil.copyProperties(finOpenRuleRed, bussFinProductOpenPlanRedDto);
                    finOpenRuleRed.setCashId(cashId_amt);
                    finOpenRuleRed.setOpenType(DictCodeList.申赎开放类型_赎回开放.getValue());
                    finOpenRuleRed.setCpriceRule(bussFinProductOpenPlanRedDto.getCpriceRule());// add
                    // zhangweiqi
                    // 新增净值日规则
                    finOpenRuleRed.setOpenMode(bussFinProductOpenPlanRedDto.getOpenMode());
                    finOpenRuleRed.setOpenDays(bussFinProductOpenPlanRedDto.getOpenDays());
                    finOpenRuleRed.setOpenCycle(bussFinProductOpenPlanRedDto.getOpenCycle());
                    finOpenRuleRed.setOpenCycleUnit(
                            bussFinProductOpenPlanRedDto.getOpenCycleUnit());
                    if (StringUtil.isNotEmpty(bussFinProductOpenPlanRedDto.getDayOfWeek())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_周几.getValue());
                        finOpenRuleRed.setDayValue(bussFinProductOpenPlanRedDto.getDayOfWeek());
                    } else if (StringUtil.isNotEmpty(bussFinProductOpenPlanRedDto.getDayOfMonth())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_第几自然日.getValue());
                        finOpenRuleRed.setDayValue(bussFinProductOpenPlanRedDto.getDayOfMonth());
                    } else if (StringUtil.isNotEmpty(
                            bussFinProductOpenPlanRedDto.getWorkdayOfMonth())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_第几工作日.getValue());
                        finOpenRuleRed.setDayValue(
                                bussFinProductOpenPlanRedDto.getWorkdayOfMonth());
                    }
                    // 第几周周几
                    else if (StringUtil.isNotEmpty(bussFinProductOpenPlanRedDto.getWeekOfMonth())
                            && StringUtil.isNotEmpty(
                            bussFinProductOpenPlanRedDto.getWorkdayOfWeek())) {
                        finOpenRuleRed.setDayType(DictCodeListExt1.日期类型_第几周周几.getValue());
                        finOpenRuleRed.setDayValue(
                                bussFinProductOpenPlanRedDto.getWeekOfMonth() + ";"
                                        + bussFinProductOpenPlanRedDto.getWorkdayOfWeek());
                    }
                    finOpenRuleRed.setCalcMethod(bussFinProductOpenPlanRedDto.getCalcMethod());
                    finOpenRuleRed.setFirstOpenDate(
                            bussFinProductOpenPlanRedDto.getFirstOpenDate());
                    finOpenRuleRed.setTradedayRule(bussFinProductOpenPlanRedDto.getTradedayRule());
                    finOpenRuleRed.setHdayOpenFlag(bussFinProductOpenPlanRedDto.getHdayOpenFlag());
                    finOpenRuleRed.setCalcRule(bussFinProductOpenPlanRedDto.getCalcRule());
                    finOpenRuleRed.setOpenDaysType(bussFinProductOpenPlanRedDto.getOpenDaysType());
                    baseFinProductBaseInfoDto.getFinOpenRuleList().add(finOpenRuleRed);

                    // 开放计划dtolist
                    if (!StringUtil.isEmptyList(
                            bussFinProductOpenPlanRedDto.getFinOpenPlanList())) {
                        for (FinOpenPlan finOpenPlanRed : bussFinProductOpenPlanRedDto.getFinOpenPlanList()) {
                            finOpenPlanRed.setCashId(cashId_amt);
                            finOpenPlanRed.setOpenType(DictCodeList.申赎开放类型_赎回开放.getValue());
                            finOpenPlanRed.setOpenDateStrUn(finOpenPlanRed.getOpenDateStr());
                            finOpenPlanRed.setOpenDateEndUn(finOpenPlanRed.getOpenDateEnd());
                            finOpenPlanRed.setOpenVdateUn(finOpenPlanRed.getOpenVdate());
                            finOpenPlanRed.setOpenMdateUn(finOpenPlanRed.getOpenMdate());
                            finOpenPlanRed.setOpenNdate(finOpenPlanRed.getOpenNdate());// add
                            // zhangweiqi
                            // 净值日
                            finOpenPlanRed.setFinprodId(
                                    baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                            finOpenPlanRed.setBranch(finProductCommonInfoDto.getBranch());
                            finOpenPlanRed.setOpenStatus(DictCodeList.开放状态_开放.getValue());
                            baseFinProductBaseInfoDto.getFinOpenPlanList().add(finOpenPlanRed);
                        }
                    }
                }

            }
            // add by zhangweiqi 增加赎回的逻辑 end
        }
    }

    /**
     * 产品浮动管理费需要保存的数据处理
     * <功能详细描述>
     *
     * @param finProductCommonInfoDto
     * @param baseFinProductBaseInfoDto
     * @param resetType
     * @throws Exception
     * @see
     * @since 1.0
     */
    private void procFinProductFloatFeeInfoData(FinProductCommonInfoDto finProductCommonInfoDto, BaseFinProductBaseInfoDto baseFinProductBaseInfoDto, FinProductFloatFeeDto finProductFloatFeeDto, String resetType, HashMap<String, String> cashMap)
            throws InvocationTargetException, IllegalAccessException {
        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getFeeId())) {
            FinProduct finProduct = baseFinProductBaseInfoDto.getFinProduct();
            String cashId_floatFee = CommonUtil.generateCashFlowCode(
                    finProduct.getFinprodId(),
                    DictCodeListExt1.现金流业务类型_金融产品.getValue(),
                    finProductCommonInfoDto.getBranch(),
                    DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            FinCashInfo finCashInfo_floatFee = new FinCashInfo();
            BeanUtil.copyProperties(finCashInfo_floatFee, finProductCommonInfoDto);
            finCashInfo_floatFee.setCashId(cashId_floatFee);
            finCashInfo_floatFee.setCashType1(DictCodeListExt1.现金流一级类型_费用.getValue());
            finCashInfo_floatFee.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
            finCashInfo_floatFee.setCcy(finProductCommonInfoDto.getCcy());
            finCashInfo_floatFee.setFinprodId(finProduct.getFinprodId());
            finCashInfo_floatFee.setFinprodType(DictCodeListExt1.现金流业务类型_金融产品.getValue());
            finCashInfo_floatFee.setBranch(finProductCommonInfoDto.getBranch());
            finCashInfo_floatFee.setFeeId(finProductFloatFeeDto.getFeeId());
            // zhangnn 2019/2/27 start
            // this.checkDealModeIsCalc(finCashInfo_floatFee, finProductCommonInfoDto.getDealMode());
            // 费用默认参与计算
            if (StringUtil.equals(DictCodeList.处理模式_周期型.getValue(), finProductCommonInfoDto.getDealMode())) {
                finCashInfo_floatFee.setIsCalc(DictCodeList.是否_否.getValue());
            } else {
                finCashInfo_floatFee.setIsCalc(DictCodeList.是否_是.getValue());
            }
            // zhangnn 2019/2/27 end
            baseFinProductBaseInfoDto.getFinCashInfoList().add(finCashInfo_floatFee);

            cashMap.put(finProductFloatFeeDto.getFeeId() + finProductCommonInfoDto.getFinprodId(), cashId_floatFee);//add by zhangweiqi

            //update zhangweiqi 新增浮动管理费 支付规则
            // 现金流支付规则
            FinCashPayRule finCashPayRule = new FinCashPayRule();
            BeanUtil.copyProperties(finCashPayRule, finProductCommonInfoDto);
            finCashPayRule.setCashId(cashId_floatFee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashPayRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashPayRule.setEffDate(DateUtil.platDateFromCache());
            }
            finCashPayRule.setFirstPayDate(finProductFloatFeeDto.getFirstPayDate());//add by zhangweiqi 首次计费结束日
            finCashPayRule.setPayCycle(finProductFloatFeeDto.getPayCycle());
            finCashPayRule.setEndTdayRule(finProductFloatFeeDto.getEndTdayRule());
            finCashPayRule.setPayTdayRule(finProductFloatFeeDto.getPayTdayRule());
            finCashPayRule.setCashUnitType(DictCodeList.现金流单位_总额.getValue());
            finCashPayRule.setFinprodId(finProduct.getFinprodId());
            finCashPayRule.setFinprodType(finProduct.getFinprodType());
            finCashPayRule.setFinprodType2(finProduct.getFinprodType2());
            finCashPayRule.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinCashPayRuleList().add(finCashPayRule);

            //add by gcl 2019/4/29 start
            FinCashCalcRule finCashCalcRule = new FinCashCalcRule();
            BeanUtil.copyProperties(finCashCalcRule, finProductCommonInfoDto);
            finCashCalcRule.setCashId(cashId_floatFee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finCashCalcRule.setEffDate(finProductCommonInfoDto.getVdate());
            } else {
                finCashCalcRule.setEffDate(DateUtil.platDateFromCache());
            }
            finCashCalcRule.setCalcType(finProductCommonInfoDto.getCalcType());
            finCashCalcRule.setBasis(finProductCommonInfoDto.getBasis());
            finCashCalcRule.setYield(
                    ArithmeticUtil.divide(finProductCommonInfoDto.getYield(), 100, 14));
            finCashCalcRule.setIsInitial(BussinessDictCodeList.是否_是.getValue());
            finCashCalcRule.setFinprodId(finProduct.getFinprodId());
            finCashCalcRule.setFinprodType(finProduct.getFinprodType());
            finCashCalcRule.setFinprodType2(finProduct.getFinprodType2());
            finCashCalcRule.setBranch(finProductCommonInfoDto.getBranch());
            baseFinProductBaseInfoDto.getFinCashCalcRuleList().add(finCashCalcRule);
            //add by gcl 2019/4/29 end

            // 业绩报酬计算规则
            FinFloatCalcRule finFloatCalcRule = new FinFloatCalcRule();
            BeanUtil.copyProperties(finFloatCalcRule, finProductCommonInfoDto);
            finFloatCalcRule.setCashId(cashId_floatFee);
            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                finFloatCalcRule.setEffDate(finProductCommonInfoDto.getVdate());
                // 一体化对应etliu
                // finFloatCalcRule.setRelaeseDate(finProductCommonInfoDto.getVdate());
            } else {
                finFloatCalcRule.setEffDate(DateUtil.platDateFromCache());
                // 一体化对应etliu
                // finFloatCalcRule.setRelaeseDate(platFormDate);
            }
            finFloatCalcRule.setBaseRule(DictCodeListExt1.业绩报酬计算规则_业绩基准.getValue());
            if (StringUtil.isNotEmpty(finProductFloatFeeDto.getFloatBaseRule())) {
                finFloatCalcRule.setBaseRule(finProductFloatFeeDto.getFloatBaseRule());
            }
            finFloatCalcRule.setAstVdateType(finProductFloatFeeDto.getNetAssetValueDay());
            finFloatCalcRule.setBaseDateType(finProductFloatFeeDto.getNetAssetValueDay());

            // 货币型产品 资产净值日 默认存 前一自然日
            if (!StringUtil.isNotEmpty(finProductFloatFeeDto.getNetAssetValueDay()) && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProductCommonInfoDto.getProfitType())) {
                finFloatCalcRule.setAstVdateType(DictCodeList.基数日期类型_前一自然日.getValue());
                finFloatCalcRule.setBaseDateType(DictCodeList.基数日期类型_前一自然日.getValue());
            }
            finFloatCalcRule.setBaseType(finProductFloatFeeDto.getBasicBill());
            finFloatCalcRule.setBasis(finProductFloatFeeDto.getBasis());
            //finProductFloatFeeDto.getFinCashPlanList();by 丁俊峰
            if (!StringUtil.isEmptyList(finProductFloatFeeDto.getFinCashPlanList())) {
                for (int i = 1; i <= finProductFloatFeeDto.getFinCashPlanList().size(); i++) {
                    FinCashPlan finCashPlan = finProductFloatFeeDto.getFinCashPlanList().get(
                            i - 1);
                    finCashPlan.setUpdateUser(finProductCommonInfoDto.getUpdateUser());
                    finCashPlan.setCreateUser(finProductCommonInfoDto.getCreateUser());
                    finCashPlan.setCreateDept(finProductCommonInfoDto.getCreateDept());
                    finCashPlan.setCashId(cashId_floatFee);
                    finCashPlan.setCashNum(new Long(i));
                    finCashPlan.setCashType(DictCodeList.现金流类型_费用支付.getValue());
                    finCashPlan.setVdateUnadjust(finCashPlan.getVdate());
                    finCashPlan.setMdateUnadjust(finCashPlan.getMdate());
                    finCashPlan.setPayDateUnadjust(finCashPlan.getPayDate());
                    finCashPlan.setFinprodId(finProduct.getFinprodId());
                    finCashPlan.setFinprodType(finProduct.getFinprodType());
                    finCashPlan.setFinprodType2(finProduct.getFinprodType2());
                    finCashPlan.setBranch(finProductCommonInfoDto.getBranch());
                    baseFinProductBaseInfoDto.getFinCashPlanList().add(finCashPlan);
                }
            }
            // 一体化对应etliu start
            // 浮动管理费随业绩基准重置而重置
            if (StringUtil.isNotBlank(resetType)) {
                finFloatCalcRule.setActRule(resetType);
            }
            // 一体化对应etliu end

            finFloatCalcRuleMapper.insert(finFloatCalcRule);

            FinBaseRuleInfo finBaseRuleInfo = new FinBaseRuleInfo();
            String baseRuleId = CommonUtil.generateCashFlowCode(finProduct.getFinprodId(), null, finProductCommonInfoDto.getBranch(), DictCodeListExt1.业绩基准类型_浮动管理费.getValue());
            if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType()) && StringUtil.isNotEmpty(finProductFloatFeeDto.getRateType())) {
                finBaseRuleInfo.setBaseRuleId(baseRuleId);
                finBaseRuleInfo.setBaseRuleType(DictCodeListExt1.披露类型_单值披露.getValue());
                finBaseRuleInfo.setBranch(finProductCommonInfoDto.getBranch());
                finBaseRuleInfo.setRateType(finProductFloatFeeDto.getRateType());
                finBaseRuleInfo.setRate(ArithmeticUtil.divide(finProductFloatFeeDto.getRate(), 100, 14));
                finBaseRuleInfo.setIndexId(finProductFloatFeeDto.getIndexId());
                finBaseRuleInfo.setCoefficient(finProductFloatFeeDto.getCoefficient());
                finBaseRuleInfo.setResetType(finProductFloatFeeDto.getResetType());
                finBaseRuleInfo.setRemark(finProductFloatFeeDto.getRemark());
                if (StringUtil.isNotEmpty(finProduct.getVdate())) {
                    finBaseRuleInfo.setEffDate(finProduct.getVdate());
                } else {
                    finBaseRuleInfo.setEffDate(DateUtil.platDateFromCache());
                }
                if (StringUtil.equals(finProductFloatFeeDto.getRateType(),
                        DictCodeList.利率类型_浮动利率.getValue())) {
                    MstIndexInfo mstIndexInfo = mstIndexInfoMapper.selectByPrimaryKey(
                            finProductFloatFeeDto.getIndexId());
                    if (mstIndexInfo != null) {
                        finBaseRuleInfo.setIndexSource(mstIndexInfo.getIndexType());
                    } else {
                        finBaseRuleInfo.setIndexSource(DictCodeListExt1.基准类型_2_利率.getValue());
                    }
                }
                finBaseRuleInfo.setFinprodId(finProduct.getFinprodId());
                finBaseRuleInfo.setSpreadRate(
                        ArithmeticUtil.divide(finProductFloatFeeDto.getSpreadRate(), 100, 14));

                // 一体化对应etliu start
                resetType = finProductFloatFeeDto.getResetType(); // 业绩基准重置规则
                // 一体化对应etliu end

                finBaseRuleInfoMapper.insert(finBaseRuleInfo);
            }

            // 一体化对应etliu start
//            // 现金流计提规则
//            FinCashCountingRule finCashCountingRule = new FinCashCountingRule();
//            BeanUtil.copyProperties(finCashCountingRule, finProductCommonInfoDto);
//            finCashCountingRule.setCashId(cashId_floatFee);
//            if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
//                finCashCountingRule.setEffDate(finProductCommonInfoDto.getVdate());
//            }
//            else {
//                finCashCountingRule.setEffDate(platFormDate);
//            }
//            finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
//            finCashCountingRuleMapper.insert(finCashCountingRule);

            // 净值型、货币型产品：估值：浮动管理费有计提频率的概念，分高频和固定频率。前台没管
            if (!StringUtil.equals(finProductCommonInfoDto.getProfitType(), DictCodeList.收益类型_收益型.getValue())) {
                // 开放式产品
                if (StringUtil.equals(DictCodeList.开放类型_开放式.getValue(), finProductCommonInfoDto.getOperationType())
                        && !StringUtil.isEmptyList(baseFinProductBaseInfoDto.getFinOpenRuleList())) {
                    FinOpenRule finOpenRuleBak = baseFinProductBaseInfoDto.getFinOpenRuleList().get(0);
                    // 高频开放（开放规则.开放模式 = 高频开放）的给高频计提
                    if (StringUtil.equals(DictCodeList.开放模式_高频开放.getValue(), finOpenRuleBak.getOpenMode())) {
                        //按照计提方式区分是否高频
                        //if (StringUtil.equals(DictCodeList.计提类型_高频.getValue(), finProductFloatFeeDto.getAccrType())) {
                        // 1、现金流计提规则表：
                        FinFloatCountingRule finCashCountingRule = new FinFloatCountingRule();
                        BeanUtil.copyProperties(finCashCountingRule, finProductCommonInfoDto);
                        finCashCountingRule.setCashId(cashId_floatFee);
                        if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                            finCashCountingRule.setEffDate(finProductCommonInfoDto.getVdate());
                        } else {
                            finCashCountingRule.setEffDate(DateUtil.platDateFromCache());
                        }
                        finCashCountingRule.setAccrType(DictCodeList.计提类型_高频.getValue());
                        finCashCountingRule.setAccrCycle(1L);
                        finCashCountingRule.setAccrCycleUnit(DictCodeList.频率_日.getValue());
                        finCashCountingRule.setAccrTdayRule(finProductFloatFeeDto.getAccrTdayRule());
                        //finCashCountingRule.setAccrType(finProductFloatFeeDto.getAccrType());
                        finCashCountingRule.setIntervalRule(finProductFloatFeeDto.getInterValRule());
                        finCashCountingRule.setfAccrEndDate(finProductFloatFeeDto.getfAccrEndDate());
                        finCashCountingRule.seteAccrDate(finProductFloatFeeDto.geteAccrDate());
                        finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());

                        //finCashCountingRule.setAccrTdayRule(DictCodeList.营业日准则_不调整.getValue());//update by yejiejie 2019-2-14
                        //update zhangweiqi 计提结束日
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.geteAccrDate())) {
                            finCashCountingRule.seteAccrDate(finProductFloatFeeDto.geteAccrDate());
                        }
                        ////update zhangweiqi 区间规则
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getInterValRule())) {
                            finCashCountingRule.setIntervalRule(finProductFloatFeeDto.getInterValRule());
                        }

                        ////update zhangweiqi 首个计提区间结束日
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getfAccrEndDate())) {
                            finCashCountingRule.setfAccrEndDate(finProductFloatFeeDto.getfAccrEndDate());
                        }

                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getAccrTdayRule())) {
                            finCashCountingRule.setAccrTdayRule(finProductFloatFeeDto.getAccrTdayRule());
                        }

                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getProvisionFirstDay())) {
                            finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
                        }
                        finCashCountingRuleMapper.insert(finCashCountingRule);

                        // 2、业绩报酬计提计划：空
                    }
                    // 非高频开放（开放规则.开放模式 != 高频开放）的存计提计划，按开放周期结束日计提
                    else {
                        // 1、现金流计提规则表：
                        FinFloatCountingRule finCashCountingRule = new FinFloatCountingRule();
                        BeanUtil.copyProperties(finCashCountingRule, finProductCommonInfoDto);
                        finCashCountingRule.setCashId(cashId_floatFee);
                        if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                            finCashCountingRule.setEffDate(finProductCommonInfoDto.getVdate());
                        } else {
                            finCashCountingRule.setEffDate(DateUtil.platDateFromCache());
                        }
                        finCashCountingRule.setAccrTdayRule(finProductFloatFeeDto.getAccrTdayRule());
                        //finCashCountingRule.setAccrType(finProductFloatFeeDto.getAccrType());
                        finCashCountingRule.setIntervalRule(finProductFloatFeeDto.getInterValRule());
                        finCashCountingRule.setfAccrEndDate(finProductFloatFeeDto.getfAccrEndDate());
                        finCashCountingRule.seteAccrDate(finProductFloatFeeDto.geteAccrDate());
                        finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
                        finCashCountingRule.setAccrType(DictCodeList.计提类型_固定频率.getValue());
                        finCashCountingRule.setAccrCycle(finOpenRuleBak.getOpenCycle());
                        finCashCountingRule.setAccrCycleUnit(finOpenRuleBak.getOpenCycleUnit());
                        finCashCountingRule.setAccrDayType(finOpenRuleBak.getDayType());
                        finCashCountingRule.setAccrDayValue(finOpenRuleBak.getDayValue());
                        // finCashCountingRule.setAccrTdayRule(DictCodeList.营业日准则_不调整.getValue());
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getProvisionFirstDay())) {
                            finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
                        }
                        //update zhangweiqi 计提结束日
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.geteAccrDate())) {
                            finCashCountingRule.seteAccrDate(finProductFloatFeeDto.geteAccrDate());
                        }
                        ////update zhangweiqi 区间规则
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getInterValRule())) {
                            finCashCountingRule.setIntervalRule(finProductFloatFeeDto.getInterValRule());
                        }

                        ////update zhangweiqi 首个计提区间结束日
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getfAccrEndDate())) {
                            finCashCountingRule.setfAccrEndDate(finProductFloatFeeDto.getfAccrEndDate());
                        }
                        if (StringUtil.isNotEmpty(finProductFloatFeeDto.getAccrTdayRule())) {
                            finCashCountingRule.setAccrTdayRule(finProductFloatFeeDto.getAccrTdayRule());
                        }

                        finCashCountingRuleMapper.insert(finCashCountingRule);

                        // 2、业绩报酬计提计划：
                        //update zhangweiqi 开放式的固定频率的按照计提规则计提  不按照
                       /*
                        List<Date> openMList=new ArrayList<Date>();//update zhangweiqi 对于申赎不同的修改
                        if (!StringUtil.isEmptyList(finOpenPlanList)) {
                            for (FinOpenPlan finOpenPlan : finOpenPlanList) {
                                if(openMList.contains(finOpenPlan.getOpenMdate())) continue;
                                openMList.add(finOpenPlan.getOpenMdate());
                                //if(!StringUtils.equals(DictCodeList.申赎开放类型_赎回开放.getValue(), finOpenPlan.getOpenType())){
                                FinFloatAccrualSche finFloatAccrualSche = new FinFloatAccrualSche();
                                finFloatAccrualSche.setCashId(cashId_floatFee);
                                finFloatAccrualSche.setAccrDate(finOpenPlan.getOpenMdate());
                                finFloatAccrualSche.setAccrStrDate(DateUtil.addDay(finOpenPlan.getOpenVdate(), -1));
                                finFloatAccrualSche.setAccrEndDate(finOpenPlan.getOpenMdate());
                                finFloatAccrualScheMapper.insert(finFloatAccrualSche);
                             //}
                            }
                        }*/
                        List<Date> busiDate = new ArrayList<Date>();

                        BussFinProductOpenPlanDto bussFinProductOpenPlanDto = finProductCommonInfoDto.getBussFinProductOpenPlanDto();


                        BussFinProductOpenPlanDto bussFinProductOpenPlanRedDto = finProductCommonInfoDto.getBussFinProductOpenPlanRedDto();
                        if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProductCommonInfoDto.getProfitType())) {
                            if (bussFinProductOpenPlanDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanDto.getFinOpenPlanList()) {
                                    addDateToList(busiDate, plan.getOpenNdate());
                                }
                            }
                            if (bussFinProductOpenPlanRedDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanRedDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanRedDto.getFinOpenPlanList()) {
                                    addDateToList(busiDate, plan.getOpenNdate());
                                }
                            }
                        } else if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProductCommonInfoDto.getProfitType())) {
                            if (bussFinProductOpenPlanDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanDto.getFinOpenPlanList()) {

                                    addDateToList(busiDate, plan.getOpenDateEnd());
                                }
                            }
                            if (bussFinProductOpenPlanRedDto != null && !StringUtil.isEmptyList(bussFinProductOpenPlanRedDto.getFinOpenPlanList())) {
                                for (FinOpenPlan plan : bussFinProductOpenPlanRedDto.getFinOpenPlanList()) {
                                    addDateToList(busiDate, plan.getOpenDateEnd());
                                }
                            }
                        }

                        addDateToList(busiDate, finProductCommonInfoDto.getVdate());
                        addDateToList(busiDate, finProductCommonInfoDto.getMdate());
                        addDateToList(busiDate, finProductFloatFeeDto.getProvisionFirstDay());
                        addDateToList(busiDate, finProductFloatFeeDto.geteAccrDate());
                        Date startDate = busiDate.get(0);
                        boolean flag = true;
                        for (Date endDate : busiDate) {
//                            if(startDate.compareTo(endDate)==0){
//                                startDate = endDate;
//                                continue;
//                            }
                            if (endDate.compareTo(finProductFloatFeeDto.getProvisionFirstDay()) < 0) {
                                startDate = endDate;
                                continue;
                            }
                            if (endDate.compareTo(finProductFloatFeeDto.geteAccrDate()) > 0) {
                                startDate = endDate;
                                continue;
                            }
                            FinFloatAccrualSche finFloatAccrualScheFirst = new FinFloatAccrualSche();
                            BeanUtil.copyProperties(finFloatAccrualScheFirst, finProductCommonInfoDto);
                            finFloatAccrualScheFirst.setCashId(cashId_floatFee);
                            finFloatAccrualScheFirst.setAccrDate(endDate);
                            finFloatAccrualScheFirst.setAccrStrDate(startDate);
                            if (flag) {
                                finFloatAccrualScheFirst.setAccrStrDate(DateUtil.addDay(startDate, -1));
                                flag = false;
                            }

                            finFloatAccrualScheFirst.setAccrEndDate(endDate);
                            finFloatAccrualScheFirst.setValDate(endDate);
                            finFloatAccrualScheMapper.insert(finFloatAccrualScheFirst);
                            startDate = endDate;
                        }

                    }
                }
                // 封闭式产品 存到期日计提。
                else if (StringUtil.equals(DictCodeList.开放类型_封闭式.getValue(), finProductCommonInfoDto.getOperationType())) {
                    // 1、现金流计提规则表：
                    FinFloatCountingRule finCashCountingRule = new FinFloatCountingRule();
                    BeanUtil.copyProperties(finCashCountingRule, finProductCommonInfoDto);
                    finCashCountingRule.setCashId(cashId_floatFee);
                    if (StringUtil.isNotEmpty(finProductCommonInfoDto.getVdate())) {
                        finCashCountingRule.setEffDate(finProductCommonInfoDto.getVdate());
                    } else {
                        finCashCountingRule.setEffDate(DateUtil.platDateFromCache());
                    }
                    finCashCountingRule.setAccrType(DictCodeList.计提类型_固定频率.getValue());
                    finCashCountingRule.setAccrCycle(null);
                    finCashCountingRule.setAccrCycleUnit(DictCodeList.频率_到期一次.getValue());
                    finCashCountingRule.setAccrDayType(null);
                    finCashCountingRule.setAccrDayValue(null);
                    finCashCountingRule.setAccrTdayRule(DictCodeList.营业日准则_不调整.getValue());
                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.getProvisionFirstDay())) {
                        finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
                    }
                    //add by yejiejie 2019-2-14 start
                    //update zhangweiqi 计提结束日
                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.geteAccrDate())) {
                        finCashCountingRule.seteAccrDate(finProductFloatFeeDto.geteAccrDate());
                    }
                    ////update zhangweiqi 区间规则
                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.getInterValRule())) {
                        finCashCountingRule.setIntervalRule(finProductFloatFeeDto.getInterValRule());
                    }

                    ////update zhangweiqi 首个计提区间结束日
                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.getfAccrEndDate())) {
                        finCashCountingRule.setfAccrEndDate(finProductFloatFeeDto.getfAccrEndDate());
                    }

                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.getAccrTdayRule())) {
                        finCashCountingRule.setAccrTdayRule(finProductFloatFeeDto.getAccrTdayRule());
                    }

//                    if (StringUtil.isNotEmpty(finProductFloatFeeDto.getProvisionFirstDay())) {
//                      finCashCountingRule.setfAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
//                    }
                    //add by yejiejie 2019-2-14 end
                    finCashCountingRuleMapper.insert(finCashCountingRule);

                    // 2、业绩报酬计提计划：
                    FinFloatAccrualSche finFloatAccrualSche = new FinFloatAccrualSche();
                    finFloatAccrualSche.setCashId(cashId_floatFee);
                    finFloatAccrualSche.setAccrDate(finProductFloatFeeDto.getProvisionFirstDay());
                    finFloatAccrualSche.setAccrStrDate(DateUtil.addDay(finProductCommonInfoDto.getVdate(), -1));
                    finFloatAccrualSche.setAccrEndDate(finProductFloatFeeDto.getProvisionFirstDay());
                    finFloatAccrualScheMapper.insert(finFloatAccrualSche);
                }
            }
            // 一体化对应etliu end

            // 业绩报酬计算规则明细
            List<FinFloatCalcRuleDetail> finFloatCalcRuleDetailList = finProductFloatFeeDto.getFinFloatCalcRuleDetailDtoList();
            Date effDate = StringUtil.isNotEmpty(
                    finProductCommonInfoDto.getVdate()) ? finProductCommonInfoDto.getVdate() : DateUtil.platDateFromCache();
            if (!StringUtil.isEmptyList(finFloatCalcRuleDetailList)) {
                for (FinFloatCalcRuleDetail finFloatCalcRuleDetail : finFloatCalcRuleDetailList) {
                    BeanUtil.copyProperties(finFloatCalcRuleDetail, finProductCommonInfoDto);
                    finFloatCalcRuleDetail.setCashId(cashId_floatFee);
                    finFloatCalcRuleDetail.setEffDate(effDate);
                    finFloatCalcRuleDetail.setLev(finFloatCalcRuleDetail.getLev());
                    finFloatCalcRuleDetail.setBaseRuleId(
                            baseRuleId);// 业绩基准代码
                    finFloatCalcRuleDetail.setSpreadRate(ArithmeticUtil.divide(
                            finFloatCalcRuleDetail.getSpreadRate(), 100, 14));
                    finFloatCalcRuleDetail.setChargeRatio(ArithmeticUtil.divide(
                            finFloatCalcRuleDetail.getChargeRatio(), 100, 14));
                    finFloatCalcRuleDetailMapper.insert(finFloatCalcRuleDetail);
                }
            }

            // 现金流计提计划 --此处一体化整合需要删除，存储入finFloatCalcRule表
            /*
            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(),
                finProductCommonInfoDto.getFinprodType2())) {
                Date startDate = finProductFloatFeeDto.getProvisionFirstDay();
                if(startDate!=null){
                if(StringUtil.equals(finProductFloatFeeDto.getAccrType(), DictCodeList.计提类型_高频)){
                while (DateUtil.compareDate(startDate,
                    finProductCommonInfoDto.getMdate()) < 1) {
                    FinFloatAccrualSche finFloatAccrualSche = new FinFloatAccrualSche();
                    BeanUtil.copyProperties(finFloatAccrualSche, finProductCommonInfoDto);
                    finFloatAccrualSche.setCashId(cashId_floatFee);
                    finFloatAccrualSche.setAccrDate(startDate);
                    finFloatAccrualSche.setAccrStrDate(startDate);
                    finFloatAccrualSche.setAccrEndDate(startDate);
                    finFloatAccrualSche.setValDate(startDate);
                    finFloatAccrualScheMapper.insert(finFloatAccrualSche);
                    startDate = DateUtil.addDay(startDate, 1);
                }
                //update zhangweiqi 固定频率开放  begin
                }else{
                    int month=1;
                    if(StringUtil.equals(DictCodeList.频率_季.getValue(), finProductFloatFeeDto.getInterValRule())){
                        month=3;
                    }else if(StringUtil.equals(DictCodeList.频率_半年.getValue(), finProductFloatFeeDto.getInterValRule())){
                        month=6;
                    }else if(StringUtil.equals(DictCodeList.频率_年.getValue(), finProductFloatFeeDto.getInterValRule())){
                        month=12;
                    }

                     FinFloatAccrualSche finFloatAccrualScheFirst = new FinFloatAccrualSche();
                     BeanUtil.copyProperties(finFloatAccrualScheFirst, finProductCommonInfoDto);
                     finFloatAccrualScheFirst.setCashId(cashId_floatFee);
                     finFloatAccrualScheFirst.setAccrDate(finProductFloatFeeDto.getfAccrEndDate());
                     finFloatAccrualScheFirst.setAccrStrDate(startDate);
                     finFloatAccrualScheFirst.setAccrEndDate(finProductFloatFeeDto.getfAccrEndDate());
                     finFloatAccrualScheFirst.setValDate(finProductFloatFeeDto.getfAccrEndDate());
                     finFloatAccrualScheMapper.insert(finFloatAccrualScheFirst);
                     startDate =finProductFloatFeeDto.getfAccrEndDate();

                     while (
                            (finProductFloatFeeDto.geteAccrDate().getYear()-startDate.getYear())*12+
                            (finProductFloatFeeDto.geteAccrDate().getMonth()-startDate.getMonth())<=month) {//update zhangweiqi 计提结束日
                         FinFloatAccrualSche finFloatAccrualSche = new FinFloatAccrualSche();
                         BeanUtil.copyProperties(finFloatAccrualSche, finProductCommonInfoDto);
                         finFloatAccrualSche.setCashId(cashId_floatFee);
                         finFloatAccrualSche.setAccrDate(DateUtil.addMonthDate(startDate, month));
                         finFloatAccrualSche.setAccrStrDate(startDate);
                         finFloatAccrualSche.setAccrEndDate(DateUtil.addMonthDate(startDate, month));
                         finFloatAccrualSche.setValDate(DateUtil.addMonthDate(startDate, month));
                         finFloatAccrualScheMapper.insert(finFloatAccrualSche);
                         startDate = DateUtil.addMonthDate(startDate, month);
                     }
                }
                //update zhangweiqi 固定频率开放  end
                }
            }*/
        }

    }

    /**
     * 按降序向列表添加 日期
     *
     * @param list 日期列表
     * @param date 日期
     * @see
     * @since 1.0
     */
    private void addDateToList(List<Date> list, Date date) {
        if (date == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).compareTo(date) == 0) {
                return;
            }
            if (list.get(i).compareTo(date) > 0) {
                list.add(i, date);
                return;
            }
        }
        list.add(date);
    }

    /**
     * 获取行内标识码
     *
     * @param map 传入条件
     * @return
     */
    public List<SelectOptionDto> queryRepFinprodInsId(Map<String, Object> map) {
        return productCommonMapper.queryRepFinprodInsId(map);
    }

    /**
     * 获取产品款型下拉框
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param param
     * @param page
     * @param length
     * @return
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getProdStyle(Map<String, Object> param, int page, int length) {
        if (StringUtil.isBlank((String) param.get("style"))) {
            param.put("style", "01");
        }
        return this.queryForPageForSelectReturn(page, length, () -> productCommonMapper.getProdStyle(param));
    }

    /**
     * 查询机构
     *
     * @param map
     * @return 结
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Page<SelectOptionDto> queryTrusteeId(Map<String, Object> map, int page, int length) {
        String pageModel = (String) map.get("pageModel");
        if (!(pageModel != null && StringUtil.equals(pageModel, GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE))) {
            map.put("isReference", DictCodeList.是否_否.getValue());
        }

        Page<SelectOptionDto> selcectList = queryForPageForSelectReturn(page, length,
                () -> productCommonMapper.queryTrusteeId(map));
//		List<SelectOptionDto> mstCustomerNatureList = selcectList.getRoot();
//		List<SelectOptionDto> list = new ArrayList<SelectOptionDto>();
//		for (int i = 0; i < mstCustomerNatureList.size(); i++) {
//			SelectOptionDto dto = new SelectOptionDto();
//			List<String> stringList = StringUtil.splitToList(GlobalConstant.COMMA,
//					(String) mstCustomerNatureList.get(i).getObject());
//			// 查看模式下有可能该产品的托管机构已经没有托管机构的标识，但是还是需要显示出来。
//			if (pageModel != null && StringUtil.equals(pageModel, GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)) {
//				dto.setId(mstCustomerNatureList.get(i).getId());
//				dto.setText(mstCustomerNatureList.get(i).getText());
//				list.add(dto);
//			} else {
//				if (StringUtil.isNotEmpty(stringList.get(3))) {
//					dto.setId(mstCustomerNatureList.get(i).getId());
//					dto.setText(mstCustomerNatureList.get(i).getText());
//					list.add(dto);
//				}
//			}
//		}
//		Page<SelectOptionDto> pag = new Page<>();
//		pag.setRoot(list);
//		pag.setRecords(list.size());
        return selcectList;
    }

    /**
     * 编号生成
     *
     * @param map
     */
    public String getTradeid(Map<String, Object> map) {
        String str = (String) map.get("str");
        String formatDate = (String) map.get("formatDate");
        String flag = (String) map.get("flag");
        int sequences;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            // dBUtil.getSequenceIndex(sequenceType);
            sequences = productCommonMapper.queryPdSequences();
        } else {
            sequences = productCommonMapper.queryOrderSequences();
        }
        // 组装8位序列号 不足补0
        String sequence = org.apache.commons.lang3.StringUtils.leftPad(String.valueOf(sequences), 8, '0');

        String tradeid = str + formatDate + sequence;
        return tradeid;
    }

    public List<SelectOptionDto> getUserNameList(Map<String, Object> pageParam) {
        return productCommonMapper.selectByUserName(pageParam);
    }
}