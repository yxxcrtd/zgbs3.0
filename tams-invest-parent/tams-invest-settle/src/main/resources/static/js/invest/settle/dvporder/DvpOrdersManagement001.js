/**
 * 交易信息查看页面
 */
var showDetailTrdPage = function (paBussType, tradeId, tradeDateVal, tradeMarket) {
    let param;
    let url;
    console.log("----------------showDetailTrdPage-------------------")
    //债券委托详情 银行间/商业银行柜台
    if ((paBussType === '09' || paBussType === '10') && (tradeMarket === '01' || tradeMarket === '02')) {
        console.log("----------------PINAN_ASSET_TYPE_F01----------------------")

        param = {
            "busiCode": tradeId,
            "startDate": tradeDateVal,
            "endDate": tradeDateVal,
            "finprodType2": PINAN_ASSET_TYPE_F01
        };
        url = "home.html?a=a&busiCode=" + tradeId
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
    else if ((paBussType === '19' || paBussType === '20') && (tradeMarket === '01' || tradeMarket === '02')) {
        console.log("----------PINAN_ASSET_TYPE_F07-------------");

        param = {
            "busiCode": tradeId,
            "startDate": tradeDateVal,
            "endDate": tradeDateVal,
            "finprodType2": PINAN_ASSET_TYPE_F07
        };
        url = "home.html?a=a&busiCode=" + tradeId
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
    else if ((paBussType === '151' || paBussType === '202'|| paBussType === '180') && (tradeMarket === '01' || tradeMarket === '02')) {
        console.log("----------PINAN_ASSET_TYPE_F17-------------");

        param = {
            "busiCode": tradeId,
            "startDate": tradeDateVal,
            "endDate": tradeDateVal,
            "finprodType2": FINPROD_TYPE_TG_F32
        };
        url = "home.html?a=a&busiCode=" + tradeId
            + "&finprodType2=" + FINPROD_TYPE_TG_F32
            + "&startDate=" + tradeDateVal
            + "&endDate=" + tradeDateVal
            + "&pageModel=1&editable=false&isTodoList=true#/transactionSearch/detail?a=a";

        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "95%", "90%",
            param);
    }
}

$(function () {
    /**
     * 获取查询参数
     */
    var getPageParams = function () {
        var result = $.serializeToObject('#dvpTransferForm');
        if ($("#portfolioId1").val()) {
            result.portfolioId = $("#portfolioId1").val().join(",");
        } else {
            result.portfolioId = null;
        }
        if ($("#paBusinessType").val()) {
            result.paBusinessType = $("#paBusinessType").val().join(",");
        } else {
            result.paBusinessType = null;
        }
        if ($("#settleDate").val()) {
            result.settleDateStart = $("#settleDate").startDate();
            result.settleDateEnd = $("#settleDate").endDate();
        }
        if (result.finprodMarketId) {
            result.finprodMarketId = result.finprodMarketId.replace(/(^\s*)|(\s*$)/g, "");
        }
        if (result.bizCode) {
            result.bizCode = result.bizCode.replace(/(^\s*)|(\s*$)/g, "");
        }
        result.isDvp = true;
        return result;
    };

    function getTransferFormParams() {
        var result = $.serializeToObject('#transferForm');
        if ($("#portfolioId").val()) {
            result.portfolioId = $("#portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }
        if ($("#payDate").val()) {
            result.payDateStart = $("#payDate").startDate();
            result.payDateEnd = $("#payDate").endDate();
        }
        result.isDvp = false;
        return result;
    }

    var dvpTransferTable = $("#dvpTransferTable").joyintable({
        ajax: {
            url: 'findDvpTransferTableData',
            type: 'POST',
            data: function () {
                return getPageParams();
            }
        },
        checkbox: true,
        searchOnInit: false,
        cols: [
            {
                field: 'bizCode', title: '业务编号', width: 200, render: function (data, type, row, meta) {
                    // 09债券类资产买入 180一级申购 202债券分销买入确认 151中标结果维护
                    if (row.paBussType === '180' || row.paBussType === '202' || row.paBussType === '151' || row.paBussType === '09'
                            || row.paBussType === '10' || row.paBussType === '19' || row.paBussType === '20') {
                        return "<a onclick=\"showDetailTrdPage('" + row.paBussType + "','" + row.bizCode + "','"
                            + row.settleDate + "','" + row.tradeMarket + "')\" + style='cursor: pointer;'>" + row.bizCode + "</a>";
                    }
                    return "<a class='pro-link' style='cursor: pointer;' " +
                        "href='javascript:openOperateWindow(\"" + PAGE_STATUS_05 + "\",\"" + row.paBussType + "\",\"" + row.portfolioId + "\",\""
                        + row.bizCode + "\",\"" + row.finprodId + "\",\"" + row.orderId + "\")'>" + data + "</a>";
                }
            }
            , {field: 'paBussTypeString', title: '业务类型', width: 200, sortField:'PA_BUSS_TYPE'}
            , {field: 'portfolioId', title: '组合代码', width: 160, sortField:'PORTFOLIO_ID'}
            , {field: 'portfolioName', title: '组合名称', width: 200, sortField:'PORTFOLIO_NAME'}
            , {field: 'finprodMarketId', title: '资产代码', width: 160, sortField:'FINPROD_MARKET_ID'}
            , {field: 'finprodName', title: '资产名称', width: 200, sortField:'finprodName'}
            , {field: 'payAcctNo', title: '付款账号', width: 200, sortField:'payAcctNo'}
            , {field: 'payAcctName', title: '付款账户名称', width: 200, sortField:'payAcctName'}
            , {field: 'recAcctNo', title: '收款账号', width: 200, sortField:'recAcctNo'}
            , {field: 'recAcctName', title: '收款账户名称', width: 200, sortField:'recAcctName'}
            , {field: 'largePayNo', title: '收款账户网点/支付系统号', width: 200, sortField:'largePayNo'}
            , {field: 'tradeDate', title: '交易日', width: 120, sortField:'TRADE_DATE'}
            , {field: 'settleDate', title: '交割日', width: 120, sortField:'SETTLE_DATE'}
            , {field: 'tradeAmt', title: '交易金额', width: 200, type: 'number', sortField:'TRADE_AMT'}
            , {field: 'registOrgString', title: '登记托管机构', width: 220, sortField:'REGIST_ORG'}
            , {field: 'dealStatusString', title: '处理状态', width: 120, sortField:'dealStatus'}
            /*, {
                field: 'operation', title: 'SysCommon.operation', type: 'action', width: 120,
                buttons: [{
                    text: '',
                    clas: 'iconfont icon-download',
                    title: '下载'
                }]
            }*/
            
            ,{field: 'operation', title: 'SysCommon.operation', type: 'action', 
                render: function (data, type, row, meta) {
                   
                        if(purviewElements.indexOf('_manual')!=-1){
                            return '<i class="btn iconfont icon-download manual" title="下载">下载</i>';
                        }
                    } 
                }
        ],
    });

    /**
     * 绑定编辑事件
     */
    dvpTransferTable.bind('click', '.manual', function (data, index) {
        var params = {};
        params.pageModel = PAGE_MODEL_REFERENCE;
        params.bizCode = data.bizCode;
        Joyin.dialog("附件信息查看-" + data.bizCode ,
            basePath + "component/common/BaseFileComm/init",
            "98%", "95%", params, function () {
            });
    });

    var transferTable = $("#transferTable").joyintable({
        ajax: {
            url: 'findTransferTableData',//请求路径
            type: 'POST',//请求类型
            data: function () {
                return getTransferFormParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox: true,
        searchOnInit: false,
        /**
         * 定义表头;
         */
        cols: [
            {field: 'bizCode', title: '业务编号', width: 120, render: function (data, type, row, meta) {
                    return "<a class='pro-link' style='cursor: pointer;' href=\"javascript:openDvp002('"+data+"')\">" + data + "</a>";
                }}
            , {field: 'portfolioId', title: '组合代码', width: 120, sortField:'PORTFOLIO_ID'}
            , {field: 'portfolioName', title: '组合名称', width: 150, sortField:'PORTFOLIO_NAME'}
            , {field: 'isNeedSendString', title: '是否发送指令', width: 100, sortField:'IS_NEED_SEND'}
            , {field: 'payTypeString', title: '划款类型', width: 120, sortField:'payType'}
            , {field: 'settleDate', title: '支付日期', width: 120, sortField:'SETTLE_DATE'}
            , {field: 'settleAmt', title: '划款金额', width: 200, type: 'number', sortField:'SETTLE_AMT'}
            , {field: 'ccyString', title: '币种', width: 150, sortField:'CCY'}
            , {field: 'payAcctNo', title: '付款账号', width: 150, sortField:'payAcctNo'}
            , {field: 'payAcctName', title: '付款账号名称', width: 200, sortField:'payAcctName'}
            , {field: 'recAcctNo', title: '收款账号', width: 150, sortField:'recAcctNo'}
            , {field: 'recAcctName', title: '收款账号名称', width: 200, sortField:'recAcctName'}
            , {field: 'largePayNo', title: '收款账号网点/支付系统号', width: 150, sortField:'largePayNo'}
            , {field: 'sendStatusString', title: '发送状态', width: 150, sortField:'sendStatus'}
            , {field: 'purpose', title: '用途', width: 200, sortField:'PURPOSE'}
            , {field: 'remark', title: '备注', width: 200, sortField:'REMARK'}
//            , {field: 'orderStatus', title: '指令状态', width: 200}
//            , {field: 'sendStatus', title: '发送状态', width: 200}
            , {field: 'orderStatus', visible : false}
            , {field: 'sendStatus', visible : false}
        ],
    });

    var dvpExportExcel = function () {
        //Joyin.exportExcel('export', getPageParams());

        if (dvpTransferTable != null && dvpTransferTable != '') {
            var selArr = dvpTransferTable.getAllCheckedRows();
            var datas = JSON.stringify(selArr);
        }
        if (!selArr || selArr.length == 0) {
            var paramObj = getPageParams();
            Joyin.exportExcel('export', paramObj);
        } else {
            Joyin.exportExcel('export', {
                datas : datas,
                isDvp : true
            });
        }

    };

    var ExportExcel = function () {
        //Joyin.exportExcel('export', getTransferFormParams());
        if (transferTable != null && transferTable != '') {
            var selArr = transferTable.getAllCheckedRows();
            var datas = JSON.stringify(selArr);
        }
        if (!selArr || selArr.length == 0) {
            var paramObj = getTransferFormParams();
            Joyin.exportExcel('export', paramObj);
        } else {
            Joyin.exportExcel('export', {
                datas : datas,
                isDvp : false
            });
        }
    };

    var printData = function() {
        var selectedRows = transferTable.getAllCheckedRows();
        if (!selectedRows || selectedRows.length != 1) {
            Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0046'))
            return;
        }
        var data = selectedRows[0]
        window.open("printPayOrders?bizCode=" + data.bizCode, "_blank", "fullscreen=yes,resizeable=no,scrollbars=yes,channelmode=yes,left=0,top=0,width=1366,height=768")
    }

    $(function () {
        $(".transfer-form #DvpOrdersManagement001_btn_search").click(function () {
            transferTable.load();
        })
        $(".dvp-transfer-form #DvpOrdersManagement001_btn_search").click(function () {
            dvpTransferTable.load();
        })
        $(".dvp-transfer-form #DvpOrdersManagement001_btn_export").click(dvpExportExcel)
        $(".transfer-form #DvpOrdersManagement001_btn_export").click(ExportExcel)
        $("#DvpOrdersManagement001_btn_print").click(printData)

        $('#DvpOrdersManagement001_btn_creatOrder').click(function () {
            var dvpCheckedRows = dvpTransferTable.getAllCheckedRows();
            if (!dvpCheckedRows || dvpCheckedRows.length == 0) {
                Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0026"))
                return;
            }
            var bizCodes = [];
            for (var key in dvpCheckedRows) {
                bizCodes.push(dvpCheckedRows[key].bizCode);
                 if (dvpCheckedRows[key].dealStatus == 已处理) {
                    Joyin.error(JoyinUtil.getMessage("FIN_ASSET_E_0024"))
                    return;
                }
            }

            for (var i=1;i<dvpCheckedRows.length;i++) {
                if (dvpCheckedRows[0].myAcctNo != dvpCheckedRows[i].myAcctNo
                || dvpCheckedRows[0].settleDate != dvpCheckedRows[i].settleDate
                || dvpCheckedRows[0].ccy != dvpCheckedRows[i].ccy
                || dvpCheckedRows[0].counterAcctNo != dvpCheckedRows[i].counterAcctNo
                ) {
                    Joyin.error(JoyinUtil.getMessage("FIN_ASSET_E_0025"))
                    return;
                }
            }
            Joyin.ajaxJson(basePath + 'invest/settle/dvporder/DvpOrdersManagement001/checkCreatOrder', bizCodes, function(result) {
                 Joyin.dialog(JoyinUtil.getLabel("DVP清算指令生成"),
                                   basePath + "invest/settle/dvporder/DvpOrdersManagement002/init",
                                                                        "95%", "90%", {bizCodes:bizCodes, pageModel: PAGE_MODEL_NEW});
             });

            /*Joyin.dialog(JoyinUtil.getLabel("DVP清算指令生成"),
                basePath + "invest/settle/dvporder/DvpOrdersManagement002/init",
                "95%", "90%", {bizCodes:bizCodes, pageModel: PAGE_MODEL_NEW});*/
        });
        
        $('#DvpOrdersManagement001_btn_revoke').click(function(){
        	if(transferTable!=null && transferTable!=''){
                var selArr = transferTable.getAllCheckedRows();
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
            //判断指令状态 已撤销的 提示不能再撤销
            if(selArr[0].orderStatus == '02'){
            	Joyin.error(JoyinUtil.getMessage("DvpOrdersManagement001_E_002"));
                return;
            }
            //判断发送状态 已发送的 不能撤销
            if(selArr[0].sendStatus == '01'){
            	Joyin.error(JoyinUtil.getMessage("DvpOrdersManagement001_E_003"));
                return;
            }
            
            var object = {};
            //add by libaisong 2019-10-14 start
            object.orderId = selArr[0].bizCode;
            object.settleDate=selArr[0].settleDate;
            object.operateType = "revoke";
            Joyin.ajaxJson(basePath + 'invest/settle/dvporder/DvpOrdersManagement001/checkRevoke', object, function(result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                	object.revokeReason = result;
                    WorkFlow.doDeleteList(basePath + 'invest/settle/dvporder/DvpOrdersManagement001/doRevoke', object, "DvpOrdersManagement001_btn_revoke", "DvpOrdersManagement002");
                });
            });
            //WorkFlow.doDeleteList(basePath + 'invest/settle/dvporder/DvpOrdersManagement001/doRevoke', object, "DvpOrdersManagement001_btn_revoke", "DvpOrdersManagement002");
            //add by libaisong 2019-10-14 end
        });
    })
});

var openOperateWindow = function (statusValid, paBussType, portfolioId, bizCode, finprodId,orderId) {
    var url = '';
    var data = {
        pageModel: PAGE_MODEL_REFERENCE,
        tradeId: bizCode,
        busiCode: bizCode,
        bizCode: bizCode,
        workflowStatus: statusValid,
        portfolioId: portfolioId,
        statusValid: statusValid,
        validStatus: statusValid,
        finprodId: finprodId,
        isFromCheck: true
    };
    if (paBussType == 债券买入 || paBussType == 债券卖出 || paBussType == 债券行权) {
        url = 债券交易URL;
    } else if (paBussType == 正回购 || paBussType == 逆回购) {
        url = 回购交易URL;
    } else if (paBussType == 正回购到期 || paBussType == 逆回购到期) {
        url = 回购存续URL;
    } else if (paBussType == 买断式正回购 || paBussType == 买断式逆回购) {
        url = 买断式回购交易URL;
    } else if (paBussType == 买断式正回购到期 || paBussType == 买断式逆回购到期) {
        url = 买断式回购存续URL;
    } else if (paBussType == 债券借贷融入到期 || paBussType == 债券借贷融出到期) {
        url = 债券借贷存续URL;
    }else if (paBussType == DVP划款) {
        data.orderId = orderId;
        url = DVP资金调拨URL;
    }

    var title = JoyinUtil.getLabel("SysCommon.refrence");
    Joyin.dialog(title, url, "98%", "98%", data);
}

var openDvp002 = function(bizCode) {
    //在新建界面进行显示单行的详细信息
    var params = {}
    params.pageModel = PAGE_MODEL_REFERENCE;
    params.orderId = bizCode;
    Joyin.dialog("DVP划款指令查看",
        basePath + "invest/settle/dvporder/DvpOrdersManagement002/init",
        "98%", "98%", params, function(){});
}