
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
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdirsvaluationduration/TrdIrsValuationDuration002/check', object, function(result) {
                Joyin.confirm(result.data, function() {
                        if (operateType === 'save') {
                            WorkFlow.doSaveList(basePath + 'valuation/trademanage/trdirsvaluationduration/TrdIrsValuationDuration002/save', object, "TrdIrsValuationDuration002_btn_save");
                        } else {
                            WorkFlow.doSubmitList(basePath + 'valuation/trademanage/trdirsvaluationduration/TrdIrsValuationDuration002/save', object, "TrdIrsValuationDuration002_btn_submit");
                        }
                    });
            })
    }

    /**
     * 原始交易编号带出产品代码和产品名称
     */
    var pTradeIdChange = function() {
        var pTradeId =  $('#pTradeId').val();
        if (pTradeId == '' || pTradeId == undefined) {
            $('#prodId').val('');
            $('#prodName').val('');
        } else {
            Joyin.ajax(basePath + 'valuation/trademanage/trdirsvaluationduration/TrdIrsValuationDuration002/findInfoByTradeId', {pTradeId:pTradeId},
                function (result) {
                    if(result){
                        //$('#prodId').val(result.finprodId).trigger('change');
                        $('#prodId').textSelect('setValue',result.finprodId);
                        $('#prodId').trigger('change');
                        $('#prodName').val(result.finprodName).trigger('change');
                        $('#counterId').textSelect('setValue', result.counterId);
                        $('#counterId').trigger('change');
                    }
                });
        }
    }

    //结算金额  结算金额=其他费用
    var settleAmtChange = function(){
        var otherAmt = $('#otherAmt').val();
        if(otherAmt != null && otherAmt != ''){
            $('#settleAmt').val(otherAmt);
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

        //原始交易编号联动事件
        $('#pTradeId').change(function(){
            pTradeIdChange();
        });

        //结算金额联动事件
        //其他费用
        $('#otherAmt').change(function(){
            settleAmtChange();
        });

        //收付息类型联动
        $('#irsPsIntType').change(function(){
            var irsPsIntType = $('#irsPsIntType').val();
            //固定利息支付日、固定利息:收付息类型为收取浮动或支付浮动时置灰
            if(irsPsIntType == '03' || irsPsIntType == '04'){
                $('#fixPayDate').attr('disabled',true).trigger('change');
                $('#fixIntAmt').attr('disabled',true).trigger('change');
                $('#floatPayDate').attr('disabled',false).trigger('change');
                $('#floatIntAmt').attr('disabled',false).trigger('change');
            }
            //浮动利息支付日、浮动利息:收付息类型为收取固定或支付固定时置灰
            else if(irsPsIntType == '01' || irsPsIntType == '02'){
                $('#floatPayDate').attr('disabled',true).trigger('change');
                $('#floatIntAmt').attr('disabled',true).trigger('change');
                $('#fixPayDate').attr('disabled',false).trigger('change');
                $('#fixIntAmt').attr('disabled',false).trigger('change');
            }else{
                $('#fixPayDate').attr('disabled',false).trigger('change');
                $('#fixIntAmt').attr('disabled',false).trigger('change');
                $('#floatPayDate').attr('disabled',false).trigger('change');
                $('#floatIntAmt').attr('disabled',false).trigger('change');
            }
        });

        //交易类型联动
        $('#ps').change(function(){
            var ps = $('#ps').val();
            //交易类型为提前终止
            if(ps == '03' ){
                //提前终止支付日:交易类型为提前终止时必填，其余置灰
                $('#beforeEndPayDate').attr('disabled',false).trigger('change');
                $('#beforeEndPayDate').attr("required",true).trigger('change');
                //提前终止收付方向:交易类型为提前终止时必填，其余置灰
                $('#beforeEndPs').attr('disabled',false).trigger('change');
                $('#beforeEndPs').attr("required",true).trigger('change');
                //提前终止名义本金:交易类型为提前终止时必填，其余置灰
                $('#beforeEndPrin').attr('disabled',false).trigger('change');
                $('#beforeEndPrin').attr("required",true).trigger('change');
                //提前终止收付金额:交易类型为提前终止时必填，其余置灰
                $('#beforeEndAmt').attr('disabled',false).trigger('change');
                $('#beforeEndAmt').attr("required",true).trigger('change');
            }else{
                //提前终止支付日:交易类型为提前终止时必填，其余置灰
                $('#beforeEndPayDate').attr('disabled',true).trigger('change');
                $('#beforeEndPayDate').attr("required",false).trigger('change');
                //提前终止收付方向:交易类型为提前终止时必填，其余置灰
                $('#beforeEndPs').attr('disabled',true).trigger('change');
                $('#beforeEndPs').attr("required",false).trigger('change');
                //提前终止名义本金:交易类型为提前终止时必填，其余置灰
                $('#beforeEndPrin').attr('disabled',true).trigger('change');
                $('#beforeEndPrin').attr("required",false).trigger('change');
                //提前终止收付金额:交易类型为提前终止时必填，其余置灰
                $('#beforeEndAmt').attr('disabled',true).trigger('change');
                $('#beforeEndAmt').attr("required",false).trigger('change');
            }
            star();
        });

        // 绑定按钮事件
        $('#TrdIrsValuationDuration002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        $('#TrdIrsValuationDuration002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });


    });

}());
