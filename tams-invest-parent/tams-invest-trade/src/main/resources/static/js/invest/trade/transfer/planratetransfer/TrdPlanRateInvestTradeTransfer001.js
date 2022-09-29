/**
 * 利率型计划转仓
 */
(function () {
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone) {
        	if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel==PAGE_MODEL_NEW){
                    riskparam.data =null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
        	}
            if(window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder']) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    getEntity();
                    entity.chlId = $("#chlId").val();
                    for (i = 0; i < entity.length; i++) {
                        entity[i].finprodType2 = 'F17';
                        entity[i].ps = 'T02';
                        entity[i].ccy = 'CNY';
                    }
                    return {
                        'isTotalTrade':'N'
                        ,'bankLendingType':null
                        ,'portfolioId':$("#transferPortfolioId").val()
                        ,'isUsePageAcct':false
                        ,'deal':entity
                        ,'jiaoyiType':'dzh'
                        ,'isLimitAcct':'Y'
                    };
                });
            }
        }
    };
    /**
     * 获取查询参数
     */
    var getPageParams = function () {
        return {
            portfolioId: $('#investPortfolioId').val(),
            cDate: $('#tradeDate').val(),
            ccy: $('#ccy').val(),
            finprodId: $('#finprodId').val(),
            mDate: $('#tradeDate').val(),
            vDate: $('#tradeDate').val(),
            effDate: $('#tradeDate').val()
        };
    };

    /**
     * 定义数据表格
     */
    var tables = {
        /*
         * 给表格赋虚拟数据
         */
        page: false,
        height: '140px',
        /*
         * 定义表头
         */
        cols: [
            {field: 'portfolioId', width: 120, title: '组合代码'}
            , {field: 'portfolioName', width: 120, title: '组合名称'}
            , {field: 'finprodId', width: 120, title: '资产代码'}
            , {field: 'tradeDate', width: 120, title: '交易日期', type: 'date'}
            , {field: 'settleDate', width: 120, title: '交割日期', type: 'date', visible:false}
            , {field: 'prinAmt', width: 120, title: '本金金额', type: 'number'}
            , {field: 'loadRate', width: 120, title: '净投资收益率%', type: 'number', digit: 4}
            , {field: 'intAmt', sortField: 'ccy', width: 130, title: '应计利息总额', type: 'number'}
            , {field: 'transferTradeAmt', width: 120, title: '交易金额', type: 'number'}
            // 待转让本金
            //, {field: 'assignPrinamt',width: 150,title: '待转让本金',type: 'money', digit: 2, sortable: false}
            // 待转让利息
            , {field: 'assignIntamt',width: 150,title: '待转让利息',type: 'money', digit: 2, sortable: false}
            , {field: 'stlPayId', title: '划款指令编号', width: 150, visible:false}
            , {field: 'fileArrayName', title: '附件名', width: 200}
            , {field: 'businessId', visible: false }//附件编号
            , {title : 'SysCommon.operation',field : 'operation',type : 'action',width : 150
                ,buttons :[
                    {text : '&#xe67c;',clas : 'uploadFile',title : '上传'}
                    ,{text : '&#xe640;',clas : 'delt',title : '删除'}
                    ,{field: 'sumitSeq',visible:false}  ]}
        ]
    };
    if (formTodoList) {
        tables.data = entityData;
    }

    var table = $("#dataTable").joyintable(tables);
    /**
     * 添加列表数据
     */
    var addRow = function (options, options1) {
        //添加列表数据
        var row = {
            portfolioId: options,
            portfolioName: options1,
            finprodId: $('#finprodId').val(),
            tradeDate: $('#tradeDate').val(),
            settleDate: $('#settleDate').val(),
            prinAmt: $('#transferPrinAmt').val(),
            loadRate: $('#loadRate').val(),
            intAmt: $('#payInt').val(),
            stlPayId : $('#stlPayId').val(),
            transferTradeAmt: $('#transferTradeAmt').val()
        };
        if (row.transferTradeAmt > Number($('#portoAvailableAmt').val())) {
            Joyin.confirm('组合剩余现金不足!',function () {
                //计算剩余可拆分金额
                var restAmt = $('#restAmt').val();
                $('#restAmt').val(Number(restAmt) - Number(row.prinAmt)).trigger("change").trigger('blur');
                var totalPrinamt = $("#prinAmt").val();
                var totalAssignIntamt = $("#assignIntamt").val();
                if (Number($('#restAmt')) == 0) {
                    var assignAmt = 0;
                    $.each(table.getAllRows(), function(index, item) {
                        assignAmt += Number(item['assignIntamt']);
                    });
                    row['assignIntamt'] = JoyinMath.doMinus(Number(totalAssignIntamt), assignAmt);
                } else {
                    row['assignIntamt'] = Number(totalPrinamt) == 0 ? 0 : JoyinMath.doMul(totalAssignIntamt, JoyinMath.doDiv(row['prinAmt'], totalPrinamt, 20), 2);
                }
                //row['assignPrinamt'] = 0;
                table.addRow(row);
            },function () {
                return;
            });
        }else{
            //计算剩余可拆分金额
            var restAmt = $('#restAmt').val();
            $('#restAmt').val(Number(restAmt) - Number(row.prinAmt)).trigger("change").trigger('blur');
            var totalPrinamt = $("#prinAmt").val();
            var totalAssignIntamt = $("#assignIntamt").val();
            if (Number($('#restAmt')) == 0) {
                var assignAmt = 0;
                $.each(table.getAllRows(), function(index, item) {
                    assignAmt += Number(item['assignIntamt']);
                });
                row['assignIntamt'] = JoyinMath.doMinus(Number(totalAssignIntamt), assignAmt);
            } else {
                row['assignIntamt'] = Number(totalPrinamt) == 0 ? 0 : JoyinMath.doMul(totalAssignIntamt, JoyinMath.doDiv(row['prinAmt'], totalPrinamt, 20), 2);
            }
            //row['assignPrinamt'] = 0;
            table.addRow(row);
        }

    };

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

    $('#portfolioId').change(function () {
        $('#portoAvailableAmt').val('').change().trigger('blur');
        if ($('#portfolioId').val()) {
            var object = $('#portfolioId').select2("data")[0].object;
            console.log("------------------")
            console.log(object)
            if (object && object.availableCash) {
                $('#portoAvailableAmt').val(object.availableCash).change().trigger('blur');
            }
        }

        // if ($('#portfolioId').val().split(':')[0]) {
        //     var dataCash = {
        //         portfolioId: $('#portfolioId').val().split(':')[0],
        //         tradeDate: $('#tradeDate').val(),
        //         ccy: $('#ccy').val(),
        //         calendarId : $("#calendarId").val(),
        //         finprodId : $("#finprodId").val(),
        //         chlId : $("#chlId").val()
        //     };
        //     findCash(dataCash);
        // }
        // if(!$('#portfolioId').val()){
        // 	$("#portoAvailableAmt").val('').trigger("change").trigger('blur');
        // }
    });


    /**
     * 利息总额计算
     */
    var intAmtCalcu = function(){
        object = {
//                portfolioId:$('#portfolioId').val(),
            finprodId:$('#finprodId').val(),
            settleDate:$('#settleDate').val(),
//                investRate:$('#investRate').val().valueOf() / 100,
            prinAmt:$('#transferPrinAmt').val()
        }

        if (pageModel != PAGE_MODEL_EDIT && pageModel != PAGE_MODEL_NEW){
            return ;
        }
        if(object.finprodId != null && object.finprodId != null &&
            object.settleDate != '' && object.settleDate != '' && object.prinAmt != null && object.prinAmt != ''
        ){
            Joyin.ajaxJson(basePath+ "invest/trade/trdplanrateinvesttrade/TrdPlanRateInvestTrade002/getIntAmt",object,function(result){
                $('#payInt').val(result.data).trigger('blur');
                var tradeAmt = parseFloat(result.data)+parseFloat($('#transferPrinAmt').val().valueOf());
                //卖出时 通道存在 收取方式为回款前扣除 减去通道费
                //2019-12-24 通道费改造 标的挂通道  通道费不参与计算
//                var data1 = $("#chlId").find("option:selected").data("collecttype");
////                if($('#ps').val()&&$('#ps').val()==PS_T02 && data1 && data1.object && data1.object.collectType==collectType_01){
//                if(($("#trdInvestPersonInfo002Dto_counterId").val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object=='N')
//           		 ||($("#trdInvestPersonInfo002Dto_counterId").val()&&$("#trdInvestPersonInfo002Dto_counterId").select2('data')[0].object!='N'
//           			&& $('#ps').val()&&$('#ps').val()==PS_T02 && data1 && data1==collectType_01)) {
//                	tradeAmt = JoyinMath.doMinus(tradeAmt, $("#channelFee").val())
//                }else if(!$("#trdInvestPersonInfo002Dto_counterId").val()&&$('#ps').val()&&$('#ps').val()==PS_T02 && data1  && data1==collectType_01){
//                	tradeAmt = JoyinMath.doMinus(tradeAmt, $("#channelFee").val())
//                }
                $('#transferTradeAmt').val(tradeAmt).trigger('blur');
            });
        }else{
            $('#payInt').val('').trigger('blur');
            $('#transferTradeAmt').val('').trigger('blur');
        }
    }
    //查询组合持仓、组合可用现金
    var findCash = function (data) {
        var params = data;
        if(params.portfolioId && params.portfolioId.trim()
            && params.calendarId && params.ccy && params.tradeDate) {
            params.acctTypeList = ACCT_TYPE_A01 + "," + ACCT_TYPE_A07;
            Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdAvailableCashQuery/getAvailableCash'), params, function (data) {
                $('#portoAvailableAmt').val(data.T_0).trigger("change").trigger('blur');
                if ($('#chlId').val()) {
                    $('#chlAvailableAmt').val(data.T_2).trigger("change").trigger('blur');
                }
            });
        }
    };

    //计算出拆分信息
    $('#transferPrinAmt').change(function () {
        var transferPrinAmt = $("#transferPrinAmt").val();//本金金额
        if (transferPrinAmt && transferPrinAmt <= 0) {
            Joyin.error("本金金额必须大于0");
            $("#transferPrinAmt").val('');
            return;
        }
        intAmtCalcu();
    });

    //联动交易金额修改事件
    $('#transferTradeAmt').change(function () {
        var transferTradeAmt = $('#transferTradeAmt').val();
        if (transferTradeAmt && transferTradeAmt <= 0) {
            Joyin.error("交易金额必须大于0");
            $("#transferTradeAmt").val('');
            return;
        }

        var transferPrinAmt = $('#transferPrinAmt').val();
        $('#payInt').val(Number(transferTradeAmt) - Number(transferPrinAmt)).trigger("change").trigger('blur')
    });

    //绑定添加按钮
    $('#TrdPlanRateInvestTradeTransfer001_btn_add').click(function () {
        var options = $("#portfolioId option:selected").val();
        var options1 = $("#portfolioId option:selected").text();
        //画面check
        if (!options || !$('#transferPrinAmt').val() || Number($("#transferPrinAmt").val()) == 0) {
            Joyin.error('组合代码和本金金额不能为空！');
            return;
        }
        if (Number($('#transferPrinAmt').val()) > Number($('#restAmt').val())) {
            Joyin.error('本金金额不能大于剩余未转仓本金金额！');
            return;
        }
        var tableRows = table.getAllRows();
        var flag = false;
        if (tableRows) {
            $.each(tableRows, function (idx, v) {
                if (options == v.portfolioId) {
                    Joyin.error('产品组合' + options + '在转仓列表已存在！');
                    flag = true;
                    return
                }
            });
            if (flag) {
                return;
            }
        }
        addRow(options, options1);
        $("#portfolioId").val('').trigger("change").trigger('blur');
        $("#transferPrinAmt").val('').trigger("change").trigger('blur');
        $("#transferTradeAmt").val('').trigger("change").trigger('blur');
        $('#payInt').val('').trigger("change").trigger('blur');
        $("#portoAvailableAmt").val('').trigger("change").trigger('blur');
        $("#chlAvailableAmt").val('').trigger("change").trigger('blur');
    });

    // 行内删除按钮点击事件
    table.bind('click', '.delt', function (data, index) {
        table.removeRow(data);
        var restAmt = $('#restAmt').val();
        //计算剩余可转仓金额
        $('#restAmt').val(Number(restAmt) + Number(data.prinAmt)).trigger("change").trigger('blur');
    });

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
            portfolioId : $("#transferPortfolioId").val(),
            chlId : $("#chlId").val(),
            tradeDate : $("#tradeDate").val(),
            ccy : $("#ccy").val(),
            listInfoList : JSON.stringify(listInfoList)
        })
    };

    function getEntity(){
        // 转出
        var object = $.serializeToObject('#trdPlanRateInvestTransferForm');
        // 转入组合
        object.dataList = JSON.stringify(table.getAllRows());

        // console.log(object)
        Joyin.ajaxSync(basePath + 'invest/trade/transfer/planratetransfer/TrdPlanRateInvestTradeTransfer001/getEntity', object, function(result) {
            entity = result;
        });
    }

    //风险试算
    var getriskdata = function () {
        var mun=null;
        var data = table.getAllRows();
        //拆分列表
        var list = [];
        var object = {};
        if(data!=null&&data!="") {
            for (var i in data) {
                var obj = {
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: $('#tradeDate').val(),
                    portfolioCode: data[i].portfolioId,
                    tradeAmount: data[i].transferTradeAmt,
                    assetType: FINPROD_TYPE_F17,
                    isEnabled: '01',
                    assetCode: $('#finprodId').val(),
                    tradeDirection: PS_T01,
                    index: null,
                    mun: mun,
                    assetSecType: MY_SUP_TIME_TYPE_02
                };
                list[i] = obj;
            }
            object = {
                valDate: ['tradeDate', '交易日期'],
                portfolioCode: ['portfolioId', '组合代码'],
                tradeAmount: ['tradeAmt', '交易金额'],
                assetType: ['assetSecType', '资产类型'],
                tradeDirection: ['ps', '交易方向'],
                data: list
            };
        }
        return object;
    };

    $(function () {
        table.load();
        //将标签隐藏
        if (!$('#chlId').val()) {
            $('#chlAvailable').hide();
        }
        //待转仓产品组合信息查询事件
        $("#TrdPlanRateInvestTradeTransfer001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        //风险试算
        $('#TrdPlanRateInvestTradeTransfer001_btn_risk').click(function(){
            var data = table.getAllRows();
            if(data != null && data != ''){
                var object = $.serializeToObject('#trdPlanRateInvestTransferForm', true);
                object.portfolioId = $("#transferPortfolioId").val();
                //深拷贝
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for(var i=0;i<bussTrdTransfenpeiDto.length;i++){
                	bussTrdTransfenpeiDto[i].counterId=bussTrdTransfenpeiDto[i].portfolioId;
                	delete bussTrdTransfenpeiDto[i].portfolioId;
                	bussTrdTransfenpeiDto[i].ps = PS_T02;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F03',object,getriskdata());
            }
            else {
                Joyin.error("转仓列表信息不能为空！");
            }
        });
        //绑定提交
        $('#TrdPlanRateInvestTradeTransfer001_btn_save').click(function () {
            var object = $.serializeToObject('#trdPlanRateInvestTransferForm', true);
            save(object, 'save')
        });
        $('#TrdPlanRateInvestTradeTransfer001_btn_submit').click(function () {
            var object = $.serializeToObject('#trdPlanRateInvestTransferForm', true);
            save(object, 'submit')
        })
    });

    var save = function (object, operateType) {
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
        var data = table.getAllRows();
        object.pageModel = pageModel;
        object.operateType = operateType;
        object.planTransferDtos = data;
        Joyin.ajaxJson(basePath + '/invest/trade/transfer/planratetransfer/TrdPlanRateInvestTradeTransfer001/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                var elementId;
                if (operateType === 'save') {
                    elementId = "TrdPlanRateInvestTradeTransfer001_btn_save";
                    WorkFlow.doSaveList(basePath + '/invest/trade/transfer/planratetransfer/TrdPlanRateInvestTradeTransfer001/save', object, elementId);
                } else {
                    elementId = "TrdPlanRateInvestTradeTransfer001_btn_submit";
                    WorkFlow.doSubmitList(basePath + '/invest/trade/transfer/planratetransfer/TrdPlanRateInvestTradeTransfer001/save', object, elementId);
                }
            });
        });
    }

}());