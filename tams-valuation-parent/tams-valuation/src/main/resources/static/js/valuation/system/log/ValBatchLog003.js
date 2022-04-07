(function () {
    $(function () {
        $("#logInfo").html($("#logInfoHidden").text());
    })

    $(function () {
        $("#resultInfo").html($("#resultInfoHidden").text());
    })

    $(function () {
        $("#detailEx").html($("#detailExHidden").text());
    })


    var table = $("#dataTable").joyintable({
        ajax: {
            url :  JoyinUtil.getRealPath("/valuation/system/log/ValBatchLog001/findDetai"),
            type : 'POST',
            data : function() {
                return {
                    seqNo : $('#seqNo').text(),
                };
            }
        },
        cols: [
              {field: 'remark', title: '属性名称',width:"50%"}
            , {field: 'value',  title: '属性值',width:"50%"}
        ],
        checkbox: false
    });


    table.load();//加载表格

}());