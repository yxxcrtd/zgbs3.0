package com.joyintech.tams.invest.batch.creditoccup.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.batch.creditoccup.dao.CreditOccupMapper;
import com.joyintech.tams.invest.batch.creditoccup.dto.CreditOccupResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zznn
 * @date 2021/8/10
 */
@Service
@Scope("prototype")
public class CreditOccupWriter extends BaseWriter<NeedToWriteEntity> {

    @Autowired
    private CreditOccupMapper creditOccupMapper;

    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        List<CreditOccupResult> resultList = (List<CreditOccupResult>) items;
        String credit_penetrate_mode = CacheUtil.getSysParam("credit_penetrate_mode");
        //穿透授信
        if(StringUtil.equals(credit_penetrate_mode,"01")){
            Map<String,Object> param = this.getParams();
            param.put("status", DictCodeListExt1.有效状态_有效.getValue());
            param.put("creditOccureType", DictCodeListRsk.授信占用类别_实占.getValue());
            param.put("isBottom", DictCodeList.是否_是.getValue());
            creditOccupMapper.delCreditUsingSeries(param);
            creditOccupMapper.delCreditUsingAst(param);
            creditOccupMapper.delCreditUsingCustomer( param);
            creditOccupMapper.delCreditUsingCustomerMst(param);
        }
        if (!StringUtil.isEmptyList(resultList)) {
            resultList.forEach(item ->{
                if(!StringUtil.isEmptyList(item.getCreditUsings())){
                    item.setUpdateTime(new Date());
                    item.setUpdateUser("system");
                    item.getCreditUsings().forEach(item2 ->{
                        if(StringUtil.equals(item2.getCreditUsingType(),"01")){
                            creditOccupMapper.updateCreditUsingCustomer(item2);
                        }
                        if(StringUtil.equals(item2.getCreditUsingType(),"02")){
                            creditOccupMapper.updateCreditUsingAst(item2);
                        }
                        if(StringUtil.equals(item2.getCreditUsingType(),"03")){
                            creditOccupMapper.updateCreditUsingSeries(item2);
                        }
                        if(StringUtil.equals(item2.getCreditUsingType(),"04")){
                            creditOccupMapper.updateCreditUsingCustomerMst(item2);
                        }
                    });
                }
            });
        }
    }
}

