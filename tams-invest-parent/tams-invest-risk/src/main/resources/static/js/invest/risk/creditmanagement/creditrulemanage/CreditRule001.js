(function() {
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
        	   var result = $.serializeToObject('#searchForm');
               if ($("#creditType").val()) {
                   result.creditType = $("#creditType").val().join(",");
               } else {
                   result.creditType = null;
               }
               if ($("#creditTagName").val()) {
                   result.creditTagName = $("#creditTagName").val().join(",");
               } else {
                   result.creditTagName = null;
               }
               if ($("#astType1").val()) {
                   result.astType1 = $("#astType1").val().join(",");
               } else {
                   result.astType1 = null;
               }
               if ($("#astType2").val()) {
                   result.astType2 = $("#astType2").val().join(",");
               } else {
                   result.astType2 = null;
               }
               if ($("#astType3").val()) {
                   result.astType3 = $("#astType3").val().join(",");
               } else {
                   result.astType3 = null;
               }
               if ($("#astType4").val()) {
                   result.astType4 = $("#astType4").val().join(",");
               } else {
                   result.astType4 = null;
               }
                if ($("#astType5").val()) {
                    result.astType5 = $("#astType5").val().join(",");
                } else {
                    result.astType5 = null;
                }
            return result;
    };

    var table2 = {
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        /**
         * 双击查看
         */
        dblclick:function(table, data) {
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/risk/creditmanagement/creditrulemanage/CreditRule002/init",
                "95%", "90%", data);
        },

        cols:[
            {field:'creditTagId', width:50 ,visible: false}
            ,{field:'astType1Name', width:200 , title: 'CeRule002.astType1',sortField:'AST_TYPE_1'}
            ,{field:'astType2Name', width:200 , title: 'CeRule002.astType2',sortField:'AST_TYPE_2'}
            ,{field:'astType3Name', width:200 , title: 'CeRule002.astType3',sortField:'AST_TYPE_3'}
           /* ,{field:'underAssetTypeName', width:150 , title: 'CeRule002.underAssetType'}*/
            ,{field:'deductObjTypeName', width:200 , title: 'CeRule002.deductObjType',sortField:'DEDUCT_OBJ_TYPE'}
            ,{field:'creditTagName', width:200 , title: 'CeRule002.creditTag',sortField:'CREDIT_TAG_NAME'}
           /* ,{field:'creditTypeName', width:150 , title: 'CeRule001.creditType'}*/
            ,{field:'operation',width:200 , title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    }

    /**
     * 定义数据表格
     */
    var table1 = {
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        /**
         * 双击查看
         */
        dblclick:function(table, data) {
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "invest/risk/creditmanagement/creditrulemanage/CreditRule002/init",
                    "95%", "90%", data);
        },
        
        cols:[
             {field:'creditTagId', width:50 ,visible: false}
            ,{field:'astType1Name', width:150 , title: 'CeRule002.astType1',sortField:'AST_TYPE_1'}
            ,{field:'astType2Name', width:150 , title: 'CeRule002.astType2',sortField:'AST_TYPE_2'}
            ,{field:'astType3Name', width:150 , title: 'CeRule002.astType3',sortField:'AST_TYPE_3'}
            ,{field:'astType4Name', width:150 , title: 'CeRule002.astType4',sortField:'AST_TYPE_4'}
            ,{field:'astType5Name', width:150 , title: 'CeRule002.astType5',sortField:'AST_TYPE_5'}
            // ,{field:'underAssetTypeName', width:150 , title: 'CeRule002.underAssetType'}
            ,{field:'deductObjTypeName', width:150 , title: 'CeRule002.deductObjType',sortField:'DEDUCT_OBJ_TYPE'}
            ,{field:'creditTagName', width:150 , title: 'CeRule002.creditTag',sortField:'CREDIT_TAG_NAME'}
            ,{field:'creditTypeName', width:150 , title: 'CeRule001.creditType',sortField:'CREDIT_TYPE'}
            ,{field:'operation',width:150 , title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    };

    var table;
    if (IS_SHOW_TYPE == '01'){
        table=$("#dataTable2").joyintable(table1);
    } else {
        table=$("#dataTable2").joyintable(table2);
    }
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.operateType = 'edit';
        Joyin.ajaxJson(basePath + 'invest/risk/creditmanagement/creditrulemanage/CreditRule002/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("CreditRule002.edit"),
                    basePath + "invest/risk/creditmanagement/creditrulemanage/CreditRule002/init",
                    "65%", "80%",data,function() {
                                    });
        });
    });
    
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/risk/creditmanagement/creditrulemanage/CreditRule001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/risk/creditmanagement/creditrulemanage/CreditRule001/delete', object,
                        "CreditRule001_btn_delete", "CreditRule002",function() {
                });
            });
        });
    });

    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
        var paramObj = getSearchParams();
        Joyin.exportExcel('export', paramObj);
    };
    /**
     * 弹出新建画面
     * @returns
     */
    function openNew() {
        Joyin.dialog(JoyinUtil.getLabel("CreditRule002"),
                JoyinUtil.getRealPath("invest/risk/creditmanagement/creditrulemanage/CreditRule002/init"),
                "65%", "80%",{pageModel : PAGE_MODEL_NEW},function(){}
        );
    }
    
    /**
     * 画面加载完成
     */
    $(function() {
    	// 绑定按钮事件
        $('#CreditRule001_btn_search').click(function() {
            table.load();
            $('#CreditRule001_btn_delete').show();
        });
        $('#CreditRule001_btn_export').click(function() {
            exportExcel();
        });
        $('#CreditRule001_btn_add').click(function() {
            openNew();
        });
        
        $('#CreditRule001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/risk/creditmanagement/creditrulemanage/CreditRule001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/risk/creditmanagement/creditrulemanage/CreditRule001/delete', object,
                            "CreditRule001_btn_delete","CreditRule002");
                });
            });
        });
        
        if (IS_SHOW_TYPE == '01'){
            $('#selector5').show();
            $('#selector6').show();
        }else{
            $('#selector5').hide();
            $('#selector6').hide();
        }
    });
}());