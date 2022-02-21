(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        return {
            tradeId: $('#tradeId').val(),
            finprodId: $.trim($('#finprodId').val()),
            oAssetId: $.trim($('#oAssetId').val()),
            cAssetId: $.trim($('#cAssetId').val()),
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
            Joyin.dialog(JoyinUtil.getLabel("证券转换交易查看："),
                JoyinUtil.getRealPath("/valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/init"),
                "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE,tradeId:data.tradeId},
                function(){
                    //table.reload();
                });
        },
        cols:[
            {field:'tradeId', title: '交易编号',width:220},
            {field:'outTradeId', title: '外部成交编号',width:220},
            {field:'finprodId', title: '产品代码',width:180},
            {field:'finprodName', title: '产品名称',width:180},
            {field:'oAssetId', title: '转出证券代码',width:180},
            {field:'oAssetAbbr', title: '转出证券名称',width:180},
            {field:'cAssetId', title: '转入证券代码',width:180},
            {field:'cAssetAbbr', title: '转入证券名称',width:180},
            {field:'psStr', title: '交易类型',sortField:"ps",width:150},
            {field:'ps', title: '交易类型',visible:false},
            {field:'tradeDateStr', width:120,  title: '交易日',sortField:'tradeDate'},
            {field:'tradeDate', title: '交易日',visible:false},
            {field:'settleDateStr', width:120,  title: '交割日',sortField:'settleDate'},
            {field:'settleDate', title: '交割日',visible:false},
            {field:'invAimStr', title: '会计分类',sortField:"invAim",width:240},
            {field:'invAim', title: '会计分类',visible:false},
            {field:'oTradeNum', type:"money",title: '转出数量',width:180},
            {field:'oTradeAmt',type:"money", title: '转出金额',width:180},
            {field:'cTradeNum', type:"money",title: '转入数量',width:180},
            {field:'cTradeAmt',type:"money", title: '转入金额',width:180},
            {field:'ccyFmt', title: '币种',sortField:"ccy",width:180},
            {field:'ccy', title: '币种',visible:false},
            {field:'settleAmt',type:"money", title: '结算金额',width:180},
            {field:'acctNo', title: '资金账户',width:180},
            {field:'operation', title:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', type:'action', fixed: 'right',width:120,
                buttons:['edit', 'delt']},
        ],
        //loadonce:true
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("证券转换交易-修改"),
                    basePath + "valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/init",//打开新界面
                    "98%", "98%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                        tradeId:data.tradeId  //金融产品代码
                    },function(){
                        table.reload();//回调函数，表格刷新
                    });
            });
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade001/delete',
                    object,
                    "TrdBondChangeTrade001_btn_delete",
                    "TrdBondChangeTrade002",
                    function() {
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
        $('#TrdBondChangeTrade001_btn_search').click(function() {
            table.load();
        });
        $('#TrdBondChangeTrade001_btn_export').click(function() {
            exportExcel();
        });

        // 绑定新建事件
        $('#TrdBondChangeTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("证券转换交易新建（<i class='iconfont must' style='display:inline'>*</i>必填）"),
                basePath + "valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_NEW},function(){
                    table.reload();
                });
        });

        // 绑定删除事件
        $('#TrdBondChangeTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade001/delete',
                        object,
                        "TrdBondChangeTrade001_btn_delete",
                        "TrdBondChangeTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());