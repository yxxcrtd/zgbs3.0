/*
 * 文件名：TrdRepoTradeService003.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2018年12月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.invest.common.trade.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrEntrustAddMapper;
import com.joyintech.tams.common.base.dao.EtrEntrustPledgeMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductAddMapper;
import com.joyintech.tams.common.base.entity.EtrEntrustAdd;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.constant.InterfCommonConstant;
import com.joyintech.tams.invest.common.dto.common.BussRepoSecDto;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.service.MapperDelegateService;
import com.joyintech.tams.invest.common.trade.dao.BussRepoSecMapper;
import com.joyintech.tams.invest.common.trade.dto.PledgeInsertDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 质押劵管理共通组件-微服务Service
 */
@Service
public class BussRepoSecService extends BaseService<PledgeBondInfoDto>{

    @Autowired
    private IPlatformDate platformDateImpl;

    /**
     * TrdRepoTradeMapper
     */
    @Autowired
    private BussRepoSecMapper bussRepoSecMapper;

    /**
     * MapperDelegateService
     */
    @Autowired
    private MapperDelegateService mapperDelegateService;
    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

    @Autowired
    private EtrEntrustPledgeMapper etrEntrustPledgeMapper;

    @Autowired
    private EtrEntrustAddMapper etrEntrustAddMapper;

    /**
     * 债券代码：下拉框
     *
     * @param param 画面参数
     * @return 债券代码：下拉框
     */
    public List<SelectOptionDto> getBondCode(Map<String, Object> param) {
        param.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        param.put("windtypeone", StringUtil.splitToList(GlobalConstant.COMMA, (String) param.get("bussRepoSec.bondType")));
        //add by meifaxin 2019092 区分银行间 start
        param.put("tradePlace", param.get("tradePlace")==null?null:param.get("tradePlace").toString());
        //add by meifaxin 20190928 区分银行间 end
        return bussRepoSecMapper.getBondCode(param);
    }

    /**
     * 债券质押回购多组合新增
     * 张庚林
     * 20190723
     */   
    public PledgeBondInfoDto portfolioName(Map<String, Object> param) {
        return bussRepoSecMapper.portfolioName(param);
    }

