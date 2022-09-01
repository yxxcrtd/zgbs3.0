(function() {
    var initFlag = true;
    var needInit = true;
    var editCellData;
    //份额总数
    var sumRegistShare;
    
    //面值
    var faceValue = finAssetIntInfoList?finAssetIntInfoList[0].faceValue:null;

    var tradeMarket;
    /**
     * 页面初始化
     */
    var calcConditionObjBackUp;
    repoInfoTableOption = {
        rowno : true,
        searchOnInit : true,
        checkbox : false,
        page : false,
        cols : [
        // 组合
        {
            field : 'portfolioId',
            width : "200",
            title : 'IntDetailDuration.portfolioId',
            sortable : false
        }
        // 组合名称
        , {
            field : 'portfolioName',
            width : "200",
            title : 'IntDetailDuration.portfolioName',
            sortable : false
        }
        // // 权益登记日
        // , {
        //     field : 'registDate',
        //     width : "200",
        //     title : 'IntDetailDuration.registDate',
        //     type : 'date',
        //     sortable : false
        // }
        // 权益登记日份额
        , {
            field : 'registShare',
            width : "200",
            title : 'IntDetailDuration.registShare',
            type : 'money',
            sortable : false
        }
        // 单位分红净值
        , {
            field : 'unitCprice',
            width : "200",
            // edit : true,
            title : 'IntDetailDuration.unitCprice',
            type : 'money', digit: 4,
            sortable : false
        }
        // 分红金额
        , {
            field : 'tradeAmt',
            width : "200",
            edit : true,
            title : 'IntDetailDuration.tradeAmt',
            type : 'money',
            sortable : false
        },
        // 分红份额
        {
            field : 'qty',
            width : "200",
            title : 'IntDetailDuration.qty',
            type : 'money',
            sortable : false
        }
        //面值
        ,{field : 'faceValue', visible:false }
        ,{field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit'],visible:false,
                render: function (data, type, row, meta) {
                    //实际交易组合无子组合交易明细
                    if(row.investYesorno =='Y'){
                        return;
                    }
                    return '<i class="btn layui-icon detail" data-ignore="true" title="明细">&#xe60a;</i>';
                }
    }

    ],
        editCell : function(value, row, field, cell, column) {
            if (field == 'tradeAmt') {
                if (!value) {
                    // 不能为空
                    revertFlag = true;
                    var msg = JoyinUtil.getMessage(JoyinMessage.COMMON_E_0057,
                            JoyinUtil.getLabel('IntDetailDuration.tradeAmt'));
                    Joyin.alert(msg, 2);
                } else if (parseInt(value) <= 0) {
                    // 不能小于零
                    revertFlag = true;
                    var msg = JoyinUtil.getMessage(
                            JoyinMessage.BUSS_COM_E_0004,
                            [ JoyinUtil.getLabel('IntDetailDuration.tradeAmt'),
                                    0 ]);
                    Joyin.alert(msg, 2);
                } else {
                    var intDetailList = repoInfoTable.getAllRows();
                    var sumTradeAmt = 0;
                    var sumQty = 0;
                    var sumRegistShare = 0;
                    // // 汇总
                    $.each(intDetailList, function (i, v) {
                        sumTradeAmt = JoyinMath.doAdd(v.tradeAmt, sumTradeAmt);
                        sumQty = JoyinMath.doAdd(v.qty, sumQty);
                        sumRegistShare = JoyinMath.doAdd(v.registShare, sumRegistShare);
                    });
                    $('#tradeAmt').val(sumTradeAmt);
                }
            }
        },
         beforeEditCell : function(value, field, row, cell, column) {
        	 if(field=='tradeAmt'){
        		 if($("#assetType").val() == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
              		return true;
              	}
        		 return false;
        	 }
         	
         },
        // editCell : function(value, row, field, cell, column) {
        //     var revertFlag = false;
        //     var assetType = $("#assetType").val();
        //     if (field == 'unitCprice') {
        //         if (!value) {
        //             // 不能为空
        //             revertFlag = true;
        //             var msg = JoyinUtil.getMessage(JoyinMessage.COMMON_E_0057,
        //                     JoyinUtil.getLabel('IntDetailDuration.unitCprice'));
        //             Joyin.alert(msg, 2);
        //         } else if (parseInt(value) <= 0) {
        //             // 不能小于零
        //             revertFlag = true;
        //             var msg = JoyinUtil
        //                     .getMessage(
        //                             JoyinMessage.BUSS_COM_E_0004,
        //                             [
        //                                     JoyinUtil
        //                                             .getLabel('IntDetailDuration.unitCprice'),
        //                                     0 ]);
        //             Joyin.alert(msg, 2);
        //         } else {
        //             var tradeAmt = JoyinMath.doMul(value, row.registShare, 2,
        //                     JoyinMath.ROUND_HALF_UP);
        //             row.tradeAmt = tradeAmt;
        //         }
        //     } else if (field == 'tradeAmt') {
        //         if (!value) {
        //             // 不能为空
        //             revertFlag = true;
        //             var msg = JoyinUtil.getMessage(JoyinMessage.COMMON_E_0057,
        //                     JoyinUtil.getLabel('IntDetailDuration.tradeAmt'));
        //             Joyin.alert(msg, 2);
        //         } else if (parseInt(value) <= 0) {
        //             // 不能小于零
        //             revertFlag = true;
        //             var msg = JoyinUtil.getMessage(
        //                     JoyinMessage.BUSS_COM_E_0004,
        //                     [ JoyinUtil.getLabel('IntDetailDuration.tradeAmt'),
        //                             0 ]);
        //             Joyin.alert(msg, 2);
        //         } else {
        //             var unitCprice = JoyinMath.doDiv(value, row.registShare,
        //                     12, JoyinMath.ROUND_HALF_UP);
        //             row.unitCprice = unitCprice;
        //         }
        //     }
        //     if (revertFlag) {
        //         row = editCellData;
        //     } else {
        //         row.useFlg = 'Y';
        //     }
        //     if(assetType == FUND_ASSET_TYPE_F01){
        //     	row.qty=row.tradeAmt;
        //     }
        //     repoInfoTable.renderRow(row.LAY_TABLE_INDEX, row);
        //     finAssetIntInfoList = repoInfoTable.getAllRows();
        //     // resetTableData();
        // },
        done : function() {
            if (pageModel != PAGE_MODEL_REFERENCE){
                if (!(typeof formTodoList === "undefined") && formTodoList) {
                    calcConditionObjBackUp = tableInitData();
                }
                //统计份额总数
                sumRegistShare = 0;
                var allData = repoInfoTable.getAllRows();
                for (var i = 0; i < allData.length; i++) {
                    sumRegistShare = JoyinMath.doAdd(sumRegistShare, allData[i].registShare);
                }

                var tradeAmt = $('#tradeAmt').val();
                if (isNotNull(tradeAmt)&&isNotNull(sumRegistShare)) {
                    var assetType = $("#assetType").val();
                    if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
                        if (faceValue){
                            $("#qty").val(JoyinMath.doDiv(tradeAmt, faceValue)).trigger("blur");
                        }else {
                            $("#qty").val(tradeAmt).trigger("blur");
                        }

                    }
                    else{
                        var unitCprice = JoyinMath.doDiv(tradeAmt, sumRegistShare, 12, JoyinMath.ROUND_HALF_UP);
                        $("#unitCprice").val(unitCprice);
                        $("#unitCprice").trigger("blur");
                    }
                }

                if (!(typeof needInit === "undefined") && needInit) {
                    resetTableData();
                    needInit = false;
                }
            }
        },
        data : []
    }
    if (finAssetIntInfoList && finAssetIntInfoList.length > 0) {
    	$.each(finAssetIntInfoList,function(i,v){
    		v.registDate = new Date(v.registDate).format();
    	})
        repoInfoTableOption.data = repoInfoTableOption.data
                .concat(finAssetIntInfoList);
    }
    /**
     * 定义数据表格
     */
    var repoInfoTable = $("#intDetailTable").joyintable(repoInfoTableOption);
    /**
     * 绑定编辑事件
     */
    repoInfoTable.bind('click', '.detail', function(data, index) {
        var param = {
            "portfolioId":data.portfolioId,
            "finprodMarketId":$('#finprodMarketId').val(),
            "dealDate": new Date(data.registDate).format(),
            "bizCode":$("#totleTradeId").val(),
            "workflowId":typeof workflowId == "undefined" ? '':workflowId,
            "isAdvance":"N",
            "defaultMap":JSON.stringify({
                "unitCprice":$("#unitCprice").val()
            }),
            "preMap":JSON.stringify({
                "registShare":data.registShare,
                "tradeAmt":data.tradeAmt,
                "qty":data.qty}),
            cols:JSON.stringify([
                // 权益登记日份额
                {
                    field : 'registShare',
                    width : "200",
                    title : 'IntDetailDuration.registShare',
                    type : 'money',
                    sortable : false
                },
                // 单位分红净值
                {
                    field : 'unitCprice',
                    width : "200",
                    // edit : true,
                    title : 'IntDetailDuration.unitCprice',
                    type : 'money', digit: 4,
                    sortable : false
                },
                {
                    field : 'tradeAmt',
                    width : "200",
                    // edit : true,
                    title : 'IntDetailDuration.tradeAmt',
                    type : 'money',
                    sortable : false
                },
                // 分红份额
                {
                    field : 'qty',
                    width : "200",
                    title : 'IntDetailDuration.qty',
                    type : 'money',
                    sortable : false
                }
            ])
        };
        Joyin.dialog(JoyinUtil.getLabel('TrdDurationCommon.ChildPortfolioInfo'), JoyinUtil.getRealPath('invest/period/common/ChildPortfolioInfo/init'), '80%', '80%',param);
    });
    /**
     * 定义数据表格
     *
     */
    var option = {
        searchOnInit : true,
        checkbox : false,
        cols : [ {
            field : 'isNeedSend',
            width : 140,
            title : 'BussTrdTransferOrder.isNeedSend',
            edit : true,
            sortable : false,
            editOption : {
                type : 'select',
                multiple : false,
                searchable : false,
                showField : 'text',
                result : YES_NO
            }
        }, {
            field : 'payTypeText',
            width : 130,
            title : 'BussTrdTransferOrder.payType',
            sortable : false,
            sortable : false
        }, {
            field : 'settleDateText',
            width : 130,
            title : 'BussTrdTransferOrder.settleDate',
            sortable : false
        }, {
            field : 'settleAmt',
            width : 130,
            title : 'BussTrdTransferOrder.settleAmt',
            type : 'money',
            sortable : false
        }, {
            field : 'ccyText',
            width : 130,
            title : 'BussTrdTransferOrder.ccy',
            sortable : false
        }, {
            field : 'payAcctNo',
            width : 130,
            title : 'BussTrdTransferOrder.payAcctNo',
            edit : true,
            sortable : false,
            editOption : {
                type : 'select',
                multiple : false,
                searchable : false,
                showField : 'id',
                parentIds : 'orderId_select',
                url : JoyinUtil.getRealPath("/common/multiOrder/select"),
                id : 'payAcctNo',
                postData : function() {
                    return {
                        selectValue : JSON.stringify(orderAcctMap),
                        type : 'pay'
                    };
                }
            }
        }, {
            field : 'payAcctName',
            width : 150,
            title : 'BussTrdTransferOrder.payAcctName',
            sortable : false
        }, {
            field : 'recAcctNo',
            width : 130,
            title : 'BussTrdTransferOrder.recAcctNo',
            edit : true,
            sortable : false,
            editOption : {
                type : 'select',
                multiple : false,
                searchable : false,
                showField : 'id',
                parentIds : 'orderId_select',
                url : JoyinUtil.getRealPath("/common/multiOrder/select"),
                id : 'recAcctNo',
                postData : function() {
                    return {
                        selectValue : JSON.stringify(orderAcctMap),
                        type : 'rec'
                    };
                }
            }
        }, {
            field : 'recAcctName',
            width : 150,
            title : 'BussTrdTransferOrder.recAcctName',
            sortable : false
        }, {
            field : 'purpose',
            width : 130,
            title : 'BussTrdTransferOrder.purpose',
            sortable : false,
            edit : true
        }, {
            field : 'portfolioId',
            width : 130,
            title : 'BussTrdTransferOrder.portfolioId',
            sortable : false
        }, {
            field : 'portfolioName',
            width : 150,
            title : 'BussTrdTransferOrder.portfolioName',
            sortable : false
        }, {
            field : 'orderId',
            visible : true,
            edit : true,
            width : 0,
            editOption : {
                type : 'select',
                id : 'orderId_select',
                childrenIds : 'recAcctNo,payAcctNo'
            }
        }, {
            field : 'ps',
            visible : false
        }, {
            field : 'settleStatus',
            visible : false
        }, {
            field : 'isTotalTrade',
            visible : false
        } ],
        done : function() {
            $("[data-field='orderId']").hide();
        },
        page : false,
        data : [],
        editCell : function(value, row, field, cell, column) {
            if (field == 'payAcctNo') {
                row.payAcctName = $(cell).data('sd')[0].text;
                if (typeof value == "undefined" || value == null || value == "") {
                    row.payAcctName = "";
                }
            } else {
                row.recAcctName = $(cell).data('sd')[0].text;
                if (typeof value == "undefined" || value == null || value == "") {
                    row.recAcctName = "";
                }
            }
            table.renderRow(row.LAY_TABLE_INDEX, row);
        }
    }

    if (bussTrdTransferOrderDtos && bussTrdTransferOrderDtos.length > 0) {
        option.data = option.data.concat(bussTrdTransferOrderDtos);
    }
    var orderAcctMap = {};
    var table = $("#bussTrdTransferOrder_table").joyintable(option);

    var tableInitData = function() {
    	var bonusMode = $("#bonusMode").val();
    	if(bonusMode == BONUS_MODE_01){
    		if (!$("#arriveDate").val()) {
                Joyin.error("请输入分红到账日");
                return false;
            }
    	}
    	else if(bonusMode == BONUS_MODE_02){
    		if(!$("#reInvesDate").val()){
        	    Joyin.error("请输入红利再投日");
                return false;
        	}
    	}
        var type = $("#type").val();
        var PS = PS_T13;
        if(type == "00"){
        	if($("#assetType").val()==FUND_ASSET_TYPE_F01&&bonusMode == BONUS_MODE_02 && !$("#reInvesDate").val()){
        	    Joyin.error("请输入红利再投日");
                return false;
        	}
        	if ($("#assetType").val()==FUND_ASSET_TYPE_F02 && !$("#exRightsDate").val()) {
                Joyin.error("请输入除权日");
                return false;
            }
        }
        if(type == "04"){
        	if(!$("#reInvesDate").val()){
        	    Joyin.error("请输入红利再投日");
                return false;
        	}
        	PS = PS_T12;
        }
        else if(type == "03"||type == "06"){
        	if(!$("#arriveDate").val()){
        	    Joyin.error("请输入分红到账日");
                return false;
        	}
        	PS = PS_T13;
        }
        var tradeDate;
        if($("#assetType").val()==FUND_ASSET_TYPE_F01 && bonusMode == BONUS_MODE_02){
        	tradeDate = $("#reInvesDate").val();
        }
        else {
//        	非货币红利再投
        	tradeDate = (type == "02" || type=="05") ? $("#exRightsDate").val() :
                (bonusMode == BONUS_MODE_02 ? $("#reInvesDate").val() : $("#arriveDate").val());
        }
        var finAssetIntInfos = repoInfoTable.getAllRows();
        var dtos = [];
        for (var i = 0; i < finAssetIntInfos.length; i++) {
        	var deal = {};
            deal.ccy = $("#ccy").val();
            deal.tradeAmt = finAssetIntInfos[i].tradeAmt;
            deal.ps = PS;
            deal.tradeDate = tradeDate;
            deal.finprodId = $("#finprodId").val();
            deal.finprodType2 = FINPRODTYPE_DEFAULT;
            // 道交易 新增 强成西 2019/03/26 start
            deal.chlId = $("#chlId").val()
            // 道交易 新增 强成西 2019/03/26 end
            deal.secManageAcctId = $("#secManageAcctId").val();
            dtos.push({
                'isTotalTrade' : YES_OR_NO_N,
                'bankLendingType' : null,
                'portfolioId' : finAssetIntInfos[i].portfolioId,
                'deal' : deal
            })
        }
        return dtos;
    }
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

    // 检查生成指令参数是否发生变化
    var isCalcConditionChangedTwo = function(bussTrdTransferOrders) {
        // 参照模式，不做check
        if (!$("#bussTrdTransferOrder_div").is(":visible")) {
            return false;
        }
        // if (calcConditionObjBackUp == null) {
        var calcConditionObjBackUp = stlCommandInput();
        for (var i = 0; i < bussTrdTransferOrders.length; i++) {
            var deal = {};
            deal.ccy = $("#ccy").val();
            deal.tradeAmt = parseFloat(bussTrdTransferOrders[i].settleAmt).toFixed(2);
            deal.ps = calcConditionObjBackUp[i].deal.ps;
            deal.tradeDate = bussTrdTransferOrders[i].settleDateText;
            deal.finprodId = $("#finprodId").val();
            deal.finprodType2 = FINPRODTYPE_DEFAULT;
            // 道交易 新增 强成西 2019/03/26 start
            deal.chlId = $("#chlId").val()
            // 道交易 新增 强成西 2019/03/26 end
            deal.secManageAcctId = $("#secManageAcctId").val();
            calcConditionObjBackUp[i].deal = deal;
        }
        // }
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
                    if ( typeof currentDealObj[key1] != "object"
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

    var generateOrders = function (callback) {
        if(stlCommandInput != null) {
            var data = stlCommandInput();
            if (!data) {
                return;
            }
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
                            table.addRow(ret.data)
                        })
                        if (callback && typeof callback === 'function') {
                            callback();
                        }
                    }
                }, false);
            }, false);
        }
    }
    $("#bussTrdTransferOrder_btn_generate").on("click", function () {generateOrders();});
    /**
     * 利息明细初始化
     */
    var dealIntDetail = function() {
        var finprodMarketId = $('#finprodMarketId').val();
        var registDate = $('#registDate').val();
        if (finprodMarketId && registDate) {
            var param = {
                finprodMarketId : finprodMarketId,
                registDate : registDate
                // 道交易 新增 强成西 2019/03/26 start
                , chlId: $("#chlId").val()
                // 道交易 新增 强成西 2019/03/26 end
                , secAcctId: $("#secManageAcctId").val()
            };
            Joyin.ajaxSync(JoyinUtil.getRealPath('/invest/period/trdfundduration/TrdFundDuration002/findAssetIntInfo'), param, function(result) {
                                if (result.data) {
                                    finAssetIntInfoList = result.data;
                                    /* 计算 数量总数 */
                                    sumRegistShare = 0;
                                    var trmFundType = $("#trmFundType").val();
                                    for (var i = 0; i < finAssetIntInfoList.length; i++) {
                                        sumRegistShare = JoyinMath.doAdd(sumRegistShare, finAssetIntInfoList[i].registShare);
                                        /*面值*/
                                        faceValue = finAssetIntInfoList[i].faceValue;
                                        if($('#assetType').val() == FUND_ASSET_TYPE_F01&&$('#bonusMode').val() == BONUS_MODE_02){
                                            if(trmFundType == '05'){
                                                faceValue = '0.01';
                                            }else  if(trmFundType == '01'){
                                                faceValue = '100';
                                            }
                                        }
                                    }
                                    /*add by xr 加载表格前先计算净值*/
                                    var tradeAmt = $('#tradeAmt').val();
                                    if($("#assetType").val() == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
                                    	return;
                                  	}else{
                                  		var unitCprice = JoyinMath.doDiv(tradeAmt, sumRegistShare, 12, JoyinMath.ROUND_HALF_UP);
                                        $("#unitCprice").val(unitCprice);
                                        $("#unitCprice").trigger("blur");
                                  	}
                                }
                            });
        } else {
            finAssetIntInfoList = [];
        }
        resetTableData();
    }
    /**
     * 表格联动
     */
    var resetTableData = function() {
        var type = $("#type").val();
        // 利息明细根据基础数据联动
        var finAssetIntInfos = finAssetIntInfoList;
        if(!finAssetIntInfos){
        	return;
        }
        //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
       var assetType = $("#assetType").val();
        if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
            var itemArr = ['qty','tradeAmt'];
        } else if( type=="01" || type == "04"){ // 红利再投确认
            var itemArr = ['qty','tradeAmt'];
        }else{
            var itemArr = ['tradeAmt'];
        }
        //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
