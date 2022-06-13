package com.joyintech.tams.business.common.dto;

import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.business.common.pojo.BaseMap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * excel导出请求参数
 */
@ApiModel(value="excel导出请求参数",description="excel导出请求参数")
public class BaseExportRequest {

    @ApiModelProperty(name="params",value="条件",example="{...}", required = true)
    private BaseMap params;
    
    @ApiModelProperty(name="header",value="excel导出表头",example="[{colCode:'id', colName:'编号'}]", required = true)
    private List<BaseMap> header;

    @ApiModelProperty(name="data",value="导出数据",example="[{...}..] | {...}", required = true)
    private List<BaseMap> data;

    public BaseMap getParams() {
        return params;
    }

    public void setParams(BaseMap params) {
        this.params = params;
    }

    public List<BaseMap> getHeader() {
        return header;
    }

    public void setHeader(List<BaseMap> header) {
        this.header = header;
    }

    /**
     * 从请求参数中获取分页排序对象，设置导出条数
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getDto(Class clazz) {
        Map pageMode = (Map) params.get("pageMode");
        if(pageMode != null) { // 设置导出条数
            pageMode.put("pageNo", 1);
            pageMode.put("pageSize", 500000);
        }
        return (T) JsonUtils.json2Object(JsonUtils.object2Json(params), clazz);
    }

    public List<BaseMap> getData() {
        return data;
    }

    public void setData(List<BaseMap> data) {
        this.data = data;
    }
}
