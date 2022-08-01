/**
 * 债券借贷投资委托新建
 */
(function() {
    var unitNum;
    var initFlag = true;
    //---add by jjpei 20190718----start---获取风险试算画面要素与Label提示文本
    var getRiskData = function () {
        //配置单位 edit by yeming 20210723 start
        var totalAmt=$('#totalAmt').val();
        if (totalAmt) {
                totalAmt=JoyinMath.doMul(totalAmt, unitNum, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
        }
        //配置单位 edit by yeming 20210723 end
        var obj = {tradeDirection:$('#busiPs').val(),timePoint:MY_SUP_TIME_TYPE_01,valDate:sysDate,assetCode:$('#finprodId').val(),portfolioCode:$('#portfolioId').val(),tradeAmount:$('#totalAmt').val()?totalAmt:null,isEnabled:USE_STATUS_01}
        if ($("#busiPs").val() == BACK_LEND_DIRECTION_INTO ) {
            obj.assetType=FINPROD_TYPE_TG_F29;
        }
        if ($("#busiPs").val() == BACK_LEND_DIRECTION_OUT ) {
            obj.assetType=FINPROD_TYPE_TG_F30;
        }
        var object = {tradeDirection:['busiPs','EtrSecLend.busiPs'],portfolioCode:['portfolioId','EtrSecLend.portfolioId'],tradeAmount:['totalAmt','EtrSecLend.totalAmt'],data:obj}
        return object;
    }
    //---add by jjpei 20190718----end---获取风险试算画面要素与Label提示文本
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
                if (window.BaseRiskMathComm.isCalcConditionChanged(getRiskData())) {
                    return;
                }
            }
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm');
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.entrustId = $('#entrustId').val();
        object.entrustStatus = $('#entrustStatus').val();
        object.objectAvailablePosition = $("#bussTrdAvailableCashQuery_assetPosition").val(); // 资产可用持仓
        object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
        ////配置单位 add by yeming 20210728 start
        object.totalAmt = $('#totalAmt').val();
        if(object.totalAmt){//非空校验 add by xxg 20210806
            object.totalAmt = JoyinMath.doMul($('#totalAmt').val(), unitNum, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
        }
        //JoyinMath.doMul($('#totalAmt').val(), unitNum, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
        //配置单位 add by yeming 20210728 end
        if(object.busiRate != null && object.busiRate != ''){
            object.busiRate = JoyinMath.doDiv(object.busiRate,100,6);
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
        object.inputValue1 = PINAN_ASSET_TYPE_F12;
        //保存跳过校验 edit by yeming 20210915 start
        if (operateType === 'save') {
            if (!$("#finprodId").val()) {
                Joyin.error(JoyinUtil.getMessage("请输入标的债券"));
                return;
            }
            WorkFlow.doSaveList(basePath+'invest/entrust/etrseclend/EtrSecLend002/save',object,"EtrSecLend002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', object, function(result1) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend002/checkAuthorization', object, function(result2) {
                    Joyin.ajaxJson(basePath + 'invest/entrust/etrseclend/EtrSecLend002/check',object,
                        function(result) {
                            Joyin.confirm(result.data,function() {
                                // if (operateType === 'save') {
                                //     WorkFlow.doSaveList(basePath+'invest/entrust/etrseclend/EtrSecLend002/save',object,"EtrSecLend002_btn_save");
                                // } else {
                                object.balanceCheck ='Y';
                                    // WorkFlow.doSubmitList(basePath+'invest/entrust/etrseclend/EtrSecLend002/save',object,"EtrSecLend002_btn_submit",null,function (result){
                                    //     // console.log(result);
                                    //     if(result.messageType=="confirm"){
                                    //     //     Joyin.confirm(result.message,function() {
                                    //             // object;
                                    //         object.balanceCheck ='N';
                                    //         WorkFlow.doSubmitList(basePath+'invest/entrust/etrseclend/EtrSecLend002/save',object,"EtrSecLend002_btn_submit")
                                    //         // })
                                    //     }
                                    // });
                                WorkFlow.doSubmitList(basePath+'invest/entrust/etrseclend/EtrSecLend002/save',object,"EtrSecLend002_btn_submit")
                                // }
                            });
                        });
                });
            });
        }
        //保存跳过校验 edit by yeming 20210915 end
    }
    //查询组合可用持仓
    var getPtlSecPositionInfo = function(){
        if(pageModel ==PAGE_MODEL_REFERENCE){
            return;
        }
      var params = {};
      params.portfolioId = $("#portfolioId").val();
      params.calendarId = $("#calendarId").val();
      params.ccy = $("#ccy").val();
      params.tradeDate = sysDate;
      params.finprodId = $("#finprodId").val();
      params.secAcctId = $("#secAcctId").val();
      params.isContain9 ='N';

      // modify by etliu 20211129 start
      // TISHKS-465: 组合T日可用现金、组合T+1日可用现金为交易可用，应为指令可用
      //   params.multiParam=100;
      // if (window.BussTrdAvailableCashQuery && window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"]) {
      //   window.BussTrdAvailableCashQuery["bussTrdAvailableCashQuery"].updateCash(function () {
      //       return params
      //   });
      // }
        if (
            params.portfolioId == '' || params.portfolioId == null || typeof (params.portfolioId) == undefined
            || params.ccy == '' || params.ccy == null || typeof (params.ccy) == undefined
            || params.tradeDate == '' || params.tradeDate == null || typeof (params.tradeDate) == undefined
            || params.finprodId == '' || params.finprodId == null || typeof (params.finprodId) == undefined
            || params.secAcctId == '' || params.secAcctId == null || typeof (params.secAcctId) == undefined
        ) {
            $("#assetAvailablePosition").val('');
            return;
        }

        Joyin.ajaxJson(basePath + 'invest/entrust/etrinvestbondinfo/EtrInvestBondInfo002/getAssetAvailablePosition', params,
            function (result) {
                $('#bussTrdAvailableCashQuery_assetPosition').val(result.position*100).trigger('blur');
            });
        // modify by etliu 20211129 end
    }
    
   
    //添加风险试算验证
    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            if(typeof(BaseRiskMathComm) != 'undefined') {
                var riskParam = getRiskData();
                if (pageModel == PAGE_MODEL_NEW) {
                    riskParam.data = null;
                }
                window.BaseRiskMathComm.getparam(riskParam);
            }
            window.BussTrdAvailableCashQuery['bussTrdAvailableCashQuery'].showPosition(true);//只展示持仓
            init();
         // 设置添加质押券共通参数
            setBussRepoSecInput();
        }
    };
    
    /**
     * 设置添加质押券共通参数
     */
    var setBussRepoSecInput = function () {
        var data = {
            portfolioId: $('#portfolioId').val(),
            ps: $("#busiPs").val(),
            secAcctId: $('#secAcctId').val(),
            chlId: ''
        }
        var object = {
            portfolioId: ['portfolioId', 'EtrSecLend.portfolioId'],
            ps: ['ps', 'EtrSecLend.busiPs'],
            secAcctId: ['secAcctId', 'EtrSecLend.secAcctId'],
            data: data
        }
        if (window.BussRepoSec && window.BussRepoSec["bussRepoSec"]) {
            window.BussRepoSec["bussRepoSec"].setInputParam(object);
        }
    };
    //初始化页面
    var init= function(){
        if(entrustAmtUnit == '01' ){
            $(".unitNum").text('元');
            unitNum = 1;
        }else{
            unitNum = 10000;
        }
        $("#avaStandardBondPositionDiv").hide();
        $("#pledgeStamps_div").hide();
        $('#busiPs').change();
    }
    
    /**
     * 绑定事件
     */
    $(function() {
        /**
         * 保存
         */
        $('#EtrSecLend002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
         * 提交
         */
        $('#EtrSecLend002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
         * 风险试算
         */
        $('#EtrSecLend002_btn_riskcal').click(function(){
            // window.BaseRiskMathComm.initBaseRiskMathCommPage(getRiskData());
            var object = $.serializeToObject('#searchForm');
            //配置单位 edit by yeming 20210723 start
            var totalAmt=$('#totalAmt').val();
            if (totalAmt) {
                    totalAmt=JoyinMath.doMul(totalAmt, unitNum, 2, JoyinMath.ROUND_HALF_UP);//万元转成元
            }
            //配置单位 edit by yeming 20210723 end
            object.entrustAmt=$('#totalAmt').val()?totalAmt:null;
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F12',object,getRiskData());
        });
        /**
         * 交易场所
         */
        $('#tradeMarket').change(function() {
            
        });
    
        /**
         * 借贷方向
         */
        $('#busiPs').change(function() {
            var busiPs= $('#busiPs').val();
           // 设置添加质押券共通参数
            setBussRepoSecInput();
            if(BACK_LEND_DIRECTION_INTO == busiPs){
                
                $("#pledgeStamps_div").show();
                $('#objectAvailablePosition').val(null).trigger('blur');
                $("#avaStandardBondPositionDiv").hide();
                
            }else if(BACK_LEND_DIRECTION_OUT == busiPs){
                $("#pledgeStamps_div").hide();
                getPtlSecPositionInfo();
                $("#avaStandardBondPositionDiv").show();
            }

        });
        /**
         * 交易组合
         */
        $("#portfolioId").on("change", function (e, isInit) {
            getPtlSecPositionInfo();
         // 设置添加质押券共通参数
            setBussRepoSecInput();
         // 债券投资 改变组合 需联动 证券账户 ,证券托管户应清空选择项
            if (!isInit && pageModel !=PAGE_MODEL_REFERENCE&&pageModel !=PAGE_MODEL_EDIT) {
                $("#secAcctId").val('').trigger("change");
            }
        });
        
        /**
         * 证券托管户
         */
        $('#secAcctId').change(function(){
            getPtlSecPositionInfo();
         // 设置添加质押券共通参数
            setBussRepoSecInput();
        });
        /**
         * 标的债券
         */
        $('#finprodId').change(function(){
            getPtlSecPositionInfo();
        });
        
        /**
         * 交易日历
         */
        $('#calendarId').change(function(){
            getPtlSecPositionInfo();
        });
        /**
         * 币种
         */
        $('#ccy').change(function() {
            getPtlSecPositionInfo();
        });
      
      
    });
}());
