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
            if(data.ps == 'T24'){
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade003/init",
                    "95%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                        tradeId:data.tradeId,ps:data.ps});
            }else{
                Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                    basePath + "valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade002/init",
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
            ,{field:'tradeDateStr',width:150, title: '交易日期',sortField:'tradeDate'}
            ,{field:'tradeDate',  title: '交易日期', visible: false}
            ,{field:'settleDateStr',width:150, title: '结算日期',sortField:'tradeDate'}
            ,{field:'settleDate',  title: '结算日期', visible: false}
            ,{field:'tradeAmt', width:150, title: '交易金额',type:'money'}
            ,{field:'dealNum', width:150, title: '交易数量',type:'number'}
            ,{field:'ccyFmt', title: '币种',sortField:"ccy",width:180}
            ,{field:'ccy', title: '币种',visible:false}
            ,{field:'trdMyAcctNo', title: '资金账户',width:180}
            ,{field:'dataSourceStr', title: '数据来源',sortField:"dataSource",width:150}
            ,{field:'dataSource', title: '数据来源',visible:false}
            ,{field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width : 120,
                align : 'center',type : 'action', buttons : [ 'edit', 'delt']}
        ]
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {selectedItem: [data], operateType:"edit"};//请求参数
        if(data.ps == 'T24'){
            Joyin.dialog(JoyinUtil.getLabel("计划类交易-修改"),
                basePath + "valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade003/init",//打开新界面
                "95%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                    tradeId:data.tradeId  //金融产品代码
                    ,ps:data.ps
                },function(){
                    table.reload();//回调函数，表格刷新
                });
        }else{
            Joyin.dialog(JoyinUtil.getLabel("计划类交易-修改"),
                basePath + "valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade002/init",//打开新界面
                "95%", "90%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
                    tradeId:data.tradeId  //金融产品代码
                    ,ps:data.ps
                },function(){
                    table.reload();//回调函数，表格刷新
                });
        }

    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
            WorkFlow.doDeleteList(
                basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade001/delete',
                object,
                "TrdPlanValuationTrade001_btn_delete",
                "TrdPlanValuationTrade002",
                function() {
                    table.reload();
                }
            );
        });
    });

    /**
     * 导出选中的表格数据
     */
    var exportExcel = function() {
        var paramObj = getPageParams();//请求参数json格式
        Joyin.exportExcel('export', paramObj);//导出（共通）
    };
    
    /**
     * 画面加载完成
     */
    $(function() {
        $('#TrdPlanValuationTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdPlanValuationTrade002"),
                    basePath+ "valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade002/init",
                        "95%", "90%", {pageModel:PAGE_MODEL_NEW});
        });

        // 绑定新建事件
        $('#TrdPlanValuationTrade001_btn_add1').click(function() {
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
            Joyin.dialog(JoyinUtil.getLabel("计划类资产赎回到账（<i class='iconfont must' style='display:inline'>*</i>必填）"),
                basePath + "valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade003/init",
                "95%", "90%",
                {tradeId:tradeId ,ps:ps}
                ,function(){
                    table.reload();
                });
        });

        // 绑定删除事件
        $('#TrdPlanValuationTrade001_btn_delete').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }

            // 启动删除流程
            var object = {selectedItem: selArr, operateType:"delete"};
            Joyin.ajaxJson(basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade001/check', object, function(result) {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                    WorkFlow.doDeleteList(
                        basePath + 'valuation/trademanage/trdplanvaluationtrade/TrdPlanValuationTrade001/delete',
                        object,
                        "TrdPlanValuationTrade001_btn_delete",
                        "TrdPlanValuationTrade002",
                        function() {
                            table.reload();
                        }
                    );
                });
            });
        });
       
        //导出
        $('#TrdPlanValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        //查询
        $('#TrdPlanValuationTrade001_btn_search').click(function(){
            table.load();
        });

    });
}());