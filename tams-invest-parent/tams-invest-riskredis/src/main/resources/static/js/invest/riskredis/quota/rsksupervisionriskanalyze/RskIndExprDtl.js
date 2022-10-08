/**
 * 监控值指标表达式明细
 * 
 * RskIndExprDtl
 * @author zhangrui1
 */
(function() {
    var count = 0;
    // 指标表达式DIV
    var divPzInfo = $('#dt-pz-info-add');

    /**
     * 定义数据表格
     */
    var table = $("#dataTable").joyintable({
        data: entity.paramList,
        checkbox: false,
        cols:[
             {field:'para',visible :false}
            ,{field:'paraStr', title: 'RskIndExprDtl.paraStr',width:'30%', sortable: false}
            ,{field:'paraVal', title: 'RskIndExprDtl.paraVal',width:'50%', sortable: false}
        ]
    });

    // 初始化
    function init(){
        if (entity && entity.paramList && entity.paramList.length > 0) {
            initExpr(entity.paramList);
            table.load(entity.paramList);
        }
    }

    function initExpr(list) {
        for(var i = 0; i<list.length; i++){
            if(list[i]){
                count++;
                divPzInfo.append(createExprDiv(list[i]));
                $(".manager_fh").attr('data-result', SUP_CONNECTOR).textSelect();
                $(".manager_rr").attr('data-result', INDICATOR_PARAM).textSelect();
            }
        }
    }

    // 构造显示表达式的控件
    function createExprDiv(obj){
        var divStr='';
        divStr+='<div class="layui-col-md6" style="margin-bottom:10px;overflow:hidden;height:36px;" id="dt-pz-info-' + count + '">';
        divStr+='<div class="input-inline select-ind-fh manager_fh_fh">';
        divStr+='<select type="text" id="manager_fh" name="manager_fh" formatoption="false" class="input-select manager_fh" placeholder="请输入内容" allowClear="true" initValue="' + obj.oper + '" disabled></select>'; 
        divStr+='</div>';
        divStr+='<div class="input-inline select-ind-rr">';
        divStr+='<select type="text" id="manager_rr" name="manager_rr" formatoption="false" class="input-select manager_rr" placeholder="请输入内容" allowClear="true" initValue="' + obj.para + '" disabled></select>'; 
        divStr+='</div>';
        divStr+='</div>';
        return divStr;
    }

    /**
     * 画面加载完成
     */
    $(function() {
        init();
    });
}());