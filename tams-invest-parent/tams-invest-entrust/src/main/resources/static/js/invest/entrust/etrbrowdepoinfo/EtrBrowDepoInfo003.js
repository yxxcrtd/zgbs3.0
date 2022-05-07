/**
 * 委托列表JS
 */
(function () {
    var getriskdata = function () {
        var selectedItem = $.serializeToObject('#searchForm').selectedItem;
        if (selectedItem.length > 0) {
            var objects = new Array();
            for ( var index in selectedItem) {
                var item = selectedItem[index];
                var trdProductDeal = {};
                trdProductDeal.tradeAmt = item.entrustAmt;
                trdProductDeal.unitCprice  = item.price;
                    var obj = {
                        tradeDirection : item.ps,
                        timePoint : MY_SUP_TIME_TYPE_02,
                        valDate : sysDate,
                        portfolioCode : item.portfolioId,
                        tradeAmount : item.entrustAmt,
                        assetType : fintype,
                        assetCode :item.finprodId,
                        isEnabled : '01',
                        fullDataFlag : '1',
                        trdProductDeal : trdProductDeal,
                        counterId : item.counterId
                    }
                var object = {
                    tradeDirection : [ 'ps', 'EtrBrowDepoInfo001.ps' ],
                    portfolioCode : [ 'portfolioId', 'EtrBrowDepoInfo001.portfolioId' ],
                    tradeAmount : [ 'entrustAmt','EtrBrowDepoInfo001.entrustAmt' ],
                    assetType : ['finprodType2','EtrBrowDepoInfo001.finprodType2'],
                    assetCode : [ 'finprodMarketId','EtrBrowDepoInfo001.finprodId'],
                    data : obj
                }
                objects.push(object);
            }
            return objects;
        }
    }
	
    var cols = [
        {field: 'indexId', title: 'EtrBrowDepoInfo003.indexId', width: 100, sortable: false}
        , {field: 'vdateStr', title: 'EtrBrowDepoInfo001.entrustVDate', width: 120, sortable: false}
        , {field: 'mdateStr', title: 'EtrBrowDepoInfo001.entrustMDate', width: 120, sortable: false}
        , {field: 'portfolioId', title: 'EtrBrowDepoInfo001.portfolioId', width: 120, sortable: false}
        , {field: 'finprodAbbr', title: 'EtrBrowDepoInfo001.finprodId', width: 150, sortable: false}
        , {field: 'psStr', title: 'EtrBrowDepoInfo001.ps', width: 150, sortable: false}
        , {field: 'ccyStr', title: 'EtrBrowDepoInfo001.ccy', width: 120, sortable: false}
        , {field: 'entrustAmt', title: 'EtrBrowDepoInfo003.entrustAmt', width: 120, sortable: false,type: 'number',digit: 2}
        , {field: 'price', title: 'EtrBrowDepoInfo003.price', width:100, type:'number', sortable: false}
        , {field: 'dateStartStr', title: 'EtrBrowDepoInfo001.vdate', width: 120, sortable: false}
        , {field: 'dateEndStr', title: 'EtrBrowDepoInfo001.mdate', width: 120, sortable: false}
        , {field: 'termDays', title: 'EtrBrowDepoInfo002.termDays', width: 120, sortable: false}
        , {field: 'ccy', title: 'EtrBondFirstLBidding002.ccy', width: 150, visible: false, sortable: false}
        , {field: 'chlIdName', title: 'invest.channelId', width: 150, sortable: false}
        , {field: 'remark', title: 'EtrBrowDepoInfo001.remark', width: 150, sortable: false}
        , {field: 'proAvailableCash', title: 'EtrBrowDepoInfo003.etrAvailableCash', type: 'number', width: 150, sortable: false}
        , {field: 'assetAvailablePosition', title: 'EtrBrowDepoInfo003.etrAvailablePosition', type: 'number', width: 150, sortable: false}
        , {
            field: 'operation1', title: 'SysCommon.operation', render: function (data, type, row, meta) {
                return '<a class="pro-link uploadFile">上传附件</a> ';
            }, width: 150, sortable: false
        }
    ];
    var table =$("#dataTable").joyintable({
		data: etrBrowDepoInfoDtoList == null || etrBrowDepoInfoDtoList == '' ? null : JSON.parse(etrBrowDepoInfoDtoList),
        cols:cols,
        page: false,
        checkbox: false,
        filter: false,
        addbtn:false,
        addToLast:true,
        searchOnInit: true
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
        }
        // add by zhangrui1 end
        //校验数据  校验表格
        var tableData = table.getAllRows();
        if (tableData.length == 0) {
            Joyin.alert("委托列表至少有一条数据！", 2);
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
            //产品化_SNSTZJY-2382 存款类资产，导入指令时，实时资产查询和实时资金查询没有发生改变 add by zhangpei 20201210 start
            temp.operateType=operateType;
            //产品化_SNSTZJY-2382 存款类资产，导入指令时，实时资产查询和实时资金查询没有发生改变 add by zhangpei 20201210 end
            temp.pageModel = pageModel;
            if (operateType === 'save') {
                temp.elementId = 'EtrBrowDepoInfo002_btn_save';
            } else {
                temp.elementId = 'EtrBrowDepoInfo002_btn_submit';
            }
            temp.price = temp.price/100
        }
        var allDto = {};
        allDto.importFlow = true;
        allDto.selectedItem = objects;
        allDto.operateType = operateType;
        allDto.pageModel = pageModel;
        allDto.riskTrialRetDto = object.riskTrialRetDto;
        Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', allDto, function(result1) {
            Joyin.ajaxJson(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo003/check', allDto,
                function (result) {
                    Joyin.confirm(result.data, function () {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo003/save', allDto, "EtrBrowDepoInfo002_btn_save",'EtrBrowDepoInfo002');
                        } else {
                            WorkFlow.doSubmitList(basePath + 'invest/entrust/etrbrowdepoinfo/EtrBrowDepoInfo003/save', allDto, "EtrBrowDepoInfo002_btn_submit",'EtrBrowDepoInfo002');
                        }
                    });
                });
        })
    }
    
    /**
     * 风险试算
     */
    $('#EtrBrowDepoInfo003_btn_riskcal').click(function(){
        //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
        // add by zhangrui1 start
        //$("#riskMathFlag").val("02");
        var object = $.serializeToObject('#searchForm').selectedItem;
        var checkParam = {};
        checkParam.data = getriskdata();
        var param = [];
        if (checkParam.data.length > 0) {
            for(var index in checkParam.data){
                var item = checkParam.data[index];
                item.data.ps = object[index].ps;
                item.data.portfolioId = object[index].portfolioId;
                item.data.finprodId = object[index].finprodId;
                item.data.assetCode = object[index].finprodId;
                item.data.entrustAmt = item.data.tradeAmount;
                item.data.counterId = item.data.counterId;
                param.push(item.data);
            }
        }
        window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/batchRiskTrialRds/0/F05', param, checkParam);
        // add by zhangrui1 end
    });
    
    /**
     * 提交
     */
    $('#EtrBrowDepoInfo003_btn_submit').click(function() {
        saveOrSubmit('submit');
    });
}());
