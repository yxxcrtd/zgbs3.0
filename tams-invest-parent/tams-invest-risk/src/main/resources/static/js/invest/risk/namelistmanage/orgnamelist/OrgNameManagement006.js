(function () {

    function save() {
        if($('#listDetailName').val()==''||typeof $('#listDetailName').val() == 'undefined'){
            var msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("OrgNameManagement003.listDetailName"));
            Joyin.error(msg);
            return;
        }
        if($('#listDetailId').val()==''||typeof $('#listDetailId').val() == 'undefined'){
            var msg = JoyinUtil.getMessage('COMMON_E_0057',JoyinUtil.getLabel("OrgNameManagement003.listDetailId"));
            Joyin.error(msg);
            return;
        }
        /**
         * 获取列表参数
         */
        var object = $.serializeToObject('#submitForm', true);
        if (object){
            top.returnData = object;
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
            return;
        }

    }
    /**
     * 画面加载完成
     */
    $(function () {

        $('#OrgNameManagement006_btn_save').click(save);

    });
}());