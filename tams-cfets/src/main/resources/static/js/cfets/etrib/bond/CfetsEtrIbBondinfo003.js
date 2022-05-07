(function () {
    // 风险试算结果
    var initFlag = false;
    var businessId;
    var prodIdFlag = 0;
    var ps = query("#ps").val();
    var riskFlag = 1;
    var calflag;

    function query(ele) {
        return $("#CfetsEtrIbBondinfo003_bodydiv " + ele);
    }

    function getBussCombDistribtTableParams() {
        var ebcd = entity_BussCombDistribtList || [{}];
        if (ebcd.length > 0) {
            for (var index in ebcd) {
                ebcd[index].entrustNum = ebcd[index].entrustNum / 10000;
                ebcd[index].entrustOwnNum = ebcd[index].entrustOwnNum / 10000;
                ebcd[index].entrustIntegrateNum = ebcd[index].entrustIntegrateNum / 10000;
            }
        }
        return ebcd;
    }

    var colsCombDistribt = [
        {
            /* 组合代码 */
            field: 'portfolioId',
            width: 200,
            title: 'CfetsEtribBondInfo003.portfolioId',
            sortable: false
            , edit: true
            , editOption: {
                type: 'select',
                id: 'portfolioId',
                showField: 'text',
                multiple: false,
                searchable: true,
                url: JoyinUtil.getRealPath('cfets/etrib/cfetsEtrIbBondinfo/selectPortfolioIdForSelect'),
                postData: function () {
                    var param = {finprodId : $("#finprodIdhid").val()};
                    if ((ps == 'T02' || ps == 'T03') && prodIdFlag == 1) {
                        param.initValue = null;
                    }
                    if (ps == 'T02' || ps == 'T03') {
                        param.ps = $("#ps").val();
                    }
                    return param;
                }
            }
        }
        , {/*业务模式*/
            field: 'businessModel',
            width: 180,
            title: 'CfetsEtribBondInfo003.businessModel',
            sortable: false,
            edit: true,
            editOption: {type: 'select', formatoption: true, showField: 'text', result: BUSINESS_MODEL}
        }
        , {
            /*劵面总额*/
            field: 'entrustNum',
            width: 160,
            title: 'CfetsEtribBondInfo003.entrustAmt',
            sortable: false,
            edit: true,
            type: 'number',
            digit: 2
        }
        , {
            /*本方券券面总额（万元）*/
            field: 'entrustOwnNum',
            width: 180,
            title: 'CfetsEtribBondInfo003.entrustOwnNum',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? (ps != 'T03' ? true : false) : false,
            type: 'number',
            digit: 2
        }
        , {
            field: 'entrustIntegrateNum',
            width: 180,
            title: 'CfetsEtribBondInfo003.entrustIntegratCasheNum',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? (ps != 'T03' ? true : false) : false,
            type: 'number',
            edit: true,
            digit: 2
        }
        , {
            field: 'proAvailableCash',
            width: 150,
            title: 'CfetsEtribBondInfo003.proAvailableCash',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: ps !== 'T03' ? true:false
        }
        , {
            field: 'proAvailableCash1',
            width: 200,
            title: 'CfetsEtribBondInfo003.proAvailableCash1',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: ps !== 'T03' ? true:false
        }
        , {
            field: 'secAcctAvailableCash',
            width: 200,
            title: 'CfetsEtribBondInfo003.secAcctAvailableCash',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: ps !== 'T03' ? true:false
        }
        , {
            field: 'porAvailableSec',
            width: 200,
            title: 'CfetsEtribBondInfo003.porAvailableSec',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? false : true,
            type: 'number',
            digit: 2
        }
        , {
            field: 'porAvailableSec1',
            width: 200,
            title: 'CfetsEtribBondInfo003.porAvailableSec1',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? false : true,
            type: 'number',
            digit: 2,
        }
        , {
            field: 'entrustIntegrateSecNum',
            width: 200,
            title: 'CfetsEtribBondInfo003.entrustIntegrateSecNum',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? true : false,
            type: 'number',
            digit: 2
        }
        , {
            field: 'ownPorAvailableSec',
            width: 200,
            title: 'CfetsEtribBondInfo003.ownPorAvailableSec',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? true : false,
            type: 'number',
            digit: 2
        }
        , {
            field: 'ownProAvailableSec1',
            width: 200,
            title: 'CfetsEtribBondInfo003.ownProAvailableSec1',
            sortable: false,
            visible: SHORT_SELLING_SUPPORTED == '02' ? true : false,
            type: 'number',
            digit: 2
        }
        , {
            field: 'entrustId',
            title: '指令Id',
            width: 200,
            visible: false
        },
        {field:'operation', width:20, title: 'SysCommon.operation',fixed:"right" ,sortable:false, type: 'action', buttons: ['delt']}

    ]

    var table = query("#bussCombDistribtTable").joyintable({
        data: getBussCombDistribtTableParams(),
        cols: colsCombDistribt,
        page: false,
        addToLast: true,
        searchOnInit: true,
        checkbox: false,
        height: 290,
        addbtn: true,
        done:function(){
            this.bind('click', '.delt', function(data, index) {
                var allRow = table.getAllRows();
                if(allRow.length > 1) {
                    table.deleteRow(index);
                }else {
                    Joyin.error("至少要保留一个组合");
                }
                setRiskFlag(1);

            });
        },
        beforeEditCell: function(preVal, field, rowData) {
            if(field === 'businessModel') {
                if(ps == 'T03' || ps == 'T02') return false;
            }
            return true;
        },
        editCell: function (value, data, field, cell, column) {
            setRiskFlag(1);
            if (field == 'portfolioId') {
                if(!value) {
                    Joyin.error("组合不能为空");
                    return;
                }
                var allRow = table.getAllRows();
                var exist = 0;
                $.each(allRow,function (index,item) {
                    if(value == item.portfolioId) {
                        exist = exist + 1;
                    }
                })
                if(exist > 1) {
                    Joyin.error(value+"组合已经存在,不可以重复选择");
                    data.portfolioId = null;
                    table.renderRow(data.LAY_TABLE_INDEX, data);
                    return;
                }
                var arr = [];
                var param = {}
                arr.push(value);
                param.portfolioIdList = arr;
                param.finprodId = query("#finprodIdhid").val();
                Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/queryPositionInfo', param, function (result) {
                    if(result.data) {
                        $.each(result.data,function (index,item) {
                            if(item.field == 'T0KYXJ') {//T日可用现金（元）
                                if ("--" == item.amount) {
                                    data.proAvailableCash = 0;
                                } else {
                                    data.proAvailableCash = item.amount;
                                }
                            }
                            if(item.field == 'T1KYXJ') {//T1日可用现金（元）
                                if ("--" == item.amount) {
                                    data.proAvailableCash1 = 0;
                                } else {
                                    data.proAvailableCash1 = item.amount;
                                }
                            }
                            if(item.field == 'ZHXJ') {//账户现金
                                if ("--" == item.amount) {
                                    data.secAcctAvailableCash = 0;
                                } else {
                                    data.secAcctAvailableCash = item.amount;
                                }
                            }
                            if(item.field == 'RRQMKDGHCC') {//融入券卖空待购回持仓（元）
                                if ("--" == item.amount) {
                                    data.entrustIntegrateSecNum = 0;
                                } else {
                                    data.entrustIntegrateSecNum = item.amount;
                                }
                            }
                            if(item.field == 'ZCTRKYCC') {//资产T日可用持仓（元）
                                if ("--" == item.amount) {
                                    data.porAvailableSec = 0;
                                } else {
                                    data.porAvailableSec = item.amount;
                                }
                            }
                            if(item.field == 'ZCT1RKYCC') {//资产T+1日可用持仓（元）
                                if ("--" == item.amount) {
                                    data.porAvailableSec1 = 0;
                                } else {
                                    data.porAvailableSec1 = item.amount;
                                }
                            }
                            if(item.field == 'BFQTRKYCC') {//本方券T日可用持仓（元）
                                if ("--" == item.amount) {
                                    data.ownPorAvailableSec = 0;
                                } else {
                                    data.ownPorAvailableSec = item.amount;
                                }
                            }
                            if(item.field == 'BFQT1RKYCC') {//本方券T+1日可用持仓（元）
                                if ("--" == item.amount) {
                                    data.ownProAvailableSec1 = 0;
                                } else {
                                    data.ownProAvailableSec1 = item.amount;
                                }
                            }
                           /* if(item.field == 'RRQTRKYCC') {//融入券T日可用持仓（元）

                            }
                            if(item.field == 'RRQT1RKYCC') {//融入券T+1日可用持仓（元）

                            }*/
                        })
                        data.entrustIntegrateNum = 0;
                        table.renderRow(data.LAY_TABLE_INDEX, data);
                    }
                },false);
                if(!data.entrustId) {
                    Joyin.ajaxSync(basePath + 'cfets/etrib/CfetsEtrIbBondinfo003/getEntrustId',{},function (result) {
                        data.entrustId = result.data;
                        table.renderRow(data.LAY_TABLE_INDEX, data);
                    })
                }
                if(ps == 'T02' || ps == 'T03') {//获取数量和模式
                    Joyin.ajax(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/selectPortfolioIdForSelect?ps='+ps+'&finprodId='+$("#finprodIdhid").val() +"&initValue="+value, {}, function (result) {
                        console.log(result);
                        if(result.root && result.root.length > 0) {
                            if(result.root[0].object.businessModel && result.root[0].object.businessModel.indexOf(",") != -1) {
                                data.portfolioId = null;
                                data.businessModel = null;
                                data.proAvailableCash = 0;
                                data.proAvailableCash1 = 0;
                                data.secAcctAvailableCash = 0;
                                data.porAvailableSec = 0;
                                data.entrustIntegrateSecNum = 0;
                                data.ownPorAvailableSec = 0;
                                data.ownProAvailableSec1 = 0;
                                table.renderRow(data.LAY_TABLE_INDEX, data);
                                Joyin.error("组合【"+result.root[0].text+"】存在多个业务模式，请先做资产重分类");
                                return;
                            }else {
                                data.businessModel = result.root[0].object.businessModel;
                            }
                            if(ps == 'T03') {
                                data.entrustNum = result.root[0].object.entrustNum;
                            }
                            table.renderRow(data.LAY_TABLE_INDEX, data);
                        }
                    },false)
                }
            }
            if (field == 'entrustNum') {
                data.entrustOwnNum = value;
                table.renderRow(data.LAY_TABLE_INDEX, data);
            }
            if(field == 'entrustIntegrateNum') {
                if(value) {
                    data.entrustOwnNum = JoyinMath.doMinus(data.entrustNum,value,2);
                    table.renderRow(data.LAY_TABLE_INDEX, data);
                }
            }
        }
    });
    var colsRiskTrialRet = [
        {
            /* 指标名称*/
            field: 'kriName',
            width: 100,
            title: 'BaseRiskMathComm.indName',
            sortable: false
        }
        , {/*指标类型*/
            field: 'kriTypeCn',
            width: 50,
            title: 'BaseRiskMathComm.indType',
            sortable: false
        }
        , {
            /*组合名称*/
            field: 'portfolioName',
            width: 150,
            title: 'BaseRiskMathComm.portfolioName',
            sortable: false
        }
        , {
            /*预警阈值*/
            field: 'warningLine',
            width: 150,
            title: 'BaseRiskMathComm.wthreshold',
            sortable: false
        }
        , {
            /*审批线*/
            field: 'approveLine',
            width: 150,
            title: 'BaseRiskMathComm.approveold',
            sortable: false
        }
        , {
            /*当前实际值*/
            field: 'kriValue',
            width: 200,
            title: 'BaseRiskMathComm.currValue',
            sortable: false
        }
        , {
            /*原值*/
            field: 'oldValue',
            width: 200,
            title: 'BaseRiskMathComm.preValue',
            sortable: false
        }
        , {
            /*趋势*/
            field: 'kriTrend',
            width: 200,
            title: 'BaseRiskMathComm.kriTrend',
            sortable: false
        }

        , {
            field: 'kriStatusCn', title: 'BaseRiskMathComm.currStatus', sortable: false,
            render: function (data, type, row, meta) {
                if (row.kriStatus == 0) {
                    return "<span>正常</span>";
                }
                if (row.kriStatus == 1) {
                    return "<span style='color:orange'>预警</span>";
                }
                if (row.kriStatus == 2) {
                    return "<span style='color:#F7580D'>审批</span>";
                }
                if (row.kriStatus == 3) {
                    return "<span style='color:#F71A0D'>违规</span>";
                }
                if (row.kriStatus == 4) {
                    return "<span style='color:#951F1F'>异常</span>";
                }
                return row.kriStatus;
            }
        }
    ]

    var tableRisk = query("#riskTrialRetTable").joyintable({
        data: entity_RiskTrialRetList || [],
        cols: colsRiskTrialRet,
        page: true,
        addToLast: true,
        searchOnInit: true,
        checkbox: false,
        height: 290
    });

    var init = function () {
        if(!!query("#finprodName").val()){
            query("#finprodName").attr("disabled", true);
        }
        query("#calculateField_price").prop('checked', true);
        layui.form.render('radio');
        if (calculateField) {
            query("#calculateField_" + calculateField).attr("checked", true);
            layui.form.render('radio');
            query("#" + calculateField + "Show").attr("disabled", false);
        }
        var restTradeAmt = query("#tradeAmt").val() == null ? 0 : query("#tradeAmt").val();
        query("#tradeAmt").val(JoyinMath.doDiv(restTradeAmt, 10000, 2)).trigger("change");
        var restEntrustOwnNum = query("#entrustOwnNum").val() == null ? 0 : query("#entrustOwnNum").val();
        query("#entrustOwnNum").val(JoyinMath.doDiv(restEntrustOwnNum, 10000, 2)).trigger("change");
        var restEntrustIntegrateNum = query("#entrustIntegrateNum").val() == null ? 0 : query("#entrustIntegrateNum").val();
        query("#entrustIntegrateNum").val(JoyinMath.doDiv(restEntrustIntegrateNum, 10000, 2)).trigger("change");
        businessId = query('#businessId').val();
       /* if(query("#matureYield").val()) {
            query("#matureYieldShow").val(JoyinMath.doMul(query("#matureYieldShow").val(), 100, 4));
            query("#matureYield").val(query("#matureYield").val() * 100);
        }
        if(query("#exerYield").val()) {
            query("#exerYieldShow").val(JoyinMath.doMul(query("#exerYieldShow").val(),100,4));
            query("#exerYield").val(query("#exerYield").val() * 100);
        }*/

        var riskLists = entity_RiskTrialRetList || [];
        if (riskLists.length > 0) {
            $("#riskTrialSpan").html("&nbsp;&nbsp;" + riskLists[0].conclusion);
            setRiskFlag(0);
        }
    }

    init();

    var calData = function (calflag) {
        if (ps == 'T03') {
            return;
        }
        if (!calflag) {
            calflag = query("input[name=calculateField]:checked").val();
        }
        if(!query("#tradeAmt").val()) {
            return;
        }
        var object = {};
        object.calflag = calflag;
        object.finvdate = query("#finvdate").val();
        object.finmdate = query("#finmdate").val();
        object.finprodId = query("#finprodIdhid").val();
        object.tradeMarketMode = SHARES_TRADE_MARKET_02;
        object.matureYield = $("#matureYield").val();
        object.ps = query("#ps").val();
        object.priceModel = $("#priceModel").val();
        object.settleSpeed = $("#settleSpeed").val();
        object.tradeMarket = $("#tradeMarket").val();
        object.etrDate = $("#etrDate").val();
        object.price = $("#price").val();
        object.unitFprice = $("#unitFprice").val();
        if (query('#exerYield').val() || query('#exerYieldShow').val()) {
            if(query('#exerYieldShow').val()) {
                object.exerYield = query('#exerYieldShow').val() / 100;
            }
            if(query('#exerYield').val() && Number(query('#exerYield').val())) {
                object.exerYield = query('#exerYield').val() / 100;
            }
        }
        if (query('#matureYield').val() || query('#matureYieldShow').val()) {
            if(query('#matureYieldShow').val()) {
                object.matureYield = query('#matureYieldShow').val() / 100;
            }
            if(query('#matureYield').val() && Number(query('#matureYield').val())) {
                object.matureYield = query('#matureYield').val() / 100;
            }
        }
        object.entrustNum = query("#tradeAmt").val() * 10000;
        Joyin.ajaxJson(basePath + "cfets/etrib/CfetsEtrIbBondinfo003/calData", object, function (result) {
            if (result && result.data) {
                var data = result.data.data;
                if (data.price) {
                    query("#price").val(data.price)
                    query("#priceShow").val(JoyinMath.doDiv(data.price, 1, 4));
                    query("#unitFpriceShow").val(JoyinMath.doDiv(data.unitFprice, 1, 4));
                    query("#unitFprice").val(data.unitFprice);
                    query('#exerYieldShow').val(JoyinMath.doMul(data.exerYield, 100, 4));
                    query('#exerYield').val(data.exerYield * 100);
                    query("#matureYieldShow").val(JoyinMath.doMul(data.matureYield, 100, 4));
                    query("#matureYield").val(data.matureYield * 100);
                    calAmountData();
                } else {
                    query("#price").val("");
                    query("#priceShow").val("");
                    query("#unitFpriceShow").val("");
                    query("#unitFprice").val("")
                    calAmountData();
                }
            }
        });
    };

    function calAmountData() {
        if (ps == 'T03') {
            return;
        }
        var tradeAmt = query("#tradeAmt").val();
        var entrustIntegrateNum = query("#entrustIntegrateNum").val();
        if (tradeAmt && entrustIntegrateNum) {
            query("#entrustOwnNum").val(JoyinMath.doMinus(tradeAmt, entrustIntegrateNum, 2));
        }
        if(!query("#tradeAmt").val() || !query("#entrustOwnNum").val()) {
            return;
        }
        var param = {};
        param.shareNum = JoyinMath.doMul(query("#tradeAmt").val(),10000);
        param.entrustOwnNum = JoyinMath.doMul(query("#entrustOwnNum").val(),10000);
        param.entrustIntegrateNum = JoyinMath.doMul(query("#entrustIntegrateNum").val(),10000);
        param.price = query("#priceShow").val();
        param.unitFprice = query("#unitFprice").val();
        Joyin.ajaxJson(basePath + 'cfets/etrib/CfetsEtrIbBondinfo003/calAmountData', param, function (result) {
            var data = result.data.data;
            if (data) {
                //给一大串字段赋值
                query("#priceShow").val(JoyinMath.doDiv(data.price, 1, 4));
                query("#price").val(data.price);
                query("#priceAmt").val(JoyinMath.doDiv(data.priceAmt, 1, 2));
                query("#unitInt").val(JoyinMath.doDiv(data.unitInt, 1, 4));
                query("#unitIntShow").val(JoyinMath.doDiv(data.unitInt, 1, 4));
                query("#unitIntAmt").val(JoyinMath.doDiv(data.unitIntAmt, 1, 2));
                query("#unitFpriceShow").val(JoyinMath.doDiv(data.unitFprice, 1, 4));
                query("#unitFprice").val(data.unitFprice);
                query("#unitFpriceAmt").val(JoyinMath.doDiv(data.unitFpriceAmt, 1, 2));
                query("#entrustOwnInt").val(JoyinMath.doDiv(data.entrustOwnInt, 1, 2));
                query("#entrustIntegrateInt").val(JoyinMath.doDiv(data.entrustIntegrateInt, 1, 2));
                query("#entrustOwnUnitFpriceAmt").val(JoyinMath.doDiv(data.entrustOwnUnitFpriceAmt, 1, 2));
                query("#entrustIntegrateUnitFpriceAmt").val(JoyinMath.doDiv(data.entrustIntegrateUnitFpriceAmt, 1, 2));
            }
        });
    }


    /**
     * 获取提交的数据
     * @param operateType
     * @returns {{}}
     */
    function getSubmitParam(operateType) {
        var res = {}
        var object = $.serializeToObject('#searchForm');
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.tradeMarket = query('#tradeMarket').val();
        object.tradeMarketMode = query('#tradeMarketMode').val();
        object.entrustId = query('#entrustId').val();
        object.businessId = query('#businessId').val();
        object.totalEtrustId = query('#totalEtrustId').val();
        object.entrustStatus = query('#entrustStatus').val();
        object.finvdate = query('#finvdate').val();
        object.invManagerId = query('#invManagerId').val();
        object.transmitTime = query('#transmitTime').val();
        // 证券账户ID
        object.secAcctId = query('#secAcctId').val();
        object.vdate = object.etrDate;
        object.mdate = object.etrDate;
        object.entrustDate = object.vdate > object.finvdate.split(' ')[0] ? object.vdate : object.finvdate.split(' ')[0];
        //提交获取计算方式单选框的值

        object.calculateField = query("input[name=calculateField]:checked").val();
        if (query('#exerYield').val() || query('#exerYieldShow').val()) {
            if(query('#exerYieldShow').val()) {
                object.exerYield = query('#exerYieldShow').val() / 100;
            }
            if(query('#exerYield').val() && Number(query('#exerYield').val())) {
                object.exerYield = query('#exerYield').val() / 100;
            }
        }
        if (query('#matureYield').val() || query('#matureYieldShow').val()) {
            if(query('#matureYieldShow').val()) {
                object.matureYield = query('#matureYieldShow').val() / 100;
            }
            if(query('#matureYield').val() && Number(query('#matureYield').val())) {
                object.matureYield = query('#matureYield').val() / 100;
            }
        }
        if (query('#tradeAmt').val()) {
            object.tradeAmt = query('#tradeAmt').val() * 10000;
            object.entrustNum = query('#tradeAmt').val() * 10000;
        }
        if (query('#entrustOwnNum').val()) {
            object.entrustOwnNum = query('#entrustOwnNum').val() * 10000;
        }
        if (query('#entrustIntegrateNum').val()) {
            object.entrustIntegrateNum = query('#entrustIntegrateNum').val() * 10000;
        }

        object.finmdate = query('#finmdate').val();
        object.ccy = query('#ccy').val();
        var objects = new Array();
        var entrustNum = 0;
        var finprodId = null;
        var counterId = null;
        var tradeMarket = query('#tradeMarket').val();
        var param = {};

        var tradeAmt = Number(0)
        for (var index in object.bussCombDistribtList) {
            var item = object.bussCombDistribtList[index];
            var temp = JSON.parse(JSON.stringify(object));
            temp.portfolioId = item.portfolioId;
            tradeAmt += parseFloat(item.entrustNum) * 10000;
            if (item.entrustNum && item.entrustNum != '') {
                item.entrustNum = item.entrustNum * 10000;
                temp.entrustNum = item.entrustNum;
            }
            if (item.entrustOwnNum && item.entrustOwnNum != '') {
                item.entrustOwnNum = item.entrustOwnNum * 10000;
                temp.entrustOwnNum = item.entrustOwnNum;
            }
            if (item.entrustIntegrateNum && item.entrustIntegrateNum != '') {
                item.entrustIntegrateNum = item.entrustIntegrateNum * 10000;
                temp.entrustIntegrateNum = item.entrustIntegrateNum;
            }
            finprodId = temp.finprodId;
            tradeMarket = temp.tradeMarket;
            temp.entrustId = item.entrustId;
            temp.invAim = item.invAim;
            temp.businessModel = item.businessModel;
            temp.elementId = 'CfetsEtribBondInfo003_btn_submit';
            objects.push(temp);
        }
        param = JSON.parse(JSON.stringify(object));
        // param.entrustNum = object.tradeAmt;
        //param.finprodId = finprodId;
        param.finprodAbbr = query("#finprodAbbr").val();
       //param.tradeMarket = tradeMarket;
        param.invManagerId = query('#invManagerId').val();
        param.ps = query("#ps").val();
        param.ccy = query('#ccy').val();
        param.etrInvestBondInfoDtos = objects;
        param.bussCombDistribtList = object.bussCombDistribtList;
        param.operateType = operateType;
        param.pageModel = pageModel;
        param.riskTrialRetDto = entity_RiskTrialRetList;
        param.elementId = 'CfetsEtrIbBondinfo003_btn_submit';
        param.isDzh = 'Y';
        res.param = param;
        res.objects = objects;
        console.log(res);
        return res;
    }

    function setRiskFlag(flag) {
        riskFlag = flag;
    }

    function paramCheck() {
        if (!query("#finprodName").val() || !query("#priceModel").val() || !query("#tradeAmt").val()) {
            Joyin.error("有*必填项未填写,请填写完整")
            return false;
        }
        if (ps !=='T03' && (!query("#priceShow") || !query("#matureYieldShow") || !query("#settleSpeed") || !query("#exerYieldShow"))) {
            Joyin.error("有*必填项未填写,请填写完整")
            return false;
        }
        var calculateField = query("input[name=calculateField]:checked").val();
        if(calculateField && calculateField === 'price' && !query("#priceShow").val()) {
            Joyin.error("净价不能为空")
            return false;
        }
        if(ps !=='T03' && (calculateField && calculateField === 'matureYield' && !query("#matureYieldShow").val())) {
            Joyin.error("到期收益率不能为空")
            return false;
        }
        if(ps !=='T03' && (calculateField && calculateField === 'exerYield' && !query("#exerYieldShow").val())) {
            Joyin.error("行权收益率不能为空")
            return false;
        }
        //组合也需要校验
        var checkFlag = true;
        var object = $.serializeToObject('#searchForm');
        var tradeAmt = query("#tradeAmt").val();
        var entrustNumAll = 0;
        for (var index in object.bussCombDistribtList) {
            var item = object.bussCombDistribtList[index];
            if(!item.portfolioId || !item.entrustNum || !item.businessModel) {
                checkFlag = false;
            }
            if(item.entrustNum) {
                entrustNumAll = entrustNumAll + Number(item.entrustNum);
            }

        }
        if(entrustNumAll <= 0) {
            Joyin.error("组合分配券面总额需要大于0！")
            return false;
        }
        if (!checkFlag) {
            Joyin.error("请填写完整组合信息")
            return false;
        }
        if(Number(tradeAmt) !== entrustNumAll) {
            Joyin.error("券面总额与组合分配处的券面总额不一致")
            return false;
        }
        if (riskFlag !== 0) {
            Joyin.error("请先完成风险试算")
            return false;
        }
        var obj = getSubmitParam("submit");
        var errorMsg = "";
        $.ajax({
            type: "POST",
            url: basePath + 'cfets/etrib/cfetsEtrIbBondinfo/checkSaveError',
            dataType: "json",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(obj.param),
            success: function (result) {
                if(result.code === '000') {
                    if(result.data && result.data.errorList && result.data.errorList.length > 0) {
                        $.each(result.data.errorList,function (index,item) {
                            errorMsg = errorMsg + item.message + "<br\>";
                        })
                    }
                }else {
                    errorMsg = result.message;
                }
            }
        });
        /*Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/checkSaveError', obj.param, function (result) {
            if(result.data && result.data.errorList && result.data.errorList.length > 0) {
                $.each(result.data.errorList,function (index,item) {
                    errorMsg = errorMsg + item.message + "<br\>";
                })
            }
        },false)*/
        if(errorMsg && errorMsg.length > 0) {
            Joyin.error(errorMsg);
            return false;
        }
        return true;
    }

    function saveOrSubmit(operateType) {
        var res = getSubmitParam(operateType)
        if(res.param) {
            if (operateType === 'save') {
                WorkFlow.doSaveList(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/save', res.param, "CfetsEtrIbBondinfo003_btn_save", null,
                    function () {
                        var index = window.parent.layer.getFrameIndex(window.name);
                        window.parent.layer.close(index);
                    })
            }
            //提交 走流程
            else if (operateType === 'submit') {
                WorkFlow.doSubmitList(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/save', res.param, "CfetsEtrIbBondinfo003_btn_submit", null,
                    function () {
                        var index = window.parent.layer.getFrameIndex(window.name);
                        window.parent.layer.close(index);
                    })
            }
         /*   if (operateType === "save") {
                WorkFlow.doSaveList(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/save', res.param, "CfetsEtrIbBondinfo003_btn_save", "CfetsEtrIbBondinfo003");
            } else {
                WorkFlow.doSubmitList(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/save', res.param, "CfetsEtrIbBondinfo003_btn_submit", "CfetsEtrIbBondinfo003");
            }*/
        }
    }

    // 绑定提交按钮
    $('#CfetsEtrIbBondinfo003_btn_submit').click(function () {
        if (paramCheck()) {
            saveOrSubmit('submit');
        }
    });

    // 绑定保存按钮
    $('#CfetsEtrIbBondinfo003_btn_save').click(function () {
        saveOrSubmit('save');
    });
    //绑定风险是算
    $('#CfetsEtrIbBondinfo003_btn_riskcal').click(function () {
        var object = $.serializeToObject('#searchForm');
        var param = JSON.parse(JSON.stringify(object));

        if(!param.tradeAmt  || !param.priceModel || !param.finprodName) {
            Joyin.error("请填写完整在进行风险试算");
            return;
        }
        if((!param.settleSpeed || !param.matureYield) && ps !=='T03') {
            Joyin.error("请填写完整在进行风险试算");
            return;
        }
        if(param.tradeAmt) {
            param.tradeAmt = param.tradeAmt * 10000;
        }
        if(param.entrustOwnNum) {
            param.entrustOwnNum = param.entrustOwnNum * 10000;
        }
        if(param.entrustIntegrateNum) {
            param.entrustIntegrateNum = param.entrustIntegrateNum * 10000;
        }
        if(param.matureYieldShow) {
            param.matureYield = param.matureYield / 100;
        }
        if(param.exerYield) {
            param.exerYield = param.exerYield / 100;
        }
        if(param.bussCombDistribtList) {
            $.each(param.bussCombDistribtList,function (index,item) {
                if(item.entrustNum) {
                    item.entrustNum = item.entrustNum * 10000;
                }
                if(item.entrustOwnNum) {
                    item.entrustOwnNum = item.entrustOwnNum * 10000;
                }
                if(item.entrustIntegrateNum) {
                    item.entrustIntegrateNum = item.entrustIntegrateNum * 10000;
                }
            })
        }
        if(param.riskTrialRetDto) {
            param.riskTrialRetDto = null;
        }
        $.ajax({
            type: "POST",
            url: basePath + 'cfets/etrib/cfetsEtrIbBondinfo/doRiskcal',
            dataType: "json",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(param),
            success: function (result) {
                if(result.code === '000') {
                    var data = result.data.data;
                    if(data.riskTrialSpan) {
                        $("#riskTrialSpan").html("&nbsp;&nbsp;" + data.riskTrialSpan);
                    }
                    entity_RiskTrialRetList = data.riskcalReturnList;
                    if(entity_RiskTrialRetList) {
                        $.each(entity_RiskTrialRetList, function (index, item) {
                            item.conclusion = data.riskTrialSpan;
                        })
                    }
                    tableRisk.clear();
                    tableRisk.addRow(entity_RiskTrialRetList);
                    //setRiskFlag(0);
                }else if(result.message){
                    Joyin.error(result.message);
                }
                setRiskFlag(0);//软控先
            }
        });
        /*Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/doRiskcal', param, function (data) {
            if(data.data.data.riskTrialSpan) {
                $("#riskTrialSpan").html("&nbsp;&nbsp;" + data.data.data.riskTrialSpan);
            }
            entity_RiskTrialRetList = data.data.data.riskcalReturnList;
            if(entity_RiskTrialRetList) {
                $.each(entity_RiskTrialRetList, function (index, item) {
                    item.conclusion = data.data.data.riskTrialSpan;
                })
            }
            tableRisk.clear();
            tableRisk.addRow(entity_RiskTrialRetList);
            setRiskFlag(0);
        },false);*/
    });

    query("#finprodName").change(function () {
        var data = query("#finprodName").select2('data')[0];
        if (data) {
            query("#finprodMarketId").val(data.id);
            query("#finprodIdhid").val(data.object.finprodId);
            query("#ccy").val(data.object.ccy);
            query("#finvdate").val(data.object.finvdate);
            query("#finmdate").val(data.object.finmdate);
            query("#isRight").val(data.object.isRight);
            query("#calendarId").val(data.object.calendarId);
            query("#tradeMarket").val(data.object.tradeMarket);
            query("#finprodAbbr").val(data.text);
            query("#assetType11").val(data.object.assetType11);
            if(data.object.assetType11 == '127' || data.object.assetType11 == '128') {
                $("input[name='calculateField'][value='matureYield']").attr('disabled', true);
                $("input[name='calculateField'][value='exerYield']").attr('disabled', true);
                query("#matureYieldShow").attr("disabled", true);
                query("#exerYieldShow").attr("disabled", true);
                query("#priceShow").attr("disabled", false);
                query("#calculateField_price").prop('checked', true);
                layui.form.render('radio');
            }else {
                $("input[name='calculateField'][value='matureYield']").attr('disabled', false);
                $("input[name='calculateField'][value='exerYield']").attr('disabled', false);
                query("#matureYieldShow").attr("disabled", false);
                query("#exerYieldShow").attr("disabled", true);
                query("#priceShow").attr("disabled", true);
                query("#calculateField_matureYield").prop('checked', true);
                layui.form.render('radio');
                if ("Y" == data.object.isRight) {
                    $("input[name='calculateField'][value='exerYield']").attr('disabled', false);
                    layui.form.render('radio');
                } else {
                    $("input[name='calculateField'][value='exerYield']").attr('disabled', true);
                    layui.form.render('radio');
                }
            }
            setRiskFlag(1);
            if (ps == 'T03') {//这里需要增加获取逻辑
                Joyin.ajax(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/queryCfetsBussCombDisList/'+data.object.finprodId,{},function (result) {
                    console.log(result);
                    if(result.data && result.data[0].entrustNum) {
                        query("#tradeAmt").val(JoyinMath.doDiv(result.data[0].entrustNum,1,2));
                    }
                },false)
            }
        }
        calAmountData();
        if (ps == 'T02' || ps == 'T03') {
            table.reload();
            var allRow = table.getAllRows();
            $.each(allRow, function (index, item) {
                item.portfolioId = null;
                item.proAvailableCash = 0;
                item.proAvailableCash1 = 0;
                item.secAcctAvailableCash = 0;
                item.porAvailableSec = 0;
                item.entrustIntegrateSecNum = 0;
                item.ownPorAvailableSec = 0;
                item.ownProAvailableSec1 = 0;
                table.renderRow(item.LAY_TABLE_INDEX, item);
            })
        }


    })
    query("#tradeAmt").change(function () {
        var tradeAmt = query("#tradeAmt").val();
        if(tradeAmt && Number(tradeAmt) <= 0) {
            Joyin.error("券面总额必须大于0");
            query("#tradeAmt").val("");
            return;
        }
        if(tradeAmt) {
            query("#tradeAmt").val(JoyinMath.doDiv(tradeAmt, 1, 2));
            query("#entrustOwnNum").val(JoyinMath.doDiv(tradeAmt, 1, 2));
            calAmountData();
            setRiskFlag(1);
        }
    })
    query("#entrustIntegrateNum").change(function () {
        var tradeAmt = query("#tradeAmt").val();
        var entrustIntegrateNum = query("#entrustIntegrateNum").val();
        if (tradeAmt && entrustIntegrateNum) {
            query("#entrustOwnNum").val(JoyinMath.doMinus(tradeAmt, entrustIntegrateNum, 2));
            calAmountData();
        }
        setRiskFlag(1);
    })

    query("input[name=calculateField]").click(function () {
        calflag = query("input[name=calculateField]:checked").val();
        query("#" + calflag + "Show").attr("disabled", false);
        if ('price' === calflag) {
            query("#matureYieldShow").attr("disabled", true);
            query("#exerYieldShow").attr("disabled", true);
        }
        if ('matureYield' === calflag) {
            query("#exerYieldShow").attr("disabled", true);
            query("#priceShow").attr("disabled", true);
        }
        if ('exerYield' === calflag) {
            query("#matureYieldShow").attr("disabled", true);
            query("#priceShow").attr("disabled", true);
        }
        calAmountData();
        setRiskFlag(1);
    })

    query('#matureYieldShow').change(function () {
        if(query('#matureYieldShow').val()) {
            setRiskFlag(1);
            query('#matureYield').val(query('#matureYieldShow').val());
            calData('matureYield');
        }
    })
    query('#exerYieldShow').change(function () {
        if(query('#exerYieldShow').val()) {
            setRiskFlag(1);
            query('#exerYield').val(query('#exerYieldShow').val());
            calData('exerYield');
        }
    })
    query('#priceShow').change(function () {
        setRiskFlag(1);
        query('#price').val(query('#priceShow').val());
        if (ps != 'T03') {
            calData('price');
        }
    })

    if (ps == 'T03') {//回售时特殊处理
        query("#calculateField_price").attr("title", "行权净价");
        query("#settleSpeedDiv").hide();
        query("#entrustOwnNumDiv").hide();
        query("#entrustIntegrateNumDiv").hide();
        query("#matureYieldDiv").hide();
        query("#exerYieldDiv").hide();
        query("#priceModel").attr("disabled", true);
        layui.form.render();
    }
    if(ps == 'T01' || ps == 'T02') {
        var assetType11 = query("#assetType11").val();
        if(assetType11 && (assetType11 == '127' || assetType11 == '128')) {
            $("input[name='calculateField'][value='matureYield']").attr('disabled', true);
            $("input[name='calculateField'][value='exerYield']").attr('disabled', true);
            query("#matureYieldShow").attr("disabled", true);
            query("#exerYieldShow").attr("disabled", true);
            query("#priceShow").attr("disabled", false);
            query("#calculateField_price").prop('checked', true);
            layui.form.render('radio');
        }
    }

    //非卖空模式
    if (SHORT_SELLING_SUPPORTED && SHORT_SELLING_SUPPORTED != '02') {
        query("#entrustOwnNumDiv").hide();
        query("#entrustIntegrateNumDiv").hide();
    }


}());
