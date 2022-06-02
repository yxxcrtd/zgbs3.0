/**
 * 计划类净值委托新建
 */
(function() {
    let invAims = invAim;
    var getriskdata = function () {
        var entrustAmt = $('#entrustAmt').val();
        var entrustNum = $('#entrustNum').val();
        var ps = $('#ps').val();
        if(entrustAmt){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            var object = {tradeDirection:['ps','EtrPlanAssetR002.ps'],portfolioCode:['portfolioId','EtrPlanAssetR002.portfolioId'],tradeAmount:['entrustAmt','EtrPlanAssetR002.entrustAmt'],assetType:['assetType1','EtrPlanAssetR002.assetType1'],data:obj}
        }else if(entrustNum){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustNum').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            var object = {tradeDirection:['ps','EtrPlanAssetR002.ps'],portfolioCode:['portfolioId','EtrPlanAssetR002.portfolioId'],tradeAmount:['entrustNum','EtrPlanAssetR002.entrustNum'],assetType:['assetType1','EtrPlanAssetR002.assetType1'],data:obj}
        }else if('T01'==ps){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            var object = {tradeDirection:['ps','EtrPlanAssetR002.ps'],portfolioCode:['portfolioId','EtrPlanAssetR002.portfolioId'],tradeAmount:['entrustAmt','EtrPlanAssetR002.entrustAmt'],assetType:['assetType1','EtrPlanAssetR002.assetType1'],data:obj}
        }else{
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustNum').val(),assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#etrInvestPersonInfo_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            var object = {tradeDirection:['ps','EtrPlanAssetR002.ps'],portfolioCode:['portfolioId','EtrPlanAssetR002.portfolioId'],tradeAmount:['entrustNum','EtrPlanAssetR002.entrustNum'],assetType:['assetType1','EtrPlanAssetR002.assetType1'],data:obj}
        }
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'EtrInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        //add wsz 修改 不显示业务模式 start
        if(invAims != null){
            $('#businessModel').textSelect('setValue', invAims);
        }
        //add wsz 修改 不显示业务模式 end
        return object;
    }
    /**
	 * 保存或提交
	 * 
	 * @param operateType
	 *            操作类型（save，submit）
	 * @returns
	 */
    function saveOrSubmit(operateType) {
        var s;
        if(typeof(BaseRiskMathComm) != 'undefined') {
            if (BaseRiskMathComm) {
                s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
                if (!s && s != '') {
                    return;
                }
                if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())) {
                    return;
                }
            }
        }
        /**
		 * 获取列表参数
		 */
        var object = $.serializeToObject('#searchForm');
        if ($('#select2-finprodId-container').attr('title')){
            object.finprodName = $('#select2-finprodId-container').attr('title');
        }
        object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.entrustId = $('#entrustId').val();
        object.finprodId =$('#finprodIdhid').val();
        object.finvdate = $('#finvdate').val();
        object.finmdate = $('#finmdate').val();
        object.entrustStatus = $('#entrustStatus').val();
        if(object.etrInvestPersonInfo.trader != null && object.etrInvestPersonInfo.trader != ''){
            object.etrInvestPersonInfo.trader = object.etrInvestPersonInfo.trader.join(",");
        }
        if ($('#etrInvestPersonInfo_counterId').select2('data') && $('#etrInvestPersonInfo_counterId').select2('data')[0]) {
            object.counterType = $('#etrInvestPersonInfo_counterId').select2('data')[0].object;;
        }
        if(FileCommon) {
            object.fileArray = FileCommon.getUploadFileInfo();
            if(object.fileArray && _.isArray(object.fileArray)){
                $.each(object.fileArray, function(index,obj){
                    obj.seqNo = obj.fileNo;
                });
            }
        }
        if(!$('#businessModel').val()){
            Joyin.error("请输入业务模式");
            return;
        }

        //指令暂存，去除校验  add by xxg 20210802
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath+'invest/entrust/etrplanassetr/EtrPlanAssetR002/save',object,"EtrPlanAssetR002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', object, function(result1) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetr/EtrPlanAssetR002/checkAuthorization', object, function(result2) {
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetr/EtrPlanAssetR002/check',object,
                        function(result) {
                            Joyin.confirm(result.data,function() {
                                WorkFlow.doSubmitList(basePath+'invest/entrust/etrplanassetr/EtrPlanAssetR002/save',object,"EtrPlanAssetR002_btn_submit");
                            });
                        },null,function (result) {
                            var disabledFlag = $('#invAim').attr('disabled');
                            if(disabledFlag) return;
                            var msg = result.errorList;
                            var message ="";
                            for(var i=0;i<msg.length;i++){
                                message = msg[i];
                                if(message.field!=null && message.field=='invAim'){
                                    if(message.message!=null && message.message==JoyinUtil.getMessage("ETR_ERROR_0059")) {
                                        $('#businessModel').textSelect('setValue', "FVTPL");
                                        break;
                                    }
                                }
                            }

                        });
                });
            });
        }
    }
    
 var changeBussnissModelByFinprodId = function(){
     var finprodId = $('#finprodId').val();
     if(finprodId){
          var data = {finprodId:finprodId }
          Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/getAssetInfoBySPPI',data,function(result) {
                if(result.data=='0') {
                     Joyin.error("当前资产未进行SPPI测试，无法进行指令提交/保存。");
                     $('#finprodId').val("").trigger('change');
                     $("#businessModel").attr("disabled",false);
                     $('#businessModel').val("").trigger('change');
                 }else if(result.data=='3'){
                     $('#businessModel').val("FVTPL").trigger('change');
                     $("#businessModel").attr("disabled",true);
                 }else if(result.data=='2') {
                      $("#businessModel").attr("disabled",false);
                      $('#businessModel').val("").trigger('change');
                 }
           });
     }else{
          $("#businessModel").attr("disabled",false);
          $('#businessModel').val("").trigger('change');
     }

 }

  // 查询组合可用现金
    var getPtlCashPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),vdate:$('#vdate').val(),calId:$('#calendarId').val(), chlId:$("#chlId").val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetr/EtrPlanAssetR002/getProAvailableCash',data,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');
            $('#proAvailableCash1').val(result.cashT1).trigger('blur');
            $('#accAvailableCash').val(result.cashTG).trigger('blur');
            $('#channelAvailableCash').val(result.cashTD).trigger('blur');
        });
    }  
    // 查询组合可用持仓
    var getPtlSecPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),finprodId:$('#finprodIdhid').val(),ccy:$('#ccy').val(), secAcctId:$("#chlId").val(),invAim:$("#businessModel").val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetr/EtrPlanAssetR002/getAssetAvailablePosition',data,
                function(result) {
            $('#assetAvailablePosition').val(result).trigger('blur');;
        });
    }
    // 添加风险试算验证
    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskparam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskparam);
            }
        }
    }
    /**
	 * 绑定事件
	 */
    $(function() { 

        var init = function(){
            $('#ps').change();
            if ($('#finprodId').val()){
                $('#finprodId').change();
            }
        }
        /**
		 * 保存
		 */
        $('#EtrPlanAssetR002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
		 * 提交
		 */
        $('#EtrPlanAssetR002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
		 * 风险试算
		 */
        $('#EtrPlanAssetR002_btn_riskcal').click(function(){
            // window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            var object = $.serializeToObject('#searchForm');
            if($('#entrustNum').val()) {
                object.entrustAmt =$('#entrustNum').val();//把份额直接赋值给金额
            }
            object.finprodId =$('#finprodIdhid').val();
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F03',object,getriskdata());
        });

        //根据参数 选择显示投资目的或者业务模式
        // if(SHOW_INVAIM_OR_BUSSINSSMODEL=="02"){
        //    $("#invAimDiv").hide();
        //    $("#businessModelDiv").show();
        // }else{
        //    $("#invAimDiv").show();
        //    $("#businessModelDiv").hide();
        // }

        $('#businessModel').change(function () {
            var ps= $('#ps').val();
            if(ps == T06){ //卖出时选完业务模式更新持仓
                getPtlSecPositionInfo();
            }
        })

        /**
		 * 交易方向
		 */
        $('#ps').change(function() {
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodIdhid = $('#finprodIdhid').val();
                if(ps == T04){
                    $('#proAvailableCash_div').show();
                    $('#proAvailableCash1_div').show();
                    // $('#accAvailableCash_div').show();
                    //隐藏合作资产可用现金
                    // $('#div_channelCash').show();
                    $('#entrustAmtDiv').show();
                    $('#entrustNumDiv').hide();
                    $('#entrustAmt').attr('disabled',false);
                    $('#entrustNum').attr('disabled',true);
                    $('#entrustNum').val("");
                    $('#assetAvailablePosition_div').hide();
                    if(ccy && portfolioId && vdate){
                        getPtlCashPositionInfo();
                    }
                }else if(ps == T06){
                    $('#proAvailableCash_div').hide();
                    $('#proAvailableCash1_div').hide();
                    // $('#accAvailableCash_div').hide();
                    //隐藏合作资产可用现金
                    // $('#div_channelCash').hide();
                    $('#entrustAmtDiv').hide();
                    $('#entrustNumDiv').show();
                    $('#entrustAmt').attr('disabled',true);
                    $('#entrustAmt').val("");
                    $('#entrustNum').attr('disabled',false);

                    $('#assetAvailablePosition_div').show();
                    if(portfolioId && finprodIdhid){
                        getPtlSecPositionInfo();
                    }
                }
        });
        /**
		 * 资产代码
		 */
        $('#finprodId').on('change', function () {
            var finprodId = $('#finprodId').val();
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodIdhid = $('#finprodIdhid').val();
            if(finprodId){
                var data = $('#finprodId').select2('data')[0].object;
                $('#finprodIdhid').val(data.finprodId).trigger('change');
                $('#finprodName').val($('#finprodId').val()).trigger('change');
                $('#calendarId').val(data.calendarId).trigger('change');
                $('#finvdate').val(new Date(data.finvdate)).trigger('change');
                $('#finmdate').val(new Date(data.finmdate)).trigger('change');
                $('#ccy').val(data.ccy).trigger('change');
                $('#assetType1').val(data.assetType1).trigger('change');
                <!--增加，3,4、zhuyongkang 2019/7/29  start -->
                $('#assetType10').val(data.assetType10).trigger('change');
                $('#assetType11').val(data.assetType11).trigger('change');
                <!--增加，3,4、zhuyongkang 2019/7/29  end -->

            }else {
                $('#finprodIdhid').val('').trigger('change');
                $('#finprodName').val('').trigger('change');
                $('#calendarId').val('').trigger('change');
                $('#finvdate').val('').trigger('change');
                $('#finmdate').val('').trigger('change');
                $('#ccy').val('').trigger('change');
                $('#assetType1').val('').trigger('change');
                <!--、zhuyongkang 2019/7/29  start -->
                $('#assetType10').val('').trigger('change');
                $('#assetType11').val('').trigger('change');
                <!--、zhuyongkang 2019/7/29  end -->
            }
            //增加业务模式的联动处理
            if(pageModel==PAGE_MODEL_NEW||pageModel==PAGE_MODEL_EDIT)
                changeBussnissModelByFinprodId();
            //增加业务模式的联动处理
            ChannelAssetCommon.setChannelSelectObject($("#chlId"), data.finprodId, false, function () {
            });
        });

        $("#chlId").change(function () {
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            if(ps == T04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
            if(ps == T06 && portfolioId){
                getPtlSecPositionInfo();
            }
        })

        $('#ccy').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodIdhid = $('#finprodIdhid').val();
            if(ps == T04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
            if(ps == T06 && ccy && portfolioId && finprodIdhid){
                getPtlSecPositionInfo();
            }
        });
        
        $('#portfolioId').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodIdhid = $('#finprodIdhid').val();
            if(ps == T04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }else if(ps == T06 && portfolioId && finprodIdhid){
                getPtlSecPositionInfo();
            }
        });
        
        $('#vdate').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var finprodIdhid = $('#finprodIdhid').val();
            if(ps == T04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
        });
        // 是否展示资产四五级分类
        if(IS_SHOW_TYPE == '01'){
        	$('#assetType11Div').show();
        }else{
        	$('#assetType11Div').hide();
        }
        init();
    });


    /**
     * 查看资产详情事件
     */
    $('#EtrPlanAssetR002_btn_search').click(function() {
        var finprodId =$('#finprodIdhid').val();
        var url = "/invest/asset/plan/planProfit/FinPlanProfitAsset002/init";
        if (finprodId != ""){
            var params = {
                'pageModel':PAGE_MODEL_REFERENCE,
                'finprodId':finprodId,
                'isQuickInput':true
            };
            EtrShowFinPlanAssetDetailInfo.searchDetail(params,url);
        } else {
            Joyin.error("资产名称不能为空")
        }
    });

}());
