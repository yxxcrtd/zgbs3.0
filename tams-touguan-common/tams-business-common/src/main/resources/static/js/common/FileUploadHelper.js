/**
 * 文件上传辅助画面（导入）
 */
var correctData;
(function(){

    // 场内竞价指令导入
    var importEtrExchangeJjCallBack = function(fileName) {
        var currentTab = SHARES_TRADE_PLATFORM_02; // 现在只支持竞价平台指令导入
        Joyin.ajaxJson(basePath + 'invest/entrust/etrexchangetrade/EtrExchangeTrade001/checkImportData', {fileName: fileName,currentTab:currentTab}, function(checkResult) {
            setTimeout( function () {
                if(checkResult.data.errorMsg){
                    Joyin.error(checkResult.data.errorMsg);
                    return;
                }
                Joyin.confirm(checkResult.data.confirmMsg,function() {
                    correctData = checkResult.data.correctData;
                    if(typeof correctData === 'string') {
                        correctData = JSON.parse(correctData);
                        if(correctData != null && correctData.length > 0){
                            var data = [];
                            for(var i in correctData){
                                data.push(JSON.parse(correctData[i]));
                            }
                            correctData = data;
                        }
                    }
                    var label=SHARES_TRADE_PLATFORM_04==currentTab?JoyinUtil.getLabel("EtrExchangeTradeImport.dzImport"):SHARES_TRADE_PLATFORM_03==currentTab?JoyinUtil.getLabel("EtrExchangeTradeImport.gsImport"):JoyinUtil.getLabel("EtrExchangeTradeImport.jjImport");
                    Joyin.dialog(label,
                        JoyinUtil.getRealPath("/etrexchange/common/EtrExchangeTradeCommonImport/init"),
                        "98%", "95%", {currentTab:currentTab},
                        function(){
                        // EtrExchangeTrade003.myrefresh();
                    });
                });
            },1000);
        });
    }

    $(function() {
        // 场内竞价指令导入
        if (importPageId == 'EtrExchangeJjImport') {
            window.uploadCallback = importEtrExchangeJjCallBack;
        }
    });
}());