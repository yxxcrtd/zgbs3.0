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
            width : "150",
            title : 'IntDetailDuration.portfolioId',
            sortable : false
        }
        // 组合名称
        , {
            field : 'portfolioName',
            width : "150",
            title : 'IntDetailDuration.portfolioName',
            sortable : false
        }
        // 证券账户
        ,{
            field :'secAcctId',
            width : 150,
            title : 'TrdBondInvestTrade002.secManageAcctId',
            edit : false,
            sortable: false,
            editOption:{type:'select',showField:'text',id:'secAcctId', multiple: false,searchable: true,
                          url: JoyinUtil.getRealPath('/component/trade/BussChannelCommon/getSecManageAcctId')
            }
        }
        // 合作资产
         , {
             field : 'chIld',
             visible : false,
             editOption:{type:'select',showField:'text',id:'chIld',multiple: false,searchable: true,
                           url: JoyinUtil.getRealPath('/component/trade/BussChannelCommon/getChannelId')
             }
         }
         // 合作资产
          , {
              field : 'chIldName',
              width : "150",
              edit : false,
              title : 'invest.channelId',
              sortable : false,
              editOption:{type:'select',showField:'text',id:'chIld',multiple: false,searchable: true,
                            url: JoyinUtil.getRealPath('/component/trade/BussChannelCommon/getChannelId')
              }
          }
        // 权益登记日份额
        , {
            field : 'registShare',
            width : "150",
            title : 'IntDetailDuration.registShare',
            type : 'money',
            sortable : false
        }
        // 分红类型
          , {
              field : 'bonusMode',
              width : "150",
              edit : true,
              title : 'TrdFundDurationNew001.bonusMode',
              sortable:false,
              editOption:{type:'select',formatoption:true,result: BONUS_MODE}
          }
        // 分红金额
        , {
            field : 'tradeAmt',
            edit : true,
            width : "150",
            title : 'IntDetailDuration.tradeAmt',
            type : 'money',
            sortable : false
        },
        // 分红份额
        {
            field : 'qty',
            width : "150",
            edit : true,
            title : 'IntDetailDuration.qty',
            type : 'money',
            sortable : false
        },
        //面值
        {field : 'faceValue', visible:false},
         // 是否拆分标识
        {field : 'isFromCopy',title: '拆分标识',visible:false},
        {field:'operation', width:100,title: 'SysCommon.operation',sortable:false, type:'action',
            buttons: ['delt',{clas:'edit',text:'&#xe61f',title:'复制'}]}
        ],
        beforeEditCell:function(value, field, data){
              if(psType != '02'){
                if(field == 'qty'){
                   return false;
                }
              }
              if(psType == '02'){
                  if(field == 'tradeAmt' || field == 'bonusMode'){
                     return false;
                  }
              }
            if(field == 'bonusMode' && ($("#assetType").val() == FUND_ASSET_TYPE_F01 || psType == '01')){
               return false;
            }
            return true;
        },
        editCell : function(value, row, field, cell, column) {
            if (field == 'tradeAmt') {
                var intDetailList = repoInfoTable.getAllRows();
                var sumTradeAmt = 0;
                var sumQty = 0;
                var sumRegistShare = 0;
                // // 汇总
                $.each(intDetailList, function (i, v) {
                    if($("#assetType").val() == FUND_ASSET_TYPE_F01 && v.bonusMode ==BONUS_MODE_02){
                       repoInfoTable.renderRow(i,  {"qty": parseFloat(v.tradeAmt)});
                       v.qty = v.tradeAmt;
                    }
                    sumTradeAmt = JoyinMath.doAdd(v.tradeAmt, sumTradeAmt);
                    sumQty = JoyinMath.doAdd(v.qty, sumQty);
                    sumRegistShare = JoyinMath.doAdd(v.registShare, sumRegistShare);
                });
                $('#tradeAmt').val(sumTradeAmt);
                $('#qty').val(sumQty);
            }else if(field == 'qty') {
                var intDetailList = repoInfoTable.getAllRows();
                var sumTradeAmt = 0;
                var sumQty = 0;
                var sumRegistShare = 0;
                // // 汇总
                $.each(intDetailList, function (i, v) {
                    sumTradeAmt = JoyinMath.doAdd(v.qty, sumTradeAmt);
                    sumQty = JoyinMath.doAdd(v.qty, sumQty);
                    sumRegistShare = JoyinMath.doAdd(v.registShare, sumRegistShare);
                });
                $('#qty').val(sumQty);
            }else if(field == 'bonusMode'){
                //现金分红，分红份额为空不可编辑
                 if(row.bonusMode == BONUS_MODE_01){
                    repoInfoTable.renderRow(row.LAY_TABLE_INDEX,  {"qty": ''});
                 }
            }
        },
        done : function() {
            if (pageModel != PAGE_MODEL_REFERENCE){
                if (!(typeof formTodoList === "undefined") && formTodoList) {
                    calcConditionObjBackUp = tableInitData();
                }
                //统计份额总数
                sumRegistShare = 0;
                var sumTradeAmt = 0;
                var sumQty = 0;
                var allData = repoInfoTable.getAllRows();
                for (var i = 0; i < allData.length; i++) {
                    sumRegistShare = JoyinMath.doAdd(sumRegistShare, allData[i].registShare);
                    sumTradeAmt = JoyinMath.doAdd(allData[i].tradeAmt, sumTradeAmt);
                    sumQty = JoyinMath.doAdd(allData[i].qty, sumQty);
                }
                $('#tradeAmt').val(sumTradeAmt);
                $('#qty').val(sumQty);
                var tradeAmt = $('#tradeAmt').val();
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
     * 空 新建 01：点击复制按钮生成的记录 02：拆分記錄
     */
    repoInfoTable.bind('click', '.edit', function(data, index) {
            if((pageModel == PAGE_MODEL_NEW && psType == '01')
                || (pageModel == PAGE_MODEL_EDIT && psType == '02')){
                return false;
            }
            var tabList  = repoInfoTable.getAllRows();
            if(isNaN(data.qty)){
                data.qty = 0;
                tabList[index].qty = 0;
            }
            if($("#assetType").val()==FUND_ASSET_TYPE_F02){
                if(data.isFromCopy =='02'){
                    var msg = JoyinUtil.getMessage("一条列表信息只可被复制/拆分一次");
                    Joyin.alert(msg, 2);
                }else if(data.isFromCopy =='01'){
                     var msg = JoyinUtil.getMessage("复制/拆分记录不可再复制/拆分");
                     Joyin.alert(msg, 2);
                }else{
                    tabList[index].isFromCopy ='02';
                    if(data.bonusMode == BONUS_MODE_01){
                        data.bonusMode = BONUS_MODE_02;
                    }else{
                        data.bonusMode = BONUS_MODE_01;
                    }
                    data.isFromCopy = '01';
                    tabList.splice(index+1,0,data);
                    repoInfoTable.reload({data:tabList});
                }
            }else{
                var msg = JoyinUtil.getMessage("货币基金不可复制/拆分!");
                Joyin.alert(msg, 2);
                return false;
            }
    });

    /**
     * 绑定删除事件
     */
    repoInfoTable.bind('click', '.delt', function(data, index) {
            if((pageModel == PAGE_MODEL_NEW && psType == '01')
                || (pageModel == PAGE_MODEL_EDIT && psType == '02')){
                return false;
            }
            if(data.isFromCopy == '01'){
                repoInfoTable.removeRow(data);
                var tabList = repoInfoTable.getAllRows();
                tabList[index -1].isFromCopy ='';
                repoInfoTable.reload({data:tabList});
            }else{
                var msg = JoyinUtil.getMessage("非复制/非拆分的记录不可删除");
                Joyin.alert(msg, 2);
            }
    });
//    /**
//     * 绑定编辑事件
//     */
//    repoInfoTable.bind('click', '.detail', function(data, index) {
//        var param = {
//            "portfolioId":data.portfolioId,
//            "finprodMarketId":$('#finprodMarketId').val(),
//            "dealDate": new Date(data.registDate).format(),
//            "bizCode":$("#totleTradeId").val(),
//            "workflowId":typeof workflowId == "undefined" ? '':workflowId,
//            "isAdvance":"N",
//            "defaultMap":JSON.stringify({
//                "unitCprice":$("#unitCprice").val()
//            }),
//            "preMap":JSON.stringify({
//                "registShare":data.registShare,
//                "tradeAmt":data.tradeAmt,
//                "qty":data.qty}),
//            cols:JSON.stringify([
//                // 权益登记日份额
//                {
//                    field : 'registShare',
//                    width : "200",
//                    title : 'IntDetailDuration.registShare',
//                    type : 'money',
//                    sortable : false
//                },
//                // 单位分红净值
//                {
//                    field : 'unitCprice',
//                    width : "200",
//                    // edit : true,
//                    title : 'IntDetailDuration.unitCprice',
//                    type : 'money', digit: 4,
//                    sortable : false
//                },
//                {
//                    field : 'tradeAmt',
//                    width : "200",
//                    // edit : true,
//                    title : 'IntDetailDuration.tradeAmt',
//                    type : 'money',
//                    sortable : false
//                },
//                // 分红份额
//                {
//                    field : 'qty',
//                    width : "200",
//                    title : 'IntDetailDuration.qty',
//                    type : 'money',
//                    sortable : false
//                }
//            ])
//        };
//        Joyin.dialog(JoyinUtil.getLabel('TrdDurationCommon.ChildPortfolioInfo'), JoyinUtil.getRealPath('invest/period/common/ChildPortfolioInfo/init'), '80%', '80%',param);
//    });
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
        var finAssetIntInfosTemp = repoInfoTable.getAllRows();
        //分红类型必填
        var bonusTypeFlag = false;
        //红利下发日必填
        var arriveDateFlag = false;
        //红利再投日必填
        var reInvesDateFlag = false;
        for (var i = 0; i < finAssetIntInfosTemp.length; i++) {
            if(finAssetIntInfosTemp[i].bonusMode == undefined){
               bonusTypeFlag = true;
            }else{
               if(finAssetIntInfosTemp[i].bonusMode == BONUS_MODE_02){
                    reInvesDateFlag = true;
               }else{
                   arriveDateFlag = true;
               }
            }
        }
        if(bonusTypeFlag){
            Joyin.error("请输入分红类型");
            return false;
        }
        if(arriveDateFlag && !$("#arriveDate").val()){
            Joyin.error("请输入红利下发日");
            return false;
        }
        if(reInvesDateFlag && !$("#reInvesDate").val()){
            Joyin.error("请输入红利再投日");
            return false;
        }
        var type = $("#type").val();
        var PS = PS_T13;
        if(pageModel == PAGE_MODEL_NEW){
//        	if($("#assetType").val()==FUND_ASSET_TYPE_F01&&bonusMode == BONUS_MODE_02 && !$("#reInvesDate").val()){
//        	    Joyin.error("请输入红利再投日");
//                return false;
//        	}
        	if ($("#assetType").val()==FUND_ASSET_TYPE_F02 && !$("#exRightsDate").val()) {
                Joyin.error("请输入除权日");
                return false;
            }
        }
//        if(type == "04"){
//        	if(!$("#reInvesDate").val()){
//        	    Joyin.error("请输入红利再投日");
//                return false;
//        	}
//        	PS = PS_T12;
//        }
//        else if(type == "03"||type == "06"){
//        	if(!$("#arriveDate").val()){
//        	    Joyin.error("请输入分红到账日");
//                return false;
//        	}
//        	PS = PS_T13;
//        }
//        var tradeDate;
//        if(reInvesDateFlag){
//        	tradeDate = $("#reInvesDate").val();
//        }else{
//            tradeDate = $("#arriveDate").val();
//        }
//        else {
////        	非货币红利再投
//        	tradeDate = (type == "02" || type=="05") ? $("#exRightsDate").val() :
//                (reInvesDateFlag ? $("#reInvesDate").val() : $("#arriveDate").val());
//        }
        var finAssetIntInfos = repoInfoTable.getAllRows();
        var dtos = [];
        for (var i = 0; i < finAssetIntInfos.length; i++) {
        	var deal = {};
            deal.ccy = $("#ccy").val();
            deal.tradeAmt = finAssetIntInfos[i].tradeAmt;
            deal.ps = PS;
            deal.tradeDate = finAssetIntInfos[i].bonusMode == BONUS_MODE_01 ? $("#arriveDate").val():$("#reInvesDate").val();
            deal.finprodId = $("#finprodId").val();
            deal.finprodType2 = FINPRODTYPE_DEFAULT;
            deal.chlId = finAssetIntInfos[i].chIld;
            deal.secManageAcctId = finAssetIntInfos[i].secAcctId;
            dtos.push({
                'isTotalTrade' : YES_OR_NO_N,
                'bankLendingType' : null,
                'portfolioId' : finAssetIntInfos[i].portfolioId,
                'jiaoyiType' : finAssetIntInfos[i].bonusMode,
                'deal' : deal
            })
        }
        return dtos;
    }
 // 生成指令参数保存
    var stlCommandInput = tableInitData;

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
            deal.secManageAcctId = bussTrdTransferOrders[i].secAcctId;
            deal.finprodId = $("#finprodId").val();
            deal.finprodType2 = FINPRODTYPE_DEFAULT;
            calcConditionObjBackUp[i].deal = deal;
        }
        // }
        if(stlCommandInput != null){
            var currentCalcConditionObj = stlCommandInput();
            // 影响计划生成的所有项目与计划生成时记录的值比较，值变更时
            for ( var key in currentCalcConditionObj) {
                for ( var key1 in currentCalcConditionObj[key]) {
                    delete currentCalcConditionObj[key].jiaoyiType;
                    if (typeof currentCalcConditionObj[key][key1] != "object"
                        && currentCalcConditionObj[key][key1] != calcConditionObjBackUp[key][key1]) {
                        var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key1);
                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
                            [labelText, "生成划款指令"]));
                        return true;
                    }
                }
            }
