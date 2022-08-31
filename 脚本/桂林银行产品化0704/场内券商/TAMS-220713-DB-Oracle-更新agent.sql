delete from t_terminal where TERMINAL_NAME in ('kcbpw','ufx');
INSERT INTO t_terminal (TERMINAL_NAME, TERMINAL_TYPE, CREATE_TIME) VALUES('kcbpw', 0, to_date('06-07-2022 16:27:13', 'dd-mm-yyyy hh24:mi:ss'));
INSERT INTO t_terminal (TERMINAL_NAME, TERMINAL_TYPE, CREATE_TIME) VALUES('ufx', 0, to_date('06-07-2022 16:27:13', 'dd-mm-yyyy hh24:mi:ss'));
