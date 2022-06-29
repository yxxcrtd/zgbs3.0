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
                result.deductRule= $('#deductRule').val();
            return result;
    };
    /**
     * 定义数据表格
     */
    var table=$("#dataTable2").joyintable({
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
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "invest/risk/creditmanagement/creditlinemanage/CeLine002/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,creditTagId:data.creditTagId});
        },
        
        cols:[
             {field:'creditTagId', width:50 ,visible: false}
            ,{field:'creditTypeText', width:350 , title: 'CeLine001.creditType', sortField:'t.CREDIT_TYPE'}
            ,{field:'creditTagName', width:400 , title: 'CeLine001.creditTagName', sortField:'t.CREDIT_TAG_NAME'}
            // ,{field:'deductRuleText', width:200 , title: 'CeLine001.deductRule', sortField:'t.DEDUCT_RULE'}
            // ,{field:'isPerpetualSecText', width:200 , title: 'CeLine001.isPerpetualSec', sortField:'t.IS_PERPETUAL_SEC'}
            // ,{field:'issueTypeText', width:200 ,title: 'CeLine001.issueType', sortField:'t.ISSUE_TYPE'}
            ,{field:'creditUsingTypeText',visible: false,width:150 , title: 'CeLine001.creditUsingType', sortField:'t.CREDIT_USING_TYPE'}
            ,{field:'isFxText',visible: false, width:150 ,title: 'CeLine001.isFx', sortField:'t.IS_FX'}
            ,{field:'curPairText',visible: false,width:150,  title: 'CeLine001.curPair', sortField:'t.CUR_PAIR'}
            // ,{field:'termValueStr',width:170 , title: 'CeLine001.termValue', sortField:'t.TERM_VALUE'}
            ,{field:'riskRatio',width:350 ,  title: 'CeLine001.riskRatio', sortField:'t.RISK_RATIO'}
            ,{field:'operation',width:150 , title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });    
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        data.operateType = 'edit';
        Joyin.ajaxJson(basePath + 'invest/risk/creditmanagement/creditlinemanage/CeLine002/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("CeOrg002.edit"),
                    basePath + "invest/risk/creditmanagement/creditlinemanage/CeLine002/init",
                    "65%", "80%",data,function() {
                                    });
        });
    });
    
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/risk/creditmanagement/creditlinemanage/CeLine001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/risk/creditmanagement/creditlinemanage/CeLine001/delete', object, 
                        "CeLine001_btn_delete", "CeLine002",function() {
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
        Joyin.dialog(JoyinUtil.getLabel("CeLine002"),
                JoyinUtil.getRealPath("invest/risk/creditmanagement/creditlinemanage/CeLine002/init"),
                "65%", "80%",{pageModel : PAGE_MODEL_NEW},function(){}
        );
    }

    // add by yushilong 20200630 增加额度标签管理导入功能 start
    /**
     * 绑定导入事件
     */
    function importExcel() {
        Joyin.confirm(JoyinUtil.getLabel("请确认是否要导入数据？"), function() {
            Joyin.uploadFile(function(fileName) {
                Joyin.ajax(basePath +"/invest/risk/creditmanagement/creditlinemanage/CeLine001/importFile", {
                    fileName : fileName
                },function(result){
                    if(!result || !($.isArray(result) && result.length > 0)) {
                        layui.layer.closeAll();
                        table.reload();
                    }
                });
            });
        });
    }
    // add by yushilong 20200630 增加额度标签管理导入功能 end

    /**
     * 画面加载完成
     */
    $(function() {
    	// 绑定按钮事件
        $('#CeLine001_btn_search').click(function() {
            table.load();
            $('#CeLine001_btn_delete').show();
        });
        $('#CeLine001_btn_export').click(function() {
            exportExcel();
        });
        $('#CeLine001_btn_add').click(function() {
            openNew();
        });
        
        $('#CeLine001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/risk/creditmanagement/creditlinemanage/CeLine001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/risk/creditmanagement/creditlinemanage/CeLine001/delete', object, 
                            "CeLine001_btn_delete","CeLine002");
                });
            });
        });
        // add by yushilong 20200630 增加额度标签管理导入功能 start
        // 导入按钮点击事件
        $('#CeLine001_btn_import').click(function() {
            importExcel();
        });
        // add by yushilong 20200630 增加额度标签管理导入功能 end
    });
}());