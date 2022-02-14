(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        return {
            tradeId: $('#tradeId').val(),
            finprodId: $.trim($('#finprodId').val()),
            targetId: $.trim($('#targetId').val()),
            contractType: $.trim($('#contractType').val()),
            tradeDateBegin: $("#tradeDate").startDate(),
            tradeDateEnd: $("#tradeDate").endDate(),
            ps: $.trim($('#ps').val())
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
            Joyin.dialog(JoyinUtil.getLabel("场外期权交易查看："),
                JoyinUtil.getRealPath("/valuation/trademanage/option/TrdOptionValuationTrade002/init"),
                "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE,tradeId:data.tradeId,prodId:data.finprodId,ps:data.ps},
                function(){
                    //table.reload();
                });
        },
        cols:[
            {field:'tradeId', title: '交易编号',width:220},
            {field:'outTradeId', title: '外部成交编号',width:220},
            {field:'finprodId', title: '产品代码',width:180},
            {field:'finprodName', title: '产品名称',width:180},
            {field:'contractTypeFmt', title: '合约类型',sortField:"contractType",width:180},
            {field:'contractType', title: '合约类型',visible:false},
            {field:'targetId', title: '合约标的',width:180},
            {field:'psStr', title: '交易类型',sortField:"ps",width:150},
            {field:'ps', title: '交易类型',visible:false},
            {field:'tradeDate', title: '交易日',width:120},
            {field:'term',title: '期限',width:180},
            {field:'vdate', title: '起息日',width:120},
            {field:'mdate', title: '到期日',width:120},
            {field:'counterId', title: '交易对手',width:180},
            {field:'nomCapi', type:"money",title: '名义本金',width:180},
            {field:'optFee',type:"money", title: '期权费',width:180},
            {field:'ccyFmt', title: '币种',sortField:"ccy",width:180},
            {field:'ccy', title: '币种',visible:false},
            {field:'settleAmt',type:"money", title: '结算金额',width:180},
            {field:'acctNo', title: '资金账户',width:180},
            {field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width : 120,
                align : 'center',type : 'action', buttons : [ 'edit', 'delt' ,
                    {text: '<i class="iconfont icon-submit"></i>', clas: 'check', title: '处理'}]}
        ],
        //loadonce:true
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        Joyin.ajaxJson(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("场外期权交易-修改"),
                    basePath + "valuation/trademanage/option/TrdOptionValuationTrade002/init",//打开新界面
                    "98%", "98%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                        tradeId:data.tradeId  //金融产品代码
                    },function(){
                        table.reload();//回调函数，表格刷新
                    });
            });
    });

    /**
     * 绑定处理事件
     */
    table.bind('click', '.check', function(data, index) {
        var object = {selectedItem: [data], operateType:"check"};//请求参数
        Joyin.ajaxJson(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.dialog(JoyinUtil.getLabel("场外期权存续期交易"),
                basePath + "valuation/trademanage/option/TrdOptionValuationTrade003/init",//打开新界面
                "98%", "98%", {//请求的page_model
                    tradeId:data.tradeId,  //金融产品代码
                    operateType:"check"
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/option/TrdOptionValuationTrade001/delete',
                    object,
                    "TrdOptionValuationTrade001_btn_delete",
                    "TrdOptionValuationTrade002",
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
        $('#TrdOptionValuationTrade001_btn_search').click(function() {
            table.load();
        });
        $('#TrdOptionValuationTrade001_btn_export').click(function() {
            exportExcel();
        });

        // 绑定新建事件
        $('#TrdOptionValuationTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("场外期权交易新建（<i class='iconfont must' style='display:inline'>*</i>必填）"),
                basePath + "valuation/trademanage/option/TrdOptionValuationTrade002/init",
                "95%", "90%", {pageModel:PAGE_MODEL_NEW},function(){
                    table.reload();
                });
        });

        // 绑定删除事件
        $('#TrdOptionValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/option/TrdOptionValuationTrade001/delete',
                        object,
                        "TrdOptionValuationTrade001_btn_delete",
                        "TrdOptionValuationTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());