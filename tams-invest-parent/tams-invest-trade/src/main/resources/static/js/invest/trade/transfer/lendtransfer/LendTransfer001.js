$(function() {

    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone) {
            //风险试算组件
        	if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel==PAGE_MODEL_NEW){
                    riskparam.data =null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
        	}

            //组合持仓组件
            window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash(true);
            $("#bussTrdAvailableCashQuery_div_channelCash").hide();
            $("#bussTrdAvailableCashQuery_avalibleCashT1").parent().parent().hide();
            $("#bussTrdAvailableCashQuery_avalibleCash").parent().prev().html("组合可用现金");
        }
    };

    /**
     * 组合代码联动组合现金、资产持仓变化
     */
    var portfolioIdChange = function () {
        var params = {};
        params.portfolioId = $("#LendTransfer001_portfolioId").val();
        params.calendarId = $("#calendarId").val();
        params.ccy = $("#LendTransfer001_ccy").val();
        params.tradeDate = $("#LendTransfer001_tradeDate").val();
        if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
            window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
                return params
            });
        }
        $("#LendTransfer001_prinAmt").val('').trigger("change").trigger('blur');
        $("#LendTransfer001_intAmt").val('').trigger("change").trigger('blur');
        $("#LendTransfer001_tradeAmt").val('').trigger("change").trigger('blur');
        $("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
    };

    /**
     * 转仓信息——交易金额联动
     */
    var tradeMoneyChange = function () {
        var tradeAmt = Number($("#LendTransfer001_tradeAmt").val());
        var prinAmt = Number($("#LendTransfer001_prinAmt").val());
            if(tradeAmt && prinAmt){
            $("#LendTransfer001_intAmt").val(tradeAmt - prinAmt).trigger("change").trigger('blur');
        }
    };

    /**
     * 转仓信息——本金金额联动
     */
    var prinAmtChange = function () {
        //本金金额
        var prinAmt = Number($("#LendTransfer001_prinAmt").val());
        //利率
        var intRate = Number($("#LendTransfer001_intRate").val());
        //计息基础
        var basis = $("#LendTransfer001_basis").val();
        //交易日期
        var tradeDate = $("#LendTransfer001_tradeDate").val();
        //起息日
        var vdate = $("#LendTransfer001_vdate").val();
        //交易金额
        var tradeAmt = Number($("#LendTransfer001_tradeAmt").val());
        if(prinAmt && intRate && basis && tradeDate && vdate){
            // if(tradeAmt){
            //     var intAmt = tradeAmt - prinAmt;
            //     $("#LendTransfer001_intAmt").val(intAmt).trigger("change").trigger('blur');
            // }
            // else {
                var term = Number(DateUtil.daysBetween(tradeDate,vdate));
                var basisAmt = 0;
                if (basis == A_360) {
                    basisAmt = 360;
                } else if (basis == A_365) {
                    basisAmt = 365;
                }
                var intAmt = prinAmt * intRate * term / basisAmt /100;//应计利息=本金*利率/100*（交易日期-起息日）/计息基础
                $("#LendTransfer001_intAmt").val(intAmt).trigger("change").trigger('blur');
                $("#LendTransfer001_tradeAmt").val(prinAmt + intAmt).trigger("change").trigger('blur');
            // }
        }
        else {
            $("#LendTransfer001_intAmt").val('').trigger("change").trigger('blur');
            $("#LendTransfer001_tradeAmt").val('').trigger("change").trigger('blur');
        }
    };

    var options = {
        /*
         * 给表格赋虚拟数据
         */
        page:false,
        height:'140px',
        /*
         * 定义表头
         */
        cols:[
            {field:'portfolioId', title: '组合代码',width:160}//组合代码
            ,{field:'portfolioName', title: '组合名称',width:160}//组合名称
            ,{field:'tradeDate', title: '交易日期',type:'date',width:160}//交易日期
            ,{field:'prinAmt', title: '本金金额',type:'number',width:160}//本金金额
            ,{field:'intRate', title: '利率%',type:'number',width:160, digit: 4}//利率%
            ,{field:'intAmt', title: '应计利息总额',type:'number',width:160}//应计利息总额
            ,{field:'tradeAmt', title: '交易金额',type:'number',width:160}//交易金额
            ,{field:'vdate', title: '起息日',type:'date',width:160}//起息日
            ,{field:'mdate', title: '到期日',type:'date',width:160}//到期日
            ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: [ 'delt']}//编辑和删除事件
        ]
    };
    if(formTodoList) {
        options.data = entityData;
    }
    var table=$("#dataTable").joyintable(options);

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        table.removeRow(data);
        //3、联动剩余未拆分金额
        var undistributedAmount = $('#LendTransfer001_undistributedAmount').val();
        var prinAmt = data.prinAmt;
        $("#LendTransfer001_undistributedAmount").val(Number(undistributedAmount) + Number(prinAmt)).trigger("change").trigger('blur');
    });

    /**
     * 添加按钮按下处理
     */
    var transferChange = function () {
        var param = {};
        param.prinAmt = $("#LendTransfer001_prinAmt").val();
        param.undistributedAmount = $('#LendTransfer001_undistributedAmount').val();
        param.tradeAmt = $("#LendTransfer001_tradeAmt").val();
        param.intAmt = $("#LendTransfer001_intAmt").val();
        param.avalibleCash = $('#bussTrdAvailableCashQuery_avalibleCash').val();
        param.portfolioId = $('#LendTransfer001_portfolioId').val();
        param.lendTransfer002DtoList = table.getAllRows();
        Joyin.ajaxJson(basePath + 'invest/trade/transfer/lendtransfer/LendTransfer001/addCheck', param, function (result) {
            if(result && result.statuCode == 200){
                //1、向列表中插入数据
                var options=$("#LendTransfer001_portfolioId").find("option:selected");
                var row = {
                    portfolioId: options.val(),
                    portfolioName: options.text().split(':')[1],
                    tradeDate: $('#LendTransfer001_tradeDate').val(),
                    prinAmt: $('#LendTransfer001_prinAmt').val(),
                    intRate: $('#LendTransfer001_intRate').val(),
                    intAmt: $('#LendTransfer001_intAmt').val(),
                    tradeAmt : $('#LendTransfer001_tradeAmt').val(),
                    vdate : $('#LendTransfer001_vdate').val(),
                    mdate : $('#LendTransfer001_mdate').val()
                };
                table.addRow(row);
                //3、联动剩余未拆分金额
                var undistributedAmount = $('#LendTransfer001_undistributedAmount').val();
                var prinAmt = $('#LendTransfer001_prinAmt').val();
                $("#LendTransfer001_undistributedAmount").val(Number(undistributedAmount) - Number(prinAmt)).trigger("change").trigger('blur');
                //4、清空转仓信息的信息
                $("#LendTransfer001_portfolioId").val('').trigger("change").trigger('blur');
                $("#LendTransfer001_prinAmt").val('').trigger("change").trigger('blur');
                $("#LendTransfer001_intAmt").val('').trigger("change").trigger('blur');
                $("#LendTransfer001_tradeAmt").val('').trigger("change").trigger('blur');
                $("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
            }
        })
    };

    /**
     * 查询待转仓组合
     */
    var findAvaTransferPortfolioInfo = function(){
        var listInfoList = [];
        var allRows = table.getAllRows();
        for(var i in allRows){
            var obj = {
                portfolioId : allRows[i].portfolioId,
                tradeAmt : allRows[i].tradeAmt
            };
            listInfoList[i] = obj;
        }
        Joyin.dialog("待转仓产品组合信息",basePath+"/invest/trade/transfer/common/TransferCommon001/init",'60%','60%',{
            portfolioId : $("#portfolioIdOut").val(),
            tradeDate : $("#LendTransfer001_tradeDate").val(),
            ccy : $("#LendTransfer001_ccy").val(),
            listInfoList : JSON.stringify(listInfoList)
        })
    };

    //判断是提交或是保存
    function saveOrSubmit(operateType) {
        if(typeof(BaseRiskMathComm) != 'undefined') {
	        if(BaseRiskMathComm) {
	            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
	            if(!s && s != ''){
	                return;
	            }
	            if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
	                return;
	            }
	        }
        }
        var object = $.serializeToObject('#lendTransferForm', true);
        var data = table.getAllRows();
        object.operateType = operateType;
        object.lendTransfer002DtoList = data;
        Joyin.ajaxJson(basePath + 'invest/trade/transfer/lendtransfer/LendTransfer001/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                if(operateType === 'save') {
                    // Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.save")), function() {
                    WorkFlow.doSaveList(basePath + 'invest/trade/transfer/lendtransfer/LendTransfer001/save',
                        object, "LendTransfer001_btn_save");
                    // });
                }
                else {
                    // Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.submit")), function() {
                    WorkFlow.doSubmitList(basePath + 'invest/trade/transfer/lendtransfer/LendTransfer001/save',
                        object, "LendTransfer001_btn_submit");
                    // });
                }
            });
        });
    };

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
                    valDate: $('#LendTransfer001_tradeDate').val(),
                    portfolioCode: data[i].portfolioId,
                    tradeAmount: data[i].tradeAmt,
                    assetType: $('#LendTransfer001_paBussType').val(),
                    isEnabled: '01',
                    tradeDirection: $('#LendTransfer001_ps').val()
                };
                var finType = '';
                var finMng = '';
                if ($('#LendTransfer001_ps').val() == BACK_LEND_DIRECTION_01) {
                    if ($('#LendTransfer001_paBussType').val() == BANK_LENDING_TYPE_02) {
                        obj.assetType = FINPROD_TYPE_F08;//选特拆也当作拆入
                    } else if (!$('#LendTransfer001_paBussType').val() || $('#LendTransfer001_paBussType').val() == '') {
                        finType = "paBussType";
                        finMng = 'TrdLendInvestTrade002.paBussType';
                        obj.assetType = null;
                    } else {
                        obj.assetType = FINPROD_TYPE_F08;
                    }
                } else if ($('#LendTransfer001_ps').val() == BACK_LEND_DIRECTION_02) {
                    obj.assetType = FINPROD_TYPE_F09;
                } else if (finMng == '' && (!$('#LendTransfer001_ps').val() || $('#LendTransfer001_ps').val() == '')) {
                    finMng = 'TrdDepositInvestTrade001.finprodType';
                }
                list[i] = obj;
            }
            object = {
                valDate: ['tradeDate', '交易日期'],
                portfolioCode: ['portfolioId', '组合代码'],
                tradeAmount: ['prin', '交易金额'],
                assetType: [finType, finMng],
                tradeDirection: ['busiPs', '交易方向'],
                data: list
            };
        };
        return object;
    };

    /**
     * 画面加载完成
     */
    $(function() {
        table.load();
        //组合代码联动
        $('#LendTransfer001_portfolioId').change(function () {
            portfolioIdChange();
        });
        //转仓信息——交易金额联动
        $('#LendTransfer001_tradeAmt').change(function () {
            tradeMoneyChange();
        });
        //转仓信息——本金金额联动
        $('#LendTransfer001_prinAmt').change(function () {
            prinAmtChange();
        });
        //添加按钮
        $('#LendTransfer001_btn_add').click(function() {
            transferChange();
        });
        //待转仓组合查询链接
        $("#LendTransfer001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        // 保存按钮事件
        $('#LendTransfer001_btn_save').click(function() {
            saveOrSubmit('save');
        });
        // 提交按钮事件
        $('#LendTransfer001_btn_submit').click(function() {
            var data = table.getAllRows();
            if(data != null && data != ''){
                var object = $.serializeToObject('#lendTransferForm', true);
                object.portfolioId = $("#portfolioIdOut").val();
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for(var i=0;i<bussTrdTransfenpeiDto.length;i++){
                    bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                    delete bussTrdTransfenpeiDto[i].portfolioId;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F08',object,getriskdata(),
                function (result,flag){
                    saveOrSubmit('submit');
                });
            }else {
                Joyin.error("转仓列表信息不能为空！");
            }
        });
        //风险试算
        $('#LendTransfer001_btn_risk').click(function(){
            var data = table.getAllRows();
            if(data != null && data != ''){
                var object = $.serializeToObject('#lendTransferForm', true);
                object.portfolioId = $("#portfolioIdOut").val();
                //深拷贝
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for(var i=0;i<bussTrdTransfenpeiDto.length;i++){
                	bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                	delete bussTrdTransfenpeiDto[i].portfolioId;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F08',object,getriskdata());
            }
            else {
                Joyin.error("转仓列表信息不能为空！");
            }
        });
    });
}());