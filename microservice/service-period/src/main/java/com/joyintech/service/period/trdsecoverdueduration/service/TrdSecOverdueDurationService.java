package com.joyintech.service.period.trdsecoverdueduration.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.valuationusemerge.calc.CalCommonResult;
import com.joyintech.tams.invest.common.dto.valuationusemerge.calc.PrincipalInfo;
import com.joyintech.tams.invest.common.dto.valuationusemerge.calc.QuoteInfo;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.common.valuationinterf.CalCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.period.trdsecoverdueduration.dao.TrdSecOverdueDurationMapper;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.autoperiod.basePtlRecePosition.BasePtlRecePositionService;
import com.joyintech.tams.invest.common.autoperiod.common.dao.PeriodCommonMapper;
import com.joyintech.tams.invest.common.autoperiod.common.service.InvestPeriodCommonService;
import com.joyintech.tams.invest.common.autoperiod.ptlreceinfo.BasePtlReceInfoService;
import com.joyintech.tams.invest.common.autoperiod.ptlreceinfo.dto.PtlReceInfo;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.FinAssetIntInfo;
import com.joyintech.tams.invest.common.dto.period.common.BaseTradeInfoDto;
import com.joyintech.tams.invest.common.dto.period.trdsecoverdueduration.TrdSecOverdueDurationDto;
import com.joyintech.tams.invest.common.dto.period.trdsecoverdueduration.TrdSecOverdueDurationZrDto;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.AllTradeCommonInputDto;

@Service
public class TrdSecOverdueDurationService extends BaseService<TrdSecOverdueDurationDto> {

    /**
     * 平台日期
     */
    @Autowired
    private IPlatformDate platformDateImpl;
    @Autowired
    private TrdSecOverdueDurationMapper trdSecOverdueDurationMapper;
    @Autowired(required = false)
    private BasePtlRecePositionService basePtlRecePositionService;
    /**
     * 投管存续期共通Service
     */
    @Autowired
    private InvestPeriodCommonService investPeriodCommonService;
    /**
     *  投管存续期共通Mapper
     */
    @Autowired
    private PeriodCommonMapper periodCommonMapper;
    /**
     *  应收款标的共通
     */
    @Autowired
    private BasePtlReceInfoService basePtlReceInfoService;

    /**
     *  估计计算共通
     */
    @Autowired
    private CalCommonService calCommonService;

    /** @MethodName: theoryFindPage
    * @Description: TODO逾期未结清
    * @Param: [page, length, param]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/5 0005
    **/
    public Page<TrdSecOverdueDurationDto> theoryFindPage(int page, int length, Map<String, Object> param) {
        setParams(param);
        return queryForPageForSelectReturn(page, length, () -> trdSecOverdueDurationMapper.theoryFindPage(param));
    }
    /** @MethodName: realityFindPage
    * @Description: TODO逾期已结清
    * @Param: [page, length, param]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.common.dto.period.trdseclendduration.TrdSecLendDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/5 0005
    **/
    public Page<TrdSecOverdueDurationDto> realityFindPage(int page, int length, Map<String, Object> param) {
        setParams(param);
        return queryForPageForSelectReturn(page, length, () -> trdSecOverdueDurationMapper.realityFindPage(param));
    }

    private void setParams(Map<String, Object> param) {
        param.put("sysDate",platformDateImpl.getSysDate());
        // 多级法人改造 add by caiqun 20200615
        if(MultilevelDataAuthorityUtil.isEnabled()) {
            //当前用户所属机构代码
            String createInst =  MultilevelDataAuthorityUtil.getService().queryOrganizationCode(LoginUserUtil.getUserName());
            //右上角选择框为空时 存放本级机构及有权限的下级机构 选择时 存放所选机构
            List<String> resourcesTreeInstCodeList =  LoginUserUtil.getTreeInstCode();
            param.put("institutionCode",createInst);
            param.put("resourcesTreeInstCodeList",resourcesTreeInstCodeList);
        }
        // 多级法人改造 add by caiqun 20200615
    }
    /** @MethodName: RecePositionDetailFindPage
    * @Description: TODO 持仓明细列表
    * @Param: [page, length, param]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.common.dto.period.trdsecoverdueduration.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/6 0006
    **/
    public Page<TrdSecOverdueDurationDto> recePositionDetailFindPage(int page, int length, Map<String, Object> param) {
        setParams(param);
        return queryForPageForSelectReturn(page, length, () -> trdSecOverdueDurationMapper.findRecePositionDetail(param));
    }
    /** @MethodName: findRecePositionDetailByPortfolioId
    * @Description: TODO 子组合持仓明细列表
    * @Param: [page, length, param]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.common.dto.period.trdsecoverdueduration.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/12 0012
    **/
    public Page<TrdSecOverdueDurationDto> findRecePositionDetailByPortfolioId(int page, int length, Map<String, Object> param) {
        setParams(param);
    	param.put("BUSI_TYPE_37", DictCodeList.业务类型_逾期交易.getValue());
    	param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
    	param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
    	param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
        return queryForPageForSelectReturn(page, length, () -> trdSecOverdueDurationMapper.findRecePositionDetailByPortfolioId(param));
    }


