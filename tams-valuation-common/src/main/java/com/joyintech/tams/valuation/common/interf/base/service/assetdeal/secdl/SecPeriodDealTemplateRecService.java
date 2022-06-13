package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.secdl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.FinalString;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.ChannelFeeInfoDeal;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.TargetFinProduct;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateCheckReturnResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateSourceFlgDTO;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecReceicableTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendAssetPeriodDeal;

/**
 * 债券借贷标的劵存续期交易 模板处理Service(VAL_SECLEND_ASSET_PERIOD_DEAL)
 *
 * @author dongxiangyang
 * @version 1.0 2018年10月29日
 * @see SecblTranDealTemplateRecService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecPeriodDealTemplateRecService {
	 /**
     * XMl文件解析Service
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    /**
     * 接口共通Service
     */
    @Autowired
    private InterfCommonService interfCommonService;
    /**
     * 债券借贷标的劵存续期交易 接口模板处理
     *
     * @param templateSourceFlgDTO 文件来源和类型
     * @param acctEntity 计提基础信息DTO
     * @return 债券买卖交易 接口模板处理返回共通结果
     * @see
     * @since 1.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public AssetDealInfoValCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
            TemplateOrderInfoReaderResult acctEntity) {
    	// 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        List<ValSeclendAssetPeriodDeal> listValSeclendAssetPeriodDeal=new ArrayList<ValSeclendAssetPeriodDeal>();
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        Map<String,Object> params = acctEntity.getParams();

        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        listValSeclendAssetPeriodDeal=returnResult.getT(ValSeclendAssetPeriodDeal.class);
        //获取数据数量
        result.setDataNum(listValSeclendAssetPeriodDeal.size());

		/**add by wujie on 20210506 start**/
		if(returnResult.isReadFile()){
			result.getParamMap().put(InterfCommonConstant.读取文件状态,DictCodeList.是否_是.getValue());
		}
		/**add by wujie on 20210506 end**/

        // 2、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = handleValSeclendDeal(listValSeclendAssetPeriodDeal,acctEntity.getResovleDate(),params,returnResult.getValInterflog());
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal=(List<TrdProductDeal>)map.get("listValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt=(List<TrdProductDealAdd>)map.get("listValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting=(List<BokDealAccounting>)map.get("listValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        //账务明细list
        List<BokDealAccountingDetail> listValDealAccountingDetail=(List<BokDealAccountingDetail>)map.get("listValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
      //账务明细代码表
        List<BokDetailId> listValBokDetailId=(List<BokDetailId>)map.get("listValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("listValOrders");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrdersExt(valOrdersExtList);
        return result;
    }


    /**
     * 02-债券借贷标的劵存续期交易
     *
     * @param listValSeclendAssetPeriodDeal
     * @return 金融产品交易List + 估值核算交易扩展表List + 入账信息表List + 账务明细表List
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
	public   Map<String, Object> handleValSeclendDeal( List<ValSeclendAssetPeriodDeal> listValSeclendAssetPeriodDeal,Date resovleDate,Map<String,Object> params,
			List<ValInterflog> valInterflog){
         List<String> bookSetIds = (List<String>)params.get(InterfCommonConstant.接口_运行接口账套ID);
    	 String valModel = (String)params.get(InterfCommonConstant.接口模式);
   	     Map<String, Object> resultMap=new HashMap<>();
   	     // 金融产品交易List
   	     List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
   	     // 估值核算交易扩展表List
   	     List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
   	     // 入账信息表List
   	     List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
   	     // 账务明细表List
   	     List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
   	     //账务明细代码表
   	     List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
   	     //结算指令
   	     List<StlOrders> listValOrders = new ArrayList<StlOrders>();
   	     List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
         List<ValInterflog> valInterflogList =  new ArrayList<>();
   	     for(ValSeclendAssetPeriodDeal dto:listValSeclendAssetPeriodDeal){
         	if(StringUtil.equals(InterfCommonConstant.接口模式_指定账套,valModel) && !bookSetIds.contains(dto.getProdId())){
         		continue;
         	}
   	    	 dto.setCreateTime(resovleDate);
   	         /**add by chendijun 20190412 start**/
   	    	 //定义 变量.债券基本信息Dto
   	    	 //调用共通设计->【详细设计-接口共通方法设计.xlsx】->标的类金融产品信息查询SHEET
   	    	 TargetFinProduct valTargetFinProduct = interfCommonService.getTargetFinProduct(dto.getSecId());
             /**add by chendijun 20190412 end**/
   	    	 TrdProductDeal valFinProductDeal = null;

             /**modify by chendijun 20190412 start**/
   	    	 if(DictCodeList.交易方向_融入债券还本.getValue().equals(dto.getPs())
            		|| DictCodeList.交易方向_本金返还.getValue().equals(dto.getPs())
            		|| DictCodeList.交易方向_融入债券派本到账.getValue().equals(dto.getPs())){
   	    	     //金融产品交易
                 valFinProductDeal = getValFinProductDeal(dto,dto.getPs());
                 /**add by wujie on 20190418 start**/
                 valFinProductDeal.setTradeMarket(valTargetFinProduct.getTradeMarket());
                 /**add by wujie on 20190418 end**/


   	    		//还本到期，拆交易
   	    		if(dto.getActInt()!=null && BigDecimal.ZERO.compareTo(dto.getActInt())<0){

   	    			String bookType = "";
   	    			if(DictCodeList.交易方向_融入债券还本.getValue().equals(dto.getPs())){
   	    				bookType = DictCodeList.交易方向_融入债券收息.getValue();
   	    			}
   	    			else if(DictCodeList.交易方向_本金返还.getValue().equals(dto.getPs())){
   	    				bookType = DictCodeList.交易方向_利息返还.getValue();
   	    			}
   	    			else if(DictCodeList.交易方向_融入债券派本到账.getValue().equals(dto.getPs())){
   	    			    bookType = DictCodeList.交易方向_融入债券派息到账.getValue();
   	    			}
   	    		    /**
   	    		     * 收息 START
   	    		     */
   	    		    //金融产品交易
   	    		    TrdProductDeal valFinProductDeal_X = getValFinProductDeal(dto,bookType);
   	    		    /**add by wujie on 20190418 start**/
   	    		    valFinProductDeal_X.setTradeMarket(valTargetFinProduct.getTradeMarket());
                    /**add by wujie on 20190418 end**/
   	    			listValFinProductDeal.add(valFinProductDeal_X);
                    // 估值核算交易扩展表
                    TrdProductDealAdd TrdProductDealExt_X=getProductDealExt(dto,valFinProductDeal_X);
                    listValFinProductDealExt.add(TrdProductDealExt_X);

	   	   	    	// 入账信息表   账务明细表、账务明细代码表调用共通
	   	   	    	InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal_X, dto.getProdId());
	   	   	    	listValDealAccounting.add(dealAccounting.getBokDealAccounting());
	   	   	    	listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());

	   	   	    	BokDetailId valBokDetailId = dealAccounting.getBokDetailId();
	   	   	    	valBokDetailId.setDetailSubjectNoTail(valTargetFinProduct.getFinprodId());
	   	   	    	valBokDetailId.setDetailSubnameTail(valTargetFinProduct.getFinprodAbbr());
	   	   	    	listValBokDetailId.add(valBokDetailId);

	   	   	    	//结算指令
	   	   	    	/**modify by wujie on 20190118 start
	   	   	    	BokDealAccounting valDealAccounting = dealAccounting.getBokDealAccounting();
	   	   	    	Map<String, Object> map = this.getvalOrders(valFinProductDeal_X,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
	   	   	    	if(map.get("ORDERS")!=null){
	   	   	    		listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
	   	   	    	}
	   	   	    	if(map.get("ORDERS_EXT")!=null){
	   	   	    		listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
	   	   	    	}
	   	   	    	**/
			   	   	  Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal_X,
		              		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
		              if(map.get("ORDERS")!=null){
		              	listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
		              }
		              if(map.get("ORDERS_EXT")!=null){
		              	listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
		              }
		              if(map.get("ACCOUNTING")!=null){
		              	 listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
		              }
		              if(map.get("ACCOUNTING_DETAI")!=null){
		              	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
		              }
		              if(map.get("BOK_DETAIL_ID")!=null){
		              	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
		              }
		              /**modify by wujie on 20190118 end**/

   	    			/**收息 END**/

   	    		}
   	    	}else{
   	    	    valFinProductDeal = getValFinProductDeal(dto,dto.getPs());

   	    	    /**add by wujie on 20190418 start**/
   	    	    valFinProductDeal.setTradeMarket(valTargetFinProduct.getTradeMarket());
                /**add by wujie on 20190418 end**/
   	    	}

   	    	 listValFinProductDeal.add(valFinProductDeal);

             // 估值核算交易扩展表
   	    	 TrdProductDealAdd TrdProductDealExt=getProductDealExt(dto,valFinProductDeal);
   	    	 listValFinProductDealExt.add(TrdProductDealExt);

   	    	 // 入账信息表   账务明细表、账务明细代码表调用共通
   	         InterfProductDealBookCommonDto dealAccounting = interfCommonService.dealAccounting(valFinProductDeal, dto.getProdId());
   	         listValDealAccounting.add(dealAccounting.getBokDealAccounting());
   	         listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());

   	         BokDetailId valBokDetailId = dealAccounting.getBokDetailId();

             valBokDetailId.setDetailSubjectNoTail(valTargetFinProduct.getFinprodId());
             valBokDetailId.setDetailSubnameTail(valTargetFinProduct.getFinprodAbbr());

   	         listValBokDetailId.add(valBokDetailId);

   	          //结算指令
   	         /** modify by wujie on 20190118 start
	   	      BokDealAccounting valDealAccounting = dealAccounting.getBokDealAccounting();
	   	      Map<String, Object> map = this.getvalOrders(valFinProductDeal,dto.getProdId(),valDealAccounting.getBooksetId(),dealAccounting.getAcctSubjectType());
	   	      if(map.get("ORDERS")!=null){
	   	    	  listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
	   	      }
	   	      if(map.get("ORDERS_EXT")!=null){
	   	    	  listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
	   	      }
	   	      **/
	   	      StlOrders stlOrders = this.getStlOrders(dto,valFinProductDeal);
	          Map<String, Object> map = interfCommonService.instructionGenerate_Interf(valFinProductDeal,stlOrders,
	          		dto.getProdId(), dealAccounting.getAcctSubjectType(),null);
	          if(map.get("ORDERS")!=null){
	          	listValOrders.addAll((List<StlOrders>)map.get("ORDERS"));
	          }
	          if(map.get("ORDERS_EXT")!=null){
	          	listValOrdersExt.addAll((List<StlOrdersExt>)map.get("ORDERS_EXT"));
	          }
	          if(map.get("ACCOUNTING")!=null){
	          	 listValDealAccounting.addAll((List<BokDealAccounting>)map.get("ACCOUNTING"));
	          }
	          if(map.get("ACCOUNTING_DETAI")!=null){
	          	listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)map.get("ACCOUNTING_DETAI"));
	          }
	          if(map.get("BOK_DETAIL_ID")!=null){
	          	listValBokDetailId.addAll((List<BokDetailId>)map.get("BOK_DETAIL_ID"));
	          }
	          /**modify by wujie on 20190118 end**/

	          /**add by wujie on 20190322 start reamrk:通道投资费用交易生成**/
	            ChannelFeeInfoDeal channelFeeInfoDeal = this.getChannelFeeInfoDeal(dto, dealAccounting.getAcctSubjectType());
	            if(channelFeeInfoDeal != null){
	                Map<String,Object> feedealMap = interfCommonService.assetChlFeeLend(valFinProductDeal, channelFeeInfoDeal);

	                if(feedealMap.get("TrdProductDeal")!=null){
	                    listValFinProductDeal.add((TrdProductDeal)feedealMap.get("TrdProductDeal"));
	                }

	                if(feedealMap.get("TrdProductDealExt")!=null){
	                    listValFinProductDealExt.add((TrdProductDealAdd)feedealMap.get("TrdProductDealExt"));
	                }

	                if(feedealMap.get("BokDealAccounting_List")!=null){
	                    listValDealAccounting.addAll((List<BokDealAccounting>)feedealMap.get("BokDealAccounting_List"));
	                }

	                if(feedealMap.get("BokDealAccountingDetail_List")!=null){
	                    listValDealAccountingDetail.addAll(( List<BokDealAccountingDetail>)feedealMap.get("BokDealAccountingDetail_List"));
	                }

	                if(feedealMap.get("BokDetailId_list")!=null){
	                    listValBokDetailId.addAll((List<BokDetailId>)feedealMap.get("BokDetailId_list"));
	                }

	                if(feedealMap.get("StlOrders_List")!=null){
	                    listValOrders.addAll((List<StlOrders>)feedealMap.get("StlOrders_List"));
	                }
	                if(feedealMap.get("StlOrdersExt_List")!=null){
	                    listValOrdersExt.addAll((List<StlOrdersExt>)feedealMap.get("StlOrdersExt_List"));
	                }
	            }
	            /**add by wujie on 20190322 end reamrk:通道投资费用交易生成**/
   	     }
   	  resultMap.put("listValFinProductDeal", listValFinProductDeal);
      resultMap.put("listValFinProductDealExt", listValFinProductDealExt);
      resultMap.put("listValDealAccounting", listValDealAccounting);
      resultMap.put("listValDealAccountingDetail", listValDealAccountingDetail);
      resultMap.put("listValBokDetailId", listValBokDetailId);
      resultMap.put("listValOrders", listValOrders);
      resultMap.put("listValOrdersExt", listValOrdersExt);

      valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
  	  resultMap.put("valInterflogList", valInterflogList);

      return  resultMap;
    }

    /**
     * 接收 结算指令信息
     * @param dto
     * @return
     */
    private StlOrders getStlOrders(ValSeclendAssetPeriodDeal dto, TrdProductDeal valFinProductDeal){
    	StlOrders result = new StlOrders();
		//modify by yanya on 20210204 判定为空包含空字符
		//if(dto.getMyAcctNo() == null && dto.getCounterAcctNo() == null){
		if(StringUtil.isEmpty(dto.getMyAcctNo()) && StringUtil.isEmpty(dto.getCounterAcctNo()) ){
    		return null;
    	}
    	result.setMyAcctNo(dto.getMyAcctNo());
    	result.setCounterAcctNo(dto.getCounterAcctNo());
    	result.setSettleDate(dto.getFundSettleDate());
    	result.setSettleAmt(valFinProductDeal.getTradeAmt());
    	return result;
    }

    /**
     * 通道投资资产交易拆分_通道费用
     * add by wujie on 20190322 start
     */
    private ChannelFeeInfoDeal getChannelFeeInfoDeal(ValSeclendAssetPeriodDeal dto,String acctSubjectType){
        //接口.通道代码为空
        if(dto.getChannelId() == null){
            return null;
        }
        //接口.通道费用空
        if(dto.getChlFeeAmt()==null || dto.getChlFeeAmt().compareTo(BigDecimal.ZERO)==0){
            return null;
        }
        ChannelFeeInfoDeal deal = new ChannelFeeInfoDeal();
        //通道费用 = 接口.通道费用
        deal.setChlFeeAmt(dto.getChlFeeAmt());
        //是否随交易支付 = 接口.通道费是否随交易支付
        deal.setIsPayWithTrade(dto.getIsChlFeeAmt());
        //通道代码 = 接口.通道代码
        deal.setChannelId(dto.getChannelId());
        //产品代码 = 接口.产品代码
        deal.setProdId(dto.getProdId());

        deal.setAcctSubjectId(dto.getProdId() + FinalString.LAYERING_SPLIT + dto.getChannelId());

        deal.setAcctSubjectType(acctSubjectType);

        return deal;
    }

    private  TrdProductDeal getValFinProductDeal(ValSeclendAssetPeriodDeal dto,String bookType){
    	TrdProductDeal trdProductDeal=new TrdProductDeal();
    	//交易编号=调用共通
        String tradeId=interfCommonService.getTradeid(dto.getPs(), dto.getTradeDate(), "A");
        trdProductDeal.setTradeId(tradeId);
    	//业务类型=业务类型.债券借贷标的债券交易
    	trdProductDeal.setBusiType(DictCodeList.业务类型_债券借贷标的债券交易.getValue());
    	//金融产品代码=B.债券代码
    	trdProductDeal.setFinprodId(dto.getSecId());
    	//金融产品类型=B.金融产品类型.债券
    	trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
    	//投资目的 = B.投资目的.卖出 sell
    	trdProductDeal.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
    	//交易日=B.交易日
    	trdProductDeal.setTradeDate(dto.getTradeDate());
    	//起息日=B.起息日
    	trdProductDeal.setVdate(dto.getVdate());
    	//到期日=B.到期日
    	trdProductDeal.setMdate(dto.getMdate());
    	//交割日=B.交割日
    	trdProductDeal.setSettleDate(dto.getSettleDate());
    	//交易币种=B.币种
    	trdProductDeal.setCcy(dto.getCcy());
        //交易方向=B.交易方向
        trdProductDeal.setPs(bookType);
        //交易状态=B.数据动作标识
        trdProductDeal.setTradeStatus(dto.getDataActionSign());
        //是否结清=B.是否结清
        trdProductDeal.setIsClean(dto.getIsClean2());
        //关联系统交易编号=B.成交编号
        trdProductDeal.setfTradeId(dto.getTradeId());
        //创建时间 = 输入.接口.创建时间
        trdProductDeal.setCreateTime(dto.getCreateTime());
        //交易市场  = 流通市场.二级市场
        trdProductDeal.setTradeMarketMode(DictCodeListExt1.交易市场_二级市场.getValue());
		//资金账户  = B.资金账户 yangchen 20220406 start
		trdProductDeal.setMyAcctNo(dto.getMyAcctNo());
		//资金账户  = B.资金账户 yangchen 20220406 end

    	if(DictCodeList.交易方向_融入债券还本.getValue().equals(bookType)//T31
        		|| DictCodeList.交易方向_本金返还.getValue().equals(bookType)//T32
        		|| DictCodeList.交易方向_融入债券派本到账.getValue().equals(bookType)//T44
        		|| DictCodeList.交易方向_卖空债券还本.getValue().equals(bookType)//T33
				|| DictCodeList.交易方向_还本到账.getValue().equals(bookType)//T23
		){
    		//交易本金=B.实收本金
        	trdProductDeal.setPrinAmt(dto.getActCapi());
        	//交易金额=B.实收本金
            trdProductDeal.setTradeAmt(dto.getActCapi());
            // 分支序号 = interfCommonService.getBranch(输入.接口.到期日,数据字典：分支类型.应收本金)
            trdProductDeal.setBranch(interfCommonService.getBranch(dto.getMdate(),DictCodeList.分支类型_应收本金.getValue()));
//        	//利息总额=B.实收利息
//        	trdProductDeal.setIntAmt(BigDecimal.ZERO);

    	}else{
    		//交易本金=B.实收利息
//        	trdProductDeal.setPrinAmt(dto.getActInt());
        	//利息总额=B.实收利息
        	trdProductDeal.setIntAmt(dto.getActInt());
        	//交易金额=B.交易金额
        	trdProductDeal.setTradeAmt(dto.getActInt());
        	// 分支序号 = interfCommonService.getBranch(输入.接口.到期日,数据字典：分支类型.应收利息)
        	trdProductDeal.setBranch(interfCommonService.getBranch(dto.getMdate(),DictCodeList.分支类型_应收利息.getValue()));
    	}


        //原交易编号
        if(trdProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
            trdProductDeal.setpTradeId(interfCommonService.getFTradeId(trdProductDeal));
        }

        //通道代码 = a.通道代码
        trdProductDeal.setChlId(dto.getChannelId());
    	return  trdProductDeal;
    }


