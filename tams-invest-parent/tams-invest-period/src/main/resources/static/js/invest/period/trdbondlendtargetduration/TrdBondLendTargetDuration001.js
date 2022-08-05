var TrdBondLendTargetDuration001;
(function () {

    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        var result = $.serializeToObject('#searchForm');
        if ($("#statusValid").val()) {
            result.statusValid = $("#statusValid").val().join(",");
        } else {
            result.statusValid = null;
        }
        if ($("#portfolioId").val()) {
            result.portfolioId = $("#portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }
        if ($("#finprodMarketId").val() && $('#finprodMarketId').select2('data')[0] && $('#finprodMarketId').select2('data')[0].object) {
            result.finprodId = $('#finprodMarketId').select2('data')[0].object;
        }
        if ($("#mDate").val()) {
            result.mdateStart = $("#mDate").startDate();
            result.mdateEnd = $("#mDate").endDate();
        }
        return result;
    };

    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        dblclick: function (table, data) {
            //在新建界面进行显示单行的详细信息
            data.pageModel = PAGE_MODEL_REFERENCE;
            var title = '融出债券收息还本（收）查看';
            if (data.tradeType == '137') {
                title = '融入债券付息还本（付）查看'
            }
            Joyin.dialog(title,
                basePath + "invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration002/init",
                "98%", "95%", data, function () {
                });
        },
        cols: [
            {field: 'statusValidString', width: 120, title: '有效状态', sortField: 'statusValid'}
            , {field: 'totleTradeId', width: 200, title: '业务编号', sortField: 'TOTLE_TRADE_ID'}
            , {field: 'tradeTypeString', width: 180, title: '交易类型', sortField: 'tradeType'}
            , {field: 'mpFinprodId', width: 200, title: '原始交易编号', sortField: 'MP_FINPROD_ID'}
            , {
                field: 'finprodMarketId', title: '标的债券代码', width: '200', sortField: 'FINPROD_MARKET_ID',
                render: function (data, type, row, meta) {
                    return "<a  class='pro-link finprodIdLink' style='cursor: pointer;'>" + data + "</ a>";
                }
            }
            , {field: 'finprodAbbr', width: 200, title: '标的债券名称', sortField: 'finprodAbbr'}
            , {
                field: 'portfolioName', width: 150, title: '组合名称', sortField: 'PORTFOLIO_NAME',
                render: function (data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">' + row.portfolioId + '</a>';
                }
            }
            , {field: 'amount', width: 200, title: '券面总额', type: 'money', sortField: 'amount'}
            , {field: 'counterName', width: 200, title: '交易对手', sortField: 'COUNTER_NAME'}
            , {field: 'vdate', width: 150, title: '计息开始日', sortField: 'vdate'}
            , {field: 'mdate', width: 150, title: '计息结束日', sortField: 'mdate'}
            , {field: 'tradeDate', width: 150, title: '实付（收）日期', sortField: 'TRADE_DATE'}//添加理论支付日
            , {field: 'theoryAmt', width: 200, title: '理论本息总额', type: 'money', sortField: 'THEORY_AMT'}
            , {field: 'tradeAmt', width: 200, title: '实收（付）本息总额', type: 'money', sortField: 'tradeAmt'}
            , {
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
        checkbox: true,
        progressBar: true
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function (data, index) {
        var confirmTrade = function (data, PAGE_MODEL_EDIT) {
            data.pageModel = PAGE_MODEL_EDIT;
            var title = '融出债券收息还本（收）确认';
            if (data.tradeType == '137') {
                title = '融入债券付息还本（付）确认'
            }
            Joyin.dialog(title,
                basePath + "invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration002/init",
                "98%", "95%", data, function () {
                });
        };
        confirmTrade(data, PAGE_MODEL_NEW);
    });
    /**
     * 资产代码链接
     */
    table.bind('click', '.finprodIdLink', function (row) {
        //在新建界面进行显示单行的详细信息
        let finprodId = row.finprodMarketId
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
        //
    });

    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 画面加载完成
     */
    $(function () {
        $("#TrdBondLendTargetDuration001_btn_export").click(exportExcel)
        $("#TrdBondLendTargetDuration001_btn_search").click(function () {
            table.load();
        })
        $("#TrdBondLendTargetDuration001_btn_revoke").click(function () {
            var rows = table.getAllCheckedRows();
            if (rows.length != 1) {
                Joyin.error(JoyinUtil.getMessage('请选择一条数据'))
                return;
            }
            var object = rows[0];
            object.operateType = "revoke";
            Joyin.ajax(basePath + 'invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration001/revokeCheck', object, function (result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    object.revokeReason = result;
                    WorkFlow.doRevoke(basePath + 'invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration001/revoke', object, "TrdBondLendTargetDuration001_btn_revoke", "TrdBondLendTargetDuration002");
                });
            });
        })
    });
    TrdBondLendTargetDuration001 = {
        reloadTable: function () {
            table.reload();
        }
    }
    
    //所有dom对象加载完的处理
    $(document).ready(function() {
        if(REM_TYPE && REM_TYPE == 'R3S16'){
            table.table? table.reload() : table.load();
        }
    })
}());

/**
 * 组合信息查看页面
 */
var showPtlManagement = function (portfolioId) {
    Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"),
        basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
        "98%", "95%", {
            pageModel: PAGE_MODEL_REFERENCE,
            portfolioId: portfolioId,
        });
}
//不同资产的查看页面
var showDetailByFinprodId = function (finprodId) {
    var label = '';
    var pagePath = '';
    //债券标的信息查看
    label = 'FinBondTargetAsset002.title';
    pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
    Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
        "98%", "95%", {
            pageModel: PAGE_MODEL_REFERENCE,
            finprodId: finprodId
        });//基金类标的信息查看
}