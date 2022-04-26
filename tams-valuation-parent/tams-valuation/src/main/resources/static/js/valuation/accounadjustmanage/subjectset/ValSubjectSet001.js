$(function () {
    /**
     * 获取查询参数
     */
    var getPageParams = function() {
        return {
            booksetId: $('#booksetId').val()
            ,fsubjectId: $('#fsubjectId').val()
            ,subjectLevel: $('#subjectLevel').val()
            ,subjectNo: $('#subjectNo').val()
            ,subjectName: $('#subjectName').val()
            ,subjectType: $('#subjectType').val()
            ,balFlag: $('#balFlag').val()
        }
    };
	var table=$("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function() {
                return getPageParams();
            }
        },
		searchOnInit: false,
		cellMinWidth:100 ,
        dblclick:function(table, data) {
            Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "valuation/accounadjustmanage/subjectset/ValSubjectSet002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_REFERENCE,//page_model:查看
                    booksetId:data.booksetId,
                    subjectNo:data.subjectNo});
        },
		cols: [
		    {field:'booksetId', width:120, title: 'ValSubjectSet001.booksetId'}
		    ,{field:'subjectNo', width:100, title: 'ValSubjectSet001.subjectNo'}
		    ,{field:'subjectName', width:180, title: 'ValSubjectSet001.subjectName'}
		    ,{field:'fsubjectId', width:120, title: 'ValSubjectSet001.fsubjectId'}
		    ,{field:'balFlagName', width:100, sortField:"balFlag", title: 'ValSubjectSet001.balFlag'}
		    ,{field:'subjectTypeName', width:120, sortField:"subjectType", title: 'ValSubjectSet001.subjectType'}
		    ,{field:'subjectLevel', width:100, title: 'ValSubjectSet001.subjectLevel'}
		    ,{field:'accQuaFlagName', width:150, sortField:"accQuaFlag", title: 'ValSubjectSet001.accQuaFlag'}
		    ,{field:'accIntFlagName', width:100, sortField:"accIntFlag",title: 'ValSubjectSet001.accIntFlag'}
		    ,{field:'overdrawnFlagName', width:150, sortField:"overdrawnFlag",title: 'ValSubjectSet001.overdrawnFlag'}
		    ,{field:'genDetsubFlagName', width:150,sortField:'genDetsubFlag',title: 'ValSubjectSet001.genDetsubFlag'}
		    ,{field:'baseSubjectNature', width:160, title: 'ValSubjectSet001.baseSubjectNature'}
		    ,{field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: [{text: '&#xe642;', clas: 'edit', title: '编辑'}]}
		]
	  });
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.dialog(JoyinUtil.getLabel("科目信息编辑"),
                basePath + "valuation/accounadjustmanage/subjectset/ValSubjectSet002/init",
                "95%", "80%", {pageModel:PAGE_MODEL_EDIT,
                                booksetId:data.booksetId,
                                subjectNo:data.subjectNo},function(){
                                    //table.reload();
                                });
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        var object = {selectedItem: [data], operateType:"delete"};
        Joyin.ajaxJson(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                Joyin.ajaxJson(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet001/delete', object, function(result) {
                    table.reload();
                });
            });
        });
    });

    /**
     * 画面加载完成
     */
    $(function() {
      //新建
      $('#ValSubjectSet001_btn_add').click(function() {
          Joyin.dialog(JoyinUtil.getLabel("SysCommon.create")+JoyinUtil.getLabel("ValSubjectSet002"),
                  basePath + "valuation/accounadjustmanage/subjectset/ValSubjectSet002/init",
                  "95%", "90%", {pageModel:PAGE_MODEL_NEW});
      });
      // 绑定按钮事件
      $('#ValSubjectSet001_btn_search').click(function() {
          table.load();
          //$('#ValSubjectSet001_btn_delete').show();
      });
      $('#ValSubjectSet001_btn_export').click(function() {
          exportExcel();
      });
      $('#ValSubjectSet001_btn_delete').click(function() {
          if(table!=null && table!=''){
              var selArr = table.getAllCheckedRows();
          }
          if (!selArr || selArr.length == 0) {
              Joyin.error(JoyinUtil.getMessage("没有选中数据"));
              return;
          }
          var object = {selectedItem: selArr, operateType:"delete"};
          Joyin.ajaxJson(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet001/check', object, function(result) {
              Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                  Joyin.ajaxJson(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet001/delete', object, function(result) {
                      table.reload();
                  });
              });
          });
      });
    });
});