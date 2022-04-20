/**
 * 股票交易新建
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
    //当前是否包含流程
    var workflowIdNow = $("#workflowId").val();

    //定义表格列数据
    var tableCols = [
        {field:'ratio', title: '数量比例%',width:220, edit:true,sortable: false,type: 'number',fixType:"2",pattern:"2", digit:2},
        {field:'lockedNum', title: '锁定数量',width:220,sortable: false,type: 'number',fixType:"2",pattern:"2", digit:2},
        {field:'startDate', title: '开始日期',width:220, edit: true, type : 'date',sortable: false},
        {field:'endDate', title: '结束日期', width:220, edit : true, type : 'date',sortable: false},
        {field:'circulateDate', title: '预计上市流通日', width:220, edit : true, type : 'date',sortable: false},
        {field:'lockedDays', title: '锁定天数',width:220,sortable: false}
    ];

    var isDisabledVal = false;//退回修改时通过页面是否disable来加载表格是否可以编辑
    if($("#prodId").prop("disabled") == true){
        isDisabledVal = true;
    }

    //无流程信息，代表是新建页面，不是我的待办中点击弹出的页面
    if(workflowIdNow==null || workflowIdNow.length==0 || !isDisabledVal){
        tableCols.push(
            {field:'operation', fixed:'right',width : 120, title : '<i id="TrdStockTrade002_btn_add" class="iconfont icon-found" style="color:green"></i>',
                align : 'center',type : 'action', buttons : ['delt']}
        );
    }

    var table=$("#dataTable").joyintable({
        searchOnInit: true,
        checkbox: false,
        height: 300,
        rowno: true,
        loading: false,
        page: false,
        ajax: {
            url : basePath + 'valuation/trademanage/trdstockvaluationtrade/TrdStockTrade002/getAddList',
            type: 'POST',
            data: {}
        },
        cols: tableCols,
        editCell : function(value, data, field, cell, column){
                if(field=='ratio' ){
                    if(data.ratio != 0){
                        var rowData = {};
                        var ps =  $('#ps').val();
                        var ratio = data.ratio;
                        var lockedNum;
                        if(ps == 'T01'){
                            var dealNum = $('#dealNum').val();
                            if(dealNum){//买入
                                lockedNum = JoyinMath.doDiv(JoyinMath.doMul(dealNum,ratio,2,JoyinMath.ROUND_HALF_UP),100,2,JoyinMath.ROUND_HALF_UP);
                            }
                        }else if(ps == 'T02'){//配股
                            var dealNum = $('#dealNum_pg').val();
                            if(dealNum){
                                lockedNum = JoyinMath.doDiv(JoyinMath.doMul(dealNum,ratio,2,JoyinMath.ROUND_HALF_UP),100,2,JoyinMath.ROUND_HALF_UP);
                            }
                        }else if(ps == 'T03'){//中签
                            var confirmNum = $('#confirmNum').val();
                            if(confirmNum){
                                lockedNum = JoyinMath.doDiv(JoyinMath.doMul(confirmNum,ratio,2,JoyinMath.ROUND_HALF_UP),100,2,JoyinMath.ROUND_HALF_UP);
                            }
                        }
                        rowData.lockedNum = lockedNum;
                        table.renderRow(data.LAY_TABLE_INDEX, rowData);
                    }else{
                        rowData.lockedNum =0
                        table.renderRow(data.LAY_TABLE_INDEX, rowData);
                    }
                }

                if(field=='startDate' || field=='endDate'){
                    var rowData = {};
                    if(new Date(data.startDate).DateDiff('d',data.endDate)>=0){
                        var nDays = Math.abs(parseInt(((Date.parse(Date.ofDate(data.startDate))) - (Date.parse(Date.ofDate(data.endDate))))/1000/3600/24));
                        rowData.lockedDays = JoyinMath.doAdd(nDays,1,2);
                        table.renderRow(data.LAY_TABLE_INDEX, rowData);
                    }else{
                        Joyin.error("结束日期不可小于开始日期!");
                    }
                }
            },
        loadonce: true
    });

    /**
     * 绑定删除事件
     */
    table.bind('click', '.delt', function(data, index) {
        table.removeRow(data);
    });

    /**
     * 点击添加按钮触发事件
     */
    table.bind('click',"#TrdStockTrade002_btn_add",function(){
        var object = $.serializeToObject('#submitForm');
        object.pageModel = pageModel;
        Joyin.ajaxJson(basePath + 'valuation/trademanage/trdstockvaluationtrade/TrdStockTrade002/doAdd', object, function(result){
            if(result.errorList.length==0){
                result.data.status = 'N';
                result.data.ratio = 0;
                result.data.startDate = platDate;
                result.data.endDate = platDate;
                result.data.circulateDate = platDate;
                result.data.lockedDays = 1;
                table.addRow(result.data);
                table.events();
            }
        });
    });


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

    /**
     * 清算信息：成交价格=成交金额/成交数量
     */
    var priceChange = function(){
        var dealNum = $('#dealNum').val();
        var tradeAmt = $('#tradeAmt').val();
        if(dealNum != null && dealNum != ''
            && tradeAmt != null && tradeAmt != ''){
            var price = JoyinMath.doDiv(tradeAmt,dealNum,2,JoyinMath.ROUND_HALF_UP);
            $('#price').val(price);
        }else{
            $('#price').val('');
        }
    }

    //买入时：清算金额=成交金额+印花税+经手费+征管费+过户费+结算费+交易费+证券其他费+净佣金（应付佣金=0.00时）
    //卖出时：清算金额=成交金额-印花税-经手费-证管费-过户费-结算费-交易费-证券其他费-净佣金（应付佣金=0.00时）
    var settleAmtChange = function(){
        var tradeAmt = $('#tradeAmt').val();
        var stampDutyAmt = $('#stampDutyAmt').val();
        var brokerage = $('#brokerage').val();
        var certfFee = $('#certfFee').val();
        var ghfAmt = $('#ghfAmt').val();
        var stlAmt = $('#stlAmt').val();
        var feeAmt = $('#feeAmt').val();
        var secOtherAmt = $('#secOtherAmt').val();
        var commission = $('#commission').val();
        var commissionPay = $('#commissionPay').val();
        var ps = $('#ps').val();
        if(tradeAmt != null && tradeAmt != '' && stampDutyAmt != null && stampDutyAmt != ''
            && brokerage != null && brokerage != '' && certfFee != null && certfFee != ''
            && ghfAmt != null && ghfAmt != '' && stlAmt != null && stlAmt != ''
            && feeAmt != null && feeAmt != ''&& secOtherAmt != null && secOtherAmt != ''
            && commission != null && commission != ''&& commissionPay != null && commissionPay != '' ){
            if(ps == 'T01'){
                var settleAmt1 = JoyinMath.doAdd(JoyinMath.doAdd(JoyinMath.doAdd(tradeAmt, stampDutyAmt,2),brokerage,2),certfFee,2);
                var settleAmt2 = JoyinMath.doAdd(JoyinMath.doAdd(JoyinMath.doAdd(JoyinMath.doAdd(settleAmt1, ghfAmt,2),stlAmt,2),feeAmt,2),secOtherAmt,2);
                if(commissionPay && commissionPay != 0 ){
                    var settleAmt3 = settleAmt2;
                }else{
                    var settleAmt3 = JoyinMath.doAdd(settleAmt2, commission,2);
                }
            }else if(ps == 'T02'){
                var settleAmt1 = JoyinMath.doMinus(JoyinMath.doMinus(JoyinMath.doMinus(tradeAmt, stampDutyAmt,2),brokerage,2),certfFee,2);
                var settleAmt2 = JoyinMath.doMinus(JoyinMath.doMinus(JoyinMath.doMinus(JoyinMath.doMinus(settleAmt1, ghfAmt,2),stlAmt,2),feeAmt,2),secOtherAmt,2);
                if(commissionPay && commissionPay != 0 ){
                    var settleAmt3 =settleAmt2;
                }else{
                    var settleAmt3 = JoyinMath.doMinus(settleAmt2, commission,2);
                }
            }
            $('#settleAmt').val(settleAmt3);
        }else{
            $('#settleAmt').val('');
        }
    }

    //配股信息：缴款金额=成交数量*成交价格
    var paymentAmt_pgChange = function(){
        var dealNum_pg = $('#dealNum_pg').val();
        var price_pg = $('#price_pg').val();
        if(dealNum_pg != null && dealNum_pg != '' && price_pg != null && price_pg != ''){
            var paymentAmt_pg = JoyinMath.doMul(dealNum_pg,price_pg,2,JoyinMath.ROUND_HALF_UP);
            $('#paymentAmt_pg').val(paymentAmt_pg);
        }else{
            $('#paymentAmt_pg').val('');
        }
    }

    //确认信息：中签价格=中签金额/中签数量
    var confirmPriceChange = function(){
        var confirmAmt = $('#confirmAmt').val();
        var confirmNum = $('#confirmNum').val();
        if(confirmAmt != null && confirmAmt != '' && confirmNum != null && confirmNum != ''){
            var confirmPrice = JoyinMath.doDiv(confirmAmt,confirmNum,2,JoyinMath.ROUND_HALF_UP);
            $('#confirmPrice').val(confirmPrice);
        }else{
            $('#confirmPrice').val('');
        }
    }

    //确认信息：，清算金额=中签金额+净佣金
    var settleAmt_qrChange = function(){
        var confirmAmt = $('#confirmAmt').val();
        var commission_qr = $('#commission_qr').val();
        if(confirmAmt != null && confirmAmt != '' && commission_qr != null && commission_qr != ''){
            var settleAmt = JoyinMath.doAdd(confirmAmt,commission_qr,2,JoyinMath.ROUND_HALF_UP);
            $('#settleAmt_qr').val(settleAmt);
        }else{
            $('#settleAmt_qr').val('');
        }
    }

    //成交数量联动计算锁定数量
    var lockedNumChange = function () {
        var ps = $('#ps').val();
        var rowList = table.getAllRows();
        if(rowList.length > 0){
            if(ps == 'T01'){//买入
                var dealNum = $('#dealNum').val();
                for(var i=0;i<rowList.length;i++){
                    var rowDate = rowList[i];
                    var result = JoyinMath.doDiv(JoyinMath.doMul(dealNum,rowDate.ratio,2,JoyinMath.ROUND_HALF_UP),100,2,JoyinMath.ROUND_HALF_UP);
                    table.renderRow(i, {lockedNum: result});
                }
            }else if(ps == 'T46'){//配股
                var dealNum_pg = $('#dealNum_pg').val();
                for(var i=0;i<rowList.length;i++){
                    var rowDate = rowList[i];
                    var result = JoyinMath.doDiv(JoyinMath.doMul(dealNum_pg,rowDate.ratio,2,JoyinMath.ROUND_HALF_UP),100,2,JoyinMath.ROUND_HALF_UP);
                    table.renderRow(i, {lockedNum: result});
                }
            }else if(ps == 'T48'){//新股中签
                var confirmNum = $('#confirmNum').val();
                for(var i=0;i<rowList.length;i++){
                    var rowDate = rowList[i];
                    var result = JoyinMath.doDiv(JoyinMath.doMul(confirmNum,rowDate.ratio,2,JoyinMath.ROUND_HALF_UP),100,2,JoyinMath.ROUND_HALF_UP);
                    table.renderRow(i, {lockedNum: result});
                }
            }
        }
    };

    $('#ps').change(function(){
        psChange();
    });

    //交易类型联动
    var psChange = function () {
        var ps = $('#ps').val();
        if(ps == '200' || ps=='107'){//买入、卖出、要约收购、港股通-现金收购、要约出售时显示
            $('#qsxx').show();
            $('#pgxx').hide();
            $('#sgxx').hide();
            $('#qrxx').hide();
            $('#limitInfo').hide();
            table.clear();
        }else if(ps == '100' ){//买入
            $('#qsxx').show();
            $('#pgxx').hide();
            $('#sgxx').hide();
            $('#qrxx').hide();
            $('#limitInfo').show();
            table.clear();
        }else if(ps == 'T46' ){//配股
            $('#qsxx').hide();
            $('#pgxx').show();
            $('#sgxx').hide();
            $('#qrxx').hide();
            $('#limitInfo').show();
            table.clear();
        }else if(ps == '105'){//新股申购
            $('#qsxx').hide();
            $('#pgxx').hide();
            $('#sgxx').show();
            $('#qrxx').hide();
            $('#limitInfo').hide();
            table.clear();
        }else if(ps == 'T48'){//新股中签
            $('#qsxx').hide();
            $('#pgxx').hide();
            $('#sgxx').hide();
            $('#qrxx').show();
            $('#limitInfo').show();
            table.clear();
        }else{//初始化
            $('#qsxx').hide();
            $('#pgxx').hide();
            $('#sgxx').hide();
            $('#qrxx').hide();
            $('#limitInfo').hide();
        }
    };


    /**
     * 画面加载完成
     */
    $(function() {

        // if(workflowId){
        //     psChange();
        // }

        //清算信息：成交价格=成交金额/成交数量
        $('#dealNum').change(function(){
            priceChange();
            lockedNumChange();
        });
        //成交价格
        $('#tradeAmt').change(function(){
            priceChange();
        });

        //清算信息：清算金额联动事件
        //交易价格
        $('#tradeAmt').change(function(){
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
        //交易费
        $('#feeAmt').change(function(){
            settleAmtChange();
        });
        //证券其它费
        $('#secOtherAmt').change(function(){
            settleAmtChange();
        });
        //净佣金
        $('#commission').change(function(){
            settleAmtChange();
        });
        //应付佣金
        $('#commissionPay').change(function(){
            settleAmtChange();
        });
        //交易方向
        $('#ps').change(function(){
            settleAmtChange();
        });

        //配股信息：缴款金额联动
        //成交数量
        $('#dealNum_pg').change(function(){
            paymentAmt_pgChange();
        });
        //成交价格
        $('#price_pg').change(function(){
            paymentAmt_pgChange();
        });

        //确认信息：中签价格=中签金额/中签数量
        //中签数量
        $('#confirmNum').change(function(){
            confirmPriceChange();
        });
        //中签金额
        $('#confirmAmt').change(function(){
            confirmPriceChange();
        });

        //确认信息:清算金额=中签金额+净佣金
        //净佣金
        $('#commission_qr').change(function(){
            settleAmt_qrChange();
        });
        //中签金额
        $('#confirmAmt').change(function(){
            settleAmt_qrChange();
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
