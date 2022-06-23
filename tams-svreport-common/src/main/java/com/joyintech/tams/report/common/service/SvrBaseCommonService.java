/*
 * 文件名：BaseCommonService.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：中信银行资管系统 修改人：qiangchengxi 修改时间：2016年11月16日 修改内容：新建 系统名称：中信银行资管系统
 */

package com.joyintech.tams.report.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.ChbSubCheckBasicRule;
import com.joyintech.tams.report.common.dao.SvrBaseCommonMapper;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 共通Service
 *
 * @author qiangchengxi
 * @version 1.0 2016年11月16日
 * @see SvrBaseCommonService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Transactional(rollbackFor = Exception.class)
public class SvrBaseCommonService extends BaseService {
    /**
     * BaseCommonMapper
     */
    @Autowired
    private SvrBaseCommonMapper svrBaseCommonMapper;
    
    /**
	 * logger
	 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    /**
//     * SuggestService
//     */
//    @Autowired
//    private SuggestService suggestService;
//
//    /**
//     * 系统参数Service
//     */
//    @Autowired
//    private SystemParaService systemParaService;

    /**
     * 获取当前系统平台日期
     *
     * @return 当前系统平台日期
     */
    public String getSysDateString() {
        return DateUtil.format(/*baseCommonMapper.getNowSysDate()*/DateUtil.now());
    }

    /**
     * 获取当前系统平台日期
     *
     * @return 当前系统平台日期
     */
    public Date getSysDate() {
        return /*baseCommonMapper.getNowSysDate()*/DateUtil.now();
    }
//
//    /**
//     * 获取名称全拼
//     *
//     * @param name 名称
//     * @return 匹配结果
//     * @throws BadHanyuPinyinOutputFormatCombination 错误信息
//     * @see
//     * @since 1.0
//     */
//    public String getPinyinStr(String name)
//        throws BadHanyuPinyinOutputFormatCombination {
//        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        String pinyin = StringUtils.EMPTY;
//        String[] pinyinArr;
//        for (int i = 0; i <= name.length() - 1; i++ ) {
//            pinyinArr = PinyinHelper.toHanyuPinyinStringArray(name.charAt(i), format);
//            if (pinyinArr == null) {
//                pinyinArr = new String[1];
//                pinyinArr[0] = String.valueOf(name.charAt(i)).toLowerCase();
//            }
//            if (pinyinArr != null && pinyinArr.length > 0) {
//                pinyin += pinyinArr[0];
//            }
//        }
//        return pinyin;
//    }
//
//    /**
//     * 获取名称首字母
//     *
//     * @param name 名称
//     * @return 匹配结果
//     * @throws BadHanyuPinyinOutputFormatCombination 报错信息
//     * @see
//     * @since 1.0
//     */
//    public String getFirstPinyinStr(String name)
//        throws BadHanyuPinyinOutputFormatCombination {
//        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        String pinyin = StringUtils.EMPTY;
//        String[] pinyinArr;
//        if (StringUtil.isNotEmpty(name)) {
//            for (int i = 0; i <= name.length() - 1; i++ ) {
//                pinyinArr = PinyinHelper.toHanyuPinyinStringArray(name.charAt(i), format);
//                if (pinyinArr == null) {
//                    pinyinArr = new String[1];
//                    pinyinArr[0] = String.valueOf(name.charAt(i)).toLowerCase();
//                }
//                if (pinyinArr != null && pinyinArr.length > 0) {
//                    pinyin += pinyinArr[0].charAt(0);
//                }
//            }
//        }
//        return pinyin;
//    }

    /**
     * 获取中债报送自检规则数据
     *
     * @return 中债报送自检规则数据
     * @see
     * @since 1.0
     */
    public Map<String, ChbSubCheckBasicRule> getSubCheckBasicRuleData() {
        Map<String, ChbSubCheckBasicRule> resultMap = new HashMap<String, ChbSubCheckBasicRule>();

        try {
            List<ChbSubCheckBasicRule> subCheckBasicRuleList = svrBaseCommonMapper.selectSubCheckBasicRuleData();
            if (CollectionUtils.isNotEmpty(subCheckBasicRuleList)) {
                resultMap = subCheckBasicRuleList.stream().collect(
                    JoyinCollectors.toMap(x -> x.getRuleId(), x -> x));
            }
        }
        catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }

        return resultMap;
    }

//    /**
//     * 刷新系统缓存
//     *
//     * @see
//     * @since 1.0
//     */
//    public void refreshCache() {
//        refreshSystemData();
//
//        /*
//         * ****************************
//         * 缓存搜索建议框业务数据
//         * ****************************
//         * */
//        Map<String, List<SuggestDto>> suggestMap = new HashMap<String, List<SuggestDto>>();
//        List<SuggestDto> prdSuggestList = suggestService.selectAllPrdInfo();
//        suggestMap.put(GlobalConstant.SUGGEST_PRDCODE, prdSuggestList);
//
//        AppCache.remove(GlobalConstant.BUSINESS_DATA_CACHE_KEY);
//        AppCache.setValue(GlobalConstant.BUSINESS_DATA_CACHE_KEY, suggestMap);
//    }
//
//    /**
//     * <一句话功能简述> <功能详细描述>
//     *
//     * @see
//     * @since 1.0
//     */
//    public void refreshSystemData() {
//        List<SysDataDictDto> list = dataDictService.findAllDataDict();
//
//        if (CollectionUtils.isNotEmpty(list)) {
//
//            Map<String, SysDataDictDto> cacheMap = new HashMap<>(list.size());
//            for (SysDataDictDto dto : list) {
//                cacheMap.put(dto.getDictCode(), dto);
//            }
//            // 删除数据字典缓存
//            AppCache.remove(GlobalConstant.DATA_DICT_CACHE_KEY);
//            // 缓存数据字典
//            AppCache.setValue(GlobalConstant.DATA_DICT_CACHE_KEY, cacheMap);
//        }
//
//        // add by liurongrong 20161219 begin
//        // 系统参数信息
//        List<ChbSysPara> sysParaList = systemParaService.getSysParaList();
//        if (CollectionUtils.isNotEmpty(sysParaList)) {
//            Map<String, String> resultMap = new HashMap<String, String>();
//            for (ChbSysPara sysPara : sysParaList) {
//                resultMap.put(sysPara.getParaCode(), sysPara.getParamValue());
//            }
//            // 删除缓存系统参数信息
//            AppCache.remove(GlobalConstant.SYS_PARA_KEY);
//            // 缓存系统参数信息
//            AppCache.setValue(GlobalConstant.SYS_PARA_KEY, resultMap);
//        }
//    }
    
    /**
     * 查询机构下的所有部门代码
     * @param institutionCode 机构代码
     * @return 部门代码列表
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    public List<String> selectDeptCode(String institutionCode) {
        return svrBaseCommonMapper.selectDeptCode(institutionCode);
    }
}
