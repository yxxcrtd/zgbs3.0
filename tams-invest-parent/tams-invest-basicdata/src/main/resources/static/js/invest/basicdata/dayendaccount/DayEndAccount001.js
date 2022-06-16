$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        var pageParam = {
            account: $('#busiAcctNo').val(),
            finprodId: $('#finprodId').val(),
            tradeMarket: $('#tradeMarket').val(),
            tradeDateBegin: $('#settleDate').startDate(),
            tradeDateEnd: $('#settleDate').endDate(),
            accountStatus: $('#accountStatus').val()
        };
        if ($("#busiAcctNo").val()) {
            pageParam.account = $("#busiAcctNo").val().join(",");
        } else {
            pageParam.portfolioId = null;
        }
        if ($("#finprodId").val()) {
            pageParam.finprodId = $("#finprodId").val().join(",");
        } else {
            pageParam.finprodId = null;
        }
        return pageParam;
    };

    var getSearchParams1 = function() {
        var pageParam = {
            busiAcctNo: $('#busiAcctNoTotal').val(),
            customerId: $('#customerId').val(),
            accountStatus:$('#accountStatusTotal').val(),
            tradeDateBegin: $('#settleDateTotal').startDate(),
            tradeDateEnd: $('#settleDateTotal').endDate(),
            secAcctId: $('#secAcctId').val()
        };
        if ($("#busiAcctNoTotal").val()) {
            pageParam.busiAcctNo = $("#busiAcctNoTotal").val().join(",");
        } else {
            pageParam.busiAcctNo = null;
        }
        return pageParam;
    };

    /**
     * 定义数据表格
     */
    
    var table=$('#dataTable').joyintable({
            ajax: {
                url: 'findPage',//请求路径
                type: 'POST',//请求类型
                data: function() {
                    return getSearchParams();//获取请求参数
                }//ajax请求数据
            },
    /**
     * 定义表格双击事件
     */
        dblclick:function(table, data) {

        },
        isMulti:true,
        /**
         * 定义表头
         */
        cols: [[
            { title: 'DayEndAccount001.dzylb',align:'center',sortable: false, colspan: 6}
            ,{ title: 'DayEndAccount001.cnqssj',align:'center',sortable: false, colspan: 8}
            ,{ title: 'DayEndAccount001.qsdzdsj',align:'center',sortable: false, colspan: 8}
            , {field: 'seqNo', width: 150, visible: false}
            ,{field:'operation', title: 'SysCommon.operation',rowspan: 2, type:'action', buttons: ['edit']}
        ],[
            {field:'busiAcctNo',sortable: false, title: 'DayEndAccount001.busiAcctNo',width:120}
            ,{field:'settleDate', type:'date',sortable: false, title: 'DayEndAccount001.settleDate',width:120}
            // ZGCPCN-260:资产代码后缀去掉 modify by fuyongsheng 20220516 start
            //,{field:'finprodMarketId',sortable: false, title: 'DayEndAccount001.finprodId',width:120}
            ,{field:'finprodMarketId',sortable: false, title: 'DayEndAccount001.finprodId',width:120,render : function(data, type, row, meta) {
                if(data.indexOf(".SZ") > -1){
                    return data.replace(".SZ","");
                }else if(data.indexOf(".SH") > -1){
                    return data.replace(".SH","");
                }}
            }
            // ZGCPCN-260:资产代码后缀去掉 modify by fuyongsheng 20220516 end
            ,{field:'tradeMarketStr',sortable: false, title: 'DayEndAccount001.tradeMarket',width:120}
            ,{field:'accountStatusStr',sortable: false, title: 'DayEndAccount001.accountStatus',width:120}
            ,{field:'finprodTypeStr',sortable: false, title: 'DayEndAccount001.finprodType',width:120}
            ,{field:'commissionAmt2', type:'money',sortable: false, title: 'DayEndAccount001.commissionAmt',width:120}
            ,{field:'jsfAmt2', type:'money',sortable: false, title: 'DayEndAccount001.jsfAmt',width:120}
            ,{field:'zgfAmt2', type:'money',sortable: false, title: 'DayEndAccount001.zgfAmt',width:120}
            ,{field:'stampFee2', type:'money',sortable: false, title: 'DayEndAccount001.stampFee',width:120}
            ,{field:'settleFee2', type:'money',sortable: false, title: 'DayEndAccount001.settleFee',width:120}
            ,{field:'ghfAmt2', type:'money',sortable: false, title: 'DayEndAccount001.ghfAmt',width:120}
            ,{field:'tradeAmt2', type:'money',sortable: false, title: 'DayEndAccount001.tradeAmt',width:120}
            ,{field:'tradeNum2', type:'money',sortable: false, title: 'DayEndAccount001.tradeNum',width:120}
            ,{field:'commissionAmt', type:'money',sortable: false, title: 'DayEndAccount001.commissionAmt',width:120}
            ,{field:'jsfAmt', type:'money',sortable: false, title: 'DayEndAccount001.jsfAmt',width:120}
            ,{field:'zgfAmt', type:'money',sortable: false, title: 'DayEndAccount001.zgfAmt',width:120}
            ,{field:'stampFee', type:'money',sortable: false, title: 'DayEndAccount001.stampFee',width:120}
            ,{field:'settleFee', type:'money',sortable: false, title: 'DayEndAccount001.settleFee',width:120}
            ,{field:'ghfAmt', type:'money',sortable: false, title: 'DayEndAccount001.ghfAmt',width:120}
            ,{field:'tradeAmt', type:'money',sortable: false, title: 'DayEndAccount001.tradeAmt',width:120}
            ,{field:'tradeNum', type:'money',sortable: false, title: 'DayEndAccount001.tradeNum',width:120}
        ]]
    });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        if (data.accountStatusStr != null && (data.accountStatusStr == "一致" || data.accountStatusStr == "手工完成")) {
            layer.alert("只能修改对账不一致和待处理数据");
            return;
        }
        Joyin.dialog(JoyinUtil.getLabel('DayEndAccount001.title'),
                basePath + 'invest/basicdata/dayendaccount/DayEndAccount002/init',//打开新界面
                '80%', '80%', data,function(){
                                    table.reload();//回调函数，表格刷新
                                });
    });


    /**
     * 汇总头寸
     */
    var totaltable=$('#dataTotalTable').joyintable({
        ajax: {
            url: 'selectPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams1();//获取请求参数
            }
            //ajax请求数据
        },

        checkbox: false,
        tree:true,
        tiernum:2,
        treeId: 'childInfo',
        treeUpId: 'parentInfo',
        treeShowName: 'customerName',
        searchOnInit: false,
        lazyload:true,
        /**
         * 定义表头
         */
        cols: [
            {field:'customerName',sortable: false, title: '券商',width:200}
            ,{field:'portfolioId',sortable: false, title: '组合代码',width:120}
            ,{field:'portfolioName',sortable: false, title: '组合名称',width:120}
            ,{field:'secAcctId',sortable: false, title: '证券账户账号',width:150}
            ,{field:'busiAcctNo',sortable: false, title: '资金账户账号',width:150}
            ,{field:'acctName',sortable: false, title: '资金账户名称',width:150}
            ,{field:'settleDate', type:'date',sortable: false, title: '清算日期',width:120}
            ,{field:'accountStatusStr', type:'money',sortable: false, title: '对账状态',width:120}
            // ZGCPCN-260:资产代码后缀去掉 modify by fuyongsheng 20220509 start
            //,{field:'finprodMarketId',sortable: false, title: '资产代码',width:120}
            ,{field:'finprodMarketId',sortable: false, title: '资产代码',width:120,render : function(data, type, row, meta) {
                    if(data.indexOf(".SZ") > -1){
                        return data.replace(".SZ","");
                    }else if(data.indexOf(".SH") > -1){
                        return data.replace(".SH","");
                    }}
            }
            // ZGCPCN-260:资产代码后缀去掉 modify by fuyongsheng 20220509 end
            ,{field:'finprodName',sortable: false, title: '资产名称',width:120}
            ,{field:'tradeMarketStr', sortable: false, title: '交易市场',width:120}
            ,{field:'finprodTypeStr1', sortable: false, title: '类型',width:120}
            ,{field:'xiTongAmount', type:'money',sortable: false, title: '系统持仓',width:120}
            ,{field:'quanShangAmount', type:'money',sortable: false, title: '券商持仓',width:120}
            ,{field:'childInfo', width:150, visible: false}
            ,{field:'parentInfo', width:150, visible: false}
        ]
    });

    /**
     * 画面加载完成
     */
    $(function() {
        $('#DayEndAccount001_btn_export').hide();
        $('.btn-select-wrapper').hide();
        // 绑定汇总头寸查询事件
        $('#DayEndAccount001_total_btn_search').click(function() {
            totaltable.load();
        });
        // 绑定汇总头寸页签重置事件
        $('#DayEndAccount001_total_btn_reset').click(function() {
            $("#busiAcctNoTotal").val('').trigger("change");
            $('#settleDateTotal').val('').trigger("change");
            $('#customerId').val('').trigger("change");
            $('#accountStatusTotal').val('').trigger("change");
            $('#secAcctId').val('').trigger("change");
        });
        // 绑定明细数据页签查询事件
        $('#DayEndAccount001_detail_btn_search').click(function() {
            table.load();
        });
        // 绑定明细数据页签重置事件
        $('#DayEndAccount001_detail_btn_reset').click(function() {
            $("#busiAcctNo").val('').trigger("change");
            $('#finprodId').val('').trigger("change");
            $('#tradeMarket').val('').trigger("change");
            $('#settleDate').val('').trigger("change");
            $('#accountStatus').val('').trigger("change");
        });
        // 绑定查询事件
        $('#DayEndAccount001_btn_account').click(function() {
            Joyin.confirm("是否确认手工对账", function () {
                var dataList = table.getAllCheckedRows();
                Joyin.ajaxJson(basePath + 'invest/basicdata/dayendaccount/DayEndAccount001/qryAccount', dataList, function(result) {
                    if (result.setMessage != null) {
                        layer.alert(result.setMessage);
                    } else {
                        table.reload();
                    }
                });
            })
        });
    });
}());