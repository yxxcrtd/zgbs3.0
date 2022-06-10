(function () {
    var getElementById = function (id) {
        return $("#" + entityName + "_" + id);
    };

    /**
     * 定义可质押券列表（Detail）
     */
    //var avaPledgeBondDetailTBL = getElementById("avaPledgeBondDetailList").joyintable({
	var avaPledgeBondDetailList=$("#avaPledgeBondDetailList").joyintable({
        /*ajax: {
            url: 'selectAvaPledgeBondDetailList',
            type: 'POST',
            data: function () {
                var param = {};

                param.portfolioId = getElementById("portfolioId").val();
                param.ps = getElementById("ps").val();
                param.bondType = getElementById("bondType").val() ? getElementById("bondType").val().join(',') : '';
                var bondCodeSelObject = getElementById("bondCode").select2('data')[0].object;
                param.bondCode = bondCodeSelObject == null ? '' : bondCodeSelObject.finprodMarketId;
                param.repRegistOrg = getElementById("repRegistOrg").val() ? getElementById("repRegistOrg").val().join(',') : '';

                // 获取请求参数
                return param;
            }
        },*/
        searchOnInit: true,
        page: false,
        /*loadonce: true,*/
        cols: [
            // 债券代码
            {
                field: 'bondCode',
                width: 90,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.bondCode',
                sortable: false
            }
            // 债券简称
            , {
                field: 'bondAbbrName',
                width: 90,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.bondAbbrName',
                sortable: false
            }
            // 券面总额
            , {
                field: 'prin',
                width: 90,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.prin',
                sortable: false,
                type: 'money',
                digit: 2,
            }
            // 已质押券面总额
            , {
                field: 'pledgePrin',
                width: 100,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.pledgePrin',
                sortable: false,
                type: 'money',
                digit: 2,
            }
            // 可质押券面总额
            , {
                field: 'avaPledgePrin',
                width: 100,
                title: 'BussRepoSec.avaPledgeBondDetailTBL.colmodel.avaPledgePrin',
                sortable: false, type: 'money',
                digit: 2,
            }
        ],
        data:[
				{
				    "bondCode":"20190200",
				    "bondAbbrName":"日薪月益2018001",
				    "prin":"10000",
				    "pledgePrin":"5000",
				    "avaPledgePrin":"5000"
				},
				{
				    "bondCode":"20190201",
				    "bondAbbrName":"日薪月益2018002",
				    "prin":"20000",
				    "pledgePrin":"500",
				    "avaPledgePrin":"15000"
				},
				{
				    "bondCode":"20190202",
				    "bondAbbrName":"日薪月益2018003",
				    "prin":"30000",
				    "pledgePrin":"5000",
				    "avaPledgePrin":"25000"
				}
              ]
    });

    var tempPledgePrin;

    /**
     * 定义质押券列表（Detail）
     */
    //var pledgeBondDetailTbl = getElementById("pledgeBondDetailList").joyintable({
    var pledgeBondDetailTbl=$("#pledgeBondDetailTbl").joyintable({
        data: (entity && entity.pledgeBondList) ? entity.pledgeBondList : [],
        page: false,
        loadonce: false,
        cols: [
            // 债券代码
            {field: 'bondCode', width: 70, title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.bondCode', sortable: false}
            // 债券简称
            , {
                field: 'bondAbbrName',
                width: 70,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.bondAbbrName',
                sortable: false
            }
            // 可质押券面总额
            , {
                field: 'avaPledgePrin',
                width: 100,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.avaPledgePrin',
                type: 'money',
                digit: 2,
                sortable: false
            }
            // 入库券面总额
            , {
                field: 'pledgePrin',
                width: 100,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrin',
                type: 'money',
                digit: 2,
                edit: true,
                sortable: false
            }
            // 首次净价
            , {
                field: 'firstNetPrice',
                width: 70,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.firstNetPrice',
                sortable: false
            }
            // 质押比例
            , {
                field: 'pledgeRatio',
                width: 80,
                title: 'BussRepoSec.pledgeBondDetailTbl.colmodel.pledgeRatio',
                type: 'number',
                digit: 2,
                edit: true,
                sortable: false
            }
        ],
        beforeEditCell: function (preVal, field, rowData) {
            if (field === "pledgePrin") {
                tempPledgePrin = preVal.replace(/\,/g, '');
            }
            return true;
        },
        editCell: function (value, data, field, cell, column) {
            if (field === "pledgePrin" && getElementById("ps").val() == BACK_LEND_DIRECTION_03 && data.pledgePrin * 1 > data.avaPledgePrin * 1) {
                var datas = pledgeBondDetailTbl.getAllRows();
                datas[data.LAY_TABLE_INDEX].pledgePrin = tempPledgePrin;
                pledgeBondDetailTbl.reload({data: datas});
                Joyin.error(JoyinUtil.getMessage("BUSS_COM_E_0003",
                    [JoyinUtil.getLabel("BussRepoSec.pledgeBondDetailTbl.colmodel.pledgePrin"),
                        JoyinUtil.getLabel("BussRepoSec.pledgeBondDetailTbl.colmodel.avaPledgePrin")]));
            }
        }
    });

    /**
     * 校验行数据中登记托管机构是否不一致
     * @param array
     * @returns {boolean}
     */
    var checkIsDifferent = function (array) {
        var isDifferent = false;
        if (array != null && array.length > 1) {
            for (var index in array) {
                if (index < array.length - 1
                    && array[index].repRegistOrg != array[index * 1 + 1].repRegistOrg) {
                    isDifferent = true;
                    break;
                }
            }
        }
        return isDifferent;
    };

    /**
     * 加入按钮按下
     * @param array
     * @returns {boolean}
     */
    var joyinProcess = function () {
        var avaSelectedRows = avaPledgeBondDetailList.getAllCheckedRows();
        var pledgeAllRows = pledgeBondDetailTbl.getAllRows();
        if (avaSelectedRows != null && avaSelectedRows.length > 0) {
            // 若添加的质押券不只一个，且登记托管机构不全相同，则保存质押券时提示质押券登记托管机构不同，不允许保存；
            // 如果 选择的待质押劵 和质押劵列表中债券托管机构不一致 则 提示债券托管机构不一致，不能添加！
            var isDuplicated = false;
            var mergeArray = new Array();
            mergeArray = _.concat(mergeArray, avaSelectedRows);
            mergeArray = _.concat(mergeArray, pledgeAllRows);
            if (checkIsDifferent(mergeArray)) {
                Joyin.error(JoyinUtil.getMessage("TRD_E_0007"));
                return false;
            }

            // 如果 选择的待质押劵 在质押劵列表中存在  则 提示 是否覆盖  质押劵列表中债券
            var isConfirm = false;
            var confirmMessageArray = new Array();

            // 倒序删除，正序会存在问题
            for (var i = avaSelectedRows.length - 1; i >= 0; i--) {
                for (var j in pledgeAllRows) {
                    if (pledgeAllRows[j].bondCode == avaSelectedRows[i].bondCode) {
                        isConfirm = true;
                        confirmMessageArray.push(avaSelectedRows[i].bondCode);
                        break;
                    }
                }
            }
            if (isConfirm) {
                Joyin.confirm(JoyinUtil.getMessage("TRD_C_0003", confirmMessageArray.join(",")), function () {
                    // 倒序删除，正序会存在问题
                    for (var i = avaSelectedRows.length - 1; i >= 0; i--) {
                        for (var j in pledgeAllRows) {
                            // 覆盖质押券列表中重复记录
                            if (pledgeAllRows[j].bondCode == avaSelectedRows[i].bondCode) {
                                pledgeBondDetailTbl.removeRow(pledgeAllRows[j]);
                            }
                        }
                        avaPledgeBondDetailList.removeRow(avaSelectedRows[i]);
                        avaSelectedRows[i].pledgePrin = avaSelectedRows[i].avaPledgePrin;
                        avaSelectedRows[i].pledgeRatio = 5;
                        pledgeBondDetailTbl.addRow(avaSelectedRows[i]);
                    }
                });
            } else {
                // 倒序删除，正序会存在问题
                for (var i = avaSelectedRows.length - 1; i >= 0; i--) {
                	avaPledgeBondDetailList.removeRow(avaSelectedRows[i]);
                    avaSelectedRows[i].pledgePrin = avaSelectedRows[i].avaPledgePrin;
                    avaSelectedRows[i].pledgeRatio = 5;
//                    	(Number(avaSelectedRows[i].firstNetPrice) / 100 * 100).toFixed(2);
                    pledgeBondDetailTbl.addRow(avaSelectedRows[i]);
                }
            }
        }
    };

    /**
     * 移除按钮按下
     * @param array
     * @returns {boolean}
     */
    var removeProcess = function () {
        var avaRows = avaPledgeBondDetailList.getAllRows();
        var pledgeAllRows = pledgeBondDetailTbl.getAllCheckedRows();
        if (pledgeAllRows != null && pledgeAllRows.length > 0) {
            // 倒序删除，正序会存在问题
            for (var i = pledgeAllRows.length - 1; i >= 0; i--) {
                var isAvaHave = false;
                // 删除可用质押券列表中相同记录
                for (var j in avaRows) {
                    if (avaRows[j].bondCode == pledgeAllRows[i].bondCode) {
                        isAvaHave = true;
                        break;
                    }
                }
                if(!pledgeAllRows[i]["pledgePrin"])
                {
                    pledgeAllRows[i]["pledgePrin"] = undefined;
                }
                if(!pledgeAllRows[i]["pledgeRatio"])
                {
                    pledgeAllRows[i]["pledgeRatio"] = undefined;
                }
                pledgeBondDetailTbl.removeRow(pledgeAllRows[i]);
                if (!isAvaHave) {
                    pledgeAllRows[i].pledgePrin = 0;
                    avaPledgeBondDetailList.addRow(pledgeAllRows[i]);
                }
            }
        }
    };

    /**
     * 保存按钮按下
     *
     * @param array
     * @returns {boolean}
     */
    var addPledgeBond = function () {
        var pledgeAllRows = pledgeBondDetailTbl.getAllRows();
        if (checkIsDifferent(pledgeAllRows)) {
            Joyin.error(JoyinUtil.getMessage("TRD_E_0007"));
            return false;
        }
        var xxt = 'bussRepoSec';
        window.parent.BussRepoSec[xxt].addPledgeBond(pledgeAllRows);
        top.getSelectData = pledgeAllRows;
        var index = window.parent.layer.getFrameIndex(window.name);
        window.parent.layer.close(index);
    };

    /**
     * 画面加载完成
     */
    $(function () {
        // 初始化时，直接加载质押券列表
        pledgeBondDetailTbl.load();
        // 添加检索事件
        getElementById("btn_search").click(function () {
            // 检索可质押债券列表
        	avaPledgeBondDetailList.load();
        });
        // 加入按钮事件
        getElementById("btn_joyin").click(joyinProcess);
        // 移除按钮事件
        getElementById("btn_remove").click(removeProcess);
        // 保存按钮事件
        getElementById("save").click(addPledgeBond);
    });
}());