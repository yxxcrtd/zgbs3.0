/**
 * 存款类转仓
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

    function getEntity(){
        // 转出
        var object = $.serializeToObject('#depositTransfereForm');
        // 转入组合
        object.dataList = JSON.stringify(table.getAllRows());

        Joyin.ajaxSync(basePath + 'invest/trade/transfer/deposittransfere/DepositTransfere001/getEntity', object, function(result) {
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
            {field: 'portfolioId', width: 130, title: '组合代码'}
            , {field: 'portfolioName', width: 140, title: '组合名称'}
            , {field: 'finprodId', width: 130, title: '资产代码'}
            , {field: 'tradeDate', width: 120, title: '交易日期', type: 'date'}
            , {field: 'settleDate', width: 120, title: '交割日期', type: 'date'}
            , {field: 'prinAmt', width: 120, title: '本金金额', type: 'number'}
            , {field: 'loadRate', width: 120, title: '借款利率%', type: 'number', digit: 4}
            , {field: 'intAmt',  width: 130, title: '应计利息总额', type: 'number'}
            , {field: 'transferTradeAmt', width: 120, title: '交易金额', type: 'number'}
            // 转让逾期利息
            , {field: 'transOverdueIntAmt', visible : false,title: '转让逾期利息',type: 'money', digit: 2, sortable: false}
            // 转让逾期本金
            , {field: 'transOverduePrinAmt', visible : false,title: '转让逾期本金',type: 'money', digit: 2, sortable: false}
            // 交割金额
            , {field: 'deliveryAmount', visible : false,title: '交割金额',type: 'money', digit: 2, sortable: false, visible:false}
            , {field:'ps', title: '交易方向',width:150,width:150, visible:false}//交易方向
            , {field:'finprodType2', title: '投管资产类型',width:150,width:150, visible:false}//交易方向
            , {field:'fileArrayName', title: '附件名',width:200}//附件名称
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
            transferTradeAmt: $('#transferTradeAmt').val(),
            stlPayId : $('#stlPayId').val()

        };

        table.addRow(row);
        //计算剩余可拆分金额
        var restAmt = $('#restAmt').val();
        $('#restAmt').val(Number(restAmt) - Number(row.prinAmt)).trigger("change").trigger('blur');

    };

    $('#portfolioId').change(function () {
        if ($('#portfolioId').val()) {
            // var dataCash = {
            //     portfolioId: $('#portfolioId').val(),
            //     tradeDate: $('#tradeDate').val(),
            //     ccy: $('#ccy').val(),
            //     calendarId : $("#calendarId").val(),
            //     chlId : $("#chlId").val(),
            //     finprodId : $("#finprodId").val()
            // };
            // findCash(dataCash);
            var object = $('#portfolioId').select2("data")[0].object;
            console.log("------------------------")
            console.log(object)
            console.log(object.availableCash)
            $('#portoAvailableAmt').val(object.availableCash).trigger("change").trigger('blur');
        }
         if(!$('#portfolioId').val()){
        	$("#portoAvailableAmt").val('').trigger("change").trigger('blur');
        }
    });
    //查询组合持仓、组合可用现金
    var findCash = function (params) {
        if(params.portfolioId && params.portfolioId.trim()
            && params.calendarId && params.ccy && params.tradeDate){
            params.acctTypeList = ACCT_TYPE_A01 + "," + ACCT_TYPE_A07;
            Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/BussTrdAvailableCashQuery/getAvailableCash'), params, function (data) {
                $('#portoAvailableAmt').val(data.T_0).trigger("change").trigger('blur');
                if ($('#chlId').val()) {
                    $('#chlAvailableAmt').val(data.T_2).trigger("change").trigger('blur');
                }
            });
        }
        // Joyin.ajax('/invest/trade/transfer/deposittransfere/DepositTransfere001/queryPortoAmtAndChlAmt', data, function (result) {
        //     $('#portoAvailableAmt').val(result.portoAvailableAmt).trigger("change").trigger('blur');
        //     if ($('#chlId').val()) {
        //         $('#chlAvailableAmt').val(result.chlAvailableAmt).trigger("change").trigger('blur');
        //     }
        // });
    };

    //计算出拆分信息
    $('#transferPrinAmt').change(function () {
        var transferPrinAmt = $('#transferPrinAmt').val();
        var vDate = $('#vDate').val();
        var yeld = $('#yield').val();
        var basis = $('#basis').val();
        var tradeDate = $('#tradeDate').val();
        //应计利息总额
        var payInt = Number(transferPrinAmt) * Number(yeld) * Math.abs((new Date(tradeDate) - new Date(vDate)) / 1000 / 60 / 60 / 24) / Number(basis.split("/")[1]);
        $('#payInt').val(payInt).trigger("change").trigger('blur');
        $('#transferTradeAmt').val(Number(transferPrinAmt) + Number(payInt)).trigger("change").trigger('blur');
    });

    //联动交易金额修改事件
    $('#transferTradeAmt').change(function () {
        var transferTradeAmt = $('#transferTradeAmt').val();
        var transferPrinAmt = $('#transferPrinAmt').val();
        if(transferTradeAmt && transferPrinAmt) {
            $('#payInt').val(Number(transferTradeAmt) - Number(transferPrinAmt)).trigger("change").trigger('blur')
        }
    });

    //绑定添加按钮
    $('#DepositTransfere001_btn_add').click(function () {
        var options = $("#portfolioId option:selected").val();
        var options1 = $("#portfolioId option:selected").text().split(":")[1];
        //画面check
        if (!options || !$('#transferPrinAmt').val() || Number($("#transferPrinAmt").val()) == 0) {
            Joyin.error('组合代码和本金金额不能为空！');
            return;
        }
        if (Number($('#transferPrinAmt').val()) > Number($('#restAmt').val())) {
            Joyin.error('本金金额不能大于剩余未拆分本金金额！');
            return;
        }
        //添加检查数值小于0的检查
        if(Math.sign(Number($('#transferPrinAmt').val()))==-1){
            Joyin.error('本金金额不能小于0！');
            return;
        }
        if(Math.sign(Number($('#transferTradeAmt').val()))==-1){
            Joyin.error('交易金额不能小于0！');
            return;
        }
        if(Math.sign(Number($('#payInt').val()))==-1){
            Joyin.error('应计利息总额不能小于0！');
            return;
        }
        var tableRows = table.getAllRows();
        var flag = false;
        if (tableRows) {
            $.each(tableRows, function (idx, v) {
                if (options == v.portfolioId) {
                    Joyin.error('产品组合' + options + '在拆分列表已存在！');
                    flag = true;
                    return
                }
            });
            if (flag) {
                return;
            }
        }
        var transferTradeAmt = Number($("#transferTradeAmt").val());
        var portoAvailableAmt =  Number($("#portoAvailableAmt").val());
        if(portoAvailableAmt && transferTradeAmt > portoAvailableAmt){
            Joyin.confirm('转入组合剩余现金不足！请确认是否添加', function () {
                addRow(options, options1);
                $("#portfolioId").val('').trigger("change").trigger('blur');
                $("#transferPrinAmt").val('').trigger("change").trigger('blur');
                $("#transferTradeAmt").val('').trigger("change").trigger('blur');
                $('#payInt').val('').trigger("change").trigger('blur');
                $("#portoAvailableAmt").val('').trigger("change").trigger('blur');
                $("#chlAvailableAmt").val('').trigger("change").trigger('blur');
            });

            return;
        }
        addRow(options, options1);
        $("#portfolioId").val('').trigger("change").trigger('blur');
        $("#transferPrinAmt").val('').trigger("change").trigger('blur');
        $("#transferTradeAmt").val('').trigger("change").trigger('blur');
        $('#payInt').val('').trigger("change").trigger('blur');
        $("#portoAvailableAmt").val('').trigger("change").trigger('blur');
        $("#chlAvailableAmt").val('').trigger("change").trigger('blur');
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

    // 行内删除按钮点击事件
    table.bind('click', '.delt', function (data, index) {
        table.removeRow(data);
        var restAmt = $('#restAmt').val();
        //计算剩余可拆分金额
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
                        assetType: FINPROD_TYPE_F07,
                        isEnabled: '01',
                        assetCode: $('#finprodId').val(),
                        tradeDirection: PS_T01,
                        index: null,
                        mun: mun,
                        assetSecType: MY_SUP_TIME_TYPE_02	
                	}
                list[i] = obj;	
        	}
            object = {
                    valDate: ['tradeDate', '交易日期'],
                    portfolioCode: ['portfolioId', '组合代码'],
                    tradeAmount: ['tradeRepAmt', '交易金额'],
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
        $("#DepositTransfere001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        //风险试算
        $('#DepositTransfere001_btn_risk').click(function(){
            var data = table.getAllRows();
            if(data != null && data != ''){
                var object = $.serializeToObject('#depositTransfereForm', true);
                object.portfolioId = $("#transferPortfolioId").val();
                //深拷贝
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for(var i=0;i<bussTrdTransfenpeiDto.length;i++){
                	bussTrdTransfenpeiDto[i].counterId = bussTrdTransfenpeiDto[i].portfolioId;
                	delete bussTrdTransfenpeiDto[i].portfolioId;
                	bussTrdTransfenpeiDto[i].ps = PS_T02;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                object.tradeId = null;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F05',object,getriskdata());
            }
            else {
                Joyin.error("转仓列表信息不能为空！");
            }
        });
        //绑定提交
        $('#DepositTransfere001_btn_save').click(function () {
            var object = $.serializeToObject('#depositTransfereForm', true);
            save(object, 'save')
        });
        $('#DepositTransfere001_btn_submit').click(function () {
            var object = $.serializeToObject('#depositTransfereForm', true);
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
        object.depositTransfere002Dto = data;
        Joyin.ajaxJson(basePath + '/invest/trade/transfer/deposittransfere/DepositTransfere001/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                var elementId;
                if (operateType === 'save') {
                    elementId = "DepositTransfere001_btn_save";
                    WorkFlow.doSaveList(basePath + '/invest/trade/transfer/deposittransfere/DepositTransfere001/save', object, elementId);
                } else {
                    elementId = "DepositTransfere001_btn_submit";
                    WorkFlow.doSubmitList(basePath + '/invest/trade/transfer/deposittransfere/DepositTransfere001/save', object, elementId);
                }
            });
        });
    }

}());