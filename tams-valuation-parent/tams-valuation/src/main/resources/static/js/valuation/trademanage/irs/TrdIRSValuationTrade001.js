(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        return {
            tradeId: $('#tradeId').val(),
            finprodId: $.trim($('#finprodId').val()),
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
            Joyin.dialog(JoyinUtil.getLabel("利率互换交易查看："),
                JoyinUtil.getRealPath("/valuation/trademanage/irs/TrdIRSValuationTrade002/init"),
                "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE,tradeId:data.tradeId,prodId:data.finprodId,ps:data.ps},
                function(){
                    //table.reload();
                });
        },
        cols:[
            {field:'finprodId', title: '产品代码',width:180},
            {field:'finprodName', title: '产品名称',width:180},
            {field:'psStr', title: '交易类型',sortField:"ps",width:150},
            {field:'ps', title: '交易类型',visible:false},
            {field:'tradeId', title: '交易编号',width:220},
            {field:'outTradeId', title: '外部成交编号',width:220},
            {field:'tradeDate', title: '交易日',width:120},
            {field:'vdate', title: '起息日',width:120},
            {field:'mdate', title: '到期日',width:120},
            {field:'ccyFmt', title: '币种',sortField:"ccy",width:180},
            {field:'ccy', title: '币种',visible:false},
            {field:'prin',type:"money", title: '名义本金',width:180},
            {field:'yield',type:"money", title: '固定利率%',width:180},
            {field:'fixPayCycle', title: '固定利率付息频率',width:180},
            {field:'firstPayDateR', title: '固定利率首期起息日',width:180},
            {field:'intType', title: '浮动挂钩利率',width:180},
            {field:'spreadRate', type:"money",title: '利差（BP）',width:180},
            {field:'floatPayCycle', title: '浮动利率付息频率',width:180},
            {field:'firstPayDateF', title: '浮动利率首期起息日',width:180},
            {field:'counterId', title: '交易对手',width:180},
            {field:'settleAmt',type:"money", title: '结算金额',width:180},
            {field:'acctNo', title: '资金账户',width:180},
            {field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width : 120,
                align : 'center',type : 'action', buttons : [ 'edit', 'delt']}
        ],
        //loadonce:true
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        Joyin.ajaxJson(basePath + 'valuation/trademanage/irs/TrdIRSValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("利率互换交易-修改"),
                    basePath + "valuation/trademanage/irs/TrdIRSValuationTrade002/init",//打开新界面
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/irs/TrdIRSValuationTrade001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/irs/TrdIRSValuationTrade001/delete',
                    object,
                    "TrdIRSValuationTrade001_btn_delete",
                    "TrdIRSValuationTrade002",
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
        $('#TrdIRSValuationTrade001_btn_search').click(function() {
            table.load();
        });
        $('#TrdIRSValuationTrade001_btn_export').click(function() {
            exportExcel();
        });

        // 绑定新建事件
        $('#TrdIRSValuationTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("利率互换交易新建（<i class='iconfont must' style='display:inline'>*</i>必填）"),
                basePath + "valuation/trademanage/irs/TrdIRSValuationTrade002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_NEW},function(){
                    table.reload();
                });
        });

        // 绑定删除事件
        $('#TrdIRSValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/irs/TrdIRSValuationTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/irs/TrdIRSValuationTrade001/delete',
                        object,
                        "TrdIRSValuationTrade001_btn_delete",
                        "TrdIRSValuationTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());