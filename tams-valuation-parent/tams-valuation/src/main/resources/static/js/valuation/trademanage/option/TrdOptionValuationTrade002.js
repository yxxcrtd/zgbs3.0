
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

        Joyin.ajaxJson(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade002/save', object, "TrdOptionValuationTrade002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/option/TrdOptionValuationTrade002/save', object, "TrdOptionValuationTrade002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 产品代码联动带出产品名称，证券账户
     */
    var prodIdChange = function() {
        var prodId = $('#prodId').val();
        if (prodId == '' || prodId == undefined) {
            $('#prodName').val('');
        } else {
            $('#prodName').val($('#prodId').select2('data')[0].object).trigger('change');
        }
        // Joyin.ajaxJson(basePath + 'valuation/trademanage/bond/TrdBondTrade002/selectInfoByProdId',
        //     {prodId: prodId}, function (data) {
        //         if (data) {
        //             $('#secManageAcctId').val(data.secManageAcctId).trigger("change");
        //         }
        //     })
    }

    //结算金额  结算金额=期权费+其他费用
    var settleAmtChange = function(){
        var optFee = $('#optFee').val();
        var otherAmt = $('#otherAmt').val();
        if(optFee != null && optFee != ''
            && otherAmt != null && otherAmt != ''){
            var settleAmt = JoyinMath.doAdd(optFee, otherAmt,2);
            $('#settleAmt').val(settleAmt);
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

        //结算金额联动事件
        //期权费
        $('#optFee').change(function(){
            settleAmtChange();
        });
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });

        // 绑定按钮事件
        $('#TrdOptionValuationTrade002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdOptionValuationTrade002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
