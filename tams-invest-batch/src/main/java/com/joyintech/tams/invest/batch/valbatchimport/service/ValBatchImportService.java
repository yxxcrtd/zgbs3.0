package com.joyintech.tams.invest.batch.valbatchimport.service;

import com.google.common.collect.Lists;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.intf.DataDict;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.dao.RskOutValtblImportHisMapper;
import com.joyintech.tams.common.base.dao.RskValtblAssetSubMapper;
import com.joyintech.tams.common.base.dao.RskValtblPrtBotassetMapMapper;
import com.joyintech.tams.common.base.dao.RskValtblPrtImpMapMapper;
import com.joyintech.tams.common.base.dao.RskValtblSumAssetSubMapper;
import com.joyintech.tams.common.base.dao.RskValtblSumsubMapMapper;
import com.joyintech.tams.common.base.dao.ValtblImportDateTraceMapper;
import com.joyintech.tams.common.base.dao.ValtblImportHisTraceMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.valbatchimport.common.dao.AssetCommonMapper;
import com.joyintech.tams.invest.batch.valbatchimport.common.dto.BaseFinProductBaseInfoDto;
import com.joyintech.tams.invest.batch.valbatchimport.common.dto.InfChinamutualfunddescriptionDto;
import com.joyintech.tams.invest.batch.valbatchimport.common.dto.InterfPayhsrcsecinfoDto;
import com.joyintech.tams.invest.batch.valbatchimport.common.dto.InterfStockInfoDto;
import com.joyintech.tams.invest.batch.valbatchimport.common.service.CommonAutoCouponService;
import com.joyintech.tams.invest.batch.valbatchimport.constant.ValtbImportConstant;
import com.joyintech.tams.invest.batch.valbatchimport.dao.RskValtblPrtBotassetMapper;
import com.joyintech.tams.invest.batch.valbatchimport.dao.ValBatchImportMapper;
import com.joyintech.tams.invest.batch.valbatchimport.dto.*;
import com.joyintech.tams.invest.batch.valbatchimport.filter.Filter;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.util.ExcelUtilForRisk;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 委外估值表导入
 * 〈功能详细描述〉
 *
 * @author wangyang1
 * @version 1.0 2021年1月18日
 * @see ValBatchImportService
 * @since 1.0
 */
