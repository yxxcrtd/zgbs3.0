(function () {
    /**
     * 页面初始化
     */
	var acctNoList;
    var calcConditionObjBackUp;
    repoInfoTableOption = {
        rowno: true,
        searchOnInit: true,
        checkbox: false,
        page: false,
        cols: [
            //组合
            {field: 'portfolioId', width: "200", title: 'IntDetailDuration.portfolioId', sortable:false}
//            组合名称
            ,{field: 'portfolioName', width: "200", title: 'IntDetailDuration.portfolioName', sortable:false}
            //付息周期
            , {field: 'calcRuleString', width: "90", title: 'IntDetailDuration.calcRuleString', sortable:false}
//          投资目的
//             , {field: 'invAimString', width: "200", title: '投资目的/是否融入',sortable:false,visible: SHOW_INVAIM_OR_BUSSINSSMODEL=='01'?true:false}
            //持有面额
            , {field: 'qty', width: "200", title: 'IntDetailDuration.holdAmt', type: 'money', sortable:false}
            //还本金额
            , {field: 'prinAmtTheory', width: "200", title: 'IntDetailDuration.returnAmount', type: 'money', sortable:false}
            //计息开始日
            , {field: 'vDate', width: "120", title: 'IntDetailDuration.vDate', sortable:false}
            //计息结束日
            , {field: 'mDate', width: "120", title: 'IntDetailDuration.mDate', sortable:false}
            //票面利率
            , {field: 'yield', width: "200", title: 'IntDetailDuration.repPaperRate', type: 'money', sortable:false}
            //理论应收利息
            , {field: 'intAmtTheory', width: "200", title: 'IntDetailDuration.intAmtTheory', type: 'money', sortable:false}
            //实收利息
            , {field: 'intAmt',  width: "200",title: 'IntDetailDuration.intAmt', type: 'money', sortable:false}
            , {field: 'channelFee',  width: "200",title: function(){return JoyinUtil.getLabel("invest.channelId")+JoyinUtil.getLabel("TrdBondDuration002.channelFee");}(), type: 'money', sortable:false,visible:CHANNEL}
            , {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit'],
                render: function (data, type, row, meta) {
                    if(row.investYesorno =='Y'){
                        return;
                    }
                    return '<i class="btn layui-icon detail" data-ignore="true" title="明细">&#xe60a;</i>';
                }
            }
        ],
        beforeEditCell:function(){
        	return false;
        },
        done : function(){
            if (formTodoList) {
                calcConditionObjBackUp = tableInitData();
            }
            if(needInit){
            	resetTableData();
            	needInit =false;
            }
        },
        data: []
    }

    if (finAssetIntInfoList && finAssetIntInfoList.length > 0) {
        repoInfoTableOption.data = repoInfoTableOption.data.concat(finAssetIntInfoList);
    }
    /**
     * 定义数据表格
     */
    var repoInfoTable = $("#intDetailTable").joyintable(repoInfoTableOption);

    /**
     * 绑定编辑事件
     */
    repoInfoTable.bind('click', '.detail', function(data, index) {
        //实际交易组合无子组合交易明细
        var param = {
            "portfolioId":data.portfolioId,
            "finprodId":$('#finprodId').val(),
            "finprodMarketId":'',
            "invAim":data.invAim,
            "dealDate": new Date(data.mDate).format(),
            'bizCode':$("#busiCode").val(),
            "workflowId":typeof workflowId == "undefined" ? '':workflowId,
            "defaultMap":JSON.stringify({
                "calcRuleString":data.calcRuleString,
                "vDate":new Date(data.vDate).format(),
                "mDate":new Date(data.mDate).format(),
                "yield":data.yield,
                "invAim":data.invAim,
                "calcRule":data.calcRule,
            }),
            "preMap":JSON.stringify({
                "prinAmtTheory":data.prinAmtTheory,
                "intAmtTheory":data.intAmtTheory,
                "intAmt":data.intAmt,
                "channelFee":data.channelFee
                
            }),
            cols:JSON.stringify([
                {field: 'prinAmtTheory', width: "200", title: '还本金额', type: 'money', sortable:false}
                , {field: 'calcRuleString', width: "90", title: '付息周期', sortable:false}
                // , {field: 'invAimString', width: "200", title: '投资目的/是否融入', sortable:false,visible: SHOW_INVAIM_OR_BUSSINSSMODEL=='01'?true:false}
                , {field: 'vDate', width: "120", title: '计息开始日', sortable:false}
                , {field: 'mDate', width: "120", title: '计息结束日', sortable:false}
                , {field: 'yield', width: "150", title: '票面利率(%)', type: 'money', sortable:false}
                , {field: 'intAmtTheory', width: "200", title: '理论应收利息', type: 'money', sortable:false}
                , {field: 'intAmt',  width: "200",title: '实收利息', type: 'money', sortable:false}
                , {field: 'channelFee',  width: "200",title: function(){return JoyinUtil.getLabel("invest.channelId")+JoyinUtil.getLabel("TrdBondDuration002.channelFee");}(), type: 'money', sortable:false,visible:CHANNEL}
                , {field: 'calcRule', visible: false}
                , {field: 'taxAmount', visible: false}
                , {field: 'amount', visible: false}
                , {field: 'qty', visible: false}
            ])
        };
        Joyin.dialog(JoyinUtil.getLabel('TrdDurationCommon.ChildPortfolioInfo'), JoyinUtil.getRealPath('invest/period/common/ChildPortfolioInfo/init'), '80%', '80%', param);
    });

    var tableInitData = function () {
        var finAssetIntInfos = repoInfoTable.getAllRows();
        var dtos = [];
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            var dealReal = JSON.parse(deal);
            dealReal.tradeMarket=$("#tradeMarket").val();
            dealReal.settleDate=$("#tradeDate").val();
            dealReal.tradeAmt=finAssetIntInfos[i].tradeAmt;
            dtos.push({
                'isTotalTrade': YES_OR_NO_Y
                , 'bankLendingType': null
                , 'portfolioId': finAssetIntInfos[i].portfolioId
                , 'invAim' : finAssetIntInfos[i].invAim
                , 'deal':dealReal
            })
        }
        return dtos;
    }
    var intDetailTableLink = function(){
        var param = {
//        券面总额、理论应计利息、理论还本金额、理论本息总额、实收利息总额、实收本息总额
        qty:$('#qty').val(),
        intAmtTheory:$('#intAmtTheory').val(),
        prinAmtTheory:$('#prinAmtTheory').val(),
        fpriceAmt:$('#fpriceAmt').val(),
        intAmt:$('#intAmt').val(),
        tradeAmt:$('#tradeAmtReal').val(),
//      证券管理账户
        secManageAcctId:$('#secManageAcctId').val(),
//      金融产品代码
        finprodId:$('#finprodId').val(),
//      数据状态
        statusValid:$('#statusValid').val(),
        };
    }
    
    /**
     * 利用占比计算所有的金额
     */
    var resetTableData = function () {
        // 按占比计算还本金额、理论本息、实收本息
        var finAssetIntInfos = finAssetIntInfoList;
        //----add by jjpei 20190727  组合按持仓从小到大排序BSTWO-1098--start---
        finAssetIntInfos.sort(function (a,b) {
            return JoyinMath.doMinus(a.amount, b.amount);
        })
        //----add by jjpei 20190727  组合按持仓从小到大排序BSTWO-1098--end---
        var sumAmount = 0;
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            sumAmount = JoyinMath.doAdd(sumAmount, finAssetIntInfos[i].amount);
        }
//      券面总额、理论应计利息、理论还本金额、理论本息总额、实收利息总额、实收本息总额
        var itemArr = ['qty','intAmtTheory','prinAmtTheory','fpriceAmt','intAmt','tradeAmt',"channelFee",'tradeAmtReal'];
//      所有的钱
        var itemObj = {};
//      已分配的钱
        var sumObj = {};
//      初始化所有的钱和已分配的钱
        $.each(itemArr,function(index,val){
        	itemObj[val] = $('#'+val).val()||0;
        	sumObj[val] = 0;
        })
//      分配
        for (var i = 0; i < finAssetIntInfos.length; i++) {
//          最后一个
        	if (i == finAssetIntInfos.length - 1) {
//        		将剩余的钱放入对应值
        		$.each(itemArr,function(index,val){
        			finAssetIntInfos[i][val] = JoyinMath.doMinus(itemObj[val], sumObj[val]);
                })
        	}
//        	非最后一个
        	else {
//        		计算占比
        		// var prop = JoyinMath.doDiv(finAssetIntInfos[i].amount, sumAmount, 12, JoyinMath.ROUND_HALF_UP);
//        		根据占比处理所有的钱
        		$.each(itemArr,function(index,val){
//        			计算当前需要的钱
        			var thisNum = JoyinMath.doDiv(JoyinMath.doMul(itemObj[val], finAssetIntInfos[i].amount, 12, JoyinMath.ROUND_HALF_UP), sumAmount,2, JoyinMath.ROUND_HALF_UP);
//        			处理已分配的钱
        			sumObj[val] = JoyinMath.doAdd(sumObj[val], thisNum);
//        			把钱放入对应数据
        			finAssetIntInfos[i][val] = thisNum;
                })
        	}
        }
        if(COLLECT_TYPE == COLLECT_TYPE_01) {//回款前扣除
            $.each(finAssetIntInfos, function(i, val){
                val.tradeAmt = JoyinMath.doMinus( val.tradeAmtReal , val.channelFee);
            });
        }
        repoInfoTable.clear(function () {
            $.each(finAssetIntInfos, function (i, v) {
                repoInfoTable.addRow(v);
            })
        });
    }
    
    /**
     * 利用占比计算所有的金额
     */
    var channelFeeChange = function () {
        // 按占比计算还本金额、理论本息、实收本息
        var finAssetIntInfos = finAssetIntInfoList;
        var sumAmount = 0;
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            sumAmount = JoyinMath.doAdd(sumAmount, finAssetIntInfos[i].amount);
        }
