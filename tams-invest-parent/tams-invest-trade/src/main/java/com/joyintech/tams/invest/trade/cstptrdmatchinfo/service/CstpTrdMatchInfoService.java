package com.joyintech.tams.invest.trade.cstptrdmatchinfo.service;

import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.export.ExportParam;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.trade.BussRepoSecService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.cstp.dao.CstpCommonMatchMapper;
import com.joyintech.tams.invest.common.cstp.service.CstpCommonMatchService;
import com.joyintech.tams.invest.common.dto.common.PledgeBondInfoDto;
import com.joyintech.tams.invest.common.dto.cstp.InterfTradRepoasstDto;
import com.joyintech.tams.invest.common.dto.trade.entrust.EtrFinishStatusDto;
import com.joyintech.tams.invest.trade.cstp.dto.CstpSelectDto;
import com.joyintech.tams.invest.trade.cstptrdmatchinfo.dto.CstpTrdMatchInfoDto;
import com.joyintech.tams.invest.trade.cstptrdmatchinfo.dto.TrdMatchInfoDto;
import com.joyintech.tams.invest.trade.investtrade.commandreceive.dto.TrdCommandReceive001Dto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * @ClassName CstpTrdMatchInfoService
 * @Description: TODO
 * @Author wangxuelei
 * @Date 2020/1/9 0009
 * @Version V1.0
 **/
@Service
@Transactional(rollbackFor=Exception.class)
public class CstpTrdMatchInfoService extends CommonExportService<CstpTrdMatchInfoDto> {

    @Autowired
    private TrdMatchInfoMapper trdMatchInfoMapper;
    @Autowired
    private InterfTradSecdealMapper interfTradSecdealMapper;
    @Autowired
    private EtrFinishStatusMapper etrFinishStatusMapper;
    @Autowired
    private TrdProductDealPreMapper trdProductDealPreMapper;
    @Autowired
    private TrdProductDealAddPreMapper trdProductDealAddPreMapper;
    @Autowired
    private InterfOpicsTradRepoMapper interfOpicsTradRepoMapper;
    @Autowired
    private TrdPledgeMatchInfoMapper trdPledgeMatchInfoMapper;
    
    @Autowired
    private CstpCommonMatchService cstpCommonMatchService;
    
    @Autowired
    private CstpCommonMatchMapper cstpCommonMatchMapper;
    
    @Autowired
    private BussRepoSecService bussRepoSecService;
    
    

