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

        //交易场所  多选择
        var tradeMarketTemp = $('#tradeMarket').val();
        var tradeMarket = null;
        if(tradeMarketTemp != null){
            tradeMarket = tradeMarketTemp.join(",");
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
            tradeDateBegin: $("#tradeDate").startDate(),
            tradeDateEnd: $("#tradeDate").endDate(),
            ps: ps,
            tradeMarket: tradeMarket
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
                basePath + "valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade002/init",
                "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    portfolioId:data.portfolioId,
                    entrustId:data.entrustId,
                    finprodId:data.finprodId,
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:150, sortField:'tradeId',title: '交易编号'}
            ,{field:'outTradeId', width:150, sortField:'outTradeId',title: '外部交易编号'}
            ,{field:'portfolioId', width:150, title: '产品代码',sortField:'portfolioId'}
            ,{field:'portfolioName', width:150, title: '产品名称',sortField:'portfolioName'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'ps', title: '交易类型',visible:false}
            ,{field:'tradeMarketStr', width:150, title: '交易场所',sortField:'tradeMarket'}
            ,{field:'tradeMarket', title: '交易场所',visible:false}
            ,{field:'counterId', title: '交易对手',width:100}
            ,{field:'term', width:150, title: '回购期限',type:'number',sortField:'term'}
            ,{field:'tradeDateStr', width:150,  title: '交易日',sortField:'tradeDate'}
            ,{field:'tradeDate',title: '交易日',visible:false}
            ,{field:'vdateStr', width:150,  title: '起息日',sortField:'vdate'}
            ,{field:'vdate', title: '起息日',visible:false}
            ,{field:'mdateStr', width:150,  title: '到期日',sortField:'mdate'}
            ,{field:'mdate', title: '到期日',visible:false}
            ,{field:'prinAmt', width:150, title: '交易本金',type:'number',sortField:'prinAmt'}
            ,{field:'rebackPrice', width:150, title: '回购利率%',type:'number',sortField:'rebackPrice'}
            ,{field:'intAmt', width:150, title: '应记利息',type:'number',sortField:'intAmt'}
            ,{field:'settleAmt', width:150, title: '结算金额',type:'number',sortField:'settleAmt'}
            ,{field:'ccyFmt', width:150, title: '币种',sortField:'ccy'}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'trdAcctId', title: '资金账户',width:100}
            ,{field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width : 120,
                align : 'center',type : 'action', buttons : [ 'edit', 'delt' ,
                    {text: '<i class="iconfont icon-submit"></i>', clas: 'confirm', title: '存续期交易'}]}
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
        $('#TrdRepoValuationTrade001_btn_search').click(function() {
            table.load();
        });
        // 绑定批量删除事件
        $('#TrdRepoValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function() {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade001/delete',
                        object,
                        "TrdRepoValuationTrade001_btn_delete",
                        "TrdRepoValuationTrade002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
        $('#TrdRepoValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        $('#TrdRepoValuationTrade001_btn_add').click(
                function() {
                    Joyin.dialog(JoyinUtil.getLabel("TrdRepoValuationTrade002"), basePath
                            + "valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade002/init", "99%", "99%", {
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("质押式回购交易-修改"),
                    basePath + "valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade002/init",//打开新界面
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade001/delete',
                    object,
                    "TrdRepoValuationTrade001_btn_delete",
                    "TrdRepoValuationTrade002",
                    function() {
                        table.reload();
                        }
                    );
                });
            });
        });

        /**
         * 绑定存续期交易事件
         */
        table.bind('click', '.confirm', function(data, index) {
            var object = {selectedItem: [data], operateType:"confirm"};//请求参数
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
                Joyin.dialog(JoyinUtil.getLabel("存续期交易"),
                    basePath + "valuation/trademanage/trdrepovaluationtrade/TrdRepoValuationTrade003/init",//打开新界面
                    "98%", "98%", {//请求的page_model
                        tradeId:data.tradeId,  //金融产品代码
                        operateType:"confirm"
                    },function(){
                        table.reload();//回调函数，表格刷新
                });
            });
        });
    });
}());