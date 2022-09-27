$(function () {
    var buttons = [
        {text: '&#xe67c;', clas: 'uploadFile', title: '上传'},
        {text: '&#xe640;', ignore: true, clas: 'delt', title: '删除'}
    ];


    //风险试算数据获取
    var getriskdata = function () {
        var allRows = funAssetTransferDtoListTable.getAllRows();
        var list = [];
        var object = {};
        if (allRows) {
            for (var i in allRows) {
                var obj = {
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: $('#fundAssetTransfer001_tradeDate').val(),
                    portfolioCode: allRows[i].portfolioId,
                    tradeAmount: allRows[i].tradePrin,
                    assetType: FINPROD_TYPE_F06,
                    isEnabled: '01',
                    assetCode: $("#finprodIdOrg").val(),
                    tradeDirection: PS_T01
                }
                list[i] = obj;
            }
            object = {
                valDate: ['tradeDate', 'TrdFundInvestTrade002.tradeDate'],
                portfolioCode: ['portfolioId', 'TrdFundInvestTrade002.portfolioId'],
                tradeAmount: ['tradePrin', 'TrdFundInvestTrade002.tradeSha'],
                assetType: ['assetType', 'TrdFundInvestTrade002.type6'],
                tradeDirection: ['ps', 'TrdFundInvestTrade002.ps'],
                data: list
            }
        }
        return object;
    }
    /**
     * 初始化
     */
    var init = function () {
        if (fundType && fundType == FUND_ASSET_TYPE_F01) {
            $("#fundAssetTransfer001_unitCprice").trigger('blur').prop("disabled", true);
        } else {
            $("#fundAssetTransfer001_unitCprice").trigger('blur').prop("disabled", false);
        }
    };
    window.componentLoadCallback = function (isDone, pagePath, params) {
        if (isDone) {
            if (typeof (BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskparam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
            }
            // if ($("#chlId").val()) {
            //     $("#bussTrdAvailableCashQuery_div_channelCash").show();
            // } else {
            //     $("#bussTrdAvailableCashQuery_div_cash").show();
            // }
            $("#bussTrdAvailableCashQuery_div_cash").show();
            $("#bussTrdAvailableCashQuery_div_position").hide();
            $("#bussTrdAvailableCashQuery_avalibleCashT1").parent().parent().hide();
            $("#bussTrdAvailableCashQuery_avalibleCash").parent().prev().html("组合可用现金");

            //隐藏 合作资产可用现金
            $("#bussTrdAvailableCashQuery_channelAvailableCash").parent().parent().hide();

            if (window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder']) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    var entityList = getEntity();
                    for (i = 0; i < entityList.length; i++) {
                        entityList[i].finprodType2 = $("#finprodType2").val();
                        entityList[i].ps = $("#ps").val();
                        entityList[i].ccy = $("#ccy").val();
                    }
                    console.log("-----------entityList--------------")
                    console.log(entityList)
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
        var object = $.serializeToObject('#fundAssetTransfer_form');
        // 指令
        object.dataList = JSON.stringify(funAssetTransferDtoListTable.getAllRows());
        var entityList;
        Joyin.ajaxSync(basePath + 'invest/trade/transfer/fund/FundAssetTransfer001/getEntity', object, function (result) {
            entityList = result;
        });
        console.log(entityList);
        return entityList;
    }

    var funAssetTransferDtoListTable = $("#fundAssetTransfer001_funAssetTransferDtoList").joyintable({
        page: false,
        data: entityData ? entityData : [],
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
            , {field: 'finprodMarketId', width: 150, title: '资产代码', sortable: false}
            // 交易日期
            , {field: 'tradeDate', width: 150, title: '交易日期', type: 'date', digit: 2, sortable: false}
            // 交易份额
            , {field: 'tradePrin', width: 150, title: '交易份额', type: 'money', digit: 2, sortable: false}
            //单位净值
            , {field: 'unitCprice', width: 150, title: '单位净值', type: 'money', digit: 4, sortable: false}
            //待结转份额
            , {field: 'carryOverQty', width: 150, title: '待结转份额', type: 'money', digit: 2, sortable: false}
            //交易金额
            , {field: 'tradeAmt', width: 150, title: '交易金额', type: 'money', digit: 2, sortable: false}
            //附件名
            , {field: 'fileArrayName', width: 200, title: '附件名', sortable: false}
            , {field: 'businessId', visible: false }//附件编号
            //交易编号
            , {field: 'tradeId', width: 150, title: 'tradeId', sortable: false, visible: false}
            //
            , {field: 'finprodId', width: 150, title: 'finprodId', sortable: false, visible: false}
            //操作
            , {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: buttons}
        ], done: function () {
            this.bind('click', '.delt', function (data, index) {
                funAssetTransferDtoListTable.deleteRow(index);
                refreshCashAndBalance();
                calRollablePrin();
                $("#fundAssetTransfer001_rollablePrin").val(Number($("#fundAssetTransfer001_rollablePrin").val()) + Number(data.tradePrin)).trigger('blur');
            });
        }
    });

    //附件上传
    funAssetTransferDtoListTable.bind('click', '.uploadFile', function (data) {
        //if ('2' == pageModel) {
        //    return;
        //}
        data.operateType = "edit";
        data.pageModel = PAGE_MODEL_EDIT;
        data.baseTradeInfoDtos = null;
        data.bussTrdTransferOrder = null;
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
                funAssetTransferDtoListTable.renderRow(data.LAY_TABLE_INDEX, {"businessId": result.businessId, "fileArrayName": result.fileArrayName});
            });
    });

    /**
     * 刷新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
     */
    var refreshCashAndBalance = function () {
        console.log("---------refreshCashAndBalance---------")
        if ($('#fundAssetTransfer001_portfolioId').val()) {
            var object = $('#fundAssetTransfer001_portfolioId').select2("data")[0].object;
            console.log(object)
            if (object && object.availableCash) {
                $('#portoAvailableAmt').val(object.availableCash).change().trigger('blur');
            } else {
                $('#portoAvailableAmt').val('').change().trigger('blur');
            }
        } else {
            $('#portoAvailableAmt').val('').change().trigger('blur');
        }

        // var params = {};
        // params.portfolioId = $("#fundAssetTransfer001_portfolioId").val();
        // params.calendarId = $("#calendarId").val();
        // params.ccy = $("#fundAssetTransfer001_ccy").val();
        // params.chlId = $("#fundAssetTransfer001_chlId").val();
        // params.tradeDate = $("#fundAssetTransfer001_tradeDate").val();
        // params.finprodId = $("#finprodIdOrg").val();
        // if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
        //     window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
        //         return params
        //     });
        // }
        // if (!$('#fundAssetTransfer001_portfolioId').val()) {
        //     $('#bussTrdAvailableCashQuery_avalibleCash').val('').trigger('change');
        // }
    };

    //组合代码change事件
    var portfolioIdChange = function () {
        var object = $('#fundAssetTransfer001_portfolioId').select2("data")[0].object;
        if (object) {
            //联动带出待结转份额
            var carryOverQty = object.carryOverQty;
            if (carryOverQty && carryOverQty > 0) {
                $("#fundAssetTransfer001_carryOverQty").val(carryOverQty).change().trigger('blur');
            } else {
                $("#fundAssetTransfer001_carryOverQty").val('').change().trigger('blur');
            }
        } else {
            $("#fundAssetTransfer001_carryOverQty").val('').change().trigger('blur');
        }

        // calTradeAmt();
        refreshCashAndBalance();
        $("#fundAssetTransfer001_tradePrin").val('').trigger("change").trigger('blur');
    };

    //保存或提交按钮按下
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
        var object = $.serializeToObject('#fundAssetTransfer_form', true);
        object.pageModel = pageModel;
        object.operateType = operateType;
        Joyin.ajaxJson(basePath + '/invest/trade/transfer/fund/FundAssetTransfer001/saveCheck', object, function (result) {
            Joyin.confirm(result.data, function () {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + '/invest/trade/transfer/fund/FundAssetTransfer001/save', object, "FundAssetTransfer001_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + '/invest/trade/transfer/fund/FundAssetTransfer001/save', object, "FundAssetTransfer001_btn_submit");
                }
            });
        });
    };
    //联动交易金额
    var calTradeAmt = function () {
        var tardeAmt = 0;
        var carryOverQty = $("#fundAssetTransfer001_carryOverQty").val();//待结转份额
        var tradePrin = $("#fundAssetTransfer001_tradePrin").val();//交易份额
        var unitCprice = $("#fundAssetTransfer001_unitCprice").val();//单位净值
        if (unitCprice && tradePrin) {
            tardeAmt = JoyinMath.doAdd(JoyinMath.doMul(tradePrin, unitCprice), carryOverQty, 2);
            $("#fundAssetTransfer001_tradeAmt").val(tardeAmt);
        } else {
            $("#fundAssetTransfer001_tradeAmt").val('');
        }
    };
    //添加按钮
    var doAddAction = function () {
        if (!$('#fundAssetTransfer001_portfolioId').val()) {
            Joyin.error("请输入组合");
            return;
        }
        if (!$('#fundAssetTransfer001_tradePrin').val()) {
            Joyin.error("请输入交易份额");
            return;
        }
        if (!$('#fundAssetTransfer001_unitCprice').val()) {
            Joyin.error("请输入单位净值");
            return;
        }
        if ($('#fundAssetTransfer001_unitCprice').val() <= 0) {
            Joyin.error("单位净值必须大于0");
            return;
        }
        var object = $.serializeToObject('#fundAssetTransfer_form', true);
        var flag = false;
        var rows = funAssetTransferDtoListTable.getAllRows();
        var portfolioId = $("#fundAssetTransfer001_portfolioId").val();
        var portfolioName = $("#fundAssetTransfer001_portfolioId").find("option:selected").text();
        object.portfolioName = portfolioName;
        $.each(rows, function (index, data) {
            if (portfolioId == data.portfolioId) {
                Joyin.alert("产品组合：" + portfolioId + "在转仓列表已存在！", 2);
                flag = true;
            }
        });
        if (!flag) {
            Joyin.ajaxJson(basePath + '/invest/trade/transfer/fund/FundAssetTransfer001/addCheck', object, function (result) {
                if (result && result.statuCode == 200) {
                    listInfoDtos = result.data.funAssetTransferDtoList;
                    funAssetTransferDtoListTable.addRow(listInfoDtos[0]);
                    calRollablePrin();
                    $("#fundAssetTransfer001_rollablePrin").val(Number($("#fundAssetTransfer001_rollablePrin").val()) - Number($("#fundAssetTransfer001_tradePrin").val())).trigger('blur');
                    $("#fundAssetTransfer001_portfolioId").val('').trigger("change").trigger('blur');
                    portfolioIdChange();
                }
            });
        }
    };
    //计算可转仓份额
    var calRollablePrin = function () {
        var rollSumPrin = 0;
        var rollSumTradeAmt = 0;
        var tradePrinOrg = $("#fundAssetTransfer001_tradePrinOrg").val();
        //var rollablePrin = $("#fundAssetTransfer001_rollablePrin").val();
        $.each(funAssetTransferDtoListTable.getAllRows(), function (index, data) {
            if ($("#fundAssetTransfer001_portfolioId").val() && $("#fundAssetTransfer001_portfolioId").val() == data.portfolioId) {
                rollSumTradeAmt = JoyinMath.doAdd(rollSumTradeAmt, data.tradeAmt, 2);
            }
            rollSumPrin = JoyinMath.doAdd(rollSumPrin, data.tradePrin, 2);
        });
        var avalibleCash = $("#bussTrdAvailableCashQuery_avalibleCash").val();
        //$("#fundAssetTransfer001_rollablePrin").val(JoyinMath.doMinus(rollablePrin,rollSumPrin,2)).trigger("blur");
        $("#bussTrdAvailableCashQuery_avalibleCash").val(JoyinMath.doMinus(avalibleCash, rollSumTradeAmt, 2)).trigger("blur");
    };

    var findAvaTransferPortfolioInfo = function () {
        var listInfoList = [];
        var allRows = funAssetTransferDtoListTable.getAllRows();
        for (var i in allRows) {
            var obj = {
                portfolioId: allRows[i].portfolioId,
                tradeAmt: allRows[i].tradeAmt
            };
            listInfoList[i] = obj;
        }
        Joyin.dialog("待转仓产品组合信息", basePath + "/invest/trade/transfer/common/TransferCommon001/init", '60%', '60%', {
            portfolioId: $("#invTransPortf").val(),
            chlId: $("#fundAssetTransfer001_chlId").val(),
            tradeDate: $("#fundAssetTransfer001_tradeDate").val(),
            ccy: $("#fundAssetTransfer001_ccy").val(),
            secAcctId: $("#secManageAcctId").val(),
            listInfoList: JSON.stringify(listInfoList)
        })
    };

    var initcalculate = function () {
        console.log("--------------initcalculate--------------------")
        var unitCprice = $("#fundAssetTransfer001_unitCprice").val();
        if (unitCprice && unitCprice > 0) {
            $("#fundAssetTransfer001_unitCprice").val(unitCprice).change().trigger('blur')
        }
    };

    var calculateDeviationDegree = function () {
        console.log("--------------deviationDegree--------------------")
        var fundType = $("#fundType").val();
        var unitCprice = $("#fundAssetTransfer001_unitCprice").val();

        if (FUND_ASSET_TYPE_F02 == fundType && unitCprice > 0) {
            var object = {};
            object.finprodId = $("#fundAssetTransfer001_finprodMarketId").val();
            object.unitPrice = unitCprice;
            Joyin.ajaxJson(basePath + '/invest/trade/transfer/fund/FundAssetTransfer001/calculateDeviationDegree', object,
                function (result) {
                    if (result && result.data) {
                        var deviationDegree = result.data.deviationDegree;
                        $("#deviationDegree").text(deviationDegree + '%');
                    }
                });
        }
    };

    /**
     * 画面加载完成
     */
    $(function () {
        //初始化
        init();
        //加载列表
        funAssetTransferDtoListTable.load();
        //组合change事件
        $("#fundAssetTransfer001_portfolioId").change(function () {
            //1.组合选中时净值回归默认
            var unitCprice = $("#fundAssetTransfer001_unitCprice").attr("value");
            if (unitCprice == undefined) {
                unitCprice = "";
            }
            $("#fundAssetTransfer001_unitCprice").val(unitCprice).trigger("change").trigger('blur');
            portfolioIdChange();
        });
        //交易份额change事件
        $("#fundAssetTransfer001_tradePrin").change(function () {
            var tradePrin = $("#fundAssetTransfer001_tradePrin").val();//交易份额
            if (tradePrin && tradePrin <= 0) {
                Joyin.error("交易份额必须大于0");
                $("#fundAssetTransfer001_tradePrin").val('');
                return;
            }
            calTradeAmt();
        });
        //待转仓产品组合信息查询事件
        $("#fundAssetTransfer001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        //单位净值change事件
        $("#fundAssetTransfer001_unitCprice").change(function () {
            var unitCprice = $("#fundAssetTransfer001_unitCprice").val();//单位净值
            if (unitCprice && unitCprice < 0) {
                // Joyin.error("单位净值必须大于0");
                $("#fundAssetTransfer001_unitCprice").val('');
                return;
            }
            calTradeAmt();

            //计算偏离度
            calculateDeviationDegree();
        });


        //待结转份额change事件
        $("#fundAssetTransfer001_carryOverQty").change(function () {
            var carryOverQty = $("#fundAssetTransfer001_carryOverQty").val();//待结转份额
            console.log("-----------carryOverQty------------")
            console.log(carryOverQty)
            if (carryOverQty && carryOverQty < 0) {
                Joyin.error("待结转份额不得小于0");
                $("#fundAssetTransfer001_carryOverQty").val('');
                return;
            }
            calTradeAmt();
        });

        //添加按钮
        $('#fundAssetTransfer001_btn_add').click(function () {
            doAddAction();
        });
        // 保存按钮事件
        $('#FundAssetTransfer001_btn_save').click(function () {
            saveOrSubmit('save');
        });
        // 提交按钮事件
        $('#FundAssetTransfer001_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
        //风险试算按钮
        $('#FundAssetTransfer001_btn_risk').click(function () {
            var data = funAssetTransferDtoListTable.getAllRows();
            if (data != null && data != '') {
                //-----------add by jjpei 2020-01-07 风险新版本 start----------
                var object = $.serializeToObject('#fundAssetTransfer_form', true);
                object.portfolioId = $("#invTransPortf").val();
                //深拷贝
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for (var i = 0; i < bussTrdTransfenpeiDto.length; i++) {
                    bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                    delete bussTrdTransfenpeiDto[i].portfolioId;
                    bussTrdTransfenpeiDto[i].ps = PS_T02;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F02', object, getriskdata());

            } else {
                Joyin.error("列表信息不能为空！");
            }
        });

        initcalculate();
    });
});