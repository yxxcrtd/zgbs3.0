(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        return {
            tradeId: $('#tradeId').val(),
            finprodId: $.trim($('#finprodId').val()),
            asset: $.trim($('#asset').val()),
            tradeDateBegin: $("#tradeDate").startDate(),
            tradeDateEnd: $("#tradeDate").endDate(),
            ps: $.trim($('#ps').val()),
            dataSource:$.trim($('#dataSource').val())
        }
    };

    /**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getPageParams();
            }
        },
        dblclick:function(table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:190, sortField:'tradeId',title: '交易编号'}
            ,{field:'prodId', width:150, title: '产品代码'}
            ,{field:'prodName', width:220, title: '产品名称'}
            ,{field:'assetId', width:150, title: '资产代码'}
            ,{field:'assetName', width:220, title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'ps', title: '交易类型',visible:false}
            ,{field:'invAimStr', title: '会计分类',sortField:"invAim",width:240}
            ,{field:'invAim', title: '会计分类',visible:false}
            ,{field:'tradeDateStr',width:150, title: '计划支付日',sortField:'tradeDate'}
            ,{field:'tradeDate', title: '计划支付日', visible: false}
            ,{field:'settleDateStr', width:150,  title: '资金到账日',sortField:'settleDate'}
            ,{field:'settleDate',title: '资金到账日',visible:false}
            ,{field:'actIntStr', width:150, title: '实收利息',type:'number',sortField:'actInt'}
            ,{field:'actCapiStr', width:150, title: '实收本金',type:'number',sortField:'actCapi'}
            ,{field:'settleAmtStr', width:150, title: '结算金额',type:'number',sortField:'settleAmt'}
            ,{field:'ccyFmt', width:150, title: '币种',sortField:'ccy'}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'myAcctNo', width:200, title: '资金账户'}
            ,{field:'dataSourceStr', title: '数据来源',sortField:"dataSource",width:150}
            ,{field:'dataSource', title: '数据来源',visible:false}
            ,{field:'action', title: '操作' , type:'action',buttons: ['edit', 'delt'] , width:'10%'}
        ],
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration001/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.dialog(JoyinUtil.getLabel("固收类存续期-修改"),
                basePath + "valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002/init",//打开新界面
                "95%", "90%", {
                    pageModel: PAGE_MODEL_EDIT,//请求的page_model
                    tradeId: data.tradeId  //金融产品代码
                }, function () {
                    table.reload();//回调函数，表格刷新
                });
        });
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration001/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration001/delete',
                    object,
                    "TrdFixIncomeValuDuration001_btn_delete",
                    "TrdFixIncomeValuDuration002",
                    function () {
                        table.reload();
                    }
                );
            });
        });
    });

    /**
     * 导出选中的表格数据
     */
    var exportExcel = function() {
        var paramObj = getPageParams();//请求参数json格式
        Joyin.exportExcel('export', paramObj);//导出（共通）
    };
    
    $(function () {
        // 绑定按钮事件
        $('#TrdFixIncomeValuDuration001_btn_search').click(function() {
            table.load();
        });

        // 绑定批量删除事件
        $('#TrdFixIncomeValuDuration001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration001/delete',
                        object,
                        "TrdFixIncomeValuDuration001_btn_delete",
                        "TrdFixIncomeValuDuration002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });

        $('#TrdFixIncomeValuDuration001_btn_export').click(function() {
            exportExcel();
        });

        $('#TrdFixIncomeValuDuration001_btn_add').click(
                function() {
                    Joyin.dialog(JoyinUtil.getLabel("TrdFixIncomeValuDuration002"), basePath
                            + "valuation/trademanage/trdfixincomevaluduration/TrdFixIncomeValuDuration002/init", "95%", "90%", {
                        pageModel : PAGE_MODEL_NEW
                    }, function() {
                        table.reload();
                    });
        });

    });
}());