//      所有的钱
        var itemObj = $('#channelFee').val()||0;
//      已分配的钱
        var sumObj = 0;

//      分配
        for (var i = 0; i < finAssetIntInfos.length; i++) {
//          最后一个
        	if (i == finAssetIntInfos.length - 1) {
//        		将剩余的钱放入对应值
        		
        			//finAssetIntInfos[i][val] = JoyinMath.doMinus(itemObj[val], sumObj[val]);
        		if(COLLECT_TYPE==COLLECT_TYPE_01){//回款前扣除
            	   repoInfoTable.renderRow(i,{"channelFee":JoyinMath.doMinus(itemObj, sumObj),
           			//"intAmt":JoyinMath.doMinus(finAssetIntInfoList[i].intAmt,JoyinMath.doMinus(itemObj, sumObj)),
           			"tradeAmt":JoyinMath.doMinus(finAssetIntInfoList[i].tradeAmt,JoyinMath.doMinus(itemObj, sumObj))});
               }else{
            	   repoInfoTable.renderRow(i,{"channelFee":JoyinMath.doMinus(itemObj, sumObj)
           			});
               }
        	}
//        	非最后一个
        	else {
//        		计算占比
        		var prop = JoyinMath.doDiv(finAssetIntInfos[i].amount, sumAmount, 12, JoyinMath.ROUND_HALF_UP);
//        		根据占比处理所有的钱
        		
//        			计算当前需要的钱
        			var thisNum = JoyinMath.doMul(itemObj, prop, 2, JoyinMath.ROUND_HALF_UP);
//        			处理已分配的钱
        			sumObj = JoyinMath.doAdd(sumObj, thisNum);
//        			repoInfoTable.renderRow(i,{"channelFee":JoyinMath.doMinus(itemObj, sumObj),
        			if(COLLECT_TYPE==COLLECT_TYPE_01){//回款前扣除
        			repoInfoTable.renderRow(i,{"channelFee":thisNum,
            			//"intAmt":JoyinMath.doMinus(repoInfoTable[i].intAmt,thisNum),
            			"tradeAmt":JoyinMath.doMinus(finAssetIntInfos[i].tradeAmt,thisNum)	
        			});
        	       }else{
        	    	   repoInfoTable.renderRow(i,{"channelFee":thisNum
               			});
        	       }
                
        	}
        }
        
    }
    /**
     * 页面初始化
     */
    var init = function () {
        if (needInit) {
            $("#intAmt").val($("#intAmtTheory").val()).trigger("blur");
            $("#tradeAmtReal").val($("#fpriceAmt").val()).trigger("blur");
        }

        $("#intAmt").change(function () {
            var prinAmtTheory = $("#prinAmtTheory").val();
            var intAmt = $("#intAmt").val();
            $("#tradeAmtReal").val(JoyinMath.doAdd(prinAmtTheory, intAmt)).trigger("blur");
            if(COLLECT_TYPE==COLLECT_TYPE_01){//回款前扣除
            	var channelFee=$("#channelFee").val();
            	if(channelFee==""||channelFee=="undefined")channelFee=0;
            	 $("#tradeAmt").val(JoyinMath.doMinus(JoyinMath.doAdd(prinAmtTheory, intAmt), channelFee)).trigger("blur");
            }else{
            	 $("#tradeAmt").val(JoyinMath.doAdd(prinAmtTheory, intAmt));
            }
            resetTableData();
        })
        $("#tradeAmtReal").change(function () {
            var prinAmtTheory = $("#prinAmtTheory").val();
            var tradeAmt = $("#tradeAmtReal").val();
            $("#intAmt").val(JoyinMath.doMinus(tradeAmt, prinAmtTheory)).trigger("blur");
            if(COLLECT_TYPE==COLLECT_TYPE_01){//回款前扣除
            	var channelFee=$("#channelFee").val();
            	if(channelFee==""||channelFee=="undefined")channelFee=0
            	 $("#tradeAmt").val(JoyinMath.doMinus(tradeAmt, channelFee)).trigger("blur");
            }else{
            	 $("#tradeAmt").val(JoyinMath.doMinus(tradeAmt,0));
            }
            resetTableData();
        })
        //update zhangweiqi 2019-10-15 
        if(CHANNEL)$("#channelFee").parent().parent().css("display","inline");
         $("#channelFee").change(function () {
            var channelFee = $("#channelFee").val();
            var tradeAmt = $("#tradeAmtReal").val();
            if(COLLECT_TYPE==COLLECT_TYPE_01){//回款前扣除
              $("#tradeAmt").val(JoyinMath.doMinus(tradeAmt, channelFee)).trigger("blur");
            }else{
            	 $("#tradeAmt").val(JoyinMath.doMinus(tradeAmt,0));
            }
            resetTableData();
        })
        acctNoList = JSON.parse($("#acctNoList").val());

    }

    function saveOrSubmit(operateType) {
        if (GEN_PAY_ORDERS_AUTO == '01') {
            generateOrders(function () {
                realSaveOrSubmit(operateType);
            });
        }
        else {
            realSaveOrSubmit(operateType);
        }
    }


    function realSaveOrSubmit(operateType) {
        if (isCalcConditionChanged()) {
            return;
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#informationForm', true);
        object.operateType = operateType;
        if(FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo();
        }
        Joyin.ajaxJson(basePath + 'invest/period/trdbondduration/TrdBondDuration002/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/period/trdbondduration/TrdBondDuration002/save', object, "TrdBondDuration002_btn_save", "TrdBondDuration002", function () {
                        if (parent.TrdBondDuration001) {
                            parent.TrdBondDuration001.reloadTable();
                        }
                    });
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/period/trdbondduration/TrdBondDuration002/save', object, "TrdBondDuration002_btn_submit", "TrdBondDuration002", function () {
                        if (parent.TrdBondDuration001) {
                            parent.TrdBondDuration001.reloadTable();
                        }
                    });
                }
            });
        });
    }

