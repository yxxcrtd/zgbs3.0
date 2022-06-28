(function(){

    /**
     * 是否可编辑
     */
    var editFlag = true;
    //这个标志是防止查询结果返回赋值后不停的联动calcAmt;
    var entrustIdChangeFlag = false;

    // 是否 abs
    let isAbs = $('#isAbs').val();
    // 是否特殊债
    let isSpecialDebt = $('#isSpecialDebt').val();
    // 是否到期违约债
    let isDefaultBondIndicator = $('#isDefaultBondIndicator').val();
    // 是否锚定净价
    let isSpecial = $('#isSpecial').val();
    /**
     * 委托代码
     */
    var entrustIdChangeAction = function(){
        var entrustId = $('#CfetsBondDealQuotedPrice004_entrustId').val();
        if (entrustId && entrustId != ''){
            Joyin.ajax(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice004/queryEntrustInfoByEntrustId', {entrustId : entrustId}, function(result) {
                if (result.data){
                    entrustIdChangeFlag = true;
                    $('#CfetsBondDealQuotedPrice004_remark').val(result.data.remark);
                    $('#CfetsBondDealQuotedPrice004_hidden_entrustAmt').val(result.data.entrustNum);
                    $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val(result.data.finprodId);
                    // 区分画面打开模式
                    var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();

                    if (result.data.calculateField){
                        if (result.data.calculateField === calcTypeUnitCprice){
                            $("input[name=calculateField][value=01]").prop("checked","true");
                            $('#calculateField_span_unitCprice').css({'color':'blue'});
                            $('#calculateField_span_yield').css({'color':'black'});
                            $('#calculateField_span_exerYield').css({'color':'black'});
                        }else if (result.data.calculateField === calcTypeYield){
                            $("input[name=calculateField][value=02]").prop("checked","true");
                            $('#calculateField_span_yield').css({'color':'blue'});
                            $('#calculateField_span_unitCprice').css({'color':'black'});
                            $('#calculateField_span_exerYield').css({'color':'black'});
                        }else if (result.data.calculateField === calcTypeExecYield){
                            $("input[name=calculateField][value=03]").prop("checked","true");
                            $('#calculateField_span_exerYield').css({'color':'blue'});
                            $('#calculateField_span_unitCprice').css({'color':'black'});
                            $('#calculateField_span_yield').css({'color':'black'});
                        }
                        layui.form.render();
                    }

                    // 回复对话报价
                    if (quotaPageModel && (quotaPageModel == 'dialogueResponse')){
                        $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true).textSelect('setValue',result.data.portfolioId);
                    }
                    else {
                        // 做市方回复
                        if (quotaPageModel != 'marketResponse'){
                            // 这些要素应该是做市方已有的信息
                            $('#CfetsBondDealQuotedPrice004_mySide').textSelect('setValue',result.data.mySide);
                            $('#CfetsBondDealQuotedPrice004_counterId').textSelect('setValue',result.data.counterId);
                            $('#CfetsBondDealQuotedPrice004_counterTrader').textSelect('setValue',result.data.counterTrader);
                            $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true).textSelect('setValue',result.data.finprodMarketId);
                            $('#CfetsBondDealQuotedPrice004_ps').val(result.data.ps).trigger('change').attr('disabled', true);
                            $('#CfetsBondDealQuotedPrice004_settleSpreed').val(result.data.settleSpreed).trigger('change').attr('disabled', true);
                            $('#CfetsBondDealQuotedPrice004_settleDate').val(result.data.settleDateStr).attr('disabled', true);
                            $('#CfetsBondDealQuotedPrice004_avaNum').val(result.data.avaNumStr).trigger('blur');
                        }else{
                            // 做市报价回复选择委托后回复量默认显示委托剩余额度
                            $('#CfetsBondDealQuotedPrice004_responseAmt').val(result.data.avaNumStr).trigger('blur');
                        }
                        // 对话报价发起模式
                        $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true).textSelect('setValue',result.data.portfolioId);
                        $('#CfetsBondDealQuotedPrice004_mySide').textSelect('setValue',result.data.mySide);
                        $('#CfetsBondDealQuotedPrice004_mySideTrader').textSelect('setValue',result.data.mySideTrader);
                        $('#CfetsBondDealQuotedPrice004_settleMethod').val(result.data.settleMethod).trigger('change');
                        $('#CfetsBondDealQuotedPrice004_settleType').val(result.data.settleType).trigger('change');
                        $('#CfetsBondDealQuotedPrice004_div_settleCcy').val(result.data.settleCcy).trigger('change');
                        $('#CfetsBondDealQuotedPrice004_unitCprice').val(result.data.price).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_matureYield').val(result.data.matureYield).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_exerYield').val(result.data.exerYield).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_unitInt').val(result.data.unitInt).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_unitIntEdit').val(result.data.unitInt).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_unitFprice').val(result.data.unitFprice).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_cpriceAmt').val(result.data.cpriceAmt).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_parValue').val(result.data.parValue).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_intAmt').val(result.data.intAmt).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_fpriceAmt').val(result.data.fpriceAmt).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_fpriceAmtEdit').val(result.data.fpriceAmt).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_parValueAmt').val(result.data.parValueAmt).trigger('blur');

                        $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true);
                        $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', true);
                        $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true);
                        $('#CfetsBondDealQuotedPrice004_settleSpreed').attr('disabled', true);
                        //add by lijie2
                        if(result.data && result.data.priceModel == PRICE_MODEL_PRM01){
                            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
                            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
                            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
                            $("input[name='calculateField']").attr('disabled', true);
                            $("#CfetsBondDealQuotedPrice004_hidden_calculateField").val("01");
                            $("input[name=calculateField][value=01]").prop("checked","true");
                            layui.form.render();
                        }else if(result.data && result.data.assetTypeOne == assetTypeOne_abs){
                            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
                            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
                            $("input[name='calculateField']").attr('disabled', true);
                            $("#CfetsBondDealQuotedPrice004_hidden_calculateField").val("01");
                            $("input[name=calculateField][value=01]").prop("checked","true");
                            layui.form.render();
                        }else{
                            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', false);
                            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', false);
                            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', false);
                            $("input[name='calculateField']").attr('disabled', false);
                            $("#CfetsBondDealQuotedPrice004_hidden_calculateField").val(result.data.calculateField);
                            layui.form.render();
                        }
                    }
                    entrustIdChangeFlag = false;
                }
            });
        }
        else {
            $('#CfetsBondDealQuotedPrice004_remark').val("");
            var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();
            //add by yzg 20210116 委托代码清空联动清空 start
            if(!quotaPageModel || (quotaPageModel != 'dialogueResponse')){
                $('#CfetsBondDealQuotedPrice004_hidden_entrustAmt').val('');
                $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val('');
                $('#CfetsBondDealQuotedPrice004_mySide').val('').trigger('change');
                $('#CfetsBondDealQuotedPrice004_counterId').val('').trigger('change');
                $('#CfetsBondDealQuotedPrice004_counterTrader').textSelect('setValue','');
                $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', false).val('').trigger('change');
                $('#CfetsBondDealQuotedPrice004_ps').val('').trigger('change').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_settleSpreed').val('01').trigger('change').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_settleDate').val('');
                $('#CfetsBondDealQuotedPrice004_avaNum').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_responseAmt').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true).val('').trigger('change');
                $('#CfetsBondDealQuotedPrice004_mySideTrader').val('');
                $('#CfetsBondDealQuotedPrice004_settleMethod').val('03').trigger('change');
                $('#CfetsBondDealQuotedPrice004_settleType').val('01').trigger('change');
                $('#CfetsBondDealQuotedPrice004_div_settleCcy').val('CNY').trigger('change');
                $('#CfetsBondDealQuotedPrice004_unitCprice').val('').trigger('blur').attr('disabled', false);;
                $('#CfetsBondDealQuotedPrice004_matureYield').val('').trigger('blur').attr('disabled', false);;
                $('#CfetsBondDealQuotedPrice004_exerYield').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_unitInt').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_unitIntEdit').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_unitFprice').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_cpriceAmt').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_parValue').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_intAmt').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_fpriceAmt').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_fpriceAmtEdit').val('').trigger('blur');
                $('#CfetsBondDealQuotedPrice004_parValueAmt').val('').trigger('blur');
            }else if(quotaPageModel == 'dialogueResponse'){
                $('#CfetsBondDealQuotedPrice004_portfolioName').val('').trigger('change');
            }
            //add by yzg 20210116 委托代码清空联动清空 end
            if (ibuFlag_startDialogueQuoteWithNoEntrust == '02'){
                $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', false);
            }
            $('#CfetsBondDealQuotedPrice004_settleDate').val(platDate);
        }
    };
    /**
     * 债券简称
     */
    var finprodNameChangeAction = function(){
        var data = $('#CfetsBondDealQuotedPrice004_finprodName').select2('data')[0].object;
        if (data){
            $('#CfetsBondDealQuotedPrice004_finprodMarketId').val(data.finprodMarketId);
            $("#CfetsBondDealQuotedPrice004_hidden_finprodId").val(data.finprodId);
            if (data.isRight == yesNoFlag_yes){
                $('#CfetsBondDealQuotedPrice004_div_exerYield').toggle(true);
            }
            else{
                $('#CfetsBondDealQuotedPrice004_div_exerYield').toggle(false);
            }
            if (data.assetType1 == assetTypeOne_abs){
                $('#CfetsBondDealQuotedPrice004_div_abs1').toggle(true);
                $('#CfetsBondDealQuotedPrice004_div_abs2').toggle(true);

                $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
                $("input[name='calculateField']").attr('disabled', true);
                $("#CfetsBondDealQuotedPrice004_hidden_calculateField").val("01");
                $("input[name=calculateField][value=01]").prop("checked","true");
                layui.form.render();

            }else{
                $('#CfetsBondDealQuotedPrice004_div_abs1').toggle(false);
                $('#CfetsBondDealQuotedPrice004_div_abs2').toggle(false);
                $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', false);
                $("input[name='calculateField']").attr('disabled', false);
            }
            $('#CfetsBondDealQuotedPrice004_settleCcy').val(data.ccy).trigger("change");
        }else{
            $('#CfetsBondDealQuotedPrice004_finprodMarketId').val('');
            $("#CfetsBondDealQuotedPrice004_hidden_finprodId").val('');
        }
        var calcField = $("[name=calculateField]:checked").val();
        if(calcField){
            calcAmt(calcField);
        }
    };
    /**
     * 组合名称
     */
    var portfolioNameChangeAction = function(){
        var portfolioId = $('#CfetsBondDealQuotedPrice004_portfolioName').val();
        $('#CfetsBondDealQuotedPrice004_portfolioId').val(portfolioId);
        var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();
        if(quotaPageModel != 'dialogueResponse'){
            $('#CfetsBondDealQuotedPrice004_mySide').val('').trigger('change');
        }
    };
    /**
     * 清算速度
     */
    var settleSpreedChangeAction = function(){
        var settleSpreed = $('#CfetsBondDealQuotedPrice004_settleSpreed').val();
        if (settleSpreed == ACCOUNT_SPEED_1 || settleSpreed == ACCOUNT_SPEED_2){
            Joyin.ajax(basePath + 'invest/bankuplink/common/CfetsQuotedCommon/getNextWorkDay', {
                'tradeDate': platDate,
                'settleSpreed': settleSpreed
            }, function (date) {
                $("#CfetsBondDealQuotedPrice004_settleDate").val(date).trigger("change");
                var calcField = $("[name=calculateField]:checked").val();
                if(calcField){
                    calcAmt(calcField);
                }
            });
        }else{
            $('#CfetsBondDealQuotedPrice004_settleDate').val(platDate);
            var calcField = $("[name=calculateField]:checked").val();
            if(calcField){
                calcAmt(calcField);
            }
        }
    };
    /**
     * 组合分配列表
     * @type {*[]}
     */
    var colsReg = [
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
            disabled: true
        }
        , {
            field: 'portfolioName',
            width: 160,
            title: '组合名称',
            edit: false,
            sortable: false,
        }
        , {
            field: 'entrustAvaNum',
            width: 160,
            title: '剩余指令额度（万元）',
            type: 'number', sortable: false
        }
        , {
            field: 'avaNum',
            width: 150,
            title: '券面总额（万元）',
            sortable: false,
            edit: true,
            type: 'number', digit: 2
        }
        , {
            field: 'dealFeeAmt',
            width: 150,
            title: '交易手续费（元）',
            sortable: false,
            edit: true,
            type: 'number',
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
            title: '交易金额（元）',
            sortable: false,
            type: 'number',
            digit: 2
        }
        , {
            field: 'intAmt',
            width: 150,
            title: '应计利息总额（元）',
            sortable: false,
            type: 'number',
            digit: 2
        }
        , {
            field: 'fpriceAmt',
            width: 150,
            title: '结算金额（元）',
            sortable: false,
            type: 'number',
            digit: 2
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
            digit: 4
        }
       , {
            field: 'exerYield',
            width: 120,
            title: '行权收益率%',
            sortable: false,
            type: 'number',
            digit: 4
        }

    ];
    var tableTedCommon = $("#cfetsQuoTedCommonTable").joyintable({
        data: cfetsBondDealDialogueQuoteDtoList == null ? [] : cfetsBondDealDialogueQuoteDtoList,
        searchOnInit: true,
        loadonce: true,
        page: false,
        addbtn: false,
        checkbox: false,
        height: 200,
        cols: colsReg
    });

    /**
     * 保存
     */
    var saveOrSubmitAction = function(operatorType) {
        var object = $.serializeToObject('#submitForm', true);
        if(!object.calculateField){
            object.calculateField = $("#CfetsBondDealQuotedPrice004_hidden_calculateField").val();
        }
        if(!object.finprodId){
            object.finprodId = $("#CfetsBondDealQuotedPrice004_hidden_finprodId").val();
        }
        object.operatorType = operatorType;
        object.isDzh = 'Y';
        object.pageId = "IbuQuoteBond002";
        object.shareAmt = JoyinMath.doMul(object.avaNum, 10000);
        if(object.cfetsBondDealDialogueQuoteDtoList){
            var cfetsBondDealRequestQuoteDtoList = object.cfetsBondDealDialogueQuoteDtoList;
            var ptlAllocations=[];
             for ( var i = 0; i <cfetsBondDealRequestQuoteDtoList.length; i++){
                 var ptlAllocation=JSON.parse(JSON.stringify(cfetsBondDealRequestQuoteDtoList[i]));
                 ptlAllocation.shareAmt = JoyinMath.doMul(ptlAllocation.avaNum, 10000);
                 ptlAllocation.avaNum = JoyinMath.doMul(ptlAllocation.entrustAvaNum, 10000);
                 ptlAllocation.avaNumOld = JoyinMath.doMul(ptlAllocation.entrustAvaNum, 10000);
                 ptlAllocations.push(ptlAllocation);
             }
            object.ptlAllocatios= ptlAllocations;
        }
        Joyin.confirm("请确认是否要提交?",
            function() {
                //保存 走流程
                if (operatorType === 'save') {
                    WorkFlow.doSaveList(basePath + 'cfets/bond/IbuQuoteBond002/save', object, "IbuQuoteBond002_btn_save", "IbuQuoteBond002",
                        function() {
                            var index = window.parent.layer.getFrameIndex(window.name);
                            window.parent.layer.close(index);
                        })
                }
                //成交 走流程
                else if (operatorType === 'done') {
                    WorkFlow.doSubmitList(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice004/save', object, "IbuQuoteBond002_btn_done", null,
                        function() {
                            var index = window.parent.layer.getFrameIndex(window.name);
                            window.parent.layer.close(index);
                        })
                }
                //提交 走流程
                else if (operatorType === 'submit') {
                    Joyin.ajaxJson(basePath + 'cfets/bond/IbuQuoteBond002/checkByHome', object, function(result1) {
                        WorkFlow.doSubmitList(basePath + 'cfets/bond/IbuQuoteBond002/save', object, "IbuQuoteBond002_btn_submit", "IbuQuoteBond002",
                            function() {
                                var index = window.parent.layer.getFrameIndex(window.name);
                                window.parent.layer.close(index);
                        })
                    })
                }
                //拒绝 不走流程
                else if (operatorType === 'refuse'){
                    Joyin.ajaxJson(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice004/saveData', object, function(result){
                        var index = window.parent.layer.getFrameIndex(window.name);
                        window.parent.layer.close(index);
                    })
                }
            })
    };
    /**
     * 计算金额相关
     */
    var calcAmt = function(calculateField,isIntAmt){
        if(entrustIdChangeFlag){
            return;
        }
        var calcField = $("[name=calculateField]:checked").val();
        var finprodId = $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val();
        if(!finprodId){
            return;
        }
        if (calculateField && calculateField != ''){
            if (calculateField != calcField){
                //$("input[name=calculateField" + "][value='"+ calcField +"']").attr("checked",true);
                //layui.form.render(); //更新;
                return;
            }
        }

        if (calcField){
            if (calcField === calcTypeUnitCprice && !$('#CfetsBondDealQuotedPrice004_unitCprice').val()){
                return;
            }
            if (calcField === calcTypeYield && !$('#CfetsBondDealQuotedPrice004_matureYield').val()){
                return;
            }
            if (calcField === calcTypeExecYield && !$('#CfetsBondDealQuotedPrice004_exerYield').val()){
                return;
            }


            var object = $.serializeToObject('#submitForm', true);
            // 区分画面打开模式
            var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();
            // 做市报价回复
            if (quotaPageModel && quotaPageModel == 'marketResponse' ){
                if (!object.responseAmt){
                    return;
                }
                object.avaNum = new BigNumber(object.responseAmt).multipliedBy(new BigNumber(10000));
            }else{
                if (!object.avaNum){
                    return;
                }
                object.avaNum = new BigNumber(object.avaNum).multipliedBy(new BigNumber(10000))
            }
             var param = {};
             param.calculateField = calcField;
             param.finprodId = finprodId;
             param.shareAmt =object.avaNum;
             param.settleDate = object.settleDate;
             param.price = object.price;
             param.fpriceAmt = object.fpriceAmt;
            if (isIntAmt && isIntAmt !== '') {
                 param.unitInt = object.unitInt;
             }
             if($("#CfetsBondDealQuotedPrice004_matureYield").val()) {
                 param.matureYield = $("#CfetsBondDealQuotedPrice004_matureYield").val() / 100;
             }
             if(calcField === calcTypeExecYield && $("#CfetsBondDealQuotedPrice004_exerYield").val()) {
                 param.exerYield = $("#CfetsBondDealQuotedPrice004_exerYield").val() / 100;
             }
             param.ps = object.ps;
             param.secManageAcctId = object.secManageAcctId;
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/bond/IbuQuoteBond/calculation', param, function(result){
                if (result.data){
                    entrustIdChangeFlag = true;
                    if (calculateField == calcTypeUnitCprice){
                        if(pageModel != PAGE_MODEL_REFERENCE){
                            $('#CfetsBondDealQuotedPrice004_matureYield').val(result.data.matureYield*100).trigger('blur');
                        }
                        $('#CfetsBondDealQuotedPrice004_exerYield').val(result.data.exerYield*100).trigger('blur');
                    }else if (calculateField == calcTypeYield){
                        $('#CfetsBondDealQuotedPrice004_unitCprice').val(result.data.price).trigger('blur');
                        $('#CfetsBondDealQuotedPrice004_exerYield').val(result.data.exerYield*100).trigger('blur');
                    }else if (calculateField == calcTypeExecYield){
                        $('#CfetsBondDealQuotedPrice004_unitCprice').val(result.data.price).trigger('blur');
                        if(pageModel != PAGE_MODEL_REFERENCE){
                            $('#CfetsBondDealQuotedPrice004_matureYield').val(result.data.matureYield*100).trigger('blur');
                        }
                    }else{
                        $('#CfetsBondDealQuotedPrice004_unitCprice').val(result.data.price).trigger('blur');
                        if(pageModel != PAGE_MODEL_REFERENCE){
                            $('#CfetsBondDealQuotedPrice004_matureYield').val(result.data.matureYield*100).trigger('blur');
                        }
                        $('#CfetsBondDealQuotedPrice004_exerYield').val(result.data.exerYield*100).trigger('blur');
                    }
                    $('#CfetsBondDealQuotedPrice004_unitInt').val(result.data.unitInt).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_unitIntEdit').val(result.data.unitInt).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_unitFprice').val(result.data.unitFprice).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_cpriceAmt').val(result.data.cpriceAmt).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_parValue').val(result.data.parValue).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_intAmt').val(result.data.intAmt).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_fpriceAmt').val(result.data.fpriceAmt).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_fpriceAmtEdit').val(result.data.fpriceAmt).trigger('blur');
                    $('#CfetsBondDealQuotedPrice004_parValueAmt').val(result.data.parValueAmt).trigger('blur');
                    $('#dealFeeAmt').val(result.data.dealFeeAmt).trigger('blur');
                    $('#settFeeAmt').val(result.data.settFeeAmt).trigger('blur');
                    entrustIdChangeFlag = false;
                }
            })
        }
    };

    /**
     * 接收子页面返回的委托信息
     */
    var saveEntrustSelectData = function(entrustInfo){
        if (entrustInfo){
            $('#CfetsBondDealQuotedPrice004_entrustId').textSelect('setValue',entrustInfo.entrustId);
        }
    }
    /**
     * 画面初始化
     */
    var initPage = function(){
        calcAmt();
        var calculateField = $("[name=calculateField" + "]:radio").next(".layui-form-radioed").prev("input").val();
        if(calculateField === calcTypeUnitCprice){
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
            layui.form.render();
        }
        if(calculateField === calcTypeYield){
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
            layui.form.render();
        }
        if(calculateField === calcTypeExecYield){
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', false);
            layui.form.render();
        }
        var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();
        if(quotaPageModel && quotaPageModel == 'dialogueResponse'){
            //$('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true);
            $('#calculateField_exerYield').attr('disabled', true);
            $('#calculateField_yield').attr('disabled', true);
        }
        if(workflowId || (pageModel == PAGE_MODEL_REFERENCE || pageModel == PAGE_MODEL_EDIT) && !(quotaPageModel && (quotaPageModel == 'dialogueResponse'))){//流程中、修改、查看
            $('#CfetsBondDealQuotedPrice004_entrustId_search').css('opacity',0.4);
        }

        /**
         * 券面总额
         */
        $('#CfetsBondDealQuotedPrice004_avaNum').on('change',function(){
           if(!entrustIdChangeFlag){
              calcAmt();
           }
        });
        /**
         * 回复量
         */
        $('#CfetsBondDealQuotedPrice004_responseAmt').on('change',function(){
            calcAmt();
        });
        /**
         * 单位净价
         */
        $('#CfetsBondDealQuotedPrice004_unitCprice').on('change',function(){
           if(!entrustIdChangeFlag){
              calcAmt("01");
           }
        });
        /**
         * 到期收益率
         */
        $('#CfetsBondDealQuotedPrice004_matureYield').on('change',function(){
           if(!entrustIdChangeFlag){
             calcAmt('02');
           }
        });
        /**
         * 行权收益率
         */
        $('#CfetsBondDealQuotedPrice004_exerYield').on('change',function(){
           if(!entrustIdChangeFlag){
             calcAmt('03');
           }
        });
        /**
         * 应计利息
         */
        $('#CfetsBondDealQuotedPrice004_unitIntEdit').on('change',function(){
            $('#CfetsBondDealQuotedPrice004_unitInt').val($('#CfetsBondDealQuotedPrice004_unitIntEdit').val()).trigger('blur');
            if(!entrustIdChangeFlag){
                calcAmt(null,'Y');
            }
        });
        /**
         * 结算金额
         */
        $('#CfetsBondDealQuotedPrice004_fpriceAmtEdit').on('change', function () {
            $('#CfetsBondDealQuotedPrice004_fpriceAmt').val($('#CfetsBondDealQuotedPrice004_fpriceAmtEdit').val()).trigger('blur');;
            // 结算金额
            var fpriceAmt = $('#CfetsBondDealQuotedPrice004_fpriceAmt').val();
            // 应计利息
            var unitInt = $('#CfetsBondDealQuotedPrice004_unitInt').val();
            // 交易金额
            var cpriceAmt = $('#CfetsBondDealQuotedPrice004_cpriceAmt').val();
            // 净价
            var unitCprice = $('#CfetsBondDealQuotedPrice004_unitCprice').val();
            //金额面板.应计利息总额=画面.结算金额-画面.交易金额
            $('#CfetsBondDealQuotedPrice004_intAmt').val(JoyinMath.doMinus(fpriceAmt, cpriceAmt, 2)).trigger('blur');;
            // 应计利息总额
            var intAmt = $('#CfetsBondDealQuotedPrice004_intAmt').val();
            //金额面板.应计利息 金额面板.应计利息总额/(券面总额（万元）*10000)*100
            $('#CfetsBondDealQuotedPrice004_unitInt').val(JoyinMath.doDiv(intAmt, cpriceAmt, 2)).trigger('blur');;
            //金额面板.全价=净价+应计利息
            $('#CfetsBondDealQuotedPrice004_unitFprice').val(JoyinMath.doAdd(unitCprice, unitInt, 2)).trigger('blur');;
        });
    }

    var  disabledElementByDzh = function(entrustList){
        //组合分配列表
        if (entrustList.length>0){
            // 回复对话报价
            if (quotaPageModel && (quotaPageModel == 'dialogueResponse')){
            }
            else {
                $('#CfetsBondDealQuotedPrice004_settleSpreed').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_mySide').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_settleDate').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true);
            }
        }
        else{
            if (!quotaPageModel || (quotaPageModel != 'dialogueResponse')) {
                $('#CfetsBondDealQuotedPrice004_settleSpreed').val('01').trigger('change').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_mySide').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_settleDate').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', false);
                $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', false);
            }
        }
    }

    /**
     * 代办中 不应该显示成交和拒绝按钮
     */
    var hideRefuseButton = function () {
        var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();
        if(workflowId){
            if (quotaPageModel && quotaPageModel == 'dialogueResponse'){
                $('#IbuQuoteBond002_btn_submit').remove();
            }else{
                $('#IbuQuoteBond002_btn_done').remove();
            }
            $("#IbuQuoteBond002_btn_refuse").hide();
        }
    }

    $(function () {
        // 初始化页面
        initPage();
        if ('Y' === isSpecialDebt) {
            $('#unitIntEditDiv').show();
            $('#fpriceAmtEditDiv').hide();
        }else{
            $('#unitIntEditDiv').hide();
            $('#fpriceAmtEditDiv').show();
        }

        // 委托代码
        var entrustId = $('#CfetsBondDealQuotedPrice004_hidden_entrustId').val();
        if (entrustId && entrustId != ''){
            $('#CfetsBondDealQuotedPrice004_entrustId').textSelect('setValue',entrustId);
            $('#CfetsBondDealQuotedPrice004_entrustId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleSpreed').attr('disabled', true);
            //add by lijie2
            if(PRICE_MODEL && PRICE_MODEL == PRICE_MODEL_PRM01){
                $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
                $("input[name='calculateField']").attr('disabled', true);
                $("input[name=calculateField][value=01]").prop("checked","true");
                layui.form.render();
            }else if(assetTypeOne == assetTypeOne_abs){
                $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
                $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
                $("input[name='calculateField']").attr('disabled', true);
                $("#CfetsBondDealQuotedPrice004_hidden_calculateField").val("01");
                $("input[name=calculateField][value=01]").prop("checked","true");
                layui.form.render();
            }
            editFlag = false;
        }
        // 画面操作模式
        var quotaPageModel = $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val();
        // 做市报价回复
        if (quotaPageModel && quotaPageModel == 'marketResponse'){
            $('#CfetsBondDealQuotedPrice004_div_responseAmt').toggle(true);
            $('#CfetsBondDealQuotedPrice004_div_settleCcy').toggle(false);
            if (ibuFlag_startDialogueQuoteWithNoEntrust != '02'){
                $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true);
            }
            $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_mySide').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_counterId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_counterTrader').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_finprodMarketId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleSpreed').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleDate').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_avaNum').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleMethod').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleType').attr('disabled', true);
            $('#IbuQuoteBond002_btn_done').toggle(false);
            $('#IbuQuoteBond002_btn_refuse').toggle(false);
            $('#IbuQuoteBond002_btn_save').toggle(false);
            $('#IbuQuoteBond002_btn_submit').toggle(true);
            $('#IbuQuoteBond002_btn_cancel').toggle(true);
        }
        // 对话报价回复(除委托代码外置灰)
        else if (quotaPageModel && quotaPageModel == 'dialogueResponse'){
            $('#CfetsBondDealQuotedPrice004_div_settleCcy').toggle(true);
            $('#IbuQuoteBond002_btn_done').toggle(true);
            $('#IbuQuoteBond002_btn_refuse').toggle(true);
            $('#IbuQuoteBond002_btn_save').toggle(false);
            $('#IbuQuoteBond002_btn_submit').toggle(false);
            $('#IbuQuoteBond002_btn_cancel').toggle(false);
            if (ibuFlag_startDialogueQuoteWithNoEntrust != '02'){
                $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true);
            }
            $('#CfetsBondDealQuotedPrice004_mySideTrader').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_mySide').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_counterId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_counterTrader').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleSpreed').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_avaNum').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleMethod').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleType').attr('disabled', true);
            //无指令时，组合可改
            if (ibuFlag_startDialogueQuoteWithNoEntrust == '02'){
                var mySideTemp = $('#CfetsBondDealQuotedPrice004_mySide').val();
                if(mySideTemp){
                    $('#CfetsBondDealQuotedPrice004_responseMySide').val(mySideTemp);
                }
            }
        }
        // 对话报价发起和保存后双击
        else if (quotaPageModel && quotaPageModel == 'new'){
            $('#CfetsBondDealQuotedPrice004_div_responseAmt').toggle(false);
            $('#CfetsBondDealQuotedPrice004_div_settleCcy').toggle(true);
            if (mySideTrader && mySideTrader != ''){
                $('#CfetsBondDealQuotedPrice004_mySideTrader').textSelect('setValue',mySideTrader);
            }
            if (counterTrader && counterTrader != ''){
                $('#CfetsBondDealQuotedPrice004_counterTrader').textSelect('setValue',counterTrader);
            }
            if (ouTraderIdCanEditFlag === 'N'){
                $('#CfetsBondDealQuotedPrice004_mySideTrader').attr('disabled', true);
            }
            $('#IbuQuoteBond002_btn_done').toggle(false);
            $('#IbuQuoteBond002_btn_refuse').toggle(false);
            $('#IbuQuoteBond002_btn_save').toggle(true);
            $('#IbuQuoteBond002_btn_submit').toggle(true);
            $('#IbuQuoteBond002_btn_cancel').toggle(true);
        }// 对话报价发起和保存后双击
        else if (quotaPageModel && quotaPageModel == 'edit'){
            $('#CfetsBondDealQuotedPrice004_div_settleCcy').toggle(true);
            $('#CfetsBondDealQuotedPrice004_mySide').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_mySideTrader').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_counterId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_counterTrader').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_settleType').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice004_remark').attr('disabled', true);
            //修改无委托初始化增加不可修改项 add by yzg 20210123 start
            $('#CfetsBondDealQuotedPrice004_entrustId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_portfolioName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_ps').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_finprodName').attr('disabled', true);
            //修改无委托初始化增加不可修改项 add by yzg 20210123 end
            $('#IbuQuoteBond002_btn_done').toggle(false);
            $('#IbuQuoteBond002_btn_refuse').toggle(false);
            $('#IbuQuoteBond002_btn_save').toggle(true);
            $('#IbuQuoteBond002_btn_submit').toggle(true);
            $('#IbuQuoteBond002_btn_cancel').toggle(true);

        }
        // 含权、资产一级类型
        var isRight = $('#CfetsBondDealQuotedPrice004_hidden_isRight').val();
        if (isRight == yesNoFlag_yes){
            $('#CfetsBondDealQuotedPrice004_div_exerYield').toggle(true);
        }
        else{
            $('#CfetsBondDealQuotedPrice004_div_exerYield').toggle(false);
        }
        var assetType1 = $('#CfetsBondDealQuotedPrice004_hidden_assetTypeOne').val();
        if (assetType1 == assetTypeOne_abs){
            $('#CfetsBondDealQuotedPrice004_div_abs1').toggle(true);
            $('#CfetsBondDealQuotedPrice004_div_abs2').toggle(true);
        }else{
            $('#CfetsBondDealQuotedPrice004_div_abs1').toggle(false);
            $('#CfetsBondDealQuotedPrice004_div_abs2').toggle(false);
        }

        /**
         * 委托代码
         */
        $('#CfetsBondDealQuotedPrice004_entrustId').change(function(){
            entrustIdChangeAction();
        });
        /**
         * 委托代码放大镜
         */
        $('#CfetsBondDealQuotedPrice004_entrustId_search').click(function(){
            if(workflowId || (pageModel == PAGE_MODEL_REFERENCE || pageModel == PAGE_MODEL_EDIT) && !(quotaPageModel && (quotaPageModel == 'dialogueResponse'))){//流程中、修改、查看
                return;
            }
            Joyin.dialog(JoyinUtil.getLabel("CfetsBondDealQuotedPrice006"),
                basePath + "invest/bankuplink/bond/CfetsBondDealQuotedPrice006/init", "99%", "80%", {
                    pageModel : PAGE_MODEL_NEW, finprodId : $('#CfetsBondDealQuotedPrice004_finprodMarketId').val()
                    , ps : $('#CfetsBondDealQuotedPrice004_ps').val(), entrustId : $('#CfetsBondDealQuotedPrice004_entrustId').val(), editFlag : editFlag
                    , mySide : $('#CfetsBondDealQuotedPrice004_mySide').val(), quotaPageModel : $('#CfetsBondDealQuotedPrice004_hidden_quotaPageModel').val()
                    , settleSpreed : $('#CfetsBondDealQuotedPrice004_settleSpreed').val()
                })
        })
        /**
         * 资产简称
         */
        $('#CfetsBondDealQuotedPrice004_finprodName').change(function(){
            finprodNameChangeAction();
        });
        /**
         * 组合名称
         */
        $('#CfetsBondDealQuotedPrice004_portfolioName').change(function(){
            portfolioNameChangeAction();
        });
        /**
         * 清算速度
         */
        $('#CfetsBondDealQuotedPrice004_settleSpreed').change(function(){
            settleSpreedChangeAction();
        });
        /**
         * 券面总额
         */
 /*       $('#CfetsBondDealQuotedPrice004_avaNum').blur(function(){
            calcAmt();
        });*/
        /**
         * 券面总额
         */
    /*    $("#CfetsBondDealQuotedPrice004_avaNum").change(function() {
            calcAmt();
        });*/
        /**
         * 回复量
         */
        $('#CfetsBondDealQuotedPrice004_responseAmt').blur(function(){
            calcAmt();
        });
        /**
         * 单位净价
         */
  /*      $('#CfetsBondDealQuotedPrice004_unitCprice').blur(function(){
            calcAmt('01');
        });*/
        /**
         * 到期收益率
         */
  /*      $('#CfetsBondDealQuotedPrice004_matureYield').blur(function(){
            calcAmt('02');
        });*/
        /**
         * 行权收益率
         */
   /*     $('#CfetsBondDealQuotedPrice004_exerYield').blur(function(){
            calcAmt('03');
        });*/

        layui.use(['form'], function() {
            // 绑定计算模式的点击事件
            var form = layui.form;
            form.on('radio(calculateField)', function(data) {
                layui.form.render(); //更新
                calculateFieldChange();
            });
        });

        /**
         * 保存按钮按下处理
         */
        $('#IbuQuoteBond002_btn_save').click(function(){
            saveOrSubmitAction('save');
        });

        /**
         * 提交按钮按下处理
         */
        $('#IbuQuoteBond002_btn_submit').click(function(){
             if(paramCheck()) {
                saveOrSubmitAction('submit');
             }
        });

        /**
         * 成交
         */
        $('#IbuQuoteBond002_btn_done').click(function(){
            saveOrSubmitAction('done');
        });

        /**
         * 拒绝
         */
        $('#IbuQuoteBond002_btn_refuse').click(function(){
            saveOrSubmitAction('refuse');
        });
        /**
         * 本方
         */
        $('#CfetsBondDealQuotedPrice004_mySide').change(function(){
            mySideChangeAction();
        });
        /**
         * 对手方方
         */
        $('#CfetsBondDealQuotedPrice004_counterId').change(function(){
            counterIdChangeAction();
        });
        //拒绝按钮不应该在待办及审批环节出现
        hideRefuseButton();
        $("#btn_autoAllocation").click(function () {
            autoAllocation();
        })
    });
    var calculateFieldChange = function(init) {
        var calculateField = $("[name=calculateField" + "]:radio").next(".layui-form-radioed").prev("input").val();
        if(calculateField === calcTypeUnitCprice){
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
            layui.form.render();
        }
        if(calculateField === calcTypeYield){
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', true);
            layui.form.render();
        }
        if(calculateField === calcTypeExecYield){
            $('#CfetsBondDealQuotedPrice004_unitCprice').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_matureYield').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice004_exerYield').attr('disabled', false);
            layui.form.render();
        }
        calcAmt(calculateField);
    }
    /**
     * 参数校验
     */
    var paramCheck = function () {
        if(!$('#CfetsBondDealQuotedPrice004_avaNum').val()||!$('#CfetsBondDealQuotedPrice004_mySideTrader').val()
             ||!$('#CfetsBondDealQuotedPrice004_counterId').val() ||!$('#CfetsBondDealQuotedPrice004_counterTrader').val()
              ||!$('#CfetsBondDealQuotedPrice004_unitCprice').val() ||!$('#CfetsBondDealQuotedPrice004_matureYield').val()
        ){
            Joyin.error("有*必填项未填写,请填写完整")
            return false;
        }
        return true;
    }

    /**
     * 计算金额相关
     */
    var autoAllocation = function () {
        var finprodId = $('#CfetsBondDealQuotedPrice004_hidden_finprodId').val();
        if (!$("#distribution").val()) {
            Joyin.error("无法自动分配，请先选择分配方式");
            return;
        }
        var object = $.serializeToObject('#submitForm', true);
        var calcField = $("[name=calculateField]:checked").val();
        var param = {};
        var shareAmt = $('#CfetsBondDealQuotedPrice004_avaNum').val();
        param.calculateField = calcField;
        param.finprodId = finprodId;
        param.shareAmt = shareAmt * 10000;
        param.settleDate = object.settleDate;
        param.ps = object.ps;
        param.secManageAcctId = object.secManageAcctId;
        param.dealFeeAmt = object.dealFeeAmt;
        param.distribution = object.distribution;
        param.settFeeAmt = object.settFeeAmt;
        param.fpriceAmt = object.fpriceAmt;
        param.price = object.price;
        if($("#CfetsBondDealQuotedPrice004_matureYield").val()) {
          param.matureYield = $("#CfetsBondDealQuotedPrice004_matureYield").val() / 100;
        }
        if($("#CfetsBondDealQuotedPrice004_exerYield").val()) {
            param.exerYield = $("#CfetsBondDealQuotedPrice004_exerYield").val() / 100;
        }
        param.cpriceAmt = object.cpriceAmt;
        param.intAmt = object.intAmt;
        param.unitFprice = object.unitFprice;
        if (object.cfetsBondDealDialogueQuoteDtoList) {
            $.each(object.cfetsBondDealDialogueQuoteDtoList, function (index, obj) {
                if(obj.avaNum){
                    obj.shareAmt = JoyinMath.doMul(obj.avaNum, 10000);
                }else{
                    obj.shareAmt = 0;
                }
                if(obj.entrustAvaNum){
                    obj.avaNum = JoyinMath.doMul(obj.entrustAvaNum, 10000);
                    obj.avaNumOld = JoyinMath.doMul(obj.entrustAvaNum, 10000);
                }else{
                   obj.avaNum = 0;
                   obj.avaNumOld = 0;
                }
                obj.price=param.price;
                obj.matureYield=param.matureYield*100;
                obj.exerYield=param.exerYield*100;
            });
        }
        param.ptlAllocations = object.cfetsBondDealDialogueQuoteDtoList;
        Joyin.ajaxJson(basePath + 'cfets/bankuplink/bond/IbuQuoteBond/autoAllocation', param, function (result) {
            if (result.data) {
                 $.each(result.data, function (index, item) {
                     item.entrustAvaNum = JoyinMath.doDiv(item.avaNum, 10000);
                     item.avaNumOld = JoyinMath.doDiv(item.avaNumOld, 10000);
                     item.avaNum=JoyinMath.doDiv(item.shareAmt,10000)
                 })
                 tableTedCommon.clear();
                 tableTedCommon.addRow(result.data);
            }
        })
    };
    /**
     * 本方
     */
    var mySideChangeAction = function(){
        /*$('#CfetsBondDealQuotedPrice004_mySideTrader').val('').trigger('change');
        // add by zhangliang 20200630 start
        var mySideTemp = $('#CfetsBondDealQuotedPrice004_mySide').val();
        if (!mySideInit || mySideTemp == mySideInit){
        	$('#CfetsBondDealQuotedPrice004_mySideTrader').val(mySideTraderInit).trigger('change');
        }*/
        $('#CfetsBondDealQuotedPrice004_mySideTrader').val('').trigger('change');
    }
    /**
     * 对手方
     */
    var counterIdChangeAction = function(){
        var data =  $('#CfetsBondDealQuotedPrice004_counterId').select2('data')[0];
        if(data) {
            $('#counterName').val(data.text).trigger('change').trigger('blur');
        }
        $('#CfetsBondDealQuotedPrice004_counterTrader').val('').trigger('change');
    }
    window.CfetsBondDealQuotedPrice004 = {
        saveEntrustSelectData: saveEntrustSelectData,
        disabledElementByDzh:disabledElementByDzh
    };
}());