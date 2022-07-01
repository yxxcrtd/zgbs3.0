/**
 * 业务基准共通JS
 * @auth zhengyan
 */
(function () {

    var getElementById = function (id) {
        return $('#' + benchMarkInfoEntityName + '_' + id);
    };
    //判断页面是否加载完成
    var isLoaded = function () {
        var select2 = getElementById('resetType').data('select2');
        return !!select2;
    };

    var profitTypeChange = function () {
        var selectedValueProfitType = $('#' + profitType).val();
        if (selectedValueProfitType) {
            // 非预期收益型
            if (selectedValueProfitType != PROFIT_TYPE_02) {
                getElementById('div').show();
            } else {
                getElementById('div').hide();
            }
            /*
            if(selectedValueProfitType == PROFIT_TYPE_01){
            	getElementById('div1').show();
            	getElementById('div2').show();
            }
            else{
            	getElementById('div1').hide();
            	getElementById('div2').hide();
            }*/
        } else {
            getElementById('div').hide();
        }
    }
    var publishTypeChange = function () {

        if (getElementById('publishType').val() === PUBLISH_TYPE_01) {
            getElementById('rateType').val('').trigger('change');
            $("#rateTypeDiv").css('display', 'none');
            getElementById('rate').val('').trigger('change').prop('disabled', true);
            getElementById('indexId').val('').trigger('change').prop('disabled', true);
            getElementById('coefficient').val('').trigger('change').prop('disabled', true);
            getElementById('spreadRate').val('').trigger('change').prop('disabled', true);
            getElementById('resetType').val('').trigger('change').prop('disabled', true);
            getElementById('rateLower').trigger('change').prop('disabled', false);
            getElementById('rateLimit').trigger('change').prop('disabled', false);
        } else if (getElementById('publishType').val() === PUBLISH_TYPE_02) {
            getElementById('rateType').trigger('change').prop('disabled', false);
            $("#rateTypeDiv").css('display', 'block');
            // rateTypeChange();
            getElementById('rateLower').val('').trigger('change').prop('disabled', true);
            getElementById('rateLimit').val('').trigger('change').prop('disabled', true);
        } else {
            getElementById('rateType').val('').trigger('change').prop('disabled', false);
            $("#rateTypeDiv").css('display', 'block');
            getElementById('rate').val('').trigger('change').prop('disabled', false);
            getElementById('indexId').val('').trigger('change').prop('disabled', false);
            getElementById('coefficient').val('').trigger('change').prop('disabled', false);
            getElementById('spreadRate').val('').trigger('change').prop('disabled', false);
            getElementById('resetType').val('').trigger('change').prop('disabled', false);
            getElementById('rateLower').val('').trigger('change').prop('disabled', true);
            getElementById('rateLimit').val('').trigger('change').prop('disabled', true);
        }
    }

    var rateTypeChange = function () {
        if (getElementById('publishType').val() === PUBLISH_TYPE_01) return;

        if (getElementById('rateType').val() === '') {
            getElementById('rate').val('').trigger('change').prop('disabled', false);
            getElementById('indexId').val('').trigger('change').prop('disabled', false);
            getElementById('coefficient').val('').trigger('change').prop('disabled', false);
            getElementById('spreadRate').val('').trigger('change').prop('disabled', false);
            getElementById('resetType').val('').trigger('change').prop('disabled', false);
        } else if (getElementById('rateType').val() === INT_TYPE_01) {
            getElementById('rate').trigger('change').prop('disabled', false);
            getElementById('indexId').val('').trigger('change').prop('disabled', true);
            getElementById('coefficient').val('').trigger('change').prop('disabled', true);
            getElementById('spreadRate').val('').trigger('change').prop('disabled', true);
            getElementById('resetType').val('').trigger('change').prop('disabled', true);
        } else {
            getElementById('rate').val('').trigger('change').prop('disabled', true);
            getElementById('indexId').trigger('change').prop('disabled', false);
            if (!getElementById('coefficient').val()) {
                getElementById('coefficient').val('1.00').trigger('change').prop('disabled', false);
            } else {
                getElementById('coefficient').trigger('change').prop('disabled', false);
            }
            getElementById('spreadRate').trigger('change').prop('disabled', false);
            getElementById('resetType').trigger('change').prop('disabled', false);
        }
    }

    var benchMarkInitAction = function (input_finprodId) {
        Joyin.ajax(JoyinUtil.getRealPath('component/prod/BussBenchMarkInfo/queryBenchMarkInfoByFinprodIdAndBaseRuleType'), {
            finprodId: input_finprodId,
            baseRuleType: BASE_RULE_TYPE_01
        }, function (data) {
            FinProductCommon.initPageDataAction(benchMarkInfoEntityName, data);
        })
    }
    var benchMarkInitActionForCycle = function (input_finprodId, input_dealMode) {
        Joyin.ajax(JoyinUtil.getRealPath('component/prod/BussBenchMarkInfo/queryBenchMarkInfoByFinprodIdAndBaseRuleType'), {
            finprodId: input_finprodId,
            baseRuleType: BASE_RULE_TYPE_01,
            dealMode: input_dealMode
        }, function (data) {
            FinProductCommon.initPageDataAction(benchMarkInfoEntityName, data);
        })
    }

    window.BussBenchMarkInfo = {
        benchMarkInitAction: benchMarkInitAction,
        benchMarkInitActionForCycle: benchMarkInitActionForCycle
    }
    /**
     * 产品处理模式联动事件
     */
    var dealModeChange = function () {
        //收益类型为净值型 且产品处理模式为分档 改界面不显示  其他场景显示 -- 针对if判断条件增加周期型产品不展示 add by wm 2019-10-08
        if ($('#' + profitType).val() === PROFIT_TYPE_01 && $('#' + dealModeElement).val() === DISPOSE_PATTERN_11) {
            getElementById('rateType').val('').trigger('change');
            getElementById('resetType').val('').trigger('change');
            getElementById('div').hide();
        } else {
            getElementById('div').show();
        }
        getElementById('publishType').val('02').trigger('change');
        getElementById('rateType').val('').trigger('change');
    }
    /**
     * 期限联动
     */
    var periodChange = function () {
        //多固定期限不展示
        if ($('#' + dealModeElement).val() === DISPOSE_PATTERN_10) {
            if ($('#' + profitType).val() === PROFIT_TYPE_01 && $('#' + periodElement).val() === TERM_TYPE_02) {
                getElementById('rateType').val('').trigger('change');
                getElementById('resetType').val('').trigger('change');
                getElementById('div').hide();
            } else {
                getElementById('div').show();
            }
            getElementById('publishType').val('02').trigger('change');
            getElementById('rateType').val('').trigger('change');
        }
    }

    $(function () {
        getElementById('publishType').change(function () {
            if (isLoaded()) {
                publishTypeChange();
            }
        });
        // 利率类型改变时触发的事件
        getElementById('rateType').change(function () {
            if (isLoaded()) {
                rateTypeChange();
            }
        });
        profitTypeChange();
        // 父画面收益类型联动事件
        $('#' + profitType).change(profitTypeChange);
        //产品处理模式联动事件
        dealModeChange();
        setTimeout(function () {
            publishTypeChange();
            // rateTypeChange();
        }, 3000);
        $('#' + dealModeElement).change(dealModeChange);
        //期限联动事件
        periodChange();
        $('#' + periodElement).change(function () {
            if (pageModel != 2) {
                periodChange();
            }
        });
    });
}());