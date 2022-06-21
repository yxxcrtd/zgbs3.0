/**
 * 风险授权管理新建
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
    	var rskAuthManage;
    	if(table!=null && table!=''){
    		rskAuthManage = table.getAllRows();
        }
        if (!rskAuthManage || rskAuthManage.length == 0) {
            Joyin.error("请输入表格数据");
            return;
        }
        for(var i =0; i<rskAuthManage.length; i++){
    		if(rskAuthManage[i].authorizeLimitAmt){
    			if(rskAuthManage[i].authorizeLimitAmt.length > 30){
    				Joyin.error("授权额度的长度不能超过30！");
    				return;
    			}
    		}
		}
        if($("#seqNo").val() == null || $("#seqNo").val() == ''){
        	var object = {selectedItem: rskAuthManage, operateType:"new",pageModel:pageModel};
        }
        else{
        	var object = {selectedItem: rskAuthManage, operateType:"edit",pageModel:pageModel};
        	object.seqNo = $("#seqNo").val();
        }
        if($("#workflowId").val() != null && $("#workflowId").val() != ''){
        	object.workflowId = $("#workflowId").val();
        }
        object.rolePos = $("#rolePos").val();
        if($("#rolePos").val() == null || $("#rolePos").val() == ''){
        	Joyin.error("请输入角色岗位！");
			return;
        }
        var userNameTemp = $('#userName').val();
        if(!userNameTemp ||  userNameTemp.length ==0 ){
        	Joyin.error("请输入用户名！");
			return;
        }
        var userName = null;
        if(userNameTemp != null)
        	userName=userNameTemp.join(",");
        object.userName=userName;
        object.wflOperType = $("#wfl_operType").val();
        /*$.each(userNameTemp, function(i1, v1) {  
        	$.each(rskAuthManage, function(i2, v2) { 
        		var rskDto = new Object();
        		rskDto.rolePos = $("#rolePos").val();
        		rskDto.userName = v1;
        		rskDto.pageModel = pageModel;
        		rskDto.assetType = v2.assetType;
        		rskDto.assetSecType = v2.assetSecType;
        		rskDto.tradeMarket = v2.tradeMarket;
        		rskDto.tradeDirection = v2.tradeDirection;
        		rskDto.authorizeType = v2.authorizeType;
        		rskDto.authorizeLimitAmt = v2.authorizeLimitAmt;
        		if($("#workflowId").val() != null && $("#workflowId").val() != ''){
        			rskDto.workflowId = $("#workflowId").val();
                }
        		
        		if($("#seqNo").val() != null && $("#seqNo").val() != ''){
        			rskDto.seqNo = $("#seqNo").val();
        		}
        		rskArr[i] = rskDto;
        		i++;
        	});
    	});*/  
        /*if($("#seqNo").val() == null || $("#seqNo").val() == ''){
        	var object = {selectedItem: rskArr, operateType:"new",pageModel:pageModel};
        }
        else{
        	var object = {selectedItem: rskArr, operateType:"edit",pageModel:pageModel};
        }
        if($("#workflowId").val() != null && $("#workflowId").val() != ''){
        	object.workflowId = $("#workflowId").val();
        }*/