@Service
public class ValBatchImportService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CommonAutoCouponService commonAutoCouponService;

    // 平台日期
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired(required = false)
    private ValBatchImportMapper valBatchImportMapper;

    @Autowired
    private RskValtblPrtImpMapMapper rskValtblPrtImpMapMapper;

    @Autowired
    private RskValtblSumsubMapMapper rskValtblSumsubMapMapper;

    @Autowired
    private RskOutValtblImportHisMapper rskOutValtblImportHisMapper;

    @Autowired
    private ValtblImportHisTraceMapper valtblImportHisTraceMapper;

    @Autowired
    private RskValtblAssetSubMapper rskValtblAssetSubMapper;

    @Autowired
    private RskValtblPrtBotassetMapMapper rskValtblPrtBotassetMapMapper;

    @Autowired
    private RskValtblSumAssetSubMapper rskValtblSumAssetSubMapper;

    @Autowired
    private AssetCommonMapper assetCommonMapper;

    @Autowired
    private ValtblImportDateTraceMapper valtblImportDateTraceMapper;

    private final static String BAK_START_STR = "BAK";

    /**
     * 债券
     */
    public static final String KEY_ZQ_STR = "1103";
    /**
     * 基金
     */
    public static final String KEY_JJ_STR = "1105";
    /**
     * 股票
     */
    public static final String KEY_GP_STR = "1102";
    /**
     * 利息
     */
    public static final String KEY_INT_STR = "1204";

    /**
     * 非标
     */
    public static final String KEY_FB_STR = "1701";
    /**
     * 项目类
     */
    public static final String KEY_LLXFB_STR = "1901";
    /**
     * 存款类
     */
    public static final String KEY_CKL_STR = "1003";
    /**
     * 逆回购
     */
    public static final String KEY_NHGB_STR = "1202";

    /**
     * 正回购利息
     */
    public static final String KEY_ZHGI_STR = "2231";
    /**
     * 期权
     */
    public static final String KEY_QQ_STR = "3103";
    /**
     * 期货
     */
    public static final String KEY_QH_STR = "3102";
    /**
     * 逆回购利息
     */
    public static final String KEY_NHGI_STR = "120410";

    /**
     * 正回购
     */
    public static final String KEY_ZHGB_STR = "2202";

    public static final String KEY_INT_GPJJ_STR = "1203";
    /**
     * 委外估值表导入
     *
     * @param supDate
     */
    public void execute(Date supDate) {
        String valTableFilePath = CacheUtil.getSysParam("RSK_OUTS_VALTABLE_IMPORT_PATH");
        //是否是联社模式
        Boolean flag = StringUtil.equals(CacheUtil.getSysParam("AUTHORITY_TYPE"),"02");
        Map<String, Date> dateMap = new HashMap<>();

        if (flag){
            //查询所有机构的平台日
            List<Map<String, String>> mapList = valBatchImportMapper.searchDate();
            mapList.forEach(x->{
                dateMap.put(x.get("CODE"),DateUtil.parse(x.get("CDATE")));
            });
        }

        String filePath = valTableFilePath+ File.separator+DateUtil.format(new Date(),DateUtil.DEFAULT_OTHER_FORMAT);
        File file = new File(filePath);
        if (file.isDirectory() && file.listFiles().length > 0) {
            logger.info("开始导入委外估值表......文件扫描，共扫描到"+file.listFiles().length+"个文件！");
            //开始解析文件内容入库
            if (StringUtil.equals(CacheUtil.getSysParam("AUTHORITY_TYPE"),"02")){
                for (File file1 : file.listFiles()) {
                    if (file1.isDirectory()){
                        this.dealFiles(file1,supDate,dateMap,filePath,flag);
                    }
                }
            } else{
                this.dealFiles(file,supDate,dateMap,filePath,flag);
            }
        }
    }

    public static String toSemiangle(String src) {
        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++) {
            if (c[index] == 12288) {
                c[index] = (char) 32;
            } else if (c[index] > 65280 && c[index] < 65375) {
                c[index] = (char) (c[index] - 65248);
            }
        }
        return String.valueOf(c);
    }

    /**
     * 底层---一级汇总
     * <功能详细描述>
     *
     * @param valList
     * @param vtaccountMap
     * @param vtcountSubList
     * @return
     * @see
     * @since 1.0
     */
    private List<RskValtblAssetSubDto> sum_handValList(List<RskValtblAssetSubDto> valList, Map<String, SubCodeEntry> vtaccountMap, List<RskValtblSubject> vtcountSubList) {
        try {
            List<RskValtblAssetSubDto> valHand_threeList = commonHandleSum(valList, vtaccountMap, vtcountSubList, "03");
            List<RskValtblAssetSubDto> valHand_twoList = commonHandleSum(valHand_threeList, vtaccountMap, vtcountSubList, "02");
            List<RskValtblAssetSubDto> valHand_oneList = commonHandleSum(valHand_twoList, vtaccountMap, vtcountSubList, "01");
            Collections.sort(valHand_oneList);
            return valHand_oneList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 底层向上汇总--共通
     * <功能详细描述>
     *
     * @param valList
     * @param vtcountSubList
     * @param level          03(三级) 02(二级) 01(一级)
     * @return
     * @see
     * @since 1.0
     */
    private List<RskValtblAssetSubDto> commonHandleSum(List<RskValtblAssetSubDto> valList, Map<String, SubCodeEntry> vtaccountMap, List<RskValtblSubject> vtcountSubList, String level) {
        List<RskValtblAssetSubDto> resultList = new LinkedList<RskValtblAssetSubDto>();
        Map<String, String> tempmap = new HashMap<String, String>();
        for (RskValtblAssetSubDto por : valList) {
            String subSubjcode = StringUtil.EMPTY;
            RskValtblAssetSubDto tempporDto = new RskValtblAssetSubDto();
            BigDecimal security_cost = BigDecimal.ZERO;
            BigDecimal ctnwr = BigDecimal.ZERO;
            BigDecimal smv = BigDecimal.ZERO;
            BigDecimal mcr = BigDecimal.ZERO;
            boolean flag = false;
            if (StringUtil.equals("03", level)) {
                if (por.getSubCode().length() >= 8) {
                    subSubjcode = por.getSubCode().substring(0, 8);
                }
            } else if (StringUtil.equals("02", level)) {
                if (por.getSubCode().length() <= 8 && por.getSubCode().length() >= 6) {
                    subSubjcode = por.getSubCode().substring(0, 6);
                }
            } else if (StringUtil.equals("01", level)) {
                if (por.getSubCode().length() <= 6 && por.getSubCode().length() >= 4) {
                    subSubjcode = por.getSubCode().substring(0, 4);
                }
            }
            if (tempmap.containsKey(subSubjcode)) {
                continue;
            }
            if (StringUtil.isNotEmpty(subSubjcode)) {
                tempmap.put(subSubjcode, subSubjcode);
                for (RskValtblAssetSubDto por_all : valList) {
                    if (StringUtil.isEmpty(por_all.getAllowSum())) {
                        por_all.setAllowSum("Y");
                    }
                    if (StringUtil.equals(por_all.getAllowSum(), "N")) {
                        continue;
                    } else {
                        if (flag == false) {
                            for (RskValtblSubject dto : vtcountSubList) {
                                if (dto.getSubCode().equals(subSubjcode)) {
                                    flag = true;
                                    tempporDto.setSubName(dto.getSubName());
                                }
                            }
                        }
                        if (por_all.getSubCode().startsWith(subSubjcode)) {
                            if ((StringUtil.equals("03", level) && (por_all.getSubCode().length() >= 8))
                                    || (StringUtil.equals("02", level) && (por_all.getSubCode().length() == 8 || por_all.getSubCode().length() == 6))
                                    || (StringUtil.equals("01", level) && ((por_all.getSubCode().length() == 6 || por_all.getSubCode().length() == 4)))) {
                                if (por_all.getSecurityCost() != null) {
                                    security_cost = ArithmeticUtil.doAdd(security_cost, por_all.getSecurityCost());
                                }
                                if (por_all.getCtnwr() != null) {
                                    ctnwr = ArithmeticUtil.doAdd(ctnwr, por_all.getCtnwr());
                                }
                                if (por_all.getSmv() != null) {
                                    smv = ArithmeticUtil.doAdd(smv, por_all.getSmv());
                                }
                                if (por_all.getMcr() != null) {
                                    mcr = ArithmeticUtil.doAdd(mcr, por_all.getMcr());
                                }
                            }
                        }
                    }
                }
                if (flag == false) {
                    tempporDto.setSubName("");
                }
                tempporDto.setValType(ValType.ASSET);
                tempporDto.setSubCode(subSubjcode);
                tempporDto.setSecurityCost(security_cost);
                tempporDto.setCtnwr(ctnwr);
                tempporDto.setSmv(smv);
                tempporDto.setMcr(mcr);
                tempporDto.setIsMatch(por.getIsMatch());//add by lpf 是否匹配
                resultList.add(tempporDto);
            }
        }
        //移除重复的科目代码数据
        for (RskValtblAssetSubDto por : valList) {
            if (!tempmap.containsKey(por.getSubCode())) {
                resultList.add(por);
            }
        }
        return resultList;
    }

    /**
     * 处理资产科目
     * <功能详细描述>
     *
     * @param valList
     * @param vtaccountMap
     * @return
     * @see
     * @since 1.0
     */
    private List<RskValtblAssetSubDto> handValList(List<RskValtblAssetSubDto> valList, Map<String, SubCodeEntry> vtaccountMap) {
        //处理valList---取出映射表配置的科目代码及底层资产
        List<RskValtblAssetSubDto> one_handval_List = new LinkedList<RskValtblAssetSubDto>();
        List<RskValtblAssetSubDto> two_handval_List = new LinkedList<RskValtblAssetSubDto>();
        List<RskValtblAssetSubDto> sum_val_List = new LinkedList<RskValtblAssetSubDto>();
        List<RskValtblAssetSubDto> unMatchList = new LinkedList<RskValtblAssetSubDto>();//add by lpf 统计未匹配的在画面红字展示2021-03-23
        String TAX_SECTION_BEGIN = CacheUtil.getSysParam("TAX_SECTION_BEGIN");
        String GP_VALUATION_APPRECIATION_SUBJECTS = CacheUtil.getSysParam("GP_VALUATION_APPRECIATION_SUBJECTS");
        for (RskValtblAssetSubDto handval : valList) {
            handval.setIsMatch(DictCodeListExt1.是否_是.getValue());
            if (handval.getValType().equals(ValType.SUM)) {
                sum_val_List.add(handval);
            }
            String subjCode = handval.getSubCode();
            if (vtaccountMap.containsKey(subjCode)) {
                if (vtaccountMap.get(subjCode) != null) {
                    //允许汇总 Y N
                    handval.setAllowSum(vtaccountMap.get(subjCode).getAllowSum());
                    one_handval_List.add(handval);
                }
            } else {
                boolean isMatch = false;//add by lpf 统计未匹配的
                for(int i=subjCode.length();i>0;i--){
                    String beforSub = subjCode.substring(0, i);
                    if(vtaccountMap.containsKey(beforSub)){
                        one_handval_List.add(handval);
                        isMatch = true;
                        break;
                    }
                }
                //统计未匹配的红字展示 add by lpf 2021-03-23
                //“1” “3”开头或者 ”2“开头回购融入
                if (!(StringUtil.splitToList(",", GP_VALUATION_APPRECIATION_SUBJECTS).contains(handval.getSubCode()))) {
                    if (!isMatch && handval.getValType() == ValType.ASSET
                            && (!(handval.getSubCode().substring(0, 1).equals(TAX_SECTION_BEGIN)) &&
                            ((handval.getSubCode().length() >= 4 && !handval.getSubCode().substring(0, 4).equals("1202"))
                                    ||(handval.getSubCode().length() >= 6 && !handval.getSubCode().substring(0, 6).equals("120410"))))) {
                        handval.setIsMatch(DictCodeListExt1.是否_否.getValue());
                        unMatchList.add(handval);
                    }
                }
                //统计未匹配的红字展示 add by lpf
            }
        }
        //保留叶子节点
        for (RskValtblAssetSubDto one_handval : one_handval_List) {
            int i = 0;
            for (RskValtblAssetSubDto two_handval : one_handval_List) {
                if (two_handval.getSubCode().startsWith(one_handval.getSubCode())
                        && two_handval.getSubCode().length() > one_handval.getSubCode().length()) {
                    i++;
                    break;
                }
            }
            if (i == 0) {
                two_handval_List.add(one_handval);
            }
        }
        //add by lpf 去除未映射List 子节点已映射的
        for(RskValtblAssetSubDto dto :two_handval_List){
            Iterator<RskValtblAssetSubDto> it = unMatchList.iterator();
            while (it.hasNext()){
                RskValtblAssetSubDto dtoit = it.next();
                if(dto.getSubCode().startsWith(dtoit.getSubCode())){
                    it.remove();
                }
            }
        }
        //资产科目代码替换成标准科目代码
        for (RskValtblAssetSubDto two_handval : two_handval_List) {
            String subjCode = two_handval.getSubCode();
            if (vtaccountMap.containsKey(subjCode)) {
                if (vtaccountMap.get(subjCode) != null) {
                    SubCodeEntry entity = vtaccountMap.get(subjCode);
                    //允许汇总 Y N
                    two_handval.setAllowSum(entity.getAllowSum());
                    two_handval.setSubCode(entity.getTgt_code());
                    two_handval.setSubName(entity.getTgt_name());
                }
            } else {
                for (int i = subjCode.length(); i > 0; i--) {
                    String beforSub = subjCode.substring(0, i);
                    String afterSub = subjCode.substring(i);
                    if (vtaccountMap.containsKey(beforSub)) {
                        SubCodeEntry tempEntity = vtaccountMap.get(beforSub);
                        two_handval.setSubCode(tempEntity.getTgt_code() + afterSub);
                        break;
                    }
                }
            }
        }
        valList.clear();
        valList.addAll(sum_val_List);
        valList.addAll(two_handval_List);
        valList.addAll(unMatchList);//统计未匹配的
        return valList;
    }

    /**
     * 处理excel------估值表
     * <功能详细描述>
     *
     * @param list
     * @param paramMap
     * @return
     * @see
     * @since 1.0
     */
    /**
     * 处理excel------估值表
     * @param list
     * @param paramMap
     * @return
     */
    private RskValtblAssetSubDto handExcelListForVal(List<String> list, Map<String, Object> paramMap) {
        RskValtblAssetSubDto val = new RskValtblAssetSubDto();
        int subjCodeColIdx = Integer.valueOf(paramMap.get("subjectCodeCol").toString()) - 1;
        String subjCode = StringUtil.EMPTY;
        if (list.size() > subjCodeColIdx + 1) {
            subjCode = list.get(subjCodeColIdx);
        }
        if (StringUtil.isEmpty(subjCode)) {
            return null;
        } else {
            // Sonar Fix modify by huwei 220609
            subjCode = subjCode.replaceAll(" ", "");
        }
        //根据科目代码列 判断是资产科目还是汇总科目
        if (subjCode.length() >= 4) {
            try {
                Integer.valueOf(subjCode.substring(0, 4));
                val.setValType(ValType.ASSET);
            } catch (NumberFormatException e) {
                val.setValType(ValType.SUM);
            }
        } else {
            val.setValType(ValType.SUM);
        }
        val = handleSubjCodeCol(list, val, paramMap, subjCodeColIdx, subjCode);
        return val;
    }

    /**
     * 处理科目代码列
     * @param list
     * @param val
     * @param paramMap
     * @param subjCodeColIdx
     * @param subjCode
     * @return
     */
    private RskValtblAssetSubDto handleSubjCodeCol(List<String> list, RskValtblAssetSubDto val, Map<String, Object> paramMap, int subjCodeColIdx, String subjCode) {
        SumNameCodeEntry sumNameCodeEntry = null;
        String subjName = StringUtil.EMPTY;
        switch (val.getValType()) {
            case ASSET:
                val.setSubCode(subjCode);
                val.setSubName(subjName);
                break;
            case SUM:
                Map<String, SumNameCodeEntry> vtsumaccountMap = (Map<String, SumNameCodeEntry>) paramMap.get("vtsumaccountMap");
                if (paramMap.get("trimChars") != null) {
                    if (StringUtil.isNotEmpty(paramMap.get("trimChars").toString())) {
                        String regex = "["+ paramMap.get("trimChars").toString()
                                .replaceAll("\\\\", "\\\\\\\\")
                                .replaceAll("\\[", "\\\\[")
                                .replaceAll("\\]", "\\\\]")
                                .replaceAll("\\^", "\\\\^")+ "]";
                        subjCode = subjCode.replaceAll(regex, "");
                    }
                }
                if (vtsumaccountMap.containsKey(subjCode)) {
                    sumNameCodeEntry = vtsumaccountMap.get(subjCode);
                }
                if (sumNameCodeEntry == null) return null;
                String sumName = "";
                if (paramMap.get("trim_chars") != null) {
                    if (StringUtil.isNotEmpty(paramMap.get("trim_chars").toString())) {
                        String regex = "["+ paramMap.get("trim_chars").toString()
                                .replaceAll("\\\\", "\\\\\\\\")
                                .replaceAll("\\[", "\\\\[")
                                .replaceAll("\\]", "\\\\]")
                                .replaceAll("\\^", "\\\\^")+ "]";
                        sumName = sumNameCodeEntry.getTgt_sumName().replaceAll(regex, "");
                    } else {
                        sumName = sumNameCodeEntry.getTgt_sumName();
                    }
                } else {
                    sumName = sumNameCodeEntry.getTgt_sumName();
                }
                val.setSubName(sumName);
                val.setSubCode(sumNameCodeEntry.getSumCode());
                break;
        }
        //下一列
        subjName = list.get(subjCodeColIdx + 1);
        if (val != null) {
            switch (val.getValType()) {
                case ASSET:
                    val.setSubName(subjName);
                    break;
                case SUM:
                    // Sonar Fix modify by huwei 220609
                    if (null == sumNameCodeEntry || ValDataType.NULL == sumNameCodeEntry.getNextColType()) break;
                    Double cellNum = null;
                    if (StringUtil.isNotEmpty(subjName)) {
                        subjName = subjName.replaceAll(",", "");
                        if (ValDataType.STRING == sumNameCodeEntry.getNextColType()) {
                            cellNum = null;
                        } else {
                            if (subjName.contains("%")) {
                                subjName = subjName.replaceAll("%", "");
                                cellNum = (BigDecimal.valueOf(Double.valueOf(subjName) / 100)).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                            } else {
                                cellNum = (BigDecimal.valueOf(Double.valueOf(subjName))).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                            }
                        }
                    } else {
                        cellNum = null;
                    }
                    if (cellNum != null) {
                        if (ValDataType.PERCENT == sumNameCodeEntry.getNextColType()) {
                            cellNum = (BigDecimal.valueOf(cellNum / 100)).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                        }
                        val.setSecurityCost(BigDecimal.valueOf(cellNum));
                    }
                    break;
            }
            //处理其他列
            handleOtherCol(list, val, paramMap);
        }
        return val;
    }

    /**
     * 处理其他列
     * @param list
     * @param val
     * @param paramMap
     */
    private void handleOtherCol(List<String> list, RskValtblAssetSubDto val, Map<String, Object> paramMap) {
        String cellVlaue = StringUtil.EMPTY;
        BigDecimal cellNum = null;
        String cellStr = StringUtil.EMPTY;
        for (int i = 0; i < list.size(); i++) {
            cellVlaue = list.get(i);
            cellStr = cellVlaue.replaceAll(" ", "");
            try {
                Map<String, ColumnMapEntry> vtstructureMap = new HashMap<String, ColumnMapEntry>();
                vtstructureMap = (Map<String, ColumnMapEntry>) paramMap.get("vtstructureMap");
                ColumnMapEntry colMapEntry = vtstructureMap.get(Integer.toString(i + 1));
                if (colMapEntry == null) continue;
                if (StringUtil.isNotEmpty(cellStr)) {
                    cellStr = cellStr.replaceAll(",", "");
                    if (ValDataType.STRING == colMapEntry.getDataType()) {
                        cellNum = null;
                    } else {
                        if (cellVlaue.contains("%")) {
                            cellStr = cellStr.replaceAll("%", "");
                            cellNum = (BigDecimal.valueOf(Double.valueOf(cellStr) / 100)).setScale(6, BigDecimal.ROUND_HALF_UP);
                        } else {
                            cellNum = (BigDecimal.valueOf(Double.valueOf(cellStr))).setScale(6, BigDecimal.ROUND_HALF_UP);
                        }
                    }
                } else {
                    cellNum = null;
                }
                if (ValDataType.PERCENT == colMapEntry.getDataType()) {
                    cellNum = cellNum == null ? null : (cellNum.divide(new BigDecimal(100))).setScale(6, BigDecimal.ROUND_HALF_UP);
                }
                switch (colMapEntry.getValIndex()) {
                    case QUANTITY:
                        if (cellNum != null) val.setSecurityQuantity(cellNum);
                        break;
                    case UNIT_COST:
                        if (cellNum != null) val.setUnitCost(cellNum);
                        break;
                    case COST:
                        if (cellNum != null) {
                            if (val.getSecurityCost() == null) {
                                val.setSecurityCost(cellNum);
                            }
                        }
                        break;
                    case CTNWR:
                        if (cellNum != null) val.setCtnwr(cellNum);
                        break;
                    case MCP:
                        if (cellNum != null) val.setMcp(cellNum);
                        break;
                    case SMV:
                        if (cellNum != null) val.setSmv(cellNum);
                        break;
                    case MCR:
                        if (cellNum != null) val.setMcr(cellNum);
                        break;
                    case VAL_INC:
                        if (cellNum != null) val.setValueAdded(cellNum);
                        break;
                    case SUSPENSION_INFO:
                        val.setSuspensionInfo(cellStr);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  重读excel
     * <功能详细描述>
     * @param dto
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    private List<List<String>> readExcelUtil(RskValTableImportParamDto dto) throws FileNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<List<String>> resultList = new ArrayList<List<String>>();
        Workbook wb = ExcelUtil.loadWorkbook(dto.getValTableFileName(), null, null);
        if (Objects.isNull(wb)) {
            return null;
        }
        // ---modify by 裴家俊 20190514---start---------
        Filter filter = null;
        if (StringUtil.isNotEmpty(dto.getFilterId())) {
            if (dto.getFilterId().startsWith(BussinessConstant.VALUATION_FILTER_MERGECOL)) {
                filter = (Filter) Class
                        .forName(BussinessConstant.VALUATION_BATCH_FILTER_URL + BussinessConstant.VALUATION_FILTER_MERGECOL)
                        .newInstance();
            } else {
                filter = (Filter) Class.forName(BussinessConstant.VALUATION_BATCH_FILTER_URL + dto.getFilterId())
                        .newInstance();
            }
        }
        if (filter != null) {
            if (dto.getFilterId().startsWith(BussinessConstant.VALUATION_FILTER_MERGECOL)) {
                wb = filter.doFilter(wb, 0,
                        dto.getFilterId().substring(BussinessConstant.VALUATION_FILTER_MERGECOL.length() + 1));
            } else {
                wb = filter.doFilter(wb, 0);
            }
            resultList = readToList(0, Integer.parseInt(dto.getStartRow()) - 1, Integer.parseInt(dto.getStartRow()) - 1,
                    wb);
        } else {
            resultList = readToList(0, Integer.parseInt(dto.getStartRow()) - 1, Integer.parseInt(dto.getStartRow()) - 2,
                    wb);
        }
        // ---modify by 裴家俊 20190514---end---------
        return resultList;
    }

    /**
     * 解析excel
     * <功能详细描述>
     * @param sheetIndex
     * @param skipRows
     * @param titleRow
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    private List<List<String>> readToList(int sheetIndex, int skipRows, int titleRow,Workbook wb)
    {
        List<List<String>> ls = new ArrayList<List<String>>();
        if (null != wb) {
            Sheet sh = wb.getSheetAt(sheetIndex);
            int rows = sh.getPhysicalNumberOfRows();
            int cells = sh.getRow(titleRow).getPhysicalNumberOfCells();
            for (int i = skipRows; i < rows; i++ ) {
                Row row = sh.getRow(i);
                if (row == null) {
                    break;
                }
                List<String> r = new ArrayList<String>(cells);
                boolean dataFlag = true;
                for (int c = 0; c < cells; c++ ) {
                    if (row.getCell(c) == null
                            || row.getCell(c).getCellTypeEnum() == CellType.BLANK) {
                        r.add("");
                    }
                    else {
                        dataFlag = false;
                        r.add(getStringCellValue(row.getCell(c)));
                    }
                }
                // 遇到一行全为空继续
                if (dataFlag) {
                    continue;
                }
                ls.add(r);
            }
        }
        return ls;
    }

    private static String getStringCellValue(Cell cell) {
        String value = null;
        CellType cellType=cell.getCellTypeEnum();
        switch (cellType) {
            case NUMERIC: // 数字
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = DateUtil.format(cell.getDateCellValue(),
                            DateUtil.DEFAULT_ANOTHER_FORMAT);
                    return value;
                }
                value = ExcelUtil.point.format(cell.getNumericCellValue());
                break;
            case STRING: // 字符串
                value = cell.getStringCellValue();
                break;
            case FORMULA: // 公式
                cell.setCellType(CellType.forInt(1));
                value = cell.getStringCellValue();
                break;
            case BLANK: // 空值
                break;
            case BOOLEAN: // Boolean
                break;
            case ERROR: // 故障
                break;
            default:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = DateUtil.format(cell.getDateCellValue());
                }
                break;
        }
        return value;
    }

    public List<String> insert(RskValtblAssetSubDto dto) throws ParseException {

        //存放加券过程中，中债要素check不通过的资产信息--徽商代码移植0602
        List<String> errorInfoList=new ArrayList<String>();

        RskValtblPrtBotassetMapDto rskValtblPrtBotassetMapDto = new RskValtblPrtBotassetMapDto();
        InterfPayhsrcsecinfoDto interfPayhsrcsecinfoDto = new InterfPayhsrcsecinfoDto();
        InfChinamutualfunddescriptionDto infChinamutualfunddescriptionDto = new InfChinamutualfunddescriptionDto();
        //股票--徽商代码移植0602
        InterfStockInfoDto interfStockInfoDto=new InterfStockInfoDto();

        //是否自动加券
        boolean isAutoCoupon = false;
        List<String> isAutoCouponBondSecidList = new ArrayList<String>();
        List<String> isAutoCouponFundfInfoWindcodeList = new ArrayList<String>();
        //要加券的股票--徽商代码移植0602
        List<String> isAutoCouponStockfInfoWindcodeList = new ArrayList<String>();

        //查找底层资产映射表
        List<RskValtblPrtBotassetMapDto> botAssetMapList = assetCommonMapper.selectBotAssetMap(dto.getPtlId());
        //查找标的金融产品信息
        List<FinProduct> finProductList = assetCommonMapper.selectAllAsset();
        List<String> subNameList = new ArrayList<String>();
        Map<String,Object> paramMap = new HashMap<String,Object>();
        dto.getSelectedItem().forEach(item->{
            if (StringUtil.isEmpty(item.getSubName())) {
                item.setSubName(StringUtil.EMPTY);
            }
            subNameList.add(item.getSubName());
        });
        paramMap.put("windNameList",subNameList);
        //查找wind债券信息
        List<InterfPayhsrcsecinfoDto>  interfPayhsrcsecinfoList  = commonAutoCouponService.selectWindBondAsset(paramMap);
        //查找wind基金信息
        List<InfChinamutualfunddescriptionDto> infChinamutualfunddescriptionList = commonAutoCouponService.selectWindFundAsset(paramMap);
        //查找wind股票信息--徽商代码移植0602
        List<InterfStockInfoDto> interfStockInfoList=commonAutoCouponService.selectWindStockAsset(paramMap);


        Map<String,String> paraMap =new HashMap<String,String>();
        for(RskValtblAssetSubDto rskValtblAssetSubDtoTemp: dto.getSelectedItem()){
            if(rskValtblAssetSubDtoTemp.getSubCode().length() ==8){
                if(paraMap.get(rskValtblAssetSubDtoTemp.getSubCode()) ==null){
                    paraMap.put(rskValtblAssetSubDtoTemp.getSubCode(),rskValtblAssetSubDtoTemp.getSubName());
                }else{
                    continue;
                }
            }
        }

        for(RskValtblAssetSubDto entity:dto.getSelectedItem()){
            String actMarketCode = null;
            String subCode=entity.getSubCode();
            String subName=entity.getSubName();
            if(StringUtil.equals(entity.getValType().getDesc(), ValType.ASSET.getDesc())
                    && StringUtil.isNotEmpty(entity.getIsMatch()) && StringUtil.equals(entity.getIsMatch(),DictCodeListExt1.是否_否.getValue())){
                continue;
            }
            boolean flag = true;
            //新增底层资产
            boolean isAddSubBotMap = true;
            //更新底层资产
            boolean isUpdateSubBotMap = false;
            //第一步 匹配底层资产映射表
            if(null!=botAssetMapList&&!botAssetMapList.isEmpty()){
                for(RskValtblPrtBotassetMapDto entityRskValtblPrtBotassetMap:botAssetMapList){
                    if(StringUtil.equals(subCode, entityRskValtblPrtBotassetMap.getSubCode())){
                        isAddSubBotMap= false;
                        if(StringUtil.isNotEmpty(entityRskValtblPrtBotassetMap.getFinprodId())){
                            flag = false;
                            entity.setFinprodId(entityRskValtblPrtBotassetMap.getFinprodId());
                            break;
                        }else{
                            isUpdateSubBotMap = true;
                        }
                    }
                }
            }
            //底层资产映射没有匹配上  该资产写入映射表
            if(isAddSubBotMap){
                entity.setWritebotAssetMap(true);
            }
            //底层资产映射匹配上  写入映射表
            if(isUpdateSubBotMap){
                entity.setUpdatebotAssetMap(true);
            }
            // add by QuJianZhong 区分债券交易场所 20200501 start　
            if (subCode.contains("SH") ||
                    subCode.contains("SZ") ||
                    subCode.contains("CY")
                    || subCode.contains("OF")//场外基金
                    || subCode.contains("OTC")//大部分行场外基金后缀都是OTC

            ) {
                if(subCode.contains("OTC")){
                    actMarketCode = subCode.substring(subCode.length() - 3);
                }else{
                    actMarketCode = subCode.substring(subCode.length() - 2);
                }
                actMarketCode = actMarketCode.replace("CY", "IB");
                actMarketCode = actMarketCode.replace("OTC", "OF");
            } else  if(subName.contains("上交所")||
                    subName.contains("深交所")||
                    subName.contains("银行间")){
                if (subName.contains("上交所")) {
                    actMarketCode = "SH";
                }
                if (subName.contains("深交所")) {
                    actMarketCode = "SZ";
                }
                if (subName.contains("银行间")) {
                    actMarketCode = "IB";
                }
            }else {
                //根据估值表底层资产映射获取债券的交易场所，适用于四级科目无法区分
                if(subCode.length() >8){
                    String subNamesub = paraMap.get(subCode.substring(0,8));
                    if(StringUtil.isNotEmpty(subNamesub)){
                        if (subNamesub.contains("上交所")) {
                            actMarketCode = "SH";
                        }
                        if (subNamesub.contains("深交所")) {
                            actMarketCode = "SZ";
                        }
                        if (subNamesub.contains("银行间")) {
                            actMarketCode = "IB";
                        }
                    }
                }
            }

            if (StringUtil.isNotEmpty(actMarketCode)) {
                entity.setMarketId(actMarketCode);
            }
            // add by QuJianZhong 区分债券交易场所 20200501 end
            //第二部  匹配标的金融产品信息
            if(flag&&null!=finProductList&&!finProductList.isEmpty()) {
                for (FinProduct entityFinProduct : finProductList) {
                    //匹配标的金融产品信息的交易场所add by lilan  20200528
                    String tradeMarket=null;
                    if(entityFinProduct.getFinprodId().length()>2){
                        tradeMarket = entityFinProduct.getFinprodId().substring(entityFinProduct.getFinprodId().length() - 2);
                    }

                    if ((StringUtil.equals(subName, entityFinProduct.getFinprodName())
                            || StringUtil.equals(subName, entityFinProduct.getFinprodAbbr()))
                            && (StringUtil.equals(actMarketCode, tradeMarket) ||
                            // add by jiangpeng on 20201221 非债券资产 //add by hxy on 20210803 债券可能是QD债券，去掉非债券资产的判断
                            (StringUtil.isEmpty(actMarketCode) && StringUtil.isNotEmpty(entityFinProduct.getFinprodType2())))
                            && subCode.length() > 8) {/*非四级科目对应行，都不能有资产代码*/
                        flag = false;
                        entity.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                        entity.setFinprodId(entityFinProduct.getFinprodId());
                        break;
                    }
                }
            }
            //第三步  匹配wind资产信息
            if(flag){
                if(StringUtil.equals(entity.getValType().getDesc(), ValType.ASSET.getDesc())){
                    //添加利息匹配1204
                    if(StringUtil.equals(subCode.substring(0, 4),KEY_ZQ_STR)
                            ||StringUtil.equals(subCode.substring(0, 4),KEY_INT_STR)){
                        if(null!=interfPayhsrcsecinfoList&&!interfPayhsrcsecinfoList.isEmpty()){
                            for(InterfPayhsrcsecinfoDto entityInterfPayhsrcsecinfo:interfPayhsrcsecinfoList){
                                if(StringUtil.equals(subName, entityInterfPayhsrcsecinfo.getSecname())
                                        ||StringUtil.equals(subName, entityInterfPayhsrcsecinfo.getSecfullname())){

                                    // add by QuJianZhong 区分债券交易场所 20200501 start
                                    String currentMarketCode = entityInterfPayhsrcsecinfo.getSecid().substring(entityInterfPayhsrcsecinfo.getSecid().indexOf(".") + 1);
                                    if (!StringUtil.equals(actMarketCode, currentMarketCode)) {
                                        // 当前接口表债券市场代码与估值表不同，则继续寻找
                                        continue;
                                    }
                                    // add by QuJianZhong 区分债券交易场所 20200501 end

                                    flag = false;
                                    //避免重复加券
                                    if(StringUtil.equals(subCode.substring(0, 4),KEY_ZQ_STR)){
                                        isAutoCoupon = true;
                                        isAutoCouponBondSecidList.add(entityInterfPayhsrcsecinfo.getSecid());
                                    }
                                    entity.setAssetSource(DictCodeListExt1.估值资产来源_WIND债券.getValue());
                                    entity.setFinprodId(entityInterfPayhsrcsecinfo.getFinprodId());
                                    entity.setFinprodMarketId(entityInterfPayhsrcsecinfo.getSecid());
                                    break;
                                }
                            }
                        }
                    }else if(StringUtil.equals(subCode.substring(0, 4),KEY_JJ_STR)
                            ||StringUtil.equals(subCode.substring(0, 4),KEY_INT_GPJJ_STR)){
                        if(null!=infChinamutualfunddescriptionList&&!infChinamutualfunddescriptionList.isEmpty()){
                            for(InfChinamutualfunddescriptionDto entityinfChinamutualfunddescription:infChinamutualfunddescriptionList){
                                if(StringUtil.equals(subName, entityinfChinamutualfunddescription.getfInfoName())
                                        ||StringUtil.equals(subName, entityinfChinamutualfunddescription.getfInfoFullname())){
                                    flag = false;
                                    //避免重复加券
                                    if(StringUtil.equals(subCode.substring(0, 4),KEY_JJ_STR)){
                                        isAutoCoupon = true;
                                        isAutoCouponFundfInfoWindcodeList.add(entityinfChinamutualfunddescription.getfInfoWindcode());
                                    }
                                    entity.setAssetSource(DictCodeListExt1.估值资产来源_WIND基金.getValue());
                                    //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  start
                                    entity.setFinprodId(entityinfChinamutualfunddescription.getFinprodId());
                                    //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  end
                                    entity.setFinprodMarketId(entityinfChinamutualfunddescription.getfInfoWindcode());
                                    break;
                                }
                            }
                        }
                        //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  start
                    }else if(StringUtil.equals(subCode.substring(0, 4),KEY_GP_STR)
                            ||StringUtil.equals(subCode.substring(0, 4),KEY_INT_GPJJ_STR)){
                        if(null!=interfStockInfoList&&!interfStockInfoList.isEmpty()){
                            for(InterfStockInfoDto entityInterfStockInfoDto:interfStockInfoList){
                                if(StringUtil.equals(subName, entityInterfStockInfoDto.getAssetname())){
                                    flag = false;
                                    //避免重复加券
                                    if(StringUtil.equals(subCode.substring(0, 4),KEY_GP_STR)){
                                        isAutoCoupon = true;
                                        isAutoCouponStockfInfoWindcodeList.add(entityInterfStockInfoDto.getAssetcode());
                                    }
                                    entity.setAssetSource(DictCodeListExt1.估值资产来源_WIND股票.getValue());
                                    entity.setFinprodId(entityInterfStockInfoDto.getFinprodId());
                                    entity.setFinprodMarketId(entityInterfStockInfoDto.getAssetcode());
                                    break;
                                }
                            }
                        }
                    }
                    //徽商代码移植-股票加券、股票债券基金加券数据的有效性check处理合并  201210602  end
                }
            }

        }
        botAssetMapList.clear();

        isAutoCouponBondSecidList = isAutoCouponBondSecidList.stream().distinct().collect(Collectors.toList());
        isAutoCouponFundfInfoWindcodeList = isAutoCouponFundfInfoWindcodeList.stream().distinct().collect(Collectors.toList());
        isAutoCouponStockfInfoWindcodeList = isAutoCouponStockfInfoWindcodeList.stream().distinct().collect(Collectors.toList());

        interfPayhsrcsecinfoDto.setSecidList(isAutoCouponBondSecidList);
        infChinamutualfunddescriptionDto.setfInfoWindcodeList(isAutoCouponFundfInfoWindcodeList);
        interfStockInfoDto.setStockCodeList(isAutoCouponStockfInfoWindcodeList);

        //自动加券
        if(isAutoCoupon){
            //调用债券自动加券方法
            interfPayhsrcsecinfoDto = commonAutoCouponService.autoBondCoupon(interfPayhsrcsecinfoDto);
            if(null != interfPayhsrcsecinfoDto  && null != interfPayhsrcsecinfoDto.getErrorBondList()) {
                errorInfoList.addAll(interfPayhsrcsecinfoDto.getErrorBondList());
            }

            //调用基金自动加券方法
            infChinamutualfunddescriptionDto = commonAutoCouponService.autoFundCoupon(infChinamutualfunddescriptionDto);
            if(null != infChinamutualfunddescriptionDto  && null != infChinamutualfunddescriptionDto.getErrorFundList()) {
                errorInfoList.addAll(infChinamutualfunddescriptionDto.getErrorFundList());
            }

            //调用股票自动加券方法
            interfStockInfoDto=commonAutoCouponService.autoStockCoupon(interfStockInfoDto);
            if(null != interfStockInfoDto  && null != interfStockInfoDto.getErrorStockList()) {
                errorInfoList.addAll(interfStockInfoDto.getErrorStockList());
            }
        }

        for(RskValtblAssetSubDto entity2:dto.getSelectedItem()){
            if(StringUtil.equals(entity2.getValType().getDesc(), ValType.ASSET.getDesc())
                    && StringUtil.isNotEmpty(entity2.getIsMatch()) && StringUtil.equals(entity2.getIsMatch(),DictCodeListExt1.是否_否.getValue())){
                continue;
            }
            //导入方式
            entity2.setImpMode(dto.getImpMode());
            if(entity2.isWritebotAssetMap()||entity2.isUpdatebotAssetMap()){
                if(StringUtil.equals(entity2.getValType().getDesc(), ValType.ASSET.getDesc())){
                    RskValtblPrtBotassetMapDto rskValtblPrtBotassetMapDto2 = new RskValtblPrtBotassetMapDto();
                    if(StringUtil.isNotEmpty(entity2.getSubCode())&&entity2.getSubCode().length()>8){
                        if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_ZQ_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_债券.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_JJ_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_基金.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_GP_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_股票.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_FB_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_非标.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_LLXFB_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_项目类.getValue());
                        }else if(StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_CKL_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_存款类.getValue());
                        }else if (StringUtil.equals(entity2.getSubCode().substring(0, 4), KEY_NHGB_STR)) {
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_逆回购.getValue());
                            String code=StringUtil.substring(entity2.getSubCode(),0,8);
                            // Sonar Fix modify by huwei 220609
                            String finprodType= "";
                            if(StringUtil.equals(code,"12020101") || StringUtil.equals(code,"12020201") || StringUtil.equals(code,"12020301")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue();
                            }else if(StringUtil.equals(code,"12020102") || StringUtil.equals(code,"12020202") || StringUtil.equals(code,"12020302")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue();
                            }
                            String termType = getTermTypeFromStr(entity2.getSubCode(), entity2.getSubName());
                            if(termType != null) {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(termType));
                            } else {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(entity2.getSubCode().substring(8, entity2.getSubCode().length())));
                            }
                            //在fin_trade_product、fin_product_type生成相关数据，风险需要用到
                            commonAutoCouponService.createRepoInfo(entity2);
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_ZHGB_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_正回购.getValue());
                            String code=entity2.getSubCode().substring(0, 8);
                            String finprodType= "";
                            if(StringUtil.equals(code,"22020101") || StringUtil.equals(code,"22020201") || StringUtil.equals(code,"22020301")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue();
                            }else if(StringUtil.equals(code,"22020102") || StringUtil.equals(code,"22020202") || StringUtil.equals(code,"22020302")){
                                finprodType=DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue();
                            }
                            String termType = getTermTypeFromStr(entity2.getSubCode(), entity2.getSubName());
                            if(termType != null) {
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(termType));
                            } else {
                                finprodType = "";
                                entity2.setFinprodId(finprodType.concat(GlobalConstant.UNDER_LINE).concat(entity2.getSubCode().substring(8, entity2.getSubCode().length())));
                            }
                            //在fin_trade_product、fin_product_type生成相关数据，风险需要用到
                            commonAutoCouponService.createRepoInfo(entity2);
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_ZHGI_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_正回购.getValue());
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 6),KEY_NHGI_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_逆回购.getValue());
                        } else if (StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_QQ_STR) || StringUtil.equals(entity2.getSubCode().substring(0, 4),KEY_QH_STR)){
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_衍生品.getValue());
                        }else{
                            rskValtblPrtBotassetMapDto2.setSubClass(DictCodeListExt1.资产科目类型_其他.getValue());
                        }
                        if(StringUtil.equals(entity2.getAssetSource(), DictCodeListExt1.估值资产来源_WIND债券.getValue())){
                            if(null!=interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList()
                                    &&!interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList().isEmpty()){
                                for(BaseFinProductBaseInfoDto baseFinProductBaseInfoDto:interfPayhsrcsecinfoDto.getBaseFinProductBaseInfoList()){
                                    if(StringUtil.equals(entity2.getFinprodMarketId(), baseFinProductBaseInfoDto.getFinProduct().getFinprodMarketId())){
                                        rskValtblPrtBotassetMapDto2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                                    }
                                }
                            }
                        }
                        if(StringUtil.equals(entity2.getAssetSource(), DictCodeListExt1.估值资产来源_WIND基金.getValue())){
                            if(null!=infChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList()
                                    &&!infChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList().isEmpty()){
                                for(BaseFinProductBaseInfoDto baseFinProductBaseInfoDto:infChinamutualfunddescriptionDto.getBaseFinProductBaseInfoList()){
                                    if(StringUtil.equals(entity2.getFinprodMarketId(), baseFinProductBaseInfoDto.getFinProduct().getFinprodMarketId())){
                                        rskValtblPrtBotassetMapDto2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                                    }
                                }
                            }
                        }
                        if(StringUtil.equals(entity2.getAssetSource(), DictCodeListExt1.估值资产来源_WIND股票.getValue())){
                            if(null!=interfStockInfoDto.getBaseFinProductBaseInfoList()
                                    &&!interfStockInfoDto.getBaseFinProductBaseInfoList().isEmpty()){
                                for(BaseFinProductBaseInfoDto baseFinProductBaseInfoDto:interfStockInfoDto.getBaseFinProductBaseInfoList()){
                                    if(StringUtil.equals(entity2.getFinprodMarketId(), baseFinProductBaseInfoDto.getFinProduct().getFinprodMarketId())){
                                        rskValtblPrtBotassetMapDto2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setFinprodId(baseFinProductBaseInfoDto.getFinProduct().getFinprodId());
                                        entity2.setAssetSource(DictCodeListExt1.估值资产来源_系统内.getValue());
                                    }
                                }
                            }
                        }
                        rskValtblPrtBotassetMapDto2.setSubCode(entity2.getSubCode());
                        rskValtblPrtBotassetMapDto2.setSubName(entity2.getSubName());
                        rskValtblPrtBotassetMapDto2.setPtlId(dto.getPtlId());
                        rskValtblPrtBotassetMapDto2.setFinprodId(entity2.getFinprodId());
                        rskValtblPrtBotassetMapDto2.setAssetSource(entity2.getAssetSource());
                        rskValtblPrtBotassetMapDto2.setTblMarketId(entity2.getMarketId());
                        if(entity2.isWritebotAssetMap()){
                            rskValtblPrtBotassetMapDto2.setIsAdd(DictCodeList.是否_是.getValue());
                        }
                        botAssetMapList.add(rskValtblPrtBotassetMapDto2);
                    }
                }
            }
        }

        rskValtblPrtBotassetMapDto.setSelectedItem(botAssetMapList);
        //write映射表
        writebotAssetMap(rskValtblPrtBotassetMapDto);
        //保存估值表

        saveValTbl(dto);

        return errorInfoList;
    }


    /**
     * 写入底层映射表
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    private void writebotAssetMap(RskValtblPrtBotassetMapDto dto) {
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
            for(RskValtblPrtBotassetMapDto entity:dto.getSelectedItem()){
                RskValtblPrtBotassetMap record = new RskValtblPrtBotassetMap();
                record.setPtlId(entity.getPtlId());
                record.setSubCode(entity.getSubCode());
                record.setSubName(entity.getSubName());
                record.setAssetSource(entity.getAssetSource());
                record.setSubClass(entity.getSubClass());
                record.setFinprodId(entity.getFinprodId());
                record.setCreateDept(entity.getCreateDept());
                if(StringUtil.equals(entity.getIsAdd(), BussinessDictCodeList.是否_是.getValue())){
                    rskValtblPrtBotassetMapMapper.insert(record);
                }else{
                    if(StringUtil.isNotEmpty(entity.getFinprodId())){
                        rskValtblPrtBotassetMapMapper.updateByPrimaryKey(record);
                    }
                }
            }
        }
    }

    /**
     * 保存估值表信息
     * <功能详细描述>
     *
     * @param dto
     * @see
     * @since 1.0
     */
    private void saveValTbl(RskValtblAssetSubDto dto) {
        valBatchImportMapper.deleteValTable(dto);
        valBatchImportMapper.deleteSumValTable(dto);
        if(null!=dto.getSelectedItem()&&!dto.getSelectedItem().isEmpty()){
            for(RskValtblAssetSubDto entity:dto.getSelectedItem()){
                if(StringUtil.equals(entity.getValType().getDesc(), ValType.ASSET.getDesc())){
                    RskValtblAssetSub record = new RskValtblAssetSub();
                    BeanUtil.copyPropertiesIgnoreNull(record, entity);
                    record.setPtlId(dto.getPtlId());
                    record.setValDate(dto.getValDate());
                    rskValtblAssetSubMapper.insert(record);
                }else{
                    RskValtblSumAssetSub record = new RskValtblSumAssetSub();
                    BeanUtil.copyPropertiesIgnoreNull(record, entity);
                    record.setPtlId(dto.getPtlId());
                    record.setValDate(dto.getValDate());
                    rskValtblSumAssetSubMapper.insert(record);
                }
            }
        }
    }

    /**
     * 保存委托估值导入记录表
     *
     * @param
     */
    private void saveRskOutValtblImportHis(String name,String path,String ptlId,String portfolioName,Date resValDate,
                                           String importType,String errorCode,Date startDate,Date impdate,String deptCode,String resultInfo) {
        //保存委托估值表接入记录
        RskOutValtblImportHis dto = new RskOutValtblImportHis();
        dto.setFileName(name);
        dto.setFilePath(path);
        dto.setPortfolioId(ptlId);
        dto.setPortfolioName(portfolioName);
        dto.setValdate(resValDate);
        dto.setImpdate(impdate);
        dto.setImportType(importType);
        dto.setErrorCode(errorCode);
        dto.setStartTime(startDate);
        dto.setEndTime(DateUtil.now());
        dto.setResultInfo(resultInfo);
        dto.setTimedTaskStatus("S");
        dto.setCreateDept(deptCode);
        RskOutValtblImportHisKey key=new RskOutValtblImportHisKey();
        key.setFileName(dto.getFileName());
        key.setPortfolioName(portfolioName);
        key.setValdate(dto.getValdate());
        RskOutValtblImportHis rskOutValtblImportHisDto = rskOutValtblImportHisMapper.selectByPrimaryKey(key);
        if(rskOutValtblImportHisDto!=null || StringUtil.isNotEmpty(rskOutValtblImportHisDto)){
            rskOutValtblImportHisMapper.updateByPrimaryKey(dto);
        }else{
            rskOutValtblImportHisMapper.insertSelective(dto);
        }
        valtblImportHisTraceMapper.insert(dto);
        //处理成功的文件入库估值表导入日期记录表
        if (StringUtil.equals(dto.getImportType(),DictCodeListExt1.估值表自动解析处理状态_正常结束.getValue())){
            ValtblImportDateTrace dateDto = new ValtblImportDateTrace();
            dateDto.setUuid(IDGenerator.generate());
            dateDto.setImpdate(dto.getImpdate());
            dateDto.setPortfolioId(dto.getPortfolioId());
            dateDto.setValdate(dto.getValdate());
            dateDto.setCreateDept(deptCode);
            valtblImportDateTraceMapper.insert(dateDto);
        }
    }

    /**
     * 中文文件名编码转换
     * @param fileName
     * @return
     */
    private String getEncodeFileName(String fileName) {
        try {
            return StringUtil.isNotEmpty(fileName)
                    ? (new String(fileName.getBytes(System.getProperty("file.encoding")), "UTF-8")) : fileName;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 估值表导入枚举值转换
     * add by xzp 20200709
     * @param dataList
     */
    public InputStream handleValImportEnmu(List<List<String>> dataList) throws Exception {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream(ValtbImportConstant.VALUATION_TAB_ENUM_RESOURCE_PATH.getDesc());
        if(in == null) {
            logger.info(String.format("估值表导入读取枚举文件失败，未找到路径下的文件，路径位置:%s",
                    ValtbImportConstant.VALUATION_TAB_ENUM_RESOURCE_PATH.getDesc()));
            return in;
        }
        prop.load(in);
        String sszb = prop.getProperty(ValtbImportConstant.VALUATION_TAB_ENUM_SSZB.getCode());
        String zchj = prop.getProperty(ValtbImportConstant.VALUATION_TAB_ENUM_ZCHJ.getCode());
        String fzhj = prop.getProperty(ValtbImportConstant.VALUATION_TAB_ENUM_FZHJ.getCode());
        String jjzcjz = prop.getProperty(ValtbImportConstant.VALUATION_TAB_ENUM_JJZCJZ.getCode());
        String jrdwjz = prop.getProperty(ValtbImportConstant.VALUATION_TAB_ENUM_JRDWJZ.getCode());
        String ljdwjz = prop.getProperty(ValtbImportConstant.VALUATION_TAB_ENUM_LJDWJZ.getCode());
        dataList.forEach(item -> {
            if(!StringUtil.equals("", item.get(0).trim())) {
                // 处理包含如'实收资本:'等这样的场景
                if(sszb.contains(item.get(0).trim()) || ((String)item.get(0)).contains(ValtbImportConstant.VALUATION_TAB_ENUM_SSZB.getDesc())) {
                    // 跳过实收资本金额
                    //修改实收资本不识别问题  mody by jiale 20200910 beign
                    if(ValtbImportConstant.VALUATION_TAB_ENUM_SSZB2.getDesc().contains((String)item.get(0).trim()) || item.get(0).trim().contains(ValtbImportConstant.VALUATION_TAB_ENUM_SSZB2.getDesc())) {
                        item.set(0, ValtbImportConstant.VALUATION_TAB_ENUM_SSZB.getDesc());
                    }
                    //修改实收资本不识别问题  mody by jiale 20200910 end
                } else if(zchj.contains(item.get(0).trim())
                        || ((String)item.get(0)).contains(ValtbImportConstant.VALUATION_TAB_ENUM_ZCHJ.getDesc())) {
                    item.set(0, ValtbImportConstant.VALUATION_TAB_ENUM_ZCHJ.getDesc());
                }else if(fzhj.contains(item.get(0).trim())
                        || ((String)item.get(0)).contains(ValtbImportConstant.VALUATION_TAB_ENUM_FZHJ.getDesc())) {
                    item.set(0, ValtbImportConstant.VALUATION_TAB_ENUM_FZHJ.getDesc());
                }else if(jjzcjz.contains(item.get(0).trim())
                        || ((String)item.get(0)).contains(ValtbImportConstant.VALUATION_TAB_ENUM_JJZCJZ.getDesc())) {
                    item.set(0, ValtbImportConstant.VALUATION_TAB_ENUM_JJZCJZ.getDesc());
                }else if(jrdwjz.contains(item.get(0).trim())
                        || ((String)item.get(0)).contains(ValtbImportConstant.VALUATION_TAB_ENUM_JRDWJZ.getDesc())) {
                    item.set(0, ValtbImportConstant.VALUATION_TAB_ENUM_JRDWJZ.getDesc());
                }else if(ljdwjz.contains(item.get(0).trim())
                        || ((String)item.get(0)).contains(ValtbImportConstant.VALUATION_TAB_ENUM_LJDWJZ.getDesc())) {
                    item.set(0, ValtbImportConstant.VALUATION_TAB_ENUM_LJDWJZ.getDesc());
                }
            }
        });
        return in;
    }

    /**
     * <一句话功能简述> 编辑按钮按下，查询相关信息
     *
     * @param ptlId
     * @return
     * @see
     * @since 1.0
     */
    public RskValRelateImportDto selectInfoById(String ptlId) {
        RskValRelateImportDto dto = new RskValRelateImportDto();
        try {
            RskValtblPrtImpMap rskValtblPrtImpMap =  rskValtblPrtImpMapMapper.selectByPrimaryKey(ptlId);
            if(null!=rskValtblPrtImpMap){
                BeanUtils.copyProperties(dto, rskValtblPrtImpMap);
                RskValtblSumsubMap rskValtblSumsubMap= rskValtblSumsubMapMapper.selectByPrimaryKey(rskValtblPrtImpMap.getSumsubMapId());
                if(null!=rskValtblSumsubMap){
                    dto.setTrimChars(rskValtblSumsubMap.getTrimChars());
                }
            }
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(),e);
        }
        //查询组合名称
        dto.setName(valBatchImportMapper.selectGroupName(ptlId));
        return dto;
    }

    /**
     * 获取期限品种
     *
     * @param subCodeStr
     * @param subNameStr
     * @return String 期限品种
     */
    public String getTermTypeFromStr(String subCodeStr, String subNameStr) {
        Map<String, DataDict> dataDictMap = CacheUtil.getAllDictCode();
        List<DataDictItem> dictItemList = new ArrayList<>();

        if (null != dataDictMap) {
            DataDict dto = dataDictMap.get(DictCode.回购期限品种.getValue());
            if (null != dto) {
                dictItemList = dto.getItems().stream().filter(item ->
                        StringUtil.equals(DictCode.回购期限品种.getValue(), item.getDictCode())).collect(Collectors.toList());
            }
        }

        if (StringUtil.isNotEmpty(subCodeStr)) {
            for (DataDictItem item : dictItemList) {
                if (subCodeStr.contains(item.getItemCode()) || subCodeStr.contains(item.getItemValue())) {
                    return item.getItemCode();
                }
            }
        }

        if (StringUtil.isNotEmpty(subNameStr)) {
            for (DataDictItem item : dictItemList) {
                if (subNameStr.contains(item.getItemCode()) || subNameStr.contains(item.getItemValue())) {
                    return item.getItemCode();
                }
            }
        }

        return null;
    }


    void dealFiles(File file, Date supDate, Map<String, Date> dateMap, String filePath, Boolean flag){
        //资管计划名称_估值日期的规则
        String RSK_FILE_NAME_RULE = CacheUtil.getSysParam("RSK_FILE_NAME_RULE");
        String deptCode = "system";

        for (File valTableFile : file.listFiles()) {
            Date startDate = DateUtil.now();
            Date impDate = supDate;
            String valDate = StringUtil.EMPTY;
            String portfolioName = valTableFile.getName();
            String fileName=getEncodeFileName(valTableFile.getName());

            //处理后的数据不再处理
            if (StringUtil.startsWith(fileName,BAK_START_STR)){
                continue;
            }
            String path = valTableFile.getPath();
            try {
                logger.info("解析委外估值表文件"+fileName);
                if (flag){
                    //获取机构代码
                    deptCode = StringUtil.substring(path,0,StringUtil.lastIndexOf(path, File.separator));
                    deptCode = StringUtil.substring(deptCode,StringUtil.lastIndexOf(deptCode, File.separator)+1,deptCode.length());

                    if (!dateMap.containsKey(deptCode)){
                        logger.info(fileName+",文件名异常组合or日期格式错误。");
                        this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, fileName, iPlatformDate.getSysDate(),
                                DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件名异常组合or日期格式错误.getValue(),startDate,impDate,deptCode,null);
                        continue;
                    }else{
                        impDate =  dateMap.get(deptCode);
                    }
                }else{
                    deptCode = null;
                }
                if(StringUtil.isNotEmpty(RSK_FILE_NAME_RULE)){
                    Pattern pattern = Pattern.compile(RSK_FILE_NAME_RULE);
                    Matcher matcher = pattern.matcher(fileName);
                    if(!matcher.matches()){
                        logger.info(fileName+",文件名异常组合or日期格式错误。");
                        this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, fileName, iPlatformDate.getSysDate(),
                                DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件名异常组合or日期格式错误.getValue(),startDate,impDate,deptCode,null);
                        continue;
                    }
                }
                List<String> valTableFiles = StringUtil.splitToList(".", fileName);
                try {
                    valDate = valTableFiles.get(0);
                    valDate = valDate.substring(valDate.length()-8,valDate.length());
                }catch (Exception e){
                    logger.info(fileName+",文件名异常组合or日期格式错误。");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, fileName, iPlatformDate.getSysDate(),
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件名异常组合or日期格式错误.getValue(),startDate,impDate,deptCode,null);
                    continue;
                }

                if (valTableFiles.size() >= 2 && !StringUtil.equals(BAK_START_STR,valTableFiles.get(0))){
                        portfolioName = fileName.replaceAll(fileName.substring(fileName.lastIndexOf(".")), "").replaceAll("估值表"+valDate,"");
                }else if (StringUtil.equals("BAK",valTableFiles.get(0))){
                    continue;
                }else{
                    logger.info(fileName+",文件名异常组合or日期格式错误。");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, fileName, iPlatformDate.getSysDate(),
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件名异常组合or日期格式错误.getValue(),startDate,impDate,deptCode,null);
                    continue;
                }
                //去掉文件后缀
                valDate = valDate.replaceAll(fileName.substring(fileName.lastIndexOf(".")), "");
                Date resValDate = DateUtil.parse(valDate);
                String days = StringUtil.defaultString(CacheUtil.getSysParam("MAX_SUB_VALDATE"),"16");
                if(DateUtil.compareDate(resValDate,DateUtil.parse(DateUtil.addDay(iPlatformDate.getSysDateStr(),-1*Integer.valueOf(days))))<0){
                    logger.error(fileName+"超过可配置的估值日期");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, portfolioName, resValDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_超过可配置的估值日期.getValue(),startDate,impDate,deptCode,null);
                }
                //1、组合
                Map<String, Object> pageParam = new HashMap<>();
                pageParam.put("portfolioType", DictCodeListExt1.组合类型_委外组合.getValue());
                pageParam.put("portfolioType2", BussinessDictCodeList.组合类型_自主组合.getValue());
                pageParam.put("search", portfolioName);
                List<String> prtfolioIdMap = valBatchImportMapper.selectPortfolioId(pageParam);
                if (prtfolioIdMap == null || prtfolioIdMap.size() == 0) {
                    logger.info(fileName+",未找到委外组合。");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, portfolioName, resValDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_无对应委外组合.getValue(),startDate,impDate,deptCode,null);
                    continue;
                }
                String ptlId = prtfolioIdMap.get(0);
                //1、组合
                pageParam.clear();
                pageParam.put("finprodMarketId", ptlId);
                Map<String,String> FinprodMarketIdMap =  valBatchImportMapper.selectFinProductByFinprodMarketId(pageParam);
                String finprodType = StringUtil.EMPTY; //
                String finprodType2 = StringUtil.EMPTY;
                if (FinprodMarketIdMap != null && FinprodMarketIdMap.size() > 0) {
                    finprodType=FinprodMarketIdMap.get("FINPROD_TYPE");
                    finprodType2=FinprodMarketIdMap.get("FINPROD_TYPE2");
                }
                //2、查询组合对应的映射
                RskValtblPrtImpMap rskValtblPrtImpMap = this.selectInfoById(ptlId);
                RskValTableImportParamDto dto = new RskValTableImportParamDto();
                dto.setPtlId(ptlId);
                dto.setStructMapId(rskValtblPrtImpMap.getStructMapId());
                dto.setSumsubMapId(rskValtblPrtImpMap.getSumsubMapId());
                dto.setAssetSubMapId(rskValtblPrtImpMap.getAssetSubMapId());
                dto.setSubjectCodeCol(StringUtil.numberToString(rskValtblPrtImpMap.getSubjectCodeCol()));
                dto.setStartRow(StringUtil.numberToString(rskValtblPrtImpMap.getStartRow()));
