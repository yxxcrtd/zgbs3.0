(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var entrustId = $('#entrustId').val();
        var pageParam = {
            entrustId: cstpEntrustId?cstpEntrustId:entrustId
            ,tradeId:$('#tradeId').val()
            ,portfolioId: $('#portfolioId').val()
            ,finprodId: $('#finprodId').val()
            ,tradeMarket: $('#tradeMarket').val()
            ,tradeMarketMode: $('#tradeMarketMode').val()
            ,tradePlat: $('#tradePlat').val()
            ,acctNo:$('#acctNo').val()
            ,ps: $('#ps').val()
        	,validStatus: $('#validStatus').val()
            ,tradeDateBegin: $('#tradeDate').startDate()
            ,tradeDateEnd: $('#tradeDate').endDate()
            // ,fTradeId: $('#fTradeId').val()
            //添加登记托管机构、债券剩余期限 add by lishuping 20191010 start
            ,registOrg: $('#registOrg').val()
            ,endDays1Start: $('#endDays1Start').val()
            ,endDays1End: $('#endDays1End').val()
            ,fTradeId:fTradeId?fTradeId:$('#fTradeId').val()
            ,chlFinprodId: $('#chlFinprodId').val()
            //添加登记托管机构、债券剩余期限 add by lishuping 20191010 end
            ,queryType:IS_QUERY_ASSETS_LOSS
        };
        if ($("#chlFinprodId").val()) {
            pageParam.chlFinprodId = $("#chlFinprodId").val().join(",");
        }
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
            data.tradeId = data.totleTradeId
            Joyin.dialog(JoyinUtil.getLabel("TrdBondInvestTrade002.reference"),
                    JoyinUtil.getRealPath("invest/trade/bond/TrdBondInvestTrade002/init"),
                    "99%", "99%", data/*,
                    function(){table.reload();}*/);
        },
        cols:[
            {field:'validStatusText',sortField:'VALID_STATUS', width:100, title: 'TrdBondInvestTrade001.validStatus'}
            ,{field:'totleTradeId', sortField:'TOTLE_TRADE_ID',width:220, title: 'TrdBondInvestTrade001.tradeId'}
            ,{field:'entrustId',sortField:'ENTRUST_ID', width:200, title: 'TrdBondInvestTrade001.entrustId'}
        	,{field:'portfolioId',sortField:'PORTFOLIO_ID', width:150, title: 'TrdBondInvestTrade001.portfolioId',
                render : function(data, type, row, meta) {
                    return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                }
        	}
        	,{field:'portfolioName', sortField:'PORTFOLIO_NAME',width:140, title: 'TrdBondInvestTrade001.portfolioName', visible: false}
            ,{field:'institutionName', sortField:'INSTITUTION_NAME',width:140, title: '机构名称'}
            ,{field:'finprodId',sortField:'FINPROD_MARKET_ID', width:180, title: 'TrdBondInvestTrade001.finprodId',
                render : function(data, type, row, meta) {
                    return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodId + "</a>";
                }
        	}
        	,{field:'finprodName', sortField:'FINPROD_NAME',width:140, title: 'TrdBondInvestTrade001.finprodName'}
        	// ,{field:'chlFinprodId', sortField:'CHL_NAME',width:140, title: 'TrdBondInvestTrade001.chlFinprodId'}
        	,{field:'psText', sortField:'ps',width:100, title: 'TrdBondInvestTrade001.ps'}
        	,{field:'tradeMarketText', sortField:'TRADE_MARKET',width:130, title: 'TrdBondInvestTrade001.tradeMarket'}
        	,{field:'tradeMarketModeText',sortField:'TRADE_MARKET_MODE', width:120, title: 'TrdBondInvestTrade001.tradeMarketMode'}
        	,{field:'tradePlatText',sortField:'TRADE_PLAT', width:120, title: 'TrdBondInvestTrade001.tradePlat'}
        	,{field:'tradeAmt',sortField:'TRADE_AMT', width:150, digit: 4 , title: 'TrdBondInvestTrade001.tradeAmt1', type: 'money'}
            ,{field:'ownTradeAmt',type:'number', title: 'TrdBondInvestTrade001.ownTradeAmt', width:150, digit: 4 ,sortField:"ownTradeAmt"}
            ,{field:'integrateTradeAmt',type:'number', title: 'TrdBondInvestTrade001.integrateTradeAmt', width:150, digit: 4 , sortField:"integrateTradeAmt"}
        	,{field:'ccyText', sortField:'CCY',width:130, title: 'TrdBondInvestTrade001.ccy'}
            //  表格增加估值偏离度,净价by赵东东 2019.9.20 start
        	,{field:'deviation',sortField:'',width:200, title:'TrdBondInvestTrade001.deviation'}
            ,{field:'unitCprice',sortField:'',width:200, title:'TrdBondInvestTrade001.unitCprice'}
            //  表格增加估值偏离度，净价by赵东东 2019.9.20 end
        	,{field:'tradeDate', sortField:'TRADE_DATE',width:100, title: 'TrdBondInvestTrade001.tradeDate', type: 'date'}
        	,{field:'settleDate',sortField:'SETTLE_DATE', width:100, title: 'TrdBondInvestTrade001.settleDate', type: 'date'}
        	//,{field:'fTradeId',sortField:'F_TRADE_ID', width:180, title: 'TrdBondInvestTrade002.fTradeId'}
        	,{field:'orderRetStatusStr',sortField:'ORDER_RET_STATUS', width:150, title: 'TrdBondInvestTrade001.importStatus', type: 'date'}
            // <!-- 资产推荐方 新增 zhuyongkang 20191023 start-->
            ,{field:'assetRecommandOrg',sortField:'ASSET_RECOMMAND_ORG', visible:false,width:150, title: 'TrdBondInvestTrade001.assetRecommandOrg'}
            // <!-- 资产推荐方 新增 zhuyongkang 20191023 end-->
            ,{field:'fTradeId',sortField:'F_TRADE_ID', width:180, title:pageId == 'TrdBondInvestTradeEx001'?'TrdBondInvestTrade002.fTradeId':'TrdBondLendInvestTrade002.fTradeId'}
        	,{field:'remark', sortField:'',width:400, title: 'TrdBondInvestTrade001.remark'}
        	,{field:'validStatus', visible: false}
        	,{field:'ps', visible: false}
            ,{field:'tradeMarket', visible: false}
            ,{field:'tradeMarketMode', visible: false}
            ,{field:'tradePlat', visible: false}
            ,{field:'ccy', visible: false}
            ,{field:'systemFinprodId', visible: false}
        ],
        initSort: {field: 'TRADE_DATE',type: 'desc'}
        //loadonce:true
    });
  
    var exportExcel = function(){
        Joyin.exportExcel('export', getPageParams());
    };

    /**
     * 导出pdf add by zengchengfa 20210803
     */
    var exportPdf = function() {
        if(table!=null && table!=''){
            var selArr = table.getAllCheckedRows();
            // 列表中未勾选
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            var paramObj = getPageParams();
            //勾选导出
            var selectedItem = [];
            $.each(table.getAllCheckedRows() ,function (i, item) {
                selectedItem.push(item.totleTradeId);
            });
            paramObj.selectedItem = selectedItem.join(',');
            Joyin.exportExcel('exportPdf', paramObj);
        }else{
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
        }
    };
