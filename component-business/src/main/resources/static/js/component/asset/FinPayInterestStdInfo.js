/**
 * 付息信息组件JS
 * @auth zhangnian
 */
(function(){
    /**
     * 上一次参数
     */
    var previewParams = {};
    var initFlag = true;
    /**
     * 是否加载table ajax data;
     */
    var loadAjaxData = true;
    /**
     * 编辑付息计划列表后，是否重新计算利息(确认按钮)
     */
    var isAgainCalInt = false;
    /**
     * 是否计划区间利息计算
     */
    var isSubAmtCal = false;
    /**
     * 是否还本计划生成计划回调付息计划
     */
    var iscallbackPayByRepay = false;
    /**
     * 是否修改 付息信息
     * @type {boolean}
     */
    var isEditPayByRepay = false;
    /**
     * 是否 点击确认
     * @type {boolean}
     */
    var isClickSure = false;
    var generationPlanTable = [];
    var getElementById = function(id,type) {
        if(type=="div"){
            return $('#' + entityName_FinPayInterestStdInfo + '_' + id+'_'+'div');
        }else{
            return $('#' + entityName_FinPayInterestStdInfo + '_' + id);
        }
    };
    //获取还本信息元素id
    var getBussFinRepayElmentById = function(id){
        return $('#' + entityName_BussFinRepayment + '_' + id);
    }
    //获取计息要素画面元素id
    var getFinInterestFactorElementById = function(id){
        return $('#' + entityName_FinInterestFactor + '_' + id);
    }
    //画面根据资产类型show or hide;
    var init = function(){
        showAll();
        //除项目类资产外，隐藏计息营业日准则
        if(INT_ASSET_TYPE!=FINPROD_TYPE_TG_F18){
            getElementById('endTdayRule','div').hide();
        }
        //债券、存款类资产hide自定义说明
        if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F07||INT_ASSET_TYPE==FINPROD_TYPE_TG_F01){
            getElementById('remark','div').hide();
        }
    }
    //展示所有元素
    var showAll = function(){
        getElementById('div').find('div').show();
        getElementById('payMonth_div').hide();
        getElementById('dayValue_div').hide();
    }
    /**
     * 定义数据表格
     */
    var cols = [];
    //债券
    if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F01) {
        cols = [ {
            field : 'vdate',
            title : 'FinPayInterestStdInfo.vdate',
            sortable : false,
            width : 200,
            edit : true,
            type : 'date'
        }, {
            field : 'mdate',
            title : 'FinPayInterestStdInfo.mdate',
            sortable : false,
            width : 200,
            edit : true,
            type : 'date'
        }, {
            field : 'payDate',
            title : 'FinPayInterestStdInfo.payDateUnadjust',
            sortable : false,
            width : 200,
            edit : false,
            type : 'date'
        }, {
            field : 'rangeYield',
            title : 'FinPayInterestStdInfo.intervalRate',
            sortable : false,
            width : 200,
            edit : false,
            type : 'number',
            digit:2
        }, {
            field : 'intPrinAmt',
            title : 'FinPayInterestStdInfo.prinAmt',
            sortable : false,
            width : 150,
            edit : false,
            type : 'number',
            digit:4
        }, {
            field : 'intAmt',
            title : 'FinPayInterestStdInfo.intAmt',
            sortable : false,
            width : 150,
            edit : true,
            type : 'number',
            digit:4
        }, {
            field : 'vdateY',
            type : 'date',
            visible : false
        }, {
            field : 'vdateTerm',
            type : 'date',
            visible : false
        }, {
            field : 'mdateTerm',
            type : 'date',
            visible : false
        }, {
            field : 'termdays',
            type : 'number',
            visible : false
        }, {
            field : 'frequency',
            type : 'number',
            visible : false
        }, 
        {field:'operation', title: 'SysCommon.operation', type:'action',
            render:function (data, type, row, meta) {
                if(row.vdate!=null  && row.mdate!=null && ( (new Date(row.vdate).DateDiff('d',row.maxTradeDate)>0&&
                        new Date(row.mdate).DateDiff('d',row.maxTradeDate)<=0)
                        ||new Date(row.mdate).DateDiff('d',row.maxTradeDate)>=0 )){
                    return '<i class="btn iconfont config" title="查看">&#xe6ac;</i>';
                }else{
                    return '<i class="btn iconfont config" title="查看">&#xe6ac;</i><i class="btn iconfont icon-delete delt" title="删除"></i>';
                }
            }
        }
        /*{
            field : 'operation',
            title : 'SysCommon.operation',
            type : 'action',
            buttons : [ {
                text : '&#xe6ac;',
                clas : 'iconfont config',
                title : '查看'
            }, 'delt' ]
        }*/ // 编辑和删除事件
        ]
        // 存款类
    } else if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F07) {
        cols = [ {
            field : 'vdate',
            width : 200,
            title : 'FinPayInterestStdInfo.vdate',
            width : 250,
            edit : true,
            type : 'date'
        }, {
            field : 'mdate',
            title : 'FinPayInterestStdInfo.mdate',
            width : 250,
            edit : true,
            type : 'date'
        }, {
            field : 'payDate',
            title : 'FinPayInterestStdInfo.payDate',
            width : 250,
            edit : false,
            type : 'date'
        }, {
            field : 'intAmt',
            title : 'FinPayInterestStdInfo.intAmt',
            width : 250,
            sortable : false,
            edit : false,
            type : 'number'
        }, {
            field : 'vdateY',
            type : 'date',
            visible : false
        }, {
            field : 'vdateTerm',
            type : 'date',
            visible : false
        }, {
            field : 'mdateTerm',
            type : 'date',
            visible : false
        }, {
            field : 'termdays',
            type : 'number',
            visible : false
        }, {
            field : 'frequency',
            type : 'number',
            visible : false
        },
        {field:'operation', title: 'SysCommon.operation', type:'action',
            render:function (data, type, row, meta) {
                if( row.vdate!=null && row.mdate!=null && ( (new Date(row.vdate).DateDiff('d',row.maxTradeDate)>0&&
                        new Date(row.mdate).DateDiff('d',row.maxTradeDate)<=0)
                        ||new Date(row.mdate).DateDiff('d',row.maxTradeDate)>=0)){
                    return '<i class="btn iconfont config" title="查看">&#xe6ac;</i>';
                }else{
                    return '<i class="btn iconfont config" title="查看">&#xe6ac;</i><i class="btn iconfont icon-delete delt" title="删除"></i>';
                }
            }
        }
        /*{
            field : 'operation',
            title : 'SysCommon.operation',
            type : 'action',
            buttons : [ {
                text : '&#xe6ac;',
                clas : 'iconfont config',
                title : '查看'
            }, 'delt' ]
        }*/ // 编辑和删除事件
        ]
        // 计划类
    } else if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F17) {
        cols = [ {
            field : 'vdate',
            title : 'FinPayInterestStdInfo.vdate',
            sortable : true,
            width : 200,
            edit : true,
            type : 'date'
        }, {
            field : 'mdate',
            title : 'FinPayInterestStdInfo.mdate',
            sortable : true,
            width : 200,
            edit : true,
            type : 'date'
        }, {
            field : 'payDate',
            title : 'FinPayInterestStdInfo.payDate',
            sortable : true,
            width : 200,
            edit : false,
            type : 'date'
        }, {
            field : 'rangeYield',
            title : 'FinPayInterestStdInfo.intervalRate2',
            sortable : false,
            width : 150,
            edit : false,
            type : 'number',
            digit:2
        }, {
            field : 'intPrinAmt',
            title : 'FinPayInterestStdInfo.prinAmt',
            sortable : false,
            width : 150,
            edit : false,
            type : 'number'
        }, {
            field : 'intAmt',
            title : 'FinPayInterestStdInfo.intAmt',
            sortable : false,
            width : 200,
            edit : true,
            type : 'number'
        }, {
            field : 'vdateY',
            type : 'date',
            visible : false
        }, {
            field : 'vdateTerm',
            type : 'date',
            visible : false
        }, {
            field : 'mdateTerm',
            type : 'date',
            visible : false
        }, {
            field : 'termdays',
            type : 'number',
            visible : false
        }, {
            field : 'frequency',
            type : 'number',
            visible : false
        },  {
            field : 'repayWithoutInt',
            type : 'number',
            visible : false
        },
        {field:'operation', title: 'SysCommon.operation', type:'action',
            render:function (data, type, row, meta) {
                if(row.vdate!=null && row.mdate!=null && ((new Date(row.vdate).DateDiff('d',row.maxTradeDate)>0&&
                        new Date(row.mdate).DateDiff('d',row.maxTradeDate)<=0)
                        ||new Date(row.mdate).DateDiff('d',row.maxTradeDate)>=0 )){
                    return '<i class="btn iconfont config" title="查看">&#xe6ac;</i>';
                }else{
                    return '<i class="btn iconfont config" title="查看">&#xe6ac;</i><i class="btn iconfont icon-delete delt" title="删除"></i>';
                }
            }
        }
        /*{
            field : 'operation',
            title : 'SysCommon.operation',
            type : 'action',
            buttons : [ {
                text : '&#xe6ac;',
                clas : 'iconfont config',
                title : '查看'
            }, 'delt' ]
        } *///编辑和删除事件
        ]
        //项目类
    } else if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F18) {
        //项目类资产付息计划维护
        if(INT_ASSET_TYPE_PAYINTEREST==COMMON_BUSINESS_TYPE_FINPROD10){
            cols = [
                    {field: 'vdate', width: 200, title: 'FinPayInterestStdInfo.vdate', sortable: false, edit: true, type: 'date'}
                    , {field: 'mdate', width: 200, title: 'FinPayInterestStdInfo.mdate', sortable: false, edit: true, type: 'date'}
                    , {field: 'payDate', width: 200, title: 'FinPayInterestStdInfo.payDate', sortable: false, edit: true, type: 'date'}
                    , {field: 'intervalRate', visible: false}
                    , {field: 'prinAmt', visible: false}
                    , {field: 'intAmt', visible: false}
                    , {field: 'vdateY', type: 'date', visible: false}
                    , {field: 'vdateTerm', type: 'date', visible: false}
                    , {field: 'mdateTerm', type: 'date', visible: false}
                    , {field: 'termdays', type: 'number', visible: false}
                    , {field: 'frequency', type: 'number', visible: false}
                    , {field: 'isPayed', width: 200, title: '状态', sortable: false, render:function (data, type, row, meta) {
                            if (data == 'Y')
                                return "已付息";
                            else
                                return '未付息';
                        }
                    }
                    , {field: 'remark', width: 150, title: '备注', sortable: false, edit: true}
                    , {field: 'cashId', visible: false}
                , {field: 'repayWithoutInt', type: 'number', visible: false}
                    , {field: 'operation', width: 150, title: 'SysCommon.operation', type: 'action', buttons: ['delt'],
                        render: function (data, type, row, meta) {
                            if (row.isPayed == 'Y')
                                return "";
                            else
                                return '<i class="btn layui-icon delt" title="删除">&#xe640;</i>';
                        }}//删除事件
                ] 
        }else{
            cols = [ {
                field : 'vdate',
                title : 'FinPayInterestStdInfo.vdate',
                sortable : false,
                width : 200,
                edit : true,
                type : 'date'
            }, {
                field : 'mdate',
                title : 'FinPayInterestStdInfo.mdate',
                sortable : false,
                width : 200,
                edit : true,
                type : 'date'
            }, {
                field : 'payDate',
                title : 'FinPayInterestStdInfo.payDate',
                sortable : false,
                width : 200,
                edit : false,
                type : 'date'
            }, {
                field : 'rangeYield',
                title : 'FinPayInterestStdInfo.intervalRate2',
                sortable : false,
                width : 150,
                edit : false,
                type : 'number',
                digit:4
            }, {
                field : 'intPrinAmt',
                title : 'FinPayInterestStdInfo.prinAmt',
                sortable : false,
                width : 150,
                edit : false,
                type : 'number',
                digit:4
            }, {
                field : 'intAmt',
                title : 'FinPayInterestStdInfo.intAmt',
                sortable : false,
                width : 150,
                edit : true,
                type : 'number',
                digit:4
            }, {
                field : 'vdateY',
                type : 'date',
                visible : false
            }, {
                field : 'vdateTerm',
                type : 'date',
                visible : false
            }, {
                field : 'mdateTerm',
                type : 'date',
                visible : false
            }, {
                field : 'termdays',
                type : 'number',
                visible : false
            }, {
                field : 'frequency',
                type : 'number',
                visible : false
            }, 
            {field:'operation', title: 'SysCommon.operation', type:'action',
                render:function (data, type, row, meta) {
                    if( row.vdate!=null  && row.mdate!=null && ( (new Date(row.vdate).DateDiff('d',row.maxTradeDate)>0&&
                            new Date(row.mdate).DateDiff('d',row.maxTradeDate)<=0)
                            ||new Date(row.mdate).DateDiff('d',row.maxTradeDate)>=0)){
                        return '<i class="btn iconfont config" title="查看">&#xe6ac;</i>';
                    }else{
                        return '<i class="btn iconfont config" title="查看">&#xe6ac;</i><i class="btn iconfont icon-delete delt" title="删除"></i>';
                    }
                }
            }
           /* {
                field : 'operation',
                title : 'SysCommon.operation',
                type : 'action',
                buttons : [ {
                    text : '&#xe6ac;',
                    clas : 'iconfont config',
                    title : '查看'
                }, 'delt' ]
            }*/ //编辑和删除事件
            ]
        }
        
    }
    var allPlanRows;
    var genClick = true;
    var options = {
        ajax: {
            url: JoyinUtil.getRealPath('/component/asset/FinPayInterestStdInfo/generationPlan'),
            type: 'POST',
            dataType: 'json',
            data: function () {
                // 保存本次参数，用于在主画面保存时进行验证
                if (loadAjaxData) {
                    previewParams = getPreviewParams();
                    return previewParams;
                }
                return getPreviewParams();

            }
        },
        cols: cols,
        searchOnInit: true,
        loadonce: true,
        checkbox: false,
        height: 290,
        page: false,
        addbtn: true,
        contextmenu : false,
        addclick: function(row) {
            var _row = generationPlanTable.getRow(0);
            row.maxTradeDate = _row ? _row.maxTradeDate : '';
            row.updateFlg = '01';
            isEditPayByRepay = true;
            isClickSure = true;
        },
        beforeEditCell:function(preVal, field, rowData){
            if(rowData.updateFlg=='02'){
                return false;
            }
            //最大交易日之前的日期不可编辑  交易之后的日期可以编辑
            if(field=='vdate'){
                if(rowData.vdate!=null && new Date(rowData.vdate).DateDiff('d',rowData.maxTradeDate)>=0){
                    return false;
                }
            }
            if(field=='mdate'){
                if( rowData.mdate!=null && new Date(rowData.mdate).DateDiff('d',rowData.maxTradeDate)>=0){
                    return false;
                }
            }
            if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F01) {
                options.beforeEditCell = function(preVal, field, rowData){
                    //息票品种利随本清只能编辑应计利息
                    if(getFinInterestFactorElementById('couponSpecies').val()==COUPON_SPECIES_04){
                        if(field=='intAmt'){
                            return true
                        }else{
                            return false;
                        }
                    }
                    return true;
                }
            }
            if (INT_ASSET_TYPE_PAYINTEREST == COMMON_BUSINESS_TYPE_FINPROD10) {
                if(rowData.isPayed == 'Y'){
                    return false;
                }
                if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F01){
                  //息票品种利随本清只能编辑应计利息
                    if(getFinInterestFactorElementById('couponSpecies').val()==COUPON_SPECIES_04){
                        if(field=='intAmt'){
                            return true
                        }else{
                            return false;
                        }
                    }
                }
            }
           return true;
        },
        editCell: function(value, data, field, cell, column){
        	if(value&&column.editOption.type=='date') {
        	    data[field] = value;//lijun modify 修改入库带时分数据
                isEditPayByRepay = true;
                isClickSure = true;
        	}
            if(new Date(value).DateDiff('d',data.maxTradeDate)>0){
                var rowData = {};
                if(field=='vdate'){
                    Joyin.error("计息开始日不可小于交易日", function() {
                        rowData.vdate = data.oldVdate;
                        generationPlanTable.renderRow(data.LAY_TABLE_INDEX, rowData);
                    });
                }
                if(field=='mdate'){
                    Joyin.error("计息结束日不可小于交易日",function(){
                        rowData.mdate = data.oldMdate;
                        generationPlanTable.renderRow(data.LAY_TABLE_INDEX, rowData);
                    });
                }
            }
            //计息截止日和计息开始日联动(暂时取消) add by wangwei5 20200401
            if(field == "mdate"){
                var oldData = generationPlanTable.getRow(data.LAY_TABLE_INDEX+1);
                oldData.vdate = value;
                generationPlanTable.renderRow(data.LAY_TABLE_INDEX+1,oldData);
                if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F07){
                      generationPlan(true);
                }
                isEditPayByRepay = true;
                isClickSure = true;
            }
            if(field == "vdate"){
                if(data.LAY_TABLE_INDEX-1<0){
                    return;
                }else{
                    var oldData = generationPlanTable.getRow(data.LAY_TABLE_INDEX-1);
                    oldData.mdate = value;
                    generationPlanTable.renderRow(data.LAY_TABLE_INDEX-1,oldData);
                    if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F07){
                         generationPlan(true);
                    }
                    isEditPayByRepay = true;
                    isClickSure = true;
                }
            }
        },
        done:function (tables, allData) {
            var obj = $('#generationPlanTable_div').find("td[data-field='operation']");
            $.each(obj,function(v){
                $(this).find('i.layui-icon.config').show();
            });
            if (genClick) {
                allPlanRows = [];
                for (var key in tables) {
                    allPlanRows.push(deepCopy(tables[key]))
                }
                genClick = false;
            }
        }
        
    };
    if (INT_ASSET_TYPE_PAYINTEREST == COMMON_BUSINESS_TYPE_FINPROD10) {
        /*options.done = function (tables, allData) {
            $.each(tables, function (index, obj) {
                if(tables[index].isPayed == 'Y'){
                    generationPlanTable.$element.next().find(".layui-table-body table.layui-table tr[data-index='"+index+"']").css({'background-color':'#dbdbdb','color':'white'})
                }

            });
        }*/
        /*options.beforeEditCell = function(preVal, field, rowData){
            if(rowData.isPayed == 'Y'){
                return false;
            }
            if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F01){
              //息票品种利随本清只能编辑应计利息
                if(getFinInterestFactorElementById('couponSpecies').val()==COUPON_SPECIES_04){
                    if(field=='intAmt'){
                        return true
                    }else{
                        return false;
                    }
                }
            }
            return true;
        }*/
    }
    /*if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F01) {
        
        options.beforeEditCell = function(preVal, field, rowData){
            //息票品种利随本清只能编辑应计利息
            if(getFinInterestFactorElementById('couponSpecies').val()==COUPON_SPECIES_04){
                if(field=='intAmt'){
                    return true
                }else{
                    return false;
                }
            }
            return true;
        }
    }*/
    generationPlanTable = $("#generationPlanTable").joyintable(options);

    /**
     * 明细查看
     */
    generationPlanTable.bind('click', '.config', function(data, index) {
        if(data.vdate==''|| data.vdate==null ||typeof data.vdate == 'undefined'||
                data.mdate==''|| data.mdate==null ||typeof data.mdate == 'undefined'){
            Joyin.error('无法查看！');
            return;
        }
        isAgainCalInt = true;
        isSubAmtCal = true;
        var param = getPreviewParams();
        //初始本金取前一条计息本金
        if(index!=0){
            data.intPrinAmt = generationPlanTable.getRow(index-1).intPrinAmt; 
        }
        param.invScheduleList = [data];
        //画面所有付息计划
//        param.finCashPlanList = getAllRows();
        param.finCashPlanList = []
        Joyin.dialog(JoyinUtil.getLabel("FinPayInterestStdInfo.plan.title"),
            basePath + "component/asset/FinPayInterestStdInfo/cashPlan/detaile",//打开新界面
            "95%", "70%", {finPayInterestStdInfo:JSON.stringify(param)
            },function(){
                isSubAmtCal = false;
                //generationPlanTable.reload();
            });
    });
    /**
     * 绑定删除事件
     */
    generationPlanTable.bind('click', '.delt', function(data, index) {
        if(index==0){
            Joyin.error('付息计划第一条数据不允许删除!');
            return;
        }
        if(index!=0){

            var newdata=generationPlanTable.getRow(index-1);
            // 取上一条数据，如果上一条数据时 上一条数据 允许修改的 才可修改mdate 否则 修改 当前数据的下一条数据的vdate
            if(newdata!=null && newdata.updateFlg != '02'){
                newdata.mdate=data.mdate;
                generationPlanTable.renderRow(index-1,newdata);
            }else{
                var nextdata=generationPlanTable.getRow(index+1);
                if(nextdata!=null){
                    nextdata.vdate=data.vdate;
                    generationPlanTable.renderRow(index+1,nextdata);
                }
            }
        }
        generationPlanTable.removeRow(data);
        generationPlan(true);
        isEditPayByRepay = true;
        isClickSure = true;
    });

    /**
     * 刷新table
     */
    var initGenerationPlan = function(flag,callback){
        generationPlanTable.table ? generationPlanTable.reload() : generationPlanTable.load();
        if(callback&&$.isFunction(callback)){
            callback(flag);
        }
        disableFinPayInterest();
    }
    
    /**
     *付息计划信息不可编辑
     */
    var disableFinPayInterest = function(){
      //息票品种
        if(getFinInterestFactorElementById("couponSpecies").val() == COUPON_SPECIES_04||getFinInterestFactorElementById("couponSpecies").val()==""){
            //隐藏加号
            getElementById('div').find('.layui-table-page').hide();
            if (INT_ASSET_TYPE == FINPROD_TYPE_TG_F01) {
            	getElementById('div').find('.btn-table-add').hide();
            }
            
            //置灰删除按钮
            getElementById('div').find('table').find('.delt').each(function(){
                $(this).prop('disabled',true);
            });
        }else{
          //隐藏加号
            getElementById('div').find('.layui-table-page').show();
            //置灰删除按钮
            getElementById('div').find('table').find('.delt').each(function(){
                $(this).prop('disabled',false);
            });
        }
    }
    /**
     * 获取行
     */
    var getAllRows = function(){
        if(generationPlanTable!=''){
            return generationPlanTable.getAllRows(true);
        }
    }
    
    /**
     * 清空表格
     */
    var clearAll = function(){
    	generationPlanTable.clear();
    }

    /**
     * 获取参数
     * @returns
     */
    function getPreviewParams() {
        //付息信息
        var param = {}
            ,endTdayRule  = getElementById('endTdayRule').val()
            , payTdayRule = getElementById('payTdayRule').val()
            , payCycle = getElementById('payCycle').val()
            , calcRule = getElementById('calcRule').val()
            ,firstPayDate = getElementById('firstPayDate').val()
            ,remark = getElementById('remark').val();
        param.finprodId = $("#finprodId").val();
        param.workflowId = $("#workflowId").val();
        //资产类型
        param.finprodType2 = INT_ASSET_TYPE;
        //推算方式非必填  check需用
        if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F07){
            param.type6 = $('#type6').val();
        }
        if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F17){
            param.profitType = $('#profitType').val();
        }
        //父画面--起息日
        param.vdate = $('#vdate').val();
        //父画面--到期日
        param.mdate = $('#mdate').val();
        //父页面--币种
        param.ccy = $('#ccy').val();
        //父画面--交易日历
        param.calendarId = $("#calendarId").val();
        //计息营业日准则
        param.endTdayRule = endTdayRule;
        //支付日营业日准则
        param.payTdayRule = payTdayRule;
        //支付频率
        param.payCycle = payCycle;
        //推算方式
        param.calcRule = calcRule;
        //首次付息日
        param.firstPayDate = firstPayDate;
        //自定义说明
        param.remark = remark;
        param.couponSpecies = getFinInterestFactorElementById('couponSpecies').val();
        //计息要素
        param.finInterestFactorDto ={};
        //资产类型
        param.finInterestFactorDto.finprodType2 = INT_ASSET_TYPE;
        //计息要素--息票品种
        param.finInterestFactorDto.couponSpecies = getFinInterestFactorElementById('couponSpecies').val();
        //计息要素--利率类型
        // 一体化对应etliu start
