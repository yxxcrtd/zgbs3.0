
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdbondvaluationduration/TrdBondValuationDuration002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdbondvaluationduration/TrdBondValuationDuration002/save', object, "TrdBondValuationDuration002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdbondvaluationduration/TrdBondValuationDuration002/save', object, "TrdBondValuationDuration002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 产品代码联动带出产品名称
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
    $('#assetId').change(function(){
        var assetId = $('#assetId').val();
        if(assetId){
            $('#assetAbbr').val($('#assetId').select2('data')[0].id).trigger('change');
        }else {
            $('#assetAbbr').val('').trigger('change');
        }
    });

    //结算金额  结算金额=实收利息+实收本金+其他费用
    var settleAmtChange = function(){
        var actInt = $('#actInt').val();
        var actCapi = $('#actCapi').val();
        if(actInt != null && actInt != ''
            && actCapi != null && actCapi != ''
        ){
            var settleAmt = JoyinMath.doAdd(actInt, actCapi,2);
            $('#settleAmt').val(settleAmt);
        }else{
            $('#settleAmt').val('');
        }
    }


    /**
     * 画面加载完成
     */
    $(function() {

        //结算金额联动事件
        //实收利息
        $('#actInt').change(function(){
            settleAmtChange();
        });
        //实收本金
        $('#actCapi').change(function(){
            settleAmtChange();
        });

        // 绑定按钮事件
        $('#TrdBondValuationDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdBondValuationDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });

    });

}());
