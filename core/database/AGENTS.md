# Reader Collection - Database Agent Guide

This module manages the local storage for the Reader Collection app using Room.

## Key Responsations
- **Room Entities**: Defines the schema for local book and user data.
- **DAOs**: Handles database operations (Insert, Update, Delete, Query).
- **Mobile Support**: Implements Room database logic for both Android and iOS. (Note: Web targets do not use this module).

## Tech Stack
- **Library**: AndroidX Room (KMP version)
- **Serialization**: Kotlinx Serialization
- **Concurrency**: Flows and Suspend functions for asynchronous database access.
- **Platforms**: Android, iOS.

## Maintenance
- **Schema**: Update the schema in `src/commonMain` and ensure schemas are exported to the `schemas/` directory.
- **Migration**: Always provide migration strategies for schema changes.
