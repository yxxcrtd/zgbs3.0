-- 删除中标状态，转二级交易
delete from SYS_DATADICT_LIST where DICT_CODE = 'FIRST_BID_STATUS' AND ITEM_CODE = '05';