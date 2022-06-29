(function(){
	
	/**
     * 获取查询参数
     */
    var getSearchParams = function() {
    	var param = $.serializeToObject('#searchForm');
    	var model = $('#model').val();
    	if (model == REDIS_MODEL_PARAMVAL) {
        	if(param.rdsKey){
        		param.rdsKey = param.rdsKey.join(",");
        	}
    	} else {
    		param.rdsKey = param.model;
    	}
        return param;
    };

	/**
     * 定义数据表格
     */
    var table=$("#dataTable").joyintable({ 
    	ajax: {
            url: 'selectForPage',
            type: 'POST',
            data: function() {
                return getSearchParams();
            }
        },
        rowno: true,
        checkbox: false,
        page: true,
        searchOnInit: false,
        cols:[
             {field:'key', title: 'RedisView.key', width:'23%'}
            ,{field:'field', title: 'RedisView.field', width:'23%'}
			,{field:'value', title: 'RedisView.value', width:'43%',sort:false}
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: [{
                text: '&#xe669;',
                clas: 'config',
                title: '刷新'
            },{ text: '&#xe642;',ignore:true, clas: 'edit', title: '更新参数值' }]}
        ],
		done:function(){
			if ( $('#model').val() == REDIS_MODEL_PARAMVAL) {
				$("div.layui-table-main:first").find("i.edit[title='更新参数值']").show();
			}else{
				$("div.layui-table-main:first").find("i.edit[title='更新参数值']").hide();
			}
		}
    });

    /**
     * 绑定刷新事件
     */
    table.bind('click', '.config', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        var isUpParamVal="N";
        if($("#isUpParamVal").prop('checked')){
			isUpParamVal="Y";
		}
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/redismgr/RedisView/refresh/'+isUpParamVal, data, function(result) {
        	if (result.data) {
        		data = result.data;
        	}
        	table.renderRow(index, data);
        });
    });

	/**
	 * 绑定更新参数值事件
	 */
	table.bind('click', '.edit', function(data, index) {
	    if( $('#model').val() == REDIS_MODEL_PARAMVAL) {
	        data.pageModel = PAGE_MODEL_EDIT;
	        data.operateType = 'edit';
	        Joyin.dialog(JoyinUtil.getLabel("RedisViewParam.edit"),
	            basePath + "invest/riskredis/quota/redismgr/RedisViewParam/init",
	            "80%", "75%",data,function() {
	                table.reload();
	            });  
	    }
	});

    var modelClick = function(){
    	var model = $('#model').val();
    	if (model == REDIS_MODEL_PARAMVAL) {
    		$('#div_key').show();
    		$("#RedisView_btn_refresh").hide();
    	} else {
    		$('#div_key').hide();
    		$("#RedisView_btn_refresh").show();
    	}
    	if (model == REDIS_MODEL_PARAM_CONFIG) {
    		$('#isUpParamValDiv').show();
    	} else {
    		$('#isUpParamValDiv').hide();
			$('#isUpParamVal').prop("checked",false);
			layui.form.render('checkbox')
    	}
    }

    //--------------add by 裴家俊 20200615 redis功能参数配置 start-------------------------
	/**
	 * 获取查询参数
	 */
	var getSearchRedisParams = function() {
		var param = $.serializeToObject('#searchParamForm');
		if(param.commonBusinessType){
			param.commonBusinessType = param.commonBusinessType.join(",");
		}
		return param;
	};

	/**
	 * 定义数据表格
	 */
	var paramTable=$("#dataParamTable").joyintable({
		ajax: {
			url: basePath+'invest/riskredis/quota/redismgr/RskRedisParamConfig001/selectForPage',
			type: 'POST',
			data: function() {
				return getSearchRedisParams();
			}
		},
		searchOnInit: false,
		cols:[
			{field:'commonBusinessType', title: 'RskRedisParamConfig001.commonBusinessType', visible: false}
			,{field:'commonBusinessTypeName',sortField:"commonBusinessType", title: 'RskRedisParamConfig001.commonBusinessType', width:'19%'}
			,{field:'paraCode', title: 'RskRedisParamConfig001.paraCode', width:'30%',visible: false}
			,{field:'paraName', title: 'RskRedisParamConfig001.paraName', width:'49%'}
			,{field:'redisModelName', title: 'RskRedisParamConfig001.redisModel',width:'20%', sortField:"redisModel"}
			,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['edit', 'delt']}
		]
	});

	/**
	 * 绑定编辑事件
	 */
	paramTable.bind('click', '.edit', function(data, index) {
		data.pageModel = PAGE_MODEL_EDIT;
		data.operateType = 'edit';
		Joyin.ajaxJson(basePath + 'invest/riskredis/quota/redismgr/RskRedisParamConfig001/check', data, function(result) {
			Joyin.dialog(JoyinUtil.getLabel("RskRedisParamConfig002.edit"),
				basePath + "invest/riskredis/quota/redismgr/RskRedisParamConfig002/init",
				"80%", "75%",data,function() {
					paramTable.reload();
				});
		});
	});

	/**
	 * 绑定删除事件
	 */
	paramTable.bind('click', '.delt', function(data, index) {
		var object = {selectedItem: [data], operateType:"delete"};
		Joyin.ajaxJson(basePath + 'invest/riskredis/quota/redismgr/RskRedisParamConfig001/check', object, function(result) {
			Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
				WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/redismgr/RskRedisParamConfig001/delete', object,
					"RedisView001_btn_delete", "RskRedisParamConfig002",function(){
						paramTable.reload();
					});
			});
		});
	});

	/**
	 * 弹出新建画面
	 * @returns
	 */
	function openNew() {
		Joyin.dialog(JoyinUtil.getLabel("RskRedisParamConfig002"),
			JoyinUtil.getRealPath("invest/riskredis/quota/redismgr/RskRedisParamConfig002/init"),
			"80%", "75%",{pageModel : PAGE_MODEL_NEW},function(){paramTable.reload();}
		);
	}
	//--------------add by 裴家俊 20200615 redis功能参数配置 end-------------------------

	$(function() {
		$('#model').val(REDIS_MODEL_PARAMVAL);
		$('#model').change(function(){
			modelClick();
		});
		$('#model').trigger('change');
		// 选择模块
		// 查询按钮
        $('.searchForm #RedisView_btn_search').click(function() {
            var model = $('#model').val();
            var rdsKey = $('#rdsKey').val();
        	if (model == REDIS_MODEL_PARAMVAL) {
            	if(rdsKey === null || rdsKey === ''){
            		Joyin.error(JoyinUtil.getMessage('BUSS_COM_E_0038',JoyinUtil.getLabel("RedisView.key")));
            		return;
            	}
        	}
            table.load();
        });
        
        $("#RedisView_btn_refresh").click(function(){
        	var data = {key:$('#model').val()};
        	Joyin.ajaxJson(basePath + 'invest/riskredis/quota/redismgr/RedisView/refresh', data, function(result) {
        		table.load();
        });
        })

		//--------------add by 裴家俊 20200615 redis功能参数配置 start-------------------------
		// 查询按钮
		$('.searchParamForm #RedisView_btn_search').click(function() {
			paramTable.load();
		});

		$('#RedisView001_btn_add').click(function() {
			openNew();
		});

		$('#RedisView001_btn_delete').click(function() {
			var selArr = paramTable.getAllCheckedRows();
			if (!selArr || selArr.length == 0) {
				Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
				return;
			}
			var object = {selectedItem: selArr, operateType:"delete"};
			Joyin.ajaxJson(basePath + 'invest/riskredis/quota/redismgr/RskRedisParamConfig001/check', object, function(result) {
				Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
					WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/redismgr/RskRedisParamConfig001/delete', object,
						"RedisView001_btn_delete","RskRedisParamConfig002",function(){
							paramTable.reload();
						});
				});
			});
		});
		//--------------add by 裴家俊 20200615 redis功能参数配置 end-------------------------
	});
}());