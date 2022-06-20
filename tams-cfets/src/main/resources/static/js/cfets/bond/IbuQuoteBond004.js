(function () {

    /**
     * 是否可编辑
     */
    var editFlag = true;
    var rskApproval;

    var el = function (ele) {
        return $("#IbuQuoteBond004_bodydiv " + ele);
    }

    /**
     * 处理表格数据
     */
    var getRegData = function () {
        var result = [];
        if(entity && entity.length > 0) {
            $.each(entity,function (index,item) {
                if(item.portfolioId) {
                    result.push(item);
                }
            })
        }
        return result;
    }



    function query(ele) {
        return $("#IbuQuoteBond004_bodydiv " + ele);
    }


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

    /**
     * 登记组合分配列表
     * @type {*[]}
     */
    var colsReg = [
        {
            field: 'id',
            width: 80,
            title: 'ID',
            sortable: false,
            visible: false
        }
        ,
        {
            field: 'entrustId',
            width: 180,
            title: '指令代码',
            sortable: false,
            disabled: true
        }
        , {
            field: 'portfolioId',
            width: 150,
            title: '组合代码',
            edit: false,
            sortable: false,
            visible: ps !='T03',
            editOption: {
                type: 'select', id: 'portfolioId', multiple: false, showField: 'text', searchable: true,
                url: JoyinUtil.getRealPath('/business/common/CommonSelect/selectPortfolioId?act=true&&auth=false')
            }
        }
        , {
            field: 'portfolioName',
            width: 160,
            title: '组合名称',
            edit: false,
            sortable: false,
        }
        , {
            field: 'avaNum',
            width: 160,
            title: '剩余指令额度（万元）',
            type: 'number', sortable: false,
            visible: ps !='T03'
        }
        , {
            field: 'shareAmt',
            width: 150,
            title: '券面总额（万元）',
            sortable: false,
            edit: true,
            type: 'number', digit: 2
        }
        , {
            field: 'ourEntNum',
            width: 150,
            title: '本方券券面总额（万元）',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: sellPara !== "02"
        }
        , {
            field: 'lendinEntNum',
            width: 150,
            title: '融入券券面总额（万元）',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: sellPara !== "02"
        }
        , {
            field: 'sellAbleNum',
            width: 150,
            title: '卖空购回券面总额（万元）',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: sellPara !== "02"
        }
        , {
            field: 'dealFeeAmt',
            width: 150,
            title: '交易手续费（元）',
            sortable: false,
            edit: true,
            type: 'number',
            visible: ps !=='T03',
            digit: 2
        }
        , {
            field: 'settFeeAmt',
            width: 150,
            title: '结算服务费（元）',
            sortable: false,
            edit: true,
            type: 'number',
            digit: 2
        }
        , {
            field: 'cpriceAmt',
            width: 150,
            title: '净价总额（元）',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: ps !== 'T03'
        }
        , {
            field: 'intAmt',
            width: 150,
            title: '应计利息总额（元）',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: ps !== 'T03'
        }
        , {
            field: 'fpriceAmt',
            width: 150,
            title: '全价总额（元）',
            sortable: false,
            type: 'number',
            digit: 2,
            visible: ps !== 'T03'
        }
        , {
            field: 'businessModel',
            width: 100,
            title: '业务模式',
            sortable: true,
            edit: true,
            editOption: {
                type: 'select',
                id: "businessModel",
                formatoption: true,
                result: BUSINESS_MODEL
            },
            visible: true
        }
        , {
            field: 'priceModel',
            width: 100,
            title: '价格模式',
            sortable: false,
            edit: true,
            editOption: {
                type: 'select',
                id: "priceModel",
                formatoption: true,
                result: PRICE_MODEL
            },
            visible: true
        }
        , {field: 'secAcctName', width: 150, title: '证券账户', sortable: false}
        , {field: 'price', width: 120, title: '指令净价(元)', sortable: false, type: 'number', digit: 2}
        , {
            field: 'matureYield',
            width: 120,
            title: '到期收益率%',
            edit: false,
            sortable: false,
            visible: true,
            type: 'number',
            digit: 4,
            visible: ps !== 'T03'
        }
        , {
            field: 'exerYield',
            width: 120,
            title: '行权收益率%',
            sortable: false,
            type: 'number',
            digit: 4,
            visible: ps !== 'T03'
        }

    ];
    var table = $("#cfetsQuoTedCommonTable").joyintable({
        data: getRegData(),
        searchOnInit: true,
        loadonce: true,
        page: false,
        addbtn: false,
        checkbox: false,
        height: 200,
        cols: colsReg,
        editCell: function (value, data, field, cell, column) {
            if ('shareAmt' === field) {//修改时同步修改
                if(value > data.avaNum) {
                    Joyin.error("券面总额不得高于指令剩余额度")
                    return;
                }
                el("#shareAmt").val(JoyinMath.doDiv(value,1,2));
                calcAmt();
                autoAllocation();
            }
            if(ps === 'T03' && field === 'settFeeAmt') {
                el("#settFeeAmt").val(JoyinMath.doDiv(value,1,2));
            }
        },
        beforeEditCell: function (preVal, field, rowData) {
            if (field === "avaNum") {
                tempAvaNum = preVal.replace(/\,/g, '');
            }
            if (field === 'businessModel' || field === 'priceModel') {
                return false;
            }
            if(ps === 'T03') {
                if (field === 'shareAmt') {
                    return false;
                }
            }
            return true;
        }
    });


    var getSubmitParam = function (operatorType) {
        var object = $.serializeToObject('#submitForm', true);
        object.operatorType = operatorType;
        object.isDzh = 'Y';
        object.pageId = "IbuQuoteBond004";
        object.shareAmt = JoyinMath.doMul(object.shareAmt, 10000);
        if (object.matureYieldShow) {
            object.matureYield = JoyinMath.doDiv(object.matureYieldShow, 100, 6);
        }
        if (object.exerYieldShow) {
            object.exerYield = JoyinMath.doDiv(object.exerYieldShow, 100, 6);
        }
        if (object.cfetsBondDealDialogueQuoteDtoList) {
            $.each(object.cfetsBondDealDialogueQuoteDtoList, function (index, obj) {
                obj.shareAmt = JoyinMath.doMul(obj.shareAmt, 10000);
                obj.avaNum = JoyinMath.doMul(obj.avaNum, 10000);
                obj.avaNumOld = JoyinMath.doMul(obj.avaNumOld, 10000);
                obj.ourEntNum = JoyinMath.doMul(obj.ourEntNum, 10000);
                obj.lendinEntNum = JoyinMath.doMul(obj.lendinEntNum, 10000);
                obj.matureYield = obj.matureYield / 100
                obj.exerYield = obj.exerYield / 100
            });

        }
        object.ptlAllocatios = object.cfetsBondDealDialogueQuoteDtoList;
        object.cfetsBondDealDialogueQuoteDtoList = null;
        object.quoteStyle = $("input:radio[name=quoteStyle]:checked").val();
        if($("input:radio[name=calculateField]:checked").val()) {
            object.calculateField = $("input:radio[name=calculateField]:checked").val();
        }
        object.workflowId = workflowId;
        object.rskApproval = rskApproval;
        return object;
    }

    /**
     * 保存
     */
    var saveOrSubmitAction = function (operatorType) {
        var object = getSubmitParam(operatorType);
        var msg = "是否确认提交?";
        if("save" === operatorType ) {
            msg = "是否确认保存?";
        }
        Joyin.confirm(msg,
            function () {
                //保存 走流程
                if (operatorType === 'save') {
                    WorkFlow.doSaveList(basePath + 'cfets/bond/IbuQuoteBond004/save', object, "IbuQuoteBond004_btn_save", "IbuQuoteBond004",
                        function () {
                            var index = window.parent.layer.getFrameIndex(window.name);
                            window.parent.layer.close(index);
                        })
                }
                //提交 走流程
                else if (operatorType === 'submit') {
                    WorkFlow.doSubmitList(basePath + 'cfets/bond/IbuQuoteBond004/save', object, "IbuQuoteBond004_btn_submit", "IbuQuoteBond004",
                        function () {
                            var index = window.parent.layer.getFrameIndex(window.name);
                            window.parent.layer.close(index);
                        })
                }
            })
    };

    /**
     * 参数校验
     */
    var paramCheck = function () {
        var object = $.serializeToObject('#submitForm', true);
        var b = false;
        if(ps !== 'T03') {
            if(!el("#CfetsBondDealQuotedPrice004_counterId").val() || !el("#shareAmt").val()) {
                Joyin.error("有*必填项未填写,请填写完整")
                return false;
            }
            var quoteStyle = $("[name=quoteStyle]:checked").val();
            if( quoteStyle === '01' || quoteStyle === '02') {
                if(!el("#CfetsBondDealQuotedPrice004_counterTrader").val()) {
                    Joyin.error("有*必填项未填写,请填写完整")
                    return false;
                }
            }

        }else {
            if(!el("#settFeeAmt").val()) {
                Joyin.error("结算服务费不能为空")
                return false;
            }
        }
        if (object.cfetsBondDealDialogueQuoteDtoList) {
            let sumShareAmt = 0;
            $.each(object.cfetsBondDealDialogueQuoteDtoList, function (index, obj) {
                if(!obj.shareAmt) {
                    b = true;
                }else {
                    sumShareAmt = JoyinMath.doAdd(sumShareAmt,obj.shareAmt);
                }
            });
            if(sumShareAmt > object.shareAmt) {
                Joyin.error("组合分配中券面总额汇总不得高于基本信息中券面总额");
                return;
            }
        }else {
            b = true;
        }
        if(b) {
            Joyin.error("组合分配信息,请填写完整")
            return false;
        }
        var obj = getSubmitParam("submit");
        var errorMsg = "";
        var confirmMsg = "";
        Joyin.ajaxJson(basePath + 'cfets/bond/IbuQuoteBond004/check', obj, function (result) {
           if(result.data && result.data.errorList && result.data.errorList.length > 0) {
               $.each(result.data.errorList,function (index,item) {
                   errorMsg = errorMsg + item.message + "<br\>";
               })
           }
            if(result.data && result.data.message && result.data.messageType === 'confirm') {
                confirmMsg = result.data.message;
            }
        },false)
        if(errorMsg && errorMsg.length > 0) {
            Joyin.error(errorMsg);
            return false;
        }

        if(confirmMsg && confirmMsg.length > 0) {
            rskApproval = result.data.data;
            return Joyin.confirm(confirmMsg, function (){
                saveOrSubmitAction('submit');
            });
        }
        return true;
    }


    /**
     * 计算金额相关
     */
    var calcAmt = function () {
        var calcField = $("[name=calculateField]:checked").val();
        var finprodId = $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val();
        if (calcField === calcTypeUnitCprice && !$('#CfetsBondDealQuotedPrice004_unitCprice').val()) {
            return;
        }
        if (calcField === calcTypeYield && !$('#CfetsBondDealQuotedPrice004_matureYield').val()) {
            return;
        }
        if (calcField === calcTypeExecYield && !$('#CfetsBondDealQuotedPrice004_exerYield').val()) {
            return;
        }
        var object = $.serializeToObject('#submitForm', true);
        var param = {};
        param.calculateField = calcField;
        param.finprodId = finprodId;
        param.shareAmt = object.shareAmt * 10000;
        param.settleDate = object.settleDate;
        param.price = object.price;
        param.fpriceAmt = object.fpriceAmt;
        if(el("#CfetsBondDealQuotedPrice004_matureYield").val()) {
            param.matureYield = el("#CfetsBondDealQuotedPrice004_matureYield").val() / 100;
        }
        if(calcField === calcTypeExecYield && el("#CfetsBondDealQuotedPrice004_exerYield").val()) {
            param.exerYield = el("#CfetsBondDealQuotedPrice004_exerYield").val() / 100;
        }
        param.ps = object.ps;
        param.secManageAcctId = object.secManageAcctId;
        Joyin.ajaxJson(basePath + 'cfets/bankuplink/bond/IbuQuoteBond/calculation', param, function (result) {
            if (result.data) {
                var data = result.data;
                el("#CfetsBondDealQuotedPrice004_unitCprice").val(JoyinMath.doDiv(data.price, 1, 4));
                el("#CfetsBondDealQuotedPrice004_unitCprice2").val(JoyinMath.doDiv(data.price, 1, 4));
                el("#CfetsBondDealQuotedPrice004_matureYield").val(JoyinMath.doMul(JoyinMath.doDiv(data.matureYield, 1, 6), 100, 4));
                if(data.exerYield) {
                    el("#CfetsBondDealQuotedPrice004_exerYield").val(JoyinMath.doMul(JoyinMath.doDiv(data.exerYield, 1, 6), 100, 4));
                }
                el("#CfetsBondDealQuotedPrice004_cpriceAmt").val(JoyinMath.doDiv(data.cpriceAmt, 1, 2));
                el("#CfetsBondDealQuotedPrice004_unitInt").val(JoyinMath.doDiv(data.unitInt, 1, 5));
                el("#CfetsBondDealQuotedPrice004_unitFprice").val(JoyinMath.doDiv(data.unitFprice, 1, 4));
                el("#CfetsBondDealQuotedPrice004_intAmt").val(JoyinMath.doDiv(data.intAmt, 1, 2));
                el("#CfetsBondDealQuotedPrice004_fpriceAmt").val(JoyinMath.doDiv(data.fpriceAmt, 1, 2));
                el("#dealFeeAmt").val(JoyinMath.doDiv(data.dealFeeAmt, 1, 2));
                el("#settFeeAmt").val(JoyinMath.doDiv(data.settFeeAmt, 1, 2));

                //hidden
                el("#CfetsBondDealQuotedPrice004_hidden_price").val(data.price);
                el("#CfetsBondDealQuotedPrice004_hidden_unitCprice2").val(data.price);
                el("#CfetsBondDealQuotedPrice004_hidden_matureYield").val(data.matureYield * 100);
                if(data.exerYield) {
                    el("#CfetsBondDealQuotedPrice004_hidden_exerYield").val(data.exerYield * 100);
                }
                el("#CfetsBondDealQuotedPrice004_hidden_cpriceAmt").val(data.cpriceAmt);
                el("#CfetsBondDealQuotedPrice004_hidden_unitInt").val(data.unitInt);
                el("#CfetsBondDealQuotedPrice004_hidden_unitFprice").val(data.unitFprice);
                el("#CfetsBondDealQuotedPrice004_hidden_intAmt").val(data.intAmt);
                el("#CfetsBondDealQuotedPrice004_hidden_fpriceAmt").val(data.fpriceAmt);

                el("#CfetsBondDealQuotedPrice004_hidden_parValue").val(data.parValue);
                el("#CfetsBondDealQuotedPrice004_hidden_parValueAmt").val(data.parValueAmt);
                el("#deviationDegree").html(data.deviationDegree);//偏离度
                if(data.assetType11 === '127' || data.assetType11 === '128') {//信贷资产支持证券、资产支持票据时显示每百元本金额、本金额
                    el("#CfetsBondDealQuotedPrice004_div_abs1").show();
                    el("#CfetsBondDealQuotedPrice004_div_abs2").show();
                    el("#CfetsBondDealQuotedPrice004_parValue").val(data.parValue);
                    el("#CfetsBondDealQuotedPrice004_parValueAmt").val(data.parValueAmt);
                }
            }
        })
    };

    /**
     * 计算金额相关
     */
    var autoAllocation = function () {
        var calcField = $("[name=calculateField]:checked").val();
        var finprodId = $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val();
        if (calcField === calcTypeUnitCprice && !$('#CfetsBondDealQuotedPrice004_unitCprice').val()) {
            Joyin.error("无法自动分配，请先进行金额计算");
            return;
        }
        if (calcField === calcTypeYield && !$('#CfetsBondDealQuotedPrice004_matureYield').val()) {
            Joyin.error("无法自动分配，请先进行金额计算");
            return;
        }
        if (calcField === calcTypeExecYield && !$('#CfetsBondDealQuotedPrice004_exerYield').val()) {
            Joyin.error("无法自动分配，请先进行金额计算");
            return;
        }
        if (!el("#distribution").val()) {
            Joyin.error("无法自动分配，请先选择分配方式");
            return;
        }
        var object = $.serializeToObject('#submitForm', true);
        var param = {};
        param.calculateField = calcField;
        param.finprodId = finprodId;
        param.shareAmt = object.shareAmt * 10000;
        param.settleDate = object.settleDate;
        param.price = object.price;
        param.fpriceAmt = object.fpriceAmt;
        if(calcField === calcTypeYield && el("#CfetsBondDealQuotedPrice004_matureYield").val()) {
            param.matureYield = el("#CfetsBondDealQuotedPrice004_matureYield").val() / 100;
        }
        if(calcField === calcTypeExecYield && el("#CfetsBondDealQuotedPrice004_exerYield").val()) {
            param.exerYield = el("#CfetsBondDealQuotedPrice004_exerYield").val() / 100;
        }
        param.ps = object.ps;
        param.secManageAcctId = object.secManageAcctId;
        param.cpriceAmt = object.cpriceAmt;
        param.dealFeeAmt = object.dealFeeAmt;
        param.distribution = object.distribution;
        param.intAmt = object.intAmt;
        param.settFeeAmt = object.settFeeAmt;
        param.unitFprice = object.unitFprice;
        param.fpriceAmt = object.fpriceAmt;
        if (object.cfetsBondDealDialogueQuoteDtoList) {
            $.each(object.cfetsBondDealDialogueQuoteDtoList, function (index, obj) {
                obj.shareAmt = JoyinMath.doMul(obj.shareAmt, 10000);
                obj.avaNum = JoyinMath.doMul(obj.avaNum, 10000);
                obj.avaNumOld = JoyinMath.doMul(obj.avaNumOld, 10000);
                obj.ourEntNum = JoyinMath.doMul(obj.ourEntNum, 10000);
                obj.lendinEntNum = JoyinMath.doMul(obj.lendinEntNum, 10000);
            });
        }
        param.ptlAllocations = object.cfetsBondDealDialogueQuoteDtoList;
        Joyin.ajaxJson(basePath + 'cfets/bankuplink/bond/IbuQuoteBond/autoAllocation', param, function (result) {
            if (result.data) {
                table.clear();
                if(result.data) {
                    $.each(result.data, function (index, obj) {
                        obj.shareAmt = JoyinMath.doDiv(obj.shareAmt, 10000,2);
                        obj.avaNum = JoyinMath.doDiv(obj.avaNum, 10000,2);
                        obj.avaNumOld = JoyinMath.doDiv(obj.avaNumOld, 10000,2);
                        obj.ourEntNum = JoyinMath.doDiv(obj.ourEntNum, 10000,2);
                        obj.lendinEntNum = JoyinMath.doDiv(obj.lendinEntNum, 10000,2);
                    });
                    table.addRow(result.data);
                }
            }
        })
    };

    /**
     * 画面初始化
     */
    var initPage = function () {

    }


    /**
     * 控制组件开关
     */
    var enableControl = function (flag) {
        el("#ps").attr('disabled', true);
        el("#finprodMarketId").attr('disabled', true);
        el("#finprodAbbr").attr('disabled', true);
        el("#mySideAbbr").attr('disabled', true);
        el("#ouTraderName").attr('disabled', true);
        el("#ouTraderName").attr('disabled', true);
        el("#CfetsBondDealQuotedPrice004_settleSpreed").attr('disabled', true);
        el("#CfetsBondDealQuotedPrice004_settleDate").attr('disabled', true);
        el("#ccy").attr('disabled', true);
        el("#CfetsBondDealQuotedPrice004_settleMethod").attr('disabled', true);
        el("#CfetsBondDealQuotedPrice004_settleType").attr('disabled', true);
        let calculateField = $("[name=calculateField]:checked").val();
        if (calculateField === '01') {
            if(ps === 'T01') {
                $("input[name='calculateField'][value='03']").attr('disabled', true);
                //$("input[name='calculateField'][value='02']").attr('disabled', true);
                layui.form.render('radio');
            }
            el("#CfetsBondDealQuotedPrice004_unitCprice").attr('disabled', false);
            el("#CfetsBondDealQuotedPrice004_matureYield").attr('disabled', true);
            el("#CfetsBondDealQuotedPrice004_exerYield").attr('disabled', true);
        }
        if (calculateField === '02') {
            el("#CfetsBondDealQuotedPrice004_unitCprice").attr('disabled', true);
            el("#CfetsBondDealQuotedPrice004_matureYield").attr('disabled', false);
            el("#CfetsBondDealQuotedPrice004_exerYield").attr('disabled', true);
        }
        if (calculateField === '03') {
            el("#CfetsBondDealQuotedPrice004_unitCprice").attr('disabled', true);
            el("#CfetsBondDealQuotedPrice004_matureYield").attr('disabled', true);
            el("#CfetsBondDealQuotedPrice004_exerYield").attr('disabled', false);
        }
        if ("Y" === el("#CfetsBondDealQuotedPrice004_hidden_isRight")) {
            $("input[name='calculateField'][value='03']").attr('disabled', false);
            layui.form.render('radio');
        }
    }


    //绑定风险试算
    $('#IbuQuoteBond004_btn_riskcal').click(function () {
        var object = $.serializeToObject('#submitForm');
        var param = JSON.parse(JSON.stringify(object));
        param.ptlAllocatios = param.cfetsBondDealDialogueQuoteDtoList;
        if ( param.ptlAllocatios) {
            $.each(param.ptlAllocatios, function (index, obj) {
                obj.shareAmt = JoyinMath.doMul(obj.shareAmt, 10000);
                obj.avaNum = JoyinMath.doMul(obj.avaNum, 10000);
                obj.avaNumOld = JoyinMath.doMul(obj.avaNumOld, 10000);
                obj.ourEntNum = JoyinMath.doMul(obj.ourEntNum, 10000);
                obj.lendinEntNum = JoyinMath.doMul(obj.lendinEntNum, 10000);
            });
        }
        $.ajax({
            type: "POST",
            url: basePath + '/riskTrial/doRiskcal/IbuQuoteBond004',
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
                    riskResultResponse = data;
                    entity_RiskTrialRetList = data.riskcalReturnList;
                    if(entity_RiskTrialRetList) {
                        $.each(entity_RiskTrialRetList, function (index, item) {
                            item.conclusion = data.riskTrialSpan;
                        })
                    }
                    tableRisk.clear();
                    tableRisk.addRow(entity_RiskTrialRetList);
                }else if(result.message){
                    Joyin.error(result.message);
                }
            }
        });
    });

    $(function () {
        // 初始化页面
        initPage();
        enableControl();

        if(ps != PS_T03){
            $("#riskTrialRet_div").hide();
            $("#IbuQuoteBond004_btn_riskcal").hide();
        }

        layui.use(['form'], function () {
            // 绑定计算模式的点击事件
            var form = layui.form;
            form.on('radio(calculateField)', function (data) {
                let calculateField = $("[name=calculateField" + "]:radio").next(".layui-form-radioed").prev("input").val();
                if (calculateField === calcTypeUnitCprice) {
                    el("#CfetsBondDealQuotedPrice004_unitCprice").attr("disabled", false);
                    el("#CfetsBondDealQuotedPrice004_matureYield").attr("disabled", true);
                    el("#CfetsBondDealQuotedPrice004_exerYield").attr("disabled", true);
                }
                if (calculateField === calcTypeYield ) {//&& ps !== 'T01'
                    el("#CfetsBondDealQuotedPrice004_unitCprice").attr("disabled", true);
                    el("#CfetsBondDealQuotedPrice004_matureYield").attr("disabled", false);
                    el("#CfetsBondDealQuotedPrice004_exerYield").attr("disabled", true);
                }
                if (calculateField === calcTypeExecYield && ps !== 'T01') {
                    el("#CfetsBondDealQuotedPrice004_unitCprice").attr("disabled", true);
                    el("#CfetsBondDealQuotedPrice004_matureYield").attr("disabled", true);
                    el("#CfetsBondDealQuotedPrice004_exerYield").attr("disabled", false);
                }
                calcAmt();
                layui.form.render();
            });
        });

        /**
         * 保存按钮按下处理
         */
        $('#IbuQuoteBond004_btn_save').click(function () {
            saveOrSubmitAction('save');
        });

        /**
         * 提交按钮按下处理
         */
        $('#IbuQuoteBond004_btn_submit').click(function () {
            if(paramCheck()) {
                saveOrSubmitAction('submit');
            }
        });

        /**
         * 本方
         */
        $('#CfetsBondDealQuotedPrice004_mySide').change(function () {
            mySideChangeAction();
        });
        /**
         * 对手方方
         */
        $('#CfetsBondDealQuotedPrice004_counterId').change(function () {
            counterIdChangeAction();
        });
        el("#CfetsBondDealQuotedPrice004_unitCprice").change(function () {//净价
            if(el("#CfetsBondDealQuotedPrice004_unitCprice").val()) {
                el("#CfetsBondDealQuotedPrice004_hidden_price").val(el("#CfetsBondDealQuotedPrice004_unitCprice").val());
                calcAmt();
            }
        })
        el("#CfetsBondDealQuotedPrice004_matureYield").change(function () {//到期
            var v = el("#CfetsBondDealQuotedPrice004_matureYield").val();
            if(v) {
                el("#CfetsBondDealQuotedPrice004_hidden_matureYield").val(v /100);
                var allRow = table.getAllRows();
                var b = false;
                $.each(allRow, function (index, item) {
                    if(v && item.matureYield && v < item.matureYield) {
                        Joyin.error("到期收益率不能小于指令的到期收益率");
                        b = true;
                        el("#CfetsBondDealQuotedPrice004_matureYield").val(0)
                    }
                })
                if(!b) {
                    calcAmt();
                }
            }
        })
        el("#CfetsBondDealQuotedPrice004_exerYield").change(function () {//行权
            if(el("#CfetsBondDealQuotedPrice004_exerYield").val()) {
                el("#CfetsBondDealQuotedPrice004_hidden_exerYield").val(el("#CfetsBondDealQuotedPrice004_exerYield").val()/100);
                calcAmt();
            }
        })

        el("#btn_autoAllocation").click(function () {
            autoAllocation();
        })
        el("#shareAmt").change(function () {
            calcAmt();
            if (!el("#distribution").val()) {
                autoAllocation();
            }
        })
        if (ps === 'T03') {//回售
            el("#shareAmt").attr("disabled", true);
            el("#dealFeeAmtDiv").hide();
            el("#xqPrice").attr("disabled", true);
            el("#distributionDiv").hide();
        }

    });

    /**
     * 本方
     */
    var mySideChangeAction = function () {
        $('#CfetsBondDealQuotedPrice004_mySideTrader').val('').trigger('change');
    }

    /**
     * 对手方
     */
    var counterIdChangeAction = function () {
        var data = el("#CfetsBondDealQuotedPrice004_counterId").select2('data')[0];
        if(data) {
            el("#CfetsBondDealQuotedPrice004_counterName").val(data.text);
        }
        $('#CfetsBondDealQuotedPrice004_counterTrader').val('').trigger('change');
    }

}());