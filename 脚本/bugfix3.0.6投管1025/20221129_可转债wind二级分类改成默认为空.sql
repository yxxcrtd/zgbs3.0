update fin_product_type_config set OUTPUT_VALUE_1 = 'B14',OUTPUT_VALUE_2=null where  config_type = 'WINDTWO_TG_TYPE' AND INPUT_VALUE_1='F01' AND INPUT_VALUE_2='02' AND INPUT_VALUE_3='70';
update fin_product_type_config set OUTPUT_VALUE_1 = 'B14',OUTPUT_VALUE_2=null where  config_type = 'TG_TYPE' AND INPUT_VALUE_1='F01' AND INPUT_VALUE_2='02' AND INPUT_VALUE_3='70';

-- 处理历史数据
update FIN_PRODUCT_TYPE set type_5 =null,type_7=null WHERE TYPE_4 IN ('B14','B16');