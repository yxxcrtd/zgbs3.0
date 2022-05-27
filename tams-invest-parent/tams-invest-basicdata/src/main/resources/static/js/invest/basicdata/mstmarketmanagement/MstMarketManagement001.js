/**
 * 证券账户管理
 */
$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        	oType:$('#oType').val(),
        	oCode:$('#finprodName').val(),
        	baseDataType:$('#ratingHistory').val(),
        	gradeSourceType:$('#gradeSourceType').val(),
        	shortLongTerm:$('#shortLongTerm').val(),
     	   	gradeResult:$('#gradeResult').val(),
     	   	gradeOrgId:$('#gradeOrgId').val(),
     	   	gradeDateStart: $.trim($('#gradeDate').startDate()),
     	   	gradeDateEnd: $.trim($('#gradeDate').endDate()),
        };
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
        cols:[
             {field:'oTypeName', sortable:false,  title: 'MstMarketManagement001.oGreadeType'}//对象类型
            ,{field:'oCode', sortable:false,  title: 'MstMarketManagement001.secId'}//对象代码
            ,{field:'oCodeName',  title: 'MstMarketManagement001.finprodName'}//对象名称
            ,{field:'gradeDate',sortable:false,title: 'MstMarketManagement001.gradeDate',type:'date'}//当前评级日期
            ,{field:'gradeResultDis', sortable:false, title: 'MstMarketManagement001.gradeResult'}//当前评级结果
            ,{field:'previousgradeDate',sortable:false,title: 'MstMarketManagement001.previousgradeDate',type:'date'}//前次评级日期
            ,{field:'previousgradeResult', sortable:false, title: 'MstMarketManagement001.previousgradeResultDis'}//前次评级结果
            ,{field:'shortLongTermName', sortable:false, title: 'MstMarketManagement001.shortLongTerm'}//评级类型
            ,{field:'noticeDate',sortable:false,   title: 'MstMarketManagement001.announcementDate',type:'date'}//公告日期
            ,{field:'gradeExpectationStr',sortable:false, title: 'MstMarketManagement001.gradeExpectation'}//评级展望
            ,{field:'directionChangeStr', sortable:false,title: 'MstMarketManagement001.directionChange'}//变动方向
            ,{field:'gradeOrgStr', sortable:false,title: 'MstMarketManagement001.gradeOrgId'}//当前评级机构
            ,{field:'gradeSourceTypeStr', sortable:false,title: '内评/外评'}//内评/外评
            ,{field:'previousgradeOrgName', visible:false}//前次评级机构(不显示)
            // 移除右侧修改及删除按钮  modified by qufangxu at 2019-10-30
            ,{field:'operation',  title: 'SysCommon.operation',sortable:false, type:'action', buttons: ['edit']}
        ],
        searchOnInit :REM_TYPE && REM_TYPE == 'R7S1',
        checkbox : false
    });
    
    var exportExcel = function() {   
        var paramObj = getSearchParams();
        Joyin.exportExcel('export', paramObj);
    };
   
    var finprodIdChange = function () {
    	if($('#internaLevaluation').val()=='02'){
		       $('#gradeOrgId').prop('disabled', true);
    	}
    	else
        $('#gradeOrgId').removeAttr("disabled");
    }
    
    var reset = function(conditionArea,table){
        $(conditionArea).find("input").val("");
        $(conditionArea).find("select").val("").trigger("change");// select清空
    }

    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.dialog(JoyinUtil.getLabel("MstMarketManagement002"), basePath
            + "invest/basicdata/mstmarketmanagement/MstMarketManagement002/init",
            "80%", "80%", data,function(){table.reload();});
    });
    /**
     * 画面加载完成
     */
    $(function() {
    	
    	$('#internaLevaluation').change(function() {
    		finprodIdChange();
    	});
    	$('#gradeSourceType').change(function() {
    		if($('#gradeSourceType').val() == '02'){
    			$('#gradeOrgId').attr("disabled","disabled");
    		}
    		else{
    			$('#gradeOrgId').removeAttr("disabled");
    		}
    	});
        // 绑定按钮事件
        $('#MstMarketManagement001_btn_search').click(function() {
              table.load();
        });
        /**
         * 数据导出
         */
        $('#MstMarketManagement001_btn_export').click(function() {
            exportExcel();
        });
      
        /** 事件绑定: 置空按钮 */
        $("#MstMarketManagement001_btn_reset").click(function(event){
            reset( '#selectArea',table);
        });
        /** 事件绑定: 置空按钮 */
        $("#MstMarketManagement001_btn_add").click(function(){
            Joyin.dialog(JoyinUtil.getLabel("MstMarketManagement002"), basePath
                + "invest/basicdata/mstmarketmanagement/MstMarketManagement002/init",
                "80%", "80%", {pageModel : PAGE_MODEL_NEW},function(){table.reload();});
        });
    });
}());