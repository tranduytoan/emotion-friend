-- V10: Backfill image_folder for existing stories.
-- Convention used by Android app and static assets: stories/story-<id>/1.png..4.png

UPDATE stories
SET image_folder = CONCAT('story-', id)
WHERE image_folder IS NULL OR TRIM(image_folder) = '';
