(function () {
    var operation;
    /**
	 * 回购期限
	 */
	var repoTermChangeAction = function(){
		var repoterm = $('#term').val();
		var re = /^[0-9]*$/;
        if(repoterm && re.test(repoterm)){
            var vdate = $('#vdate').val();
            if(vdate){
                //回购期限联动期限品种
            	$('#termTypeReback').val(getTermType(vdate,repoterm)).trigger('change');
            }
        }else{
        	$('#termTypeReback').val("").trigger('change');
        }
	};
	
    /**
     * 获取期限品种  date(yyyy-MM-dd),repoterm
     */
    function getTermType(date,repoterm) {
    	if(repoterm&&repoterm<=21){
    		//期限品种小于21天
    		if(repoterm==1){
    			return REPO_TYPE_R001;
    		}
    		if(repoterm>1&&repoterm<=7){
    			return REPO_TYPE_R007;
    		}
    		if(repoterm>7&&repoterm<=14){
    			return REPO_TYPE_R014;
    		}
    		if(repoterm>14&&repoterm<=21){
    			return REPO_TYPE_R021;
    		}
    	}
    	var day=0;
    	//date为空时  返回0
    	if(date&&repoterm){
    		//获取日期
    		date =DateUtil.parse(date);
    		//获取之后月00天 即本月最后一天
    		day= new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+1,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R1M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+2,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R2M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+3,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R3M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+4,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R4M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+5,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+6,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R6M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+7,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+8,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+9,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R9M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+10,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+11,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+12,0).getDate();
    		if(repoterm<=day){
    			return REPO_TYPE_R1Y;
    		}
    	}
    	return null;
    }

    var isSectId = false;
    //是否来自与Cstp
    var isFromCstp = false;
    // 委托指令dto
    var etrEntrust;
    // 委托指令附属表dtovpayDate
    var etrEntrustAdd;
    var fTradeIdFromCstp = fTradeId;
    // 委托金额
    var entrustAmt;
    // 委托的回购利率
    var entrustPrice;
    // 委托的回购起息日
    var entrustVdate;
    // 委托的回购到期日
    var entrustMdate;
    // 委托的回购期限From
    var entrustTermFrom;
    // 委托的回购期限To
    var entrustTermTo;
    // 委托的期限品种
    var entrustTermType;
    // add by wangguoxian 费用设置 20190807 start
    var isPayWithTrade = $("#isPayWithTrade").val()?$("#isPayWithTrade").val():YES_OR_NO_Y;//佣金是否随交易支付，默认为是
    $("#isPayWithTrade").val(isPayWithTrade);
    // add by wangguoxian 费用设置 20190807 end
    var getriskdata = function () {
        var obj = {
            timePoint: MY_SUP_TIME_TYPE_02,
            valDate: $('#tradeDate').val(),
            portfolioCode: $('#portfolioId').val(),
            tradeAmount: $('#prinAmt').val(),
            assetType: getFinProdType2FromPs(),
            tradeDirection:$('#ps').val(),
            isEnabled: '01'
        };
        if($('#ps').val() == BACK_LEND_DIRECTION_03){
            obj.assetType = FINPROD_TYPE_F02;
        }else if($('#ps').val() == BACK_LEND_DIRECTION_04){
            obj.assetType = FINPROD_TYPE_F03;
        }else{
            obj.assetType = null;
        }
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#trdInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {
            valDate: ['tradeDate', 'TrdRepoTrade002.tradeDate'],
            portfolioCode: ['portfolioId', 'TrdRepoTrade002.portfolioId'],
            tradeAmount: ['prinAmt', 'TrdRepoTrade002.prinAmt'],
            assetType: ['ps', 'TrdFundInvestTrade001.finprodId'],
            tradeDirection:['ps', 'TrdRepoTrade002.ps'],
            data: obj
        };
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'TrdInvestPersonInfo002Dto.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
    };

    /**
     * 获取查询参数
     */
    var getSearchParams = function () {
        return {};
    };

    /**
     * 画星星
     */
    function star() {
        // 加红星星
        // 查找label
        var _findPrevLabel = function (element, level) {
            var $el = element.prev();
            if (!$el.is('label') && level > 0) {
                $el = $el.find('label');
                if ($el.length === 0) {
                    return _findPrevLabel(element.parent(), level - 1);
                }
            }
            return $el;
        };

        // 非查看模式
        if (typeof pageModel !== 'undefined'
            && (pageModel === PAGE_MODEL_EDIT || pageModel === PAGE_MODEL_NEW)) {
            $('.must').each(function () {
                $(this).remove();
            });
            $('*[required]').each(function () {
                var $el = $(this);
                var $target = _findPrevLabel($el, 3);
                if ($target.find('> .must').length === 0) {
                    $target.append($('<i class="must">*</i>'));
                }
            });
        }
    }

    function saveOrSubmit(operateType) {
    	// 划款指令隐藏时，自动生成划款指令信息
    	generateOrders(function () {
	        var s;
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
	        // 检查生成划款指令参数是否发生变化
	        if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
	            return;
	        }

	        if($('#fTradeId').val()&&$('#prinAmt').val()&&(pageModel==PAGE_MODEL_NEW||pageModel==PAGE_MODEL_EDIT)){
	            var isTrue = false;
	            var params = {};
	            params.fTradeId = $('#fTradeId').val();
	            params.tradeAmt = $('#prinAmt').val();
	            params.tradeType = 'PLEDGE';
	            params.workflowId = $('#workflowId').val();
	            params.ps = $('#ps').val();
	            Joyin.ajaxJson(JoyinUtil.getRealPath('/invest/trade/trdrepotrade/TrdRepoTrade002/isAllTradeAmtByFTradeId'), params, function (result) {
	                if(result&&!result.data){
	                    isTrue = true;
	                    Joyin.error("成交编号获得的劵面总额不得大于已存在的劵面总额！");
	                }
	            },false);

	            if(isTrue){
	                return;
	            }
	        }
	        var object = $.serializeToObject('#submitForm', true);
	        object.pageModel = pageModel;
	        object.operateType = operateType;
	        object.isGuarantee = $('#portfolioId').select2('data')[0].object; // 是否保本
	        if (FileCommon) {
	            object.uploadFileList = FileCommon.getUploadFileInfo();
	        }
	        // 委托金额
	        object.entrustAmt = entrustAmt;
	        // 委托的回购利率
	        object.entrustPrice = entrustPrice;
	        // 委托的回购起息日
	        object.entrustVdate = entrustVdate;
	        // 委托的回购到期日
	        object.entrustMdate = entrustMdate;
	        // 委托的回购期限From
	        object.entrustTermFrom = entrustTermFrom;
	        // 委托的回购期限To
	        object.entrustTermTo = entrustTermTo;
	        // 委托的期限品种
	        object.entrustTermType = entrustTermType;
	        object.preTradeId = preTradeId;
            if(parentPageId){
                object.parentPageId = parentPageId;
            }
	        if(object.trdInvestPersonInfo && $("#fTradeId").val()){
	        	object.trdInvestPersonInfo.counterTrader = $('select#trdInvestPersonInfo_counterTrader').val();
	        }else{
	        	object.trdInvestPersonInfo.counterTrader = $('input#trdInvestPersonInfo_counterTrader').val();
	        }
//	        object.fTradeId = fTradeId;
	        // 资产可用持仓
	        object.assetPosition = $("#bussTrdAvailableCashQuery_assetPosition").val();
	        object.inputValue1 = PINAN_ASSET_TYPE_F07;
	        object.inputValue2 = $('#repoType').val();
	        object.inputValue3 = $('#ps').val();
	        if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
	            object.counterId=$('#trdInvestPersonInfo_counterId').select2('data')[0].id;
	        }
	        var obj = $.extend(true, {}, object);
	        obj.finprodType2 = getFinProdType2FromPs();
	        obj.remark=$('#repoType').select2('data')[0].id;
	        Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', obj, function(result1) {
	            Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/check', object, function (result) {
	            	//授信占用额度
//	                Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {});
	            	object.riskCreditListCommonDto = result1.data;
	            	Joyin.confirm(result.data, function () {
                        //质押券信息 面额万元改成元为单位
                        if(object.bussRepoSec){
                            if(object.bussRepoSec.totalPrinAmt){
                                object.bussRepoSec.totalPrinAmt = JoyinMath.doMul(object.bussRepoSec.totalPrinAmt,10000);
                            }
                            if(object.bussRepoSec.pledgeBondList&&obj.bussRepoSec.pledgeBondList.length>0){
                                $.each(object.bussRepoSec.pledgeBondList, function(index,item){
                                    item.avaPledgePrin = JoyinMath.doMul(item.avaPledgePrin,10000);
                                    item.pledgePrin = JoyinMath.doMul(item.pledgePrin,10000);
                                    item.prin = JoyinMath.doMul(item.prin,10000);
                                });
                            }
                        }
	                    if (operateType === 'save') {
	                        WorkFlow.doSaveList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/save', object, "TrdRepoTrade002_btn_save");
	                    } else {
	                        WorkFlow.doSubmitList(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/save', object, "TrdRepoTrade002_btn_submit");
	                    }
	                });
	            },false);
	        });
    	});
	}
    var checkAndAssign = function (el, value, editable) {
        if (value != null && value != '') {
            $(el).val(value).trigger('change').trigger('blur');
            if (!editable) {
                $(el).attr("disabled", true);
            }
        }
    };
    var checkNotEmpty = function () {
        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i] == null || arguments[i] == '') {
                return false;
            }
        }
        return true;
    };

    /**
     * 交易场所、回购类型、交易方向改变时
     */
    var repoTypeOnChange = function (isInit) {

        var repoType = $("#repoType").val();
        // var ps = $("#ps").val();
        //深交所 协议 期限品种非必填
        if($("#tradeMarket").val()==TRADE_PLACE_03&&repoType == TARGET_BACK_TYPE_H01){
        	$('#termTypeReback').removeAttr("required");
        	$("#termTypeReback").val("").prop("disabled", true);
        }else{
        	$("#termTypeReback").attr("required",true);
        	$("#termTypeReback").prop("disabled", false);
        }
        star();

        if (repoType == TARGET_BACK_TYPE_H02) {
        	$("#term").prop("disabled", true);
        }else{
        	$("#term").prop("disabled", false);
        }

        // 1) 当回购类型为质押式协议回购时，期限品种隐藏
        if (repoType == TARGET_BACK_TYPE_H01) {
            if (!isInit && !isTodoList) {
                $("#termTypeReback").val("");
            }
            $('#termTypeReback').prop('disabled', true);
            /*$("#termTypeReback").attr("data-url-hidden", $("#termTypeReback").attr("data-url")).bsSuggest("destroy");
            $("#termTypeReback").attr("data-url", "");
            $("#termTypeReback").parent().children(":gt(0)").remove();*/
            //select2-hidden-accessible
          //  $("#termTypeReback").addClass('select2-hidden-accessible');
            $("#term").prop("disabled", false);
            //$("#termTypeReback").unbind("change");
            if (etrEntrust && !etrEntrust.chlId) { // 待办事项不是委托，通道会清空，所以判断是：在委托没有通道的情况下
                $("#chlId").val('')
            }
            $("#chlId").trigger('change');
            if ($("#secAcctId").val()) {
                $("#chlId").prop("disabled", true);
            }
            else {
                $("#chlId").prop("disabled", false);
            }

        } else if (repoType == TARGET_BACK_TYPE_H02)
        // 买断式回购从回购资产交易画面移除了，本处不作判断
        // 2) 当回购类型为质押式竞价回购和买断式回购时，期限品种为文本框，文本框支持输入联想，期限品种枚举值为：
        {
            if (!isInit && !isTodoList && isTranRecord != 'Y') {
                //modify by mashukai ZGBSSE-185 审批后双击查看交易，回购期限不显示 20210429 start
                if (pageModel != PAGE_MODEL_REFERENCE){
                    $("#termTypeReback").val("");
                    $("#term").val("").trigger("change");    
                }
                //modify by mashukai ZGBSSE-185 审批后双击查看交易，回购期限不显示 20210429 start
            }
            /*$("#termTypeReback").attr("data-url", $("#termTypeReback").attr("data-url-hidden")).associateInput();
            $("#termTypeReback").attr("data-url-hidden", "");*/
           // $("#term").prop("disabled", true);
            $('#termTypeReback').prop('disabled', false);
            $("#termTypeReback").bind("change", function () {
                var selectedData = $('#termTypeReback').data("selected");
                if (selectedData) {
                    $("#term").val(selectedData["object"]).trigger("change");
                }
            });
            $("#chlId").val('').prop("disabled", true).trigger('change');
        }
        // 3) 当回购类型为质押式竞价回购，页面无质押券信息，可使用标准券额度显示，（此处需求有【交易方向为正回购时】的限制，经确认不需要）
        if (repoType == TARGET_BACK_TYPE_H02/*&&!$('#entrustId').val() 委托页面竞价回购无质押券信息，这个条件删除*/) {
            if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                // 质押券信息清除并隐藏
                window.BussRepoSec["bussRepoSec"].clearHideRepo();
            }
            // 当交易方向为正回购时
            if ($("#ps").val() == BACK_LEND_DIRECTION_03) {
            $("#avaStandardStockPositionDiv").show();
            }else{
            	$("#avaStandardStockPositionDiv").hide();
            }

            refreshAvaStandardStockPosition();
        } else {
            if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                // 质押券信息恢复显示
                window.BussRepoSec["bussRepoSec"].showRepo();
            }
            if (!isInit) {
                $("#avaStandardStockPosition").val("");
            }
            $("#avaStandardStockPositionDiv").hide();
        }
        if ((pageModel != PAGE_MODEL_REFERENCE && isTranRecord != 'Y') || ( isTranRecord == 'Y'&& $('#termTypeReback').val() !='' && $('#termTypeReback').val())) {//查看页面
            var termTypeReback = pageModel== PAGE_MODEL_NEW? $('#termTypeReback').val():termTypeRebackValue;
            $('#termTypeReback').tradeRepTypeChange({
                termType: 'termTypeReback',
                termTypeValue: termTypeReback,
                tradeMarket: $(tradeMarket).val(),
                tradeP02: TRADE_PLACE_02,
                tradeP03: TRADE_PLACE_03,
                tradeP01: TRADE_PLACE_01,
                repoType: $('#repoType').val(),
                repoTypeH02: TARGET_BACK_TYPE_H02,
                repoTypeH01: TARGET_BACK_TYPE_H01,
                repoTypeH04: TARGET_BACK_TYPE_H04
            });
        }
    };

    /**
     * 交易场所、回购类型、交易方向改变时
     */
    var tradeMarketOnChange = function (isInit) {
        var tradeMarket = $("#tradeMarket").val();
        //如果是质押式协议回购则隐藏期限品种
        if ($('#repoType').val() && $('#repoType').val()== TARGET_BACK_TYPE_H01) {
            $('#termTypeReback').prop('disabled', true);
        }else {
            $('#termTypeReback').prop('disabled', false);
        }
        // 上海证券交易所、深圳证券交易所时
        if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
            // 4) 清算速度只能为T+1，资金可用日显示；
            $("#actCapDays").prop("disabled", true);
            $("#settleSpeedStr").val(ACCOUNT_SPEED_01).trigger("change").prop("disabled", true);
            $("#avaFundsDateDiv").show();
            $("#vpayDateDiv").show();
            $("#mpayDateDiv").show();
            // TISHKS-253:场内质押式交易不展示资产可用持仓 fuyongsheng 20211116 edit start
            // //add by meifaxin 2021-9-25 银行间业务交易删除首期交收日、到期交收日、资产可用持仓  start
            // $(".div_position").show();
            // //add by meifaxin 2021-9-25 银行间业务交易删除首期交收日、到期交收日、资产可用持仓  end
            // TISHKS-253:场内质押式交易不展示资产可用持仓 fuyongsheng 20211116 edit end
            calcAvaFundsDate();
            if (!isInit) {
                $("#fTradeId").val("").trigger("change");
            }else{
                $("#fTradeId").prop("disabled", true);
            }
            if(!$("#fTradeId").val()){
                $("#fTradeId").prop("disabled", false);
            }
            // add by wangguoxian 费用设置 20190719 start
            $("#procedureAmtDiv").hide();
            $("#settleAmtDiv").hide();
            $("#commissionAmtDiv").show();
            // add by wangguoxian 费用设置 20190719 end
        }
        // 当交易场所是银行间时
        if (tradeMarket == TRADE_PLACE_01) {
            // 2) 清算速度默认为T+0，资金可用日隐藏；
        	$("#chlId").prop("disabled", false);
            $("#actCapDays").prop("disabled", true);
            if (pageModel!= PAGE_MODEL_REFERENCE) {
                $("#settleSpeedStr").val(ACCOUNT_SPEED_01).trigger("change").prop("disabled", false);
            }
            if (!isInit) {
                $("#avaFundsDate").val("");
            }
            $("#avaFundsDateDiv").hide();
            $("#vpayDateDiv").hide();
            $("#mpayDateDiv").hide();
            //add by meifaxin 2021-9-25 银行间业务交易删除首期交收日、到期交收日、资产可用持仓  start
            $(".div_position").hide();
            //add by meifaxin 2021-9-25 银行间业务交易删除首期交收日、到期交收日、资产可用持仓  end
            //交易日历默认为银行间
            if (pageModel==PAGE_MODEL_NEW) {
            	$("#calendarId").val('03').trigger("change");
            }

            // 3) 可以输入银行间成交编号，相关交易要素根据成交编号自动带出（取银行间下行数据）；
            if(!$("#fTradeId").val()){
                $("#fTradeId").prop("disabled", false);
            }
            // add by wangguoxian 费用设置 20190719 start
            $("#procedureAmtDiv").show();
            $("#settleAmtDiv").show();
            $("#commissionAmtDiv").hide();
            // add by wangguoxian 费用设置 20190719 end

            //add by nixiukuan 当交易场所选择银行间时候隐藏回购类型 20191025 start
           /* $("#repoTypeDiv").hide();*/
            //add by nixiukuan 当交易场所选择 20191025 end
        }
        /*else
        {
            //add by nixiukuan 当交易场所选择银行间时候隐藏回购类型 20191025 start
            $("#repoTypeDiv").show();
            //add by nixiukuan 当交易场所选择 20191025 end
        }*/
        if ((pageModel != PAGE_MODEL_REFERENCE && isTranRecord != 'Y') || ( isTranRecord == 'Y'&& $('#termTypeReback').val() !='' && $('#termTypeReback').val())) {
            $('#termTypeReback').tradeRepTypeChange({
                termType: 'termTypeReback',
                termTypeValue: $('#termTypeReback').val(),
                tradeMarket: tradeMarket,
                tradeP02: TRADE_PLACE_02,
                tradeP03: TRADE_PLACE_03,
                tradeP01: TRADE_PLACE_01,
                repoType: $('#repoType').val(),
                repoTypeH02: TARGET_BACK_TYPE_H02,
                repoTypeH01: TARGET_BACK_TYPE_H01,
                repoTypeH04: TARGET_BACK_TYPE_H04
            });
        }
        // add by wangguoxian 费用设置 20190719 start
        if(!tradeMarket){
            $("#procedureAmtDiv").hide();
            $("#settleAmtDiv").hide();
            $("#commissionAmtDiv").hide();
        }
        // add by wangguoxian 费用设置 20190719 end
    };

    //回购类型枚举值变化单独方法 add by wangwei5 start
    $.fn.updateChosenArr = function updateChosenArr(obj){
        var $this = $(this),arr = obj.dataResult || JSON.parse($this.attr('data-result') || '[]'),item = obj.item;
        $this.empty();
        $this.append($('<option value>'));
        for(var i in arr){
            if (!arr.hasOwnProperty(i))continue;
            if (obj.item && obj.item.length > 0){
                if ($.inArray(arr[i].id,item) >= 0)
                    $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this);
            } else
                $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this)
        }
        if (obj.chosen)
            $this.val(obj.chosen);
        if (obj.disabled === true || obj.disabled === false)
            $this.attr('disabled', obj.disabled);
        $this.trigger("chosen:updated").trigger('change');
        if (obj.callback)
            obj.callback($this);
        return $this;
    }
    //回购类型枚举值变化单独方法 add by wangwei5 end
    /**
     * 交易方向改变时
     */
    var psOnChange = function (isInit) {
        var ps = $("#ps").val();
        // 当交易方向是逆回购时
        if (ps == BACK_LEND_DIRECTION_04) {
            // 组合可用现金、托管户剩余现金显示，自动带出，不可修改，允许透支，计算规则见通用规则
            if(window&&window.BussTrdAvailableCashQuery&&window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]){
                window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].showCash();
            }
            refreshCashAndBalance();
        } else
        // 当交易方向为正回购时
        if (ps == BACK_LEND_DIRECTION_03) {
            // 组合可用现金隐藏
            if(window&&window.BussTrdAvailableCashQuery&&window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]){
                window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].hideCash();
            }

        }
    };


    /**
     * 本金金额、回购利率、回购期限、计息基础改变时，刷新回购利息
     */
    var refreshRebackIntAmt = function (varTerm) {
        if(isFromCstp&&preTradeId){
            return;
        }
        if(isFromCstp&&!isSubmit){
            return;
        }
        var prinAmt = $("#prinAmt").val();
        var rebackPrice = $("#rebackPrice").val();
        var actCapDays = $("#actCapDays").val();
        var basis = $("#basis").val();
        if (prinAmt && rebackPrice && actCapDays && basis) {
            var number = JoyinMath.doMul(prinAmt,rebackPrice,8);
            var num =  JoyinMath.doMul(JoyinMath.doDiv(number,100,8),actCapDays,8);
            var result = JoyinMath.doDiv(num,basis == BASIS_A_360 ? 360 : 365,2);
            $("#rebackIntAmt").val(result).trigger("change").trigger("blur");
        }
//        if (varTerm == 'term') {
//            var re = /^[0-9]*$/;
//            if(term && re.test(term)){
//                var vdate = $('#vdate').val();
//                if(vdate){
//                    var d1 = new Date(vdate).getTime();
//                    var day = parseInt(term*24*60*60*1000);
//                    var mdate = new Date(d1+day);
//                    if(!$('#mdate').val()){
//                    	if($("#tradeMarket").val() == TRADE_PLACE_01){
//                    		//银行间
//                    		$('#mdate').val(mdate.format("yyyy-MM-dd"));
//                    	}else{
//                    		var d2 = new Date($("#tradeDate").val()).getTime();
//                            $('#mdate').val((new Date(d2+day)).format("yyyy-MM-dd"));
//                    	}
//
//                    }
//                }
//            }
//        }
    };

    /**
     * 组合代码、证券托管户、交易日、到期日改变时，刷新可使用标准券额度
     */
    var refreshAvaStandardStockPosition = function () {
        if ($("#avaStandardStockPositionDiv").is(":visible")
            && $("#portfolioId").val()
            && $("#secAcctId").val()
            && $("#tradeDate").val()
            && $("#mdate").val()) {
            var params = {};
            params.portfolioId = $("#portfolioId").val();
            params.secAcctId = $("#secAcctId").val();
            params.tradeDate = $("#tradeDate").val();
            params.mdate = $("#mdate").val();
            $("#avaStandardStockPosition").val('');
            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getAvaStandardStockPosition',
                $.serializeToObject('#submitForm', true),
                function (data) {
                    if (data) {
                        checkAndAssign("#avaStandardStockPosition", data);
                    }
                    else {
                        $("#avaStandardStockPosition").val('0');
                    }
                });
        }
    };

    /**
     * 本金金额、回购利息改变时
     */
    var prinAmtRebackIntAmtOnChange = function () {
        var prinAmt = $("#prinAmt").val();
        var rebackIntAmt = $("#rebackIntAmt").val();
        if (prinAmt && rebackIntAmt) {
            $("#mdateSettleAmt").val((Number(prinAmt) + Number(rebackIntAmt)).toFixed(2)).trigger("blur");
        }
    };

    /**
     * 刷新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
     */
    var refreshCashAndBalance = function () {
        var params = {};
        params.portfolioId = $("#portfolioId").val();
        params.calendarId = $("#calendarId").val();
        params.ccy = $("#ccy").val();
        // 通道交易 新增 强成西 2019/03/26 start
        params.chlId = $("#chlId").val();
        // 通道交易 新增 强成西 2019/03/26 end
        params.tradeDate = $("#tradeDate").val();
        if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
            window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
                return params
            });
            params.acctType = ACCT_TYPE_A01;
            window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateSecAcctBalance(function () {
                return params
            });
        }
    };


