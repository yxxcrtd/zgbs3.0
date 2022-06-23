package com.joyintech.service.valuation.invest.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jixu.joyin2jixu.service.Joyin2JixuInterface;
import com.jixu.msg.dto.JJBkCashAcctDto;
import com.jixu.msg.dto.JJPortfolioDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.service.SystemToolService;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.dto.ApiTrdCommDto;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dao.ValInterfCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.dealcommon.DealCommonService;
import com.joyintech.tams.valuation.common.interf.base.util.InterfHelper;
import com.joyintech.tams.valuation.common.interf.common.ResultCode;
import com.joyintech.tams.valuation.common.interf.common.ResultObject;
import com.joyintech.tams.valuation.common.interf.invest.constant.ResultMapKey;
import com.joyintech.tams.valuation.common.interf.val.dao.BokTradeBuildMapper;
import com.joyintech.tams.valuation.common.interf.val.dao.BorrowBaseInfoInitMapper;
import com.joyintech.tams.valuation.common.interf.val.dto.ProdInfoDto;
import com.joyintech.tams.valuation.common.interf.val.service.ValSysParaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * 资管接口共通Service
 *
 * @author zhangwei
 * @version 1.0 2019年1月11日
 * @see ValApiCommonService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ValApiCommonService {
	/**
	 * 几许接口
	 */
	@Autowired
	private Joyin2JixuInterface joyin2JixuInterface;

    /**
     * 接口共通
     */
    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * 系统工具类
     */
    @Autowired
    private SystemToolService systemToolService;

    /**
     * 账套
     */
    @Autowired
    private BokBooksetMapper bokBooksetMapper;

    /**
     * 账套信息
     */
    @Autowired
    private BokBooksetDateMapper bokBooksetDateMapper;

    /**
     * 账套配置
     */
    @Autowired
    private ValBokBooksetExtBatchMapper valBokBooksetExtBatchMapper;

    /**
     * 核算主体信息
     */
    @Autowired
    private BokAcctSubjectInfoMapper bokAcctSubjectInfoMapper;

    /**
     * 核算分层信息
     */
    @Autowired
    private ValAcctLayeringInfoBatchMapper valAcctLayeringInfoBatchMapper;

    /**
     * 账务明细代码
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 现金流信息
     */
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;

    /**
     * 估值方案
     */
    @Autowired
    private BokBooksetPlanBatchMapper bokBooksetPlanBatchMapper;

    /**
     * 账套清算账户
     */
    @Autowired
    private ValBooksetCapacctInfoBatchMapper valBooksetCapacctInfoBatchMapper;

    /**
     * 交易入账
     */
    @Autowired
    private BokTradeBuildMapper bokTradeBuildMapper;

    /**
     * 结算指令
     */
    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;

    /**
     * 结算指令扩展
     */
    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;

    /**
     * 入账信息
     */
    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    /**
     * 入账信息明细
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;

    /**
     * 同业借款基本信息初始化
     */
    @Autowired
    private BorrowBaseInfoInitMapper borrowBaseInfoInitMapper;

    /**
     * 交易类金融产品
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    @Autowired
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private ValFinProductAcctBatchMapper valFinProductAcctBatchMapper;

    /**
     * 系统参数
     */
    @Autowired
    private ValSysParaService valSysParaService;

    @Autowired
    private DealCommonService dealCommonService;

    /**
     * 金融产品表
     */
    @Autowired
    private TrdProductDealMapper trdProductDealMapper;

    /**
     * 金融产品交易附属表
     */
    @Autowired
    private TrdProductDealAddMapper trdProductDealAddMapper;

    /**
     * 接口共通类Mapper
     */
    @Autowired
    private ValInterfCommonMapper valInterfCommonMapper;

    /**
     * 金融产品交易费用明细Mapper
     */
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;
    
    //金融产品实物交易表
    @Autowired
    private TrdFinProductBarterMapper trdFinProductBarterMapper;

    /**
     * 几许接口service
     */
    @Autowired
    private ValInvest2JixuService valInvest2JixuService;
    /**
     * 产品其他信息初始化
     *
     * @param prodInfoDto 产品标的Dto
     * @param productAcctList 金融产品账户List传值要求：为产品的全部资金账户
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
	@SuppressWarnings("unchecked")
	public ResultObject prodOtherInfoInit(ProdInfoDto prodInfoDto, List<StlFinProductAcct> productAcctList) {

        // 定义 变量.resultObject
        ResultObject resultObject = new ResultObject();
        // 变量.resultObject.处理结果代码 = "0000"
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 定义 变量.分层代码
        String layeringId = null;
        // 定义 变量.核算分层信息List
        List<BokAcctLayeringInfo> listBokAcctLayeringInfo = Lists.newArrayList();
        // 定义 变量.入库账套Dto
        BokBookset bokBookset = null;
        // 定义 变量.入库账套日期Dto
        BokBooksetDate bokBooksetDate = null;
        // 定义 变量.入库账套配置List
        List<BokBooksetExt> listBokBooksetExt = Lists.newArrayList();
        // 定义 变量.入库核算主体信息Dto
        BokAcctSubjectInfo bokAcctSubjectInfo = null;
        // 定义 变量.入库核算分层信息List
        List<BokAcctLayeringInfo> listEnterBokAcctLayeringInfo = Lists.newArrayList();
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.入库现金流信息List
        List<FinCashInfo> listFinCashInfo = Lists.newArrayList();
        // 定义 变量.入库估值方案List
        List<BokBooksetPlan> listBokBooksetPlan = Lists.newArrayList();
        // 定义 变量.入库账套清算账户List
        List<BokBooksetCapacctInfo> listBokBooksetCapacctInfo = Lists.newArrayList();
        // 定义 变量.入库金融产品账户List = 输入.金融产品账户信息List

        /**ADD BY WUJIE ON 20190726 START**/
        //定义 变量.几许产品信息
        JJPortfolioDto jJPortfolio = null;
        //定义 变量.几许产品资金账户信息List
        List<JJBkCashAcctDto> listJJBkCashAcct = new ArrayList<JJBkCashAcctDto>();
        /**ADD BY WUJIE ON 20190726 END**/

        // 定义 变量.账套参数Map = 输入.产品标的Dto.账套参数Map Map<String,String>
        Map<String, String> mapBooksetParam = prodInfoDto.getMapBooksetParam();

        // 2.1 输入参数业务CHECK
        checkProdOtherInfoInit(prodInfoDto, resultObject);
        // IF 变量.resultObject.处理结果代码 ne "0000"
        if (ResultCode.SUCCESS.getCode() != resultObject.getResultCode()) {
            return resultObject;
        }
        // 变量.分层代码 = 输入.产品标的Dto.金融产品代码
        layeringId = prodInfoDto.getFinprodId();
        // IF 数据字典：金融产品类型.理财子产品 eq 输入.产品标的Dto.金融产品类型
        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodInfoDto.getFinprodType())) {
            // 定义 变量.分层代码 += new SimpleDateFormat("yyyyMMdd").format(输入.产品标的Dto.起息日)
            layeringId += DateUtil.format(prodInfoDto.getVdate(), DateUtil.DEFAULT_OTHER_FORMAT);
        }
        // 变量.分层代码 += FinalString.LAYERING_SPLIT + 输入.产品标的Dto.金融产品类型
        layeringId += FinalString.LAYERING_SPLIT + prodInfoDto.getFinprodType();

        // 2.2 初始化账套信息
        // IF 数据字典：是否_是 eq 输入.产品标的Dto.是否核算
        if (DictCodeList.是否_是.getValue().equals(prodInfoDto.getIsAccount())) {
            // 定义 变量.标的类金融产品信息Dto FinProduct
            FinProduct finProduct = new FinProduct();
            // 变量.标的类金融产品信息Dto 赋值
            // 输入.产品标的Dto.金融产品代码
            finProduct.setFinprodId(prodInfoDto.getFinprodId());
            // 输入.产品标的Dto.金融产品类型
            finProduct.setFinprodType(prodInfoDto.getFinprodType());
            // 输入.产品标的Dto.金融产品简称
            finProduct.setFinprodAbbr(prodInfoDto.getFinprodAbbr());
            // 输入.产品标的Dto.金融产品名称
            finProduct.setFinprodName(prodInfoDto.getFinprodName());
            // 输入.产品标的Dto.收益类型
            finProduct.setProfitType(prodInfoDto.getProfitType());
            // 输入.产品标的Dto.起息日
            finProduct.setVdate(prodInfoDto.getVdate());
            // 输入.产品标的Dto.到期日
            finProduct.setMdate(prodInfoDto.getMdate());
            // 输入.产品标的Dto.币种
            finProduct.setCcy(prodInfoDto.getCcy());
            // 输入.产品标的Dto.交易日历
            finProduct.setCalendarId(prodInfoDto.getCalendarId());
            // 输入.产品标的Dto.实际到期日
            finProduct.setActmdate(
                    prodInfoDto.getActmdate() == null ? prodInfoDto.getMdate() : prodInfoDto.getActmdate());

            // 定义 变量.标的类金融产品扩展信息表Dto FinProductExt
            ValFinProductExt finProductAdd = new ValFinProductExt();
            // 定义 变量.托管活期利息计提 =
            // 变量.账套参数Map.get("DEPOSIT_ACCOUT")==null?数据字典：是否_否：变量.账套参数Map.get("DEPOSIT_ACCOUT")
            String tgIsCalc = mapBooksetParam.get("DEPOSIT_ACCOUT") == null ? DictCodeList.是否_否.getValue() : mapBooksetParam.get("DEPOSIT_ACCOUT");
            // 变量.标的类金融产品扩展信息表Dto 赋值
            // 变量.托管活期利息计提
            finProductAdd.setIsCalc(tgIsCalc);
            // 输入.产品标的Dto.是否分层核算
            finProductAdd.setIsLayerAccount(prodInfoDto.getIsLayerAccount());
            // 输入.产品标的Dto.投资组合代码
            finProductAdd.setPortfolioId(prodInfoDto.getPortfolioId());
            // 输入.产品标的Dto.是否核算
            finProductAdd.setIsAccount(prodInfoDto.getIsAccount());
            // 定义 变量.其他参数值Dto
            OtherParamsDto otherParamsDto = new OtherParamsDto();
            // 变量.其他参数值Dto 赋值
            // 输入.产品标的Dto.估值表模板
            otherParamsDto.setValuationTemplate(prodInfoDto.getValuationTemplate());
            // 模板账套ID 默认为空
            otherParamsDto.setTmp_booksetId(StringUtils.EMPTY);

            if (DictCodeList.收益类型_净值型.getValue().equals(prodInfoDto.getProfitType())) {
                //新增估值方案=dto.产品估值方案
                Map<String,String> sysParas = new HashMap<>();
                //份额小数位=
                sysParas.put(ValDictCodeList.账套配置表参数项_份额小数位位数.getValue(), null);
                //份额尾数处理
                sysParas.put(ValDictCodeList.账套配置表参数项_份额小数位尾数处理.getValue(), null);
                //单位净值小数位
                sysParas.put(ValDictCodeList.账套配置表参数项_估值小数位位数.getValue(), mapBooksetParam.get("VAL_DEC_DIGITS"));
                //单位净值尾数处理
                sysParas.put(ValDictCodeList.账套配置表参数项_估值小数位处理方式.getValue(), mapBooksetParam.get("VAL_DEC_TYPE"));
                otherParamsDto.setSysParams(sysParas);
            } else if (DictCodeList.收益类型_收益型.getValue().equals(prodInfoDto.getProfitType())) {
                //新增估值方案
                Map<String,String> sysParas = new HashMap<>();
                //利率小数位
                sysParas.put(ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue(), mapBooksetParam.get("YEILD_DEC_DIGITS"));
                //利率处理方式
                sysParas.put(ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue(), mapBooksetParam.get("YEILD_DEC_TYPE"));
                otherParamsDto.setSysParams(sysParas);
            } else if (DictCodeList.收益类型_货币型.getValue().equals(prodInfoDto.getProfitType())) {
                //新增估值方案
                Map<String,String> sysParas = new HashMap<>();
                //份额小数位=
                sysParas.put(ValDictCodeList.账套配置表参数项_份额小数位位数.getValue(), null);
                //份额尾数处理
                sysParas.put(ValDictCodeList.账套配置表参数项_份额小数位尾数处理.getValue(), null);
                //万份收益小数位位数
                sysParas.put(ValDictCodeList.账套配置表参数项_万份收益小数位位数.getValue(), mapBooksetParam.get("VAL_DEC_DIGITS"));
                //万份收益尾数处理
                sysParas.put(ValDictCodeList.账套配置表参数项_万份收益尾数处理.getValue(), mapBooksetParam.get("VAL_DEC_TYPE"));
                //7日年化收益率小数位
                sysParas.put(ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue(), mapBooksetParam.get("YEILD_DEC_DIGITS"));
                //7日年化收益率尾数处理
                sysParas.put(ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue(), mapBooksetParam.get("YEILD_DEC_TYPE"));
                otherParamsDto.setSysParams(sysParas);
            }
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息初始化_分层产品SHEET
            BooksetInitResult booksetInitResult = interfCommonService.getParStrucProduct(finProduct, finProductAdd, otherParamsDto);

            // 变量.入库账套Dto = DTO.账套信息
            bokBookset = booksetInitResult.getValBokBookset();
            // 变量.变量.入库账套日期Dto = DTO.账套日期Dto
            bokBooksetDate = booksetInitResult.getValBokBooksetDate();
            // 变量.入库账套配置List.addAll(DTO.账套配置List)
            listBokBooksetExt.addAll(booksetInitResult.getListValBokBooksetExt());
            // 变量.入库核算主体信息Dto = DTO.核算主体信息Dto
            bokAcctSubjectInfo = booksetInitResult.getAcctSubjectInfo();
            // 变量.入库账务明细代码List.addAll(DTO.账务明细代码List）
            listBokDetailId.add(booksetInitResult.getBokDetailId());
            // 变量.核算分层信息List.add(DTO.核算分层信息DTO)
            listBokAcctLayeringInfo.add(booksetInitResult.getAcctLayeringInfo());
            listBokBooksetPlan.add(booksetInitResult.getBokBooksetPlan());

            /**ADD BY WUJIE ON 20190726 START**/
            //2.2.1 几许产品信息初始化
            jJPortfolio = new JJPortfolioDto();
            //账套
            jJPortfolio.setBookSet_id(bokBookset.getBooksetId());
        	//货币
            jJPortfolio.setStandard_ccy(bokBookset.getStandardCcy());
            //账套模板
            jJPortfolio.setBookset_id_tmpl(bokBookset.getBooksetIdTmpl());
        	 //账套创建日期
            jJPortfolio.setStart_date(bokBookset.getStartDate());
        	 //账套全名
            jJPortfolio.setBookset_fullname(bokBookset.getBooksetFullname());
            //账套名
            jJPortfolio.setBookset_name(bokBookset.getBooksetName());
             //账套节假日配置
            jJPortfolio.setCalendar_id(prodInfoDto.getCalendarId());
            /**ADD BY WUJIE ON 20190726 END**/

        }

        // 2.3 初始化核算分层信息
        // 定义 变量.本次新增分层产品List
        List<LayerPrdInfo> listLayerPrdInfo = Lists.newArrayList();
        // 定义 变量.本次新增账套信息List
        List<BokBookset> listBokBookset = Lists.newArrayList();
        // 定义 变量.已有分层产品Map
        Map<String, LayerPrdInfo> mapLayerPrdInfo = Maps.newHashMap();
        // IF 变量.入库账套Dto IS NOT NULL and 变量.入库账套Dto.账套代码 IS NOT NULL
        if (bokBookset != null && StringUtils.isNotEmpty(bokBookset.getBooksetId())) {
            // 变量.本次新增账套信息List.add(变量.入库账套Dto)
            listBokBookset.add(bokBookset);
        }

        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->已有产品信息查询SHEET
        mapLayerPrdInfo = interfCommonService.getExistPrdInfo();

        // 定义 变量.分层产品Dto
        LayerPrdInfo layerPrdInfo = new LayerPrdInfo();
        // 定义 变量.金融产品代码 = 输入.产品标的Dto.金融产品代码
        String finProductId = prodInfoDto.getFinprodId();
        // IF 输入.产品标的Dto.金融产品类型 eq 数据字典：金融产品类型.理财子产品
        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodInfoDto.getFinprodType())) {
            // 变量.金融产品代码 += Long.valueOf(new SimpleDateFormat("yyyyMMdd").format(输入.产品标的Dto.起息日))
            finProductId += DateUtil.format(prodInfoDto.getVdate(), DateUtil.DEFAULT_OTHER_FORMAT);
        }

        // 变量.分层产品Dto 赋值
        // 金融产品代码 变量.金融产品代码
        layerPrdInfo.setFinprodId(finProductId);;
        // 金融产品类型 输入.产品标的Dto.金融产品类型
        layerPrdInfo.setFinprodType(prodInfoDto.getFinprodType());
        // 金融产品名称 输入.产品标的Dto.金融产品名称
        layerPrdInfo.setFinprodName(prodInfoDto.getFinprodName());
        // 母金融产品代码 输入.产品标的Dto.母金融产品代码
        layerPrdInfo.setpFinprodId(prodInfoDto.getpFinprodId());
        // 收益类型 输入.产品标的Dto.收益类型
        layerPrdInfo.setProfitType(prodInfoDto.getProfitType());
        // 起息日 输入.产品标的Dto.起息日
        layerPrdInfo.setVdate(prodInfoDto.getVdate());
        // 到期日 输入.产品标的Dto.到期日
        layerPrdInfo.setMdate(prodInfoDto.getMdate());
        // 投资组合代码 输入.产品标的Dto.投资组合代码
        layerPrdInfo.setPortfolioId(prodInfoDto.getPortfolioId());
        // 分层代码 变量.分层代码
        layerPrdInfo.setLayeringId(layeringId);
        // 交易日历 输入.产品标的Dto.交易日历
        layerPrdInfo.setCalendarId(prodInfoDto.getCalendarId());

        // 变量.已有分层产品Map.put(变量.分层产品Dto.分层代码,变量.分层产品Dto)
        mapLayerPrdInfo.put(layerPrdInfo.getLayeringId(), layerPrdInfo);
        // IF 输入.产品标的Dto.金融产品类型 ne 数据字典：金融产品类型.投资组合
        if (!DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodInfoDto.getFinprodType())) {
            // 变量.本次新增分层产品List.add(变量.分层产品Dto)
            listLayerPrdInfo.add(layerPrdInfo);
        }

        // IF 变量.本次新增分层产品List IS NOT NULL AND 变量.本次新增分层产品List.size()>0
        if (CollectionUtils.isNotEmpty(listLayerPrdInfo)) {
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->核算分层信息初始化_分层产品SHEET
            Map<String, Object> map = interfCommonService.getStrucSubProInit(listLayerPrdInfo, mapLayerPrdInfo, listBokBookset,
                    listBokAcctLayeringInfo);
            // IF DTO.get("ACCT_LAYERING_INFO") is not null
            if (map.get("ACCT_LAYERING_INFO") != null) {
                // 变量.入库核算分层信息List.addAll(DTO.get("ACCT_LAYERING_INFO"))
                listEnterBokAcctLayeringInfo.addAll((List<BokAcctLayeringInfo>)map.get("ACCT_LAYERING_INFO"));
            }
            // IF DTO.get("BOK_DETAIL_ID") is not null
            if (map.get("BOK_DETAIL_ID") != null) {
                // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID"))
                listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
            }
        }
        else {
            // IF 变量.核算分层信息List IS NOT NULL and 变量.核算分层信息List.size()>0
            if (CollectionUtils.isNotEmpty(listBokAcctLayeringInfo)) {
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->核算分层信息部分字段值初始化SHEET
                listEnterBokAcctLayeringInfo.addAll(interfCommonService.initBokAcctLayeringInfo(listBokAcctLayeringInfo));
            }
        }

        // 2.4 增值税信息初始化
        // IF 数据字典：是否.是 eq 输入.产品标的Dto.是否计税 OR 变量.入库核算分层信息List is not null
        if (DictCodeList.是否_是.getValue().equals(prodInfoDto.getIsTax()) && CollectionUtils.isNotEmpty(listEnterBokAcctLayeringInfo)) {
            // 定义 变量.计税产品List
            List<BokAcctLayeringInfo> listTaxProduct = Lists.newArrayList();
            // LOOP 变量.入库核算分层信息List L
            for (BokAcctLayeringInfo item : listEnterBokAcctLayeringInfo) {
                // IF L.分层代码 eq 变量.分层代码
                if (StringUtils.equals(item.getLayeringId(), layeringId)) {
                    // 变量.计税产品List.add(L)
                    listTaxProduct.add(item);
                }
            }
            // IF 变量.计税产品List IS NOT NULL AND 变量.计税产品List.size()>0
            if (CollectionUtils.isNotEmpty(listTaxProduct)) {
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->增值税初始化_分层产品SHEET
                Map<String, Object> map = interfCommonService.initValueTax(listTaxProduct);
                // IF DTO.get("FIN_CASH_INFO") is not null
                if (map.get("FIN_CASH_INFO") != null) {
                    // 变量.入库现金流信息List.addAll(DTO.get("FIN_CASH_INFO"));
                    listFinCashInfo.addAll((List<FinCashInfo>)map.get("FIN_CASH_INFO"));
                }
                // IF DTO.get("BOK_DETAIL_ID_LIST") is not null
                if (map.get("BOK_DETAIL_ID_LIST") != null) {
                    // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID_LIST"));
                    listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST"));
                }
                // IF DTO.get("BOK_BOOKSET_PLAN") is not null
                if (map.get("BOK_BOOKSET_PLAN") != null) {
                    // 变量.入库估值方案List.addAll(DTO.get("BOK_BOOKSET_PLAN"));
                    listBokBooksetPlan.addAll((List<BokBooksetPlan>)map.get("BOK_BOOKSET_PLAN"));
                }

            }
        }

        // 2.5 账套清算款账户信息初始化
        // IF 金融产品账户信息List IS NOT NULL AND 金融产品账户信息List.size()>0
        if (CollectionUtils.isNotEmpty(productAcctList)) {
            // 定义 变量.本次新增产品对应账套List
            List<ValBooksetInfoDto> listValBooksetInfoDto = Lists.newArrayList();
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
            List<ValBooksetInfoDto> list = interfCommonService.getBokBooksetByProdId_Layering(layeringId);

            // IF DTO IS NOT NULL AND DTO.size()>0
            if (CollectionUtils.isNotEmpty(list)) {
                // IF 变量.入库账套Dto IS NULL OR 变量.入库账套Dto.账套代码 IS NULL
                if (bokBookset == null || StringUtils.isEmpty(bokBookset.getBooksetId())) {
                    // 变量.本次新增产品对应账套List.addAll(DTO)
                    listValBooksetInfoDto.addAll(list);
                }
                else {
                    // LOOP DTO L
                    for (ValBooksetInfoDto item : list) {
                        // IF DTO.账套代码 ne 变量.入库账套DTO.账套代码
                        if (!StringUtils.equals(item.getBooksetId(), bokBookset.getBooksetId())) {
                            // 变量.本次新增产品对应账套List.add(L)
                            listValBooksetInfoDto.add(item);
                        }
                    }
                    // 定义 变量.产品对应账套Dto
                    ValBooksetInfoDto valBooksetInfoDto = new ValBooksetInfoDto();
                    // 变量.产品对应账套Dto.账套代码 = 变量.入库账套DTO.账套代码
                    valBooksetInfoDto.setBooksetId(bokBookset.getBooksetId());
                    // 变量.本次新增产品对应账套List.add（变量.产品对应账套Dto）
                    listValBooksetInfoDto.add(valBooksetInfoDto);
                }
            }
            else {
                // 定义 变量.产品对应账套Dto ValBooksetInfoDto
                ValBooksetInfoDto dto = new ValBooksetInfoDto();
                // 变量.产品对应账套Dto.账套代码 = 变量.入库账套DTO.账套代码
                if(bokBookset != null){
                    dto.setBooksetId(bokBookset.getBooksetId());
                }
                // 变量.本次新增产品对应账套List.add（变量.产品对应账套Dto）
                listValBooksetInfoDto.add(dto);
            }

            // IF 变量.本次新增产品对应账套List IS NOT NULL
            if (CollectionUtils.isNotEmpty(listValBooksetInfoDto)) {
                // LOOP 输入.金融产品账户信息List L
                for (StlFinProductAcct item : productAcctList) {
                    // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套清算账户初始化SHEET
                    List<BokBooksetCapacctInfo> listInfo = interfCommonService.booksetCapacctInfoInit(item, listValBooksetInfoDto);
                    // IF DTO IS NOT NULL AND DTO.size()>0
                    if (CollectionUtils.isNotEmpty(listInfo)) {
                        // 变量.入库账套清算账户List.addAll(DTO)
                        listBokBooksetCapacctInfo.addAll(listInfo);
                    }
                    // IF 数据字段：资金账户类型.托管户 eq L.账户类型
                    if (DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(item.getAcctType())) {
                        // 定义 变量.清算款账户dto StlFinProductAcct
                        StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
                        // 变量.清算款账户dto 赋值
                        // 金融产品代码 L.金融产品代码
                        stlFinProductAcct.setFinprodId(item.getFinprodId());
                        // 金融产品类型 L.金融产品类型
                        stlFinProductAcct.setFinprodType(item.getFinprodType());
                        // 账号 InterfCommonConstant.清算款账户号
                        stlFinProductAcct.setAcctNo(InterfCommonConstant.清算款账户号+item.getCcy());
                        // 币种 L.币种
                        stlFinProductAcct.setCcy(item.getCcy());
                        // 账户类型 数据字典：资金账户类型.待清算款
                        stlFinProductAcct.setAcctType(DictCodeListExt1.资金账户类型_待清算款.getValue());
                        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套清算账户初始化SHEET
                        List<BokBooksetCapacctInfo> listDto = interfCommonService.booksetCapacctInfoInit(stlFinProductAcct, listValBooksetInfoDto);
                        // IF DTO IS NOT NULL AND DTO.size()>0
                        if (CollectionUtils.isNotEmpty(listDto)) {
                            // 变量.入库账套清算账户List.addAll(DTO)
                            listBokBooksetCapacctInfo.addAll(listDto);
                        }
                    }

                    /**ADD BY WUJIE ON 20190726 START**/
                    for (ValBooksetInfoDto itemBok : listValBooksetInfoDto) {
	                    JJBkCashAcctDto jJBkCashAcct = new JJBkCashAcctDto();
	                    //银行账户代码
	                    jJBkCashAcct.setAcct_no(item.getAcctNo());
	                    //账套代码
	                    jJBkCashAcct.setBookset_id(itemBok.getBooksetId());
	                    //币种
	                    jJBkCashAcct.setCcy(item.getCcy());
	                    //账户类型  jixu接口需要  add by qmh 20190812 start
	                    jJBkCashAcct.setAcc_type(item.getAcctType());
	                	StlCapacctInfo StlCapacctInfo = valFinProductAcctBatchMapper.getStlCapacctInfo(item.getAcctNo(),item.getCcy());
	                	jJBkCashAcct.setAcct_name(StlCapacctInfo.getAcctName());

	                    //  jixu接口需要  add by qmh 20190812 end
	                    listJJBkCashAcct.add(jJBkCashAcct);
                    }
                    /**ADD BY WUJIE ON 20190726 END**/
                    /**modify by wujie on 20190809 remark:取消判断条件
                    // IF 数据字典：是否.是 eq 输入.产品标的Dto.账套参数Map.get("DEPOSIT_ACCOUT")
                    if (DictCodeList.是否_是.getValue().equals(prodInfoDto.getMapBooksetParam().get("DEPOSIT_ACCOUT"))) {
                       **/
                        // LOOP 变量.本次新增产品对应账套List BOK
                        for (ValBooksetInfoDto itemBok : listValBooksetInfoDto) {
                            // 定义 变量.账务明细代码Dto BokDetailId
                            BokDetailId bokDetailId = new BokDetailId();
                            // 变量.账务明细代码Dto.账套代码 = BOK.账套代码
                            bokDetailId.setBooksetId(itemBok.getBooksetId());
                            // 变量.账务明细代码Dto.业务明细代码 = L.账号
                            bokDetailId.setBusiId(item.getAcctNo());
                            // 变量.账务明细代码Dto.业务明细代码类型 = L.账户类型
                            bokDetailId.setBusiDetailType(item.getAcctType());
                            //变量.账务明细代码Dto.币种 = L.币种
                            bokDetailId.setCcy(item.getCcy());
                            // 定义 变量.账务明细代码 String
                            // 变量.账务明细代码 = interfCommonService.genMd5BokDetailId(变量.账务明细代码Dto )
                            String strBokDetailId = interfCommonService.genMd5BokDetailId(bokDetailId);
                            // 变量.账务明细代码Dto.账务明细代码 = 变量.账务明细代码
                            bokDetailId.setBokDetailId(strBokDetailId);
                            // 变量.入库账务明细代码List.add(变量.账务明细代码Dto)
                            listBokDetailId.add(bokDetailId);
                        }
                        /**modify by wujie on 20190809 remark:取消判断条件
                    }
                    **/

                }
            }
        }

        // 2.6 根据前台传的账套参数信息进行初始化
        // IF 变量.入库账套配置List IS NOT NULL AND 变量.入库账套配置List.SIZE()>0
        if (CollectionUtils.isNotEmpty(listBokBooksetExt)) {
            // LOOP 变量.入库账套配置List L
            for (BokBooksetExt item : listBokBooksetExt) {
                // IF 变量.账套参数Map.get(L.参数项) != null
                if (mapBooksetParam.get(item.getParamCode()) != null) {
                    // L.参数值 = 变量.账套参数Map.get(L.参数项)
                    item.setParamValue(mapBooksetParam.get(item.getParamCode()));
                }
            }

        }

        // 2.7 初始化完成的账套信息入数据库
        // 变量.入库账套Dto 账套信息表 BOK_BOOKSET 以账套代码进行删除，然后再入库
        if (bokBookset != null) {
            bokBooksetMapper.deleteByPrimaryKey(bokBookset.getBooksetId());
            bokBooksetMapper.insertSelective(bokBookset);
        }
        // 变量.入库账套日期Dto 账套日期表 BOK_BOOKSET_DATE 以账套代码进行删除，然后再入库
        if (bokBooksetDate != null) {
            bokBooksetDateMapper.deleteByPrimaryKey(bokBooksetDate.getBooksetId());
            bokBooksetDateMapper.insertSelective(bokBooksetDate);
        }
        // 变量.入库账套配置List 账套配置表 BOK_BOOKSET_EXT 以账套代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listBokBooksetExt)) {
            valBokBooksetExtBatchMapper.deleteBatchById(listBokBooksetExt);
            valBokBooksetExtBatchMapper.insertBatch(listBokBooksetExt);
        }
        // 变量.入库核算主体信息Dto 核算主体信息表 BOK_ACCT_SUBJECT_INFO 以核算主体代码、核算主体类型进行删除，然后再入库
        if (bokAcctSubjectInfo != null) {
            BokAcctSubjectInfoKey key = new BokAcctSubjectInfoKey();
            key.setAcctSubjectId(bokAcctSubjectInfo.getAcctSubjectId());
            key.setAcctSubjectType(bokAcctSubjectInfo.getAcctSubjectType());
            bokAcctSubjectInfoMapper.deleteByPrimaryKey(key);
            bokAcctSubjectInfoMapper.insert(bokAcctSubjectInfo);
        }
        // 变量.入库核算分层信息List 核算分层信息表 BOK_ACCT_LAYERING_INFO 以账套代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listEnterBokAcctLayeringInfo)) {
            valAcctLayeringInfoBatchMapper.deleteBatchBycondition(listEnterBokAcctLayeringInfo);
            valAcctLayeringInfoBatchMapper.insert(listEnterBokAcctLayeringInfo);
        }
        // 变量.入库账务明细代码List 账务明细代码表 BOK_DETAIL_ID 以账套代码、账务明细代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listBokDetailId)) {
            valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
            valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
        }
        // 变量.入库现金流信息List 现金流信息表 FIN_CASH_INFO 以现金流代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listFinCashInfo)) {
            valCashInfoBatchMapper.deleteBatch(listFinCashInfo);
            valCashInfoBatchMapper.insertBatch(listFinCashInfo);
        }
        // 变量.入库估值方案List 账套估值方案 BOK_BOOKSET_PLAN 以账套代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listBokBooksetPlan)) {
            bokBooksetPlanBatchMapper.deleteBatch(listBokBooksetPlan);
            bokBooksetPlanBatchMapper.insertBatch(listBokBooksetPlan);
        }
        // 变量.入库账套清算账户List 账套清算账户表 BOK_BOOKSET_CAPACCT_INFO 以账套代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listBokBooksetCapacctInfo)) {
            valBooksetCapacctInfoBatchMapper.deleteBatch(listBokBooksetCapacctInfo);
            valBooksetCapacctInfoBatchMapper.insertBatch(listBokBooksetCapacctInfo);
        }
        /**ADD BY WUJIE ON 20190726 START**/
        if(jJPortfolio != null){
        	List<JJPortfolioDto> list = new ArrayList<JJPortfolioDto>();
        	list.add(jJPortfolio);
        	joyin2JixuInterface.createPortfolios(list);
        }
        if (CollectionUtils.isNotEmpty(listJJBkCashAcct)) {
        	joyin2JixuInterface.updateBkCashAccts(listJJBkCashAcct);
        }
        /**ADD BY WUJIE ON 20190726 END**/
        return resultObject;
    }

    /**
     * 交易入账
     *
     * @param prodid 产品代码
     * @param prodFinprodType 产品类型(F16、F18、F22)：见数据字典：金融产品类型
     * @param branch 子产品起息日(产品类型为F22时为必输项)
     * @param trdProductDeal 交易信息Dto
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject bokTradeBuild(String prodid, String prodFinprodType, Long branch, TrdProductDeal trdProductDeal) {
        // 定义 变量.resultObject
        // 变量.resultObject.处理结果代码 = "0000"
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.1 输入参数业务CHECK
        checkBokTradeBuild(prodid, prodFinprodType, branch, trdProductDeal, resultObject);
        // IF 变量.resultObject.处理结果代码 ne "0000"
//        if (!ResultCode.SUCCESS.getCode().equals(resultObject.getResultCode())) {
        if (ResultCode.SUCCESS.getCode() != resultObject.getResultCode()) {
            return resultObject;
        }

        // 2.2 交易处理
        // IF 数据字典：交易状态.新增 eq 输入.交易信息Dto.交易状态
        if (DictCodeListExt1.交易状态_初始.getValue().equals(trdProductDeal.getTradeStatus())) {
            /**add by wujie on 20190215 start**/
            if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)){
                FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(prodid);
                if(finProduct != null && finProduct.getFinprodId() != null){
                    prodid = finProduct.getFinprodId();
                    prodFinprodType = finProduct.getFinprodType();
                }
            }
            /**add by wujie on 20190215 end**/

            // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_新增SHEET
            resultObject = this.bokTradeBuildForAdd(prodid, prodFinprodType, branch, trdProductDeal);
        }
        else if (DictCodeListExt1.交易状态_撤销.getValue().equals(trdProductDeal.getTradeStatus())) {
            // IF 数据字典：交易状态.删除 eq 输入.交易信息Dto.交易状态
            // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_删除SHEET
            resultObject = this.bokTradeBuildForDel(trdProductDeal);
        }
        return resultObject;
    }

    /**
     * 交易入账_新增
     *
     * @param prodid 产品代码
     * @param prodFinprodType 产品类型(F16、F18、F22)：见数据字典：金融产品类型
     * @param branch 子产品起息日(产品类型为F22时为必输项)
     * @param trdProductDeal 交易信息Dto
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    @SuppressWarnings("unchecked")
    private ResultObject bokTradeBuildForAdd(String prodid, String prodFinprodType, Long branch, TrdProductDeal trdProductDeal) {
        // 定义 变量.resultObject
        ResultObject resultObject = null;
        // 定义 变量.入库结算指令List
        List<StlOrders> listStlOrders = Lists.newArrayList();
        // 定义 变量.入库结算指令扩展List
        List<StlOrdersExt> listStlOrdersExt = Lists.newArrayList();
        // 定义 变量.入库入账信息List
        List<BokDealAccounting> listBokDealAcct = Lists.newArrayList();
        // 定义 变量.入库入账明细信息List
        List<BokDealAccountingDetail> listBokDealAcctDetail = Lists.newArrayList();
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.账套信息List
        List<ValBooksetInfoDto> listValBooksetInfoDto = Lists.newArrayList();

        // 2.1 交易入账
        // IF (输入.交易信息Dto.业务类型 eq 数据字典：业务类型.货币基金 or 输入.交易信息Dto.业务类型 eq 数据字典：业务类型.非货币基金)
        // and
        // （输入.交易信息Dto.交易方向 eq 数据字典：交易方向.申购 or 输入.交易信息Dto.交易方向 eq 数据字典：交易方向.赎回到账)
        if ((DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue().equals(trdProductDeal.getBusiType())
                || DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue().equals(trdProductDeal.getBusiType()))
                && (DictCodeList.交易方向_申购.getValue().equals(trdProductDeal.getPs()) || DictCodeList.交易方向_赎回到账.getValue().equals(trdProductDeal.getPs()))) {
            //核算主体代码
            String acctSubjectId = null;
            if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){
                acctSubjectId = prodid;
            }else{
                acctSubjectId = prodid+FinalString.LAYERING_SPLIT+trdProductDeal.getChlId();
            }



            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
            List<ValBooksetInfoDto> list = interfCommonService.getBokBooksetByProdId_Layering(acctSubjectId);
            // IF DTO IS NULL OR DTO.SIZE()==0
            if (CollectionUtils.isEmpty(list)) {
                // 变量.resultObject.处理结果代码 = "1111"
                // 变量.resultObject.处理结果描述 = "产品代码["+输入.产品代码+"]无对应核算主体"
                resultObject = new ResultObject();
                resultObject.setResultCode(ResultCode.WARNING.getCode());
                if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){
                    resultObject.setResultMsg("产品代码[" + prodid + "]无对应核算主体");

                }else{
                    resultObject.setResultMsg("产品代码[" + prodid +FinalString.LAYERING_SPLIT+trdProductDeal.getChlId()+ "]无对应核算主体");
                }


                // return 变量.resultObject
                return resultObject;
            } else {
                // 变量.账套信息List.addAll(DTO)
                listValBooksetInfoDto.addAll(list);
            }
        }
        else {
            //IF 数据字典：业务类型.活期存款交易 eq输入.交易信息Dto.业务类型
            if(DictCodeList.业务类型_活期存款交易.getValue().equals(trdProductDeal.getBusiType())){
                //核算主体代码
                String acctSubjectId = null;
                if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){
                    acctSubjectId = prodid;
                }else{
                    acctSubjectId = prodid+FinalString.LAYERING_SPLIT+trdProductDeal.getChlId();
                }


                //定义 变量.账套代码
                String booksetId=null;
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
                List<ValBooksetInfoDto> dto=interfCommonService.getBokBooksetByProdId_Layering(acctSubjectId);
                if(dto==null||dto.size()==0){
                    resultObject = new ResultObject();
                    resultObject.setResultCode(ResultCode.WARNING.getCode());
                    if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){
                        resultObject.setResultMsg("产品代码[" + prodid + "]无对应核算主体");
                    }else{
                        resultObject.setResultMsg("产品代码[" + prodid +FinalString.LAYERING_SPLIT+trdProductDeal.getChlId()+ "]无对应核算主体");
                    }


//        			 resultObject.setResultMsg("产品代码[" + prodid + "]无对应核算主体");
                    return resultObject;

                }else{
                    booksetId=dto.get(0).getBooksetId();
                    BokBooksetCapacctInfo bokBooksetCapacctInfo=interfCommonService.queryBokBooksetCapacctInfo(booksetId, trdProductDeal.getFinprodId(), trdProductDeal.getCcy(),null);
                    if(bokBooksetCapacctInfo==null){
                        resultObject = new ResultObject();
                        resultObject.setResultCode(ResultCode.WARNING.getCode());
                        resultObject.setResultMsg("产品代码[" + prodid + "]无对应核算主体");
                        return resultObject;
                    }else{
                        trdProductDeal.setFinprodType(bokBooksetCapacctInfo.getAcctType());
                    }

                }

            }



            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->交易入账_分层产品SHEET
            InterfProductDealBookCommonDto dto = interfCommonService.dealAccounting_Layering_Branch(trdProductDeal, prodid, branch, prodFinprodType,null, null);

            // IF DTO.bokDealAccountingList IS NOT NULL AND DTO.bokDealAccountingList.size()>0
            if (CollectionUtils.isNotEmpty(dto.getBokDealAccountingList())) {
                // 变量.入库入账信息List.addAll(DTO.bokDealAccountingList)
                listBokDealAcct.addAll(dto.getBokDealAccountingList());
            }
            // IF DTO.bokDealAccountingDetailList IS NOT NULL AND
            // DTO.bokDealAccountingDetailList.size()>0
            if (CollectionUtils.isNotEmpty(dto.getBokDealAccountingDetailList())) {
                // 变量.入库入账明细信息List.addAll(DTO.bokDealAccountingDetailList)
                listBokDealAcctDetail.addAll(dto.getBokDealAccountingDetailList());
            }

            // IF DTO.bokDetailIdList IS NOT NULL AND DTO.bokDetailIdList.size()>0
            if (CollectionUtils.isNotEmpty(dto.getBokDetailIdList())) {
                // 定义 变量.账务明细代码List = DTO.bokDetailIdList
                List<BokDetailId> list = dto.getBokDetailIdList();
                // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->四级科目尾号设置SHEET
                //产品费用交易:四级科目尾号获取特殊处理
                String finprodId = trdProductDeal.getFinprodId();
                String finprodType = trdProductDeal.getFinprodType();
                /**modify by wujie on 20190220 start
                 if ((DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())
                 && DictCodeList.交易方向_产品付费.getValue().equals(trdProductDeal.getPs()))
                 || (DictCodeList.业务类型_增值税交易.getValue().equals(trdProductDeal.getBusiType())
                 && (DictCodeList.交易方向_税费缴税.getValue().equals(trdProductDeal.getPs())
                 || DictCodeList.交易方向_税费退税.getValue().equals(
                 trdProductDeal.getPs())))) {
                 **/
                if (
//                		DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())
//                	|| DictCodeList.业务类型_增值税交易.getValue().equals(trdProductDeal.getBusiType())
//                	||
                        DictCodeList.业务类型_活期存款交易.getValue().equals(trdProductDeal.getBusiType())
                ){
                    /**modify by wujie on 20190220 end**/
                    finprodId = prodid;
                    finprodType = prodFinprodType;
                }else if( DictCodeList.业务类型_增值税交易.getValue().equals(trdProductDeal.getBusiType())){
                    if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){
                        finprodId = prodid;
                        finprodType = prodFinprodType;
                    }else{
//                		finprodId = prodid+FinalString.LAYERING_SPLIT+trdProductDeal.getChlId();
                        finprodId = trdProductDeal.getChlId();
                        finprodType = DictCodeListExt1.金融产品类型_通道.getValue();
                    }

                }else if(DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())){
                    finprodId = prodid;
                    finprodType = prodFinprodType;
                    if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){

                    }else{
//                		finprodId=prodid+FinalString.LAYERING_SPLIT+trdProductDeal.getChlId();
//                		if(finprodId.equals(trdProductDeal.getFinprodId())){
//                			finprodId =trdProductDeal.getFinprodId();
//                			finprodType =DictCodeListExt1.金融产品类型_通道.getValue();
//                		}
                        finprodId = trdProductDeal.getChlId();
                        finprodType =DictCodeListExt1.金融产品类型_通道.getValue();
                    }
                }

                BokDetailId bokDetailId = interfCommonService.settingDetailSubjectNoTaiL(finprodId, finprodType, branch,
                        trdProductDeal.getBusiType(),dto.getBokDetailIdList().get(0).getBooksetId());

                // 定义 变量.四级科目尾号 = DTO.四级科目尾号
                String detailSubjectNoTaiL = bokDetailId.getDetailSubjectNoTail();
                // 定义 变量.四级科目尾号名称 = DTO.四级科目尾号名称
                String detailSubjectNameTaiL = bokDetailId.getDetailSubnameTail();

                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账务明细代码_四级科目设置SHEET
                interfCommonService.setDetailSubjectNoTail(list, detailSubjectNoTaiL, detailSubjectNameTaiL);

                // 变量.入库账务明细代码List.addAll(变量.账务明细代码List)
                listBokDetailId.addAll(list);
            }

            // IF DTO.valBooksetInfoDtoList IS NOT NULL AND DTO.valBooksetInfoDtoList.size()>0
            if (CollectionUtils.isNotEmpty(dto.getValBooksetInfoDtoList())) {
                // IF 数据字典：业务类型.净值型TA交易 eq 输入.交易信息Dto.业务类型
                // OR 数据字典：业务类型.货币型TA交易 eq 输入.交易信息Dto.业务类型
                // OR 数据字典：业务类型.预期收益型TA交易 eq 输入.交易信息Dto.业务类型
                // OR ( 数据字典:业务类型.产品费用交易 eq 输入.交易信息Dto.业务类型 and 数据字典：交易方向.产品付费 eq 输入.交易信息Dto.交易方向)
                if (DictCodeList.业务类型_净值型TA交易.getValue().equals(trdProductDeal.getBusiType())
                        || DictCodeList.业务类型_货币型TA交易.getValue().equals(trdProductDeal.getBusiType())
                        || DictCodeList.业务类型_预期收益型TA交易.getValue().equals(trdProductDeal.getBusiType())
                        || (DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())
                        && DictCodeList.交易方向_产品付费.getValue().equals(trdProductDeal.getPs()))) {
                    // 变量.账套信息List.addAll(DTO.valBooksetInfoDtoList)
                    listValBooksetInfoDto.addAll(dto.getValBooksetInfoDtoList());
                }
                else {
                    // LOOP DTO.valBooksetInfoDtoList L
                    for (ValBooksetInfoDto item : dto.getValBooksetInfoDtoList()) {
                        /** modify by dongjun on 20190419 start**/
                        String acctSubjectType = prodFinprodType;

                        if(StringUtil.isNotEmpty(trdProductDeal.getChlId())){
                            acctSubjectType = DictCodeListExt1.金融产品类型_通道.getValue();
                        }

                        if(item.getAcctSubjectType().equals(acctSubjectType)){
                            // 变量.账套信息List.add(L)
                            listValBooksetInfoDto.add(item);
                        }
                        /** modify by dongjun on 20190419 end**/
                    }
                }
            }
        }

        // 2.2 结算指令入账
        /**modify by wujie on 20190516 start
         // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->根据交易查询结算指令类型SHEET
         TrdProductDeal fTrdProductDeal = new TrdProductDeal();
         //业务类型
         fTrdProductDeal.setBusiType(trdProductDeal.getBusiType());
         //交易方向
         fTrdProductDeal.setPs(trdProductDeal.getPs());
         //投资目的
         fTrdProductDeal.setInvAim(trdProductDeal.getInvAim());
         //金融产品类型
         fTrdProductDeal.setFinprodType(trdProductDeal.getFinprodType());
         if(DictCodeList.业务类型_产品费用交易.getValue().equals(trdProductDeal.getBusiType())
         ){
         fTrdProductDeal.setFinprodType(prodFinprodType);
         if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){

         }else{
         //核算主体代码
         String acctSubjectId = null;
         acctSubjectId = prodid+FinalString.LAYERING_SPLIT+trdProductDeal.getChlId();
         if(acctSubjectId.equals(trdProductDeal.getFinprodId())){
         fTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
         }
         }
         }
         //活期存款交易
         else if(DictCodeList.业务类型_活期存款交易.getValue().equals(trdProductDeal.getBusiType())){
         fTrdProductDeal.setFinprodType(prodFinprodType);
         if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){

         }else{
         //核算主体代码
         String acctSubjectId = null;
         acctSubjectId = prodid+FinalString.LAYERING_SPLIT+trdProductDeal.getChlId();
         if(acctSubjectId.equals(trdProductDeal.getFinprodId())){
         fTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
         }
         }
         }
         //增值税交易
         else if(DictCodeList.业务类型_增值税交易.getValue().equals(trdProductDeal.getBusiType())){
         fTrdProductDeal.setFinprodType(prodFinprodType);
         if(trdProductDeal.getChlId()==null||"".equals(trdProductDeal.getChlId())){

         }else{
         fTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
         }
         }
         List<StlOrdersMapping> listStlOrdersMapping = this.findOrderTypeByTrade(fTrdProductDeal);
         // IF 变量.交易指令映射关系List IS NOT NULL AND 变量.交易指令映射关系List.SIZE()>0
         if (CollectionUtils.isNotEmpty(listStlOrdersMapping)) {
         // 定义 变量.交易结算指令Dto ProductDealDto
         ProductDealDto productDealDto = new ProductDealDto();
         // 变量.交易结算指令Dto 赋值
         // 交易编号 输入.交易信息Dto.交易编号
         productDealDto.setTradeId(trdProductDeal.getTradeId());
         // 交易日 输入.交易信息Dto.交易日
         productDealDto.setTradeDate(trdProductDeal.getTradeDate());
         // 交割日 输入.交易信息Dto.交割日
         productDealDto.setEffDate(trdProductDeal.getSettleDate());
         // 交易方向 输入.交易信息Dto.交易方向
         productDealDto.setPs(trdProductDeal.getPs());
         // 交易金额 输入.交易信息Dto.交易金额
         productDealDto.setTardeAmt(trdProductDeal.getTradeAmt());
         // 交易状态 输入.交易信息Dto.交易状态
         productDealDto.setTradeStatus(trdProductDeal.getTradeStatus());
         // 币种 输入.交易信息Dto.币种
         productDealDto.setCcy(trdProductDeal.getCcy());

         // 金融产品代码 输入.交易信息Dto.金融产品代码
         // productDealDto.setFinprodId(trdProductDeal.getFinprodId());
         // 金融产品代码 = 输入.产品代码
         productDealDto.setFinprodId(prodid);

         // 业务类型 输入.交易信息Dto.业务类型
         productDealDto.setBusiType(trdProductDeal.getBusiType());
         // 原交易编号 输入.交易信息Dto.原交易编号
         productDealDto.setpTradeId(trdProductDeal.getpTradeId());
         // 交易场所 输入.交易信息Dto.交易场所
         productDealDto.setTradeMarket(trdProductDeal.getTradeMarket());
         // 交易市场 输入.交易信息Dto.交易市场
         productDealDto.setTradeMarketMode(trdProductDeal.getTradeMarketMode());
         // 金融产品类型 输入.产品类型
         productDealDto.setFinprodType(prodFinprodType);
         // 理财产品代码 输入.产品代码
         productDealDto.setProdId(prodid);
         // 账套集合 变量.账套信息List
         productDealDto.setValBooksetInfoDtoList(listValBooksetInfoDto);

         if(DictCodeList.业务类型_活期存款交易.getValue().equals(trdProductDeal.getBusiType())){
         Map<String,Object> map = dealCommonService.settlementOrder_current(productDealDto,
         listStlOrdersMapping, trdProductDeal.getFinprodId(), trdProductDeal.getFinprodType());
         if (map.get("ORDERS_LIST") != null) {
         listStlOrders.addAll((List<StlOrders>)map.get("ORDERS_LIST"));
         }
         if (map.get("ORDERS_EXT_LIST") != null) {
         listStlOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT_LIST"));
         }
         }
         else{
         // LOOP 变量.交易指令映射关系List L
         for (StlOrdersMapping item : listStlOrdersMapping) {
         // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->结算指令生成_分层产品SHEET
         Map<String, Object> map = interfCommonService.settlementInstructionGeneration_Layering(productDealDto, item.getOrderType(),
         item.getIsBook());

         // IF DTO.get("ACCOUNTING_LIST") IS NOT NULL
         if (map.get("ACCOUNTING_LIST") != null) {
         // 变量.入库入账信息List.addAll(DTO.get("ACCOUNTING_LIST"))
         listBokDealAcct.addAll((List<BokDealAccounting>)map.get("ACCOUNTING_LIST"));
         }
         // IF DTO.get("ACCOUNTING_DETAI_LIST") IS NOT NULL
         if (map.get("ACCOUNTING_DETAI_LIST") != null) {
         // 变量.入库入账明细信息List.addAll(DTO.get("ACCOUNTING_DETAI_LIST"))
         listBokDealAcctDetail.addAll((List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI_LIST"));
         }
         // IF DTO.get("BOK_DETAIL_ID_LIST") IS NOT NULL
         if (map.get("BOK_DETAIL_ID_LIST") != null) {
         // 变量.入库账务明细代码List.addAll(DTO.get("BOK_DETAIL_ID_LIST"))
         listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID_LIST"));
         }
         // IF DTO.get("ORDERS") IS NOT NULL
         if (map.get("ORDERS") != null) {
         // 变量.入库结算指令List.add(DTO.get("ORDERS"))
         listStlOrders.add((StlOrders)map.get("ORDERS"));
         }
         // IF DTO.get("ORDERS_EXT") IS NOT NULL
         if (map.get("ORDERS_EXT") != null) {
         // 变量.入库结算指令扩展List.add(DTO.get("ORDERS_EXT"))
         listStlOrdersExt.add((StlOrdersExt)map.get("ORDERS_EXT"));
         }
         }
         }
         }
         **/
        Map<String, Object> map = interfCommonService.instructionGenerate_Branch(trdProductDeal, prodid,prodFinprodType,branch,null);
        if(map.get("ORDERS")!=null){
            listStlOrders.addAll((List<StlOrders>)map.get("ORDERS"));
        }
        if(map.get("ORDERS_EXT")!=null){
            listStlOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
        }
        if(map.get("ACCOUNTING")!=null){
            listBokDealAcct.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
        }
        if(map.get("ACCOUNTING_DETAI")!=null){
            listBokDealAcctDetail.addAll(( List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
        }
        if(map.get("BOK_DETAIL_ID")!=null){
            listBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
        }
        /**modify by wujie on 20190516 end**/

        // 2.3 初始化完成的账套信息入数据库
        // 变量.入库结算指令List 结算指令表 STL_ORDERS
        if (CollectionUtils.isNotEmpty(listStlOrders)) {
            valOrdersBatchMapper.insertBatch(listStlOrders);
        }
        // 变量.入库结算指令扩展List 结算指令扩展表 STL_ORDERS_EXT
        if (CollectionUtils.isNotEmpty(listStlOrdersExt)) {
            valOrdersExtBatchMapper.insertBatch(listStlOrdersExt);
        }
        // 变量.入库入账信息List 入账信息表 BOK_DEAL_ACCOUNTING
        if (CollectionUtils.isNotEmpty(listBokDealAcct)) {
            valDealAccountingBatchMapper.insertBatch(listBokDealAcct);
        }
        // 变量.入库入账明细信息List 入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        if (CollectionUtils.isNotEmpty(listBokDealAcctDetail)) {
            valDealAccountingDetailBatchMapper.insertBatch(listBokDealAcctDetail);
        }
        // 变量.入库账务明细代码List 账务明细代码表 BOK_DETAIL_ID
        if (CollectionUtils.isNotEmpty(listBokDetailId)) {
            // 先根据账套代码、账务明细代码删除，然后入库
            valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
            valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
        }
        // 变量.resultObject.处理结果代码 = "0000"
        // 变量.resultObject.处理结果描述 = "成功"
        resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        return resultObject;
    }

    /**
     * 交易入账_删除
     *
     * @param trdProductDeal 交易信息Dto
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private ResultObject bokTradeBuildForDel(TrdProductDeal trdProductDeal) {
        // 定义 变量.resultObject
        ResultObject resultObject = null;

        // 2.1 入账账务明细表
        bokTradeBuildMapper.deleteBokAcctDetail(trdProductDeal.getpTradeId());
        // 2.2 入账信息表
        bokTradeBuildMapper.deleteBokAcct(trdProductDeal.getpTradeId());

        // 2.3 完成以上操作，执行成功
        // 变量.resultObject.处理结果代码 = "0000"
        // 变量.resultObject.处理结果描述 = "成功"
        resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        return resultObject;

    }
    /**
     * 根据交易查询结算指令类型
     *
     * @param trdProductDeal 交易信息Dto
     * @return 返回共通ResultObject；具体内容参见ResultObject的API 指令集合
     */
    private List<StlOrdersMapping> findOrderTypeByTrade(TrdProductDeal trdProductDeal) {
        // 1.2 定义变量
        // 定义 变量.resultObject List<交易指令映射关系Dto>
        List<StlOrdersMapping> listStlOrdersMapping = Lists.newArrayList();
        // 定义 变量.查询结果集 List<交易指令映射关系Dto>
        // 查询结果保存至 变量.查询结果集
        List<StlOrdersMapping> listSelectResult = bokTradeBuildMapper.findOrderTypeByTrade(trdProductDeal);

        // IF 变量.查询结果集 IS NOT NULL AND 变量.查询结果集.size()>0
        if (CollectionUtils.isNotEmpty(listSelectResult)) {
            // LOOP 变量.查询结果集 L
            for (StlOrdersMapping item : listSelectResult) {
                // IF L.系统参数Code IS NULL
                if (StringUtils.isEmpty(item.getParaCode())) {
                    // 变量.resultObject.add(L)
                    listStlOrdersMapping.add(item);
                }
                else {
                    // 定义 变量.默认值 = CacheUtil.getSysParam(L.系统参数Code)
                    // String defaultValue = CacheUtil.getSysParam(item.getParaCode());
                    String defaultValue = valSysParaService.findParamValue(item.getParaCode());
                    // IF L.系统参数Value eq 变量.默认值
                    if (StringUtils.equals(item.getParamValue(), defaultValue)) {
                        // 变量.resultObject.add(L)
                        listStlOrdersMapping.add(item);
                    }
                }
            }
        }

        // 3.返回 变量.resultObject
        return listStlOrdersMapping;
    }

    /**
     * 同业借款基本信息初始化
     *
     * @param finTradeProduct
     * @param dataActionSign
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
	public ResultObject interBorrowBaseInfoInit(FinTradeProduct finTradeProduct, String dataActionSign) {
        // 变量.resultObject.处理结果代码 = "0000"
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.1 输入参数业务CHECK
        checkPaForInterBorrowBaseInfoInit(finTradeProduct, resultObject);
        // IF 变量.resultObject.处理结果代码 ne "0000"
        if (ResultCode.SUCCESS.getCode() != resultObject.getResultCode()) {
            return resultObject;
        }

        // 2.2 删除已存在的标的信息
        borrowBaseInfoInitMapper.deleteFinTradeProduct(finTradeProduct.getFinprodId(), finTradeProduct.getFinprodType(), new Long(0));

        // 2.3 新增、更新标的信息
        if (!DictCodeList.动作标志_删除.getValue().equals(dataActionSign)) {

            // 获取期限品种
            if (StringUtils.isEmpty(finTradeProduct.getTermType())) {
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->期限品种映射关系SHEET
                String termTypeNow = interfCommonService.getRepTermType(finTradeProduct.getFinprodType(), finTradeProduct.getTerm().intValue());
                // 塞入期限品种
                finTradeProduct.setTermType(termTypeNow);
            }

            // 其他值为默认传递的，以下未默认值
            finTradeProduct.setBranch(new Long(0));// 分支序号默认为0
            finTradeProduct.setBranchType(null);// 分支类型默认为空
            finTradeProduct.setIntRateId(null); // 利率编号（浮动利率基准编号）为空
            // 逾期交易编号为空---注：该字段估值一体化中已被删除

            // 数据入库(变量.入库同业借款标的信息Dto入库)
            finTradeProductMapper.insert(finTradeProduct);
        }

        return resultObject;
    }

    /**
     * 结算指令交易入账
     *
     * @param prodid 产品代码
     * @param prodFinprodType 产品类型(F16、F18、F22)：见数据字典：金融产品类型
     * @param prodVdate 子产品起息日(产品类型为F22时为必输项)
     * @param stlOrders 结算指令交易信息Dto(StlOrders)
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject stlOrdersBokTradeBuild(String prodid, String prodFinprodType, Date prodVdate, StlOrders stlOrders) {
        // 定义 变量.resultObject
        // 变量.resultObject.处理结果代码 = "0000"
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 定义 变量.入库入账信息List
        List<BokDealAccounting> listBokDealAcct = Lists.newArrayList();
        // 定义 变量.入库入账明细信息List
        List<BokDealAccountingDetail> listBokDealAcctDetail = Lists.newArrayList();
        // 定义 变量.入库账务明细代码List
        List<BokDetailId> listBokDetailId = Lists.newArrayList();
        // 定义 变量.账套信息List List<ValBooksetInfoDto>
        List<ValBooksetInfoDto> listValBooksetInfoDto = Lists.newArrayList();

        // 2.1 输入参数业务CHECK
        checkStlOrdersBokTradeBuild(prodid, prodFinprodType, prodVdate, stlOrders, resultObject);
        // IF 变量.resultObject.处理结果代码 ne "0000"
        if (ResultCode.SUCCESS.getCode() != resultObject.getResultCode()) {
            return resultObject;
        }
        /***
         * ADD BY WUJIE ON 20190718 START
         * 指令类型    本方账户      对手方账户    收付方向
         * 1001   A07     A01                收入
         * 1002   A07     A01               支出
         */
        if(DictCodeListExt1.指令类型_银证转账_银转证.getValue().equals(stlOrders.getOrderType())
                || DictCodeListExt1.指令类型_银证转账_证转银.getValue().equals(stlOrders.getOrderType())){
            String old_myAcctNo = stlOrders.getMyAcctNo();
            String old_counterAcctNo = stlOrders.getCounterAcctNo();
            String old_ps = stlOrders.getPs();
            stlOrders.setMyAcctNo(old_counterAcctNo);
            stlOrders.setCounterAcctNo(old_myAcctNo);
            if(DictCodeList.收付方向_收入.getValue().equals(old_ps)){
                stlOrders.setPs(DictCodeListExt1.收付方向_支出.getValue());
            }else{
                stlOrders.setPs(DictCodeList.收付方向_收入.getValue());
            }
        }
        /**ADD BY WUJIE ON 20190718 END**/


        // 2.2 交易处理
        // IF 数据字典：交易状态.新增 eq 输入.结算指令交易信息Dto.指令状态
        if (DictCodeListExt1.交易状态_初始.getValue().equals(stlOrders.getOrderStatus())) {

            /**add by wujie on 20190215 start**/
            if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)){
                FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(prodid);
                if(finProduct != null && finProduct.getFinprodId() != null){
                    prodid = finProduct.getFinprodId();
                    prodFinprodType = finProduct.getFinprodType();
                }
            }
            /**add by wujie on 20190215 end**/

            // 2.2.1 查询账套信息

            //定义 变量.核算主体代码
            String acctSubjectId = null;
            if(stlOrders.getChannelId()==null||"".equals(stlOrders.getChannelId())){

                acctSubjectId = prodid;
            }else{
                acctSubjectId = prodid+FinalString.LAYERING_SPLIT+stlOrders.getChannelId();
            }




            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(根据产品代码) _分层产品SHEET
            List<ValBooksetInfoDto> list = interfCommonService.getBokBooksetByProdId_Layering(acctSubjectId);
            // IF DTO IS NULL OR DTO.SIZE()==0
            if (CollectionUtils.isEmpty(list)) {
                // 变量.resultObject.处理结果代码 = "1111"
                // 变量.resultObject.处理结果描述 = "产品代码["+输入.产品代码+"]无对应核算主体"
                resultObject.setResultCode(ResultCode.WARNING.getCode());
//                resultObject.setResultMsg("产品代码[" + prodid + "]无对应核算主体");
                if(stlOrders.getChannelId()==null||"".equals(stlOrders.getChannelId())){
                    resultObject.setResultMsg("产品代码[" + prodid + "]无对应核算主体");
                }else{
                    resultObject.setResultMsg("产品代码[" + prodid +FinalString.LAYERING_SPLIT+stlOrders.getChannelId()+ "]无对应核算主体");
                }
                // 直接执行第3步
                // return 变量.resultObject
                return resultObject;
            }
            // 变量.账套信息List.addAll(DTO)
            listValBooksetInfoDto.addAll(list);

            // LOOP 变量.账套信息List L
            for (ValBooksetInfoDto item : listValBooksetInfoDto) {
                // 2.2.2 查询本方资金账户的账户类型
                // 定义 变量.业务明细类型
                String busiDetailType = null;
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询账套清算款账户SHEET
                BokBooksetCapacctInfo bokBooksetCapacctInfo = interfCommonService.queryBokBooksetCapacctInfo(item.getBooksetId(),
                        stlOrders.getMyAcctNo(), stlOrders.getCcy(),null);
                // IF DTO IS NULL
                if (bokBooksetCapacctInfo == null) {
                    // 变量.resultObject.处理结果代码 = "1111"
                    // 变量.resultObject.处理结果描述 = "账套["+变量.账套代码+"]无资金账户["+输入.结算指令交易信息Dto.本方资金账户+"]"
                    resultObject.setResultCode(ResultCode.WARNING.getCode());
                    resultObject.setResultMsg("账套[" + item.getBooksetId() + "]无资金账户[" + stlOrders.getMyAcctNo() + "]");
                    // 直接执行第3步
                    return resultObject;
                }
                // 变量.业务明细类型 = DTO.资金账户类型
                busiDetailType = bokBooksetCapacctInfo.getAcctType();
                // 定义 变量.账务明细代码 =
                // interfCommonService.getBokDetailId(输入.结算指令交易信息Dto.本方资金账户,null,变量.业务明细类型,null,null)
                BokDetailId bokDetailId = interfCommonService.getBokDetailId(stlOrders.getMyAcctNo(), null, busiDetailType, null,null,stlOrders.getCcy());

                // 2.2.3 初始化账务明细代码表
                BokDetailId bokDetailIdInitDto = new BokDetailId();
                // 字段名 字段code 数据设置值
                // 账套代码 BOOKSET_ID L.账套代码
                bokDetailIdInitDto.setBooksetId(item.getBooksetId());
                // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
                bokDetailIdInitDto.setBokDetailId(bokDetailId.getBokDetailId());
                // 业务明细代码 BUSI_ID 输入.结算指令交易信息Dto.本方资金账户
                bokDetailIdInitDto.setBusiId(stlOrders.getMyAcctNo());
                // 业务明细类型 BUSI_DETAIL_TYPE 变量.业务明细类型
                bokDetailIdInitDto.setBusiDetailType(busiDetailType);
                // 交易编号 TRADE_ID 输入.结算指令交易信息Dto.指令编号
                bokDetailIdInitDto.setTradeId(stlOrders.getOrderId());
                /**ADD BY WUJIE ON 20190829 START**/
                // 币种 CCY 输入.结算指令交易信息Dto.币种
                bokDetailIdInitDto.setCcy(stlOrders.getCcy());
                /**ADD BY WUJIE ON 20190829 END**/
                // 变量.入库账务明细代码List.add(初始化结果)
                listBokDetailId.add(bokDetailIdInitDto);

                // 2.2.4 初始化入账信息表
                BokDealAccounting bokDealAcctInitDto = new BokDealAccounting();
                // 字段名 字段code 数据设置值
                // 账套代码 BOOKSET_ID L.账套代码
                bokDealAcctInitDto.setBooksetId(item.getBooksetId());
                // 业务编号 BUSI_NO 输入.结算指令交易信息Dto.指令编号
                bokDealAcctInitDto.setBusiNo(stlOrders.getOrderId());
                // 业务表 BUSI_TABLE 数据字典：业务表.结算指令表
                bokDealAcctInitDto.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
                // 录入日 BOOKSET_DATE 输入.结算指令交易信息Dto.结算日期
                bokDealAcctInitDto.setBooksetDate(stlOrders.getSettleDate());
                // 入账日 BOOK_DATE 输入.结算指令交易信息Dto.结算日期
                bokDealAcctInitDto.setBookDate(stlOrders.getSettleDate());
                // 实际发生日 HAPPEN_DATE 输入.结算指令交易信息Dto.结算日期
                bokDealAcctInitDto.setHappenDate(stlOrders.getSettleDate());
                // 是否已入账 IS_ACCOUNTED 数据字典：是否.否
                bokDealAcctInitDto.setIsAccounted(DictCodeList.是否_否.getValue());
                // 变量.入库入账信息List.add(初始化结果)
                listBokDealAcct.add(bokDealAcctInitDto);

                // 2.2.5 初始化入账明细信息表
                BokDealAccountingDetail bokDealAcctDetailInitDto = new BokDealAccountingDetail();
                // 字段名 字段code 数据设置值
                // 账套代码 BOOKSET_ID L.账套代码
                bokDealAcctDetailInitDto.setBooksetId(item.getBooksetId());
                // 业务编号 BUSI_NO 输入.结算指令交易信息Dto.指令编号
                bokDealAcctDetailInitDto.setBusiNo(stlOrders.getOrderId());
                // 业务表 BUSI_TABLE 数据字典：业务表.结算指令表
                bokDealAcctDetailInitDto.setBusiTable(DictCodeList.业务表_结算指令表.getValue());
                // 业务类型 BUSI_TYPE 数据字典：业务类型.指令交易
                bokDealAcctDetailInitDto.setBusiType(DictCodeList.业务类型_指令交易.getValue());
                // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
                bokDealAcctDetailInitDto.setBokDetailId(bokDetailId.getBokDetailId());
                // 变量.入库入账明细信息List.add(初始化结果)
                listBokDealAcctDetail.add(bokDealAcctDetailInitDto);
            }

            // 2.2.6 初始化完成的入账信息入数据库
            // 入数据库数据 入库表 表名 补充说明
            // 变量.入库入账信息List 入账信息表 BOK_DEAL_ACCOUNTING
            if (CollectionUtils.isNotEmpty(listBokDealAcct)) {
                valDealAccountingBatchMapper.insertBatch(listBokDealAcct);
            }
            // 变量.入库入账明细信息List 入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
            if (CollectionUtils.isNotEmpty(listBokDealAcctDetail)) {
                valDealAccountingDetailBatchMapper.insertBatch(listBokDealAcctDetail);
            }
            // 变量.入库账务明细代码List 账务明细代码表 BOK_DETAIL_ID 先根据账套代码、账务明细代码删除，然后入库
            if (CollectionUtils.isNotEmpty(listBokDetailId)) {
                valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
                valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
            }
            //指令表入账
            List<StlOrders> listStlOrders = new ArrayList<StlOrders>();
            listStlOrders.add(stlOrders);
            valOrdersBatchMapper.insertBatch(listStlOrders);
            //指令扩展表入账
            StlOrdersExt valStlOrdersExt = new StlOrdersExt();
            valStlOrdersExt.setOrderId(stlOrders.getOrderId());
            valStlOrdersExt.setIsTrade(DictCodeList.是否_否.getValue());
            List<StlOrdersExt> listvalStlOrdersExt = new ArrayList<StlOrdersExt>();
            listvalStlOrdersExt.add(valStlOrdersExt);
            valOrdersExtBatchMapper.insertBatch(listvalStlOrdersExt);

            // 变量.resultObject.处理结果描述 = "成功"
            resultObject = new ResultObject();
            resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        }
        else if (DictCodeListExt1.交易状态_撤销.getValue().equals(stlOrders.getOrderStatus())) {
            // ELSE IF 数据字典：交易状态.删除 eq 输入.结算指令交易信息Dto.指令状态

            // 定义 变量.交易信息Dto TrdProductDeal
            TrdProductDeal trdProductDeal = new TrdProductDeal();
            // 交易信息Dto 赋值
            // 交易编号 输入.结算指令交易信息Dto.指令编号
            trdProductDeal.setTradeId(stlOrders.getOrderId());
            // 交易状态 输入.结算指令交易信息Dto.指令状态
            trdProductDeal.setTradeStatus(stlOrders.getOrderStatus());
            // 原交易编号 输入.结算指令交易信息Dto.原指令编号
            trdProductDeal.setpTradeId(stlOrders.getpOrderId());
            // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_删除SHEET
            resultObject = this.bokTradeBuildForDel(trdProductDeal);
        }
        return resultObject;
    }

    /**
     * 交易入账生成信息删除
     * @param tradeId
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject bokTradeBuldInfoDelete(String tradeId) {
        // 定义 变量.resultObject
        // 变量.resultObject.处理结果代码 = "0000"
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.1 入账账务明细表
        bokTradeBuildMapper.deleteBokAcctDetail(tradeId);

        // 2.2 入账信息表
        bokTradeBuildMapper.deleteBokAcct(tradeId);

        // 2.3 结算指令扩展表
        valInterfCommonMapper.delStlOrdersExt(tradeId);

        // 2.4 结算指令表
        valInterfCommonMapper.delStlOrders(tradeId);

        return resultObject;
    }

    /**
     * 输入参数业务CHECK : 结算指令交易入账
     *
     * @param prodid 产品代码
     * @param prodFinprodType 产品类型(F16、F18、F22)：见数据字典：金融产品类型
     * @param prodVdate 子产品起息日(产品类型为F22时为必输项)
     * @param stlOrders 结算指令交易信息Dto(StlOrders)
     * @param resultObject
     */
    private void checkStlOrdersBokTradeBuild(String prodid, String prodFinprodType, Date prodVdate, StlOrders stlOrders, ResultObject resultObject) {
        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

        // 结算指令交易信息Dto.指令状态
        if (StringUtils.isEmpty(stlOrders.getOrderStatus())) {
            sb.append("指令状态不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.交易状态.getValue(), stlOrders.getOrderStatus()) == null) {
            sb.append("指令状态值不在有效范围内!").append("\n");
        }

        if (!DictCodeListExt1.交易状态_撤销.getValue().equals(stlOrders.getOrderStatus())) {
            // 产品代码
            if (StringUtils.isEmpty(prodid)) {
                sb.append("产品代码不可为空!").append("\n");
            }
            // 产品类型
            if (StringUtils.isEmpty(prodFinprodType)) {
                sb.append("产品类型不可为空!").append("\n");
            }
            else if (systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(), prodFinprodType) == null) {
                sb.append("产品类型值不在有效范围内!").append("\n");
            }
            // 子产品起息日
            if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodFinprodType) && prodVdate == null) {
                sb.append("子产品起息日不可为空!").append("\n");
            }
            // 结算指令交易信息Dto.结算日期
            if (stlOrders.getSettleDate() == null) {
                sb.append("结算日期不可为空!").append("\n");
            }
            // 结算指令交易信息Dto.本方资金账户
            if (StringUtils.isEmpty(stlOrders.getMyAcctNo())) {
                sb.append("指令交易信息Dto【本方资金账户】不可为空!").append("\n");
            }
            // 结算指令交易信息Dto.收付方向
            if (StringUtils.isEmpty(stlOrders.getPs())) {
                sb.append("指令交易信息Dto【收付方向】不可为空!").append("\n");
            }
            else if (systemToolService.getDataDictItem(DictCode.收付方向.getValue(), stlOrders.getPs()) == null) {
                sb.append("指令交易信息Dto【收付方向】不在有效范围内!").append("\n");
            }
            // 结算指令交易信息Dto.币种
            if (StringUtils.isEmpty(stlOrders.getCcy())) {
                sb.append("指令交易信息Dto【币种】不可为空!").append("\n");
            }
            else if (systemToolService.getDataDictItem(DictCode.币种.getValue(), stlOrders.getCcy()) == null) {
                sb.append("指令交易信息Dto【币种】不在有效范围内!").append("\n");
            }
        }
        else { // eq 交易状态.删除

            // 结算指令交易信息Dto.原指令编号
            if (StringUtils.isEmpty(stlOrders.getpOrderId())) {
                sb.append("指令交易信息Dto【原指令编号】不可为空!").append("\n");
            }
        }

        if (StringUtils.isNotEmpty(sb.toString())) {
            resultObject.setResultCode(ResultCode.WARNING.getCode());
            resultObject.setResultMsg(sb.toString());
        }
    }

    /**
     * 输入参数业务CHECK : 同业借款基本信息初始化
     *
     * @param finTradeProduct
     * @param resultObject
     */
    private void checkPaForInterBorrowBaseInfoInit(FinTradeProduct finTradeProduct, ResultObject resultObject) {
        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

        // 金融产品代码
        if (StringUtils.isEmpty(finTradeProduct.getFinprodId())) {
            sb.append("金融产品代码不可为空!").append("\n");
        }

        // 金融产品类型
        if (StringUtils.isEmpty(finTradeProduct.getFinprodType())) {
            sb.append("金融产品类型不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(), finTradeProduct.getFinprodType()) == null) {
            sb.append("金融产品类型值不在有效值范围内!").append("\n");
        }

        // 收益类型
        if (StringUtils.isEmpty(finTradeProduct.getProfitType())) {
            sb.append("收益类型不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.收益类型.getValue(), finTradeProduct.getProfitType()) == null) {
            sb.append("收益类型值不在有效值范围内!").append("\n");
        }

        // 名义本金
        if (finTradeProduct.getPrin() == null) {
            sb.append("名义本金不可为空!").append("\n");
        }

        // 本金币种
        if (StringUtils.isEmpty(finTradeProduct.getCcy())) {
            sb.append("本金币种不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.币种.getValue(), finTradeProduct.getCcy()) == null) {
            sb.append("本金币种值不在有效值范围内!").append("\n");
        }

        // 期限(天)
        if (finTradeProduct.getTerm() == null) {
            sb.append("期限(天)不可为空!").append("\n");
        }

        // 期限品种
        if (StringUtils.isNotEmpty(finTradeProduct.getTermType()) && systemToolService.getDataDictItem(DictCode.期限品种_内部拆借.getValue(), finTradeProduct.getTermType()) == null) {
            sb.append("期限品种值不在有效值范围内!").append("\n");
        }

        // 利率类型
        if (StringUtils.isEmpty(finTradeProduct.getIntType())) {
            sb.append("利率类型不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.利率类型.getValue(), finTradeProduct.getIntType()) == null) {
            sb.append("利率类型值不在有效值范围内!").append("\n");
        }

        // 固定利率
        if (finTradeProduct.getIntRate() == null) {
            sb.append("固定利率不可为空!").append("\n");
        }

        // 计息基础
        if (StringUtils.isEmpty(finTradeProduct.getBasis())) {
            sb.append("计息基础不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.计息基础.getValue(), finTradeProduct.getBasis()) == null) {
            sb.append("计息基础值不在有效值范围内!").append("\n");
        }

        // 到期金额
        if (finTradeProduct.getmTradeAmt() == null) {
            sb.append("到期金额不可为空!").append("\n");
        }

        // 本金收益特征
        if (StringUtils.isEmpty(finTradeProduct.getCapiIncomeFeature())) {
            sb.append("本金收益特征不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.本金收益特征.getValue(), finTradeProduct.getCapiIncomeFeature()) == null) {
            sb.append("本金收益特征值不在有效值范围内!").append("\n");
        }

        if (StringUtils.isNotEmpty(sb.toString())) {
            resultObject.setResultCode(ResultCode.WARNING.getCode());
            resultObject.setResultMsg(sb.toString());
        }
    }


    /**
     * 交易入账接口参数校验
     * @param prodid
     * @param prodFinprodType
     * @param branch
     * @param trdProductDeal
     * @param resultObject
     */
    private void checkBokTradeBuild(String prodid, String prodFinprodType, Long branch, TrdProductDeal trdProductDeal, ResultObject resultObject) {
        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

        if (StringUtils.isEmpty(trdProductDeal.getTradeStatus())) {
            sb.append("交易信息【交易状态】不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.交易状态.getValue(), trdProductDeal.getTradeStatus()) == null) {
            sb.append("交易信息【交易状态】值不在有效范围内!").append("\n");
        }

        if (DictCodeList.业务类型_银行间债券交易.getValue().equals(trdProductDeal.getBusiType())) {
            if (StringUtils.isEmpty(trdProductDeal.getInvAim())) {
                sb.append("交易信息【投资目的】不可为空!").append("\n");
            }
            else if (DictCodeList.业务类型_银行间债券交易.getValue().equals(trdProductDeal.getBusiType())
                    && systemToolService.getDataDictItem(DictCode.投资目的.getValue(), trdProductDeal.getInvAim()) == null) {
                sb.append("交易信息【投资目的】值不在有效范围内!").append("\n");
            }
        }

        if (DictCodeListExt1.交易状态_撤销.getValue().equals(trdProductDeal.getTradeStatus())) {
            if (StringUtils.isEmpty(trdProductDeal.getpTradeId())) {
                sb.append("交易信息【原交易编号】不可为空!").append("\n");
            }
        }
        else {
            if (StringUtils.isEmpty(prodid)) {
                sb.append("产品代码不可为空!").append("\n");
            }
            if (StringUtils.isEmpty(prodFinprodType)) {
                sb.append("产品类型不可为空!").append("\n");
            }
            else if (systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(), prodFinprodType) == null) {
                sb.append("产品类型值不在有效范围内!").append("\n");
            }
            if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodFinprodType) && branch == null) {
                sb.append("子产品起息日不可为空!").append("\n");
            }
            if (StringUtils.isEmpty(trdProductDeal.getFinprodId())) {
                sb.append("交易信息【金融产品代码】不可为空!").append("\n");
            }
            if (StringUtils.isEmpty(trdProductDeal.getFinprodType())) {
                sb.append("交易信息【金融产品类型】不可为空!").append("\n");
            }
            else if (systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(), trdProductDeal.getFinprodType()) == null) {
                sb.append("交易信息【金融产品类型】值不在有效范围内!").append("\n");
            }
            if (StringUtils.isEmpty(trdProductDeal.getBusiType())) {
                sb.append("交易信息【业务类型】不可为空!").append("\n");
            }
            else if (systemToolService.getDataDictItem(DictCode.业务类型.getValue(), trdProductDeal.getBusiType()) == null) {
                sb.append("交易信息【业务类型】值不在有效范围内!").append("\n");
            }
        }

        if (StringUtils.isNotEmpty(sb.toString())) {
            resultObject.setResultCode(ResultCode.WARNING.getCode());
            resultObject.setResultMsg(sb.toString());
        }

    }

    /**
     * 输入参数业务CHECK : 产品其他信息初始化
     *
     * @param prodInfoDto 产品标的Dto
     * @param resultObject
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private void checkProdOtherInfoInit(ProdInfoDto prodInfoDto, ResultObject resultObject) {
        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

        if (StringUtils.isEmpty(prodInfoDto.getFinprodId())) {
            sb.append("金融产品代码不可为空!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getFinprodType())) {
            sb.append("金融产品类型不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(), prodInfoDto.getFinprodType()) == null) {
            sb.append("金融产品类型值不在有效值范围内!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getFinprodName())) {
            sb.append("金融产品名称不可为空!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getProfitType())) {
            sb.append("收益类型不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.收益类型.getValue(), prodInfoDto.getProfitType()) == null) {
            sb.append("收益类型值不在有效值范围内!").append("\n");
        }

        if (prodInfoDto.getVdate() == null) {
            sb.append("起息日不可为空!").append("\n");
        }

        if (prodInfoDto.getMdate() == null) {
            sb.append("到期日不可为空!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getCcy())) {
            sb.append("币种不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.币种.getValue(), prodInfoDto.getCcy()) == null) {
            sb.append("币种不在有效值范围内!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getCalendarId())) {
            sb.append("交易日历不可为空!").append("\n");
        }

        // if (StringUtils.isEmpty(prodInfoDto.getIsCalc())) {
        // sb.append("托管活期利息计提不可为空!").append("\n");
        // }
        // else if (DictCodeList.from(DictCode.是否,
        // prodInfoDto.getIsCalc())== null) {
        // sb.append("托管活期利息计提不在有效值范围内!").append("\n");
        // }

        if (StringUtils.isEmpty(prodInfoDto.getIsLayerAccount())) {
            sb.append("是否分层核算不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.是否.getValue(), prodInfoDto.getIsLayerAccount()) == null) {
            sb.append("是否分层核算不在有效值范围内!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getIsAccount())) {
            sb.append("是否核算不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.是否.getValue(), prodInfoDto.getIsAccount()) == null) {
            sb.append("是否核算不在有效值范围内!").append("\n");
        }

        if (StringUtils.isEmpty(prodInfoDto.getIsTax())) {
            sb.append("是否计税不可为空!").append("\n");
        }
        else if (systemToolService.getDataDictItem(DictCode.是否.getValue(), prodInfoDto.getIsTax()) == null) {
            sb.append("是否计税不在有效值范围内!").append("\n");
        }

        if (StringUtils.isNotEmpty(sb.toString())) {
            resultObject.setResultCode(ResultCode.FAILURE.getCode());
            resultObject.setResultMsg(sb.toString());
        }
    }


    /**
     * 拆入到期_新增
     * @param prodid 产品代码
     * @param prodFinprodType 产品类型(F16、F18、F22)：见数据字典：金融产品类型
     * @param branch 分支序号
     * @param dealList 汇总交易明细集合(List<TrdProductDeal>)
     * @param trdTradeFeeDetail 通道费用信息
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private ResultObject lendMdateAdd(String prodid, String prodFinprodType, Long branch, List<TrdProductDeal> dealList,
                                     TrdTradeFeeDetail trdTradeFeeDetail) {

        // 定义 变量.resultObject
        // 变量.resultObject.处理结果代码 = "0000"
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());

        // 2.1 输入参数业务CHECK
        checkLsendMdateAdd(prodid, prodFinprodType, branch, dealList, resultObject);
        // IF 变量.resultObject.处理结果代码 ne "0000"
        if (ResultCode.SUCCESS.getCode() != resultObject.getResultCode()) {
            return resultObject;
        }

        // 2.2 交易处理

        // 2.2.0 针对净值型、货币型投资组合的交易，通过组合下的理财产品进行交易入账
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)){
            FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(prodid);
            if(finProduct != null && finProduct.getFinprodId() != null){
                prodid = finProduct.getFinprodId();
                prodFinprodType = finProduct.getFinprodType();
            }
        }

        // 定义 变量.金融产品表Dto
        TrdProductDeal trdProductDealVo = new TrdProductDeal();
        // 定义 变量.金融产品交易附属表Dto
        TrdProductDealAdd trdProductDealAddVo = new TrdProductDealAdd();

        // 2.2.1 初始化金融产品表
        // 定义 变量.本金金额
        BigDecimal prinAmt = BigDecimal.ZERO;
        // 定义 变量.实收利息
        BigDecimal intAmt = BigDecimal.ZERO;
        // 定义 变量.交易份额
        BigDecimal shareAmt = BigDecimal.ZERO;
        // 循环交易数据，获取本金金额和实收利息
        for(TrdProductDeal deal : dealList){
            if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_拆出.getValue().equals(deal.getFinprodType())) {
                if(DictCodeList.交易方向_还本到账.getValue().equals(deal.getPs())){
                    //变量.本金金额 +=	L.交易本金
                    prinAmt = ArithmeticUtil.doAdd(prinAmt,deal.getPrinAmt());
                }
                else if(DictCodeList.交易方向_收息到账.getValue().equals(deal.getPs())){
                    //变量.实收利息 +=L.利息总额
                    intAmt = ArithmeticUtil.doAdd(intAmt,deal.getIntAmt());
                }
            }
            else if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(deal.getFinprodType())) {
                if(DictCodeList.交易方向_还本到账.getValue().equals(deal.getPs())){
                    //变量.交易份额 = L.交易份额
                    //shareAmt = deal.getShareAmt();
                    //变量.交易份额 +=L.交易份额
                    shareAmt = ArithmeticUtil.doAdd(shareAmt,deal.getShareAmt());

                    //变量.本金金额 +=	L.交易本金
                    prinAmt = ArithmeticUtil.doAdd(prinAmt,deal.getPrinAmt());
                }
                else if(DictCodeList.交易方向_收息到账.getValue().equals(deal.getPs())){
                    //变量.实收利息 +=L.利息总额
                    intAmt = ArithmeticUtil.doAdd(intAmt,deal.getIntAmt());
                }
            }
        }
        // 定义 变量.净价总额
        BigDecimal cpriceAmt = BigDecimal.ZERO;
        // 定义 变量.全价总额
        BigDecimal fpriceAmt = BigDecimal.ZERO;
        // 定义 变量.交易金额
        BigDecimal tradeAmt = BigDecimal.ZERO;
        // IF 数据字典：金融产品类型.拆入 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(dealList.get(0).getFinprodType())
                || DictCodeListExt1.金融产品类型_拆出.getValue().equals(dealList.get(0).getFinprodType())) {
            // 变量.净价总额 = 变量.本金金额
            cpriceAmt = prinAmt;
            // 变量.交易份额 = 变量.本金金额
            shareAmt = prinAmt;
            // 变量.全价总额 = 变量.本金金额 + 变量.实收利息
            fpriceAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
            // 变量.交易金额 = 变量.本金金额 + 变量.实收利息
            tradeAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
        }
        // ELSE IF 数据字典：金融产品类型.债券质押式正回购 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        // OR 数据字典：金融产品类型.债券质押式逆回购 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        else if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(dealList.get(0).getFinprodType())
                || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(dealList.get(0).getFinprodType())) {
            // 变量.净价总额 = 变量.交易份额
            cpriceAmt = shareAmt;
            // 变量.全价总额 = 变量.本金金额 + 变量.实收利息
            fpriceAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
            // 变量.交易金额 = 变量.本金金额 + 变量.实收利息
            tradeAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
        }
        //ELSE IF 数据字典：金融产品类型.债券买断式正回购 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        //OR 数据字典：金融产品类型.债券买断式逆回购 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        else if(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(dealList.get(0).getFinprodType())
                || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(dealList.get(0).getFinprodType())){
            // 变量.净价总额 = NULL
            cpriceAmt = null;
            // 变量.交易份额 = 变量.交易份额
            //shareAmt = shareAmt;
            // 变量.全价总额 = NULL
            fpriceAmt = null;
            // 变量.交易金额 = 变量.本金金额 + 变量.实收利息
            tradeAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
        }

        //[金融产品表Dto]塞值
        trdProductDealVo.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_到期.getValue(), dealList.get(0).getTradeDate(), "A"));//交易编号
        trdProductDealVo.setBusiType(dealList.get(0).getBusiType());//业务类型
        trdProductDealVo.setFinprodId(dealList.get(0).getFinprodId());//金融产品代码
        trdProductDealVo.setFinprodType(dealList.get(0).getFinprodType());//金融产品类型（估值核算）
        trdProductDealVo.setFinprodType2(dealList.get(0).getFinprodType2());//金融产品类型（投管分类）
        trdProductDealVo.setBranch(InterfCommonConstant.分支序号_默认);//分支序号
        trdProductDealVo.setTradeDate(dealList.get(0).getTradeDate());//交易日期
        trdProductDealVo.setVdate(dealList.get(0).getVdate());//起息日
        trdProductDealVo.setMdate(dealList.get(0).getMdate());//到期日
        trdProductDealVo.setSettleDate(dealList.get(0).getSettleDate());//交割日期
        trdProductDealVo.setCcy(dealList.get(0).getCcy());//交易币种
        trdProductDealVo.setCpriceAmt(cpriceAmt);//净价总额
        trdProductDealVo.setShareAmt(shareAmt);//交易份额
        trdProductDealVo.setPrinAmt(prinAmt);//交易本金
        trdProductDealVo.setIntAmt(intAmt);//利息总额
        trdProductDealVo.setFpriceAmt(fpriceAmt);//全价总额
        trdProductDealVo.setTradeAmt(tradeAmt);//交易金额
        trdProductDealVo.setPs(DictCodeList.交易方向_到期.getValue());//交易方向
        trdProductDealVo.setStatus(DictCodeListExt1.交易状态_初始.getValue());//交易状态
        trdProductDealVo.setIsCancel(DictCodeList.是否_否.getValue());//是否已撤销
        trdProductDealVo.setTotleTradeId(dealList.get(0).getTotleTradeId());//汇总交易编号
        trdProductDealVo.setCounterId(dealList.get(0).getCounterId());//交易对手
        trdProductDealVo.setTrader(dealList.get(0).getTrader());//交易员
        trdProductDealVo.setCashId(dealList.get(0).getCashId());//现金流代码
        trdProductDealVo.setTradeMarket(dealList.get(0).getTradeMarket());//交易场所
        trdProductDealVo.setTradePlat(dealList.get(0).getTradePlat());//交易平台
        trdProductDealVo.setSecManageAcctId(dealList.get(0).getSecManageAcctId());//证券管理户代码
        trdProductDealVo.setTradeMarketMode(dealList.get(0).getTradeMarketMode());//交易市场
        trdProductDealVo.setTradeStatus(dealList.get(0).getTradeStatus());
        /**add by wujie on 20190530 start**/
        trdProductDealVo.setChlId(dealList.get(0).getChlId());
        /**add by wujie on 20190530 end**/
        // 2.2.2 初始化金融产品交易附属表
        trdProductDealAddVo.setTradeId(trdProductDealVo.getTradeId());//交易编号
        trdProductDealAddVo.setPortfolioId(prodid);//投资组合代码
        trdProductDealAddVo.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());//生成方式

        // 2.2.3 初始化完成数据库入库
        trdProductDealMapper.insert(trdProductDealVo);//金融产品表数据入库
        trdProductDealAddMapper.insert(trdProductDealAddVo);//金融产品交易附属表数据入库
        // 2.2.4 调用交易入账接口
        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_新增SHEET
        resultObject = this.bokTradeBuildForAdd(prodid, prodFinprodType, branch, trdProductDealVo);
        /** modify by dongjun on 20190321 通道变更 start **/
        boolean chlFeeProFlag = trdProductDealVo != null && trdProductDealVo.getChlId() != null
                && trdTradeFeeDetail != null && trdTradeFeeDetail.getFeeAmt() != null && trdTradeFeeDetail.getFeeAmt().compareTo(BigDecimal.ZERO) > 0;
        if(chlFeeProFlag){
            TrdProductDeal feeDeal = new TrdProductDeal();
            TrdProductDealAdd feeDealAdd = new TrdProductDealAdd();
            TrdTradeFeeDetail feeDetail = new TrdTradeFeeDetail();
            feeDeal.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_付费.getValue(),trdProductDealVo.getTradeDate(),"A"));
            feeDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
            feeDeal.setFinprodType(DictCodeListExt1.金融产品类型_交易费用.getValue());
            feeDeal.setBranch(InterfCommonConstant.分支序号_默认);
            feeDeal.setTradeDate(trdProductDealVo.getTradeDate());
            feeDeal.setSettleDate(trdProductDealVo.getSettleDate());
            feeDeal.setCcy(trdProductDealVo.getCcy());
            feeDeal.setTradeAmt(trdTradeFeeDetail.getFeeAmt());
            feeDeal.setPs(DictCodeList.交易方向_付费.getValue());
            feeDeal.setTradeStatus(trdProductDealVo.getTradeStatus());
            feeDeal.setTotleTradeId(trdProductDealVo.getTradeId());
            feeDeal.setChlId(trdProductDealVo.getChlId());
            feeDeal.setRegistOrg(trdProductDealVo.getRegistOrg());

            feeDealAdd.setTradeId(feeDeal.getTradeId());
            feeDealAdd.setPortfolioId(prodid);
            feeDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

            feeDetail.setTradeId(feeDeal.getTradeId());
            feeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            feeDetail.setIsPayWithTrade(trdTradeFeeDetail.getIsPayWithTrade());
            feeDetail.setFeeAmt(trdTradeFeeDetail.getFeeAmt());
            feeDetail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());

            // 2.3.2 通道费用交易入库
            trdProductDealMapper.insert(feeDeal);
            trdProductDealAddMapper.insert(feeDealAdd);
            trdTradeFeeDetailMapper.insert(feeDetail);
            // 2.3.3 调用交易入账接口
            resultObject = bokTradeBuildForAdd(prodid, prodFinprodType, branch, feeDeal);
        }
        /** modify by dongjun on 20190321 通道变更  end **/
        if(!StringUtil.isEmpty(trdProductDealVo.getChlId())){
            //判断通道属性
            FinProductAdd payType = finProductAddMapper.selectByPrimaryKey(trdProductDealVo.getChlId());
            if (DictCodeListExt2.划款方式_逐笔划款.getValue().equals(payType.getPayType())){
                ChannelProdDeal channelProdDeal = new ChannelProdDeal();
                channelProdDeal.setTradeId(trdProductDealVo.getTradeId());//交易编号
                channelProdDeal.setBusiType(trdProductDealVo.getBusiType());//业务类型
                channelProdDeal.setFinprodId(trdProductDealVo.getFinprodId());//金融产品代码
                channelProdDeal.setFinprodType(trdProductDealVo.getFinprodType());//金融产品类型（估值核算）
                channelProdDeal.setFinprodType2(trdProductDealVo.getFinprodType2());//金融产品类型（投管分类）
                channelProdDeal.setBranch(trdProductDealVo.getBranch());//分支序号
                channelProdDeal.setTradeDate(trdProductDealVo.getTradeDate());//交易日期
                channelProdDeal.setVdate(trdProductDealVo.getVdate());//起息日
                channelProdDeal.setMdate(trdProductDealVo.getMdate());//到期日
                channelProdDeal.setSettleDate(trdProductDealVo.getSettleDate());//交割日期
                channelProdDeal.setCcy(trdProductDealVo.getCcy());//交易币种
                /**modify by wujie on 20190603 start
                 channelProdDeal.setCpriceAmt(trdProductDealVo.getCpriceAmt());//净价总额
                 channelProdDeal.setShareAmt(trdProductDealVo.getShareAmt());//交易份额
                 channelProdDeal.setPrinAmt(trdProductDealVo.getPrinAmt());//交易本金
                 channelProdDeal.setIntAmt(trdProductDealVo.getIntAmt());//利息总额
                 **/
                channelProdDeal.setShareAmt(trdProductDealVo.getTradeAmt());//交易份额
                channelProdDeal.setPrinAmt(trdProductDealVo.getTradeAmt());//交易本金
                /**moidfy by wujie on 20190603 end**/
                channelProdDeal.setFpriceAmt(trdProductDealVo.getFpriceAmt());//全价总额
                channelProdDeal.setTradeAmt(trdProductDealVo.getTradeAmt());//交易金额
                channelProdDeal.setPs(trdProductDealVo.getPs());//交易方向
                channelProdDeal.setStatus(trdProductDealVo.getStatus());//交易状态
                channelProdDeal.setIsCancel(trdProductDealVo.getIsCancel());//是否已撤销
                channelProdDeal.setTotleTradeId(trdProductDealVo.getTotleTradeId());//汇总交易编号
                channelProdDeal.setCounterId(trdProductDealVo.getCounterId());//交易对手
                channelProdDeal.setTrader(trdProductDealVo.getTrader());//交易员
                channelProdDeal.setCashId(trdProductDealVo.getCashId());//现金流代码
                channelProdDeal.setTradeMarket(trdProductDealVo.getTradeMarket());//交易场所
                channelProdDeal.setTradePlat(trdProductDealVo.getTradePlat());//交易平台
                channelProdDeal.setSecManageAcctId(trdProductDealVo.getSecManageAcctId());//证券管理户代码
                channelProdDeal.setTradeMarketMode(trdProductDealVo.getTradeMarketMode());//交易市场
                channelProdDeal.setTradeStatus(trdProductDealVo.getTradeStatus());
                channelProdDeal.setChlId(dealList.get(0).getChlId());
                channelProdDeal.setProdId(prodid);
                channelProdDeal.setDealSepFlag("00");
                //调用理财产品借通道投资
                resultObject = this.getChannelProdDeal(channelProdDeal, prodFinprodType);
            }
        }

        /** modify by dongjun on 20190321 通道变更 end **/
        return resultObject;
    }

    /**
     * 拆入到期_撤销
     * @param totleTradeId 汇总交易编号
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject lendMdatDelete(String totleTradeId) {

        // 定义 变量.resultObject
        // 变量.resultObject.处理结果代码 = "0000"
//        ResultObject resultObject = new ResultObject(ResultCode.SUCCESS.getCode());
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        //定义 变量.待删除到期交易
        List<TrdProductDeal> toDeltrdProductDealList = new ArrayList<>();

        // 2.1 根据汇总交易编号、交易方向、交易状态查询需要删除的到期交易
        // 定义 变量.拆入到期交易编号
        List<TrdProductDeal> listTrdProductDeal = valInterfCommonMapper.getTradeIdByFilter(totleTradeId);

        // 获取不到交易情况
        if(CollectionUtils.isEmpty(listTrdProductDeal)){
            resultObject.setResultCode(ResultCode.WARNING.getCode());
            resultObject.setResultMsg("汇总交易编号["+totleTradeId+"]无对应拆入到期交易");
            return resultObject;
        }

        // 变量.待删除交易信息List.addAll(变量.待删除到期交易)
        toDeltrdProductDealList.addAll(listTrdProductDeal);

        // 2.1.2 查询到期交易对应的通道费用交易
        List<TrdProductDeal> toDelFeeDealList = valInterfCommonMapper.getFeeDealByFilter(totleTradeId);

        if(CollectionUtils.isNotEmpty(toDelFeeDealList)){
            toDeltrdProductDealList.addAll(toDelFeeDealList);
        }

        // 2.2 删除交易信息及入账信息

        if(CollectionUtils.isNotEmpty(toDeltrdProductDealList)){
            toDeltrdProductDealList.forEach(i -> {
                TrdProductDeal trdProductDeal = new TrdProductDeal();
                trdProductDeal.setpTradeId(i.getTradeId());

                // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易信息_删除SHEET
                tradeInfoForDel(i.getTradeId());

                // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_删除SHEET
                bokTradeBuildForDel(trdProductDeal);
            });
        }

        return resultObject;
    }

    /**
     * 交易信息_删除
     * @param tradeId 交易编号(String)
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private ResultObject tradeInfoForDel(String tradeId){
        // 定义 变量.resultObject
        // 变量.resultObject.处理结果代码 = "0000"
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 处理详情
        // 2.1 结算指令扩展表
        valInterfCommonMapper.delStlOrdersExt(tradeId);
        // 2.2 结算指令表
        valInterfCommonMapper.delStlOrders(tradeId);
        // 2.3 交易费用明细表
        valInterfCommonMapper.delTrdTradeFeeDetail(tradeId);
        // 2.4 金融产品交易附属表
        valInterfCommonMapper.delTrdProductDealAdd(tradeId);
        // 2.5 金融产品交易
        valInterfCommonMapper.delTrdProductDeal(tradeId);

        return resultObject;
    }

    /**
     * 输入参数业务CHECK : 拆入到期_新增
     * @param prodid 产品代码
     * @param prodFinprodType 产品类型(F16、F18、F22)：见数据字典：金融产品类型
     * @param branch 分支序号
     * @param dealList 汇总交易明细集合(List<TrdProductDeal>)
     * @param resultObject
     */
    private void checkLsendMdateAdd(String prodid, String prodFinprodType, Long branch,List<TrdProductDeal> dealList, ResultObject resultObject) {

        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);

        if (dealList == null || dealList.size() == 0) {
            sb.append("待处理交易明细不可为空!").append("\n");
        }

        if (StringUtils.isEmpty(prodid)) {
            sb.append("产品代码不可为空!").append("\n");
        }

        if (StringUtils.isEmpty(prodFinprodType)) {
            sb.append("产品类型不可为空!").append("\n");
        } else if (systemToolService.getDataDictItem(DictCode.金融产品类型.getValue(), prodFinprodType) == null) {
            sb.append("产品类型值不在有效范围内!").append("\n");
        }

        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(prodFinprodType) && branch == null) {
            sb.append("子产品起息日不可为空!").append("\n");
        }

        if (StringUtils.isNotEmpty(sb.toString())) {
            resultObject.setResultCode(ResultCode.WARNING.getCode());
            resultObject.setResultMsg(sb.toString());
        }
    }

    /**
     * 理财产品借通道投资交易
     *
     * @param deal 理财产品借通道投资交易
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    @SuppressWarnings("unchecked")
	public ResultObject getChannelProdDeal(ChannelProdDeal deal, String productType){
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(productType)){
            FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(deal.getProdId());
            if(finProduct != null && finProduct.getFinprodId() != null){
                deal.setProdId(finProduct.getFinprodId());
            }
        }
        // 1.3 定义变量
        // 定义 变量.金融产品代码
        String finprodId = deal.getProdId() + FinalString.LAYERING_SPLIT + deal.getChlId();
        // 定义 变量.产品账套基本信息
        ValBooksetInfoDto valBooksetInfoDto = new ValBooksetInfoDto();
        // 定义 变量.通道基本信息
        TargetFinProduct targetFinProduct = new TargetFinProduct();
        // 定义 变量.resultObject
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.0 输入参数业务CHECK
        // 2.0.1 产品账套基本信息查询
        if(StringUtil.equals(DictCodeListExt1.交易状态_初始.getValue(), deal.getTradeStatus())){
            valBooksetInfoDto = interfCommonService.getBooksetInfoByProdId(deal.getProdId());
            if(valBooksetInfoDto == null){
                resultObject.setResultCode(ResultCode.WARNING.getCode());
                resultObject.setResultMsg(resultObject.getResultMsg() + "产品【" + deal.getProdId() + "】对应基本信息不存在；");
            }
        }
        //2.0.2 通道基本信息
        if(StringUtil.equals(DictCodeListExt1.交易状态_初始.getValue(), deal.getTradeStatus())){
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->标的类金融产品信息查询SHEET
            targetFinProduct =  interfCommonService.getTargetFinProduct(deal.getChlId());
            if(targetFinProduct == null){
                resultObject.setResultCode(ResultCode.WARNING.getCode());
                resultObject.setResultMsg(resultObject.getResultMsg() + "通道【" + deal.getChlId() + "】对应基本信息不存在；");
            }
        }

        if(StringUtil.equals(resultObject.getResultCode(), ResultCode.SUCCESS.getCode())){
            // 2.1 交易撤销处理逻辑
            if(StringUtil.equals(DictCodeListExt1.交易状态_撤销.getValue(), deal.getTradeStatus())){
                List<TrdProductDeal> listTrdProductDeal = valInterfCommonMapper.queryToDelDeal(deal.getpTradeId(),
                        DictCodeListExt1.交易状态_初始.getValue());
                if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
                    for(TrdProductDeal trd : listTrdProductDeal){
                        tradeInfoForDel(trd.getTradeId());
                    }
                }

                // 2.1.2 判断产品代码、通道代码是否还存在有效的交易
                List<TrdProductDeal> listTrdDeal = valInterfCommonMapper.queryToDelChlDeal(finprodId);
                // 2.1.2 删除通道核算信息
                /**modify by wujie on 20190612 start
                 if(CollectionUtils.isNotEmpty(listTrdDeal)){
                 **/
                if(!CollectionUtils.isNotEmpty(listTrdDeal)){
                    /**modify by wujie on 20190612 end**/
                    List<ValChannelProdDealDto> dtoList = new ArrayList<>();
                    ValChannelProdDealDto dto  = new ValChannelProdDealDto();
                    dto.setFinprodId(finprodId);
                    dtoList.add(dto);
                    // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->通道核算信息_删除SHEET
                    interfCommonService.channelAcctSubjectDelete(dtoList);
                }
            } else if(StringUtil.equals(DictCodeListExt1.交易状态_初始.getValue(), deal.getTradeStatus())){
                // 2.2.1 判断产品代码、通道代码对应核算主体是否已存在
                Map<String,Object> paramsMap = new HashMap<>();
                paramsMap.put("acctSubjectId", finprodId);
                // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->查询子账套信息SHEET
                List<BokBooksetSonDto> listBokBooksetSonDto = interfCommonService.queryBokBooksetSonInfo(finprodId, null, null);
                if(CollectionUtils.isEmpty(listBokBooksetSonDto)){
                    // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->通道核算信息_初始化SHEET
                    ChannelAcctSubjectDto channelAcctSubjectDto = interfCommonService.channeAaccountingInit(deal);
                    if(channelAcctSubjectDto != null && StringUtil.equals(channelAcctSubjectDto.getResultCode(), "00")){
                        List<ChannelAcctSubjectDto> dtoList = new ArrayList<>();
                        dtoList.add(channelAcctSubjectDto);
                        interfCommonService.channelAcctSubjectAdd(dtoList);
                    }
                }

                // 2.2.2 交易拆分处理
                // 定义 变量.通道投资交易List
                List<TrdProductDeal> listTrdProductDeal = new ArrayList<>();
                // 定义 变量.通道投资交易附属表List
                List<TrdProductDealAdd> listTrdProductDealAdd = new ArrayList<>();
                // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->产品借通道投资交易_拆分SHEET
                Map<String,Object> resultMap = getChannelProdDealSep(deal);
                if(resultMap.get("TRD_PRODUCT_DEAL") != null){
                    listTrdProductDeal = (List<TrdProductDeal>)resultMap.get("TRD_PRODUCT_DEAL");
                }
                if(resultMap.get("TRD_PRODUCT_DEAL_ADD") != null){
                    listTrdProductDealAdd = (List<TrdProductDealAdd>)resultMap.get("TRD_PRODUCT_DEAL_ADD");
                }

                // 初始化完成数据入库
                // 变量.通道投资交易List
                if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
                    listTrdProductDeal.forEach(i -> trdProductDealMapper.insert(i));
                }
                // 变量.通道投资交易附属表List
                if(CollectionUtils.isNotEmpty(listTrdProductDealAdd)){
                    listTrdProductDealAdd.forEach(i -> trdProductDealAddMapper.insert(i));
                }

                if(valBooksetInfoDto!=null && (CollectionUtils.isNotEmpty(listTrdProductDeal))){
                    for(TrdProductDeal trdProductDeal : listTrdProductDeal){
                        /** modify by dongjun on 20190417 货币型TA交易账未入账修正 start**/
                        String prodId = deal.getProdId();
                        String prodType = valBooksetInfoDto.getAcctSubjectType();
//	    	    			if(StringUtil.equals(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue(), trdProductDeal.getBusiType())){
//	    	    				prodId = deal.getProdId();
//	    	    				prodType = valBooksetInfoDto.getAcctSubjectId();
//	    	    			} else if(StringUtil.equals(DictCodeList.业务类型_货币型TA交易.getValue(), trdProductDeal.getBusiType())){
//	    	    				prodId = finprodId;
//	    	    				prodType = DictCodeListExt1.金融产品类型_通道.getValue();
//	    	    			}
                        /** modify by dongjun on 20190417 货币型TA交易账未入账修正 end**/
                        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_新增SHEET
                        bokTradeBuildForAdd(prodId, prodType, null, trdProductDeal);
                    }
                }
            }
        }

        return resultObject;

    }

    /**
     * 通道投资资产交易拆分
     *
     * @param prodId 产品代码
     * @param prodFinrodType 产品类型
     * @param trdProductDeal 金融产品交易
     * @param trdProductDealAdd 金融产品交易附属信息
     * @param trdTradeFeeDetail 金融产品交易费用明细
     * @param trdFinProductBarter 金融产品实物交易
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject chlInvestAssetSep(String prodId,String prodFinrodType,TrdProductDeal trdProductDeal,
                                          TrdProductDealAdd trdProductDealAdd,TrdTradeFeeDetail trdTradeFeeDetail,TrdFinProductBarter trdFinProductBarter){
        if (trdProductDealAdd == null) {
            trdProductDealAdd = new TrdProductDealAdd();
        }
        String prodid = prodId;
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinrodType)){
            FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(prodId);
            if(finProduct != null && finProduct.getFinprodId() != null){
                prodid = finProduct.getFinprodId();
            }
        }
        // 1.2 定义变量
        // 定义 变量.通道随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        // 定义 变量.通道未随交易支付费用
        BigDecimal feeAmtUnpay = BigDecimal.ZERO;
        // 定义 变量.费用金额
        BigDecimal feeAmt = BigDecimal.ZERO;
        // 定义 变量.金融产品交易List
        List<TrdProductDeal> listTrdProductDeal = new ArrayList<>();
        // 定义 变量.金融产品交易附属List
        List<TrdProductDealAdd> listTrdProductDealAdd = new ArrayList<>();
        // 定义 变量.金融产品交易费用明细List
        List<TrdTradeFeeDetail> listTrdTradeFeeDetail = new ArrayList<>();
        /**ADD BY WUJIE ON 20191022 START**/
        //定义 变量.金融产品实物交易List
        List<TrdFinProductBarter> listTrdFinProductBarter = new ArrayList<TrdFinProductBarter>();
        /**ADD BY WUJIE ON 20191022 END**/
        // 定义 变量.金融产品代码
        String finprodId = prodid + FinalString.LAYERING_SPLIT + trdProductDeal.getChlId();
        // 定义 变量.resultObject
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        resultObject.setResultMsg(StringUtil.EMPTY);

        if(StringUtil.isEmpty(finprodId)){
            return resultObject;
        }

        // 2.1 交易撤销时处理逻辑
        if(StringUtil.equals(DictCodeListExt1.交易状态_撤销.getValue(), trdProductDeal.getTradeStatus())){
            if (StringUtil.isNotEmpty(trdProductDeal.getpTradeId())) {
                List<TrdProductDeal> listToDel = valInterfCommonMapper.queryToDelDeal(trdProductDeal.getpTradeId(), DictCodeListExt1.交易状态_初始.getValue());
                if (CollectionUtils.isNotEmpty(listToDel)) {
                    for (TrdProductDeal deal : listToDel) {
                        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_删除SHEET
                        deal.setpTradeId(deal.getTradeId());
                        bokTradeBuildForDel(deal);
                        // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易信息_删除SHEET。
                        tradeInfoForDel(deal.getTradeId());
                    }
                }
            }
            return resultObject;
        }

        // 2.2 获取通道费用金额
        if(trdTradeFeeDetail != null){
            if(StringUtil.equals(DictCodeList.是否_是.getValue(), trdTradeFeeDetail.getIsPayWithTrade())){
                feeAmtPay = trdTradeFeeDetail.getFeeAmt();
                // 变量.费用金额 = 输入.金融产品交易费用明细.费用金额
                feeAmt = trdTradeFeeDetail.getFeeAmt();
            } else {
                // 变量.通道未随交易支付费用 = 0
                feeAmtUnpay = BigDecimal.ZERO;
            }
        }
        // 2.3 通道费用交易初始化
        if(feeAmt != null && feeAmt.compareTo(BigDecimal.ZERO) > 0){
            String feeTradeId = interfCommonService.getTradeid(DictCodeList.交易方向_付费.getValue(), trdProductDeal.getTradeDate(), "A");
            // 2.3.1 初始化通道费用交易信息
            TrdProductDeal trdProdDeal = new TrdProductDeal();
            trdProdDeal.setTradeId(feeTradeId);
            trdProdDeal.setBusiType(DictCodeList.业务类型_产品费用交易.getValue());
            trdProdDeal.setFinprodId(finprodId);
            trdProdDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道费.getValue());//modify by dongjun on 20190329
            trdProdDeal.setBranch(InterfCommonConstant.分支序号_默认);
            trdProdDeal.setTradeDate(trdProductDeal.getTradeDate());
            trdProdDeal.setSettleDate(trdProductDeal.getSettleDate());
            trdProdDeal.setCcy(trdProductDeal.getCcy());
            trdProdDeal.setTradeAmt(feeAmt);//modify by dongjun on 20190418 费用金额赋值错误
            trdProdDeal.setPs(DictCodeList.交易方向_付费.getValue());
            trdProdDeal.setTradeStatus(trdProductDeal.getTradeStatus());
            trdProdDeal.setIsCancel(DictCodeList.是否_否.getValue());
            /**modify by wujie on 20190529 start remark:代码实现与设计不一致
             trdProdDeal.setTotleTradeId(trdProductDeal.getTotleTradeId());
             **/
            trdProdDeal.setTotleTradeId(trdProductDeal.getTradeId());
            /**modify by wujie on 20190529 end**/
            trdProdDeal.setChlId(trdProductDeal.getChlId());
            trdProdDeal.setTradeMarket(trdProductDeal.getTradeMarket());

            listTrdProductDeal.add(trdProdDeal);

            //2.3.2 初始化通道费用交易附属信息
            TrdProductDealAdd trdProdDealAdd = new TrdProductDealAdd();
            trdProdDealAdd.setTradeId(feeTradeId);
            trdProdDealAdd.setPortfolioId(finprodId);
            trdProdDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

            listTrdProductDealAdd.add(trdProdDealAdd);

            // 2.3.4 初始化通道费用交易明细表
            TrdTradeFeeDetail trdTrdFeeDetail = new TrdTradeFeeDetail();
            trdTrdFeeDetail.setTradeId(feeTradeId);
            trdTrdFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            trdTrdFeeDetail.setIsPayWithTrade(DictCodeList.是否_是.getValue());
            trdTrdFeeDetail.setFeeAmt(feeAmt);
            trdTrdFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());

            listTrdTradeFeeDetail.add(trdTrdFeeDetail);
        }

        // 定义 变量.交易金额
        BigDecimal tradeAmt = trdProductDeal.getTradeAmt();
        // 定义 变量.随交易支付费用 =
        BigDecimal feeAmtPay1 = trdProductDeal.getFeeAmtPay();
        // 定义 变量.未随交易支付费用
        BigDecimal feeAmtUnpay1 = trdProductDeal.getFeeAmtUnpay();
        // 定义 变量.总交易费用 =
        BigDecimal totFeeAmt = trdProductDeal.getFeeAmt();
        // 变量.随交易支付费用 = 变量.随交易支付费用 - 变量.通道随交易支付费用
        feeAmtPay1 = ArithmeticUtil.subtract(feeAmtPay1, feeAmtPay);
        // 变量.未随交易支付费用 = 变量.未随交易支付费用 - 变量.通道未随交易支付费用
        feeAmtUnpay1 = ArithmeticUtil.subtract(feeAmtUnpay1, feeAmtUnpay);
        // 变量.总交易费用 = 变量.总交易费用 - 变量.费用金额
        totFeeAmt = ArithmeticUtil.subtract(totFeeAmt, feeAmt);
        // 定义 变量.费用处理方式Map 初始化逻辑见右边方框
        Map<String,BigDecimal> feeProMap = new HashMap<>();
        feeProMap.put(DictCodeList.交易方向_申购.getValue(), new BigDecimal(-1));
        feeProMap.put(DictCodeList.交易方向_申购确认.getValue(), new BigDecimal(-1));
        feeProMap.put(DictCodeList.交易方向_买入.getValue(), new BigDecimal(-1));
        feeProMap.put(DictCodeList.交易方向_赎回.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_赎回确认.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_卖出.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_还本到账.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_收息到账.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_到期.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_提前支取.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_行权_回售.getValue(), BigDecimal.ONE);
        feeProMap.put(DictCodeList.交易方向_行权_赎回.getValue(), BigDecimal.ONE);

        //金融产品类型：正回购
        if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(trdProductDeal.getFinprodType())
                || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(trdProductDeal.getFinprodType())){
            feeProMap.put(DictCodeList.交易方向_买入.getValue(), BigDecimal.ONE);
            feeProMap.put(DictCodeList.交易方向_卖出.getValue(), new BigDecimal(-1));
            feeProMap.put(DictCodeList.交易方向_到期.getValue(), new BigDecimal(-1));
        }

        // 变量.交易金额 = 变量.交易金额 + 变量.费用金额*变量.费用处理方式Map.get(变量.金融产品交易.交易方向)
        tradeAmt = ArithmeticUtil.doAdd(tradeAmt,ArithmeticUtil.multiply(feeAmt,feeProMap.get(trdProductDeal.getPs())));
        // 定义 变量.通道投资交易编号
        String chlInvestTradeId = interfCommonService.getTradeid(trdProductDeal.getPs(),trdProductDeal.getTradeDate(),"A");
        // 输入.金融产品交易.汇总交易编号
        trdProductDeal.setTotleTradeId(trdProductDeal.getTradeId());
        // 输入.金融产品交易.随交易支付费用
        trdProductDeal.setFeeAmtPay(feeAmtPay1);
        // 输入.金融产品交易.未随交易支付费用
        trdProductDeal.setFeeAmtUnpay(feeAmtUnpay1);
        // 输入.金融产品交易.总交易费用
        trdProductDeal.setFeeAmt(totFeeAmt);
        // 输入.金融产品交易.交易金额
        trdProductDeal.setTradeAmt(tradeAmt);
        // 输入.金融产品交易.交易编号
        trdProductDeal.setTradeId(chlInvestTradeId);
        // 变量.金融产品交易List.add(输入.金融产品交易)
        listTrdProductDeal.add(trdProductDeal);
        // 输入.金融产品交易附属信息.交易编号
        trdProductDealAdd.setTradeId(chlInvestTradeId);
        // 输入.金融产品交易附属信息.投资组合代码 =
        trdProductDealAdd.setPortfolioId(finprodId);
        // 输入.金融产品交易附属信息.生成方式 =
        trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());
        // 变量.金融产品交易附属List.add(输入.金融产品交易附属信息)
        listTrdProductDealAdd.add(trdProductDealAdd);
        
        /**ADD BY WUJIE ON 20191022 START**/
        if(trdFinProductBarter != null && StringUtil.isNotEmpty(trdFinProductBarter.getTradeId())){
        	trdFinProductBarter.setTradeId(chlInvestTradeId);
        	listTrdFinProductBarter.add(trdFinProductBarter);
        }
        /**ADD BY WUJIE ON 20191022 END**/

        // 2.5 拆分交易入账
        if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
            for(TrdProductDeal item : listTrdProductDeal){
                resultObject = bokTradeBuild(prodId, prodFinrodType, null, item);
                if(!StringUtil.equals(resultObject.getResultCode(), ResultCode.SUCCESS.getCode())){
                    return resultObject;
                }
            }
        }

        // 2.6 拆分交易入账入库
        // 变量.金融产品交易List
        if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
            listTrdProductDeal.forEach(i -> trdProductDealMapper.insert(i));
        }
        // 变量.金融产品交易附属List
        if(CollectionUtils.isNotEmpty(listTrdProductDealAdd)){
            listTrdProductDealAdd.forEach(i -> trdProductDealAddMapper.insert(i));
        }
        // 变量.金融产品交易费用明细List
        if(CollectionUtils.isNotEmpty(listTrdTradeFeeDetail)){
            listTrdTradeFeeDetail.forEach(i -> trdTradeFeeDetailMapper.insert(i));
        }
        /**ADD BY WUJIE ON 20191022 START**/
        // 变量.金融产品实物交易List
        if(CollectionUtils.isNotEmpty(listTrdFinProductBarter)){
        	listTrdFinProductBarter.forEach(i -> trdFinProductBarterMapper.insert(i));
        }
        /**ADD BY WUJIE ON 20191022 END**/


        return resultObject;
    }
    /**
     * 产品借通道投资交易_拆分
     *
     * @param deal 通道交易
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private Map<String,Object> getChannelProdDealSep(ChannelProdDeal deal){
        Map<String,Object> resultMap = new HashMap<>();

        // 1.3 定义变量
        // 定义 变量.金融产品代码
        String finprodId = deal.getProdId() + FinalString.LAYERING_SPLIT + deal.getChlId();
        // 定义 变量.通道投资交易List
        List<TrdProductDeal> listChlDeal = new ArrayList<>();
        // 定义 变量.通道投资交易附属表List
        List<TrdProductDealAdd> listChlDealAdd = new ArrayList<>();
        // 定义 变量.通道投资交易费用明细List List<TrdTradeFeeDetail>
        List<TrdTradeFeeDetail> listTrdTradeFeeDetail = new ArrayList<>();
        // 定义 变量.通道产品交易方向
        String ps = null;
        // 定义 变量.理财产品投资交易方向
        String investPs = null;
        // 定义 变量.通道产品交易编号
        String tradeId = null;
        // 定义 变量.理财产品投资交易编号
        String investTradeId = null;
        // 定义 变量.金融产品交易费用明细 = NULL TrdTradeFeeDetail
        TrdTradeFeeDetail trdTradeFeeDetail = null;
        // 定义 变量.通道费用金额 = 0
        BigDecimal chlFeeAmt = BigDecimal.ZERO;

        // 2.处理详情
        // 2.1 交易方向、交易编号处理、通道费用金额
        if(StringUtil.equals(DictCodeList.交易方向_申购.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品申购.getValue();
            investPs = DictCodeList.交易方向_申购确认.getValue();
        } else if(StringUtil.equals(DictCodeList.交易方向_买入.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品申购.getValue();
            investPs = DictCodeList.交易方向_买入.getValue();

            /**add by wujie on 20190529 start**/
            //金融产品类型：正回购
            if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(deal.getFinprodType())){
                ps = DictCodeList.交易方向_产品赎回.getValue();
                investPs = DictCodeList.交易方向_卖出.getValue();
            }
            /**add by wujie on 20190529 end**/

        } else if(StringUtil.equals(DictCodeList.交易方向_赎回.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品赎回.getValue();
            investPs = DictCodeList.交易方向_赎回确认.getValue();
        } else if(StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品赎回.getValue();
            investPs = DictCodeList.交易方向_卖出.getValue();

            /**add by wujie on 20190529 start**/
            //金融产品类型：正回购
            if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(deal.getFinprodType())){
                ps = DictCodeList.交易方向_产品申购.getValue();
                investPs = DictCodeList.交易方向_买入.getValue();
            }
            /**add by wujie on 20190529 end**/

        }
        /**add by wujie on 20190528 start remark：增加通道划款、通道回款的支持**/
        //1.通道划款
        else if(StringUtil.equals(DictCodeList.交易方向_通道划款.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品申购.getValue();
            investPs = DictCodeList.交易方向_买入.getValue();
        }
        //2.通道回款
        else if(StringUtil.equals(DictCodeList.交易方向_通道划款.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品赎回.getValue();
            investPs = DictCodeList.交易方向_卖出.getValue();
        }
        /**add by wujie on 20190528 end**/

        else if(StringUtil.equals(DictCodeList.交易方向_收息到账.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_还本到账.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_赎回到账.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_到期.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_还本.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_收息.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_转出.getValue(), deal.getPs())){
            ps = DictCodeList.交易方向_产品赎回.getValue();
            investPs = DictCodeList.交易方向_卖出.getValue();

            /**add by wujie on 20190529 start**/
            //金融产品类型：正回购
            if(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(deal.getFinprodType())
                    || DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(deal.getFinprodType())){
                ps = DictCodeList.交易方向_产品申购.getValue();
                investPs = DictCodeList.交易方向_买入.getValue();
            }
            /**add by wujie on 20190529 end**/

        } else if(StringUtil.equals(DictCodeList.交易方向_认购确认.getValue(), deal.getPs())
                || StringUtil.equals(DictCodeList.交易方向_转入.getValue(), deal.getPs())) {
            ps = DictCodeList.交易方向_产品申购.getValue();
            investPs = DictCodeList.交易方向_买入.getValue();
        } else {
            ps = DictCodeList.交易方向_产品赎回.getValue();
            investPs = DictCodeList.交易方向_卖出.getValue();
        }

//	    	modify by wujie on 20190529
//	    	/** moidfy by dongjun on 20190527 质押式正回购交易方向特殊处理  start**/
//	    	if(StringUtil.equals(deal.getFinprodType(), DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())
//	    			&& StringUtil.equals(deal.getPs(), DictCodeList.交易方向_买入.getValue())){
//	    		ps = DictCodeList.交易方向_产品赎回.getValue();
//	    		investPs = DictCodeList.交易方向_卖出.getValue();
//	    	}
//	    	/** moidfy by dongjun on 20190527 质押式正回购交易方向特殊处理  end**/

        tradeId = interfCommonService.getTradeid(ps, deal.getTradeDate(), "A");
        investTradeId = interfCommonService.getTradeid(investPs, deal.getTradeDate(), "A");

        // IF 输入.理财产品借通道投资交易.通道费用信息 IS NOT NULL
        if (deal.getChannelFeeInfo() != null) {
            // IF 数据字典：是否.否 eq 输入.理财产品借通道投资交易.通道费用信息.是否随交易支付
            if (DictCodeList.是否_否.getValue().equals(deal.getChannelFeeInfo().getIsPayWithTrade())) {
                // 变量.通道费用金额 = 输入.理财产品借通道投资交易.通道费用信息.费用金额
                chlFeeAmt = deal.getChannelFeeInfo().getFeeAmt();
            }
        }

        // 2.2 初始化金融产品表
        // 定义 变量.通道产品交易Dto
        TrdProductDeal chlDeal = new TrdProductDeal();
        // 定义 变量.理财产品投资交易Dto
        TrdProductDeal chlInvestDeal = new TrdProductDeal();

        chlDeal.setTradeId(tradeId);
        chlDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
        chlDeal.setFinprodId(finprodId);
        chlDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
        chlDeal.setBranch(InterfCommonConstant.分支序号_默认);
        chlDeal.setTradeDate(deal.getTradeDate());
        chlDeal.setSettleDate(deal.getSettleDate());
        chlDeal.setCcy(deal.getCcy());
        chlDeal.setShareAmt(deal.getShareAmt());
        chlDeal.setPrinAmt(deal.getShareAmt());
        chlDeal.setTradeAmt(deal.getTradeAmt());
        chlDeal.setPs(ps);
        chlDeal.setTradeStatus(deal.getTradeStatus());
        chlDeal.setIsCancel(DictCodeList.是否_否.getValue());
        chlDeal.setTotleTradeId(deal.getTradeId());//modify by dongjun on 20190527 产品借通道投资交易_拆分汇总交易编号错误
        chlDeal.setChlId(deal.getChlId());
        chlDeal.setTradeMarket(deal.getTradeMarket());

        chlDeal.setFeeAmtUnpay(chlFeeAmt);
        chlDeal.setFeeAmt(chlFeeAmt);

        chlInvestDeal.setTradeId(investTradeId);
        chlInvestDeal.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
        chlInvestDeal.setFinprodId(finprodId);
        chlInvestDeal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
        chlInvestDeal.setBranch(InterfCommonConstant.分支序号_默认);
        chlInvestDeal.setTradeDate(deal.getTradeDate());
        chlInvestDeal.setSettleDate(deal.getSettleDate());
        chlInvestDeal.setCcy(deal.getCcy());
        chlInvestDeal.setShareAmt(deal.getShareAmt());
        chlInvestDeal.setPrinAmt(deal.getShareAmt());
        chlInvestDeal.setTradeAmt(deal.getTradeAmt());
        chlInvestDeal.setPs(investPs);
        chlInvestDeal.setTradeStatus(deal.getTradeStatus());
        chlInvestDeal.setIsCancel(DictCodeList.是否_否.getValue());
        chlInvestDeal.setTotleTradeId(deal.getTradeId());//modify by dongjun on 20190527 产品借通道投资交易_拆分汇总交易编号错误
//	    	chlInvestDeal.setChlId(deal.getChlId());
        chlInvestDeal.setTradeMarket(deal.getTradeMarket());

        // 2.3 初始化金融产品交易附属表
        // 定义 变量.通道产品交易附属表Dto
        TrdProductDealAdd chlDealAdd = new TrdProductDealAdd();
        // 定义 变量.理财产品投资交易附属表Dto
        TrdProductDealAdd chlInvestDealAdd = new TrdProductDealAdd();

        chlDealAdd.setTradeId(tradeId);
        chlDealAdd.setPortfolioId(finprodId);
        chlDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

        chlInvestDealAdd.setTradeId(investTradeId);
        chlInvestDealAdd.setPortfolioId(deal.getProdId());
        chlInvestDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

        // 2.4 初始化通道费用信息明细
        // IF 变量.通道费用金额 > 0
        if (chlFeeAmt.compareTo(BigDecimal.ZERO) > 0) {
            trdTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 变量.通道产品交易编号
            trdTradeFeeDetail.setTradeId(tradeId);
            // 一次性费用类型 数据字典：一次性费用类型.通道费
            trdTradeFeeDetail.setFeeType(DictCodeListExt1.一次性费用类型_通道费.getValue());
            // 是否随交易支付 数据字典：是否.否
            trdTradeFeeDetail.setIsPayWithTrade(DictCodeList.是否_否.getValue());
            // 费用金额 变量.通道费用金额
            trdTradeFeeDetail.setFeeAmt(chlFeeAmt);
            // 费用代码 数据字典：一次性费用类型.通道费
            trdTradeFeeDetail.setFeeId(DictCodeListExt1.一次性费用类型_通道费.getValue());
        }

        // 3. 初始化交易拆分保存至resultMap对象中,并返回
        if(StringUtil.equals("01", deal.getDealSepFlag())){
            listChlDeal.add(chlDeal);

            // IF 变量.金融产品交易费用明细 IS NOT NULL
            if (trdTradeFeeDetail != null) {
                // 变量.通道投资交易费用明细List.add(变量.金融产品交易费用明细)
                listTrdTradeFeeDetail.add(trdTradeFeeDetail);
            }

            listChlDealAdd.add(chlDealAdd);
        } else if(StringUtil.equals("02", deal.getDealSepFlag())){
            listChlDeal.add(chlInvestDeal);
            listChlDealAdd.add(chlInvestDealAdd);
        } else {
            listChlDeal.add(chlDeal);

            // IF 变量.金融产品交易费用明细 IS NOT NULL
            if (trdTradeFeeDetail != null) {
                // 变量.通道投资交易费用明细List.add(变量.金融产品交易费用明细)
                listTrdTradeFeeDetail.add(trdTradeFeeDetail);
            }

            listChlDealAdd.add(chlDealAdd);
            listChlDeal.add(chlInvestDeal);
            listChlDealAdd.add(chlInvestDealAdd);
        }

        resultMap.put("TRD_PRODUCT_DEAL", listChlDeal);
        resultMap.put("TRD_PRODUCT_DEAL_ADD", listChlDealAdd);

        return resultMap;
    }

    /**
     * 产品端交易拆分
     *
     * @param trdProductDeal 产品端交易
     * @param trdTradeFeeDetailList 交易费用明细集合
     * @param prodId 产品代码
     * @param channelId 通道代码
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    /**
     * 产品端交易拆分
     * @param trdProductDeal 产品端交易
     * @param trdTradeFeeDetailList 交易费用明细集合
     * @param prodId 产品代码
     * @param channelId 通道代码
     * @param prodType 金融产品类型
     * @param branch 分支序号
     * @param dealSepFlag 交易拆分标志
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject prdDealSep(TrdProductDeal trdProductDeal,List<TrdTradeFeeDetail> trdTradeFeeDetailList,
                                   String prodId,String channelId,String prodType,Long branch,String dealSepFlag ){
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.处理详情
        // 2.1 交易撤销处理
        if(StringUtil.equals(DictCodeListExt1.交易状态_撤销.getValue(), trdProductDeal.getTradeStatus())){
            List<TrdProductDeal> listTrdProductDeal = valInterfCommonMapper.queryToDelDeal(trdProductDeal.getpTradeId(), DictCodeListExt1.交易状态_初始.getValue());
            if(CollectionUtils.isNotEmpty(listTrdProductDeal)){
                listTrdProductDeal.forEach(i -> {
                    // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易入账_删除SHEET
                    TrdProductDeal trdProductDealInfo = new TrdProductDeal();
                    trdProductDealInfo.setpTradeId(i.getTradeId());
                    bokTradeBuildForDel(trdProductDealInfo);

                    // 调用共通设计->【详细设计-投管接口共通方法设计.xlsx】->交易信息_删除SHEET
                    tradeInfoForDel(i.getTradeId());
                });
            }

            return resultObject;
        }

        // 2.2 交易新增处理
        /**add by wujie on 20190610 start**/
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodType)){
            FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(prodId);
            if(finProduct != null && finProduct.getFinprodId() != null){
                prodId = finProduct.getFinprodId();
                prodType = finProduct.getFinprodType();
            }
        }
        /**add by wujie on 20190610 end**/
        if(StringUtil.equals(DictCodeList.业务类型_产品费用交易.getValue(), trdProductDeal.getBusiType())){
            resultObject = prdAssetFeeSep(trdProductDeal, trdTradeFeeDetailList, prodId, channelId,prodType,branch,dealSepFlag);
        } else if(StringUtil.equals(DictCodeList.业务类型_活期存款交易.getValue(), trdProductDeal.getBusiType())){
            resultObject = prdCurDepDealSep(trdProductDeal, prodId, channelId,prodType,branch,dealSepFlag);
        } else if(StringUtil.equals(DictCodeList.业务类型_增值税交易.getValue(), trdProductDeal.getBusiType())){
            resultObject = prdTaxDealSep(trdProductDeal, trdTradeFeeDetailList, prodId, channelId,prodType,branch,dealSepFlag);
        }

        return resultObject;
    }

    /**
     * 产品端投资端费用拆分
     *
     * @param trdProductDeal 产品通道费用交易
     * @param trdTradeFeeDetailList 交易费用明细集合
     * @param prodId 产品代码
     * @param channelId 通道代码
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private ResultObject prdAssetFeeSep(TrdProductDeal trdProductDeal,List<TrdTradeFeeDetail> trdTradeFeeDetailList,
                                       String prodId,String channelId,String prodType,Long branch,String dealSepFlag){
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.处理详情
        if(StringUtil.equals(DictCodeListExt1.交易状态_初始.getValue(), trdProductDeal.getTradeStatus())){

            String totleTradeId = trdProductDeal.getTradeId();

            // 定义 变量.金融产品交易List
            List<TrdProductDeal> listTrdProductDeal = new ArrayList<>();
            // 定义 变量.金融产品交易附属List
            List<TrdProductDealAdd> listTrdProductDealAdd = new ArrayList<>();
            // 定义 变量.金融产品交易费用明细List
            List<TrdTradeFeeDetail> listTrdTradeFeeDetail = new ArrayList<>();
            // 定义 变量.金融产品代码
            String finprodId = prodId + FinalString.LAYERING_SPLIT + channelId;

            // 2.1 初始化交易信息
            // 定义 变量.通道产品_通道费用支付
            TrdProductDeal chlTrdProductDeal = trdProductDeal;
            // 定义 变量.交易编号
            String tradeId = interfCommonService.getTradeid(trdProductDeal.getPs(),trdProductDeal.getTradeDate(),"A");
            // 变量.通道产品_通道费用支付.通道代码
            chlTrdProductDeal.setChlId(channelId);
            // 变量.通道产品_通道费用支付.汇总交易编号
            chlTrdProductDeal.setTotleTradeId(totleTradeId);
            // 变量.通道产品_通道费用支付.交易编号
            chlTrdProductDeal.setTradeId(tradeId);
            // 变量.通道产品_通道费用支付.金融产品代码
            chlTrdProductDeal.setFinprodId(finprodId);
            // 变量.通道产品_通道费用支付.金融产品类型
            /**modify by wujie on 20190610 start
             chlTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
             **/
            chlTrdProductDeal.setFinprodType(chlTrdProductDeal.getFinprodType2());
            /**modify by wujie on 20190610 end**/

            // 定义 变量.通道产品_通道费用支付附属
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(tradeId);
            trdProductDealAdd.setPortfolioId(finprodId);
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

            listTrdProductDeal.add(chlTrdProductDeal);
            listTrdProductDealAdd.add(trdProductDealAdd);

            trdTradeFeeDetailList.forEach(i -> {
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                trdTradeFeeDetail.setTradeId(tradeId);
                trdTradeFeeDetail.setFeeType(i.getFeeType());
                trdTradeFeeDetail.setIsPayWithTrade(i.getIsPayWithTrade());
                trdTradeFeeDetail.setFeeAmt(i.getFeeAmt());
                trdTradeFeeDetail.setFeeId(i.getFeeId());
                listTrdTradeFeeDetail.add(trdTradeFeeDetail);
            });

            if(StringUtil.equals(dealSepFlag, "00")){
                // 2.1.2 初始化通道产品申购交易
                /** modify by dongjun on 20190329 start**/
                TrdProductDeal purTrdProductDeal = new TrdProductDeal();
                purTrdProductDeal.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_产品申购.getValue(),trdProductDeal.getTradeDate(),"A"));//modify by dongjun on 20190329
                purTrdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
                purTrdProductDeal.setFinprodId(finprodId);
                purTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
                purTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
                purTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
                purTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
                purTrdProductDeal.setCcy(trdProductDeal.getCcy());
                purTrdProductDeal.setShareAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setPrinAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setPs(DictCodeList.交易方向_产品申购.getValue());
                purTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
                purTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                purTrdProductDeal.setTotleTradeId(totleTradeId);
                purTrdProductDeal.setChlId(channelId);

                // 定义 变量.金融产品交易附加_通道申购
                TrdProductDealAdd chlTrdProductDealAdd = new TrdProductDealAdd();
                chlTrdProductDealAdd.setTradeId(purTrdProductDeal.getTradeId());
                chlTrdProductDealAdd.setPortfolioId(finprodId);
                chlTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

                listTrdProductDeal.add(purTrdProductDeal);
                listTrdProductDealAdd.add(chlTrdProductDealAdd);
                // 2.1.3 初始化理财产品买计划类资产(通道虚拟资产)
                TrdProductDeal confirmTrdProductDeal = new TrdProductDeal();
                confirmTrdProductDeal.setTradeId(interfCommonService.getTradeid(DictCodeList.交易方向_申购确认.getValue(),trdProductDeal.getTradeDate(),"A"));//modify by dongjun on 20190329
                confirmTrdProductDeal.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
                confirmTrdProductDeal.setFinprodId(finprodId);
                confirmTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
                confirmTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
                confirmTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
                confirmTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
                confirmTrdProductDeal.setCcy(trdProductDeal.getCcy());
                confirmTrdProductDeal.setShareAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setPrinAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setPs(DictCodeList.交易方向_申购确认.getValue());
                confirmTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
                confirmTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                confirmTrdProductDeal.setTotleTradeId(totleTradeId);

                TrdProductDealAdd confirmTrdProductDealAdd = new TrdProductDealAdd();
                confirmTrdProductDealAdd.setTradeId(confirmTrdProductDeal.getTradeId());
                confirmTrdProductDealAdd.setPortfolioId(prodId);
                confirmTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

                listTrdProductDeal.add(confirmTrdProductDeal);
                listTrdProductDealAdd.add(confirmTrdProductDealAdd);
            }
            /** modify by dongjun on 20190329 end**/

            // 2.2 交易数据入库
            dealCommonService.insertBatchTrdProductDeal(listTrdProductDeal);

            for(TrdProductDealAdd i : listTrdProductDealAdd){
                trdProductDealAddMapper.insert(i);
            }
            for(TrdTradeFeeDetail i : listTrdTradeFeeDetail){
                trdTradeFeeDetailMapper.insert(i);
            }
            // 2.3 交易入账
            for(TrdProductDeal i : listTrdProductDeal){
                ResultObject rstObject = bokTradeBuild(prodId, prodType, branch, i);
                if(!StringUtil.equals(ResultCode.SUCCESS.getCode(), rstObject.getResultCode())){
                    return resultObject;
                }
            }
        }
        return resultObject;
    }

    /**
     * 产品端活期存款交易拆分
     * @param trdProductDeal 产品活期存款交易
     * @param prodId 产品代码
     * @param channelId 通道代码
     * @param prodType 产品类型
     * @param branch 分支序号
     * @param dealSepFlag 拆分标志（全部拆分\部分拆分）
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private ResultObject prdCurDepDealSep(TrdProductDeal trdProductDeal,String prodId,String channelId,String prodType,
                                         Long branch,String dealSepFlag){
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.处理详情
        if(StringUtil.equals(DictCodeListExt1.交易状态_初始.getValue(), trdProductDeal.getTradeStatus())){
            // 定义 变量.金融产品交易List
            List<TrdProductDeal> listTrdProductDeal = new ArrayList<>();
            // 定义 变量.金融产品交易附属List
            List<TrdProductDealAdd> listTrdProductDealAdd = new ArrayList<>();
            // 定义 变量.金融产品代码
            String finprodId = prodId + FinalString.LAYERING_SPLIT + channelId;

            // 2.1 初始化交易信息
            // 初始化通道产品活期存款交易相关信息

            String totleTradeId = trdProductDeal.getTradeId();

            // 定义 变量.通道产品活期存款交易 = 输入.产品活期存款交易
            TrdProductDeal chlTrdProductDeal = trdProductDeal;
            // 定义 变量.交易编号
            String tradeId = interfCommonService.getTradeid(trdProductDeal.getPs(),trdProductDeal.getTradeDate(),"A");
            // 变量.通道产品增值税交易.通道代码
            chlTrdProductDeal.setChlId(channelId);
            // 变量.通道产品增值税交易.汇总交易编号
            chlTrdProductDeal.setTotleTradeId(totleTradeId);
            // 变量.通道产品增值税交易.交易编号
            chlTrdProductDeal.setTradeId(tradeId);

            // 定义 变量.通道产品活期存款交易附属信息
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(tradeId);
            trdProductDealAdd.setPortfolioId(finprodId);
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

            listTrdProductDeal.add(chlTrdProductDeal);
            listTrdProductDealAdd.add(trdProductDealAdd);
            //TODO 确认下这个参数的意义
            if(StringUtil.equals(dealSepFlag, "00")){
                /** modify by dongjun on 20190329 start **/
                // 2.1.2 初始化通道产品赎回交易相关信息
                // 变量 交易方向
                String ps = null;
                // 变量 理财产品买资产_交易方向
                String prodPs = null;
                if(StringUtil.equals(DictCodeList.交易方向_收息.getValue(), trdProductDeal.getPs())
                        ||StringUtil.equals(DictCodeList.交易方向_卖出.getValue(), trdProductDeal.getPs())){
                    ps = DictCodeList.交易方向_产品赎回.getValue();
                    prodPs = DictCodeList.交易方向_赎回确认.getValue();
                } else{
                    ps = DictCodeList.交易方向_产品申购.getValue();
                    prodPs = DictCodeList.交易方向_申购确认.getValue();
                }
                TrdProductDeal purTrdProductDeal = new TrdProductDeal();
                purTrdProductDeal.setTradeId(interfCommonService.getTradeid(ps,trdProductDeal.getTradeDate(),"A"));
                purTrdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
                purTrdProductDeal.setFinprodId(finprodId);
                purTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
                purTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
                purTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
                purTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
                purTrdProductDeal.setCcy(trdProductDeal.getCcy());
                purTrdProductDeal.setShareAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setPrinAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setPs(ps);
                purTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
                purTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                purTrdProductDeal.setTotleTradeId(totleTradeId);
                purTrdProductDeal.setChlId(channelId);

                // 定义 变量.金融产品交易附加_通道申购
                TrdProductDealAdd chlTrdProductDealAdd = new TrdProductDealAdd();
                chlTrdProductDealAdd.setTradeId(purTrdProductDeal.getTradeId());
                chlTrdProductDealAdd.setPortfolioId(finprodId);
                chlTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

                listTrdProductDeal.add(purTrdProductDeal);
                listTrdProductDealAdd.add(chlTrdProductDealAdd);

                // 2.1.3 初始化理财产品申赎计划类资产(通道虚拟资产)
                TrdProductDeal confirmTrdProductDeal = new TrdProductDeal();
                confirmTrdProductDeal.setTradeId(interfCommonService.getTradeid(prodPs,trdProductDeal.getTradeDate(),"A"));
                confirmTrdProductDeal.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
                confirmTrdProductDeal.setFinprodId(finprodId);
                confirmTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
                confirmTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
                confirmTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
                confirmTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
                confirmTrdProductDeal.setCcy(trdProductDeal.getCcy());
                confirmTrdProductDeal.setShareAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setPrinAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setPs(prodPs);
                confirmTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
                confirmTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                confirmTrdProductDeal.setTotleTradeId(totleTradeId);
