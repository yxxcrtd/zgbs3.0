/**
 * 拆解委托新建
 */
(function() {

    var getriskdata = function () {
        if($('#assetType1').val() == S02){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype_cr,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
        }
        else if($('#ps').val() == T32){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype_cr,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
        }else if($('#ps').val() == T33){
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype_cc,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
        }else{
            var obj = {tradeDirection:$('#ps').val(),timePoint:MY_SUP_TIME_TYPE_02,valDate:sysDate,portfolioCode:$('#portfolioId').val(),tradeAmount:$('#entrustAmt').val(),assetType:fintype_cr,isEnabled:'01',assetCode:$('#finprodIdhid').val()}
        }
        // add by zhangrui1 交易对手影响风险试算 start
        obj.counterId = $('#etrInvestPersonInfo_counterId').val();
        // add by zhangrui1 交易对手影响风险试算 end
        var object = {tradeDirection:['ps','EtrLendSpecInfo002.ps'],portfolioCode:['portfolioId','EtrLendSpecInfo002.portfolioId'],tradeAmount:['entrustAmt','EtrLendSpecInfo002.entrustAmt'],assetType:['assetType1','EtrLendSpecInfo002.type6'],data:obj}
        // add by zhangrui1 交易对手影响风险试算 start
        object.counterId = ['counterId', 'EtrInvestPersonInfo.counterId'];
        // add by zhangrui1 交易对手影响风险试算 end
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
        if(BaseRiskMathComm) {
            s = BaseRiskMathComm.saveOrSubmitCheck(operateType);
            if(!s && s != ''){
                return;
            }
            if (window.BaseRiskMathComm.isCalcConditionChanged(getriskdata())){
                return;
            }
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#searchForm');
        object.etrInvestPersonInfo.invManagerId = $('#etrInvestPersonInfo_invManagerId').val();
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
        object.operateType = operateType;
        object.pageModel = pageModel;
        object.entrustId = $('#entrustId').val();
        object.entrustStatus = $('#entrustStatus').val();
        object.termDays = $('#termDays').val().split('.')[0];
        object.ps = $('#ps').val();
        object.inputValue1 = PINAN_ASSET_TYPE_F08;
        object.inputValue2 = $('#ps').val();
        if(parentPageId){
            object.parentPageId = parentPageId;
        }
        //指令暂存，去除校验  add by xxg 20210802
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath+'invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/save',object,"EtrLendSpecInfo002_btn_save");
        }else{
            Joyin.ajaxJson(basePath + 'invest/entrust/EtrCommonController/check', object, function(result1) {
                Joyin.ajaxJson(basePath + 'invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/check',object,
                    function(result) {
                        Joyin.confirm(result.data,function() {
                            WorkFlow.doSubmitList(basePath+'invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/save',object,"EtrLendSpecInfo002_btn_submit");
                        });
                    });
            });
        }
    }
    /**
     * 判断是否节假日
     */
    var judgeIsHday = function(){
        object = {mdateAdd:$('#mdateAdd').val(),calId:$('#calendarId').val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/judgeIsHday',object,
          function(result) {
        });
    }


    /**
     * 组合可用现金
     */
    var proMoney = function(){
        var data = {portfolioId:$('#portfolioId').val(),ccy:$('#ccy').val(),calId:$('#calendarId').val()}
        Joyin.ajaxJson(basePath + 'invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/getProAvailableCash',data,
                function(result) {
            $('#proAvailableCash').val(result.cashT).trigger('blur');
           // $('#proAvailableCash1').val(result.cashT1).trigger('blur');
            $('#accAvailableCash').val(result.cashTG).trigger('blur');
        });
    }

    // 一体化对应etliu end
    $.fn.updateChosenArr = function updateChosenArr(obj) {
        var $this = $(this), arr = obj.dataResult || JSON.parse($this.attr('data-result') || '[]'), item = obj.item;
        $this.empty();
        $this.append($('<option value>'));
        for (var i in arr) {
            if (!arr.hasOwnProperty(i)) continue;
            if (obj.item && obj.item.length > 0) {
                if ($.inArray(arr[i].id, item) >= 0)
                    $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this);
            } else
                $('<option>').attr('value', arr[i].id).text(arr[i].text).appendTo($this)
        }
        if (obj.chosen)
            $this.val(obj.chosen);
        if (obj.disabled === true || obj.disabled === false)
            $this.attr('disabled', obj.disabled);
        $this.trigger("chosen:updated").trigger('change');
        if (obj.callback)
            obj.callback($this);
        return $this;
    }

    //添加风险试算验证
    window.componentLoadCallback = function(isDone, pagePath, params) {
        if (isDone) {
            var riskparam = getriskdata();
            if (pageModel==PAGE_MODEL_NEW){
                riskparam.data =null;
            }
            window.BaseRiskMathComm.getparam(riskparam);
        }
    }
    /**
     * 绑定事件
     */
    $(function() {
        
        var init = function(){
            $('#assetType1').change();
            $('#ps').change();
            $('#vdate').change();

            $('#tradeMarket').change();
            $('#termType').change();
            $('#settleSpreed').change();

        }
        /**
         * 保存
         */
        $('#EtrLendSpecInfo002_btn_save').click(function() {
            saveOrSubmit('save');
        });
        /**
         * 提交
         */
        $('#EtrLendSpecInfo002_btn_submit').click(function() {
            saveOrSubmit('submit');
        });
        /**
         * 风险试算
         */
        $('#EtrLendSpecInfo002_btn_riskcal').click(function(){
            // window.BaseRiskMathComm.initBaseRiskMathCommPage(getriskdata());
            var object = $.serializeToObject('#searchForm');
            window.BaseRiskMathComm.initBaseRiskMathRedis(basePath + 'riskrds/interf/doInfData/0/RSK002/F08',object,getriskdata());
        });
        /**
         * 拆借类型
         */
        $('#assetType1').change(function(){
        	if (pageModel != PAGE_MODEL_REFERENCE) {
	            if($('#assetType1').val()==S01){
	                $("#ps").prop("disabled",false).change();
	            }else{
	                $("#ps").prop("disabled",true).val(T32).change();
	            }
        	}
        });
        /**
         * 交易方向
         */
        $('#ps').change(function(){
            if($('#ps').val()==T33){
                $('#proAvailableCash_div').show();
                $('#proAvailableCash1_div').show();
                // $('#accAvailableCash_div').show();
                var portfolioId = $('#portfolioId').val();
                var ccy = $('#ccy').val();
                if(portfolioId && ccy){
                    proMoney();
                }
            }else{
                $('#proAvailableCash_div').hide();
                $('#proAvailableCash1_div').hide();
                // $('#accAvailableCash_div').hide();
            }
        });

        /**
         * 交易场所
         */
        $('#tradeMarket').change(function(){
            var tradeMarket = $('#tradeMarket').val();
            if($('#tradeMarket').val()=='09'){
                $("#termType").val('').change().prop("disabled",true);

                var $settleSpreed = $("#settleSpreed");
                var settleSpreed = $settleSpreed.val();
                $settleSpreed.updateChosenArr({
                    item: ['01'],
                    chosen: settleSpreed
                });
            }else{
                $("#termType").prop("disabled",true);
                $("#settleSpreed").prop("disabled",false);
                var $settleSpreed = $("#settleSpreed");
                var settleSpreed = $settleSpreed.val();
                $settleSpreed.updateChosenArr({
                    item: ['01','02'],
                    chosen: settleSpreed
                });
            }
            $('#termDays').trigger('change');
        });

        /**
         * 币种
         */
        $('#ccy').change(function(){
            var portfolioId = $('#portfolioId').val();
            var ccy = $('#ccy').val(); 
            var ps = $('#ps').val();
            if(portfolioId && ccy && ps == T33){
                proMoney();
            }
        });
        /**
         * 组合代码
         */
        $('#portfolioId').change(function(){
            var portfolioId = $('#portfolioId').val();
            var ccy = $('#ccy').val(); 
            var ps = $('#ps').val();
            if(portfolioId && ccy && ps == T33){
                proMoney();
            }
        });
        /**
         * 委托开始日
         */
        $('#vdate').change(function(){
            var vdate = Date.ofDate($('#vdate').val());
            var mdate = Date.ofDate($("#mdate").val());
            if(vdate.getTime() == mdate.getTime()){
                $("#vdateAdd").val(vdate.format("yyyy-MM-dd")).attr("disabled",true).change();
                $("#mdateAdd").attr("disabled",true);
            }
            else{
            	//若委托开始日和结束日不为同一天，起息日和到期日为空不可填  yzg 20190904 start
                $("#vdateAdd").attr("disabled",true);
                $("#mdateAdd").attr("disabled",true);
                $("#vdateAdd").val('');
                $("#mdateAdd").val('');
              //若委托开始日和结束日不为同一天，起息日和到期日为空不可填  yzg 20190904 end
            }
        });
        /**
         * 委托结束日
         */
        $('#mdate').change(function(){
            var vdate = Date.ofDate($('#vdate').val());
            var mdate = Date.ofDate($("#mdate").val());
            if(vdate.getTime() == mdate.getTime()){
                $("#vdateAdd").val(vdate.format("yyyy-MM-dd")).attr("disabled",true).change();
                $("#mdateAdd").attr("disabled",true);
            }else{
            	//若委托开始日和结束日不为同一天，起息日和到期日为空不可填  yzg 20190904 start
                $("#vdateAdd").attr("disabled",true);
                $("#mdateAdd").attr("disabled",true);
                $("#vdateAdd").val('');
                $("#mdateAdd").val('');
                //若委托开始日和结束日不为同一天，起息日和到期日为空不可填  yzg 20190904 end
            }
        });
        /**
         * 到期日
         */
        $('#mdateAdd').change(function(){
           var vdate = Date.ofDate($('#vdate').val());
           var mdate = Date.ofDate($("#mdate").val());
           var vdateAdd = $('#vdateAdd').val();
           var mdateAdd = $("#mdateAdd").val();
           var calId = $('#calendarId').val();
           if(mdateAdd !=null && mdateAdd != '' && calId != null && calId != ''){
               isHoliday(mdateAdd,calId);
           }
           if(Date.ofDate(vdateAdd).getTime()>Date.ofDate(mdateAdd).getTime()){
               Joyin.error("到期日不能小于起息日");
               $("#mdateAdd").val(vdateAdd.format("yyyy-MM-dd"));
               // $('#termDays').val(0);
               return;
           }
           /** 到期日联动期限 **/
           // if(vdateAdd != null && vdateAdd != '' && mdateAdd != null && mdateAdd != ''){
           //    var d1 = vdateAdd.getTime();
           //    var d2 = mdateAdd.getTime();
           //    var total = (d2-d1)/1000;
           //    var day = parseInt(total / (24*60*60));
           //    $('#termDays').val(day).trigger('change');
           // }
        });

        /**
         * 是否为节假日 是：日期顺延  否：不做处理
         * @param mdateAdd
         * @param calId
         */
        var isHoliday = function(mdateAdd,calId){
            var object = {mdateAdd:mdateAdd,calId:calId};
            Joyin.ajaxJson(basePath+"invest/entrust/etrlendspecinfo/EtrLendSpecInfo002/isHoliday",object ,function (result) {
                if (result){//到期日为节假日，顺延
                    var d = new Date(parseInt(result)).format("yyyy-MM-dd");
                    $('#mdateAdd').val(d);
                }
            });
        };


       /**
        * 起息日
        */
       $('#vdateAdd').change(function(){
          var vdateAdd = $('#vdateAdd').val();
          var mdateAdd = $("#mdateAdd").val();
          var day = $('#termDays').val();
          if(Date.ofDate(vdateAdd).getTime()>Date.ofDate(mdateAdd).getTime()&&mdateAdd){
              Joyin.error("起息日不能大于到期日");
              $("#vdateAdd").val(mdateAdd.format("yyyy-MM-dd"));
              $('#termDays').val(0);
              return;
          }
          /** 起息日不可联动期限 （起息日根据清算速度联动到期日） **/
          // if(vdate.getTime() == mdate.getTime() && vdateAdd != null && vdateAdd != ''){
          //    var d1 = vdateAdd.getTime();
          //    var d2 = mdateAdd.getTime();
          //    var total = (d2-d1)/1000;
          //    var day = parseInt(total / (24*60*60));
          //    $('#termDays').val(day);
          // }
          if(!day){
        	  return;
          }
           var d1 = DateUtil.parse(vdateAdd);
           var d2 = new Date(d1.getFullYear(),d1.getMonth(),d1.getDate()+parseInt(day));
           $("#mdateAdd").val(d2.format("yyyy-MM-dd"));
           $('#mdateAdd').change();

       });
        /**
         * 期限
         */
        $('#termDays').change(function(){
            var vdate = Date.ofDate($('#vdate').val());
            var mdate = Date.ofDate($("#mdate").val());
            var vdateAdd = Date.ofDate($('#vdateAdd').val());
            if(vdateAdd != null && vdateAdd != ''){
                var d1 = vdateAdd.getTime();
                var day = parseInt($('#termDays').val()*24*60*60*1000);
                var mdateAdd = Date.ofDate(d1+day);
                $('#mdateAdd').val(mdateAdd.format("yyyy-MM-dd"));
            }
            var repoterm = $('#termDays').val();
            var re = /^[0-9]*$/;
            var tradeMarket = $('#tradeMarket').val();
            if($('#tradeMarket').val()=='01'||$('#tradeMarket').val()=='09'||$('#tradeMarket').val()==''){
                if(repoterm && re.test(repoterm)){
                    var vdate = $('#vdateAdd').val();
                    if(vdate){
                        //回购期限联动期限品种
                        var term = getTermType(vdate,repoterm);
                        // alert(term)
                        $('#termType').val(term).trigger('change');
                        $('#termType').textSelect('setValue', term);
                        $('#termType').attr('disabled',true);
                    }
                }else{
                    $('#termType').val("").trigger('change');
                }
            }
            else{
                $('#termType').val("").trigger('change');
            }
            $('#mdateAdd').change();
        });

        /**
         * 获取期限品种  date(yyyy-MM-dd),repoterm
         */
        function getTermType(date,repoterm) {
            if(repoterm&&repoterm<=21){
                //期限品种小于21天
                if(repoterm==1){
                    return 'IBO001';
                }
                if(repoterm>1&&repoterm<=7){
                    return 'IBO007';
                }
                if(repoterm>7&&repoterm<=14){
                    return 'IBO014';
                }
                if(repoterm>14&&repoterm<=21){
                    return 'IBO021';
                }
            }
            var day=0;
            //date为空时  返回0
            if(date&&repoterm){
                //获取日期
                date =DateUtil.parse(date);
                //获取之后月00天 即本月最后一天
                day= new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+1,0).getDate();
                if(repoterm<=day){
                    return 'IBO1M';
                }
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+2,0).getDate();
                if(repoterm<=day){
                    return 'IBO2M';
                }
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+3,0).getDate();
                if(repoterm<=day){
                    return 'IBO3M';
                }
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+4,0).getDate();
                if(repoterm<=day){
                    return 'IBO4M';
                }
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+5,0).getDate();
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+6,0).getDate();
                if(repoterm<=day){
                    return 'IBO6M';
                }
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+7,0).getDate();
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+8,0).getDate();
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+9,0).getDate();
                if(repoterm<=day){
                    return 'IBO9M';
                }
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+10,0).getDate();
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+11,0).getDate();
                day= day+new Date(date.getFullYear(),new Date(DateUtil.parse(date)).getMonth()+12,0).getDate();
                if(repoterm<=day){
                    return 'IBO1Y';
                }
            }
            return null;
        }
        /** 交易日历 **/
        $('#calendarId').change(function(){
            var mdateAdd = Date.ofDate($("#mdateAdd").val());
            var calId = $('#calendarId').val();
            var portfolioId = $('#portfolioId').val();
            var ccy = $('#ccy').val(); 
            var ps = $('#ps').val();
            if(mdateAdd !=null && mdateAdd != '' && calId != null && calId != ''){
                judgeIsHday();
            }
            if(portfolioId && ccy && ps == T33){
                proMoney();
            }
        });
        /** 清算速度 **/
        $('#settleSpreed').change(function () {
            var vdate = $('#vdate').val();//指令开始日
            var mdate = $('#mdate').val();//指令结束日
            var settleSpreed = $('#settleSpreed').val();//清算速度
            var day = DateUtil.parse(vdate);
            if (vdate == mdate && settleSpreed == '01'){//指令开始日=指令终止日，清算速度T+0
                $('#vdateAdd').val(day.format("yyyy-MM-dd"));
            }
            if (vdate == mdate && settleSpreed == '02'){
                day = new Date(day.getFullYear(),day.getMonth(),day.getDate()+1);
                $('#vdateAdd').val(day.format("yyyy-MM-dd"));
            }
            $('#vdateAdd').change();
            // $('#termDays').change();// 清算速度只能联动起息日
        });
        init();
    });
}());

