var TrdBondDuration001;
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
        /*if ($("#portfolioId").val()) {
            result.portfolioId = $("#portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }*/
        if ($("#finprodMarketId").val() &&  $('#finprodMarketId').select2('data')[0] && $('#finprodMarketId').select2('data')[0].object) {
            result.finprodId = $('#finprodMarketId').select2('data')[0].object;
        }
        if ($("#chlId").val()) {
        	result.chlId = $("#chlId").val().join(",");
        }
        if ($("#mDate").val()) {
            result.mdateStart = $("#mDate").startDate();
            result.mdateEnd = $("#mDate").endDate();
        }
        if ($("#tradeDate").val()) {
            result.tradeDateStart = $("#tradeDate").startDate();
            result.tradeDateEnd = $("#tradeDate").endDate();
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
        dblclick:function(table, data) {
            let url = "invest/period/trdbondduration/TrdBondDuration002/init";
            let title = "债券还本付息信息查看";
            if (data.paBussType == 11) {
                url = "invest/period/trdbondduration/TrdBondDuration003/init";
                title = "债券行权交易信息查看";
            }
            //在新建界面进行显示单行的详细信息
            data.pageModel = PAGE_MODEL_REFERENCE;
            Joyin.dialog(title,
                basePath + url,
                "98%", "95%", data, function(){});
        },
        cols: [
              {field: 'statusValidString', width: 120, title: '有效状态', sortField: 'statusValid'}
            , {field: 'busiCode', width: 200, title: '业务编号',sortField:'busiCode'}
            , {field: 'paBussTypeString', width: 130, title: '类型',sortable:false,sortField:'PA_BUSS_TYPE'}
            /*, {field: 'portfolioId', width:150, title: '组合代码',
                render : function(data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">'+row.portfolioId+'</a>';
                }}
            , {field: 'portfolioName', width:150, title: '组合名称'}*/
            , {field: 'secAcctName', width:130, title: function(){
                if(ISChongQing == 'Y' ){
                    return JoyinUtil.getLabel("EtrStandardBond002.secAcctId1");
                }else{
                    return JoyinUtil.getLabel("EtrStandardBond002.secAcctId")+JoyinUtil.getLabel("invest.channelId");
                }
            }(), sortField:'secAcctName'}
            , {field: 'finprodMarketId', width:180, title: '资产代码',sortField:'FINPROD_MARKET_ID',
                render : function(data, type, row, meta) {
                    if(row.finprodMarketId !=undefined){
                        return "<a onclick=\"showDetailByFinprodId('"+row.finprodId+"')\" + style='cursor: pointer;'>"+row.finprodMarketId+"</a>";
                    }
                }}
            , {field: 'finprodAbbr', width:180, title: '资产简称',sortField:'finprodAbbr'}
            , {field: 'vDate', width:120, title: '计息开始日',sortField:'vDate'}
            , {field: 'mDate', width:120, title: '计息结束日',sortField:'mDate'}
            , {field: 'payDate', width:120, title: '理论支付日',sortField:'PAY_DATE'}//添加理论支付日
            , {field: 'tradeDate', width:120, title: '资金到账日',sortField:'TRADE_DATE'}
            , {field: 'qty', width:150, title: '券面总额', type:'money',sortField:'qty'}
            , {field: 'fpriceAmt', width:150, title: '理论本息总额', type:'money',sortField:'fpriceAmt'}
            , {field: 'tradeAmt', width:150, title: '实收本息总额', type:'money',sortField:'TRADE_AMT'}
            //多级法人改造 add by caiqun 20200615
            , {field: 'institutionName', width:150, title: '机构名称',sortField:'institutionName',visible:AUTHORITY_TYPE=='02'}
            // //多级法人改造 add by caiqun 20200615
            // , {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit']
            //     ,
            //     render: function (data, type, row, meta) {
            //         if (row.statusValid != '01')
            //             return "";
            //         else
            //         //modifiled by liupengfei for 多级法人改造 确认按钮权限控制 S
            //         if(purviewElements.indexOf('_edit')!=-1){
            //             return '<i class="btn layui-icon edit" title="确认">&#x1005;</i>';
            //         }
            //         //modifiled by liupengfei for 多级法人改造 确认按钮权限控制 E
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
        var confirmTrade = function (data, PAGE_MODEL_EDIT) {
            data.pageModel = PAGE_MODEL_EDIT;
            Joyin.dialog("债券还本付息到期确认",
                basePath + "invest/period/trdbondduration/TrdBondDuration002/init",
                "98%", "95%", data, function () {
                });
        };
        confirmTrade(data, PAGE_MODEL_NEW);
    });

    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 画面加载完成
     */
    $(function () {
        $("#TrdBondDuration001_btn_export").click(exportExcel)
        $("#TrdBondDuration001_btn_search").click(function () {
            table.load();
        })
        $('#TrdBondDuration001_btn_add').click(function() {
            Joyin.dialog("行权赎回新建", basePath
                + "invest/period/trdbondduration/TrdBondDuration003/init", "99%", "99%", {
                pageModel : PAGE_MODEL_NEW
            }, function() {
                table.reload();
            });
        });

        $("#TrdBondDuration001_btn_revoke").click(function () {
            var rows = table.getAllCheckedRows();
            if (rows.length != 1) {
                Joyin.error(JoyinUtil.getMessage('请选择一条数据'))
                return;
            }
            var object = rows[0];
            object.operateType = "revoke";
            let pageId = "TrdBondDuration002";
            if (object.paBussType == 11) {
                pageId = "TrdBondDuration003";
            }
            Joyin.ajax(basePath + 'invest/period/trdbondduration/TrdBondDuration001/revokeCheck', object, function (result) {
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    object.revokeReason = result;
                    WorkFlow.doRevoke(basePath + 'invest/period/trdbondduration/TrdBondDuration001/revoke', object, "TrdBondDuration001_btn_revoke", pageId,function(){
                        table.reload();//回调事件请求及回调函数
                    });
                });
            });
        })
    });
    TrdBondDuration001 = {reloadTable: function () {
            table.reload();
        }}
    // 所有dom对象加载完的处理
    $(document).ready(function() {
        if(REM_TYPE && REM_TYPE == 'R3S5'){
            table.table? table.reload() : table.load();
        }
    })
}(TrdBondDuration001));

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
//不同资产的查看页面
var showDetailByFinprodId=function(finprodId){
    var label='';
    var pagePath='';
    //债券标的信息查看
    label = 'FinBondTargetAsset002.title';
    pagePath = "invest/asset/bond/FinBondTargetAsset002/init";
    Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
        "95%", "90%", {
            pageModel: PAGE_MODEL_REFERENCE,
            finprodId: finprodId
        });//基金类标的信息查看
}