-- DML (data) script
SET AUTOCOMMIT OFF;
BEGIN TRANSACTION;

INSERT INTO oidc_authority (user_authority) SELECT * FROM (VALUES
  ('ADMIN_PAGE'),
  ('ADMIN_USER'),
  ('SETUP')
) WHERE NOT EXISTS (SELECT 1 FROM oidc_authority);

INSERT INTO currency (currency_code, currency_title, currency_base) SELECT * FROM (VALUES
  ('EUR', 'Euro', 'Y')
) WHERE NOT EXISTS (SELECT 1 FROM currency);
UPDATE currency SET currency_base = 'Y' WHERE currency_code IN (
  'EUR','USD','GBP','DKK','AUD','JPY','CNY','RUB'
);

INSERT INTO split_ratio (split_ratio_name, split_ratio_title) SELECT * FROM (VALUES
  ('ONE', ''),
  ('HALF', '1/2 - 1/2'),
  ('THIRD', '1/3 - 1/3 - 1/3'),
  ('ONE_THIRD', '1/3 - 2/3'),
  ('TWO_THIRD', '2/3 - 1/3'),
  ('ONE_QUATER', '1/4 - 3/4'),
  ('THREE_QUATER', '3/4 - 1/4'),
  ('QUATER', '1/4 - 1/4 - 1/4 - 1/4')
) WHERE NOT EXISTS (SELECT 1 FROM split_ratio);

INSERT INTO tag_type (tag_type_name, tag_type_title) SELECT * FROM (VALUES
  ('TAG_TYPE_01', 'Tag Type 01'), -- row
  ('TAG_TYPE_02', 'Tag Type 02') -- column
) WHERE NOT EXISTS (SELECT 1 FROM tag_type);

INSERT INTO tag (tag_name, tag_title, tag_type_id) SELECT * FROM (VALUES
  ('TAG_01', 'Tag 01', 1), -- row
  ('TAG_02', 'Tag 02', 1), -- row
  ('TAG_03', 'Tag 03', 1), -- row
  ('TAG_04', 'Tag 04', 1), -- row
  ('TAG_05', 'Tag 05', 1), -- row
  ('TAG_06', 'Tag 06', 2), -- column
  ('TAG_07', 'Tag 07', 2), -- column
  ('TAG_08', 'Tag 08', 2), -- column
  ('TAG_09', 'Tag 09', 2), -- column
  ('TAG_10', 'Tag 10', 2)  -- column
) WHERE NOT EXISTS (SELECT 1 FROM tag);

INSERT INTO page (page_name) SELECT * FROM (VALUES
  ('My First Page')
) WHERE NOT EXISTS (SELECT 1 FROM page);

INSERT INTO page_tab (tab_name, tab_index, page_id) SELECT * FROM (VALUES
  ('Tab 1', 1, 1),
  ('Tab 2', 2, 1)
) WHERE NOT EXISTS (SELECT 1 FROM page_tab);

INSERT INTO page_section (section_name, section_index, section_split_ratio, tab_id) SELECT * FROM (VALUES
  ('Section 1', 1, 'ONE',       1),
  ('Section 2', 2, 'HALF',      1),
  ('Section 3', 1, 'ONE_THIRD', 2),
  ('Section 4', 2, 'THIRD',     2)
) WHERE NOT EXISTS (SELECT 1 FROM page_section);

INSERT INTO page_sub_section (sub_section_name, sub_section_index, section_id, row_tag_type_id, column_tag_type_id) SELECT * FROM (VALUES
  ('SubSection 1', 1, 1, NULL, NULL),
  ('SubSection 2', 1, 2, NULL, NULL),
  ('SubSection 3', 2, 2, NULL, NULL),
  ('SubSection 4', 1, 3, NULL, NULL),
  ('SubSection 5', 2, 3, NULL, NULL),
  ('SubSection 6', 1, 4, NULL, NULL),
  ('SubSection 7', 2, 4,    2,    1),
  ('SubSection 8', 3, 4,    1,    2)
) WHERE NOT EXISTS (SELECT 1 FROM page_sub_section);

INSERT INTO series_data_key (key_name, key_title) SELECT * FROM (VALUES
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
) WHERE NOT EXISTS (SELECT 1 FROM series_data_key);
  
INSERT INTO sub_section_key (sub_section_id, key_id) SELECT * FROM (VALUES
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
) WHERE NOT EXISTS (SELECT 1 FROM sub_section_key);

INSERT INTO key_tag (key_id, tag_id) SELECT * FROM (VALUES
  (1, 1),
  (1, 6),
  (2, 1),
  (2, 7),
  (3, 1),
  (3, 8),
  (4, 2),
  (4, 9),
  (5, 2),
  (5, 10),
  (6, 10)
) WHERE NOT EXISTS (SELECT 1 FROM key_tag);

COMMIT;
