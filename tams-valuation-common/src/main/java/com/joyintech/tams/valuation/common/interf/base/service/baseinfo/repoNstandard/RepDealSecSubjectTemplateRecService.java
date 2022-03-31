/*
 * 文件名：DepositeAssetBaseInfoTemplateRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.service.baseinfo.repoNstandard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.FinProductClass;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductValuationPara;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.RepoNStandardCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValRepDealSecSubject;

/**
 * 债券回购质押品信息模板处理Service(VAL_REP_DEAL_ASSET)
 * 
 * @author leipeng
 * @version 1.0 2018年6月27日
 * @see RepDealSecSubjectTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RepDealSecSubjectTemplateRecService {

    /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;
    
    @Autowired
    private InterfCommonService interfCommonService;
    

    /**
     * 债券质押式回购标的信息接口模板处理
     * 
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 净值型产品交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public RepoNStandardCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                                        TemplateOrderInfoReaderResult acctEntity) {
        // 定义返回变量
    	RepoNStandardCommonResult result = new RepoNStandardCommonResult();
        List<ValRepDealSecSubject> listValRepDealSecSubject = new ArrayList<ValRepDealSecSubject>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValRepDealSecSubject = returnResult.getT(ValRepDealSecSubject.class);

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValBotAstInfo(listValRepDealSecSubject);
        if(map != null){
        	 // 交易类金融产品List
            List<FinTradeProduct> listValTradeFinProduct = (List<FinTradeProduct>)map.get(
                "ValTradeFinProduct");
            result.setListValTradeFinProduct(listValTradeFinProduct);
            // 定义 金融产品分类List
            List<FinProductType> listValFinProductType = (List<FinProductType>)map.get(
                "ValFinProductType");
            result.setListValFinProductType(listValFinProductType);

            // 定义 交易类金融产品附属表List
            List<FinTradeProductAdd> listFinTradeProductAdd = (List<FinTradeProductAdd>)map.get(
                    "FinTradeProductAdd");
            result.setListFinTradeProductAdd(listFinTradeProductAdd);
            /**add by wujie on 20190420 start**/
            //金融产品估值参数表
            List<FinProductValuationPara> listFinProductValuationPara = (List<FinProductValuationPara>) map.get("FinProductValuationPara");
            result.setListFinProductValuationPara(listFinProductValuationPara);
            /**add by wujie on 20190420 end**/
        }
        // 添加日志
        result.setValInterflog(returnResult.getValInterflog());
        // 返回处理结果数据
        return result;
    }

    public Map<String, Object> handleValBotAstInfo(List<ValRepDealSecSubject> listValRepDealSecSubject) {
        Map<String, Object> result = new HashMap<>();
        // 定义 交易类金融产品List
        List<FinTradeProduct> listValTradeFinProduct = new ArrayList<FinTradeProduct>();
        // 定义 金融产品分类List
        List<FinProductType> listValFinProductType = new ArrayList<FinProductType>();
        // 定义 交易类金融产品附属表List
        List<FinTradeProductAdd> listFinTradeProductAdd = new ArrayList<FinTradeProductAdd>();
        /**add by wujie on 20190420 start**/
        List<FinProductValuationPara> listFinProductValuationPara = new ArrayList<FinProductValuationPara>();
        /**add by wujie on 20190420 end**/

        for (ValRepDealSecSubject dto : listValRepDealSecSubject) {
            // 交易类金融产品
            FinTradeProduct valTradeFinProduct = getValTradeFinProduct(dto);
            listValTradeFinProduct.add(valTradeFinProduct);
            // 金融产品分类
            FinProductType valFinProductType = getValFinProductType(dto);
            listValFinProductType.add(valFinProductType);

            //交易类金融产品附属表
            FinTradeProductAdd finTradeProductAdd = getFinTradeProductAdd(dto);
            listFinTradeProductAdd.add(finTradeProductAdd);
            /**add by wujie on 20190420 start**/
            // 金融产品估值参数表
            FinProductValuationPara finProductValuationPara = getValFinProductValuationPara(dto);
            listFinProductValuationPara.add(finProductValuationPara);
            /**add by wujie on 20190420 end**/

        }
        result.put("ValTradeFinProduct", listValTradeFinProduct);
        result.put("ValFinProductType", listValFinProductType);
        result.put("FinTradeProductAdd", listFinTradeProductAdd);
        /**add by wujie on 20190420 start**/
        result.put("FinProductValuationPara", listFinProductValuationPara);
        /**add by wujie on 20190420 end**/
        return result;
    }

    /**
     * T1 交易类金融产品
     * 
     * @param dto 01-基础信息
     * @param floatingResult 
     * @return 交易类金融产品
     * @see
     * @since 1.0
     */
    private FinTradeProduct getValTradeFinProduct(ValRepDealSecSubject dto) {
        FinTradeProduct valTradeFinProduct = new FinTradeProduct();
        // 金融产品代码 = A.回购标的代码
        valTradeFinProduct.setFinprodId(dto.getTradeId());
        // 金融产品类型 = A.回购标的类型
        valTradeFinProduct.setFinprodType(dto.getTradeType());
        // 分支序号 = 设置全局变量(默认为0）
        valTradeFinProduct.setBranch(InterfCommonConstant.分支序号_默认);
        // 分支类型 = 设置全局变量(默认为空)
        valTradeFinProduct.setBranchType(null);
        // 名义本金 = A.名义本金
        valTradeFinProduct.setPrin(dto.getCapital());
        // 本金币种 = A.币种
        valTradeFinProduct.setCcy(dto.getCcy());
        // 起息日 = A.起息日
        valTradeFinProduct.setVdate(dto.getVdate());
        // 到期日 = A.到期日
        valTradeFinProduct.setMdate(dto.getMdate());
        // 期限(天) = A.到期日-A.起息日
        valTradeFinProduct.setTerm(DateUtil.getBetweenDayNumber(dto.getVdate(), dto.getMdate()));
        // 利率类型 = A.利率类型
        valTradeFinProduct.setIntType(dto.getRateType());
        // 固定利率 = A.回购利率
        valTradeFinProduct.setIntRate(dto.getRedRate());
        // 计息基础 = A.计息基础
        valTradeFinProduct.setBasis(dto.getBasis());
        // 到期本金 = A.到期本金
        valTradeFinProduct.setmPrinAmt(dto.getmPrinAmt());
        // 到期利息 = A.到期利息
        valTradeFinProduct.setmIntAmt(dto.getmIntAmt());
        // 到期金额 = A.到期金额
        valTradeFinProduct.setmTradeAmt(dto.getmTradeAmt());
        // 原金融产品代码 = A.回购标的代码
        valTradeFinProduct.setoFinprodId(dto.getTradeId());
        // 原金融产品类型 = A.回购标的类型
//        valTradeFinProduct.setoFinprodType(dto.getTradeType());
        //期限品种 = A.期限品种
        if("ON".equals(CacheUtil.getSysParam("REPO_TERM_KIND_KG"))){
        	valTradeFinProduct.setTermType(dto.getTermKind());
        }else{
        	/**
             * modify by wujie on 20180914 start remark:根据期限天数调用共通获取对应的期限品种
            valTradeFinProduct.setTermType(dto.getTermKind());
            **/
            String termType = interfCommonService.getRepTermType(valTradeFinProduct.getFinprodType(),valTradeFinProduct.getTerm().intValue());
            valTradeFinProduct.setTermType(termType);
            
            /**modify by wujie on 20180914 end**/
        }
        //本金收益特征 = A.本金收益特征
        valTradeFinProduct.setCapiIncomeFeature(dto.getCapiIncomeFeature());
        
        /**add by wujie on 20181221 start**/
        //交易场所 = A.交易场所
        valTradeFinProduct.setTradeMarket(dto.getTradeMarket());
        
        // 交易日历 = 共通
        valTradeFinProduct.setCalendarId(interfCommonService.getCalendarId(dto.getTradeMarket()));
        
        /**add by wujie on 20181221 end**/
        
        /**add by wujie on 20190115 start**/
        //收益类型 = 数据字典：收益类型.预期收益
        valTradeFinProduct.setProfitType(DictCodeList.收益类型_收益型.getValue());
        /**add by wujie on 20190115 end**/
        
        /**add by wujie on 20190420 start**/
        //首期交付日 = A.首期交付日
        valTradeFinProduct.setVpayDate(dto.getVpayDate());
        //到期交付日 = A.到期交付日
        valTradeFinProduct.setMpayDate(dto.getMpayDate());
        //实际占款天数 = A.到期交付日-A.首期交付日
        if(valTradeFinProduct.getVpayDate() != null && valTradeFinProduct.getMpayDate()!= null){
            valTradeFinProduct.setActCapDays(DateUtil.getBetweenDayNumber(dto.getVpayDate(), dto.getMpayDate()));
        }
        /**add by wujie on 20190420 end**/
        
        // 所属机构 = A.所属机构   add by tanxuan on 20210310 多级法人权限控制改造
        valTradeFinProduct.setBelongInstitutionCode(dto.getBelongInstitutionCode());
        //modify by yanya on 20210828 增加交易对手代码
        valTradeFinProduct.setCounterId(dto.getCounterId());
        return valTradeFinProduct;
    }

    /**
     * T2 金融产品分类
     * 
     * @param dto 01-基础信息
     * @return 金融产品分类
     * @see
     * @since 1.0
     */
    private FinProductType getValFinProductType(ValRepDealSecSubject dto) {
        FinProductType valFinProductType = new FinProductType();
        // 金融产品代码 = T1.金融产品代码
        valFinProductType.setFinprodId(dto.getTradeId());
        // 金融产品类型 = T1.金融产品类型
//        valFinProductType.setFinprodType(dto.getTradeType());
        /**add by wujie on 20180822 start**/
        // 分类1 = A.资产类型
        valFinProductType.setType1(dto.getAssetClass());
        // 分类2 = 调用共通方法查询核算分类
        FinProductClass fdto = new FinProductClass();
        //金融产品类型
        fdto.setFinprodType(dto.getTradeType());
        //资产类型
        fdto.setAssetType(dto.getAssetClass());
        //机构核算分类
        fdto.setOrgClass(dto.getValClas());
        
        valFinProductType.setType2(interfCommonService.getFinanceProductClass(fdto));
        
        // 分类3 = 调用共通方法查询税务分类
        valFinProductType.setType3(interfCommonService.getFinanceProductTaxClass(fdto));

        // 分类18 = 调用共通方法查询证券品种 add by yanya on 20210828
        valFinProductType.setType18(interfCommonService.getFinanceProductSecType(fdto));

        /**add by wujie on 20180822 end**/
        valFinProductType.setBranch(InterfCommonConstant.分支序号_默认);
        /**add by yinya on 2022-03-07 新会计引擎升级，科目体系需要区分回购类型，回购标的接口新增回购类型**/
        valFinProductType.setType6(dto.getTargetBackType());
        //保存SEC_TYPE
        valFinProductType.setType18(DictCodeListExt1.证券品种_回购.getValue());
        return valFinProductType;
    }

    
    /**
     * T3 交易类金融产品附属表
     * 
     * @param dto 01-基础信息 
     * @return 交易类金融产品附属表
     * @see
     * @since 1.0
     */
    private FinTradeProductAdd getFinTradeProductAdd(ValRepDealSecSubject dto) {
    	FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
        // 金融产品代码 = T1.金融产品代码
    	finTradeProductAdd.setFinprodId(dto.getTradeId());
        // 金融产品类型 = T1.金融产品类型
    	finTradeProductAdd.setFinprodType(dto.getTradeType());
    	 // 分支序号 = 设置全局变量(默认为0）
    	finTradeProductAdd.setBranch(InterfCommonConstant.分支序号_默认);
    	//登记托管机构 = A.登记托管机构
    	finTradeProductAdd.setRegistOrg(dto.getRegistOrg());
    	//押品只数 = A.押品只数
    	finTradeProductAdd.setPledgeSecNum(dto.getPledgeSecNum());
        
        return finTradeProductAdd;
    }



    /**
     * T4.金融产品估值参数表 接收
     * @param dto
     * @return 金融产品估值参数表
     * add by wujie on 20190420
     */
    private FinProductValuationPara getValFinProductValuationPara(ValRepDealSecSubject dto) {
        // 金融产品估值参数表
        FinProductValuationPara valFinProductValuationPara = new FinProductValuationPara();
        // 金融产品代码= A.回购标的代码
        valFinProductValuationPara.setFinprodId(dto.getTradeId());
        // 分支序号= 固定值 设置全局变量(默认为0）
        valFinProductValuationPara.setBranch(InterfCommonConstant.分支序号_默认);
        //交易所回购计提天数方式 = 系统参数.交易所回购计提天数方式
        valFinProductValuationPara.setSeRepDayType(CacheUtil.getSysParam("SE_REP_DAY_TYPE"));
        return valFinProductValuationPara;
    }

}
