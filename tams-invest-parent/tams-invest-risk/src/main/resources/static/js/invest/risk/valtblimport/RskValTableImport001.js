var valTableFileName = '';
RskValTableImport001 = function(){
        return {
            uploadCallback : function(res){
                valTableFileName = '';
                if(res && res.length > 0){
                    valTableFileName = res[0].fileName;
                 // 为了消除进度条
                    Joyin.ajax(JoyinUtil.getRealPath('invest/risk/valtblimport/RskValTableImport001/uploadFile'));

                    // 解析上传文件名称，获取资产名称和估值日期
                    var arr = valTableFileName.split(chbValSplitStr);
                    var dateFlag = true;
                    if (typeof arr[1] != 'undefined') {
                        var valDate = arr[1];
                        if (valDate.indexOf('_') >= 0) {
                            var dateArr = valDate.split('_');
                            valDate = dateArr[0];
                        }
                        if (valDate.length == 8) {
                            var year = valDate.substr(0,4);
                            var month = valDate.substr(4,2);
                            var day = valDate.substr(6,2);
                            valDate = year + "-" + month + "-" + day;
                            if (checkDate(valDate)) {
                                $('#valDate').val(valDate);
                                // $('#RskValTableImport001_btn_see').attr("disabled", false);
                                dateFlag = false;
                            }
                        }
                    }

                    if (dateFlag){
                        $('#ptlName').val('');
                        $('#ptlId').val('');
                        $('#valDate').val('');
                        $('#ptlId').trigger('change');
                        Joyin.error("上传的估值表文件名称不符合格式要求!");
                        // $('#RskValTableImport001_btn_see').attr("disabled", true);
                    } else {
                        var ptlName = arr[0];
                        if (ptlName.indexOf('\\') >= 0) {
                            var nameArr = ptlName.split('\\');
                            ptlName = nameArr[nameArr.length - 1];
                        }
                        $('#ptlName').val(ptlName);
                        $('#ptlId').val(ptlName);

                        var object = $.serializeToObject('#submitForm');
                        object.valTableFileName = valTableFileName;

                        Joyin.ajaxJson(basePath + 'invest/risk/valtblimport/RskValTableImport001/checkFileName',object, function(result) {
                            if (typeof result.data == 'undefined') {
                                $('#ptlName').val('');
                                $('#ptlId').val('');
                                $('#valDate').val('');
                                $('#ptlId').trigger('change');
                                Joyin.error("上传的估值表文件资产名称不存在!");
                            } else {
                                $('#ptlName').val(result.data.text);
                                $('#ptlId').val(result.data.id);
                                $('#ptlId').trigger('change');
                            }
                        });
                    }
                }
            }
        }
}();
/**
 * 验证日期是否正确
 * 日期格式：yyyy-mm-dd，yyyy-m-d，yyyy/mm/dd，yyyy/m/d
 */
