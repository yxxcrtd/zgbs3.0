package com.joyintech.tams.invest.common.component.base.core.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.tams.common.base.entity.MstBusinessIdRule;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.component.base.core.BaseGetBussinessId;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.component.base.core.impl.dao.BaseGetBussinessIdMapper;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;

/**
 * 业务编号生成类
 *
 * @author dingzhipeng
 */
@Component
public class BaseGetBussinessIdimpl implements BaseGetBussinessId{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(BaseGetBussinessIdimpl.class);

    /**
     * 业务编号生成规则mapper
     */
    @Autowired
    private BaseGetBussinessIdMapper baseGetBussinessIdMapper;

    /**
     *
     */
    @Autowired(required=false)
    private IPlatformDate pPlatformDate;
    /**
     * 生成业务编号
     *
     * @param parameter 共通业务类型
     * @return 业务编号
     * @throws Exception
     */
    @Override
    public String buildBussinessCode(BussinessCodeParameter parameter) {
        // 生成的业务编号
        String bussinessCode = null;
        // 多条规则时直接取第一条 modify by renping 20190924 start
        List<MstBusinessIdRule> ruleList = null;
        if(parameter.getMapInfo()!=null && parameter.getMapInfo().size()>0){
            ruleList=baseGetBussinessIdMapper.selectRuleByMapInfo(parameter);
        }
        else{
            ruleList=baseGetBussinessIdMapper.selectRuleByParameter(parameter);
        }
        MstBusinessIdRule rule = null;
        if (!StringUtil.isEmptyList(ruleList)) {
            rule = ruleList.get(0);
        }
        if(StringUtil.isNotEmpty(rule) || null != rule ){
            if(StringUtil.isNotEmpty(rule.getBusinessIdRuleType())){
                switch (rule.getBusinessIdRuleType()) {
                    case "01":
                        // 直接获取
                        bussinessCode = IDGenerator.generate();
                        break;
                    case "02":
                        // 通过序列号获取
                        bussinessCode = buildCodeByType2(rule);
                        break;
                    case "03":
                        // 前缀+后缀
                        bussinessCode = buildCodeByType3(rule);
                        break;
                    case "04":
                        // 前缀+日期+后缀
                        bussinessCode = buildCodeByType4(rule);
                        break;
                    case "05":
                        // 自定义
                        bussinessCode = buildCodeByType5(rule);
                        break;
                }
            }
        }
        // 多条规则时直接取第一条 modify by renping 20190924 end
       /* if (StringUtil.isNotEmpty(rule) && StringUtil.isNotEmpty(rule.getBusinessIdRuleType())) {
            *//*
             * 01:UUID 02:序列号 03:前缀+后缀 04:前缀+日期+后缀 05:独特规则
             *//*
            switch (rule.getBusinessIdRuleType()) {
                case "01":
                    // 直接获取
                    bussinessCode = IDGenerator.generate();
                    break;
                case "02":
                    // 通过序列号获取
                    bussinessCode = buildCodeByType2(rule);
                    break;
                case "03":
                    // 前缀+后缀
                    bussinessCode = buildCodeByType3(rule);
                    break;
                case "04":
                    // 前缀+日期+后缀
                    bussinessCode = buildCodeByType4(rule);
                    break;
                case "05":
                    // 自定义
                    bussinessCode = buildCodeByType5(rule);
                    break;
            }
        }*/
        return bussinessCode == null ? IDGenerator.generate(20) : bussinessCode;
    }

    /**
     * 类型2：创建方式根据序列生成 业务编号生成规则为【02:序列号】的时候，按照上面取得的前缀生成用序列名称生成序列号，作为业务编号返回给调用端
     *
     * @param rule 规则
     * @return 生成的code值
     * @see
     * @since 1.0
     */
    private String buildCodeByType2(MstBusinessIdRule rule) {
        // 前缀生成用序列名称是否为空
        if (StringUtil.isNotEmpty(rule.getPrefixSequenceName())) {
            // 根据名字获取序列号
            String nextVal = baseGetBussinessIdMapper.getNextValBySequenceName(
                rule.getPrefixSequenceName());
            String bussinessCode = getFormatCode(nextVal, rule.getPrefixSequenceLen().toString());
            return bussinessCode;
        }
        return null;
    }

