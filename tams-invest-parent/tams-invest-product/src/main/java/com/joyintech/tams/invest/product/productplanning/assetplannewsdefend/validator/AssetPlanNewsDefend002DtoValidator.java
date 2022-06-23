package com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.validator;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.validation.BeanValidator;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsDefend;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.dto.AssetPlanNewsDefend002Dto;
import com.joyintech.tams.invest.product.productplanning.assetplannewsdefend.service.AssetPlanNewsDefend002Service;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class AssetPlanNewsDefend002DtoValidator extends BaseService implements BeanValidator {

    @Autowired
    private AssetPlanNewsDefend002Service assetPlanNewsDefend002Service;

    @Override
    public List<ValidMessage> valid(Object o) {
        ArrayList<ValidMessage> errorList = new ArrayList<>();

        // （规划开始日和规划结束日、规划基准日需相同）
        AssetPlanNewsDefend002Dto dto = (AssetPlanNewsDefend002Dto) o;
        Map<String,Object> params = new HashMap<>();
        params.put("assetPlanCode",dto.getAssetPlanCode());
        params.put("vdate",DateUtil.format(dto.getVdate(),DateUtil.DEFAULT_DATE_FORMAT_EN));
        params.put("edate",DateUtil.format(dto.getEdate(),DateUtil.DEFAULT_DATE_FORMAT_EN));

        Integer conflictCount = assetPlanNewsDefend002Service.getConflictCount(params);
        //如果开始日、结束日和Db中资产规划信息维护表相同，则提示数据库中已有改记录，请修改！
        if(conflictCount > 0){
            errorList.add(new ValidMessage("vdate","数据库中已有该记录，请修改"));
            errorList.add(new ValidMessage("edate","数据库中已有该记录，请修改"));
        }
        //规划计划（规划开始日、规划结束日）区间不重叠
        Integer overlapCount = assetPlanNewsDefend002Service.getOverlapCount(params);

        if(overlapCount > 0){
            errorList.add(new ValidMessage("vdate","规划计划区间重叠,请修改"));
            errorList.add(new ValidMessage("edate","规划计划区间重叠,请修改"));
        }

        List<AssetPlanNewsDefend> defends = assetPlanNewsDefend002Service.queryProductTarget(params);

        if(CollectionUtils.isNotEmpty(defends)){
            AssetPlanNewsDefend defend = defends.get(0);
            BigDecimal assetTargetScale = dto.getAssetTargetScale();

            if(assetTargetScale != null && defend.getPlanTargetSum() != null){
                if(ArithmeticUtil.multiply(assetTargetScale,BigDecimal.valueOf(10000)).compareTo(defend.getPlanTargetSum()) < 0){
                    errorList.add(new ValidMessage("assetTargetScale","资产规划信息维护中的各产品系列的规划目标值之和必须小于目标资产规模"));
                }
            }

            if(defend.getBenchDate().compareTo(dto.getBenchDate()) != 0){
                errorList.add(new ValidMessage("benchDate","资产规划信息的基准日必须和产品规划信息的基准日相同"));
            }
        }

        return errorList;
    }
}