//    window.componentLoadCallback = function (isDone, pagePath, params) {
//        if (isDone) {
//            window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
//                if (!$("#tradeAmt").val()) {
//                    Joyin.error("请输入实收本息总额")
//                    return;
//                }
//                if (!$("#tradeDate").val()) {
//                    Joyin.error("请输入资金到账日")
//                    return;
//                }
//                var dealReal = JSON.parse(deal);
//                dealReal.tradeAmt = $("#tradeAmt").val();
//                dealReal.settleDate = $("#tradeDate").val();
//                dealReal.tradeMarket = $("#tradeMarket").val();
//                return {
//                    'isTotalTrade': YES_OR_NO_Y
//                    , 'bankLendingType': null
//                    , 'portfolioId': $("#portfolioId").val()
//                    , 'deal': dealReal
//                };
//            });
//            if (formTodoList) {
//                window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
//            }
//        }
//    }
//    2.0划款指令
    /**
     * 定义数据表格
     *
     */
    var option = {
        searchOnInit: true,
        checkbox: false,
        cols: [
            {
                field: 'isNeedSend', width: 140, title: 'BussTrdTransferOrder.isNeedSend', edit: true, sortable:false,
                editOption: {type: 'select', multiple: false, searchable: false, showField: 'text', result: YES_NO}
            }
            , {field:'payTypeText', width:130, title: 'BussTrdTransferOrder.payType', sortable:false, sortable:false}
            , {field:'settleDateText', width:130, title: 'BussTrdTransferOrder.settleDate', sortable:false}
            , {field: 'settleAmt', width: 130, title: 'BussTrdTransferOrder.settleAmt', type: 'money', sortable:false}
            , {field: 'ccyText', width: 130, title: 'BussTrdTransferOrder.ccy', sortable:false}
            , {
                field: 'payAcctNo', width: 130, title: 'BussTrdTransferOrder.payAcctNo', edit: true, sortable:false,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'id', parentIds:'orderId_select,acctNoList',
                    url: JoyinUtil.getRealPath("/common/multiOrder/select"), id: 'payAcctNo', postData:function () {
                        return {selectValue: JSON.stringify(orderAcctMap), type: 'pay'};
                    }
                }
            }
            , {field: 'payAcctName', width: 150, title: 'BussTrdTransferOrder.payAcctName', sortable:false}
            , {
                field: 'recAcctNo', width: 130, title: 'BussTrdTransferOrder.recAcctNo', edit: true, sortable:false,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'id', parentIds:'orderId_select,acctNoList',
                    url: JoyinUtil.getRealPath("/common/multiOrder/select"),id: 'recAcctNo',  postData:function () {
                        return {selectValue: JSON.stringify(orderAcctMap), type: 'rec'};
                    }
                }
            }
            , {field: 'recAcctName', width: 150, title: 'BussTrdTransferOrder.recAcctName', sortable:false}
            ,{field:'purpose', width:130, title: 'BussTrdTransferOrder.purpose', sortable:false, edit:true}
            , {field: 'portfolioId', width: 130, title: 'BussTrdTransferOrder.portfolioId', sortable:false}
            ,{field:'portfolioName', width:150, title: 'BussTrdTransferOrder.portfolioName', sortable:false}
            , {field: 'orderId', visible: true, edit: true,width:0,
                editOption: {
                    type: 'select', id:'orderId_select', childrenIds:'recAcctNo,payAcctNo'
                }}
            , {field: 'ps', visible: false}
            , {field: 'settleStatus', visible: false}
            , {field: 'isTotalTrade', visible: false}
        ],
        done:function(){
            $("[data-field='orderId']").hide();
        },
        page: false,
        data: [],
        editCell: function (value, row, field, cell, column) {
            if (field == 'payAcctNo') {
                if (typeof value == "undefined" || value == null || value == "") {
                    row.payAcctName = "";
                }
                // 选择付款账户带出贷款账户名称
                if (typeof value != "undefined" && value) {
                    Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                        ccy: row.ccy,
                        acctNo: value
                    }, function (result) {
                        // 网点
//                        row.recAcctPoint = result.largePayno;
                        //add by wangmin 2019-0-06 begin
                        if (!result) {
                            Joyin.error("该币种下，无此账户。")
                            return;
                        }
                        row.payAcctName = result.acctName;
                        //add by wangmin 2019-0-06 end
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                    })
                } else {
                    table.renderRow(row.LAY_TABLE_INDEX, row);
                }

            } else {
                if (typeof value == "undefined" || value == null || value == "") {
                    row.recAcctName = "";
                }
                if (typeof value != "undefined" && value) {
                    // 选择收款账户带出行内/行外、网点
                    Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                        ccy: row.ccy,
                        acctNo: value
                    }, function (result) {
                        var assetSource = JSON.parse(ASSET_SOURCE);
                        $.each(assetSource, function (i, v) {
                            if (v.id == result.inOutBank) {
                                row.inOutBank = v.text;
                            }
                        });
                        // 网点
                        row.recAcctPoint = result.largePayno;
                        //add by wangmin 2019-0-06 begin
                        row.recAcctName = result.acctName;
                        //add by wangmin 2019-0-06 end
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                    })
                } else {
                    table.renderRow(row.LAY_TABLE_INDEX, row);
                }
            }
        }
    }

    if (bussTrdTransferOrderDtos && bussTrdTransferOrderDtos.length > 0) {
        option.data = option.data.concat(bussTrdTransferOrderDtos);
    }
    var orderAcctMap = {};
    var table = $("#bussTrdTransferOrder_table").joyintable(option);
    // 生成指令参数保存
    var stlCommandInput = tableInitData;

    // 检查生成指令参数是否发生变化
    var isCalcConditionChanged = function() {
        // 参照模式，不做check
        if (pageModel === PAGE_MODEL_REFERENCE) {
            return false;
        }
        if (!$("#bussTrdTransferOrder_div").is(":visible")) {
            return false;
        }
        if (calcConditionObjBackUp == null) {
            return false;
        }

        if(stlCommandInput != null){
            var currentCalcConditionObj = stlCommandInput();
            // 影响计划生成的所有项目与计划生成时记录的值比较，值变更时
            for ( var key in currentCalcConditionObj) {
                for ( var key1 in currentCalcConditionObj[key]) {
                    if (typeof currentCalcConditionObj[key][key1] != "object"
                        && currentCalcConditionObj[key][key1] != calcConditionObjBackUp[key][key1]) {
                        var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key1);
                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                            [labelText, "生成划款指令"]));
                        return true;
                    }
                }
            }
            for ( var key in currentCalcConditionObj) {
                for ( var key1 in currentCalcConditionObj[key].deal) {
                    var currentDealObj = currentCalcConditionObj[key].deal;
                    var backupDealObj = calcConditionObjBackUp[key].deal;
                    if (typeof currentDealObj[key1] != "object"
                        && currentDealObj[key1] != backupDealObj[key1]) {
                        var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key1);
                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                            [labelText, "生成划款指令"]));
                        return true;
                    }
                }
            }
        }
    };


    $("#bussTrdTransferOrder_btn_generate").on("click", function () {generateOrders();});

    function generateOrders(callback) {
            if(stlCommandInput != null) {
                var index = layer.open({
                    type: 1,
                    icon : 16,
                    area: ['200px', '100px'],
                    content: '数据加载中',closeBtn: 0
                });
                var data = stlCommandInput();
                // 保存生成指令参数
                calcConditionObjBackUp = data;
                orderAcctMap = {};
                var result = [];
                Joyin.ajaxJson(JoyinUtil.getRealPath('/common/multiOrder/genOrderCheck'), data, function () {
                    Joyin.ajaxJson(JoyinUtil.getRealPath('/common/multiOrder/generateOrder'), data, function (ret) {
                        if (data.length == ret.data.length) {
                            for (var key in ret.data) {
                                orderAcctMap[ret.data[key].orderId] = {
                                    payAcctNoList: ret.data[key].payAcctNoList,
                                    recAcctNoList: ret.data[key].recAcctNoList
                                }
                            }
                            table.clear(function () {
                                if (checkMyAccount(ret.data)) {
                                    Joyin.error("本方账户为空，请检查账户配置。");
                                    return;
                                }
                                table.addRow(ret.data)
                                layui.layer.close(index);
                                if (callback && typeof callback === 'function') {
                                    callback();
                                }
                            })
                        }
                    });
                });
            }
    }

    var checkMyAccount = function (order) {
        var datas = [];
        if(_.isArray(order)) {
            datas = _.concat(datas, order);
        }
        else {
            datas.push(order);
        }
        $.each(datas, function (i, v) {
            if (v && ((v.ps == '01' && !v.recAcctNo) || (v.ps == '02' && !v.payAcctNo))) {
                return true;
            }
        })
        return false;
    }

    /**
     * 画面加载完成
     */
    $(function () {
        init();
        $('#TrdBondDuration002_btn_save').click(function () {
            saveOrSubmit('save');
        });
        $('#TrdBondDuration002_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
    });
}());