function checkDate(dateStr) {
    dateStr = dateStr.replace(/\//g, '-');
    var dateReg = /^(\d{4})-(\d{1,2})-(\d{1,2})$/;
    var rValue = dateStr.match(dateReg);
    if (rValue == null) {
        return false;
    }
    rValue[1] = parseInt(rValue[1], 10);
    rValue[2] = parseInt(rValue[2] - 1, 10);
    rValue[3] = parseInt(rValue[3], 10);
    var dateObj = new Date(rValue[1], rValue[2], rValue[3]);
    if (dateObj.getFullYear() != rValue[1] || dateObj.getMonth() != rValue[2] || dateObj.getDate() != rValue[3]) {
        return false;
    }
    return true;
}
(function() {
    /**
     * 获取查询参数
     */
    var param = {};
    var getSearchParams = function() {
        return param;
    };
    /**
     * 定义数据表格(指标结构映射)
     */
    var rskvalstructureTable=$("#rskvalstructureTable").joyintable({
        ajax: {
            url: basePath+'invest/risk/penetrationmanagement/rskvalstructure/RskValStructure001/findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox:false,
        cols:[
             {
                field : 'srcColIndex',visible: false,
                title : 'RskValStructure001.srcColIndex'
            }
            ,{
                field : 'srcColIndexLetter',
                title : 'RskValStructure001.srcColIndex',sortField:'srcColIndex'
            }, {
                field : 'srcColDataTypeStr',
                sortField : "srcColDataType",
                title : 'RskValStructure001.srcColDataType'
            }, {
                field : 'tgtIndexStr',
                sortField : "tgtIndex",
                
                title : 'RskValStructure001.tgtIndex'
            }, {
                field : 'memo',
                title : 'RskValStructure001.memo'
            }
        ]
    });
    
    /**
     * 定义数据表格(资产科目映射)
     */
    var rskValAssetCodeTable=$("#rskValAssetCodeTable").joyintable({
        ajax: {
            url: basePath+'invest/risk/penetrationmanagement/rskvalassetcode/RskValAssetCode001/findPageDtl',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox:false,
        cols:[
             {
            field : 'id',
            visible : false
        }, {
            field : 'srcCode',
            sortField : "SRC_CODE",
            title : 'RskValAssetCode004.srcCode'
        }, {
            field : 'tgtCode',
            sortField : "TGT_CODE",
            title : 'RskValAssetCode004.tgtCode'
        }, {
            field : 'allowSumName',
            sortField : "ALLOW_SUM",
            title : 'RskValAssetCode004.allowSum'
        }, {
            field : 'memo',
            title : 'RskValAssetCode004.memo'
        }
        ]
    });
    
    /**
     * 定义数据表格(汇总科目映射)
     */
    var rskvalsumnameTable=$("#rskvalsumnameTable").joyintable({
        ajax: {
            url: basePath+'invest/risk/penetrationmanagement/rskvalsumname/RskValSumname001/findPage',//请求路径
            type: 'POST',//请求类型
            data: function() {
                return getSearchParams();//获取请求参数
            }//ajax请求数据
        },
        checkbox:false,
        /**
         * 定义表头
         */
        cols:[
             {
            field : 'sumName',
            title : 'RskValSumname001.sumName'
        }, {
            field : 'sumCodeStr',
            sortField : "sumCode",
            title : 'RskValSumname001.sumCode'
        }, {
            field : 'nextIndexTypeStr',
            sortField : "nextIndexType",
            title : 'RskValSumname001.nextIndexType'
        }
        ]
    });
    
    /**
     * 组合名称change事件
     */
    var reloadTable = function(){
        Joyin.ajaxJson(basePath + 'invest/risk/valtblimport/RskValTableImport001/selectRskValRelateImportById',{ptlId:$('#ptlId').val()}, function(result) {
            param.structMapId = result.structMapId;param.sumsubMapId = result.sumsubMapId;param.assetSubMapId = result.assetSubMapId;
            $('#structMapId').val(result.structMapId);$('#sumsubMapId').val(result.sumsubMapId);$('#assetSubMapId').val(result.assetSubMapId);
            $('#subjectCodeCol').val(result.subjectCodeCol);$('#startRow').val(result.startRow);
            $('#trimChars').val(result.trimChars);
            //--add by 裴家俊20190514---start----
            $('#filterId').val(result.filterId);
            //--add by 裴家俊20190514---end----
            $('div.rsk-dl-wrap').show();
            $('#ddstartRow').text(result.startRow);$('#ddassetCodeColIndex').text(result.subjectCodeColLetter);$('#ddtrimChars').text(result.trimChars);
            //--add by 裴家俊20190514---start----
            $('#ddfilterId').text(result.filterName==null||result.filterName==''?'':result.filterName);
            //--add by 裴家俊20190514---end----


            $("#tabList>ul>li[lay-id='00']").trigger('click');
            rskvalstructureTable.table ? rskvalstructureTable.reload() : rskvalstructureTable.load();

            $("#tabList>ul>li[lay-id='01']").trigger('click');
            rskValAssetCodeTable.table ? rskValAssetCodeTable.reload() : rskValAssetCodeTable.load();

            $("#tabList>ul>li[lay-id='02']").trigger('click');
            rskvalsumnameTable.table ? rskvalsumnameTable.reload() : rskvalsumnameTable.load();
        })
    }
    
    /**
     * 前台check
     */
    var check = function(){
        var rskvalstructureList = rskvalstructureTable.getAllRows();
        var rskValAssetCodeList = rskValAssetCodeTable.getAllRows();
        var rskvalsumnameList = rskvalsumnameTable.getAllRows();
        var msg;var msgArray=[];var messages="";
        if(!rskvalstructureList || rskvalstructureList.length == 0) {
            msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("RskValStructure001.title"));
            msgArray.push(msg);
        }
        if(!rskValAssetCodeList || rskValAssetCodeList.length == 0) {
            msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("RskValAssetCode002.title2"));
            msgArray.push(msg);
        }
        if(!rskvalsumnameList || rskvalsumnameList.length == 0) {
            msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("RskValSumname001.title"));
            msgArray.push(msg);
        }
        if(msgArray&&msgArray.length>0){
            msgArray.forEach(function(value){
                messages = messages + '<span>' +value + '</span>' + '<br>';
            });
        }
        if(messages){
            Joyin.error(messages);
            $('.layui-layer-padding').height('80');
            $('.layui-layer-dialog').height('200');
            return false;
        }else{
            return true;
        }
        /*if(msg) {
            Joyin.error(msg);
            return false;
        }
        return true;*/
    }
    
    /**
     * 获取文件路径名
     */
    var getFileName = function(){
        var fun = $('#valtable').data('getSelectedFiles');
        if(typeof fun === 'function') {
            return fun();
        }
        return [];
    }
    /**
     * 弹出入库画面
     * @returns
     */
    function showDialog() {
        var object = $.serializeToObject('#submitForm');
        object.operateType = "sava";
        object.pageModel = pageModel;
        var fileInfo = getFileName();
        if(fileInfo.length==0){
            Joyin.error("未导入文件。");
            return;
        }
        if(typeof fileInfo[0] != 'undefined'){
            object.valTableFileName = fileInfo[0].fileName;
        }
        Joyin.ajaxJson(basePath + 'invest/risk/valtblimport/RskValTableImport001/check',object, function(result) {
            if(check()){
                Joyin.dialog(JoyinUtil.getLabel('估值表预览'), '../RskValTableImport002/init', '95%', '95%'
                        ,object);
            }
        });
        
    }

        //批量导入
        function batchUpload() {
            Joyin.confirm("请确认是否要批量导入数据？", function () {
                Joyin.uploadFile(
                    {
                        pageId : 'RskValTableImport001',
                        exts: 'zip',
                        hideTpl: true
                    },
                    function (fileName) {
                        $('#valDate').val('');
                        $('#ptlName').val('');
                        $('#ptlId').val('').trigger('change');

                        JoyinProgress.showInDialog('uploadFileProgressBar');
                        Joyin.ajax(basePath + "invest/risk/valtblimport/RskValTableImport001/batchUploadFile?",
                            {
                                fileName: fileName
                            }, function (result) {
                               // layer.close(processIndex);
                                //点击保存后关闭窗口
                                var index = parent.layer.getFrameIndex(window.name);
                                parent.layer.close(index);
                            });
                    });
            });
        }

    /**
     * 画面加载完成
     */
    $(function() {
        $('#ptlId').change(reloadTable);
        // 绑定入库按钮事件
        $('#RskValTableImport001_btn_see').click(showDialog);
        // 绑定批量导入事件
        $('#RskValTableImport001_btn_batchUpload').click(batchUpload);
    });
}());