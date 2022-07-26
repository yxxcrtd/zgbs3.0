/**
 * 行情（左上）
 */
(function(){

    var finprodAbbr = '';
    var secId = '';

    var viewPositionInfo = function(result){
        // 组合T日可用现金
        if (result.proAvailableCash) {
            document.getElementById('zhkyxj_t').innerHTML = JoyinUtil.formatNumber(result.proAvailableCash, 2, null, 2) + '元';
        }else{
            document.getElementById('zhkyxj_t').innerHTML = '0.00元';
        }
        // 组合T+1日可用现金
        if(result.proAvailableCashT1) {
            document.getElementById('zhkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.proAvailableCashT1, 2, null, 2) + '元';
        }else{
            document.getElementById('zhkyxj_t1').innerHTML = '0.00元';
        }
        // 托管户T日可用现金
        if(result.secManageAvailablePosition) {
            document.getElementById('tghkyxj_t').innerHTML = JoyinUtil.formatNumber(result.secManageAvailablePosition, 2, null, 2) + '元';
        }else{
            document.getElementById('tghkyxj_t').innerHTML = '0.00元';
        }
        // 托管户T+1日可用现金
        if(result.secManageAvailablePositionT1) {
            document.getElementById('tghkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.secManageAvailablePositionT1, 2, null, 2) + '元';
        }else{
            document.getElementById('tghkyxj_t1').innerHTML = '0.00元';
        }
        // 资产持仓券面总额
        if(result.assetPositionEntrustAmt) {
            
            document.getElementById('zccc_qmze').innerHTML = JoyinUtil.formatNumber(result.assetPositionEntrustAmt, 2, null, 2) + '元';
        }else{
            document.getElementById('zccc_qmze').innerHTML = '0.00万元';
        }
        // 资产可用券面总额
        if(result.assetAvailableEntrustAmt) {
            document.getElementById('zcky_qmze').innerHTML = JoyinUtil.formatNumber(result.assetAvailableEntrustAmt, 2, null, 2) + '元';
        }else{
            document.getElementById('zcky_qmze').innerHTML = '0.00万元';
        }
    }
    var queryPositionInfo = function(finprodId,portfolioId,chlFinprodId,ccy,vdate,calId) {
        var rs;
        var param = {
            finprodId : finprodId,
            portfolioId : portfolioId,
            chlFinprodId : chlFinprodId,
            ccy:ccy,
            vdate:vdate,
            calId:calId
        };
        document.getElementById('zhkyxj_t').innerHTML = '0.00元';
        document.getElementById('zhkyxj_t1').innerHTML = '0.00元';
        document.getElementById('tghkyxj_t').innerHTML = '0.00元';
        document.getElementById('tghkyxj_t1').innerHTML = '0.00元';
        // document.getElementById('tdkyxj_t').innerHTML = '0.00元';
        // document.getElementById('tdkyxj_t1').innerHTML = '0.00元';
        document.getElementById('zccc_qmze').innerHTML = '0.00万元';
        document.getElementById('zcky_qmze').innerHTML = '0.00万元';
        if(!portfolioId){
            return;
        }
        Joyin.ajaxJson(basePath + 'invest/entrust/etribbondinfo/EtrIbBondinfo001/findPositionInfo', param, function (result) {
            if (result) {
                rs = result;
                if (result.zhkyxj_t != null && result.zhkyxj_t != '') {
                    document.getElementById('zhkyxj_t').innerHTML = JoyinUtil.formatNumber(result.zhkyxj_t, 2, null, 2) + '元';
                    parent.EtrIbBondinfoHq.zhkyxj_t = result.zhkyxj_t;
                }
                if (result.zhkyxj_t1 != null && result.zhkyxj_t1 != '') {
                    document.getElementById('zhkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.zhkyxj_t1, 2, null, 2) + '元';
                }
                if (chlFinprodId != null && chlFinprodId != '') {
                    document.getElementById("tghkyxj_t_dev").style.display="none";
                    document.getElementById("tghkyxj_t1_dev").style.display="none";
                    // document.getElementById("tdkyxj_t_dev").style.display="";
                    // document.getElementById("tdkyxj_t1_dev").style.display="";
                    // if (result.tdkyxj_t != null && result.tdkyxj_t != '') {
                    //     document.getElementById('tdkyxj_t').innerHTML = JoyinUtil.formatNumber(result.tdkyxj_t, 2, null, 2) + '元';
                    // }
                    // if (result.tdkyxj_t1 != null && result.tdkyxj_t1 != '') {
                    //     document.getElementById('tdkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.tdkyxj_t1, 2, null, 2) + '元';
                    // }
                } else {
                    // document.getElementById("tdkyxj_t_dev").style.display="none";
                    // document.getElementById("tdkyxj_t1_dev").style.display="none";
                    document.getElementById("tghkyxj_t_dev").style.display="";
                    document.getElementById("tghkyxj_t1_dev").style.display="";
                    if (result.tghkyxj_t != null && result.tghkyxj_t != '') {
                        document.getElementById('tghkyxj_t').innerHTML = JoyinUtil.formatNumber(result.tghkyxj_t, 2, null, 2) + '元';
                    }
                    if (result.tghkyxj_t1 != null && result.tghkyxj_t1 != '') {
                        document.getElementById('tghkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.tghkyxj_t1, 2, null, 2) + '元';
                    }
                }
                if (result.zccc_qmze != null && result.zccc_qmze != '') {
                    document.getElementById('zccc_qmze').innerHTML = JoyinUtil.formatNumber(result.zccc_qmze, 2, null, 2) + '万元';
                }
                if (result.zcky_qmze != null && result.zcky_qmze != '') {
                    document.getElementById('zcky_qmze').innerHTML = JoyinUtil.formatNumber(result.zcky_qmze, 2, null, 2) + '万元';
                }
            }
        }, false);
        
        return rs;
    }
    
    /**根据市场代码，查询行情信息，展示出来*/
    var querySecinfo = function(finprodId,portfolioId,chlFinprodId,ccy,sysDateT1,calId) {
        var rs;
        var param = {
            finprodId : finprodId,
            portfolioId : portfolioId,
            // chlFinprodId : chlFinprodId,
            ccy:ccy, 
    		sysDateT1:sysDateT1,
    		calId:calId
        };
        if (portfolioId != null && portfolioId != '') {
            document.getElementById('zhkyxj_t').innerHTML = '0.00元';
            document.getElementById('zhkyxj_t1').innerHTML = '0.00元';
            document.getElementById('tghkyxj_t').innerHTML = '0.00元';
            document.getElementById('tghkyxj_t1').innerHTML = '0.00元';
            // document.getElementById('tdkyxj_t').innerHTML = '0.00元';
            // document.getElementById('tdkyxj_t1').innerHTML = '0.00元';
            document.getElementById('zccc_qmze').innerHTML = '0.00万元';
            document.getElementById('zcky_qmze').innerHTML = '0.00万元';
            Joyin.ajaxJson(basePath + 'invest/entrust/etribbondinfo/EtrIbBondinfo001/findPositionInfo', param, function (result) {
                if (result) {
                    rs = result;
                    if (result.zhkyxj_t != null && result.zhkyxj_t != '') {
                        document.getElementById('zhkyxj_t').innerHTML = JoyinUtil.formatNumber(result.zhkyxj_t, 2, null, 2) + '元';
                        parent.EtrIbBondinfoHq.zhkyxj_t = result.zhkyxj_t;
                    }
                    if (result.zhkyxj_t1 != null && result.zhkyxj_t1 != '') {
                        document.getElementById('zhkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.zhkyxj_t1, 2, null, 2) + '元';
                    }
                    if (chlFinprodId != null && chlFinprodId != '') {
                        document.getElementById("tghkyxj_t_dev").style.display="none";
                        document.getElementById("tghkyxj_t1_dev").style.display="none";
                        // document.getElementById("tdkyxj_t_dev").style.display="";
                        // document.getElementById("tdkyxj_t1_dev").style.display="";
                        // if (result.tdkyxj_t != null && result.tdkyxj_t != '') {
                        //     document.getElementById('tdkyxj_t').innerHTML = JoyinUtil.formatNumber(result.tdkyxj_t, 2, null, 2) + '元';
                        // }
                        // if (result.tdkyxj_t1 != null && result.tdkyxj_t1 != '') {
                        //     document.getElementById('tdkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.tdkyxj_t1, 2, null, 2) + '元';
                        // }
                    } else {
                        // document.getElementById("tdkyxj_t_dev").style.display="none";
                        // document.getElementById("tdkyxj_t1_dev").style.display="none";
                        document.getElementById("tghkyxj_t_dev").style.display="";
                        document.getElementById("tghkyxj_t1_dev").style.display="";
                        if (result.tghkyxj_t != null && result.tghkyxj_t != '') {
                            document.getElementById('tghkyxj_t').innerHTML = JoyinUtil.formatNumber(result.tghkyxj_t, 2, null, 2) + '元';
                        }
                        if (result.tghkyxj_t1 != null && result.tghkyxj_t1 != '') {
                            document.getElementById('tghkyxj_t1').innerHTML = JoyinUtil.formatNumber(result.tghkyxj_t1, 2, null, 2) + '元';
                        }
                    }
                    if (result.zccc_qmze != null && result.zccc_qmze != '') {
                        document.getElementById('zccc_qmze').innerHTML = JoyinUtil.formatNumber(result.zccc_qmze, 2, null, 2) + '万元';
                    }
                    if (result.zcky_qmze != null && result.zcky_qmze != '') {
                        document.getElementById('zcky_qmze').innerHTML = JoyinUtil.formatNumber(result.zcky_qmze, 2, null, 2) + '万元';
                    }
                }
            }, false);
        }
        if (finprodId != null && finprodId != '') {
            
            // document.getElementById('secName').innerHTML = '';
            // document.getElementById('secType').innerHTML = '';
            // document.getElementById('issuer').innerHTML = '';
            // document.getElementById('vdate').innerHTML = '';
            // document.getElementById('mdate').innerHTML = '';
            // document.getElementById('lastqx').innerHTML = '';
            // document.getElementById('lastPaydate').innerHTML = '';
            // document.getElementById('rateType').innerHTML = '';
            // document.getElementById('rate').innerHTML = '';
            // document.getElementById('xqFlg').innerHTML = '';
            // document.getElementById('xqdate').innerHTML = '';
            // document.getElementById('zxGrade').innerHTML = '';
            // document.getElementById('ztGrade').innerHTML = '';
            // document.getElementById('gzRate').innerHTML = '';
            // document.getElementById('priceXq').innerHTML = '';
            // document.getElementById('priceTj').innerHTML = '';
            Joyin.ajaxJson(basePath + 'invest/entrust/etribbondinfo/EtrIbBondinfo001/findSecinfo', param, function (result) {
                if (result) {
                    secId = result.secId;
                    finprodAbbr = result.finprodAbbr;
                    $("#secName").html('');
                    $("#secName").html(result.secName);
                    result.secType=result.secTypeName;
                    result.rateType=result.rateTypeName;
                    result.issuer=result.issuerName;
                    result.xqFlg=result.xqFlgName;
                    result.sponsor=result.sponsorName;
                    dynview.resultToView(result);
                    //
                    // if (result.secName != null && result.secName != '') {
                    //     document.getElementById('secName').innerHTML = result.secName;
                    // }
                    // if (result.secTypeName != null && result.secTypeName != '') {
                    //     document.getElementById('secType').innerHTML = result.secTypeName;
                    // }
                    // if (result.issuerName != null && result.issuerName != '') {
                    //     document.getElementById('issuer').innerHTML = result.issuerName;
                    // }
                    // if (result.vdate != null && result.vdate != '') {
                    //     document.getElementById('vdate').innerHTML = result.vdate;
                    // }
                    // if (result.mdate != null && result.mdate != '') {
                    //     document.getElementById('mdate').innerHTML = result.mdate;
                    // }
                    // if (result.lastqx != null && result.lastqx != '') {
                    //     if (result.lastqx != 0) {
                    //         document.getElementById('lastqx').innerHTML = result.lastqx + '年';
                    //     } else {
                    //         document.getElementById('lastqx').innerHTML = '--';
                    //     }
                    // }
                    // if (result.lastPaydate != null && result.lastPaydate != '') {
                    //     document.getElementById('lastPaydate').innerHTML = result.lastPaydate;
                    // }
                    // if (result.rateTypeName != null && result.rateTypeName != '') {
                    //     document.getElementById('rateType').innerHTML = result.rateTypeName;
                    // }
                    // if (result.rate != null && result.rate != '') {
                    //     document.getElementById('rate').innerHTML = JoyinUtil.formatNumber(result.rate, 4, null, 2) + '%';
                    // }
                    // if (result.xqFlg != null && result.xqFlg != '') {
                    //     document.getElementById('xqFlg').innerHTML = result.xqFlg;
                    // }
                    // if (result.xqdate != null && result.xqdate != '') {
                    //     document.getElementById('xqdate').innerHTML = result.xqdate;
                    // }
                    // if (result.zxGrade != null && result.zxGrade != '') {
                    //     document.getElementById('zxGrade').innerHTML = result.zxGrade;
                    // }
                    // if (result.ztGrade != null && result.ztGrade != '') {
                    //     document.getElementById('ztGrade').innerHTML = result.ztGrade;
                    // }
                    // if (result.gzRate != null && result.gzRate != '') {
                    //     document.getElementById('gzRate').innerHTML = JoyinUtil.formatNumber(result.gzRate, 4, null, 2) + '%';
                    // }
                    // if (result.priceXq != null && result.priceXq != '') {
                    //     document.getElementById('priceXq').innerHTML = JoyinUtil.formatNumber(result.priceXq, 4, null, 2) ;
                    // }
                    // if (result.priceTj != null && result.priceTj != '') {
                    //     document.getElementById('priceTj').innerHTML = JoyinUtil.formatNumber(result.priceTj, 4, null, 2) ;
                    // }
                }
            }, false);
            // add by caiqun 20211220 调用接口平台外汇cmds行情 start
			//queryJJHQXX(secId);
            var secIdStr = secId.split(".");
            sendBond(websocket,secIdStr[0]);
            // add by caiqun 20211220 调用接口平台外汇cmds行情 end
            if(window.EtrInitAmount)
            window.EtrInitAmount.queryAmountInfo(secId,finprodAbbr,finprodId,portfolioId,chlFinprodId,ccy,sysDateT1,calId);
            return rs;
        }
    }

    var queryAmountInfoMuti = function(finprodId,portfolioIds,chlFinprodId,ccy,sysDateT1,calId) {
        if(window.EtrInitAmount)
        window.EtrInitAmount.queryAmountInfoMuti(secId,finprodAbbr,finprodId,portfolioIds,chlFinprodId,ccy,sysDateT1,calId);
    }

    var calCanBuyAmount = function(portfolioId,ccy,sysDate,price) {
        if(window.EtrInitAmount)
        window.EtrInitAmount.calCanBuyAmount(portfolioId,ccy,sysDate,price);
    }

    var changeElement = function(changeFlg) {
        if(window.EtrInitAmount)
        window.EtrInitAmount.changeElement(changeFlg);
    }


    var websocket = cmds(cmdsWebSocketUrl,
        function (data) {
            if(data != null && data != "" && "没有数据" != data){
                var objs = eval('('+data+')');
                var noMDEntries = objs.noMDTypes.noRelatedSym.noMDEntries;
                debugger
                for(var j = 0;j<noMDEntries.length;j++) {
                    if (noMDEntries[j].mDEntryType === '1') { // 最新收益率
                        document.getElementById('EtrIbBondinfoHq_newRate').innerHTML = JoyinUtil.formatNumber(noMDEntries[j].mDEntryPx, 4, null, 2) + '%';
                    } else if (noMDEntries[j].mDEntryType === 'N') { // 收益率的涨跌
                        document.getElementById('EtrIbBondinfoHq_latestYield').innerHTML =  JoyinUtil.formatNumber(noMDEntries[j].mDEntryPx, 2, null, 2)+ 'BP';
                    } else if (noMDEntries[j].mDEntryType === 'e') { // 最新净价
                        document.getElementById('EtrIbBondinfoHq_newPrice').innerHTML =  JoyinUtil.formatNumber(noMDEntries[j].mDEntryPx, 4, null, 2)+'元';
                    }
                }
            }else{
                document.getElementById('EtrIbBondinfoHq_newRate').innerHTML = ''; // 最新收益率
                document.getElementById('EtrIbBondinfoHq_latestYield').innerHTML ='';// 收益率的涨跌
                document.getElementById('EtrIbBondinfoHq_newPrice').innerHTML ='';// 最新净价
            }
        }
        ,
        function (event) {
            console.log("建立连接:" + event);
        },
        function (event) {
            console.log("连接发生错误:" + event);
        }
    );

    /**
     * 对外公开的接口
     */
    parent.EtrIbBondinfoHq = {
        querySecinfo:querySecinfo,
        queryPositionInfo:queryPositionInfo,
        viewPositionInfo:viewPositionInfo,
        calCanBuyAmount:calCanBuyAmount,
        queryAmountInfoMuti:queryAmountInfoMuti,
        changeElement:changeElement,
        zhkyxj_t: zhkyxj_t
    }
    
    /**
     * 画面加载完成
     */
    var dynview; 
    $(function() {
        dynview = new DynamicView("EtrIbBondinfoHq", "EtrIbBondinfoHq_dyn");
        dynview.init();
    });
    
}());