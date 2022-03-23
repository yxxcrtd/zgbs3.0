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
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    tradeId:data.tradeId,ps:data.ps});
        },
        cols:[
            {field:'tradeId', width:180, title: '交易编号'}
            ,{field:'prodId', width:150, title: '产品代码'}
            ,{field:'prodName', width:150, title: '产品名称'}
            ,{field:'assetId', width:150, title: '资产代码'}
            ,{field:'assetAbbr', width:150, title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'tradeDateStr',width:150, title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate',  title: '交易日期', visible: false}
            ,{field:'bonusAmt', width:150, title: '分红金额',type:'number'}
            ,{field:'unitBonusUnitValue', width:150, title: '单位分红净值',type:'number'}
            ,{field:'dealNumReInvest', width:150, title: '交易份额',type:'number'}
            ,{field:'ccyFmt', title: '币种',sortField:"ccy",width:180}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'settleAmt', width:150, title: '结算金额',type:'money'}
            ,{field:'myAcctNo', title: '资金账户',width:180}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
        ]
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade001/check', object, function(result) {//请求路径，请求参数回调函数
            Joyin.dialog(JoyinUtil.getLabel("计划类存续期管理-修改"),
                basePath + "valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/init",//打开新界面
                "95%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                    tradeId:data.tradeId  //金融产品代码
                    ,ps:data.ps
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(
                    basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade001/delete',
                    object,
                    "TrdJihuaBonusValuationTrade001_btn_delete",
                    "TrdJihuaBonusValuationTrade002",
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
        $('#TrdJihuaBonusValuationTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdJihuaBonusValuationTrade002"),
                    basePath+ "valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade002/init",
                        "95%", "90%", {pageModel:PAGE_MODEL_NEW});
        });
       
        //导出
        $('#TrdJihuaBonusValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        //查询
        $('#TrdJihuaBonusValuationTrade001_btn_search').click(function(){
            table.load();
        });

        // 绑定删除事件
        $('#TrdJihuaBonusValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdjihuabonusvaluationtrade/TrdJihuaBonusValuationTrade001/delete',
                        object,
                        "TrdJihuaBonusValuationTrade001_btn_delete",
                        "TrdJihuaBonusValuationTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });

    });
}());