//        param.finInterestFactorDto.rateType = getFinInterestFactorElementById('rateType').val();
        param.finInterestFactorDto.intType = getFinInterestFactorElementById('rateType').val();
     // 一体化对应etliu end
        
        //计息要素--计息基础
        param.finInterestFactorDto.basis = getFinInterestFactorElementById('basis').val();
        //计息要素--利息分配方式
        param.finInterestFactorDto.basisStyle = getFinInterestFactorElementById('basisStyle').val();
        //计息要素--初始化利率
        param.finInterestFactorDto.rate = getFinInterestFactorElementById('rate').val();
        //利率上限
        param.finInterestFactorDto.highestRate = getFinInterestFactorElementById('rateMax').val();
        //利率下限
        param.finInterestFactorDto.lowestRate = getFinInterestFactorElementById('rateMin').val();
        //浮动利率--基准编号
        param.finInterestFactorDto.benchmarkId = getFinInterestFactorElementById('indexId').val();
        //系数
        param.finInterestFactorDto.coefficient = getFinInterestFactorElementById('coefficient').val();
        //利差
        param.finInterestFactorDto.spreadRate = getFinInterestFactorElementById('spreadRate').val();
        //基准利率调整规则
        param.finInterestFactorDto.resetType = getFinInterestFactorElementById('resetType').val();
        //延后时间
        param.finInterestFactorDto.observeBefDay = getFinInterestFactorElementById('observeBefDay').val();
        //延后时间单位
        param.finInterestFactorDto.observeBefUnit = getFinInterestFactorElementById('observeBefUnit').val();
        //资产推荐费
        param.finInterestFactorDto.referRate = getFinInterestFactorElementById('referRate').val();
        //净投资收益率
        param.finInterestFactorDto.investRate = getFinInterestFactorElementById('investRate').val();
        //借款利率
        param.finInterestFactorDto.loadRate = getFinInterestFactorElementById('loadRate').val();
        //买断利率
        param.finInterestFactorDto.buyOutRate = $('#buyOutRate').val();
        //计息利率方式
        param.finInterestFactorDto.calRateType = getFinInterestFactorElementById('calRateType').val();
        param.initFlag = initFlag;
        param.isAgainCalInt = isAgainCalInt;
        param.isSubAmtCal = isSubAmtCal;
        var isPayIntFlag = getBussFinRepayElmentById('isPayIntFlag').next().hasClass('layui-form-onswitch');
        if(isPayIntFlag){
            //是否还本不带息
            param.isPayIntFlag='Y';

        }else{
            param.isPayIntFlag='N';
        }
        param.pageModel = pageModel;
        param.entityName =  entityName_FinPayInterestStdInfo;
        //确认按钮，需要带出计划列表重新生成计划
        if(isAgainCalInt){
            param.invScheduleList = getAllRows();
        }else{
            //若该资产有交易时，交易中的计划不可修改 需要带入后台  重新算出计划区间
            param.invScheduleList = getAllRows().filter(function(item){
                if((new Date(item.vdate).DateDiff('d',item.maxTradeDate)>0&&
                        new Date(item.mdate).DateDiff('d',item.maxTradeDate)<=0)
                        ||new Date(item.mdate).DateDiff('d',item.maxTradeDate)>=0){
                    return true;
                }
                /*if(item.updateFlg=='02'){
                    return true;
                }*/
                return false;
            })
        }
        //获取还本列表信息
        //存款类、计划类、项目类(主画面存款本金、融资金额)--issueAmt
        if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F07||INT_ASSET_TYPE==FINPROD_TYPE_TG_F17||INT_ASSET_TYPE==FINPROD_TYPE_TG_F18){
            param.issueAmt = $('#issueAmt').val();
        }else if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F01){
            param.issueAmt = 100;
        }
        //存款类、计划类无还本列表  INT_ASSET_TYPE_PAYINTEREST:区分付息计划维护
        if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F01||(INT_ASSET_TYPE==FINPROD_TYPE_TG_F18 && INT_ASSET_TYPE_PAYINTEREST == null)
        ||INT_ASSET_TYPE==FINPROD_TYPE_TG_F17){
            if(window.BussFinRepayment){
                var invRepayList = BussFinRepayment.getAllRows();
                param.invRepayList = invRepayList;
            }
        }
        param.iscallbackPayByRepay = iscallbackPayByRepay;
        //项目类资产通道费率
        if((INT_ASSET_TYPE==FINPROD_TYPE_TG_F18&&INT_ASSET_TYPE_PAYINTEREST== '')
        ||INT_ASSET_TYPE==FINPROD_TYPE_TG_F17){
            if(BussParticipantInfo){
                param.bussParticipantInfoDtoList =  BussParticipantInfo.getAllRows();
            }
        }
        //付息计划维护标志 INT_ASSET_TYPE_PAYINTEREST
        param.adjPlanFlag = INT_ASSET_TYPE_PAYINTEREST;
        _.each(param.invScheduleList, function(item) {
            item.mdate = (new Date(item.mdate)).format('yyyy-MM-dd');
            item.vdate = (new Date(item.vdate)).format('yyyy-MM-dd');
        });
        _.each(param.invRepayList, function(item) {
            item.mdate = (new Date(item.mdate)).format('yyyy-MM-dd');
            item.vdate = (new Date(item.vdate)).format('yyyy-MM-dd');
        });
        return param;
    }


    /**
     * 生成计划
     * flag(true:确认  flase:生成计划)
     * iscallback:是否被还本信息业务组件生成计划按钮回调 
     */
    var generationPlan = function(flag,iscallback){
        loadAjaxData = true;
        initFlag = false;//非初始化
        genClick = true;
        isAgainCalInt = flag;
        iscallbackPayByRepay = iscallback;
        var list = getAllRows();
        if(!list || list.length == 0) {
            initGenerationPlan(flag,callFinRepayment);
        }else{
            if(isAgainCalInt){
                initGenerationPlan(flag,callFinRepayment);
            }else{
                if(iscallback||flag){
                    initGenerationPlan(flag,callFinRepayment);
                }else if(isEditPayByRepay){
                    Joyin.confirm(JoyinUtil.getMessage('BUSS_COM_C_0098','付息计划'),function(){
                        //callFinRepayment 调用还本计划公开接口方法
                        initGenerationPlan(flag,callFinRepayment);
                        isEditPayByRepay = false;
                        isClickSure = false;
                    });
                }else{
                    Joyin.confirm(JoyinUtil.getMessage('BUSS_COM_C_0047'),function(){
                        //callFinRepayment 调用还本计划公开接口方法
                        initGenerationPlan(flag,callFinRepayment);
                        isEditPayByRepay = false;
                        isClickSure = false;
                    }); 
                }
            }
        }
        iscallbackPayByRepay = false;
    }
    /**
     * 生成计划前台check(js)
     */
    var generationPlanCheck = function(){
        var msgArray=[];var errorClassArray = [];var messages="";
        var newParam = getPreviewParams(),msg;
        //债券
        if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F01){
            if(newParam.vdate==''||typeof newParam.vdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.vdate"));
                msgArray.push(msg);errorClassArray.push(vdate);
            }
            if(newParam.mdate==''||typeof newParam.mdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.mdate"));
                msgArray.push(msg);errorClassArray.push(mdate);
            }
            if(newParam.calendarId==''||typeof newParam.calendarId == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.calendarId"));
                msgArray.push(msg);errorClassArray.push(calendarId);
            }
            if(newParam.payTdayRule==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.payTdayRule"));
                msgArray.push(msg);errorClassArray.push(getElementById('payTdayRule'));
            }
            //存款类
        }else if(INT_ASSET_TYPE == FINPROD_TYPE_TG_F07){
            if(newParam.vdate==''||typeof newParam.vdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.vdate"));
                msgArray.push(msg);errorClassArray.push(vdate);
            }
            if(newParam.mdate==''||typeof newParam.mdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.mdate"));
                msgArray.push(msg);errorClassArray.push(mdate);
            }
            if(newParam.calendarId==''||typeof newParam.calendarId == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.calendarId"));
                msgArray.push(msg);errorClassArray.push(calendarId);
            }
            if(newParam.issueAmt==''||typeof newParam.issueAmt == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.issueAmt"));
                msgArray.push(msg);errorClassArray.push(issueAmt);
            }
            if(newParam.payTdayRule==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.payTdayRule"));
                msgArray.push(msg);errorClassArray.push(getElementById('payTdayRule'));
            }
            //计划类
        }else if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F17){
            if(newParam.vdate==''||typeof newParam.vdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.vdate2"));
                msgArray.push(msg);errorClassArray.push(vdate);
            }
            if(newParam.mdate==''||typeof newParam.mdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.mdate2"));
                msgArray.push(msg);errorClassArray.push(mdate);
            }
            if(newParam.calendarId==''||typeof newParam.calendarId == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.calendarId"));
                msgArray.push(msg);errorClassArray.push(calendarId);
            }
            if(newParam.payTdayRule==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.payTdayRule"));
                msgArray.push(msg);errorClassArray.push(getElementById('payTdayRule'));
            }
            if(newParam.issueAmt==''||typeof newParam.issueAmt == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.issueAmt2"));
                msgArray.push(msg);errorClassArray.push(issueAmt);
            }
            if(BigNumber($('#referRate'))<=0){
                msg = JoyinUtil.getMessage('BUSS_COM_E_0004',[JoyinUtil.getLabel("FinInterestFactor.investRate"), JoyinUtil.getLabel("FinInterestFactor.buyOutRate")]);
                msgArray.push(msg);errorClassArray.push(referRate);
            }
            if(newParam.vdate!=null && newParam.vdate!=''&&newParam.mdate!=null && newParam.mdate!=''){
               var vdateDate = new Date(newParam.vdate);
               var mdateDate = new Date(newParam.mdate);
               if (vdateDate.DateDiff('d', mdateDate) <= 0) {
                    msg = JoyinUtil.getMessage("终止日期必须大于成立日期");
                    msgArray.push(msg);
               }
            }
        }else if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F18){
            if(newParam.vdate==''||typeof newParam.vdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.vdate"));
                msgArray.push(msg);errorClassArray.push(vdate);
            }
            if(newParam.mdate==''||typeof newParam.mdate == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.mdate"));
                msgArray.push(msg);errorClassArray.push(mdate);
            }
            if(newParam.calendarId==''||typeof newParam.calendarId == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.calendarId"));
                msgArray.push(msg);errorClassArray.push(calendarId);
            }
            if(newParam.issueAmt==''||typeof newParam.issueAmt == 'undefined'){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.asset.issueAmt2"));
                msgArray.push(msg);errorClassArray.push(issueAmt);
            }
            if(newParam.endTdayRule==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.endTdayRule"));
                msgArray.push(msg);errorClassArray.push(getElementById('endTdayRule'));
            }
            if(newParam.payTdayRule==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.payTdayRuleProject"));
                msgArray.push(msg);errorClassArray.push(getElementById('payTdayRule'));
            }
            if(BigNumber($('#referRate').val())<0){
                msg = JoyinUtil.getMessage('BUSS_COM_E_0004',[JoyinUtil.getLabel("FinInterestFactor.investRate"), JoyinUtil.getLabel("FinInterestFactor.buyOutRate")]);
                msgArray.push(msg);errorClassArray.push(referRate);
            }
            //还本信息 还本周期不可为空
            /*if(getBussFinRepayElmentById('payCycle').val()==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("BussFinRepayment.payCycle"));
            }*/
            /*//支付频率“不规则”，自定义说明不可为空
            if(newParam.calcRule==INTPAY_CYCLE_09){
                if(newParam.firstPayDate==''){
                    msg = JoyinUtil.getMessage('BUSS_COM_E_0008',[JoyinUtil.getLabel("FinPayInterestStdInfo.payCycle"),INTPAY_CYCLE_NAME_09,JoyinUtil.getLabel("FinPayInterestStdInfo.remark")]);
                }
            }*/
        }
        //息票品种为空或者附息时
        if(newParam.couponSpecies===""&&newParam.couponSpecies==COUPON_SPECIES_01){
            if(newParam.payCycle==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.payCycle"));
                msgArray.push(msg);errorClassArray.push(getElementById('payCycle'));
            }
            if(newParam.calcRule==''){
                msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.calcRule"));
                msgArray.push(msg);errorClassArray.push(getElementById('calcRule'));
            }
        }
        //当推算方式选择“首次付息日”，首次付息日不可为空
        if(newParam.calcRule==CALC_METHOD_04){
            if(newParam.firstPayDate==''){
                msg = JoyinUtil.getMessage('BUSS_COM_E_0008',[JoyinUtil.getLabel("FinPayInterestStdInfo.calcRule"),CALC_METHOD_NAME_04,JoyinUtil.getLabel("FinPayInterestStdInfo.firstPayDate")]);
                msgArray.push(msg);errorClassArray.push(getElementById('firstPayDate'));
            }
            else {
                if (newParam.vdate && newParam.firstPayDate <= newParam.vdate) {
                    msg = JoyinUtil.getMessage('BUSS_COM_E_0036',[JoyinUtil.getLabel("FinPayInterestStdInfo.firstPayDate"), "资产起息日"]);
                    msgArray.push(msg);errorClassArray.push(getElementById('firstPayDate'));
        }
            }
        }
        if(msgArray&&msgArray.length>0){
            msgArray.forEach(function(value){
                messages = messages + '<span>' +value + '</span>' + '<br>';
            });
        }
        if(errorClassArray&&errorClassArray.length>0){
            errorClassArray.forEach(function(value){
                var $el = $(value);
                if ($el.is('.input-select')) {
                    $el = $el.find(' ~ .select2').find('.select2-selection');
                }else if ($(value).is('.input-checkbox')) {
                    $el = $el.next('ul.layui-checkbox-u').find('li.layui-chcekbox');
                }
                $el.addClass('errors');
            });
        }
        if(messages){
            Joyin.error(messages);
            $('.layui-layer-padding').height('80');
            $('.layui-layer-dialog').height('200');
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 比较两个数值是否相等
     * @param numStr1
     * @param numStr2
     * @returns
     */
    function isNumEquals (numStr1, numStr2) {
    	var isNumStr1Null = false;
    	var isNumStr2Null = false;
    	if (numStr1 == null || numStr1 == '' || !numStr1) {
    		isNumStr1Null = true;
    	}
    	if (numStr2 == null || numStr2 == '' || !numStr2) {
    		isNumStr2Null = true;
    	}
    	if (isNumStr1Null && isNumStr2Null) {
    		return true;
    	}
    	if ((isNumStr1Null && !isNumStr2Null)
    			|| (!isNumStr1Null && isNumStr2Null)) {
    		return false;
    	}
    	var num1 = parseFloat(numStr1);
    	var num2 = parseFloat(numStr2);
    	var tempResutl = JoyinMath.doMinus(num1, num2, 12);
    	if (tempResutl == 0) {
    		return true;
    	} else {
    		return false;
    	}
    }

    /*
     * 保存验证
     */
    function check() {
        // 付息计划列表为空时
        var list = getAllRows();
        if(!list || list.length == 0) {
            getElementById('btn_preview').trigger('click');
        }
        else {
            var newParam = getPreviewParams(), msg;
            // 起息日，到期日，交易日历与计划生成时记录的值比较，值变更时
            if(previewParams.vdate != newParam.vdate) {
                msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.asset.vdate"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
            }
            else if(previewParams.mdate != newParam.mdate) {
                msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.asset.mdate"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
            }
            else if(previewParams.calendarId != newParam.calendarId) {
                msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.asset.calendarId"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
            }
            //计息要素所有值  计划生成时记录的值比较，值变更时
            else if(previewParams.finInterestFactorDto.couponSpecies != newParam.finInterestFactorDto.couponSpecies||previewParams.finInterestFactorDto.rateType != newParam.finInterestFactorDto.rateType
                ||previewParams.finInterestFactorDto.basis != newParam.finInterestFactorDto.basis||previewParams.finInterestFactorDto.basisStyle != newParam.finInterestFactorDto.basisStyle
                || !isNumEquals(previewParams.finInterestFactorDto.rate, newParam.finInterestFactorDto.rate) || !isNumEquals(previewParams.finInterestFactorDto.highestRate, newParam.finInterestFactorDto.highestRate)
                || !isNumEquals(previewParams.finInterestFactorDto.lowestRate, newParam.finInterestFactorDto.lowestRate) || previewParams.finInterestFactorDto.benchmarkId != newParam.finInterestFactorDto.benchmarkId
                || previewParams.finInterestFactorDto.coefficient != newParam.finInterestFactorDto.coefficient || !isNumEquals(previewParams.finInterestFactorDto.spreadRate, newParam.finInterestFactorDto.spreadRate)
                ||previewParams.finInterestFactorDto.resetType != newParam.finInterestFactorDto.resetType||previewParams.finInterestFactorDto.observeBefDay != newParam.finInterestFactorDto.observeBefDay
                ||previewParams.finInterestFactorDto.observeBefUnit != newParam.finInterestFactorDto.observeBefUnit|| !isNumEquals(previewParams.finInterestFactorDto.investRate, newParam.finInterestFactorDto.investRate)
                ||!isNumEquals(previewParams.finInterestFactorDto.loadRate, newParam.finInterestFactorDto.loadRate) || !isNumEquals(previewParams.finInterestFactorDto.buyOutRate, newParam.finInterestFactorDto.buyOutRate)
                || !isNumEquals(previewParams.finInterestFactorDto.referRate, newParam.finInterestFactorDto.referRate)
                ||previewParams.finInterestFactorDto.calRateType != newParam.finInterestFactorDto.calRateType) {
                msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.InterestFactor"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
            }
            //付息规则，计划生成时记录的值比较，值变更时
            else if(previewParams.endTdayRule != newParam.endTdayRule || previewParams.payTdayRule != newParam.payTdayRule
                || previewParams.payCycle != newParam.payCycle || previewParams.calcRule != newParam.calcRule
                || previewParams.firstPayDate != newParam.firstPayDate) {
                msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
            }
            //存款类
            if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F07){
                //存款本金变化
                if(previewParams.issueAmt != newParam.issueAmt){
                    msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.asset.issueAmt"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
                }
                
            }
            //计划类、项目类
            if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F17||INT_ASSET_TYPE==FINPROD_TYPE_TG_F18){
                //存款本金变化
                if(previewParams.issueAmt != newParam.issueAmt){
                    msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.asset.issueAmt2"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
                }

                if(previewParams.isPayIntFlag != newParam.isPayIntFlag){
                    msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.ispayInt"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
                }
            }
            if(msg) {
                Joyin.error(msg);
                return false;
            }
        }
        return true;
    }
     /*
         * 保存验证
         */
        function saveCheck() {
            var msg;
            // 付息计划列表为空时
            var list = getAllRows();
            if(!list || list.length == 0) {
                msg = JoyinUtil.getMessage('COMMON_E_0057',"付息计划");
            }
            else {
                //付息计划条数增加或减少，应点击确定按钮
                if(list && list.length > 0 && previewParams.invScheduleList && previewParams.invScheduleList.length>0 && list.length !=previewParams.invScheduleList.length ) {
                      msg = JoyinUtil.getMessage("付息计划已变更,请点击确认!");
                }

            }
             if(msg) {
                  Joyin.error(msg);
                  return false;
             }
            return true;
        }
    /**
     * 维护付息计划后，是否点击确认
     * 可以通过支付日是否为空判断是否重新计算利息
     * 添加标志 isClickSure 当修改付息计划时为true,点击确认后为false
     */
    var isSureCheck = function(){
        var list = getAllRows();var msg;

        if(!list || list.length == 0) {
            msg = JoyinUtil.getMessage('COMMON_E_0057',"付息计划");
        }

        if(list){
            $.each(list,function(index,obj){
                if (obj.payDate == null || obj.payDate == '' || typeof obj.payDate == 'undefined' || isClickSure) {
                    msg = JoyinUtil.getMessage("付息计划已变更,请点击确认,重新计算应计利息!");
                }
            })
        }
        if(msg) {
            Joyin.error(msg);
            return false;
        }
        return true;
    }

    /**
     * 确认按钮check(js)
     */
    var comfirmCheck = function(){
        var listSortBefore = getAllRows(),msg;
        var breakflag = true;
        if(listSortBefore){
            $.each(listSortBefore,function(index,obj){
                //支付日是否为空 判断该数据是新增还是原始数据
                if(obj.payDate==''|| obj.payDate==null ||typeof obj.payDate =='undefined'){
                    if(obj.vdate==''|| obj.vdate==null ||typeof obj.vdate =='undefined'){
                        msg = JoyinUtil.getMessage("新增计划计息开始日不可以为空");
                    }else if(new Date(obj.vdate).DateDiff('d',obj.maxTradeDate)>0){
                        msg = JoyinUtil.getMessage("新增计划计息开始日不可以小于交易日");
                    }
                    if(obj.mdate==''|| obj.mdate==null ||typeof obj.mdate =='undefined'){
                         msg = JoyinUtil.getMessage("新增计划计息结束日不可以为空");
                    }else if(new Date(obj.mdate).DateDiff('d',obj.maxTradeDate)>0){
                        msg = JoyinUtil.getMessage("新增计划计息结束日不可以小于交易日");
                    }
                    if(msg) {
                        Joyin.error(msg);
                        breakflag = false;
                    }
                }
            });
        }
        if(!breakflag){
            return false;
        }
        sortTalbe();
        var newParam = getPreviewParams();
        var list = getAllRows();
        if(!list || list.length == 0) {
            msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.table.title"));
        }
        else {
             if(previewParams.finInterestFactorDto.couponSpecies != newParam.finInterestFactorDto.couponSpecies||previewParams.finInterestFactorDto.rateType != newParam.finInterestFactorDto.rateType
                ||previewParams.finInterestFactorDto.basis != newParam.finInterestFactorDto.basis||previewParams.finInterestFactorDto.basisStyle != newParam.finInterestFactorDto.basisStyle
                || !isNumEquals(previewParams.finInterestFactorDto.rate, newParam.finInterestFactorDto.rate) || !isNumEquals(previewParams.finInterestFactorDto.highestRate, newParam.finInterestFactorDto.highestRate)
                || !isNumEquals(previewParams.finInterestFactorDto.lowestRate, newParam.finInterestFactorDto.lowestRate) || previewParams.finInterestFactorDto.benchmarkId != newParam.finInterestFactorDto.benchmarkId
                || previewParams.finInterestFactorDto.coefficient != newParam.finInterestFactorDto.coefficient || !isNumEquals(previewParams.finInterestFactorDto.spreadRate, newParam.finInterestFactorDto.spreadRate)
                ||previewParams.finInterestFactorDto.resetType != newParam.finInterestFactorDto.resetType||previewParams.finInterestFactorDto.observeBefDay != newParam.finInterestFactorDto.observeBefDay
                ||previewParams.finInterestFactorDto.observeBefUnit != newParam.finInterestFactorDto.observeBefUnit|| !isNumEquals(previewParams.finInterestFactorDto.investRate, newParam.finInterestFactorDto.investRate)
                ||!isNumEquals(previewParams.finInterestFactorDto.loadRate, newParam.finInterestFactorDto.loadRate) || !isNumEquals(previewParams.finInterestFactorDto.buyOutRate, newParam.finInterestFactorDto.buyOutRate)
                || !isNumEquals(previewParams.finInterestFactorDto.referRate, newParam.finInterestFactorDto.referRate)
                ||previewParams.finInterestFactorDto.calRateType != newParam.finInterestFactorDto.calRateType) {
                msg = JoyinUtil.getMessage('BUSS_COM_E_0037', [JoyinUtil.getLabel("FinPayInterestStdInfo.InterestFactor"),JoyinUtil.getLabel("FinPayInterestStdInfo.title")]);
            }
            //第一条计息开始日==起息日
            var firstVdateUnadjust = new Date(list[0].vdate).format('yyyy-MM-dd');
            //最后一条计息截止日==到期日
            var lastMdateUnadjust = new Date(list[list.length-1].mdate).format('yyyy-MM-dd');
            //计息开始日、计息结束日、支付日
            //vdateUnadjust、mdateUnadjust、payDate
            if(firstVdateUnadjust != newParam.vdate){
                msg = JoyinUtil.getMessage('FIN_ASSET_E_0010', JoyinUtil.getLabel("FinPayInterestStdInfo.asset.vdate"));
            }
            if(lastMdateUnadjust != newParam.mdate){
                msg = JoyinUtil.getMessage('FIN_ASSET_E_0011', JoyinUtil.getLabel("FinPayInterestStdInfo.asset.mdate"));
            }
            $.each(list,function(index,obj){
                var vdateUnadjust = new Date(obj.vdate).format('yyyy-MM-dd');
                var mdateUnadjust = new Date(obj.mdate).format('yyyy-MM-dd');
                if(obj.vdate==''||typeof obj.vdate == 'undefined'){
                        msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.vdate"));
                 }
                if(obj.mdate==''||typeof obj.mdate == 'undefined'){
                    msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.mdate"));
                }
                if(INT_ASSET_TYPE_PAYINTEREST==COMMON_BUSINESS_TYPE_FINPROD10){
                    if(obj.payDate==''||typeof obj.payDate =='undefined'){
                        msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("FinPayInterestStdInfo.payDate"));
                    } 
                }
                //付息计划多条
                if(list.length!='1'&&index<list.length-1){
                    //下一条计息开始日
                    var nextVdateUnadjust = new Date(list[index+1].vdate).format('yyyy-MM-dd');
                    if(nextVdateUnadjust!=mdateUnadjust){
                        msg = JoyinUtil.getMessage('FIN_ASSET_E_0012');
                    }
                }
            })
        }
        if(msg) {
            Joyin.error(msg);
            return false;
        }
        return true;
    }

    //确认按钮执行
    var confirmPlan = function(){
        generationPlan();
    }

    /**
     * 调用还本业务组建--生成还本计划
     */
    var callFinRepayment = function(flag){
        //仅债券类资产、项目类默认调用
        if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F01||(INT_ASSET_TYPE==FINPROD_TYPE_TG_F18&&!iscallbackPayByRepay)
        ||(INT_ASSET_TYPE==FINPROD_TYPE_TG_F17&&!iscallbackPayByRepay)){
            if(BussFinRepayment){
                    BussFinRepayment.generationPlan(flag,false,true);
            }
        }
    }

    /**
     * 重新排序table
     */
    var sortTalbe = function(){
        //排序不改变本次参数保存
        loadAjaxData = false;
        var list = getAllRows();
        list.sort(function(a,b){
            return -(new Date(a.vdate).DateDiff('d',b.vdate));
        })
        generationPlanTable.reload({data:list, url:null});
    }

    function checkObjectDiff(currentDealObj, backupDealObj) {
        var result = false;
        if (currentDealObj && backupDealObj && typeof currentDealObj == "object"
            && typeof backupDealObj == "object") {
            for ( var key1 in currentDealObj) {
                var currentDealObj1 = currentDealObj[key1];
                var backupDealObj1 = backupDealObj[key1];
                result = checkObjectDiff(currentDealObj1, backupDealObj1);
                if (result) {
                    break;
                }
            }
        }
        else if (currentDealObj != backupDealObj) {
            // add by jiangpeng on 20210708 start
            // TISHKBS-2450 mdate vdate 传值为number格式,表格reload后,转为string格式,导致判断不相等 此处转化为日期格式对比
            if(typeof currentDealObj !== typeof backupDealObj){
                var currentDealObjDate = new Date(currentDealObj);
                var backupDealObjDate = new Date(backupDealObj);
                var dsadf = new Date('dsadf');
                if (currentDealObjDate.DateDiff('d', backupDealObjDate) === 0) {
                    return false;
                }
            }
            // add by jiangpeng on 20210708 end
            return true;
        }
        return result;
    }

    var checkPlanChanged = function(){
        var result = false;
        if (allPlanRows) {
            var planRows = generationPlanTable.getAllRows();
            if (allPlanRows.length != planRows.length) {
                return true;
            }
            if (allPlanRows.length != 0 && planRows.length != 0) {
                for ( var key in allPlanRows) {
                    var currentDealObj = allPlanRows[key];
                    var backupDealObj = planRows[key];
                    result = checkObjectDiff(currentDealObj, backupDealObj);
                    if (result) {
                        break;
                    }
                }
            }
        }
        return result;
    }
    // 公开接口
    window.FinPayInterestStdInfo = {
        comfirmCheck: comfirmCheck,
        isSureCheck: isSureCheck,
        getAllRows:getAllRows,
        check:check,
        generationPlan:generationPlan,
        disableFinPayInterest:disableFinPayInterest,
        checkPlanChanged : checkPlanChanged,
        clearAll: clearAll
    };


    /**
     * 推算方式change
     * 推算方式为首次付息日时，首次付息日可填且必填
     * 其他情况置灰
     */
    var handlecalcRule = function(){
        var calcRule = getElementById("calcRule").val();
        //当推算方式改为首次付息日时，画面.首次付息日改为必填。否则，画面.首次付息日清空，且改为只读。
        if(!calcRule || calcRule==CALC_METHOD_04){
            getElementById("firstPayDate").removeAttr('disabled');
        }else{
            getElementById("firstPayDate").val('').attr('disabled',true).trigger("change");
        }
    }

    /**
     * 付息频率change事件
     */
    var payCycleChange = function(){
        var payCycle = getElementById("payCycle").val();
        $("#finPayInterestStdInfoDto_payMonth").attr("disabled",false);
        if(payCycle==INTPAY_CYCLE_04){
            $("#finPayInterestStdInfoDto_payMonth").attr("multiple",true)
        }
        else if(payCycle==INTPAY_CYCLE_05){
            $("#finPayInterestStdInfoDto_payMonth").attr("multiple",false)
        }
        else {
            $("#finPayInterestStdInfoDto_payMonth").attr("disabled",true);
        }
        if($("#finPayInterestStdInfoDto_payMonth").data("select2")){
            $("#finPayInterestStdInfoDto_payMonth").select2('destroy').empty();
            $("#finPayInterestStdInfoDto_payMonth").textSelect()
        }
    }
    $(function(){
        init();
        //推算方式change
        getElementById('calcRule').change(handlecalcRule).trigger("change");
        //付息频率
        getElementById('payCycle').change(payCycleChange).trigger('change');
        //生成计划按钮
        getElementById("btn_generationPlan").click(function(){
            // modify by  20200903

            if(generationPlanCheck()){
                Joyin.ajaxJson('/component/asset/FinPayInterestStdInfo/check', getPreviewParams(), function(result) {
                    //计划类还款周期置到期还本
                    getBussFinRepayElmentById('payCycle').val(INTPAY_CYCLE_10).trigger('change');
                    //生成付息计划
                    generationPlan(false);
                });
            }
        })
        //确认按钮
        getElementById("btn_confirm").click(function(){
            if(comfirmCheck()){
                Joyin.ajaxJson('/component/asset/FinPayInterestStdInfo/check', getPreviewParams(), function(result) {
                    if(INT_ASSET_TYPE==FINPROD_TYPE_TG_F01||INT_ASSET_TYPE==FINPROD_TYPE_TG_F18||INT_ASSET_TYPE==FINPROD_TYPE_TG_F17){
                        isAgainCalInt = true;
                        Joyin.ajaxJson('/component/asset/BussFinRepayment/check', getPreviewParams(), function(result) {
                            Joyin.confirm(JoyinUtil.getMessage('FIN_ASSET_W_0013'),function(){
                                generationPlan(true);
                                isClickSure = false;
                            });
                        });
                    }else{
                        Joyin.confirm(JoyinUtil.getMessage('FIN_ASSET_W_0013'),function(){
                            generationPlan(true);
                            isClickSure = false;
                        });
                    }
                });
            }
        })
    });
}());
function deepCopy(obj) {
    var result = Array.isArray(obj) ? [] : {};
    for (var key in obj) {
        if (obj.hasOwnProperty(key)) {
            if (typeof obj[key] === 'object' && obj[key]!==null) {
                result[key] = deepCopy(obj[key]);   //递归复制
            } else {
                result[key] = obj[key];
            }
        }
    }
    return result;
}