// <!-- 通道交易 新增 强成西 2019/03/26 start -->
    $("#channelFee").on("change", function () {
        changeTradeAmtAndFee();
    });

    $("#prinAmt").on("change", function () {
        // add by wangguoxian 费用设置 20190807 start
        calFee();
        // add by wangguoxian 费用设置 20190807 end
        changeTradeAmtAndFee();
    });

    // modified by wanguoxian 费用设置 20190807 start
    var changeTradeAmtAndFee = function () {
        if (BACK_LEND_DIRECTION_04 == $("#ps").val()) {
            $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#prinAmt").val(), $("#channelFee").val(), 2)).trigger('blur');
            if (isPayWithTrade == YES_OR_NO_Y) {
                $("#tradeAmtAndFee").val(JoyinMath.doAdd($("#tradeAmtAndFee").val(), $("#commissionAmt").val(), 2)).trigger('blur');
            }
        }
        else {
            $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#prinAmt").val(), $("#channelFee").val(), 2)).trigger('blur');
            if (isPayWithTrade == YES_OR_NO_Y) {
                $("#tradeAmtAndFee").val(JoyinMath.doMinus($("#tradeAmtAndFee").val(), $("#commissionAmt").val(), 2)).trigger('blur');
            }
        }
    };
    // modified by wanguoxian 费用设置 20190807 start
    // <!-- 通道交易 新增 强成西 2019/03/26 end -->

    // add by wangguoxian 费用设置 20190719 start

    /**
     * 计算费用
     */
    var calFee = function(){
        if(!$("#tradeDate").val()){
            return;
        }

        if(!$("#chlId").val()){//如果通道为空，校验证券账户
            if(!$("#secAcctId").val()){
                return;
            }
        }

        // if(!$("#secAcctId").val()){
        //     return;
        // }
        if(!$("#ps").val()){
            return;
        }
        if(!$("#prinAmt").val()){
            return;
        }
        if($("#tradeMarket").val() == TRADE_PLACE_01 && !$("#termTypeReback").val()){
            return;
        }
        if (pageModel == PAGE_MODEL_REFERENCE || pageModel == PAGE_MODEL_EDIT){
            // 审批人审批或者生效的交易不再重新计算
            return;
        }

        var object = $.serializeToObject('#submitForm', true);
        Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/calFee', object, function (result) {
            if (result) {
                var tradeFeeList = result.tradeFeeList;
                if(tradeFeeList && tradeFeeList.length>0){
                    // 交易手续费条数
                    var cntP = 0;
                    // 结算费条数
                    var cntS = 0;
                    // 佣金条数
                    var cntC = 0;
                    $.each(tradeFeeList,function(i,v){
                        // 佣金设置
                        if(v.feeType==FEE_TYPE_05&&v.feeAmt){
                            cntC++;
                            isPayWithTrade = v.isPayWithTrade;
                            $("#isPayWithTrade").val(isPayWithTrade);
                            if(pageModel==PAGE_MODEL_NEW){
                                $("#commissionAmt").val(v.feeAmt).trigger("blur");
                            }
                            // 交易方向为逆回购并且随交易支付，交易金额 = 本金金额 + 随交易支付的费用
                            changeTradeAmtAndFee();
                        }
                        if(v.feeType==FEE_TYPE_06){
                            cntP++;
                            if (v.feeAmt) {
                                $("#procedureAmt").val(v.feeAmt).trigger("blur");
                            }
                            else {
                                $("#procedureAmt").val(0).trigger("blur");
                            }
                            // $("#tradeAmtAndFee").val($("#prinAmt").val()).trigger("blur");
                        }
                        else if(v.feeType==FEE_TYPE_08){
                            cntS++;
                            if (v.feeAmt) {
                                $("#settleAmt").val(v.feeAmt).trigger("blur");
                            }
                            else {
                                $("#settleAmt").val(0).trigger("blur");
                            }
                            // $("#tradeAmtAndFee").val($("#prinAmt").val()).trigger("blur");
                        }


                    });
                    // 按照最新费用规则计算相关费用无结果则清空相关费用
                    if(cntP == 0){
                        if(pageModel==PAGE_MODEL_NEW){
                            $("#procedureAmt").val("").trigger("blur");
                        }
                    }
                    if(cntS == 0){
                        if(pageModel==PAGE_MODEL_NEW){
                            $("#settleAmt").val("").trigger("blur");
                        }
                    }
                    if(cntC == 0){
                        if(pageModel==PAGE_MODEL_NEW) {
                            $("#commissionAmt").val("").trigger("blur");
                        }
                    }
                }else{
                    if(pageModel==PAGE_MODEL_NEW) {
                        $("#commissionAmt").val('').trigger("blur");
                        $("#procedureAmt").val('').trigger("blur");
                        $("#settleAmt").val('').trigger("blur");
                    }

                }
            }
        },false);
    };

    // add by wangguoxian 费用设置 20190719 end

    /**
     * 计算资金可用日
     */
    var calcAvaFundsDate = function () {
        var settleSpeedStr = $("#settleSpeedStr").val();
        if (settleSpeedStr == ACCOUNT_SPEED_02) {
            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getNextWorkDay', {
                'calendarId': $("#calendarId").val(),
                'tradeDate': $("#tradeDate").val()
            }, function (date) {
                $("#avaFundsDate").val(date).trigger("change");
            });
        }
    };

    /**
     * 设置添加质押券共通参数
     */
    var setBussRepoSecInput = function () {
        var data = {
            portfolioId: $('#portfolioId').val(),
            ps: $("#ps").val(),
            secAcctId: $('#secAcctId').val(),
            repoType: $('#repoType').val(),
            chlId: $('#chlId').val(),
            //add by 张年  2019/08/05 start
            tradeMarket:$('#tradeMarket').val(),
            //add by 张年  2019/08/05 end
            isFromCstp:isFromCstp
        };
        var object = {
            portfolioId: ['portfolioId', 'TrdRepoTrade002.portfolioId'],
            ps: ['ps', 'TrdRepoTrade002.ps'],
            secAcctId: ['secAcctId', 'TrdRepoTrade002.secAcctId'],
            chlId: ['chlId', 'TrdRepoTrade002.chlId'],
            repoType: ['repoType', 'TrdRepoTrade002.repoType'],
            //add by 张年  2019/08/05 start
            tradeMarket: ['tradeMarket', 'TrdRepoTrade002.tradeMarket'],
            //add by 张年  2019/08/05 end
            data: data
        };
        if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
            window.BussRepoSec["bussRepoSec"].setInputParam(object);
        }
    };

    // 委托进入场合，根据委托代码设置画面信息
    var initPageInfoFromEntrust = function () {
        /**
         * 若从委托场合点击交易录单
         */
        var entrustId = $("#entrustId").val();
        if (entrustId) {
            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getEntrustInfo', {'entrustId': entrustId},
                function (data) {
                    // 基本要素信息由投资委托带出，其中有：
                    // 委托代码、组合代码、回购类型、交易场所、交易方向、
                    // 清算速度、交易日历、期限品种、起息日、到期日、资金可用日、证券托管户，不可编辑
                    // 金融要素信息由投资委托带出，其中有：币种、本金金额、回购利率%
                    if (data && data.etrEntrust) {
                        etrEntrust = data.etrEntrust;
                        // 组合代码
                        checkAndAssign("#portfolioId", etrEntrust.portfolioId);
                        $("#portfolioId").textSelect('setValue', etrEntrust.portfolioId);
                        // 交易场所
                        checkAndAssign("#tradeMarket", etrEntrust.tradeMarket);
                        // 交易平台
                        checkAndAssign("#tradePlat", etrEntrust.tradePlat);
                        // 交易日历
                        checkAndAssign("#calendarId", etrEntrust.calendarId);
                        $("#calendarId").textSelect('setValue', etrEntrust.calendarId);
                        // 币种
                        checkAndAssign("#ccy", etrEntrust.ccy);
                        // 本金金额
                        if (!$("#prinAmt").val()) {
                            //委托金额大于cstp的劵面总额
                            //委托金额大于cstp的劵面总额
                            if (repoamt == null || etrEntrust.entrustAmt <= repoamt) {
                                checkAndAssign("#prinAmt", etrEntrust.entrustAmt, true); // 券面总额
                            }else{
                                checkAndAssign("#prinAmt",repoamt, true); // 券面总额
                            }
                        }
                        // 交易对手
                        checkAndAssign("#trdInvestPersonInfo_counterId", etrEntrust.counterId, true);
                        $("#trdInvestPersonInfo_counterId").textSelect('setValue', etrEntrust.counterId);
                        // 对手方交易员
                        checkAndAssign("#trdInvestPersonInfo_counterTrader", etrEntrust.counterTrader, true);
                        // 联系方式
                        checkAndAssign("#trdInvestPersonInfo_contactInfo", etrEntrust.contactInfo, true);
                        entrustAmt = etrEntrust.entrustAmt;
                        $("#chlId").textSelect('setValue', etrEntrust.chlId);
                    }
                    if (data != null) {
                        // 清算速度
                        checkAndAssign("#settleSpeedStr", data.settleSpeedStr);
                    }
                    if (data != null && data.etrEntrustAdd != null) {
                        etrEntrustAdd = data.etrEntrustAdd;
                        // 回购类型
                        checkAndAssign("#repoType", etrEntrustAdd.assetType1);
                        // 交易方向
                        checkAndAssign("#ps", etrEntrustAdd.busiPs);
                        // 期限品种
                        if (etrEntrustAdd.termType) {
                            $("#termTypeReback").html("<option value='"+etrEntrustAdd.termType+"'>" + etrEntrustAdd.termType +"</option>")
                            $("#termTypeReback").prop("disabled", true).val(etrEntrustAdd.termType).trigger("change")
                        }
                        if (etrEntrustAdd.vdate) {
                            var vdateYMD = Date.ofDate(etrEntrustAdd.vdate).format('yyyy-MM-dd');
                            // 起息日
                            checkAndAssign("#vdate", vdateYMD);
                            // 委托的回购起息日
                            entrustVdate = vdateYMD;
                        }
                        if (etrEntrustAdd.mdate) {
                            var mdateStr = Date.ofDate(etrEntrustAdd.mdate).format('yyyy-MM-dd');
                            // 到期日
                            checkAndAssign("#mdate", mdateStr);
                            // 委托的回购到期日
                            entrustMdate = etrEntrustAdd.mdate;
                        }
                        // 期限(天)
                        checkAndAssign("#term", etrEntrustAdd.termDays);
                        // 资金可用日
                        if (etrEntrustAdd.cashUsedate) {
                            checkAndAssign("#avaFundsDate", Date.ofDate(etrEntrustAdd.cashUsedate).format('yyyy-MM-dd'));
                        }
                        // 证券托管户
                        checkAndAssign("#secAcctId", etrEntrustAdd.secAcctId);
                        $("#secAcctId").textSelect('setValue', etrEntrustAdd.secAcctId);
                        $("#secAcctId").trigger("change");
                        // 回购利率%
                        if (!$("#rebackPrice").val()) {
                            checkAndAssign("#rebackPrice", JoyinMath.doMul(etrEntrust.price,100), true);
                        }
                        // 委托的回购利率
                        entrustPrice =JoyinMath.doMul(etrEntrust.price,100) ;
                        // 委托的回购期限From
                        entrustTermFrom = etrEntrustAdd.termDays;
                        // 委托的回购期限To
                        entrustTermTo = etrEntrustAdd.termDays2;
                        // 委托的期限品种
                        entrustTermType = etrEntrustAdd.termType;
                    }

                    if (data && data.etrEntrust && data.etrEntrust.chlId) {
                        $("#chlId").prop("disabled", true).trigger('change');
                    }
                });
        }
    };

    window.componentLoadCallback = function (isDone, pagePath, params) {
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
            var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                riskparam.data =null;
            }
            window.BaseRiskMathComm.getparam(riskparam);
            }
	        tradeMarketChangeCalendarId();
            window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                var dealObj = {};
                dealObj.finprodType2 = getFinProdType2FromPs();
                dealObj.settleDate = $("#vdate").val();
                dealObj.tradeDate = $("#tradeDate").val();
                dealObj.ps = PS_T01;
                dealObj.ccy = $("#ccy").val();
                dealObj.tradeAmt = $("#tradeAmtAndFee").val();
                dealObj.chlId = $("#chlId").val();
                dealObj.counterId = $("#trdInvestPersonInfo_counterId").val();
                dealObj.tradeMarket = $("#tradeMarket").val();
                dealObj.secManageAcctId = $("#secAcctId").val();
                dealObj.branch = $("#noBranchNoValue").val();
                return {
                    'isTotalTrade': YES_OR_NO_N
                    , 'bankLendingType': null
                    , 'portfolioId': $("#portfolioId").val()
                    , 'deal': dealObj
                    ,'repPageId': pageId
                };
            });
            if(window&&window.BussTrdAvailableCashQuery&&window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]){
                window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showCash();
            }
            if (entrustId != '') {
                if (etrEntrust) {
                    if(etrEntrust.counterId){
                        Joyin.ajaxJson(JoyinUtil.getRealPath('/component/trade/TrdInvestPersonInfo002/selectCounterId?'+"initValue="+etrEntrust.counterId), {initValue:etrEntrust.counterId}, function (result) {
                            if(result&&result.root&&result.root[0]){
                                etrEntrust.innerTrade= result.root[0].object
                            }
                        },false)
                    }
                    window.TrdInvestPersonInfo002['trdInvestPersonInfo'].setEntrust(function () {
                        return etrEntrust;
                    });
                }
            }
            // 隐藏资产可用持仓
            if(window&&window.BussTrdAvailableCashQuery&&window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]){
                window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].hidePosition();
            }

            // 初始化时，根据画面信息，获取当前最新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
            tradeMarketOnChange(true);
            repoTypeOnChange(true);
            psOnChange(true);
            if(fTradeIdFromCstp){
                $('#fTradeId').textSelect('setValue',fTradeIdFromCstp);
                $('#fTradeId').attr('disabled',true);
            }
            if(isCstp&&entrustId&&fTradeId){
                fTradeIdChangeAction(fTradeId);
            }
            if(isCstp&&preTradeId&&fTradeId){
                fTradeIdChangeAction(fTradeId);
            }
            // 委托进入场合，根据委托代码设置画面信息
            if (pageModel==PAGE_MODEL_NEW) {
                initPageInfoFromEntrust();
            }
            if (formTodoList) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
            }
            // 设置添加质押券共通参数
            setBussRepoSecInput();
            if(isSectId&&pageModel==PAGE_MODEL_NEW){
                $("#secAcctId").prop("disabled", true);
            }
           /* $('#trdInvestPersonInfo_counterId').on("change",function(){
                var counterId = $('#trdInvestPersonInfo_counterId');
                if (counterId.select2('data') && counterId.select2('data')[0]) {
                    var 交易对手 = counterId.select2('data')[0].object;
                    // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                    if (交易对手 == 'N') {
                        if (pageModel != PAGE_MODEL_REFERENCE) {
                            $('#settleSpeed').val(01).prop("disabled", true).trigger("change");
                        }
                    } else {
                        if (pageModel != PAGE_MODEL_REFERENCE) {
                            $('#settleSpeed').prop("disabled", false).trigger("change");
                        }
                    }
                }
            });*/
        }
    };

    /**
     * 根据交易方向，确定资产类型：金融产品类型_投管用_债券质押式正回购、金融产品类型_投管用_债券质押式逆回购
     *
     * @returns
     */
    function getFinProdType2FromPs() {
        return ($("#ps").val() == null || $("#ps").val() == "") ? "" : ($("#ps").val() == BACK_LEND_DIRECTION_03 ? FINPROD_TYPE_TG_F02 : FINPROD_TYPE_TG_F03);
    }
    var settleSpeedChangeAction = function(){
    	var settleSpeed = $('#settleSpeedStr').val();
    	var tradeDate = $('#tradeDate').val();
        if(pageModel != PAGE_MODEL_REFERENCE){//修复查看页面起息日显示不正确的问题
            if (settleSpeed && settleSpeed != '' && tradeDate && tradeDate != ''){
                var num = 0;
                if (settleSpeed === SETTLESPEED_1){
                    num = 1;
                }else if(settleSpeed === SETTLESPEED_2){
                    num = 2;
                }
                //
               Joyin.ajaxSync(basePath + 'invest/trade/bond/TrdBondInvestTrade002/getNextWorkDay', {
                     'calendarId': $("#calendarId").val(),
                      'tradeDate': tradeDate,
                      'settleSpeed': num
                }, function (date) {
                      $("#vdate").val(date).trigger('change');;
                 });
               // $('#vdate').val(DateUtil.parse(tradeDate).DateAdd('d', num).Format('yyyy-MM-dd')).trigger('change');
                vmdateOnChangeCalcDate($('#vdate').val(),$('#vpayDate'));
            }
        }

    };


    /**
     * 银行间起息日、回购期限改变时，自动计算到期日
     */
    var vmdateOnChangeCalcTerm = function () {
        if(isFromCstp){
            return;
        }
    	//todo  银行间 ！！！！
        var vdate = $("#vdate").val();
        var mdate = $("#mdate").val();
        var calendarId = $('#calendarId').val();
       var fTradeId =  $('#fTradeId').val();
        // if (vdate && mdate) {
        //     var days = (Date.ofDate(mdate) - Date.ofDate(vdate)) / (1000 * 60 * 60 * 24);
        //     $("#term").val(days);
        // }
        if (vdate  && calendarId&&!fTradeId) {
            var object={
                    vdate:vdate,
                    term:$("#term").val(),
                    calendarId:calendarId
            };
            if(pageModel != PAGE_MODEL_REFERENCE){
            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getMdate',object,
                    function (data) {
                        if (data && data.tradeDate && data.tradeDate) {
                        	$("#mdate").val(data.tradeDate).trigger('change');
                            vmdateOnChangeCalcDate($('#mdate').val(),$('#mpayDate'));
                            if($('#tradeMarket').val() == TRADE_PLACE_01){
                                if(data.cashholdingdays){
                                    $('#actCapDays').val(data.cashholdingdays).trigger('blur').trigger('change');
                                }
                            }
                        }
           });
        }
        }
    };

    /**
     * 起息日、到期日改变时，自动计算首次交收日、到期交收日、实际占款天数
     */
    var vmdateOnChangeCalcDate = function (date,dom) {
    	if(pageModel!=PAGE_MODEL_REFERENCE){
    		//8)	竞价回购时：首次交收日=起息日+1，遇节假日顺延；到期交收日=到期日+1，遇节假日顺延；协议回购时，首次交收日=起息日，到期交收日=到期日；
    		if($("#repoType").val()&&$("#repoType").val()==TARGET_BACK_TYPE_H01){
    			dom.val(date).trigger('change');
    			if($('#tradeMarket').val() != TRADE_PLACE_01){
                    caculateDays($("#vpayDate").val(),$("#mpayDate").val());
                }else{
                    caculateDays($("#vdate").val(),$("#mdate").val());
                }
    		}else {
    			var vdate = date;
                var term = '1';
                var calendarId = $('#calendarId').val();
                if (vdate  && calendarId) {
                    Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getMdate',
                            {vdate:vdate,term:term,calendarId:calendarId},
                            function (data) {
                                if (data && data.tradeDate && data.tradeDate) {
                                    dom.val(data.tradeDate).trigger('change');
                                    if($('#tradeMarket').val() != TRADE_PLACE_01){
                                        caculateDays($("#vpayDate").val(),$("#mpayDate").val());
                                    }else{
                                        caculateDays($("#vdate").val(),$("#mdate").val());
                                    }
                                }
                   });
                }
    		}
    	}

    };

    var caculateDays = function(vdate,mdate){
        var fTradeId =  $('#fTradeId').val();
        if(vdate && mdate&&!fTradeId){
            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getBetweenDays',
                    {vdate:vdate,mdate:mdate},
                    function (data) {
                        if(data){
                                $('#actCapDays').val(data).trigger('change');
                        }
                    });
        }
    };

    var vpayDateChange =function(){
    	if(pageModel!=PAGE_MODEL_REFERENCE){
    		if($('#tradeMarket').val() != TRADE_PLACE_01){
                var mpayDate = $('#mpayDate').val();
                var vpayDate = $('#vpayDate').val();
                var calendarId = $("#calendarId").val();
                if(vpayDate&&calendarId){
                    Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/isDealCalendarHoliday', {
                           calendarId: calendarId,
                           mpayDate: vpayDate
                        }, function (date) {
                            if(date){
                                Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getNextWorkDay', {
                                   'calendarId': calendarId,
                                   'tradeDate': vpayDate
                                }, function (date) {
                                   $("#vpayDate").val(date);
                                   if(mpayDate){
                                       caculateDays($("#vpayDate").val(),mpayDate);
                                   }
                                });
                            }
                            if(mpayDate){
                                caculateDays($("#vpayDate").val(),mpayDate);
                            }
                        },false);
                }
            }
    	}

    }

    var mpayDateChange = function(){
        if($('#tradeMarket').val() != TRADE_PLACE_01){
            var mpayDate = $('#mpayDate').val();
            var vpayDate = $('#vpayDate').val();
            var calendarId = $("#calendarId").val();
            if(calendarId && mpayDate){
            Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/isDealCalendarHoliday', {
                   calendarId: calendarId,
                   mpayDate: mpayDate
                }, function (date) {
                    if(date){
                        Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getNextWorkDay', {
                           'calendarId': calendarId,
                           'tradeDate': mpayDate
                        }, function (date) {
                           $("#mpayDate").val(date);
                           if(mpayDate){
                               caculateDays(vpayDate,$("#mpayDate").val());
                           }
                        }, false);
                    }
                    if(mpayDate){
                        caculateDays(vpayDate,$("#mpayDate").val());
                    }
                }, false);
            }
        }
    }
    var mdateChange =function(){
//        if($('#tradeMarket').val() != TRADE_PLACE_01){
            var mdate = $('#mdate').val();
            var vdate = $('#vdate').val();
            var calendarId = $("#calendarId").val();
            if(mdate&&calendarId){
                Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/isDealCalendarHoliday', {
                       calendarId: calendarId,
                       mpayDate: mdate
                    }, function (date) {
                        if(date){
                            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getNextWorkDay', {
                               'calendarId': calendarId,
                               'tradeDate': mdate
                            }, function (date) {
                                // TISHKS-810: modify by fuyongsheng 20220509 start
                                // $("#mdate").val(date).trigger('change');
                                $("#mdate").val(date);
                                // TISHKS-810: modify by fuyongsheng 20220509 end
                                if(vdate&&($('#tradeMarket').val() == TRADE_PLACE_01)){
                                   caculateDays($("#vdate").val(),$("#mdate").val());
                               }
                            });
                        }
