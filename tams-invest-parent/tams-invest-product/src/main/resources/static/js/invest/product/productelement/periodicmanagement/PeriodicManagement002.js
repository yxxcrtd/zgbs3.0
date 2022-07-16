$(function () {
    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        ajax: {
            url: JoyinUtil.getRealPath('invest/product/productelement/periodicmanagement/PeriodicManagement002/selectFloatFeeList'),
            type: 'POST',
            data: function () {
                return {periodProdId: $('#periodProdId').val(), workflowId: workflowId, periodId: $('#periodId').val()};
            }
        },

        addclick: function (rowData) {
            rowData.lev = table.getAllRows().length;
            rowData.baseRate = JoyinUtil.getLabel("BussBenchMarkInfo");
            rowData.coefficient = 1;
        },
        searchOnInit: true,
        addbtn: true,
        height: 200,
        page: false,
        checkbox: false,
        loadonce: true,
//        rowno: true,
        /*cols:[
              {field:'lev', width:200, title: 'BussFinProductFloatFee.table.floatBaseLevel', sortable: false, render : function(data, type, row, meta) { return (row.LAY_TABLE_INDEX+1); }}
              ,{field:'baseRuleId', width:200, title: 'BussFinProductFloatFee.table.baseRate', sortable: false, render:function () {return JoyinUtil.getLabel("BussBenchMarkInfo");}}
              ,{field:'coefficient', width:200, title: 'BussFinProductFloatFee.table.coefficient', sortable: false,edit:true, type: 'number',fixType:"2",pattern:"2"}
              ,{field:'spreadRate', width:200, title: 'BussFinProductFloatFee.table.spreadRate', sortable: false,edit:true, type: 'number',fixType:"2",pattern:"2", digit:4}
              ,{field:'chargeRatio', width:200, title: 'BussFinProductFloatFee.table.chargeRatio', sortable: false,edit:true, type: 'number',fixType:"2",pattern:"2", digit:4}
              ,{field:'operation', title: 'SysCommon.operation', type:'action', buttons: ['delt']}
          ]*/

        cols: [
            {field: 'lev', width: 200, title: 'BussFinProductFloatFee.table.floatBaseLevel', sortable: false}
            , {field: 'baseRuleId', visible: false}
            , {
                field: 'baseRate',
                width: 200,
                title: 'BussFinProductFloatFee.table.baseRate',
                sortable: false,
                render: function () {
                    return JoyinUtil.getLabel("BussBenchMarkInfo");
                }
            }
            , {
                field: 'coefficient',
                width: 200,
                title: 'BussFinProductFloatFee.table.coefficient',
                sortable: false,
                edit: true,
                type: 'number',
                fixType: "2",
                pattern: "2"
            }
            , {
                field: 'spreadRate',
                width: 200,
                title: 'BussFinProductFloatFee.table.spreadRate',
                sortable: false,
                edit: true,
                type: 'number',
                fixType: "2",
                pattern: "2",
                digit: 4
            }
            , {
                field: 'chargeRatio',
                width: 200,
                title: 'BussFinProductFloatFee.table.chargeRatio',
                sortable: false,
                edit: true,
                type: 'number',
                fixType: "2",
                pattern: "2",
                digit: 4
            }
            , {field: 'operation', title: 'SysCommon.operation', type: 'action', buttons: ['delt']}
        ]
    });

    /*
     * 绑定删除事件
    */
    table.bind('click', '.delt', function (data, index) {
        table.removeRow(data);
    });

    /*
     * 产品代码change事件
     */
    $('#finprodId').on('change', function () {
        finprodIdChangrAction();
        commonChangeAction();
    });

    $('#BussFinProductFloatFee_feeId').on('change', function () {
        feeIdChangeAction();
    });

    var feeIdChangeAction = function () {
        var data = $('#BussFinProductFloatFee_feeId').select2('data')[0].object;
        $("#BussFinProductFloatFee_basicBill").val('').attr("disabled", false).trigger('change');
        $("#BussFinProductFloatFee_basis").val('').attr("disabled", false).trigger('change');

        if (data != null && data != '') {
            var dataArr = data.split(",");
            if (dataArr != null && dataArr[0]) {
                $("#BussFinProductFloatFee_basicBill").val(dataArr[0]).attr("disabled", true).trigger('change');
            }

            if (dataArr != null && dataArr[1]) {
                $("#BussFinProductFloatFee_basis").val(dataArr[1]).attr("disabled", true).trigger('change');
            }

        }

    }

    /**
     * 投资周期文本框change事件
     */
    $('#investCycle2').on('change', function () {
        //期限开始日
        var srartDate = $('#startDate').val();
        //投资周期
        var investCycle = $('#investCycle2').val();
        if (srartDate != "" && investCycle != "" && pageModel != PAGE_MODEL_REFERENCE) {
            //期限结束日 = 期限开始日 + 投资周期
            var endDate = DateUtil.parse(srartDate).DateAdd('d', investCycle).Format('yyyy-MM-dd');
            if ($('#operateType').val() != 'look' && $('#operateType').val() != 'edit') {
                $("#endDate").val(endDate).trigger('change');
            }
        }
        commonChangeAction();
    });

    /**
     * 投资周期下拉框change事件
     */
    $('#investCycle').on('change', function () {

        //产品代码
        var finprodId = $('#finprodId').val();
        //期限开始日
        var srartDate = $('#startDate').val();
        //投资周期
        var investCycle = $('#investCycle').val();
        if (srartDate != "" && investCycle != "" && pageModel != PAGE_MODEL_REFERENCE) {
            //期限结束日 = 期限开始日 + 投资周期
            var endDate = DateUtil.parse(srartDate).DateAdd('d', investCycle).Format('yyyy-MM-dd');
            $("#endDate").val(endDate).trigger('change');
        }
        commonChangeAction();
    });

    var finprodIdChangrAction = function () {
        if (fromToDoList) {
            return;
        }
        var finprodId = $('#finprodId').val();
        if ($('#periodId').val() === 'undefined') {
            $('#periodId').val('');
        }
        var branch = $('#branch').val();
        var periodId = '';
        if ($('#operateType').val() == 'confrim') {
            periodId = $('#periodProdId').val();
        } else {
            periodId = $('#periodId').val();
            if (periodId == null) {
                periodId = $('#periodProdId').val();
            }
        }
        if (finprodId != "" && $('#operateType').val() != 'confrim') {
            //查询基本信息
            Joyin.ajax(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/selectInfoByfinprodId', {
                finprodId: finprodId,
                branch: branch,
                periodId: periodId
            }, function (result) {
                //1.基本信息
                $("#finprodAbbr").val(result.finprodAbbr).trigger('change');
                $("#ccy").val(result.ccy).trigger('change');
                $("#termType").val(result.termType).trigger('change');
                $("#termType").val(result.termType);
                $("#profitType").val(result.profitType).trigger('change');
                $("#periodProdId").val(result.finprodId).trigger('change');

                if (result.termType == '01') {
                    //当期限品种为单一固定期限
                    $('#investCycleSelect').hide();
                    $('#investCycleInput').show();
                    if (result.investCycle != null) {
                        $('#investCycle2').val(result.investCycle).prop('disabled', true).trigger('change');
                    }
                } else if (result.termType == '02') {
                    //当期限品种多固定期限
                    $('#investCycleSelect').show();
                    $('#investCycleInput').hide();
                    //解决多固定期限不显示 BSTOT-689 add by cxq 20200122
                    if (result.investCycle != null) {
                        $('#investCycle2').val(result.investCycle).prop('disabled', true).trigger('change');
                    }
                } else {
                    //当期限品种为无固定期限
                    $('#investCycleSelect').hide();
                    $('#investCycleInput').show();
                    //修改bug【期次管理】有效状态的数据，双击查看，投资周期（天）字段值丢失。 add by wangcheng 20191030
//    				$('#investCycle2').val('').prop('disabled', false).trigger('change');
                    console.log("-----investCycle-------")
                    console.log(result.investCycle)
                    if (result.investCycle != null) {
                        $('#investCycle2').val(result.investCycle).prop('disabled', false).trigger('change');
                    } else {
                        $('#investCycle2').val('').prop('disabled', false).trigger('change');
                    }
                }
                // $('#investCycle2').val(result.investCycle).prop('disabled', true).trigger('change');
                if (pageModel == PAGE_MODEL_REFERENCE) {
                    commonChangeAction();
                }
            });
        }
    }

    var commonChangeAction = function () {

        if (fromToDoList) {
            return;
        }
        var finprodId = $('#finprodId').val();
        var termType = $('#termType').val();
        var investCycle;
        if (termType == '02') {
            investCycle = $('#investCycle').val();
        } else if (termType == '01') {
            investCycle = $('#investCycle2').val();
        }

        var bussFinProductFloatFeeRateType = $("#BussFinProductFloatFee_rateType").val()
        console.log("-----bussFinProductFloatFeeRateType-----");
        console.log(bussFinProductFloatFeeRateType);
        var bussFinProductFloatFeeRate = $("#BussFinProductFloatFee_rate").val();
        console.log(bussFinProductFloatFeeRate);
        var bussFinProductFloatFeeIndexId = $("#BussFinProductFloatFee_indexId").val();
        var bussFinProductFloatFeeCoefficient = $("#BussFinProductFloatFee_coefficient").val();
        var bussFinProductFloatFeeSpreadRate = $("#BussFinProductFloatFee_spreadRate").val();
        var bussFinProductFloatFeeResetType = $("#BussFinProductFloatFee_resetType").val();
        var bussFinProductFloatFeeRemark = $("#BussFinProductFloatFee_remark").val();

        if (finprodId && termType) {
            //当产品为多固定期限时 return false
            if (termType != '03' && !investCycle) {
                return false;
            }
            //根据产品代码 、投资周期查询 查询 期限产品代码
            Joyin.ajax(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/selectPeriodProdId', {
                finprodId: finprodId,
                investCycle: investCycle
            }, function (result) {
                if (result) {
                    $('#periodProdId').val(result.periodProdId).trigger('change');
                }
                if (result.periodProdId) {
                    //根据期限产品代码 查询 业绩基准信息、 浮动管理费信息 、分层列表
                    //add branch termType 解决多固定期限产品不显示问题  by cxq 20200122
                    Joyin.ajax(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/selectAllInfo', {
                        periodProdId: $('#periodProdId').val(),
                        branch: $('#branch').val(),
                        termType: $('#termType').val()
                    }, function (result2) {
                        //2.业绩基准信息
                        if (result2) {
                            if (result2.bussBenchMarkInfoDto) {
                                $("#BussBenchMarkInfo_publishType").val(result2.bussBenchMarkInfoDto.publishType).trigger('change');
                                $("#BussBenchMarkInfo_rateType").val(result2.bussBenchMarkInfoDto.rateType).trigger('change');
                                $("#BussBenchMarkInfo_rate").val(result2.bussBenchMarkInfoDto.rate).trigger('change');
                                $("#BussBenchMarkInfo_indexId").val(result2.bussBenchMarkInfoDto.indexId).trigger('change');
                                $("#BussBenchMarkInfo_coefficient").val(result2.bussBenchMarkInfoDto.coefficient).trigger('change');
                                $("#BussBenchMarkInfo_spreadRate").val(result2.bussBenchMarkInfoDto.spreadRate).trigger('change');
                                $("#BussBenchMarkInfo_resetType").val(result2.bussBenchMarkInfoDto.resetType).trigger('change');
                                //修改 bug 新建画面，选择产品的投资周期对应的业绩基准为区间披露，带不出利率区间 add by wangcheng 20191119 start
                                $("#BussBenchMarkInfo_rateLower").val(result2.bussBenchMarkInfoDto.rateLower).trigger('change');
                                $("#BussBenchMarkInfo_rateLimit").val(result2.bussBenchMarkInfoDto.rateLimit).trigger('change');
                                //修改 bug 新建画面，选择产品的投资周期对应的业绩基准为区间披露，带不出利率区间 add by wangcheng 20191119 end
                            } else {
                                clearbussBenchMarkInfo();
                            }

                            //3.浮动管理费信息
                            if (result2.bussFinProductFloatFeeDto) {
                                $("#BussFinProductFloatFee_DIV").show();
                                $("#periodCashId").val(result2.bussFinProductFloatFeeDto.periodCashId).trigger('change');
                                if (result2.bussFinProductFloatFeeDto.feeId != null && result2.bussFinProductFloatFeeDto.feeId != '') {
                                    $('#BussFinProductFloatFee_feeId').textSelect('setValue', result2.bussFinProductFloatFeeDto.feeId);
                                } else {
                                    $("#BussFinProductFloatFee_feeId").val('').trigger("change");
                                }
                                $("#BussFinProductFloatFee_basicBill").val(result2.bussFinProductFloatFeeDto.basicBill).trigger('change');
                                $("#BussFinProductFloatFee_basis").val(result2.bussFinProductFloatFeeDto.basis).trigger('change');

                                if (bussFinProductFloatFeeRateType) {
                                    $("#BussFinProductFloatFee_rateType").val(bussFinProductFloatFeeRateType).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.rateType) {
                                    $("#BussFinProductFloatFee_rateType").val(result2.bussFinProductFloatFeeDto.rateType).trigger('change');
                                }

                                if (bussFinProductFloatFeeRateType == '02') {
                                    $("#BussFinProductFloatFee_rate").val('').trigger('change');
                                } else if (bussFinProductFloatFeeRate) {
                                    $("#BussFinProductFloatFee_rate").val(bussFinProductFloatFeeRate).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.rate) {
                                    $("#BussFinProductFloatFee_rate").val(result2.bussFinProductFloatFeeDto.rate).trigger('change');
                                }

                                if (bussFinProductFloatFeeIndexId) {
                                    $("#BussFinProductFloatFee_indexId").val(bussFinProductFloatFeeIndexId).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.indexId) {
                                    $("#BussFinProductFloatFee_indexId").val(result2.bussFinProductFloatFeeDto.indexId).trigger('change');
                                }
                                if (bussFinProductFloatFeeCoefficient) {
                                    $("#BussFinProductFloatFee_coefficient").val(bussFinProductFloatFeeCoefficient).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.coefficient) {
                                    $("#BussFinProductFloatFee_coefficient").val(result2.bussFinProductFloatFeeDto.coefficient).trigger('change');
                                }
                                if (bussFinProductFloatFeeSpreadRate) {
                                    $("#BussFinProductFloatFee_spreadRate").val(bussFinProductFloatFeeSpreadRate).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.spreadRate) {
                                    $("#BussFinProductFloatFee_spreadRate").val(result2.bussFinProductFloatFeeDto.spreadRate).trigger('change');
                                }
                                if (bussFinProductFloatFeeResetType) {
                                    $("#BussFinProductFloatFee_resetType").val(bussFinProductFloatFeeResetType).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.resetType) {
                                    $("#BussFinProductFloatFee_resetType").val(result2.bussFinProductFloatFeeDto.resetType).trigger('change');
                                }
                                if (bussFinProductFloatFeeRemark) {
                                    $("#BussFinProductFloatFee_remark").val(bussFinProductFloatFeeRemark).trigger('change');
                                } else if (result2.bussFinProductFloatFeeDto.remark) {
                                    $("#BussFinProductFloatFee_remark").val(result2.bussFinProductFloatFeeDto.remark).trigger('change');
                                }
                            } else {
                                clearbussFinProductFloatFee();
                                $("#BussFinProductFloatFee_DIV").hide();
                            }
                        }
                    });
                    //4.业绩分层列表
                    table.reload();
                } else {
                    clearbussBenchMarkInfo();
                    clearbussFinProductFloatFee();
                    $("#BussFinProductFloatFee_DIV").show();
                    table.reload();
                }
            });
        }
    }

    //清除业绩基准信息
    var clearbussBenchMarkInfo = function () {
        $("#BussBenchMarkInfo_publishType").val('').trigger('change');
        $("#BussBenchMarkInfo_rateType").val('').trigger('change');
        $("#BussBenchMarkInfo_rate").val('').trigger('change');
        $("#BussBenchMarkInfo_indexId").val('').trigger('change');
        $("#BussBenchMarkInfo_coefficient").val('').trigger('change');
        $("#BussBenchMarkInfo_spreadRate").val('').trigger('change');
        $("#BussBenchMarkInfo_resetType").val('').trigger('change');
        $("#BussBenchMarkInfo_rateLower").val('').trigger('change');
        $("#BussBenchMarkInfo_rateLimit").val('').trigger('change');
    }

    //清除浮动管理费信息
    var clearbussFinProductFloatFee = function () {
        $("#periodCashId").val('').trigger('change');
        $("#BussFinProductFloatFee_feeId").val('').trigger("change");
        $("#BussFinProductFloatFee_basicBill").val('').trigger('change');
        $("#BussFinProductFloatFee_basis").val('').trigger('change');
        $("#BussFinProductFloatFee_rateType").val('').trigger('change');
        $("#BussFinProductFloatFee_rate").val('').trigger('change');
        $("#BussFinProductFloatFee_indexId").val('').trigger('change');
        $("#BussFinProductFloatFee_coefficient").val('').trigger('change');
        $("#BussFinProductFloatFee_spreadRate").val('').trigger('change');
        $("#BussFinProductFloatFee_resetType").val('').trigger('change');
        $("#BussFinProductFloatFee_remark").val('').trigger('change');
    }

    var publishTypeChangeAction = function () {
        var publishType = $('#BussBenchMarkInfo_publishType').val();
        //是否区间披露为 是
        if (publishType == '01') {
            //利率类型、利率、浮动利率、系数、利差、业绩基准重置规则不可见
            $('#BussBenchMarkInfo_rateType1').hide();
            $('#BussBenchMarkInfo_rate1').hide();
            $('#BussBenchMarkInfo_indexId1').hide();
            $('#BussBenchMarkInfo_resetType1').hide();
            $('#BussBenchMarkInfo_div2').show();
        } else {
            $('#BussBenchMarkInfo_rateType1').show();
            $('#BussBenchMarkInfo_rate1').show();
            $('#BussBenchMarkInfo_indexId1').show();
            $('#BussBenchMarkInfo_resetType1').show();
            $('#BussBenchMarkInfo_div2').hide();
        }
    }

    $('#startDate').on('change', function () {
        //期限开始日
        var srartDate = $('#startDate').val();
        //期限品种
        var termType = $('#termType').val();
        //投资周期
        var investCycle = null;
        if (termType == '02') {
            investCycle = $('#investCycle').val();
        } else {
            investCycle = $('#investCycle2').val();
        }
        if (srartDate && investCycle && pageModel != PAGE_MODEL_EDIT && pageModel != PAGE_MODEL_REFERENCE) {
            //期限结束日 = 期限开始日 + 投资周期
            var endDate = DateUtil.parse(srartDate).DateAdd('d', investCycle).Format('yyyy-MM-dd');
            $("#endDate").val(endDate).trigger('change');
        }
    });

    //业绩基准 是否区间披露
    $('#BussBenchMarkInfo_publishType').on('change', function () {
        publishTypeChangeAction();
    });

    //业绩基准利率类型
    $('#BussBenchMarkInfo_rateType').on('change', function () {

        var rateType = $('#BussBenchMarkInfo_rateType').val();
        if (rateType == '01') {
            $('#BussBenchMarkInfo_indexId').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_coefficient').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_spreadRate').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_resetType').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_rate').val('').trigger('change').prop('disabled', false);
        } else {
            $('#BussBenchMarkInfo_indexId').val('').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_coefficient').val('1.00').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_spreadRate').val('').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_resetType').val('').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_rate').val('').trigger('change').prop('disabled', true);
        }
    });

    //
    $('#BussFinProductFloatFee_rateType').on('change', function () {

        var rateType = $('#BussFinProductFloatFee_rateType').val();
        if (rateType == '01') {
            $('#BussFinProductFloatFee_indexId').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_coefficient').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_spreadRate').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_resetType').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_rate').val('').trigger('change').prop('disabled', false);
        } else {
            $('#BussFinProductFloatFee_indexId').val('').trigger('change').prop('disabled', false);
            $('#BussFinProductFloatFee_coefficient').val('1.00').trigger('change').prop('disabled', false);
            $('#BussFinProductFloatFee_spreadRate').val('').trigger('change').prop('disabled', false);
            $('#BussFinProductFloatFee_resetType').val('').trigger('change').prop('disabled', false);
            $('#BussFinProductFloatFee_rate').val('').trigger('change').prop('disabled', true);
        }
    });

    var bussBenchMarkInfoRateType = function () {
        var rateType = $('#BussBenchMarkInfo_rateType').val();
        if (rateType == '01') {
            $('#BussBenchMarkInfo_indexId').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_coefficient').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_spreadRate').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_resetType').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_rate').trigger('change').prop('disabled', false);
        } else {
            $('#BussBenchMarkInfo_indexId').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_coefficient').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_spreadRate').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_resetType').trigger('change').prop('disabled', false);
            $('#BussBenchMarkInfo_rate').trigger('change').prop('disabled', true);
        }
    }


    var init = function () {
        //update by hechuan 2019-10-17 start
        var termType = $('#termType').val();
        if (termType == '01') {
            //当期限品种为单一固定期限
            $('#investCycleSelect').hide();
            $('#investCycleInput').show();
        } else if (termType == '02') {
            //当期限品种多固定期限
            $('#investCycleSelect').show();
            $('#investCycleInput').hide();
        } else {
            //当期限品种为无固定期限
            $('#investCycleSelect').hide();
            $('#investCycleInput').show();
        }
        //update by hechuan 2019-10-17 end
        if (pageModel == PAGE_MODEL_EDIT) {
            //修改bug【强化测试】期次管理修改期次画面，期次开始日字段应置灰不可修改，目前未置灰。 add by wangcheng 20191030
            $('#startDate').trigger('change').prop('disabled', true);
            $('#periodId').trigger('change').prop('disabled', true);
            $('#finprodId').trigger('change').prop('disabled', true);
        }
        //修改bug 待处理数据确认时 期次代码、期次开始日、期次结束日、投资周期置灰不可修改 add by wangcheng 20191101 start
        if ($('#operateType').val() == 'confrim' || $('#operateType').val() == 'new') {
            $('#periodId').trigger('change').prop('disabled', true);
            $('#startDate').trigger('change').prop('disabled', true);
//    		$('#endDate').trigger('change').prop('disabled', true);
            $('#investCycle').trigger('change').prop('disabled', true);
            if ($('#termType').val() == '01' || termType == '02' || termType == '') {
                $('#investCycle2').trigger('change').prop('disabled', true);
            }
            //期限开始日
            var srartDate = $('#startDate').val();
            //期限结束日
            var endDateInit = $('#endDate').val();
            //期限品种
            var termType = $('#termType').val();
            //投资周期
            var investCycle = null;
            if (termType == '02') {
                investCycle = $('#investCycle').val();
            } else {
                investCycle = $('#investCycle2').val();
            }
            if (srartDate && investCycle && !endDateInit && pageModel != PAGE_MODEL_REFERENCE) {
                //期限结束日 = 期限开始日 + 投资周期
                var endDate = DateUtil.parse(srartDate).DateAdd('d', investCycle).Format('yyyy-MM-dd');
                $("#endDate").val(endDate).trigger('change');
            }

        }
        //修改bug 待处理数据确认时 期次代码、期次开始日、期次结束日、投资周期置灰不可修改 add by wangcheng 20191101 end
        finprodIdChangrAction();
        publishTypeChangeAction();
        bussBenchMarkInfoRateType();
        feeIdChangeAction();

        var rateType = $('#BussFinProductFloatFee_rateType').val();
        if (rateType == '01') {
            $('#BussFinProductFloatFee_indexId').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_coefficient').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_spreadRate').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_resetType').val('').trigger('change').prop('disabled', true);
            $('#BussFinProductFloatFee_rate').trigger('change').prop('disabled', false);
        }
