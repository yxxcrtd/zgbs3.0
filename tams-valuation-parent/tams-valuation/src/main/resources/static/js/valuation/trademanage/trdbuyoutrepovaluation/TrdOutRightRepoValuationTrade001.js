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
            var ps=data.ps;
            if(ps=="03" || ps=="04"){
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade002/init",
                    "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                        portfolioId:data.portfolioId,
                        entrustId:data.entrustId,
                        finprodId:data.finprodId,
                        tradeId:data.tradeId});
            }else { //展示到期确认页面
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade003/init",
                    "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                        portfolioId:data.portfolioId,
                        entrustId:data.entrustId,
                        finprodId:data.finprodId,
                        tradeId:data.tradeId});
            }

        },

        cols:[
            {field:'tradeId', width:150, sortField:'tradeId',title: '交易编号'}
            ,{field:'outTradeId', width:150, sortField:'outTradeId',title: '外部交易编号'}
            ,{field:'portfolioId', width:150, title: '产品代码',sortField:'portfolioId'}
            ,{field:'portfolioName', width:150, title: '产品名称',sortField:'portfolioName'}
            ,{field:'finprodMarketId', width:150, title: '标的资产代码',sortField:'finprodMarketId'}
            ,{field:'finprodName', width:150, title: '标的资产名称',sortField:'finprodName'}
            ,{field:'term', width:150, title: '回购期限',sortField:'term',type:'number'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'ps', title: '交易类型',visible:false}
            ,{field:'tradeDateStr', width:150,  title: '交易日',sortField:'tradeDate'}
            ,{field:'tradeDate',title: '交易日',visible:false}
            ,{field:'vdateStr', width:150,  title: '起息日',sortField:'vdate'}
            ,{field:'vdate', title: '起息日',visible:false}
            ,{field:'mdateStr', width:150,  title: '到期日',sortField:'mdate'}
            ,{field:'mdate', title: '到期日',visible:false}
            ,{field:'prinAmt', width:150, title: '券面总额(万元)',type:'number',sortField:'prinAmt',digit:2}
            ,{field:'rebackPrice', width:150, title: '回购利率%',type:'number',sortField:'rebackPrice'}
            ,{field:'fprice', width:150, title: '首次全价总额',type:'number',sortField:'fprice'}
            ,{field:'mfprice', width:150, title: '到期全价总额',type:'number',sortField:'mfprice'}
            ,{field:'settleAmt', width:150, title: '结算金额',type:'number',sortField:'settleAmt'}
            ,{field:'ccyFmt', width:150, title: '币种',sortField:'ccy'}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'trdAcctId', title: '资金账户',width:100}
            ,{field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width:'120',
                align : 'left',type : 'action', buttons : [ 'edit', 'delt' ,'confirm'],
                    // {text: '<i id="daoqi" class="iconfont icon-submit"></i>', clas: 'confirm', title: '到期'}],
            render: function (data, type, row, meta) {
                if (row.ps == 'T07')
                    return null;
                else
                    return '<i class="btn layui-icon edit" title="编辑">&#xe642;</i><i class="btn layui-icon delt" title="删除">&#xe640;</i><i class="btn layui-icon confirm" title="确认">&#x1005;</i>';
            }

            }
        ],
        //loadonce:true
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
        $('#TrdOutRightRepoValuationTrade001_btn_search').click(function() {
            table.load();
        });
        // 绑定批量删除事件
        $('#TrdOutRightRepoValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade001/delete',
                        object,
                        "TrdOutRightRepoValuationTrade001_btn_delete",
                        "TrdOutRightRepoValuationTrade002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });
        
        $('#TrdOutRightRepoValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        $('#TrdOutRightRepoValuationTrade001_btn_add').click(
                function() {
                    Joyin.dialog(JoyinUtil.getLabel("TrdRepoValuationTrade002"), basePath
                            + "valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade002/init", "99%", "99%", {
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.dialog(JoyinUtil.getLabel("买断式回购交易-修改"),
                    basePath + "valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade002/init",//打开新界面
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
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数

                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001", JoyinUtil.getLabel("SysCommon.delete")), function () {
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade001/delete',
                        object,
                        "TrdOutRightRepoValuationTrade001_btn_delete",
                        "TrdOutRightRepoValuationTrade002",
                        function () {
                            table.reload();
                        }
                    );
                });
            });
        });

        /**
         * 绑定确认事件
         */
        table.bind('click', '.confirm', function(data, index) {
            var object = {selectedItem: [data], operateType:"confirm"};//请求参数
            // Joyin.ajaxJson(basePath + 'valuation/trademanage/trdlendvaluationtrade/TrdLendValuationTrade002/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.dialog(JoyinUtil.getLabel("到期确认"),
                basePath + "valuation/trademanage/trdbuyoutrepovaluation/TrdOutRightRepoValuationTrade003/init",//打开新界面
                "98%", "98%", {//请求的page_model
                    tradeId:data.tradeId,  //金融产品代码
                    operateType:"confirm"
                },function(){
                    table.reload();//回调函数，表格刷新
                });
            // });
        });

    });
}());