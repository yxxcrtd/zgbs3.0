/**
 * 规则信息新建
 */
$(function() {
	 var init = function(){
	    	var wfl_operType = $("#wfl_operType").val();
	    	if(pageModel==PAGE_MODEL_EDIT){
	    		if(wfl_operType == null || wfl_operType != 'NEW'){
	    			$('#finprodMarketId').attr("disabled",true);
	        		$('#effDate').attr("disabled",true);
	    		}
	    	}
	    	
	    };
    //var editable = pageModel != PAGE_MODEL_REFERENCE;
    /**
     * 保存或提交
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
    	/**
    	 * 获取列表参数
    	 */
    	var object = $.serializeToObject('#submitForm', true);
    	if(table!=null && table!=''){
            var selArr = table.getAllRows();
        }
        if (!selArr || selArr.length == 0) {
            Joyin.error("表格不能为空");
            return;
        }
        
        object.selectedItem = table.getAllRows();
        for (var i = 0; i < object.selectedItem.length; i++){
    		object.selectedItem[i].paramNum = i+1;
            object.selectedItem[i].ruleId = object.ruleId;
            object.selectedItem[i].ruleName = object.ruleName;
    	}
        object.selectedItem.map(function(v) {
        	if(v.classifyDimensionVal) {
        		var ptlParamArray=v.classifyDimensionVal.split(',')
        		var ptlParamArrayN=[];
        		ptlParamArray.map(function(y){
        			var i = y.indexOf('.');
	        		if(i != -1) {
	        			ptlParamArrayN.push(y.substring(i + 1));
	        		}
        		})
        		v.classifyDimensionVal = ptlParamArrayN.join(',');
        	}
        });
        object.pageId = "RskPortAutoRelatePoint002";
        object.pageModel = pageModel;
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
            	if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/save', object, "RskPortAutoRelatePoint002_btn_save");
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/save', object, "RskPortAutoRelatePoint002_btn_submit");
                }
            });
        });
    }
    /**
     * 定义数据表格
     */
    var addbtn = (pageModel == PAGE_MODEL_NEW) || ($("#wfl_operType").val() == 'NEW');
	var options = {
		page: true,
        ajax : {
            url : JoyinUtil.getRealPath('invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/findPage'),
            type : 'POST',
            data : function() {
                return {
                	pageModel:pageModel,
                	ruleId : $('#ruleId').val(),
                    workflowId : $('#workflowId').val()
                };
            }
        },
        addbtn:true,
        addToLast:true,
        searchOnInit: true,
        checkbox:false,
        height:250,
        editCell: function (value, row, field, cell, column) {
            if (column.field == 'classifyDimension') {
                var sel = table.getAllRows();
                for(var i=0;i<sel.length;i++){
                	if(i!=row.LAY_TABLE_INDEX && (value==sel[i].classifyDimension)){
                		JoyinUtil.showMessage(JoyinUtil.getMessage('RSKRDS_E_0063'), 'error', function() {
                			table.deleteRow(row.LAY_TABLE_INDEX);
                			var r = table.getRow(0);
                			r.linkSign = '';
                			table.renderRow(0, r);
                  		});
              			return;
                	}
                	
                }
            } 
        },
        beforeEditCell : function(preVal, field,data){
      	  if(field == 'linkSign' && data.LAY_TABLE_INDEX == 0){
      		  return false;
      	  }
      	  
       	return true;
       	},
    	cols:[
             {field:'ruleId',visible: false}
             ,{field:'linkSign',title: 'RskPortAutoRelatePoint002.linkSign', edit:true
                ,editOption:{type:'select',id:"linkSign",formatoption:true,result:SUP_LINK_TYPE}, sortable:false
            }//连接符
            ,{field:'classifyDimension' ,title: 'RskPortAutoRelatePoint002.classifyDimension', edit:true
            	 ,editOption:{type:'select',childrenIds:"classifyDimensionVal",id:"classifyDimension",formatoption:true,result:CLASSIFY_DIMENSION}, sortable:false
             }//分类维度
             ,{field:'classifyDimensionVal' ,title: 'RskPortAutoRelatePoint002.classifyDimensionVal', edit:true
            	 ,editOption:{type:'select',parentIds:"classifyDimension",id:"classifyDimensionVal",multiple: true,formatoption:true,result:CLASS_DIMENSION_SOURCE}, sortable:false
             }//分类维度值
             ,{field:'operation',title: 'SysCommon.operation', type:'action', buttons: ['delt']}
         ],
        // editCell:function (value, data, field, cell, column) {
        //     if(field == 'assetType'){
        //         data.assetSecType = "";
        //         data.tradeMarket = "";
        //         data.tradeDirection = "";
        //         // $('.laytable-cell-1-assetSecType').hide();
        //         // $('.laytable-cell-1-tradeMarket').hide();
        //         // $('.laytable-cell-1-tradeDirection').hide();
        //     }
        // }
         
    };
	
	/*
     * 绑定表格事件
     */
	var table = $("#dataTable").joyintable(options);
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
    	if(index==0&&table.getAllRows().length>1){
        	rowData = {linkSign:''};
        	table.renderRow(index+1, rowData);
        }
        table.removeRow(data);
    });
    var init = function(){
	var wfl_operType = $("#wfl_operType").val();
   	if(pageModel==PAGE_MODEL_EDIT){
   		if(wfl_operType == null || wfl_operType != 'NEW'){
   			$('#rolePos').attr("disabled",true);
   			$('#userName').attr("disabled",true);
   		}
   	}
   };

    $(function() {
    	init();
        // 按钮事件
        $('#RskPortAutoRelatePoint002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#RskPortAutoRelatePoint002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        $('body').on('change', '#assetType', function(e, data) {
            table.renderRow(data.rowIndex, {'assetSecType': '','tradeMarket': '','tradeDirection': ''});
        });
    });
}());