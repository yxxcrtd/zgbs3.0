$(function() {
    var columns = [[
        {title: '基本信息',align:'center',sortable: false, colspan: 2}
        ,{title: '监管综合相关类型',align:'center',sortable: false, colspan: 2}
        ,{title: '流动性管理办法相关类型',align:'center',sortable: false, colspan: 3}
        ,{title: '理财新规相关类型',align:'center',sortable: false, colspan: 2}
        ,{field:'specificId', visible: false}
        ,{field:'type1Name', visible: false}
        ,{field:'type2Name', visible: false}
        ,{field:'type3Name', visible: false}
        ,{field:'type4Name', visible: false}
        ,{field:'operation', title: 'SysCommon.operation',rowspan: 2, type:'action', buttons: ['edit']}
    ],[
        {field:'finprodMarketId', sortField:"FINPROD_MARKET_ID", width:165 , title: 'RskSpecificAssetType001.finprodId'}
        ,{field:'finprodName', sortField:"FINPROD_NAME", width:165 , title: 'RskSpecificAssetType001.finprodName'}
        ,{field:'isFlowLimitedStr', sortField:"IS_FLOW_LIMITED", width:165 , title: '是否流动性受限'}
        ,{field:'isRegulationName', sortField:"IS_REGULATION", width:165 , title: 'RskSpecificAssetType001.isRegulation'}
        ,{field:'isLiquidity1Name', sortField:"IS_LIQUIDITY1", width:165 , title: 'RskSpecificAssetType001.isLiquidity1'}
        ,{field:'isLiquidity2Name', sortField:"IS_LIQUIDITY2", width:165 , title: 'RskSpecificAssetType001.isLiquidity2'}
        ,{field:'isLiquidity3Name', sortField:"IS_LIQUIDITY3", width:165 , title: 'RskSpecificAssetType001.isLiquidity3'}
        ,{field:'isBadassetName', sortField:"IS_BADASSET", width:165 , title: 'RskSpecificAssetType001.isBadasset'}
        ,{field:'isCreditAssetName', sortField:"IS_CREDIT_ASSET", width:165 , title: 'RskSpecificAssetType001.isCreditAsset'}
    ]];
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var param = $.serializeToObject('#searchForm');
        // 20210812 fuyongsheng删除 start
        // if(param.type1){
        //     param.type1 = $('#type1').val().join(",");
        // }
        // if(param.type2){
        //     param.type2 = $('#type2').val().join(",");
        // }
        // if(param.type3){
        //     param.type3 = $('#type3').val().join(",");
        // }
        // if(param.type4){
        //     param.type4 = $('#type4').val().join(",");
        // }
        // 20210812 fuyongsheng删除 end
        return param;
    }
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
        /**
         * 双击查看
         */
        dblclick:function(table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/risk/quota/rskspecificassettype/RskSpecificAssetType002/init",
                "85%", "85%", {pageModel:PAGE_MODEL_REFERENCE,specificId:data.specificId});
        },

        isMulti:true,
        cols:columns
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.ajaxJson(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType001/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("RskSpecificAssetType001"),
                basePath + "invest/risk/quota/rskspecificassettype/RskSpecificAssetType002/init",
                "85%", "85%",data,function() {
                    table.reload();
                });

        });
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType001/delete', object,
                    "RskSpecificAssetType001_btn_delete", "RskSpecificAssetType002",function() {
                        table.reload();
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
     * 画面加载完成
     */
    $(function() {
        // 绑定按钮事件
        $('#RskSpecificAssetType001_btn_search').click(function() {
            table.load();
            $('#RskSpecificAssetType001_btn_delete').show();
        }).click();
        $('#RskSpecificAssetType001_btn_export').click(function() {
            exportExcel();
        });
        <!--mody by   meifaxin  2021-9-26 弹出框高度不够，导致审批人弹框无法看到确定按钮 start-->
        $('#RskSpecificAssetType001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("RskSpecificAssetType001"),
                basePath + "invest/risk/quota/rskspecificassettype/RskSpecificAssetType002/init",
                "85%", "85%", {pageModel:PAGE_MODEL_NEW});
        });
        <!--mody by   meifaxin  2021-9-26 弹出框高度不够，导致审批人弹框无法看到确定按钮 end-->
        $('#RskSpecificAssetType001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(basePath + 'invest/risk/quota/rskspecificassettype/RskSpecificAssetType001/delete', object,
                        "RskSpecificAssetType001_btn_delete","RskSpecificAssetType002",function () {
                            table.reload();//删除事件请求及回调函数
                        });
                });
            });
        });

    });
}());