(function () {
    // 风险试算结果
    var riskResultResponse = entity_RiskcalResponse;
    var initFlag = false;
    var cluflag;
    var businessId;
    var riskFlag = 0;
    var rskApproval;

    /**
     * 画星星
     */
    var star = function () {
        // 加红星星
        // 查找label
        var _findPrevLabel = function (element, level) {
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
            && (pageModel !== PAGE_MODEL_REFERENCE)) {
            $('.must').each(function () {
                $(this).remove();
            })
            $('*[required]').each(function () {
                var $el = $(this);
                var $target = _findPrevLabel($el, 3);
                if ($target.find('> .must').length === 0) {
                    $target.append($('<i class="must">*</i>'));
                }
            });
        }
    }

    /**
     * 获取风险试算参数
     *
     * @param operateType 操作类型（save，submit）
     * @returns
     */
    function getSearchParams() {
        var object = $.serializeToObject("#searchForm");
        return object;
    }

    var cols = [

        {
            /* 组合代码 */
            field: 'portfolioId',
            width: 200,
            title: 'EtribRepoInfo003.portfolioId',
            edit: (repo_pledge_model == '02' && $("#ps").val() == '03') ? false : true,
            sortable: false,
            editOption: {
                type: 'select',
                id: 'portfolioId',
                multiple: false,
                showField: 'text',
                childrenIds : 'zyqk',
                searchable: true,
                url: JoyinUtil.getRealPath('/business/common/CommonSelect/selectPortfolioId?act=true')
            }
        },

        {
            /* 质押券库代码 */
            field: 'zyqk', width: 200, title: 'EtrIbRepo001.zyqk',
            visible: (repo_pledge_model == '03' && $("#ps").val() == '03') ? true : false,
            edit: true,
            sortable: false,
            editOption: {
                type: 'select',
                id: 'zyqk',
                multiple: false,
                showField: 'text',
                parentIds : 'portfolioId',
                searchable: true,
                url: JoyinUtil.getRealPath('/cfets/etrib/cfetsEtrIbRepoinfo/getPledgeStockId')
            }
        }
        , {
            /*指令金额*/
            field: 'entrustAmt',
            width: 200,
            title: '指令金额（元）',
            edit: true,
            sortable: false,
            type: 'number',
            digit: 2
        },
        {
            field: "totalAmount",
            width: 200,
            title: '质押券折算总额(元)',
            visible: repo_pledge_model == '02' ? true : false,
            sortable: false,
            type: 'number',
            digit: 2
        },
        {
            /*可质押券面总额*/
            field: 'avaPledgePrin',
            width: 200,
            title: '可质押券面总额（万元）',
            visible: ((repo_pledge_model == '01' || repo_pledge_model == '03') && $("#ps").val() == '03') ? true : false,
            sortable: false,
            type: 'number',
            digit: 2
        },
        {
            /*组合t日可用*/
            field: 'availableCashT',
            width: 200,
            title: 'T日可用现金（元）',
            edit: false,
            sortable: false,
            type: 'number',
            digit: 2
        }, {
            /*组合t+1可用*/
            field: 'availableCashT1',
            width: 200,
            title: 'T+1日可用现金（元）',
            edit: false,
            sortable: false,
            type: 'number',
            digit: 2
        }, {
            /*账户现金*/
            field: 'accountCash',
            width: 200,
            title: '账户现金（元）',
            edit: false,
            sortable: false,
            type: 'number',
            digit: 2
        } , {
            field: 'entrustId',
            title: '指令Id',
            width: 200,
            visible: false
        },
        {field:'operation', width:20, title: 'SysCommon.operation',fixed:"right", sortable:false, type: 'action', buttons: ['delt']}
    ]
    var table = $("#etrInvestRepoInfoDtos").joyintable({
        data: entity_BussCombDistribtList || [{}],
        cols: cols,
        page: false,
        addbtn: (repo_pledge_model == '02' && $("#ps").val() == '03') ? false : true,
        addToLast: true,
        searchOnInit: true,
        checkbox: false,
        height: 190,
        done:function(){
            this.bind('click', '.delt', function(data, index) {
                var allRow = table.getAllRows();
                if(allRow.length > 1) {
                    table.deleteRow(index);
                }else {
                    Joyin.error("至少要保留一个组合");
                }
            });
        },
         beforeEditCell: function (preVal, field, rowData) {
            return true;
        }, editCell: function (value, data, field, cell, column) {
            setRiskFlag(1);
            if (field == 'entrustAmt') {
                var amt = 0;
                var allRow = table.getAllRows();
                $.each(allRow,function (index,item) {
                    amt = JoyinMath.doAdd(amt,item.entrustAmt,2);
                })
                $("#entrustAmt").val(amt);
            }
            if (field == 'portfolioId') {
                var allRow = table.getAllRows();
                var exist = 0;
                $.each(allRow,function (index,item) {
                    if(value == item.portfolioId) {
                        exist = exist + 1;
                    }
                })
                if(exist > 1) {
                    Joyin.error(value+"组合已经存在,不可以重复选择");
                    data.portfolioId = null;
                    table.renderRow(data.LAY_TABLE_INDEX, data);
                    return;
                }
                var arr = [];
                var param = {}
                arr.push(value);
                param.portfolioIdList = arr;
                Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbBondinfo/queryPositionInfo', param, function (result) {
                    if(result.data) {
                        $.each(result.data,function (index,item) {
                            if(item.field == 'T0KYXJ') {//T日可用现金（元）
                                if ("--" == item.amount) {
                                    data.availableCashT = 0;
                                } else {
                                    data.availableCashT = item.amount;
                                }
                            }
                            if(item.field == 'T1KYXJ') {//T1日可用现金（元）
                                if ("--" == item.amount) {
                                    data.availableCashT1 = 0;
                                } else {
                                    data.availableCashT1 = item.amount;
                                }
                            }
                            if(item.field == 'ZHXJ') {//账户现金
                                if ("--" == item.amount) {
                                    data.accountCash = 0;
                                } else {
                                    data.accountCash = item.amount;
                                }
                            }
                        })
                        table.renderRow(data.LAY_TABLE_INDEX, data);
                    }

                });
                if(!data.entrustId) {
                    Joyin.ajaxSync(basePath + 'cfets/etrib/CfetsEtrIbBondinfo003/getEntrustId',{},function (result) {
                        data.entrustId = result.data;
                        table.renderRow(data.LAY_TABLE_INDEX, data);
                    })
                }
                if(repo_pledge_model == '01' || repo_pledge_model == '03') {
                    var param = {};
                    param.portfolioId = value;
                    param.pledgeStockId = "999999";
                    if(data.zyqk) {
                        param.pledgeStockId = data.zyqk;
                    }
                    Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo/getPledgeable', param, function (result) {
                        if(result.data) {
                            data.avaPledgePrin = result.data / 10000;
                            table.renderRow(data.LAY_TABLE_INDEX, data);
                        }
                    });
                }
            }
            if (field == 'zyqk') {
                var param = {};
                param.portfolioId = data.portfolioId;
                param.pledgeStockId = value;
                Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo/getPledgeable', param, function (result) {
                    if(result.data) {
                        data.avaPledgePrin = result.data;
                        table.renderRow(data.LAY_TABLE_INDEX, data);
                    }
                });
            }
            return true;
        }

    });
    var colsRiskTrialRet = [
        {
            field: 'kriStatusCn', title: 'BaseRiskMathComm.currStatus', sortable: false,
            render: function (data, type, row, meta) {
                if (row.kriStatus == 0) {
                    return "<span>正常</span>";
                }
                if (row.kriStatus == 1) {
                    return "<span style='color:orange'>预警</span>";
                }
                if (row.kriStatus == 2) {
                    return "<span style='color:#F7580D'>审批</span>";
                }
                if (row.kriStatus == 3) {
                    return "<span style='color:#F71A0D'>违规</span>";
                }
                if (row.kriStatus == 4) {
                    return "<span style='color:#951F1F'>异常</span>";
                }
                return row.kriStatus;
            }
        }
        , {
            /* 指标名称*/
            field: 'kriName', width: 200, title: 'BaseRiskMathComm.indName', sortable: false
        }
        , {/*指标类型*/
            field: 'kriTypeCn',
            width: 100,
            title: 'BaseRiskMathComm.indType',
            sortable: false
        }
        , {
            /*组合名称*/
            field: 'portfolioName',
            width: 150,
            title: 'BaseRiskMathComm.portfolioName',
            sortable: false
        }
        , {
            /*预警阈值*/
            field: 'warningLine',
            width: 150,
            title: 'BaseRiskMathComm.wthreshold',
            sortable: false
        }
        , {
            /*审批线*/
            field: 'approveLine',
            width: 150,
            title: 'BaseRiskMathComm.approveold',
            sortable: false
        }
        , {
            /*当前实际值*/
            field: 'kriValue',
            width: 200,
            title: 'BaseRiskMathComm.currValue',
            sortable: false
        }
        , {
            /*原值*/
            field: 'oldValue',
            width: 200,
            title: 'BaseRiskMathComm.preValue',
            sortable: false
        }
        , {
            /*趋势*/
            field: 'kriTrend',
            width: 200,
            title: 'BaseRiskMathComm.kriTrend',
            sortable: false
        }

    ]

    var tableRisk = $("#riskTrialRetTable").joyintable({
        data: entity_RiskTrialRetList || [],
        cols: colsRiskTrialRet,
        page: true,
        addToLast: true,
        searchOnInit: true,
        checkbox: false,
        height: 290
    });


    var init = function () {
        if ($('#ps').val() == '04') {
            $("#pledgeBondTotalTblDetailed_div").hide();
        }
    }
    var pledgeBondTotalTblDetailed;
    if (repo_pledge_model == '02') {
        /**
         * 质押券汇总信息（Total）
         */
        pledgeBondTotalTblDetailed = $("#pledgeBondTotalListDetailed").joyintable({
            page: false,
            data: entity_PledgeBondTotalListDetailed || [],
            height: 200,
            searchOnInit: true,
            checkbox: false,
            /**
             * 定义表头
             */
            cols: [
                // 委托代码
                /* {field: 'entrustId',title: 'BussRepoSecPtl.entrustId',type: 'text',align: 'left',sortable: false,edit: false},*/
                // 债券代码
                {
                    field: 'finprodMarketId',
                    title: 'BussRepoSecPtl.secId',
                    type: 'text',
                    align: 'left',
                    sortable: false,
                    edit: false,
                    width: 200,
                    visible:false
                }
                // 债券简称
                , {
                    field: 'finprodAbbr',
                    title: '债券简称',
                    type: 'text',
                    align: 'left',
                    sortable: true,
                    edit: false,
                    width: 200
                }
                // 债券类型
                /*,{field: 'secTypeStr',title: 'BussRepoSecPtl.secType',type: 'text',align: 'left',edit: false,sortable: false}*/
                // 组合代码
                , {
                    field: 'portfolioId',
                    title: 'BussRepoSecPtl.portfolioId',
                    type: 'text',
                    sortable: false,
                    edit: false,
                    width: 150,
                    visible:false
                }
                // 组合名称
                , {
                    field: 'portfolioName',
                    title: 'BussRepoSecPtl.portfolioName',
                    type: 'text',
                    edit: false,
                    sortable: false,
                    width: 200
                }
                // 质押本方券券面总额
//                , {field: 'pledgeBfPrin',title: '质押本方券券面总额',type: 'number',edit: true,sortable: false}
                // 质押融入券券面总额
//                , {field: 'pledgeRrPrin',title: '质押融入券券面总额',type: 'number',edit: true,sortable: false}
                // 可质押券面总额
                , {
                    field: 'pledStrdPrinAmt',
                    title: '可质押券面总额（万元）',
                    type: 'number',
                    edit: false,
                    sortable: false,
                    width: 200
                }
                //质押券汇总券面总额
                , {
                    field: 'pledgePrin', title: '质押券面总额（万元）', type: 'number', edit: true, sortable: false, width: 240
                }
                // 估价净价
                , {
                    field: 'firstNetPrice',
                    width: 150, digit: 4,
                    title: '估价净价（元）',
                    sortable: false,
                    render: function (data, type, row, meta) {
                        return (data * 1).toFixed(4);
                    }
                }
                //折算比例%
                , {
                    field: 'scale',
                    title: '折算比例(%)',
                    type: 'number',
                    edit: true,
                    sortable: false,
                    digit: 2,
                    width: 100
                }
                // 登记托管机构
                , {
                    field: 'registOrgStr',
                    title: 'BussRepoSecPtl.repRegistOrg',
                    type: 'text',
                    align: 'left',
                    edit: false,
                    sortable: false,
                    width: 200
                }

            ], editCell: function (value, data, field, cell, column) {
                if (field == 'pledgePrin') {
                    if(value > data.pledStrdPrinAmt) {
                        Joyin.error("质押券面总额不能大于可质押券面总额");
                        data.pledgePrin = data.pledStrdPrinAmt;
                        pledgeBondTotalTblDetailed.renderRow(data.LAY_TABLE_INDEX, data);
                    }
                    var allRow = table.getAllRows();
                    $.each(allRow,function (index,item) {
                        item.totalAmount = value * 100 * data.scale;
                        table.renderRow(item.LAY_TABLE_INDEX, item);
                    })
                }
                if(field == 'scale') {
                    var rows = pledgeBondTotalTblDetailed.getAllRows();
                    var rowData = null;
                    $.each(rows, function(i, obj){
                        if(obj.finprodMarketId == data.finprodMarketId) {
                            rowData = obj;
                            rowData.scale = value;
                            pledgeBondTotalTblDetailed.renderRow(obj.LAY_TABLE_INDEX, rowData);
                        }
                    })
                    var allRow = table.getAllRows();
                    $.each(allRow,function (index,item) {
                        item.totalAmount = data.pledgePrin * 100 * value;
                        table.renderRow(item.LAY_TABLE_INDEX, item);
                    })
                }
                setRiskFlag(1);
                return true;
            },
            done:function () {
                var rows = pledgeBondTotalTblDetailed.getAllRows();
                $.each(rows, function(i, obj){
                    obj.scale = obj.scale * 100;
                    obj.pledgePrin = obj.pledgePrin / 10000;
                    obj.pledStrdPrinAmt = obj.pledStrdPrinAmt /10000;
                    pledgeBondTotalTblDetailed.renderRow(obj.LAY_TABLE_INDEX, obj);
                })
            }
        });

    }
    init();

    /**
     * 获取提交的数据
     * @param operateType
     * @returns {{}}
     */
    function getSubmitParam(operateType) {
        var res = {}
        var object = $.serializeToObject('#searchForm');

        var param = {};
        param = JSON.parse(JSON.stringify(object));
        param.entrustAmt = object.entrustAmt;
        param.operateType = operateType;
        param.isDzh = 'Y';
        param.pageModel = pageModel;
        param.cfetsEtribdoRiskcalResponse = riskResultResponse;
        param.elementId = 'CfetsEtrIbRepoinfo003_btn_submit';
        param.pageId = "CfetsEtrIbRepoinfo003";
        param.rskApproval = rskApproval;
        if(param.price) {
            param.price = param.price / 100;
        }
        if(param.pledgeBondTotalListDetailed) {
            $.each(param.pledgeBondTotalListDetailed,function (index,obj) {
                obj.scale = obj.scale / 100;
                obj.pledgePrin = obj.pledgePrin * 10000;
                obj.pledStrdPrinAmt = obj.pledStrdPrinAmt * 10000;
            })
        }
        if(param.etrInvestRepoInfoDtos) {
            $.each(param.etrInvestRepoInfoDtos,function (index,item) {
                if(item.avaPledgePrin) {
                    item.avaPledgePrin = item.avaPledgePrin * 10000;
                }
            })
        }
        res.param = param;
        return res;
    }
    function setRiskFlag(flag) {
        riskFlag = flag;
    }
    function paramCheck() {
        var object = $.serializeToObject('#searchForm');
        if (!object.entrustAmt || !object.settleSpreed || !object.priceModel || !object.price || !object.repoTerm || !object.termType) {
            Joyin.error("有*必填项未填写,请填写完整")
            return false;
        }
        if(object.etrInvestRepoInfoDtos) {
            var avaPledgePrin = 0;
            var totalAmount = 0;
            var entrustAmt = 0;
            $.each(object.etrInvestRepoInfoDtos,function (index,item) {
                entrustAmt = item.entrustAmt + entrustAmt;
                avaPledgePrin = item.avaPledgePrin * 10000 + avaPledgePrin;
                totalAmount = item.totalAmount + totalAmount;
            })
            if(repo_pledge_model == '02' && Number(entrustAmt) > Number(totalAmount)) {
                Joyin.error("指令金额不能大于质押券折算总额")
                return false;
            }
            if((repo_pledge_model == '01' || repo_pledge_model == '03') && Number(entrustAmt) > Number(avaPledgePrin)) {
                Joyin.error("指令金额不能大于可质押券面总额")
                return false;
            }
        }
        // if(riskFlag === 1) {
        //     Joyin.error("请先完成风险试算")
        //     return false;
        // }

        var obj = getSubmitParam("submit");
        var errorMsg = "";
        var confirmMsg = "";
        $.ajax({
            type: "POST",
            url: basePath + 'cfets/etrib/cfetsEtrIbRepoinfo/checkSaveError',
            dataType: "json",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(obj.param),
            success: function (result) {
                if(result.code === '000') {
                    if(result.data && result.data.errorList && result.data.errorList.length > 0) {
                        $.each(result.data.errorList,function (index,item) {
                            errorMsg = errorMsg + item.message + "<br\>";
                        })
                    }
                    if(result.data && result.data.message && result.data.messageType === 'confirm') {
                        confirmMsg = result.data.message;
                    }
                }else {
                    errorMsg = result.message;
                }
            }
        });
        if(errorMsg && errorMsg.length > 0) {
            Joyin.error(errorMsg);
            return false;
        }
        if(confirmMsg && confirmMsg.length > 0) {
            rskApproval = result.data.data;
            return Joyin.confirm(confirmMsg, function (){
                saveOrSubmit('submit');
            });
        }
        return true;
    }


    function saveOrSubmit(operateType) {
        var res = getSubmitParam(operateType)
        // if(res.objects.length>0){
        //保存 走流程
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo003/save', res.param, "cfetsEtrIbRepoinfo003_btn_save", null,
                function () {
                    var index = window.parent.layer.getFrameIndex(window.name);
                    window.parent.layer.close(index);
                })
        }
        //提交 走流程
        else if (operateType === 'submit') {
            WorkFlow.doSubmitList(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo003/save', res.param, "cfetsEtrIbRepoinfo003_btn_submit", null,
                function () {
                    var index = window.parent.layer.getFrameIndex(window.name);
                    window.parent.layer.close(index);
                })
        }
        /*if (operateType === "save") {
            WorkFlow.doSaveList(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo003/save', res.param, "cfetsEtrIbRepoinfo003_btn_save", "cfetsEtrIbRepoinfo003");
        } else {
            WorkFlow.doSubmitList(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo003/save', res.param, "cfetsEtrIbRepoinfo003_btn_submit", "cfetsEtrIbRepoinfo003");
        }*/
        // }

    }

    // 绑定提交按钮
    $('#CfetsEtrIbRepoinfo003_btn_submit').click(function () {
        if (paramCheck()) {
            saveOrSubmit('submit');
        }
    });
    // 绑定保存按钮
    $('#CfetsEtrIbRepoinfo003_btn_save').click(function () {
        saveOrSubmit('save');
    });

    $('#CfetsEtrIbRepoinfo003_btn_riskcal').click(function () {
        var object = $.serializeToObject('#searchForm');
        var param = JSON.parse(JSON.stringify(object))
        $.ajax({
            type: "POST",
            url: basePath + 'riskTrial/doRiskcal/CfetsEtrIbRepoinfo003',
            dataType: "json",
            contentType: "application/json",
            async: false,
            data: JSON.stringify(param),
            success: function (result) {
                if(result.code === '000') {
                    var data = result.data.data;
                    if(data && data.riskTrialSpan) {
                        $("#riskTrialSpan").html("&nbsp;&nbsp;" + data.riskTrialSpan);
                    }
                    if(data){
                        riskResultResponse = data;
                        entity_RiskTrialRetList = data.riskcalReturnList;
                        if(entity_RiskTrialRetList) {
                            $.each(entity_RiskTrialRetList, function (index, item) {
                                item.conclusion = data.riskTrialSpan;
                            })
                        }
                    }
                    tableRisk.clear();
                    tableRisk.addRow(entity_RiskTrialRetList);
                    //setRiskFlag(0);
                }else if(result.message){
                    Joyin.error(result.message);
                }
                setRiskFlag(0);//软控先
            }
        });

  /*      Joyin.ajaxJson(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo/doRiskcal', param, function (data) {
            if(data.data.data) {
                $("#riskTrialSpan").html("&nbsp;&nbsp;" + data.data.data.riskTrialSpan);
                riskResultResponse = data.data.data;
                entity_RiskTrialRetList = data.data.data.riskcalReturnList;
                tableRisk.clear();
                tableRisk.addRow(data.data.data.riskcalReturnList);
            }
        });*/
    });
    if(entity_riskTrialSpan) {
        $("#riskTrialSpan").html("&nbsp;&nbsp;" + entity_riskTrialSpan);
    }
    //cfets/etrib/cfetsEtrIbRepoinfo/getTermType
    $("#repoTerm").blur(function () {
        var repoTerm = $("#repoTerm").val();
        if (!repoTerm) return;
        if(Number(repoTerm) < 1 || Number(repoTerm) > 365) {
            Joyin.error("回购期限只能在1~365之间");
            $("#repoTerm").val("");
        }
        Joyin.ajax(basePath + 'cfets/etrib/cfetsEtrIbRepoinfo/getTermTypeByRepoTerm?repoTerm=' + repoTerm, {}, function (data) {
            if(data.data) {
                $("#termType").textSelect('setValue', data.data.data);
            }
        });
        setRiskFlag(1);
    })
    $("#price").change(function () {
        var price = $("#price").val();
        if(price && Number(price) <= 0) {
            Joyin.error("回购利率需大于0");
            $("#price").val("");
        }
        setRiskFlag(1);
    })

}());