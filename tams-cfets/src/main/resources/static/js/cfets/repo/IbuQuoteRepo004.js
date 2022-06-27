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
        return $("#IbuQuoteRepo004_bodydiv " + ele);
    }

    /**
     * 保存
     */
    var saveOrSubmitAction = function (operatorType) {
        var object = $.serializeToObject('#submitForm', true);
        object.operatorType = operatorType;
        object.isDzh = 'Y';
        object.pageId = "IbuQuoteRepo004";
        object.ptlAllocations = table.getAllRows();//组合分配列表
        object.repoEntrustPledgeDtos = pledgeTable.getAllRows();//质押券信息
        //保存 走流程
        if (operatorType === 'save') {
            WorkFlow.doSaveList(basePath + 'cfets/repo/IbuQuoteRepo004/save', object, "IbuQuoteRepo004_btn_save", null,
                function () {
                    var index = window.parent.layer.getFrameIndex(window.name);
                    window.parent.layer.close(index);
                })
        }
        //提交 走流程
        else if (operatorType === 'submit') {
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo004/checkByHome', object, function (result) {
                WorkFlow.doSubmitList(basePath + 'cfets/repo/IbuQuoteRepo004/save', object, "IbuQuoteRepo004_btn_submit", null,
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
            field: 'tradeAmt',
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
            field: 'mIntAmt',
            width: 100,
            title: '回购利息(元)',
            sortable: false,
            type: 'money',
            digit: 2
            , disabled: true
        }
        , {
            field: 'mtradeAmt',
            width: 150,
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
        , {
            field: 'operation',
            title: 'SysCommon.operation',
            type: 'action',
            buttons: ['delt'],
            visible: false
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
            field: 'pledgeRatio',
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
        , {
            field: 'operation',
            title: 'SysCommon.operation',
            type: 'action',
            buttons: ['delt'],
            visible: false
        }
    ]
    /**
     * 组合分配初始化
     * @type {jQuery}
     */
    var table = query("#bussCombDistribtTable").joyintable({
        data: ptlAllocations == null ? [] : ptlAllocations,
        cols: colsCombDistribt,
        searchOnInit: true,
        checkbox: false,
        height: 140,
        page: false,
        editCell: function (value, data, field, cell, column) {
            if (field === "tradeAmt" ) {
                var tradeAmt = 0;
                for (var i = 0; i < table.getAllRows.length; i++) {
                    tradeAmt = JoyinMath.doAdd(table.getAllRows()[i].tradeAmt, tradeAmt);
                }
                $("#tradeAmt").val(tradeAmt).change();//交易金额
            }
            return true;
        }
    });
    /**
     * 列表删除
     */
    table.bind('click', '.delt', function (data, index) {
        if (table.getAllRows.length <= 1) {
            Joyin.error(JoyinUtil.getMessage("组合分配中至少需存在一条指令!"));
        } else {
            table.removeRow(data);
        }
    });
    /**
     * 质押券初始化
     * @type {jQuery}
     */
    var pledgeTable = query("#pledgeTable").joyintable({
        data: repoEntrustPledgeDtos == null ? [] : repoEntrustPledgeDtos,
        cols: pledgeTableCols,
        searchOnInit: true,
        checkbox: false,
        height: 140,
        page: false,
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
                $("#convertAmt").val(convertAmt).change();//质押券折算总额
                $("#shareAmtStr").val(shareAmtStr).change();//券面总额合计(万元)
                $("#shareAmt").val(pledgePrin).change();//券面总额合计
                ptlAndpledgeEdit();
            }
            if (field === "pledgeRatio") {
                var pledgePrin;
                var convertAmt;
                data.convertAmt = JoyinMath.doMul(JoyinMath.doDiv(data.pledgeRatio, 100, 4), data.pledgePrin, 2);
                pledgeTable.renderRow(data.LAY_TABLE_INDEX, data);
                for (var i = 0; i < pledgeTable.getAllRows.length; i++) {
                    convertAmt = JoyinMath.doAdd(pledgeTable.getAllRows()[i].convertAmt, convertAmt);
                }
                $("#convertAmt").val(convertAmt).change();//质押券折算总额
                ptlAndpledgeEdit();
            }
            return true;
        }
    });
    /**
     * 列表删除
     */
    pledgeTable.bind('click', '.delt', function (data, index) {
        if (pledgeTable.getAllRows.length <= 1) {
            Joyin.error(JoyinUtil.getMessage("质押券信息至少需存在一条质押券!"));
        } else {
            pledgeTable.removeRow(data);
        }
    });
    /**
     * 页面初始化
     */
    var init = function () {
        if (EXPORT_SHOW === '01') {
            $("#select_counterTrader").attr('required',true);
            $("#input_counterTrader").attr('required',false);
            $("#mySide").attr('required',true);
            $("#select_counterTrader_div").show();
            $("#input_counterTrader_div").hide();
        } else {
            $("#select_counterTrader").attr('required',false);
            $("#select_counterTrader").attr('required',false);
            $("#mySide").attr('required',false);
            $("#select_counterTrader_div").hide();
            $("#input_counterTrader_div").show();
        }
        if (quoteStyle === '05') {
            $("#counterId").attr('required',false);
            $("#counterIdCCP").attr('required',true);
            $("#counterIdCCP_div").show();
            $("#counterId_div").hide();
        } else {
            $("#counterId").attr('required',true);
            $("#counterIdCCP").attr('required',false);
            $("#counterIdCCP_div").hide();
            $("#counterId_div").show();
        }
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
        $("#tradeAmt").change(function () {
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
                    $("#settlementEndDate").val(data.settlementEndDate).change();//到期结算日
                    $("#actCapDays").val(data.actCapDays).change();//实际占款天数
                    $("#mIntAmt").val(data.mIntAmt).change();//回购利息
                    $("#mtradeAmt").val(data.mtradeAmt).change();//到期结算金额
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
        request.repoTerm = $("#repoTerm").val();//回购期限
        request.settleSpreedCode = $("#IbuQuoteRepo004_settleSpreed").val();//清算速度
        request.tradeAmt = $("#tradeAmt").val();//本金金额
        request.price = $("#price").val();//回购利率
        request.distribution = $("#distribution").val();//分配方式
        request.ps = $("#ps").val();//交易方向
        request.shareAmt = $("#shareAmt").val();//质押券总额
        request.convertAmt = $("#convertAmt").val();//质押券折算总额
        request.dealFeeAmt = $("#dealFeeAmt").val();//交易手续费
        request.settFeeAmt = $("#settFeeAmt").val();//结算服务费
        request.mIntAmt = $("#mIntAmt").val();//回购利息
        request.mtradeAmt = $("#mtradeAmt").val();//到期结算金额
        request.secManageAcctId = $("#secManageAcctId").val();//证券账户
        request.termType = $("#termType").val();//期限品种
        request.settlementEndDate = $("#settlementEndDate").val();//到期结算日
        request.quotaPageModel = $("#quotaPageModel").val();//页面模式
        request.ptlAllocations = table.getAllRows();//组合分配列表
        request.repoEntrustPledgeDtos = pledgeTable.getAllRows();//质押券信息
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
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo/autoAllocation', request, function (result) {
                if (result) {
                    var data = result.data;
                    table.clear(function () {
                        table.addRow(data.ptlAllocations)
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
        if (request.repoTerm != null && request.repoTerm !== ''
            && request.settleSpreedCode != null && request.settleSpreedCode !== ''
            && request.tradeAmt != null && request.tradeAmt !== ''
            && request.price != null && request.price !== '') {
            Joyin.ajaxJson(basePath + 'cfets/bankuplink/repo/IbuQuoteRepo/ptlAndpledgeEdit', request, function (result) {
                if (result) {
                    var data = result.data;
                    table.clear(function () {
                        table.addRow(data.ptlAllocations)
                    });
                    pledgeTable.clear(function () {
                        pledgeTable.addRow(data.repoEntrustPledgeDtos)
                    });
                    $("#actCapDays").val(data.actCapDays);//实际占款天数
                    $("#convertAmt").val(data.convertAmt);//质押券折算总额
                    $("#dealFeeAmt").val(data.dealFeeAmt);//交易手续费
                    $("#mIntAmt").val(data.mIntAmt);//回购利息
                    $("#mtradeAmt").val(data.mtradeAmt);//到期结算金额
                    $("#settFeeAmt").val(data.settFeeAmt);//结算服务费
                    $("#shareAmt").val(data.shareAmt);//质押券面
                    $("#tradeAmt").val(data.tradeAmt);//交易金额
                }
            });
        }
    }

    $(function () {
        init();
        /**
         * 保存按钮按下处理
         */
        $('#IbuQuoteRepo004_btn_save').click(function () {
            saveOrSubmitAction('save');
        });

        /**
         * 提交按钮按下处理
         */
        $('#IbuQuoteRepo004_btn_submit').click(function () {
            saveOrSubmitAction('submit');
        });

    });

}());