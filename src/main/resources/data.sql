-- DML (data) script
insert into series_data
  (series_date, series_value, series_title) values
  (CURRENT_DATE, RAND() * (1000 - 100) + 100, 'aaa'),
  (CURRENT_DATE, '1234.5678', 'Read Me Twice');