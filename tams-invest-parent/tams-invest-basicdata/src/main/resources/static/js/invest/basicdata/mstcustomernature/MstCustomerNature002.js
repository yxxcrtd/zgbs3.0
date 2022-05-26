/*
 * 客户性质配置
 */
$(function() {
    /**
     * 获取表单参数
     */
    var getSearchParams = function() {
        return {
            customerType : $('#customerType').val(),
            customerId : $('#customerId').val(),
            customerName : $('#customerName').val()
        };
    };
    /**
     * map
     */
    var map = new Map();
    var table=$("#dataTable").joyintable({
    /*
     * 给表格赋虚拟数据
     */
    ajax : {
        url : JoyinUtil.getRealPath('invest/basicdata/mstcustomernature/MstCustomerNature002/searchMstCustomerNature'),
        data : function() {
            return getSearchParams();
        }
    },
    searchOnInit: formTodoList,
    done: function() {
      if(formTodoList) {
          var customerIdArray = entityData.selectedCustomerId || [];
          _.each(customerIdArray, function(id) {
              table.selectRow({customerId: id});
          });
      }  
    },
        /*
         * 定义表头
         */
    cols:[
         {field:'customerId', title: 'MstCustomerNature002.customerId'}
        ,{field:'customerName', title: 'MstCustomerNature002.customerName'}
        ,{field:'customerType', title: 'MstCustomerNature002.customerType',
            render : function(data, type, row, meta) {
                if (data =='01') {
                    return JoyinUtil.getLabel("MstCustomerNature001.business");
                }
                if (data =='02') {
                    return JoyinUtil.getLabel("MstCustomerNature001.natural");
                }
            }
         }
         ]
    });
    var customerCharaterData = [{
     	  "customerCharaterStr":typeName_01,
     	  "customerCharater":typeValue_01
        },
           {
        	   "customerCharaterStr":typeName_02,
        	   "customerCharater":typeValue_02
           },
           {
        	   "customerCharaterStr":typeName_03,
        	   "customerCharater":typeValue_03
           },
           {
        	   "customerCharaterStr":typeName_04,
        	   "customerCharater":typeValue_04
           },
           {
        	   "customerCharaterStr":typeName_05,
        	   "customerCharater":typeValue_05
           },
           {
               "customerCharaterStr":typeName_06,
               "customerCharater":typeValue_06
           },
           {
               "customerCharaterStr":typeName_07,
               "customerCharater":typeValue_07
           },
           {
               "customerCharaterStr":typeName_08,
               "customerCharater":typeValue_08
           },
           {
               "customerCharaterStr":typeName_09,
               "customerCharater":typeValue_09
        },
        {
            "customerCharaterStr":typeName_10,
            "customerCharater":typeValue_10
        },
        {
            "customerCharaterStr":typeName_12,
            "customerCharater":typeValue_12
        }
        ,
        {
            "customerCharaterStr":typeName_13,
            "customerCharater":typeValue_13
        }
       ];
    var customerCharaterTable=$("#customerCharaterTable").joyintable({
    /*
	 * 给表格赋虚拟数据
	 */
          data:customerCharaterData,
          /**
			 * 给表格赋予双击查看功能
			 */
          searchOnInit:true,
          page:false,
          loadonce:true,
    
        /*
         * 定义表头
         */
    cols:[
         {field:'customerCharaterStr',width:140, title: 'MstCustomerNature002.customerCharater',sortable:false}
         ,{field:'customerCharater', visible:false}
         ],
    done: function() {
        if(formTodoList) {
            var customerCharaterArray = entityData.selectedCustomerCharater || [];
            _.each(customerCharaterArray, function(id) {
                customerCharaterTable.selectRow({customerCharater: id});
            });
  }  
},
    });
    var options = {
    /*
     * 给表格赋虚拟数据
     */
    page:false,
    height:'225px',
        /*
         * 定义表头
         */
    cols:[
          {field:'customerId', title: 'MstCustomerNature002.customerId',sortable:false}
         ,{field:'customerName', title: 'MstCustomerNature002.customerName',sortable:false}
         ,{field:'customerCharaterStr', title: 'MstCustomerNature002.customerCharater',sortable:false}
         ,{field:'customerCharater',visible:false,sortable:false},
         {field:'customerType', title: 'MstCustomerNature002.customerType',
             render : function(data, type, row, meta) {
                 if (data =='01') {
                     return JoyinUtil.getLabel("MstCustomerNature001.business");
                 }
                 if (data =='02') {
                     return JoyinUtil.getLabel("MstCustomerNature001.natural");
                 }
                 
             },sortable:false
         }
         ]
    };
    if(formTodoList) {
        options.data = entityData.selectedItem;
    }
    var matchTable=$("#matchTable").joyintable(options);

    /**
     *  取消匹配
     */
    var deleteAllRow = function(){
        var rows = matchTable.getAllCheckedRows();//获取选中的行
        if( rows != null &&  rows.length > 0 ){//判断是否选中元素
            for(var row=rows.length-1;row>=0;row--){//倒序删除，正序会存在问题
                matchTable.removeRow(rows[row]);
                map.delete('customerId:'+rows[row].customerId+',customerCharater:'+rows[row].customerCharater);
            }
        }else{
            Joyin.alert(JoyinUtil.getLabel("CustomerNatureInfo001.notNull"),2);
        }
    }
    /**
     * 进行匹配
     */
    var match = function(){
        var datas = table.getAllCheckedRows();
        var customerCharaters = customerCharaterTable.getAllCheckedRows();
        if(datas != null && datas.length > 0 ){    //判断是否选择左侧表中的元素
            if(customerCharaters != null && customerCharaters.length > 0){//判断是否选择右侧表中的元素
                matchTable.clear();
		var isbonder = '0';//是否是券商，0为否 1为是
                var z=0;
                var tempList = [];
                for(var i=0; i< customerCharaters.length; i++){  //对右侧表中选择的元素进行循环
                    for(var j = 0; j<datas.length;j++){ //对左侧表中选择的元素进行循环
                        var temp={};
                        for(var key in datas[j]){   //将左侧表中元素复制到中间的json对象中
                            temp[key]=datas[j][key];
                        }
                    	if(customerCharaters[i]["customerCharater"]  ==  typeValue_10){//是否选择了券商
                    		isbonder = '1';
                    	}
                        temp["customerCharater"]=customerCharaters[i]["customerCharater"]; //将右侧表中元素复制到中间的json对象中
                        temp["customerCharaterStr"]=customerCharaters[i]["customerCharaterStr"]; //将右侧表中元素复制到中间的json对象中
//                        if(map.has('customerId:'+temp.customerId+',customerCharater:'+temp.customerCharater)){
//                            continue;
//                        }
//                       //如果是券商，则判断是否列表中已经存在该客户期货公司的匹配数据
//                         if(isbonder == '1'){
//                         	if(map.has('customerId:'+temp.customerId+',customerCharater:'+typeValue_11)){
//                         		Joyin.error(JoyinUtil.getMessage('客户:'+temp.customerName+' 已经选择了期货公司性质，不能同时为券商性质'));
//                         		continue;
//                         	}
//                         }
                        tempList.push(temp);
                    }
                }
                for(var i = 0; i<tempList.length;i++){
                    map.set('customerId:'+tempList[i].customerId+',customerCharater:'+tempList[i].customerCharater,''); 
                }
                matchTable.addRow(tempList);   //将中间的json对象添加到表中
            }else{
                Joyin.alert(JoyinUtil.getLabel("MstCustomerNature002.rightNotNull"),2);
            }
        }else{
            Joyin.alert(JoyinUtil.getLabel("MstCustomerNature002.leftNotNull"
            ),2);
        }
    }
/**
 * 进行保存和提交操作
 * @param operateType
 * @returns
 */
function saveOrSubmit(operateType) {
    var data = matchTable.getAllRows();
    var customerData = customerCharaterTable.getAllCheckedRows();
    var leftData = table.getAllCheckedRows();
    var object = {};
    object.selectedCustomerId = [];
    object.selectedCustomerCharater = [];
    $.each(customerData, function(idx,v) {
        object.selectedCustomerCharater.push(v.customerCharater);
    });
    $.each(leftData, function(idx,v) {
        object.selectedCustomerId.push(v.customerId);
        object.customerId=v.customerId
    });
    object.pageModel = pageModel;
    object.operateType=operateType;
    object.selectedItem=data;
    if(data.length==0){
        Joyin.error(JoyinUtil.getMessage("COMMON_E_0057",JoyinUtil.getLabel("CustomerNatureInfo001.matchInfo")));
        return;
    }
    Joyin.ajaxJson(JoyinUtil.getRealPath('invest/basicdata/mstcustomernature/MstCustomerNature002/check'), object, function(result) {
        Joyin.confirm(result.data, function() {
            if(operateType === 'save') {
                WorkFlow.doSaveList(JoyinUtil.getRealPath('invest/basicdata/mstcustomernature/MstCustomerNature002/save'), object, "MstCustomerNature002_btn_save");
            }
            else {
                WorkFlow.doSubmitList(JoyinUtil.getRealPath('invest/basicdata/mstcustomernature/MstCustomerNature002/save'), object, "MstCustomerNature002_btn_submit");
            }
        });
    });
};
$(function() {
    // 绑定按钮事件
	matchTable.load();
    $('#MstCustomerNature002_btn_search').click(function() {
    	var customerType = $('#customerType').val();
        //mody by  meifaxin 2021-9-26 客户类型字段改为选填 start
        table.load();
        var data = customerCharaterData.map(a => Object.assign({}, a));
        if(customerType == CUSTOMER_TYPE_02){
            var idx = data.indexOf(data.find(function(element){ return element.customerCharater === typeValue_13;}));
            if(idx != -1){
                data.splice(idx, 1);
            }
        }
        customerCharaterTable.options.data = data;
        customerCharaterTable.reload();
    	// if(customerType){
        //     table.load();
        //     var data = customerCharaterData.map(a => Object.assign({}, a));
        //     if(customerType == CUSTOMER_TYPE_02){
        //     	var idx = data.indexOf(data.find(function(element){ return element.customerCharater === typeValue_12;}));
        //     	if(idx != -1){
        //     	 data.splice(idx, 1);
        //        }
        //     }
        //     customerCharaterTable.options.data = data;
        //     customerCharaterTable.reload();
    	// }else{
    	// 	Joyin.error(JoyinUtil.getMessage("COMMON_E_0057",JoyinUtil.getLabel("MstCustomerNature002.customerType")));
        //     return;
    	// }
        //mody by  meifaxin 2021-9-26 客户类型字段改为选填 end
    });
   $('#MstCustomerNature002_btn_match').click(function(){
	   match();
   })
   $('#MstCustomerNature002_btn_unMatch').click(function(){
	   deleteAllRow();
   })
   $("#MstCustomerNature002_btn_save").click(function(){
       saveOrSubmit('save');
   });
   
   $("#MstCustomerNature002_btn_submit").click(function(){
       saveOrSubmit('submit');
   });
  });
});