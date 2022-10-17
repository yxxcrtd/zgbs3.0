    var layerIndex;
    //生成画面用
    var globalPanelPattern;
    var hasDataDivs;
    var tableId = "SystemSqlQuery001_table";
    // 默认日期格式
    var defaultDateStyle = 'yyyy-MM-dd';
    var update=function(){
        var paramObj = {
                sqlText:$('#inputSql').val()
         };
        Joyin.ajax('search',paramObj,function(result) {
            var cols=result[1];
            if(result.length>0){
                var cols=result[1];
                if(cols==''||cols==null){
                    layer.alert('无效的执行语句!');
                    return;
                }else{
                    for(var i in cols){
                        layer.alert(cols[i]);
                    } 
                }
            }
        });
    };
    var search = function(buttonType) {
        var paramObj = {
               sqlText:$('#inputSql').val()
        };
        Joyin.ajax('checkSearch',paramObj,function(result) {
            if(result && result.statuCode == '200') {
                Joyin.ajax('search',paramObj,function(result) {
                    //生成报表面板信息
                    createReportPanelDiv(result);
                    
                });
            }
        });
    };
//生成报表面板信息
var createReportPanelDiv= function (result) {
//    var cols=new Array();
//    var col_top=result[0];
//    for(var i=1;i<result.length;i++){
//        var obj={};
//        var col_body=result[i];
//        for(var j in col_top){
//            obj.field=col_top[j];
//            obj.title=col_top[j];
//            for(var x in col_body){
//                if(x=j){
//                    obj.text=col_body[x]; 
//                }
//            }
//            //var obj={field:''+col_top[j]+'', title: ''+col_top[j]+''};
//        }
//        cols.push(obj);
//    }
    /**
     * 定义数据表格
     */
//    var table=$("#SystemSqlQuery001_table").joyintable({
//        checkbox: false,
//        cellMinWidth : 100,
//        cols: cols
//    });

    hasDataDivs = result.length;
    var html='';
    var clos= result[0];
    html+='<div class="layui-form layui-border-box layui-table-view">';
      html+='<table class="layui-table">';
          html+='<thead>';
              html+='<tr class="layui-bg-green">';
                   for(var i in clos){
                      html+='<td data-field='+clos[i]+' title='+clos[i]+'>'
                          html+='<div class="layui-table-cell">'+clos[i]+'</div>';
                      html+='</td>';
                  }
              html+='</tr>';
          html+='</thead>';
          html+='<tbody>';
              for(var i=1;i<result.length;i++){
                  html+='<tr>';
                      clos=result[i];
                      for(var j in clos){
                          html+='<td title='+clos[j]+'>';
                              html+='<div class="layui-table-cell">'+clos[j]+'</div>';
                          html+='</td>';
                      }
                  html+='</tr>';
              }
          html+='</tbody>';              
      html+='</table>';
    html+='</div>';       
    
    $('#SystemSqlQuery001_table').append(html);
};
/**
 * 画面加载完成
 */
$(function() {
    // 绑定按钮事件
    $('#SystemSqlQuery001_btn_submit').click(function() {
        var sqlText=$('#inputSql').val();
        if (sqlText.trim().toUpperCase().indexOf("SELECT") == 0 || sqlText.trim().toUpperCase().indexOf("WITH") == 0) {
            $('#div1').show();
            $('#SystemSqlQuery001_table').html('');
            search();
        }else{
            $('#div1').hide();
            update();
        }

    });
});
