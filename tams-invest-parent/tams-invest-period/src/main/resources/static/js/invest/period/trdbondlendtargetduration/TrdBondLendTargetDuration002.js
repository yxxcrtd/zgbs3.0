(function () {

    /**
     * 页面初始化
     */
    var init = function () {

    }


    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                //全价总额不能为空
                if (!$("#intAmt").val() || Number.parseFloat($("#intAmt").val()) <= 0){
                    Joyin.error('实收利息总额不能为空或负值。');
                    return null;
                }
                if (!$("#tradeAmt").val() || Number.parseFloat($("#tradeAmt").val()) <= 0){
                    Joyin.error('理论本息总额不能为空或负值。');
                    return null;
                }
                if (!$("#tradeDate").val()){
                    Joyin.error('资金到账日不能为空。');
                    return null;
                }

                var dealinfo = {};
                dealinfo.busiType = $("#tradeType").val();
                dealinfo.finprodType2 = 'F01';
                dealinfo.ps = 'T11';
                dealinfo.settleDate = $("#tradeDate").val();
                dealinfo.tradeDate = $("#tradeDate").val();
                dealinfo.ccy = $("#ccy").val();
                dealinfo.tradeAmt = $("#tradeAmt").val();
                dealinfo.portfolioId = $("#portfolioId").val();
                dealinfo.counterId = $("#counterId").val();
                dealinfo.tradeMarket = $("#tradeMarket").val();
                dealinfo.secManageAcctId = $("#secManageAcctId").val();

                return {
                    'isTotalTrade':YES_OR_NO_N
                    ,'bankLendingType':null
                    ,'portfolioId':$("#portfolioId").val()
                    ,'deal':dealinfo
                };
            });

            if (formTodoList) {
                window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder'] && window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
            }
        }
    };

    function saveOrSubmit(operateType) {
        if (GEN_PAY_ORDERS_AUTO == '01') {
            window.BussTrdTransferOrder['bussTrdTransferOrder'].generateOrders(function () {
                realSaveOrSubmit(operateType);
            });
        }
        else {
            realSaveOrSubmit(operateType);
        }
    }

    function realSaveOrSubmit(operateType) {
        // 检查生成划款指令参数是否发生变化
        if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
            return;
        }

        var object = $.serializeToObject('#informationForm', true);
        object.operateType = operateType;
        if (FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo();
        }
        object.balanceCheck ='Y'
        Joyin.ajaxJson(basePath + 'invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration002/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration002/save', object, "TrdBondLendTargetDuration002_btn_save", "TrdBondLendTargetDuration002", function () {
                        if (parent.TrdBondLendTargetDuration001) {
                            parent.TrdBondLendTargetDuration001.reloadTable();
                        }
                    });
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/period/trdbondlendtargetduration/TrdBondLendTargetDuration002/save', object, "TrdBondLendTargetDuration002_btn_submit", "TrdBondLendTargetDuration002", function () {
                        if (parent.TrdBondLendTargetDuration001) {
                            parent.TrdBondLendTargetDuration001.reloadTable();
                        }
                    });
                }
            });
        });
    }

    /**
     * 画面加载完成
     */
    $(function () {
        init();
        $('#TrdBondLendTargetDuration002_btn_save').click(function () {
            saveOrSubmit('save');
        });
        $('#TrdBondLendTargetDuration002_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
        $("#intAmt").change(function () {
            var intAmt = $("#intAmt").val();
            var prinAmt = $("#theoryPrinAmt").val();
            $("#tradeAmt").val(JoyinMath.doAdd(intAmt, prinAmt));
        })
        $("#tradeAmt").change(function () {
            var tradeAmt = $("#tradeAmt").val();
            var prinAmt = $("#theoryPrinAmt").val();
            $("#intAmt").val(JoyinMath.doAdd(tradeAmt, prinAmt));
        })
    });
}());

