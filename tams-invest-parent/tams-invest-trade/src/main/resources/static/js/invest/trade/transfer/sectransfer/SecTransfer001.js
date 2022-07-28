$(function(){

    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone) {
        	if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel==PAGE_MODEL_NEW){
                    riskparam.data =null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
        	}
            $("#bussTrdAvailableCashQuery_div_cash").show();
            $("#bussTrdAvailableCashQuery_div_position").hide();
            $("#bussTrdAvailableCashQuery_avalibleCashT1").parent().parent().hide();
            $("#bussTrdAvailableCashQuery_avalibleCash").parent().prev().html("组合可用现金");
            if(chlId && chlId != null){
                $("#bussTrdAvailableCashQuery_div_channelCash").show();
            }
        }
    };
    
    /**
     * 组合代码联动组合现金、资产持仓变化
     */
    var portfolioIdChange = function (object) {
    	// debugger;
        if(object && object != null && object != ""){
            var params = {};
            var avalibleCash = $("#bussTrdAvailableCashQuery_avalibleCash").val();
            var channelAvailableCash = $('#bussTrdAvailableCashQuery_channelAvailableCash').val();
            if(avalibleCash && channelAvailableCash){
            	params.avalibleCash = Number(avalibleCash) - Number(object.tradeAmt);
                params.channelAvailableCash = Number(channelAvailableCash) - Number(object.tradeAmt);
            }
            if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
            	window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].setPosition(params);
            }
            $("#portfolioId").val('').trigger("change").trigger('blur');
        }
        else {
            var params = {};
            params.portfolioId = $("#portfolioId").val();
            params.calendarId = $("#calendarId").val();
            params.ccy = $("#ccy").val();
            params.tradeDate = $("#tradeDate").val();
            params.finprodId = $("#finprodId").val();
            params.chlId = $("#chlId").val();
            params.avalibleCash = "";
            params.channelAvailableCash = "";
            if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
                window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
                    return params
                });
            }
        }
        if(!$("#portfolioId").val()){
        	$("#bussTrdAvailableCashQuery_avalibleCash").val('').trigger("change").trigger('blur');
        }
        $("#tradeAmt").val('').trigger("change").trigger('blur');
        $("#cpriceAmt").val('').trigger("change").trigger('blur');
        $("#intAmt").val('').trigger("change").trigger('blur');
        $("#fpriceAmt").val('').trigger("change").trigger('blur');
        $("#procedureAmt").val('').trigger("change").trigger('blur');
        $("#settleAmt").val('').trigger("change").trigger('blur');
        $("#tradeRepAmt").val('').trigger("change").trigger('blur');
        $("#unitInt").val('').trigger("change").trigger('blur');
        $("#unitFprice").val('').trigger("change").trigger('blur');
        $("#yield").val('').trigger("change").trigger('blur');
        $("#exerYield").val('').trigger("change").trigger('blur');      
        $("#turnInvAim").val('').trigger("change").trigger('blur');
        $("#yield").val('').trigger("change").trigger('blur');
        $("#turnInvAim").val('').trigger("change").trigger('blur');
        indexCount = 0;
    };
    

    
    var options = {
            /*
             * 给表格赋虚拟数据
             */
            page:false,
            height:'225px',
            /*
             * 定义表头
             */
    	    cols:[
    	        {field:'counterId', title: '组合代码',width:150, sortable: false}//组合代码
    	        ,{field:'portfolioName', title: '组合名称',width:150, sortable: false}//组合名称
    	        ,{field:'finprodMarketId', title: '资产代码',width:150, sortable: false}//资产代码
    	        ,{field:'tradeDate', title: '交易日期',type:'date',width:150, sortable: false}//交易日期
    	        ,{field:'settleDate', title: '交割日期',type:'date',width:150, sortable: false}//交易日期
    	        ,{field:'tradeAmt', title: '券面总额',width:150,type:'number', sortable: false}//券面总额
    	        ,{field:'unitCprice', title: '净价',width:150,type:'number',digit:6,sortable: false}//净价
    	        ,{field:'cpriceAmt', title: '净价总额',width:150,type:'number', sortable: false}//净价总额
    	        ,{field:'unitInt', title: '应计利息',width:150,digit:6,render : function(data, type, row, meta) {
                        if(row.unitInt !=undefined){
                            return Number(row.unitInt).toFixed(6);
                        }
                    }, sortable: false}//应计利息
    	        ,{field:'intAmt', title: '应计利息总额',width:150,type:'number', sortable: false}//应计利息总额
    	        ,{field:'unitFprice', title: '全价',width:150,type:'number', digit:6,sortable: false}//全价
    	        ,{field:'fpriceAmt', title: '全价总额',width:150,type:'number', sortable: false}//全价总额
    	        ,{field:'tradeRepAmt', title: '交易金额',width:150,type:'number', sortable: false}//交易金额
    	        ,{field:'yield', title: '到期收益率%',width:150,type:'number', digit: 12, sortable: false}//到期收益率 /*modify by jiangpeng on 20210512 ZGBSSE-258*/
    	        ,{field:'exerYield', title: '行权收益率%',width:150,type:'number', digit: 12, sortable: false}//行权收益率 /*modify by jiangpeng on 20210512 ZGBSSE-258*/
    	        ,{field:'finprodId', title: '资产代码',width:150, visible:false}//交易组合代码
    	        ,{field:'portfolioId', title: '交易对手',width:150, visible:false}//交易对手
    	        ,{field:'isOutTrade', title: '是否外部交易',width:150, visible:false}//是否外部交易
    	        ,{field:'ps', title: '交易方向',width:150,width:150, visible:false}//交易方向
    	        ,{field:'calendarId', title: '计息基础',width:150,width:150, visible:false}//交易方向
    	        ,{field:'ccy', title: '币种',width:150,width:150, visible:false}//交易方向
    	        ,{field:'counterType', title: '对手类型',width:150,width:150, visible:false}//交易方向
    	        ,{field:'finprodType2', title: '投管资产类型',width:150,width:150, visible:false}//交易方向
    	        //,{field:'invAim', title: '转仓投资目的',width:150,width:150}//投资目的
    	        ,{field:'isPayWithTrade', title: '是否随交易支付',width:150,width:150, visible:false}//交易方向
    	        ,{field:'tradeMarketMode', title: '交易市场',width:150,width:150, visible:false}//交易方向
    	        ,{field:'tradeMarket', title: '交易场所',width:150,width:150 }//交易方向
    	        // ,{field:'tradeAmt', title: '交易金额',width:150,type:'number', visible:false}//交易方向
    	        ,{field:'vdate', title: '资产起息日',type:'date',width:150, sortable: false}//交易日期
    	        ,{field:'mdate', title: '资产到期日',type:'date',width:150, sortable: false}//交易日期
    	        ,{field:'turnInvAim', title: '转仓投资目的',width:150,width:150, visible:false}//转仓投资目的
    	        ,{field:'turnInvAimStr', title: '转仓投资目的',width:150,width:150 }//转仓投资目的
    	        // ,{field:'invAim', title: '转仓投资目的',width:150,width:150,visible:false}//投资目的
    	        // ,{field:'invAimStr', title: '转仓投资目的',width:150,width:150}//投资目的
    	        // 待转让本金
	            //,{field: 'assignPrinamt',width: 150,title: '待转让本金',type: 'money', digit: 2, sortable: false}
	            // 待转让利息
	            ,{field: 'assignIntamt',width: 150,title: '待转让利息',type: 'money', digit: 2, sortable: false}
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
        $('#undistributedAmount').val(Number($('#undistributedAmount').val()) + Number(data.tradeAmt)).trigger("change").trigger('blur');
        $('#distributedAmount').val(Number($('#distributedAmount').val()) - Number(data.tradeAmt)).trigger("change").trigger('blur');
    });

    /**
     * 添加按钮按下处理
     */
    var resoluteChange = function () {
        //modified by xuzhihao start
        if (indexCount == 0) {
            Joyin.error("计算要素已变更，请重新点击计算按钮!");
        } else {
            var param = {};
            var options=$("#portfolioId").find("option:selected");
            var turnInvAimOptions=$("#turnInvAim").find("option:selected");
            param.turnInvAim = $('#turnInvAim').val();
            param.portfolioId = $('#portfolioId').val();
            param.undistributedAmount = $('#undistributedAmount').val();
            param.tradeAmt = $('#tradeAmt').val();
            param.tradeRepAmt = $('#tradeRepAmt').val();
            param.avalibleCash = $('#bussTrdAvailableCashQuery_avalibleCash').val();
            param.tradeRepAmtS = $('#fpriceAmt').val();
            param.unitCprice = $('#unitCprice').val();
            param.secResoluteDtoList = table.getAllRows();
            Joyin.ajaxJson(basePath + 'invest/trade/transfer/sectransfer/SecTransfer001/addCheck', param, function (result) {
                if(result && result.statuCode == 200){
                    //1、列表中添加数据
                    var portfolioId= options.val();
                    var portfolioId2 = options.text();
                    var row = {
                        counterId : options.val(),
                        portfolioName :options.text(),
                        finprodMarketId : $('#finprodMarketId').val(),
                        tradeDate : $('#tradeDate').val(),
                        settleDate : $('#settleDate').val(),
                        tradeAmt : $('#tradeAmt').val(),
                        unitCprice : $('#unitCprice').val(),
                        cpriceAmt : $('#cpriceAmt').val(),
                        unitInt : $('#unitInt').val(),
                        intAmt : $('#intAmt').val(),
                        unitFprice : $('#unitFprice').val(),
                        fpriceAmt : $('#fpriceAmt').val(),
                        procedureAmt : $('#procedureAmt').val(),
                        settleAmt : $('#settleAmt').val(),
                        exerYield : $('#yield').val(),
                        yield : $('#yield').val(),
                        tradeRepAmt : $('#tradeRepAmt').val(),
                        finprodId : $("#finprodId").val(),
                        portfolioId : $("#portfolioId").val(),
                        isOutTrade : 'N',//转仓交易默认内交易
                        ps : PS_T01,//转仓交易默认卖出交易
                        calendarId : '',
                        ccy : 'CNY',
                        counterType : 'N',//转仓交易默认内交易
                        finprodType2 : 'F01',//转仓交易默认债券
                        //invAim : $("#invAim").val(),//投资目的
                        // invAim : turnInvAimOptions.val(),//投资目的
                        // invAimStr : turnInvAimOptions.text(),//投资目的
                        turnInvAim : turnInvAimOptions.val(),//投资目的
                        turnInvAimStr : turnInvAimOptions.text(),//投资目的
                        isPayWithTrade : 'Y',
                        tradeMarketMode : '02',   //转仓交易默认二级市场
                        // tradeMarket : $('#tradeMarket').val(),
                        tradeMarket : $("#tradeMarket").find("option:selected").text(),
                        // tradeAmt : $('#fpriceAmt').val(),
                        vdate : $('#vdate').val(),
                        mdate : $('#mdate').val()
                        //turnInvAim : turnInvAimOptions.val(),
                       // turnInvAimStr : turnInvAimOptions.text()
                    };
                    $('#undistributedAmount').val(Number($('#undistributedAmount').val()) - Number($("#tradeAmt").val())).trigger("change").trigger('blur');
                    $('#distributedAmount').val(Number($('#distributedAmount').val()) + Number($("#tradeAmt").val())).trigger("change").trigger('blur');
                    
                    var totalPrinamt = $("#shareAmt").val();
                    var totalAssignIntamt = $("#assignIntamt").val();
                    if (Number($('#undistributedAmount').val()) == 0) {
                    	var assignAmt = 0;
                    	$.each(table.getAllRows(), function(index, item) {
                    		assignAmt += Number(item['assignIntamt']);
                    	});
                    	row['assignIntamt'] = JoyinMath.doMinus(Number(totalAssignIntamt), assignAmt);
                    } else {
                    	row['assignIntamt'] = Number(totalPrinamt) == 0 ? 0 : JoyinMath.doMul(totalAssignIntamt, JoyinMath.doDiv(row['tradeAmt'], totalPrinamt, 20), 2);
                    }
                    //row['assignPrinamt'] = 0;
                    table.addRow(row);

                    //2、现金和持仓联动
                    var object = {};
                    object.prinAmount = $('#tradeAmt').val();
                    object.tradeAmt = $('#tradeRepAmt').val();
                    $("#portfolioId").text('').trigger("change").trigger('blur');                   
                    portfolioIdChange(object);
                    
                	//3. 添加按钮点击时，净价回归默认
                    var unitCprice =  $("#unitCprice").attr("value");
                    if(unitCprice==undefined){
                    	unitCprice="";
                    }
                    $("#unitCprice").val(unitCprice).trigger("change").trigger('blur');  
                }
            })
        }
        // modified by xuzhihao end
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
        var object = $.serializeToObject('#secTransferForm', true);

        var data = table.getAllRows();
        var ptlFundAllocationDtos = [];
        for(i = 0; i < data.length; i ++){
            var ptlFundAllocationDto = {};
            ptlFundAllocationDto.portfolioId = data[i].counterId;
            ptlFundAllocationDto.settleDate = data[i].settleDate;
            ptlFundAllocationDto.ccy = data[i].ccy;
            ptlFundAllocationDto.payType = '1001';
            ptlFundAllocationDto.settleAmt = data[i].tradeRepAmt;
            ptlFundAllocationDtos.push(ptlFundAllocationDto);
        }
        object.ptlFundAllocationDtos = ptlFundAllocationDtos;

        //将交易场所设置为码值 add by xuzhihao start
        $.each(data, function (i, v) {
            v.tradeMarket = $('#tradeMarket').val();
        })
        //将交易场所设置为码值 add by xuzhihao end
        object.pageModel = pageModel;
        object.operateType = operateType;
        object.secResoluteDtoList = data;
        //保存unitCprice初始值，用于待办中编辑
        var unitCprice =  $("#unitCprice").attr("value");
        if (null == unitCprice || 'undefined' == typeof(unitCprice) || '' == unitCprice) {
        	delete object.unitCprice;
        }
        else{
        	object.unitCprice = unitCprice;
        }
        Joyin.ajaxJson(basePath + 'invest/trade/transfer/sectransfer/SecTransfer001/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                var tableArry = table.getAllRows();
                // Joyin.dialog('风险试算',)
                //object.riskCreditListCommonDto = result.data;
                if(operateType === 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/trade/transfer/sectransfer/SecTransfer001/save',
                        object, "SecTransfer001_btn_save");
                }
                else {
                    WorkFlow.doSubmitList(basePath + 'invest/trade/transfer/sectransfer/SecTransfer001/save',object, "SecTransfer001_btn_submit");
                }
            });
            });
    };

    var getriskdata = function () {
        var mun=null;
        var data = table.getAllRows();
        //拆分列表
        var list = [];
        var object = {};
        if(data!=null && data!="") {
            for (var i in data) {
                var obj = {
                    timePoint: MY_SUP_TIME_TYPE_02,
                    valDate: $('#tradeDate').val(),
                    portfolioCode: data[i].portfolioId,
                    tradeAmount: data[i].tradeRepAmt,
                    assetType: FINPROD_TYPE_F01,
                    isEnabled: '01',
                    assetCode: $('#finprodId').val(),
                    tradeDirection: PS_T01,
                    index: null,
                    mun: mun
                };
                list[i] = obj;
            }
            if ($('#windtypeone').val() && $('#windtypeone').val() != '') {
                for (var i in list) {
                    list[i].assetSecType = $('#windtypeone').val();
                }
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
    /**
     * 查询待转仓组合
     */
    var findAvaTransferPortfolioInfo = function(){
        var listInfoList = [];
        var allRows = table.getAllRows();
        for(var i in allRows){
            var obj = {
                portfolioId : allRows[i].portfolioId,
                tradeAmt : allRows[i].tradeMoney
            };
            listInfoList[i] = obj;
        }
        Joyin.dialog("待转仓产品组合信息",basePath+"/invest/trade/transfer/common/TransferCommon001/init",'60%','60%',{
            portfolioId : $("#investPortfolioId").val(),
            chlId : $("#chlId").val(),
            tradeDate : $("#tradeDate").val(),
            ccy : $("#ccy").val(),
            secAcctId:$("#secManageAcctId").val(),
            listInfoList : JSON.stringify(listInfoList)
        })
    };
    //add by xuzhihao start
    var indexCount = 0;
    //添加券面总额更改事件
    $("#tradeAmt").change(function () {
        indexCount = 0;
    });
    //add by xuzhihao end
    //计算所需的要素
    function getCalcParam() {
    	// debugger;
        var paramObj = $.serializeToObject('#secTransferForm', true);
        paramObj.tradeAmt = $("#tradeAmt").val();
        paramObj.unitCprice = $("#unitCprice").val();
        paramObj.unitInt = $("#unitInt").val();
        paramObj.unitFprice = $("#unitFprice").val();
        paramObj.cpriceAmt = $("#cpriceAmt").val();
        paramObj.intAmt = $("#intAmt").val();
        paramObj.fpriceAmt = $("#fpriceAmt").val();
        paramObj.exerYield = $("#exerYield").val();
        paramObj.yield = $("#yield").val();
        // 行权计算时用数据库中的理论行权日 add by renping 20191105 start
        if(paramObj.finprodId && ($("#ps").val() == 'T03' || $("#ps").val() == 'T39')) {
            Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/bond/TrdBondInvestTrade002/getExerciseDate'), paramObj, function (result) {
                if (result && result.data) {
                    paramObj.settleDate = result.data;
                }
            }, false);
            
        }
        // 行权计算时用数据库中的理论行权日 add by renping 20191105 end
        
        // 计算之前，先将应计利息清空
        paramObj.unitInt = null;
        // add by wangguoxian 费用设置 20190808 start
        // 交易手续费，结算费，佣金，通道费，交易金额不再计入计算要素
        paramObj.procedureAmt = null;
        paramObj.settleAmt = null;
        paramObj.commissionAmt = null;
        paramObj.channelFee = null;
        paramObj.tradeAmtAndFee = null;
        // add by wangguoxian 费用设置 20190808 end
        return paramObj;
    }
    /**
     * 点击计算按钮计算金额和利息
     */
    /**
     * 1.7计算按钮点击事件
     */
    $('#SecTransfer001_btn_calculate').click(function() {
        var paramObj = getCalcParam();

        Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/transfer/sectransfer/SecTransfer001/calculateData'), paramObj, function (result) {
            if (result && result.data) {
                var data = result.data;
                $("#tradeAmt").val(data.tradeAmt).trigger("blur");
                $("#unitCprice").val(data.unitCprice).trigger("blur");
                $("#unitInt").val(data.unitInt).trigger("blur");
                $("#unitFprice").val(data.unitFprice).trigger("blur");
                $("#cpriceAmt").val(data.cpriceAmt).trigger("blur");
                $("#intAmt").val(data.intAmt).trigger("blur");
                
                if($("#zhilingtype").val()=="02"){
                	for(var i=0;i<table.getAllRows().length;i++){
                		if($("#unitFprice").val()){
                			//获取i行的券面总额
                    		//全价总额
                    		var fpriceAmtSum=Number(0);
			        		   for(var i=0;i<table.getAllRows().length;i++){
			        			   //券面总额
				        		   var tradeAmt=table.getAllRows()[i].tradeAmt;
				        		   table.renderRow(i, {"fpriceAmt": Number($("#unitFprice").val())*Number(tradeAmt)/100});
				        		   fpriceAmtSum=Number(fpriceAmtSum)+Number(Number($("#unitFprice").val())*Number(tradeAmt)/100);
				        	   }
				        	$("#fpriceAmt").val(fpriceAmtSum).trigger('change');
                		}else{
                			table.renderRow(i, {"fpriceAmt": 0});
                		}
                		
                		if($("#unitInt").val()){
                    		//应计利息总额
                    		var unitIntSum=Number(0);
			        		   for(var i=0;i<table.getAllRows().length;i++){
			        			   //券面总额
				        		   var tradeAmt=table.getAllRows()[i].tradeAmt;
				        		   table.renderRow(i, {"intAmt": Number($("#unitInt").val())*Number(tradeAmt)/100});
				        		   unitIntSum=Number(unitIntSum)+Number(Number($("#unitInt").val())*Number(tradeAmt)/100);
				        	   }
                		}else{
                			table.renderRow(i, {"intAmt": 0});
                		}
                		
                	}
                }else{
                	$("#fpriceAmt").val(Number($("#intAmt").val())+Number($("#cpriceAmt").val())).trigger("blur").trigger('change');
                }
                //交易金额
                $("#tradeRepAmt").val($("#fpriceAmt").val()).trigger("blur");
                $("#exerYield").val(data.yield).trigger("blur");
                $("#yield").val(data.yield).trigger("blur");
                // 记录隐藏值
                $("#unitCprice_calc").val(data.unitCprice);
                $("#unitInt_calc").val(data.unitInt);
                $("#unitFprice_calc").val(data.unitFprice);
                $("#cpriceAmt_calc").val(data.cpriceAmt);
                $("#intAmt_calc").val(data.intAmt);
                $("#fpriceAmt_calc").val(data.fpriceAmt);
                $("#exerYield_calc").val(data.exerYield);
                $("#yield_calc").val(data.yield);
                saveCalcParam = getCalcParam();
            }
            });
        indexCount = 1;
       });
	/**
     * 画面加载完成
     */
    $(function() {
        //初始化
        table.load();
        //组合代码联动
        $('#portfolioId').change(function () {
        	//1. 组合代码点击时，净价回归默认
            var unitCprice =  $("#unitCprice").attr("value");
            if(unitCprice==undefined){
            	unitCprice="";
            }
            $("#unitCprice").val(unitCprice).trigger("change").trigger('blur');  
            portfolioIdChange(null);
        });
        //计算按钮
        /*$('#SecTransfer001_btn_calculate').change(function () {
        	amountChange();
        });*/
        //添加按钮
        $('#SecTransfer001_btn_add').click(function() {
            resoluteChange();
        });
        //待转仓组合查询链接
        $("#SecTransfer001_btn_searchInfo").click(function () {
            findAvaTransferPortfolioInfo();
        });
        // 保存按钮事件
        $('#SecTransfer001_btn_save').click(function() {
            saveOrSubmit('save');
        });
        // 提交按钮事件
        $('#SecTransfer001_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        //风险试算
        $('#SecTransfer001_btn_risk').click(function(){
            var data = table.getAllRows();
            if(data!=null && data != '') {
                //-----------add by jjpei 2020-01-07 风险新版本 start----------
                var object = $.serializeToObject('#secTransferForm', true);
                object.portfolioId = $("#investPortfolioId").val();
                //深拷贝
                var bussTrdTransfenpeiDto = JSON.parse(JSON.stringify(data));
                for(var i=0;i<bussTrdTransfenpeiDto.length;i++){
                	delete bussTrdTransfenpeiDto[i].portfolioId;
                	bussTrdTransfenpeiDto[i].ps = PS_T02;
                }
                object.bussTrdTransfenpeiDto = bussTrdTransfenpeiDto;
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfBatchData/1/RSK002/F01',object,getriskdata());

            } else {
                Joyin.error("拆分列表信息不能为空！");
            }
        });
    });
    //非编辑时不显示净价
//    if(pageModel==PAGE_MODEL_REFERENCE){
//    	setTimeout(function(){
//    		 $("#unitFprice").val("");
//    	},100);
//    }
}());