//    	else{
//    		$('#BussFinProductFloatFee_indexId').val('').trigger('change').prop('disabled', false);
//    		$('#BussFinProductFloatFee_coefficient').val('1.00').trigger('change').prop('disabled', false);
//    		$('#BussFinProductFloatFee_spreadRate').val('').trigger('change').prop('disabled', false);
//    		$('#BussFinProductFloatFee_resetType').val('').trigger('change').prop('disabled', false);
//    		$('#BussFinProductFloatFee_rate').val('').trigger('change').prop('disabled', true);
//    	}
        $('#periodProdId2').val($('#periodProdId').val());
    }
    /**
     * 绑定按钮事件
     */
    $(function () {
        init();
        if ($('#BussBenchMarkInfo_rateType').val() == '01') {
            $('#BussBenchMarkInfo_indexId').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_coefficient').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_spreadRate').val('').trigger('change').prop('disabled', true);
            $('#BussBenchMarkInfo_resetType').val('').trigger('change').prop('disabled', true);
        } else {
//    		$('#BussBenchMarkInfo_indexId').val('').trigger('change').prop('disabled', false);
//    		$('#BussBenchMarkInfo_coefficient').val('1.00').trigger('change').prop('disabled', false);
//    		$('#BussBenchMarkInfo_spreadRate').val('').trigger('change').prop('disabled', false);
//    		$('#BussBenchMarkInfo_resetType').val('').trigger('change').prop('disabled', false); 
        }

        // 绑定按钮事件
        $('#PeriodicManagement002_btn_submit').click(function () {
            saveOrSubmit("submit");
        });
        //  绑定导出事件
        $('#PeriodicManagement002_btn_save').click(function () {
            saveOrSubmit("save");
        });
    });

    var clearPublishTypeChange = function () {
        var publishType = $('#BussBenchMarkInfo_publishType').val();
        //是否区间披露为 是
        if (publishType == '01') {
            //利率类型、利率、浮动利率、系数、利差、业绩基准重置规则不可见
            $('#BussBenchMarkInfo_rateType').val('').trigger('change');
            $('#BussBenchMarkInfo_rate').val('').trigger('change');
            $('#BussBenchMarkInfo_indexId').val('').trigger('change');
            $('#BussBenchMarkInfo_coefficient').val('').trigger('change');
            $('#BussBenchMarkInfo_spreadRate').val('').trigger('change');
            $('#BussBenchMarkInfo_resetType').val('').trigger('change');
        } else {
            $('#BussBenchMarkInfo_rateLower').val('').trigger('change');
            $('#BussBenchMarkInfo_rateLimit').val('').trigger('change');
        }
    }

    var getSaveObject = function () {
        var object = $.serializeToObject('#PeriodicManagement002_Form');

        console.log("-----------operateType-------------");
        console.log(object.operateType);

        if ("edit" != object.operateType) {
            object.operateType = "new";
        }

        object.pageModel = pageModel;
        object.workflowId = workflowId;
        object.periodProdId = $('#periodProdId').val();
        if (object.periodProdId == null || object.periodProdId == "") {
            object.periodProdId = $('#periodProdId2').val();
        }
        var finFloatCalcRuleDetailList = table.getAllRows();
        object.finFloatCalcRuleDetailList = finFloatCalcRuleDetailList;
        return object;
    }

    function saveOrSubmit(operateType) {
        var object = getSaveObject();
        object.saveOrSubmit = operateType;
        console.log("-----------object.saveOrSubmit-------------");
        console.log(object.saveOrSubmit);

        Joyin.ajaxJson(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/check', object, function (result) {
            Joyin.confirm(result.data, function () {
                clearPublishTypeChange();
                object = getSaveObject();
                console.log("-----------object-------------");
                console.log(object);

                if (operateType == 'save') {
                    WorkFlow.doSaveList(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/save', object, "PeriodicManagement002_btn_save");
                } else {
                    WorkFlow.doSubmitList(basePath + 'invest/product/productelement/periodicmanagement/PeriodicManagement002/save', object, "PeriodicManagement002_btn_submit");
                }
            })
        })

    }
});



