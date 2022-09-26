$(function () {

    window.componentLoadCallback = function (isDone, pagePath, params) {
        if (isDone) {
            //组合持仓现金组件
            var chlId = $('#chlId').val();
            //通道为空时，显示组合可用现金
            if (!chlId) {
                $("#bussTrdAvailableCashQuery_div_cash").show();
                $("#bussTrdAvailableCashQuery_div_channelCash").hide();
                $("#bussTrdAvailableCashQuery_div_position").hide();
                // $("#bussTrdAvailableCashQuery_avalibleCashT1").parent().parent().hide();
                // $("#bussTrdAvailableCashQuery_avalibleCash").parent().prev().html("组合可用现金");
            }
            //通道有值时，显示通道可用现金
            else {
                $("#bussTrdAvailableCashQuery_div_cash").hide();
                $("#bussTrdAvailableCashQuery_div_channelCash").show();
                $("#bussTrdAvailableCashQuery_div_position").hide();
            }

            if (window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder']) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    getEntity();
                    entity.chlId = $("#chlId").val();
                    return {
                        'isTotalTrade': 'N'
                        , 'bankLendingType': null
                        , 'portfolioId': $("#portfolioIdOut").val()
                        , 'isUsePageAcct': false
                        , 'deal': entity
                        , 'jiaoyiType': 'dzh'
                        , 'isLimitAcct': 'Y'
                    };
                });
            }

            function getEntity() {
                // 转出
                var object = $.serializeToObject('#netPlanTransferForm');
                // 转入组合
                object.dataList = JSON.stringify(table.getAllRows());

                Joyin.ajaxSync(basePath + 'invest/trade/transfer/netplantransfer/NetPlanTransfer001/getEntity', object, function (result) {
                    result.createTime = null;
                    result.updateTime = null;
                    result.tradeId = null;
                    result.totleTradeId = null;
                    result.cpriceAmt = null;
                    result.shareAmt = null;
                    result.prinAmt = null;
                    result.intAmt = null;
                    result.fpriceAmt = null;
                    // 输入备注等，点击提交，提示计息要素已变更 add by renping 20191018 start
                    result.remark = null;
                    result.counterContact = null;
                    result.counterTrader = null;
                    // 输入备注等，点击提交，提示计息要素已变更 add by renping 20191018 end
                    entity = result;
                });
            }

            //风险试算组件
            if (typeof (BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskparam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
            }
        }
    };

    /**
     * 组合代码联动组合现金、资产持仓变化
     */
    var portfolioIdChange = function () {
        $('#portoAvailableAmt').val('').change().trigger('blur');
        if ($('#NetPlanTransfer001_portfolioId').val()) {
            var object = $('#NetPlanTransfer001_portfolioId').select2("data")[0].object;
            if (object && object.availableCash) {
                $('#portoAvailableAmt').val(object.availableCash).change().trigger('blur');
            }
        }

        // var params = {};
        // params.portfolioId = $("#NetPlanTransfer001_portfolioId").val();
        // params.calendarId = $("#calendarId").val();
        // params.ccy = $("#NetPlanTransfer001_ccy").val();
        // params.tradeDate = $("#NetPlanTransfer001_tradeDate").val();
        // params.finprodId = $("#finprodId").val();
        // params.chlId = $("#chlId").val();
        // params.finprodMarketId = $("#NetPlanTransfer001_finprodMarketId").val();
        // if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
        //     window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
        //         return params
        //     });
        // }
        //获取单位净值
        // Joyin.ajax(basePath + 'invest/trade/transfer/NetPlanTransfer/NetPlanTransfer001/getUnitValue', params, function (result) {
        //     if(result && result.data){
        //         $("#NetPlanTransfer001_unitValue").val(result.data).trigger("change").trigger('blur');
        //     }
        // });
        $("#NetPlanTransfer001_unitValue").val($("#unitPrice").val()).trigger("change").trigger('blur');
        $("#NetPlanTransfer001_tradeAmt").val('').trigger("change").trigger('blur');
        $("#NetPlanTransfer001_tradeMoney").val('').trigger("change").trigger('blur');
        // $("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
        // $("#bussTrdAvailableCashQuery_channelAvailableCash").val('').trigger("change").trigger('blur');
    };

    /**
     * 转仓信息——交易金额联动
     */
    var tradeMoneyChange = function () {
        var tradeAmt = Number($("#NetPlanTransfer001_tradeAmt").val());
        var unitValue = Number($("#NetPlanTransfer001_unitValue").val());
        if (tradeAmt && unitValue) {
            $("#NetPlanTransfer001_tradeMoney").val(tradeAmt * unitValue).trigger("change").trigger('blur');
        }
    };

    var options = {
        /*
         * 给表格赋虚拟数据
         */
        page: false,
        height: '140px',
        /*
         * 定义表头
         */
        cols:[
            {field:'portfolioId', title: '组合代码',width:160}//组合代码
            ,{field:'portfolioName', title: '组合名称',width:160}//组合名称
            ,{field:'finprodMarketId', title: '资产代码',width:160}//资产代码
            ,{field:'tradeDate', title: '交易日期',type:'date',width:160}//交易日期
            ,{field:'tradeAmt', title: '交易份额',type:'number',width:160}//交易份额
            ,{field:'unitValue', title: '单位净值',type:'number',digit:'4',width:160}//单位净值
            ,{field:'tradeMoney', title: '交易金额',type:'number',width:160}//交易金额
            ,{field:'fileArrayName', title: '附件名',width:200}//附件名称
            , {field: 'businessId', visible: false }//附件编号
            ,{field:'operation', title: 'SysCommon.operation', type:'action', width : 150,
                                                buttons :[
                                                {text : '&#xe67c;',clas : 'uploadFile',title : '上传'}
                                               ,{text : '&#xe640;',clas : 'delt',title : '删除'}
                                               ,{field: 'sumitSeq',visible:false}  ]}//编辑和删除事件
        ]
    };
    if (formTodoList) {
        options.data = entityData;
    }
    var table = $("#dataTable").joyintable(options);

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function (data, index) {
        table.removeRow(data);
        //3、联动剩余未拆分金额
        var undistributedAmount = $('#NetPlanTransfer001_undistributedAmount').val();
        var tradeAmt = data.tradeAmt;
        $("#NetPlanTransfer001_undistributedAmount").val(Number(undistributedAmount) + Number(tradeAmt)).trigger("change").trigger('blur');
    });

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
            function(result){
                var fileArrayName = '';
                if (result && result.fileArray && _.isArray(result.fileArray) && result.fileArray.length>0) {
                    for (var index in result.fileArray) {
                        fileArrayName += result.fileArray[index].srcName +'，';
                    }
                    result.fileArrayName = fileArrayName.substr(0,fileArrayName.length-1);
                }
                table.renderRow(data.LAY_TABLE_INDEX, {"businessId": result.businessId, "fileArrayName": result.fileArrayName});
            });
    });

    /**
     * 添加按钮按下处理
     */
    var transferChange = function () {
        var param = {};
        param.tradeAmt = $("#NetPlanTransfer001_tradeAmt").val();
        param.undistributedAmount = $('#NetPlanTransfer001_undistributedAmount').val();
        param.tradeMoney = $("#NetPlanTransfer001_tradeMoney").val();
        // param.avalibleCash = $('#bussTrdAvailableCashQuery_avalibleCash').val();
        param.avalibleCash = $('#portoAvailableAmt').val();
        param.portfolioId = $('#NetPlanTransfer001_portfolioId').val();
        param.chlId = $('#chlId').val();
        param.unitValue = $('#NetPlanTransfer001_unitValue').val();
        param.netPlanTransfer002DtoList = table.getAllRows();
        Joyin.ajaxJson(basePath + 'invest/trade/transfer/netplantransfer/NetPlanTransfer001/addCheck', param, function (result) {
            if (result && result.statuCode == 200) {
                //1、向列表中插入数据
                var options = $("#NetPlanTransfer001_portfolioId").find("option:selected");
                var row = {
                    portfolioId: options.val(),
                    portfolioName: options.text().split(':')[1],
                    finprodMarketId: $('#NetPlanTransfer001_finprodMarketId').val(),
                    tradeDate: $('#NetPlanTransfer001_tradeDate').val(),
                    tradeAmt: $('#NetPlanTransfer001_tradeAmt').val(),
                    unitValue: $('#NetPlanTransfer001_unitValue').val(),
                    tradeMoney: $('#NetPlanTransfer001_tradeMoney').val(),
                };
                table.addRow(row);
                //3、联动剩余未拆分金额
                var undistributedAmount = $('#NetPlanTransfer001_undistributedAmount').val();
                var tradeAmt = $('#NetPlanTransfer001_tradeAmt').val();
                $("#NetPlanTransfer001_undistributedAmount").val(Number(undistributedAmount) - Number(tradeAmt)).trigger("change").trigger('blur');
                //4、清空转仓信息的信息
                $("#NetPlanTransfer001_portfolioId").val('').trigger("change").trigger('blur');
                $("#NetPlanTransfer001_tradeAmt").val('').trigger("change").trigger('blur');
                $("#NetPlanTransfer001_tradeMoney").val('').trigger("change").trigger('blur');
                $("#portoAvailableAmt").val('').trigger("change").trigger('blur');
                // $("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
                // $("#bussTrdAvailableCashQuery_channelAvailableCash").val('').trigger("change").trigger('blur');
                var unitValue = $("#NetPlanTransfer001_unitValue").attr("value");
                if (unitValue == undefined) {
                    unitValue = "";
                }
                $("#NetPlanTransfer001_unitValue").val(unitValue).trigger("change").trigger('blur');

            }
        })
    };

    /**
     * 查询待转仓组合
     */
    var findAvaTransferPortfolioInfo = function () {
        var listInfoList = [];
        var allRows = table.getAllRows();
        for (var i in allRows) {
            var obj = {
                portfolioId: allRows[i].portfolioId,
                tradeAmt: allRows[i].tradeMoney
            };
            listInfoList[i] = obj;
        }
        Joyin.dialog("待转仓产品组合信息", basePath + "/invest/trade/transfer/common/TransferCommon001/init", '60%', '60%', {
            portfolioId: $("#portfolioIdOut").val(),
            chlId: $("#chlId").val(),
            tradeDate: $("#NetPlanTransfer001_tradeDate").val(),
            ccy: $("#NetPlanTransfer001_ccy").val(),
            listInfoList: JSON.stringify(listInfoList)
        })
    };

    //判断是提交或是保存
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
        var object = $.serializeToObject('#netPlanTransferForm', true);
        var data = table.getAllRows();
        object.operateType = operateType;
        object.netPlanTransfer002DtoList = data;
        var unitValue = $("#NetPlanTransfer001_unitValue").attr("value");
        if (null == unitValue || 'undefined' == typeof (unitValue) || '' == unitValue) {
            delete object.unitValue;
        } else {
            object.unitValue = unitValue;
        }

        Joyin.ajaxJson(basePath + 'invest/trade/transfer/netplantransfer/NetPlanTransfer001/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                if (operateType === 'save') {
                    // Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.save")), function() {
                    WorkFlow.doSaveList(basePath + 'invest/trade/transfer/netplantransfer/NetPlanTransfer001/save',
                        object, "NetPlanTransfer001_btn_save");
                    // });
                } else {
                    // Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.submit")), function() {
                    WorkFlow.doSubmitList(basePath + 'invest/trade/transfer/netplantransfer/NetPlanTransfer001/save',
                        object, "NetPlanTransfer001_btn_submit");
                    // });
                }
            });
        });
    }

    var getriskdata = function () {
        var mun = null;
        var data = table.getAllRows();
        //拆分列表
        var list = [];
        var object = {};
        if (data != null && data != "") {
            for (var i in data) {
                var obj = {
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: $('#NetPlanTransfer001_tradeDate').val(),
                    portfolioCode: data[i].portfolioId,
                    tradeAmount: data[i].tradeAmt,
                    assetType: FINPROD_TYPE_F17,
                    isEnabled: '01',
                    assetCode: $('#finprodId').val(),
                    tradeDirection: PS_T01,
                    index: null,
                    mun: mun,
                    amtType: '1'
                };
                if ($('#type7').val() && $('#type7').val() != '') {
                    obj.assetSecType = $('#type7').val();
                }
                list[i] = obj;
            }
            object = {
                valDate: ['tradeDate', '交易日期'],
                portfolioCode: ['portfolioId', '组合代码'],
                tradeAmount: ['tradeAmt', '交易份额'],
                assetType: ['assetSecType', '资产类型'],
                tradeDirection: ['ps', '交易方向'],
                data: list
            };
        }
        return object;
    };

    var initcalculate = function () {
        console.log("--------------initcalculate--------------------")
        var unitCprice = $("#NetPlanTransfer001_unitValue").val();
        if (unitCprice && unitCprice > 0) {
            $("#NetPlanTransfer001_unitValue").val(unitCprice).change().trigger('blur')
        }
    };

    var calculateDeviationDegree = function () {
        console.log("--------------deviationDegree--------------------")
        var unitCprice = $("#NetPlanTransfer001_unitValue").val();

        if (unitCprice && unitCprice > 0) {
            var object = {};
            object.finprodId = $("#NetPlanTransfer001_finprodMarketId").val();
            object.unitPrice = unitCprice;
            Joyin.ajaxJson(basePath + '/invest/trade/transfer/netplantransfer/NetPlanTransfer001/calculateDeviationDegree', object,
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
        table.load();
        //组合代码联动
        $('#NetPlanTransfer001_portfolioId').change(function () {
            portfolioIdChange();
        });
        //转仓信息——交易份额联动
        $('#NetPlanTransfer001_tradeAmt').change(function () {
            tradeMoneyChange();
        });
        //转仓信息——单位净值联动
        $('#NetPlanTransfer001_unitValue').change(function () {
            tradeMoneyChange();

            calculateDeviationDegree();
        });
        //添加按钮
        $('#NetPlanTransfer001_btn_add').click(function () {
            transferChange();
        });
        //待转仓组合查询链接
        $("#NetPlanTransfer001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        // 保存按钮事件
        $('#NetPlanTransfer001_btn_save').click(function () {
            saveOrSubmit('save');
        });
        // 提交按钮事件
        $('#NetPlanTransfer001_btn_submit').click(function () {
            var data = table.getAllRows();
            if (data != null && data != '') {
                var object = $.serializeToObject('#netPlanTransferForm', true);
                object.portfolioId = $("#portfolioIdOut").val();
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for (var i = 0; i < bussTrdTransfenpeiDto.length; i++) {
                    bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                    delete bussTrdTransfenpeiDto[i].portfolioId;
                    bussTrdTransfenpeiDto[i].ps = PS_T02;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F04', object, getriskdata(),
                function (result,flag){
                    saveOrSubmit('submit');
                });
            } else {
                Joyin.error("拆分列表信息不能为空！");
            }
        });
        //风险试算
        $('#NetPlanTransfer001_btn_risk').click(function () {
            var data = table.getAllRows();
            if (data != null && data != '') {
                //-----------add by jjpei 2020-01-07 风险新版本 start----------
                var object = $.serializeToObject('#netPlanTransferForm', true);
                object.portfolioId = $("#portfolioIdOut").val();
                //深拷贝
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for (var i = 0; i < bussTrdTransfenpeiDto.length; i++) {
                    bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                    delete bussTrdTransfenpeiDto[i].portfolioId;
                    bussTrdTransfenpeiDto[i].ps = PS_T02;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F04', object, getriskdata());

            } else {
                Joyin.error("拆分列表信息不能为空！");
            }
        });

        initcalculate();
    });
}());