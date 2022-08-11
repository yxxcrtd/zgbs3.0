/**
 * 交易信息查看页面
 */
var showDetailTrdPage = function (paBussType, tradeId, validStatus, tradeDateVal, tradeMarket, finprodId, status, counterId, entrustId, bidId) {
    console.log("----------------showDetailTrdPage-------------------")
    console.log(validStatus)
    //债券委托详情 银行间/商业银行柜台
    if (paBussType === '09' && (data.tradeMarket == '01' || data.tradeMarket == '02')
        && validStatus !== '03') {
        console.log("----------------PINAN_ASSET_TYPE_F01----------------------")

        if (status === 'Y') {
            tradeId = tradeId.substring(0, tradeId.length - 1)
        }

        var param = {
            "busiCode": tradeId,
            "startDate": tradeDateVal,
            "endDate": tradeDateVal,
            "finprodType2": PINAN_ASSET_TYPE_F01
        };
        var url = "home.html?a=a&busiCode=" + tradeId
            + "&startDate=" + tradeDateVal
            + "&endDate=" + tradeDateVal
            + "&finprodType2=" + PINAN_ASSET_TYPE_F01
            + "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "95%", "90%",
            param);
    }
    //回购委托详情 银行间/商业银行柜台
    else if ((paBussType === '19' || paBussType === '20') && (tradeMarket == '01' || tradeMarket == '02')
        && validStatus !== '03') {
        console.log("----------PINAN_ASSET_TYPE_F07-------------");

        if (status === 'Y') {
            tradeId = tradeId.substring(0, tradeId.length - 1)
        }

        var param = {
            "busiCode": tradeId,
            "startDate": tradeDateVal,
            "endDate": tradeDateVal,
            "finprodType2": PINAN_ASSET_TYPE_F07
        };
        var url = "home.html?a=a&busiCode=" + tradeId
            + "&startDate=" + tradeDateVal
            + "&endDate=" + tradeDateVal
            + "&finprodType2=" + PINAN_ASSET_TYPE_F07 +
            "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "95%", "90%",
            param);
    }
    //一级投标债券 银行间/商业银行柜台
    else if (paBussType === '151' && (tradeMarket == '01' || tradeMarket == '02')
        && validStatus !== "03") {
        console.log("----------PINAN_ASSET_TYPE_F17-------------");

        if (status === 'Y') {
            tradeId = tradeId.substring(0, tradeId.length - 1)
        }

        var param = {
            "busiCode": tradeId,
            "startDate": tradeDateVal,
            "endDate": tradeDateVal,
            "finprodType2": FINPROD_TYPE_TG_F32
        };
        var url = "home.html?a=a&busiCode=" + tradeId
            + "&finprodType2=" + FINPROD_TYPE_TG_F32
            + "&startDate=" + tradeDateVal
            + "&endDate=" + tradeDateVal
            + "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "95%", "90%",
            param);
    } else if (paBussType === '151' && tradeMarket !== '01' && tradeMarket !== '02'
        && validStatus !== "03") {
        if (status === 'Y') {
            tradeId = tradeId.substring(0, tradeId.length - 1)
        }
        var title = JoyinUtil.getLabel("SysCommon.refrence");
        var data = {
            pageModel: PAGE_MODEL_REFERENCE,
            bidId: bidId,
            entruststatusyjs: '04', //指令接受状态
            finprodId: finprodId, //资产代码
            counterId: counterId, //交易对手
            entrustId: entrustId, //指令代码
            pinanassettypeyj: FINPROD_TYPE_TG_F32 // 金融产品分类
        };
        var url = '/invest/trade/primarybiddingmanage/PrimaryBiddingManage002/init';
        Joyin.dialog(title, url, "98%", "98%", data, function () {
            if (typeof parenPage != 'undefined' && parenPage && statusValid != '05' && statusValid != '06') {
                parenPage.refreshTable();
            }
        });
    }
}
let table;
(function () {
    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        var result = $.serializeToObject('#searchForm');
        if ($("#portfolioId").val()) {
            result.portfolioId = $("#portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }
        if ($("#bizCode").val()) {
            result.bizCode = $("#bizCode").val().join(",");
        } else {
            result.bizCode = null;
        }
        if ($("#bizType").val()) {
            result.bizType = $("#bizType").val().join(",");
        } else {
            result.bizType = null;
        }
        result.cashDateStart = $("#cashDate").startDate();
        result.cashDateEnd = $("#cashDate").endDate();
        return result;
    };

    /**
     * 定义数据表格
     */
    table = $("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        cols: [
            {field: 'statusValidString', width: 200, sortField:'statusValid', title: '有效状态'}
            , {
                field: 'bizCode', width: 200, title: '业务编号', render: function (data, type, row, meta) {
                    // 09债券类资产买入 180一级申购 202债券分销买入确认 151中标结果维护 212信用保护凭证申购
                    if(row.paBussType==='212'||row.paBussType==='180'||row.paBussType==='202'||row.paBussType==='09'){
                        return data;
                    }
                    if(row.paBussType==='151'){
                        return "<a onclick=\"showDetailTrdPage('" + row.paBussType + "','"+ row.bizCode + "','" + row.statusValid + "','"
                            + row.cashDate + "','" + row.tradeMarket+ "','" + row.finprodId+ "','"+ row.status+ "','"
                            + row.counterId+ "','" + row.entrustId + "','" + row.bidId + "')\" + style='cursor: pointer;'>" + row.bizCode + "</a>";
                    }
                    return "<a class='pro-link' style='cursor: pointer;' " +
                        "href='javascript:openOperateWindow(\"" + row.statusValid + "\",\"" + row.paBussType + "\",\"" + row.portfolioId + "\",\""
                        + row.bizCode + "\",\"" + row.workflowId + "\",\"" + row.finprodId + "\",\"" + row.ps + "\",\"" + row.wflWorkflowId + "\","+row.todo+ ",\""
                        + row.profitType + "\",\"" + row.finprodId + "\",\"" + row.operateType+ "\",null,null,\"" + row.paBussTypeAdd + "\")'>" + data + "</a>";
                }
            }
            , {field: 'paBussTypeString', width: 200, sortField:'PA_BUSS_TYPE', title: '业务类型'}
            , {field: 'operateTypeString', width: 200, sortField:'operateType', title: '操作类型'}
            , {field: 'payTypeString', width: 200, sortField:'PAY_TYPE', title: '划款类型'}
            , {field: 'portfolioId', width: 200, sortField:'PORTFOLIO_ID', title: '组合代码'}
            , {field: 'portfolioName', width: 200, sortField:'PORTFOLIO_NAME', title: '组合名称'}
            , {field: 'finProdMarketId', width: 200, sortField:'FINPROD_MARKET_ID', title: '产品/资产代码'}
            , {field: 'finProdMarketName', width: 200, sortField:'finProdMarketName', title: '产品/资产名称'}
            , {field: 'cashDate', width: 200, sortField:'cashDate', title: '现金流日期'}
            , {field: 'cashAmt', width: 200, sortField:'cashAmt', title: '划款金额', type: 'money'}
            , {field: 'managerString', width: 200, sortField:'manager', title: '经办人'}
            , {field: 'receiveDateTime', width: 200, sortField:'receiveDateTime', title: '接收时间'}
            , {field: 'payNo', width: 200, sortField:'payNo', title: '付款账户'}
            , {field: 'payName', width: 200, sortField:'payNo', title: '付款账户名称'}
            , {field: 'receiveNo', width: 200, sortField:'receiveNo', title: '收款账户'}
            , {field: 'receiveName', width: 200, sortField:'receiveNo', title: '收款账户名称'}
            , {field: 'purPose', width: 200, sortField:'purPose', title: '资金用途'}
            , {field: 'ccyString', width: 200, sortField:'ccy', title: '币种'}
            , {field: 'sendStatusStr', width: 200, sortable:false, title: '发送状态'}
        ],
        checkbox: true,
        progressBar: true
    });
    
    /**
     * 划拨资金合计计算
     */
    var refreshTotalPrinAmt = function () {
        var selectedRows = table.getAllCheckedRows();
        var cashAmtSum = Number(0);
        for (var i in selectedRows) {
        	cashAmtSum += Number(selectedRows[i].cashAmt);
        }
        $("#cashAmtSum").val(cashAmtSum).trigger('change').trigger('blur');
    };

    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    function printPayOrders() {
        var selectedRows = table.getAllCheckedRows();
        if (!selectedRows || selectedRows.length != 1) {
            Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0046'))
            return;
        }
        var data = selectedRows[0]
        if (data.operateType == '02') {
            Joyin.error('交易在撤销流程中，请检查后再打印单据。')
            return;
        }
        //表格记录查出的有效状态与查询交易有效状态值保持一致
        var statusValid = null;
        if(data.statusValid == '03'){
        	statusValid  = "01";
        }else{
        	statusValid  = "02";
        }
        window.open("printPayOrders?bizCode=" + data.bizCode + "&paBussType=" + data.paBussType + "&statusValid=" + statusValid, "_blank", "fullscreen=yes,resizeable=no,scrollbars=yes,channelmode=yes,left=0,top=0,width=1366,height=768")
    }

    /**
     * 画面加载完成
     */
    $(function () {
        $("#FundBackgroundManagement001_btn_search").click(function () {
            $('#cashAmtSum').val('');
            table.load();
        })
        $("#FundBackgroundManagement001_btn_print").click(function () {
            printPayOrders();
        })
        $("#FundBackgroundManagement001_btn_export").click(exportExcel)
        // $(document).on("click", "div.laytable-cell-checkbox div.layui-form-checkbox", function (e) {
        // 	refreshTotalPrinAmt();
        // 	});
        // $("#FundBackgroundManagement001_btn_calculate").click(function(){
        // 	refreshTotalPrinAmt();
        // });
        // $(".layTableCheckbox").on("click", function () {
        // 	refreshTotalPrinAmt();
        // });
        $("#dataTableDiv").on("click", function () {
            refreshTotalPrinAmt();
        });

    });
}());
