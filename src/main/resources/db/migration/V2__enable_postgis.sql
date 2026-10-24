-- Flyway Migration script to enable PostGIS and migrate to geometry types

CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. properties
ALTER TABLE properties ADD COLUMN location geometry(Point, 4326);
UPDATE properties SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326);
ALTER TABLE properties ALTER COLUMN location SET NOT NULL;
ALTER TABLE properties DROP COLUMN latitude;
ALTER TABLE properties DROP COLUMN longitude;
CREATE INDEX idx_properties_location ON properties USING gist(location);

-- 2. crime_data
ALTER TABLE crime_data ADD COLUMN location geometry(Point, 4326);
UPDATE crime_data SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326);
ALTER TABLE crime_data ALTER COLUMN location SET NOT NULL;
ALTER TABLE crime_data DROP COLUMN latitude;
ALTER TABLE crime_data DROP COLUMN longitude;
CREATE INDEX idx_crime_data_location ON crime_data USING gist(location);

-- 3. environmental_data
ALTER TABLE environmental_data ADD COLUMN location geometry(Point, 4326);
UPDATE environmental_data SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326);
ALTER TABLE environmental_data ALTER COLUMN location SET NOT NULL;
ALTER TABLE environmental_data DROP COLUMN latitude;
ALTER TABLE environmental_data DROP COLUMN longitude;
CREATE INDEX idx_environmental_data_location ON environmental_data USING gist(location);

-- 4. infrastructure_projects
ALTER TABLE infrastructure_projects ADD COLUMN location geometry(Point, 4326);
UPDATE infrastructure_projects SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326);
ALTER TABLE infrastructure_projects ALTER COLUMN location SET NOT NULL;
ALTER TABLE infrastructure_projects DROP COLUMN latitude;
ALTER TABLE infrastructure_projects DROP COLUMN longitude;
CREATE INDEX idx_infrastructure_projects_location ON infrastructure_projects USING gist(location);
