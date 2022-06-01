package com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.validator;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.dto.RskSupervisionIndicatorParamDto;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.service.RskSupervisionIndicatorParam001Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RskSupervisionIndicatorParamValidator implements BeanValidator {
    /**
     * 指标参数管理Service
     */
    @Autowired
    private RskSupervisionIndicatorParam001Service rskSupervisionIndicatorParamService;
    @Autowired
	private MstWorkflowCommonService mstWorkflowCommonService;

    @Override
    public List<ValidMessage> valid(Object value) {
        RskSupervisionIndicatorParamDto dto = (RskSupervisionIndicatorParamDto) value;
        ArrayList<ValidMessage> errorList = new ArrayList<>();
        if (StringUtil.equals(dto.getOperateType(), "delete")) {
            if(rskSupervisionIndicatorParamService.isConfigure(dto.getParaCode())>0){
                errorList.add(new ValidMessage(getMessage("SUPERVISION_E_0002")));
            }
            // modify by wangshuang 删除参数，只验证是否与指标关联 2019-12-17 start
           /* if(StringUtil.equals(dto.getParaType(), BussinessDictCodeListRds.指标参数实现类型_固定值.getValue())
            		&&dto.getValidDate()!=null&&DateUtil.compareDate(dto.getValidDate(), DateUtil.platDateFromCache())<0){
            	errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0015",
            			getMessage("RskSupervisionIndicatorParam003.validDate") + DateUtil.format(dto.getValidDate()),
                        "平台日" + DateUtil.format(DateUtil.platDateFromCache()))));
            }
            if(StringUtil.equals(dto.getParaCode(), BussinessDictCodeListRds.参数模板_复合模板.getValue())
            		||StringUtil.equals(dto.getParaCode(), BussinessDictCodeListRds.参数模板_前N日净资产市值.getValue())
            		||StringUtil.equals(dto.getParaCode(), BussinessDictCodeListRds.参数模板_前N日净资产成本.getValue())){
            	errorList.add(new ValidMessage(getMessage("BUSS_COM_E_0005","","参数模板")));
            }*/
            // modify by wangshuang 删除参数，只验证是否与指标关联 2019-12-17 end
      
        }
        else if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)||StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_EDIT)){
          	Map<String,Object> param = new HashMap<String,Object>();
          	param.put("paraName", dto.getParaName());
          	param.put("paraCode", dto.getParaCode());
        	if(rskSupervisionIndicatorParamService.isExistParaName(param)){
        		errorList.add(new ValidMessage(getMessage("BUSS_COM_I_0017",getMessage("RskSupervisionIndicatorParam002.paraName"))));
          	}
        	if(StringUtil.isNotEmpty(dto.getParaCode())) {
                char content[] = dto.getParaCode().toCharArray();
                Pattern pattern = Pattern.compile("[A-Za-z0-9]+$");
                Matcher matcher = pattern.matcher(dto.getParaCode());
                if (!matcher.matches()) {
                        errorList.add(new ValidMessage("paraCode",getMessage("RSKRDS_W_0002",
                                getMessage("RskSupervisionIndicatorParam002.paraCode"))));
                }
            }
            if(StringUtil.equals(dto.getParaDisplayType(), BussinessDictCodeListRds.指标参数实现类型_SQL.getValue())){
                if(StringUtil.isBlank(dto.getMrContent())){
                    errorList.add(new ValidMessage("mrContent",getMessage("BUSS_COM_E_0038",
                            getMessage("RskSupervisionIndicatorParam002.mrContent"))));
                }
                // 当计算方式是redis时才判断redis存储识别域是否为空, fuyongsheng 20210820 edit
                if(StringUtil.equals(dto.getParamMathType(), BussinessDictCodeListRds.指标参数计算方式_REDIS.getValue())
                        &&StringUtil.isBlank(dto.getRedisField())){
                    errorList.add(new ValidMessage("redisField",getMessage("BUSS_COM_E_0038",
                            getMessage("RskSupervisionIndicatorParam002.redisField"))));
                }
                if(StringUtil.isBlank(dto.getMcContent())){
                    errorList.add(new ValidMessage("mcContent",getMessage("BUSS_COM_E_0038",
                            getMessage("RskSupervisionIndicatorParam002.mcContent"))));
                }
            }
            if(StringUtil.equals(dto.getIsForbid(), "Y")&&StringUtil.isBlank(dto.getForbidEnum())){
                errorList.add(new ValidMessage("forbidEnum",getMessage("BUSS_COM_E_0038",
                        getMessage("RskSupervisionIndicatorParam002.forbidEnum"))));
            }
            if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
	             if(rskSupervisionIndicatorParamService.selectParaCode(dto.getParaCode())>0){
	                 errorList.add(new ValidMessage("paraCode",getMessage("BUSS_COM_I_0017",
	                         getMessage("RskSupervisionIndicatorParam002.paraCode"))));
	             }
            }
        	if(StringUtil.isNotEmpty(dto.getParaCode())){
        		WflValidInputParameter parameter = new WflValidInputParameter();
        		parameter.setBusinessDataPk2(dto.getParaCode());
                if(StringUtil.isNotEmpty(dto.getWorkflowId())){
                	parameter.setWorkflowId(dto.getWorkflowId());
                }
                parameter.setPageId("RskSupervisionIndicatorParam002");
                if(null != mstWorkflowCommonService && 
                    mstWorkflowCommonService.isInWflProcess(parameter)){
                	errorList.add(
                        new ValidMessage(this.getMessage("BUSS_COM_I_0017","流程")));
                }
                return errorList;
        	}
        	//判断流程中是否存在相同的数据
        	WflValidInputParameter params = new WflValidInputParameter();
            params.setBusinessDataPk1(dto.getParaName());
            if(StringUtil.isNotEmpty(dto.getWorkflowId())){
            	params.setWorkflowId(dto.getWorkflowId());
            }
            params.setPageId("RskSupervisionIndicatorParam002");
            if(null != mstWorkflowCommonService && 
                mstWorkflowCommonService.isInWflProcess(params)){
            	errorList.add(
                    new ValidMessage(this.getMessage("RSKRDS_W_0073")));
            }
            WflValidInputParameter paraCode = new WflValidInputParameter();
            paraCode.setBusinessDataPk2(dto.getParaCode());
            if(StringUtil.isNotEmpty(dto.getWorkflowId())){
            	paraCode.setWorkflowId(dto.getWorkflowId());
            }
            paraCode.setPageId("RskSupervisionIndicatorParam002");
            if(null != mstWorkflowCommonService && mstWorkflowCommonService.isInWflProcess(paraCode)){
            	errorList.add(new ValidMessage(this.getMessage("流程中参数代码已存在!")));
            }
            WflValidInputParameter params2 = new WflValidInputParameter();
            params2.setBusinessDataPk1(dto.getParaName());
            if(StringUtil.isNotEmpty(dto.getWorkflowId())){
            	params2.setWorkflowId(dto.getWorkflowId());
            }
            params2.setPageId("RskSupervisionIndicatorParam003");
            if(null != mstWorkflowCommonService && 
                mstWorkflowCommonService.isInWflProcess(params2)){
            	errorList.add(
                    new ValidMessage(this.getMessage("RSKRDS_W_0073")));
            }
            WflValidInputParameter params3 = new WflValidInputParameter();
            params3.setBusinessDataPk1(dto.getParaName());
            if(StringUtil.isNotEmpty(dto.getWorkflowId())){
            	params3.setWorkflowId(dto.getWorkflowId());
            }
            params3.setPageId("RskSupervisionIndicatorParam005");
            if(null != mstWorkflowCommonService && 
                mstWorkflowCommonService.isInWflProcess(params3)){
            	errorList.add(
                    new ValidMessage(this.getMessage("RSKRDS_W_0073")));
            }
            WflValidInputParameter params4 = new WflValidInputParameter();
            params4.setBusinessDataPk1(dto.getParaName());
            if(StringUtil.isNotEmpty(dto.getWorkflowId())){
            	params4.setWorkflowId(dto.getWorkflowId());
            }
            params4.setPageId("RskSupervisionIndicatorParam006");
            if(null != mstWorkflowCommonService && 
                mstWorkflowCommonService.isInWflProcess(params4)){
            	errorList.add(
                    new ValidMessage(this.getMessage("RSKRDS_W_0073")));
            }
               
          }
        return errorList;
    }
}
