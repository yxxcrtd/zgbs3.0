(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var pageParam = {
            entrustId: $('#entrustId').val()
            ,tradeId:$('#tradeId').val()
            ,portfolioId: $('#portfolioId').val()
            ,finprodId: $('#finprodId').val()
            ,tradeMarket: $('#tradeMarket').val()
            ,ps: $('#ps').val()
            ,purchType: $('#purchType').val()
            ,purchModle: $('#purchModle').val()
        	,validStatus: $('#validStatus').val()
            ,purchDateBegin: $('#purchDate').startDate()
            ,purchDateEnd: $('#purchDate').endDate()
            ,validStatus:$('#validStatus').val()
        };
        
        if ($("#portfolioId").val()) {
            pageParam.portfolioId = $("#portfolioId").val().join(",");
        } else {
            pageParam.portfolioId = null;
        }
        
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
            if(data.ps == PS_T04){
                Joyin.dialog(JoyinUtil.getLabel("TrdFundInvestTrade002.reference"),
                        JoyinUtil.getRealPath("invest/trade/conbond/TrdConBond002/init"),
                        "98%", "90%", data,
                        function(){table.reload();});
            }else 
            	/*if(data.ps == PS_T05)*/
            {
                Joyin.dialog(JoyinUtil.getLabel("TrdFundInvestTrade002.reference"),
                        JoyinUtil.getRealPath("invest/trade/conbond/TrdConBond003/init"),
                        "98%", "90%", data,
                        function(){table.reload();});
            }
        },
        cols:[
            {field:'validStatusStr',sortField:'validStatus', width:100, title: 'TrdConBond001.validStatus'}
            ,{field:'busiCode', sortField:'busiCode',width:200, title: 'TrdConBond001.tradeId'}
            ,{field:'entrustId',sortField:'entrustId', width:200, title: 'TrdConBond001.entrustId'}
        	,{field:'portfolioId',sortField:'portfolioId', width:150, title: 'TrdConBond001.portfolioId',
                render : function(data, type, row, meta) {
                    return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                }
        	}
        	,{field:'portfolioName', sortField:'portfolioName',width:140, title: 'TrdConBond001.portfolioName', visible: false}
        	,{field:'finprodId',sortField:'finprodId', width:180, title: 'TrdConBond001.finprodId',
                render : function(data, type, row, meta) {
                    return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodId + "</a>";
                }
        	}
        	,{field:'finprodName', sortField:'finprodName',width:140, title: 'TrdConBond001.finprodName', visible: false}
        	,{field:'psStr', sortField:'ps',width:100, title: 'TrdConBond001.ps'}
        	
        	,{field:'prinAmt',sortField:'prinAmt', width:120, digit:4,title: 'TrdConBond001.prinAmt1'}
        	,{field:'winlotteryAmt',sortField:'winlotteryAmt', width:120, digit:4, title: 'TrdConBond001.winlotteryAmt1', type: 'money'}
        	,{field:'purchDate',sortField:'purchDate', width:120, title: 'TrdConBond001.purchDate', type: 'date'}
        	,{field:'tradeDate', sortField:'tradeDate',width:100, title: 'TrdConBond001.tradeDate', type: 'date'}
        	,{field:'settleDate',sortField:'settleDate', width:100, title: 'TrdConBond001.settleDate', type: 'date'}
        	,{field:'purchModleStr',sortField:'purchModle', width:150, title: 'TrdConBond001.purchModle',visible: false}
        	,{field:'purchTypeStr',sortField:'purchType', width:150, title: 'TrdConBond001.purchType'}
        	,{field:'tradeMarketStr', sortField:'tradeMarket',width:130, title: 'TrdConBond001.tradeMarket'}
        	,{field:'ccyStr', sortField:'CCY',width:130, title: 'TrdConBond001.ccy'}
        	,{field:'remark', sortField:'',width:400, title: 'TrdConBond001.remark'}
        	,{field:'validStatus', visible: false}
        	,{field:'ps', visible: false}
            ,{field:'tradeMarket', visible: false}
            ,{field:'ccy', visible: false}
            /*,{field:'action', title: '操作' , type:'action',
                render: function (data, type, row, meta) {
                if (row.flg != '01')
                    return "";
                else
                    return '<i class="btn layui-icon edit" title="确认">&#x1005;</i>';
                }
            }*/
            ,{field:'operation', title: '操作' , type:'action',
                render: function (data, type, row, meta) {
                if (row.flg != '01')
                    return "";
                else
                	if(purviewElements.indexOf('_edit')!=-1){
                        return '<i class="btn layui-icon edit" title="确认">&#x1005;</i>';
                    }
                }
            }
        ],
        initSort: {field: 'tradeDate',type: 'desc'}
        //loadonce:true
    });
    
    /**
     * 交易确认
     */
    table.bind('click','.edit',function(data,index){
        data.operateType = "confirm";
        data.pageModel= PAGE_MODEL_NEW;
//        Joyin.ajaxJson(basePath + 'invest/trade/conbond/TrdConBond001/check', data, function(result1) {
            Joyin.dialog(JoyinUtil.getLabel("TrdConBond002"),
                    basePath + "invest/trade/conbond/TrdConBond003/init",
                    "98%", "90%", data,function(){table.reload();});
//        });
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
            "95%", "80%",
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
        $('#TrdConBond001_btn_search').click(function() {
            table.load();
            $('#TrdConBond001_btn_revoke').show();
        });
        
        $('#TrdConBond001_btn_export').click(function() {
            exportExcel();
        });
        
        $("#tradeMarket").on("change",function(){
            if ($("#tradeMarket").val() == '01') {
                $("#tradePlat").val('01').trigger('change');
            }
            else {
                $("#tradePlat").val('').trigger('change');
            }
        });
        //更新状态
        $("#TrdConBond001_btn_refresh").click(function(){
    		if(table!=null && table!=''){
                var arrs = table.getAllCheckedRows();//判断表格不为空，然后获取选择的数据
            }
            if (!arrs || arrs.length == 0) {//判断是否选择，如果没有则报错COMMON_E_0026
                Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0046"));
                return;
            }
            var tradeId = "";
            var tradeIdArr = new Array();
            $.each(arrs,function(v,t){
            	if(t.validStatus == STATUS_REVOKE){
            		tradeIdArr[v] = t.tradeId + STR_R;
            	}else{
            		tradeIdArr[v] = t.tradeId;
            	}
            });
            tradeId = tradeIdArr.join('/');
            var object = {tradeId:tradeId};
            Joyin.ajax(basePath + 'invest/trade/bond/TrdConBond001/refreshStatus', object, function() {
            	table.reload();
            });
    	});
        
        $('#TrdConBond001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdConBond002"), basePath
                            + "invest/trade/conbond/TrdConBond002/init", "99%", "99%", {
                        pageModel : PAGE_MODEL_NEW
                    }, function() {
                        table.reload();
                    });
        });
        
        
        /**
         * 撤销
         */
        $('#TrdConBond001_btn_revoke').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("TRDFUND_E_XXT_001"));
                return;
            }else if(!selArr || selArr.length > 1){
                var mes = "";
                for(var data in selArr){
                    mes = mes+ data.tradeId + ","
                }
                // COMMON_C_0029 不存在 提示错误 改为 COMMON_E_0058 yjm 20190904
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0058","产品代码",mes));
                return;
            }
            var object = {};
            object.selectedItem =selArr;
            object.operateType ="revoke";
            object.busiCode = object.selectedItem[0].busiCode;
            object.statusValid=object.selectedItem[0].validStatus;
            object.ps=object.selectedItem[0].ps;
            if( object.selectedItem[0].ps == PS_T05){
                Joyin.ajaxJson(basePath + 'invest/trade/conbond/TrdConBond001/check', object, function(result1) {
                    Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.revoke")),0 ,function(result2){
                        object.revokeReason = result2;
                        WorkFlow.doRevokeList(basePath + 'invest/trade/conbond/TrdConBond001/doRevoke', object, "TrdConBond001_btn_revoke", "TrdConBond003");
                    });
                });
            }else{
                Joyin.ajaxJson(basePath + 'invest/trade/conbond/TrdConBond001/check', object, function(result1) {
                    Joyin.prompt(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.revoke")),0 ,function(result2){
                        object.revokeReason = result2;
                        WorkFlow.doRevokeList(basePath + 'invest/trade/conbond/TrdConBond001/doRevoke', object, "TrdConBond001_btn_revoke", "TrdConBond002");
                    });
                });
            }
        });
    });
}());