-- Remove role_in_project column from project_coordinators
ALTER TABLE project_coordinators DROP COLUMN IF EXISTS role_in_project;
