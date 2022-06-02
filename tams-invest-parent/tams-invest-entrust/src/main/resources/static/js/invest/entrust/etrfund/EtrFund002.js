/**
 * 基金委托新建
 */
(function() {
    /**
     * 初始化界面标识
     * @type {boolean}
     */
    var initFlag = true;
    var getriskdata = function () {
        var entrustAmt = $('#entrustAmt').val();
        var entrustNum = $('#entrustNum').val();
        var ps = $('#ps').val();
        var object={};
        if(ps == T04||ps == T52){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustAmt,assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#bussEtrInvestPersonInfoDto_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            object = {assetCode: ['finprodMarketId', 'EtrFund001.finprodId'],tradeDirection:['ps','EtrFund001.ps'],portfolioCode:['portfolioId','EtrFund001.portfolioId'],tradeAmount:['entrustAmt','EtrFund001.entrustAmt'],assetType:['assetType1','EtrFund001.finprodType2'],data:obj}
        }else if(($("#ps").val() == T06 || $("#ps").val() == T65)){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustNum,assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val(),amtType:'1'}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#bussEtrInvestPersonInfoDto_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            object = {assetCode: ['finprodMarketId', 'EtrFund001.finprodId'],tradeDirection:['ps','EtrFund001.ps'],portfolioCode:['portfolioId','EtrFund001.portfolioId'],tradeAmount:['entrustNum','EtrFund001.entrustNum'],assetType:['assetType1','EtrFund001.finprodType2'],data:obj}
        }else if(ps == T01){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustAmt,assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#bussEtrInvestPersonInfoDto_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            object = {assetCode: ['finprodMarketId', 'EtrFund001.finprodId'],tradeDirection:['ps','EtrFund001.ps'],portfolioCode:['portfolioId','EtrFund001.portfolioId'],tradeAmount:['entrustAmt','EtrFund001.entrustAmt'],assetType:['assetType1','EtrFund001.finprodType2'],data:obj}
        }else if (ps == T02) {
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:entrustNum,assetType:fintype,isEnabled:'01',assetCode:$('#finprodIdhid').val(),amtType:'1'}
            // add by zhangrui1 交易对手影响风险试算 start
            obj.counterId = $('#bussEtrInvestPersonInfoDto_counterId').val();
            // add by zhangrui1 交易对手影响风险试算 end
            object = {assetCode: ['finprodMarketId', 'EtrFund001.finprodId'],tradeDirection:['ps','EtrFund001.ps'],portfolioCode:['portfolioId','EtrFund001.portfolioId'],tradeAmount:['entrustNum','EtrFund001.entrustNum'],assetType:['assetType1','EtrFund001.finprodType2'],data:obj}
        }
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'BussEtrInvestPersonInfoDto.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
        return object;
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
        var tradeMarket = $("#tradeMarket").val();
        var changeToFinprodMarketId = $("#changeToFinprodMarketId").val();
        var ps = $("#ps").val();
        if (ps == T65 && tradeMarket && tradeMarket != TRADE_PLACE_06) {
            Joyin.error("只有场外基金才能做转换。");
            return;
        }
        if (ps == T65 && changeToFinprodMarketId=='' ) {
            Joyin.error("转入资产代码不能为空。");
            return;
        }
        
        if ($('#changeToFinprodMarketId').val() && $('#changeToFinprodMarketId').select2('data')[0].object) {
            tradeMarket = $('#changeToFinprodMarketId').select2('data')[0].object.tradeMarket
            if (ps == T65 && tradeMarket && tradeMarket != TRADE_PLACE_06) {
                Joyin.error("转入基金是场外基金才能做转换。");
                return;
            }

            var issuer1 = $('#finprodMarketId').select2('data')[0].object.issuer;
            var issuer = $('#changeToFinprodMarketId').select2('data')[0].object.issuer;
            if (issuer1 && issuer && issuer1 != issuer) {
                Joyin.error("转入基金和转出基金必须属于同一管理人。");
                return;
            } 
        }

        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm',true);
        if ($('#select2-finprodMarketId-container').attr('title')){
            object.finprodName = $('#select2-finprodMarketId-container').attr('title');
        }
        if (object.bussEtrInvestPersonInfoDto){
            object.bussEtrInvestPersonInfoDto.invManagerId = $('#bussEtrInvestPersonInfoDto_invManagerId').val();
        }

        if(object.bussEtrInvestPersonInfoDto.trader != null && object.bussEtrInvestPersonInfoDto.trader != ''){
            object.bussEtrInvestPersonInfoDto.trader = object.bussEtrInvestPersonInfoDto.trader.join(",");
        }
        if(object.finprodMarketId != null && object.finprodMarketId != ''){
            object.finprodId = $('#finprodMarketId').select2('data')[0].object.finprodId;
        }
        if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data') && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
            object.counterType = $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object;;
        }
        if(FileCommon) {
            object.fileArray = FileCommon.getUploadFileInfo();
            if(object.fileArray && _.isArray(object.fileArray)){
                $.each(object.fileArray, function(index,obj){
                    obj.seqNo = obj.fileNo;
                });
            }
        }
        object.entrustAmt = $('#entrustAmt').val();
        object.entrustId = $("#entrustId").val();
        object.operateType = operateType;
        object.pageModel = pageModel;

        if ($('#changeToFinprodMarketId').val() && $('#changeToFinprodMarketId').select2('data')[0].object && $('#changeToFinprodMarketId').select2('data')[0].object.finprodId) {
            object.rFinprodId = $('#changeToFinprodMarketId').select2('data')[0].object.finprodId;
        }

        if(object.ps == T04 || object.ps == T01){
            object.proAvailableCash = $('#proAvailableCash').val();
        }
        if(object.ps != T04){
            object.assetAvailablePosition = $('#assetAvailablePosition').val();
        }
        if(parentPageId){
            object.parentPageId = parentPageId;
        }
        //指令保存，不校验要素
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath+'invest/entrust/etrfund/EtrFund002/save',object,"EtrFund002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/checkAuthorization', object, function(result2) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/check',object,
                    function(result) {
                        if(object.ps == T04 || object.ps == T01){
                            if($('#proAvailableCash').val() != null && $('#proAvailableCash').val() != '' && $('#entrustAmt').val() != null && $('#entrustAmt').val() != ''){
                                if(parseInt($('#entrustAmt').val()) > parseInt($('#proAvailableCash').val())&&ETR_CHECK_CASH_SWITCH == 01){
                                    Joyin.confirm("委托金额必须小于等于组合T日可用现金,确认保存或提交吗",function() {
                                        //Joyin.confirm(result.data,function() {
                                        WorkFlow.doSubmitList(basePath+'invest/entrust/etrfund/EtrFund002/save',object,"EtrFund002_btn_submit");
                                        //});
                                    },function(){
                                        return false;
                                    })
                                }
                                else{
                                    Joyin.confirm(result.data,function() {
                                        WorkFlow.doSubmitList(basePath+'invest/entrust/etrfund/EtrFund002/save',object,"EtrFund002_btn_submit");
                                    });
                                }
                            }
                        }
                        else{
                            Joyin.confirm(result.data,function() {
                                WorkFlow.doSubmitList(basePath+'invest/entrust/etrfund/EtrFund002/save',object,"EtrFund002_btn_submit");
                            });
                        }
                    });
            });
        }
    }
    
    //查询资产管理人
    var selectManeger = function(){
        if($('#finprodMarketId').val()){
            var data = $('#finprodMarketId').select2('data')[0].object;
            if(data){
                Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/selectManeger',{issuer:data.issuer},
                        function(result) {
                    //场内通道不存在时  需要默认
                    if(($("#tradeMarket").val()&&($("#tradeMarket").val()==TRADE_PLACE_03 ||$("#tradeMarket").val()==TRADE_PLACE_04))&&!$("#entrustTongdao").val()){
                        //无交易对手不默认，置灰后无法提交，要求管理人配为交易对手之后在进行委托
                        if(result&&Object.keys(result).length!=0){
                            $("#bussEtrInvestPersonInfoDto_counterId").textSelect('setValue',result.id);
                        }
                        if($('#finprodMarketId').select2('data')[0].object){
                            $("#bussEtrInvestPersonInfoDto_counterId").attr('disabled',true);
                        }else{
                            $("#bussEtrInvestPersonInfoDto_counterId").attr('disabled',false);
                        }
                    }
                });
            }
        }
    }
    

    
    //资产代码联动
    function changeInput(assetType1,tradeMarket,ccy){
//        $('#assetType1').val(assetType1).trigger('change');//基金类型改为资产二级分类 王贵奇 2019/7/29
        $('#tradeMarket').val(tradeMarket).trigger('change');
        $('#ccy').val(ccy).trigger('change');
    }
    //
    function changeDisabled(status){
        $('#assetType1').attr('disabled',status).trigger('change');
        $('#assetType10').attr('disabled',status).trigger('change');// 资产三级分类不可见    王贵奇    2019/7/29
        $('#assetType11').attr('disabled',status).trigger('change');// 资产四级分类不可见    王贵奇    2019/7/29
        //$('#tradeMarket').attr('disabled',status);
        $('#ccy').attr('disabled',status).trigger('change');
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
    //画面联动，资产代码带出资产名称
    $("#finprodMarketId").change(function(){
        var tradeMarket;
        var ccy;
        $("#purSpeed").val('').trigger('change');
        $("#redSpeed").val('').trigger('change');
        if($("#finprodMarketId").val()){
            var selectObject = $('#finprodMarketId').select2('data')[0].object;
            Joyin.ajaxJson(basePath + 'invest/trade/fund/TrdFundInvestTrade002/getSpeedByFinprodId', selectObject,function (result) {
                $("#purSpeed").val(result.PURSPEED).trigger('change');
                $("#redSpeed").val(result.REDSPEED).trigger('change');
            });
            if ($('#finprodMarketId').select2('data')[0].object) {
                $('#finprodIdhid').val(selectObject.finprodId);
                //新增资产二、三、四级分类    王贵奇    2019/7/29    开始    
                $('#assetType1').val(selectObject.assetType1).trigger('change');
                $('#assetType10').val(selectObject.assetType10).trigger('change');
                $('#assetType11').val(selectObject.assetType11).trigger('change');
                //新增资产二、三、四级分类    王贵奇    2019/7/29    结束    
                $('#trmFundAbbr').val(selectObject.trmFundAbbr).trigger('change');
                $('#trmFundType').val(selectObject.trmFundType).trigger('change');
            }else if ($('#finprodMarketId').select2('data')[0].id) {
                //自动 加载
                var autoCoupon = AutoCoupon.autoFundTradeCoupon($('#finprodMarketId').val());
                $('#finprodIdhid').val(autoCoupon.finprodId);
                tradeMarket = autoCoupon.tradeMarket;
                ccy = autoCoupon.ccy;
            }
        }
        /*if($("#ps").val()==T01 || $("#ps").val()==T02){
            $("#entrustNum").val('');
            $("#entrustAmt").val('');
        }*/
        $("#finprodName").val($('#finprodMarketId').select2('data')[0].id);
        if($("#finprodMarketId").val()&&$('#finprodMarketId').select2('data')[0].object&&$('#finprodMarketId').select2('data')[0].object.tradeMarket){
            $("#tradeMarket").attr("disabled",true);
        }else{
            $("#tradeMarket").attr("disabled",false);
        }
        if($("#finprodMarketId").val()&&$('#finprodMarketId').select2('data')[0].text){
            $("#finprodName").val($('#finprodMarketId').select2('data')[0].text);
        }else{
            $("#finprodName").val("")
        }
        var assetType1 = $('#finprodMarketId').select2('data')[0].col;
        
        if($('#finprodMarketId').select2('data')[0].object){
            tradeMarket = $('#finprodMarketId').select2('data')[0].object.tradeMarket;
            ccy = $('#finprodMarketId').select2('data')[0].object.ccy;
        }
        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
            changeInput(assetType1,tradeMarket,ccy);
            changeDisabled(true);
        }else{
            if(pageModel == PAGE_MODEL_NEW){
                changeInput('','','');
            }
//            changeDisabled(false);
            $('#assetAvailablePosition').val('').trigger('blur');
        }
        if($("#assetType1").val() == F01 && ($('#finprodMarketId').val() != null || $('#finprodMarketId').val() != '') && ($("#ps").val() == T06 || $("#ps").val() == T65)){
            $("#pschange2").show();
        }
        else if($("#ps").val() != T04 && $("#ps").val() != T01){
            $("#pschange2").show();
        }
        psChange(true);
        getProAvailableCash1();
        getAssetAvailablePosition();
        getPrice();
        getEntrustAmt();
    })
    $("#portfolioId").change(function(){
        getProAvailableCash();
        getProAvailableCash1();
        getAssetAvailablePosition();
        getProTongDaoCash();
    })
    $("#entrustTongdao").change(function(){
        TongDao();
        getProTongDaoCash();
        getProAvailableCash();
        getProAvailableCash1();
        getAssetAvailablePosition();
    })

    //获取组合可用持仓
    function getAssetAvailablePosition(){
        if(($("#portfolioId").val() != '' && $("#portfolioId").val() != null) && ($("#ccy").val() != '' && $("#ccy").val() != null)
                && ($("#finprodMarketId").val() != '' && $("#finprodMarketId").val() != null)){
            var secAcctId = $("#secAcctId").val();
            if (!secAcctId) {
                    secAcctId = $("#entrustTongdao").val();
            }
            var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),finprodId:$('#finprodMarketId').select2('data')[0].object.finprodId, secAcctId: secAcctId}
            Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/getAssetAvailablePosition',data,
                    function(result) {
                $('#assetAvailablePosition').val(result).trigger('blur');
            });
        }
    }
    //通道联动
    function TongDao(){
        if($("#entrustTongdao").val()==null||$("#entrustTongdao").val()==''){        
            $("#TongDaoNum2").hide();
        }
        else{
            $("#TongDaoNum2").show();
        }
    }
    
    //交易所资金账户联动
    function tradeMarketCashChange(flag){
        if($("#tradeMarket").val()&&($("#tradeMarket").val()==TRADE_PLACE_03 ||$("#tradeMarket").val()==TRADE_PLACE_04)
                &&$("#secAcctId").val()){
            $("#tradeMarketCash_div").show();
            var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),acctId:$("#secAcctId").val()}
            Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/getProAvailableCash',data,
                    function(result) {
                $('#tradeMarketCash').val(result.cashT4).trigger('blur');
            });
        }else{
            $("#tradeMarketCash_div").hide();
        }
        if(!flag){
            if($("#tradeMarket").val()&&($("#tradeMarket").val()==TRADE_PLACE_03 ||$("#tradeMarket").val()==TRADE_PLACE_04)&&!$("#entrustTongdao").val()){
                $("#secAcctId").attr("disabled",false);
            }else{
                $("#secAcctId").val('').attr("disabled",true).trigger('change');
            }
        }
    }

    //通道可用现金查询
     function getProTongDaoCash(){
        if($('#entrustTongdao').val() != null && $('#entrustTongdao').val() != '' ){
            if($("#portfolioId").val() && $("#ccy").val() && $("#entrustTongdao").val()){
                var data = {portfolioId:$('#portfolioId').val(),chlFinprodId:$("#entrustTongdao").val(), ccy: $("#ccy").val()}
                Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/getTongDaoCash',data,
                        function(result) {
                    $('#TongDaoNum').val(result).trigger('blur');
                });
            }
        }
    }
    
    //获取组合T+1日可用现金
    function getProAvailableCash1(){
        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != '' ){
            if(($("#portfolioId").val() != '' && $("#portfolioId").val() != null) && ($("#ccy").val() != '' && $("#ccy").val() != null)){
                var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),finprodId:$('#finprodMarketId').select2('data')[0].object.finprodId, chlFinprodId: $("#entrustTongdao").val()}
                Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/getProAvailableCash',data,
                        function(result) {
                    //$('#proAvailableCash1').val(result).trigger('blur');
                    $('#proAvailableCash1').val(result.cashT1).trigger('blur');
                });
            }
        }
    }
    //获取组合T日可用现金
    function getProAvailableCash(){
        if($("#portfolioId").val() != null && $("#portfolioId").val() !='' && $("#ccy").val() != null && $("#ccy").val() != ''){
            var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(), chlFinprodId: $("#entrustTongdao").val()}
            Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/getProAvailableCash',data,
                    function(result) {
                //$('#proAvailableCash').val(result).trigger('blur');
                $('#proAvailableCash').val(result.cashT).trigger('blur');
                $('#accAvailableCash').val(result.cashTG).trigger('blur');
                // ZGZGST-2077:展示交易所可用现金 fuyongsheng 20210904 add start
                $('#tradeMarketCash').val(result.cashT4).trigger('blur');
                // ZGZGST-2077:展示交易所可用现金 fuyongsheng 20210904 add end
            });
        }
    }
    //添加风险试算验证
    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskparam = getriskdata();
                if (typeof riskparam != 'undefined') {
                    if (pageModel == PAGE_MODEL_NEW) {
                        riskparam.data = null;
                    }
                    window.BaseRiskMathComm.getparam(riskparam);
                }
            }
            $("#bussEtrInvestPersonInfoDto_counterId").change(function(){
                getPrice();
                getEntrustAmt();
                star();
            });
            elementShowCtrl();
        }
    }

    //pschange
    function psChange(flag){
        if (!initFlag) {
            $("#changeToFinprodMarketId").val('').trigger('change');
        }
        //ZGBUGTWO-77 公募基金指令_新建窗口内部分字段回显异常以及按钮点击无反应，见截图 modify by lizhi 20200712 start
        $('#finprodMarketId').attr("required",true); //.trigger('change');
        $('#assetType1').attr("required",true);
        $('#assetType10').attr("required",true);
        $('#assetType11').attr("required",true);
        //ZGBUGTWO-77 公募基金指令_新建窗口内部分字段回显异常以及按钮点击无反应，见截图 modify by lizhi 20200712 end
        $(".changein").hide();
        //选择了交易方向，查询标的，符合条件选中，不符合清空
        if($("#finprodMarketId").val()&&!flag){
            var finprodMarketId =$("#finprodMarketId").val();
            $('#finprodMarketId option').remove();
            $("#finprodMarketId").textSelect('setValue',finprodMarketId);
        }
        //交易对手返回可选状态
        $("#bussEtrInvestPersonInfoDto_counterId").attr('disabled',false);
        if($("#ps").val()==T04){
            $(".pschange").show();
            // $("#pschange1").show();
            $("#pschange2").hide();
            $("#entrustAmt0").show();
            $("#entrustNum0").hide();
            $("#entrustAmt").attr("disabled",false);
            $("#entrustNum").val('')
            $('#assetAvailablePosition').val('').trigger('blur');
            $('#changeToFinprodMarketId_div').hide();
            $('#changeToFinprodMarketId').val('');
            getProAvailableCash();
            getProAvailableCash1();
            if($("#assetType1").val() != F02){
                $('#finprodMarketId').removeAttr("required");
                // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add start
                $('#assetType1').removeAttr("required");
                $('#assetType10').removeAttr("required");
                $('#assetType11').removeAttr("required");
                // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add end
                star();
            }
            if(!initFlag) {
                selectManeger();
            }
            if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data') && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                var 交易对手 =  $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object;
                if (交易对手 == 'N') {
                    $('#bussEtrInvestPersonInfoDto_counterId option').remove();
                }
            }
        }
        else if(($("#ps").val() == T06 || $("#ps").val() == T65)){
            $(".pschange").hide();
            // $("#pschange1").hide();
            if($("#assetType1").val() == F01 && ($('#finprodMarketId').val() == null || $('#finprodMarketId').val() == '')){
                $("#pschange2").hide();
            }
            else{
                $("#pschange2").show();
            }
            $("#entrustNum0").show();
            $("#entrustNum").attr("disabled",false);
            $("#entrustAmt0").hide();
            $("#entrustAmt").val('');
            $('#proAvailableCash').val('').trigger('blur');
            $('#proAvailableCash1').val('').trigger('blur');
            getAssetAvailablePosition();
            if($("#assetType1").val() != F02){
                $('#finprodMarketId').removeAttr("required");
                // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add start
                $('#assetType1').removeAttr("required");
                $('#assetType10').removeAttr("required");
                $('#assetType11').removeAttr("required");
                // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add end
                star();
            }
            if(!initFlag){
                selectManeger();
            }
            if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data') && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                var 交易对手 = $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object;
                if (交易对手 == 'N') {
                    $('#bussEtrInvestPersonInfoDto_counterId option').remove();
                }
            }
            if ($("#ps").val() == T65) {
                $(".changein").show();
                $("#changeToFinprodMarketId_div").show();
            }
            if ($("#ps").val() == T06) {
                $('#changeToFinprodMarketId_div').hide();
                $('#changeToFinprodMarketId').val('');
            }
        }
        else if($("#ps").val()==T52){
            $(".pschange").show();
            // $("#pschange1").show();
            $("#pschange2").hide();
            $("#entrustAmt0").show();
            $("#entrustNum0").hide();
            $("#entrustAmt").attr("disabled",false);
            $("#entrustNum").val('')
            $('#assetAvailablePosition').val('').trigger('blur');
            $('#changeToFinprodMarketId_div').hide();
            $('#changeToFinprodMarketId').val('');
            getProAvailableCash();
            getProAvailableCash1();
            if($("#assetType1").val() != F02){
                $('#finprodMarketId').removeAttr("required");
                // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add start
                $('#assetType1').removeAttr("required");
                $('#assetType10').removeAttr("required");
                $('#assetType11').removeAttr("required");
                // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add end
                star();
            }
            if(!initFlag) {
                selectManeger();
            }
            if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data') && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                var 交易对手 =  $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object;
                if (交易对手 == 'N') {
                    $('#bussEtrInvestPersonInfoDto_counterId option').remove();
                }
            }
        }
        else if($("#ps").val()==T01){
            $(".pschange").show();
            // $("#pschange1").show();
            $("#pschange2").hide();
            $("#entrustAmt0").show();
            $("#entrustNum0").show();
            $('#changeToFinprodMarketId_div').hide();
            $('#changeToFinprodMarketId').val('');
            if(pageModel === PAGE_MODEL_REFERENCE) {
            	$("#entrustNum").attr("disabled",false);
            } else {
            	$("#entrustNum").val('').attr("disabled",false);
            }
            $("#entrustAmt").attr("disabled",false);
            getEntrustAmt();
            if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data') && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                var 交易对手 = $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object;
                if (交易对手 != 'N') {
                    $('#bussEtrInvestPersonInfoDto_counterId option').remove();
                }
            }
            if($("#tradeMarket").val()==TRADE_PLACE_03){
                $('#bussEtrInvestPersonInfoDto_counterId').textSelect('setValue',JYDSJYS000000001);

            }
            if($("#tradeMarket").val()==TRADE_PLACE_04){
                $('#bussEtrInvestPersonInfoDto_counterId').textSelect('setValue',JYDSJYS000000002);
            }
        }
        else if($("#ps").val()==T02){
            $(".pschange").hide();
            // $("#pschange1").hide();
            $("#pschange2").show();
            $("#entrustNum0").show();
            $("#entrustNum").attr("disabled",false);
            $("#entrustAmt0").hide();
            $("#entrustAmt").val('');
            $('#changeToFinprodMarketId_div').hide();
            $('#changeToFinprodMarketId').val('');
            getAssetAvailablePosition();
            getEntrustAmt();
            if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data') && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                var 交易对手 = $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object;
                if (交易对手 != 'N') {
                    $('#bussEtrInvestPersonInfoDto_counterId option').remove();
                }
            }
            if($("#tradeMarket").val()==TRADE_PLACE_03){

                $('#bussEtrInvestPersonInfoDto_counterId').textSelect('setValue',JYDSJYS000000001);
            }
            if($("#tradeMarket").val()==TRADE_PLACE_04){
                $('#bussEtrInvestPersonInfoDto_counterId').textSelect('setValue',JYDSJYS000000002);
            }
        }
        //新建时，交易方向为认申购时，交易对手下拉框默认为基金管理人add by wl 2020-03-25
        if($('#finprodMarketId').val()){
            var data = $('#finprodMarketId').select2('data')[0].object;
            if(data){
                Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/selectManeger',{issuer:data.issuer},
                        function(result) {
                	if(($("#ps").val() == T04 || $("#ps").val() == T52)){
        		        if ($('#finprodMarketId').select2('data')[0].object) {
        		        	$('#bussEtrInvestPersonInfoDto_counterId').textSelect('setValue', result.id);
        		            $("#bussEtrInvestPersonInfoDto_counterId").attr("disabled",true);
        		        }else{
                            $('#bussEtrInvestPersonInfoDto_counterId').textSelect('setValue', "");
                            $("#bussEtrInvestPersonInfoDto_counterId").attr("disabled",false);
    		            }
                	}
                });
            }
        }

        if( ($("#tradeMarket").val()==TRADE_PLACE_03 || $("#tradeMarket").val()==TRADE_PLACE_04 )
        && ( $("#ps").val()==T02 || $("#ps").val()==T01 )){
            $("#bussEtrInvestPersonInfoDto_counterId").attr("disabled",true).trigger('change');
        }else{
            $("#bussEtrInvestPersonInfoDto_counterId").attr("disabled",false).trigger('change');
        }
        // 场外基金指令
        if(parentPageId && parentPageId == "EtrFundOff001"){
            var ps = $("#ps").val();
            if(!ps || (ps == T01 || ps == T04)){ // 当交易方向是买入/申购时,显示组合可用现金字段
                $("#cashT").show();
                $("#cashT1").show();
            }else{
                $("#cashT").hide();
                $("#cashT1").hide();
            }
            if(!ps || (ps == T02 || ps == T06)){ // 交易方向为卖出/赎回时,显示资产的可用持仓字段
                $("#pschange2").show();
            }else{
                $("#pschange2").hide();
            }
        }
    }
    
    function getPrice(){
        if (($("#ps").val()==T01 || $("#ps").val()==T02)
            &&($("#tradeMarket").val()==TRADE_PLACE_03||$("#tradeMarket").val()==TRADE_PLACE_04)
            &&($('#bussEtrInvestPersonInfoDto_counterId').val()==JYDSJYS000000001||$('#bussEtrInvestPersonInfoDto_counterId').val()==JYDSJYS000000002)) {
            $("#price").prop("disabled", false).attr("required",true);
            var data = {finprodId:$('#finprodMarketId').select2('data')[0].object.finprodId}
            if (data !== '') {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrfund/EtrFund002/getPrice',data,
                    function(result) {
                        $("#price").val(result).trigger('blur');
                    });
            }
        }else{
            if(pageModel === PAGE_MODEL_REFERENCE) {
                $("#price").prop("disabled", true).removeAttr("required");
            } else {
                $("#price").val('').prop("disabled", true).removeAttr("required");
            }
        }
    }
    
    function getEntrustAmt(){
        const price = $("#price").val();
        const entrustNum = $("#entrustNum").val();
        const entrustAmt = $("#entrustAmt").val();
        const ps = $("#ps").val();
        const finprodMarketId = $('#finprodMarketId').val();
        if (price == null || price.length === 0 || ps == null || ps.length === 0) {
            return;
        }
        //买入
        if (ps === T01 || ps === T02){
            if (finprodMarketId != null && finprodMarketId.length > 0 && $('#finprodMarketId').select2('data')[0].object) {
                if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data')
                    && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                    // 交易对手不是内部交易对手时，应该是指令价格 能输，指令金额 or 指令份额能输入，不能同时输入
                    // N:内部交易对手 Y:上交所，深交所
                    if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object=='Y') {
                        // 指令份额为空
                        if (entrustNum == null || entrustNum.length === 0) {
                            $("#entrustNum").prop("disabled", false);
                            $("#entrustAmt").prop("disabled", false);
                            return;
                        }
                        $("#entrustAmt").val(entrustNum * price).trigger('blur');
                        $("#entrustAmt").prop("disabled", true).removeAttr("required");
                    }
                    // 交易对手是内部交易对手时 指令份额输入，指令金额不能输入，价格可以输
                    else if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object=='N') {
                        $("#entrustNum").prop("disabled", false);
                        $("#entrustAmt").prop("disabled", true);
                    }
                }
            }
            else{
                $("#entrustNum").val('');
                $('#finprodMarketId').attr("required",true);
                // ZGZGST-2788:资产代码必填时，资产二、三、四级分类也必填 fuyongsheng 20210917 add start
                $('#assetType1').attr("required",true);
                $('#assetType10').attr("required",true);
                $('#assetType11').attr("required",true);
                // ZGZGST-2788:资产代码必填时，资产二、三、四级分类也必填 fuyongsheng 20210917 add end
                star();
            }
        }
    }

    function getEntrustNum(){
        const price = $("#price").val();
        const entrustNum = $("#entrustNum").val();
        const entrustAmt = $("#entrustAmt").val();
        const ps = $("#ps").val();
        const finprodMarketId = $('#finprodMarketId').val();
        if (price == null || price.length === 0 || ps == null || ps.length === 0) {
            return;
        }
        //买入
        if (ps === T01 || ps === T02){
            if (finprodMarketId != null && finprodMarketId.length > 0 && $('#finprodMarketId').select2('data')[0].object) {
                if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data')
                    && $('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0]) {
                    // 交易对手不是内部交易对手时，应该是指令价格 能输，指令金额 or 指令份额能输入，不能同时输入
                    // N:内部交易对手 Y:上交所，深交所
                    if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object=='Y') {
                        // 指令金额为空
                        if (entrustAmt == null || entrustAmt.length === 0) {
                            $("#entrustNum").prop("disabled", false);
                            $("#entrustAmt").prop("disabled", false);
                            return;
                        }

                        $("#entrustNum").val(entrustAmt / price).trigger('blur');
                        $("#entrustNum").prop("disabled", true).removeAttr("required");
                    }
                    // 交易对手是内部交易对手时 指令份额输入，指令金额不能输入，价格可以输
                    else if ($('#bussEtrInvestPersonInfoDto_counterId').select2('data')[0].object=='N') {
                        $("#entrustNum").prop("disabled", false);
                        $("#entrustAmt").prop("disabled", true);
                    }
                }
            }
            else{
                $("#entrustNum").val('');
                $('#finprodMarketId').attr("required",true);
                // ZGZGST-2788:资产代码必填时，资产二、三、四级分类必填 fuyongsheng 20210917 add start
                $('#assetType1').attr("required",true);
                $('#assetType10').attr("required",true);
                $('#assetType11').attr("required",true);
                // ZGZGST-2788:资产代码必填时，资产二、三、四级分类必填 fuyongsheng 20210917 add end
                star();
            }
        }
    }
    
    function init(){
        psChange();
        elementShowCtrl();
        initFlag = false;
        secAcctIdDisabled();
    }

    // 场外基金指令是不需要证券账户的
    var secAcctIdDisabled =  function(){
        if($("#tradeMarket").val()&&$("#tradeMarket").val()==TRADE_PLACE_06){
            $("#secAcctId").val('').trigger("change").prop("disabled", true);
        }
    }

    // 画面初始化控件显示控制
    var elementShowCtrl =  function(){
        if($("#finprodMarketId").val()){
            var selectObject = $('#finprodMarketId').select2('data')[0].object;
            if ($('#finprodMarketId').select2('data')[0].object) {
                if(selectObject.trmFundAbbr !== null && selectObject.trmFundAbbr !== ''){
                    $('#trmFundAbbr').show();
                }else{
                    $('#trmFundAbbr').hide();
                }
            }
        }
        if($("#finprodMarketId").val()&&$('#finprodMarketId').select2('data')[0].object&&$('#finprodMarketId').select2('data')[0].object.tradeMarket){
            $("#tradeMarket").attr("disabled",true);
        }else{
            $("#tradeMarket").attr("disabled",false);
        }

        if($('#finprodMarketId').val() != null && $('#finprodMarketId').val() != ''){
            changeDisabled(true);
        }

        if($("#assetType1").val() == F01 && ($('#finprodMarketId').val() != null || $('#finprodMarketId').val() != '') && ($("#ps").val() == T06 || $("#ps").val() == T65)){
            $("#pschange2").show();
        }
        else if($("#ps").val() != T04 && $("#ps").val() != T01){
            $("#pschange2").show();
        }
        $(".changein").hide();
        //交易对手返回可选状态
        $("#bussEtrInvestPersonInfoDto_counterId").attr('disabled',false);
        if($("#ps").val()==T04){
            $(".pschange").show();
            $("#pschange2").hide();
            $("#entrustAmt0").show();
            $("#entrustNum0").hide();
            $("#entrustAmt").attr("disabled",false);
            $('#assetAvailablePosition').val('').trigger('blur');
            $("#bussEtrInvestPersonInfoDto_counterId").attr('disabled',true);
        }
        else if(($("#ps").val() == T06 || $("#ps").val() == T65)){
            $(".pschange").hide();
            if($("#assetType1").val() == F01 && ($('#finprodMarketId').val() == null || $('#finprodMarketId').val() == '')){
                $("#pschange2").hide();
            }
            else{
                $("#pschange2").show();
            }
            $("#entrustNum0").show();
            $("#entrustNum").attr("disabled",false);
            $("#entrustAmt0").hide();
            if ($("#ps").val() == T65) {
                $(".changein").show();
            }
        }
        else if($("#ps").val()==T52){
            $(".pschange").show();
            $("#pschange2").hide();
            $("#entrustAmt0").show();
            $("#entrustNum0").hide();
            $("#entrustAmt").attr("disabled",false);
        }
        else if($("#ps").val()==T01){
            $(".pschange").show();
            $("#pschange2").hide();
            $("#entrustAmt0").show();
            $("#entrustNum0").show();
            $("#entrustAmt").attr("disabled",false);
        }
        else if($("#ps").val()==T02){
            $(".pschange").hide();
            $("#pschange2").show();
            $("#entrustAmt0").show();
            $("#entrustNum0").show();
            $("#entrustNum").attr("disabled",false);
        }
        if(($("#ps").val()==T01 || $("#ps").val()==T02)&&($("#tradeMarket").val()==TRADE_PLACE_03||$("#tradeMarket").val()==TRADE_PLACE_04)
            &&($('#bussEtrInvestPersonInfoDto_counterId').val()==JYDSJYS000000001||$('#bussEtrInvestPersonInfoDto_counterId').val()==JYDSJYS000000002)){
            $("#price").prop("disabled", false).attr("required",true);
        }else{
            if(pageModel === PAGE_MODEL_REFERENCE) {
                $("#price").prop("disabled", true).removeAttr("required");
            } else {
                $("#price").prop("disabled", true).removeAttr("required");
            }
        }
        if($("#tradeMarket").val()&&($("#tradeMarket").val()==TRADE_PLACE_03 ||$("#tradeMarket").val()==TRADE_PLACE_04)
            &&$("#secAcctId").val()){
            $("#tradeMarketCash_div").show();
        }else{
            $("#tradeMarketCash_div").hide();
        }
        $("#plat_price_div").hide();
        // 场外基金指令
        if(parentPageId && parentPageId == "EtrFundOff001"){
            var ps = $("#ps").val();
            if(!ps || (ps == T01 || ps == T04)){ // 当交易方向是买入/申购时,显示组合可用现金字段
                $("#cashT").show();
                $("#cashT1").show();
            }else{
                $("#cashT").hide();
                $("#cashT1").hide();
            }
            if(!ps || (ps == T02 || ps == T06)){ // 交易方向为卖出/赎回时,显示资产的可用持仓字段
                $("#pschange2").show();
            }else{
                $("#pschange2").hide();
            }
        }
    };

    /**
     * 查看资产详情事件
     */
    $('#EtrFund002_btn_search').click(function() {
        var finprodId = $("#finprodIdhid").val();
        var url = "invest/asset/fund/FinFundTarget002/init";
        if (finprodId != ""){
            var params = {
                pageModel:PAGE_MODEL_REFERENCE,
                isQuickInput:true,
                finprodId:finprodId
            };
            EtrShowFinPlanAssetDetailInfo.searchDetail(params,url);
        } else {
            Joyin.error("资产名称不能为空")
        }
    });
    
    /**
     * 绑定事件
     */
    $(function() {
        init();

        $("#ccy").change(function(){
            getProAvailableCash();
            getProAvailableCash1();
            getAssetAvailablePosition();
        })
        //基金类型
        $("#assetType1").change(function(){
            if($("#ps").val() == T04){
                $("#pschange2").hide();
            }
            else if($("#assetType1").val() == F01 && ($('#finprodMarketId').val() != null || $('#finprodMarketId').val() != '') && ($("#ps").val() == T06 || $("#ps").val() == T65)){
                $("#pschange2").hide();
            }
            else if($("#assetType1").val() == F02 && ($('#finprodMarketId').val() != null || $('#finprodMarketId').val() != '') && ($("#ps").val() == T06 || $("#ps").val() == T65)){
                $("#pschange2").show();
            }
            else if($("#ps").val() == T01){
                $("#pschange2").hide();
            }
            if($("#ps").val() == T04 || ($("#ps").val() == T06 || $("#ps").val() == T65)){
                if($("#assetType1").val() != F02){
                    $('#finprodMarketId').removeAttr("required");
                    // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add start
                    $('#assetType1').removeAttr("required");
                    $('#assetType10').removeAttr("required");
                    $('#assetType11').removeAttr("required");
                    // ZGZGST-2788:资产代码非必填时，资产二、三、四级分类也非必填 fuyongsheng 20210917 add end
                    star();
                }
                else{
                    $('#finprodMarketId').attr("required",true);
                    // ZGZGST-2788:资产代码必填时，资产二、三、四级分类必填 fuyongsheng 20210917 add start
                    $('#assetType1').attr("required",true);
                    $('#assetType10').attr("required",true);
                    $('#assetType11').attr("required",true);
                    // ZGZGST-2788:资产代码必填时，资产二、三、四级分类必填 fuyongsheng 20210917 add start
                    star();
                }
            }
        })

        //交易方向联动
        $("#ps").change(function(){
            psChange();
        })

        $("#changeToFinprodMarketId").change(function () {
            if ($("#changeToFinprodMarketId").val() && $('#changeToFinprodMarketId').select2('data')[0].text) {
                $("#changeToFinprodName").val($('#changeToFinprodMarketId').select2('data')[0].text);
            } else {
                $("#changeToFinprodName").val("")
            }
        })

        $("#entrustNum").change(function(){
            if ($("#ps").val() == T01 || $("#ps").val() == T02) {
                $("#entrustAmt").prop("disabled", true);
            }
            getEntrustAmt();

        });

        $("#entrustAmt").change(function(){
            if ($("#ps").val() == T01 || $("#ps").val() == T02) {
                $("#entrustNum").prop("disabled", true);
            }
            getEntrustNum();
        })

        $("#price").change(function(){
            if ($("#entrustNum").prop("disabled") == true) {
                getEntrustNum();
            } else if ($("#entrustAmt").prop("disabled") == true) {
                getEntrustAmt();
            }
        })

        //交易市场联动
        $("#tradeMarket").change(function(){
            /*//证券托管户，交易场所为交易所时必填
            if($("#tradeMarket").val()&&($("#tradeMarket").val()==TRADE_PLACE_03||$("#tradeMarket").val()==TRADE_PLACE_04)){
                $("#secAcctId").attr("required",true);
            }else{
                $("#secAcctId").removeAttr("required");
            }*/
            tradeMarketCashChange();
            //交易市场变动  确认是否需要填写委托价格
            getPrice();
            //交易市场改别时，调用交易方向change事件，确认交易对手
            if($("#ps").val()==T52||($("#ps").val() == T06 || $("#ps").val() == T65)||$("#ps").val()==T04){
                if(!initFlag){
                    selectManeger();
                }
            }
            star();
        });

        $('#secAcctId').on('change',function(){
            if ($("#secAcctId").val()) {
                $("#entrustTongdao").val('').prop("disabled", true).trigger("change");
            }else {
                $("#entrustTongdao").prop("disabled", false);
            }
            tradeMarketCashChange(true);
            // ZGZGST-1744:证券账户变化时带出资产可用持仓 fuyongsheng 20210831 add start
            getAssetAvailablePosition();
            // ZGZGST-1744:证券账户变化时带出资产可用持仓 fuyongsheng 20210831 add end
        });

        $("#entrustTongdao").on('change',function(){
            if ($("#entrustTongdao").val()) {
                $("#secAcctId").val('').trigger("change").prop("disabled", true);
                if($("#ps").val()&&($("#ps").val()==T04||($("#ps").val() == T06 || $("#ps").val() == T65)||$("#ps").val()==T52)){
                    $("#bussEtrInvestPersonInfoDto_counterId").val("").attr('disabled',false).trigger('change');
                }
            } else {
                $("#secAcctId").prop("disabled", false);
            }
            tradeMarketCashChange();
            if($("#ps").val()&&($("#ps").val()==T52||($("#ps").val() == T06 || $("#ps").val() == T65)||$("#ps").val()==T04)){
                selectManeger();
            }


        });

        /**
         * 保存
         */
        $('#EtrFund002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
         * 提交
         */
        $('#EtrFund002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
         * 风险试算
         */
        $('#EtrFund002_btn_riskCalculate').click(function(){
            // window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
			var object = $.serializeToObject('#searchForm',true);
            /* del by zhangrui1 这里不需要把份额赋值给金额 start
           if($('#entrustNum').val()) {
               object.entrustAmt =$('#entrustNum').val();//把份额直接赋值给金额
           }
              del by zhangrui1 这里不需要把份额赋值给金额 start */
			if(object.finprodMarketId != null && object.finprodMarketId != ''){
				object.finprodId = $('#finprodMarketId').select2('data')[0].object.finprodId;
			}
            // 基金转换申请时调用批量接口
            if (object.ps == T65) {
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK011/F02',object,getriskdata());
            }
            else {
                window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F02',object,getriskdata());
            }
        });
   /* }*/
        if(IS_SHOW_TYPE == '01'){
            $('#assetType11Div').show();
        }else{
            $('#assetType11Div').hide();
        }

        // add by etliu 20210501 银行间场内场外拆分 start
        if (parentPageId) {
            if(parentPageId == 'EtrFundEx001' || parentPageId == 'EtrFund001' || !parentPageId){
                $('#trmFundAbbrDiv').show();
                $('#trmFundTypeDiv').show();
            }else{
                $('#trmFundAbbrDiv').hide();
                $('#trmFundTypeDiv').hide();
            }
        }
        // add by etliu 20210501 银行间场内场外拆分 end
    });
}());
