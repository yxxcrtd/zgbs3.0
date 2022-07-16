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
        if ($("#ps").val()) {
            result.ps = $("#ps").val().join(",");
        } else {
            result.ps = null;
        }
        if ($("#chlFinprodId").val()) {
            result.chlFinprodId = $("#chlFinprodId").val().join(",");
        }
        if ($("#psType").val()) {
            result.psType = $("#psType").val().join(",");
        } else {
            result.psType = null;
        }
        if($("#tradeDate").val()) {
            result.tradeDateStart = $("#tradeDate").startDate();
            result.tradeDateEnd = $("#tradeDate").endDate();
        }

        if($("#mDate").val()) {
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
        searchOnInit :REM_TYPE && (REM_TYPE == 'R2S4'||REM_TYPE == 'R2S5'||REM_TYPE == 'R2S6'||REM_TYPE == 'R3S14'),
        dblclick: function (table, data) {
            //在新建界面进行显示单行的详细信息
            data.pageModel = PAGE_MODEL_REFERENCE;
            data.bustype=data.ps;/*到期续作增加交易方向判断是否展示续做信息*/
            Joyin.dialog("回购存续期交易查看",
                basePath + "invest/period/trdrepoduration/TrdRepoDuration002/init",
                "98%", "95%", data, function () {
                });
        },
        cols: [
            {field: 'statusValidString', width: 200, sortField:'statusValid', title: '有效状态'}
            , {field: 'portfolioId', width: 200, sortField:'PORTFOLIO_ID', title: '组合代码',
                render : function(data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">'+row.portfolioId+'</a>';
                }
            }
            , {field: 'portfolioName', width: 200, sortField:'PORTFOLIO_NAME', title: '组合名称'}
            , {field: 'type6String', width: 200, sortField:'TYPE_6', title: '回购类型'}
            , {field: 'psStr', width: 200, sortField:'ps', title: '类型'}
            , {field: 'finprodType2String', width: 200, sortField:'FINPROD_TYPE2', title: '交易方向'}
            , {field: 'tradeDate', width: 200, sortField:'TRADE_DATE', title: '交易日'}
            , {field: 'vDate', width: 200, sortField:'vDate', title: '起息日'}
            , {field: 'mDate', width: 200, sortField:'mDate', title: '到期日'}
            /* , {field: 'intRate', width: 200, sortField:'intRate', title: '回购利率%'}
             , {field: 'counterName', width: 200, sortField:'counterName', title: '交易对手'}*/
            , {field: 'theoryPrin', width: 200, sortField:'THEORY_PRIN', title: '本金金额', type: 'money'}
            , {field: 'settleDate', width: 200, sortField:'SETTLE_DATE', title: '资金到账日'}
            , {field: 'theoryInt', width: 200, sortField:'THEORY_INT', title: '回购利息', type: 'money'}
            , {field: 'fpriceAmt', width: 200, sortField:'THEORY_AMT', title: '到期结算金额', type: 'money'}
            , {field: 'tradeAmt', width: 200, sortField:'THEORY_AMT', title: '实际结算金额', type: 'money'}
            , {field: 'busiCode', width: 200, sortField:'busiCode', title: '业务编号'
                // , render: function (data, type, row, meta) {
                //     return "<a class='pro-link' style='cursor: pointer;' " +
                //         "href='javascript:openOperateWindow(\"" + row.finprodId + "\")'>" + data||'' + "</a>";
                // }
            }
            , {field: 'chlFinprodId', width: 200, title: '合作资产', visible:!ElementsControl.includes("chlFinprodId") }
            , {field: 'statusValid', visible: false}
            , {field: 'type6', visible: false}
            , {field: 'workflowId', visible: false}
            , {field: 'finprodType2', visible: false}
            // , {
            //     field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit']
            //     ,
            //     render: function (data, type, row, meta) {
            //         if (row.statusValid != '01')
            //             return "";
            //         else
            //             //modifiled by liupengfei for 多级法人改造 确认按钮权限控制 S
            //             if(purviewElements.indexOf('_edit')!=-1){
            //                 return '<i class="btn layui-icon edit" title="确认">&#x1005;</i>';
            //             }
            //             //modifiled by liupengfei for 多级法人改造 确认按钮权限控制 E
            //     }
            // }
        ],
        checkbox: true,
        progressBar: true
    });

    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function (data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.dialog("回购存续期交易确认",
            basePath + "invest/period/trdrepoduration/TrdRepoDuration002/init",
            "98%", "95%", data, function () {
                table.reload();
            });
    });

    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 画面加载完成
     */
    $(function () {
        $("#TrdRepoDuration001_btn_search").click(function () {
            table.load();
        })
        $("#TrdRepoDuration001_btn_revoke").click(function () {
            var rows = table.getAllCheckedRows();
            if (rows.length != 1) {
                Joyin.error(JoyinUtil.getMessage('请选择一条数据'))
                return;
            }
            var object = rows[0];
            object.operateType = "revoke";
            Joyin.ajax(basePath + 'invest/period/trdrepoduration/TrdRepoDuration001/revokeCheck', object, function (result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    object.revokeReason = result;
                    WorkFlow.doRevoke(basePath + 'invest/period/trdrepoduration/TrdRepoDuration001/revoke', object, "TrdRepoDuration001_btn_revoke", "TrdRepoDuration002",function(){
                        table.reload();//回调事件请求及回调函数
                    });
                });
            });
        })
        $("#TrdRepoDuration001_btn_export").click(exportExcel)

        // 绑定到期续做按钮事件
        $('#TrdRepoDuration001_btn_add').click(function () {
            var rows = table.getAllCheckedRows();
            if (rows.length != 1) {
                Joyin.error(JoyinUtil.getMessage('请选择一条数据'))
                return;
            }
            var object = rows[0];
            var type=object.type6;
            if (type!=="H01") {//只有交易所债券协议回购可以做到期续做
                Joyin.error(JoyinUtil.getMessage('请选择交易所质押式协议回购！'))
                return;
            }
            var status=object.statusValid;
            if(status!=="01"){
                Joyin.error(JoyinUtil.getMessage('请选择待处理数据处理！'))
                return;
            }
            object.pageModel=PAGE_MODEL_EDIT;
            object.bustype='T56';
            //检查是否交易所债券，是否在到期日续做
            Joyin.ajax(basePath + 'invest/period/trdrepoduration/TrdRepoDuration001/durationCheck', object, function (result) {
                delete object.isTodoList;
                Joyin.dialog('质押式回购到期续做交易',
                    basePath + "invest/period/trdrepoduration/TrdRepoDuration002/init",
                    "98%", "95%", object, function () {
                        table.reload();
                    });
            });




        });
    });
}());

var openOperateWindow = function (tradeId) {
    Joyin.dialog('回购交易查看',
        basePath + "invest/trade/trdrepotrade/TrdRepoTrade002/init?tradeId="+tradeId,
        "98%", "98%", {}, function () {
        });
}

/**
 * 组合信息查看页面
 */
var showPtlManagement=function(portfolioId){
    Joyin.dialog(JoyinUtil.getLabel("PtlManagementInfo002.see"),
        basePath + "invest/portfolio/ptlmanagementinfo/PtlManagementInfo002/init",
        "95%", "90%",{
            pageModel:PAGE_MODEL_REFERENCE,
            portfolioId:portfolioId,
        });
}