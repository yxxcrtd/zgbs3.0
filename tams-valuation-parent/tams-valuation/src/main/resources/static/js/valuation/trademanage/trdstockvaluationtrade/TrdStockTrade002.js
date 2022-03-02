
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdstockvaluationtrade/TrdStockTrade002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdstockvaluationtrade/TrdStockTrade002/save', object, "TrdStockTrade002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdstockvaluationtrade/TrdStockTrade002/save', object, "TrdStockTrade002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 产品代码联动带出产品名称
     */
    var prodIdChange = function() {
        var prodId = $('#prodId').val();
        if (prodId == '' || prodId == undefined) {
            $('#prodName').val('');
        } else {
            $('#prodName').val($('#prodId').select2('data')[0].object).trigger('change');
        }
    }

    /**
     * 资产代码联动带出资产简称，交易场所
     */
    var secIdChange = function() {
        var assetId =  $('#assetId').val();
        if (assetId == '' || assetId == undefined) {
            $('#assetAbbr').val('');
        } else {
            $('#assetAbbr').val($('#assetId').select2('data')[0].object).trigger('change');
        }
    }

    //结算金额  买入时：结算金额=成交金额+净佣金+印花税+经手费+证管费+过户费+结算费+其他费用
    //卖出时：结算金额=成交金额-净佣金-印花税-经手费-证管费-过户费-结算费-其他费用
    var settleAmtChange = function(){
        var tradeAmt = $('#tradeAmt').val();
        var commission = $('#commission').val();
        var stampDutyAmt = $('#stampDutyAmt').val();
        var brokerage = $('#brokerage').val();
        var certfFee = $('#certfFee').val();
        var ghfAmt = $('#ghfAmt').val();
        var stlAmt = $('#stlAmt').val();
        var otherAmt = $('#otherAmt').val();
        var ps = $('#ps').val();
        if(tradeAmt != null && tradeAmt != '' && commission != null && commission != ''
            && stampDutyAmt != null && stampDutyAmt != '' && brokerage != null && brokerage != ''
            && certfFee != null && certfFee != '' && ghfAmt != null && ghfAmt != ''
            && stlAmt != null && stlAmt != ''&& otherAmt != null && otherAmt != '' ){
            if(ps != '' && ps == 'T01'){
                var settleAmt1 = JoyinMath.doAdd(JoyinMath.doAdd(JoyinMath.doAdd(tradeAmt, commission,2),stampDutyAmt,2),brokerage,2);
                var settleAmt2 = JoyinMath.doAdd(JoyinMath.doAdd(JoyinMath.doAdd(settleAmt1, certfFee,2),ghfAmt,2),stlAmt,2);
                var settleAmt3 = JoyinMath.doAdd(settleAmt2, otherAmt,2);
            }else if(ps != '' && ps == 'T02'){
                var settleAmt1 = JoyinMath.doMinus(JoyinMath.doMinus(JoyinMath.doMinus(tradeAmt, commission,2),stampDutyAmt,2),brokerage,2);
                var settleAmt2 = JoyinMath.doMinus(JoyinMath.doMinus(JoyinMath.doMinus(settleAmt1, certfFee,2),ghfAmt,2),stlAmt,2);
                var settleAmt3 = JoyinMath.doMinus(settleAmt2, otherAmt,2);
            }
            $('#settleAmt').val(settleAmt3);
        }else{
            $('#settleAmt').val('');
        }
    }

    //缴款金额=成交数量*成交价格
    var paymentAmt_pgChange = function(){
        var dealNum_pg = $('#dealNum_pg').val();
        var tradedPrice = $('#tradedPrice').val();
        if(dealNum_pg != null && dealNum_pg != '' && tradedPrice != null && tradedPrice != ''){
            var paymentAmt_pg = JoyinMath.doMul(dealNum_pg,tradedPrice,2,JoyinMath.ROUND_HALF_UP);
            $('#paymentAmt_pg').val(paymentAmt_pg);
        }else{
            $('#paymentAmt_pg').val('');
        }
    }


    /**
     * 画面加载完成
     */
    $(function() {

        //当前是否包含流程
        var workflowIdNow = $("#workflowId").val()

        // init();
        //产品代码联动事件
        $('#prodId').change(function(){
            prodIdChange();
        });

        //资产代码联动事件
        $('#assetId').change(function(){
            secIdChange();
        });

        //缴款金额联动
        //成交数量
        $('#dealNum_pg').change(function(){
            paymentAmt_pgChange();
        });
        //成交价格
        $('#tradedPrice').change(function(){
            paymentAmt_pgChange();
        });

        //结算金额联动事件
        //交易价格
        $('#tradeAmt').change(function(){
            settleAmtChange();
        });
        //佣金
        $('#commission').change(function(){
            settleAmtChange();
        });
        //印花税
        $('#stampDutyAmt').change(function(){
            settleAmtChange();
        });
        //经手费
        $('#brokerage').change(function(){
            settleAmtChange();
        });
        //证管费
        $('#certfFee').change(function(){
            settleAmtChange();
        });
        //过户费
        $('#ghfAmt').change(function(){
            settleAmtChange();
        });
        //结算费
        $('#stlAmt').change(function(){
            settleAmtChange();
        });
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });
        //交易方向
        $('#ps').change(function(){
            settleAmtChange();
        });

        //交易类型联动
        $('#ps').change(function () {
            var ps = $('#ps').val();
            if(ps == 'T01' || ps == 'T02'){
                $('#qsxx').show();
                $('#pgxx').hide();
                $('#sgxx').hide();
                $('#qrxx').hide();
            }else if(ps == 'T46' || ps == 'T47' ){
                $('#qsxx').hide();
                $('#pgxx').show();
                $('#sgxx').hide();
                $('#qrxx').hide();
            }else if(ps == '105'){
                $('#qsxx').hide();
                $('#pgxx').hide();
                $('#sgxx').show();
                $('#qrxx').hide();
            }else if(ps == 'T48'){
                $('#qsxx').hide();
                $('#pgxx').hide();
                $('#sgxx').hide();
                $('#qrxx').show();
            }else{
                $('#qsxx').show();
                $('#pgxx').show();
                $('#sgxx').show();
                $('#qrxx').show();
            }
        });

        //是否锁定联动
        $('#isLock').change(function () {
           var isLock = $('#isLock').val();
           if(isLock == 'Y'){
               $('#endDate').attr("required",true).trigger('change');
           }else{
               $('#endDate').attr("required",false).trigger('change');
           }
           star();
        });

        // 绑定按钮事件
        $('#TrdStockTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdStockTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