//	    		confirmTrdProductDeal.setChlId(channelId);

                // 定义 变量.金融产品交易附加_通道申购
                TrdProductDealAdd confirmTrdProductDealAdd = new TrdProductDealAdd();
                confirmTrdProductDealAdd.setTradeId(confirmTrdProductDeal.getTradeId());
                confirmTrdProductDealAdd.setPortfolioId(prodId);
                confirmTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

                listTrdProductDeal.add(confirmTrdProductDeal);
                listTrdProductDealAdd.add(confirmTrdProductDealAdd);
                /** modify by dongjun on 20190329 end **/

            }

            // 2.2 交易数据入库
            dealCommonService.insertBatchTrdProductDeal(listTrdProductDeal);
            listTrdProductDealAdd.forEach(i -> trdProductDealAddMapper.insert(i));

            // 2.3 交易入账
            for(TrdProductDeal i : listTrdProductDeal){
                ResultObject rstObject = bokTradeBuild(prodId, prodType, branch, i);
                if(!StringUtil.equals(ResultCode.SUCCESS.getCode(), rstObject.getResultCode())){
                    return resultObject;
                }
            }
        }
        return resultObject;
    }

    /**
     * 产品端增值税交易拆分
     *
     * @param trdProductDeal 产品增值税交易
     * @param trdTradeFeeDetailList 交易费用明细集合
     * @param prodId 产品代码
     * @param channelId 通道代码
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    private ResultObject prdTaxDealSep(TrdProductDeal trdProductDeal,List<TrdTradeFeeDetail> trdTradeFeeDetailList,
                                      String prodId,String channelId,String prodType,Long branch,String dealSepFlag){
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        // 2.处理详情
        if(StringUtil.equals(DictCodeListExt1.交易状态_初始.getValue(), trdProductDeal.getTradeStatus())){
            // 定义 变量.金融产品交易List
            List<TrdProductDeal> listTrdProductDeal = new ArrayList<>();
            // 定义 变量.金融产品交易附属List
            List<TrdProductDealAdd> listTrdProductDealAdd = new ArrayList<>();
            // 定义 变量.金融产品交易费用明细List
            List<TrdTradeFeeDetail> listTrdTradeFeeDetail = new ArrayList<>();
            // 定义 变量.金融产品代码
            String finprodId = prodId + FinalString.LAYERING_SPLIT + channelId;

            // 2.1 初始化交易信息
            // 初始化通道产品增值税交易相关信息
            String totleTradeId = trdProductDeal.getTradeId();

            // 定义 变量.通道产品增值税交易
            TrdProductDeal chlTrdProductDeal = trdProductDeal;
            // 定义 变量.交易编号
            String tradeId = interfCommonService.getTradeid(trdProductDeal.getPs(),trdProductDeal.getTradeDate(),"A");
            // 变量.通道产品增值税交易.通道代码
            chlTrdProductDeal.setChlId(channelId);
            // 变量.通道产品增值税交易.汇总交易编号
            chlTrdProductDeal.setTotleTradeId(totleTradeId);
            // 变量.通道产品增值税交易.交易编号
            chlTrdProductDeal.setTradeId(tradeId);
            // 变量.通道产品增值税交易.金融产品代码
            chlTrdProductDeal.setFinprodId(InterfHelper.getMd5CashId_(finprodId,DictCodeListExt1.金融产品类型_通道.getValue(),DictCodeListExt1.现金流二级类型_税额.getValue()));
            // 变量.通道产品_通道费用支付.金融产品类型
            chlTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
            // 定义 变量.通道产品_通道费用支付附属
            TrdProductDealAdd trdProductDealAdd = new TrdProductDealAdd();
            trdProductDealAdd.setTradeId(tradeId);
            trdProductDealAdd.setPortfolioId(finprodId);
            trdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

            listTrdProductDeal.add(chlTrdProductDeal);
            listTrdProductDealAdd.add(trdProductDealAdd);

            trdTradeFeeDetailList.forEach(i -> {
                TrdTradeFeeDetail trdTradeFeeDetail = new TrdTradeFeeDetail();
                trdTradeFeeDetail.setTradeId(tradeId);
                trdTradeFeeDetail.setFeeType(i.getFeeType());
                trdTradeFeeDetail.setIsPayWithTrade(i.getIsPayWithTrade());
                trdTradeFeeDetail.setFeeAmt(i.getFeeAmt());
                trdTradeFeeDetail.setFeeId(i.getFeeId());
                listTrdTradeFeeDetail.add(trdTradeFeeDetail);
            });

            if(StringUtil.equals(dealSepFlag, "00")){
                /** modify by dongjun on 20190329 start**/
                // 2.1.2 初始化通道产品申赎交易相关信息
                String ps = null;
                // 变量 理财产品卖资产_交易方向
                String prodPs = null;
                if(StringUtil.equals(DictCodeList.交易方向_税费退税.getValue(), trdProductDeal.getPs())){
                    ps = DictCodeList.交易方向_产品赎回.getValue();
                    prodPs = DictCodeList.交易方向_赎回确认.getValue();
                } else{
                    ps = DictCodeList.交易方向_产品申购.getValue();
                    prodPs = DictCodeList.交易方向_赎回确认.getValue();
                }
                TrdProductDeal purTrdProductDeal = new TrdProductDeal();
                purTrdProductDeal.setTradeId(interfCommonService.getTradeid(ps,trdProductDeal.getTradeDate(),"A"));
                purTrdProductDeal.setBusiType(DictCodeList.业务类型_货币型TA交易.getValue());
                purTrdProductDeal.setFinprodId(finprodId);
                purTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_通道.getValue());
                purTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
                purTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
                purTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
                purTrdProductDeal.setCcy(trdProductDeal.getCcy());
                purTrdProductDeal.setShareAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setPrinAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
                purTrdProductDeal.setPs(ps);
                purTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
                purTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                purTrdProductDeal.setTotleTradeId(totleTradeId);
                purTrdProductDeal.setChlId(channelId);

                // 定义 变量.金融产品交易附加_通道申购
                TrdProductDealAdd chlTrdProductDealAdd = new TrdProductDealAdd();
                chlTrdProductDealAdd.setTradeId(purTrdProductDeal.getTradeId());
                chlTrdProductDealAdd.setPortfolioId(finprodId);
                chlTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

                listTrdProductDeal.add(purTrdProductDeal);
                listTrdProductDealAdd.add(chlTrdProductDealAdd);

                // 2.1.3 初始化理财产品申赎计划类资产(通道虚拟资产)
                TrdProductDeal confirmTrdProductDeal = new TrdProductDeal();
                confirmTrdProductDeal.setTradeId(interfCommonService.getTradeid(prodPs,trdProductDeal.getTradeDate(),"A"));
                confirmTrdProductDeal.setBusiType(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue());
                confirmTrdProductDeal.setFinprodId(finprodId);
                confirmTrdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
                confirmTrdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
                confirmTrdProductDeal.setTradeDate(trdProductDeal.getTradeDate());
                confirmTrdProductDeal.setSettleDate(trdProductDeal.getSettleDate());
                confirmTrdProductDeal.setCcy(trdProductDeal.getCcy());
                confirmTrdProductDeal.setShareAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setPrinAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setTradeAmt(trdProductDeal.getTradeAmt());
                confirmTrdProductDeal.setPs(prodPs);
                confirmTrdProductDeal.setTradeStatus(trdProductDeal.getTradeStatus());
                confirmTrdProductDeal.setIsCancel(DictCodeList.是否_否.getValue());
                confirmTrdProductDeal.setTotleTradeId(totleTradeId);
