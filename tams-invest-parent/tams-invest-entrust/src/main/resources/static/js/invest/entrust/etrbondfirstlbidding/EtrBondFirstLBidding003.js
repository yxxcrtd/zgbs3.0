/**
 * 债券一级投标多对多委托新建
 */
(function () {
    var bidPrinAmt = 0;
    var getriskdata = function () {
        var etrBondFirstLBiddingDtoList = $.serializeToObject('#searchForm').etrBondFirstLBiddingDtoList;
        if (etrBondFirstLBiddingDtoList.length > 0) {
            var objects = new Array();
            for ( var index in etrBondFirstLBiddingDtoList) {
                var item = etrBondFirstLBiddingDtoList[index];
                var trdProductDeal = {};
                trdProductDeal.shareAmt  = item.entrustNum;
                var obj = {
                    tradeDirection:etrPs,
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: sysDate,
                    portfolioCode: item.portfolioId,
                    tradeAmount:item.bidPrinAmt,
                    assetType: fintype,
                    assetSecType: item.assetType1,
                    isEnabled: '01',
                    assetCode:item.finprodId,
                    tradeAmt: item.tradeAmt,
                    counterId:item.counterId,
                    fullDataFlag : '1',
                    trdProductDeal : trdProductDeal
                }
                var object = {
                    tradeDirection: ['ps', 'EtrBondFirstLBidding002.ps'],
                    portfolioCode: ['portfolioId', 'EtrBondFirstLBidding002.portfolioId'],
                    tradeAmount: ['entrustNum', 'EtrBondFirstLBidding002.entrustAmt'],
                    assetType: ['assetType1', 'EtrBondFirstLBidding002.bondType'],
                    counterId: ['counterId', 'trdInvestPersonInfo_counterId'],
                    assetCode: ['finprodId', 'EtrBondFirstLBidding002.finprodId'],
                    data: obj
                }
                objects.push(object);
            }
            return objects;
        }
    }

    //初始化
    /**
     * 定义数据表格
     */
    var cols = [
        {field: 'indexId', title: 'EtrBondFirstLBidding003.indexId', sortable : false,width: 100}
        , {field: 'vdate', title: 'EtrBondFirstLBidding002.vdate',type : 'date',sortable : false, width: 120}
        , {field: 'mdate', title: 'EtrBondFirstLBidding002.mdate',type : 'date',sortable : false, width: 120}
        , {field: 'portfolioId', title: 'EtrBondFirstLBidding002.portfolioId', sortable : false,width: 120}
        , {field: 'finprodId', title: 'EtrBondFirstLBidding002.finprodId', sortable : false,width: 150, visible: false}
        , {field: 'finprodMarketId', title: 'EtrBondFirstLBidding002.finprodId',sortable : false, width: 150}
        , {field: 'tradeMarket', title: 'EtrBondFirstLBidding002.tradeMarket', sortable : false,width: 120, visible: false}
        , {field: 'tradeMarketName', title: 'EtrBondFirstLBidding002.tradeMarket',sortable : false, width: 120}
        , {field: 'entrustDate', title: 'EtrBondFirstLBidding002.bidDate',sortable : false, type: 'date', width: 150}
        , {field: 'etrPayType', title: 'EtrBondFirstLBidding002.payType',sortable : false, width: 150, visible: false}
        , {field: 'etrPayTypeName', title: 'EtrBondFirstLBidding002.payType', sortable : false,width: 150}
        , {field: 'invAim', title: 'EtrBondFirstLBidding002.invAim', width: 120, sortable : false,visible: false}
        , {field: 'invAimName', title: 'EtrBondFirstLBidding002.invAim', sortable : false,width: 120}
        , {field: 'counterId', title: 'EtrInvestPersonInfo002.counterId', sortable : false,width: 120, visible: false}
        , {field: 'counterIdName', title: 'EtrInvestPersonInfo002.counterId', sortable : false,width: 120}
        , {field: 'bidPrice', title: 'EtrBondFirstLBidding001.bidPrice',sortable : false, type: 'number', width: 150}
        , {field: 'ccy', title: 'EtrBondFirstLBidding002.ccy', sortable : false,width: 150, visible: false}
        , {field: 'ccyName', title: 'EtrBondFirstLBidding002.ccy',sortable : false, width: 150}
        , {field: 'assetType1', title: 'EtrInvestBondInfo001.type9', sortable : false,width: 150, visible: false}
        , {field: 'assetType1Name', title: 'EtrInvestBondInfo001.type9', sortable : false,width: 150}
        , {field: 'assetType10', title: 'EtrInvestBondInfo001.type10', sortable : false,width: 150, visible: false}
        , {field: 'assetType10Name', title: 'EtrInvestBondInfo001.type10', sortable : false,width: 150}
        , {field: 'assetType11', title: 'EtrInvestBondInfo001.type11',sortable : false, width: 150, visible: false}
        , {field: 'assetType11Name', title: 'EtrInvestBondInfo001.type11',sortable : false, width: 150}
        , {field: 'bidPrinAmt', title: 'EtrBondFirstLBidding001.bidPriceTotal', sortable : false,type: 'number', width: 150}
        , {field: 'dealAmount', title: 'EtrBondFirstLBidding003.dealAmount', sortable : false,type: 'number', width: 150}
        , {field: 'etrAvailableCash', title: 'EtrBondFirstLBidding003.etrAvailableCash',sortable : false, type: 'number', width: 150}
        , {field: 'etrAvailablePosition', title: 'EtrBondFirstLBidding003.etrAvailablePosition', sortable : false,type: 'number', width: 150}
        , {field: 'mainConsignee', width: 150,title: 'EtrBondFirstLBidding002.mainConsignee', sortable : false}
        , {field: 'fileArray', title: 'EtrBondFirstLBidding003.fileArray', width: 150, visible: false}
        , {field: 'businessId', width: 150, visible: false}
        , {field: 'fileArrayName', title: 'EtrBondFirstLBidding003.fileArray', type: 'text', width: 150}
        , {
            field: 'operation1', title: 'SysCommon.operation', render: function (data, type, row, meta) {
                return '<a class="pro-link uploadFile">上传附件</a> ';
            }, width: 150
        }
    ];
    var table = $("#dataTable").joyintable({
        data: etrBondFirstLBiddingDtoList == null || etrBondFirstLBiddingDtoList == '' ? null : JSON.parse(etrBondFirstLBiddingDtoList),
        cols: cols,
        height: 290,
        addbtn:true
    });


    //绑定上传事件
    table.bind('click', '.uploadFile', function (data) {
        data.operateType = "edit";
        data.pageModel = PAGE_MODEL_EDIT;
        var object = {};
        object.rowData = JSON.stringify(data);
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
                table.renderRow(data.LAY_TABLE_INDEX, result);
            });
    });

    /**
     * 保存或提交
     *
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        // 风险试算验证 add by yufei 20200826 start
        var s;
        if(BaseRiskMathComm) {
            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
            if(!s && s != ''){
                return;
            }
        }
        // // 风险试算验证 add by yufei 20200826 end
        //校验数据  校验表格
        var tableData = table.getAllRows();
        if(operateType != 'save'){
            if (tableData.length == 0) {
                Joyin.alert("委托列表至少有一条数据！", 2);
                return false;
            }
            for (var i = 0; i < tableData.length; i++) {
                if (!tableData[i].bidPrice) {
                    Joyin.alert("投标标位不能为空！", 2);
                    return false;
                }
                if (tableData[i].bidPrice > 100 || tableData[i].bidPrice <= 0) {
                    Joyin.alert("投标标位的值应大于0，小于等于100！", 2);
                    return false;
                }
                if (!tableData[i].bidPrinAmt) {
                    Joyin.alert("投标券面总额不能为空！", 2);
                    return false;
                }
                if (tableData[i].bidPrinAmt.length > 16) {
                    Joyin.alert("投标券面总额的值不能超过16位！", 2);
                    return false;
                }
            }
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm', true);
        object.operateType = operateType;
        object.pageModel = pageModel;
        var objects = new Array();
        for (var index in object.etrBondFirstLBiddingDtoList) {
            var item = object.etrBondFirstLBiddingDtoList[index];
            var temp = JSON.parse(JSON.stringify(object));
            temp.vdate = item.vdate;
            temp.mdate = item.mdate;
            temp.portfolioId = item.portfolioId;
            temp.tradeMarket = item.tradeMarket;
            temp.invAim = item.invAim;
            var bidPriceList = new Array();
            var bidPrice = {};
            bidPrice.bidPrice = item.bidPrice;
            bidPrice.bidPrinAmt = item.bidPrinAmt;
            bidPriceList.push(bidPrice);
            temp.bidPriceList = bidPriceList;
            temp.entrustNum = item.bidPrinAmt;
            temp.ccy = item.ccy;
            temp.finprodId = item.finprodId;
            temp.finprodMarketId = item.finprodMarketId;
            temp.assetType1 = item.assetType1;
            temp.assetType10 = item.assetType10;
            temp.assetType11 = item.assetType11;
            temp.etrPayType = item.etrPayType;
            temp.entrustDate = item.entrustDate;
            temp.dealAmount = item.dealAmount;
            temp.etrAvailableCash = item.etrAvailableCash;
            temp.etrAvailablePosition = item.etrAvailablePosition;
            temp.fileArray = item.fileArray;
            temp.mainConsignee = item.mainConsignee;
            temp.counterId = item.counterId;
            temp.etrInvestPersonInfo = item.etrInvestPersonInfo;
            temp.etrInvestPersonInfo.counterId = item.counterId;
            if (operateType === 'save') {
                temp.elementId = 'EtrBondFirstLBidding002_btn_save';
            } else {
                temp.elementId = 'EtrBondFirstLBidding002_btn_submit';
            }
            objects.push(temp);
        }
        var allDto = {};
        allDto.importFlow = true;
        allDto.selectedItem = objects;
        Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', allDto, function(result1) {
            Joyin.ajaxJson(basePath + 'invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding003/check', allDto,
                function (result) {
                    Joyin.confirm(result.data, function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding003/save', allDto, "EtrBondFirstLBidding002_btn_save","EtrBondFirstLBidding002");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/entrust/etrbondfirstlbidding/EtrBondFirstLBidding003/save', allDto, "EtrBondFirstLBidding002_btn_submit","EtrBondFirstLBidding002");
                        }
                    });
                });
        })
    }

    $(function () {
        if (etrBondFirstLBiddingDtoList) {
            $.each(JSON.parse(etrBondFirstLBiddingDtoList), function (id, value) {
                bidPrinAmt += parseInt(value.bidPrinAmt);
            });
            $('#entrustNum').val(bidPrinAmt);
            table.reload({data: JSON.parse(etrBondFirstLBiddingDtoList)});
        }
        /**
         * 提交
         */
        $('#EtrBondFirstLBidding003_btn_submit').click(function () {
            saveOrSubmit('submit');
        });


        // 代办显示点击查看风险试算结果  Start
        /**
         * 代办点击查看风险试算结果
         */
        if (todoList == true) {
            $("#riskOfTrialLink").show();
        } else {
            $("#riskOfTrialLink").hide();
        }
        $("#riskOfTrialLink").click(function () {
            Joyin.dialog(JoyinUtil.getLabel("BaseRiskMathComm.riskAndCredit"),
                basePath + "component/common/RiskMathAndCredit/init",
                "90%", "90%", {workflowId: workflowId, tradeId: tradeId, TodoList: todoList});
        });
        // 代办显示点击查看风险试算结果   End

        /**
         * 风险试算
         */
        $('#EtrBondFirstLBidding003_btn_riskcal').click(function () {
            // // 风险试算修改 add by yufei 20200826 start
            var object = $.serializeToObject('#searchForm').etrBondFirstLBiddingDtoList;
            var checkParam = {};
            checkParam.data = getriskdata();
            var param = [];
            if (checkParam.data.length > 0) {
                for(var index in checkParam.data){
                    var item = checkParam.data[index];
                    item.data.ps = etrPs;
                    item.data.portfolioId = object[index].portfolioId;
                    item.data.finprodId = object[index].finprodId;
                    item.data.assetCode = object[index].finprodId;
                    item.data.entrustAmt = item.data.tradeAmount;
                    item.data.counterId = item.data.counterId;
                    param.push(item.data);
                }
            }
            // Joyin.ajaxJson(basePath + 'component/common/BaseRiskMathComm/check', initParam, function (resultresult) {
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/batchRiskTrialRds/0/F01',param,checkParam);
            // });
            // // 风险试算修改 add by yufei 20200826 end
        });
    })
}());