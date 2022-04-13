
var indexParams;
var table;

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
            subjectNo: $('#subjectNo').val(),
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
	table=$("#dataTable").joyintable({
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
                    JoyinUtil.getRealPath("/valuation/accounadjustmanage/voucherquery/ValVoucherQuery002/init"),
                    "90%", "90%", {pageModel:PAGE_MODEL_EDIT,
                busVouchnum:data.busVouchnum,tableId : data.tableId,booksetId:data.booksetId,bookDate:data.bookDate,
                	subjectNo:data.subjectNo,booksetName:data.booksetName,offsetFlagFmt:data.offsetFlagFmt,
                	tableIdFmt:data.tableIdFmt,vouchRemark:data.vouchRemark},
                    function(){
                		//双击结束后将index赋值为空.避免出现问题
                		index=null;
                	});
        },
        click:function(table,data){
            //2.获取所有行
            var rowList = table.getAllRows();
            //获取行号
            var index = data.LAY_TABLE_INDEX;
            if(index == undefined){
                for (var i = 0 ;i < rowList.length;i++){
                    if(JSON.stringify(rowList[i]).indexOf(JSON.stringify(data)) != -1){
                        index = i;
                        break;
                    }
                }
            }
            var fixedCheckBox = $('#dataTable').next('.layui-table-view').find('.layui-table-fixed').find('tr[data-index="' + index + '"]').find(':checkbox[name="layTableCheckbox"]:checked');
            if(rowList != '' && rowList!=undefined){
                for(var j=0;j<rowList.length;j++){
                    if(index != j ) {
                        var checkBox = $('#dataTable').next('.layui-table-view').find('.layui-table-fixed').find('tr[data-index="' + j + '"]').find(':checkbox[name="layTableCheckbox"]:checked');
                        if (data.booksetId == rowList[j].booksetId && data.busVouchnum == rowList[j].busVouchnum ) {
                            //勾选时：选中的判断，全部是否选中
                            if (fixedCheckBox.length > 0 && checkBox.length == 0) {
                                table.checkRow(j);
                            }//取消时：
                            else if (fixedCheckBox.length == 0 && checkBox.length > 0) {
                                table.checkRow(j);
                            }
                        }else if( data.busVouchnum != rowList[j].busVouchnum && checkBox.length > 0){
                            table.checkRow(j);
                        }
                    }
                }
            }
        },
        // check:function(table,data,index){
        //     //2.获取所有行
        //     var rowList = table.getAllRows();
        //     // if(rowList != '' && rowList!=undefined){
        //     //     for(var j=0;j<rowList.length;j++){
        //     //         if(data.busVouchnum == rowList[j].busVouchnum && data.LAY_TABLE_INDEX != j){
        //     //             table.checkRow(j);
        //     //         }
        //     //     }
        //     // }
        // },
		cols: [
		   {field:'bookDate', title: '记账日期',width:120},
		   {field:'happenDate', title: '生成日期',width:120},
		   {field:'booksetId', title: '账套代码',width:140},
		   {field:'booksetName', title: '账套名称',width:160},
		   {field:'tableIdFmt', title: '业务类别', sortField:'tableId',width:250},
           {field:'busiId', title: '资产/负债代码',width:180},                  //凭证是否展示分录为“否”时隐藏
           {field:'busiName', title: '资产/负债名称',width:180},                //凭证是否展示分录为“否”时隐藏
		   {field:'busVouchnum',title: '凭证编号',width:160},
		   // {field:'bankSerialNumber', title: '银行账记账流水号',width:100},
		   {field:'subjectNo', title: '科目号',width:160},                    //凭证是否展示分录为“否”时隐藏；
		   {field:'subjectName', title: '科目名称',width:380},                 //凭证是否展示分录为“否”时隐藏；
		   {field:'cdFlagFmt', sortField:'cdFlag', title: '借贷',width:80},                          //凭证是否展示分录为“否”时隐藏；
		   {field:'ccyFmt', sortField:'ccy', title: '原币币种',width:140},                            //凭证是否展示分录为“否”时隐藏；
           {field:'bCcyFmt', sortField:'bCcy', title: '记账币种',width:140},                            //凭证是否展示分录为“否”时隐藏；
           {field:'happenAmt', type:"money", title: '原币金额',width:200},        //凭证是否展示分录为“否”时隐藏；
           {field:'bHappenAmt', type:"money", title: '本币金额',width:200},        //凭证是否展示分录为“否”时隐藏；
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
		   {field:'action', title:'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;操 作', type:'action', fixed: 'right',width:120,
		       buttons:[{text:'&#xe642;',clas:'edit',title:'修改凭证备注'}]
//			   render:function(data,type,row,meta){
//  					var html= "";
//  	                  if(purviewElements.indexOf('_edit')!=-1){
//  	                	  html = html+"<i class='iconfont icon-edit edit' title = '修改凭证备注'></i>";
//  	                  }
//  	                  return html;
//  					} 
		   }],
	});


	/**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        Joyin.dialog(JoyinUtil.getLabel("ValVoucherQuery001.editRemark"),
                basePath + "valuation/accounadjustmanage/voucherquery/ValVoucherQuery003/init",
                "40%", "40%", {pageModel:PAGE_MODEL_EDIT, vouchNum:data.vouchNum},function(){
                    table.reload();
                });
    });

    /**
     * @param target 被比较的对象
     * @param data 比较对象
     */
    var compare = function(target, data) {
        if(data['LAY_CHECKED'] != 'undefined') {
            delete data['LAY_CHECKED'];
            delete target['LAY_CHECKED'];
        }
        if(typeof data['LAY_TABLE_INDEX'] != 'undefined') {
            delete data['LAY_TABLE_INDEX'];
            delete target['LAY_TABLE_INDEX'];
        }
        return _.isEqual(target, data);
    };

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

    var printVoucher = function () {
        var selectedRows = table.getAllCheckedRows();
        if (!selectedRows || selectedRows.length != 1) {
            Joyin.error(JoyinUtil.getMessage('请选择一条数据！'));
            return;
        }
        var data = selectedRows[0];
        window.open("printVoucher?busVouchnum=" + data.busVouchnum
            + "&vouchNum=" + data.vouchNum + "&booksetId=" + data.booksetId
            + "&bookDate=" + data.bookDate + "&booksetName=" + data.booksetName
            + "&tableId=" + data.tableIdFmt ,
            "_blank",
            "fullscreen=yes,resizeable=no,scrollbars=yes,channelmode=yes,left=0,top=0,width=1366,height=768")
    };
    
    var booksetIdChange = function () {
        var booksetType = $('#booksetType').val();
        var object = {booksetTypes:booksetType};
        Joyin.ajaxJson(basePath + '/valuation/accounadjustmanage/voucherquery/ValVoucherQuery001/findBooksetIds', object, function(result) {
        	$("#booksetId").empty();
        	$.each(result, function(index, item) {                   
        		$('#booksetId').append(new Option(item.text,item.id));
            });
		}); 
    };

	/**
     * 画面加载完成
     */
    $(function() {
        // 绑定按钮事件
        $('#ValVoucherQuery001_btn_search').click(function() {
//            if(!$.trim($('#booksetId').val())){
//                Joyin.error("请选择所属账套。");
//                return;
//            }
        	table.load();
            if($("#isShowfl").next().hasClass('layui-form-onswitch')){
                table.showColumns([
                        {field:'subjectNo', visible: true},
                        {field:'subjectName', visible: true},
                        {field:'cdFlagFmt', visible: true},
                        {field:'ccyFmt', visible: true},
                        {field:'happenAmt', visible: true},
                        {field:'bCcyFmt', visible: true},
                        {field:'bHappenAmt', visible: true},
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
                    {field:'bCcyFmt', visible: false},
                    {field:'bHappenAmt', visible: false},
                    {field:'happenNumber', visible: false},
                    {field:'balanceDirectionFmt', visible: false},
                    {field:'balanceAmount', visible: false},
                    {field:'busiId',visible: false},
                    {field:'busiName',visible:false }
                ]);
            }
        });
        $('#ValVoucherQuery001_btn_export').click(function() {
            exportExcel();
        });

        $('#ValVoucherQuery001_btn_print').click(function() {
            printVoucher();
        });
        
        $('#booksetType').change(function(){
        	booksetIdChange();
        });
    });
	
});


function getIndexVal(num){
	return indexParams(num);
}