//    /**
//     * 绑定编辑事件
//     */
//    table.bind('click', '.edit', function(data, index) {
//        data.operateType = "edit";
//        data.pageModel=PAGE_MODEL_EDIT;
//        Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade002/check', data, function(result) {
//            Joyin.dialog(JoyinUtil.getLabel("TrdBondInvestTrade002.edit"),
//                    basePath + "invest/trade/bond/TrdBondInvestTrade002/init",
//                    "99%", "99%", {pageModel:PAGE_MODEL_EDIT,
//            						tradeId:data.tradeId},function(){table.reload();});
//        });
//    });
//    /**
//     * 绑定删除事件
//     */
//    table.bind('click', '.delt', function(data, index) {
//        var object = {selectedItem: [data], operateType:"delete"};
//        Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade001/check', object, function(result) {
//            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
//                WorkFlow.doDeleteList(basePath + 'invest/trade/bond/TrdBondInvestTrade001/delete', object, "TrdBondInvestTrade001_btn_revoke", "TrdBondInvestTrade001",function() {
//                	table.reload();
//                });
//            });
//        });
//    });

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
        Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002"),
                basePath + "invest/asset/bond/FinBondTargetAsset002/init",
                "95%", "90%",
                { pageModel:PAGE_MODEL_REFERENCE,
                finprodId: row.systemFinprodId,
                finprodName: row.finprodName });
    });
    
