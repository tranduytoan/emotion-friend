-- V11: Add image_name for scenario lessons.
-- Admin uploads scenario image files to res/img/scenario_lessons/<id>.png,
-- and this column stores the file name for app rendering.

ALTER TABLE scenario_lessons
    ADD COLUMN image_name VARCHAR(255) NULL AFTER situation;