//                    dto.setTrimChars(rskValtblPrtImpMap.getTrimChars());
                dto.setFilterId(rskValtblPrtImpMap.getFilterId());
                dto.setValTableFileName(valTableFile.getPath());
                Map<String, Object> paramMap = new HashMap<String, Object>();
                List<RskValtblAssetSubDto> valList = new LinkedList<RskValtblAssetSubDto>();
                List<RskValtblAssetSubDto> valHand_oneList = new LinkedList<RskValtblAssetSubDto>();
                //指标结构映射
                List<RskValStructureDto> rskValStructureList = valBatchImportMapper.selectForList(dto.getStructMapId());
                if(rskValStructureList==null || rskValStructureList.size()==0){
                    logger.info(fileName+",指标结构映射数据不能为空。");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表指标结构映射异常.getValue(),startDate,impDate,deptCode,null);
                    continue;
                }
                //资产科目映射
                List<RskValtblAssetSubMapDtlDto> rskValAssetCodeList = valBatchImportMapper.selectDtlList(dto.getAssetSubMapId());
                if(rskValAssetCodeList==null || rskValAssetCodeList.size()==0){
                    logger.info(fileName+",资产科目映射数据不能为空。");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表资产科目映射异常.getValue(),startDate,impDate,deptCode,null);
                    continue;
                }
                //汇总科目映射
                List<RskValSumnameDto> rskValSumnameList = valBatchImportMapper.selectSumForList(dto.getSumsubMapId());
                if(rskValSumnameList==null || rskValSumnameList.size()==0){
                    logger.info(fileName+",汇总科目映射数据不能为空。");
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                            DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表汇总科目映射异常.getValue(),startDate,impDate,deptCode,null);
                    continue;
                }
                //目标资产科目
                List<RskValtblSubject> rskValtblSubjectList = valBatchImportMapper.selecctRskValtblSubjectCodeList("01");
                //目标汇总科目
                List<RskValtblSubject> rskValtblSubjectList2 = valBatchImportMapper.selecctRskValtblSubjectCodeList("02");
                Map<String, ColumnMapEntry> vtstructureMap = new HashMap<String, ColumnMapEntry>();
                Map<String, SubCodeEntry> vtaccountMap = new HashMap<String, SubCodeEntry>();
                Map<String, SumNameCodeEntry> vtsumaccountMap = new HashMap<String, SumNameCodeEntry>();
                //构造结构映射map
                for (RskValStructureDto rskValStructureDto : rskValStructureList) {
                    ColumnMapEntry vtstructEntry = new ColumnMapEntry();
                    vtstructEntry.setColIndex(Integer.parseInt(rskValStructureDto.getSrcColIndex()));
                    vtstructEntry.setDataType(rskValStructureDto.getSrcColDataType());
                    vtstructEntry.setValIndex(rskValStructureDto.getTgtIndex());
                    vtstructureMap.put(rskValStructureDto.getSrcColIndex(), vtstructEntry);
                }
                //构造资产科目映射map
                for (RskValtblAssetSubMapDtlDto rskValtblAssetSubMapDtlDto : rskValAssetCodeList) {
                    SubCodeEntry subentry = new SubCodeEntry();
                    subentry.setSrc_code(rskValtblAssetSubMapDtlDto.getSrcCode());
                    subentry.setTgt_code(rskValtblAssetSubMapDtlDto.getTgtCode());
                    subentry.setTgt_name(rskValtblAssetSubMapDtlDto.getTgtName());
                    subentry.setAllowSum(rskValtblAssetSubMapDtlDto.getAllowSum());
                    vtaccountMap.put(rskValtblAssetSubMapDtlDto.getSrcCode(), subentry);
                }
                //构造汇总资产科目映射map
                for (RskValSumnameDto vtsumaccountDto : rskValSumnameList) {
                    SumNameCodeEntry entry = new SumNameCodeEntry();
                    entry.setSumName(vtsumaccountDto.getSumName().trim());
                    entry.setSumCode(vtsumaccountDto.getSumCode().trim());
                    entry.setNextColType(vtsumaccountDto.getNextIndexType());
                    for (RskValtblSubject rskValtblSubject : rskValtblSubjectList2) {
                        if (StringUtil.equals(rskValtblSubject.getSubCode(), vtsumaccountDto.getSumCode())) {
                            entry.setTgt_sumName(rskValtblSubject.getSubName());
                        }
                    }
                    vtsumaccountMap.put(vtsumaccountDto.getSumName(), entry);
                }
                paramMap.put("start_row", dto.getStartRow());
                paramMap.put("subjectCodeCol", dto.getSubjectCodeCol());
                paramMap.put("trimChars", dto.getTrimChars());
                paramMap.put("vtstructureMap", vtstructureMap);
                paramMap.put("vtaccountMap", vtaccountMap);
                paramMap.put("vtsumaccountMap", vtsumaccountMap);
                //处理excel数据
                if (StringUtil.isNotEmpty(dto.getValTableFileName())) {
                    List<List<String>> dataList = Lists.newArrayList();
                    InputStream in = null;
                    try{
                        //框架遇到空行 不读取  估值表比较特殊 资产与汇总科目中间有空格 需要重写
                        dataList = readExcelUtil(dto);
                        //添加枚举值的转换，处理估值表时，会根据对应名称作为key映射到科目代码，如果名称与数据库中的不匹配，则无法获得汇总科目的科目代码
                        in = handleValImportEnmu(dataList);
                    } catch (Exception e) {
                        logger.info(fileName+",文件内容读取异常。");
                        this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                                DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_文件内容读取异常.getValue(),startDate,impDate,deptCode,null);
                        continue;
                    }finally {
                        // 关闭流
                        if(in != null) {
                            try {
                                in.close();
                            } catch (Exception e) {
                                logger.info(String.format("估值表导关闭流失败..., %s", e.getMessage()));
                            }
                        }
                    }
                    for (List<String> list : dataList) {
                        //处理excel------估值表
                        RskValtblAssetSubDto val = handExcelListForVal(list, paramMap);
                        if (val != null) {
                            val.setValDate(dto.getValDate());
                            val.setPtlId(dto.getPtlId());
                            valList.add(val);
                        }
                    }
                    //处理valList---取出映射表配置的科目代码及底层资产
                    List<RskValtblAssetSubDto> handval_List = handValList(valList, vtaccountMap);
                    if (StringUtil.isEmptyList(handval_List) &&  valHand_oneList.size() > 0){
                        logger.info(fileName+",估值表关联导入映射异常。");
                        this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                                DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表关联导入映射异常.getValue(),startDate,impDate,deptCode,null);
                        continue;
                    }
                    //底层---一级汇总
                    valHand_oneList = sum_handValList(handval_List, vtaccountMap, rskValtblSubjectList);
                    for (RskValtblAssetSubDto entity : valHand_oneList){
                        entity.setSubName(toSemiangle(entity.getSubName().replaceAll(" ","")));
                        entity.setCreateDept(deptCode);
                    }
                    valHand_oneList = valHand_oneList.parallelStream().filter(item->StringUtil.isNotEmpty(item.getSubName())).collect(Collectors.toList());
                    BigDecimal securityCost = BigDecimal.ZERO;

                    List<String> errorList = new ArrayList<>();

                    if (valHand_oneList.size() > 0) {
                        //保存
                        RskValtblAssetSubDto rskValtblAssetSubDto = new RskValtblAssetSubDto();
                        rskValtblAssetSubDto.setPtlId(dto.getPtlId());
                        rskValtblAssetSubDto.setValDate(resValDate);
                        rskValtblAssetSubDto.setSelectedItem(valHand_oneList);
                        rskValtblAssetSubDto.setImpMode(DictCodeListExt1.导入方式_自动.getValue());
                        rskValtblAssetSubDto.setCreateDept(deptCode);
//                            this.autoCouponBetter(rskValtblAssetSubDto);
                        errorList = this.insert(rskValtblAssetSubDto);
                    }else{
                        logger.info(fileName+",估值表关联导入映射异常。");
                        this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                                DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_估值表关联导入映射异常.getValue(),startDate,impDate,deptCode,null);
                        continue;
                    }
                    logger.info(fileName+"估值表自动解析处理状态正常结束!!");
                    String runFlag = StringUtil.EMPTY;
                    if (errorList != null && errorList.size()>0){
                        runFlag = DictCodeListExt1.估值表自动解析错误码_自动加券失败.getValue();
                    }
                    StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
                    for (String item : errorList) {
                        sb.append("[");
                        sb.append(item);
                        sb.append("]");
                        sb.append(GlobalConstant.HTML_NEWLINE);
                    }

                    //保存委托估值导入记录表
                    this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), ptlId, portfolioName, resValDate,
                            DictCodeListExt1.估值表自动解析处理状态_正常结束.getValue(),runFlag,startDate,impDate,deptCode,sb.toString());
                    if (flag){
                        ExcelUtilForRisk.fixFileName( filePath+ File.separator +deptCode, fileName,BAK_START_STR+"_"+fileName);
                    } else{
                        ExcelUtilForRisk.fixFileName( filePath, fileName,BAK_START_STR+"_"+fileName);
                    }
                }
            } catch (Exception ex) {
                logger.error("委外估值表接口批处理Error", ex);
                String sOut = "";
                StackTraceElement[] trace = ex.getStackTrace();
                for (StackTraceElement s : trace) {
                    sOut += s + GlobalConstant.HTML_NEWLINE;
                }
                //保存委托估值导入记录表
                this.saveRskOutValtblImportHis(fileName, valTableFile.getPath(), StringUtil.EMPTY, portfolioName, iPlatformDate.getSysDate(),
                        DictCodeListExt1.估值表自动解析处理状态_异常结束.getValue(),DictCodeListExt1.估值表自动解析错误码_其它异常.getValue(),startDate,impDate,deptCode,sOut);
            }
        }

    }
}
