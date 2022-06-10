$(function () {
    
	var IndexVal= function(num){
		//获取父类的行号参数方法
		return parent.getIndexVal(num);
	};
	
	
	var reload = function(data){
		//将获取的数据静态赋值,并且重载table
		if(data){
			$('#booksetName').val(data.booksetName);
			$('#tableIdFmt').val(data.tableIdFmt);
			$('#offsetFlagFmt').val(data.offsetFlagFmt);
			$('#subjectNo').val(data.subjectNo);
			$('#bookDate').val(data.bookDate);
			$('#booksetId').val(data.booksetId);
			$('#tableId').val(data.tableId);
			$('#busVouchnum').val(data.busVouchnum);
			$('#vouchNum').val(data.vouchNum);
			$('#vouchRemark').val(data.vouchRemark);
			table.reload();
		}
	}
	
	
	//上一行方法
	var upNum = function(data){
		var data = IndexVal(-1);
		if(data){
			reload(data);
		}else{
			layer.alert('已到第一条');
		}
	}
	
	//下一行方法
	var downNum = function(data){
		var data = IndexVal(1);
		if(data){
			reload(data);
		}else{
			layer.alert('已到最末一条');
		}
	}
	
	
	var getSearchParams = function() {
        return {
        	busVouchnum: $("#busVouchnum").val(),
        	vouchNum: $("#vouchNum").val(),
            tableId:$("#tableId").val(),
            booksetId:$("#booksetId").val(),
            bookDate: $('#bookDate').val(),
            subjectNo: $('#subjectNo').val()
        }
    };
	
    /**
     * 定义数据表格
     */
	var table=$("#BankValVoucherQuery002_table").joyintable({
	    ajax: {
            url: 'searchList',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        searchOnInit: true,
        checkbox: false,
		cols: [
		   {field:'subjectNo', title: '科目号',width:200},
           {field:'subjectName', title: '科目名称',width:500},
           {field:'cdFlag', title: '借贷',width:80},
           {field:'ccy', title: '币种',width:120},
           {field:'happenAmt', type:"money", title: '金额',width:150},
           {field:'happenNumber', type:"number", title: '数量',width:130}
		],
		loadonce:true
	});
	
	
	$(function(){
		$("#BankValVoucherQuery002_btn_up").click(function(){
			upNum();
		});
		
		$("#BankValVoucherQuery002_btn_down").click(function(){
			downNum();
		});
	});
	
});