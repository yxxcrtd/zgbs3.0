$(function () {
    /**
     * 定义数据表格
     */
	var table=$("#ValAccountsAdjustAccountEntry004_table").joyintable({
	    ajax: {
            url: 'searchList',
            type: 'POST',
            data: {
                booksetId: $("#booksetId").val(),
                sceneNodeId: $("#sceneNodeId").val()
            }
        },
        searchOnInit: true,
        checkbox: false,
        cols: [
            {field:'subjectNo', title: $("#booksetId").val()=='SUBJECT_INTERNAL_001'?'科目性质': '科目号'},
            {field:'subjectName',title: $("#booksetId").val()=='SUBJECT_INTERNAL_001'?'科目性质名称': '科目名称'},
            {field:'cdFlag', title: '借贷'},
            {field:'dictName',sortField:'formEle', title: '金额元素'},
            {field:'dictCode',sortField:'formEle', title: '金额元素代码'},
            {field:'dictSource',sortField:'formEle', title: '元素数据来源'},
            {field:'bokDetailId', title: '账务明细元素'},
            {field:'accountNet', title: '记账网点',visible:false}
        ],
		loadonce:true
	});
	
});