//                        if(mdate){
//                            caculateDays($("#vdate").val(),mdate);
//                        }
                    },false);
            }
//        }
    }
    var vdateChange =function(){
//        if($('#tradeMarket').val() != TRADE_PLACE_01){
            var mdate = $('#mdate').val();
            var vdate = $('#vdate').val();
            var calendarId = $("#calendarId").val();
            if(vdate&&calendarId){
                Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/isDealCalendarHoliday', {
                       calendarId: calendarId,
                       mpayDate: vdate
                    }, function (date) {
                        if(date){
                            Joyin.ajaxSync(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/getNextWorkDay', {
                               'calendarId': calendarId,
                               'tradeDate': vdate
                            }, function (date) {
                                // TISHKS-810: modify by fuyongsheng 20220509 start
                               //$("#vdate").val(date).trigger('change');
                                $("#vdate").val(date);
                                // TISHKS-810: modify by fuyongsheng 20220509 end
                                vmdateOnChangeCalcDate($('#vdate').val(),$('#vpayDate'));
                               if(mdate&&($('#tradeMarket').val() == TRADE_PLACE_01)){
                                   caculateDays($("#vdate").val(),$("#mdate").val());
                               }
                            });
                        }
//                        if(mdate){
//                            caculateDays($("#vdate").val(),mdate);
//                        }
                    },false);
            }
