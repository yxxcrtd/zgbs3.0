/**
 * RskPortAutoRelatePoint001 组合自动关联指标
 */
var QueryFunc = function () {
    //放大镜点击事件
    return {
        serachKey: function () {
            //树的重新加载
            loadTree();
        }
    }
}();
var ruleId = '';
var ruleName = '';
//树的加载
var loadTree = function (callback) {
    $("#mixTree").load(basePath +'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint001/getRuleName',
        {ruleName: $('#rulename').val()}, function (data) {
        	ruleId = '';
            if (typeof callback == 'function') {
                callback();
            }
        })
}

$(function () {
    // var nodeId = '';
    //获取查询参数
    var getSearchParams = function () {
        return {
            ruleId: ruleId,
            indName:$("#indName").val()
        };
    };
    //树加载
    loadTree();
    //鼠标放置显示操作列效果
    $("body").on('mouseover', ".mixtree-node", null, function () {
        $(".operate_div").hide();
        $(this).find(".operate_div").css({display: 'inline-block'});
    });
    //移开鼠标隐藏操作列效果
    $("body").on('mouseout', ".mixtree-node", null, function () {
        $(".operate_div").hide();
    });
    //点击规则名称事件
    $("body").on('click', ".mixtree-node", null, function () {
    	$("div#trimChars").html("");
    	$("div#trimChars").html($(this).attr('trimChars'));
    	//$("div#fileshow").html(result.rval);
        ruleId = $(this).attr('ruleid');
        ruleName = $(this).attr('rulename');
        table.reload();
        //点击选中后显示背景色
        $(".mixtree-node").css({'background-color': ''});
        $(this).css({'background-color': '#D9ECFF'})
    });
  //映射名称编辑事件
    $("body").on('click', ".node_modify", null, function () {
        var thisRuleId = $(this).attr('ruleid');
        var thisRuleName = $(this).attr('rulename');
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/check', {pageModel:PAGE_MODEL_EDIT,ruleId:thisRuleId,ruleName:thisRuleName,operateType: 'edit'}, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("RskPortAutoRelatePoint002.edit"),
                basePath + "invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/init",
                "85%", "80%", {pageModel:PAGE_MODEL_EDIT,id:thisRuleId,name:thisRuleName},
                function() {
                    loadTree();
                    $(".mixtree-node[ruleid='"+ruleId+"']").css({'background-color': '#D9ECFF'})
                     table.clear();
                });
         });
    })

    //映射名称查看事件
    $("body").on('click', ".node_search", null, function () {
        var thisRuleId = $(this).attr('ruleid');
        var thisRuleName = $(this).attr('rulename');
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + "invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/init",
            "85%", "80%", {pageModel:PAGE_MODEL_REFERENCE,id:thisRuleId,name:thisRuleName}
        );
    })

    //映射名称删除事件
    $("body").on('click', ".node_delete", null, function () {
        var thisRuleId = $(this).attr('ruleid');
        var thisRuleName = $(this).attr('rulename');
        //获取该删除节点的参数
        var getData=function(){
            return {ruleId:thisRuleId,ruleName:thisRuleName}
        }
        var object = {selectedItem:[getData()],operateType :"delete",ruleId:thisRuleId,ruleName:thisRuleName};
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
            	WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/delete', object,
                		"RskPortAutoRelatePoint001_btn_rule_delete", "RskPortAutoRelatePoint002",
                    function(result){
                        loadTree();
                        table.clear();
                    });
            });
        });
    })

    //绑定规则包双击事件
    $("body").on('dblclick','.mixtree-node',function(event){
        event.stopPropagation();
        var jqBtn = $(this);
        var jqDiv = jqBtn.closest('div');
        jqDiv.siblings().removeClass('active');
        jqDiv.addClass('active');
        Joyin.dialog("规则包【"+jqDiv.attr('rulename')+"】成分查看",
            JoyinUtil.getRealPath("invest/riskredis/quota/portfolioindicator/RskSupervisionPortfPoint005/init"),
            "90%", "90%",{ptlBagId:jqDiv.attr('ruleid'),ptlBagName:jqDiv.attr('rulename'),isRule:"Y"}

        );
    });

    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'findPage',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        searchOnInit: true,
        checkbox:true,
        cols: [
              {field: 'indId',visible: false}//指标代码
              ,{field:'supType', visible: false}
              ,{field:'warnCond', visible: false}
              ,{field:'foulCond',visible: false}
            //,{field: 'indName', title:'RskPortAutoRelatePoint001.indName'}//指标名称
            ,{field:'indName',title: 'RskPortAutoRelatePoint001.indName',
            	render : function(data, type, row, meta) {
                    return  '<i style="color:#0078F0;font-style: normal;cursor: pointer;" class="indName">'+data+'</a>';
                }
            }
            , {field: 'indTypeStr',sortField:"IND_TYPE", title: 'RskPortAutoRelatePoint001.indType'}//指标类型
            ,{field:'supTypeStr', width:130, title: 'RskPortAutoRelatePoint001.supType',sortField:"SUP_TYPE"}
            ,{field:'warnCond',  title: 'RskPortAutoRelatePoint001.warnCond',render: RskQuotaCommon.transferWarnFoul}
            ,{field:'foulCond', title: 'RskPortAutoRelatePoint001.foulCond',render: RskQuotaCommon.transferWarnFoul}
            ,{field:'approveCond', title: 'RskPortAutoRelatePoint001.approveCond',render: RskQuotaCommon.transferWarnFoul}
            //, {field: 'nextIndexTypeStr', sortField:"nextIndexType",title: 'RskPortAutoRelatePoint001.nextIndexType'}
            ,{field:'set', sortable:false, title: 'RskPortAutoRelatePoint001.supervisionindividuation',
                render : function(data, type, row, meta) {
                    return '<i style="color:#0078F0;font-style: normal;cursor: pointer;" class="set">'+'设置'+'</i>';
                }
            }
            , {field: 'operation', width: 100, title: 'SysCommon.operation', type: 'action',
                buttons: ['delt']
            }]
    });
    /**
     * 绑定指标名称事件
     */
    table.bind('click', '.indName', function(data, index) {
    	data.pageModel = PAGE_MODEL_REFERENCE;
    	Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
                basePath + "invest/riskredis/quota/superviseindicator/RskSupervisionIndicator002/init",
                "70%", "90%",data,function() {
                                    table.reload();
                                });
    })
	
    /**
     * 绑定编辑事件
     */
    table.bind('click', '.edit', function(data, index) {
        data.pageModel = PAGE_MODEL_EDIT;
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint003/check',data, function(result) {
            Joyin.dialog(JoyinUtil.getLabel("RskPortAutoRelatePoint003.edit"),
                    basePath + "invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint003/init",
                    "100%", "95%",data,function() {
                                        table.reload();
                                    });
            })
    });

    /**
     * 绑定设置事件
     */
    table.bind('click', '.set', function(data, index) {
        data.pageModel = PAGE_MODEL_NEW;
        data.processModel = "RULE";
        Joyin.dialog(JoyinUtil.getLabel("RskSupervisionIndividuation001"),
            basePath + "invest/riskredis/quota/supervisionindividuation/RskSupervisionIndividuation001/init",
            "90%", "90%",data,function() {
                table.reload();
            });
    })

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
    	data.ruleId = ruleId;
    	data.ruleName = ruleName;
        var object = {selectedItem: [data], operateType:"delete",pageId:"RskPortAutoRelatePoint003"};
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint003/delete', object,
                        "RskPortAutoRelatePoint001_btn_delete", "RskPortAutoRelatePoint003",function() {
                    table.reload();
                });
            });
        });
    });
  //指标参数查询
    $('#RskPortAutoRelatePoint001_btn_search').click(function() {
    	table.load();
    });
  //新增规则事件
    $('#RskPortAutoRelatePoint001_btn_creat').click(function () {
        Joyin.dialog(JoyinUtil.getLabel("RskPortAutoRelatePoint002"),
            basePath + "invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint002/init",
            "80%", "90%", {pageModel: PAGE_MODEL_NEW}, function () {
                loadTree(function () {
                    $(".mixtree-node:first").trigger('click')
                });
            });
    });
  //绑定指标添加按钮事件
    $('#RskPortAutoRelatePoint001_btn_add').click(function () {
    	if(ruleId==''|| !ruleId){
    		Joyin.error(JoyinUtil.getMessage("RSKRDS_E_0059"));
            return;
    	}
    	Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint003/check',
                {
                    pageModel: PAGE_MODEL_NEW,
                    ruleId: ruleId,
                    pageId:"RskPortAutoRelatePoint003"
                }, function (result) {
                	Joyin.dialog(JoyinUtil.getLabel("RskPortAutoRelatePoint003"),
                            basePath + "invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint003/init",
                            "75%", "75%", {
                                pageModel: PAGE_MODEL_NEW,
                                id: ruleId,
                                ruleName:ruleName
                            },
                            function () {
                                table.reload();
                            });
                });
    });
    $('#RskPortAutoRelatePoint001_btn_delete').click(function() {
        if(table!=null && table!=''){
            var selArr = table.getAllCheckedRows();
        }
        if (!selArr || selArr.length == 0) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0002"));
            return;
        }
        for(var i=0;i<selArr.length;i++){
        	selArr[i].ruleId = ruleId;
        	selArr[i].ruleName = ruleName;
        }
        var object = {selectedItem: selArr, operateType:"delete",pageId:"RskPortAutoRelatePoint003"};
        Joyin.ajaxJson(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint001/check', object, function(result) {
            Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.delete")), function(){
                WorkFlow.doDeleteList(basePath + 'invest/riskredis/quota/rskportautorelatepoint/RskPortAutoRelatePoint003/delete', object,
                        "RskPortAutoRelatePoint001_btn_delete","RskPortAutoRelatePoint003");
            });
        });
    });
    //“映射名称”上下箭头展示事件 '.icon-downarrow',  '.icon-uparrow'
    $("body").on('click','.mixtree-head', function () {
        if($(this).find('.icon-downarrow').length > 0) {
            $(this).find('.icon-downarrow').removeClass('icon-downarrow').addClass('icon-uparrow');
            $(".tree_node_div").toggle();
            return;
        }
        if($(this).find('.icon-uparrow').length > 0) {
            $(this).find('.icon-uparrow').removeClass('icon-uparrow').addClass('icon-downarrow');
            $(".tree_node_div").toggle();
            return;
        }

    })
}());