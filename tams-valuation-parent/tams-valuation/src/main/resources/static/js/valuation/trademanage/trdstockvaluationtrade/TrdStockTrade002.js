
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

    //结算金额  结算金额=交易价格*交易数量+佣金+印花税+其他费用
    var settleAmtChange = function(){
        var tradeAmt = $('#tradeAmt').val();
        var dealNum = $('#dealNum').val();
        var stampDutyAmt = $('#stampDutyAmt').val();
        var commission = $('#commission').val();
        var otherAmt = $('#otherAmt').val();
        if(tradeAmt != null && tradeAmt != ''
            && dealNum != null && dealNum != ''
            && stampDutyAmt != null && stampDutyAmt != ''
            && commission != null && commission != ''
            && otherAmt != null && otherAmt != ''){
            var settleAmt1 = JoyinMath.doMul(tradeAmt, dealNum,2,JoyinMath.ROUND_HALF_UP);
            var settleAmt2 = JoyinMath.doAdd(JoyinMath.doAdd(settleAmt1, commission,2),stampDutyAmt,2);
            var settleAmt3 = JoyinMath.doAdd(settleAmt2, otherAmt,2);
            $('#settleAmt').val(settleAmt3);
        }else{
            $('#settleAmt').val('');
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

        //结算金额联动事件
        //交易价格
        $('#tradeAmt').change(function(){
            settleAmtChange();
        });
        //交易数量
        $('#dealNum').change(function(){
            settleAmtChange();
        });
        //印花税
        $('#stampDutyAmt').change(function(){
            settleAmtChange();
        });
        //佣金
        $('#commission').change(function(){
            settleAmtChange();
        });
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });

        //交易类型联动
        $('#ps').change(function () {
            var ps = $('#ps').val();
            if(ps == 'T01' || ps == 'T02'){
                $('#settdateDateDiv').show();
                $('#paymentDateDiv').hide();
                $('#settleDateDiv').hide();
            }else if(ps == 'T46' || ps == 'T47' || ps == 'T48'){
                $('#settdateDateDiv').hide();
                $('#paymentDateDiv').show();
                $('#settleDateDiv').show();
            }else{
                $('#settdateDateDiv').show();
                $('#paymentDateDiv').show();
                $('#settleDateDiv').show();
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
