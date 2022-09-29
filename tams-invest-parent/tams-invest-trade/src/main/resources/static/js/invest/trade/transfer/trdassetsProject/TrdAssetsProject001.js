(function () {
    var projectListInfoList = entity.projectListInfoList;
    var buttons = [{text: '&#xe640;', ignore: true, clas: 'delt', title: '删除'}];


    /**
     * 列表信息（Total）
     */
    var options = {
        page: false,
        height: 200,
        /**
         * 定义表头
         */
        cols: [
            // 组合代码
            {field: 'portfolioId', width: 150, title: '组合代码', sortable: false}
            // 组合名称
            , {field: 'portfolioName', width: 150, title: '组合名称', sortable: false}
            //资产代码
            , {field: 'finprodMarketId', width: 150, title: '资产代码', digit: 2, sortable: false}
            // 交易日期
            , {field: 'tradeDate', width: 150, title: '交易日期', type: 'date', sortable: false}
            /* // 交割日期
             , {field: 'settleDate',width: 150,title: '交割日期',type: 'date', sortable: false}*/
            // 本金金额
            , {field: 'prinAmt', width: 150, title: '本金金额', type: 'money', digit: 2, sortable: false}
            // 计提利率%
            , {field: 'investRate', width: 100, title: '计提利率%', type: 'number', digit: 4, sortable: false}
            // 应计利息总额
            , {field: 'splitIntAmt', width: 150, title: '应计利息总额', type: 'money', digit: 2, sortable: false}
            // 交易金额
            , {field: 'splitTradeAmt', width: 150, title: '交易金额', type: 'money', digit: 2, sortable: false}
            //交易金额--隐藏
            , {field: 'tradeAmt', width: 150, title: '交易金额', type: 'money', digit: 2, sortable: false, visible: false}
            //交易编号
            , {field: 'tradeId', width: 150, title: 'tradeId', sortable: false, visible: false}
            // 投资目的
            , {field: 'invAim', width: 100, title: 'invAim', sortable: false, visible: false}
            // 净价总额
            , {
                field: 'splitCprice',
                width: 150,
                title: '净价总额',
                type: 'money',
                digit: 2,
                sortable: false,
                visible: false
            }
            , {field: 'fileArrayName', title: '附件名', width: 200}//附件名称
            , {field: 'businessId', visible: false }//附件编号
            // 待转让本金
            //, {field: 'assignPrinamt',width: 150,title: '待转让本金',type: 'money', digit: 2, sortable: false}
            // 待转让利息
            //, {field: 'assignIntamt',width: 150,title: '待转让利息',type: 'money', digit: 2, sortable: false}
            //操作
            //,{field:'operation',title: 'SysCommon.operation',type:'action',buttons: buttons}
            , {
                title: 'SysCommon.operation', field: 'operation', type: 'action', width: 50
                , buttons: [
                    {text: '&#xe67c;', clas: 'uploadFile', title: '上传'}
                    , {text: '&#xe640;', clas: 'delt', title: '删除'}
                    , {field: 'sumitSeq', visible: false}]
            }
        ]
    };
    if (formTodoList) {
        options.data = entityData;
    }
    var projectListInfoList = $("#projectListInfoList").joyintable(options);

    projectListInfoList.bind('click', '.delt', function (data, index) {
        projectListInfoList.deleteRow(index);
        var unSplitPrinAmt = $("#unSplitPrinAmt").val();
        $("#unSplitPrinAmt").val(Number(unSplitPrinAmt) + Number(data.prinAmt)).trigger('blur');
        calUnSplitPrinAmt();
    });

    var table = $("#projectListInfoList").joyintable(options);
    //附件上传
    table.bind('click', '.uploadFile', function (data) {
        //if ('2' == pageModel) {
        //    return;
        //}
        data.operateType = "edit";
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {};
        object.rowData = JSON.stringify({});
        object.businessId = data.businessId;
        Joyin.dialog("上传附件",
            JoyinUtil.getRealPath("component/entrust/EtrListUploadFile/init"),
            "88%", "85%", object,
            function (result) {
                var fileArrayName = '';
                if (result && result.fileArray && _.isArray(result.fileArray) && result.fileArray.length > 0) {
                    for (var index in result.fileArray) {
                        fileArrayName += result.fileArray[index].srcName + '，';
                    }
                    result.fileArrayName = fileArrayName.substr(0, fileArrayName.length - 1);
                }
                table.renderRow(data.LAY_TABLE_INDEX, {"businessId": result.businessId, "fileArrayName": result.fileArrayName});
            });
    });

    //风险测试数据
    var getriskdata = function () {
        var data = projectListInfoList.getAllRows();
        var object = {};
        var list = []
        if (data != null && data != "") {
            for (var i in data) {
                var obj = {
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: new Date(data[i].settleDate).format("yyyy-MM-dd"),
                    portfolioCode: data[i].portfolioId,
                    tradeAmount: data[i].splitTradeAmt,
                    assetType: FINPROD_TYPE_F18,
                    isEnabled: '01',
                    assetCode: data[i].finprodId,
                    tradeDirection: T01
                };
                list[i] = obj;
            }
            if ($('#projectType').val() && $('#projectType').val() != '') {
                obj.assetSecType = $('#projectType').val();
            }
            object = {
                assetCode: ['finprodMarketId', 'TrdBondInvestTrade002.finprodId'],
                valDate: ['settleDate', 'TrdPlanRateInvestTrade002.settleDate'],
                portfolioCode: ['portfolioId', 'TrdBondInvestTrade002.portfolioId'],
                tradeAmount: ['tradeAmt', 'TrdProjectInvestTrade002.fpriceAmt'],
                assetType: ['projectType', 'TrdDepositInvestTrade001.finprodType'],
                tradeDirection: ['ps', 'TrdLendInvestTrade002.ps'],
                data: list
            };
        }
        return object;
    };

    function saveOrSubmit(operateType) {
        if (typeof (BaseRiskMathComm) != 'undefined') {
            if (BaseRiskMathComm) {
                s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
                if (!s && s != '') {
                    return;
                }
                if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())) {
                    return;
                }
            }
        }
        var objects = getriskdata();
        var object = $.serializeToObject('#TrdAssetsProject001_form', true);
        object.pageModel = pageModel;
        object.operateType = operateType;
        Joyin.ajaxJson(basePath + '/invest/trade/transfer/trdassetsProject/TrdAssetsProject001/saveCheck', object, function (result) {
            Joyin.confirm(result.data, function () {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + '/invest/trade/transfer/trdassetsProject/TrdAssetsProject001/save', object, "TrdAssetsProject001_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + '/invest/trade/transfer/trdassetsProject/TrdAssetsProject001/save', object, "TrdAssetsProject001_btn_submit");
                }
            });
        }, false);
    };
    var changeSplitPrinAmt = function () {
        $('#splitIntAmt').val('');//应计利息总额
        // $("#unitIntAmt").val('');//单位应计利息
        // $("#unitFprice").val('');//全价
        var finprodId = "F18_" + $("#finprodMarketId").val();
        var object = {
            portfolioId: $('#portfolioId').val(),
            finprodId: finprodId,
            settleDate: $('#settleDate').val(),//交割日期
            basis: $('#basis').val(),//计息基础
            investRate: $('#investRate').val().valueOf() / 100,//净投资收益率
            prinAmt: $('#splitPrinAmt').val()
        }
        //update by xr for 分段计息 start
        if (object.finprodId && object.settleDate && object.prinAmt) {
            //update by xr for 分段计息 end
            Joyin.ajaxJson(basePath + "/invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getIntAmt", object, function (result) {
                $('#splitIntAmt').val(result.data).trigger("blur");
                //加上精度计算，  yjm 20190725
                // var unitIntAmt = JoyinMath.doDiv(JoyinMath.doMul(result.data*1, 100, 6), object.prinAmt*1, 4);
                // $("#unitIntAmt").val(unitIntAmt).trigger('blur');
                // $("#unitFprice").val(JoyinMath.doAdd(unitIntAmt, 100, 6)).trigger('blur');
                //卖出时减去手续费 yjm start 20190903
                // var commissionAmt = $('#commissionAmt').val();
                // var ps = $('#ps').val();
                //if (ps && ps != T01) {
                //卖出
                // commissionAmt = commissionAmt * -1;
                //}
                //卖出时减去手续费 yjm start 20190903
                //$("#tradeAmt").val(JoyinMath.doAdd(JoyinMath.doAdd(result.data*1, object.prinAmt*1), commissionAmt, 6)).trigger("blur");
                $("#splitTradeAmt").val(JoyinMath.doAdd(result.data * 1, object.prinAmt * 1)).trigger("blur");
                //
                var splitPrinAmt = $('#splitPrinAmt').val();
                $("#splitCprice").val(JoyinMath.doMul(splitPrinAmt, 100));//净价总额
            });
        }
    };
    var changeSplitIntAmt = function () {
        $("#splitTradeAmt").val(JoyinMath.doAdd($("#splitIntAmt").val(), $("#splitPrinAmt").val())).trigger("blur");
    };
    /**
     * 添加按钮
     */
    var addAction = function () {
        var object = $.serializeToObject('#TrdAssetsProject001_form', true);
        var flag = false;
        var rows = projectListInfoList.getAllRows();
        var portfolioId = $("#portfolioId").val();
        var finprodId = "F18_" + $("#finprodMarketId").val();
        var portfolioName = $("#portfolioId").find("option:selected").text();
        object.portfolioName = portfolioName;
        object.finprodId = finprodId;
        object.turnInvAim = $('#turnInvAim').val();
        $.each(rows, function (index, data) {
            if (portfolioId == data.portfolioId) {
                Joyin.alert("产品组合：" + portfolioId + "在转仓列表已存在！");
                flag = true;
            }
        });
        if (!flag) {
            Joyin.ajaxJson(basePath + '/invest/trade/transfer/trdassetsProject/TrdAssetsProject001/addCheck', object, function (result) {
                if (result && result.statuCode == 200) {
                    var row = result.data.projectListInfoList[0];
                    row.invAim = $('#turnInvAim').val();
                    var totalPrinamt = $("#prinAmt").val();
                    var totalAssignIntamt = $("#assignIntamt").val();
                    if (Number($('#unSplitPrinAmt').val()) - Number(row['prinAmt']) == 0) {
                        var assignAmt = 0;
                        $.each(projectListInfoList.getAllRows(), function (index, item) {
                            assignAmt += Number(item['assignIntamt']);
                        });
                        row['assignIntamt'] = JoyinMath.doMinus(Number(totalAssignIntamt), assignAmt);
                    } else {
                        row['assignIntamt'] = Number(totalPrinamt) == 0 ? 0 : JoyinMath.doMul(totalAssignIntamt, JoyinMath.doDiv(row['prinAmt'], totalPrinamt, 20), 2);
                    }
                    //row['assignPrinamt'] = 0;
                    projectListInfoList.addRow(row);
                    var unSplitPrinAmt = $("#unSplitPrinAmt").val();
                    var splitPrinAmt = $("#splitPrinAmt").val();
                    $("#unSplitPrinAmt").val(Number(unSplitPrinAmt) - Number(splitPrinAmt)).trigger('blur');
                    calUnSplitPrinAmt();
                    //清空转仓信息的信息
                    $("#portfolioId").val('').trigger("change").trigger('blur');
                    $("#splitPrinAmt").val('').trigger("change").trigger('blur');
                    $("#splitIntAmt").val('').trigger("change").trigger('blur');
                    $("#splitTradeAmt").val('').trigger("change").trigger('blur');

                    // $("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
                    // $("#bussTrdAvailableCashQuery_avalibleCashT1").val('').trigger("change").trigger('blur');
                    // $("#bussTrdAvailableCashQuery_channelAvailableCash").val('').trigger("change").trigger('blur');

                    $("#portoAvailableAmt").val('').trigger("change").trigger('blur');
                }
            });
        }
    };
    var calUnSplitPrinAmt = function () {
        var splitSumPrinAmt = 0;
        var allSplitSumPrinAmt = 0;
        var splitSumTradeAmt = 0;
        var prinAmt = $("#prinAmt").val();
        var alSplitPrinAmt = $("#alSplitPrinAmt").val();
        $.each(projectListInfoList.getAllRows(), function (index, data) {
            if ($("#portfolioId").val() && $("#portfolioId").val() == data.portfolioId) {
                splitSumPrinAmt = JoyinMath.doAdd(splitSumPrinAmt, data.prinAmt, 2);
                splitSumTradeAmt = JoyinMath.doAdd(splitSumTradeAmt, data.splitTradeAmt, 2);
            }
            allSplitSumPrinAmt = JoyinMath.doAdd(allSplitSumPrinAmt, data.prinAmt, 2);

        });
        // var avalibleCash = $("#bussTrdAvailableCashQuery_avalibleCash");
        // var channelAvailableCash = $("#bussTrdAvailableCashQuery_channelAvailableCash");
        // var assetPosition = $("#bussTrdAvailableCashQuery_assetPosition");
        //$("#unSplitPrinAmt").val(JoyinMath.doMinus(JoyinMath.doMinus(prinAmt,allSplitSumPrinAmt,2),alSplitPrinAmt,2)).trigger("blur");
        // $("#bussTrdAvailableCashQuery_avalibleCash").val(JoyinMath.doMinus(avalibleCash,splitSumTradeAmt,2)).trigger("blur");
        // $("#bussTrdAvailableCashQuery_channelAvailableCash").val(JoyinMath.doMinus(channelAvailableCash,splitSumTradeAmt,2)).trigger("blur");
        // $("#bussTrdAvailableCashQuery_assetPosition").val(JoyinMath.doMinus(assetPosition,splitSumPrinAmt,2)).trigger("blur");
        var avalibleCash = $("#portoAvailableAmt");
        $("#portoAvailableAmt").val(JoyinMath.doMinus(avalibleCash,splitSumTradeAmt,2)).trigger("blur");
    };
    var changePortfolioId = function () {
        refreshCashAndBalance();
    };
    /**
     * 刷新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
     */
    var refreshCashAndBalance = function () {
        $('#portoAvailableAmt').val('').change().trigger('blur');
        if ($('#portfolioId').val()) {
            var object = $('#portfolioId').select2("data")[0].object;
            if (object && object.availableCash) {
                $('#portoAvailableAmt').val(object.availableCash).change().trigger('blur');
            }
        }

        //debugger;
        // var params = {};
        // params.portfolioId = $("#portfolioId").val();
        // if(!params.portfolioId){
        // 	// $("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
        //     // $("#bussTrdAvailableCashQuery_avalibleCashT1").val('').trigger("change").trigger('blur');
        // }
        // params.calendarId = $("#calendarId").val();
        // params.ccy = $("#ccy").val();
        // // 通道交易 新增 强成西 2019/03/26 start
        // params.chlId = $("#chlId").val();
        // // 通道交易 新增 强成西 2019/03/26 end
        // params.tradeDate = $("#tradeDate").val();
        // var finprodId= "F18_"+$("#finprodMarketId").val();
        // params.finprodId =finprodId;
        // if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
        //     window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
        //         return params
        //     });
        // }
    };
    var findAvaTransferPortfolioInfo = function () {
        var listInfoList = [];
        var allRows = projectListInfoList.getAllRows();
        for (var i in allRows) {
            var obj = {
                portfolioId: allRows[i].portfolioId,
                tradeAmt: allRows[i].splitTradeAmt
            };
            listInfoList[i] = obj;
        }
        Joyin.dialog("待转仓产品组合信息", basePath + "/invest/trade/transfer/common/TransferCommon001/init", '60%', '60%', {
            portfolioId: $("#invTransPortf").val(),
            chlId: $("#chlId").val(),
            tradeDate: $("#tradeDate").val(),
            ccy: $("#ccy").val(),
            listInfoList: JSON.stringify(listInfoList)
        })
    };

    /**
     * 画面加载完成
     */
    $(function () {
        //列表信息加载
        projectListInfoList.load();
        //转仓信息  本金金额输入联动
        $("#splitPrinAmt").change(function () {
            changeSplitPrinAmt()
        });
        //转仓信息  应计利息总额输入联动
        $("#splitIntAmt").change(function () {
            changeSplitIntAmt()
        });
        //交易金额改变
        $("#splitTradeAmt").change(function () {
            if ($("#splitTradeAmt").val() && $("#splitPrinAmt").val()) {
                $("#splitIntAmt").val(Number($("#splitTradeAmt").val()) - Number($("#splitPrinAmt").val())).trigger("change").trigger('blur');
            }
        })
        //组合代码改变
        $("#portfolioId").change(function () {
            changePortfolioId()
        });
        //添加按钮按下
        $("#TrdAssetsProject001_btn_add").click(function () {
            addAction();
        });
        //待转仓产品组合查询
        $("#TrdAssetsProject001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        // 绑定按钮事件
        $('#TrdAssetsProject001_btn_save').click(function () {
            saveOrSubmit('save');
        });
        $('#TrdAssetsProject001_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
        //风险试算
        $('#TrdAssetsProject001_btn_risk').click(function () {
            var data = projectListInfoList.getAllRows();
            if (data != null && data != '') {
                var object = $.serializeToObject('#TrdAssetsProject001_form', true);
                object.portfolioId = $("#invTransPortf").val();
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for (var i = 0; i < bussTrdTransfenpeiDto.length; i++) {
                    bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                    delete bussTrdTransfenpeiDto[i].portfolioId;
                    bussTrdTransfenpeiDto[i].ps = T02;
                    if (bussTrdTransfenpeiDto[i].splitTradeAmt) {
                        bussTrdTransfenpeiDto[i].tradeAmt = bussTrdTransfenpeiDto[i].splitTradeAmt;
                    }
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F06', object, getriskdata());
            } else {
                Joyin.error("拆分列表信息不能为空！");
            }
        });
    });
    // window.onload =function () {
    //         $("#bussTrdAvailableCashQuery_div_T1cash").hide();
    //         $("#bussTrdAvailableCashQuery_avalibleCash").parent().prev().html("组合可用现金");
    //         if(chlId && chlId != null){
    //         	$("#bussTrdAvailableCashQuery_div_channelCash").show();
    //         }
    //         $("#bussTrdAvailableCashQuery_div_position").hide();
    // }
    window.componentLoadCallback = function (isDone, pagePath, params) {
        if (isDone) {
            if (typeof (BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskparam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
            }
            // $("#bussTrdAvailableCashQuery_div_T1cash").hide();
            // $("#bussTrdAvailableCashQuery_avalibleCash").parent().prev().html("组合可用现金");
            //if(chlId && chlId != null){
            //    $("#bussTrdAvailableCashQuery_div_channelCash").show();
            //}
            //$("#bussTrdAvailableCashQuery_div_position").hide();

            // $("#bussTrdAvailableCashQuery_div_cash_T1").hide();
            // $("#bussTrdAvailableCashQuery_div_channelCash").hide();
            // $("#bussTrdAvailableCashQuery_div_position").hide();


            if (window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder']) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    var entityList = getEntity();
                    for (i = 0; i < entityList.length; i++) {
                        entityList[i].finprodType2 = $("#finprodType2").val();
                        entityList[i].ps = $("#ps").val();
                        entityList[i].ccy = $("#ccy").val();
                    }
                    return {
                        'isTotalTrade': 'N'
                        , 'bankLendingType': null
                        , 'portfolioId': $("#invTransPortf").val()
                        , 'isUsePageAcct': false
                        , 'deal': entityList
                        , 'jiaoyiType': 'dzh'
                        , 'isLimitAcct': 'Y'
                    };
                });
            }

        }
    };

    function getEntity() {
        // 转出
        var object = $.serializeToObject('#TrdAssetsProject001_form');

        var tempList = projectListInfoList.getAllRows();
        for (i = 0; i < tempList.length; i++) {
            tempList[i].tradeAmt = tempList[i].splitTradeAmt;
        }

        // 转入组合
        object.dataList = JSON.stringify(tempList);
        var entityList;
        Joyin.ajaxSync(basePath + '/invest/trade/transfer/trdassetsProject/TrdAssetsProject001/getEntity', object, function (result) {
            entityList = result;
        });
        return entityList;
    }
}());