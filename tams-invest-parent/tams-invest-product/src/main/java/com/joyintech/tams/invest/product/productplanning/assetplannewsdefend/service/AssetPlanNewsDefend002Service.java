package com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.AssetClass;
import com.joyintech.tams.common.base.entity.AssetConfigMonDetail;
import com.joyintech.tams.common.base.entity.AssetPlanDetail;
import com.joyintech.tams.common.base.entity.AssetPlanInfo;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsDefend;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsDefend002Dto;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsForm;
import com.joyintech.tams.invest.product.productplanning.common.dto.BondRepaymentAmountDto;
import com.joyintech.tams.invest.product.productplanning.common.dto.HoldPositionDto;
import com.joyintech.tams.invest.product.productplanning.common.dto.TacticsOptimizeDto;
import com.joyintech.tams.invest.product.productplanning.common.service.PlanningCommonService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 资产规划信息维护
 * @author guyunfei
 * 20191226
 */
@Service
public class AssetPlanNewsDefend002Service extends BaseService<AssetPlanNewsDefend002Dto>  implements BaseProcessService<AssetPlanNewsDefend002Dto> {

    @Autowired
    private PlanningCommonService planningCommonService;

    @Override
    public void doComplete(AssetPlanNewsDefend002Dto dto, OperationType operationType) {

        if(!operationType.equals(OperationType.DELETE)){
            //新增或修改
            AssetPlanNewsForm form = new AssetPlanNewsForm();

            AssetPlanInfo assetPlanInfo = new AssetPlanInfo();
            BeanUtils.copyProperties(dto,assetPlanInfo);
            assetPlanInfo.setTargetScale(dto.getAssetTargetScale());

            List<AssetPlanNewsDefend> assetPlanNewsDefendList = dto.getAssetPlanNewsDefendList();
            List<AssetPlanDetail> assetPlanDetailList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(assetPlanNewsDefendList)){
                //资产规划信息明细对象造成
                assetPlanNewsDefendList.forEach(e->{
                    AssetPlanDetail detail = new AssetPlanDetail();
                    BeanUtils.copyProperties(e,detail);
                    detail.setAssetPlanCode(dto.getAssetPlanCode());
                    assetPlanDetailList.add(detail);
                });
            }

            form.setOperateType(operationType.toString());
            form.setAssetPlanInfo(assetPlanInfo);
            form.setAssetPlanDetailList(assetPlanDetailList);
            restTemplate.postForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/AssetPlanNewsDefend/updateOrAdd",
                    form,
                    Integer.class
            );
        }else{
            //删除
            String assetPlanCode = dto.getAssetPlanCode();
            Map<String,Object> params = new HashMap<>();
            params.put("assetPlanCode",assetPlanCode);
            restTemplate.postForObject(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/AssetPlanNewsDefend/deleteByPrimary",
                    params,
                    Integer.class
            );
        }
    }

    /**
     * 查询详情
     * @param assetPlanCode
     * @return
     */
    public AssetPlanNewsDefend002Dto queryData(String assetPlanCode){

        AssetPlanNewsDefend002Dto result = new AssetPlanNewsDefend002Dto();

        AssetPlanNewsForm form = restTemplate.getForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/AssetPlanNewsDefend/queryData?assetPlanCode=" + assetPlanCode,
                AssetPlanNewsForm.class
        );
        //数据结构转化
        if(form != null && form.getAssetPlanInfo() != null){
            AssetPlanInfo info = form.getAssetPlanInfo();
            BeanUtils.copyProperties(info,result);
            result.setAssetTargetScale(info.getTargetScale());
        }
        if(form != null && form.getAssetPlanDetailList() != null){
            List<AssetPlanDetail> detailData = form.getAssetPlanDetailList();
            List<AssetPlanNewsDefend> defends = new ArrayList<>();
            detailData.forEach(e->{
                AssetPlanNewsDefend defend = new AssetPlanNewsDefend();
                BeanUtils.copyProperties(e,defend);
                defends.add(defend);
            });
            result.setAssetPlanNewsDefendList(defends);
        }
        return result;
    }

    /**
     * 根据画面输入元素 获取 资产规划系列列表
     * @param params
     * @return
     */
    public List<AssetPlanNewsDefend> getPlans(Map<String,Object> params){

        String pageType = (String) params.get("pageType");
        String assetPlanCode = (String) params.get("assetPlanCode");
        String isInit = (String) params.get("isInit");
        String workflowId = (String) params.get("workflowId");
        //pageType == 2  查看
        if(StringUtil.equals(pageType,"2") && StringUtil.isNotEmpty(assetPlanCode)){
            if(StringUtil.isNotEmpty(workflowId)){
                //待办事项页面的查看
                AssetPlanNewsDefend002Dto dto = this.getBussinessEntityByWorkflowId(workflowId);
                if(dto != null && CollectionUtils.isNotEmpty(dto.getAssetPlanNewsDefendList())){
                    return dto.getAssetPlanNewsDefendList();
                }else {
                    return new ArrayList<>();
                }
            }else{
                //不在流程中数据的查看
                AssetPlanNewsDefend002Dto dto = queryData(assetPlanCode);
                if(dto != null && CollectionUtils.isNotEmpty(dto.getAssetPlanNewsDefendList())){
                    return dto.getAssetPlanNewsDefendList();
                }else{
                    return new ArrayList<>();
                }
            }
        }
        //isInit == YES 表示初始化进入页面时调用的该方法； isInit == NO 表示改动页面数据触发change事件调用的该方法
        //pageType == 1  表示修改
        if(StringUtil.equals(pageType,"1") && StringUtil.isNotEmpty(assetPlanCode) && StringUtil.equals(isInit,"YES")){
            //退回时的查看
            if(StringUtil.isNotEmpty(workflowId)){
                AssetPlanNewsDefend002Dto dto = this.getBussinessEntityByWorkflowId(workflowId);
                if(dto != null && CollectionUtils.isNotEmpty(dto.getAssetPlanNewsDefendList())){
                    return dto.getAssetPlanNewsDefendList();
                }else {
                    return new ArrayList<>();
                }
            }
            //修改 初始化
            AssetPlanNewsDefend002Dto dto = queryData(assetPlanCode);
            if(dto != null && CollectionUtils.isNotEmpty(dto.getAssetPlanNewsDefendList())){
                return dto.getAssetPlanNewsDefendList();
            }else{
                return new ArrayList<>();
            }
        }

        if(StringUtil.isEmpty(params.get("vdate").toString())
                || StringUtil.isEmpty(params.get("edate").toString())
                || StringUtil.isEmpty(params.get("benchDate").toString())
                || StringUtil.isEmpty(params.get("assetTargetScale").toString())){
            return new ArrayList<>();
        }

        List<AssetPlanNewsDefend> resultList = new ArrayList<>();

        //画面.目标资产规模
        BigDecimal assetTargetScale = BigDecimal.ZERO;
        if(StringUtil.isNotEmpty(params.get("assetTargetScale"))){
            assetTargetScale = new BigDecimal((String) params.get("assetTargetScale"));
        }
        //估值列表
        List<HoldPositionDto> holdPositionDtos = planningCommonService.queryHoldPosition(params);
        //资产分类结果
        List<AssetClass> assetClasses = planningCommonService.queryAssetType();
        //战术配置优化列表
        List<TacticsOptimizeDto> tacticsOptimizeDtos = planningCommonService.queryTacticsOptimize(params);
        //资产大类的排序升序
        List<? extends DataDictItem> dataDictItem = SysDataUtil.getDataDictItem(DictCode.资产大类.getValue());
        if(CollectionUtils.isNotEmpty(dataDictItem)){
            for (DataDictItem item : dataDictItem){

                AssetPlanNewsDefend defend = new AssetPlanNewsDefend();

                //规划初始值
                BigDecimal planInitAmt = BigDecimal.ZERO;
                //规划目标值
                BigDecimal planTargetAmt = BigDecimal.ZERO;
                //规划变动值
                BigDecimal planChangeAmt = BigDecimal.ZERO;

                //资产二级分类列表
                List<String> secondTypes = new ArrayList<>();
                //资产三级分类列表
                List<String> thirdTypes = new ArrayList<>();
                //资产四级分类列表
                List<String> forthTypes = new ArrayList<>();
                //资产五级分类列表
                List<String> fifthTypes = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(assetClasses)){
                    for(AssetClass asset : assetClasses){
                        if(StringUtil.equals(asset.getAssetType(),item.getItemCode())){
                            secondTypes.add(asset.getCustomAssetTypeTwo());
                            thirdTypes.add(asset.getCustomAssetTypeThree());
                            forthTypes.add(asset.getCustomAssetTypeFour());
                            fifthTypes.add(asset.getCustomAssetTypeFive());
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(holdPositionDtos)){
                    //SUM（估值列表.持仓数量）
                    holdPositionDtos = holdPositionDtos.stream().filter(e->{
                        if(e.getShartAmt() == null){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    //BigDecimal shartAmtSum = holdPositionDtos.stream().map(HoldPositionDto::getShartAmt).reduce(BigDecimal::add).get();

                    BigDecimal shartAmtSum = BigDecimal.ZERO;

                    for(HoldPositionDto dto : holdPositionDtos){
                        if(secondTypes.contains(dto.getType9())
                            && thirdTypes.contains(dto.getType10())
                            && forthTypes.contains(dto.getType11())
                            && fifthTypes.contains(dto.getType12())){
                            shartAmtSum = ArithmeticUtil.doAdd(shartAmtSum,dto.getShartAmt());
                        }
                    }
                    // 资产分类明细 = 数据字典.资产大类.债券类—信用债 or 债券类—利率债
                    if(StringUtil.equals(item.getItemCode(),DictCodeListExt3.资产一级分类_债券类信用债.getValue())
                            || StringUtil.equals(item.getItemCode(),DictCodeListExt3.资产一级分类_债券类利率债.getValue())){
                        //规划初始值 = round（SUM（估值列表.持仓数量）*100/10000，2）
                        planInitAmt = ArithmeticUtil.round(ArithmeticUtil.divide(shartAmtSum,100),2,RoundingMode.HALF_UP);
                    }else {
                        //规划初始值 = round（SUM（估值列表.持仓数量）/10000，2）
                        planInitAmt = ArithmeticUtil.round(ArithmeticUtil.divide(shartAmtSum,10000),2,RoundingMode.HALF_UP);
                    }

                }
                if(CollectionUtils.isNotEmpty(tacticsOptimizeDtos)){

                    for(TacticsOptimizeDto dto : tacticsOptimizeDtos){
                        if(StringUtil.equals(item.getItemCode(),dto.getAssetType())){
                            //规划目标值 = round（画面.目标资产规模*战术配置明细.修正后占比，2）
                            planTargetAmt = ArithmeticUtil.round(ArithmeticUtil.multiply(assetTargetScale,dto.getCorRatio()),2,RoundingMode.HALF_UP);
                        }
                    }
                }
                //到期金额
                BigDecimal expireAmt = BigDecimal.ZERO;

                // 资产分类明细 = 数据字典.资产大类.债券类—信用债 or 债券类—利率债
                if(StringUtil.equals(item.getItemCode(),DictCodeListExt3.资产一级分类_债券类信用债.getValue())
                        || StringUtil.equals(item.getItemCode(),DictCodeListExt3.资产一级分类_债券类利率债.getValue())){
                    if(secondTypes.size() >0 && thirdTypes.size() > 0 && forthTypes.size() > 0 && fifthTypes.size() > 0){
                        params.put("secondTypes",secondTypes);
                        params.put("thirdTypes",thirdTypes);
                        params.put("forthTypes",forthTypes);
                        params.put("fifthTypes",fifthTypes);
                        //调用【债券还本到期金额】
                        List<BondRepaymentAmountDto> bondRepaymentAmount = planningCommonService.getBondRepaymentAmount(params);
                        if(CollectionUtils.isNotEmpty(bondRepaymentAmount)){
                            expireAmt = bondRepaymentAmount.get(0).getAmount();
                        }
                    }

                }else {
                    // 调用【资产还本到期金额】

                    //画面基准日
                    String benchDate = params.get("benchDate").toString();
                    int year = DateUtil.getYear(benchDate);
                    String lastDay = year + "-12-31";
                    //params.put("vdate",benchDate);
                    //params.put("edate",lastDay);

                    if(secondTypes.size() >0 && thirdTypes.size() > 0 && forthTypes.size() > 0 && fifthTypes.size() > 0){
                        //调用方法参数构成
                        Map<String,Object> paramMap = new HashMap<>();
                        paramMap.put("cdate",benchDate);
                        paramMap.put("type9",secondTypes);
                        paramMap.put("type10",thirdTypes);
                        paramMap.put("type11",forthTypes);
                        paramMap.put("type12",fifthTypes);
                        paramMap.put("benchDate",benchDate);
                        paramMap.put("benchDateEnd",lastDay);
                        List<AssetConfigMonDetail> curEndAmt = planningCommonService.getCurEndAmt(paramMap);
                        if(CollectionUtils.isNotEmpty(curEndAmt)){
                            expireAmt = curEndAmt.stream().map(AssetConfigMonDetail::getCurEndAmt).reduce(BigDecimal::add).get();
                        }
                    }

                }
                //规划变动值=round（规划目标值-规划初始值+到期金额/10000，2）
                planChangeAmt = ArithmeticUtil.round(ArithmeticUtil.doAdd(ArithmeticUtil.subtract(planTargetAmt,planInitAmt),ArithmeticUtil.divide(expireAmt,10000)),2,RoundingMode.HALF_UP);

                defend.setAssetType(item.getItemCode());
                defend.setPlanInitAmt(planInitAmt);
                defend.setPlanChangeAmt(planChangeAmt);
                defend.setPlanTargetAmt(planTargetAmt);
                resultList.add(defend);
            }
            // ※最后一个资产大类的规划目标值 = 画面.目标资产规模 - SUM(上述.规划目标值)
            BigDecimal targetSum = BigDecimal.ZERO;
            for(int i = 0; i < resultList.size() -1; i++){
                targetSum = ArithmeticUtil.doAdd(resultList.get(i).getPlanTargetAmt(),targetSum);
            }
            AssetPlanNewsDefend defend = resultList.get(resultList.size() - 1);
            defend.setPlanTargetAmt(ArithmeticUtil.subtract(assetTargetScale,targetSum));
            resultList.set(resultList.size()-1,defend);
        }
        return resultList;
    }

    /**
     * 如果开始日、结束日和Db中资产规划信息维护表相同，查询数据库中已有改记录的条数
     * @param params
     * @return
     */
    public Integer getConflictCount(Map<String,Object> params){
        return restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/AssetPlanNewsDefend/getConflictCount",
                params,
                Integer.class
        );
    }

    /**
     * 查询规划计划（规划开始日、规划结束日）区间重叠的数量
     * @param params
     * @return
     */
    public Integer getOverlapCount(Map<String,Object> params){
        return restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/AssetPlanNewsDefend/getOverlapCount",
                params,
                Integer.class
        );
    }

    /**
     * 用于新增、修改check
     * @param params
     * @return
     */
    public List<AssetPlanNewsDefend> queryProductTarget(Map<String,Object> params){
        return restTemplate.postForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/AssetPlanNewsDefend/queryProductTarget",
                params,
                AssetPlanNewsDefend.class
        );
    }
}