    public Page<CstpTrdMatchInfoDto> findPage(Map<String,Object> map){
        if(map.containsKey("trdMatchTyp")&&StringUtil.isNotBlank(map.get("trdMatchTyp").toString())){
            String[] trdMatchTyp = map.get("trdMatchTyp").toString().split(",");
            map.replace("trdMatchTyp", trdMatchTyp);
        }else{
            map.replace("trdMatchTyp", null);
        }
        if(map.containsKey("portfolioId")&&StringUtil.isNotBlank(map.get("portfolioId").toString())){
            String[] portfolioId = map.get("portfolioId").toString().split(",");
            map.replace("portfolioId", portfolioId);
        }else{
            map.replace("portfolioId", null);
        }
        if(map.containsKey("finprodType2")&&StringUtil.isNotBlank(map.get("finprodType2").toString())){
            String finprodType2Old = (String)map.get("finprodType2");
            List<String> list1=new ArrayList<>();
            List<String> list2=new ArrayList<>();
            List<String> list3=new ArrayList<>();
            if (StringUtil.isNotEmpty(finprodType2Old) || StringUtil.isNotEmpty(map.get("ps"))) {
                List<String> finprodType2ListOld = StringUtil.splitToList(GlobalConstant.COMMA, finprodType2Old);
                for(String finprodType2 : finprodType2ListOld){
	                //              //为不同资产类型赋查询条件
	                if(StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_基金.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_计划类资产_利率型.getValue(), finprodType2)){
	                	list3.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
	                	list2.add(DictCodeList.收益类型_收益型.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), finprodType2)){
	                	list3.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
	                	list2.add(DictCodeList.收益类型_净值型.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_计划类资产_货币型.getValue(), finprodType2)){
	                	list3.add(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue());
	                	list2.add(DictCodeList.收益类型_货币型.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_存款.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_存款.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_项目类资产.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_质押式回购.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_买断式回购.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue());
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue());
	                }
	                else if(StringUtil.equals(DictCodeList.平安资产类型_拆借.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
	                }
	
	
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_股票交易.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_拆入.getValue());
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_拆出.getValue());
	                }
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_外汇即期.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_即期.getValue());
	                }
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_外汇远期.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_远期.getValue());
	                }
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_掉期.getValue());
	                }
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue());
	                }
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue());
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue());
	                }
	                else if(StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), finprodType2)){
	                	list1.add(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
	                }
	                else if(StringUtil.equals(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue(), finprodType2)){
	                	list1.add(BussinessDictCodeList.金融产品类型_投管用_一级投标债券.getValue());
	                }
	                //交易方向为利率互换 转换
	                if(StringUtil.isNotEmpty((String)map.get("ps"))){
	                	if( StringUtil.equals((String)map.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取固定.getValue()) ){
	                		map.put("ps", DictCodeListExt2.互换方式_收取固定.getValue());
	                		map.put("IRSps", DictCodeListExt2.互换方式_收取固定.getValue());
	                    }
	                    if( StringUtil.equals((String)map.get("ps") , DictCode.互换方式.getValue()+DictCodeListExt2.互换方式_收取浮动.getValue()) ){
	                    	map.put("ps", DictCodeListExt2.互换方式_收取浮动.getValue());
	                    	map.put("IRSps", DictCodeListExt2.互换方式_收取浮动.getValue());
	                    }
	                    map.put("PS_T01", DictCodeList.交易方向_买入.getValue());
	                }
                }
            }
            
            map.put("finprodType2ListPre", list1);

            String[] finprodType2 = map.get("finprodType2").toString().split(",");
            map.replace("finprodType2", finprodType2);
        }else{
            map.replace("finprodType2", null);
            map.put("finprodType2ListPre", null);
        }
        if(map.containsKey("execid")&&StringUtil.isNotBlank(map.get("execid").toString())){
            String[] execid = map.get("execid").toString().split(",");
            map.replace("execid", execid);
        }else{
            map.replace("execid", null);
        }
        Page<CstpTrdMatchInfoDto> cstpSelectDtoPage = restTemplate.postForPage(ServiceApplication.TRADE_SERVICE_NAME
                + "/CSTPTrdMatchInfo/findPage", map, CstpTrdMatchInfoDto.class);
        setValue(cstpSelectDtoPage);
        return cstpSelectDtoPage;
    }


    private void setValue( Page<CstpTrdMatchInfoDto> cstpSelectDtoPage){
        if(CollectionUtils.isNotEmpty(cstpSelectDtoPage.getRoot())){
            for (CstpTrdMatchInfoDto dto:cstpSelectDtoPage.getRoot()) {
                if(StringUtil.equals(dto.getTradeType(),"02")&&StringUtil.equals(dto.getFinprodType(),"F07")){
                    if(dto.getPrinAmt()!=null && dto.getUnitCprice()!=null &&dto.getActCapDays()!=null){
                        BigDecimal multiply = ArithmeticUtil.multiply(dto.getPrinAmt(), dto.getUnitCprice());
                        BigDecimal multiply1 = ArithmeticUtil.multiply(ArithmeticUtil.divide(multiply, new BigDecimal(100), 8), new BigDecimal(dto.getActCapDays()));
                        BigDecimal divide = ArithmeticUtil.divide(multiply1, 365, 2);
                        dto.setUnitInt(divide);
                        dto.setIntAmt(divide);
                        dto.setMdateSettleAmt(ArithmeticUtil.doAdd(divide,dto.getPrinAmt()));
                    }
                }
            }
        }
    }
    /** @MethodName: cstp交易匹配确认保存
    * @Description: TODO
    * @Param: []
    * @Return: void
    * @Author: wangxuelei
    * @Date: 2020/1/13 0013
    **/
    public void saveTrdMatchInfoByCstp(CstpTrdMatchInfoDto cstpTrdMatchInfoDto){
    	//cstpSelectDtoList  cstp下行数据
		//trdCommandReceive001DtoList 系统内的交易数据
    	if(StringUtil.isEmptyList(cstpTrdMatchInfoDto.getCstpSelectDtoList())
    			&&StringUtil.isEmptyList(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList())){
    		//委托列表及cstp列表为空
    		return ;
    	}
    	if(cstpTrdMatchInfoDto.getCstpSelectDtoList().size()==1){
    		//cstp匹配多委托
    		if(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size()==1&&StringUtil.equals(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(0).getTermType(), "01")){
    			//预录入交易
    			transforMatch(cstpTrdMatchInfoDto);
    		}else{
    			//调用共通 TODO
    			StringBuffer sb = new StringBuffer();
    			for(int i =0;i<cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size();i++){
    				sb.append(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(i).getEntrustId());
    				if(i+1!=cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size()){
    					sb.append(",");
    				}
    			}
    			Map<String,Object> map = new HashMap<>();
    			map.put("execId", cstpTrdMatchInfoDto.getCstpSelectDtoList().get(0).getExecid());
    			map.put("matchType", "1");
    			map.put("entrustIds", sb.toString());
    			cstpCommonMatchService.dealMatchEntrust(map);
        		return;
    		}
    	}
    	if(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size()==1 && cstpTrdMatchInfoDto.getCstpSelectDtoList().size()>1){
    		TrdCommandReceive001Dto entrust=cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(0);
    		if(StringUtil.equals(entrust.getFinprodType2(), DictCodeList.平安资产类型_质押式回购.getValue())
    				&&StringUtil.equals(entrust.getPs(), DictCodeList.回购拆借方向_正回购.getValue())){
    			正回购处理(cstpTrdMatchInfoDto);
    		}else{
    			transforMatch(cstpTrdMatchInfoDto);
    		}
    	}
        saveTrdMatchInfo(cstpTrdMatchInfoDto);
    }


	private void 正回购处理(CstpTrdMatchInfoDto cstpTrdMatchInfoDto) {
		//委托匹配多cstp下行数据
		
		//获取当前cstp剩余质押券
		for(CstpSelectDto cstpVal:cstpTrdMatchInfoDto.getCstpSelectDtoList() ){
			List<InterfTradRepoasstDto> interfTradRepoasstDtos = cstpCommonMatchMapper.getInterfTradRepoasst(cstpVal.getExecid());
			cstpVal.setInterfTradRepoasstDtoList(interfTradRepoasstDtos);
		}
		
        
		//委托匹配多成交
		
		TrdCommandReceive001Dto entrust=cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(0);
		//获取当前委托可用质押券
		Map<String,Object> params = new HashMap<>();
		params.put("ps", entrust.getPs());
		params.put("tradeMarket", DictCodeListExt1.交易场所_银行间.getValue());
		params.put("portfolioId", entrust.getPortfolioId());
        List<PledgeBondInfoDto> pledgeBondInfoDtoList = bussRepoSecService.selectAvaPledgeBondDetailList(params);
        entrust.setPledgeBondInfoDtoList(pledgeBondInfoDtoList);
        
		List<TrdMatchInfoDto> trdMatchInfos = new ArrayList<>();
		//初始化 委托可交易金额
		BigDecimal ava = entrust.getProAvailableCash();
		for(CstpSelectDto cstpVal:cstpTrdMatchInfoDto.getCstpSelectDtoList() ){
			if(ava.compareTo(BigDecimal.ZERO)<=0){//无可用时，跳出
				break;
			}
//    			var cstpVal =tableCstp[i];//cstp下行数据
			TrdMatchInfoDto trdMatchInfo = new TrdMatchInfoDto();//需要匹配数据
			List<TrdPledgeMatchInfo> trdPledgeMatchInfoList = new ArrayList<>(); //需要匹配的质押券信息
			trdMatchInfo.setCstpTradeId(cstpVal.getExecid());
			trdMatchInfo.setEntrustId(entrust.getEntrustId());
			trdMatchInfo.setMatchType("1");//手工匹配
			trdMatchInfo.setTradeDate(entrust.getCreateTimeVal());
			//成交对应的折算比例
			BigDecimal totalRepoAsst = BigDecimal.ZERO;//汇总质押券面
			for(InterfTradRepoasstDto interfTradRepoasst:cstpVal.getInterfTradRepoasstDtoList()){
				totalRepoAsst = ArithmeticUtil.doAdd(totalRepoAsst,interfTradRepoasst.getDealamt());
			}
			 //上请本金
//                cstpVal.scFpriceAmt
		    //中债本金
//                cstpVal.cbFpriceAmt
			BigDecimal matchNum =BigDecimal.ZERO;//匹配额度
//        		BigDecimal avaMatchNum =ArithmeticUtil.subtract(cstpVal.getTradecashamt(), cstpVal.getLastqtyready());//未匹配额度
			//cstp质押券信息
			for(InterfTradRepoasst repoasst:cstpVal.getInterfTradRepoasstDtoList()){
				for(PledgeBondInfoDto etrpledge :entrust.getPledgeBondInfoDtoList()){
					String  repoasstSecId = repoasst.getSecid()+".IB";
					if(StringUtil.equals(repoasstSecId, etrpledge.getBondCode())){//资产代码一致
//        					etrpledge.repRegistOrg//登记机构
//                			repoasst.facetotalamt//成交券面
//                			repoasst.convrate//折算比例
//                			repoasst.dealamt//成交金额
//                			etrpledge.avaPledgePrin//可质押券面
		    			if(etrpledge.getAvaPledgePrin().compareTo(BigDecimal.ZERO)<=0){//无可用质押   继续循环
		    				continue;
		    			}
		    			TrdPledgeMatchInfo trdPledgeMatchInfo = new TrdPledgeMatchInfo();
						trdPledgeMatchInfo.setCstpTradeId(cstpVal.getExecid());
						trdPledgeMatchInfo.setEntrustId(entrust.getEntrustId());
						trdPledgeMatchInfo.setMatchType("1");//手工匹配
						trdPledgeMatchInfo.setTradeDate(entrust.getCreateTimeVal());
						//折算本金
						BigDecimal entrustminus = ArithmeticUtil.multiply(ArithmeticUtil.multiply(etrpledge.getAvaPledgePrin(),repoasst.getConvrate()),ArithmeticUtil.divide( repoasst.getDealamt() ,  totalRepoAsst, 12));
						if(entrustminus.compareTo(ava)<0){//折算小于剩余可交易
							ava =ArithmeticUtil.subtract(ava ,entrustminus );//减少剩余额度
							etrpledge.setAvaPledgePrin(ArithmeticUtil.subtract(etrpledge.getAvaPledgePrin(),repoasst.getFacetotalamt() )); //减少可用券面
							matchNum =ArithmeticUtil.doAdd(matchNum,entrustminus );//增加匹配额度
							trdPledgeMatchInfo.setMatchNum(etrpledge.getAvaPledgePrin());
						}else{
							//折算大于剩余可交易
							BigDecimal match  = ArithmeticUtil.multiply(ava,ArithmeticUtil.divide(totalRepoAsst, repoasst.getDealamt(), 12));//匹配数量
							if(match.compareTo(repoasst.getFacetotalamt())>0){//匹配金额大于成交信息
								match =repoasst.getFacetotalamt();
								//成交金额* 折算比例
								matchNum =ArithmeticUtil.doAdd(matchNum,ArithmeticUtil.multiply(cstpVal.getTradecashamt() ,ArithmeticUtil.divide(repoasst.getDealamt(), totalRepoAsst, 12)) );//增加匹配额度
								trdPledgeMatchInfo.setMatchNum(match);
								etrpledge.setAvaPledgePrin(ArithmeticUtil.subtract(etrpledge.getAvaPledgePrin(),match)); //减少可用券面
								ava =ArithmeticUtil.subtract(ava ,matchNum );//减少剩余额度
							}else{
								matchNum =ArithmeticUtil.doAdd(matchNum,entrustminus );//增加匹配额度
								trdPledgeMatchInfo.setMatchNum(match);
								etrpledge.setAvaPledgePrin(ArithmeticUtil.subtract(etrpledge.getAvaPledgePrin(),match)); //减少可用券面
								ava =ArithmeticUtil.subtract(ava ,entrustminus );//减少剩余额度
							}
						}
						trdPledgeMatchInfo.setPledgeFinprodId("F01_"+repoasstSecId);
						trdPledgeMatchInfoList.add(trdPledgeMatchInfo);
					}
					
				}
			}
			trdMatchInfo.setMatchNum(matchNum);
			trdMatchInfo.setTrdPledgeMatchInfoList(trdPledgeMatchInfoList);
			trdMatchInfos.add(trdMatchInfo);
		}
		cstpTrdMatchInfoDto.setTrdMatchInfos(trdMatchInfos);
	}

    /**
     * 原先页面转换方式  移动后台处理
     * @param cstpTrdMatchInfoDto
     * @return
     */
    private CstpTrdMatchInfoDto  transforMatch (CstpTrdMatchInfoDto cstpTrdMatchInfoDto) {
    	List<TrdMatchInfoDto> trdMatchInfos = new ArrayList<>();
    	boolean flag = true;
    	if(cstpTrdMatchInfoDto.getCstpSelectDtoList().size()==1){
    		CstpSelectDto cstpSelectDto = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(0);
    		//成交可使用金额
    		BigDecimal avaMatch = BigDecimal.ZERO;
			if(StringUtil.equals(cstpSelectDto.getBusinesstype(), "BOND")){
				avaMatch=ArithmeticUtil.subtract(cstpSelectDto.getPrinamt(), cstpSelectDto.getLastqtyready());
			}else if (StringUtil.equals(cstpSelectDto.getBusinesstype(), "REPO")){
				avaMatch=ArithmeticUtil.subtract(cstpSelectDto.getRepoamt(), cstpSelectDto.getLastqtyready());
			}
    		for(TrdCommandReceive001Dto trdCommandReceive001Dto :cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList()){
    			if(avaMatch.compareTo(BigDecimal.ZERO)<1){
    				break;
    			}
    			commonGetTrdMatch(cstpTrdMatchInfoDto, trdMatchInfos, trdCommandReceive001Dto, cstpSelectDto,avaMatch);
        	}
    	}else{
    		//委托可交易金额
    		TrdCommandReceive001Dto trdCommandReceive001Dto = cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(0);
    		BigDecimal avaMatch = trdCommandReceive001Dto.getProAvailableCash();//页面为可交易金额
    		for(CstpSelectDto cstpSelectDto:cstpTrdMatchInfoDto.getCstpSelectDtoList()){
    			if(avaMatch.compareTo(BigDecimal.ZERO)<1){
    				break;
    			}
    			commonGetTrdMatch(cstpTrdMatchInfoDto, trdMatchInfos, trdCommandReceive001Dto, cstpSelectDto,avaMatch);
    		}
    	}
    	
    	cstpTrdMatchInfoDto.setTrdMatchInfos(trdMatchInfos);    	
       
        return cstpTrdMatchInfoDto;
    }


	private void commonGetTrdMatch(CstpTrdMatchInfoDto cstpTrdMatchInfoDto, List<TrdMatchInfoDto> trdMatchInfos,
			TrdCommandReceive001Dto trdCommandReceive001Dto, CstpSelectDto cstpSelectDto,BigDecimal avaMatch) {
		TrdMatchInfoDto trdMatchInfo = new TrdMatchInfoDto();
		trdMatchInfo.setCstpTradeId(cstpSelectDto.getExecid());//cstpId
		if(StringUtil.equals(trdCommandReceive001Dto.getTradeType(), "02")){//指令
			trdMatchInfo.setEntrustId(trdCommandReceive001Dto.getEntrustId());
		}else{
			trdMatchInfo.setPreTradeId(trdCommandReceive001Dto.getEntrustId());
		}
		
		BigDecimal amt = BigDecimal.ZERO;//cstp可匹配金额
		if(StringUtil.equals(cstpSelectDto.getBusinesstype(), "BOND")){
			amt=ArithmeticUtil.subtract(cstpSelectDto.getPrinamt(), cstpSelectDto.getLastqtyready());
		}else if (StringUtil.equals(cstpSelectDto.getBusinesstype(), "REPO")){
			amt=ArithmeticUtil.subtract(cstpSelectDto.getRepoamt(), cstpSelectDto.getLastqtyready());
		}
		
		if(StringUtil.equals(trdCommandReceive001Dto.getTradeType(), "02")){//指令
			if(trdCommandReceive001Dto.getEntrustAmt().compareTo(amt)>0){
				if(avaMatch.compareTo(amt)<1){
					trdMatchInfo.setMatchNum(avaMatch);
				}else{
					trdMatchInfo.setMatchNum(amt);
				}
				avaMatch = ArithmeticUtil.subtract(avaMatch, trdMatchInfo.getMatchNum());
				
			}else{
				
				if(avaMatch.compareTo(trdCommandReceive001Dto.getEntrustAmt())<1){
					trdMatchInfo.setMatchNum(avaMatch);
				}else{
					trdMatchInfo.setMatchNum(trdCommandReceive001Dto.getEntrustAmt());
				}
				avaMatch = ArithmeticUtil.subtract(avaMatch, trdMatchInfo.getMatchNum());
			}
		}else{
			if(StringUtil.equals(cstpSelectDto.getBusinesstype(), "BOND")){
				trdMatchInfo.setMatchNum(cstpSelectDto.getPrinamt());
			}else if (StringUtil.equals(cstpSelectDto.getBusinesstype(), "REPO")){
				trdMatchInfo.setMatchNum(cstpSelectDto.getRepoamt());
			}
		}
		//债券匹配额度 以百元券登录
		if(StringUtil.equals(cstpSelectDto.getBusinesstype(), "BOND")){
			trdMatchInfo.setMatchNum(ArithmeticUtil.divide(trdMatchInfo.getMatchNum(),new BigDecimal("100"),2));
		}
		trdMatchInfo.setMatchType("1");//手工匹配
		trdMatchInfo.setTradeDate(trdCommandReceive001Dto.getCreateTimeVal());

		trdMatchInfos.add(trdMatchInfo);
	}

    /** @MethodName: 交易匹配确认保存
     * @Param: []
     * @Return: void
     * @Date: 2021/03/30
     **/
	private void saveTrdMatchInfo(CstpTrdMatchInfoDto cstpTrdMatchInfoDto) {
		if(CollectionUtils.isNotEmpty(cstpTrdMatchInfoDto.getTrdMatchInfos())){
            for (TrdMatchInfoDto info:cstpTrdMatchInfoDto.getTrdMatchInfos()) {
                info.setSeqno(UUID.randomUUID().toString().replaceAll("-", ""));
                finishEntrustAmt(info,false);
//                updateInterfForZhaiJuan(info,true);
                trdMatchInfoMapper.insertSelective(info);
//              质押品匹配表
                if(!StringUtil.isEmptyList(info.getTrdPledgeMatchInfoList())){
                	for(TrdPledgeMatchInfo trdPledgeMatchInfo:info.getTrdPledgeMatchInfoList()){
                		trdPledgeMatchInfo.setSeqno(UUID.randomUUID().toString().replaceAll("-", ""));
                		trdPledgeMatchInfoMapper.insertSelective(trdPledgeMatchInfo);
                	}
                }
            }
        }
	}
    /** @MethodName: 匹配确认取消匹配
    * @Description: TODO
    * @Param: [cstpTrdMatchInfoDto]
    * @Return: void
    * @Author: wangxuelei
    * @Date: 2020/1/13 0013
    **/
    @DTXTransaction
    @Transactional(rollbackFor=Exception.class)
    public void deleteTrdMatchInfoByCstp(CstpTrdMatchInfoDto cstpTrdMatchInfoDto){
        if(CollectionUtils.isNotEmpty(cstpTrdMatchInfoDto.getTrdMatchInfos())){
            for (TrdMatchInfoDto info:cstpTrdMatchInfoDto.getTrdMatchInfos()) {
                finishEntrustAmt(info,true);
//                updateInterfForZhaiJuan(info,false);
                trdMatchInfoMapper.deleteByPrimaryKey(info.getSeqno());
//              质押品匹配表
                restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/CSTPTrdMatchInfo/deleteTrdPledgeMatchInfo",
                		info, Boolean.class);
            }
        }
    }
    /** @MethodName: 较少cstp的可匹配额度
    * @Description: TODO
    * @Param: [cstpTrdMatchInfoDto]
    * @Return: void
    * @Author: wangxuelei
    * @Date: 2020/1/13 0013
    **/
    public void updateInterfForZhaiJuanForSecdeal(TrdMatchInfo cstpTrdMatchInfoDto, Boolean type){
        InterfTradSecdeal interfTradSecdeal = interfTradSecdealMapper.selectByPrimaryKey(cstpTrdMatchInfoDto.getCstpTradeId());
        InterfTradSecdeal record = new InterfTradSecdeal();
        if(interfTradSecdeal!=null){
            BigDecimal subtract = new BigDecimal(0);
            record.setDealno(cstpTrdMatchInfoDto.getCstpTradeId());
            BigDecimal prinamt = interfTradSecdeal.getPrinamt();
            if(type){//匹配确认
                 subtract = ArithmeticUtil.subtract(prinamt, cstpTrdMatchInfoDto.getMatchNum());
            }else{//取消匹配
                TrdMatchInfo trdMatchInfo = trdMatchInfoMapper.selectByPrimaryKey(cstpTrdMatchInfoDto.getSeqno());
                subtract =  ArithmeticUtil.doAdd(prinamt,trdMatchInfo.getMatchNum());
            }
            if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                record.setPrinamt(BigDecimal.ZERO);
            }else{
                record.setPrinamt(subtract);
            }
            interfTradSecdealMapper.updateByPrimaryKeySelective(record);
        }
    }

    public void updateInterfForZhaiJuanForOpic(TrdMatchInfo cstpTrdMatchInfoDto, Boolean type){
        InterfOpicsTradRepo interfOpicsTradRepo = interfOpicsTradRepoMapper.selectByPrimaryKey(cstpTrdMatchInfoDto.getCstpTradeId());
        InterfOpicsTradRepo record = new InterfOpicsTradRepo();
        if(interfOpicsTradRepo!=null){
            BigDecimal subtract = new BigDecimal(0);
            record.setDealno(cstpTrdMatchInfoDto.getCstpTradeId());
            BigDecimal repoamt = interfOpicsTradRepo.getRepoamt();
            if(type){//匹配确认
                subtract = ArithmeticUtil.subtract(repoamt, cstpTrdMatchInfoDto.getMatchNum());
            }else{//取消匹配
                TrdMatchInfo trdMatchInfo = trdMatchInfoMapper.selectByPrimaryKey(cstpTrdMatchInfoDto.getSeqno());
                subtract =  ArithmeticUtil.doAdd(repoamt,trdMatchInfo.getMatchNum());
            }
            if(subtract.compareTo(BigDecimal.ZERO)==-1||subtract.compareTo(BigDecimal.ZERO)==0){//小于0的话
                record.setRepoamt(BigDecimal.ZERO);
            }else{
                record.setRepoamt(subtract);
            }
            interfOpicsTradRepoMapper.updateByPrimaryKeySelective(record);
        }
    }


    private Boolean finishEntrustAmt(TrdMatchInfo dto,boolean isCancel){
        List<EtrFinishStatusDto> baseEtrInvestInfoDtos = new ArrayList<>();
        EtrFinishStatusDto status = new EtrFinishStatusDto();
        if(StringUtil.isNotBlank(dto.getEntrustId())){
            status.setEntrustId(dto.getEntrustId());
            status.setTradeId(dto.getCstpTradeId());
            status.setFinishDate(dto.getTradeDate());
            status.setFinishAmt(dto.getMatchNum());
            status.setTrader(LoginUserUtil.getUserName());
            baseEtrInvestInfoDtos.add(status);
        }else{
            TrdProductDealPreKey key = new TrdProductDealPreKey();
            key.setTradeId(dto.getPreTradeId());
            key.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
            TrdProductDealPre trdProductDealPre = trdProductDealPreMapper.selectByPrimaryKey(key);
            TrdProductDealAddPre trdProductDealAddPre = trdProductDealAddPreMapper.selectByPrimaryKey(dto.getPreTradeId());
            EtrFinishStatusKey etrKey = new EtrFinishStatusKey();
            etrKey.setEntrustId(trdProductDealAddPre.getEntrustId());
            etrKey.setTradeId(dto.getPreTradeId());
            EtrFinishStatus etrFinishStatus = etrFinishStatusMapper.selectByPrimaryKey(etrKey);
            if(trdProductDealPre!=null && StringUtils.isNotEmpty(trdProductDealAddPre.getEntrustId())) {
                status.setEntrustId(trdProductDealAddPre.getEntrustId());
                status.setTradeId(dto.getCstpTradeId());
                status.setFinishDate(dto.getTradeDate());
                status.setFinishAmt(dto.getMatchNum());
                status.setEntFinishAmt(etrFinishStatus != null ? etrFinishStatus.getEntFinishAmt() : BigDecimal.ZERO);
                status.setTrader(LoginUserUtil.getUserName());
                baseEtrInvestInfoDtos.add(status);
            }
        }
        if (!baseEtrInvestInfoDtos.isEmpty()) {
            if (isCancel) {
                return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/cancelEntrust", baseEtrInvestInfoDtos, Boolean.class);
            } else {
                return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/finishEntrust", baseEtrInvestInfoDtos, Boolean.class);
            }
        }
        return Boolean.TRUE;
    }
    
    /**
     * 导出 
     */
     @Override
     protected void buildExportParam(ExportParam exportParam) {
         exportParam.config(
                 "/com/joyintech/tams/invest/trade/cstptrdmatchinfo/conf/export_conf.xml").dataSource(DataSourceType.MASERT);
     }
     
     /**
      * {@inheritDoc}
      */
     @Override
     public Page<CstpTrdMatchInfoDto> selectPageData(Map<String, Object> map) {
         return findPage(map);
     }


	public Map<String, Object> checkPosition(Map<String, Object> map) {
		Map<String,Object> result = new HashMap<>();
		String isContainPosition = DictCodeList.是否_否.getValue();
		String execid="";
		if(StringUtil.isNotEmpty(map.get("execid"))){
			execid =(String) map.get("execid");
		}else{
			return result;
		}
		List<InterfTradRepoasstDto> interfTradRepoasstDtos = cstpCommonMatchMapper.getInterfTradRepoasst(execid);
		//获取当前委托可用质押券
		Map<String,Object> params = new HashMap<>();
		params.put("ps", DictCodeList.回购拆借方向_正回购.getValue());
		params.put("tradeMarket", DictCodeListExt1.交易场所_银行间.getValue());
		params.put("portfolioId", map.get("portfolioId"));
        List<PledgeBondInfoDto> pledgeBondInfoDtoList = bussRepoSecService.selectAvaPledgeBondDetailList(params);
        if(StringUtil.isEmptyList(pledgeBondInfoDtoList)){
        	return result;
        }
        here:
		for(InterfTradRepoasstDto interfTradRepoasstDto:interfTradRepoasstDtos){
			for(PledgeBondInfoDto pledgeBondInfoDto:pledgeBondInfoDtoList){
				String  repoasstSecId = interfTradRepoasstDto.getSecid()+".IB";
				if(StringUtil.equals(repoasstSecId, pledgeBondInfoDto.getBondCode())){//资产代码一致
					isContainPosition =DictCodeList.是否_是.getValue();
					break here;
				}
			}
		}
		result.put("isContainPosition", isContainPosition);
		return result;
	}

	/**
	 * 获取回购天数枚举值
	 */
	private String calcTradelimitdays(int teDays,String tradeDateStr){
		String termDateStr = "";
		if (StringUtil.isNotEmpty(teDays)) {
			try {
				BigDecimal checkTeDays = new BigDecimal(teDays);
				String ipDateStr = tradeDateStr;
				Date ipDate1 = DateUtil.getLastDay(DateUtil.parse(ipDateStr));
				int days = DateUtil.getDayOfMonth(DateUtil.format(ipDate1));
				Date ipDatex2 = DateUtil.addMonthDateStr(ipDateStr, 2);
				Date ipDate2 = DateUtil.getLastDay(ipDatex2);
				int days2 = days + DateUtil.getDayOfMonth(DateUtil.format(ipDate2));
				Date ipDatex3 = DateUtil.addMonthDateStr(ipDateStr, 3);
				Date ipDate3 = DateUtil.getLastDay(ipDatex3);
				int days3 = days2 + DateUtil.getDayOfMonth(DateUtil.format(ipDate3));
				Date ipDatex4 = DateUtil.addMonthDateStr(ipDateStr, 4);
				Date ipDate4 = DateUtil.getLastDay(ipDatex4);
				int days4 = days3 + DateUtil.getDayOfMonth(DateUtil.format(ipDate4));
				Date ipDatex5 = DateUtil.addMonthDateStr(ipDateStr, 5);
				Date ipDate5 = DateUtil.getLastDay(ipDatex5);
				int days5 = days4 + DateUtil.getDayOfMonth(DateUtil.format(ipDate5));
				Date ipDatex6 = DateUtil.addMonthDateStr(ipDateStr, 6);
				Date ipDate6 = DateUtil.getLastDay(ipDatex6);
				int days6 = days5 + DateUtil.getDayOfMonth(DateUtil.format(ipDate6));
				Date ipDatex7 = DateUtil.addMonthDateStr(ipDateStr, 7);
				Date ipDate7 = DateUtil.getLastDay(ipDatex7);
				int days7 = days6 + DateUtil.getDayOfMonth(DateUtil.format(ipDate7));
				Date ipDatex8 = DateUtil.addMonthDateStr(ipDateStr, 8);
				Date ipDate8 = DateUtil.getLastDay(ipDatex8);
				int days8 = days7 + DateUtil.getDayOfMonth(DateUtil.format(ipDate8));
				Date ipDatex9 = DateUtil.addMonthDateStr(ipDateStr, 9);
				Date ipDate9 = DateUtil.getLastDay(ipDatex9);
				int days9 = days8 + DateUtil.getDayOfMonth(DateUtil.format(ipDate9));
				Date ipDatex10 = DateUtil.addMonthDateStr(ipDateStr, 10);
				Date ipDate10 = DateUtil.getLastDay(ipDatex10);
				int days10 = days9 + DateUtil.getDayOfMonth(DateUtil.format(ipDate10));
				Date ipDatex11 = DateUtil.addMonthDateStr(ipDateStr, 11);
				Date ipDate11 = DateUtil.getLastDay(ipDatex11);
				int days11 = days10 + DateUtil.getDayOfMonth(DateUtil.format(ipDate11));
				Date ipDatex12 = DateUtil.addMonthDateStr(ipDateStr, 12);
				Date ipDate12 = DateUtil.getLastDay(ipDatex12);
				int days12 = days11 + DateUtil.getDayOfMonth(DateUtil.format(ipDate12));

				if (checkTeDays.compareTo(BigDecimal.ONE) == 0) {
					termDateStr = "R001";
				} else if (checkTeDays.compareTo(BigDecimal.ONE) > 0 && checkTeDays.compareTo(new BigDecimal(7)) <= 0) {
					termDateStr = "R007";
				} else if (checkTeDays.compareTo(new BigDecimal(7)) > 0 && checkTeDays.compareTo(new BigDecimal(14)) <= 0) {
					termDateStr = "R014";
				} else if (checkTeDays.compareTo(new BigDecimal(14)) > 0 && checkTeDays.compareTo(new BigDecimal(21)) <= 0) {
					termDateStr = "R021";
				} else if (checkTeDays.compareTo(new BigDecimal(days)) <= 0) {
					termDateStr = "R1M";
				} else if (checkTeDays.compareTo(new BigDecimal(days2)) <= 0) {
					termDateStr = "R2M";
				} else if (checkTeDays.compareTo(new BigDecimal(days3)) <= 0) {
					termDateStr = "R3M";
				} else if (checkTeDays.compareTo(new BigDecimal(days4)) <= 0) {
					termDateStr = "R4M";
				} else if (checkTeDays.compareTo(new BigDecimal(days6)) <= 0) {
					termDateStr = "R6M";
				} else if (checkTeDays.compareTo(new BigDecimal(days9)) <= 0) {
					termDateStr = "R9M";
				} else if (checkTeDays.compareTo(new BigDecimal(days12)) <= 0) {
					termDateStr = "R1Y";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return termDateStr;
	}

	/**
	 * 获取证券账户托管机构
	 */
	private String getOurdepositaccountnumber(String accountnumber){
/*		Map<String,Object> map = new HashMap<String,Object>();
		map.put("accountnumber",accountnumber);
		return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/CSTPTrdMatchInfo/getOurdepositaccountnumber",
				map, String.class);*/
		String temp = "";
		if(StringUtil.equals(BussinessDictCodeList.登记托管机构_中央国债登记结算有限责任公司.getCodeName(),accountnumber)){
			temp = BussinessDictCodeList.登记托管机构_中央国债登记结算有限责任公司.getValue();
		}
		if(StringUtil.equals(BussinessDictCodeList.登记托管机构_上海清算所.getCodeName(),accountnumber)){
			temp = BussinessDictCodeList.登记托管机构_上海清算所.getValue();
		}
		return temp;
	}

	/**
	 * 获取转化资产类型枚举值
	 */
	private String codeToCode(String code){
		String toCode = "";
		if(StringUtil.equals(code,"BOND")){
			toCode = "F01";
		}
		if(StringUtil.equals(code,"REPO")){
			toCode = "F07";
		}
		return toCode;
	}

	/**
	 * 获取计费基数
	 */
	private String getChargeBasis(CstpTrdMatchInfoDto cstpTrdMatchInfoDto,String feeType,int i){
		//获取资产类型
		String fAssetType = codeToCode(cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getBusinesstype());
		//获取交易方向
		String fTradeSide = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getSide();
		//获取回购期限
		String fTremStr = "";
		if(StringUtil.equals(fAssetType,"F07")){
			fTremStr = calcTradelimitdays(cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradelimitdays(),
					cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradeDateStr());
		}
		//获取成交日期
		String tradeDateStr = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradeDateStr();
		//获取证券账户托管机构  参数托管账号
		String fAccountNumber = getOurdepositaccountnumber(cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getOurdepositinstitution());
		//获取银行间交易费用设置信息 要素 资产类型 交易方向 回购期限 托管机构
		CstpTrdMatchInfoDto feeDto = new CstpTrdMatchInfoDto();
		feeDto = getFeeSetting(fAssetType,fTradeSide,fTremStr,fAccountNumber,feeType,tradeDateStr);
		if(null==feeDto){
			return BussinessDictCodeList.计费基数_券面总额.getValue();
		}
		if(StringUtil.isEmpty(feeDto.getChargeBasis())){
			return BussinessDictCodeList.计费基数_券面总额.getValue();
		}
		return feeDto.getChargeBasis();
	}

	/**
	 * 获取银行间费用信息
	 */
	private CstpTrdMatchInfoDto getFeeSetting(String fAssetType,String fTradeSide,String fTremStr,String fAccountNumber,String feeType,String tradeDateStr){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("fAssetType",fAssetType);
		map.put("fTradeSide",fTradeSide);
		map.put("fTremStr",fTremStr);
		map.put("fAccountNumber",fAccountNumber);
		map.put("feeType",feeType);
		map.put("tradeDateStr",tradeDateStr);
		return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/CSTPTrdMatchInfo/getFeeSetting",
				map, CstpTrdMatchInfoDto.class);
	}

	/**
	 * 获取交易手续费/结算手续费
	 */
	private BigDecimal getFeeByTypeAndInfo(CstpTrdMatchInfoDto cstpTrdMatchInfoDto,String feeType,int i){
		BigDecimal feeInfo = new BigDecimal("0");
		//获取资产类型
		String fAssetType = codeToCode(cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getBusinesstype());
		//获取交易方向
		String fTradeSide = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getSide();
		//获取回购期限
		String fTremStr = "";
		if(StringUtil.equals(fAssetType,"F07")){
			fTremStr = calcTradelimitdays(cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradelimitdays(),
					cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradeDateStr());
		}
		//获取成交日期
		String tradeDateStr = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradeDateStr();
		//获取证券账户托管机构  参数托管账号
		String fAccountNumber = getOurdepositaccountnumber(cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getOurdepositinstitution());
		//获取银行间交易费用设置信息 要素 资产类型 交易方向 回购期限 托管机构
		CstpTrdMatchInfoDto feeDto = new CstpTrdMatchInfoDto();
		feeDto = getFeeSetting(fAssetType,fTradeSide,fTremStr,fAccountNumber,feeType,tradeDateStr);
		if(StringUtil.equals(fAssetType,"F01") && feeDto != null){
			if(StringUtil.equals(feeDto.getChargeType(),BussinessDictCodeList.计费类型_固定费率.getValue())){
				//有最大值，最小值
				if(StringUtil.equals(feeDto.getChargeBasis(),BussinessDictCodeList.计费基数_券面总额.getValue())){
					//获取券面总额
					BigDecimal lastQty = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getLastqty();
					//计算金额
					feeInfo = lastQty.multiply(feeDto.getChargeRate());
				}
				if(StringUtil.equals(feeDto.getChargeBasis(),BussinessDictCodeList.计费基数_成交金额.getValue())){
					//获取成交金额
					BigDecimal tradecashamt = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradecashamt();
					feeInfo = tradecashamt.multiply(feeDto.getChargeRate());
				}
				//最小值比较
				if(!(feeDto.getFeeMini() == null)){
					if(feeInfo.compareTo(feeDto.getFeeMini()) == -1){
						feeInfo = feeDto.getFeeMini();
					}
				}
				//最大值比较
				if(!(feeDto.getFeeMaxi() == null)){
					if(feeInfo.compareTo(feeDto.getFeeMaxi()) == 1){
						feeInfo = feeDto.getFeeMaxi();
					}
				}
			}
			if(StringUtil.equals(feeDto.getChargeType(),BussinessDictCodeList.计费类型_固定金额.getValue())){
				feeInfo = feeDto.getChargeValue();
			}
		}
		if(StringUtil.equals(fAssetType,"F07")  && feeDto != null){
			if(StringUtil.equals(feeDto.getChargeType(),BussinessDictCodeList.计费类型_固定费率.getValue())){
				if(StringUtil.equals(feeDto.getChargeBasis(),BussinessDictCodeList.计费基数_成交金额.getValue())){
					//有最大值，最小值
					//获取成交金额
					cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradecashamt();
					BigDecimal tradecashamt = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getTradecashamt();
					feeInfo = tradecashamt.multiply(feeDto.getChargeRate());
					//最小值比较
					if(!(feeDto.getFeeMini() == null)){
						if(feeInfo.compareTo(feeDto.getFeeMini()) == -1){
							feeInfo = feeDto.getFeeMini();
						}
					}
					//最大值比较
					if(!(feeDto.getFeeMaxi() == null)){
						if(feeInfo.compareTo(feeDto.getFeeMaxi()) == 1){
							feeInfo = feeDto.getFeeMaxi();
						}
					}

				}
			}
			if(StringUtil.equals(feeDto.getChargeType(),BussinessDictCodeList.计费类型_固定金额.getValue())){
				feeInfo = feeDto.getChargeValue();
			}
		}
		return feeInfo;
	}

	/** @MethodName: cstp交易手续费/结算手续费计算
	 * @Description: TODO
	 * @Param: []
	 * @Return: void
	 * @Author: wangxuelei
	 * @Date: 2020/1/13 0013
	 **/
	public void calcProcedureAmtAndSettleAmt(CstpTrdMatchInfoDto cstpTrdMatchInfoDto){
		//cstpSelectDtoList  cstp下行数据
		//trdCommandReceive001DtoList 系统内的交易数据
		if(StringUtil.isEmptyList(cstpTrdMatchInfoDto.getCstpSelectDtoList())
				&&StringUtil.isEmptyList(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList())){
			//委托列表及cstp列表为空
			return ;
		}
		if(cstpTrdMatchInfoDto.getCstpSelectDtoList().size()==1){
			//cstp匹配多委托
			if(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size()==1){
				//1=1
				String execId = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(0).getExecid();
				String entrustId = cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(0).getEntrustId();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("execId",execId);
				map.put("entrustId",entrustId);
				List<CstpTrdMatchInfoDto> cstpSelectDtoList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
						+ "/CSTPTrdMatchInfo/getCstpInfo", map, CstpTrdMatchInfoDto.class);
				setValue2(cstpSelectDtoList);
				//获取当前信息
				TrdMatchInfo infoDto = trdMatchInfoMapper.selectByPrimaryKey(cstpSelectDtoList.get(0).getSeqno());
				//处理交易结算费，手续费
				infoDto.setProcedureAmt(
						getFeeByTypeAndInfo(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_交易手续费.getValue(),0));
				infoDto.setSettleAmt(
						getFeeByTypeAndInfo(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_结算手续费.getValue(),0));
				trdMatchInfoMapper.updateByPrimaryKeySelective(infoDto);

			}else{
				//1=n
				//先计算出相关费用
				BigDecimal procedureAmtS = getFeeByTypeAndInfo(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_交易手续费.getValue(),0);
				BigDecimal settleAmtS = getFeeByTypeAndInfo(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_结算手续费.getValue(),0);
				//获取交易手续费计费基数
				String proChargeBasis = getChargeBasis(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_交易手续费.getValue(),0);
				//获取结算手续费计费基数
				String setChargeBasis = getChargeBasis(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_结算手续费.getValue(),0);
				//交易手续费尾差
				BigDecimal procedureAmtTail = new BigDecimal(0);
				//多组合条件下，结算手续费平均分配到每个组合，尾数计到成交额最大的一个组合；
				BigDecimal settleAmt = settleAmtS.divide(new BigDecimal(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size()),2,BigDecimal.ROUND_HALF_UP);
				//计算结算手续费尾差Tail
				BigDecimal a = new BigDecimal(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size());
				BigDecimal settleAmtTail = settleAmtS.subtract(settleAmt.multiply(new BigDecimal(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size())).setScale(2,BigDecimal.ROUND_HALF_UP));
				//记录券面总额的和（用于计算费用拆分比列）
				BigDecimal prinAmtS = new BigDecimal(0);
				//记录交易金额的和（用于计算费用拆分比列）
				BigDecimal tradeAmtS = new BigDecimal(0);
				//记录券面总额最大的数据的流水号
				String newSeqno = "";
				//记录成交金额最大的数据的流水号
				String traSeqno = "";
				//券面总额prinAmt
				BigDecimal prinAmt = new BigDecimal(0);
				//交易金额
				BigDecimal tradeAmt = new BigDecimal(0);
				List<CstpTrdMatchInfoDto> cstpSelectDtoListTemp = new ArrayList<CstpTrdMatchInfoDto>();
				List<TrdMatchInfo> TrdMatchInfoTemp = new ArrayList<TrdMatchInfo>();
				for(int i = 0; i < cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size(); i++){
					String execId = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(0).getExecid();
					String entrustId = cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(i).getEntrustId();
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("execId",execId);
					map.put("entrustId",entrustId);
					List<CstpTrdMatchInfoDto> cstpSelectDtoList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
							+ "/CSTPTrdMatchInfo/getCstpInfo", map, CstpTrdMatchInfoDto.class);
					if(!StringUtil.isEmptyList(cstpSelectDtoList)){
						setValue2(cstpSelectDtoList);
						CstpTrdMatchInfoDto cstpTrdMatchInfoDto1 = cstpSelectDtoList.get(0);
						prinAmtS = prinAmtS.add(cstpTrdMatchInfoDto1.getPrinAmt());
						tradeAmtS = tradeAmtS.add(cstpTrdMatchInfoDto1.getTradeAmt());
						if(cstpTrdMatchInfoDto1.getPrinAmt().compareTo(prinAmt) == 1){
							prinAmt = cstpTrdMatchInfoDto1.getPrinAmt();
							newSeqno = cstpTrdMatchInfoDto1.getSeqno();
						}
						if(cstpTrdMatchInfoDto1.getTradeAmt().compareTo(tradeAmt) == 1){
							tradeAmt = cstpTrdMatchInfoDto1.getTradeAmt();
							traSeqno = cstpTrdMatchInfoDto1.getSeqno();
						}
						cstpSelectDtoListTemp.add(cstpTrdMatchInfoDto1);
					}
				}
				for(int i = 0;i<cstpSelectDtoListTemp.size();i++){
					//获取当前信息
					TrdMatchInfo infoDto = trdMatchInfoMapper.selectByPrimaryKey(cstpSelectDtoListTemp.get(i).getSeqno());
					//计算比例，计算交易手续费
					if(StringUtil.equals(proChargeBasis,BussinessDictCodeList.计费基数_券面总额.getValue())){
						BigDecimal procedureAmt = procedureAmtS.multiply((cstpSelectDtoListTemp.get(i).getPrinAmt()))
								.divide(prinAmtS,2,BigDecimal.ROUND_HALF_UP)
								.setScale(2,BigDecimal.ROUND_HALF_UP);
						infoDto.setProcedureAmt(procedureAmt);
					}else if(StringUtil.equals(proChargeBasis,BussinessDictCodeList.计费基数_成交金额.getValue())){
						BigDecimal procedureAmt = procedureAmtS.multiply((cstpSelectDtoListTemp.get(i).getTradeAmt()))
								.divide(tradeAmtS,2,BigDecimal.ROUND_HALF_UP)
								.setScale(2,BigDecimal.ROUND_HALF_UP);
						infoDto.setProcedureAmt(procedureAmt);
					}else{
						BigDecimal procedureAmt = procedureAmtS.multiply((cstpSelectDtoListTemp.get(i).getPrinAmt()))
								.divide(prinAmtS,2,BigDecimal.ROUND_HALF_UP)
								.setScale(2,BigDecimal.ROUND_HALF_UP);
						infoDto.setProcedureAmt(procedureAmt);
					}
					infoDto.setSettleAmt(settleAmt);
					TrdMatchInfoTemp.add(infoDto);
				}
				//计算交易手续费尾差
				BigDecimal procedureAmtSum = new BigDecimal(0);
				for(int i = 0;i<TrdMatchInfoTemp.size();i++){
					procedureAmtSum = procedureAmtSum.add(TrdMatchInfoTemp.get(i).getProcedureAmt());
				}
				procedureAmtTail = procedureAmtS.subtract(procedureAmtSum).setScale(2,BigDecimal.ROUND_HALF_UP);
				//组装尾差入库
				for(int i = 0;i<TrdMatchInfoTemp.size();i++){
					TrdMatchInfo infoDtos = TrdMatchInfoTemp.get(i);
					//交易手续费尾差
					if(StringUtil.equals(proChargeBasis,BussinessDictCodeList.计费基数_券面总额.getValue())){
						if(StringUtil.equals(TrdMatchInfoTemp.get(i).getSeqno(),newSeqno)){
							infoDtos.setProcedureAmt(infoDtos.getProcedureAmt().add(procedureAmtTail));
						}
					}else if(StringUtil.equals(proChargeBasis,BussinessDictCodeList.计费基数_成交金额.getValue())){
						if(StringUtil.equals(TrdMatchInfoTemp.get(i).getSeqno(),traSeqno)){
							infoDtos.setProcedureAmt(infoDtos.getProcedureAmt().add(procedureAmtTail));
						}
					}else{
						if(StringUtil.equals(TrdMatchInfoTemp.get(i).getSeqno(),newSeqno)){
							infoDtos.setProcedureAmt(infoDtos.getProcedureAmt().add(procedureAmtTail));
						}
					}
					//结算手续费尾差
					if(StringUtil.equals(setChargeBasis,BussinessDictCodeList.计费基数_券面总额.getValue())){
						if(StringUtil.equals(TrdMatchInfoTemp.get(i).getSeqno(),newSeqno)){
							infoDtos.setSettleAmt(infoDtos.getSettleAmt().add(settleAmtTail));
						}
					}else if(StringUtil.equals(proChargeBasis,BussinessDictCodeList.计费基数_成交金额.getValue())){
						if(StringUtil.equals(TrdMatchInfoTemp.get(i).getSeqno(),traSeqno)){
							infoDtos.setSettleAmt(infoDtos.getSettleAmt().add(settleAmtTail));
						}
					}else{
						if(StringUtil.equals(TrdMatchInfoTemp.get(i).getSeqno(),newSeqno)){
							infoDtos.setSettleAmt(infoDtos.getSettleAmt().add(settleAmtTail));
						}
					}
					trdMatchInfoMapper.updateByPrimaryKeySelective(infoDtos);
				}
			}
		}
		if(cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().size()==1 && cstpTrdMatchInfoDto.getCstpSelectDtoList().size()>1){
			//n=1
			for(int i = 0; i < cstpTrdMatchInfoDto.getCstpSelectDtoList().size() ; i++){
				String execId = cstpTrdMatchInfoDto.getCstpSelectDtoList().get(i).getExecid();
				String entrustId = cstpTrdMatchInfoDto.getTrdCommandReceive001DtoList().get(0).getEntrustId();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("execId",execId);
				map.put("entrustId",entrustId);
				List<CstpTrdMatchInfoDto> cstpSelectDtoList = restTemplate.postForList(ServiceApplication.TRADE_SERVICE_NAME
						+ "/CSTPTrdMatchInfo/getCstpInfo", map, CstpTrdMatchInfoDto.class);
				setValue2(cstpSelectDtoList);
				//获取当前信息
				TrdMatchInfo infoDto = trdMatchInfoMapper.selectByPrimaryKey(cstpSelectDtoList.get(0).getSeqno());
				//处理交易结算费，手续费
				infoDto.setProcedureAmt(
						getFeeByTypeAndInfo(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_交易手续费.getValue(),i));
				infoDto.setSettleAmt(
						getFeeByTypeAndInfo(cstpTrdMatchInfoDto,BussinessDictCodeList.费用类型_结算手续费.getValue(),i));
				trdMatchInfoMapper.updateByPrimaryKeySelective(infoDto);
			}

		}
	}
	private void setValue2( List<CstpTrdMatchInfoDto> cstpSelectDtoPage){
		if(cstpSelectDtoPage.size() > 0){
			for (CstpTrdMatchInfoDto dto:cstpSelectDtoPage) {
				if(StringUtil.equals(dto.getTradeType(),"02")&&StringUtil.equals(dto.getFinprodType(),"F07")){
					if(dto.getPrinAmt()!=null && dto.getUnitCprice()!=null &&dto.getActCapDays()!=null){
						BigDecimal multiply = ArithmeticUtil.multiply(dto.getPrinAmt(), dto.getUnitCprice());
						BigDecimal multiply1 = ArithmeticUtil.multiply(ArithmeticUtil.divide(multiply, new BigDecimal(100), 8), new BigDecimal(dto.getActCapDays()));
						BigDecimal divide = ArithmeticUtil.divide(multiply1, 365, 2);
						dto.setUnitInt(divide);
						dto.setIntAmt(divide);
						dto.setMdateSettleAmt(ArithmeticUtil.doAdd(divide,dto.getPrinAmt()));
					}
				}
			}
		}
	}
}