    /**
     * 通过entrustId/bizCode检索已质押券信息
     *
     * @param params 画面参数
     * @return List<BussRepoSecDto>
     */
    public BussRepoSecDto selectBussRepoSec(Map<String, Object> params) {
        BussRepoSecDto bussRepoSecDto = new BussRepoSecDto();
        String ps = (String) params.get("ps");
        String entrustId = (String) params.get("entrustId");
        String bussBondType =(String) params.get("bussBondType");
        List<String> entrustIdList = StringUtil.splitToList(GlobalConstant.COMMA, entrustId);
        List<PledgeBondInfoDto> pledgeList = new ArrayList<>();
        new ArrayList<>();
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(ps)||DictCodeList.回购拆借方向_融入.getValue().equals(ps)) {
            params.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
            params.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
            params.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
            params.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
            params.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
            params.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
            params.put("plateDate", platformDateImpl.getSysDateStr());
            if(entrustIdList.size()>1){
                params.put("entrustIdList", entrustIdList);
                pledgeList = bussRepoSecMapper.selectBussRepoSecPtlSellRepo(params);
            }else{
                pledgeList = bussRepoSecMapper.selectBussRepoSecSellRepo(params);
            }
        } else if (DictCodeList.回购拆借方向_逆回购.getValue().equals(ps)||DictCodeList.回购拆借方向_融出.getValue().equals(ps)) {
            params.put("plateDate", platformDateImpl.getSysDateStr());
            params.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
            params.put("YES_OR_NO_N", DictCodeList.是否_否.getValue());
            params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            if(entrustIdList.size()>1){
                params.put("entrustIdList", entrustIdList);
                pledgeList = bussRepoSecMapper.selectBussRepoSecPtlAntiRepo(params);
            }else{
                pledgeList = bussRepoSecMapper.selectBussRepoSecAntiRepo(params);
            }
        }
        BigDecimal totalPrinAmt = BigDecimal.ZERO;
        BigDecimal zzdPrinAmt = BigDecimal.ZERO;
        BigDecimal sqsPrinAmt = BigDecimal.ZERO;
        for (PledgeBondInfoDto pbi : pledgeList) {
            totalPrinAmt = ArithmeticUtil.doAdd(totalPrinAmt, ArithmeticUtil.multiply(pbi.getPledgePrin(), pbi.getPledgeRatio()).divide(new BigDecimal("100")));
            if("01".equals(pbi.getRepRegistOrg())) {
                zzdPrinAmt = ArithmeticUtil.doAdd(zzdPrinAmt, ArithmeticUtil.multiply(pbi.getPledgePrin(), pbi.getPledgeRatio()).divide(new BigDecimal("100")));
            }
            if("02".equals(pbi.getRepRegistOrg())) {
                sqsPrinAmt = ArithmeticUtil.doAdd(sqsPrinAmt, ArithmeticUtil.multiply(pbi.getPledgePrin(), pbi.getPledgeRatio()).divide(new BigDecimal("100")));
            }
        }
        bussRepoSecDto.setPledgeBondList(pledgeList);
        bussRepoSecDto.setTotalPrinAmt(totalPrinAmt);
        bussRepoSecDto.setZzdTotalPrinAmt(zzdPrinAmt);
        bussRepoSecDto.setSqsTotalPrinAmt(sqsPrinAmt);
        //债券借贷交易设置质押劵置换安排字段信息
        if(StringUtils.isNotBlank(bussBondType)){
            FinTradeProductAdd finTradeProductAdd = new FinTradeProductAdd();
            finTradeProductAdd.setFinprodId((String) params.get("bizCode"));
            finTradeProductAdd.setBranch(InterfCommonConstant.分支序号_默认);
            finTradeProductAdd = finTradeProductAddMapper.selectByPrimaryKey(finTradeProductAdd);
            if(finTradeProductAdd!=null){
            bussRepoSecDto.setExchangePledge(finTradeProductAdd.getExchangePledge());
            }
        }
        return bussRepoSecDto;
    }

    /**
     * 根据画面条件检索可质押券信息
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public List<PledgeBondInfoDto> selectAvaPledgeBondDetailList(Map<String, Object> params) {
        String ps = (String) params.get("ps");
        String bondType = (String) params.get("bondType");
        String tradeMarket = (String) params.get("tradeMarket");
        if(StringUtil.isNotEmpty(bondType)){
            params.put("bondTypeArr", StringUtil.split(bondType,","));
        }
        List<PledgeBondInfoDto> resultList = new ArrayList<>();
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(ps)||DictCodeList.回购拆借方向_融入.getValue().equals(ps)) {
            params.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
            params.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
            params.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
            params.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
            params.put("POSITION_TYPE_06", DictCodeList.持仓类型_融出.getValue());
            params.put("tradeMarket",tradeMarket);
            params.put("plateDate", platformDateImpl.getSysDateStr());
            params.put("repRegistOrg", StringUtil.splitToList(GlobalConstant.COMMA, (String) params.get("repRegistOrg")));
            params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            //产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201116 start
            //resultList = bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepo(params);
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(ps)&&StringUtil.equals(params.get("pageFromTz"), "TrdRepoTrade002")){
            	resultList = bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepoForEtrPledge(params);
            }else{
            	resultList = bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepo(params);
            }
            //产品化_质押式回购交易界面,正回购时：点击添加后的可质押券范围为投资经理移入可质押库的、该组合下的债券，不可选不在质押库里债券 add by zhangpei 20201116 end
            if (params.get("isWithInvAim") != null && (Boolean) params.get("isWithInvAim") && resultList != null && !resultList.isEmpty()) {
                // 用融入的持仓（投资目的999999）减去卖空和卖空可用的持仓，并且最后结果没有卖空的数据
                List<PledgeBondInfoDto> resultList1 = new ArrayList<>();
                PledgeBondInfoDto 融入券 = null;
                PledgeBondInfoDto 卖空 = null;
                for (PledgeBondInfoDto pledgeBondInfoDto : resultList) {
                    if ("999999".equals(pledgeBondInfoDto.getInvAim())) {
                        融入券 = pledgeBondInfoDto;
                    }
                    if (DictCodeListExt1.投资目的_卖空.getValue().equals(pledgeBondInfoDto.getInvAim())) {
                        卖空 = pledgeBondInfoDto;
                    }

                    if (!DictCodeListExt1.投资目的_卖空.getValue().equals(pledgeBondInfoDto.getInvAim())) {
                        resultList1.add(pledgeBondInfoDto);
                    }
                }
                if (融入券 != null && 卖空 != null) {
                    融入券.setAvaPledgePrin(ArithmeticUtil.doAdd(融入券.getAvaPledgePrin(), 卖空.getAvaPledgePrin()));
                }
                return resultList;
            }
        } else if (DictCodeList.回购拆借方向_逆回购.getValue().equals(ps)||DictCodeList.回购拆借方向_融出.getValue().equals(ps)) {
            params.put("plateDate", platformDateImpl.getSysDateStr());
            params.put("repRegistOrg", StringUtil.splitToList(GlobalConstant.COMMA, (String) params.get("repRegistOrg")));
            params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            params.put("tradeMarket",tradeMarket);
            resultList = bussRepoSecMapper.selectAvaPledgeBondDetailListAntiRepo(params);
        }
        return resultList;
    }
    
    
    /**
     * 根据画面条件检索可质押券信息
     *
     * @param params 画面参数
     * @return List<PledgeBondInfoDto>
     */
    public Page<PledgeBondInfoDto> selectAvaPledgeBondDetailPage(Map<String, Object> params,int page,int length) {
        String ps = (String) params.get("ps");
        String bondType = (String) params.get("bondType");
        String tradeMarket = (String) params.get("tradeMarket");
        if(StringUtil.isNotEmpty(bondType)){
            params.put("bondTypeArr", StringUtil.split(bondType,","));
        }
        //CFETS质押式回购报价界面，质押券维护界面 仅展示银行间的债券
        if(params.get("pageFromTz") != null && (StringUtils.equals(params.get("pageFromTz").toString(),"CfetsRepoDealQuotedPrice002")
        		||StringUtils.equals(params.get("pageFromTz").toString(),"EtrIbRepoinfo002")
        		||StringUtils.equals(params.get("pageFromTz").toString(),"EtribRepoInfo003"))){
            tradeMarket = DictCodeList.交易流通场所_银行间债券市场.getValue();
        }
        Page<PledgeBondInfoDto> resultList = new Page<>();
        if (DictCodeList.回购拆借方向_正回购.getValue().equals(ps)||DictCodeList.回购拆借方向_融入.getValue().equals(ps)) {
            params.put("POSITION_TYPE_01", DictCodeList.持仓类型_可用.getValue());
            params.put("POSITION_TYPE_03", DictCodeList.持仓类型_实际.getValue());
            params.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
            params.put("POSITION_TYPE_05", DictCodeList.持仓类型_融入.getValue());
            params.put("POSITION_TYPE_06", DictCodeList.持仓类型_融出.getValue());
            params.put("tradeMarket",tradeMarket);
            params.put("plateDate", platformDateImpl.getSysDateStr());
            params.put("repRegistOrg", StringUtil.splitToList(GlobalConstant.COMMA, (String) params.get("repRegistOrg")));
            params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            
            // add by etliu 20210303 银行间上下行 start
//            param.ignoreFlowTradeId = ignoreFlowTradeId;
            if (StringUtil.isNotBlank((String) params.get("ignoreFlowTradeId"))) {
                params.put("folwtradeId", (String) params.get("ignoreFlowTradeId"));
            }
            // add by etliu 20210303 银行间上下行 end
            
            //resultList = this.queryForPage(page, length,() -> bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepo(params));
            //resultList = bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepo(params);
            //银行间上下行多组合 正回购
            if(DictCodeList.回购拆借方向_正回购.getValue().equals(ps)&&
            		(StringUtil.equals(params.get("pageFromTz"), "CfetsRepoDealQuotedPrice003")
            				||StringUtil.equals(params.get("pageFromTz"), "EtrIbRepoinfo002")
            				||StringUtil.equals(params.get("pageFromTz"), "EtribRepoInfo003")
            				)){
                String portfolioId = (String)params.get("portfolioId");
                List<String> portfolioIds = Arrays.asList(portfolioId.split(","));
                if(StringUtil.isNotEmpty(portfolioIds)&&portfolioIds.size()>0){
                    params.put("portfolioIds", Arrays.asList(portfolioId.split(",")));
                    resultList = this.queryForPage(page, length,() -> bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepoForBankDzh(params));
                }
            }
            else{
            	resultList = this.queryForPage(page, length,() -> bussRepoSecMapper.selectAvaPledgeBondDetailListSellRepo(params));
            }
            if (params.get("isWithInvAim") != null && (Boolean) params.get("isWithInvAim") && resultList != null && !resultList.getRoot().isEmpty()) {
                // 用融入的持仓（投资目的999999）减去卖空和卖空可用的持仓，并且最后结果没有卖空的数据
                List<PledgeBondInfoDto> resultList1 = new ArrayList<>();
                PledgeBondInfoDto 融入券 = null;
                PledgeBondInfoDto 卖空 = null;
                for (PledgeBondInfoDto pledgeBondInfoDto : resultList.getRoot()) {
                    if ("999999".equals(pledgeBondInfoDto.getInvAim())) {
                        融入券 = pledgeBondInfoDto;
                    }
                    if (DictCodeListExt1.投资目的_卖空.getValue().equals(pledgeBondInfoDto.getInvAim())) {
                        卖空 = pledgeBondInfoDto;
                    }

                    if (!DictCodeListExt1.投资目的_卖空.getValue().equals(pledgeBondInfoDto.getInvAim())) {
                        resultList1.add(pledgeBondInfoDto);
                    }
                }
                if (融入券 != null && 卖空 != null) {
                    融入券.setAvaPledgePrin(ArithmeticUtil.doAdd(融入券.getAvaPledgePrin(), 卖空.getAvaPledgePrin()));
                }
                return resultList;
            }
        } else if (DictCodeList.回购拆借方向_逆回购.getValue().equals(ps)||DictCodeList.回购拆借方向_融出.getValue().equals(ps)) {
            params.put("plateDate", platformDateImpl.getSysDateStr());
            params.put("repRegistOrg", StringUtil.splitToList(GlobalConstant.COMMA, (String) params.get("repRegistOrg")));
            params.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
            params.put("tradeMarket",tradeMarket);
            resultList = this.queryForPage(page,length,() -> bussRepoSecMapper.selectAvaPledgeBondDetailListAntiRepo(params));
            //resultList = bussRepoSecMapper.selectAvaPledgeBondDetailListAntiRepo(params);
        }
        if(StringUtil.isNotEmpty(resultList) && params.get("pageFromTz") != null
                && ((StringUtils.equals(params.get("pageFromTz").toString(),"CfetsRepoDealQuotedPrice002"))
                ||(StringUtils.equals(params.get("pageFromTz").toString(),"CfetsRepoDealQuotedPrice003")))){
            String entrustId = (String) params.get("entrustId");
            if(entrustId.endsWith(","))entrustId=entrustId.substring(0,entrustId.length()-1);
            List<String> finprodIdList = new ArrayList<>();
            for(String entrustIdObj:entrustId.split(",")){
                if(entrustIdObj != null&& !"".equals(entrustIdObj)) {
                    EtrEntrustAdd etrEntrustAdd = etrEntrustAddMapper.selectByPrimaryKey(entrustIdObj);
                    finprodIdList.addAll(bussRepoSecMapper.selectFinprodIdByPledge(etrEntrustAdd.getPledgeStockId()));
                }
            }
            List<PledgeBondInfoDto> rootList =resultList.getRoot().stream().filter(item->finprodIdList.contains(item.getFinprodId())).collect(Collectors.toList());
            resultList.setRoot(rootList);
        }
        return resultList;
    }

    /**
     * 撤销委托/回购交易时，删除回购质押品信息
     *
     * @param param 删除参数
     * @return successCount
     * @see
     * @since 1.0
     */
    public int deletePledgeBond(Map<String, Object> param) {
        String pageFrom = (String) param.get("pageFrom");
        String bizCode = (String) param.get("bizCode");
        if (StringUtil.isNotEmpty(pageFrom) && StringUtil.isNotEmpty(bizCode)) {
        	if(pageFrom.startsWith("TrdRepoTrade00")) {
        		param.put("pageFrom","TrdRepoTrade002");
        	}
            return bussRepoSecMapper.deletePledgeBond(param);
        } else {
            return 0;
        }
    }

    public int insert(PledgeInsertDto pledgeInsertDto) {
        List<Integer> result =  mapperDelegateService.insertAutoClassifyBatch(pledgeInsertDto.getEtrPledgeList(),
                pledgeInsertDto.getFinDealPledgeList());
        int ret = 0;
        for (Integer integer : result) {
            ret = ret + integer;
        }
        return ret;
    }

    /**
     * 质押券置换换出列表按投资目的获取数据
     * nixiukuan
     * 20191129
     */
    public List<PledgeBondInfoDto> selectOutPledgeBondDetailList(Map<String, Object> param) {
        List<PledgeBondInfoDto> resultList = new ArrayList<>();
        param.put("POSITION_TYPE_04", DictCodeList.持仓类型_质押.getValue());
        param.put("plateDate", platformDateImpl.getSysDateStr());
        resultList=bussRepoSecMapper.selectOutPledgeBondDetailList(param);
        return resultList;
    }

    /**
     *查询证券账户信息
     * @param params
     * @return
     */
    public String querySecAcctId(Map<String, Object> params){
        return bussRepoSecMapper.querySecAcctId(params);
    }
}
