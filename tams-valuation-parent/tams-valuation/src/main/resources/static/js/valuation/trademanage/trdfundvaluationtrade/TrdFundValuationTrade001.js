$(function(){
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
            tradeMarket:$('#tradeMarket').val(),
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
            if(data.ps == 'T24'){
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                        tradeId:data.tradeId,ps:data.ps});
            }else{
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                        tradeId:data.tradeId,ps:data.ps});
            }
        },
        cols:[
            {field:'tradeId', width:180, title: '交易编号'}
            ,{field:'outTradeId', width:150, title: '外部成交编号'}
            ,{field:'prodId', width:150, title: '产品代码'}
            ,{field:'prodName', width:150, title: '产品名称'}
            ,{field:'assetId', width:150, title: '资产代码'}
            ,{field:'assetAbbr', width:150, title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'tradeMarketStr', width:150, title: '交易场所',sortField:'tradeMarket'}
            ,{field:'tradeDateStr',width:150, title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate',  title: '交易日期', visible: false}
            ,{field:'tradeAmt', width:150, title: '交易金额',type:'money'}
            ,{field:'dealNum', width:150, title: '交易份额',type:'number'}
            ,{field:'ccyFmt', title: '币种',sortField:"ccy",width:180}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'settleDateStr',width:150, title: '结算日期',sortField:'settleDate'}
            ,{field:'settleDate',  title: '结算日期', visible: false}
            ,{field:'tradeAmtArrive', width:150, title: '到账金额',type:'money'}
            ,{field:'subFee', width:150, title: '认购费',type:'money'}
            ,{field:'purFee', width:150, title: '申购费',type:'money'}
            ,{field:'redFee', width:150, title: '赎回费',type:'money'}
            ,{field:'brokerage', width:150, title: '经手费',type:'money'}
            ,{field:'commission', width:150, title: '净佣金',type:'money'}
            ,{field:'trdMyAcctNo', title: '资金账户',width:180}
            ,{field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width : 120,
                align : 'center',type : 'action', buttons : [ 'edit', 'delt' ]}
        ]
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        if(data.ps == 'T24'){
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("基金交易-修改"),
                    basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/init",//打开新界面
                    "95%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                        tradeId:data.tradeId  //金融产品代码
                        ,ps:data.ps
                    },function(){
                        table.reload();//回调函数，表格刷新
                    });
            });
        }else{
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("基金交易-修改"),
                    basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/init",//打开新界面
                    "95%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                        tradeId:data.tradeId  //金融产品代码
                        ,ps:data.ps
                    },function(){
                        table.reload();//回调函数，表格刷新
                    });
            });
        }
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001/delete',
                    object,
                    "TrdFundValuationTrade001_btn_delete",
                    "TrdFundValuationTrade002",
                    function() {
                        table.reload();
                    }
                );
            });
        });
    });

    /**
     * 导出事件
     */
    var exportExcel = function() {
        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
            var datas = JSON.stringify(selArr);
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
    
    /**
     * 画面加载完成
     */
    $(function() {
        $('#TrdFundValuationTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdFundValuationTrade002"),
                    basePath+ "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/init",
                        "95%", "90%", {pageModel:PAGE_MODEL_NEW});
        });

        // 绑定新建事件
        $('#TrdFundValuationTrade001_btn_add1').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            if (!selArr || selArr.length >1) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                return;
            }
            var ps = selArr[0].ps;
            var tradeId = selArr[0].tradeId;
            if(ps != 'T19'){
                Joyin.error(JoyinUtil.getMessage("请选择交易类型为'赎回确认'的数据进行操作！"));
                return;
            }
            Joyin.dialog(JoyinUtil.getLabel("基金赎回到账（<i class='iconfont must' style='display:inline'>*</i>必填）"),
                basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/init",
                "95%", "90%",
                {tradeId:tradeId ,ps:ps}
                ,function(){
                    table.reload();
                });
        });
       
        //导出
        $('#TrdFundValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        //查询
        $('#TrdFundValuationTrade001_btn_search').click(function(){
            table.load();
        });

        // 绑定删除事件
        $('#TrdFundValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade001/delete',
                        object,
                        "TrdFundValuationTrade001_btn_delete",
                        "TrdFundValuationTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());