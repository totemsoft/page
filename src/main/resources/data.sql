-- DML (data) script
insert into split_ratio (split_ratio_name, split_ratio_title) values
  ('ONE', ''),
  ('HALF', '1/2 - 1/2'),
  ('THIRD', '1/3 - 1/3 - 1/3'),
  ('ONE_THIRD', '1/3 - 2/3'),
  ('TWO_THIRD', '2/3 - 1/3'),
  ('ONE_QUATER', '1/4 - 3/4'),
  ('THREE_QUATER', '3/4 - 1/4')
  ;

insert into tag_type (tag_type_name, tag_type_title) values
  ('TAG_TYPE_01', 'Tag Type 01'), -- row
  ('TAG_TYPE_02', 'Tag Type 02'), -- column
  ('TAG_TYPE_03', 'Tag Type 03'),
  ('TAG_TYPE_04', 'Tag Type 04'),
  ('TAG_TYPE_05', 'Tag Type 05'),
  ('TAG_TYPE_06', 'Tag Type 06'),
  ('TAG_TYPE_07', 'Tag Type 07'),
  ('TAG_TYPE_08', 'Tag Type 08'),
  ('TAG_TYPE_09', 'Tag Type 09'),
  ('TAG_TYPE_10', 'Tag Type 10')
  ;

insert into tag (tag_name, tag_title, tag_type_id) values
  ('TAG_01', 'Tag 01', 1), -- row
  ('TAG_02', 'Tag 02', 1), -- row
  ('TAG_03', 'Tag 03', 1), -- row
  ('TAG_04', 'Tag 04', 1), -- row
  ('TAG_05', 'Tag 05', 1), -- row
  ('TAG_06', 'Tag 06', 2), -- column
  ('TAG_07', 'Tag 07', 2), -- column
  ('TAG_08', 'Tag 08', 2), -- column
  ('TAG_09', 'Tag 09', 2), -- column
  ('TAG_10', 'Tag 10', 2), -- column
  ('TAG_11', 'Tag 11', 3),
  ('TAG_12', 'Tag 12', 4)
  ;

insert into page (page_name) values
  ('My First Page')
  ;

insert into page_tab (tab_name, page_id) values
  ('Tab 1', 1),
  ('Tab 2', 1)
  ;

insert into page_section (section_name, section_index, section_split_ratio, tab_id) values
  ('Section 1', 1, 'ONE',       1),
  ('Section 2', 2, 'HALF',      1),
  ('Section 3', 1, 'ONE_THIRD', 2),
  ('Section 4', 2, 'THIRD',     2)
  ;

insert into page_sub_section (sub_section_name, section_id, row_tag_type_id, column_tag_type_id) values
  ('SubSection 1', 1, NULL, NULL),
  ('SubSection 2', 2, NULL, NULL),
  ('SubSection 3', 2, NULL, NULL),
  ('SubSection 4', 3, NULL, NULL),
  ('SubSection 5', 3, NULL, NULL),
  ('SubSection 6', 4, NULL, NULL),
  ('SubSection 7', 4, NULL, NULL),
  ('SubSection 8', 4,    1,    2)
  ;

insert into series_data_key (key_name, key_title) values
  ('KEY_01', 'Key 01'),
  ('KEY_02', 'Key 02'),
  ('KEY_03', 'Key 03'),
  ('KEY_04', 'Key 04'),
  ('KEY_05', 'Key 05'),
  ('KEY_06', 'Key 06'),
  ('KEY_07', 'Key 07'),
  ('KEY_08', 'Key 08'),
  ('KEY_09', 'Key 09'),
  ('KEY_10', 'Key 10')
  ;

insert into series_data (series_data_date, series_data_value, series_data_title, key_id) values
  (CURRENT_DATE, RAND() * ( 10000 -  1000) +  1000, 'Read Me  1',  1),
  (CURRENT_DATE, RAND() * ( 20000 -  2000) +  2000, 'Read Me  2',  2),
  (CURRENT_DATE, RAND() * ( 30000 -  3000) +  3000, 'Read Me  3',  3),
  (CURRENT_DATE, RAND() * ( 40000 -  4000) +  4000, 'Read Me  4',  4),
  (CURRENT_DATE, RAND() * ( 50000 -  5000) +  5000, 'Read Me  5',  5),
  (CURRENT_DATE, RAND() * ( 60000 -  6000) +  6000, 'Read Me  6',  6),
  (CURRENT_DATE, RAND() * ( 70000 -  7000) +  7000, 'Read Me  7',  7),
  (CURRENT_DATE, RAND() * ( 80000 -  8000) +  8000, 'Read Me  8',  8),
  (CURRENT_DATE, RAND() * ( 90000 -  9000) +  9000, 'Read Me  9',  9)
--  ,(CURRENT_DATE, RAND() * (100000 - 10000) + 10000, 'Read Me 10', 10)
  ;

insert into sub_section_key (sub_section_id, key_id) values
  -- sub-section #1
  (1, 1),
  -- sub-section #2
  (2, 1),
  (2, 2),
  -- sub-section #3
  (3, 1),
  (3, 2),
  (3, 3),
  -- sub-section #4
  (4, 1),
  (4, 2),
  (4, 3),
  (4, 4),
  -- sub-section #5
  (5, 1),
  (5, 2),
  (5, 3),
  (5, 4),
  (5, 5),
  -- sub-section #6
  (6, 1),
  (6, 2),
  (6, 3),
  (6, 4),
  (6, 5),
  (6, 6),
  -- sub-section #7
  (7, 1),
  (7, 2),
  (7, 3),
  (7, 4),
  (7, 5),
  (7, 6),
  (7, 7),
  (7, 8),
  (7, 9),
  (7, 10),
  -- sub-section #8
  (8, 1),
  (8, 2),
  (8, 3),
  (8, 4),
  (8, 5),
  (8, 6),
  (8, 7),
  (8, 8),
  (8, 9),
  (8, 10)
  ;

insert into key_tag (key_id, tag_id) values
  (1, 1),
  (1, 6),
  (2, 1),
  (2, 7),
  (3, 1),
  (3, 8),
  (4, 2),
  (4, 9),
  (5, 2),
  (5, 10)
  ;
