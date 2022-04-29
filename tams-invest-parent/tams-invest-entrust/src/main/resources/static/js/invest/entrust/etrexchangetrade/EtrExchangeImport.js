/**
 * 指令列表JS
 */
(function () {
	
	var etrExchangeInfoDtoList = parent.correctData || [];
	
    var getriskdataAll = function () {
        var selectedItem = $.serializeToObject('#searchForm').selectedItem;
        if (selectedItem.length > 0) {
            var objects = new Array();
            for ( var index in selectedItem) {
                var item = selectedItem[index];
                var object = getriskdata(item);
                objects.push(object);
            }
            return objects;
        }
    }
	
    /**
     * 获取风险试算数据
     */
    var getriskdata = function (object) {
    	if(!object){
    		return;
    	}
        var obj = {}
        //债券
        if(object.finprodType2 == FINPROD_TYPE_TG_F01) {            
            var tradeAmount = 0;
            if(object.ps == PS_T49) {
                tradeAmount = object.entrustNum;
            }
            else {
                tradeAmount =  object.entrustAmt;
            }
            obj = {
                    ps: object.ps,
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: sysDate,
                    portfolioCode: object.portfolioId,
                    entrustAmt: tradeAmount,
                    assetType: object.finprodType2,
                    isEnabled: '01',
                    assetCode: object.finprodId
            }
            
        }
        //基金
        else if (object.finprodType2 == FINPROD_TYPE_TG_F06) {
            //买入
            if(object.ps == PS_T01) {
                obj = {
                        ps: object.ps,
                        timePoint: MY_SUP_TIME_TYPE_02,
                        valDate: sysDate,
                        portfolioCode: object.portfolioId,
                        entrustAmt: object.entrustAmt,
                        assetType: object.finprodType2,
                        isEnabled: '01',
                        assetCode: object.finprodId
                }
            }
            //卖出
            else if(object.ps == PS_T02){
                obj = {
                        ps: object.ps,
                        timePoint: MY_SUP_TIME_TYPE_02,
                        valDate: sysDate,
                        portfolioCode: object.portfolioId,
                        entrustAmt: object.entrustAmt,
                        assetType: object.finprodType2,
                        isEnabled: '01',
                        assetCode: object.finprodId,
                        amtType:'1'
                }
            }
        }
        //股票
        else if (object.finprodType2 == FINPROD_TYPE_TG_F10) {
            obj = {
                    ps: object.ps,
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: sysDate,
                    portfolioCode: object.portfolioId,
                    entrustAmt: object.entrustAmt,
                    assetType: object.finprodType2,
                    isEnabled: '01',
                    assetCode: object.finprodId
            }
        }
        //质押式回购
        else if (object.finprodType2 == FINPROD_TYPE_TG_F02 || object.finprodType2 == FINPROD_TYPE_TG_F03 || object.finprodType2 == FINPROD_TYPE_TG_ZYSHG) {
            obj = {
                    ps: object.ps,
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: sysDate,
                    portfolioCode: object.portfolioId,
                    entrustAmt: object.entrustAmt,
                    assetType: object.finprodType2,
                    isEnabled: '01',
                    assetCode: object.finprodId
            }
        }

        var riskdata = {
                ps: ['ps', 'EtrExchangeTrade001.ps'],
                portfolioCode: ['portfolioId', 'EtrExchangeTrade001.portfolioId'],
                entrustAmt: ['entrustAmt','EtrExchangeTrade003.entrustAmt'],
                assetType: ['assetType1', 'EtrExchangeTrade003.finprodType2'],
                data: obj,
                counterId: ['counterId', 'EtrExchangeTrade001.counterId']
            }
        return riskdata;
    }
    var cols = [
          {field: 'seqNo', title: 'EtrBrowDepoInfo003.indexId', width: 70, sortable: false}
        , {field: 'portfolioId', title: 'EtrBrowDepoInfo001.portfolioId', width: 120, sortable: false}
        , {field: 'acctNo', title: 'EtrExchangeTrade001.acctNo2', width: 150, sortable: false}
        , {field: 'stockId', title: 'EtrBrowDepoInfo001.finprodId', width: 150, sortable: false}
        , {field: 'psStr', title: 'EtrExchangeTrade001.ps', width: 150, sortable: false}
        , {field: 'priceModelStr', title: 'EtrExchangeTrade003.priceModel',sortable : false, width: 120,edit: false}
        , {field: 'entrustPrice', title: 'EtrExchangeTrade003.price', width:100, type:'number', digit : 3, sortable: false,
        	render: function(data, type, row, meta) {
        	    if (row.priceModel == "PRM03") {
        	        return "0.00";//市价指令，价格默认显示0
        	    } else {
        	        return row.entrustPrice.toFixed(3);
        	    }
        	}
        }
        , {field: 'fprice', title: 'EtrExchangeTrade001.fprice', width:100, type:'number', digit : 3, sortable: false}
        , {field: 'yield', title: 'EtrExchangeImport.yield', width:100, type:'number', digit : 6, sortable: false}
        , {field: 'entrustNum', title: 'EtrExchangeTrade001.entrustNum', width: 120, type:'number', digit : 0, sortable: false}
        , {field: 'entrustAmt', title: 'EtrBrowDepoInfo003.entrustAmt2', width: 120, type:'number',sortable: false}
        , {field: 'proAvailableCash', title: 'EtrBrowDepoInfo003.etrAvailableCash', type: 'number', width: 150, sortable: false}
        , {field: 'accAvailablePos', title: 'EtrBrowDepoInfo003.etrAvailablePosition', type: 'number', width: 150, sortable: false}
        , {field:'vdate', title: 'EtrExchangeTrade003.vdate',type:'date', width : 120,sortField:'VDATE'}//持仓成本
        , {field:'mdate', title: 'EtrExchangeTrade003.mdate',type:'date', width : 120,sortField:'MDATE'}//最新价
        //add by chenjiaxing 20210830 导入添加投资目的 start
        // , {field:'invAimStr', title: 'EtrExchangeTrade001.invAim', width : 240,sortable: false}
        , {field:'businessModelStr', title: 'EtrExchangeTrade001.businessModel', width : 240,sortable: false}
        //add by chenjiaxing 20210830 导入添加投资目的 end
    ];

    //大宗交易指令
    var dzCols = [
        {field: 'seqNo', title: 'EtrBrowDepoInfo003.indexId', width: 70, sortable: false}
        , {field: 'portfolioId', title: 'EtrExchangeTrade001.portfolioId', width: 120, sortable: false}
        , {field: 'acctNo', title: 'EtrExchangeTrade001.acctNo', width: 150, sortable: false}
        , {field: 'finprodId', title: 'EtrExchangeTrade001.finprodId', width: 120, sortable: false}
        , {field: 'psStr', title: 'EtrExchangeTrade001.ps', width: 150, sortable: false}
        , {field: 'priceModelStr', title: 'EtrExchangeTrade003.priceModel',sortable : false, width: 120,edit: false}
        , {field: 'declaTypeStr', title: 'EtrExchangeTrade001.priceModel2',sortable : false, width: 120,edit: false}
        , {field: 'entrustPrice', title: 'EtrExchangeTrade003.price', width:100, type:'number', digit : 3, sortable: false,
            render: function(data, type, row, meta) {
                if (row.priceModel == "PRM03") {
                    return "0.00";//市价指令，价格默认显示0
                } else {
                    return row.entrustPrice;
                }
            }
        }
        , {field: 'fprice', title: 'EtrExchangeTrade001.fprice', width:100, type:'number', digit : 3, sortable: false}
        , {field: 'yield', title: 'EtrExchangeImport.yield', width:100, type:'number', digit : 6, sortable: false}
        , {field: 'targetTypeStr', title: 'EtrExchangeTrade001.targetType', width: 150, sortable: false}
        , {field: 'entrustNum', title: 'EtrExchangeTrade001.entrustNum', width: 120, type:'number', digit : 0, sortable: false}
        , {field: 'entrustAmt', title: 'EtrExchangeTrade001.entrustAmt', width: 120, type:'number',sortable: false}
        , {field: 'proAvailableCash', title: 'EtrBrowDepoInfo003.etrAvailableCash', type: 'number', width: 150, sortable: false}
        , {field: 'accAvailablePos', title: 'EtrBrowDepoInfo003.etrAvailablePosition', type: 'number', width: 150, sortable: false}
        // , {field: 'counterId', title: 'EtrExchangeTrade001.counterId', width: 120, sortable: false}
        , {field: 'counterId', title: 'EtrExchangeTrade001.counterId', width: 120, sortable: false ,
            editOption: {
                type: 'select',
                id: 'counterId',
                multiple: false,
                showField: 'text',
                searchable: true,
                url: JoyinUtil.getRealPath('/invest/entrust/etrexchangetrade/EtrExchangeTrade001/selectCounterIdByCounterType')
            }
        }
        , {field: 'invAim', title: 'EtrAssetConfigTransfer002.invAim', width: 100,edit:true,editOption:{type:'select',formatoption:true,showField: 'text',result: INV_AIM}}
        , {field: 'seatId', title: 'EtrExchangeTrade001.seatId', width: 120, sortable: false}
        , {field: 'agreeOnId', title: 'EtrExchangeTrade001.agreeOnId', width: 120, sortable: false}
        , {field:'vdate', title: 'EtrExchangeTrade003.vdate',type:'date', width : 120,sortField:'VDATE'}//委托开始日
        , {field:'mdate', title: 'EtrExchangeTrade003.mdate',type:'date', width : 120,sortField:'MDATE'}//委托开始日
        , {field: 'secAcctId',visible: false}
    ];

    //固定收益指令
    var gsCols = [
        {field: 'seqNo', title: 'EtrBrowDepoInfo003.indexId', width: 70, sortable: false}
        , {field: 'portfolioId', title: 'EtrExchangeTrade001.portfolioId', width: 120, sortable: false}
        , {field: 'acctNo', title: 'EtrExchangeTrade001.acctNo', width: 150, sortable: false}
        , {field: 'finprodId', title: 'EtrExchangeTrade001.finprodId', width: 120, sortable: false}
        , {field: 'psStr', title: 'EtrExchangeTrade001.ps', width: 150, sortable: false}
        , {field: 'settleSpreedName', title: 'EtrExchangeTrade001.settleSpreed', width: 150, sortable: false}
        , {field: 'priceModelStr', title: 'EtrExchangeTrade003.priceModel',sortable : false, width: 120,edit: false}
        , {field: 'declaTypeStr', title: 'EtrExchangeTrade001.priceModel2',sortable : false, width: 120,edit: false}
        , {field: 'entrustPrice', title: 'EtrExchangeTrade003.price', width:100, type:'number', digit : 3, sortable: false,
            render: function(data, type, row, meta) {
                if (row.priceModel == "PRM03") {
                    return "0.00";//市价指令，价格默认显示0
                } else {
                    return row.entrustPrice;
                }
            }
        }
        , {field: 'fprice', title: 'EtrExchangeTrade001.fprice', width:100, type:'number', digit : 3, sortable: false}
        , {field: 'yield', title: 'EtrExchangeImport.yield', width:100, type:'number', digit : 6, sortable: false}
        , {field: 'targetTypeStr', title: 'EtrExchangeTrade001.targetType', width:100, sortable: false}
        , {field: 'entrustNum', title: 'EtrExchangeTrade001.entrustNum', width: 120, type:'number', digit : 0, sortable: false}
        , {field: 'entrustAmt', title: 'EtrBrowDepoInfo003.entrustAmt', width: 120, type:'number',sortable: false}
        , {field: 'proAvailableCash', title: 'EtrBrowDepoInfo003.etrAvailableCash', type: 'number', width: 150, sortable: false}
        , {field: 'accAvailablePos', title: 'EtrBrowDepoInfo003.etrAvailablePosition', type: 'number', width: 150, sortable: false}
        , {field: 'counterId', title: 'EtrExchangeTrade001.counterId', width: 120, sortable: false}
        , {field: 'counterTrader', title: 'EtrExchangeTrade001.counterTrader', width: 120, sortable: false}
        , {field: 'invAim', title: 'EtrAssetConfigTransfer002.invAim', width: 100,edit:true,editOption:{type:'select',formatoption:true,showField: 'text',result: INV_AIM}}
        , {field: 'trader', title: 'EtrExchangeTrade001.traders', width: 120, sortable: false}
        , {field: 'agreeOnId', title: 'EtrExchangeTrade001.agreeOnId', width: 120, sortable: false}
        , {field:'vdate', title: 'EtrExchangeTrade003.vdate',type:'date', width : 120,sortField:'VDATE'}//委托开始日
        , {field:'mdate', title: 'EtrExchangeTrade003.mdate',type:'date', width : 120,sortField:'MDATE'}//委托开始日
    ];

    var table =$("#dataTable").joyintable({
		data: etrExchangeInfoDtoList,
        cols:SHARES_TRADE_PLATFORM_03==currentTab?gsCols:SHARES_TRADE_PLATFORM_04==currentTab?dzCols:cols,
        page: false,
        checkbox: false,
        filter: false,
        addbtn:false,
        addToLast:true,
        searchOnInit: true,
        height:250
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
	
    // add by zhangrui1 start
    //添加风险试算验证
    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                for(var index in riskparam){
                    var item = checkParam.data[index];
                    item.data =null;
                }
            }
            window.BaseRiskMathComm.getparam(riskparam);
        }
    }
    // add by zhangrui1 end
    
    /**
     * 保存或提交
     *
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        // add by zhangrui1 start
       var s;
       if(BaseRiskMathComm) {
           s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
           if(!s && s != ''){
               return;
           }
           if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
               return;
           }
       }
        // add by zhangrui1 end
        //校验数据  校验表格
        var tableData = table.getAllRows();
        if (tableData.length == 0) {
            Joyin.alert("指令列表至少有一条数据！", 2);
            return false;
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm', true);
        object.operateType = operateType;
        object.pageModel = pageModel;
        var objects = object.selectedItem;
        for (var index in objects) {
            var temp = objects[index];
            temp.pageModel = pageModel;
            temp.operateType = operateType;
            if (operateType === 'save') {
                temp.elementId = 'EtrBrowDepoInfo002_btn_save';
            } else {
                temp.elementId = 'EtrBrowDepoInfo002_btn_submit';
            }
        }
        var allDto = {};
        allDto.importFlow = true;
        allDto.selectedItem = objects;
        allDto.operateType = operateType;
        allDto.pageModel = pageModel;
        allDto.riskTrialRetDto = object.riskTrialRetDto;
        Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', allDto, function(result1) {
            Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeImport/check', allDto,
                function (result) {
                    Joyin.confirm(result.data, function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeImport/save', allDto, "EtrExchangeImport_btn_save",'EtrExchangeImport', function () {
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);
                            });
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeImport/save', allDto, "EtrExchangeImport_btn_submit",'EtrExchangeImport', function () {
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);
                            });
                        }
                    });
                });
        })
    }
    $(function () {
        /**
         * 风险试算
         */
        $('#EtrBrowDepoInfo003_btn_riskcal').click(function () {
            var object = $.serializeToObject('#searchForm').selectedItem;
            var checkParam = {};
            checkParam.data = getriskdataAll();
            var param = [];
            if (checkParam.data.length > 0) {
                for (var index in checkParam.data) {
                    var item = checkParam.data[index];
                    item.data.ps = object[index].ps;
                    item.data.portfolioCode = object[index].portfolioId;
                    item.data.finprodId = object[index].finprodId;
                    item.data.assetCode = object[index].finprodId;
                    item.data.entrustAmt = object[index].entrustAmt;
                    if (item.data.ps == PS_03) {
                        item.data.assetType = FINPROD_TYPE_TG_F02;
                        object[index].finprodType2 = FINPROD_TYPE_TG_F02;
                    } else if (item.data.ps == PS_04) {
                        item.data.assetType = FINPROD_TYPE_TG_F03;
                        object[index].finprodType2 = FINPROD_TYPE_TG_F02;
                    } else {
                        item.data.assetType = object[index].finprodType2;
                    }
                    param.push(object[index]);
                }
            }
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskRdsTrial/interf/trialList/' + pageId + '/0', param, checkParam);
            // add by zhangrui1 end
        });

        /**
         * 提交
         */
        $('#EtrBrowDepoInfo003_btn_submit').click(function () {
            saveOrSubmit('submit');
        });
    });
}());
