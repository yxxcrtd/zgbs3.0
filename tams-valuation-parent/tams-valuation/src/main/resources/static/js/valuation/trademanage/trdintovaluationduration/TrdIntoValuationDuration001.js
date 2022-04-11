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
            ps: $('#ps').val()
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
                basePath + "valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:180, sortField:'tradeId',title: '交易编号'}
            ,{field:'prodId', width:150, title: '产品代码'}
            ,{field:'prodName', width:150, title: '产品名称'}
            ,{field:'secId', width:150,title: '资产代码'}
            ,{field:'secAbbr', width:150,title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'ps', title: '交易类型',visible:false}
            ,{field:'ccyFmt', width:150, title: '币种',sortField:'ccy'}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'vdateStr', width:150,  title: '计息开始日',sortField:'vdate'}
            ,{field:'vdate', title: '计息开始日',visible:false}
            ,{field:'mdateStr', width:150,  title: '计息结束日',sortField:'mdate'}
            ,{field:'mdate', title: '计息结束日',visible:false}
            ,{field:'tradeDateStr', width:150,  title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate',title: '交易日期',visible:false}
            ,{field:'settleDateStr', width:150,  title: '交割日期',sortField:'settleDate'}
            ,{field:'settleDate',title: '交割日期',visible:false}
            ,{field:'actInt', width:150, title: '实收利息',type:'money'}
            ,{field:'actCapi', width:150, title: '实收本金',type:'money'}
            ,{field:'settleAmt', width:150, title: '结算金额',type:'money'}
            ,{field:'myAcctNo', title: '资金账户',width:100}
            ,{field:'action', title: '操作' , type:'action',buttons: ['edit', 'delt'] , width:'10%'}
        ],
        //loadonce:tru
    });

    /**
     * 导出选中的表格数据
     */
    var exportExcel = function() {

        var selArr = [];
        var datas = null;
        if (table != null && table != '') {
            selArr = table.getAllCheckedRows();
            datas = JSON.stringify(selArr);
        }
        if (!selArr || selArr.length == 0) {
            var paramObj = getPageParams();
            Joyin.exportExcel('export', paramObj);
        } else {
            Joyin.exportExcel('export', {
                datas : datas
            });
        }
    };

    
    $(function () {
        // 绑定按钮事件
        $('#TrdIntoValuationDuration001_btn_search').click(function() {
            table.load();
        });

        // 绑定批量删除事件
        $('#TrdIntoValuationDuration001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration001/delete',
                        object,
                        "TrdIntoValuationDuration001_btn_delete",
                        "TrdIntoValuationDuration002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
        $('#TrdIntoValuationDuration001_btn_export').click(function() {
            exportExcel();
        });
        $('#TrdIntoValuationDuration001_btn_add').click(
                function() {
                    Joyin.dialog(JoyinUtil.getLabel("TrdIntoValuationDuration002"), basePath
                            + "valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration002/init", "95%", "90%", {
                        pageModel : PAGE_MODEL_NEW
                    }, function() {
                        table.reload();
                    });
        });

        /**
         * 绑定编辑事件
         */
        table.bind('click', '.edit', function(data, index) {
            data.pageModel = PAGE_MODEL_EDIT;
            var object = {selectedItem: [data], operateType:"edit"};//请求参数
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("融入券存续期-修改"),
                    basePath + "valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration002/init",//打开新界面
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdintovaluationduration/TrdIntoValuationDuration001/delete',
                        object,
                        "TrdIntoValuationDuration001_btn_delete",
                        "TrdIntoValuationDuration002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
    });
}());