//	    		confirmTrdProductDeal.setChlId(channelId);

                // 定义 变量.金融产品交易附加_通道申购
                TrdProductDealAdd confirmTrdProductDealAdd = new TrdProductDealAdd();
                confirmTrdProductDealAdd.setTradeId(confirmTrdProductDeal.getTradeId());
                confirmTrdProductDealAdd.setPortfolioId(prodId);
                confirmTrdProductDealAdd.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

                listTrdProductDeal.add(confirmTrdProductDeal);
                listTrdProductDealAdd.add(confirmTrdProductDealAdd);
                /** modify by dongjun on 20190329 end**/
            }

            // 2.2 交易数据入库
            dealCommonService.insertBatchTrdProductDeal(listTrdProductDeal);
            listTrdProductDealAdd.forEach(i -> trdProductDealAddMapper.insert(i));
            listTrdTradeFeeDetail.forEach(i -> trdTradeFeeDetailMapper.insert(i));

            // 2.3 交易入账
            for(TrdProductDeal i : listTrdProductDeal){
                ResultObject rstObject = bokTradeBuild(prodId, prodType, branch, i);
                if(!StringUtil.equals(ResultCode.SUCCESS.getCode(), rstObject.getResultCode())){
                    return resultObject;
                }
            }
        }
        return resultObject;
    }



    /**
     * 核算到期日调整
     * @param prodId   产品代码
     * @param prodType 产品类型
     * @param branch 分支号
     * @param mDate 到期日
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject updateProdMdate(String prodId,String prodType,Long branch,Date mDate) {
        if(branch == null) {
            branch = new Long(0);
        }
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        //定义 变量.账套信息
        BokBookset valBokBookset = new BokBookset();
        //定义 变量.子账套信息
        List<BokBooksetRe> valBokBooksetReList = new ArrayList<BokBooksetRe>();

        //2.处理详情
        //2.1 查询产品作为核算主体时的账套信息
        if(!StringUtil.equals(DictCodeListExt1.金融产品类型_理财子产品.getValue(), prodType)) {
            valBokBookset = valInterfCommonMapper.queryBooksetAsAcct(prodId,prodType);
        }
        //2.2 查询子账套信息
        if(valBokBookset != null) {

            valBokBooksetReList =  valInterfCommonMapper.queryBokBooksetReList(valBokBookset.getBooksetId());
        }

        //2.3 更新到期日
        //2.3.1 更新账套结束日
        Map<String,Object> params = new HashMap<>();
        params.put("mDate", mDate);
        if(valBokBookset != null) {
            params.put("booksetId", valBokBookset.getBooksetId());
            valInterfCommonMapper.updateBooksetAsAcct(params);
        }

        //2.3.2 更新产品对应核算分层信息到期日
        params.put("prodId", prodId);
        params.put("prodType", prodType);
        params.put("branch", branch);
        valInterfCommonMapper.updateAcctLayeringInfo(params);
        //2.3.3 更新子账套对应核算分层信息到期日
        if(valBokBooksetReList != null && valBokBooksetReList.size() > 0) {
            Map<String,Object> params2 = new HashMap<String,Object>();
            params2.put("mDate", mDate);
            for (BokBooksetRe bokBooksetRe : valBokBooksetReList) {
                params2.put("booksetIdSon", bokBooksetRe.getBooksetIdSon());
                valInterfCommonMapper.updateAcctLayeringInfo(params2);
            }
        }

        return resultObject;
    }
    /**
     * 查询投资组合对应的产品信息
     * @param prodid
     * @return 返回金融产品DTO
     */
    private FinProduct queryFinProductByPortfolidId(String prodid) {
        return dealCommonService.queryFinProductByPortfolidId(prodid);
    }


    /**
     * 按规则生成回购期限品种
     * @param finprodType 金融产品类型
     * @param termDay 期限天数
     * @return  返回共通ResultObject；具体内容参见ResultObject的API
     */
    public ResultObject getRepTermType(String finprodType, long termDay) {
        ResultObject resultObject = new ResultObject();
        String repTerm = interfCommonService.getRepTermType(finprodType, termDay);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(ResultMapKey.QUERYREPTERMTYPE,repTerm);
        resultObject.setResultCode(ResultCode.SUCCESS.getCode());
        resultObject.setResultMap(map);
        return resultObject;
    }

    /**
     * 投管-几许接口
     * @param apiTrdCommDto
     * @return 返回共通ResultObject；具体内容参见ResultObject的API
     */
	public ResultObject runValInvest2Jixu(ApiTrdCommDto apiTrdCommDto) {
		return valInvest2JixuService.sendInfo(apiTrdCommDto);
	}
	
	/**
	 * 查询缴纳附加税相关附加税类型
	 * @param portfolioId 组合代码
	 * @param prodType 组合类型
	 * @param queryDate 查询日期
	 * @return
	 * @see
	 * @since 1.0
	 */
    public List<String> getAddTaxType(List<String> listProdId, String prodType, Date queryDate,Date queryDateBegin,Date queryDateEnd){
        // 查询附加税税则中的附加税类型
        //OPEX-650【产品缴纳】缴纳查询界面，选择纳税日期查询条件进行查询，报数据接口异常 guyunfei 20191227 start
        return interfCommonService.getAddTaxType(listProdId, prodType, queryDate,queryDateBegin,queryDateEnd);
        //OPEX-650【产品缴纳】缴纳查询界面，选择纳税日期查询条件进行查询，报数据接口异常 guyunfei 20191227 end
    }
}
