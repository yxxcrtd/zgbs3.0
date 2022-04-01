$(function () {
    // 获取查询参数
    var getSearchParams = function () {
        return {
            pbcProdId: $('#pbcProdId').val() == null ? null : $('#pbcProdId').val().join(","),
            prodId: $('#prodId').val() == null ? null : $('#prodId').val().join(","),
            operateMode: $('#operateMode').val() == null ? null : $('#operateMode').val().join(','),
            ivstorientation: $('#ivstorientation').val() == null ? null : $('#ivstorientation').val().join(','),
            sendStatus: $('#sendStatus').val() == null ? null : $('#sendStatus').val().join(','),
            status: $('#status').val() == null ? null : $('#status').val().join(','),
            statusDateStart: $('#statusDate').startDate(),
            statusDateEnd: $('#statusDate').endDate(),
            submitTimeStart: $('#submitTime').startDate(),
            submitTimeEnd: $('#submitTime').endDate(),
            submitFeedbackStatus: $('#submitFeedbackStatus').val() == null ? null : $('#submitFeedbackStatus').val().join(','),
            pbcSubmitDeadlineDateEnd: $('#pbcSubmitDeadline').endDate(),
            pbcSubmitDeadlineDateStart: $('#pbcSubmitDeadline').startDate(),
            vdateStart: $('#vdate').startDate(),
            vdateEnd: $('#vdate').endDate(),

        };
    }
    // 定义数据表格
    var table = $("#dataTable").joyintable({
        ajax: {
            url: 'searchPage',
            type: 'POST',
            data: function () {
                return getSearchParams();
            }
        },
        showDetail: true,//双击详情对比画面
        rowno: false, // 显示行号
        searchOnInit: false, //初始化的时候就请求后台数据
        cols: [
            {field: 'registSeqno', visible: false},
            {title: "数据状态", field: 'statusStr', width: 150, sortField: 'status'},
            {title: "数据日期", field: 'statusDate', width: 150, type: 'datetime', sortField: 'statusDate'},
            {title: "报送状态", field: 'sendStatusStr', width: 150, sortField: 'sendStatus'},
            {title: '报送日期', field: 'submitTime', width: 150, type: 'datetime', sortField: 'submitTime'},
            {title: "报送截止日", field: 'pbcSubmitDeadline', width: 150, type: 'date', sortField: 'pbcSubmitDeadline'},
            {title: "反馈状态", field: 'submitFeedbackStatusStr', width: 150, sortField: 'submitFeedbackStatus'},
            {field: 'pbcProdId', visible: false},
            {field: 'pbcPoolCode', visible: false},

//				{ title: "产品代码(内部)", field: 'pbcProdIdShow',width:150,sortField:''},
//				{ title: "产品代码(外部)", field: 'pbcCode',width:150,sortField:''},
            {title: "产品代码", field: 'pbcProdIdShow', width: 150, sortField: 'pbcProdIdShow'},
            {title: "发行机构代码", field: 'issuePartyId', width: 150, sortField: 'issuePartyId'},
            {title: "产品名称", field: 'prodName', width: 150, sortField: 'prodName'},
            {title: "产品品种", field: 'pbcProdType', width: 150, sortField: 'pbcProdType'},
            {title: "产品类型", field: 'ivstorientation', width: 150, sortField: 'ivstorientation'},
            {title: "产品品牌", field: 'prodBrand', width: 150, sortField: 'prodBrand'},
            {title: "产品期次", field: 'prodStage', width: 150, sortField: 'prodStage'},
//				{ title: "发起机构内部产品代码", field: 'prodId',width:150,sortField:''},
            {title: "发行机构内部产品代码", field: 'prodId', width: 150, sortField: 'prodId'},
            {title: "募集方式", field: 'raiseType', width: 150, sortField: 'raiseType'},
            {title: "管理方式", field: 'manageType', width: 150, sortField: 'manageType'},
            {title: "运行方式", field: 'operateMode', width: 150, sortField: 'operateMode'},
            {title: "募集起始日期", field: 'raiseVdate', width: 150, type: 'date', sortField: 'raiseVdate'},
            {title: "募集结束日期", field: 'raiseEnddate', width: 150, type: 'date', sortField: 'raiseEnddate'},
            {title: "发行机构提前终止权标识", field: 'termRightFlag', width: 150, sortField: 'termRightFlag'},
            {title: "客户赎回权标识", field: 'cusRedeemFlag', width: 150, sortField: 'cusRedeemFlag'},
            {title: "产品增信标识", field: 'creditFlag', width: 150, sortField: 'creditFlag'},
            {title: "境内托管机构代码", field: 'inDepCode', width: 150, sortField: 'inDepCode'},
            {title: "境外托管机构国别", field: 'outDepCtry', width: 150, sortField: 'outDepCtry'},
            {title: "境外托管机构名称", field: 'outDepName', width: 150, sortField: 'outDepName'},
            {title: "产品起始日期", field: 'vdate', width: 150, type: 'date', sortField: 'vdate'},
            {title: "产品预计终止日期", field: 'mdate', width: 150, type: 'date', sortField: 'mdate'},
            {title: "受托职责", field: 'fiduciaryDuty', width: 150, sortField: 'fiduciaryDuty'},
            {title: "分级产品标识", field: 'isConstracte', width: 150, sortField: 'isConstracte'},
            {title: "收益权转让产品标识", field: 'transferType', width: 150, sortField: 'transferType'},
            {title: "货币市场基金或现金管理类产品", field: 'isMarketFundStr', width: 150, sortField: 'isMarketFund'},

            /***未提供字段.暂时使用资产池代码替代.*/
            {title: "跨境理财通", field: 'crossBorderFlag', width: 150, sortField: 'crossBorderFlag'},
            {title: "以财产权信托名义开展的资金信托标识", field: 'zjxtFlag', width: 150, sortField: 'zjxtFlag'},
            {title: "其他产品标识3", field: '', width: 150, sortable:false},


            {title: "募集资金币种", field: 'raiseCcy', width: 150, sortField: 'raiseCcy'},
            {title: "兑付本金币种", field: 'cashPrinCcy', width: 150, sortField: 'cashPrinCcy'},
            {title: "兑付收益币种", field: 'cashProfCcy', width: 150, sortField: 'cashProfCcy'},
            {title: "增信机构类型", field: 'creditType', width: 150, sortField: 'creditType'},
            {title: "增信形式", field: 'creditMode', width: 150, sortField: 'creditMode'},

            //
            // {title: "客户类型", field: 'customerType', width: 150, sortField: 'customerType'},
            // {title: "合作模式", field: 'contMode', width: 150, sortField: 'contMode'},
            // {title: "业务模式", field: 'busiModel', width: 150, sortField: 'busiModel'},
            // {title: "收益保障标识", field: 'incomeGuaranIdent', width: 150, sortField: 'incomeGuaranIdent'},
            // {title: "本金保障标识", field: 'prinGuaranIdent', width: 150, sortField: 'prinGuaranIdent'},
            // {title: "预计客户最高年收益率", field: 'highYieldIssue', width: 150, sortField: 'highYieldIssue'},
            // {title: "预计客户最低年收益率", field: 'lowYieldIssue', width: 150, sortField: 'lowYieldIssue'},
            //
            //
            //
            // {title: "境内托管机构名称", field: 'inDepName', width: 150, sortField: 'inDepName'},
            //
            //
            //
            // {title: "产品对应资产池代码", field: 'accountId', width: 150, sortField: 'accountId'},
            // {title: "理财产品发起机构标识", field: 'identiFlag', width: 150, sortField: 'identiFlag'},
            {field: 'sendStatus', visible: false},
            {field: 'status', visible: false},
            {field: 'submitFeedbackStatus', visible: false}
        ]
    })
    // 查询按钮
    $('#PbcPrdBaseinfoSubNew001_btn_search').click(function () {
        table.load();
    })

    // 导出按钮
    $('#PbcPrdBaseinfoSubNew001_btn_chbexport').click(function () {
        var expArr = table.getAllCheckedRows();
        if (expArr != null && expArr.length > 0) {
            Joyin.confirm('确认导出选中信息？', function () {
                exportExcel(1);
            })
        } else {
            Joyin.confirm('确认导出查询结果？', function () {
                exportExcel(0);
            })
        }
    })
    /**
     * 导出
     * flag:0 全部导出，选中导出
     */
    var exportExcel = function (flag) {
        if (flag == 0) {
            var param = getSearchParams();
            Joyin.exportExcel("export", param);
        } else {
            var param = {};
            var expArr = table.getAllCheckedRows();
            var keyList = '';
            layui.each(expArr, function (index, data) {
                if (index == expArr.length - 1) {
                    keyList = keyList + data.registSeqno + '';
                } else {
                    keyList = keyList + data.registSeqno + ',';
                }
            })
            param.keyList = keyList;
            Joyin.exportExcel("export", param);
        }

    };
    //数据检查
    $('#PbcPrdBaseinfoSubNew001_btn_submit').click(dateCheckMethod);

    function dateCheckMethod() {
        var checkArr = table.getAllCheckedRows();
        if (checkArr.length === 0) {
            var submitStatus = $('#sendStatus').val();
            var status = $('#status').val();
//            if(submitStatus!='01'){
            if (!(submitStatus && submitStatus.length == 1 && submitStatus[0] == '01')) {
                Joyin.error("只能对报送状态为未报送，数据状态为新建和修改的数据进行数据检查！");
                return;
            }
//            else if(status && status.includes('03')){
            else if (status && status.indexOf('03') > -1) {
                Joyin.error("只能对数据状态为新建和修改的数据进行报送！");
                return;
            }
            var paramObj = getSearchParams()
            if (!status) {
                paramObj.status = '01,02';
            }
            Joyin.ajax('reportCheck', paramObj, function (result) {
                window.checkResult = result;
                Joyin.checkData(dataTitle, checkUrl, getSearchParams());
            })
            return;
        }
        var keyList = [];
        for (var i = 0; i < checkArr.length; i++) {
            //modify by wangmin 修改取数顺序 start 2018-07-12
            keyList.push(checkArr[i].registSeqno);
            //modify by wangmin 修改取数顺序 end 2018-07-12
            if (checkArr[i].sendStatus != "01") {
                Joyin.error("数据检查只能检查未报送数据，请重新选择！");
                return;
            }
//            else if(checkArr[i].status && checkArr[i].status.includes('03')){
            else if (checkArr[i].status && checkArr[i].status.indexOf('03') > -1) {
                Joyin.error("选择的报送数据中存在删除状态的数据，请重新选择！");
                return;
            }
        }
        ;
        Joyin.ajax('reportCheck', {checkKeys: keyList.join(',')}, function (result) {
            window.checkResult = result;
            Joyin.checkData(dataTitle, checkUrl, {checkKeys: keyList.join(',')});
        })

    }

    // 手工处理
    $('#PbcPrdBaseinfoSubNew001_btn_manual').click(function () {
        var rows = table.getAllCheckedRows();
        if (rows.length === 0) {
           return Joyin.error("请至少选择一条记录");
            //add by liubin
            var sendStatus = $("#sendStatus").val();
            var submitFeedbackStatus = $("#submitFeedbackStatus").val();
            if (
                (
                    (
                        (sendStatus && sendStatus.length== 1 && sendStatus[0] == '03')
                        || (sendStatus && sendStatus.indexOf('03') > -1)
                    )
                    ||
                    (
                        ( sendStatus && sendStatus.length== 1 && sendStatus[0] == '02')
                            || (sendStatus && sendStatus.indexOf('02') > -1)
                        )
                        &&(submitFeedbackStatus
                            && (submitFeedbackStatus.indexOf('01') > -1
                                || submitFeedbackStatus.indexOf('03') > -1
                            )
                        )
                    )
                ) {
                Joyin.error("报送状态为【手工处理】，或者报送状态为【接口报送】并且反馈状态为【人行通过】、【发送成功】的数据不可进行手工处理。");
                return;
            }
            var paramObj = getSearchParams();
            paramObj.flag = '02';
            Joyin.confirm("请确认是否要手工处理符合查询条件的全部记录？", function () {
                layui.layer.open({type: 3});
                Joyin.ajaxJson('doManul', paramObj, function () {
                    table.load();
                    layui.layer.closeAll();
                });
            });
        } else {
            for (let i = 0; i < rows.length; i++) {
                let sendStatusRow = rows[i].sendStatus;
                let submitFeedbackStatusRow = rows[i].submitFeedbackStatus;
                if (
                    (
                        (
                            (sendStatusRow && sendStatusRow.length== 1 && sendStatusRow[0] == '03')
                            || (sendStatusRow && sendStatusRow.indexOf('03') > -1)
                        )
                        ||
                        (
                            (( sendStatusRow && sendStatusRow.length== 1 && sendStatusRow[0] == '02')
                                || (sendStatusRow && sendStatusRow.indexOf('02') > -1))
                            &&(submitFeedbackStatusRow
                                && (submitFeedbackStatusRow.indexOf('01') > -1
                                    || submitFeedbackStatusRow.indexOf('03') > -1)
                            )
                        )
                    )
                ) {
                    Joyin.error("报送状态为【手工处理】，或者报送状态为【接口报送】并且反馈状态为【人行通过】、【发送成功】的数据不可进行手工处理。");
                    return;
                }


                if(rows[i].pbcPoolCode==null||rows[i].pbcPoolCode==''){
                    Joyin.error("请先维护人行产品代码后，再进行手工处理。");
                    return;
                }
            }

            var paramObj = {};
            paramObj.flag = '01';
            paramObj.infoList = rows;
            Joyin.confirm("请确认是否要手工处理选中记录？", function () {
                layui.layer.open({type: 3});
                Joyin.ajaxJson('doManul', paramObj, function () {
                    table.load();
                    layui.layer.closeAll();
                });
            });
        }

    })
    // 取消手工处理
    $('#PbcPrdBaseinfoSubNew001_btn_cancelManual').click(function () {
        var manArr = table.getAllCheckedRows();
        if (!manArr || manArr.length == 0) {
            var sendStatus = $("#sendStatus").val();
            if (!(sendStatus && sendStatus.length == 1 && sendStatus[0] == '03')) {
                Joyin.error("只能选择手工处理的数据，请重新选择！");
                return;
            }
            var paramObj = getSearchParams();
            paramObj.flag = '02';
            Joyin.confirm("请确认是否要取消手工处理符合查询条件所有记录？", function () {
                layui.layer.open({type: 3});
                Joyin.ajaxJson('cancelManul', paramObj, function () {
                    table.load();
                    layui.layer.closeAll();
                });
            });
        } else {
            for (var i = 0; i < manArr.length; i++) {
                if (manArr[i].sendStatus != "03") {
                    Joyin.error("只能选择手工处理的数据，请重新选择！");
                    return;
                }
            }
            ;
            var paramObj = {};
            paramObj.flag = '01';
            paramObj.infoList = manArr;
            Joyin.confirm("请确认是否要取消手工处理选中记录？", function () {
                layui.layer.open({type: 3});
                Joyin.ajaxJson('cancelManul', paramObj, function () {
                    table.load();
                    layui.layer.closeAll();
                });
            });
        }

    })

    function init() {
        if (isMain == '01') {
            setTimeout(function () {
                if ($('#sendStatus').val() == '02' || $('#sendStatus').val() == '03') {
                    $('#submitTime').val(searchDataStart + ' - ' + searchDataEnd);
                } else {
                    $('#pbcSubmitDeadline').val(searchDataStart + ' - ' + searchDataEnd);
                }
                table.load();
            }, 500);
        }
    };


    // 导出人行Excel按钮
    $('#PbcPrdBaseinfoSubNew001_btn_chbexportxls').click(function () {
        var expArr = table.getAllCheckedRows();
        if (expArr != null && expArr.length > 0) {
            Joyin.confirm('确认导出选中信息？', function () {
                downloadExcel(1);
            })
        } else {
            Joyin.confirm('确认导出查询结果？', function () {
                downloadExcel(0);
            })
        }
    })

    /**
     * 导出人行Excel
     * flag:1 选中导出
     * flag:0 全部导出
     */
    var downloadExcel = function (flag) {
        var expArr = table.getAllCheckedRows();
        var expArr1 = table.getAllCheckedRows();
        var param = {};
        var paramObj = getSearchParams();

        if (flag == "0") {

            param = getSearchParams();
            param.flag = flag;
            if (flag == "0") {
                //param.status="01";
                param.sendStatus = "01";
            }
            var isHaveData = true;
            Joyin.ajaxSync('searchPage', param, function (result) {
                if (!result || result.root.length == 0) {
                    isHaveData = false;
                    return;
                }
            })

            //Joyin.exportExcel("download",paramObj);
            Joyin.exportExcel("download", param);

            //ZGBSEI-394  新人行产品基本信息登记页面，统计模板登记页面，产品终止信息登记页面，导出待修复按钮点击后，去掉是否手工处理的提示 LEIPENG 20210827
            isHaveData = false;
            if (isHaveData) {
                Joyin.confirm("导出成功，是否自动标识未报送数据的报送状态为“手工处理”？", function () {
                    param.isall = '01';
                    //页面添加滚动条
                    // layui.layer.open({type: 3});
                    Joyin.ajaxJson('doManul', param, function () {
                        table.load();
                        layui.layer.closeAll();
                    });
                });
            }
        } else {
            // 用来标注选择数据中存在修改或者已报送数据，默认为false
            var statuFlag = false;
            var count = 0;
            var message = "";
            var p = {};
            if (flag == "1") {

                for (var i = 0; i < expArr.length; i++) {
                    // 含有删除状态数据
                    if (expArr[i].status == "03") {
                        Joyin.error("所选数据中包含数据状态为删除的数据，请重新选择。");
                        return;
                        // 含有修改状态数据或者已报送数据，将标志改为true
                    } else if ((expArr[i].submitFeedbackStatus == "01" && expArr[i].submitFeedbackStatus == "03")
                        || expArr[i].sendStatus != "01") {
                        statuFlag = true;
                    }
                    if (expArr[i].submitFeedbackStatus == "01" || expArr[i].submitFeedbackStatus == "03"){
                        Joyin.error("所选记录中包含已报送数据，请重新选择。");
                        return;
                    }
                }
            }

            if (count == 0) {
                var infoList = [];
                param.infoList = JSON.stringify(expArr1);
                //param.expKeys = infoList.join(',')
                param.flag = "01";
                param.exportflag = 'exportSelected';
                if (statuFlag) {
                    Joyin.error("所选记录中包含已报送数据，请重新选择");
                } else {
                    confirmExport(param, expArr1);
                }
            } else {
                Joyin.confirm(message + "产品没有起始募集信息，不会导出Excel文件，请确认是否导出！", function () {
                    confirmExport(param, expArr1);
                });
            }
        }
    };

    /**
     * 确认导出之后的操作
     */
    var confirmExport = function (param, expArr1) {

        var datas = JSON.stringify(expArr1);
        Joyin.exportExcel("download", {datas: datas});
        var obj = {};
        obj.flag = '01';
        obj.infoList = expArr1;

        //            //ZGBSEI-394  新人行产品基本信息登记页面，统计模板登记页面，产品终止信息登记页面，导出待修复按钮点击后，去掉是否手工处理的提示 LEIPENG 20210827
        // for (var i = 0; i < expArr1.length; i++) {
        //     // 含有删除状态数据
        //     if (expArr1[i].sendStatus == "01") {
        //         Joyin.confirm("导出成功，是否自动标识未报送数据的报送状态为“手工处理”？", function () {
        //             for(var i = 0; i < expArr1.length; i++){
        //                 if(expArr1[i].pbcPoolCode==null||expArr1[i].pbcPoolCode==''){
        //                     Joyin.error("请先维护人行产品代码后，再进行手工处理。");
        //                     return;
        //                 }
        //             }
        //
        //
        //             layui.layer.open({type: 3});
        //             Joyin.ajaxJson('doManul', obj, function () {
        //                 table.load();
        //                 layui.layer.closeAll();
        //             });
        //         });
        //     } else {
        //         return;
        //     }
        // }
    };


    init();

})