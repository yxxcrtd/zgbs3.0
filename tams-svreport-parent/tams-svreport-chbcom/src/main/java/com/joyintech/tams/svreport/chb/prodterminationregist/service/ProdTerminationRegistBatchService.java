package com.joyintech.tams.svreport.chb.prodterminationregist.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.batch.report.common.db.SvrDBUtil;
import com.joyintech.tams.common.svreport.chb.constant.SVReportGlobalConstant;
import com.joyintech.tams.common.svreport.chb.util.ChbCcyConvertUtil;
import com.joyintech.tams.common.svreport.chb.util.SvrDataUtil;
import com.joyintech.tams.common.svreport.datacheck.beforeinsert.dto.ChbCheckTableInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.report.common.constant.BatchBeanName;
import com.joyintech.tams.report.common.util.ChbDeadlineUtil;
import com.joyintech.tams.svreport.chb.common.SvrDataStatusUtil;
import com.joyintech.tams.svreport.chb.datacheck.beforeinsert.service.ChbCommonCheckService;
import com.joyintech.tams.svreport.chb.prodterminationregist.dao.ProdTerminationRegist001Mapper;
import com.joyintech.tams.svreport.chb.prodterminationregist.dto.ProdForTerminationDto;
import com.joyintech.tams.svreport.chb.prodterminationregist.dto.ProdTerminationRegist001Dto;
import com.joyintech.tams.svreport.position.dto.ChbPrdFeePosition;
import com.joyintech.tams.svreport.position.dto.ChbPrdPosition;
import com.joyintech.tams.systemtools.constant.DataIndexSequenceType;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 功能:产品终止登记批处理
 * @since 1.7.0
 * @version 1.7.0
 * @作者：weiyuqiang
 * @创建时间：2019年5月30日
 * @修改记录：
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProdTerminationRegistBatchService extends SvrDataStatusUtil {

    @Autowired
    private ProdTerminationRegist001Mapper prodTerminationRegist001Mapper;
    
    @Autowired
    private SvrDBUtil svrDBUtil;
    
	@Value("${application.reportInsertCount}")
	public  int reportInsertCount;
    /**
     * 入库前校验用Service
     */
    @Autowired
    private ChbCommonCheckService chbCommonCheckService;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ProdTerminationRegistBatchService.class);
    
	public void generateRegistData(Date executeDate,List<String> prodIdList) {
		// 设置批处理bean
		setBatchBeanName(BatchBeanName.产品终止登记报送);
		// 批处理开始
		logInfoBatchStart();
		generateTermRegistData(executeDate, prodIdList);
		logInfoBatchEnd();

	}

    /**
     * @Description 生成终止登记数据
     * @param executeDate 跑批日
     * @param prodIdList 跑批产品id
     * @version 1.7 2019年5月24日
     */
    public void generateTermRegistData(Date executeDate,List<String> prodIdList){
        // 查询所有需要生成产品终止登记批处理的产品
    	logger.debug("查询所有需要生成产品终止登记批处理的产品--------开始---------");
        List<ProdForTerminationDto> prodIds = prodTerminationRegist001Mapper
                .getProdIdsByEndSub(DateUtil.format(executeDate), DictCodeListExt2.业务种类_到期兑付.getValue(),prodIdList);
        logger.debug("查询所有需要生成产品终止登记批处理的产品--------结束---------");
        // 产品基础信息
        List<ProdForTerminationDto> list = new ArrayList<ProdForTerminationDto>();
        // 费用头寸信息
        List<ChbPrdFeePosition> feeList = new ArrayList<ChbPrdFeePosition>();
        // 产品交易头寸信息
        List<ChbPrdPosition> dealList = new ArrayList<ChbPrdPosition>();
        //新增数据
        List<ProdTerminationRegist001Dto> insertlist = new ArrayList<ProdTerminationRegist001Dto>();
        //删除数据
        List<String> deletelist = new ArrayList<String>();
        logger.debug("查询已登记的数据、产品费用头寸、产品头寸--------开始---------");
        if (!CollectionUtils.isEmpty(prodIds)) {
            List<List<ProdForTerminationDto>> endprods = createList(prodIds,20);
            Map<String,ProdTerminationRegist001Dto> regMap = new HashMap<String,ProdTerminationRegist001Dto>();
            //分批查询
            for(int i=0;i<endprods.size();i++){
                //查询已登记数据
                List<ProdTerminationRegist001Dto> reglist = prodTerminationRegist001Mapper.selectRegPerProdList(endprods.get(i));
                if(!CollectionUtils.isEmpty(reglist)){
                    for(ProdTerminationRegist001Dto regdto : reglist){
                        regMap.put(regdto.getProdId(), regdto);
                    }
                }
                // 产品基础信息
                List<ProdForTerminationDto> prodlist = prodTerminationRegist001Mapper.getProdInfoByEndSub(DictCodeListExt2.业务种类_到期兑付.getValue(),
                        DictCodeListExt2.报送状态_未报送.getValue(), "01", endprods.get(i));
                // 产品交易头寸信息
                List<ChbPrdPosition> deal = prodTerminationRegist001Mapper.getPrdDealPositionList(endprods.get(i));
                list.addAll(prodlist);
//                feeList.addAll(fee);
                dealList.addAll(deal);
            }
            // 费用头寸信息
            feeList = prodTerminationRegist001Mapper.getPrdFeePositionList(null);
            logger.debug("查询已登记的数据、产品费用头寸、产品头寸--------结束---------");
            if (!CollectionUtils.isEmpty(list)){
                Map<String,ProdForTerminationDto> prodidmap = new HashMap<String,ProdForTerminationDto>();
                //判断到期交易是否全是D状态
                for (ProdForTerminationDto prodForTerminationDto : list) {
                    if(!DictCodeListExt2.数据状态_删除.getValue().equals(prodForTerminationDto.getStatus())){
                        prodidmap.put(prodForTerminationDto.getProdId(), prodForTerminationDto);
                    }else if(prodidmap.get(prodForTerminationDto.getProdId())==null&&DictCodeListExt2.数据状态_删除.getValue().equals(prodForTerminationDto.getStatus())){
                        prodidmap.put(prodForTerminationDto.getProdId(), prodForTerminationDto);
                    }
                }
                Iterator<Map.Entry<String, ProdForTerminationDto>> entries = prodidmap.entrySet().iterator();
                logger.debug("终止登记数据处理--------开始---------");
                while (entries.hasNext()) {
                    Map.Entry<String, ProdForTerminationDto> terminationMap = entries.next();
                    ProdForTerminationDto prodForTerminationDto = terminationMap.getValue();
                    // 临时费用list
                    List<ChbPrdFeePosition> listFee = new ArrayList<ChbPrdFeePosition>();
                    // 临时交易list
                    List<ChbPrdPosition> listDeal = new ArrayList<ChbPrdPosition>();
                    BigDecimal balanceQtysum = BigDecimal.ZERO;
                    // 费用处理
                    for (ChbPrdFeePosition chbPrdFeeDeal : feeList) {
                        if (prodForTerminationDto.getProdId().equals(chbPrdFeeDeal.getProdId())) {
                            listFee.add(chbPrdFeeDeal);
                        }
                    }
                    // 交易处理
                    for (ChbPrdPosition chbPrdDeal : dealList) {
                        if (prodForTerminationDto.getProdId().equals(chbPrdDeal.getProdId())) {
                        	balanceQtysum = balanceQtysum.add(chbPrdDeal.getBalanceQty());
                            listDeal.add(chbPrdDeal);
                        }
                    }
                    prodForTerminationDto.setFeePositionList(listFee);
                    prodForTerminationDto.setDealPositionList(listDeal);

                    ProdTerminationRegist001Dto result = dataProcess(prodForTerminationDto, executeDate);

                    boolean iszero = false;
                    if(!CollectionUtils.isEmpty(listDeal)){
                        iszero = isAssetPosZero(listDeal.get(0).getCdate(), prodForTerminationDto.getProdId());
                    }

                    // add by zhangliang 20201109 数据流向处理 ===================================start
                    Map<String, ProdTerminationRegist001Dto> handleDataMap = handleDataStatus(result, regMap.get(prodForTerminationDto.getProdId()));
                    if (handleDataMap.containsKey(SvrDataStatusUtil.INSERT_DATA)){
                        ProdTerminationRegist001Dto subDto = handleDataMap.get(SvrDataStatusUtil.INSERT_DATA);
                        if(!StringUtil.equals(subDto.getStatus(), DictCodeListExt2.报送数据状态_删除.getValue())){
                            if (iszero&&BigDecimal.ZERO.compareTo(balanceQtysum)==0){
                                insertlist.add(subDto);
                            }
                        }else{
                            insertlist.add(subDto);
                        }
                    }
                    if (handleDataMap.containsKey(SvrDataStatusUtil.DELETE_DATA)){
                        deletelist.add(handleDataMap.get(SvrDataStatusUtil.DELETE_DATA).getProdId());
                    }



//                    if (prodForTerminationDto.getRegSeq() == null) {
//                        //未报送
//                        if(DictCodeListExt2.数据状态_删除.getValue().equals(prodForTerminationDto.getStatus())){
//                        	//删除数据(未报送的删除)
//                            deletelist.add(prodForTerminationDto.getProdId());
//                            continue;
//                        }else{
//                        	// 判断产品持仓,剩余现金是否为零
//                        	if(iszero&&BigDecimal.ZERO.compareTo(balanceQtysum)==0){
//		                        //设置数据状态
//		                        result.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
//		                        result.setCreateUser(CommonUtil.getUserName());
//		                        result.setUpdateUser(CommonUtil.getUserName());
//		                        insertlist.add(result);
//                        	}
//                            //删除数据(未报送的删除)
//                            deletelist.add(prodForTerminationDto.getProdId());
//                        }
//                    }else {
//                    	// 删除数据(未报送的删除)
//                        deletelist.add(prodForTerminationDto.getProdId());
//                        // 判断产品持仓,剩余现金是否为零
//                    	if(iszero&&BigDecimal.ZERO.compareTo(balanceQtysum)==0){
//                    		//已报送
//                            //设置数据状态
//                            if(DictCodeListExt2.数据状态_删除.getValue().equals(prodForTerminationDto.getStatus())){
//                            	if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(prodForTerminationDto.getProdId()).getStatus())){
//                            		continue;
//                            	}
//                                result.setStatus(DictCodeListExt2.报送数据状态_删除.getValue());
//                            }else{
//                            	if(DictCodeListExt2.报送数据状态_删除.getValue().equals(regMap.get(prodForTerminationDto.getProdId()).getStatus())){
//                            		result.setStatus(DictCodeListExt2.报送数据状态_新建.getValue());
//                            	}else if(compareData(regMap.get(result.getProdId()),result)){// 判断要素表数据有没有修改
//                                    continue;
//                                }else{
//                                	result.setStatus(DictCodeListExt2.报送数据状态_修改.getValue());
//                                }
//                            }
//                            result.setCreateUser(CommonUtil.getUserName());
//                            result.setUpdateUser(CommonUtil.getUserName());
//                            insertlist.add(result);
//                    	}
//                    }
                    // add by zhangliang 20201109 数据流向处理 ===================================end
                }
            }
        }
        
      //update by leipeng 插入的数据获取表 内的实际登记编码
	       if(CommonUtil.isBAmodel()){
	           Map<String,String> data = svrDBUtil.getAllRegCode();
	           
	           for (ProdTerminationRegist001Dto dto : insertlist) {
	        	   dto.setIssuePartyId(data.get(dto.getProdId()));
	           }
	   		}
        
        // add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 start
        insertlist = chbCommonCheckService.checkData(insertlist, ChbCheckTableInfo.终止登记SUB);
        // add by dubaojun 2020-10-13 入库前需要进行数据检验，检验错误的数据不入库 end
        logger.debug("终止登记数据处理--------结束---------");
        int singalCount = Integer.parseInt( null == SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT)?"100" : SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_INSERT_COUNT));
        int deleteCount = deletelist.size();
        int insertCount = insertlist.size();
        logger.debug("终止登记数据删除--------开始---------");
        if(deleteCount>0){
            if(deleteCount<=singalCount){
                prodTerminationRegist001Mapper.deleteBatch(deletelist);
            }
            else{
                for(int i = 0; i < deleteCount/singalCount ; i++){
                    prodTerminationRegist001Mapper.deleteBatch(deletelist.subList(0, singalCount));
                    deletelist.removeAll(deletelist.subList(0, singalCount));
                }
                if(!CollectionUtils.isEmpty(deletelist)){
                    prodTerminationRegist001Mapper.deleteBatch(deletelist);
                }
            }
        }
        logger.debug("终止登记数据删除--------结束---------");
        logger.debug("终止登记数据插入--------开始---------");
        if(insertCount>0){
            if(insertCount<=reportInsertCount){
                prodTerminationRegist001Mapper.insertBatch(insertlist);
            }
            else{
                for(int i = 0; i < insertCount/reportInsertCount ; i++){
                    prodTerminationRegist001Mapper.insertBatch(insertlist.subList(0, reportInsertCount));
                    insertlist.removeAll(insertlist.subList(0, reportInsertCount));
                }
                if(!CollectionUtils.isEmpty(insertlist)){
                    prodTerminationRegist001Mapper.insertBatch(insertlist);
                }
            }
        }
        logger.debug("终止登记数据插入--------结束---------");
    }
    
    /**
     * @Description 终止登记数据详细处理
     * @param arg0 生成前的dto
     * @param executeDate 跑批日
     * @return result 生成后的dto
     */
    public ProdTerminationRegist001Dto dataProcess(ProdForTerminationDto arg0,Date executeDate) {
        ProdTerminationRegist001Dto result = new ProdTerminationRegist001Dto();
        //初始化金额数据，避免后面check被筛选掉
        initAmtData(result);
        //汇率来源
        String exchangeSource = SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_EXCHANGE_SOURCE);
        Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
        /**
         * 处理费用头寸信息
         */
        List<ChbPrdFeePosition> feeList = arg0.getFeePositionList();
        for (ChbPrdFeePosition chbPrdFeeInfo : feeList) {
            // 汇率换算
            BigDecimal rateAmt = ChbCcyConvertUtil.exchangeMoney(chbPrdFeeInfo.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), chbPrdFeeInfo.getCdate(), chbPrdFeeInfo.getAmt(), rateMap, exchangeSource);
            // 根据费用类型往result中set值
            if (DictCodeListExt2.费用类型_本机构费用.getValue().equals(chbPrdFeeInfo.getFeeType())) {
                // 银行实现收入
                result.setProfit(result.getProfit().add(rateAmt));
                if (DictCodeListExt2.费用二级类型_本机构托管费.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setCustFeeIn(result.getCustFeeIn().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_本机构管理费.getValue().equals(chbPrdFeeInfo.getFeeSectype())||
                        DictCodeListExt2.费用二级类型_中收费.getValue().equals(chbPrdFeeInfo.getFeeSectype())||
                        DictCodeListExt2.费用二级类型_浮动费.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setManFeeIn(result.getManFeeIn().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_本机构销售手续费.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setCouFeeIn(result.getCouFeeIn().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_本机构其他产品费用.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setOtherFeeIn(result.getOtherFeeIn().add(rateAmt));
                }
            } else if (DictCodeListExt2.费用类型_其他机构费用.getValue().equals(chbPrdFeeInfo.getFeeType())) {
                if (DictCodeListExt2.费用二级类型_其他机构托管费.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setCustFeeOut(result.getCustFeeOut().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_其他机构管理费.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setManFeeOut(result.getManFeeOut().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_其他机构销售手续费.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setCouFeeOut(result.getCouFeeOut().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_投资顾问费用.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setAdvFeeOut(result.getAdvFeeOut().add(rateAmt));
                } else if (DictCodeListExt2.费用二级类型_其他机构其他产品费用.getValue().equals(chbPrdFeeInfo.getFeeSectype())) {
                    result.setOtherFeeOut(result.getOtherFeeOut().add(rateAmt));
                }
            }
        }
        /**
         * 处理交易头寸信息
         */
        List<ChbPrdPosition> dealList = arg0.getDealPositionList();
        // 实际募集金额
        BigDecimal raiseAmt = BigDecimal.ZERO;
        // 兑付客户总份额
        BigDecimal customQty = BigDecimal.ZERO;
        // 兑付客户总金额
        BigDecimal customAmt = BigDecimal.ZERO;
        // 兑付客户收益
        BigDecimal customProfit = BigDecimal.ZERO;
        // 终止日
        Date endDate = new Date();
        for (ChbPrdPosition chbPrdDeal : dealList) {
            // 实际终止日
            endDate = chbPrdDeal.getCdate();
            // 实际募集金额
            raiseAmt = raiseAmt.add(ChbCcyConvertUtil.exchangeMoney(chbPrdDeal.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), chbPrdDeal.getCdate(), chbPrdDeal.getRaiseamt(), rateMap, exchangeSource));
            // 兑付客户总份额(份额不折算人民币)
            customQty = customQty.add(chbPrdDeal.getCustqty());
            // 兑付客户总金额
            customAmt = customAmt.add(ChbCcyConvertUtil.exchangeMoney(chbPrdDeal.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), chbPrdDeal.getCdate(), chbPrdDeal.getCustamt(), rateMap, exchangeSource));
            // 兑付客户收益
            customProfit = customProfit.add(ChbCcyConvertUtil.exchangeMoney(chbPrdDeal.getCcy(), DictCodeListExt1.币种_中债_人民币.getValue(), chbPrdDeal.getCdate(), chbPrdDeal.getTotalIntamt(), rateMap, exchangeSource));
        }
        result.setCustomQty(customQty);
        result.setCustomAmt(customAmt);
        result.setCustomProfit(customProfit);
        if (raiseAmt.compareTo(BigDecimal.ZERO) != 0) {
            /**
             * 客户实际年化收益率=兑付客户收益÷实际募集金额×全年实际天数÷产品实际运作天数
             */
            BigDecimal cusYearYield = BigDecimal.ZERO;
            BigDecimal basic = new BigDecimal(365);
            if (result.getCustomProfit().compareTo(BigDecimal.ZERO) != 0) {
                if(DictCodeList.计息基础_A_360.getValue().equals(arg0.getBasic())){
                    basic = new BigDecimal(360);
                }
                cusYearYield = result.getCustomProfit().multiply(basic).multiply(new BigDecimal(100))
                        .divide(raiseAmt.multiply(
                                new BigDecimal(DateUtil.getBetweenDayNumber(arg0.getVdate(),endDate))), 5,
                                BigDecimal.ROUND_HALF_UP);
            }
            result.setCusYearYield(cusYearYield);
            /**
             * 产品端实际年化收益率=（兑付客户收益+银行实际实现收入）÷实际募集金额×全年实际天数÷产品实际运作天数
             */
            BigDecimal prdYearYield = BigDecimal.ZERO;
            BigDecimal allProfit = result.getCustomProfit().add(result.getProfit());
            if (allProfit.compareTo(BigDecimal.ZERO) != 0) {
                prdYearYield = result.getCustomProfit().add(result.getProfit()).multiply(basic).multiply(new BigDecimal(100))
                        .divide(raiseAmt.multiply(
                                new BigDecimal(DateUtil.getBetweenDayNumber(arg0.getVdate(),endDate))), 5,
                                BigDecimal.ROUND_HALF_UP);
            }
            result.setPrdYearYield(prdYearYield);
        }
        //产品实际终止日
        result.setMdate(endDate);
        //报送状态
        result.setSendStatus(DictCodeListExt2.报送状态_未报送.getValue());
        //主键
        result.setRegistSeqno(IDGenerator.generate(36));
        // 报送流水号
        result.setSumitSeq(CommonUtil.getSequenceIndexData(DataIndexSequenceType.中债登记管理));
        //产品id
        result.setProdId(arg0.getProdId());
        //产品登记编码
        result.setProdRegCode(arg0.getRegCode());
        result.setProdRegistCode(arg0.getProdRegistCode());
        //机构代码
        result.setOrgCode(CommonUtil.getOrgCode());
        //部门代码
        result.setDeptCode(CommonUtil.getDeptCode());
        //发行机构代码取值
        result.setIssuePartyId(SvrDataUtil.getSysParam(SVReportGlobalConstant.SVR_ISSUE_PARTY_ID));
//        if(StringUtil.isEmpty(result.getIssuePartyId())){
//            String issuePartyId = CommonUtil.selectBankCode();
//            result.setIssuePartyId(issuePartyId);
//        }
        //数据日期
        result.setStatusDate(executeDate);


        //-----add by lingxq on 2021-06-07 for 费用计算后保留两位小数------>>>>>
        setFeeScale(result);
        //-----add by lingxq on 2021-06-07 for 费用计算后保留两位小数------<<<<<

        // add by zhangliang 20201109 数据流向处理 ===================================start
        result.setStatus(arg0.getStatus());
        // add by zhangliang 20201109 数据流向处理 ===================================end


        // 报送截止日生成
        //当前批次报送时点集合
        Map<String, List<Date>> dateMap = new HashMap<String, List<Date>>();
        List<Date> dateList = ChbDeadlineUtil.getChbEndDate(DictCodeListExt2.报表名称_产品终止登记.getValue(),
                arg0.getStatus(),dateMap, endDate);
        if (!StringUtil.isEmptyList(dateList)) {
            result.setChbSubmitDeadline(dateList.get(0));
            result.setSubmitDeadline(dateList.get(1));
        }


        return result;
    }

    /**
     * 费用保留两位小数
     * @param result
     */
    private void setFeeScale(ProdTerminationRegist001Dto result) {
        if(null != result.getProfit()) {
            result.setProfit(result.getProfit().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getCustFeeIn()) {
            result.setCustFeeIn(result.getCustFeeIn().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getManFeeIn()) {
            result.setManFeeIn(result.getManFeeIn().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getCouFeeIn()) {
            result.setCouFeeIn(result.getCouFeeIn().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getOtherFeeIn()) {
            result.setOtherFeeIn(result.getOtherFeeIn().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getCustFeeOut()) {
            result.setCustFeeOut(result.getCustFeeOut().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getManFeeOut()) {
            result.setManFeeOut(result.getManFeeOut().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getCouFeeOut()) {
            result.setCouFeeOut(result.getCouFeeOut().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getAdvFeeOut()) {
            result.setAdvFeeOut(result.getAdvFeeOut().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getOtherFeeOut()) {
            result.setOtherFeeOut(result.getOtherFeeOut().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getCustomAmt()) {
            result.setCustomAmt(result.getCustomAmt().setScale(2, RoundingMode.HALF_UP));
        }
        if(null != result.getCustomProfit()) {
            result.setCustomProfit(result.getCustomProfit().setScale(2, RoundingMode.HALF_UP));
        }

        //-------费用因四舍五入差一分导致check不过进行倒减处理------->>>>>>>
        BigDecimal bankInt = null == result.getProfit() ? BigDecimal.ZERO :  result.getProfit();
        BigDecimal manFeeIn = null == result.getManFeeIn() ? BigDecimal.ZERO :  result.getManFeeIn();
        BigDecimal custFeeIn = null == result.getCustFeeIn() ? BigDecimal.ZERO :  result.getCustFeeIn();
        BigDecimal otherFeeIn = null == result.getOtherFeeIn() ? BigDecimal.ZERO :  result.getOtherFeeIn();
        BigDecimal couFeeIn = null == result.getCouFeeIn() ? BigDecimal.ZERO :  result.getCouFeeIn();
        BigDecimal diff = bankInt.subtract(manFeeIn.add(custFeeIn.add(otherFeeIn).add(couFeeIn)));
        if(diff.compareTo(BigDecimal.ZERO) !=0 ){
            List<BigDecimal> tmpList = new ArrayList<>();
            tmpList.add(manFeeIn);
            tmpList.add(custFeeIn);
            tmpList.add(otherFeeIn);
            tmpList.add(couFeeIn);
            Collections.sort(tmpList);

            BigDecimal bigData = tmpList.get(tmpList.size() - 1);
            if(manFeeIn.equals(bigData)){
                result.setManFeeIn(result.getManFeeIn().add(diff));
            }else if(custFeeIn.equals(bigData)){
                result.setCustFeeIn(result.getCustFeeIn().add(diff));
            }else if(otherFeeIn.equals(bigData)){
                result.setOtherFeeIn(result.getOtherFeeIn().add(diff));
            }else if(couFeeIn.equals(bigData)){
                result.setCouFeeIn(result.getCouFeeIn().add(diff));
            }else{
                //nothing to do
            }

        }
        //-------费用因四舍五入差一分导致check不过进行倒减处理-------<<<<<<<
    }

//    public static void main(String[] args) {
//        BigDecimal A = new BigDecimal("90");
//        BigDecimal B = new BigDecimal("20");
//        BigDecimal C = new BigDecimal("140");
//        BigDecimal D = new BigDecimal("240");
//        BigDecimal E = new BigDecimal("50");
//        List<BigDecimal> tmpList = new ArrayList<>();
//        tmpList.add(A);
//        tmpList.add(B);
//        tmpList.add(C);
//        tmpList.add(D);
//        tmpList.add(E);
//        Collections.sort(tmpList);
//        tmpList.forEach(x-> {
//            logger.info(x.toString());
//        });
//    }


    /**
     * 
     * @param result
     */
    private void initAmtData(ProdTerminationRegist001Dto result) {
		//兑付客户收益
    	result.setCustomProfit(BigDecimal.ZERO);
    	//兑付客户总金额
    	result.setCustomAmt(BigDecimal.ZERO);
    	//兑付总份额
    	result.setCustomQty(BigDecimal.ZERO);
    	//本机构托管费
    	result.setCustFeeIn(BigDecimal.ZERO);
    	//本机构管理费
    	result.setManFeeIn(BigDecimal.ZERO);
    	//本机构销售手续费
    	result.setCouFeeIn(BigDecimal.ZERO);
    	//本机构其他产品费用
    	result.setOtherFeeIn(BigDecimal.ZERO);
    	//其他机构托管费
    	result.setCustFeeOut(BigDecimal.ZERO);
    	//其他机构管理费
    	result.setManFeeOut(BigDecimal.ZERO);
    	//其他机构销售手续费
    	result.setCouFeeOut(BigDecimal.ZERO);
    	//投资顾问费用
    	result.setAdvFeeOut(BigDecimal.ZERO);
    	//其他机构其他产品费用
    	result.setOtherFeeOut(BigDecimal.ZERO);
    	//银行收入
    	result.setProfit(BigDecimal.ZERO);
	}

    /**
     * @Description 判断产品持仓是否为零
     * @param mdate
     * @param prodid
     * @return boolean
     * @version 1.7 2019年7月2日
     */
    private boolean isAssetPosZero(Date mdate,String prodid){
    	BigDecimal pos = BigDecimal.ZERO;
    	pos = prodTerminationRegist001Mapper.isAssetPosZero(mdate,prodid);
    	if(pos.compareTo(BigDecimal.ZERO)==0){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private List<List<ProdForTerminationDto>> createList(List<ProdForTerminationDto> targe,int size) {
        List<List<ProdForTerminationDto>> listArr = new ArrayList<List<ProdForTerminationDto>>();
        //获取被拆分的数组个数
        int arrSize = targe.size()%size==0?targe.size()/size:targe.size()/size+1;
        for(int i=0;i<arrSize;i++) {
            List<ProdForTerminationDto> sub = new ArrayList<ProdForTerminationDto>();
            //把指定索引数据放入到list中
            for(int j = i*size;j <= size*(i+1)-1;j++) {
                if(j <= targe.size()-1) {
                    sub.add(targe.get(j));
                }
            }
            listArr.add(sub);
        }
        return listArr;
    }
    
    public boolean compareData(ProdTerminationRegist001Dto dto,ProdTerminationRegist001Dto regdto){
        ProdTerminationRegist001Dto compare1 = genDto(dto);
        ProdTerminationRegist001Dto compare2 = genDto(regdto);
        if(!JSON.toJSONString(compare1).equals(JSON.toJSONString(compare2))){
            logger.info("info:" + JSON.toJSONString(compare1));
            logger.info("sub:" + JSON.toJSONString(compare2));
            return true;
        }else{
            return false;
        }
    }
    
    private ProdTerminationRegist001Dto genDto(ProdTerminationRegist001Dto regdto) {
    	ProdTerminationRegist001Dto gendto = new ProdTerminationRegist001Dto();
    	gendto.setMdate(regdto.getMdate());
    	gendto.setProfit(regdto.getProfit().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCustomProfit(regdto.getCustomProfit().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCustomAmt(regdto.getCustomAmt().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCustomQty(regdto.getCustomQty().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCusYearYield((regdto.getCusYearYield()==null?BigDecimal.ZERO:regdto.getCusYearYield()).setScale(7, BigDecimal.ROUND_HALF_UP));
    	gendto.setPrdYearYield((regdto.getPrdYearYield()==null?BigDecimal.ZERO:regdto.getPrdYearYield()).setScale(7, BigDecimal.ROUND_HALF_UP));
    	gendto.setCustFeeIn(regdto.getCustFeeIn().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setManFeeIn(regdto.getManFeeIn().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCouFeeIn(regdto.getCouFeeIn().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setOtherFeeIn(regdto.getOtherFeeIn().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setOtherFeeOut(regdto.getOtherFeeOut().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCustFeeOut(regdto.getCustFeeOut().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setCouFeeOut(regdto.getCouFeeOut().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setManFeeOut(regdto.getManFeeOut().setScale(2, BigDecimal.ROUND_HALF_UP));
    	gendto.setAdvFeeOut(regdto.getAdvFeeOut().setScale(2, BigDecimal.ROUND_HALF_UP));
		return gendto;
	}

	/**
     * 功能:数据变更接口统一调度入口
     * @since 1.7.0
     * @version 1.7.0
     * @author：weiyuqiang 2019年5月9日
     * @param executeDate
     * @param prodList
     * @param assetList
     * @throws BusinessException 
     * @see com.joyintech.tams.batch.report.autosendcb.common.service.SvrCBBaseService#execute(java.util.Date, java.util.List, java.util.List)
     */
    @Override
    public void execute(Date executeDate, List<String> prodList, List<String> assetList,Map<String,List<String>> param) throws BusinessException {
        try {
            generateRegistData(executeDate,prodList);
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
            throw new BusinessException(e);
        }
    }

    @Override
    public <T> boolean compareData(T info, T sub) {
        return this.compareData((ProdTerminationRegist001Dto) info, (ProdTerminationRegist001Dto) sub);
    }
}
