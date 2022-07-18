var parenPage = function () {
    /**
     * 获取查询参数
     */
    var getPageParams = function () {
        var result = $.serializeToObject('#dvpTransferForm');
        if ($("#portfolioId").val()) {
            result.portfolioId = $("#portfolioId").val().join(",");
        } else {
            result.portfolioId = null;
        }
        if ($("#finprodId").val()) {
            result.finprodId = $("#finprodId").val().join(",");
        } else {
            result.finprodId = null;
        }
        if ($("#paBusinessType").val()) {
            result.paBusinessType = $("#paBusinessType").val().join(",");
        } else {
            result.paBusinessType = null;
        }
        if ($("#cashDate").val()) {
            result.cashDateStart = $("#cashDate").startDate();
            result.cashDateEnd = $("#cashDate").endDate();
        }
        return result;
    };
    
    var tabcols = [
            {field: 'bizCode', title: '业务编号', width: 200, sortable: false, render: function (data, type, row, meta) {
                    if(row.paBizType=='151'||row.paBizType=='180'||row.paBizType=='202'||row.paBizType=='212'){
                        return data;
                    }
                    return "<a class='pro-link' style='cursor: pointer;' " +
                        "href='javascript:openOperateWindow(\"" + STATUS_VALID_05 + "\",\"" + row.paBizType + "\",\"" + row.portfolioId + "\",\""
                        + row.bizCode + "\", null, null, null, null, null, \""+row.profitType+"\",\"" + row.finprodId + "\",\"" + row.operateType+ "\",null,null,\"" + row.paBizTypeAdd+ "\")'>" + data + "</a>";
                }
            }
            , {field: 'orderId', title: '指令编号', width: 150, sortable: false,visible : false}
            , {field: 'paBizTypeString', title: '业务类型', width: 150, sortable: false}
            , {field: 'payTypeString', title: '指令类型', width: 150, sortable: false}
            // 查询列表中增加“付款账户”、“付款账户名称”、“收款账户”、“收款账户名称”、“划款用途”、“币种” 20191030 add by renping start
            , {field: 'purpose', title: '划款用途', width: 150, sortable: false, visible : false}
            // 查询列表中增加“付款账户”、“付款账户名称”、“收款账户”、“收款账户名称”、“划款用途”、“币种” 20191030 add by renping end
            , {field: 'portfolioId', title: '组合代码', width: 150, sortable: false}
            , {field: 'finprodMarketId', title: '产品/资产代码', width: 150, sortable: false}
            , {field: 'finprodName', title: '产品/资产名称', width: 150, sortable: false}
            , {field: 'settleDate', title: '现金流日期', width: 150, sortable: false}
            , {field: 'settleAmt', title: '划款金额', width: 200, type: 'number', sortable: false}
            , {field: 'ccyString', title: '币种', width: 150, sortable: false}
            // 查询列表中增加“付款账户”、“付款账户名称”、“收款账户”、“收款账户名称”、“划款用途”、“币种” 20191030 add by renping start
            , {field: 'payAcctNo', title: '付款账户', width: 180, sortable: false, visible : false}
            , {field: 'payAcctName', title: '付款账户名称', width: 160, sortable: false, visible : false}
            , {field: 'recAcctNo', title: '收款账户', width: 180, sortable: false, visible : false}
            , {field: 'recAcctName', title: '收款账户名称', width: 150, sortable: false, visible : false}
            // 查询列表中增加“付款账户”、“付款账户名称”、“收款账户”、“收款账户名称”、“划款用途”、“币种” 20191030 add by renping end
            , {field: 'sendDate', title: '发送时间', width: 150, sortable: false}
            , {field: 'sendStatusString', title: '发送状态', width: 200, sortable: false,
                render : function(data, type, row, meta) {
                    return row.sendStatusString;
                },sortField:"sendStatus"
              }
            , {field: 'receiveStatusString', title: '指令接收状态', width: 150, sortable: false}
            , {field: 'dealStatusString', title: '处理状态', width: 200, sortable: false}
        ];

    var orderDataTable = $("#orderDataTable").joyintable({
        ajax: {
            url: 'findOrderData',//请求路径
            type: 'POST',//请求类型
            data: function () {
                return getPageParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox: true,
        searchOnInit: false,
        /**
         * 定义表头;
         */
        cols: tabcols ,
    });
 // 发送状态link页面，各系统酌情使用
    orderDataTable.bind('click','.sendStatusLink', function(row) {
        console.log(row.finprodId);
       var label='';
       var pagePath='';
       pagePath = "invest/settle/order/StlOrderManagementResend002/init";
       Joyin.dialog(JoyinUtil.getLabel(label), basePath + pagePath,
           "95%", "90%", {
               pageModel: PAGE_MODEL_REFERENCE,
               finprodId: row.finprodId
           });
    });

    var exportExcel = function () {
        Joyin.exportExcel('export', getPageParams());
    };
    //重新发送
    var resendOrder = function(){
        if(orderDataTable!=null && orderDataTable!=''){
            var selArr = orderDataTable.getAllCheckedRows();
            //判断是否选中元素
            if( selArr != null &&  selArr.length > 0 ){
                var data = {};
                data.selectedItem = selArr;
                Joyin.ajaxJson(basePath + 'invest/settle/order/StlOrderManagement001/resendOrderInfo', data,
                        function(result) {
                            var finprodIdStr = "";
                            $.each(selArr, function(idx,v) {
                                finprodIdStr = finprodIdStr + v.finprodId + ",";
                            });
                        }
                    );
            }else{
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
        }else{
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }      
    }
    
    //刷新
    var refresh = function(){
        if(orderDataTable!=null && orderDataTable!=''){
            Joyin.ajaxJson(basePath + 'invest/settle/order/StlOrderManagement001/refreshInfo', 
                        function(result) {
                            orderDataTable.reload();
                        }
            );
        } 
        
    }
    
    //Add By ZhangJin For 邮储银行--从远程文件服务器下载附件 Start 20191213
    //附件下载
    var download =function(){
        var rows=orderDataTable.getAllCheckedRows();
        if(rows.length<=0){
            layer.alert("请选择一条记录!");
            return false;
        }else{
            if(rows.length!=1){
                layer.alert("下载附件时只可选择一条记录!");
                return false;
            }
        }
        var row=rows[0];
        var bizCode=row.bizCode;
        var param = $.serializeToObject();
        param.bizCode=bizCode;
        Joyin.ajaxSync('checkFileExists', param, function(result) {
            if (typeof result.data == 'undefined' || result.data == '') {
                layer.alert('附件不存在!');
            } else {
            	param.seqNo = result.data;
            	Joyin.exportExcel('download', param);
            }
        });
    }
    //Add For 邮储银行--从远程文件服务器下载附件 End 20191213
    
    //作废
    var invalidOrder = function(){
        if(orderDataTable!=null && orderDataTable!=''){
            var selArr = orderDataTable.getAllCheckedRows();
            //判断是否选中元素
            if( selArr != null &&  selArr.length > 0 ){
                var data = {};
                data.selectedItem = selArr;
                Joyin.ajaxJson(basePath + 'invest/settle/order/StlOrderManagement001/invalidOrderInfo', data,
                        function(result) {
                            var finprodIdStr = "";
                            $.each(selArr, function(idx,v) {
                                finprodIdStr = finprodIdStr + v.finprodId + ",";
                            });
                        }
                    );
            }else{
                Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
                return;
            }
        }else{
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }      
    }

    $(function () {
        $("#StlOrderManagement001_btn_search").click(function () {
            orderDataTable.load();
        })

        $("#StlOrderManagement001_btn_export").click(exportExcel)

        $('#DvpOrderManagement001_btn_resend').click(function () {
            resendOrder();
        });
        $('#DvpOrderManagement001_btn_refresh').click(function () {
            refresh();
        });
        $('#DvpOrderManagement001_btn_invalid').click(function () {
        	invalidOrder();
        });
        $('#StlOrderManagement001_btn_download').click(function () {
            download();
        });
    })
    $('.download').download();
    return {
        refreshTable: function () {
            orderDataTable.reload();
        }
    };
    
 

}();