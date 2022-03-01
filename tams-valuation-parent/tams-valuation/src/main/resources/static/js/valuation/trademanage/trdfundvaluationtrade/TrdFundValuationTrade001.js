$(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var createUser=$("#createUser").val()
        if(createUser){
            createUser=createUser.join(",");
        }
        //产品代码 多选择
        var portfolioIdTemp = $('#portfolioId').val();
        var portfolioId = null;
        if(portfolioIdTemp != null){
            portfolioId = portfolioIdTemp.join(",");
        }

        //资产代码  多选择
        var finprodIdTemp = $('#finprodId').val();
        var finprodId = null;
        if(finprodIdTemp != null){
            finprodId = finprodIdTemp.join(",");
        }

        //交易类型  多选择
        var psTemp = $('#ps').val();
        var ps = null;
        if(psTemp != null && psTemp != ''){
            ps = psTemp.join(",");
        }
        return {
            tradeId:$('#tradeId').val(),
            portfolioId:portfolioId,
            finprodType:$('#finprodType').val(),
            finprodId:finprodId,
            ps:ps,
            tradeMarket:$('#tradeMarket').val(),
            createUser:createUser
        };
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
                    basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/init",
                    "98%", "98%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    portfolioId:data.portfolioId,
                    finprodId:data.finprodId,
                    tradeId:data.tradeId});
        },
        cols:[
            {field:'tradeId', width:180, title: '交易编号'}
            ,{field:'outTradeId', width:150, title: '外部成交编号'}
            ,{field:'portfolioId', width:150, title: '产品代码'}
            ,{field:'portfolioName', width:150, title: '产品名称'}
            ,{field:'finprodId', width:150, title: '资产代码'}
            ,{field:'finprodName', width:150, title: '资产名称'}
            ,{field:'psStr', width:150, title: '交易类型',sortField:'ps'}
            ,{field:'tradeMarketStr', width:150, title: '交易场所',sortField:'tradeMarket'}
            ,{field:'tradeDate', type:'date' ,width:150, title: '交易日', visible: false}
            ,{field:'tradeDateStr',width:150, title: '交易日'}
            ,{field:'tradeAmt', width:150, title: '交易金额',type:'number'}
            ,{field:'nums', width:150, title: '交易份额',type:'number'}
            ,{field:'unitValue', width:150, title: '单位净值',type:'number'}
            ,{field:'ccy', width:150, title: '币种',sortField:'ccy'}
            ,{field:'tradeDateStr',width:150, title: '净值日期'}
            ,{field:'mdateSettleAmt', width:150, title: '结算金额',type:'number'}
            ,{field:'assetAccount', width:150, title: '资金账户'}
            ,{field:'operation', title: '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', fixed:'right',width : 120,
                align : 'center',type : 'action', buttons : [ 'edit', 'delt' ,
                    {text: '<i class="iconfont icon-submit"></i>', clas: 'confirm', title: '赎回到账'}]}
        ]
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
                        "98%", "98%", {pageModel:PAGE_MODEL_NEW});
        });
       
        //导出
        $('#TrdFundValuationTrade001_btn_export').click(function() {
            exportExcel();
        });
        //查询
        $('#TrdFundValuationTrade001_btn_search').click(function(){
            table.load();
        });

        /**
         * 绑定编辑事件
         */
        table.bind('click', '.edit', function(data, index) {
            data.pageModel = PAGE_MODEL_EDIT;
            var object = {selectedItem: [data], operateType:"edit"};//请求参数
            Joyin.dialog(JoyinUtil.getLabel("基金交易-修改"),
                basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade002/init",//打开新界面
                "98%", "98%", {pageModel:PAGE_MODEL_EDIT,//请求的page_model
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


        /**
         * 绑定确认事件
         */
        table.bind('click', '.confirm', function(data, index) {
            var object = {selectedItem: [data], operateType:"confirm"};//请求参数
            Joyin.dialog(JoyinUtil.getLabel("基金交易-赎回到账"),
                basePath + "valuation/trademanage/trdfundvaluationtrade/TrdFundValuationTrade003/init",//打开新界面
                "98%", "98%", {//请求的page_model
                    tradeId:data.tradeId,  //金融产品代码
                    operateType:"confirm"
                },function(){
                    table.reload();//回调函数，表格刷新
                });
        });
    });
}());