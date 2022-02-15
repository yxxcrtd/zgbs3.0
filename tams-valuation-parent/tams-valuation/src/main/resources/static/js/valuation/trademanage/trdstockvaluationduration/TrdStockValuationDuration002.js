
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration002/save', object, "TrdStockValuationDuration002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration002/save', object, "TrdStockValuationDuration002_btn_submit");
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

    //结算金额  结算金额=分红金额
    var settleAmtChange = function(){
        var bonusAmt = $('#bonusAmt').val();
        if(bonusAmt != null && bonusAmt != ''){
            $('#settleAmt').val(bonusAmt);
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
        //分红金额
        $('#bonusAmt').change(function(){
            settleAmtChange();
        });

        //交易类型联动
        $('#ps').change(function(){
            var ps = $('#ps').val();
            if(ps == '01' || ps == '02'){
                $('#bonusAmt').attr("required",true).trigger('change');
            }else{
                $('#bonusAmt').attr("required",false).trigger('change');
            }
            if(ps == '02' || ps == '03'){
                $('#devidentAmt').attr("required",true).trigger('change');
            }else{
                $('#devidentAmt').attr("required",false).trigger('change');
            }
            star();
        });

        // 绑定按钮事件
        $('#TrdStockValuationDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdStockValuationDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
