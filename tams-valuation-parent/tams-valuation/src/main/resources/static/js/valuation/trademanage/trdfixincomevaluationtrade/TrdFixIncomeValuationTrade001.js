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
            ps: $.trim($('#ps').val()),
            dataSource:$.trim($('#dataSource').val())
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
                    basePath + "valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:180, title: '交易编号'}
            ,{field:'outTradeId', width:150, title: '外部成交编号'}
            ,{field:'prodId', width:150, title: '产品代码'}
            ,{field:'prodName', width:220, title: '产品名称'}
            ,{field:'assetId', width:150, title: '资产代码'}
            ,{field:'assetName', width:220, title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'tradeDateStr',width:150, title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate', title: '交易日期', visible: false}
            ,{field:'effDateStr',width:150, title: '交割日期',sortField:'effDate'}
            ,{field:'effDate', title: '交割日期', visible: false}
            ,{field:'invAimStr', width:240, title: '会计分类'}
            ,{field:'capital', width:150, title: '本金金额',type:'number'}
            ,{field:'cpriceAmt', width:150, title: '净价总额',type:'number'}
            ,{field:'intAmt', width:150, title: '应收利息总额',type:'number'}
            ,{field:'ccyFmt', title: '币种',sortField:"ccy",width:180}
            ,{field:'ccy',  title: '币种' ,visible: false}
            ,{field:'settleAmt', width:150, title: '结算金额',type:'number'}
            ,{field:'myAcctNo', width:200, title: '资金账户'}
            ,{field:'dataSourceStr', title: '数据来源',sortField:"dataSource",width:150}
            ,{field:'dataSource', title: '数据来源',visible:false}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });

    /**
     * 导出选中的表格数据
     */
    var exportExcel = function() {
        var paramObj = getPageParams();//请求参数json格式
        Joyin.exportExcel('export', paramObj);//导出（共通）
    };

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        Joyin.dialog(JoyinUtil.getLabel("固收类交易-修改"),
            basePath + "valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002/init",//打开新界面
            "95%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                tradeId:data.tradeId  //金融产品代码
            },function(){
                table.reload();//回调函数，表格刷新
            });
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
            WorkFlow.doDeleteList(
                basePath + 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade001/delete',
                object,
                "TrdFixIncomeValuationTrade001_btn_delete",
                "TrdFixIncomeValuationTrade002",
                function() {
                    table.reload();
                }
            );
        });
    });
    
    /**
     * 画面加载完成
     */
    $(function() {
        $('#TrdFixIncomeValuationTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdFixIncomeValuationTrade002"),
                    basePath+ "valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade002/init",
                        "95%", "90%", {pageModel:PAGE_MODEL_NEW});
        });
       
        //导出
        $('#TrdFixIncomeValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        //查询
        $('#TrdFixIncomeValuationTrade001_btn_search').click(function(){
            table.load();
        });

        // 绑定删除事件
        $('#TrdFixIncomeValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdfixincomevaluationtrade/TrdFixIncomeValuationTrade001/delete',
                        object,
                        "TrdFixIncomeValuationTrade001_btn_delete",
                        "TrdFixIncomeValuationTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());