//     var init = function(){
//    	 Joyin.ajax(basePath + 'invest/trade/bond/TrdBondInvestTrade001/getSysDate', object, function() {
//         	table.reload();
//         });
//     }
//     添加剩余期限 add by lishuping 20191012 start
        var endDays1Change = function () {
            var startVal= $('#endDays1Start').val();
            var endVal= $('#endDays1End').val();
            if(startVal && endVal && Number(startVal) > Number(endVal)){
                $('#endDays1End').val(startVal).trigger('blur');
                $('#endDays1Start').val(endVal).trigger('blur');
            }
        }

    $(function () {
        if(fTradeId||cstpEntrustId){
            $('.form-button-area').hide();
            $('.condition-area').hide();
            table.load();
        }
        $('#endDays1Start').change(endDays1Change);
        $('#endDays1End').change(endDays1Change);
//     添加剩余期限 add by lishuping 20191012 end
        // 绑定按钮事件
        $('#'+pageId+'_btn_search').click(function() {
            table.load();
            $('#TrdBondInvestTrade001_btn_revoke').show();
        });

        $('#'+pageId+'_btn_export').click(function() {
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
        $('#'+pageId+"_btn_refresh").click(function(){
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
            Joyin.ajax(basePath + 'invest/trade/bond/TrdBondInvestTrade001/refreshStatus', object, function() {
            	table.reload();
            });
    	});

        $('#'+pageId+'_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdBondInvestTrade002"), basePath
                            + "invest/trade/bond/TrdBondInvestTrade002/init", "99%", "99%", {
                        pageModel : PAGE_MODEL_NEW,parentPageId:pageId
                    }, function() {
                        table.reload();
                    });
        });
        
        // add by zhanggenglin 债券多组合 20190819 start
        $('#TrdBondInvestTrade003_btn_add').click(function() {
            Joyin.dialog('债券类资产多组合交易新建', basePath
                            + "invest/trade/bond/TrdBondInvestTrade003/init", "99%", "99%", {
                        pageModel : PAGE_MODEL_NEW,parentPageId:pageId
                    }, function() {
                        table.reload();
                    });
        });
     // add by zhanggenglin 债券多组合 20190819 end
        
        /**
         * 撤销
         */
        $('#'+pageId+'_btn_revoke').click(function() {
            if(table!=null && table!=''){
                var selArr = table.getAllCheckedRows();
            }
            // 列表中未勾选
            if (!selArr || selArr.length == 0) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
            // 列表中勾选对象1条以上时
            if (selArr.length > 1) {
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
                return;
            }
            // 流程中不允许撤销
            if(selArr[0].validStatus == '01'){
                Joyin.error(JoyinUtil.getMessage("流程中的交易不允许撤销"));
                return;
            }



            var checkObj = {};
            checkObj.totleTradeId = selArr[0].totleTradeId;
            checkObj.tradeId = selArr[0].totleTradeId;
            checkObj.systemFinprodId = selArr[0].systemFinprodId;
            checkObj.tradeDate = selArr[0].tradeDate;
            checkObj.operateType = "revoke";
            checkObj.finprodName = selArr[0].finprodName;
            checkObj.entrustId = selArr[0].entrustId;
            Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade001/checkRevoke', checkObj, function(result) {
                var resdata = result.data;
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    Joyin.confirm(resdata, function() {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    checkObj.revokeReason = result;
                    WorkFlow.doRevokeList(basePath + 'invest/trade/bond/TrdBondInvestTrade001/doRevoke', checkObj, "TrdBondInvestTrade001_btn_revoke", "TrdBondInvestTrade002");
                    });
                });
            });
//            
//            if(table!=null && table!=''){
//                var selArr = table.getAllCheckedRows();
//            }
//            if (!selArr || selArr.length == 0) {
//                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
//                return;
//            }
//            var object = {selectedItem: selArr, operateType:"delete"};
//            Joyin.ajaxJson(basePath + 'invest/trade/bond/TrdBondInvestTrade001/check', object, function(result) {
//                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
//                    WorkFlow.doDeleteList(basePath + 'invest/trade/bond/TrdBondInvestTrade001/delete', object, "TrdBondInvestTrade001_btn_revoke", "TrdBondInvestTrade001");
//                });
//            });
        });

        //add by zengchengfa 20210803 pdf导出
        $('#'+ pageId +'_btn_export_pdf').click(function() {
            exportPdf();
        });
    });
}());