//    /**
//     * T1:金融产品交易表 接收
//     *
//     * @param dto 01-借贷交易
//     * @return 金融产品交易表
//     * @see
//     * @since 1.0
//     */
//    private  TrdProductDeal getValFinProductDeal(ValSeclendAssetPeriodDeal dto){
//    	TrdProductDeal trdProductDeal=new TrdProductDeal();
//    	//业务类型=业务类型.债券借贷交易
//    	trdProductDeal.setBusiType(DictCodeList.业务类型_债券借贷标的债券交易.getValue());
//    	//交易编号=调用共通
//    	String tradeId=interfCommonService.getTradeid(dto.getPs(), dto.getTradeDate(), "A");
//    	trdProductDeal.setTradeId(tradeId);
//    	//金融产品代码=B.债券ID
//    	trdProductDeal.setFinprodId(dto.getSecId());
//    	//金融产品类型=B.金融产品类型
//    	trdProductDeal.setFinprodType(DictCodeListExt1.金融产品类型_债券.getValue());
//    	// 分支序号 = 设置全局变量(默认为0）
//    	trdProductDeal.setBranch(InterfCommonConstant.分支序号_默认);
//    	//交易日=B.交易日
//    	trdProductDeal.setTradeDate(dto.getTradeDate());
//    	//起息日=B.起息日
//    	trdProductDeal.setVdate(dto.getVdate());
//    	//到期日=B.到期日
//    	trdProductDeal.setMdate(dto.getMdate());
//    	//交割日=B.交割日
//    	trdProductDeal.setSettleDate(dto.getSettleDate());
//    	//交易币种=B.币种
//    	trdProductDeal.setCcy(dto.getCcy());
//    	//交易本金=B.实收本金
//    	trdProductDeal.setPrinAmt(dto.getActCapi());
//    	//利息总额=B.实收利息
//    	trdProductDeal.setIntAmt(dto.getActInt());
//    	//交易金额=B.交易金额
//    	trdProductDeal.setTradeAmt(dto.getTardeAmt());
//    	//交易方向=B.交易方向
//    	trdProductDeal.setPs(dto.getPs());
//    	//交易状态=B.数据动作标识
//    	trdProductDeal.setTradeStatus(dto.getDataActionSign());
//
//    	//是否结清=B.是否结清
//    	trdProductDeal.setIsClean(dto.getIsClean2());
//    	//关联系统交易编号=B.成交编号
//    	trdProductDeal.setfTradeId(dto.getTradeId());
//
//    	trdProductDeal.setCreateTime(dto.getCreateTime());
//
//        //原交易编号
//        if(trdProductDeal.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())){
//            trdProductDeal.setpTradeId(interfCommonService.getFTradeId(trdProductDeal));
//        }
//    	return  trdProductDeal;
//    }
    /**
     * T2:估值核算交易扩展表 接收
     *
     * @param dto 01-债券借贷交易
     * @return 估值核算交易扩展表
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getProductDealExt(ValSeclendAssetPeriodDeal dto,TrdProductDeal valFinProductDeal){
    	//1.定义变量
    	TrdProductDealAdd trdProductDealExt=new TrdProductDealAdd();
    	//交易编号=T1.交易编号
    	trdProductDealExt.setTradeId(valFinProductDeal.getTradeId());
    	//投资组合代码 = B.产品代码
    	trdProductDealExt.setPortfolioId(dto.getProdId());
//    	//理财产品代码=B.产品代码
//    	trdProductDealExt.setProdId(dto.getProdId());
    	/** modify by wujie on 20181115 start remark:投资组合投资时应赋值[表.投资组合代码]字段
        trdProductDealExt.setProdId(dto.getProdId());
        **/
