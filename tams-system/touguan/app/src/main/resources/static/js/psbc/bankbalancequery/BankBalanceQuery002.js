$(function () {
    
    /**
     * 定义数据表格
     */
	var table=$("#BankBalanceQuery002_table").joyintable({
	    ajax: {
            url: 'searchList',
            type: 'POST',
            data: {
                    booksetId: $("#booksetId").val(),
                    balanceDateFmt:$("#balanceDateFmt").val(),
                    subjectNo:$("#subjectNo").val(),
                    oCcy:$("#oCcy").val(),
                    finprodId:$("#finprodId").val()
            }
        },
        dblclick:function(table, data) {
            Joyin.dialog(JoyinUtil.getLabel("查看记账凭证："+data.booksetId),
                    JoyinUtil.getRealPath("/psbc/bankbalancequery/BankBalanceQuery002/init"),
                    "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE,
                booksetId:data.booksetId,bookDate:data.bookDate,subjectNo:data.subjectNo,vouchNum:data.vouchNum},
                    function(){});
        },
        searchOnInit: true,
        checkbox: false,
        cols : [ {field : 'bookDate', title : '日期',width:120, sort : true}, 
                 { field : 'vouchNum', title : '记账流水号',width:200, sort : true}, 
                 {field : 'booksetId',title : '账套代码',width:200,sort : true}, 
                 {field : 'booksetName',title : '账套名称', width:180,sort : true }, 
                 {field : 'busiId', title : '资产/负债代码',width:180,sort : true },  
                 {field : 'finprodAbbr',title : '资产/负债名称', width:200,sort : true}, 
                 {field : 'subjectNo', title : '科目号',width:120,sort : true },
                 {field : 'subjectName', title : '科目名称', width:350,sort : true}, 
                 {field : 'cdFlagFmt',title : '借贷方向', width:100,sort : true }, 
                 {field : 'ccyFmt',title : '币种',sortField:'ccy', width:160,sort : true},
                 {field : 'happenAmt', type:"money", title : '发生额', width:120,sort : true},
                 {field : 'happenNumber', type:"number", title : '发生数量',width:140, sort : true},
                 {field : 'balFlagFmt',title : '余额方向', width:100,sort : true }, 
                 {field : 'balance', type:"money", title : '余额金额', width:120,sort : true},
                 {field : 'tradeId',title : '业务编码', width:160,sort : true},
                 {field : 'comTableIdFmt', title : '业务类别',sortField:'comTableId',width:260,sort : true }
                 ],
		loadonce:true
	});
	
});