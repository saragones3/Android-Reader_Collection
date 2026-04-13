# Reader Collection - Util Agent Guide

Common utility functions and data handling for the Reader Collection app.

## Key Responsibilities
- **Date Handling**: Using Kotlinx Datetime for common date operations and formatting.
- **Common Helpers**: Shared logic, constants (Preferences, BookState), and validation regex (email, username).
- **KMP Support**: Ensuring utility functions work correctly across Android, iOS, and Web.

## Guidelines
- Avoid heavy logic in this module; keep it focused on general-purpose utilities.
- For date-related work, prioritize `kotlinx-datetime`.
- Add global constants and Enums here to ensure consistency across the project.
