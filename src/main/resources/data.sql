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

insert into page (page_name) values
  ('My First Page')
  ;

insert into page_tab (tab_name, page_id) values
  ('Tab 1', 1),
  ('Tab 2', 1)
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

insert into tag_type (tag_type_name, tag_type_title) values
  ('TAG_TYPE_01', 'Tag Type 01'),
  ('TAG_TYPE_02', 'Tag Type 02'),
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
  ('TAG_TYPE_01', 'Tag Type 01', 1),
  ('TAG_TYPE_02', 'Tag Type 02', 2),
  ('TAG_TYPE_03', 'Tag Type 03', 3),
  ('TAG_TYPE_04', 'Tag Type 04', 4),
  ('TAG_TYPE_05', 'Tag Type 05', 5),
  ('TAG_TYPE_06', 'Tag Type 06', 6),
  ('TAG_TYPE_07', 'Tag Type 07', 7),
  ('TAG_TYPE_08', 'Tag Type 08', 8),
  ('TAG_TYPE_09', 'Tag Type 09', 9),
  ('TAG_TYPE_10', 'Tag Type 10',10)
  ;

insert into series_data_key (series_data_key_name) values
  ('KEY_01'),
  ('KEY_02')
  ;

insert into series_data (series_data_date, series_data_value, series_data_title, series_data_key_id) values
  (CURRENT_DATE, RAND() * (10000 - 1000) + 1000, 'Read Me Once',  1),
  (CURRENT_DATE, RAND() * (20000 - 2000) + 2000, 'Read Me Twice', 2)
  ;

insert into key_tag (key_id, tag_id) values
  (1, 1),
  (1, 2),
  (2, 1),
  (2, 2)
  ;
