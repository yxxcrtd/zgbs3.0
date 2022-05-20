package com.joyintech.service.basicdata.standardcouponreconversionrate.service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.basicdata.standardcouponreconversionrate.dao.BasicStandardCouponReConversionRateMapper;
import com.joyintech.service.basicdata.standardcouponreconversionrate.dto.StandardCouponReConversionRateDto;
import com.joyintech.tams.common.base.dao.StdBondMoreConvrateInfoMapper;
import com.joyintech.tams.common.base.entity.StdBondMoreConvrateInfo;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.invest.common.util.ExcelUtil;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("backStandardCouponReConversionRateService")
public class StandardCouponReConversionRateService extends BaseService<StandardCouponReConversionRateDto> {

    /**
     * 资金账户管理mapper
     */
    @Autowired
    private BasicStandardCouponReConversionRateMapper basicStandardCouponReConversionRateMapper;

    @Autowired
    private StdBondMoreConvrateInfoMapper stdBondMoreConvrateInfoMapper;

    /**
     * {@linkplain}
     */
    @Autowired
    private IPlatformDate iPlatformDate;
    /**
     * 当在做导入前的check就是判断业务数据是否需要覆盖的时候在webresult的data中塞入这个值可以不会弹框两次
     */
    private static final String NO_INFO_SHOWOUT = "noInfoShowOut";


    /**
     * 使用给定的acctName检索分页数据。返回类型是Service的泛型。
     * @param length
     * @param page
     *
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    public Page<StandardCouponReConversionRateDto> selectForPage(int page, int length, Map<String, Object> pageParam)
            throws Exception {
        return super.queryForPage(page,length,() -> basicStandardCouponReConversionRateMapper.select(pageParam));
    }
    /**
     * 获取券商
     *
     * @param map
     *            检索参数
     * @return SelectOptionDto
     */
    public List<SelectOptionDto> getMstCustomerInfo(Map<String, Object> map) {
        return basicStandardCouponReConversionRateMapper.getMstCustomerInfo(map);
    }

    /**
     * 主键唯一性验证
     *
     *            检索参数
     * @return SelectOptionDto
     */
    public StandardCouponReConversionRateDto selectByPrimaryKey(StandardCouponReConversionRateDto dto){
        return basicStandardCouponReConversionRateMapper.selectByPrimaryKey(dto);
    }

    public List<StandardCouponReConversionRateDto> getList(StandardCouponReConversionRateDto dto){
        return basicStandardCouponReConversionRateMapper.getList(dto);
    }

