package com.joyintech.tams.valuation.valapi.service;

import com.jixu.joyin2jixu.service.Joyin2JixuInterface;
import com.jixu.msg.dto.JJBkCashAcctDto;
import com.jixu.msg.dto.JJPortfolioDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dao.ValInterfCommonMapper;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.dealcommon.DealCommonService;
import com.joyintech.tams.valuation.common.interf.val.dao.BokTradeBuildMapper;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import com.joyintech.tams.valuation.valapi.constant.ValApiConstant;
import com.joyintech.tams.valuation.valapi.dto.BokTradeBuild001Dto;
import com.joyintech.tams.valuation.valapi.dto.BokTradeBuildPtl001Dto;
import com.joyintech.tams.valuation.valapi.dto.DurationTrade001Dto;
import com.joyintech.tams.valuation.valapi.dto.ProdAcctInit001Dto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName ValInterfCommonApiService
 * @Description: 对外调用估值apiservice
 * @Author yanya
 * @Date 2021/2/3
 * @Version V1.0
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ValInterfCommonApiService {


    /**
     * 接口共通
     */
    @Autowired
    private InterfCommonService interfCommonService;

    @Autowired
    private FinProductMapper finProductMapper;

    @Autowired
    private ValFinProductAcctBatchMapper valFinProductAcctBatchMapper;

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
     * 接口共通类Mapper
     */
    @Autowired
    private ValInterfCommonMapper valInterfCommonMapper;

    /**
     * 交易入账
     */
    @Autowired
    private BokTradeBuildMapper bokTradeBuildMapper;

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
     * 金融产品交易费用明细Mapper
     */
    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    @Autowired
    private FinProductAddMapper finProductAddMapper;

    @Autowired
    private DealCommonService dealCommonService;

    @Autowired
    private BookAccountCommonService bookAccountCommonService;
    /**
     * 几许接口
     */
    @Autowired
    private Joyin2JixuInterface joyin2JixuInterface;

    /**
     * 根据产品代码、产品类型生成分层代码
     * @param finprodId
     * @param finprodType
     * @param vdate
     * @return
     */
    public String generateLayeringId(String finprodId, String finprodType, Date vdate) {
        String layeringId = finprodId;
        // IF 数据字典：金融产品类型.理财子产品 eq 输入.金融产品类型
        if (DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(finprodType)) {
            // 定义 变量.分层代码 += new SimpleDateFormat("yyyyMMdd").format(输入.起息日)
            layeringId += DateUtil.format(vdate, DateUtil.DEFAULT_OTHER_FORMAT);
        }
        // 变量.分层代码 += FinalString.LAYERING_SPLIT + 输入.金融产品类型
        layeringId += FinalString.LAYERING_SPLIT + finprodType;
        return layeringId;
    }

    /**
     * 根据产品代码、通道代码生成核算主体代码
     * @param prodId 产品代码
     * @param chlId 通道代码
     * @return
     */
    public String generateAcctSubjectId(String prodId, String chlId) {
        //定义核算主体代码
        String acctSubjectId = null;
        //通道代码不为空
        if(StringUtil.isNotEmpty(chlId)) {
            //核算主体代码  = 产品代码 + _ + 通道代码
            acctSubjectId = prodId + FinalString.LAYERING_SPLIT + chlId;
        }
        else {
            //核算主体代码  = 产品代码
            acctSubjectId = prodId;
        }
        return acctSubjectId;
    }
    /**
     * 构建产品信息
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param finprodAbbr 金融产品简称
     * @param finprodName 金融产品名称
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param mdate 到期日
     * @param ccy 币种
     * @param calendarId 交易日历
     * @param actmdate 实际到期日
     * @return
     */
    public FinProduct buildFinProduct(String finprodId,String finprodType,String finprodAbbr,String finprodName,String profitType,
                                Date vdate,Date mdate,String ccy,String calendarId,Date actmdate) {
        // 定义 变量.标的类金融产品信息Dto FinProduct
        FinProduct finProduct = new FinProduct();
        // 变量.标的类金融产品信息Dto 赋值
        // 输入.金融产品代码
        finProduct.setFinprodId(finprodId);
        // 输入.金融产品类型
        finProduct.setFinprodType(finprodType);
        // 输入.金融产品简称
        finProduct.setFinprodAbbr(finprodAbbr);
        // 输入.金融产品名称
        finProduct.setFinprodName(finprodName);
        // 输入.收益类型
        finProduct.setProfitType(profitType);
        // 输入.起息日
        finProduct.setVdate(vdate);
        // 输入.到期日
        finProduct.setMdate(mdate);
        // 输入.币种
        finProduct.setCcy(ccy);
        // 输入.交易日历
        finProduct.setCalendarId(calendarId);
        // 输入.实际到期日,如果为空，用到期日进行赋值
        finProduct.setActmdate(actmdate == null ? mdate : actmdate);
        return finProduct;
    }

    /**
     * 投建账套其他参数信息
     * @param valuationTemplate 估值表模板
     * @param tmp_booksetId 模板
     * @param sysParams 系统参数
     * @return
     */
    public OtherParamsDto buildOtherParamsDto(String valuationTemplate, String tmp_booksetId, Map<String, String> sysParams) {
        // 定义 变量.其他参数值Dto
        OtherParamsDto otherParamsDto = new OtherParamsDto();
        // 变量.其他参数值Dto 赋值
        // 输入.产品标的Dto.估值表模板
        otherParamsDto.setValuationTemplate(valuationTemplate);
        // 模板账套ID 默认为空
        otherParamsDto.setTmp_booksetId(tmp_booksetId);
        //系统参数
        otherParamsDto.setSysParams(sysParams);
        return otherParamsDto;
    }

    /**
     * 构建估值侧标的类金融产品扩展信息表
     * @param tgIsCalc 托管活期利息计提
     * @param isLayerAccount 是否分层核算
     * @param portfolioId 投资组合代码
     * @param isAccount 是否核算
     * @return
     */
    public ValFinProductExt buildValFinProductExt(String tgIsCalc,String isLayerAccount,String portfolioId,String isAccount) {
        // 定义 变量.标的类金融产品扩展信息表Dto FinProductExt
        ValFinProductExt valFinProductExt = new ValFinProductExt();
        // 变量.标的类金融产品扩展信息表Dto 赋值
        // 变量.托管活期利息计提
        valFinProductExt.setIsCalc(tgIsCalc);
        // 输入.是否分层核算
        valFinProductExt.setIsLayerAccount(isLayerAccount);
        // 输入.投资组合代码
        valFinProductExt.setPortfolioId(portfolioId);
        // 输入.是否核算
        valFinProductExt.setIsAccount(isAccount);
        return valFinProductExt;
    }

    /**
     * 构建几许资组合
     * @param booksetId 账套代码
     * @param standardCcy 币种
     * @param booksetIdTmpl 模板
     * @param startDate 开始日期
     * @param booksetFullname 账套全称
     * @param booksetName 账套名称
     * @param calendarId 交易日历
     * @return
     */
    public JJPortfolioDto buildJJPortfolioDto(String booksetId, String standardCcy, String booksetIdTmpl, Date startDate,
                                              String booksetFullname, String booksetName, String calendarId) {
        //2.2.1 几许产品信息初始化
        JJPortfolioDto jJPortfolio = new JJPortfolioDto();
        //账套
        jJPortfolio.setBookSet_id(booksetId);
        //币种
        jJPortfolio.setStandard_ccy(standardCcy);
        //账套模板
        jJPortfolio.setBookset_id_tmpl(booksetIdTmpl);
        //账套创建日期
        jJPortfolio.setStart_date(startDate);
        //账套全名
        jJPortfolio.setBookset_fullname(booksetFullname);
        //账套名
        jJPortfolio.setBookset_name(booksetName);
        //账套节假日配置
        jJPortfolio.setCalendar_id(calendarId);
        return jJPortfolio;
    }

    /**
     * 构建分层产品信息
     * @param finProductId 金融产品代码
     * @param finprodType 金融产品类型
     * @param finprodName 金融产品名称
     * @param pFinprodId 母金融产品代码
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param mdate 到期日
     * @param portfolioId 投资组合代码
     * @param layeringId 分层代码
     * @param calendarId 交易日历
     * @return
     */
    public LayerPrdInfo buildLayerPrdInfo(String finProductId, String finprodType, String finprodName,String pFinprodId, String profitType,
                                          Date vdate, Date mdate, String portfolioId, String layeringId, String calendarId) {
        LayerPrdInfo layerPrdInfo = new LayerPrdInfo();
        // 变量.分层产品Dto 赋值
        // 金融产品代码 变量.金融产品代码
        layerPrdInfo.setFinprodId(finProductId);;
        // 金融产品类型 输入.产品标的Dto.金融产品类型
        layerPrdInfo.setFinprodType(finprodType);
        // 金融产品名称 输入.产品标的Dto.金融产品名称
        layerPrdInfo.setFinprodName(pFinprodId);
        // 母金融产品代码 输入.产品标的Dto.母金融产品代码
        layerPrdInfo.setpFinprodId(pFinprodId);
        // 收益类型 输入.产品标的Dto.收益类型
        layerPrdInfo.setProfitType(profitType);
        // 起息日 输入.产品标的Dto.起息日
        layerPrdInfo.setVdate(vdate);
        // 到期日 输入.产品标的Dto.到期日
        layerPrdInfo.setMdate(mdate);
        // 投资组合代码 输入.产品标的Dto.投资组合代码
        layerPrdInfo.setPortfolioId(portfolioId);
        // 分层代码 变量.分层代码
        layerPrdInfo.setLayeringId(layeringId);
        // 交易日历 输入.产品标的Dto.交易日历
        layerPrdInfo.setCalendarId(calendarId);
        return layerPrdInfo;
    }

    /**
     * 构建金融产品账户表
     * @param finprodId 产品代码
     * @param finprodType 产品类型
     * @param acctNo 账户号
     * @param ccy 币种
     * @param acctType 账户类型
     * @return
     */
    public StlFinProductAcct buildStlFinProductAcct(String finprodId, String finprodType, String acctNo, String ccy, String acctType) {
        // 定义 变量.清算款账户dto StlFinProductAcct
        StlFinProductAcct stlFinProductAcct = new StlFinProductAcct();
        // 变量.清算款账户dto 赋值
        // 金融产品代码 L.金融产品代码
        stlFinProductAcct.setFinprodId(finprodId);
        // 金融产品类型 L.金融产品类型
        stlFinProductAcct.setFinprodType(finprodType);
        // 账号 InterfCommonConstant.清算款账户号
        stlFinProductAcct.setAcctNo(acctNo);
        // 币种 L.币种
        stlFinProductAcct.setCcy(ccy);
        // 账户类型
        stlFinProductAcct.setAcctType(acctType);
        return stlFinProductAcct;
    }

    /**
     * 构建几许银行账户
     * @param acctNo 账户号
     * @param booksetId 账套代码
     * @param ccy 币种
     * @param acctType 账户类型
     * @return
     */
    public JJBkCashAcctDto buildJJBkCashAcctDto(String acctNo, String booksetId, String ccy, String acctType) {
        JJBkCashAcctDto jJBkCashAcct = new JJBkCashAcctDto();
        //银行账户代码
        jJBkCashAcct.setAcct_no(acctNo);
        //账套代码
        jJBkCashAcct.setBookset_id(booksetId);
        //币种
        jJBkCashAcct.setCcy(ccy);
        //账户类型  jixu接口需要  add by qmh 20190812 start
        jJBkCashAcct.setAcc_type(acctType);
        StlCapacctInfo StlCapacctInfo = valFinProductAcctBatchMapper.getStlCapacctInfo(acctNo, ccy);
        jJBkCashAcct.setAcct_name(StlCapacctInfo.getAcctName());
        return jJBkCashAcct;
    }

    /**
     * 根据字段值映射账套参数信息
     * @param qtMap 账套参数
     * @param valDecDigits 单位净值/万份收益小数位
     * @param valDecType 单位净值/万份收益尾数处理方式
     * @param yeildDecDigits 利率小数位
     * @param yeildDecType 利率处理方式
     */
    public void initBookParamByField(Map<String,String> qtMap ,String valDecDigits, String valDecType, String yeildDecDigits, String yeildDecType) {
        //单位净值/万份收益小数位
        qtMap.put(ValApiConstant.产品费前估值小数位数, valDecDigits);
        qtMap.put(ValApiConstant.产品费后估值小数位数, valDecDigits);
        //单位净值/万份收益尾数处理方式
        qtMap.put(ValApiConstant.产品费前估值小数尾数处理方式, valDecType);
        qtMap.put(ValApiConstant.产品费后估值小数尾数处理方式, valDecType);
        //利率小数位
        qtMap.put(ValApiConstant.本日年化收益率小数位数, yeildDecDigits);
        //利率处理方式
        qtMap.put(ValApiConstant.七日年化收益率小数尾数, yeildDecType);
    }

    /**
     * 根据已赋值好的账套参数值赋给账套
     * @param listBokBooksetExt 账套参数List
     * @param qtMap 账套参数值map
     */
    public void initBookParamsByMap(List<BokBooksetExt> listBokBooksetExt, Map<String,String> qtMap) {
        for (BokBooksetExt item : listBokBooksetExt) {
            // IF 变量.账套参数Map.get(L.参数项) != null
            if (StringUtil.isNotEmpty(qtMap.get(item.getParamCode()))) {
                // L.参数值 = 变量.账套参数Map.get(L.参数项)
                item.setParamValue(qtMap.get(item.getParamCode()));
            }
        }
    }

    /**
     * 产品初始化账套 入库操作
     * @param bokBookset 账套
     * @param bokBooksetDate 账套日期
     * @param listBokBooksetExt 账套参数
     * @param bokAcctSubjectInfo 核算信息
     * @param listEnterBokAcctLayeringInfo 核算分层信息
     * @param listBokDetailId 账务明细代码
     * @param listFinCashInfo 现金流信息
     * @param listBokBooksetPlan 估值方案
     * @param jJPortfolio 几许产品信息
     */
    public void prodOtherInfoDataOpt(BokBookset bokBookset, BokBooksetDate bokBooksetDate, List<BokBooksetExt> listBokBooksetExt,
                                     BokAcctSubjectInfo bokAcctSubjectInfo,List<BokAcctLayeringInfo> listEnterBokAcctLayeringInfo,
                                     List<BokDetailId> listBokDetailId, List<FinCashInfo> listFinCashInfo, List<BokBooksetPlan> listBokBooksetPlan,
                                     JJPortfolioDto jJPortfolio) {
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
            /**ADD BY WUJIE ON 20191008 START**/
            listBokDetailId = interfCommonService.toRepeatBokDetailId(listBokDetailId);
            /**ADD BY WUJIE ON 20191008 END**/
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
        /**ADD BY WUJIE ON 20190726 START**/
        if(jJPortfolio != null){
            List<JJPortfolioDto> list = new ArrayList<JJPortfolioDto>();
            list.add(jJPortfolio);
            joyin2JixuInterface.createPortfolios(list);
        }
    }

    /**
     * 根据产品ID获取产品信息
     * @param finprodId
     * @return
     */
    public FinProduct getFinProductById(String finprodId) {
        return finProductMapper.selectByPrimaryKey(finprodId);
    }

    /**
     * 根据分层信息及账套信息构建要操作的账套列表
     * @param bokBookset 账套信息
     * @param list 分层获取的账套列表
     * @param listValBooksetInfoDto 总的账套列表
     */
    public void getValBooksetInfoList(BokBookset bokBookset, List<ValBooksetInfoDto> list, List<ValBooksetInfoDto> listValBooksetInfoDto) {
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

    /**
     * 根据账套构建账套信息DTO
     * @param bokBookset
     * @return
     */
    public ValBooksetInfoDto getValBooksetInfoDto(BokBookset bokBookset) {
        // 定义 变量.产品对应账套Dto ValBooksetInfoDto
        ValBooksetInfoDto valBooksetInfoDto = new ValBooksetInfoDto();
        // 变量.产品对应账套Dto.账套代码 = 变量.入库账套DTO.账套代码
        valBooksetInfoDto.setBooksetId(bokBookset.getBooksetId());
        return valBooksetInfoDto;
    }

    /**
     * 账户数据及账套列表构建账套账户数据
     * @param item 账户数据
     * @param listValBooksetInfoDto 账套列表
     * @param listBokBooksetCapacctInfo 账套清算账户表
     * @param listJJBkCashAcct 几许账套账户表
     * @param listBokDetailId 账务明细代码
     */
    public void prodAcctInit(ProdAcctInit001Dto item, List<ValBooksetInfoDto> listValBooksetInfoDto,
                             List<BokBooksetCapacctInfo> listBokBooksetCapacctInfo, List<JJBkCashAcctDto> listJJBkCashAcct,
                             List<BokDetailId> listBokDetailId) {
        // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套清算账户初始化SHEET
        List<BokBooksetCapacctInfo> listInfo = interfCommonService.booksetCapacctInfoInit(
                buildStlFinProductAcct(null, null, item.getAcctNo(), item.getCcy(), item.getAcctType()),
                listValBooksetInfoDto);
        // IF DTO IS NOT NULL AND DTO.size()>0
        if (CollectionUtils.isNotEmpty(listInfo)) {
            // 变量.入库账套清算账户List.addAll(DTO)
            listBokBooksetCapacctInfo.addAll(listInfo);
        }
        // IF 数据字段：资金账户类型.托管户 eq L.账户类型
        if (DictCodeListExt1.资金账户类型_产品托管户.getValue().equals(item.getAcctType())) {
            // 定义 变量.清算款账户dto StlFinProductAcct
            StlFinProductAcct stlFinProductAcct = buildStlFinProductAcct(item.getFinprodId(), item.getFinprodType(),
                    InterfCommonConstant.清算款账户号+item.getCcy(), item.getCcy(), DictCodeListExt1.资金账户类型_待清算款.getValue());
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
            JJBkCashAcctDto jJBkCashAcct = buildJJBkCashAcctDto(item.getAcctNo(), itemBok.getBooksetId(),
                    item.getCcy(), item.getAcctType());
            //  jixu接口需要  add by qmh 20190812 end
            listJJBkCashAcct.add(jJBkCashAcct);
        }
        /**ADD BY WUJIE ON 20190726 END**/
        /**modify by wujie on 20190809 remark:取消判断条件
         // IF 数据字典：是否.是 eq 输入.产品标的Dto.账套参数Map.get("DEPOSIT_ACCOUT")
         if (DictCodeList.是否_是.getValue().equals(prodInfoDto.getMapBooksetParam().get("DEPOSIT_ACCOUT"))) {
         **/
        /** add by lby on 20201226 四级科目尾号集合 start**/
        //定义 变量.四级科目尾号集合
        Map<String,BokDetailId> bokDetailIdMap= new HashMap<>();
        /** add by lby on 20201226 四级科目尾号集合 end**/
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
            //定义 变量.bokDetailId = 变量.四级科目尾号集合.get(BOK.账套代码+变量.账务明细代码)
            BokDetailId bokDetailIdTail = bokDetailIdMap.get(itemBok.getBooksetId() + bokDetailId.getBokDetailId());
            //IF 变量.bokDetailId IS NULL
            if (!StringUtil.isNotEmpty(bokDetailIdTail)){
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                //变量.bokDetailId = 返回值
                bokDetailIdTail = interfCommonService.settingDetailSubjectNoTaiL(item.getAcctNo(), item.getAcctType(), null, null, itemBok.getBooksetId());
            }
            //变量.四级科目尾号集合.put(BOK.账套代码+变量.账务明细代码,变量.bokDetailId)
            bokDetailIdMap.put(itemBok.getBooksetId() + bokDetailId.getBokDetailId(),bokDetailIdTail);
            //变量.账务明细代码表Dto.四级科目尾号 = 变量.bokDetailId.四级科目尾号
            bokDetailId.setDetailSubjectNoTail(bokDetailIdTail.getDetailSubjectNoTail());
            //变量.账务明细代码表Dto.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
            bokDetailId.setDetailSubnameTail(bokDetailIdTail.getDetailSubnameTail());
            // 变量.入库账务明细代码List.add(变量.账务明细代码Dto)
            listBokDetailId.add(bokDetailId);
        }
        /**modify by wujie on 20190809 remark:取消判断条件
         }
         **/
    }

    /**
     * 账户相关信息入库
     * @param listBokBooksetCapacctInfo 清算款账户
     * @param listJJBkCashAcct 几许资金账户
     * @param listBokDetailId 账务明细代码
     */
    public void prodAcctInfoDataOpt(List<BokBooksetCapacctInfo> listBokBooksetCapacctInfo, List<JJBkCashAcctDto> listJJBkCashAcct,
                                    List<BokDetailId> listBokDetailId) {
        // 变量.入库账务明细代码List 账务明细代码表 BOK_DETAIL_ID 以账套代码、账务明细代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listBokDetailId)) {
            /**ADD BY WUJIE ON 20191008 START**/
            listBokDetailId = interfCommonService.toRepeatBokDetailId(listBokDetailId);
            /**ADD BY WUJIE ON 20191008 END**/
            valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
            valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
        }
        // 变量.入库账套清算账户List 账套清算账户表 BOK_BOOKSET_CAPACCT_INFO 以账套代码进行删除，然后再入库
        if (CollectionUtils.isNotEmpty(listBokBooksetCapacctInfo)) {
            valBooksetCapacctInfoBatchMapper.deleteBatch(listBokBooksetCapacctInfo);
            valBooksetCapacctInfoBatchMapper.insertBatch(listBokBooksetCapacctInfo);
        }
        //几许资金账户入库
        if (CollectionUtils.isNotEmpty(listJJBkCashAcct)) {
            joyin2JixuInterface.updateBkCashAccts(listJJBkCashAcct);
        }
    }

    /**
     * 根据产品代码、产品类型返回真正的产品代码、产品类型（因为组合需要处理）
     * @param prodId 产品代码
     * @param prodFinprodType 产品类型
     * @return
     */
    public Map<String, String> getRealFinprodInfo(String prodId, String prodFinprodType) {
        Map<String, String> realProdMap = new HashMap<>();
        //如果为投资组合，则产品代码是根据组合进行查询出来
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)){
            //产品数据
            FinProduct finProduct = dealCommonService.queryFinProductByPortfolidId(prodId);
            if(finProduct != null && finProduct.getFinprodId() != null){
                prodId = finProduct.getFinprodId();
                prodFinprodType = finProduct.getFinprodType();
            }
        }
        realProdMap.put(ValApiConstant.产品代码字段, prodId);
        realProdMap.put(ValApiConstant.产品类型字段, prodFinprodType);
        return realProdMap;
    }

    /**
     * 判断交易是否入账
     * @param busiType 业务类型
     * @param ps 交易方向
     * @return
     */
    public boolean judgeIsTradeDealAcccount(String busiType, String ps) {
        boolean isTradeDeal = true;
        if(DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue().equals(busiType)
                && (
                        DictCodeList.交易方向_申购.getValue().equals(ps)
                    || DictCodeList.交易方向_赎回到账.getValue().equals(ps)
                    || DictCodeListExt2.交易方向_认购.getValue().equals(ps)
                )
        ){
            isTradeDeal = false;
        }
        if(DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue().equals(busiType)
                && (
                        DictCodeList.交易方向_申购.getValue().equals(ps)
                    || DictCodeList.交易方向_赎回到账.getValue().equals(ps)
                    || DictCodeListExt2.交易方向_认购.getValue().equals(ps)
                )
        ){
            isTradeDeal = false;
        }
        else if(DictCodeList.业务类型_指令交易.getValue().equals(busiType)
                &&(
                        DictCodeListExt1.交易方向_保证金划款.getValue().equals(ps)
                    || DictCodeListExt1.交易方向_保证金回款.getValue().equals(ps)
                )
        ){
            isTradeDeal = false;
        }
        return isTradeDeal;
    }

    /**
     * 根据传递的组合数据，生成组合交易数据
     * @param ptlTradeList 组合数据，
     * @return
     */
    public List<PtlTrade> generatePtlTradeList(List<BokTradeBuildPtl001Dto> ptlTradeList) {
        List<PtlTrade> listPtlTrade = new ArrayList<>();
        if(CollectionUtils.isEmpty(ptlTradeList)){
            ptlTradeList.stream().forEach(item -> {
                PtlTrade ptlTrade = new PtlTrade();
                ptlTrade.setPortfolioId(item.getPortfolioId());
                listPtlTrade.add(ptlTrade);
            });
        }
        return listPtlTrade;
    }

    /**
     * 如果为投资组合且满足指定条件，获取额外的账套信息
     * @param listValBooksetInfoDto 返回的账套信息
     * @param prodId 产品代码
     * @param prodFinprodType 产品类型
     * @param chlId 通道代码
     * @param busiType 业务类型
     * @param listPtlTrade 组合交易
     */
    public void bokTradeBuildAdditionalBookset(List<ValBooksetInfoDto> listValBooksetInfoDto, String prodId,
                                               String prodFinprodType, String chlId, String busiType, List<PtlTrade> listPtlTrade) {
        //IF 数据字典：金融产品类型.投资组合 eq 输入.产品类型 and 输入.交易信息Dto.通道代码  IS NULL
        //and 数据字典：业务类型.净值型产品TA交易 ne 输入.交易信息Dto.业务类型
        //and 数据字典：业务类型.货币型产品TA交易 ne 输入.交易信息Dto.业务类型
        //and 数据字典：业务类型.预期型产品TA交易 ne 输入.交易信息Dto.业务类型
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)
                && StringUtil.isEmpty(chlId)
                && !DictCodeList.业务类型_净值型TA交易.getValue().equals(busiType)
                && !DictCodeList.业务类型_货币型TA交易.getValue().equals(busiType)
                && !DictCodeList.业务类型_预期收益型TA交易.getValue().equals(busiType)
                && !DictCodeList.业务类型_产品费用交易.getValue().equals(busiType)){
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(子组合)查询 SHEET
            List<ValBooksetInfoDto>  valBooksetInfoDtoList= interfCommonService.getSonPtlPortfolo(prodId, listPtlTrade);
            if(valBooksetInfoDtoList != null && valBooksetInfoDtoList.size() > 0){
                //变量.账套信息List.addAll(DTO)
                listValBooksetInfoDto.addAll(valBooksetInfoDtoList);
            }
        }
    }

    /**
     * 剔除重复的账套数据
     * @param listValBooksetInfoDto 账套数据
     */
    public void clearRepeatBookset(List<ValBooksetInfoDto> listValBooksetInfoDto) {
        /**ADD BY WUJIE ON 20191118 START**/
        Map<String,ValBooksetInfoDto> qcMap = new HashMap<String,ValBooksetInfoDto>();
        List<ValBooksetInfoDto> bokSetList = new ArrayList<ValBooksetInfoDto>();
        for(ValBooksetInfoDto dto : listValBooksetInfoDto){
            if(qcMap.get(dto.getBooksetId()) == null){
                bokSetList.add(dto);
            }
            qcMap.put(dto.getBooksetId(), dto);
        }
        listValBooksetInfoDto.clear();
        listValBooksetInfoDto.addAll(bokSetList);
        /**ADD BY WUJIE ON 20191118 END**/
    }

    /**
     * 根据传递的数据构建交易信息
     * @param tradeId 交易编码
     * @param finprodId 金融产品代码
     * @param finprodType 金融产品类型
     * @param busiType 业务类型
     * @param fpriceAmt 全价总额
     * @param chlId 通道代码
     * @param invAim 投资目的
     * @param ps 交易方向
     * @param tradeDate 交易日
     * @param settleDate 交割日期
     * @param ccy 币种
     * @param tradeAmt 交易金额
     * @param tradeMarket 交易场所
     * @param tradeMarketMode 交易市场
     * @param tradeStatus 交易状态
     * @param psPayment 收付方向
     * @param pTradeId 原交易编码
     * @param finprodType2 投管分类
     * @param branch 分支序号
     * @param vdate 起息日
     * @param mdate 到期日
     * @param cpriceAmt 净价总额
     * @param shareAmt 交易份额
     * @param prinAmt 交易本金
     * @param intAmt 利息总额
     * @param status 交易状态
     * @param isCancel 是否已撤销
     * @param totleTradeId 汇总交易编号
     * @param counterId 交易对手
     * @param trader 交易员
     * @param cashId 现金流代码
     * @param tradePlat 交易平台
     * @param secManageAcctId 证券管理户代码
     * @return
     */
    public TrdProductDeal buildTrdProductDeal(String tradeId, String finprodId, String finprodType, String busiType, BigDecimal fpriceAmt,
                                              String chlId, String invAim, String ps, Date tradeDate, Date settleDate, String ccy,
                                              BigDecimal tradeAmt, String tradeMarket, String tradeMarketMode, String tradeStatus,
                                              String psPayment, String pTradeId, String finprodType2, Long branch, Date vdate, Date mdate,
                                              BigDecimal cpriceAmt, BigDecimal shareAmt, BigDecimal prinAmt, BigDecimal intAmt,
                                              String status, String isCancel, String totleTradeId, String counterId, String trader,
                                              String cashId, String tradePlat, String secManageAcctId) {
        TrdProductDeal trdProductDeal = new TrdProductDeal();
        /**交易编号**/
        trdProductDeal.setTradeId(tradeId);
        /**金融产品代码**/
        trdProductDeal.setFinprodId(finprodId);
        /**金融产品类型**/
        trdProductDeal.setFinprodType(finprodType);
        /**业务类型**/
        trdProductDeal.setBusiType(busiType);
        /**全价总额**/
        trdProductDeal.setFpriceAmt(fpriceAmt);
        /**通道代码**/
        trdProductDeal.setChlId(chlId);
        /**投资目的**/
        trdProductDeal.setInvAim(invAim);
        /**交易方向**/
        trdProductDeal.setPs(ps);
        /**交易日**/
        trdProductDeal.setTradeDate(tradeDate);
        /**交割日**/
        trdProductDeal.setSettleDate(settleDate);
        /**币种**/
        trdProductDeal.setCcy(ccy);
        /**交易金额**/
        trdProductDeal.setTradeAmt(tradeAmt);
        /**交易场所**/
        trdProductDeal.setTradeMarket(tradeMarket);
        /**交易市场**/
        trdProductDeal.setTradeMarketMode(tradeMarketMode);
        /**交易状态**/
        trdProductDeal.setTradeStatus(tradeStatus);
        /**收付方向**/
        trdProductDeal.setPsPayment(psPayment);
        /**原交易编号**/
        trdProductDeal.setpTradeId(pTradeId);
        //金融产品类型（投管分类）
        trdProductDeal.setFinprodType2(finprodType2);
        //分支序号
        trdProductDeal.setBranch(branch);
        //起息日
        trdProductDeal.setVdate(vdate);
        //到期日
        trdProductDeal.setMdate(mdate);
        //净价总额
        trdProductDeal.setCpriceAmt(cpriceAmt);
        //交易份额
        trdProductDeal.setShareAmt(shareAmt);
        //交易本金
        trdProductDeal.setPrinAmt(prinAmt);
        //利息总额
        trdProductDeal.setIntAmt(intAmt);
        //交易状态
        trdProductDeal.setStatus(status);
        //是否已撤销
        trdProductDeal.setIsCancel(isCancel);
        //汇总交易编号
        trdProductDeal.setTotleTradeId(totleTradeId);
        //交易对手
        trdProductDeal.setCounterId(counterId);
        //交易员
        trdProductDeal.setTrader(trader);
        //现金流代码
        trdProductDeal.setCashId(cashId);
        return trdProductDeal;
    }

    /**
     * 构建指令信息
     * @param settleAmt 结算金额
     * @param settleDate 结算日期
     * @param myAcctNo 本方资金账户
     * @param counterAcctNo 对方资金账户
     * @param orderId 指令编号
     * @param busiType 业务类型
     * @param orderType 指令类型
     * @param orderNum 指令序号
     * @param tradeId 交易编号
     * @param ps 收付方向
     * @param ccy 币种
     * @param settleStatus 结算状态
     * @param orderStatus 指令状态
     * @param pOrderId 原指令编号
     * @param fOrderId 关联系统指令编号
     * @param channelId 通道代码
     * @return
     */
    public StlOrders buildStlOrders(BigDecimal settleAmt, Date settleDate, String myAcctNo, String counterAcctNo,
                                    String orderId,String busiType,String orderType,Long orderNum,
                                    String tradeId,String ps,String ccy,String settleStatus,String orderStatus,
                                    String pOrderId,String fOrderId,String channelId) {
        StlOrders stlOrders = new StlOrders();
        //指令结算金额
        stlOrders.setSettleAmt(settleAmt);
        //指令结算日期
        stlOrders.setSettleDate(settleDate);
        //指令本方资金账户
        stlOrders.setMyAcctNo(myAcctNo);
        //指令对方资金账户
        stlOrders.setCounterAcctNo(counterAcctNo);
        /**指令编号**/
        stlOrders.setOrderId(orderId);
        /**业务类型**/
        stlOrders.setBusiType(busiType);
        /**指令类型**/
        stlOrders.setOrderType(orderType);
        /**指令序号**/
        stlOrders.setOrderNum(orderNum);
        /**交易编号**/
        stlOrders.setTradeId(tradeId);
        /**收付方向**/
        stlOrders.setPs(ps);
        /**币种**/
        stlOrders.setCcy(ccy);
        /**结算状态**/
        stlOrders.setSettleStatus(settleStatus);
        /**指令状态**/
        stlOrders.setOrderStatus(orderStatus);
        /**原指令编号**/
        stlOrders.setpOrderId(pOrderId);
        /**关联系统指令编号**/
        stlOrders.setfOrderId(fOrderId);
        /**通道代码**/
        stlOrders.setChannelId(channelId);
        return stlOrders;
    }

    /**
     * 根据数据构建金融产品实物数据
     * @param finprodId
     * @param finprodType
     * @param branch
     * @return
     */
    public TrdFinProductBarter buildTrdFinProductBarter(String finprodId, String finprodType, Long branch) {
        TrdFinProductBarter barter = null;
        if(StringUtil.isNotEmpty(finprodId)) {
            barter = new TrdFinProductBarter();
            //实物金融产品代码
            barter.setFinprodId(finprodId);
            //实物金融产品类型
            barter.setFinprodType(finprodType);
            //实物分支序号
            barter.setBranch(branch);
        }
        return barter;
    }

    /**
     * 根据转换资产代码生成账务明细数据
     * @param list 账务明细数据
     * @param rFinprodId 转换资产代码
     * @param booksetMap 账套map数据
     */
    public void generateRBokDetailIdList(List<BokDetailId> list, String rFinprodId, Map<String,String> booksetMap) {
        /** add by yanya on 20200113 增加[金融产品交易附属信息DTO.转换资产代码]对应的账务明细代码生成 start**/
        //IF 输入.金融产品交易附属信息DTO IS NOT NULL AND 输入.金融产品交易附属信息DTO.转换资产代码 IS NOT NULL
        if(StringUtil.isNotEmpty(rFinprodId)) {
            //LOOP 变量.账套代码Map.keySet() 账套代码
            for(String booksetIdKey : booksetMap.keySet()) {
                //定义 变量.标的信息 = 	InterfCommonService.queryTargetFinProduct(输入.金融产品交易附属信息DTO.转换资产代码)
                TargetFinProduct targetInfo = interfCommonService.queryTargetFinProduct(rFinprodId);
                //IF 变量.标的信息  IS NOT NULL
                if(targetInfo != null) {
                    //定义 变量.账务明细代码DTO
                    BokDetailId trdProductDealAddBokDetailId = new BokDetailId();
                    //变量.账务明细代码DTO.业务明细代码 = 	变量.标的信息.金融产品代码
                    trdProductDealAddBokDetailId.setBusiId(targetInfo.getFinprodId());
                    //变量.账务明细代码DTO.业务明细类型 = 	变量.标的信息.金融产品类型
                    trdProductDealAddBokDetailId.setBusiDetailType(targetInfo.getFinprodType());
                    //变量.账务明细代码DTO.四级科目尾号 = 	变量.标的信息.金融产品代码
                    trdProductDealAddBokDetailId.setDetailSubjectNoTail(targetInfo.getFinprodId());
                    //变量.账务明细代码DTO.四级科目尾号名称 = 变量.标的信息.金融产品名称
                    trdProductDealAddBokDetailId.setDetailSubnameTail(targetInfo.getFinprodName());
                    //变量.账务明细代码DTO.账务明细代码 = bookAccountCommonService.genMd5BokDetailId(变量.账务明细代码DTO)
                    trdProductDealAddBokDetailId.setBokDetailId(bookAccountCommonService.genMd5BokDetailId(trdProductDealAddBokDetailId));
                    //变量.入库账务明细代码List.add(变量.账务明细代码DTO)
                    list.add(trdProductDealAddBokDetailId);
                }
            }
        }
        /** add by yanya on 20200113 增加[金融产品交易附属信息DTO.转换资产代码]对应的账务明细代码生成 end**/
    }

    /**
     * 根据账务明细代码数据构建最终交易生成的账务明细代码list
     * @param list 账务明细数据
     * @param listBokDetailId 结果账务明细数据
     * @param busiType 业务类型
     * @param rFinprodId 转换资产代码
     */
    public void generateTradeBuildBokDetailIdList(List<BokDetailId> list, List<BokDetailId> listBokDetailId,String busiType, String rFinprodId) {
        /** add by yanya on 20200113 入账生成的账务明细代码：四级科目尾号和四级科目尾号名称设置逻辑调整 start **/
        //定义 变量.账套代码Map	 Map<String,String>
        Map<String,String> booksetMap = new HashMap<>();
        /** add by yanya on 20200113 入账生成的账务明细代码：四级科目尾号和四级科目尾号名称设置逻辑调整 end**/
        Map<String,BokDetailId> 四级科目尾号集合 = new HashMap<String,BokDetailId>();
        for(BokDetailId obj :list){
            BokDetailId bokDetailId = 四级科目尾号集合.get(obj.getBokDetailId());
            /** add by yanya on 20200113 入账生成的账务明细代码：四级科目尾号和四级科目尾号名称设置逻辑调整 start **/
            if(bokDetailId == null){
                //调用共通设计->【详细设计-接口共通方法设计.xlsx】->四级科目号设置SHEET
                /**参数名：					模板账套信息
                 finprodId				账务明细代码ENTITY.业务明细代码
                 finprodType				账务明细代码ENTITY.业务明细类型
                 branch					账务明细代码ENTITY.分支序号
                 busiType				输入.交易信息Dto.业务类型
                 返回值：**/
                bokDetailId = interfCommonService.settingDetailSubjectNoTaiL(obj.getBusiId(), obj.getBusiDetailType(),
                        obj.getBranch(), busiType, obj.getBooksetId());
                //bokDetailId = interfCommonService.settingDetailSubjectNoTaiL(finprodId, finprodType, branch, trdProductDeal.getBusiType());
                四级科目尾号集合.put(obj.getBokDetailId(), bokDetailId);
            }
            //变量.账套代码Map.put(账务明细代码ENTITY.账套代码,账务明细代码ENTITY.账套代码)
            booksetMap.put(obj.getBooksetId(), obj.getBooksetId());
            obj.setDetailSubjectNoTail(bokDetailId.getDetailSubjectNoTail());
            obj.setDetailSubnameTail(bokDetailId.getDetailSubnameTail());
            /** add by yanya on 20200113 入账生成的账务明细代码：四级科目尾号和四级科目尾号名称设置逻辑调整 end**/
        }
        this.generateRBokDetailIdList(list, rFinprodId, booksetMap);
        // 变量.入库账务明细代码List.addAll(变量.账务明细代码List)
        listBokDetailId.addAll(list);
    }

    /**
     * 特殊交易及业务类型构建账套信息
     * @param prodFinprodType 产品类型
     * @param chlId 通道代码
     * @param busiType 业务类型
     * @param ps 交易方向
     * @param valBooksetInfoDtoList 账套信息
     * @param listValBooksetInfoDto 结果账套信息
     */
    public void bokTradeBuildSpecialBookset(String prodFinprodType, String chlId, String busiType, String ps,
                                            List<ValBooksetInfoDto> valBooksetInfoDtoList, List<ValBooksetInfoDto> listValBooksetInfoDto) {
        // IF 数据字典：业务类型.净值型TA交易 eq 输入.交易信息Dto.业务类型
        // OR 数据字典：业务类型.货币型TA交易 eq 输入.交易信息Dto.业务类型
        // OR 数据字典：业务类型.预期收益型TA交易 eq 输入.交易信息Dto.业务类型
        // OR ( 数据字典:业务类型.产品费用交易 eq 输入.交易信息Dto.业务类型 and 数据字典：交易方向.产品付费 eq 输入.交易信息Dto.交易方向)
        if (DictCodeList.业务类型_净值型TA交易.getValue().equals(busiType)
                || DictCodeList.业务类型_货币型TA交易.getValue().equals(busiType)
                || DictCodeList.业务类型_预期收益型TA交易.getValue().equals(busiType)
                || (DictCodeList.业务类型_产品费用交易.getValue().equals(busiType)
                && DictCodeList.交易方向_产品付费.getValue().equals(ps))) {
            // 变量.账套信息List.addAll(DTO.valBooksetInfoDtoList)
            listValBooksetInfoDto.addAll(valBooksetInfoDtoList);
        }
        else {
            for (ValBooksetInfoDto item : valBooksetInfoDtoList) {
                String acctSubjectType = prodFinprodType;
                if(StringUtil.isNotEmpty(chlId)){
                    acctSubjectType = DictCodeListExt1.金融产品类型_通道.getValue();
                }
                if(item.getAcctSubjectType().equals(acctSubjectType)){
                    // 变量.账套信息List.add(L)
                    listValBooksetInfoDto.add(item);
                }
            }
        }
    }

    /**
     * 根据数据生成交易入账最终需要入库的数据
     * @param prodId 产品代码
     * @param prodFinprodType 产品类型
     * @param branch 分支序号
     * @param deal 交易数据
     * @param stlOrders 指令信息
     * @param listPtlTrade 组合交易
     * @param listStlOrders 结算指令
     * @param listStlOrdersExt 结算指令扩展
     * @param listBokDealAcct 入账信息
     * @param listBokDealAcctDetail 入账明细信息
     * @param listBokDetailId 账务明细代码
     */
    public void generateBokTradeBuildData(String prodId, String prodFinprodType, Long branch, TrdProductDeal deal, StlOrders stlOrders,
                                          List<PtlTrade> listPtlTrade, List<StlOrders> listStlOrders, List<StlOrdersExt> listStlOrdersExt,
                                          List<BokDealAccounting> listBokDealAcct, List<BokDealAccountingDetail> listBokDealAcctDetail,
                                          List<BokDetailId> listBokDetailId) {
        //结算指令入账
        boolean  dealOrdersBuild = true;
        Map<String, Object> map = new HashMap<String,Object>();
        //add by tanxuan 20200115
        if(DictCodeListExt1.金融产品类型_基金.getValue().equals(deal.getFinprodType())
                || DictCodeList.业务类型_债券交易.getValue().equals(deal.getBusiType())
                || DictCodeList.业务类型_债券质押式回购交易.getValue().equals(deal.getBusiType())
                || DictCodeList.业务类型_债券买断式回购交易.getValue().equals(deal.getBusiType())
                || DictCodeList.业务类型_债券借贷交易.getValue().equals(deal.getBusiType())
                || DictCodeList.业务类型_债券借贷标的债券交易.getValue().equals(deal.getBusiType())){
            if(stlOrders!=null && stlOrders.getMyAcctNo()!=null){
                map  = interfCommonService.instructionGenerateByFront(deal, stlOrders, prodId, prodFinprodType, branch, listPtlTrade);
                dealOrdersBuild = false;
            }
        }
        if(dealOrdersBuild){
            map = interfCommonService.instructionGenerate_Branch(deal, prodId, prodFinprodType, branch, listPtlTrade);
        }
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
    }

    /**
     * 交易入账最终进行入库操作
     * @param listStlOrders 结算指令
     * @param listStlOrdersExt 结算指令扩展
     * @param listBokDealAcct 入账信息
     * @param listBokDealAcctDetail 入账明细信息
     * @param listBokDetailId 账务明细代码
     */
    public void bokTradeBuildForAddDataOpt(List<StlOrders> listStlOrders, List<StlOrdersExt> listStlOrdersExt,
                                           List<BokDealAccounting> listBokDealAcct, List<BokDealAccountingDetail> listBokDealAcctDetail,
                                           List<BokDetailId> listBokDetailId) {
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
            listBokDetailId = interfCommonService.toRepeatBokDetailId(listBokDetailId);
            // 先根据账套代码、账务明细代码删除，然后入库
            valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
            valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
        }
    }

    /**
     * 查询入账交易
     * @param pTradeId 原交易编号
     * @param tradeStatus 交易状态
     * @return
     */
    public TrdProductDeal queryAccountDeal(String pTradeId, String tradeStatus) {
        return valInterfCommonMapper.queryAccountDeal(pTradeId, tradeStatus);
    }

    /**
     * 查询待删除交易List
     * @param pTradeId 原交易编号
     * @param tradeStatus 交易状态
     * @return
     */
    public List<TrdProductDeal> queryToDelDeal(String pTradeId, String tradeStatus) {
        return valInterfCommonMapper.queryToDelDeal(pTradeId, tradeStatus);
    }

    /**
     * 交易入账撤销数据库操作
     * @param dealList 交易信息
     */
    public void bokTradeForDelDataOpt(List<TrdProductDeal> dealList) {
        if (CollectionUtils.isNotEmpty(dealList)) {
            for(TrdProductDeal deal :dealList){
                // 2.1 入账账务明细表
                bokTradeBuildMapper.deleteBokAcctDetail(deal.getTradeId());
                // 2.2 入账信息表
                bokTradeBuildMapper.deleteBokAcct(deal.getTradeId());
            }
        }
    }


    /**
     * 特殊指令类型需要交行本方账户及对方账户
     * @param stlOrders 指令信息
     */
    public void exchangeStlOrdersByStlOrder(StlOrders stlOrders) {
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
    }

    /**
     * 指令入账特殊增加账套信息
     * 投资组合以及通道代码为空
     * @param listValBooksetInfoDto 账套信息集合
     * @param prodId 产品代码
     * @param prodFinprodType 产品类型
     * @param channelId 通道代码
     * @param listPtlTrade 组合交易
     */
    public void stlOrdersBokTradeAdditionalBookset(List<ValBooksetInfoDto> listValBooksetInfoDto, String prodId,
                                               String prodFinprodType, String channelId, List<PtlTrade> listPtlTrade) {
        //IF 数据字典：金融产品类型.投资组合 eq 输入.产品类型 and 输入.交易信息Dto.通道代码  IS NULL
        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(prodFinprodType)
                && StringUtil.isEmpty(channelId)){
            // 调用共通设计->【详细设计-接口共通方法设计.xlsx】->账套信息(子组合)查询 SHEET
            List<ValBooksetInfoDto>  valBooksetInfoDtoList= interfCommonService.getSonPtlPortfolo(prodId, listPtlTrade);
            if (CollectionUtils.isNotEmpty(valBooksetInfoDtoList)) {
                listValBooksetInfoDto.addAll(valBooksetInfoDtoList);
            }
        }
    }

    /**
     * 构建账务明细代码
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param busiId 业务明细代码
     * @param busiDetailType 业务类型
     * @param tradeId 交易编号
     * @param ccy 币种
     * @param detailSubjectNoTail 四级科目明细号
     * @param detailSubnameTail 四级科目明细名称
     * @return
     */
    public BokDetailId buildBokDetailId(String booksetId, String bokDetailId, String busiId, String busiDetailType,
                                        String tradeId, String ccy, String detailSubjectNoTail, String detailSubnameTail) {
        // 2.2.3 初始化账务明细代码表
        BokDetailId bokDetailIdInitDto = new BokDetailId();
        // 字段名 字段code 数据设置值
        // 账套代码 BOOKSET_ID L.账套代码
        bokDetailIdInitDto.setBooksetId(booksetId);
        // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
        bokDetailIdInitDto.setBokDetailId(bokDetailId);
        // 业务明细代码 BUSI_ID 输入.结算指令交易信息Dto.本方资金账户
        bokDetailIdInitDto.setBusiId(busiId);
        // 业务明细类型 BUSI_DETAIL_TYPE 变量.业务明细类型
        bokDetailIdInitDto.setBusiDetailType(busiDetailType);
        // 交易编号 TRADE_ID 输入.结算指令交易信息Dto.指令编号
        bokDetailIdInitDto.setTradeId(tradeId);
        /**ADD BY WUJIE ON 20190829 START**/
        // 币种 CCY 输入.结算指令交易信息Dto.币种
        bokDetailIdInitDto.setCcy(ccy);
        //变量.四级科目尾号集合.put(L.账套代码+变量.账务明细代码,变量.bokDetailId)
        //变量.账务明细代码表Dto.四级科目尾号 = 变量.bokDetailId.四级科目尾号
        bokDetailIdInitDto.setDetailSubjectNoTail(detailSubjectNoTail);
        //变量.账务明细代码表Dto.四级科目尾号名称 = 变量.bokDetailId.四级科目尾号名称
        bokDetailIdInitDto.setDetailSubnameTail(detailSubnameTail);
        /**ADD BY LBY ON 20201226 END**/
        return bokDetailIdInitDto;
    }

    /**
     * 投建入账信息表
     * @param booksetId 账套代码
     * @param busiNo 业务编号
     * @param busiTable 业务表
     * @param booksetDate 账套日期
     * @param bookDate 入账日
     * @param happenDate 发生日
     * @param isAccounted 是否已入账
     * @return
     */
    public BokDealAccounting buildBokDealAccounting(String booksetId, String busiNo, String busiTable, Date booksetDate, Date bookDate,
                                                    Date happenDate,String isAccounted) {
        // 2.2.4 初始化入账信息表
        BokDealAccounting bokDealAcctInitDto = new BokDealAccounting();
        // 字段名 字段code 数据设置值
        // 账套代码 BOOKSET_ID L.账套代码
        bokDealAcctInitDto.setBooksetId(booksetId);
        // 业务编号 BUSI_NO 输入.结算指令交易信息Dto.指令编号
        bokDealAcctInitDto.setBusiNo(busiNo);
        // 业务表 BUSI_TABLE 数据字典：业务表.结算指令表
        bokDealAcctInitDto.setBusiTable(busiTable);
        // 录入日 BOOKSET_DATE 输入.结算指令交易信息Dto.结算日期
        bokDealAcctInitDto.setBooksetDate(booksetDate);
        // 入账日 BOOK_DATE 输入.结算指令交易信息Dto.结算日期
        bokDealAcctInitDto.setBookDate(bookDate);
        // 实际发生日 HAPPEN_DATE 输入.结算指令交易信息Dto.结算日期
        bokDealAcctInitDto.setHappenDate(happenDate);
        // 是否已入账 IS_ACCOUNTED 数据字典：是否.否
        bokDealAcctInitDto.setIsAccounted(isAccounted);
        return bokDealAcctInitDto;
    }

    /**
     * 构建入账明细数据
     * @param booksetId 账套代码
     * @param busiNo 业务编号
     * @param busiTable 业务表
     * @param busiType 业务类型
     * @param bokDetailId 账务明细代码
     * @return
     */
    public BokDealAccountingDetail buildBokDealAccountingDetail(String booksetId, String busiNo, String busiTable, String busiType, String bokDetailId) {
        // 2.2.5 初始化入账明细信息表
        BokDealAccountingDetail bokDealAcctDetailInitDto = new BokDealAccountingDetail();
        // 字段名 字段code 数据设置值
        // 账套代码 BOOKSET_ID L.账套代码
        bokDealAcctDetailInitDto.setBooksetId(booksetId);
        // 业务编号 BUSI_NO 输入.结算指令交易信息Dto.指令编号
        bokDealAcctDetailInitDto.setBusiNo(busiNo);
        // 业务表 BUSI_TABLE 数据字典：业务表.结算指令表
        bokDealAcctDetailInitDto.setBusiTable(busiTable);
        // 业务类型 BUSI_TYPE 数据字典：业务类型.指令交易
        bokDealAcctDetailInitDto.setBusiType(busiType);
        // 账务明细代码 BOK_DETAIL_ID 变量.账务明细代码
        bokDealAcctDetailInitDto.setBokDetailId(bokDetailId);
        return bokDealAcctDetailInitDto;
    }

    /**
     * 构建指令扩展信息
     * @param orderId
     * @param isTrade
     * @return
     */
    public StlOrdersExt buildStlOrdersExt(String orderId, String isTrade) {
        StlOrdersExt valStlOrdersExt = new StlOrdersExt();
        valStlOrdersExt.setOrderId(orderId);
        valStlOrdersExt.setIsTrade(isTrade);
        return valStlOrdersExt;
    }

    /**
     * 结算指令交易入账操作数据库
     * @param listBokDealAcct
     * @param listBokDealAcctDetail
     * @param listBokDetailId
     * @param listStlOrders
     * @param listvalStlOrdersExt
     */
    public void stlOrdersBokTradeBuildDataOpt(List<BokDealAccounting> listBokDealAcct, List<BokDealAccountingDetail> listBokDealAcctDetail,
                                              List<BokDetailId> listBokDetailId, List<StlOrders> listStlOrders, List<StlOrdersExt> listvalStlOrdersExt) {
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
            /**ADD BY WUJIE ON 20191008 START**/
            listBokDetailId = interfCommonService.toRepeatBokDetailId(listBokDetailId);
            /**ADD BY WUJIE ON 20191008 END**/
            valBokDetailIdBatchMapper.deleteBatch(listBokDetailId);
            valBokDetailIdBatchMapper.insertBatch(listBokDetailId);
        }
        //指令
        if (CollectionUtils.isNotEmpty(listStlOrders)) {
            valOrdersBatchMapper.insertBatch(listStlOrders);
        }
        //指令扩展
        if (CollectionUtils.isNotEmpty(listvalStlOrdersExt)) {
            valOrdersExtBatchMapper.insertBatch(listvalStlOrdersExt);
        }
    }

    /**
     * 生成存续洗交易合并的金额信息
     * @param durationList 存续期交易数据
     * @return
     */
    public Map<String, BigDecimal> calcDurationTradeMergeAmt(List<DurationTrade001Dto> durationList) {
        Map<String, BigDecimal> 金额Map = new HashMap<>();
        // 2.2.1 初始化金融产品表
        // 定义 变量.本金金额
        BigDecimal prinAmt = BigDecimal.ZERO;
        // 定义 变量.实收利息
        BigDecimal intAmt = BigDecimal.ZERO;
        // 定义 变量.交易份额
        BigDecimal shareAmt = BigDecimal.ZERO;
        // 循环交易数据，获取本金金额和实收利息
        for(DurationTrade001Dto deal : durationList){
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
                    // edit by chengzhong for 回购款实收利息存在收息交易的交易金额里面，使用交易金额 20200107 start
//                    intAmt = ArithmeticUtil.doAdd(intAmt,deal.getIntAmt());
                    intAmt = ArithmeticUtil.doAdd(intAmt,deal.getTradeAmt());
                    // edit by chengzhong for 回购款实收利息存在收息交易的交易金额里面，使用交易金额 20200107 end
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
        if (DictCodeListExt1.金融产品类型_拆入.getValue().equals(durationList.get(0).getFinprodType())
                || DictCodeListExt1.金融产品类型_拆出.getValue().equals(durationList.get(0).getFinprodType())) {
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
        else if (DictCodeListExt1.金融产品类型_债券质押式正回购.getValue().equals(durationList.get(0).getFinprodType())
                || DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(durationList.get(0).getFinprodType())) {
            // 变量.净价总额 = 变量.交易份额
            cpriceAmt = shareAmt;
            // 变量.全价总额 = 变量.本金金额 + 变量.实收利息
            fpriceAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
            // 变量.交易金额 = 变量.本金金额 + 变量.实收利息
            tradeAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
        }
        //ELSE IF 数据字典：金融产品类型.债券买断式正回购 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        //OR 数据字典：金融产品类型.债券买断式逆回购 eq 输入.汇总交易明细集合.get(0).金融产品类型(估值核算)
        else if(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue().equals(durationList.get(0).getFinprodType())
                || DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(durationList.get(0).getFinprodType())){
            // 变量.净价总额 = NULL
            cpriceAmt = null;
            // 变量.交易份额 = 变量.交易份额
            //shareAmt = shareAmt;
            // 变量.全价总额 = NULL
            fpriceAmt = null;
            // 变量.交易金额 = 变量.本金金额 + 变量.实收利息
            tradeAmt = ArithmeticUtil.doAdd(prinAmt, intAmt);
        }
        金额Map.put(ValApiConstant.本金金额字段, prinAmt);
        金额Map.put(ValApiConstant.实收利息字段, intAmt);
        金额Map.put(ValApiConstant.交易份额字段, shareAmt);
        金额Map.put(ValApiConstant.净价总额字段, cpriceAmt);
        金额Map.put(ValApiConstant.全价总额字段, fpriceAmt);
        金额Map.put(ValApiConstant.交易金额字段, tradeAmt);
        return 金额Map;
    }

    /**
     * 获取存续期合并交易的交易方向
     * @param psType 投管交易方向
     * @param busiType 业务类型
     * @return
     */
    public String getDurationTradeMergePs(String psType, String busiType) {
        /**ADD BY WUJIE ON 20210129 START REMARK:回购区分 到期、到期续作、逾期贩售**/
        String ps = DictCodeList.交易方向_到期.getValue();
        if(DictCodeList.业务类型_债券质押式回购交易.getValue().equals(busiType)){
            if(DictCodeList.交易方向_到期续做.getValue().equals(psType)){
                ps = DictCodeList.交易方向_到期续做.getValue();
            }else if (DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(psType)){
                ps = DictCodeListExt3.交易方向_投资端_逾期返售.getValue();
            }
        }
        return ps;
    }

    /**
     * 构建金融产品交易附属表信息
     * @param tradeId 交易编号
     * @param prodId 产品代码
     * @param genType 生成方式
     */
    public TrdProductDealAdd buildTrdProductDealAdd(String tradeId, String prodId, String genType) {
        TrdProductDealAdd trdProductDealAddVo = new TrdProductDealAdd();
        // 2.2.2 初始化金融产品交易附属表
        trdProductDealAddVo.setTradeId(tradeId);//交易编号
        trdProductDealAddVo.setPortfolioId(prodId);//投资组合代码
        trdProductDealAddVo.setGenType(genType);//生成方式
        return trdProductDealAddVo;
    }

    /**
     * 金融产品及附属表入库
     * @param trdProductDealVo 金融产品交易
     * @param trdProductDealAddVo 金融产品交易附属表
     * @param feeDetail 交易费用信息
     */
    public void durationTradeMergeDataOpt(TrdProductDeal trdProductDealVo, TrdProductDealAdd trdProductDealAddVo, TrdTradeFeeDetail feeDetail) {
        if(trdProductDealVo != null) {
            trdProductDealMapper.insert(trdProductDealVo);//金融产品表数据入库
        }
        if(trdProductDealAddVo != null) {
            trdProductDealAddMapper.insert(trdProductDealAddVo);//金融产品交易附属表数据入库
        }
        if(feeDetail != null) {
            trdTradeFeeDetailMapper.insert(feeDetail);//交易费用信息
        }
    }

    /**
     * 根据交易信息构建交易入账api接口参数
     * @param prodId
     * @param prodFinprodType
     * @param branch
     * @param trdProductDealVo
     * @return
     */
    public BokTradeBuild001Dto buildBokTradeBuild001Dto(String prodId, String prodFinprodType, Long branch, TrdProductDeal trdProductDealVo) {
        BokTradeBuild001Dto bokTradeBuild001Dto = new BokTradeBuild001Dto();
        BeanUtil.copyPropertiesIgnoreNull(bokTradeBuild001Dto, trdProductDealVo);
        bokTradeBuild001Dto.setProdId(prodId);//产品代码
        bokTradeBuild001Dto.setProdFinprodType(prodFinprodType);//产品类型
        bokTradeBuild001Dto.setBranch(branch);//分支序号
        return bokTradeBuild001Dto;
    }

    /**
     * 投建交易费用数据
     * @param tradeId 交易编号
     * @param feeType 费用类型
     * @param isPayWithTrade 是否随交易支付
     * @param feeAmt 费用金额
     * @param feeId 费用ID
     * @return
     */
    public TrdTradeFeeDetail buildTrdTradeFeeDetail(String tradeId, String feeType, String isPayWithTrade, BigDecimal feeAmt, String feeId) {
        TrdTradeFeeDetail feeDetail = new TrdTradeFeeDetail();
        feeDetail.setTradeId(tradeId);//交易编号
        feeDetail.setFeeType(feeType);//费用类型
        feeDetail.setIsPayWithTrade(isPayWithTrade);//是否随交易支付
        feeDetail.setFeeAmt(feeAmt);//费用金额
        feeDetail.setFeeId(feeId);//费用ID
        return feeDetail;
    }

    /**
     * 根据金融产品代码获取金融产品附属表信息
     * @param finprodId 金融产品代码
     * @return
     */
    public FinProductAdd getFinProductAdd(String finprodId) {
        FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
        return finProductAdd;
    }

    /**
     * 根据汇总交易编号查询交易数据
     * @param totleTradeId 汇总交易编号
     * @return
     */
    public List<TrdProductDeal> getTradeIdByTotleTradeId(String totleTradeId) {
        return valInterfCommonMapper.getTradeIdByFilter(totleTradeId);
    }

    /**
     * 根据汇总交易编号查询生成的通道交易
     * @param totleTradeId 汇总交易编号
     * @return
     */
    public List<TrdProductDeal> getFeeDealByTotleTradeId(String totleTradeId){
        return valInterfCommonMapper.getFeeDealByFilter(totleTradeId);
    }

    /**
     * 删除估值相关交易数据
     * @param tradeId 交易编号
     */
    public void delValTradeInfo(String tradeId) {
        // 2.1 入账账务明细表
        bokTradeBuildMapper.deleteBokAcctDetail(tradeId);
        // 2.2 入账信息表
        bokTradeBuildMapper.deleteBokAcct(tradeId);
        // 2.3 结算指令扩展表
        valInterfCommonMapper.delStlOrdersExt(tradeId);
        // 2.4 结算指令表
        valInterfCommonMapper.delStlOrders(tradeId);
        // 2.5 交易费用明细表
        valInterfCommonMapper.delTrdTradeFeeDetail(tradeId);
        // 2.6 金融产品交易附属表
        valInterfCommonMapper.delTrdProductDealAdd(tradeId);
        // 2.7 金融产品实物交易
        valInterfCommonMapper.delTrdFinProductBarter(tradeId);
        // 2.8 金融产品交易
        valInterfCommonMapper.delTrdProductDeal(tradeId);
    }

    /**
     * 根据产品代码、产品类型查询账套信息
     * @param prodId 产品代码
     * @param prodType 产品类型
     * @return
     */
    public BokBookset queryBooksetAsAcct(String prodId, String prodType) {
        return valInterfCommonMapper.queryBooksetAsAcct(prodId, prodType);
    }

    /**
     * 根据账套代码查询关联账套信息
     * @param booksetId 账套代码
     * @return
     */
    public List<BokBooksetRe> queryBokBooksetReList(String booksetId) {
        return valInterfCommonMapper.queryBokBooksetReList(booksetId);
    }

    /**
     * 根据账套代码根据账套到期日
     * @param booksetId 账套代码
     * @param mdate 到期日
     */
    public void updateBooksetAsAcct(String booksetId, Date mdate) {
        //2.3.1 更新账套结束日
        Map<String,Object> params = new HashMap<>();
        params.put("mDate", mdate);
        params.put("booksetId", booksetId);
        valInterfCommonMapper.updateBooksetAsAcct(params);
    }

    /**
     * 根据产品信息更新分层信息到期日
     * @param prodId 产品代码
     * @param prodType 产品类型
     * @param branch 分支序号
     * @param mdate 到期日
     */
    public void updateAcctLayeringInfoByProd(String prodId, String prodType, Long branch, Date mdate) {
        Map<String,Object> params = new HashMap<>();
        //2.3.2 更新产品对应核算分层信息到期日
        params.put("mDate", mdate);//核算到期日
        params.put("prodId", prodId);//产品代码
        params.put("prodType", prodType);//产品类型
        params.put("branch", branch);//分支序号
        valInterfCommonMapper.updateAcctLayeringInfo(params);
    }

    /**
     * 根据关联账套信息更新分层信息的到期日
     * @param valBokBooksetReList 关联账套信息
     * @param mdate 到期日
     */
    public void updateAcctLayeringInfoByBookset(List<BokBooksetRe> valBokBooksetReList, Date mdate) {
        if (CollectionUtils.isNotEmpty(valBokBooksetReList)) {
            Map<String, Object> params = new HashMap<>();
            params.put("mDate", mdate);//核算到期日
            for (BokBooksetRe bokBooksetRe : valBokBooksetReList) {
                params.put("booksetIdSon", bokBooksetRe.getBooksetIdSon());
                valInterfCommonMapper.updateAcctLayeringInfo(params);
            }
        }
    }
}
