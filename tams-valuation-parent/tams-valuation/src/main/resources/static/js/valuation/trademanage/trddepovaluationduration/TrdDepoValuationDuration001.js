(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {

        //产品代码 多选择
        var portfolioIdTemp = $('#portfolioId').val();
        var portfolioId = null;
        if(portfolioIdTemp != null){
            portfolioId = portfolioIdTemp.join(",");
        }

        //资产代码  多选择
        var finprodMarketIdTemp = $('#finprodMarketId').val();
        var finprodMarketId = null;
        if(finprodMarketIdTemp != null){
            finprodMarketId = finprodMarketIdTemp.join(",");
        }

        //交易类型  多选择
        var psTemp = $('#ps').val();
        var ps = null;
        if(psTemp != null){
            ps = psTemp.join(",");
        }

        return {
            tradeId: $('#tradeId').val(),
            portfolioId: portfolioId,
            finprodMarketId: finprodMarketId,
            tradeDateBegin: $("#tradeDate").startDate(),
            tradeDateEnd: $("#tradeDate").endDate(),
            ps: ps
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
                basePath + "valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    portfolioId:data.portfolioId,
                    entrustId:data.entrustId,
                    finprodId:data.finprodId,
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:190, sortField:'tradeId',title: '交易编号'}
            ,{field:'portfolioId', width:150, title: '产品代码',sortField:'portfolioId'}
            ,{field:'portfolioName', width:150, title: '产品名称',sortField:'portfolioName'}
            ,{field:'finprodMarketId', width:150, sortField:'finprodMarketId',title: '资产代码'}
            ,{field:'finprodName', width:150, sortField:'finprodName',title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'ps', title: '交易类型',visible:false}
            ,{field:'tradeDateStr', width:150,  title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate',title: '交易日',visible:false}
            ,{field:'planPayDateStr', width:150,  title: '计划支付日',sortField:'planPayDate'}
            ,{field:'planPayDate', title: '计划支付日',visible:false}
            ,{field:'payDateStr', width:150,  title: '实际支付日',sortField:'payDate'}
            ,{field:'payDate', title: '实际支付日',visible:false}
            ,{field:'actIntStr', width:150, title: '实收利息',type:'number',sortField:'actInt'}
            ,{field:'actCapiStr', width:150, title: '实收本金',type:'number',sortField:'actCapi'}
            ,{field:'settleAmtStr', width:150, title: '结算金额',type:'number',sortField:'settleAmt'}
            ,{field:'ccyFmt', width:150, title: '币种',sortField:'ccy'}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'trdAcctId', title: '资金账户',width:100}
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
        $('#TrdDepoValuationDuration001_btn_search').click(function() {
            table.load();
        });

        // 绑定批量删除事件
        $('#TrdDepoValuationDuration001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration001/delete',
                        object,
                        "TrdDepoValuationDuration001_btn_delete",
                        "TrdDepoValuationDuration002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
        $('#TrdDepoValuationDuration001_btn_export').click(function() {
            exportExcel();
        });
        $('#TrdDepoValuationDuration001_btn_add').click(
                function() {
                    Joyin.dialog(JoyinUtil.getLabel("TrdDepoValuationDuration002"), basePath
                            + "valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration002/init", "99%", "99%", {
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.dialog(JoyinUtil.getLabel("存款类存续期-修改"),
                    basePath + "valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration002/init",//打开新界面
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trddepovaluationduration/TrdDepoValuationDuration001/delete',
                        object,
                        "TrdDepoValuationDuration001_btn_delete",
                        "TrdDepoValuationDuration002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
    });
}());