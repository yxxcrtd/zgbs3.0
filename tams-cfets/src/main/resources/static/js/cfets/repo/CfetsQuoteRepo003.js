(function () {
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
    function query(ele) {
        return $("#CfetsQuoteRepo003_bodydiv " + ele);
    }

    /**
     * 保存
     */
    var saveOrSubmitAction = function (operateType) {
        var object = $.serializeToObject('#submitForm', true);
        object.operateType = operateType;
        if ($("#quotaPageModel").val() === 'new' || $("#quotaPageModel").val() === 'edit') {
            object.operatorType = operateType;
        }
        object.isDzh = 'Y';
        object.pageId = "CfetsQuoteRepo003";
        object.ptlAllocationList = table.getAllRows();//组合分配列表
        object.pledgeBondList = pledgeTable.getAllRows();//质押券信息
        //保存 走流程
        if (operateType === 'save') {
            WorkFlow.doSaveList(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo003/save', object, "CfetsQuoteRepo003_btn_save", null,
                function () {
                    var index = window.parent.layer.getFrameIndex(window.name);
                    window.parent.layer.close(index);
                })
        }
        //提交 走流程
        else if (operateType === 'submit') {
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo003/checkByHome', object, function(result) {
                WorkFlow.doSubmitList(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo003/save', object, "CfetsQuoteRepo003_btn_submit", null,
                    function () {
                        var index = window.parent.layer.getFrameIndex(window.name);
                        window.parent.layer.close(index);
                    })
            });
        }
    };
    /**
     * 组合分配
     */
    var colsCombDistribt = [
        {
            /* 指令代码 */
            field: 'entrustId', width: 200, title: '指令代码', sortable: false, disabled: true
        }
        , {/*组合名称*/
            field: 'portfolioName',
            width: 120,
            title: '组合名称',
            sortable: false
            , disabled: true
        }
        , {/*组合代码*/
            field: 'portfolioId',
            width: 120,
            title: '组合名称',
            sortable: false
            , disabled: true,
            visible: false
        }
        , {
            field: 'avaNum',
            width: 150,
            title: '剩余指令金额(元)',
            sortable: false,
            type: 'money',
            digit: 2
            , disabled: true
        }
        , {
            field: 'fpriceAmt',
            width: 120,
            title: '交易金额(元)',
            sortable: false,
            type: 'money',
            edit: true,
            digit: 2
        }
        , {
            field: 'shareAmt',
            width: 120,
            title: '质押券总额(元)',
            sortable: false,
            type: 'money',
            edit: true,
            digit: 2,
            visible: false
        }
        , {
            field: 'convertAmt',
            width: 120,
            title: '质押券折算金额(元)',
            sortable: false,
            type: 'money',
            edit: true,
            digit: 2,
            visible: false
        }
        , {
            field: 'dealFeeAmt',
            width: 120,
            title: '交易手续费(元)',
            sortable: false,
            type: 'money',
            edit: true,
            digit: 2
        }
        , {
            field: 'settFeeAmt',
            width: 100,
            title: '结算服务费(元)',
            sortable: false,
            type: 'money',
            edit: true,
            digit: 2
        }
        , {
            field: 'intAmt',
            width: 100,
            title: '回购利息(元)',
            sortable: false,
            type: 'money',
            digit: 2
            , disabled: true
        }
        , {
            field: 'mtradeAmt',
            width: 100,
            title: '到期结算金额(元)',
            sortable: false,
            type: 'money',
            digit: 2
            , disabled: true
        }
        , {
            field: 'priceModelStr',
            width: 100,
            title: '价格模式',
            sortable: false
        }
        , {
            field: 'priceModel',
            width: 100,
            title: '价格模式',
            sortable: false,
            visible: false
        }
        , {
            field: 'price',
            width: 100,
            title: '指令回购利率(%)',
            sortable: false,
            type: 'number',
            digit: 4
            , disabled: true
        }
    ]
    /**
     * 质押券信息
     */
    var pledgeTableCols = [
        {
            /* 债券代码 */
            field: 'bondCode', width: 100, title: '债券代码', sortable: false, disabled: true
        },
        {
            /* 债券代码 */
            field: 'finprodId', width: 100, title: '债券代码', sortable: false, visible: false
        }
        , {/*组合名称*/
            field: 'bondAbbrName',
            width: 150,
            title: '债券简称',
            sortable: false
            , disabled: true
        }
        , {
            field: 'portfolioId',
            width: 120,
            title: '组合代码',
            sortable: false
            , disabled: true
            , visible: ps === '03'
        }
        , {
            field: 'portfolioName',
            width: 120,
            title: '组合名称',
            sortable: false
            , disabled: true
            , visible: ps === '03'
        }
        , {
            field: 'avaPledgePrinStr',
            width: 150,
            title: '可质押券面总额(万元)',
            sortable: false,
            type: 'number',
            digit: 2
            , disabled: true
            , visible: ps === '03'
        }
        , {
            field: 'avaPledgePrin',
            width: 100,
            title: '可质押券面总额(元)',
            sortable: false,
            visible: false
        }
        , {
            field: 'pledgePrinStr',
            width: 150,
            title: '质押券面总额(万元)',
            sortable: false,
            type: 'number',
            edit: true,
            digit: 2
        }
        , {
            field: 'pledgePrin',
            width: 100,
            title: '质押券面总额(元)',
            sortable: false,
            visible: false
        }
        , {
            field: 'firstNetPrice',
            width: 100,
            title: '估价净价(元)',
            sortable: false,
            type: 'money',
            digit: 4
            , disabled: true
        }
        , {
            field: 'pledgeRate',
            width: 100,
            title: '折算比例(%)',
            sortable: false,
            type: 'number',
            edit: true,
            digit: 2
        }
        , {
            field: 'repRegistOrgStr',
            width: 200,
            title: '登记托管机构',
            sortable: false
            , disabled: true
        }
        , {
            field: 'repRegistOrg',
            width: 200,
            title: '登记托管机构',
            sortable: false,
            visible: false
        }
        , {
            field: 'mdate',
            sortable: false,
            visible: false
        }
        , {
            field: 'mdateStr',
            sortable: false,
            visible: false
        }
        , {
            field: 'prin',
            sortable: false,
            visible: false
        }
        , {
            field: 'convertAmt',
            title: '质押券折算总额',
            sortable: false,
            visible: false
        }
    ]
    var table = query("#bussCombDistribtTable").joyintable({
        data: ptlAllocations == null ? [] : ptlAllocations,
        cols: colsCombDistribt,
        searchOnInit:true,
        checkbox : false,
        height : 140,
        page:false,
        editCell: function (value, data, field, cell, column) {
            if (field === "fpriceAmt" ) {
                var dealAmt = 0;
                for (var i = 0; i < table.getAllRows.length; i++) {
                    dealAmt = JoyinMath.doAdd(table.getAllRows()[i].fpriceAmt, dealAmt);
                }
                $("#dealAmt").val(dealAmt).change();//交易金额
            }
            return true;
        }
    });
    var pledgeTable = query("#pledgeTable").joyintable({
        data: repoEntrustPledgeDtos == null ? [] : repoEntrustPledgeDtos,
        cols: pledgeTableCols,
        searchOnInit:true,
        checkbox : false,
        height : 140,
        page:false,
        editCell: function (value, data, field, cell, column) {
            if (field === "pledgePrinStr") {
                var pledgePrin;
                var convertAmt;
                data.pledgePrin = JoyinMath.doMul(data.pledgePrinStr, 10000, 2);
                pledgeTable.renderRow(data.LAY_TABLE_INDEX, data);
                for (var i = 0; i < pledgeTable.getAllRows.length; i++) {
                    pledgePrin = JoyinMath.doAdd(pledgeTable.getAllRows()[i].pledgePrin, pledgePrin);
                    convertAmt = JoyinMath.doAdd(pledgeTable.getAllRows()[i].convertAmt, convertAmt);
                }
                var shareAmtStr = JoyinMath.doDiv(pledgePrin,10000,2);
                $("#shareCtAmt").val(convertAmt).change();//质押券折算总额
                $("#shareAmtStr").val(shareAmtStr).change();//券面总额合计(万元)
                $("#shareAmt").val(pledgePrin).change();//券面总额合计
                ptlAndpledgeEdit();
            }
            if (field === "pledgeRate") {
                var pledgePrin;
                var convertAmt;
                data.convertAmt = JoyinMath.doMul(JoyinMath.doDiv(data.pledgeRate, 100, 4), data.pledgePrin, 2);
                pledgeTable.renderRow(data.LAY_TABLE_INDEX, data);
                for (var i = 0; i < pledgeTable.getAllRows.length; i++) {
                    convertAmt = JoyinMath.doAdd(pledgeTable.getAllRows()[i].convertAmt, convertAmt);
                }
                $("#shareCtAmt").val(convertAmt).change();//质押券折算总额
                ptlAndpledgeEdit();
            }
            return true;
        }
    });
    /**
     * 页面初始化
     */
    var init = function () {
        if (isEdit === 'N') {
            $("#counterId").attr('disabled', 'disabled');
        }
        initChangeEvent();
        initClickEvent();
        star();
    }

    /**
     * 初始化change事件
     */
    var initChangeEvent = function () {
        $("#repoTerm").change(function () {
            calAmtInfo();
        });
        $("#IbuQuoteRepo004_settleSpreed").change(function () {
            calAmtInfo();
        });
        $("#dealAmt").change(function () {
            calFee();
            calAmtInfo();
        });
        $("#price").change(function () {
            calAmtInfo();
        });
        $("#dealFeeAmt").change(function () {
            autoAllocation();
        });
        $("#settFeeAmt").change(function () {
            autoAllocation();
        });
        $("#btn_autoAllocation").click(function () {
            autoAllocation();
        })
        if(ps == PS_03) {//正回购禁止修改交易金额
            $("#dealAmt").attr('disabled', 'disabled');
        }
    }
    /**
     * 初始化click事件
     */
    var initClickEvent = function () {
        // 报价方式勾选标识
        layui.form.on('radio(quoteStyleFilter)', function (data) {
            if (data) {
                if (data.value === '05') {
                    $("#counterIdCCP_div").show();
                    $("#counterId_div").hide();
                } else {
                    $("#counterIdCCP_div").hide();
                    $("#counterId_div").show();
                }
            }
        });
        // $("#bussCombDistribt_btn_add").click(addPtlAllocations);
    }

    /**
     * 数据不可编辑
     * @deprecated 被退回后可修改 不用该方法
     */
    hideRepoPage = function () {
        if (workflowId) {
            $('#submitForm').find('input,text,textarea,select').attr('disabled', 'disabled');
        }

    }

    /**
     * 计算金额元素
     * 需要 回购期限 清算速度 本金金额 回购利率
     */
    var calAmtInfo = function () {
        var request = getParam();
        if (request.repoTerm != null && request.repoTerm !== ''
            && request.settleSpreedCode != null && request.settleSpreedCode !== ''
            && request.tradeAmt != null && request.tradeAmt !== ''
            && request.price != null && request.price !== '') {
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo/calAmtInfo', request, function (result) {
                if (result) {
                    var data = result.data;
                    $("#settlementStartDate").val(data.settlementStartDate).change();//首次结算日
                    $("#mSettleDate").val(data.settlementEndDate).change();//到期结算日
                    $("#actCapDays").val(data.actCapDays).change();//实际占款天数
                    $("#mIntAmt").val(data.mIntAmt).change();//回购利息
                    $("#mTradeAmt").val(data.mtradeAmt).change();//到期结算金额
                    $("#termType").val(data.termType).change();//期限品种
                    autoAllocation();
                }
            });
        }
    }
    /**
     *
     * 质押式回购计算费用 根据[交易方向][交易金额][证券账户][期限品种]计算 [交易手续费][结算手续费]
     */
    var calFee = function () {
        var request = getParam();
        if (request.ps != null && request.ps !== ''
            && request.tradeAmt != null && request.tradeAmt !== ''
            && request.termType != null && request.termType !== '') {
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo/calFee', request, function (result) {
                if (result) {
                    var data = result.data;
                    $("#settFeeAmt").val(data.settFeeAmt).change();//结算服务费
                    $("#dealFeeAmt").val(data.dealFeeAmt).change();//交易手续费
                }
            });
        }
    }
    /**
     * 参数设置
     */
    var getParam = function () {
        var request = {};
        request.distribution = $("#distribution").val();//分配方式
        request.repoTerm = $("#term").val();//回购期限
        request.settleSpreedCode = $("#CfetsQuoteRepo003_settleSpreed").val();//清算速度
        request.tradeAmt = $("#dealAmt").val();//本金金额
        request.price = $("#price").val();//回购利率
        request.distribution = $("#distribution").val();//分配方式
        request.ps = $("#ps").val();//交易方向
        request.shareAmt = $("#shareAmt").val();//质押券总额
        request.convertAmt = $("#shareCtAmt").val();//质押券折算总额
        request.dealFeeAmt = $("#dealFeeAmt").val();//交易手续费
        request.settFeeAmt = $("#settFeeAmt").val();//结算服务费
        request.mIntAmt = $("#mIntAmt").val();//回购利息
        request.mtradeAmt = $("#mTradeAmt").val();//到期结算金额
        request.secManageAcctId = $("#secManageAcctId").val();//证券账户
        request.termType = $("#termType").val();//期限品种
        request.settlementEndDate = $("#mSettleDate").val();//到期结算日
        request.quotaPageModel = $("#quotaPageModel").val();//页面模式
        var ptlAllocationsData = [];
        var ptlAllocations = table.getAllRows() ;
        for (i = 0; i < ptlAllocations.length; i++) {
            var temp1 = ptlAllocations[i];
            temp1.tradeAmt = temp1.fpriceAmt;
            temp1.mIntAmt = temp1.intAmt;
            ptlAllocationsData.push(temp1);
        }
        var repoEntrustPledgeData = [];
        var repoEntrustPledges = pledgeTable.getAllRows();
        for (i = 0; i < repoEntrustPledges.length; i++) {
            var temp2 = repoEntrustPledges[i];
            temp2.pledgeRatio = temp2.pledgeRate;
            repoEntrustPledgeData.push(temp2);
        }
        request.ptlAllocations = ptlAllocationsData;//组合分配列表
        request.repoEntrustPledgeDtos = repoEntrustPledgeData;//质押券信息
        var entrustIdList = new Array();
        for (var i = 0; i < request.ptlAllocations.length; i++) {
            entrustIdList[i] = request.ptlAllocations[i].entrustId;
        }
        request.entrustIdList = entrustIdList;//指令代码List
        return request;
    }
    /**
     * 自动分配
     * 自动分配按钮触发功能,将基本信息中费用,金额按要求分配到组合分配中
     */
    var autoAllocation = function(){
        var request = getParam();
        if(!request.distribution && request.ps == PS_04) {
            Joyin.error("请先选择分配方式");
            return;
        }
        Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo/autoAllocation', request, function (result) {
            if (result) {
                var resultData = [];
                for (i = 0; i < result.data.ptlAllocations.length; i++) {
                    var temp = result.data.ptlAllocations[i];
                    temp.fpriceAmt = temp.tradeAmt;
                    temp.intAmt = temp.mIntAmt;
                    resultData.push(temp);
                }
                table.clear(function () {
                    table.addRow(resultData)
                });
            }
        });
    }
    /**
     * 组合分配、质押券联动
     * 添加、删除 组合分配、质押券信息后联动计算
     */
    var ptlAndpledgeEdit = function(){
        var request = getParam();
        Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo/ptlAndpledgeEdit', request, function (result) {
            if (result) {
                var data = result.data;
                var ptlAllocationsData = [];
                for (i = 0; i < data.ptlAllocations.length; i++) {
                    var temp1 = data.ptlAllocations[i];
                    temp1.fpriceAmt = temp1.tradeAmt;
                    temp1.intAmt = temp1.mIntAmt;
                    ptlAllocationsData.push(temp1);
                }
                table.clear(function () {
                    table.addRow(ptlAllocationsData)
                });
                var repoEntrustPledgeData = [];
                for (i = 0; i < data.repoEntrustPledgeDtos.length; i++) {
                    var temp2 = data.repoEntrustPledgeDtos[i];
                    temp2.pledgeRate = temp2.pledgeRatio;
                    repoEntrustPledgeData.push(temp2);
                }
                pledgeTable.clear(function () {
                    pledgeTable.addRow(repoEntrustPledgeData)
                });
                $("#actCapDays").val(data.actCapDays);//实际占款天数
                $("#shareCtAmt").val(data.convertAmt);//质押券折算总额
                $("#dealFeeAmt").val(data.dealFeeAmt);//交易手续费
                $("#mIntAmt").val(data.mIntAmt);//回购利息
                $("#mTradeAmt").val(data.mtradeAmt);//到期结算金额
                $("#settFeeAmt").val(data.settFeeAmt);//结算服务费
                $("#shareAmt").val(data.shareAmt);//质押券面
                $("#tradeAmt").val(data.tradeAmt);//交易金额
            }
        });
    }

    $(function () {
        // hideRepoPage();
        init();
        /**
         * 保存按钮按下处理
         */
        $('#CfetsQuoteRepo003_btn_save').click(function () {
            saveOrSubmitAction('save');
        });

        /**
         * 提交按钮按下处理
         */
        $('#CfetsQuoteRepo003_btn_submit').click(function () {
            saveOrSubmitAction('submit');
        });

    });

}());