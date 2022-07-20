(function(){


    /**
     * 委托代码
     */
    var entrustIdChangeAction = function(){
        var entrustId = $('#CfetsBondDealQuotedPrice005_entrustId').val();
        if (entrustId && entrustId != ''){
            Joyin.ajax(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice003/queryEntrustInfoByEntrustId', {entrustId : entrustId}, function(result) {
                if (result){
                    $('#CfetsBondDealQuotedPrice005_hidden_entrustAmt').val(result.entrustNum);
                    $('#CfetsBondDealQuotedPrice005_hidden_finprodId').val(result.finprodId);
                    $('#CfetsBondDealQuotedPrice005_portfolioName').attr('disabled', true).textSelect('setValue',result.portfolioId);
                    $('#CfetsBondDealQuotedPrice005_mySide').attr('disabled', true).textSelect('setValue',result.mySide);
                    $('#CfetsBondDealQuotedPrice005_mySideTrader').textSelect('setValue',result.mySideTrader);
                    $('#CfetsBondDealQuotedPrice005_ps').val(result.ps).trigger('change').attr('disabled', true);
                    $('#CfetsBondDealQuotedPrice005_finprodName').attr('disabled', true).textSelect('setValue',result.finprodMarketId);
                    $('#CfetsBondDealQuotedPrice005_settleCcy').val(result.settleCcy == null ? CNY : result.settleCcy);
                    $('#CfetsBondDealQuotedPrice005_settleSpreed').val(result.settleSpreed).trigger('change').attr('disabled', true);
                    $('#CfetsBondDealQuotedPrice005_settleDate').val(result.settleDateStr).attr('disabled', true);
                    $('#CfetsBondDealQuotedPrice005_avaNum').val(result.avaNumStr).trigger('change');
                    $('#CfetsBondDealQuotedPrice005_effDate').val(result.effDate);
                    $('#CfetsBondDealQuotedPrice005_remark').val(result.remark);
                }
            });
        }
    };
    /**
     * 债券简称
     */
    var finprodNameChangeAction = function(){
        var data = $('#CfetsBondDealQuotedPrice005_finprodName').select2('data')[0].object;
        if (data){
            $('#CfetsBondDealQuotedPrice005_finprodMarketId').val(data.finprodMarketId);
            $("#CfetsBondDealQuotedPrice005_hidden_finprodId").val(data.finprodId);
            $('#CfetsBondDealQuotedPrice005_settleCcy').val(data.ccy).trigger("change");
        }
    };
    /**
     * 组合名称
     */
    var portfolioNameChangeAction = function(){
        var portfolioId = $('#CfetsBondDealQuotedPrice005_portfolioName').val();
        $('#CfetsBondDealQuotedPrice005_portfolioId').val(portfolioId);
    };
    /**
     * 清算速度
     */
    var settleSpreedChangeAction = function(){
        var settleSpreed = $('#CfetsBondDealQuotedPrice005_settleSpreed').val();
        if (settleSpreed == ACCOUNT_SPEED_1){
            Joyin.ajax(basePath + 'invest/bankuplink/common/CfetsQuotedCommon/getNextWorkDay', {
                'tradeDate': platDate
            }, function (date) {
                $("#CfetsBondDealQuotedPrice005_settleDate").val(date).trigger("change");
            });
        }else{
            $('#CfetsBondDealQuotedPrice005_settleDate').val(platDate);
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
            data: cfetsBondDealRequestQuoteDtoList == null ? [] : cfetsBondDealRequestQuoteDtoList,
            searchOnInit: true,
            loadonce: true,
            page: false,
            addbtn: false,
            checkbox: false,
            height: 200,
            cols: colsReg
    });



    /**
     * 保存或提交
     */
    var saveOrSubmitAction = function(operatorType){
        var object = $.serializeToObject('#submitForm', true);
        var orgList = table.getAllRows();
        if(orgList != null && orgList.length>0){
           // var orgInfoList = [];
            var orgIdList = [];
            for(var i = 0 ;i<orgList.length; i++){
                var org = orgList[i];
               // orgInfoList.push(org);
                orgIdList.push(org.counterId);
            }
          //  object.counterInfoList = orgInfoList;
            object.counterIdList = orgIdList;
        }
        object.isDzh = 'Y';
        object.operatorType = operatorType;
        object.pageId = "IbuQuoteBond003";
        object.shareAmt = JoyinMath.doMul(object.avaNum, 10000);
        if(object.cfetsBondDealDialogueQuoteDtoList){
            object.cfetsBondDealRequestQuoteDtoList = object.cfetsBondDealDialogueQuoteDtoList;
             var ptlAllocations=[];
             for ( var i = 0; i <object.cfetsBondDealRequestQuoteDtoList.length; i++){
                  var ptlAllocation=JSON.parse(JSON.stringify(object.cfetsBondDealRequestQuoteDtoList[i]));
                  ptlAllocation.shareAmt = JoyinMath.doMul(ptlAllocation.avaNum, 10000);
                  ptlAllocation.avaNum = JoyinMath.doMul(ptlAllocation.entrustAvaNum, 10000);
                  ptlAllocation.avaNumOld = JoyinMath.doMul(ptlAllocation.entrustAvaNum, 10000);
                  ptlAllocations.push(ptlAllocation);
             }
             object.ptlAllocatios= ptlAllocations;
        }
          Joyin.confirm("是否确认提交?",
            function () {
                //保存 走流程
                if (operatorType === 'save') {
                    WorkFlow.doSaveList(basePath + 'cfets/bond/IbuQuoteBond003/save', object, "IbuQuoteBond003_btn_save", "IbuQuoteBond003",
                        function () {
                            var index = window.parent.layer.getFrameIndex(window.name);
                            window.parent.layer.close(index);
                        })
                }
                //提交 走流程
                else if (operatorType === 'submit') {
                     Joyin.ajaxJson(basePath + 'cfets/bond/IbuQuoteBond003/checkByHome', object, function(result1) {
                      WorkFlow.doSubmitList(basePath + 'cfets/bond/IbuQuoteBond003/save', object, "IbuQuoteBond003_btn_submit", "IbuQuoteBond003",
                         function () {
                             var index = window.parent.layer.getFrameIndex(window.name);
                             window.parent.layer.close(index);
                         })
                     })
                }
        })
    };
    /**
     * 撤回
     */
    var revokeAction = function(){
        var object = $.serializeToObject('#submitForm', true);
        if(!object.finprodId){
            object.finprodId = $("#CfetsBondDealQuotedPrice005_hidden_finprodId").val();
        }
        object.operationType = 'recall';
        object.pageId = "CfetsBondDealQuotedPrice005";
        Joyin.ajaxJson(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice001/checkOperationAllow', object, function(result) {
            if (result.data){
                var param = result.data;
                Joyin.confirm(param, function() {
                    Joyin.ajaxJson(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice001/doReCall', object, function(result){
                        var index = window.parent.layer.getFrameIndex(window.name);
                        window.parent.layer.close(index);
                    })
                })
            }
        });
    };
    /**
     * 成交
     */
    var clinchDealAction = function(){
        var object = $.serializeToObject('#submitForm', true);
        if(!object.finprodId){
            object.finprodId = $("#CfetsBondDealQuotedPrice005_hidden_finprodId").val();
        }
        var orgList = table.getAllCheckedRows();
        if(orgList != null && orgList.length > 0){
            var orgInfoList = [];
            var orgIdList = [];
            var detIdList = [];
            for(var i = 0 ;i<orgList.length; i++){
                var org = orgList[i];
                // 勾选对象的交易状态不是已回复时
                if (org.tradeStatusStr != tradeStatus12) {
                    Joyin.error(JoyinUtil.getMessage("CFETS_COMMON_R_028"));
                    return;
                }
                orgInfoList.push(org);
                orgIdList.push(org.counterId);
                detIdList.push(org.detId);
            }
            object.counterInfoList = orgInfoList;
            object.counterIdList = orgIdList;
            object.detIdList = detIdList;
        }
        // 列表中勾选对象1条以上时
        if (orgList && orgList.length > 1) {
            Joyin.error(JoyinUtil.getMessage("COMMON_E_0058"));
            return;
        }
        //请求报价回复页面成交按钮
        object.checkType = "answerClinchDeal";
        object.operatorType = "done";
        object.isDzh = 'Y';
        object.pageId = "CfetsBondDealQuotedPrice005";
        if(object.cfetsBondDealDialogueQuoteDtoList){
            object.cfetsBondDealRequestQuoteDtoList = object.cfetsBondDealDialogueQuoteDtoList;
        }
        Joyin.ajaxJson(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice005/clinchDealCheck', object, function(result) {
            if (result.data){
                var param = result.data;
                WorkFlow.doSubmitList(basePath + 'invest/bankuplink/bond/CfetsBondDealQuotedPrice005/save', object, "CfetsBondDealQuotedPrice005_btn_done", null,
                    function() {
                        var index = window.parent.layer.getFrameIndex(window.name);
                        window.parent.layer.close(index);
                    })
            }
        });
    };
    /**
     * 定义数据表格
     */
    var table=$("#CfetsBondDealQuotedPrice005_orgTable").joyintable({
        checkbox:true,
        data:(entity && entity.counterInfoList) ? entity.counterInfoList : [],
        searchOnInit:true,
        page:false,
        height:300,
        cols:[
            {field:'counterValue', title: 'CfetsBondDealQuotedPrice003.counterId'},
            {field:'cpTrader', title: 'CfetsBondDealQuotedPrice003.cpTrader', visible: YES_OR_NO_Y == isRevert},
            {field:'tradeStatusStr', title: 'CfetsBondDealQuotedPrice003.tradeStatus', visible: YES_OR_NO_Y == isRevert,sortField:"tradeStatus"},
            {field:'shareAmt', title: 'CfetsRepoDealQuotedPrice003.shareAmt', type:'number',digit:2, visible: YES_OR_NO_Y == isRevert},
            {field:'unitCprice', title: 'CfetsRepoDealQuotedPrice003.unitCprice', type:'number',digit:4, visible: YES_OR_NO_Y == isRevert},
            {field:'unitFprice', title: 'CfetsRepoDealQuotedPrice003.unitFprice', type:'number',digit:4, visible: YES_OR_NO_Y == isRevert},
            {field:'yield', title: 'CfetsRepoDealQuotedPrice003.yield', type:'number',digit:4, visible: YES_OR_NO_Y == isRevert},
            {field:'exerYield', title: 'CfetsRepoDealQuotedPrice003.exerYield',type:'number',digit:4, visible: (YES_OR_NO_Y == isRevert && YES_OR_NO_Y == assetIsRight)},
            {field:'counterId', visible: false},
            {field:'detId', visible: false},
            {field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt'], visible: YES_OR_NO_Y != isRevert}
        ]
    });
    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        table.removeRow(data);
    });

    /**
     * 添加可选机构事件
     */
    var addRow = function(){
        var orgId = $('#CfetsBondDealQuotedPrice005_orgType').val();
        if(orgId && orgId != ''){
            var orgList = table.getAllRows();
            if(orgList != null && orgList.length>0){
                for(var i = 0 ;i<orgList.length; i++){
                    var org = orgList[i];
                    if(org.counterId == orgId){
                        Joyin.error("可选机构中不允许存在多条相同对手方信息!");
                        return;
                    }
                }
            }
            var orgValue = $('#CfetsBondDealQuotedPrice005_orgType :selected').text();
            var temp = {};
            temp.counterId=orgId;
            temp.counterValue=orgValue;
            table.addRow(temp);
        }
    };

    var  disabledElementByDzh = function(entrustList){
        // 回复对话报价
        if (quotaPageModel == 'response'){
            return;
        }
        //组合分配列表
        if (entrustList.length>0){
            $('#CfetsBondDealQuotedPrice005_entrustId').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice005_portfolioName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice005_ps').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice005_finprodName').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice005_settleSpreed').attr('disabled', true);
            $('#CfetsBondDealQuotedPrice005_mySide').attr('disabled', true);
        }
        else{
            $('#CfetsBondDealQuotedPrice005_portfolioName').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice005_ps').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice005_finprodName').attr('disabled', false);
            $('#CfetsBondDealQuotedPrice005_settleSpreed').attr('disabled', false);
        }
    }


    $(function () {
        // 初始化页面
        /*var entrustId = $('#CfetsBondDealQuotedPrice005_hidden_entrustId').val();
        if (entrustId && entrustId != ''){*/
        $('#CfetsBondDealQuotedPrice005_entrustId').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_portfolioName').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_ps').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_finprodName').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_settleSpreed').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_mySide').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_settleMethod').attr('disabled', true);
        $('#CfetsBondDealQuotedPrice005_settleType').attr('disabled', true);
        /*}else{
            //未选择委托时,默认：T+0
            if ($("#CfetsBondDealQuotedPrice005_settleSpreed").val() == ''){
                $("#CfetsBondDealQuotedPrice005_settleSpreed").val(ACCOUNT_SPEED_0).trigger("change");
            }
            if ($("#CfetsBondDealQuotedPrice005_settleDate").val() == ''){
                $('#CfetsBondDealQuotedPrice005_settleDate').val(platDate);
            }
            //默认人民币
            if ($("#CfetsBondDealQuotedPrice005_settleCcy").val() == ''){
                $('#CfetsBondDealQuotedPrice005_settleCcy').val(CNY).trigger("change");
            }
            //默认券款对付
            if ($("#CfetsBondDealQuotedPrice005_settleMethod").val() == ''){
                $('#CfetsBondDealQuotedPrice005_settleMethod').val(SETT_MODE_03).trigger("change");
            }
            //默认全额清算
            if ($("#CfetsBondDealQuotedPrice005_settleType").val() == ''){
                $('#CfetsBondDealQuotedPrice005_settleType').val(CLEAR_TYPE_02).trigger("change");
            }
            // 默认登录用户
            if ($("#CfetsBondDealQuotedPrice005_mySideTrader").val() == ''){
                $('#CfetsBondDealQuotedPrice005_mySideTrader').val(mySideTraderInit).trigger("change");
            }
        }*/
        // 判断本方交易员是否可改
        if (ouTraderIdCanEditFlag === 'N'){
            $('#CfetsBondDealQuotedPrice005_mySideTrader').attr('disabled', true);
        }

        // 初始化对手方列表
        /*if(entity && entity.counterInfoList){
            table.table ? table.reload() : table.load();
            var counterInfoList = entity.counterInfoList;
            var tempList = [];
            for(var i=0; i< counterInfoList.length; i++){
                var temp = {};
                temp.counterId=counterInfoList[i].counterId;
                temp.counterValue=counterInfoList[i].counterValue;
                temp.cpTrader=counterInfoList[i].cpTrader;
                temp.tradeStatusStr=counterInfoList[i].tradeStatusStr;
                temp.shareAmt=counterInfoList[i].shareAmt;
                temp.unitCprice=counterInfoList[i].unitCprice;
                temp.unitFprice=counterInfoList[i].unitFprice;
                temp.yield=counterInfoList[i].yield;
                temp.exerYield=counterInfoList[i].exerYield;
                temp.detId=counterInfoList[i].detId;
                tempList.push(temp);
            }
            table.addRow(tempList);
        }*/
        /**
         * 委托代码
         */
        $('#CfetsBondDealQuotedPrice005_entrustId').change(function(){
            entrustIdChangeAction();
        });
        /**
         * 资产简称
         */
        $('#CfetsBondDealQuotedPrice005_finprodName').change(function(){
            finprodNameChangeAction();
        });
        /**
         * 组合名称
         */
        $('#CfetsBondDealQuotedPrice005_portfolioName').change(function(){
            portfolioNameChangeAction();
        });
        /**
         * 清算速度
         */
        $('#CfetsBondDealQuotedPrice005_settleSpreed').change(function(){
            settleSpreedChangeAction();
        });
        /**
         * 保存按钮按下处理
         */
        $('#IbuQuoteBond003_btn_save').click(function(){
             saveOrSubmitAction('save');
        });
        /**
         * 提交按钮按下处理
         */
        $('#IbuQuoteBond003_btn_submit').click(function(){
             if(paramCheck()) {
                 saveOrSubmitAction('submit');
             }
        });
        /**
         * 添加按钮按下处理
         */
        $('#IbuQuoteBond003_btn_add').click(function(){
            addRow();
        });
        /**
         * 撤回按钮按下处理
         */
        $('#IbuQuoteBond003_btn_revokeBtn').click(function(){
            revokeAction();
        });
        /**
         * 成交按钮按下处理
         */
        $('#IbuQuoteBond003_btn_clinchDeal').click(function(){
            clinchDealAction();
        });
        /**
         * 本方
         */
        $('#CfetsBondDealQuotedPrice005_mySide').change(function(){
            mySideChangeAction();
        });
        $("#btn_autoAllocation").click(function () {
            autoAllocation();
        })
        $('#CfetsBondDealQuotedPrice005_avaNum').on('change',function(){
           calcAmt();
        });
    });
    /**
     * 参数校验
     */
    var paramCheck = function () {
        if(!$('#CfetsBondDealQuotedPrice005_avaNum').val()||!$('#CfetsBondDealQuotedPrice005_mySideTrader').val()){
            Joyin.error("有*必填项未填写,请填写完整")
            return false;
        }
        return true;
    }
    /**
     * 计算金额相关
     */
    var calcAmt = function(calculateField){
        var finprodId = $('#CfetsBondDealQuotedPrice005_hidden_finprodId').val();
        if(!finprodId){
            return;
        }
        var object = $.serializeToObject('#submitForm', true);
        // 区分画面打开模式
        var quotaPageModel = $('#CfetsBondDealQuotedPrice005_hidden_quotaPageModel').val();
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
         param.finprodId = finprodId;
         param.shareAmt =object.avaNum;
         param.settleDate = object.settleDate;
         param.price = object.price;
         param.fpriceAmt = object.fpriceAmt;
         param.ps = object.ps;
         param.secManageAcctId = object.secManageAcctId;
        Joyin.ajaxJson(basePath + 'cfets/bankuplink/bond/IbuQuoteBond/calculation', param, function(result){
            if (result.data){
                $('#dealFeeAmt').val(result.data.dealFeeAmt).trigger('blur');
                $('#settFeeAmt').val(result.data.settFeeAmt).trigger('blur');
            }
        })
    };
    /**
     * 计算金额相关
     */
    var autoAllocation = function () {
        var finprodId = $('#CfetsBondDealQuotedPrice005_hidden_finprodId').val();
        if (!$("#distribution").val()) {
            Joyin.error("无法自动分配，请先选择分配方式");
            return;
        }
        var object = $.serializeToObject('#submitForm', true);
        var param = {};
        var shareAmt = $('#CfetsBondDealQuotedPrice005_avaNum').val();
        param.finprodId = finprodId;
        param.shareAmt = shareAmt * 10000;
        param.settleDate = object.settleDate;
        param.ps = object.ps;
        param.secManageAcctId = object.secManageAcctId;
        param.dealFeeAmt = object.dealFeeAmt;
        param.distribution = object.distribution;
        param.settFeeAmt = object.settFeeAmt;
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
        /*$('#CfetsBondDealQuotedPrice003_mySideTrader').val('').trigger('change');
        // add by zhangliang 20200630 start
        var mySideTemp = $('#CfetsBondDealQuotedPrice003_mySide').val();
        if (mySideTemp == mySideInit){
        	$('#CfetsBondDealQuotedPrice003_mySideTrader').val(mySideTraderInit).trigger('change');
        }*/
        $('#CfetsBondDealQuotedPrice005_mySideTrader').val(mySideTraderInit).trigger('change');
    }
    //撤回按钮不应该在待办及审批环节出现
    if(busWorkflowId){
        $("#IbuQuoteBond003_btn_revokeBtn").hide();
    }

    window.CfetsBondDealQuotedPrice005 = {
        disabledElementByDzh:disabledElementByDzh
    };
}());