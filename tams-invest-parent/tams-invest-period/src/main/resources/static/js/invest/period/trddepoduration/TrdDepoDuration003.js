/**
 * 存款存续期
 */
$(function() {
	var needInit = true;
    var uploadFileList =[];
	/**
     * 页面初始化
     */
    var calcConditionObjBackUp;
    repoInfoTableOption = {
        rowno: true,
        searchOnInit: true,
        checkbox: false,
        page: false,
        cols: [
            //组合
            {field: 'portfolioId', width: "200", title: 'IntDetailDuration.portfolioId', sortable:false}
            //付息周期
            , {field: 'calcRuleString', width: "200", title: 'IntDetailDuration.calcRuleString', sortable:false}
            //持有金额
            , {field: 'remainPrinAmt', width: "200", title: 'IntDetailDuration.holdMoney', type: 'money', sortable:false}
            //还本金额
            , {field: 'returnAmount', width: "200", title: 'IntDetailDuration.returnAmount', type: 'money', sortable:false}
            //计息开始日
            , {field: 'vDate', width: "200", title: 'IntDetailDuration.vDate', sortable:false}
            //计息结束日
            , {field: 'mDate', width: "200", title: 'IntDetailDuration.mDate', sortable:false}
            //利率
            , {field: 'loadRate', width: "200", title: '利率（%）', type: 'money', sortable:false}
            //理论应收利息
            , {field: 'theoryInt', width: "200", title: 'IntDetailDuration.intAmtTheory', type: 'money', sortable:false}
            //实收利息
            , {field: 'intAmt',  width: "200",title: 'IntDetailDuration.intAmt', type: 'money', sortable:false},
            // add by renping 2019-10-15 添加通道费 start
            {field: 'channelFee',  width: "200",title: function(){ return JoyinUtil.getLabel("invest.channelId")+JoyinUtil.getLabel("TrdDepoDuration003.channelFee");}(), type: 'money', sortable:false,visible:CHANNEL},
            // add by renping 2019-10-15 添加通道费 end
            {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['edit'],
                render: function (data, type, row, meta) {
                    //实际交易组合无子组合交易明细
                    if(row.investYesorno =='Y'){
                        return;
                    }
                    return '<i class="btn layui-icon detail" data-ignore="true" title="明细">&#xe60a;</i>';
                }
            }
        ],
        done : function(){
            if (formTodoList) {
                calcConditionObjBackUp = tableInitData();
            }
            if(needInit && pageModel == PAGE_MODEL_NEW){
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
//    
//    var deal = {};
//    deal.ccy = $("#ccy").val();
//    deal.tradeAmt = $("#tradeAmt").val();
//    // deal.settleDate = $("#settleDate").val();
//    deal.ps = PS_DEFAULT;
//    deal.tradeDate = $("#tradeDate").val();
//    deal.finprodType2 = FINPRODTYPE_DEFAULT;
//    return {
//        'isTotalTrade' : YES_OR_NO_Y,
//        'bankLendingType' : null,
//        'portfolioId' : $("#portfolioId").val(),
//        'deal' : eval(deal)
//    };

    var repoInfoTable = $("#intDetailTable").joyintable(repoInfoTableOption);
    /**
     * 绑定编辑事件
     */
    repoInfoTable.bind('click', '.detail', function(data, index) {
        var param = {
            "portfolioId":data.portfolioId,
            "finprodMarketId":$('#finprodMarketId').val(),
            "dealDate": new Date(data.mDate).format(),
            'bizCode':$("#busiCode").val(),
            "workflowId":typeof workflowId == "undefined" ? '':workflowId,
            "defaultMap":JSON.stringify({
                "calcRuleString":data.calcRuleString,
                "vDate":new Date(data.vDate).format(),
                "mDate":new Date(data.mDate).format(),
                "loadRate":data.loadRate,
                "calcRule":data.calcRule,
            }),
            "preMap":JSON.stringify({
                "remainPrinAmt":data.remainPrinAmt,
                "returnAmount":data.returnAmount,
                "theoryInt":data.theoryInt,
                "intAmt":data.intAmt
                // add by renping 2019-10-15 添加通道费 start
                ,"channelFee":data.channelFee
                // add by renping 2019-10-15 添加通道费 end
            }),
            cols:JSON.stringify([
                //付息周期
                 {field: 'calcRuleString', width: "200", title: 'IntDetailDuration.calcRuleString', sortable:false}
                //持有金额
                , {field: 'remainPrinAmt', width: "200", title: 'IntDetailDuration.holdMoney', type: 'money', sortable:false}
                //还本金额
                , {field: 'returnAmount', width: "200", title: 'IntDetailDuration.returnAmount', type: 'money', sortable:false}
                //计息开始日
                , {field: 'vDate', width: "200", title: 'IntDetailDuration.vDate', sortable:false}
                //计息结束日
                , {field: 'mDate', width: "200", title: 'IntDetailDuration.mDate', sortable:false}
                //利率
                , {field: 'loadRate', width: "200", title: '利率（%）', type: 'money', sortable:false}
                //理论应收利息
                , {field: 'theoryInt', width: "200", title: 'IntDetailDuration.intAmtTheory', type: 'money', sortable:false}
                //实收利息
                , {field: 'intAmt',  width: "200",title: 'IntDetailDuration.intAmt', type: 'money', sortable:false}
                // add by renping 2019-10-15 添加通道费 start
                , {field: 'channelFee',  width: "200",title: function(){return JoyinUtil.getLabel("invest.channelId")+JoyinUtil.getLabel("TrdDepoDuration003.channelFee");}(), type: 'money', sortable:false,visible:CHANNEL}
                // add by renping 2019-10-15 添加通道费 end
            ])
        };
        Joyin.dialog(JoyinUtil.getLabel('TrdDurationCommon.ChildPortfolioInfo'), JoyinUtil.getRealPath('invest/period/common/ChildPortfolioInfo/init'), '80%', '80%',param);
    });
    var tableInitData = function () {
        var finAssetIntInfos = repoInfoTable.getAllRows();
        var dtos = [];
        var deal = {
        ccy:$("#ccy").val(),
        ps:PS_DEFAULT,
        finprodType2:FINPRODTYPE_DEFAULT,
        tradeDate:$("#tradeDate").val()
        };
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            if (finAssetIntInfos[i].tradeAmt && finAssetIntInfos[i].tradeAmt > 0) {
                var tmpDeal = _.cloneDeep(deal);
                tmpDeal.tradeAmt = finAssetIntInfos[i].tradeAmt;
                // 通道存续期 qiangchengxi start
                tmpDeal.chlId = $("#chlId").val();
                // 通道存续期 qiangchengxi end
                dtos.push({
                    'isTotalTrade': YES_OR_NO_Y
                    , 'bankLendingType': null
                    , 'portfolioId': finAssetIntInfos[i].portfolioId
                    , 'deal':tmpDeal
                })
            }
        }
        return dtos;
    }
    /**
     * 利用占比计算所有的金额
     */
    var resetTableData = function () {
        // 按占比计算还本金额、理论本息、实收本息
        var finAssetIntInfos = finAssetIntInfoList;
        var sumAmount = 0;
        for (var i = 0; i < finAssetIntInfos.length; i++) {
        	if(!finAssetIntInfos[i].amount){
        		continue;
        	}
            sumAmount = JoyinMath.doAdd(sumAmount, finAssetIntInfos[i].amount);
        }
//      持有金额、理论应收利息、理论还本金额？、理论本息总额、实收利息总额、实收本息总额
        // add by renping 2019-10-15 添加通道费 start
        var itemArr = ['remainPrinAmt','intAmtTheory','returnAmount','theoryInt','fpriceAmt','intAmt','tradeAmt','channelFee','tradeAmtReal'];
        // add by renping 2019-10-15 添加通道费 end
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
        		var prop = JoyinMath.doDiv(finAssetIntInfos[i].amount, sumAmount, 12, JoyinMath.ROUND_HALF_UP);
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
        if(COLLECT_TYPE == COLLECT_TYPE_01) {//回款前扣除
            $.each(finAssetIntInfos, function(i, val){
                val.tradeAmt = JoyinMath.doMinus( val.tradeAmtReal , val.channelFee);
            });
        }
       
        repoInfoTable.clear(function () {
        	var loadRate = $("#loadRate").val();
            $.each(finAssetIntInfos, function (i, v) {
            	v.loadRate = loadRate;
                repoInfoTable.addRow(v);
            })
        });
    }
//  2.0划款指令
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
                editOption: {type: 'select', multiple: false, searchable: false, showField: 'text', result: YES_OR_NO}
            }
            , {field:'payTypeText', width:130, title: 'BussTrdTransferOrder.payType', sortable:false, sortable:false}
            , {field:'settleDateText', width:130, title: 'BussTrdTransferOrder.settleDate', sortable:false}
            , {field: 'settleAmt', width: 130, title: 'BussTrdTransferOrder.settleAmt', type: 'money', sortable:false}
            , {field: 'ccyText', width: 130, title: 'BussTrdTransferOrder.ccy', sortable:false}
            , {
                field: 'payAcctNo', width: 130, title: 'BussTrdTransferOrder.payAcctNo', edit: true, sortable:false,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'id', parentIds:'orderId_select',
                    url: JoyinUtil.getRealPath("/common/multiOrder/select"), id: 'payAcctNo', postData:function () {
                        return {selectValue: JSON.stringify(orderAcctMap), type: 'pay'};
                    }
                }
            }
            , {field: 'payAcctName', width: 150, title: 'BussTrdTransferOrder.payAcctName', sortable:false}
            , {
                field: 'recAcctNo', width: 130, title: 'BussTrdTransferOrder.recAcctNo', edit: true, sortable:false,
                editOption: {
                    type: 'select', multiple: false, searchable: true, showField: 'id', parentIds:'orderId_select',
                    url: JoyinUtil.getRealPath("/common/multiOrder/select"),id: 'recAcctNo',  postData:function () {
                        return {selectValue: JSON.stringify(orderAcctMap), type: 'rec'};
                    }
                }
            }
            , {field: 'recAcctName', width: 150, title: 'BussTrdTransferOrder.recAcctName', sortable:false}
            ,{field:'purpose', width:130, title: 'BussTrdTransferOrder.purpose', sortable:false, edit:true}
            , {field: 'portfolioId', width: 130, title: 'BussTrdTransferOrder.portfolioId', sortable:false}
            ,{field:'portfolioName', width:150, title: 'BussTrdTransferOrder.portfolioName', sortable:false}
            , {field: 'inOutBank', width: 130, title: 'BussTrdTransferOrder.inOutBank', sortable: false}
            , {field: 'remark', width: 130, title: 'BussTrdTransferOrder.remark', sortable: false, edit:true}
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
            if (typeof value != "undefined" && value && field == 'payAcctNo') {
                // 选择收款账户带出行内/行外、网点
                Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {
                    ccy: $('#ccy').val(),
                    acctNo: value
                }, function (result) {
                    if (!result) {
                        Joyin.error("该币种下，无此账户。");
                        row.recAcctNo = null;
                        table.renderRow(row.LAY_TABLE_INDEX, row);
                        return;
                    }
                    var assetSource = JSON.parse(ASSET_SOURCE);
                    $.each(assetSource, function (i, v) {
                        if (v.id == result.inOutBank) {
                            row.inOutBank = v.text;
                        }
                    });
                })
            }
            table.renderRow(row.LAY_TABLE_INDEX, row);
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

    $("#bussTrdTransferOrder_btn_generate").on("click", function(){generateOrders();});

    var generateOrders = function (callback) {
        var tradeDate = $('#tradeDate').val();
        if(!tradeDate){
             Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0038",
                JoyinUtil.getLabel("TrdDepoDuration001.fundAtDate")));
            return false;
        }
        var tradeAmt = $('#tradeAmt').val();
        if(!tradeAmt){
             Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0038",
                JoyinUtil.getLabel("TrdDepoDuration001.tradeAmt")));
            return false;
        }
        if(stlCommandInput != null) {
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

    function formatDateByMis(mis) {
        if (mis) {
            var tempDate = new Date(mis);
            return tempDate.format();
        } else {
            return null;
        }
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
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#informationForm', true);
        object.operateType = operateType;
        object.opType = opType;
        if(FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo()
        }
        Joyin
                .ajaxJson(
                        basePath
                                + 'invest/period/trddepoduration/TrdDepoDuration002/check',
                        object,
                        function(result) {
                            Joyin
                                    .confirm(
                                            result.data,
                                            function() {
                                                if (operateType === 'save') {
                                                    WorkFlow
                                                            .doSaveList(
                                                                    basePath
                                                                            + 'invest/period/trddepoduration/TrdDepoDuration002/save',
                                                                    object,
                                                                    "TrdDepoDuration003_btn_save","TrdDepoDuration003");
                                                } else {
                                                    WorkFlow
                                                            .doSubmitList(
                                                                    basePath
                                                                            + 'invest/period/trddepoduration/TrdDepoDuration002/save',
                                                                    object,
                                                                    "TrdDepoDuration003_btn_submit","TrdDepoDuration003");
                                                }
                                            });
                        });
    }
//    window.componentLoadCallback = function(isDone, pagePath, params) {
//        if (isDone) {
//            window.BussTrdTransferOrder['bussTrdTransferOrder']
//                    .setStlCommandInput(function() {
//                        var deal = {};
//                        deal.ccy = $("#ccy").val();
//                        deal.tradeAmt = $("#tradeAmt").val();
//                        // deal.settleDate = $("#settleDate").val();
//                        deal.ps = PS_DEFAULT;
//                        deal.tradeDate = $("#tradeDate").val();
//                        deal.finprodType2 = FINPRODTYPE_DEFAULT;
//                        return {
//                            'isTotalTrade' : YES_OR_NO_Y,
//                            'bankLendingType' : null,
//                            'portfolioId' : $("#portfolioId").val(),
//                            'deal' : eval(deal)
//                        };
//                    });
//        }
//    }
    $(function() {
    	if(CHANNEL){
    		$('#channelFee').prop('disabled',false);
    	}
    	$('#intAmt').change(function(){
    		var intAmt = $('#intAmt').val();
    		if(intAmt != ''&& intAmt != null){
                var returnAmount = $('#returnAmount').val();
        // add by renping 2019-10-15 添加通道费 start
                $("#tradeAmtReal").val(JoyinMath.doAdd(intAmt,returnAmount)).trigger("blur");
                $("#tradeAmtReal").trigger("change");
    			resetTableData();
    		}
    	});
    	$('#tradeAmtReal').change(function(){
    		var tradeAmtReal = $('#tradeAmtReal').val();
    		if(tradeAmtReal != '' && tradeAmtReal != null){
                var returnAmount = $('#returnAmount').val();
                $("#intAmt").val(JoyinMath.doMinus(tradeAmtReal,returnAmount,2)).trigger("blur");
                if(IS_CAL_LOADRATE&&IS_CAL_LOADRATE=='01'){
                	if(COLLECT_TYPE == COLLECT_TYPE_01){//回款前扣除
                    	var channelFee = $("#channelFee").val();
                    	if(!channelFee){
                    		channelFee = 0
                    	}
                    	 $("#tradeAmt").val(JoyinMath.doMinus(tradeAmtReal, channelFee)).trigger("blur");
                    } else {
                    	 $("#tradeAmt").val(tradeAmtReal).trigger("blur");
                    }
                }else{
                	$("#tradeAmt").val(tradeAmtReal).trigger("blur");
                }
    			resetTableData();
    		}
    	});
    	$('#channelFee').change(function(){
    		var tradeAmtReal = $('#tradeAmtReal').val();
    		if(tradeAmtReal != '' && tradeAmtReal != null) {
    			if(IS_CAL_LOADRATE&&IS_CAL_LOADRATE=='01'){
    				if(COLLECT_TYPE == COLLECT_TYPE_01){//回款前扣除
    	            	var channelFee = $("#channelFee").val();
    	            	if(!channelFee){
    	            		channelFee = 0;
    	            	}
    	            	 $("#tradeAmt").val(JoyinMath.doMinus(tradeAmtReal, channelFee)).trigger("blur");
    	            }
    			}
    		}
			resetTableData();
    	});
    	// add by renping 2019-10-15 添加通道费 end
        $('#TrdDepoDuration003_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdDepoDuration003_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
    });
}());