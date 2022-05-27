package com.joyintech.service.basicdata.standardcouponreconversionrate.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.basicdata.standardcouponreconversionrate.dto.StandardCouponReConversionRateDto;
import com.joyintech.service.basicdata.standardcouponreconversionrate.service.StandardCouponReConversionRateService;
import com.joyintech.tams.component.base.core.dto.WflValidInputParameter;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/StandardCouponReConversionRate")
public class StandardCouponReConversionRateController {
    /**
     * {@Autowired}
     */
    @Autowired
    private StandardCouponReConversionRateService standardCouponReConversionRateService;


    /**
     * 使用给定的acctName检索分页数据。返回类型是Service的泛型。
     *
     * @param pageParam 查询方法
     * @return Page类
     * @throws Exception 异常信息
     */
    @PostMapping("/selectForPage/{page}/{length}")
    public Page<StandardCouponReConversionRateDto> selectForPage(@RequestBody Map<String, Object> pageParam,
                                                                 @PathVariable("page") int page, @PathVariable("length") int length)
            throws Exception {
        return standardCouponReConversionRateService.selectForPage(page,length,pageParam);
    }

    /**
     * 获取券商
     *
     * @param map
     *            检索参数
     * @return SelectOptionDto
     */
    @PostMapping("/getMstCustomerInfo")
    public List<SelectOptionDto> getMstCustomerInfo(@RequestBody Map<String, Object> map) {
        return standardCouponReConversionRateService.getMstCustomerInfo(map);
    }

    /**
     * 详情、修改、查看、唯一性验证
     *
     * @param dto
     * @return
     */
    @PostMapping("/selectByPrimaryKey")
    public StandardCouponReConversionRateDto selectByPrimaryKey(@RequestBody StandardCouponReConversionRateDto dto){
        return standardCouponReConversionRateService.selectByPrimaryKey(dto);
    }

    /**
     * 详情、修改、查看、唯一性验证
     *
     * @param dto
     * @return
     */
    @PostMapping("/getList")
    public List<StandardCouponReConversionRateDto> getList(@RequestBody StandardCouponReConversionRateDto dto){
        return standardCouponReConversionRateService.getList(dto);
    }

    /**
     * 详情、修改、查看、唯一性验证
     *
     * @param dto
     * @return
     */
    @PostMapping("/getBokerList")
    public List<StandardCouponReConversionRateDto> getBokerList(@RequestBody StandardCouponReConversionRateDto dto){
        return standardCouponReConversionRateService.getBokerList(dto);
    }

    /**
     * 详情、修改、查看、唯一性验证
     *
     * @param dto
     * @return
     */
    @PostMapping("/getBusinessData")
    public List<WflValidInputParameter> getBusinessData(@RequestBody WflValidInputParameter dto){
        return standardCouponReConversionRateService.getBusinessData(dto);
    }

    /**
     * 删除
     *
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("delete")
    public int delete(@RequestBody StandardCouponReConversionRateDto dto) throws Exception {
        return standardCouponReConversionRateService.delete(dto);
    }

    /**
     * 插入
     *
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("insert")
    public int insert(@RequestBody StandardCouponReConversionRateDto dto) throws Exception {
        return standardCouponReConversionRateService.insert(dto);
    }

    /**
     * 更新
     *
     * @param dto
     *            页面选中的数据
     * @return 结果
     * @throws Exception
     *             异常
     */
    @Transactional
    @DTXTransaction
    @PostMapping("update")
    public int update(@RequestBody StandardCouponReConversionRateDto dto) throws Exception {
        return standardCouponReConversionRateService.update(dto);
    }


    /**
     * 获取组合资金账户
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getPtlSecAcctInfo")
    public List<SelectOptionDto> getPtlSecAcctInfo(@RequestBody Map<String, Object> map) throws Exception {
        return standardCouponReConversionRateService.getPtlSecAcctInfo(map);
    }

    /**
     * 获取组合资金账户
     * @param map
     * @return
     * @throws Exception
     */
    @PostMapping("getFinprodIdInfo")
    public List<SelectOptionDto> getFinprodIdInfo(@RequestBody Map<String, Object> map) throws Exception {
        return standardCouponReConversionRateService.getFinprodIdInfo(map);
    }

    @PostMapping("brokerIdChange")
    public List<SelectOptionDto> brokerIdChange(@RequestBody Map<String, Object> map){
        return standardCouponReConversionRateService.brokerIdChange(map);
    }

    /**
     * 导入数据文件之前检查数据
     *
     * @param param 导入的文件名
     * @return 处理结果
     */
    @SuppressWarnings("unchecked")
    @PostMapping("checkConfirmMsgForImportData")
    public AjaxResult checkConfirmMsgForImportData(@RequestBody Map<String, Object> param)  {
        List<List<String>> dataList = (List<List<String>>) param.get("dataList");
        String orgCode=param.get("orgCode")==null?null:param.get("orgCode").toString();
        String deptCode=param.get("deptCode")==null?null:param.get("deptCode").toString();
        return standardCouponReConversionRateService.checkConfirmMsgForImportData(dataList);
    }

    /**
     * 导入数据文件
     *
     * @param param 导入的文件名
     * @return 导入结果，包含验证内容，如果验证失败，返回message
     */
    @SuppressWarnings("unchecked")
    @PostMapping("importData")
    public List<ExcelValidDto> importData(@RequestBody Map<String, Object> param) {
        List<List<String>> dataList = (List<List<String>>) param.get("dataList");
        List<List<String>> tempList = (List<List<String>>) param.get("tempList");
        String userName=param.get("userName")==null?null:param.get("userName").toString();
        String orgCode=param.get("orgCode")==null?null:param.get("orgCode").toString();
        String deptCode=param.get("deptCode")==null?null:param.get("deptCode").toString();
        return this.standardCouponReConversionRateService.importData(dataList,tempList,userName,orgCode,deptCode);
    }
}
