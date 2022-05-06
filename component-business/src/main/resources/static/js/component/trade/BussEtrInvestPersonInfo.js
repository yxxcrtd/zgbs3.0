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
    
    
    /**
     * 交易场所
     */
    $('#tradeMarket').change(function () {
    	if(callPageId && callPageId == "EtrInvestBondInfo002"){
    		if($('#tradeMarket').val() == '01'){
            	$('#counterTraderDiv').hide();
    			$('#counterTraderDiv #etrInvestPersonInfo_counterTrader').val('');
    			$('#traderDiv').show();
    		} else {
    			$('#counterTraderDiv').show();
    			$('#traderDiv').hide();
    			$('#traderDiv #etrInvestPersonInfo_counterTrader').val('');
    		}
    	}
    })
    
    $(function() {
    	//银行间交易对手与对手方交易员取值与其他不同；
    	var traderName = '#'+entityName+'_counterTrader';
		if (callPageId && callPageId == "EtrInvestBondInfo002" && $('#tradeMarket').val() == '01') {
			$('#counterTraderDiv').hide();
			$('#counterTraderDiv '+traderName).val('');
			$('#traderDiv').show();
		} else {
			$('#counterTraderDiv').show();
			$('#traderDiv').hide();
			$('#traderDiv '+traderName).val('');
		}
		
        var name = '#'+entityName+'_counterId';
        if(single == "1" && pageId != 'EtrFund002'){
            $(name).attr('required',true);
        }
        if (pageId == 'EtrConBond002'){
            $('#etrInvestPersonInfo_trader').removeAttr('multiple'); 
        }
        star();
        $(name).change(function () {// 内部交易对手时，只能选择卖出
            if ($(name).select2('data') && $(name).select2('data')[0]) {
              if(callPageId && callPageId == "EtrInvestBondInfo002"){
            	  if(pageModel != PAGE_MODEL_REFERENCE){
              		var 交易对手 =  $(name).select2('data')[0].id;
              		if(交易对手){
              			$("select#etrInvestPersonInfo_counterTrader").each(
              				function(){
              					$(this).val('').trigger('change');
              			})
              		}
              	 }
              }else {
            	if(pageModel != PAGE_MODEL_REFERENCE){
            		var 交易对手 =  $(name).select2('data')[0].object;
                    if (!$(name).val()) {
                        // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                        $("#settleSpreed").prop("disabled", false).change();
                        $("#settleSpeed").prop("disabled", false).change();
                        if(callPageId == 'EtrInvestBondInfo002'){
                             $('#price').val('');
                        }
                    }
                    // 【债券投资委托】交易对手选择内部交易对手时，清算速度为T+0不可改
                    if (交易对手 == 'N') {
                        $("#settleSpreed").val('01').prop("disabled", true).change();
                        $("#settleSpeed").val('01').prop("disabled", true).change();
                        $('#entrustIntegrateNum').val(0).prop("disabled", true).trigger("change");
                        $('#entrustOwnNum').val($("#entrustNum").val()).prop("disabled", true).trigger("change");
                    } else {
                        $("#settleSpreed").prop("disabled", false);
                        $("#settleSpeed").prop("disabled", false);
                        $('#entrustIntegrateNum').prop("disabled", false);
                        $('#entrustOwnNum').prop("disabled", false);
                        if(callPageId == 'EtrInvestBondInfo002'){
                             $('#price').val('');
                        }
                    }
            	}
              }
            }
        })
    });
}());