//        var rskArr = new Array();
//        var i = 0;
//        var allData = [];
//        $.each(userNameTemp, function(i1, v1) {  
//        	
//        	$.each(rskAuthManage, function(i2, v2) { 
//        		var data = {
//    				rolePos:$("#rolePos").val(),
//    				userName:v1,
//    				assetType:v2.assetType,
//    				assetSecType:v2.assetSecType?v2.assetSecType:null,
//					tradeMarket:v2.tradeMarket?v2.tradeMarket:null,
//					tradeDirection:v2.tradeDirection?v2.tradeMarket:null
//        		};
//                data = data+rskDto;
//        		if(i!=userNameTemp.length*rskAuthManage.length-1){
//        			allData=allData+"data"+i+":"+data+",";
//        		}
//        		else{
//        			allData=allData+"data"+i+":"+data;
//        		}
//        		
//        		i++;
//        	});
//    	});
//        allData=allData+"}";
//        object.allData = allData;
        var allData = [];
        var rolePos = $("#rolePos").val();
        if(userNameTemp != null && userNameTemp.length >0 
        		&& rskAuthManage != null && rskAuthManage.length>0){
        	$.each(userNameTemp,function(i,name){
            	$.each(rskAuthManage,function(i,dto){
            		var temp = {userName:name};
            		 temp.rolePos= rolePos;
            		 $.extend(temp,dto);
            		 allData.push(temp);
            	})
            });
        }
        object.allData = JSON.stringify({selectedItem:allData});
        object.pageId = "RskAuthorityManage002";
        Joyin.ajaxJson(basePath + 'invest/risk/rskauthoritymanage/RskAuthorityManage002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
            	if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/risk/rskauthoritymanage/RskAuthorityManage002/save', object, "RskAuthorityManage002_btn_save");
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/risk/rskauthoritymanage/RskAuthorityManage002/save', object, "RskAuthorityManage002_btn_submit");
                }
            });
        });
    }
    /**
     * 定义数据表格
     */
    var addbtn = (pageModel == PAGE_MODEL_NEW) || ($("#wfl_operType").val() == 'NEW');
	var options = {

        ajax : {
            url : JoyinUtil.getRealPath('invest/risk/rskauthoritymanage/RskAuthorityManage002/findPage'),
            type : 'POST',
            data : function() {
                return {
                	seqNo : $('#seqNo').val(),
                    workflowId : $('#workflowId').val()
                };
            }
        },
        page: false,
        addbtn:addbtn,
        addToLast:true,
        searchOnInit: true,
        checkbox:false,
        // editCell: function (value, row, field, cell, column){
        //     console.log(value)
        //     var selArr = table.getAllRows();
        //     if(selArr && selArr.length>0){
        //         for(var i=0;i<selArr.length;i++){
        //             var assetType = selArr[i].assetType;
        //             if(assetType == 'F11'){
        //                 // selArr[i].tradeMarket
        //                 // selArr[i].tradeDirection
        //             }
        //             // changeTable(assetType);
        //         }
        //     }
        // },
        beforeEditCell : function(preVal, field,data){
        	if(field == 'authorizeLimitAmt' && data.authorizeType != null &&data.authorizeType != AUTHORIZE_TYPE_02){
        		return false;
        	}
           	return true;
           	},
    	cols:[
             // 资产类型改为授权类型 modify by renping 2019-08-06  end
             {field:'assetType' , title: 'RskAuthorityManage001.assetType',width:200 , edit:true
            	 ,editOption:{type:'select',childrenIds:'assetSecType,tradeDirection,tradeMarket',id:"assetType",formatoption:false,result:RSK_AUTHORIZE_TYPE1}, sortable:false
             }
             // ,{field:'assetSecType' ,title: 'RskAuthorityManage001.assetSecType', width:200 ,edit:true,
             //    render: function (data, type, row, meta) {
             //        return row.assetSecTypeStr;
             //    }
            	//  ,editOption:{type:'select',parentIds:'assetType',id:"assetSecType",formatoption:true,url:JoyinUtil.getRealPath('invest/risk/rskauthoritymanage/RskAuthorityManage002/makeAllAssetSecType')}, sortable:false
             // }
            // 授权类型二级
           ,{field:'assetSecType' , title: 'RskAuthorityManage001.assetSecType',width:200 , edit:true
                ,editOption:{type:'select',parentIds:'assetType',id:"assetSecType",formatoption:false,result:RSK_AUTHORIZE_TYPE2}, sortable:false
            }
             // ,{field:'tradeMarket' ,title: 'RskAuthorityManage001.tradeMarket', width:200 ,edit:true,
             //    render: function (data, type, row, meta) {
             //        return row.tradeMarketStr;
             //    }
            	//  ,editOption:{type:'select',parentIds:'assetType',id:"tradeMarket",formatoption:true,url:JoyinUtil.getRealPath('invest/risk/rskauthoritymanage/RskAuthorityManage002/makeAlltradeMarket')}, sortable:false
             // }
            // 交易场所
            ,{field:'tradeMarket' , title: 'RskAuthorityManage001.tradeMarket',width:200 , edit:true
                ,editOption:{type:'select',parentIds:'assetType',id:"tradeMarket",formatoption:true,result:TRADE_PLACE}, sortable:false
            }
             // ,{field:'tradeDirection',title: 'RskAuthorityManage001.tradeDirection',width:200 , edit:true,
             //    render: function (data, type, row, meta) {
             //        return row.tradeDirectionStr;
             //    }
            	//  ,editOption:{type:'select',parentIds:'assetType',id:"tradeDirection",formatoption:true,url:JoyinUtil.getRealPath('invest/risk/rskauthoritymanage/RskAuthorityManage002/makeAllPs')}, sortable:false
             // }
            // 交易方向
            ,{field:'tradeDirection' , title: 'RskAuthorityManage001.tradeDirection',width:200 , edit:true
                ,editOption:{type:'select',parentIds:'assetType',id:"tradeDirection",formatoption:true,result:PS}, sortable:false
            }
             // 资产类型改为授权类型 modify by renping 2019-08-06  end
             //------------modify by jjpei 20190710------隐藏授权类型，且默认为授权-------------
             ,{field:'authorizeType' ,title: 'RskAuthorityManage001.authorizeType', edit:true
            	 ,editOption:{type:'select',id:"authorizeType",formatoption:true,result: AUTHORIZE_TYPE,initValue:AUTHORIZE_TYPE_02}, sortable:false
             }
             ,{field:'authorizeLimitAmt' , title: 'RskAuthorityManage001.authorizeLimitAmt',width:257 , edit:true, type:"number", digit: 6,sortable:false
             }
             ,{field:'operation',title: 'SysCommon.operation', type:'action', buttons: [addbtn? 'delt':'']}
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
        table.removeRow(data);
    });


    var init = function(){
	var wfl_operType = $("#wfl_operType").val();
   	if(pageModel==PAGE_MODEL_EDIT){
   		if(wfl_operType == null || wfl_operType != 'NEW'){
   			$('#rolePos').attr("disabled",true);
   			$('#userName').attr("disabled",true);
   		}
   	};
   };



    $(function() {
    	init();
        // 按钮事件
        $('#RskAuthorityManage002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#RskAuthorityManage002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        $('body').on('change', '#assetType', function(e, data) {
            table.renderRow(data.rowIndex, {'assetSecType': '','tradeMarket': '','tradeDirection': ''});
        });
    });
}());