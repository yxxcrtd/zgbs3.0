var table;

(function () {
    /**
     * 定义数据表格
     */
    var unitNum;
    var rateTable=$("#bondLendTable").joyintable({
        searchOnInit: true,
        checkbox:false,
        cols:[
            {field:'payDateUnadjust',title:'TrdBondLendInvestTrade002.initPayDate',width:400,type: 'date',sortable:false}
            ,{field:'intAmt',title:'TrdBondLendInvestTrade002.intAmt',width:400,sortable:false}
            ,{field:'intAmtInit',visible:false}
            ,{field:'newRate',width:400,title:'TrdBondLendInvestTrade002.rate',sortable:false}
        ],
        height:200,
        rowno:true,
        page:false,
        data:[]
    });

    var etrEntrust;
    var entrusrmYield;
    var entrustNum; // 委托时的券面总额
    var entrustPrice; // 委托时的净价
    var entrustYield; // 委托时的到期收益率
    var entrustExerYield; // 委托时的行权收益率
    //获取风险试算画面要素与Label提示文本
    var getriskdata = function () {
        var obj = {timePoint:MY_SUP_TIME_TYPE_02,valDate:$('#tradeDate').val(),portfolioCode:$('#portfolioId').val(),assetCode:$('#finprodId').val(),tradeAmount:$('#shareAmt').val()?$('#shareAmt').val()*unitNum:null,isEnabled:'01',tradeDirection:$('#ps').val()}
        if ($("#ps").val() == BACK_LEND_DIRECTION_05 ) {
            obj.assetType=FINPROD_TYPE_TG_F29;
        }
        if ($("#ps").val() == BACK_LEND_DIRECTION_06 ) {
            obj.assetType=FINPROD_TYPE_TG_F30;
        }
        var object = {valDate:['tradeDate','TrdBondLendInvestTrade002.tradeDate'],portfolioCode:['portfolioId','TrdBondLendInvestTrade002.portfolioId'],tradeAmount:['shareAmt','TrdBondLendInvestTrade002.tradeAmt'],tradeDirection:['ps','TrdBondLendInvestTrade002.ps'],data:obj}
        return object;
    }

    /**
     * 保存或提交逻辑
     * @param operateType
     */
    function saveOrSubmit(operateType) {
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
        var object = $.serializeToObject('#submitForm', true);
        object.pageModel = pageModel;
        object.operateType = operateType;
        if(FileCommon) {
            object.uploadFileList = FileCommon.getUploadFileInfo();
        }
        object.entrustNum = entrustNum; // 委托时的券面总额
        object.shareAmt = JoyinMath.doMul($('#shareAmt').val(),unitNum,2,JoyinMath.ROUND_HALF_UP);; // 券面总额装换
        object.entrustPrice = entrustPrice; // 委托时的借贷费率
        object.assetPosition = $("#bussTrdAvailableCashQuery_assetPosition").val(); // 资产可用持仓
        object.inputValue1 = PINAN_ASSET_TYPE_F12; 
        if ($('#trdInvestPersonInfo_counterId').select2('data') && $('#trdInvestPersonInfo_counterId').select2('data')[0]) {
            object.counterType = $('#trdInvestPersonInfo_counterId').select2('data')[0].object;
            object.counterId=$('#trdInvestPersonInfo_counterId').select2('data')[0].id;
        }
        
        //校验质押券置换安排是否为空
//        var exchangePledge = object.bussRepoSec.exchangePledge;
//        if(exchangePledge==undefined||exchangePledge==""){
//        	Joyin.error("质押劵置换安排不能为空！");
//        	return;
//        }
        
        Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/check', object, function(result1) {
            Joyin.ajaxJson(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/check', object, function(result) {
            	//授信占用额度
//            	Joyin.ajaxJson(basePath + 'invest/trade/TrdCommonController/delLinesDeal',result1.data,function(result2) {}); 
            	object.riskCreditListCommonDto = result1.data;
            	Joyin.confirm(result.data, function() {
                    if(operateType === 'save') {
                        WorkFlow.doSaveList(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/save', object, "TrdBondLendInvestTrade002_btn_save");
                    }
                    else {
                        WorkFlow.doSubmitList(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/save', object, "TrdBondLendInvestTrade002_btn_submit");
                    }
                });
          });
      }); 
    };
    /**
     * 获取收付款账号信息
     */
    var queryRecAndPayAccountInfo=function (proAccount) {
        if(null==$("#ccy").val()|| ''==$("#ccy").val()){
            return;
        }
        var object={};
        object.ccy=$("#ccy").val();
        if(proAccount){
            if(null==$('#portfolioId').val()|| ''==$("#portfolioId").val()){
                return;
            }
            object.portfolioId=$("#portfolioId").val();
            Joyin.ajax(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/getProAccountInfo', object, function(result) {
                if(result) {
                    checkAndAssign("#acctOwenAcctId", result[0].acctNo,true);
                    $('#acctOwenAcctId').textSelect('setValue', result[0].acctNo);
                    checkAndAssign("#acctOwnName", result[0].acctName);
                }
            });
        }else {
            if(null==$('#trdInvestPersonInfo_counterId').val()|| ''==$("#trdInvestPersonInfo_counterId").val()){
                return;
            }
            object.counterId=$('#trdInvestPersonInfo_counterId').val()
            Joyin.ajax(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/getCounterCapacctInfo', object, function(result) {
                if(result) {
                    checkAndAssign("#acctRivalId", result[0].acctNo,true);
                    $('#acctRivalId').textSelect('setValue', result[0].acctNo);
                    checkAndAssign("#acctRivalName", result[0].acctName);
                    checkAndAssign("#acctRivalNetWorkId", result[0].largePayno);
                }
            });
        }

    }
    /**
     * 回收款及对手方账号变化
     */
    var accountChangeAction=function (etl) {
        //币种
        var ccy=$("#ccy").val();
        //当前选择的账号
        var acctNo=$(etl).val();
        // 选择付款账户带出贷款账户名称
        Joyin.ajax(JoyinUtil.getRealPath('/component/trade/BussTrdTransferOrder/queryStlCapacctInfoByAcctNo'), {ccy : ccy, acctNo : acctNo}, function(result){
            //回收款账户
            if($(etl).attr("name")=='acctOwenAcctId'){
                checkAndAssign("#acctOwnName",result.acctName);
            }
            //对手方账号
            else{
                checkAndAssign("#acctRivalName",result.acctName);
                checkAndAssign("#acctRivalNetWorkId",result.largePayno);
            }
        })
    }
    var checkAndAssign = function(el, value, editable){
        if(value != null && value != ''){
            $(el).val(value).trigger('change').trigger('blur');
            if(!editable){
                $(el).attr("disabled",true);
            }
        }
    };
    var checkNotEmpty = function () {
        for(var i = 0; i < arguments.length; i++){
            if(arguments[i] == null || arguments[i] == ''){
                return false;
            }
        }
        return true;
    }

    /**
     * 清算速度
     */
    var settleSpeedChangeAction=function(){
        var settleSpeed = $('#settleSpeed').val();
        var tradeDate = $('#tradeDate').val();
        if (settleSpeed && settleSpeed != '' && tradeDate && tradeDate != ''){
            var num = 0;
            if (settleSpeed === ACCOUNT_SPEED_T1){
                num = 1;
            }
            $('#vdate').val(DateUtil.parse(tradeDate).DateAdd('d', num).Format('yyyy-MM-dd')).trigger('change');
            //联动处理到期结算日
            bondLendTermChangeAction();
            //联动处理实际占券天数
            vdateMdateChangeAction();
        }
    }

    /**
     * 期限(天)
     */
    var bondLendTermChangeAction = function(){
        //update by 张年  20190821 start
        calMdate();
        //update by 张年  20190821 end

        if($('#vdate').val()&&$("#term").val()){
        	 //回购期限联动期限品种
        	if(getTermType($('#vdate').val(),$("#term").val())){
        		$('#termType').val(getTermType($('#vdate').val(),$("#term").val())).trigger('change');
        		$('#termType').attr("disabled", true);
        	}else{
                $('#termType').val("").trigger('change');
        		$('#termType').attr("disabled", false);
        	}
	    }
        
        /*var bondLendTerm = $('#term').val();
        var re = /^[0-9]*$/;
        if(bondLendTerm && re.test(bondLendTerm)){
            var vdate = $('#vdate').val();//首次结算日
            var calendarId = $("#calendarId").val() //交易日历
            if(vdate){
                var d1 = new Date(vdate).getTime();
                var day = parseInt(bondLendTerm*24*60*60*1000);
                var mdate = new Date(d1+day);
                $('#mdate').val(mdate.format("yyyy-MM-dd")).change();
            }
        }*/
    }
    
    /**
     * 获取期限品种  date(yyyy-MM-dd),repoterm
     */
    function getTermType(date,repoterm) {
    	if(repoterm&&repoterm<=21){
    		//期限品种小于21天
    		if(repoterm==1){
    			return TERM_TYPE_BONDLEND_L001;
    		}
    		if(repoterm>1&&repoterm<=7){
    			return TERM_TYPE_BONDLEND_L007;
    		}
    		if(repoterm>7&&repoterm<=14){
    			return TERM_TYPE_BONDLEND_L014;
    		}
    		if(repoterm>14&&repoterm<=21){
    			return TERM_TYPE_BONDLEND_L021;
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
    			return TERM_TYPE_BONDLEND_L1M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+2,0).getDate();
    		if(repoterm<=day){
    			return TERM_TYPE_BONDLEND_L2M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+3,0).getDate();
    		if(repoterm<=day){
    			return TERM_TYPE_BONDLEND_L3M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+4,0).getDate();
    		if(repoterm<=day){
    			return TERM_TYPE_BONDLEND_L4M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+5,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+6,0).getDate();
    		if(repoterm<=day){
    			return TERM_TYPE_BONDLEND_L6M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+7,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+8,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+9,0).getDate();
    		if(repoterm<=day){
    			return TERM_TYPE_BONDLEND_L9M;
    		}
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+10,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+11,0).getDate();
    		day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+12,0).getDate();
    		if(repoterm<=day){
    			return TERM_TYPE_BONDLEND_L1Y;
    		}
    	}
    	return null;
    }

    //add by 张年  20190812 start
    //计算到期结算日
    var calMdate = function(){
        var bondLendTerm = $('#term').val();
        var re = /^[0-9]*$/;
        if(bondLendTerm && re.test(bondLendTerm)){
            var vdate = $('#vdate').val();//首次结算日
            var calendarId = $("#calendarId").val() //交易日历
            if(vdate&&calendarId){
                var d1 = new Date(vdate).getTime();
                var day = parseInt(bondLendTerm*24*60*60*1000);
                var mdate = new Date(d1+day);
                mdate=mdate.format("yyyy-MM-dd");
                Joyin.ajaxJson(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/isDealCalendarHoliday', {
                    calendarId: calendarId,
                    mpayDate: mdate
                 }, function (date) {
                     if(date){
		                Joyin.ajax(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/getNextWorkDay', {
		                    'calendarId': calendarId,
		                    'tradeDate': mdate
		                }, function (date) {
		                    $("#mdate").val(date).trigger("change");
		                });
                     }else{
                    	 $("#mdate").val(mdate).trigger('change');
                     }
                 },false);
            }else{
            	$("#mdate").val('').trigger('change');
            }
        }
    }
    //add by 张年  20190812  end


    /**
     * 首次结算日、到期结算日联动实际占券天数
     */
    var vdateMdateChangeAction = function(){
        var vdate = $('#vdate').val();
        var mdate = $('#mdate').val();
        if (vdate && mdate){
            var days = (new Date(mdate) - new Date(vdate))/(24*60*60*1000);
            $('#actAmtDays').val(days).trigger('change');
        }else{
            $('#actAmtDays').val('');
        }
    };
    /**
     * 画面联动处理
     * 1.1交易场所
     */
    $('#tradeMarket').on("change", function () {
        var tradeMarket = $('#tradeMarket').val();
        if (tradeMarket != null) {
            if (tradeMarket == TRADE_PLACE_01) {
                // 选择【银行间】时，
                // 1)交易平台默认为银行间且不可修改；
                checkAndAssign("#tradePlat", SHARES_TRADE_PLATFORM_01);
                // 2)需输入银行间成交编号，相关交易要素根据成交编号作为输入参数调用接口自动带出;
                $("#fTradeId").attr("disabled", false);
            } else if (tradeMarket == TRADE_PLACE_02 || tradeMarket == TRADE_PLACE_03) {
                // 选择【上海证券交易所】或【深圳证券交易所】时，
                // 1)交易平台可自主选择;
                $("#tradePlat").attr("disabled", false);
                // 3)无交易成交单 此项不可填
                $("#fTradeId").val("").trigger("change").attr("disabled", true);
            } else {
                // 1)交易平台可自主选择;
                // 1)交易平台默认为其它，可改
                checkAndAssign("#tradePlat", SHARES_TRADE_PLATFORM_99, true);
                $("#tradePlat").attr("disabled", false);
                // 3)无交易成交单 此项不可填
                $("#fTradeId").val("").trigger("change").attr("disabled", true);
            }
        }
    });

    /**
     * 1.3交易方向
     */
    var psChangeAction=function(){
        var ps = $("#ps").val();
        if (ps == BACK_LEND_DIRECTION_05 ) {
            $("#avaStandardBondPositionDiv").hide();
        }
        else if (ps == BACK_LEND_DIRECTION_06) {
            $("#avaStandardBondPositionDiv").show();
        }
        // 设置添加质押券共通参数
        setBussRepoSecInput();
    }
    /**
     *借贷费率或实际占券天数或券面总额发生变化联动费用
     */
    var reflashMtradeAmt=function () {
        if($('#shareAmt').val()&&$('#mYield').val()&&$('#actAmtDays').val()) {
            $('#mTradeAmt').val(JoyinMath.doDiv(JoyinMath.doMul($('#mYield').val(), $('#shareAmt').val()*100*$('#actAmtDays').val()),365,2));
        }
    }
    /**
     * 查询标的付息计划
     */
    var generationPlan=function(){
        if(null==$('#finprodId').val()||""==$('#finprodId').val()){
            return;
        }
        Joyin.ajax(JoyinUtil.getRealPath('/invest/trade/bondlend/TrdBondLendInvestTrade002/generationPlan'), {finprodId : $('#finprodId').val(), vdate : $('#finprodId').select2('data')[0].object.vdate}, function(result){
            if(result!=null){
                //反显列表
                var rs=rateTable.getAllRows();
                if (!rs) {
                    rs.splice(0,rs.length);
                }
                result.map(function(v){
                    v.intAmtInit = v.intAmt;
                    if($("#shareAmt").val()&&v.intAmt) {
                        v.intAmt = JoyinMath.doMul($("#shareAmt").val() * 100, v.intAmt);
                    }
                    else{
                        v.intAmt='';
                    }
                })

                rateTable.reload({data:result});
                /*result.map(function(v){
                    rateTable.addRow(v);
                })*/
            }
        })

}

    /**
     * 标的付息列表
     */
    var reflashRateTable=function(){
        var rs=rateTable.getAllRows();
        if (!rs) {
            rs.splice(0,rs.length);
        }
        rs.map(function(v){
            if($("#shareAmt").val()&&v.intAmtInit) {
                v.intAmt = JoyinMath.doMul($("#shareAmt").val() * 100, v.intAmtInit);
            }
            else{
                v.intAmt='';
            }
        })

        rateTable.reload({data:rs});
    }

    /**
     * 1.5资产代码选择时
     * 资产名称文本框根据资产代码选择自动显示对应名称;
     * 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;
     */
    $('#finprodId').on('change', function () {
        var finprodId = $('#finprodId').val();
        var data = $('#finprodId').select2('data')[0].object;
        if($('#finprodId').select2('data')[0].id){
        	if (data && data.finprodId) {// 资产名称文本框根据资产代码选择自动显示对应名称;
                $("#finprodName").val(data.finprodName).trigger('change');
                // 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;

                $("#ccy").val(data.ccy).trigger('change');
                //$("#calendarId").val(data.calendarId).trigger('change');
                reloadCashAndPosition();
            } else {
	        	//自动加券 yjm
	        	var autoCoupon = AutoCoupon.autoFundTradeCoupon($('#finprodId').select2('data')[0].text);
	            // 资产名称文本框根据资产代码选择自动显示对应名称;
	            $("#finprodName").val(autoCoupon.finprodName).trigger('change');

	            $("#ccy").val(autoCoupon.ccy).trigger('change');
	           //$("#calendarId").val(autoCoupon.calendarId).trigger('change');
			}
            reloadCashAndPosition();
        	//查询付息计划
            generationPlan();

        } else {
            // 资产名称文本框根据资产代码选择自动显示对应名称;
            $("#finprodName").val("").trigger('change');
            // 同时带出资产相关信息，包括起息日，到期日，发行日，交易场所，币种，交易日历，计息基础等;
            $("#vdate").val("").trigger('change');
            $("#mdate").val("").trigger('change');
            $("#idate").val("").trigger('change');
            $("#ccy").val("").trigger('change');
            $("#calendarId").val("").trigger('change');
            $("#basis").val("").trigger('change');
            settleSpeedChangeAction();//调用清算速度，重新赋值

        }
    });
    /**
     * 资产代码查看
     * 若点击资产代码后面的【查看】按钮跳转到本资产的详情画面
     */
    $('#TrdBondLendInvestTrade002_btn_viewAsset').click(function(){
        var url = "home.html?a=a&finprodMarketId=" + $('#finprodId').val() + "&pageModel=1&editable=false&isTodoList=true#/approve/subjectbond?a=a"; // 默认债券标的
        var param = {
            pageModel: PAGE_MODEL_REFERENCE,
            isQuickInput: true,
            finprodId: $('#finprodId').val()
        };
        Joyin.dialog(JoyinUtil.getLabel("SysCommon.refrence"),
            basePath + url,
            "98%", "98%",
            param);
        // if ($('#finprodId').val()) {
        //     Joyin.dialog(JoyinUtil.getLabel("FinBondTargetAsset002.title"), basePath
        //             + "invest/asset/bond/FinBondTargetAsset002/init", "95%", "90%", {
        //         pageModel : PAGE_MODEL_REFERENCE,
        //         finprodId : $('#finprodId').val()
        //     });
        // }
    });

    /**
     * 组合代码变化更新资产可用持仓
     */
    $("#portfolioId").on("change", function (e, isInit) {
        //刷新资产持仓
        reloadCashAndPosition();
        // 设置添加质押券共通参数
        setBussRepoSecInput();
        //更新回收款账户
        queryRecAndPayAccountInfo(true);
        // 债券投资 改变组合 需联动 证券账户 ,证券托管户应清空选择项
        if (!isInit && pageModel !=PAGE_MODEL_REFERENCE&&pageModel !=PAGE_MODEL_EDIT) {
            $("#secManageAcctId").val('').trigger("change");
        }
    });

    /**
     * 设置添加质押券共通参数
     */
    var setBussRepoSecInput = function () {
        var data = {
            portfolioId: $('#portfolioId').val(),
            ps: $("#ps").val(),
            secAcctId: $('#secManageAcctId').val(),
            chlId: ''
        }
        var object = {
            portfolioId: ['portfolioId', 'TrdBondLendInvestTrade002.portfolioId'],
            ps: ['ps', 'TrdBondLendInvestTrade002.ps'],
            secAcctId: ['secManageAcctId', 'TrdBondLendInvestTrade002.secManageAcctId'],
            data: data
        }
        if (window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
            window.BussRepoSec["bussRepoSec"].setInputParam(object);
        }
    };

    /**
     * 刷新资产可用持仓
     */
    var reloadCashAndPosition = function () {
        var params = {};
        params.portfolioId = $("#portfolioId").val();
        params.calendarId = $("#calendarId").val();
        params.ccy = $("#ccy").val();
        params.tradeDate = $("#tradeDate").val();
        params.finprodId = $("#finprodId").val();
        params.secAcctId = $("#secManageAcctId").val();
        // 债券借贷资产可用持仓乘100by赵东东 2019.8.30 start
        params.multiParam=100;
        // 债券借贷资产可用持仓乘100by赵东东 2019.8.30 end
        if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
            window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
                return params
            });
        }
    };

    //画面初始化
    var init = function() {
        
        /**
         * 若从委托场合点击交易录单
         */
        //配置单位 edit by yeming 20210726 start
        if(entrustAmtUnit == '01' ){
            $(".unitNum").text('元');
            unitNum = 1;
        }else{
            unitNum = 10000;
        }
        //配置单位 edit by yeming 20210726 start
        var entrustId = $("#entrustId").val();
        if(entrustId != ''){
            Joyin.ajaxSync(basePath + 'invest/trade/bondlend/TrdBondLendInvestTrade002/getEntrustInfo', {'entrustId':entrustId},function (data) {
                // 基本要素信息由投资委托带出，其中有：委托代码、组合代码、资产代码、清算速度、借贷方向、交易场所,交易日历等，不可编辑
                // 金融要素信息由投资委托带出，其中有：币种、券面总额、借贷费率等
                if(data != null && data.etrEntrust != null){
                    etrEntrust = data.etrEntrust;
                    checkAndAssign("#portfolioId", etrEntrust.portfolioId); // 组合代码、
                    $('#portfolioId').textSelect('setValue', etrEntrust.portfolioId);
                    checkAndAssign("#finprodId", etrEntrust.finprodId);
                    $('#finprodId').textSelect('setValue', etrEntrust.finprodId); // 资产代码
                    // 资产名称文本框根据资产代码选择自动显示对应名称;
                    $("#finprodName").val(data.finprodName).trigger('change');
                    
                    checkAndAssign("#tradeMarket", etrEntrust.tradeMarket); // 交易场所
                    checkAndAssign("#calendarId", etrEntrust.calendarId); // 交易日历
                    $('#calendarId').textSelect('setValue', etrEntrust.calendarId); // 交易日历
                    checkAndAssign("#ps", etrEntrust.ps); // 借贷方向
                    checkAndAssign("#ccy", etrEntrust.ccy); // 币种
                    if ($("#shareAmt").val()) {
                    } else {
                        checkAndAssign("#shareAmt", etrEntrust.entrustAmt, true); // 券面总额
                    }
                    if ($("#mYield").val()) {
                    } else {
                        checkAndAssign("#mYield", etrEntrust.price, true); // 借贷费率
                        entrusrmYield = etrEntrust.price;
                    }

                    entrustNum = etrEntrust.entrustAmt;
                    entrustPrice = etrEntrust.price;
                }
                if(data != null && data.etrEntrustAdd != null){
                    var etrEntrustAdd = data.etrEntrustAdd;
                    checkAndAssign("#termType", etrEntrustAdd.termType); // 期限品种
                    checkAndAssign("#secManageAcctId", etrEntrustAdd.secAcctId); // 证券代码
                    $('#secManageAcctId').textSelect('setValue', etrEntrustAdd.secAcctId); // 证券代码

                }
                if (data != null) {
                    checkAndAssign("#settleSpeed", data.settleSpeedStr); // 清算速度
                    // 计算首次结算日
                    settleSpeedChangeAction();
                }
            });
        };
        //根据交易方向
        psChangeAction();
        //组合代码联动更新回收款账户
        queryRecAndPayAccountInfo(true);
        //交易对手代码联动更新对手方账户
        queryRecAndPayAccountInfo(false);
        //资产代码联动标的付息计划

        //首次结算方式默认券券对付
        if(!$('#deliveryType').val()){
        	$('#deliveryType').textSelect('setValue', SETTLEMENT_WAY_LENDING_QQDF);
        }
        
        //质押券置换安排添加必填标志        
        var markInterval = setInterval(function(){
        	addMustMark();
        },50);

        var addMustMark = function () {
        	var obj = $("#isBondLendTrade :first-child");
       			if(obj.length>0){
       				obj = $(obj[0]);
       				var html = obj.html()+'<i class="must">*</i>';
       				obj.html(html);
       				clearInterval(markInterval);
       			}
        };
        // “对手方账号名称”、“对手方账号开户网点” 字段初始化
        if ($("#acctRivalId").val !== null && $("#acctRivalId").val !== '') {
            accountChangeAction($("#acctRivalId"));
        }
    };
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            if(typeof(BaseRiskMathComm) != 'undefined') {
            var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                riskparam.data =null;
            }
            window.BaseRiskMathComm.getparam(riskparam);
            }
            window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showPosition(true);//只展示持仓

            if(entrustId != ''){
                if(etrEntrust){
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
            // 交易对手
            $("select[name='trdInvestPersonInfo.counterId']").change(function () {
                //更新对手方账户
                queryRecAndPayAccountInfo(false);
            }).trigger('change');
            if($('#finprodId').val()){
                $('#finprodId').trigger('change');
            }
            // 设置添加质押券共通参数
            setBussRepoSecInput();
        }
    };

    /**
     * 画面加载完成
     */
    $(function() {
        //初始化画面
        init(); 
        // 绑定按钮事件
        $('#TrdBondLendInvestTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondLendInvestTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

        /**
         * 清算速度、首次结算日之间有联动关系
         */
        $("#settleSpeed").on("change", function () {
            settleSpeedChangeAction();
        });

        // 期限(天)
        $('#term').change(function(){
            bondLendTermChangeAction();
        });

        //add by 张年 20190821 start
        //交易日历
        $('#calendarId').change(function(){
            bondLendTermChangeAction();
        });
        //add by 张年 20190821 end
        // 交易方向改变联动
        $('#ps').change(function(){
            psChangeAction();
        });

        // 画面首次结算日联动实际占款天数
        $('#vdate').change(function(){
            vdateMdateChangeAction();
        });

        // 画面到期结算日联动实际占款天数
        $('#mdate').change(function(){
            vdateMdateChangeAction();
        });

        // 币种,交易日历刷新资产持仓
        $('#ccy,#calendarId').on("change", function () {
        	
            reloadCashAndPosition();
        });

        /**
         * 证券托管户变化更新资产可用持仓
         */
        $("#secManageAcctId").on("change", function () {
            //刷新资产持仓
            reloadCashAndPosition();
            // 设置添加质押券共通参数
            setBussRepoSecInput();
        });

        // 券面总额
        $('#shareAmt,#actAmtDays,#mYield').blur(function(){
            reflashMtradeAmt();
            if($(this).attr("name")=='shareAmt'){
                reflashRateTable();//券面总额变动刷新标的付息列表
            }
        });
        $('#actAmtDays').change(function(){
            reflashMtradeAmt();
        });

        $('#mYield').change(function () {
            if(entrustId){
                if($('#ps').val()=='05'){//融入
                    if(Number($(this).val())>Number(entrusrmYield)){
                        $(this).val(entrusrmYield);
                        Joyin.warn('融入时借贷费率不能大于委托的借贷费率')
                    }
                }else{
                    if(Number($(this).val())<Number(entrusrmYield)){
                        $(this).val(entrusrmYield);
                        Joyin.warn('融入时借贷费率不能小于委托的借贷费率')
                    }
                }
            }
        });

        // 回收款与对手方账号
        $('#acctRivalId,#acctOwenAcctId').on("change", function () {
            accountChangeAction($(this));
        });

       //风险试算
        $('#TrdBondLendInvestTrade002_btn_risk').click(function(){
            //window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 start----------
            var object = $.serializeToObject('#submitForm', true);
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/1/RSK002/F12', object,getriskdata());
            //-----------add by jjpei 2020-01-07 风险新版本 end----------
            window.BaseRiskMathComm.initBaseRiskMathCommPageCus(getriskdata());

        })
    });
    
	/**
     * 参数是json字符串的ajax请求，必须是Post请求
     */
    var ajaxJsonSync = function (url, param, callback) {
        var elementIdForLog = Joyin.getElementId(url);
        if (url && url.indexOf("pageIdForLog") == -1) {
            if (url.indexOf("?") != -1) {
                if (pageId) {
                    url += "&pageIdForLog=" + pageId + "&elementIdForLog=" + elementIdForLog;
                }

            } else {
                if (pageId) {
                    url += "?pageIdForLog=" + pageId + "&elementIdForLog=" + elementIdForLog;
                }
            }
        }
        // 是否为待办事项标识
        if (param) {
            url += (url.indexOf("?") != -1 ? '&' : '?') + 'isTodoList=' + isTodoList;
        }
        param = JSON.stringify(param);
        if(url && url.indexOf(basePath) != 0 && url.indexOf('/') == 0) {
            url = JoyinUtil.getRealPath(url);
        }
        // “保存”、“提交”操作时，记录画面修改值
        if(elementIdForLog && (elementIdForLog.indexOf('_submit') != -1 || elementIdForLog.indexOf('_save') != -1)) {
            var data = JSON.parse(param);
            if (elementIdForLog.indexOf('_submit') != -1 && data.businessInfo) {
                JoyinUtil.setModifyFormData(JSON.parse(data.businessInfo));
            } else {
                JoyinUtil.setModifyFormData(data);
            }
        }
        $.ajax({
            type: "POST",
            url: url,
            dataType: "json",
            contentType: "application/json",
            data: param,
            async:false,
            success: function (result) {
                Joyin.processResult(result, callback);
            }
        });
    };
    
    /**
     * 计算费用
     */
    var calFee = function(){
    	
    };
    window.calFee=calFee;
}());