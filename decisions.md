# TerraNest Design Decisions: Advanced Geospatial Features

This document outlines the architectural and logic decisions made while expanding the geospatial capabilities of the TerraNest backend.

## 1. Proximity Intelligence (Nearest Neighbor)
**Logic**: Transitioning from simple radius-based "existence" checks to precise distance calculations using PostGIS `<->` operator and `ST_Distance`.
**Decision**: Implement `findNearest` for Infrastructure and Crime data.
**Impact**: 
- Provides users with exact distances to the nearest hospital, park, or police station.
- Allows the Livability ML model to weight features by distance (decay function) rather than binary inclusion.

## 2. Neighborhood Snapshot (Spatial Enrichment)
**Logic**: A single aggregate endpoint that performs a "Spatial Join" across four tables (Properties, Crime, Environment, Infrastructure).
**Decision**: Create a `NeighborhoodService` to orchestrate multi-repository spatial lookups.
**Impact**:
- Reduces frontend complexity by providing a unified "Intelligence Score" for any coordinate.
- Enables "Compare Neighborhoods" features.

## 3. Spatial Aggregation (Heatmap/Grid Intelligence)
**Logic**: Using `ST_SnapToGrid` to group data points into geographic cells.
**Decision**: Implement an endpoint that returns average scores per grid cell for a given bounding box.
**Impact**:
- Enables high-level map visualizations (heatmaps) without fetching thousands of individual points.
- Identifies "Investment Hotspots" where ROI is high and Crime is low.

## 4. GeoJSON Standardization
**Logic**: Wrapping spatial responses in the RFC 7946 GeoJSON format.
**Decision**: Add a `toGeoJson()` helper to Property and Intelligence DTOs.
**Impact**:
- Direct compatibility with Leaflet, Mapbox, and Google Maps API.
- Standardizes spatial data exchange between the backend and external consumers.

## 5. Persistence Strategy: Geography vs Geometry
**Logic**: The system stores data as `geometry(Point, 4326)` for indexing efficiency but casts to `geography` for distance calculations.
**Decision**: Maintain `geometry` for storage (GIST index speed) but strictly use `::geography` in SQL functions for meter-accurate results on the WGS84 spheroid.
**Impact**: Prevents distance distortion at different latitudes (e.g., comparing distances in NYC vs. Mumbai).
