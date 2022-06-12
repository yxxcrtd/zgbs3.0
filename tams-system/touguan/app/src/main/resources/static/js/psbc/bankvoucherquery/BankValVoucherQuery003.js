$(function () {
    $('#BankValVoucherQuery003_btn_submit').click(function() {
        var object = $.serializeToObject('#submitForm');
        object.pageModel = pageModel;
        Joyin.ajaxJson('updateVouch', object, function(result){
            if(result.errorList.length==0){
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            }
        });
    });
    
});