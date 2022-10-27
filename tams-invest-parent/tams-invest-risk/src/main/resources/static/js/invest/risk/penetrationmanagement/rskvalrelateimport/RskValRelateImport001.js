$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var param = $.serializeToObject('#searchForm');
        if (param.groupId) {
            param.groupId = param.groupId.join(",");
        }
        return param;
    };
    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        cols:[
            {field:'ptlId',visible:false}//组合代码
            ,{field:'name', width:200 , title: '资管计划名称'}//组合名称
            ,{field:'startRow', width:200 , title: 'RskValRelateImport001.startRow'}//起始行
            ,{field:'subjectCodeCol', width:200,visible:false , title: 'RskValRelateImport001.subjectCodeCol'}//科目代码列
           /* ,{field:'startRowLetter', width:200 , title: 'RskValRelateImport001.startRow',sortField:'START_ROW'}//起始行*/
            ,{field:'subjectCodeColLetter', width:200 , title: 'RskValRelateImport001.subjectCodeCol',sortField:'SUBJECT_CODE_COL'}//科目代码列
            ,{field:'structMapId', width:200 ,title: 'RskValRelateImport001.structMapId'}//指标结构映射
            ,{field:'sumsubMapId', width:200 ,title: 'RskValRelateImport001.sumsubMapId'}//汇总科目名称映射
            ,{field:'assetSubMapId', width:200 ,title: 'RskValRelateImport001.assetSubMapId'}//资产科目代码映射
//            ,{field:'importFilter', width:150 , title: '导入过滤器'}//导入过滤器
            ,{field:'operation', width:150,title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });
    
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.operateType = 'edit';
        Joyin.ajaxJson(basePath + 'invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport002/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel('RskValRelateImport002.edit'),
                    basePath + 'invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport002/init',
                    "60%", "60%", {ptlId : data.ptlId, pageModel : PAGE_MODEL_EDIT}, function(){
                        table.reload();
                        });
        });
    });
    
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var param = {ptlId : data.ptlId, operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport002/check', param, function(result) {
            Joyin.confirm(result.data, function() {
                Joyin.ajaxJson(basePath + 'invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport002/delete',
                        param, function(result) {
                    table.reload();//删除事件请求及回调函数
                })
            })
        })
    });
    
    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();
        Joyin.exportExcel('export', paramObj);
    };
    
    /**
     * 画面加载完成
     */
    $(function() {
        //查询
        $('#RskValRelateImport001_btn_search').click(function() { 
            table.load();
        });
        $('#RskValRelateImport001_btn_export').click(function() {
            exportExcel();
        });
        //新建事件
        $('#RskValRelateImport001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("RskValRelateImport002"),
                basePath + "invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport002/init",
                "60%", "60%", {pageModel:PAGE_MODEL_NEW});
        });
        //批量删除事件
        $('#RskValRelateImport001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport002/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    Joyin.ajaxJson(basePath + 'invest/risk/penetrationmanagement/rskvalrelateimport/RskValRelateImport001/delete',
                            object, function(result) {
                        table.reload();//删除事件请求及回调函数
                    })
                });
            });
        });

    });
}());