//        if(DictCodeListExt1.金融产品类型_投资组合.getValue().equals(finprodType)){
//        	trdProductDealExt.setPortfolioId(dto.getProdId());
//        }else{
//        	trdProductDealExt.setProdId(dto.getProdId());
//        }
        /**modify by wujie on 20181115 end remark:投资组合投资时应赋值[表.投资组合代码]字段**/
//    	//是否有结算指令
//    	trdProductDealExt.setHasOrder(dto.getIsClean());
    	//生成方式=固定值：接口接入
    	trdProductDealExt.setGenType(DictCodeListExt1.生成方式_接口接入.getValue());

    	trdProductDealExt.setMpFinprodId(dto.getFinprodId());
    	trdProductDealExt.setMpBranch(new Long(0));
    	return trdProductDealExt;
    }

//    /**
//     * 结算指令的生成
//     * @param valFinProductDeal
//     * @param valBooksetInfoDtoList
//     * @return
//     */
//    private Map<String, Object> getvalOrders(TrdProductDeal valFinProductDeal,String prodId,String booksetId,String acctSubjectType) {
//    	 Map<String, Object> map = new HashMap<String,Object>();
//    	 List<StlOrders> listValOrders = new ArrayList<StlOrders>();
//         List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
//
//    	 ProductDealDto productDealDto=new ProductDealDto();
//    	 productDealDto.setTradeStatus(valFinProductDeal.getTradeStatus());  //* 交易状态
//         productDealDto.setTradeId(valFinProductDeal.getTradeId());    // * 交易编号
//         productDealDto.setTradeDate(valFinProductDeal.getTradeDate()); //  * 交易日
//         productDealDto.setEffDate(valFinProductDeal.getSettleDate());//交割日
//         productDealDto.setPs(valFinProductDeal.getPs());//  * 交易方向
//         productDealDto.setTardeAmt(valFinProductDeal.getTradeAmt()); //  * 交易金额
//         productDealDto.setFinprodId(prodId); // * 金融产品代码
//         /**modify by wujie on 20190107 start
//         productDealDto.setFinprodType(interfCommonService.queryProductType(prodId));
//         **/
//         productDealDto.setFinprodType(acctSubjectType);
//         /**modify by wujie on 20190107 end**/
//         productDealDto.setBusiType(valFinProductDeal.getBusiType());// * 业务类型
//         productDealDto.setProdId(valFinProductDeal.getFinprodId()); //  * 理财产品代码
//         productDealDto.setCcy(valFinProductDeal.getCcy()); //  * 币种
//         productDealDto.setIsTrade(DictCodeList.是否_是.getValue());   //  * 是否随交易出账
//         productDealDto.setBookNo(booksetId);
//         productDealDto.setCreateTime(valFinProductDeal.getCreateTime());
//         //原交易编号
//         productDealDto.setpTradeId(valFinProductDeal.getpTradeId());
//         String insType = "";
//         if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_融入债券收息.getValue())){
//    		 insType=DictCodeListExt1.指令类型_债券借贷_融入期间标的债券收息.getValue();
//    		 map = interfCommonService.settlementInstructionGeneration(productDealDto,
//        			 insType,DictCodeList.是否_是.getValue());
//    	 }
//         else if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_融入债券利息返还.getValue())){
//    		 insType=DictCodeListExt1.指令类型_债券借贷_融入期间标的债券付息.getValue();
//    		 map = interfCommonService.settlementInstructionGeneration(productDealDto,
//        			 insType,DictCodeList.是否_是.getValue());
//    	 }
////    	 else if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_卖空债券结息.getValue())){
////    		 insType=DictCodeListExt1.指令类型_债券借贷_融入债券卖出后收息.getValue();
////    		 map = interfCommonService.settlementInstructionGeneration(productDealDto,
////        			 insType,DictCodeList.是否_是.getValue());
////    	 }
//    	 else if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_融入债券还本.getValue())){
//    		 insType=DictCodeListExt1.指令类型_债券借贷_融入期间标的债券还本.getValue();
//    		 map = interfCommonService.settlementInstructionGeneration(productDealDto,
//        			 insType,DictCodeList.是否_是.getValue());
//    	 }
////    	 else if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_卖空债券还本.getValue())){
////    		 insType=DictCodeListExt1.指令类型_债券借贷_融入债券卖出后还本.getValue();
////    		 map = interfCommonService.settlementInstructionGeneration(productDealDto,
////        			 insType,DictCodeList.是否_是.getValue());
////    	 }
//    	 else if(valFinProductDeal.getPs().equals(DictCodeList.交易方向_融入债券本金返还.getValue())){
//    		 insType=DictCodeListExt1.指令类型_债券借贷_融入期间标的券本金支付.getValue();
//    		 map = interfCommonService.settlementInstructionGeneration(productDealDto,
//        			 insType,DictCodeList.是否_是.getValue());
//    	 }
//         StlOrders ValOrders=(StlOrders)map.get("ORDERS");
//         StlOrdersExt ValOrdersExt=(StlOrdersExt)map.get("ORDERS_EXT");
//         listValOrders.add(ValOrders);
//         listValOrdersExt.add(ValOrdersExt);
//
//         map.put("ORDERS", listValOrders);
//         map.put("ORDERS_EXT", listValOrdersExt);
//         return map;
//    }
}
