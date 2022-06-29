package com.joyintech.tams.invest.batch.creditoccup.service;

import com.google.common.collect.Lists;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.batch.creditoccup.dao.CreditOccupMapper;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditOccupReaderResult;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditUsingDto;
import com.joyintech.tams.invest.common.trade.dto.CreditLinesInputDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zznn
 * @date 2021/8/10
 */
@Service
@Scope("prototype")
public class CreditOccupReaderService extends BaseService {

    @Autowired
    private CreditOccupMapper creditOccupMapper;


    /**
     * 查询释放授信的数据
     * @param param
     * @return
     */
    public List<CreditOccupReaderResult> execute(Map<String, Object> param) {
        List<CreditOccupReaderResult> resultList = new ArrayList<CreditOccupReaderResult>();
        CreditOccupReaderResult creditOccupReaderResult = new CreditOccupReaderResult();
        List<CreditUsingDto> creditUsings = new ArrayList<>();
        String credit_penetrate_mode = CacheUtil.getSysParam("credit_penetrate_mode");
        param.put("credit_penetrate_mode",credit_penetrate_mode);
        param.put("status", DictCodeListExt1.有效状态_有效.getValue());
        param.put("executeDate",param.get("executeDate"));
        param.put("creditOccureType", DictCodeListRsk.授信占用类别_实占.getValue());
        //场内
        param.put("operatioMode","02");
        creditUsings.addAll(creditOccupMapper.getCreditUsingSeries(param));
        creditUsings.addAll(creditOccupMapper.getCreditUsingAst(param));
        creditUsings.addAll(creditOccupMapper.getCreditUsingCustomerMst(param));
        creditUsings.addAll(creditOccupMapper.getCreditUsingCustomer(param));
        creditOccupReaderResult.setCreditUsings(creditUsings);
        resultList.add(creditOccupReaderResult);
        return resultList;
    }

    /**
     * 查询需要预实占的数据(场内交易及委外穿透)
     * @param param
     * @return
     */
    public List<CreditOccupReaderResult> execute2(Map<String, Object> param) {
        List<CreditOccupReaderResult> resultList = new ArrayList<CreditOccupReaderResult>();
        CreditOccupReaderResult creditOccupReaderResult = new CreditOccupReaderResult();
        List<CreditLinesInputDto> creditLinesInputs = new ArrayList<>();
        String credit_penetrate_mode = CacheUtil.getSysParam("credit_penetrate_mode");
        param.put("status", DictCodeListExt1.有效状态_有效.getValue());
        param.put("executeDate",param.get("executeDate"));
        param.put("ps", DictCodeList.交易方向_买入.getValue());
        param.put("YES_OR_NO_Y",DictCodeList.是否_是.getValue());
        param.put("YES_OR_NO_N",DictCodeList.是否_否.getValue());
        param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        param.put("PORTFOLIO_TYPE_02", DictCodeListExt1.组合类型_委外组合.getValue());
        //场内交易
        List<CreditLinesInputDto> creditLinesInputDtos = creditOccupMapper.listTrdProductDeal(param);
        if(CollectionUtils.isNotEmpty(creditLinesInputDtos)) {
            for (int i = 0; i < creditLinesInputDtos.size(); i++) {
                if ("T49".equals(creditLinesInputDtos.get(i).getPs())) {
                    CreditLinesInputDto creditLinesInputDto = creditLinesInputDtos.get(i);
                    creditLinesInputDto.setPs(DictCodeList.交易方向_卖出.getValue());
                    creditLinesInputDtos.add(creditLinesInputDto);

                    CreditLinesInputDto creditLinesInputDto1 = new CreditLinesInputDto();
                    BeanUtil.copyPropertiesIgnoreNull(creditLinesInputDto1, creditLinesInputDto);
                    creditLinesInputDto1.setPs(DictCodeList.交易方向_买入.getValue());
                    creditLinesInputDto1.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_股票.getValue());
                    creditLinesInputDto1.setFinprodId(creditLinesInputDto1.getZhaiZhuanGuId());
                    creditLinesInputDto1.setShareAmt(creditLinesInputDto1.getZhaiZhuanGuNum());
                    creditLinesInputDto1.setNetPrice(creditLinesInputDto1.getZhaiZhuanGuDanJia());
                    creditLinesInputDto1.setTradeAmt(creditLinesInputDto1.getZhaiZhuanGuDanJia().multiply(creditLinesInputDto1.getZhaiZhuanGuNum()));
                    creditLinesInputDtos.add(creditLinesInputDto1);
                    creditLinesInputDtos.remove(i--);
                }
            }
        }
        //委外
        if(StringUtil.equals(credit_penetrate_mode,"01")){
            List<CreditLinesInputDto> creditLinesInputDtos1 = creditOccupMapper.listWwSec(param);
            creditLinesInputs.addAll(creditLinesInputDtos1);
        }
        creditLinesInputs.addAll(creditLinesInputDtos);
        List<CreditLinesInputDto> creditLinesInputsObj = new ArrayList<>();
        for(int i=0;i<creditLinesInputs.size();i++){
            if("T01".equals(creditLinesInputs.get(i).getPs())||"T04".equals(creditLinesInputs.get(i).getPs())||"T52".equals(creditLinesInputs.get(i).getPs())){
                creditLinesInputsObj.add(creditLinesInputs.get(i));
                creditLinesInputs.remove(i--);
            }
        }
        creditLinesInputsObj.addAll(creditLinesInputs);
        creditOccupReaderResult.setCreditLinesInputs(creditLinesInputsObj);
        resultList.add(creditOccupReaderResult);
        return resultList;
    }


    /**
     * 处理一级市场债的授信释放
     *
     * @param param
     * @return
     */
    public List<CreditOccupReaderResult> execute3(Map<String, Object> param) {
        CreditOccupReaderResult result = new CreditOccupReaderResult();
        result.setCreditLinesInputs(creditOccupMapper.listTotalTempDeal(param));
        return Lists.newArrayList(result);
    }
}

