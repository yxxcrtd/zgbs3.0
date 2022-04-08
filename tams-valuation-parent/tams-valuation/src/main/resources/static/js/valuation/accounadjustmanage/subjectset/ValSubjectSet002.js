(function() {
    function saveOrSubmit() {
        //getHands();
        var object = $.serializeToObject('#submitForm');
        if(object.type1){
            object.type1 = object.type1.join(",");
        }
        if(object.type2){
            object.type2 = object.type2.join(",");
        }
        if(object.type3){
            object.type3 = object.type3.join(",");
        }
        if(object.type4){
            object.type4 = object.type4.join(",");
        }
        if(object.type5){
            object.type5 = object.type5.join(",");
        }
        if(object.type6){
            object.type6 = object.type6.join(",");
        }
        if(object.type7){
            object.type7 = object.type7.join(",");
        }
        if(object.type8){
            object.type8 = object.type8.join(",");
        }
        if(object.type9){
            object.type9 = object.type9.join(",");
        }
        object.pageModel = pageModel;
        Joyin.ajaxJson(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet002/check', object, function(result) {
            Joyin.confirm(result.data, function() {
                Joyin.ajaxJson(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet002/save', object, function(result) {
                    var index = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(index);
                });
            });
        });
    };

    /**组装科目辅助项JSON**/
    function getHands() {
        var checkbox = [];
        var str="";
        var fel="";
        $("select[name='handCodes']").each(function (i) {
            console.log($(this).css('display'));
            // if($(this).css('display')!='none'&&($(this).val() == '' || $(this).val() == undefined || $(this).val() == null)){
            //     Joyin.warn($(this).attr("title")+"不能为空");
            // }

            console.log($(this).val());
            if ($(this).val() != '' && $(this).val() != undefined && $(this).val() != null) {
                str = '{"handCode" :"' + $(this).attr('id') + '","handCodeValue" :"' + $(this).val() + '"}';
                checkbox.push(str);
                fel = fel + '' + $(this).attr('id') + '==' + "'" + $(this).val() + "'" + '&&';
            }
        });
        $('#handjson').val('['+checkbox+']');
        fel=fel.substr(0,fel.lastIndexOf("&&"));
        console.log(fel);
        $('#subjectHandFelCode').val(fel);



    }

    /**
     * 选择内部科目编码 联动查询已配置的辅助核算项 拼接_DIV找到与页面对应的下拉选项并展示
     */
    var sbjectInternalcodeChange = function() {
        reset();
        var booksetId = $('#booksetId').val();
        if (booksetId == '' || booksetId == undefined) {
            reset();
            return ;
        }
        var subjectInternalCode = $('#subjectInternalCode').val();

        if (subjectInternalCode == '' || subjectInternalCode == undefined) {
            reset();
            return ;
        }

        Joyin.ajax(basePath + 'valuation/accounadjustmanage/assistaccount/ValAssistAccounting001/findHandsByCondition',
            {booksetId: booksetId,subjectInternalCode:subjectInternalCode,status:"Y"}, function (data) {
                if (data) {
                    for (var i = 0, l = data.length; i < l; i++) {
                        $("#"+data[i].handCode+"_DIV").show();
                        $("#"+data[i].handCode).show();
                    }

                }
            })
    }

    function reset(){
        $("select[name='handCodes']").each(function () {
            //清空所有选项
            var id=$(this).attr("id");
            //每个下拉框的值 被保存该下拉框ID +"_O"的隐藏域中
            $(this).val( $("#"+id+"_O").val() ).trigger("change");
            $(this).hide();
        });

        $("div[name='handCodesdiv']").each(function () {
            //隐藏所有
            $(this).hide();
        });
    }

    /*add by sunshuaishuai 辅助核算项修改 20220406 start*/
    //辅助核算项下拉框匹配
    var subjectInternalCodeChange = function(){
        var subjectInternalCode = $("#subjectInternalCode").val();
        if(subjectInternalCode){
            //1.查询内部科目记账辅助总表，找出科目性质对应的辅助核算项
            Joyin.ajax(basePath + 'valuation/accounadjustmanage/subjectset/ValSubjectSet002/getHandCode',
                {subjectInternalCode: subjectInternalCode}, function (data) {
                //2。根据查询结果，隐藏不需要的下拉框，并把label名称改为对应字典名称
                    if (data) {
                        $(".typeDiv").show();
                        //隐藏多余的下拉框
                        for (var i = 9 ; i > data.length ; i--) {
                            $("#div"+i).hide();
                        }
                        //修改标签名
                        for (var j = 1;j <= data.length; j++){
                            document.getElementById("label"+j).innerHTML=data[j-1].handName;
                        }
                    }else{
                        $(".typeDiv").show();
                    }
                })
        }
    }

    //核算表达式 字符串拼接和展示
    var expressionChange = function(){
        var type1 = $("#type1").val();
        var type2 = $("#type2").val();
        var type3 = $("#type3").val();
        var type4 = $("#type4").val();
        var type5 = $("#type5").val();
        var type6 = $("#type6").val();
        var type7 = $("#type7").val();
        var type8 = $("#type8").val();
        var type9 = $("#type9").val();
        var typeDefault = $("#typeDefault").val();
        var result="";
        var resultName="";
        if(typeDefault && typeDefault!=null){
            result = "DEFAULT=="+"'"+$('#typeDefault').select2('data')[0].id+"'";
            resultName = "默认=='是'";
        }
        if(type1 && type1 != null ){
            var obj = $('#type1').select2('data')[0].object;
            var result1 = concat(obj.dictCode,type1);
            var result1Name = concatName(obj.dictName,"type1");
            result += "&&" + result1;
            resultName += "&&" + result1Name;
        }
        if(type2 && type2 != null ){
            var obj = $('#type2').select2('data')[0].object;
            var result2 = concat(obj.dictCode,type2);
            var result2Name = concatName(obj.dictName,"type2");
            result += "&&" + result2;
            resultName += "&&" + result2Name;
        }
        if(type3 && type3 != null ){
            var obj = $('#type3').select2('data')[0].object;
            var result3 = concat(obj.dictCode,type3);
            var result3Name = concatName(obj.dictName,"type3");
            result += "&&" + result3;
            resultName += "&&" + result3Name;
        }
        if(type4 && type4 != null){
            var obj = $('#type4').select2('data')[0].object;
            var result4 = concat(obj.dictCode,type4);
            var result4Name = concatName(obj.dictName,"type4");
            result += "&&" + result4;
            resultName += "&&" + result4Name;
        }
        if(type5 && type5 != null ){
            var obj = $('#type5').select2('data')[0].object;
            var result5 = concat(obj.dictCode,type5);
            var result5Name = concatName(obj.dictName,"type5");
            result += "&&" + result5;
            resultName += "&&" + result5Name;
        }
        if(type6 && type6 != null ){
            var obj = $('#type6').select2('data')[0].object;
            var result6 = concat(obj.dictCode,type6);
            var result6Name = concatName(obj.dictName,"type6");
            result += "&&" + result6;
            resultName += "&&" + result6Name;
        }
        if(type7 && type7 != null ){
            var obj = $('#type7').select2('data')[0].object;
            var result7 = concat(obj.dictCode,type7);
            var result7Name = concatName(obj.dictName,"type7");
            result += "&&" + result7;
            resultName += "&&" + result7Name;
        }
        if(type8 && type8 != null ){
            var obj = $('#type8').select2('data')[0].object;
            var result8 = concat(obj.dictCode,type8);
            var result8Name = concatName(obj.dictName,"type8");
            result += "&&" + result8;
            resultName += "&&" + result8Name;
        }
        if(type9 && type9 != null ){
            var obj = $('#type9').select2('data')[0].object;
            var result9 = concat(obj.dictCode,type9);
            var result9Name = concatName(obj.dictName,"type9");
            result += "&&" + result9;
            resultName += "&&" + result9Name;
        }
        if(result.substring(0,2)=="&&"){
            result = result.substring(2,result.length);
        }
        if(resultName.substring(0,2)=="&&"){
            resultName = resultName.substring(2,result.length);
        }
        $('#subjectHandFelCode').val(result);
        $('#subjectHandFelCodeName').val(resultName);
    }

    function concat(obj,type){
        var str ="";
        var concat="";
        var rst="";
        if(type.length > 1){
            for (var j = 0,len=type.length; j < len; j++) {
                str = obj + "==" + "'" + type[j] + "'";
                concat += str + "||" ;
                rst = "(" + concat.substring(0,concat.length-2) + ")";
            }
        }else{
            rst = obj + "==" + "'" + type[0] + "'";
        }
        return rst;
    }

    function concatName(obj,tname){
        var str ="";
        var concat="";
        var rst="";
        var typeArray = new Array();
        //获取选择的数据数组
        var arr = $("#"+tname).select2('data');
        if(arr){
            for (var i = 0; i < arr.length ; i++) {
                typeArray.push(arr[i].text);
            }
        }
        if(typeArray.length > 1){
            for (var j = 0,len=typeArray.length; j < len; j++) {
                str = obj + "==" + "'" + typeArray[j] + "'";
                concat += str + "||" ;
                rst = "(" + concat.substring(0,concat.length-2) + ")";
            }
        }else{
            rst = obj + "==" + "'" + typeArray[0] + "'";
        }
        return rst;
    }
    /*add by sunshuaishuai 辅助核算项修改 20220406 end*/
    /**
     * 画面加载完成
     */
    $(function () {
        if (typeof pageModel !== 'undefined'
            && (pageModel === PAGE_MODEL_EDIT)) {
            //账套和科目号不允许修改
            $("#booksetId").attr('disabled',"true");
            $("#subjectNo").attr('disabled',"true");
            subjectInternalCodeChange();
        }

        sbjectInternalcodeChange();
        // 绑定按钮事件
        $('#ValSubjectSet002_btn_submit').click(function() {
            saveOrSubmit();
        });
        
        //修改上级科目号时，自动添加到科目号中
        $("#fsubjectId").change(function(){
            $("#subjectNo").val($("#fsubjectId").val());
        });

        /**内部科目编码联动显示可见的辅助核算项**/
        $("#subjectInternalCode").change(function(){
            sbjectInternalcodeChange();
            layui.form.render();
        });

        $("#booksetId").change(function(){
            sbjectInternalcodeChange();
            layui.form.render();
        });

        /*add by sunshuaishuai 辅助核算项修改 20220406 start*/
        /**基础科目性质联动辅助核算项**/
        $("#subjectInternalCode").change(function(){
            subjectInternalCodeChange();
        });

        //拼接辅助核算表达式 联动事件
        $("#type1").change(function(){
            expressionChange();
        });
        $("#type2").change(function(){
            expressionChange();
        });
        $("#type3").change(function(){
            expressionChange();
        });
        $("#type4").change(function(){
            expressionChange();
        });
        $("#type5").change(function(){
            expressionChange();
        });
        $("#type6").change(function(){
            expressionChange();
        });
        $("#type7").change(function(){
            expressionChange();
        });
        $("#type8").change(function(){
            expressionChange();
        });
        $("#type9").change(function(){
            expressionChange();
        });
        $("#typeDefault").change(function(){
            expressionChange();
        });
        /*add by sunshuaishuai 辅助核算项修改 20220406 end*/


    });



}());