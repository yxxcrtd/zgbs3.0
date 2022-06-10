$(function () {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
            balanceDateBegin: $('#balanceDate').startDate(),
            balanceDateEnd: $('#balanceDate').endDate(),
            booksetId: $.trim($('#booksetId').val()),
            finprodId: $.trim($('#finprodId').val()),
            subjectNo: $('#subjectNo').val(),      
            subjectLevel: $('#subjectLevel').val(), 
            ccy: $('#ccy').val(), 
            issueType:$('#issueType').val()
        }
    };
    
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
            var issueType=$('#issueType').val();
            if(issueType=='02'){
                Joyin.dialog(JoyinUtil.getLabel("查看账套号："+data.booksetId),
                        JoyinUtil.getRealPath("/psbc/bankbalancequery/BankBalanceQuery002/init"),
                        "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE,
                    booksetId:data.booksetId,finprodId:data.finprodId,balanceDateFmt : data.balanceDateFmt,subjectNo:data.subjectNo,oCcy:data.oCcy},
                        function(){});
            }else{
                Joyin.dialog(JoyinUtil.getLabel("查看科目号："+data.subjectNo),
                        JoyinUtil.getRealPath("/psbc/bankbalancequery/BankBalanceQuery003/init"),
                        "90%", "90%", {pageModel:PAGE_MODEL_REFERENCE, balanceDateFmt : data.balanceDateFmt,
                    subjectNo1:data.subjectNo,finprodId:data.finprodId,oCcy:data.oCcy},
                        function(){});
            }
        },
		checkbox: true,
		cellMinWidth : 100,
		cols: [
		       {field:'balanceDateFmt', title: '记账日期', sortField:'vouchNum',width:120, sort: true},
		       {field:'finprodId', title: '产品代码',width:150, sort: true},
		       {field:'booksetId', title: '账套代码',width:150, sort: true},
               {field:'bookSetName', title: '账套名称',width:200, sort: true}, 
//               {field:'tableName', title: '科目场景',width:200, sort: true},
               {field:'subjectNo', title: '科目号',width:120, sort: true},
               {field:'subjectName', title: '科目名称',width:200, sort: true},
               {field:'oCcy', title: '原币币种',width:90, sort: true,visible:false},
               {field:'oCcyFmt', title: '原币币种', sortField:'oCcy',width:130, sort: true},
               {field:'bCcy', title: '记账币种',width:90, sort: true,visible:false},
               {field:'bCcyFmt', title: '记账币种', sortField:'bCcy',width:130, sort: true},
               {field:'oAmt', type:"money", title: '原币余额',width:150, sort: true},
//               {field:'', type:"money", title: '汇率',width:150, sort: true},
               {field:'bAmt', type:"money", title: '本位币余额',width:150, sort: true},
               {field:'balFlag', title: '借/贷',width:80, sort: true,visible:false},
               {field:'balFlagFmt', title: '借/贷', sortField:'balFlag',width:80, sort: true}, 
               {field:'numAmt', type:"number", title: '数量',width:120, sort: true}
        ],
	});
	
	var exportExcel = function() {
	    var selArr;
        var datas;
	    if (table != null && table != '') {
            selArr = table.getAllCheckedRows();
            datas = JSON.stringify(selArr);
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
        $('#BankBalanceQuery001_btn_search').click(function() { 
            table.load();
        });
        $('#BankBalanceQuery001_btn_export').click(function() {
            exportExcel();
        });
        
        $("#booksetId").on('change',function(){
            // 不知道调用何种方法，框架没有说明，将js从框架中拿出
            // 若有值，默认为 逐账套展示
            if($("#booksetId").val()){
                $("#issueType").val("02");
                $("#issueType").next().addClass('disabled').find('li').off('click');
                $("#issueType").next().find('li').removeClass('laytable-checkbox-selected');
                $("#issueType").next().find('li').each(function(index,n){
                    if($(this).data('value') == '02'){
                        $(this).addClass('laytable-checkbox-selected');
                    }
                });
            }else{
                $("#issueType").next().removeClass('disabled').find('li').off('click').bind('click',function(){
                    if(!$(this).hasClass('laytable-checkbox-selected')){
                        $("#issueType").next().find('li').removeClass('laytable-checkbox-selected');
                        $(this).addClass('laytable-checkbox-selected');
                        $("#issueType").val($(this).data('value')).trigger('change');
                    }
                });
            }
        })
    });

	
});