/**
 * 投资委托人员信息管理
 */
(function(){
	/**
     * 画星星
     */
    var star = function() {
        // 加红星星
        // 查找label
        var _findPrevLabel = function(element, level) {
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
            $('.must').each(function() {
                $(this).remove();
            })
            $('*[required]').each(function() {
                var $el = $(this);
                var $target = _findPrevLabel($el, 3);
                if ($target.find('> .must').length === 0) {
                    $target.append($('<i class="must">*</i>'));
                }
            });
        }
    }
    var entityName = ENTITY_NAME;
    var getElementById = function(id) {
        return $('#' + entityName + '_' + id);
    };
    var init = function () {
    	if(callPageId == "TrdBondInvestTrade002" && ($("#fTradeId").val() || $("#fTradeIdShow").val())){
    		$("#cfets_counterTraderDiv").show();
    		$('#counterTraderDiv #trdInvestPersonInfo_counterTrader').val('');
    		$("#counterTraderDiv").hide();
    	}else{
    		$("#counterTraderDiv").show();
    		$("#cfets_counterTraderDiv #trdInvestPersonInfo_counterTrader").val('').trigger('change');
    		$("#cfets_counterTraderDiv").hide();
    	}
    	
    	/*$("#tradeMarket #chlId #ps").change(function(){
    		var tradeMarket = $("#tradeMarket").val();
    		var chlId = $("#chlId").val();
    		var ps = $("#ps").val();
    		if(callPageId == "TrdBondInvestTrade002"){
    			$("#trdInvestPersonInfo_counterId").textSelect('setValue','');
    			$("select#trdInvestPersonInfo_counterTrader").textSelect('setValue','');
    		}
    	});*/
    	$("#fTradeId").change(function(){
    		var fTradeId = $("#fTradeId").val();
    		if(fTradeId){
    			$("#cfets_counterTraderDiv").show();
        		$('input#trdInvestPersonInfo_counterTrader').val('');
        		$("#counterTraderDiv").hide();
        		$("#trdInvestPersonInfo_counterId").textSelect('setValue','');
    		}else{
    			$("#counterTraderDiv").show();
        		$("select#trdInvestPersonInfo_counterTrader").textSelect('setValue','');
        		$("#cfets_counterTraderDiv").hide();
        		$("#trdInvestPersonInfo_counterId").textSelect('setValue','');
    		}
    	})
    	
    	if(callPageId != "TrdBondInvestTrade002"){
    		$("#cfets_counterTraderDiv").empty();
    	}
    	
    	
        var counterId = getElementById('counterId');
        if(pageId == 'TrdFundInvestTrade003' || pageId == 'TrdPlanNetInvestTrade003'){
            var trader = getElementById('trader');
          var counterTrader = getElementById('counterTrader');
          var contactInfo = getElementById('contactInfo');
          $(trader).attr('disabled',true);
          $(counterId).attr('disabled',true);
          $(counterTrader).attr('disabled',true);
          $(contactInfo).attr('disabled',true);
        }
        // TISHKS-727:场内公募基金交易新建页面交易对手必填 modify by fuyongsheng 20220507 start
        else if(pageId == 'TrdFundInvestTrade002'){
            getElementById('counterId').attr("required", true);
            star();
        }
        // TISHKS-727:场内公募基金交易新建页面交易对手必填 modify by fuyongsheng 20220507 end
        else if(pageId =='TrdStock002'){
            //交易对手必填 modified by lishuping 20191029 start
        	//getElementById('counterId').removeAttr("required");
            //交易对手必填 modified by lishuping 20191029 end
        	star();
        }else if(pageId =='TrdBondLendInvestTrade002'){
            getElementById('counterId').attr("required", true);
            star();
        }
        getElementById('counterId').on('change', function () {
            var counterIdSelect = getElementById('counterId');
            if(counterIdSelect.val()){
                getElementById('isOutTrade').val(counterIdSelect.select2('data')[0].object).trigger('change');
            }
            if(counterIdSelect && $("select#trdInvestPersonInfo_counterTrader").prop("disabled") == false){
            	$("select#trdInvestPersonInfo_counterTrader").val('').trigger('change');
            }
        });
        counterId.change(function () {// 内部交易对手时，只能选择卖出
            if (counterId.select2('data') && counterId.select2('data')[0]) {
                var 交易对手 =  counterId.select2('data')[0].object;
                if (!counterId.val()) {
                    // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                       /* $("#settleSpreed").prop("disabled", false).change();
                        $("#settleSpeed").prop("disabled", false).change();*/
                }
                // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                if (交易对手 == 'N') {
                    /*$("#settleSpreed").val('01').prop("disabled", true).change();
                    $("#settleSpeed").val('01').prop("disabled", true).change();*/
                    //内部交易对手清空置灰费用
                    $('#commissionRate').val('').prop('disabled', true);
                    $('#commissionAmt').val('').prop('disabled', true);
                } else {
                    /*$("#settleSpreed").prop("disabled", false);
                    $("#settleSpeed").prop("disabled", false);*/
                    //内部交易对手清空置灰费用
                    $('#commissionRate').prop('disabled', false);
                }
            }
        });
        $("#windtypeone").on('change',function(){
        	var windtypeone = $("#windtypeone").val();
        	if(windtypeone == WINDTYPEONE_B14){
        		getElementById('counterId').removeAttr("required");
            	star();
        	}else{
        		getElementById('counterId').attr("required", true);
            	star();
        	}
    	});
    };
    var setEntrust = function (func) {
        if (pageModel == '0') {
            var entrustInfo = func();
            if(!getElementById("counterId").val()){
                if(entrustInfo.innerTrade=='N'){
                    $("#innerTrade").val(true).trigger('change');
                }else{
                    $("#innerTrade").val(false).trigger('change');
                }
            }
            if (!getElementById("counterId").val()) {
                getElementById("counterId").textSelect('setValue', entrustInfo.counterId);
            }
            if (!getElementById("counterTrader").val()) {
                getElementById("counterTrader").val(entrustInfo.counterTrader).trigger('change');
            }
            if (!getElementById("contactInfo").val()) {
                getElementById("contactInfo").val(entrustInfo.contactInfo).trigger('change');
            }
            getElementById("isOutTrade").val(entrustInfo.isOutTrade).trigger('change');
        }
    };
//    init();
    // 划款指令共通组件公开接口
    if(window.TrdInvestPersonInfo002 == undefined){
        window.TrdInvestPersonInfo002 = {};
    }
    window.TrdInvestPersonInfo002[entityName] = {
        init: init,
        /**
         * 输入函数范例，需提供交易对手、对手方交易员、联系方式：
         *   function {
         *       return {'counterId':'12412512','counterTrader':null,'isOutTrade':'02'}
         *   }
         */
        setEntrust : setEntrust
    };
    init();


    /**
     * 画面加载完成
     */
    $(function() {
        // ZGZGST-2008:投资经理指定内部交易对手，交易员不能修改交易对手 fuyongsheng add start
        if(entity && entity.counterId && entity.isOutTrade && entity.isOutTrade === "false"){
            var counterId = getElementById('counterId');
            $(counterId).attr('disabled',true);
        }
        // ZGZGST-2008:投资经理指定内部交易对手，交易员不能修改交易对手 fuyongsheng add end
    });
}());
