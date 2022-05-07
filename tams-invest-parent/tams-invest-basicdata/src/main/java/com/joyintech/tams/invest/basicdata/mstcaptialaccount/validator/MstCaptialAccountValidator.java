/*
 * 文件名：MstCaptialAccountValidator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.mstcaptialaccount.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.basicdata.mstcaptialaccount.dto.MstCaptialAccountDto;
import com.joyintech.tams.invest.basicdata.mstcaptialaccount.service.MstCaptialAccountService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
/**
 * 
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author huwenbin
 * @version 1.0 2018年8月20日
 * @see MstCaptialAccountValidator
 * @since 1.0
 */
@Component
public class MstCaptialAccountValidator implements BeanValidator{
    /**
     * 资金账户管理service
     */
    @Autowired
    private MstCaptialAccountService mstCaptialAccountService;

    /**
     * 工作流CommonServic
     */
    @Autowired
    private MstWorkflowCommonService mstWorkflowCommonService;
    
    @Override
    public List<ValidMessage> valid(Object value) {
        MstCaptialAccountDto dto = (MstCaptialAccountDto)value;
        List<ValidMessage> errorList = new ArrayList<>();
        if (WorkFlowUtil.isNotFromToDoList()) {
            if (StringUtil.equals(dto.getOperateType(), "delete")) {
                //行删除按钮与删除按钮校验
                List<MstCaptialAccountDto> listDto = dto.getSelectedItem();
                List<MstCaptialAccountDto> tempList = mstCaptialAccountService.checkAllCodeIsUse(listDto);
                if(tempList.size()>0){
                    for(MstCaptialAccountDto tempDto:tempList){
                        errorList.add(new ValidMessage("", getMessage("COMMON_E_0030",
                            getMessage("MstCaptialAccount002.acctNo")
                            +"("+getMessage("MstCaptialAccount002.currency")+")"
                            ,tempDto.getAcctNo()+"("+tempDto.getCcy()+")")));
                
                    }
                }   
            }
            else {
                //新建校验
                if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                    if(!StringUtil.equals(dto.getCcy(), "CNY")){
                        if(StringUtil.isEmpty(dto.getSwiftCode())){
                            errorList.add(new ValidMessage("swiftCode", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.swiftCode"))));
                        }
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("acctName", "");
                    List<MstCaptialAccountDto> list = mstCaptialAccountService.select(map);
                    for(int i=0; i<list.size(); i++){
                    	String temp = "";
                    	if(StringUtil.isNotEmpty(list.get(i).getLargePayno())){
                    		temp = list.get(i).getLargePayno()+list.get(i).getBusiAcctNo()+list.get(i).getCcy();
                    	}
                    	else{
                            temp = ""+list.get(i).getBusiAcctNo()+list.get(i).getCcy();
                    	}
                    	if(StringUtil.equals(temp, dto.getLargePayno()+dto.getBusiAcctNo()+dto.getCcy())){
                            errorList.add(new ValidMessage("",getMessage("BUSS_COM_E_0064",
                                getMessage("MstCaptialAccount002.accountOutlet")+"+"+getMessage("MstCaptialAccount002.accountId")
                                +"+"+getMessage("MstCaptialAccount002.currency"))));
                        }
                    }  
                    String largePayno = dto.getLargePayno();
                    if(StringUtil.isNotEmpty(largePayno)){
                    	if(largePayno.length() > 12){
                            errorList.add(new ValidMessage("largePayno", getMessage("MST_CAP_COUNT_1")));
                        }
                    } else if(StringUtil.isNotEmpty(dto.getAccountNature())
                        && !StringUtil.equals(dto.getAccountNature(), DictCodeListExt3.账户性质_非银行账户.getValue())
                        && StringUtil.equals(dto.getCcy(), "CNY")) {
                        //当“账户性质”为非银行账户时，“开户网点/大额支付行号”非必填,当“币种”不是人民币时，“开户网点/大额支付行号”非必填。
                        errorList.add(new ValidMessage("largePayno", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.accountOutlet"))));
                    }

                    // 校验利息
                    this.checkIsCalint(dto, errorList);
                }
                else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
                    if(!StringUtil.equals(dto.getCcy(), "CNY")){
                        if(StringUtil.isEmpty(dto.getSwiftCode())){
                            errorList.add(new ValidMessage("swiftCode", getMessage("COMMON_E_0057",
                                getMessage("MstCaptialAccount002.swiftCode"))));
                        }
                    }
                    // TISHKS-1189:增加利息校验 modify by fuyongsheng 20220507 start
                    // 校验利息
                    this.checkIsCalint(dto, errorList);
                    // TISHKS-1189:增加利息校验 modify by fuyongsheng 20220507 end
                }
                
               if(StringUtil.isNotEmpty(dto.getAccountType())
                   // ZGZGST-1860:修改判断是否有券商资金账户属性方法 fuyongsheng 20210902 edit start
                   && StringUtil.indexOf(dto.getAccountType(), DictCodeListExt1.资金账户类型_券商资金账户.getValue())>-1) {
                   // ZGZGST-1860:修改判断是否有券商资金账户属性方法 fuyongsheng 20210902 edit end
                   if(!StringUtil.isNotEmpty(dto.getOwnDept())) {
                       errorList.add(new ValidMessage("ownDept", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.ownDept")))); 
                   }
                   
                   if(!StringUtil.isNotEmpty(dto.getAccPass())) {
                       errorList.add(new ValidMessage("accPass", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.accPass")))); 
                   } 
                   
                   if(!StringUtil.isNotEmpty(dto.getTradePass())) {
                       errorList.add(new ValidMessage("tradePass", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.tradePass"))));
                   } 
               }
            }
        }
        else{
          //新建校验
            String dooubleRecord =  CacheUtil.getSysParam("DOUBLE_RECORD");
            if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
                if(!StringUtil.equals(dto.getCcy(), "CNY")){
                    if(StringUtil.isEmpty(dto.getSwiftCode())){
                        errorList.add(new ValidMessage("swiftCode", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.swiftCode"))));
                    }
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("acctName", "");
                List<MstCaptialAccountDto> list = mstCaptialAccountService.select(map);
                for(int i=0; i<list.size(); i++){
                	String temp = "";
                	if(StringUtil.isNotEmpty(list.get(i).getLargePayno())){
                		temp = list.get(i).getLargePayno()+list.get(i).getBusiAcctNo()+list.get(i).getCcy();
                	}
                	else{
                        temp = ""+list.get(i).getBusiAcctNo()+list.get(i).getCcy();
                	}
                	if(StringUtil.equals(temp, dto.getLargePayno()+dto.getBusiAcctNo()+dto.getCcy())){
                        errorList.add(new ValidMessage("",getMessage("BUSS_COM_E_0006",
                            getMessage("MstCaptialAccount002.accountOutlet")+"+"+getMessage("MstCaptialAccount002.accountId")
                            +"+"+getMessage("MstCaptialAccount002.currency"))));
                    }
                } 
                
                if(StringUtil.isNotEmpty(dto.getAccountType()) 
                    && StringUtil.indexOf(dto.getAccountType(), DictCodeListExt1.资金账户类型_券商资金账户.getValue())>0) {
                    if(!StringUtil.isNotEmpty(dto.getOwnDept())) {
                        errorList.add(new ValidMessage("ownDept", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.ownDept")))); 
                    }
                    
                    if(!StringUtil.isNotEmpty(dto.getAccPass())) {
                        errorList.add(new ValidMessage("accPass", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.accPass")))); 
                    } 
                    
                    if(!StringUtil.isNotEmpty(dto.getTradePass())) {
                        errorList.add(new ValidMessage("tradePass", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.tradePass"))));
                    }                   
                }
                
            }
            else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
                if(!StringUtil.equals(dto.getCcy(), "CNY")){
                    if(StringUtil.isEmpty(dto.getSwiftCode())){
                        errorList.add(new ValidMessage("swiftCode", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.swiftCode"))));
                    }
                }
                
                if(StringUtil.isNotEmpty(dto.getAccountType()) 
                    && StringUtil.indexOf(dto.getAccountType(), DictCodeListExt1.资金账户类型_券商资金账户.getValue())>0) {
                    if(!StringUtil.isNotEmpty(dto.getOwnDept())) {
                        errorList.add(new ValidMessage("ownDept", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.ownDept")))); 
                    }
                    
                    if(!StringUtil.isNotEmpty(dto.getAccPass())) {
                        errorList.add(new ValidMessage("accPass", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.accPass")))); 
                    } 
                    
                    if(!StringUtil.isNotEmpty(dto.getTradePass())) {
                        errorList.add(new ValidMessage("tradePass", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.tradePass"))));
                    }                   
                }

            } else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_REFERENCE)){
                if(StringUtil.equals(dto.getWflOperType(), "NEW")) {
                    if(StringUtil.equals(dooubleRecord, "01") &&
                        StringUtil.equals(dto.getIsFirstApprovalNode(), DictCodeList.是否_是.getValue())) {
                        //双录校验开户网点
                        if(StringUtil.isNotEmpty(dto.getLargePayno()) && !StringUtil.equals(dto.getLargePayno(), dto.getLargePaynoContrast())) {
                            errorList.add(new ValidMessage("largePayno", getMessage("MST_CAP_COUNT_2",getMessage("FundAccountManagement002.accountOutlet"))));
                        }
                        else if(!StringUtil.isNotEmpty(dto.getLargePayno()) && StringUtil.isNotEmpty(dto.getLargePaynoContrast())) {
                            //当“账户性质”为非银行账户时，“开户网点/大额支付行号”非必填,当“币种”不是人民币时，“开户网点/大额支付行号”非必填。
                            errorList.add(new ValidMessage("largePayno", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.accountOutlet"))));
                        }
                        
                        //双录校验币种
                        if(StringUtil.isNotEmpty(dto.getCcy()) && !StringUtil.equals(dto.getCcy(), dto.getCcyContrast())) {
                            errorList.add(new ValidMessage("ccy", getMessage("MST_CAP_COUNT_2",getMessage("FundAccountManagement002.currency"))));
                        }
                        
                        //双录校验开户行
                        if(StringUtil.isNotEmpty(dto.getBankDep()) && !StringUtil.equals(dto.getBankDep(), dto.getBankDepContrast())) {
                            errorList.add(new ValidMessage("bankDep", getMessage("MST_CAP_COUNT_2",getMessage("FundAccountManagement002.bankAccount"))));
                        }
                    }
                    
                } else if(StringUtil.equals(dto.getWflOperType(), "MODIFY")) {
                    
                    if(StringUtil.equals(dooubleRecord, "01") &&
                        StringUtil.equals(dto.getIsFirstApprovalNode(), DictCodeList.是否_是.getValue())) {
                        //双录校验开户行
                        if(StringUtil.isNotEmpty(dto.getBankDep()) && !StringUtil.equals(dto.getBankDep(), dto.getBankDepContrast())) {
                            errorList.add(new ValidMessage("bankDep", getMessage("MST_CAP_COUNT_2",getMessage("FundAccountManagement002.bankAccount"))));
                        }  
                        
                    }
                    
                } 
            }
        }
		
        /*add by lpf 2021-04-27 增加字段 账户性质*/
        if(!StringUtil.equals(dto.getOperateType(),"delete")&&!StringUtil.equals(dto.getOperateType(),"edit")){
            if(StringUtil.isEmpty(dto.getAccountNature())){
                errorList.add(new ValidMessage("accountNature", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.accountNature"))));
            }
        }
		
        return errorList;
    }

    /**
     * 校验利息
     * @param dto
     * @param errorList
     */
    private void checkIsCalint(MstCaptialAccountDto dto, List<ValidMessage> errorList){
        if(StringUtil.isNotEmpty(dto.getIsCalint())) {
            if(StringUtil.equals(dto.getIsCalint(), DictCodeList.是否_是.getValue())){
                if(!StringUtil.isNotEmpty(dto.getEffectDate())) {
                    //生效日不能为空
                    errorList.add(new ValidMessage("effectDate", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.effectDate"))));
                }

                if(!StringUtil.isNotEmpty(dto.getBasis())) {
                    //计息基础不能为空
                    errorList.add(new ValidMessage("basis", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.basis"))));
                }

                if(StringUtil.isNotEmpty(dto.getRateType())) {
                    if(StringUtil.equals(dto.getRateType(), DictCodeList.利率类型_固定利率.getValue())) {
                        //利率不能为空
                        if(!StringUtil.isNotEmpty(dto.getRate())) {
                            errorList.add(new ValidMessage("rate", getMessage("COMMON_E_0057",getMessage("MstCaptialAccount002.rate"))));
                        }
                    } else if(StringUtil.equals(dto.getRateType(), DictCodeList.利率类型_浮动利率.getValue())) {
                        //浮动利率不能为空
                        if(!StringUtil.isNotEmpty(dto.getIndexId())) {
                            errorList.add(new ValidMessage("indexId", getMessage("COMMON_E_0057",
                                    getMessage("MstCaptialAccount002.currentRate"))));
                        }
                        //系数不能为空
                        if(!StringUtil.isNotEmpty(dto.getCoefficient())) {
                            errorList.add(new ValidMessage("coefficient", getMessage("COMMON_E_0057",
                                    getMessage("MstCaptialAccount002.coefficient"))));
                        }
                        //利差不能为空
                        if(!StringUtil.isNotEmpty(dto.getSpreadRate())) {
                            errorList.add(new ValidMessage("spreadRate", getMessage("COMMON_E_0057",
                                    getMessage("MstCaptialAccount002.spreadRate"))));
                        }
                    }
                } else {
                    errorList.add(new ValidMessage("rateType", getMessage("COMMON_E_0057",
                            getMessage("MstCaptialAccount002.rateType"))));
                }
            }

        } else {
            errorList.add(new ValidMessage("isCalint", getMessage("COMMON_E_0057",
                    getMessage("MstCaptialAccount002.isCalint"))));
        }
    }

}
