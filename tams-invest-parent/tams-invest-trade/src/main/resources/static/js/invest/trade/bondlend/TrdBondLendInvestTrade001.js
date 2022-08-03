(function(){
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        var pageParam = {
            entrustId: $('#entrustId').val()
            ,portfolioId: $('#portfolioId').val()
            ,finprodId: $('#finprodId').val()
            ,tradeMarket: $('#tradeMarket').val()
            ,ps: $('#ps').val()
        	,validStatus: $('#validStatus').val()
            ,tradeDateBegin: $('#tradeDate').startDate()
            ,tradeDateEnd: $('#tradeDate').endDate()
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
            Joyin.dialog(JoyinUtil.getLabel("TrdBondLendInvestTrade002.reference"),
                    JoyinUtil.getRealPath("invest/trade/bondlend/TrdBondLendInvestTrade002/init"),
                    "99%", "99%", data, function(){table.reload();});
        },
        cols:[
            {field:'validStatusText',sortField:'VALID_STATUS', width:100, title: 'TrdBondLendInvestTrade001.validStatus'}
            ,{field:'tradeId', sortField:'TRADE_ID',width:200, title: 'TrdBondLendInvestTrade001.tradeId'}
            ,{field:'entrustId',sortField:'ENTRUST_ID', width:200, title: 'TrdBondLendInvestTrade001.entrustId'}
        	,{field:'portfolioId',sortField:'PORTFOLIO_ID', width:140, title: 'TrdBondLendInvestTrade001.portfolioId',
                render : function(data, type, row, meta) {
                    return "<a class='portfolioId-link' style='cursor: pointer;'>" + row.portfolioId + "</a>";
                }
        	}
        	,{field:'portfolioName', sortField:'PORTFOLIO_NAME',width:140, title: 'TrdBondLendInvestTrade001.portfolioName'}
        	,{field:'finprodId',sortField:'FINPROD_MARKET_ID', width:140, title: 'TrdBondLendInvestTrade001.finprodId',
                render : function(data, type, row, meta) {
                    return "<a class='finprodId-link' style='cursor: pointer;'>" + row.finprodId + "</a>";
                }
        	}
        	,{field:'finprodName', sortField:'FINPROD_NAME',width:140, title: 'TrdBondLendInvestTrade001.finprodName'}
        	,{field:'psText', sortField:'ps',width:100, title: 'TrdBondLendInvestTrade001.ps'}
        	,{field:'tradeMarketText', sortField:'',width:100, title: 'TrdBondLendInvestTrade001.tradeMarket'}
        	,{field:'tradeAmt',sortField:'TRADE_AMT', width:120, title: 'TrdBondLendInvestTrade001.tradeAmt1', digit: 4, type: 'money'}
            ,{field:'tradeDate', sortField:'TRADE_DATE',width:100, title: 'TrdBondLendInvestTrade001.tradeDate', type: 'date'}
            ,{field:'vdate',sortField:'VDATE', width:100, title: 'TrdBondLendInvestTrade001.vdate', type: 'date'}
            ,{field:'mdate',sortField:'MDATE', width:100, title: 'TrdBondLendInvestTrade001.mdate', type: 'date'}
            ,{field:'termType',sortField:'TERM_TYPE', width:100, title: 'TrdBondLendInvestTrade001.termType'}
            ,{field:'actAmtDays',sortField:'actAmtDays', width:100, title: 'TrdBondLendInvestTrade001.actAmtDays', render : function(data, type, row, meta) {
                    if (row.vdate && row.mdate){
                        var days = (new Date(row.mdate) - new Date(row.vdate))/(24*60*60*1000);
                       return days;
                    }else{
                        return '';
                    }
            }}
            ,{field:'ccyText', sortField:'CCY',width:130, title: 'TrdBondLendInvestTrade001.ccy'}
            ,{field:'remark', width:150, title: 'TrdBondLendInvestTrade001.remark'}
        	,{field:'validStatus', visible: false}
        	,{field:'ps', visible: false}
            ,{field:'ccy', visible: false}
            ,{field:'tradeMarket', visible: false}
            ,{field:'systemFinprodId', visible: false}
            ,{field:'exchangePledge', visible: false}
        ],
        initSort: {field: 'TRADE_DATE',type: 'desc'}
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
            "98%", "98%",
            param);
        // Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002"),
        //         basePath + "invest/asset/bond/FinBondTargetAsset002/init",
        //         "95%", "90%",
        //         { pageModel:PAGE_MODEL_REFERENCE,
        //         finprodId: row.systemFinprodId,
        //         finprodName: row.finprodName });
    });

    $(function () {
        // 绑定按钮事件
        $('#TrdBondLendInvestTrade001_btn_search').click(function() {
            table.load();
            $('#TrdBondLendInvestTrade001_btn_revoke').show();
        });
        
        $('#TrdBondLendInvestTrade001_btn_export').click(function() {
            exportExcel();
        });
        $('#TrdBondLendInvestTrade001_btn_add').click(function() {
            Joyin.dialog(JoyinUtil.getLabel("TrdBondLendInvestTrade002"), basePath
                            + "invest/trade/bondlend/TrdBondLendInvestTrade002/init", "99%", "99%", {
                        pageModel : PAGE_MODEL_NEW
                    }, function() {
                        table.reload();
                    });
        });

        // 质押券置换按钮事件
        $('#TrdRepoTrade001_btn_substitution').click(function () {
            if (table != null && table != '') {
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
            //只有交易状态为有效，交易.质押券置换安排 != 是 then 提示不能做质押券置换
                    if(selArr[0]["validStatusText"]=='有效' && selArr[0]["exchangePledge"]=='Y') {
                        Joyin.dialog(JoyinUtil.getLabel("TrdStandardBond004"), basePath
                            + "invest/trade/trdrepotrade/TrdRepoTrade004/init", "99%", "99%", selArr[0], {
                            pageModel: PAGE_MODEL_NEW
                        }, function () {
                            table.reload();
                        });
                    }
                    else
                    {
                        Joyin.error(JoyinUtil.getMessage("请选择交易状态为有效、质押券置换安排为是的数据进行操作!"));
                        return;
                    }
        });
        /**
         * 撤销
         */
        $('#TrdBondLendInvestTrade001_btn_revoke').click(function() {
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
            
            var checkObj = {};
            checkObj.tradeId = selArr[0].tradeId;
            checkObj.finprodName = selArr[0].finprodName;
            checkObj.tradeDate = selArr[0].tradeDate;
            checkObj.operateType = "revoke";
            checkObj.ps = selArr[0].ps;
            Joyin.ajaxJson(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade001/checkRevoke', checkObj, function(result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    checkObj.revokeReason = result;
                    WorkFlow.doDeleteList(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade001/doRevoke', checkObj, "TrdBondLendInvestTrade001_btn_revoke", "TrdBondLendInvestTrade002");
                });
            });

        });
    });
}());