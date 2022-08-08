(function () {
    /**
     * 页面初始化
     */
    var init = function() {

    }

    /**
     * 定义数据表格
     */
    var secLendInfoTable = $("#secLendInfoTable").joyintable({
        rowno: true,
        searchOnInit:true,
        ajax: {
            url: basePath + 'invest/period/trdrepoduration/TrdRepoDuration002/findRepoInfo',
            type: 'POST',
            data: {finprodId: finprodId}
        },
        checkbox: false,
        page: false,
        cols: [
            {field: 'finprodMarketId', width: "23%", title: '债券代码'}
            , {field: 'finprodAbbr', width: "23%", title: '债券简称'}
            , {field: 'qty', width: 300, title: '质押券面总额', type: 'money'}
            , {field: 'pledgeRatio', width: 330, title: '折算比例(%)', type: 'money'}
        ]
        // 债券借贷到期确认交易增加汇总券面总额字段 add by renping 20190923 start
        ,
        done: function(){
            var fpriceAmtSum=Number(0);
            for(var i=0;i<secLendInfoTable.getAllRows().length;i++){
                var fpriceAmt=secLendInfoTable.getAllRows()[i].qty;
                fpriceAmtSum=Number(fpriceAmtSum)+Number(fpriceAmt);
                $("#fpriceAmt").val(fpriceAmtSum).trigger('blur');
            }
        }
        // 债券借贷到期确认交易增加汇总券面总额字段 add by renping 20190923 end
    });

    var setParam = function(){
        return {portfolioId: $('#actPortfolioId').val(), finprodId: $('#pledgeFinprodId').val(),
            invAim: $("#invAim").val()||'999999', secAcctId: $("#secManageAcctId").val(),
            tradeDate: platDate, ccy: $("#ccy").val()
        };
    }

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
        // 划款指令参数变更校验
        if(window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()){
            return ;
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#informationForm', true);
        if(!(object.bussTrdTransferOrder)||object.bussTrdTransferOrder.length==0){
            return Joyin.alert(JoyinUtil.getMessage("PTL_COM_E_0007"),2);
        }

        if ($("#isCashPay").prop("checked")) {
            object.isCashPay = 'Y'
            if ($("#ps").val() == '05' && !$("#invAim").val()) {
                return Joyin.alert("融入到期并且是现金交割时，投资目的必填。",2);
            }
        }
        else {
            object.isCashPay = 'N'
        }

        if (shortSellingSupported == '01') {
            if ($("#ps").val() == '05') {
                TradeCommon.getSecPositionForSaleAll(setParam(), function (result) {
                    if (object.isCashPay == 'N' && (result.INTEGRATE_POSITION || 0) * 1 < $("#shareAmt").val() * 1 && object.isCashPay == 'N') {
                        return Joyin.error("融入券可用持仓不足，请先购回")
                    }
                    object.assetIntegrateAvailablePosition = result.INTEGRATE_POSITION;
                    save(object, operateType);
                });
            }
            else {
                save(object, operateType);
            }
        }
        else {
            save(object, operateType);
        }
    }

    function save(object, operateType) {
        object.operateType = operateType;
        Joyin.ajaxJson(basePath + 'invest/period/trdseclendduration/TrdSecLendDuration002/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                if (operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/period/trdseclendduration/TrdSecLendDuration002/save', object, "TrdSecLendDuration002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'invest/period/trdseclendduration/TrdSecLendDuration002/save', object, "TrdSecLendDuration002_btn_submit");
                }
            });
        });
    }

    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                var deal = JSON.parse(dealString);
                deal.tradeAmt = $("#tradeAmt").val();//即借贷费用 + 现金交割金额
                /*modify by jiangpeng on 20210420 start BS-1593 【债券借贷交易存续期】：缺少资金到账日*/
                var settleDate = $("#settleDate").val();
                if (settleDate === null || settleDate === '') {
                    return Joyin.error("请输入资金到账日。");
                }
                deal.settleDate = settleDate;
                /*modify by jiangpeng on 20210420 end BS-1593 【债券借贷交易存续期】：缺少资金到账日*/
                return {
                    'isTotalTrade': YES_OR_NO_Y
                    , 'bankLendingType': $("#mDeliveryType").val()
                    , 'portfolioId': portfolioId
                    , 'deal':deal
                };
            });
        }
    }

    /**
     * 画面加载完成
     */
    $(function () {
        init();
        $('#TrdSecLendDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdSecLendDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

        layui.form.on('switch(isCashPayFilter)', function(data){
            $("#invAim").prop("disabled", true);
            if ($(this).prop("checked")) {
                $("#prinAmt").prop("disabled", false).change();
                if ($("#ps").val() == '05') {
                    $("#invAim").prop("disabled", false);
                }
            }
            else {
                $("#prinAmt").val(0).prop("disabled", true).change();
            }
        });

        $("#prinAmt").change(function () {
            $("#tradeAmt").val(JoyinMath.doAdd($("#mTradeAmt").val(), $("#prinAmt").val(), 2, JoyinMath.ROUND_HALF_UP)).trigger('blur')
        })

        $("#tradeAmt").change(function () {
            $("#mTradeAmt").val(JoyinMath.doMinus($("#tradeAmt").val(), $("#prinAmt").val(), 2, JoyinMath.ROUND_HALF_UP)).trigger('blur')
        })

        if (pageModel == PAGE_MODEL_EDIT && isTodoList != 'true') {
            $("#invAim").prop("disabled", true);
            if ($("isCashPay").prop("checked")) {
                $("#prinAmt").prop("disabled", false).change();
                if ($("#ps").val() == '05') {
                    $("#invAim").prop("disabled", false);
                }
            }
            else {
                $("#prinAmt").val(0).prop("disabled", true).change();
            }
        }
    });
}());

