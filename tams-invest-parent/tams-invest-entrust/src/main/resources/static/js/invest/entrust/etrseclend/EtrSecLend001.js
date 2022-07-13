/**
 * 债券借贷投资委托查询
 */
(function () {
    /**
     * 获取查询参数
     */
    var searchForm = function () {
        var data = $.serializeToObject('#searchForm');
        if ($('#portfolioId').val()){
            data.portfolioId = $('#portfolioId').val().join(',');
        }
        return data;
    }

    /***
     * 定义表格
     */
    var table = $('#dataTable').joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return searchForm();
            }
        },
        dblclick: function (table, data) {
            data.operateType = "dblclick";
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("EtrSecLend.refrence"),
                JoyinUtil.getRealPath("invest/entrust/etrseclend/EtrSecLend002/init"),
                "98%", "95%", data,
                function(){table.reload();});
        },
        cols: [
            {field:'busEntrustStatus', title: 'EtrSecLend.entrustStatus', width:'8%',sortField:"entrustStatus"}
            ,{field:'invManagerId', title: 'EtrSecLend.invManagerId', width:'10%',sortField:"invManagerId"}
            ,{field:'entrustId',visible:false}
            ,{field:'targetFinprodId',visible:false}
            ,{field:'portfolioId', title: 'EtrSecLend.portfolioId', width:'10%',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link portfolioIdLink' style='cursor: pointer;'>"+row.portfolioId+"</ a>";
                },sortField:"portfolioId"
            }
            ,{field:'portfolioName', title: 'EtrSecLend.portfolioName', width:'10%',sortField:"portfolioName"}
            ,{
                field: 'finprodId', title: 'EtrSecLend.targetFinprodId', width: '10%',
                render: function (data, type, row, meta) {
                    return "<a  class='pro-link finprodIdLink' style='cursor: pointer;'>" + row.finprodId + "</ a>";
                }, sortField: "finprodId"
            }
            ,{field:'targetFinprodName', title: 'EtrSecLend.targetFinprodName', width:'10%',sortField:"targetFinprodName"}
            ,{field:'busBusiPs', title: 'EtrSecLend.busiPs', width:'10%',sortField:"busiPs"}
            ,{field:'busTradeMarket', title: 'EtrSecLend.tradeMarket', width:'10%',sortField:"tradeMarket"}
            ,{field:'totalAmt',type:'number',title: 'EtrSecLend.totalAmt1', width:'10%', digit: 4, sortField:"totalAmt"}
            ,{field:'busCcy', title: 'EtrSecLend.ccy', width:'10%',sortField:"ccy"}
            ,{field:'vdate', title: 'EtrSecLend.vdate', width:'10%',type:'date',sortField:"vdate"}
            ,{field:'mdate', title: 'EtrSecLend.mdate', width:'10%',type:'date',sortField:"mdate"}
            ,{field:'finishAmt',type:'number', title: 'EtrSecLend.finishAmt', width:'10%',sortField:"finishAmt"}
            ,{field:'transactioncom', title: 'EtrSecLend.transactioncom',render : function(data, type, row, meta){
                    return "<div class='layui-progress layui-progress-big' style='margin-top:5px;'><div class='layui-progress-bar layui-bg-green' style='width:"+(row.transactioncom*100).toFixed(2)+"%'><span class='layui-progress-text'>"+(row.transactioncom*100).toFixed(2)+"%</span></div></div>";
                }, width:'10%',sortable:false}
            ,{field:'remark', title: 'EtrSecLend.remark', width:'10%',sortField:"remark"}
            ,{field:'nextUser', title: 'EtrCommon.nextUser', width:'10%',sortable:false}
            ,{field:'action', title: '操作' , type:'action',buttons: ['edit'] , width:'10%'}
        ],
    });

    /**
     * 组合代码链接
     */
    table.bind('click','.portfolioIdLink', function(row) {
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
            "98%", "90%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                portfolioId:row.portfolioId});//主键组合代码
    });
    /**
     * 资产代码链接
     */
    table.bind('click','.finprodIdLink', function(row) {
        //在新建界面进行显示单行的详细信息
        let finprodId = row.finprodId
        var url = "home.html?a=a&finprodMarketId=" + finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a"; // 默认债券标的
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
            finprodId: finprodId
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "98%", "98%",
            param);
        // Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
        //     basePath
        //     + "invest/asset/bond/FinBondTargetAsset002/init",
        //     "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
        //         finprodId:row.targetFinprodId,  //金融产品代码
        //         //valDate:data.valDate,//估值日
        //         //finprodMarketId:data.finprodMarketId//市场代码
        //     });
    });

    /***
     * 新建
     */
    var showDialog = function () {
        Joyin.dialog(JoyinUtil.getLabel('EtrSecLend.add'), basePath + 'invest/entrust/etrseclend/EtrSecLend002/init',
            '98%', '95%',function () { table.reload(); })
    }
    /**
     * 导出
     */
    var exportExcel = function () {
        Joyin.exportExcel(basePath + 'invest/entrust/etrseclend/EtrSecLend001/export', searchForm());
    }
    /**
     * 编辑
     */
    table.bind('click', '.edit', function (data, index) {
        data.operateType = "edit";
        data.pageModel=PAGE_MODEL_EDIT;
        data.calendarId="01";
        data.settleSpreed="01";
        data.tradeMarket="01";
        Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend001/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("EtrSecLend002.edit"),
                JoyinUtil.getRealPath("invest/entrust/etrseclend/EtrSecLend002/init"),
                "98%", "95%", data,
                function(){table.reload();});
        });
    });

    /**
     * 绑定事件
     */
    $(function () {
        /**
         * 查询按钮
         */
        $('#EtrSecLend001_btn_search').click(function () {
            table.load();
            $('#EtrSecLend001_btn_close').show();
            $('#EtrSecLend001_btn_revoke').show();
        });
        /**
         * 导出按钮
         */
        $('#EtrSecLend001_btn_export').click(function () {
            exportExcel();
        });
        /**
         * 新建
         */
        $('#EtrSecLend001_btn_add').click(function () {
            showDialog();
        });
        /**
         * 关闭委托
         */
        $('#EtrSecLend001_btn_close').click(function () {
            if (table){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }else {
                var object = {selectedItem: selArr, operateType:"close"};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend001/check', object, function(result) {
                    Joyin.confirm(JoyinUtil.getMessage("ETR_C_0002"), function(){
                        Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend001/close', object, function(result) {
                            table.reload();
                        });
                    });
                });
            }

        });
        /**
         * 撤销
         */
        $('#EtrSecLend001_btn_revoke').click(function() {
            if (table != null && table != '') {
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }else {
                var object = {selectedItem: selArr, operateType:"recall"};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend001/check', object, function(result) {
                    Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_C_0032","撤销"), function(){
                        Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend001/recall', object, function(result) {
                            table.reload();
                        });
                    });
                });
            }
        });
    });

}());
