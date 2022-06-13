/**
 * 买断式回购投资委托新建
 */
(function() {
    if(entrustAmtUnit == '01' ){
        $(".unitNum").text('元');
    }
    var getriskdata = function () {
        var ps = $('#ps').val();
        var object={};
        var obj={};
        //配置单位 edit by yeming 20210721 start
        var entrustAmt=$('#entrustAmt').val();
        if (entrustAmt) {
            if(entrustAmtUnit == '02' ){
                entrustAmt=JoyinMath.doMul(entrustAmt, 10000, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
            }
        }
        //配置单位 edit by yeming 20210721 end
        if(ps == ps_03){
             obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustAmt,assetType:fintype_zhg,isEnabled:'01'}
             object = {tradeDirection:['ps','EtrBoutRepo002.ps'],portfolioCode:['portfolioId','EtrBoutRepo002.portfolioId'],tradeAmount:['entrustAmt','EtrBoutRepo002.entrustAmt'],assetType:['assetType1','EtrBoutRepo002.assetType1'],data:obj}
        }else if(ps == ps_04){
             obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustAmt,assetType:fintype_nhg,isEnabled:'01'}
             object = {tradeDirection:['ps','EtrBoutRepo002.ps'],portfolioCode:['portfolioId','EtrBoutRepo002.portfolioId'],tradeAmount:['entrustAmt','EtrBoutRepo002.entrustAmt'],assetType:['assetType1','EtrBoutRepo002.assetType1'],data:obj}
        }else{
             obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustAmt,assetType:fintype_zhg,isEnabled:'01'}
             object = {tradeDirection:['ps','EtrBoutRepo002.ps'],portfolioCode:['portfolioId','EtrBoutRepo002.portfolioId'],tradeAmount:['entrustAmt','EtrBoutRepo002.entrustAmt'],assetType:['assetType1','EtrBoutRepo002.assetType1'],data:obj}
        }
        return object;
    }

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
     * 
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function saveOrSubmit(operateType) {
        var s;
        if(typeof(BaseRiskMathComm) != 'undefined') {
        if(BaseRiskMathComm) {
            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
            if(!s && s != ''){
                return;
            }
            if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
                return;
                }
            }
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm');
        object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.entrustId = $('#entrustId').val();
        object.finprodId =$('#finprodIdhid').val();
        object.entrustStatus = $('#entrustStatus').val();
        object.finvdate = $('#finvdate').val();
        object.finmdate = $('#finmdate').val();
        if($('#entrustAmt').val()){
            object.entrustAmt = JoyinMath.doMul($('#entrustAmt').val(), entrustAmtUnit == '02' ? 10000 : 1, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
        }
        if(object.etrInvestPersonInfo.trader != null && object.etrInvestPersonInfo.trader != ''){
            object.etrInvestPersonInfo.trader = object.etrInvestPersonInfo.trader.join(",");
        }
        if(FileCommon) {
            object.fileArray = FileCommon.getUploadFileInfo();
            if(object.fileArray && _.isArray(object.fileArray)){
                $.each(object.fileArray, function(index,obj){
                    obj.seqNo = obj.fileNo;
                });
            }
        }
        object.inputValue1 = PINAN_ASSET_TYPE_F10;
        object.inputValue2 = $('#ps').val();
        //保存跳过校验 edit by yeming 20210915 start
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath+'invest/entrust/etrboutrepo/EtrBoutRepo002/save',object,"EtrBoutRepo002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/etrboutrepo/EtrBoutRepo002/checkAuthorization', object, function(result2) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrboutrepo/EtrBoutRepo002/check',object,
                    function(result) {
                        Joyin.confirm(result.data,function() {
                            // if (operateType === 'save') {
                            //     WorkFlow.doSaveList(basePath+'invest/entrust/etrboutrepo/EtrBoutRepo002/save',object,"EtrBoutRepo002_btn_save");
                            // } else {
                                WorkFlow.doSubmitList(basePath+'invest/entrust/etrboutrepo/EtrBoutRepo002/save',object,"EtrBoutRepo002_btn_submit");
                            // }
                        });
                    });
            });
            //保存跳过校验 edit by yeming 20210915 end
        }
    }
  //查询组合可用现金
    var getPtlCashPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),vdate:$('#vdate').val(),
                    calId:$('#calendarId').val(),chlFinprodId:$('#chlId').val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/getProAvailableCash',data,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');
            $('#proAvailableCash1').val(result.cashT1).trigger('blur');
            $('#accAvailableCash').val(result.cashT3).trigger('blur');
            if ($("#chlId").val()){
                if (result.payType == '02'){
                    $('#channelAvailableCash').val(result.cashT2);
                    $("#chlId_value_div").show();
                    return;
                }
                $("#chlId_value_div").hide();
            }
            else{
                $("#chlId_value_div").hide();
            }
        });
    }  
    //查询标准券额度
