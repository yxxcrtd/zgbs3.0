package com.joyintech.tams.invest.batch.overduesecpositionforward.service;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.EtrSecPositionFlowMapper;
import com.joyintech.tams.common.base.dao.PtlRecePositionFlowMapper;
import com.joyintech.tams.common.base.dao.PtlSecPositionFlowMapper;
import com.joyintech.tams.common.base.entity.EtrSecPositionFlow;
import com.joyintech.tams.common.base.entity.PtlRecePositionFlow;
import com.joyintech.tams.common.base.entity.PtlSecPositionFlow;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.overduesecpositionforward.dao.OverdueSecPositionForwardMapper;
import com.joyintech.tams.invest.batch.overduesecpositionforward.dto.OverdueSecPositionForwardDto;
import com.joyintech.tams.invest.batch.overduesecpositionforward.dto.OverdueSecPositionForwardResultDto;
import com.joyintech.tams.invest.batch.overduesecpositionforward.dto.SecManageAcctInfoDto;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OverdueSecPositionForwardService {
    /**
     * 应收款流水处理Mapper
     */
    @Autowired
    private PtlRecePositionFlowMapper ptlRecePositionFlowMapper;
    /**
     * 持仓流水处理Mapper
     */
    @Autowired
    private PtlSecPositionFlowMapper ptlSecPositionFlowMapper;
    /**
     * 逾期资产结转DAO
     */
    @Autowired
    private OverdueSecPositionForwardMapper overdueSecPositionForwardMapper;

    /**
     *  委托指令流水处理Mapper
     */
    @Autowired
    private EtrSecPositionFlowMapper etrSecPositionFlowMapper;

    @Autowired(required = false)
    private BaseGetBussinessId baseGetBussinessId;
    /**
     * 查询需要处理的数据
     * @param params 参数
     * @return 查询到的需要处理的结果
     */
    public List<OverdueSecPositionForwardDto> findPendingData(Map<String, Object> params) {
//        平台日
        Date platDate = (Date)params.get(BatchConstant.PLATE_DATE);
//        平台日前一天
        Date prePlatDate = DateUtil.addDay(platDate,-1);
        params.put("PRE_PLATE_DATE",prePlatDate);
        params.put("PLATE_DATE",platDate);
        params.put("POSITION_TYPE_SJ", DictCodeList.持仓类型_实际.getValue());
        params.put("BRANCH_TYPE_YSBJ",DictCodeList.分支类型_应收本金.getValue());
        params.put("CASH_TYPE_2_BJ",DictCodeListExt1.现金流二级类型_本金.getValue());
        params.put("CASH_TYPE_2_LX",DictCodeListExt1.现金流二级类型_利息.getValue());
//        债券，存款，计划类资产(利率型)，项目类资产
        params.put("PINAN_ASSET_TYPE_ZQ",DictCodeList.平安资产类型_债券.getValue());
        params.put("PINAN_ASSET_TYPE_CK",DictCodeList.平安资产类型_存款.getValue());
        params.put("PINAN_ASSET_TYPE_JHLLLX",DictCodeList.平安资产类型_计划类资产_利率型.getValue());
        params.put("PINAN_ASSET_TYPE_XML",DictCodeList.平安资产类型_项目类资产.getValue());
        params.put("TRADE_STATUS_CS",DictCodeListExt1.交易状态_初始.getValue());
        params.put("YON_N",DictCodeListExt1.是否_否.getValue());
        params.put("YON_Y",DictCodeListExt1.是否_是.getValue());
        params.put("BRANCH",0);
//      还本到账，收息到账
	    params.put("PS_HBDZ",DictCodeList.交易方向_还本到账.getValue());
	    params.put("PS_SXDZ",DictCodeList.交易方向_收息到账.getValue());
	    return overdueSecPositionForwardMapper.findPendingData(params);
  }

    /**
     * 生成流水的seqno
     * @return
     */
    private String genSeqno(){
        return IDGenerator.generate();
    }

    /**
     * 根据组装信息处理流水dto
     * @param 交易编号 交易编号
     * @param 待结转金额 待结转金额
     * @param input 输入dto
     * @param secInfo 信息
     * @param params 批处理参数
     * @return
     */
    private  PtlSecPositionFlow genSecPositionFlowDto(String 交易编号, BigDecimal 待结转金额, OverdueSecPositionForwardDto input, SecManageAcctInfoDto secInfo, Map<String, Object> params){
//        流水号							<-		自动生成
//        组合代码							<-		入参.查询结果DTO.组合代码
//        证券管理账户/通道代码							<-		入参.查询结果DTO.证券管理账户/通道代码
//        金融产品代码							<-		入参.查询结果DTO.金融产品代码
//        分支序号							<-		0
//        投资目的							<-		入参.查询结果DTO.投资目的
//        持仓类型							<-		数据字典.持仓类型.实际
//        日期							<-		平台日
//        币种							<-		入参.查询结果DTO.币种
//        数量							<-		变量.结转金额 * -1
//        母金融产品代码							<-		空
//        交易编号							<-		变量.交易编号
        PtlSecPositionFlow flowDto = new PtlSecPositionFlow();
        flowDto.setSeqno(genSeqno());
        flowDto.setPortfolioId(input.getPortfolioId());
        flowDto.setSecAcctId(secInfo.getSecAcctId());
        flowDto.setFinprodId(input.getFinprodId());
        flowDto.setBranch(0L);
        flowDto.setInvAim(input.getInvAim());
        flowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        flowDto.setCdate((Date)params.get(BatchConstant.PLATE_DATE));
        flowDto.setCcy(input.getCcy());
        flowDto.setAmount(待结转金额.negate());
        flowDto.setTradeId(交易编号);
        String TASK_ID = "11003";
        if(params.containsKey("TASK_ID")){
            TASK_ID = (String)params.get("TASK_ID");

        }
        flowDto.setCreateUser(TASK_ID);
        flowDto.setUpdateUser(TASK_ID);
        return flowDto;
    }

    /**
     *
     * @param 交易编号 交易编号
     * @param 应收款类型 应收款类型
     * @param 计划支付日 计划支付日
     * @param 到期日 到期日
     * @param 结转金额 结转金额
     * @param input input
     * @param secInfo 额外信息
     * @param params 批处理参数
     * @return 应收款流水
     */
    private  PtlRecePositionFlow genRecePositionFlowDto(String 交易编号, String 应收款类型, Date 计划支付日, Date 到期日, BigDecimal 结转金额, OverdueSecPositionForwardDto input,
                                                        SecManageAcctInfoDto secInfo, Map<String, Object> params){
//        流水号							<-		自动生成
//        组合代码							<-		入参.查询结果DTO.组合代码
//        证券管理账户/通道代码							<-		入参.查询结果DTO.证券管理账户/通道代码
//        金融产品代码							<-		入参.查询结果DTO.金融产品代码
//        分支序号							<-		入参.查询结果DTO.分支序号
//        投资目的							<-		入参.查询结果DTO.投资目的
//        持仓类型							<-		数据字典.持仓类型.实际
//        发生日期							<-		平台日
//        应收款类型							<-		数据字典.应收款类型.变量.应收款类型
//        计划支付日期							<-		变量.计划支付日
//        币种							<-		入参.查询结果DTO.币种
//        数量							<-		变量.结转金额
//        母金融产品代码							<-		空
//        交易编号							<-		变量.交易编号
        PtlRecePositionFlow flowDto = new PtlRecePositionFlow();
        flowDto.setSeqno(genSeqno());
        flowDto.setPortfolioId(input.getPortfolioId());
        flowDto.setSecAcctId(secInfo.getSecAcctId());
        flowDto.setFinprodId(input.getFinprodId());
        flowDto.setBranch(secInfo.getBranch());
        flowDto.setInvAim(input.getInvAim());
        flowDto.setHodingType(DictCodeList.持仓类型_实际.getValue());
        flowDto.setPayDate(计划支付日);
        flowDto.setCdate((Date)params.get(BatchConstant.PLATE_DATE));
        flowDto.setCcy(input.getCcy());
        flowDto.setAmount(结转金额);
        flowDto.setRecType(应收款类型);
        flowDto.setTradeId(交易编号);
        flowDto.setInputType(DictCodeListExt1.录入方式_批处理生成.getValue());
        String TASK_ID = "11003";
        if(params.containsKey("TASK_ID")){
            TASK_ID = (String)params.get("TASK_ID");
        }
        flowDto.setCreateUser(TASK_ID);
        flowDto.setUpdateUser(TASK_ID);
        return flowDto;
    }

    public EtrSecPositionFlow genEtrSecPositionFlow(String 交易编号, BigDecimal 待结转金额, OverdueSecPositionForwardDto input, SecManageAcctInfoDto secInfo, Map<String, Object> params){
//        流水号							<-		自动生成
//组合代码							<-		入参.查询结果DTO.组合代码
//证券管理账户/通道代码							<-		入参.查询结果DTO.证券管理账户/通道代码
//金融产品代码							<-		入参.查询结果DTO.金融产品代码
//分支序号							<-		0
//投资目的							<-		入参.查询结果DTO.投资目的
//持仓类型							<-		数据字典.持仓类型.委托指令可用
//日期							<-		平台日
//币种							<-		入参.查询结果DTO.币种
//数量							<-		变量.结转金额 * -1
//母金融产品代码							<-		空
//交易编号							<-		变量.交易编号
//创建人							<-		批处理taskId
//更新人							<-		批处理taskId
        EtrSecPositionFlow flowDto = new EtrSecPositionFlow();
        flowDto.setSeqno(genSeqno());
        flowDto.setPortfolioId(input.getPortfolioId());
        flowDto.setSecAcctId(secInfo.getSecAcctId());
        flowDto.setFinprodId(input.getFinprodId());
        flowDto.setBranch(0L);
        flowDto.setInvAim(input.getInvAim());
        flowDto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
        flowDto.setCdate((Date)params.get(BatchConstant.PLATE_DATE));
        flowDto.setCcy(input.getCcy());
        flowDto.setAmount(待结转金额.negate());
        flowDto.setTradeId(交易编号);
        String TASK_ID = "11003";
        if(params.containsKey("TASK_ID")){
            TASK_ID = (String)params.get("TASK_ID");

        }
        flowDto.setCreateUser(TASK_ID);
        flowDto.setUpdateUser(TASK_ID);
        return flowDto;
    }
    /**
     * 将读取到的信息转换成入库结果
     * @param item 读取到的信息
     * @param params 批处理参数
     * @return 入库结果
     */
    public OverdueSecPositionForwardResultDto convertResultFromRead(OverdueSecPositionForwardDto item, Map<String, Object> params) {
        OverdueSecPositionForwardResultDto resultDto = new OverdueSecPositionForwardResultDto();
//        组合持仓流水容器
        List<PtlSecPositionFlow> ptlSecPositionFlowList = new ArrayList<>();
//        应收款流水容器
        List<PtlRecePositionFlow>ptlRecePositionFlowList = new ArrayList<>();
//        委托指令流水容器
        List<EtrSecPositionFlow>etrSecPositionFlowList = new ArrayList<>();
        BigDecimal 待结转金额 = BigDecimal.ZERO;
        BigDecimal 累计扣除应收款 = BigDecimal.ZERO;
//        防止空
        List<SecManageAcctInfoDto> secManageList = item==null?null:item.getSecManageAcctInfoDtoList();
        if(CollectionUtils.isNotEmpty(secManageList)){
            for(SecManageAcctInfoDto secInfo :secManageList){
                          /*
                * if（循环的最后一条数据）
                        变量.结转金额 = 变量.证券账户持仓.应收款 - 变量.累计扣除应收款
                    else
                        变量.结转金额 = 变量.证券账户持仓.应收款*变量.证券账户持仓.数量/变量.证券账户持仓.持仓总数量 保留两位小数
                        变量.累计扣除应收款 += 变量.结转金额
                    end
                * */
//                最后一条
                if(secManageList.indexOf(secInfo) == secManageList.size()-1){
                    待结转金额 = ArithmeticUtil.subtract(secInfo.getAmount(),累计扣除应收款);
                }
                else{
                    待结转金额 = ArithmeticUtil.divide(ArithmeticUtil.multiply(secInfo.getQty(),secInfo.getAmount()),secInfo.getHoldAmtTotal(),2);
                    累计扣除应收款 = ArithmeticUtil.doAdd(待结转金额,累计扣除应收款);
                }
//                BATCH+ 平台日日期+4位流水号
                String 交易编号 = gen交易编号();
                if(StringUtil.equals(item.getBranchType(),DictCodeList.分支类型_应收本金.getValue())&&
//                        增加条件 OR 入参.查询结果DTO.到期日 = 入参.查询结果DTO.实际到期日 2019-12-31 设计变更
                        (!StringUtil.equals(item.getType8(),DictCodeList.平安资产类型_债券.getValue())|| DateUtil.compareDate(item.getMdate(),item.getAssetMdate())==0)){
                    ptlSecPositionFlowList.add(genSecPositionFlowDto(交易编号,待结转金额,item,secInfo,params));
                    etrSecPositionFlowList.add(genEtrSecPositionFlow(交易编号,待结转金额,item,secInfo,params));
                }
                String 应收款类型 = null;
                Date 计划支付日 = null;
                Date 到期日 = null;
                if(StringUtil.isEmpty(item.getCashType())){
                    if(StringUtil.equals(item.getIsPayIntMid(),DictCodeList.是否_是.getValue())){
//                        变量.应收款类型 = 数据字典.应收款类型.逾期未收利息
//                        变量.到期日 = 入参.查询结果DTO.中间还本计算结束日
//                        变量.计划支付日 = 入参.查询结果DTO.中间还本计划支付日
                        应收款类型 = DictCodeListExt3.应收款类型_逾期未收利息.getValue();
                        到期日 = item.getCalMDateMid();
                        计划支付日 = item.getPayDateMid();
                    }
                    else{
                        应收款类型 = DictCodeListExt3.应收款类型_还本未收利息.getValue();
                        Map<String,Object> param = new HashMap<>();
                        param.put("finprodId",item.getFinprodId());
                        param.put("calMDateMid",item.getCalMDateMid());
                        param.put("CASH_CLASS_TYPE_ZJSX",DictCodeList.现金流类型_中间收息.getValue());
                        param.put("CASH_CLASS_TYPE_DQSX",DictCodeList.现金流类型_到期收息.getValue());
                        param.put("BRANCH",0);
                        Map<String,Object> dateMap = overdueSecPositionForwardMapper.findAdjustDate(param);
//                      变量.到期日 = 入参.查询结果DTO.调整后的计算结束日
//                      变量.计划支付日 = 上述查询.调整后的计算结束日
                        到期日 = item.getMdate();
                        if(dateMap != null){
                            计划支付日 = (Date)dateMap.get("PAY_DATE");
                        }
                    }
                }
                else{
//                    if (入参.查询结果DTO.分支类型 = 数据字典.分支类型.本金)
//                        变量.应收款类型 = 数据字典.应收款类型.逾期未收本金
//                    else
//                        变量.应收款类型 = 数据字典.应收款类型.逾期未收利息
//                    end
//                    变量.计划支付日 = 入参.查询结果DTO.计划支付日
//                    变量.到期日 = 入参.查询结果DTO.到期日
                    if(StringUtil.equals(item.getBranchType(),DictCodeList.分支类型_应收本金.getValue())){
                        应收款类型 = DictCodeListExt3.应收款类型_逾期未收本金.getValue();
                    }
                    else{
                        应收款类型 = DictCodeListExt3.应收款类型_逾期未收利息.getValue();
                    }
                    计划支付日 = item.getPayDate();
                    到期日 = item.getMdate();
                }
                if(计划支付日 != null){
                    ptlRecePositionFlowList.add(genRecePositionFlowDto(交易编号,应收款类型,计划支付日,到期日,待结转金额,item,secInfo,params));
                }

            }
        }
//        处理结果放入result容器
        if(CollectionUtils.isNotEmpty(ptlSecPositionFlowList)){
            resultDto.setPtlSecPositionFlowList(ptlSecPositionFlowList);
        }
        if(CollectionUtils.isNotEmpty(ptlRecePositionFlowList)){
            resultDto.setPtlRecePositionFlowList(ptlRecePositionFlowList);
        }
        if(CollectionUtils.isNotEmpty(etrSecPositionFlowList)){
            resultDto.setEtrSecPositionFlowList(etrSecPositionFlowList);
        }
        return resultDto;
    }

    /**
     * 生成交易编号
     * @return 生成结果
     */
    private String gen交易编号() {
        if(baseGetBussinessId!=null){
            BussinessCodeParameter codeParameter = new BussinessCodeParameter();
            codeParameter.setSeqNo("9993");
            return baseGetBussinessId.buildBussinessCode(codeParameter);
        }
        else{
            return  IDGenerator.generate();
        }
    }

    /**
     * 持久化处理结果dto
     * @param items 处理结果
     */
    public void holdResultDto(List<? extends OverdueSecPositionForwardResultDto> items) {
        if(CollectionUtils.isNotEmpty(items)){
            items.forEach(temp->{
                if(temp != null){
                    if(CollectionUtils.isNotEmpty(temp.getPtlRecePositionFlowList())){
                        temp.getPtlRecePositionFlowList().forEach(flowDto->{
                            if(StringUtil.isNotEmpty(flowDto.getPortfolioId())){
                                ptlRecePositionFlowMapper.insertSelective(flowDto);
                            }
                        });
                    }
                    if(CollectionUtils.isNotEmpty(temp.getPtlSecPositionFlowList())){
                        temp.getPtlSecPositionFlowList().forEach(flowDto->{
                            if(StringUtil.isNotEmpty(flowDto.getPortfolioId())){
                                ptlSecPositionFlowMapper.insertSelective(flowDto);
                            }
                        });
                    }
                    if(CollectionUtils.isNotEmpty(temp.getEtrSecPositionFlowList())){
                        temp.getEtrSecPositionFlowList().forEach(flowDto->{
                            if(StringUtil.isNotEmpty(flowDto.getPortfolioId())) {
                                etrSecPositionFlowMapper.insertSelective(flowDto);
                            }
                        });
                    }
                }
            });
        }
    }
}
