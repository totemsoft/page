-- DML (data) script
insert into page (page_name) values
  ('My First Page')
  ;

insert into page_tab (tab_name, page_id) values
  ('Tab 1', 1),
  ('Tab 2', 1)
  ;

insert into split_ratio (split_ratio_name, split_ratio_title) values
  ('ONE', ''),
  ('HALF', '1/2 - 1/2'),
  ('THIRD', '1/3 - 1/3 - 1/3'),
  ('ONE_THIRD', '1/3 - 2/3'),
  ('TWO_THIRD', '2/3 - 1/3'),
  ('ONE_QUATER', '1/4 - 3/4'),
  ('THREE_QUATER', '3/4 - 1/4')
  ;

insert into page_section (section_name, section_index, section_split_ratio, tab_id) values
  ('Section 1',  0, 'ONE',       1),
  ('Section 2',  1, 'HALF',      1),
  ('Section 10', 0, 'ONE_THIRD', 2),
  ('Section 20', 1, 'THIRD',     2)
  ;

insert into page_sub_section (sub_section_name, section_id) values
  ('SubSection 1', 1),
  ('SubSection 2', 2),
  ('SubSection 3', 2),
  ('SubSection 10', 3),
  ('SubSection 20', 3),
  ('SubSection 30', 4),
  ('SubSection 40', 4),
  ('SubSection 50', 4)
  ;

insert into series_data (series_date, series_value, series_title) values
  (CURRENT_DATE, RAND() * (1000 - 100) + 100, 'aaa'),
  (CURRENT_DATE, '1234.5678', 'Read Me Twice')
  ;
