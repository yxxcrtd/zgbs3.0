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

    $("#download").click(function(){

        var data = {seqNo : $('#seqNo').text()}
        Joyin.exportExcel('/valuation/system/log/ValBatchLog001/downloadFile', data);

    });

    //详情日志若无，隐藏列表
    var isShow=$("#exeSeqno").text();
    if($.isEmptyObject(isShow)){
        $("#detailLog").css('display','none');
    }


    var table = $("#dataTable").joyintable({
        ajax: {
            url :  JoyinUtil.getRealPath("/valuation/system/log/ValBatchLog001/findDetail"),
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