//      所有的钱
        var itemObj = {};
//      已分配的钱
        var sumObj = {};
//      初始化所有的钱和已分配的钱
        $.each(itemArr,function(index,val){
            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
            if ((assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02)
                || ( type=="01" || type == "04") // 红利再投确认
                 ) {
                if (faceValue && val == 'qty') {
                    var value = $('#' + val).val();
                    var tradeAmt = $('#tradeAmt').val();
                    //   modify by caojin 2020/6/2  start
                    if (value) {
                        itemObj[val] = value || 0;
                    } else {
                        itemObj[val] = JoyinMath.doDiv(tradeAmt, faceValue) || 0;
                    }
                    //   modify by caojin 2020/6/2  end

                } else {
                    itemObj[val] = $('#' + val).val() || 0;
                }
            }
            else{
                itemObj[val] = $('#' + val).val() || 0;
            }
            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
            sumObj[val] = 0;
        })
//      分配
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
            if((assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02)
                || ( type=="01" || type == "04") // 红利再投确认
            ){

            }else{
                finAssetIntInfos[i][qty]=" ";
            }
            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
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
                var prop = JoyinMath.doDiv(finAssetIntInfos[i].registShare, sumRegistShare, 12, JoyinMath.ROUND_HALF_UP);
//        		根据占比处理所有的钱
                $.each(itemArr,function(index,val){
//        			计算当前需要的钱
                    var thisNum = JoyinMath.doMul(itemObj[val], prop, 2, JoyinMath.ROUND_HALF_UP);
//        			处理已分配的钱
                    sumObj[val] = JoyinMath.doAdd(sumObj[val], thisNum);
//        			把钱放入对应数据
                    finAssetIntInfos[i][val] = thisNum;
                })
            }
        }
        // // 单位分红净值
        var unitCprice = $("#unitCprice").val();
        var tradeAmt = $("#tradeAmt").val();
        var assetType = $("#assetType").val();
        // // 分配处理每一行
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            if (finAssetIntInfos[i].useFlg
                    && finAssetIntInfos[i].useFlg === 'Y') {
                continue;
            }
            if (unitCprice) {
                finAssetIntInfos[i].unitCprice = unitCprice;
                // 分红金额
                var tradeAmt = JoyinMath.doMul(unitCprice,
                        finAssetIntInfos[i].registShare, 2,
                        JoyinMath.ROUND_HALF_UP);
//                finAssetIntInfos[i].tradeAmt = tradeAmt;
                if (FUND_ASSET_TYPE_F01 == assetType && $('#bonusMode').val() == BONUS_MODE_02) {
                    finAssetIntInfos[i].qty = tradeAmt;
                }
            }
        }
        if(repoInfoTable.table){
            repoInfoTable.clear(function() {
                var unitCprice = $("#unitCprice").val();
                $.each(finAssetIntInfos, function(i, v) {
                    v.unitCprice = unitCprice;
                    if($('#trmFundType').val()&&($('#trmFundType').val()=='05'||$('#trmFundType').val()=='01')){
                        v.qty = JoyinMath.doDiv(v.tradeAmt,unitCprice);
                    }
                    repoInfoTable.addRow(v);
                });
            });
        }
    }

    function saveOrSubmit(operateType) {
        if (GEN_PAY_ORDERS_AUTO == '01') {
            generateOrders(function () {
                realSaveOrSubmit(operateType);
            });
        }
        else {
            // 检查生成划款指令参数是否发生变化
            if (!$("#bussTrdTransferOrder_div").is(":visible")) {
                generateOrders(function () {
                    realSaveOrSubmit(operateType)
                });
            }
            else {
                realSaveOrSubmit(operateType);
            }
        }
    }

    function realSaveOrSubmit(operateType) {
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#informationForm', true);
        // var intDetailList = repoInfoTable.getAllRows();
        // var sumTradeAmt = 0;
        // var sumQty = 0;
        // var sumRegistShare = 0;
        // // // 汇总
        // $.each(intDetailList, function(i, v) {
        //     sumTradeAmt = JoyinMath.doAdd(v.tradeAmt, sumTradeAmt);
        //     sumQty = JoyinMath.doAdd(v.qty, sumQty);
        //     sumRegistShare = JoyinMath.doAdd(v.registShare, sumRegistShare);
        // });
        // object.tradeAmt = sumTradeAmt;
        // object.qty = sumQty;
        object.registShare = sumRegistShare;
        object.operateType = operateType;
        object.tradeMarket = tradeMarket;
        //        新建打开为00
        if (object.type == '00') {
            if (object.assetType == FUND_ASSET_TYPE_F01) {
                //            	现金分红为05（分红除权），红利再投为01
                object.type = object.bonusMode == BONUS_MODE_01?'05':'01';
            }
            else {
                // 非货币除权登记
                object.type = '02';
            }
            // 其他场景已近在001打开画面时处理
        }
        if (FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo();
        }
        //分红明细不能为空
        if(object.finAssetIntInfoList.length == 0){
            Joyin.error("分红明细信息不能为空！");
            return;
        }
        if(isCalcConditionChangedTwo(object.bussTrdTransferOrder)){
            return;
        }

        Joyin.ajaxJson(basePath + 'invest/period/trdfundduration/TrdFundDuration002/check', object, function(result) {
            Joyin.confirm(result.data,function() {
                // 非货币基金的现金分红，红利再投
                if (object.type == '03'||object.type == '06') {
                    var elementId = 'TrdFundDuration002_btn_total';
                }
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/period/trdfundduration/TrdFundDuration002/save', object, isNotNull(elementId) ? elementId
                        : "TrdFundDuration002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'invest/period/trdfundduration/TrdFundDuration002/save', object, isNotNull(elementId) ? elementId
                        : "TrdFundDuration002_btn_submit");
                }
            });
        });
    }

    var triValueChange = function(id, data) {
        if (data) {
            $('#' + id).val(data).trigger("change").trigger('blur');
        } else if (!initFlag) {
            $('#' + id).val('').trigger("change");
        } else {
            $('#' + id).trigger("change").trigger('blur');
        }
    };
    // 判空处理
    var isNotNull = function(value) {
        return value && value != "";
    };
    var bonusModeChange = function() {
    	var assetType = $('#assetType').val();
    	var bonusMode = $('#bonusMode').val();
        if (assetType == FUND_ASSET_TYPE_F01 && bonusMode == BONUS_MODE_02) {
            // 权益登记日 除权日 权益登记日份额 单位分红净值不显示
//            $('.currency').hide();
            if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
                $("#exRightsDate").val(sysDate).trigger("change");
                $("#registDate").val(sysDate).trigger("change");
                $("#exRightsDate").attr("disabled",true);
                $("#registDate").attr("disabled",true);
            }
//            $('#unitCprice').attr("disabled","true");
//        	triValueChange('unitCprice',1);
            //triValueChange('unitCprice');
            if(assetType == FUND_ASSET_TYPE_F01){
                var trmFundType = $("#trmFundType").val();
                if($('#bonusMode').val() == BONUS_MODE_02){
                    if(trmFundType == '05'){
                        triValueChange("unitCprice",'0.01');
                    }else  if(trmFundType == '01'){
                        triValueChange("unitCprice",'100');
                    }else{
                        triValueChange("unitCprice");
                    }
                }else{
                    triValueChange("unitCprice");
                }
            }
            if(pageModel!=PAGE_MODEL_NEW){
                triValueChange('exRightsDate');
            }
        }
        // 资产类型为非货币
        else if(assetType == FUND_ASSET_TYPE_F01 && bonusMode == BONUS_MODE_01){
        	$('.currency').show();
            triValueChange('tradeAmt');
            if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
                $("#exRightsDate").val(sysDate).trigger("change");
                $("#registDate").val(sysDate).trigger("change");
                $("#exRightsDate").attr("disabled",true);
                $("#registDate").attr("disabled",true);
            }
        }
        else if(assetType == FUND_ASSET_TYPE_F02){
        	triValueChange('unitCprice');
            // 展示联动区域
            $('.currency').show();
            if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
                $("#exRightsDate").val('').trigger("change");
                $("#registDate").val('').trigger("change");
                $("#exRightsDate").attr("disabled",false);
                $("#registDate").attr("disabled",false);
            }
        }
        if(isFromCheck&&isFromCheck=='true'){
            $("#exRightsDate").attr("disabled",true);
            $("#registDate").attr("disabled",true);
        }
     // 判断分红类型
        if (bonusMode == BONUS_MODE_01) {
            $('#reInvesDate').val('');
            $('.reInvesDate').hide();
            $('.arriveDate').show();
        } else if (bonusMode == BONUS_MODE_02) {
            $('#arriveDate').val('')
            $('.arriveDate').hide();
            $('.reInvesDate').show();
        }
    }
    var init = function() {
        // 资产类型
//        assetTypeChagne();
//        if(pageModel != PAGE_MODEL_REFERENCE){
    	    bonusModeChange();
//    	}
        var assetType = $("#assetType").val();
        if(assetType == FUND_ASSET_TYPE_F01){
            $(".unitCprice").hide();
        }else{
            $(".unitCprice").show();
        }
        initFlag = false;
        if($("#type").val()!="03"&&$("#type").val()!="06"){
        	$("#bussTrdTransferOrder_div").hide();
        }
    };
    /**
     * 画面加载完成
     */
    $(function() {
        //通过来账分拣初始化的页面交易金额不可改变
        if(pageModel==PAGE_MODEL_NEW&&isFromCheck&&isFromCheck=='true'){
            $("#tradeAmt").attr("disabled",true);
        }

        // 1.资产代码下拉框选择后，联动带出 币种、资产类型、资产名称、金融产品代码、发行人
        $('#finprodMarketId')
                .change(
                        function() {
                            var finprodMarketId = $('#finprodMarketId').val();
                            var data = $("#finprodMarketId").find("option:selected").data("data");
                            var payType = data.object ? data.object || '' : '';
                            if (payType) {
                                $("#bonusMode").val(payType).prop('disabled', true).trigger("change");
                            }
                            else {
                                if(pageModel != PAGE_MODEL_REFERENCE){
                                    $("#bonusMode").prop('disabled', false);
                                }
                                triValueChange("bonusMode");
                            }
                            tradeMarket = ''
                            if (finprodMarketId || finprodMarketId != '') {
                                Joyin.ajax(basePath + "invest/period/trdfundduration/TrdFundDuration002/finprodMarketIdLink",
                                    {finprodMarketId: finprodMarketId }, function (result) {
                                        if (result.data) {
                                            tradeMarket = result.data.TRADE_MARKET;
                                            triValueChange('trmFundType', result.data.TRM_FUND_TYPE);
                                            triValueChange('finprodMarketName', result.data.FINPRODMARKETNAME);
                                            triValueChange('finprodId', result.data.FINPRODID);
                                            triValueChange('issuer', result.data.ISSUER);
                                            triValueChange('ccy', result.data.CCY);
                                            triValueChange('assetType', result.data.ASSETTYPE);
                                            bonusModeChange();
                                        }
                                    });
                            } else {
                                triValueChange('finprodMarketName');
                                triValueChange('finprodId');
                                triValueChange('issuer');
                                triValueChange('ccy');
                                triValueChange('assetType');
                            }
                            if(pageModel != PAGE_MODEL_REFERENCE){
                                triValueChange('qty');
                            }
                            
                            dealIntDetail();
                        });
        // 5.基金类型为货币基金时，分行类型 默认为红利再投，不可改
       $('#assetType').change(function() {
           var assetType = $("#assetType").val();
           if(assetType == FUND_ASSET_TYPE_F01){
               $(".unitCprice").hide();
               var trmFundType = $("#trmFundType").val();
               if($('#bonusMode').val() == BONUS_MODE_02){
                   if(trmFundType == '05'){
                       triValueChange("unitCprice",'0.01');
                   }else  if(trmFundType == '01'){
                       triValueChange("unitCprice",'100');
                   }else{
                       triValueChange("unitCprice");
                   }
               }else{
                   triValueChange("unitCprice");
               }
           }else{
               $(".unitCprice").show();
               triValueChange("unitCprice");
           }
       });

        // 道交易 新增 强成西 2019/03/26 start
       $('#chlId').change(function() {
           dealIntDetail();
       });
        // 道交易 新增 强成西 2019/03/26 end

        // 6.分红类型为现金分红时
        // 红利再投日不显示
        // 7.分红类型为红利再投时
        // 分红到账日不显示
        $('#bonusMode').change(bonusModeChange);
        // 单位分红净值联动
        $('#unitCprice').change(function() {
            var unitCprice = $('#unitCprice').val();
            if (isNotNull(unitCprice)&&isNotNull(sumRegistShare)) {
                var tradeAmt = JoyinMath.doMul(sumRegistShare, unitCprice, 2, JoyinMath.ROUND_HALF_UP);
                $("#tradeAmt").val(tradeAmt);
                var assetType = $("#assetType").val();
                if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
                    $("#qty").val(tradeAmt).trigger("blur");
                }
                resetTableData();
            }
        });
        // 交易金额联动
        $('body').on('change', '#tradeAmt', function() {
            var tradeAmt = $('#tradeAmt').val();
            if (isNotNull(tradeAmt)&&isNotNull(sumRegistShare)) {
                var assetType = $("#assetType").val();
                if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
                    if(faceValue){
                        $("#qty").val(JoyinMath.doDiv(tradeAmt, faceValue)).trigger("blur");
                    }else{
                        $("#qty").val(tradeAmt).trigger("blur");
                    }
                }
                else{
                    var unitCprice = JoyinMath.doDiv(tradeAmt, sumRegistShare, 12, JoyinMath.ROUND_HALF_UP);
                    $("#unitCprice").val(unitCprice);
                    $("#unitCprice").trigger("blur");
                }
                resetTableData();
            }
        });
        // 份额联动
        $('#qty').change(function() {
            var qty = $('#qty').val();
            if (isNotNull(qty)) {
                resetTableData();
            }
        });
        $('#chlId').change(function(e, 其他事件触发的) {
            var chlId = $('#chlId').val();
            if (chlId) {
                $("#secManageAcctId").val("").trigger("change", true);
            }
            if (!其他事件触发的) {
                dealIntDetail();
            }
        });
        $('#secManageAcctId').change(function(e, 其他事件触发的) {
            var secManageAcctId = $('#secManageAcctId').val();
            if (secManageAcctId) {
                $("#chlId").val("").trigger("change", true);
            }
            if (!其他事件触发的) {
                dealIntDetail();
            }
        });

        // 权益登记日联动
        $('#registDate').change(function() {
            // var finprodMarketId = $('#finprodMarketId').val();
            var registDate = $('#registDate').val();
            if (isNotNull(registDate)) {
                dealIntDetail();
            }
        });
        $('#TrdFundDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdFundDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        //add by jiangpeng on 20210522 start TISHKBS-1803
        $("#arriveDate").change(function () {
            if(isTodo){
                var index = layer.open({
                    type: 1,
                    icon : 16,
                    area: ['200px', '100px'],
                    content: '数据加载中',closeBtn: 0
                });
                var orderTabel = table.getAllRows();
                for (var i = 0; i < orderTabel.length; i++) {
                    orderTabel[i].settleDateText = $("#arriveDate").val();
                    orderTabel[i].settleDate = $("#arriveDate").val();
                }
                table.clear(function () {
                    layui.layer.close(index);
                    table.addRow(orderTabel)
                });

            }
        });
        //add by jiangpeng on 20210522 end TISHKBS-1803
        setTimeout(function(){init();},0);
    });
}());
