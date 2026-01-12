# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Angular](https://github.com/angular/angular/blob/main/CHANGELOG.md).

## [Unreleased]
### Added
- Added refresh token functionality.
### Changed
- For changes in existing functionality.
### Deprecated
- For soon-to-be removed features.
### Removed
- For now removed features.
### Fixed 
- For any bug fixes.
### Security 
- In case of vulnerabilities.

## [1.2.0] - 2026-01-07
### Added
- Expenses can now be deleted based on their expense category's ID.
- Expenses can now be deleted based on their currency's ID.
- Users can now delete their account all of their personal data.
- Expense category can now have its own associated color.
- Users can now updated their password.
- Integrated PostgreSQL as the new database.
### Changed
- Changed the JWT token subject to contain user's ID too.
- Deleting a non-existing entity now returns HTTP 404, instead of a silent HTTP 204.
- Updated the README.md
### Removed
- Removed H2 as the production in-memory database.
### Fixed
- Fixed incorrect string equality checks.

## [1.1.1] - 2026-01-02
### Fixed
- Fixed the mistmatches, caused by branching conflicts related to the version 1.1.0.
  
## [1.1.0] - 2026-01-02
### Added
- Added authentication and authorization, based on Spring Security.
- Added validation inside services.
- Added User entity and according relations to Currency, ExpenseCategory and Expense.
### Changed
- Updated tests and documentation.
