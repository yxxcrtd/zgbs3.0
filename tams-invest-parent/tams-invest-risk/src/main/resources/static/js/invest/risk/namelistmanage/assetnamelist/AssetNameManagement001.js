(function() {
    /**
     * 获取有效查询参数
     */
    var getSearchParamsByEff = function() {
        var listId;
        if ($("#listId").val()) {
            listId = $("#listId").val().join(",");
        } else {
            listId = null;
        }
        return {
            isEffFlag:'01',
            listName:$('#listName').val(),
            rskSubjectType:RSK_SUBJECT_TYPE_02,
            listId:listId
        }
    };
    /**
     * 获取失效查询参数
     */
    var getSearchParamsByDelEff = function() {
        var listId;
        if ($("#listId").val()) {
            listId = $("#listId").val().join(",");
        } else {
            listId = null;
        }
        return {
            isEffFlag:'02',
            listName:$('#listName').val(),
            rskSubjectType:RSK_SUBJECT_TYPE_02
        }
    };

    /**
     * 定义数据表格（生效名单）
     */
    var effTable=$("#effectTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParamsByEff();
            }
        },
        /**
         * 定义表格双击事件
         */
        dblclick:function(table, data) {
            //在新建界面进行显示单行的详细信息
            Joyin.dialog(JoyinUtil.getLabel("AssetNameManagementDetail"),
                basePath + "invest/risk/namelistmanage/assetnamelist/AssetNameManagementDetail/init",
                "98%", "98%", {/*pageModel:PAGE_MODEL_REFERENCE,*/
                    workflowId:data.workflowId?data.workflowId:'',  //工作流Id
                    workflowStatus:data.workflowStatus,//流程状态
                    listId:data.listId//名单代码
                });
        },
        rowno:true,
        checkbox: false,
        searchOnInit: false,
        cols:[
                {field:'workflowId', width:190 , visible: false,title:'AssetNameManagement001.workflowId'}
                ,{field:'workflowStatus', width:190 , visible: false,title:'AssetNameManagement001.workflowStatus'}
                ,{field:'listId', width:190 , visible: false,title:'AssetNameManagement001.listId'}
                ,{field:'listType', width:300 ,visible: false,sortField:'LIST_TYPE'}
                ,{field:'listLevel', width:300 ,visible: false,sortField:'LIST_LEVEL'}
                /*,{field:'listName', width:400 , title:'AssetNameManagement001.listName',sortField:'LIST_NAME'}*/
                ,{field:'listName', title : 'AssetNameManagement001.listName', width:300,sortField:'LIST_NAME',
                    render: function (data, type, row, meta) {
                            var obj = {};
                            // modify by etliu 20211201 start
                            // TISHKS-432：资产名单管理中，列表数据中的名单名称需要可以链接进去，查看名单明细数据，同机构名单管理一样
                        // return  '<a href="javascript:void(0);" onclick="seeAllInfo('+row.workflowId+','+row.workflowStatus+','+row.listId+');" >'+row.listName+'</a>';
                            return  '<a href="javascript:void(0);" onclick="seeAllInfo(\''+ (row.workflowId ? row.workflowId : '') +'\',\''+ (row.workflowStatus ? row.workflowStatus : '') +'\',\''+row.listId+'\');" >'+row.listName+'</a>';
                            // modify by etliu 20211201 end
                    }
                }
                ,{field:'listTypeStr', width:200 ,title:'AssetNameManagement001.listType',sortField:'LIST_TYPE'}
                ,{field:'listLevelStr', width:200 ,title:'AssetNameManagement001.listLevel',sortField:'LIST_LEVEL'}
                ,{field:'effdate', width:200 ,title:'AssetNameManagement001.effdate',type:'date',sortField:'EFFDATE'}
                ,{field:'operation', width:400 , title: 'SysCommon.operation', sortable:false,
                            render:function (data, type, row, meta) {
                            return '<a class="btn layui-btn  manual" title="手工录入" style="color:white">手工录入</a><a class="btn layui-btn batch" title="批量录入" style="color:white">批量录入</a><a class="btn layui-btn del" title="删除" style="color:white">删除</a>';
                    }
                }]
    });

    /**
     * 手工录入弹出页面
     */
    effTable.bind('click', '.manual', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete",listId:data.listId,listDetailType:RSK_SUBJECT_TYPE_02};//请求参数
        Joyin.ajaxJson(basePath + 'invest/risk/namelistmanage/assetnamelist/AssetNameManagement001/check', object, function(result) {
            Joyin.dialog(JoyinUtil.getLabel('AssetNameManagement003'), '../AssetNameManagement003/init', '98%', '98%',
                {
                    listId:data.listId,
                    listName:data.listName,
                    operateStyle:RSK_INPUT_TYPE_MANUAL,
                    listDetailType:RSK_SUBJECT_TYPE_02,
                    listType:data.listType,
                    listLevel:data.listLevel
                },function(){
                    effTable.reload();//回调函数，表格刷新
                    uneffTable.reload();//回调函数，表格刷新
            });
        });
    });
    /**
     * 批量录入弹出页面
     */
    effTable.bind('click', '.batch', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete",listId:data.listId,listDetailType:RSK_SUBJECT_TYPE_02};//请求参数
        Joyin.ajaxJson(basePath + 'invest/risk/namelistmanage/assetnamelist/AssetNameManagement001/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.dialog(JoyinUtil.getLabel('AssetNameManagement004.batch'), '../AssetNameManagement004/init', '98%', '98%',
                {
                    listId:data.listId,
                    listName:data.listName,
                    operateStyle:RSK_INPUT_TYPE_BATCH,
                    listDetailType:RSK_SUBJECT_TYPE_02,
                    listType:data.listType,
                    listLevel:data.listLevel
                },function(){
                    effTable.reload();//回调函数，表格刷新
                    uneffTable.reload();//回调函数，表格刷新
                });
        });
    });

    /**
     * 删除
     */
    effTable.bind('click', '.del', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete",listId:data.listId};//请求参数
        Joyin.ajaxJson(basePath + 'invest/risk/namelistmanage/assetnamelist/AssetNameManagement001/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){//删除事件的请求
                WorkFlow.doDeleteList(basePath + 'invest/risk/namelistmanage/assetnamelist/AssetNameManagement001/delete', object,"AssetNameManagement001_btn_delete", "AssetNameManagement002", function(){
                    effTable.reload();//回调函数，表格刷新
                    uneffTable.reload();//回调函数，表格刷新
            });
        });
        });
    });


    /**
     * 绑定导出事件
     */
    var exportExcel = function() {
        var paramObj = getSearchParamsByOrg();
        Joyin.exportExcel('export', paramObj);
    };

    /**
     * 定义数据表格（失效）
     */
    var uneffTable=$("#uneffectTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParamsByDelEff();
            }
        },
        rowno:true,
        checkbox: false,
        searchOnInit: false,
        cols:[
            {field:'listId', width:190 , visible: false,title:'AssetNameManagement001.listId'}
            ,{field:'listName', width:450 , title:'AssetNameManagement001.listName',sortField:'LIST_NAME'}
            ,{field:'effdate', width:400 ,title:'AssetNameManagement001.effdate',type:'date',sortField:'EFFDATE'}
            ,{field:'deldate', width:400 ,title:'AssetNameManagement001.deldate',type:'date',sortField:'DELDATE'}
        ]
    });

    /**
     * 绑定导出事件
     */
    var exportAssetExcel = function() {
        var paramObj = getSearchParamsByAsset();
        Joyin.exportExcel('export', paramObj);
    };


    /**
     * 画面加载完成
     */
    $(function() {
        //初始化查询
        $('#AssetNameManagement001_btn_search').click(function() {
            effTable.load();
            uneffTable.load();
        });
        //新建
        /*$('#AssetNameManagement001_btn_add').click(showDialog);*/
    });
}());
/**
 * 超链接查看方法
 */
var seeAllInfo = function(workflowId,workflowStatus,listId){
    Joyin.dialog(JoyinUtil.getLabel("AssetNameManagementDetail"),
        basePath + "invest/risk/namelistmanage/assetnamelist/AssetNameManagementDetail/init",
        "98%", "98%", {/*pageModel:PAGE_MODEL_REFERENCE,*/
            workflowId:workflowId?workflowId:'',  //工作流Id
            workflowStatus:workflowStatus?workflowStatus:'',//流程状态
            listId:listId?listId:''//名单代码
        });
}