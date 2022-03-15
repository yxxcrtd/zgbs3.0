$(function(){
    /**
     * 获取查询参数
     */
    // var getPageParams = function() {
    //
    //     //产品代码 多选择
    //     var portfolioIdTemp = $('#portfolioId').val();
    //     var portfolioId = null;
    //     if(portfolioIdTemp != null){
    //         portfolioId = portfolioIdTemp.join(",");
    //     }
    //
    //     //资产代码  多选择
    //     var finprodMarketIdTemp = $('#finprodMarketId').val();
    //     var finprodMarketId = null;
    //     if(finprodMarketIdTemp != null){
    //         finprodMarketId = finprodMarketIdTemp.join(",");
    //     }
    //
    //     //交易类型  多选择
    //     var psTemp = $('#ps').val();
    //     var ps = null;
    //     if(psTemp != null){
    //         ps = psTemp.join(",");
    //     }
    //
    //     return {
    //         tradeId: $('#tradeId').val(),
    //         portfolioId: portfolioId,
    //         finprodMarketId: finprodMarketId,
    //         tradeDateBegin: $("#tradeDate").startDate(),
    //         tradeDateEnd: $("#tradeDate").endDate(),
    //         ps: ps
    //     }
    // };

    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        return {
            tradeId: $('#tradeId').val(),
            portfolioId: $.trim($('#portfolioId').val()),
            finprodMarketId: $.trim($('#finprodMarketId').val()),
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
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002/init",
                    "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    portfolioId:data.portfolioId,
                    entrustId:data.entrustId,
                    finprodId:data.finprodId,
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:200, sortField:'tradeId',title: '交易编号'}
            ,{field:'outTradeId', width:120, sortField:'outTradeId',title: '外部交易编号'}
            ,{field:'portfolioId', width:150, title: '产品代码',sortField:'portfolioId'}
            ,{field:'portfolioName', width:150, title: '产品名称',sortField:'portfolioName'}
            ,{field:'finprodMarketId', width:150, sortField:'finprodMarketId',title: '资产代码'}
            ,{field:'finprodName', width:120, sortField:'finprodName',title: '资产名称'}
            ,{field:'psStr', width:100, title: 'TrdDepositValuation001.ps',sortField:'ps'}
            ,{field:'ps', title: '交易类型',visible:false}
            ,{field:'tradeDateStr', width:120,  title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate',title: '交易日期',visible:false}
            ,{field:'settDateStr', width:120,  title: '交割日期',sortField:'settDate'}
            ,{field:'settDate', title: '交割日期',visible:false}
            ,{field:'prinAmt', width:120, title: '交易本金',type:'number',sortField:'prinAmt'}
            ,{field:'intAmt', width:120, title: '应记利息总额',type:'number',sortField:'intAmt'}
            ,{field:'counterId', title: '交易对手',width:100}
            ,{field:'ccyFmt', width:120, title: '币种',sortField:'ccy'}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'settleAmt', width:120, title: '结算金额',type:'number',sortField:'settleAmt'}
            // ,{field:'dealAmt', width:120, title: '本币交易金额',type:'number',sortField:'dealAmt'}
            ,{field:'trdAcctId', title: '资金账户',width:100}
            ,{field:'action', title: '操作' , type:'action',buttons: ['edit', 'delt'] , width:'10%'}
        ]
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
    
    /**
     * 画面加载完成
     */
    $(function() {
        $('#TrdDepositValuationTrade001_btn_add').click(function () {
            Joyin.dialog(JoyinUtil.getLabel("TrdDepositValuationTrade002"),
                basePath + "valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002/init",
                "98%", "98%", {pageModel: PAGE_MODEL_NEW});
        });


        // 绑定批量删除事件
        $('#TrdDepositValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade001/delete',
                        object,
                        "TrdDepositValuationTrade001_btn_delete",
                        "TrdDepositValuationTrade002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
        //导出
        $('#TrdDepositValuationTrade001_btn_export').click(function () {
            exportExcel();
        });
        //查询
        $('#TrdDepositValuationTrade001_btn_search').click(function () {
            table.load();
        });

        /**
         * 绑定编辑事件
         */
        table.bind('click', '.edit', function(data, index) {
            data.pageModel = PAGE_MODEL_EDIT;
            var object = {selectedItem: [data], operateType:"edit"};//请求参数
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.dialog(JoyinUtil.getLabel("存款类交易-修改"),
                    basePath + "valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade002/init",//打开新界面
                    "98%", "98%", {
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trddepositvaluationtrade/TrdDepositValuationTrade001/delete',
                        object,
                        "TrdDepositValuationTrade001_btn_delete",
                        "TrdDepositValuationTrade002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });

    });

}());