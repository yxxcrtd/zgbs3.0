$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var object = $.serializeToObject('#searchForm');
//      object.finprodId = $('#finprodId_hidden').val();
        if(object.portfolioId){
            object.portfolioId = object.portfolioId.join(',');
        }
        return object;
    };
    /**
     * 定义数据表格
     */

    var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        dblclick:function(table, data) {
            data.operateType = "dblclick";
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("EtrConBond002.refrence"),
                JoyinUtil.getRealPath("/invest/entrust/etrconbond/EtrConBond002/init"),
                "98%", "95%", data,
                function(){table.reload();});
        },
        /**
         * 定义表头
         */
        cols:[
            {field:'busEntrustStatus', title: 'EtrInvestBondInfo001.entrustStatus', width:'10%'}
            ,{field:'entrustId',visible:false}
            ,{field:'portfolioName', title: 'EtrInvestBondInfo001.portfolioName', width:'10%',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link portfolioIdLink' style='cursor: pointer;'>"+row.portfolioName+"</ a>";
                },sortField:"portfolioName"
            }
            ,{field:'finprodId', visible:false}
            ,{field:'finprodMarketId', title: 'EtrInvestBondInfo001.finprodId', width:'10%',
                render : function(data, type, row, meta) {
                    return "<a  class='pro-link finprodIdLink' style='cursor: pointer;'>"+data+"</ a>";
                },sortField:"finprodMarketId"
            }
            ,{field:'finprodName', title: 'EtrConBond001.finprodName', width:'10%'}
            ,{field:'purchBondTotal', title:'EtrConBond001.purchBondTotal1', digit: 4 , width:'10%'}
            ,{field:'winlotteryAmt', title:'EtrConBond001.winlotteryAmt1', digit: 4 , width:'10%'}
            ,{field:'entrustDate', title:'EtrConBond001.purchDate',width:'10%',type:'date'}
            ,{field:'purchModle',visible:false}
            ,{field:'purchModleStr', title:'EtrConBond001.purchModle',width:'10%'}
            ,{field:'purchType', visible:false}
            ,{field:'purchTypeStr', title:'EtrConBond001.purchType',width:'10%'}
            ,{field:'TradeMarket', visible:false}
            ,{field:'busTradeMarket', title: 'EtrInvestBondInfo001.tradeMarket', width:'10%'}
            ,{field:'ccy',visible:false}
            ,{field:'busCcy', title: 'EtrInvestBondInfo001.ccy', width:'10%',sortField:"ccy"}
            ,{field:'remark', title: 'EtrInvestBondInfo001.remark', width:'10%',sortable:false}
            ,{field:'invManagerId', title: 'EtrInvestBondInfo001.invManagerId', width:'10%',sortField:"invManagerId"}
            ,{field:'nextUser', title: 'EtrCommon.nextUser', width:'10%',sortable:false}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit']}
        ]
    });

    /**
     * 画面加载完成
     */
    $(function() {
        $("#EtrConBond001_btn_search").click(function () {
            table.load();
        });
        //add by gaopeng 20191223 导入按钮 start
        /**
         * 导入按钮
         */
        $('#EtrConBond001_btn_import').click(function() {
            Joyin.uploadFile({pageId : 'EtrConBond003MTM'}, function(fileName) {
                Joyin.ajax(basePath + 'invest/entrust/etrconbond/EtrConBond003/checkImportData', {fileName: fileName}, function(checkResult) {
                    setTimeout( function () {
                        if(checkResult.data.errorMsg){
                            Joyin.error(checkResult.data.errorMsg);
                            return;
                        }
                        Joyin.confirm(checkResult.data.confirmMsg,function() {
                            var result = {};
                            result.tempList = checkResult.data.correctData;
                            Joyin.dialog(JoyinUtil.getLabel("EtrConBond003"),
                                JoyinUtil.getRealPath("/invest/entrust/etrconbond/EtrConBond003/init"),
                                "98%", "95%", result,
                                function(){table.reload();});
                        });
                    },1000);
                });
            },function() {
                table.reload();
            });
        });
        //add by gaopeng 20191223 导入按钮 end
        /**
         * 导出按钮
         */
        $('#EtrConBond001_btn_export').click(function() {
            exportExcel();
        });
        //新建事件
        $("#EtrConBond001_btn_add").click(function () {
            Joyin.dialog(JoyinUtil.getLabel("可转债指令新建"),
                basePath + "invest/entrust/etrconbond/EtrConBond002/init",
                "98%", "98%"); table.load(); });


        //撤回事件
        $('#EtrConBond001_btn_revoke').click(function(){

            if (table != null && table != '') {
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }else {
                var object = {selectedItem: selArr, operateType:"recall"};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond001/check', object, function(result) {
                    Joyin.confirm(JoyinUtil.getMessage("BUSS_COM_C_0032","撤销"), function(){
                        Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond001/recall', object, function(result) {
                            table.reload();
                        });
                    });
                });
            }

        });
        //关闭事件
        $('#EtrConBond001_btn_close').click(function(){

            if (table != null && table != '') {
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }else {
                var object = {selectedItem: selArr, operateType:"close"};
                Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond001/check', object, function(result) {
                    Joyin.confirm(JoyinUtil.getMessage("ETR_C_0002"), function(){
                        Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond001/close', object, function(result) {
                            table.reload();
                        });
                    });
                });
            }

        });
        /**
         * 导出
         */
        var exportExcel = function(){
            Joyin.exportExcel(basePath+"invest/entrust/etrconbond/EtrConBond001/export", getSearchParams());
        };
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
            "95%", "80%",
            param);
        // Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
        //     basePath
        //     + "invest/asset/bond/FinBondTargetAsset002/init",
        //     "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,
        //         finprodId:row.finprodId  //金融产品代码
        //     });
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.operateType = "edit";
        data.pageModel=PAGE_MODEL_EDIT;
        Joyin.ajaxJson(basePath + 'invest/entrust/etrconbond/EtrConBond002/check', data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("EtrConBond002.edit"),
                JoyinUtil.getRealPath("/invest/entrust/etrconbond/EtrConBond002/init"),
                "98%", "95%", data,
                function(){table.reload();});
        });
    });
}());