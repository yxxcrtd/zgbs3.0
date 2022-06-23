package com.joyintech.tams.valuation.common.batch.tradecommon.service;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.ValExecFeeDiffDto;
import com.joyintech.tams.valuation.common.batch.tradecommon.dao.ValTradeCommonMapper;
import com.joyintech.tams.valuation.common.batch.tradecommon.dto.ValExecApplyInfoDto;
import com.joyintech.tams.valuation.common.batch.tradecommon.dto.ValTrdProductDealInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ValTradeCommonService
 * @Description: 交易信息查询共通
 * @Author yanya
 * @Date 2021/3/1
 * @Version V1.0
 **/
@Service
public class ValTradeCommonService {

    @Autowired
    ValTradeCommonMapper valTradeCommonMapper;

    /**
     * 入账交易信息查询
     * @param paramsMap 参数结合map。包含：
     *                  booksetId账套代码、finprodId金融产品代码、
     *                  finprodType金融产品类型、invAim投资目的、
     *                  busiType业务类型、ps交易方向、bookDate入账日、branch分支序号、
     *                  endBookDate入账日_结束日
     * @return
     */
    public List<ValTrdProductDealInfoDto> findEntryTradeInfo(Map<String, Object> paramsMap) {
        return valTradeCommonMapper.findEntryTradeInfo(paramsMap);
    }

    /**
     * 交易费用明细查询
     * @param tradeId 交易编码
     * @return
     */
    public List<TrdTradeFeeDetail> findTrdTradeFeeDetailInfo(String tradeId) {
        return valTradeCommonMapper.findTrdTradeFeeDetailInfo(tradeId);
    }

    /**
     * 债券行权回售申请查询
     * add by yanya on 20210309
     * @param booksetId 账套代码
     * @param finprodId 金融产品代码
     * @param invAim 投资目的
     * @param queryDate 查询日期
     * @param execResaleapplyEeal 行权方式
     * @return
     */
    public ValExecApplyInfoDto secExecApplyQuery(String booksetId, String finprodId, String invAim, Date queryDate, String execResaleapplyEeal) {
        //定义 变量.行权回售申请交易List
        List<ValTrdProductDealInfoDto> list = null;
        //IF 数据字典：行权回售申请交易处理.重新估值 eq 入参.行权回售申请交易处理
        if(StringUtil.equals(execResaleapplyEeal, DictCodeListExt3.行权回售申请交易处理_重新估值.getValue())) {
            Map<String, Object> 参数 = new HashMap<>();
            //金融产品代码					交易明细.金融产品代码
            参数.put("finprodId", finprodId);
            //金融产品类型					数据字典：业务类型.金融产品类型_债券
            参数.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
            //投资目的					交易明细.投资目的
            参数.put("invAim", invAim);
            //业务类型					数据字典：业务类型.业务类型_债券交易预处理
            参数.put("busiType", DictCodeList.业务类型_债券交易预处理.getValue());
            //交易方向					数据字典：交易方向_行权回售申请
            参数.put("ps", DictCodeListExt3.交易方向_行权回售申请.getValue());
            //查询日	查询日
            参数.put("queryDate", queryDate);
            //行权状态判定 数据字典：行权状态_放弃行权
            参数.put("notExerciseStatus", DictCodeListExt3.行权状态_放弃行权.getValue());
            //现金流一级类型
            参数.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
            list = valTradeCommonMapper.findExecApplyInfoAgain(参数);
        }
        //ELSE IF 数据字典：行权回售申请交易处理.保留原数据 eq 入参.行权回售申请交易处理
        else if(StringUtil.equals(execResaleapplyEeal, DictCodeListExt3.行权回售申请交易处理_保留原数据.getValue())) {
            Map<String, Object> 参数 = new HashMap<>();
            //金融产品代码					交易明细.金融产品代码
            参数.put("finprodId", finprodId);
            //金融产品类型					数据字典：业务类型.金融产品类型_债券
            参数.put("finprodType", DictCodeListExt1.金融产品类型_债券.getValue());
            //投资目的					交易明细.投资目的
            参数.put("invAim", invAim);
            //业务类型					数据字典：业务类型.业务类型_债券交易预处理
            参数.put("busiType", DictCodeList.业务类型_债券交易预处理.getValue());
            //交易方向					数据字典：交易方向_行权回售申请
            参数.put("ps", DictCodeListExt3.交易方向_行权回售申请.getValue());
            //查询日	查询日
            参数.put("queryDate", queryDate);
            //现金流一级类型
            参数.put("cashType1", DictCodeListExt1.现金流一级类型_本金.getValue());
            list = valTradeCommonMapper.findExecApplyInfo(参数);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            ValExecApplyInfoDto dto = new ValExecApplyInfoDto();
            //变量.行权申请信息.行权价格 = 变量.行权回售申请交易List.get(0).单位净价
            dto.setExecPrice(list.get(0).getUnitCprice());
            //变量.行权申请信息.行权份额 = 变量.行权回售申请交易List.get(0).交易份额
            dto.setExecNum(list.get(0).getShareAmt());
            return dto;
        }
        return null;
    }

}