//    var getPtlSecPositionInfo = function(){
//        var data = {portfolioId:$('#portfolioId').val(),manageId:$("#securitiesCust").val()}
//        Joyin.ajaxJson(basePath + 'invest/entrust/etrboutrepo/EtrBoutRepo002/selectStandardAmt',data,
//                function(result) {
//            $('#assetAvailablePosition').val(result).trigger('blur');
//        });
//    }
  //查询组合可用持仓
    var getPtlSecPositionInfo = function(){
        var data = {portfolioId:$('#portfolioId').val(),
            finprodId:$('#finprodIdhid').val(),
            ccy:$('#ccy').val(), 
            secAcctId:$('#securitiesCust').val()?$('#securitiesCust').val(): $('#chlId').val(), 
            entrust: true, 
            channel: $('#chlId').val()? true: false}
        if (!data.portfolioId || !data.finprodId) {
            return;
        }
        Joyin.ajaxJson(basePath + 'invest/entrust/etrplanassetn/EtrPlanAssetN002/getAssetAvailablePosition',data,
                function(result) {
            $('#assetAvailablePosition').val(result).trigger('blur');;
        });
    }
    /**
     * 判断是否节假日
     */
    var judgeIsHday = function(){
        object = {mdateAdd:$('#mdateAdd').val(),calId:$('#calendarId').val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrboutrepo/EtrBoutRepo002/judgeIsHday',object,
          function(result) {
            if(result.msg){
                Joyin.alert(result.msg);
                $('#mdateAdd').val(result.mdateAdd2);
            }
        });
    }
    
    window.componentLoadCallback = function (isDone, pagePath, params) {
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
        	var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                  riskparam.data =null;
            }
            window.BaseRiskMathComm.getparam(riskparam);
            }
            $('#ps').change();
            $('#finprodId').change();
            $('#termType').change();
            $('#settleSpreed').change();
        }

        //回显通道可用现金
        if (payType == '02'){
            $('#chlId_value_div').show()
            if (cash)
                $('#channelAvailableCash').val(cash)
        }
    };
    /**
     * 弹出添加画面
     * @returns
     */
    function showDialog() {
        Joyin.dialog(JoyinUtil.getLabel('EtrBoutRepo002.addPledge'), '../TrdRepoTrade003/init', '90%', '90%');
    }
    /**
     * 绑定事件
     */
    $(function() {

        /**
         * 保存
         */
        $('#EtrBoutRepo002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
         * 提交
         */
        $('#EtrBoutRepo002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
         * 风险试算
         */
        $('#EtrBoutRepo002_btn_riskcal').click(function(){

            var objectForm = $.serializeToObject('#searchForm');
            objectForm.finprodId =$('#finprodIdhid').val();
            var entrustAmt=$('#entrustAmt').val();
            if (entrustAmt) {
                if(entrustAmtUnit == '02' ){
                    entrustAmt=JoyinMath.doMul(entrustAmt, 10000, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
                }
            }
            objectForm.entrustAmt = entrustAmt;
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F10',objectForm,getriskdata());
        });

         /**
          * 查询通道可用现金
          */
         $("#chlId").change(function(){
             if ($("#chlId").val()) {
                 $("#securitiesCust").val('').prop("disabled", true);
             }
             else {
                 $("#securitiesCust").prop("disabled", false);
             }
             //资产可用持仓刷新
             getPtlSecPositionInfo();
             getPtlCashPositionInfo();
         })
         function queryChlCash(object){
             Joyin.ajaxJson(basePath + 'invest/entrust/etrrepo/EtrRepo002/queryChlCash', object,
                 function(result) {
                     if (result.payType == '02'){
                         $('#channelAvailableCash').val(result.cash)
                         $("#chlId_value_div").show();
                         return;
                     }
                     $("#chlId_value_div").hide();
                 });
         }

        /**
         * 交易方向
         */
        $('#ps').change(function() {
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var calendarId = $("#calendarId").val();
            var vdate = $('#vdate').val();
            var securitiesCust = $('#securitiesCust').val();
                if(ps == ps_04){
                    $('#proAvailableCash_div').show();
                    $('#proAvailableCash1_div').show();
                    // $('#accAvailableCash_div').show();
                    $('#assetAvailablePosition_div').hide();
                    if(ccy && portfolioId && vdate){
                        getPtlCashPositionInfo();
                    }
                }else if(ps == ps_03){
                    $('#proAvailableCash_div').hide();
                    $('#proAvailableCash1_div').hide();
                    // $('#accAvailableCash_div').hide();
                    $('#assetAvailablePosition_div').show();
                    if(portfolioId && securitiesCust){
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
                $('#finprodName').val(data.finprodName).trigger('change');
                $('#finvdate').val(new Date(data.finvdate)).trigger('change');
                $('#finmdate').val(new Date(data.finmdate)).trigger('change');
                $('#ccy').val(data.ccy).trigger('change');
            }
            if(ps == ps_04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
            if(ps == ps_03 && portfolioId){
                getPtlSecPositionInfo();
            }
        });
        
        $('#ccy').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            var securitiesCust = $('#securitiesCust').val();
            if(ps == ps_04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
            if(ps == ps_03 && ccy && portfolioId && securitiesCust ){
                getPtlSecPositionInfo();
            }
        });
        
        $('#portfolioId').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            $("#securitiesCust").val('').trigger("change");
            var securitiesCust = $('#securitiesCust').val();
            var vdate = $('#vdate').val();
            if(ps == ps_04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }else if(ps == ps_03 && portfolioId && securitiesCust){
                getPtlSecPositionInfo();
            }
        });
        
        $('#vdate').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var vdate = $('#vdate').val();
            if(ps == ps_04 && ccy && portfolioId && vdate){
                getPtlCashPositionInfo();
            }
        });
        
        $('#calendarId').change(function(){
            var ps= $('#ps').val();
            var ccy = $('#ccy').val();
            var portfolioId = $('#portfolioId').val();
            var calendarId = $("#calendarId").val();
            if(ps == ps_04 && ccy && portfolioId){
                getPtlCashPositionInfo();
            }
            
            var mdateAdd = $('#mdateAdd').val();
            if(mdateAdd && calendarId){
                judgeIsHday();
            }
        });
        
        $('#tradeMarket').change(function(){
        	//add by gaopeng 20191112 选择交易场所时清空证券账户  start
        	$("#securitiesCust").val('').trigger('change');
        	//add by gaopeng 20191112 选择交易场所时清空证券账户  end
            var tradeMarket = $('#tradeMarket').val();
            var assetType1 = $('#assetType1').val();
            if(tradeMarket == tradeP02 || tradeMarket == tradeP03){
                if(assetType1 == repoTypeH01){
                    $('#termType_div').hide();
                }else{
                    $('#termType_div').show();
                }
                $('#settleSpreed').val(speed1).attr('disabled',true).trigger('change');
            }else if(tradeMarket == tradeP01){
                $('#settleSpreed').val(speed0).attr('disabled',false).trigger('change');
                $('#termType_div').show();
            }
        });
        
        $('#settleSpreed').change(function(){
            var settleSpreed = $('#settleSpreed').val();
            if(settleSpreed == speed1){
                $('#availableDay_div').show();
            }else{
                $('#availableDay_div').hide();
            }
        });
        
        $('#repoTerm').change(function(){
            var repoTerm = $('#repoTerm').val();
            var repoTerm2 = $('#repoTerm2').val();
            if(repoTerm && repoTerm2 && Number(repoTerm) == Number(repoTerm2)){
                var vdateAdd = $('#vdateAdd').val();
                if(vdateAdd){
                    var d1 = new Date(vdateAdd).getTime();
                    var day = parseInt($('#repoTerm').val()*24*60*60*1000);
                    var mdateAdd = new Date(d1+day);
                    $('#mdateAdd').val(mdateAdd.format("yyyy-MM-dd")).change();
                }
            }else if(repoTerm && repoTerm2 && Number(repoTerm) > Number(repoTerm2)){
            	Joyin.error('回购期限（左）不能大于回购期限（右）');
        		$('#repoTerm').val('');
            }
        });
        
        $('#repoTerm2').change(function(){
        	var repoTerm = Number($('#repoTerm').val());
            var repoTerm2 = Number($('#repoTerm2').val());
        	if(repoTerm && repoTerm2){
        		if(repoTerm == repoTerm2){
        			$('#repoTerm').change();
        		}else if(repoTerm < repoTerm2){
            		$('#vdateAdd').val('');
            		$('#mdateAdd').val('');
            	}else if(repoTerm > repoTerm2){
            		Joyin.error('回购期限（右）不能小于回购期限（左）');
            		$('#repoTerm2').val('');
            	}
        	}
        });
        
        $('#assetType1').change(function(){
            var assetType1 = $('#assetType1').val();
            var tradeMarket = $('#tradeMarket').val();
            if(assetType1 == repoTypeH01){
                if(tradeMarket == tradeP02 || tradeMarket == tradeP03){
                    $('#termType_div').hide();
                }else{
                    $('#termType_div').show();
                }
            }else{
                $('#termType_div').show();
            }
        });
        
        $('#vdateAdd').change(function(){
            var vdateAdd = $('#vdateAdd').val();
            var repoTerm = $('#repoTerm').val();
            var mdateAdd = $('#mdateAdd').val();
            var repoTerm2 = $('#repoTerm2').val();
            if(vdateAdd && repoTerm && repoTerm2 && repoTerm == repoTerm2 ){
                var d1 = new Date(vdateAdd).getTime();
                var day = parseInt($('#repoTerm').val()*24*60*60*1000);
                var mdateAdd = new Date(d1+day);
                $('#mdateAdd').val(mdateAdd.format("yyyy-MM-dd")).change();
            }
            if(vdateAdd && mdateAdd){
                if(new Date(vdateAdd).getTime()>new Date(mdateAdd).getTime()){
                    Joyin.error("起息日不能大于到期日");
                    $("#vdateAdd").val('');
                    return;
                }
                var datev = new Date(vdateAdd).getTime();
                var datem = new Date(mdateAdd).getTime();
                var total = (datem-datev)/1000;
                var day = parseInt(total / (24*60*60));
                $('#repoTerm').val(day);
                $('#repoTerm2').val(day);
            }
            if(!vdateAdd){
            	$('#mdateAdd').val('');
            }
        });
        
        $('#mdateAdd').change(function(){
        	var startdate = $('#vdateAdd').val();
        	var enddate = $('#mdateAdd').val();
        	var repoTerm = $('#repoTerm').val();
        	var repoTerm2 = $('#repoTerm2').val();
        	if(startdate){
        		var vdateAdd = new Date($('#vdateAdd').val());
        	}
            if(enddate){
            	var mdateAdd = new Date($('#mdateAdd').val());
            }
            var calId = $('#calendarId').val();
            if(mdateAdd && calId){
                judgeIsHday();
            }
            if(vdateAdd && mdateAdd){
                if(vdateAdd.getTime()>mdateAdd.getTime()){
                    Joyin.error("到期日不能小于起息日");
                    $("#mdateAdd").val('');
                    return;
                }
                var datev = new Date(vdateAdd).getTime();
                var datem = new Date(mdateAdd).getTime();
                var total = (datem-datev)/1000;
                var day = parseInt(total / (24*60*60));
                $('#repoTerm').val(day);
                $('#repoTerm2').val(day);
            }else if(mdateAdd && repoTerm && repoTerm2 && repoTerm==repoTerm2){
            	var d1 = new Date(mdateAdd).getTime();
                var day = parseInt($('#repoTerm').val()*24*60*60*1000);
                var vdateAdd = new Date(d1-day);
                $('#vdateAdd').val(vdateAdd.format("yyyy-MM-dd")).change();
            }
        });
        
        $('#securitiesCust').change(function(){
            var ps= $('#ps').val();
            var portfolioId = $('#portfolioId').val();
            var securitiesCust = $('#securitiesCust').val();
            if (securitiesCust) {
                $("#chlId").val('').prop("disabled", true);
            }
            else {
                $("#chlId").prop("disabled", false);
            }
            if(ps == ps_03 && portfolioId && securitiesCust){
                getPtlSecPositionInfo();
            }
        });
        
        $('#termType').change(function(){
        	if(pageModel != PAGE_MODEL_REFERENCE){
                var termType = $('#termType').val();
                if(termType == GC001){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(1).attr('disabled',false).change();
                    $('#repoTerm2').val(1).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC002){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(2).attr('disabled',false).change();
                    $('#repoTerm2').val(2).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC003){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(3).attr('disabled',false).change();
                    $('#repoTerm2').val(3).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC004){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(4).attr('disabled',false).change();
                    $('#repoTerm2').val(4).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC007){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(7).attr('disabled',false).change();
                    $('#repoTerm2').val(7).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',true);
                }else if(termType == GC014){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(14).attr('disabled',false).change();
                    $('#repoTerm2').val(14).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC028){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(28).attr('disabled',false).change();
                    $('#repoTerm2').val(28).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC091){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(91).attr('disabled',false).change();
                    $('#repoTerm2').val(91).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == GC182){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(182).attr('disabled',false).change();
                    $('#repoTerm2').val(182).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R001){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(1).attr('disabled',false).change();
                    $('#repoTerm2').val(1).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R002){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(2).attr('disabled',false).change();
                    $('#repoTerm2').val(2).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R003){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(3).attr('disabled',false).change();
                    $('#repoTerm2').val(3).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R004){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(4).attr('disabled',false).change();
                    $('#repoTerm2').val(4).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R007){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(7).attr('disabled',false).change();
                    $('#repoTerm2').val(7).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R014){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(14).attr('disabled',false).change();
                    $('#repoTerm2').val(14).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R021){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(21).attr('disabled',false).change();
                    $('#repoTerm2').val(21).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == RC001){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(1).attr('disabled',false).change();
                    $('#repoTerm2').val(1).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == RC003){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(3).attr('disabled',false).change();
                    $('#repoTerm2').val(3).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == RC007){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(7).attr('disabled',false).change();
                    $('#repoTerm2').val(7).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_001){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(1).attr('disabled',false).change();
                    $('#repoTerm2').val(1).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_002){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(2).attr('disabled',false).change();
                    $('#repoTerm2').val(2).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_003){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(3).attr('disabled',false).change();
                    $('#repoTerm2').val(3).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_004){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(4).attr('disabled',false).change();
                    $('#repoTerm2').val(4).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_007){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(7).attr('disabled',false).change();
                    $('#repoTerm2').val(7).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_014){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(14).attr('disabled',false).change();
                    $('#repoTerm2').val(14).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_028){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(28).attr('disabled',false).change();
                    $('#repoTerm2').val(28).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_091){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(91).attr('disabled',false).change();
                    $('#repoTerm2').val(91).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R_182){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(182).attr('disabled',false).change();
                    $('#repoTerm2').val(182).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R1M){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(30).attr('disabled',false).change();
                    $('#repoTerm2').val(30).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R2M){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(60).attr('disabled',false).change();
                    $('#repoTerm2').val(60).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R3M){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(90).attr('disabled',false).change();
                    $('#repoTerm2').val(90).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R4M){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(120).attr('disabled',false).change();
                    $('#repoTerm2').val(120).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else if(termType == R6M){
                	$('#repoTerm').val('').attr('disabled',false).change();
                    $('#repoTerm2').val('').attr('disabled',false).change();
                    $('#repoTerm').val(180).attr('disabled',false).change();
                    $('#repoTerm2').val(180).attr('disabled',false).change();
                    $('#mdateAdd').attr('disabled',false);
                }else{
//                	$('#repoTerm').val('').attr('disabled',true).change();
//                    $('#repoTerm2').val('').attr('disabled',true).change();
                    $('#mdateAdd').attr('disabled',false);
                    $('#repoTerm').attr('disabled',false);
                    $('#repoTerm2').attr('disabled',false);
                }
        	}
        });
        /**
         * 价格模式
         */
        $('#priceModel').change(function(){
        	if($('#priceModel').val() == PRICEMODEL_03){
                $('#price').val('').attr('disabled',true);
                $('#firstprice').val('').attr('disabled',true);
                $('#firstprice2').val('').attr('disabled',true);
                $('#firstprice').removeAttr("required");
                $('#price').removeAttr("required");
                $('#firstprice2').removeAttr("required");
            }else{
            	$('#price').attr('disabled',false);
            	$('#firstprice').attr('disabled',false);
            	$('#firstprice2').attr('disabled',false);
                $('#price').attr("required", "true");
                $('#firstprice').attr("required", "true");
                $('#firstprice2').attr("required", "true");
            }
            star();
        });
    });
}());
