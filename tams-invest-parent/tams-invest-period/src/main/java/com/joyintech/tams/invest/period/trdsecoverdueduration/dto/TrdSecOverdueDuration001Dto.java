package com.joyintech.tams.invest.period.trdsecoverdueduration.dto;

import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.framework.validation.annotation.ValidLogic;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.period.trdsecoverdueduration.TrdSecOverdueDurationDto;
import com.joyintech.tams.invest.period.trdsecoverdueduration.validator.TrdSecOverdueDuration001DtoValidator;
import com.joyintech.tams.invest.period.trdsecoverdueduration.validator.TrdSecOverdueDuration002DtoValidator;
import com.joyintech.workflow.engine.annotation.PrimaryKey;
import com.joyintech.workflow.engine.dto.ProcessEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName TrdSecOverdueDurationDto
 * @Description: TODO
 * @Author wangxuelei
 * @Date 2019/12/4 0004
 * @Version V1.0
 **/
@PageId("TrdSecOverdueDuration001")
@ValidLogic(targetClass = TrdSecOverdueDuration001DtoValidator.class, groups = {ValidateGroupDelete.class, ValidateGroupEdit.class})
public class TrdSecOverdueDuration001Dto extends TrdSecOverdueDurationDto implements ProcessEntity{

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_2)
    public String getFinprodId() {
        return super.getFinprodId();
    }

    @Override
    @PrimaryKey(PrimaryKey.Field.PK_3)
    public String getSecAcctId() {
        return super.getSecAcctId();
    }

    @PrimaryKey(PrimaryKey.Field.PK_4)
    public String getBranchStr() {
        if(super.getBjBranch()!=null){
            return super.getBjBranch().toString();
        }
        else if(super.getLiBranch()!=null){
            return super.getLiBranch().toString();
        }
        return null;
    }
    public String getCashTypeStr() {
        if(StringUtil.isNoneBlank(this.getCashType())){
            String result ="";
            List<String> list = new ArrayList<>();
            String[] split = this.getCashType().split(",");
            if(split.length>0){
                TreeSet<String> hset = new TreeSet<String> (Arrays.asList(split));
                Iterator i = hset.iterator();
                while(i.hasNext()){
                    list.add(i.next().toString());
                }
            }
            if(CollectionUtils.isNotEmpty(list)){
                if(list.size()>1){
                    for (int i=0;i<list.size();i++) {
                        if(StringUtil.equals(list.get(i),DictCodeList.现金流类型_中间还本.getValue())||
                                StringUtil.equals(list.get(i),DictCodeList.现金流类型_到期还本.getValue())) {
                            result = SysDataUtil.getNameFromDataDict(DictCode.现金流类型, list.get(i));
                            break;
                        }
                    }
                }else{
                    result = SysDataUtil.getNameFromDataDict(DictCode.现金流类型, list.get(0));
                }
            }
            return result == null ? this.getCashType() : result;
        }
        return null;
    }
    public String getIsAccrPenaltyIntStr() {
        String result = "";
        List<String> list = new ArrayList<>();
        if(StringUtil.isNotBlank(this.getIsAccrPenaltyInt())){
            String[] split = this.getIsAccrPenaltyInt().split(",");
            if(split.length>0){
                TreeSet<String> hset = new TreeSet<String> (Arrays.asList(split));
                Iterator i = hset.iterator();
                while(i.hasNext()){
                    list.add(i.next().toString());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            result = SysDataUtil.getNameFromDataDict(DictCode.是否,list.get(0));
        }else{
            result = SysDataUtil.getNameFromDataDict(DictCode.是否,this.getIsAccrPenaltyInt());
        }
        return result == null ?this.getIsAccrPenaltyInt() : result;
    }
    public String getIsRushBackStr() {
        String result = "";
        List<String> list = new ArrayList<>();
        if(StringUtil.isNotBlank(this.getIsRushBack())){
            String[] split = this.getIsRushBack().split(",");
            if(split.length>0){
                TreeSet<String> hset = new TreeSet<String> (Arrays.asList(split));
                Iterator i = hset.iterator();
                while(i.hasNext()){
                    list.add(i.next().toString());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            result = SysDataUtil.getNameFromDataDict(DictCode.是否,list.get(0));
        }else{
            result = SysDataUtil.getNameFromDataDict(DictCode.是否,this.getIsRushBack());
        }
        return result == null ?this.getIsRushBack() : result;
    }

    public String getPenaltyBaseTypeStr(){
        String result = "";
        List<String> list = new ArrayList<>();
        if(StringUtil.isNotBlank(this.getPenaltyBaseType())){
            String[] split = this.getPenaltyBaseType().split(",");
            if(split.length>0){
                TreeSet<String> hset = new TreeSet<String> (Arrays.asList(split));
                Iterator i = hset.iterator();
                while(i.hasNext()){
                    list.add(i.next().toString());
                }
            }
        }
        if(CollectionUtils.isNotEmpty(list)){
            result = SysDataUtil.getNameFromDataDict(DictCode.罚息计提基数,list.get(0));
        }else{
            result = SysDataUtil.getNameFromDataDict(DictCode.罚息计提基数,this.getPenaltyBaseType());
        }
        return result == null ?this.getPenaltyBaseType() : result;
    }
}
