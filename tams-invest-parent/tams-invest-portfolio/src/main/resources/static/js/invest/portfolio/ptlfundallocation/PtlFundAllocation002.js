/**
 * 资金划拨管理
 */

(function() {
    //划款指令信息
    window.componentLoadCallback = function(isDone, pagePath, params){
        if(isDone){
            if(window.BussTrdTransferOrder && window.BussTrdTransferOrder['bussTrdTransferOrder']) {
                window.BussTrdTransferOrder['bussTrdTransferOrder'].setStlCommandInput(function () {
                    var dealObj = {};
                    dealObj.finprodType2 = Fin_Type;
                    dealObj.settleDate = $("#settleDate").val();
                    dealObj.tradeDate = $("#settleDate").val();
                    dealObj.ps = $("#payType").val();
                    dealObj.ccy = $("#ccy").val();
                    dealObj.tradeAmt = $("#settleAmt").val();
                    dealObj.counterId = $("#counterAcctNo").val();
                    dealObj.secManageAcctId = $("#myAcctNo").val();
                    
                    // 付款账户
                    var payAcctNoList = new Array();
                    if ($("#myAcctNo").val()) {
                        var payAcctNo = {};
                        payAcctNo.id = $("#myAcctNo").val();
                        // 流程退回重新生成指令没有check且报错bug修正 modify by renping 20191102 start
                        payAcctNo.text = $("#myAcctNo").select2("data")[0].text.trim();
                        // 流程退回重新生成指令没有check且报错bug修正 modify by renping 20191102 end
                        payAcctNo.object = "isUsePageAcctTrue";
                        payAcctNoList[0] = payAcctNo;
                    }
                    
                    // 收款账户
                    var recAcctNoList = new Array();
                    if ($("#counterAcctNo").val()) {
                        var recAcctNo = {};
                        recAcctNo.id = $("#counterAcctNo").val();
                        // 流程退回重新生成指令没有check且报错bug修正 modify by renping 20191102 start
                        recAcctNo.text = $("#counterAcctNo").select2("data")[0].text.trim();
                        // 流程退回重新生成指令没有check且报错bug修正 modify by renping 20191102 start
                        recAcctNo.object = "isUsePageAcctTrue";
                        recAcctNoList[0] = recAcctNo;
                    }
                    return {
                        'isTotalTrade':YES_OR_NO_N
                        ,'bankLendingType':null
                        ,'portfolioId':$("#portfolioId").val()
                        ,'deal':dealObj
                        ,'isUsePageAcct':true
                        ,'payAcctNoList':payAcctNoList
                        ,'recAcctNoList':recAcctNoList
                        ,'repPageId':pageId
                    };
                });
            }

            // 流程退回重新生成指令没有check且报错bug修正 add by renping 20191102 start
            if(stlPayOrdersList){
                window.BussTrdTransferOrder['bussTrdTransferOrder'].saveCalcCondition();
            }
            // 流程退回重新生成指令没有check且报错bug修正 add by renping 20191102 end
        }
    }
    //初始化
    var init =function(){
        if(pageModel == PAGE_MODEL_NEW){
            Joyin.ajax(basePath + 'invest/portfolio/ptlfundallocation/PtlFundAllocation002/getInitDate',{}, function(result) {//数据验证
                if(result){
                    $('#settleDate').val(result);
                }    
            });
        }else{
//            initAmount();
            initMyAcctAmount();
            iniCounterAcctAmount();
        }
    }

    function saveOrSubmit(operateType) {
        if (GEN_PAY_ORDERS_AUTO == '01') {
            window.BussTrdTransferOrder['bussTrdTransferOrder'].generateOrders(function () {
                realSaveOrSubmit(operateType);
            });
        }
        else {
            realSaveOrSubmit(operateType);
        }
    }
    
    /**
     * 保存和提交处理函数
     * @param operateType 操作类型
     * @returns 没有返回值
     */
    function realSaveOrSubmit(operateType){
        if (window.BussTrdTransferOrder['bussTrdTransferOrder'].isCalcConditionChanged()) {
            return;
        }
        var object = $.serializeToObject("#submitForm");
        object.operateType = operateType;
        if (object.bussTrdTransferOrder && object.bussTrdTransferOrder.length > 0) {
            object.orderId = object.bussTrdTransferOrder[0].orderId;
        }
        Joyin.ajaxJson(basePath + 'invest/portfolio/ptlfundallocation/PtlFundAllocation002/check', object, function(result) {
            if(operateType === 'save') {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.save")), function() {
                    WorkFlow.doSaveList(basePath + 'invest/portfolio/ptlfundallocation/PtlFundAllocation002/save', object, "PtlFundAllocation002_btn_save");
                });
            }
            else {
                Joyin.confirm(JoyinUtil.getMessage("COMMON_C_0001",JoyinUtil.getLabel("SysCommon.submit")), function() {
                    WorkFlow.doSubmitList(basePath + 'invest/portfolio/ptlfundallocation/PtlFundAllocation002/save', object, "PtlFundAllocation002_btn_submit");
                });
            }
        });
    }
    /**
     * 初始化现金余额
     */
    var initAmount = function(){
        if($('#myAcctNo').val() && $('#myAcctNo').val() != ''
            && $('#settleDate').val() && $('#settleDate').val() != ''
            && $('#ccy').val() && $('#ccy').val() != ''){
            var data = {
                    acctId:$('#myAcctNo').val(),
                    cdate:$('#settleDate').val(),
                    portfolioId:$('#portfolioId').val(),
                    ccy:$('#ccy').val()
            };
            Joyin.ajax(basePath + 'invest/portfolio/ptlfundallocation/PtlFundAllocation002/getAmount', data, function(result) {//数据验证
                if(result && result != ''){
                    $('#amount').val(result).trigger('blur');
                }else{
                    $('#amount').val('').trigger('blur');
                }    
            });
        }else{
            $('#amount').val('').trigger('blur');
        }
    }
    
    /**
     * 初始化账户余额
     */
    var initAcctAmount = function(acctNoId, acctAmountId) {
        if ($('#' + acctNoId).val() && $('#' + acctNoId).val() != ''
            && $('#settleDate').val() && $('#settleDate').val() != ''
            && $('#ccy').val() && $('#ccy').val() != ''){
            var data = {
                    acctId:$('#' + acctNoId).val(),
                    cdate:$('#settleDate').val(),
                    portfolioId:$('#portfolioId').val(),
                    ccy:$('#ccy').val()
            };
            Joyin.ajax(basePath + 'invest/portfolio/ptlfundallocation/PtlFundAllocation002/getActAmount', data, function(result) {//数据验证
                if (result && result != ''){
                    $('#' + acctAmountId).val(result).trigger('blur');
                } else {
                    $('#' + acctAmountId).val('').trigger('blur');
                }    
            });
        } else {
            $('#' + acctAmountId).val('').trigger('blur');
        }
    }

    /**
     * 初始化付款账户余额
     */
    var initMyAcctAmount = function() {
        initAcctAmount("myAcctNo", "myAcctAmount");
    }
    /**
     * 初始化收款账户余额
     */
    var iniCounterAcctAmount = function() {
        initAcctAmount("counterAcctNo", "counterAcctAmount");
    }

    /**
     * 画面加载完成
     */
    $(function() {
        
        init();
        // 是否显示划款指令
        if(GEN_PAY_ORDERS_AUTO=='01'){
        	$('#bussTrdTransferOrder_div').hide() ;
        }else{
        	$('#bussTrdTransferOrder_div').show() ;
        }
        /**
         * 组合、币种、划款类型变动联动事件
         */
        $(".baseInfo").change(function(){
            $('#myAcctNo').val('').trigger('change');
            $('#counterAcctNo').val('').trigger('change');
        })
        
        $("#settleDate").change(function(){
            initMyAcctAmount();
            iniCounterAcctAmount();
        })
        /**
         * 付款账户联动事件
         */
        $('#myAcctNo').change(function(){
            initMyAcctAmount();
        });
        /**
         * 收款账户联动事件
         */
        $('#counterAcctNo').change(function(){
            iniCounterAcctAmount();
        });
        //保存
        $('#PtlFundAllocation002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        //提交
        $('#PtlFundAllocation002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
    });
}());