    public List<WflValidInputParameter> getBusinessData(WflValidInputParameter dto){
        List<String> list = basicStandardCouponReConversionRateMapper.getBusinessData(dto);
        if (CollectionUtils.isNotEmpty(list)) {
            List<WflValidInputParameter> result = new ArrayList<>();
            result.add(new WflValidInputParameter());
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 删除操作
     */
    public int delete(StandardCouponReConversionRateDto entity) {
        int res = 0;
        if (entity.getSelectedItem() != null && entity.getSelectedItem().size() > 0) {
            for (StandardCouponReConversionRateDto unit : entity.getSelectedItem()) {
                res += stdBondMoreConvrateInfoMapper.deleteByPrimaryKey(unit.getSeqNo());
            }
        } else {
            res = stdBondMoreConvrateInfoMapper.deleteByPrimaryKey(entity.getSeqNo());
        }
        return res;
    }

    /**
     * 插入操作
     */
    public int insert(StandardCouponReConversionRateDto dto) {
        dto.setMoreConvrate(new BigDecimal(dto.getTandardRate()).divide(new BigDecimal("100")));
        if(StringUtils.isNotEmpty(dto.getAccountType())&&StringUtils.isEmpty(dto.getAcctNo())){
            dto.setAcctNo(dto.getAccountType());
        }
        return stdBondMoreConvrateInfoMapper.insertSelective(dto);
    }

    /**
     * 更新操作
     */
    public int update(StandardCouponReConversionRateDto dto) {
        dto.setMoreConvrate(new BigDecimal(dto.getTandardRate()).divide(new BigDecimal("100")));
        return stdBondMoreConvrateInfoMapper.updateByPrimaryKeySelective(dto);
    }


    /**
     * 组合资金账户
     * @param map
     * @return
     */
    public List<SelectOptionDto> getPtlSecAcctInfo(Map<String,Object> map){
        List<SelectOptionDto> list = basicStandardCouponReConversionRateMapper.getPtlSecAcctInfo(map);
        return list;
    }

    /**
     * 券商
     * @param map
     * @return
     */
    public List<SelectOptionDto> getFinprodIdInfo(Map<String,Object> map){
        map.put("F01_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        map.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
        map.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
        List<SelectOptionDto> list = basicStandardCouponReConversionRateMapper.getFinprodIdInfo(map);
        return list;
    }

    public List<SelectOptionDto> brokerIdChange(Map<String,Object> map){
        map.put ("broker",map.get ("borker"));
        List<SelectOptionDto> list = basicStandardCouponReConversionRateMapper.brokerIdChange(map);
        return list;
    }

    /**
     * 导入文件之前检查文件 导入的excel是否有重复数据
     *
     * param fileName 导入的文件名名
     * @return 检查结果
     * @see
     * @since 1.0
     */
    public AjaxResult checkConfirmMsgForImportData(List<List<String>> dataList) {
        AjaxResult webResult = new AjaxResult();
        List<ValidMessage> errorList = new ArrayList<>();
        if (!StringUtil.isEmptyList(dataList)) {
            for (int i = 0; i < dataList.size(); i++ ) {
                boolean flag = true;
                List<String> list = dataList.get(i);
                Map<String,Object> param = new HashMap<String,Object>();
                if(list.get(0) == null || list.get(0).trim().equals("")){
                    flag = false;
                }else{
                    param.put("insitituteCode", list.get(0));
                    String result = basicStandardCouponReConversionRateMapper.checkMstCustomerInfo(param);
                    if(StringUtil.isEmpty(result)){
                        flag = false;
                    }
                }
                //资产代码
                if(list.get(1) == null || list.get(1).trim().equals("")){
                    flag = false;
                }else{
                    param.put("finprodId", list.get(1));
                    param.put("F01_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
                    param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
                    String result = basicStandardCouponReConversionRateMapper.checkFinprodIdInfo(param);
                    if(StringUtil.isEmpty(result)){
                        flag = false;
                    }
                }
                if(list.get(4) == null || list.get(4).trim().equals("")){
                    flag = false;
                }else{
                    if (!checkDate(list.get(4))) {
                        flag = false;
                    }else{
                        String platDate = iPlatformDate.getSysDateStr();
                        if(DateUtil.parse(list.get(4)).getTime() < DateUtil.parse(platDate).getTime()){
                            flag = false;
                        }
                    }
                }
                if(flag){
                    StandardCouponReConversionRateDto dto1 = new StandardCouponReConversionRateDto();
                    //券商
                    param.put("insitituteCode", list.get(0));
                    String BrokerIdResult = basicStandardCouponReConversionRateMapper.checkMstCustomerInfo(param);
                    dto1.setBrokerId(BrokerIdResult);
                    //资产代码
                    param.put("finprodId", list.get(1));
                    param.put("F01_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
                    param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
                    String finprodIdResult = basicStandardCouponReConversionRateMapper.checkFinprodIdInfo(param);
                    dto1.setFinprodId(finprodIdResult);
                    //开始日期
                    String startDate = DateUtil.format(DateUtil.parse(list.get(4)),"yyyy-MM-dd");
                    dto1.setStartDate(startDate);
                    StandardCouponReConversionRateDto checkdto = basicStandardCouponReConversionRateMapper.selectByPrimaryKey(dto1);
                    if(checkdto != null){
                        ValidMessage validMessage = new ValidMessage((i+2)+"记录已存在，是否覆盖！");
                        errorList.add(validMessage);
                    }
                }
            }

        }
        if(errorList.size() == 0){
            webResult.setData(NO_INFO_SHOWOUT);
        }else{
            webResult.setErrorList(errorList);
        }
        return webResult;
    }

    /**
     * 导入文件
     * <p>
     * 解析excel文件并进行验证
     * </p>
     *

     * @return 导入结果
     */
    public List<ExcelValidDto> importData(List<List<String>> dataList, List<List<String>> tempList, String userName, String orgCode, String deptCode) {
        if (!StringUtil.isEmptyList(dataList)) {
            List<String> headList = new ArrayList<String>();
            headList.add("券商");
            headList.add("资产代码");
            headList.add("资产名称");
            headList.add("标准券再折算率%");
            headList.add("开始使用日期");
            headList.add("结束使用日期");
            List<ExcelValidDto> tempCheckResult = ExcelUtil.checkExcelTemp(tempList, headList);
            if (!StringUtil.isEmptyList(tempCheckResult)) {
                return tempCheckResult;
            }
            if (null != dataList) {
                List<StdBondMoreConvrateInfo> entities = new ArrayList<>(dataList.size());
                List<ExcelValidDto> checkResult= convertToEntity(dataList, entities,userName,deptCode);
                if(checkResult.isEmpty()) {
                    for(StdBondMoreConvrateInfo dto : entities){
                        //校验主键存在则更新
                        StandardCouponReConversionRateDto dto1 = new StandardCouponReConversionRateDto();
                        dto1.setBrokerId(dto.getBrokerId());
                        dto1.setFinprodId(dto.getFinprodId());
                        dto1.setStartDate(dto.getStartDate());
                        StandardCouponReConversionRateDto checkdto = basicStandardCouponReConversionRateMapper.selectByPrimaryKey(dto1);
                        if(checkdto != null && StringUtil.isNotEmpty(checkdto.getSeqNo())){
                            dto.setSeqNo(checkdto.getSeqNo());
                            stdBondMoreConvrateInfoMapper.updateByPrimaryKeySelective(dto);
                        }else{
                            stdBondMoreConvrateInfoMapper.insert(dto);
                        }
                    }
                }
                return checkResult;
            }
        }else{
            List<ExcelValidDto> checkResult = new ArrayList<>();
            ExcelValidDto excelValidDto = new ExcelValidDto();
            String errorMsg = "模板为空，请输入数据！";
            excelValidDto.setErrorInfo(errorMsg);
            checkResult.add(excelValidDto);
            return checkResult;
        }
        return null;
    }

    /**
     * excel数据转为java对象
     * <p> 将excel导入的数据，按行转为ProRegistCodeManage001Dto  FinProductValuation对象，同时验证各字段值</p>
     *
     * @param dataList excel导入的数据
     * @param entities 转化后的列表
     * @return 验证信息
     */
    private List<ExcelValidDto> convertToEntity(List<List<String>> dataList, List<StdBondMoreConvrateInfo> entities, String userName, String deptCode) {
        List<ExcelValidDto> checkResult = new ArrayList<>();
        Date nowDate = new Date();
        List<String> checkTemp = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++ ) {
            //是否正常标志
            boolean flag = true;
            List<String> list = dataList.get(i);
            // 验证消息
            List<String> message = new ArrayList<>();
            Map<String,Object> param = new HashMap<String,Object>();
            //券商
            if(list.get(0) == null || list.get(0).trim().equals("")){
                flag = false;
                message.add("券商不能为空");
            }else{
                param.put("insitituteCode", list.get(0));
                String result = basicStandardCouponReConversionRateMapper.checkMstCustomerInfo(param);
                if(StringUtil.isEmpty(result)){
                    flag = false;
                    message.add("券商不存在");
                } else {
                    param.put("brokerId", result);
                    String accountType = basicStandardCouponReConversionRateMapper.checkAccountTypeInfo(param);
                    if(StringUtil.isNotEmpty(accountType)){
                        flag = false;
                        message.add("已维护整体再折算率，无法按单个资产维护");
                    }
                }
            }
            //资产代码
            if(list.get(1) == null || list.get(1).trim().equals("")){
                flag = false;
                message.add("资产代码不能为空");
            }else{
                param.put("finprodId", list.get(1));
                param.put("F01_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
                param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
                String result = basicStandardCouponReConversionRateMapper.checkFinprodIdInfo(param);
                if(StringUtil.isEmpty(result) && !StringUtil.equals(list.get(1),"所有")){
                    flag = false;
                    message.add("资产代码有误");
                }


            }
            if(list.get(3) == null || list.get(3).trim().equals("")){
                flag = false;
                message.add("标准券再折算率%不能为空");
            }else{
                if(!checkFigure(list.get(3))){
                    flag = false;
                    message.add("标准券再折算率%不是数字");
                }else{
                    BigDecimal tandardRate = new BigDecimal (list.get(3));
                    BigDecimal tandardRate0 = new BigDecimal ("0");
                    BigDecimal tandardRate1 = new BigDecimal ("100");
                    if(tandardRate.compareTo (tandardRate0) < 0 || tandardRate.compareTo (tandardRate1) > 0){
                        flag = false;
                        message.add("输入的标准券再折算率不在0和100之间");
                    }
                }
            }

            if(list.get(4) == null || list.get(4).trim().equals("")){
                flag = false;
                message.add("开始使用日期不能为空");
            }else{
                if (!checkDate(list.get(4))) {
                    flag = false;
                    message.add("开始使用日期格式不对");
                }else{
                    String platDate = iPlatformDate.getSysDateStr();
                    if(DateUtil.parse(list.get(4)).getTime() < DateUtil.parse(platDate).getTime()){
                        flag = false;
                        message.add("开始使用日期小于平台日");
                    }
                }
            }
           if(list.get(5) != null && !list.get(5).trim().equals("")){
                if(!checkDate(list.get(5))){
                    flag = false;
                    message.add("结束使用日期格式不对");
                }else{
                    if(StringUtil.isNotEmpty(list.get(5)) && StringUtil.isNotEmpty(list.get(4))){
                        if(DateUtil.parse(list.get(5)).getTime() <= DateUtil.parse(list.get(4)).getTime()){
                            flag = false;
                            message.add("结束使用日期小于等于开始使用日期");
                        }
                    }
                }
            }
           //过滤重复数据
            if(flag){
                String zTemp = list.get(0)+list.get(1)+list.get(4);
                for(int j = 0 ; j < checkTemp.size() ; j++){
                    if(StringUtil.equals(zTemp,checkTemp.get(j))){
                        message.add("文件内部数据重复");
                        flag = false;
                        break;
                    }
                }
                checkTemp.add(zTemp);
            }
            if(flag){
                StdBondMoreConvrateInfo entity = new StdBondMoreConvrateInfo();
                //序列号
                entity.setSeqNo(IDGenerator.generate(32));
                //券商
                param.put("insitituteCode", list.get(0));
                String BrokerIdResult = basicStandardCouponReConversionRateMapper.checkMstCustomerInfo(param);
                entity.setBrokerId(BrokerIdResult);
                //资产代码
                if(StringUtil.equals(list.get(1),"所有")){
                    entity.setFinprodId("BROKER");
                }else{
                    param.put("finprodId", list.get(1));
                    param.put("F01_TG", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
                    param.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
                    param.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
                    String finprodIdResult = basicStandardCouponReConversionRateMapper.checkFinprodIdInfo(param);
                    entity.setFinprodId(finprodIdResult);
                }
                //再折算率
                entity.setMoreConvrate(new BigDecimal(list.get(3)).divide(new BigDecimal("100")));
                //开始日期
                String startDate = DateUtil.format(DateUtil.parse(list.get(4)),"yyyy-MM-dd");
                entity.setStartDate(startDate);
                //结束日期
                if(StringUtil.isNotEmpty(list.get(5))){
                    String endDate = DateUtil.format(DateUtil.parse(list.get(5)),"yyyy-MM-dd");
                    entity.setEndDate(endDate);
                }
                entity.setCreateUser(userName);
                entity.setCreateDept(deptCode);
                Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
                entity.setUpdateUser(userName);
                entity.setUpdateTime(nowDate);
                entities.add(entity);
            }

            if (CollectionUtils.isNotEmpty(message)) {
                checkResult.add(new ExcelValidDto(list.get(0), String.valueOf(i+2),
                        StringUtil.joinString(message, GlobalConstant.SEMICOLON)));
            }
        }
        return checkResult;
    }

    /**
     * 日期格式校验
     *
     * @param
     * @return 返回boolean
     */
    private boolean checkDate(String date) {
        boolean flag = true;
        if(!DateUtil.isDateStr(date, DateUtil.DEFAULT_OTHER_FORMAT)){
            flag = false;
        }else{
            date = date.replace("-", "").replace("/", "");
            //年
            int year = Integer.valueOf(date.substring(0, 4));
            //月
            int month = Integer.valueOf(date.substring(4, 6));
            //日
            int day = Integer.valueOf(date.substring(6));
            if(month < 1 || month > 12){
                flag = false;
            }else{
                if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
                    if(day < 1 || day > 31){
                        flag = false;
                    }
                }else if(month == 4 || month == 6 || month == 9 || month == 11){
                    if(day < 1 || day > 30){
                        flag = false;
                    }
                }else if(month == 2){
                    if((year % 4 == 0 && year % 100 != 0) ||year % 400 == 0){
                        if(day < 1 || day > 29){
                            flag = false;
                        }
                    }else{
                        if(day < 1 || day > 28){
                            flag = false;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 判断是否为数字
     *
     * @param
     * @return 返回boolean
     */
    private boolean checkFigure(String figure) {
        try{
            new BigDecimal(figure);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