    /**
     * 类型3：前缀+后缀 业务编号生成规则为【03:前缀+后缀】的时候，按照下面规则生成业务编号返回给调用端
     *
     * @param rule 规则
     * @return 生成的code值
     * @see
     * @since 1.0
     */
    private String buildCodeByType3(MstBusinessIdRule rule) {
        // 根据名字获取前缀序列号
        String prefixSequence = null;
        // 后缀生成用序列名称
        String postfixSequence = null;
        // 前缀生成用序列名称不为空
        if (StringUtil.isNotEmpty(rule.getPrefixSequenceName())) {
            // 根据名字获取前缀序列号
            prefixSequence = getFormatCode(
                baseGetBussinessIdMapper.getNextValBySequenceName(rule.getPrefixSequenceName()),
                rule.getPrefixSequenceLen().toString());
        }
        // 后缀生成用序列名称不为空
        if (StringUtil.isNotEmpty(rule.getPostfixSequenceName())) {
            // 根据名字获取后缀序列号
            postfixSequence = getFormatCode(
                baseGetBussinessIdMapper.getNextValBySequenceName(rule.getPostfixSequenceName()),
                rule.getPostfixSequenceLen().toString());
        }
        // 生成的业务编号
        StringBuffer bussinessCode = new StringBuffer();
        // 前缀名 + 前缀生成用序列名称采番的序列号 + 后缀名 + 后缀生成用序列名称采番的序列号
        bussinessCode.append(nvl(rule.getPrefixName())).append(nvl(prefixSequence)).append(
            nvl(rule.getPostfixName())).append(nvl(postfixSequence));
        return bussinessCode.toString();
    }

    /**
     * 类型4：前缀+日期+后缀 业务编号生成规则为【04:前缀+日期+后缀】的时候，按照下面规则生成业务编号返回给调用端
     *
     * @param rule 规则
     * @return 生成的code值
     * @see
     * @since 1.0
     */
    private String buildCodeByType4(MstBusinessIdRule rule) {
        Date sysDate = null;
        switch (rule.getDateRuleType()) {
            case "01":
                // 平台日期
                if(pPlatformDate!=null){
                    sysDate = pPlatformDate.getSysDate();
                }
                else{
                    sysDate = DateUtil.now();
                }
                break;
            case "02":
                // 系统日期（AP服务器日期）
                sysDate = DateUtil.now();
                break;
            case "03":
                // 系统日期（DB日期）
                if(pPlatformDate!=null){
                    sysDate = pPlatformDate.getDbDate();
                }
                else{
                    sysDate = DateUtil.now();
                }
                break;
        }
        // 根据名字获取前缀序列号
        String prefixSequence = null;
        // 后缀生成用序列名称
        String postfixSequence = null;
        // 前缀生成用序列名称不为空
        if (StringUtil.isNotEmpty(rule.getPrefixSequenceName())) {
            // 根据名字获取前缀序列号
            prefixSequence = getFormatCode(
                baseGetBussinessIdMapper.getNextValBySequenceName(rule.getPrefixSequenceName()),
                rule.getPrefixSequenceLen().toString());
        }
        // 后缀生成用序列名称不为空
        if (StringUtil.isNotEmpty(rule.getPostfixSequenceName())) {
            // 根据名字获取后缀序列号
            postfixSequence = getFormatCode(
                baseGetBussinessIdMapper.getNextValBySequenceName(rule.getPostfixSequenceName()),
                rule.getPostfixSequenceLen().toString());
        }
        // 生成的业务编号
        StringBuffer bussinessCode = new StringBuffer();
        // 前缀名 + 前缀生成用序列名称采番的序列号 + 上面取得的日期 + 后缀名 + 后缀生成用序列名称采番的序列号
        bussinessCode.append(nvl(rule.getPrefixName())).append(nvl(prefixSequence)).append(
                DateUtil.format(sysDate, DateUtil.DEFAULT_OTHER_FORMAT)).append(
                nvl(rule.getPostfixName())).append(nvl(postfixSequence));
        return bussinessCode.toString();
    }

    /**
     * 类型5：创建方式根据序列生成 <功能详细描述>
     *
     * @param rule 规则
     * @return 生成的code值
     * @throws Exception
     * @see
     * @since 1.0
     */
    private String buildCodeByType5(MstBusinessIdRule rule) {
        if (StringUtil.isNotEmpty(rule.getBusinessIdServiceName())
            && StringUtil.isNotEmpty(rule.getBusinessIdMethodName())) {
            Class<?> clazz;
            try {
                clazz = Class.forName(rule.getBusinessIdServiceName());
                Method method = clazz.getMethod(rule.getBusinessIdMethodName());
                return (String)method.invoke(clazz.newInstance());
            }
            catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }

    /**
     * 根据当前值和长度生成格式化长度
     *
     * @param code 待格式化数据
     * @param rule 格式化长度
     * @return 结果
     * @see
     * @since 1.0
     */
    private String getFormatCode(String code, String rule) {
        rule = "%0" + rule + "d";
        return String.format(rule, Long.valueOf(code));
    }

    /**
     * 获取默认字符串 <功能详细描述>
     *
     * @param code 目标值
     * @param def 默认值
     * @return 结果
     * @see
     * @since 1.0
     */
    private String nvl(String code, String def) {
        return StringUtil.isNotEmpty(code) ? code : def != null ? def : "";
    }

    /**
     * 默认非空方法 <功能详细描述>
     *
     * @param code 非空字段
     * @return 返回值
     * @see
     * @since 1.0
     */
    private String nvl(String code) {
        return nvl(code, "");
    }
}
