
var indexParams;

$(function () {
    
	var index;

    /**
     * 获取查询参数
     */
	var getSearchParams = function() {
        return {
            booksetId: $.trim($('#booksetId').val()),
            tableIds: $.trim($('#tableId').val()),     
            busiId: $('#busiId').val(),     
            bookDateBegin: $('#bookDate').startDate(),
            bookDateEnd: $('#bookDate').endDate(),
            tradeId: $.trim($('#tradeId').val()),
            bookType: $('#bookType').val(),
            busVouchnum: $('#busVouchnum').val(),
            happenDateBegin: $("#happenDate").startDate(),
            happenDateEnd: $("#happenDate").endDate(),
            pTradeId : $("#pTradeId").val(),
            isShowfl : $("#isShowfl").next().hasClass('layui-form-onswitch') ? 'yes' : 'no'
        }
    };
    
    //获取根据行数获取参数
    indexParams = function(num){
    	//判断需要获取的行号
		 var row = index+parseInt(num);
		 //如果行号小于0.直接取消
	    	if(row>=0){
	    		//获取需要获取行号的数据
	    		var data = table.getRow(row);
	    		//如果数据未空.则可能为最后一条.则停止
	    		if(data){
	    			//获取数据后将当前页面行号与查询行号调整为一致
		    		index+=parseInt(num);
	    			return {
		    			busVouchnum:data.busVouchnum,
		    			tableId : data.tableId,
		    			booksetId:data.booksetId,
		    			bookDate:data.bookDate,
		            	subjectNo:data.subjectNo,
		            	booksetName:data.booksetName,
		            	offsetFlagFmt:data.offsetFlagFmt,
		            	tableIdFmt:data.tableIdFmt,
		            	vouchRemark:data.vouchRemark
		            }
	    		}
	    	}
    }
    
    
    
    /**
     * 定义数据表格
     */
	var table=$("#dataTable").joyintable({
	    ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
	
        },
		dblclick:function(table, data) {
			//双击事件获取当前行号
			index = table.getAllRows().indexOf(data);
            Joyin.dialog(JoyinUtil.getLabel("查看凭证编号："+data.busVouchnum),
                    JoyinUtil.getRealPath("/psbc/bankvoucherquery/BankValVoucherQuery002/init"),
                    "90%", "90%", {pageModel:PAGE_MODEL_EDIT,
                busVouchnum:data.busVouchnum,tableId : data.tableId,booksetId:data.booksetId,bookDate:data.bookDate,
                	subjectNo:data.subjectNo,booksetName:data.booksetName,offsetFlagFmt:data.offsetFlagFmt,
                	tableIdFmt:data.tableIdFmt,vouchRemark:data.vouchRemark},
                    function(){
                		//双击结束后将index赋值为空.避免出现问题
                		index=null;
                	});
        },
		cols: [
           {field:'taskStatus', title: '发送状态',width:100},    
		   {field:'bookDate', title: '记账日期',width:120},
		   {field:'happenDate', title: '生成日期',width:120},
		   {field:'booksetId', title: '账套代码',width:140},
		   {field:'booksetName', title: '账套名称',width:160},
		   {field:'coreId', title: '核心流水号',width:140},
		   {field:'tableIdFmt', title: '业务类别', sortField:'tableId',width:250},
           {field:'busiId', title: '资产/负债代码',width:180},                  //凭证是否展示分录为“否”时隐藏
           {field:'busiName', title: '资产/负债名称',width:180},                //凭证是否展示分录为“否”时隐藏
		   {field:'busVouchnum',title: '凭证编号',width:160},
		   {field:'subjectNo', title: '科目号',width:160},                    //凭证是否展示分录为“否”时隐藏；
		   {field:'subjectName', title: '科目名称',width:380},                 //凭证是否展示分录为“否”时隐藏；
		   {field:'cdFlagFmt', sortField:'cdFlag', title: '借贷',width:80},                          //凭证是否展示分录为“否”时隐藏；
		   {field:'ccyFmt', sortField:'ccy', title: '币种',width:140},                            //凭证是否展示分录为“否”时隐藏；
		   {field:'happenAmt', type:"money", title: '金额',width:200},        //凭证是否展示分录为“否”时隐藏；
		   {field:'happenNumber', type:"number", title: '数量',width:100},    //凭证是否展示分录为“否”时隐藏；BAL_FLAG
		   {field:'tradeId',title: '业务编号',width:200},
		   {field:'pTradeId',title: '原始业务编号',width:120},
//	       {field:'balanceDirectionFmt',sortField:'balanceDirection', title: '余额方向',width:120},  //凭证是否展示分录为“否”时隐藏
//           {field:'balanceAmount',title: '余额金额',width:120},
           {field:'bookTypeFmt',title: '凭证类型',sortField:'bookType',width:120},
		   {field:'offsetFlagFmt',sortField:'offsetFlag', title: '冲销标识',width:100},
		   {field:'vouchRemark', title: '摘要',width:360},
		   {field:'createUser', title: '制作人',width:120},
		   {field:'vouchNum', title: 'vouchNum',visible: false, width:120},
		   {field:'right', title:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', type:'action', fixed: 'right',
		       buttons:[{text:'&#xe642;',clas:'edit',title:'修改凭证备注'}] }
		],
	});
	
	
	/**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        Joyin.dialog(JoyinUtil.getLabel("修改摘要"),
                basePath + "psbc/bankvoucherquery/BankValVoucherQuery003/init",
                "40%", "40%", {pageModel:PAGE_MODEL_EDIT, vouchNum:data.vouchNum},function(){
                    table.reload();
                });
    });
	
	/**
	 * 导出数据
	 */
    var exportExcel = function() {
        if (table != null && table != '') {
            var selArr = table.getAllCheckedRows();
            var datas = JSON.stringify(selArr);
        }
        if (!selArr || selArr.length == 0) {
            var paramObj = getSearchParams();
            Joyin.exportExcel('export', paramObj);
        } else {
            Joyin.exportExcel('export', {
                datas : datas
            });
        }
    };
	/**
     * 画面加载完成
     */
    $(function() {
        // 绑定按钮事件
        $('#BankValVoucherQuery001_btn_search').click(function() {
//            var data=$.serializeToObject('#searchForm');
//            Joyin.ajaxJson(basePath + 'psbc/bankvoucherquery/BankValVoucherQuery001/checkQuery', data, function(result) {
//                if(!table.table) {
//                    table.load();
//                }
//                else {
//                    table.reload();
//                }
//            });
            if(!$.trim($('#booksetId').val())){
                Joyin.error("请选择所属账套。");
                return;
            }
            table.load();
            if($("#isShowfl").next().hasClass('layui-form-onswitch')){
                table.showColumns([
                        {field:'subjectNo', visible: true},
                        {field:'subjectName', visible: true},
                        {field:'cdFlagFmt', visible: true},
                        {field:'ccyFmt', visible: true},
                        {field:'happenAmt', visible: true},
                        {field:'happenNumber', visible: true},
                        {field:'balanceDirectionFmt', visible: true},
                        {field:'balanceAmount', visible: true},
                        {field:'busiId',visible: true},             
                        {field:'busiName',visible: true}
                    ]);
            }
            else{
                table.showColumns([
                    {field:'subjectNo', visible: false},
                    {field:'subjectName', visible: false},
                    {field:'cdFlagFmt', visible: false},
                    {field:'ccyFmt', visible: false},
                    {field:'happenAmt', visible: false},
                    {field:'happenNumber', visible: false},
                    {field:'balanceDirectionFmt', visible: false},
                    {field:'balanceAmount', visible: false},
                    {field:'busiId',visible: false},             
                    {field:'busiName',visible:false }
                ]);
            }
        });
        $('#BankValVoucherQuery001_btn_export').click(function() {
            exportExcel();
        });
    });
	
});


function getIndexVal(num){
	return indexParams(num);
}