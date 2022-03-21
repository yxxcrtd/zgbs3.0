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
            ps: $('#ps').val(),
            tradeMarket: $('#tradeMarket').val()
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
            Joyin.dialog(JoyinUtil.getLabel("债券交易查看："),
                JoyinUtil.getRealPath("/valuation/trademanage/bond/TrdBondTrade002/init"),
                "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE,tradeId:data.tradeId,prodId:data.finprodId,ps:data.ps},
                function(){
                    //table.reload();
                });
        },
        cols:[
            {field:'tradeId', title: '交易编号',width:220},
            {field:'outTradeId', title: '外部成交编号',width:220},
            {field:'prodId', title: '产品代码',width:180},
            {field:'prodName', title: '产品名称',width:180},
            {field:'secId', title: '债券代码',width:180},
            {field:'secAbbr', title: '债券简称',width:180},
            {field:'tradeMarketStr', title: '交易场所',sortField:"tradeMarket",width:180},
            {field:'tradeMarket', title: '交易场所',visible:false},
            {field:'psStr', title: '交易类型',sortField:"ps",width:150},
            {field:'ps', title: '交易类型',visible:false},
            {field:'tradeDateStr', width:120,  title: '交易日期',sortField:'tradeDate'},
            {field:'tradeDate',title: '交易日期',visible:false},
            {field:'settdateDateStr', width:120,  title: '交割日期',sortField:'settdateDate'},
            {field:'settdateDate', title: '交割日期',visible:false},
            {field:'fundSettleDateStr', width:120,  title: '资金交收日期',sortField:'fundSettleDate'},
            {field:'fundSettleDate', title: '资金交收日期',visible:false},
            {field:'invAimStr', title: '会计分类',sortField:"invAim",width:240},
            {field:'invAim', title: '会计分类',visible:false},
            {field:'dealNum',title: '成交数量',width:180},
            //{field:'fpriceAmt',type:"money", title: '全价总额',width:180},
            {field:'tradeAmt', type:"money",title: '交易金额',width:180},
            {field:'intAmt', type:"money",title: '应收利息总额',width:180},
            {field:'ccyFmt', title: '币种',sortField:"ccy",width:180},
            {field:'ccy', title: '币种',visible:false},
            {field:'settleAmt',type:"money", title: '结算金额',width:180},
            {field:'myAcctNo', title: '资金账户',width:180},
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("债券交易-修改"),
                    basePath + "valuation/trademanage/bond/TrdBondTrade002/init",//打开新界面
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/bond/TrdBondTrade001/delete',
                    object,
                    "TrdBondTrade001_btn_delete",
                    "TrdBondTrade002",
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
        $('#TrdBondTrade001_btn_search').click(function() {
            table.load();
        });
        $('#TrdBondTrade001_btn_export').click(function() {
            exportExcel();
        });

        // 绑定新建事件
        $('#TrdBondTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("债券交易新建（<i class='iconfont must' style='display:inline'>*</i>必填）"),
                basePath + "valuation/trademanage/bond/TrdBondTrade002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_NEW},function(){
                    table.reload();
                });
        });

        // 绑定删除事件
        $('#TrdBondTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/bond/TrdBondTrade001/delete',
                        object,
                        "TrdBondTrade001_btn_delete",
                        "TrdBondTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());