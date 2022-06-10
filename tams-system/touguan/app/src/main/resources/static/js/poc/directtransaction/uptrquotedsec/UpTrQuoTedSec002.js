$(function() {
    /**
     * 获取查询参数
     */
    var getSearchParams = function() {
        return {
        };
    };

    /*
     * 对买入卖出方向进行处理
     */
    var ps = $("#psBuy").val();
	if(ps == 'psBuy'){
		$("#psSell").attr("disabled", true );
	}else if(ps == 'psSell'){
		$("#psSell").attr("checked", "checked" )
		$('#psBuy').removeAttr("checked");
		$("#psBuy").attr("disabled", true );
	}
    
    /**
     * 定义数据表格
     */
    var optionalIns=$("#optionalIns").joyintable({
        /*ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },*/
        /**
         * 定义表头
         */
        searchOnInit: true,
        page:false,
        height:200,
        cols:[
            {field:'institution',  title: '机构'}//机构
        ],
        data:[
              	  {"institution":"工商银行"},
                  {"institution":"建设银行"},
                  {"institution":"上海银行"},
                  {"institution":"广发银行"},
                  {"institution":"中国银行"},
                  {"institution":"农业银行"}
        ]
    });
    var selectedIns=$("#selectedIns").joyintable({
        /*ajax: {
            url: 'findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },*/
        /**
         * 定义表头
         */
        searchOnInit: true,
        page:false,
        checkbox:false,
        cols:[
            {field:'institution',  title: '已选机构'}//报价编号
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt']}//编辑和删除事件
        ],
        data:[
            /*{"selectedIns":"中国银行"},
            {"selectedIns":"农业银行"},
            {"selectedIns":"建设银行"}*/
        ]
    });
    
    sourceData = [
                  {"institution":"工商银行"},
                  {"institution":"建设银行"},
                  {"institution":"上海银行"},
                  {"institution":"广发银行"},
                  {"institution":"中国银行"},
                  {"institution":"农业银行"}
                  ];
    
    sourceData1 = [
		//		{"selectedIns":"中国银行"},
		//		{"selectedIns":"农业银行"},
		//		{"selectedIns":"建设银行"}
                   ];
    /**
     * 绑定编辑事件
     */
    selectedIns.bind('click', '.delt', function(data, index) {
     //   data.pageModel = PAGE_MODEL_EDIT;
        /*Joyin.dialog(JoyinUtil.getLabel("准入池新建"),
            basePath + "risk/admission/admissionassetpool/AdmissionAssetPool003/init",//打开新界面
            "75%", "45%",function(){
                table.reload();//回调函数，表格刷新
            });*/
    	selectedIns.removeRow(data);
    	optionalIns.addRow(data);
    	selectedIns.load();
    });

    
    /**
     * 画面加载完成
     */
    $(function() {
    	$("#UpTrInqSec001_btn_add1").click(function(){
    		var rows = optionalIns.getAllCheckedRows(); 
    		if(rows.length != 0){
    			for(var i=0 ;i<rows.length; i++){
    				if(rows[i].institution === "工商银行"){
        				selectedIns.addRow(sourceData[0]);
        				optionalIns.removeRow(sourceData[0]);
        			}else if(rows[i].institution === "建设银行"){
        				selectedIns.addRow(sourceData[1]);
        				optionalIns.removeRow(sourceData[1]);
        			}else if(rows[i].institution === "上海银行"){
        				selectedIns.addRow(sourceData[2]);
        				optionalIns.removeRow(sourceData[2]);
        			}else if(rows[i].institution === "广发银行"){
        				selectedIns.addRow(sourceData[3]);
        				optionalIns.removeRow(sourceData[3]);
        			}else if(rows[i].institution === "中国银行"){
        				selectedIns.addRow(sourceData[4]);
        				optionalIns.removeRow(sourceData[4]);
        			}else if(rows[i].institution === "农业银行"){
        				selectedIns.addRow(sourceData[5]);
        				optionalIns.removeRow(sourceData[5]);
        			}
    			}
    			
    			/*var allRows = selectedIns.getAllRows();
    			if(allRows.length !=0 ){
    				for(var i=0;i<allRows.length;i++){
    					if(rows[i].institution != ''){
    						selectedIns.clear();
    					}
    				}
    			}*/
    			
    			
    		}
    	});
    	
    	
    	/**
         * 绑定事件
         */
        $(function() {
    	var init= function(){
            $('#finprodId').change();
         // 画星星
            star();
    	}
            /**
             * 资产代码联动资产名称
             */
            $('#finprodId').change(function() {
                var finprodId = $('#finprodId').val();
                var data = $('#finprodId').select2("data")[0].object;
                $('#finprodName').val(data);
        });
      });
    	
    	//绑定提交按钮
    	$(function() {
        	$("#UpTrInqSec001_btn_submit").click(function(){
        		Joyin.error("暂未开放上行接口");
        		return;
        	})
        });
    	
    });
    
    
}());