package com.joyintech.tams.business.common.util;

import com.joyintech.framework.validation.ValidMessage;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * 根据@ApiModelProperty注解配置校验字段是否必填
 * 参考 FinTenderBondAsset002Dto
 *
 * @author gwl
 * @date 2022/04/22
 */
public class ValidUtils {

    /**
     * 校验是否必填
     *
     * @param obj
     * @param validList
     */
    public static void valid(Object obj, List<ValidMessage> validList) {
        if (obj == null) return;
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) return;
        for (Field field : fields) {
            ApiModelProperty ap = field.getAnnotation(ApiModelProperty.class);
            if (Objects.nonNull(ap) && ap.required()) {//是必填字段
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (field.getGenericType().getTypeName().equals("java.lang.String")) {
                        if (Objects.isNull(value) || value.equals("")) {
                            validList.add(new ValidMessage(String.format("%s%s", ap.value(), "不能为空")));
                        }
                    } else if (Objects.isNull(value)) {
                        validList.add(new ValidMessage(String.format("%s%s", ap.value(), "不能为空")));
                    }
                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取错误消息
     *
     * @param validList
     * @return
     */
    public static String getValidMessage(List<ValidMessage> validList) {
        if (CollectionUtils.isEmpty(validList)) return null;
        StringBuffer msg = new StringBuffer();
        validList.forEach(e -> msg.append(e.getMessage()).append("<br/>"));
        return msg.toString();
    }

}
