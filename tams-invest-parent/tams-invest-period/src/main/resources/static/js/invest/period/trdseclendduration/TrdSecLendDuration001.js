(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var pageParam = {
            finprodId: $('#finprodId').val()
            ,tradeDateStart: $('#mdate').startDate()
            ,tradeDateEnd: $('#mdate').endDate()
        };
        pageParam.statusValid = $("#statusValid").val();
        pageParam.ps = $("#ps").val();
        if ($("#portfolioId").val()) {
            pageParam.portfolioId = $("#portfolioId").val().join(",");
        } else {
            pageParam.portfolioId = null;
        }
        pageParam.busiCode = $("#busiCode").val();
        
        return pageParam;
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
            data.pageModel=PAGE_MODEL_REFERENCE;
            Joyin.dialog(JoyinUtil.getLabel("TrdSecLendDuration002.reference"),
                    JoyinUtil.getRealPath("invest/period/trdseclendduration/TrdSecLendDuration002/init"),
                    "99%", "99%", data);
        },
        cols:[
             {field:'statusValidString',sortField:'statusValid', width:100, title: 'TrdSecLendDuration001.validStatus'}
            ,{field: 'busiCode', width: 220, sortField:'busiCode', title: 'TrdSecLendDuration001.busiCode'}
        	,{field:'portfolioId',sortField:'PORTFOLIO_ID', width:140, title: 'TrdSecLendDuration001.portfolioId',
                render : function(data, type, row, meta) {
                    return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                }
        	}
        	,{field:'portfolioName', sortField:'PORTFOLIO_NAME',width:140, title: 'TrdSecLendDuration001.portfolioName'}
            ,{field:'psText', sortField:'ps',width:100, title: 'TrdSecLendDuration001.ps'}
        	,{field:'finprodId',sortField:'FINPROD_ID', width:180, title: 'TrdSecLendDuration001.finprodId',
                render : function(data, type, row, meta) {
                    return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
                }
        	}
            // ,{field:'finprodMarketId',sortField:'FINPROD_MARKET_ID', width:180, title: 'TrdSecLendDuration001.finprodId',
            //     render : function(data, type, row, meta) {
            //         return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodMarketId + "</a>";
            //     }
            // }
        	,{field:'finprodName', sortField:'finprodName',width:180, title: 'TrdSecLendDuration001.finprodName'}
        	,{field:'shareAmt',sortField:'shareAmt', width:180, title: 'TrdSecLendDuration001.tradeAmt', type: 'money'}
            ,{field:'vdate',sortField:'VDATE', width:140, title: 'TrdSecLendDuration001.vdate', type: 'date'}
            ,{field:'mdate',sortField:'MDATE', width:140, title: 'TrdSecLendDuration001.mdate', type: 'date'}
            ,{field:'settleDate',sortField:'SETTLE_DATE', width:140, title: 'TrdSecLendDuration001.settleDate', type: 'date'}
            ,{field:'mYield',sortField:'INT_RATE', width:120, title: 'TrdSecLendDuration001.mYield',type:'number', digit:6}
            ,{field:'mTradeAmt',sortField:'PRIN', width:120, title: 'TrdSecLendDuration001.mTradeAmt', type: 'money'}
        	,{field:'validStatus', visible: false}
        	,{field:'ps', visible: false}
            ,{field:'tradeMarket', visible: false}
            ,{field:'ccy', visible: false}
            ,{field:'systemFinprodId', visible: false}
            ,{
                field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit']
                ,
                render: function (data, type, row, meta) {
                    if (row.statusValid != '01')
                        return "";
                    else
                        //modifiled by liupengfei for 多级法人改造 确认按钮权限控制 S
                        if(purviewElements.indexOf('_edit')!=-1){
                            return '<i class="btn layui-icon edit" title="确认">&#x1005;</i>';
                        }
                        //modifiled by liupengfei for 多级法人改造 确认按钮权限控制 E
                }
            }
        ],
        searchOnInit :REM_TYPE && REM_TYPE == 'R3S16',
        checkbox: true
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function (data, index) {
        data.pageModel = PAGE_MODEL_NEW;
        Joyin.dialog(JoyinUtil.getLabel("TrdSecLendDuration002.confirm"),
            basePath + "invest/period/trdseclendduration/TrdSecLendDuration002/init",
            "98%", "95%", data, function () {
                table.reload();
            });
    });

    var exportExcel = function(){
        Joyin.exportExcel('export', getPageParams());
    };
  
    /**
     * 点击组合代码链接跳转到该组合信息查看页面
     */
    table.bind('click','.portfolioId-link', function(row) {
        Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002"),
                basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
                "95%", "90%",
                { pageModel: PAGE_MODEL_REFERENCE,
                portfolioId: row.portfolioId,
                portfolioName: row.portfolioName });
    });
    /**
     * 点击资产代码链接跳转到该资产标的信息查看页面；
     */
    table.bind('click','.finprodId-link', function(row) {
        let finprodId = row.finprodId
        var url = "home.html?a=a&finprodMarketId=" + finprodId + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a"; // 默认债券标的
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
            finprodId: finprodId
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "95%", "90%",
            param);
        // Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002"),
        //         basePath + "invest/asset/bond/FinBondTargetAsset002/init",
        //         "95%", "90%",
        //         { pageModel:PAGE_MODEL_REFERENCE,
        //         finprodId: row.finprodId,
        //         finprodName: row.finprodName });
    });
    
    $(function () {
        // 绑定按钮事件
        $('#TrdSecLendDuration001_btn_search').click(function() {
            table.load();
            $('#TrdSecLendDuration001_btn_revoke').show();
        });
        
        $('#TrdSecLendDuration001_btn_export').click(function() {
            exportExcel();
        });
        /**
         * 撤销
         */
        $('#TrdSecLendDuration001_btn_revoke').click(function() {
            var rows = table.getAllCheckedRows();
            if (rows.length != 1) {
                Joyin.error(JoyinUtil.getMessage('请选择一条数据'))
                return;
            }
            var object = rows[0];
            object.operateType = "revoke";
            Joyin.ajax(basePath + 'invest/period/trdseclendduration/TrdSecLendDuration001/revokeCheck', object, function(result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    object.cancelRemark = result;
                    WorkFlow.doRevoke(basePath + 'invest/period/trdseclendduration/TrdSecLendDuration001/revoke', object, "TrdSecLendDuration001_btn_revoke", "TrdSecLendDuration002");
                });
            });

        });
    });
}());