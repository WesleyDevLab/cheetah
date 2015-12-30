#创建tick数据表
create 'stocks_tick_data',{NAME=>'d',COMPRESSION => 'GZ',VERSIONS => 1}
#创建日线数据表
create 'stocks_data_daily',{NAME=>'d',COMPRESSION => 'GZ',VERSIONS => 1}