    /** @MethodName: intertstDetailFindPage
    * @Description: TODO 收息明细信息
    * @Param: [page, length, param]
    * @Return: com.joyintech.framework.grid.Page<com.joyintech.tams.invest.common.dto.period.trdsecoverdueduration.TrdSecOverdueDurationDto>
    * @Author: wangxuelei
    * @Date: 2019/12/6 0006
    **/
    public Page<TrdSecOverdueDurationDto> intertstDetailFindPage(int page, int length, Map<String, Object> param) {
        setParams(param);
        return queryForPageForSelectReturn(page, length, () -> trdSecOverdueDurationMapper.findIntertstDetail(param));
    }


    public TrdSecOverdueDurationDto selectJxwuFromFinprodId(Map<String, Object> param){
        TrdSecOverdueDurationDto trdSecOverdueDurationDto = trdSecOverdueDurationMapper.selectJxwuFromFinprodId(param);
        return trdSecOverdueDurationDto;
    }
    
    /**
     * 新建基本信息设置
     * @param param
     * @return
     */
    public TrdSecOverdueDurationDto selectNewBasicdata(Map<String, Object> param){
    	param.put("CASH_TYPE_2_C01", DictCodeListExt1.现金流二级类型_本金.getValue());
    	param.put("CASH_TYPE_2_C02", DictCodeListExt1.现金流二级类型_利息.getValue());
    	param.put("PINAN_ASSET_TYPE", DictCode.平安资产类型.getValue());
        TrdSecOverdueDurationDto trdSecOverdueDurationDto = trdSecOverdueDurationMapper.selectNewBasicdata(param);
        trdSecOverdueDurationDto.setTradeDate(platformDateImpl.getSysDate());
        if(StringUtil.equals(trdSecOverdueDurationDto.getFinprodType2(), DictCodeList.平安资产类型_债券.getValue())){
        	trdSecOverdueDurationDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        }else if(StringUtil.equals(trdSecOverdueDurationDto.getFinprodType2(), DictCodeList.平安资产类型_存款.getValue())){
        	trdSecOverdueDurationDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_存款.getValue());
        }else if(StringUtil.equals(trdSecOverdueDurationDto.getFinprodType2(), DictCodeList.平安资产类型_项目类资产.getValue())){
        	trdSecOverdueDurationDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
        }else if(StringUtil.equals(trdSecOverdueDurationDto.getFinprodType2(), DictCodeList.平安资产类型_计划类资产_利率型.getValue())){
        	trdSecOverdueDurationDto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
        }
        return trdSecOverdueDurationDto;
    }
    
    /**
     * 新建利息明细信息
     * @param param
     * @return
     */
    public List<FinAssetIntInfo> selectNewInterestDetails(Map<String, Object> param){
    	param.put("PORTFOLIO_TYPE_01", DictCodeListExt1.组合类型_投资类组合.getValue());
    	param.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
    	param.put("Cdate", platformDateImpl.getSysDate());
    	param.put("RECE_TYPE_01", DictCodeListExt3.应收款类型_逾期未收本金.getValue());
    	param.put("RECE_TYPE_02", DictCodeListExt3.应收款类型_逾期未收利息.getValue());
    	param.put("RECE_TYPE_03", DictCodeListExt3.应收款类型_还本未收利息.getValue());
    	param.put("BUSI_TYPE_37", DictCodeList.业务类型_逾期交易.getValue());
    	param.put("TRADE_STATUS_01", DictCodeListExt1.交易状态_初始.getValue());
    	param.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
    	param.put("PS_T01", DictCodeList.交易方向_买入.getValue());
    	param.put("INPUT_TYPE_04", DictCodeListExt1.录入方式_批处理生成.getValue());
    	List<FinAssetIntInfo> trdSecOverdueDurationDto = trdSecOverdueDurationMapper.selectNewInterestDetails(param);
    	
    	for(FinAssetIntInfo finAssetIntInfo:trdSecOverdueDurationDto){
//    		理论本金罚息  本金总罚息*(未支付本金 - 当日本金)/本金总持仓  - 实付本金罚息 保留2位小数
    		finAssetIntInfo.setCapitalPenalty(ArithmeticUtil.subtract(ArithmeticUtil.divide(ArithmeticUtil.multiply(finAssetIntInfo.getCapitalPenaltyTotle(),ArithmeticUtil.subtract(finAssetIntInfo.getPrinAmtTheory(),finAssetIntInfo.getEarlyPrinAmtTheory())),finAssetIntInfo.getCapitalHoldPosition(),2), finAssetIntInfo.getActPenaltyIntBj()));
    		//理论利息罚息  利息总罚息*(收息未支付利息-当日利息)/利息总持仓 - 实付利息罚息  保留2位小数
    		finAssetIntInfo.setInterestPenalty(ArithmeticUtil.subtract(ArithmeticUtil.divide(ArithmeticUtil.multiply(finAssetIntInfo.getInterestPenaltyTotle(),ArithmeticUtil.subtract(finAssetIntInfo.getChargeUnpayInterest(),finAssetIntInfo.getEarlyChargeUnpayInterest())),finAssetIntInfo.getInterestHoldPosition(),2), finAssetIntInfo.getActPenaltyIntLx()));
    		//理论还本未支付利息  还本未支付利息总罚息*(还本未支付利息-当日还本未收利息)/还本未付息总持仓  -实付还本未付息罚息 保留2位小数
    		finAssetIntInfo.setRepaymentUnpayHoldPenalty(ArithmeticUtil.subtract(ArithmeticUtil.divide(ArithmeticUtil.multiply(finAssetIntInfo.getRepaymentUnpayHoldPenaltyTotle(),ArithmeticUtil.subtract(finAssetIntInfo.getRepaymentUnpayInterest(), finAssetIntInfo.getEarlyRepaymentUnpayInterest())),finAssetIntInfo.getRepaymentUnpayHoldPosition(),2), finAssetIntInfo.getActPenaltyIntHbWfx()));

    		//理论总罚息
    		finAssetIntInfo.setTheoryPenalty(ArithmeticUtil.doAdd(finAssetIntInfo.getCapitalPenalty(),finAssetIntInfo.getInterestPenalty(),finAssetIntInfo.getRepaymentUnpayHoldPenalty()));
    		//理论利息  收息未支付利息+还本未支付利息
    		finAssetIntInfo.setTheoryInt(ArithmeticUtil.doAdd(finAssetIntInfo.getRepaymentUnpayInterest(),finAssetIntInfo.getChargeUnpayInterest()));
    		//理论本息和
    		finAssetIntInfo.setTheoryAmt(ArithmeticUtil.doAdd(finAssetIntInfo.getPrinAmtTheory(),finAssetIntInfo.getTheoryInt(),finAssetIntInfo.getTheoryPenalty()));

    		//初始化  实收默认为理论
    		finAssetIntInfo.setPrinAmt(finAssetIntInfo.getPrinAmtTheory());
    		finAssetIntInfo.setIntAmt(finAssetIntInfo.getTheoryInt());
    		finAssetIntInfo.setPenaltyIntamt(finAssetIntInfo.getTheoryPenalty());
    		finAssetIntInfo.setTradeAmt(finAssetIntInfo.getTheoryAmt());
    		
    	}
    	
        return trdSecOverdueDurationDto;
    }

    public Page<SelectOptionDto> getFinprodMarketId(int page, int length, Map<String, Object> param) {
        return queryForPageForSelectReturn(page, length, () -> trdSecOverdueDurationMapper.getFinprodMarketId(param));
    }
    public Map<String, Object> genAssetTfInfo(Map<String, Object> param) throws InstantiationException, IllegalAccessException {
//        未支付本金
        BigDecimal theoryPrin = null;
//        未支付利息
        BigDecimal theoryInt = null;
        String bjBranch = (String)param.get("bjBranch");
        String liBranch = (String)param.get("liBranch");
        String finprodId = (String)param.get("finprodId");
        String portfolioId = (String)param.get("portfolioId");
        String secAcctId = (String)param.get("secAcctId");
        Date platDate = platformDateImpl.getSysDate();
//        1.1 未支付本金设置
//        if (传入.本金分支序号 不为空 时)
//	    调用应收款共通.应收款可用持仓查询,参数设置如下：
//		组合代码							:	画面.组合代码
//		证券管理账户/通道代码							:	画面.证券管理账户/通道代码
//		金融产品代码							:	画面.金融产品代码
//		分支序号							:	传入.本金分支序号
//		持仓类型							:	实际
//		日期							:	平台日
//
//	画面.未支付本金 = 上述查询的结果，查询不到时为0
//else
//	画面.未支付本金 = 0
        if(StringUtil.isNotEmpty(bjBranch)){
            if(basePtlRecePositionService != null){
//               应收款可用持仓查询
                param.put("branch",bjBranch);
                param.put("PLAT_DATE",platDate);
                List<PtlRecePosition> ptlRecePosition = basePtlRecePositionService.genPtlPeceAvailableIntPosition(param);
                if(CollectionUtils.isNotEmpty(ptlRecePosition)){
                    theoryPrin = ptlRecePosition.get(0).getAmount();
                }
            }
            if(theoryPrin == null){
                theoryPrin = BigDecimal.ZERO;
            }
        }
//        if (传入.利息分支序号 不为空 时)
//        调用应收款共通.应收款可用持仓查询,参数设置如下：
//        组合代码							:	画面.组合代码
//        证券管理账户/通道代码							:	画面.证券管理账户/通道代码
//        金融产品代码							:	画面.金融产品代码
//        分支序号							:	传入.利息分支序号
//        持仓类型							:	实际
//        日期							:	平台日
//
//        画面.未支付利息 = 上述查询的结果，查询不到时为0
//else
//        画面.未支付利息 = 0
        if(StringUtil.isNotEmpty(liBranch)){
            if(basePtlRecePositionService != null){
//                应收款可用持仓查询
//                theoryInt = basePtlRecePositionService.findUnpayPtlRecePosition(param);
                param.put("branch",liBranch);
                param.put("PLAT_DATE",platDate);
                List<PtlRecePosition> ptlRecePosition = basePtlRecePositionService.genPtlPeceAvailableIntPosition(param);
                if(CollectionUtils.isNotEmpty(ptlRecePosition)){
                    theoryInt = ptlRecePosition.get(0).getAmount();
                }
            }
            if(theoryInt == null){
                theoryInt = BigDecimal.ZERO;
            }
        }
//        还本未带息应收款查询
        if(StringUtil.isNotEmpty(liBranch)){
            Map<String,Object> lixiMap = new HashMap<>();
            lixiMap.put("branch",liBranch);
            lixiMap.put("finprodId",finprodId);
            lixiMap.put("portfolioId",portfolioId);
            lixiMap.put("secAcctId",secAcctId);
            lixiMap.put("platDate",platDate);
            List<String> 还本不带息分支序号 = basePtlRecePositionService.genLixiBranchList(lixiMap);
            if(CollectionUtils.isNotEmpty(还本不带息分支序号)){
//            还本不带息应收款查询
                for (String unBranch :还本不带息分支序号){
                    lixiMap.put("branch",unBranch);
                    lixiMap.put("PLAT_DATE",platDate);
                    List<PtlRecePosition> unpayPtlRecePosition = basePtlRecePositionService.genPtlPeceAvailableIntPosition(lixiMap);
                    for(PtlRecePosition ptlRecePosition:unpayPtlRecePosition){
                        theoryInt = ArithmeticUtil.doAdd(theoryInt,ptlRecePosition.getAmount());
                    }

                }
            }
        }

        BigDecimal theoryPenaltyInt=null;
        BigDecimal 理论本金罚息 = null;
        BigDecimal 理论利息罚息 = null;

        //罚息设置
        if(StringUtil.isNotEmpty(bjBranch)&&BigDecimal.ZERO.compareTo(theoryPrin)!=0){
//            应收款标的dto = 调用共通【应收款标的共通】.标的查询(传入.金融产品代码，传入.本金分支序号)
            Map<String,Object>ptlReceMap = new HashMap<>();
            ptlReceMap.put("branch",bjBranch);
            ptlReceMap.put("finprodId",finprodId);
            PtlReceInfo ptlReceInfo = basePtlReceInfoService.genPtlReceInfo(ptlReceMap);
            if(ptlReceInfo!=null && CollectionUtils.isNotEmpty(ptlReceInfo.getFinCashCalcRuleList())&&CollectionUtils.isNotEmpty(ptlReceInfo.getFinCashPlanList())){
//                调用估值利息算法
                List<PrincipalInfo>prinList = new ArrayList<>();
                PrincipalInfo principalInfo = new PrincipalInfo();
                principalInfo.setPrinAmount(theoryPrin);
                principalInfo.setChangeDate(ptlReceInfo.getMdate());
                prinList.add(principalInfo);
                List<QuoteInfo> quoteList = new ArrayList<>();
                ptlReceInfo.getFinCashCalcRuleList().forEach(item->{
                    QuoteInfo quoteInfo= new QuoteInfo();
                    quoteInfo.setAccountBasis(item.getBasis());
                    quoteInfo.setIssueDate(item.getEffDate());
                    quoteInfo.setInterest(item.getYield());
                    quoteList.add(quoteInfo);
                });
                CalCommonResult calRes = calCommonService.calCommon(ptlReceInfo.getMdate(),DateUtil.addDay(DateUtil.platDateFromCache(),-1),prinList,quoteList);
                理论本金罚息 = calRes.getTotalInterestAmt();
            }
            if(理论本金罚息 == null){
                理论本金罚息 = BigDecimal.ZERO;
            }
        }
       // 罚息设置
        if(StringUtil.isNotEmpty(liBranch)&&BigDecimal.ZERO.compareTo(theoryInt)!=0){
//            应收款标的dto = 调用共通【应收款标的共通】.标的查询(传入.金融产品代码，传入.本金分支序号)
            Map<String,Object>ptlReceMap = new HashMap<>();
            ptlReceMap.put("branch",liBranch);
            ptlReceMap.put("finprodId",finprodId);
            PtlReceInfo ptlReceInfo = basePtlReceInfoService.genPtlReceInfo(ptlReceMap);
            if(ptlReceInfo!=null && CollectionUtils.isNotEmpty(ptlReceInfo.getFinCashCalcRuleList())&&CollectionUtils.isNotEmpty(ptlReceInfo.getFinCashPlanList())){
//                调用估值利息算法
                List<PrincipalInfo>prinList = new ArrayList<>();
                PrincipalInfo principalInfo = new PrincipalInfo();
                principalInfo.setPrinAmount(theoryInt);
                principalInfo.setChangeDate(ptlReceInfo.getMdate());
                prinList.add(principalInfo);
                List<QuoteInfo> quoteList = new ArrayList<>();
                ptlReceInfo.getFinCashCalcRuleList().forEach(item->{
                    QuoteInfo quoteInfo= new QuoteInfo();
                    quoteInfo.setAccountBasis(item.getBasis());
                    quoteInfo.setIssueDate(item.getEffDate());
                    quoteInfo.setInterest(item.getYield());
                    quoteList.add(quoteInfo);
                });
                CalCommonResult calRes = calCommonService.calCommon(ptlReceInfo.getMdate(),DateUtil.addDay(DateUtil.platDateFromCache(),-1),prinList,quoteList);
                理论利息罚息 = calRes.getTotalInterestAmt();
            }
            if(理论利息罚息 == null){
                理论利息罚息 = BigDecimal.ZERO;
            }
        }
        theoryPenaltyInt = ArithmeticUtil.doAdd(理论本金罚息,理论利息罚息);
        Map<String,Object> map = new HashMap<>();
//        未支付本金
        map.put("theoryPrin",theoryPrin);
//        未支付利息
        map.put("theoryInt",theoryInt);
//        罚息
        map.put("theoryPenaltyInt",theoryPenaltyInt);
        map.put("theoryPenaltyIntBj",理论本金罚息);
        map.put("theoryPenaltyIntLx",理论利息罚息);
        return map;
    }

    /**
     *
     * @param param
     * @return
     */
    public TrdSecOverdueDurationDto findTFEffData(Map<String, Object> param) {
        return trdSecOverdueDurationMapper.findTFEffData(param);
    }

    /**
     *  组合信息查询
     * @param page 页数
     * @param length 长度
     * @param param 参数
     * @return 返回值
     */
    public Page<SelectOptionDto> selectPortfolioId(int page, int length, Map<String, Object> param) {
        param.put("POSITION_TYPE_SJ",DictCodeList.持仓类型_实际.getValue());
        param.put("YON_Y",DictCodeList.是否_是.getValue());
        param.put("PS_YSKZR",DictCodeList.交易方向_应收款项转入.getValue());
        param.put("BUSI_TYPE_YQJY",DictCodeList.业务类型_逾期交易.getValue());
        param.put("TRADE_STATUS_CS",DictCodeListExt1.交易状态_初始.getValue());
        param.put("YN_N",DictCodeListExt1.是否_否.getValue());
        param.put("RECE_TYPE_HBWSLX",DictCodeListExt3.应收款类型_还本未收利息.getValue());
        param.put("RECE_TYPE_YQWSBJ",DictCodeListExt3.应收款类型_逾期未收本金.getValue());
        param.put("RECE_TYPE_YQWSLX",DictCodeListExt3.应收款类型_逾期未收利息.getValue());
        setParams(param);
        Page<SelectOptionDto> retPage= this.queryForPageForSelectReturn(page, length,
                () -> trdSecOverdueDurationMapper.selectPortfolioId(param));
        if(CollectionUtils.isNotEmpty(retPage.getRoot())){
            retPage.getRoot().forEach(item->{
                if(item.getObject() != null){
//                    隐藏字段.本金理论罚息=本金总罚息*未支付本金/本金总持仓 -   实付本金罚息
//   隐藏字段.利息理论罚息=利息总罚息*收息未支付利息/利息总持仓 - 实付利息罚息  +利息总罚息*还本未支付利息/还本未付息总持仓 - 实付还本未付息罚息
//   画面.罚息	=	理论本金罚息 + 理论利息罚息 +理论还本未付利息罚息
//   画面.未支付利息	  =	收息未支付利息+还本未支付利息
//   画面.未支付本金	 =	上述查询.未支付本金
                    Map<String,Object>amtInfo = (Map)item.getObject();
                    BigDecimal theoryPrin = (BigDecimal) amtInfo.get("theoryPrin");
                    BigDecimal theoryIntSx = (BigDecimal) amtInfo.get("theoryIntSx");
                    BigDecimal theoryIntHb = (BigDecimal) amtInfo.get("theoryIntHb");
                    BigDecimal amountBj = (BigDecimal) amtInfo.get("amountBj");
                    BigDecimal amountLx = (BigDecimal) amtInfo.get("amountLx");
                    BigDecimal amountHbWfx = (BigDecimal) amtInfo.get("amountHbWfx");
                    BigDecimal penaltyIntBj = (BigDecimal) amtInfo.get("penaltyIntBj");
                    BigDecimal penaltyIntLx = (BigDecimal) amtInfo.get("penaltyIntLx");
                    BigDecimal penaltyIntHbWfx = (BigDecimal) amtInfo.get("penaltyIntHbWfx");
                    BigDecimal actPenaltyIntBj = (BigDecimal) amtInfo.get("actPenaltyIntBj");
                    BigDecimal actPenaltyIntLx = (BigDecimal) amtInfo.get("actPenaltyIntLx");
                    BigDecimal actPenaltyIntHbWfx = (BigDecimal) amtInfo.get("actPenaltyIntHbWfx");
                    Map<String,BigDecimal>retMap= new HashMap<>();
                    BigDecimal 本金理论罚息 = ArithmeticUtil.subtract(ArithmeticUtil.divide( ArithmeticUtil.multiply(penaltyIntBj,theoryPrin),amountBj),actPenaltyIntBj);
                    BigDecimal 利息理论罚息 = ArithmeticUtil.doAdd(ArithmeticUtil.subtract(ArithmeticUtil.divide(ArithmeticUtil.multiply(penaltyIntLx,theoryIntSx),amountLx,2),actPenaltyIntLx),
                            ArithmeticUtil.subtract(ArithmeticUtil.divide(ArithmeticUtil.multiply(penaltyIntLx,theoryIntHb),amountHbWfx,2),actPenaltyIntHbWfx)
                    );
                    BigDecimal 罚息 = ArithmeticUtil.doAdd(本金理论罚息,利息理论罚息,penaltyIntHbWfx);
                    BigDecimal 未支付利息 = ArithmeticUtil.doAdd(theoryIntSx,theoryIntHb);
                    BigDecimal 未支付本金 = theoryPrin;
                    Map<String,Object> map = new HashMap<>();
//        未支付本金
                    map.put("theoryPrin",未支付本金);
//        未支付利息
                    map.put("theoryInt",未支付利息);
//        罚息
                    map.put("theoryPenaltyInt",罚息);
                    map.put("theoryPenaltyIntBj",本金理论罚息);
                    map.put("theoryPenaltyIntLx",利息理论罚息);
                    item.setObject(map);
                }
            });
        }
        return retPage;
    }

    /**
     * 转让完成complete
     * @param durationDto 数据dto
     * @return 处理结果
     */
    public String doCompleteZr(TrdSecOverdueDurationZrDto durationDto) {
        String updateUser = durationDto.getUpdateUser();
        Date updateTime = new Date();
        if (BussinessConstant.REVOKE.equals(durationDto.getOperateType())) {
            List<StlPayOrders> ordersTotal = periodCommonMapper.findOrdersByOrderId(durationDto.getBusiCode());
            investPeriodCommonService.deleteByWorkflowId(durationDto.getWorkflowId());
            List<String> subBizCodes = periodCommonMapper.findSubTotalTradeId(durationDto.getBusiCode());
            if (subBizCodes != null && !subBizCodes.isEmpty()) {
                for (String subBizCode : subBizCodes) {
                    investPeriodCommonService.revoke汇总和交易相关表(subBizCode,
                            updateUser,true, durationDto.getRevokeReason(), null, durationDto.getActPortfolioId(),false);
                }
            }
            investPeriodCommonService.revoke汇总和交易相关表(durationDto.getBusiCode(),
                    updateUser,true, durationDto.getRevokeReason(), null, durationDto.getActPortfolioId(),false);
            调估值接口Revoke(durationDto,ordersTotal);
            return Boolean.TRUE.toString();
        }
		investPeriodCommonService.doInsertAndDelete(durationDto.getBaseTotalTradeInfoDto(), updateUser, updateTime,
				durationDto.getWorkflowId());

		List<BaseTradeInfoDto> baseTradeInfoDtos = durationDto.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
		if (baseTradeInfoDtos != null && !baseTradeInfoDtos.isEmpty()) {
			baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
                List<BaseTradeInfoDto> tradeList = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(baseTradeInfoDto.getBaseTradeInfoDtoList())){
                    tradeList.addAll(baseTradeInfoDto.getBaseTradeInfoDtoList());
                }
                if(CollectionUtils.isNotEmpty(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList())){
                    tradeList.addAll(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList());
                }
                tradeList.forEach(tradeInfo->{
                    TrdProductDeal deal  = tradeInfo.getTrdProductDeal();
                    if(StringUtil.equals(deal.getPs(),DictCodeList.交易方向_卖出.getValue())||StringUtil.equals(deal.getPs(),DictCodeList.交易方向_应收款项转出.getValue())){
//                        String portfolioId = durationDto.getPortfolioId();
                        调估值接口(tradeInfo, durationDto.getActPortfolioId());
//                        if (!StringUtil.equals(durationDto.getPortfolioId(), durationDto.getActPortfolioId())) {
//                            调估值接口(tradeInfo, durationDto.getActPortfolioId());
//                        }
                    }
                    else{
//                    转入
                        String 交易对手 = durationDto.getCounterId();
                        调估值接口(tradeInfo, 交易对手);
//                        String 母交易对手 = AllTradeCommonUtil.exchangePortfolio(交易对手);
//                        if(!StringUtil.equals(交易对手,母交易对手)){
//                            调估值接口(tradeInfo, 交易对手);
//                        }
//                        else{
//                            调估值接口(tradeInfo, 交易对手);
//                        }
                    }
                });
			});
		}
        return Boolean.TRUE.toString();
    }

    private void 调估值接口Revoke(TrdSecOverdueDurationZrDto durationDto, List<StlPayOrders> ordersTotal) {
        Map<String, Object> map = new HashMap<>();
        map.put("totleTradeId", durationDto.getBusiCode());
        List<TrdProductDeal> trdProductDeals1 = periodCommonMapper.findDealByTotalIdForInterf(map);
        if(CollectionUtils.isEmpty(trdProductDeals1)){
            return;
        }
        List<TrdProductDealAdd> trdProductDealAdds = periodCommonMapper.findDealAddByTradeIds(trdProductDeals1);
        List<PtlTrade> ptlTrades = periodCommonMapper.findPtlTradeByTradeIds(trdProductDeals1);
        // 所有交易生效后调用的共通方法
        for (TrdProductDeal baseDto : trdProductDeals1) {
            AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
            inputDto.setProdid(durationDto.getPortfolioId());
            inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
//                子组合估值调用
            if(!StringUtil.equals(durationDto.getActPortfolioId(),durationDto.getPortfolioId())){
                inputDto.setActPortfolioId(durationDto.getActPortfolioId());
            }
            if(StringUtil.equals(baseDto.getChlId(),"999999")){
                baseDto.setChlId(null);
            }
            baseDto.setTradeStatus(DictCodeListExt1.交易状态_撤销.getValue());
            inputDto.setTrdProductDeal(baseDto);
            inputDto.setTrdProductDealAdd(investPeriodCommonService.getAdd(trdProductDealAdds, baseDto.getTradeId()));
            inputDto.setRepoType(null);
            StlPayOrders stlPayOrders = ordersTotal.get(0);
            inputDto.setIsNotAutoGenerateReverseDeal(DictCodeList.是否_是.getValue());
            stlPayOrders.setSettleAmt(baseDto.getTradeAmt());
            inputDto.setStlPayOrders(stlPayOrders);
            List<PtlTrade> ptlTradeList = new ArrayList<>();
            for (PtlTrade ptlTrade : ptlTrades) {
                if (baseDto.getTradeId().equals(ptlTrade.getBusiNo())) {
                    ptlTradeList.add(ptlTrade);
                }
            }
            inputDto.setPtlTradeList(ptlTradeList);
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        }
    }

    private void 调估值接口(BaseTradeInfoDto baseTradeInfoDto, String portfolioId) {
        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
        inputDto.setProdid(portfolioId);
        if(!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeInfoDto.getTrdTradeFeeDetail())) {
            inputDto.setTrdTradeFeeDetail(baseTradeInfoDto.getTrdTradeFeeDetail().get(0));
        }
        inputDto.setIsNotAutoGenerateReverseDeal(DictCodeList.是否_是.getValue());
        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
        if (baseTradeInfoDto.getTrdProductDeal() != null) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal, baseTradeInfoDto.getTrdProductDeal());
            inputDto.setTrdProductDeal(deal);
            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
            inputDto.setPtlTradeList(baseTradeInfoDto.getPtlTrade());
            inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
            // 存续期增加通道费 add by renping 20191018 start
            inputDto.setTrdTradeFeeDetail(null);
            // 存续期增加通道费 add by renping 20191018 end
        }
        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
            TrdProductDeal deal = new TrdProductDeal();
            BeanUtil.copyPropertiesIgnoreNull(deal,
                    baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
            inputDto.setTrdProductDeal(deal);
            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
            inputDto.setPtlTradeList(baseTradeInfoDto.getBaseTrade().getPtlTrade());
            inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
            AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
        }
    }

    /**
     *  匹配是否是证券托管户
     * @param param 证券托管户信息
     * @return 统计数量
     */
    public int isSecManage(Map <String ,Object> param) {
        return trdSecOverdueDurationMapper.isSecManage(param);
    }
    
	public String doComplete(TrdSecOverdueDurationDto durationDto) {
        String updateUser = durationDto.getUpdateUser();
        Date updateTime = new Date();
        if (BussinessConstant.REVOKE.equals(durationDto.getOperateType())) {
            investPeriodCommonService.deleteByWorkflowId(durationDto.getWorkflowId());
            investPeriodCommonService.revoke汇总和交易相关表(durationDto.getBusiCode(),
                updateUser, durationDto.getRevokeReason(), null, null);
            // 删除其他表
            return Boolean.TRUE.toString();
        }
        investPeriodCommonService.doInsertAndDelete(durationDto.getBaseTotalTradeInfoDto(), updateUser, updateTime,
            durationDto.getWorkflowId());

        //组合层
        List<BaseTradeInfoDto> baseTradeInfoDtos = durationDto.getBaseTotalTradeInfoDto().getBaseTradeInfoDtoList();
        if (baseTradeInfoDtos != null && !baseTradeInfoDtos.isEmpty()) {
            baseTradeInfoDtos.forEach(baseTradeInfoDto -> {
                String portfolioId = baseTradeInfoDto.getTrdFinProductDealTotal().getPortfolioId();
                调估值接口2(baseTradeInfoDto, portfolioId);
                //子组合层
                baseTradeInfoDto.getBaseTradeInfoDtoList().forEach(baseTradeInfoDto1 -> {
                    String portfolioId1 = null;
                    //有利息取利息信息中组合id
                    if(baseTradeInfoDto1.getTrdProductDealAdd() != null){
                    	portfolioId1 = baseTradeInfoDto1.getTrdProductDealAdd().getPortfolioId();
                    }
                    //有本金取本金信息中组合id
                    if(StringUtil.isEmpty(portfolioId1)&&baseTradeInfoDto1.getBaseTrade()!=null&&baseTradeInfoDto1.getBaseTrade().getTrdProductDealAdd() != null){
                    	portfolioId1 = baseTradeInfoDto1.getBaseTrade().getTrdProductDealAdd().getPortfolioId();
                    }
                    //有罚息 取罚息中组合id
                    if(StringUtil.isEmpty(portfolioId1)&&!StringUtil.isEmptyList(baseTradeInfoDto1.getPenaltyIntBaseTradeInfoDtoList())){
                    	portfolioId1 = baseTradeInfoDto1.getPenaltyIntBaseTradeInfoDtoList().get(0).getTrdProductDealAdd().getPortfolioId();
                    }
                    //子组合id不为空调用估值
                    if(StringUtil.isNotEmpty(portfolioId1)){
                    	调估值接口2(baseTradeInfoDto1, portfolioId1); // 子组合调用估值接口
                    }
                });

            });
        }
        return Boolean.TRUE.toString();
    }
	
	 private void 调估值接口2(BaseTradeInfoDto baseTradeInfoDto, String portfolioId) {
	        AllTradeCommonInputDto inputDto = new AllTradeCommonInputDto();
	        inputDto.setProdid(portfolioId);
	        //update zhangweiqi 通道费修改 2019-10-16 begin
	        if(!com.joyintech.framework.util.StringUtil.isEmptyList(baseTradeInfoDto.getTrdTradeFeeDetail())) {
	            inputDto.setTrdTradeFeeDetail(baseTradeInfoDto.getTrdTradeFeeDetail().get(0));
	        }
	        //update zhangweiqi 通道费修改 2019-10-16 end
	        inputDto.setProdFinprodType(DictCodeListExt1.金融产品类型_投资组合.getValue());
	        //利息
	        if (baseTradeInfoDto.getTrdProductDeal() != null) {
	            TrdProductDeal deal = new TrdProductDeal();
	            BeanUtil.copyPropertiesIgnoreNull(deal, baseTradeInfoDto.getTrdProductDeal());
	            inputDto.setTrdProductDeal(deal);
	            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getTrdProductDealAdd());
	            inputDto.setPtlTradeList(baseTradeInfoDto.getPtlTrade());
                inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                inputDto.setTrdTradeFeeDetail(null);//利息存在时，后续罚息，本金不会存在通道费
	        }
	      
	        //罚息
	        if(baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList() != null &&  baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().size()>0){
	        	TrdTradeFeeDetail detail = inputDto.getTrdTradeFeeDetail();
	        	inputDto.setTrdTradeFeeDetail(null);//此处清空，因为罚息存在时，本金不会存在通道费
	        	baseTradeInfoDto.getPenaltyIntBaseTradeInfoDtoList().forEach(item->{
	        		if(StringUtil.isNotEmpty(detail)&&StringUtil.equals(detail.getTradeId(), item.getTrdProductDeal().getTradeId())){
	        			inputDto.setTrdTradeFeeDetail(detail);
	        		}
	        		inputDto.setTrdProductDeal(item.getTrdProductDeal());
	        		inputDto.setTrdProductDealAdd(item.getTrdProductDealAdd());
	        		inputDto.setPtlTradeList(item.getPtlTrade());
                    inputDto.setStlPayOrders(item.getStlPayOrders());
                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                    inputDto.setTrdTradeFeeDetail(null);//此处清空，当前罚息已经挂除，不影响其他罚息
	        	});
	        }
	        
	        //本金
	        if (baseTradeInfoDto.getBaseTrade() != null && baseTradeInfoDto.getBaseTrade().getTrdProductDeal() != null) {
	            TrdProductDeal deal = new TrdProductDeal();
	            BeanUtil.copyPropertiesIgnoreNull(deal,
	                baseTradeInfoDto.getBaseTrade().getTrdProductDeal());
	            inputDto.setTrdProductDeal(deal);
	            inputDto.setTrdProductDealAdd(baseTradeInfoDto.getBaseTrade().getTrdProductDealAdd());
	            inputDto.setPtlTradeList(baseTradeInfoDto.getBaseTrade().getPtlTrade());
                inputDto.setStlPayOrders(baseTradeInfoDto.getStlPayOrders());
                AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
                inputDto.setTrdTradeFeeDetail(null);//本金清除通道费信息
	        }
	        //还本不带息
	          
	        if(baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList() != null &&  baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().size()>0){
	        	baseTradeInfoDto.getRepaymentUnpayInterestBaseTradeInfoDtoList().forEach(item->{
	        		inputDto.setTrdProductDeal(item.getTrdProductDeal());
	        		inputDto.setTrdProductDealAdd(item.getTrdProductDealAdd());
	        		inputDto.setPtlTradeList(item.getPtlTrade());
                    inputDto.setStlPayOrders(item.getStlPayOrders());
                    AllTradeCommonUtil.handleAfterTradeComplete(inputDto);
	        	});
	        }
	    }
	 
	 

	 /**
	  * 撤销
	  * @param trdSecOverdueDurationDto
	  * @return
	  */
	 public String doRevoke(TrdSecOverdueDurationDto trdSecOverdueDurationDto) {
		 Map<String, Object> params = new HashMap<>();
		 params.put("bizCode", trdSecOverdueDurationDto.getBusiCode());
		 params.put("workflowId", trdSecOverdueDurationDto.getWorkflowId());
		 params.put("workflowStatus", DictCodeList.数据状态_删除中.getValue());
		 params.put("user", trdSecOverdueDurationDto.getCreateUser());
		 params.put("dept", trdSecOverdueDurationDto.getCreateDept() == null ? "dept" : trdSecOverdueDurationDto.getCreateDept());
		 params.put("time", trdSecOverdueDurationDto.getCreateTime());
		 params.put("revokeReason", trdSecOverdueDurationDto.getRevokeReason());
		 investPeriodCommonService.insertWflTable(params);
		 return Boolean.TRUE.toString();
	 }

	 public TrdSecOverdueDurationDto selectCashIdsByBranchAll(Map<String,Object> map){
         TrdSecOverdueDurationDto trdSecOverdueDurationDto = trdSecOverdueDurationMapper.selectCashIdsByBranchAll(map);
         return  trdSecOverdueDurationDto;
     }

    public TrdSecOverdueDurationDto selectWithOutBranchByLiBranch(Map<String,Object> map){
        TrdSecOverdueDurationDto trdSecOverdueDurationDto = trdSecOverdueDurationMapper.selectWithOutBranchByLiBranch(map);
        return  trdSecOverdueDurationDto;
    }
	public TrdSecOverdueDurationDto findSecOverdueData(Map<String, Object> param) {
		TrdSecOverdueDurationDto trdSecOverdueDurationDto = trdSecOverdueDurationMapper.findSecOverdueData(param);
        return  trdSecOverdueDurationDto;
	}
    /**
     * 查询到期资产转让后续交易
     * @param param 参数
     * @return 查询结果
     */
    public int checkCounterFlowDeal(Map<String, Object> param) {
        param.put("TRADE_STATUS_CS", DictCodeListExt1.交易状态_初始.getValue());
        param.put("YON_N", DictCodeListExt1.是否_否.getValue());
        param.put("PINAN_BUSINESS_TYPE_YDQZCCXQ", DictCodeListExt3.平安业务类型_已到期资产存续期.getValue());
        param.put("PINAN_BUSINESS_TYPE_YDQZCZR", DictCodeListExt3.平安业务类型_已到期资产转让.getValue());
        param.put("transferDate",DateUtil.parse((String)param.get("transferDate")));
        param.put("PS_MC", DictCodeList.交易方向_卖出.getValue());
	     return trdSecOverdueDurationMapper.checkCounterFlowDeal(param);
    }

    /**
     * 根据汇总的汇总交易编号查询有效的数据
     * @param busiCode
     * @return
     */
    public TrdSecOverdueDurationDto findEffectiveData(String busiCode) {
        return trdSecOverdueDurationMapper.findEffectiveData(busiCode);
    }
}