//        }
    }
    /**
     * 交易场所联动交易日历
     */
    var tradeMarketChangeCalendarId = function(){
    	if($("#tradeMarket").val()){
    		if($("#tradeMarket").val()==TRADE_PLACE_01){
    			//银行间
    			$("#calendarId").textSelect('setValue', '03');
    			if(pageModel!=PAGE_MODEL_REFERENCE){
    			    // BS-1144银行间:回购类型为质押式回购  add by yujiaping 2020-11-25 start
                    $("#repoType").textSelect('setValue', TARGET_BACK_TYPE_H04);
                    $('#repoType').attr('disabled',false);
    				var $repoType = $('#repoType');
                     if($("#tradeMarket").val() == TRADE_PLACE_01){
                         $repoType.updateChosenArr({
                             item: [TARGET_BACK_TYPE_H04],
                             chosen: TARGET_BACK_TYPE_H04,
                             disabled: false
                         });
                     }
                     //BS-1144银行间:回购类型为质押式回购  add by yujiaping 2020-11-25 end
                    /*$('#repoType').val('H01').trigger('change');
                    $('#repoType').attr('disabled',true);*/
    			}
    		}else if($("#tradeMarket").val()==TRADE_PLACE_02){
    			//上交所
    			$("#calendarId").textSelect('setValue', '04');
    			//默认回购类型质押式竞价回购
            	if(pageModel!=PAGE_MODEL_REFERENCE){
                    // modify by jiangpeng on 20210511 ZGBSSE-347
                    // 【质押式协议回购】新建一笔质押式协议逆回购，点击保存按钮，在待办事项中查看到该笔交易的期限品种、质押券信息丢失
                    if($("#repoType").val()!=TARGET_BACK_TYPE_H01 &&!$('#entrustId').val()){
                        $("#repoType").textSelect('setValue', TARGET_BACK_TYPE_H02);
                        $('#repoType').attr('disabled',false);
                        //上交所:回购类型去掉质押式回购  add by yujiaping 2020-11-25 start
                        var $repoType = $('#repoType');
                        if($("#tradeMarket").val() == TRADE_PLACE_02){
                            $repoType.updateChosenArr({
                                item: [TARGET_BACK_TYPE_H01,TARGET_BACK_TYPE_H02],
                                chosen: TARGET_BACK_TYPE_H02,
                                disabled: false
                            });
                        }
                    }
                    //上交所:回购类型去掉质押式回购  add by yujiaping 2020-11-25 end
            	}
    		}else if($("#tradeMarket").val()==TRADE_PLACE_03){
    			//深交所
    			$("#calendarId").textSelect('setValue', '05');
    			//默认回购类型质押式竞价回购
            	if(pageModel!=PAGE_MODEL_REFERENCE && !$('#entrustId').val()){
            	$("#repoType").textSelect('setValue', TARGET_BACK_TYPE_H02);
                    $('#repoType').attr('disabled',false);
                    var $repoType = $('#repoType');
                    //BS-1144深圳证券交易所:回购类型放开质押式协议回购  add by yujiaping 2020-11-25 start
                   if($("#tradeMarket").val() == TRADE_PLACE_03){
                        $repoType.updateChosenArr({
                            item: [TARGET_BACK_TYPE_H01,TARGET_BACK_TYPE_H02],
                            chosen: TARGET_BACK_TYPE_H02,
                            disabled: false
                        });
                    }
                    //BS-1144深圳证券交易所:回购类型为质押式竞价回购 add by yujiaping 2020-11-25 end
            	}
    		}
    	}
    }

	/**
	 * 期限品种联动期限
	 */
	var termTypeChangeAction = function(){
		var termType = $('#termTypeReback').val();
//		if($("#repoType").val()&&$("#repoType").val()==TARGET_BACK_TYPE_H02){
			if ($('#termTypeReback').val()){
				if(termType == REPO_TYPE_R001 || termType == REPO_TYPE_R007 || termType == REPO_TYPE_R014 || termType == REPO_TYPE_R021 ||
						termType == REPO_TYPE_R1M || termType == REPO_TYPE_R2M || termType == REPO_TYPE_R3M ||
						termType == REPO_TYPE_R4M || termType == REPO_TYPE_R6M || termType == REPO_TYPE_R9M ||termType == REPO_TYPE_R1Y ){
					return;
				}
				var repoterm = '';
				if (termType == R001 || termType == GC001 || termType == R_001 || termType == RC001){
					repoterm = 1;
				}else if(termType == GC002 || termType == R_002){
				    repoterm = 2;
				}else if(termType == GC003 || termType == R_003 || termType == RC003){
	                repoterm = 3;
	            }else if(termType == GC004 || termType == R_004){
	                repoterm = 4;
	            }else if (termType == R007 || termType == GC007 || termType == R_007 || termType == RC007){
					repoterm = 7;
				}else if (termType == R014 || termType == GC014 || termType == R_014){
					repoterm = 14;
				}else if (termType == R021){
					repoterm = 21;
				}else if (termType == GC028 || termType == R_028){
	                repoterm = 28;
				}else if (termType == R1M){
					repoterm = 30;
				}else if (termType == R2M){
					repoterm = 60;
				}else if (termType == R3M){
					repoterm = 90;
				}else if (termType == GC091 || termType == R_091){
	                repoterm = 91;
				}else if (termType == R4M){
					repoterm = 120;
				}else if (termType == R6M){
					repoterm = 180;
				}else if (termType == GC182 || termType == R_182){
	                repoterm = 182;
				}
				/*$('#TrdOutRightRepoTrade002_mdate').attr('disabled',true);*/
				//YHZGCPB20200617004   银行间质押式正回购交易选择期限品种R007,修改回购期限为2天，点击保存按钮，在待办中双击查看此数据，回购期限变成了7天，导致到期结算金额错误 leipeng 20200619
                //if(!$('#workflowId').val()){}
                //ZGBSS-711     新建的质押式回购交易在复核岗被退回，修改期限品种时,到期日,首期交收日和到期交收日没有联动变化  wangyaning  20210302
				    if($('#term').val()!=repoterm){
				    $('#term').val(repoterm).trigger('change');
				    }
			}
//		}

	};
    /**
     * 根据 回购期限 获得 期限品种
     * @param date
     * @param term
     * @returns {string|null}
     */
    function getTermTypeReback(date,term) {
        if(term && term<=21){
            //期限品种小于21天
            if(term==1){
                return R001;
            }
            if(term>1&&term<=7){
                return R007;
            }
            if(term>7&&term<=14){
                return R014;
            }
            if(term>14&&term<=21){
                return R021;
            }
        }
        var day=0;
        //date为空时  返回0
        if(date&&term){
            //获取日期
            date =DateUtil.parse(date);
            //获取之后月00天 即本月最后一天
            day= new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+1,0).getDate();
            if(term<=day){
                return R1M;
            }
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+2,0).getDate();
            if(term<=day){
                return R2M;
            }
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+3,0).getDate();
            if(term<=day){
                return R3M;
            }
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+4,0).getDate();
            if(term<=day){
                return R4M;
            }
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+5,0).getDate();
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+6,0).getDate();
            if(term<=day){
                return R6M;
            }
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+7,0).getDate();
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+8,0).getDate();
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+9,0).getDate();
            if(term<=day){
                return R9M;
            }
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+10,0).getDate();
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+11,0).getDate();
            day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+12,0).getDate();
            if(term<=day){
                return R1Y;
            }
        }
        return null;
    }
    /** modified by qufnagxu at 2020-0806 start **/

	/**
	 * 起息日联动首付日
	 */
	var vpayDateChangeByvdate = function(){
		if(pageModel!=PAGE_MODEL_REFERENCE){
			$("#vpayDate").val($("#vdate").val()).trigger('change');
		}
	}
	/**
	 * 到期日联动到付日
	 */
	var mpayDateChangeBymdate = function(){
		var settleSpeed = $('#settleSpeedStr').val();
		var num =0;
		if (settleSpeed === SETTLESPEED_1){
			num = 1;
		}else if(settleSpeed === SETTLESPEED_2){
			num = 2;
		}
		 if($("#mdate").val()&&$("#term").val()){
			 var d1 = new Date($("#mdate").val()).getTime();
	         var day = parseInt(num*24*60*60*1000);
	         var mdate = new Date(d1+day);
	         $('#mpayDate').val(mdate.format("yyyy-MM-dd")).trigger('change');
		 }
	}
    var deleteSelect = function (id) {
        $('#'+id).val('').trigger('change');
    }
    /**
     * 银行间成交编号输入时从cstp查询数据带出
     */
    var fTradeIdChangeAction = function(fTradeId){
        if (fTradeId == null) {
            var fTradeId = $('#fTradeId').val();
        }
        //$('#repoType').attr('disabled', false);
        $('#ps').attr('disabled', false);
        $("#vdate").attr('disabled', false);
        $('#mdate').attr('disabled', false);
        $('#term').attr('disabled', false);
        /*$('#basis').attr('disabled', false);*/
        $('#ccy').attr('disabled', false);
        $('#prinAmt').attr('disabled', false);
        $('#rebackPrice').attr('disabled', false);
        $('#trdInvestPersonInfo_counterId').attr('disabled', false);

        if (fTradeId && fTradeId != ''){
            Joyin.ajaxJson(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/queryRepoCstpInfoByNo', {fedealno : fTradeId,entrustId:entrustId,preTradeId:preTradeId,isCstpSure:isCstpSure}, function(result){
                if (result){
                    isFromCstp = true;
                    //是否有报价
                	if(result.isOfferFlag && result.isOfferFlag != ''){
                		$('#isOfferFlag').val(result.isOfferFlag);
                	}else{
                		$('#isOfferFlag').val('');
                	}
                    if (result.dealdateStr && result.dealdateStr != ''){
                        $('#tradeDate').val(result.dealdateStr).trigger('change');// 交易日期
                        if($('#tradeDate').val()){
                            $('#tradeDate').attr('disabled', true);
                        }
                    }else{
                        $('#tradeDate').val('');
                        $('#tradeDate').attr('disabled', false);
                    }
                    //非cstp下行交易画面
                    if(isCstpSure!="true"){
                        if (result.entrustId && result.entrustId != ''){
                            $('#entrustId').val(result.entrustId);// 价格
                            if($('#entrustId').val()){
                                $('#entrustId').attr('disabled', true);
                            }
                        }else{
                            $('#entrustId').val('');
                        }
                        if (result.portfolioId && result.portfolioId != ''){
                            $('#portfolioId').textSelect('setValue', result.portfolioId);//组合代码
                            if($('#portfolioId').val()){
                                $('#portfolioId').attr('disabled', true);
                            }
                        }else{
                            $('#bussTrdAvailableCashQuery_avalibleCash').val('');
                            $('#bussTrdAvailableCashQuery_avalibleCashT1').val('');
                            deleteSelect('portfolioId');
                            $('#portfolioId').attr('disabled', false);
                        }
                    }
                    /*if (result.prodtype && result.prodtype != ''){
                        $('#repoType').val(result.prodtype).trigger('change');// 回购类型
                        if($('#repoType').val()){
                            $('#repoType').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('repoType');
                        $('#repoType').attr('disabled', false);
                    }*/
                    if(result.market){
                        $('#tradeMarket').val(result.market).trigger('change');//交易场所
                        if($('#tradeMarket').val()){
                            $('#tradeMarket').attr('disabled', true);
                        }

                        $('#calendarId').textSelect('setValue', result.calendarId);//交易日历
                        if($('#calendarId').val()){
                            $('#calendarId').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('tradeMarket');
                        deleteSelect('calendarId');
                        $('#tradeMarket').attr('disabled', false);
                        $('#calendarId').attr('disabled', false);
                    }
                    if (result.ourdepositaccountnumber && result.ourdepositaccountnumber != ''){
                        isSectId = true;
                        $('#secAcctId').textSelect('setValue', result.ourdepositaccountnumber);//证券账户
                        if($('#secAcctId').val()){
                            $("#secAcctId").trigger("change").prop("disabled", true);
                        }
                    }else{
                        deleteSelect('secAcctId');
                        deleteSelect('chlId');
                        $('#secAcctId').attr('disabled', false);
                        $('#chlId').attr('disabled', false);
                    }
                    if (result.ps && result.ps != ''){
                        $('#ps').val(result.ps).trigger('change').attr('disabled', true);// 交易方向
                        if($('#ps').val()){
                            $('#ps').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('ps');
                        $('#ps').attr('disabled', false);
                    }
                    if (result.vdateStr && result.vdateStr != ''){
                        $('#vdate').val(result.vdateStr).trigger('change');// 起息日
                        vmdateOnChangeCalcDate($('#vdate').val(),$('#vpayDate'));
                        if($('#vdate').val()){
                            $('#vdate').attr('disabled', true);
                        }
                    }else{
                        $('moneyAvaDate').val('');
                        $('vpayDate').val('');
                        $('#vdate').val('');
                        $('#vpayDate').val('');
                    }
                    if (result.mdateStr && result.mdateStr != ''){
                        $('#mdate').val(result.mdateStr).trigger('change');// 到期日
                        if($('#mdate').val()){
                            $('#mdate').attr('disabled', true);
                        }
                    }else{
                        $('#actCapDays').val('');
                        $('#avaFundsDate').val('');
                        $('#mpayDate').val('');
                        $('#mdate').val('');
                    }
                    if (result.term && result.term != ''){
                        $('#term').val(result.term).trigger('change');// 期限(天)
                        if($('#term').val()){
                            $('#term').attr('disabled', true);
                        }
                    }else{
                        $('#term').val('');
                        $('#term').attr('disabled', false);
                    }
                    if (result.basis && result.basis != ''){
                        $('#basis').val(result.basis).trigger('change');// 计息基础
                        if($('#basis').val()){
                            $('#basis').attr('disabled', true);
                        }
                    }
                    /*else{
                       deleteSelect('basis');
                        $('#basis').attr('disabled', false);
                    }*/
                    if (result.ccy && result.ccy != ''){
                        $('#ccy').val(result.ccy).trigger('change');//币种
                        if($('#ccy').val()){
                            $('#ccy').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('ccy');
                        $('#ccy').attr('disabled', false);
                    }
                    if (result.facetotalamt && result.facetotalamt != ''){
                        $('#prinAmt').val(result.facetotalamt).trigger('change');//本金金额
                        if( $('#prinAmt').val()){
                            $('#prinAmt').attr('disabled', true);
                        }
                    }else{
                        $('#prinAmt').val('');
                        $('#prinAmt').attr('disabled', false);
                    }
                    if (result.reporate && result.reporate != ''){
                        var number =   result.reporate*100;
                        $('#rebackPrice').val(number).trigger('change');//回购利率%
                        if($('#rebackPrice').val()){
                            $('#rebackPrice').attr('disabled', true);
                        }
                    }else{
                        $('#rebackPrice').val('');
                        $('#rebackPrice').attr('disabled', false);
                    }
                    if (result.repointamt && result.repointamt != ''){
                        $('#rebackIntAmt').val(result.repointamt).trigger('change');//回购利息
                        if($('#rebackIntAmt').val()){
                            $('#rebackIntAmt').attr('disabled', true);
                        }
                    }else{
                        $('#rebackIntAmt').val('');
                        $('#rebackIntAmt').attr('disabled', false);
                    }
                    if (result.repoamt && result.repoamt != ''){
                        $('#tradeAmtAndFee').val(result.repoamt).trigger('change');//回购金额
                        if($('#tradeAmtAndFee').val()){
                            $('#tradeAmtAndFee').attr('disabled', true);
                        }
                    }else{
                        $('#tradeAmtAndFee').val('');
                        $('#tradeAmtAndFee').attr('disabled', false);
                    }
                    if (result.mdealamt && result.mdealamt != ''){
                        $('#mdateSettleAmt').val(result.mdealamt).trigger('change');//到期结算金额
                        if($('#mdateSettleAmt').val()){
                            $('#mdateSettleAmt').attr('disabled', true);
                        }
                    }else{
                        $('#mdateSettleAmt').val('');
                        $('#tradeAmtAndFee').val('');
                        $('#mdateSettleAmt').attr('disabled', false);
                        $('#tradeAmtAndFee').attr('disabled', false);
                    }
                    setTimeout(function () {
                        //本方交易员
                        if (result.buycost && result.buycost != ''){
                            $('#trdInvestPersonInfo_trader').textSelect('setValue',result.buycost);
                        }else{
                            $('#trdInvestPersonInfo_trader').textSelect('setValue',userId);
                        }
                        if (result.seller && result.seller != ''){
                            $('#trdInvestPersonInfo_counterId').textSelect('setValue', result.seller);// 交易对手
                            if( $('#trdInvestPersonInfo_counterId').val()){
                                $('#trdInvestPersonInfo_counterId').attr('disabled', true);
                            }
                        }else{
                            deleteSelect('trdInvestPersonInfo_counterId');
                            $('#trdInvestPersonInfo_counterId').attr('disabled', false);
                        }
                        if (result.selltrader && result.selltrader != ''){
                            $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').textSelect('setValue',result.selltrader);// 对手方交易员
                            if($('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').val()){
                                $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').attr('disabled', true);
                            }
                        }else{
                            $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').val('');
                            $('#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader').attr('disabled', false);
                        }
                        if (result.countertelephone && result.countertelephone != ''){
                            $('#trdInvestPersonInfo_contactInfo').val(result.countertelephone);// 联系方式
                            if($('#trdInvestPersonInfo_contactInfo').val()){
                                $('#trdInvestPersonInfo_contactInfo').attr('disabled', true);
                            }
                        }else{
                            $('#trdInvestPersonInfo_contactInfo').val('');
                            $('#trdInvestPersonInfo_contactInfo').attr('disabled', false);
                        }
                    },1000);
                    if (result.facetotalamt && result.facetotalamt != ''){
                        $('#bussRepoSec_totalPrinAmt').val(result.facetotalamt/10000).trigger('blur');//汇总券面总额
                        if($('#bussRepoSec_totalPrinAmt').val()){
                            $('#bussRepoSec_totalPrinAmt').attr('disabled', true);
                        }
                    }else {
                        $('#bussRepoSec_totalPrinAmt').val('');
                        $('#bussRepoSec_totalPrinAmt').attr('disabled', false);
                    }
                    if (result.dealtext && result.dealtext != ''){
                        $('#remark').val(result.dealtext).trigger('change');// 备注
                    }else{
                        $('#remark').val('');
                    }
                    if (result.termType && result.termType != ''){
                        $('#repoType').val('H01');
                        $('#termTypeReback').val(result.termType).trigger('change');// 期限品种
                        if($('#termTypeReback').val()){
                            $('#termTypeReback').attr('disabled', true);
                        }
                    }else{
                        deleteSelect('termTypeReback');
                    }
                    if(result.settleSpeed && result.settleSpeed != ''){
                        $('#settleSpeedStr').val(result.settleSpeed).trigger('change');// 清算速度
                        if($('#settleSpeedStr').val()){
                            $('#settleSpeedStr').attr('disabled', true);
                        }
                    }else{
                        $('#settleSpeedStr').val('');
                        $('#settleSpeedStr').attr('disabled', false);
                    }
                    if(result.cashHoldingDays && result.cashHoldingDays != ''){
                        $('#actCapDays').val(result.cashHoldingDays).trigger('change');// 实际占款天数
                        if($('#actCapDays').val()){
                            $('#actCapDays').attr('disabled', true);
                        }
                    }else{
                        $('#actCapDays').val('');
                        $('#actCapDays').attr('disabled', false);
                    }
                    if(result.zzdPrinAmt && result.zzdPrinAmt != ''){
                        $('#zzdPrinAmt').val(result.zzdPrinAmt).trigger('change');// 中债登券面总额  zzdPrinAmt bussRepoSec_zzdTotalPrinAmt
                        $('#bussRepoSec_zzdTotalPrinAmt').val(result.zzdPrinAmt).trigger('change');
                        if($('#zzdPrinAmt').val()){
                            $('#zzdPrinAmt').attr('disabled', true);
                        }
                        if($('#bussRepoSec_zzdTotalPrinAmt').val()){
                            $('#bussRepoSec_zzdTotalPrinAmt').attr('disabled', true);
                        }
                    }else{
                        $('#zzdPrinAmt').val('');
                        $('#zzdPrinAmt').attr('disabled', false);
                        $('#bussRepoSec_zzdTotalPrinAmt').val('');
                        $('#bussRepoSec_zzdTotalPrinAmt').attr('disabled', false);
                    }
                    if(result.sqsPrinAmt && result.sqsPrinAmt != ''){
                        $('#sqsPrinAmt').val(result.sqsPrinAmt).trigger('change');// 上清所券面总额
                        $('#bussRepoSec_sqsTotalPrinAmt').val(result.sqsPrinAmt).trigger('change');
                        if($('#sqsPrinAmt').val()){
                            $('#sqsPrinAmt').attr('disabled', true);
                        }
                        if($('#bussRepoSec_sqsTotalPrinAmt').val()){
                            $('#bussRepoSec_sqsTotalPrinAmt').attr('disabled', true);
                        }
                    }else{
                        $('#sqsPrinAmt').val('');
                        $('#sqsPrinAmt').attr('disabled', false);
                        $('#bussRepoSec_sqsTotalPrinAmt').val('');
                        $('#bussRepoSec_sqsTotalPrinAmt').attr('disabled', false);
                    }
                    if (_.isArray(result.pledgeBondInfoDtoList)){
                        if (result.prodtype != TARGET_BACK_TYPE_H02) {
                            if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                                //质押券反显万元为单位
                                var obj = [];
                                $.each(result.pledgeBondInfoDtoList,function (index,item) {
                                    item.avaPledgePrin = item.avaPledgePrin/10000;
                                    item.pledgePrin = item.pledgePrin/10000;
                                    obj.push(item);
                                });
                                window.BussRepoSec["bussRepoSec"].reloadPledgeBond(obj);
                            }
                        }
                    }else{
                        $('#vpayDate').val('');
                        if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                            window.BussRepoSec["bussRepoSec"].reloadPledgeBond('');
                        }
                    }
                    $('#repoType').val(TARGET_BACK_TYPE_H04).trigger('change');
                    $('#repoType').attr('disabled',true);
                    //成交编号   通道置灰
                    $('#chlId').attr('disabled',true);
                    $('#bussRepoSec_btn_add').addClass('layui-btn-disabled').off();
                    $('#bussRepoSec_btn_delete').addClass('layui-btn-disabled').off();
                   /* setTimeout(function () {
                        isFromCstp = false;
                    },1000);*/
                }
            })
        }else{
            isFromCstp = false;
            $('#entrustId').val('');
            /*if ( $('#tradeDate').val()){
                $('#tradeDate').val($('#formatDate').val());
                $('#tradeDate').attr('disabled', true);
            }
            if ( $('#settleSpeedStr').val()){
                $('#settleSpeedStr').val('');
                $('#settleSpeedStr').attr('disabled', false);
            }
            // if ( $('#portfolioId').val()){
            //     $('#bussTrdAvailableCashQuery_avalibleCash').val('');
            //     $('#bussTrdAvailableCashQuery_avalibleCashT1').val('');
            //     deleteSelect('portfolioId');
            //     $('#portfolioId').attr('disabled', false);
            // }
            if (  $('#secAcctId').val()){
                deleteSelect('secAcctId');
                deleteSelect('chlId');
                $('#secAcctId').attr('disabled', false);
                $('#chlId').attr('disabled', false);
            }
            if ( $('#repoType').val()){
                deleteSelect('repoType');
                $('#repoType').attr('disabled', false);
            }
            if( $('#tradeMarket').val()){
                deleteSelect('tradeMarket');
                deleteSelect('calendarId');
                $('#tradeMarket').attr('disabled', false);
                $('#calendarId').attr('disabled', false);
            }

            if ( $('#ps').val()){
                deleteSelect('ps');
                $('#ps').attr('disabled', false);
            }
            if ( $('#vdate').val()){
                $('moneyAvaDate').val('');
                $('vpayDate').val('');
                $('#vdate').val('');
                $('#vpayDate').val('');
            }
            if ( $('#mdate').val()){
                $('#actCapDays').val('');
                $('#avaFundsDate').val('');
                $('#mpayDate').val('');
                $('#mdate').val('');
            }
            if ($('#term').val()){
                $('#term').val('');
                $('#term').attr('disabled', false);
            }
            if ( $('#basis').val()){
                /!*deleteSelect('basis');
                $('#basis').attr('disabled', false);*!/
                $('#basis').val(BASIS_A_365).trigger('change');
            }
            if ( $('#ccy').val()){
                deleteSelect('ccy');
                $('#ccy').attr('disabled', false);
            }
            if ( $('#prinAmt').val()){
                $('#prinAmt').val('');
                $('#prinAmt').attr('disabled', false);
            }
            if ( $('#rebackPrice').val()){
                $('#rebackPrice').val('');
                $('#rebackPrice').attr('disabled', false);
            }
            if ( $('#rebackIntAmt').val()){
                $('#rebackIntAmt').val('');
                $('#rebackIntAmt').attr('disabled', false);
            }
            if (  $('#mdateSettleAmt').val()){
                $('#mdateSettleAmt').val('');
                $('#tradeAmtAndFee').val('');
                $('#mdateSettleAmt').attr('disabled', false);
                $('#tradeAmtAndFee').attr('disabled', false);
            }
            if (  $('#trdInvestPersonInfo_counterId').val()){
                deleteSelect('trdInvestPersonInfo_counterId');
                $('#trdInvestPersonInfo_counterId').attr('disabled', false);
            }
            if ( $('#trdInvestPersonInfo_counterTrader').val()){
                $('#trdInvestPersonInfo_counterTrader').val('');
                $('#trdInvestPersonInfo_counterTrader').attr('disabled', false);
            }
            if ( $('#trdInvestPersonInfo_contactInfo').val()){
                $('#trdInvestPersonInfo_contactInfo').val('');
                $('#trdInvestPersonInfo_contactInfo').attr('disabled', false);
            }
            if ( $('#bussRepoSec_totalPrinAmt').val()){
                $('#bussRepoSec_totalPrinAmt').val('');
                $('#bussRepoSec_totalPrinAmt').attr('disabled', false);
            }
            if ( $('#remark').val()){
                $('#remark').val('');
            }
            if ($('#tradeMarket').val()){
                deleteSelect('settleSpeedStr');
                deleteSelect('tradeMarket');
                $('#settleSpeedStr').attr('disabled', false);
            }
            if ( $('#termTypeReback').val()){
                deleteSelect('termTypeReback');
            }
            if($('#settleSpeedStr').val()){
                deleteSelect('settleSpeedStr');
            }
            $('#vpayDate').val('');
            if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                window.BussRepoSec["bussRepoSec"].reloadPledgeBond('');
            }
            setTimeout(function () {
                $('#bussTrdAvailableCashQuery_avalibleCash').val('');
                $('#bussTrdAvailableCashQuery_avalibleCashT1').val('');
            },1500)*/

        }
    };
    //add by lizhi 2020-2-26 划款指令是否自动生成  start
    var generateOrders = function (callback) {
    	// 如果还款指令设置为隐藏，则自动生成划款指令
    	if(GEN_PAY_ORDERS_AUTO =='01'){
    		$('#bussTrdTransferOrder_btn_generate').click();
    	}
    	// 回调方法
	    if (callback && typeof callback === 'function') {
	        callback();
	    }
    }
    //add by lizhi 2020-2-26 划款指令是否自动生成  end

    /**
     * 验证日期是否正确
     * 日期格式：yyyy-mm-dd，yyyy-m-d，yyyy/mm/dd，yyyy/m/d
     */
    function checkDate(dateStr) {
        dateStr = dateStr.replace(/\//g, '-');
        var dateReg = /^(\d{4})-(\d{1,2})-(\d{1,2})$/;
        var rValue = dateStr.match(dateReg);
        if (rValue == null) {
            return false;
        }
        rValue[1] = parseInt(rValue[1], 10);
        rValue[2] = parseInt(rValue[2] - 1, 10);
        rValue[3] = parseInt(rValue[3], 10);
        var dateObj = new Date(rValue[1], rValue[2], rValue[3]);
        if (dateObj.getFullYear() != rValue[1] || dateObj.getMonth() != rValue[2] || dateObj.getDate() != rValue[3]) {
            return false;
        }
        return true;
    }

    /**
     * 画面加载完成
     */
    $(function () {
        //add by meifaxin 2021-9-25 银行间业务交易删除首期交收日、到期交收日、资产可用持仓  start
        if(parentPageId=='TrdRepoTradeIb001'){ //从银行间质押式回购交易菜单跳过去的时候
            $("#tradeMarket").textSelect('setValue', TRADE_PLACE_01);
        }
        //add by meifaxin 2021-9-25 银行间业务交易删除首期交收日、到期交收日、资产可用持仓  end
    	// 是否显示划款指令
        if(GEN_PAY_ORDERS_AUTO=='01'){
        	$('#bussTrdTransferOrder_div').hide() ;
        }else{
        	$('#bussTrdTransferOrder_div').show() ;
        }
        // 绑定按钮事件
        $('#TrdRepoTrade002_btn_save').click(function () {
            saveOrSubmit('save');
        });
        $('#TrdRepoTrade002_btn_submit').click(function () {
            saveOrSubmit('submit');
        });

        $('#repoType').on("change", function () {
            if(isFromCstp){
                return;
            }
                // 交易场所、回购类型、交易方向改变时
                repoTypeOnChange();
            if(operation != 'calendarId') {
                settleSpeedChangeAction();
            }
        });
        $('#ps').on("change", function () {
                // 交易场所、回购类型、交易方向改变时
                // tradeMarketRepoTypePsOnChange();
                // 交易方向改变时
                psOnChange();
                // 组合代码、证券托管户、交易日、到期日改变时，刷新可使用标准券额度
                refreshAvaStandardStockPosition();
                // 设置添加质押券共通参数
                setBussRepoSecInput();
                // add by wangguoxian 费用设置 20190807 start
                calFee();
                changeTradeAmtAndFee();
                // add by wangguoxian 费用设置 20190807 end
                // 交易场所、回购类型、交易方向改变时
                repoTypeOnChange();
        });
        $("#chlId").on("change", function () {
                if ($("#chlId").val()) {
                    $("#secAcctId").val('').trigger("change").prop("disabled", true);
                } else {
                    $("#secAcctId").prop("disabled", false);
                }
                refreshCashAndBalance();
                if(pageModel==PAGE_MODEL_NEW&&!$('#entrustId').val()){
                    //清空质押劵
                    //非成交编号  刷新
                    if(!isFromCstp){
                        if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                            if(window.BussRepoSec["bussRepoSec"].pledgeBondTotalTbl.table){
                                window.BussRepoSec["bussRepoSec"].pledgeBondTotalTbl.clear();
                            }
                        }
                    }
                }
                setBussRepoSecInput();
        });
        $('#vdate').on("change", function () {
            // 起息日、到期日改变时，自动计算回购期限
                vmdateOnChangeCalcTerm();
                /*vmdateOnChangeCalcDate($('#vdate').val(),$('#vpayDate'));*/
                vpayDateChangeByvdate();
                vdateChange();
                /*vpayDateChangeByvdate();*/
                // vdateChange();
        });
        $('#mdate').on("change", function () {
            // TISHKS-253:校验日期格式 fuyongsheng 20211105 add start
            if($('#mdate').val() && !checkDate($('#mdate').val())){
                Joyin.error(JoyinUtil.getLabel("TrdRepoTrade002.mdate")+ "格式不正确!");
                return;
            }
            // TISHKS-253:校验日期格式 fuyongsheng 20211105 add end
            // 起息日、到期日改变时，自动计算回购期限
            /*vmdateOnChangeCalcTerm();*/
            vmdateOnChangeCalcDate($('#mdate').val(), $('#mpayDate'));
            /*mpayDateChangeBymdate();*/
            // 组合代码、证券托管户、交易日、到期日改变时，刷新可使用标准券额度
            refreshAvaStandardStockPosition();
            mdateChange();
        });
        //实际占款天数刷新回购利息
        $('#actCapDays').on("change", function () {
                refreshRebackIntAmt();
        });
        $('#calendarId, #tradeMarket,#term').on("change", function () {
                vmdateOnChangeCalcTerm();
                /*vmdateOnChangeCalcDate($('#vdate').val(),$('#vpayDate'));*/
                /*vmdateOnChangeCalcDate($('#mdate').val(),$('#mpayDate'));*/
                /*vpayDateChangeByvdate();*/
        });
        // $('#tradeMarket').on("change", function () {
        //         tradeMarketChangeCalendarId();
        // });
        $('#tradeMarket').on("change", function () {
            if(isFromCstp){
                return;
            }
            //add by gaopeng 20191112 选择交易场所时清空证券账户  start
            $("#secAcctId").val('').trigger('change');
            //add by gaopeng 20191112 选择交易场所时清空证券账户  end
            tradeMarketChangeCalendarId();
            // 交易场所、回购类型、交易方向改变时
            tradeMarketOnChange(true);
            // add by wangguoxian 费用设置 20190807 start
            calFee();
            changeTradeAmtAndFee();
            // add by wangguoxian 费用设置 20190807 end
        });


        // add by zhanggenglin 首期交付日,到期交付日变动时 20190807 start
        $('#vpayDate').on("change", function(){
            // TISHKS-253:校验日期格式 fuyongsheng 20211105 add start
            if($('#vpayDate').val() && !checkDate($('#vpayDate').val())){
                Joyin.error(JoyinUtil.getLabel("TrdRepoTrade002.vpayDate")+ "格式不正确!");
                return;
            }
            // TISHKS-253:校验日期格式 fuyongsheng 20211105 add end
            vpayDateChange()
        });
        $('#mpayDate').on("change",function(){
            // TISHKS-253:校验日期格式 fuyongsheng 20211105 add start
            if($('#mpayDate').val() && !checkDate($('#mpayDate').val())){
                Joyin.error(JoyinUtil.getLabel("TrdRepoTrade002.mpayDate")+ "格式不正确!");
                return;
            }
            // TISHKS-253:校验日期格式 fuyongsheng 20211105 add end
            mpayDateChange()
        });
       // add by zhanggenglin 首期交付日,到期交付日变动时 20190807 end

        // 本金金额、回购利息改变时
        $('#prinAmt, #rebackIntAmt').on("change", function () {
                prinAmtRebackIntAmtOnChange();
        });
        // 手工改变佣金时
        $("#commissionAmt").on("keyup", function() {
                changeTradeAmtAndFee();
        });

        $('#calendarId, #ccy').on("change", function () {
            // 刷新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
                refreshCashAndBalance();
        });
        $('#portfolioId').on("change", function () {
                // 刷新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
                refreshCashAndBalance();
                // 组合代码、证券托管户、交易日、到期日改变时，刷新可使用标准券额度
                refreshAvaStandardStockPosition();
            if(pageModel==PAGE_MODEL_NEW&&!$('#entrustId').val()){
                //非成交编号  刷新
                if(!isFromCstp){
                    //清空质押劵
                    if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                        window.BussRepoSec["bussRepoSec"].pledgeBondTotalTbl.clear();
                    }
                }
            }
                // 设置添加质押券共通参数
                setBussRepoSecInput();
            if(pageModel== PAGE_MODEL_NEW && isTranRecord != 'Y'){
                $("#secAcctId").val('').trigger('change');
            }
        });
        // 刷新托管户剩余现金、组合T日可用现金、组合T+1日可用现金
        // $('#portfolioId, #calendarId, #ccy').on("change", function () {
        //     refreshCashAndBalance();
        // });
        // 本金金额、回购利率、计息基础改变时，刷新回购利息
        $('#prinAmt, #rebackPrice, #basis').on("change", function () {
                refreshRebackIntAmt();
        });

		// 回购期限
		$('#term').on('change',function(){
			if($('#tradeMarket').val() == TRADE_PLACE_01 || ($('#tradeMarket').val() == TRADE_PLACE_02  && $("#repoType").val() == TARGET_BACK_TYPE_H01)){
				repoTermChangeAction();
			}
		});

        $('#secAcctId').on("change", function () {
                var repoType = $("#repoType").val();
                var tradeMarket = $('#tradeMarket').val();
                if ($("#secAcctId").val()) {
                    $("#chlId").val('').prop("disabled", true);
                } else if ((!etrEntrust || !etrEntrust.chlId)
                		&& (repoType == TARGET_BACK_TYPE_H01 || (tradeMarket ==TRADE_PLACE_01 && repoType == TARGET_BACK_TYPE_H04))) {
                    $("#chlId").prop("disabled", false);
                }
                if ($('#secAcctId').val()) {
                    var data = $('#secAcctId').select2('data')[0];
                    $('#registOrg').val(data.object);
                } else {
                    $('#registOrg').val('');
                }
                // 组合代码、证券托管户、交易日、到期日改变时，刷新可使用标准券额度
                refreshAvaStandardStockPosition();
                if(pageModel==PAGE_MODEL_NEW&&!$('#entrustId').val()&&!preTradeId){
                    //非成交编号  刷新
                    if(!isFromCstp){
                        //清空质押劵
                        if (window && window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
                            window.BussRepoSec["bussRepoSec"].pledgeBondTotalTbl.clear();
                        }
                    }
                }
                setBussRepoSecInput();
                // add by wangguoxian 费用设置 20190807 start
                calFee();
                changeTradeAmtAndFee();
            // add by wangguoxian 费用设置 20190807 end
        });
        // TODO:交易所场合，如何根据成交编号，带出成交单上的信息，带出的信息不可修改；(等待接口开发后添加）
        // TODO:风险违规时校验处理（待共通完善后添加）
        // 风险试算
        $('#TrdRepoTrade002_btn_risk').click(function () {
            //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 start----------
            var object = $.serializeToObject('#submitForm', true);
            
         // add by etliu 20210129 start
            // 选择的成交编号存在对应的报价，则不影响风险
            if (object.fTradeId) {
                Joyin.ajaxSync(basePath + 'invest/trade/TrdCommonController/checkExecIdHasQuote', {'execId': object.fTradeId}, function (result) {
                    if (result == 'Y') {
                        object.isSkipRisk = 'Y';
                        object.isSkipRiskMsg = '选择的成交编号存在对应的报价，本次交易不影响风险';
                    }
                });
            }
         // add by etliu 20210129 end
            
             window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F07', object,getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 end----------
          // add by etliu 20210129 start
             if (object.isSkipRisk != 'Y') {
                 window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());
             }
          // add by etliu 20210129 end

        });

        // 清算速度
        $('#settleSpeedStr').change(function(){
                settleSpeedChangeAction();
		});

		$('#calendarId').on("change", function () {
           // 日历更新重新算 起息日
            settleSpeedChangeAction();
            operation = 'calendarId';
        });

		// add by wangguoxian 费用设置 20190807 start
        // 交易日，期限品种改变时
        $("#tradeDate").change(function(){
                calFee();
                changeTradeAmtAndFee();
        });
        // 期限品种改变时
        $("#termTypeReback").on("keyup blur",function(){
                calFee();
                changeTradeAmtAndFee();
                termTypeChangeAction();
        });
        
        $("#termTypeReback").on("change",function(){
                calFee();
                termTypeChangeAction();
        });

        // add by wangguoxian 费用设置 20190807 end


        // 银行间成交编号(当交易场所选择银行间时，银行间成交编号可编辑)
        $('#fTradeId').change(function(){
            if (pageModel==PAGE_MODEL_NEW&&!fTradeId){
                fTradeIdChangeAction(fTradeId);
                if($("#fTradeId").val()==''){
                    Joyin.ajax(basePath + 'invest/trade/trdrepotrade/TrdRepoTrade002/init', {entrustId:$('#entrustId').val(),portfolioId:$('#portfolioId').val()}, function (data) {
                        $("body").html(data);
                    });
                }
            }
        });
        var getDateByNow =  function (){
            var myDate = new Date;
            var year = myDate.getFullYear(); //获取当前年
            var mon = myDate.getMonth() + 1; //获取当前月
            var date = myDate.getDate();
            return year+'-'+mon+'-'+date;
        }

        //场内成交匹配录单时，触发一下期限品种联动期限的方法
        if(isTranRecord == 'Y') {

            if($('#termTypeReback').val() !='' && $('#termTypeReback').val()) {
                termTypeChangeAction();
            }

        }
        if(preTradeId){
            $('#baseRiskMathComm').hide();
            $('#TrdRepoTrade002_btn_risk').hide();
        }
        if(portfolioId){
            $('#portfolioId').attr('disabled',true).trigger('change');
        }
        window.calFee=calFee;
    });
}());
