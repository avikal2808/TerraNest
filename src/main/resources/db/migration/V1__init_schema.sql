-- Flyway Migration script for initializing schema without PostGIS

-- Create crime_data table
CREATE TABLE IF NOT EXISTS crime_data (
    id BIGSERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    crime_type VARCHAR(255) NOT NULL,
    severity INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

-- Create environmental_data table
CREATE TABLE IF NOT EXISTS environmental_data (
    id BIGSERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    air_quality_index DOUBLE PRECISION NOT NULL,
    water_reliability_score DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

-- Create infrastructure_projects table
CREATE TABLE IF NOT EXISTS infrastructure_projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    expected_completion_date DATE,
    impact_score DOUBLE PRECISION NOT NULL
);

-- Create properties table
CREATE TABLE IF NOT EXISTS properties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price NUMERIC(15, 2) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    area_sqft DOUBLE PRECISION NOT NULL
);
