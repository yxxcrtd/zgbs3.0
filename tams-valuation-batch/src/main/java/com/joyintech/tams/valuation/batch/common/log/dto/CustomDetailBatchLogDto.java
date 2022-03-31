package com.joyintech.tams.valuation.batch.common.log.dto;
/*
 * @ClassName CustomDetailBatchLogDto
 * @Description 自定义日志扩展属性 属性必须有注解，反射调用
 * @Date 2022/3/30 11:43
 * @Author 殷亚
 * @Version 1.0
 **/

public class CustomDetailBatchLogDto extends DetailBatchLogExpand{
    // TODO
    //属性名必须使用DetailLogConfig注解配置字段描述
    /*
     *业务代码
     **/
    @DetailLogConfig(description="测试")
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
