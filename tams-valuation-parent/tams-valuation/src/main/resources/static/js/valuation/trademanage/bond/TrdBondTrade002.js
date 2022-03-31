
/**
 * 债券交易新建
 */
(function() {
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
     * 保存或提交
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#submitForm', false);
        object.pageModel = pageModel;
        object.operateType = operateType;
        object.pageId = pageId;

        Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/bond/TrdBondTrade002/save', object, "TrdBondTrade002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/bond/TrdBondTrade002/save', object, "TrdBondTrade002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 产品代码联动带出产品名称，证券账户
     */
    $('#prodId').change(function(){
        var prodId = $('#prodId').val();
        if(prodId){
            $('#prodName').val($('#prodId').select2('data')[0].id).trigger('change');

        }else {
            $('#prodName').val('').trigger('change');
        }
    });

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    $('#secId').change(function(){
        var secId = $('#secId').val();
        if(secId){
            var dataPort = $('#secId').select2('data')[0].object;
            var tradeMarket = dataPort.tradeMarket;
            $('#secAbbr').val($('#secId').select2('data')[0].id).trigger('change');
            $('#tradeMarket').val(tradeMarket).trigger('change');

        }else {
            $('#secAbbr').val('').trigger('change');
            $('#tradeMarket').val('').trigger('change');
        }
    });

    /*净价 = 净价总额/数量计算 */
    $('#dealNum').change(function () {
        var tradeAmt = $('#tradeAmt').val();
        var dealNum = $("#dealNum").val();
        if (tradeAmt && dealNum ) {
            var cprice = JoyinMath.doDiv(tradeAmt, dealNum, 4, JoyinMath.ROUND_HALF_UP);
            $("#cprice").val(cprice).trigger("blur");
        }
    });

    $('#ps').change(function(){
        psChange();
    });

    //申购时必填
    var psChange = function () {
        var ps = $('#ps').val();
        if(ps == "T04"){
            $('#purchInfo').show();
            $('#confirmInfo').hide();
            $('#settleInfo').hide();
        }else if(ps == "T05"){
            $('#purchInfo').hide();
            $('#confirmInfo').show();
            $('#settleInfo').hide();
        }else if(ps == "T01" || ps == "T02" || ps == "T03" || ps == "T39"){
            $('#purchInfo').hide();
            $('#confirmInfo').hide();
            $('#settleInfo').show();
        }else{
            $('#purchInfo').hide();
            $('#confirmInfo').hide();
            $('#settleInfo').hide();
        }
    };

    //结算金额  结算金额=净价成交金额+应收利息总额+经手费+结算费+净佣金+交易手续费+结算服务费
    var settleAmtChange = function(){
        var tradeAmt = $('#tradeAmt').val();
        var intAmt = $('#intAmt').val();
        var brokerage = $('#brokerage').val();
        var stlAmt = $('#stlAmt').val();
        var commission = $('#commission').val();
        var dealFeeAmt = $('#dealFeeAmt').val();
        var stlFeeAmt = $('#stlFeeAmt').val();
        var commissionPay = $('#commissionPay').val();
        var ps = $('#ps').val();
        if(tradeAmt != null && tradeAmt != ''
            && brokerage != null && brokerage != ''
            && stlAmt != null && stlAmt != ''
            && commission != null && commission != ''
            && dealFeeAmt != null && dealFeeAmt != ''
            && stlFeeAmt != null && stlFeeAmt != ''
            ){
            if(ps == 'T01'){
                var settleAmt1 = JoyinMath.doAdd(JoyinMath.doAdd(tradeAmt, intAmt,2),brokerage,2);
                var settleAmt2 = JoyinMath.doAdd(JoyinMath.doAdd(JoyinMath.doAdd(settleAmt1,stlAmt,2),dealFeeAmt,2),stlFeeAmt,2);
                if(commissionPay && commissionPay != 0 ){
                    var settleAmt3 = JoyinMath.doAdd(settleAmt2, commissionPay,2);
                }else{
                    var settleAmt3 = JoyinMath.doAdd(settleAmt2, commission,2);
                }
            }else{
                var settleAmt1 = JoyinMath.doMinus(JoyinMath.doAdd(tradeAmt, intAmt,2),brokerage,2);
                var settleAmt2 = JoyinMath.doMinus(JoyinMath.doMinus(JoyinMath.doMinus(settleAmt1,stlAmt,2),dealFeeAmt,2),stlFeeAmt,2);
                if(commissionPay && commissionPay != 0 ){
                    var settleAmt3 = JoyinMath.doMinus(settleAmt2, commissionPay,2);
                }else{
                    var settleAmt3 = JoyinMath.doMinus(settleAmt2, commission,2);
                }
            }
            $('#settleAmt').val(settleAmt3);
            $('#bTradeAmt').val(settleAmt3);
        }else{
            $('#settleAmt').val('');
            $('#bTradeAmt').val('');
        }
    }


    /**
     * 画面加载完成
     */
    $(function() {

        if(workflowId){
            psChange();
        }

        /*全价总额变动计算净价总额*/
        $('#tradeAmt').change(function () {
            var tradeAmt = $('#tradeAmt').val();
            var dealNum = $("#dealNum").val();
            if(tradeAmt && dealNum ){
                var cprice = JoyinMath.doDiv(tradeAmt, dealNum, 4, JoyinMath.ROUND_HALF_UP);
                $("#cprice").val(cprice).trigger("blur");
            }
            //结算金额
            settleAmtChange();
        });

        //交易类型联动
        $('#ps').change(function () {
            var ps = $('#ps').val();
            if(ps == 'T04'){
                $('#bondAmt').attr("required",true).trigger('change');
            }else{
                $('#bondAmt').attr("required",false).trigger('change');
            }
            star();
        });

        //结算金额联动事件
        //经手费
        $('#brokerage').change(function(){
            settleAmtChange();
        });
        //结算费
        $('#stlAmt').change(function(){
            settleAmtChange();
        });
        //佣金
        $('#commission').change(function(){
            settleAmtChange();
        });
        //交易手续费
        $('#dealFeeAmt').change(function(){
            settleAmtChange();
        });
        //结算手续费
        $('#stlFeeAmt').change(function(){
            settleAmtChange();
        });
        //应收利息总额
        $('#intAmt').change(function(){
            settleAmtChange();
        });
        //应收利息总额
        $('#commissionPay').change(function(){
            settleAmtChange();
        });

        // 绑定按钮事件
        $('#TrdBondTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