//            for ( var key in currentCalcConditionObj) {
//                for ( var key1 in currentCalcConditionObj[key].deal) {
//                    var currentDealObj = currentCalcConditionObj[key].deal;
//                    var backupDealObj = calcConditionObjBackUp[key].deal;
//                    if ( typeof currentDealObj[key1] != "object"
//                        && currentDealObj[key1] != backupDealObj[key1]) {
//                        var labelText = JoyinUtil.getLabel("BussTrdTransferOrder." + key1);
//                        Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0013",
//                            [labelText, "生成划款指令"]));
//                        return true;
//                    }
//                }
//            }
            for(var i in calcConditionObjBackUp){
                var backupDealObj = calcConditionObjBackUp[i].deal;
                var currentDealObj = currentCalcConditionObj[i].deal;
                //delete currentDealObj.bonusMode;
                if(currentDealObj.secManageAcctId == undefined || currentDealObj.secManageAcctId == null){
                    currentDealObj.secManageAcctId = currentDealObj.chlId;
                    delete currentDealObj.secManageAcctId;
                }
                if(currentDealObj.chlId == undefined || currentDealObj.chlId == null){
                    delete currentDealObj.chlId;
                }else{
                    currentDealObj.secManageAcctId = currentDealObj.chlId;
                    delete currentDealObj.chlId;
                }
                for ( var key1 in currentDealObj) {
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
            //如果不是现金分红，不需要生成划款指令
            //for(var i in data){
            for(var i=data.length-1;i >= 0;i--){
                if(data[i].jiaoyiType != '01'){
                    data.splice(i,1);
                }
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
            };
            Joyin.ajaxSync(JoyinUtil.getRealPath('/invest/period/trdfunddurationnew/TrdFundDurationNew002/findAssetIntInfo'), param, function(result) {
                                if (result.data) {
                                    finAssetIntInfoList = result.data;
                                    /* 计算 数量总数 */
                                    sumRegistShare = 0;
                                    for (var i = 0; i < finAssetIntInfoList.length; i++) {
                                        sumRegistShare = JoyinMath.doAdd(sumRegistShare, finAssetIntInfoList[i].registShare);
                                        /*面值*/
                                        faceValue = finAssetIntInfoList[i].faceValue;
                                    }
                                    /*add by xr 加载表格前先计算净值*/
                                    var tradeAmt = $('#tradeAmt').val();
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
        // 利息明细根据基础数据联动
        var finAssetIntInfos = finAssetIntInfoList;
        if(!finAssetIntInfos){
        	return;
        }
        //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
       var assetType = $("#assetType").val();
       var itemArr = ['qty','tradeAmt'];
//        if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
//            var itemArr = ['qty','tradeAmt'];
//        }else{
//            var itemArr = ['tradeAmt'];
//        }
        //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
//      所有的钱
        var itemObj = {};
//      已分配的钱
        var sumObj = {};
//      初始化所有的钱和已分配的钱
        $.each(itemArr,function(index,val){
            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
//            if (assetType == FUND_ASSET_TYPE_F01) {
//                if (faceValue && val == 'qty') {
//                    var value = $('#' + val).val();
//                    var tradeAmt = $('#tradeAmt').val();
//                    if (value) {
//                        itemObj[val] = value || 0;
//                    } else {
//                        itemObj[val] = JoyinMath.doDiv(tradeAmt, faceValue) || 0;
//                    }
//                } else {
//                    itemObj[val] = $('#' + val).val() || 0;
//                }
//            }else{
                itemObj[val] = $('#' + val).val() || 0;
//            }
            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
            sumObj[val] = 0;
        })
//      分配
//        for (var i = 0; i < finAssetIntInfos.length; i++) {
//            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
//            if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
//
//            }else{
//                finAssetIntInfos[i][qty]=" ";
//            }
//            //现金分红的时候，列表中不展示分红份额字段，货币型红利再投显示
////          最后一个
//            if (i == finAssetIntInfos.length - 1) {
////        		将剩余的钱放入对应值
//                $.each(itemArr,function(index,val){
//                    finAssetIntInfos[i][val] = JoyinMath.doMinus(itemObj[val], sumObj[val]);
//                })
//            }
////        	非最后一个
//            else {
////        		计算占比
//                var prop = JoyinMath.doDiv(finAssetIntInfos[i].registShare, sumRegistShare, 12, JoyinMath.ROUND_HALF_UP);
////        		根据占比处理所有的钱
//                $.each(itemArr,function(index,val){
////        			计算当前需要的钱
//                    var thisNum = JoyinMath.doMul(itemObj[val], prop, 2, JoyinMath.ROUND_HALF_UP);
////        			处理已分配的钱
//                    sumObj[val] = JoyinMath.doAdd(sumObj[val], thisNum);
////        			把钱放入对应数据
//                    finAssetIntInfos[i][val] = thisNum;
//                })
//            }
//        }
        var tradeAmt = $("#tradeAmt").val();
        var assetType = $("#assetType").val();
        // // 分配处理每一行
        for (var i = 0; i < finAssetIntInfos.length; i++) {
            if (finAssetIntInfos[i].useFlg
                    && finAssetIntInfos[i].useFlg === 'Y') {
                continue;
            }
        }
        if(repoInfoTable.table){
            repoInfoTable.clear(function() {
                $.each(finAssetIntInfos, function(i, v) {
                    repoInfoTable.addRow(v);
                });
            });
        }
    }

    function saveOrSubmit(operateType) {
        var objectTemp = $.serializeToObject('#informationForm', true);
        for(var i in objectTemp.finAssetIntInfoList){
            if(!objectTemp.finAssetIntInfoList[i].tradeAmt || objectTemp.finAssetIntInfoList[i].tradeAmt <= 0){
                var msg = JoyinUtil.getMessage("分红明细信息中分红金额不能为空/小于等于0!");
                Joyin.alert(msg, 2);
                return;
            }
            if(psType == '02'
                && (!objectTemp.finAssetIntInfoList[i].qty || objectTemp.finAssetIntInfoList[i].qty <= 0)){
                     var msg = JoyinUtil.getMessage("分红明细信息中分红份额不能为空/小于等于0!");
                     Joyin.alert(msg, 2);
                     return;
            }
        }
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
        object.registShare = sumRegistShare;
        object.operateType = operateType;
        object.tradeMarket = tradeMarket;
        //分红明细不能为空
        if(object.finAssetIntInfoList.length == 0){
            var msg = JoyinUtil.getMessage("分红明细信息不能为空！");
            Joyin.alert(msg, 2);
            return;
        }
        if(isCalcConditionChangedTwo(object.bussTrdTransferOrder)){
            return;
        }
        //        新建打开为00
//        if (object.type == '00') {
//            if (object.assetType == FUND_ASSET_TYPE_F01) {
//                //            	现金分红为05（分红除权），红利再投为01
//                object.type = object.finAssetIntInfoList[0].bonusMode == BONUS_MODE_01?'05':'01';
//            }
//            else {
//                // 非货币除权登记
//                object.type = '02';
//            }
//            // 其他场景已近在001打开画面时处理
//        }
        if (FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo();
        }

        Joyin.ajaxJson(basePath + 'invest/period/trdfunddurationnew/TrdFundDurationNew002/check', object, function(result) {
            Joyin.confirm(result.data,function() {
//                // 非货币基金的现金分红，红利再投
                if (object.type == '03'||object.type == '06') {
                    var elementId = 'TrdFundDurationNew002_btn_total';
                }
                if (operateType == 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/period/trdfunddurationnew/TrdFundDurationNew002/save', object, isNotNull(elementId) ? elementId
                        : "TrdFundDurationNew002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'invest/period/trdfunddurationnew/TrdFundDurationNew002/save', object, isNotNull(elementId) ? elementId
                        : "TrdFundDurationNew002_btn_submit");
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
//    var bonusModeChange = function() {
//    	var assetType = $('#assetType').val();
//    	var bonusMode = $('#bonusMode').val();
//        if (assetType == FUND_ASSET_TYPE_F01 && bonusMode == BONUS_MODE_02) {
//            // 权益登记日 除权日 权益登记日份额 单位分红净值不显示
////            $('.currency').hide();
//            if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
//                $("#exRightsDate").val(sysDate).trigger("change");
//                $("#registDate").val(sysDate).trigger("change");
//                $("#exRightsDate").attr("disabled",true);
//                $("#registDate").attr("disabled",true);
//            }
////            $('#unitCprice').attr("disabled","true");
////        	triValueChange('unitCprice',1);
//            triValueChange('unitCprice');
//            if(pageModel!=PAGE_MODEL_NEW){
//                triValueChange('exRightsDate');
//            }
//        }
//        // 资产类型为非货币
//        else if(assetType == FUND_ASSET_TYPE_F01 && bonusMode == BONUS_MODE_01){
//        	$('.currency').show();
//            triValueChange('tradeAmt');
//            if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
//                $("#exRightsDate").val(sysDate).trigger("change");
//                $("#registDate").val(sysDate).trigger("change");
//                $("#exRightsDate").attr("disabled",true);
//                $("#registDate").attr("disabled",true);
//            }
//        }
//        else if(assetType == FUND_ASSET_TYPE_F02){
//        	triValueChange('unitCprice');
//            // 展示联动区域
//            $('.currency').show();
//            if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
//                $("#exRightsDate").val('').trigger("change");
//                $("#registDate").val('').trigger("change");
//                $("#exRightsDate").attr("disabled",false);
//                $("#registDate").attr("disabled",false);
//            }
//        }
//        if(isFromCheck&&isFromCheck=='true'){
//            $("#exRightsDate").attr("disabled",true);
//            $("#registDate").attr("disabled",true);
//        }
//     // 判断分红类型
//        if (bonusMode == BONUS_MODE_01) {
//            $('#reInvesDate').val('');
//            $('.reInvesDate').hide();
//            $('.arriveDate').show();
//        } else if (bonusMode == BONUS_MODE_02) {
//            $('#arriveDate').val('')
//            $('.arriveDate').hide();
//            $('.reInvesDate').show();
//        }
//    }
    var init = function() {
        // 资产类型
//        assetTypeChagne();
//        if(pageModel != PAGE_MODEL_REFERENCE){
//    	    bonusModeChange();
//    	}
        if(pageModel == PAGE_MODEL_NEW && !psType){
            $("#finprodMarketId").attr("disabled",false);
        }else{
            $("#finprodMarketId").attr("disabled",true);
        }
        var assetType = $("#assetType").val();
        if(assetType == FUND_ASSET_TYPE_F01){
            $("#exRightsDate").attr("disabled",true);
            $("#registDate").attr("disabled",true);
        }
        initFlag = false;
        if(psType == '01'){
            $("#reInvesDate").val('').prop("disabled", true);
            $("#exRightsDate").attr("disabled",true);
            $("#registDate").attr("disabled",true);
        }else if(psType == '02'){
            $("#arriveDate").val('').prop("disabled", true);
            $("#exRightsDate").attr("disabled",true);
            $("#registDate").attr("disabled",true);
        }
        if(psType != '01'){
        	$("#bussTrdTransferOrder_div").hide();
        }
    };
    /**
     * 画面加载完成
     */
    $(function() {
        // 1.资产代码下拉框选择后，联动带出 币种、资产类型、资产名称、金融产品代码、发行人
        $('#finprodMarketId')
                .change(
                        function() {
                            var finprodMarketId = $('#finprodMarketId').val();
                            var data = $("#finprodMarketId").find("option:selected").data("data");
                            var payType = data.object ? data.object || '' : '';
                            tradeMarket = ''
                            if (finprodMarketId || finprodMarketId != '') {
                                Joyin.ajax(basePath + "invest/period/trdfunddurationnew/TrdFundDurationNew002/finprodMarketIdLink",
                                    {finprodMarketId: finprodMarketId }, function (result) {
                                        if (result.data) {
                                            tradeMarket = result.data.TRADE_MARKET;
                                            triValueChange('finprodMarketName', result.data.FINPRODMARKETNAME);
                                            triValueChange('finprodId', result.data.FINPRODID);
                                            triValueChange('issuer', result.data.ISSUER);
                                            triValueChange('ccy', result.data.CCY);
                                            triValueChange('assetType', result.data.ASSETTYPE);
//                                            bonusModeChange();
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
               $("#exRightsDate").val(sysDate).trigger("change");
               $("#registDate").val(sysDate).trigger("change");
               $("#exRightsDate").attr("disabled",true);
               $("#registDate").attr("disabled",true);
           }else{
               if(pageModel==PAGE_MODEL_NEW&&!isFromCheck){
                   $("#exRightsDate").val('').trigger("change");
                   $("#registDate").val('').trigger("change");
                   $("#exRightsDate").attr("disabled",false);
                   $("#registDate").attr("disabled",false);
               }
           }
       });

//        // 道交易 新增 强成西 2019/03/26 start
//       $('#chlId').change(function() {
//           dealIntDetail();
//       });
//        // 道交易 新增 强成西 2019/03/26 end

        // 6.分红类型为现金分红时
        // 红利再投日不显示
        // 7.分红类型为红利再投时
        // 分红到账日不显示
//        $('#bonusMode').change(bonusModeChange);
        // 交易金额联动
//        $('#tradeAmt').change(function() {
//            var tradeAmt = $('#tradeAmt').val();
//            if (isNotNull(tradeAmt)&&isNotNull(sumRegistShare)) {
//                var assetType = $("#assetType").val();
//                if(assetType == FUND_ASSET_TYPE_F01 && $('#bonusMode').val() == BONUS_MODE_02){
//                    $("#qty").val(tradeAmt).trigger("blur");
//                }
//                else{
//                    var unitCprice = JoyinMath.doDiv(tradeAmt, sumRegistShare, 12, JoyinMath.ROUND_HALF_UP);
//                    $("#unitCprice").val(unitCprice);
//                    $("#unitCprice").trigger("blur");
//                }
//                resetTableData();
//            }
//        });
//        // 份额联动
//        $('#qty').change(function() {
//            var qty = $('#qty').val();
//            if (isNotNull(qty)) {
//                resetTableData();
//            }
//        });
//        $('#chlId').change(function(e, 其他事件触发的) {
//            var chlId = $('#chlId').val();
//            if (chlId) {
//                $("#secManageAcctId").val("").trigger("change", true);
//            }
//            if (!其他事件触发的) {
//                dealIntDetail();
//            }
//        });
//        $('#secManageAcctId').change(function(e, 其他事件触发的) {
//            var secManageAcctId = $('#secManageAcctId').val();
//            if (secManageAcctId) {
//                $("#chlId").val("").trigger("change", true);
//            }
//            if (!其他事件触发的) {
//                dealIntDetail();
//            }
//        });

        // 权益登记日联动
        $('#registDate').change(function() {
            // var finprodMarketId = $('#finprodMarketId').val();
            var registDate = $('#registDate').val();
            if (isNotNull(registDate)) {
                dealIntDetail();
            }
        });
        $('#TrdFundDurationNew002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdFundDurationNew002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        setTimeout(function(){init();},0);
    });
}());
