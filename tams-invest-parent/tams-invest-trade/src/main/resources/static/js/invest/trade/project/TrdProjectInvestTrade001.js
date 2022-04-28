(function () {

    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        var result = $.serializeToObject('#searchForm');
        if ($("#projectType").val()) {
            result.projectType = $("#projectType").val().join(",");
        } else {
            result.projectType = null;
        }
        if ($("#chlFinprodId").val()) {
        	result.chlFinprodId = $("#chlFinprodId").val().join(",");
        }
        // 项目类资产五级分类改造 add by renping 2019-08-01 start
        if ($("#type9").val()) {
        	result.type9 = $("#type9").val().join(",");
        } else {
        	result.type9 = null;
        }
        if ($("#type10").val()) {
        	result.type10 = $("#type10").val().join(",");
        } else {
        	result.type10 = null;
        }
        if ($("#type11").val()) {
        	result.type11 = $("#type11").val().join(",");
        } else {
        	result.type11 = null;
        }
        // 项目类资产五级分类改造 add by renping 2019-08-01 end
        if ($("#ps").val()) {
            result.ps = $("#ps").val().join(",");
        } else {
            result.ps = null;
        }
        if ($("#portfolioId").val()) {
            result.portfolioId = $("#portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }
        result.tradeDateStart = $('#tradeDate').startDate();//开始日
        result.tradeDateEnd = $('#tradeDate').endDate();//结束日
        //是否查询减值清理交易 add by yzg 20201029 start
        result.queryType = IS_QUERY_ASSETS_LOSS;
        //是否查询减值清理交易 add by yzg 20201029 end
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
            Joyin.dialog(JoyinUtil.getLabel("TrdProjectInvestTrade002.view"),
                basePath + "invest/trade/project/TrdProjectInvestTrade002/init",
                "98%", "95%", data, function () {
                });
        },
        cols: [
            {field: 'statusValidString', width: 120, title: '有效状态', sortField:"statusValid" }
            , {field: 'bizCode', width:150, title: '业务编号', sortField:"bizCode"}
            , {field: 'entrustId', width:150, title: '委托代码', sortField:"ENTRUST_ID"}
            , {field: 'portfolioId', width:150, title: '组合代码', sortField:"PORTFOLIO_ID",
                render : function(data, type, row, meta) {
                    return '<a onclick="showPtlManagement(\'' + row.portfolioId
                        + '\')" class="pro-link"  style="cursor: pointer;">'+row.portfolioId+'</a>';
                }}
            , {field: 'portfolioName', width:150, title: '组合名称', sortField:"PORTFOLIO_NAME"}
            // , {field: 'chlFinprodId', width:150, title: 'TrdProjectInvestTrade001.chlFinprodId', sortField:"chlFinprodId"}
            , {field: 'finprodMarketId', width:150, title: '资产代码', sortField:"FINPROD_MARKET_ID",
                render : function(data, type, row, meta) {
                    if(row.finprodMarketId !=undefined){
                        return "<a onclick=\"showDetailByFinprodId('"+row.finprodId+"')\" + style='cursor: pointer;'>"+row.finprodMarketId+"</a>";
                    }
                }}
            , {field: 'finprodName', width:150, title: '资产名称', sortField:"finprodName"}
            // 项目类资产五级分类改造 add by renping 2019-08-01 start
            , {field: 'projectTypeString', width:150, title: '资产类型', sortField:"projectType",visible:false}
            , {field: 'type9Name', width:150, title: 'TrdProjectInvestTrade001.type9', sortField:"type9"}
            , {field: 'type10Name', width:150, title: 'TrdProjectInvestTrade001.type10', sortField:"type10"}
            , {field: 'type11Name', width:150, title: 'TrdProjectInvestTrade001.type11', sortField:"type11",visible:IS_SHOW_TYPE == '01'?true:false}
            // 项目类资产五级分类改造 add by renping 2019-08-01 end
            , {field: 'psString', width:150, title: '交易方向', sortField:"PS"}
            , {field: 'tradeDate', width:150, title: '交易日期', sortField:"TRADE_DATE"}
            , {field: 'prinAmt', width:150, title: '本金金额',type:'money', textrange:2, sortField:"PRIN_AMT"}
            , {field: 'unitCprice', width:150, title: '净价', type:'number', textrange:2,sortField:"UNIT_CPRICE"}
            , {field: 'unitFprice', width:150, title: '全价', type:'number', digit:4,sortField:"UNIT_FPRICE"}
            , {field: 'tradeAmt', width:150, title: '实付金额',type:'money', textrange:2,sortField:"TRADE_AMT"}
            , {field: 'ccy', width:100, title: '币种', sortField:"CCY"}
            , {field: 'remark', width:150, title: '备注', sortField:"REMARK"}
        ],
        initSort: {field: 'TRADE_DATE',type: 'desc'},
        checkbox: true,
        progressBar: false
    });

    var confirmTrade = function (data, pageModel) {
        data.pageModel = pageModel;
        var title = JoyinUtil.getLabel("TrdProjectInvestTrade002.edit")
        if (pageModel == PAGE_MODEL_NEW) {
            title = JoyinUtil.getLabel("TrdProjectInvestTrade002.new")
        }
        Joyin.dialog(title,
            basePath + "invest/trade/project/TrdProjectInvestTrade002/init",
            "98%", "95%", data, function () {
            });
    };

    var exportExcel = function () {
        Joyin.exportExcel('export', getSearchParams());
    };

    /**
     * 画面加载完成
     */
    $(function () {
        $("#TrdProjectInvestTrade001_btn_search").click(function () {
            table.load();
        })
        $("#TrdProjectInvestTrade001_btn_add").click(function () {
            confirmTrade({}, PAGE_MODEL_NEW);
        })
        $('#TrdProjectInvestTrade003_btn_add').click(function() {
            Joyin.dialog('项目类资产多组合交易新建', basePath
                            + "invest/trade/project/TrdProjectInvestTrade003/init", "99%", "99%", {
                        pageModel : PAGE_MODEL_NEW
                    }, function() {
                        table.reload();
                    });
        });
        $("#TrdProjectInvestTrade001_btn_export").click(function() {
            exportExcel();
        });;

        $("#TrdProjectInvestTrade001_btn_revoke").click(function () {
            var rows = table.getAllCheckedRows();
            if (rows.length != 1) {
                Joyin.error(JoyinUtil.getMessage('请选择一条数据'))
                return;
            }
            var object = rows[0];
            /* CQTZJYUAT-305 内部转仓只能撤销卖出交易 add by kongzhifang 20211207 start */
            if(object.psString == '买入'){
                Joyin.alert('内部转仓只能撤销卖出交易，买入自动撤销')
                return;
            }
            /* CQTZJYUAT-305 内部转仓只能撤销卖出交易 add by kongzhifang 20211207 end */
            object.operateType = "revoke";
            Joyin.ajax(basePath + 'invest/trade/project/TrdProjectInvestTrade001/revokeCheck', object, function (result) {
                var resdata = result.data;
                Joyin.prompt(JoyinUtil.getLabel("pingan.common.revokeReason"), 0, function (result) {
                    Joyin.confirm(resdata, function() {
                    result = result.replace(/\s+/g,"");
                    if (!result) {
                        result = '无';
                    }
                    object.revokeReason = result;
                    WorkFlow.doRevoke(basePath + 'invest/trade/project/TrdProjectInvestTrade001/revoke', object, "TrdProjectInvestTrade001_btn_revoke", "TrdProjectInvestTrade002",function(){
                        table.reload();//回调事件请求及回调函数
                    });
                    });
                });
            });
        })
      //是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#type11Div').show();
        }else{
        	$('#type11Div').hide();
        }
    });
}());

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
    var label= 'FinProjectTargetAsset002.title';
    var pagePath = "invest/asset/project/FinProjectTargetAsset002/init";
    Joyin.dialog(JoyinUtil.getLabel(label),basePath+pagePath,
        "95%", "90%",{
            pageModel:PAGE_MODEL_REFERENCE,
            finprodId:finprodId
        });
}